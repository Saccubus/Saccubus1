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
	private StringBuffer sb;

	private String premium;
	private final boolean liveConversion;
	private final boolean premiumColorCheck;

	private int voteN = 0;
	private String[] voteStr = null;
	private String[] voteRate = null;

	private String liveOpDuration = "";	// 0以上なら適用
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
		// ニコスコメントは premium "2" or "3"みたいなのでニコスコメントの時は運営コメント変換しないようにする
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
			String com = sb.substring(0);
			// log.println("\t| "+com+" |");
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
					log.println("Converting: " + com);
					item.setScript();
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
			//運営コメント
			if(liveConversion && !premium.isEmpty() && !premium.equals("1")){
				if(com.startsWith("/")){
					//運営コマンド premium="3" or "6" only? not check
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
						// 数値を変換する
						// 投票数字を各行に
						if(list.length>1 && list[1].equals("start")){
							///vote start 今日の番組はいかがでしたか？ とても良かった まぁまぁ良かった ふつうだった あまり良くなかった 良くなかった
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
							//  15秒ぐらい
							if(voteN==1 || voteStr==null || voteRate==null){
								voteN = 6;
								voteStr = new String[]{
									"今日の番組はいかがでしたか？",
									"　とても良かった　",
									"まぁまぁ良かった　",
									"　ふつうだった　　",
									"あまり良くなかった",
									"　良くなかった　　"
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
							// /vote その他
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
						//運営コマンド該当無し
						// /disconnect など?
						if(duration.isEmpty())
							duration = "4";
						item.setMail("ue ender @"+duration);
						item_fork = true;
						if(com.contains("」"))
							com = "@ボタン 「["+com.replaceAll("「", "『").replaceAll("」", "』")+"]」";
						else
							com = "@ボタン 「["+com+"]」";
					}
				}
				else if(!premium.isEmpty() && !premium.equals("1")){
					//運営コメント(生主 or BSP?) コマンドなし
					// premium="3" or "6" ? not check
					if(duration.isEmpty())
						duration = "12";
					item.setMail("ue ender @"+duration);	//4秒でいい？
					item_fork = true;
					if(com.contains("」"))
						com = "@ボタン 「["+com.replaceAll("「", "『").replaceAll("」", "』")+"]」";
					else
						com = "@ボタン 「["+com+"]」";
				}
				com = com.replace("<u>", "\n").replace("</u>", "")
					.replace("<b>", "\n").replace("</b>", "")
					.replace("<font ", "\n<font ").replace("</font>", "")
					.replace("<br>", "\n").replace("<br />", "\n").replace("<br/>", "\n")
					.replaceAll("<a href=([^>]+)>","\n$1\n").replace("</a>","\n")
					.replaceAll("<.>", "\n").replaceAll("</.>", "")
					.replaceAll("\n+","\n");
			}
			//ニコスクリプト処理
			if(com.startsWith("@")||com.startsWith("＠")){
				if(item_fork){
					if(com.startsWith("置換",1)){
						//置換
						//item.setMail("");	//リセットサイズ、ロケーション、色
						item.setScript();
						script = true;
						log.println("Converting＠置換: " + com);
						com = com.replaceFirst("^[@＠]置換[ 　]", "");
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
						if(list[p].startsWith("全")){
							fill = "T";
							p++;
						}
						if(list[p].startsWith("単")){
							fill = "F";
							p++;
						}
						if(list[p].startsWith("含む")){
							item.setScriptForOwner();
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
						//item.setMail("");	//リセットサイズ、ロケーション、色
						item.setScript();
						log.println("Converted:" +vpos +":＠置換 「"+src +"」「 "+dest
							+"」 "+target +" fill:"+fill +" partial:"+partial+").");
						com = "/r," + src + "," + dest + "," + fill;
						CommentReplace comrpl = new CommentReplace(item,src,dest,"T",partial,target,fill,log);
						packet.addReplace(comrpl);
					}
					if(com.startsWith("逆",1)){
						//逆走
						item.setScript();
						script = true;
					}else if(com.startsWith("デフォルト",1)){
						//デフォルト値設定
						item.setScript();
						script = true;
					}else if(com.startsWith("ボタン",1)){
						//ボタン（投稿者用）
						item.setScript();
						is_button = true;
						//itemに追加
						item.setButton();
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
				item.setScript();
				script = true;
			}
			//投稿者フィルター
			if (owner_filter!=null){
				item.setVpos("0");
				item.setMail("");	//リセットサイズ、ロケーション、色
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
						log.println("Converted:0" +":フィルター \""+src +"\" \""+dest
								+"\" "+target +" fill:"+fill +" partial:T).");
						com = "/r," + src + "," + dest + "," + fill;
						CommentReplace comrpl = new CommentReplace(item, src, dest, "T", "T", target, fill, log);
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
			//プレミアム専用カラー　一般アカウントでは無効
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
	 * ドキュメント終了
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
