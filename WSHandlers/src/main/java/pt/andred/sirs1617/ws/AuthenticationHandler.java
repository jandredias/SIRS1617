package pt.andred.sirs1617.ws;

import java.util.Set;

import javax.crypto.Cipher;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import pt.andred.sirs1617.ui.Dialog;

@SuppressWarnings("restriction")
public class AuthenticationHandler implements SOAPHandler<SOAPMessageContext>{

	private static String MESSAGE_AUTHOR;

	public void close(MessageContext arg0) {}

	public boolean handleFault(SOAPMessageContext arg0) {
		Dialog.IO().red();
		Dialog.IO().debug("Fault Message Received");
		Dialog.IO().reset();
		LoggingHandler.logToSystemERR(arg0);
		return false;
	}

	public boolean handleMessage(SOAPMessageContext arg0) {
		Boolean outbound = (Boolean) arg0.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if(outbound){
    		Dialog.IO().yellow();
    		Dialog.IO().debug("AuthenticationHandler", "Outbound SOAP message:");
    		Dialog.IO().white();
			try{
				signMessage(arg0);
			}catch(Exception e){
				Dialog.IO().red();
				Dialog.IO().debug("Failed to cipher body because: " + e.getMessage());
				Dialog.IO().reset();
			}
		}else{
    		Dialog.IO().cyan();
    		Dialog.IO().debug("AuthenticationHandler", "Inbound SOAP message:");
    		Dialog.IO().white();
			try{
				if(checkSignature(arg0)){
					Dialog.IO().debug("Message is valid");
					return true;
				}else{
					Dialog.IO().debug("Message is not valid");
					return false;
				}
			}catch(Exception e){
				Dialog.IO().red();
				Dialog.IO().debug("Failed to validate message because: " + e.getMessage());
				Dialog.IO().reset();
			}
		}
		return true;
	}

	private boolean checkSignature(SOAPMessageContext arg0) {
		// TODO Auto-generated method stub
		return true;
	}

	private void signMessage(SOAPMessageContext arg0) {
		// TODO Auto-generated method stub
		
	}

	public Set<QName> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void setAuthor(String name){
		AuthenticationHandler.MESSAGE_AUTHOR = name;
	}
	
	public static String getAuthor(){ return AuthenticationHandler.MESSAGE_AUTHOR; }
	

}
