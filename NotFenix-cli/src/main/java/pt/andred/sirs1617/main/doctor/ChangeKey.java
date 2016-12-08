package pt.andred.sirs1617.main.doctor;

import java.io.IOException;

import pt.andred.sirs1617.ui.Command;
import pt.andred.sirs1617.ui.Dialog;
import pt.andred.sirs1617.ui.DialogException;
import pt.andred.sirs1617.ws.cli.NotFenixClient;

public class ChangeKey extends Command<NotFenixClient> {
	public ChangeKey(NotFenixClient client){
		super("Change my key", client);
	}

	@Override
	public void execute() throws DialogException, IOException {
			Dialog.IO().println("Are you sure you want to change your key?");
			String answer = Dialog.IO().readString("yes/no");
			if(answer.matches("yes"))
				if(_receiver.revokeDoctorKey());
						Dialog.IO().println("You have been assigned a new key. A new file has been created with the keys");
				else
						Dialog.IO().println("There was an error. Speak to HR");
	}
}
