package edu.concordia.dpis;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.UnknownHostException;

public interface IGroupListener {

	void joinGroup();

	void leaveGroup() throws UnknownHostException, IOException;

	Object onMessage(DatagramPacket pack);
}
