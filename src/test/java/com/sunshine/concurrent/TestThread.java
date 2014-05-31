package com.sunshine.concurrent;

public class TestThread implements Runnable {
	
	private volatile boolean isRunning = true;
	
	@Override
	public void run() {
		/*while(!Thread.currentThread().isInterrupted()){
			System.out.println("--------------------------------------------" + Thread.currentThread().getName());
		}*/
		while(isRunning){
			System.out.println("--------------------------------------------" + Thread.currentThread().getName());
		}
	}
	 
}
