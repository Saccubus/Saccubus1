package saccubus.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import saccubus.ConvertManager;
import saccubus.ConvertStopFlag;
import saccubus.ConvertWorker;
import saccubus.MainFrame_AboutBox;
import saccubus.WayBackDate;
import saccubus.conv.ChatSave;
import saccubus.json.Mson;
import saccubus.net.BrowserInfo.BrowserCookieKind;
import saccubus.util.Logger;
import saccubus.util.Stopwatch;

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
public class NicoClient {
	private static final String HTTP_WWW_NICOVIDEO_JP = "http://www.nicovideo.jp/";
	private static final String HTTP_WWW_NICOVIDEO_WATCH = HTTP_WWW_NICOVIDEO_JP + "watch/";
	private static final String HTTP_WWW_NICOVIDEO_USER = HTTP_WWW_NICOVIDEO_JP + "user/";
	private static final String HTTP_FLAPI_GETFLV = "http://flapi.nicovideo.jp/api/getflv/";
	private static final String HTTP_FLAPI_GETTHREADKEY = "http://flapi.nicovideo.jp/api/getthreadkey?thread=";
	private static final String HTTP_FLAPI_GETWAYBACKKEY = "http://flapi.nicovideo.jp/api/getwaybackkey?thread=";
	private static final String HTTP_EXT_THUMBINFO = "http://ext.nicovideo.jp/api/getthumbinfo/";
	private static final String HTTP_EXT_THUMBUSER = "http://ext.nicovideo.jp/thumb_user/";
	private final String User;
	private final String Pass;
	private boolean Logged_in = false;
	private final Proxy ConProxy;
	boolean Debug = false;
	private final NicoMap nicomap;
	private Stopwatch Stopwatch;
	private Path titleHtml = null;
	private Logger log;
	private boolean isHtml5;
	private boolean isHtml5Ok = false;

	public static final String DEBUG_PROXY = "debug";	// debug paramerter end with '/'
	static final String S_QUOTE2 = "\"";
	static final char C_QUOTE2 = '"';
	static final String S_ESCAPE = "\\";
	static final char C_ESCAPE = '\\';
	static final String JSON_START = "{&quot;flashvars&quot;:";
	static final String JSON_START2 = "{&quot;video&quot;:";	// HTML5(beta) 2016.11.12

	/**
	 * ブラウザ共有しないでログイン
	 * @param user
	 * @param pass
	 * @param proxy
	 * @param proxy_port
	 */
	public NicoClient(final String user, final String pass,
			final String proxy, final int proxy_port, final Stopwatch stopwatch,
			Logger logger, boolean is_html5) {
		log = logger;
		User = user;
		Pass = pass;
		Stopwatch = stopwatch;
		nicomap = new NicoMap();
		ConProxy = conProxy(proxy, proxy_port);
		isHtml5 = is_html5;
		// ログイン
		Logged_in = login() && loginCheck();
	}

	private Proxy conProxy(String proxy, final int proxy_port){
		Proxy tmpProxy;
		if (proxy != null && proxy.startsWith(DEBUG_PROXY)){
			log.println("Print debug information.");
			Debug = true;
			proxy = proxy.substring(proxy.indexOf('/', DEBUG_PROXY.length())+1);
		}
		if (proxy != null && !proxy.isEmpty() &&
				proxy_port >= 0 && proxy_port <= 65535) {
			try{
				tmpProxy = new Proxy(Proxy.Type.HTTP,
						new InetSocketAddress(proxy, proxy_port));
				//ConProxy = tmpProxy;
				return tmpProxy;
			} catch(Exception ex){
				log.printStackTrace(ex);
				log.println("Unable to make Proxy. maybe bug.");
				return null;
			}
		} else {
			//ConProxy = Proxy.NO_PROXY;
			return Proxy.NO_PROXY;
		}
	}

	/**
	 * ブラウザ共有状態でニコニコ動画にアクセスする<br/>
	 * 既にログインしていなければならない<br/>
	 * ユーザーセッション(Cookie情報)をブラウザから取得するので<br/>
	 * メールアドレス、パスワードは不明
	 * @param browser_kind : ブラウザの種類 : int
	 * @param user_session : String
	 * @param proxy : String
	 * @param proxy_port : int
	 * @param watch_html5
	 */
	public NicoClient(final BrowserCookieKind browser_kind, final String user_session,
			final String proxy, final int proxy_port, final Stopwatch stopwatch,
			Logger logger, boolean is_html5) {
		log = logger;
		User = "";
		Pass = "";
		Stopwatch = stopwatch;
		nicomap = new NicoMap();
		isHtml5 = is_html5;
		ConProxy = conProxy(proxy, proxy_port);
		if (user_session == null || user_session.isEmpty()){
			log.println("Invalid user session" + browser_kind.toString());
			setExtraError("セッションを取得出来ません");
			Logged_in = false;
		} else {
			String[] sessions = user_session.split(" ");	// "user_session_12345..."+" "+...
			for(String session: sessions){
				if (session != null && !session.isEmpty()){
					String this_session = "user_session=" + session;
					Cookie = new NicoCookie();
					Cookie.setSession(this_session);
					if(isHtml5)
						Cookie.addNormalCookie("watch_html5=1");
					if(loginCheck()){
						Logged_in = true;	// ログイン済みのハズ
						setExtraError("");
						return;
					}
					Cookie = new NicoCookie();
					log.println("Fault user session " + browser_kind.toString());
					setExtraError("セッションが無効です");
				}
			}
			Logged_in = false;
		}
	}

	void debug(String messege){
		if (Debug){
			log.print(messege);
		}
	}
	void debug(Mson m){
		if(Debug){
			m.prettyPrint(log);
		}
	}
	void debug(String s, Mson m){
		if(Debug){
			log.print(s);
			m.prettyPrint(log);
		}
	}
	private NicoCookie Cookie = null;

	HttpURLConnection urlConnectGET(String url){
		return urlConnect(url, "GET");
	}

	private HttpURLConnection urlConnect(String url, String method){
		return urlConnect(url, method, Cookie, true, false, "close");
	}

	private HttpURLConnection urlConnect(String url, String method, NicoCookie cookieProp,
			boolean doInput, boolean doOutput, String connectionProp){
		return urlConnect(url,method,cookieProp,doInput,doOutput,connectionProp,"",false);
	}

	private HttpURLConnection urlConnect(String url, String method, NicoCookie cookieProp,
			boolean doInput, boolean doOutput, String connectionProp, String range){
		return urlConnect(url,method,cookieProp,doInput,doOutput,connectionProp,range,false);
	}

	private HttpURLConnection urlConnect(String url, String method, NicoCookie cookieProp,
			boolean doInput, boolean doOutput, String connectionProp, boolean followRedirect)
	{
		return urlConnect(url,method,cookieProp,doInput,doOutput,connectionProp,"",followRedirect);
	}

	private HttpURLConnection urlConnect(String url, String method, NicoCookie cookieProp,
			boolean doInput, boolean doOutput, String connectionProp, String range,
			boolean followRedirect){

		try {
			debug("\n■URL<" + url + ">\n");
			String host = url.substring("http://".length())+"/";
			host = host.substring(0,host.indexOf("/"));
			debug("■HOST<" + host + ">\n");
			HttpURLConnection con = (HttpURLConnection) (new URL(url))
				.openConnection(ConProxy);
			/* リクエストの設定 */
	// this is a successfull request header from waterfox to nmsg.nicovideo.jp/api/
	//POST http://nmsg.nicovideo.jp/api/ HTTP/1.1
	//Host: nmsg.nicovideo.jp
	//User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:40.0) Gecko/20100101 Firefox/40.0.2 Waterfox/40.0.2
	//Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
	//Accept-Language: ja,en-US;q=0.7,en;q=0.3
	//Accept-Encoding: gzip, deflate
	//DNT: 1
	//Pragma: no-cache
	//Cache-Control: no-cache
	//Referer: http://nmsg.nicovideo.jp/api/
	//Content-Length: 292
	//Content-Type: text/plain; charset=UTF-8
	//Cookie: __utmc=8292653; nicosid=1440976771.UserID数字; nicorepo_filter=all;
	//Connection: keep-alive
	//Range: bytes=0-1048576	// byte=1MB
	// the following commented source code lines can be made valid in the future, and it would be ok (already tested).

			con.setRequestMethod(method);
			if(isHtml5){
				if(cookieProp == null){
					cookieProp = new NicoCookie();
				}
				cookieProp.addNormalCookie("watch_html5=1");
			}
			if (cookieProp != null)
				con.addRequestProperty("Cookie", cookieProp.get(url));
 		//	con.setRequestProperty("Host", "nmsg.nicovideo.jp");
			con.addRequestProperty("HOST", host);
			con.setRequestProperty("User-Agent", "Java/Saccubus-"+MainFrame_AboutBox.rev);
		//	con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			con.setRequestProperty("Accept-Language", "ja,en-US;q=0.7,en;q=0.3");
		//	con.setRequestProperty("Accept-Encoding", "deflate");
		//	/* gzip deflateを受付可能にしたらコメント取得が早くなる？ 実際にdeflateで来るかは確かめてない */
			con.setRequestProperty("DNT", "1");
		//	con.setRequestProperty("Pragma", "no-cache");
		//	con.setRequestProperty("Referer", "http://nmsg.nicovideo.jp/api/");
			con.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");	// REQUIRED on nmsg/nicovideo.jp/api/
			if (connectionProp != null){
				con.addRequestProperty("Connection", connectionProp);
			}
			if (doInput){
				con.setDoInput(true);
			}
			if (doOutput){
				con.setDoOutput(true);
			}
			HttpURLConnection.setFollowRedirects(followRedirect);
			if(range==null)
				range = "";
			if(!range.isEmpty()){
				range = "bytes="+range;
				con.addRequestProperty("Range", range);
			}

			debug("■"+(isHtml5?"HTML5":"FLASH")+" Connect: " + method
				+ (cookieProp==null? "" : ",Cookie<" + cookieProp.get(url) +">")
				+ (doInput? ",DoInput" : "")
				+ (doOutput? ",DoOutput" : "")
				+ (followRedirect? ",FollowRedirects" : "")
				+ (connectionProp==null? "" : ",Connection " + connectionProp)
				+ (range.isEmpty()? "":(",Range: "+range))
				+ "\n");

			connect(con);
			if (doOutput){
				return con;
			}
			int code = con.getResponseCode();
			if (code >= HttpURLConnection.HTTP_OK
					&& code < HttpURLConnection.HTTP_BAD_REQUEST) {
				debug("■Response:" + Integer.toString(code) + " " + con.getResponseMessage() + "\n");
				return con;
			} else {
				log.println("Error Response:" + Integer.toString(code) + " " + con.getResponseMessage());
				setExtraError("" + code);
				return null;
			}
		} catch(IOException ex){
			if(Debug)
				log.printStackTrace(ex);
			log.println("Connection error. Check proxy ?");
			setExtraError("コネクションエラー。プロキシが不正？");
		} catch(IllegalStateException ex){
			if(Debug)
				log.printStackTrace(ex);
			log.println("Connection error. Check proxy ?");
			setExtraError("コネクションエラー。プロキシが不正？");
		}
		return null;
	}

	private void connect(HttpURLConnection con) throws IOException {
		Stopwatch.show();
		con.connect();
	}

	private String readConnection(HttpURLConnection con){
		try {
			Stopwatch.show();
			BufferedReader br = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			String ret = br.readLine();
			br.close();
			debug("■readLine:" + ret+ "\n");
			con.disconnect();
			return ret;
		} catch(IOException ex){
			log.printStackTrace(ex);
		}
		return "";
	}

	private NicoCookie detectCookie(HttpURLConnection con){
		nicomap.putConnection(con, (Debug? log:null));
		NicoCookie cookie = new NicoCookie();
		nicomap.setCookie(cookie);
	//	debug("■<NicoCookie><" + cookie.toString() + ">\n");
		return cookie;
	}

	private boolean login() {
		try {
			log.print("Trying login...");
			String url = "https://account.nicovideo.jp/api/v1/login?show_button_twitter=1&site=niconico&show_button_facebook=1";
			debug("\n■HTTPS<" + url + ">\n");
			HttpURLConnection con = (HttpsURLConnection) (new URL(url))
				.openConnection(ConProxy);
			/* 出力のみ */
			con.setDoOutput(true);
			HttpURLConnection.setFollowRedirects(false);
			con.setInstanceFollowRedirects(false);
			con.setRequestMethod("POST");
			con.addRequestProperty("Connection", "close");
			debug("■Connect: POST,DoOutput,Connection close\n");
			connect(con);
			StringBuffer sb = new StringBuffer(4096);
			sb.append("next_url=/&");
			sb.append("mail_tel=");
			sb.append(URLEncoder.encode(User, "Shift_JIS"));
			sb.append("&password=");
			sb.append(URLEncoder.encode(Pass, "Shift_JIS"));
			sb.append("&submit.x=103&submit.y=16");
			String sbstr = sb.toString();
			debug("■write:" + sbstr + "\n");
			OutputStream os = con.getOutputStream();
			os.write(sbstr.getBytes());
			os.flush();
			os.close();
			Stopwatch.show();
			int code = con.getResponseCode();
			String mes = con.getResponseMessage();
			debug("■Response:" + Integer.toString(code) + " " + mes + "\n");
			if (code < HttpURLConnection.HTTP_OK || code >= HttpURLConnection.HTTP_BAD_REQUEST) { // must 200 <= <400
				log.println("Can't login:" + mes);
				return false;
			}
			Cookie = detectCookie(con);
			con.disconnect();
			if (Cookie == null || Cookie.isEmpty()) {
				log.println("Can't login: cannot set cookie.");
				return false;
			}
		//	log.println("Logged in.");
		} catch (IOException ex) {
			log.printStackTrace(ex);
			return false;
		}
		return true;
	}

	public boolean isLoggedIn() {
		return Logged_in;
	}

	public String getVideoTitle() {
		return VideoTitle;
	}

	private String altTag = "";
	public String getAlternativeTag(){
		return altTag;
	}

	private static Pattern safeFileName_SPACE = Pattern.compile(" {2}+");
	public static String safeFileName(String str) {
		//実体参照のパース
		int old_index = 0;
		int new_index = 0;
		StringBuffer sb = new StringBuffer();
		String ch;
		while((new_index = str.indexOf("&#",old_index)) >= 0){
			sb.append(str,old_index,new_index);
			old_index = str.indexOf(";",new_index);
			ch = str.substring(new_index+2,old_index);
			sb.append(new String(new char[]{(char) Integer.parseInt(ch)}));
			old_index++;
		}
		//最後に追加
		sb.append(str,old_index,str.length());
		str = sb.toString();
		//MS-DOSシステム(ffmpeg.exe)で扱える形に(UTF-8のまま)
		str = toSafeWindowsName(str, "MS932");
		return str;
	}

	public static String toSafeWindowsName(String str, String encoding){
		str = toSafeString(str, encoding);
		//ファイルシステムで扱える形に
		str = str.replace('/', '／');
		str = str.replace('\\', '￥');
		str = str.replace('?', '？');
		str = str.replace('*', '＊');
		str = str.replace(':', '：');
		str = str.replace('|', '｜');
		str = str.replace('\"', '”');
		str = str.replace('<', '＜');
		str = str.replace('>', '＞');
		str = str.replace('.', '．');
		str = safeFileName_SPACE.matcher(str).replaceAll(" ");
		str = str.trim();
		return str;
	}

	/**
	 * convert to safe (no change in re-encoding) String
	 * @param str : String
	 * @param encoding : String
	 * @return : String
	 */
	private static String toSafeString(String str, String encoding) {
		StringBuilder sb = new StringBuilder(64);
		for (int i = 0; i < str.length(); i++){
			String s = str.substring(i, i+1);
			byte[] b = { (byte)'-' };
			int len = 1;
			try {
				b = s.getBytes(encoding);		// to encoding
				len = b.length;
			} catch (IOException e) {
				// log.printStackTrace(e);
			}
			if (len == 1 && b[0] == '?'){	// illegal char -> '?', but it's not safe, -> '-'
				b[0] = '-';
				s = "-";	//this is unicode
			}
			/*
			if ("MS932".equals(encoding) && len == 2 &&
					(b[1] == 0x5C || b[1] == 0x7C)){
				log.println("Checked Danger Byte Code<" + b[1] + ">, better to fix?");
			}
			*/
			sb.append(s);
		}
		String dest = sb.toString();	// to Unicode
		return dest;
	}

	private String VideoTitle = null;
	private int VideoLength = -1;

	private static final String TITLE_PARSE_STR_START = "<title>";
	//RC2になってタイトルが変更、使わなくなった。
	//private static final String TITLE_PARSE_STR_END = "</title>";
	private static final String TITLE_END = "‐";
	private static final String TITLE_END2 = "- ニコニコ動画";
	private static final String TITLE_ZERO_DIV = "id=\"videoHeaderDetail\"";
	private static final String TITLE_ZERO_DUMMY = "<title>ニコニコ動画:Zero</title>";
	private static final String TITLE_GINZA_DIV = "DataContainer\"";
	private static final String TITLE_GINZA_DUMMY = "<title>ニコニコ動画:GINZA</title>";
	private static final int SPLIT_TEST_SIZE = 4096;	//4k
	private static final int SPLITS = 2;
	public boolean getVideoHistoryAndTitle(String tag, String watchInfo, boolean saveWatchPage) {
		if(getThumbInfoFile(tag) != null && titleHtml!=null){
			return true;
		}
		return getVideoHistoryAndTitle1(tag, watchInfo, saveWatchPage);
	}
	private String watchThread = "";
	private String getWatchThread() {
		return watchThread;
	}
	private String getThread(String url){
		if(url!=null && !url.isEmpty()){
			int index = (url+"?").indexOf('?');
			url = (url+"?").substring(0,index);
			index = url.lastIndexOf("/");
			url = url.substring(Math.min(index+1, url.length()));
			if(Pattern.matches("[1-9][0-9]+", url))
				return url;
		}
		return "";
	}
	public boolean getVideoHistoryAndTitle1(String tag, String watchInfo, boolean saveWatchPage) {
		String thumbTitle = getVideoTitle();
		VideoTitle = null;
		boolean found = false;
		String url = HTTP_WWW_NICOVIDEO_WATCH + tag + watchInfo;
		String tag1 = tag;
		if(!tag1.contains(watchInfo))
			tag1 += watchInfo;
		log.print("Getting video history...");
		boolean zero_title = false;
		try {
			HttpURLConnection con = urlConnectGET(url);
			while (con == null || con.getResponseCode() != HttpURLConnection.HTTP_OK){
				log.println("ng.\nCan't getVideoHistory:" + url);
				if(con == null) return false;
				Cookie.update(detectCookie(con));
				String location = nicomap.get("Location");
				if(location==null || url.equals(location))
					return false;
				url = location;
				log.println("Redirect to "+url);
				con = urlConnectGET(url);
			}
			Cookie.update(detectCookie(con));
			if(getWatchThread().isEmpty())
				watchThread = getThread(url);
			String encoding = con.getContentEncoding();
			if (encoding == null){
				encoding = "UTF-8";
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(con
					.getInputStream(), encoding));
			log.print("ok.\nChecking VideoTitle...");
			debug("\n");
			String ret;
			int index = -1;
			StringBuilder sb = new StringBuilder();
			while ((ret = br.readLine()) != null) {
				Stopwatch.show();
				sb.append(ret + "\n");
				if(found) continue;
				if(ret.contains(TITLE_ZERO_DUMMY) || ret.contains(TITLE_GINZA_DIV) || ret.contains(TITLE_GINZA_DUMMY)){
					zero_title = true;
					continue;
				}
				if(ret.contains(TITLE_ZERO_DIV)){
					zero_title = true;
				}
				if(zero_title){
					ret = getXmlElement(ret, "h2");
					if(ret==null){
						continue;
					}
					String tmp = ret;
					if (tmp.contains("span"))
						ret = getXmlElement2(tmp, "span");
					if(ret==null){
						ret = tmp;
					}
					found = true;
					zero_title = false;
					if(getVideoTitle()==null){
						VideoTitle = safeFileName(ret);
					}
					log.print("<" + VideoTitle + ">...");
					continue;
				}
				if (ret.contains(TITLE_PARSE_STR_START)) {
					ret = getXmlElement(ret, "title");
					if(ret==null)
						continue;
					found = true;
					index = 0;
					String title1 = ret;
					int index2 = ret.lastIndexOf(TITLE_END);
					if (index2 < 0)
						index2 = ret.lastIndexOf(TITLE_END2);
					if (index2 >= 0){
						title1 = title1.substring(index,index2);
					}else{
						String re = "[‐-]\\h*ニコニコ動画\\h*(:\\h*[\\w]+)?$";
						title1 = title1.replaceAll(re, "").trim();
					}
					if(getVideoTitle()==null){
						VideoTitle = safeFileName(title1);
					}
					log.print("<" + VideoTitle + ">...");
					continue;
				}
			}
			br.close();
			con.disconnect();
			found = getVideoTitle()!=null;
			if(!found){
				VideoTitle = thumbTitle;
				found = getVideoTitle()!=null;
			}
			String ss = sb.substring(0);
			if(altTag.isEmpty())
				altTag = getAltTag(ss);

			PrintWriter pw;
			if(!found || saveWatchPage){
				titleHtml = Path.mkTemp(tag + "watch.htm");
				pw = new PrintWriter(titleHtml, encoding);
				pw.write(ss);
				pw.flush();
				pw.close();
				if(!found)
					log.print(" Title not found.");
				log.println(" <" + Path.toUnixPath(titleHtml) + "> saved.");
			}
			//Json解析
			if(ss.contains(JSON_START)){
				if(extractWatchApiJson(ss, encoding, url)!=null){
					log.println("ok.");
				}
			}else{
				log.println("JSON_START not found, try HTML5_JSON");
				if(ss.contains(JSON_START2)){
					if(extractDataApiDataJson(ss, encoding, url)!=null){
						log.println("html5 ok.");
						isHtml5Ok = true;
					} else {
						log.println("html5 NG.");
						isHtml5Ok = false;
					}
				}
			}
		} catch (IOException ex) {
			log.printStackTrace(ex);
			return false;
		} catch (Exception e) {
			log.printStackTrace(e);
			return false;
		}
		return true;
	}
	private String getAltTag(String text) {
		Pattern p = Pattern.compile("www.nicovideo.jp/allegation/([a-zA-Z]+[0-9]+)");
		Matcher m = p.matcher(text);
		String ret = "";
		while(m.find()){
			ret = m.group(1);
			if(!ret.isEmpty())
				return ret;
		}
		return "";
	}

	private boolean NeedsKey = false;
	private String Premium = "";
	private String OptionalThraedID = "";	// normal Comment ID when Community DOUGA
	private boolean economy = false;
	private String ownerFilter = "";			// video owner filter（replace）
	public boolean getVideoInfo(String tag, String watchInfo, String time, boolean saveWatchPage) {
		if(videoTag==null)
			videoTag = tag;
		if (!getVideoHistoryAndTitle(tag, watchInfo, saveWatchPage)) {
			return false;
		}
		if(isHtml5Ok)
			return true;
		try {
			String url = HTTP_FLAPI_GETFLV + tag;
			if (!getWatchThread().isEmpty()){
				url = HTTP_FLAPI_GETFLV + getWatchThread();
				log.println("\ntry url="+url);
			}
			if (tag.startsWith("nm")) {
				url += "?as3=1";
			}
			if (url.contains("?") && !watchInfo.isEmpty()){
				watchInfo = "&" + watchInfo.substring(1);
			}
			log.print("Getting video informations...");
			HttpURLConnection con = urlConnectGET(url + watchInfo);
			if (con == null || con.getResponseCode() != HttpURLConnection.HTTP_OK){
				log.println("ng.\nCan't getVideoInfo:" + url + watchInfo);
				if(con == null || !loginCheck(con)){
					log.println("Can't login.");
					return false;
				}
			}
			String encoding = con.getContentEncoding();
			if (encoding == null){
				encoding = "UTF-8";
			}
			String ret = readConnection(con);
			if (ret == null || ret.isEmpty()){
				log.println("ng.\nCan't getVideoInfo: null respense.");
				return false;
			}
			nicomap.putArrayURLDecode(ret, encoding);
			if (Debug){
				nicomap.printAll(log);
			}
			ThreadID = nicomap.get("thread_id");
			VideoUrl = nicomap.get("url");
			MsgUrl = nicomap.get("ms");
			UserID = nicomap.get("user_id");
			if (OptionalThraedID.isEmpty() && nicomap.containsKey("optional_thread_id")){
				OptionalThraedID = nicomap.get("optional_thread_id");
			}
			if (nicomap.containsKey("needs_key")) {
				NeedsKey = true;
			}
			Premium = nicomap.get("is_premium");
			try {
				VideoLength = Integer.parseInt(nicomap.get("l"));
			} catch (NumberFormatException e) {
				VideoLength = -1;
			}
			ownerFilter = nicomap.get("ng_up");
			if (ThreadID == null || VideoUrl == null
				|| MsgUrl == null || UserID == null) {
				log.println("ng.\nCan't get video information keys.");
				con = urlConnectGET(url + watchInfo);
				if(!loginCheck(con)){
					log.println("Can't logged In.");
				}
				return false;
			}
			//economy  = VideoUrl.toLowerCase().contains("low");
			log.println("ok.");
			if(serverIsDmc()){
				log.println("Video:<" + apiSessionUrl + ">;");
			}
			log.println("Video:<" + VideoUrl + ">; Comment:<" + MsgUrl
					+ (NeedsKey ? ">; needs_key=1" : ">"));
			economy = VideoUrl.toLowerCase().contains("low");
			if(size_video_thumbinfo==null)
				size_video_thumbinfo = economy? size_low : size_high;
			log.println("size_video: "+size_video_thumbinfo);
			log.println("Video time length: " + VideoLength + "sec");
			log.println("ThreadID:<" + ThreadID + "> Maybe uploaded on "
					+ WayBackDate.format(ThreadID));
			if (OptionalThraedID!=null && !OptionalThraedID.isEmpty()){
				log.println("OptionalThreadID:<" + OptionalThraedID + ">");
			}
		} catch (IOException ex) {
			log.printStackTrace(ex);
			return false;
		}
		return true;
	}

	private byte[] buf = new byte[1024 * 1024];

	private String VideoUrl = null;
	private String ContentType = null;
	private String ContentDisp;
	private int dmcVideoLength = -1;
	private String sessionID;
//	private String video_src_ids;
//	private String audio_src_ids;
	private String[] video_srcs;
	private String[] audio_srcs;
	private String contentUri;
//	private String expire_time;
//	private String responseToken;
//	private String signature;
//	private String r_created_time;
	private String responseXmlData;
	private String postXmlData;
	private String size_video_thumbinfo;
	private String size_high;
	private String size_low;
	//private int sizeHigh;
	private int sizeDmc;
	private int sizeVideo;
	private int downloadLimit;
	private Path crossdomain;

	public int getDmcVideoLength(){
		return dmcVideoLength;
	}
	public String getSizeSmile(){
		return size_video_thumbinfo;
	}
	public int getSizeDmc(){
		return sizeDmc;
	}
	public int getSizeVideo(){
		return sizeVideo;
	}
	public String getVideoContentType() {
		return ContentType;
	}
	public File getVideo(File file, final JLabel status, final ConvertStopFlag flag,
			boolean renameMp4) {
		try {
			log.print("Getting video size...");
			if (VideoUrl == null) {
				log.println("Video url is not detected.");
				return null;
			}
			if(VideoUrl.contains("rtmp")||VideoUrl.contains("rtmpe")){
				// rtmp(e) はダウンロード不可
				log.println("Can't get video:" + VideoUrl);
				setExtraError("　ストリーミング動画は保存できません。");
				return null;
			}
			if (file.canRead() && file.delete()) { // ファイルがすでに存在するなら削除する。
				log.print("previous video deleted...");
			}
			long startTime = Stopwatch.getElapsedTime(0);
			HttpURLConnection con = urlConnect(VideoUrl, "GET", Cookie, true, false, null);
			if (con == null || con.getResponseCode() != HttpURLConnection.HTTP_OK) {
				log.println("Can't get video:" + VideoUrl);
				String ecode = getExtraError();
				if(ecode==null){

				}
				else if (ecode.contains("403")){
					setExtraError("=不適切な動画の可能性。readmeNew.txt参照");
				}
				else if(ecode.contains("50")){
					// 5秒待機
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// log.printStackTrace(e);
					}
				}
				return null;
			}
			InputStream is = con.getInputStream();
			new NicoMap().putConnection(con, (Debug? log:null));

			ContentType = con.getHeaderField("Content-Type");
			if(ContentType == null) ContentType = "";
			ContentDisp = con.getHeaderField("Content-Disposition");
			int max_size = con.getContentLength();	// -1 when invalid
			if(max_size > 0 && sizeVideo <= 0)
				sizeVideo = max_size;
			log.println("size="+(max_size/1000)+"Kbytes"
				+", type=" + ContentType + ", " + ContentDisp);
			log.print("Downloading smile video...");
			if(renameMp4 && ContentType.contains("mp4")){
				file = Path.getReplacedExtFile(file,".mp4");
			}else if(!renameMp4 && !ContentType.contains("mp4")){
				file = Path.getReplacedExtFile(file,".flv");
			}
			OutputStream os = new FileOutputStream(file);
			int size = 0;
			int read = 0;
			debugsInit();
			while ((read = is.read(buf, 0, buf.length)) > 0) {
				debugsAdd(read);
				size += read;
				os.write(buf, 0, read);
				sendStatus(status, "動画", max_size, size, startTime);
				Stopwatch.show();
				if (flag.needStop()) {
					log.println("\nStopped.");
					is.close();
					os.flush();
					os.close();
					con.disconnect();
					if (file.delete()){
						log.println("video deleted.");
					}
					return null;
				}
			}
			debugsOut("\n■read+write statistics(bytes) ");
			log.println("ok.");
			is.close();
			os.flush();
			os.close();
			con.disconnect();
			return file;
		} catch (FileNotFoundException ex) {
			log.printStackTrace(ex);
		} catch (IOException ex) {
			log.printStackTrace(ex);
		} finally{
		//	debug("■read+write statistics(bytes) ");
		//	debugsOut();
		}
		return null;
	}

	public File getVideoDmc(File video, final JLabel status, final ConvertStopFlag flag,
			boolean renameMp4, long[] limits,
			boolean canRangeReq, boolean tryResume, long resume_size) {

		FileOutputStream fos = null;
		OutputStream os = null;
		HttpURLConnection con = null;
		BufferedReader br = null;
		PrintWriter pw = null;
		InputStream is = null;
		Timer timer = null;
		Path hbxml = null;
		long min_size = limits[0];
		try {
		// 動画URL
			log.print("Getting video url...");
			if (apiSessionUrl == null || apiSessionUrl.isEmpty()) {
				log.println("Video url(DMC) is not detected.");
				return null;
			}
			sessionXml = Path.mkTemp(videoTag+"_session.xml");
			sessionData = makeSessionXml(sessionXml, sessionApi, "mp4");
			log.println("sessionXML save to "+sessionXml.getPath());
			String url = apiSessionUrl;
			int index1 = url.indexOf("/","http://".length());
			String host_url = url.substring(0, index1);
			// GET /crossdomain.xml HTTP/1.1
			// Host: api.dmc.nico:2805
			// User-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0
			// Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
			// Accept-Language: ja,en-US;q=0.7,en;q=0.3
			// Accept-Encoding: gzip, deflate
			// DNT: 1
			// Referer: http://res.nimg.jp/swf/player/nicoplayer.swf?ts=59e6229f239741dda87bff0f8ce2dfb7
			// Connection: keep-alive
		//クロスドメイン
			log.print("Getting crossdomain.xml...");
			url = host_url + "/crossdomain.xml";
			con = urlConnect(url, "GET", null, true, false, null);
			if (con == null || con.getResponseCode() != HttpURLConnection.HTTP_OK){
				log.println("ng.\nCan't crossdomain.xml:" + url);
				// error but skip
			}else{
				String encoding = con.getContentEncoding();
				if (encoding == null){
					encoding = "UTF-8";
				}
				br = new BufferedReader(new InputStreamReader(con.getInputStream(), encoding));
				log.print("ok.\nSaving crossdomain.xml...");
				String ret;
				StringBuilder sb = new StringBuilder();
				while ((ret = br.readLine()) != null) {
				//	Stopwatch.show();
					sb.append(ret + "\n");
				}
				br.close();
				con.disconnect();
				log.println("ok.");
				crossdomain = Path.mkTemp(videoTag+"_crossdomain.xml");
				pw = new PrintWriter(crossdomain, encoding);
				pw.write(sb.substring(0));
				pw.flush();
				pw.close();
			}
			//
			//	POST /api/sessions?suppress_response_codes=true&_format=xml HTTP/1.1
			//	Host: api.dmc.nico:2805
			//	User-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0
			//	Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
			//	Accept-Language: ja,en-US;q=0.7,en;q=0.3
			//	Accept-Encoding: gzip, deflate
			//	DNT: 1
			//	Connection: keep-alive
			//	Content-type: application/x-www-form-urlencoded
			//
		//セッション
			url = apiSessionUrl + "?suppress_response_codes=true&_format=xml";
			debug("\n■URL<" + url + ">\n");
			//	con = urlConnect(url, "POST", null, true, true, "keep-alive", false);
			con = (HttpURLConnection) (new URL(url)).openConnection(ConProxy);
			con.setDoOutput(true);
			HttpURLConnection.setFollowRedirects(false);
			con.setInstanceFollowRedirects(false);
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", "Java/Saccubus-"+MainFrame_AboutBox.rev);
			con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			con.setRequestProperty("Accept-Language", "ja,en-US;q=0.7,en;q=0.3");
		//	con.setRequestProperty("Accept-Encoding", "deflate");
		//	con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		//	con.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
			con.setRequestProperty("Content-Type", "application/xml");
			con.addRequestProperty("DNT", "1");
			con.addRequestProperty("Connection", "keep-alive");
			debug("■Connect: POST,DoOutput,Connection keep-alive\n");
			connect(con);
			String poststr = sessionData;
			debug("■write:" + poststr + "\n");
			os = con.getOutputStream();
			os.write(poststr.getBytes());
			os.flush();
			os.close();
			Stopwatch.show();
			int code = con.getResponseCode();
			String mes = con.getResponseMessage();
			debug("■Response:" + Integer.toString(code) + " " + mes + "\n");
			if (code < HttpURLConnection.HTTP_OK || code >= HttpURLConnection.HTTP_BAD_REQUEST) { // must 200 <= <400
				log.println("Can't get DMC session:" + mes);
				return null;
			}
			responseXmlData = readConnection(con);
			// save all response
			// session response input stream data should be used to heartbeat POST data
			// from <session> to </session> copied
			postXmlData = "<session>"+getXmlElement(responseXmlData, "session")+"</session>";
			sessionID = getXmlElement(responseXmlData, "id");
			Path responseXml = Path.mkTemp(videoTag+"_DmcResponse.xml");
			pw = new PrintWriter(responseXml);
			pw.write(responseXmlData);
			pw.flush();
			pw.close();
			debug("\n■session response:\n"+responseXmlData+"\n");
			log.println("Refer dmc responseXml <"+responseXml.getPath()+">");
		//動画サーバ
			contentUri = getXmlElement(responseXmlData, "content_uri");
			if(contentUri==null){
				String resStatus = getXmlElement(responseXmlData, "object");
				log.println("\nDmcHttpResponse: "+resStatus);
				return null;
			}
			String video_src_ids = getXmlElement(responseXmlData, "video_src_ids")
				.replace("<string>", "").replace("</string>", " ").trim();
			video_srcs = video_src_ids.split("\\W+");
			String audio_src_ids = getXmlElement(responseXmlData, "audio_src_ids")
				.replace("<string>", "").replace("</string>", " ").trim();
			audio_srcs = audio_src_ids.split("\\W+");
			debug(" ID:"+sessionID+"\n");
			log.println(" video_src_ids:"+Arrays.asList(video_srcs));
			log.println(" audio_src_ids:"+Arrays.asList(audio_srcs));
			int max_size = 0;
		//	部分ダウンロード設定
			boolean isSplittable = canRangeReq;
			int gaten = Gate.getNumGate();
			int runn = Gate.getNumRun();
			int threadn = (new ConvertManager(null)).getNumThread();
			int convn = (new ConvertManager(null)).getNumRun();
			if(runn >= gaten || convn >= threadn){
				// 同時ダウンロード数不足
				isSplittable = false;
			}
			// Rangeヘッダーチェック
			url = contentUri + "&starti=0&start=0";
			con = urlConnect(url, "GET", null, true, false, null, "0-"+(SPLIT_TEST_SIZE-1));
			if (con == null) {
				// コネクションエラー
				log.println("\nConnection Error. Can't get video(dmc):" + url);
				return null;
			}
			int rcode = con.getResponseCode();
			if (rcode == HttpURLConnection.HTTP_OK) {
				// 部分ダウンロード不可
				debug("\ntest Response(dmc) HTTP_OK");
				canRangeReq = false;
				tryResume = false;
			}
			else{
				if (rcode == HttpURLConnection.HTTP_PARTIAL){
					// Rangeヘッダー受領　部分ダウンロード可能
					debug("\ntest Response(dmc) HTTP_PARTIAL");
					//isSplittable = true;
				}
				else { //エラー
					log.println("\nCan't get video(dmc):" + url);
					String ecode = getExtraError();
					if(ecode==null){
					}
					else if (ecode.contains("403")){
						setExtraError("=不適切な動画の可能性。readmeNew.txt参照");
					}
					else if(ecode.contains("50")){
						// 5秒待機
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							// log.printStackTrace(e);
						}
					}
					return null;
				}
			}
			NicoMap dmcmap = new NicoMap();
			dmcmap.putConnection(con, (Debug? log:null));
			ContentType = con.getHeaderField("Content-Type");
			if(ContentType == null) ContentType = "";
			ContentDisp = con.getHeaderField("Content-Disposition");
			String acceptRange = "";
			String contentLength = "";
			String contentRange = "";
			int sizeRanged = 0;
			int sizeAll = 0;
			//レスポンスヘッダ
			// Accept-Ranges: bytes
			// Content-Length: 100
			// Content-Range: bytes 101-200/3024
			acceptRange = dmcmap.get("Accept-Ranges");
			contentLength = dmcmap.get("Content-Length");
			contentRange = dmcmap.get("Content-Range");
			if(acceptRange==null || contentLength==null || contentRange==null){
				canRangeReq = false;
				tryResume = false;
			}
			else if(acceptRange.equals("bytes")){
				// Response header check
				try {
					debug("\ntest(dmc) Content-Length: " + contentLength);
					sizeRanged = Integer.decode(contentLength);
				}catch(Exception e){
					sizeRanged = 0;
					debug("\nerror Response header(dmc) Content-Length: " + sizeRanged);
				}
				if(contentRange.contains("/")){
					String allsize = contentRange.substring(contentRange.lastIndexOf("/")+1);
					try {
						debug("\ntest(dmc) Content-Range: " + contentRange);
						sizeAll = Integer.decode(allsize);
					} catch(Exception ex){
						sizeAll = 0;
						debug("\nerror Response header(dmc) Content-Range: " + sizeAll);
					}
				}
				if(sizeRanged == 0 || sizeAll == 0) {
					canRangeReq = false;
					tryResume = false;
				}
				debug("\n");
			}
			max_size = con.getContentLength();	// -1 when invalid
		//	ダミーリード
			int dummy = 0;
			is = con.getInputStream();
			File dummyfile = Path.mkTemp("dummy["+videoTag+"].flv");
			downloadLimit = 4096*1024;
			int byterate = downloadLimit / 60;
			os = new FileOutputStream(dummyfile);
			while((dummy = is.read(buf, 0, SPLIT_TEST_SIZE)) > 0){
				os.write(buf, 0, dummy);
			}
			is.close();
			os.flush();
			os.close();
			con.disconnect();	//テスト終了
			try {
				if(sizeAll > max_size)
					max_size = sizeAll;
				if(sizeRanged > max_size)
					max_size = sizeRanged;
				if(max_size > 0 && sizeDmc <= 0){
					limits[1] = max_size;
					if(max_size < min_size){
						setExtraError("97 最小限度サイズより小さいのでダウンロード中止");
						return null;
					}
					if(max_size == min_size){
						setExtraError("97 最小限度サイズと同じ。ダウンロード済みのようです");
						return null;
					}
					// 続行
					sizeDmc = max_size;
					if(max_size == resume_size){
						setExtraError("97 ダウンロード完了済み");
						return video;
					}
				}
				log.println("max_size="+(max_size/1000)+"Kbytes.");
				// ダウンロードリミット設定
				int videolen = getDmcVideoLength();
				if(videolen > 0){
					byterate = (int)((double)max_size / videolen);
					downloadLimit = byterate * 60;
				}
				if(tryResume || resume_size>0)
					log.println("setting download limit = "+downloadLimit);
				if(dummyfile==null){
					log.println("Error:test download(dmc) failed.");
					return null;
				}
				if(dummyfile.length() != SPLIT_TEST_SIZE){
					log.println("Error:test download(dmc) size("+dummyfile.length()+")mismatch.");
					return null;
				}
			} catch(Exception e){
				log.printStackTrace(e);
			} finally {
				if(dummyfile!=null && dummyfile.delete())
					debug("\ndeleted test(dmc) file.");
				else
					log.println("\ncan't delete dummyfile:"+dummyfile.getPath());
			}
		// 拡張子変更チェック
			if(renameMp4){	// not check contenttype
				video = Path.getReplacedExtFile(video,".mp4");
				log.println("video will save to "+video.getPath());
			}
			// heartbeat thread を起動してバックグランド実行
			String hbUrl1 = apiSessionUrl + "/";
			String hbUrl2 = "?suppress_response_codes=true&_format=xml&_method=PUT";
			hbxml = Path.mkTemp(videoTag+"_HeartBeatPostData.xml");
			TimerTask task = new HeartBeatDmc(hbUrl1, hbUrl2, hbxml);
			timer = new Timer("リトライ分間隔タイマー");
			timer.schedule(task, 10000, 10000);	// 10 seconds
			log.println("heartbeat thread will start in "+10+" seconds.");
			if(tryResume || resume_size > 0){
			//	シーケンシャルリジューム
				debugsInit((int)video.length());
				long starttime = Stopwatch.getStartTime();
				int resumed = (int)resume_size;
				url = contentUri;
				do {
					int resumelimit = resumed + downloadLimit;
					if (resumelimit > max_size)
						resumelimit = max_size;
					con = urlConnect(url, "GET", null, true, false, "keep-alive", ""+resumed+"-"+(resumelimit-1));
					if(con==null){
						log.println("Can't get video(dmc):" + url);
						return null;
					}
					rcode = con.getResponseCode();
					if (rcode == HttpURLConnection.HTTP_PARTIAL) {
						// success range downlaod
					}
					else if (rcode == HttpURLConnection.HTTP_OK) {
						// not ranged
						log.println("Not ranged video(dmc):" + url);
						return null;
					}
					else {
						log.println("Can't get video(dmc)"+rcode+":" + url);
						return null;
					}
					is = con.getInputStream();
					dmcmap = new NicoMap();
					dmcmap.putConnection(con, (Debug? log:null));
					ContentType = con.getHeaderField("Content-Type");
					if(ContentType == null) ContentType = "";
					ContentDisp = con.getHeaderField("Content-Disposition");
					log.println("ContentType:" + ContentType + ", " + ContentDisp);
					contentRange = dmcmap.get("Content-Range");
					log.println("Content-Range: "+contentRange);
					log.println("Download limit = "+(downloadLimit>>10)+"KiB, bitrate = "+(byterate/125)+"kbps");
					log.println("resume Downloading dmc(S) video...");
					os = new FileOutputStream(video, true);
					int read = 0;
					while ((read = is.read(buf, 0, buf.length)) > 0) {
						debugsAdd(read);
						resumed += read;
						os.write(buf, 0, read);
						sendStatus(status, "dmc動画(S)", max_size, resumed, starttime);
						Stopwatch.show();
						if (flag.needStop()) {
							log.println("Stopped.");
							timer.cancel();
							log.println("heartbeat thread stopped.");
							is.close();
							os.flush();
							os.close();
							con.disconnect();
							// stopped video won't be delete
							return null;
						}
						if (resumed > resumelimit){
							log.println("Suspended at "+resumed+" bytes.");
							timer.cancel();
							log.println("heartbeat thread stopped.");
							is.close();
							os.flush();
							os.close();
							con.disconnect();
							limits[1] = max_size;
							apiSessionUrl = null;
							return video;
						}
					}
					debugsOut("\n■read+write statistics(bytes) \n");
					log.println("resumed size = "+resumed+", max_size="+max_size);
					is.close();
					os.flush();
					os.close();
					//con.disconnect();
					if(resumed < max_size)
						log.println("Download not finished, continue.\n");
					Stopwatch.show();
				} while (resumed < max_size);
				log.println("Download finished.");
				timer.cancel();
				log.println("heartbeat thread stopped.");
				is.close();
				os.flush();
				os.close();
				con.disconnect();
				log.println("resume ok.");
				if(!Debug){
					// delete work files
					log.print("delete workfiles...");
					if(sessionXml!=null && sessionXml.delete())
						log.print(sessionXml+", ");
					if(crossdomain!=null && crossdomain.delete())
						log.print(crossdomain+", ");
					if(hbxml!=null && hbxml.delete())
						log.print(hbxml+", ");
					if(responseXml.delete())
						log.print(responseXml+", ");
					log.println();
				}
				return video;
			}
			if(!canRangeReq || !isSplittable){
			//	部分ダウンロードではないならもう一度ダウンロードを実行する
			//	GET content_uri to download video
				long starttime = Stopwatch.getStartTime();
				url = contentUri + "&starti=0&start=0";
				con = urlConnect(url, "GET", null, true, false, null, "");
				if (con == null || con.getResponseCode() != HttpURLConnection.HTTP_OK) {
					log.println("\nCan't get video(dmc):" + url);
					String ecode = getExtraError();
					if(ecode==null){
					}
					else if (ecode.contains("403")){
						setExtraError("=不適切な動画の可能性。readmeNew.txt参照");
					}
					else if(ecode.contains("50")){
						// 5秒待機
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							// log.printStackTrace(e);
						}
					}
					return null;
				}
				// ファイルがすでに存在するなら削除する。
				if (video.canRead() && video.delete()) {
					log.print("previous video("+video.getPath()+") deleted...");
				}
				is = con.getInputStream();
				new NicoMap().putConnection(con, (Debug? log:null));
				ContentType = con.getHeaderField("Content-Type");
				if(ContentType == null) ContentType = "";
				ContentDisp = con.getHeaderField("Content-Disposition");
				log.println("ContentType:" + ContentType + ", " + ContentDisp);
				log.print("Downloading dmc video...");
				os = new FileOutputStream(video);
				int size = 0;
				int read = 0;
				debugsInit();
				while ((read = is.read(buf, 0, buf.length)) > 0) {
					debugsAdd(read);
					size += read;
					os.write(buf, 0, read);
					sendStatus(status, "dmc動画", max_size, size, starttime);
					Stopwatch.show();
					if (flag.needStop()) {
						log.println("\nStopped.");
						timer.cancel();
						is.close();
						os.flush();
						os.close();
						con.disconnect();
						if (video.delete()){
							log.println("video deleted.");
						}
						return null;
					}
				}
				debugsOut("\n■read+write statistics(bytes) ");
				timer.cancel();
				log.println("heartbeat thread stopped.");
				if(size < max_size){
					log.println("\nDownload stopped less than max_size. "+size+"<"+max_size+"\n");
					Stopwatch.show();
					is.close();
					os.flush();
					os.close();
					con.disconnect();
					if (video.delete()){
						log.println("video fragment deleted.");
					}
					return null;
				}
				log.println("ok.");
				if(!Debug){
					// delete work files
					log.print("delete workfiles...");
					if(sessionXml!=null && sessionXml.delete())
						log.print(sessionXml+", ");
					if(crossdomain!=null && crossdomain.delete())
						log.print(crossdomain+", ");
					if(hbxml!=null && hbxml.delete())
						log.print(hbxml+", ");
					if(responseXml.delete())
						log.print(responseXml+", ");
					log.println();
				}
				is.close();
				os.flush();
				os.close();
				con.disconnect();
				return video;
			}
		//	部分ダウンロードならスレッド2分割して実行
			int subsize = (max_size + SPLITS - 1) / SPLITS;
			log.println("subsize="+(subsize/1000)+"Kbytes.");
			final int totalSize = max_size;

			class SubDownload implements Callable<File> {
				private String url;
				private HttpURLConnection con;
				private InputStream is;
				private OutputStream os;
				private int max_size = totalSize;
				private final int downloadID;
				private final String downloadRange;
				private File downloadVideo;
				private final Logger dlog;

				public SubDownload(final int subID, final int subsize, File video, Logger sublog) {
					downloadID = subID;
					int from = subsize * subID;
					int to = from + subsize - 1;
					if(to > (max_size - 1))
						to = max_size - 1;
					downloadRange = "" + from + "-" + to;
					downloadVideo = video;
					dlog = sublog;
				}
				@Override
				public File call() throws Exception {
					url = contentUri;
					long started = Stopwatch.getElapsedTime(0);
					con = urlConnect(url, "GET", null, true, false, null, downloadRange);
					if (con != null && con.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
						// success range downlaod
					}
					else if (con != null && con.getResponseCode() == HttpURLConnection.HTTP_OK) {
						// not ranged
						dlog.println("Not ranged video(dmc):" + url);
						return null;
					}
					else {
						dlog.println("Can't get video(dmc):" + url);
						return null;
					}
					is = con.getInputStream();
					NicoMap dmcmap = new NicoMap();
					dmcmap.putConnection(con, (Debug? dlog:null));
					String acceptRange = dmcmap.get("Accept-Ranges");
					dlog.println("Accept-Ranges: "+acceptRange);
					String contentLength = dmcmap.get("Content-Length");
					dlog.println("Content-Length: "+contentLength);
					String contentRange = dmcmap.get("Content-Range");
					dlog.println("Content-Range: "+contentRange);
					ContentType = con.getHeaderField("Content-Type");
					if(ContentType == null) ContentType = "";
					ContentDisp = con.getHeaderField("Content-Disposition");
					dlog.println("Content-Type: " + ContentType + ", " + ContentDisp);
					dlog.print("Downloading dmc(R) video...");

					os = new FileOutputStream(downloadVideo);
					int read = 0;
					int len = buf.length / SPLITS;
					int offset = downloadID * len;
					while ((read = is.read(buf, offset, len)) > 0) {
						os.write(buf, offset, read);
						debugsAdd(read);
						sendStatus(status, "dmc動画(R)", max_size, dsSum, started);
						Stopwatch.show();
						if (flag.needStop()) {
							dlog.println("Stopped.");
							is.close();
							os.flush();
							os.close();
							con.disconnect();
							if (downloadVideo.delete()){
								dlog.println("video("+downloadID+") deleted. :"+downloadVideo);
							}
							return null;
						}
					}
					debugsOut("\n■sub read+write statistics(bytes) ");
				//	timer.cancel();
					dlog.println("ok.");
					is.close();
					os.flush();
					os.close();
					con.disconnect();
					return downloadVideo;
				}
			};

			String videoname = video.getPath();
			ExecutorService pool = Executors.newFixedThreadPool(SPLITS);
			debugsInit();
			try {
				List<Future<File>> list = new ArrayList<>();
				List<File> videolist = new ArrayList<>();
				for(int i=0; i<SPLITS; i++){
					File subvideo = new File(videoname + "_dmc_" + i);
					videolist.add(subvideo);
					Logger sublog = new Logger(videoTag+"_dmc", i, "sub_frontend.txt");
					//スレッド生成　実行開始
					Future<File> future =
						pool.submit(new SubDownload(i, subsize, subvideo, sublog));
					log.println("submit subDownload("+i+"): "+subvideo.getName());
					//プール
					list.add(future);
				}
				// ファイルがすでに存在するなら削除する。
				if (video.canRead() && video.delete()) {
					log.println("previous video("+video.getPath()+") deleted.");
				}
				// ファイルの結合
				log.println("Combining dmc(R) video.");
				for(Future<File> future : list){
					try {
						File result = future.get();
						if(result==null){
							log.println("Error Split Download");
						}
					} catch(InterruptedException|ExecutionException e){
						log.printStackTrace(e);
					}
				}
				for(File subvideo : videolist){
					try {
						if(!subvideo.canRead()){
							log.println("sub-video: "+subvideo+" can't read.");
							break;
						}
						is = new FileInputStream(subvideo);
						os = new FileOutputStream(video, true);
						int read;
						while ((read = is.read(buf, 0, buf.length)) > 0) {
							os.write(buf, 0, read);
						}
						log.println("Combined "+subvideo.getName());
					} catch(Exception e) {
						log.printStackTrace(e);
					} finally {
						is.close();
						os.flush();
						os.close();
					}
				}
				for(File subvideo : videolist){
					if(subvideo.delete())
						log.println("video flagment deleted: "+subvideo);
				}
			} finally {
				pool.shutdown();
			}
			debugsOut("\n■read+write statistics(bytes) ");
			timer.cancel();	// timer is single
			log.println("heartbeat thread stopped.");
			log.println("video.length="+video.length()+", sizeDmc="+sizeDmc+".");
			if(video.length()!=sizeDmc){
				log.println("video.length does not equal sizeDmc.");
				// but continue
			}else{
				log.println("ok.");
			}
			if(!Debug){
				// delete work files
				log.print("delete workfiles...");
				if(sessionXml!=null && sessionXml.delete())
					log.print(sessionXml+", ");
				if(crossdomain!=null && crossdomain.delete())
					log.print(crossdomain+", ");
				if(hbxml!=null && hbxml.delete())
					log.print(hbxml+", ");
				if(responseXml.delete())
					log.print(responseXml+", ");
				log.println();
			}
			is.close();
			os.flush();
			os.close();
			return video;
		} catch (FileNotFoundException ex) {
			log.printStackTrace(ex);
		} catch (IOException ex) {
			log.printStackTrace(ex);
		} finally{
			try {
				if(fos!=null) {fos.flush(); fos.close();}
			}catch(IOException e){}
			try {
				if(os!=null) {os.flush(); os.close();}
			}catch(IOException e){}
			try {
				if(br!=null) br.close();
			}catch(IOException e){}
			if(pw!=null) pw.close();
			try {
				if(is!=null) is.close();
			}catch(IOException e){}
			try {
				if(timer!=null) timer.cancel();
			}catch(Exception e){}
			if(responseXmlData.contains("token_accept_time_limit")){
				setExtraError("98 dmc Token timeout");
				String resStatus = getXmlElement(responseXmlData, "object");
				log.println("DmcHttpResponse: "+resStatus);
			}
		}
		return null;
	}

	private String UserID = null;
	private String ThreadID = null;
	private String MsgUrl = null;
	public  final static String STR_OWNER_COMMENT = "1000";
	private static final long NEW_COMMENT_BEGIN_SECOND =
		new WayBackDate("2010/12/22 18:00").getSecond();
	// Refer to http://blog.nicovideo.jp/2010/12/post_261.php
	// 2010年12月22日 18:00 動画のコメントが消えにくくなる対応について
	private  enum CommentType {
		USER{
			@Override
			public String dlmsg(){ return "コメント"; }
		},
		OWNER{
			@Override
			public String dlmsg(){ return "投稿者コメント"; }
		},
		OPTIONAL{
			@Override
			public String dlmsg(){ return "オプショナルスレッド"; }
		};
		public abstract String dlmsg();
	}

	public File getComment(final File file, final JLabel status, final String back_comment,
			final String time, final ConvertStopFlag flag, final int comment_mode, boolean isAppend) {
		if (time != null && !time.isEmpty() && "0".equals(WayBackKey)){
			if (!getWayBackKey(time)) { // WayBackKey
				log.println("It may be wrong Date.");
				//return null;
			}
		}
		boolean useNewComment = true;
		if(comment_mode == 2 || comment_mode == 0 && !hasNewCommentBegun){
			useNewComment = false;
		}
		return downloadComment(file, status, back_comment, CommentType.USER, flag, useNewComment, isAppend);
	}

	public File getOwnerComment(final File file, final JLabel status, final ConvertStopFlag flag) {
		return downloadComment(file, status, STR_OWNER_COMMENT, CommentType.OWNER, flag, false, false);
	}

	public File getOptionalThread(final File file, final JLabel status, final String optionalThreadID,
			final String back_comment, final String time, final ConvertStopFlag flag,
			final int comment_mode, final boolean isAppend) {
		ThreadID = optionalThreadID;
	 	NeedsKey = retry_threadkey;
	 	Official = "";
		// この後でOwnerCommentを取得するとユーザー動画の投稿者コメントが取得される。
		if (time != null && !time.isEmpty()){
		 	WayBackKey = "0";
			if (!getWayBackKey(time)) { // WayBackKey
				log.println("It may be wrong Date.");
				//return null;
			}
		}
		boolean useNewComment = true;
		if(comment_mode == 2 || comment_mode == 0 && !hasNewCommentBegun){
			useNewComment = false;
		}
		return downloadComment(file, status, back_comment, CommentType.OPTIONAL, flag, useNewComment, isAppend);
	}

	private String Official = "";

	private String commentCommand2006(CommentType comType, String back_comment){
		if(!back_comment.endsWith("-")){
			back_comment = "-" + back_comment;
		}
		return "<thread user_id=\"" + UserID
		+ "\" scores=\"1"	//NGscore
		+ "\" when=\"" + WayBackTime + "\" waybackkey=\"" + WayBackKey
		+ "\" res_from=\"" + back_comment
		+ "\" version=\"20061206\" thread=\"" + ThreadID
		+ Official
		+ (comType == CommentType.OWNER ? "\" fork=\"1\"/>" :  "\"/>");
	}

	private String commentCommand2009(CommentType commentType, String back_comment, String res_from){
		String req;
		String wayback =  "\" when=\"" + WayBackTime + "\" waybackkey=\"" + WayBackKey;
		String resfrom;
		if(!back_comment.endsWith("-")){
			// normal
			if (res_from.isEmpty()){
				// overwrite file
				resfrom = "\" res_from=\"-" + back_comment;
			}else{
				// append file mode using res_from param (this is test)
				resfrom = "\" res_from=\"" + res_from;
			}
		}else {
			// for Debug, input comment_no "12345-" etc.
			resfrom = "\" res_from=\"" + back_comment;
		}
		String req_thread_id = retry_threadkey? OptionalThraedID : ThreadID;
		StringBuffer sb = new StringBuffer();
		sb.append("<packet>");
		sb.append("<thread thread=\"" + req_thread_id);
		sb.append("\" version=\"20090904");
		sb.append(resfrom);
		sb.append("\" user_id=\"" + UserID);
		if(NeedsKey){
			sb.append(Official);
		}
		if(!"0".equals(WayBackKey)){
			sb.append(wayback);
		}
		sb.append("\" scores=\"1");	//NGscore
		sb.append("\" nicoru=\"1");
		sb.append("\" with_global=\"1");
		sb.append("\"/>");
		//thread end, thread_leaves start
		sb.append("<thread_leaves thread=\"" + req_thread_id);
		sb.append("\" version=\"20090904");
		sb.append(resfrom);
		sb.append("\" user_id=\"" + UserID);
		if(NeedsKey){
			sb.append(Official);
		}
		if(!"0".equals(WayBackKey)){
			sb.append(wayback);
		}
		sb.append("\" scores=\"1");	//NGscore
		sb.append("\" nicoru=\"1");
		sb.append("\" with_global=\"1");
		sb.append("\">0-");	//>0-10:100,1000<
		sb.append((VideoLength + 59) / 60);
		sb.append(":100,");
		sb.append(back_comment);
		sb.append("</thread_leaves>");
		sb.append("</packet>");
		req = sb.toString();
		return req;
	}

	private File downloadComment(final File file, final JLabel status,
			String back_comment, CommentType commentType, final ConvertStopFlag flag,
			boolean useNewComment, boolean isAppend) {
		log.print("Downloading " + commentType.toString().toLowerCase()
				+" comment, size:" + back_comment + "...");
		//String official = "";	/* 公式動画用のkey追加 */
		if(NeedsKey && Official.isEmpty()){
			if(force184 == null || threadKey == null){
				if(!getOfficialOption(ThreadID) && !retry_threadkey){
					return null;
				}
				if(!getOfficialOption(OptionalThraedID)){
					return null;
				}
			}
			Official ="\" force_184=\"" + force184
					+ "\" threadkey=\"" + threadKey;
		}
		FileOutputStream fos = null;
		InputStream is = null;
		OutputStream os = null;
		HttpURLConnection con = null;
		try {
			String lastNo = "";
			long start0 = Stopwatch.getElapsedTime(0);
			if (file.canRead()){
				if(isAppend && useNewComment){
					if(commentType != CommentType.OWNER)
						lastNo = ConvertWorker.getNoUserLastChat(file);
				}else{
					if (file.delete()) {	//	ファイルがすでに存在するなら削除する。
						log.print("previous " + commentType.toString().toLowerCase() + " comment deleted...");
					}
				}
			}
			fos = new FileOutputStream(file, isAppend);
			con = urlConnect(MsgUrl, "POST", Cookie, true, true, "keep-alive",true);
			os = con.getOutputStream();
			/*
			 * 投稿者コメントは2006versionを使用するらしい。「いんきゅばす1.7.0」
			 * 過去ログ新表示、チャンネル＋コミュニティ新表示。「coroid　いんきゅばす1.7.2」
			 * 新コメント表示と旧表示を選択可能にする。
			 * 既定では2010年12月22日 18:00以後は新表示にする。
			 *		http://sourceforge.jp/projects/coroid/wiki/NicoApiSpec
			 */
			String req;
			if (useNewComment) {
				req = commentCommand2009(commentType, back_comment, lastNo);
				if (lastNo.isEmpty())
					log.print("New comment mode...");
				else
					log.print("Append new comment mode...");
			} else {
				req = commentCommand2006(commentType, back_comment);
				log.print("Old comment mode...");
			}
			debug("\n■write:" + req + "\n");
			os.write(req.getBytes());
			os.flush();
			os.close();
			debug("■Response:" + Integer.toString(con.getResponseCode()) + " " + con.getResponseMessage() + "\n");
			if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
				log.println("ng.\nCan't download " + commentType.toString().toLowerCase() + " comment:" + MsgUrl);
				return null;
			}
			is = con.getInputStream();
			int read = 0;
			int max_size = 0;
			String content_length_str = con.getHeaderField("Content-length");
			if (content_length_str != null && !content_length_str.isEmpty()) {
				max_size = Integer.parseInt(content_length_str);
			}
			int size = 0;
			debugsInit();
			while ((read = is.read(buf, 0, buf.length)) > 0) {
				debugsAdd(read);
				fos.write(buf, 0, read);
				size += read;
				sendStatus(status, commentType.dlmsg(), max_size, size, start0);
				Stopwatch.show();
				if (flag.needStop()) {
					log.println("Stopped.");
					is.close();
					os.flush();
					os.close();
					con.disconnect();
					fos.close();
					if (file.delete()){
						log.println(commentType.toString().toLowerCase() + " comment deleted.");
					}
					return null;
				}
			}
			debugsOut("■read+write statistics(bytes) ");
			log.println("ok.");
			is.close();
			fos.flush();
			if(ownerFilter!=null && commentType==CommentType.OWNER){
				// add OwnerFilter to the end of owner comment file before </packet>
				fos.close();
				String ownerText = Path.readAllText(file.getAbsolutePath(), "UTF-8");
				if(!ownerText.isEmpty()){
					int lastIndex = ownerText.toLowerCase().lastIndexOf("</packet>");
					ownerFilter = "<chat filter=\"1\">" + ChatSave.safeReference(ownerFilter) + "</chat>";
					if(lastIndex>=0){
						ownerText = ownerText.substring(0, lastIndex) + ownerFilter + ownerText.substring(lastIndex);
					}else{
						ownerText = ownerText + ownerFilter + "</packet>\n";
					}
					new Path(file).writeAllText(ownerText, "UTF-8");
				}
			}
			// fos.close();
			con.disconnect();
			return file;
		} catch (IOException ex) {
			log.printStackTrace(ex);
			if(ex.toString().contains("Unexpected")){	//"Unexpected end of file from server"
				setExtraError("サーバーから切断されました。タイムアウト？");
			}
		} catch(NumberFormatException ex){
			log.printStackTrace(ex);
		}
		finally{
			if (fos != null){
				try { fos.close(); } catch (IOException e) {}
			}
			if (is != null){
				try { is.close(); } catch (IOException e) {}
			}
			if (os != null){
				try { os.close(); } catch (IOException e) {}
			}
		}

		return null;
	}

	private String threadKey = null;
	private String force184 = null;
	private boolean retry_threadkey = false;

	private boolean getOfficialOption(String threadId) {
		String url = HTTP_FLAPI_GETTHREADKEY+threadId;
		log.print("\nGetting Official options (threadkey)...");
		try {
			if (force184 != null && threadKey != null){
				log.println("ok. But this call twice, not necessary.");
				return true;
			}
			HttpURLConnection con = urlConnectGET(url);
			if (con == null || con.getResponseCode() != HttpURLConnection.HTTP_OK){
					log.println("ng.\nCan't get Oficial option:" + url);
					return false;
			}
			String ret = readConnection(con);
			if (ret == null || ret.isEmpty()){
				log.println("ng.\nNull response.");
				return false;
			}
			nicomap.splitAndPut(ret, "&");
			threadKey = nicomap.get("threadkey");
			force184 = nicomap.get("force_184");
			if (threadKey == null || force184 == null) {
				log.println("ng.\nCan't get Oficial option.");
				log.println("ret: " + ret);
				return false;
			}
			if(threadKey.isEmpty()){
				log.println("ng.\nCan't get threadkey. retry with community thread");
				if(!retry_threadkey){
					retry_threadkey  = true;
					Official = "";
				 	threadKey = null;
				 	force184 = null;
				}
				return false;
			}
			log.println("ok.  Thread Key: " + threadKey);
			return true;
		} catch (IOException e) {
			log.printStackTrace(e);
		}
		return false;
	}

	private String WayBackKey = "0";
	private String WayBackTime = "0";
	private String ExtraError = "";
	private boolean hasNewCommentBegun = true;

	/**
	 * Parse String time to canonical String WayBackTime<br/>
	 * Check whether new comment mode has begun then,<br/>
	 * And get wayback key from ThreadID.
	 * @param time
	 * @return
	 */
	private boolean getWayBackKey(String time) {
		log.print("Setting wayback time...");
		try {
			if(!"0".equals(WayBackKey)){
				log.println("ok. But this call twice, not necessary.");
				hasNewCommentBegun = true;
				return true;
			}
			WayBackDate wayback = new WayBackDate(time);
			if (!wayback.isValid()){
				log.println("ng.\nCannot parse time.\"" + time + S_QUOTE2);
				setExtraError("過去ログ指定文字列が違います");
				return false;
			}
			String waybacktime = wayback.getWayBackTime();
			log.println("ok. [" + wayback.format() + "]: " + waybacktime);
			log.print("Getting wayback key...");
			String url = HTTP_FLAPI_GETWAYBACKKEY + ThreadID;
			HttpURLConnection con = urlConnectGET(url);
			if (con == null || con.getResponseCode() != HttpURLConnection.HTTP_OK){
				log.println("ng.\nCan't open connection: " + url);
				return false;
			}
			String ret = readConnection(con);
			if (ret == null) {
				log.println("ng.\nCannot find waybackkey from response.");
				return false;
			}
			nicomap.splitAndPut(ret, "&");
			String waybackkey = nicomap.get("waybackkey");
			if (waybackkey == null || waybackkey.isEmpty()) {
				log.println("ng.\nCannot get wayback key. it's invalid");
				if ("0".equals(Premium)){
					setExtraError("一般会員は過去ログ不可です");
				}
				return false;
			}
			log.println("ok.  Wayback key: " + waybackkey);
			WayBackTime = waybacktime;
			WayBackKey = waybackkey;
			hasNewCommentBegun = wayback.getSecond() > NEW_COMMENT_BEGIN_SECOND;
			return true;
		} catch (IOException e) {
			log.printStackTrace(e);
		}
		return false;
	}

	public boolean loginCheck() {
		String url = HTTP_WWW_NICOVIDEO_JP;
		log.print("Checking login...");
		// GET (NO_POST), UTF-8, AllowAutoRedirect,
			HttpURLConnection con = urlConnectGET(url);
			// response 200, 302 is OK
			if (con == null){
				log.println("ng.\nCan't read TopPage at loginCheck:" + url);
				return false;
			}
		return loginCheck(con);
	}

	private boolean loginCheck(HttpURLConnection con) {
		NicoCookie new_cookie = detectCookie(con);
		if (new_cookie == null || new_cookie.isEmpty()) {
			log.print(" new_cookie isEmpty. ");
			// but continue
		}
		String auth = nicomap.get("x-niconico-authflag");
		if(auth==null || auth.isEmpty() || auth.equals("0")){
			log.println("ng. Not logged in. authflag=" + auth);
			con.disconnect();
			return false;
		}
		Cookie.update(new_cookie);
		debug("\n■Now Cookie is<" + Cookie.toString() + ">\n");
		log.println("ok.");
		setExtraError("");
		return true;
	}

	public String getBackCommentFromLength(String def) {
		if (VideoLength < 0) {
			return def;
		} else if (VideoLength >= 0 && VideoLength < 60) {
			return "100";
		} else if (VideoLength >= 60 && VideoLength < 300) {
			return "250";
		} else if (VideoLength >= 300 && VideoLength < 600) {
			return "500";
		} else {
			return "1000";
		}
	}

	private int dsCount = 0;
	private int dsMax;
	private int dsMin;
	private int dsSum;
	private int resume_start=-1;
	private synchronized void debugsInit(int resume_size){
		if(resume_start==-1)
			resume_start = resume_size;
		debugsInit();
	}
	private synchronized void debugsInit(){
		dsCount = dsMax = dsSum = 0;
		dsMin = Integer.MAX_VALUE;
	}
	private synchronized void debugsAdd(int data){
		dsSum += data;
		if(!Debug) return;
		dsCount++;
		dsMax = Math.max(dsMax, data);
		dsMin = Math.min(dsMin, data);
	}
	private synchronized void debugsOut(String header){
		if(!Debug) return;
		log.print(header);
		if(dsCount==0){
			log.println("Count 0");
		} else {
			log.print("Count "+dsCount+", Min "+dsMin+", Max "+dsMax);
			log.println(", Sum "+dsSum+", Avg "+dsSum/dsCount);
		}
	}

	/*
	 * msg = "動画" または "コメント" または "投稿者コメント"
	 */
	private void sendtext(final JLabel status, final String s){
		if(!SwingUtilities.isEventDispatchThread()){
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					status.setText(s);
				}
			});
		}else{
			status.setText(s);
		}
	}
	private synchronized void sendStatus(JLabel status, String msg,
			int max_size, int size, long start_mili){
		String str = "";
		if (max_size > 0) {
			str = String.format("%.2f%%, ",((double)size * 100)/ max_size);
		}
		str += String.format("%.1fMiB", (size >> 10)/ 1024.0);	//ダウンロードサイズ
		if (max_size > 0){
			str += String.format("/%.1fMiB", (max_size >> 10)/ 1024.0);	//動画サイズ
		}
		long milisec = Stopwatch.getElapsedTime(start_mili);
		if(milisec<=0) milisec=1;
		str += String.format(", %dKbps", (size - resume_start)/milisec*8);
		sendtext(status, msg+"ダウンロード：" + str);
	}

	public void setExtraError(String extraError) {
		ExtraError = extraError;
	}

	public String getExtraError() {
		return ExtraError;
	}

	public String getOptionalThreadID() {
		return OptionalThraedID;
	}

	public int getVideoLength() {
		return VideoLength;
	}

	public boolean isEco() {
		return economy;
	}

	private String thumbInfoData;
	private String watchApiJson;
	private String flvInfo;
	private String flvInfoArrays;
	private String isDmc;
	private String dmcInfo;
	private String dmcInfoArrays;
	private String dmcInfoDec;
	private String dmcToken;
	private String dmcTokenUnEscape;
	private String sessionApi;
	private Path sessionXml;
	private String video_src;
	private String audio_src;
	private String videos;
	private String audios;
	private String apiUrls;
	private String sessionData;
	private String player_id;
	private String apiSessionUrl;
	private String videoTag;
	private String recipe_id;
	private String t_created_time;
	private String service_user_id;
	private String dataApiJson;
	private String priority;
	private ArrayList<String> nicoTaglist = new ArrayList<>();
	private String nicoCat;
	public boolean serverIsDmc(){
		return "1".equals(isDmc);
	}
	public Path getThumbInfoFile(String tag){
		String url = HTTP_EXT_THUMBINFO + tag;
		if(videoTag==null)
			videoTag = tag;
		log.print("Getting thumb Info...");
		Path thumbXml = null;
		try {
			HttpURLConnection con = urlConnectGET(url);
			if (con == null || con.getResponseCode() != HttpURLConnection.HTTP_OK){
				log.println("ng.\nCan't getThumbInfo:" + url);
				return null;
			}
		//	Cookie.update(detectCookie(con));

			String encoding = con.getContentEncoding();
			if (encoding == null){
				encoding = "UTF-8";
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(con
					.getInputStream(), encoding));
			log.print("ok.\nSaving thumb Info...");
			String ret;
			StringBuilder sb = new StringBuilder();
			while ((ret = br.readLine()) != null) {
				Stopwatch.show();
				sb.append(ret + "\n");
			}
			br.close();
			con.disconnect();
			String s = sb.substring(0);
			String title = getVideoTitle();
			if(title==null){
				if(s!=null && s.contains("title")){
					title = safeFileName(getXmlElement(s, "title"));
				}
				if(title==null){
					boolean saveHtml = titleHtml==null || !titleHtml.canRead();
					if(getVideoHistoryAndTitle1(tag, "?watch_harmful=1", saveHtml))
						title = getVideoTitle();
					else
						title = VideoTitle;
				} else if(!title.isEmpty()
						&& (VideoTitle==null || VideoTitle.equals("null"))){
					VideoTitle = title;
					thumbInfoData = s;
				}
			}
			if(ContentType==null){
				ContentType = getXmlElement(s, "movie_type");
			}
			if(getWatchThread().isEmpty())
				watchThread = getThread(getXmlElement(s, "watch_url"));
			thumbXml  = Path.mkTemp(tag + "_" + title + ".xml");
			if(titleHtml!=null){
				String html = Path.readAllText(titleHtml, encoding);
				if(html==null) return null;
				if(html.contains(JSON_START)){
					if(extractWatchApiJson(html, encoding, url)==null)
						return null;
					log.println("ok.");
				}else{
					log.println("JSON_START not found, try HTML5_JSON");
					if(html.contains(JSON_START2)){
						if(extractDataApiDataJson(html, encoding, url)==null){
							log.println("html5 NG.");
							isHtml5Ok = false;
						}
						log.println("html5 ok.");
						isHtml5Ok = true;
					}
				}
			}
			if(s.indexOf("status=\"ok\"") < 0 && titleHtml!=null){
				// 可能ならthumbXmlをtitleHtmlから構成する
				sb = new StringBuilder();
				sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
				sb.append("<nicovideo_thumb_response status=\"ok\">\n");
				sb.append("<thumb>\n");
				sb.append(makeNewXmlElement(s,"code"));
				sb.append("<video_id>"+tag+"</video_id>\n");
				sb.append("<title>"+title+"</title>\n");
				if(isHtml5Ok){
					String text = getDataApiJson();
					String video = getJsonValue(text,"video");
					String description = getJsonValue(video,"description");
					if(description==null)
						description = "";
					description = description.replace("&quot;", "”")
						.replace("&lt;", "(").replace("&gt;", ")")
						.replaceAll("\\(br ?/?\\)","\n");
					sb.append(makeNewElement("description", description));
					String thumbnailURL = getJsonValue(video,"thumbnailURL");
					if(thumbnailURL==null)
						thumbnailURL="";
					sb.append(makeNewElement("thumbnail_url",thumbnailURL));
					if(ContentType==null)
						ContentType = getJsonValue(video,"movieType");
					sb.append(makeNewElement("movie_type",ContentType));
					if(altTag.isEmpty())
						altTag = getJsonValue(video, "id");
					if(altTag.isEmpty())
						altTag = tag;
					sb.append(makeNewElement("watch_url",HTTP_WWW_NICOVIDEO_WATCH +altTag));
					sb.append(makeNewElement("thumb_type","video"));
					String tag_list = getJsonValue(text,"tags");
					if(tag_list!=null && tag_list.length()>2){
						if(tag_list.startsWith("[{")&&tag_list.endsWith("}]")){
							sb.append("<tags domain=\"jp\">\n");
							String[] tags = tag_list.substring(2,tag_list.length()-2).split("\\},\\{");
							debug("\ntags.length="+tags.length);
							for(String tagitem: tags){
								String hash = "{"+tagitem+"}";
								debug("\ntagitem="+hash);
								// hash = {"name":"ニコニコ技術部","isCategory":true,"isDictionaryExists":true,"isLocked":true}
								String t = getJsonValue(hash,"name");
								String cat = getJsonValue(hash,"isCategory");
								String lck = getJsonValue(hash,"isLocked");
								if(t!=null){
									debug("\ntag="+t);
									sb.append("<tag");
									if("true".equals(cat)) sb.append(" category=\"1\"");
									if("true".equals(lck)) sb.append(" lock=\"1\"");
									sb.append(">"+t+"</tag>\n");
								}
							}
							sb.append("</tags>\n");
						}
					}
					String owner = getJsonValue(text,"owner");
					String user_id = getJsonValue(owner,"id");
					//if(user_id==null || user_id.isEmpty())
					//	user_id = getJsonValue(text,"user_id");
					if(user_id==null)
						user_id = "";
					sb.append(makeNewElement("user_id",user_id));
					String nickname = getJsonValue(owner,"nickname");
					if(nickname!=null)
						sb.append(makeNewJsonValue(text,"user_nickname"));
					sb.append(makeNewElement("user_icon_url",getJsonValue(owner, "iconURL")));
					sb.append("</thumb>\n");
					sb.append("</nicovideo_thumb_response>\n");
					s = sb.substring(0);
				}else{
					String text = getWatchApiJson();
					String description = getJsonValue(text,"description");
					if(description==null)
						description = "";
					description = description.replace("&quot;", "”")
						.replace("&lt;", "(").replace("&gt;", ")")
						.replaceAll("\\(br ?/?\\)","\n");
					sb.append("<description>"+description+"</description>\n");
					String thumbUrl = getJsonValue(text,"thumbnail");
					if(thumbUrl==null)
						thumbUrl = getJsonValue(text,"thumbImage");
					if(thumbUrl==null) thumbUrl="";
					sb.append("<thumbnail_url>"+thumbUrl+"</thumbnail_url>\n");
					if(ContentType==null)
						ContentType = getJsonValue(text,"movie_type");
					sb.append("<movie_type>"+ContentType+"</movie_type>\n");
					if(altTag.isEmpty())
						altTag = getJsonValue(text, "watch_url");
					if(altTag.isEmpty())
						altTag = tag;
					sb.append("<watch_url>"+HTTP_WWW_NICOVIDEO_WATCH +altTag+"</watch_url>\n");
					sb.append("<thumb_type>video</thumb_type>\n");
					String tag_list = getJsonValue(text,"tagList");
					if(tag_list!=null && tag_list.length()>2){
						if(tag_list.startsWith("[{")&&tag_list.endsWith("}]")){
							sb.append("<tags domain=\"jp\">\n");
							String[] tags = tag_list.substring(2,tag_list.length()-2).split("\\},\\{");
							debug("\ntags.length="+tags.length);
							for(String tagitem: tags){
								String hash = "{"+tagitem+"}";
								debug("\ntagitem="+hash);
								// hash = {"id":"120343647","tag":"ニコニコ技術部","cat":true,"dic":true,"lck":"1"}
								String t = getJsonValue(hash,"tag");
								String cat = getJsonValue(hash,"cat");
								String lck = getJsonValue(hash,"lck");
								if(t!=null){
									debug("\ntag="+t);
									sb.append("<tag");
									if("true".equals(cat)) sb.append(" category=\"1\"");
									if("1".equals(lck)) sb.append(" lock=\"1\"");
									sb.append(">"+t+"</tag>\n");
								}
							}
							sb.append("</tags>\n");
						}
					}
					String user_id = getJsonValue(text,"user_id");
					if(user_id==null || user_id.isEmpty())
						user_id = getJsonValue(text,"videoUserId");
					if(user_id==null)
						user_id = "";
					sb.append("<user_id>"+user_id+"</user_id>\n");
					String nickname = getJsonValue(text,"user_nickname");
					if(nickname!=null)
						sb.append(makeNewJsonValue(text,"user_nickname"));
					sb.append(makeNewJsonValue(text,"user_icon_url"));
					sb.append("</thumb>\n");
					sb.append("</nicovideo_thumb_response>\n");
					s = sb.substring(0);
				}
			}
			PrintWriter pw = new PrintWriter(thumbXml, encoding);
			pw.write(s);
			pw.flush();
			pw.close();
			if(s==null || s.indexOf("status=\"ok\"") < 0)
				log.println("ng.\nSee file:" + thumbXml);
			else {
				log.println("ok.");
				thumbInfoData = s;
			}
			if(size_high==null && thumbInfoData!=null && !thumbInfoData.isEmpty()){
				size_high = getXmlElement(thumbInfoData, "size_high");
				size_low = getXmlElement(thumbInfoData, "size_low");
			}
			if(nicoTaglist.isEmpty() && thumbInfoData!=null && !thumbInfoData.isEmpty()){
				String nico_tags = getXmlElement1(thumbInfoData, "tags");
				if(nico_tags!=null){
					nico_tags = nico_tags.trim();
					debug("\n(NicoClient)nico_tags: "+nico_tags);
					if(!nico_tags.isEmpty()){
						nicoCat = getXmlElement2(nico_tags,"tag category=\"1\" lock=\"1\"");
						debug("\n(NicoClient)nicoCat: "+nicoCat);
						String[] tagarray = nico_tags.split("\n");
						if(tagarray!=null && tagarray.length>0){
							for(int i = 0; i < tagarray.length; i++){
								String tl = tagarray[i];
								String t = getXmlElement1(tl,"tag");
								nicoTaglist.add(t);
								debug("\n(NicoClient)tag["+i+"]: "+t);
							}
						}
						debug("\n(NicoClient)nicoTaglist: "+nicoTaglist.toString());
					}
				}
			}
			return thumbXml;
		} catch (IOException ex) {
			log.printStackTrace(ex);
			return null;
		} catch (Exception e) {
			log.printStackTrace(e);
			return null;
		}
	}
	private String extractDataApiDataJson(String ss, String encoding, String url) {
		// HTML5 watchpage
		String url1 = url.replaceAll("[./:]+", "_")+".html";
		File dataJsonFile = Path.mkTemp(url1);
		Path.writeAllText(dataJsonFile, ss, encoding);
		Path file = getDataApiData(ss, encoding, url);
		if(file==null) return null;
		return extractDataJson(file, encoding);
	}

	private String extractDataJson(Path xml, String encoding) {
		//Json解析
		debug("\n■{");
		if(dataApiJson==null){
			Properties prop = new Properties();
			try {
				prop.loadFromXML(new FileInputStream(xml));
				dataApiJson = prop.getProperty("json", "0");
				debug("\n■dataAPIData_JSON:\n "+dataApiJson);
			} catch (IOException e) {
				//log.printStackTrace(e);
				log.println("\nloadFromXML error: "+xml.getPath());
				return null;
			}
			if(dataApiJson==null || dataApiJson.isEmpty()){
				log.println("dataAPIData_JSON error ");
				return null;
			}
		}
		try {
			Mson dataApiMson = null;
			try{
				dataApiMson = Mson.parse(dataApiJson);
			//	debug("\n■dataApiMson", dataApiMson);
			}catch(Exception e){
				log.println("\nMson: parse error(dataApiJson)");
				return extractDataJson1(dataApiJson);
			}
			isDmc = "0";
			Mson m_video = dataApiMson.getMson("video");
			debug("\n■video: ",m_video);
				Mson m_id = m_video.getMson("id");
				debug("\n■id: ",m_id);
				if(altTag.isEmpty()){
					altTag = m_id.toUnquoteString();
					if(altTag.equals(videoTag))
						altTag = "";
				}
				Mson m_source = m_video.getMson("source");
				debug("\n■source: ",m_source);
				VideoUrl = unquote(getJsonValue1(dataApiJson, "source"));
				debug("\nVideoUrl: "+VideoUrl);
				Mson m_movieType = m_video.getMson("movieType");
				ContentType = m_movieType.toUnquoteString();
			Mson m_title = m_video.getMson("title");
			debug("\n■title: ",m_title);
				VideoTitle = m_title.toUnquoteString();
				log.println("\nVideoTitle: "+VideoTitle);
				//###############
				Mson m_duration = m_video.getMson("duration");
				String l = m_duration.toUnquoteString();
				try {
					VideoLength = (int)Integer.valueOf(l);
				} catch(NumberFormatException e){
					VideoLength = 0;
				};
				debug("\n■VideoLength: "+VideoLength);
				debug("\n■duration: ",m_duration);
				Mson m_isOfficial = m_video.getMson("isOfficial");
				debug("\n■isOfficial: ",m_isOfficial);
				String isOfficial = m_isOfficial.toUnquoteString();
				if(isOfficial!=null && isOfficial.equals("true")){
					NeedsKey = true;
					debug("\nNeedsKey: "+NeedsKey);
				}
				Mson m_dmcInfo = m_video.getMson("dmcInfo");
				debug("\n■dmcInfo: ",m_dmcInfo);
				dmcInfo = m_dmcInfo.toUnquoteString();
				debug("\ndmcInfo: "+dmcInfo);
				if(dmcInfo==null){
					log.println("\ndmcInfo==null");
				} else if(dmcInfo.equals("null")){
					log.println("\ndmcInfo.equals(\"null\")");
				}
				isDmc = (dmcInfo==null || dmcInfo.equals("null"))? "0":"1";
				if(serverIsDmc()){
					isDmc = m_dmcInfo.isNull() ? "0":"1";
					debug("\n■isDmc: "+isDmc+", serverIsDmc(): " + serverIsDmc()+"\n");
					dmcVideoLength = VideoLength;
					debug("\ndmcVideoLength: "+dmcVideoLength);
					Mson m_session_api = m_dmcInfo.getMson("session_api");
					debug("\n■session_api: ",m_session_api);
					sessionApi = m_session_api.toUnquoteString();
					debug("\nsessionApi: "+sessionApi);
					Mson m_token = m_session_api.getMson("token");
					debug("\n■token: ",m_token);
					dmcToken = m_token.toUnquoteString();
					debug("\ndmcToken: "+dmcToken);
					String	s = dmcToken.replace("\\/", "/").replace("\\\"", S_QUOTE2).replace("\\\\", S_ESCAPE);
					dmcTokenUnEscape = s;
					debug("\n■dmcTokenUnEscape:\n "+dmcTokenUnEscape);
					t_created_time = getJsonValue(dmcTokenUnEscape, "created_time");
					debug("\n■token created_time: "+t_created_time);
					//
					recipe_id = getJsonValue(sessionApi, "recipe_id");
					debug("\n■recipe_id: "+recipe_id);
					videos = getJsonValue(sessionApi, "videos");
					debug("\n■videos: "+videos);
					audios = getJsonValue(sessionApi, "audios");
					debug("\n■audios: "+audios);
					apiUrls = getJsonValue(sessionApi, "api_urls").trim();
					if(apiUrls.startsWith("[") && apiUrls.endsWith("]")){
						apiUrls = apiUrls.substring(1, apiUrls.length()-1);
					}
					if(!apiUrls.contains(",")){
						apiSessionUrl = unquote(apiUrls);
					}
					debug("\n■apiUrls: "+apiUrls);
					debug("\n■apiSessionUrl: "+apiSessionUrl);
					player_id = getJsonValue(sessionApi, "player_id");
					debug("\n■player_id:\n "+player_id);
					service_user_id = getJsonValue(sessionApi, "service_user_id");
					debug("\n■service_user_id:\n "+service_user_id);
					debug("\n");
				}
			Mson m_player = dataApiMson.getMson("player");
			debug("\n■player: ",m_player);
			Mson m_thread = dataApiMson.getMson("thread");
			debug("\n■thread: ",m_thread);
				Mson m_thread_id = m_thread.getMson("thread_id");
				debug("\n■thread_id: ",m_thread_id);
				if(!m_thread_id.isNull()){
					ThreadID = m_thread_id.toUnquoteString();
					log.println("\nThreadID: "+ThreadID);
					Mson m_server_url = m_thread.getMson("server_url");
					debug("\n■server_url: ",m_server_url);
					MsgUrl = m_server_url.toUnquoteString();
					log.println("\nMsgUrl: "+MsgUrl);
					Mson m_thread_key_required = m_thread.getMson("thread_key_required");
					debug("\n■thread_key_required: ",m_thread_key_required);
				}else{
					Mson m_ids = m_thread.getMson("ids");
					Mson m_default = m_ids.getMson("default");
					debug("\n■default: ",m_default);
					ThreadID = m_default.toUnquoteString();
					log.println("\nThreadID: "+ThreadID);
					Mson m_community = m_ids.getMson("community");
					debug("\n■community: ",m_community);
					if(!m_community.isNull()){
						OptionalThraedID = m_community.toUnquoteString();
						log.println("\nOptionalThreadID: "+OptionalThraedID);
					}
					Mson m_nicos = m_ids.getMson("nicos");
					debug("\n■nicos: ",m_nicos);
					Mson m_serverUrl = m_thread.getMson("serverUrl");
					debug("\n■serverUrl: ",m_serverUrl);
					MsgUrl = m_serverUrl.toUnquoteString();
					log.println("\nMsgUrl: "+MsgUrl);
				}
			Mson m_tags = dataApiMson.getMson("tags");
			debug("\n■tags: "+m_tags.toString().length());
			Mson m_playlist = dataApiMson.getMson("playlist");
			debug("\n■playlist: "+m_playlist.toString().length());
			Mson m_owner = dataApiMson.getMson("owner");
			debug("\n■owner: ",m_owner);
			Mson m_viewer = dataApiMson.getMson("viewer");
			debug("\n■viewer: ",m_viewer);
				Mson m_userID = m_viewer.getMson("id");
				debug("\n■viewr.id: ",m_userID);
				UserID = m_userID.toUnquoteString();
				log.println("\nUserID: "+UserID);
				Mson m_isPremium = m_viewer.getMson("isPremium");
				debug("\n■isPremium: ",m_isPremium);
				Premium = m_isPremium.toUnquoteString();
				log.println("\nPremium: "+Premium);
				nicomap.put("is_premium", Premium);
			Mson m_ad = dataApiMson.getMson("ad");
			debug("\n■ad: ",m_ad);
			Mson m_lead = dataApiMson.getMson("lead");
			debug("\n■lead: "+m_lead.toString().length());
			Mson m_context = dataApiMson.getMson("context");
			debug("\n■context: ",m_context);
				Mson m_isMyMemory = m_context.getMson("isMyMemory");
				debug("\n■isMyMemory: ",m_isMyMemory);
			Mson m_liveTopics = dataApiMson.getMson("liveTopics");
			debug("\n■liveTopics: "+m_liveTopics.toString().length());
			debug("\n■}\n");
			economy = VideoUrl.toLowerCase().contains("low");
			if(size_video_thumbinfo==null && size_high!=null && size_low!=null)
				size_video_thumbinfo = economy? size_low : size_high;
			return dataApiJson;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	/*
	if (nicomap.containsKey("needs_key")) {
		NeedsKey = true;
	}
	ownerFilter = nicomap.get("ng_up");
	 */
/*
  "video": {
    "id": "sm9",
    "source": "http://smile-com42.nicovideo.jp/smile?m\u003d9.0468low",
    "title": "新・豪血寺一族 -煩悩解放 - レッツゴー！陰陽師",
    "originalTitle": "新・豪血寺一族 -煩悩解放 - レッツゴー！陰陽師",
    "description": "レッツゴー！陰陽師（フルコーラスバージョン）",
    "originalDescription": "レッツゴー！陰陽師（フルコーラスバージョン）",
    "thumbnailURL": "http://tn-skr2.smilevideo.jp/smile?i\u003d9",
    "postedDateTime": "2007/03/06 00:33:00",
    "width": 320,
    "height": 240,
    "duration": 319,
    "viewCount": 16137580,
    "mylistCount": 166856,
    "translation": false,
    "movieType": "flv",
    "isCommentExpired": false,
    "isDeleted": false,
    "isTranslated": false,
    "isR18": false,
    "isAdult": false,
    "isPublic": true,
    "isCommunityMemberOnly": "",
    "isCommonsTreeExists": true,
    "isNoIchiba": false,
    "isOfficial": false,
    "isMonetized": false
  },
  "player": {
    "playerInfoXMLUpdateTIme": 1477631016,
    "isContinuous": false
  },
  "thread": {
    "commentCount": 4447427,
    "serverUrl": "http://nmsg.nicovideo.jp/api/",
    "subServerUrl": "http://nmsg.nicovideo.jp/api/",
    "ids": {
      "default": "1173108780"
    }
  },
  "tags": [
    {
      "id": "1348286",
      "name": "陰陽師",
      "isCategory": false,
      "isDictionaryExists": true,
      "isLocked": true
    },
    {
      "id": "1348992",
      "name": "レッツゴー！陰陽師",
      "isCategory": false,
      "isDictionaryExists": true,
      "isLocked": true
    },
    {
      "id": "1348995",
      "name": "公式",
      "isCategory": false,
      "isDictionaryExists": true,
      "isLocked": true
    },
    {
      "id": "73234594",
      "name": "音楽",
      "isCategory": false,
      "isDictionaryExists": true,
      "isLocked": true
    },
    {
      "id": "73241329",
      "name": "ゲーム",
      "isCategory": false,
      "isDictionaryExists": true,
      "isLocked": true
    },
    {
      "id": "243770175",
      "name": "ニコニコ10周年記念祭り",
      "isCategory": false,
      "isDictionaryExists": true,
      "isLocked": false
    },
    {
      "id": "243930687",
      "name": "全ての元凶",
      "isCategory": false,
      "isDictionaryExists": true,
      "isLocked": false
    },
    {
      "id": "243930799",
      "name": "ニコニコ四天王",
      "isCategory": false,
      "isDictionaryExists": true,
      "isLocked": false
    },
    {
      "id": "243973266",
      "name": "sm9",
      "isCategory": false,
      "isDictionaryExists": true,
      "isLocked": false
    },
    {
      "id": "243999232",
      "name": "な阪関！",
      "isCategory": false,
      "isDictionaryExists": false,
      "isLocked": false
    }
  ],
  "playlist": {
    "items": [
      {
        "id": "sm9",
        "title": "新・豪血寺一族 -煩悩解放 - レッツゴー！陰陽師",
        "thumbnailURL": "http://tn-skr2.smilevideo.jp/smile?i\u003d9",
        "viewCounter": "16137580",
        "numRes": "4447427",
        "mylistCounter": "166856",
        "firstRetrieve": "2007-03-06 00:33:00",
        "lengthSeconds": "319",
        "width": 320,
        "height": 240,
        "isTranslated": false,
        "hasData": true
      },
      {
        "id": "sm83",
        "title": "【ｺﾞﾑ】　ロックマン2　おっくせんまん！（Version ｺﾞﾑ）",
        "thumbnailURL": "http://tn-skr4.smilevideo.jp/smile?i\u003d83",
        "viewCounter": "7511723",
        "numRes": 1215968,
        "mylistCounter": "107482",
        "firstRetrieve": "2007-03-06 04:54:36",
        "lengthSeconds": "171",
        "width": 320,
        "height": 240,
        "isTranslated": false,
        "hasData": true
      },
      {
        "id": "sm117",
        "title": "ドロー！！モンスターカード！！！",
        "thumbnailURL": "http://tn-skr2.smilevideo.jp/smile?i\u003d117",
        "viewCounter": "1777144",
        "numRes": 37152,
        "mylistCounter": "22146",
        "firstRetrieve": "2007-03-06 04:59:46",
        "lengthSeconds": "150",
        "width": 320,
        "height": 240,
        "isTranslated": false,
        "hasData": true
      },
      {
        "id": "sm6981",
        "title": "マイケルクエスト",
        "thumbnailURL": "http://tn-skr2.smilevideo.jp/smile?i\u003d6981",
        "viewCounter": "1151415",
        "numRes": 18857,
        "mylistCounter": "18264",
        "firstRetrieve": "2007-03-06 22:07:56",
        "lengthSeconds": "202",
        "isTranslated": false,
        "hasData": true
      },
      {
        "id": "sm3980",
        "title": "オーケストラ　ゼルダの伝説",
        "thumbnailURL": "http://tn-skr1.smilevideo.jp/smile?i\u003d3980",
        "viewCounter": "986733",
        "numRes": 52093,
        "mylistCounter": "24255",
        "firstRetrieve": "2007-03-06 14:09:06",
        "lengthSeconds": "335",
        "width": 320,
        "height": 240,
        "isTranslated": false,
        "hasData": true
      },
      {
        "id": "sm30191709",
        "title": "MTT先生",
        "thumbnailURL": "http://tn-skr2.smilevideo.jp/smile?i\u003d30191709",
        "viewCounter": "10499",
        "mylistCounter": "439",
        "firstRetrieve": "2016-12-09 03:46:13",
        "lengthSeconds": "136",
        "width": 1280,
        "height": 720,
        "isTranslated": false,
        "tkasType": 2,
        "hasData": true
      },
      {
        "id": "sm2248",
        "title": "デスクリムゾン",
        "thumbnailURL": "http://tn-skr1.smilevideo.jp/smile?i\u003d2248",
        "viewCounter": "1319382",
        "numRes": 49092,
        "mylistCounter": "15996",
        "firstRetrieve": "2007-03-06 10:26:20",
        "lengthSeconds": "262",
        "width": 320,
        "height": 240,
        "isTranslated": false,
        "hasData": true
      },
      {
        "id": "sm42",
        "title": "shuffle 19話",
        "thumbnailURL": "http://tn-skr3.smilevideo.jp/smile?i\u003d42",
        "viewCounter": "192218",
        "numRes": 5448,
        "mylistCounter": "999",
        "firstRetrieve": "2007-03-06 04:40:39",
        "lengthSeconds": "96",
        "isTranslated": false,
        "hasData": true
      },
      {
        "id": "sm167",
        "title": "[キーボードクラッシャー]Unreal Gamer[本家]",
        "thumbnailURL": "http://tn-skr4.smilevideo.jp/smile?i\u003d167",
        "viewCounter": "575276",
        "numRes": 31751,
        "mylistCounter": "7504",
        "firstRetrieve": "2007-03-06 05:07:31",
        "lengthSeconds": "262",
        "width": 320,
        "height": 240,
        "isTranslated": false,
        "hasData": true
      },
      {
        "id": "sm157",
        "title": "Nursery Rhyme ナーサリィ☆ライム （きしめん） 歌詞つき",
        "thumbnailURL": "http://tn-skr2.smilevideo.jp/smile?i\u003d157",
        "viewCounter": "121469",
        "numRes": 4687,
        "mylistCounter": "10863",
        "firstRetrieve": "2007-03-06 05:07:28",
        "lengthSeconds": "114",
        "width": 320,
        "height": 240,
        "isTranslated": false,
        "hasData": true
      },
      {
        "id": "sm5927",
        "title": "患部で止まってすぐ溶ける ～ 狂気の優曇華院FULL",
        "thumbnailURL": "http://tn-skr4.smilevideo.jp/smile?i\u003d5927",
        "viewCounter": "634548",
        "numRes": 8915,
        "mylistCounter": "6134",
        "firstRetrieve": "2007-03-06 19:13:57",
        "lengthSeconds": "179",
        "width": 320,
        "height": 240,
        "isTranslated": false,
        "hasData": true
      },
      {
        "id": "sm3551",
        "title": "魔理沙は大変なものを盗んでいきました。Full.ver",
        "thumbnailURL": "http://tn-skr4.smilevideo.jp/smile?i\u003d3551",
        "viewCounter": "241255",
        "numRes": 9677,
        "mylistCounter": "4868",
        "firstRetrieve": "2007-03-06 13:20:25",
        "lengthSeconds": "244",
        "width": 320,
        "height": 240,
        "isTranslated": false,
        "hasData": true
      },
      {
        "id": "sm241",
        "title": "THE IDOLM@STER アイドルマスター とかちラーメン大盛り ～望みの限りに～",
        "thumbnailURL": "http://tn-skr2.smilevideo.jp/smile?i\u003d241",
        "viewCounter": "526568",
        "numRes": 80072,
        "mylistCounter": "6732",
        "firstRetrieve": "2007-03-06 05:20:24",
        "lengthSeconds": "599",
        "width": 320,
        "height": 240,
        "isTranslated": false,
        "hasData": true
      },
      {
        "id": "sm50",
        "title": "手品　ひっくりかえるカード",
        "thumbnailURL": "http://tn-skr3.smilevideo.jp/smile?i\u003d50",
        "viewCounter": "123007",
        "numRes": 771,
        "mylistCounter": "309",
        "firstRetrieve": "2007-03-06 04:45:47",
        "lengthSeconds": "23",
        "width": 320,
        "height": 240,
        "isTranslated": false,
        "hasData": true
      },
      {
        "id": "sm56",
        "title": "中川翔子を描いてみた",
        "thumbnailURL": "http://tn-skr1.smilevideo.jp/smile?i\u003d56",
        "viewCounter": "153802",
        "numRes": 3075,
        "mylistCounter": "629",
        "firstRetrieve": "2007-03-06 04:46:12",
        "lengthSeconds": "126",
        "width": 320,
        "height": 240,
        "isTranslated": false,
        "hasData": true
      },
      {
        "id": "sm1687",
        "title": "おでんぱ☆ラブガール",
        "thumbnailURL": "http://tn-skr4.smilevideo.jp/smile?i\u003d1687",
        "viewCounter": "748078",
        "numRes": 25252,
        "mylistCounter": "8666",
        "firstRetrieve": "2007-03-06 09:08:29",
        "lengthSeconds": "162",
        "width": 256,
        "height": 192,
        "isTranslated": false,
        "hasData": true
      },
      {
        "id": "sm7594",
        "title": "ドナルドのうわさ　ランランルーってなんなんだー？",
        "thumbnailURL": "http://tn-skr3.smilevideo.jp/smile?i\u003d7594",
        "viewCounter": "279887",
        "numRes": 7262,
        "mylistCounter": "1910",
        "firstRetrieve": "2007-03-06 23:31:55",
        "lengthSeconds": "28",
        "width": 320,
        "height": 240,
        "isTranslated": false,
        "hasData": true
      },
      {
        "id": "sm76",
        "title": "MONSTER HUNTER2(dos) プレイ動画　－百花繚乱ー１",
        "thumbnailURL": "http://tn-skr1.smilevideo.jp/smile?i\u003d76",
        "viewCounter": "83314",
        "numRes": 1571,
        "mylistCounter": "111",
        "firstRetrieve": "2007-03-06 04:53:09",
        "lengthSeconds": "327",
        "width": 320,
        "height": 240,
        "isTranslated": false,
        "hasData": true
      }
    ],
    "type": "recommend",
    "ref": "other",
    "option": []
  },
  "owner": {
    "id": "4",
    "nickname": "運営長の中の人 さん",
    "stampExp": "129",
    "iconURL": "https://secure-dcdn.cdn.nimg.jp/nicoaccount/usericon/0/4.jpg?1271141672",
    "isUserVideoPublic": false,
    "isUserMyVideoPublic": false,
    "isUserOpenListPublic": false
  },
  "viewer": {
    "id": 60625665,
    "nickname": "eiji さん",
    "prefecture": 1,
    "sex": 0,
    "age": 109,
    "isPremium": false,
    "isPrivileged": false,
    "isPostLocked": false,
    "isHtrzm": false,
    "isTwitterConnection": false
  },
  "ad": {},
  "lead": {
    "tagRelatedMarquee": {
      "id": "1159",
      "url": "http://rd.nicovideo.jp/cc/video_tag_marquee/1481619394",
      "title": "お待たせしました！闘会議2017の追加出演者・ゲームタイトル・企画を公開しました！"
    },
    "tagRelatedBanner": {
      "id": "1341",
      "url": "http://rd.nicovideo.jp/cc/video_tag_banner/1481619394",
      "title": "闘会議2017の追加出演者・ゲームタイトル・企画を公開しました！",
      "thumbnailURL": "http://res.nimg.jp/img/system/watch_banner_related_tag/1481962388_tkg2017webopen161217tag2.png"
    }
  },
  "context": {
    "watchId": "sm9",
    "isEconomyExists": 1,
    "isNeedPayment": false,
    "isAdultRatingNG": true,
    "isTranslatable": false,
    "isTagUneditable": false,
    "isVideoOwner": false,
    "isThreadOwner": false,
    "useChecklistCache": "1",
    "isDictionaryDisplayable": true,
    "isDefaultCommentInvisible": false,
    "csrfToken": "60625665-1482181872-80063e30c7312fc8d09fcab5e0701bd69447e123",
    "translationVersionJsonUpdateTime": 1481782197,
    "userkey": "1482097272.~1~-7rYs4xOFo9i5SvzsT_YyoQfnRw1hvgvpwbCZswQ4Zo",
    "watchAuthKey": "1:1482095472:1173108780:51f2dd5f17ef67b5dd7c54fee5072c4281d7e81ef077b684f6665f3b9a48b673",
    "watchTrackId": "S4ifzkTs7K_1482095472927",
    "watchPageServerTime": 1482095472993,
    "isAuthenticationRequired": false,
    "ngRevision": 1,
    "isMyMemory": false,
    "ownerNGFilters": [
      {
        "source": "",
        "destination": ""
      }
    ]
  },
  "liveTopics": {
    "items": [
      {
        "id": "lv285232097",
        "title": "ドリーム佐野BC放送局　腹減ったからガスト行くぞ",
        "thumbnailURL": "http://icon.nimg.jp/community/s/211/co2116132.jpg?1479781433",
        "point": 13,
        "isHigh": false,
        "elapsedTimeM": 219,
        "communityId": "co2116132",
        "communityName": "ドリーム佐野BC放送局"
      },
      {
        "id": "lv285234268",
        "title": "【スマ対】syamu_movieナイト上映",
        "thumbnailURL": "http://icon.nimg.jp/community/s/250/co2500567.jpg?1450936624",
        "point": 11,
        "isHigh": false,
        "elapsedTimeM": 202,
        "communityId": "co2500567",
        "communityName": "【本人公認】Syamu_Gameファンクラブ"
      }
    ]
  }
}
// dmc server
{
	"video":	{
		略
		"dmcInfo":	{
			"time":1482109220,
			"time_ms":1482109220682,
			"video":	{
				"video_id":"sm39999999",
				"length_seconds":203,
				"deleted":0
			},
			"thread":	{
				"server_url":"http://nmsg.nicovideo.jp/api/",
				"sub_server_url":"http://nmsg.nicovideo.jp/api/",
				"thread_id":1481768772,
				"nicos_thread_id":null,
				"optional_thread_id":null,
				"thread_key_required":false,
				"channel_ng_words":[],
				"owner_ng_words":[],
				"maintenances_ng":false,
				"postkey_available":true,
				"ng_revision":1
			},
			"user":	{
				"user_id":99999999,
				"is_premium":false,
				"nickname":"eiji"
			},
			"hiroba":	{
				"fms_token":null,
				"server_url":"hiroba.nicovideo.jp",
				"server_port":2527,
				"thread_id":30,
				"thread_key":"1482109280.PV4hnVkTBBnZ05aaZx9y879qAxM"
			},
			"error":null,
			"session_api":	{
				"api_urls":	[
					"http://api.dmc.nico:2805/api/sessions"
				],
				"recipe_id":"nicovideo-sm30229297",
				"player_id":"nicovideo-6-1FubPR2ujh_1482109220622",
				"videos":[
					"archive_h264_2000kbps_720p",
					"archive_h264_1000kbps_540p",
					"archive_h264_600kbps_360p",
					"archive_h264_300kbps_360p"
				],
				"audios":[
					"archive_aac_192kbps",
					"archive_aac_64kbps"
				],
				"movies":[],
				"protocols":["http"],
				"auth_types":{"http":"ht2"},
				"service_user_id":"60625665",
				"token":"{"service_id":"nicovideo","player_id":"nicovideo-6-1FubPR2ujh_1482109220622","recipe_id":"nicovideo-sm30229297","service_user_id":"60625665","protocols":[{"name":"http","auth_type":"ht2"}],"videos":["archive_h264_1000kbps_540p","archive_h264_2000kbps_720p","archive_h264_300kbps_360p","archive_h264_600kbps_360p"],"audios":["archive_aac_192kbps","archive_aac_64kbps"],"movies":[],"created_time":1482109220000,"expire_time":1482195620000,"content_ids":["out1"],"heartbeat_lifetime":60000,"content_key_timeout":600000,"priority":0.4,"transfer_presets":[]}",
				"signature":"b054b256517142c00904b4a9b63d560e537062f30aa1e10584314c0880d84610",
				"content_id":"out1",
				"heartbeat_lifetime":60000,
				"content_key_timeout":600000,
				"priority":0.4
			}
		},
		後略
	},
	"player":{略},
	"thread":{略},
	"tags":[略],
	"playlist":{略},
	"owner":{略},
	"viewer":{略},
	 以下略
}

 */
	}

	private String extractDataJson1(String json) {
		try{
			String j_video = getJsonValue1(json, "video");
			debug("\n■video: "+j_video);
			String j_id = getJsonValue1(j_video,"id");
			debug("\n■id: "+j_id);
			if(altTag.isEmpty()){
				altTag = unquote(j_id);
				if(altTag.equals(videoTag))
					altTag = "";
			}
			String j_source = getJsonValue(j_video,"source");
			debug("\n■source: "+j_source);
			VideoUrl = unquote(getJsonValue1(dataApiJson, "source"));
			debug("\nVideoUrl: "+VideoUrl);
			if(ContentType==null){
				ContentType = unquote(getJsonValue1(j_video,"movieType"));
				log.println("\nContentType: "+ContentType);
			}
			if(VideoTitle==null){
				String j_title = getJsonValue1(j_video,"title");
				debug("\n■title: "+j_title);
				VideoTitle = unquote(j_title);
				log.println("\nVideoTitle: "+VideoTitle);
			}
			if(VideoLength<=0){
				String j_duration = getJsonValue1(j_video,"duration");
				debug("\n■duration: "+j_duration);
				String l = unquote(j_duration);
				try {
					VideoLength = (int)Integer.valueOf(l);
				} catch(NumberFormatException e){
					VideoLength = 0;
				};
				debug("\n■VideoLength: "+VideoLength);
			}
			String j_dmcInfo = getJsonValue1(j_video,"dmcInfo");
			debug("\n■dmcInfo: "+j_dmcInfo);
			dmcInfo = unquote(j_dmcInfo);
			debug("\ndmcInfo: "+dmcInfo);
			if(dmcInfo==null){
				log.println("\ndmcInfo==null");
			} else if(dmcInfo.equals("null")){
				log.println("\ndmcInfo.equals(\"null\")");
			}
			isDmc = (dmcInfo==null || dmcInfo.equals("null"))? "0":"1";
			debug("\n■isDmc: "+isDmc+", serverIsDmc(): " + serverIsDmc()+"\n");
			if(serverIsDmc()){
				dmcVideoLength = VideoLength;
				debug("\ndmcVideoLength: "+dmcVideoLength);
				String j_session_api = getJsonValue1(j_dmcInfo,"session_api");
				debug("\n■session_api: "+j_session_api);
				sessionApi = unquote(j_session_api);
				debug("\nsessionApi: "+sessionApi);
				String j_token = getJsonValue1(j_session_api,"token");
				debug("\n■token: "+j_token);
				dmcToken = unquote(j_token);
				debug("\ndmcToken: "+dmcToken);
				String	s = dmcToken.replace("\\/", "/").replace("\\\"", S_QUOTE2).replace("\\\\", S_ESCAPE);
				dmcTokenUnEscape = s;
				debug("\n■dmcTokenUnEscape:\n "+dmcTokenUnEscape);
				t_created_time = getJsonValue(dmcTokenUnEscape, "created_time");
				debug("\n■token created_time: "+t_created_time);
				//
				recipe_id = getJsonValue(sessionApi, "recipe_id");
				debug("\n■recipe_id: "+recipe_id);
				videos = getJsonValue(sessionApi, "videos");
				debug("\n■videos: "+videos);
				audios = getJsonValue(sessionApi, "audios");
				debug("\n■audios: "+audios);
				apiUrls = getJsonValue(sessionApi, "api_urls").trim();
				if(apiUrls.startsWith("[") && apiUrls.endsWith("]")){
					apiUrls = apiUrls.substring(1, apiUrls.length()-1);
				}
				if(!apiUrls.contains(",")){
					apiSessionUrl = unquote(apiUrls);
				}
				debug("\n■apiUrls: "+apiUrls);
				debug("\n■apiSessionUrl: "+apiSessionUrl);
				player_id = getJsonValue(sessionApi, "player_id");
				debug("\n■player_id:\n "+player_id);
				service_user_id = getJsonValue(sessionApi, "service_user_id");
				debug("\n■service_user_id:\n "+service_user_id);
				debug("\n");
			}

			debug("\n■player: "+getJsonValue(dataApiJson,"player"));
			String j_thread = getJsonValue(dataApiJson,"thread");
			debug("\n■thread: "+j_thread);
			String j_ids = getJsonValue1(j_thread,"ids");
			String j_default = getJsonValue1(j_ids,"default");
			debug("\n■default: "+j_default);
			ThreadID = unquote(j_default);
			log.println("\nThreadID: "+ThreadID);
			String j_community = getJsonValue1(j_ids,"community");
			debug("\n■community: "+j_community);
			if(j_community != null && !j_community.equals("null")){
				OptionalThraedID = unquote(j_community);
				log.println("\nOptionalThreadID: "+OptionalThraedID);
			}
			String j_nicos = getJsonValue1(j_ids,"nicos");
			debug("\n■nicos: "+j_nicos);
			String j_serverUrl = getJsonValue1(j_thread,"serverUrl");
			debug("\n■serverUrl: "+j_serverUrl);
			MsgUrl = unquote(j_serverUrl);
			log.println("\nMsgUrl: "+MsgUrl);
			String j_tags = getJsonValue1(dataApiJson,"tags");
			debug("\n■tags: "+j_tags);
			String j_playlist = getJsonValue1(dataApiJson,"playlist");
			debug("\n■playlist: "+j_playlist);
			String j_owner = getJsonValue1(dataApiJson,"owner");
			debug("\n■owner: "+j_owner);
			String j_viewer = getJsonValue1(dataApiJson,"viewer");
			debug("\n■viewer: "+j_viewer);
			String j_userID = getJsonValue1(j_viewer,"id");
			debug("\n■viewer.id: "+j_id);
			UserID = unquote(j_userID);
			log.println("\nUserID: "+UserID);
			if(Premium==null){
				String j_isPremium = getJsonValue1(j_viewer,"isPremium");
				debug("\n■isPremium: "+j_isPremium);
				Premium = unquote(j_isPremium);
				log.println("\nPremium: "+Premium);
				nicomap.put("is_premium", Premium);
			}
			String j_context = getJsonValue1(dataApiJson,"context");
			debug("\n■context: "+j_context);
			String j_isMyMemory = getJsonValue1(j_context,"isMyMemory");
			debug("\n■isMyMemory: "+j_isMyMemory);
			String j_liveTopics = getJsonValue1(dataApiJson,"liveTopics");
			debug("\n■liveTopics: "+j_liveTopics);
			debug("\n");
			economy = VideoUrl.toLowerCase().contains("low");
			if(size_video_thumbinfo==null && size_high!=null && size_low!=null)
				size_video_thumbinfo = economy? size_low : size_high;
			return dataApiJson;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	void debug(String s, String input) {
		if(Debug){
			log.print(s);
			Mson.prettyPrint(input,log);
		}
	}
	private String getDataApiJson(){
		if(dataApiJson==null){
			String encoding = "UTF-8";
			if(titleHtml==null)
				return null;
			String html = Path.readAllText(titleHtml, encoding);
			String r = extractDataApiDataJson(html, encoding, "getDataApiJson");
			if(r==null)
				return null;
		}
		return dataApiJson;
	}
	private Path getDataApiData(String html, String encoding, String comment){
		String text = html;
		// 動画ページのJSONを取り出す
		text = getXmlElement1(text, "body");	//body
		if(text==null)
			return null;
		text = getXmlAttribute(html, "data-api-data");
			//div id="js-initial-watch-data" data-api-data="{
		if(text==null){
			log.println("error: not found data-api-data");
			return null;
		}
		int start = text.indexOf(JSON_START2);
		if(start < 0){
			log.println("error: not found \""+JSON_START2+"\"");
			return null;
		}
		String json_end = S_QUOTE2;
		int end = (text+json_end).indexOf(json_end, start);	// end of JSON
		text = (text+json_end).substring(start, end);
		if(text==null || text.isEmpty()){
			log.println("error: not found dataApi JSON2");
			return null;
		}
		text = text.replace("&quot;", S_QUOTE2);
		if(text==null || text.isEmpty()){
			log.println("error: replace to NULL");
			return null;
		}
		// URLDecodeしない
		Path file0 = Path.mkTemp(videoTag+"_dataApiJson.txt");
		Path file = Path.mkTemp(videoTag+"_dataApiJ.xml");
		Path.writeAllText(file0, text, encoding);
		Mson dataApiMson;
		try {
			dataApiMson = Mson.parse(text);
			debug("\ndataApiMson:\n", dataApiMson);
			dataApiJson = dataApiMson.toUnquoteString();
			text = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
				+"<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">\n"
				+"<properties><comment>" + comment + "</comment>\n"
				+"<entry key=\"json\">" + text + "</entry>";
			Path.writeAllText(file, text, encoding);
		}catch(Exception e){
			log.println("dataApiMson parse error");
			log.println("Saved dataApiData to "+file0.getPath());
			Path.unescapeStoreXml(file, text, comment);		//xml is property key:json val:JSON
		}
		log.println("Saved dataApiData to "+file.getPath());
		return file;
	}

	private String extractWatchApiJson(String html, String encoding, String url){
		Path file = getWatchApiData(html, encoding, url);
		if(file==null) return null;
		return extractJson(file, encoding);
	}
	private String getWatchApiJson(){
		if(watchApiJson==null){
			String encoding = "UTF-8";
			if(titleHtml==null)
				return null;
			String html = Path.readAllText(titleHtml, encoding);
			String r = extractWatchApiJson(html, encoding, "getWatchApiJson");
			if(r==null)
				return null;
		}
		return watchApiJson;
	}
	private String extractJson(Path xml, String encoding) {
		//Json解析
		if(watchApiJson==null){
			Properties prop = new Properties();
			try {
				prop.loadFromXML(new FileInputStream(xml));
			} catch (IOException e) {
				log.printStackTrace(e);
			}
			watchApiJson = prop.getProperty("json", "0");
			debug("\n■watchAPIData_JSON:\n "+watchApiJson);
			if(watchApiJson==null || watchApiJson.isEmpty()){
				log.println("watchAPIData_JSON error ");
				return null;
			}
		}
			// flvInfo
			flvInfo = getJsonValue(watchApiJson, "flvInfo");
			String s = flvInfo;
			s = unquote(s);
			StringBuffer sb = new StringBuffer();
			try {
				String dec = URLDecoder.decode(s, encoding);
				String[] sa = dec.split("&");
				for(String v: sa){
					sb.append(URLDecoder.decode(v, encoding));
					sb.append("\n ");
				}
			} catch (UnsupportedEncodingException e) {
				log.printStackTrace(e);
			}
			flvInfoArrays = sb.substring(0).trim();
			debug("\n■flvInfo:\n "+flvInfo);
			debug("\n■flvInfos:\n [\n "+flvInfoArrays);
			debug("\n ]\n");
			// dmcInfo
			isDmc = getJsonValue(watchApiJson, "isDmc");
			debug("\n■isDmc: "+isDmc+", serverIsDmc(): " + serverIsDmc()+"\n");
			if(serverIsDmc()){
				dmcInfo = getJsonValue(watchApiJson, "dmcInfo");
				s = unquote(dmcInfo);
				sb = new StringBuffer();
				try {
					s = URLDecoder.decode(s, encoding);
				} catch (UnsupportedEncodingException e) {
					log.printStackTrace(e);
				}
				dmcInfoDec = s;
				if(s.contains("&")){
					String[] sa = s.split("&");
					for(String v: sa){
						try {
							sb.append(URLDecoder.decode(v, encoding));
							sb.append("\n ");
						} catch (UnsupportedEncodingException e) {
							log.printStackTrace(e);
						}
					}
					dmcInfoArrays = "[ "+sb.substring(0).trim()+ " ]";
				}
				else if (s.startsWith("{")){
					dmcInfoArrays = prettyBufferPrint(s);
				}
				debug("\n■dmcInfo:\n "+dmcInfo);
				debug("\n■dmcInfos:\n "+dmcInfoArrays);
				if(dmcInfoDec!=null){
					String l = getJsonValue(dmcInfoDec, "length_seconds");
					try {
						dmcVideoLength = (int)Integer.valueOf(l);
					} catch(NumberFormatException e){
						dmcVideoLength = 0;
					};
					debug("\n■dmcVideoLength: "+dmcVideoLength);
					dmcToken = getJsonValue(dmcInfoDec, "token");
					debug("\n■dmcToken:\n "+dmcToken);
					s = dmcToken.replace("\\/", "/").replace("\\\"", S_QUOTE2).replace("\\\\", S_ESCAPE);
					dmcTokenUnEscape = s;
					debug("\n■dmcTokenUnEscape:\n "+dmcTokenUnEscape);
					sessionApi = getJsonValue(dmcInfoDec, "session_api");
					debug("\n■session_api:\n "+sessionApi);
					recipe_id = getJsonValue(sessionApi, "recipe_id");
					debug("\n■recipe_id: "+recipe_id);
					videos = getJsonValue(sessionApi, "videos");
					debug("\n■videos: "+videos);
					audios = getJsonValue(sessionApi, "audios");
					debug("\n■audios: "+audios);
					apiUrls = getJsonValue(sessionApi, "api_urls").trim();
					if(apiUrls.startsWith("[") && apiUrls.endsWith("]")){
						apiUrls = apiUrls.substring(1, apiUrls.length()-1);
					}
					t_created_time = getJsonValue(dmcTokenUnEscape, "created_time");
					debug("\n■token created_time: "+t_created_time);
					if(!apiUrls.contains(",")){
						apiSessionUrl = unquote(apiUrls);
					}
					debug("\n■apiUrls: "+apiUrls);
					debug("\n■apiSessionUrl: "+apiSessionUrl);
					player_id = getJsonValue(sessionApi, "player_id");
					debug("\n■player_id:\n "+player_id);
					service_user_id = getJsonValue(sessionApi, "service_user_id");
					debug("\n■service_user_id:\n "+service_user_id);
					priority = getJsonValue(sessionApi, "priority");
					debug("\n■priority:\n "+priority);
					debug("\n");
				}
			}
	//	}
		return watchApiJson;
	}

	private String unquote(String str) {
		if(str==null) return null;
		str = str.trim();
		if(str.startsWith(S_QUOTE2) && str.endsWith(S_QUOTE2)){
			str = str.substring(1, str.length()-1);
		}
		return str;
	}

	private String makeSessionXml(Path xml, String json, String file_extension) {
		StringBuilder sb = new StringBuilder();
		sb.append("<session>\n");
		sb.append("  "+makeNewElement("recipe_id", recipe_id));
		sb.append("  <content_id>out1</content_id>\n");
		sb.append("  <content_type>movie</content_type>\n");
		sb.append("  <protocol>\n");
		sb.append("    <name>http</name>\n");
		sb.append("    <parameters>\n");
		sb.append("      <http_parameters>\n");
		sb.append("        <method>GET</method>\n");
		sb.append("        <parameters>\n");
		sb.append("          <http_output_download_parameters>\n");
		sb.append("            "+makeNewElement("file_extension",file_extension));
		sb.append("          </http_output_download_parameters>\n");
		sb.append("        </parameters>\n");
		sb.append("      </http_parameters>\n");
		sb.append("    </parameters>\n");
		sb.append("  </protocol>\n");
		sb.append("  "+makeNewJsonValue(json, "priority"));
		sb.append("  <content_src_id_sets>\n");
		sb.append("    <content_src_id_set>\n");
		sb.append("      <content_src_ids>\n");
		sb.append("        <src_id_to_mux>\n");
		sb.append("          <video_src_ids>\n");
		video_src = videos.replace("[\"","            <string>").replace("\"]","</string>\n")
				.replace("\",\"", "</string>\n            <string>");
		sb.append(video_src);
		sb.append("          </video_src_ids>\n");
		sb.append("          <audio_src_ids>\n");
		audio_src = audios.replace("[\"","            <string>").replace("\"]","</string>\n")
				.replace("\",\"", "</string>\n            <string>");
		sb.append(audio_src);
		sb.append("          </audio_src_ids>\n");
		sb.append("        </src_id_to_mux>\n");
		sb.append("      </content_src_ids>\n");
		sb.append("    </content_src_id_set>\n");
		sb.append("  </content_src_id_sets>\n");
		sb.append("  <keep_method>\n");
		sb.append("    <heartbeat>\n");
		sb.append("      <lifetime>60000</lifetime>\n");
		sb.append("    </heartbeat>\n");
		sb.append("  </keep_method>\n");
		sb.append("  <timing_constraint>unlimited</timing_constraint>\n");
		sb.append("  <session_operation_auth>\n");
		sb.append("    <session_operation_auth_by_signature>\n");
		sb.append("      "+makeNewElement("token", dmcTokenUnEscape));
		sb.append("      "+makeNewJsonValue(json, "signature"));
		sb.append("    </session_operation_auth_by_signature>\n");
		sb.append("  </session_operation_auth>\n");
		sb.append("  <content_auth>\n");
		sb.append("    <auth_type>ht2</auth_type>\n");
		sb.append("    <service_id>nicovideo</service_id>\n");
		sb.append("    "+makeNewJsonValue(json,"service_user_id"));
		sb.append("    <max_content_count>10</max_content_count>\n");
		sb.append("    <content_key_timeout>600000</content_key_timeout>\n");
		sb.append("  </content_auth>\n");
		sb.append("  <client_info>\n");
		sb.append("    "+makeNewJsonValue(json, "player_id"));
		sb.append("  </client_info>\n");
		sb.append("</session>");
		String s = sb.substring(0);
		try {
			PrintWriter pw = new PrintWriter(xml);
			pw.write(s);
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			log.printStackTrace(e);
		}
		return s;
	}

	String prettyBufferPrint(String s){
		StringBuffer sb = new StringBuffer();
		s = prettyBufferPrint(s, sb, 1);
		if(s==null) s = "";
		if(!s.isEmpty())
			s = "\nREST:"+s;
		return sb.substring(0)+ s;
	}

	String prettyBufferPrint(String s, StringBuffer sb, int indent){
		String head = s.substring(0, 1);
		String ids = String.format("%"+indent+"s", " ");
		if(head.equals(S_QUOTE2)){
			int i = 1;
			while(i < s.length()){
				String s1 = s.substring(i,i+1);
				if(s1.equals(S_QUOTE2))
					break;
				if(s1.equals(S_ESCAPE))
					i+=2;
				else
					i++;
			}
			sb.append(s.substring(0, i+1));
			return s.substring(i+1);
		}
		String HASH1 = "{";
		String HASH2 = "}";
		String PAIR = ":";
		String COMMA = ",";
		if(head.equals(HASH1)){
			sb.append(HASH1+"\n"+ids);
			do {
				s = prettyBufferPrint(s.substring(1),sb,indent+1);
				if(s==null) return null;
				head = s.substring(0, 1);
				if(!head.equals(PAIR)){
					log.println("ERROR prettyBufferPrint >"+s);
					return null;
				}
				sb.append(PAIR);
				s = prettyBufferPrint(s.substring(1),sb,indent+1);
				if(s==null) return null;
				head = s.substring(0, 1);
				if(head.equals(COMMA)){
					sb.append(COMMA+"\n"+ids);
					continue;
				}
				if(head.equals(HASH2)){
					sb.append("\n"+ids+HASH2);
					return s.substring(1);
				}
				log.println("ERROR prettyBufferPrint >"+s);
				return null;
			}while(s!=null);
			return null;
		}
		String ARRAY1 = "[";
		String ARRAY2 = "]";
		if(head.equals(ARRAY1)){
			sb.append(ARRAY1+"\n"+ids);
			do {
				s = prettyBufferPrint(s.substring(1),sb,indent+1);
				if(s==null) return null;
				head = s.substring(0, 1);
				if(head.equals(COMMA)){
					sb.append(COMMA+"\n"+ids);
					continue;
				}
				if(head.equals(ARRAY2)){
					sb.append("\n"+ids+ARRAY2);
					return s.substring(1);
				}
			}while(s!=null);
			return null;
		}
		if("0123456789.".contains(head)){
			int i = 0;
			while(Character.isDigit(s.charAt(i)) || s.charAt(i)=='.'){
				sb.append(s.charAt(i++));
			}
			s = s.substring(i);
			return s;
		}
		if(s.startsWith("null")){
			sb.append("null");
			s = s.substring(4);
			return s;
		}
		if(s.startsWith("true")){
			sb.append("true");
			s = s.substring(4);
			return s;
		}
		if(s.startsWith("false")){
			sb.append("false");
			s = s.substring(5);
			return s;
		}
		return s;
	}

	private Path getWatchApiData(String html, String encoding, String comment) {
		String text = html;
		// 動画ページのJSONを取り出す
		text = getXmlElement1(text, "body");	//body
		if(text==null)
			return null;
		text = getXmlElement1(text, "div");
			//div id="watchAPIDataContainer" style="display:none"
		if(text==null)
			return null;
		int start = text.indexOf(JSON_START);
		if(start < 0)
			return null;
		String json_end = "<";
		int end = (text+json_end).indexOf(json_end, start);	// end of JSON
		text = (text+json_end).substring(start, end);
		text = text.replace("&quot;", S_QUOTE2);
		// URLDecodeしない
		Path file0 = Path.mkTemp(videoTag+"_watchJ.txt");
		Path file = Path.mkTemp(videoTag+"_watchJ.xml");
		Mson dataApiMson;
		String xml;
		try {
			dataApiMson = Mson.parse(text);
		//	debug("dataApiMson:\n ", dataApiMson);
			watchApiJson = dataApiMson.toUnquoteString();
			xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
				+"<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">\n"
				+"<properties><comment>" + comment + "</comment>\n"
				+"<entry key=\"json\">" + text + "</entry>";
			Path.writeAllText(file, xml, encoding);
		}catch(Exception e){
			log.println("watchApiMson parse error");
			Path.writeAllText(file0, text, encoding);
			log.println("Saved watchApiData to "+file0.getPath());
			Path.unescapeStoreXml(file, text, comment);		//xml is property key:json val:JSON
		}
		log.println("Saved watchApiData to "+file.getPath());
		return file;
	}

	private String makeNewElement(String key, String val){
		if(val==null)
			val = "";
		return "<"+key+">"+val+"</"+key+">\n";
	}
	public String getJsonValue(String input, String key) {
		String r = null;
		if(input==null)
			return null;
		try {
			r = getJsonValue0(input, key);
			return unquote(r);
		} catch(Exception e){
			debug("\ngetJsonValue0(\""+key+"\"): error\n");
			r = getJsonValue1(input, key);
			debug("getJsonValue1(\""+key+"\"): "+unquote(r)+"\n");
			return unquote(r);
		}
	}
	public String getJsonValue0(String input, String key) throws Exception {
		Mson m = Mson.parse(input);
		return m.getValue(key);
	}
	public String getJsonValue1(String input, String key){
		String key1 = S_QUOTE2+key+S_QUOTE2+":";
		int index1 = input.indexOf(key1);
		if(index1 < 0) return null;
		int index2 = index1 + key1.length();
		char c2 = input.charAt(index2);
		String tail = input.substring(index2);
		if(c2=='{'){
			int count = 1;
			int i = index2+1;
			while(count > 0 && i < input.length()){
				char ce = input.charAt(i++);
				if(ce=='{') count++;
				else if(ce=='}') count--;
				else if(ce==C_ESCAPE) i++;
			}
			String r = (input+'}').substring(index2, i);
			return r;	// ハッシュ {x:v, y:u}
		}
		if(c2=='['){
			int count = 1;
			int i = index2+1;
			while(count > 0 && i < input.length()){
				char ce = input.charAt(i++);
				if(ce=='[') count++;
				else if(ce==']') count--;
				else if(ce==C_ESCAPE) i++;
			}
			String r = (input+']').substring(index2, i);
			return r;	// 配列 [X,Y,Z]
		}
		if(c2==C_QUOTE2){
			int i = index2+1;
			while(i < input.length()){
				char ce = input.charAt(i++);
				if(ce==C_QUOTE2) break;
				if(ce==C_ESCAPE) i++;
			}
			String r = (input + S_QUOTE2).substring(index2, i);
			return r;	// 文字列 "ABC"
		}
		// キーワード
		if(tail.startsWith("null")){
			return "null";
		}
		if(tail.startsWith("true")){
			return "true";
		}
		if(tail.startsWith("false")){
			return "false";
		}
		// else 他の文字 {
		if('0' <= c2 && c2 <= '9') {
			int i = index2 + 1;
			while(i < input.length()){
				char ce = input.charAt(i);
				if(ce!='.' && (ce < '0' || ce > '9')) break;
				i++;
			}
			String r = input.substring(index2, i);
			return r;	// 数字
		}
		return null;
	}

	private String makeNewJsonValue(String html, String key) {
		return makeNewElement(key, getJsonValue(html, key));
	}

	private String makeNewXmlElement(String html, String key) {
		return makeNewElement(key, getXmlElement(html, key));
	}
	public String getXmlElement(String input, String key){
		Pattern p = Pattern.compile("<"+key+">(.*)</"+key+">",Pattern.DOTALL);
		Matcher m = p.matcher(input);
		if(m.find()){
			return m.group(1);
		}
		return null;
	}

	public String getXmlElement1(String xml, String key){
		Pattern p = Pattern.compile("<"+key+"[^>]*>(.*)</"+key,Pattern.DOTALL);
		Matcher m = p.matcher(xml);
		String dest = "";
		if(m.find()){
			dest = m.group(1);
			return dest;
		}
		return dest;
	}

	public String getXmlElement2(String input, String key){
		Pattern p = Pattern.compile("<"+key+"[^>]*>([^<]*)</",Pattern.DOTALL);
		Matcher m = p.matcher(input);
		if(m.find())
			return m.group(1);
		return null;
	}

	public String getXmlAttribute(String input, String atribname){
		Pattern p = Pattern.compile("<[^>]*"+atribname+"=\"([^\"]+)\"[^>]*>",Pattern.DOTALL);
		Matcher m = p.matcher(input);
		if(m.find())
			return m.group(1);
		return null;
	}

	public Path getThumbUserFile(String userID, File userFolder){
		String url = HTTP_EXT_THUMBUSER + userID;
		log.print("Getting thumb User...");
		Path userHtml = null;
		try {
			HttpURLConnection con = urlConnectGET(url);
			if (con == null || con.getResponseCode() != HttpURLConnection.HTTP_OK){
				log.println("ng.\nCan't getThumbUser:" + url);
				return null;
			}
		//	Cookie.update(detectCookie(con));

			String encoding = con.getContentEncoding();
			if (encoding == null){
				encoding = "UTF-8";
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(con
					.getInputStream(), encoding));
			log.print("ok.\nSaving thumb user...");
			String ret;
			StringBuilder sb = new StringBuilder();
			while ((ret = br.readLine()) != null) {
				Stopwatch.show();
				sb.append(ret + "\n");
			}
			br.close();
			con.disconnect();
			PrintWriter pw;
			userHtml  = new Path(userFolder,userID + ".htm");
			pw = new PrintWriter(userHtml, encoding);
			pw.write(sb.toString());
			pw.flush();
			pw.close();
			log.println("ok.");
		} catch (IOException ex) {
			log.printStackTrace(ex);
			return null;
		}
		return userHtml;
	}

	public Path getUserInfoFile(String userID, File userFolder) {
		String url = HTTP_WWW_NICOVIDEO_USER + userID;
		log.print("Getting User Info...");
		Path userHtml = null;
		try {
			HttpURLConnection con = urlConnectGET(url);
			if (con == null || con.getResponseCode() != HttpURLConnection.HTTP_OK){
				log.println("ng.\nCan't getUserInfo:" + url);
				return null;
			}
		//	Cookie.update(detectCookie(con));

			String encoding = con.getContentEncoding();
			if (encoding == null){
				encoding = "UTF-8";
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(con
					.getInputStream(), encoding));
			log.print("ok.\nSaving user info...");
			String ret;
			StringBuilder sb = new StringBuilder();
			while ((ret = br.readLine()) != null) {
				Stopwatch.show();
				sb.append(ret + "\n");
				if(ret.contains("</title>")){
					break;
				}
			}
			br.close();
			con.disconnect();
			String text = sb.toString();
			int index = text.indexOf("<title");
			if (index < 0){
				index = 0;
			}
			text = text.substring(index);
			PrintWriter pw;
			userHtml  = new Path(userFolder,userID + ".htm");
			pw = new PrintWriter(userHtml, encoding);
			pw.write(text);
			pw.flush();
			pw.close();
			log.println("ok.");
		} catch (IOException ex) {
			log.printStackTrace(ex);
			return null;
		}
		return userHtml;
	}

	public boolean getThumbnailJpg(String url, File thumbnalJpgFile) {
		log.print("Getting thumbnail...");
		try {
			HttpURLConnection con = urlConnectGET(url);
			if (con == null || con.getResponseCode() != HttpURLConnection.HTTP_OK){
				log.println("ng.\nCan't getThumbnailJpg:" + url);
				return false;
			}
		//	Cookie.update(detectCookie(con));

			InputStream is = con.getInputStream();
			FileOutputStream fos = new FileOutputStream(thumbnalJpgFile);
			byte[] buf = new byte[4096];
			log.print("ok.\nSaving thumbnail...");
			int len = 0;
			while ((len = is.read(buf, 0, buf.length)) > 0) {
				fos.write(buf, 0, len);
				Stopwatch.show();
			}
			log.println("ok.");
			is.close();
			fos.flush();
			fos.close();
			con.disconnect();
		} catch (IOException ex) {
			log.printStackTrace(ex);
			return false;
		}
		return true;
	}

	public String getThumbInfoData() {
		return thumbInfoData;
	}

	public List<String> getNicotags(){
		return nicoTaglist;
	}

	public String getNicocategory(){
		return nicoCat;
	}
	class HeartBeatDmc extends TimerTask implements Runnable {
		private String dmcHBUrl1 = "";
		private String dmcHBUrl2 = "";
		private Path postXml = null;

		public HeartBeatDmc(String url1, String url2, Path xml){
			dmcHBUrl1 = url1;
			dmcHBUrl2 = url2;
			postXml = xml;
		}

		@Override
		public void run() {
			OutputStream os = null;
			PrintWriter pw = null;
			Path response = Path.mkTemp(videoTag+"_HBresponce.xml");
			String url = null;
			HttpURLConnection con = null;
			// POST dmcHBUrl
			try {
				url = dmcHBUrl1+sessionID+dmcHBUrl2;
				debug("\n");
				debug("■heartbeat URL<" + url + ">\n");
				//	con = urlConnect(url, "POST", null, true, true, "keep-alive", false);
				con = (HttpURLConnection)(new URL(url)).openConnection(ConProxy);
				con.setDoOutput(true);
				HttpURLConnection.setFollowRedirects(false);
				con.setInstanceFollowRedirects(false);
				con.setRequestMethod("POST");
				con.setRequestProperty("User-Agent", "Java/Saccubus-"+MainFrame_AboutBox.rev);
				con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
				con.setRequestProperty("Accept-Language", "ja,en-US;q=0.7,en;q=0.3");
			//	con.setRequestProperty("Accept-Encoding", "deflate");
			//	con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				con.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
				con.addRequestProperty("DNT", "1");
				con.addRequestProperty("Connection", "keep-alive");
				debug("■heartbeat Connect: POST,DoOutput,Connection keep-alive\n");
				connect(con);
				debug("■heartbeat write: " + postXmlData.length()+"\n");
				os = con.getOutputStream();
				os.write(postXmlData.getBytes());
				os.flush();
				os.close();
				int code = con.getResponseCode();
				String mes = con.getResponseMessage();
				debug("■heartbeat Response: " + Integer.toString(code) + " " + mes+"\n");
				if (code < HttpURLConnection.HTTP_OK || code >= HttpURLConnection.HTTP_BAD_REQUEST) { // must 200 <= <400
					log.println("\nheartbeat Can't get HeartBeat response:" + mes);
					throw new IOException("Heartbeat response error");
				}
				responseXmlData = readConnection(con);
				con.disconnect();
				if(responseXmlData.contains("status=\"20")){
					postXmlData = "<session>"+getXmlElement(responseXmlData, "session")+"</session>";
					sessionID = getXmlElement(responseXmlData, "id");
					if(sessionID==null){
						throw new IOException("Heartbeat data error");
					}
				}else{
					// BAD response not replace POST DATA
					if(responseXmlData.contains("token_accept_time_limit")){
						setExtraError("98 dmc Token timeout");
					}
				}
				debug("■heartbeat sessionID: "+sessionID+"\n");
				// save all response
				pw = new PrintWriter(response);
				pw.write(responseXmlData);
				pw.flush();
				pw.close();
				debug("■heartbeat session response: "+responseXmlData.length()+"\n");
				debug("■heartbeat response write to: "+postXml.getPath()+"\n");
			} catch (IOException e) {
				log.printStackTrace(e);
			}finally{
				try{
					if(con!=null) con.disconnect();
				} catch(Exception e){};
				try{
					if(os!=null) os.close();
				} catch(IOException e){};
				if(pw!=null) pw.close();
			}
		}
	}

}
