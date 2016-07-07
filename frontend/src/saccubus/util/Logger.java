package saccubus.util;

import java.io.File;
import java.io.FileNotFoundException;
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
	private File logfile;
	private static TextView logview = null;
	private StringBuilder logbuf = new StringBuilder();
	private int loglength = 0;
	private static final int LOG_LIMIT = 1000000;
	private static final int LOG_CONTINUE = 10000;

	public Logger(File file){
		logfile = file;
		if(file==null){
			out = null;
		}else
		try {
			out = new PrintStream(file);
		} catch (FileNotFoundException e) {
			out = null;
			e.printStackTrace();
		}
	}

	// sm12345[log1]frontend.txt
	public Logger(String tag, int tid, String uniq){
		this(Logger.mkTemp(tag,tid,uniq));
	}

	public static File mkTemp(String tag, int tid, String uniq) {
		return Path.mkTemp(tag+"[log"+tid+"]"+uniq);
	}

	public void print(String s){
		if(out!=null)
			out.print(s);
		System.out.print(s);
		logPrint(s);
	}

	private void logPrint(String s){
		if(loglength > LOG_LIMIT ){
			logbuf.delete(0, logbuf.length() - LOG_CONTINUE);
			loglength = logbuf.length();
		}
		logbuf.append(s);
		loglength += s.length();
		if(existLogview()){
			if(loglength > LOG_LIMIT ){
				logview.clearlog();
				logview.print(logbuf.substring(0));
			} else {
				logview.print(s);
			}
		}
	}

	private static boolean existLogview(){
		if(logview==null){
			logview = new TextView(MainFrame.getMaster(), "ログView", false, false);
			if(logview==null){
				System.out.print("ログViewが作れません");
			}
		}
		return logview!=null;
	}

	public static void setViewVisislbe(boolean visible){
		if(existLogview())
			logview.setVisible(visible);
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
		if(out!=null)
			e.printStackTrace(out);
		e.printStackTrace();
	}
}
