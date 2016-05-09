package saccubus.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

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
