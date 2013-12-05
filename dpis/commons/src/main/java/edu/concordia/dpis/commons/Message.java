package edu.concordia.dpis.commons;

import java.io.Serializable;
import java.util.List;

public interface Message extends IReliable, Serializable {

	String getActualMessage();

	List<Object> getArguments();

	Address getToAddress();

	int getSequenceNumber();

	boolean isMulticast();

	boolean isReply();

	boolean isReplyToThisMessage();
}
