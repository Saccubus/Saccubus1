package saccubus.conv;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import saccubus.SharedNgScore;

/**
 * <p>
 * �^�C�g��: ������΂�
 * </p>
 *
 * <p>
 * ����: �j�R�j�R����̓�����R�����g���ŕۑ�
 * </p>
 *
 * <p>
 * ���쌠: Copyright (c) 2007 PSI
 * </p>
 *
 * <p>
 * ��Ж�:
 * </p>
 *
 * @author ������
 * @version 1.0
 */
public class NicoXMLReader extends DefaultHandler {
	private final Packet packet;

	private Chat item;

	private boolean item_kicked;

	//Object waitObject = new Object();

	private final Pattern NG_Word;

	private final Pattern NG_ID;

	private final CommandReplace NG_Cmd;

	private boolean item_fork;

	private boolean is_button;

	private final int ng_Score;
	private int countNG_Score = 0;
	private int countNG_Word = 0;
	private int countNG_ID = 0;

	public NicoXMLReader(Packet packet, Pattern ngIdPat, Pattern ngWordPat, CommandReplace cmd, int scoreLimit){
		this.packet = packet;
		NG_Word = ngWordPat;
		NG_ID = ngIdPat;
		NG_Cmd = cmd;
		ng_Score = scoreLimit;
	}

	public static final Pattern makePattern(String word) throws PatternSyntaxException{
		if (word == null || word.length() <= 0) {
			return null;
		}
		String tmp[] = word.split(" ");
		String tmp2[] = new String[tmp.length];
		int tmp_index = 0;
		int index;
		for (index = 0; index < tmp.length && tmp_index < tmp.length; index++) {
			String tmpw = tmp[tmp_index];
			if (tmpw.startsWith("/") && !tmpw.endsWith("/")) {
				StringBuffer str = new StringBuffer(tmpw);
				for (tmp_index++; tmp_index < tmp.length; tmp_index++) {
					str.append(" " + tmp[tmp_index]);
					if (tmp[tmp_index].endsWith("/")) {
						tmp_index++;
						break;
					}
				}
				tmp2[index] = str.substring(0);
			} else if (tmpw.startsWith("\"") && !tmpw.endsWith("\"")) {
				StringBuffer str = new StringBuffer(tmpw);
				for (tmp_index++; tmp_index < tmp.length; tmp_index++) {
					str.append(" " + tmp[tmp_index]);
					if (tmp[tmp_index].endsWith("\"")) {
						tmp_index++;
						break;
					}
				}
				tmp2[index] = str.substring(0);
			} else {
				tmp2[index] = tmpw;
				tmp_index++;
			}
		}
		String elt[] = new String[index];
		for (int i = 0; i < index; i++) {
			elt[i] = tmp2[i];
		}
		StringBuffer regb = new StringBuffer();
		for (int i = 0; i < elt.length; i++) {
			String e = elt[i];
			System.out.println(e);
			if (i > 0) {
				regb.append("|");
			}
			if (e.length() > 1 && e.startsWith("/") && e.endsWith("/")) {
				regb.append("(" + e.substring(1, e.length() - 1) + ")");
			} else if (e.length() > 1 && e.startsWith("\"") && e.endsWith("\"")) {
				regb.append("(" + Pattern.quote(e.substring(1, e.length() - 1))
						+ ")");
			} else {
				regb.append("(.*(" + Pattern.quote(e) + ")+.*)");
			}
		}
		String reg = regb.substring(0);
		System.out.println("reg:" + reg);
		Pattern pat;
		pat = Pattern.compile(reg);
		return pat;
	}

	private static final boolean match(Pattern pat, String word) {
		if (word == null || word.length() <= 0 || pat == null) {
			return false;
		}
		return pat.matcher(word).matches();
	}

	/**
	 *
	 */
	@Override
	public void startDocument() {
		System.out.println("Start converting to intermediate file.");
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
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		if (qName.toLowerCase().equals("chat")) {
			// System.out.println("----------");
			item = new Chat();
			item_kicked = false;
			item_fork = false;
			is_button = false;
			//�}�C�������폜�Ώ�
			//�^�c�폜�Ώ�
			String deleted = attributes.getValue("deleted");
			if("1".equals(deleted) || "2".equals(deleted)){
				item_kicked = true;
				return;
			}
			//NG���L���x���Ώۍ폜
			if(ng_Score>SharedNgScore.MINSCORE){
				String score_str = attributes.getValue("score");
				int score = 0;
				if (score_str != null){
					try {
						score = Integer.parseInt(score_str);
					} catch(NumberFormatException e){
						score = 0;
					}
				}
				if(score <= ng_Score){
					item_kicked = true;
					countNG_Score++;
					return;
				}
			}
			// item.setDate(attributes.getValue("date"));
			String mail = attributes.getValue("mail");
			//184�i�����j�łȂ��ꍇ�́A186�i�ʒm�j���Ō�ɕt��
			if(mail==null){
				mail = "186";
			}else if(!mail.contains("184")){
				mail += " 186";
			}
			if(mail.contains("is_button")){
				is_button = true;
			}
			if (match(NG_Word, mail)) {
				item_kicked = true;
				countNG_Word++;
				return;
			}
			mail = NG_Cmd.replace(mail);
			item.setMail(mail);
			item.setNo(attributes.getValue("no"));
			String user_id = attributes.getValue("user_id");
			if (match(NG_ID, user_id)) {
				item_kicked = true;
				countNG_ID++;
				return;
			}
			String forkval = attributes.getValue("fork");
			if (forkval != null && forkval.equals("1")) {
				item_fork = true;
			}
			item.setOwner(item_fork);
			// item.setUserID(user_id);
			item.setVpos(attributes.getValue("vpos"));

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
	@Override
	public void characters(char[] ch, int offset, int length) {
		if (item != null) {
			for (int i = offset; i < offset + length; i++) {
				if (!Character.isDefined(ch[i])) {
					ch[i] = '?';
				}
			}
			String com = new String(ch, offset, length);
			boolean script = false;
			//�j�����ꏈ��
			if (item_fork && com.startsWith("/")){
				String[] lines = com.substring(1).split(";");
				for(String nicos: lines){
					if(nicos.startsWith("replace")){
						com = "/" + nicos;
						break;
					}
				}
				if(!com.startsWith("/")){
					item_kicked = true;
					return;
				}
				if(com.startsWith("replace(",1) && com.endsWith(")")){
					// /replace���� �R�����g�� /replace()�����̏ꍇ
					System.out.println("Converting: " + com);
					item.addCmd(Chat.CMD_LOC_SCRIPT);
					script = true;
					com = com.replaceFirst("^/replace\\(", "").replaceFirst("\\)[ �@]*$", "");
					String[] terms = com.split(",");
					if(terms.length<2){
						item_kicked = true;
						return;
					}
					String src = "";
					String dest = "";
					String target = "";
					String enabled = "T";
					String fill = "F";
					String partial = "T";
					String rcolor = "";
					String rsize = "";
					String rpos = "";
					String ret;
					for(String term:terms){
			//			System.out.println("Converting3: " + term);
						if ((ret = niwango(term,"src:"))!=null){
							src = ret;
						}
						else if((ret = niwango(term, "dest:"))!=null){
							dest = ret;
						}
						else if((ret = niwango(term, "enabled:"))!=null){
							enabled = CommentReplace.encodeBoolean(ret);
						}
						else if((ret = niwango(term, "target:"))!=null){
							target = ret;
							if(target.contains("owner"))
								item.addCmd(Chat.CMD_LOC_SCRIPT_FOR_OWNER);
							if(target.contains("user"))
								item.addCmd(Chat.CMD_LOC_SCRIPT_FOR_USER);
						}
						else if((ret = niwango(term, "fill:"))!=null){
							fill = CommentReplace.encodeBoolean(ret);
						}
						else if((ret = niwango(term, "partial:"))!=null){
							partial = CommentReplace.encodeBoolean(ret);
						}
						else if((ret = niwango(term, "color:"))!=null){
							rcolor = ret;
						}
						else if((ret = niwango(term, "size:"))!=null){
							rsize = ret;
						}
						else if((ret = niwango(term, "pos:"))!=null){
							rpos = ret;
						}
						else{
							System.out.println("Warning: /replace() contains " + term);
						}
					}
					int vpos = item.getVpos();
					System.out.println("Converted:" +vpos +":/replace(src:"+src +",dest:"+dest
						+",enabled:"+enabled +",target:"+target +",fill:"+fill +",partial:"+partial
						+",color:"+rcolor +",size:" +rsize+",pos:" +rpos+").");
					com = "/r," + src + "," + dest + "," + fill;
					System.out.println("Converted-Comment: " + com);
					CommentReplace comrpl = new CommentReplace(item, src, dest, enabled, partial,
						target, fill, item.getVpos(), rcolor, rsize, rpos);
					packet.addReplace(comrpl);
				}else{
					//ignore NIWANGO temporary
					item_kicked = true;
					return;
				}
			}
			//�j�R�X�N���v�g����
			if(com.startsWith("@")||com.startsWith("��")){
				if(item_fork){
					if(com.startsWith("�t",1)){
						//�t��
						item.addCmd(Chat.CMD_LOC_SCRIPT);
						script = true;
					}else if(com.startsWith("�f�t�H���g",1)){
						//�f�t�H���g�l�ݒ�
						item.addCmd(Chat.CMD_LOC_SCRIPT);
						script = true;
					}
				}
			}
			if(is_button){
				item.addCmd(Chat.CMD_LOC_SCRIPT);
				script = true;
			}
			//NG���[�h
			if (!script && match(NG_Word, com)) {
				item_kicked = true;
				countNG_Word++;
				return;
			}
			item.setComment(com);
		}
	}

	private String niwango(String str, String key){
		if(str==null) return null;
		int len = str.length();
		if(str.startsWith(key)){
			int index = key.length();
			if(len<=index){
				return null;
			}
			char c1 = str.charAt(index);
			if(c1=='\''|| c1=='\"'){
				index++;
				if(str.charAt(len-1)!=c1){
					return null;
				}
				len--;
			}
			str = str.substring(index,len);
			str = str.replace("\\r", "\n").replace("\\n", "\n").replace("\\t", "\t");
			return str;
		}
		return null;
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
	@Override
	public void endElement(String uri, String localName, String qName) {
		if (qName.toLowerCase().equals("chat")) {
			if (!item_kicked) {
				packet.addChat(item);
			}
			item = null;
		}
	}

	/**
	 * �h�L�������g�I��
	 */
	@Override
	public void endDocument() {
		// System.out.println("----------");
		System.out.println("Converting finished. "
			+ packet.size() + " items.");
		System.out.println("Deleted NG Word:"+countNG_Word
			+" ID:"+countNG_ID+" Score:"+countNG_Score);
	}

}
