package edu.concordia.dpis.commons;

import java.io.Serializable;
import java.util.List;

public interface Message extends IReliable, Serializable {

	String getActualMessage();

	List<Object> getArguments();

	Address getToAddress();
}
