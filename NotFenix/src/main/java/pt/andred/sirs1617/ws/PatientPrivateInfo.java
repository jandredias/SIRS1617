package pt.andred.sirs1617.ws;


import java.util.HashMap;
import java.util.Map;

class PatientPrivateInfo{

  private String _name;
  private String _keyEncryptedMaster;
  private Map<String, String> _keysDoctor;
  private String _details;

  private String _2keyEncryptedGeneral;
  private String _publicDetails = null;

  public PatientPrivateInfo(String name, String key_master, String doctorName, String doctorKey, String detailsEnc){

    _name = name;
    _keyEncryptedMaster = key_master;
    _keysDoctor = new HashMap<>();
    _keysDoctor.put(doctorName, doctorKey);
    _details = detailsEnc;
    //_2keyEncryptedGeneral = 2ndKey;//FIXME
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
  public void setDetails(String d){
    _details = d;
  }
  public void setPublicDetails(String d){
    _publicDetails = d;
  }
  public void setPublicKey(String k){
    _2keyEncryptedGeneral = k;
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
  public String getDetails(){
    return _details;
  }
  public String getPublicKey(){
    return _2keyEncryptedGeneral;
  }
  public String getPublicDetails(){
    return _publicDetails;
  }
}
