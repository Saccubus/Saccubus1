package saccubus;

import java.text.*;
import java.util.Date;

/**
 * <p>
 * ������΂��@�g��
 *
 * class MyDateFormat extends DateFormat
 *
 * �ߋ����O�����߂邽�߂̃t�H�[�}�b�g�ϊ��A�Ȃ�
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
			"yyyy�^MM�^dd_HH�Fmm�Fss");

	public MyDateFormat() {
		super();
		wayBackTime = "0";
		date = null;
		time = "";
	}

/**
 *
 * @param time
 * �@�ߋ����O�̎��Ԏw�蕶����<br>
 * yyyy/MM/dd ���� yyyy/MM/dd HH:mm ���� yyyy/MM/dd HH:mm:ss<br>
 * ���� 1970/1/1 ����̕b��
 * @return
 * �@�ϊ����ʂ̉�
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
 * �@���O�̎��Ԏw�蕶����<br>
 * yyyy/MM/dd ���� yyyy/MM/dd HH:mm ���� yyyy/MM/dd HH:mm:ss<br>
 * ���� 1970/1/1 0:0:0 ����̕b��
 * @return
 * �@�ϊ��\�̏ꍇ�̓t�@�C�����̃v���t�B�b�N�X�ɗp���镶����<br>
 * �@�s�̏ꍇ�́@�󕶎���
 */
	public static String formatTime(String time){
		MyDateFormat mdf = new MyDateFormat();
		if (mdf.makeTime(time)){
			return mdf.formatDate();
		}
		return "";
	}

/**
 * ���s�O�� new ���āAmakeTime(time) ���Ȃ���΂Ȃ�Ȃ��B
 * @return
 * �@�ߋ����O�����̃p�[�X���ʂ̕�����
 */
	public String formatDate(){
		if (date != null){
			return DateFmtF.format(date);
		} else {
			return null;
		}
	}

/**
 * ���ݎ������t�H�[�}�b�g���ĕԂ�
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
