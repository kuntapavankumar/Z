package edu.concordia.dpis;

import java.util.List;
import edu.concordia.dpis.stationserver.StationServerImpl;

public class CreateMissingRecord extends StationCommand {

	public CreateMissingRecord(StationServer spvm, StationServerImpl spb,
			StationServerImpl spl) {
		// this.stationServer = stationServer;
		super(spvm, spb, spl);
	}

	@Override
	public Object execute(List<Object> params) {
		System.out.println("Executing create Missing Record");
		StationServer stationserver = getStation(params.get(0).toString());

		if (stationserver == null) {
			return null;
		}
		return stationserver.createMRecord(params.get(0).toString(), params
				.get(1).toString(), params.get(2).toString(), params.get(3)
				.toString(), params.get(4).toString(),
				params.get(5).toString(), params.get(6).toString())
				+ "";
	}

}
