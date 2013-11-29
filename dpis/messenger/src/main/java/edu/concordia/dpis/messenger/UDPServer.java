package edu.concordia.dpis.messenger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

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
					while (true) {
						byte[] buffer = new byte[1000];
						DatagramPacket request = new DatagramPacket(buffer,
								buffer.length);
						aSocket.receive(request);
						byte[] payload = getReplyMessage(request).getBytes(
								"US-ASCII");
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

	protected abstract String getReplyMessage(DatagramPacket request);
}
