package saccubus.conv;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import saccubus.SharedNgScore;

/**
 * <p>
 * タイトル: さきゅばす
 * </p>
 *
 * <p>
 * 説明: ニコニコ動画の動画をコメントつきで保存
 * </p>
 *
 * <p>
 * 著作権: Copyright (c) 2007 PSI
 * </p>
 *
 * <p>
 * 会社名:
 * </p>
 *
 * @author 未入力
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
			//投稿者フィルター
			owner_filter = attributes.getValue("filter");
			if(owner_filter!=null){
				item_fork = true;
				return;
			}
			//マイメモリ削除対象
			//運営削除対象
			String deleted = attributes.getValue("deleted");
			if("1".equals(deleted) || "2".equals(deleted)){
				item_kicked = true;
				return;
			}
			//NG共有レベル対象削除
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
			//184（匿名）でない場合は、186（通知）を最後に付加
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
			//ニワン語処理
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
					// /replace実装 コメントは /replace()だけの場合
					System.out.println("Converting: " + com);
					item.addCmd(Chat.CMD_LOC_SCRIPT);
					script = true;
					com = com.replaceFirst("^/replace\\(", "").replaceFirst("\\)[ 　]*$", "");
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
					item.setMail(rcolor + " " + rsize + " " + rpos);
					//System.out.println("Converted-Comment: " + com);
					CommentReplace comrpl = new CommentReplace(item,src,dest,enabled,partial,target,fill);
					packet.addReplace(comrpl);
				}else{
					//ignore NIWANGO temporary
					item_kicked = true;
					return;
				}
			}
			//ニコスクリプト処理
			if(com.startsWith("@")||com.startsWith("＠")){
				if(item_fork){
					if(com.startsWith("置換",1)){
						//置換
						item.setMail("");	//リセットサイズ、ロケーション、色
						item.addCmd(Chat.CMD_LOC_SCRIPT);
						script = true;
						System.out.println("Converting＠置換: " + com);
						com = com.replaceFirst("^[@＠]置換[ 　]", "");
						String src = "";
						String dest = "";
						String fill = "F";
						String target = "user";
						item.addCmd(Chat.CMD_LOC_SCRIPT_FOR_USER);
						String partial = "T";
						String[] list = spsplit(com,6);
						src = list[0];
						dest = list[1];
						int p = 2;
						if(list[p].startsWith("全")){
							fill = "T";
							p++;
						}
						if(list[p].startsWith("単")){
							fill = "F";
							p++;
						}
						if(list[p].startsWith("含む")){
							item.addCmd(Chat.CMD_LOC_SCRIPT_FOR_OWNER);
							target = "user owner";
							p++;
						}
						if(list[p].startsWith("含まない")){
							target = "user";
							p++;
						}
						if(list[p].startsWith("部分一致")){
							partial = "T";
							p++;
						}
						if(list[p].startsWith("完全一致")){
							partial = "F";
						}
						int vpos = item.getVpos();
						item.setMail("");	//リセットサイズ、ロケーション、色
						item.addCmd(Chat.CMD_LOC_SCRIPT);
						System.out.println("Converted:" +vpos +":＠置換 「"+src +"」「 "+dest
							+"」 "+target +" fill:"+fill +" partial:"+partial+").");
						com = "/r," + src + "," + dest + "," + fill;
						CommentReplace comrpl = new CommentReplace(item,src,dest,"T",partial,target,fill);
						packet.addReplace(comrpl);
					}
					if(com.startsWith("逆",1)){
						//逆走
						item.addCmd(Chat.CMD_LOC_SCRIPT);
						script = true;
					}else if(com.startsWith("デフォルト",1)){
						//デフォルト値設定
						item.addCmd(Chat.CMD_LOC_SCRIPT);
						script = true;
					}else if(com.startsWith("ボタン",1)){
						//ボタン（投稿者用）
						item.addCmd(Chat.CMD_LOC_SCRIPT);
						is_button = true;
						//itemに追加
						item.addCmd(Chat.CMD_LOC_IS_BUTTON);
						com = com.replaceFirst("^.ボタン[ 　]+", "");
						char c0 = com.charAt(0);
						int index1 = -2;
						if(c0=='「')
							index1 = com.indexOf('」');
						else if(c0=='"')
							index1 = com.indexOf('"');
						if(index1 > 0)
							com = com.substring(1,index1);
						else if(index1 == -1)
							com = com.substring(1);
						else
							com = com.replaceFirst("[ 　].*$", "");
						if(!com.contains("[")){
							//文字列全体をボタン表示するには[]で全体を囲む
							com = "[" + com + "]";
						}
						script = true;
					}
				}
			}
			//ボタン　視聴者、投稿者共
			if(is_button){
				item.addCmd(Chat.CMD_LOC_SCRIPT);
				script = true;
			}
			//投稿者フィルター
			if (owner_filter!=null){
				item.setVpos("0");
				item.setMail("");	//リセットサイズ、ロケーション、色
				item.addCmd(Chat.CMD_LOC_SCRIPT);
				item.addCmd(Chat.CMD_LOC_SCRIPT_FOR_USER);
				item.addCmd(Chat.CMD_LOC_SCRIPT_FOR_OWNER);
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
						System.out.println("Converted:0" +":フィルター \""+src +"\" \""+dest
								+"\" "+target +" fill:"+fill +" partial:T).");
						com = "/r," + src + "," + dest + "," + fill;
						CommentReplace comrpl = new CommentReplace(item, src, dest, "T", "T", target, fill);
						packet.addReplace(comrpl);
					}
				}
			}
			//NGワード
			if (!script && match(NG_Word, com)) {
				item_kicked = true;
				countNG_Word++;
				return;
			}
			item.setComment(com);
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
			if(intext.length() > 1 && (h=='"'||h=='「')){
				if(h=='「')
					h = '」';
				index = intext.indexOf(h,1);
				if(index >= 1){
					ret[i] = intext.substring(1, index);
					intext = intext.substring(index+1);
					if(!intext.isEmpty())
						if(intext.charAt(0)==' '||intext.charAt(0)=='　')
							intext = intext.substring(1);
					continue;
				}
			}
			index = intext.indexOf(' ');
			index1 = intext.indexOf('　');
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
			if (!item_kicked) {
				packet.addChat(item);
			}
			item = null;
		}
	}

	/**
	 * ドキュメント終了
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
