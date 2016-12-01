package pt.andred.sirs1617;

import pt.andred.sirs1617.ui.Dialog;
import pt.andred.sirs1617.ws.cli.NotFenixClient;

/**
 * Hello world!
 *
 */
public class NotFenixClientApplication 
{
    public static void main( String[] args )
    {
    	NotFenixClient client = new NotFenixClient("http://localhost:8080/notfenix-ws/endpoint");
    	Dialog.IO().println("Login: " + client.ping());
        System.out.println( "Hello World!" );
    }
}
