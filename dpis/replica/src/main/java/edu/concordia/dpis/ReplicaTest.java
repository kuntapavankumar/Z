package edu.concordia.dpis;

import java.net.UnknownHostException;

import edu.concordia.dpis.commons.Address;

public class ReplicaTest {

	public static void main(String[] args) throws UnknownHostException {
		leader();
	}

	private static void leader() throws UnknownHostException {
		Address replica1Address = new Address("localhost", 2200);
		ProxyNode replica1 = new ProxyNode(replica1Address);

		Address replica2Address = new Address("localhost", 2300);
		ProxyNode replica2 = new ProxyNode(replica2Address);

		Address replica3Address = new Address("localhost", 2400);
		ProxyNode replica3 = new ProxyNode(replica3Address);

		Address frontEndAddress = new Address("localhost", 2333);

		Replica leader = new Replica(2100, true, 10, frontEndAddress)
				.addNode(replica1).addNode(replica2).addNode(replica3);

		leader.start();
		leader.startFailureDetection();
	}
}
