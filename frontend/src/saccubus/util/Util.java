package saccubus.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

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

	//文字列の長さチェック
	public static boolean isCommentLength(String str, int leng, boolean is_total)
	{
		boolean result = false;
		
		int ll = str.length();
		if (ll <= 0 || leng <= 0) return result;

		if (is_total) {
			result = str.codePointCount(0, ll) > leng; 
		} else {
			//splitして1行ごとの行数チェック
			List<String> ddd = Arrays.asList(str.split("(?<=\\n)"));
			for (String tt : ddd) {
				ll = tt.length();
				if (ll > 0) {
					if (tt.codePointCount(0, ll) > leng) {
						result = true;
						break;
					}
				}
			}
		}
		return result;
	}

	//サロゲートペア＆(結合文字) 検出＆文字除去
	//異体字セレクタ U+FE00～U+FE0F、U+E0100〜U+E01EF は削除
	//サロゲートペア文字は t で置換
	public static String DelEmoji(String str, String t)
	{
		if (str.length() <= 0) return str;

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c >= (char)0xFE00 && c <= (char)0xFE0F) {
				continue;
			}
			else if (Character.isHighSurrogate(c)) {
				if (c == (char)0xdb40) {
					char cc = str.charAt(i+1);
					if (cc >= (char)0xdd00 && cc <= (char)0xddef) {
						++i;
						continue;
					}
				}
				sb.append(t);
				++i;
			}
			else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	//サロゲートペア＆(結合文字) 検出＆文字除去
	//特定の異体字セレクタ U+E0100〜U+E01EF のみ削除
	//U+E0100 UTF-16 Encoding:	0xDB40 0xDD00
	//U+E01EF UTF-16 Encoding:	0xDB40 0xDDEF
	public static String DelEmoji2(String str)
	{
		if (str.length() <= 0) return str;

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (Character.isHighSurrogate(c) && (c == (char)0xdb40)) {
				char cc = str.charAt(i+1);
				if (cc >= (char)0xdd00 && cc <= (char)0xddef) {
					++i;
					continue;
				}
			}
			sb.append(c);
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

	// UTCオフセット付時刻(yyyy/MM/ddTHH:mm:ss+0:00)をUnixtimeに変換
	public static String OffsetDateTime2UnixTime(String offset_datetime) {
		OffsetDateTime odt = OffsetDateTime.parse(offset_datetime);
		long unixtime = odt.toEpochSecond();
		return String.valueOf(unixtime);
	}

	// ローカルタイムの時刻(yyyy/MM/dd HH:mm:ss)をUnixtimeに変換
	public static String LocalDateTime2UnixTime(String datetime) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime ldt = LocalDateTime.parse(datetime, dtf);
		long unixtime = ldt.atZone(ZoneId.systemDefault()).toEpochSecond();
		return String.valueOf(unixtime);
	}

	// 秒で表示される文字列を HH:MM/dd 型式に変換
	public static String Seconds2Hms(String seconds) {
		String result = "";
		if (seconds == null)
			return null;
		try {
			int secs = Integer.parseInt(seconds);
			String ttt;
			if (secs >= 3600) {
				result = (secs / 3600) + ":";
				ttt = "000" +((secs % 3600) / 60);
				result += ttt.substring(ttt.length()-2) + ":";
				ttt = "000" + (secs % 60);
				result += ttt.substring(ttt.length()-2);
			}
			else if (secs >= 60) {
				result = ((secs % 3600) / 60) + ":";
				ttt = "000" + (secs % 60);
				result += ttt.substring(ttt.length()-2);
			}
			else {
				ttt = "000" + (secs % 60);
				result = "0:" + ttt.substring(ttt.length()-2);
			}
		} catch (NumberFormatException nfex) {
			return null;
		}
		return result;
	}
}
