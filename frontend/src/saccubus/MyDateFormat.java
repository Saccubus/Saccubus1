package saccubus;

import java.text.*;
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

public class MyDateFormat extends DateFormat {
	private static final long serialVersionUID = 1L;
	private final static DateFormat DateFmt = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss");
	private final static DateFormat DateFmt2 = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm");
	private String wayBackTime = "";
	private Date date;
	private String time;
	private final static DateFormat DateFmtF = new SimpleDateFormat(
			"yyyy／MM／dd_HH：mm：ss");

	public MyDateFormat() {
		super();
		wayBackTime = "0";
		date = null;
		time = "";
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
	public boolean makeTime(String time) {
		Date tmpdate = null;
		if (time == null || time.equals("") || time.equals("0")){
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
				// System.out.println("ng.\nCannot parse time." + time);
				return false;
			}
		}
		this.time = time;
		date = tmpdate;
//		System.out.println("ok.(" + format(date) + "):" + wayBackTime);
		return true;
	}

	public String getWayBackTime() {
		return wayBackTime;
	}

	public String getTime() {
		return time;
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
	public static String formatTime(String time){
		MyDateFormat mdf = new MyDateFormat();
		if (mdf.makeTime(time)){
			return mdf.formatDate();
		}
		return "";
	}

/**
 * 実行前に new して、makeTime(time) しなければならない。
 * @return
 * 　過去ログ日時のパース結果の文字列
 */
	public String formatDate(){
		if (date != null){
			return DateFmtF.format(date);
		} else {
			return null;
		}
	}

/**
 * 現在時刻をフォーマットして返す
 * @return
 */
	public String formatNow(){
		return DateFmtF.format(new Date());
	}

	@Override
	public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
		toAppendTo.append(DateFmt.format(date));
		return toAppendTo;
	}

	@Override
	public Date parse(String time, ParsePosition pos) {
		if (pos == null) {
			if (makeTime(time)){
				return date;
			} else {
				return null;
			}
		}
		if (makeTime(time.substring(pos.getIndex()))){
			return date;
		} else {
			pos.setErrorIndex(pos.getIndex());
			return null;
		}
	}
}
