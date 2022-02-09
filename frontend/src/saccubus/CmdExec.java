package saccubus;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

public class CmdExec extends Thread {
	private final File execFile;
	private String exitMsg = "";
	private String Video;

	public CmdExec(File exec_file, String video) {
		execFile = exec_file;
		Video = video;
	}

	public String exec(){
		ArrayList<String> cmd = new ArrayList<String>();
		cmd.add("cmd.exe");
		cmd.add("/C");
		cmd.add(execFile.getPath());
		System.out.println(cmd.toString());
		ProcessBuilder pb = null;
		Process process = null;
		BufferedReader ebr = null;
		int ret = -1;
		try {
			pb = new ProcessBuilder(cmd);
			Map<String,String> env = pb.environment();
			env.put("VIDEO", "\""+Video+"\"");
			pb.redirectErrorStream(true);
			process = pb.start();
			ebr = new BufferedReader(new InputStreamReader(process.getInputStream()));
			process.waitFor();
			String lastout = ebr.readLine();
			ret = process.exitValue();
			if(ret!=0)
				return "(" + ret + ")エラー:" + lastout;
			return "okay";
		} catch(NullPointerException ex){
			ex.printStackTrace();
			return "NullPo";
		} catch (InterruptedException ex) {
			//ex.printStackTrace();
			return "Interrupt";
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
		exec();
	}

	public String getExitMsg(){
		return exitMsg;
	}
}
