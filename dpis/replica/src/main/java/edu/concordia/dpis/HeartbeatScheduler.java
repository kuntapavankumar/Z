package edu.concordia.dpis;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class HeartbeatScheduler {

	public void start() {
		new ScheduledThreadPoolExecutor(1).scheduleWithFixedDelay(
				new HeartBeatTask(), 10, 30, TimeUnit.SECONDS);
		System.out.println("HeartbeatScheduler is up and running");
	}

	class HeartBeatTask implements Runnable {
		@Override
		public void run() {
			List<Node> nodes = getNodes();
			for (Node node : nodes) {
				boolean isAlive = node.isAlive();
				if (!isAlive) {
					onFailedNode(node);
				} else {
					System.out.println("Node [" + node.getAddress().getHost()
							+ "," + node.getAddress().getPort() + "] is alive");
				}
			}
		}
	}

	protected abstract List<Node> getNodes();

	protected abstract void onFailedNode(Node node);

	protected abstract boolean isLeader(String id);

}