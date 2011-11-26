package saccubus.conv;

import java.util.LinkedList;
import java.io.*;
import java.util.Iterator;

import saccubus.util.Util;

/**
 * <p>
 * タイトル: さきゅばす
 * </p>
 *
 * <p>
 * 説明: ニコニコ動画の動画をコメントつきで保存
 * </p>
 *
 * <p>
 * 著作権: Copyright (c) 2007 PSI
 * </p>
 *
 * <p>
 * 会社名:
 * </p>
 *
 * @author 未入力
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
