/**
 *
 */
package pt.andred.sirs1617.ws;

import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;
import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
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
	private String encrypt(String text){
		//Dialog.IO().println("signMessage signing");
		PublicKey publicKey;
		try {
			publicKey = PublicKeyReader.get("public_key.der");

			// specify mode and padding instead of relying on defaults (use OAEP if available!)
			Cipher encrypt = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			// init with the *public key*!
			encrypt.init(Cipher.ENCRYPT_MODE, publicKey);
			// encrypt with known character encoding, you should probably use hybrid cryptography instead

			byte[] bodyByte = text.getBytes("UTF-8");
			byte[] bodyByteEncrypted = encrypt.doFinal(bodyByte);
			String encrypted = Base64.getEncoder().encodeToString(bodyByteEncrypted);

			//Dialog.IO().println("fim do signing");
			return encrypted;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	private String decrypt(String encryptedText){
		try {
			PrivateKey privateKey = PrivateKeyReader.get("private_key.der");

			Cipher decrypt=Cipher.getInstance("RSA/ECB/PKCS1Padding");
			decrypt.init(Cipher.DECRYPT_MODE, privateKey);

			byte[] bodyByte64 = Base64.getDecoder().decode(encryptedText);

			byte[] bodyByteDecrypted = decrypt.doFinal(bodyByte64);
			String bodyDecrypted;
			bodyDecrypted = new String(bodyByteDecrypted, "UTF-8");
			return bodyDecrypted;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
    	return NotFenixManager.instance().login(decrypt(username), decrypt(password));
    }

    /**
     *
     * @param password
     * @param allKeysEncrypted
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
        String publicKey,
        @WebParam(name = "allKeysEncrypted", targetNamespace = "")
        String allKeysEncrypted){
    	return NotFenixManager.instance().addDoctor(
    			decrypt(token),
    			decrypt(username),
    			decrypt(password),
    			decrypt(publicKey),
    			decrypt(allKeysEncrypted));
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
    	return NotFenixManager.instance().deleteDoctor(
    			decrypt(token),
    			decrypt(username));
    }

    /**
     *
     * @param token
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(name = "keys", targetNamespace = "")
    @RequestWrapper(localName = "revokeDoctorKey", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.RevokeDoctorKey")
    @ResponseWrapper(localName = "revokeDoctorKeyResponse", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.RevokeDoctorKeyResponse")
    public String revokeDoctorKey(
        @WebParam(name = "token", targetNamespace = "")
        String token){
    	return NotFenixManager.instance().revokeDoctorKey(decrypt(token));
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
    	return NotFenixManager.instance().changePassword(
    			decrypt(token),
    			decrypt(username),
    			decrypt(password),
    			decrypt(oldPassword));
   }

    /**
     *
     * @param detailsEnc
     * @param keyMaster
     * @param name
     * @param iv2String
     * @param keyDoctor
     * @param detailsPublicEnc
     * @param allKeysEncString
     * @param ivString
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
        @WebParam(name = "iv_string", targetNamespace = "")
        String ivString,
        @WebParam(name = "detailsEnc", targetNamespace = "")
        String detailsEnc,
        @WebParam(name = "allKeysEnc_string", targetNamespace = "")
        String allKeysEncString,
        @WebParam(name = "iv2_string", targetNamespace = "")
        String iv2String,
        @WebParam(name = "detailsPublicEnc", targetNamespace = "")
        String detailsPublicEnc){
    	return NotFenixManager.instance().addPatient(
    			decrypt(token),
    			decrypt(name),
    			decrypt(keyMaster),
    			decrypt(keyDoctor),
    			decrypt(ivString),
    			decrypt(detailsEnc),
    			decrypt(allKeysEncString),
    			decrypt(iv2String),
    			decrypt(detailsPublicEnc));
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
    	return NotFenixManager.instance().deletePatient(decrypt(token), decrypt(name));
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
    	return NotFenixManager.instance().getInfoPatient(
    			decrypt(token),
    			decrypt(name),
    			decrypt(infoName));
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
    	return NotFenixManager.instance().setInfoPatient(
    			decrypt(token),
    			decrypt(name),
    			decrypt(infoName),
    			decrypt(infoValue));
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
    	return NotFenixManager.instance().getPatient(
    			decrypt(token),
				decrypt(username));
    }



    /**
     *
     * @param token
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(name = "key", targetNamespace = "")
    @RequestWrapper(localName = "getMasterKey", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.GetMasterKey")
    @ResponseWrapper(localName = "getMasterKeyResponse", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.GetMasterKeyResponse")
    public String getMasterKey(
        @WebParam(name = "token", targetNamespace = "")
        String token){
		return NotFenixManager.instance().getMasterKey(decrypt(token));
	}

    /**
     *
     * @param token
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(name = "key", targetNamespace = "")
    @RequestWrapper(localName = "getMyKey", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.GetMyKey")
    @ResponseWrapper(localName = "getMyKeyResponse", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.GetMyKeyResponse")
    public String getMyKey(
        @WebParam(name = "token", targetNamespace = "")
        String token){
		return NotFenixManager.instance().getMyKey(decrypt(token));
	}

    /**
     *
     * @param token
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(name = "key", targetNamespace = "")
    @RequestWrapper(localName = "getAllDoctorsKeys", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.GetAllDoctorsKeys")
    @ResponseWrapper(localName = "getAllDoctorsKeysResponse", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.GetAllDoctorsKeysResponse")
    public String getAllDoctorsKeys(
        @WebParam(name = "token", targetNamespace = "")
        String token){
		return NotFenixManager.instance().getAllDoctorsKeys(decrypt(token));
	}

    /**
     *
     * @param keys
     * @param token
     * @return
     *     returns boolean
     */
    @WebMethod(operationName = "revokeDoctorKey_phase2")
    @WebResult(name = "out", targetNamespace = "")
    @RequestWrapper(localName = "revokeDoctorKey_phase2", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.RevokeDoctorKeyPhase2")
    @ResponseWrapper(localName = "revokeDoctorKey_phase2Response", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.RevokeDoctorKeyPhase2Response")
    public boolean revokeDoctorKeyPhase2(
        @WebParam(name = "token", targetNamespace = "")
        String token,
        @WebParam(name = "keys", targetNamespace = "")
        String keys){
    	return NotFenixManager.instance().revokeDoctorKey_phase2(decrypt(token), decrypt(keys));
   }

    /**
     *
     * @param token
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(name = "allKeys", targetNamespace = "")
    @RequestWrapper(localName = "getAllPublicKeys", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.GetAllPublicKeys")
    @ResponseWrapper(localName = "getAllPublicKeysResponse", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.GetAllPublicKeysResponse")
    public String getAllPublicKeys(
        @WebParam(name = "token", targetNamespace = "")
        String token){
    	return NotFenixManager.instance().getAllPublicKeys(decrypt(token));
	}

    /**
     *
     * @param pname
     * @param dsname
     * @param symmkeyEncNewString
     * @param token
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(name = "out", targetNamespace = "")
    @RequestWrapper(localName = "sharePatient", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.SharePatient")
    @ResponseWrapper(localName = "sharePatientResponse", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.SharePatientResponse")
    public boolean sharePatient(
        @WebParam(name = "token", targetNamespace = "")
        String token,
        @WebParam(name = "pname", targetNamespace = "")
        String pname,
        @WebParam(name = "dsname", targetNamespace = "")
        String dsname,
        @WebParam(name = "symmkey_enc_new_string", targetNamespace = "")
        String symmkeyEncNewString){
		return NotFenixManager.instance().sharePatient(
				decrypt(token),
				decrypt(pname),
				decrypt(dsname),
				decrypt(symmkeyEncNewString));
	}

	/**
     *
     * @param name
     * @param token
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(name = "out", targetNamespace = "")
    @RequestWrapper(localName = "isMyPatient", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.IsMyPatient")
    @ResponseWrapper(localName = "isMyPatientResponse", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.IsMyPatientResponse")
    public boolean isMyPatient(
        @WebParam(name = "token", targetNamespace = "")
        String token,
        @WebParam(name = "name", targetNamespace = "")
        String name){
		return NotFenixManager.instance().isMyPatient(decrypt(token), decrypt(name));
	}
    /**
     *
     * @param dname
     * @param token
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(name = "key", targetNamespace = "")
    @RequestWrapper(localName = "getDoctorKey", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.GetDoctorKey")
    @ResponseWrapper(localName = "getDoctorKeyResponse", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.GetDoctorKeyResponse")
    public String getDoctorKey(
        @WebParam(name = "token", targetNamespace = "")
        String token,
        @WebParam(name = "dname", targetNamespace = "")
        String dname){
    	return NotFenixManager.instance().getDoctorKey(decrypt(token), decrypt(dname));
    }

    /**
     *
     * @param token
     * @return
     *     returns java.util.List<pt.andred.sirs1617.ws.Array>
     */
    @WebMethod
    @WebResult(name = "array", targetNamespace = "")
    @RequestWrapper(localName = "getDoctorsKeysNewFunction", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.GetDoctorsKeysNewFunction")
    @ResponseWrapper(localName = "getDoctorsKeysNewFunctionResponse1", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.GetDoctorsKeysNewFunctionResponse1")
    public List<DoctorInfo> getDoctorsKeysNewFunction(
        @WebParam(name = "token", targetNamespace = "")
        String token){
    	return NotFenixManager.instance().getDoctorsKeysNewFunction(decrypt(token));
    }

    /**
     *
     * @param infoName
     * @param infoValue
     * @param pName
     * @param dname
     * @param token
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(name = "success", targetNamespace = "")
    @RequestWrapper(localName = "setInfoPatient2", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.SetInfoPatient2")
    @ResponseWrapper(localName = "setInfoPatient2Response1", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.SetInfoPatient2Response1")
    public boolean setInfoPatient2(
        @WebParam(name = "token", targetNamespace = "")
        String token,
        @WebParam(name = "pName", targetNamespace = "")
        String pName,
        @WebParam(name = "infoName", targetNamespace = "")
        String infoName,
        @WebParam(name = "infoValue", targetNamespace = "")
        String infoValue,
        @WebParam(name = "dname", targetNamespace = "")
        String dname){
    	return NotFenixManager.instance().setInfoPatient2(
    			decrypt(token),
    			decrypt(pName),
    			decrypt(infoName),
    			decrypt(infoValue),
    			decrypt(dname));
    }

    /**
     *
     * @param token
     * @return
     *     returns java.util.List<pt.andred.sirs1617.ws.PatientInfo>
     */
    @WebMethod
    @WebResult(name = "list", targetNamespace = "")
    @RequestWrapper(localName = "getAllPatientPublicKey", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.GetAllPatientPublicKey")
    @ResponseWrapper(localName = "getAllPatientPublicKeyResponse1", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.GetAllPatientPublicKeyResponse1")
    public List<PatientInfo> getAllPatientPublicKey(
        @WebParam(name = "token", targetNamespace = "")
        String token){
    	return NotFenixManager.instance().getAllPatientPublicKey(decrypt(token));
    }
}
