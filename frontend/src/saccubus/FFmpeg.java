package saccubus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;

import saccubus.util.BitReader;
import saccubus.util.Cws2Fws;
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
		String reg = "(\"[^\"]*\")|([^ ]+)";
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

	public int exec(int abortedCode, CallbackInterface callback) {
		ProcessBuilder pb = null;
		Process process = null;
		BufferedReader ebr = null;
		try {
	//		System.out.println("\n\n----\nProcessing FFmpeg...\n----\n\n");
	//		process = Runtime.getRuntime().exec(getCmd());
			pb = new ProcessBuilder(getCmdArrayList());
			pb.redirectErrorStream(true);
			process = pb.start();
			ebr = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String e;
			while ((e = ebr.readLine()) != null) {
				callback.doEveryLoop(e);
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
			ex.printStackTrace();
			return -1;
		} catch (IOException ex) {
			ex.printStackTrace();
			return -1;
		} finally {
			try {
				ebr.close();
				process.getInputStream().close();
			} catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}

	private StringBuffer errorLogging = null;
	private int videoLength = 0;
	public int exec(JLabel status, int abortedCode, ConvertStopFlag flag, Stopwatch watch) {

		class FFmpegCallback implements CallbackInterface {
			private JLabel status;
			private ConvertStopFlag flag;
			private Stopwatch stopwatch;

			public FFmpegCallback(JLabel status, ConvertStopFlag flag, Stopwatch watch){
				this.status = status;
				this.flag = flag;
				stopwatch = watch;
			}
			@Override
			public boolean checkStop() {
				return flag.needStop();
			}
			@Override
			public void doAbort(String e) {
				status.setText("ffmpegの実行を中止しました。");
			}
			@Override
			public void doEveryLoop(String e) {
				stopwatch.show();
				if (e.startsWith("frame=")) { //
					LastFrame = e;
					status.setText(e);
				} else {
					LastError = e;
					errorLogging.append(LastError + "\n");
					if(!e.endsWith("No accelerated colorspace conversion found")){
						System.out.println(e);
					}
				}
			}
		}

		LastError = "エラー情報がありません";
		LastFrame = "";
		errorLogging = new StringBuffer();
		System.out.println("\n\n----\nProcessing FFmpeg...\n----\n\n");
		return exec(abortedCode, new FFmpegCallback(status, flag, watch));
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
		void doEveryLoop(String e);
	}

	/**
	 * ffmpeg を実行する　アボート出来ない。
	 * @param callback
	 * @return
	 */
	public int exec(Callback callback){
		ProcessBuilder pb = null;
		Process process = null;
		BufferedReader ebr = null;
		try {
	//		process = Runtime.getRuntime().exec(getCmd());
			pb = new ProcessBuilder(getCmdArrayList());
			pb.redirectErrorStream(true);
			process = pb.start();
			ebr = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String e;
			while ((e = ebr.readLine()) != null) {
				callback.doEveryLoop(e);
			}
			process.waitFor();
			return process.exitValue();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
			return -1;
		} catch (IOException ex) {
			ex.printStackTrace();
			return -1;
		} finally {
			try {
				ebr.close();
	//			process.getErrorStream().close();
				process.getInputStream().close();
			} catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}

	public Aspect getAspect(File videoFile) {
		final StringBuffer output = new StringBuffer();

		class GetAspectCallback implements Callback {
			@Override
			public void doEveryLoop(String e) {
				if (e.indexOf("Video:") >= 0) {
					System.out.println(" " + e.trim());
					output.append(e.trim() + "\n");
				}
			}
		}

		long width;
		long height;
		if (Cws2Fws.isFws(videoFile)){	// swf, maybe NMM
			FileInputStream fis = null;
			System.out.println("get aspect from FWS(swf)");
			try {
				fis = new FileInputStream(videoFile);
				BitReader br = new BitReader(fis);
				int bit = (int)br.readBit(32);	// "FWS" + version, dummy
					bit = (int)br.readBit(32);	// file size, dummy
					bit = (int)br.readBit(5);	// RECT bits spec
				width =	 br.readBit(bit);		// xmin is 0
				width =  br.readBit(bit);		// xmax is width
				width /= 20;	// From swip to pixel
				height = br.readBit(bit);		// ymin is 0
				height = br.readBit(bit);		// ymax is height
				height /= 20;	// From swip to pixel
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} finally {
				if (fis != null){
					try {
						fis.close();
					} catch (IOException e) { }
				}
			}
		} else {
			// check by ffmpeg
			// ffmpeg.exe -y -i file
			setCmd("-y -i ");
			addFile(videoFile);
			System.out.println("get aspect: " + getCmd());
			exec(new GetAspectCallback());
			String src = output.toString();
			//
			String duration = "Duration:";
			if(src.contains(duration)){
				int index = src.indexOf("Duration:");
				duration = src.substring(index, src.indexOf(",", index)).trim();
				String tms = "";
				int it = 0;
				index = duration.lastIndexOf(":");	//for min:sec
				if(index < 0){
					it = Integer.parseInt(duration);	//sec
				}else{
					tms = duration.substring(index+1);
					duration = duration.substring(0, index);	//hour:min
					it = Integer.parseInt(tms);	//sec
					index = duration.lastIndexOf(":");	//for hour:min
					if(index < 0){
						it += Integer.parseInt(duration) * 60;	//min
					}else{
						tms = duration.substring(index+1);	//min
						duration = duration.substring(0, index);	//hour
						it += Integer.parseInt(tms) * 60;	//min
						it += Integer.parseInt(duration) * 3600;	//hour
					}
				}
				videoLength  = it;
			}
			//
			src = src.replaceAll("[^0-9x]", "_");
			String[] list = src.split("_+");
			src = "1x1";	// prevent Exception
			for (String s : list){
				if (!s.startsWith("0x") && s.indexOf('x') > 0){
					src = s;
					break;
				}
			}
			list = src.split("x");
			try {
				width = Long.parseLong(list[0]);
				height = Long.parseLong(list[1]);
			} catch(NumberFormatException e){
				e.printStackTrace();
				return null;
			}
		}
		Aspect asp = new Aspect((int)width, (int)height);
		System.out.println(asp.explain());
		return asp;
	}

	int getVideoLength(File videoFile) {
		return videoLength;
	}

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
			if ( y != 0){
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
			return "" + width + ":" + height;
		}
		public boolean equals(Aspect a){
			return aspect == a.aspect;
		}
		public static final Aspect NORMAL = new Aspect(4, 3);
		public static final Aspect WIDE = new Aspect(16, 9);
	}
}
