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
		String username = Dialog.IO().readString("Patient's name? ");
		if(!_receiver.getPatient(username)){
			Dialog.IO().println("Patient does not exist in database");
			return;
		}
		Dialog.IO().println("Private patient's details:");
		Dialog.IO(). println(_receiver.seePatient(username));
	}
}
