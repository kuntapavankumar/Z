package edu.concordia.dpis.fifo;

import edu.concordia.dpis.commons.Message;
import edu.concordia.dpis.messenger.Imessenger;

public class FIFO implements Imessenger {

	Imessenger messenger;
	
	FIFO(Imessenger messenger){
		this.messenger = messenger;
	}
	
	@Override
	public Message send(Message msg) {
		// tamper msg
		String s=msg.getActualMessage();
		
		this.messenger.send(msg);
		return null;
	}
}
