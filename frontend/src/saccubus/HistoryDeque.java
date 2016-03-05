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
	 *  �Ō���ɒǉ�
	 *  ���݈ʒuindex�͍Ō���Ɉړ�(t���|�C���g����)
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
	 *  �擪�ɒǉ�
	 *  ���݈ʒuindex�͐擪�Ɉړ�(t���|�C���g����)
	 */
	public void addFirst(T t) {
		synchronized(deque) {
			now = t;
			index = 0;
			deque.addFirst(t);
		}
	}
	/**
	 *  �Ō���ɒǉ�
	 *  ���݈ʒuindex�͓����Ȃ�
	 */
	public boolean offer(T t) {
		synchronized(deque){
			return deque.offer(t);
		}
	}
	/**
	 *  ���݂̒l��Ԃ�
	 *  ���݈ʒuindex�͓����Ȃ�(now���|�C���g����)
	 */
	public T getNow(){
		synchronized(deque){
			return now;
		}
	}
	/**
	 *  �Ō����Ԃ�
	 *  ���݈ʒuindex�͍Ō���Ɉړ�(now���|�C���g����)
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
	 *  ���݈ʒuindex�̒l��Ԃ�
	 *  index�͕Ԃ����l�̎��̈ʒu�Ɉړ��B
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
	 *  ���݈ʒuindex�̑O�̒l��Ԃ�
	 *  index�͕Ԃ����l�̈ʒu�Ɉړ��B
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
	 *  �擪�̒l���폜���ĕԂ�
	 *  ���݈ʒuindex�͓����Ȃ�
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
	 *  �擪�̒l��Ԃ�
	 *  ���݈ʒuindex�͓����Ȃ�
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
	 *  �������Ԃ�
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
	 *  �T�C�Y��Ԃ�
	 */
	public int size() {
		return deque.size();
	}
	/**
	 *  index��Ԃ�
	 */
	public int getIndex() {
		return index;
	}
}
