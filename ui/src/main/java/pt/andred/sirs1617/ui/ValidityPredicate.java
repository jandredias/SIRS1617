package pt.andred.sirs1617.ui;

public abstract class ValidityPredicate<Receiver> {

	/** Receiver for the command. */
	protected Receiver _receiver;

	/**
	 * @param receiver
	 */
	public ValidityPredicate(Receiver receiver) {
		_receiver = receiver;
	}

	/**
	 * @return whether the command is valid for the predicate.
	 */
	public abstract boolean isValid();
}
