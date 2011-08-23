package saccubus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.JLabel;

import saccubus.util.BitReader;
import saccubus.util.Cws2Fws;
import saccubus.util.Stopwatch;

public class FFmpeg {

	private final String exePath;
	private StringBuffer sb;
	private String LastFrame = "";
	private String LastError = "�G���[��񂪂���܂���";
	public enum Aspect {
		NORMAL, WIDE,
	}

	public FFmpeg(String path) {
		exePath = path.replace("\\", "/");
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
		String path = file.getPath().replace("\\", "/");
		if (path.indexOf(' ') >= 0 || path.indexOf("�@") >= 0){
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

	public interface CallbackInterface {
		public void doEveryLoop(String text);
		public boolean checkStop();
		public void doAbort(String text);
	}

	public int exec(int abortedCode, CallbackInterface callback) {
		LastError = "�G���[��񂪂���܂���";
		LastFrame = "";
		Process process = null;
		BufferedReader ebr = null;
		try {
		//	System.out.println("\n\n----\nProcessing FFmpeg...\n----\n\n");
			process = Runtime.getRuntime().exec(getCmd());
			ebr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String e;
			while ((e = ebr.readLine()) != null) {
				callback.doEveryLoop(e);
				Stopwatch.show();
				if (e.startsWith("frame=")) { //
					LastFrame = e;
				} else {
					LastError = e;
				}
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
				process.getErrorStream().close();
				ebr.close();
			} catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}

	public int exec(JLabel status, int abortedCode, ConvertStopFlag flag) {
		class FFmpegCallback implements CallbackInterface {
			private JLabel status;
			private ConvertStopFlag flag;

			public FFmpegCallback(JLabel status, ConvertStopFlag flag){
				this.status = status;
				this.flag = flag;
			}
			@Override
			public boolean checkStop() {
				return flag.needStop();
			}
			@Override
			public void doAbort(String e) {
				status.setText("ffmpeg�̎��s�𒆎~���܂����B");
			}
			@Override
			public void doEveryLoop(String e) {
				if (e.startsWith("frame=")) { //
					status.setText(e);
				} else {
					if(!e.endsWith("No accelerated colorspace conversion found")){
						System.out.println(e);
					}
				}
			}
		}

		System.out.println("\n\n----\nProcessing FFmpeg...\n----\n\n");
		return exec(abortedCode, new FFmpegCallback(status, flag));
	}
/*
		LastError = "�G���[��񂪂���܂���";
		LastFrame = "";
		Process process = null;
		BufferedReader ebr = null;
		try {
			System.out.println("\n\n----\nProcessing FFmpeg...\n----\n\n");
			process = Runtime.getRuntime().exec(getCmd());
			ebr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String e;
			while ((e = ebr.readLine()) != null) {
				if (e.startsWith("frame=")) { //
					status.setText(e);
					LastFrame = e;
					Stopwatch.show();
				} else {
					LastError = e;
					if(!e.endsWith("No accelerated colorspace conversion found")){
						System.out.println(e);
					}
				}
				if (flag.needStop()) {
					process.destroy();
					status.setText("ffmpeg�̎��s�𒆎~���܂����B");
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
				process.getErrorStream().close();
				ebr.close();
			} catch(Exception ex){
				ex.printStackTrace();
			}
		}
*/

	public String getLastFrame() {
		return LastFrame;
	}

	public String getLastError() {
		return LastError;
	}

	public Aspect getAspect(File videoFile, StringBuffer sb) {
		final StringBuffer output = new StringBuffer();

		class GetAspectCallback implements CallbackInterface {
			@Override
			public void doEveryLoop(String e) {
				if (e.indexOf("Video") >= 0) {
					System.out.println(e);
					output.append(e.trim() + "\n");
				}
			}
			@Override
			public boolean checkStop() {
				return false;
			}
			@Override
			public void doAbort(String e) {
			// nothig
			}
		}

		long width;
		long height;
		final double aspect;
		if (Cws2Fws.isFws(videoFile)){	// swf, maybe NMM
			FileInputStream fis = null;
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
			int code = exec(-9, new GetAspectCallback());
			if (code != 0){
				// don't care, because error must have occurred
			}
			String src = output.toString();
			src = src.replaceAll("[^0-9x]", "_");
			String[] list = src.split("_+");
			src = "1x1";	// prevent Exception
			for (String s : list){
				if (s.indexOf('x') > 0){
					src = s;
					break;
				}
			}
			list = src.split("x");
			try {
				width = Long.parseLong(list[0]);
				height = Long.parseLong(list[1]);
			} catch(NumberFormatException e){
				return null;
			}
		}
		sb.append("(" + width + "x" + height + ")");
		aspect = (double)width / (double)height;
		sb.append(String.format("%.3f", aspect));
		System.out.println("width hight:" + width + "x" + height
				+ ", aspect: " + String.format("%.3f", aspect));
		if (aspect > 1.7){				// 1.33 or 1.77
			return Aspect.WIDE;
		} else {
			return Aspect.NORMAL;
		}
	}
}
