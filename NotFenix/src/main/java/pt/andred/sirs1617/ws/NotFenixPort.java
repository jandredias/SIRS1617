/**
 * 
 */
package pt.andred.sirs1617.ws;

import javax.jws.WebService;

import pt.andred.sirs1617.ui.Dialog;

/**
 * @author André Dias
 *
 */
@WebService(name = "NotFenixPortType", targetNamespace = "http://ws.sirs1617.andred.pt/")
public class NotFenixPort implements NotFenixPortType {

	/**
	 * 
	 */
	public NotFenixPort() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see pt.andred.sirs1617.ws.NotFenixPortType#login(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean login(String username, String password) {
		Dialog.IO().println("Login try");
		return NotFenixManager.instance().login(username, password);
	}

	/* (non-Javadoc)
	 * @see pt.andred.sirs1617.ws.NotFenixPortType#addPatient(java.lang.String)
	 */
	@Override
	public String addPatient(String in) {
		return NotFenixManager.instance().addPatient(in);
	}

	/* (non-Javadoc)
	 * @see pt.andred.sirs1617.ws.NotFenixPortType#getPatientPublicInfo(java.lang.String)
	 */
	@Override
	public String getPatientPublicInfo(String in) {
		return NotFenixManager.instance().getPatientPublicInfo(in);
	}

	/* (non-Javadoc)
	 * @see pt.andred.sirs1617.ws.NotFenixPortType#getPatientPrivateInfo(java.lang.String)
	 */
	@Override
	public String getPatientPrivateInfo(String in) {
		return NotFenixManager.instance().getPatientPrivateInfo(in);
	}

	/* (non-Javadoc)
	 * @see pt.andred.sirs1617.ws.NotFenixPortType#addPatientPublicInfo(java.lang.String)
	 */
	@Override
	public String addPatientPublicInfo(String in) {
		return NotFenixManager.instance().addPatientPublicInfo(in);
	}

	/* (non-Javadoc)
	 * @see pt.andred.sirs1617.ws.NotFenixPortType#addPatientPrivateInfo(java.lang.String)
	 */
	@Override
	public String addPatientPrivateInfo(String in) {
		return NotFenixManager.instance().addPatientPrivateInfo(in);
	}

	/* (non-Javadoc)
	 * @see pt.andred.sirs1617.ws.NotFenixPortType#deletePatient(java.lang.String)
	 */
	@Override
	public String deletePatient(String in) {
		return NotFenixManager.instance().deletePatient(in);
	}

	/* (non-Javadoc)
	 * @see pt.andred.sirs1617.ws.NotFenixPortType#sharePatient(java.lang.String)
	 */
	@Override
	public String sharePatient(String in) {
		return NotFenixManager.instance().sharePatient(in);
	}

	/* (non-Javadoc)
	 * @see pt.andred.sirs1617.ws.NotFenixPortType#addDoctor(java.lang.String)
	 */
	@Override
	public String addDoctor(String in) {
		return NotFenixManager.instance().addDoctor(in);
	}

	/* (non-Javadoc)
	 * @see pt.andred.sirs1617.ws.NotFenixPortType#deleteDoctor(java.lang.String)
	 */
	@Override
	public String deleteDoctor(String in) {
		return NotFenixManager.instance().deleteDoctor(in);
	}

	/* (non-Javadoc)
	 * @see pt.andred.sirs1617.ws.NotFenixPortType#changePassword(java.lang.String)
	 */
	@Override
	public String changePassword(String in) {
		return NotFenixManager.instance().changePassword(in);
	}

	/* (non-Javadoc)
	 * @see pt.andred.sirs1617.ws.NotFenixPortType#changeDoctorPublicKey(java.lang.String)
	 */
	@Override
	public String changeDoctorPublicKey(String in) {
		return NotFenixManager.instance().changeDoctorPublicKey(in);
	}

	/* (non-Javadoc)
	 * @see pt.andred.sirs1617.ws.NotFenixPortType#resetPatientPublicInfo(java.lang.String)
	 */
	@Override
	public String resetPatientPublicInfo(String in) {
		return NotFenixManager.instance().resetPatientPublicInfo(in);
	}

	/* (non-Javadoc)
	 * @see pt.andred.sirs1617.ws.NotFenixPortType#resetPatientPrivateInfo(java.lang.String)
	 */
	@Override
	public String resetPatientPrivateInfo(String in) {
		return NotFenixManager.instance().resetPatientPrivateInfo(in);
	}

}
