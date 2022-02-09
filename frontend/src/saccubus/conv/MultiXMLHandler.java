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

	public MultiXMLHandler(ChatArray chatArray) {
		this.chatArray = chatArray;
		chat_kicked = false;
	}

	@Override
	public void startDocument() {
		// System.out.println("Starting Combining XML files.");
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
	  try{
		if (qName.toLowerCase().equals("thread")){
			chat = null;
			String thread = attributes.getValue("thread");
			chatArray.setThread(thread);
			// System.out.print("thread(" + thread + ") ");
			return;
		}
		if (qName.toLowerCase().equals("chat")) {
			chat = new ChatSave("chat");
			chat_kicked = false;
			//マイメモリ削除対象
			String deleted = attributes.getValue("deleted");
			if(deleted != null && deleted.equals("1")){
				chat_kicked = true;
				return;
			}
			chat.setAttributeString(new ChatAttribute("chat",attributes));
			return;
		}
		// other qName
		{
			chat = new ChatSave(qName);
			chat_kicked = false;
			chat.setAttributeString(new ChatAttribute(qName, attributes));
			return;
		}
	  }catch(Exception e){
		// just ignore element
	  }
		// System.out.print(qName + " ");
	}

	@Override
	public void characters(char[] ch, int offset, int length) {
		if (chat != null) {
			if(chat.getQName().equals("chat")){
				chat.setComment(new String(ch, offset, length));
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) {
		if (chat != null) {
			if (!chat_kicked) {
				chatArray.addChat(chat);
			}
			chat = null;
		}
	}

	@Override
	public void endDocument() {
		System.out.print(".");
	}

}
