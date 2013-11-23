package edu.concordia.dpis;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import edu.concordia.dpis.commons.Address;
import edu.concordia.dpis.messenger.UDPServer;

public class Replica extends UDPServer implements Node {

	private RequestHandler requestHandler;

	private Address address;

	private String leaderName;

	// private boolean isLeaderAlive = false;

	private List<Node> nodes = new ArrayList<Node>();

	public Replica(int port) throws UnknownHostException {
		super(port);
		this.address = new Address(InetAddress.getLocalHost().getHostAddress(),
				port);
		this.address.setId(System.currentTimeMillis() + "");
	}

	@Override
	protected String getReplyMessage(DatagramPacket request) {
		return requestHandler.doOperation(request).toString();
	}

	@Override
	public String getLeaderName() {
		return leaderName;
	}

	@Override
	public void newLeader(String name) {
		this.leaderName = name;
	}

	@Override
	public MessageType election(String name) {
		return null;
	}

	@Override
	public Address getAddress() {
		return this.address;
	}

}
