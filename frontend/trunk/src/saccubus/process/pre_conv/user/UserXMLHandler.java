/**
 * 
 */
package saccubus.process.pre_conv.user;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import saccubus.process.pre_conv.ng.NGList;

/**
 * @author PSI
 *
 */
public class UserXMLHandler extends DefaultHandler {
	private final Packet Packet;
	private NGList CommandList;
	private NGList UserList;
	private NGList WordList;

	/**
	 * 内部のみで使われる変数群
	 */
	private Chat _Item;
	private boolean _ItemKicked;

	/**
	 * 初期化する。
	 * @param packet
	 * @param commandList
	 * @param userList
	 * @param wordList
	 */
	protected UserXMLHandler(final Packet packet, NGList commandList, NGList userList, NGList wordList) {
		Packet = packet;
		CommandList = commandList;
		UserList = userList;
		WordList = wordList;
	}

	/**
	 * 変換開始時に呼ばれる
	 */
	public void startDocument() {
		System.out.println("Start converting to interval file.");
	}

	/**
	 * 
	 * @param uri
	 *            String
	 * @param localName
	 *            String
	 * @param qName
	 *            String
	 * @param attributes
	 *            Attributes
	 */
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		if (qName.toLowerCase().equals("chat")) {
			// System.out.println("----------");
			_Item = new Chat();
			_ItemKicked = false;
			//マイメモリ削除対象
			String deleted = attributes.getValue("deleted");
			if(deleted != null && deleted.toLowerCase().equals("1")){
				_ItemKicked = true;
				return;
			}
			_Item.setDate(attributes.getValue("date"));
			String mail = attributes.getValue("mail");
			if(CommandList.match(mail)){
				_ItemKicked = true;
				return;
			}
			_Item.setMail(mail);
			_Item.setNo(attributes.getValue("no"));
			String user_id = attributes.getValue("user_id");
			if (UserList.match(user_id)){
				_ItemKicked = true;
				return;
			}
			_Item.setUserID(user_id);
			_Item.setVpos(attributes.getValue("vpos"));

		}
	}

	/**
	 * 
	 * @param ch
	 *            char[]
	 * @param offset
	 *            int
	 * @param length
	 *            int
	 */
	public void characters(char[] ch, int offset, int length) {
		char input[] = (new String(ch, offset, length)).toCharArray();
		for (int i = 0; i < input.length; i++) {
			if (!Character.isDefined(Character.codePointAt(input, i))) {
				input[i] = '?';
			}
		}
		if (_Item != null) {
			String com = new String(input);
			if(WordList.match(com)){
				_ItemKicked = true;
				return;
			}
			_Item.setComment(com);
		}
	}

	/**
	 * 
	 * @param uri
	 *            String
	 * @param localName
	 *            String
	 * @param qName
	 *            String
	 */
	public void endElement(String uri, String localName, String qName) {
		if (qName.toLowerCase().equals("chat")) {
			if (!_ItemKicked) {
				Packet.addChat(_Item);
			}
			_Item = null;
		}
	}

	/**
	 * ドキュメント終了
	 */
	public void endDocument() {
		// System.out.println("----------");
		System.out.println("Converting finished.");
	}

}