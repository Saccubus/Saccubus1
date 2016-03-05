package saccubus;

import java.util.LinkedList;

public class HistoryDeque<T> {
	private final LinkedList<T> deque = new LinkedList<>();
	private T now;
	private final T initV;
	private int index;

	public HistoryDeque(T t){
		initV = t;
		now = initV;
		index = 0;
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
	 */
	public boolean offer(T t) {
		synchronized(deque){
			return deque.offer(t);
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
			index = size() - 1;
			if(deque.isEmpty())
				now = initV;
			else
				now = deque.getLast();
			return now;
		}
	}
	/**
	 *  現在位置indexの値を返す
	 *  indexは返した値の次の位置に移動。
	 */
	public T next(){
		synchronized(deque){
			if(index >= deque.size()){
				index = deque.size();
				now = initV;
			} else {
				now = deque.get(index);
				index++;			}
			return now;
		}
	}
	/**
	 *  現在位置indexの前の値を返す
	 *  indexは返した値の位置に移動。
	 */
	public T back(){
		synchronized(deque){
			if(index <= 0 || deque.isEmpty()){
				index = 0;
				now = initV;
			}else{
				now = deque.get(--index);
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
