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

import javax.swing.JLabel;

import saccubus.ConvertingSetting;
import saccubus.net.BrowserInfo.BrowserCookieKind;
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
	private String userSession;
	private BrowserCookieKind browserKind;
	private Logger log;

	public Loader(ConvertingSetting setting, JLabel[] status3, Logger logger) {
		log = logger;
		this.setting = setting;
		this.status = status3[0];
		stopwatch = new Stopwatch(status3[1]);
	}

	void sendtext(String text){
		synchronized (status) {
			status.setText(text);
		}
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
		NicoClient client = getNicoClient(log);
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
		BrowserInfo browser = new BrowserInfo(log);
		userSession = browser.getUserSession(setting);
		browserKind = browser.getValidBrowser();
		if (browserKind == BrowserCookieKind.NONE){
			mailAddress = setting.getMailAddress();
			password = setting.getPassword();
			if (mailAddress == null || mailAddress.isEmpty()
				|| password == null || password.isEmpty()) {
				sendtext("メールアドレスかパスワードが空白です。");
				return false;
			}
		} else if (userSession.isEmpty()){
			sendtext("ブラウザ" + browserKind.getName() + "のセッション取得に失敗");
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
		if (browserKind != BrowserCookieKind.NONE){
			// セッション共有、ログイン済みのNicoClientをclientに返す
			client = new NicoClient(browserKind, userSession, proxy, proxyPort, stopwatch, log);
		} else {
			client = new NicoClient(mailAddress, password, proxy, proxyPort, stopwatch, log);
		}
		if (!client.isLoggedIn()) {
			sendtext("ログイン失敗 " + browserKind.getName() + " " + client.getExtraError());
			log.println("\nLogin failed.");
			return null;
		} else {
			sendtext("ログイン成功 " + browserKind.getName());
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
}
