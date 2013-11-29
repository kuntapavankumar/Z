package edu.concordia.dpis;

public class CreateCriminalRecord implements Command {

	private StationServer stationServer;

	public CreateCriminalRecord(StationServer stationServer) {
		this.stationServer = stationServer;
	}

	@Override
	public Object execute(Object... params) {
		return stationServer.createCRecord(params[0].toString(),
				params[1].toString(), params[2].toString(),
				params[3].toString(), params[4].toString());
	}

}
