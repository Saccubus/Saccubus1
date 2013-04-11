package saccubus.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JLabel;

/**
 *
 * @author orz
 * @version 1.22r3e
 */
public class Stopwatch {

	private Date startedDate = new Date();
	private Date stopedDate = new Date();
	private final JLabel out;

	synchronized public void start() {
		startedDate = new Date();
	}

	synchronized public void stop() {
		stopedDate = new Date();
	}

	public Stopwatch(JLabel out){
		this.out = out;
	}

	public static void setup(JLabel display) {
		//out = display;
	}

	private boolean isSetup() {
		return out != null;
	}

	public JLabel getSource(){
		return out;
	}

	synchronized public void clear(){
		if (isSetup()){
			out.setText(" ");
		}
	}

	private long getStartTime() {
		return startedDate.getTime();
	}

	private long getStopTime() {
		return stopedDate.getTime();
	}

	private long getElapsedTime(){
		return new Date().getTime() - getStartTime();
	}

	private static String FMT1 = "s•bSSSƒ~ƒŠ";
	private static String FMT2 = "m•ªss•bSSSƒ~ƒŠ";
	private static String FMT3 = "HŽžŠÔmm•ªss•bSSSƒ~ƒŠ";
//	private static String FMT3 = "%dŽžŠÔ%02d•ª%02d•b%03dƒ~ƒŠ";
//	maybe ok since synchronized fixed
	private static long LONG_MINUIT = 60 * 1000;
	private static long LONG_HOUR = 60 * LONG_MINUIT;

	synchronized private static String  format(long time){
		Date date = new Date(time);
		if (time < 0){
			return "";
		} else if (time < LONG_MINUIT){
			return new SimpleDateFormat(FMT1).format(date);
		} else if (time < LONG_HOUR){
			return new SimpleDateFormat(FMT2).format(date);
		} else {
			return new SimpleDateFormat(FMT3).format(date);
//			long h = time / LONG_HOUR;
//			time %= LONG_HOUR;
//			long m = time / LONG_MINUIT;
//			time %= LONG_MINUIT;
//			long s = time / 1000;
//			time %= 1000;
//			return String.format(FMT3, h, m, s, time);
		}
	}

	public String formatElapsedTime(){
		return format(getElapsedTime());
	}

	public void show() {
		if (isSetup()){
			out.setText("Œo‰ßŽžŠÔ@" + formatElapsedTime());
		}
	}

	public String formatLatency(){
		return format(getStopTime() - getStartTime());
	}
}
