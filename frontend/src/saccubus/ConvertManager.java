package saccubus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import saccubus.json.Mson;
import saccubus.net.Loader;
import saccubus.net.Path;
import saccubus.util.Stopwatch;

/**
 * <p>�^�C�g��: ������΂�</p>
 *
 * <p>����: �j�R�j�R����̓�����R�����g���ŕۑ�</p>
 *
 * <p>���쌠: Copyright (c) 2007 PSI</p>
 *
 * <p>��Ж�: </p>
 *
 * @author ������
 * @version 1.0
 */
public class ConvertManager extends Thread {
	private final ConvertingSetting Setting;
	private final String Url;
	private String Tag;
	private String VideoID;
	private String Time;
	private JLabel Status;
	private final ConvertStopFlag StopFlag;
	private static final String VIDEO_URL_PARSER = "http://www.nicovideo.jp/watch/";
	private final JLabel MovieInfo;
	private final Stopwatch Stopwatch;
	private StringBuffer sbRet;
	private saccubus.MainFrame parent;
	/*
	 * sbRet is return String value to EXTERNAL PROGRAM such as BAT file, SH script, so on.
	 * string should be ASCII or URLEncoded in System Encoding.
	 * format: KEY=VALUE\n[KRY=VALUE\n]...
	 * KEYs are:
	 *  RESULT=0 //success, other integer //error code, Prompt(CUI) will exit(this value)
	 *  DATEUF=integer //Date seconds of first user comment downloaded, otherwise ThreadID
	 *  ...
	 */
	private String result = "0";
	private final boolean watchvideo;
	private ConcurrentLinkedQueue<File> fileQueue;
	private static final String MY_MYLIST = "my/mylist";
	VPlayer vplayer;
	private final JLabel watchArea;

	public ConvertManager(String url, String time, ConvertingSetting setting,
			JLabel status, ConvertStopFlag flag, JLabel movieInfo, JLabel watch,
			MainFrame frame, StringBuffer sb,ConcurrentLinkedQueue<File> queue) {
		url = url.trim();
		if(url.startsWith("/")){
			url = url.substring(1);
		}
		if(url.startsWith(VIDEO_URL_PARSER)){
			url = url.substring(VIDEO_URL_PARSER.length());
		}else if(url.startsWith("http://www.nicovideo.jp/" + MY_MYLIST)
				||url.startsWith(MY_MYLIST)){
			int index = url.indexOf("/#/");
			if(index < 0){
				url = "http://www.nicovideo.jp/api/deflist/list";
			}else{
				url = "http://www.nicovideo.jp/api/mylist/list?group_id="+url.substring(index+3);
				//url = url.replace("my/mylist/#/","mylist/");
			}
		}else if(!url.startsWith("http")){
			if(	  url.startsWith("mylist/")
				||url.startsWith("user/")
				||url.startsWith("my/")){
				url = "http://www.nicovideo.jp/" + url;	//may not work
			}else if(url.startsWith("lv")){
				url = "http://live.nicovideo.jp/watch/"+ url;	//may not work
			}else if(url.startsWith("co")){
				url = "http://com.nicovideo.jp/watch/" + url;	//may not work
			}
		}
		Url = url;
		watchvideo = !url.startsWith("http");
		int index = 0;
		index = url.indexOf('#');
		if(index >= 0){
			url = url.replace("#+", "?").replace("#/", "?");
		}
		index = url.indexOf('?');
		if(index >= 0){
			int index2 = url.lastIndexOf('/',index);
			Tag = url.substring(index2+1,index);
			url.substring(index);
		}else{
			int index2 = url.lastIndexOf('/');
			Tag = url.substring(index2+1);
		}
		if(Tag.contains("/")||Tag.contains(":")){
			Tag = Tag.replace("/","_").replace(":","_");
			System.out.println("BUG Tag changed: "+Tag);
		}
		VideoID = "[" + Tag + "]";
		new VideoIDFilter(VideoID);
		if (time.equals("000000") || time.equals("0")){		// for auto.bat
			Time = "";
		} else {
			Time = time;
		}
		Setting = setting;
		Status = status;
		StopFlag = flag;
		MovieInfo = movieInfo;
		MovieInfo.setText(" ");
		watchArea = watch;
		Stopwatch = new Stopwatch(watch);
		sbRet  = sb;
		parent = frame;
		fileQueue = queue;
	}

	private File ConvertedVideoFile = null;
	private ConvertingSetting getSetting(){
		return Setting;
	}
	private void sendtext(String text){
		synchronized (Status) {
			Status.setText(text);
		}
	}

	/* Interface to Worker */
//	private Path myFile;
//	private String text;
	/* return from Worker */
	private String resultText;
	/* debug */
	private static boolean DLDEBUG = false;
	private Converter converter;
	private String mylistID;

	private void saveAutoList(ArrayList<String[]> mylist) {
		File autobat = new File(".\\auto.bat");
		final String CMD_LINE = "%CMD% ";
		File autolist = new File(".\\autolist.bat");
		if(!autobat.canRead()){
			System.out.println("auto.bat���Ȃ��̂�autolist.bat���o�͂ł��܂���:"+mylistID);
			sendtext("�o�͎��s autolist.bat:"+mylistID);
			return;
		}
		BufferedReader br = null;
		PrintWriter pw = null;
		String s;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(autobat), "MS932"));
			pw = new PrintWriter(autolist, "MS932");
			pw.println(":�������� autolist.bat for myslist/" + mylistID);
			pw.println(": produced by Saccubus" + MainFrame_AboutBox.rev + " " + new Date());
			pw.println(":�\�\�\�\�\�\�\�\�\�\�\�\�\�\�\�\�\�\");
			while((s = br.readLine())!=null){
				if(!s.startsWith(CMD_LINE)){
					// %CMD%�s�������܂ŃR�s�[
					pw.println(s);
				}else{
					// �}�C���X�g��%CMD%�o��
					pw.println(":�\�\�\�\�\�\�\�\�\�\�\�\�\�\�\�\�\�\");
					pw.println(":set OPTION=�ߋ����O���� ���̃I�v�V���� �Ȃǂ�K�v�ɉ����w��(readmeNew.txt�Q��)");
					pw.println("set OPTION=");
					pw.println(":�ۑ��ϊ����Ȃ��s�͍폜���Ă�������");
					for(String[] ds: mylist){
						pw.println(":�^�C�g�� " + ds[1]);
						pw.println("%CMD% "+ ds[0] + " %OPTION%");
					}
					break;
				}
			}
			while((s = br.readLine())!=null){
				if(!s.startsWith(CMD_LINE)){
					// %CMD%�s�ȊO���o��
					pw.println(s);
					continue;
				}
			}
			sendtext("�o�͐��� autolist.bat:"+mylistID);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try{
				br.close();
				pw.flush();
				pw.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}

	}

	// �ϊ�����Đ�
	public void playConvertedVideo() {
		try {
			File convertedVideo = fileQueue==null ? null : fileQueue.poll();
			if(convertedVideo==null){
				sendtext("�ϊ���̓��悪����܂���");
				return;
			}
			if(!convertedVideo.canRead()){
				sendtext("�ϊ���̓��悪�ǂ߂܂���F" + convertedVideo.getName());
				return;
			}
			if(vplayer!=null && vplayer.isAlive()){
				vplayer.interrupt();
			}
			vplayer = new VPlayer(convertedVideo, Status);
			vplayer.start();
			return ;
		} catch(NullPointerException ex){
			sendtext("playConvertedVideo: NullPo.");
			ex.printStackTrace();
		}
	}

	@Override
	public void run() {
	ConvertStopFlag subStopFlag;
//		try{
//			String url = VideoID_TextField.getText();
//			if (converter != null && !converter.isFinished()) {
//				//converter���s����converter���~
//				final ConvertStopFlag flag = converter.getStopFlag();
//				if(flag!=null && !flag.needStop() && !flag.isFinished()){ //* �܂��X�g�b�v���Ă��Ȃ��B
//					flag.stop();
//				}
//			}else
//				//converter is finished and Button is pushed
//				// so new video or new mylist will be converted
//			if(url==null){
//				sendtext("MainFrame NULLPOinter url error");
//				System.out.println("MainFrame NULLPOinter url error");
//			}else if(url.contains("mylist")){
//				//converter worker start
//				isLastMylist = true;
//				stopFlag = new ConvertStopFlag(DoButton, DoButtonStopString, DoButtonWaitString, DoButtonDefString);
//				StringBuffer sb = new StringBuffer();
//				converter = new Converter(
//						url,
//						WayBackField.getText(),
//						getSetting(),
//						statusBar,
//						stopFlag,
//						vhookInfoBar,
//						elapsedTimeBar,
//						this,
//						sb);
//				converter.start();
//				// return to dispatch
//			}else
//			{
//				//�ʏ�ϊ�
//				// Shall fileQueue for playing video be cleared or truncated to 1 file?
//				// Now it will be cleared only change mylist to video, temporally.
//				if(isLastMylist){
//					queue.clear();
//					if(converter.getConvertedVideoFile()!=null){
//						queue.offer(converter.getConvertedVideoFile());
//					}
//					isLastMylist = false;
//				}
//				stopFlag = new ConvertStopFlag(DoButton, DoButtonStopString, DoButtonWaitString, DoButtonDefString);
//				converter = new Converter(
//					url,
//					WayBackField.getText(),
//					getSetting(),
//					statusBar,
//					stopFlag,
//					vhookInfoBar,
//					elapsedTimeBar,
//					queue);
//				converter.start();
//			}
//		}catch(Exception ex){
//			ex.printStackTrace();
//			sendtext("MainFrame error");
//			System.out.println("MainFrame error");
//		}
//	}
		if(watchvideo){
			try{
				subStopFlag = new ConvertStopFlag(new JButton());
				converter = new Converter(
						Url,
						Time,
						Setting,
						Status,
						subStopFlag,
						MovieInfo,
						watchArea,
						sbRet);
				converter.start();
				while(converter!=null && !subStopFlag.isFinished()){
					if(StopFlag.needStop()){
						//�q�����~�߂�
						if(subStopFlag!=null && !subStopFlag.isFinished()){
							subStopFlag.stop();
						}
					}
					try {
						//�q�����~�܂�̂�҂�
						converter.join(1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				StopFlag.finish();
				Stopwatch.clear();
			}
			return;
		}
		else{
			//not watch video get try mylist
			ArrayList<String[]> plist = new ArrayList<String[]>();
			String url = Url;
			int ngn = 0;
			try{
				//start here.
				Path file = Path.mkTemp(url.replace("http://","").replace("nicovideo.jp/","")
						.replaceAll("[/\\:\\?=\\&]+", "_") + ".html");
				Loader loader = new Loader(getSetting(), Status, MovieInfo);
				if(!loader.load(url,file)){
					sendtext("load���s "+url);
					return;
				}
				String text = Path.readAllText(file.getPath(), "UTF-8");
				sendtext("�ۑ����܂����B" + file.getRelativePath());
				if(StopFlag.needStop()) {
					return;
				}
				if(DLDEBUG && parent!=null){
					resultText = HtmlView.markupHtml(text);
					HtmlView hv = new HtmlView(parent, "�}�C���X�g", url);
					hv.setText(resultText);
				}
				if(StopFlag.needStop()) {
					return;
				}
				if(url.contains("api/deflist")) {
					//mylist api����
					mylistID = "deflist";
				}else
				if(url.contains("api/mylist/list?group_id=")){
					//mylist api����
					int start = url.indexOf("id=")+3;
					mylistID = url.substring(start);
				}else
				if(url.contains("mylist")) {
					//mylist����
					String json_start = "Mylist.preload(";
					int start = text.indexOf(json_start);
					if(start < 0){
						sendtext("JSON not found "+url);
						return;	//JSON not found
					}
					start += json_start.length();
					int end = (text+");\n").indexOf(");\n", start);	// end of JSON
					text = (text+");\n").substring(start, end);
					start = text.indexOf(",");
					mylistID = text.substring(0, start);
					text = text.substring(start+1).trim();
				}else{
					// here will come XML parser
				}
				//common
				{
					file = new Path(file.getRelativePath().replace(".html", ".xml"));
					Path.unescapeStoreXml(file, text, url);	//xml is property key:json val:JSON
					Properties prop = new Properties();
					prop.loadFromXML(new FileInputStream(file));	//read JSON xml
					text = prop.getProperty("json", "0");
					file = new Path(file.getRelativePath().replace(".html", ".xml"));
					//
					if(DLDEBUG && parent!=null){
						resultText = HtmlView.markupHtml(text);
						HtmlView hv2 = new HtmlView(parent, "�}�C���X�g mson", "mson");
						hv2.setText(resultText);
					}
					//
					System.out.println("get mylist/"+mylistID);
					System.out.println("mson: "+text.length());
					if(StopFlag.needStop()) {
						return;
					}
					// parse mson
					sendtext("�p�[�X���s��");
					Mson mson = null;
					try{
						mson = Mson.parse(text);
					}catch(Exception e){
						e.printStackTrace();
					}
					if(mson==null){
						sendtext("�p�[�X���s");
						return;
					}
					sendtext("�p�[�X���� "+mylistID);
					if(StopFlag.needStop()) {
						return;
					}
					//rename to .txt
					file = new Path(file.getRelativePath().replace(".xml", ".txt"));
					mson.prettyPrint(new PrintStream(file));	//pretty print
					sendtext("���X�g���� "+mylistID);
					if(StopFlag.needStop()) {
						return;
					}
					String[] keys = {"watch_id","title"};
					ArrayList<String[]> id_title_list = mson.getListString(keys);	// List of id & title
					for(String[] vals:id_title_list){
						System.out.println("Getting ["+ vals[0] + "]"+ vals[1]);
						plist.add(0, vals);
					}
					//
					sendtext("���o���� "+mylistID);
					int sz = plist.size();
					System.out.println("Success mylist/"+mylistID+" item:"+sz);
					if(sz == 0){
						sendtext("���悪����܂���B"+mylistID);
						return;
					}
					if(StopFlag.needStop()) {
						return;
					}
					if(DLDEBUG && parent!=null){
						TextView dlg = new TextView(parent, "mylist/"+mylistID);
						JTextArea textout = dlg.getTextArea();
						for(String[] idts:plist){
							textout.append("["+idts[0]+"]"+idts[1]+"\n");
						}
						textout.setCaretPosition(0);
					}
					if(StopFlag.needStop()) {
						return;
					}
					//start downloader
					if(Setting.isSaveAutoList()){
						saveAutoList(plist);
						return;
					}
					StringBuffer sb = new StringBuffer();
					for(String[] ds: plist){
						String vid = ds[0];
						String vtitle = ds[1];
						System.out.println("Converting ["+ vid +"]" + vtitle);
						//converter���Ă�
						if(StopFlag.needStop()) {
							return;
						}
						ConvertingSetting mySetting = getSetting();
						if(parent!=null){
							mySetting = parent.getSetting();
							// parent!=null�Ȃ�mylist��fileQueue==null
							fileQueue = parent.getQueue();
						}
						sb = new StringBuffer();
						subStopFlag = new ConvertStopFlag();
						converter = new Converter(
								vid,
								Time,
								mySetting,
								Status,
								subStopFlag,
								MovieInfo,
								watchArea,
								parent,
								sb);
						converter.start();
						while(converter!=null && !converter.isFinished()){
							if(StopFlag.needStop()){
								//�q�����~�߂�
								if(subStopFlag!=null && !subStopFlag.isFinished()){
									subStopFlag.stop();
								}
							}
							try {
								//�q�����~�܂�̂�҂�
								converter.join(1000);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
						}
						ConvertedVideoFile = converter.getConvertedVideoFile();
						if(!sb.toString().contains("RESULT=0\n")){
							result=sb.toString().replace("\n", ",").trim();
							ngn++;
						}
						Long t = new Date().getTime();
						watchArea.setText("�ҋ@��");
						System.out.println("Sleep start." + WayBackDate.formatNow());
						//�E�F�C�g10�b
						int wt = 10;
						while(!StopFlag.needStop() && wt-->0){
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
								System.out.println("Sleep stop.");
								wt = 0;
							}
						}
						System.out.println("Sleep end. " + (new Date().getTime() - t)/1000 + "sec.");
						if(StopFlag.needStop()){
							return;
						}
					}//end for()
					sendtext("�}�C���X�g"+mylistID+" �S���I��, ���s:"+ngn+"/"+plist.size()+"����");
					return;
				}
			}catch(InterruptedException e){
			}catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(StopFlag.needStop())
					result="FF";
				StopFlag.finish();
				System.out.println("LastStatus:[" + result + "]" + Status.getText());
				System.out.println("VideoInfo: " + MovieInfo.getText());
				Stopwatch.clear();
				if(sbRet!=null){
					sbRet.append("RESULT=" + result + "\n");
					sbRet.append("FAIL="+ngn+"\n");
				}
			}
			return;
		}
	}

	public boolean isFinished() {
		return StopFlag.isFinished();
	}

	public ConvertStopFlag getStopFlag() {
		return this.StopFlag;
	}

	/**
	 *
	 * @author orz
	 *
	 */
	private static class VideoIDFilter implements FilenameFilter {
		private final String VideoTag;
		public VideoIDFilter(String videoTag){
			VideoTag = videoTag;
		}
		@Override
		public boolean accept(File dir, String name) {
			if (name.indexOf(VideoTag) >= 0){
				return true;
			}
			return false;
		}
	}

	public File getConvertedVideoFile() {
		return ConvertedVideoFile;
	}

}
