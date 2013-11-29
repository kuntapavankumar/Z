package edu.concordia.dpis;

import edu.concordia.dpis.commons.Address;
import edu.concordia.dpis.commons.DeadNodeException;

public interface Node {

	Address getAddress();

	String getLeaderName() throws DeadNodeException;

	void newLeader(String name) throws DeadNodeException;

	MessageType election(String name) throws DeadNodeException;

	boolean isAlive() throws DeadNodeException;

}