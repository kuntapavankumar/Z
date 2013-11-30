package edu.concordia.dpis;

import java.util.HashMap;
import java.util.List;

import edu.concordia.dpis.commons.Message;

public class DefaultRequestHandler implements RequestHandler {

	private HashMap<String, Command> commands = new HashMap<String, Command>();

	@Override
	public String getOperationName(Message request) {
		return request.getActualMessage();
	}

	@Override
	public List<Object> getArguments(Message request) {
		return request.getArguments();
	}

	@Override
	public Object doOperation(Message request) {
		final Command command = commands.get(getOperationName(request));
		return command.execute(getArguments(request));
	}

	public void addCommand(String operationName, Command command) {
		this.commands.put(operationName, command);
	}
}
