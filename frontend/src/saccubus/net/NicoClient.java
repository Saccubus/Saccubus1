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
import java.io.PrintStream;
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
	private static boolean Logged_in = false;
	private final Proxy ConProxy;
	boolean Debug = false;
	private final NicoMap nicomap;
	private Stopwatch Stopwatch;
	private Path titleHtml = null;
	private Logger log;
	private boolean isHtml5;
	private boolean isHtml5Ok = false;
	private BrowserInfo browserInfo = null;

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
	 * @param browser 
	 * @param proxy
	 * @param proxy_port
	 */
	public NicoClient(final String user, final String pass, BrowserInfo browser,
			final String proxy, final int proxy_port, final Stopwatch stopwatch,
			Logger logger, boolean is_html5) {
		log = logger;
		User = user;
		Pass = pass;
		Stopwatch = stopwatch;
		nicomap = new NicoMap();
		ConProxy = conProxy(proxy, proxy_port);
		isHtml5 = is_html5;
		browserInfo = browser;
		// ログイン
		login();
		setLoggedIn(loginCheck());
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
	public NicoClient(final BrowserInfo browser,
			final String proxy, final int proxy_port, final Stopwatch stopwatch,
			Logger logger, boolean is_html5) {
		browserInfo = browser;
		log = logger;
		User = "";
		Pass = "";
		Stopwatch = stopwatch;
		nicomap = new NicoMap();
		isHtml5 = is_html5;
		ConProxy = conProxy(proxy, proxy_port);
		String user_session = browserInfo.getLastUsersession();
		if (user_session == null || user_session.isEmpty()){
			log.println("Invalid user session" + browserInfo.getName());
			setExtraError("セッションを取得出来ません");
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
						setLoggedIn(true);	// ログイン済みのハズ
						setExtraError("");
						return;
					}
					Cookie = new NicoCookie();
					log.println("Fault user session " + browserInfo.getName());
					browserInfo.addFaultUserSession(Cookie.getUsersession());
					setExtraError("セッションが無効です");
				}
			}
			setLoggedIn(false);
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
	void debugPrettyPrint(String s, Mson m){
		if(Debug){
			log.print(s);
			m.prettyPrint(log);
		}
	}
	void debugPrettyPrint(String s, String input) {
		if(Debug){
			log.print(s);
			Mson.prettyPrint(input,log);
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
			if(url==null || url.isEmpty()){
				log.println("url is null or empty.");
				setExtraError("URLが取得できません。");
				return null;
			}
			String host = "";
			if(url.startsWith("http://"))
				host = url.substring("http://".length())+"/";
			else if(url.startsWith("https://"))
				host = url.substring("https://".length())+"/";
			else
				host += "/";
			host = host.substring(0,host.indexOf("/"));
			debug("■HOST<" + host + ">\n");
			if(host==null || host.isEmpty()){
				log.println("host is null or empty.");
				setExtraError("HOSTが取得できません。");
				return null;
			}
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
		//Stopwatch.show();
		con.connect();
	}

	private String readConnection(HttpURLConnection con){
		try {
			//Stopwatch.show();
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
			//Stopwatch.show();
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
	private static synchronized void setLoggedIn(boolean b){
		Logged_in = b;
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
		Pattern p = Pattern.compile("&#([0-9]+);|&#(x[0-9a-fA-F]+);");
		Matcher m = p.matcher(str);
		StringBuffer sb = new StringBuffer();
		String ch;
		while (m.find()) {
			ch = m.group();
			if(ch.startsWith("x")) ch = "0"+ch;
			try {
				ch = new String(Character.toChars((int)Integer.decode(ch)));
			}catch(NullPointerException|NumberFormatException e){
				ch = "";
			}
			m.appendReplacement(sb, ch);
		}
		//最後に追加
		m.appendTail(sb);
		str = sb.toString();
		//MS-DOSシステム(ffmpeg.exe)で扱える形に(UTF-8のまま)
		str = toSafeWindowsName(str, "MS932");
		return str;
	}
	public static String eRaseMultiByteMark(String str){
		str = str.replaceAll("[／￥？＊：｜“＜＞．＆；]", "");
		if(str.isEmpty()) str = "null";
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
			browserInfo.setLastUsersession(Cookie.getUsersession());
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
				//Stopwatch.show();
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
					log.println("video history flash ok.");
				}
			}else{
				log.println("JSON_START not found, try HTML5_JSON");
				if(ss.contains(JSON_START2)){
					if(extractDataApiDataJson(ss, encoding, url)!=null){
						log.println("video history html5 ok.");
						isHtml5Ok = true;
					} else {
						log.println("video history html5 NG.");
						isHtml5Ok = false;
					}
				}
			}
//			if(VideoUrl==null || VideoUrl.isEmpty()){
//				log.println("video URL ["+VideoUrl+"] is invalid. ");
//				nicomap.printAll(log);
//				return false;
//			}
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
	private String optionalThreadID = "";	// normal Comment ID when Community DOUGA
	private String nicosID = "";
	private boolean economy = false;
	private String ownerFilter;			// video owner filter（replace）
	public boolean getVideoInfo(String tag, String watchInfo, String time, boolean saveWatchPage) {
		if(videoTag==null)
			videoTag = tag;
		if (!getVideoHistoryAndTitle(tag, watchInfo, saveWatchPage)) {
			return false;
		}
		if(isHtml5Ok)
			return true;

		String url = null;
		HttpURLConnection con;
		try {
			if(!nicomap.containsKey("flvInfo")){
				// flvinfo is same as FLAPI_GETFLV response
				url = HTTP_FLAPI_GETFLV + tag;
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
				con = urlConnectGET(url + watchInfo);
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
				nicomap.put("flvInfo", ret);
				nicomap.putArrayURLDecode(ret, encoding);
				// flvinfo end
			}
			if (Debug){
				nicomap.printAll(log);
			}
			ThreadID = nicomap.get("thread_id");
			VideoUrl = nicomap.get("url");
			MsgUrl = nicomap.get("ms");
			if(MsgUrl!=null && MsgUrl.contains("_nmsg_")){
				MsgUrl = nicomap.get("ms_sub");
				log.println("reset MsgUrl: "+MsgUrl);
			}
			UserID = nicomap.get("user_id");
			userKey = nicomap.get("userkey");
			if (optionalThreadID.isEmpty() && nicomap.containsKey("optional_thread_id")){
				optionalThreadID = nicomap.get("optional_thread_id");
				if(videoTag.equals(optionalThreadID)){
					// html5の場合逆になっているようだ
					// ThreadKey が引けるのは メインthreadの方だけ
					// chanelの場合 メイン=チャンネル=threadKey optionalは何もない
					// communityの場合 メイン=コミュニティ=threadKey optionalはsm動画のコメント
					optionalThreadID = ThreadID;
					ThreadID = videoTag;
					log.println("reset ThreadID: "+ThreadID);
				}
				log.println("OptionalThreadID: "+optionalThreadID);
			}
			if (nicosID.isEmpty() && nicomap.containsKey("nicos_id")){
				nicosID = nicomap.get("nicos_id");
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
				|| MsgUrl == null || UserID == null
				|| ThreadID.isEmpty() || VideoUrl.isEmpty()
				|| MsgUrl.isEmpty() || UserID.isEmpty()) {
				log.println("ng.\nCan't get video information keys.");
				if(VideoUrl==null){
					log.println("Video url is null. maybe Forbidden or Paid");
					return false;
				}
				try {
					con = urlConnectGET(url + watchInfo);
					if(!loginCheck(con)){
						log.println("Can't logged In.");
					}
				}catch(NullPointerException e){
					log.println("Can't logged In.");
				}
				return false;
			}
			//economy  = VideoUrl.toLowerCase().contains("low");
			log.println("videoinfo ok.");
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
			if (optionalThreadID!=null && !optionalThreadID.isEmpty()){
				log.println("OptionalThreadID:<" + optionalThreadID + ">");
			}
			if (nicosID!=null && !nicosID.isEmpty()){
				log.println("nicosID:<" + nicosID + ">");
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
	private String signature;
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
				//Stopwatch.show();
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
			//Stopwatch.show();
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
			int threadn = ConvertManager.getNumThread();
			int convn = ConvertManager.getNumRun();
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
					if(max_size <= min_size){
						setExtraError("97 最小限度サイズと同じか小さいのでダウンロード中止");
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
				log.println("Downloading dmc(S) video..."+(max_size>>20)+"MiB. each "+(downloadLimit>>10)+"KiB");
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
					debug("\nContentType:" + ContentType + ", " + ContentDisp);
					contentRange = dmcmap.get("Content-Range");
					debug("\nContent-Range: "+contentRange);
					debug("\nDownload limit = "+(downloadLimit>>10)+"KiB, bitrate = "+(byterate/125)+"kbps");
					debug("\nresume Downloading dmc(S) video...");
					os = new FileOutputStream(video, true);
					int read = 0;
					while ((read = is.read(buf, 0, buf.length)) > 0) {
						debugsAdd(read);
						resumed += read;
						os.write(buf, 0, read);
						sendStatus(status, "dmc動画(S)", max_size, resumed, starttime);
						//Stopwatch.show();
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
					debug("\nresumed size = "+resumed+", max_size="+max_size);
					is.close();
					os.flush();
					os.close();
					//con.disconnect();
					if(resumed < max_size)
						debug("\nDownload not finished, continue.\n");
					//Stopwatch.show();
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
					//Stopwatch.show();
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
					//Stopwatch.show();
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
						//Stopwatch.show();
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
	private String userKey = null;
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
		},
		NICOS{
			@Override
			public String dlmsg(){ return "ニコスコメント"; }
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
		File dl = downloadComment(file, status, ThreadID, NeedsKey, back_comment, CommentType.USER, flag, useNewComment, isAppend);
		if(dl==null && retry_threadkey){
			if(optionalThreadID!=null && !optionalThreadID.equals(videoTag)){
				// may be optionalThread is main and needs_key
				String thread = ThreadID;
				ThreadID = optionalThreadID;
				optionalThreadID = thread;
				dl = downloadComment(file, status, ThreadID, NeedsKey, back_comment, CommentType.USER, flag, useNewComment, isAppend);
			}
		}
		return dl;
	}

	public File getCommentJson(final File file, final JLabel status, final String back_comment,
			final String time, final ConvertStopFlag flag){
		if(Debug){
			String comjson = downloadCommentJson(status, flag, back_comment);
			if(comjson==null || comjson.isEmpty()){
				log.println("\n["+videoTag+"]can't download comment json.");
				return null;
			}
			Path.writeAllText(file, comjson, "UTF-8");
			log.println("\nwrite comment json to "+file);
		}
		return file;
	}
	public File getOwnerComment(final File file, final JLabel status, final ConvertStopFlag flag) {
		return downloadComment(file, status, ThreadID, false, STR_OWNER_COMMENT, CommentType.OWNER, flag, false, false);
	}

	public File getNicosComment(final File file, final JLabel status, final String nicos_id,
			final String back_comment, final String time, final ConvertStopFlag flag,
			final int comment_mode, final boolean isAppend) {
		Official = "";
		if (time != null && !time.isEmpty()){
		 	WayBackKey = "0";
			if (!getWayBackKey(time)) { // WayBackKey
				log.println("It may be wrong Date.");
			}
		}
		boolean useNewComment = true;
		if(comment_mode == 2 || comment_mode == 0 && !hasNewCommentBegun){
			useNewComment = false;
		}
		return downloadComment(file, status, nicos_id, false, back_comment, CommentType.NICOS, flag, useNewComment, isAppend);
	}

	public File getOptionalThread(final File file, final JLabel status, final String optionalThreadID,
			final String back_comment, final String time, final ConvertStopFlag flag,
			final int comment_mode, final boolean isAppend) {
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
		return downloadComment(file, status, optionalThreadID, false, back_comment, CommentType.OPTIONAL, flag, useNewComment, isAppend);
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

	private String commentCommand2009(CommentType commentType, String thread, boolean needskey, String back_comment, String res_from){
		String req;
//		String resfrom;
//		if(!back_comment.endsWith("-")){
//			// normal
//			if (res_from.isEmpty()){
//				// overwrite file
//				resfrom = "\" res_from=\"-" + back_comment;
//			}else{
//				// append file mode using res_from param (this is test)
//				resfrom = "\" res_from=\"" + res_from;
//			}
//		}else {
//			// for Debug, input comment_no "12345-" etc.
//			resfrom = "\" res_from=\"" + back_comment;
//		}
		String user_key = "";
		if(userKey!=null && !userKey.isEmpty())
			user_key = "\" userkey=\""+userKey;
		String wayback = "";
		if(!WayBackKey.isEmpty()){
			wayback = "\" when=\"" + WayBackTime + "\" waybackkey=\"" + WayBackKey;
			user_key = "";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("<packet>");
		sb.append("<thread thread=\"" + thread);
		sb.append("\" version=\"20090904");
		//sb.append(resfrom);
		sb.append("\" user_id=\"" + UserID);
		if(needskey){
			sb.append(Official);
		}else{
			sb.append(user_key);
		}
		if(!"0".equals(WayBackKey)){
			sb.append(wayback);
		}
		sb.append("\" scores=\"1");	//NGscore
		sb.append("\" nicoru=\"1");
		sb.append("\" with_global=\"1");
		sb.append("\"/>");
		//thread end, thread_leaves start
		sb.append("<thread_leaves thread=\"" + thread);
		sb.append("\" version=\"20090904");
		//sb.append(resfrom);
		sb.append("\" user_id=\"" + UserID);
		if(needskey){
			sb.append(Official);
		}else{
			sb.append(user_key);
		}
		if(!"0".equals(WayBackKey)){
			sb.append(wayback);
		}
		sb.append("\" scores=\"1");	//NGscore
		sb.append("\" nicoru=\"1");
		//sb.append("\" with_global=\"1");
		sb.append("\">0-");	//>0-10:100,1000<
		sb.append((VideoLength + 59) / 60);
		sb.append(":100,");
		sb.append(back_comment);
		sb.append("</thread_leaves>");
		sb.append("</packet>");
		req = sb.toString();
		return req;
	}

	private String getOfficial(String thread, boolean needs_key){
		String officialkey = "";
		if(needs_key){
			if(force184 == null || threadKey == null){
				if(!getOfficialOption(thread)){
					return null;
				}
			}
			officialkey =
				  "\" threadkey=\"" + threadKey
				+ "\" force_184=\"" + force184;
		}
		return officialkey;
	}

	private File downloadComment(final File file, final JLabel status, String thread,
			boolean needs_key, String back_comment, CommentType commentType, final ConvertStopFlag flag,
			boolean useNewComment, boolean isAppend) {
		log.print("Downloading " + commentType.toString().toLowerCase()
				+" comment, size:" + back_comment + "...");
		//String official = "";	/* 公式動画用のkey追加 */
		Official = getOfficial(thread, needs_key);
		if(Official==null){
			return null;
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
				}
				if(!isAppend){
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
				req = commentCommand2009(commentType, thread, needs_key, back_comment, lastNo);
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
				//Stopwatch.show();
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
				log.println("ng.\nNull response. retry with community thread");
				Official = "";
				if(!retry_threadkey){
					retry_threadkey  = true;
				 	threadKey = null;
				 	force184 = null;
				} else {
					retry_threadkey = false;
				}
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
				Official = "";
				if(!retry_threadkey){
					retry_threadkey  = true;
				 	threadKey = null;
				 	force184 = null;
				} else {
					retry_threadkey = false;
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
	private String backcomment;

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
			log.println("ng. Not logged in. "+browserInfo.getName()+" authflag=" + auth);
			log.println("last_user_session="+browserInfo.getLastUsersession());
			con.disconnect();
			return false;
		}
		Cookie.update(new_cookie);
		debug("\n■Now Cookie is<" + Cookie.toString() + ">\n");
		browserInfo.setLastUsersession(Cookie.getUsersession());
		debug("■last_user_session is<" + browserInfo.getLastUsersession() + ">\n");
		log.println("loginCheck ok.");
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


	private String commentJsonPost2009(String thread, String optional, String threadkey) {
		StringBuilder sb = new StringBuilder();
		int p = 0;
		sb.append("[{\"ping\":{\"content\":\"rs:0\"}}");
		if(optional==null || optional.isEmpty()){
			sb.append(postJsonData(p++, thread, userKey, false, false));
			sb.append(postJsonData(p++, thread, userKey, true, false));
		}else{
			sb.append(postJsonData(p++, thread, threadkey, false, true));
			sb.append(postJsonData(p++, thread, threadkey, true, true));
			sb.append(postJsonData(p++, optional, userKey, false, false));
			sb.append(postJsonData(p++, optional, userKey, true, false));
		}
		sb.append(",{\"ping\":{\"content\":\"rf:0\"}}]");
		return sb.substring(0);
	}
	private String postJsonData(int n, String thread, String key, boolean isleaf, boolean needs_key){
		StringBuilder sb = new StringBuilder();
		sb.append(",{\"ping\":{\"content\":\"ps:"+n+"\"}}");
		if(!isleaf)
			sb.append(",{\"thread\":{");
		else
			sb.append(",{\"thread_leaves\":{");
		sb.append("\"thread\":\""+thread+"\"");
		if(!isleaf){
			sb.append(",\"version\":\"20090904\"");
		}
		sb.append(",\"language\":0");
		sb.append(",\"user_id\":\""+UserID+"\"");
		if(needs_key)
			sb.append(",\"force_184\":\"1\"");
		if(!isleaf)
			sb.append(",\"with_global\":1");
		else
			sb.append(",\"content\":\"0-"+((VideoLength+59)/60)+":100,"+backcomment+"\"");	//0-10:100,1000
		sb.append(",\"scores\":1,\"nicoru\":0");
		if(key!=null && !key.isEmpty()){
			if(!needs_key)
				sb.append(",\"userkey\":\""+key+"\"");
			else
				sb.append(",\"threadkey\":\""+key +"\"");
		}
		sb.append("}},{\"ping\":{\"content\":\"pf:"+n+"\"}}");
		return sb.substring(0);
	}
	//	Req Post data =
//		[	//以下では整形したが実際は改行も空白もインデントも無い
//			{"ping":{"content":"rs:0"}},	//R start 0
//			{"ping":{"content":"ps:0"}},	//  P start 0
//			{"thread":{
//				"thread":"9999999993",	//smスレッド?オプショナルスレッド?
//				"version":"20090904",
//				"language":0,
//				"user_id":ユーザーID,
//				"with_global":1,
//				"scores":1,
//				"nicoru":0,
//				"userkey":ユーザーキー}
//			},
//			{"ping":{"content":"pf:0"}},	//  P finish 0
//			{"ping":{"content":"ps:1"}},	//  P start 1
//		  	{"thread_leaves":{
//				"thread":"999999993",
//				"language":0,
//				"user_id":ユーザーID,
//				"content":"0-3:100,250",	//3分の動画  250コメント
//				"scores":1,
//				"nicoru":0,
//				"userkey":ユーザーキー}
//			},
//		  	{"ping":{"content":"pf:1"}},	//  P finish 1
//		  	{"ping":{"content":"ps:2"}},	//  P start 2
//		  	{"thread":{
//				"thread":"9999999994",	//コミュニティthread
//				"version":"20090904",
//				"language":0,
//				"user_id":ユーザーID,
//				"force_184":"1",
//				"with_global":1,
//				"scores":1,
//				"nicoru":0,
//				"threadkey":スレッドキー}
//			},
//			{"ping":{"content":"pf:2"}},	//  P finish 2
//			{"ping":{"content":"ps:3"}},	//  P start 3
//			{"thread_leaves":{
//				"thread":"9999999994",
//				"language":0,
//				"user_id":ユーザーID,
//				"content":"0-3:100,250",	//3分の動画  250コメント
//				"scores":1,
//				"nicoru":0,
//				"force_184":"1",
//				"threadkey":スレッドキー}
//			},
//			{"ping":{"content":"pf:3"}},	//  P finish 3
//			{"ping":{"content":"rf:0"}}		//R finish 0
//		]
//	Response Header =
//		HTTP/1.1 200 OK
//		Access-Control-Allow-Origin: *
//		Access-Control-Allow-Methods: POST,GET,OPTIONS,HEAD
//		Access-Control-Allow-Headers: Content-Type
//		Vary: Accept-Encoding
//		Cache-Control: max-age=0
//		Content-Encoding: gzip
//		Content-Type: text/json
//		Connection: Keep-Alive
//		Keep-Alive: timeout=15, max=100
//		Content-Length: 15009
//	Response Data =
//		[
//			{"ping": {"content": "rs:0"}},
//			{"ping": {"content": "ps:0"}},
//			{"thread": {
//				"resultcode": 0,
//				"thread": "9999999993",
//				"server_time": 1484681074,
//				"last_res": 7496,
//				"ticket": "0x38b3de9b",
//				"revision": 1,
//				"click_revision": 48}
//			},
//			{"leaf": {"thread": "9999999993","count": 5140}},
//			{"leaf": {"thread": "9999999993","leaf": 1,"count": 2140}},
//			{"leaf": {"thread": "9999999993","leaf": 2,"count": 216}},
//			{"global_num_res": {"thread": "9999999993","num_res": 7670}},
//			{"ping": {"content": "pf:0"}},
//			{"ping": {"content": "ps:1"}},
//			{"thread": {
//				"resultcode": 0,
//				"thread": "9999999993",
//				"server_time": 1484681074,
//				"last_res": 7496,
//				"ticket": "0x38b3de9b",
//				"revision": 1,
//				"click_revision": 48}
//			},
//			{"chat": {
//				"thread": "9999999993",
//				"no": 4877,
//				"vpos": 13854,
//				"leaf": 2,
//				"date": 1401116767,
//				"premium": 1,
//				"anonymity": 1,
//				"user_id": ユーザーID_4877,
//				"mail": "184",
//				"content": 通常コメント4877}
//			},
//			中略
//			{"chat": {
//				"thread": "9999999993",
//				"no": 7496,
//				"vpos": 2426,
//				"date": 1484578038,
//				"date_usec": 959793,
//				"anonymity": 1,
//				"user_id": "wyT4hdcnpRa5gKm7EiagcCsO20A",
//				"mail": "184",
//				"content": 通常コメント7496}
//			},
//			{"ping": {"content": "pf:1"}},
//			{"ping": {"content": "ps:2"}},
//			{"thread": {
//				"resultcode": 0,
//				"thread": "9999999994",
//				"server_time": 1484681074,
//				"last_res": 52,
//				"ticket": "0x365b5d16",
//				"revision": 1}
//			},
//			{"leaf": {"thread": "9999999994","count": 41}},
//			{"leaf": {"thread": "9999999994","leaf": 1,"count": 10}},
//			{"leaf": {"thread": "9999999994","leaf": 2,"count": 1}},
//			{"global_num_res": {"thread": "9999999994","num_res": 52}},
//			{"ping": {"content": "pf:2"}},
//			{"ping": {"content": "ps:3"}},
//			{"thread": {
//				"resultcode": 0,
//				"thread": "9999999994",
//				"server_time": 1484681074,
//				"last_res": 52,
//				"ticket": "0x365b5d16",
//				"revision": 1}
//			},
//			{"chat": {
//				"thread": "9999999994",
//				"no": 1,
//				"vpos": 4014,
//				"date": 1324739748,
//				"premium": 1,
//				"anonymity": 1,
//				"user_id": ユーザーID_1,
//				"content": コミュニティコメント1}
//			},
//			中略
//			{"chat": {
//				"thread": "9999999994",
//				"no": 51,
//				"vpos": 5791,
//				"date": 1474285779,
//				"date_usec": 890564,
//				"premium": 1,
//				"anonymity": 1,
//				"user_id": ユーザーID_51,
//				"content": コミュニティコメント51}
//			},
//			{"ping": {"content": "pf:3"}},
//			{"ping": {"content": "rf:0"}}
//		]
//

//	Url = http://nmsg.nicovideo.jp/api.json/
//	Req Header =
//		POST /api.json/ HTTP/1.1
//		Host: nmsg.nicovideo.jp
//		User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:50.0) Gecko/20100101 Firefox/50.0
//		Accept: */*
//		Accept-Language: ja,en-US;q=0.7,en;q=0.3
//		Accept-Encoding: gzip, deflate
//		Content-Type: text/plain;charset=UTF-8
//		Origin: http://www.nicovideo.jp
//		Referer: http://www.nicovideo.jp/watch/9999999994	//コミュニティ動画(スレッド番号)
//		Content-Length: 1051
//		DNT: 1
//		Connection: keep-alive
	public String downloadCommentJson(JLabel status, ConvertStopFlag flag, String back_comment){
		String url = "http://nmsg.nicovideo.jp/api.json/";
		InputStream is = null;
		OutputStream os = null;
		HttpURLConnection con = null;
		StringBuffer fosb = new StringBuffer();
		String retComment = null;
		backcomment = back_comment;
		try {
			long start0 = Stopwatch.getElapsedTime(0);
			con = urlConnect(url, "POST", Cookie, true, true, "keep-alive", true);
			os = con.getOutputStream();
			String postdata;
			postdata = commentJsonPost2009(ThreadID, optionalThreadID, threadKey);
			debug("\n■write:" + postdata + "\n");
			os.write(postdata.getBytes());
			os.flush();
			os.close();
			int code = con.getResponseCode();
			debug("■Response:" + code + " " + con.getResponseMessage() + "\n");
			if (code != HttpURLConnection.HTTP_OK) {
				log.println("ng.\nCan't download JSON comment:" + url);
				return null;
			}
			is = con.getInputStream();
			int max_size = 0;
			try {
				String content_length = con.getHeaderField("Content-length");
				if(content_length!=null)
					max_size = Integer.parseInt(content_length);
			} catch(NumberFormatException e){
				max_size = 0;
			}
			int size = 0;
			int read = 0;
			//debugsInit();
			while ((read = is.read(buf, 0, buf.length)) > 0) {
				//debugsAdd(read);
				fosb.append(new String(buf, 0, read, "UTF-8"));
				size += read;
				sendStatus(status, "comment JSON ", max_size, size, start0);
				//Stopwatch.show();
				if (flag.needStop()) {
					log.println("Stopped.");
					return null;
				}
			}
			//debugsOut("■read+write statistics(bytes) ");
			log.println("ok.");
			is.close();
			// add OwnerFilter to the end of owner comment file before </packet>
			// fos.close();
			con.disconnect();
			retComment = fosb.substring(0);
			return retComment;
		} catch (IOException e) {
			log.printStackTrace(e);
		}finally{
			if(is!=null)
				try { is.close(); } catch(IOException e1){}
			if(os!=null)
				try { os.close(); } catch(IOException e2){}
			if(con!=null)
				con.disconnect();
		}
		return null;
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
		return optionalThreadID;
	}

	public String getNicosID() {
		return nicosID;
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
	private String dmcInfoDec;
	private String dmcToken;
	private String dmcTokenUnEscape;
	private String sessionApi;
	private Path sessionXml;
	//private String video_src;
	//private String audio_src;
	//private String videos;
	private ArrayList<String> videolist;
	//private String audios;
	private ArrayList<String> audiolist;
	private String apiUrls;
	private String sessionData;
	private String player_id;
	private String apiSessionUrl;
	private String videoTag;
	private String recipe_id;
//	private String t_created_time;
	private String service_user_id;
	private String dataApiJson;
	private String priority;
	private ArrayList<String> nicoTaglist = new ArrayList<>();
	private String nicoCat;
	private Mson dataApiMson;
	private Mson watchApiMson;
	public boolean serverIsDmc(){
		return "1".equals(isDmc) && !sessionApi.isEmpty();
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
				//Stopwatch.show();
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
				log.println("checking saved titlepage JSON... ");
				if(watchApiJson!=null){
					log.println("flash ok.");
				} else if(dataApiJson!=null){
					log.println("html5 ok.");
					isHtml5Ok = true;
				}else{
					log.println("NG.");
					isHtml5Ok = false;
				}
			}
			if(s.indexOf("status=\"ok\"") < 0 && titleHtml!=null){
				// 可能ならthumbXmlをtitleHtmlから構成する
				sb = new StringBuilder();
				sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
				sb.append("<nicovideo_thumb_response status=\"ok\">\n");
				sb.append("<thumb>\n");
				sb.append(makeNewXmlElement(s,"code"));
				sb.append(makeNewElement("video_id",tag));
				sb.append(makeNewElement("title",title));
				if(isHtml5Ok){
					Mson m_dataApi = dataApiMson;
					if(m_dataApi==null){
						m_dataApi = Mson.parse(getDataApiJson());
					}
					Mson m_video = m_dataApi.get("video");
					String description = m_video.getAsString("description");
					if(description==null)
						description = "";
					description = description.replace("&quot;", "”")
						.replace("&lt;", "(").replace("&gt;", ")")
						.replaceAll("\\(br ?/?\\)","\n");
					sb.append(makeNewElement("description", description));
					String thumbnailURL = m_video.getAsString("thumbnailURL");
					if(thumbnailURL==null)
						thumbnailURL="";
					sb.append(makeNewElement("thumbnail_url",thumbnailURL));
					if(ContentType==null)
						ContentType = m_video.getAsString("movieType");
					sb.append(makeNewElement("movie_type",ContentType));
					if(altTag.isEmpty())
						altTag = m_video.getAsString("id");
					if(altTag.isEmpty())
						altTag = tag;
					sb.append(makeNewElement("watch_url",HTTP_WWW_NICOVIDEO_WATCH +altTag));
					sb.append(makeNewElement("thumb_type","video"));
					Mson m_tags = m_dataApi.get("tags");
					if(m_tags.getSize()>0){
						sb.append("<tags domain=\"jp\">\n");
						debug("\ntags.length="+m_tags.getSize());
						for(int i=0; i< m_tags.getSize(); i++){
							Mson m_hash = m_tags.get(i);
							debug("\nhash="+m_hash);
					// hash = {"name":"ニコニコ技術部","isCategory":true,"isDictionaryExists":true,"isLocked":true}
							String t = m_hash.getAsString("name");
							String cat = m_hash.getAsString("isCategory");
							String lck = m_hash.getAsString("isLocked");
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
					Mson m_owner = m_dataApi.get("owner");
					String user_id = m_owner.getAsString("id");
					if(user_id==null)
						user_id = "";
					sb.append(makeNewElement("user_id",user_id));
					String nickname = m_owner.getAsString("nickname");
					if(nickname!=null)
						sb.append(makeNewElement("user_nickname",nickname));
					sb.append(makeNewElement("user_icon_url",m_owner.getAsString("iconURL")));
					sb.append("</thumb>\n");
					sb.append("</nicovideo_thumb_response>\n");
					s = sb.substring(0);
				}else{
					// flash page
					Mson m_watchApi = watchApiMson;
					if(m_watchApi==null){
						m_watchApi = Mson.parse(getWatchApiJson());
					}
					String description = m_watchApi.getAsString("description");
					if(description==null)
						description = "";
					else
						description = description.replace("&quot;", "”")
							.replace("&lt;", "(").replace("&gt;", ")")
							.replaceAll("\\(br ?/?\\)","\n");
					sb.append(makeNewElement("description",description));
					String thumbUrl = m_watchApi.getAsString("thumbnail");
					if(thumbUrl==null)
						thumbUrl = m_watchApi.getAsString("thumbImage");
					if(thumbUrl==null) thumbUrl="";
					sb.append(makeNewElement("thumbnail_url",thumbUrl));
					if(ContentType==null)
						ContentType = m_watchApi.getAsString("movie_type");
					sb.append(makeNewElement("movie_type",ContentType));
					if(altTag.isEmpty())
						altTag = m_watchApi.getAsString("watch_url");
					if(altTag.isEmpty())
						altTag = tag;
					sb.append(makeNewElement("watch_url",HTTP_WWW_NICOVIDEO_WATCH +altTag));
					sb.append(makeNewElement("thumb_type","video"));
					String tag_list = m_watchApi.getAsString("tagList");
					debug("\ntag_list="+tag_list);
					Mson m_tagList = m_watchApi.get("tagList");
					if(m_tagList.getSize()>0){
						sb.append("<tags domain=\"jp\">\n");
						debug("\ntags.length="+m_tagList.getSize());
						for(int i = 0; i < m_tagList.getSize(); i++){
							Mson m_hash = m_tagList.get(i);
							debug("\ntagitem="+m_hash);
							// hash = {"id":"120343647","tag":"ニコニコ技術部","cat":true,"dic":true,"lck":"1"}
							String t = m_hash.getAsString("tag");
							String cat = m_hash.getAsString("cat");
							String lck = m_hash.getAsString("lck");
							if(t!=null && !t.isEmpty()){
								debug("\ntag="+t);
								sb.append("<tag");
								if("true".equals(cat)) sb.append(" category=\"1\"");
								if("1".equals(lck)) sb.append(" lock=\"1\"");
								sb.append(">"+t+"</tag>\n");
							}
							sb.append("</tags>\n");
						}
					}
					String user_id = m_watchApi.getAsString("user_id");
					if(user_id==null || user_id.isEmpty())
						user_id = m_watchApi.getAsString("videoUserId");
					if(user_id==null)
						user_id = "";
					sb.append(makeNewElement("user_id",user_id));
					String nickname = m_watchApi.getAsString("user_nickname");
					if(nickname!=null)
						sb.append(makeNewElement("user_nickname",nickname));
					sb.append(makeNewElement("user_icon_url",m_watchApi.getAsString("user_icon_url")));
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
				log.println("thumInfo data ok.");
				thumbInfoData = s;
			}
			if(size_high==null && thumbInfoData!=null && !thumbInfoData.isEmpty()){
				size_high = getXmlElement(thumbInfoData, "size_high");
				size_low = getXmlElement(thumbInfoData, "size_low");
				log.println("size_high="+size_high+", size_low="+size_low);
			}
			if(nicoTaglist.isEmpty() && thumbInfoData!=null && !thumbInfoData.isEmpty()){
				String nico_tags = getXmlElement1(thumbInfoData, "tags");
				if(nico_tags!=null){
					nico_tags = nico_tags.trim();
					debug("\n(NicoClient)nico_tags: "+nico_tags);
					if(!nico_tags.isEmpty()){
						nicoCat = getXmlElement2(nico_tags,"tag category=\"1\" lock=\"1\"");
						nicoCat = safeTag(nicoCat);
						debug("\n(NicoClient)nicoCat: "+nicoCat);
						String[] tagarray = nico_tags.split("\n");
						if(tagarray!=null && tagarray.length>0){
							for(int i = 0; i < tagarray.length; i++){
								String tl = tagarray[i];
								String t = safeTag(getXmlElement1(tl,"tag"));
								nicoTaglist.add(i, t);
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
	private static String safeTag(String s){
		if(s==null) return "null";
		return safeFileName(s);
	}
	private String extractDataApiDataJson(String text, String encoding, String url) {
		// HTML5 watchpage
		url = safeFileName(url);
		dataApiJson = getDataApiData(text, encoding, url);
		saveApiJson(dataApiJson, encoding, url);
		if(dataApiJson==null) return null;
		extractDataJson(dataApiJson, encoding);
		return dataApiJson;
	}

	private void extractDataJson(String json, String encoding) {
		//Json解析	html5
		debug("\n■{");
		if(json==null)
			return;
		try {
			dataApiMson = null;	//not assigned yet
			try{
				dataApiMson = Mson.parse(json);
			}catch(Exception e){
				log.println("\nMson: parse error(dataApiJson)");
				return;
			}
			isDmc = "0";
			Mson m_video = dataApiMson.get("video");
			debugPrettyPrint("\n■video: ",m_video);
			{
				if(altTag.isEmpty()){
					altTag = m_video.getAsString("id");
					if(altTag.equals(videoTag))
						altTag = "";
				}
				 //2017.04.01
				if(VideoUrl==null||VideoUrl.isEmpty())
					VideoUrl = m_video.getAsString("source");
				if(VideoUrl==null||VideoUrl.isEmpty()){
					Mson m_smileInfo = m_video.get("smileInfo");
					if(!m_smileInfo.isNull())
						VideoUrl = m_smileInfo.getAsString("url");
				}
				log.println("VideoUrl: "+VideoUrl);
				if(Debug){
					if(VideoUrl.contains("dmc")){
						log.println("ERROR! VideoUrl: must NOT dmc_video, THIS IS BUG! while smile server exists!\n");
						return;
					}
				}
				ContentType = m_video.getAsString("movieType");
				log.println("ContentType: "+ContentType);
				if(VideoTitle==null || VideoTitle.isEmpty()){
					VideoTitle = m_video.getAsString("title");
					log.println("VideoTitle: "+VideoTitle);
					VideoTitle = safeFileName(VideoTitle);
				}
				log.println("VideoTitle(safe): "+VideoTitle);
				String l = m_video.getAsString("duration");
				try {
					VideoLength = (int)Integer.valueOf(l);
				} catch(NumberFormatException e){
					VideoLength = 0;
				};
				log.println("VideoLength: "+VideoLength);
				String isOfficial = m_video.getAsString("isOfficial");
				debug("■isOfficial: "+isOfficial+"\n");
				if(isOfficial!=null && isOfficial.equals("true")){
					NeedsKey = true;
					log.println("NeedsKey: "+NeedsKey);
				}
				Mson m_dmcInfo = m_video.get("dmcInfo");
				if(!m_dmcInfo.isNull()){
					debugPrettyPrint("■m_dmcInfo: ",m_dmcInfo+"\n");
					dmcInfo = m_dmcInfo.getAsString();
					debug("■dmcInfo: "+dmcInfo+"\n");
					isDmc = "1";
				}
				log.println("isDmc: "+isDmc+", serverIsDmc(): " + serverIsDmc());
				if(serverIsDmc()){
					dmcVideoLength = VideoLength;
					log.println("dmcVideoLength: "+dmcVideoLength);
					Mson m_sessionApi = m_dmcInfo.get("session_api");
					sessionApi = m_sessionApi.getAsString();
					log.println("sessionApi: "+sessionApi);
					dmcToken = m_sessionApi.getAsString("token");
					log.println("dmcToken: "+dmcToken);
					dmcTokenUnEscape =
						dmcToken.replace("\\/", "/").replace("\\\"", S_QUOTE2).replace("\\\\", S_ESCAPE);
					debug("■dmcTokenUnEscape:\n "+dmcTokenUnEscape+"\n");
					//
					setFromSessionApi(m_sessionApi);
					debug("\n");
				}
			}
			Mson m_thread = dataApiMson.get("thread");
			debugPrettyPrint("■thread: ",m_thread);
			Mson m_thread_id = m_thread.get("thread_id");
			debug("\n■thread_id: "+m_thread_id+"\n");
			if(!m_thread_id.isNull()){
				ThreadID = m_thread_id.getAsString();
				log.println("ThreadID: "+ThreadID);
				MsgUrl = m_thread.getAsString("server_url");
				log.println("MsgUrl: "+MsgUrl);
				String key_required = m_thread.getAsString("thread_key_required");
				debug("■thread_key_required: "+key_required+"\n");
				Mson m_optional_thread_id = m_thread.get("optional_thread_id");
				if(!m_optional_thread_id.isNull()){
					optionalThreadID = m_optional_thread_id.getAsString();
					if(videoTag.equals(optionalThreadID)){
						// html5の場合逆になっているようだ
						// ThreadKey が引けるのは メインthreadの方だけ
						// chanelの場合 メイン=チャンネル=threadKey optionalは何もない
						// communityの場合 メイン=コミュニティ=threadKey optionalはsm動画のコメント
						optionalThreadID = ThreadID;
						ThreadID = videoTag;
						log.println("reset ThreadID: "+ThreadID);
					}
					log.println("OptionalThreadID: "+optionalThreadID);
					NeedsKey =  true;
				}
				if(key_required.equals("true")){
					NeedsKey =  true;
				}
				log.println("NeedsKey: "+NeedsKey);
				Mson m_nicos_thread_id = m_thread.get("nicos_thread_id");
				if(!m_nicos_thread_id.isNull()){
					nicosID = m_nicos_thread_id.getAsString();
					log.println("nicosID: "+nicosID);
				}
			}else{
				Mson m_ids = m_thread.get("ids");
				ThreadID = m_ids.getAsString("default");
				log.println("ThreadID: "+ThreadID);
				Mson m_community = m_ids.get("community");
				if(!m_community.isNull()){
					optionalThreadID = m_community.getAsString();
					if(videoTag.equals(optionalThreadID)){
						// html5の場合逆になっているようだ
						// ThreadKey が引けるのは メインthreadの方だけ
						// chanelの場合 メイン=チャンネル=threadKey optionalは何もない
						// communityの場合 メイン=コミュニティ=threadKey optionalはsm動画のコメント
						optionalThreadID = ThreadID;
						ThreadID = videoTag;
						log.println("reset ThreadID: "+ThreadID);
					}
					log.println("OptionalThreadID: "+optionalThreadID);
					NeedsKey = true;
				}
				Mson m_nicos = m_ids.get("nicos");
				if(!m_nicos.isNull()){
					nicosID = m_nicos.getAsString();
					log.println("nicosID: "+nicosID);
				}
				MsgUrl = m_thread.getAsString("serverUrl");
				log.println("MsgUrl: "+MsgUrl);
			}
			log.println("NeedsKey: "+NeedsKey);
			Mson m_viewer = dataApiMson.get("viewer");
			UserID = m_viewer.getAsString("id");
			debug("■UserID: "+UserID+"\n");
			Premium = m_viewer.getAsString("isPremium");
			log.println("Premium: "+Premium);
			nicomap.put("is_premium", Premium);
			Mson m_context = dataApiMson.get("context");
			if(userKey==null || userKey.isEmpty()){
				userKey = m_context.getAsString("userkey");
				log.println("userkey(html5): "+userKey);
			}
			debug("\n");
			log.println("isMyMemory: "+m_context.getAsString("isMyMemory"));
			/*
				html5	"ownerNGFilters":[{"source":"b2","destination":"CM中か！今がチャンスだ！"},{
				flash	ng_up:	b2=CM中か！今がチャンスだ！&
						ownerFilter = nicomap.get("ng_up");
				ownerNGFileters -> ownerFilter
				source=destination& という形式に変換
				<chat filter="1">　</chat>で囲み投コメxmlに追加
			*/
			Mson m_ownerNGFilters = m_context.get("ownerNGFilters");
			String ownerNGFilters = m_ownerNGFilters.getAsString();
			debug("■ownerNGFilters: "+ownerNGFilters+"\n");
			String[] keys = new String[]{"source","destination"};
			ArrayList<String[]> list = Mson.getListString(m_ownerNGFilters, keys);
			StringBuffer sb = new StringBuffer();
			for(String[] s: list){
				if(s[0]==null || s[0].isEmpty())
					continue;
				debug("■hash: "+s[0]+","+s[1]+"\n");
				String entry = unquote(s[0])+"="+unquote(s[1])+"&";
				sb.append(entry);
				debug("■sb.append: "+entry+"\n");
			}
			ownerNGFilters = sb.substring(0).trim();
			if(!ownerNGFilters.isEmpty()){
				if(ownerNGFilters.endsWith("&"))
					ownerFilter = ownerNGFilters.substring(0,ownerNGFilters.length()-1);
				else
					ownerFilter = ownerNGFilters;
				if(ownerFilter.isEmpty())
					ownerFilter = null;
			}
			debug("■}\n");
			economy = VideoUrl.toLowerCase().contains("low");
			log.println("economy: "+economy +" ,isEco(): "+ isEco());
			if(size_video_thumbinfo==null && size_high!=null && size_low!=null){
				size_video_thumbinfo = economy? size_low : size_high;
				log.println("video size(html5): "+size_video_thumbinfo +" bytes.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getDataApiJson(){
		if(dataApiJson!=null)
			return dataApiJson;
		if(titleHtml==null)
			return null;
		String encoding = "UTF-8";
		String html = Path.readAllText(titleHtml, encoding);
		extractDataApiDataJson(html, encoding, "getDataApiJson");
		return dataApiJson;
	}
	private String getDataApiData(String text, String encoding, String comment){
		// 動画ページのJSONを取り出す
		text = getXmlElement1(text, "body");	//body
		if(text==null)
			return null;
		text = getXmlAttribute(text, "data-api-data");
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
		}
		return text;
	}

	private String extractWatchApiJson(String html, String encoding, String url){
		// flash
		url = safeFileName(url);
		watchApiJson = getWatchApiData(html, encoding, url);
		saveApiJson(watchApiJson, encoding, url);
		if(watchApiJson==null) return null;
		extractJson(watchApiJson, encoding);
		return watchApiJson;
	}
	private String getWatchApiJson(){
		if(watchApiJson!=null)
			return watchApiJson;
		String encoding = "UTF-8";
		if(titleHtml==null)
			return null;
		String html = Path.readAllText(titleHtml, encoding);
		extractWatchApiJson(html, encoding, "getWatchApiJson");
		return watchApiJson;
	}
	private void extractJson(String text, String encoding) {
		//Json解析	flash
		if(text==null)
			return;
		try {
			watchApiMson = Mson.parse(text);
		//	debugPrettyPrint("watchApi:\n ",watchApiMson);
			// flvInfo
			if(!nicomap.containsKey("flvInfo")){
				if(nicomap.containsKey("url")){
					debug("Ooops. this is duplicated url "+nicomap.get("url"));
				}
				flvInfo = watchApiMson.getAsString("flvInfo");
				debug("\n■flvInfo:\n "+flvInfo);
				nicomap.put("flvInfo", flvInfo);
				flvInfoArrays = URLDecoder.decode(flvInfo, encoding);
				nicomap.putArrayURLDecode(flvInfoArrays, encoding);
				VideoUrl = nicomap.get("url");
				if(VideoUrl==null || VideoUrl.isEmpty()){
					debug("Ooops. url not found.");
					return;
				}
			}
			// dmcInfo
			isDmc = watchApiMson.getAsString("isDmc");
			log.println("isDmc: "+isDmc+", serverIsDmc(): " + serverIsDmc());
			if(serverIsDmc()){
				dmcInfo = watchApiMson.getAsString("dmcInfo");
				dmcInfoDec = URLDecoder.decode(unquote(dmcInfo), encoding);
				Mson m_dmcInfo = Mson.parse(dmcInfoDec);
				debugPrettyPrint("\n■m_dmcInfo:\n ",m_dmcInfo);
				if(dmcInfoDec!=null){
					String l = m_dmcInfo.getAsString("length_seconds");
					try {
						dmcVideoLength = Integer.decode(l);
					} catch(NumberFormatException e){
						dmcVideoLength = 0;
					};
					debug("\n■dmcVideoLength: "+dmcVideoLength);
					dmcToken = m_dmcInfo.getAsString("token");
					dmcTokenUnEscape = dmcToken.replace("\\/", "/").replace("\\\"", S_QUOTE2).replace("\\\\", S_ESCAPE);
					debug("\n■dmcTokenUnEscape:\n "+dmcTokenUnEscape);

					Mson m_sessionApi = m_dmcInfo.get("session_api");
					sessionApi = m_sessionApi.getAsString();
					debug("\n■session_api:\n "+sessionApi);
					setFromSessionApi(m_sessionApi);
					debug("\n");
				}
			}
		} catch (UnsupportedEncodingException e1) {
			log.printStackTrace(e1);
		}finally{

		}
		return;
	}

	private void setFromSessionApi(Mson m_sessionApi){
		// flash html5 common
		recipe_id = m_sessionApi.getAsString("recipe_id");
		debug("\n■recipe_id: "+recipe_id);
		Mson m_videos = m_sessionApi.get("videos");
		debug("\n■videos: "+m_videos.getAsString());
		videolist = new ArrayList<String>();
		for(int i = 0; i < m_videos.getSize(); i++){
			videolist.add(m_videos.getAsString(i));
		}
		debug("\n■videolist: "+videolist);
		Mson m_audios = m_sessionApi.get("audios");
		debug("\n■audios: "+m_audios.getAsString());
		audiolist = new ArrayList<String>();
		for(int i = 0; i < m_audios.getSize(); i++){
			audiolist.add(m_audios.getAsString(i));
		}
		debug("\n■audiolist: "+audiolist);
		apiUrls = m_sessionApi.getAsString("api_urls");
		debug("\n■apiUrls: "+apiUrls);
		apiSessionUrl = m_sessionApi.get("api_urls").getAsString(0);
		debug("\n■apiSessionUrl: "+apiSessionUrl);
		player_id = m_sessionApi.getAsString("player_id");
		debug("\n■player_id: "+player_id);
		service_user_id = m_sessionApi.getAsString("service_user_id");
		debug("\n■service_user_id: "+service_user_id);
		priority = m_sessionApi.getAsString("priority");
		debug("\n■priority: "+priority);
		signature = m_sessionApi.getAsString("signature");
		debug("\n■signature: "+signature);
	}
	public static String unquote(String str) {
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
		sb.append("  "+makeNewElement("priority",priority));
		sb.append("  <content_src_id_sets>\n");
		sb.append("    <content_src_id_set>\n");
		sb.append("      <content_src_ids>\n");
		sb.append("        <src_id_to_mux>\n");
		sb.append("          <video_src_ids>\n");
		for(String v:videolist){
			sb.append("            <string>"+v+"</string>\n");
		}
		sb.append("          </video_src_ids>\n");
		sb.append("          <audio_src_ids>\n");
		for(String a:audiolist){
			sb.append("            <string>"+a+"</string>\n");
		}
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
		sb.append("      "+makeNewElement("signature", signature));
		sb.append("    </session_operation_auth_by_signature>\n");
		sb.append("  </session_operation_auth>\n");
		sb.append("  <content_auth>\n");
		sb.append("    <auth_type>ht2</auth_type>\n");
		sb.append("    <service_id>nicovideo</service_id>\n");
		sb.append("    "+makeNewElement("service_user_id",service_user_id));
		sb.append("    <max_content_count>10</max_content_count>\n");
		sb.append("    <content_key_timeout>600000</content_key_timeout>\n");
		sb.append("  </content_auth>\n");
		sb.append("  <client_info>\n");
		sb.append("    "+makeNewElement("player_id",player_id));
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

	private String getWatchApiData(String text, String encoding, String comment) {
		// 動画ページのJSONを取り出す flash
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
		text = unquote(text);
		return text;
	}
	void saveApiJson(String json, String encoding, String comment){
		Path file = Path.mkTemp(videoTag+"_watchJson.txt");
		log.println("file: "+file.getPath()+" is <"+comment+"> Json");
		if(json==null){
			log.println("error json is null!");
		}else{
			PrintStream ps ;
			try {
				ps = new PrintStream(file);
				Mson.parse(json).prettyPrint(ps);
				ps.flush();
				ps.close();
				log.println("Saved prettyPrinted ApiData to "+file.getPath());
			} catch (FileNotFoundException e) {
				log.printStackTrace(e);
				log.println("json parse error!");
				Path.writeAllText(file, json, encoding);
				log.println("Saved ApiData to "+file.getPath());
			}
		}
	}

	private String makeNewElement(String key, String val){
		if(val==null)
			val = "";
		return "<"+key+">"+val+"</"+key+">\n";
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

	private String getXmlAttribute(String input, String atribname){
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
				//Stopwatch.show();
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
				//Stopwatch.show();
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
				//Stopwatch.show();
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
