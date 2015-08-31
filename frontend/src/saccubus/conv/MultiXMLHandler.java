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
	}

	@Override
	public void startDocument() {
		// System.out.println("Starting Combining XML files.");
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
	  try{
		if (qName.toLowerCase().equals("thread")){
			String thread = attributes.getValue("thread");
			chatArray.setThread(thread);
			// System.out.print("thread(" + thread + ") ");
			return;
		}
		if (qName.toLowerCase().equals("chat")) {
			chat = new ChatSave();
			chat_kicked = false;
			//É}ÉCÉÅÉÇÉäçÌèúëŒè€
			String deleted = attributes.getValue("deleted");
			if(deleted != null && deleted.equals("1")){
				chat_kicked = true;
				return;
			}
			chat.setAttributeString(toAttributeString(attributes));
			//chat.setNo(attributes.getValue("no"));
			return;
		}
	  }catch(Exception e){
		// just ignore element
	  }
		// System.out.print(qName + " ");
	}

	private String toAttributeString(Attributes attributes){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i <attributes.getLength(); i++) {
			sb.append(attributes.getQName(i));
			sb.append("=\"");
			sb.append(ChatSave.safeReference(attributes.getValue(i)));
			sb.append("\" ");
		}
		return sb.substring(0);
	}

	@Override
	public void characters(char[] ch, int offset, int length) {
		if (chat != null) {
			chat.setComment(new String(ch, offset, length));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) {
		if (qName.equalsIgnoreCase("chat")) {
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
