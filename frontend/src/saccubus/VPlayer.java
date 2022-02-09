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
	private String exitMsg = "";
	private static AtomicInteger playCount = new AtomicInteger(0);

	public VPlayer(File playvideo, JLabel stat) {
		playVideo = playvideo;
		status = stat;
	}

	public String play(){
		String videoPath = playVideo.getAbsolutePath();
		//半角スペースの場合はシステムが処理するが全角スペースはこっちで処理しないとダメ
		//引用符で囲む
		if((videoPath.contains("　") && !videoPath.contains(" "))
			&& !videoPath.startsWith("\"")){
			videoPath = "\"" + videoPath + "\"";
		}
		//Windowsファイル名に使えるがコマンドプロンプトではエスケープしないとダメな文字()^&;,及び全角半角スペース	"()^&;, 　"
		//コマンドプロンプトのエスケープは^である				"()^&;, 　" -> "^(^)^^^&^;^,^ ^　"
		//()^は正規表現内で\でエスケープしないとだめ			"([\(\)\^&;, 　])" -> "^$1"
		//\はJavaのリテラルでは\\と書かないとダメ、面倒いｗ		"([\\(\\)\\^&;, 　])" -> "^$1"
		videoPath = videoPath.replaceAll("([\\(\\)\\^&;, 　=])","^$1");
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
				return "(" + ret + ")再生エラー:" + lastout;
			return "okay";
		} catch(NullPointerException ex){
			ex.printStackTrace();
			return "(´∀｀)＜ぬるぽ\nガッ\n";
		} catch (InterruptedException ex) {
			//ex.printStackTrace();
			return "再生中断";
		} catch (IOException ex) {
			ex.printStackTrace();
			return "IO例外"+ex.getMessage();
		} finally {
			try {
				if(process!=null){
					process.destroy();
					ebr.close();
					process.getInputStream().close();
				}
			} catch(Exception ex){
				//ex.printStackTrace();
			}
		}
	}

	@Override
	public void run(){
		playCount.incrementAndGet();
		sendtext("再生:"+playVideo.getName());
		String msg = play();
//		synchronized (playCount) {
			int ct = playCount.decrementAndGet();
			if(ct==0)
				sendtext(msg);
//		}

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
