package edu.concordia.dpis.commons;

import java.util.List;

public class UDPMessage implements Message {

	private String message;
	
	private Address toAddress;

	public UDPMessage(String message, String host, int port) {
		this.message = message;
		this.toAddress = new Address(host, port);
	}

	public String getActualMessage() {
		return message;
	}

	@Override
	public Address getToAddress() {
		return this.toAddress;
	}

	public static class MessageBuilder {

		private String operationName;

		private List<Object> params;

		private String host;

		private int port;

		public Message build() {
			String actualMsg = prepareMessageAsString();
			UDPMessage message = new UDPMessage(actualMsg, host, port);
			return message;
		}

		private String prepareMessageAsString() {
			StringBuffer sb = new StringBuffer();
			sb.append(operationName);
			for (Object obj : params) {
				sb.append(obj.toString());
			}
			return sb.toString();
		}

		public MessageBuilder setOperationName(String operationName) {
			this.operationName = operationName;
			return this;
		}

		public MessageBuilder addParam(Object param) {
			params.add(param);
			return this;
		}

		public MessageBuilder setToHost(String host) {
			this.host = host;
			return this;
		}

		public MessageBuilder setToPort(int port) {
			this.port = port;
			return this;
		}
	}
}