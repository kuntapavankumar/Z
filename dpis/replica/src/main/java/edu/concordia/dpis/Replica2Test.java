package edu.concordia.dpis;

import java.net.UnknownHostException;
import java.util.List;

import edu.concordia.dpis.commons.Address;
import edu.concordia.dpis.stationserver.StationServerImpl;
import edu.concordia.dpis.stationserver.domain.StationType;

public class Replica2Test {

	public static void main(String[] args) throws UnknownHostException {
		start();
	}

	public static void start() throws UnknownHostException {

		Address frontEndAddress = new Address("localhost", 2100);

		Address leaderAddress = new Address("localhost", 2200);
		ProxyNode leader = new ProxyNode(leaderAddress);

		Address replica3Address = new Address("localhost", 2400);
		ProxyNode replica3 = new ProxyNode(replica3Address);

		Address replica4Address = new Address("localhost", 2500);
		ProxyNode replica4 = new ProxyNode(replica4Address);

		Replica replica2 = new Replica(2300, 75, frontEndAddress)
				.addNode(leader).addNode(replica3).addNode(replica4);

		DefaultRequestHandler requestHandler = new DefaultRequestHandler();

		requestHandler.addCommand("isAlive", new Command() {

			@Override
			public Object execute(List<Object> params) {
				return true;
			}
		});

		StationServer stationServer = new StationServerImpl(StationType.SPVM);

		requestHandler.addCommand("createCRecord", new CreateCriminalRecord(
				stationServer));

		replica2.setRequestHandler(requestHandler);

		replica2.start();
		replica2.startFailureDetection();
	}
}
