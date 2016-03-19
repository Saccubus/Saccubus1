package saccubus.net;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Gate extends Thread {
	private final static int MAX_GATE = 2;
	private final static AtomicInteger numRun  = new AtomicInteger(0);
	private final static AtomicInteger numGate = new AtomicInteger(MAX_GATE);
	private final static AtomicInteger numReq = new AtomicInteger(0);
	private boolean entered = false;
	private int limitter;
	private int count;
	private Integer ticket;
	private int id;
	private static Integer pool = null;
	private final static LinkedBlockingQueue<Integer> que = new LinkedBlockingQueue<Integer>();
	static {
		netInit(MAX_GATE);
	}
	private final static AtomicBoolean waitSetNumGate = new AtomicBoolean(false);
	public final static int RETRY_WAIT_MILISECOND = 5000;	//5秒
	public final static int ERROR_WAIT_MILISECOND = 1000;	//5秒

	public Gate(){
		entered = false;
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
		System.out.println("Gate#netInit("+n+") numGate="+numGate.get());
	}

	public static Gate open(int wid){
		Gate g = new Gate();
		g.id = wid;
		g.enter();
		return g;
	}
	public void enter(){
		if(entered)
			return;

		entered = true;
		numReq.incrementAndGet();
		do{
			while(ticket==null){
				try {
					ticket = que.take();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				while(waitSetNumGate.get()){
					try {
						Thread.sleep(100);
						que.put(ticket);
						ticket = null;
					} catch (InterruptedException e) {
						System.out.println("Gate#enter("+id+"):Exception ticket="+ticket);
						e.printStackTrace();
					}
					continue;
				}
			}
			if(numRun.incrementAndGet() > numGate.get()){
				numRun.decrementAndGet();
				try {
					Thread.sleep(100);
					if(ticket!=null){
						que.put(ticket);
						ticket = null;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}while(ticket==null);
		numReq.decrementAndGet();
		System.out.println("Gate#enter("+id+") nRun="+numRun.get()+",nReq="+numReq.get()+",nGate="+numGate.get());
		return;
	}

	/**
	 * exit(miliseconds)
	 * @param miliseconds 次の enterを待たせるミリ秒数、エラー終了後は待つこと
	 */
	public void exit(int miliseconds){
		if(entered){
			entered = false;
			if(miliseconds > 0){
				// エラー終了後は次のenterさせない。ticket持ったまま
				try{
					Thread.sleep(miliseconds);
				}catch(InterruptedException e){
					// e.printStackTrace();
				}
			}
			try {
				numRun.decrementAndGet();
				assert numRun.get() >= 0 : numRun;
				System.out.println("Gate#exit("+id+")  nRun="+numRun.get()+",nReq="+numReq.get()+",nGate="+numGate.get());
				assert ticket != null : ticket;
				que.put(ticket);
			} catch (InterruptedException e) {
				System.out.println("Gate#exit("+id+") Exception:  nRun="+numRun.get()+",nReq="+numReq.get()+",nGate="+numGate.get());
				e.printStackTrace();
			}
		}
	}

	public boolean notExceedLimiterGate() {
		if(!entered)
			return false;

		System.out.println("Gate#notExceedLimitterGate("+id+") waiting");
		exit(RETRY_WAIT_MILISECOND);		//　5秒待機後、ロック開放
		if(count++ > limitter){	//retry数超えたら false
			count = 0;
			return false;
		}
		// retry
		enter();
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

	public static int getNumReq() {
		return numReq.get();
	}

	public void exit(String result) {
		if("0".equals(result)){
			exit(0);
		}else{
			exit(ERROR_WAIT_MILISECOND);
		}
	}
}
