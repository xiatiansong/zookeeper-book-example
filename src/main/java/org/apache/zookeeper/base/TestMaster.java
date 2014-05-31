package org.apache.zookeeper.base;

import java.io.IOException;
import java.util.Random;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.ConnectionLossException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class TestMaster implements Watcher {
	private ZooKeeper zk;
	private String hostPort;

	public TestMaster(String hostPort) {
		this.hostPort = hostPort;
	}

	public void startZK() throws IOException {
		zk = new ZooKeeper(hostPort, 15000, this);
	}

	public void process(WatchedEvent e) {
		System.out.println(e);
	}

	String serverId = Integer.toString(new Random().nextInt());
	boolean isLeader = false;

	// returns true if there is a master
	public boolean checkMaster() throws KeeperException, InterruptedException {
		while (true) {
			try {
				Stat stat = new Stat();
				byte data[] = zk.getData("/master", false, stat);
				isLeader = new String(data).equals(serverId);
				return true;
			} catch (NoNodeException e) {
				// no master, so try create again
				return false;
			} catch (ConnectionLossException e) {
			}
		}
	}

	public void runForMaster() throws InterruptedException, KeeperException {
		while (true) {
			try {
				zk.create("/master", serverId.getBytes(), Ids.OPEN_ACL_UNSAFE,
						CreateMode.EPHEMERAL);
				isLeader = true;
				break;
			} catch (NodeExistsException e) {
				isLeader = false;
				break;
			} catch (ConnectionLossException e) {
			}
			if (checkMaster())
				break;
		}
	}

	public void stopZK() throws Exception {
		zk.close();
	}

	/**
	 * 
	 * @param args
	 *            void
	 * @author 夏天松
	 * @throws Exception 
	 * @date 2014-1-2
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		TestMaster m = new TestMaster("192.168.130.101:2181");
		m.startZK();
		m.runForMaster();
		if (m.isLeader) {
			System.out.println("I'm the leader");
			// wait for a bit
			Thread.sleep(60000);
		} else {
			System.out.println("Someone else is the leader");
		}
		m.stopZK();
	}
}