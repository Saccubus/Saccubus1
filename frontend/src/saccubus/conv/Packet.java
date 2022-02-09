package saccubus.conv;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import saccubus.util.Logger;
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
	private final LinkedList<Chat> ChatList = new LinkedList<Chat>();
	private final ArrayList<CommentReplace> ReplaceList;
	private final Logger log;
	private final boolean isDebug;

	public Packet(ArrayList<CommentReplace> list, Logger logger, boolean is_debug) {
		ReplaceList = list;
		log = logger;
		isDebug = is_debug;
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
			if(isDebug)
				chat.debug(log);
		}
	}

	public int size() {
		return ChatList.size();
	}

	public void addReplace(CommentReplace comrpl){
		ReplaceList.add(comrpl);
	}
}
