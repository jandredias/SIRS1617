package pt.andred.sirs1617.ws;

import javax.xml.ws.Endpoint;

import pt.andred.sirs1617.ui.Dialog;

public class NotFenixManager {

	private static NotFenixManager _instance;
	private NotFenixPort _port;
	private Endpoint _endpoint;
	
	private NotFenixManager() {
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
		return true;
	}

	public String addPatient(String in) {
		// TODO Auto-generated method stub
		return null;
	}

	public String resetPatientPrivateInfo(String in) {
		// TODO Auto-generated method stub
		return null;
	}

	public String resetPatientPublicInfo(String in) {
		// TODO Auto-generated method stub
		return null;
	}

	public String changeDoctorPublicKey(String in) {
		// TODO Auto-generated method stub
		return null;
	}

	public String changePassword(String in) {
		// TODO Auto-generated method stub
		return null;
	}

	public String deleteDoctor(String in) {
		// TODO Auto-generated method stub
		return null;
	}

	public String addDoctor(String in) {
		// TODO Auto-generated method stub
		return null;
	}

	public String sharePatient(String in) {
		// TODO Auto-generated method stub
		return null;
	}

	public String deletePatient(String in) {
		// TODO Auto-generated method stub
		return null;
	}

	public String addPatientPrivateInfo(String in) {
		// TODO Auto-generated method stub
		return null;
	}

	public String addPatientPublicInfo(String in) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPatientPrivateInfo(String in) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPatientPublicInfo(String in) {
		// TODO Auto-generated method stub
		return null;
	}
	public void start(String address){
		_endpoint.publish(address);
		Dialog.IO().println("NotFenixManager started");
	}
	
	public void stop() {
		_endpoint.stop();
		// TODO Auto-generated method stub
		
	}
}
