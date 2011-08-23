package saccubus.conv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 *
 * @author orz
 * @version 1.22r3e
 */
public class ChatArray {
	private ArrayList<ChatSave> chatList;
	private ArrayList<Integer> chatIndex;
	private String thread;

	public ChatArray() {
		chatList = new ArrayList<ChatSave>();
		chatIndex = new ArrayList<Integer>();
		thread = null;
	}

	public void addChat(ChatSave chat){
		int no = chat.getNo();
		if (no < 0) {
			System.out.println("\nCan't add chat()");
			return;
		}
		if (chatIndex.size() <= no){
			for (int i = chatIndex.size(); i < no; i++){
				chatIndex.add(i, -1);
			}
			chatIndex.add(no, chatList.size());
			chatList.add(chatList.size(), chat);
			return;
		}
		int idx = chatIndex.get(no);
		if (idx == -1){
			chatIndex.set(no, chatList.size());
			chatList.add(chatList.size(), chat);
			return;
		}
		// nothing to do, because chat is idential if No. is same.
		return;
	}

	public void writeXML(File file) throws IOException{
		PrintWriter pw = new PrintWriter(new BufferedWriter(
			new OutputStreamWriter(new FileOutputStream(file),"UTF-8")));
		pw.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		pw.println("<packet><thread thread=\"" + thread + "\" />");
		pw.flush();
		for (int no = 0; no < chatIndex.size(); no++){
			int idx = chatIndex.get(no);
			if (idx >= 0 && idx < chatList.size()){
				ChatSave chat = chatList.get(idx);
				chat.printXML(pw);
			} else {
				// idx == -1 then nothing to do
			}
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
