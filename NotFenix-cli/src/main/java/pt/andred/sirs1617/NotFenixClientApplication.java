package pt.andred.sirs1617;

import pt.andred.sirs1617.main.MenuBuilder;
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

    	pt.andred.sirs1617.main.MenuBuilder.menuFor(client);;
    }
}
