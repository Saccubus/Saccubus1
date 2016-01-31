package saccubus.net;

import java.util.concurrent.atomic.AtomicInteger;

public class Gate extends Thread {
	private static int numThread = 2;
	private static AtomicInteger lock = new AtomicInteger(numThread);
	private boolean entered = false;

	public Gate(){
		entered = true;
	}

	public static Gate enter(){
		synchronized(lock){
			int n = lock.decrementAndGet();
			if (n < 0){
				try {
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return new Gate();
	}

	public void exit(){
		if(entered){
			synchronized (lock) {
				int n = lock.getAndIncrement();
				if(n < 0){
					lock.notify();
				}
				entered = false;
			}
		}
	}
}
