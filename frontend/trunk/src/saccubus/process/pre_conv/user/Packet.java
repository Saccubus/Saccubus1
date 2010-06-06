/**
 * 
 */
package saccubus.process.pre_conv.user;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;

import saccubus.util.IOUtil;

/**
 * @author PSI
 *
 */
public class Packet {
	LinkedList<Chat> ChatList = new LinkedList<Chat>();

	public Packet() {
	}

	protected void addChat(Chat chat) {
		ChatList.add(chat);
	}

	public void write(OutputStream os) throws IOException {
		IOUtil.writeInt(os, ChatList.size());
		Iterator<Chat> it = ChatList.iterator();
		while (it.hasNext()) {
			Chat chat = it.next();
			chat.write(os);
		}
	}
}
