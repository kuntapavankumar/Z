package edu.concordia.dpis.messenger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import edu.concordia.dpis.commons.Message;
import edu.concordia.dpis.commons.ReliableMessage;

public abstract class UDPServer {
	private DatagramSocket aSocket = null;
	private final int port;

	public UDPServer(int port) {
		this.port = port;
	}

	public void start() {
		new Thread(new Runnable() {
			public void run() {
				try {
					aSocket = new DatagramSocket(port);
					System.out.println("UDPServer is up and running on port"
							+ port);
					while (true) {
						byte[] buffer = new byte[1000];
						DatagramPacket request = new DatagramPacket(buffer,
								buffer.length);
						aSocket.receive(request);
						byte[] payload = getReplyMessage(
								deserializeMessage(request.getData()))
								.getBytes("US-ASCII");
						DatagramPacket reply = new DatagramPacket(payload,
								payload.length, request.getAddress(),
								request.getPort());
						aSocket.send(reply);
					}
				} catch (SocketException e) {
					System.out.println("Socket: " + e.getMessage());
				} catch (IOException e) {
					System.out.println("IO: " + e.getMessage());
				} finally {
					if (aSocket != null) {
						aSocket.close();
					}
				}
			}

		}).start();
	}

	public void close() {
		if (aSocket != null) {
			aSocket.close();
		}
	}

	private Message deserializeMessage(byte[] msg) throws IOException {
		ObjectInputStream reader = new ObjectInputStream(
				new ByteArrayInputStream(msg));
		try {
			Message request = (ReliableMessage) reader.readObject();
			return request;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected abstract String getReplyMessage(Message message);
}
