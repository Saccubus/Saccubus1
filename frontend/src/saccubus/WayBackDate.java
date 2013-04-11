package saccubus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

public class WayBackDate {
	private final static SimpleDateFormat DateFmt = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss");
	private final static SimpleDateFormat DateFmt2 = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm");
	private String wayBackTime = "";
	private Date date;
	private String time;
	public final static String STR_FMT = "yyyy�^MM�^dd_HH�Fmm�Fss";
	public final static String STR_FMT_REGEX = "[0-9]{4}(�^[0-3][0-9]){2}_[0-2][0-9](�F[0-5][0-9]){2}";
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
	 * �@�ߋ����O�̎��Ԏw�蕶����<br>
	 * yyyy/MM/dd ���� yyyy/MM/dd HH:mm ���� yyyy/MM/dd HH:mm:ss<br>
	 * ���� 1970/1/1 ����̕b��
	 * @return
	 * �@�ϊ����ʂ̉�
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
				// System.out.println("ng.\nCannot parse time." + time);
				return false;
			}
		}
		this.time = time;
		date = tmpdate;
//		System.out.println("ok.(" + format(date) + "):" + wayBackTime);
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
	 * waybackdate �� 1970/1/1 0:0:0 ����̕b����Ԃ�
	 */
	public long getSecond(){
		return date.getTime() / 1000;
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
	public static String format(String time){
		return new WayBackDate(time).format();
	}

	/**
	 * @return
	 * �@�ߋ����O�����̃p�[�X���ʂ̕�����
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
	 * ���ݎ������t�H�[�}�b�g���ĕԂ�
	 */
	synchronized public static String formatNow(){
		return MyDateFmt.format(new Date());
	}

	public static Date unFormat(String time){
		try {
			return MyDateFmt.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
}
