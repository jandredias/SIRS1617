package pt.andred.sirs1617.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

//import java.util.Base64;

import pt.andred.sirs1617.ui.Dialog;

public class Crypter{

  private static final String PRIVATE_KEY_FILE = "_private.key";
  private static final String PUBLIC_KEY_FILE = "_public.key";
  private static final String SYMM_KEY_FILE = "_symmetric.key";
  private static final String KEY_PREFIX = "";

  /**
   * Generate key which contains a pair of private and public key using 1024
   * bytes. Store the set of keys in Prvate.key and Public.key files.
   *
   * @throws NoSuchAlgorithmException
   * @throws IOException
   * @throws FileNotFoundException
   */
  public static KeyPair generateRSAKey(String username) {
    KeyPair key;
    try {
      final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
      keyGen.initialize(1024);
      key = keyGen.generateKeyPair();
      Dialog.IO().debug("Crypter generateRSAKey teste 1"); //TESTE

      File privateKeyFile = new File(KEY_PREFIX + username+PRIVATE_KEY_FILE);
      File publicKeyFile = new File(KEY_PREFIX +username+PUBLIC_KEY_FILE);
      Dialog.IO().debug("Crypter generateRSAKey teste 2"); //TESTE

      // Create files to store public and private key

      privateKeyFile.createNewFile();
      publicKeyFile.createNewFile();
      Dialog.IO().debug("Crypter generateRSAKey teste 3"); //TESTE

      // Saving the Public key in a file
      ObjectOutputStream publicKeyOS = new ObjectOutputStream(
          new FileOutputStream(publicKeyFile));
      publicKeyOS.writeObject(key.getPublic());
      publicKeyOS.close();
      Dialog.IO().debug("Crypter generateRSAKey teste 4"); //TESTE

      // Saving the Private key in a file
      ObjectOutputStream privateKeyOS = new ObjectOutputStream(
          new FileOutputStream(privateKeyFile));
      privateKeyOS.writeObject(key.getPrivate());
      privateKeyOS.close();
    } catch (Exception e) {
      return null;
    }
    return key;

  }

  public static boolean generateAESKey(String username, String type){
    try{
      KeyGenerator KeyGen = KeyGenerator.getInstance("AES");
      KeyGen.init(128);
      SecretKey secKey = KeyGen.generateKey();
      File KeyFile = new File(username +"_" + type + SYMM_KEY_FILE);

      ObjectOutputStream KeyOS = new ObjectOutputStream(
          new FileOutputStream(KeyFile));
      KeyOS.writeObject(secKey);
      KeyOS.close();

    } catch(Exception e){
      return false;
    }
    return true;
  }

  /**
   * The method checks if the pair of public and private key has been generated.
   *
   * @return flag indicating if the pair of keys were generated.
   */
  public static boolean areRSAKeysPresent(String username) {

    File privateKey = new File(KEY_PREFIX + username+PRIVATE_KEY_FILE);
    File publicKey = new File(KEY_PREFIX + username+PUBLIC_KEY_FILE);

    if (privateKey.exists() && publicKey.exists()) {
      return true;
    }
    return false;
  }
  public static boolean isPublicKeyPresent(String username) {
    Dialog.IO().debug("Crypter isPublicKeyPresent teste 1"); //TESTE

    File publicKey = new File(KEY_PREFIX + username+PUBLIC_KEY_FILE);
      Dialog.IO().debug("Crypter isPublicKeyPresent teste 2"); //TESTE


    if (publicKey.exists()) {
        Dialog.IO().debug("Crypter isPublicKeyPresent teste 2"); //TESTE
      return true;
    }
    return false;
  }
  public static boolean isPrivateKeyPresent(String username) {

    File privateKey = new File(KEY_PREFIX + username+PRIVATE_KEY_FILE);

    if (privateKey.exists()) {
      return true;
    }
    return false;
  }
  public static boolean isSymmKeyPresent(String username, String type) {

    File SymmKey = new File(username +"_" + type +SYMM_KEY_FILE);

    if (SymmKey.exists()) {
      return true;
    }
    return false;
  }
  public static byte[] encrypt_RSA(String text, String username){
    PublicKey pk = getPublicKey(username);
    if(pk==null)
      return null;
    return encrypt_RSA(text, pk);
  }
  /**
   * Encrypt the plain text using public key.
   *
   * @param text
   *          : original plain text
   * @param key
   *          :The public key
   * @return Encrypted text
   * @throws java.lang.Exception
   */
  public static byte[] encrypt_RSA(String text, PublicKey key) {
    byte[] cipherText = null;
    try {
      // get an RSA cipher object and print the provider
      final Cipher cipher = Cipher.getInstance("RSA");
      // encrypt the plain text using the public key
      cipher.init(Cipher.ENCRYPT_MODE, key);
      cipherText = cipher.doFinal(text.getBytes());
    } catch (Exception e) {
      return null;
    }
    return cipherText;
  }

  public static String decrypt_RSA(byte[] text, String username){
    PrivateKey pk = getPrivateKey(username);
    if(pk==null)
      return null;
    return decrypt_RSA(text, pk);
  }
  /**
   * Decrypt text using private key.
   *
   * @param text
   *          :encrypted text
   * @param key
   *          :The private key
   * @return plain text
   * @throws java.lang.Exception
   */
  public static String decrypt_RSA(byte[] text, PrivateKey key) {
    byte[] dectyptedText = null;
    try {
      // get an RSA cipher object and print the provider
      final Cipher cipher = Cipher.getInstance("RSA");

      // decrypt the text using the private key
      cipher.init(Cipher.DECRYPT_MODE, key);
      dectyptedText = cipher.doFinal(text);

    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }

    return new String(dectyptedText);
  }
  public static PublicKey getPublicKey(String username){
    if(isPublicKeyPresent(username) == false)
      return null;
    Dialog.IO().debug("Crypter getPublicKey teste 1"); //TESTE
      ObjectInputStream inputStream;
    try{
      inputStream = new ObjectInputStream(new FileInputStream(KEY_PREFIX + username + PUBLIC_KEY_FILE));
      return(PublicKey) inputStream.readObject();
    } catch (Exception e) {
      return null;
    }
  }
  public static PrivateKey getPrivateKey(String username){
    if(isPrivateKeyPresent(username) == false)
      return null;
      ObjectInputStream inputStream;
    try{
      inputStream = new ObjectInputStream(new FileInputStream(KEY_PREFIX + username + PRIVATE_KEY_FILE));
      return(PrivateKey) inputStream.readObject();
    }catch (Exception e) {
      return null;
    }
  }
  public static SecretKeySpec getSymmKey(String username, String type){
    if(isSymmKeyPresent(username, type) == false)
      return null;
      ObjectInputStream inputStream;
    try{
      inputStream = new ObjectInputStream(new FileInputStream(KEY_PREFIX + username +"_" + type+  SYMM_KEY_FILE));
      return(SecretKeySpec) inputStream.readObject();
    }catch (Exception e) {
      return null;
    }
  }
  /*public static byte[] encrypt_AES(String text, String username, String iv){
    SecretKeySpec sk = getSymmKey(username);
    if(sk==null)
      return null;
    return encrypt_RSA(text, sk, iv);
  }*/
  public static byte[] encrypt_AES(String text, SecretKeySpec key, String IV_string){
    try{
      IvParameterSpec iv =  new IvParameterSpec(IV_string.getBytes());
      return encrypt_AES(text, key, iv);
    } catch(Exception e){
      return null;
    }
  }

  public static byte[] size16 (byte[] input){
    Dialog.IO().debug("---------------------size16 teste 0"); //TESTE
    int size = input.length;
    Dialog.IO().debug("---------------------size16 teste 1"); //TESTE
    if(size % 16 == 0){
      Dialog.IO().debug("---------------------size16 teste 2"); //TESTE
      return input;
    }
    int i;
    byte[] toReturn;
    ByteArrayOutputStream outputStream;
    try{
      for(i = 0; i< size; i+=16);
      	outputStream = new ByteArrayOutputStream();
        outputStream.write(input);
      for(;size<i; size ++){
        outputStream.write(" ".getBytes()[0]);
      }
          Dialog.IO().debug("---------------------size16 teste 3 size:" + input.length); //TESTE
      toReturn = outputStream.toByteArray();
      Dialog.IO().debug("---------------------size16 teste 3 toreturnsize:" + toReturn.length); //TESTE
    }catch (Exception e) {
      return null;
    }
    return toReturn;
  }
  public static byte[] encrypt_AES(String text, SecretKeySpec key, IvParameterSpec iv){
    try{
      Dialog.IO().debug("---------------------encrypt_AES teste 0"); //TESTE
      Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
      Dialog.IO().debug("---------------------encrypt_AES teste 1"); //TESTE
      cipher.init(Cipher.ENCRYPT_MODE, key, iv);
      //text = size16(text);
      byte[] text2 = size16(text.getBytes());
      Dialog.IO().debug("---------------------encrypt_AES teste 2 input: <"+ text2+">"); //TESTE
    //  byte[] a = text.getBytes();
      Dialog.IO().debug("---------------------encrypt_AES teste 2.1"); //TESTE
      byte[] t = cipher.doFinal(text2);
      Dialog.IO().debug("---------------------encrypt_AES teste 3"); //TESTE
      return t;
    } catch(Exception e){
      e.printStackTrace();
      return null;
    }
  }
  public static String decrypt_AES(byte[] cipherText, SecretKeySpec key, String iv){
    try{
       Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
       IvParameterSpec ivparam = new IvParameterSpec(iv.getBytes());
       Dialog.IO().debug("decrypt_AES iv= "+iv); //TESTE
       Dialog.IO().debug("decrypt_AES key= "+key.getEncoded()); //TESTE
       cipher.init(Cipher.DECRYPT_MODE, key, ivparam);
       return new String(cipher.doFinal(cipherText),"UTF-8");
    } catch(Exception e){
      e.printStackTrace();
      return null;
    }
  }

}
