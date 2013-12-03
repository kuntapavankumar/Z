package edu.concordia.dpis;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import edu.concordia.dpis.commons.Address;
import edu.concordia.dpis.commons.DeadNodeException;
import edu.concordia.dpis.commons.Message;
import edu.concordia.dpis.commons.MessageTransformer;
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

	private MulticastListener multicastListener;

	public Replica(int port, int replicaId, Address frontEndAddress)
			throws UnknownHostException {
		this(port, false, replicaId, frontEndAddress);
		multicastListener = new MulticastListener(3000, "230.0.0.1") {
			@Override
			public Message onMessage(DatagramPacket pack) {
				ReliableMessage msg = null;
				ReliableMessage reply;
				try {
					msg = (ReliableMessage) MessageTransformer
							.deserializeMessage(pack.getData());
					msg.setMulticast(false);
					String str = getReplyMessage(msg);
					reply = new ReliableMessage("SUCCESS", msg.getToAddress()
							.getHost(), msg.getToAddress().getPort());
					reply.addArgument(str);
					reply.setReply(true);
					reply.setSequenceNumber(msg.getSequenceNumber());
					return reply;
				} catch (IOException e) {
					e.printStackTrace();
				}
				reply = new ReliableMessage("FAILURE", msg.getToAddress()
						.getHost(), msg.getToAddress().getPort());
				reply.setSequenceNumber(msg.getSequenceNumber());
				return reply;
			}
		};
		multicastListener.joinGroup();
	}

	public Replica(int port, boolean isLeader, int replicaId,
			Address frontEndAddress) throws UnknownHostException {
		super(port);
		this.address = new Address(InetAddress.getLocalHost().getHostAddress(),
				port);
		this.address.setId(replicaId + "");
		this.frontEndAddress = frontEndAddress;
		if (isLeader) {
			this.leaderName = replicaId + "";
			// now let the front end know that you are the leader
			notifyFrontEndTheNewLeader();
		}
		System.out
				.println("Replica initialized, start the replica by calling start() method.");
	}

	public void startFailureDetection() {
		new HeartbeatScheduler() {

			@Override
			protected boolean isLeader(String id) {
				System.out.println("isLeader[" + leaderName + "," + id + "]");
				System.out.println("isLeader:" + getLeaderName().equals(id));
				return getLeaderName().equals(id);
			}

			protected void onFailedNode(Node node) {
				System.out.println("Now the leader is " + getLeaderName());
				System.out.println("Node deployed on"
						+ node.getAddress().getHost() + " and on port"
						+ node.getAddress().getPort() + " is not responding");
				if (getLeaderName() == null
						|| isLeader(node.getAddress().getId())) {
					System.out.println("Replica " + address.getId()
							+ " found leader failure");
					election(address.getId());
				}
			};

			public List<Node> getNodes() {
				System.out.println("Heartbeatscheduler initiated by ["
						+ address.getHost() + "," + address.getPort() + "]");
				return nodes;
			};
		}.start();
	}

	@Override
	protected String getReplyMessage(Message request) {
		System.out.println("request received :" + request.getActualMessage());
		if ("election".equalsIgnoreCase(request.getActualMessage())) {
			return election((String) request.getArguments().get(0)).toString();
		} else if ("newLeader".equalsIgnoreCase(request.getActualMessage())) {
			newLeader((String) request.getArguments().get(0));
			return "SUCCESS";
		}
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
		System.out.println("Now, the leader is " + name);
		if (leaderName.equals(this.address.getId())) {
			if (!multicastListener.isClosed()) {
				multicastListener.leaveGroup();
			}
			notifyFrontEndTheNewLeader();
			for (final Node node : nodes) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							node.newLeader(name);
						} catch (DeadNodeException e) {
							System.out.println(e.getMessage());
						}
					}
				}).start();
			}
		} else {
			if (this.multicastListener.isClosed()) {
				this.multicastListener.joinGroup();
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
					Message replyMsg = new UDPClient().send(leaderMsg, 0);
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
		if (this.address.getId().compareTo(replicaId) >= 0) {
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
								if (node.getAddress().getId()
										.equals(leaderName)) {
									continue;
								}
								if (node.getAddress().getId()
										.compareTo(address.getId()) >= 0) {
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
											Thread.sleep((long) (5000 + (1000 * Math
													.random())));
										} catch (InterruptedException e) {
											// expect the leader is
											// available by this time
										}
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
