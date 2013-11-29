package edu.concordia.dpis;

import java.util.HashMap;
import java.util.List;

public class StationServerRequestHandler implements RequestHandler {

	private HashMap<String, Command> commands = new HashMap<String, Command>();

	@Override
	public String getOperationName(String message) {
		return null;
	}

	@Override
	public List<Object> getArguments(String message) {
		return null;
	}

	@Override
	public Object doOperation(String requestMessage) {
		final Command command = commands.get(getOperationName(requestMessage));
		return command.execute(getArguments(requestMessage));
	}

	public void addCommand(String operationName, Command command) {
		this.commands.put(operationName, command);
	}
}
