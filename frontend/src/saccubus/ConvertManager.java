package saccubus;

import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JLabel;

public class ConvertManager extends Thread {
	private static AtomicInteger numThread = new AtomicInteger(1);
	private static AtomicInteger numRun = new AtomicInteger(0);
	private static ConcurrentLinkedQueue<ConvertWorker> reqQueue = new ConcurrentLinkedQueue<>();
	private static AtomicInteger numReq = new AtomicInteger(0);

	public ConvertManager(){

	}

	public void run(){
		init();
	}

	public ConvertWorker request(
		int nThread,
		String url,
		String time,
		ConvertingSetting setting,
		JLabel[] status3,
		ConvertStopFlag flag,
		MainFrame frame,
		ConcurrentLinkedQueue<File> queue
		)
	{
//		System.out.println("manager URL="+url+" run="+numRun.get()+" thread="+numThread.get());
		numThread.set(nThread);
		ConvertWorker converter = new ConvertWorker(
				url,
				time,
				setting,
				status3,
				flag,
				frame,
				queue,
				this,
				new StringBuffer());
		reqQueue.offer(converter);
		int nReq = numReq.incrementAndGet();
		ConvertWorker conv = null;
		int nRun = numRun.get();
		int nMax = numThread.get();
		int count = 0;
		while(nRun < nMax && count < nMax && nReq > 0){
			conv = reqQueue.poll();
			if(conv!=null){
				conv.execute();
				nRun = numRun.incrementAndGet();
				nReq = numReq.decrementAndGet();
			}
			else{
				System.out.println("manager#request ex run="+numRun.get()+" count="+count+" req="+nReq);
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// e.printStackTrace();
			}	//100ms 待機
			count++;
		}
		return converter;	//実行したものではなく要求を受け付けたもの
	}

	public void reqDone(String result){
		if(result==null || !result.equals("0")){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// e.printStackTrace();
			}	//1秒 待機
		}
		ConvertWorker conv = null;
		int nRun = numRun.decrementAndGet();
		int nMax = numThread.get();
		int nReq = numReq.get();
		if(nRun < nMax && nReq > 0){
				conv = reqQueue.poll();
				if(conv!=null){
					conv.execute();
					nRun = numRun.incrementAndGet();
					nReq = numReq.decrementAndGet();
				}
				else
					System.out.println("manager#reqDone ex run="+numRun.get()+" req="+nReq);
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// e.printStackTrace();
		}	//500ms 待機
	}

	public void notice(Object num) {
		int nMax = numThread.get();
		try {
			nMax = Integer.decode(num.toString());
		} catch(NumberFormatException e){
			e.printStackTrace();
		}
		numThread.set(nMax);
		int nReq = numReq.get();
		ConvertWorker conv = null;
		int nRun = numRun.get();
		int count = 0;
		while(nRun < nMax && count < nMax && nReq > 0){
			conv = reqQueue.poll();
			if(conv!=null){
				conv.execute();
				nRun = numRun.incrementAndGet();
				nReq = numReq.decrementAndGet();
				nMax = numThread.get();
			}
			else
				System.out.println("manager#notice run="+numRun.get()+" count="+count+" req="+nReq);
			count++;
		}
	}

	public void cancelAllRequest() {
		reqQueue.clear();
		numReq.set(0);
	}

	public void cancelAllRequest(String nThread) {
		cancelAllRequest();
		notice(nThread);
	}

	public void init(){
		reqQueue.clear();
		numReq.set(0);
		numRun.set(0);
		numThread.set(1);
	}
}
