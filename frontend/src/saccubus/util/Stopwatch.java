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

	private static Date startedDate = new Date();
	private static Date stopedDate = new Date();
	public static JLabel out = null;

	public static void start() {
		startedDate = new Date();
	}

	public static void setup(JLabel display) {
		out = display;
	}

	private static boolean isSetup() {
		return out != null;
	}

	public static void clear(){
		if (isSetup()){
			out.setText(" ");
		}
	}

	private static String FMT1 = "s•bSSSƒ~ƒŠ";
	private static String FMT2 = "m•ªss•bSSSƒ~ƒŠ";
	private static String FMT3 = "HŽžŠÔmm•ªss•bSSSƒ~ƒŠ";
	private static long LONG_MINUIT = 60 * 1000;
	private static long LONG_HOUR = 60 * LONG_MINUIT;

	public static String format(long time){
		Date date = new Date(time);
		if (time < 0){
			return "";
		} else if (time < LONG_MINUIT){
			return new SimpleDateFormat(FMT1).format(date);
		} else if (time < LONG_HOUR){
			return new SimpleDateFormat(FMT2).format(date);
		} else {
			return new SimpleDateFormat(FMT3).format(date);
		}
	}

	public static void show() {
		if (isSetup()){
			out.setText("Œo‰ßŽžŠÔ@" + format(getElapsedTime()));
		}
	}

	private static long getElapsedTime(){
		return new Date().getTime() - startedDate.getTime();
	}

	public static void stop() {
		stopedDate = new Date();
	}

	public static long getStartTime() {
		return startedDate.getTime();
	}

	public static long getStopTime() {
		return stopedDate.getTime();
	}

	public static String formatLatency(){
		return format(getStopTime() - getStartTime());
	}
}
