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
	private ConvertingSetting Setting;
	private String Tag;
	private String VideoID;
	private String VideoTitle;
	private String VideoBaseName = "";
	private String Time;
	private JLabel Status;
	private final JLabel MovieInfo;
	private final ConvertStopFlag StopFlag;
	private static final String TMP_COMMENT = "_vhook.tmp";
	private static final String TMP_OWNERCOMMENT = "_vhookowner.tmp";
	private static final String TMP_OPTIONALTHREAD = "_vhookoptional.tmp";
//	private static final String VIDEO_URL_PARSER = "http://www.nicovideo.jp/watch/";
	public static final String OWNER_EXT = "[Owner].xml";	// 投稿者コメントサフィックス
	public static final String OPTIONAL_EXT = "{Optional}.xml";	// オプショナルスレッドサフィックス
	public static final String TMP_APPEND_EXT = "_all_comment.xml";
	public static final String TMP_APPEND_OPTIONAL_EXT = "_all_optional.xml";
	private static final String TMP_COMBINED_XML = "_tmp_comment.xml";
	private static final String TMP_COMBINED_XML2 = "_tmp_optional.xml";
	private static final String TMP_COMBINED_XML3 = "_tmp_comment2.xml";
	private static final String TMP_COMBINED_XML4 = "_tmp_optiona2.xml";
	private static final String THUMB_INFO = "_thumb_info";
	private String OtherVideo;
	private final String WatchInfo;
	private InfoStack infoStack;
	private BrowserCookieKind BrowserKind = BrowserCookieKind.NONE;
	private final BrowserInfo browserInfo = new BrowserInfo();
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
	private HistoryDeque<File> playList;
	private ArrayList<CommentReplace> CommentReplaceList = new ArrayList<CommentReplace>();
	private File imgDir;
	private Aspect outAspect;
	private VPlayer vplayer = null;
	private String alternativeVideoID = "";
	private final ConvertManager manager;
	private Gate gate;
	private StringBuffer errorList;
	private String lowVideoID;
	private int tid;

	public ConvertWorker(int worker_id,
			String url, String time, ConvertingSetting setting,
			JLabel[] jLabels, ConvertStopFlag flag,	MainFrame frame,
			HistoryDeque<File> play_list, ConvertManager conv, StringBuffer sb) {
		url = url.trim();
		//watchvideo = !url.startsWith("http");
		int index = 0;
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
		VideoID = "[" + Tag + "]";
		lowVideoID = VideoID + "low_";
		DefaultVideoIDFilter = new VideoIDFilter(VideoID);
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
		manager = conv;
		playList = play_list;
		parent = frame;
		sbRet = sb;
		errorList = Setting.getErrorList();
		tid = worker_id;
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
	private File lowVideoFile;
	private boolean isConverting = false;

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

	private void sendtext(final String text){
		publish(text);
	}
	protected void process(List<String> chunk){
		while(!chunk.isEmpty())
			Status.setText(chunk.remove(0));
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
					System.out.println("CA用フォント" + saveGulimFont.getPath() + "を" + gulimFont.getName() + "で代替します。");
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
					System.out.println("CA用フォント" + georgiaFont.getPath() + "を" + gothicFont.getName() + "で代替します。");
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
					System.out.println("CA用フォント" + devabagariFont.getPath() + "を" + arialFont.getName() + "で代替します。");
					devabagariFont = arialFont;
				}
				tahomaFont = new File(fontDir, "tahoma.ttf");
				if (!tahomaFont.canRead()) {
					sendtext("警告　CA用フォントが見つかりません。" + tahomaFont.getPath());
					//retValue = "16";
					//return false;
					System.out.println("CA用フォント" + tahomaFont.getPath() + "を" + arialFont.getName() + "で代替します。");
					tahomaFont = arialFont;
				}
				mingliuFont = new File(fontDir, "mingliu.ttc");
				if (!mingliuFont.canRead()) {
					sendtext("警告　CA用フォントが見つかりません。" + mingliuFont.getPath());
					//retValue = "17";
					//return false;
					System.out.println("CA用フォント" + mingliuFont.getPath() + "を" + simsunFont.getName() + "で代替します。");
					mingliuFont = simsunFont;
				}
				newMinchoFont = new File(fontDir, "SIMSUN.TTC");	//NGULIM.TTFが無かった
				if (!newMinchoFont.canRead()) {
					sendtext("警告　CA用フォントが見つかりません。" + newMinchoFont.getPath());
					//retValue = "18";
					//return false;
					System.out.println("CA用フォント" + newMinchoFont.getPath() + "を" + simsunFont.getName() + "で代替します。");
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
					System.out.println("CA用フォント" + estrangeloEdessaFont.getPath() + "を" + arialFont.getName() + "で代替します。");
					estrangeloEdessaFont = arialFont;
				}
				arialUnicodeFont = new File(fontDir, "arialuni.ttf");
				if (!arialUnicodeFont.canRead()) {
					sendtext("警告　CA用フォントが見つかりません。" + arialUnicodeFont.getPath());
					//retValue = "20";
					//return false;
					System.out.println("CA用フォント" + arialUnicodeFont.getPath() + "を" + arialFont.getName() + "で代替します。");
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
					System.out.println("CA用フォント" + gujaratiFont.getPath() + "を" + arialUnicodeFont.getName() + "で代替します。");
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
					System.out.println("CA用フォント" + bengalFont.getPath() + "を" + arialUnicodeFont.getName() + "で代替します。");
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
					System.out.println("CA用フォント" + tamilFont.getPath() + "を" + arialUnicodeFont.getName() + "で代替します。");
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
					System.out.println("CA用フォント" + laooFont.getPath() + "を" + arialUnicodeFont.getName() + "で代替します。");
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
					System.out.println("CA用フォント" + gurmukhiFont.getPath() + "を" + arialUnicodeFont.getName() + "で代替します。");
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
					System.out.println("CA用フォント" + kannadaFont.getPath() + "を" + arialUnicodeFont.getName() + "で代替します。");
					kannadaFont = arialUnicodeFont;
				}
				thaanaFont = new File(fontDir, "mvboli.ttf");
				if (!thaanaFont.canRead()) {
					sendtext("警告　CA用フォントが見つかりません。" + thaanaFont.getPath());
					//retValue = "27";
					//return false;
					System.out.println("CA用フォント" + thaanaFont.getPath() + "を" + arialUnicodeFont.getName() + "で代替します。");
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
					System.out.println("CA用フォント" + malayalamFont.getPath() + "を" + arialUnicodeFont.getName() + "で代替します。");
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
					System.out.println("CA用フォント" + teluguFont.getPath() + "を" + arialUnicodeFont.getName() + "で代替します。");
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
				proxy = Setting.getProxy();
				proxy_port = Setting.getProxyPort();
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
		resultBuffer = Setting.getReturnBuffer();
		sendtext("チェック終了");
		return true;
	}

	public synchronized NicoClient getNicoClient() {
		if (isSaveVideo() || isSaveComment() || isSaveOwnerComment()
			|| Setting.isSaveThumbInfo()) {
			sendtext("ログイン中");
			NicoClient client = null;
			if (BrowserKind != BrowserCookieKind.NONE){
				// セッション共有、ログイン済みのNicoClientをclientに返す
				client = new NicoClient(BrowserKind, UserSession, proxy, proxy_port, stopwatch);
			} else {
				client = new NicoClient(mailAddress, password, proxy, proxy_port, stopwatch);
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
		File folder = Setting.getVideoFixFileNameFolder();
		sendtext("動画の保存");
		/*動画の保存*/
		try {
			if (isSaveVideo()) {
				if (isVideoFixFileName()) {
					if (folder.mkdir()) {
						System.out.println("Folder created: " + folder.getPath());
					}
					if (!folder.isDirectory()) {
						sendtext("動画の保存先フォルダが作成できません。");
						result = "40";
						return false;
					}
					lowVideoFile = null;
					if(client.isEco()){
						if(Setting.isDisableEco()){
							sendtext("エコノミーモードなので中止します");
							result = "42";
							return false;
						}else{
							lowVideoFile = new File(folder, lowVideoID+VideoTitle+".flv");
						}
					}
					VideoFile = new File(folder, getVideoBaseName() + ".flv");
				} else {
					VideoFile = Setting.getVideoFile();
				}
				if(VideoFile.isFile() && VideoFile.canRead()){
					sendtext("動画は既に存在します");
					System.out.println("動画は既に存在します。ダウンロードをスキップします");
				}else{
					sendtext("動画のダウンロード開始中");
					if (client == null){
						sendtext("ログインしてないのに動画の保存になりました");
						result = "41";
						return false;
					}
					if(lowVideoFile==null){
						lowVideoFile = VideoFile;
					}
					if(client.isEco() && lowVideoFile.isFile() && lowVideoFile.canRead()){
						sendtext("エコノミーモードでエコ動画は既に存在します");
						System.out.println("エコ動画は既に存在します。ダウンロードをスキップします");
						VideoFile = lowVideoFile;
					}else{
						VideoFile = client.getVideo(lowVideoFile, Status, StopFlag,
								isVideoFixFileName() && Setting.isChangeMp4Ext());
							if (stopFlagReturn()) {
								result = "43";
								return false;
							}
					}
					if (VideoFile == null) {
						sendtext("動画のダウンロードに失敗" + client.getExtraError());
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
								sendtext("動画ファイルがフォルダに存在しません。");
								result = "45";
							} else {
								sendtext("動画ファイルが.flvでありません：" + OtherVideo);
								result = "46";
							}
							return false;
						}
						VideoFile = new File(folder, videoFilename);
						if (!VideoFile.canRead()) {
							sendtext("動画ファイルが読み込めません。");
							result = "47";
							return false;
						}
					} else {
						VideoFile = Setting.getVideoFile();
						if (!VideoFile.exists()) {
							sendtext("動画ファイルが存在しません。");
							result = "48";
							return false;
						}
					}
					setVideoTitleIfNull(VideoFile.getName());
				}
			}
			sendtext("動画の保存を終了");
		}catch(NullPointerException e){
			sendtext("(´∀｀)＜ぬるぽ\nガッ\n");
			e.printStackTrace();
		}
		return true;
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
					System.out.println("Folder created: " + folder.getPath());
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
			if(isAppendComment()){
				// ファイル名設定
				appendCommentFile = mkTemp(TMP_APPEND_EXT);
				// 前処理
				if(CommentFile.exists()){
					backup = Path.fileCopy(CommentFile,appendCommentFile);
				}
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
			if(isAppendComment()){
				// ファイル内ダブリを整理
				filelist.add(CommentFile);
				sendtext("コメントファイル整理中");
				if (!CombineXML.combineXML(filelist, CommentFile)){
					sendtext("コメントファイルが整理出来ませんでした");
					result = "5A";
					return false;
				}
			}
			//コメントファイルの最初のdate="integer"を探して dateUserFirst にセット
			dateUserFirst = getDateUserFirst(CommentFile);
			sendtext("コメントのダウンロード終了");
			optionalThreadID = client.getOptionalThreadID();
			sendtext("オプショナルスレッドの保存");
			if (optionalThreadID != null && !optionalThreadID.isEmpty() ){
				if (isCommentFixFileName()) {
					OptionalThreadFile = new File(folder, getVideoBaseName() + prefix + OPTIONAL_EXT);
				} else {
					OptionalThreadFile = getOptionalThreadFile(Setting.getCommentFile());
				}
				backup = false;
				if(isAppendComment()){
					appendOptionalFile = mkTemp(TMP_APPEND_OPTIONAL_EXT);
					// 前処理
					if(OptionalThreadFile.exists()){
						backup = Path.fileCopy(OptionalThreadFile, appendOptionalFile);
					}
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
				if(isAppendComment()){
					filelist.clear();
					filelist.add(OptionalThreadFile);
					sendtext("オプショナルスレッド整理中");
					if (!CombineXML.combineXML(filelist, OptionalThreadFile)){
						sendtext("オプショナルスレッドが整理出来ませんでした");
						result = "5B";
						return false;
					}
				}
				if (dateUserFirst.isEmpty()) {
					//ファイルの最初のdate="integer"を探して dateUserFirst にセット
					dateUserFirst = getDateUserFirst(OptionalThreadFile);
				}
				sendtext("オプショナルスレッドの保存終了");
			}
			resultBuffer.append("comment: "+CommentFile.getName()+"\n");
		}
		sendtext("コメントの保存終了");
		return true;
	}

	private File getOptionalThreadFile(File file) {
		if (file == null || file.getPath() == null) {
			return mkTemp(OPTIONAL_EXT);
		}
		return getReplacedExtFile(file, OPTIONAL_EXT);
	}
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
					System.out.println("Folder created: " + folder.getPath());
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
				System.out.println("投稿者コメントのダウンロードに失敗");
				//result = "63";
				return true;
			}
			if (optionalThreadID == null || optionalThreadID.isEmpty()) {
				optionalThreadID = client.getOptionalThreadID();
			}
		}
		sendtext("投稿者コメントの保存終了");
		return true;
	}

	private boolean saveThumbInfo0(NicoClient client) {
		sendtext("動画情報の保存");
		/*ページの保存*/
		String ext = Setting.isSaveThumbInfoAsText()? ".txt":".xml";
		File folder = Setting.getVideoFixFileNameFolder();
		if (isVideoFixFileName()) {
			if (folder.mkdir()) {
				System.out.println("Folder created: " + folder.getPath());
			}
			if (!folder.isDirectory()) {
				sendtext("動画情報の保存先フォルダが作成できません。");
				result = "A0";
				return false;
			}
			thumbInfoFile = new File(folder, getVideoBaseName() + ext);
		} else {
			thumbInfoFile = getThumbInfoFileFrom(Setting.getVideoFile(), ext);
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
		thumbInfo = client.getThumbInfoFile(Tag);
		if (stopFlagReturn()) {
			result = "A3";
			return false;
		}
		if (thumbInfo == null) {
			sendtext("動画情報の取得に失敗" + client.getExtraError());
			result = "A4";
			return false;
		}
		System.out.println("reading:" + thumbInfo);
		if(!saveThumbUser(thumbInfo, client)){
			System.out.println("投稿者情報の取得に失敗");
			return false;
		}
		if(!saveThumbnailJpg(thumbInfo, client)){
			System.out.println("サムネイル画像の取得に失敗");
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
		sendtext("動画情報の保存終了");
		return true;
	}

	private boolean saveThumbInfo(NicoClient client) {
		if(Setting.isSaveThumbInfo())
			return saveThumbInfo0(client);
		else
			return true;
	}

	private boolean saveThumbUser(Path infoFile, NicoClient client) {
		sendtext("投稿者情報の保存");
		Path userThumbFile = null;
		if(Setting.isSaveThumbUser()){
			String infoXml = Path.readAllText(infoFile.getPath(), "UTF-8");
			String userID = NicoClient.getXmlElement(infoXml, "user_id");
			if(userID==null || userID.isEmpty() || userID.equals("none")){
				sendtext("投稿者の情報がありません");
				result = "A5";
				return false;
			}
			System.out.println("投稿者:"+userID);
			File userFolder = new File(Setting.getUserFolder());
			if (userFolder.mkdirs()){
				System.out.println("Folder created: " + userFolder.getPath());
			}
			if(!userFolder.isDirectory()){
				sendtext("ユーザーフォルダが作成できません");
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
			if(ownerName == null || ownerName.contains("非公開プロフィール")){
				ownerName = null;
				userThumbFile = client.getUserInfoFile(userID, userFolder);
				if(userThumbFile != null && userThumbFile.canRead()){
					html = Path.readAllText(userThumbFile.getPath(), "UTF-8");
					ownerName = NicoClient.getXmlElement(html, "title");
				}
				if(ownerName==null){
					sendtext("投稿者の情報の入手に失敗");
					result = "A7";
					return false;
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
		sendtext("投稿者情報の保存終了");
		return true;
	}

	private boolean setThumbnailJpg() {
		if (isVideoFixFileName()) {
			File folder = Setting.getVideoFixFileNameFolder();
			if (folder.mkdir()) {
				System.out.println("Folder created: " + folder.getPath());
			}
			if (!folder.isDirectory()) {
				sendtext("サムネイル画像の保存先フォルダが作成できません。");
				result = "A9";
				return false;
			}
			thumbnailJpg = new File(folder, getVideoBaseName() + ".jpg");
		} else {
			File file = Setting.getVideoFile();
			if (file == null || !file.isFile() || file.getPath() == null) {
				thumbnailJpg = mkTemp(Tag + "_thumnail.jpg");
			}else{
				thumbnailJpg = getReplacedExtFile(file, ".jpg");
			}
		}
		return true;
	}

	private boolean saveThumbnailJpg(Path infoFile, NicoClient client) {
		sendtext("サムネイル画像の保存");
		thumbnailJpg = null;
		if(Setting.isSaveThumbnailJpg()){
			String infoXml = Path.readAllText(infoFile.getPath(), "UTF-8");
			String url = NicoClient.getXmlElement(infoXml, "thumbnail_url");
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
		}
		sendtext("サムネイル画像の保存終了");
		return true;
	}

	private File getThumbInfoFileFrom(File file, String ext) {
		if (file == null || file.getPath() == null) {
			return mkTemp(THUMB_INFO + ext);
		}
		return getReplacedExtFile(file, THUMB_INFO + ext);
	}

	private boolean makeNGPattern() {
		sendtext("NGパターン作成中");
		try{
			String all_regex = "/((docomo|iPhone|softbank|device:3DS) (white )?)?.* 18[46]|18[46]( (iPhone|device:3DS))? .*/";
			String def_regex = "/((docomo|iPhone|softbank|device:3DS) (white )?)?18[46]|18[46]( (iPhone|device:3DS))?/";
			String ngWord = Setting.getNG_Word().replaceFirst("^all", all_regex).replace(" all", all_regex);
			ngWord = ngWord.replaceFirst("^default", def_regex).replace(" default", def_regex);
			ngWordPat = NicoXMLReader.makePattern(ngWord);
			ngIDPat = NicoXMLReader.makePattern(Setting.getNG_ID());
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

	private String getRemovedExtName(String path) {
		int index = path.lastIndexOf(".");
		if (index > path.lastIndexOf(File.separator)) {
			path = path.substring(0, index);		// 拡張子を削除
		}
		return path;
	}

	private String getReplacedExtName(String path, String ext) {
		return getRemovedExtName(path) + ext;
	}

	private File getReplacedExtFile(File file, String ext){
		return new File(getReplacedExtName(file.getPath(),ext));
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
			if(alternativeVideoID.isEmpty()){
				alternativeVideoID = getViewCounterID(CommentFile);
			}
			//combine ファイル内ダブリも削除
			filelist.clear();
			filelist.add(CommentFile);
			CombinedCommentFile = mkTemp(TMP_COMBINED_XML3);
			sendtext("コメントファイルマージ中");
			if (!CombineXML.combineXML(filelist, CombinedCommentFile)){
				sendtext("コメントファイルがマージ出来ませんでした");
				result = "72";
				return false;
			}
			CommentMiddleFile = mkTemp(TMP_COMMENT);
			if(!convertToCommentMiddle(CombinedCommentFile, CommentMiddleFile)){
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

	private String getViewCounterID(File comfile) {
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
				return "[" + ret + "]";
		}
		return ret;
	}

	private boolean convertOprionalThread(){
		sendtext("オプショナルスレッドの中間ファイルへの変換中");
		File folder = Setting.getCommentFixFileNameFolder();
		ArrayList<File> filelist = new ArrayList<File>();
		if (isConvertWithComment()) {
			if (isCommentFixFileName()) {
				if (Setting.isAddTimeStamp() && !isAppendComment()) {
					// フォルダ指定時、複数のオプショナルスレッド（過去ログ）があるかも
					ArrayList<String> pathlist = detectFilelistFromOptionalThread(folder);
					if (pathlist == null || pathlist.isEmpty()){
						sendtext(Tag + ": オプショナルスレッド・過去ログが存在しません。");
						System.out.println("No optional thread.");
						OptionalThreadFile = null;
						return true;
					}
					// VideoTitle は見つかった。
					for (String path: pathlist){
						filelist.add(new File(folder, path));
					}
					OptionalThreadFile = mkTemp(TMP_COMBINED_XML2);
					sendtext("オプショナルスレッド結合中");
					if (!CombineXML.combineXML(filelist, OptionalThreadFile)){
						sendtext("オプショナルスレッドが結合出来ませんでした（バグ？）");
						result = "77";
						return false;
					}
					if (dateUserFirst.isEmpty()) {
						//コメントファイルの最初のdate="integer"を探して dateUserFirst にセット
						dateUserFirst = getDateUserFirst(OptionalThreadFile);
					}
					listOfCommentFile.addAll(filelist);
				} else {
					// フォルダ指定時、オプショナルスレッドは１つ
					String filename = detectTitleFromOptionalThread(folder);
					if (filename == null || filename.isEmpty()){
						sendtext(Tag + ": オプショナルスレッドがフォルダに存在しません。");
						System.out.println("No optional thread.");
						OptionalThreadFile = null;
						return true;
					}
					OptionalThreadFile = new File(folder, filename);
					if (dateUserFirst.isEmpty()) {
						//コメントファイルの最初のdate="integer"を探して dateUserFirst にセット
						dateUserFirst = getDateUserFirst(OptionalThreadFile);
					}
				}
			} else {
				// ファイル指定の時
				OptionalThreadFile = getOptionalThreadFile(Setting.getCommentFile());
				if (!OptionalThreadFile.exists()){
					sendtext("オプショナルスレッドが存在しません。");
					System.out.println("No optional thread.");
					OptionalThreadFile = null;
					return true;
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
			if (!CombineXML.combineXML(filelist, CombinedOptionalFile)){
				sendtext("オプショナルスレッドがマージ出来ませんでした");
				result = "77";
				return false;
			}
			OptionalMiddleFile = mkTemp(TMP_OPTIONALTHREAD);
			if(!convertToCommentMiddle(CombinedOptionalFile, OptionalMiddleFile)){
				sendtext("オプショナルスレッド変換に失敗");
				OptionalMiddleFile = null;
				result = "78";
				return false;
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
						System.out.println("投稿者コメントファイルがフォルダに存在しません。");
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
						System.out.println("投稿者コメントファイルが存在しません。");
						OwnerCommentFile = null;
						return true;
					}
				}
			}
			//alternativeVideoID取得
			if(alternativeVideoID.isEmpty()){
				alternativeVideoID = getViewCounterID(OwnerCommentFile);
			}
			OwnerMiddleFile = mkTemp(TMP_OWNERCOMMENT);
			//ここで commentReplaceが作られる
			if (!convertToCommentMiddle(OwnerCommentFile, OwnerMiddleFile)){
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
		//コメント数が0の時削除する
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
		sendtext("動画の変換を開始");
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
				System.out.println("Created folder: " + folder.getPath());
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
					System.out.println("Created folder: " + folder.getPath());
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
			sendtext("変換後のファイル名が変換前と同じです");
			result = "96";
			return false;
		}
		if(ConvertedVideoFile.isFile() && ConvertedVideoFile.canRead()){
			sendtext("変換後のファイルは既に存在します");
			System.out.println("変換後のファイルは既に存在します");
			String otherFilename = "1"+ ConvertedVideoFile.getName();
			if(ConvertedVideoFile.renameTo(new File(ConvertedVideoFile.getParentFile(),otherFilename))){
				sendtext("同名のファイルをリネームしました");
				System.out.println("同名のファイルをリネームしました"+otherFilename);
			}else{
				sendtext("同名のファイルをリネーム出来ませんでした。上書きします");
				System.out.println("同名のファイルをリネーム出来ませんでした。上書きします");
			}
		}
		int code = converting_video();
		stopwatch.stop();
		//vhext(nicovideoログ)をコピーする
		File log_vhext = new File(".","[log]vhext.txt");
		File video_vhext = Path.mkTemp(Tag+"[log]vhext.txt");
		if(video_vhext.exists()){
			if(log_vhext.delete()){
			}
			Path.fileCopy(video_vhext, log_vhext);
		}else{
			System.out.println(Tag+"[log]vhext.txt が有りません.");
		}
		if (code == 0) {
			sendtext("変換が正常に終了しました。");
			System.out.println(lastFrame);
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

	private static String safeAsciiFileName(String str) {
		//Windowsファイルシステムで扱えるAscii文字列に
		str = str.replace('/', '_')
			.replace('\\', '_')
			.replace('?', '_')
			.replace('*', '_')
			.replace(':', ';')		//	:(colon) to ;(semi colon)
			.replace('|', '_')
			.replace('\"', '\'')
			.replace('<', '(')
			.replace('>', ')')
//			.replace('.', '．')		// .(dot) is let there
			.replaceAll(" +", " ")
			.trim();
		return str;
	}

	private boolean canRetry(NicoClient client, Gate gate){
		//ゲート制限超えないならリトライ可能
		String ecode;
		if(client==null) return false;
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
			if(gate.notExceedLimiterGate()){
				// can retry
				client.setExtraError("retry,");
				return true;
			}
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
		System.out.println("LastStatus:[FF]Converter cancelled.");
		if(sbRet!=null){
			sbRet.append("RESULT=[FF]\n");
		}
		if(parent!=null){
			parent.setErrorUrl(errorList);
			errorList.append(Tag+WatchInfo+"\n");
		}
	}

	@Override
	protected String doInBackground() throws Exception {
		synchronized (StopFlag) {
			while(StopFlag.isPending()){
				StopFlag.wait();
			}
			if(stopFlagReturn()){
				abortByCancel();
				manager.reqDone(result, StopFlag);
				return "FF";
			}
			StopFlag.start();
		}
	/*
		if(!watchvideo){
			//not watch video get try mylist
			sendtext("バグ URL振り分け失敗");
			System.out.println("バグ URL振り分け失敗");
			result = "-1";
			return result;
		}
	*/
		gate = Gate.open(tid);
		stopwatch.clear();
		stopwatch.start();
		manager.sendTimeInfo();
		try {
			if(parent!= null){
				Setting = parent.getSetting();
			}
			if (!checkOK()) {
				return result;
			}
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
				VideoTitle = client.getVideoTitle();
				VideoBaseName = Setting.isChangeTitleId()?
					VideoTitle + VideoID : VideoID + VideoTitle;
				sendtext(Tag + "の情報の取得に成功");
			}

			stopwatch.show();
			success = false;
			do{
				success = saveVideo(client);
			}while (!stopFlagReturn() && !success && canRetry(client, gate));
			if(!success) return result;

			stopwatch.show();
			success = false;
			do{
				success = saveComment(client);
			}while (!stopFlagReturn() && !success && canRetry(client, gate));
			if(!success) return result;

			stopwatch.show();
			success = false;
			do{
				success = saveOwnerComment(client);
			}while (!stopFlagReturn() && !success && canRetry(client, gate));
			if(!success) return result;

			stopwatch.show();
			if(!saveThumbInfo(client)){
				if(isSaveConverted())
					System.out.println("追加情報の取得に失敗しましたが続行します。");
				else {
					String tstr = Status.getText();
					if(isSaveComment()) {
						tstr = "コメント取得成功、" + tstr;
					}
					if(isSaveVideo())
						tstr = "[警告]動画取得成功、" + tstr;
					else
						tstr = "[警告]" + tstr;
					sendtext(tstr);
					return result;
				}
			}
			if(stopFlagReturn()){
				return result;
			}

			stopwatch.show();
			System.out.println("変換前時間　" + stopwatch.formatElapsedTime());

			gate.exit(result);
			manager.sendTimeInfo();
			if (!isSaveConverted()) {
				sendtext("動画・コメントを保存し、変換は行いませんでした。");
				result = "0";
				return result;
			}

			if(!isConverting){
				manager.incNumConvert();
				isConverting = true;
			}
			stopwatch.show();
			if(!makeNGPattern() || stopFlagReturn()){
				return result;
			}

			stopwatch.show();
			if (!convertOwnerComment() || stopFlagReturn()){
				return result;
			}

			stopwatch.show();
			if (!convertComment() || stopFlagReturn()) {
				return result;
			}

			stopwatch.show();
			if (!convertOprionalThread() || stopFlagReturn()) {
				return result;
			}

			stopwatch.show();
			if (convertVideo()) {
				// 変換成功
				result = "0";
				playList.offer(ConvertedVideoFile);
				if (isDeleteCommentAfterConverting())
					deleteCommentFile();
				if (isDeleteVideoAfterConverting())
					deleteFile(VideoFile);
				deleteFile(CommentMiddleFile);
				deleteFile(OwnerMiddleFile);
				deleteFile(OptionalMiddleFile);
				deleteFile(CombinedCommentFile);
				deleteFile(CombinedOptionalFile);
				if(parent==null && Setting.isAutoPlay()
				 ||parent!=null && parent.getSetting().isAutoPlay()){
					playConvertedVideo();
				}
			}
			return result;
		} catch (IOException ex) {
			ex.printStackTrace();
			if("0".equals(result)) result = "EX";
		} finally {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					StopFlag.finish();
					StopFlag.setButtonEnabled(false);
				}
			});

			if(isConverting){
				manager.decNumConvert();
				isConverting = false;
			}
			manager.reqDone(result, StopFlag);
			stopwatch.show();
			stopwatch.stop();
			gate.exit(result);
			manager.sendTimeInfo();
			System.out.println("変換時間　" + stopwatch.formatLatency());
			System.out.println("LastStatus:[" + result + "]" + Status.getText());
			System.out.println("VideoInfo: " + MovieInfo.getText());
			System.out.println("LastFrame: "+ lastFrame);
			//end alarm
			File wav = new File("end.wav");
			if(wav.exists()){
				if(!AudioPlay.playWav(wav)){
					sendtext("wav error");
				};
			}
			if(sbRet!=null){
				sbRet.append("RESULT=" + result + "\n");
				if(!dateUserFirst.isEmpty()){
					sbRet.append("DATEUF=" + dateUserFirst + "\n");
				}
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
		if("FF".equals(result)){
			return;
		}
		String retStr = null;
		try {
			retStr = get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		if(retStr == null)
			System.out.println("ConvertWorker#done.ret==null. ConvertWorker might had Exception!");
		else {
			System.out.println("["+retStr+"]Converter.done! "+Tag);
			if(parent!=null){
				if(!retStr.equals("0")){
					errorList.append(Tag+WatchInfo+"\n");
					parent.setErrorUrl(errorList);
				}else{
					parent.setPlayList();
				}
			}
		}
	}

	// 変換動画再生
	public void playConvertedVideo() {
		try {
			File convertedVideo = playList.next();
			if(convertedVideo==null){
				convertedVideo = ConvertedVideoFile;
			}
			if(convertedVideo==null){
				sendtext("変換後の動画がありません");
				return;
			}
			if(!convertedVideo.canRead()){
				sendtext("変換後の動画が読めません：" + convertedVideo.getName());
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
	private void deleteList(ArrayList<File> list){
		if (list== null || list.isEmpty())
			return;
		System.out.print("Deleted: ");
		for (File file : list){
			if(file.delete())
				System.out.print(file.getName()+" ");
		}
		System.out.println("done.");
	}
	private void deleteFile(File file){
		if (file != null && file.canWrite()
		 && file.delete())
			System.out.println("Deleted: " + file.getPath());
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
			e.printStackTrace();
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
		VideofileInfo info = new VideofileInfo(video, ffmpeg, Status, StopFlag, stopwatch);
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
			MovieInfo.setText(auto + "拡張Vhook Q " + str);
		} else if (isPlayerWide){
			selectedVhook = VhookWide;
			MovieInfo.setText(auto + "拡張Vhook ワイド " + str);
		} else {
			selectedVhook = VhookNormal;
			MovieInfo.setText(auto + "拡張Vhook 従来 " + str);
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
				if(((ac = getAudioCodecKV(optmap))!=null) && (ac[1].contains("aac"))){
					replaceOption(optmap,ac[0],"copy");
					System.out.println("Changed: "+ac[0]+" "+ac[1]+" -> copy");
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
				if(((ac = getAudioCodecKV(optmap))!=null) && (ac[1].contains("aac")) && !ac[1].contains("he")){
					replaceOption(optmap,ac[0],"copy");
					System.out.println("Changed: "+ac[0]+" "+ac[1]+" -> copy");
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
			System.out.println(" framerate="+ropt);
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
		System.out.println("Output Video Area " + width + ":" + height);
		//width heightは出力動画の大きさ(outs指定時はそのサイズ)
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
		ffmpeg.addCmd(" -metadata");
		ffmpeg.addCmd(" \"title="+VideoTitle+"\"");
		ffmpeg.addCmd(" -metadata");
		ffmpeg.addCmd(" \"comment="+alternativeVideoID+"\"");
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
		code = ffmpeg.exec(Status, CODE_CONVERTING_ABORTED, StopFlag, stopwatch);
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
		// tl==0(情報なし) または tlは最小長
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
		 * 音声を合成
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
			System.out.println("サムネイルが読めません："+thumbfile.getPath());
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
					stopwatch.show();
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
			//どうしても読めない場合
			System.out.println("サムネイルが読めません："+thumbfile.getPath());
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
		if(alternativeVideoID.isEmpty()){
			alternativeVideoID = VideoID;
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
					System.out.println("FPS filter");
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
					System.out.println("("+code+")fps filterに失敗 ");
					errorLog += "\nfps filterに失敗 "+ getLastError();
					System.out.println("続行\n");	//続行モード
				}

				// 2パスFPS変換
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
					System.out.println("("+code+")fps変換に失敗 ");
					errorLog += "\nfps変換に失敗 "+ getLastError();
					if(Setting.canSoundOnly()){
						System.out.println("コメントと音声だけを合成します");
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
			System.out.println("FLV 従来通り");
			String vfoptsave = getFFmpegVfOption();
			if(checkFps && Setting.isUseFpsFilter()){
				String vfopt = "";
				String ropt = getRopt();
				if(ropt != null && !ropt.isEmpty()){
					vfopt = "fps=fps="+ropt
						+ ",scale="+outAspect.getSize();
					// -s オプションも -vf scale=w:h として先に追加
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
			// nm動画 FWS入力
			if(!Setting.isSwfTo3Path()){
				// nm対応しない
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
						System.out.println("("+code+")fps変換に失敗 ");
						errorLog += "\nfps変換に失敗 "+ getLastError();
						if(Setting.canSoundOnly()){
							System.out.println("コメントと音声だけを合成します");
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
				System.out.println("FWS 従来通り");
				infoStack.pushText("FWS");
				code = convFLV(input, ConvertedVideoFile);
				infoStack.popText();
				return code;
			} else {
				System.out.println("FWS 3path");
				// try 3 path
				/*
				 * SWFファイルをJPEG形式に合成
				 * ffmpeg.exe -y -i fws_tmp.swf -an -vcodec copy -f image2 %03d.jpg
				 */
				//出力先を作る
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
						// jpegに変換できない場合は音声のみにする
						code = convFLV_audio(input, ConvertedVideoFile);
					}
					return code;
				}
				/*
				 * JPEGファイルをMP4形式に合成
				 * ffmpeg.exe -r 1/4 -y -i %03d.jpg -an -vcodec huffyuv -f avi huffjpg.avi
				 */
				//出力
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
				 * コメントを合成
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
				int index = s.indexOf("vhext=");
				if(index > 0){
					index += "vhext=".length();
					sb.append(s.substring(0, index));
					s = s.substring(index);
					s = vf_quote(s);	// vhext= のオプションは video filter用に quoteする
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
				// -s オプションを -vf scale=w:h として先に追加
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
					System.out.print("警告　キーワードではありません:"+w);
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
					System.out.print("警告　'-'使っています:"+w);
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
			System.out.println("バグってる");
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
				ex.printStackTrace();
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
			// 過去ログ時刻を削除
			String regex = "\\[" + WayBackDate.STR_FMT_REGEX + "\\]";
			videoTitle = videoTitle.replaceAll(regex, "");
		//	int index = videoTitle.lastIndexOf("[");
		//		//過去ログは[YYYY/MM/DD_HH:MM:SS]が最後に付く
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
	private String getTitleFromPath(String path, String videoID){
		if (path.indexOf(videoID) >= 0){
			path = path.replace(videoID, "");	// videoIDの位置は無関係に削除
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

}
