package edu.concordia.dpis;

import java.util.List;

import edu.concordia.dpis.stationserver.StationServerImpl;

public class EditRecord extends StationCommand {

	public EditRecord(StationServer spvm, StationServerImpl spb,
			StationServerImpl spl) {
		super(spvm, spb, spl);
	}

	@Override
	public Object execute(List<Object> params) {
		System.out.println("Executing Edit Record");
		StationServer stationserver = getStation(params.get(0).toString());

		if (stationserver == null) {
			return null;
		}
		return stationserver
				.editRecord(params.get(0).toString(), params.get(1).toString(),
						params.get(2).toString(), params.get(3).toString())
				+ "";
	}

}
