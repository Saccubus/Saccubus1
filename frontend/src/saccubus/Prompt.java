package saccubus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JLabel;

import saccubus.net.Path;
import saccubus.util.Logger;

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
	private static AutoPlay autoPlay;
	private static ErrorControl errorControl;
	private static boolean aborted = false;
	private static ArrayList<String> retryList = new ArrayList<>();
	private static Logger log;

	public static void main(String[] args){
		Saccubus.initEnv();
		int code = 0;
		do {
			code = main1(args.clone());
		}while(code==0x98);
		System.exit(code);
	}

	public static int main1(String[] args) {
		log = setLog(logname);
		log.println(WayBackDate.formatNow());
		if (args.length < 3){
			log.println("Error. MailAddress, Password, VideoID must be specified.");
			return exit(2);
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
			log.println("Error. MailAddress, Password, VideoID must be specified.");
			return exit(2);
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
				log.println("Set No Download.");
				continue;
			}
			if(arg.equals("@DLO")){
				downloadMap.put(ConvertingSetting.PROP_SAVE_VIDEO, "true");
				downloadMap.put(ConvertingSetting.PROP_SAVE_COMMENT, "true");
				downloadMap.put(ConvertingSetting.PROP_SAVE_CONVERTED,"false");
				log.println("Set Download Only.");
				continue;
			}
			if(arg.equals("@DLC")){
				downloadMap.put(ConvertingSetting.PROP_SAVE_COMMENT, "true");
				downloadMap.put(ConvertingSetting.PROP_SAVE_CONVERTED,"false");
				log.println("Set Download Comment Only.");
				continue;
			}
			if(arg.equals("@PUP")){
				enablePupup = true;
				continue;
			}
			if(arg.startsWith("@SET=")){
				propFile  = arg.substring(arg.indexOf('=')+1);
				log.println("Set Setting Property File:" + propFile + ".");
				continue;
			}
			if(arg.startsWith("@ADD=")){
				addPropFile = arg.substring(arg.indexOf('=')+1);
				log.println("Set Adding Property File:" + addPropFile + ".");
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
			log.println("Undefined Argument: <" + arg + ">");
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
		errorControl = new ErrorControl("");
		JLabel status = new JLabel();
		JLabel info = new JLabel();
		JLabel watch = new JLabel();
		status3 = new JLabel[]{status, info, watch, new JLabel()};
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

		log.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		log.println("Saccubus on CUI");
		log.println();
		log.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		log.println("Mailaddr: " + mail);
		log.println("Password: hidden");
		log.println("VideoID: " + tag);
		log.println("WaybackTime: " + time);
		if(!optionFilePrefix.isEmpty()){
			log.println("OptionPrefix: " + optionFilePrefix);
		}
		if(!atArgs.isEmpty()){
			log.print("Other args:");
			for(String arg : atArgs){
				log.print(" " + arg);
			}
			log.println();
		}
		log.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		log.println("Version " + MainFrame_AboutBox.rev );
		log.println();

		manager = new ConvertManager(null);
		manager.start();
		autoPlay = new AutoPlay(setting.isAutoPlay());
		autoPlay.setStatus(status);
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
			index = url.indexOf('?');
			if(index >= 0){
				int index2 = url.lastIndexOf('/',index);
				tag = url.substring(index2+1,index);
				watchinfo = url.substring(index);
			}else{
				int index2 = url.lastIndexOf('/');
				tag = url.substring(index2+1);
			}
		}
		log.println("Tag:"+tag+" watchinfo="+watchinfo);
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
					autoPlay,
					errorControl,
					sbReturn,
					log);
			while(converter!=null && !converter.isDone()){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// e.printStackTrace();
					// continue;
				}
			}
			popup.dispose();

			log.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
			log.println("Finished.");
			log.println();
			String[] ret = sbReturn.toString().split("\n");
			int code = 0;
			for(int l=0;l<ret.length;l++){
				log.println(ret[l]);
				String[] s = ret[l].split("=");
				if("RESULT".equals(s[0]) && !"0".equals(s[1])){
					code = Integer.parseInt(s[1],16);
				}
			}
			return exit(code);
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
					errorControl,
					sbReturn,
					log);
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
						log.println("Error: マイリストが取得できません(１分経過)");
						return exit(251);
					}
				}
				text = status3[0].getText();
				if(text==null||!text.equals("[00]")){
					log.println("Error: result="+text);
					return exit(252);
				}
				text = sbReturn.substring(0);
			} else {
				// auto
				localListFile= new File( tag + ".txt");
				if(!localListFile.exists()){
					log.println("Error: "+localListFile.getAbsolutePath()+"がありません .");
					return exit(253);
				}
				text = Path.readAllText(localListFile, "MS932");
				if(text.isEmpty()){
					log.println("Error: "+localListFile.getAbsolutePath()+"に動画がありません. 書式が違っていないか確認して下さい");
					return exit(254);
				}
			}
			String[] lists = text.split("\n");
			//int nConvert = lists.length;
			ActivityControl activities = new ActivityControl();
			ArrayList<ConvertWorker> converterList = new ArrayList<>();
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
				int indexNow = convNo++;
				JButton jbutton = new JButton();
				final ConvertStopFlag autoStop = new ConvertStopFlag(jbutton, "停止", "待機", "終了", "変換", false);
				ListInfo listInfo = new ListInfo(vid+"\tauto", true, indexNow,
					jbutton, autoStop);
				activities.add(listInfo);
				log.println(">"+indexNow+" "+vid+watchinfo);
				// ConverManager処理を要求
				StringBuffer sbRet = new StringBuffer();
				converter = manager.request(
					indexNow,
					setting.getNumThread(),
					vid + watchinfo,
					time,
					setting,
					status3,
					autoStop,
					null,
					autoPlay,
					errorControl,
					sbRet,
					log);
				converterList.add(converter);
				flags.add(autoStop);
				// return to dispatch
	 		}

			int codes = 0;
			int code = 0;
			String results = "";
			String result = "";
			String vid = "";
			boolean isEcoVideo;
			boolean isEcos = false;
			do{
				ArrayList<ConvertWorker> doneList = new ArrayList<>();
				for(ConvertWorker conv: converterList){
					if(conv==null){
						doneList.add(conv);
						continue;
					}
					ConvertStopFlag flag = conv.getStopFlag();
					if(conv.isDone()|| flag==null || flag.isFinished()){
						try{
							int j = conv.getId();
							log.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
							log.println("Finished. ("+j+") "+conv.getVid());
							StringBuffer sbRet = conv.getSbRet();
							if(sbRet==null){
								log.println("エラー：ret=null");
								code = -1;
								result = "-1";
							}else{
								String[] ret = sbRet.toString().split("\n");
								code = 0;
								result = "";
								vid = conv.getVid();
								isEcoVideo = conv.getIsEco();
								if(isEcoVideo)
									isEcos = true;
								for(int l=0;l<ret.length;l++){
									log.println(ret[l]);
									String[] s = ret[l].split("=");
									if("RESULT".equals(s[0]) && !"0".equals(s[1])){
										code = Integer.parseInt(s[1],16);
										result = s[1];
										break;
									}
								}
							}
						}catch(Exception e1){
							code = -999;
							result = "-999";
							e1.printStackTrace();
						}finally{
							if(code!=0 && codes==0) codes = code;	//最初のエラーコード
							if(code==0x98){
								// リトライリストにvid登録
								retryList.add(vid);
							}
							if(results.isEmpty() && !result.equals("0"))
								results = result;
							doneList.add(conv);
						}
					}
				}
				converterList.removeAll(doneList);
				if(converterList.isEmpty())
					break;
				// manager待ち
				manager.waitActivity(1);
				if(manager.getNumReq() > 0)
					continue;
				if(ConvertManager.getNumRun() > 0)
					continue;
				if(ConvertManager.getNumFinish() < convNo)
					continue;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}while(!converterList.isEmpty());
			if(aborted){
				codes = 255;
				results = "FF";
				log.println("中止\nRESULTS="+results);
			}else if(!retryList.isEmpty()){
				//リトライ要求が有る
				codes = 0x98;
				results = "98";
				log.println("サスペンド\nRESULTS="+results);
			}else {
				if(results.isEmpty())
					results = "0";
				log.println("終了\nRESULTS="+results);
			}
			if(codes!=0)
				log.println("エラーがありました");
			if(isEcos)
				log.println("エコノミー動画がありました");
			if(codes!=0 || isEcos){
				if(errorControl.save())
					log.println("エラーリストを保存しました");
				else
					log.println("エラーリストを保存失敗");
			}
			if(!retryList.isEmpty()){
				codes = 0x98;
				log.println("サスペンドした動画をリトライします");
				if(localListFile.renameTo(new File(localListFile.getPath()+"_sav")))
					log.println("autolistをリネームしました。");
				PrintWriter pw = null;
				try {
					pw = new PrintWriter(localListFile);
					pw.println(":retryList "+(new Date().toString()));
					for(String videoid: retryList){
						pw.println(videoid);
					}
					pw.flush();
					pw.close();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} finally {
					if(pw!=null) {
						pw.flush();
						pw.close();
					}
				}
				retryList.clear();
			}
			return exit(codes);
		}
	}

	private static void AllCancel_ActionHandler(ActionEvent e) {
		aborted  = true;
		for(ConvertStopFlag flag:flags){
			if(flag!=null){
				manager.gotoCancel(flag);
			}
		}
		manager.cancelAllRequest();
		manager.queueCheckAndGo();
		stopButton.setEnabled(false);
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

	private static Logger setLog(String name){
		int logsize = getLogsize();
		String text = Path.readAllText(name, "MS932");
		int len = text.length();
		if(len > logsize){
			text = text.substring(len - logsize);
			Path.writeAllText(name, text, "MS932");
		}
		Logger.setLogviewVisible(false);
		Logger logger = new Logger(new File(name), true);
		Logger.MainLog.addSysout(logger);
		return Logger.MainLog;
	}

	private static int exit(int status) {
		return status;
	}

}
