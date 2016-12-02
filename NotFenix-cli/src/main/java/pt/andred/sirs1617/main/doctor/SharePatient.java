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
		// TODO Auto-generated method stub
	}
}
