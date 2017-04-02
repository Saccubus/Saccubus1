package saccubus;

import java.util.LinkedList;

/*
 * 1.66.2.7 next����Ɉړ����Ĉړ����Ԃ����ƂɕύX 2016/12/11
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
	 *  (������Ԃ�G���[��Ԃ͏C��)
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
			index = deque.size() - 1;
			if(deque.isEmpty())
				now = initV;
			else
				now = deque.getLast();
			return now;
		}
	}
	/**
	 *  index�͎��̈ʒu�Ɉړ��B
	 *  wraparound�̏ꍇ: index���Ō���Ȃ�擪�ɖ߂�
	 *  ���݈ʒuindex�̒l��Ԃ�
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
	 *  index�͑O�̈ʒu�Ɉړ��B
	 *  wraparound�̏ꍇ�F�@�擪�Ȃ�Ō���Ɉړ��B
	 *  ���݈ʒuindex�̒l��Ԃ�
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
