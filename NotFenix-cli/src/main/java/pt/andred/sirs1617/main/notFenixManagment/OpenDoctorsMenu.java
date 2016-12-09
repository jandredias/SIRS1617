package pt.andred.sirs1617.main.notFenixManagment;

import java.io.IOException;

import pt.andred.sirs1617.ui.Command;
import pt.andred.sirs1617.ui.Dialog;
import pt.andred.sirs1617.ui.DialogException;
import pt.andred.sirs1617.ws.cli.NotFenixClient;

public class OpenDoctorsMenu extends Command<NotFenixClient> {
	public OpenDoctorsMenu(NotFenixClient client){
		super("Open doctors menu", client);
	}

	@Override
	public void execute() throws DialogException, IOException {
		pt.andred.sirs1617.main.doctor.MenuBuilder.menuFor(_receiver);
	}
}
