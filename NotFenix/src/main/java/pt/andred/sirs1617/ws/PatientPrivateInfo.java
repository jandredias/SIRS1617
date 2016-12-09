package pt.andred.sirs1617.ws;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.Arrays;

class PatientPrivateInfo{

  private String _name;
  private String _keyEncryptedMaster;
  private Map<String, String> _keysDoctor;
  private String _privateIV;
  private String _details;

  private Map<String, String> _publicKeys;
  private String _publicIV;
  private String _publicDetails;

  private int _keySize;

  public PatientPrivateInfo(String name, String key_master, String doctorName,
                      String doctorKey, String privateIV,  String detailsEnc,
                      Set<String> allDoctors, String keyPublicDoctors,
                      String publicIV, String publicDetailsEnc, int keysize) throws Exception{

    _name = name;
    _keyEncryptedMaster = key_master;
    _keysDoctor = new HashMap<>();
    _keysDoctor.put(doctorName, doctorKey);
    _publicKeys = new HashMap<>();
    _details = detailsEnc;
    System.out.println("PatientPrivateInfo detailsEnc = "+_details);//TESTE
    _publicIV = publicIV;
    _privateIV = privateIV;
    _publicDetails = publicDetailsEnc;
    _keySize = keysize;

    Iterator itr= allDoctors.iterator();
    int i = 0;
    byte[] keySet_bytes = keyPublicDoctors.getBytes();
    System.out.println("PatientPrivateInfo keySet_bytes = "+keySet_bytes); //TESTE
    System.out.println("PatientPrivateInfo keysize = "+keysize);//TESTE

    int max = keySet_bytes.length;
    while(itr.hasNext()) {
      if(i>=max)
        throw new Exception("Failed");
      String doc = (String) itr.next();
      byte[] bytes = Arrays.copyOfRange(keySet_bytes, i, i+_keySize);
      System.out.println("PatientPrivateInfo bytes = "+bytes);//TESTE
      String key = new String(bytes);
      System.out.println("PatientPrivateInfo doc = "+doc);//TESTE
      System.out.println("PatientPrivateInfo key = "+key);//TESTE
      System.out.println("PatientPrivateInfo LENGHT = "+bytes.length);//TESTE
      _publicKeys.put(doc, key);
      i+=_keySize;

    }

  }



  public boolean checkDoctor(String doctor){
    return _keysDoctor.containsKey(doctor);
  }
  public void removeDoctor(String doctor){
    _keysDoctor.remove(doctor);
  }
  public void removePublicDoctor(String doctor){
    _publicKeys.remove(doctor);
  }
  //Set's and Get's
  public void setName(String name){
    _name = name;
  }
  public void setKeyMaster(String key){
    _keyEncryptedMaster = key;
  }
  public void setKeyDoctor(String doctor, String keyDoctor){
    _keysDoctor.put(doctor, keyDoctor);
  }
  public void setPublicKeyDoctor(String doctor, String keyDoctor){
    _publicKeys.put(doctor, keyDoctor);
  }
  public void setDetails(String d){
    _details = d;
  }
  public void setPublicDetails(String d){
    _publicDetails = d;
  }
  public void setIV(String i){
    _privateIV = i;
  }
  public void setPublicIV(String i){
    _publicIV = i;
  }
  public String getName(){
    return _name;
  }
  public String getKeyMaster(){
    return _keyEncryptedMaster;
  }
  public String getKeyDoctor(String doctor){
    return _keysDoctor.get(doctor);
  }
  public String getPublicKey(String doctor){
    return _publicKeys.get(doctor);
  }
  public String getDetails(){
    return _details;
  }
  public String getPublicDetails(){
    return _publicDetails;
  }
  public String getIV(){
    return _privateIV;
  }
  public String getPublicIV(){
    return _publicIV;
  }
}
