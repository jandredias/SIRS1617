package pt.andred.sirs1617.main.notFenixManagment;

import java.io.IOException;

import pt.andred.sirs1617.ui.Command;
import pt.andred.sirs1617.ui.Dialog;
import pt.andred.sirs1617.ui.DialogException;
import pt.andred.sirs1617.ws.cli.NotFenixClient;

public class DeleteDoctor extends Command<NotFenixClient> {
	public DeleteDoctor(NotFenixClient client){
		super("Delete Doctor", client);
	}

	@Override
	public void execute() throws DialogException, IOException {
		Dialog.IO().println("");
		String dname = Dialog.IO().readString("Name of doctor to remove? ");
		Boolean success = _receiver.deleteDoctor(dname);
		if(success)
				Dialog.IO().println("Doctor deleted successfully");
		else
				Dialog.IO().println("Some Error");
	}

}
