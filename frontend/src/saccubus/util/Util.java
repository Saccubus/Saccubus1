package saccubus.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Util {
	/*
	 * OutputStreamへデータを書き込む
	 */
	public static void writeInt(OutputStream os, int i) throws IOException {
		// リトルエンディアン
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

	//サロゲートペア＆(結合文字) 検出＆文字除去
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

	//サロゲートペア＆(結合文字) 文字置き換え
	public static String ReplaceEmoji(String str, char hs, char ls, String t)
	{
		if (str.length() <= 0) return str;

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (Character.isHighSurrogate(ch)) {
				char cl = str.charAt(++i);
				//sb.append("high="+Integer.toHexString((int) ch)+ ":low="+Integer.toHexString((int) cl));
				if ((ch == hs) && (cl == ls)) {
					sb.append(t);
				}
				else {
					sb.append(ch);
					sb.append(cl);
				}
			}
			else {
				sb.append(ch);
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
