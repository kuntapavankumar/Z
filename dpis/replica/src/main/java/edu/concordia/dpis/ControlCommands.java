package edu.concordia.dpis;

import java.util.HashMap;

public class ControlCommands {

	public static HashMap<String, Command> getCommands(
			StationServer stationServer) {
		HashMap<String, Command> commands = new HashMap<String, Command>();
		commands.put("isAlive", new CreateCriminalRecord(stationServer));
		return commands;
	}
}
