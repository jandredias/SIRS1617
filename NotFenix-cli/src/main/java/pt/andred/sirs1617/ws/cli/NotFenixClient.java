package pt.andred.sirs1617.ws.cli;

import java.util.Map;

import javax.xml.ws.BindingProvider;
import pt.andred.sirs1617.ui.Dialog;
import pt.andred.sirs1617.ws.NotFenixPortType;
import pt.andred.sirs1617.ws.NotFenixService;

public class NotFenixClient {
	private NotFenixService _client;
	private NotFenixPortType _port;
	private BindingProvider _bindingProvider;
	private String _token;
	public NotFenixClient(String url){
		_token = null;
		_client = new NotFenixService();
		_port = _client.getNotFenixPort();
		
		_bindingProvider = (BindingProvider) _port;
		
		Map<String, Object> requestContext = _bindingProvider.getRequestContext();
		
		requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
	}
	
    public boolean login(
        String username,
        String password){
    	Dialog.IO().debug("Login request");
    	String token = _port.login(username, password);
    	Dialog.IO().debug("Login response");
    	if(token == null) return false;
    	Dialog.IO().debug("Login successful");
    	return true;
    }

    public boolean addDoctor(
                String username,
                String password,
                String publicKey){
    	return _port.addDoctor(_token, username, password, publicKey);
    }

    public boolean deleteDoctor(
                String username){
    	return _port.deleteDoctor(_token, username);
    }

    public boolean revokeDoctorKey(
                String username,
                String oldPublicKey,
                String newPublicKey){
    	return _port.revokeDoctorKey(_token, username, oldPublicKey, newPublicKey);
    }

    public boolean changePassword(
                String username,
                String password,
                String oldPassword){
    	return _port.changePassword(_token, username, password, oldPassword);
    }

    public boolean addPatient(
                String name,
                String keyMaster,
                String keyDoctor,
                String details){
    	return _port.addPatient(_token, name, keyMaster, keyDoctor, details);
    }

    public boolean deletePatient(
                String name){
    	return _port.deletePatient(_token, name);
    }

    public String getInfoPatient(
                String name,
                String infoName){
    	return _port.getInfoPatient(_token, name, infoName);
    }

    public boolean setInfoPatient(
                String name,
                String infoName,
                String infoValue){
    	return _port.setInfoPatient(_token, name, infoName, infoValue);
    }

    public String getPublicInfoPatient(
                String name,
                String infoName){
    	return _port.getPublicInfoPatient(_token, name, infoName);
    }

    public String setPublicInfoPatient(
                String name,
                String infoName,
                String infoValue){
    	return _port.setPublicInfoPatient(_token, name, infoName, infoValue);
    }

    public boolean getPatient(
        String username){
    	return _port.getPatient(_token, username);
    }
	
	
	
}
