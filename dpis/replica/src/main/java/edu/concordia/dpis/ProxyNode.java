package edu.concordia.dpis;

import edu.concordia.dpis.commons.Address;
import edu.concordia.dpis.commons.DeadNodeException;
import edu.concordia.dpis.commons.Message;
import edu.concordia.dpis.commons.ReliableMessage;
import edu.concordia.dpis.messenger.UDPClient;

public class ProxyNode implements Node {

	private Address address;

	private UDPClient udpClient;

	public ProxyNode() {
		this.udpClient = new UDPClient();
	}

	@Override
	public String getLeaderName() throws DeadNodeException {
		Message fromMessage = sendMessage("getLeaderName");
		if (fromMessage == null) {
			return "";
		}
		return fromMessage.getActualMessage();
	}

	private Message sendMessage(String operationName, Object... params)
			throws DeadNodeException {
		Message toMessage = newMessage(operationName, params);
		Message fromMessage = null;
		try {
			fromMessage = this.udpClient.send(toMessage, 1000);
		} catch (edu.concordia.dpis.commons.TimeoutException e) {
			e.printStackTrace();
		}
		return fromMessage;
	}

	private Message newMessage(String operationName, Object... params) {
		ReliableMessage rMsg = new ReliableMessage(operationName,
				this.address.getHost(), this.address.getPort());

		for (Object param : params) {
			rMsg.addArgument(param);
		}
		return rMsg;
	}

	@Override
	public void newLeader(String name) throws DeadNodeException {
		sendMessage("newLeader", name);
	}

	@Override
	public MessageType election(String name) throws DeadNodeException {
		Message msg = sendMessage("election", name);
		return MessageType.valueOf(msg.getActualMessage());
	}

	@Override
	public Address getAddress() {
		return address;
	}

	@Override
	public boolean isAlive() {
		try {
			Message msg = sendMessage("isAlive");
			if (msg != null) {
				return true;
			}
		} catch (DeadNodeException ex) {
			System.out.println(ex.getMessage());
		}
		return false;
	}
}
