package saccubus;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Hashtable;

import javax.swing.JLabel;

import saccubus.net.Path;
import saccubus.util.DuplicatedOutput;

/**
 * <p>
 * タイトル: さきゅばす
 * </p>
 *
 * <p>
 * 説明: ニコニコ動画の動画をコメントつきで保存
 * </p>
 *
 * <p>
 * 著作権: Copyright (c) 2007 PSI
 * </p>
 *
 * <p>
 * 会社名:
 * </p>
 *
 * @author 未入力
 * @version 1.0
 */
public class Prompt {
	private static final String LOGFILE = ".\\log.txt";
	private static DuplicatedOutput dout = null;
	private static String logname = LOGFILE;
	private static int maxsize = 1000000;
	private static Hashtable<String, String> settingMap = new Hashtable<String, String>(16);
	private static Hashtable<String, String> optionMap = new Hashtable<String, String>(16);
	private static String optionFilePrefix = "";
	private static ConvertingSetting setting;
	public static void main(String[] args) {
		if(!setLog(logname)){
			exit(1);
		}
		System.out.println(WayBackDate.formatNow());
		if (args.length < 3){
			System.out.println("Error. MailAddress, Password, VideoID must be specified.");
			exit(2);
		}
		String mail = args[0];
		String pass = args[1];
		String tag = args[2];
		String time = args.length < 4 ? "" : args[3];
		int index;
		String arg, key, value;
		for (int i = 4; i< args.length; i++){
			arg = args[i];
			if(arg == null || arg.isEmpty()){
				continue;
			}
			if(arg.startsWith("#") || arg.startsWith("//")){
				break;
			}
			if(arg.startsWith("-") && arg.contains("=")){
				index = arg.indexOf('=');
				key = arg.substring(1, index);
				value = arg.substring(index+1);
				optionMap.put(key, value);
				continue;
			}
			if(arg.contains("=")){
				index = arg.indexOf('=');
				key = arg.substring(0, index);
				value = arg.substring(index+1);
				settingMap.put(key, value);
				continue;
			}
			if(arg.contains(":")){
				if(time.contains(":")){
					time = time.trim() + " " + arg.trim();
				}
				continue;
			}
			//just a word -> prefix of selected option filename
			if(optionFilePrefix.isEmpty() && Character.isLetter(arg.charAt(0))){
				optionFilePrefix = arg.trim();
				continue;
			}
			System.out.println("Undefined Argument: <" + arg + ">");
		}
		setting = ConvertingSetting.loadSetting(mail, pass);
		setting.override(optionFilePrefix, settingMap, optionMap);
		JLabel status = new JLabel();
		JLabel info = new JLabel();
		JLabel watch = new JLabel();
		Converter conv = new Converter(tag, time, setting, status, new ConvertStopFlag()
										, info, watch);		// these two params are extended
		System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		System.out.println("Saccubus on CUI");
		System.out.println();
		System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		System.out.println("Mailaddr: " + mail);
		System.out.println("Password: hidden");
		System.out.println("VideoID: " + tag);
		System.out.println("WaybackTime: " + time);
		if(!optionFilePrefix.isEmpty()){
			System.out.println("OprionPrefix: " + optionFilePrefix);
		}
		if(args.length > 4){
			System.out.print("Other args:");
			for(int i = 4; i < args.length; i++){
				if(!args[i].equals(optionFilePrefix)){
					System.out.print(" " + args[i]);
				}
			}
			System.out.println();
		}
		System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		System.out.println("Version " + MainFrame_AboutBox.rev );
		conv.start();
		try {
			conv.join();
		} catch (InterruptedException e) {
			// e.printStackTrace();
			// continue;
		}
		// System.out.println("LastStatus: " + status.getText());
		// System.out.println("VideoInfo: " + info.getText());
		// System.out.println("ElapsedTime: " + watch.getText());
		System.out.println("Finished.");
	}

	private static int getLogsize(){
		String logsize = System.getenv("logsize");
		if(logsize != null && !logsize.isEmpty()){
			return decode(logsize);
		}
		return maxsize;
	}
	private static int decode(String str) {
		if(str == null || str.isEmpty()){
			return maxsize;
		}
		str = str.toLowerCase();
		int value = 1;
		int len = str.length();
		char suffix = str.charAt(len - 1);
		if(suffix == 'k'){
			value = 1000;
			str = str.substring(0, len - 1);
		}else if(suffix == 'm'){
			value = 1000000;
			str = str.substring(0, len - 1);
		}
		try{
			value *= Integer.decode(str);
		}catch(NumberFormatException e){
			e.printStackTrace();
			value = maxsize;
		}
		return value;
	}

	private static void exit(int status) {
		if(dout != null){
			dout.flush();
			dout.close();
		}
		System.exit(status);
	}

	private static boolean setLog(String path) {
		File log = new File(path);
		if(log.exists() && log.canRead()){
			String text = Path.readAllText(path, "MS932");
			int len = text.length();
			if(len > getLogsize()){
				text = text.substring(len - getLogsize());
				if(log.delete()){
					try {
						PrintStream ps = new PrintStream(log);
						ps.print(text);
						ps.flush();
						ps.close();
						dout = new DuplicatedOutput(log);
						//PrintStream ps = new PrintStream(new FileOutputStream(log, true));
						System.setErr(dout.dup(System.err));
						System.setOut(dout.dup(System.out));
						return true;
					} catch (IOException e) {
						e.printStackTrace();
						return false;
					}
				}
			}
		}
		if(!log.exists() || log.canWrite()){
			try {
				dout = new DuplicatedOutput(log);
				//PrintStream ps = new PrintStream(new FileOutputStream(log, true));
				System.setErr(dout.dup(System.err));
				System.setOut(dout.dup(System.out));
				return true;
			} catch (IOException e1) {
				e1.printStackTrace();
				return false;
			}
		}
		return true;
	}

/*
	@SuppressWarnings("unused")
	private static boolean setLog(String path, int size) {
		File log = new File(path);
		if(log.exists() && !log.canRead()){
			// Already opened as WRITE, Maybe.
			return true;
		}
		if(dout == null){
			System.out.println("Log Bug?");
			exit(9);
		}
		dout.flush();
		dout.close();
		long len;
		long skiplen = 0;
		if(log.canRead()){
			len = log.length();
			if(len > maxsize){
				skiplen = len - (maxsize + 128);
			}
			BufferedReader br;
			String line;
			StringBuffer sb;
			try {
				br = new BufferedReader(new FileReader(log));
				line = null;
				sb = new StringBuffer();
				if(skiplen > 0){
					br.skip(skiplen);
					line = br.readLine();
				}
				while((line = br.readLine())!= null){
					sb.append(line + "\n");
				}
				br.close();
				if(!log.delete()){
					// Already opened as WRITE, Maybe.
					return true;
				}
				if(!setLog(path)){
					return false;
				}
				PrintStream fps = dout.getFilePrintStream();
				line = sb.toString();
				fps.print(line);
				fps.flush();
				fps.close();
				System.out.println("Previous Log truncated.");
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		return setLog(path);
	}
*/
}
