package saccubus.conv;

import java.util.LinkedList;
import java.io.*;
import java.util.Iterator;

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
			chat.write(os);
		}
	}

	public int size() {
		return ChatList.size();
	}
}
