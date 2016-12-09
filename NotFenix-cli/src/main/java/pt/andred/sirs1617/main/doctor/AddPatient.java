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
			Dialog.IO().println("");
			String pname = Dialog.IO().readString("Name? ");
			String private_details = Dialog.IO().readString("Private Details? ");
			String public_details = Dialog.IO().readString("Sharable Details? ");
			Boolean exists = _receiver.getPatient(pname);
			if(exists){
				Dialog.IO().println("Patient already exists");
				return;
			}
			if(_receiver.addPatient(pname, private_details, public_details))
				Dialog.IO().println("Patient added successfully");
			else
				Dialog.IO().println("Server Error");


	}
}
