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
					log.print(" Title not found.");
				log.println(" <" + Path.toUnixPath(titleHtml) + "> saved.");
			}
			log.println("ok.");
		} catch (IOException ex) {
			log.printStackTrace(ex);
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
			HttpURLConnection con = urlConnect(MsgUrl, "POST", Cookie, true, true, "keep-alive",true);
			OutputStream os = con.getOutputStream();
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
				log.println("ng.\nCannot parse time.\"" + time + "\"");
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
						log.printStackTrace(e);
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
			pw.printf("�^�C�g��:  %s\n", titleStr);
			pw.printf("��������F   %s\n", uploadComment);
			pw.printf("�T���l:    %s\n", thumbnail);
			pw.printf("���e��:   %s\n", postedAt);
			pw.printf("����(�b):  %s\n", timeLength);
			pw.printf("�Đ����F    %s\n", viewCount);
			pw.printf("�R�����g���F  %s\n", commentCount);
			pw.printf("�}�C���X�g���F %s\n", mylistCount);
			pw.printf("���e��:    '%s'\n", ownerName);
			pw.printf("�^�O:     %s\n", tags);
			pw.flush();
			pw.close();
		} catch (IOException e) {
			log.printStackTrace(e);
			return null;
		}
		return filePath;
	}
*/

	private String thumbInfoData;
	public Path getThumbInfoFile(String tag){
		final String THUMBINFO_URL = "http://ext.nicovideo.jp/api/getthumbinfo/";
		String url = THUMBINFO_URL + tag;
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
			if(s.indexOf("status=\"ok\"") < 0 && titleHtml!=null){
				// �\�Ȃ�thumbXml��titleHtml����\������
				//
				String html = Path.readAllText(titleHtml, encoding);
				if(html==null)
					return null;
				String text = html;
				// ����y�[�W��JSON�����o��
				text = getXmlElement1(text, "body");	//body
				if(text==null)
					return null;
				text = getXmlElement1(text, "div");
					//div id="watchAPIDataContainer" style="display:none"
				if(text==null)
					return null;
				String json_start = "{&quot;flashvars&quot;:";
				int start = text.indexOf(json_start);
				if(start < 0)
					return null;
				String json_end = "<";
				int end = (text+json_end).indexOf(json_end, start);	// end of JSON
				text = (text+json_end).substring(start, end);
				text = text.replace("&quot;", "\"");
				try {
					text = URLDecoder.decode(text, encoding);	// decode URLEncode
				} catch(Exception e){
					log.println("caught exception in json URLdecode");
				}
				//Json���
				Path file = new Path(titleHtml.getRelativePath()+"J.xml");
				Path.unescapeStoreXml(file, text, url);		//xml is property key:json val:JSON
				Properties prop = new Properties();
				prop.loadFromXML(new FileInputStream(file));		//read JSON xml
				text = prop.getProperty("json", "0");

				sb = new StringBuilder();
				sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
				sb.append("<nicovideo_thumb_response status=\"ok\">\n");
				sb.append("<thumb>\n");
				sb.append(makeNewXmlElement(s,"code"));
				sb.append("<video_id>"+tag+"</video_id>\n");
				sb.append("<title>"+title+"</title>\n");
				String description = getJsonValue(text,"description");
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
		}
	}

	public static String getXmlElement(String input, String key){
		Pattern p = Pattern.compile("<"+key+">(.*)</"+key+">",Pattern.DOTALL);
		Matcher m = p.matcher(input);
		if(m.find()){
			return m.group(1);
		}
		return null;
	}

	private static String makeNewXmlElement(String html, String key) {
		String val = getXmlElement(html, key);
		if(val==null)
			val = "";
		return "<"+key+">"+val+"</"+key+">\n";
	}

	public static String getJsonValue(String input, String key){
		Pattern p = Pattern.compile("\""+key+"\":\"?([^,}]+)\"?[,}]",Pattern.DOTALL);
		Matcher m = p.matcher(input);
		String val = "";
		if(m.find()){
			val = m.group(1).replace("\"", "");
			return val;
		}
		return null;
	}

	private static String makeNewJsonValue(String html, String key) {
		String val = getJsonValue(html, key);
		if(val==null)
			val = "";
		return "<"+key+">"+val+"</"+key+">\n";
	}

	public static String getXmlElement1(String xml, String key){
		Pattern p = Pattern.compile("<"+key+"[^>]*>(.*)</"+key,Pattern.DOTALL);
		Matcher m = p.matcher(xml);
		String dest = "";
		if(m.find()){
			dest = m.group(1);
			return dest;
		}
		return dest;
	}

	public static String getXmlElement2(String input, String key){
		Pattern p = Pattern.compile("<"+key+"[^>]*>([^<]*)</",Pattern.DOTALL);
		Matcher m = p.matcher(input);
		if(m.find())
			return m.group(1);
		return null;
	}

	public static String getXmlAttribute(String input, String atribname){
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
