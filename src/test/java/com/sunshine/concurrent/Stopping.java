package com.sunshine.concurrent;

import java.util.Timer;
import java.util.TimerTask;

public class Stopping {

	/**
	 * 
	 * @param args
	 *            void
	 * @author 夏天松
	 * @date 2014-2-27
	 */
	public static void main(String[] args) {
		final CanStop_2 stoppable = new CanStop_2();
		stoppable.start();
		new Timer(true).schedule(new TimerTask() {
			public void run() {
				System.out.println("Requesting stop");
				stoppable.requestStop();
			}
		}, 500); // run() after 500 milliseconds
	}

}

class CanStop_2 extends Thread {
	// Must be volatile:
	private volatile boolean stop = false;

	private int counter = 0;

	public void run() {
		while (!stop && counter < 100000) {
			System.out.println(counter++);
		}
		if (stop)
			System.out.println("Detected stop");
	}

	public void requestStop() {
		stop = true;
	}
}