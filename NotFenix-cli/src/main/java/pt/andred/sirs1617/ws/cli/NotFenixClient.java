package pt.andred.sirs1617.ws.cli;

import java.util.Map;
import java.util.Arrays;
import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.xml.ws.BindingProvider;
import pt.andred.sirs1617.ui.Dialog;
import pt.andred.sirs1617.ws.NotFenixPortType;
import pt.andred.sirs1617.ws.NotFenixService;
import pt.andred.sirs1617.main.Crypter;
import java.security.*;
import java.security.SecureRandom;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.KeyFactory;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import java.security.spec.X509EncodedKeySpec;

public class NotFenixClient {
	private NotFenixService _client;
	private NotFenixPortType _port;
	private BindingProvider _bindingProvider;
	private String _token;
	private int _keySize = 0;
	private String _username;

	private String P_NAME_TAG = "P_NAME";
	private String P_KEY_MASTER_TAG = "P_KEY_M";
	private String P_KEY_DOCTOR_TAG = "P_KEY_DOCTOR";
	private String P_DETAILS_TAG = "P_DETAILS";
	private String P_PUBLIC_KEY = "P_PUBLIC_KEY";
	private String P_PUBLIC_DETAILS = "P_PUBLIC_DETAILS";
	private Stirng P_PRIVATE_IV = "P_PRIVATE_IV";
	private String P_PUBLIC_IV = "P_PUBLIC_IV";

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
			_username = username;
    	return true;
    }

    public boolean addDoctor(
                String username,
                String password){

			if(!Crypter.generateRSAKey(username))
				return false;
			PublicKey pk = Crypter.getPublicKey(username);
			if(pk == null)
				return false;
			byte[] pk_byte = pk.getEncoded();
			String pKey;
			try{
				pKey = new String(pk_byte, "UTF-8");
			} catch(Exception e){
				return false;
			}
			if(_keySize == 0)
				_keySize = pk_byte.length;
    	return _port.addDoctor(_token, username, password, pKey);
    }

    public boolean deleteDoctor(
                String username){
    	return _port.deleteDoctor(_token, username);
    }

    public boolean revokeDoctorKey(){

			if(_keySize == 0) return false;
			String allKeysEnc_string;
			try{
				//1st we save the old keys
				PublicKey old_public = Crypter.getPublicKey(_username);
				PrivateKey old_private = Crypter.getPrivateKey(_username);

				//Generate new keys
				if(!generateRSAKey(_username)){
					//if it doens't work we have to put the keysback the way they were
					try{
						File privateKeyFile = new File(username+PRIVATE_KEY_FILE);
			      File publicKeyFile = new File(username+PUBLIC_KEY_FILE);
						privateKeyFile.createNewFile();
						publicKeyFile.createNewFile();

						// Saving the Public key in a file
						ObjectOutputStream publicKeyOS = new ObjectOutputStream(
								new FileOutputStream(publicKeyFile));
						publicKeyOS.writeObject(key.getPublic());
						publicKeyOS.close();

						// Saving the Private key in a file
						ObjectOutputStream privateKeyOS = new ObjectOutputStream(
								new FileOutputStream(privateKeyFile));
						privateKeyOS.writeObject(key.getPrivate());
						privateKeyOS.close();
					}catch (Exception e) {
						Dialog.IO().System.out.println("A problem has been found and your key is no longer valid. Pelase ask HR to put you back in the system");
					}
					return false;
				}

				//Get the new keys
				PublicKey newPublic = Crypter.getPublicKey(_username);
				PrivateKey newPrivate = Crypter.getPrivateKey(_username);

				//Get all keys that need to be encrypted
				String allKeys = _port.revokeDoctorKey(_token);

				//Decrypt and encrypt all keys with the new public Key
				byte allKeys_byte[] = allKeys.getBytes("UTF-8");
				int fullSize = allKeys_byte.length;
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				for(int i = 0; i < fullSize; i+=_keySize){
				  byte[] toDecrypt_byte = Arrays.copyOfRange(allKeys_byte, i, i+_keySize);
					String toEncrypt_String = Crypter.decrypt_RSA(toDecrypt_byte, old_private);
					byte[] encripted = Crypter.encrypt_RSA(toEncript_string, newPublic);
				  outputStream.write(encripted.getBytes());
				}
				byte allKeysEnc[] = outputStream.toByteArray();
				allKeysEnc_string = new String(allKeysEnc, "UTF-8");
			}
			return _port.revokeDoctorKey_phase2(token, allKeysEnc_string);
    }

    public boolean changePassword(
                String username,
                String password,
                String oldPassword){
    	return _port.changePassword(_token, username, password, oldPassword);
    }

    public boolean addPatient(
                String name,
                String private_details,
								String public_details){
			if(_keySize == 0) return false;
			String keyMaster;
			String keyDoctor;
			String detailsEnc;
			String detailsPublicEnc;
			String allKeysEnc_string;
			String iv2_string;
			String iv_string;
			try{
				//Generate 1st key
				if(!Crypter.generateAESKey(name, "first"))
				  return false;
				SecretKeySpec sk = Crypter.getSymmKey(name);
				if(sk == null)
				  return false;
				String sk_string = new String(sk.getEncoded(), "UTF-8");


				//Generate 1st IV
				SecureRandom randomSecureRandom = SecureRandom.getInstance("SHA1PRNG");
				byte[] iv = new byte[_keySize];
				randomSecureRandom.nextBytes(iv);
				IvParameterSpec ivParams = new IvParameterSpec(iv);
				iv_string = new String(ivParams.getIV(), "UTF.8");


				//Encrypt private details with 1st IV and 1st key
				detailsEnc = new String(Crypter.encrypt_AES(private_details, sk, ivParams), "UTF-8");


				//Encrypt 1st key with Master
				String mKey = getMasterKey();
				keyMaster = new String(Crypter.encrypt_RSA(sk_string, mKey),"UTF-8");


				//Encrypt 1st key with Doctor
				PublicKey dKey = Crypter.getPublicKey(_username);
				keyDoctor = new String(Crypter.encrypt_RSA(sk_string, mKey),"UTF-8");


				//Generate 2nd Key
				if(!Crypter.generateAESKey(name, "second"))
				  return false;
				SecretKeySpec sk2 = Crypter.getSymmKey(name);
				if(sk2 == null)
				  return false;
				String sk2_string = new String(sk2.getEncoded(), "UTF-8");

				//Generate 2nd IV
				randomSecureRandom = SecureRandom.getInstance("SHA1PRNG");
				byte[] iv2 = new byte[_keySize];
				randomSecureRandom.nextBytes(iv);
				IvParameterSpec ivParams2 = new IvParameterSpec(iv2);
				iv2_string = new String(ivParams2.getIV(), "UTF.8");


				//Encrypt public details with 1st IV and 1st key
				detailsPublicEnc = new String(Crypter.encrypt_AES(public_details, sk2, ivParams2), "UTF-8");


				//Encrypt 2nd key with all keys;
				String allKeys= getAllDoctorKeys();
				byte allKeys_byte[] = allKeys.getBytes("UTF-8");

				int fullSize = allKeys_byte.length;
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				for(int i = 0; i < fullSize; i+=_keySize){
				  byte[] odKey = Arrays.copyOfRange(allKeys_byte, i, i+_keySize);
				  PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(odKey));
					String odKey_enc = new String(Crypter.encrypt_RSA(sk2_string, mKey),"UTF-8");
				  outputStream.write(odKey_enc.getBytes());
				}
				byte allKeysEnc[] = outputStream.toByteArray();
				allKeysEnc_string = new String(allKeysEnc, "UTF-8");
			}catch (Exception e) {
				return false;
			}
    	return _port.addPatient(_token, name, keyMaster, keyDoctor,
			iv_string, detailsEnc, allKeysEnc_string, iv2_string,
			detailsPublicEnc);
    }

		public boolean setInfoPatient(String name, String infoMode, String info){
			return _port.setInfoPatient(_token, name, infoMode, info);
		}

		public String  getMasterKey(){
			return _port.getMasterKey(_token);
		}

		public String getMyKey(){
			return _port.getMyKey(_token)
		}

		public String getAllDoctorKeys(){
			eturn _port.getAllDoctorKeys(_token);
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

		public void seePatient(String name){
			String private_enc = _port.getInfoPatient(token, name, P_KEY_DOCTOR_TAG);
			if(!private_enc == null){
				String private_details = _port.getInfoPatient(token, name, P_DETAILS_TAG);
				String private_iv = _port.getInfoPatient(token, name, P_PRIVATE_IV);

				try{

				}catch (Exception e) {
					Dialog.IO().println("An error happened. Please try again");
					return;
				}

			}

		}


    public boolean getPatient(
        String username){
    	return _port.getPatient(_token, username);
    }



}
