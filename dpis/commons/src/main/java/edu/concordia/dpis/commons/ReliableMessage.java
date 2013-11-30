package edu.concordia.dpis.commons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReliableMessage implements Message {

	private static final long serialVersionUID = 1L;

	private int sequenceNumber;

	private String message;

	private Address toAddress;

	private List<Object> args;

	public ReliableMessage(String message, String host, int port) {
		this.message = message;
		this.toAddress = new Address(host, port);
		args = new ArrayList<Object>();
	}

	public String getActualMessage() {
		return message;
	}

	@Override
	public Address getToAddress() {
		return this.toAddress;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public void addArgument(Object arg) {
		this.args.add(arg);
	}

	@Override
	public List<Object> getArguments() {
		return Collections.unmodifiableList(this.args);
	}
}