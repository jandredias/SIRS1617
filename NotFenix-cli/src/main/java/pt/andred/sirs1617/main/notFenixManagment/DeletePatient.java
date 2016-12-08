package pt.andred.sirs1617.main.notFenixManagment;

import java.io.IOException;

import pt.andred.sirs1617.ui.Command;
import pt.andred.sirs1617.ui.Dialog;
import pt.andred.sirs1617.ui.DialogException;
import pt.andred.sirs1617.ws.cli.NotFenixClient;

public class DeletePatient extends Command<NotFenixClient> {
	public DeletePatient(NotFenixClient client){
		super("Delete patient", client);
	}

	@Override
	public void execute() throws DialogException, IOException {
		String username = Dialog.IO().readString("Patient's name?");
		Boolean success = _receiver.deletePatient(username);
		if(!success){
			Dialog.IO().println("Patient deleted");
		}else{
			Dialog.IO().println("No such patient");
		}
	}
}
