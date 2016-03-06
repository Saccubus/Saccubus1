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
			//�Q�[�g�҂�
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
					que.notify();	//�K�v���H
			}
			// que�擪������, nRun < nGate
			que.poll();
			numRun.incrementAndGet();
		}
		//�E�F�C�g����
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
					//���̑҂��������[�X
					//	if(que.peek() == null){
					//		System.out.println("Gate que return null, �o�O?");
					//		return;
								//	}
					que.notify();
				}
			}
			// Req <=0 �܂��́@nRun>=nGate
			System.out.println("Gate exited: nRun="+numRun.get()+",nReq="+que.size()+",nGate="+numGate.get());
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
