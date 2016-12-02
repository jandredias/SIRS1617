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
	public Boolean login(String username, String password) {
		Dialog.IO().println(username);
		return NotFenixManager.instance().login(username, password);
	}

}
