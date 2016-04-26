package saccubus.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.JLabel;

import saccubus.ConvertStopFlag;
import saccubus.ConvertWorker;
import saccubus.WayBackDate;
import saccubus.conv.ChatSave;
import saccubus.net.BrowserInfo.BrowserCookieKind;
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
	private final String User;
	private final String Pass;
	private boolean Logged_in = false;
	private final Proxy ConProxy;
	boolean Debug = false;
	private final NicoMap nicomap;
	private Stopwatch Stopwatch;
	private Path titleHtml = null;

	public static final String DEBUG_PROXY = "debug";	// debug paramerter end with '/'

	/**
	 * ブラウザ共有しないでログイン
	 * @param user
	 * @param pass
	 * @param proxy
	 * @param proxy_port
	 */
	public NicoClient(final String user, final String pass,
			final String proxy, final int proxy_port, final Stopwatch stopwatch) {
		User = user;
		Pass = pass;
		Stopwatch = stopwatch;
		nicomap = new NicoMap();
		ConProxy = conProxy(proxy, proxy_port);
		// ログイン
		Logged_in = login() && loginCheck();
	}

	private Proxy conProxy(String proxy, final int proxy_port){
		Proxy tmpProxy;
		if (proxy != null && proxy.startsWith(DEBUG_PROXY)){
			System.out.println("Print debug information.");
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
				ex.printStackTrace();
				System.out.println("Unable to make Proxy. maybe bug.");
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
	 */
	public NicoClient(final BrowserCookieKind browser_kind, final String user_session,
			final String proxy, final int proxy_port, final Stopwatch stopwatch) {
		User = "";
		Pass = "";
		Stopwatch = stopwatch;
		nicomap = new NicoMap();
		ConProxy = conProxy(proxy, proxy_port);
		if (user_session == null || user_session.isEmpty()){
			System.out.println("Invalid user session" + browser_kind.toString());
			setExtraError("セッションを取得出来ません");
			Logged_in = false;
		} else {
			String[] sessions = user_session.split(" ");	// "user_session_12345..."+" "+...
			for(String session: sessions){
				if (session != null && !session.isEmpty()){
					String this_session = "user_session=" + session;
					Cookie = new NicoCookie();
					Cookie.setSession(this_session);
					if(loginCheck()){
						Logged_in = true;	// ログイン済みのハズ
						return;
					}
					Cookie = new NicoCookie();
					System.out.println("Fault user session" + browser_kind.toString());
					setExtraError("セッションが無効です");
				}
			}
			Logged_in = false;
		}
	}

	void debug(String messege){
		if (Debug){
			System.out.print(messege);
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
		return urlConnect(url,method,cookieProp,doInput,doOutput,connectionProp,false);
	}

	private HttpURLConnection urlConnect(String url, String method, NicoCookie cookieProp,
			boolean doInput, boolean doOutput, String connectionProp, boolean followRedirect){

		try {
			debug("\n■URL<" + url + ">\n");
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
	// the following commented source code lines can be made valid in the future, and it would be ok (already tested).
			con.setRequestMethod(method);
			if (cookieProp != null)
				con.addRequestProperty("Cookie", cookieProp.get(url));
 		//	con.setRequestProperty("Host", "nmsg.nicovideo.jp");
		//	con.setRequestProperty("User-Agent", "Java/Saccubus-1.xx");
		//	con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		//	con.setRequestProperty("Accept-Language", "ja,en-US;q=0.7,en;q=0.3");
		//	con.setRequestProperty("Accept-Encoding", "deflate");
		//	/* gzip deflateを受付可能にしたらコメント取得が早くなる？ 実際にdeflateで来るかは確かめてない */
		//	con.setRequestProperty("DNT", "1");
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

			debug("■Connect: " + method + ","
				+ (cookieProp == null ? "" : "Cookie<" + cookieProp.get(url) +">,")
				+ (doInput ? "DoInput," : "")
				+ (doOutput ? "DoOutput," : "")
				+ (followRedirect ? "FollowRedirects," : "")
				+ (connectionProp == null ?
					"" : "Connection " + connectionProp)
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
				System.out.println("Error Response:" + Integer.toString(code) + " " + con.getResponseMessage());
				setExtraError("" + code);
				return null;
			}
		} catch(IOException ex){
			ex.printStackTrace();
			System.out.println("Connection error. Check proxy ?");
			setExtraError("コネクションエラー。プロキシが不正？");
		} catch(IllegalStateException ex){
			ex.printStackTrace();
			System.out.println("Connection error. Check proxy ?");
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
			ex.printStackTrace();
		}
		return "";
	}

	private NicoCookie detectCookie(HttpURLConnection con){
		nicomap.putConnection(con, (Debug? System.out:null));
		NicoCookie cookie = new NicoCookie();
		nicomap.setCookie(cookie);
	//	debug("■<NicoCookie><" + cookie.toString() + ">\n");
		return cookie;
	}

	private boolean login() {
		try {
			System.out.print("Trying login...");
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
				System.out.println("Can't login:" + mes);
				return false;
			}
			Cookie = detectCookie(con);
			con.disconnect();
			if (Cookie == null || Cookie.isEmpty()) {
				System.out.println("Can't login: cannot set cookie.");
				return false;
			}
		//	System.out.println("Logged in.");
		} catch (IOException ex) {
			ex.printStackTrace();
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
		str = toSafeString(str, "MS932");
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
				// e.printStackTrace();
			}
			if (len == 1 && b[0] == '?'){	// illegal char -> '?', but it's not safe, -> '-'
				b[0] = '-';
				s = "-";	//this is unicode
			}
			/*
			if ("MS932".equals(encoding) && len == 2 &&
					(b[1] == 0x5C || b[1] == 0x7C)){
				System.out.println("Checked Danger Byte Code<" + b[1] + ">, better to fix?");
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
	private static final String TITLE_ZERO_DIV = "id=\"videoHeaderDetail\"";
	private static final String TITLE_ZERO_DUMMY = "<title>ニコニコ動画:Zero</title>";
	private static final String TITLE_GINZA_DIV = "DataContainer\"";
	private static final String TITLE_GINZA_DUMMY = "<title>ニコニコ動画:GINZA</title>";
	public boolean getVideoHistoryAndTitle(String tag, String watchInfo, boolean saveWatchPage) {
		if(getThumbInfoFile(tag) != null && !saveWatchPage){
			//return true;
		}
		return getVideoHistoryAndTitle1(tag, watchInfo, saveWatchPage);
	}
	public boolean getVideoHistoryAndTitle1(String tag, String watchInfo, boolean saveWatchPage) {
		String thumbTitle = getVideoTitle();
		VideoTitle = null;
		boolean found = false;
		String url = "http://www.nicovideo.jp/watch/" + tag + watchInfo;
		System.out.print("Getting video history...");
		boolean zero_title = false;
		try {
			HttpURLConnection con = urlConnectGET(url);
			if (con == null || con.getResponseCode() != HttpURLConnection.HTTP_OK){
				System.out.println("ng.\nCan't getVideoHistory:" + url);
				return false;
			}
			Cookie.update(detectCookie(con));
			String encoding = con.getContentEncoding();
			if (encoding == null){
				encoding = "UTF-8";
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(con
					.getInputStream(), encoding));
			System.out.print("ok.\nChecking VideoTitle...");
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
					System.out.print("<" + VideoTitle + ">...");
					continue;
				}
				if (ret.contains(TITLE_PARSE_STR_START)) {
					ret = getXmlElement(ret, "title");
					index = 0;
					int index2 = ret.lastIndexOf(TITLE_END);
					if (index2 < 0){
						continue;
					}
					found = true;
					if(getVideoTitle()==null){
						VideoTitle = safeFileName(ret.substring(index,index2));
					}
					System.out.print("<" + VideoTitle + ">...");
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
			if(altTag.isEmpty())
				altTag = getAltTag(sb.substring(0));

			PrintWriter pw;
			if(!found || saveWatchPage){
				titleHtml = Path.mkTemp(tag + "watch.htm");
				pw = new PrintWriter(titleHtml, encoding);
				pw.write(sb.toString());
				pw.flush();
				pw.close();
				if(!found)
					System.out.print(" Title not found.");
				System.out.println(" <" + Path.toUnixPath(titleHtml) + "> saved.");
			}
			System.out.println("ok.");
		} catch (IOException ex) {
			ex.printStackTrace();
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
		if (!getVideoHistoryAndTitle(tag, watchInfo, saveWatchPage)) {
			return false;
		}
		try {
			String url = "http://flapi.nicovideo.jp/api/getflv/" + tag;
			if (tag.startsWith("nm")) {
				url += "?as3=1";
			}
			if (url.contains("?") && !watchInfo.isEmpty()){
				watchInfo = "&" + watchInfo.substring(1);
			}
			System.out.print("Getting video informations...");
			HttpURLConnection con = urlConnectGET(url + watchInfo);
			if (con == null || con.getResponseCode() != HttpURLConnection.HTTP_OK){
				System.out.println("ng.\nCan't getVideoInfo:" + url + watchInfo);
				if(con == null || !loginCheck(con)){
					System.out.println("Can't login.");
					return false;
				}
			}
			String encoding = con.getContentEncoding();
			if (encoding == null){
				encoding = "UTF-8";
			}
			String ret = readConnection(con);
			if (ret == null || ret.isEmpty()){
				System.out.println("ng.\nCan't getVideoInfo: null respense.");
				return false;
			}
			nicomap.putArrayURLDecode(ret, encoding);
			if (Debug){
				nicomap.printAll(System.out);
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
				System.out.println("ng.\nCan't get video information keys.");
				con = urlConnectGET(url + watchInfo);
				if(!loginCheck(con)){
					System.out.println("Can't logged In.");
				}
				return false;
			}
			economy  = VideoUrl.toLowerCase().contains("low");
			System.out.println("ok.");
			System.out.println("Video:<" + VideoUrl + ">; Comment:<" + MsgUrl
					+ (NeedsKey ? ">; needs_key=1" : ">"));
			System.out.println("Video time length: " + VideoLength + "sec");
			System.out.println("ThreadID:<" + ThreadID + "> Maybe uploaded on "
					+ WayBackDate.format(ThreadID));
			if (OptionalThraedID!=null && !OptionalThraedID.isEmpty()){
				System.out.println("OptionalThreadID:<" + OptionalThraedID + ">");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	private byte[] buf = new byte[1024 * 1024];

	private String VideoUrl = null;
	private String ContentType = null;
	private String ContentDisp;

	public File getVideo(File file, final JLabel status, final ConvertStopFlag flag,
			boolean renameMp4) {
		try {
			System.out.print("Getting video size...");
			if (VideoUrl == null) {
				System.out.println("Video url is not detected.");
				return null;
			}
			if (file.canRead() && file.delete()) { // ファイルがすでに存在するなら削除する。
				System.out.print("previous video deleted...");
			}
			HttpURLConnection con = urlConnect(VideoUrl, "GET", Cookie, true, false, null);
			if (con == null || con.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.println("Can't get video:" + VideoUrl);
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
						// e.printStackTrace();
					}
				}
				return null;
			}
			InputStream is = con.getInputStream();
			new NicoMap().putConnection(con, (Debug? System.out:null));

			if(ContentType==null){
				ContentType = con.getHeaderField("Content-Type");
				if(ContentType == null) ContentType = "";
			}
			ContentDisp = con.getHeaderField("Content-Disposition");
			int max_size = con.getContentLength();	// -1 when invalid
			System.out.print("size="+(max_size/1000)+"Kbytes");
			System.out.println(", type=" + ContentType + ", " + ContentDisp);
			System.out.print("Downloading video...");
			if(renameMp4 && ContentType.contains("mp4")){
				String filepath = file.getPath();
				int index = filepath.lastIndexOf(".");
				if(filepath.lastIndexOf(File.separator) < index){
					filepath = filepath.substring(0, index) + ".mp4";
				}
				file = new File(filepath);
			}
			OutputStream os = new FileOutputStream(file);
			int size = 0;
			int read = 0;
			debugsInit();
			while ((read = is.read(buf, 0, buf.length)) > 0) {
				debugsAdd(read);
				size += read;
				os.write(buf, 0, read);
				sendStatus(status, "動画", max_size, size);
				Stopwatch.show();
				if (flag.needStop()) {
					System.out.println("\nStopped.");
					is.close();
					os.flush();
					os.close();
					con.disconnect();
					if (file.delete()){
						System.out.println("video deleted.");
					}
					return null;
				}
			}
			debugsOut("\n■read+write statistics(bytes) ");
			System.out.println("ok.");
			is.close();
			os.flush();
			os.close();
			con.disconnect();
			return file;
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally{
		//	debug("■read+write statistics(bytes) ");
		//	debugsOut();
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
				System.out.println("It may be wrong Date.");
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
	 	NeedsKey = false;
	 	Official = "";
		// この後でOwnerCommentを取得するとユーザー動画の投稿者コメントが取得される。
		if (time != null && !time.isEmpty()){
		 	WayBackKey = "0";
			if (!getWayBackKey(time)) { // WayBackKey
				System.out.println("It may be wrong Date.");
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
		StringBuffer sb = new StringBuffer();
		sb.append("<packet>");
		sb.append("<thread thread=\"" + ThreadID);
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
		sb.append("<thread_leaves thread=\"" + ThreadID);
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
		System.out.print("Downloading " + commentType.toString().toLowerCase()
				+" comment, size:" + back_comment + "...");
		//String official = "";	/* 公式動画用のkey追加 */
		if(NeedsKey && Official.isEmpty()){
			if((force184 == null || threadKey == null)
					&& !getOfficialOption(ThreadID)) {
				return null;
			}
			Official ="\" force_184=\"" + force184
					+ "\" threadkey=\"" + threadKey;
		}
		FileOutputStream fos = null;
		try {
			String lastNo = "";
			if (file.canRead()){
				if(isAppend && useNewComment){
					if(commentType != CommentType.OWNER)
						lastNo = ConvertWorker.getNoUserLastChat(file);
				}else{
					if (file.delete()) {	//	ファイルがすでに存在するなら削除する。
						System.out.print("previous " + commentType.toString().toLowerCase() + " comment deleted...");
					}
				}
			}
			fos = new FileOutputStream(file, isAppend);
			HttpURLConnection con = urlConnect(MsgUrl, "POST", Cookie, true, true, "keep-alive",true);
			OutputStream os = con.getOutputStream();
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
					System.out.print("New comment mode...");
				else
					System.out.print("Append new comment mode...");
			} else {
				req = commentCommand2006(commentType, back_comment);
				System.out.print("Old comment mode...");
			}
			debug("\n■write:" + req + "\n");
			os.write(req.getBytes());
			os.flush();
			os.close();
			debug("■Response:" + Integer.toString(con.getResponseCode()) + " " + con.getResponseMessage() + "\n");
			if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.println("ng.\nCan't download " + commentType.toString().toLowerCase() + " comment:" + MsgUrl);
				return null;
			}
			InputStream is = con.getInputStream();
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
				sendStatus(status, commentType.dlmsg(), max_size, size);
				Stopwatch.show();
				if (flag.needStop()) {
					System.out.println("\nStopped.");
					is.close();
					os.flush();
					os.close();
					con.disconnect();
					fos.close();
					if (file.delete()){
						System.out.println(commentType.toString().toLowerCase() + " comment deleted.");
					}
					return null;
				}
			}
			debugsOut("■read+write statistics(bytes) ");
			System.out.println("ok.");
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
			ex.printStackTrace();
			if(ex.toString().contains("Unexpected")){	//"Unexpected end of file from server"
				setExtraError("サーバーから切断されました。タイムアウト？");
			}
		} catch(NumberFormatException ex){
			ex.printStackTrace();
		}
		finally{
			if (fos != null){
				try { fos.close(); } catch (IOException e) {}
			}
		}

		return null;
	}

	private String threadKey = null;
	private String force184 = null;

	private boolean getOfficialOption(String threadId) {
		String url = "http://flapi.nicovideo.jp/api/getthreadkey?thread="
			+threadId;
		System.out.print("\nGetting Official options (threadkey)...");
		try {
			if (force184 != null && threadKey != null){
				System.out.println("ok. But this call twice, not necessary.");
				return true;
			}
			HttpURLConnection con = urlConnectGET(url);
			if (con == null || con.getResponseCode() != HttpURLConnection.HTTP_OK){
					System.out.println("ng.\nCan't get Oficial option:" + url);
					return false;
			}
			String ret = readConnection(con);
			if (ret == null || ret.isEmpty()){
				System.out.println("ng.\nNull response.");
				return false;
			}
			nicomap.splitAndPut(ret, "&");
			threadKey = nicomap.get("threadkey");
			force184 = nicomap.get("force_184");
			if (threadKey == null || force184 == null) {
				System.out.println("ng.\nCan't get Oficial option.");
				System.out.println("ret: " + ret);
				return false;
			}
			System.out.println("ok.  Thread Key: " + threadKey);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
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
		System.out.print("Setting wayback time...");
		try {
			if(!"0".equals(WayBackKey)){
				System.out.println("ok. But this call twice, not necessary.");
				hasNewCommentBegun = true;
				return true;
			}
			WayBackDate wayback = new WayBackDate(time);
			if (!wayback.isValid()){
				System.out.println("ng.\nCannot parse time.\"" + time + "\"");
				setExtraError("過去ログ指定文字列が違います");
				return false;
			}
			String waybacktime = wayback.getWayBackTime();
			System.out.println("ok. [" + wayback.format() + "]: " + waybacktime);
			System.out.print("Getting wayback key...");
			String url = "http://flapi.nicovideo.jp/api/getwaybackkey?thread="
					+ ThreadID;
			HttpURLConnection con = urlConnectGET(url);
			if (con == null || con.getResponseCode() != HttpURLConnection.HTTP_OK){
				System.out.println("ng.\nCan't open connection: " + url);
				return false;
			}
			String ret = readConnection(con);
			if (ret == null) {
				System.out.println("ng.\nCannot find waybackkey from response.");
				return false;
			}
			nicomap.splitAndPut(ret, "&");
			String waybackkey = nicomap.get("waybackkey");
			if (waybackkey == null || waybackkey.isEmpty()) {
				System.out.println("ng.\nCannot get wayback key. it's invalid");
				if ("0".equals(Premium)){
					setExtraError("一般会員は過去ログ不可です");
				}
				return false;
			}
			System.out.println("ok.  Wayback key: " + waybackkey);
			WayBackTime = waybacktime;
			WayBackKey = waybackkey;
			hasNewCommentBegun = wayback.getSecond() > NEW_COMMENT_BEGIN_SECOND;
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean loginCheck() {
		String url = "http://www.nicovideo.jp/";
		System.out.print("Checking login...");
		// GET (NO_POST), UTF-8, AllowAutoRedirect,
			HttpURLConnection con = urlConnectGET(url);
			// response 200, 302 is OK
			if (con == null){
				System.out.println("ng.\nCan't read TopPage at loginCheck:" + url);
				return false;
			}
		return loginCheck(con);
	}

	private boolean loginCheck(HttpURLConnection con) {
		NicoCookie new_cookie = detectCookie(con);
		if (new_cookie == null || new_cookie.isEmpty()) {
			System.out.print(" new_cookie isEmpty. ");
			// but continue
		}
		String auth = nicomap.get("x-niconico-authflag");
		if(auth==null || auth.isEmpty() || auth.equals("0")){
			System.out.println("ng. Not logged in. authflag=" + auth);
			con.disconnect();
			return false;
		}
		Cookie.update(new_cookie);

		debug("\n■Now Cookie is<" + Cookie.toString() + ">\n");
		System.out.println("ok.");
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
	private void debugsInit(){
		if(!Debug) return;
		dsCount = dsMax = dsSum = 0;
		dsMin = Integer.MAX_VALUE;
	}
	private void debugsAdd(int data){
		if(!Debug) return;
		dsCount++;
		dsSum += data;
		dsMax = Math.max(dsMax, data);
		dsMin = Math.min(dsMin, data);
	}
	private void debugsOut(String header){
		if(!Debug) return;
		System.out.print(header);
		if(dsCount==0){
			System.out.println("Count 0");
		} else {
			System.out.print("Count "+dsCount+", Min "+dsMin+", Max "+dsMax);
			System.out.println(", Sum "+dsSum+", Avg "+dsSum/dsCount);
		}
	}

	/*
	 * msg = "動画" または "コメント" または "投稿者コメント"
	 */
	private void sendStatus(JLabel status, String msg,
			int max_size, int size){
		if (max_size > 0) {
			String per = Double.toString((((double) size) * 100)
					/ max_size);
			per = per.substring(0, Math.min(per.indexOf(".") + 3, per
					.length()));
			synchronized (status) {
				status.setText(msg + "ダウンロード：" + per + "パーセント完了");
			}
		} else {
			synchronized (status) {
				status.setText(msg + "ダウンロード中：" + Integer.toString(size >> 10)
						+ "kbytesダウンロード");
			}
		}
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

/*
	private String getKeyValue(String src, String keyword, char delimc){
		String dest = null;
		char escapec = '\\';
		int index = src.indexOf(keyword);
		index += keyword.length();
		int endIx = src.indexOf(delimc, index);
		if(index < 0 || endIx < 0)
			return null;
		dest = src.substring(index).trim();	//trim
		return getUniValue(dest, delimc, escapec);
	}
	private String getUniValue(String src, char delimc, char escapec){
		StringBuilder sb = new StringBuilder();
		char stop = 0;
		int p = 1;
		for(int i = 0; i < src.length(); i+=p){
			char c = src.charAt(i);
			if(stop == 0){
				if(c == '\''){
					sb.append(c);
					stop = c;
					p = 1;
					continue;
				}
				stop = delimc;
			}
			if( c == stop){
				sb.append(c);
				break;
			}
			if(c == escapec){
				c = src.charAt(i+1);
				if ( c == 'u'){
					int v = -1;
					try {
						v = Integer.parseInt(src.substring(i+2, i+6),16);
					} catch(NumberFormatException e){
						e.printStackTrace();
					}
					sb.append((char)v);
					p = 6;
					continue;
				}
				// c was escaped, just append
				sb.append(c);
				p = 2;
				continue;
			}
			sb.append(c);
			p = 1;
		}
		return sb.toString();
	}
	private String getHtmlElement(String src, String keyword){
		String dest;
		int index = src.indexOf(keyword);
		int endIx = src.indexOf("</", index);
		if(index < 0 || endIx < 0)
			return null;
		index += keyword.length();
		dest = src.substring(index, endIx).replace("\\", "");
		return dest;
	}
	public Path getWatchPage() {
		Path filePath = null;
		if(thumbxml  == null)
			return null;
		try {
			String text = Path.readAllText(thumbxml.getPath(),"UTF-8");
			int index;
			String threadId = getKeyValue(text, "v:", ',');
			String videoId = getKeyValue(text, "id:\t", ',');
			String titleStr = getKeyValue(text, "title:", ',');
			String uploadComment = getKeyValue(text, "description:", ',');
			String thumbnail  = getKeyValue(text, "thumbnail:", ',');
			String postedAt  = getKeyValue(text, "postedAt:", ',');
			String timeLength  = getKeyValue(text, "length:", ',');
			String viewCount = getKeyValue(text, "viewCount:", ',');
			String commentCount = getKeyValue(text, "commentCount:", ',');
			String mylistCount = getKeyValue(text, "mylistCount:", ',');
			String ownerName = getHtmlElement(text,"<p class=\"font12\"><a href=\"user/");
			ownerName = ("user/" + ownerName).replace("\"><strong>", "' '");
			String tags = getKeyValue(text, "tags:", ']');
			//tags = convertUniList(tags);
			String fileName = thumbxml.getPath();
			index = fileName.lastIndexOf(".");
			if (index >= thumbxml.getPath().lastIndexOf(File.separator)) {
				fileName = fileName.substring(0, index);
			}
			fileName += ".txt";
			filePath = new Path(fileName);
			PrintWriter pw = new PrintWriter(filePath, "UTF-8");
			pw.printf("Encode: UTF-8\n");
			pw.printf("v:     %s\n", threadId);
			pw.printf("ID:     %s\n", videoId);
			pw.printf("タイトル:  %s\n", titleStr);
			pw.printf("動画説明：   %s\n", uploadComment);
			pw.printf("サムネ:    %s\n", thumbnail);
			pw.printf("投稿日:   %s\n", postedAt);
			pw.printf("時間(秒):  %s\n", timeLength);
			pw.printf("再生数：    %s\n", viewCount);
			pw.printf("コメント数：  %s\n", commentCount);
			pw.printf("マイリスト数： %s\n", mylistCount);
			pw.printf("投稿者:    '%s'\n", ownerName);
			pw.printf("タグ:     %s\n", tags);
			pw.flush();
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return filePath;
	}
*/

	public Path getThumbInfoFile(String tag){
		final String THUMBINFO_URL = "http://ext.nicovideo.jp/api/getthumbinfo/";
		String url = THUMBINFO_URL + tag;
		System.out.print("Getting thumb Info...");
		Path thumbXml = null;
		try {
			HttpURLConnection con = urlConnectGET(url);
			if (con == null || con.getResponseCode() != HttpURLConnection.HTTP_OK){
				System.out.println("ng.\nCan't getThumbInfo:" + url);
				return null;
			}
		//	Cookie.update(detectCookie(con));

			String encoding = con.getContentEncoding();
			if (encoding == null){
				encoding = "UTF-8";
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(con
					.getInputStream(), encoding));
			System.out.print("ok.\nSaving thumb Info...");
			String ret;
			StringBuilder sb = new StringBuilder();
			while ((ret = br.readLine()) != null) {
				Stopwatch.show();
				sb.append(ret + "\n");
			}
			br.close();
			con.disconnect();
			String s = sb.toString();
			String title = getVideoTitle();
			if(title==null){
				if(s!=null && s.contains("title")){
					title = safeFileName(getXmlElement(s, "title"));
				}
				if(title==null){
					if(getVideoHistoryAndTitle1(tag, "", false))
						title = getVideoTitle();
					else
						title = VideoTitle;
				}
			}
			if(ContentType==null){
				ContentType = getXmlElement(s, "movie_type");
			}
			PrintWriter pw;
			thumbXml  = Path.mkTemp(tag + "_" + title + ".xml");
			;
			pw = new PrintWriter(thumbXml, encoding);
			pw.write(sb.toString());
			pw.flush();
			pw.close();
			if(thumbXml==null || sb.indexOf("status=\"ok\"") < 0){
				System.out.println("ng.\nSee file:" + thumbXml);
				return null;
			}
			System.out.println("ok.");
			return thumbXml;
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static String getXmlElement(String xml, String tag){
		String dest;
		int index = xml.indexOf("<"+tag+">");
		int endIx = xml.indexOf("</", index+2);
		if(index < 0 || endIx < 0)
			return null;
		index += tag.length() + 2;
		dest = xml.substring(index, endIx);
		return dest;
	}

	public static String getXmlElement2(String xml, String tag){
		String dest;
		int index = xml.indexOf("<"+tag);
		if(index < 0)
			return null;
		index += tag.length() + 1;
		index = xml.indexOf(">", index);
		if(index < 0)
			return null;
		int endIx = xml.indexOf("</", index+1);
		if(endIx < 0)
			endIx = xml.length();
		dest = xml.substring(index+1, endIx);
		return dest;
	}

	public Path getThumbUserFile(String userID, File userFolder){
		final String THUMBUSER_URL = "http://ext.nicovideo.jp/thumb_user/";
		String url = THUMBUSER_URL + userID;
		System.out.print("Getting thumb User...");
		Path userHtml = null;
		try {
			HttpURLConnection con = urlConnectGET(url);
			if (con == null || con.getResponseCode() != HttpURLConnection.HTTP_OK){
				System.out.println("ng.\nCan't getThumbUser:" + url);
				return null;
			}
		//	Cookie.update(detectCookie(con));

			String encoding = con.getContentEncoding();
			if (encoding == null){
				encoding = "UTF-8";
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(con
					.getInputStream(), encoding));
			System.out.print("ok.\nSaving thumb user...");
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
			System.out.println("ok.");
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
		return userHtml;
	}

	public Path getUserInfoFile(String userID, File userFolder) {
		final String USER_URL = "http://www.nicovideo.jp/user/";
		String url = USER_URL + userID;
		System.out.print("Getting User Info...");
		Path userHtml = null;
		try {
			HttpURLConnection con = urlConnectGET(url);
			if (con == null || con.getResponseCode() != HttpURLConnection.HTTP_OK){
				System.out.println("ng.\nCan't getUserInfo:" + url);
				return null;
			}
		//	Cookie.update(detectCookie(con));

			String encoding = con.getContentEncoding();
			if (encoding == null){
				encoding = "UTF-8";
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(con
					.getInputStream(), encoding));
			System.out.print("ok.\nSaving user info...");
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
			System.out.println("ok.");
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
		return userHtml;
	}

	public boolean getThumbnailJpg(String url, File thumbnalJpgFile) {
		System.out.print("Getting thumbnail...");
		try {
			HttpURLConnection con = urlConnectGET(url);
			if (con == null || con.getResponseCode() != HttpURLConnection.HTTP_OK){
				System.out.println("ng.\nCan't getThumbnailJpg:" + url);
				return false;
			}
		//	Cookie.update(detectCookie(con));

			InputStream is = con.getInputStream();
			FileOutputStream fos = new FileOutputStream(thumbnalJpgFile);
			byte[] buf = new byte[4096];
			System.out.print("ok.\nSaving thumbnail...");
			int len = 0;
			while ((len = is.read(buf, 0, buf.length)) > 0) {
				fos.write(buf, 0, len);
				Stopwatch.show();
			}
			System.out.println("ok.");
			is.close();
			fos.flush();
			fos.close();
			con.disconnect();
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}
}
