package pt.andred.sirs1617.main.doctor;

import java.io.IOException;

import pt.andred.sirs1617.ui.Command;
import pt.andred.sirs1617.ui.Dialog;
import pt.andred.sirs1617.ui.DialogException;
import pt.andred.sirs1617.ws.cli.NotFenixClient;

public class SeePatient extends Command<NotFenixClient> {
	public SeePatient(NotFenixClient client){
		super("Do we have a patient?", client);
	}

	@Override
	public void execute() throws DialogException, IOException {
		// TODO Auto-generated method stub
		String username = Dialog.IO().readString("Patient's name?");
		Boolean success = _receiver.getPatient(username);
		if(!success){
			Dialog.IO().println("Patient exists");
		}else{
			Dialog.IO().println("No such patient");
		}
	}
}
