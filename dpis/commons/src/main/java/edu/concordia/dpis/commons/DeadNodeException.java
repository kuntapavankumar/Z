package edu.concordia.dpis.commons;

public class DeadNodeException extends Exception {

	private static final long serialVersionUID = 1L;

	public DeadNodeException() {
		super("Timed out");
	}

}
