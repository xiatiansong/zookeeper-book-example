package org.apache.zookeeper.base;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;

public class TestListener {

	CuratorListener curatorListener = new CuratorListener() {
		@Override
		public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
			
		}
	};
	/** 
	 *  
	 * @param args void
	 * @author 夏天松 
	 * @date 2014-1-9 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
