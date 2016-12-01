package pt.andred.sirs1617;

import java.io.IOException;

import pt.andred.sirs1617.ui.Dialog;
import pt.andred.sirs1617.ws.NotFenixManager;

/**
 * Hello world!
 *
 */
public class NotFenix 
{
    public static void main( String[] args )
    {
    	if (args.length < 2) {
    		System.err.println("Argument(s) missing!" + args.length);
			return;
    	}
		for(String s : args)
			System.err.println(s);
		try {
			NotFenixManager.instance().start(args[1]);
			Dialog.IO().println("Waiting for connections");
			Dialog.IO().println("Press enter to shutdown");
			System.in.read();
			Dialog.IO().println("Shutting down");
			NotFenixManager.instance().stop();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
