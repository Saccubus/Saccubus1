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
 * Key �� lowercase �̉p����
 * @author orz
 *
 */
public class NicoMap {
	private Map<String, String> map;
	public NicoMap(){
		map = new HashMap<String, String>();
	}
	/**
	 * �S�}�b�s���O���v�����g�A�E�g����
	 * @param out
	 */
	public void printAll(PrintStream out) {
		for (String key: map.keySet()){
			out.println("��map:<" + key + "> <" + map.get(key) + ">");
		}
	}
	/**
	 * key���܂�ł����true
	 * @param key
	 * @return
	 */
	boolean containsKey(String key) {
		return map.containsKey(key.toLowerCase());
	}
	/**
	 * key��lowercase�ɒ�����map��put
	 * @param key
	 * @param value
	 */
	private void put(String key, String value){
		map.put(key.toLowerCase(), value);
	}
	/**
	 * =�̑O��key, ����value�Ƃ���put<br/>
	 * =���Ȃ��ꍇ�͉������Ȃ�
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
	 * key��lowercase�ɒ�����map����get
	 * @param key
	 * @return
	 */
	String get(String key){
		return map.get(key.toLowerCase());
	}
	/**
	 * HttpURLConnection�̃w�b�_�[��S��put����
	 * @param con�@connect���HttpURLConnection
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
	 * �������&�ŋ�؂��ĕ�����put����<br/>
	 * ������̕������key=value�ƂȂ��Ă��邱��
	 * @param string
	 */
	void putArray(String string){
		String[] array = string.split("&");
		for (int i = 0; i < array.length; i++) {
			this.put(array[i]);
		}
	}
	/**
	 * �������&�ŋ�؂��ĕ�����URLDecode�����̂�put����<br/>
	 * ������̕������key=value�ƂȂ��Ă��邱��
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