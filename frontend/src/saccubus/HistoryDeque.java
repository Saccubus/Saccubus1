package saccubus;

import java.util.LinkedList;

/*
 * 1.66.2.7 nextも先に移動して移動先を返すことに変更 2016/12/11
 */
public class HistoryDeque<T> {
	private final LinkedList<T> deque = new LinkedList<>();
	private T now;
	private final T initV;
	private int index;
	private boolean wraparound = false;

	public HistoryDeque(T t){
		initV = t;
		now = initV;
		index = 0;
	}

	public HistoryDeque(T t, boolean wrap){
		this(t);
		wraparound = wrap;
		this.add(t);
	}

	/**
	 *  最後尾に追加
	 *  現在位置indexは最後尾に移動(tをポイントする)
	 */
	public boolean add(T t){
		synchronized(deque){
			now = t;
			index = deque.size();
			return deque.add(t);
		}
	}
	public boolean addLast(T t){
		return add(t);
	}
	/**
	 *  先頭に追加
	 *  現在位置indexは先頭に移動(tをポイントする)
	 */
	public void addFirst(T t) {
		synchronized(deque) {
			now = t;
			index = 0;
			deque.addFirst(t);
		}
	}
	/**
	 *  最後尾に追加
	 *  現在位置indexは動かない
	 *  (初期状態やエラー状態は修正)
	 */
	public boolean offer(T t) {
		boolean b = false;
		synchronized(deque){
			b = deque.offer(t);
			if(deque.size()<=1){
				index = 0;
			}else if(index>=deque.size()){
				index = deque.size() - 1;
			}
			now = deque.get(index);
			return b;
		}
	}
	/**
	 *  現在の値を返す
	 *  現在位置indexは動かない(nowをポイントする)
	 */
	public T getNow(){
		synchronized(deque){
			return now;
		}
	}
	/**
	 *  最後尾を返す
	 *  現在位置indexは最後尾に移動(nowをポイントする)
	 */
	public T getLast(){
		synchronized(deque){
			index = deque.size() - 1;
			if(deque.isEmpty())
				now = initV;
			else
				now = deque.getLast();
			return now;
		}
	}
	/**
	 *  indexは次の位置に移動。
	 *  wraparoundの場合: indexが最後尾なら先頭に戻る
	 *  現在位置indexの値を返す
	 */
	public T next(){
		synchronized(deque){
			index++;
			if(index >= deque.size()){
				if(wraparound){
					index = 0;
					now = deque.get(index);
				}else{
					index = deque.size();
					now = initV;
				}
			}
			else{
				now = deque.get(index);
			}
			return now;
		}
	}
	/**
	 *  indexは前の位置に移動。
	 *  wraparoundの場合：　先頭なら最後尾に移動。
	 *  現在位置indexの値を返す
	 */
	public T back(){
		synchronized(deque){
			index--;
			if(index < 0){
				if(wraparound){
					now = this.getLast();
				}else{
					index = -1;
					now = initV;
				}
			}else{
				now = deque.get(index);
			}
			return now;
		}
	}
	/**
	 *  先頭の値を削除して返す
	 *  現在位置indexは動かない
	 */
	public T poll() {
		synchronized(deque){
			if(!deque.isEmpty()){
				now = deque.poll();
			}else
				now = initV;
			return now;
		}
	}
	/**
	 *  先頭の値を返す
	 *  現在位置indexは動かない
	 */
	public T peek() {
		synchronized(deque){
			if(!deque.isEmpty()){
				now = deque.peek();
			}else
				now = initV;
			return now;
		}
	}
	/**
	 *  文字列を返す
	 */
	public String getText() {
		StringBuffer sb = new StringBuffer("");
		synchronized(deque){
			if(!deque.isEmpty()){
				for(T t:deque){
					if(t==null || t.equals(initV))
						continue;
					sb.append(t.toString());
					sb.append("\n");
				}
			}
			return sb.substring(0);
		}
	}
	/**
	 *  サイズを返す
	 */
	public int size() {
		return deque.size();
	}
	/**
	 *  indexを返す
	 */
	public int getIndex() {
		return index;
	}
}
