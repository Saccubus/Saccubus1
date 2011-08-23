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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import java.text.*;
import javax.net.ssl.HttpsURLConnection;
import saccubus.ConvertStopFlag;

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

	public NicoClient(final String user, final String pass,
			final ConvertStopFlag flag, final String proxy, final int proxy_port) {
		User = user;
		Pass = pass;
		if (proxy != null && proxy.length() > 0 && proxy_port >= 0
				&& proxy_port <= 65535) {
			ConProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy,
					proxy_port));
		} else {
			ConProxy = Proxy.NO_PROXY;
		}
		// ログイン
		Logged_in = login();
		StopFlag = flag;
	}

	private String Cookie = null;

	private boolean login() {
		try {
			HttpURLConnection con = (HttpsURLConnection) (new URL(
					"https://secure.nicovideo.jp/secure/login?site=niconico"))
					.openConnection(ConProxy);
			/* 出力のみ */
			con.setDoOutput(true);
			HttpURLConnection.setFollowRedirects(false);
			con.setInstanceFollowRedirects(false);
			con.setRequestMethod("POST");
			con.addRequestProperty("Connection", "close");
			con.connect();
			StringBuffer sb = new StringBuffer(4096);
			sb.append("next_url=&");
			sb.append("mail=");
			sb.append(URLEncoder.encode(User, "Shift_JIS"));
			sb.append("&password=");
			sb.append(URLEncoder.encode(Pass, "Shift_JIS"));
			sb.append("&submit.x=103&submit.y=16");
			OutputStream os = con.getOutputStream();
			os.write(sb.substring(0).getBytes());
			os.flush();
			os.close();
			int code = con.getResponseCode();
			if (code < 200 || code >= 400) {
				System.out.println("Can't login:" + con.getResponseMessage());
				return false;
			}
			int i = 1;
			String key;
			String value;
			while ((key = con.getHeaderFieldKey(i)) != null) {
				if (key.equalsIgnoreCase("Set-Cookie")) {
					value = con.getHeaderField(i);
					if (value != null) {
						Cookie = value.substring(0, value.indexOf(";"));
					}
				}
				i++;
			}
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
		return str;
	}

	private String VideoTitle = null;

	private int VideoLength = -1;

	private static final String TITLE_PARSE_STR_START = "<title>";

	//RC2になってタイトルが変更、使わなくなった。
	//private static final String TITLE_PARSE_STR_END = "</title>";

	public boolean getVideoHistoryAndTitle(String tag) {
		String url = "http://www.nicovideo.jp/watch/" + tag;
		System.out.print("Getting video history...");
		String new_cookie = null;
		try {
			HttpURLConnection con = (HttpURLConnection) (new URL(url))
					.openConnection(ConProxy);
			/* リクエストの設定 */
			con.setRequestMethod("GET");
			con.addRequestProperty("Cookie", Cookie);
			con.addRequestProperty("Connection", "close");
			con.connect();
			if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.println("Can't getVideoHistory:" + url);
				return false;
			}
			int i = 1;
			String key;
			String value;
			while ((key = con.getHeaderFieldKey(i)) != null) {
				if (key.equalsIgnoreCase("Set-Cookie")) {
					value = con.getHeaderField(i);
					if (value != null) {
						new_cookie = value.substring(0, value.indexOf(";"));
					}
				}
				i++;
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(con
					.getInputStream(), "UTF-8"));
			String ret;
			int index = -1;
			while ((ret = br.readLine()) != null && index < 0) {
				if ((index = ret.indexOf(TITLE_PARSE_STR_START)) >= 0) {
					VideoTitle = ret.substring(index+TITLE_PARSE_STR_START.length(), ret.indexOf("‐", index));
					VideoTitle = safeFileName(VideoTitle);
				}
			}
			br.close();
			con.disconnect();
			if (new_cookie == null) {
				System.out.println("Can't getVideoHistory: cannot get cookie.");
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
			HttpURLConnection con = (HttpURLConnection) (new URL(url))
					.openConnection(ConProxy);
			/* リクエストの設定 */
			con.setRequestMethod("GET");
			con.addRequestProperty("Cookie", Cookie);
			con.addRequestProperty("Connection", "close");
			con.connect();
			if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.println("Can't getVideoInfo:" + url);
				return false;
			}
			/* 戻り値の取得 */
			BufferedReader br = new BufferedReader(new InputStreamReader(con
					.getInputStream()));
			String ret = br.readLine();
			br.close();
			con.disconnect();
			ret = URLDecoder.decode(ret, "Shift_JIS");
			String[] array = ret.split("&");
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
				} else if (VideoLength < 0 && key.equalsIgnoreCase("l")) {
					try {
						VideoLength = Integer.parseInt(value);
					} catch (NumberFormatException e) {
						VideoLength = -1;
					}
				}
			}
			if (cnt < 4) {
				System.out
						.println("ng.\nCan't getVideoInfo: Can't get video informations.");
				return false;
			}
			System.out.println("ok.");
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
		if (!(time == null || time.equals("")) && !getWayBackKey(time)) { // WayBackKey
			return false;
		}
		return true;
	}

	private byte[] buf = new byte[1024 * 1024];

	private String VideoUrl = null;

	public File getVideo(final File file, final JLabel status) {
		if (VideoUrl == null) {
			System.out.println("Video url is not detected.");
			return null;
		}
		try {
			if (file.canRead()) { // ファイルがすでに存在するなら削除する。
				file.delete();
			}
			HttpURLConnection con = (HttpURLConnection) (new URL(VideoUrl))
					.openConnection(ConProxy);
			/* 出力のみ */
			con.setDoInput(true);
			con.setRequestMethod("GET");
			con.addRequestProperty("Cookie", Cookie);
			con.connect();
			if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.println("Can't get video:" + VideoUrl);
				return null;
			}
			InputStream is = con.getInputStream();
			OutputStream os = new FileOutputStream(file);
			String content_length_str = con.getHeaderField("Content-length");
			int max_size = 0;
			if (content_length_str != null && !content_length_str.equals("")) {
				max_size = Integer.parseInt(content_length_str);
			}
			int size = 0;
			System.out.print("Downloading video...");
			int read = 0;
			while ((read = is.read(buf, 0, buf.length)) > 0) {
				size += read;
				os.write(buf, 0, read);
				if (max_size != 0) {
					String per = Double.toString((((double) size) * 100)
							/ max_size);
					per = per.substring(0, Math.min(per.indexOf(".") + 3, per
							.length()));
					status.setText("動画ダウンロード：" + per + "パーセント完了");
				} else {
					status.setText("動画ダウンロード中：" + Integer.toString(size >> 10)
							+ "kbytesダウンロード");
				}
				if (StopFlag.needStop()) {
					System.out.println("Stopped.");
					is.close();
					os.flush();
					os.close();
					con.disconnect();
					file.delete();
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
		return null;
	}

	private String UserID = null;

	private String ThreadID = null;

	private String MsgUrl = null;

	public File getComment(final File file, final JLabel status,
			String back_comment) {
		System.out.print("Downloading comment size:" + back_comment + "...");
		try {
			if (file.canRead()) { // ファイルがすでに存在するなら削除する。
				file.delete();
			}
			OutputStream fos = new FileOutputStream(file);
			HttpURLConnection con = (HttpURLConnection) (new URL(MsgUrl))
					.openConnection(ConProxy);
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestMethod("POST");
			con.addRequestProperty("Cookie", Cookie);
			con.addRequestProperty("Connection", "close");
			con.connect();
			OutputStream os = con.getOutputStream();
			String req = "<thread user_id=\"" + UserID + "\" when=\""
					+ WayBackTime + "\" waybackkey=\"" + WayBackKey
					+ "\" res_from=\"-" + back_comment
					+ "\" version=\"20061206\" thread=\"" + ThreadID + "\" />";
			os.write(req.getBytes());
			os.flush();
			os.close();
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
			while ((read = is.read(buf, 0, buf.length)) > 0) {
				fos.write(buf, 0, read);
				size += read;
				if (max_size != 0) {
					String per = Double.toString((((double) size) * 100)
							/ max_size);
					per = per.substring(0, Math.min(per.indexOf(".") + 3, per
							.length()));
					status.setText("コメントダウンロード：" + per + "パーセント完了");
				} else {
					status.setText("コメントダウンロード中："
							+ Integer.toString(size >> 10) + "kbytesダウンロード");
				}
				if (StopFlag.needStop()) {
					System.out.println("Stopped.");
					is.close();
					os.flush();
					os.close();
					con.disconnect();
					file.delete();
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

		return null;
	}

	private String WayBackKey = "0";

	private String WayBackTime = "0";

	private final static DateFormat DateFmt = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss");

	private final static DateFormat DateFmt2 = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm");

	private final static String WAYBACKKEY_STR = "waybackkey=";

	private boolean getWayBackKey(String time) {
		System.out.print("Setting wayback time...");
		Date date = null;
		String waybacktime = "0";
		try {
			date = DateFmt.parse(time);
		} catch (ParseException ex2) {
			date = null;
		}
		if (date == null) {
			try {
				date = DateFmt2.parse(time);
			} catch (ParseException ex3) {
				date = null;
			}
		}
		if (date != null) {
			waybacktime = Long.toString(date.getTime() / 1000);
			System.out.println("ok.(" + date.toString() + "):" + waybacktime);
		} else {
			try {
				long tmp_time = Long.parseLong(time);
				waybacktime = Long.toString(tmp_time);
				date = new Date(tmp_time * 1000);
				System.out.println("ok.(" + date.toString() + "):"
						+ waybacktime);
			} catch (NumberFormatException ex4) {
				System.out.println("ng.");
				System.out.println("Cannot parse wayback time.");
				return false;
			}
		}
		System.out.print("Getting wayback key...");
		String url = "http://www.nicovideo.jp/api/getwaybackkey?thread="
				+ ThreadID;
		String ret = "";
		try {
			HttpURLConnection con = (HttpURLConnection) (new URL(url))
					.openConnection(ConProxy);
			/* リクエストの設定 */
			con.setRequestMethod("GET");
			con.addRequestProperty("Cookie", Cookie);
			con.addRequestProperty("Connection", "close");
			con.setDoInput(true);
			con.connect();
			if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.println("Can't get WayBackKey:" + url);
				return false;
			}
			/* 戻り値の取得 */
			BufferedReader br = new BufferedReader(new InputStreamReader(con
					.getInputStream()));
			ret = br.readLine();
			br.close();
			con.disconnect();
		} catch (IOException ex1) {
			System.out.println("ng.");
			ex1.printStackTrace();
			return false;
		}
		int idx = 0;
		if ((idx = ret.indexOf(WAYBACKKEY_STR)) < 0) {
			System.out.println("ng.");
			System.out.println("Cannot find wayback key from response.");
			return false;
		}
		int end_idx = Math.max(ret.length(), ret.indexOf("&"));
		String waybackkey = ret.substring(idx + WAYBACKKEY_STR.length(),
				end_idx);
		if (waybackkey == null || waybackkey.equals("")) {
			System.out.println("ng.");
			System.out.println("Cannot get wayback key.");
			return false;
		}
		System.out.println("ok. key:" + waybackkey);
		WayBackTime = waybacktime;
		WayBackKey = waybackkey;
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
}
