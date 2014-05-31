package com.sunshine.concurrent;

public class TestWait extends Thread{
	
	private MyWaitNotify instance = null;
	
	public TestWait(){
		instance = new MyWaitNotify();
	}
	
	/** 
	 *  
	 * @param args void
	 * @author 夏天松 
	 * @throws InterruptedException 
	 * @date 2014-2-13 
	 */
	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		/*TestWait m1 = new TestWait();
		TestWait m2 = new TestWait();
		TestWait m3 = new TestWait();
		m1.start();
		m2.start();
		m3.start();
		
		m1.doNotify();
		
		m1.wait();*/
		TestThread test1 = new TestThread();
		Thread t1 = new Thread(test1);
		Thread t2 = new Thread(test1);
		t1.start();
		t2.start();
		Thread.currentThread().sleep(5000);
		t1.interrupt();
		//t2.interrupt();
	}

	@Override
	public void run() {
		instance.doWait();
	}
	
	public void doNotify(){
		instance.doNotify();
	}
}