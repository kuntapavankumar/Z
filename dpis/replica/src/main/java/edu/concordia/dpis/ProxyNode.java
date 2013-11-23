package edu.concordia.dpis;

import java.io.FileNotFoundException;
import java.io.IOException;

import edu.concordia.dpis.commons.Address;
import edu.concordia.dpis.commons.Message;
import edu.concordia.dpis.commons.UDPMessage;
import edu.concordia.dpis.commons.UDPMessage.MessageBuilder;
import edu.concordia.dpis.messenger.UDPClient;

public class ProxyNode implements Node {

	private Address address;

	private UDPClient udpClient;

	public ProxyNode() {
		this.udpClient = new UDPClient();
	}

	@Override
	public String getLeaderName() {
		Message fromMessage = sendMessage("getLeaderName");
		if (fromMessage == null) {
			return "";
		}
		return fromMessage.getActualMessage();
	}

	private Message sendMessage(String operationName, Object... params) {
		Message toMessage = newMessage(operationName, params);
		Message fromMessage = null;
		try {
			fromMessage = this.udpClient.send(toMessage);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fromMessage;
	}

	private Message newMessage(String operationName, Object... params) {
		MessageBuilder builder = new UDPMessage.MessageBuilder()
				.setOperationName(operationName)
				.setToHost(this.address.getHost())
				.setToPort(this.address.getPort());
		for (Object param : params) {
			builder.addParam(param);
		}
		return builder.build();
	}

	@Override
	public void newLeader(String name) {
		sendMessage("newLeader", name);
	}

	@Override
	public MessageType election(String name) {
		Message msg = sendMessage("newLeader", name);
		return MessageType.valueOf(msg.getActualMessage());
	}

	@Override
	public Address getAddress() {
		return address;
	}
}
