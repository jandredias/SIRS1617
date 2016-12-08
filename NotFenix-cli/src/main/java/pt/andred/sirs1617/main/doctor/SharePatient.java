package pt.andred.sirs1617.main.doctor;

import java.io.IOException;

import pt.andred.sirs1617.ui.Command;
import pt.andred.sirs1617.ui.Dialog;
import pt.andred.sirs1617.ui.DialogException;
import pt.andred.sirs1617.ws.cli.NotFenixClient;

public class SharePatient extends Command<NotFenixClient> {
	public SharePatient(NotFenixClient client){
		super("Share a patient with another doctor", client);
	}

	@Override
	public void execute() throws DialogException, IOException {


////NOT DONE
		String pname = Dialog.IO().readString("Patient's name?");
		String dsname =  Dialog.IO().readString("Doctor to be shared with?");
		Boolean success = _receiver.isMyPatient(pname);
		if(!_receiver.getPatient(pname))
			Dialog.IO().println("This patient does not exist");
		else if(!_receiver.isMyPatient(pname))
			Dialog.IO().println("You don't have access to this patient");
		else{
			if(_receiver.sharePatient(pname, dsname))
				Dialog.IO().println("Patient successfully shared");
			else
				Dialog.IO().println("An error happened. Are you sure tha doctor exists?");
		}
	}
}
