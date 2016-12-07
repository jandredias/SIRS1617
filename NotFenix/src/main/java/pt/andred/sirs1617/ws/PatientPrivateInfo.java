package pt.andred.sirs1617.ws;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

class PatientPrivateInfo{

  private String _name;
  private String _keyEncryptedMaster;
  private Map<String, String> _keysDoctor;
  private String _privateIV;
  private String _details;

  private Map<String, String> _publicKeys;
  private String _publicIV;
  private String _publicDetails;

  public PatientPrivateInfo(String name, String key_master, String doctorName,
                      String doctorKey, String privateIV,  String detailsEnc,
                      Set<String> allDoctors, String[] keyPublicDoctors,
                      String publicIV, String publicDetailsEnc){

    _name = name;
    _keyEncryptedMaster = key_master;
    _keysDoctor = new HashMap<>();
    _keysDoctor.put(doctorName, doctorKey);
    _details = detailsEnc;
    _publicIV = publicIV;
    _publicDetails = publicDetailsEnc;

    Iterator itr= allDoctors.iterator();
    int i = 0;
    while(itr.hasNext()) {
      String doc = (String) itr.next();
      _publicKeys.put(doc, keyPublicDoctors[i]);
      i++;
    }
  }


  public boolean checkDoctor(String doctor){
    return _keysDoctor.containsKey(doctor);
  }
  public void removeDoctor(String doctor){
    _keysDoctor.remove(doctor);
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
