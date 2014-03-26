package saccubus;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JLabel;

public class VPlayer extends Thread {
	private final File playVideo;
	private final JLabel status;
	private String exitMsg;

	public VPlayer(File playvideo, JLabel stat) {
		playVideo = playvideo;
		status = stat;
	}

	public String play(){
		ArrayList<String> cmd = new ArrayList<String>();
		cmd.add("cmd.exe");
		cmd.add("/C");
		cmd.add("\""+playVideo.getAbsolutePath()+"\"");
		System.out.println(cmd.toString());
		ProcessBuilder pb = null;
		Process process = null;
		BufferedReader ebr = null;
		try {
			pb = new ProcessBuilder(cmd);
			pb.redirectErrorStream(true);
			process = pb.start();
			ebr = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String e;
			String lastout = "";
			while ((e = ebr.readLine()) != null) {
				lastout = e;
				if (interrupted()) {
					process.destroy();
					return "�Đ������~����܂���";
				}
			}
			process.waitFor();
			int ret = process.exitValue();
			if(ret!=0)
				return "(" + ret + ")�Đ����G���[���܂���:" + lastout;
		} catch(NullPointerException ex){
			ex.printStackTrace();
			return "(�L�́M)���ʂ��\n�K�b\n";
		} catch (InterruptedException ex) {
			ex.printStackTrace();
			return ex.getMessage();
		} catch (IOException ex) {
			ex.printStackTrace();
			return ex.getMessage();
		} finally {
			try {
				ebr.close();
				process.getInputStream().close();
			} catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return "";
	}
	
	@Override
	public void run(){
		sendtext("�Đ���:"+playVideo.getName());
		exitMsg = play();
		System.out.println(exitMsg);
		sendtext(exitMsg);
	}
	
	public String getExitMsg(){
		return exitMsg;
	}

	private void sendtext(String text) {
		synchronized (status) {
			status.setText(text);
		}
	}

}
