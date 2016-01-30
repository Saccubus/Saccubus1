/**
 * <p>
 * �^�C�g��: ������΂�<br/>
 * ����: �j�R�j�R����̓�����R�����g���ŕۑ�<br/>
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

	public Loader(ConvertingSetting setting, JLabel[] status3) {
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
		NicoClient client = getNicoClient();
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
		BrowserInfo browser = new BrowserInfo();
		userSession = browser.getUserSession(setting);
		browserKind = browser.getValidBrowser();
		if (browserKind == BrowserCookieKind.NONE){
			mailAddress = setting.getMailAddress();
			password = setting.getPassword();
			if (mailAddress == null || mailAddress.isEmpty()
				|| password == null || password.isEmpty()) {
				sendtext("���[���A�h���X���p�X���[�h���󔒂ł��B");
				return false;
			}
		} else if (userSession.isEmpty()){
			sendtext("�u���E�U" + browserKind.getName() + "�̃Z�b�V�����擾�Ɏ��s");
			return false;
		}
		if (setting.useProxy()){
			proxy = setting.getProxy();
			proxyPort = setting.getProxyPort();
			if (   proxy == null || proxy.isEmpty()
				|| proxyPort < 0 || proxyPort > 65535   ){
				sendtext("�v���L�V�̐ݒ肪�s���ł��B");
				return false;
			}
		} else {
			proxy = null;
			proxyPort = -1;
		}
		return true;
	}

	private NicoClient getNicoClient(){
		sendtext("���O�C����");
		NicoClient client = null;
		if (browserKind != BrowserCookieKind.NONE){
			// �Z�b�V�������L�A���O�C���ς݂�NicoClient��client�ɕԂ�
			client = new NicoClient(browserKind, userSession, proxy, proxyPort, stopwatch);
		} else {
			client = new NicoClient(mailAddress, password, proxy, proxyPort, stopwatch);
		}
		if (!client.isLoggedIn()) {
			sendtext("���O�C�����s " + browserKind.getName() + " " + client.getExtraError());
			System.out.println("\nLogin failed.");
			return null;
		} else {
			sendtext("���O�C������ " + browserKind.getName());
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
			System.out.print("Loading...");
			HttpURLConnection con = client.urlConnectGET(url);
			if (con == null || con.getResponseCode() != HttpURLConnection.HTTP_OK){
				System.out.println("ng.\nCan't get URL Object:" + url);
				return false;
			}
			if (file.canRead() && file.delete()) {
				System.out.print("previous file " + file.getRelativePath() + " deleted...");
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
				System.out.println();
				new NicoMap().putConnection(con, System.out);
			}
			while ((ret = br.readLine()) != null) {
				pw.println(ret);
			}
			System.out.println("ok.");
			br.close();
			pw.flush();
			pw.close();
			con.disconnect();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
