package pt.andred.sirs1617.ui;

import java.io.EOFException;
import java.io.IOException;

/**
 * Class Menu manages a set of commands.
 * 
 * @see ist.po.ui.Command
 * @see ist.po.ui.Dialog
 */
@SuppressWarnings("nls")
public class Menu {

	/** Prompt for menu option. */
	private static final String SELECT_OPTION = "Escolha uma opção: ";

	/** Message for communicating an invalid option. */
	private static final String INVALID_OPTION = "Opção inválida!";

	/** Exit option for all menus. */
	private static final String EXIT_OPTION = "0 - Sair";

	/** Menu title. */
	private String _title;

	/** Commands available from the menu. */
	private Command<?> _commands[];

	/**
	 * @param title
	 *            menu title.
	 * 
	 * @param commands
	 *            list of commands managed by the menu.
	 */
	public Menu(String title, Command<?> commands[]) {
		_title = title;
		_commands = commands;
	}

	/**
	 * Main function: the menu interacts with the user and executes the
	 * appropriate commands.
	 */
	public void open() {
		int option = 0;

		while (true) {
			Dialog.IO().println(_title);
			for (int i = 0; i < _commands.length; i++) {
				if (_commands[i].isValid())
					Dialog.IO().println((i + 1) + " - " + _commands[i].title()); //$NON-NLS-1$
			}
			Dialog.IO().println(EXIT_OPTION);

			try {
				option = Dialog.IO().readInteger(SELECT_OPTION);
				if (option == 0)
					return;

				if (option < 0 || option > _commands.length || !_commands[option - 1].isValid()) {
					Dialog.IO().println(INVALID_OPTION);
				} else {
					_commands[option - 1].execute();
					if (_commands[option - 1].isLast())
						return;
				}
			} catch (DialogException oi) {
				Dialog.IO().println(_commands[option - 1].title() + ": " + oi); //$NON-NLS-1$
			} catch (EOFException eof) {
				Dialog.IO().println(ErrorMessages.errorEOF(eof));
				return;
			} catch (IOException ioe) {
				Dialog.IO().println(ErrorMessages.errorIO(ioe));
			} catch (NumberFormatException nbf) {
				Dialog.IO().println(ErrorMessages.errorInvalidNumber(nbf));
			}
		}
	}

}
