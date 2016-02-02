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
		ConcurrentLinkedQueue<File> queue,
		StringBuffer sbret
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
				sbret);
		reqQueue.offer(converter);
		int nReq = numReq.incrementAndGet();	//
		queueCheckAndGo(nReq, numRun.get(), numThread.get());
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
		int nRun = numRun.decrementAndGet();	//
		queueCheckAndGo(numReq.get(), nRun, numThread.get());
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
			numThread.set(nMax);
			queueCheckAndGo(numReq.get(),numRun.get(),nMax);
		} catch(NumberFormatException e){
			e.printStackTrace();
		}
	}

	private void queueCheckAndGo(int nReq, int nRun, int nMax){
		ConvertWorker conv = null;
		for(int count = 0;count < (nMax+2);count++)
			while(nRun < nMax && nReq > 0){
				conv = reqQueue.poll();
				if(conv!=null){
					conv.execute();
					nRun = numRun.incrementAndGet();
					nReq = numReq.decrementAndGet();
					System.out.println("manager#queueGo excute()  req="+nReq+" run="+nRun+" thread="+nMax+" count="+count);
				}
				else{
					System.out.println("manager#queueGo null  req="+nReq+" run="+nRun+" thread="+nMax+" count="+count);
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// e.printStackTrace();
				}	//100ms 待機
			}
		return;
	}

	public void cancelAllRequest() {
		reqQueue.clear();
		numReq.set(0);
	}

	public void init(){
		reqQueue.clear();
		numReq.set(0);
		numRun.set(0);
		numThread.set(1);
	}
}
