package org.apache.zookeeper.base;

import java.io.IOException;
import java.util.Random;

import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class TestStringCallBack implements Watcher {
	private ZooKeeper zk;
	private String hostPort;
	boolean isLeader = false;
	String serverId = Integer.toString(new Random().nextInt());

	StringCallback masterCreateCallback = new StringCallback() {
		@Override
		public void processResult(int rc, String path, Object ctx, String name) {
 			System.out.println("[回调函数传递对象：masterCreateCallback-]"+ctx);
			switch (Code.get(rc)) {
			case CONNECTIONLOSS:
				checkMaster();
				return;
			case OK:
				isLeader = true;
				break;
			default:
				isLeader = false;
			}
			System.out.println("I'm " + (isLeader ? "" : "not ") + "the leader");
		}
	};

	DataCallback masterCheckCallback = new DataCallback() {
		@Override
		public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
			System.out.println("[回调函数传递对象]"+ctx);
			switch (Code.get(rc)) {
			case CONNECTIONLOSS:
				checkMaster();
				return;
			case NONODE:
				runForMaster();
				return;
			}
		}
	};

	public TestStringCallBack(String hostPort) {
		this.hostPort = hostPort;
	}

	public void startZK() throws IOException {
		zk = new ZooKeeper(hostPort, 15000, this);
	}

	public void process(WatchedEvent e) {
		System.out.println(e);
	}

	public void checkMaster() {
		String test = "master path";
		zk.getData("/master", false, masterCheckCallback, test);
	}

	public void runForMaster() {
		String str = "/master/config/path";
		zk.create("/master", serverId.getBytes(), Ids.OPEN_ACL_UNSAFE,
				CreateMode.EPHEMERAL, masterCreateCallback, str);
	}

	public void stopZK() throws Exception {
		zk.close();
	}

	/**
	 * 
	 * @author 夏天松
	 * @date 2014-1-2
	 */
	public static void main(String[] args) throws Exception {
		TestStringCallBack m = new TestStringCallBack("192.168.130.101:2181");
		m.startZK();
		m.runForMaster();
		if (m.isLeader) {
			System.out.println("I'm the leader");
			Thread.sleep(60000);
		} else {
			System.out.println("Someone else is the leader");
		}
		m.stopZK();
	}
}