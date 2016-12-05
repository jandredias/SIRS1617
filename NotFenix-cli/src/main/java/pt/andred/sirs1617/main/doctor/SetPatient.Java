package pt.andred.sirs1617.main;

import java.io.IOException;

import pt.andred.sirs1617.ui.Command;
import pt.andred.sirs1617.ui.Dialog;
import pt.andred.sirs1617.ui.DialogException;
import pt.andred.sirs1617.ws.cli.NotFenixClient;

public class SetPatient extends Command<NotFenixClient> {
	public Login(NotFenixClient client){
		super("Login", client);
	}

	@Override
	public void execute() throws DialogException, IOException {
		String username = Dialog.IO().readString("Username? ");
		String password = Dialog.IO().readPassword("Password? ");
		Boolean success = _receiver.login(username, password);
		if(success){
			pt.andred.sirs1617.main.doctor.MenuBuilder.menuFor(_receiver);
		}else{
			Dialog.IO().println("");
			Dialog.IO().println("Incorrect data");
			Dialog.IO().println("");
		}
	}
}
