package pt.andred.sirs1617.ws.cli;

import java.util.Map;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.io.FileOutputStream;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingProvider;

import pt.andred.sirs1617.ui.Dialog;
import pt.andred.sirs1617.ws.AuthenticationHandler;
import pt.andred.sirs1617.ws.NotFenixPortType;
import pt.andred.sirs1617.ws.NotFenixService;
import pt.andred.sirs1617.ws.PrivateKeyReader;
import pt.andred.sirs1617.ws.PublicKeyReader;
import pt.andred.sirs1617.main.Crypter;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

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
	private String P_PRIVATE_IV = "P_PRIVATE_IV";
	private String P_PUBLIC_IV = "P_PUBLIC_IV";
	private String NULL_STRING_TAG = "NADA";

  private static final String PRIVATE_KEY_FILE = "_private.key";
  private static final String PUBLIC_KEY_FILE = "_public.key";

	public NotFenixClient(String url){
		AuthenticationHandler.setAuthor("doctor");
		_token = null;
		_client = new NotFenixService();
		_port = _client.getNotFenixPort();

		_bindingProvider = (BindingProvider) _port;

		Map<String, Object> requestContext = _bindingProvider.getRequestContext();

		requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
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

		//	Dialog.IO().println("fim do encrypt");
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
    public boolean login(
        String username,
        String password){
    	Dialog.IO().debug("Login request");
    	_token = _port.login(encrypt(username), encrypt(password));
    	Dialog.IO().debug("Login response");
    	if(_token == null) return false;
    	Dialog.IO().debug("Login successful");
		_username = username;
		if(_keySize==0){
			PublicKey pk = Crypter.getPublicKey(_username);
			byte[] pk_byte = pk.getEncoded();
			_keySize = pk_byte.length;
		}
    	return true;
    }

    public boolean addDoctor(
                String dname,
                String password){
			KeyPair key = Crypter.generateRSAKey(dname);
			if(key == null)
				return false;
			PublicKey pk_new = key.getPublic();

			if(pk_new == null)
				return false;

			byte[] pk_byte = pk_new.getEncoded();
			String pKey;

			try{
				pKey = Base64.getEncoder().encodeToString(pk_byte);

			} catch(Exception e){
				return false;
			}
			if(_keySize == 0)
				_keySize = pk_byte.length;


			String allKeys = _port.getAllPublicKeys(encrypt(_token));
      Dialog.IO().println("allKeys_String = <"+ allKeys + ">"); //TESTE
			PrivateKey private_key = Crypter.getPrivateKey(_username);
			if(private_key == null){
				Dialog.IO().println("Your Private Key is not here. You can't access patient's files. Please speak to HR");
				return false;
			}

			//Decrypt and encrypt all keys with the new public Key
			byte allKeys_byte[] = null;
			String allKeysEnc_string= NULL_STRING_TAG;
			if(!Objects.equals(allKeys, "")){
				allKeys_byte = Base64.getDecoder().decode(allKeys);
				int fullSize = allKeys_byte.length;

				try{
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					for(int i = 0; i < fullSize; i+=_keySize){
						byte[] toDecrypt_byte = Arrays.copyOfRange(allKeys_byte, i, i+_keySize);
						String toEncrypt_String = Crypter.decrypt_RSA(toDecrypt_byte, private_key);
						byte[] encrypted = Crypter.encrypt_RSA(toEncrypt_String, pk_new);
						outputStream.write(encrypted);
					}
					byte allKeysEnc[] = outputStream.toByteArray();
					allKeysEnc_string = Base64.getEncoder().encodeToString(allKeysEnc);

				}catch (Exception e) {
					return false;
				}
			}

				if(!_port.addDoctor(encrypt(_token),
						encrypt(dname),
						encrypt(password),
						encrypt(pKey),
						encrypt(allKeysEnc_string)))
					return false;

		return true;
    }

    public boolean deleteDoctor(
                String pname){
			if(Objects.equals(_username, pname)){
				//XXX este é SO NOT IMPORTANT pq nunca vai acontecer segundo o nosso programa
				// è porque uma pessoa se apagou a si propria,
				//o seu token já foi apagado do manager mas o client
				//tem que kickar a pessoa de volta ao login screen
				//Ou seja, se este if for verdade -> login screen
			}
    	return _port.deleteDoctor(encrypt(_token), encrypt(pname));
    }

    public boolean revokeDoctorKey(){

			if(_keySize == 0) return false;
			String allKeysEnc_string;
			try{
				//1st we save the old keys
				PublicKey old_public = Crypter.getPublicKey(_username);
				PrivateKey old_private = Crypter.getPrivateKey(_username);

				//Generate new keys
				KeyPair key = Crypter.generateRSAKey(_username);
				if(key == null){
					//if it doens't work we have to put the keys back the way they were
					try{
						File privateKeyFile = new File(_username+PRIVATE_KEY_FILE);
			      File publicKeyFile = new File(_username+PUBLIC_KEY_FILE);
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
						Dialog.IO().println("A problem has been found and your key is no longer valid. Pelase ask HR to put you back in the system");
					}
					return false;
				}

				//Get the new keys
				PublicKey newPublic = Crypter.getPublicKey(_username);
				PrivateKey newPrivate = Crypter.getPrivateKey(_username);

				//Get all keys that need to be encrypted
				String allKeys = _port.revokeDoctorKey(encrypt(_token));

				//Decrypt and encrypt all keys with the new public Key
				byte allKeys_byte[] = Base64.getDecoder().decode(allKeys);
				int fullSize = allKeys_byte.length;
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				for(int i = 0; i < fullSize; i+=_keySize){
				  byte[] toDecrypt_byte = Arrays.copyOfRange(allKeys_byte, i, i+_keySize);
					String toEncrypt_String = Crypter.decrypt_RSA(toDecrypt_byte, old_private);
					byte[] encrypted = Crypter.encrypt_RSA(toEncrypt_String, newPublic);
				  outputStream.write(encrypted);
				}
				byte allKeysEnc[] = outputStream.toByteArray();
				allKeysEnc_string = Base64.getEncoder().encodeToString(allKeysEnc);
			}catch (Exception e) {
				return false;
			}
			return _port.revokeDoctorKeyPhase2(encrypt(_token), encrypt(allKeysEnc_string));
    }

    public boolean changePassword(
                String username,
                String password,
                String oldPassword){
    	return _port.changePassword(
    			encrypt(_token),
    			encrypt(username),
    			encrypt(password),
    			encrypt(oldPassword));
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
			String allKeysEnc_string = NULL_STRING_TAG;
			String iv2_string;
			String iv_string;

      Dialog.IO().println("---------------------addPatient teste 1"); //TESTE
			try{
				//Generate 1st key
				if(!Crypter.generateAESKey(name, "first"))
				  return false;
				SecretKeySpec sk = Crypter.getSymmKey(name, "first");

	      Dialog.IO().println("---------------------addPatient teste 1.1"); //TESTE
				if(sk == null)
				  return false;

				Dialog.IO().println("---------------------addPatient teste 1.1.1"); //TESTE
				String sk_string = Base64.getEncoder().encodeToString(sk.getEncoded());
	      Dialog.IO().println("---------------------addPatient teste 2"); //TESTE


				//Generate 1st IV
				SecureRandom randomSecureRandom = SecureRandom.getInstance("SHA1PRNG");
				byte[] iv = new byte[16];
				randomSecureRandom.nextBytes(iv);
	      Dialog.IO().println("---------------------addPatient teste 2"); //TESTE
				IvParameterSpec ivParams = new IvParameterSpec(iv);
				iv_string = Base64.getEncoder().encodeToString(ivParams.getIV());
	      Dialog.IO().println("---------------------addPatient teste 2.1"); //TESTE
				Dialog.IO().println("---------------------addPatient teste IV= <"+iv_string+">"); //TESTE
				Dialog.IO().println("---------------------addPatient teste SK= <"+sk_string+">"); //TESTE
				Dialog.IO().println("---------------------addPatient teste De= <"+private_details+">"); //TESTE


				//Encrypt private details with 1st IV and 1st key
				byte[] enc = Crypter.encrypt_AES(private_details, sk, ivParams);
	      Dialog.IO().println("---------------------addPatient teste 2.1.1"); //TESTE
				if(enc == null){
		      Dialog.IO().println("---------------------addPatient teste 2.1.1 NULL"); //TESTE
				}
				Dialog.IO().println("---------------------enc: <" + new String(enc, "UTF-8")+">"); //TESTE
				detailsEnc = Base64.getEncoder().encodeToString(enc);
	      Dialog.IO().println("---------------------addPatient teste 2.2"); //TESTE


				//Encrypt 1st key with Master
				String mKey = getMasterKey();
	      Dialog.IO().println("---------------------addPatient teste 2.3"); //TESTE
				keyMaster = Base64.getEncoder().encodeToString(Crypter.encrypt_RSA(sk_string, mKey));
	      Dialog.IO().println("---------------------addPatient teste 3"); //TESTE


				//Encrypt 1st key with Doctor
				PublicKey dKey = Crypter.getPublicKey(_username);
				keyDoctor = Base64.getEncoder().encodeToString(Crypter.encrypt_RSA(sk_string, mKey));


				//Generate 2nd Key
				if(!Crypter.generateAESKey(name, "second"))
				  return false;
				SecretKeySpec sk2 = Crypter.getSymmKey(name, "second");
				if(sk2 == null)
				  return false;
				String sk2_string = Base64.getEncoder().encodeToString(sk2.getEncoded());

				//Generate 2nd IV
				randomSecureRandom = SecureRandom.getInstance("SHA1PRNG");
				byte[] iv2 = new byte[16];
				randomSecureRandom.nextBytes(iv2);
				IvParameterSpec ivParams2 = new IvParameterSpec(iv2);
				iv2_string = Base64.getEncoder().encodeToString(ivParams2.getIV());
	      Dialog.IO().println("---------------------addPatient teste 4"); //TESTE


				//Encrypt public details with 1st IV and 1st key
				detailsPublicEnc = Base64.getEncoder().encodeToString(Crypter.encrypt_AES(public_details, sk2, ivParams2));


				//Encrypt 2nd key with all keys;
				String allKeys= getAllDoctorKeys();
				if(allKeys != null){
					byte allKeys_byte[] = Base64.getDecoder().decode(allKeys);

					int fullSize = allKeys_byte.length;
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					for(int i = 0; i < fullSize; i+=_keySize){
					  byte[] odKey = Arrays.copyOfRange(allKeys_byte, i, i+_keySize);
					  PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(odKey));
						String odKey_enc = Base64.getEncoder().encodeToString(Crypter.encrypt_RSA(sk2_string, mKey));
					  outputStream.write(Base64.getDecoder().decode(odKey_enc));
					}
					byte allKeysEnc[] = outputStream.toByteArray();
					allKeysEnc_string = Base64.getEncoder().encodeToString(allKeysEnc);
		      Dialog.IO().println("---------------------addPatient teste 5"); //TESTE
				}
			}catch (Exception e) {
				return false;
			}
			Dialog.IO().println("---------------------addPatient teste 6"); //TESTE
    	return _port.addPatient(
    			encrypt(_token),
    			encrypt(name),
    			encrypt(keyMaster),
    			encrypt(keyDoctor),
    			encrypt(iv_string),
    			encrypt(detailsEnc),
    			encrypt(allKeysEnc_string),
    			encrypt(iv2_string),
    			encrypt(detailsPublicEnc));
    }

		public boolean setInfoPatient(String name, String infoMode, String info){
			return _port.setInfoPatient(
					encrypt(_token),
					encrypt(name),
					encrypt(infoMode),
					encrypt(info));
		}

		public String  getMasterKey(){
			return _port.getMasterKey(encrypt(_token));
		}

		public String getMyKey(){
			return _port.getMyKey(encrypt(_token));
		}

		public String getAllDoctorKeys(){
			return _port.getAllDoctorsKeys(encrypt(_token));
		}


    public boolean deletePatient(
                String name){
    	return _port.deletePatient(
    			encrypt(_token),
    			encrypt(name));
    }

    public String getInfoPatient(
                String name,
                String infoName){
    	return _port.getInfoPatient(
    			encrypt(_token),
    			encrypt(name),
    			encrypt(infoName));
    }

		public void seePatient(String name){

			PrivateKey private_key = Crypter.getPrivateKey(_username);
			if(private_key == null){
				Dialog.IO().println("Your Private Key is not here. You can't access patient's files. Please speak to HR");
				return;
			}

			//Check if doctor has access to private details
			String private_symmkey_enc_string  = _port.getInfoPatient(encrypt(_token), encrypt(name), encrypt(P_KEY_DOCTOR_TAG));
			if(private_symmkey_enc_string != null){

				//Private details
				//Get Symmkey
				byte[] private_symmkey_enc_byte = null;
				private_symmkey_enc_byte = Base64.getDecoder().decode(private_symmkey_enc_string);

				try{
					String private_symmKey_string = Crypter.decrypt_RSA(private_symmkey_enc_byte, private_key);
					SecretKeySpec private_symmKey = new SecretKeySpec(Base64.getDecoder().decode(private_symmKey_string), "AES");

					//get IV
					String private_iv_string = _port.getInfoPatient(
							encrypt(_token),
							encrypt(name),

							encrypt(P_PRIVATE_IV));

					//get details
					String private_details_enc_string = _port.getInfoPatient(
							encrypt(_token),
							encrypt(name),
							encrypt(P_DETAILS_TAG));
					byte[] private_details_enc_byte = Base64.getDecoder().decode(private_details_enc_string);
					String private_details = Crypter.decrypt_AES(private_details_enc_byte, private_symmKey, private_iv_string);

					Dialog.IO().println("Private patient's details:");
					Dialog.IO().println(private_details);

				}catch (Exception e) {
					Dialog.IO().println("An error with the private details happened. Please try again");
				}
			}

			//Public Details
			//Get Symmkey
			String public_symmkey_enc_string  = _port.getInfoPatient(
					encrypt(_token),
					encrypt(name),
					encrypt(P_PUBLIC_KEY));
			byte[] public_symmkey_enc_byte = null;
			public_symmkey_enc_byte = Base64.getDecoder().decode(public_symmkey_enc_string);

			try{
				String public_symmKey_string = Crypter.decrypt_RSA(public_symmkey_enc_byte, private_key);
				SecretKeySpec public_symmKey = new SecretKeySpec(Base64.getDecoder().decode(public_symmKey_string), "AES");

				//get IV
				String public_iv_string = _port.getInfoPatient(encrypt(_token), encrypt(name), encrypt(P_PUBLIC_IV));

				//get details
				String public_details_enc_string = _port.getInfoPatient(encrypt(_token), encrypt(name), encrypt(P_PUBLIC_DETAILS));
				byte[] public_details_enc_byte = Base64.getDecoder().decode(public_details_enc_string);
				String public_details = Crypter.decrypt_AES(public_details_enc_byte, public_symmKey, public_iv_string);

				Dialog.IO().println("Public patient's details:");
				Dialog.IO().println(public_details);

			}catch (Exception e) {
				Dialog.IO().println("An error with the private details happened. Please try again");
			}
		}


    public boolean getPatient(
        String username){
    	return _port.getPatient(encrypt(_token), encrypt(username));
    }

		public boolean isMyPatient(String pname){
			return _port.isMyPatient(encrypt(_token), encrypt(pname));
		}

		public boolean sharePatient(String pname, String dsname){

			//Get new Doctor public key
			String d_key = _port.getDoctorKey(encrypt(_token), encrypt(dsname));
			if(d_key == null)
				return false;
			PublicKey publicKey = null;
			try {
				publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(d_key)));
			} catch (InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//get my private key
			PrivateKey private_key = Crypter.getPrivateKey(_username);
			if(private_key == null){
				Dialog.IO().println("Your Private Key is not here. You can't access patient's files. Please speak to HR");
				return false;
			}

			//Get Patient's SymmKey
			String symmkey_enc_string = _port.getInfoPatient(encrypt(_token), encrypt(pname), encrypt(P_KEY_DOCTOR_TAG));
			if(symmkey_enc_string == null)
				return false;
			byte[] symmkey_enc_byte = null;
			symmkey_enc_byte = Base64.getDecoder().decode(symmkey_enc_string);

			//Decrypt SymmKey
			String symmkey_string = Crypter.decrypt_RSA(symmkey_enc_byte, private_key);

			//Encrypt SymmKey with	 new doctor's public key
			byte[] symmkey_enc_new_byte = Crypter.encrypt_RSA(symmkey_string, publicKey);
			String symmkey_enc_new_string = null;
			symmkey_enc_new_string = Base64.getEncoder().encodeToString(symmkey_enc_new_byte);


			return _port.sharePatient(encrypt(_token), encrypt(pname), encrypt(dsname), encrypt(symmkey_enc_new_string));
		}

		public String getUsername() {
			return _username;
		}
	}
