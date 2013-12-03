package edu.concordia.dpis.commons;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MessageTransformer {

	public static byte[] serializeMessage(Message msg) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bos);
		out.writeObject(msg);
		out.close();
		return bos.toByteArray();
	}

	public static Message deserializeMessage(byte[] msg) throws IOException {
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
}
