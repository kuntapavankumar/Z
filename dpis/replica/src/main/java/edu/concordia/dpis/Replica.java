package edu.concordia.dpis;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import edu.concordia.dpis.commons.Address;
import edu.concordia.dpis.commons.DeadNodeException;
import edu.concordia.dpis.commons.Message;
import edu.concordia.dpis.commons.ReliableMessage;
import edu.concordia.dpis.commons.TimeoutException;
import edu.concordia.dpis.messenger.UDPClient;
import edu.concordia.dpis.messenger.UDPServer;

/**
 * A Replica is a Distributed {@link Node}, being a UDP Server can reply to the
 * requests delegating the operation to the actual Implementation with the help
 * of a request handler. A Replica periodically checks for the aliveness of the
 * other nodes it is supposed to know, if it detects a node failure and it being
 * a leader an election would be started immediately notifying every other node.
 * 
 * @see Node
 * @see UDPServer
 * @since 1.0
 * @author Pavan, Aliasgar
 * 
 */
public class Replica extends UDPServer implements Node, FrontEndAware {

	private RequestHandler requestHandler;

	private Address address;

	private String leaderName;

	private List<Node> nodes = new ArrayList<Node>();

	private Address frontEndAddress;

	public Replica(int port, int replicaId) throws UnknownHostException {
		this(port, false, replicaId, null);
	}

	public Replica(int port, boolean isLeader, int replicaId,
			Address frontEndAddress) throws UnknownHostException {
		super(port);
		this.address = new Address(InetAddress.getLocalHost().getHostAddress(),
				port);
		this.address.setId(replicaId + " ");
		this.leaderName = replicaId + "";
		this.frontEndAddress = frontEndAddress;
		// now let the front end know that you are the leader
		notifyFrontEndTheNewLeader();
		System.out
				.println("Replica initialized, start the replica by calling start() method.");
	}

	public void startFailureDetection() {
		new HeartbeatScheduler() {

			@Override
			protected boolean isLeader(String id) {
				return getLeaderName().equals(id);
			}

			protected void onFailedNode(Node node) {
				System.out.println("Node deployed on"
						+ node.getAddress().getHost() + " and on port"
						+ node.getAddress().getPort() + " is not responding");
				if (isLeader(node.getAddress().getId())) {
					System.out.println("Replica " + address.getId()
							+ " found leader failure");
					election(address.getId());
				}
			};

			public List<Node> getNodes() {
				System.out
						.println("getNodes called nodes size:" + nodes.size());
				return nodes;
			};
		}.start();
	}

	@Override
	protected String getReplyMessage(Message request) {
		return requestHandler.doOperation(request).toString();
	}

	@Override
	public String getLeaderName() {
		return leaderName;
	}

	@Override
	/**
	 * if this replica is declared as the leader, then it is this replica's 
	 * responsibility to let other nodes know about it being the new leader 
	 * in effect immediately.
	 */
	public void newLeader(final String name) {
		this.leaderName = name;
		if (leaderName.equals(this.address.getId())) {
			notifyFrontEndTheNewLeader();
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

	private void notifyFrontEndTheNewLeader() {
		if (frontEndAddress != null) {
			ReliableMessage leaderMsg = new ReliableMessage("leaderInfo",
					frontEndAddress.getHost(), frontEndAddress.getPort());
			leaderMsg.addArgument(this.address);
			int attempts = 0;
			while (attempts < 3) {
				try {
					Message replyMsg = new UDPClient().send(leaderMsg, 5000);
					if ("OK".equals(replyMsg.getActualMessage()))
						break;
				} catch (TimeoutException e1) {
					e1.printStackTrace();
				}
				attempts++;
			}
		} else {
			System.out
					.println("Front end Info not available to tell about the new leader");
		}
	}

	@Override
	public MessageType election(String replicaId) {
		System.out.println("election is going to be started by " + replicaId);
		leaderName = null;
		if (this.address.getId().compareTo(replicaId) > 0) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					for (final Node node : nodes) {
						if (leaderName == null) {
							try {
								System.out
										.println("sent election message to node deployed on "
												+ node.getAddress().getHost()
												+ " and on port "
												+ node.getAddress().getPort());
								MessageType mType = node.election(address
										.getId());
								if (MessageType.COORDINATOR.equals(mType)) {
									System.out
											.println("node deployed on "
													+ node.getAddress()
															.getHost()
													+ " and on port "
													+ node.getAddress()
															.getPort()
													+ " replied with a COORDINATOR message");
									newLeader(node.getAddress().getId());
								} else if (MessageType.OK.equals(mType)) {
									System.out.println("node deployed on "
											+ node.getAddress().getHost()
											+ " and on port"
											+ node.getAddress().getPort()
											+ " replied with a OK message");
									try {
										System.out
												.println("will wait for some time to let some one inform me about the new leader");
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
						System.out.println("no one responded to election");
						System.out.println("electing myself as the new leader");
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

	public void setRequestHandler(RequestHandler requestHandler) {
		this.requestHandler = requestHandler;
	}

	@Override
	public void setFrontEndAddress(Address address) {
		this.frontEndAddress = address;
	}

	public Replica addNode(Node node) {
		this.nodes.add(node);
		return this;
	}
}
