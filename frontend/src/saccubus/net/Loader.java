/**
 * <p>
 * タイトル: さきゅばす<br/>
 * 説明: ニコニコ動画の動画をコメントつきで保存<br/>
 * </p>
 */
package saccubus.net;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import saccubus.ConvertingSetting;
import saccubus.util.Logger;
import saccubus.util.Stopwatch;

/**
 * @author orz<br/>
 * @version 1.30
 */
public class Loader {

	private ConvertingSetting setting;
	private final JLabel status;
	private final Stopwatch stopwatch;
	private String mailAddress;
	private String password;
	private String proxy;
	private int proxyPort;
	//private String userSession;
	//private BrowserCookieKind browserKind;
	private BrowserInfo browserInfo;
	private Logger log;
	private boolean Debug = false;
	private NicoClient client;
	private boolean isHtml5;

	public Loader(ConvertingSetting setting, JLabel[] status3, Logger logger, boolean is_html5) {
		log = logger;
		this.setting = setting;
		this.status = status3[0];
		stopwatch = Stopwatch.create(status3[2]);
		Debug = setting.isDebugNicovideo();
		isHtml5 = is_html5;
	}

	void sendtext(final String text){
		if(SwingUtilities.isEventDispatchThread()){
			status.setText(text);
		}else
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				status.setText(text);
			}
		});
	}

	/**
	 * Load url to file, eather use browser session or login by mail-address and password.
	 * @param url : String
	 * @param file : Path
	 * @return true if success.
	 */
	public boolean load(String url, Path file) {
		if (!check(setting)){
			return false;
		}
		client = getNicoClient(log);
		if(client == null){
			return false;
		}
		return loadToFile(client, url, file);
	}

	/**
	 * Check setting being OK and browser session.
	 * @param setting : ConvertingSetting
	 * @return true if OK.
	 */
	private boolean check(ConvertingSetting setting) {
		browserInfo = new BrowserInfo(log);
		browserInfo.checkUserSession(setting);
		//browserKind = myBrowser.getValidBrowser();
		if (!BrowserInfo.isBrowser(setting)){
			mailAddress = setting.getMailAddress();
			password = setting.getPassword();
			if (mailAddress == null || mailAddress.isEmpty()
				|| password == null || password.isEmpty()) {
				sendtext("メールアドレスかパスワードが空白です。");
				return false;
			}
		} else if (!browserInfo.isValid()){
			sendtext("ブラウザ" + browserInfo.getName() + "のセッション取得に失敗");
			return false;
		}
		if (setting.useProxy()){
			proxy = setting.getProxy();
			proxyPort = setting.getProxyPort();
			if (   proxy == null || proxy.isEmpty()
				|| proxyPort < 0 || proxyPort > 65535   ){
				sendtext("プロキシの設定が不正です。");
				return false;
			}
		} else {
			proxy = null;
			proxyPort = -1;
		}
		return true;
	}

	private NicoClient getNicoClient(Logger log){
		sendtext("ログイン中");
		NicoClient client = null;
		isHtml5 = setting.isHtml5();
		if (browserInfo.isValid()){
			// セッション共有、ログイン済みのNicoClientをclientに返す
			client = new NicoClient(browserInfo, proxy, proxyPort, stopwatch, log, isHtml5);
		} else {
			client = new NicoClient(mailAddress, password, browserInfo, proxy, proxyPort, stopwatch, log, isHtml5);
		}
		if (!client.isLoggedIn()) {
			sendtext("ログイン失敗 " + browserInfo.getName() + " " + client.getExtraError());
			log.println("\nLogin failed.");
			return null;
		} else {
			sendtext("ログイン成功 " + browserInfo.getName());
			return client;
		}
	}

	/**
	 * Load url to file using client, which was logged in.
	 * @param client : NicoClient
	 * @param url : String
	 * @param file : Path
	 */
	private boolean loadToFile(NicoClient client, String url, Path file) {
		try {
			log.print("Loading...");
			HttpURLConnection con = client.urlConnectGET(url);
			if (con == null || con.getResponseCode() != HttpURLConnection.HTTP_OK){
				log.println("ng.\nCan't get URL Object:" + url);
				return false;
			}
			if (file.canRead() && file.delete()) {
				log.print("previous file " + file.getRelativePath() + " deleted...");
			}
			String encoding = con.getContentEncoding();
			if (encoding == null){
				encoding = "UTF-8";
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(con
					.getInputStream(), encoding));
			String ret;
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(file), encoding));			if(client.Debug){
				log.println();
				new NicoMap().putConnection(con, log);
			}
			while ((ret = br.readLine()) != null) {
				pw.println(ret);
			}
			log.println("ok.");
			br.close();
			pw.flush();
			pw.close();
			con.disconnect();
			return true;
		} catch (IOException e) {
			log.printStackTrace(e);
		}
		return false;
	}

	public NicoMap loadHttpsUrl(String url){
		NicoMap map = new NicoMap();
		try {
			debug("\n▼HTTPS<" + url + ">\n");
			HttpURLConnection con = (HttpsURLConnection)(new URL(url)).openConnection(Proxy.NO_PROXY);
			con.setDoInput(true);
			con.setInstanceFollowRedirects(false);
			con.setRequestMethod("GET");
			con.addRequestProperty("Connection", "close");
			debug("▼Connect: GET,DoInput,Connection close\n");
			con.connect();
			int code = con.getResponseCode();
			String mes = con.getResponseMessage();
			debug("▼Response:" + Integer.toString(code) + " " + mes + "\n");
			if (code < HttpURLConnection.HTTP_OK || code >= HttpURLConnection.HTTP_BAD_REQUEST) { // must 200 <= <400
				log.println("Can't access:" + mes + " to <"+url+">");
				map.put("_RESPONSE_", mes);
				return map;
			}
			map.putConnection(con, Debug?log:null);
			if(code >= HttpURLConnection.HTTP_MULT_CHOICE){	// code >= 300
				log.println("locaciton is "+map.get("location"));
				map.put("_RESPONSE_", mes);
				return map;
			}
			StringBuffer sb = new StringBuffer();
			BufferedReader br = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			String ret;
			while((ret = br.readLine())!=null){
				sb.append(ret+"\n");
				debug(" ▼"+ret+"\n");
			}
			map.put("_RESPONSE_", sb.substring(0));
			br.close();
			con.disconnect();
		} catch (IOException ex) {
			log.printStackTrace(ex);
		}
		return map;
	}
	private void debug(String messege){
		if (Debug){
			log.print(messege);
		}
	}

	public boolean isLoggedIn() {
		if(client==null)
			return false;
		return client.isLoggedIn();
	}

	public boolean getIsHtml5() {
		return isHtml5;
	}
}
