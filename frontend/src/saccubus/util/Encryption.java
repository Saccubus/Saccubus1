/**
 * <p>
 * �Q�ƌ�:
 * <a href="http://www.ne.jp/asahi/hishidama/home/tech/java/aes.html"
 *  >Java�uAES�Í��v����(Hishidama's AES Sample)</a><br/>
 * �P���ȗ�
 * �閧���i�Í����E�������œ������̂��g���j���o�C�g��ŗp�ӂ��Ďg����B
 * </p>
 */
package saccubus.util;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * <p>
 * �^�C�g��: ������΂�<br/>
 * ����: �j�R�j�R����̓�����R�����g���ŕۑ�<br/>
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

		// �Í���
		byte[] enc = encode(args[0].getBytes(), skey);
		System.out.println(new String(enc));
		// �A�X�L�[��
		byte[] a64 = utoa(enc);
		System.out.println(new String(a64));
		// �o�C�g��
		byte[] b64 = atou(a64);
		System.out.println(new String(b64));
		// ������
		byte[] dec = decode1(b64, skey);
		System.out.println(new String(dec));
	}
*/
	private static byte[] bseed = null;

	/**
	 * seed��ݒ�
	 * @param seed : String
	 */
	public static void setSeed(String seed){
		bseed = seed.getBytes();
	}

	/**
	 * �閧�����o�C�g�񂩂琶������
	 * @param key_bits ���̒����i�r�b�g�P�ʁj
	 */
	private static Key makeKey(int key_bits){
		// �o�C�g��
		byte[] key = new byte[key_bits / 8];
		int i;
		// �o�C�g��̓��e�i�閧���̒l�j�̓v���O���}�[�����߂�
		for(i = 0; i < key.length && i < bseed.length; i++){
			key[i] = (byte) (bseed[i] ^ i);
		}
		for(; i < key.length; i++){
			key[i] = (byte) (i + 1);
		}
		return new SecretKeySpec(key, "AES");
	}

	/**
	 * �閧����seed���琶������
	 * @param key_bits ���̒����i�r�b�g�P�ʁj
	 * @param seed : String
	 * @return
	 */
	public static Key makeKey(int key_bits, String seed){
		setSeed(seed);
		return makeKey(key_bits);
	}

	/**
	 * �Í���
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
	 * ������
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
	 * �A�X�L�[��
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
	 * �o�C�i���[��
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
