package saccubus.util;

import java.util.Date;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 *
 * @author orz
 * @version 1.22r3e
 */
public class Stopwatch {

	private Date startedDate = new Date();
	private Date stopedDate = new Date();
	private final JLabel out;
	private static Hashtable<JLabel, Stopwatch> stopWatchTab = new Hashtable<JLabel, Stopwatch>();
	private TimerTask timertask;
	private Timer timer;
	private static final long TICK_INTERVAL = 100;	// 0.1sec=100milisec
	private boolean started = false;
	private String header;
	private String trailer;

	public Stopwatch(JLabel lbl){
		out = lbl;
		out.setVisible(false);
		started = false;
		header = "経過時間　";
		trailer = "";
		timertask = new TimerTask() {
			@Override
			public void run() {
				show();
				SelfTerminate.restartTimer();
			}
		};
	}

	public static Stopwatch create(JLabel lbl){
		Stopwatch prev = stopWatchTab.get(lbl);
		if(prev==null){
			prev = new Stopwatch(lbl);
			stopWatchTab.put(lbl, prev);
		}
		return prev;
	}

	public void setHeader(String h){
		header = h;
	}
	public void setTrailer(String t){
		trailer = t;
	}
	private void startTimer(String t, long periodmilisec){
		if(timer!=null){
			timer.cancel();
		}
		timer = new Timer(t);
		timer.schedule(timertask, 0, periodmilisec);
	}
	private void sendText(final String s){
		if(SwingUtilities.isEventDispatchThread()) {
			out.setText(s);
		}
		else
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					out.setText(s);
				}
			});
	}

	synchronized public void start() {
		startedDate = new Date();
		if(!started){
			startTimer(startedDate.toString(), TICK_INTERVAL);
			out.setVisible(true);
			started = true;
		}
	}

	synchronized public void stop() {
		stopedDate = new Date();
	}

	private boolean isSetup() {
		return out != null;
	}

	public JLabel getSource(){
		return out;
	}

	public void clear(){
		if (isSetup()){
			sendText(" ");
		}
	}

	public long getStartTime() {
		return startedDate.getTime();
	}

	private long getStopTime() {
		return stopedDate.getTime();
	}

	public long getElapsedTime(){
		return new Date().getTime() - getStartTime();
	}

	public long getElapsedTime(long startMilisec){
		return new Date().getTime() - startMilisec;
	}

	//	private static String FMT1 = "s秒SSSミリ";
	private static String FMT1 = "%d秒%03dミリ";
//	private static String FMT2 = "m分ss秒SSSミリ";
	private static String FMT2 = "%d分%02d秒%03dミリ";
//	private static String FMT3 = "H時間mm分ss秒SSSミリ";
	private static String FMT3 = "%d時間%02d分%02d秒%03dミリ";
//	maybe ok since synchronized fixed
//	oops NOT ok There was a mistake between JST and UTC
	private static long LONG_SEC = 1000L;
	private static long LONG_MINUIT = 60 * LONG_SEC;
	private static long LONG_HOUR = 60 * LONG_MINUIT;

//	synchronized
	private static String  format(long time){
//		Date date = new Date(time);
		long s, m, h;
		if (time < 0){
			return "";
		} else if (time < LONG_MINUIT){
//			return new SimpleDateFormat(FMT1).format(date);
			s = time / LONG_SEC;
			time %= LONG_SEC;
			return String.format(FMT1, s, time);
		} else if (time < LONG_HOUR){
//			return new SimpleDateFormat(FMT2).format(date);
			m = time / LONG_MINUIT;
			time %= LONG_MINUIT;
			s = time / LONG_SEC;
			time %= LONG_SEC;
			return String.format(FMT2, m, s, time);
		} else {
//			return new SimpleDateFormat(FMT3).format(date);
			h = time / LONG_HOUR;
			time %= LONG_HOUR;
			m = time / LONG_MINUIT;
			time %= LONG_MINUIT;
			s = time / LONG_SEC;
			time %= LONG_SEC;
			return String.format(FMT3, h, m, s, time);
		}
	}

	public String formatElapsedTime(){
		return format(getElapsedTime());
	}

	public void show() {
		if (isSetup()){
			sendText(header + formatElapsedTime() + trailer);
		}
	}

	public String formatLatency(){
		return format(getStopTime() - getStartTime());
	}

	public void cancel() {
		timertask.cancel();
	}
}
