package edu.concordia.dpis;

import java.net.UnknownHostException;
import java.util.List;

import edu.concordia.dpis.commons.Address;
import edu.concordia.dpis.stationserver.StationServerImpl;
import edu.concordia.dpis.stationserver.domain.StationType;

public class Replica3Test {

	public static void main(String[] args) throws UnknownHostException {
		start();
	}

	public static void start() throws UnknownHostException {

		Address frontEndAddress = new Address("localhost", 2100);

		Address leaderAddress = new Address("localhost", 2200);
		ProxyNode leader = new ProxyNode(leaderAddress);

		Address replica2Address = new Address("localhost", 2300);
		ProxyNode replica2 = new ProxyNode(replica2Address);

		Address replica4Address = new Address("localhost", 2500);
		ProxyNode replica4 = new ProxyNode(replica4Address);

		Replica replica3 = new Replica(2400, 50, frontEndAddress)
				.addNode(leader).addNode(replica2).addNode(replica4);

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

		replica3.setRequestHandler(requestHandler);

		replica3.start();
		replica3.startFailureDetection();
	}
}
