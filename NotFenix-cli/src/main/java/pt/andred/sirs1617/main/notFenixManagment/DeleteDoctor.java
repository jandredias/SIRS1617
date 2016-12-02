package pt.andred.sirs1617.main.notFenixManagment;

import java.io.IOException;

import pt.andred.sirs1617.ui.Command;
import pt.andred.sirs1617.ui.Dialog;
import pt.andred.sirs1617.ui.DialogException;
import pt.andred.sirs1617.ws.cli.NotFenixClient;

public class DeleteDoctor extends Command<NotFenixClient> {
	public DeleteDoctor(NotFenixClient client){
		super("Login", client);
	}

	@Override
	public void execute() throws DialogException, IOException {
		// TODO Auto-generated method stub
	}
}
