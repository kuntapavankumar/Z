package edu.concordia.dpis.messenger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import edu.concordia.dpis.commons.Message;
import edu.concordia.dpis.commons.UDPMessage;

public class UDPClient {

	// private static Logger log = Logger.getLogger(UDPClient.class.getName());

	public Message send(Message msg, int timeout) throws FileNotFoundException,
			IOException, TimeoutException {
		String response = null;
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket();
			byte[] m = msg.getActualMessage().getBytes();
			InetAddress aHost = InetAddress.getByName(msg.getToAddress()
					.getHost());
			DatagramPacket request = new DatagramPacket(m, m.length, aHost, msg
					.getToAddress().getPort());
			aSocket.send(request);
			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			aSocket.setSoTimeout(timeout);
			try {
				aSocket.receive(reply);
			} catch (SocketTimeoutException timeoutException) {
				System.out.println(timeoutException.getMessage());
				throw new TimeoutException();
			}

			response = new String(Arrays.copyOfRange(reply.getData(), 0,
					reply.getLength()));

		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null) {
				aSocket.close();
			}
		}
		final Message retMessage = new UDPMessage(response, msg.getToAddress()
				.getHost(), msg.getToAddress().getPort());
		return retMessage;

	}

	public Message send(Message msg) throws FileNotFoundException, IOException, TimeoutException {
		return send(msg, 0);
	}
}
