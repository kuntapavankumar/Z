package edu.concordia.dpis;

import java.util.List;

public class CreateCriminalRecord extends StationCommand {

	public CreateCriminalRecord(StationServer spvm, StationServer spb,
			StationServer spl) {
		super(spvm, spb, spl);
	}

	@Override
	public Object execute(List<Object> params) {
		System.out.println("executing create criminal record");
		StationServer stationServer = getStation(params.get(0).toString());
		if (stationServer == null) {
			return null;
		}
		return stationServer.createCRecord(params.get(0).toString(), params
				.get(1).toString(), params.get(2).toString(), params.get(3)
				.toString(), params.get(4).toString())
				+ "";
	}

}
