package pt.andred.sirs1617.main.patient;

import pt.andred.sirs1617.ui.Command;
import pt.andred.sirs1617.ui.Menu;
import pt.andred.sirs1617.ws.cli.NotFenixClient;

public abstract class MenuBuilder {

	/**
	 * @param receiver
	 */
	public static void menuFor(NotFenixClient client) {
		Menu menu = new Menu("Patient Area", new Command<?>[] {});
		menu.open();
	}

}
