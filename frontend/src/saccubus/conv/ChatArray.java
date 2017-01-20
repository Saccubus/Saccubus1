package saccubus.conv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.TreeMap;

/**
 *
 * @author orz
 * @version 1.22r3e
 */
public class ChatArray {
	private String thread;
	private TreeMap<ChatAttribute,ChatSave> chatMap;

	public ChatArray() {
		thread = Integer.toString(Integer.MAX_VALUE);
		chatMap = new TreeMap<>();
	}

	public void addChat(ChatSave chat){
		chatMap.put(chat.getAttributes(), chat);
	}

	public void writeXML(File file) throws IOException{
		PrintWriter pw = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(file),"UTF-8")));
		pw.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		pw.println("<packet><thread thread=\"" + thread + "\" />");
		pw.flush();
		for (ChatSave chat : chatMap.values()) {
			chat.printXML(pw);
		}
		pw.println("</packet>");
		pw.flush();
		pw.close();
	}

	public void setThread(String newThread) {
		if (thread == null){
			thread = newThread;
			return;
		}
		try {
			int t = Integer.parseInt(thread);
			int nt = Integer.parseInt(newThread);
			if (nt < t) {
				thread = newThread;
			}
		} catch(NumberFormatException ex){
			System.out.println("thread is not number, maybe no problem.");
			// resume bug.
		}
	}

}
