package saccubus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

import javax.swing.JButton;
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
	private static final String LOGFILE2 = ".\\sacclog.txt";
	private static DuplicatedOutput dout = null;
	private static String logname = LOGFILE;
	private static int maxsize = 1000000;
	private static boolean enablePupup = false;
	private static HashMap<String, String> settingMap = new HashMap<String, String>(16);
	private static HashMap<String, String> optionMap = new HashMap<String, String>(16);
	private static HashMap<String,String> downloadMap = new HashMap<String, String>(16);
	private static String optionFilePrefix = "";
	private static ConvertingSetting setting;
	private static String propFile = ConvertingSetting.PROP_FILE;
	private static String addPropFile = "";
	private static Properties prop;

	public static void main(String[] args) {
		if(!setLog(logname)){
			if(!setLog(LOGFILE2)){
				exit(1);
			}
		}
		System.out.println(WayBackDate.formatNow());
		if (args.length < 3){
			System.out.println("Error. MailAddress, Password, VideoID must be specified.");
			exit(2);
		}
		String mail = "";
		String pass = "";
		String tag = "";
		String time = "";
		ArrayList<String> atArgs = new ArrayList<String>();
		int i = 0;
		for(String arg : args){
			if(arg.startsWith("@") || i>3){
				atArgs.add(arg);
			}else{
				switch(i){
				case 0:	mail = arg;	i++;	break;
				case 1:	pass = arg;	i++;	break;
				case 2:	tag = arg;	i++;	break;
				case 3:	time = arg;	i++;	break;
				}
			}
		}
	//	mail = args[0];
	//	pass = args[1];
	//	tag = args[2];
	//	time = args.length < 4 ? "" : args[3];
		if (mail.isEmpty() || pass.isEmpty() || tag.isEmpty()){
			System.out.println("Error. MailAddress, Password, VideoID must be specified.");
			exit(2);
		}
		int index;
		String key, value;
		for (String arg : atArgs){
			if(arg == null || arg.isEmpty()){
				continue;
			}
			if(arg.startsWith("#") || arg.startsWith("//")){
				break;
			}
			if(arg.equals("@NDL")){
				downloadMap.put(ConvertingSetting.PROP_SAVE_VIDEO, "false");
				downloadMap.put(ConvertingSetting.PROP_SAVE_COMMENT, "false");
				System.out.println("Set No Download.");
				continue;
			}
			if(arg.equals("@DLO")){
				downloadMap.put(ConvertingSetting.PROP_SAVE_VIDEO, "true");
				downloadMap.put(ConvertingSetting.PROP_SAVE_COMMENT, "true");
				downloadMap.put(ConvertingSetting.PROP_SAVE_CONVERTED,"false");
				System.out.println("Set Download Only.");
				continue;
			}
			if(arg.equals("@DLC")){
				downloadMap.put(ConvertingSetting.PROP_SAVE_COMMENT, "true");
				downloadMap.put(ConvertingSetting.PROP_SAVE_CONVERTED,"false");
				System.out.println("Set Download Comment Only.");
				continue;
			}
			if(arg.equals("@PUP")){
				enablePupup = true;
				continue;
			}
			if(arg.startsWith("@SET=")){
				propFile  = arg.substring(arg.indexOf('=')+1);
				System.out.println("Set Setting Property File:" + propFile + ".");
				continue;
			}
			if(arg.startsWith("@ADD=")){
				addPropFile = arg.substring(arg.indexOf('=')+1);
				System.out.println("Set Adding Property File:" + addPropFile + ".");
				continue;
			}
			if(arg.startsWith("-") && arg.contains("=")){
				//ffmpeg変換オプション
				index = arg.indexOf('=');
				key = arg.substring(0, index);
				value = arg.substring(index+1);
				optionMap.put(key, value);
				continue;
			}
			if(arg.contains("=")){
				//saccubus.xmlプロパティ
				index = arg.indexOf('=');
				key = arg.substring(0, index);
				value = arg.substring(index+1);
				settingMap.put(key, value);
				continue;
			}
			if(arg.contains(":")){
				//過去ログ時刻の引用符"yyyy/mm/dd hh:MM"なし
				if(time.contains("/")){
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
		prop = ConvertingSetting.loadProperty(propFile, true);
		//option prefix 設定
		if(!optionFilePrefix.isEmpty()){
			prop.setProperty(ConvertingSetting.PROP_OPTION_FILE,
				optionFilePrefix+ConvertingSetting.PROP_OPTION_FILE);
			prop.setProperty(ConvertingSetting.PROP_WIDE_OPTION_FILE,
				optionFilePrefix+ConvertingSetting.PROP_WIDE_OPTION_FILE);
		}
		//settingMap 設定
		for(Entry<String, String> e : settingMap.entrySet()){
			prop.setProperty(e.getKey(), e.getValue());
		}
		//downloadMap 設定
		for(Entry<String, String> e : downloadMap.entrySet()){
			prop.setProperty(e.getKey(), e.getValue());
		}
		setting = ConvertingSetting.loadSetting(mail, pass, prop);
		//@ADD設定
		if(!addPropFile.isEmpty()){
			ConvertingSetting.addSetting(setting, addPropFile);
		}
	//	setting.override(optionFilePrefix, settingMap, optionMap);
		//optionMap 設定
		if(!optionMap.isEmpty()){
			setting.setReplaceOptions(optionMap);
		}
		JLabel status = new JLabel();
		JLabel info = new JLabel();
		JLabel watch = new JLabel();
		JButton stopButton = new JButton();
		final ConvertStopFlag cuiStop = new ConvertStopFlag(stopButton, "停止", "待機", "終了");
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cuiStop.stop();
			}
		});
		Window popup = new Window(null);
		popup.setSize(360, 20);
		popup.setLocation(0,0);
		popup.setLayout(new BorderLayout(5, 0));
		Color fg = Color.black;
		Color bg = Color.lightGray;
		popup.setForeground(fg);
		popup.setBackground(bg);
		stopButton.setForeground(fg);
		stopButton.setBackground(bg);
		stopButton.setSize(25,15);
		stopButton.setBounds(0, 0, 25, 15);
		stopButton.setVisible(true);
		status.setForeground(fg);
		status.setBackground(bg);
		status.setVisible(true);
		popup.add(stopButton,BorderLayout.WEST);
		popup.add(status, BorderLayout.CENTER);
		popup.setVisible(enablePupup);
		StringBuffer sbReturn = new StringBuffer(16);

		Converter conv = new Converter(tag, time, setting, status, cuiStop,
									   info, watch, sbReturn);		// these three params are extended
		System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		System.out.println("Saccubus on CUI");
		System.out.println();
		System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		System.out.println("Mailaddr: " + mail);
		System.out.println("Password: hidden");
		System.out.println("VideoID: " + tag);
		System.out.println("WaybackTime: " + time);
		if(!optionFilePrefix.isEmpty()){
			System.out.println("OptionPrefix: " + optionFilePrefix);
		}
		if(!atArgs.isEmpty()){
			System.out.print("Other args:");
			for(String arg : atArgs){
				System.out.print(" " + arg);
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
		popup.dispose();
		// System.out.println("LastStatus: " + status.getText());
		// System.out.println("VideoInfo: " + info.getText());
		// System.out.println("ElapsedTime: " + watch.getText());
		System.out.println("Finished.");
		System.out.println();
		String[] ret = sbReturn.toString().split("\n");
		for(int l=0;l<ret.length;l++){
			System.out.println(ret[l]);
			String[] s = ret[l].split("=");
			if("RESULT".equals(s[0]) && !"0".equals(s[1])){
				exit(decode(s[1]));
			}
		}
		exit(0);
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
