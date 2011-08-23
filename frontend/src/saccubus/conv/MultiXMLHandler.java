/**
 *
 */
package saccubus.conv;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author orz
 *
 */
public class MultiXMLHandler extends DefaultHandler {
	private final ChatArray chatArray;
	private Chat chat;
	private boolean chat_kicked;
	private String forkStr;

	public MultiXMLHandler(ChatArray chatArray) {
		this.chatArray = chatArray;
	}

	public void startDocument() {
		System.out.println("Convert muliti_XML to a combined file.");
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		if (qName.equals("thread")){
			String thread = attributes.getValue("thread");
			chatArray.setThread(thread);
			System.out.print("(thread " + thread + ")");
			return;
		}
		if (qName.equals("chat")) {
			chat = new Chat();
			chat_kicked = false;
			//É}ÉCÉÅÉÇÉäçÌèúëŒè€
			String deleted = attributes.getValue("deleted");
			if(deleted != null && deleted.equals("1")){
				chat_kicked = true;
				return;
			}
			chat.setDate(attributes.getValue("date"));
			chat.setMail(attributes.getValue("mail"));
			chat.setNo(attributes.getValue("no"));
			chat.setUserID(attributes.getValue("user_id"));
			chat.setVpos(attributes.getValue("vpos"));
			forkStr = attributes.getValue("fork");
			if (forkStr != null){
				chat.setFork(forkStr);
			}
			System.out.print(".");
			return;
		}
		System.out.print(qName + " ");
	}

	public void characters(char[] ch, int offset, int length) {
		char input[] = (new String(ch, offset, length)).toCharArray();
		for (int i = 0; i < input.length; i++) {
			if (!Character.isDefined(input[i])) {
				input[i] = '?';
			}
		}
		if (chat != null) {
			chat.setComment(new String(input));
		}
	}

	public void endElement(String uri, String localName, String qName) {
		if (qName.equals("chat")) {
			if (!chat_kicked) {
				chatArray.addChat(this.chat);
			}
			chat = null;
		}
	}

	public void endDocument() {
		System.out.println("\nEach convert finished.");
	}

}
