package saccubus;

import java.io.File;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import saccubus.net.NicoClient;

public class ConvertManager extends Thread {
	private static AtomicInteger numThread = new AtomicInteger(1);
	private static AtomicInteger numRun = new AtomicInteger(0);
	private static AtomicInteger numFinish = new AtomicInteger(0);
	private static ConcurrentLinkedQueue<ConvertWorker> reqQueue = new ConcurrentLinkedQueue<>();
	private static Hashtable<ConvertWorker, NicoClient> clientTab = new Hashtable<>(2);
	private static Hashtable<ConvertStopFlag, ConvertWorker> flagTable = new Hashtable<>();
	@SuppressWarnings("unused")
	private JLabel managerStatus = new JLabel();
	private JLabel managerTime = new JLabel();
	@SuppressWarnings("unused")
	private JLabel managerInfo = new JLabel();
	private int nPending = 0;

	public ConvertManager(JLabel[] st3){
		if(st3!=null){
			int len = st3.length;
			if (len > 0) managerStatus = st3[0];
			if (len > 1) managerTime = st3[1];
			if (len > 2) managerInfo = st3[2];
		}
	}

	public void run(){
		init();
	}

	private int getNumReq(){
		return reqQueue.size();
	}

	public ConvertWorker request(
		int nThread,
		String url,
		String time,
		ConvertingSetting setting,
		JLabel[] status3,
		ConvertStopFlag flag,
		MainFrame frame,
		HistoryDeque<File> play_list,
		StringBuffer sbret
		)
	{
		numThread.set(nThread);
		ConvertWorker converter = new ConvertWorker(
				url,
				time,
				setting,
				status3,
				flag,
				frame,
				play_list,
				this,
				sbret);
		flagTable.put(flag, converter);
		if(flag.isPending()){
			nPending++;
		}else{
			reqQueue.offer(converter);
			queueCheckAndGo();
		}
		return converter;	//実行したものではなく要求を受け付けたもの
	}

	public void reqDone(String result, ConvertStopFlag flag){
		if(flag!=null){
			//table から削除
			flagTable.remove(flag);
		}
		if(result==null || (!result.equals("0") && !result.equals("FF"))){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// e.printStackTrace();
			}	//1秒 待機
		}
		// 次の変換をqueから取り出して実行
		numRun.decrementAndGet();
		numFinish.incrementAndGet();
		queueCheckAndGo();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// e.printStackTrace();
		}	//500ms 待機
	}

	public int notice(Object num) {
		int nMax = numThread.get();
		try {
			nMax = Integer.decode(num.toString());
			numThread.set(nMax);
			queueCheckAndGo();
		} catch(NumberFormatException e){
			e.printStackTrace();
		}
		return nMax;
	}

	void queueCheckAndGo(){
		ConvertWorker conv = null;
		for(int count = 0;count < (numThread.get()+2);count++){
			sendTimeInfo();
			while(numRun.get() < numThread.get() && getNumReq() > 0){
				conv = reqQueue.poll();
				if(conv==null){
					System.out.println("Error: manager#queueGo null  req="+getNumReq()+" run="+numRun.get()+" thread="+numThread.get());
					break;
				}
				ConvertStopFlag flag = conv.getStopFlag();
				synchronized(flag){
					flag.go();
					flag.notify();
				}
				conv.execute();
				numRun.incrementAndGet();
				System.out.println("manager#queueGo excute()  req="+getNumReq()+" run="+numRun.get()+" thread="+numThread.get());
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// e.printStackTrace();
				}	//100ms 待機
			}
		}
		return;
	}

	private void sendTime(final String text) {
		if(SwingUtilities.isEventDispatchThread()){
			managerTime.setText(text);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					managerTime.setText(text);
				}
			});
		}
	}
	private void sendTimeInfo(){
		sendTime("Thr:"+numThread.get()+"　 Fin:"+numFinish.get()+"　 Run:"+numRun.get()+"　 Req:"+getNumReq()+"　 Pending:"+nPending);
	}

	public void cancelAllRequest() {
		reqQueue.clear();
	}

	public void init(){
		reqQueue.clear();
		numRun.set(0);
		numThread.set(1);
	}

	public static NicoClient getManagerClient(ConvertWorker conv){
		NicoClient client = clientTab.get(conv);
		if(client==null){
			client = conv.getNicoClient();
		//	clientTab.put(conv, client);
		}
		return client;
	}

	public ConvertWorker gotoCancel(ConvertStopFlag flag) {
		ConvertWorker conv = flagTable.get(flag);
		if(conv!=null && !flag.isFinished()){
			// skip reqQue whether or not in reqQue
			if(flag.isPending()){
				// not reqested nor executed
				synchronized(flag){
					flag.go();
					flag.start();
					nPending--;
					flag.stop();
					conv.abortByCancel();
					numFinish.incrementAndGet();
					flagTable.remove(flag);
				}
				System.out.println("manager#cancel pending  req="+getNumReq()+" run="+numRun.get()+" thread="+numThread.get());
				sendTimeInfo();
			}else
			if(reqQueue.remove(conv)){
				// not executed
				synchronized(flag){
					flag.stop();
					conv.abortByCancel();
					numFinish.incrementAndGet();
					flagTable.remove(flag);
				}
				System.out.println("manager#cancel reqQueue  req="+getNumReq()+" run="+numRun.get()+" thread="+numThread.get());
				sendTimeInfo();
			}else{
				// executed
				synchronized(flag){
					flag.stop();
					flag.notify();
				}
			}
		}
		return conv;
	}

	public ConvertWorker gotoRequest(ConvertStopFlag flag) {
		ConvertWorker conv = flagTable.get(flag);
		if(conv!=null && flag.isPending()){
			synchronized(flag){
				flag.go();
				flag.notify();
			}
			nPending--;
			reqQueue.offer(conv);
			queueCheckAndGo();
		}
		return conv;
	}

	public ConvertWorker buttonPushed(ConvertStopFlag flag) {
		if(flag.isPending()){
			gotoRequest(flag);
		}else
		if(!flag.isFinished()){
			gotoCancel(flag);
		}
		queueCheckAndGo();
		return flagTable.get(flag);
	}

	public void allDelete() {
		numFinish.set(0);
		sendTimeInfo();
	}

	public int getNumRun() {
		return numRun.get();
	}
}
