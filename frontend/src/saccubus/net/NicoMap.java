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
	public NicoMap(){
		map = new HashMap<String, String>();
	}
	/**
	 * 全マッピングをプリントアウトする
	 * @param out
	 */
	public void printAll(PrintStream out) {
		for (String key: map.keySet()){
			out.println("■map:<" + key + "> <" + this.get(key) + ">");
		}
	}
	/**
	 * ニコマップが空ならtrue
	 * @return
	 */
	private boolean isEmpty(){
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
	private void put(String key, String value){
		map.put(key.toLowerCase(), value);
	}
	/**
	 * 文字列に変換
	 * @return String 
	 */
	@Override
	public String toString(){
		if (this.isEmpty())
			return null;
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
		int idx = str.indexOf("=");
		if (idx < 0) {
			return;
		}
		String key = str.substring(0, idx);
		String value = str.substring(idx + 1);
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
	private void putArray(String[] array){
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
	void add(String key, String value){
		if(!key.equalsIgnoreCase("Set-Cookie")){
			this.put(key, value);
		} else {
		//Set-Cookie
			if (!this.containsKey(key)){
				this.put(key, value);
			} else {
				//Set-Cookie 2nd
				NicoMap nm = new NicoMap();
				nm.splitAndPut(this.get(key), "; ");
				nm.splitAndPut(value, "; ");
				this.put(key, nm.toString());
			}
		}
	}
	/**
	 * HttpURLConnectionのヘッダーを全部addする
	 * @param con　connect後のHttpURLConnection
	 */
	public void putConnection(HttpURLConnection con){
		String key;
		if ((key = con.getHeaderFieldKey(0)) != null){
			this.add(key, con.getHeaderField(0));
		}
		for (int i = 1; (key = con.getHeaderFieldKey(i)) != null; i++){
			this.add(key, con.getHeaderField(i));
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