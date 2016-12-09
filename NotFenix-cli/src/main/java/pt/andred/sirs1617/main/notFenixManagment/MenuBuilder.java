package pt.andred.sirs1617.main.notFenixManagment;

import pt.andred.sirs1617.ui.Command;
import pt.andred.sirs1617.ui.Menu;
import pt.andred.sirs1617.ws.cli.NotFenixClient;

public abstract class MenuBuilder {

	/**
	 * @param receiver
	 */
	public static void menuFor(NotFenixClient client) {
		Menu menu = new Menu("NotFenix Managment Area", new Command<?>[] {
			new AddDoctor(client),
			new DeleteDoctor(client),
			new DeletePatient(client),
			new OpenDoctorsMenu(client)
				 });
		menu.open();
	}

}
