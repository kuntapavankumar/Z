package edu.concordia.dpis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.concordia.dpis.commons.Message;
import edu.concordia.dpis.commons.ReliableMessage;
import edu.concordia.dpis.fifo.FIFO;

public class DefaultRequestHandler implements RequestHandler {

	private HashMap<String, Command> commands = new HashMap<String, Command>();

	private HashMap<Integer, List<ReliableMessage>> replies = new HashMap<Integer, List<ReliableMessage>>();

	@Override
	public String getOperationName(Message request) {
		return request.getActualMessage();
	}

	@Override
	public List<Object> getArguments(Message request) {
		return request.getArguments();
	}

	@Override
	public Object doOperation(final Message msg) {
		ReliableMessage rMsg = (ReliableMessage) msg;
		Object thisReturn = null;
		if (msg.isMulticast()) {
			System.out.println("message " + rMsg.getSequenceNumber()
					+ " must be multicasted");
			try {
				if (replies.get(rMsg.getSequenceNumber()) == null) {
					List<ReliableMessage> r = new ArrayList<ReliableMessage>();
					replies.put(rMsg.getSequenceNumber(), r);
				}
				FIFO.INSTANCE.multicast(msg);
				final Command command = commands.get(getOperationName(msg));
				if (command == null) {
					throw new UnsupportedOperationException();
				}
				thisReturn = command.execute(msg.getArguments());
				System.out
						.println("giving some time for others to reply back to the leader.");
				Thread.sleep(5000);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			ReliableMessage thisResultMsg = new ReliableMessage("SUCCESS", msg
					.getToAddress().getHost(), msg.getToAddress().getPort());
			thisResultMsg.addArgument(thisReturn);
			replies.get(rMsg.getSequenceNumber()).add(thisResultMsg);
			Object response = getAResponse(replies
					.get(rMsg.getSequenceNumber()));
			System.out.println("replying to message "
					+ rMsg.getSequenceNumber() + response);
			return response;
		}
		if (msg.isReply()) {
			System.out.println("got a reply for sequencenumber:"
					+ rMsg.getSequenceNumber());
			replies.get(rMsg.getSequenceNumber()).add(rMsg);
		} else {
			final Command command = commands.get(getOperationName(msg));
			return command.execute(msg.getArguments());
		}
		return "OK";
	}

	private Object getAResponse(List<ReliableMessage> list) {
		HashMap<Object, Integer> results = new HashMap<Object, Integer>();
		for (Message msg : list) {
			if ("SUCCESS".equalsIgnoreCase(msg.getActualMessage())) {
				Object result = msg.getArguments().get(0);
				if (results.get(result) != null) {
					int i = results.get(result);
					results.put(result, ++i);
				} else {
					results.put(result, 1);
				}
			}
		}
		Object result = null;
		int max = 0;
		for (Object obj : results.keySet()) {
			if (results.get(obj) > max) {
				max = results.get(obj);
				result = obj;
			}
		}
		return result;
	}

	public void addCommand(String operationName, Command command) {
		this.commands.put(operationName, command);
	}
}
