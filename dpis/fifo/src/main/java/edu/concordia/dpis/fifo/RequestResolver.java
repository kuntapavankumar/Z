package edu.concordia.dpis.fifo;

import java.util.List;

public interface RequestResolver {

	String getOperationName(String message);
	
	List<Object> getArguments(String message);
}
