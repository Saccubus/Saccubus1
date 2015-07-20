package saccubus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import saccubus.FFmpeg.Aspect;
import saccubus.conv.Chat;
import saccubus.conv.CombineXML;
import saccubus.conv.CommandReplace;
import saccubus.conv.CommentReplace;
import saccubus.conv.ConvertToVideoHook;
import saccubus.conv.NicoXMLReader;
import saccubus.json.Mson;
import saccubus.net.BrowserInfo;
import saccubus.net.BrowserInfo.BrowserCookieKind;
import saccubus.net.Loader;
import saccubus.net.NicoClient;
import saccubus.net.Path;
import saccubus.util.Cws2Fws;
import saccubus.util.Stopwatch;
import saccubus.util.Util;

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
public class Converter extends Thread {
	private final ConvertingSetting Setting;
	private final String Url;
	private String Tag;
	private String VideoID;
	private String VideoTitle;
	private String VideoBaseName;
	private String Time;
	private JLabel Status;
	private final ConvertStopFlag StopFlag;
	private static final String TMP_COMMENT = "_vhook.tmp";
	private static final String TMP_OWNERCOMMENT = "_vhookowner.tmp";
	private static final String TMP_OPTIONALTHREAD = "_vhookoptional.tmp";
	private static final String VIDEO_URL_PARSER = "http://www.nicovideo.jp/watch/";
	public static final String OWNER_EXT = "[Owner].xml";	// ���e�҃R�����g�T�t�B�b�N�X
	public static final String OPTIONAL_EXT = "{Optional}.xml";	// �I�v�V���i���X���b�h�T�t�B�b�N�X
	private static final String TMP_COMBINED_XML = "_tmp_comment.xml";
	private static final String TMP_COMBINED_XML2 = "_tmp_optional.xml";
	private static final String THUMB_INFO = "_thumb_info";
	private String OtherVideo;
	private final String WatchInfo;
	private final JLabel MovieInfo;
	private InfoStack infoStack;
	private BrowserCookieKind BrowserKind = BrowserCookieKind.NONE;
	private final BrowserInfo BrowserInfo = new BrowserInfo();
	private String UserSession = "";	//�u���E�U����擾�������[�U�[�Z�b�V����
	private final Stopwatch Stopwatch;
	private File selectedVhook;
	private Aspect videoAspect;
	private boolean isPlayerWide;
	private File fwsFile = null;
	private VideoIDFilter DefaultVideoIDFilter;
	private String proxy;
	private int proxy_port;
	private String mailAddress;
	private String password;
	private String inSize;
	private String setSize;
	private String padOption;
	private String outSize;
	private String aprilFool;
	private StringBuffer sbRet = null;
	private saccubus.MainFrame parent = null;
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
	private String dateUserFirst = "";
	private ArrayList<CommentReplace> commentReplaceSet = new ArrayList<CommentReplace>();
	private final boolean watchvideo;
	private double frameRate = 0.0;
	private double fpsUp = 0.0;
	private double fpsMin = 0.0;
	private String lastFrame = "";
	private ConcurrentLinkedQueue<File> fileQueue;
	private static final String MY_MYLIST = "my/mylist";

	public Converter(String url, String time, ConvertingSetting setting,
			JLabel status, ConvertStopFlag flag, JLabel movieInfo, JLabel watch) {
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
			WatchInfo = url.substring(index);
		}else{
			int index2 = url.lastIndexOf('/');
			Tag = url.substring(index2+1);
			WatchInfo = "";
		}
		if(Tag.contains("/")||Tag.contains(":")){
			Tag = Tag.replace("/","_").replace(":","_");
			System.out.println("BUG Tag changed: "+Tag);
		}
		VideoID = "[" + Tag + "]";
		DefaultVideoIDFilter = new VideoIDFilter(VideoID);
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
		Stopwatch = new Stopwatch(watch);
	}

	public Converter(String url, String time, ConvertingSetting setting,
			JLabel status, ConvertStopFlag flag, JLabel movieInfo, JLabel watch, StringBuffer sbret) {
		this(url,time,setting,status,flag,movieInfo,watch);
		sbRet  = sbret;
	}
	public Converter(String url, String time, ConvertingSetting setting,
			JLabel status, ConvertStopFlag flag, JLabel movieInfo, JLabel watch,
			MainFrame frame, StringBuffer sb) {
		this(url,time,setting,status,flag,movieInfo,watch);
		sbRet  = sb;
		parent = frame;
	}
	public Converter(String url, String time, ConvertingSetting setting,
			JLabel status, ConvertStopFlag flag, JLabel movieInfo, JLabel watch,
			ConcurrentLinkedQueue<File> queue) {
		this(url,time,setting,status,flag,movieInfo,watch);
		fileQueue = queue;
	}
	private File VideoFile = null;
	private File CommentFile = null;
	private File OwnerCommentFile = null;
	private File OptionalThreadFile = null;
	private File ConvertedVideoFile = null;
	private File CommentMiddleFile = null;
	private File OwnerMiddleFile = null;
	private File OptionalMiddleFile = null;
	private FFmpeg ffmpeg = null;
	private File VhookNormal = null;
	private File VhookWide = null;
	private File VhookQ = null;
	private int wayOfVhook = 0;
	private ArrayList<File> listOfCommentFile = new ArrayList<File>();
	private String optionalThreadID = "";	// set in
	private String errorLog = "";
	private int videoLength = 0;
	private int ownerCommentNum = 0;
	private File fontDir;
	private File gothicFont = null;
	private File simsunFont = null;
	private File gulimFont = null;
	private File arialFont = null;
	private File georgiaFont = null;
//	private File msuigothicFont = null;
	private File devabagariFont = null;
	private File tahomaFont = null;
	private File mingliuFont = null;
	private File newMinchoFont = null;
	private File estrangeloEdessaFont = null;
	private File arialUnicodeFont = null;
	private File gujaratiFont = null;
	private File bengalFont = null;
	private File tamilFont = null;
	private File laooFont = null;
	private File gurmukhiFont = null;
	private File kannadaFont = null;
	private File thaanaFont = null;
	private File malayalamFont = null;
	private File teluguFont = null;
	private File nirmalaFont = null;
	private Pattern ngWordPat;
	private Pattern ngIDPat;
	private CommandReplace ngCmd;
	private Path thumbInfo = new Path("null");
	private File thumbInfoFile;
	private String wakuiro = "";
	private StringBuffer resultBuffer;
	private File thumbnailJpg;
	private String addOption;

	public File getVideoFile() {
		return VideoFile;
	}
	private String getVideoBaseName() {
		return VideoBaseName;
	}
	public ConvertingSetting getSetting(){
		return Setting;
	}
	public String getErrorLog() {
		return errorLog;
	}
	public String getLastError() {
		if (errorLog==null)
			return "";
		errorLog = errorLog.trim();
		int index = errorLog.lastIndexOf("\n");
		String lasterror = errorLog.substring(index+1);
		return lasterror;
	}

	private void sendtext(String text){
		synchronized (Status) {
			Status.setText(text);
		}
	}

	private boolean isSaveConverted(){
		return Setting.isSaveConverted();
	}
	private boolean isSaveVideo(){
		return Setting.isSaveVideo();
	}
	private boolean isSaveComment(){
		return Setting.isSaveComment();
	}
	private boolean isSaveOwnerComment(){
		return Setting.isSaveOwnerComment() && isSaveComment();
	}
	private boolean isConvertWithComment(){
		return Setting.isConvertWithComment();
	}
	private boolean isConvertWithOwnerComment(){
		return Setting.isConvertWithOwnerComment();
	}
	private boolean isVideoFixFileName(){
		return Setting.isVideoFixFileName();
	}
	private boolean isCommentFixFileName(){
		return Setting.isCommentFixFileName();
	}
	private boolean isDeleteVideoAfterConverting(){
		return Setting.isDeleteVideoAfterConverting();
	}
	private boolean isDeleteCommentAfterConverting(){
		return Setting.isDeleteCommentAfterConverting();
	}
	private boolean useProxy(){
		return Setting.useProxy();
	}
	ArrayList<CommentReplace> getCommentReplaceSet(){
		return commentReplaceSet;
	}
	void addCommentReplace(CommentReplace cmrpl){
		commentReplaceSet.add(cmrpl);
	}

	private boolean checkOK() {
		sendtext("�`�F�b�N���Ă��܂�");
		if (!isSaveConverted() && !isSaveVideo()
			&& !isSaveComment() && !isSaveOwnerComment()
			&& !Setting.isSaveThumbInfo()){
			sendtext("�������邱�Ƃ�����܂���");
			result = "1";
			return false;
		}
		if (isSaveConverted()) {
			File a = new File(Setting.getFFmpegPath());
			if (!a.canRead()) {
				sendtext("FFmpeg��������܂���B");
				result = "2";
				return false;
			}
			this.ffmpeg = new FFmpeg(Setting.getFFmpegPath());
			if (Setting.isZqPlayer()) {
				if(Setting.getZqVhookPath().indexOf(' ') >= 0){
					sendtext("�����܂���B����vhook���C�u�����ɂ͔��p�󔒂͎g���܂���B");
					result = "3";
					return false;
				}
				VhookQ = new File(Setting.getZqVhookPath());
				if(!VhookQ.canRead()){
					sendtext("���ʊg��Vhook���C�u������������܂���B");
					result = "4";
					return false;
				}
				wayOfVhook = 3;
			} else {
				if (Setting.isUseVhookNormal()){
					if(Setting.getVhookPath().indexOf(' ') >= 0) {
						sendtext("�����܂���B����vhook���C�u�����ɂ͔��p�󔒂͎g���܂���B");
						result = "3";
						return false;
					}
					VhookNormal = new File(Setting.getVhookPath());
					if (!VhookNormal.canRead()) {
						sendtext("Vhook���C�u������������܂���B");
						result = "4";
						return false;
					}
					wayOfVhook++;
				}
				if (Setting.isUseVhookWide()){
					if(Setting.getVhookWidePath().isEmpty()){
						VhookWide = VhookNormal;
					}
					else {
						if(Setting.getVhookWidePath().indexOf(' ') >= 0) {
							sendtext("�����܂���B����vhook�t�@�C�����ɂ͔��p�󔒂͎g���܂���B");
							result = "5";
							return false;
						}
						VhookWide = new File(Setting.getVhookWidePath());
					}
					if (!VhookWide.canRead()) {
						sendtext("Vhook���C�u�����i���C�h�j��������܂���B");
						result = "6";
						return false;
					}
					wayOfVhook++;
				}
			}
			if (wayOfVhook == 0){
				sendtext("�g�p�ł���Vhook���C�u����������܂���B");
				result = "7";
				return false;
			}
			if(Setting.isEnableCA()){
				String windir = System.getenv("windir");
				if(windir == null){
					sendtext("Windows�t�H���_��������܂���B");
					result = "8";
					return false;
				}
				fontDir = new File(windir, "Fonts");
				if(!fontDir.isDirectory()){
					sendtext("Fonts�t�H���_��������܂���B");
					result = "9";
					return false;
				}
				simsunFont = new File(fontDir, "SIMSUN.TTC");
				if (!simsunFont.canRead()) {
					sendtext("CA�p�t�H���g��������܂���B" + simsunFont.getPath());
					result = "10";
					return false;
				}
				gulimFont = new File(fontDir, "GULIM.TTC");	//windowsXP,7,8 �ە���
				File saveGulimFont = gulimFont;
				if (!gulimFont.canRead()) {
					sendtext("�x���@CA�p�t�H���g��������܂���B" + gulimFont.getPath());
					gulimFont = new File(fontDir, "MALGUN.TTF");	//windows10 �n���O��
				}
				if (!gulimFont.canRead()) {
					gulimFont = simsunFont;	// ����
				}
				if (!gulimFont.canRead()) {
					sendtext("CA�p�t�H���g�̑�ւ�������܂���B" + gulimFont.getPath());
					result = "11";
					return false;
				}
				if(!gulimFont.equals(saveGulimFont)){
					System.out.println("CA�p�t�H���g" + saveGulimFont.getPath() + "��" + gulimFont.getName() + "�ő�ւ��܂��B");
				}
				arialFont = new File(fontDir, "ARIAL.TTF");
				if(!arialFont.canRead()){
					sendtext("CA�p�t�H���g��������܂���B" + arialFont.getPath());
					result = "12";
					return false;
				}
				gothicFont = new File(fontDir, "MSGOTHIC.TTC");
				if (!gothicFont.canRead()) {
					sendtext("CA�p�t�H���g��������܂���B" + gothicFont.getPath());
					result = "13";
					return false;
				}
				georgiaFont  = new File(fontDir, "sylfaen.ttf");
				if (!georgiaFont.canRead()) {
					sendtext("�x���@CA�p�t�H���g��������܂���B" + georgiaFont.getPath());
					//retValue = "14";
					//return false;
					System.out.println("CA�p�t�H���g" + georgiaFont.getPath() + "��" + gothicFont.getName() + "�ő�ւ��܂��B");
					georgiaFont = gothicFont;
				}
				nirmalaFont = new File(fontDir,"Nirmala.ttf");
				devabagariFont = new File(fontDir, "mangal.ttf");
				if (!devabagariFont.canRead()) {
					devabagariFont = nirmalaFont;
				}
				if(!devabagariFont.canRead()){
					sendtext("�x���@CA�p�t�H���g��������܂���B" + devabagariFont.getPath());
					//retValue = "15";
					//return false;
					System.out.println("CA�p�t�H���g" + devabagariFont.getPath() + "��" + arialFont.getName() + "�ő�ւ��܂��B");
					devabagariFont = arialFont;
				}
				tahomaFont = new File(fontDir, "tahoma.ttf");
				if (!tahomaFont.canRead()) {
					sendtext("�x���@CA�p�t�H���g��������܂���B" + tahomaFont.getPath());
					//retValue = "16";
					//return false;
					System.out.println("CA�p�t�H���g" + tahomaFont.getPath() + "��" + arialFont.getName() + "�ő�ւ��܂��B");
					tahomaFont = arialFont;
				}
				mingliuFont = new File(fontDir, "mingliu.ttc");
				if (!mingliuFont.canRead()) {
					sendtext("�x���@CA�p�t�H���g��������܂���B" + mingliuFont.getPath());
					//retValue = "17";
					//return false;
					System.out.println("CA�p�t�H���g" + mingliuFont.getPath() + "��" + simsunFont.getName() + "�ő�ւ��܂��B");
					mingliuFont = simsunFont;
				}
				newMinchoFont = new File(fontDir, "SIMSUN.TTC");	//NGULIM.TTF����������
				if (!newMinchoFont.canRead()) {
					sendtext("�x���@CA�p�t�H���g��������܂���B" + newMinchoFont.getPath());
					//retValue = "18";
					//return false;
					System.out.println("CA�p�t�H���g" + newMinchoFont.getPath() + "��" + simsunFont.getName() + "�ő�ւ��܂��B");
					newMinchoFont = simsunFont;
				}
				estrangeloEdessaFont = new File(fontDir, "estre.ttf");
				if (!estrangeloEdessaFont.canRead()) {
					estrangeloEdessaFont = new File(fontDir, "seguihis.ttf");
				}
				if (!estrangeloEdessaFont.canRead()){
					sendtext("�x���@CA�p�t�H���g��������܂���B" + estrangeloEdessaFont.getPath());
					//retValue = "19";
					//return false;
					System.out.println("CA�p�t�H���g" + estrangeloEdessaFont.getPath() + "��" + arialFont.getName() + "�ő�ւ��܂��B");
					estrangeloEdessaFont = arialFont;
				}
				arialUnicodeFont = new File(fontDir, "arialuni.ttf");
				if (!arialUnicodeFont.canRead()) {
					sendtext("�x���@CA�p�t�H���g��������܂���B" + arialUnicodeFont.getPath());
					//retValue = "20";
					//return false;
					System.out.println("CA�p�t�H���g" + arialUnicodeFont.getPath() + "��" + arialFont.getName() + "�ő�ւ��܂��B");
					arialUnicodeFont = arialFont;
				}
				gujaratiFont = new File(fontDir, "shruti.ttf");
				if (!gujaratiFont.canRead()){
					gujaratiFont = nirmalaFont;
				}
				if (!gujaratiFont.canRead()) {
					sendtext("�x���@CA�p�t�H���g��������܂���B" + gujaratiFont.getPath());
					//retValue = "21";
					//return false;
					System.out.println("CA�p�t�H���g" + gujaratiFont.getPath() + "��" + arialUnicodeFont.getName() + "�ő�ւ��܂��B");
					gujaratiFont = arialUnicodeFont;
				}
				bengalFont = new File(fontDir, "vrinda.ttf");
				if (!bengalFont.canRead()) {
					bengalFont = nirmalaFont;
				}
				if(!bengalFont.canRead()){
					sendtext("�x���@CA�p�t�H���g��������܂���B" + bengalFont.getPath());
					//retValue = "22";
					//return false;
					System.out.println("CA�p�t�H���g" + bengalFont.getPath() + "��" + arialUnicodeFont.getName() + "�ő�ւ��܂��B");
					bengalFont = arialUnicodeFont;
				}
				tamilFont = new File(fontDir, "latha.ttf");
				if (!tamilFont.canRead()) {
					tamilFont = nirmalaFont;
				}
				if (!tamilFont.canRead()) {
					sendtext("�x���@CA�p�t�H���g��������܂���B" + tamilFont.getPath());
					//retValue = "23";
					//return false;
					System.out.println("CA�p�t�H���g" + tamilFont.getPath() + "��" + arialUnicodeFont.getName() + "�ő�ւ��܂��B");
					tamilFont = arialUnicodeFont;
				}
				laooFont = new File(fontDir, "laoui.ttf");
				if (!laooFont.canRead()) {
					laooFont = new File(fontDir, "LeelawUI.ttf");
				}
				if (!laooFont.canRead()) {
					sendtext("�x���@CA�p�t�H���g��������܂���B" + laooFont.getPath());
					//retValue = "24";
					//return false;
					System.out.println("CA�p�t�H���g" + laooFont.getPath() + "��" + arialUnicodeFont.getName() + "�ő�ւ��܂��B");
					laooFont = arialUnicodeFont;
				}
				gurmukhiFont = new File(fontDir, "raavi.ttf");
				if (!gurmukhiFont.canRead()) {
					gurmukhiFont = nirmalaFont;
				}
				if (!gurmukhiFont.canRead()) {
					sendtext("�x���@CA�p�t�H���g��������܂���B" + gurmukhiFont.getPath());
					//retValue = "25";
					//return false;
					System.out.println("CA�p�t�H���g" + gurmukhiFont.getPath() + "��" + arialUnicodeFont.getName() + "�ő�ւ��܂��B");
					gurmukhiFont = arialUnicodeFont;
				}
				kannadaFont = new File(fontDir, "tunga.ttf");
				if (!kannadaFont.canRead()) {
					kannadaFont = nirmalaFont;
				}
				if (!kannadaFont.canRead()) {
					sendtext("�x���@CA�p�t�H���g��������܂���B" + kannadaFont.getPath());
					//retValue = "26";
					//return false;
					System.out.println("CA�p�t�H���g" + kannadaFont.getPath() + "��" + arialUnicodeFont.getName() + "�ő�ւ��܂��B");
					kannadaFont = arialUnicodeFont;
				}
				thaanaFont = new File(fontDir, "mvboli.ttf");
				if (!thaanaFont.canRead()) {
					sendtext("�x���@CA�p�t�H���g��������܂���B" + thaanaFont.getPath());
					//retValue = "27";
					//return false;
					System.out.println("CA�p�t�H���g" + thaanaFont.getPath() + "��" + arialUnicodeFont.getName() + "�ő�ւ��܂��B");
					thaanaFont = arialUnicodeFont;
				}
				malayalamFont = new File(fontDir, "kartika.ttf");
				if (!malayalamFont.canRead()) {
					malayalamFont = nirmalaFont;
				}
				if (!malayalamFont.canRead()) {
					sendtext("�x���@CA�p�t�H���g��������܂���B" + malayalamFont.getPath());
					//retValue = "28";
					//return false;
					System.out.println("CA�p�t�H���g" + malayalamFont.getPath() + "��" + arialUnicodeFont.getName() + "�ő�ւ��܂��B");
					malayalamFont = arialUnicodeFont;
				}
				teluguFont = new File(fontDir, "gautami.ttf");
				if (!teluguFont.canRead()) {
					teluguFont = nirmalaFont;
				}
				if (!teluguFont.canRead()) {
					sendtext("�x���@CA�p�t�H���g��������܂���B" + teluguFont.getPath());
					//retValue = "29";
					//return false;
					System.out.println("CA�p�t�H���g" + teluguFont.getPath() + "��" + arialUnicodeFont.getName() + "�ő�ւ��܂��B");
					teluguFont = arialUnicodeFont;
				}
			}else{
				a = new File(Setting.getFontPath());
				if (!a.canRead()) {
					sendtext("�t�H���g��������܂���B");
					result = "30";
					return false;
				}
			}
		} else {
			if (isDeleteVideoAfterConverting()) {
				sendtext("�ϊ����Ȃ��̂ɁA����폜��������ėǂ���ł����H");
				result = "31";
				return false;
			}
			if (isDeleteCommentAfterConverting()) {
				sendtext("�ϊ����Ȃ��̂ɁA�R�����g�폜��������ėǂ���ł����H");
				result = "32";
				return false;
			}
		}
		if (isSaveVideo() || isSaveComment() || isSaveOwnerComment()
			|| Setting.isSaveThumbInfo()) {
			// �u���E�U�Z�b�V�������L�̏ꍇ�͂����ŃZ�b�V������ǂݍ���
			UserSession = BrowserInfo.getUserSession(Setting);
			BrowserKind = BrowserInfo.getValidBrowser();
			if (BrowserKind == BrowserCookieKind.NONE){
				mailAddress = Setting.getMailAddress();
				password = Setting.getPassword();
				if (mailAddress == null || mailAddress.isEmpty()
					|| password == null || password.isEmpty()) {
					sendtext("���[���A�h���X���p�X���[�h���󔒂ł��B");
					result = "33";
					return false;
				}
			} else if (UserSession.isEmpty()){
					sendtext("�u���E�U" + BrowserKind.getName() + "�̃Z�b�V�����擾�Ɏ��s");
					result = "34";
					return false;
			}
			if (useProxy()){
				proxy = Setting.getProxy();
				proxy_port = Setting.getProxyPort();
				if (   proxy == null || proxy.isEmpty()
					|| proxy_port < 0 || proxy_port > 65535   ){
					sendtext("�v���L�V�̐ݒ肪�s���ł��B");
					result = "35";
					return false;
				}
			} else {
				proxy = null;
				proxy_port = -1;
			}
		}
		resultBuffer = Setting.getReturnBuffer();
		sendtext("�`�F�b�N�I��");
		return true;
	}

	private NicoClient getNicoClient() {
		if (isSaveVideo() || isSaveComment() || isSaveOwnerComment()
			|| Setting.isSaveThumbInfo()) {
			sendtext("���O�C����");
			NicoClient client = null;
			if (BrowserKind != BrowserCookieKind.NONE){
				// �Z�b�V�������L�A���O�C���ς݂�NicoClient��client�ɕԂ�
				client = new NicoClient(BrowserKind, UserSession, proxy, proxy_port, Stopwatch);
			} else {
				client = new NicoClient(mailAddress, password, proxy, proxy_port, Stopwatch);
			}
			if (!client.isLoggedIn()) {
				sendtext("���O�C�����s " + BrowserKind.getName() + " " + client.getExtraError());
			} else {
				sendtext("���O�C������ " + BrowserKind.getName());
			}
			return client;
		} else {
			return null;
		}
	}

	private boolean saveVideo(NicoClient client) {
		File folder = Setting.getVideoFixFileNameFolder();
		sendtext("����̕ۑ�");
		/*����̕ۑ�*/
		try {
			if (isSaveVideo()) {
				if (isVideoFixFileName()) {
					if (folder.mkdir()) {
						System.out.println("Folder created: " + folder.getPath());
					}
					if (!folder.isDirectory()) {
						sendtext("����̕ۑ���t�H���_���쐬�ł��܂���B");
						result = "40";
						return false;
					}
					VideoFile = new File(folder, getVideoBaseName() + ".flv");
				} else {
					VideoFile = Setting.getVideoFile();
				}
				if(VideoFile.isFile() && VideoFile.canRead()){
					sendtext("����͊��ɑ��݂��܂�");
					System.out.println("����͊��ɑ��݂��܂��B�_�E�����[�h���X�L�b�v���܂�");
				}else{
					sendtext("����̃_�E�����[�h�J�n��");
					if (client == null){
						sendtext("���O�C�����ĂȂ��̂ɓ���̕ۑ��ɂȂ�܂���");
						result = "41";
						return false;
					}
					if(Setting.isDisableEco() &&  client.isEco()){
						sendtext("�G�R�m�~�[���[�h�Ȃ̂Œ��~���܂�");
						result = "42";
						return false;
					}
					VideoFile = client.getVideo(VideoFile, Status, StopFlag,
						isVideoFixFileName() && Setting.isChangeMp4Ext());
					if (stopFlagReturn()) {
						result = "43";
						return false;
					}
					if (VideoFile == null) {
						sendtext("����̃_�E�����[�h�Ɏ��s" + client.getExtraError());
						result = "44";
						return false;
					}
					resultBuffer.append("video: "+VideoFile.getName()+"\n");
				}
				if (optionalThreadID == null || optionalThreadID.isEmpty()) {
					optionalThreadID = client.getOptionalThreadID();
				}
				videoLength = client.getVideoLength();
				setVideoTitleIfNull(VideoFile.getName());
			} else {
				if (isSaveConverted()) {
					if (isVideoFixFileName()) {
						String videoFilename;
						if((videoFilename = detectTitleFromVideo(folder)) == null){
							if (OtherVideo == null){
								sendtext("����t�@�C�����t�H���_�ɑ��݂��܂���B");
								result = "45";
							} else {
								sendtext("����t�@�C����.flv�ł���܂���F" + OtherVideo);
								result = "46";
							}
							return false;
						}
						VideoFile = new File(folder, videoFilename);
						if (!VideoFile.canRead()) {
							sendtext("����t�@�C�����ǂݍ��߂܂���B");
							result = "47";
							return false;
						}
					} else {
						VideoFile = Setting.getVideoFile();
						if (!VideoFile.exists()) {
							sendtext("����t�@�C�������݂��܂���B");
							result = "48";
							return false;
						}
					}
					setVideoTitleIfNull(VideoFile.getName());
				}
			}
			sendtext("����̕ۑ����I��");
		}catch(NullPointerException e){
			sendtext("(�L�́M)���ʂ��\n�K�b\n");
			e.printStackTrace();
		}
		return true;
	}

	private boolean saveComment(NicoClient client) {
		sendtext("�R�����g�̕ۑ�");
		File folder = Setting.getCommentFixFileNameFolder();
		String commentTitle = "";
		String prefix = "";
		String back_comment = Setting.getBackComment();
		if (isSaveComment()) {
			if (isCommentFixFileName()) {
				if (folder.mkdir()) {
					System.out.println("Folder created: " + folder.getPath());
				}
				if (!folder.isDirectory()) {
					sendtext("�R�����g�̕ۑ���t�H���_���쐬�ł��܂���B");
					result = "50";
					return false;
				}
				if (Setting.isAddTimeStamp()) {	// prefix set
					if(Time == null || Time.isEmpty() || Time.equals("0")
						|| Time.equals("Owner") || Time.equals("Optional")){
						prefix = "[" + WayBackDate.formatNow() + "]";
					} else {
						WayBackDate wbDate = new WayBackDate(Time);
						if (wbDate.isValid()){
							prefix = "[" + wbDate.format() + "]";
						} else {
							prefix = "[" + Time + "]";
						}
					}
				}
				commentTitle = getVideoBaseName() + prefix;
			//	commentTitle = (Setting.isChangeTitleId()? VideoTitle + VideoID : VideoID + VideoTitle) + prefix;
				CommentFile = new File(folder, commentTitle + ".xml");
			} else {
				CommentFile = Setting.getCommentFile();
			}
			if (client == null){
				sendtext("���O�C�����ĂȂ��̂ɃR�����g�̕ۑ��ɂȂ�܂���");
				result = "51";
				return false;
			}
			if (Setting.isFixCommentNum()) {
				back_comment = client
						.getBackCommentFromLength(back_comment);
			}
			sendtext("�R�����g�̃_�E�����[�h�J�n��");
			CommentFile = client.getComment(CommentFile, Status, back_comment, Time, StopFlag, Setting.getCommentIndex());
			if (stopFlagReturn()) {
				result = "52";
				return false;
			}
			if (CommentFile == null) {
				sendtext("�R�����g�̃_�E�����[�h�Ɏ��s " + client.getExtraError());
				result = "53";
				return false;
			}
			//�R�����g�t�@�C���̍ŏ���date="integer"��T���� dateUserFirst �ɃZ�b�g
			dateUserFirst = getDateUserFirst(CommentFile);
			sendtext("�R�����g�̃_�E�����[�h�I��");
			optionalThreadID = client.getOptionalThreadID();
			sendtext("�I�v�V���i���X���b�h�̕ۑ�");
			if (optionalThreadID != null && !optionalThreadID.isEmpty() ){
				if (isCommentFixFileName()) {
					OptionalThreadFile = new File(folder, getVideoBaseName() + prefix + OPTIONAL_EXT);
				} else {
					OptionalThreadFile = getOptionalThreadFile(Setting.getCommentFile());
				}
				sendtext("�I�v�V���i���X���b�h�̃_�E�����[�h�J�n��");
				OptionalThreadFile = client.getOptionalThread(
					OptionalThreadFile, Status, optionalThreadID, back_comment, Time, StopFlag, Setting.getCommentIndex());
				if (stopFlagReturn()) {
					result = "54";
					return false;
				}
				if (OptionalThreadFile == null) {
					sendtext("�I�v�V���i���X���b�h�̃_�E�����[�h�Ɏ��s " + client.getExtraError());
					result = "55";
					return false;
				}
				if (dateUserFirst.isEmpty()) {
					//�t�@�C���̍ŏ���date="integer"��T���� dateUserFirst �ɃZ�b�g
					dateUserFirst = getDateUserFirst(OptionalThreadFile);
				}
				sendtext("�I�v�V���i���X���b�h�̕ۑ��I��");
			}
			resultBuffer.append("comment: "+CommentFile.getName()+"\n");
		}
		sendtext("�R�����g�̕ۑ��I��");
		return true;
	}
	private File getOptionalThreadFile(File file) {
		if (file == null || !file.isFile() || file.getPath() == null) {
			return mkTemp(OPTIONAL_EXT);
		}
		String path = file.getPath();
		int index = path.lastIndexOf(".");
		if (index > path.lastIndexOf(File.separator)) {
			path = path.substring(0, index);		// �g���q���폜
		}
		return new File(path + OPTIONAL_EXT);
	}
	private String getDateUserFirst(File comfile){
		//�R�����g�t�@�C���̍ŏ���date="integer"��T���� dateUserFirst �ɃZ�b�g
		try {
			BufferedReader br = new BufferedReader(new FileReader(CommentFile));
			String text = br.readLine();
			int begin = 0;
			int end = 0;
			if (text!=null && text.contains("date=\"")) {
				begin = text.indexOf("date=\"") + "date=\"".length();
				end = text.indexOf("\" ", begin);
				if(end>0){
					br.close();
					return text.substring(begin, end);
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	private boolean saveOwnerComment(NicoClient client){
		sendtext("���e�҃R�����g�̕ۑ�");
		File folder = Setting.getCommentFixFileNameFolder();
		if (isSaveOwnerComment()) {
			if (isCommentFixFileName()) {
				if (folder.mkdir()) {
					System.out.println("Folder created: " + folder.getPath());
				}
				if (!folder.isDirectory()) {
					sendtext("���e�҃R�����g�̕ۑ���t�H���_���쐬�ł��܂���B");
					result = "60";
					return false;
				}
				OwnerCommentFile = new File(folder, getVideoBaseName() + OWNER_EXT);
			} else {
				OwnerCommentFile = Setting.getOwnerCommentFile();
			}
			sendtext("���e�҃R�����g�̃_�E�����[�h�J�n��");
			if (client == null){
				sendtext("���O�C�����ĂȂ��̂ɓ��e�҃R�����g�̕ۑ��ɂȂ�܂���");
				result = "61";
				return false;
			}
			OwnerCommentFile = client.getOwnerComment(OwnerCommentFile, Status,
					StopFlag);
			if (stopFlagReturn()) {
				result = "62";
				return false;
			}
			if (OwnerCommentFile == null) {
				sendtext("���e�҃R�����g�̃_�E�����[�h�Ɏ��s");
				System.out.println("���e�҃R�����g�̃_�E�����[�h�Ɏ��s");
				//result = "63";
				return true;
			}
			if (optionalThreadID == null || optionalThreadID.isEmpty()) {
				optionalThreadID = client.getOptionalThreadID();
			}
		}
		sendtext("���e�҃R�����g�̕ۑ��I��");
		return true;
	}

	private boolean saveThumbInfo0(NicoClient client) {
		sendtext("������̕ۑ�");
		/*�y�[�W�̕ۑ�*/
		String ext = Setting.isSaveThumbInfoAsText()? ".txt":".xml";
		File folder = Setting.getVideoFixFileNameFolder();
		if (isVideoFixFileName()) {
			if (folder.mkdir()) {
				System.out.println("Folder created: " + folder.getPath());
			}
			if (!folder.isDirectory()) {
				sendtext("������̕ۑ���t�H���_���쐬�ł��܂���B");
				result = "A0";
				return false;
			}
			thumbInfoFile = new File(folder, getVideoBaseName() + ext);
		} else {
			thumbInfoFile = getThumbInfoFileFrom(Setting.getVideoFile(), ext);
		}
		if(thumbInfoFile==null){
			sendtext("������t�@�C����null�ł�");
			result = "A1";
			return false;
		}
		sendtext("������̕ۑ���");
		if (client == null){
			sendtext("���O�C�����ĂȂ��̂ɓ�����̕ۑ��ɂȂ�܂���");
			result = "A2";
			return false;
		}
		thumbInfo = client.getThumbInfoFile(Tag);
		if (stopFlagReturn()) {
			result = "A3";
			return false;
		}
		if (thumbInfo == null) {
			sendtext("������̎擾�Ɏ��s" + client.getExtraError());
			result = "A4";
			return false;
		}
		System.out.println("reading:" + thumbInfo);
		if(!saveThumbUser(thumbInfo, client)){
			System.out.println("���e�ҏ��̎擾�Ɏ��s");
			return false;
		}
		if(!saveThumbnailJpg(thumbInfo, client)){
			System.out.println("�T���l�C���摜�̎擾�Ɏ��s");
			return false;
		}
		//Path.fileCopy(thumbInfo, thumbInfoFile);
		String text = Path.readAllText(thumbInfo.getPath(), "UTF-8");
		text = text.replace("\n", "\r\n");
		PrintWriter pw;
		try {
			pw = new PrintWriter(thumbInfoFile, "UTF-8");
			pw.write(text);
			pw.flush();
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(thumbInfo.delete()){
			System.out.println("Deleted:" + thumbInfo);
		}
		sendtext("������̕ۑ��I��");
		return true;
	}

	private boolean saveThumbInfo(NicoClient client) {
		if(Setting.isSaveThumbInfo())
			return saveThumbInfo0(client);
		else
			return true;
	}

	private boolean saveThumbUser(Path infoFile, NicoClient client) {
		sendtext("���e�ҏ��̕ۑ�");
		Path userThumbFile = null;
		if(Setting.isSaveThumbUser()){
			String infoXml = Path.readAllText(infoFile.getPath(), "UTF-8");
			String userID = NicoClient.getXmlElement(infoXml, "user_id");
			if(userID==null || userID.isEmpty() || userID.equals("none")){
				sendtext("���e�҂̏�񂪂���܂���");
				result = "A5";
				return false;
			}
			System.out.println("���e��:"+userID);
			File userFolder = new File(Setting.getUserFolder());
			if (userFolder.mkdirs()){
				System.out.println("Folder created: " + userFolder.getPath());
			}
			if(!userFolder.isDirectory()){
				sendtext("���[�U�[�t�H���_���쐬�ł��܂���");
				result = "A6";
				return false;
			}
			userThumbFile = new Path(userFolder, userID + ".htm");
			String html = null;
			String ownerName = null;
			if(!userThumbFile.canRead()){
				userThumbFile = client.getThumbUserFile(userID, userFolder);
			}
			if(userThumbFile != null && userThumbFile.canRead()){
				html = Path.readAllText(userThumbFile.getPath(), "UTF-8");
				ownerName = NicoClient.getXmlElement(html, "title");
			}
			if(ownerName == null || ownerName.contains("����J�v���t�B�[��")){
				ownerName = null;
				userThumbFile = client.getUserInfoFile(userID, userFolder);
				if(userThumbFile != null && userThumbFile.canRead()){
					html = Path.readAllText(userThumbFile.getPath(), "UTF-8");
					ownerName = NicoClient.getXmlElement(html, "title");
				}
				if(ownerName==null){
					sendtext("���e�҂̏��̓���Ɏ��s");
					result = "A7";
					return false;
				}
			}
			int index = ownerName.lastIndexOf("����̃v���t�B�[���]");
			if(index > 0){
				ownerName = ownerName.substring(0,index);
			}
			index = ownerName.lastIndexOf("����̃��[�U�[�y�[�W �]");
			if(index > 0){
				ownerName = ownerName.substring(0,index) + "(�j�R���|����J)";
			}
			infoXml = infoXml.replace("</user_id>",
				"</user_id>\n<user>" + ownerName + "</user>");
			try {
				PrintWriter pw = new PrintWriter(infoFile, "UTF-8");
				pw.write(infoXml);
				pw.flush();
				pw.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		sendtext("���e�ҏ��̕ۑ��I��");
		return true;
	}

	private boolean setThumbnailJpg() {
		if (isVideoFixFileName()) {
			File folder = Setting.getVideoFixFileNameFolder();
			if (folder.mkdir()) {
				System.out.println("Folder created: " + folder.getPath());
			}
			if (!folder.isDirectory()) {
				sendtext("�T���l�C���摜�̕ۑ���t�H���_���쐬�ł��܂���B");
				result = "A9";
				return false;
			}
			thumbnailJpg = new File(folder, getVideoBaseName() + ".jpg");
		} else {
			File file = Setting.getVideoFile();
			if (file == null || !file.isFile() || file.getPath() == null) {
				thumbnailJpg = mkTemp(Tag + "_thumnail.jpg");
			}else{
				String path = file.getPath();
				int index = path.lastIndexOf(".");
				if (index > path.lastIndexOf(File.separator)) {
					path = path.substring(0, index) + ".jpg";		// �g���q��ύX
				}
				thumbnailJpg = new File(path);
			}
		}
		return true;
	}

	private boolean saveThumbnailJpg(Path infoFile, NicoClient client) {
		sendtext("�T���l�C���摜�̕ۑ�");
		thumbnailJpg = null;
		if(Setting.isSaveThumbnailJpg()){
			String infoXml = Path.readAllText(infoFile.getPath(), "UTF-8");
			String url = NicoClient.getXmlElement(infoXml, "thumbnail_url");
			if(url==null || url.isEmpty() || !url.startsWith("http")){
				sendtext("�T���l�C���摜�̏�񂪂���܂���");
				result = "A8";
				return false;
			}
			if(!setThumbnailJpg())
				return false;
			sendtext("�T���l�C���摜�̕ۑ���");
			if (!client.getThumbnailJpg(url+".L", thumbnailJpg)
				&& !client.getThumbnailJpg(url, thumbnailJpg)) {
				sendtext("�T���l�C���摜�̎擾�Ɏ��s" + client.getExtraError());
				result = "AA";
				return false;
			}
		}
		sendtext("�T���l�C���摜�̕ۑ��I��");
		return true;
	}

	private File getThumbInfoFileFrom(File file, String ext) {
		if (file == null || !file.isFile() || file.getPath() == null) {
			return mkTemp(THUMB_INFO + ext);
		}
		String path = file.getPath();
		int index = path.lastIndexOf(".");
		if (index > path.lastIndexOf(File.separator)) {
			path = path.substring(0, index);		// �g���q���폜
		}
		return new File(path + THUMB_INFO + ext);
	}

	private boolean makeNGPattern() {
		sendtext("NG�p�^�[���쐬��");
		try{
			String all_regex = "/((docomo|iPhone|softbank) (white )?)?.* 18[46]|18[46] .*/";
			String def_regex = "/((docomo|iPhone|softbank) (white )?)?18[46]/";
			String ngWord = Setting.getNG_Word().replaceFirst("^all", all_regex).replace(" all", all_regex);
			ngWord = ngWord.replaceFirst("^default", def_regex).replace(" default", def_regex);
			ngWordPat = NicoXMLReader.makePattern(ngWord);
			ngIDPat = NicoXMLReader.makePattern(Setting.getNG_ID());
			ngCmd = new CommandReplace(Setting.getNGCommand(), Setting.getReplaceCommand());
		}catch (Exception e) {
			sendtext("NG�p�^�[���쐬�Ɏ��s�B�����炭���K�\���̊ԈႢ�H");
			result = "70";
			return false;
		}
		sendtext("NG�p�^�[���쐬�I��");
		return true;
	}

	private Path mkTemp(String uniq){
		return Path.mkTemp(Tag + uniq);
	}

	private boolean convertComment(){
		sendtext("�R�����g�̒��ԃt�@�C���ւ̕ϊ���");
		File folder = Setting.getCommentFixFileNameFolder();
		if (isConvertWithComment()) {
			if (Setting.isAddTimeStamp() && isCommentFixFileName()) {
				// �����̃R�����g�t�@�C���i�ߋ����O�j�����邩��
				ArrayList<String> pathlist = detectFilelistFromComment(folder);
				if (pathlist == null || pathlist.isEmpty()){
					sendtext(Tag + ": �R�����g�t�@�C���E�ߋ����O�����݂��܂���B");
					result = "71";
					return false;
				}
				// VideoTitle �͌��������B
				if (pathlist.size() > 0) {			// 0 1.22r3e8, for NP4 comment ver 2009
					ArrayList<File> filelist = new ArrayList<File>();
					for (String path: pathlist){
						filelist.add(new File(folder, path));
					}
					CommentFile = mkTemp(TMP_COMBINED_XML);
					sendtext("�R�����g�t�@�C��������");
					if (!CombineXML.combineXML(filelist, CommentFile)){
						sendtext("�R�����g�t�@�C���������o���܂���ł����i�o�O�H�j");
						result = "72";
						return false;
					}
					if (dateUserFirst.isEmpty()) {
						//�R�����g�t�@�C���̍ŏ���date="integer"��T���� dateUserFirst �ɃZ�b�g
						dateUserFirst = getDateUserFirst(CommentFile);
					}
					listOfCommentFile = filelist;
				} else {
					// �R�����g�t�@�C���͂ЂƂ�����������
					// �����ɂ͗��Ȃ� 1.22r3e8, for NP4 comment ver 2009
				}
			}
			if (!isSaveComment()) {
				if (isCommentFixFileName()) {
					if (!Setting.isAddTimeStamp()){
						// �R�����g�t�@�C���͂ЂƂ�
						String commentfilename = detectTitleFromComment(folder);
						if(commentfilename == null){
							sendtext("�R�����g�t�@�C�����t�H���_�ɑ��݂��܂���B");
							result = "73";
							return false;
						}
						// VideoTitle �͌��������B
						CommentFile = new File(folder, commentfilename);
						if (!CommentFile.canRead()) {
							sendtext("�R�����g�t�@�C�����ǂݍ��߂܂���B");
							result = "74";
							return false;
						}
						if (dateUserFirst.isEmpty()) {
							//�R�����g�t�@�C���̍ŏ���date="integer"��T���� dateUserFirst �ɃZ�b�g
							dateUserFirst = getDateUserFirst(CommentFile);
						}
					} else {
						// �����ς�
					}
				} else {
					CommentFile = Setting.getCommentFile();
					if (!CommentFile.exists()) {
						sendtext("�R�����g�t�@�C�������݂��܂���B");
						result = "75";
						return false;
					}
					if (dateUserFirst.isEmpty()) {
						//�R�����g�t�@�C���̍ŏ���date="integer"��T���� dateUserFirst �ɃZ�b�g
						dateUserFirst = getDateUserFirst(CommentFile);
					}
				}
			}
			CommentMiddleFile = mkTemp(TMP_COMMENT);
			if(!convertToCommentMiddle(CommentFile, CommentMiddleFile)){
				sendtext("�R�����g�ϊ��Ɏ��s");
				CommentMiddleFile = null;
				result = "76";
				return false;
			}
			if(!CommentMiddleFile.canRead()){
				CommentMiddleFile = null;
				// But OK!
			}
		}
		return true;
	}

	private boolean convertOprionalThread(){
		sendtext("�I�v�V���i���X���b�h�̒��ԃt�@�C���ւ̕ϊ���");
		File folder = Setting.getCommentFixFileNameFolder();
		if (isConvertWithComment()) {
			if (isCommentFixFileName()) {
				if (Setting.isAddTimeStamp()) {
					// �t�H���_�w�莞�A�����̃I�v�V���i���X���b�h�i�ߋ����O�j�����邩��
					ArrayList<String> pathlist = detectFilelistFromOptionalThread(folder);
					if (pathlist == null || pathlist.isEmpty()){
						sendtext(Tag + ": �I�v�V���i���X���b�h�E�ߋ����O�����݂��܂���B");
						System.out.println("No optional thread.");
						OptionalThreadFile = null;
						return true;
					}
					// VideoTitle �͌��������B
					ArrayList<File> filelist = new ArrayList<File>();
					for (String path: pathlist){
						filelist.add(new File(folder, path));
					}
					OptionalThreadFile = mkTemp(TMP_COMBINED_XML2);
					sendtext("�I�v�V���i���X���b�h������");
					if (!CombineXML.combineXML(filelist, OptionalThreadFile)){
						sendtext("�I�v�V���i���X���b�h�������o���܂���ł����i�o�O�H�j");
						result = "77";
						return false;
					}
					if (dateUserFirst.isEmpty()) {
						//�R�����g�t�@�C���̍ŏ���date="integer"��T���� dateUserFirst �ɃZ�b�g
						dateUserFirst = getDateUserFirst(OptionalThreadFile);
					}
					listOfCommentFile.addAll(filelist);
				} else {
					// �t�H���_�w�莞�A�I�v�V���i���X���b�h�͂P��
					String filename = detectTitleFromOptionalThread(folder);
					if (filename == null || filename.isEmpty()){
						sendtext(Tag + ": �I�v�V���i���X���b�h���t�H���_�ɑ��݂��܂���B");
						System.out.println("No optional thread.");
						OptionalThreadFile = null;
						return true;
					}
					OptionalThreadFile = new File(folder, filename);
					if (dateUserFirst.isEmpty()) {
						//�R�����g�t�@�C���̍ŏ���date="integer"��T���� dateUserFirst �ɃZ�b�g
						dateUserFirst = getDateUserFirst(OptionalThreadFile);
					}
				}
			} else {
				// �t�@�C���w��̎�
				OptionalThreadFile = getOptionalThreadFile(Setting.getCommentFile());
				if (!OptionalThreadFile.exists()){
					sendtext("�I�v�V���i���X���b�h�����݂��܂���B");
					System.out.println("No optional thread.");
					OptionalThreadFile = null;
					return true;
				}
				if (dateUserFirst.isEmpty()) {
					//�R�����g�t�@�C���̍ŏ���date="integer"��T���� dateUserFirst �ɃZ�b�g
					dateUserFirst = getDateUserFirst(OptionalThreadFile);
				}
			}
			OptionalMiddleFile = mkTemp(TMP_OPTIONALTHREAD);
			if(!convertToCommentMiddle(OptionalThreadFile, OptionalMiddleFile)){
				sendtext("�I�v�V���i���X���b�h�ϊ��Ɏ��s");
				OptionalMiddleFile = null;
				result = "78";
				return false;
			}
			//�R�����g��������
			if(!OptionalMiddleFile.canRead()){
				OptionalMiddleFile = null;
				// But OK!
			}
		}
		return true;
	}

	private boolean convertOwnerComment(){
		sendtext("���e�҃R�����g�̒��ԃt�@�C���ւ̕ϊ���");
		File folder = Setting.getCommentFixFileNameFolder();
		if (isConvertWithOwnerComment()){
			if (!isSaveOwnerComment()) {
				if (isCommentFixFileName()) {
					String ownerfilename = detectTitleFromOwnerComment(folder);
					if(ownerfilename == null){
						sendtext("���e�҃R�����g�t�@�C�����t�H���_�ɑ��݂��܂���B");
					//	retValue = "80";
					//	return false;
						System.out.println("���e�҃R�����g�t�@�C�����t�H���_�ɑ��݂��܂���B");
						OwnerCommentFile = null;
						return true;
					}
					// VideoTitle �͌��������B
					OwnerCommentFile = new File(folder, ownerfilename);
					if (!OwnerCommentFile.canRead()) {
						sendtext("���e�҃R�����g�t�@�C�����ǂݍ��߂܂���B");
						result = "81";
						return false;
					}
				} else {
					OwnerCommentFile = Setting.getOwnerCommentFile();
					if (!OwnerCommentFile.exists()) {
						sendtext("���e�҃R�����g�t�@�C�������݂��܂���B");
					//	retValue = "82";
					//	return false;
						System.out.println("���e�҃R�����g�t�@�C�������݂��܂���B");
						OwnerCommentFile = null;
						return true;
					}
				}
			}
			OwnerMiddleFile = mkTemp(TMP_OWNERCOMMENT);
			//������ commentReplace�������
			if (!convertToCommentMiddle(OwnerCommentFile, OwnerMiddleFile)){
				sendtext("���e�҃R�����g�ϊ��Ɏ��s");
				OwnerMiddleFile = null;
				result = "83";
				return false;
			}
			//�R�����g��������
			if(!OwnerMiddleFile.canRead()){
				OwnerMiddleFile = null;
				ownerCommentNum = 0;
				// But OK!
			} else {
				try{
					FileInputStream fos = new FileInputStream(OwnerMiddleFile);
					ownerCommentNum = Util.readInt(fos);
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
					OwnerMiddleFile = null;
					result = "84";
					return false;
				}
			}
		}
		return true;
	}

	private void deleteCommentFile(){
		if (CommentFile != null && CommentFile.delete()) {
			System.out.println("Deleted: " + CommentFile.getPath());
		}
		if (OptionalThreadFile != null && OptionalThreadFile.delete()){
			System.out.println("Deleted: " + OptionalThreadFile.getPath());
		}
		deleteList(listOfCommentFile);
		if (OwnerCommentFile != null && OwnerCommentFile.delete()) {
			System.out.println("Deleted: " + OwnerCommentFile.getPath());
		}
	}

	private boolean convertToCommentMiddle(File commentfile, File middlefile) {
		if(!ConvertToVideoHook.convert(
				commentfile, middlefile, CommentReplaceList,
				ngIDPat, ngWordPat, ngCmd, Setting.getScoreLimit(),
				Setting.isLiveOperationConversion(), Setting.isPremiumColorCheck())){
			return false;
		}
		//�R�����g����0�̎��폜����
		try{
			FileInputStream fis = new FileInputStream(middlefile);
			int comment_num = Util.readInt(fis);
			fis.close();
			if(comment_num == 0){
				if(middlefile.delete()){
					System.out.println("Deleted 0 comment-file: " + middlefile.getPath());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean convertVideo() throws IOException {
		sendtext("����̕ϊ����J�n");
		Stopwatch.start();
		if(!VideoFile.canRead()){
			sendtext("���悪�ǂݍ��߂܂���");
			result = "90";
			return false;
		}
		/*�r�f�I���̊m��*/
		File folder = Setting.getConvFixFileNameFolder();
		if (!chekAspectVhookOption(VideoFile, wayOfVhook)){
			result = "91";
			return false;
		}
		if (Setting.isConvFixFileName()) {
			if (folder.mkdir()) {
				System.out.println("Created folder: " + folder.getPath());
			}
			if (!folder.isDirectory()) {
				sendtext("�ϊ���̕ۑ���t�H���_���쐬�ł��܂���B");
				result = "92";
				return false;
			}
			String conv_name = VideoTitle;
			if (conv_name == null){
				conv_name = "null";
			}
			if (!Setting.isNotAddVideoID_Conv()||conv_name.isEmpty()) {//�t������Ȃ�
				conv_name = Setting.isChangeTitleId()?
						VideoTitle + VideoID : VideoID + VideoTitle;
			}
			if (conv_name.isEmpty()) {
				sendtext("�ϊ���̃^�C�g��������܂���(�r�f�I�t�@�C�������m��ł��܂���)�B");
				result = "93";
				return false;
			}

			if (Setting.isAddOption_ConvVideoFile()){
				byte[] dirName = new File(folder, conv_name)
					.getAbsolutePath().getBytes("Shift_JIS");
				// �t�H���_������������ꍇ
				if (dirName.length > (255-12)){
					conv_name = VideoID;
				}
				conv_name = conv_name.trim();	// In Windows API, cant make dir as " ABC" nor "ABC "
				folder = new File(folder, conv_name);
				if (folder.mkdir()) {
					System.out.println("Created folder: " + folder.getPath());
				}
				if (!folder.isDirectory()) {
					sendtext("����(FFmpeg�ݒ薼)�t�@�C���̕ۑ���t�H���_���쐬�ł��܂���B");
					result = "94";
					return false;
				}
				conv_name = MainOption + InOption + OutOption;
				if (!getFFmpegVfOption().isEmpty()){
					conv_name = vfilter_flag + " " + getFFmpegVfOption() + conv_name;
				}
				conv_name = getFFmpegOptionName() + safeAsciiFileName(conv_name);
				dirName = new File(folder, conv_name).getAbsolutePath().getBytes("Shift_JIS");
				// �t�@�C��������������ꍇ
				if (dirName.length > (255 - 3)){
					int len = conv_name.length() - (dirName.length - (255 - 3));
					if (len < 1){
						sendtext("�쐬����r�f�I�t�@�C�������������܂��B");
						result = "95";
						return false;
					}
					conv_name = conv_name.substring(0, len);
				}
				conv_name = conv_name.trim();
			}
			conv_name = safeAsciiFileName(conv_name);
			ConvertedVideoFile = new File(folder, conv_name + ExtOption);
		} else {
			String filename = Setting.getConvertedVideoFile().getPath();
			if (!filename.endsWith(ExtOption)) {
				filename = filename.substring(0, filename.lastIndexOf('.'))
						+ ExtOption;
				ConvertedVideoFile = new File(filename);
			} else {
				ConvertedVideoFile = Setting.getConvertedVideoFile();
			}
		}
		if (ConvertedVideoFile.getAbsolutePath().equals(VideoFile.getAbsolutePath())){
			sendtext("�ϊ���̃t�@�C�������ϊ��O�Ɠ����ł�");
			result = "96";
			return false;
		}
		if(ConvertedVideoFile.isFile() && ConvertedVideoFile.canRead()){
			sendtext("�ϊ���̃t�@�C���͊��ɑ��݂��܂�");
			System.out.println("�ϊ���̃t�@�C���͊��ɑ��݂��܂�");
			String otherFilename = "1"+ ConvertedVideoFile.getName();
			if(ConvertedVideoFile.renameTo(new File(ConvertedVideoFile.getParentFile(),otherFilename))){
				sendtext("�����̃t�@�C�������l�[�����܂���");
				System.out.println("�����̃t�@�C�������l�[�����܂���"+otherFilename);
			}else{
				sendtext("�����̃t�@�C�������l�[���o���܂���ł����B�㏑�����܂�");
				System.out.println("�����̃t�@�C�������l�[���o���܂���ł����B�㏑�����܂�");
			}
		}
		int code = converting_video();
		Stopwatch.stop();
		//vhext(nicovideo���O)���R�s�[����
		File log_vhext = new File(".","[log]vhext.txt");
		File video_vhext = Path.mkTemp(Tag+"[log]vhext.txt");
		if(video_vhext.exists()){
			if(log_vhext.delete()){
			}
			Path.fileCopy(video_vhext, log_vhext);
		}else{
			System.out.println(Tag+"[log]vhext.txt ���L��܂���.");
		}
		if (code == 0) {
			sendtext("�ϊ�������ɏI�����܂����B");
			System.out.println(lastFrame);
			return true;
		} else if (code == CODE_CONVERTING_ABORTED) { /*���f*/

		} else {
			if(errorLog==null||errorLog.isEmpty())
				if(ffmpeg!=null)
					errorLog = ffmpeg.getLastError().toString();
			sendtext("�ϊ��G���[�F(" + code + ") "+ getLastError());
		}
		result = "97";
		return false;
	}

	private static String safeAsciiFileName(String str) {
		//Windows�t�@�C���V�X�e���ň�����Ascii�������
		str = str.replace('/', '_')
			.replace('\\', '_')
			.replace('?', '_')
			.replace('*', '_')
			.replace(':', ';')		//	:(colon) to ;(semi colon)
			.replace('|', '_')
			.replace('\"', '\'')
			.replace('<', '(')
			.replace('>', ')')
//			.replace('.', '�D')		// .(dot) is let there
			.replaceAll(" +", " ")
			.trim();
		return str;
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
	private JLabel watchArea = new JLabel();
	private ArrayList<CommentReplace> CommentReplaceList = new ArrayList<CommentReplace>();
	private boolean checkFps;
	private File imgDir;
	private Aspect outAspect;
	private VPlayer vplayer = null;

	void downloadPage(String url){
		ArrayList<String[]> plist = new ArrayList<String[]>();
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
				if(Stopwatch.getSource()!=null){
					watchArea = Stopwatch.getSource();
				}
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
					converter = new Converter(
							vid,
							Time,
							mySetting,
							Status,
							new ConvertStopFlag(new JButton(),null,null,null),
							MovieInfo,
							watchArea,
							parent,
							sb);
					converter.start();
					while(converter!=null && !converter.isFinished()){
						if(StopFlag.needStop()){
							//�q�����~�߂�
							final ConvertStopFlag stopFlag = converter.getStopFlag();
							if(stopFlag!=null && !stopFlag.isFinished()){
								stopFlag.stop();
							}
							return;
						}
						try {
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
			StopFlag.finish();
			if(StopFlag.needStop())
				result="FF";
			System.out.println("LastStatus:[" + result + "]" + Status.getText());
			System.out.println("VideoInfo: " + MovieInfo.getText());
			Stopwatch.clear();
			if(sbRet!=null){
				sbRet.append("RESULT=" + result + "\n");
				sbRet.append("FAIL="+ngn+"\n");
			}
		}
	}

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

	@Override
	public void run() {
		if(!watchvideo){
			//not watch video get try mylist
			downloadPage(Url);
			return;
		}
		Stopwatch.clear();
		Stopwatch.start();
		try {
			if (!checkOK()) {
				return;
			}
			NicoClient client = getNicoClient();
			if (client != null){
				if (!client.isLoggedIn()){
					return;
				}
				if (!client.getVideoInfo(Tag, WatchInfo, Time, Setting.isSaveWatchPage())) {
					if(Tag==null || Tag.isEmpty()){
						sendtext("URL/ID�̎w�肪����܂��� " + client.getExtraError());
					}else if(!client.loginCheck()){
						sendtext("���O�C�����s " + BrowserKind.getName() + " " + client.getExtraError());
					}else{
						sendtext(Tag + "�̏��̎擾�Ɏ��s " + client.getExtraError());
					}
					return;
				}
				if (stopFlagReturn()) {
					return;
				}
				VideoTitle = client.getVideoTitle();
				VideoBaseName = Setting.isChangeTitleId()?
					VideoTitle + VideoID : VideoID + VideoTitle;
				sendtext(Tag + "�̏��̎擾�ɐ���");
			}

			Stopwatch.show();
			if (!saveVideo(client) || stopFlagReturn()) {
				return;
			}

			Stopwatch.show();
			if (!saveComment(client) || stopFlagReturn()){
				return;
			}

			Stopwatch.show();
			if (!saveOwnerComment(client) || stopFlagReturn()) {
				return;
			}

			Stopwatch.show();
			if(!saveThumbInfo(client)){
				if(isSaveConverted())
					System.out.println("�ǉ����̎擾�Ɏ��s���܂��������s���܂��B");
				else {
					String tstr = Status.getText();
					if(isSaveComment()) {
						tstr = "�R�����g�擾�����A" + tstr;
					}
					if(isSaveVideo())
						tstr = "[�x��]����擾�����A" + tstr;
					else
						tstr = "[�x��]" + tstr;
					sendtext(tstr);
					return;
				}
			}
			if(stopFlagReturn()){
				return;
			}

			Stopwatch.show();
			System.out.println("�ϊ��O���ԁ@" + Stopwatch.formatElapsedTime());

			if (!isSaveConverted()) {
				sendtext("����E�R�����g��ۑ����A�ϊ��͍s���܂���ł����B");
				return;
			}

			Stopwatch.show();
			if(!makeNGPattern() || stopFlagReturn()){
				return;
			}

			Stopwatch.show();
			if (!convertOwnerComment() || stopFlagReturn()){
				return;
			}

			Stopwatch.show();
			if (!convertComment() || stopFlagReturn()) {
				return;
			}

			Stopwatch.show();
			if (!convertOprionalThread() || stopFlagReturn()) {
				return;
			}

			Stopwatch.show();
			if (convertVideo()) {
				// �ϊ�����
				if(parent!=null)
					fileQueue = parent.getQueue();
				if(fileQueue!=null)
					fileQueue.offer(ConvertedVideoFile);
				if (isDeleteCommentAfterConverting()
					&& CommentFile != null) {
					deleteCommentFile();
				}
				if (isDeleteVideoAfterConverting()
					&& VideoFile != null) {
					if (VideoFile.delete()) {
						System.out.println("Deleted: " + VideoFile.getPath());
					}
				}
				if (CommentMiddleFile != null) {
					if (CommentMiddleFile.delete()) {
						System.out.println("Deleted: " + CommentMiddleFile.getPath());
					}
				}
				if (OwnerMiddleFile != null){
					if (OwnerMiddleFile.delete()) {
						System.out.println("Deleted: " + OwnerMiddleFile.getPath());
					}
				}
				if (OptionalMiddleFile != null) {
					if (OptionalMiddleFile.delete()) {
						System.out.println("Deleted: " + OptionalMiddleFile.getPath());
					}
				}
				if (Setting.isAutoPlay()){
					playConvertedVideo();
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			StopFlag.finish();
			Stopwatch.show();
			Stopwatch.stop();
			System.out.println("�ϊ����ԁ@" + Stopwatch.formatLatency());
			System.out.println("LastStatus:[" + result + "]" + Status.getText());
			System.out.println("VideoInfo: " + MovieInfo.getText());
			System.out.println("LastFrame: "+ lastFrame);
			if(sbRet!=null){
				sbRet.append("RESULT=" + result + "\n");
				if(!dateUserFirst.isEmpty()){
					sbRet.append("DATEUF=" + dateUserFirst + "\n");
				}
			}
		}
	}

	// �ϊ�����Đ�
	public void playConvertedVideo() {
		try {
			File convertedVideo = fileQueue.poll();
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
			sendtext("(�L�́M)���ʂ��\n�K�b");
			ex.printStackTrace();
		}
	}
	private void deleteList(ArrayList<File> list){
		if (list== null)	{
			return;
		}
		boolean b = true;
		for (File file : list){
			b = file.delete() && b;
		}
		if (!b){
			System.out.println("Can't delete list of all Comment.");
		}
	}

	/**
	 * CWS�Ȃ�FWS�ɕϊ�����<br/>
	 * ���̌�A�A�X�y�N�g��𔻒肵Vhook��I���A�I�v�V������ǂݍ��ݐݒ肷��
	 * @param video : File
	 * @param way : int  1 or 2
	 * Output videoAspect : Aspect
	 * Output VideoFile : File
	 * Output selectedVhook : File  vhook.exe
	 * OUTPUT ExtOption, MainOption, InOption, OutOption
	 */
	private boolean chekAspectVhookOption(File video, int way){
		fwsFile = null;
		try {
			fwsFile = Cws2Fws.createFws(video);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (fwsFile != null){
//			VideoFile = fwsFile;
			video = fwsFile;
		}else{
			if (Cws2Fws.isCws(video)){
				sendtext("SWF��FWS�ϊ��Ɏ��s���܂���");
				//return false;
			}
		}
		VideofileInfo info = new VideofileInfo(video, ffmpeg, Status, StopFlag, Stopwatch);
		videoAspect = info.getAspect();
		if(videoLength <= 0){
			videoLength = info.getDuration();
		}
		frameRate = info.getFrameRate();
		checkFps = Setting.enableCheckFps();
		fpsUp = Setting.getFpsUp();
		fpsMin = Setting.getFpsMin();
		System.out.println("frameRate:"+frameRate+",fpsUp:"+fpsUp+",fpsMin:"+fpsMin);
		String str;
		if (videoAspect == null || videoAspect == Aspect.ERROR){
			str = "Analize Error   ";
			videoAspect = Aspect.NORMAL;
		} else {
			str = videoAspect.explain() + "  ";
		}
		isPlayerWide = videoAspect.isWide();
		if (Setting.isZqPlayer()){
			//
		} else {
			if (way == 1){
				if (VhookNormal == null){
					if (!isPlayerWide){
						str = "��" + str;
					}
					isPlayerWide = true;
				} else {
					if (isPlayerWide){
						str = "��" + str;
					}
					isPlayerWide = false;
				}
			}
		}
		String auto = "";
		if (way==3){
			auto = "����";
		}
		if (way==2) {
			auto = "�����I�� ";
		}
		if (Setting.isZqPlayer()){
			selectedVhook = VhookQ;
			MovieInfo.setText(auto + "�g��Vhook Q " + str);
		} else if (isPlayerWide){
			selectedVhook = VhookWide;
			MovieInfo.setText(auto + "�g��Vhook ���C�h " + str);
		} else {
			selectedVhook = VhookNormal;
			MovieInfo.setText(auto + "�g��Vhook �]�� " + str);
		}
		if (!detectOption(isPlayerWide,Setting.isZqPlayer())){
			sendtext("�ϊ��I�v�V�����t�@�C���̓ǂݍ��݂Ɏ��s���܂����B");
			return false;
		}
		if(!addAdditionalOption(isPlayerWide,Setting.isZqPlayer())){
			sendtext("�ǉ��I�v�V�����̐ݒ�Ɏ��s���܂����B");
			return false;
		}

		//replace�`�F�b�N
		if(Setting.getReplaceOptions()!=null){
			replace3option(Setting.getReplaceOptions());
		}
		ffmpegVfOption = getvfOption();

		//AAC copy if -aacp set
		if(getAacCopyFlag()){
			//Outpotion contains "-aacp"
			//check input audio codec
			if(info.isAudioContainsAac()){
				//if input-audio-codec is AAC
				//then -acodec or -codec:a or -c:a audio-codec
				//and codec is within aac faac ffaac libvo-aacanc
				//set copy to acodec
				String[] acodecs;
				if((acodecs = getAudioCodecKV(outputOptionMap))!=null){
					if(acodecs[1].toLowerCase().contains("aac")){
						replaceOption(outputOptionMap,acodecs[0],"copy");
						System.out.println("Changed: "+acodecs[0]+" "+acodecs[1]+" -> copy");
					}
				}else if((acodecs = getAudioCodecKV(mainOptionMap))!=null){
					//then -acodec or -codec:a or -c:a audio-codec
					//and then codec is aac faac ffaac libvo-aacanc
					if(acodecs[1].toLowerCase().contains("aac")){
						replaceOption(mainOptionMap,acodecs[0],"copy");
						System.out.println("Changed: "+acodecs[0]+" "+acodecs[1]+" -> copy");
					}
				}
			}
		}

		inSize = videoAspect.getSize();
		setSize = getSetSize();	//videoSetSize="width"x"height"
		padOption = getPadOption();		//padOption=width:height:x:y
		outSize = getOutSize();
		outAspect = videoAspect;
		if (setSize != null){
			//setSize=width:height in -s WIDTHxHEIGHT
			outAspect = toAspect(setSize,outAspect);
		}
		if (outSize != null){
			//outSize=width:height in -vf outs=w:h
			outAspect = toAspect(outSize, outAspect);
			setSize = outSize;
			printOutputSize(setSize,outAspect);
			outputOptionMap.put("-s", setSize.replace(':', 'x'));
			return true;
		}
		if (getSameAspectMaxFlag()){
			//Outoption contains "-samx"
			//check and set outAspect & setSize to be same as input video
			if(!outAspect.equals(videoAspect)){
				double out_aspect = outAspect.getValue();
				int outw = outAspect.getWidth();
				int outh = outAspect.getHeight();
				double video_aspect = videoAspect.getValue();
				if(out_aspect < video_aspect){
					// ow / oh < w / h -> oh ��ύX
					outh = toMod4(outw / video_aspect);
				}else if(out_aspect > video_aspect){
					// ow / oh > w / h -> ow ��ύX
					outw = toMod2(outh * video_aspect);
				}
				outAspect = new Aspect(outw, outh);
				setSize = outAspect.getSize();
				printOutputSize(setSize,outAspect);
				outputOptionMap.put("-s", setSize.replace(':', 'x'));
				return true;
			}
		}
		if (padOption != null){
			//padOption=width:height:videox:videoy in -vf pad=w:h:x:y
			printOutputSize(padOption,outAspect);
			printOutputSize(inSize,outAspect);
		}else
		if (setSize != null){
			//setSize=width:height in -s WIDTHxHEIGHT
			printOutputSize(setSize,outAspect);
		} else {
			//inSize=width:height
			printOutputSize(inSize,outAspect);
		}
		// ropt
		String ropt = getRopt();
		if(!ropt.isEmpty()){
			System.out.println(" framerate="+ropt);
		}
		return true;
	}

	void printOutputSize(String sizestr, Aspect aspect){
		int commentWidth = 640;		//���h
		int commentHeight = 384;	//���h
		if(Setting.isZqPlayer()){
			commentWidth = 800;		//Qwatch����
			commentHeight = 480;
		}
		aspect = toAspect(sizestr, aspect);
		int width = aspect.getWidth();
		int height = aspect.getHeight();
		System.out.println("Output Video Area " + width + ":" + height);
		//width height�͏o�͓���̑傫��(outs�w�莞�͂��̃T�C�Y)
		System.out.println("Video "+aspect.getSize());
		double rate;
		if (Setting.isZqPlayer()){
			if(aspect.isQWide()){
				rate = (double)width / commentWidth;
				height = toMod4(commentHeight * rate);
			}else{
				rate = (double)height / commentHeight;
				width = toMod2(commentWidth * rate);
			}
		} else {
			if(isPlayerWide){
				rate = (double)width / commentWidth;
				height = toMod4(commentHeight * rate);
			}else{
				rate = (double)height / commentHeight;
				width = toMod2(commentWidth * rate);
			}
		}
		System.out.println("Output Commetnt Area " + width + ":" + height + " Wide? " + isPlayerWide);
		//width height�͏o�̓R�����g�̑傫���i������͂ݏo���Ȃ��j
		return;
	}

	private int toMod4(double d){
		return ((int)(d / 4.0 + 0.5)) * 4;
	}

	private int toMod2(double d){
		return ((int)(d / 2.0 + 0.5)) * 2;
	}

	private Aspect toAspect(String str,Aspect defaultAspect){
		String[] list = str.split(":");
		int width = defaultAspect.getWidth();
		if(list.length>=1 && !list[0].equals("0")){
			try {
				width = Integer.parseInt(list[0]);
			} catch (NumberFormatException e){
				e.printStackTrace();
			}
		}
		int height = defaultAspect.getHeight();
		if(list.length>=2 && !list[1].equals("0")){
			try {
				height = Integer.parseInt(list[1]);
			} catch(NumberFormatException e){
				e.printStackTrace();
			}
		}
		return new Aspect(width, height);
	}
	private String getSetSize(){
		String size = outputOptionMap.get("-s");
		if(size!=null && size.contains("x"))
			return size.replace('x', ':');
		return null;
	}
	private String getPadOption() {
		return getFromVfOpotion("pad=");
	}

	private String getOutSize(){
		//outSize=width:height in -vf outs=w:h
		String outs = getFromVfOpotion("outs=");
		String outs_str = "outs=" + outs;
		if(outs != null){
			if((outs_str).equals(getFFmpegVfOption())){
				setFfmpegVfOption("");
			} else if(getvfOption().startsWith(outs_str)){
				setFfmpegVfOption(getFFmpegVfOption().replace(outs_str + ",", ""));
			} else {
				setFfmpegVfOption(getFFmpegVfOption().replace("," + outs_str, ""));
			}
		}
		return outs;
	}
	private boolean getSameAspectMaxFlag(){
		//-samx
		return outputOptionMap.remove("-samx") != null;
	}
	private boolean getAacCopyFlag(){
		//-aacp
		return outputOptionMap.remove("-aacp") != null;
	}
	private String[] getAudioCodecKV(HashMap<String,String> map){
		String[] pair = new String[2];
		String value = "";
		String[] keys = {"-acodec","-codec:a","-c:a"};
		for (String key:keys){
			value = map.get(key);
			if(value!=null && value.toLowerCase().contains("aac")){
				pair[0] = key;
				pair[1] = value;
				return pair;
			}
		}
		return null;
	}
	private boolean replaceOption(HashMap<String, String> map, String key, String value){
		if(map.containsKey(key)){
			map.put(key, value);
			return true;
		}
		return false;
	}
	private String getRopt(){
		//-r or -r:v
		String value = "-r";
		value = outputOptionMap.get("-r");
		if(value==null)
			value = mainOptionMap.get("-r");
		if(value==null)
			value = outputOptionMap.get("-r:v");
		if(value==null)
			value = mainOptionMap.get("-r:v");
		if(value==null)
			value = "";
		return value;
	}

	private String getFromVfOpotion(String prefix){
		for(String arg: getFFmpegVfOption().split(",")){
			if(arg.startsWith(prefix)){
				return arg.substring(prefix.length());
			}
		}
		return null;
	}

	boolean addAdditionalOption(boolean wide, boolean isQ) {
		addOption = "";
		if(isQ){
			addOption = Setting.getZqAddOption();
		} else if(wide){
			addOption = Setting.getWideAddOption();
		}else{
			addOption = Setting.getAddOption();
		}
		if(addOption.isEmpty()){
			return true;
		}
		setOptionMap(addOption, addOptionMap);
//		�d�l�ύX MainOpt InOpt�͒u�������Ȃ�
		for(String key : addOptionMap.keySet())
			outputOptionMap.put(key, addOptionMap.get(key));
		return true;
	}

	private static final int CODE_CONVERTING_ABORTED = 100;

	private void setOption1(File infile){
		ffmpeg.setCmd("-y ");
		ffmpeg.addMap(mainOptionMap);
		ffmpeg.addCmd(" ");
		ffmpeg.addMap(inputOptionMap);
		ffmpeg.addCmd(" -i ");
		ffmpeg.addFile(infile);
		ffmpeg.addCmd(" ");
	}

	private void setOption2(){
		ffmpeg.addCmd(" ");
		ffmpeg.addMap(outputOptionMap);
		ffmpeg.addCmd(" -metadata");
		ffmpeg.addCmd(" \"title="+VideoTitle+"\"");
		ffmpeg.addCmd(" -metadata");
		ffmpeg.addCmd(" \"comment="+VideoID+"\"");
		ffmpeg.addCmd(" ");
	}

	private boolean setOption3(File outfile){
		if (!Setting.isVhookDisabled()) {
			if(!addVhookSetting(ffmpeg, selectedVhook, isPlayerWide)){
				return false;
			}
		} else {
			ffmpeg.addCmd(" "+vfilter_flag+" ");
			ffmpeg.addCmd(getFFmpegVfOption());
		}
		ffmpeg.addCmd(" ");
		ffmpeg.addFile(outfile);
		return true;
	}

	private int execOption(){
		int code;
		System.out.println("arg:" + ffmpeg.getCmd());
		code = ffmpeg.exec(Status, CODE_CONVERTING_ABORTED, StopFlag, Stopwatch);
		errorLog = ffmpeg.getErrotLog().toString();
		lastFrame = ffmpeg.getLastFrame();
		return code;
	}

	private int convFLV(File videoin, File videoout){
		int code = -1;
		setOption1(videoin);
		setOption2();
		if(!setOption3(videoout))
			return code;
		code = execOption();
		return code;
	}

	private int conv_fpsUp(File videoin, File videoout){
		int code = -1;
		/*
		 * ffmpeg -r fpsUp
		 */
		setOption1(videoin);
		ffmpeg.addCmd(" -r " + fpsUp);
		String out_option_t = outputOptionMap.get("-t");
		if(out_option_t!=null)
			ffmpeg.addCmd(" -t "+out_option_t);
		String out_option_ss = outputOptionMap.get("-ss");
		if(out_option_ss!=null)
			ffmpeg.addCmd(" -ss "+out_option_ss);
		ffmpeg.addCmd(ConvertingSetting.getDefOptsFpsUp());
		// -acodec copy -vsync 1 -vcodec libx264 -qscale 1 -f mp4
		ffmpeg.addFile(videoout);

		code = execOption();
		if(code==0){
			// -itsoffset�폜 ���s�ς�
			inputOptionMap.remove("-itsoffset");
			mainOptionMap.remove("-itsoffset");
			// -ss�폜  ���s�ς�
			inputOptionMap.remove("-ss");
			mainOptionMap.remove("-ss");
			// out�� -ss �͂��̂܂܎c��
			out_option_ss = outputOptionMap.get("-ss");
			if(out_option_ss!=null){
				// �o�͂�-itsoffset�� -ss�ɂ��
				inputOptionMap.put("-itsoffset", out_option_ss);
			}
			// -t �͂��̂܂܎c���ėǂ�
		}
		return code;
	}

	private int convSWF_JPG(File videoin, File videoout){
		int code = -1;
		//�o��
		ffmpeg.setCmd("-y -i ");
		ffmpeg.addFile(videoin);
		ffmpeg.addCmd(ConvertingSetting.getDefOptsSwfJpeg());
		// -an -vcodec copy -r 1 -f image2
		ffmpeg.addFile(videoout);
		code = execOption();
		return code;
	}

	private int convJPG_MP4(File videoin, File videoout){
		int code = -1;
		//
		// frame check
		//
		// JPG�֑ؑ��x�w�肷��?
		String frames = ffmpeg.getLastFrame();
		int frame = 0;
		int index = frames.indexOf("frame=");
		if(index >=0){
			frames = frames.substring(index+6).trim();
			index = (frames+" ").indexOf(" ");
			frames = frames.substring(0, index);
			try{
				frame = Integer.decode(frames);
			}catch(NumberFormatException e){
				frame = 0;
			}
		}
		if(frame == 0)
			frame = 1;
		double rate = 1.0;
		if(videoLength > 0 && frame > 1){
			rate = (double)frame / (double)videoLength;
		}
		System.out.printf("Frame= %d, Rate= %.5f(fps)\n", frame, rate);
		String out_t = outputOptionMap.get("-t");
		double t0 = 0.0;
		if(out_t!=null){
			try{
				t0 = Double.parseDouble(out_t);
			}catch(NumberFormatException e){
				t0 = 0.0;
			}
		}
		double tl = (double)videoLength;
		if(tl == 0.0)
			tl = t0;
		else if(t0 != 0.0)
			tl = Math.min(t0, tl);
		// tl==0(���Ȃ�) �܂��� tl�͍ŏ���
		double length_frame = 1.0 /rate;
		System.out.printf("Frame= %.2f(sec/frame), Rate= %.5f(fps)\n", length_frame, rate);
	//	if(tl != 0.0){
	//		tl += length_frame;
	//	}
		System.out.printf("Frame= %d, Rate= %.5f(fps)\n", frame, rate);

		//File outputAvi = new File(imgDir,"huffyuv.avi");
		ffmpeg.setCmd(" -loop 1 -r " + Double.toString(rate));
		ffmpeg.addCmd(" -itsoffset " + Double.toString(length_frame));
		ffmpeg.addCmd(" -y -i ");
		ffmpeg.addFile(videoin);
		ffmpeg.addCmd(" -shortest ");
		if(tl!=0.0)
			ffmpeg.addCmd(" -t " + tl);
		ffmpeg.addCmd(ConvertingSetting.getDefOptsJpegMp4());
		// -an -vcodec libx264 -qscale 1 -pix_fmt yuv420p -f mp4
		ffmpeg.addFile(videoout);
		code = execOption();
		return code;
	}

	private int convMix(File videoin, File audioin, File videoout){
		int code = -1;
		double fps = 25.0;
		if(checkFps && fps < fpsMin){
			fps = fpsUp;
		}
		/*
		 * ����������
		 * ffmpeg.exe -shortest -y -i fws_tmp.swf -itsoffset 1.0 -i avi4.avi
		 *  -vcodec libxvid -acodec libmp3lame -ab 128k -ar 44100 -ac 2 fwsmp4.avi
		 */
		ffmpeg.setCmd("-y -i ");
		ffmpeg.addFile(audioin);	// audio, must be FWS_SWF
		ffmpeg.addCmd(" -i ");
		ffmpeg.addFile(videoin);	// visual
		ffmpeg.addCmd(" -map 1:v -map 0:a ");
		String out_option_t = outputOptionMap.get("-t");
		if(out_option_t!=null)
			ffmpeg.addCmd(" -t "+out_option_t);
		ffmpeg.addCmd(" -r " + fps);
		ffmpeg.addCmd(ConvertingSetting.getDefOptsMix());
		// -acodec copy -vcodec libx264 -qscale 1 -pix_fmt yuv420p -f mp4
		ffmpeg.addFile(videoout);
		code = execOption();
		return code;
	}

	private int convFLV_audio(File input, File output){
		return convFLV_audio(input,output,".\\bin\\b32.jpg");
	}

	private int convFLV_audio(File input, File output, String thumbname) {
		int code = -1;
		File thumbfile;
		if(thumbname==null||thumbname.isEmpty()||thumbname.equals(MainFrame.THUMB_DEFALT_STRING)){
			//�T���l�C���I��,����
			thumbfile = new File(Setting.getVideoFixFileNameFolder(),getVideoBaseName()+".jpg");
			if(!thumbfile.isFile()){
				if(setThumbnailJpg()){
					if(thumbnailJpg!=null && thumbnailJpg.isFile()){
						thumbfile = thumbnailJpg;
					}else{
						thumbfile = new File(".\\bin\\b32.jpg");
					}
				}else {
					NicoClient client = getNicoClient();
					if(saveThumbInfo0(client) && saveThumbnailJpg(thumbInfo, client)){
						thumbfile = thumbnailJpg;
					}
				}
			}
		}else{
			String currect_dir = System.getenv("CD");
			System.out.println("CD:"+currect_dir);
			thumbfile = new File(currect_dir, thumbname);
		}
		if(!thumbfile.canRead()){
			System.out.println("�T���l�C�����ǂ߂܂���F"+thumbfile.getPath());
			sendtext("�T���l�C�����ǂ߂܂���");
			thumbfile = new Path(".\\bin\\b32.jpg");
		}else{
			// �T���l�C�����e���|�����[�ɃR�s�[�ijava�͓ǂ߂�̂ɂȂ���ffmpeg���ǂ߂Ȃ��̂Łj
			File tempthumb = Path.mkTemp("t.jpg");
			FileInputStream fis = null;
			FileOutputStream fos = null;
			boolean copyok = false;
			try{
				byte[] buf = new byte[4096];
				fis = new FileInputStream(thumbfile);
				fos = new FileOutputStream(tempthumb);
				int len = 0;
				while ((len = fis.read(buf, 0, buf.length)) > 0) {
					fos.write(buf, 0, len);
					Stopwatch.show();
				}
				copyok = true;
			}catch(IOException e){
				e.printStackTrace();
			}finally{
				try{
					if(fis!=null){
						fis.close();
					}
					if(fis!=null){
						fos.flush();
						fos.close();
					}
				}catch(Exception e){
				}
			}
			if(copyok){
				thumbfile = tempthumb;
			}
		}
		if(!thumbfile.canRead()){
			//�ǂ����Ă��ǂ߂Ȃ��ꍇ
			System.out.println("�T���l�C�����ǂ߂܂���F"+thumbfile.getPath());
			sendtext("��փT���l�C�����ǂ߂܂���");
			errorLog = "��փT���l�C�����ǂ߂܂���";
			code = 198;
			return code;
		}
		code = convFLV_thumbaudio(thumbfile, input, output);
		return code;
	}

	int convFLV_thumbaudio(File thumbin, File audioin, File videoout){
		int code = -1;
		// �T���l�C���̃A�X�y�N�g��͖���
		/*
		 * ffmpeg -y mainoption -loop 1 -shortest -i thmbnail_picture -i input
		 * outoption -map 0:0 -map 1:a [vhookOption]  output
		 */
		double fps = 25.0;
		if(checkFps && fps < fpsMin)
			fps = fpsUp;
		ffmpeg.setCmd("-y ");
		ffmpeg.addMap(mainOptionMap);
		ffmpeg.addCmd(" -loop 1 -i ");
		ffmpeg.addFile(thumbin);
		ffmpeg.addCmd(" ");
		ffmpeg.addMap(inputOptionMap);
		ffmpeg.addCmd(" -i ");
		ffmpeg.addFile(audioin);
		ffmpeg.addCmd(" -map 0:v -map 1:a ");
		ffmpeg.addCmd(" -r " + fpsUp);
		setOption2();
		if(!setOption3(videoout))
			return code;
		code = execOption();
		return code;
	}

	private int converting_video() {
		int code = -1;
		infoStack = new InfoStack(MovieInfo);
		File input = VideoFile;
		if(fwsFile!=null)
			input = fwsFile;
		if (!Cws2Fws.isFws(input) && !Cws2Fws.isCws(input)) {
			//�ʏ��FLV
			// fps up check
			if(checkFps && frameRate < fpsMin){
				//FPS�ϊ��K�v
				if(Setting.isUseFpsFilter()){
					//FPS Filter�I��
					System.out.println("FPS filter");
					String vfoptsave = getFFmpegVfOption();
					String vfopt = "fps=fps="+fpsUp
						+ ",scale="+outAspect.getSize();	// -s �I�v�V������ -vf scale=w:h �Ƃ��Đ�ɒǉ�
					if(!vfoptsave.isEmpty()){
						vfopt += "," + vfoptsave;
					}
					setFfmpegVfOption(vfopt);
					/*
					 * ffmpeg.exe -y mainoption inoption -i infile outoptiont -vf fps=fps=fpsUP [vhookOption] outfile
					 */
					infoStack.pushText("Filter");
					code = convFLV(input, ConvertedVideoFile);
					infoStack.popText();
					setFfmpegVfOption(vfoptsave);
					if(code == CODE_CONVERTING_ABORTED){
						return code;
					}
					if (code == 0){
						//fpsfilter�ϊ�����
						return code;
					}
					System.out.println("("+code+")fps filter�Ɏ��s ");
					errorLog += "\nfps filter�Ɏ��s "+ getLastError();
					System.out.println("���s\n");	//���s���[�h
				}

				// 2�p�XFPS�ϊ�
				File outputFps = Path.mkTemp("fpsUp"+ConvertedVideoFile.getName());
				System.out.println("FLV Up "+fpsUp+"fps");
				infoStack.pushText("FLV "+fpsUp);
				code = conv_fpsUp(input, outputFps);
				infoStack.popText();
				if(code == CODE_CONVERTING_ABORTED){
					return code;
				}
				if(code != 0){
					//error
					System.out.println("("+code+")fps�ϊ��Ɏ��s ");
					errorLog += "\nfps�ϊ��Ɏ��s "+ getLastError();
					if(Setting.canSoundOnly()){
						System.out.println("�R�����g�Ɖ����������������܂�");
						infoStack.pushText("SoundOnly");
						code = convFLV_audio(input, ConvertedVideoFile, Setting.getDefaultThumbnail());
						infoStack.popText();
					}
					return code;
				}
				if (code == 0){
					//fps�ϊ�����
					input = outputFps;
				}
			}

			//FPS�ϊ��Ȃ�
			/*
			 * ffmpeg.exe -y mainoption inoption -i infile outoptiont [vhookOption] outfile
			 */
			System.out.println("FLV �]���ʂ�");
			String vfoptsave = getFFmpegVfOption();
			if(checkFps && Setting.isUseFpsFilter()){
				String vfopt = "";
				String ropt = getRopt();
				if(ropt != null && !ropt.isEmpty()){
					vfopt = "fps=fps="+ropt
						+ ",scale="+outAspect.getSize();
					// -s �I�v�V������ -vf scale=w:h �Ƃ��Đ�ɒǉ�
					System.out.println("FPS filter -r "+ropt);
					if(!vfoptsave.isEmpty()){
						vfopt += "," + vfoptsave;
					}
					setFfmpegVfOption(vfopt);
				}
			}
			code = convFLV(input, ConvertedVideoFile);
			infoStack.popText();
			setFfmpegVfOption(vfoptsave);
		}
		else {
			// nm���� FWS����
			if(!Setting.isSwfTo3Path()){
				// nm�Ή����Ȃ�
				if(checkFps && frameRate < fpsMin){
					/*
					 * ffmpeg -r 25.0
					 */
					File outputFps = Path.mkTemp("fpsUp"+ConvertedVideoFile.getName());
					System.out.println("FWS fpsUp");
					infoStack.pushText("FWS fpsUp");
					code = conv_fpsUp(input, outputFps);
					infoStack.popText();
					if(code == CODE_CONVERTING_ABORTED){
						return code;
					}
					if (code != 0){
						System.out.println("("+code+")fps�ϊ��Ɏ��s ");
						errorLog += "\nfps�ϊ��Ɏ��s "+ getLastError();
						if(Setting.canSoundOnly()){
							System.out.println("�R�����g�Ɖ����������������܂�");
							infoStack.pushText("SoundOnly");
							code = convFLV_audio(input, ConvertedVideoFile, Setting.getDefaultThumbnail());
							infoStack.popText();
						}
						return code;
					}else{
						//fps�ϊ�����
						input = outputFps;
					}
				}

				/*
				 * ffmpeg.exe -y mainoption inoption -i infile outoptiont [vhookOption] outfile
				 */
				System.out.println("FWS �]���ʂ�");
				infoStack.pushText("FWS");
				code = convFLV(input, ConvertedVideoFile);
				infoStack.popText();
				return code;
			} else {
				System.out.println("FWS 3path");
				// try 3 path
				/*
				 * SWF�t�@�C����JPEG�`���ɍ���
				 * ffmpeg.exe -y -i fws_tmp.swf -an -vcodec copy -f image2 %03d.jpg
				 */
				//�o�͐�����
				imgDir = Path.mkTemp("IMG"+VideoID);
				if(imgDir.mkdir())
					System.out.println("Created folder - " + imgDir);
				File outputImg = new File(imgDir,"%03d.jpeg");
				System.out.println("outputImg="+outputImg);
				System.out.println("Tring SWF to .number.JPG");
				infoStack.pushText("SWF->JPG");
				code = convSWF_JPG(input, outputImg);
				infoStack.popText();
				if(code == CODE_CONVERTING_ABORTED){
					return code;
				}
				if(code!=0){
					if (Setting.canSoundOnly()){
						// jpeg�ɕϊ��ł��Ȃ��ꍇ�͉����݂̂ɂ���
						code = convFLV_audio(input, ConvertedVideoFile);
					}
					return code;
				}
				/*
				 * JPEG�t�@�C����MP4�`���ɍ���
				 * ffmpeg.exe -r 1/4 -y -i %03d.jpg -an -vcodec huffyuv -f avi huffjpg.avi
				 */
				//�o��
				File outputAvi = new File(imgDir,"huffyuv.mp4");
				System.out.println("outputImg="+outputImg);
				System.out.println("outputAvi="+outputAvi);
				System.out.println("Tring JPG to .MP4");
				infoStack.pushText("JPG->MP4");
				code = convJPG_MP4(outputImg, outputAvi);
				infoStack.popText();
				if(code == CODE_CONVERTING_ABORTED){
					return code;
				}
				if(code!=0){
					if (Setting.canSoundOnly()){
						// jpeg��mp4�ɕϊ��ł��Ȃ��ꍇ�͉����݂̂ɂ���
						code = convFLV_audio(input, ConvertedVideoFile);
					}
					return code;
				}
				/*
				 * ����������
				 * ffmpeg.exe -y -i fws_tmp.swf -itsoffset 1.0 -i avi4.avi
				 *  -vcodec libxvid -acodec libmp3lame -ab 128k -ar 44100 -ac 2 fwsmp4.avi
				 */
				File outputMix = new File(imgDir,"mix.mp4");
				System.out.println("Tring MP4+sound to .MP4");
				infoStack.pushText("MP4 Mix");
				code = convMix(outputAvi, input, outputMix);
				infoStack.popText();
				if(code == CODE_CONVERTING_ABORTED){
					return code;
				}
				if(code!=0){
					if (Setting.canSoundOnly()){
						code = convFLV_audio(input, ConvertedVideoFile);
					}
					return code;
				}
				/*
				 * �R�����g������
				 * ffmpeg.exe -y -i fws_tmp.swf -itsoffset 1.0 -i avi4.avi
				 *  -vcodec libxvid -acodec libmp3lame -ab 128k -ar 44100 -ac 2 fwsmp4.avi
				 */
				System.out.println("Tring MIX & comment to .mp4");
				infoStack.pushText("FWS comment");
				code = convFLV(outputMix,ConvertedVideoFile);
				infoStack.popText();
				if(code!=0){
					if (Setting.canSoundOnly()){
						code = convFLV_audio(input, ConvertedVideoFile);
					}
					return code;
				}
			}
			if (fwsFile != null){
				// fwsFile.delete();	// For DEBUG
			}
		}
		return code;
	}

	private boolean addVhookSetting(FFmpeg ffmpeg, File vhookExe, boolean isWide){
		FFmpeg ffmpeg1 = new FFmpeg("#");
		ffmpeg1.setCmd(" ");
		if(!addVhookSetting2014(ffmpeg1, vhookExe, isWide)){
			return false;
		}
		Iterator<String> it = ffmpeg1.getCmdArrayList().iterator();
		// ffmpeg1�ɐݒ肳�ꂽ����������o��iterator
		StringBuilder sb = new StringBuilder();
		String s = "";
		it.next();	//�ŏ��͓ǂݔ�΂�
		while(it.hasNext()){
			s = it.next();
			s = s.replaceAll(VFILTER_FLAG, VFILTER_FLAG2);
			if(s.equals(VFILTER_FLAG2)){
				sb.append(" "+vfilter_flag+" ");
				s = it.next();
				int index = s.indexOf("vhext=");
				if(index > 0){
					index += "vhext=".length();
					sb.append(s.substring(0, index));
					s = s.substring(index);
					s = vf_quote(s);	// vhext= �̃I�v�V������ video filter�p�� quote����
				}else{
					return false;
				}
			}
			sb.append(s);
			sb.append(' ');
		}
		s = sb.substring(0);
		ffmpeg.addCmd(s);
		return true;
	}

	/*
	 *  Character escape convention
	 *  1st File or path	'\'-> '/'			(Path.toUnixPath)
	 *  2nd String Encode	Unicode->ShiftJis	(URLEncoder)
	 *  3rd Filter Quote	,:;[\]				(See below)
	 */
	private String vf_quote(String s) {
		String r = s
				.replaceAll(",", "%2C")
				.replaceAll(":", "%3A")
				.replaceAll(";", "%3B")
				.replaceAll("=", "%3D")
				.replaceAll("\\[", "%5B")
				.replaceAll("\\\\", "%5C")
				.replaceAll("\\]", "%5D");
		return r;
	}

	private boolean addVhookSetting2014(FFmpeg ffmpeg, File vhookExe, boolean isWide) {
		try {
			String encoding = "Shift_JIS";
			ffmpeg.addCmd(" "+vfilter_flag+" \"");
			if (!getFFmpegVfOption().isEmpty()){
				ffmpeg.addCmd(getFFmpegVfOption());
				ffmpeg.addCmd(",");
			}else{
				// -s �I�v�V������ -vf scale=w:h �Ƃ��Đ�ɒǉ�
				ffmpeg.addCmd("scale="+outAspect.getSize());
				ffmpeg.addCmd(",");
			}
			ffmpeg.addCmd("vhext=");
			ffmpeg.addFile(vhookExe);
			if(CommentMiddleFile!=null){
				ffmpeg.addCmd("|--data-user:");
				ffmpeg.addCmd(URLEncoder.encode(
					Path.toUnixPath(CommentMiddleFile), encoding));
				ffmpeg.addCmd("|--show-user:");
				ffmpeg.addCmd(Setting.getVideoShowNum());
			}
			if(OwnerMiddleFile!=null){
				ffmpeg.addCmd("|--data-owner:");
				ffmpeg.addCmd(URLEncoder.encode(
					Path.toUnixPath(OwnerMiddleFile), encoding));
				int usershowcomment = 0;
				try {
				usershowcomment = Integer.parseInt(Setting.getVideoShowNum());
				} catch (NumberFormatException e1){
					usershowcomment = 0;
				}
				int ownershowcomment = Integer.parseInt(NicoClient.STR_OWNER_COMMENT);
				if(ownershowcomment > ownerCommentNum){
					ownershowcomment = ownerCommentNum;
				}
				if(usershowcomment > 0 && usershowcomment < ownershowcomment){
					ownershowcomment = usershowcomment;
				}
				ffmpeg.addCmd("|--show-owner:" + ownershowcomment);
			}
			if (OptionalMiddleFile!=null){
				ffmpeg.addCmd("|--data-optional:");
				ffmpeg.addCmd(URLEncoder.encode(
					Path.toUnixPath(OptionalMiddleFile), encoding));
				ffmpeg.addCmd("|--show-optional:");
				ffmpeg.addCmd(Setting.getVideoShowNum());
				if (Setting.isOptionalTranslucent()) {
					ffmpeg.addCmd("|--optional-translucent");
				}
			}
			if(Setting.getFontPath()!=null){
				ffmpeg.addCmd("|--font:");
				ffmpeg.addCmd(URLEncoder.encode(
					Path.toUnixPath(Setting.getFontPath()), encoding));
			}
			ffmpeg.addCmd("|--font-index:");
			ffmpeg.addCmd(Setting.getFontIndex());
			ffmpeg.addCmd("|--shadow:" + Setting.getShadowIndex());
			if (Setting.isVhook_ShowConvertingVideo()) {
				ffmpeg.addCmd("|--enable-show-video");
			}
			if (Setting.isFixFontSize()) {
				ffmpeg.addCmd("|--enable-fix-font-size");
			}
			if (Setting.isOpaqueComment()) {
				ffmpeg.addCmd("|--enable-opaque-comment");
				ffmpeg.addCmd("|--opaque:" + Setting.getOpaqueRate());
			}
			if (Setting.isZqPlayer()){
				ffmpeg.addCmd("|--enable-Qwatch");
			}
			if (isWide){
				ffmpeg.addCmd("|--nico-width-wide");
			}
			ffmpeg.addCmd("|--input-size:" + inSize);
			if(setSize != null){
				ffmpeg.addCmd("|--set-size:" + setSize);
			}
			if(padOption != null){
				ffmpeg.addCmd("|--pad-option:" + padOption);
			}
			if(outSize!=null){
				ffmpeg.addCmd("|--out-size:" + outSize);
			}
			if (videoLength > 0){
				ffmpeg.addCmd("|--video-length:");
				ffmpeg.addCmd(Integer.toString(videoLength));
			}
			if (Setting.isFontHeightFix()){
				ffmpeg.addCmd("|--font-height-fix-ratio:"
						+ Setting.getFontHeightFixRaito());
			}
			String comment_speed = Setting.getCommentSpeed();
			if (Setting.isSetCommentSpeed() &&
				comment_speed != null && !comment_speed.isEmpty()){
				ffmpeg.addCmd("|--comment-speed:"
					+ URLEncoder.encode(comment_speed, encoding));
			}
			String extra = Setting.getExtraMode();
			if(extra.contains("-April=")){
				int index = extra.indexOf("-April=");
				aprilFool = extra.substring(index + "-April=".length());
				index = (aprilFool + " ").indexOf(" ");
				aprilFool = aprilFool.substring(0, index).trim();
				extra = extra.replace("-April=" + aprilFool, "");
			}
			if(aprilFool!=null){
				ffmpeg.addCmd("|--april-fool:" + aprilFool);
			}
			if(extra.contains("-wakuiro=")){
				//�g�F�w��F����@=���甼�p�X�y�[�X�܂ł������Ƃ���
				int index = extra.indexOf("-wakuiro=");
				wakuiro = extra.substring(index + "-wakuiro=".length());
				index = (wakuiro + " ").indexOf(" ");
				wakuiro = wakuiro.substring(0, index);
				extra = extra.replace("-wakuiro=" + wakuiro, "");
			}
			if(wakuiro!=null && !wakuiro.isEmpty()){
				wakuiro = Chat.makeWakuiro(wakuiro);
				ffmpeg.addCmd("|--wakuiro:" + wakuiro);
			}
			if(extra.contains("debug")){
				ffmpeg.addCmd("|--debug-print");
				extra = extra.replace("-debug", "").replace("debug", "");
			}
			if(!extra.isEmpty()){
				ffmpeg.addCmd("|--extra-mode:" + extra.replaceAll(" +", " ").trim().replace(' ', '+'));
			}
			if(!getRopt().isEmpty()){
				ffmpeg.addCmd("|--fr:" + getRopt());
			}
			if(Setting.isEnableCA()){
				ffmpeg.addCmd("|--enable-CA");
				ffmpeg.addCmd("|--font-dir:"
					+ URLEncoder.encode(Path.toUnixPath(fontDir) + "/", encoding));
				ffmpeg.addCmd("|--font-list:");
				ffmpeg.addCmd("0:1+");
				ffmpeg.addCmd(getFontUrl(gothicFont, encoding));
				ffmpeg.addCmd("+1:");
				ffmpeg.addCmd(getFontUrl(simsunFont, encoding));
				ffmpeg.addCmd("+2:");
				ffmpeg.addCmd(getFontUrl(gulimFont, encoding));
				ffmpeg.addCmd("+3:");
				ffmpeg.addCmd(getFontUrl(arialFont, encoding));
				ffmpeg.addCmd("+4:");
				ffmpeg.addCmd(getFontUrl(georgiaFont, encoding));
//				ffmpeg.addCmd(getFontUrl(msuigothicFont, encoding));
				ffmpeg.addCmd("+5:");
				ffmpeg.addCmd(getFontUrl(arialUnicodeFont, encoding));
				ffmpeg.addCmd("+6:");
				ffmpeg.addCmd(getFontUrl(devabagariFont, encoding));
				ffmpeg.addCmd("+7:");
				ffmpeg.addCmd(getFontUrl(tahomaFont, encoding));
				ffmpeg.addCmd("+8:");
				ffmpeg.addCmd(getFontUrl(mingliuFont, encoding));
				String newMinchoPath = getFontUrl(newMinchoFont, encoding);
				if(newMinchoFont.equals(simsunFont)){
					newMinchoPath = "1+" + newMinchoPath;	//NSIMSUN is index 1 of simsun.ttc
				}
				ffmpeg.addCmd("+9:");
				ffmpeg.addCmd(newMinchoPath);
				ffmpeg.addCmd("+10:");
				ffmpeg.addCmd(getFontUrl(estrangeloEdessaFont, encoding));
				ffmpeg.addCmd("+11:");
				ffmpeg.addCmd(getFontUrl(gujaratiFont, encoding));
				ffmpeg.addCmd("+12:");
				ffmpeg.addCmd(getFontUrl(bengalFont, encoding));
				ffmpeg.addCmd("+13:");
				ffmpeg.addCmd(getFontUrl(tamilFont, encoding));
				ffmpeg.addCmd("+14:");
				ffmpeg.addCmd(getFontUrl(laooFont, encoding));
				ffmpeg.addCmd("+15:");
				ffmpeg.addCmd(getFontUrl(gurmukhiFont, encoding));
				ffmpeg.addCmd("+16:");
				ffmpeg.addCmd(getFontUrl(kannadaFont, encoding));
				ffmpeg.addCmd("+17:");
				ffmpeg.addCmd(getFontUrl(thaanaFont, encoding));
				ffmpeg.addCmd("+18:");
				ffmpeg.addCmd(getFontUrl(malayalamFont, encoding));
				ffmpeg.addCmd("+19:");
				ffmpeg.addCmd(getFontUrl(teluguFont, encoding));
				if(Setting.isUseLineSkip()){
					ffmpeg.addCmd("|--use-lineskip-as-fontsize");
				}
				if(Setting.isUseExtraFont()){
					ffmpeg.addCmd("|--extra-font:");
					ffmpeg.addCmd(URLEncoder.encode(
						Setting.getExtraFontText(), encoding));
				}
			}
			if (Setting.isDisableOriginalResize()){
				ffmpeg.addCmd("|--disable-original-resize");
			}
			if (Setting.isFontWidthFix()){
				ffmpeg.addCmd("|--font-width-fix-ratio:"
					+ Setting.getFontWidthFixRaito());
			}
			ffmpeg.addCmd("|--end-of-argument\"");
			return true;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
	}

	private String getFontUrl(File fontfile,String enc) throws UnsupportedEncodingException {
		if(fontDir.isDirectory() && fontfile.getParentFile().equals(fontDir)){
			return URLEncoder.encode(fontfile.getName(), enc);
		}
		return URLEncoder.encode(Path.toUnixPath(fontfile), enc);
	}

	public boolean isFinished() {
		return StopFlag.isFinished();
	}

	private boolean stopFlagReturn() {
		if (StopFlag.needStop()) {
			sendtext("���~���܂����B");
			return true;
		}
		return false;
	}

	public ConvertStopFlag getStopFlag() {
		return this.StopFlag;
	}

	private String ExtOption;

	private String InOption;

	private String OutOption;

	private String MainOption;

	private String ffmpegOptionName;

	private String ffmpegVfOption = "";

	private LinkedHashMap<String, String> inputOptionMap = new LinkedHashMap<String, String>(16);

	private LinkedHashMap<String, String> outputOptionMap = new LinkedHashMap<String, String>(40);

	private LinkedHashMap<String, String> mainOptionMap = new LinkedHashMap<String, String>(16);

	private LinkedHashMap<String, String> addOptionMap = new LinkedHashMap<String, String>(16);
	private final String[] SINGLE_KEYWORD = {"-an","-vn","-y","-shortest","-loop_input","-samx","-help","-h",};

	private boolean isSingleKeyword(String keyword){
		for(String s:SINGLE_KEYWORD){
			//�O�̃L�[���[�h����
			if(keyword.equals(s))
				return true;
		}
		return false;
	}

	private boolean setOptionMap(String option, HashMap<String,String> optionMap){
		if (option==null || option.isEmpty()){
			return false;
		}
		Matcher m = Pattern.compile("[^ ]+").matcher(option);
		int start = 0;
		int end = 0;
		int flag = 0;
		// 0: for keyword, 1: for parameter, 2: for additional parameter or next keyword
		boolean processing_quote = false;
		String w = "";
		String keyword = null;
		String parameter = null;
		String non_keyword = "";
		int begin_quote = 0;
		int end_quote = 0;
		char c = 0;
		while(m.find()){
			start = m.start();
			end = m.end();
			w = option.substring(start, end);	//w is word
			c = w.charAt(0);
			if(processing_quote){
				// ""�X�L�b�v��
				if(start < end_quote)
					continue;
				processing_quote = false;
				end_quote = 0;
			}
			if(c=='"'){
				//quote�̍Ō�܂Ŏ��s���ăX�L�b�v������
				begin_quote = start;
				end_quote = option.indexOf("\" ", begin_quote+1);
				if(end_quote<0){
					end_quote = option.length();	//������̏I���܂�
				}
				w = option.substring(begin_quote, end_quote);
				if(w.charAt(w.length()-1)!='"'){
					w += "\"";	//parameter��""���܂�
				}
				processing_quote = true;
			}
			switch(flag){
			case 0:
				if(c!='-'){
					System.out.print("�x���@�L�[���[�h�ł͂���܂���:"+w);
					non_keyword += w + " ";
					continue;
				}
				keyword = w;
				parameter = "";
				flag = 1;
				continue;
			case 1:
				if(c=='-'){
					//�O�̃L�[���[�h�`�F�b�N
					if(isSingleKeyword(keyword)){
						//�O�̃L�[���[�h����
						optionMap.put(keyword, "");
						keyword = w;
						parameter = "";
						flag = 1;
						continue;
					}
					System.out.print("�x���@'-'�g���Ă��܂�:"+w);
				}
				parameter = w + " ";
				flag = 2;
				continue;
			case 2:
				if(c=='-'){
					//���̃L�[���[�h?
					//�O�̃p�����[�^����
					if(keyword.equals("-map"))
						optionMap.put(keyword+" "+parameter.trim(), "");
					else
						optionMap.put(keyword, parameter.trim());
					keyword = w;
					parameter = "";
					flag = 1;
					continue;
				}
				parameter += w + " ";
				flag = 2;
				continue;
			}
			System.out.println("�o�O���Ă�");
		}
		//parameter���c���Ă�����o�^(�o�̓t�@�C��?)
		if(parameter!=null){
			optionMap.put(keyword, parameter.trim());
		}
		if(!non_keyword.isEmpty()){
			optionMap.put(":non_keyord", non_keyword.trim());
		}
		return true;
	}

	boolean detectOption(boolean isWide, boolean isQ) {
		File option_file = null;
		ffmpegOptionName = "���ړ���";
		if(isQ){
			option_file = Setting.getZqOptionFile();
			if(option_file == null){
				ExtOption = Setting.getZqCmdLineOptionExt();
				InOption = Setting.getZqCmdLineOptionIn();
				OutOption = Setting.getZqCmdLineOptionOut();
				MainOption = Setting.getZqCmdLineOptionMain();
			}
		} else if (!isWide) {
			option_file = Setting.getOptionFile();
			if(option_file == null){
				ExtOption = Setting.getCmdLineOptionExt();
				InOption = Setting.getCmdLineOptionIn();
				OutOption = Setting.getCmdLineOptionOut();
				MainOption = Setting.getCmdLineOptionMain();
			}
		} else {
			option_file = Setting.getWideOptionFile();
			if(option_file == null){
				ExtOption = Setting.getWideCmdLineOptionExt();
				InOption = Setting.getWideCmdLineOptionIn();
				OutOption = Setting.getWideCmdLineOptionOut();
				MainOption = Setting.getWideCmdLineOptionMain();
			}
		}
		if (option_file != null) {
			try {
				Properties prop = new Properties();
				prop.loadFromXML(new FileInputStream(option_file));
				ExtOption = prop.getProperty("EXT");
				InOption = prop.getProperty("IN");
				OutOption = prop.getProperty("OUT");
				MainOption = prop.getProperty("MAIN");
				if (ExtOption == null || InOption == null || OutOption == null
						|| MainOption == null) {
					return false;
				}
				ffmpegOptionName = option_file.getName().replace(".xml", "");
			} catch (IOException ex) {
				ex.printStackTrace();
				return false;
			}
		}
		//�I�v�V�����Ɋg���q���܂�ł��܂����ꍇ�ɂ��Ή���
		if(ExtOption != null && !ExtOption.startsWith(".")){
			ExtOption = "."+ExtOption;
		}
		setOptionMap(InOption, inputOptionMap);
		setOptionMap(MainOption, mainOptionMap);
		setOptionMap(OutOption, outputOptionMap);
		return true;
	}
	private void replace3option(Map<String, String> map) {
		boolean replaced = false;
		for(Entry<String, String> pair : map.entrySet()){
			String key = pair.getKey();
			String value = pair.getValue();
			replaced = false;
			if(mainOptionMap.containsKey(key)){
				mainOptionMap.put(key, value);
				replaced = true;
			}
			if(inputOptionMap.containsKey(key)){
				inputOptionMap.put(key, value);
				replaced = true;
			}
			if(outputOptionMap.containsKey(key)){
				outputOptionMap.put(key, value);
				replaced = true;
			}
			if(!replaced){
				LinkedHashMap<String,String> newoptionmap = new LinkedHashMap<String, String>(40);
				// �V����key�� OuOption�̐擪�ɒǉ�
				newoptionmap.put(key, value);
				newoptionmap.putAll(outputOptionMap);
				outputOptionMap.clear();
				outputOptionMap = newoptionmap;
			}
		}
	}

	/**
	 * @param option :String
	 * @param key
	 * @param value
	 * @return replaced :String
	 */
	private String getvfOption() {
		String vfIn, vfOut, vfMain, vfOpt;
		vfIn = getvfOption(inputOptionMap);
		deletevfOption(inputOptionMap);
		vfOut = getvfOption(outputOptionMap);
		deletevfOption(outputOptionMap);
		vfMain = getvfOption(mainOptionMap);
		deletevfOption(mainOptionMap);
		vfOpt = vfIn;
		if (vfOpt.isEmpty()){
			vfOpt = vfMain;
		} else if (!vfMain.isEmpty()){
			vfOpt += "," + vfMain;
		}
		if (vfOpt.isEmpty()){
			vfOpt = vfOut;
		} else if (!vfOut.isEmpty()){
			vfOpt += "," + vfOut;
		}
		return vfOpt;
	}

	private static final String VFILTER_FLAG = "-vfilters";
	private static final String VFILTER_FLAG2 = "-vf";
	private String vfilter_flag = VFILTER_FLAG2;
	private String getvfOption(Map<String,String> map){
		if(map==null)
			return "";
		String vfopt = null;
		vfopt = map.get(VFILTER_FLAG);
		if(vfopt==null){
			vfopt = map.get(VFILTER_FLAG2);
		}
		if(vfopt==null)
			return "";
		vfopt = vfopt.trim();
		if(vfopt.charAt(0)=='"')
			vfopt = vfopt.substring(1);
		if(vfopt.charAt(vfopt.length()-1)=='"')
			vfopt = vfopt.substring(0, vfopt.length()-1);
		return vfopt;
	}
	private void deletevfOption(Map<String, String> map) {
		if(map==null)
			return;
		map.remove(VFILTER_FLAG);
		map.remove(VFILTER_FLAG2);
	}
	public String getFFmpegOptionName() {
		return ffmpegOptionName;
	}
	public String getFFmpegVfOption() {
		return ffmpegVfOption;
	}
	public void setFfmpegVfOption(String vfOption) {
		ffmpegVfOption = vfOption;
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

	private void setVideoTitleIfNull(String path) {
		String videoTitle = VideoTitle;
		if (videoTitle == null){
			videoTitle = getTitleFromPath(path, VideoID);
			// �ߋ����O�������폜
			String regex = "\\[" + WayBackDate.STR_FMT_REGEX + "\\]";
			videoTitle = videoTitle.replaceAll(regex, "");
		//	int index = videoTitle.lastIndexOf("[");
		//		//�ߋ����O��[YYYY/MM/DD_HH:MM:SS]���Ō�ɕt��
		//	if (index >= 0){
		//		videoTitle = videoTitle.substring(0, index);
		//	}
			System.out.println("Title<" + videoTitle + ">");
			VideoTitle = videoTitle;
		}
	}

	String detectTitleFromVideo(File dir){
		if (dir == null){ return null; }
		String list[] = dir.list(DefaultVideoIDFilter);
		if(list == null){ return null; }
		for (int i = 0; i < list.length; i++) {
			if (list[i].startsWith(VideoID)) {
				String path = list[i];
				if(path.endsWith(".flv") ||
				   path.endsWith(".mp4") && Setting.isChangeMp4Ext()){
					setVideoTitleIfNull(path);
					return path;
				}
				OtherVideo = path;
				continue;
			}
		}
		return null;
	}

	public String detectTitleFromConvertedVideo(File dir){
		if (dir == null){ return null; }
		String list[] = dir.list(DefaultVideoIDFilter);
		if(list == null){ return null; }
		for (int i = 0; i < list.length; i++) {
			if (list[i].startsWith(VideoID)) {
				String path = list[i];
				int index = path.lastIndexOf(".");
				if (index < 0) continue;
				String ext = path.substring(index).toLowerCase();
				if(ext.equals(".flv") || ext.equals(".mp4")  || ext.equals(".avi")  || ext.equals(".mpg") ){
					return path;
				}
			}
		}
		return null;
	}

	private String detectTitleFromComment(File dir){
		String list[] = dir.list(DefaultVideoIDFilter);
		if(list == null){ return null; }
		for (int i = 0; i < list.length; i++) {
			String path = list[i];
			if (!path.endsWith(".xml") || path.endsWith(OWNER_EXT)
					|| path.endsWith(OPTIONAL_EXT)){
				continue;
			}
			setVideoTitleIfNull(path);
			return path;
		}
		return null;
	}

	private static final String TCOMMENT_EXT =".txml";
	private String detectTitleFromOwnerComment(File dir){
		String list[] = dir.list(DefaultVideoIDFilter);
		if(list == null){ return null; }
		for (int i = 0; i < list.length; i++) {
			String path = list[i];
			String ext;
			if (path.endsWith(OWNER_EXT)){
				ext = OWNER_EXT;
			} else if (path.endsWith(TCOMMENT_EXT)) {
				ext = TCOMMENT_EXT;
			} else {
				continue;
			}
			setVideoTitleIfNull(path.replace(ext,""));
			return path;
		}
		return null;
	}

	private String detectTitleFromOptionalThread(File dir){
		String list[] = dir.list(DefaultVideoIDFilter);
		if(list == null){ return null; }
		for (int i = 0; i < list.length; i++) {
			String path = list[i];
			if (!path.endsWith(OPTIONAL_EXT)){
				continue;
			}
			setVideoTitleIfNull(path.replace(OPTIONAL_EXT, ""));
			return path;
		}
		return null;
	}

	private ArrayList<String> detectFilelistFromComment(File dir){
		String list[] = dir.list(DefaultVideoIDFilter);
		if (list == null) { return null; }
		ArrayList<String> filelist = new ArrayList<String>();
		for (String path : list){
			if (!path.endsWith(".xml") || path.endsWith(OWNER_EXT)
					|| path.endsWith(OPTIONAL_EXT)){
				continue;
			}
			setVideoTitleIfNull(path);
			filelist.add(path);
		}
		return filelist;
	}

	private ArrayList<String> detectFilelistFromOptionalThread(File dir){
		String list[] = dir.list(DefaultVideoIDFilter);
		if (list == null) { return null; }
		ArrayList<String> filelist = new ArrayList<String>();
		for (String path : list){
			if (!path.endsWith(OPTIONAL_EXT)){
				continue;
			}
			setVideoTitleIfNull(path.replace(OPTIONAL_EXT, ""));
			filelist.add(path);
		}
		return filelist;
	}

	/*
	 * 	���e�҃R�����g�Ɋւ���t�@�C�����^�O�Ɗg���q������
	 * �@�@	�@Ver.1.25�ȍ~�̂�����΂� �A�Ɉȉ���ǉ�
	 * 			�I�v�V���i���X���b�h�� VideoID + VideoTitile + "{Optional}.xml"
	 * 			���̉ߋ����O�� VideoID + VideoTitile + "[YYYY�^MM�^DD_HH�Fmm�Fss]{Optional}.xml"
	 * 		�A����̂�����΂�
	 * 			���[�U�R�����g = VideoID + VideoTitle + ".xml"
	 * 			�ߋ����O       = VideoID + VideoTitle + "[YYYY�^MM�^DD_HH�Fmm�Fss].xml"
	 * 			���e�҃R�����g = VideoID + VideoTitle + "[Owner].xml"
	 * 		�B NicoBrowser�g��1.4.4�̏ꍇ
	 * 			���[�U�R�����g = VideiID + VideoTitle + ".xml"
	 * 			���e�҃R�����g = VideoID + VideoTitle + ".txml"
	 * 		�CNNDD�Ȃ�
	 * 			���[�U�R�����g = VideoTitle + VideoID + ".xml"
	 * 			���e�҃R�����g = VideoTitle + VideoID + "[Owner].xml"
	 * 		�CNicoPlayer�Ȃ�
	 * 			���[�U�R�����g = VideoTitle + "(" + Tag + ")" + ".xml"
	 * 			�ߋ����O       = VideoTitie + "(" + Tag + ")[" + YYYY�NMM��DD��HH��MM��SS�b + "}.xml"
	 * 			���e�҃R�����g = VideoTitle + "(" + Tag + "){Owner].xml"
	 *
	 *
	 */

	/*
	 * videoID�̈ʒu�͖��֌W�ɍ폜
	 * �g���q������΂��̑O�܂�
	 */
	private String getTitleFromPath(String path, String videoID){
		if (path.indexOf(videoID) >= 0){
			path = path.replace(videoID, "");	// videoID�̈ʒu�͖��֌W�ɍ폜
		}
		// �g���q������΂��̑O�܂�
		if (path.lastIndexOf(".") > path.lastIndexOf(File.separator)){
			path = path.substring(0, path.lastIndexOf("."));
		}
		return path;
	}

	public File getConvertedVideoFile() {
		return ConvertedVideoFile;
	}

}
