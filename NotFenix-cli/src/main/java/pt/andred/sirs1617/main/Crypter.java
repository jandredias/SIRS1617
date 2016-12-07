package pt.andred.sirs1617.main;

import java.io.File;
import java.io.FileInputStream;
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

public class Crypter{

  public static final String PRIVATE_KEY_FILE = "_private.key";
  public static final String PUBLIC_KEY_FILE = "_public.key";
  public static final String SYMM_KEY_FILE = "_symmetric.key";

  /**
   * Generate key which contains a pair of private and public key using 1024
   * bytes. Store the set of keys in Prvate.key and Public.key files.
   *
   * @throws NoSuchAlgorithmException
   * @throws IOException
   * @throws FileNotFoundException
   */
  public static boolean generateRSAKey(String username) {
    try {
      final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
      keyGen.initialize(1024);
      final KeyPair key = keyGen.generateKeyPair();

      File privateKeyFile = new File(username+PRIVATE_KEY_FILE);
      File publicKeyFile = new File(username+PUBLIC_KEY_FILE);

      // Create files to store public and private key

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
    } catch (Exception e) {
      return false;
    }
    return true;

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

    File privateKey = new File(username+PRIVATE_KEY_FILE);
    File publicKey = new File(username+PUBLIC_KEY_FILE);

    if (privateKey.exists() && publicKey.exists()) {
      return true;
    }
    return false;
  }
  public static boolean isPublicKeyPresent(String username) {

    File publicKey = new File(username+PUBLIC_KEY_FILE);

    if (publicKey.exists()) {
      return true;
    }
    return false;
  }
  public static boolean isPrivateKeyPresent(String username) {

    File privateKey = new File(username+PRIVATE_KEY_FILE);

    if (privateKey.exists()) {
      return true;
    }
    return false;
  }
  public static boolean isSymmKeyPresent(String username) {

    File SymmKey = new File(username+PRIVATE_KEY_FILE);

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
      return null;
    }

    return new String(dectyptedText);
  }
  public static PublicKey getPublicKey(String username){
    if(isPublicKeyPresent(username) == false)
      return null;
      ObjectInputStream inputStream;
    try{
      inputStream = new ObjectInputStream(new FileInputStream(username + PUBLIC_KEY_FILE));
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
      inputStream = new ObjectInputStream(new FileInputStream(username + PRIVATE_KEY_FILE));
      return(PrivateKey) inputStream.readObject();
    }catch (Exception e) {
      return null;
    }
  }
  public static SecretKeySpec getSymmKey(String username){
    if(isSymmKeyPresent(username) == false)
      return null;
      ObjectInputStream inputStream;
    try{
      inputStream = new ObjectInputStream(new FileInputStream(username + SYMM_KEY_FILE));
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
      IvParameterSpec iv =  new IvParameterSpec(IV_string.getBytes("UTF-8"));
      return encrypt_AES(text, key, iv);
    } catch(Exception e){
      return null;
    }
  }
  public static byte[] encrypt_AES(String text, SecretKeySpec key, IvParameterSpec iv){
    try{
      Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
      cipher.init(Cipher.ENCRYPT_MODE, key, iv);
      return cipher.doFinal(text.getBytes("UTF-8"));
    } catch(Exception e){
      return null;
    }
  }
  public static String decrypt_AES(byte[] cipherText, SecretKeySpec key, String iv){
    try{
       Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
       cipher.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(iv.getBytes("UTF-8")));
       return new String(cipher.doFinal(cipherText),"UTF-8");
    } catch(Exception e){
      return null;
    }
  }

}