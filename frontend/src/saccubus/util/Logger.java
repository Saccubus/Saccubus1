package saccubus.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import saccubus.MainFrame;
import saccubus.TextView;
import saccubus.net.Path;

/**
 * Logger ログ出力クラス
 * @author orz_e
 *
 */
public class Logger {

	public static final Logger MainLog = new Logger(Path.mkTemp("main[log]frontend.txt"));
	private PrintStream out;
	private final File logfile;
	private static TextView logview = null;
	private StringBuffer logbuf = new StringBuffer();
	private int loglength = 0;
	private static final int LOG_LIMIT = 1000000;
	private static final int LOG_CONTINUE = 10000;
	private static boolean enableLogview = true;
	private Logger sysout;

	public Logger(File file){
		this(file, false);
	}

	public Logger(File file, boolean append){
		logfile = file;
		if(file==null){
			out = null;
		}else
		try {
			out = new PrintStream(new FileOutputStream(logfile, append), true);
		} catch (FileNotFoundException e) {
			out = null;
			e.printStackTrace();
		}
	}

	public static void setLogviewVisible(boolean visible){
		enableLogview = visible;
	}

	// sm12345[log1]frontend.txt
	public Logger(String tag, int tid, String uniq){
		this(Logger.mkTemp(tag,tid,uniq));
	}

	public static File mkTemp(String tag, int tid, String uniq) {
		return Path.mkTemp(tag+"[log"+tid+"]"+uniq);
	}

	public void print(String s){
		if(getOutPrintStream()!=null)
			getOutPrintStream().print(s);
		if(sysout != null)
			sysout.print(s);
		else {
			System.out.print(s);
			if(enableLogview)
				logPrint(s);
		}
	}

	private void logPrint(String s){
		logbuf.append(s);
		loglength += s.length();
		if(existLogview()){
			logview.print(s);
		}
		if(loglength > LOG_LIMIT ){
			logbuf.delete(0, loglength - LOG_CONTINUE);
			loglength = logbuf.length();
			if(existLogview()){
				logview.setText(logbuf.substring(0));
			}
		}
	}

	private static boolean existLogview(){
		if(logview==null && enableLogview && MainFrame.getMaster()!=null){
			logview = new TextView(MainFrame.getMaster(), "ログView", false, false);
			if(logview==null){
				System.out.print("ログViewが作れません");
			}
		}
		return logview!=null;
	}

	public static void setViewVisislbe(boolean visible){
		if(existLogview()){
			logview.setVisible(visible);
		}
	}
	public static boolean isViewVisible(){
		if(logview!=null && enableLogview)
			return logview.isVisible();
		else
			return false;
	}

	public void println(String s){
		print(s + "\n");
	}

	public void println() {
		println("");
	}

	public void printf(String format, Object... args) {
		print(String.format(format, args));
	}

	public boolean deleteLog() {
		if(logfile!=null && logfile.canWrite())
			return logfile.delete();
		out = null;
		return false;
	}

	public void printStackTrace(Exception e) {
		if(getOutPrintStream()!=null)
			e.printStackTrace(getOutPrintStream());
		e.printStackTrace();
	}

	public void addSysout(Logger logger) {
		logger.flush();
		if(sysout == null)
			sysout = logger;
		else
			sysout.addSysout(logger);
	}

	private void flush() {
		if(out!=null)
			out.flush();
		if(sysout!=null)
			sysout.flush();
		else
			System.out.flush();
	}

	public PrintStream getOutPrintStream() {
		return out;
	}
}
