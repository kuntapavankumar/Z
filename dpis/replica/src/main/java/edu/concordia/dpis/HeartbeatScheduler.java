package edu.concordia.dpis;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import edu.concordia.dpis.commons.DeadNodeException;

public class HeartbeatScheduler extends ScheduledThreadPoolExecutor {

	private List<Node> nodes;

	public HeartbeatScheduler(List<Node> nodes) {
		super(1);
		this.nodes = nodes;
	}

	public void start() {
		this.schedule(new HeartBeatTask(), 5, TimeUnit.SECONDS);
	}

	class HeartBeatTask implements Runnable {
		@Override
		public void run() {
			// check for heart beat of other nodes,
			
			try {
				noResponse(null);
			} catch (DeadNodeException e) {
				e.printStackTrace();
			}
		}
	}

	void noResponse(Node node) throws DeadNodeException {
		if (isLeader(node)) {
			startElection();
		}
	}

	private void startElection() throws DeadNodeException {
		for (Node node : nodes) {
			MessageType message = node.election("");
		}
	}

	private boolean isLeader(Node node) {
		return false;
	}
}