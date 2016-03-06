package saccubus.net;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Gate extends Thread {
	private final static AtomicInteger numRun  = new AtomicInteger(0);
	private final static AtomicInteger numGate = new AtomicInteger(2);
	private final static AtomicInteger numReq = new AtomicInteger(0);
	private boolean entered = false;
	private int limitter;
	private int count;
	private Integer ticket;
	private static Integer pool = null;
	private final static LinkedBlockingQueue<Integer> que = new LinkedBlockingQueue<Integer>();
	static {
		netInit(numGate.get());
	}
	private final static AtomicBoolean waitSetNumGate = new AtomicBoolean(false);

	public Gate(){
		entered = true;
		count = 0;
		limitter = 3;		//retry max
		ticket = null;
	}

	public static void init(){
		netInit(numGate.get());
	}

	public static void netInit(int n){
		que.clear();
		for(int i = 0; i < n; i++ ){
			que.offer(i);
		}
		System.out.println("Gate#netInit() n="+n+" numGate="+numGate.get());
	}

	public static Gate enter(){
		Gate g = new Gate();
		g.reEnter();
		return g;
	}
	public void reEnter(){
		entered = true;
		numReq.incrementAndGet();
		while(ticket==null){
			try {
				ticket = que.take();
				if(waitSetNumGate.get() || numRun.get()>numGate.get()){
					Thread.sleep(100);
					que.put(ticket);
					ticket = null;
					continue;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		numReq.decrementAndGet();
		numRun.incrementAndGet();
		System.out.println("Gate#enter() nRun="+numRun.get()+",nReq="+numReq.get()+",nGate="+numGate.get());
		return;
	}

	public void exit(){
		if(entered){
			entered = false;
			try {
				numRun.decrementAndGet();
				System.out.println("Gate#exit()  nRun="+numRun.get()+",nReq="+numReq.get()+",nGate="+numGate.get());
				que.put(ticket);
			} catch (InterruptedException e) {
				System.out.println("Gate#exit() Exception:  nRun="+numRun.get()+",nReq="+numReq.get()+",nGate="+numGate.get());
				e.printStackTrace();
			}
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

	public synchronized static void setNumGate(int nGate){
		final int n = nGate;
		final int old = numGate.get();
		if(n==old)
			return;
		if(n > old && pool==null){
			System.out.println("Gate setNumGate(): pool==null added. bug?");
			return;
		}
		waitSetNumGate.set(true);
		numGate.set(n);
		new Thread(){
			@Override
			public void run() {
				// run in new thread since it may take some time.
				System.out.println("Gate setNumGate("+n+"): old="+old);
				try {
					if(n > old){		// {0}.put(1)->{0,1}
						que.put(pool);
						pool = null;
					}else if(n < old){
						pool = que.take();	// {0,1}.take()->{0}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				waitSetNumGate.set(false);
			}
		}.start();
	}

	public static int getNumRun() {
		return numRun.get();
	}
}
