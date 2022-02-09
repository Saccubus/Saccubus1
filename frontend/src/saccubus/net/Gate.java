package saccubus.net;

import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import saccubus.util.Logger;

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
		netInit(MAX_GATE, Logger.MainLog);
	}
	private final static AtomicBoolean waitSetNumGate = new AtomicBoolean(false);
	public final static int RETRY_WAIT_MILISECOND = 5000;	//5秒
	public final static int ERROR_WAIT_MILISECOND = 1000;	//1秒
	private final static int MAX_RETRY_WAIT_MILISECOND = 120000;	//2分間
	private Logger log;
	private static Date lastErrorDate = new Date();
	private static int lastErrorWaittime = ERROR_WAIT_MILISECOND;
	private static boolean isLastError = false;

	public Gate(){
		entered = false;
		count = 0;
		limitter = 3;		//retry max
		ticket = null;
	}

	public Gate(int tid, Logger logger) {
		this();
		id = tid;
		log = logger;
	}

	public static void init(Logger log){
		netInit(numGate.get(), log);
	}

	public static void netInit(int n, Logger log){
		que.clear();
		for(int i = 0; i < n; i++ ){
			que.offer(i);
		}
		log.println("Gate#netInit("+n+") numGate="+numGate.get());
	}

	public static Gate open(int tid, Logger log){
		Gate g = new Gate(tid, log);
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
					log.printStackTrace(e1);
				}
				while(waitSetNumGate.get()){
					try {
						Thread.sleep(100);
						que.put(ticket);
						ticket = null;
					} catch (InterruptedException e) {
						log.println("Gate#enter("+id+"):Exception ticket="+ticket);
						log.printStackTrace(e);
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
					log.printStackTrace(e);
				}
			}
		}while(ticket==null);
		numReq.decrementAndGet();
		log.println("Gate#enter("+id+") nRun="+numRun.get()+",nReq="+numReq.get()+",nGate="+numGate.get());
		return;
	}

	/**
	 * exit(miliseconds)
	 * @param miliseconds 次の enterを待たせるミリ秒数、エラー終了後は待つこと
	 */
	private void exit(int miliseconds){
		if(entered){
			entered = false;
			if(miliseconds > 0){
				// エラー終了後は次のenterさせない。ticket持ったまま
				try{
					Thread.sleep(miliseconds);
				}catch(InterruptedException e){
					// log.printStackTrace(e);
				}
			}
			try {
				numRun.decrementAndGet();
				assert numRun.get() >= 0 : numRun;
				log.println("Gate#exit("+id+")  nRun="+numRun.get()+",nReq="+numReq.get()+",nGate="+numGate.get());
				assert ticket != null : ticket;
				que.put(ticket);
			} catch (InterruptedException e) {
				log.println("Gate#exit("+id+") Exception:  nRun="+numRun.get()+",nReq="+numReq.get()+",nGate="+numGate.get());
				log.printStackTrace(e);
			}
		}
	}

	public boolean notExceedLimiterGate() {
		if(!entered)
			return false;

		log.println("Gate#notExceedLimitterGate("+id+") waiting");
		exit(getErrorRetryWait());		//　待機後、ロック開放
		if(count++ > limitter){	//retry数超えたら false
			log.println("Gate#notExceedLimitterGate("+id+") limmiter("+limitter
					+") over, wait: "+lastErrorWaittime/1000+"sec.");
			count = 0;
			return false;
		}
		// retry
		enter();
		return true;
	}

	private int getErrorRetryWait() {
		synchronized (lastErrorDate) {
			try {
				if(isLastError){
					lastErrorWaittime = Math.min(lastErrorWaittime*2,MAX_RETRY_WAIT_MILISECOND);
					long now = (new Date()).getTime();
					long waitend = lastErrorDate.getTime() + lastErrorWaittime;
					return (int)Math.max(0, waitend - now) + RETRY_WAIT_MILISECOND;
				}else{
					isLastError = true;
					return lastErrorWaittime;	//5秒待機
				}
			} finally {
				lastErrorDate = new Date();
			}
		}
	}

	public void setError(){
		if(!entered) throw new RuntimeException("Gate#setError() while !enterd. ");
		synchronized (lastErrorDate) {
			lastErrorDate = new Date();
			isLastError = true;
		}
	}

	public void resetError(){
		if(!entered) throw new RuntimeException("Gate#resetError() while !enterd. ");
		synchronized (lastErrorDate) {
			if(isLastError){
				long now = (new Date()).getTime();
				if (lastErrorDate.getTime() + MAX_RETRY_WAIT_MILISECOND < now){
					lastErrorWaittime = Math.max(lastErrorWaittime/2,ERROR_WAIT_MILISECOND);
				}
				lastErrorDate = new Date();
				isLastError = false;
			}
		}
	}

	public synchronized static void setNumGate(int nGate, final Logger log){
		final int n = nGate;
		final int old = numGate.get();
		if(n==old)
			return;
		if(n > old && pool==null){
			log.println("Gate setNumGate(): pool==null added. bug?");
			return;
		}
		waitSetNumGate.set(true);
		numGate.set(n);
		new Thread(){
			@Override
			public void run() {
				// run in new thread since it may take some time.
				log.println("Gate setNumGate("+n+"): old="+old);
				try {
					if(n > old){		// {0}.put(1)->{0,1}
						que.put(pool);
						pool = null;
					}else if(n < old){
						pool = que.take();	// {0,1}.take()->{0}
					}
				} catch (InterruptedException e) {
					log.printStackTrace(e);
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

	public static int getNumGate(){
		return numGate.get();
	}

	public void exit(String result) {
		if("0".equals(result)){
			exit(0);
		}else{
			exit(ERROR_WAIT_MILISECOND);
		}
	}
}
