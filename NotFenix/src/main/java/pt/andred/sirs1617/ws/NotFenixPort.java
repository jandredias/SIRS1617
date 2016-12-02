/**
 * 
 */
package pt.andred.sirs1617.ws;

import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.Action;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import pt.andred.sirs1617.ui.Dialog;

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
     *     returns boolean
     */
    @WebMethod
    @WebResult(name = "success", targetNamespace = "")
    @RequestWrapper(localName = "login", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.Login")
    @ResponseWrapper(localName = "loginResponse", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.LoginResponse")
    public boolean login(
        @WebParam(name = "username", targetNamespace = "")
        String username,
        @WebParam(name = "password", targetNamespace = "")
        String password){
		return NotFenixManager.instance().login(username, password);
    	
    }

    /**
     * 
     * @param password
     * @param name
     * @param publicKey
     * @param username
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(name = "success", targetNamespace = "")
    @RequestWrapper(localName = "addDoctor", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.AddDoctor")
    @ResponseWrapper(localName = "addDoctorResponse", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.AddDoctorResponse")
    public boolean addDoctor(
        @WebParam(name = "name", targetNamespace = "")
        String name,
        @WebParam(name = "username", targetNamespace = "")
        String username,
        @WebParam(name = "password", targetNamespace = "")
        String password,
        @WebParam(name = "publicKey", targetNamespace = "")
        String publicKey){
		return NotFenixManager.instance().addDoctor(username, publicKey);
    }

    /**
     * 
     * @param username
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(name = "success", targetNamespace = "")
    @RequestWrapper(localName = "deleteDoctor", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.DeleteDoctor")
    @ResponseWrapper(localName = "deleteDoctorResponse", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.DeleteDoctorResponse")
    public boolean deleteDoctor(
        @WebParam(name = "username", targetNamespace = "")
        String username){
		return NotFenixManager.instance().deleteDoctor(username);
    }

    /**
     * 
     * @param newPublicKey
     * @param oldPublicKey
     * @param username
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(name = "success", targetNamespace = "")
    @RequestWrapper(localName = "revokeDoctorKey", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.RevokeDoctorKey")
    @ResponseWrapper(localName = "revokeDoctorKeyResponse", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.RevokeDoctorKeyResponse")
    public boolean revokeDoctorKey(
        @WebParam(name = "username", targetNamespace = "")
        String username,
        @WebParam(name = "oldPublicKey", targetNamespace = "")
        String oldPublicKey,
        @WebParam(name = "newPublicKey", targetNamespace = "")
        String newPublicKey){
		return NotFenixManager.instance().revokeDoctorKey(username, oldPublicKey, newPublicKey);
    	
    }

    /**
     * 
     * @param password
     * @param username
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(name = "out", targetNamespace = "")
    @RequestWrapper(localName = "changePassword", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.ChangePassword")
    @ResponseWrapper(localName = "changePasswordResponse", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.ChangePasswordResponse")
    public String changePassword(
        @WebParam(name = "username", targetNamespace = "")
        String username,
        @WebParam(name = "password", targetNamespace = "")
        String password){
		return NotFenixManager.instance().changePassword(username, password);
    	
    }

    /**
     * 
     * @param name
     * @param key
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(name = "success", targetNamespace = "")
    @RequestWrapper(localName = "addPatient", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.AddPatient")
    @ResponseWrapper(localName = "addPatientResponse", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.AddPatientResponse")
    public boolean addPatient(
        @WebParam(name = "name", targetNamespace = "")
        String name,
        @WebParam(name = "key", targetNamespace = "")
        String key){
		return NotFenixManager.instance().addPatient(name, key);
    	
    }

    /**
     * 
     * @param name
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(name = "success", targetNamespace = "")
    @RequestWrapper(localName = "deletePatient", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.DeletePatient")
    @ResponseWrapper(localName = "deletePatientResponse", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.DeletePatientResponse")
    public boolean deletePatient(
        @WebParam(name = "name", targetNamespace = "")
        String name){
		return NotFenixManager.instance().deletePatient(name);
    	
    }

    /**
     * 
     * @param infoName
     * @param name
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(name = "out", targetNamespace = "")
    @RequestWrapper(localName = "getInfoPatient", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.GetInfoPatient")
    @ResponseWrapper(localName = "getInfoPatientResponse", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.GetInfoPatientResponse")
    public String getInfoPatient(
        @WebParam(name = "name", targetNamespace = "")
        String name,
        @WebParam(name = "infoName", targetNamespace = "")
        String infoName){
		return NotFenixManager.instance().getInfoPatient(name, infoName);
    }

    /**
     * 
     * @param infoName
     * @param infoValue
     * @param name
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(name = "out", targetNamespace = "")
    @RequestWrapper(localName = "setInfoPatient", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.SetInfoPatient")
    @ResponseWrapper(localName = "setInfoPatientResponse", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.SetInfoPatientResponse")
    public String setInfoPatient(
        @WebParam(name = "name", targetNamespace = "")
        String name,
        @WebParam(name = "infoName", targetNamespace = "")
        String infoName,
        @WebParam(name = "infoValue", targetNamespace = "")
        String infoValue){
		return NotFenixManager.instance().setInfoPatient(name, infoName, infoValue);
    }
    
    /**
     * 
     * @param username
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(name = "success", targetNamespace = "")
    @RequestWrapper(localName = "getPatient", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.GetPatient")
    @ResponseWrapper(localName = "getPatientResponse1", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.GetPatientResponse1")
    public boolean getPatient(
        @WebParam(name = "username", targetNamespace = "")
        String username){
    	return NotFenixManager.instance().getPatient(username);
    }
}
    