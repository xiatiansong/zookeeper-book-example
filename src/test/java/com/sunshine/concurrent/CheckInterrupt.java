package com.sunshine.concurrent;

import java.util.Timer;
import java.util.TimerTask;

public class CheckInterrupt {

	public static void main(String[] args) {
        final CanStop stoppable = new CanStop();
        stoppable.start();
        new Timer(true).schedule(new TimerTask() {
            public void run() {
                System.out.println("Requesting Interrupt");
                stoppable.interrupt();//不会中断正在执行的线程,原因是因为interrupt()方法只设置中断状态标志位为true
                System.out.println("in timer stoppable.isInterrupted() -------3"+stoppable.isInterrupted());
            }
        }, 500); // run() after 500 milliseconds
        
        System.out.println("-------------结束-------------");
    }
}

class CanStop extends Thread {
	private int counter = 0;

	public void run() {
		boolean done = false;
		try {
			Thread.sleep(100);// 设置成100比主线程中的500要小
		} catch (InterruptedException ie) {
			ie.printStackTrace();
			// return;假如要使用interrupt来终止线程则在捕获的InterruptedException中return
		}
		while (counter < 100000 && !done) {
			System.out.println(counter++);
			// 在主线程中调用stoppable.interrupt()之前为false,假如之后没有调用Thread.interrupted()则一直为true,
			// 否则为第一次为true,调用Thread.interrupted之后为false
			System.out.println("in thread stoppable.isInterrupted()----1 " + isInterrupted());
			//在主线程中调用stoppable.interrupt()之前为false,之后只有第一个会显示为true,之后全为false
			// System.out.println("stoppable.isInterrupted() "+Thread.interrupted());//
			// 调用Thread.interrupted()一次会清除线程的中断标志位,因此以后都为false
			if (Thread.interrupted() == true) {
				try {
					// Thread.interrupted()会清除中断标志位,显然这里面只会调用一次
					System.out.println("in thread after Thread.interrupted()-----2 " + isInterrupted());
					sleep(10000);
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
			}
		}
	}
}