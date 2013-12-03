package edu.concordia.dpis;

import java.util.List;

public class CreateCriminalRecord implements Command {

	private StationServer stationServer;

	public CreateCriminalRecord(StationServer stationServer) {
		this.stationServer = stationServer;
	}

	@Override
	public Object execute(List<Object> params) {
		System.out.println("executing create criminal record");
		return stationServer.createCRecord(params.get(0).toString(), params
				.get(1).toString(), params.get(2).toString(), params.get(3)
				.toString(), params.get(4).toString())
				+ "";
	}

}
