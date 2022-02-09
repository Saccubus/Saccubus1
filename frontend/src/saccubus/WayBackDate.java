package saccubus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>
 * さきゅばす　拡張
 *
 * class MyDateFormat extends DateFormat
 *
 * 過去ログを求めるためのフォーマット変換、など
 * </p>
 * @author orz
 *
 */

public class WayBackDate {
	private final static SimpleDateFormat DateFmt = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss");
	private final static SimpleDateFormat DateFmt2 = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm");
	private String wayBackTime = "";
	private Date date;
	private String time;
	public final static String STR_FMT = "yyyy／MM／dd_HH：mm：ss";
	public final static String STR_FMT_REGEX = "[0-9]{4}(／[0-3][0-9]){2}_[0-2][0-9](：[0-5][0-9]){2}";
	private final static SimpleDateFormat MyDateFmt
		= new SimpleDateFormat(STR_FMT);

	public WayBackDate(String time){
		wayBackTime = "0";
		date = null;
		this.time = "";
		parse(time);
	}

	/**
	 *
	 * @param time
	 * 　過去ログの時間指定文字列<br>
	 * yyyy/MM/dd 又は yyyy/MM/dd HH:mm 又は yyyy/MM/dd HH:mm:ss<br>
	 * 又は 1970/1/1 からの秒数
	 * @return
	 * 　変換結果の可否
	 */
	synchronized public boolean parse(String time) {
		Date tmpdate = null;
		if (time == null || time.isEmpty() || time.equals("0")){
			return false;
		}
		try {
			tmpdate = DateFmt.parse(time);
		} catch (ParseException ex) {
			tmpdate = null;
		}
		if (tmpdate == null) {
			try {
				tmpdate = DateFmt2.parse(time);
			} catch (ParseException ex2) {
				tmpdate = null;
			}
			if (tmpdate == null) {
				try {
					tmpdate = DateFmt2.parse(time + " 0:0");
					time += " 0:0";
				} catch(ParseException ex3) {
					tmpdate = null;
				}
			}
		}
		if (tmpdate != null) {
			wayBackTime = Long.toString(tmpdate.getTime() / 1000);
		} else {
			try {
				// assume that time is from 1970/1/1 0:0:0 in seconds.
				long tmp_time = Long.parseLong(time);
				wayBackTime = Long.toString(tmp_time);
				tmpdate = new Date(tmp_time * 1000);
			} catch (NumberFormatException ex4) {
				// wayBackTime = "0";	// no need
				// date = null;	// no need
				// not set time
				return false;
			}
		}
		this.time = time;
		date = tmpdate;
		return true;
	}

	public boolean isValid(){
		return date != null;
	}

	public String getWayBackTime() {
		return wayBackTime;
	}

	public String getTime() {
		return time;
	}

	/**
	 * waybackdate の 1970/1/1 0:0:0 からの秒数を返す
	 */
	public long getSecond(){
		return date.getTime() / 1000;
	}

	/**
	 *
	 * @param time
	 * 　ログの時間指定文字列<br>
	 * yyyy/MM/dd 又は yyyy/MM/dd HH:mm 又は yyyy/MM/dd HH:mm:ss<br>
	 * 又は 1970/1/1 0:0:0 からの秒数
	 * @return
	 * 　変換可能の場合はファイル名のプリフィックスに用いる文字列<br>
	 * 　不可の場合は　空文字列
	 */
	public static String format(String time){
		return new WayBackDate(time).format();
	}
	public static String toSourceFormat(String time){
		Date wbd = new WayBackDate(time).date;
		if (wbd != null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			return sdf.format(wbd);
		}
		return "";
	}

	/**
	 * @return
	 * 　過去ログ日時のパース結果の文字列
	 */
	synchronized public String format(){
		if (date != null){
			return MyDateFmt.format(date);
		} else {
			return "";
		}
	}

	/**
	 * @return
	 * 現在時刻をフォーマットして返す
	 */
	synchronized public static String formatNow(){
		return MyDateFmt.format(new Date());
	}

//	public static Date unFormat(String time){
//		try {
//			return MyDateFmt.parse(time);
//		} catch (ParseException e) {
//			return null;
//		}
//	}
}
