package edu.concordia.dpis.fifo;

import edu.concordia.dpis.commons.Imessenger;
import edu.concordia.dpis.commons.Message;
import edu.concordia.dpis.commons.TimeoutException;

public class FIFO implements Imessenger {

	private Imessenger messenger;

	private static int sequenceNumber = 0;

	public FIFO(Imessenger messenger) {
		this.messenger = messenger;
	}

	@Override
	public Message send(Message msg, int timeout) throws TimeoutException {
		msg.setSequenceNumber(++sequenceNumber);
		return messenger.send(msg, timeout);
	}
}