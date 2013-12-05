package edu.concordia.dpis;

import java.net.UnknownHostException;
import java.util.List;

import edu.concordia.dpis.commons.Address;
import edu.concordia.dpis.stationserver.StationServerImpl;
import edu.concordia.dpis.stationserver.domain.StationType;

public class Replica4Test {

	public static void main(String[] args) throws UnknownHostException {
		start();
	}

	public static void start() throws UnknownHostException {

		Address frontEndAddress = new Address("localhost", 2100);

		Address leaderAddress = new Address("localhost", 2200);
		ProxyNode leader = new ProxyNode(leaderAddress);
		leaderAddress.setId("100");

		Address replica2Address = new Address("localhost", 2300);
		ProxyNode replica2 = new ProxyNode(replica2Address);
		replica2Address.setId("75");

		Address replica3Address = new Address("localhost", 2400);
		ProxyNode replica3 = new ProxyNode(replica3Address);
		replica3Address.setId("50");

		Replica replica4 = new Replica(2500, 25, frontEndAddress)
				.addNode(leader).addNode(replica2).addNode(replica3);

		DefaultRequestHandler requestHandler = new DefaultRequestHandler();

		requestHandler.addCommand("isAlive", new Command() {

			@Override
			public Object execute(List<Object> params) {
				return true;
			}
		});

		StationServerImpl spvm = new StationServerImpl(StationType.SPVM);
		spvm.startUDPServer("4019");
		spvm.startTCPPServer("4020");
		StationServer spb = new StationServerImpl(StationType.SPB);
		spvm.startUDPServer("4021");
		spvm.startTCPPServer("4022");
		StationServer spl = new StationServerImpl(StationType.SPL);
		spvm.startUDPServer("4023");
		spvm.startTCPPServer("4024");

		requestHandler.addCommand("createCRecord", new CreateCriminalRecord(
				spvm, spb, spl));
		replica4.setRequestHandler(requestHandler);

		replica4.start();
		replica4.startFailureDetection();
	}
}
