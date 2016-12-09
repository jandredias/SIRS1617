package pt.andred.sirs1617.ws.cli;

import java.util.Map;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
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
import pt.andred.sirs1617.ws.DoctorInfo;
import pt.andred.sirs1617.ws.NotFenixPortType;
import pt.andred.sirs1617.ws.NotFenixService;
import pt.andred.sirs1617.ws.PatientInfo;
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
			String pk_encoded_byte = Base64.getEncoder().encodeToString(pk_byte);
			_keySize = pk_encoded_byte.getBytes().length;
			Dialog.IO().debug("KEYSIZE", "" + _keySize);
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


		PrivateKey private_key = Crypter.getPrivateKey(_username);
		if(private_key == null){
			Dialog.IO().println("Your Private Key is not here. You can't access patient's files. Please speak to HR");
			return false;
		}

		List<PatientInfo> patients = _port.getAllPatientPublicKey(_token);
		for(PatientInfo p : patients){
			String toEncrypt_String = Crypter.decrypt_RSA(
					Base64.getDecoder().decode(p.getPublicKey()), private_key);
			byte[] key_encrypted = Crypter.encrypt_RSA(toEncrypt_String, pk_new);
			String key_string = Base64.getEncoder().encodeToString(key_encrypted);

			_port.setInfoPatient2(_token, p.getName(), dname, P_PUBLIC_KEY, key_string);
		}
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
			String allKeysEnc_string = "";
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
				byte allKeys_byte[] = allKeys.getBytes();
				int fullSize = allKeys_byte.length;
				//ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

				for(int i = 0; i < fullSize; i+=_keySize){
				  byte[] toDecrypt_byte_encoded = Arrays.copyOfRange(allKeys_byte, i, i+_keySize);
					byte[] toDecrypt_byte = Base64.getDecoder().decode(toDecrypt_byte_encoded);
					String toEncrypt_String = Crypter.decrypt_RSA(toDecrypt_byte, old_private);
					byte[] encrypted = Crypter.encrypt_RSA(toEncrypt_String, newPublic);
					allKeysEnc_string += Base64.getEncoder().encodeToString(encrypted);
				//  outputStream.write(encrypted);
				}
			//	byte allKeysEnc[] = outputStream.toByteArray();
				//allKeysEnc_string = new String(allKeysEnc);
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
			String allKeysEnc_string = "";
			String iv2_string;
			String iv_string;
			String keyDoctor2;
			Dialog.IO().println("---------------------addPatient teste 1"); //TESTE
			try{
				//Simetrica Priv Generate 1st key
				if(!Crypter.generateAESKey(name, "first"))
				  return false;
				SecretKeySpec sk = Crypter.getSymmKey(name, "first");
				Dialog.IO().debug("CHAVE SIMETRICA INFORMACAO PRIVADA", Base64.getEncoder().encodeToString(sk.getEncoded()));
				Dialog.IO().println("---------------------addPatient teste 1.1"); //TESTE
				if(sk == null)
				  return false;

				Dialog.IO().println("---------------------addPatient teste 1.1.1= "+ sk.getEncoded().length +"---------------------***************"); //TESTE
				String sk_string = Base64.getEncoder().encodeToString(sk.getEncoded());
				Dialog.IO().println("---------------------addPatient teste 2"); //TESTE


				//Generate 1st IV
				byte[] iv = new byte[16];
				SecureRandom.getInstance("SHA1PRNG").nextBytes(iv);
				IvParameterSpec ivParams = new IvParameterSpec(iv);
				iv_string = Base64.getEncoder().encodeToString(ivParams.getIV());



				//Encrypt private details with 1st IV and 1st key
				//byte[] enc = Crypter.encrypt_AES(private_details, sk, ivParams);
				//XXX
				Dialog.IO().debug("TEEEEEEEEEESTE", "SIZE: " + "1111111111111111".getBytes().length);

				byte[] enc = Crypter.encrypt_AES(private_details, sk, "1111111111111111");
				Dialog.IO().println("---------------------addPatient teste 2.1.1"); //TESTE
				if(enc == null){
					Dialog.IO().println("---------------------addPatient teste 2.1.1 NULL"); //TESTE
				}
				Dialog.IO().println("---------------------enc: <" + new String(enc, "UTF-8")+">"); //TESTE
				detailsEnc = Base64.getEncoder().encodeToString(enc);
				Dialog.IO().println("---------------------addPatient teste 2.2"); //TESTE


				//Encrypt 1st key with Master
				String mKey_encoded = getMasterKey();
				Dialog.IO().println("---------------------addPatient teste 2.3"); //TESTE
				byte[] mKey_byte = Base64.getDecoder().decode(mKey_encoded);
				PublicKey mKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(mKey_byte));
				keyMaster = Base64.getEncoder().encodeToString(Crypter.encrypt_RSA(sk_string, mKey));

				Dialog.IO().println("---------------------addPatient teste 3"); //TESTE


				//Encrypt 1st key with Doctor
				PublicKey dKey = Crypter.getPublicKey(_username);
				Dialog.IO().println("---------------------addPatient teste 3.0.1"); //TESTE
				keyDoctor = Base64.getEncoder().encodeToString(Crypter.encrypt_RSA(sk_string, dKey));
				Dialog.IO().println("---------------------addPatient teste 3.1"); //TESTE


				//Generate 2nd Key
				if(!Crypter.generateAESKey(name, "second"))
				  return false;
				SecretKeySpec sk2 = Crypter.getSymmKey(name, "second");
				if(sk2 == null)
				  return false;
				String sk2_string = Base64.getEncoder().encodeToString(sk2.getEncoded());
	      Dialog.IO().println("---------------------addPatient teste 3.2 = "+ sk2.getEncoded().length +"---------------------***************"); //TESTE

				//Generate 2nd IV
				byte[] iv2 = new byte[16];
				SecureRandom.getInstance("SHA1PRNG").nextBytes(iv2);
				IvParameterSpec ivParams2 = new IvParameterSpec(iv2);
				iv2_string = Base64.getEncoder().encodeToString(ivParams2.getIV());
	      Dialog.IO().println("---------------------addPatient teste 4"); //TESTE


				//Encrypt public details with 2nd IV and 2nd key
				//detailsPublicEnc = Base64.getEncoder().encodeToString(Crypter.encrypt_AES(public_details, sk2, ivParams2));
	      		//XXX
				detailsPublicEnc = Base64.getEncoder().encodeToString(Crypter.encrypt_AES(public_details, sk2, "1111111111111111"));
				//Encrypt 2nd key with Doctor
				Dialog.IO().println("---------------------addPatient teste 3.0.1"); //TESTE
				 keyDoctor2 = Base64.getEncoder().encodeToString(Crypter.encrypt_RSA(sk2_string, dKey));
				Dialog.IO().println("---------------------addPatient teste 3.1"); //TESTE

				//Encrypt 2nd key with all keys;


				List<DoctorInfo> allKeys= _port.getDoctorsKeysNewFunction(_token);
				if(!_port.addPatient(
						encrypt(_token),
						encrypt(name),
						encrypt(keyMaster),
						encrypt(keyDoctor),
						encrypt(iv_string),
						encrypt(detailsEnc),
					/*	allKeysEnc_string,*/ encrypt(keyDoctor2),
						encrypt(iv2_string),
						encrypt(detailsPublicEnc)))
						return false;

				for(DoctorInfo df: allKeys){

					PublicKey publicKey =KeyFactory.getInstance("RSA").generatePublic(
										new X509EncodedKeySpec(Base64.getDecoder().decode(df.getPublicKey())));
					String odKey_enc = Base64.getEncoder().
							encodeToString(Crypter.encrypt_RSA(sk2_string,df.getPublicKey()));
					_port.setInfoPatient2(_token, name, df.getName(), P_PUBLIC_KEY, odKey_enc);

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;


	}


			/*	String allKeys= getAllDoctorKeys();

	      Dialog.IO().println("---------------------addPatient teste 5 allKeysdd= "+allKeys);
				Dialog.IO().println("---------------------addPatient teste 5 mKey_enco= "+mKey_encoded);
				Dialog.IO().println("---------------------addPatient teste 5 keyDoctor= "+keyDoctor);

				if(!Objects.equals(allKeys, "")){
					byte allKeys_byte[] = allKeys.getBytes();

					int fullSize = allKeys_byte.length;
					//ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					Dialog.IO().println("---------------------addPatient teste 5.1 fullSize= "+fullSize);
					Dialog.IO().println("---------------------addPatient teste 5.1 _keySize= "+_keySize);
					for(int i = 0; i < fullSize; i+=_keySize){
					  byte[] odKey = Arrays.copyOfRange(allKeys_byte, i, i+_keySize);
					  PublicKey publicKey =
							  KeyFactory.
							  	getInstance("RSA").
							  		generatePublic(
							  			new X509EncodedKeySpec(
							  					Base64.getDecoder().decode(odKey)));
						Dialog.IO().println("---------------------addPatient teste 5.1 keygootf= "+ Base64.getEncoder().encodeToString(publicKey.getEncoded()));

						String odKey_enc = new String(Crypter.encrypt_RSA(sk2_string, publicKey));
						//String odKey_enc = new String();
						//for(int iuk = 0; iuk < sk2_string.length(); iuk += 	16){
						//	odKey_enc += new String(Crypter.encrypt_RSA(sk2_string.substring(i, i + 16), publicKey));
						//	//JUST A TEST
							//XXX
						//}


					  //outputStream.write(Base64.getEncoder().encodeToString(odKey_enc.getBytes()).getBytes());
						allKeysEnc_string += Base64.getEncoder().encodeToString(odKey_enc.getBytes());
					}
					//byte allKeysEnc[] = outputStream.toByteArray();
					//allKeysEnc_string = new String(allKeysEnc);
		      Dialog.IO().println("---------------------addPatient teste 5"); //TESTE
				}
				Dialog.IO().println("---------------------addPatient teste 6 1st symmkey      = "+ Base64.getEncoder().encodeToString(sk_string.getBytes())); //TESTE
				Dialog.IO().println("---------------------addPatient teste 6 2nd symmkey      = "+ Base64.getEncoder().encodeToString(sk2_string.getBytes())); //TESTE
				*/
		/*	}catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			if(allKeysEnc_string == null || Objects.equals(allKeysEnc_string, ""))
				allKeysEnc_string = NULL_STRING_TAG;
			Dialog.IO().println("---------------------addPatient teste 6 allKeysEnc_string= "+ allKeysEnc_string); //TESTE
			Dialog.IO().println("---------------------addPatient teste 6 KEYdOCTOR 1ST KEY= "+ keyDoctor); //TESTE

			Dialog.IO().debug("CHAAAAAAAAAAAAAAVE", keyDoctor);
    	return _port.addPatient(
    			encrypt(_token),
    			encrypt(name),
    			encrypt(keyMaster),
    			encrypt(keyDoctor),
    			encrypt(iv_string),
    			encrypt(detailsEnc),
    		/*	allKeysEnc_string,*//* encrypt(keyDoctor2),
    		/*	encrypt(iv2_string),
    			encrypt(detailsPublicEnc));*/


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
		if(!Objects.equals(infoName, P_DETAILS_TAG))
    	return _port.getInfoPatient(
    			encrypt(_token),
    			encrypt(name),
    			encrypt(infoName));
			return seePatient(name);

    }

		public String seePatient(String name){

			PrivateKey private_key = Crypter.getPrivateKey(_username);
			if(private_key == null){
				Dialog.IO().println("Your Private Key is not here. You can't access patient's files. Please speak to HR");
				return null;
			}

			//Check if doctor has access to private details
			String private_symmkey_encoded_string  = _port.getInfoPatient(encrypt(_token), encrypt(name), encrypt(P_KEY_DOCTOR_TAG));
			if(private_symmkey_encoded_string != null){

				Dialog.IO().debug("RETURN CHAVE SIMETRICA INFORMACAO PRIVADA", private_symmkey_encoded_string);

				//Private details
				//Get Symmkey
				byte[] private_symmkey_encrypted_byte = null;
				private_symmkey_encrypted_byte = Base64.getDecoder().decode(private_symmkey_encoded_string);
				String private_details = null;
				try{
					String private_symmKey_string = Crypter.decrypt_RSA(private_symmkey_encrypted_byte, private_key);
					Dialog.IO().debug("RAW PRIV SYMETRIC KEY", private_symmKey_string);
					SecretKeySpec private_symmKey = new SecretKeySpec(Base64.getDecoder().decode(private_symmKey_string), "AES");

					//get IV
					String private_iv_encoded_string = _port.getInfoPatient(
							encrypt(_token),
							encrypt(name),

							encrypt(P_PRIVATE_IV));
					String private_iv_string2 = new String(Base64.getDecoder().decode(private_iv_encoded_string));
					byte[] private_iv_byte = new byte[16];
					byte[] private_iv_byte2 = private_iv_string2.getBytes();
					for(int i =0; i<16; i++){
						private_iv_byte[i] = private_iv_byte2[i];
					}
						String private_iv_string = new String(private_iv_byte);
						private_iv_string = "1111111111111111";

							Dialog.IO().println("--------seePatient iv= "+private_iv_encoded_string +"xxxxxxxxxxxxx"); //TESTE
							Dialog.IO().println("--------seePatient iv length= "+private_iv_encoded_string.getBytes().length +"xxxxxxxxxxxxx"); //TESTE

					//get details
					String private_details_encoded_string = _port.getInfoPatient(
							encrypt(_token),
							encrypt(name),
							encrypt(P_DETAILS_TAG));
					byte[] private_details_encoded_byte = private_details_encoded_string.getBytes();
					System.out.println("seePatient private_details_encoded_byte= "+private_details_encoded_byte); //TESTE
					byte[] private_details_encrypted_byte = Base64.getDecoder().decode(private_details_encoded_byte);
					System.out.println("seePatient private_details_encrypted_byte= "+private_details_encrypted_byte); //TESTE
					private_details = Crypter.decrypt_AES(private_details_encrypted_byte, private_symmKey, private_iv_string);

				//	Dialog.IO().println("Private patient's details:");
				//	Dialog.IO().println(private_details);

				}catch (Exception e) {
					e.printStackTrace();
					Dialog.IO().println("An error with the private details happened. Please try again");
				}
				return private_details;
			}

			//Public Details
			//Get Symmkey
		/*	String public_symmkey_encoded_string  = _port.getInfoPatient(
					encrypt(_token),
					encrypt(name),
					encrypt(P_PUBLIC_KEY));
					Dialog.IO().println("public_symmkey_encoded_string= " +public_symmkey_encoded_string); //TESTE
			try{
				byte[] public_symmkey_encrypted_byte = Base64.getDecoder().decode(public_symmkey_encoded_string.getBytes());
				Dialog.IO().println("public_symmkey_encrypted_byte= " +public_symmkey_encrypted_byte); //TESTE

				String public_symmKey_string  =	Crypter.decrypt_RSA(public_symmkey_encrypted_byte, private_key);
				Dialog.IO().println("public_symmKey_string= " +public_symmKey_string); //TESTE
				Dialog.IO().println("public_symmKey_BYTES= " +public_symmKey_string.getBytes()); //TESTE
				SecretKeySpec public_symmKey = new SecretKeySpec(Base64.getDecoder().decode(public_symmKey_string), "AES");

				//get IV
				String public_iv_encoded_string = _port.getInfoPatient(encrypt(_token), encrypt(name), encrypt(P_PUBLIC_IV));
				//String public_iv_string = new String(Base64.getDecoder().decode(public_iv_encoded_string));
				//XXX
				String public_iv_string = "1111111111111111";



				//get details
				String public_details_encoded_string = _port.getInfoPatient(encrypt(_token), encrypt(name), encrypt(P_PUBLIC_DETAILS));
				byte[] public_details_encoded_byte = public_details_encoded_string.getBytes();
				byte[] public_details_encrypted_byte = Base64.getDecoder().decode(public_details_encoded_byte);
				String public_details = Crypter.decrypt_AES(public_details_encrypted_byte, public_symmKey, public_iv_string);

				Dialog.IO().println("Public patient's details:");
				Dialog.IO().println(public_details);

			}catch (Exception e) {
				e.printStackTrace();
				Dialog.IO().println("An error with the public details happened. Please try again");
			}*/
			return null;
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
			String symmkey_encoded_string = _port.getInfoPatient(encrypt(_token), encrypt(pname), encrypt(P_KEY_DOCTOR_TAG));
			if(symmkey_encoded_string == null)
				return false;
			byte[] symmkey_encrypted_byte = null;
			symmkey_encrypted_byte = Base64.getDecoder().decode(symmkey_encoded_string.getBytes());

			//Decrypt SymmKey
			String symmkey_string = Crypter.decrypt_RSA(symmkey_encrypted_byte, private_key);

			//Encrypt SymmKey with	 new doctor's public key
			byte[] symmkey_encrypted_new_byte = Crypter.encrypt_RSA(symmkey_string, publicKey);
			String symmkey_encrypted_new_string = null;
			symmkey_encrypted_new_string = Base64.getEncoder().encodeToString(symmkey_encrypted_new_byte);


			return _port.sharePatient(encrypt(_token), encrypt(pname), encrypt(dsname), encrypt(symmkey_encrypted_new_string));
		}

		public String getUsername() {
			return _username;
		}
	}
