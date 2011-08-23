package saccubus.net;

import java.io.File;
import java.net.URL;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URLEncoder;
import java.net.URLDecoder;
import javax.swing.JLabel;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;
import saccubus.ConvertStopFlag;

import saccubus.MyDateFormat;

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
	private final ConvertStopFlag StopFlag;
	private final Proxy ConProxy;
	private boolean Debug = false;

	private static final String DEBUG_PROXY = "debug/";

	public NicoClient(final String user, final String pass,
			final ConvertStopFlag flag, String proxy, final int proxy_port) {
		User = user;
		Pass = pass;
		StopFlag = flag;
		Proxy tmpProxy;
		if (proxy != null && proxy.startsWith(DEBUG_PROXY)){
			System.out.println("Print debug information.");
			Debug = true;
			proxy = proxy.substring(DEBUG_PROXY.length());
		}
		if (proxy != null && proxy.length() > 0 && proxy_port >= 0
				&& proxy_port <= 65535) {
			try{
				tmpProxy = new Proxy(Proxy.Type.HTTP,
						new InetSocketAddress(proxy, proxy_port));
			} catch(Exception ex){
				System.out.println("Unable to make Proxy. maybe bug.");
				ex.printStackTrace();
				ConProxy = null;
				return;
			}
			ConProxy = tmpProxy;
		} else {
			ConProxy = Proxy.NO_PROXY;
		}
		// ログイン
		Logged_in = login();
	}

	private void debug(String messege){
		if (Debug){
			System.out.print(messege);
		}
	}

	private String Cookie = null;

	private HttpURLConnection reqConnectionGet(String url){
		try {
			debug("\n■URL<" + url + ">\n");
			HttpURLConnection con = (HttpURLConnection) (new URL(url))
			.openConnection(ConProxy);
			/* リクエストの設定 */
			debug("■Connect: GET,Cookie<" + Cookie + ">,DoInput,Connection close\n");
			con.setRequestMethod("GET");
			con.addRequestProperty("Cookie", Cookie);
			con.addRequestProperty("Connection", "close");
			con.setDoInput(true);
			con.connect();
			debug("■Response:" + Integer.toString(con.getResponseCode()) + " " + con.getResponseMessage() + "\n");
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				return con;
			}
		} catch(IOException ex){
			ex.printStackTrace();
		} catch(IllegalStateException ex){
			ex.printStackTrace();
		}
		return null;
	}

	private String readConnection(HttpURLConnection con){
		try {
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
		String cookie = null;
		String key;
		String value;
		for (int i = 1; (key = con.getHeaderFieldKey(i)) != null; i++){
			value = con.getHeaderField(i);
			debug("■header("+i+")<" + key+"><" + value + ">\n");
			if (key.equalsIgnoreCase("Set-Cookie")) {
				value = con.getHeaderField(i);
				if (value != null) {
					cookie = value.substring(0, value.indexOf(";"));
				}
			}
		}
		debug("■set-cookie<" + cookie + ">\n");
		return cookie;
	}

	private boolean login() {
		try {
			System.out.print("Trying login...");
			HttpURLConnection con = (HttpsURLConnection) (new URL(
					"https://secure.nicovideo.jp/secure/login?site=niconico"))
					.openConnection(ConProxy);
			debug("\n■connection<" + con.toString() + ">\n");
			/* 出力のみ */
			con.setDoOutput(true);
			HttpURLConnection.setFollowRedirects(false);
			con.setInstanceFollowRedirects(false);
			con.setRequestMethod("POST");
			con.addRequestProperty("Connection", "close");
			debug("■connect: POST,DoOuput,Connection close,FollowRedirects false\n");
			con.connect();
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
			debug("■Response:" + Integer.toString(con.getResponseCode()) + " " + con.getResponseMessage() + "\n");
			int code = con.getResponseCode();
			if (code < 200 || code >= 400) {
				System.out.println("Can't login:" + con.getResponseMessage());
				return false;
			}
			Cookie = detectCookie(con);
			con.disconnect();
			if (Cookie == null) {
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
	private static String safeFileName(String str) {
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

	private String VideoTitle = null;
	private int VideoLength = -1;

	private static final String TITLE_PARSE_STR_START = "<title>";
	//RC2になってタイトルが変更、使わなくなった。
	//private static final String TITLE_PARSE_STR_END = "</title>";
	private static final String TITLE_END = "‐";

	public boolean getVideoHistoryAndTitle(String tag) {
		String url = "http://www.nicovideo.jp/watch/" + tag;
		System.out.print("Getting video history...");
		try {
			HttpURLConnection con = reqConnectionGet(url);
			if (con == null){
				System.out.println("ng.\nCan't getVideoHistory:" + url);
				return false;
			}
			String new_cookie = detectCookie(con);
			BufferedReader br = new BufferedReader(new InputStreamReader(con
					.getInputStream(), "UTF-8"));
			System.out.print("ok.\nCheking VideoTitle...");
			debug("\n");
			String ret;
			int index = -1;
			while ((ret = br.readLine()) != null) {
				//
				debug("■readLine(UTF-8):" + ret + "\n");
				if ((index = ret.indexOf(TITLE_PARSE_STR_START)) >= 0) {
					VideoTitle = safeFileName(
						ret.substring(index+TITLE_PARSE_STR_START.length(),
							ret.lastIndexOf(TITLE_END)));
					break;
				}
			}
			br.close();
			con.disconnect();
			if (new_cookie == null) {
				System.out.println("ng.\nCan't getVideoHistory: cannot get cookie.");
				return false;
			}
			System.out.println("ok.");
			Cookie += "; ";
			Cookie += new_cookie;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean NeedsKey = false;
	public boolean getVideoInfo(String tag, String time) {
		if (!getVideoHistoryAndTitle(tag)) {
			return false;
		}
		try {
			String url = "http://flapi.nicovideo.jp/api/getflv/" + tag;
			if (tag.startsWith("nm")) {
				url += "?as3=1";
			}
			System.out.print("Getting video informations...");
			HttpURLConnection con = reqConnectionGet(url);
			if (con == null){
				System.out.println("ng.\nCan't getVideoInfo:" + url);
				return false;
			}
			String ret = readConnection(con);
			if (ret == null || ret.equals("")){
				System.out.println("ng.\nCan't getVideoInfo: null respense.");
				return false;
			}
			String[] array = ret.split("&");
			if (Debug){
				for(int i=0;i<array.length;i++){
					String a = URLDecoder.decode(array[i],"UTF-8");
					if (a.indexOf('%')>=0){
						a = "DoubleDecode■"+URLDecoder.decode(a, "UTF-8");
					}
					debug("■array_UTF-8("+i+")"+a+"\n");
				}
			}
			ret = URLDecoder.decode(ret, "Shift_JIS");
			debug("■Decoded(Shift_JIS):" + ret + "\n");
			array = ret.split("&");
			int cnt = 0;
			for (int i = 0; i < array.length; i++) {
				int idx = array[i].indexOf("=");
				if (idx < 0) {
					continue;
				}
				String key = array[i].substring(0, idx);
				String value = array[i].substring(idx + 1);
				if (ThreadID == null && key.equalsIgnoreCase("thread_id")) {
					ThreadID = value;
					cnt++;
				} else if (VideoUrl == null && key.equalsIgnoreCase("url")) {
					VideoUrl = value;
					cnt++;
				} else if (MsgUrl == null && key.equalsIgnoreCase("ms")) {
					MsgUrl = value;
					cnt++;
				} else if (UserID == null && key.equalsIgnoreCase("user_id")) {
					UserID = value;
					cnt++;
				} else if (key.equalsIgnoreCase("needs_key")) {
			        NeedsKey = true;
				} else if (VideoLength < 0 && key.equalsIgnoreCase("l")) {
					try {
						VideoLength = Integer.parseInt(value);
					} catch (NumberFormatException e) {
						VideoLength = -1;
					}
				}
			}
			if (cnt < 4) {
				System.out.println("ng.\nCan't get video information keys.");
				return false;
			}
			System.out.println("ok.");
			System.out.println("Video:<" + VideoUrl + ">; Comment:<" + MsgUrl
					+ (NeedsKey ? ">; needs_key=1" : ">"));
			System.out.println("Video time length: " + VideoLength + "sec");
			System.out.println("ThreadID:<" + ThreadID + "> Uploaded on "
					+ MyDateFormat.formatTime(ThreadID));
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
		if (time != null && !time.equals("")
				&& !getWayBackKey(time)) { // WayBackKey
			System.out.println("It may be wrong Date.");
			//return false;
		}
		return true;
	}

	private byte[] buf = new byte[1024 * 1024 * 4];

	private String VideoUrl = null;

	public File getVideo(final File file, final JLabel status) {
		try {
			System.out.print("Getting video size...");
			if (VideoUrl == null) {
				System.out.println("Video url is not detected.");
				return null;
			}
			if (file.canRead()) { // ファイルがすでに存在するなら削除する。
				file.delete();
			}
			HttpURLConnection con = (HttpURLConnection) (new URL(VideoUrl))
					.openConnection(ConProxy);
			debug("\n■connection<" + con.toString() + ">\n");
			/* 出力のみ */
			debug("■connect: GET,Cookie<" + Cookie + ">,DoInput\n");
			con.setDoInput(true);
			con.setRequestMethod("GET");
			con.addRequestProperty("Cookie", Cookie);
			con.connect();
			debug("■Response:" + Integer.toString(con.getResponseCode()) + " " + con.getResponseMessage() + "\n");
			if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.println("Can't get video:" + VideoUrl);
				return null;
			}
			InputStream is = con.getInputStream();
			OutputStream os = new FileOutputStream(file);
			String content_length_str = con.getHeaderField("Content-length");
/*
			if(Debug){
				String k = null;
				for(int i=1;(k=con.getHeaderFieldKey(i))!=null;i++){
					debug("■header("+i+")<"+k+"><"+con.getHeaderField(i)+">\n");
				}
			}
*/
			int max_size = 0;
			if (content_length_str != null && !content_length_str.equals("")) {
				max_size = Integer.parseInt(content_length_str);
			}
			int size = 0;
			System.out.println("size="+(max_size/1000)+"Kbytes");
			System.out.print("Downloading video...");
			int read = 0;
			debugsInit();
			while ((read = is.read(buf, 0, buf.length)) > 0) {
				debugsAdd(read);
				size += read;
				os.write(buf, 0, read);
				sendStatus(status, "動画", max_size, size);
				if (StopFlag.needStop()) {
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
		}
		finally{
			debug("■read+write statistics(bytes) ");
			debugsOut();
		}
		return null;
	}

	private String UserID = null;
	private String ThreadID = null;
	private String MsgUrl = null;
	private final static String STR_OWNER_COMMENT = "500";

	public File getComment(final File file, final JLabel status, String back_comment) {
		return downloadComment(file, status, back_comment, false);
	}

	public File getOwnerComment(final File file, final JLabel status) {
		return downloadComment(file, status, STR_OWNER_COMMENT, true);
	}

	private File downloadComment(final File file, final JLabel status,
			String back_comment, boolean isOwnerComment) {
		System.out.print("Downloading " + (isOwnerComment ? "owner " : "")
				+"comment size:" + back_comment + "...");
		String official = "";	/* 公式動画用のkey追加 */
		if(NeedsKey){
			if((force184 == null || threadKey == null)
					&& !getOfficialOption(ThreadID)) {
				return null;
			}
			official ="force_184=\"" + force184
			+ "\" threadkey=\"" + threadKey + "\" ";
		}
		try {
			if (file.canRead()) {	//	ファイルがすでに存在するなら削除する。
				file.delete();
			}
			OutputStream fos = new FileOutputStream(file);
			HttpURLConnection con = (HttpURLConnection) (new URL(MsgUrl))
					.openConnection(ConProxy);
			debug("\n■connection<" + con.toString() + ">\n");
			debug("■connect: POST,Cookie<" + Cookie + ">,DoInput,DoOutput,Connection close\n");
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestMethod("POST");
			con.addRequestProperty("Cookie", Cookie);
			con.addRequestProperty("Connection", "close");
			con.connect();
			OutputStream os = con.getOutputStream();
			String req;
			if (isOwnerComment){
				req = "<thread user_id=\"" + UserID
					+ "\" when=\"0\" waybackkey=\"0"
					+ "\" res_from=\"-" + back_comment
					+ "\" version=\"20061206\" thread=\"" + ThreadID
					+ "\" fork=\"1\"  "+ official + "/>";
			} else {
				req = "<thread user_id=\"" + UserID + "\" when=\""
					+ WayBackTime + "\" waybackkey=\"" + WayBackKey
					+ "\" res_from=\"-" + back_comment
					+ "\" version=\"20061206\" thread=\"" + ThreadID
					+ "\" " + official + "/>";
			}
			//??? when="0" waybackkey="0" が必要か？
			debug("■write:" + req + "\n");
			os.write(req.getBytes());
			os.flush();
			os.close();
			debug("■Response:" + Integer.toString(con.getResponseCode()) + " " + con.getResponseMessage() + "\n");
			if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.println("ng.\nCan't download comment:" + MsgUrl);
				return null;
			}
			InputStream is = con.getInputStream();
			int read = 0;
			int max_size = 0;
			String content_length_str = con.getHeaderField("Content-length");
			if (content_length_str != null && !content_length_str.equals("")) {
				max_size = Integer.parseInt(content_length_str);
			}
			int size = 0;
			String dlmsg = (isOwnerComment ? "投稿者" : "") + "コメントダウンロード";
			debugsInit();
			while ((read = is.read(buf, 0, buf.length)) > 0) {
				debugsAdd(read);
				fos.write(buf, 0, read);
				size += read;
				sendStatus(status, dlmsg, max_size, size);
				if (StopFlag.needStop()) {
					System.out.println("\nStopped.");
					is.close();
					os.flush();
					os.close();
					con.disconnect();
					if (file.delete()){
						System.out.println("comment deleted.");
					}
					return null;
				}
			}
			System.out.println("ok.");
			is.close();
			fos.flush();
			fos.close();
			con.disconnect();
			return file;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		finally{
			debug("■read+write statistics(bytes) ");
			debugsOut();
		}

		return null;
	}

	private String threadKey = null;
	private String force184 = null;

	private boolean getOfficialOption(String threadId) {
		String url = "http://flapi.nicovideo.jp/api/getthreadkey?thread="
			+threadId;
		System.out.print("\nGetting Official options (threadkey)...");
		if (force184 != null && threadKey != null){
			System.out.println("ok. But this call twice, not necessary.");
			return true;
		}
		HttpURLConnection con = reqConnectionGet(url);
		if (con == null){
				System.out.println("ng.\nCan't get Oficial option:" + url);
				return false;
		}
		String ret = readConnection(con);
		if (ret == null || ret.equals("")){
			System.out.println("ng.\nNull response.");
			return false;
		}
		String[] array = ret.split("&");
		for(int i=0;i<array.length;i++){debug("■array("+i+")"+array[i]+"\n");}
		//	int cnt = 0;
		for (int i = 0; i < array.length; i++) {
			int idx = array[i].indexOf("=");
			if (idx < 0) {
				continue;
			}
			String key = array[i].substring(0, idx);
			String value = array[i].substring(idx + 1);
			if (threadKey == null && key.equalsIgnoreCase("threadkey")) {
				threadKey = value;
				//	cnt++;
			} else if (force184 == null && key.equalsIgnoreCase("force_184")) {
				force184 = value;
				//	cnt++;
			}
		}
		if (threadKey == null || force184 == null) {
			System.out.println("ng.\nCan't get Oficial option.");
			System.out.println("\nret: " + ret);
			return false;
		}
		System.out.print("ok...");
		return true;
    }

	private String WayBackKey = "0";
	private String WayBackTime = "0";
	private final static String WAYBACKKEY_STR = "waybackkey=";

	/**
	 * @param time
	 * @return
	 */
	private boolean getWayBackKey(String time) {
		System.out.print("Setting wayback time...");
		if (WayBackKey != "0"){
			System.out.println("ok. But this call twice, not necessary.");
			return true;
		}
		MyDateFormat mdf = new MyDateFormat();
		if (!mdf.makeTime(time)){
			System.out.println("ng.\nCannot parse time.\"" + time + "\"");
			return false;
		}
		String waybacktime = mdf.getWayBackTime();
		System.out.println("ok.(" + mdf.formatDate() + "): " + waybacktime);
		System.out.print("Getting wayback key...");
		String url = "http://flapi.nicovideo.jp/api/getwaybackkey?thread="
				+ ThreadID;
		HttpURLConnection con = reqConnectionGet(url);
		if (con == null){
			System.out.println("ng.\nCan't get wayback key:" + url);
			return false;
		}
		String ret = readConnection(con);
		int idx = 0;
		if (ret == null ||
			(idx = ret.indexOf(WAYBACKKEY_STR)) < 0) {
			System.out
				.println("ng.\nCannot find wayback key from response.");
			return false;
		}
		String waybackkey = ret.substring(idx + WAYBACKKEY_STR.length());
		if ((idx = waybackkey.indexOf("&")) >= 0){
			waybackkey = waybackkey.substring(0, idx);
		}
		if (waybackkey == null || waybackkey.equals("")) {
			System.out.println("ng.\nCannot get wayback key.");
			return false;
		}
		System.out.println("ok.\nwayback key: " + waybackkey);
		WayBackTime = waybacktime;
		WayBackKey = waybackkey;
		return true;
	}

	public String getBackCommentFromLength(String def) {
		if (VideoLength < 0) {
			return def;
/**
		} else if (VideoLength >= 0 && VideoLength < 60) {
			return "100";
		} else if (VideoLength >= 60 && VideoLength < 300) {
			return "250";
		} else if (VideoLength >= 300 && VideoLength < 600) {
			return "500";
		} else {
*/
		} if (VideoLength < 600){		//↓■ 要検証 ■
			return Integer.toString((VideoLength+59)/60 * 100);
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
	private void debugsOut(){
		if(!Debug) return;
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
		if (max_size != 0) {
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

}
