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
	private Map<String, String> map;
	public NicoMap(){
		map = new HashMap<String, String>();
	}
	/**
	 * 全マッピングをプリントアウトする
	 * @param out
	 */
	public void printAll(PrintStream out) {
		for (String key: map.keySet()){
			out.println("■map:<" + key + "> <" + map.get(key) + ">");
		}
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
	private void put(String key, String value){
		map.put(key.toLowerCase(), value);
	}
	/**
	 * =の前をkey, 後ろをvalueとしてput<br/>
	 * =がない場合は何もしない
	 * @param str
	 */
	private void put(String str){
		int idx = str.indexOf("=");
		if (idx < 0) {
			return;
		}
		String key = str.substring(0, idx);
		String value = str.substring(idx + 1);
		put(key, value);
	}
	/**
	 * keyをlowercaseに直してmapからget
	 * @param key
	 * @return
	 */
	String get(String key){
		return map.get(key.toLowerCase());
	}
	/**
	 * HttpURLConnectionのヘッダーを全部putする
	 * @param con　connect後のHttpURLConnection
	 */
	public void putConnection(HttpURLConnection con){
		String key;
		if ((key = con.getHeaderFieldKey(0)) != null){
			this.put(key, con.getHeaderField(0));
		}
		for (int i = 1; (key = con.getHeaderFieldKey(i)) != null; i++){
			this.put(key, con.getHeaderField(i));
		}
	}
	/**
	 * 文字列を&で区切って分割しputする<br/>
	 * 分割後の文字列はkey=valueとなっていること
	 * @param string
	 */
	void putArray(String string){
		String[] array = string.split("&");
		for (int i = 0; i < array.length; i++) {
			this.put(array[i]);
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