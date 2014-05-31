package com.sunshine.concurrent;

public class Joining {

	/**
	 * 
	 * @param args
	 *            void
	 * @author 夏天松
	 * @date 2014-2-27
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Sleeper sleepy = new Sleeper("Sleepy----1", 1500), grumpy = new Sleeper("Grumpy-----2", 1500);
		Joiner dopey = new Joiner("Dopey----3", sleepy), doc = new Joiner("Doc----4", grumpy);
		grumpy.interrupt();
		doc.interrupt();
		dopey.getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), new RuntimeException());
	}
}

// Understanding join().
class Sleeper extends Thread {
	private int duration;

	public Sleeper(String name, int sleepTime) {
		super(name);
		duration = sleepTime;
		start();
	}

	public void run() {
		try {
			sleep(duration);
		} catch (InterruptedException e) {
			// System.out.println(getName() + " was interrupted. " +
			// "isInterrupted(): " + isInterrupted());
			System.out.println(getName() + " in catch Thread.interrupted(). " + "Thread.interrupted(): "
					+ Thread.interrupted());
			return;
		}
		System.out.println(getName() + " has awakened");
	}
}

class Joiner extends Thread {
	private Sleeper sleeper;

	public Joiner(String name, Sleeper sleeper) {
		super(name);
		this.sleeper = sleeper;
		start();
	}

	public void run() {
		try {
			sleeper.join();
		} catch (InterruptedException e) {
			// run方法不能Throw CheckedException,要抛只能抛出RuntimeException,也不会被主线程捕获
			// 要使主线程能够捕获这个RuntimeException请参见另外一篇文章
			// 地址:http://www.blogjava.net/fhtdy2004/archive/2009/08/07/290210.html
			throw new RuntimeException(e);
		}
		System.out.println(getName() + " join completed");
	}
}