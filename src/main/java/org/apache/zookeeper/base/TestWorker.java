package org.apache.zookeeper.base;

import java.io.IOException;
import java.util.Random;

import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.book.Worker;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestWorker implements Watcher {

	private static final Logger LOG = LoggerFactory.getLogger(TestWorker.class);
	ZooKeeper zk;
	String hostPort;
	String serverId = Integer.toHexString((new Random()).nextInt());
	private String status;
	private String name;

	TestWorker(String hostPort) {
		this.hostPort = hostPort;
	}

	void startZK() throws IOException {
		zk = new ZooKeeper(hostPort, 15000, this);
	}

	public void process(WatchedEvent e) {
		LOG.info(e.toString() + ", " + hostPort);
	}

	void register() {
		name = "worker-" + serverId;
		zk.create("/workers/" + name, "Idle".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
				createWorkerCallback, null);
	}

	/**创建节点的回调函数**/
	StringCallback createWorkerCallback = new StringCallback() {
		public void processResult(int rc, String path, Object ctx, String name) {
			switch (Code.get(rc)) {
			case CONNECTIONLOSS:
				register();
				break;
			case OK:
				LOG.info("Registered successfully: " + serverId);
				break;
			case NODEEXISTS:
				LOG.warn("Already registered: " + serverId);
				break;
			default:
				LOG.error("Something went wrong: " + KeeperException.create(Code.get(rc), path));
			}
		}
	};

	/**当连接失败时，回调函数继续进行状态更新**/
	StatCallback statusUpdateCallback = new StatCallback() {
		public void processResult(int rc, String path, Object ctx, Stat stat) {
			switch (Code.get(rc)) {
			case CONNECTIONLOSS:
				updateStatus((String) ctx);
				return;
			}
		}
	};

	/**同步修改worker状态,即把对应worker下面某一节点的值设置为新的值**/
	synchronized private void updateStatus(String status) {
		if (status == this.status) {
			zk.setData("/workers/" + name, status.getBytes(), -1, statusUpdateCallback, status);
		}
	}

	/**设置worker状态**/
	public void setStatus(String status) {
		this.status = status;
		updateStatus(status);
	}

	public static void main(String args[]) throws Exception {
		Worker w = new Worker("192.168.130.101:2181");
		w.startZK();
		w.register();
		Thread.sleep(30000);
	}
}
