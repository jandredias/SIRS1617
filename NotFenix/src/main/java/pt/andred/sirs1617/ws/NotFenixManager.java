package pt.andred.sirs1617.ws;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.ws.Endpoint;

import pt.andred.sirs1617.ui.Dialog;

public class NotFenixManager {

	private static NotFenixManager _instance;
	private NotFenixPort _port;
	private Endpoint _endpoint;
	private Map<String, String> _doctors;
//	private Map<String, String, Map<String, String>, String> _patientsPrivate;
//	private Map<String, String, String> __patientsPublic;
	private Map<String, String> _logins;

	private NotFenixManager() {
		_doctors  = new HashMap<>();
		_logins = new HashMap<>();

		_doctors.put("andre.dias", "andre");
		_doctors.put("jorge.veiga", "andre");
		_doctors.put("miguel.amaral", "andre");
		_port = new NotFenixPort();
		_endpoint = Endpoint.create(_port);
		// TODO Auto-generated constructor stub
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

	public boolean setInfoPatient(String token, String name, String infoName, String infoValue) {
		String username = checkToken(token);
		if (username == null)
			return false;



		return false;
	}

	public String getInfoPatient(String token, String name, String infoName) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean deletePatient(String token, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean addPatient(String token, String name, String key, String keyDoctor, String details) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean changePassword(String token, String username, String password, String oldPassword) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean revokeDoctorKey(String token, String username, String oldPublicKey, String newPublicKey) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean deleteDoctor(String token, String username) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean addDoctor(String token, String username, String password, String publicKey) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean getPatient(String token, String username) {
		// TODO Auto-generated method stub
		return false;
	}

	public String setPublicInfoPatient(String token, String name, String infoName, String infoValue) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPublicInfoPatient(String token, String name, String infoName) {
		// TODO Auto-generated method stub
		return null;
	}
}
