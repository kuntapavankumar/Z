package edu.concordia.dpis.messenger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;

import edu.concordia.dpis.commons.Imessenger;
import edu.concordia.dpis.commons.Message;
import edu.concordia.dpis.commons.ReliableMessage;
import edu.concordia.dpis.commons.TimeoutException;

public class UDPClient implements Imessenger {

	@Override
	public Message send(Message msg, int timeout)
			throws edu.concordia.dpis.commons.TimeoutException {
		String response = null;
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket();
			byte[] m = serializeMessage(msg);
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
		final Message retMessage = new ReliableMessage(response, msg
				.getToAddress().getHost(), msg.getToAddress().getPort());
		return retMessage;

	}

	private byte[] serializeMessage(Message msg) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bos);
		out.writeObject(msg);
		out.close();
		return bos.toByteArray();
	}
}
