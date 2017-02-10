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
 * <p>タイトル: さきゅばす</p>
 *
 * <p>説明: ニコニコ動画の動画をコメントつきで保存</p>
 *
 * <p>著作権: Copyright (c) 2007 PSI</p>
 *
 * <p>会社名: </p>
 *
 * @author 未入力
 * @version 1.0
 *  非EventDispatchThreadのためGUI描画はすべてinvokeLater()にて行うこと
 *  JLabelの表示書き換えなど
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

			sendTimer(label, timerString + tick +"秒");
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
	public static final String OWNER_EXT = "[Owner].xml";	// 投稿者コメントサフィックス
	public static final String OPTIONAL_EXT = "{Optional}.xml";	// オプショナルスレッドサフィックス
	public static final String NICOS_EXT = "{Nicos}.xml";	//ニコスコメントサフィックス
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
	private String UserSession = "";	//ブラウザから取得したユーザーセッション
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
		sendtext("チェックしています");
		if (!isSaveConverted() && !isSaveVideo()
			&& !isSaveComment() && !isSaveOwnerComment()
			&& !Setting.isSaveThumbInfo()){
			sendtext("何もすることがありません");
			result = "1";
			return false;
		}
		if (isSaveConverted()) {
			File a = new File(Setting.getFFmpegPath());
			if (!a.canRead()) {
				sendtext("FFmpegが見つかりません。");
				result = "2";
				return false;
			}
			this.ffmpeg = new FFmpeg(Setting.getFFmpegPath());
			if (Setting.isZqPlayer()) {
				if(Setting.getZqVhookPath().indexOf(' ') >= 0){
					sendtext("すいません。現在vhookライブラリには半角空白は使えません。");
					result = "3";
					return false;
				}
				VhookQ = new File(Setting.getZqVhookPath());
				if(!VhookQ.canRead()){
					sendtext("共通拡張Vhookライブラリが見つかりません。");
					result = "4";
					return false;
				}
				wayOfVhook = 3;
			} else {
				if (Setting.isUseVhookNormal()){
					if(Setting.getVhookPath().indexOf(' ') >= 0) {
						sendtext("すいません。現在vhookライブラリには半角空白は使えません。");
						result = "3";
						return false;
					}
					VhookNormal = new File(Setting.getVhookPath());
					if (!VhookNormal.canRead()) {
						sendtext("Vhookライブラリが見つかりません。");
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
							sendtext("すいません。現在vhookファイル名には半角空白は使えません。");
							result = "5";
							return false;
						}
						VhookWide = new File(Setting.getVhookWidePath());
					}
					if (!VhookWide.canRead()) {
						sendtext("Vhookライブラリ（ワイド）が見つかりません。");
						result = "6";
						return false;
					}
					wayOfVhook++;
				}
			}
			if (wayOfVhook == 0){
				sendtext("使用できるVhookライブラリがありません。");
				result = "7";
				return false;
			}
			if(Setting.isEnableCA()){
				String windir = System.getenv("windir");
				if(windir == null){
					sendtext("Windowsフォルダが見つかりません。");
					result = "8";
					return false;
				}
				fontDir = new File(windir, "Fonts");
				if(!fontDir.isDirectory()){
					sendtext("Fontsフォルダが見つかりません。");
					result = "9";
					return false;
				}
				simsunFont = new File(fontDir, "SIMSUN.TTC");
				if (!simsunFont.canRead()) {
					sendtext("CA用フォントが見つかりません。" + simsunFont.getPath());
					result = "10";
					return false;
				}
				gulimFont = new File(fontDir, "GULIM.TTC");	//windowsXP,7,8 丸文字
				File saveGulimFont = gulimFont;
				if (!gulimFont.canRead()) {
					sendtext("警告　CA用フォントが見つかりません。" + gulimFont.getPath());
					gulimFont = new File(fontDir, "MALGUN.TTF");	//windows10 ハングル
				}
				if (!gulimFont.canRead()) {
					gulimFont = simsunFont;	// 明朝
				}
				if (!gulimFont.canRead()) {
					sendtext("CA用フォントの代替が見つかりません。" + gulimFont.getPath());
					result = "11";
					return false;
				}
				if(!gulimFont.equals(saveGulimFont)){
					log.println("CA用フォント" + saveGulimFont.getPath() + "を" + gulimFont.getName() + "で代替します。");
				}
				arialFont = new File(fontDir, "ARIAL.TTF");
				if(!arialFont.canRead()){
					sendtext("CA用フォントが見つかりません。" + arialFont.getPath());
					result = "12";
					return false;
				}
				gothicFont = new File(fontDir, "MSGOTHIC.TTC");
				if (!gothicFont.canRead()) {
					sendtext("CA用フォントが見つかりません。" + gothicFont.getPath());
					result = "13";
					return false;
				}
				georgiaFont  = new File(fontDir, "sylfaen.ttf");
				if (!georgiaFont.canRead()) {
					sendtext("警告　CA用フォントが見つかりません。" + georgiaFont.getPath());
					//retValue = "14";
					//return false;
					log.println("CA用フォント" + georgiaFont.getPath() + "を" + gothicFont.getName() + "で代替します。");
					georgiaFont = gothicFont;
				}
				nirmalaFont = new File(fontDir,"Nirmala.ttf");
				devabagariFont = new File(fontDir, "mangal.ttf");
				if (!devabagariFont.canRead()) {
					devabagariFont = nirmalaFont;
				}
				if(!devabagariFont.canRead()){
					sendtext("警告　CA用フォントが見つかりません。" + devabagariFont.getPath());
					//retValue = "15";
					//return false;
					log.println("CA用フォント" + devabagariFont.getPath() + "を" + arialFont.getName() + "で代替します。");
					devabagariFont = arialFont;
				}
				tahomaFont = new File(fontDir, "tahoma.ttf");
				if (!tahomaFont.canRead()) {
					sendtext("警告　CA用フォントが見つかりません。" + tahomaFont.getPath());
					//retValue = "16";
					//return false;
					log.println("CA用フォント" + tahomaFont.getPath() + "を" + arialFont.getName() + "で代替します。");
					tahomaFont = arialFont;
				}
				mingliuFont = new File(fontDir, "mingliu.ttc");
				if (!mingliuFont.canRead()) {
					sendtext("警告　CA用フォントが見つかりません。" + mingliuFont.getPath());
					//retValue = "17";
					//return false;
					log.println("CA用フォント" + mingliuFont.getPath() + "を" + simsunFont.getName() + "で代替します。");
					mingliuFont = simsunFont;
				}
				newMinchoFont = new File(fontDir, "SIMSUN.TTC");	//NGULIM.TTFが無かった
				if (!newMinchoFont.canRead()) {
					sendtext("警告　CA用フォントが見つかりません。" + newMinchoFont.getPath());
					//retValue = "18";
					//return false;
					log.println("CA用フォント" + newMinchoFont.getPath() + "を" + simsunFont.getName() + "で代替します。");
					newMinchoFont = simsunFont;
				}
				estrangeloEdessaFont = new File(fontDir, "estre.ttf");
				if (!estrangeloEdessaFont.canRead()) {
					estrangeloEdessaFont = new File(fontDir, "seguihis.ttf");
				}
				if (!estrangeloEdessaFont.canRead()){
					sendtext("警告　CA用フォントが見つかりません。" + estrangeloEdessaFont.getPath());
					//retValue = "19";
					//return false;
					log.println("CA用フォント" + estrangeloEdessaFont.getPath() + "を" + arialFont.getName() + "で代替します。");
					estrangeloEdessaFont = arialFont;
				}
				arialUnicodeFont = new File(fontDir, "arialuni.ttf");
				if (!arialUnicodeFont.canRead()) {
					sendtext("警告　CA用フォントが見つかりません。" + arialUnicodeFont.getPath());
					//retValue = "20";
					//return false;
					log.println("CA用フォント" + arialUnicodeFont.getPath() + "を" + arialFont.getName() + "で代替します。");
					arialUnicodeFont = arialFont;
				}
				gujaratiFont = new File(fontDir, "shruti.ttf");
				if (!gujaratiFont.canRead()){
					gujaratiFont = nirmalaFont;
				}
				if (!gujaratiFont.canRead()) {
					sendtext("警告　CA用フォントが見つかりません。" + gujaratiFont.getPath());
					//retValue = "21";
					//return false;
					log.println("CA用フォント" + gujaratiFont.getPath() + "を" + arialUnicodeFont.getName() + "で代替します。");
					gujaratiFont = arialUnicodeFont;
				}
				bengalFont = new File(fontDir, "vrinda.ttf");
				if (!bengalFont.canRead()) {
					bengalFont = nirmalaFont;
				}
				if(!bengalFont.canRead()){
					sendtext("警告　CA用フォントが見つかりません。" + bengalFont.getPath());
					//retValue = "22";
					//return false;
					log.println("CA用フォント" + bengalFont.getPath() + "を" + arialUnicodeFont.getName() + "で代替します。");
					bengalFont = arialUnicodeFont;
				}
				tamilFont = new File(fontDir, "latha.ttf");
				if (!tamilFont.canRead()) {
					tamilFont = nirmalaFont;
				}
				if (!tamilFont.canRead()) {
					sendtext("警告　CA用フォントが見つかりません。" + tamilFont.getPath());
					//retValue = "23";
					//return false;
					log.println("CA用フォント" + tamilFont.getPath() + "を" + arialUnicodeFont.getName() + "で代替します。");
					tamilFont = arialUnicodeFont;
				}
				laooFont = new File(fontDir, "laoui.ttf");
				if (!laooFont.canRead()) {
					laooFont = new File(fontDir, "LeelawUI.ttf");
				}
				if (!laooFont.canRead()) {
					sendtext("警告　CA用フォントが見つかりません。" + laooFont.getPath());
					//retValue = "24";
					//return false;
					log.println("CA用フォント" + laooFont.getPath() + "を" + arialUnicodeFont.getName() + "で代替します。");
					laooFont = arialUnicodeFont;
				}
				gurmukhiFont = new File(fontDir, "raavi.ttf");
				if (!gurmukhiFont.canRead()) {
					gurmukhiFont = nirmalaFont;
				}
				if (!gurmukhiFont.canRead()) {
					sendtext("警告　CA用フォントが見つかりません。" + gurmukhiFont.getPath());
					//retValue = "25";
					//return false;
					log.println("CA用フォント" + gurmukhiFont.getPath() + "を" + arialUnicodeFont.getName() + "で代替します。");
					gurmukhiFont = arialUnicodeFont;
				}
				kannadaFont = new File(fontDir, "tunga.ttf");
				if (!kannadaFont.canRead()) {
					kannadaFont = nirmalaFont;
				}
				if (!kannadaFont.canRead()) {
					sendtext("警告　CA用フォントが見つかりません。" + kannadaFont.getPath());
					//retValue = "26";
					//return false;
					log.println("CA用フォント" + kannadaFont.getPath() + "を" + arialUnicodeFont.getName() + "で代替します。");
					kannadaFont = arialUnicodeFont;
				}
				thaanaFont = new File(fontDir, "mvboli.ttf");
				if (!thaanaFont.canRead()) {
					sendtext("警告　CA用フォントが見つかりません。" + thaanaFont.getPath());
					//retValue = "27";
					//return false;
					log.println("CA用フォント" + thaanaFont.getPath() + "を" + arialUnicodeFont.getName() + "で代替します。");
					thaanaFont = arialUnicodeFont;
				}
				malayalamFont = new File(fontDir, "kartika.ttf");
				if (!malayalamFont.canRead()) {
					malayalamFont = nirmalaFont;
				}
				if (!malayalamFont.canRead()) {
					sendtext("警告　CA用フォントが見つかりません。" + malayalamFont.getPath());
					//retValue = "28";
					//return false;
					log.println("CA用フォント" + malayalamFont.getPath() + "を" + arialUnicodeFont.getName() + "で代替します。");
					malayalamFont = arialUnicodeFont;
				}
				teluguFont = new File(fontDir, "gautami.ttf");
				if (!teluguFont.canRead()) {
					teluguFont = nirmalaFont;
				}
				if (!teluguFont.canRead()) {
					sendtext("警告　CA用フォントが見つかりません。" + teluguFont.getPath());
					//retValue = "29";
					//return false;
					log.println("CA用フォント" + teluguFont.getPath() + "を" + arialUnicodeFont.getName() + "で代替します。");
					teluguFont = arialUnicodeFont;
				}
			}else{
				a = new File(Setting.getFontPath());
				if (!a.canRead()) {
					sendtext("フォントが見つかりません。");
					result = "30";
					return false;
				}
			}
		} else {
			if (isDeleteVideoAfterConverting()) {
				sendtext("変換しないのに、動画削除しちゃって良いんですか？");
				result = "31";
				return false;
			}
			if (isDeleteCommentAfterConverting()) {
				sendtext("変換しないのに、コメント削除しちゃって良いんですか？");
				result = "32";
				return false;
			}
		}
		proxy = Setting.getProxy();
		proxy_port = Setting.getProxyPort();
		if (isSaveVideo() || isSaveComment() || isSaveOwnerComment()
			|| Setting.isSaveThumbInfo()) {
			// ブラウザセッション共有の場合はここでセッションを読み込む
			UserSession = browserInfo.getUserSession(Setting);
			BrowserKind = browserInfo.getValidBrowser();
			if (BrowserKind == BrowserCookieKind.NONE){
				mailAddress = Setting.getMailAddress();
				password = Setting.getPassword();
				if (mailAddress == null || mailAddress.isEmpty()
					|| password == null || password.isEmpty()) {
					sendtext("ログインセッション無し、メールアドレスかパスワードが空白です。");
					result = "33";
					return false;
				}
			} else if(UserSession.isEmpty()){
				sendtext("ブラウザ" + BrowserKind.getName() + "のセッション取得に失敗");
				result = "34";
				return false;
			}
			if (useProxy()){
				if (   proxy == null || proxy.isEmpty()
					|| proxy_port < 0 || proxy_port > 65535   ){
					sendtext("プロキシの設定が不正です。");
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
		sendtext("チェック終了");
		return true;
	}

	public synchronized NicoClient getNicoClient() {

		if (isSaveVideo() || isSaveComment() || isSaveOwnerComment()
			|| Setting.isSaveThumbInfo()) {
			sendtext("ログイン中");
			NicoClient client = null;
			boolean is_html5 = Setting.isHtml5();
			if (BrowserKind != BrowserCookieKind.NONE){
				// セッション共有、ログイン済みのNicoClientをclientに返す
				client = new NicoClient(BrowserKind, UserSession, proxy, proxy_port, stopwatch, log, is_html5);
			} else {
				client = new NicoClient(mailAddress, password, proxy, proxy_port, stopwatch, log, is_html5);
			}
			if (!client.isLoggedIn()) {
				sendtext("ログイン失敗 " + BrowserKind.getName() + " " + client.getExtraError());
			} else {
				sendtext("ログイン成功 " + BrowserKind.getName());
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
		sendtext("動画の保存");
		/*動画の保存*/
		try {
			if (isSaveVideo()) {
				if (client == null){
					sendtext("ログインしてないのに動画の保存になりました");
					result = "41";
					return false;
				}
				lowVideoFile = null;
				boolean renameMp4 = Setting.isChangeMp4Ext();
				if(client.isEco()){
					if(Setting.isDisableEco()){
						sendtext("エコノミーモードなので中止します");
						result = "42";
						return false;
					}
				}
				if (isVideoFixFileName()) {
					if (folder.mkdir()) {
						log.println("Folder created: " + folder.getPath());
					}
					if (!folder.isDirectory()) {
						sendtext("動画の保存先フォルダが作成できません。");
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
					File file = Setting.getVideoFile();	//置換前
					//%LOW%以外の置換
					VideoFile = replaceFilenamePattern(file, false, false);	//置換後
					String name = VideoFile.getName();	//置換後
					File dir = VideoFile.getParentFile();
					if(client.isEco()){
						lowVideoFile = replaceFilenamePattern(file, true, false);
						// %LOW%は置換済み
						if(!Path.contains(lowVideoFile, "low_")){
							log.println("MACRO doesn't contain %LOW%. "+lowVideoFile.getPath());
							// ID->IDlow_ , [ID]->[ID]low_
							if(name.contains(lowVideoID)){
								//通常はない
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
						sendtext("エコノミーモードで通常動画は既に存在します");
						log.println("エコノミーモードで通常動画は既に存在します。ダウンロードをスキップします");
						VideoFile = existVideo;
						return true;
					}
					if(client.isEco() && existVideoFile(dmcVideoFile, ".flv", ".mp4")){
						sendtext("エコノミーモードでdmc動画は既に存在します");
						log.println("エコノミーモードでdmc動画は既に存在します。ダウンロードをスキップします");
						dmcVideoFile = existVideo;
						VideoFile = dmcVideoFile;
						return true;
					}
					if(client.isEco() && existVideoFile(lowVideoFile,".flv",".mp4")){
						sendtext("エコノミーモードでエコ動画は既に存在します");
						log.println("エコノミーモードで動画は既に存在します。ダウンロードをスキップします");
						lowVideoFile = existVideo;
						VideoFile = lowVideoFile;
						return true;
					}
				}
				sendtext("動画のダウンロード開始中");
				log.println("serverIsDmc: "+client.serverIsDmc()
					+", preferSmile: "+Setting.isSmilePreferable()
					+", forceDMC:" + Setting.doesDmcforceDl()
					+", client.isEco:"+client.isEco());
				if(!client.serverIsDmc() || Setting.isSmilePreferable() && !Setting.doesDmcforceDl()){
					// 通常サーバ
					if(existVideoFile(VideoFile,".flv",".mp4")){
						sendtext("動画は既に存在します");
						log.println("動画は既に存在します。ダウンロードをスキップします");
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
						sendtext("動画のダウンロードに失敗" + client.getExtraError());
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
						log.println("動画は既に存在します。");
						sendtext("動画は既に存在します");
						VideoFile = existVideo;
						video_size = VideoFile.length();
						log.println("video size: "+(video_size>>20)+"MiB");
					}
					if(existVideoFile(dmcVideoFile, ".flv", ".mp4")){
						log.println("dmc動画は既に存在します。");
						sendtext("dmc動画は既に存在します");
						dmcVideoFile = existVideo;
						dmc_size = dmcVideoFile.length();
						log.println("dmc size: "+(dmc_size>>20)+"MiB");
					} else {
						long min_size = Math.max(video_size, size_high);
						long[] limits = {min_size, 0, 0};	// limits[1] is return value
						if(Setting.doesDmcforceDl())
							limits[0] = 0;	//途中で中止しない
						if(Setting.canSeqResume()){
							if(resumeDmcFile.isFile() && resumeDmcFile.canRead()){
								if(dmcVideoFile.exists())
									dmcVideoFile.delete();
								if(resumeDmcFile.renameTo(dmcVideoFile)){
									log.println("中断したdmc動画をresumeします。");
									sendtext("中断したdmc動画resumeします");
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
										log.println("dmc動画サーバからの(S)ダウンロードに失敗しました。");
										sendtext("dmc動画の(S)ダウンロードに失敗。" + ecode);
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
								// watchページ更新チェック
								if(!client.getVideoInfo(Tag, WatchInfo, Time, Setting.isSaveWatchPage())){
									log.println("dmc(S) watchページエラー");
									sendtext("dmc(S) watchページエラー　"+client.getExtraError());
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										log.printStackTrace(e);
									}
									client.setExtraError("98 dmc(S)エラーリトライ。");
									result = "98";
									if(dmcVideoFile.canRead()){
										if(dmcVideoFile.renameTo(resumeDmcFile))
											log.println("dmcVideo renamed to "+resumeDmcFile);
									}
									for(int l=60; l>0; l--){
										try{
											Thread.sleep(1000);
											sendtext("dmc(S)エラーリトライ待ち "+l+"秒");
											//stopwatch.show();
											if(stopFlagReturn()){
												result = "43";
												break;
											}
										}catch(InterruptedException e){
											break;
										}
									}
									sendtext("98 dmc(S)エラーリトライ　");
									return false;
								}
							} while(resume_size < dmc_size);
							if(dmc_high == 0){
								log.println("dmc(S) getsize失敗!(dmc_high == 0)");
								sendtext("dmc(S) getsize失敗!");
							}else if(video_size == dmc_high){
								log.println("dmc(S) ダウンロード済(video_size == dmc_high)");
								sendtext("dmc(S) ダウンロード済");
								dmcVideoFile = VideoFile;
								dmc_size = video_size;
							}else if(min_size >= dmc_size){
								log.println("dmc(S) ダウンロード中止(min_size >= dmc_size)");
								sendtext("dmc(S) ダウンロード中止");
								dmcVideoFile = null;
								dmc_size = 0;
							}else if(dmc_size == 0){
								log.println("dmc(S) download失敗!(dmc_size == 0)");
								sendtext("dmc(S) download失敗!");
							}else if(resume_size != dmc_size){
								log.println("dmc(S) resume失敗!(resume_size != dmc_size)");
								sendtext("dmc(S) resume失敗!");
								log.println("dmc size: "+(dmc_size>>20)+"MiB, resumed size: "+(resume_size>>20)+"MiB");
								dmc_size = 0;
							}else{
							//	コンテナ変換不要
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
									log.println("dmc動画サーバからのダウンロードに失敗しました。");
									sendtext("dmc動画のダウンロードに失敗" + ecode);
								}
							}
							if(dmcVideoFile!=null && existVideoFile(dmcVideoFile, ".flv", ".mp4")){
								log.println("dmc download "+dmcVideoFile.length()+"bytes");
								videoLength = client.getDmcVideoLength();
								videoContentType = client.getVideoContentType();
								log.println("video ContentType: "+videoContentType);
								dmc_size = dmcVideoFile.length();
								log.println("dmc size: "+(dmc_size>>20)+"MiB");
							//	コンテナ変換不要
							//	if(Setting.isAutoFlvToMp4())
							//		dmcVideoFile = dmcFlvToMp4Convert(dmcVideoFile);
							}
						}
					}
					// video_size , dmc_size should be real size of a file which exists now or has been loaded
					if ( (size_high > video_size && size_high > dmc_size && !Setting.isInhibitSmaller())
						||(size_high != video_size && size_high != dmc_size && Setting.isSmilePreferable())){
						// smile動画をダウンロード
						log.println("Smile download start.");
						if(lowVideoFile==null)
							lowVideoFile = VideoFile;
						VideoFile = client.getVideo(lowVideoFile, Status, StopFlag, renameMp4);
						if (stopFlagReturn()) {
							result = "43";
							return false;
						}
						if (VideoFile == null) {
							log.println("smile動画サーバからのダウンロードに失敗しました。");
							sendtext("smile動画のダウンロードに失敗　" + client.getExtraError());
							video_size = 0;
						}else{
							videoLength = client.getVideoLength();
							videoContentType = client.getVideoContentType();
							log.println("video ContentType: "+videoContentType);
							video_size = VideoFile.length();
						}
					}
					if(dmc_size==0 && video_size==0){
						sendtext("動画のダウンロードに失敗　" + client.getExtraError());
						result = "44";
						return false;
					}
					setVideoTitleIfNull(VideoFile.getName());
					if(dmc_size!=0)
						log.println("dmc size: "+(dmc_size>>20)+"MiB");
					if(video_size!=0)
						log.println("video size: "+(video_size>>20)+"MiB");
					if(dmc_size > video_size){
						log.println("変換にはdmc動画を使います");
						sendtext("変換にはdmc動画を使います");
						VideoFile = dmcVideoFile;
					}
					if (optionalThreadID == null || optionalThreadID.isEmpty()) {
						optionalThreadID = client.getOptionalThreadID();
					}
					resultBuffer.append("video: "+VideoFile.getName()+"\n");
					if(!Setting.isOnlyMp4AutoPlay()||Path.hasExt(VideoFile, ".mp4")){	//mp4拡張子の動画のみ条件あり
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
								sendtext("動画ファイルがフォルダに存在しません。");
								result = "45";
							} else {
								sendtext("動画ファイルが.flvや.mp4でありません：" + OtherVideo);
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
						sendtext("動画ファイルが存在しません。");
						result = "47";
						return false;
					}
					VideoFile = existVideo;
					setVidTitile(tid, Tag, VideoTitle);
				}
			}
			sendtext("動画の保存を終了");
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
		sendtext("コメントの保存");
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
					sendtext("コメントの保存先フォルダが作成できません。");
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
				sendtext("ログインしてないのにコメントの保存になりました");
				result = "51";
				return false;
			}
			if (Setting.isFixCommentNum()) {
				back_comment = client
						.getBackCommentFromLength(back_comment);
			}
			sendtext("コメントのダウンロード開始中");
			// ファイル名設定
			appendCommentFile = mkTemp("_"+tid+TMP_APPEND_EXT);
			// 前処理
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
				sendtext("コメントのダウンロードに失敗 " + client.getExtraError());
				if(backup)
					Path.move(appendCommentFile, CommentFile);
				result = "53";
				return false;
			}
			File commentJson = Path.getReplacedExtFile(CommentFile, "_commentJSON.txt");
			commentJson = client.getCommentJson(commentJson, Status, back_comment, Time, StopFlag);
			if(commentJson!=null){
				sendtext("コメントJSONのダウンロードに成功 " + commentJson.getPath());
			}
			// ファイル内ダブリを整理
			backup = Path.fileCopy(CommentFile,appendCommentFile);
			filelist.add(CommentFile);
			sendtext("コメントファイル整理中");
			if (!CombineXML.combineXML(filelist, CommentFile)){
				sendtext("コメントファイルが整理出来ませんでした");
				if(backup)
					Path.move(appendCommentFile, CommentFile);	// 失敗したらバックアップを戻す
				result = "5A";
				return false;
			}
			//コメントファイルの最初のdate="integer"を探して dateUserFirst にセット
			dateUserFirst = getDateUserFirst(CommentFile);
			sendtext("コメントのダウンロード終了");
			optionalThreadID = client.getOptionalThreadID();
			sendtext("オプショナルスレッドの保存");
			if (optionalThreadID != null && !optionalThreadID.isEmpty() && CommentFile!=null ){
				OptionalThreadFile = Path.getReplacedExtFile(CommentFile, OPTIONAL_EXT);
				backup = false;
				appendOptionalFile = mkTemp(TMP_APPEND_OPTIONAL_EXT);
				// 前処理
				if(OptionalThreadFile.exists()){
					backup = Path.fileCopy(OptionalThreadFile, appendOptionalFile);
				}
				sendtext("オプショナルスレッドのダウンロード開始中");
				target = client.getOptionalThread(
					OptionalThreadFile, Status, optionalThreadID, back_comment, Time, StopFlag,
					Setting.getCommentIndex(),isAppendComment());
				if (stopFlagReturn()) {
					result = "54";
					return false;
				}
				if (target == null) {
					sendtext("オプショナルスレッドのダウンロードに失敗 " + client.getExtraError());
					if(backup)
						Path.move(appendOptionalFile, OptionalThreadFile);
					result = "55";
					return false;
				}
				backup = Path.fileCopy(OptionalThreadFile, appendOptionalFile);
				filelist.clear();
				filelist.add(OptionalThreadFile);
				sendtext("オプショナルスレッド整理中");
				if (!CombineXML.combineXML(filelist, OptionalThreadFile)){
					sendtext("オプショナルスレッドが整理出来ませんでした");
					if(backup)
						Path.move(appendOptionalFile, OptionalThreadFile);
					result = "5B";
					return false;
				}
				if (dateUserFirst.isEmpty()) {
					//ファイルの最初のdate="integer"を探して dateUserFirst にセット
					dateUserFirst = getDateUserFirst(OptionalThreadFile);
				}
				sendtext("オプショナルスレッドの保存終了");
			}
			//ニコスコメント
			nicos_id = client.getNicosID();
			sendtext("ニコスコメントの保存");
			if(nicos_id!=null && !nicos_id.isEmpty() && CommentFile!=null){
				isNicos = true;
				nicosCommentFile = Path.getReplacedExtFile(CommentFile, NICOS_EXT);
				// 前処理
				backup = false;
				File appendNicosFile = mkTemp(TMP_APPEND_NICOS_EXT);
				if(nicosCommentFile.exists()){
					backup = Path.fileCopy(nicosCommentFile, appendNicosFile);
				}
				sendtext("ニコスコメントの保存開始中");
				target = client.getNicosComment(
					nicosCommentFile, Status, nicos_id, back_comment, Time,
						StopFlag, Setting.getCommentIndex(), isAppendComment());
				if (stopFlagReturn()) {
					result = "54";
					return false;
				}
				if (target == null) {
					sendtext("ニコスコメントのダウンロードに失敗 " + client.getExtraError());
					if(backup)
						Path.move(appendNicosFile, nicosCommentFile);
					result = "55";
					return false;
				}
				// ダブリ整理
				backup = Path.fileCopy(nicosCommentFile, appendNicosFile);
				filelist.clear();
				filelist.add(nicosCommentFile);
				sendtext("ニコスコメント整理中");
				if (!CombineXML.combineXML(filelist, nicosCommentFile)){
					sendtext("ニコスコメントが整理出来ませんでした");
					if(backup)
						Path.move(appendNicosFile, nicosCommentFile);
					result = "5B";
					return false;
				}
				sendtext("ニコスコメントの保存終了");
			}
			resultBuffer.append("comment: "+CommentFile.getName()+"\n");
		}
		sendtext("コメントの保存終了");
		return true;
	}

//	private File getOptionalThreadFile(File file) {
//		if (file == null || file.getPath() == null) {
//			return mkTemp(OPTIONAL_EXT);
//		}
//		return Path.getReplacedExtFile(file, OPTIONAL_EXT);
//	}
	private String getDateUserFirst(File comfile){
		//コメントファイルの最初のdate="integer"を探して dateUserFirst にセット
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
		//コメントファイルの最後の<chat thread="..." > の文字列を返す
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
		sendtext("投稿者コメントの保存");
		File folder = Setting.getCommentFixFileNameFolder();
		if (isSaveOwnerComment()) {
			if (isCommentFixFileName()) {
				if (folder.mkdir()) {
					log.println("Folder created: " + folder.getPath());
				}
				if (!folder.isDirectory()) {
					sendtext("投稿者コメントの保存先フォルダが作成できません。");
					result = "60";
					return false;
				}
				OwnerCommentFile = new File(folder, getVideoBaseName() + OWNER_EXT);
			} else {
				OwnerCommentFile = Setting.getOwnerCommentFile();
			}
			sendtext("投稿者コメントのダウンロード開始中");
			if (client == null){
				sendtext("ログインしてないのに投稿者コメントの保存になりました");
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
				sendtext("投稿者コメントのダウンロードに失敗");
				log.println("投稿者コメントのダウンロードに失敗");
				//result = "63";
				return true;
			}
			if (optionalThreadID == null || optionalThreadID.isEmpty()) {
				optionalThreadID = client.getOptionalThreadID();
			}
			if (nicos_id == null || nicos_id.isEmpty())
				nicos_id = client.getNicosID();
		}
		sendtext("投稿者コメントの保存終了");
		return true;
	}

	private boolean saveThumbInfo0(NicoClient client,String vtag) {
		sendtext("動画情報の保存");
		/*ページの保存*/
		String ext = Setting.isSaveThumbInfoAsText()? ".txt":".xml";
		File folder = Setting.getVideoFixFileNameFolder();
		if (isVideoFixFileName()) {
			if (folder.mkdir()) {
				log.println("Folder created: " + folder.getPath());
			}
			if (!folder.isDirectory()) {
				sendtext("動画情報の保存先フォルダが作成できません。");
				result = "A0";
				return false;
			}
			thumbInfoFile = new File(folder, getVideoBaseName() + THUMB_INFO + ext);
		} else {
			thumbInfoFile = getThumbInfoFileFrom(Setting.getVideoFile(), ext);
			thumbInfoFile = replaceFilenamePattern(thumbInfoFile, false, false);
		}
		if(thumbInfoFile==null){
			sendtext("動画情報ファイルがnullです");
			result = "A1";
			return false;
		}
		sendtext("動画情報の保存中");
		if (client == null){
			sendtext("ログインしてないのに動画情報の保存になりました");
			result = "A2";
			return false;
		}
		thumbInfo = client.getThumbInfoFile(vtag);
		if (stopFlagReturn()) {
			result = "A3";
			return false;
		}
		if (thumbInfo == null) {
			sendtext("動画情報の取得に失敗" + client.getExtraError());
			result = "A4";
			return false;
		}
		log.println("reading:" + thumbInfo);
		boolean isOK = true;
		if(!saveThumbUser(thumbInfo, client)){
			sendtext("投稿者情報の取得に失敗");
			log.println("投稿者情報の取得に失敗");
			isOK = false;
		}
		if(!saveThumbnailJpg(thumbInfo, client)){
			sendtext("サムネイル画像の取得に失敗");
			log.println("サムネイル画像の取得に失敗");
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
			sendtext("動画情報の保存終了");
		return isOK;
	}

	private boolean saveThumbInfo(NicoClient client, String vtag) {
		if(!Setting.isSaveThumbInfo())
			return true;
		if(saveThumbInfo0(client, vtag))
			return true;
		// コミュニティ動画はthumbinfoが取れないのでsmIDを使う
		if(alternativeTag.isEmpty())
			alternativeTag = client.getAlternativeTag();
		if(alternativeTag.isEmpty() || alternativeTag.equals(Tag))
			return false;
		return saveThumbInfo0(client, alternativeTag);
	}

	private boolean saveThumbUser(Path infoFile, NicoClient client) {
		if(Setting.isSaveThumbUser()){
			sendtext("投稿者情報の保存");
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
				sendtext("投稿者の情報がありません");
				result = "A5";
				return false;
			}
			log.println("投稿者:"+userID);
			File userFolder = new File(Setting.getUserFolder());
			if (userFolder.mkdirs()){
				log.println("Folder created: " + userFolder.getPath());
			}
			if(!userFolder.isDirectory()){
				sendtext("ユーザーフォルダが作成できません");
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
				if(ownerName == null || ownerName.contains("非公開プロフィール")){
					ownerName = null;
					userThumbFile = client.getUserInfoFile(userID, userFolder);
					if(userThumbFile != null && userThumbFile.canRead()){
						html = Path.readAllText(userThumbFile.getPath(), "UTF-8");
						ownerName = client.getXmlElement(html, "title");
					}
					if(ownerName==null){
						sendtext("投稿者の情報の入手に失敗");
						result = "A7";
						if(ownerName==null || ownerName.isEmpty())
							ownerName = user_nickname;
						if(ownerName==null || ownerName.isEmpty())
							ownerName = "投稿者の情報の入手に失敗";
					//	return false;
					}
				}
				int index = ownerName.lastIndexOf("さんのプロフィール‐");
				if(index > 0){
					ownerName = ownerName.substring(0,index);
				}
				index = ownerName.lastIndexOf("さんのユーザーページ ‐");
				if(index > 0){
					ownerName = ownerName.substring(0,index) + "(ニコレポ非公開)";
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
			sendtext("投稿者情報の保存終了");
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
				sendtext("サムネイル画像の保存先フォルダが作成できません。");
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
			sendtext("サムネイル画像の保存");
			thumbnailJpg = null;
			String infoXml = Path.readAllText(infoFile.getPath(), "UTF-8");
			String url = client.getXmlElement(infoXml, "thumbnail_url");
			if(url==null || url.isEmpty() || !url.startsWith("http")){
				sendtext("サムネイル画像の情報がありません");
				result = "A8";
				return false;
			}
			if(!setThumbnailJpg())
				return false;
			sendtext("サムネイル画像の保存中");
			if (!client.getThumbnailJpg(url+".L", thumbnailJpg)
				&& !client.getThumbnailJpg(url, thumbnailJpg)) {
				sendtext("サムネイル画像の取得に失敗" + client.getExtraError());
				result = "AA";
				return false;
			}
			sendtext("サムネイル画像の保存終了");
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
		sendtext("NGパターン作成中");
		try{
			String all_regex = "/((docomo|iPhone|softbank|device:3DS) (white )?)?.* 18[46]|18[46]( (iPhone|device:3DS))? .*/";
			String def_regex = "/((docomo|iPhone|softbank|device:3DS) (white )?)?18[46]|18[46]( (iPhone|device:3DS))?/";
			String ngWord = Setting.getNG_Word().replaceFirst("^all", all_regex).replace(" all", all_regex);
			ngWord = ngWord.replaceFirst("^default", def_regex).replace(" default", def_regex);
			ngWordPat = NicoXMLReader.makePattern(ngWord, log, enableML);
			ngIDPat = NicoXMLReader.makePattern(Setting.getNG_ID(), log, enableML);
			ngCmd = new CommandReplace(Setting.getNGCommand(), Setting.getReplaceCommand());
		}catch (Exception e) {
			sendtext("NGパターン作成に失敗。おそらく正規表現の間違い？");
			result = "70";
			return false;
		}
		sendtext("NGパターン作成終了");
		return true;
	}

	private Path mkTemp(String uniq){
		return Path.mkTemp(Tag + uniq);
	}

	private boolean convertComment(){
		sendtext("コメントの中間ファイルへの変換中");
		File folder = Setting.getCommentFixFileNameFolder();
		ArrayList<File> filelist = new ArrayList<File>();
		if (isConvertWithComment()) {
			if (Setting.isAddTimeStamp() && isCommentFixFileName() && !isAppendComment()) {
				// 複数のコメントファイル（過去ログ）があるかも
				ArrayList<String> pathlist = detectFilelistFromComment(folder);
				if (pathlist == null || pathlist.isEmpty()){
					sendtext(Tag + ": コメントファイル・過去ログが存在しません。");
					result = "71";
					return false;
				}
				// VideoTitle は見つかった。
				if (pathlist.size() > 0) {			// 0 1.22r3e8, for NP4 comment ver 2009
					for (String path: pathlist){
						filelist.add(new File(folder, path));
					}
					CommentFile = mkTemp(TMP_COMBINED_XML);
					sendtext("コメントファイル結合中");
					log.println("コメントファイル結合中");
					if (!CombineXML.combineXML(filelist, CommentFile)){
						sendtext("コメントファイルが結合出来ませんでした（バグ？）");
						result = "72";
						return false;
					}
					if (dateUserFirst.isEmpty()) {
						//コメントファイルの最初のdate="integer"を探して dateUserFirst にセット
						dateUserFirst = getDateUserFirst(CommentFile);
					}
					listOfCommentFile = filelist;
				} else {
					// コメントファイルはひとつだけ見つかった
					// ここには来ない 1.22r3e8, for NP4 comment ver 2009
				}
			}
			if (!isSaveComment()) {
				if (isCommentFixFileName()) {
					if (!Setting.isAddTimeStamp()){
						// コメントファイルはひとつ
						String commentfilename = detectTitleFromComment(folder);
						if(commentfilename == null){
							sendtext("コメントファイルがフォルダに存在しません。");
							result = "73";
							return false;
						}
						// VideoTitle は見つかった。
						CommentFile = new File(folder, commentfilename);
						if (!CommentFile.canRead()) {
							sendtext("コメントファイルが読み込めません。");
							result = "74";
							return false;
						}
						if (dateUserFirst.isEmpty()) {
							//コメントファイルの最初のdate="integer"を探して dateUserFirst にセット
							dateUserFirst = getDateUserFirst(CommentFile);
						}
					} else {
						// 処理済み
					}
				} else {
					CommentFile = Setting.getCommentFile();
					if (!CommentFile.exists()) {
						sendtext("コメントファイルが存在しません。");
						result = "75";
						return false;
					}
					if (dateUserFirst.isEmpty()) {
						//コメントファイルの最初のdate="integer"を探して dateUserFirst にセット
						dateUserFirst = getDateUserFirst(CommentFile);
					}
				}
			}
			//alternativeVideoID取得
			if(alternativeTag.isEmpty()){
				alternativeTag = getViewCounterVideoTag(CommentFile);
			}
			//combine ファイル内ダブリも削除
			filelist.clear();
			filelist.add(CommentFile);
			CombinedCommentFile = mkTemp(TMP_COMBINED_XML3);
			sendtext("コメントファイルマージ中");
			log.println("コメントファイルマージ中");
			if (!CombineXML.combineXML(filelist, CombinedCommentFile)){
				sendtext("コメントファイルがマージ出来ませんでした");
				result = "72";
				return false;
			}
			CommentMiddleFile = mkTemp(TMP_COMMENT);
			if(!convertToCommentMiddle(CombinedCommentFile, CommentMiddleFile, isNicos)){
				sendtext("コメント変換に失敗");
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
		//コメントファイルの最初の<view_counter id="..." > の文字列を返す
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
		sendtext("オプショナルスレッドの中間ファイルへの変換中");
		log.println(gettext());
		File folder = Setting.getCommentFixFileNameFolder();
		ArrayList<File> filelist = new ArrayList<File>();
		String optext;
		if (isConvertWithComment()) {
			if (isCommentFixFileName()) {
				if (Setting.isAddTimeStamp() && !isAppendComment()) {
					// フォルダ指定時、複数のオプショナルスレッド（過去ログ）があるかも
					optext = OPTIONAL_EXT;
					ArrayList<String> pathlist = detectFilelistFromOptionalThread(folder, optext);
					if (pathlist == null || pathlist.isEmpty()){
						sendtext(Tag + ": オプショナルスレッド・過去ログが存在しません。ニコスコメントでリトライ");
						log.println(gettext());
						// ニコスコメントでリトライ
						optext = NICOS_EXT;
						isOptionalTranslucent = false;
						pathlist = detectFilelistFromOptionalThread(folder, optext);
						if(pathlist == null || pathlist.isEmpty()){
							sendtext(Tag + ": ニコスコメント・過去ログが存在しません。");
							log.println(gettext());
							log.println("No optional thread.");
							OptionalThreadFile = null;
							return true;
						}
					}
					// VideoTitle は見つかった。
					for (String path: pathlist){
						filelist.add(new File(folder, path));
					}
					OptionalThreadFile = mkTemp(TMP_COMBINED_XML2);
					sendtext("オプショナルスレッド結合中");
					log.println(gettext());
					if (!CombineXML.combineXML(filelist, OptionalThreadFile)){
						sendtext("オプショナルスレッドが結合出来ませんでした（バグ？）");
						result = "77";
						//return false;
						OptionalThreadFile = null;
						return true;
					}
					if (dateUserFirst.isEmpty()) {
						//コメントファイルの最初のdate="integer"を探して dateUserFirst にセット
						dateUserFirst = getDateUserFirst(OptionalThreadFile);
					}
					listOfCommentFile.addAll(filelist);
				} else {
					// フォルダ指定時、オプショナルスレッドは１つ
					if(isSaveComment()){
						if(OptionalThreadFile==null){
							OptionalThreadFile = nicosCommentFile;
							if(OptionalThreadFile==null){
								return true;
							}
							isOptionalTranslucent = false;
						}
					}else{
						// フォルダ指定時、オプショナルスレッドを検索
						optext = OPTIONAL_EXT;
						String filename = detectTitleFromOptionalThread(folder, optext);
						if (filename == null || filename.isEmpty()){
							sendtext(Tag + ": オプショナルスレッドがフォルダに存在しません。ニコスコメントでリトライ");
							log.println(gettext());
							// ニコスコメントでリトライ
							optext = NICOS_EXT;
							filename = detectTitleFromOptionalThread(folder, optext);
							if(filename == null || filename.isEmpty()){
								sendtext(Tag + ": ニコスコメントがフォルダに存在しません。");
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
						//コメントファイルの最初のdate="integer"を探して dateUserFirst にセット
						dateUserFirst = getDateUserFirst(OptionalThreadFile);
					}
				}
			} else {
				if(isSaveComment()){
					// ファイル指定の時、オプショナルスレッドは１つ
					if(OptionalThreadFile==null){
						OptionalThreadFile = nicosCommentFile;
						if(OptionalThreadFile==null){
							return true;
						}
						isOptionalTranslucent = false;
					}
				}else{
					// ファイル指定の時検索
					optext = OPTIONAL_EXT;
					OptionalThreadFile = Path.getReplacedExtFile(CommentFile, optext);
					if (!OptionalThreadFile.exists()){
						sendtext("オプショナルスレッドが存在しません。ニコスコメントでリトライ");
						log.println(gettext());
						// ニコスコメントでリトライ
						optext = NICOS_EXT;
						OptionalThreadFile = Path.getReplacedExtFile(CommentFile, optext);
						if(!OptionalThreadFile.exists()){
							sendtext("ニコスコメントが存在しません。");
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
					//コメントファイルの最初のdate="integer"を探して dateUserFirst にセット
					dateUserFirst = getDateUserFirst(OptionalThreadFile);
				}
			}
			//combine ファイル内ダブリも削除
			filelist.clear();
			filelist.add(OptionalThreadFile);
			CombinedOptionalFile = mkTemp(TMP_COMBINED_XML4);
			sendtext("オプショナルスレッドマージ中");
			log.println(gettext());
			if (!CombineXML.combineXML(filelist, CombinedOptionalFile)){
				sendtext("オプショナルスレッドがマージ出来ませんでした");
				result = "77";
			//	return false;
				OptionalMiddleFile = null;
				return true;
			}
			OptionalMiddleFile = mkTemp(TMP_OPTIONALTHREAD);
			if(!convertToCommentMiddle(CombinedOptionalFile, OptionalMiddleFile, isNicos)){
				sendtext("オプショナルスレッド変換に失敗");
				log.println(gettext());
				OptionalMiddleFile = null;
				result = "78";
				//	return false;
				OptionalMiddleFile = null;
				return true;
			}
			//コメント数を検査
			if(!OptionalMiddleFile.canRead()){
				OptionalMiddleFile = null;
				// But OK!
			}
		}
		return true;
	}

	private boolean convertOwnerComment(){
		sendtext("投稿者コメントの中間ファイルへの変換中");
		File folder = Setting.getCommentFixFileNameFolder();
		if (isConvertWithOwnerComment()){
			if (!isSaveOwnerComment()) {
				if (isCommentFixFileName()) {
					String ownerfilename = detectTitleFromOwnerComment(folder);
					if(ownerfilename == null){
						sendtext("投稿者コメントファイルがフォルダに存在しません。");
					//	retValue = "80";
					//	return false;
						log.println("投稿者コメントファイルがフォルダに存在しません。");
						OwnerCommentFile = null;
						return true;
					}
					// VideoTitle は見つかった。
					OwnerCommentFile = new File(folder, ownerfilename);
					if (!OwnerCommentFile.canRead()) {
						sendtext("投稿者コメントファイルが読み込めません。");
						result = "81";
						return false;
					}
				} else {
					OwnerCommentFile = Setting.getOwnerCommentFile();
					if (!OwnerCommentFile.exists()) {
						sendtext("投稿者コメントファイルが存在しません。");
					//	retValue = "82";
					//	return false;
						log.println("投稿者コメントファイルが存在しません。");
						OwnerCommentFile = null;
						return true;
					}
				}
			}
			//alternativeVideoID取得
			if(alternativeTag.isEmpty()){
				alternativeTag = getViewCounterVideoTag(OwnerCommentFile);
			}
			OwnerMiddleFile = mkTemp(TMP_OWNERCOMMENT);
			//ここで commentReplaceが作られる
			log.println("投稿者コメント変換");
			if (!convertToCommentMiddle(OwnerCommentFile, OwnerMiddleFile, isNicos)){
				sendtext("投稿者コメント変換に失敗");
				OwnerMiddleFile = null;
				result = "83";
				return false;
			}
			//コメント数を検査
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
		// ニコスコメントは premium "2" or "3"みたいなのでニコスコメントの時は運営コメント変換しないようにする
		boolean live_op = Setting.isLiveOperationConversion() && !is_nicos;
		if(is_nicos)
			isOptionalTranslucent = false;
		if(!ConvertToVideoHook.convert(
				commentfile, middlefile, CommentReplaceList,
				ngIDPat, ngWordPat, ngCmd, Setting.getScoreLimit(),
				live_op, Setting.isPremiumColorCheck(), duration, log, isDebugNet)){
			return false;
		}
		//コメント数が0の時削除する
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
		sendtext("動画の変換を開始");
		video_vhext = Path.mkTemp(Tag+"[log]vhext.txt");
		stopwatch.start();
		if(!VideoFile.canRead()){
			sendtext("動画が読み込めません");
			result = "90";
			return false;
		}
		/*ビデオ名の確定*/
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
				sendtext("変換後の保存先フォルダが作成できません。");
				result = "92";
				return false;
			}
			String conv_name = VideoTitle;
			if (conv_name == null){
				conv_name = "null";
			}
			if (!Setting.isNotAddVideoID_Conv()||conv_name.isEmpty()) {//付加するなら
				conv_name = Setting.isChangeTitleId()?
						VideoTitle + VideoID : VideoID + VideoTitle;
			}
			if (conv_name.isEmpty()) {
				sendtext("変換後のタイトルがありません(ビデオファイル名が確定できません)。");
				result = "93";
				return false;
			}

			if (Setting.isAddOption_ConvVideoFile()){
				byte[] dirName = new File(folder, conv_name)
					.getAbsolutePath().getBytes("Shift_JIS");
				// フォルダ名が長すぎる場合
				if (dirName.length > (255-12)){
					conv_name = VideoID;
				}
				conv_name = conv_name.trim();	// In Windows API, cant make dir as " ABC" nor "ABC "
				folder = new File(folder, conv_name);
				if (folder.mkdir()) {
					log.println("Created folder: " + folder.getPath());
				}
				if (!folder.isDirectory()) {
					sendtext("動画(FFmpeg設定名)ファイルの保存先フォルダが作成できません。");
					result = "94";
					return false;
				}
				conv_name = MainOption + InOption + OutOption;
				if (!getFFmpegVfOption().isEmpty()){
					conv_name = vfilter_flag + " " + getFFmpegVfOption() + conv_name;
				}
				conv_name = getFFmpegOptionName() + safeAsciiFileName(conv_name);
				dirName = new File(folder, conv_name).getAbsolutePath().getBytes("Shift_JIS");
				// ファイル名が長すぎる場合
				if (dirName.length > (255 - 3)){
					int len = conv_name.length() - (dirName.length - (255 - 3));
					if (len < 1){
						sendtext("作成するビデオファイル名が長すぎます。");
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
			sendtext("変換後のファイル名が変換前と同じです");
			result = "96";
			return false;
		}
		if(ConvertedVideoFile.isFile() && ConvertedVideoFile.canRead()){
			sendtext("変換後のファイルは既に存在します");
			log.println("変換後のファイルは既に存在します");
			String otherFilename = "1"+ ConvertedVideoFile.getName();
			if(ConvertedVideoFile.renameTo(new File(ConvertedVideoFile.getParentFile(),otherFilename))){
				sendtext("同名のファイルをリネームしました");
				log.println("同名のファイルをリネームしました"+otherFilename);
			}else{
				sendtext("同名のファイルをリネーム出来ませんでした。上書きします");
				log.println("同名のファイルをリネーム出来ませんでした。上書きします");
			}
		}
		int code = converting_video();
		//stopwatch.stop();
		//vhext(nicovideoログ)をコピーする
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
						log.println(video_vhext.getPath()+" が有りません.");
				}
			}catch(Exception e){
				log.println(video_vhext.getPath()+" に書けません.");
			}
		}
		if (code == 0) {
			sendtext("変換が正常に終了しました。");
			log.println(lastFrame);
			return true;
		} else if (code == CODE_CONVERTING_ABORTED) { /*中断*/
			result = "97";
		} else {
			if(errorLog==null||errorLog.isEmpty())
				if(ffmpeg!=null)
					errorLog = ffmpeg.getLastError().toString();
			sendtext("変換エラー：(" + code + ") "+ getLastError());
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
	 *  %ID% -> Tag, %id% -> [Tag](VideoIDと同じ) %TITLE% -> VideoTitle,
	 *  %CAT% -> もしあればカテゴリータグ, %TAG1% ->２番めの動画タグ
	 *  %TAGn% (n=2,3,...10) n+1番目のタグ
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
			VideoTitle.replace("　", " ").replaceAll(" +", " ").trim()
			.replace("．", ".");
		String lowString = economy? "low_":(dmc?"dmc_":"");
		String surfix = videoFilename.contains("%LOW%")? "":lowString;
		videoFilename =
			videoFilename.replace("%ID%", Tag+surfix) 	// %ID% -> 動画ID
			.replace("%id%", VideoID+surfix)	// %id% -> [動画ID]
			.replace("%LOW%", lowString)	// %LOW% -> economy時 low_
			.replace("%TITLE%",VideoTitle)	// %TITLE% -> 動画タイトル
			.replace("%title%", canonical)	// %title% -> 動画タイトル（空白大文字を空白小文字に）
			.replace("%CAT%", nicoCategory)		// %CAT% -> もしあればカテゴリータグ
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
				log.println("フォルダが作成できません:"+parent.getPath());
				log.println("置換失敗 "+videoFilename);
				target = file;
			}
		}
		return target;
	}

	private static String safeAsciiFileName(String str) {
		return NicoClient.toSafeWindowsName(str, "MS932");
	}

	private boolean canRetry(NicoClient client, Gate gate){

		//ゲート制限超えないならリトライ可能
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
			//  サービスが一時的に過負荷 ゲートウェイタイムアウト
			// retry count check
			sendtext("リトライ待ち中");
			TimerTask task = new Tick(Status, "リトライ待ち　");
			Timer timer = new Timer("リトライ秒間隔タイマー");
			timer.schedule(task, 0, 1000);	// 1000 miliseconds
			if(gate.notExceedLimiterGate()){
				// can retry
				client.setExtraError("retry,");
				timer.cancel();
				return true;
			}
			timer.cancel();
			sendtext("リトライ失敗");
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
						sendtext("URL/IDの指定がありません " + client.getExtraError());
					}else if(!client.loginCheck()){
						sendtext("ログイン失敗 " + BrowserKind.getName() + " " + client.getExtraError());
					}else{
						sendtext(Tag + "の情報の取得に失敗 " + client.getExtraError());
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
				sendtext(Tag + "の情報の取得に成功");
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
					log.println("追加情報の取得に失敗しましたが続行します。");
				else {
					String tstr = gettext();
					if(isSaveComment()) {
						tstr = "コメント取得成功　" + tstr;
					}
					if(isSaveVideo()) {
						tstr = "動画取得成功　" + tstr;
					}
					tstr = "[警告]" + tstr;
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
			log.println("変換前時間　" + before);
			stopwatch.setTrailer("、変換前 "+before);

			gate.exit(result);
			//manager.sendTimeInfo();
			if (!isSaveConverted()) {
				sendtext("動画・コメントを保存し、変換は行いませんでした。");
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

			//ローカル時のthumbInfoDataセット
			if(thumbInfoData==null){
				String ext = Setting.isSaveThumbInfoAsText()? ".txt":".xml";
				if(thumbInfoFile==null)
					thumbInfoFile = Path.getReplacedExtFile(VideoFile, ext);
				if(thumbInfoFile!=null  && thumbInfoFile.equals(CommentFile) && thumbInfoFile.canRead())
					thumbInfoData = Path.readAllText(thumbInfoFile, "UTF-8");
				if(thumbInfoData!=null
				  && !thumbInfoData.contains("status=\"ok\"")){
					// 生放送ではなくthumbinfoロード失敗またはcommentfileだったら
					// 動画のメタデータを入れておく
					thumbInfoData=null;
				}
			}

			//stopwatch.show();
			if (convertVideo()) {
				// 変換成功
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
				errorControl.setError(result,url,"中止しました");
			else if(result.equals("98")){
				StringBuffer sb = new StringBuffer(Tag+"\tリトライ\t"+WatchInfo);
				if(parent!=null){
					parent.myListGetterDone(sb, log);
				}else{
					errorControl.setError(result,url,"サスペンド\t"+resumeDmcFile);
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
			log.println("変換時間　" + stopwatch.formatLatency());
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
//				// batch file 実行
//				CmdExec cmdexec = new CmdExec(bat,ConvertedVideoFile.getAbsolutePath());
//				cmdexec.start();
//			}else if (exe.exists()){
//				// exe file 実行
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
	 * CWSならFWSに変換する<br/>
	 * その後、アスペクト比を判定しVhookを選択、オプションを読み込み設定する
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
				sendtext("SWFのFWS変換に失敗しました");
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
						str = "≠" + str;
					}
					isPlayerWide = true;
				} else {
					if (isPlayerWide){
						str = "≠" + str;
					}
					isPlayerWide = false;
				}
			}
		}
		String auto = "";
		if (way==3){
			auto = "共通";
		}
		if (way==2) {
			auto = "自動選択 ";
		}
		if (Setting.isZqPlayer()){
			selectedVhook = VhookQ;
		} else if (isPlayerWide){
			selectedVhook = VhookWide;
		} else {
			selectedVhook = VhookNormal;
		}
		if (!detectOption(isPlayerWide,Setting.isZqPlayer())){
			sendtext("変換オプションファイルの読み込みに失敗しました。");
			return false;
		}
		if(!addAdditionalOption(isPlayerWide,Setting.isZqPlayer())){
			sendtext("追加オプションの設定に失敗しました。");
			return false;
		}

		//replaceチェック
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
			MovieInfo.setText(auto + "拡張Vhook Q " + str);
		} else if (isPlayerWide){
			MovieInfo.setText(auto + "拡張Vhook ワイド " + str);
		} else {
			MovieInfo.setText(auto + "拡張Vhook 従来 " + str);
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
					// ow / oh < w / h -> oh を変更
					outh = toMod4(outw / video_aspect);
				}else if(out_aspect > video_aspect){
					// ow / oh > w / h -> ow を変更
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
		int commentWidth = 640;		//原宿
		int commentHeight = 384;	//原宿
		if(Setting.isZqPlayer()){
			commentWidth = 800;		//Qwatch大画面
			commentHeight = 480;
		}
		aspect = toAspect(sizestr, aspect);
		int width = aspect.getWidth();
		int height = aspect.getHeight();
		log.println("Output Video Area " + width + ":" + height);
		//width heightは出力動画の大きさ(outs指定時はそのサイズ)
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
		//width heightは出力コメントの大きさ（動画をはみ出さない）
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
//		仕様変更 MainOpt InOptは置き換えない
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
			// -itsoffset削除 実行済み
			inputOptionMap.remove("-itsoffset");
			mainOptionMap.remove("-itsoffset");
			// -ss削除  実行済み
			inputOptionMap.remove("-ss");
			mainOptionMap.remove("-ss");
			// outの -ss はそのまま残す
			out_option_ss = outputOptionMap.get("-ss");
			if(out_option_ss!=null){
				// 出力の-itsoffsetは -ssによる
				inputOptionMap.put("-itsoffset", out_option_ss);
			}
			// -t はそのまま残して良い
		}
		return code;
	}

	private int convSWF_JPG(File videoin, File videoout){
		int code = -1;
		//出力
		//ffmpeg.setCmd("-y -analyzeduration 10M -i ");
		ffmpeg.setCmd("-y -i ");	//swfの場合、解析時間はもとのままにする
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
		// JPG切替速度指定する?
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
		// tl==0(情報なし) または tlは最小長
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
		 * 音声を合成
		 * ffmpeg.exe -shortest -y -i fws_tmp.swf -itsoffset 1.0 -i avi4.avi
		 *  -vcodec libxvid -acodec libmp3lame -ab 128k -ar 44100 -ac 2 fwsmp4.avi
		 */
		//ffmpeg.setCmd("-y -analyzeduration 10M -i ");
		ffmpeg.setCmd("-y -i ");	//swfの場合、解析時間はもとのままにする
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
			//サムネイル選択,検索
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
			log.println("サムネイルが読めません："+thumbfile.getPath());
			sendtext("サムネイルが読めません");
			thumbfile = new Path(".\\bin\\b32.jpg");
		}else{
			// サムネイルをテンポラリーにコピー（javaは読めるのになぜかffmpegが読めないので）
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
			//どうしても読めない場合
			log.println("サムネイルが読めません："+thumbfile.getPath());
			sendtext("代替サムネイルが読めません");
			errorLog = "代替サムネイルが読めません";
			code = 198;
			return code;
		}
		code = convFLV_thumbaudio(thumbfile, input, output);
		return code;
	}

	private int convFLV_thumbaudio(File thumbin, File audioin, File videoout){
		int code = -1;
		// サムネイルのアスペクト比は無視
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
			//通常のFLV
			// fps up check
			if(checkFps && frameRate < fpsMin){
				//FPS変換必要
				if(Setting.isUseFpsFilter()){
					//FPS Filter選択
					log.println("FPS filter");
					String vfoptsave = getFFmpegVfOption();
					String vfopt = "fps=fps="+fpsUp
						+ ",scale="+outAspect.getSize();	// -s オプションを -vf scale=w:h として先に追加
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
						//fpsfilter変換成功
						return code;
					}
					log.println("("+code+")fps filterに失敗 ");
					errorLog += "\nfps filterに失敗 "+ getLastError();
					log.println("続行\n");	//続行モード
				}

				// 2パスFPS変換
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
					log.println("("+code+")fps変換に失敗 ");
					errorLog += "\nfps変換に失敗 "+ getLastError();
					if(Setting.canSoundOnly()){
						log.println("コメントと音声だけを合成します");
						infoStack.pushText("SoundOnly");
						code = convFLV_audio(input, ConvertedVideoFile, Setting.getDefaultThumbnail());
						infoStack.popText();
					}
					return code;
				}
				if (code == 0){
					//fps変換成功
					input = outputFps;
				}
			}

			//FPS変換なし
			/*
			 * ffmpeg.exe -y mainoption inoption -i infile outoptiont [vhookOption] outfile
			 */
			log.println("FLV 従来通り");
			String vfoptsave = getFFmpegVfOption();
			if(checkFps && Setting.isUseFpsFilter()){
				String vfopt = "";
				String ropt = getRopt();
				if(ropt != null && !ropt.isEmpty()){
					vfopt = "fps=fps="+ropt
						+ ",scale="+outAspect.getSize();
					// -s オプションも -vf scale=w:h として先に追加
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
			// nm動画 FWS入力
			if(!Setting.isSwfTo3Path()){
				// nm対応しない
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
						log.println("("+code+")fps変換に失敗 ");
						errorLog += "\nfps変換に失敗 "+ getLastError();
						if(Setting.canSoundOnly()){
							log.println("コメントと音声だけを合成します");
							infoStack.pushText("SoundOnly");
							code = convFLV_audio(input, ConvertedVideoFile, Setting.getDefaultThumbnail());
							infoStack.popText();
						}
						return code;
					}else{
						//fps変換成功
						input = outputFps;
					}
				}

				/*
				 * ffmpeg.exe -y mainoption inoption -i infile outoptiont [vhookOption] outfile
				 */
				log.println("FWS 従来通り");
				infoStack.pushText("FWS");
				code = convFLV(input, ConvertedVideoFile);
				infoStack.popText();
				return code;
			} else {
				log.println("FWS 3path");
				// try 3 path
				/*
				 * SWFファイルをJPEG形式に合成
				 * ffmpeg.exe -y -i fws_tmp.swf -an -vcodec copy -f image2 %03d.jpg
				 */
				//出力先を作る
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
						// jpegに変換できない場合は音声のみにする
						code = convFLV_audio(input, ConvertedVideoFile);
					}
					return code;
				}
				/*
				 * video lengthが0の時には音声を先に抽出する。
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
				 * JPEGファイルをMP4形式に合成
				 * ffmpeg.exe -r 1/4 -y -i %03d.jpg -an -vcodec huffyuv -f avi huffjpg.avi
				 */
				//出力
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
						// jpegがmp4に変換できない場合は音声のみにする
						code = convFLV_audio(input, ConvertedVideoFile);
					}
					return code;
				}
				/*
				 * 音声を合成
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
				 * コメントを合成
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
		// ffmpeg1に設定された文字列を取り出すiterator
		StringBuilder sb = new StringBuilder();
		String s = "";
		it.next();	//最初は読み飛ばす
		while(it.hasNext()){
			s = it.next();
			s = s.replaceAll(VFILTER_FLAG, VFILTER_FLAG2);
			if(s.equals(VFILTER_FLAG2)){
				sb.append(" "+vfilter_flag+" ");
				s = it.next();
				// sは　-vf のパラメータ, ここでは""で囲まれているはず(saccubusの仕様)
				s = unquote(s);
				sb.append("\"");
				String s2 = "";	// vhext=の後ろに移動する文字列
				// vhext=はsの最後についている(saccubusの仕様)
				// @の位置にvhextの位置を変更
				int index = s.indexOf("@=");
				if(index >= 0){
					// @=があった
					sb.append(s.substring(0, index));
					s = s.substring(index+2); // @=読み飛ばし
					index = s.indexOf(",");
					if(index < 0)
						return false;
					if(index > 0)
						vhspeedrate = s.substring(0, index);	// @=のパラメータ
					s = s.substring(index+1);
					index = s.indexOf("vhext=");
					if(index < 0)
						return false;
					if(index > 0)
						s2 = s.substring(0, index-1);
				}else{
					index = s.indexOf("@,");
					if(index >= 0){
						// @,があった
						sb.append(s.substring(0, index));
						s = s.substring(index+2);	// @,読み飛ばし
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
				// vhextより前のvfは追加済み
				sb.append("vhext=");
				index += "vhext=".length();
				s = s.substring(index);
				if(vhspeedrate!=null && !vhspeedrate.isEmpty())
					s = s.replace(END_OF_ARGUMENT,
						"|--vfspeedrate:"+vhspeedrate+END_OF_ARGUMENT);
				s = vf_quote(s);	// vhext= のオプションは video filter用に quoteする
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
					// vfoptなし
					// -s オプションを -vf scale=w:h として先に追加
					ffmpeg.addCmd("scale="+outAspect.getSize());
					ffmpeg.addCmd(",");
				}else{
					if(vfopt.contains("scale=")){
						// vfoptにscaleがある場合は変更しない。
						ffmpeg.addCmd(vfopt);
						ffmpeg.addCmd(",");
					}else{
						// vfoptにscaleなし
						int index = vfopt.indexOf("@");
						if(index >= 0){
							// vfoptに@あり
							if(index > 0){
								// @より前は,を含んでいる
								ffmpeg.addCmd(vfopt.substring(0, index));
							}
							// -s オプションを -vf scale=w:h として先に追加
							ffmpeg.addCmd("scale="+outAspect.getSize());
							ffmpeg.addCmd(",");
							ffmpeg.addCmd(vfopt.substring(index));
							// @より後ろの最後には,を含んでいない
							ffmpeg.addCmd(",");
						}else{
							// vfoptに@なし
							ffmpeg.addCmd(vfopt);
							ffmpeg.addCmd(",");
							// -s オプションを -vf scale=w:h として先に追加
							ffmpeg.addCmd("scale="+outAspect.getSize());
							ffmpeg.addCmd(",");
						}
					}
				}
			}else{
				// outAspectが不正な場合
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
				//枠色指定：特殊　=から半角スペースまでを引数とする
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
			// ローカル変換で
			if(Pattern.matches("sm[0-8]|(sm[0-8]_)?lv.*", Tag))		// Tagが sm9より小さい場合 lvを含む場合
				isLive = true;
			if(Tag.length() <= 2 || !Character.isDigit(Tag.charAt(2)))	// 3文字目が数字ではない場合
				isLive = true;
			if(Pattern.matches("[a-zA-Z][0-9].*", Tag))	// 英字1文字+数字の場合
				isLive = true;
		}
		else if(!MainFrame.idcheck(Tag))	// IDではない文字が設定された場合(エラーになる?)
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
			sendtext("中止しました。");
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
			//前のキーワード処理
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
				// ""スキップ中
				if(start < end_quote)
					continue;
				processing_quote = false;
				end_quote = 0;
			}
			if(c=='"'){
				//quoteの最後まで実行してスキップさせる
				begin_quote = start;
				end_quote = option.indexOf("\" ", begin_quote+1);
				if(end_quote<0){
					end_quote = option.length();	//文字列の終わりまで
				}
				w = option.substring(begin_quote, end_quote);
				if(w.charAt(w.length()-1)!='"'){
					w += "\"";	//parameterは""を含む
				}
				processing_quote = true;
			}
			switch(flag){
			case 0:
				if(c!='-'){
					log.print("警告　キーワードではありません:"+w);
					non_keyword += w + " ";
					continue;
				}
				keyword = w;
				parameter = "";
				flag = 1;
				continue;
			case 1:
				if(c=='-'){
					//前のキーワードチェック
					if(isSingleKeyword(keyword)){
						//前のキーワード処理
						optionMap.put(keyword, "");
						keyword = w;
						parameter = "";
						flag = 1;
						continue;
					}
					log.print("警告　'-'使っています:"+w);
				}
				parameter = w + " ";
				flag = 2;
				continue;
			case 2:
				if(c=='-'){
					//次のキーワード?
					//前のパラメータ処理
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
			log.println("バグってる");
		}
		//parameterが残っていたら登録(出力ファイル?)
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
		ffmpegOptionName = "直接入力";
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
		//オプションに拡張子を含んでしまった場合にも対応☆
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
				// 新しいkeyは OuOptionの先頭に追加
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
			// 過去ログ時刻を削除
			String regex = "\\[" + WayBackDate.STR_FMT_REGEX + "\\]";
			videoTitle = videoTitle.replaceAll(regex, "");
		//	int index = videoTitle.lastIndexOf("[");
		//		//過去ログは[YYYY/MM/DD_HH:MM:SS]が最後に付く
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
	 * 	投稿者コメントに関するファイル名タグと拡張子を扱う
	 * 　　	①Ver.1.25以降のさきゅばす ②に以下を追加
	 * 			オプショナルスレッド＝ VideoID + VideoTitile + "{Optional}.xml"
	 * 			↑の過去ログ＝ VideoID + VideoTitile + "[YYYY／MM／DD_HH：mm：ss]{Optional}.xml"
	 * 		②今回のさきゅばす
	 * 			ユーザコメント = VideoID + VideoTitle + ".xml"
	 * 			過去ログ       = VideoID + VideoTitle + "[YYYY／MM／DD_HH：mm：ss].xml"
	 * 			投稿者コメント = VideoID + VideoTitle + "[Owner].xml"
	 * 		③ NicoBrowser拡張1.4.4の場合
	 * 			ユーザコメント = VideiID + VideoTitle + ".xml"
	 * 			投稿者コメント = VideoID + VideoTitle + ".txml"
	 * 		④NNDDなど
	 * 			ユーザコメント = VideoTitle + VideoID + ".xml"
	 * 			投稿者コメント = VideoTitle + VideoID + "[Owner].xml"
	 * 		④NicoPlayerなど
	 * 			ユーザコメント = VideoTitle + "(" + Tag + ")" + ".xml"
	 * 			過去ログ       = VideoTitie + "(" + Tag + ")[" + YYYY年MM月DD日HH時MM分SS秒 + "}.xml"
	 * 			投稿者コメント = VideoTitle + "(" + Tag + "){Owner].xml"
	 *
	 *
	 */

	/*
	 * videoIDの位置は無関係に削除
	 * 拡張子があればその前まで
	 */
	private String getTitleFromPath(String path, String videoID, String Tag){
		if (path.contains(videoID)){
			path = path.replace(videoID, "");	// videoIDの位置は無関係に削除
		} else if(path.contains(Tag)){
			path = path.replace(Tag, "");
			if(path.startsWith("_")){
				path = path.substring(1);
			}
		}
		// 拡張子があればその前まで
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
