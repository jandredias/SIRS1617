/**
 * 
 */
package pt.andred.sirs1617.ws;

import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * @author André Dias
 *
 */
@WebService(name = "NotFenixPortType", targetNamespace = "http://ws.sirs1617.andred.pt/")
@HandlerChain(file="/handler-chain.xml")
public class NotFenixPort implements NotFenixPortType {

	/**
	 * 
	 */
	public NotFenixPort() {
		// TODO Auto-generated constructor stub
	}

    /**
     * 
     * @param password
     * @param username
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(name = "token", targetNamespace = "")
    @RequestWrapper(localName = "login", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.Login")
    @ResponseWrapper(localName = "loginResponse", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.LoginResponse")
    public String login(
        @WebParam(name = "username", targetNamespace = "")
        String username,
        @WebParam(name = "password", targetNamespace = "")
        String password){
    	return NotFenixManager.instance().login(username, password);
    }

    /**
     * 
     * @param password
     * @param publicKey
     * @param token
     * @param username
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(name = "success", targetNamespace = "")
    @RequestWrapper(localName = "addDoctor", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.AddDoctor")
    @ResponseWrapper(localName = "addDoctorResponse", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.AddDoctorResponse")
    public boolean addDoctor(
        @WebParam(name = "token", targetNamespace = "")
        String token,
        @WebParam(name = "username", targetNamespace = "")
        String username,
        @WebParam(name = "password", targetNamespace = "")
        String password,
        @WebParam(name = "publicKey", targetNamespace = "")
        String publicKey){
    	return NotFenixManager.instance().addDoctor(token, username, password, publicKey);
    }

    /**
     * 
     * @param token
     * @param username
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(name = "success", targetNamespace = "")
    @RequestWrapper(localName = "deleteDoctor", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.DeleteDoctor")
    @ResponseWrapper(localName = "deleteDoctorResponse", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.DeleteDoctorResponse")
    public boolean deleteDoctor(
        @WebParam(name = "token", targetNamespace = "")
        String token,
        @WebParam(name = "username", targetNamespace = "")
        String username){
    	return NotFenixManager.instance().deleteDoctor(token, username);
    }

    /**
     * 
     * @param newPublicKey
     * @param oldPublicKey
     * @param token
     * @param username
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(name = "success", targetNamespace = "")
    @RequestWrapper(localName = "revokeDoctorKey", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.RevokeDoctorKey")
    @ResponseWrapper(localName = "revokeDoctorKeyResponse", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.RevokeDoctorKeyResponse")
    public boolean revokeDoctorKey(
        @WebParam(name = "token", targetNamespace = "")
        String token,
        @WebParam(name = "username", targetNamespace = "")
        String username,
        @WebParam(name = "oldPublicKey", targetNamespace = "")
        String oldPublicKey,
        @WebParam(name = "newPublicKey", targetNamespace = "")
        String newPublicKey){
    	return NotFenixManager.instance().revokeDoctorKey(token, username, oldPublicKey, newPublicKey);
    }

    /**
     * 
     * @param password
     * @param oldPassword
     * @param token
     * @param username
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(name = "success", targetNamespace = "")
    @RequestWrapper(localName = "changePassword", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.ChangePassword")
    @ResponseWrapper(localName = "changePasswordResponse", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.ChangePasswordResponse")
    public boolean changePassword(
        @WebParam(name = "token", targetNamespace = "")
        String token,
        @WebParam(name = "username", targetNamespace = "")
        String username,
        @WebParam(name = "password", targetNamespace = "")
        String password,
        @WebParam(name = "oldPassword", targetNamespace = "")
        String oldPassword){
    	return NotFenixManager.instance().changePassword(token, username, password, oldPassword);
    }

    /**
     * 
     * @param keyMaster
     * @param name
     * @param keyDoctor
     * @param details
     * @param token
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(name = "success", targetNamespace = "")
    @RequestWrapper(localName = "addPatient", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.AddPatient")
    @ResponseWrapper(localName = "addPatientResponse", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.AddPatientResponse")
    public boolean addPatient(
        @WebParam(name = "token", targetNamespace = "")
        String token,
        @WebParam(name = "name", targetNamespace = "")
        String name,
        @WebParam(name = "keyMaster", targetNamespace = "")
        String keyMaster,
        @WebParam(name = "keyDoctor", targetNamespace = "")
        String keyDoctor,
        @WebParam(name = "details", targetNamespace = "")
        String details){
    	return NotFenixManager.instance().addPatient(token, name, keyMaster, keyDoctor, details);
    }

    /**
     * 
     * @param name
     * @param token
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(name = "success", targetNamespace = "")
    @RequestWrapper(localName = "deletePatient", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.DeletePatient")
    @ResponseWrapper(localName = "deletePatientResponse", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.DeletePatientResponse")
    public boolean deletePatient(
        @WebParam(name = "token", targetNamespace = "")
        String token,
        @WebParam(name = "name", targetNamespace = "")
        String name){
    	return NotFenixManager.instance().deletePatient(token, name);
    }

    /**
     * 
     * @param infoName
     * @param name
     * @param token
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(name = "infoValue", targetNamespace = "")
    @RequestWrapper(localName = "getInfoPatient", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.GetInfoPatient")
    @ResponseWrapper(localName = "getInfoPatientResponse", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.GetInfoPatientResponse")
    public String getInfoPatient(
        @WebParam(name = "token", targetNamespace = "")
        String token,
        @WebParam(name = "name", targetNamespace = "")
        String name,
        @WebParam(name = "infoName", targetNamespace = "")
        String infoName){
    	return NotFenixManager.instance().getInfoPatient(token, name, infoName);
    }

    /**
     * 
     * @param infoName
     * @param infoValue
     * @param name
     * @param token
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(name = "success", targetNamespace = "")
    @RequestWrapper(localName = "setInfoPatient", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.SetInfoPatient")
    @ResponseWrapper(localName = "setInfoPatientResponse", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.SetInfoPatientResponse")
    public boolean setInfoPatient(
        @WebParam(name = "token", targetNamespace = "")
        String token,
        @WebParam(name = "name", targetNamespace = "")
        String name,
        @WebParam(name = "infoName", targetNamespace = "")
        String infoName,
        @WebParam(name = "infoValue", targetNamespace = "")
        String infoValue){
    	return NotFenixManager.instance().setInfoPatient(token, name, infoName, infoValue);
    }

    /**
     * 
     * @param infoName
     * @param name
     * @param token
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(name = "infoValue", targetNamespace = "")
    @RequestWrapper(localName = "getPublicInfoPatient", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.GetPublicInfoPatient")
    @ResponseWrapper(localName = "getPublicInfoPatientResponse", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.GetPublicInfoPatientResponse")
    public String getPublicInfoPatient(
        @WebParam(name = "token", targetNamespace = "")
        String token,
        @WebParam(name = "name", targetNamespace = "")
        String name,
        @WebParam(name = "infoName", targetNamespace = "")
        String infoName){
    	return NotFenixManager.instance().getPublicInfoPatient(token, name, infoName);
    }

    /**
     * 
     * @param infoName
     * @param infoValue
     * @param name
     * @param token
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(name = "out", targetNamespace = "")
    @RequestWrapper(localName = "setPublicInfoPatient", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.SetPublicInfoPatient")
    @ResponseWrapper(localName = "setPublicInfoPatientResponse", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.SetPublicInfoPatientResponse")
    public String setPublicInfoPatient(
        @WebParam(name = "token", targetNamespace = "")
        String token,
        @WebParam(name = "name", targetNamespace = "")
        String name,
        @WebParam(name = "infoName", targetNamespace = "")
        String infoName,
        @WebParam(name = "infoValue", targetNamespace = "")
        String infoValue){
    	return NotFenixManager.instance().setPublicInfoPatient(token, name, infoName, infoValue);
    }

    /**
     * 
     * @param token
     * @param username
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(name = "success", targetNamespace = "")
    @RequestWrapper(localName = "getPatient", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.GetPatient")
    @ResponseWrapper(localName = "getPatientResponse", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.GetPatientResponse")
    public boolean getPatient(
        @WebParam(name = "token", targetNamespace = "")
        String token,
        @WebParam(name = "username", targetNamespace = "")
        String username){
    	return NotFenixManager.instance().getPatient(token, username);
    }
}
    