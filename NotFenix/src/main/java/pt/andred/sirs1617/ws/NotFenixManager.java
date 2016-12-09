package pt.andred.sirs1617.ws;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Set;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Endpoint;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.io.FileInputStream;

import pt.andred.sirs1617.ui.Dialog;

public class NotFenixManager {

	private static NotFenixManager _instance;
	private NotFenixPort _port;
	private Endpoint _endpoint;
	private Map<String, String> _doctors;
	private Map<String, String> _doctorKeys;
	private Map<String, PatientPrivateInfo> _patientsPrivate;
	private Map<String, String> _logins;

	private String P_NAME_TAG = "P_NAME";
	private String P_KEY_MASTER_TAG = "P_KEY_M";
	private String P_KEY_DOCTOR_TAG = "P_KEY_DOCTOR";
	private String P_DETAILS_TAG = "P_DETAILS";
	private String P_PUBLIC_KEY = "P_PUBLIC_KEY";
	private String P_PUBLIC_DETAILS = "P_PUBLIC_DETAILS";
	private String P_PRIVATE_IV = "P_PRIVATE_IV";
	private String P_PUBLIC_IV = "P_PUBLIC_IV";
	private String PUBLIC_KEY_FILE = "_public.key";
	private String NULL_STRING_TAG = "NADA";

	private String HR_MASTER = "HR";

	private static int _keySize;



	private NotFenixManager() {
		_doctors  = new HashMap<>();
		_logins = new HashMap<>();
		_patientsPrivate = new HashMap<>();
		_doctorKeys = new HashMap<>();

		//_doctors.put("andre.dias", "andre");
		//_doctors.put("jorge.veiga", "andre");
		//_doctors.put("miguel.amaral", "andre");
		_doctors.put(HR_MASTER, "M");

		PublicKey HR_public = getPublicKey(HR_MASTER);
		byte[] HR_public_byte = HR_public.getEncoded();
		String HR_public_string = null;
		try {
			//HR_public_string = new String(HR_public_byte);
			HR_public_string = Base64.getEncoder().encodeToString(HR_public_byte);
		}catch (Exception e) {
			e.printStackTrace();
		}
		_doctorKeys.put(HR_MASTER, HR_public_string);


		_port = new NotFenixPort();
		_endpoint = Endpoint.create(_port);
	}

	private PublicKey getPublicKey(String username){
			ObjectInputStream inputStream;
		try{
			inputStream = new ObjectInputStream(new FileInputStream(username + PUBLIC_KEY_FILE));
			PublicKey pk = (PublicKey) inputStream.readObject();
			byte[] pk_byte = pk.getEncoded();
			String pk_encoded_byte = Base64.getEncoder().encodeToString(pk_byte);
			_keySize = pk_encoded_byte.getBytes().length;
			Dialog.IO().println("keySize= "+_keySize);
			return pk;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static NotFenixManager instance(){
		if(_instance==null)
			_instance = new NotFenixManager();
		return _instance;
	}


	public String login(String username, String password){
		Dialog.IO().println("Login try:" + username + " " + password);
		if(_doctors.containsKey(username) && _doctors.get(username).equals(password))
			return generateNewToken(username);
		return null;

	}
	private String generateNewToken(String username) {
		UUID token = UUID.randomUUID();
		String t = token.toString().substring(0, 10);
		Dialog.IO().debug("TOKEN: ", t);
		_logins.put(t, username);
		return t;
	}

	public void start(String address){
		_endpoint.publish(address);
		Dialog.IO().println("NotFenixManager started");
	}

	public void stop() {
		_endpoint.stop();
	}

	public String checkToken(String token){
		String username = _logins.get(token);
		Dialog.IO().println("Check Token name: " + username); //TESTE
		Dialog.IO().println("Check Token toke: " + token); //TESTE
		if(username == null){
			Dialog.IO().println("Invalid Token");
			return null;
		}
		return username;

	}

	
	
	public boolean setInfoPatient(String token, String pname, String infoName, String infoValue) {
		String name = checkToken(token);
		if (name == null)
			return false; //TODO: must return a problem
		PatientPrivateInfo patient = _patientsPrivate.get(pname);
		if (patient == null)
			return false;

		if(infoName.matches(P_NAME_TAG)){
			patient.setName(infoValue);
			_patientsPrivate.remove(name);
			_patientsPrivate.put(name, patient);
			return true;
		}
		/*else if(infoName.matches(P_KEY_MASTER_TAG)){
			patient.setKeyMaster(infoValue);
			return true;
		}*/
		else if (infoName.matches(P_DETAILS_TAG)){
			patient.setDetails(infoValue);
			return true;
		}
		else if (infoName.matches(P_PUBLIC_KEY)){
			patient.setPublicKeyDoctor(name, infoValue);
			return true;
		}
		else if (infoName.matches(P_PUBLIC_DETAILS)){
			patient.setPublicDetails(infoValue);
			return true;
		}
		else
			return false;
	}
	public boolean sharePatient(String token, String pname, String dname, String key_enc) {
		String name = checkToken(token);
		if (name == null)
			return false; //TODO: must retunr a problem

		PatientPrivateInfo patient = _patientsPrivate.get(pname);
		if (patient == null)
			return false;

		if(!patient.checkDoctor(name))
			return false;
		patient.setKeyDoctor(dname, key_enc);
		return true;

	}

	public String getInfoPatient(String token, String pname, String infoName) {
		String name = checkToken(token);
		if (name == null)
			return null; //TODO: must return a problem
		PatientPrivateInfo patient = _patientsPrivate.get(pname);
		if (patient == null)
			return null;
			Dialog.IO().println("getInfoPatient"); //TESTE
			Dialog.IO().println("pname= " + pname); //TESTE
			Dialog.IO().println("infoName= "+infoName); //TESTE
		if(infoName.matches(P_NAME_TAG))
			return patient.getName();
		else if(infoName.matches(P_KEY_MASTER_TAG))
			return patient.getKeyMaster();
		else if(infoName.matches(P_KEY_DOCTOR_TAG)){
			String a = patient.getKeyDoctor(name);
			Dialog.IO().debug("PATIENTKEY with doctor", a);
			return a;
		}
		else if(infoName.matches(P_DETAILS_TAG))
			return patient.getDetails();
		else if(infoName.matches(P_PUBLIC_KEY))
			return patient.getPublicKey(name);
		else if(infoName.matches(P_PUBLIC_DETAILS))
			return patient.getPublicDetails();
		else if(infoName.matches(P_PRIVATE_IV))
			return patient.getIV();
		else if(infoName.matches(P_PUBLIC_IV))
			return patient.getPublicIV();
		else
			return null;
	}

	public boolean deletePatient(String token, String pname) {
		String name = checkToken(token);
		if (!Objects.equals(name, HR_MASTER))
			return false; //TODO: must return a problem

		_patientsPrivate.remove(pname);
		return true;
	}


	public boolean addPatient(String token, String pname, String key_master,
	String keyDoctor, String privateIV, String detailsEnc, String allKeysEnc,
	String publicIV, String publicDetailsEnc){

		String name = checkToken(token);
		if (name == null)
			return false; //TODO: must return a problem
			Dialog.IO().println("addPatient teste 1"); //TESTE

		if(allKeysEnc == null || allKeysEnc.equals("") || allKeysEnc.equals(NULL_STRING_TAG))
			allKeysEnc = null;
		PatientPrivateInfo patient;
		String temp = publicDetailsEnc;
		publicDetailsEnc = null;
		Dialog.IO().debug("HERE ddPatient teste 2 chave encoded= ", keyDoctor); //TESTE
		try{
			patient = new PatientPrivateInfo(pname, key_master,
			name, keyDoctor, privateIV, detailsEnc, _doctorKeys.keySet(),
			allKeysEnc, publicIV, publicDetailsEnc, _keySize);
			Dialog.IO().println("addPatient teste 5"); //TESTE
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		patient.setPublicKeyDoctor(name, temp);
		_patientsPrivate.put(pname, patient);
		return true;
	}

	public boolean changePassword(String token, String username, String password, String oldPassword) {
		String name = checkToken(token);
		if (name == null)
			return false; //TODO: must return a problem
		if(!Objects.equals(name, HR_MASTER))
			if(!Objects.equals(name, username))
				return false;
		String _old = _doctors.get(username);
		if(_old.equals(oldPassword)){
			_doctors.put(username, password);
			return true;
		}
		return false;
	}


	private String getAllKeysDoctor_private_method(String dname){

		Set<String> patients = _patientsPrivate.keySet();
		Iterator<String> itr = patients.iterator();
		String toReturn = "";

		while(itr.hasNext()){
			PatientPrivateInfo patient = _patientsPrivate.get(itr.next());
			if(patient.checkDoctor(dname))
				toReturn += patient.getKeyDoctor(dname);
			toReturn += patient.getPublicKey(dname);
		}
		return toReturn;
	}


	public String revokeDoctorKey(String token){
		String name = checkToken(token);
		if (name == null)
			return null; //TODO: must return a problem
		return getAllKeysDoctor_private_method(name);
	}

	public boolean revokeDoctorKey_phase2(String token, String allKeysEnc){
		String name = checkToken(token);
		if (name == null)
			return false; //TODO: must return a problem

		return putNewPublicKeysEnc_private_method(name, allKeysEnc);
	}

	private boolean putNewPublicKeysEnc_private_method(String name, String allKeysEnc){
		Set<String> patients = _patientsPrivate.keySet();
		Iterator<String> itr = patients.iterator();
		int i = 0;
		byte[] allKeysEnc_byte = allKeysEnc.getBytes();
		int max = allKeysEnc_byte.length;
		while(itr.hasNext()){
			if(i>=max)
				return false;
			PatientPrivateInfo patient = _patientsPrivate.get(itr.next());
			if(patient.checkDoctor(name)){
				byte[] b = Arrays.copyOfRange(allKeysEnc.getBytes(), i, i+_keySize);
				String k = null;
				try {
					k = new String(b);
				} catch (Exception e) {
					//Let's assume this won't happen
					//There's no time to fix this :(
					e.printStackTrace();
				}
				patient.setKeyDoctor(name, k);
				i+=_keySize;
			}
				byte[] ba = Arrays.copyOfRange(allKeysEnc.getBytes(), i, i+_keySize);
				String ka = null;
				try {
					ka = new String(ba);
				} catch (Exception e) {
					//Let's assume this won't happen
					//There's no time to fix this :(
					e.printStackTrace();
				}
				patient.setPublicKeyDoctor(name, ka);
				i+=_keySize;
		}
		return true;
	}

	public boolean deleteDoctor(String token, String doctor) {
		String name = checkToken(token);
		if (name == null)
			return false; //TODO: must return a problem
		Dialog.IO().println("deleteDoctor teste 1"); //TESTE
		if(!Objects.equals(name, HR_MASTER))
			if(!Objects.equals(name, doctor))
				return false;
			Dialog.IO().println("deleteDoctor teste 2 doctor: <" + doctor + ">"); //TESTE
		if(!_doctors.containsKey(doctor))
			return false;
		Dialog.IO().println("deleteDoctor teste 3"); //TESTE
		_doctors.remove(doctor);
		Set<String> keys = _patientsPrivate.keySet();
		Iterator<String> itr = keys.iterator();
		while(itr.hasNext()){
			PatientPrivateInfo patient = _patientsPrivate.get(itr.next());
			patient.removeDoctor(doctor);
			patient.removePublicDoctor(doctor);
		}
			_logins.remove(doctor);
		return true;
	}

	public boolean addDoctor(String token, String username, String password, String publicKey, String allKeysEnc) {
		String name = checkToken(token);
		Dialog.IO().println("addDoctor teste 1"); //TESTE

		Dialog.IO().println("/name: <" + name + "> /HR: <"+ HR_MASTER +">"); //TESTE

		if(!Objects.equals(name, HR_MASTER))
			return false; //TODO: must return a problem
			Dialog.IO().println("addDoctor teste 2"); //TESTE
		if(_doctors.containsKey(username))
			return false;
			Dialog.IO().println("addDoctor teste 3"); //TESTE
		/*if(Objects.equals(allKeysEnc,NULL_STRING_TAG)|| allKeysEnc == null){
			if(!_patientsPrivate.isEmpty()){
				Dialog.IO().println("addDoctor teste 3.1"); //TESTE
				return false;
			}
		}
		else if(putNewPublicKeysEnc_private_method(username, allKeysEnc)){
			Dialog.IO().println("addDoctor teste 3.2"); //TESTE
			return false;
		}*/
		_doctors.put(username, password);
		_doctorKeys.put(username, publicKey);
		Dialog.IO().println("addDoctor teste 4"); //TESTE
		return true;
	}

	public boolean getPatient(String token, String username) {
		String name = checkToken(token);
		if (name == null)
			return false; //TODO: must return a problem

		if(_patientsPrivate.containsKey(username))
			return true;
		return false;
	}

	public String getMasterKey(String token){
		String name = checkToken(token);
		if (name == null)
			return null; //TODO: must return a problem
		Dialog.IO().println("---------getMasterKey teste 1"); //TESTE
		String k= _doctorKeys.get(HR_MASTER);
			Dialog.IO().println("---------getMasterKey teste 2 key= "+k); //TESTE
		return k;
	}
	public String getDoctorKey(String token, String name) {
		String dname = checkToken(token);
		if (name == null)
			return null; //TODO: must return a problem

		return _doctorKeys.get(name);
	}

	public String getAllDoctorsKeys(String token) {
		String name = checkToken(token);
		if (name == null)
			return null; //TODO: must return a problem

		String data= "";
		Set<String> allDoctors = _doctorKeys.keySet();
		Iterator itr = allDoctors.iterator();
		Dialog.IO().println("---------------------gettallDoctorskeys teste 1"); //TESTE
		while(itr.hasNext()){
			String doc = (String) itr.next();
			Dialog.IO().println("---------------------gettallDoctorskeys teste 2 doc= "+ doc); //TESTE
			data += _doctorKeys.get(doc);
			Dialog.IO().println("---------------------gettallDoctorskeys teste 3 data= "+ data); //TESTE
		}
		return data;
	}

	public String getAllPublicKeys(String token){
		String name = checkToken(token);
		if (name == null)
			return null;
		if(!Objects.equals(name, HR_MASTER))
			return null;

		String data= "";
		Set<String> allPatients = _patientsPrivate.keySet();
		Iterator<String> itr = allPatients.iterator();
		while(itr.hasNext()){
			String patient_string = (String) itr.next();
			PatientPrivateInfo patient = _patientsPrivate.get(patient_string);
			data += patient.getPublicKey(name);
		}
		return data;
	}

	public boolean isMyPatient(String token, String pname){
		String name = checkToken(token);
		if (name == null)
			return false; //TODO: must return a problem
		PatientPrivateInfo p = _patientsPrivate.get(pname);
		if(p == null)
			return false;
		return p.checkDoctor(name);
	}

	public String getMyKey(String token) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<DoctorInfo> getDoctorsKeysNewFunction(String token) {
		List<DoctorInfo> list = new ArrayList<>();
		for(Map.Entry<String, String> entry : _doctorKeys.entrySet()){
			DoctorInfo info = new DoctorInfo();
			info.setName(entry.getKey());
			info.setPublicKey(entry.getValue());
			list.add(info);
		}
		return list;
	}
	
	public boolean setInfoPatient2(String token, String pname, String infoName, String infoValue, String dname){
		//TODO
		return true;
	}
}
