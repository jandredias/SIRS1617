package pt.andred.sirs1617.ws;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Set;
import java.util.Iterator;

import java.security.PublicKey;
import javax.xml.ws.Endpoint;

import java.io.ObjectInputStream;
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
  public static final String PUBLIC_KEY_FILE = "_public.key";

	private String HR_MASTER = "RH";



	private NotFenixManager() {
		_doctors  = new HashMap<>();
		_logins = new HashMap<>();
		_patientsPrivate = new HashMap<>();
		_doctorKeys = new HashMap<>();

	/*	_doctors.put("andre.dias", "andre");
		_doctors.put("jorge.veiga", "andre");
		_doctors.put("miguel.amaral", "andre");*/
		_doctors.put(HR_MASTER, "MASTER");

		PublicKey HR_public = getPublicKey(HR_MASTER);
		byte[] HR_public_byte = HR_public.getEncoded();
		String HR_public_string = new String(HR_public_byte, "UTF-8");
		_doctorKeys.put(HR_MASTER, HR_public_string);


		_port = new NotFenixPort();
		_endpoint = Endpoint.create(_port);
		// TODO Auto-generated constructor stub
	}

	private PublicKey getPublicKey(String username){
			ObjectInputStream inputStream;
		try{
			inputStream = new ObjectInputStream(new FileInputStream(username + PUBLIC_KEY_FILE));
			return(PublicKey) inputStream.readObject();
		} catch (Exception e) {
			return null;
		}
	}

	public static NotFenixManager instance(){
		if(_instance==null)
			_instance = new NotFenixManager();
		return _instance;
	}

	public String login(String username, String password){
		Dialog.IO().debug("Login try:" + username + " " + password);
		if(_doctors.containsKey(username) && _doctors.get(username).equals(password))
			return generateNewToken(username);
		return null;
	}
	private String generateNewToken(String username) {
		UUID token = UUID.randomUUID();
		_logins.put(token.toString(), username);
		return token.toString();
	}

	public void start(String address){
		_endpoint.publish(address);
		Dialog.IO().println("NotFenixManager started");
	}

	public void stop() {
		_endpoint.stop();
		// TODO Auto-generated method stub

	}
	public String checkToken(String token){
		String username = _logins.get(token);
		if(username == null){
			//TODO, return problem
			Dialog.IO().print("Invalid Token");
		}
		return username;

	}

	public boolean setInfoPatient(String token, String pname, String infoName, String infoValue) {
		String name = checkToken(token);
		if (name == null)
			return false; //TODO: must retunr a problem
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
	/*	else if(infoName.matches(P_KEY_DOCTOR_TAG)){
			patient.setKeyDoctor(name, infoValue);
			return true;
		}*/
		else if (infoName.matches(P_DETAILS_TAG)){
			patient.setDetails(infoValue);
			return true;
		}
	/*	else if (infoName.matches(P_PUBLIC_KEY)){
			patient.setPublicKey(name, infoValue);
			return true;
		}*/
		else if (infoName.matches(P_PUBLIC_DETAILS)){
			patient.setPublicDetails(infoValue);
			return true;
		}
		else
			return false;
	}

	public String getInfoPatient(String token, String pname, String infoName) {
		String name = checkToken(token);
		if (name == null)
			return null; //TODO: must retunr a problem
		PatientPrivateInfo patient = _patientsPrivate.get(pname);
		if (patient == null)
			return null;

		if(infoName.matches(P_NAME_TAG))
			return patient.getName();
		else if(infoName.matches(P_KEY_MASTER_TAG))
			return patient.getKeyMaster();
		else if(infoName.matches(P_KEY_DOCTOR_TAG))
			return patient.getKeyDoctor(name);
		else if (infoName.matches(P_DETAILS_TAG))
			return patient.getDetails();
		else if (infoName.matches(P_PUBLIC_KEY))
				return patient.getPublicKey(name);
		else if (infoName.matches(P_PUBLIC_DETAILS))
				return patient.getPublicDetails();
		else
			return null;
	}

	public boolean deletePatient(String token, String pname) {
		String name = checkToken(token);
		if (name != HR_MASTER)
			return false; //TODO: must retunr a problem

		_patientsPrivate.remove(pname);
		return true;
	}

	public boolean addPatient(String token, String pname, String key, String keyDoctor, String details, String detailsEnc, String allKeysEncString, String iv2String, String detailsPublicEnc) {
	//public boolean addPatient(String token, String pname, String key_master, String keyDoctor, String privateIV, String detailsEnc, String allKeysEnc, String publicIV, publicDetailsEnc)
	//FIXME descomenter isto^. falta por um {} no fim
		String name = checkToken(token);
		if (name == null)
			return false; //TODO: must retunr a problem

		//FIXME descomentar isto
		/*PatientPrivateInfo patient = new PatientPrivateInfo(pname, key_master, name, keyDoctor, privateIV, detailsEnc, _doctorKeys.keySet()), allKeysEnc, publicIV, publicDetailsEnc;
		if(_patientsPrivate.put(pname, patient)!= null)
			return true;*/
		return false;
	}

	public boolean changePassword(String token, String username, String password, String oldPassword) {
		String name = checkToken(token);
		if (name == null)
			return false; //TODO: must retunr a problem
		if(name != HR_MASTER)
			if(name != username)
				return false;
		String _old = _doctors.get(username);
		if(_old.equals(oldPassword)){
			_doctors.put(username, password);
			return true;
		}
		return false;
	}
	//
	public boolean revokeDoctorKey(String token, String username, String oldPublicKey, String newPublicKey) {
		String name = checkToken(token);
		if (name == null)
			return false; //TODO: must retunr a problem
		if(name != HR_MASTER)
			if(name != username)
				return false;
		if(_doctorKeys.containsKey(username)){
			_doctorKeys.put(username, newPublicKey);
			return true;
		}
		return false;
	}

	public boolean deleteDoctor(String token, String username) {
		String name = checkToken(token);
		if (name == null)
			return false; //TODO: must retunr a problem
		if(name != HR_MASTER)
			if(name != username)
				return false;
		if(_doctors.containsKey(username))
			return false;
		_doctors.remove(username);
		return true;
	}

	public boolean addDoctor(String token, String username, String password, String publicKey) {
		String name = checkToken(token);
		if(name != HR_MASTER)
			return false; //TODO: must retunr a problem
		if(_doctors.containsKey(username))
			return false;
		_doctors.put(username, password);
		_doctorKeys.put(username, publicKey);
		return true;
	}

	public boolean getPatient(String token, String username) {
		String name = checkToken(token);
		if (name == null)
			return false; //TODO: must retunr a problem

		if(_patientsPrivate.containsKey(username))
			return true;
		return false;
	}

	public String getMasterKey(String token){
		return _doctorKeys.get(HR_MASTER);
	}
	public String getMyKey(String token) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAllDoctorsKeys(String token) {
		String data= "";
		Set<String> allDoctors = _doctorKeys.keySet();
		Iterator itr = allDoctors.iterator();

		while(itr.hasNext()){
			String doc = (String) itr.next();
			data += _doctorKeys.get(doc);
		}
	}



}
