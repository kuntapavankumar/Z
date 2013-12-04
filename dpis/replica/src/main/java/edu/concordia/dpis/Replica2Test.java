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
		leaderAddress.setId("100");

		Address replica3Address = new Address("localhost", 2400);
		ProxyNode replica3 = new ProxyNode(replica3Address);
		replica3Address.setId("50");

		Address replica4Address = new Address("localhost", 2500);
		ProxyNode replica4 = new ProxyNode(replica4Address);
		replica4Address.setId("25");

		Replica replica2 = new Replica(2300, 75, frontEndAddress)
				.addNode(leader).addNode(replica3).addNode(replica4);

		DefaultRequestHandler requestHandler = new DefaultRequestHandler();

		requestHandler.addCommand("isAlive", new Command() {

			@Override
			public Object execute(List<Object> params) {
				return true;
			}
		});

		StationServerImpl spvm = new StationServerImpl(StationType.SPVM);
		spvm.startUDPServer("3200");
		spvm.startTCPPServer("3300");

		StationServerImpl spb = new StationServerImpl(StationType.SPB);
		spb.startUDPServer("3400");
		spb.startTCPPServer("3500");

		StationServerImpl spl = new StationServerImpl(StationType.SPL);
		spl.startUDPServer("3600");
		spl.startTCPPServer("3700");

		requestHandler.addCommand("createCRecord", new CreateCriminalRecord(
				spvm, spb, spl));

		requestHandler.addCommand("getRecordCounts", new CreateCriminalRecord(
				spvm, spb, spl));

		replica2.setRequestHandler(requestHandler);
		replica2.start();
		replica2.startFailureDetection();
	}
}
