/**
 * クッキー用ユーティリティ。
 * クッキーの取得、管理などを行う。基本的に全部staticメソッド。
 */
package saccubus.process.net;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import saccubus.info.NicoInfo;
import saccubus.util.SystemUtil;

/**
 * @author PSI
 *
 */
public class NicoCookie {
	private static String Cookie = null;
	private ConnectionMaker ConMaker;
	private NicoInfo NicoInfo;
	/**
	 * @param conMaker
	 */
	protected NicoCookie(NicoInfo nicoInfo,ConnectionMaker conMaker) {
		NicoInfo = nicoInfo;
		ConMaker = conMaker;
	}
	/**
	 * クッキーを取得する。
	 * @param last_failed
	 * @return
	 */
	protected String getCookie(boolean last_failed){
		if(!last_failed && Cookie != null){
			return Cookie;
		}
		//システム側のクッキーが使えないかどうかチェックしてみる。
		String cookie = SystemUtil.getCookie();
		if(checkLogin(cookie)){
			Cookie = cookie;
			return cookie;
		}
		//ログインしてクッキーを取得する。
		cookie = login();
		if(cookie == null){
			Cookie = null;
			return null;
		}
		int index = cookie.indexOf(";");
		//書式がおかしい
		if(index < 0){
			Cookie = null;
			return null;
		}
		//システム側に設定する。
		SystemUtil.setCookie(cookie);
		//生存時間などの情報は削除する。
		Cookie = cookie.substring(0, index);
		return Cookie;
	}
	/**
	 * クッキー情報を追加する。
	 * @param con
	 */
	protected void addCookie(Connection con){
		if(con == null){
			return;
		}
		String add = con.getHeaderInfo("Set-Cookie");
		if(add == null){
			return;
		}
		int index = add.indexOf(";");
		if(index < 0){
			return;
		}
		Cookie += "; ";
		Cookie += add.substring(0,index);
	}
	/**
	 * そのクッキーでログインできるかどうかをチェックする
	 * @param cookie
	 * @return
	 */
	private boolean checkLogin(String cookie){
		Connection con = ConMaker.makeConnection(false, true, NicoUtil.NICO_TOP_URL);
		String let = con.loadString();
		//ログインURLが存在しない＝ログインできている
		return let.indexOf(NicoUtil.NICO_LOGIN_URL) < 0;
	}
	/**
	 * ログインする
	 * @return
	 */
	private String login(){
		String login;
		try {
			StringBuffer sb = new StringBuffer(4096);
			sb.append("next_url=&");
			sb.append("mail=");
			sb.append(URLEncoder.encode(NicoInfo.getMailaddr(), "UTF-8"));
			sb.append("&password=");
			sb.append(URLEncoder.encode(NicoInfo.getPassword(), "UTF-8"));
			sb.append("&submit.x=103&submit.y=16");
			login = sb.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		Connection con = ConMaker.makeConnection(false, false, NicoUtil.NICO_LOGIN_URL,login);
		if(con.getStatus() == Connection.State.REDIRECTED){
			return  con.getHeaderInfo("Set-Cookie");
		}
		return null;
	}
}
