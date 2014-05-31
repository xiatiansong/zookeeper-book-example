package com.sunshine.concurrent;

public class MyWaitNotify {
	
	static MonitorObject myMonitorObject = new MonitorObject();

	public void doWait(){
	    synchronized(myMonitorObject){
	      try{
	        myMonitorObject.wait();
	        System.out.println("---------doWait()--------------" + this.toString());
	      } catch(InterruptedException e){}
	    }
	  }

	public void doNotify() {
		synchronized (myMonitorObject) {
			myMonitorObject.notify();
			System.out.println("---------doNotify()--------------" + this.toString());
		}
	}
}