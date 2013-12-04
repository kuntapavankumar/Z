package edu.concordia.dpis;

import java.util.List;

import edu.concordia.dpis.stationserver.StationServerImpl;

public class GetRecordCounts extends StationCommand {

	public GetRecordCounts(StationServer spvm, StationServerImpl spb,
			StationServerImpl spl) {
		super(spvm, spb, spl);
	}

	@Override
	public Object execute(List<Object> params) {
		System.out.println("executing getRecordCounts");
		StationServer stationServer = getStation(params.get(0).toString());
		if (stationServer == null) {
			return null;
		}
		return stationServer.getRecordCounts((String) params.get(0));
	}
}
