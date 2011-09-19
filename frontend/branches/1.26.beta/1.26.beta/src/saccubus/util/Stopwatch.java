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

	public void start() {
		startedDate = new Date();
	}

	public void stop() {
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

	public void clear(){
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
	private static long LONG_MINUIT = 60 * 1000;
	private static long LONG_HOUR = 60 * LONG_MINUIT;

	private static String format(long time){
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
