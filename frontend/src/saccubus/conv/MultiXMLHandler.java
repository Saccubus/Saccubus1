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
	private ChatSave chat;
	private boolean chat_kicked;
	@SuppressWarnings("unused")
	private String forkStr;

	public MultiXMLHandler(ChatArray chatArray) {
		this.chatArray = chatArray;
	}

	public void startDocument() {
		// System.out.println("Starting Combining XML files.");
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		if (qName.toLowerCase().equals("thread")){
			String thread = attributes.getValue("thread");
			chatArray.setThread(thread);
			// System.out.print("thread(" + thread + ") ");
			return;
		}
		if (qName.toLowerCase().equals("chat")) {
			chat = new ChatSave();
			chat_kicked = false;
			//ƒ}ƒCƒƒ‚ƒŠíœ‘ÎÛ
			String deleted = attributes.getValue("deleted");
			if(deleted != null && deleted.equals("1")){
				chat_kicked = true;
				return;
			}
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i <attributes.getLength(); i++) {
				sb.append(attributes.getQName(i));
				sb.append("=\"");
				sb.append(attributes.getValue(i));
				sb.append("\" ");
			}
			chat.setAttributeString(sb.substring(0));
			chat.setNo(attributes.getValue("no"));
			return;
		}
		// System.out.print(qName + " ");
	}

	public void characters(char[] ch, int offset, int length) {
		if (chat != null) {
			chat.setComment(new String(ch, offset, length));
		}
	}

	public void endElement(String uri, String localName, String qName) {
		if (qName.equalsIgnoreCase("chat")) {
			if (!chat_kicked) {
				chatArray.addChat(chat);
			}
			chat = null;
		}
	}

	public void endDocument() {
		System.out.print(".");
	}

}
