package edu.concordia.dpis.commons;

import edu.concordia.dpis.commons.Message;

public interface Imessenger {
	public Message send(Message msg, int timeout) throws TimeoutException;

}
