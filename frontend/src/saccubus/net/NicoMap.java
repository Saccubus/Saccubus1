/**
 *
 */
package saccubus.net;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Map<String Key, String Value><br/>
 * Key は lowercase の英数字
 * @author orz
 *
 */
public class NicoMap {
	private final Map<String, String> map;
	private int setCookieCount = 0;
	private static final String SECURE_COOKIE = "_secure_cookie";
	private static final String NORMAL_COOKIE = "_normal_cookie";
	private static final String SESSION_COOKIE = "_session_cookie";
	private static final String DELETE_COOKIE = "_delete_cookie";
	private static final String OTHER_COOKIE = "_other_cookie";

	/**
	 * @param cookie
	 */
	void setCookie(NicoCookie cookie) {
		cookie.setSecureCookie(get(NicoMap.SECURE_COOKIE));
		cookie.setNormalCookie(get(NicoMap.NORMAL_COOKIE));
		cookie.setSessionCookie(get(NicoMap.SESSION_COOKIE));
		cookie.setDeleteCookie(get(NicoMap.DELETE_COOKIE));
		cookie.setOtherCookie(get(NicoMap.OTHER_COOKIE));
	}

	public NicoMap(){
		map = new HashMap<String, String>();
	}
	/**
	 * 全マッピングをプリントアウトする
	 * @param out
	 */
	public void printAll(PrintStream out) {
		if(out==null) return;
		for (String key: map.keySet()){
			String value = this.get(key);
			if(!key.startsWith("_"))
				debugOut(out, key, value);
		}
	}
	private void debugOut(PrintStream out, String key, String value){
		if(out!=null)
			out.println("■map:<" + key + "> <" + value + ">");
	}
	/**
	 * ニコマップが空ならtrue
	 * @return
	 */
	public boolean isEmpty(){
		return (map.isEmpty());
	}
	/**
	 * keyを含んでいればtrue
	 * @param key
	 * @return
	 */
	boolean containsKey(String key) {
		return map.containsKey(key.toLowerCase());
	}
	/**
	 * keyをlowercaseに直してmapにput
	 * @param key
	 * @param value
	 */
	void put(String key, String value){
		map.put(key.toLowerCase(), value);
	}
	/**
	 * 文字列に変換
	 * @return String
	 */
	@Override
	public String toString(){
//		if (this.isEmpty())
//			return null;
		StringBuilder sb = new StringBuilder();
		for (String key: map.keySet()){
			sb.append(key + "=" + map.get(key) + "; ");
		}
		String ret = sb.toString();
		return ret.substring(0, ret.lastIndexOf(";"));
	}
	/**
	 * =の前をkey, 後ろをvalueとしてput<br/>
	 * =がない場合は何もしない
	 * @param str
	 */
	private void put(String str){
		String key;
		String value;
		int idx = str.indexOf("=");
		if (idx > 0) {
			key = str.substring(0, idx);
			value = str.substring(idx + 1);
		} else {
			key = str;
			value = "";
		}
		this.put(key, value);
	}
	/**
	 * keyをlowercaseに直してmapから文字列をget
	 * @param key
	 * @return
	 */
	String get(String key){
		return map.get(key.toLowerCase());
	}
	/**
	 * 文字列配列をputする。各要素はkey=valueとなっていること
	 * @param array
	 */
	void putArray(String[] array){
		for (int i = 0; i < array.length; i++) {
			this.put(array[i]);
		}
	}
	/**
	 * 文字列を正規表現regxで区切って分割しputする<br/>
	 * 分割後の文字列はkey=valueとなっていること
	 * @param string
	 * @param regx
	 */
	void splitAndPut(String string, String regx){
		this.putArray(string.split(regx));
	}
	/**
	 * keyをlowercaseに直してmapにput、Set-Cookieは前の値に追加
	 * @param key
	 * @param value
	 */
	void add2(String key, String value){
		if(this.containsKey(key)){
			value = this.get(key) + "; " + value;
		}
		this.put(key, value);
	}
	/**
	 * keyをlowercaseに直してmapにput、Set-Cookieは前の値に追加
	 * @param key
	 * @param value
	 */
	void add(String key, String value){
		if(!key.equalsIgnoreCase("Set-Cookie")){
			this.put(key, value);
		}else{
			key = "_" + key + setCookieCount++;
			this.put(key, value);
			//Set-Cookie
			// 1 https secure cookie for login (about one month)
			// 2 nicosid/nicohistory cookie
			// 3 user_session cookie (about one month)
			// 4 delete cookie, shall immediately expire (no life)
			// 5 other
			NicoMap nm = new NicoMap();
			nm.splitAndPut(value, "; *");
			String max_age = nm.get("Max-Age");
			long age = -1;
			try {
				age = Long.parseLong(max_age);
			} catch(NumberFormatException ex){
				age = -1;
			}
			if(age < 0){
				this.add2(DELETE_COOKIE, value);	//4
			} else if(nm.containsKey("Secure")){
				this.put(SECURE_COOKIE, value);	//1
			} else if(!nm.containsKey("user_session")) {
				this.add2(NORMAL_COOKIE, value);	//2
			} else {
				String user_session = nm.get("user_session");
				if(user_session!=null && user_session.contains("user_session")){
					this.put(SESSION_COOKIE, value);	//3
				} else {
					this.add2(OTHER_COOKIE, value);	//5
				}
			}
		}
	}
	/**
	 * HttpURLConnectionのヘッダーを全部addし outがnullでないならprintlnする。
	 * @param con　connect後のHttpURLConnection
	 */
	public void putConnection(HttpURLConnection con, PrintStream out){
		String key;
		String value;
		key = con.getHeaderFieldKey(0);
		value = con.getHeaderField(0);
		if (key == null){
			debugOut(out,"_Response",value);
		} else {
			this.add(key, value);
			debugOut(out,key,value);
		}
		for (int i = 1; (key = con.getHeaderFieldKey(i)) != null; i++){
			value = con.getHeaderField(i);
			this.add(key, value);
			debugOut(out,key,value);
		}
	}
	/**
	 * 文字列を&で区切って分割しURLDecodeしたのちputする<br/>
	 * 分割後の文字列はkey=valueとなっていること
	 * @param string
	 * @param encoding
	 */
	void putArrayURLDecode(String string, String encoding)
			throws UnsupportedEncodingException {
		String[] array = string.split("&");
		for (int i = 0; i < array.length; i++) {
			this.put(URLDecoder.decode(array[i], encoding));
		}
	}
}