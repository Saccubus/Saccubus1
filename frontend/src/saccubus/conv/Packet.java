package saccubus.conv;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import saccubus.util.Util;

/**
 * <p>
 * �^�C�g��: ������΂�
 * </p>
 *
 * <p>
 * ����: �j�R�j�R����̓�����R�����g���ŕۑ�
 * </p>
 *
 * <p>
 * ���쌠: Copyright (c) 2007 PSI
 * </p>
 *
 * <p>
 * ��Ж�:
 * </p>
 *
 * @author ������
 * @version 1.0
 */
public class Packet {
	LinkedList<Chat> ChatList = new LinkedList<Chat>();
	ArrayList<CommentReplace> ReplaceList = new ArrayList<CommentReplace>();

	public Packet() {
	}

	public void addChat(Chat chat) {
		ChatList.add(chat);
	}

	public void write(OutputStream os) throws IOException {
		Util.writeInt(os, ChatList.size());
		Iterator<Chat> it = ChatList.iterator();
		while (it.hasNext()) {
			Chat chat = it.next();
			for(CommentReplace cr: ReplaceList){
				cr.replace(chat);
			}
			chat.write(os);
		}
	}

	public int size() {
		return ChatList.size();
	}

	public void addReplace(CommentReplace comrpl){
		ReplaceList.add(comrpl);
	}
}
