package pt.andred.sirs1617.ws;


import java.util.HashMap;
import java.util.Map;

class PatientPrivateInfo{

  private String _name;
  private String _keyEncryptedMaster;
  private Map<String, String> _keysDoctor;
  private String _details;

  public PatientPrivateInfo(String name, String key_master, String doctorName, String doctorKey, String detailsEnc){

    _name = name;
    _keyEncryptedMaster = key_master;
    _keysDoctor = new HashMap<>();
    _keysDoctor.put(doctorName, doctorKey);
    _details = details;
  }


  public boolean checkDoctor(String doctor){
    return _keysDoctor.containsKey(doctor);
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
}
