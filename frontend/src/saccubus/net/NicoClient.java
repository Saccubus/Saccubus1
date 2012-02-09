package saccubus.net;

import java.io.*;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URLEncoder;
import javax.swing.JLabel;

import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;

import com.sun.xml.internal.ws.util.ByteArrayBuffer;

import saccubus.ConvertStopFlag;

import saccubus.WayBackDate;
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

	public static final String DEBUG_PROXY = "debug/";

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
		Logged_in = login();
	}

	private Proxy conProxy(String proxy, final int proxy_port){
		Proxy tmpProxy;
		if (proxy != null && proxy.startsWith(DEBUG_PROXY)){
			System.out.println("Print debug information.");
			Debug = true;
			proxy = proxy.substring(DEBUG_PROXY.length());
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
		Cookie = "user_session=" + user_session;	// "user_session_12345..."
		if (user_session == null || user_session.isEmpty()){
			System.out.println("Invalid user session" + browser_kind.toString());
			setExtraError("セッションを取得出来ません");
			Logged_in = false;
		} else {
			Logged_in = loginCheck();					// ログイン済みのハズ
		}
	}

	void debug(String messege){
		if (Debug){
			System.out.print(messege);
		}
	}

	private String Cookie = null;

	HttpURLConnection urlConnectGET(String url){
		return urlConnect(url, "GET");
	}

	private HttpURLConnection urlConnect(String url, String method){
		return urlConnect(url, method, Cookie, true, false, "close");
	}

	private HttpURLConnection urlConnect(String url, String method, String cookieProp,
			boolean doInput, boolean doOutput, String connectionProp){
		try {
			debug("\n■URL<" + url + ">\n");
			HttpURLConnection con = (HttpURLConnection) (new URL(url))
				.openConnection(ConProxy);
			/* リクエストの設定 */
			con.setRequestMethod(method);
			con.addRequestProperty("Cookie", cookieProp);
			if (connectionProp != null){
				con.addRequestProperty("Connection", connectionProp);
			}
			if (doInput){
				con.setDoInput(true);
			}
			if (doOutput){
				con.setDoOutput(true);
			}
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
		debug("■Connect: " + con.getRequestMethod() + ","
			+ (Cookie == null ? "" : "Cookie<" + con.getRequestProperty("Cookie") +">,")
			+ (con.getDoInput() ? "DoInput," : "")
			+ (con.getDoOutput() ? "DoOutput," : "")
			+ (HttpURLConnection.getFollowRedirects() ? "FollowRedirects," : "")
			+ (con.getRequestProperty("Connection") == null ?
				"" : "Connection " + con.getRequestProperty("Connection"))
			+ "\n");
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

	private String detectCookie(HttpURLConnection con){
		nicomap.putConnection(con);
		if (Debug){
			nicomap.printAll(System.out);
		}
		String cookie = "";
		String value = null;
		if ((value = nicomap.get("Set-Cookie")) != null
				&& value.indexOf(";") >= 0){
			cookie = value.substring(0, value.indexOf(";"));
		}
		debug("■<Set-Cookie><" + cookie + ">\n");
		return cookie;
	}

	private boolean login() {
		try {
			System.out.print("Trying login...");
			String url = "https://secure.nicovideo.jp/secure/login?site=niconico";
			debug("\n■HTTPS<" + url + ">\n");
			HttpURLConnection con = (HttpsURLConnection) (new URL(url))
				.openConnection(ConProxy);
			/* 出力のみ */
			con.setDoOutput(true);
			HttpURLConnection.setFollowRedirects(false);
			con.setInstanceFollowRedirects(false);
			con.setRequestMethod("POST");
			con.addRequestProperty("Connection", "close");
			connect(con);
			StringBuffer sb = new StringBuffer(4096);
			sb.append("next_url=&");
			sb.append("mail=");
			sb.append(URLEncoder.encode(User, "Shift_JIS"));
			sb.append("&password=");
			sb.append(URLEncoder.encode(Pass, "Shift_JIS"));
			sb.append("&submit.x=103&submit.y=16");
			debug("■write:" + sb.toString() + "\n");
			OutputStream os = con.getOutputStream();
			os.write(sb.substring(0).getBytes());
			os.flush();
			os.close();
			Stopwatch.show();
			debug("■Response:" + Integer.toString(con.getResponseCode()) + " " + con.getResponseMessage() + "\n");
			int code = con.getResponseCode();
			if (code < HttpURLConnection.HTTP_OK || code >= HttpURLConnection.HTTP_BAD_REQUEST) { // must 200 <= <400
				System.out.println("Can't login:" + con.getResponseMessage());
				return false;
			}
			Cookie = detectCookie(con);
			con.disconnect();
			if (Cookie == null || Cookie.isEmpty()) {
				System.out.println("Can't login: cannot set cookie.");
				return false;
			}
			System.out.println("Logged in.");
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
		ByteArrayBuffer bytebuf = new ByteArrayBuffer();
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
			}
			/*
			if ("MS932".equals(encoding) && len == 2 &&
					(b[1] == 0x5C || b[1] == 0x7C)){
				System.out.println("Checked Danger Byte Code<" + b[1] + ">, better to fix?");
			}
			*/

			bytebuf.write(b, 0, len);
		}
		try {
			bytebuf.flush();
			bytebuf.close();
		} catch (IOException e) {
			// e.printStackTrace();
		}
		String dest = bytebuf.toString();	// to Unicode
		return dest;
	}

	private String VideoTitle = null;
	private int VideoLength = -1;

	private static final String TITLE_PARSE_STR_START = "<title>";
	//RC2になってタイトルが変更、使わなくなった。
	//private static final String TITLE_PARSE_STR_END = "</title>";
	private static final String TITLE_END = "‐";

	public boolean getVideoHistoryAndTitle(String tag, String watchInfo) {
		String url = "http://www.nicovideo.jp/watch/" + tag + watchInfo;
		System.out.print("Getting video history...");
		try {
			HttpURLConnection con = urlConnectGET(url);
			if (con == null || con.getResponseCode() != HttpURLConnection.HTTP_OK){
				System.out.println("ng.\nCan't getVideoHistory:" + url);
				return false;
			}
			String new_cookie = detectCookie(con);
			if (new_cookie == null || new_cookie.isEmpty()) {
				System.out.println("ng.\nCan't getVideoHistory: cannot get cookie.");
				/*
				con.disconnect();
				return false;
				*/
			}
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
			while ((ret = br.readLine()) != null) {
				Stopwatch.show();
				debug("■readLine(" + encoding + "):" + ret + "\n");
				if ((index = ret.indexOf(TITLE_PARSE_STR_START)) >= 0) {
					VideoTitle = safeFileName(
						ret.substring(index+TITLE_PARSE_STR_START.length(),
							ret.lastIndexOf(TITLE_END)));
					System.out.print("<" + VideoTitle + ">...");
					break;
				}
			}
			br.close();
			con.disconnect();
			System.out.println("ok.");
			Cookie += "; " + new_cookie;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean NeedsKey = false;
	private String Premium = "";
	private String OptionalThraedID = "";	// normal Comment ID when Community DOUGA
	private boolean economy = false;
	public boolean getVideoInfo(String tag, String watchInfo, String time) {
		if (!getVideoHistoryAndTitle(tag, watchInfo)) {
			return false;
		}
		try {
			String url = "http://flapi.nicovideo.jp/api/getflv/" + tag;
			if (tag.startsWith("nm")) {
				url += "?as3=1";
			}
			System.out.print("Getting video informations...");
			HttpURLConnection con = urlConnectGET(url);
			if (con == null || con.getResponseCode() != HttpURLConnection.HTTP_OK){
				System.out.println("ng.\nCan't getVideoInfo:" + url);
				return false;
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
			if (ThreadID == null || VideoUrl == null
				|| MsgUrl == null || UserID == null) {
				System.out.println("ng.\nCan't get video information keys.");
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
		/*
		if (time != null && !time.isEmpty()) {
			if (!getWayBackKey(time)) { // WayBackKey
				System.out.println("It may be wrong Date.");
				return false;
			}
		}
		*/
		return true;
	}

	private byte[] buf = new byte[1024 * 1024];

	private String VideoUrl = null;
	private String ContentType;
	private String ContentDisp;

	public File getVideo(final File file, final JLabel status, final ConvertStopFlag flag) {
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
				if("403".equals(getExtraError())){
					setExtraError("=不適切な動画の可能性。readmeNew.txt参照");
				}
				return null;
			}
			InputStream is = con.getInputStream();
			OutputStream os = new FileOutputStream(file);
			if(Debug){
				NicoMap videoMap = new NicoMap();
				videoMap.putConnection(con);
				videoMap.printAll(System.out);
			}

			ContentType = con.getHeaderField("Content-Type");
			ContentDisp = con.getHeaderField("Content-Disposition");
			int max_size = con.getContentLength();	// -1 when invalid
			System.out.print("size="+(max_size/1000)+"Kbytes");
			System.out.println(", type=" + ContentType + ", " + ContentDisp);
			System.out.print("Downloading video...");
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
			public String dlmsg(){ return "コメント"; }
		},
		OWNER{
			public String dlmsg(){ return "投稿者コメント"; }
		},
		OPTIONAL{
			public String dlmsg(){ return "オプショナルスレッド"; }
		};
		public abstract String dlmsg();
	}

	public File getComment(final File file, final JLabel status, final String back_comment,
			final String time, final ConvertStopFlag flag, final int comment_mode) {
		if (time != null && !time.isEmpty() && "0".equals(WayBackKey)){
			if (!getWayBackKey(time)) { // WayBackKey
				// System.out.println("It may be wrong Date.");
				return null;
			}
		}
		boolean useNewComment = true;
		if(comment_mode == 2 || comment_mode == 0 && !hasNewCommentBegun){
			useNewComment = false;
		}
		return downloadComment(file, status, back_comment, CommentType.USER, flag, useNewComment);
	}

	public File getOwnerComment(final File file, final JLabel status, final ConvertStopFlag flag) {
		return downloadComment(file, status, STR_OWNER_COMMENT, CommentType.OWNER, flag, false);
	}

	public File getOptionalThread(final File file, final JLabel status, final String optionalThreadID,
			final String back_comment, final String time, final ConvertStopFlag flag,final int comment_mode) {
		ThreadID = optionalThreadID;
	 	NeedsKey = false;
	 	Official = "";
		// この後でOwnerCommentを取得するとユーザー動画の投稿者コメントが取得される。
		if (time != null && !time.isEmpty()){
		 	WayBackKey = "0";
			if (!getWayBackKey(time)) { // WayBackKey
				return null;
			}
		}
		boolean useNewComment = true;
		if(comment_mode == 2 || comment_mode == 0 && !hasNewCommentBegun){
			useNewComment = false;
		}
		return downloadComment(file, status, back_comment, CommentType.OPTIONAL, flag, useNewComment);
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

	private String commentCommand2009(CommentType commentType, String back_comment){
		String req;
		String wayback =  "\" when=\"" + WayBackTime + "\" waybackkey=\"" + WayBackKey;
		String resfrom;
		if(!back_comment.endsWith("-")){
			resfrom = "";
		}else {
			resfrom = "\" res_from=\"" + back_comment;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("<packet>");
		sb.append("<thread thread=\"" + ThreadID);
		sb.append("\" version=\"20090904");
		if(!"0".equals(WayBackKey)){
			sb.append(wayback);
		}
		sb.append("\" user_id=\"" + UserID);
		if(NeedsKey){
			sb.append(Official);
		}
		sb.append("\" scores=\"1");	//NGscore
		sb.append("\"/>");
		//thread end, thread_leaves start
		sb.append("<thread_leaves thread=\"" + ThreadID);
		if(!"0".equals(WayBackKey)){
			sb.append(wayback);
		}
		sb.append("\" user_id=\"" + UserID);
		if(NeedsKey){
			sb.append(Official);
		}
		sb.append("\" scores=\"1");	//NGscore
		sb.append(resfrom);
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
			String back_comment, CommentType commentType, final ConvertStopFlag flag, boolean useNewComment) {
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
			if (file.canRead()	 && file.delete()) {	//	ファイルがすでに存在するなら削除する。
				System.out.print("previous " + commentType.toString().toLowerCase() + " comment deleted...");
			}
			fos = new FileOutputStream(file);
			HttpURLConnection con = urlConnect(MsgUrl, "POST", Cookie, true, true, "close");
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
				req = commentCommand2009(commentType, back_comment);
				System.out.print("New comment mode...");
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
			// fos.close();
			con.disconnect();
			return file;
		} catch (IOException ex) {
			ex.printStackTrace();
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
			nicomap.putArray(ret);
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
			nicomap.putArray(ret);
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

	private boolean loginCheck() {
		String url = "http://www.nicovideo.jp";
		System.out.print("Checking login...");
		// GET (NO_POST), UTF-8, AllowAutoRedirect,
/*
		BufferedReader br = null;
		try {
*/
			HttpURLConnection con = urlConnectGET(url);
			// response 200, 302 is OK
			if (con == null){
				System.out.println("ng.\nCan't read TopPage at loginCheck:" + url);
				return false;
			}
			String new_cookie = detectCookie(con);
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
/*
			String encoding = con.getContentEncoding();
			if (encoding == null){
				encoding = "UTF-8";
			}
			br = new BufferedReader(new InputStreamReader(con
					.getInputStream(), encoding));
			System.out.print("  Checking TopPage...");
			// debug("\n");
			String ret;
			boolean found = false;
			while ((ret = br.readLine()) != null) {
				Stopwatch.show();
				// debug("■readLine(" + encoding + "):" + ret + "\n");
				// NO_LOGIN_TAG = "var User = { id: false";
				if (ret.indexOf("var User = { id: false") >= 0) {
					System.out.println("ng. Not logged in.");
					con.disconnect();
					return false;
				}
				if (ret.indexOf("var User = { id:") >= 0){
					// UserID is valid
					found = true;
					debug("\n■readLine(" + encoding + "):" + ret);
					break;
				}
			}
			con.disconnect();
			if (!found){
				System.out.println("ng. Can't found UserID Key. Is Niconico TopPage format ◆CHANGED?◆");
				return false;
			}
*/
			if (new_cookie != null && !new_cookie.isEmpty()) {
				Cookie += "; " + new_cookie;
			}
			debug("\n■Now Cookie is<" + Cookie + ">\n");
			System.out.println("ok.");
			return true;
/*
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		} finally {
			if (br != null){
				try { br.close(); } catch (IOException e) { }
			}
		}
*/
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
/*
		} if (VideoLength < 600){		//↓数値計算では正しい数はでない。実際の分布による。
			return Integer.toString((VideoLength+59)/60 * 100);
		} else {
*/
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
			status.setText(msg + "ダウンロード：" + per + "パーセント完了");
		} else {
			status.setText(msg + "ダウンロード中：" + Integer.toString(size >> 10)
					+ "kbytesダウンロード");
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
}
