package edu.concordia.dpis;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import edu.concordia.dpis.commons.Address;
import edu.concordia.dpis.commons.DeadNodeException;
import edu.concordia.dpis.messenger.UDPServer;

public class Replica extends UDPServer implements Node {

	private RequestHandler requestHandler;

	private Address address;

	private String leaderName;

	private List<Node> nodes = new ArrayList<Node>();

	public Replica(int port) throws UnknownHostException {
		super(port);
		this.address = new Address(InetAddress.getLocalHost().getHostAddress(),
				port);
		this.address.setId(System.currentTimeMillis() + "");
		new HeartbeatScheduler(nodes) {

			@Override
			protected boolean isLeader(String id) {
				return id.equals(getLeaderName());
			}

			protected void onFailedNode(Node node) {
				if (isLeader(node.getAddress().getId())) {
					election(address.getId());
				}
			};
		}.start();
	}

	@Override
	protected String getReplyMessage(DatagramPacket request) {
		return requestHandler.doOperation(request).toString();
	}

	@Override
	public String getLeaderName() {
		return leaderName;
	}

	@Override
	public void newLeader(final String name) {
		this.leaderName = name;
		if (leaderName.equals(this.address.getId())) {
			for (final Node node : nodes) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							node.newLeader(name);
						} catch (DeadNodeException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		}
	}

	@Override
	public MessageType election(String replicaId) {
		leaderName = null;
		if (this.address.getId().compareTo(replicaId) > 0) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					for (final Node node : nodes) {
						if (leaderName == null) {
							try {
								MessageType mType = node.election(address
										.getId());
								if (MessageType.COORDINATOR.equals(mType)) {
									newLeader(node.getAddress().getId());
								} else if (MessageType.OK.equals(mType)) {
									try {
										Thread.sleep(2000);
									} catch (InterruptedException e) {
										// expect the leader is
										// available by this time
									}
								}
							} catch (DeadNodeException e) {
								e.printStackTrace();
							}
						}
					}
					if (leaderName == null) {
						newLeader(address.getId());
					}
				}
			}).start();
		} else {
			// this shouldn't happen
		}
		return MessageType.OK;
	}

	@Override
	public Address getAddress() {
		return this.address;
	}

	@Override
	public boolean isAlive() {
		return true;
	}

}
