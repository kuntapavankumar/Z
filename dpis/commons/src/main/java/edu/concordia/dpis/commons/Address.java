package edu.concordia.dpis.commons;

import java.io.Serializable;

public class Address implements Serializable{

	private static final long serialVersionUID = 1L;

	private String id;

	private String host;

	private int port;

	public Address(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
