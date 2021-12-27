package saccubus.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Util {
	/*
	 * OutputStream�փf�[�^����������
	 */
	public static void writeInt(OutputStream os, int i) throws IOException {
		// ���g���G���f�B�A��
		os.write((i >> 0) & 0xff);
		os.write((i >> 8) & 0xff);
		os.write((i >> 16) & 0xff);
		os.write((i >> 24) & 0xff);
	}
	public static int readInt(InputStream is) throws IOException{
		int num = (is.read() & 0xff) +
		((is.read() & 0xff) << 8) +
		((is.read() & 0xff) << 16) +
		((is.read() & 0xff) << 24);
		return num;
	}

	//�T���Q�[�g�y�A��(��������) ���o����������
	public static String DelEmoji(String str, String t)
	{
		if (!IsSurrogatePair(str)) return str;

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (Character.isHighSurrogate(c)) {
				sb.append(t);
				++i;
			}
			else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static Boolean IsSurrogatePair(String str)
	{
		int ll = str.length();
		if (ll < 1) return false;
		return str.codePointCount(0, ll) < ll;
	}
}
