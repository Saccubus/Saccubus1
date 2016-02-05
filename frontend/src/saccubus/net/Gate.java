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
		Gate g = new Gate();
		g.reEnter();
		return g;
	}
	public void reEnter(){
		int nRun;
		int nGate;
		int nReq;
		entered = true;
		synchronized(que){
			nRun = numRun.incrementAndGet();
			nGate = numGate.get();
			nReq = numReq.get();
			if(nRun <= nGate){
				System.out.println("Gate entered: nRun="+nRun+",nReq="+nReq+",nGate="+nGate);
				return;
			}
			//�Q�[�g�҂�
			que.offer(this);
			nReq = numReq.incrementAndGet();
			nRun = numRun.decrementAndGet();
		}
		synchronized(this){
			try {
				System.out.println("Gate waiting: nRun="+nRun+",nReq="+nReq+",nGate="+nGate);
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//�E�F�C�g����
		System.out.println("Gate entered after wait");
		return;
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
					//���̑҂��������[�X
					rg = que.poll();
					if(rg == null){
						System.out.println("Gate que return null, �o�O?");
						return;
					}
					nReq = numReq.decrementAndGet();
					nRun = numRun.incrementAndGet();
				}
			}
			if(rg!=null){
				synchronized (rg) {
					rg.notify();
				}
			}
			System.out.println("Gate exited: nRun="+nRun+",nReq="+nReq+",nGate="+nGate);
		}
	}

	public boolean notExceedLimiterGate() {
		if(!entered)
			return false;

		exit();
		// ���b�N�J��
		try {	//5�b�ҋ@
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		reEnter();
		if(count++ > limitter){	//retry���������� false
			count = 0;
			return false;
		}
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
