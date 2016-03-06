package saccubus.net;

import java.util.concurrent.atomic.AtomicInteger;

import saccubus.HistoryDeque;

public class Gate extends Thread {
	private final static AtomicInteger numGate = new AtomicInteger(2);
	private final static AtomicInteger numRun  = new AtomicInteger(0);
	private final static HistoryDeque<Gate> que = new HistoryDeque<Gate>(null);
	private boolean entered = false;
	private int limitter;
	private int count;

	public Gate(){
		entered = true;
		count = 0;
		limitter = 3;		//retry max
	}

	public static Gate enter(){
		Gate g = new Gate();
		g.reEnter();
		return g;
	}
	public void reEnter(){
		entered = true;
		synchronized(que){
			numRun.incrementAndGet();
			if(numRun.get() <= numGate.get()){
				System.out.println("Gate entered: nRun="+numRun.get()+",nReq="+que.size()+",nGate="+numGate.get());
				return;
			}
			//ゲート待ち
			que.offer(this);
			numRun.decrementAndGet();
			System.out.println("Gate waiting: nRun="+numRun.get()+",nReq="+que.size()+",nGate="+numGate.get());
			while(numRun.get() >= numGate.get() || que.peek()!=this){
				try {
					que.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(que.peek()!=this)
					que.notify();	//必要か？
			}
			// que先頭が自分, nRun < nGate
			que.poll();
			numRun.incrementAndGet();
		}
		//ウェイト抜け
		System.out.println("Gate entered after wait");
		return;
	}

	public void exit(){
		if(entered){
			entered = false;
			numRun.decrementAndGet();
			while(numRun.get() < numGate.get()){
				synchronized (que) {
					if(que.size()==0)
						break;
					//他の待ちをリリース
					//	if(que.peek() == null){
					//		System.out.println("Gate que return null, バグ?");
					//		return;
								//	}
					que.notify();
				}
			}
			// Req <=0 または　nRun>=nGate
			System.out.println("Gate exited: nRun="+numRun.get()+",nReq="+que.size()+",nGate="+numGate.get());
		}
	}

	public boolean notExceedLimiterGate() {
		if(!entered)
			return false;

		exit();
		// ロック開放
		try {	//5秒待機
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		reEnter();
		if(count++ > limitter){	//retry数超えたら false
			count = 0;
			return false;
		}
		return true;
	}

	public static void setNumGate(int nGate){
		if(nGate <0)
			nGate = 0;
		if(nGate > 2)
			nGate = 2;
		numGate.addAndGet(nGate - numGate.get());
		while(numRun.get() < numGate.get()){
			synchronized(que){
				if(que.size()==0)
					break;
			//	if(que.peek()==null){
			//		System.out.println("Gate que return null");
			//	}
				que.notify();
			}
		}
	}

	public static int getNumRun() {
		return numRun.get();
	}
}
