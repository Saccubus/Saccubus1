package saccubus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import saccubus.FFmpeg.Aspect;
import saccubus.conv.Chat;
import saccubus.conv.CombineXML;
import saccubus.conv.CommandReplace;
import saccubus.conv.CommentReplace;
import saccubus.conv.ConvertToVideoHook;
import saccubus.conv.NicoXMLReader;
import saccubus.net.BrowserInfo;
import saccubus.net.BrowserInfo.BrowserCookieKind;
import saccubus.net.Gate;
import saccubus.net.NicoClient;
import saccubus.net.Path;
import saccubus.util.AudioPlay;
import saccubus.util.Cws2Fws;
import saccubus.util.Logger;
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
 *  ��EventDispatchThread�̂���GUI�`��͂��ׂ�invokeLater()�ɂčs������
 *  JLabel�̕\�����������Ȃ�
 */
public class ConvertWorker extends SwingWorker<String, String> {

	private static final String END_OF_ARGUMENT = "|--end-of-argument";
	class Tick extends TimerTask {
		private JLabel label;
		private String timerString = "";
		private int tick = 0;
		private int up = 1;
		public Tick(JLabel status, String s){
			label = status;
			timerString = s;
		}
		public Tick(JLabel status, String s, int initial_tick){
			this(status, s);
			tick = initial_tick;
			up = -1;
		}
		@Override
		public void run() {

			sendTimer(label, timerString + tick +"�b");
			tick += up;
		}
		private void sendTimer(final JLabel l, final String s){
			if(SwingUtilities.isEventDispatchThread()) {
				l.setText(s);
			}
			else
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						l.setText(s);
					}
				});
		}
	}

	private ConvertingSetting Setting;
	private String Vid;
	private String Tag;
	private String VideoID;
	private String VideoTitle;
	private String VideoBaseName = "";
	private String Time;
	private JLabel Status;
	private final JLabel MovieInfo;
	private JLabel vidLabel;
	private final ConvertStopFlag StopFlag;
	private static final String TMP_COMMENT = "_vhook.tmp";
	private static final String TMP_OWNERCOMMENT = "_vhookowner.tmp";
	private static final String TMP_OPTIONALTHREAD = "_vhookoptional.tmp";
//	private static final String VIDEO_URL_PARSER = "http://www.nicovideo.jp/watch/";
	public static final String OWNER_EXT = "[Owner].xml";	// ���e�҃R�����g�T�t�B�b�N�X
	public static final String OPTIONAL_EXT = "{Optional}.xml";	// �I�v�V���i���X���b�h�T�t�B�b�N�X
	public static final String NICOS_EXT = "{Nicos}.xml";	//�j�R�X�R�����g�T�t�B�b�N�X
	public static final String TMP_APPEND_EXT = "_all_comment.xml";
	public static final String TMP_APPEND_OPTIONAL_EXT = "_all_optional.xml";
	public static final String TMP_APPEND_NICOS_EXT = "_all_nicos.xml";
	private static final String TMP_COMBINED_XML = "_tmp_comment.xml";
	private static final String TMP_COMBINED_XML2 = "_tmp_optional.xml";
	private static final String TMP_COMBINED_XML3 = "_tmp_comment2.xml";
	private static final String TMP_COMBINED_XML4 = "_tmp_optiona2.xml";
	static final String TMP_LOG_FRONTEND = "frontend.txt";
	private static final String THUMB_INFO = "_thumb_info";
	private String OtherVideo;
	private final String WatchInfo;
	private InfoStack infoStack;
	private BrowserCookieKind BrowserKind = BrowserCookieKind.NONE;
	private final BrowserInfo browserInfo;
	private String UserSession = "";	//�u���E�U����擾�������[�U�[�Z�b�V����
	private final Stopwatch stopwatch;
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
	private final StringBuffer sbRet;
	private final saccubus.MainFrame parent;
	private List<String> nicoTagList = null;
	private String nicoCategory;
	private int numTag;
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
	//private String dateUserLast = "";
	//private final boolean watchvideo;
	private double frameRate = 0.0;
	private double fpsUp = 0.0;
	private double fpsMin = 0.0;
	private boolean checkFps;
	private String lastFrame = "";
	private AutoPlay autoPlay;
	private ArrayList<CommentReplace> CommentReplaceList = new ArrayList<CommentReplace>();
	private File imgDir;
	private Aspect outAspect;
	private String alternativeTag = "";
	private final ConvertManager manager;
	private Gate gate;
	private ErrorControl errorControl;
	private String lowVideoID;
	private String dmcVideoID;
	private String videoContentType;
	private int tid;
	private Logger log;
	private String thumbInfoData;

	public ConvertWorker(int worker_id,
			String url, String time, ConvertingSetting setting,
			JLabel[] jLabels, ConvertStopFlag flag,	MainFrame frame,
			AutoPlay autoplay, ConvertManager conv, ErrorControl errcon,
			StringBuffer sb, Logger logger) {
		Vid = url;
		url = url.trim();
		//watchvideo = !url.startsWith("http");
		int index = 0;
		index = url.indexOf('?');
		if(index >= 0){
			WatchInfo = url.substring(index);
			url = url.substring(0, index);
		}else{
			WatchInfo = "";
		}
		int index2 = url.lastIndexOf('/');
		Tag = url.substring(index2+1);
		VideoID = "[" + Tag + "]";
		lowVideoID = VideoID + "low_";
		dmcVideoID = VideoID + "dmc_";
		DefaultVideoIDFilter = new VideoIDFilter(Tag);
		if (time.equals("000000") || time.equals("0")){		// for auto.bat
			Time = "";
		} else {
			Time = time;
		}
		Setting = setting;
		Status = jLabels[0];
		StopFlag = flag;
		MovieInfo = jLabels[1];
		MovieInfo.setText(" ");
		stopwatch = new Stopwatch(jLabels[2]);
		vidLabel = jLabels[3];
		manager = conv;
		autoPlay = autoplay;
		parent = frame;
		sbRet = sb;
		errorControl = errcon;
		tid = worker_id;
		log = logger;
		browserInfo = new BrowserInfo(log);
	}
	private File VideoFile = null;
	private File CommentFile = null;
	private File OwnerCommentFile = null;
	private File OptionalThreadFile = null;
	private File nicosCommentFile = null;
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
	private String nicos_id = "";
	private String errorLog = "";
	private boolean isNicos = false;
	private boolean isOptionalTranslucent = true;
	private int videoLength = 0;
	private int ownerCommentNum = 0;
	private File fontDir;
	private File gothicFont = null;
	private File simsunFont = null;
	private File gulimFont = null;
	private File arialFont = null;
	private File georgiaFont = null;
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
	private File CombinedCommentFile;
	private File CombinedOptionalFile;
	private File appendCommentFile;
	private File appendOptionalFile;
	//private File lowVideoFile;
	//private File dmcVideoFile;
	private File resumeDmcFile;
	private boolean isConverting = false;
	private boolean isDebugNet = false;
	private boolean isLive = false;

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

//	private void sendtext(final String text){
//		Status.setText(text);
//	}

	private String mySendedText;
	private Path metaDataFile = null;
	private void sendtext(final String text){
		mySendedText = text;
		publish(text);
	}
	private String gettext(){
		return mySendedText;
	}
	protected void process(List<String> chunk){
		while(!chunk.isEmpty()){
			String text = chunk.remove(0);
			if(text.startsWith("@vid ")){
				text = text.substring(5);
				vidLabel.setText(text);
			}else{
				Status.setText(text);
			}
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
//	private ArrayList<CommentReplace> getCommentReplaceSet(){
//		return commentReplaceSet;
//	}
//	private void addCommentReplace(CommentReplace cmrpl){
//		commentReplaceSet.add(cmrpl);
//	}
	private boolean isAppendComment(){
		return Setting.isAppendComment();
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
					log.println("CA�p�t�H���g" + saveGulimFont.getPath() + "��" + gulimFont.getName() + "�ő�ւ��܂��B");
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
					log.println("CA�p�t�H���g" + georgiaFont.getPath() + "��" + gothicFont.getName() + "�ő�ւ��܂��B");
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
					log.println("CA�p�t�H���g" + devabagariFont.getPath() + "��" + arialFont.getName() + "�ő�ւ��܂��B");
					devabagariFont = arialFont;
				}
				tahomaFont = new File(fontDir, "tahoma.ttf");
				if (!tahomaFont.canRead()) {
					sendtext("�x���@CA�p�t�H���g��������܂���B" + tahomaFont.getPath());
					//retValue = "16";
					//return false;
					log.println("CA�p�t�H���g" + tahomaFont.getPath() + "��" + arialFont.getName() + "�ő�ւ��܂��B");
					tahomaFont = arialFont;
				}
				mingliuFont = new File(fontDir, "mingliu.ttc");
				if (!mingliuFont.canRead()) {
					sendtext("�x���@CA�p�t�H���g��������܂���B" + mingliuFont.getPath());
					//retValue = "17";
					//return false;
					log.println("CA�p�t�H���g" + mingliuFont.getPath() + "��" + simsunFont.getName() + "�ő�ւ��܂��B");
					mingliuFont = simsunFont;
				}
				newMinchoFont = new File(fontDir, "SIMSUN.TTC");	//NGULIM.TTF����������
				if (!newMinchoFont.canRead()) {
					sendtext("�x���@CA�p�t�H���g��������܂���B" + newMinchoFont.getPath());
					//retValue = "18";
					//return false;
					log.println("CA�p�t�H���g" + newMinchoFont.getPath() + "��" + simsunFont.getName() + "�ő�ւ��܂��B");
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
					log.println("CA�p�t�H���g" + estrangeloEdessaFont.getPath() + "��" + arialFont.getName() + "�ő�ւ��܂��B");
					estrangeloEdessaFont = arialFont;
				}
				arialUnicodeFont = new File(fontDir, "arialuni.ttf");
				if (!arialUnicodeFont.canRead()) {
					sendtext("�x���@CA�p�t�H���g��������܂���B" + arialUnicodeFont.getPath());
					//retValue = "20";
					//return false;
					log.println("CA�p�t�H���g" + arialUnicodeFont.getPath() + "��" + arialFont.getName() + "�ő�ւ��܂��B");
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
					log.println("CA�p�t�H���g" + gujaratiFont.getPath() + "��" + arialUnicodeFont.getName() + "�ő�ւ��܂��B");
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
					log.println("CA�p�t�H���g" + bengalFont.getPath() + "��" + arialUnicodeFont.getName() + "�ő�ւ��܂��B");
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
					log.println("CA�p�t�H���g" + tamilFont.getPath() + "��" + arialUnicodeFont.getName() + "�ő�ւ��܂��B");
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
					log.println("CA�p�t�H���g" + laooFont.getPath() + "��" + arialUnicodeFont.getName() + "�ő�ւ��܂��B");
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
					log.println("CA�p�t�H���g" + gurmukhiFont.getPath() + "��" + arialUnicodeFont.getName() + "�ő�ւ��܂��B");
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
					log.println("CA�p�t�H���g" + kannadaFont.getPath() + "��" + arialUnicodeFont.getName() + "�ő�ւ��܂��B");
					kannadaFont = arialUnicodeFont;
				}
				thaanaFont = new File(fontDir, "mvboli.ttf");
				if (!thaanaFont.canRead()) {
					sendtext("�x���@CA�p�t�H���g��������܂���B" + thaanaFont.getPath());
					//retValue = "27";
					//return false;
					log.println("CA�p�t�H���g" + thaanaFont.getPath() + "��" + arialUnicodeFont.getName() + "�ő�ւ��܂��B");
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
					log.println("CA�p�t�H���g" + malayalamFont.getPath() + "��" + arialUnicodeFont.getName() + "�ő�ւ��܂��B");
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
					log.println("CA�p�t�H���g" + teluguFont.getPath() + "��" + arialUnicodeFont.getName() + "�ő�ւ��܂��B");
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
		proxy = Setting.getProxy();
		proxy_port = Setting.getProxyPort();
		if (isSaveVideo() || isSaveComment() || isSaveOwnerComment()
			|| Setting.isSaveThumbInfo()) {
			// �u���E�U�Z�b�V�������L�̏ꍇ�͂����ŃZ�b�V������ǂݍ���
			UserSession = browserInfo.getUserSession(Setting);
			BrowserKind = browserInfo.getValidBrowser();
			if (BrowserKind == BrowserCookieKind.NONE){
				mailAddress = Setting.getMailAddress();
				password = Setting.getPassword();
				if (mailAddress == null || mailAddress.isEmpty()
					|| password == null || password.isEmpty()) {
					sendtext("���O�C���Z�b�V���������A���[���A�h���X���p�X���[�h���󔒂ł��B");
					result = "33";
					return false;
				}
			} else if(UserSession.isEmpty()){
				sendtext("�u���E�U" + BrowserKind.getName() + "�̃Z�b�V�����擾�Ɏ��s");
				result = "34";
				return false;
			}
			if (useProxy()){
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
		if(proxy!=null)
			isDebugNet = proxy.startsWith(NicoClient.DEBUG_PROXY);
		resultBuffer = Setting.getReturnBuffer();
		sendtext("�`�F�b�N�I��");
		return true;
	}

	public synchronized NicoClient getNicoClient() {

		if (isSaveVideo() || isSaveComment() || isSaveOwnerComment()
			|| Setting.isSaveThumbInfo()) {
			sendtext("���O�C����");
			NicoClient client = null;
			boolean is_html5 = Setting.isHtml5();
			if (BrowserKind != BrowserCookieKind.NONE){
				// �Z�b�V�������L�A���O�C���ς݂�NicoClient��client�ɕԂ�
				client = new NicoClient(BrowserKind, UserSession, proxy, proxy_port, stopwatch, log, is_html5);
			} else {
				client = new NicoClient(mailAddress, password, proxy, proxy_port, stopwatch, log, is_html5);
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
		File dmcVideoFile;
		File lowVideoFile;
		File folder = Setting.getVideoFixFileNameFolder();
		sendtext("����̕ۑ�");
		/*����̕ۑ�*/
		try {
			if (isSaveVideo()) {
				if (client == null){
					sendtext("���O�C�����ĂȂ��̂ɓ���̕ۑ��ɂȂ�܂���");
					result = "41";
					return false;
				}
				lowVideoFile = null;
				boolean renameMp4 = Setting.isChangeMp4Ext();
				if(client.isEco()){
					if(Setting.isDisableEco()){
						sendtext("�G�R�m�~�[���[�h�Ȃ̂Œ��~���܂�");
						result = "42";
						return false;
					}
				}
				if (isVideoFixFileName()) {
					if (folder.mkdir()) {
						log.println("Folder created: " + folder.getPath());
					}
					if (!folder.isDirectory()) {
						sendtext("����̕ۑ���t�H���_���쐬�ł��܂���B");
						result = "40";
						return false;
					}
					String name = getVideoBaseName() + ".flv";
					VideoFile = new File(folder, name);
					if(client.isEco()){
						lowVideoFile = new File(folder, name.replace(VideoID, lowVideoID));
						log.println("client.isEco:: low_VideoFile:"+lowVideoFile.getPath());
					}
					dmcVideoFile = new File(folder, name.replace(VideoID, dmcVideoID));
					resumeDmcFile = Path.getReplacedExtFile(dmcVideoFile, ".flv_dmc");	// suspended video
				} else {
					File file = Setting.getVideoFile();	//�u���O
					//%LOW%�ȊO�̒u��
					VideoFile = replaceFilenamePattern(file, false, false);	//�u����
					String name = VideoFile.getName();	//�u����
					File dir = VideoFile.getParentFile();
					if(client.isEco()){
						lowVideoFile = replaceFilenamePattern(file, true, false);
						// %LOW%�͒u���ς�
						if(!Path.contains(lowVideoFile, "low_")){
							log.println("MACRO doesn't contain %LOW%. "+lowVideoFile.getPath());
							// ID->IDlow_ , [ID]->[ID]low_
							if(name.contains(lowVideoID)){
								//�ʏ�͂Ȃ�
								lowVideoFile = VideoFile;
							}else if(name.contains(VideoID)){
								lowVideoFile = new File(dir,name.replace(VideoID, lowVideoID));
							}else if(name.contains(Tag)){
								lowVideoFile = new File(dir,name.replace(Tag, Tag+"low_"));
							}else
								lowVideoFile = new File(dir,"low_"+name);
						}
						log.println("client.isEco:: low_VideoFile:"+lowVideoFile.getPath());
						dmcVideoFile = lowVideoFile;
					}else{
						dmcVideoFile = replaceFilenamePattern(file, false, true);
					}
					resumeDmcFile = Path.getReplacedExtFile(dmcVideoFile, ".flv_dmc");
				}
				if(lowVideoFile!=null){
					if(client.isEco() && existVideoFile(VideoFile, ".flv", ".mp4")){
						sendtext("�G�R�m�~�[���[�h�Œʏ퓮��͊��ɑ��݂��܂�");
						log.println("�G�R�m�~�[���[�h�Œʏ퓮��͊��ɑ��݂��܂��B�_�E�����[�h���X�L�b�v���܂�");
						VideoFile = existVideo;
						return true;
					}
					if(client.isEco() && existVideoFile(dmcVideoFile, ".flv", ".mp4")){
						sendtext("�G�R�m�~�[���[�h��dmc����͊��ɑ��݂��܂�");
						log.println("�G�R�m�~�[���[�h��dmc����͊��ɑ��݂��܂��B�_�E�����[�h���X�L�b�v���܂�");
						dmcVideoFile = existVideo;
						VideoFile = dmcVideoFile;
						return true;
					}
					if(client.isEco() && existVideoFile(lowVideoFile,".flv",".mp4")){
						sendtext("�G�R�m�~�[���[�h�ŃG�R����͊��ɑ��݂��܂�");
						log.println("�G�R�m�~�[���[�h�œ���͊��ɑ��݂��܂��B�_�E�����[�h���X�L�b�v���܂�");
						lowVideoFile = existVideo;
						VideoFile = lowVideoFile;
						return true;
					}
				}
				sendtext("����̃_�E�����[�h�J�n��");
				log.println("serverIsDmc: "+client.serverIsDmc()
					+", preferSmile: "+Setting.isSmilePreferable()
					+", forceDMC:" + Setting.doesDmcforceDl()
					+", client.isEco:"+client.isEco());
				if(!client.serverIsDmc() || Setting.isSmilePreferable() && !Setting.doesDmcforceDl()){
					// �ʏ�T�[�o
					if(existVideoFile(VideoFile,".flv",".mp4")){
						sendtext("����͊��ɑ��݂��܂�");
						log.println("����͊��ɑ��݂��܂��B�_�E�����[�h���X�L�b�v���܂�");
						VideoFile = existVideo;
						return true;
					}
					if(lowVideoFile==null)
						lowVideoFile = VideoFile;
					VideoFile = client.getVideo(lowVideoFile, Status, StopFlag, renameMp4);
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
					if (optionalThreadID == null || optionalThreadID.isEmpty()) {
						optionalThreadID = client.getOptionalThreadID();
					}
					videoLength = client.getVideoLength();
					videoContentType = client.getVideoContentType();
					log.println("video ContentType: "+videoContentType);
					setVideoTitleIfNull(VideoFile.getName());
				}else{
					// dmc
					log.println("Dmc download start.");
					long dmc_size = 0;
					long dmc_high = 0;
					long resume_size = 0;
					long video_size = 0;
					long size_high = 0;
					String size_high_str = client.getSizeSmile();
					if(size_high_str!=null){
						try {
							size_high = Long.decode(size_high_str);
						}catch(NumberFormatException e){
							log.printStackTrace(e);
							size_high = 0;
						}
					}
					if(size_high>0)
						log.println("smile size: "+(size_high>>20)+"MiB");
					else
						log.println("bug? can't get smile size");
					if(existVideoFile(VideoFile, ".flv", ".mp4")){
						log.println("����͊��ɑ��݂��܂��B");
						sendtext("����͊��ɑ��݂��܂�");
						VideoFile = existVideo;
						video_size = VideoFile.length();
						log.println("video size: "+(video_size>>20)+"MiB");
					}
					if(existVideoFile(dmcVideoFile, ".flv", ".mp4")){
						log.println("dmc����͊��ɑ��݂��܂��B");
						sendtext("dmc����͊��ɑ��݂��܂�");
						dmcVideoFile = existVideo;
						dmc_size = dmcVideoFile.length();
						log.println("dmc size: "+(dmc_size>>20)+"MiB");
					} else {
						long min_size = Math.max(video_size, size_high);
						long[] limits = {min_size, 0, 0};	// limits[1] is return value
						if(Setting.doesDmcforceDl())
							limits[0] = 0;	//�r���Œ��~���Ȃ�
						if(Setting.canSeqResume()){
							if(resumeDmcFile.isFile() && resumeDmcFile.canRead()){
								if(dmcVideoFile.exists())
									dmcVideoFile.delete();
								if(resumeDmcFile.renameTo(dmcVideoFile)){
									log.println("���f����dmc�����resume���܂��B");
									sendtext("���f����dmc����resume���܂�");
									resume_size = dmcVideoFile.length();
									log.println("resumed size: "+(resume_size>>20)+"MiB");
								}
							}
							do {
								limits[2] = resume_size;
								File dmclowFile = lowVideoFile;
								if(dmclowFile==null)
									dmclowFile = dmcVideoFile;
								File video = client.getVideoDmc(
									dmclowFile, Status, StopFlag, renameMp4, limits,
									Setting.canRangeRequest(), true, resume_size);
								if (stopFlagReturn()) {
									result = "43";
									if(dmcVideoFile.canRead()){
										if(dmcVideoFile.renameTo(resumeDmcFile))
											log.println("dmcVideo renamed to "+resumeDmcFile);
									}
									return false;
								}
								dmc_high = limits[1];
								if(dmcVideoFile.canRead())
									dmc_size = dmcVideoFile.length();
								log.println("dmc size: "+(dmc_high>>20)+"MiB");
								if(video==null){
									//dmc_size = 0;
									String ecode = client.getExtraError();
									if(ecode.contains("97")){
										// skip or done
										log.println(ecode);
										sendtext(ecode);
										break;
									} else {
										log.println("dmc����T�[�o�����(S)�_�E�����[�h�Ɏ��s���܂����B");
										sendtext("dmc�����(S)�_�E�����[�h�Ɏ��s�B" + ecode);
										if(dmcVideoFile.canRead()){
											if(dmcVideoFile.renameTo(resumeDmcFile))
												log.println("dmcVideo renamed to "+resumeDmcFile);
										}
										if(ecode.contains("98")){
											result = "98";	// suspended, retry next
											return false;
										}
									}
								}else{
									// intended suspend
									videoLength = client.getDmcVideoLength();
									videoContentType = client.getVideoContentType();
									log.println("video ContentType: "+videoContentType);
									dmcVideoFile = video;
									resume_size = dmcVideoFile.length();
									log.println("resumed size: "+(resume_size>>20)+"MiB");
								}
								if(resume_size == dmc_size)
									break;
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									log.printStackTrace(e);
								}
								// watch�y�[�W�X�V�`�F�b�N
								if(!client.getVideoInfo(Tag, WatchInfo, Time, Setting.isSaveWatchPage())){
									log.println("dmc(S) watch�y�[�W�G���[");
									sendtext("dmc(S) watch�y�[�W�G���[�@"+client.getExtraError());
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										log.printStackTrace(e);
									}
									client.setExtraError("98 dmc(S)�G���[���g���C�B");
									result = "98";
									if(dmcVideoFile.canRead()){
										if(dmcVideoFile.renameTo(resumeDmcFile))
											log.println("dmcVideo renamed to "+resumeDmcFile);
									}
									for(int l=60; l>0; l--){
										try{
											Thread.sleep(1000);
											sendtext("dmc(S)�G���[���g���C�҂� "+l+"�b");
											//stopwatch.show();
											if(stopFlagReturn()){
												result = "43";
												break;
											}
										}catch(InterruptedException e){
											break;
										}
									}
									sendtext("98 dmc(S)�G���[���g���C�@");
									return false;
								}
							} while(resume_size < dmc_size);
							if(dmc_high == 0){
								log.println("dmc(S) getsize���s!(dmc_high == 0)");
								sendtext("dmc(S) getsize���s!");
							}else if(video_size == dmc_high){
								log.println("dmc(S) �_�E�����[�h��(video_size == dmc_high)");
								sendtext("dmc(S) �_�E�����[�h��");
								dmcVideoFile = VideoFile;
								dmc_size = video_size;
							}else if(min_size >= dmc_size){
								log.println("dmc(S) �_�E�����[�h���~(min_size >= dmc_size)");
								sendtext("dmc(S) �_�E�����[�h���~");
								dmcVideoFile = null;
								dmc_size = 0;
							}else if(dmc_size == 0){
								log.println("dmc(S) download���s!(dmc_size == 0)");
								sendtext("dmc(S) download���s!");
							}else if(resume_size != dmc_size){
								log.println("dmc(S) resume���s!(resume_size != dmc_size)");
								sendtext("dmc(S) resume���s!");
								log.println("dmc size: "+(dmc_size>>20)+"MiB, resumed size: "+(resume_size>>20)+"MiB");
								dmc_size = 0;
							}else{
							//	�R���e�i�ϊ��s�v
							//	if(Setting.isAutoFlvToMp4())
							//		dmcVideoFile = dmcFlvToMp4Convert(dmcVideoFile);
							}
						} else {
							// not dmc(S)
							File dmclowFile = lowVideoFile;
							if(dmclowFile==null)
								dmclowFile = dmcVideoFile;
							dmcVideoFile = client.getVideoDmc(
								dmclowFile, Status, StopFlag, renameMp4, limits,
								Setting.canRangeRequest(), false, 0);
							if (stopFlagReturn()) {
								result = "43";
								return false;
							}
							if(dmcVideoFile==null){
								//dmc_size = 0;
								String ecode = client.getExtraError();
								if(ecode.contains("97")){
									dmc_size = limits[1];
									if(dmc_size !=0 && dmc_size==video_size){
										dmcVideoFile = VideoFile;
									}
									log.println(ecode);
									sendtext(ecode);
									log.println("dmc size: "+(dmc_size>>20)+"MiB");
								}else{
									log.println("dmc����T�[�o����̃_�E�����[�h�Ɏ��s���܂����B");
									sendtext("dmc����̃_�E�����[�h�Ɏ��s" + ecode);
								}
							}
							if(dmcVideoFile!=null && existVideoFile(dmcVideoFile, ".flv", ".mp4")){
								log.println("dmc download "+dmcVideoFile.length()+"bytes");
								videoLength = client.getDmcVideoLength();
								videoContentType = client.getVideoContentType();
								log.println("video ContentType: "+videoContentType);
								dmc_size = dmcVideoFile.length();
								log.println("dmc size: "+(dmc_size>>20)+"MiB");
							//	�R���e�i�ϊ��s�v
							//	if(Setting.isAutoFlvToMp4())
							//		dmcVideoFile = dmcFlvToMp4Convert(dmcVideoFile);
							}
						}
					}
					// video_size , dmc_size should be real size of a file which exists now or has been loaded
					if ( (size_high > video_size && size_high > dmc_size && !Setting.isInhibitSmaller())
						||(size_high != video_size && size_high != dmc_size && Setting.isSmilePreferable())){
						// smile������_�E�����[�h
						log.println("Smile download start.");
						if(lowVideoFile==null)
							lowVideoFile = VideoFile;
						VideoFile = client.getVideo(lowVideoFile, Status, StopFlag, renameMp4);
						if (stopFlagReturn()) {
							result = "43";
							return false;
						}
						if (VideoFile == null) {
							log.println("smile����T�[�o����̃_�E�����[�h�Ɏ��s���܂����B");
							sendtext("smile����̃_�E�����[�h�Ɏ��s�@" + client.getExtraError());
							video_size = 0;
						}else{
							videoLength = client.getVideoLength();
							videoContentType = client.getVideoContentType();
							log.println("video ContentType: "+videoContentType);
							video_size = VideoFile.length();
						}
					}
					if(dmc_size==0 && video_size==0){
						sendtext("����̃_�E�����[�h�Ɏ��s�@" + client.getExtraError());
						result = "44";
						return false;
					}
					setVideoTitleIfNull(VideoFile.getName());
					if(dmc_size!=0)
						log.println("dmc size: "+(dmc_size>>20)+"MiB");
					if(video_size!=0)
						log.println("video size: "+(video_size>>20)+"MiB");
					if(dmc_size > video_size){
						log.println("�ϊ��ɂ�dmc������g���܂�");
						sendtext("�ϊ��ɂ�dmc������g���܂�");
						VideoFile = dmcVideoFile;
					}
					if (optionalThreadID == null || optionalThreadID.isEmpty()) {
						optionalThreadID = client.getOptionalThreadID();
					}
					resultBuffer.append("video: "+VideoFile.getName()+"\n");
					if(!Setting.isOnlyMp4AutoPlay()||Path.hasExt(VideoFile, ".mp4")){	//mp4�g���q�̓���̂ݏ�������
						autoPlay.offer(VideoFile,true);
						if(autoPlay.isPlayDownload())
							autoPlay.playAuto();
					}
				}
			} else {
				if (isSaveConverted()) {
					if (isVideoFixFileName()) {
						String videoFilename;
						if((videoFilename = detectTitleFromVideo(folder)) == null){
							if (OtherVideo == null){
								sendtext("����t�@�C�����t�H���_�ɑ��݂��܂���B");
								result = "45";
							} else {
								sendtext("����t�@�C����.flv��.mp4�ł���܂���F" + OtherVideo);
								result = "46";
							}
							return false;
						}
						VideoFile = new File(folder, videoFilename);
						lowVideoFile = new File(folder, videoFilename.replace(VideoID, lowVideoID));
						dmcVideoFile = new File(folder, videoFilename.replace(VideoID, dmcVideoID));
					} else {
						VideoFile = Setting.getVideoFile();
						VideoFile = replaceFilenamePattern(VideoFile,false,false);
						lowVideoFile = replaceFilenamePattern(VideoFile,true,false);
						dmcVideoFile = replaceFilenamePattern(VideoFile,false,true);
					}
					setVideoTitleIfNull(VideoFile.getName());
					if (!existVideoFile(VideoFile, ".flv", ".mp4")
					 && !existVideoFile(lowVideoFile, ".flv", ".mp4")
					 && !existVideoFile(dmcVideoFile, ".flv", ".mp4")) {
						sendtext("����t�@�C�������݂��܂���B");
						result = "47";
						return false;
					}
					VideoFile = existVideo;
					setVidTitile(tid, Tag, VideoTitle);
				}
			}
			sendtext("����̕ۑ����I��");
		}catch(NullPointerException e){
			log.printStackTrace(e);
		}
		return true;
	}

	private File existVideo;
	private File log_vhext = null;
	private Path video_vhext = null;
	private String vhspeedrate;
	private boolean existVideoFile(File file, String ext1, String ext2) {
		existVideo = file;
		if(existVideo.isFile() && existVideo.canRead())
			return true;
		existVideo = Path.getReplacedExtFile(file, ext1);
		if(existVideo.isFile() && existVideo.canRead())
			return true;
		existVideo = Path.getReplacedExtFile(file, ext2);
		if(existVideo.isFile() && existVideo.canRead())
			return true;
		return false;
	}
	private boolean saveComment(NicoClient client) {
		sendtext("�R�����g�̕ۑ�");
		File folder = Setting.getCommentFixFileNameFolder();
		String commentTitle = "";
		String prefix = "";
		String back_comment = Setting.getBackComment();
		ArrayList<File> filelist = new ArrayList<>();
		boolean backup = false;
		if (isSaveComment()) {
			if (isCommentFixFileName()) {
				if (folder.mkdir()) {
					log.println("Folder created: " + folder.getPath());
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
			// �t�@�C�����ݒ�
			appendCommentFile = mkTemp("_"+tid+TMP_APPEND_EXT);
			// �O����
			if(CommentFile.exists()){
				backup = Path.fileCopy(CommentFile,appendCommentFile);
			}
			File target = client.getComment(CommentFile, Status, back_comment, Time, StopFlag,
					Setting.getCommentIndex(), isAppendComment());
			if (stopFlagReturn()) {
				result = "52";
				return false;
			}
			if (target == null) {
				sendtext("�R�����g�̃_�E�����[�h�Ɏ��s " + client.getExtraError());
				if(backup)
					Path.move(appendCommentFile, CommentFile);
				result = "53";
				return false;
			}
			File commentJson = Path.getReplacedExtFile(CommentFile, "_commentJSON.txt");
			commentJson = client.getCommentJson(commentJson, Status, back_comment, Time, StopFlag);
			if(commentJson!=null){
				sendtext("�R�����gJSON�̃_�E�����[�h�ɐ��� " + commentJson.getPath());
			}
			// �t�@�C�����_�u���𐮗�
			backup = Path.fileCopy(CommentFile,appendCommentFile);
			filelist.add(CommentFile);
			sendtext("�R�����g�t�@�C��������");
			if (!CombineXML.combineXML(filelist, CommentFile)){
				sendtext("�R�����g�t�@�C���������o���܂���ł���");
				if(backup)
					Path.move(appendCommentFile, CommentFile);	// ���s������o�b�N�A�b�v��߂�
				result = "5A";
				return false;
			}
			//�R�����g�t�@�C���̍ŏ���date="integer"��T���� dateUserFirst �ɃZ�b�g
			dateUserFirst = getDateUserFirst(CommentFile);
			sendtext("�R�����g�̃_�E�����[�h�I��");
			optionalThreadID = client.getOptionalThreadID();
			sendtext("�I�v�V���i���X���b�h�̕ۑ�");
			if (optionalThreadID != null && !optionalThreadID.isEmpty() && CommentFile!=null ){
				OptionalThreadFile = Path.getReplacedExtFile(CommentFile, OPTIONAL_EXT);
				backup = false;
				appendOptionalFile = mkTemp(TMP_APPEND_OPTIONAL_EXT);
				// �O����
				if(OptionalThreadFile.exists()){
					backup = Path.fileCopy(OptionalThreadFile, appendOptionalFile);
				}
				sendtext("�I�v�V���i���X���b�h�̃_�E�����[�h�J�n��");
				target = client.getOptionalThread(
					OptionalThreadFile, Status, optionalThreadID, back_comment, Time, StopFlag,
					Setting.getCommentIndex(),isAppendComment());
				if (stopFlagReturn()) {
					result = "54";
					return false;
				}
				if (target == null) {
					sendtext("�I�v�V���i���X���b�h�̃_�E�����[�h�Ɏ��s " + client.getExtraError());
					if(backup)
						Path.move(appendOptionalFile, OptionalThreadFile);
					result = "55";
					return false;
				}
				backup = Path.fileCopy(OptionalThreadFile, appendOptionalFile);
				filelist.clear();
				filelist.add(OptionalThreadFile);
				sendtext("�I�v�V���i���X���b�h������");
				if (!CombineXML.combineXML(filelist, OptionalThreadFile)){
					sendtext("�I�v�V���i���X���b�h�������o���܂���ł���");
					if(backup)
						Path.move(appendOptionalFile, OptionalThreadFile);
					result = "5B";
					return false;
				}
				if (dateUserFirst.isEmpty()) {
					//�t�@�C���̍ŏ���date="integer"��T���� dateUserFirst �ɃZ�b�g
					dateUserFirst = getDateUserFirst(OptionalThreadFile);
				}
				sendtext("�I�v�V���i���X���b�h�̕ۑ��I��");
			}
			//�j�R�X�R�����g
			nicos_id = client.getNicosID();
			sendtext("�j�R�X�R�����g�̕ۑ�");
			if(nicos_id!=null && !nicos_id.isEmpty() && CommentFile!=null){
				isNicos = true;
				nicosCommentFile = Path.getReplacedExtFile(CommentFile, NICOS_EXT);
				// �O����
				backup = false;
				File appendNicosFile = mkTemp(TMP_APPEND_NICOS_EXT);
				if(nicosCommentFile.exists()){
					backup = Path.fileCopy(nicosCommentFile, appendNicosFile);
				}
				sendtext("�j�R�X�R�����g�̕ۑ��J�n��");
				target = client.getNicosComment(
					nicosCommentFile, Status, nicos_id, back_comment, Time,
						StopFlag, Setting.getCommentIndex(), isAppendComment());
				if (stopFlagReturn()) {
					result = "54";
					return false;
				}
				if (target == null) {
					sendtext("�j�R�X�R�����g�̃_�E�����[�h�Ɏ��s " + client.getExtraError());
					if(backup)
						Path.move(appendNicosFile, nicosCommentFile);
					result = "55";
					return false;
				}
				// �_�u������
				backup = Path.fileCopy(nicosCommentFile, appendNicosFile);
				filelist.clear();
				filelist.add(nicosCommentFile);
				sendtext("�j�R�X�R�����g������");
				if (!CombineXML.combineXML(filelist, nicosCommentFile)){
					sendtext("�j�R�X�R�����g�������o���܂���ł���");
					if(backup)
						Path.move(appendNicosFile, nicosCommentFile);
					result = "5B";
					return false;
				}
				sendtext("�j�R�X�R�����g�̕ۑ��I��");
			}
			resultBuffer.append("comment: "+CommentFile.getName()+"\n");
		}
		sendtext("�R�����g�̕ۑ��I��");
		return true;
	}

//	private File getOptionalThreadFile(File file) {
//		if (file == null || file.getPath() == null) {
//			return mkTemp(OPTIONAL_EXT);
//		}
//		return Path.getReplacedExtFile(file, OPTIONAL_EXT);
//	}
	private String getDateUserFirst(File comfile){
		//�R�����g�t�@�C���̍ŏ���date="integer"��T���� dateUserFirst �ɃZ�b�g
		String text = Path.readAllText(comfile, "UTF-8");
		Pattern p = Pattern.compile("<chat [^>]+>");
		Matcher m = p.matcher(text);
		String chats = "";
		String ret = "";
		while(m.find()){
			chats = m.group() ;
			ret = getDateFromChat(chats);
			if(!ret.isEmpty())
				return ret;
		}
		return ret;
	}
	private static String getLastChat(File comfile){
		//�R�����g�t�@�C���̍Ō��<chat thread="..." > �̕������Ԃ�
		String text = Path.readAllText(comfile, "UTF-8");
		Pattern p = Pattern.compile("<chat [^>]+>");
		Matcher m = p.matcher(text);
		String chats = "";
		while(m.find()){
			chats = m.group();
		}
		return chats;
	}
	private static String getRexpFromChats(String chats, String rexp, int i){
		Pattern p = Pattern.compile(rexp);
		Matcher m = p.matcher(chats);
		if(m.find()){
			return m.group(i);
		}
		return "";
	}
	private String getDateFromChat(String chat){
		return getRexpFromChats(chat,"date=\"([0-9]+)\"",1);
	}
	public static String getNoUserLastChat(File file) {
		return getRexpFromChats(getLastChat(file),"no=\"([0-9]+)\"", 1);
	}

	private boolean saveOwnerComment(NicoClient client){
		sendtext("���e�҃R�����g�̕ۑ�");
		File folder = Setting.getCommentFixFileNameFolder();
		if (isSaveOwnerComment()) {
			if (isCommentFixFileName()) {
				if (folder.mkdir()) {
					log.println("Folder created: " + folder.getPath());
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
				log.println("���e�҃R�����g�̃_�E�����[�h�Ɏ��s");
				//result = "63";
				return true;
			}
			if (optionalThreadID == null || optionalThreadID.isEmpty()) {
				optionalThreadID = client.getOptionalThreadID();
			}
			if (nicos_id == null || nicos_id.isEmpty())
				nicos_id = client.getNicosID();
		}
		sendtext("���e�҃R�����g�̕ۑ��I��");
		return true;
	}

	private boolean saveThumbInfo0(NicoClient client,String vtag) {
		sendtext("������̕ۑ�");
		/*�y�[�W�̕ۑ�*/
		String ext = Setting.isSaveThumbInfoAsText()? ".txt":".xml";
		File folder = Setting.getVideoFixFileNameFolder();
		if (isVideoFixFileName()) {
			if (folder.mkdir()) {
				log.println("Folder created: " + folder.getPath());
			}
			if (!folder.isDirectory()) {
				sendtext("������̕ۑ���t�H���_���쐬�ł��܂���B");
				result = "A0";
				return false;
			}
			thumbInfoFile = new File(folder, getVideoBaseName() + THUMB_INFO + ext);
		} else {
			thumbInfoFile = getThumbInfoFileFrom(Setting.getVideoFile(), ext);
			thumbInfoFile = replaceFilenamePattern(thumbInfoFile, false, false);
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
		thumbInfo = client.getThumbInfoFile(vtag);
		if (stopFlagReturn()) {
			result = "A3";
			return false;
		}
		if (thumbInfo == null) {
			sendtext("������̎擾�Ɏ��s" + client.getExtraError());
			result = "A4";
			return false;
		}
		log.println("reading:" + thumbInfo);
		boolean isOK = true;
		if(!saveThumbUser(thumbInfo, client)){
			sendtext("���e�ҏ��̎擾�Ɏ��s");
			log.println("���e�ҏ��̎擾�Ɏ��s");
			isOK = false;
		}
		if(!saveThumbnailJpg(thumbInfo, client)){
			sendtext("�T���l�C���摜�̎擾�Ɏ��s");
			log.println("�T���l�C���摜�̎擾�Ɏ��s");
			isOK = false;
		}
		if(Path.fileCopy(thumbInfo, thumbInfoFile)){
			if(thumbInfo.delete()){
				log.println("Deleted:" + thumbInfo);
			}
		}
		else
			isOK = false;
		if(isOK)
			sendtext("������̕ۑ��I��");
		return isOK;
	}

	private boolean saveThumbInfo(NicoClient client, String vtag) {
		if(!Setting.isSaveThumbInfo())
			return true;
		if(saveThumbInfo0(client, vtag))
			return true;
		// �R�~���j�e�B�����thumbinfo�����Ȃ��̂�smID���g��
		if(alternativeTag.isEmpty())
			alternativeTag = client.getAlternativeTag();
		if(alternativeTag.isEmpty() || alternativeTag.equals(Tag))
			return false;
		return saveThumbInfo0(client, alternativeTag);
	}

	private boolean saveThumbUser(Path infoFile, NicoClient client) {
		if(Setting.isSaveThumbUser()){
			sendtext("���e�ҏ��̕ۑ�");
			Path userThumbFile = null;
			boolean isUser = true;
			String ownerName = null;
			String infoXml = Path.readAllText(infoFile.getPath(), "UTF-8");
			String userID = client.getXmlElement(infoXml, "user_id");
			String user_nickname = client.getXmlElement(infoXml, "user_nickname");
			if(userID==null || userID.isEmpty()){
				isUser = false;
				userID = client.getXmlElement(infoXml, "ch_id");
				ownerName = client.getXmlElement(infoXml, "ch_name");
				if(userID!=null && !userID.isEmpty())
					userID = "ch"+userID;
				else
					isUser = true;
			}
			if(userID==null || userID.isEmpty() || userID.equals("none")){
				sendtext("���e�҂̏�񂪂���܂���");
				result = "A5";
				return false;
			}
			log.println("���e��:"+userID);
			File userFolder = new File(Setting.getUserFolder());
			if (userFolder.mkdirs()){
				log.println("Folder created: " + userFolder.getPath());
			}
			if(!userFolder.isDirectory()){
				sendtext("���[�U�[�t�H���_���쐬�ł��܂���");
				result = "A6";
				return false;
			}
			userThumbFile = new Path(userFolder, userID + ".htm");
			String html = null;
			if(isUser){
				if(!userThumbFile.canRead()){
					userThumbFile = client.getThumbUserFile(userID, userFolder);
				}
				if(userThumbFile != null && userThumbFile.canRead()){
					html = Path.readAllText(userThumbFile.getPath(), "UTF-8");
					ownerName = client.getXmlElement(html, "title");
				}
				if(ownerName == null || ownerName.contains("����J�v���t�B�[��")){
					ownerName = null;
					userThumbFile = client.getUserInfoFile(userID, userFolder);
					if(userThumbFile != null && userThumbFile.canRead()){
						html = Path.readAllText(userThumbFile.getPath(), "UTF-8");
						ownerName = client.getXmlElement(html, "title");
					}
					if(ownerName==null){
						sendtext("���e�҂̏��̓���Ɏ��s");
						result = "A7";
						if(ownerName==null || ownerName.isEmpty())
							ownerName = user_nickname;
						if(ownerName==null || ownerName.isEmpty())
							ownerName = "���e�҂̏��̓���Ɏ��s";
					//	return false;
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
				if(user_nickname==null || user_nickname.isEmpty())
					infoXml = infoXml.replace("</user_id>",
						"</user_id>\n<user>" + ownerName + "</user>");
			}
			try {
				PrintWriter pw = new PrintWriter(infoFile, "UTF-8");
				pw.write(infoXml);
				pw.flush();
				pw.close();
			} catch (IOException e) {
				log.printStackTrace(e);
				return false;
			}
			sendtext("���e�ҏ��̕ۑ��I��");
		}
		return true;
	}

	private boolean setThumbnailJpg() {
		if (isVideoFixFileName()) {
			File folder = Setting.getVideoFixFileNameFolder();
			if (folder.mkdir()) {
				log.println("Folder created: " + folder.getPath());
			}
			if (!folder.isDirectory()) {
				sendtext("�T���l�C���摜�̕ۑ���t�H���_���쐬�ł��܂���B");
				result = "A9";
				return false;
			}
			thumbnailJpg = new File(folder, getVideoBaseName() + ".jpg");
		} else {
			File file = Setting.getVideoFile();
			if(file!=null)
				file = replaceFilenamePattern(file, false, false);
			if (file == null || file.getPath() == null) {
				thumbnailJpg = mkTemp(Tag + "_thumnail.jpg");
			}else{
				thumbnailJpg = Path.getReplacedExtFile(file, ".jpg");
			}
		}
		return true;
	}

	private boolean saveThumbnailJpg(Path infoFile, NicoClient client) {
		if(Setting.isSaveThumbnailJpg()){
			sendtext("�T���l�C���摜�̕ۑ�");
			thumbnailJpg = null;
			String infoXml = Path.readAllText(infoFile.getPath(), "UTF-8");
			String url = client.getXmlElement(infoXml, "thumbnail_url");
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
			sendtext("�T���l�C���摜�̕ۑ��I��");
		}
		return true;
	}

	private File getThumbInfoFileFrom(File file, String ext) {
		if (file == null || file.getPath() == null) {
			return mkTemp(THUMB_INFO + ext);
		}
		return Path.getReplacedExtFile(file, THUMB_INFO + ext);
	}

	private boolean makeNGPattern(boolean enableML) {
		sendtext("NG�p�^�[���쐬��");
		try{
			String all_regex = "/((docomo|iPhone|softbank|device:3DS) (white )?)?.* 18[46]|18[46]( (iPhone|device:3DS))? .*/";
			String def_regex = "/((docomo|iPhone|softbank|device:3DS) (white )?)?18[46]|18[46]( (iPhone|device:3DS))?/";
			String ngWord = Setting.getNG_Word().replaceFirst("^all", all_regex).replace(" all", all_regex);
			ngWord = ngWord.replaceFirst("^default", def_regex).replace(" default", def_regex);
			ngWordPat = NicoXMLReader.makePattern(ngWord, log, enableML);
			ngIDPat = NicoXMLReader.makePattern(Setting.getNG_ID(), log, enableML);
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
		ArrayList<File> filelist = new ArrayList<File>();
		if (isConvertWithComment()) {
			if (Setting.isAddTimeStamp() && isCommentFixFileName() && !isAppendComment()) {
				// �����̃R�����g�t�@�C���i�ߋ����O�j�����邩��
				ArrayList<String> pathlist = detectFilelistFromComment(folder);
				if (pathlist == null || pathlist.isEmpty()){
					sendtext(Tag + ": �R�����g�t�@�C���E�ߋ����O�����݂��܂���B");
					result = "71";
					return false;
				}
				// VideoTitle �͌��������B
				if (pathlist.size() > 0) {			// 0 1.22r3e8, for NP4 comment ver 2009
					for (String path: pathlist){
						filelist.add(new File(folder, path));
					}
					CommentFile = mkTemp(TMP_COMBINED_XML);
					sendtext("�R�����g�t�@�C��������");
					log.println("�R�����g�t�@�C��������");
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
			//alternativeVideoID�擾
			if(alternativeTag.isEmpty()){
				alternativeTag = getViewCounterVideoTag(CommentFile);
			}
			//combine �t�@�C�����_�u�����폜
			filelist.clear();
			filelist.add(CommentFile);
			CombinedCommentFile = mkTemp(TMP_COMBINED_XML3);
			sendtext("�R�����g�t�@�C���}�[�W��");
			log.println("�R�����g�t�@�C���}�[�W��");
			if (!CombineXML.combineXML(filelist, CombinedCommentFile)){
				sendtext("�R�����g�t�@�C�����}�[�W�o���܂���ł���");
				result = "72";
				return false;
			}
			CommentMiddleFile = mkTemp(TMP_COMMENT);
			if(!convertToCommentMiddle(CombinedCommentFile, CommentMiddleFile, isNicos)){
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

	private String getViewCounterVideoTag(File comfile) {
		//�R�����g�t�@�C���̍ŏ���<view_counter id="..." > �̕������Ԃ�
		String text = Path.readAllText(comfile, "UTF-8");
		Pattern p = Pattern.compile("<view_counter [^>]+>");
		Matcher m = p.matcher(text);
		String view_counter = "";
		String ret = "";
		while(m.find()){
			view_counter  = m.group() ;
			ret = getRexpFromChats(view_counter, "id=\"([a-zA-Z]+[0-9]+)\"", 1);
			if(!ret.isEmpty())
				return ret;
		}
		return ret;
	}

	private boolean convertOprionalThread(){
		sendtext("�I�v�V���i���X���b�h�̒��ԃt�@�C���ւ̕ϊ���");
		log.println(gettext());
		File folder = Setting.getCommentFixFileNameFolder();
		ArrayList<File> filelist = new ArrayList<File>();
		String optext;
		if (isConvertWithComment()) {
			if (isCommentFixFileName()) {
				if (Setting.isAddTimeStamp() && !isAppendComment()) {
					// �t�H���_�w�莞�A�����̃I�v�V���i���X���b�h�i�ߋ����O�j�����邩��
					optext = OPTIONAL_EXT;
					ArrayList<String> pathlist = detectFilelistFromOptionalThread(folder, optext);
					if (pathlist == null || pathlist.isEmpty()){
						sendtext(Tag + ": �I�v�V���i���X���b�h�E�ߋ����O�����݂��܂���B�j�R�X�R�����g�Ń��g���C");
						log.println(gettext());
						// �j�R�X�R�����g�Ń��g���C
						optext = NICOS_EXT;
						isOptionalTranslucent = false;
						pathlist = detectFilelistFromOptionalThread(folder, optext);
						if(pathlist == null || pathlist.isEmpty()){
							sendtext(Tag + ": �j�R�X�R�����g�E�ߋ����O�����݂��܂���B");
							log.println(gettext());
							log.println("No optional thread.");
							OptionalThreadFile = null;
							return true;
						}
					}
					// VideoTitle �͌��������B
					for (String path: pathlist){
						filelist.add(new File(folder, path));
					}
					OptionalThreadFile = mkTemp(TMP_COMBINED_XML2);
					sendtext("�I�v�V���i���X���b�h������");
					log.println(gettext());
					if (!CombineXML.combineXML(filelist, OptionalThreadFile)){
						sendtext("�I�v�V���i���X���b�h�������o���܂���ł����i�o�O�H�j");
						result = "77";
						//return false;
						OptionalThreadFile = null;
						return true;
					}
					if (dateUserFirst.isEmpty()) {
						//�R�����g�t�@�C���̍ŏ���date="integer"��T���� dateUserFirst �ɃZ�b�g
						dateUserFirst = getDateUserFirst(OptionalThreadFile);
					}
					listOfCommentFile.addAll(filelist);
				} else {
					// �t�H���_�w�莞�A�I�v�V���i���X���b�h�͂P��
					if(isSaveComment()){
						if(OptionalThreadFile==null){
							OptionalThreadFile = nicosCommentFile;
							if(OptionalThreadFile==null){
								return true;
							}
							isOptionalTranslucent = false;
						}
					}else{
						// �t�H���_�w�莞�A�I�v�V���i���X���b�h������
						optext = OPTIONAL_EXT;
						String filename = detectTitleFromOptionalThread(folder, optext);
						if (filename == null || filename.isEmpty()){
							sendtext(Tag + ": �I�v�V���i���X���b�h���t�H���_�ɑ��݂��܂���B�j�R�X�R�����g�Ń��g���C");
							log.println(gettext());
							// �j�R�X�R�����g�Ń��g���C
							optext = NICOS_EXT;
							filename = detectTitleFromOptionalThread(folder, optext);
							if(filename == null || filename.isEmpty()){
								sendtext(Tag + ": �j�R�X�R�����g���t�H���_�ɑ��݂��܂���B");
								log.println(gettext());
								log.println("No optional thread.");
								OptionalThreadFile = null;
								return true;
							}
							isNicos=true;
							isOptionalTranslucent = false;
						}
						OptionalThreadFile = new File(folder, filename);
					}
					if (dateUserFirst.isEmpty()) {
						//�R�����g�t�@�C���̍ŏ���date="integer"��T���� dateUserFirst �ɃZ�b�g
						dateUserFirst = getDateUserFirst(OptionalThreadFile);
					}
				}
			} else {
				if(isSaveComment()){
					// �t�@�C���w��̎��A�I�v�V���i���X���b�h�͂P��
					if(OptionalThreadFile==null){
						OptionalThreadFile = nicosCommentFile;
						if(OptionalThreadFile==null){
							return true;
						}
						isOptionalTranslucent = false;
					}
				}else{
					// �t�@�C���w��̎�����
					optext = OPTIONAL_EXT;
					OptionalThreadFile = Path.getReplacedExtFile(CommentFile, optext);
					if (!OptionalThreadFile.exists()){
						sendtext("�I�v�V���i���X���b�h�����݂��܂���B�j�R�X�R�����g�Ń��g���C");
						log.println(gettext());
						// �j�R�X�R�����g�Ń��g���C
						optext = NICOS_EXT;
						OptionalThreadFile = Path.getReplacedExtFile(CommentFile, optext);
						if(!OptionalThreadFile.exists()){
							sendtext("�j�R�X�R�����g�����݂��܂���B");
							log.println(gettext());
							log.println("No optional thread.");
							OptionalThreadFile = null;
							return true;
						}
						isNicos=true;
						isOptionalTranslucent = false;
					}
				}
				if (dateUserFirst.isEmpty()) {
					//�R�����g�t�@�C���̍ŏ���date="integer"��T���� dateUserFirst �ɃZ�b�g
					dateUserFirst = getDateUserFirst(OptionalThreadFile);
				}
			}
			//combine �t�@�C�����_�u�����폜
			filelist.clear();
			filelist.add(OptionalThreadFile);
			CombinedOptionalFile = mkTemp(TMP_COMBINED_XML4);
			sendtext("�I�v�V���i���X���b�h�}�[�W��");
			log.println(gettext());
			if (!CombineXML.combineXML(filelist, CombinedOptionalFile)){
				sendtext("�I�v�V���i���X���b�h���}�[�W�o���܂���ł���");
				result = "77";
			//	return false;
				OptionalMiddleFile = null;
				return true;
			}
			OptionalMiddleFile = mkTemp(TMP_OPTIONALTHREAD);
			if(!convertToCommentMiddle(CombinedOptionalFile, OptionalMiddleFile, isNicos)){
				sendtext("�I�v�V���i���X���b�h�ϊ��Ɏ��s");
				log.println(gettext());
				OptionalMiddleFile = null;
				result = "78";
				//	return false;
				OptionalMiddleFile = null;
				return true;
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
						log.println("���e�҃R�����g�t�@�C�����t�H���_�ɑ��݂��܂���B");
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
						log.println("���e�҃R�����g�t�@�C�������݂��܂���B");
						OwnerCommentFile = null;
						return true;
					}
				}
			}
			//alternativeVideoID�擾
			if(alternativeTag.isEmpty()){
				alternativeTag = getViewCounterVideoTag(OwnerCommentFile);
			}
			OwnerMiddleFile = mkTemp(TMP_OWNERCOMMENT);
			//������ commentReplace�������
			log.println("���e�҃R�����g�ϊ�");
			if (!convertToCommentMiddle(OwnerCommentFile, OwnerMiddleFile, isNicos)){
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
					log.printStackTrace(e);
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
			log.println("Deleted: " + CommentFile.getPath());
		}
		if (OptionalThreadFile != null && OptionalThreadFile.delete()){
			log.println("Deleted: " + OptionalThreadFile.getPath());
		}
		deleteList(listOfCommentFile);
		if (OwnerCommentFile != null && OwnerCommentFile.delete()) {
			log.println("Deleted: " + OwnerCommentFile.getPath());
		}
	}

	private boolean convertToCommentMiddle(File commentfile, File middlefile, boolean is_nicos) {
		String duration = "";
		if(Setting.changedLiveOperationDuration())
			duration = Setting.getLiveOperationDuration();
		// �j�R�X�R�����g�� premium "2" or "3"�݂����Ȃ̂Ńj�R�X�R�����g�̎��͉^�c�R�����g�ϊ����Ȃ��悤�ɂ���
		boolean live_op = Setting.isLiveOperationConversion() && !is_nicos;
		if(is_nicos)
			isOptionalTranslucent = false;
		if(!ConvertToVideoHook.convert(
				commentfile, middlefile, CommentReplaceList,
				ngIDPat, ngWordPat, ngCmd, Setting.getScoreLimit(),
				live_op, Setting.isPremiumColorCheck(), duration, log, isDebugNet)){
			return false;
		}
		//�R�����g����0�̎��폜����
		try{
			FileInputStream fis = new FileInputStream(middlefile);
			int comment_num = Util.readInt(fis);
			fis.close();
			if(comment_num == 0){
				if(middlefile.delete()){
					log.println("Deleted 0 comment-file: " + middlefile.getPath());
				}
			}
		} catch (IOException e) {
			log.printStackTrace(e);
			return false;
		}
		//log.println("comment replace list size = "+CommentReplaceList.size());
		return true;
	}

	private boolean convertVideo() throws IOException {
		sendtext("����̕ϊ����J�n");
		video_vhext = Path.mkTemp(Tag+"[log]vhext.txt");
		stopwatch.start();
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
				log.println("Created folder: " + folder.getPath());
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
					log.println("Created folder: " + folder.getPath());
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
			if(Path.contains(VideoFile,"low_") && !conv_name.contains("low_")){
				if(conv_name.contains(VideoID))
					conv_name = conv_name.replace(VideoID, lowVideoID);
				else
					conv_name = "low_" + conv_name;
			}
			ConvertedVideoFile = new File(folder, conv_name + ExtOption);
		} else {
			File file = Setting.getConvertedVideoFile();
			if (!Path.hasExt(file, ExtOption)) {
				file = Path.getReplacedExtFile(file,ExtOption);
			}
			boolean videoIsLow = Path.contains(VideoFile,"low_");
			//boolean videoIsDmc = Path.contains(VideoFile, "dmc_");
			if(VideoTitle==null){
				setVideoTitleIfNull(file.getName());
			}
			file = replaceFilenamePattern(file, videoIsLow, false);
			String convfilename = file.getName();
			folder = file.getParentFile();
			if(videoIsLow && !convfilename.contains("low_")){
				if(convfilename.contains(VideoID))
					convfilename = convfilename.replace(VideoID, lowVideoID);
				else if(convfilename.contains(Tag))
					convfilename = convfilename.replace(Tag, Tag+"low_");
				else
					convfilename = "low_" + convfilename;
			}
			ConvertedVideoFile = new File(folder, convfilename);
		}
		if (ConvertedVideoFile.getAbsolutePath().equals(VideoFile.getAbsolutePath())){
			sendtext("�ϊ���̃t�@�C�������ϊ��O�Ɠ����ł�");
			result = "96";
			return false;
		}
		if(ConvertedVideoFile.isFile() && ConvertedVideoFile.canRead()){
			sendtext("�ϊ���̃t�@�C���͊��ɑ��݂��܂�");
			log.println("�ϊ���̃t�@�C���͊��ɑ��݂��܂�");
			String otherFilename = "1"+ ConvertedVideoFile.getName();
			if(ConvertedVideoFile.renameTo(new File(ConvertedVideoFile.getParentFile(),otherFilename))){
				sendtext("�����̃t�@�C�������l�[�����܂���");
				log.println("�����̃t�@�C�������l�[�����܂���"+otherFilename);
			}else{
				sendtext("�����̃t�@�C�������l�[���o���܂���ł����B�㏑�����܂�");
				log.println("�����̃t�@�C�������l�[���o���܂���ł����B�㏑�����܂�");
			}
		}
		int code = converting_video();
		//stopwatch.stop();
		//vhext(nicovideo���O)���R�s�[����
		if(video_vhext != null){
			log_vhext = new File(".","[log]vhext.txt");
			try{
				if(!log_vhext.exists()|| log_vhext.canWrite()){
					if(!video_vhext.exists())
						video_vhext = Path.mkTemp("sm0[log]vhext.txt");
					if(video_vhext.exists()){
						Path.fileCopy(video_vhext, log_vhext);
					}
					else
						log.println(video_vhext.getPath()+" ���L��܂���.");
				}
			}catch(Exception e){
				log.println(video_vhext.getPath()+" �ɏ����܂���.");
			}
		}
		if (code == 0) {
			sendtext("�ϊ�������ɏI�����܂����B");
			log.println(lastFrame);
			return true;
		} else if (code == CODE_CONVERTING_ABORTED) { /*���f*/
			result = "97";
		} else {
			if(errorLog==null||errorLog.isEmpty())
				if(ffmpeg!=null)
					errorLog = ffmpeg.getLastError().toString();
			sendtext("�ϊ��G���[�F(" + code + ") "+ getLastError());
			result = ""+code;
		}
		return false;
	}

	private void setVidTitile(int tid, String tag, String title) {
		sendtext("@vid"+" ("+tid+")"+tag+"_"+title);
	}

	/**
	 * replaceFilenamePattern(File source)
	 * @param file
	 * @return
	 *  %ID% -> Tag, %id% -> [Tag](VideoID�Ɠ���) %TITLE% -> VideoTitle,
	 *  %CAT% -> ��������΃J�e�S���[�^�O, %TAG1% ->�Q�Ԃ߂̓���^�O
	 *  %TAGn% (n=2,3,...10) n+1�Ԗڂ̃^�O
	 */
	private File replaceFilenamePattern(File file, boolean economy, boolean dmc) {
		String videoFilename = file.getPath();
		if(VideoTitle==null){
			String filename = file.getName();
			// filename = filename.replace("%title%","").replace("%TITLE%", "");
			// Maybe bug, if contains
			setVideoTitleIfNull(filename);
		}
		if(nicoCategory==null)
			nicoCategory = "";
		String canonical =
			VideoTitle.replace("�@", " ").replaceAll(" +", " ").trim()
			.replace("�D", ".");
		String lowString = economy? "low_":(dmc?"dmc_":"");
		String surfix = videoFilename.contains("%LOW%")? "":lowString;
		videoFilename =
			videoFilename.replace("%ID%", Tag+surfix) 	// %ID% -> ����ID
			.replace("%id%", VideoID+surfix)	// %id% -> [����ID]
			.replace("%LOW%", lowString)	// %LOW% -> economy�� low_
			.replace("%TITLE%",VideoTitle)	// %TITLE% -> ����^�C�g��
			.replace("%title%", canonical)	// %title% -> ����^�C�g���i�󔒑啶�����󔒏������Ɂj
			.replace("%CAT%", nicoCategory)		// %CAT% -> ��������΃J�e�S���[�^�O
			;
		for(int i = 1; i < numTag; i++){
			videoFilename = videoFilename.replace("%TAG"+i+"%", nicoTagList.get(i));
		}
		File target = new File(videoFilename);
		File parent = target.getParentFile();
		if(!parent.isDirectory()){
			if(parent.mkdirs()){
				log.println("folder created: "+parent.getPath());
			}
			if(!parent.isDirectory()){
				log.println("�t�H���_���쐬�ł��܂���:"+parent.getPath());
				log.println("�u�����s "+videoFilename);
				target = file;
			}
		}
		return target;
	}

	private static String safeAsciiFileName(String str) {
		return NicoClient.toSafeWindowsName(str, "MS932");
	}

	private boolean canRetry(NicoClient client, Gate gate){

		//�Q�[�g���������Ȃ��Ȃ烊�g���C�\
		String ecode;
		if(client==null) return false;
		gate.setError();
		ecode = client.getExtraError();
		if(ecode==null) {
			// illegal error code, cannnot retry
			return false;
		}
		if((ecode.contains("503") || ecode.contains("504"))){
			//	HTTP_UNAVAILABLE  HTTP_GATEWAY_TIMEOUT
			//  �T�[�r�X���ꎞ�I�ɉߕ��� �Q�[�g�E�F�C�^�C���A�E�g
			// retry count check
			sendtext("���g���C�҂���");
			TimerTask task = new Tick(Status, "���g���C�҂��@");
			Timer timer = new Timer("���g���C�b�Ԋu�^�C�}�[");
			timer.schedule(task, 0, 1000);	// 1000 miliseconds
			if(gate.notExceedLimiterGate()){
				// can retry
				client.setExtraError("retry,");
				timer.cancel();
				return true;
			}
			timer.cancel();
			sendtext("���g���C���s");
		}
		// not error or other error,cannnot retry
		return false;
	}

	public void abortByCancel(){
		StopFlag.finish();
		StopFlag.setButtonEnabled(false);
		result = "FF";
		sendtext("[FF]Converter cancelled.");
		log.println("LastStatus:[FF]Converter cancelled.");
		sbRet.append("RESULT=FF\n");
		if(!Tag.contains(WatchInfo))
			Tag += WatchInfo;
		errorControl.setError(result,Tag,gettext());
	}

	@Override
	protected String doInBackground() throws Exception {
		Logger savelog = log;
		log = new Logger(null);
		synchronized (StopFlag) {
			while(StopFlag.isPending()){
				StopFlag.wait();
			}
			if(stopFlagReturn()){
				abortByCancel();
				manager.reqDone(result, StopFlag, isConverting);
				return "FF";
			}
			StopFlag.start();
		}

		log = new Logger(Tag, tid, TMP_LOG_FRONTEND);
		log.addSysout(savelog);
		gate = Gate.open(tid,log);
		stopwatch.clear();
		stopwatch.start();
		//manager.sendTimeInfo();
		try {
			if(parent!= null){
				Setting = parent.getSetting();
			}
			if (!checkOK()) {
				return result;
			}
			isOptionalTranslucent = Setting.isOptionalTranslucent();
			boolean success = false;
			NicoClient client = null;
			if (isSaveVideo() || isSaveComment() || isSaveOwnerComment()
					|| Setting.isSaveThumbInfo()) {
				do{
					client = ConvertManager.getManagerClient(this);
				}while (!stopFlagReturn() && canRetry(client, gate));
			}

			if (client != null){
				if (!client.isLoggedIn()){
					result = "-2";
					return result;
				}
			//	Gate.resetLimit();
				do{
					success = client.getVideoInfo(Tag, WatchInfo, Time, Setting.isSaveWatchPage());
				}while (!success && canRetry(client, gate));
				if (!success) {
					if(Tag==null || Tag.isEmpty()){
						sendtext("URL/ID�̎w�肪����܂��� " + client.getExtraError());
					}else if(!client.loginCheck()){
						sendtext("���O�C�����s " + BrowserKind.getName() + " " + client.getExtraError());
					}else{
						sendtext(Tag + "�̏��̎擾�Ɏ��s " + client.getExtraError());
					}
					result = "-3";
					return result;
				}
				if (stopFlagReturn()) {
					return "97";
				}
				gate.resetError();
				VideoTitle = client.getVideoTitle();
				thumbInfoData = client.getThumbInfoData();
			//	log.println("size_high: "+client.getSizeSmile());
			//	log.println("is_eco: "+client.isEco());
				VideoBaseName = Setting.isChangeTitleId()?
					VideoTitle + VideoID : VideoID + VideoTitle;
				nicoCategory = ""+client.getNicocategory();	// null-> "null"
				List<String> ct = client.getNicotags();
				numTag = 11;
				nicoTagList = new ArrayList<String>();
				int i = 0;
				if(ct!=null){
					nicoTagList.addAll(0, ct);
					i = ct.size();
				}
				while(i<numTag){
					nicoTagList.add(i++,"");
				}
				nicoTagList.remove(nicoCategory);
				nicoTagList.add(0, "");
				numTag = nicoTagList.size();
				setVidTitile(tid, Tag, VideoTitle);
				sendtext(Tag + "�̏��̎擾�ɐ���");
				if(alternativeTag.isEmpty()){
					alternativeTag = client.getAlternativeTag();
				}
			}

			//stopwatch.show();
			success = false;
			do{
				success = saveVideo(client);
			}while (!stopFlagReturn() && !success && canRetry(client, gate));
			if(!success) return result;
			gate.resetError();

			//stopwatch.show();
			success = false;
			do{
				success = saveOwnerComment(client);
			}while (!stopFlagReturn() && !success && canRetry(client, gate));
			if(!success) return result;
			gate.resetError();

			//stopwatch.show();
			success = false;
			do{
				success = saveComment(client);
			}while (!stopFlagReturn() && !success && canRetry(client, gate));
			if(!success) return result;
			gate.resetError();

			//stopwatch.show();
			if(!saveThumbInfo(client, Tag)){
				if(isSaveConverted())
					log.println("�ǉ����̎擾�Ɏ��s���܂��������s���܂��B");
				else {
					String tstr = gettext();
					if(isSaveComment()) {
						tstr = "�R�����g�擾�����@" + tstr;
					}
					if(isSaveVideo()) {
						tstr = "����擾�����@" + tstr;
					}
					tstr = "[�x��]" + tstr;
					sendtext(tstr);
					log.println(tstr);
					return result;
				}
			}
			if(stopFlagReturn()){
				return result;
			}

			//stopwatch.show();
			String before = stopwatch.formatElapsedTime();
			log.println("�ϊ��O���ԁ@" + before);
			stopwatch.setTrailer("�A�ϊ��O "+before);

			gate.exit(result);
			//manager.sendTimeInfo();
			if (!isSaveConverted()) {
				sendtext("����E�R�����g��ۑ����A�ϊ��͍s���܂���ł����B");
				result = "0";
				return result;
			}

			if(!isConverting){
				manager.incNumConvert();
				isConverting = true;
			}
			//stopwatch.show();
			if(!makeNGPattern(Setting.isNGenableML()) || stopFlagReturn()){
				return result;
			}

			//stopwatch.show();
			if (!convertOwnerComment() || stopFlagReturn()){
				return result;
			}

			//stopwatch.show();
			if (!convertComment() || stopFlagReturn()) {
				return result;
			}

			//stopwatch.show();
			if (!convertOprionalThread() || stopFlagReturn()) {
				return result;
			}

			//���[�J������thumbInfoData�Z�b�g
			if(thumbInfoData==null){
				String ext = Setting.isSaveThumbInfoAsText()? ".txt":".xml";
				if(thumbInfoFile==null)
					thumbInfoFile = Path.getReplacedExtFile(VideoFile, ext);
				if(thumbInfoFile!=null  && thumbInfoFile.equals(CommentFile) && thumbInfoFile.canRead())
					thumbInfoData = Path.readAllText(thumbInfoFile, "UTF-8");
				if(thumbInfoData!=null
				  && !thumbInfoData.contains("status=\"ok\"")){
					// �������ł͂Ȃ�thumbinfo���[�h���s�܂���commentfile��������
					// ����̃��^�f�[�^�����Ă���
					thumbInfoData=null;
				}
			}

			//stopwatch.show();
			if (convertVideo()) {
				// �ϊ�����
				result = "0";
				autoPlay.offer(ConvertedVideoFile, false);
				if(!autoPlay.isPlayDownload())
					autoPlay.playAuto();
				if (isDeleteCommentAfterConverting())
					deleteCommentFile();
				if (isDeleteVideoAfterConverting())
					deleteFile(VideoFile);
				deleteFile(CommentMiddleFile);
				deleteFile(OwnerMiddleFile);
				deleteFile(OptionalMiddleFile);
				deleteFile(CombinedCommentFile);
				deleteFile(CombinedOptionalFile);
				return result;
			}
		} catch (IOException ex) {
			log.printStackTrace(ex);
			if("0".equals(result))
				result = "EX";
		} finally {
			sbRet.append("RESULT=" + result + "\n");
			if(!dateUserFirst.isEmpty()){
				sbRet.append("DATEUF=" + dateUserFirst + "\n");
				if(parent!=null && Setting.isSetDateUserFirst()){
					String timewayback = "";
					if(Time!=null && !Time.isEmpty()){
						timewayback = WayBackDate.toSourceFormat(Time);
					}
					String wayback = WayBackDate.toSourceFormat(dateUserFirst);
					if(!wayback.equals(timewayback)){
						parent.setDateUserFirst(wayback);
					}
				}
			}
			String url = Tag.contains(WatchInfo)? Tag : Tag+WatchInfo;
			if(result.equals("97"))
				errorControl.setError(result,url,"���~���܂���");
			else if(result.equals("98")){
				StringBuffer sb = new StringBuffer(Tag+"\t���g���C\t"+WatchInfo);
				if(parent!=null){
					parent.myListGetterDone(sb, log);
				}else{
					errorControl.setError(result,url,"�T�X�y���h\t"+resumeDmcFile);
				}
			}
			else
			if(!result.equals("0"))
				errorControl.setError(result,url,gettext());
			synchronized(StopFlag){
				StopFlag.finish();
				StopFlag.setButtonEnabled(false);
			}
			//stopwatch.show();
			stopwatch.stop();
			stopwatch.cancel();
			log.println("�ϊ����ԁ@" + stopwatch.formatLatency());
			log.println("LastStatus:[" + result + "]" + gettext());
			log.println("VideoInfo: " + MovieInfo.getText());
			log.println("LastFrame: "+ lastFrame);

			gate.exit(result);
			manager.reqDone(result, StopFlag, isConverting);
			isConverting = false;
			//manager.sendTimeInfo();

			//end alarm
			File wav = new File("end.wav");
			if(wav.exists()){
				if(!AudioPlay.playWav(wav)){
					sendtext("wav error");
				};
			}
//			File exe = new File("end.exe");
//			File bat = new File("end.bat");
//			if(bat.exists()){
//				// batch file ���s
//				CmdExec cmdexec = new CmdExec(bat,ConvertedVideoFile.getAbsolutePath());
//				cmdexec.start();
//			}else if (exe.exists()){
//				// exe file ���s
//				CmdExec cmdexec = new CmdExec(exe,ConvertedVideoFile.getAbsolutePath());
//				cmdexec.start();
//			}
		}
		return result;
	}

	public void done(){
		String retStr = null;
		try {
			retStr = get();
		} catch (InterruptedException | ExecutionException e) {
			log.printStackTrace(e);
		}
		if(retStr == null)
			log.println("ConvertWorker#done.ret==null. ConvertWorker might had Exception!");
		else {
			System.out.println("["+retStr+"]Converter.done! "+Tag);
			if(result.equals("0") && !isDebugNet)
				log.deleteLog();
		}
	}

	private void deleteList(ArrayList<File> list){
		if (list== null || list.isEmpty())
			return;
		log.print("Deleted: ");
		for (File file : list){
			if(file.delete())
				log.print(file.getName()+" ");
		}
		log.println("done.");
	}
	private void deleteFile(File file){
		if (file != null && file.canWrite()
		 && file.delete())
			log.println("Deleted: " + file.getPath());
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
			log.printStackTrace(e);
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
		metaDataFile = mkTemp("METADATA");
		VideofileInfo info = new VideofileInfo(video, ffmpeg, Status, StopFlag, stopwatch, metaDataFile, log);
		videoAspect = info.getAspect();
		if(videoLength <= 0){
			videoLength = info.getDuration();
		}
		frameRate = info.getFrameRate();
		checkFps = Setting.enableCheckFps();
		fpsUp = Setting.getFpsUp();
		fpsMin = Setting.getFpsMin();
		String fixed = "";
		if(frameRate == 0.0){
			frameRate = 25.0;
			log.println("frameRate error, set frameRate to Default");
		}
		else if(Setting.isFpsIntegralMultiple()){
			try{
				int fpsMultiple = Math.max(1,(int)Math.round(fpsUp/frameRate));
				fpsUp = fpsMultiple * frameRate;
				fixed = "(fixed)";
			}catch(RuntimeException e){
				//
			}
		}
		log.println("\nframeRate:"+frameRate+",fpsUp:"+fpsUp+fixed+",fpsMin:"+fpsMin);
		String str;
		if (videoAspect == null || videoAspect.isInvalid()){
			str = "Analize Error   ";
			videoAspect = Aspect.ERROR;
		} else {
			str = videoAspect.explain() + "  ";
		}
		isPlayerWide = videoAspect.isWide() || videoAspect.isInvalid();
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
		} else if (isPlayerWide){
			selectedVhook = VhookWide;
		} else {
			selectedVhook = VhookNormal;
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
			if(info.isAudioContainsAac()){
				String[] ac;
				LinkedHashMap<String,String> optmap = outputOptionMap;
				if(getAudioCodecKV(optmap)==null){
					optmap = mainOptionMap;
				}
				if(optmap.get("-af")==null){
					if(((ac = getAudioCodecKV(optmap))!=null) && (ac[1].contains("aac"))){
						replaceOption(optmap,ac[0],"copy");
						log.println("Changed: "+ac[0]+" "+ac[1]+" -> copy");
					}
				}
			}
		}
		//AAC-LC copy if -alcp set
		if(getAacLcCopyFlag()){
			if(info.isAudioContainsAacLc()){
				String[] ac;
				LinkedHashMap<String,String> optmap = outputOptionMap;
				if(getAudioCodecKV(optmap)==null){
					optmap = mainOptionMap;
				}
				if(optmap.get("-af")==null){
					if(((ac = getAudioCodecKV(optmap))!=null) && (ac[1].contains("aac")) && !ac[1].contains("he")){
						replaceOption(optmap,ac[0],"copy");
						log.println("Changed: "+ac[0]+" "+ac[1]+" -> copy");
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
			//return true;
		}
		if(videoAspect.isInvalid() && !outAspect.isInvalid()){
			videoAspect = outAspect;
			inSize = outAspect.getSize();
		}
		if(videoAspect.isInvalid()){ // and outAspect is also invalid
			if(selectedVhook==VhookNormal)
				inSize = "512:384";
			else
				inSize = "640:360";
			videoAspect = toAspect(inSize, Aspect.WIDE);
			outAspect = videoAspect;
		}
		str = videoAspect.explain() + "  ";
		if (Setting.isZqPlayer()){
			MovieInfo.setText(auto + "�g��Vhook Q " + str);
		} else if (isPlayerWide){
			MovieInfo.setText(auto + "�g��Vhook ���C�h " + str);
		} else {
			MovieInfo.setText(auto + "�g��Vhook �]�� " + str);
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
			log.println(" framerate="+ropt);
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
		log.println("Output Video Area " + width + ":" + height);
		//width height�͏o�͓���̑傫��(outs�w�莞�͂��̃T�C�Y)
		log.println("Video "+aspect.getSize());
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
		log.println("Output Commetnt Area " + width + ":" + height + " Wide? " + isPlayerWide);
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
				log.printStackTrace(e);
			}
		}
		int height = defaultAspect.getHeight();
		if(list.length>=2 && !list[1].equals("0")){
			try {
				height = Integer.parseInt(list[1]);
			} catch(NumberFormatException e){
				log.printStackTrace(e);
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
	private boolean getAacLcCopyFlag(){
		//-alcp
		return outputOptionMap.remove("-alcp") != null;
	}
	private String[] getAudioCodecKV(HashMap<String,String> map){
		String[] pair = new String[2];
		String value = "";
		String[] keys = {"-acodec","-codec:a","-c:a"};
		for (String key:keys){
			value = map.get(key);
			if(value!=null){
				pair[0] = key;
				pair[1] = value.toLowerCase();
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
		if(Setting.enableMetadata())
			ffmpeg.addCmd(getMetadata());
		ffmpeg.addCmd(" ");
	}

	private String getMetadata(){
		String opt;
		File file;
		ArrayList<String> optlist = new ArrayList<>();
		opt = Setting.getZqMetadataOption();
		for(String os:opt.split(" +")){
			file = new File("temp",safeAsciiFileName(os));
			optlist.add(replaceFilenamePattern(file, false, false).getName());
		}
		if(Setting.getDefOptsSaveThumbinfoMetadata()){
			String desc = thumbInfoData;
			if(desc==null){
				if(metaDataFile!=null && metaDataFile.canRead()){
					desc = Path.readAllText(metaDataFile, "UTF-8");
					desc = escapeMetadata(desc);
				}
			}
			if(desc!=null){
				optlist.add("description="+escapeQuote(desc));
			}
		}
		StringBuffer sb = new StringBuffer();
		for(String os:optlist){
			sb.append(" -metadata \""+os+"\"");
		}
		return sb.substring(0);
	}

	private String escapeMetadata(String info){
		return info
//			.replace("\\", "\\\\")
			.replace(";", "\\;")
//			.replace("=", "\\=")
//			.replace("\n", "\\\n")
//			.replace("#", "\\#")
			;
	}

	private String escapeQuote(String info){
		info = info.replace("\"", "\\\"");
		return info;
	}

	private boolean setOption3(File outfile){
		if (!Setting.isVhookDisabled()) {
			if(!addVhookSetting(ffmpeg, selectedVhook, isPlayerWide)){
				return false;
			}
		} else if(!getFFmpegVfOption().isEmpty()){
			String vfopt = getFFmpegVfOption();
			vfopt = Pattern.compile("(,@(=[^,]+)?|@(=[^,]+)?,?)").matcher(vfopt).replaceAll("");
			if(!vfopt.isEmpty()){
				ffmpeg.addCmd(" "+vfilter_flag+" ");
				ffmpeg.addCmd(vfopt);
			}
		}
		ffmpeg.addCmd(" ");
		ffmpeg.addFile(outfile);
		return true;
	}

	private int execOption(){
		int code;
		log.println("arg:" + ffmpeg.getCmd());
		code = ffmpeg.exec(Status, CODE_CONVERTING_ABORTED, StopFlag, stopwatch, log);
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
		//ffmpeg.setCmd("-y -analyzeduration 10M -i ");
		ffmpeg.setCmd("-y -i ");	//swf�̏ꍇ�A��͎��Ԃ͂��Ƃ̂܂܂ɂ���
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
		log.printf("Frame= %d, Rate= %.5f(fps)\n", frame, rate);
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
		log.printf("Frame= %.2f(sec/frame), Rate= %.5f(fps)\n", length_frame, rate);
	//	if(tl != 0.0){
	//		tl += length_frame;
	//	}
		log.printf("Frame= %d, Rate= %.5f(fps)\n", frame, rate);

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
		//ffmpeg.setCmd("-y -analyzeduration 10M -i ");
		ffmpeg.setCmd("-y -i ");	//swf�̏ꍇ�A��͎��Ԃ͂��Ƃ̂܂܂ɂ���
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
					NicoClient client = ConvertManager.getManagerClient(this);
					if(thumbInfoFile==null || !thumbInfoFile.canRead())
						saveThumbInfo0(client, Tag);
					if(saveThumbnailJpg(thumbInfo, client)){
						thumbfile = thumbnailJpg;
					}
				}
			}
		}else{
			String currect_dir = System.getenv("CD");
			log.println("CD:"+currect_dir);
			thumbfile = new File(currect_dir, thumbname);
		}
		if(!thumbfile.canRead()){
			log.println("�T���l�C�����ǂ߂܂���F"+thumbfile.getPath());
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
					//stopwatch.show();
				}
				copyok = true;
			}catch(IOException e){
				log.printStackTrace(e);
			}finally{
				try{
					if(fis!=null){
						fis.close();
					}
					if(fos!=null){
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
			log.println("�T���l�C�����ǂ߂܂���F"+thumbfile.getPath());
			sendtext("��փT���l�C�����ǂ߂܂���");
			errorLog = "��փT���l�C�����ǂ߂܂���";
			code = 198;
			return code;
		}
		code = convFLV_thumbaudio(thumbfile, input, output);
		return code;
	}

	private int convFLV_thumbaudio(File thumbin, File audioin, File videoout){
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
		if(alternativeTag.isEmpty()){
			alternativeTag = Tag;
		}
		if(fwsFile!=null)
			input = fwsFile;
		if (!Cws2Fws.isFws(input) && !Cws2Fws.isCws(input)) {
			//�ʏ��FLV
			// fps up check
			if(checkFps && frameRate < fpsMin){
				//FPS�ϊ��K�v
				if(Setting.isUseFpsFilter()){
					//FPS Filter�I��
					log.println("FPS filter");
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
					log.println("("+code+")fps filter�Ɏ��s ");
					errorLog += "\nfps filter�Ɏ��s "+ getLastError();
					log.println("���s\n");	//���s���[�h
				}

				// 2�p�XFPS�ϊ�
				File outputFps = Path.mkTemp("fpsUp"+ConvertedVideoFile.getName());
				log.println("FLV Up "+fpsUp+"fps");
				infoStack.pushText("FLV "+fpsUp);
				code = conv_fpsUp(input, outputFps);
				infoStack.popText();
				if(code == CODE_CONVERTING_ABORTED){
					return code;
				}
				if(code != 0){
					//error
					log.println("("+code+")fps�ϊ��Ɏ��s ");
					errorLog += "\nfps�ϊ��Ɏ��s "+ getLastError();
					if(Setting.canSoundOnly()){
						log.println("�R�����g�Ɖ����������������܂�");
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
			log.println("FLV �]���ʂ�");
			String vfoptsave = getFFmpegVfOption();
			if(checkFps && Setting.isUseFpsFilter()){
				String vfopt = "";
				String ropt = getRopt();
				if(ropt != null && !ropt.isEmpty()){
					vfopt = "fps=fps="+ropt
						+ ",scale="+outAspect.getSize();
					// -s �I�v�V������ -vf scale=w:h �Ƃ��Đ�ɒǉ�
					log.println("FPS filter -r "+ropt);
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
					log.println("FWS fpsUp");
					infoStack.pushText("FWS fpsUp");
					code = conv_fpsUp(input, outputFps);
					infoStack.popText();
					if(code == CODE_CONVERTING_ABORTED){
						return code;
					}
					if (code != 0){
						log.println("("+code+")fps�ϊ��Ɏ��s ");
						errorLog += "\nfps�ϊ��Ɏ��s "+ getLastError();
						if(Setting.canSoundOnly()){
							log.println("�R�����g�Ɖ����������������܂�");
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
				log.println("FWS �]���ʂ�");
				infoStack.pushText("FWS");
				code = convFLV(input, ConvertedVideoFile);
				infoStack.popText();
				return code;
			} else {
				log.println("FWS 3path");
				// try 3 path
				/*
				 * SWF�t�@�C����JPEG�`���ɍ���
				 * ffmpeg.exe -y -i fws_tmp.swf -an -vcodec copy -f image2 %03d.jpg
				 */
				//�o�͐�����
				imgDir = Path.mkTemp("IMG"+VideoID);
				if(imgDir.mkdir())
					log.println("Created folder - " + imgDir);
				File outputImg = new File(imgDir,"%03d.jpeg");
				log.println("outputImg="+outputImg);
				log.println("Tring SWF to .number.JPG");
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
				 * video length��0�̎��ɂ͉������ɒ��o����B
				 */
				if(videoLength==0){
					File tempAudio = new File(imgDir,"audio.m4a");
					log.println("tempAudio="+tempAudio);
					log.println("Tring SWF to audio(M4A)");
					infoStack.pushText("SWF->audio");
					/*
					 * ffmpeg -y -i input temp.m4a
					 */
					ffmpeg.setCmd("-y -i ");
					ffmpeg.addFile(input);
					ffmpeg.addCmd(" ");
					ffmpeg.addFile(tempAudio);
					log.println("arg:" + ffmpeg.getCmd());
					code = ffmpeg.exec(Status, CODE_CONVERTING_ABORTED, StopFlag, stopwatch, log);
					errorLog = ffmpeg.getErrotLog().toString();
					lastFrame = ffmpeg.getLastFrame();
					infoStack.popText();
					if(code != 0)
						return code;
					VideofileInfo audioinfo = new VideofileInfo(tempAudio, ffmpeg, Status, StopFlag, stopwatch, metaDataFile, log);
					videoLength = audioinfo.getDuration();
					if(videoLength <= 0){
						if(code == 0) code = -999;
						return code;
					}
				}
				/*
				 * JPEG�t�@�C����MP4�`���ɍ���
				 * ffmpeg.exe -r 1/4 -y -i %03d.jpg -an -vcodec huffyuv -f avi huffjpg.avi
				 */
				//�o��
				File outputAvi = new File(imgDir,"huffyuv.mp4");
				log.println("outputImg="+outputImg);
				log.println("outputAvi="+outputAvi);
				log.println("Tring JPG to .MP4");
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
				log.println("Tring MP4+sound to .MP4");
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
				log.println("Tring MIX & comment to .mp4");
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
				// s�́@-vf �̃p�����[�^, �����ł�""�ň͂܂�Ă���͂�(saccubus�̎d�l)
				s = unquote(s);
				sb.append("\"");
				String s2 = "";	// vhext=�̌��Ɉړ����镶����
				// vhext=��s�̍Ō�ɂ��Ă���(saccubus�̎d�l)
				// @�̈ʒu��vhext�̈ʒu��ύX
				int index = s.indexOf("@=");
				if(index >= 0){
					// @=��������
					sb.append(s.substring(0, index));
					s = s.substring(index+2); // @=�ǂݔ�΂�
					index = s.indexOf(",");
					if(index < 0)
						return false;
					if(index > 0)
						vhspeedrate = s.substring(0, index);	// @=�̃p�����[�^
					s = s.substring(index+1);
					index = s.indexOf("vhext=");
					if(index < 0)
						return false;
					if(index > 0)
						s2 = s.substring(0, index-1);
				}else{
					index = s.indexOf("@,");
					if(index >= 0){
						// @,��������
						sb.append(s.substring(0, index));
						s = s.substring(index+2);	// @,�ǂݔ�΂�
						index = s.indexOf("vhext=");
						if(index < 0)
							return false;
						if(index > 0)
							s2 = s.substring(0, index-1);
					}else{
						index = s.indexOf("vhext=");
						if(index < 0)
							return false;
						sb.append(s.substring(0, index));
					}
				}
				// vhext���O��vf�͒ǉ��ς�
				sb.append("vhext=");
				index += "vhext=".length();
				s = s.substring(index);
				if(vhspeedrate!=null && !vhspeedrate.isEmpty())
					s = s.replace(END_OF_ARGUMENT,
						"|--vfspeedrate:"+vhspeedrate+END_OF_ARGUMENT);
				s = vf_quote(s);	// vhext= �̃I�v�V������ video filter�p�� quote����
				sb.append(s);
				if(!s2.isEmpty()){
					sb.append(",");
					sb.append(s2);
				}
				sb.append("\"");
			}else{
				sb.append(s);
			}
			sb.append(' ');
		}
		s = sb.substring(0);
		ffmpeg.addCmd(s);
		return true;
	}

	private String unquote(String s) {
		return NicoClient.unquote(s);
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
			String vfopt = getFFmpegVfOption();
			if(!outAspect.isInvalid()){
				if(vfopt.isEmpty()){
					// vfopt�Ȃ�
					// -s �I�v�V������ -vf scale=w:h �Ƃ��Đ�ɒǉ�
					ffmpeg.addCmd("scale="+outAspect.getSize());
					ffmpeg.addCmd(",");
				}else{
					if(vfopt.contains("scale=")){
						// vfopt��scale������ꍇ�͕ύX���Ȃ��B
						ffmpeg.addCmd(vfopt);
						ffmpeg.addCmd(",");
					}else{
						// vfopt��scale�Ȃ�
						int index = vfopt.indexOf("@");
						if(index >= 0){
							// vfopt��@����
							if(index > 0){
								// @���O��,���܂�ł���
								ffmpeg.addCmd(vfopt.substring(0, index));
							}
							// -s �I�v�V������ -vf scale=w:h �Ƃ��Đ�ɒǉ�
							ffmpeg.addCmd("scale="+outAspect.getSize());
							ffmpeg.addCmd(",");
							ffmpeg.addCmd(vfopt.substring(index));
							// @�����̍Ō�ɂ�,���܂�ł��Ȃ�
							ffmpeg.addCmd(",");
						}else{
							// vfopt��@�Ȃ�
							ffmpeg.addCmd(vfopt);
							ffmpeg.addCmd(",");
							// -s �I�v�V������ -vf scale=w:h �Ƃ��Đ�ɒǉ�
							ffmpeg.addCmd("scale="+outAspect.getSize());
							ffmpeg.addCmd(",");
						}
					}
				}
			}else{
				// outAspect���s���ȏꍇ
				if(!vfopt.isEmpty()){
					ffmpeg.addCmd(vfopt);
					ffmpeg.addCmd(",");
				}
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
				if (isOptionalTranslucent) {
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
			if (Setting.isSetCommentSpeed()){
				ffmpeg.addCmd("|--comment-speed:");
				if(comment_speed != null && !comment_speed.isEmpty())
					ffmpeg.addCmd(URLEncoder.encode(comment_speed, encoding));
			}
			if(convertIsLive()){
				ffmpeg.addCmd("|--live");
			}
			if(Setting.isEnableCommentVposShift()){
				ffmpeg.addCmd("|--comment-shift:"
					+ URLEncoder.encode(Setting.getCommentVposShiftString(), encoding));
			}
			ffmpeg.addCmd("|--comment-erase:" + Setting.getCommentEraseType());
			if(Setting.isCommentOff()){
				ffmpeg.addCmd("|--comment-off:"
					+URLEncoder.encode(Setting.getCommentOff(), encoding));
			}
			if(Setting.enableCommentLF()){
				ffmpeg.addCmd("|--comment-lf:"
					+URLEncoder.encode(Setting.getCommentLF(), encoding));
			}
			if(Setting.isLayerControl()){
				ffmpeg.addCmd("|--comment-layer");
			}
			if(Setting.isResizeAdjust()){
				ffmpeg.addCmd("|--resize-adjust:"
					+URLEncoder.encode(Setting.getResizeAdjust(), encoding));
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
			ffmpeg.addCmd(END_OF_ARGUMENT);
			ffmpeg.addCmd("\"");
			return true;
		} catch (UnsupportedEncodingException e) {
			log.printStackTrace(e);
			return false;
		}
	}

	private boolean convertIsLive() {
		if(Setting.isLive()){
			// ���[�J���ϊ���
			if(Pattern.matches("sm[0-8]|(sm[0-8]_)?lv.*", Tag))		// Tag�� sm9��菬�����ꍇ lv���܂ޏꍇ
				isLive = true;
			if(Tag.length() <= 2 || !Character.isDigit(Tag.charAt(2)))	// 3�����ڂ������ł͂Ȃ��ꍇ
				isLive = true;
			if(Pattern.matches("[a-zA-Z][0-9].*", Tag))	// �p��1����+�����̏ꍇ
				isLive = true;
		}
		else if(!MainFrame.idcheck(Tag))	// ID�ł͂Ȃ��������ݒ肳�ꂽ�ꍇ(�G���[�ɂȂ�?)
			isLive = true;
		return isLive;
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
			result = "97";
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
					log.print("�x���@�L�[���[�h�ł͂���܂���:"+w);
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
					log.print("�x���@'-'�g���Ă��܂�:"+w);
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
			log.println("�o�O���Ă�");
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
				log.printStackTrace(ex);
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
			if (name.indexOf("["+VideoTag+"]") >= 0){
				return true;
			}
			if(name.startsWith(VideoTag + "_")
				|| name.startsWith(VideoTag + "low_") || name.startsWith(VideoTag + "dmc_")){
				return true;
			}
			return false;
		}
	}

	private void setVideoTitleIfNull(String path) {
		String videoTitle = VideoTitle;
		if (videoTitle == null){
			videoTitle = getTitleFromPath(path, VideoID, Tag);
			// �ߋ����O�������폜
			String regex = "\\[" + WayBackDate.STR_FMT_REGEX + "\\]";
			videoTitle = videoTitle.replaceAll(regex, "");
		//	int index = videoTitle.lastIndexOf("[");
		//		//�ߋ����O��[YYYY/MM/DD_HH:MM:SS]���Ō�ɕt��
		//	if (index >= 0){
		//		videoTitle = videoTitle.substring(0, index);
		//	}
			log.println("Title<" + videoTitle + ">");
			VideoTitle = videoTitle;
			setVidTitile(tid,Tag,VideoTitle);
		}
	}

	String detectTitleFromVideo(File dir){
		if (dir == null){ return null; }
		String list[] = dir.list(DefaultVideoIDFilter);
		if(list == null){ return null; }
		for (int i = 0; i < list.length; i++) {
			String path = list[i];
			if (path.contains(VideoID)) {
				if(path.endsWith(".flv") ||  path.endsWith(".mp4")){
					setVideoTitleIfNull(path);
					return path;
				}
				OtherVideo = path;
			}
			if(path.startsWith(Tag+"_")||path.startsWith(Tag+"low_")||path.startsWith(Tag+"dmc_")){
				if(path.endsWith(".flv") ||  path.endsWith(".mp4")){
					setVideoTitleIfNull(path);
					return path;
				}
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
				if(".flv.f4v.mp4.avi.mpg.mpeg.wmv.webm".contains(ext)){
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
			if (!path.endsWith(".xml")
					|| path.endsWith(OWNER_EXT)
					|| path.endsWith(OPTIONAL_EXT)
					|| path.endsWith(NICOS_EXT)){
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

	private String detectTitleFromOptionalThread(File dir, String ext){
		String list[] = dir.list(DefaultVideoIDFilter);
		if(list == null){ return null; }
		for (int i = 0; i < list.length; i++) {
			String path = list[i];
			if (!path.endsWith(ext)){
				continue;
			}
			setVideoTitleIfNull(path.replace(ext, ""));
			return path;
		}
		return null;
	}

	private ArrayList<String> detectFilelistFromComment(File dir){
		String list[] = dir.list(DefaultVideoIDFilter);
		if (list == null) { return null; }
		ArrayList<String> filelist = new ArrayList<String>();
		for (String path : list){
			if (!path.endsWith(".xml")
					|| path.endsWith(OWNER_EXT)
					|| path.endsWith(OPTIONAL_EXT)
					|| path.endsWith(NICOS_EXT)){
				continue;
			}
			setVideoTitleIfNull(path);
			filelist.add(path);
		}
		return filelist;
	}

	private ArrayList<String> detectFilelistFromOptionalThread(File dir , String ext){
		String list[] = dir.list(DefaultVideoIDFilter);
		if (list == null) { return null; }
		ArrayList<String> filelist = new ArrayList<String>();
		for (String path : list){
			if (!path.endsWith(ext)){
				continue;
			}
			setVideoTitleIfNull(path.replace(ext, ""));
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
	private String getTitleFromPath(String path, String videoID, String Tag){
		if (path.contains(videoID)){
			path = path.replace(videoID, "");	// videoID�̈ʒu�͖��֌W�ɍ폜
		} else if(path.contains(Tag)){
			path = path.replace(Tag, "");
			if(path.startsWith("_")){
				path = path.substring(1);
			}
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

	public int getId() {
		return tid;
	}
	public String getVid() {
		return Vid;
	}
	public StringBuffer getSbRet() {
		return sbRet;
	}

}
