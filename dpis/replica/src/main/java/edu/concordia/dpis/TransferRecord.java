package edu.concordia.dpis;

import java.util.List;

import edu.concordia.dpis.stationserver.StationServerImpl;

public class TransferRecord extends StationCommand {

	public TransferRecord(StationServer spvm, StationServerImpl spb,
			StationServerImpl spl) {
		// this.stationServer = stationServer;
		super(spvm, spb, spl);
	}

	@Override
	public Object execute(List<Object> params) {
		System.out.println("Executing Transfer Record");
		StationServer stationserver = getStation(params.get(0).toString());

		if (stationserver == null) {
			return null;
		}
		return stationserver.transferRecord(params.get(0).toString(), params
				.get(1).toString(), params.get(2).toString())
				+ "";
	}

}
