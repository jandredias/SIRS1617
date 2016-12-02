package pt.andred.sirs1617.ws.cli;

import java.util.Map;

import javax.xml.ws.BindingProvider;

import pt.andred.sirs1617.ws.NotFenixPortType;
import pt.andred.sirs1617.ws.NotFenixService;

public class NotFenixClient {
	private NotFenixService _client;
	private NotFenixPortType _port;
	private BindingProvider _bindingProvider;
	
	public NotFenixClient(String url){
		_client = new NotFenixService();
		_port = _client.getNotFenixPort();
		
		_bindingProvider = (BindingProvider) _port;
		
		Map<String, Object> requestContext = _bindingProvider.getRequestContext();
		
		requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
	}
	
	public boolean login(String username, String password){
		return _port.login(username, password);
	}
}
