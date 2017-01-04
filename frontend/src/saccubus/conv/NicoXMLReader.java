package saccubus.conv;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import saccubus.SharedNgScore;
import saccubus.util.Logger;

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

	private String owner_filter;

	private final int ng_Score;
	private int countNG_Score = 0;
	private int countNG_Word = 0;
	private int countNG_ID = 0;
	private StringBuffer sb;

	private String premium;
	private final boolean liveConversion;
	private final boolean premiumColorCheck;

	private int voteN = 0;
	private String[] voteStr = null;
	private String[] voteRate = null;

	private String liveOpDuration = "";	// 0�ȏ�Ȃ�K�p
	private Logger log;
	private String duration = "";

	public NicoXMLReader(Packet packet, Pattern ngIdPat, Pattern ngWordPat, CommandReplace cmd,
		int scoreLimit, boolean liveOp, boolean prem_color_check, String duration, Logger logger){
		this.packet = packet;
		NG_Word = ngWordPat;
		NG_ID = ngIdPat;
		NG_Cmd = cmd;
		ng_Score = scoreLimit;
		owner_filter = null;
		premium = "";
		liveConversion = liveOp;
		// �j�R�X�R�����g�� premium "2" or "3"�݂����Ȃ̂Ńj�R�X�R�����g�̎��͉^�c�R�����g�ϊ����Ȃ��悤�ɂ���
		premiumColorCheck = prem_color_check;
		liveOpDuration = duration;
		log = logger;
	}

	public static final Pattern makePattern(String word, Logger logger) throws PatternSyntaxException{
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
			logger.println(e);
			if (i > 0) {
				regb.append("|");
			}
			if (e.length() > 1 && e.startsWith("/") && e.endsWith("/")) {
				regb.append("(" + e.substring(1, e.length() - 1) + ")");
			} else if (e.length() > 1 && e.startsWith("\"") && e.endsWith("\"")) {
				regb.append("(.*(" + Pattern.quote(e.substring(1, e.length() - 1))
						+ ")+.*)");
			} else {
				regb.append("(.*(" + Pattern.quote(e) + ")+.*)");
			}
		}
		String reg = regb.substring(0);
		logger.println("reg:" + reg);
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
		log.println("Start converting to intermediate file.");
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
		// log.println("<"+qName);
		// for (int i = 0; i < attributes.getLength(); i++) {
		// 	log.println(" [" + attributes.getQName(i)+"=" + attributes.getValue(i)+"]");
		// }
		// log.println(">");
		if (qName.toLowerCase().equals("chat")) {
			// log.println("----------");
			item = new Chat(log);
			item_kicked = false;
			item_fork = false;
			is_button = false;
			premium = "";
			sb = new StringBuffer();
			//���e�҃t�B���^�[
			owner_filter = attributes.getValue("filter");
			if(owner_filter!=null){
				item_fork = true;
				return;
			}
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
			String user_id = attributes.getValue("user_id");
			if (match(NG_ID, user_id)) {
				item_kicked = true;
				countNG_ID++;
				return;
			}
			if (match(NG_Word, mail)) {
				item_kicked = true;
				countNG_Word++;
				return;
			}
			mail = NG_Cmd.replace(mail);
			if(mail.contains("@")){
				Matcher m = Pattern.compile(".*@([0-9]+).*").matcher(mail);
				if(m.matches())
					duration = m.group(1);
			}
			else if(liveConversion && !liveOpDuration.isEmpty())
				duration = liveOpDuration;
			item.setMail(mail);
			item.setNo(attributes.getValue("no"));
			String forkval = attributes.getValue("fork");
			if (forkval != null && forkval.equals("1")) {
				item_fork = true;
			}
			if(liveConversion && premium.isEmpty()){	//here premium!=null
				premium = attributes.getValue("premium");
			}
			if(premium==null)	//here premium may be null
				premium = "";
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
			sb.append(com);
		}
	}

	private String[] spsplit(String intext, int max){
		String[] ret = new String[max];
		for(int i = 0; i < ret.length; i++){
			ret[i] = "";
		}
		if(intext==null || intext.isEmpty()){
			return ret;
		}
		for(int i = 0; i < ret.length; i++){
			int index = 0;
			int index1 = 0;
			if(intext.isEmpty())
				return ret;
			char h = intext.charAt(0);
			if(intext.length() > 1 && (h=='"'||h=='�u')){
				if(h=='�u')
					h = '�v';
				index = intext.indexOf(h,1);
				if(index >= 1){
					ret[i] = intext.substring(1, index);
					intext = intext.substring(index+1);
					if(!intext.isEmpty())
						if(intext.charAt(0)==' '||intext.charAt(0)=='�@')
							intext = intext.substring(1);
					continue;
				}
			}
			index = intext.indexOf(' ');
			index1 = intext.indexOf('�@');
			if(index < 0 || (index1 >= 0 && index1 < index))
				index = index1;
			if(index < 0) {
				ret[i] = intext;
				return ret;
			}
			ret[i] = intext.substring(0, index);
			intext = intext.substring(index+1);
		}
		if(!intext.isEmpty()){
			ret[max-1] = ret[max-1] + intext;
		}
		return ret;
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
			String com = sb.substring(0);
			// log.println("\t| "+com+" |");
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
					log.println("Converting: " + com);
					item.setScript();
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
			//			log.println("Converting3: " + term);
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
								item.setScriptForOwner();
							if(target.contains("user"))
								item.setScriptForUser();
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
							log.println("Warning: /replace() contains " + term);
						}
					}
					int vpos = item.getVpos();
					log.println("Converted:" +vpos +":/replace(src:"+src +",dest:"+dest
						+",enabled:"+enabled +",target:"+target +",fill:"+fill +",partial:"+partial
						+",color:"+rcolor +",size:" +rsize+",pos:" +rpos+").");
					com = "/r," + src + "," + dest + "," + fill;
					item.setMail(rcolor + " " + rsize + " " + rpos);
					//log.println("Converted-Comment: " + com);
					CommentReplace comrpl = new CommentReplace(item,src,dest,enabled,partial,target,fill,log);
					packet.addReplace(comrpl);
				}else{
					//ignore NIWANGO temporary
					item_kicked = true;
					return;
				}
			}
			//�^�c�R�����g
			if(liveConversion && !premium.isEmpty() && !premium.equals("1")){
				if(com.startsWith("/")){
					//�^�c�R�}���h premium="3" or "6" only? not check
					String[] list = com.trim().split(" +");
					if(list[0].equals("/perm")){
						// prem
						if(duration.isEmpty())
							duration = "10";
						item.setMail("ue ender @"+duration);
					}
					else if(list[0].equals("/vote")){
						int lfLim = 4;
						String VOTECMD = "ue cyan ender full";
						// vote
						// ���l��ϊ�����
						// ���[�������e�s��
						if(list.length>1 && list[1].equals("start")){
							///vote start �����̔ԑg�͂������ł������H �ƂĂ��ǂ����� �܂��܂��ǂ����� �ӂ������� ���܂�ǂ��Ȃ����� �ǂ��Ȃ�����
							voteN = list.length-2;
							voteStr = new String[voteN];
							voteStr[0] = list[2];
							for(int i=1; i<voteN; i++){
								voteStr[i] = (list[i+2] + "          ")
												.substring(0, 9);
							}
							voteRate = new String[voteN];
							StringBuffer sb = new StringBuffer();
							sb.append(voteStr[0]);
							sb.append("\n");
							lfLim = 4;
							if(voteN==5) lfLim = 3;
							for(int i=1; i<voteN;i++){
								if(i == lfLim){
									sb.append("\n");
									lfLim = 7;
								}
								sb.append("[            \n");
								sb.append(Integer.toString(i));
								sb.append(".");
								sb.append(voteStr[i]);
								sb.append("\n");
								sb.append(" \n");
								sb.append("]");
							}
							com = "/vote start "+sb.substring(0).trim();
							item.setMail(VOTECMD);
							item.setScript();
							item_fork = true;
							script = true;
						}
						else if(list.length>1 && list[1].equals("showresult")){
							///vote showresult per  602 181 60 47 108 0 0 0 0 0
							//  15�b���炢
							if(voteN==1 || voteStr==null || voteRate==null){
								voteN = 6;
								voteStr = new String[]{
									"�����̔ԑg�͂������ł������H",
									"�@�ƂĂ��ǂ������@",
									"�܂��܂��ǂ������@",
									"�@�ӂ��������@�@",
									"���܂�ǂ��Ȃ�����",
									"�@�ǂ��Ȃ������@�@"
								};
								voteRate = new String[voteN];
							}
							for(int i=1; i<voteN; i++){
								String s = list[i+2].trim();
								int rate = 0;
								voteRate[i] = "           %";
								if(s!=null && !s.isEmpty()){
									try{
										rate = Integer.valueOf(s);
									}catch(NumberFormatException e){
										log.printStackTrace(e);
									}
								}
								voteRate[i] = String.format("% 11.1f%%",(rate/10.0));
							}
							StringBuffer sb = new StringBuffer();
							sb.append(voteStr[0]);
							sb.append("\n");
							lfLim = 4;
							if(voteN==5) lfLim = 3;
							for(int i=1; i<voteN;i++){
								if(i == lfLim){
									sb.append("\n");
									lfLim = 7;
								}
								sb.append("[            \n");
								sb.append(Integer.toString(i));
								sb.append(".");
								sb.append(voteStr[i]);
								sb.append("\n");
								sb.append(voteRate[i]);
								sb.append("\n");
								sb.append("]");
							}
							com = "/vote show "+sb.substring(0).trim();
							item.setMail(VOTECMD);
							item.setScript();
							item_fork = true;
							script = true;
						}
						else if(list.length>1 && list[1].equals("stop")){
							com = "/vote stop";
							item.setMail(VOTECMD);
							item.setScript();
							item_fork = true;
							script = true;
						}
						else {
							// /vote ���̑�
							StringBuffer sb = new StringBuffer();
							for(int i=1; i<list.length; i++){
								sb.append(list[i]);
							}
							com = "/vote [" + sb.substring(0) + "]";
							item.setMail(VOTECMD);
							item.setScript();
							item_fork = true;
							script = true;
						}
					}
					else {
						//�^�c�R�}���h�Y������
						// /disconnect �Ȃ�?
						if(duration.isEmpty())
							duration = "4";
						item.setMail("ue ender @"+duration);
						item_fork = true;
						if(com.contains("�v"))
							com = "@�{�^�� �u["+com.replaceAll("�u", "�w").replaceAll("�v", "�x")+"]�v";
						else
							com = "@�{�^�� �u["+com+"]�v";
					}
				}
				else if(!premium.isEmpty() && !premium.equals("1")){
					//�^�c�R�����g(���� or BSP?) �R�}���h�Ȃ�
					// premium="3" or "6" ? not check
					if(duration.isEmpty())
						duration = "12";
					item.setMail("ue ender @"+duration);	//4�b�ł����H
					item_fork = true;
					if(com.contains("�v"))
						com = "@�{�^�� �u["+com.replaceAll("�u", "�w").replaceAll("�v", "�x")+"]�v";
					else
						com = "@�{�^�� �u["+com+"]�v";
				}
				com = com.replace("<u>", "\n").replace("</u>", "")
					.replace("<b>", "\n").replace("</b>", "")
					.replace("<font ", "\n<font ").replace("</font>", "")
					.replace("<br>", "\n").replace("<br />", "\n").replace("<br/>", "\n")
					.replaceAll("<a href=([^>]+)>","\n$1\n").replace("</a>","\n")
					.replaceAll("<.>", "\n").replaceAll("</.>", "")
					.replaceAll("\n+","\n");
			}
			//�j�R�X�N���v�g����
			if(com.startsWith("@")||com.startsWith("��")){
				if(item_fork){
					if(com.startsWith("�u��",1)){
						//�u��
						//item.setMail("");	//���Z�b�g�T�C�Y�A���P�[�V�����A�F
						item.setScript();
						script = true;
						log.println("Converting���u��: " + com);
						com = com.replaceFirst("^[@��]�u��[ �@]", "");
						String src = "";
						String dest = "";
						String fill = "F";
						String target = "user";
						item.setScriptForUser();
						String partial = "T";
						String[] list = spsplit(com,6);
						src = list[0];
						dest = list[1];
						int p = 2;
						if(list[p].startsWith("�S")){
							fill = "T";
							p++;
						}
						if(list[p].startsWith("�P")){
							fill = "F";
							p++;
						}
						if(list[p].startsWith("�܂�")){
							item.setScriptForOwner();
							target = "user owner";
							p++;
						}
						if(list[p].startsWith("�܂܂Ȃ�")){
							target = "user";
							p++;
						}
						if(list[p].startsWith("������v")){
							partial = "T";
							p++;
						}
						if(list[p].startsWith("���S��v")){
							partial = "F";
						}
						int vpos = item.getVpos();
						//item.setMail("");	//���Z�b�g�T�C�Y�A���P�[�V�����A�F
						item.setScript();
						log.println("Converted:" +vpos +":���u�� �u"+src +"�v�u "+dest
							+"�v "+target +" fill:"+fill +" partial:"+partial+").");
						com = "/r," + src + "," + dest + "," + fill;
						CommentReplace comrpl = new CommentReplace(item,src,dest,"T",partial,target,fill,log);
						packet.addReplace(comrpl);
					}
					if(com.startsWith("�t",1)){
						//�t��
						item.setScript();
						script = true;
					}else if(com.startsWith("�f�t�H���g",1)){
						//�f�t�H���g�l�ݒ�
						item.setScript();
						script = true;
					}else if(com.startsWith("�{�^��",1)){
						//�{�^���i���e�җp�j
						item.setScript();
						is_button = true;
						//item�ɒǉ�
						item.setButton();
						com = com.replaceFirst("^.�{�^��[ �@]+", "");
						char c0 = com.charAt(0);
						int index1 = -2;
						if(c0=='�u')
							index1 = com.indexOf('�v');
						else if(c0=='"')
							index1 = com.indexOf('"');
						if(index1 > 0)
							com = com.substring(1,index1);
						else if(index1 == -1)
							com = com.substring(1);
						else
							com = com.replaceFirst("[ �@].*$", "");
						if(!com.contains("[")){
							//������S�̂��{�^���\������ɂ�[]�őS�̂��͂�
							com = "[" + com + "]";
						}
						script = true;
					}
				}
			}
			//�{�^���@�����ҁA���e�ҋ�
			if(is_button){
				item.setScript();
				script = true;
			}
			//���e�҃t�B���^�[
			if (owner_filter!=null){
				item.setVpos("0");
				item.setMail("");	//���Z�b�g�T�C�Y�A���P�[�V�����A�F
				item.setScript();
				item.setScriptForUser();
				item.setScriptForOwner();
				script = true;
				String[] list = com.split("&");
				for(String pair:list){
					if(pair.contains("=")){
						String src = pair.substring(0, pair.indexOf("="));
						String dest = pair.substring(pair.indexOf("=")+1);
						try {
							src = URLDecoder.decode(src, "UTF-8");
							dest = URLDecoder.decode(dest, "UTF-8");
						} catch (UnsupportedEncodingException e) {
							continue;
						}
						String target = "user owner";
						String fill = "F";
						if(src.charAt(0)=='*'){
							fill = "T";
							src = src.substring(1);
						}
						log.println("Converted:0" +":�t�B���^�[ \""+src +"\" \""+dest
								+"\" "+target +" fill:"+fill +" partial:T).");
						com = "/r," + src + "," + dest + "," + fill;
						CommentReplace comrpl = new CommentReplace(item, src, dest, "T", "T", target, fill, log);
						packet.addReplace(comrpl);
					}
				}
			}
			//NG���[�h
			if (!script && match(NG_Word, com)) {
				item_kicked = true;
				countNG_Word++;
				return;
			}
			//�v���~�A����p�J���[�@��ʃA�J�E���g�ł͖���
			if (premiumColorCheck && (premium==null || premium.isEmpty())){
				if(item.isPremumColor()){
					item.setDefColor();
				}
			}
			item.setComment(com);
			// log.println("\tpreimum="+premium+"| item="+item.toString()+" |");
			if (!item_kicked) {
				packet.addChat(item);
			}
			item = null;
			// log.println("</"+qName+">");
		}
	}

	/**
	 * �h�L�������g�I��
	 */
	@Override
	public void endDocument() {
		// log.println("----------");
		log.println("Converting finished. "
			+ packet.size() + " items.");
		log.println("Deleted NG Word:"+countNG_Word
			+" ID:"+countNG_ID+" Score:"+countNG_Score);
	}

}
