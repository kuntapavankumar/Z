package edu.concordia.dpis.fifo;

import java.util.List;

import edu.concordia.dpis.commons.Message;

public interface RequestResolver {

	String getOperationName(Message request);

	List<Object> getArguments(Message request);
}
