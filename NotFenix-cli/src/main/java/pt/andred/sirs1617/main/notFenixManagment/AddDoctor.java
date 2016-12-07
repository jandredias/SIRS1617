package pt.andred.sirs1617.main.notFenixManagment;

import java.io.IOException;

import pt.andred.sirs1617.ui.Command;
import pt.andred.sirs1617.ui.Dialog;
import pt.andred.sirs1617.ui.DialogException;
import pt.andred.sirs1617.ws.cli.NotFenixClient;

public class AddDoctor extends Command<NotFenixClient> {
	public AddDoctor(NotFenixClient client){
		super("Add Doctor", client);
	}

	@Override
	public void execute() throws DialogException, IOException {
		String username = Dialog.IO().readString("Username? ");
		String password = Dialog.IO().readPassword("Password? ");
		String rep_pass = Dialog.IO().readPassword("Repeat Password? ");
		if(password.equals(rep_pass)){
			if(!_receiver.addDoctor(username, password))
				Dialog.IO().println("Server error");
		}
		else{
			Dialog.IO().println("");
			Dialog.IO().println("Incorrect data");
			Dialog.IO().println("");
		}
	}
}
