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
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.JLabel;

import saccubus.ConvertStopFlag;
import saccubus.ConvertWorker;
import saccubus.WayBackDate;
import saccubus.conv.ChatSave;
import saccubus.json.Mson;
import saccubus.net.BrowserInfo.BrowserCookieKind;
import saccubus.util.Logger;
import saccubus.util.Stopwatch;

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
public class NicoClient {
	private final String User;
	private final String Pass;
	private boolean Logged_in = false;
	private final Proxy ConProxy;
	boolean Debug = false;
	private final NicoMap nicomap;
	private Stopwatch Stopwatch;
	private Path titleHtml = null;
	private Logger log;

	public static final String DEBUG_PROXY = "debug";	// debug paramerter end with '/'
	static final String S_QUOTE2 = "\"";
	static final char C_QUOTE2 = '"';
	static final String S_ESCAPE = "\\";
	static final char C_ESCAPE = '\\';
	static final String JSON_START = "{&quot;flashvars&quot;:";

	/**
	 * �u���E�U���L���Ȃ��Ń��O�C��
	 * @param user
	 * @param pass
	 * @param proxy
	 * @param proxy_port
	 */
	public NicoClient(final String user, final String pass,
			final String proxy, final int proxy_port, final Stopwatch stopwatch,
			Logger logger) {
		log = logger;
		User = user;
		Pass = pass;
		Stopwatch = stopwatch;
		nicomap = new NicoMap();
		ConProxy = conProxy(proxy, proxy_port);
		// ���O�C��
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
	 * �u���E�U���L��ԂŃj�R�j�R����ɃA�N�Z�X����<br/>
	 * ���Ƀ��O�C�����Ă��Ȃ���΂Ȃ�Ȃ�<br/>
	 * ���[�U�[�Z�b�V����(Cookie���)���u���E�U����擾����̂�<br/>
	 * ���[���A�h���X�A�p�X���[�h�͕s��
	 * @param browser_kind : �u���E�U�̎�� : int
	 * @param user_session : String
	 * @param proxy : String
	 * @param proxy_port : int
	 */
	public NicoClient(final BrowserCookieKind browser_kind, final String user_session,
			final String proxy, final int proxy_port, final Stopwatch stopwatch,
			Logger logger) {
		log = logger;
		User = "";
		Pass = "";
		Stopwatch = stopwatch;
		nicomap = new NicoMap();
		ConProxy = conProxy(proxy, proxy_port);
		if (user_session == null || user_session.isEmpty()){
			log.println("Invalid user session" + browser_kind.toString());
			setExtraError("�Z�b�V�������擾�o���܂���");
			Logged_in = false;
		} else {
			String[] sessions = user_session.split(" ");	// "user_session_12345..."+" "+...
			for(String session: sessions){
				if (session != null && !session.isEmpty()){
					String this_session = "user_session=" + session;
					Cookie = new NicoCookie();
					Cookie.setSession(this_session);
					if(loginCheck()){
						Logged_in = true;	// ���O�C���ς݂̃n�Y
						return;
					}
					Cookie = new NicoCookie();
					log.println("Fault user session" + browser_kind.toString());
					setExtraError("�Z�b�V�����������ł�");
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
			debug("\n��URL<" + url + ">\n");
			HttpURLConnection con = (HttpURLConnection) (new URL(url))
				.openConnection(ConProxy);
			/* ���N�G�X�g�̐ݒ� */
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
	//Cookie: __utmc=8292653; nicosid=1440976771.UserID����; nicorepo_filter=all;
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
		//	/* gzip deflate����t�\�ɂ�����R�����g�擾�������Ȃ�H ���ۂ�deflate�ŗ��邩�͊m���߂ĂȂ� */
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

			debug("��Connect: " + method + ","
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
				debug("��Response:" + Integer.toString(code) + " " + con.getResponseMessage() + "\n");
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
			setExtraError("�R�l�N�V�����G���[�B�v���L�V���s���H");
		} catch(IllegalStateException ex){
			if(Debug)
				log.printStackTrace(ex);
			log.println("Connection error. Check proxy ?");
			setExtraError("�R�l�N�V�����G���[�B�v���L�V���s���H");
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
			debug("��readLine:" + ret+ "\n");
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
	//	debug("��<NicoCookie><" + cookie.toString() + ">\n");
		return cookie;
	}

	private boolean login() {
		try {
			log.print("Trying login...");
			String url = "https://account.nicovideo.jp/api/v1/login?show_button_twitter=1&site=niconico&show_button_facebook=1";
			debug("\n��HTTPS<" + url + ">\n");
			HttpURLConnection con = (HttpsURLConnection) (new URL(url))
				.openConnection(ConProxy);
			/* �o�͂̂� */
			con.setDoOutput(true);
			HttpURLConnection.setFollowRedirects(false);
			con.setInstanceFollowRedirects(false);
			con.setRequestMethod("POST");
			con.addRequestProperty("Connection", "close");
			debug("��Connect: POST,DoOutput,Connection close\n");
			connect(con);
			StringBuffer sb = new StringBuffer(4096);
			sb.append("next_url=/&");
			sb.append("mail_tel=");
			sb.append(URLEncoder.encode(User, "Shift_JIS"));
			sb.append("&password=");
			sb.append(URLEncoder.encode(Pass, "Shift_JIS"));
			sb.append("&submit.x=103&submit.y=16");
			String sbstr = sb.toString();
			debug("��write:" + sbstr + "\n");
			OutputStream os = con.getOutputStream();
			os.write(sbstr.getBytes());
			os.flush();
			os.close();
			Stopwatch.show();
			int code = con.getResponseCode();
			String mes = con.getResponseMessage();
			debug("��Response:" + Integer.toString(code) + " " + mes + "\n");
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
		//���̎Q�Ƃ̃p�[�X
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
		//�Ō�ɒǉ�
		sb.append(str,old_index,str.length());
		str = sb.toString();
		//MS-DOS�V�X�e��(ffmpeg.exe)�ň�����`��(UTF-8�̂܂�)
		str = toSafeString(str, "MS932");
		//�t�@�C���V�X�e���ň�����`��
		str = str.replace('/', '�^');
		str = str.replace('\\', '��');
		str = str.replace('?', '�H');
		str = str.replace('*', '��');
		str = str.replace(':', '�F');
		str = str.replace('|', '�b');
		str = str.replace('\"', '�h');
		str = str.replace('<', '��');
		str = str.replace('>', '��');
		str = str.replace('.', '�D');
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
	//RC2�ɂȂ��ă^�C�g�����ύX�A�g��Ȃ��Ȃ����B
	//private static final String TITLE_PARSE_STR_END = "</title>";
	private static final String TITLE_END = "�]";
	private static final String TITLE_END2 = "- �j�R�j�R����";
	private static final String TITLE_ZERO_DIV = "id=\"videoHeaderDetail\"";
	private static final String TITLE_ZERO_DUMMY = "<title>�j�R�j�R����:Zero</title>";
	private static final String TITLE_GINZA_DIV = "DataContainer\"";
	private static final String TITLE_GINZA_DUMMY = "<title>�j�R�j�R����:GINZA</title>";
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
		String url = "http://www.nicovideo.jp/watch/" + tag + watchInfo;
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
						String re = "[�]-]\\h*�j�R�j�R����\\h*(:\\h*[\\w]+)?$";
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
			//Json���
			if(!ss.contains(JSON_START)){
				log.println("����y�[�W�����s�H");
			}else{
				extractWatchApiJson(ss, encoding, url);
				log.println("ok.");
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
	private String ownerFilter = "";			// video owner filter�ireplace�j
	public boolean getVideoInfo(String tag, String watchInfo, String time, boolean saveWatchPage) {
		if (!getVideoHistoryAndTitle(tag, watchInfo, saveWatchPage)) {
			return false;
		}
		try {
			String url = "http://flapi.nicovideo.jp/api/getflv/" + tag;
			if (!getWatchThread().isEmpty()){
				url = "http://flapi.nicovideo.jp/api/getflv/" + getWatchThread();
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
			economy  = VideoUrl.toLowerCase().contains("low");
			log.println("ok.");
			if(severIsDmc()){
				log.println("Video:<" + apiSessionUrl + ">;");
			}
			log.println("Video:<" + VideoUrl + ">; Comment:<" + MsgUrl
					+ (NeedsKey ? ">; needs_key=1" : ">"));
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

	public int getDmcVideoLength(){
		return dmcVideoLength;
	}
	public File getVideo(File file, final JLabel status, final ConvertStopFlag flag,
			boolean renameMp4) {
		try {
			log.print("Getting video size...");
			if (VideoUrl == null) {
				log.println("Video url is not detected.");
				return null;
			}
			if (file.canRead() && file.delete()) { // �t�@�C�������łɑ��݂���Ȃ�폜����B
				log.print("previous video deleted...");
			}
			HttpURLConnection con = urlConnect(VideoUrl, "GET", Cookie, true, false, null);
			if (con == null || con.getResponseCode() != HttpURLConnection.HTTP_OK) {
				log.println("Can't get video:" + VideoUrl);
				String ecode = getExtraError();
				if(ecode==null){

				}
				else if (ecode.contains("403")){
					setExtraError("=�s�K�؂ȓ���̉\���BreadmeNew.txt�Q��");
				}
				else if(ecode.contains("50")){
					// 5�b�ҋ@
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

			if(ContentType==null){
				ContentType = con.getHeaderField("Content-Type");
				if(ContentType == null) ContentType = "";
			}
			ContentDisp = con.getHeaderField("Content-Disposition");
			int max_size = con.getContentLength();	// -1 when invalid
			log.print("size="+(max_size/1000)+"Kbytes");
			log.println(", type=" + ContentType + ", " + ContentDisp);
			log.print("Downloading video...");
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
				sendStatus(status, "����", max_size, size);
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
			debugsOut("\n��read+write statistics(bytes) ");
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
		//	debug("��read+write statistics(bytes) ");
		//	debugsOut();
		}
		return null;
	}
	public File getVideoDmc(File video, JLabel status, ConvertStopFlag flag, boolean renameMp4) {
		FileOutputStream fos = null;
		OutputStream os = null;
		HttpURLConnection con = null;
		BufferedReader br = null;
		PrintWriter pw = null;
		InputStream is = null;
		try {
			log.print("Getting video size...");
			if (apiSessionUrl == null || apiSessionUrl.isEmpty()) {
				log.println("Video url(DMC) is not detected.");
				return null;
			}
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
			url = host_url + "/crossdomain.xml";
			con = urlConnectGET(url);
			if (con == null || con.getResponseCode() != HttpURLConnection.HTTP_OK){
				log.println("ng.\nCan't crossdomain.xml:" + url);
				return null;
			}
			String encoding = con.getContentEncoding();
			if (encoding == null){
				encoding = "UTF-8";
			}
			br = new BufferedReader(new InputStreamReader(con.getInputStream(), encoding));
			log.print("ok.\nSaving crossdomain.xml...");
			String ret;
			StringBuilder sb = new StringBuilder();
			while ((ret = br.readLine()) != null) {
				Stopwatch.show();
				sb.append(ret + "\n");
			}
			br.close();
			con.disconnect();
			File crossdomain = Path.mkTemp(videoTag+"_crossdomain.xml");
			pw = new PrintWriter(crossdomain, encoding);
			pw.write(sb.substring(0));
			pw.flush();
			pw.close();
			//
			//	POST /api/sessions?suppress_response_codes=true&_format=xml HTTP/1.1
			//	Host: api.dmc.nico:2805
			//	User-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0
			//	Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
			//	Accept-Language: ja,en-US;q=0.7,en;q=0.3
			//	Accept-Encoding: gzip, deflate
			//	DNT: 1
			//	Connection: keep-alive
			//  Content-type: application/x-www-form-urlencoded
			//
			url = apiSessionUrl + "?suppress_response_codes=true&_format=xml";
			debug("\n��URL<" + url + ">\n");
			//	con = urlConnect(url, "POST", null, true, true, "keep-alive", false);
			con = (HttpURLConnection) (new URL(url)).openConnection(ConProxy);
			con.setDoOutput(true);
			HttpURLConnection.setFollowRedirects(false);
			con.setInstanceFollowRedirects(false);
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", "Java/Saccubus-1.65.xx");
			con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			con.setRequestProperty("Accept-Language", "ja,en-US;q=0.7,en;q=0.3");
			con.setRequestProperty("Accept-Encoding", "gzip, deflate");
		//	con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
			con.addRequestProperty("DNT", "1");
			con.addRequestProperty("Connection", "keep-alive");
			debug("��Connect: POST,DoOutput,Connection keep-alive\n");
			connect(con);
			String poststr = sessionData;
			debug("��write:" + poststr + "\n");
			os = con.getOutputStream();
			os.write(poststr.getBytes());
			os.flush();
			os.close();
			Stopwatch.show();
			int code = con.getResponseCode();
			String mes = con.getResponseMessage();
			debug("��Response:" + Integer.toString(code) + " " + mes + "\n");
			if (code < HttpURLConnection.HTTP_OK || code >= HttpURLConnection.HTTP_BAD_REQUEST) { // must 200 <= <400
				log.println("Can't get DMC session:" + mes);
				return null;
			}
			String responseXmlData = readConnection(con);
			// save all responce
			Path responseXml = Path.mkTemp(videoTag+"_DmcResponse.xml");
			pw = new PrintWriter(responseXml);
			pw.write(responseXmlData);
			pw.flush();
			pw.close();
			debug("\n��session response:\n"+responseXmlData);
			debug("Refer dmc responceXml <"+responseXml.getPath()+">");
			String contentUri = getXmlElement(responseXmlData, "content_uri");
			if(contentUri==null){
				String resStatus = getXmlElement(responseXmlData, "object");
				log.println("\nDmcHttpResponse: "+resStatus);
				return null;
			}
		//	GET content_uri
			if (video.canRead() && video.delete()) { // �t�@�C�������łɑ��݂���Ȃ�폜����B
				log.print("previous video("+video.getPath()+") deleted...");
			}
			url = contentUri + "&starti=0&start=0";
			con = urlConnect(url, "GET", null, true, false, null);
			if (con == null || con.getResponseCode() != HttpURLConnection.HTTP_OK) {
				log.println("Can't get video(dmc):" + url);
				String ecode = getExtraError();
				if(ecode==null){
				}
				else if (ecode.contains("403")){
					setExtraError("=�s�K�؂ȓ���̉\���BreadmeNew.txt�Q��");
				}
				else if(ecode.contains("50")){
					// 5�b�ҋ@
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// log.printStackTrace(e);
					}
				}
				return null;
			}
			is = con.getInputStream();
			new NicoMap().putConnection(con, (Debug? log:null));
			if(ContentType==null){
				ContentType = con.getHeaderField("Content-Type");
				if(ContentType == null) ContentType = "";
			}
			ContentDisp = con.getHeaderField("Content-Disposition");
			int max_size = con.getContentLength();	// -1 when invalid
			if(max_size > 0)
				dmcVideoLength = max_size;
			log.print("size="+(max_size/1000)+"Kbytes");
			log.println(", type=" + ContentType + ", " + ContentDisp);
			log.print("Downloading video...");
			if(renameMp4 && ContentType.contains("mp4")){
				String filepath = video.getPath();
				int index = filepath.lastIndexOf(".");
				if(filepath.lastIndexOf(File.separator) < index){
					filepath = filepath.substring(0, index) + ".mp4";
				}
				video = new File(filepath);
			}
			os = new FileOutputStream(video);
			int size = 0;
			int read = 0;
			debugsInit();
			while ((read = is.read(buf, 0, buf.length)) > 0) {
				debugsAdd(read);
				size += read;
				os.write(buf, 0, read);
				sendStatus(status, "����", max_size, size);
				Stopwatch.show();
				if (flag.needStop()) {
					log.println("\nStopped.");
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
			debugsOut("\n��read+write statistics(bytes) ");
			if(size < max_size){
				log.println("\nDownload stopped less than max_size. "+size+"<"+max_size);
				Stopwatch.show();
				is.close();
				os.flush();
				os.close();
				con.disconnect();
				return null;
			}
			log.println("ok.");
			is.close();
			os.flush();
			os.close();
			con.disconnect();
			return video;
		} catch (FileNotFoundException ex) {
			log.printStackTrace(ex);
		} catch (IOException ex) {
			log.printStackTrace(ex);
		} finally{
			try {
				if(fos!=null) fos.close();
			}catch(IOException e){}
			try {
				if(os!=null) os.close();
			}catch(IOException e){}
			try {
				if(br!=null) br.close();
			}catch(IOException e){}
			if(pw!=null) pw.close();
			try {
				if(is!=null) is.close();
			}catch(IOException e){}
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
	// 2010�N12��22�� 18:00 ����̃R�����g�������ɂ����Ȃ�Ή��ɂ���
	private  enum CommentType {
		USER{
			@Override
			public String dlmsg(){ return "�R�����g"; }
		},
		OWNER{
			@Override
			public String dlmsg(){ return "���e�҃R�����g"; }
		},
		OPTIONAL{
			@Override
			public String dlmsg(){ return "�I�v�V���i���X���b�h"; }
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
	 	NeedsKey = false;
	 	Official = "";
		// ���̌��OwnerComment���擾����ƃ��[�U�[����̓��e�҃R�����g���擾�����B
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
		log.print("Downloading " + commentType.toString().toLowerCase()
				+" comment, size:" + back_comment + "...");
		//String official = "";	/* ��������p��key�ǉ� */
		if(NeedsKey && Official.isEmpty()){
			if((force184 == null || threadKey == null)
					&& !getOfficialOption(ThreadID)) {
				return null;
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
			if (file.canRead()){
				if(isAppend && useNewComment){
					if(commentType != CommentType.OWNER)
						lastNo = ConvertWorker.getNoUserLastChat(file);
				}else{
					if (file.delete()) {	//	�t�@�C�������łɑ��݂���Ȃ�폜����B
						log.print("previous " + commentType.toString().toLowerCase() + " comment deleted...");
					}
				}
			}
			fos = new FileOutputStream(file, isAppend);
			con = urlConnect(MsgUrl, "POST", Cookie, true, true, "keep-alive",true);
			os = con.getOutputStream();
			/*
			 * ���e�҃R�����g��2006version���g�p����炵���B�u���񂫂�΂�1.7.0�v
			 * �ߋ����O�V�\���A�`�����l���{�R�~���j�e�B�V�\���B�ucoroid�@���񂫂�΂�1.7.2�v
			 * �V�R�����g�\���Ƌ��\����I���\�ɂ���B
			 * ����ł�2010�N12��22�� 18:00�Ȍ�͐V�\���ɂ���B
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
			debug("\n��write:" + req + "\n");
			os.write(req.getBytes());
			os.flush();
			os.close();
			debug("��Response:" + Integer.toString(con.getResponseCode()) + " " + con.getResponseMessage() + "\n");
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
				sendStatus(status, commentType.dlmsg(), max_size, size);
				Stopwatch.show();
				if (flag.needStop()) {
					log.println("\nStopped.");
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
			debugsOut("��read+write statistics(bytes) ");
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
				setExtraError("�T�[�o�[����ؒf����܂����B�^�C���A�E�g�H");
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

	private boolean getOfficialOption(String threadId) {
		String url = "http://flapi.nicovideo.jp/api/getthreadkey?thread="
			+threadId;
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
				setExtraError("�ߋ����O�w�蕶���񂪈Ⴂ�܂�");
				return false;
			}
			String waybacktime = wayback.getWayBackTime();
			log.println("ok. [" + wayback.format() + "]: " + waybacktime);
			log.print("Getting wayback key...");
			String url = "http://flapi.nicovideo.jp/api/getwaybackkey?thread="
					+ ThreadID;
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
					setExtraError("��ʉ���͉ߋ����O�s�ł�");
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
		String url = "http://www.nicovideo.jp/";
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

		debug("\n��Now Cookie is<" + Cookie.toString() + ">\n");
		log.println("ok.");
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
		log.print(header);
		if(dsCount==0){
			log.println("Count 0");
		} else {
			log.print("Count "+dsCount+", Min "+dsMin+", Max "+dsMax);
			log.println(", Sum "+dsSum+", Avg "+dsSum/dsCount);
		}
	}

	/*
	 * msg = "����" �܂��� "�R�����g" �܂��� "���e�҃R�����g"
	 */
	private void sendStatus(JLabel status, String msg,
			int max_size, int size){
		if (max_size > 0) {
			String per = Double.toString((((double) size) * 100)
					/ max_size);
			per = per.substring(0, Math.min(per.indexOf(".") + 3, per
					.length()));
			synchronized (status) {
				status.setText(msg + "�_�E�����[�h�F" + per + "�p�[�Z���g����");
			}
		} else {
			synchronized (status) {
				status.setText(msg + "�_�E�����[�h���F" + Integer.toString(size >> 10)
						+ "kbytes�_�E�����[�h");
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
	public boolean severIsDmc(){
		return "1".equals(isDmc);
	}
	public Path getThumbInfoFile(String tag){
		final String THUMBINFO_URL = "http://ext.nicovideo.jp/api/getthumbinfo/";
		String url = THUMBINFO_URL + tag;
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
				if(html==null)
					return null;
				extractWatchApiJson(html, encoding, url);
			}
			if(s.indexOf("status=\"ok\"") < 0 && titleHtml!=null){
				// �\�Ȃ�thumbXml��titleHtml����\������
				sb = new StringBuilder();
				sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
				sb.append("<nicovideo_thumb_response status=\"ok\">\n");
				sb.append("<thumb>\n");
				sb.append(makeNewXmlElement(s,"code"));
				sb.append("<video_id>"+tag+"</video_id>\n");
				sb.append("<title>"+title+"</title>\n");
				String text = getWatchApiJson();
				String description = getJsonValue(text,"description");
				if(description==null)
					description = "";
				description = description.replace("&quot;", "�h")
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
				if(altTag==null)
					altTag = getJsonValue(text, "watch_url");
				if(altTag==null)
					altTag = tag;
				sb.append("<watch_url>http://www.nicovideo.jp/watch/"+altTag+"</watch_url>\n");
				sb.append("<thumb_type>video</thumb_type>\n");
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
			return thumbXml;
		} catch (IOException ex) {
			log.printStackTrace(ex);
			return null;
		} catch (Exception e) {
			log.printStackTrace(e);
			return null;
		}
	}

	private void extractWatchApiJson(String html, String encoding, String url){
		Path file = getWatchApiData(html, encoding, url);
		if(file==null) return;
		extractJson(file, encoding);
	}
	private String getWatchApiJson(){
		if(watchApiJson==null){
			String encoding = "UTF-8";
			if(titleHtml==null)
				return null;
			String html = Path.readAllText(titleHtml, encoding);
			if(html==null)
				return null;
			extractWatchApiJson(html, encoding, "getWatchApiJson");
		}
		return watchApiJson;
	}
	private String extractJson(Path xml, String encoding) {
		//Json���
		Properties prop = new Properties();
		try {
			prop.loadFromXML(new FileInputStream(xml));
		} catch (IOException e) {
			log.printStackTrace(e);
		}		//read JSON xml
		if(watchApiJson==null){
			watchApiJson = prop.getProperty("json", "0");
			debug("\n��watchAPIData_JSON:\n "+watchApiJson);
			if(flvInfo==null){
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
				debug("\n��flvInfo:\n "+flvInfo);
				debug("\n��flvInfos:\n [\n "+flvInfoArrays);
				debug("\n��]\n");
			}
			isDmc = getJsonValue(watchApiJson, "isDmc");
			log.println("\nisDmc: "+isDmc);
			if(isDmc!=null && isDmc.equals("1")){
				if(dmcInfo==null){
					dmcInfo = getJsonValue(watchApiJson, "dmcInfo");
					String s = dmcInfo;
					s = unquote(s);
					StringBuffer sb = new StringBuffer();
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
					debug("\n��dmcInfo:\n "+dmcInfo);
					debug("\n��dmcInfos:\n "+dmcInfoArrays);
					if(dmcInfoDec!=null){
						dmcToken = getJsonValue(dmcInfoDec, "token");
						debug("\n��dmcToken:\n "+dmcToken);
						s = dmcToken.replace("\\/", "/").replace("\\\"", S_QUOTE2).replace("\\\\", S_ESCAPE);
						dmcTokenUnEscape = s;
						log.print("\ndmcTokenUnEscape:\n "+dmcTokenUnEscape);
						sessionApi = getJsonValue(dmcInfoDec, "session_api");
						debug("\n��session_api:\n "+sessionApi);
						videos = getJsonValue(sessionApi, "videos");
						log.print("\nvideos:\n "+videos);
						audios = getJsonValue(sessionApi, "audios");
						log.print("\naudios:\n "+audios);
						apiUrls = getJsonValue(sessionApi, "api_urls").trim();
						if(apiUrls.startsWith("[") && apiUrls.endsWith("]")){
							apiUrls = apiUrls.substring(1, apiUrls.length()-1);
						}
						if(!apiUrls.contains(",")){
							apiSessionUrl = unquote(apiUrls);
						}
						debug("\n��apiUrls:\n "+apiUrls);
						debug("\n��apiSessionUrl:\n "+apiSessionUrl);
						player_id = getJsonValue(sessionApi, "player_id");
						debug("\n��player_id:\n "+player_id);
						sessionXml = Path.mkTemp(videoTag+"_session.xml");
						sessionData = makeSessionXml(sessionXml, sessionApi);
						log.println("\nsessionXML save to "+sessionXml.getPath());
					}
				}
			}
		}
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

	private String makeSessionXml(Path xml, String json) {
		StringBuilder sb = new StringBuilder();
		sb.append("<session>\n");
		sb.append("  "+makeNewJsonValue(json, "recipe_id"));
		sb.append("  <content_id>out1</content_id>\n");
		sb.append("  <content_type>movie</content_type>\n");
		sb.append("  <protocol>\n");
		sb.append("    <name>http</name>\n");
		sb.append("    <parameters>\n");
		sb.append("      <http_parameters>\n");
		sb.append("        <method>GET</method>\n");
		sb.append("        <parameters>\n");
		sb.append("          <http_output_download_parameters>\n");
		sb.append("            <file_extension>flv</file_extension>\n");
		sb.append("          </http_output_download_parameters>\n");
		sb.append("        </parameters>\n");
		sb.append("      </http_parameters>\n");
		sb.append("    </parameters>\n");
		sb.append("  </protocol>\n");
		sb.append("  <priority>0.8</priority>\n");
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
		// ����y�[�W��JSON�����o��
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
		// URLDecode���Ȃ�
		Path file = Path.mkTemp(videoTag+"_watchJ.xml");
		Path.unescapeStoreXml(file, text, comment);		//xml is property key:json val:JSON
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
			if(Debug)
				Logger.MainLog.printStackTrace(e);
			r = getJsonValue1(input, key);
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
			return r;	// �n�b�V�� {x:v, y:u}
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
			return r;	// �z�� [X,Y,Z]
		}
		if(c2==C_QUOTE2){
			int i = index2+1;
			while(i < input.length()){
				char ce = input.charAt(i++);
				if(ce==C_QUOTE2) break;
				if(ce==C_ESCAPE) i++;
			}
			String r = (input + S_QUOTE2).substring(index2, i);
			return r;	// ������ "ABC"
		}
		// �L�[���[�h
		if(tail.startsWith("null")){
			return "null";
		}
		if(tail.startsWith("true")){
			return "true";
		}
		if(tail.startsWith("false")){
			return "false";
		}
		// else ���̕��� {
		if('0' <= c2 && c2 <= '9') {
			int i = index2 + 1;
			while(i < input.length()){
				char ce = input.charAt(i);
				if(ce!='.' && (ce < '0' || ce > '9')) break;
				i++;
			}
			String r = input.substring(index2, i);
			return r;	// ����
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
		final String THUMBUSER_URL = "http://ext.nicovideo.jp/thumb_user/";
		String url = THUMBUSER_URL + userID;
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
		final String USER_URL = "http://www.nicovideo.jp/user/";
		String url = USER_URL + userID;
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

}
