package edu.concordia.dpis;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class HeartbeatScheduler {

	public void start() {
		new ScheduledThreadPoolExecutor(1).schedule(new HeartBeatTask(), 5,
				TimeUnit.SECONDS);
	}

	class HeartBeatTask implements Runnable {
		@Override
		public void run() {
			for (Node node : getNodes()) {
				boolean isAlive = node.isAlive();
				if (!isAlive) {
					onFailedNode(node);
				}
			}
		}
	}

	protected abstract List<Node> getNodes();

	protected abstract void onFailedNode(Node node);

	protected abstract boolean isLeader(String id);

}