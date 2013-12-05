package edu.concordia.dpis;

import java.net.UnknownHostException;
import java.util.List;

import edu.concordia.dpis.commons.Address;
import edu.concordia.dpis.stationserver.StationServerImpl;
import edu.concordia.dpis.stationserver.domain.StationType;

public class LeaderTest {

	public static void main(String[] args) throws UnknownHostException {
		start();
	}

	public static void start() throws UnknownHostException {

		Address frontEndAddress = new Address("localhost", 2100);

		Address replica2Address = new Address("localhost", 2300);
		replica2Address.setId("75");
		ProxyNode replica2 = new ProxyNode(replica2Address);

		Address replica3Address = new Address("localhost", 2400);
		ProxyNode replica3 = new ProxyNode(replica3Address);
		replica3Address.setId("50");

		Address replica4Address = new Address("localhost", 2500);
		ProxyNode replica4 = new ProxyNode(replica4Address);
		replica4Address.setId("25");

		Replica leader = new Replica(2200, true, 100, frontEndAddress).addNode(
				replica2).addNode(replica3);
		// .addNode(replica4);

		DefaultRequestHandler requestHandler = new DefaultRequestHandler();

		requestHandler.addCommand("isAlive", new Command() {

			@Override
			public Object execute(List<Object> params) {
				return true + "";
			}
		});

		StationServerImpl spvm = new StationServerImpl(StationType.SPVM);
		spvm.startUDPServer("2600");
		spvm.startTCPPServer("2700");

		StationServerImpl spb = new StationServerImpl(StationType.SPB);
		spb.startUDPServer("2800");
		spb.startTCPPServer("2900");

		StationServerImpl spl = new StationServerImpl(StationType.SPL);
		spl.startUDPServer("3000");
		spl.startTCPPServer("3100");

		requestHandler.addCommand("createCRecord", new CreateCriminalRecord(
				spvm, spb, spl));

		requestHandler.addCommand("getRecordCounts", new CreateCriminalRecord(
				spvm, spb, spl));

		leader.setRequestHandler(requestHandler);
		leader.start();
		leader.startFailureDetection();
	}
}
