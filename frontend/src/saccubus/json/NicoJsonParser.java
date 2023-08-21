package saccubus.json;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import saccubus.conv.ChatAttribute;
import saccubus.conv.ChatSave;
import saccubus.net.Path;
import saccubus.util.Logger;
import saccubus.util.Util;

//Response Data =
//[
//	{"ping": {"content": "rs:0"}},
//	{"ping": {"content": "ps:0"}},
//	{"thread": {
//		"resultcode": 0,
//		"thread": "9999999993",
//		"server_time": 1484681074,
//		"last_res": 7496,
//		"ticket": "0x38b3de9b",
//		"revision": 1,
//		"click_revision": 48}
//	},
//	{"leaf": {"thread": "9999999993","count": 5140}},
//	{"leaf": {"thread": "9999999993","leaf": 1,"count": 2140}},
//	{"leaf": {"thread": "9999999993","leaf": 2,"count": 216}},
//	{"global_num_res": {"thread": "9999999993","num_res": 7670}},
//	{"ping": {"content": "pf:0"}},
//	{"ping": {"content": "ps:1"}},
//	{"thread": {
//		"resultcode": 0,
//		"thread": "9999999993",
//		"server_time": 1484681074,
//		"last_res": 7496,
//		"ticket": "0x38b3de9b",
//		"revision": 1,
//		"click_revision": 48}
//	},
//	{"chat": {
//		"thread": "9999999993",
//		"no": 4877,
//		"vpos": 13854,
//		"leaf": 2,
//		"date": 1401116767,
//		"premium": 1,
//		"anonymity": 1,
//		"user_id": ユーザーID_4877,
//		"mail": "184",
//		"content": 通常コメント4877}
//	},
//	中略
//	{"chat": {
//		"thread": "9999999993",
//		"no": 7496,
//		"vpos": 2426,
//		"date": 1484578038,
//		"date_usec": 959793,
//		"anonymity": 1,
//		"user_id": "wyT4hdcnpRa5gKm7EiagcCsO20A",
//		"mail": "184",
//		"content": 通常コメント7496}
//	},
//	{"ping": {"content": "pf:1"}},
//	{"ping": {"content": "ps:2"}},
//	{"thread": {
//		"resultcode": 0,
//		"thread": "9999999994",
//		"server_time": 1484681074,
//		"last_res": 52,
//		"ticket": "0x365b5d16",
//		"revision": 1}
//	},
//	{"leaf": {"thread": "9999999994","count": 41}},
//	{"leaf": {"thread": "9999999994","leaf": 1,"count": 10}},
//	{"leaf": {"thread": "9999999994","leaf": 2,"count": 1}},
//	{"global_num_res": {"thread": "9999999994","num_res": 52}},
//	{"ping": {"content": "pf:2"}},
//	{"ping": {"content": "ps:3"}},
//	{"thread": {
//		"resultcode": 0,
//		"thread": "9999999994",
//		"server_time": 1484681074,
//		"last_res": 52,
//		"ticket": "0x365b5d16",
//		"revision": 1}
//	},
//	{"chat": {
//		"thread": "9999999994",
//		"no": 1,
//		"vpos": 4014,
//		"date": 1324739748,
//		"premium": 1,
//		"anonymity": 1,
//		"user_id": ユーザーID_1,
//		"content": コミュニティコメント1}
//	},
//	中略
//	{"chat": {
//		"thread": "9999999994",
//		"no": 51,
//		"vpos": 5791,
//		"date": 1474285779,
//		"date_usec": 890564,
//		"premium": 1,
//		"anonymity": 1,
//		"user_id": ユーザーID_51,
//		"content": コミュニティコメント51}
//	},
//	{"ping": {"content": "pf:3"}},
//	{"ping": {"content": "rf:0"}}
//]
//

public class NicoJsonParser {

	private final static String ENCODING = "UTF-8";
	private final Logger log;
	private int chatCount;

	public NicoJsonParser(Logger logger) {
		log = logger;
	}

	public int getChatCount(){
		return chatCount;
	}
	public boolean commentJson2xml(File json, File xml, String kind, boolean append){
		return commentJson2xml(json, xml, kind, append, false);
	}
	public boolean nvcommentJson2xml(File json, File xml, String kind, boolean append){
		return nvcommentJson2xml(json, xml, kind, append, false);
	}
	public boolean commentJson2xml(File json, File xml, String kind, boolean append, boolean localconv) {
		// json 入力コメントJsonテキストファイル
		// xml 出力コメントxmlファイル
		String jsonText = Path.readAllText(json, ENCODING);
		Mson mson = Mson.parse(jsonText);
		//mson.prettyPrint(log);
		FileOutputStream fos = null;
		OutputStreamWriter ow = null;
		try {
			String xmlString = makeCommentXml(mson, kind, localconv);
			if(xmlString!=null){
				fos = new FileOutputStream(xml, append);
				ow = new OutputStreamWriter(fos, ENCODING);
				ow.write(xmlString);
				ow.flush();
				ow.close();
				log.println(kind+"コメントJSONをXMLに変換しました: "+xml.getPath());
				return true;
			}
		} catch (IOException e) {
			log.printStackTrace(e);
		} finally {
			if(ow!=null) try {ow.close();}catch(IOException e){};
			if(fos!=null) try {fos.close();}catch(IOException e){};
		}
		return false;
	}

	//2022.06ぐらいから？のニコ動新サーバー(nvcomment)のJSON→XML変換
	public boolean nvcommentJson2xml(File json, File xml, String kind, boolean append, boolean localconv) {
		// json 入力コメントJsonテキストファイル(nvcomment)
		// xml 出力コメントxmlファイル
		String jsonText = Path.readAllText(json, ENCODING);
		Mson mson = Mson.parse(jsonText);
		//mson.prettyPrint(log);
		FileOutputStream fos = null;
		OutputStreamWriter ow = null;
		try {
			String xmlString = makeNvCommentXml(mson, kind, localconv);
			if(xmlString!=null){
				fos = new FileOutputStream(xml, append);
				ow = new OutputStreamWriter(fos, ENCODING);
				ow.write(xmlString);
				ow.flush();
				ow.close();
				log.println(kind+"コメントJSONをXMLに変換しました: "+xml.getPath());
				return true;
			}
		} catch (IOException e) {
			log.printStackTrace(e);
		} finally {
			if(ow!=null) try {ow.close();}catch(IOException e){};
			if(fos!=null) try {fos.close();}catch(IOException e){};
		}
		return false;
	}

	//2022.06ぐらいから？のニコ動新サーバー(nvcomment)のJSON→XML変換
	private String makeNvCommentXml(Mson mson, String kind, boolean localconv){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		chatCount = 0;
		// ヘッダ
		pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		// パケット開始
		pw.println("<packet>");
		//{"meta":{"status":200}でなければnullを返す
		Mson m_status = mson.get("status");
		if (m_status == null) {
			log.println("\nJSON status: null");
			return null;
		} else if (!m_status.toString().equals("200")) {
			log.println("\nJSON status: "+m_status.toString());
			return null;
		}
		boolean outflag = true;
		int p = 0;
		Mson m_threads = mson.get("threads");
		Mson m_global = mson.get("globalComments");
		// kind と threads() の数とthreads().fork でどの threads() からデーターを取得するか決める
		int ctype = 0; // 0:ユーザー動画 1:公式動画 2:ニコスクリプトありユーザー動画
		if (m_threads.getSize() > 3) {
			if (m_threads.get(2).getAsString("fork").equals("main"))
				ctype = 1;
			else if (m_threads.get(2).getAsString("fork").equals("easy"))
				ctype = 2;
		}
		if(kind.equals("owner")){
			p = 0;
			if(outflag) log.println(kind+"_comment p="+p);
		}else if(kind.equals("user")){
			p = 1;
			if (ctype == 1)
				p = 2;
			if(outflag) log.println(kind+"_comment p="+p);
		}else if(kind.equals("easy")){
			p = 2;
			if (ctype == 1)
				p = 3;
			if(outflag) log.println(kind+"_comment p="+p);
		}else if(kind.equals("optional")){
			p = 1;
			if(outflag) log.println(kind+"_comment p="+p);
		}else if(kind.equals("nicos")){
			p = 3;
			if(outflag) log.println(kind+"_comment p="+p);
		}else{
			p = 1;
			if (ctype == 1)
				p = 2;
			if(outflag) log.println(kind+"_comment p="+p);
		}
		// threads.get(p) からデーター読み込み
		Mson m_data = m_threads.get(p);
		int num_res = 0;
		try {
			num_res = Integer.parseInt(m_global.get("count").toString());
			if (m_threads.getSize() > 3) {
				for (int i = 0; i < m_threads.getSize(); i++) {
					num_res += Integer.parseInt(m_threads.get(i).get("commentCount").toString());
				}
			}
		}catch (NumberFormatException ex){
			ex.printStackTrace();
		}
		if (outflag) {
			log.print("id: "+m_data.getAsString("id"));
			log.print(" fork: "+m_data.getAsString("fork"));
			log.print(" commentCount: "+m_data.get("commentCount"));
			log.print("\n");
		}
		String s;
		if(outflag) {
			s = "<thread thread=\"" + m_data.getAsString("id") + "\" />";
			pw.println(s);
			s = "<global_num_res thread=\"" + m_global.getAsString("id")
			  + "\" num_res=\"" +  num_res + "\"/>";
			pw.println(s);
			s = "<leaf thread=\"" + m_data.getAsString("id")
			  + "\" count=\"" + m_data.get("commentCount").toString() + "\"/>";
			pw.println(s);
		}
		// threads.comments() からデーター読み込み
		Mson m_comments = m_data.get("comments");
		if(outflag) {
			log.print("comments: "+m_comments.getSize()+"\n");
		}
		String key = null;
		JsonElement value = null;
		String vpos;
		JsonArray commands;
		Mson elem = null;
		for (int i = 0; i < m_comments.getSize(); i++) {
			//chat処理
			elem = m_comments.get(i);
			s = "<chat thread=\"" + m_data.getAsString("id") + "\"";
			for(Entry<String, JsonElement> e:elem.entrySet()){
				key = e.getKey();
				value = e.getValue();
				if (key.equals("no")) {
					s += " no=\"" + value.toString() + "\"";
				}else if (key.equals("vposMs")) {
					vpos = elem.get("vposMs").toString();
					if (vpos.length() > 1)
						vpos = vpos.substring(0, vpos.length()-1);
					else
						vpos = "0";
					s += " vpos=\"" + vpos + "\"";
				}else if (key.equals("postedAt")) {
					s += " date=\"" + Util.OffsetDateTime2UnixTime(value.getAsString()) + "\""
					  + " date_usec=\"0\"";
				}else if (key.equals("nicoruCount")) {
					if (!(value.toString()).equals("0"))
						s += " nicoru=\"" + value.toString() + "\"";
				}else if (key.equals("userId")) {
					if (value.getAsString().startsWith("nvc:"))
						s += " anonymity=\"1\"";
					s += " user_id=\"" + value.getAsString() + "\"";
				}else if (key.equals("isPremium")) {
					if (value.getAsBoolean())
						s += " premium=\"1\"";
				}else if (key.equals("commands")) {
					commands = value.getAsJsonArray();
					if (commands.size() > 0) {
						s += " mail=\"";
						for (int j = 0; j < commands.size(); j++) {
							s += commands.get(j).getAsString();
							if (j < commands.size() - 1)
								s += " ";
						}
						s += "\"";
					}
				}else if (key.equals("score")) {
					s += " score=\"" + value.toString() + "\"";
				//}else if (key.equals("id")) {
				//	s += " id=\"" + value.getAsString() + "\"";
				}else if (key.equals("source")) {
					s += " source=\"" + value.getAsString() + "\"";
				}
			}
			s += ">" + xmlContents(elem, "body")
			  + "</chat>";
			if(outflag) {
				pw.println(s);
				chatCount++;
			}
		}
		pw.println("</packet>");
		pw.close();
		return sw.toString();
	}

	//ニコ動旧サーバー(legacy)のJSON→XML変換
	private String makeCommentXml(Mson mson, String kind, boolean localconv){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		chatCount = 0;
		// ヘッダ
		pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		// パケット開始
		pw.println("<packet>");
		//msonの全てのArrayについてxmlElementStringを作成
		if(mson.isArray()){
			String s;
			boolean outflag = true;
			for(int i = 0; i < mson.getSize(); i++){
				Mson elem = mson.get(i);
				String key = null;
				JsonElement value = null;
				if(elem.isObject()){
					if(elem.entrySet().size()==1){
						for(Entry<String, JsonElement> e:elem.entrySet()){
							key = e.getKey();
							value = e.getValue();
						}
					}else{
						//ハッシュは一つ
						return null;
					}
				}else{
					//配列の中身がArrayやprimitiveやNullはエラー
					return null;
				}
				// key: value
				if(key==null){
					return null;
				}
				Mson m = new Mson(value);
				String contents;
				char p;	// p= 0,1,2,3,4,5,6 固定
				// kind :"user" 一般コメント又はコミュニティコメント(p=0,1)
				// kind :"owner" 投稿者コメント抽出(p=4のみ, fork==1のみ)
				// kind :"oprional" オプショナルスレッド抽出(p=5,6のみ)
				// kind :"nicos" ニコスコメント抽出(p=???)
				// kind :"easy" かんたんコメント抽出(p=2.3のみ, fork==2のみ)
				if(key.equals("ping")){
					// コンテンツ区切り
					contents = xmlContents(m, "content");
					if(contents.contains("ps:")){
						p = contents.charAt(contents.indexOf(":")+1);
						// contents: "ps:n" n番目 pスタート
						// contents: "pf:n" n番目 pフィニッシュ
						// contents: "rs:0" json スタート
						// contents: "rf:0" json フィニッシュ
						//log.println("contetns="+contents+", p="+p);
						if(kind.equals("user")){
							outflag = p == '0' || p == '1';
							if(outflag) log.println(kind+"_comment p="+p);
						}else
						if(kind.equals("owner")){
							outflag = p == '4';
							if(outflag) log.println(kind+"_comment p="+p);
						}else
						if(kind.equals("optional")){
							outflag = p == '5' || p == '6';
							if(outflag) log.println(kind+"_comment p="+p);
						}else
						if(kind.equals("nicos")){
							outflag = true;
							if(outflag) log.println(kind+"_comment p="+p);
						}else
						if(kind.equals("easy")){
							outflag = p == '2' || p == '3';
							if(outflag) log.println(kind+"_comment p="+p);
						}else{
							outflag = p == '0' || p == '1';
							if(outflag) log.println(kind+"_comment p="+p);
						}
					}
					continue;
				}
				if(key.equals("thread")){
					// thread処理
					String fork = m.getAsString("fork");
					if(localconv && kind.equals("owner")){
						outflag = fork!=null && fork.equals("1"); 
						if(outflag) log.println(kind+"_comment fork="+fork);
					}
					s = "<thread"
						+xmlAttributeValue(m, "resultcode")
						+xmlAttributeValue(m, "thread")
						+xmlAttributeValue(m, "fork")
						+xmlAttributeValue(m, "server_time")
						+xmlAttributeValue(m, "last_res")
						+xmlAttributeValue(m, "ticket")
						+xmlAttributeValue(m, "revision")
						+"/>";
					if(outflag) pw.println(s);
					continue;
				}
				if(key.equals("leaf")){
					// leaf処理
					s = "<leaf"
						+xmlAttributeValue(m, "thread")
						+xmlAttributeValue(m, "leaf")
						+xmlAttributeValue(m, "count")
						+"/>";
					if(outflag) pw.println(s);
					continue;
				}
				if(key.equals("global_num_res")){
					//global_num_res処理
					s = "<global_num_res"
						+xmlAttributeValue(m, "thread")
						+xmlAttributeValue(m, "num_res")
						+"/>";
					if(outflag) pw.println(s);
					continue;
				}
				if(key.equals("num_click")){
					//num_click処理
					s = "<num_click"
						+xmlAttributeValue(m, "thread")
						+xmlAttributeValue(m, "no")
						+xmlAttributeValue(m, "count")
						+"/>";
					if(outflag) pw.println(s);
					continue;
				}
				if(key.equals("chat")){
					//chat処理
					s = "<chat"
						+xmlAttributeValue(m, "thread")
						+xmlAttributeValue(m, "fork")
						+xmlAttributeValue(m, "no")
						+xmlAttributeValue(m, "vpos")
						+xmlAttributeValue(m, "leaf")
						+xmlAttributeValue(m, "date")
						+xmlAttributeValue(m, "date_usec")
						+xmlAttributeValue(m, "score")
						+xmlAttributeValue(m, "nicoru")
						+xmlAttributeValue(m, "premium")
						+xmlAttributeValue(m, "anonymity")
						+xmlAttributeValue(m, "user_id")
						+xmlAttributeValue(m, "mail")
						+xmlAttributeValue(m, "filter")
						+xmlAttributeValue(m, "locale")
						+xmlAttributeValue(m, "deleted")
						+">"+xmlContents(m,"content")+"</chat>";
					if(outflag) {
						pw.println(s);
						chatCount++;
					}
					continue;
				}
				// その他
				{
					s = "<"+key
						+xmlAttributeValue(m, "thread")
						+">"+xmlContents(m, key)+"</"+key+">";
					if(outflag) pw.println(s);
				}
			}
		}else{
			//全体が配列でないならエラー
			return null;
		}
		// パケット終了
		pw.println("</packet>");
		pw.close();
		return sw.toString();
	}
	private static String xmlAttributeValue(Mson m, String key){
		// return empty string when value is null or empty
		String val = m.getAsString(key);
		if(val==null || val.isEmpty())
			val = "";
		else
			val = " "+key+"=\""+ChatAttribute.safeReference(val)+"\"";
		return val;
	}
	private static String xmlContents(Mson m, String key){
		// return empty string when value is null or empty
		String val = m.getAsString(key);
		if(val==null || val.isEmpty()){
			val = "";
		}else{
			val = ChatSave.safeReference(val);
			val = evalUnicodeDescr(val)		//unicode description
				.replace("\\n", "\n")		//linefeed eval
				.replace("\\t", "\t")		//linefeed eval
				.replace("\\\"", "\"")		//quote unescape
				.replace("\\\\", "\\");		//escape eval
		}
		return val;
	}

	private static String evalUnicodeDescr(String val) {
		Pattern p = Pattern.compile("\\\\u([0-9a-fA-F]{4})", Pattern.UNIX_LINES);
		Matcher m = p.matcher(val);
		StringBuffer sb = new StringBuffer();
		while(m.find()){
			m.appendReplacement(sb, unicodeReplace(m.group(1)));
		}
		m.appendTail(sb);
		return sb.substring(0);
	}

	private static String unicodeReplace(String xDigits) {
		try {
			int codepoint = Integer.parseInt(xDigits, 16);
			if(codepoint< 0x20 && codepoint!=0x09 && codepoint!=0x0a && codepoint!=0x0d){
				// unprintable char and SAXPY will cause exception
				Logger.MainLog.println("warning: illegal unicode description found: u+"+codepoint);
				Logger.MainLog.println("change to spase u+0020");
				codepoint = 0x20;
			}
			char c = (char)codepoint;
			return ""+c;
		}catch(NumberFormatException e){
			Logger.MainLog.printStackTrace(e);
			return ""+'\u200C';
		}
	}
}
