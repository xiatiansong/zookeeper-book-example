package org.apache.zookeeper.base;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSetData implements Watcher {
	private static final Logger LOG = LoggerFactory.getLogger(TestSetData.class);
	private ZooKeeper zk;
	private CountDownLatch connectedSemaphore = new CountDownLatch( 1 ); 
	private String hostPort;

	public TestSetData(String hostPort) {
		this.hostPort = hostPort;
	}

	public void startZK() throws IOException {
		zk = new ZooKeeper(hostPort, 15000, this);
	}
	
	public void bootstrap() {
		createParent("/workers", new byte[0]);
		createParent("/assign", new byte[0]);
		createParent("/tasks", new byte[0]);
		createParent("/status", new byte[0]);
	}

	void createParent(String path, byte[] data) {
		zk.create(path, data, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT,
				createParentCallback, data);
	}

	StringCallback createParentCallback = new StringCallback() {
		public void processResult(int rc, String path, Object ctx, String name) {
			switch (Code.get(rc)) {
			case CONNECTIONLOSS:
				createParent(path, (byte[]) ctx);
				break;
			case OK:
				LOG.info("Parent created");
				break;
			case NODEEXISTS:
				LOG.warn("Parent already registered: " + path);
				break;
			default:
				LOG.error("Something went wrong: ",
						KeeperException.create(Code.get(rc), path));
			}
		}
	};

	public void stopZK() throws Exception {
		zk.close();
	}
	
	/**
	 * 
	 * @param args
	 *            void
	 * @author 夏天松
	 * @throws Exception 
	 * @date 2014-1-3
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		TestSetData m = new TestSetData("192.168.130.101:2181");
		m.startZK();
		m.connectedSemaphore.await();
		m.bootstrap();
		m.stopZK();
	}

	@Override
	public void process(WatchedEvent event) {
		System.out.println( "收到事件通知：" + event.getState() +"\n"  ); 
        if ( KeeperState.SyncConnected == event.getState() ) { 
            connectedSemaphore.countDown(); 
        } 
	}

}
