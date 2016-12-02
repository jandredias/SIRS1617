package pt.andred.sirs1617.ws;

import java.util.HashMap;
import java.util.Map;

import javax.xml.ws.Endpoint;

import pt.andred.sirs1617.ui.Dialog;

public class NotFenixManager {

	private static NotFenixManager _instance;
	private NotFenixPort _port;
	private Endpoint _endpoint;
	private Map<String, String> _doctors;
	
	private NotFenixManager() {
		_doctors  = new HashMap<>();
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
	
	public boolean login(String username, String password){
		Dialog.IO().println("Login try:" + username + " " + password);
		if(!_doctors.containsKey(username)) return false;
		return _doctors.get(username).equals(password);
	}
	public void start(String address){
		_endpoint.publish(address);
		Dialog.IO().println("NotFenixManager started");
	}
	
	public void stop() {
		_endpoint.stop();
		// TODO Auto-generated method stub
		
	}

	public String setInfoPatient(String name, String infoName, String infoValue) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getInfoPatient(String name, String infoName) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean deletePatient(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean addPatient(String name, String key) {
		// TODO Auto-generated method stub
		return false;
	}

	public String changePassword(String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean revokeDoctorKey(String username, String oldPublicKey, String newPublicKey) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean deleteDoctor(String username) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean addDoctor(String username, String publicKey) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean getPatient(String username) {
		// TODO Auto-generated method stub
		return false;
	}
}
