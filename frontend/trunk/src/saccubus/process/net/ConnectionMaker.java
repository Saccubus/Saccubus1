/**
 * 
 */
package saccubus.process.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import saccubus.info.NetworkInfo;
import saccubus.info.NicoInfo;

/**
 * @author PSI
 *
 */
public class ConnectionMaker {
	private Proxy Proxy;
	private NicoCookie Cookie;
	/**
	 * @param netInfo
	 */
	public ConnectionMaker(NicoInfo nicoInfo,NetworkInfo netInfo) {
		Proxy = new Proxy(netInfo);
		Cookie = new NicoCookie(nicoInfo,this);
	}
	/**
	 * ポストを利用したコネクションをはる
	 * @param url
	 * @param post
	 * @return
	 */
	protected Connection makeConnection(boolean last_failed, String url,String post){
		return makeConnection(last_failed, true,true,url,post);
	}
	/**
	 * ポストを利用したクッキーを用いないコネクションを張る
	 * @param last_failed
	 * @param follow_redirect
	 * @param url
	 * @param post
	 * @return
	 */
	protected Connection makeConnection(boolean last_failed, boolean follow_redirect,String url,String post){
		return makeConnection(last_failed, false, follow_redirect,url,post);
	}
	/**
	 * ポストを利用したコネクションをはる
	 * @param use_cookie
	 * @param url
	 * @param post
	 * @return
	 */
	protected Connection makeConnection(boolean last_failed, boolean use_cookie,boolean follow_redirect,String url,String post){
		try {
			HttpURLConnection con;
			con = (HttpURLConnection) (new URL(url)).openConnection(Proxy.getProxy());
			con.setInstanceFollowRedirects(follow_redirect);
			//リクエストモードの設定
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestMethod("POST");
			//ニコニコ動画のサイトならばクッキーを設定する。
			if(checkNicoURL(url)){
				String cookie = Cookie.getCookie(last_failed);
				if(cookie == null){
					return null;
				}
				con.addRequestProperty("Cookie", cookie);
			}
			//コネクションは毎回切っても良いんじゃないかなあ。
			con.addRequestProperty("Connection", "close");
			con.connect();
			//POSTデータを書き込み
			OutputStream os = con.getOutputStream();
			os.write(url.getBytes("UTF-8"));
			os.flush();
			os.close();
			//レスポンスコードの取得
			int rescode = con.getResponseCode();
			if (rescode != HttpURLConnection.HTTP_OK) {
				return null;
			}else if(rescode >= 300 && rescode < 400){
				//Input == nullはリダイレクトをあらわす
				return new Connection(con,null,-1);
			}
			InputStream is = con.getInputStream();
			//ファイルの長さも取得する。
			String content_length_str = con.getHeaderField("Content-length");
			int max_size = -1;
			if (content_length_str != null && !content_length_str.equals("")) {
				try {
					max_size = Integer.parseInt(content_length_str);
				} catch (NumberFormatException e) {
					max_size = -1;
				}
			}
			//オブジェクトを生成して返す
			return new Connection(con,is,max_size);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * GETを利用したコネクションを張る
	 * @param url
	 * @return
	 */
	public Connection makeConnection(boolean last_failed, String url){
		return makeConnection(last_failed,true,true,url);
	}
	/**
	 * GETを利用したクッキーを用いないコネクションを張る
	 * @param url
	 * @return
	 */
	protected Connection makeConnection(boolean last_failed,boolean follow_redirect, String url){
		return makeConnection(last_failed,follow_redirect,url);
	}
	/**
	 * GETを利用したコネクションを張る
	 * @param url
	 * @return
	 */
	protected Connection makeConnection(boolean last_failed, boolean use_proxy, boolean follow_redirect, String url){
		HttpURLConnection con;
		try {
			con = (HttpURLConnection) (new URL(url)).openConnection(Proxy.getProxy());
			con.setInstanceFollowRedirects(follow_redirect);
			//出力のみ
			con.setDoInput(true);
			con.setRequestMethod("GET");
			//ニコニコ動画ならばクッキーをつける
			if(checkNicoURL(url)){
				String cookie = Cookie.getCookie(last_failed);
				if(cookie == null){
					return null;
				}
				con.addRequestProperty("Cookie", cookie);
			}
			//コネクションは毎回切る事にしておく。
			con.addRequestProperty("Connection", "close");
			//接続してレスポンスコードの取得
			con.connect();
			int rescode = con.getResponseCode();
			if (rescode != HttpURLConnection.HTTP_OK) {
				return null;
			}else if(rescode >= 300 && rescode < 400){
				//Input == nullはリダイレクトをあらわす
				return new Connection(con,null,-1);
			}
			InputStream is = con.getInputStream();
			//ファイルの長さも取得する。
			String content_length_str = con.getHeaderField("Content-length");
			int max_size = -1;
			if (content_length_str != null && !content_length_str.equals("")) {
				try {
					max_size = Integer.parseInt(content_length_str);
				} catch (NumberFormatException e) {
					max_size = -1;
				}
			}
			//オブジェクトを生成して返す
			return new Connection(con,is,max_size);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * URLがニコニコ動画、スマイルビデオかどうかのチェック
	 * @param url
	 * @return
	 */
	private static boolean checkNicoURL(String url){
		url = url.toLowerCase();
		if(!url.startsWith("http://")){
			return false;
		}
		int idx = url.indexOf("/",7);
		if(url.indexOf(NicoUtil.NICO_DOMAIN) < idx){
			return true;
		}
		return false;
	}
}
