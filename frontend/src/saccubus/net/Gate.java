package saccubus.net;

import java.util.concurrent.atomic.AtomicInteger;

import saccubus.HistoryDeque;

public class Gate extends Thread {
	private static AtomicInteger numGate = new AtomicInteger(2);
	private static AtomicInteger numRun  = new AtomicInteger(0);
	private static HistoryDeque<Gate> que = new HistoryDeque<Gate>(null);
	private static AtomicInteger numReq = new AtomicInteger(0);
	private boolean entered = false;
	private int limitter;
	private int count;

	public Gate(){
		entered = true;
		count = 0;
		limitter = 3;		//retry max
	}

	public static Gate enter(){
		int nRun = 0;
		int nGate = 2;
		int nReq;
		Gate g = new Gate();
		synchronized(que){
			nRun = numRun.incrementAndGet();
			nGate = numGate.get();
			if(nRun <= nGate){
				System.out.println("Gate entered: nRun="+nRun+",nGate="+nGate);
				return g;
			}
			//ゲート待ち
			que.offer(g);
			nReq = numReq.incrementAndGet();
			numRun.decrementAndGet();
			System.out.println("Gate waiting: nRun="+nRun+",nReq="+nReq+",nGate="+nGate);
		}
		synchronized(g){
			try {
				g.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//ウェイト抜け
		System.out.println("Gate entered after wait");
		return g;
	}

	public void exit(){
		Gate rg = null;
		int nRun;
		int nGate;
		int nReq;
		if(entered){
			entered = false;
			synchronized (que) {
				nRun = numRun.decrementAndGet();
				nGate = numGate.get();
				nReq = numReq.get();
				if(nRun < nGate && nReq>0){
					//他の待ちをリリース
					rg = que.poll();
					if(rg == null)
						System.out.println("Gate que return null, バグ?");
					else {
						numReq.decrementAndGet();
						numRun.incrementAndGet();
						synchronized (rg) {
							rg.notify();
						}
					}
				}
			}
			System.out.println("Gate exited: nRun="+nRun+",nReq="+nReq+",nGate="+nGate);
		}
	}

	public boolean notExceedLimiterGate() {
		if(!entered)
			return false;

		exit();
		// ロック開放
		if(count++ > limitter){	//retry数超えたら false
			count = 0;
			exit();
			return false;
		}
		try {	//5秒待機
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		enter();
		entered = true;
		return true;
	}

	public static void setNumGate(int n){
		if(n <0)
			n = 0;
		if(n > 2)
			n = 2;
		int nGate = numGate.get();
		int delta = n - nGate;
		while(delta < 0){
			nGate = numGate.decrementAndGet();
			delta++;
		}
		while (delta > 0){
			nGate = numGate.incrementAndGet();
			delta--;
		}
		int nReq;
		int nRun;
		Gate g = null;
		synchronized(que){
			nReq = numReq.get();
			nRun = numRun.get();
			if(nRun < nGate && nReq>0){
				g = que.poll();
			}
		}
		while(g!=null){
			synchronized(g){
				g.notify();
			}
			g = null;
			synchronized(que){
				nReq = numReq.decrementAndGet();
				nRun = numRun.incrementAndGet();
				if(nRun < nGate && nReq>0){
					g = que.poll();
				}
			}
		}
	}

	public static void downloadDown(boolean selected) {
	}
}
