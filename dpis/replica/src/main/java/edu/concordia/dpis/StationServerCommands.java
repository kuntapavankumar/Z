package edu.concordia.dpis;

import java.util.HashMap;

public class StationServerCommands {

	public static HashMap<String, Command> getCommands(
			StationServer stationServer) {
		HashMap<String, Command> commands = new HashMap<String, Command>();
		commands.put("createCRecord", new CreateCriminalRecord(
				stationServer));
		return commands;
	}

}
