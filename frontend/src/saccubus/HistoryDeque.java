package saccubus;

import java.util.LinkedList;

public class HistoryDeque<T> {
	private LinkedList<T> back = new LinkedList<>();
	private LinkedList<T> forward = new LinkedList<>();
	private T last;
	private T initV;

	public HistoryDeque(T t){
		initV = t;
		last = initV;
	}

	public boolean add(T t){
		synchronized(back){
			last = t;
			return back.add(t);
		}
	}

	public void addFirst(T t) {
		synchronized(back) {
			last = t;
			back.addFirst(t);
		}

	}

	public void offer(T t) {
		synchronized(back){
			last = t;
			back.offer(t);
			return;
		}
	}

	public T getLast(){
		synchronized(back){
			if(!back.isEmpty())
				last = back.getLast();
			else
				last = initV;
			return last;
		}
	}

	public T removeLast(){
		synchronized(back){
			if(!back.isEmpty()){
				last = back.removeLast();
				forward.add(last);
			} else {
				last = initV;
			}
			return last;
		}
	}

	public T getNext(){
		synchronized(back){
			if(!forward.isEmpty()){
				last = forward.getLast();
			} else
				last = initV;
			return last;
		}
	}

	public T removeNext(){
		synchronized(back){
			if(!forward.isEmpty()){
				last = forward.removeLast();
				back.add(last);
			}else{
				last = initV;
			}
			return last;
		}
	}

	public T removeBack(){
		synchronized(back){
			if(!forward.isEmpty()){
				last = forward.removeLast();
				back.addFirst(last);
			}else{
				last = initV;
			}
			return last;
		}
	}

	public T poll() {
		synchronized(back){
			if(!back.isEmpty()){
				last = back.poll();
				forward.add(last);
			}else
				last = initV;
			return last;
		}
	}

	public T peek() {
		synchronized(back){
			if(!back.isEmpty()){
				last = back.peek();
			}else{
				last = initV;
			}
			return last;
		}
	}

	public String getText() {
		synchronized(back){
			StringBuffer sb = new StringBuffer();
			if(!forward.isEmpty()){
				for(T t:forward){
					sb.append(t.toString());
					sb.append("\n");
				}
			}
			for(T t:back){
				sb.append(t.toString());
				sb.append("\n");
			}
			return sb.substring(0);
		}
	}
}
