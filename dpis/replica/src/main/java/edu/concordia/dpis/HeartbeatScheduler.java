package edu.concordia.dpis;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HeartbeatScheduler extends ScheduledThreadPoolExecutor {

	private List<Node> nodes;

	public HeartbeatScheduler(List<Node> nodes) {
		super(1);
		this.nodes = nodes;
	}

	public void start() {
		this.schedule(new HeartBeatTask(), 1, TimeUnit.SECONDS);
	}

	class HeartBeatTask implements Runnable {

		@Override
		public void run() {
			// check for heartbeat of other nodes,
			noResponse(null);
		}
	}

	void noResponse(Node node) {
		if (isLeader(node)) {
			startElection();
		}
	}

	private void startElection() {
		for (Node node : nodes) {
			MessageType message = node.election("");
		}
	}

	private boolean isLeader(Node node) {
		return false;
	}
}
