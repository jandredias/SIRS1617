package pt.andred.sirs1617.main.doctor;

import java.io.IOException;

import pt.andred.sirs1617.ui.Command;
import pt.andred.sirs1617.ui.Dialog;
import pt.andred.sirs1617.ui.DialogException;
import pt.andred.sirs1617.ws.cli.NotFenixClient;

public class ChangePassword extends Command<NotFenixClient> {
	public ChangePassword(NotFenixClient client){
		super("Change my password", client);
	}

	@Override
	public void execute() throws DialogException, IOException {
		String password = Dialog.IO().readPassword("Old Password? ");
		String new_pass = Dialog.IO().readPassword("New Password? ");
		String rep_new_pass = Dialog.IO().readPassword("Repeat new Password? ");
		if(new_pass.equals(rep_new_pass)){
			if(!_receiver.ChangePassword(username, new_pass, password))
				Dialog.IO().println("Server error or old Password is wrong");
		}
		else{
			Dialog.IO().println("");
			Dialog.IO().println("Passwords don't match");
			Dialog.IO().println("");
		}
	}
}
