package saccubus;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JLabel;

import saccubus.util.Stopwatch;

public class FFmpeg {

	private final String exePath;
	private StringBuffer sb;
	private String LastFrame = "";
	private String LastError = "エラー情報がありません";

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
		addQuote(file.getPath());
	}

	void addQuote(String string) {
		sb.append(" \"");
		sb.append(string.replace("\\", "/"));
		sb.append("\"");
	}

	public String getCmd() {
		return sb.substring(0);
	}

	public int exec(JLabel status, int abortedCode, ConvertStopFlag flag) {
		LastError = "エラー情報がありません";
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
					status.setText("中止しました。");
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

	public String getLastFrame() {
		return LastFrame;
	}

	public String getLastError() {
		return LastError;
	}

}
