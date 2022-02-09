package saccubus;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;

import saccubus.util.Logger;
import saccubus.util.Stopwatch;

/**
 * @author orz
 */
public class FFmpeg {

	private final String exePath;
	private StringBuffer sb;
	private ArrayList<String> sa;
	private String LastFrame = "";
	private String LastError = "";

	public FFmpeg(String path) {
		exePath = path.replace(File.separator, "/");
	}

	public String getExePath(){
		return exePath;
	}
	public void setCmd(String string) {
		sb = new StringBuffer();
		sb.append("\"");
		sb.append(exePath);
		sb.append("\" ");
		sb.append(string);
	}

	public void addCmd(String string) {
		sb.append(string);
	}

	public void addMap(LinkedHashMap<String,String> map){
		String key = "";
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			key = (String) it.next();
			sb.append(key + " " + map.get(key) + " ");
		}
		sb.toString();
	}

	public void addFile(File file) {
		String path = file.getPath().replace(File.separator, "/");
		if (path.indexOf(' ') >= 0 || path.indexOf("　") >= 0){
			sb.append(" \"");
			sb.append(path);
			sb.append("\"");
		} else {
			sb.append(path);
		}
	}

	public String getCmd() {
		return sb.substring(0);
	}

	public ArrayList<String> getCmdArrayList() {
		sa = parse(getCmd());
		return sa;
	}

	private ArrayList<String> parse(String cmd) {
		// 引用符で囲まれた文字列かスペースで区切られたトークンごとにArrayList要素にする
		String reg = "(\"[^\"]*[^\\\\]\")|([^ ]+)";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(cmd);
		sa = new ArrayList<String>();
		while (m.find()) {
			sa.add(m.group());
		}
		return sa;
	}

	public interface CallbackInterface extends Callback {
		public boolean checkStop();
		public void doAbort(String text);
	}

	public int exec(int abortedCode, CallbackInterface callback, Logger log) {
		ProcessBuilder pb = null;
		Process process = null;
		BufferedReader ebr = null;
		try {
	//		log.println("\n\n----\nProcessing FFmpeg...\n----\n\n");
	//		process = Runtime.getRuntime().exec(getCmd());
			pb = new ProcessBuilder(getCmdArrayList());
			pb.redirectErrorStream(true);
			// add java.class.path to env PATH
			pb.environment().put("PATH", Saccubus.pathenv);
			process = pb.start();
			ebr = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
			String e;
			while ((e = ebr.readLine()) != null) {
				callback.doEveryLoop(e, log);
				//Stopwatch.show();				// must be set in callback.doEveryLoop
				if (callback.checkStop()) {
					process.destroy();
					callback.doAbort(e);
					return abortedCode;
				}
			}
			process.waitFor();
			return process.exitValue();
		} catch (InterruptedException ex) {
			log.printStackTrace(ex);
			return -1;
		} catch (IOException ex) {
			log.printStackTrace(ex);
			return -1;
		} finally {
			try {
				ebr.close();
				process.getInputStream().close();
			} catch(Exception ex){
				log.printStackTrace(ex);
			}
		}
	}

	private StringBuffer errorLogging = null;
//	private int videoLength = 0;
	public int exec(JLabel status, int abortedCode, ConvertStopFlag flag, Stopwatch watch, Logger log) {

		class FFmpegCallback implements CallbackInterface {
			private JLabel status;
			private ConvertStopFlag flag;
			//private Stopwatch stopwatch;

			public FFmpegCallback(JLabel status, ConvertStopFlag flag, Stopwatch watch){
				this.status = status;
				this.flag = flag;
				//stopwatch = watch;
			}
			@Override
			public boolean checkStop() {
				return flag.needStop();
			}
			@Override
			public void doAbort(String e) {
				synchronized (status) {
					status.setText("ffmpegの実行を中止しました。");
				}
			}
			@Override
			public void doEveryLoop(String e, Logger log) {
				//stopwatch.show();
				if (e.startsWith("frame=")) { //
					LastFrame = e;
					synchronized (status) {
						status.setText(e);
					}
				} else {
					LastError = e;
					errorLogging.append(LastError + "\n");
					if(!e.endsWith("No accelerated colorspace conversion found")){
						log.println(e);
					}
				}
			}
		}

		LastError = "エラー情報がありません";
		LastFrame = "";
		errorLogging = new StringBuffer();
		log.println("\n\n----\nProcessing FFmpeg...\n----\n\n");
		return exec(abortedCode, new FFmpegCallback(status, flag, watch), log);
	}

	public String getLastFrame() {
		return LastFrame;
	}

	public String getLastError() {
		return LastError;
	}

	public StringBuffer getErrotLog() {
		return errorLogging;
	}

	public interface Callback {
		void doEveryLoop(String e, Logger log);
	}

	/**
	 * ffmpeg を実行する　アボート出来ない。
	 * @param callback
	 * @return
	 */
	public int exec(Callback callback, Logger log){
		ProcessBuilder pb = null;
		Process process = null;
		BufferedReader ebr = null;
		try {
	//		process = Runtime.getRuntime().exec(getCmd());
			pb = new ProcessBuilder(getCmdArrayList());
			pb.redirectErrorStream(true);
			// add java.class.path to env PATH
			pb.environment().put("PATH", Saccubus.pathenv);
			process = pb.start();
			ebr = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String e;
			while ((e = ebr.readLine()) != null) {
				callback.doEveryLoop(e, log);
			}
			process.waitFor();
			return process.exitValue();
		} catch (InterruptedException ex) {
			log.printStackTrace(ex);
			return -1;
		} catch (IOException ex) {
			log.printStackTrace(ex);
			return -1;
		} finally {
			try {
				ebr.close();
	//			process.getErrorStream().close();
				process.getInputStream().close();
			} catch(Exception ex){
				log.printStackTrace(ex);
			}
		}
	}

//	public Aspect getAspect(File videoFile) {
//		return new VideofileInfo(videoFile, this).getAspect();
//	}

//	int getVideoLength(File videoFile) {
//		return videoLength;
//	}

//	public enum Aspect {
//		NORMAL, WIDE,
//	}
	public static class Aspect {
		private final int width;
		private final int height;
		private final double aspect;
		public int getWidth() { return width; }
		public int getHeight() { return height; }
		public double getValue() { return aspect; }
		public Aspect(int x, int y){
			width = x;
			height = y;
			if ( x !=0 && y != 0){
				aspect = (double)x / (double)y;
			} else {
				aspect = 0.0;
			}
		}
		public boolean isWide(){
			if (aspect >= 1.666) {	// 640x384
				return true;
			} else {
				return false;
			}
		}
		public boolean isQWide(){
			if (aspect >= 1.777) {	// 640x360 < 854x480=1.779
				return true;
			} else {
				return false;
			}
		}
		public String explain(){
			return "(" + width + "x" + height + ")" + String.format("%.3f", aspect);
		}
		public String getSize(){
			if(isInvalid()) return null;
			return "" + width + ":" + height;
		}
		public boolean isInvalid(){
			return (aspect<=0.0);
		}
		public static final Aspect NORMAL = new Aspect(4, 3);
		public static final Aspect WIDE = new Aspect(16, 9);
		public static final Aspect ERROR = new Aspect(0,0);
	}
}
