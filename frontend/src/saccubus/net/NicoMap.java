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
	private final Map<String, String> map;
	public NicoMap(){
		map = new HashMap<String, String>();
	}
	/**
	 * �S�}�b�s���O���v�����g�A�E�g����
	 * @param out
	 */
	public void printAll(PrintStream out) {
		for (String key: map.keySet()){
			out.println("��map:<" + key + "> <" + this.get(key) + ">");
		}
	}
	/**
	 * �j�R�}�b�v����Ȃ�true
	 * @return
	 */
	private boolean isEmpty(){
		return (map.isEmpty());
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
	 * ������ɕϊ�
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
		this.put(key, value);
	}
	/**
	 * key��lowercase�ɒ�����map���當�����get
	 * @param key
	 * @return
	 */
	String get(String key){
		return map.get(key.toLowerCase());
	}
	/**
	 * ������z���put����B�e�v�f��key=value�ƂȂ��Ă��邱��
	 * @param array
	 */
	private void putArray(String[] array){
		for (int i = 0; i < array.length; i++) {
			this.put(array[i]);
		}
	}
	/**
	 * ������𐳋K�\��regx�ŋ�؂��ĕ�����put����<br/>
	 * ������̕������key=value�ƂȂ��Ă��邱��
	 * @param string
	 * @param regx
	 */
	void splitAndPut(String string, String regx){
		this.putArray(string.split(regx));
	}
	/**
	 * key��lowercase�ɒ�����map��put�ASet-Cookie�͑O�̒l�ɒǉ�
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
	 * HttpURLConnection�̃w�b�_�[��S��add����
	 * @param con�@connect���HttpURLConnection
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