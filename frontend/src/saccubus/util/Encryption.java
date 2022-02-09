/**
 * <p>
 * 参照元:
 * <a href="http://www.ne.jp/asahi/hishidama/home/tech/java/aes.html"
 *  >Java「AES暗号」メモ(Hishidama's AES Sample)</a><br/>
 * 単純な例
 * 秘密鍵（暗号化・復号化で同じものを使う）をバイト列で用意して使う例。
 * </p>
 */
package saccubus.util;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * <p>
 * タイトル: さきゅばす<br/>
 * 説明: ニコニコ動画の動画をコメントつきで保存<br/>
 * @author Hishidama
 * @author orz
 * @version 1.30
 * </p>
 */
public class Encryption {
/*
	public static void main(String[] args) {
		String seed = "el@psy.cong.looo";
		setSeed(seed);
		Key skey = makeKey(128);

		// 暗号化
		byte[] enc = encode(args[0].getBytes(), skey);
		System.out.println(new String(enc));
		// アスキー化
		byte[] a64 = utoa(enc);
		System.out.println(new String(a64));
		// バイト化
		byte[] b64 = atou(a64);
		System.out.println(new String(b64));
		// 復号化
		byte[] dec = decode1(b64, skey);
		System.out.println(new String(dec));
	}
*/
	private static byte[] bseed = null;

	/**
	 * seedを設定
	 * @param seed : String
	 */
	public static void setSeed(String seed){
		bseed = seed.getBytes();
	}

	/**
	 * 秘密鍵をバイト列から生成する
	 * @param key_bits 鍵の長さ（ビット単位）
	 */
	private static Key makeKey(int key_bits){
		// バイト列
		byte[] key = new byte[key_bits / 8];
		int i;
		// バイト列の内容（秘密鍵の値）はプログラマーが決める
		for(i = 0; i < key.length && i < bseed.length; i++){
			key[i] = (byte) (bseed[i] ^ i);
		}
		for(; i < key.length; i++){
			key[i] = (byte) (i + 1);
		}
		return new SecretKeySpec(key, "AES");
	}

	/**
	 * 秘密鍵をseedから生成する
	 * @param key_bits 鍵の長さ（ビット単位）
	 * @param seed : String
	 * @return
	 */
	public static Key makeKey(int key_bits, String seed){
		setSeed(seed);
		return makeKey(key_bits);
	}

	/**
	 * 暗号化
	 */
	public static byte[] encode(byte[] src, Key skey) {
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, skey);
			return cipher.doFinal(src);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 復号化
	 */
	public static byte[] decode(byte[] src, Key skey) {
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, skey);
			return cipher.doFinal(src);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static final String STR_CONVERT =
	//	 0        1         2         3         4         5         6   6
	//	 1234567890123456789012345678901234567890123456789012345678901234
		"0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_ ";

	/**
	 * アスキー化
	 * @param indata : binary byte[]
	 * @return ascii byte[]
	 */
	public static byte[] utoa(byte[] indata){
		int pos = 0;
		byte[] outdata = new byte[indata.length * 2];
		int j = 0;
		for(int i = 0; i < indata.length; i++){
			int val = indata[i] & 0xff;
			int high = val >> 4;
			int low = val & 0x0f;
			outdata[j++] = (byte) STR_CONVERT.charAt(high + pos);
			outdata[j++] = (byte) STR_CONVERT.charAt(low + pos);
			pos = (pos + 16) % STR_CONVERT.length();
		}
		return outdata;
	}

	/**
	 * バイナリー化
	 * @param indata : ascii byte[]
	 * @return binary byte[]
	 */
	public static byte[] atou(byte[] indata){
		int pos = 0;
		byte[] outdata = new byte[indata.length / 2];
		int i = 0;
		for(int j = 0; j < outdata.length; j++){
			int high = STR_CONVERT.indexOf(indata[i++]) - pos;
			int low = STR_CONVERT.indexOf(indata[i++]) - pos;
			outdata[j] = (byte) ((high << 4) + low);
			pos = (pos + 16) % STR_CONVERT.length();
		}
		return outdata;
	}

	public static String encode(String input, Key skey){
		return new String(utoa(encode(input.getBytes(),skey)));
	}

	public static String decode(String input, Key skey){
		return new String(decode(atou(input.getBytes()),skey));
	}
}
