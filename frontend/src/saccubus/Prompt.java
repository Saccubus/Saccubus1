package saccubus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import javax.swing.JPanel;

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
	private static ConvertStopFlag cuiStop;
	private static JButton stopButton;
	private static ConvertManager manager;
	private static JLabel[] status3;
	private static ConvertWorker converter;
	private static StringBuffer sbReturn;
	private static int convNo;
	private static File localListFile;
	private static String watchinfo;
	private static ArrayList<ConvertStopFlag> flags = new ArrayList<ConvertStopFlag>();
	private static HistoryDeque<File> playList;
	private static StringBuffer errorList;
	private static boolean aborted = false;

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
			prop.setProperty(ConvertingSetting.PROP_ZQ_OPTION_FILE,
				optionFilePrefix + ConvertingSetting.PROP_ZQ_OPTION_FILE);
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
		errorList = setting.getErrorList();
		JLabel status = new JLabel();
		JLabel info = new JLabel();
		JLabel watch = new JLabel();
		status3 = new JLabel[]{status, info, watch};
		stopButton = new JButton();
		cuiStop = new ConvertStopFlag(stopButton, "停止", "待機", "終了", "変換", false);
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized(cuiStop){
					cuiStop.stop();
					cuiStop.notify();
				}
			}
		});
		Window popup = new Window(null);
		popup.setSize(400, 20);
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
		sbReturn = new StringBuffer(16);

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
		System.out.println();
		System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
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

		manager = new ConvertManager(null);
		playList = new HistoryDeque<File>(null);
		String url = MainFrame.treatUrlHttp(tag);
		boolean isMylist = url.startsWith("http");
		index = url.indexOf('#');
		if(index >= 0){
			url = url.replace("#+", "?").replace("#/", "?");
		}
		watchinfo = "";
		if(isMylist){
			tag = url;
		}else{
			index = tag.indexOf('?');
			if(index >= 0){
				int index2 = url.lastIndexOf('/',index);
				tag = url.substring(index2+1,index);
				watchinfo = url.substring(index);
			}else{
				int index2 = url.lastIndexOf('/');
				tag = url.substring(index2+1);
			}
		}
		System.out.println("Tag:"+tag+" watchinfo="+watchinfo);
		String text;
		if(!tag.startsWith("auto") && !isMylist){
			converter = manager.request(
					-1,
					setting.getNumThread(),
					tag+watchinfo,
					time,
					setting,
					status3,
					cuiStop,
					null,
					playList,
					sbReturn);
			while(converter!=null && !converter.isDone()){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// e.printStackTrace();
					// continue;
				}
			}
			popup.dispose();

			System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
			System.out.println("Finished.");
			System.out.println();
			String[] ret = sbReturn.toString().split("\n");
			int code = 0;
			for(int l=0;l<ret.length;l++){
				System.out.println(ret[l]);
				String[] s = ret[l].split("=");
				if("RESULT".equals(s[0]) && !"0".equals(s[1])){
					code = Integer.parseInt(s[1],16);
				}
			}
			exit(code);
		}else{
			if (isMylist){
				// "http://www/nicovideo.jp/mylist/1234567?watch_harmful=1" など
				MylistGetter mylistGetter = new MylistGetter(
					-1,
					tag,
					watchinfo,
					setting,
					status3,
					cuiStop,
					sbReturn);
				mylistGetter.execute();
				cuiStop.go();		//mylistGetterは無条件に実行
				int count = 0;
				while(mylistGetter!=null && !mylistGetter.isDone()){
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// e.printStackTrace();
					}
					if(++count > 120){
						System.out.println("Error: マイリストが取得できません(１分経過)");
						exit(251);
					}
				}
				text = status3[0].getText();
				if(text==null||!text.equals("[00]")){
					System.out.println("Error: result="+text);
					exit(252);
				}
				text = sbReturn.substring(0);
			} else {
				// auto
				localListFile= new File( tag + ".txt");
				if(!localListFile.exists()){
					System.out.println("Error: "+localListFile.getAbsolutePath()+"がありません .");
					exit(253);
				}
				text = Path.readAllText(localListFile, "MS932");
				if(text.isEmpty()){
					System.out.println("Error: "+localListFile.getAbsolutePath()+"に動画がありません. 書式が違っていないか確認して下さい");
					exit(254);
				}
			}
			String[] lists = text.split("\n");
			int nConvert = lists.length;
			JPanel activityPane = new JPanel();
			ConvertWorker[] converterList = new ConvertWorker[nConvert];
			StringBuffer[] sbRetList = new StringBuffer[nConvert];
			String[] vids = new String[nConvert];
			stopButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					AllCancel_ActionHandler(e);
				}
			});
			for(int k=0; k<lists.length; k++){
				String id_title = lists[k];
				if(id_title.isEmpty()) continue;
				String[] ss = id_title.split("\\t");
				String vid = MainFrame.treatUrlHttp(ss[0]);
				if(vid.trim().isEmpty()||vid.charAt(0)==':') continue;
				// idを登録
				ListInfo listInfo = new ListInfo(vid+"\tauto", true);
				listInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
				activityPane.add(listInfo);
				int indexNow = convNo++;
				System.out.println(">"+indexNow+" "+vid+watchinfo);
				// ConverManager処理を要求
				StringBuffer sbRet = new StringBuffer();
				final ConvertStopFlag autoStop = new ConvertStopFlag(new JButton(), "停止", "待機", "終了", "変換", false);
				converter = manager.request(
					indexNow,
					setting.getNumThread(),
					vid + watchinfo,
					time,
					setting,
					status3,
					autoStop,
					null,
					playList,
					sbRet);
				converterList[indexNow] = converter;
				sbRetList[indexNow] = sbRet;
				vids[indexNow] = vid;
				flags.add(autoStop);
				// return to dispatch
	 		}

			int codes = 0;
			int code = 0;
			while(manager.getNumRun()>0){
				for(int j = 0; j<convNo;j++){
					ConvertWorker conv = converterList[j];
					if(conv!=null && conv.isDone()){
						try{
							System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
							System.out.println("Finished. ("+j+") "+vids[j]);
							if(sbRetList[j]==null){
								System.out.println("エラー：ret=null");
								continue;
							}
							String[] ret = sbRetList[j].toString().split("\n");
							code = 0;
							for(int l=0;l<ret.length;l++){
								System.out.println(ret[l]);
								String[] s = ret[l].split("=");
								if("RESULT".equals(s[0]) && !"0".equals(s[1])){
									code = Integer.parseInt(s[1],16);
								}
							}
						}catch(Exception e1){
							e1.printStackTrace();
							code = -999;
						}finally{
							if(code!=0 && codes==0) codes = code;	//最初のエラーコード
							converterList[j] = null;
						}
					}
				}
			}
			if(aborted){
				code = 255;
				System.out.println("中止\nRESULTS="+code);
			}else{
				System.out.println("正常終了\nRESULTS="+code);
			}
			if(code!=0){
				MainFrame.errorListSave(errorList);
				System.out.println("エラーがありました");
			}
			exit(code);
		}
	}

	private static void AllCancel_ActionHandler(ActionEvent e) {
		for(ConvertStopFlag flag:flags){
			if(flag!=null){
				manager.gotoCancel(flag);
			}
		}
		manager.cancelAllRequest();
		manager.queueCheckAndGo();
		stopButton.setEnabled(false);
		aborted  = true;
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
}
