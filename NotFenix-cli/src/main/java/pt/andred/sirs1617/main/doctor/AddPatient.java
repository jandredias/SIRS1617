package pt.andred.sirs1617.main.doctor;

import java.io.IOException;

import pt.andred.sirs1617.ui.Command;
import pt.andred.sirs1617.ui.Dialog;
import pt.andred.sirs1617.ui.DialogException;
import pt.andred.sirs1617.ws.cli.NotFenixClient;

public class AddPatient extends Command<NotFenixClient> {
	public AddPatient(NotFenixClient client){
		super("Add a new patient", client);
	}

	@Override
	public void execute() throws DialogException, IOException {
		// TODO Auto-generated method stub
	}
}
