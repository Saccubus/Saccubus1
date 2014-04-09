package saccubus;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JLabel;

public class VPlayer extends Thread {
	private final File playVideo;
	private final JLabel status;
	private String exitMsg;
	private static AtomicInteger playCount = new AtomicInteger(0);
 
	public VPlayer(File playvideo, JLabel stat) {
		playVideo = playvideo;
		status = stat;
	}

	public String play(){
		String videoPath = playVideo.getAbsolutePath();
		//���p�X�y�[�X�̏ꍇ�̓V�X�e�����������邪�S�p�X�y�[�X�͂������ŏ������Ȃ��ƃ_��
		//���p���ň͂�
		if((videoPath.contains("�@") && !videoPath.contains(" "))
			&& !videoPath.startsWith("\"")){
			videoPath = "\"" + videoPath + "\"";
		}
		//Windows�t�@�C�����Ɏg���邪�R�}���h�v�����v�g�ł̓G�X�P�[�v���Ȃ��ƃ_���ȕ���()^&;�y�ёS�p���p�X�y�[�X
		//�R�}���h�v�����v�g�̃G�X�P�[�v��^�ł���
		//()^�͐��K�\������\�ŃG�X�P�[�v���Ȃ��Ƃ���
		//\��Java�̃��e�����ł�\\�Ə����Ȃ��ƃ_���@�ʓ|����
		videoPath = videoPath.replaceAll("([\\(\\)\\^&; �@])","^$1");
		ArrayList<String> cmd = new ArrayList<String>();
		cmd.add("cmd.exe");
		cmd.add("/C");
		cmd.add(videoPath);
		System.out.println(cmd.toString());
		ProcessBuilder pb = null;
		Process process = null;
		BufferedReader ebr = null;
		int ret = -1;
		try {
			pb = new ProcessBuilder(cmd);
			pb.redirectErrorStream(true);
			process = pb.start();
			ebr = new BufferedReader(new InputStreamReader(process.getInputStream()));
			process.waitFor();
			String lastout = ebr.readLine();
			ret = process.exitValue();
			if(ret!=0)
				return "(" + ret + ")�Đ��G���[:" + lastout;
			return "okay";
		} catch(NullPointerException ex){
			ex.printStackTrace();
			return "(�L�́M)���ʂ��\n�K�b\n";
		} catch (InterruptedException ex) {
			//ex.printStackTrace();
			return "�Đ����f";
		} catch (IOException ex) {
			ex.printStackTrace();
			return "IO��O"+ex.getMessage();
		} finally {
			try {
				if(process!=null){
					process.destroy();
				}
				ebr.close();
				process.getInputStream().close();
			} catch(Exception ex){
				//ex.printStackTrace();
			}
		}
	}
	
	@Override
	public void run(){
		playCount.incrementAndGet();
		sendtext("�Đ�:"+playVideo.getName());
		String msg = play();
		synchronized (playCount) {
			int ct = playCount.decrementAndGet();
			if(ct==0)
				sendtext(msg);
		}

	}
	
	public String getExitMsg(){
		return exitMsg;
	}

	private void sendtext(String text) {
		synchronized (status) {
			System.out.println(text);
			status.setText(text);
		}
	}

}
