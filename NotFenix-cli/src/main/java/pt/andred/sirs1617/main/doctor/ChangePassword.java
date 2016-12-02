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
		// TODO Auto-generated method stub
	}
}
