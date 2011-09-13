package saccubus;

import javax.swing.JLabel;

import saccubus.net.BrowserInfo;
import saccubus.net.BrowserInfo.BrowserCookieKind;
import saccubus.net.NicoClient;
import saccubus.net.Path;

import java.io.*;

import saccubus.conv.CombineXML;
import saccubus.conv.ConvertToVideoHook;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Properties;

import saccubus.util.Cws2Fws;
import saccubus.util.Stopwatch;
import saccubus.ConvertingSetting;
import saccubus.FFmpeg.Aspect;

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
 */
public class Converter extends Thread {
	private final ConvertingSetting Setting;
	private String Tag;
	private String VideoID;
	private String VideoTitle;
	private String Time;
	private JLabel Status;
	private final ConvertStopFlag StopFlag;
	private static final String TMP_COMMENT = "_vhook.tmp";
	private static final String TMP_OWNERCOMMENT = "_vhookowner.tmp";
	private static final String TMP_OPTIONALTHREAD = "_vhookoptional.tmp";
	private static final String VIDEO_URL_PARSER = "http://www.nicovideo.jp/watch/";
	public static final String OWNER_EXT = "[Owner].xml";	// 投稿者コメントサフィックス
	public static final String OPTIONAL_EXT = "{Optional}.xml";	// オプショナルスレッドサフィックス
	private static final String TMP_COMBINED_XML = "_tmp_comment.xml";
	private static final String TMP_COMBINED_XML2 = "_tmp_optional.xml";
	private String OtherVideo;
	private final String WatchInfo;
	private final JLabel MovieInfo;
	private BrowserCookieKind BrowserKind = BrowserCookieKind.NONE;
	private final BrowserInfo BrowserInfo = new BrowserInfo();
	private String UserSession = "";	//ブラウザから取得したユーザーセッション
	private final Stopwatch Stopwatch;
	private File selectedVhook;
	private Aspect videoAspect;
	private File fwsFile = null;
	private VideoIDFilter DefaultVideoIDFilter;

	public Converter(String url, String time, ConvertingSetting setting,
			JLabel status, ConvertStopFlag flag, JLabel movieInfo, JLabel watch) {
		url = url.trim();
		if(url.startsWith(VIDEO_URL_PARSER)){
			url = url.substring(VIDEO_URL_PARSER.length());
		}
		int index = url.indexOf('?');
		if(index >= 0){
			Tag = url.substring(0,index);
			WatchInfo = url.substring(index);
		}else{
			Tag = url;
			WatchInfo = "";
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
	private int wayOfVhook = 0;
	private ArrayList<File> listOfCommentFile = new ArrayList<File>();
	private String optionalThreadID = "";	// set in
	private String errorLog = "";
	private int videoLength = 0;

	public File getVideoFile() {
		return VideoFile;
	}
	public ConvertingSetting getSetting(){
		return Setting;
	}
	public String getErrorLog() {
		return errorLog;
	}

	private void sendtext(String text){
		Status.setText(text);
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
		return Setting.isSaveOwnerComment();
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
	private String getProxy(){
		return Setting.getProxy();
	}
	private int getProxyPort(){
		return Setting.getProxyPort();
	}
	private String getMailAddress(){
		return Setting.getMailAddress();
	}
	private String getPassword(){
		return Setting.getPassword();
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

	private boolean checkOK() {
		sendtext("チェックしています");
		if (!isSaveConverted() && !isSaveVideo()
			&& !isSaveComment() && !isSaveOwnerComment()){
			sendtext("何もすることがありません");
			return false;
		}
		if (isSaveConverted()) {
			File a = new File(Setting.getFFmpegPath());
			if (!a.canRead()) {
				sendtext("FFmpegが見つかりません。");
				return false;
			}
			this.ffmpeg = new FFmpeg(Setting.getFFmpegPath());
			if (Setting.isUseVhookNormal()){
				if(Setting.getVhookPath().indexOf(' ') >= 0) {
					sendtext("すいません。現在vhookライブラリには半角空白は使えません。");
					return false;
				}
				VhookNormal = new File(Setting.getVhookPath());
				if (!VhookNormal.canRead()) {
					sendtext("Vhookライブラリが見つかりません。");
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
						return false;
					}
					VhookWide = new File(Setting.getVhookWidePath());
				}
				if (!VhookWide.canRead()) {
					sendtext("Vhookライブラリ（ワイド）が見つかりません。");
					return false;
				}
				wayOfVhook++;
			}
			if (wayOfVhook == 0){
				sendtext("使用できるVhookライブラリがありません。");
				return false;
			}
			a = new File(Setting.getFontPath());
			if (!a.canRead()) {
				sendtext("フォントが見つかりません。");
				return false;
			}
		} else {
			if (isDeleteVideoAfterConverting()) {
				sendtext("変換しないのに、動画削除しちゃって良いんですか？");
				return false;
			}
			if (isDeleteCommentAfterConverting()) {
				sendtext("変換しないのに、コメント削除しちゃって良いんですか？");
				return false;
			}
		}
		if (isSaveVideo() ||
				isSaveComment() || isSaveOwnerComment()) {
			// ブラウザセッション共有の場合はここでセッションを読み込む
			if (Setting.isBrowserIE()){
				BrowserKind = BrowserCookieKind.MSIE;
				UserSession = BrowserInfo.getUserSession(BrowserKind);
			}
			if (UserSession.isEmpty() && Setting.isBrowserFF()){
				BrowserKind = BrowserCookieKind.Firefox;
				UserSession = BrowserInfo.getUserSession(BrowserKind);
			}
			if (UserSession.isEmpty() && Setting.isBrowserChrome()){
				BrowserKind = BrowserCookieKind.Chrome;
				UserSession = BrowserInfo.getUserSession(BrowserKind);
			}
			if (UserSession.isEmpty() && Setting.isBrowserChromium()){
				BrowserKind = BrowserCookieKind.Chromium;
				UserSession = BrowserInfo.getUserSession(BrowserKind);
			}
			if (UserSession.isEmpty() && Setting.isBrowserOpera()){
				BrowserKind = BrowserCookieKind.Opera;
				UserSession = BrowserInfo.getUserSession(BrowserKind);
			}
			if (UserSession.isEmpty() && Setting.isBrowserOther()){
				BrowserKind = BrowserCookieKind.Other;
				UserSession = BrowserInfo.getUserSessionOther(Setting.getBrowserCookiePath());
			}
			if (BrowserKind != BrowserCookieKind.NONE && UserSession.isEmpty()){
				sendtext("ブラウザ　" + BrowserKind.toString()
						+ "　のセッション取得に失敗");
				return false;
			}
			if (BrowserKind == BrowserCookieKind.NONE
				&& (getMailAddress() == null
					|| getMailAddress().isEmpty()
					|| getPassword() == null
					|| getPassword().isEmpty())) {
				sendtext("メールアドレスかパスワードが空白です。");
				return false;
			}
			if (useProxy()
				&& (   getProxy() == null || getProxy().isEmpty()
					|| getProxyPort() < 0 || getProxyPort() > 65535   )){
				sendtext("プロキシの設定が不正です。");
				return false;
			}
		}
		sendtext("チェック終了");
		return true;
	}

	private NicoClient getNicoClient() {
		if (isSaveVideo() || isSaveComment() || isSaveOwnerComment()) {
			String proxy = null;
			int proxy_port = -1;
			if (useProxy()) {
				proxy = getProxy();
				proxy_port = getProxyPort();
			}
			sendtext("ログイン中");
			NicoClient client = null;
			if (BrowserKind != BrowserCookieKind.NONE){
				// セッション共有、ログイン済みのNicoClientをclientに返す
				client = new NicoClient(BrowserKind, UserSession, proxy, proxy_port, Stopwatch);
			} else {
				client = new NicoClient(getMailAddress(), getPassword(), proxy, proxy_port, Stopwatch);
			}
			if (!client.isLoggedIn()) {
				sendtext("ログイン失敗 " + BrowserInfo.getBrowserName() + " " + client.getExtraError());
			} else {
				sendtext("ログイン成功 " + BrowserInfo.getBrowserName());
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
		if (isSaveVideo()) {
			if (isVideoFixFileName()) {
				if (folder.mkdir()) {
					System.out.println("Folder created: " + folder.getPath());
				}
				if (!folder.isDirectory()) {
					sendtext("動画の保存先フォルダが作成できません。");
					return false;
				}
				VideoFile = new File(folder,
						VideoID + VideoTitle + ".flv");
			} else {
				VideoFile = Setting.getVideoFile();
			}
			sendtext("動画のダウンロード開始中");
			if (client == null){
				sendtext("ログインしてないのに動画の保存になりました");
				return false;
			}
			VideoFile = client.getVideo(VideoFile, Status, StopFlag);
			if (stopFlagReturn()) {
				return false;
			}
			if (VideoFile == null) {
				sendtext("動画のダウンロードに失敗");
				return false;
			}
			if (optionalThreadID == null || optionalThreadID.isEmpty()) {
				optionalThreadID = client.getOptionalThreadID();
			}
			videoLength = client.getVideoLength();
		} else {
			if (isSaveConverted()) {
				if (isVideoFixFileName()) {
					String videoFilename;
					if((videoFilename = detectTitleFromVideo(folder)) == null){
						if (OtherVideo == null){
							sendtext("動画ファイルがフォルダに存在しません。");
						} else {
							sendtext("動画ファイルが.flvでありません：" + OtherVideo);
						}
						return false;
					}
					VideoFile = new File(folder, videoFilename);
					if (!VideoFile.canRead()) {
						sendtext("動画ファイルが読み込めません。");
						return false;
					}
				} else {
					VideoFile = Setting.getVideoFile();
					if (!VideoFile.exists()) {
						sendtext("動画ファイルが存在しません。");
						return false;
					}
				}
			}
		}
		sendtext("動画の保存を終了");
		return true;
	}

	private boolean saveComment(NicoClient client) {
		sendtext("コメントの保存");
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
					sendtext("コメントの保存先フォルダが作成できません。");
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
				commentTitle = VideoID + VideoTitle + prefix;
				CommentFile = new File(folder, commentTitle + ".xml");
			} else {
				CommentFile = Setting.getCommentFile();
			}
			if (client == null){
				sendtext("ログインしてないのにコメントの保存になりました");
				return false;
			}
		//	String back_comment = Setting.getBackComment();
			if (Setting.isFixCommentNum()) {
				back_comment = client
						.getBackCommentFromLength(back_comment);
			}
			sendtext("コメントのダウンロード開始中");
			CommentFile = client.getComment(CommentFile, Status, back_comment, Time, StopFlag);
			if (stopFlagReturn()) {
				return false;
			}
			if (CommentFile == null) {
				sendtext("コメントのダウンロードに失敗 " + client.getExtraError());
				return false;
			}
			sendtext("コメントのダウンロード終了");
			optionalThreadID = client.getOptionalThreadID();
			sendtext("オプショナルスレッドの保存");
			if (optionalThreadID != null && !optionalThreadID.isEmpty() ){
				if (isCommentFixFileName()) {
					OptionalThreadFile = new File(folder, VideoID + VideoTitle + prefix + OPTIONAL_EXT);
				} else {
					OptionalThreadFile = getOptionalThreadFile(Setting.getCommentFile());
				}
				sendtext("オプショナルスレッドのダウンロード開始中");
				OptionalThreadFile = client.getOptionalThread(
					OptionalThreadFile, Status, optionalThreadID, back_comment, Time, StopFlag);
				if (stopFlagReturn()) {
					return false;
				}
				if (OptionalThreadFile == null) {
					sendtext("オプショナルスレッドのダウンロードに失敗 " + client.getExtraError());
					return false;
				}
				sendtext("オプショナルスレッドの保存終了");
			}
		}
		sendtext("コメントの保存終了");
		return true;
	}
	private File getOptionalThreadFile(File file) {
		if (file == null || !file.isFile() || file.getPath() == null) {
			return mkTemp(OPTIONAL_EXT);
		}
		String path = file.getPath();
		int index = path.lastIndexOf(".");
		if (index > path.lastIndexOf(File.separator)) {
			path = path.substring(0, index);		// 拡張子を削除
		}
		return new File(path + OPTIONAL_EXT);
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
					return false;
				}
				OwnerCommentFile = new File(folder, VideoID + VideoTitle + OWNER_EXT);
			} else {
				OwnerCommentFile = Setting.getOwnerCommentFile();
			}
			sendtext("投稿者コメントのダウンロード開始中");
			if (client == null){
				sendtext("ログインしてないのに投稿者コメントの保存になりました");
				return false;
			}
			OwnerCommentFile = client.getOwnerComment(OwnerCommentFile, Status,
					StopFlag);
			if (stopFlagReturn()) {
				return false;
			}
			if (OwnerCommentFile == null) {
				sendtext("投稿者コメントのダウンロードに失敗");
				return false;
			}
			if (optionalThreadID == null || optionalThreadID.isEmpty()) {
				optionalThreadID = client.getOptionalThreadID();
			}
		}
		sendtext("投稿者コメントの保存終了");
		return true;
	}

	private Path mkTemp(String uniq){
		return Path.mkTemp(Tag + uniq);
	}

	private boolean convertComment(){
		sendtext("コメントの中間ファイルへの変換中");
		File folder = Setting.getCommentFixFileNameFolder();
		if (isConvertWithComment()) {
			if (Setting.isAddTimeStamp() && isCommentFixFileName()) {
				// 複数のコメントファイル（過去ログ）があるかも
				ArrayList<String> pathlist = detectFilelistFromComment(folder);
				if (pathlist == null || pathlist.isEmpty()){
					sendtext(Tag + ": コメントファイル・過去ログが存在しません。");
					return false;
				}
				// VideoTitle は見つかった。
				if (pathlist.size() > 0) {			// 0 1.22r3e8, for NP4 comment ver 2009
					ArrayList<File> filelist = new ArrayList<File>();
					for (String path: pathlist){
						filelist.add(new File(folder, path));
					}
					CommentFile = mkTemp(TMP_COMBINED_XML);
					sendtext("コメントファイル結合中");
					if (!CombineXML.combineXML(filelist, CommentFile)){
						sendtext("コメントファイルが結合出来ませんでした（バグ？）");
						return false;
					}
					listOfCommentFile = filelist;
				} else {
					// コメントファイルはひとつだけ見つかった
					// ここには来ない 1.22r3e8, for NP4 comment ver 2009
//					File comfile = new File(folder,pathlist.get(0));
//					if (isSaveComment()){			// for NP4 comment ver 2009
//						ArrayList<File> filelist = new ArrayList<File>();
//						listOfCommentFile = filelist;
//					} else {
//						CommentFile = comfile;
//						}
				}
			}
			if (!isSaveComment()) {
				if (isCommentFixFileName()) {
					if (!Setting.isAddTimeStamp()){
						// コメントファイルはひとつ
						String commentfilename = detectTitleFromComment(folder);
						if(commentfilename == null){
							sendtext("コメントファイルがフォルダに存在しません。");
							return false;
						}
						// VideoTitle は見つかった。
						CommentFile = new File(folder, commentfilename);
						if (!CommentFile.canRead()) {
							sendtext("コメントファイルが読み込めません。");
							return false;
						}
					} else {
						// 処理済み
					}
				} else {
					CommentFile = Setting.getCommentFile();
					if (!CommentFile.exists()) {
						sendtext("コメントファイルが存在しません。");
						return false;
					}
				}
			}
			CommentMiddleFile = convertToCommentMiddle(CommentFile, mkTemp(TMP_COMMENT));
			if (CommentMiddleFile == null){
				sendtext("コメント変換に失敗。おそらく正規表現の間違い？");
				return false;
			}
		}
		return true;
	}

	private boolean convertOprionalThread(){
		sendtext("オプショナルスレッドの中間ファイルへの変換中");
		File folder = Setting.getCommentFixFileNameFolder();
		if (isConvertWithComment()) {
			if (isCommentFixFileName()) {
				// フォルダ指定時、複数のオプショナルスレッド（過去ログ）があるかも
				ArrayList<String> pathlist = detectFilelistFromOptionalThread(folder);
				if (pathlist == null || pathlist.isEmpty()){
					sendtext(Tag + ": オプショナルスレッド・過去ログが存在しません。");
					System.out.println("No optional thread.");
					OptionalThreadFile = null;
					return true;
				}
				// VideoTitle は見つかった。
				ArrayList<File> filelist = new ArrayList<File>();
				for (String path: pathlist){
					filelist.add(new File(folder, path));
				}
				OptionalThreadFile = mkTemp(TMP_COMBINED_XML2);
				sendtext("オプショナルスレッド結合中");
				if (!CombineXML.combineXML(filelist, OptionalThreadFile)){
					sendtext("オプショナルスレッドが結合出来ませんでした（バグ？）");
					return false;
				}
				listOfCommentFile.addAll(filelist);
			} else {
				// ファイル指定の時
				OptionalThreadFile = getOptionalThreadFile(Setting.getCommentFile());
				if (!OptionalThreadFile.exists()){
					sendtext("オプショナルスレッドが存在しません。");
					System.out.println("No optional thread.");
					OptionalThreadFile = null;
					return true;
				}
			}
			OptionalMiddleFile = convertToCommentMiddle(OptionalThreadFile, mkTemp(TMP_OPTIONALTHREAD));
			if (OptionalMiddleFile == null){
				sendtext("オプショナルスレッド変換に失敗。おそらく正規表現の間違い？");
				return false;
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
						return false;
					}
					// VideoTitle は見つかった。
					OwnerCommentFile = new File(folder, ownerfilename);
					if (!OwnerCommentFile.canRead()) {
						sendtext("投稿者コメントファイルが読み込めません。");
						return false;
					}
				} else {
					OwnerCommentFile = Setting.getOwnerCommentFile();
					if (!OwnerCommentFile.exists()) {
						sendtext("投稿者コメントファイルが存在しません。");
						return false;
					}
				}
			}
			OwnerMiddleFile = convertToCommentMiddle(OwnerCommentFile, mkTemp(TMP_OWNERCOMMENT));
			if (OwnerMiddleFile == null){
				sendtext("投稿者コメント変換に失敗。おそらく正規表現の間違い？");
				return false;
			}
		}
		return true;
	}

	private void deleteCommentFile(){
		if (CommentFile.delete()) {
			System.out.println("Deleted: " + CommentFile.getPath());
		}
		if (OptionalThreadFile != null && OptionalThreadFile.delete()){
			System.out.println("Deleted: " + OptionalThreadFile.getPath());
		}
		deleteList(listOfCommentFile);
		// OwnerCommentFile.delete();
		if (OwnerCommentFile != null && OwnerCommentFile.delete()) {
			System.out.println("Deleted: " + OwnerCommentFile.getPath());
		}
	}

	private File convertToCommentMiddle(File commentfile, File middlefile) {
		if (!ConvertToVideoHook.convert(
				commentfile, middlefile,
				Setting.getNG_ID(), Setting.getNG_Word())){
			return null;
		}
		return middlefile;
	}

	private boolean convertVideo() throws IOException {
		sendtext("動画の変換を開始");
		Stopwatch.start();
		/*ビデオ名の確定*/
		File folder = Setting.getConvFixFileNameFolder();
		if (!chekAspectVhookOption(VideoFile, wayOfVhook)){
			return false;
		}
		if (Setting.isConvFixFileName()) {
			if (folder.mkdir()) {
				System.out.println("Created folder: " + folder.getPath());
			}
			if (!folder.isDirectory()) {
				sendtext("変換後の保存先フォルダが作成できません。");
				return false;
			}
			String conv_name = VideoTitle;
			if (conv_name == null){
				conv_name = "";
			}
			if (!Setting.isNotAddVideoID_Conv()) {//付加するなら
				conv_name = VideoID + conv_name;
			}
			if (conv_name.isEmpty()) {
				sendtext("変換後のビデオファイル名が確定できません。");
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
					return false;
				}
				conv_name = MainOption + InOption + OutOption;
				if (!getFFmpegVfOption().isEmpty()){
					conv_name = VFILTER_FLAG + " " + getFFmpegVfOption() + conv_name;
				}
				conv_name = getFFmpegOptionName() + safeAsciiFileName(conv_name);
				dirName = new File(folder, conv_name).getAbsolutePath().getBytes("Shift_JIS");
				// ファイル名が長すぎる場合
				if (dirName.length > (255 - 3)){
					int len = conv_name.length() - (dirName.length - (255 - 3));
					if (len < 1){
						sendtext("作成するビデオファイル名が長すぎます。");
						return false;
					}
					conv_name = conv_name.substring(0, len);
				}
				conv_name = conv_name.trim();
			}
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
			return false;
		}
		int code = converting_video();
		Stopwatch.stop();
		if (code == 0) {
			sendtext("変換が正常に終了しました。");
			System.out.println(ffmpeg.getLastFrame());
			return true;
		} else if (code == CODE_CONVERTING_ABORTED) { /*中断*/

		} else {
			sendtext("変換エラー：" + ffmpeg.getLastError());
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

	@Override
	public void run() {
		try {
			Stopwatch.clear();
			Stopwatch.start();
			if (!checkOK()) {
				return;
			}
			NicoClient client = getNicoClient();
			if (client != null){
				if (!client.isLoggedIn()){
					return;
				}
				if (!client.getVideoInfo(Tag, WatchInfo, Time)) {
					sendtext(Tag + "の情報の取得に失敗 " + client.getExtraError());
					return;
				}
				if (stopFlagReturn()) {
					return;
				}
				VideoTitle = client.getVideoTitle();
				sendtext(Tag + "の情報の取得に成功");
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
			System.out.println("変換前時間　" + Stopwatch.formatElapsedTime());

			if (!isSaveConverted()) {
				sendtext("動画・コメントを保存し、変換は行いませんでした。");
				return;
			}

			Stopwatch.show();
			if (!convertComment() || stopFlagReturn()) {
				return;
			}

			Stopwatch.show();
			if (!convertOwnerComment() || stopFlagReturn()){
				return;
			}

			Stopwatch.show();
			if (!convertOprionalThread() || stopFlagReturn()) {
				return;
			}

			Stopwatch.show();
			if (convertVideo()) {
				// 変換成功
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
				/*
				if (OptionalMiddleFile != null) {
					if (OptionalMiddleFile.delete()) {
						System.out.println("Deleted: " + OptionalMiddleFile.getPath());
					}
				}
				*/
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			StopFlag.finished();
			Stopwatch.show();
			Stopwatch.stop();
			System.out.println("変換時間　" + Stopwatch.formatLatency());
			System.out.println("LastStatus: " + Status.getText());
			System.out.println("VideoInfo: " + MovieInfo.getText());
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
			VideoFile = fwsFile;
			video = fwsFile;
		}
		videoAspect = ffmpeg.getAspect(video);
		String str;
		if (videoAspect == null){
			str = "Analize Error   ";
			videoAspect = Aspect.NORMAL;
		} else {
			str = videoAspect.explain() + "  ";
		}
		if (way == 1){
			if (VhookNormal == null){
				if (!videoAspect.isWide()){
					str = "≠" + str;
				}
				videoAspect = Aspect.WIDE;
			} else {
				if (videoAspect.isWide()){
					str = "≠" + str;
				}
				videoAspect = Aspect.NORMAL;
			}
		}
		String auto = "";
		if (way==2) {
			auto = "自動選択 ";
		}
		if (videoAspect.isWide()){
			selectedVhook = VhookWide;
			MovieInfo.setText(auto + "拡張Vhook ワイド " + str);
		} else {
			selectedVhook = VhookNormal;
			MovieInfo.setText(auto + "拡張Vhook 従来 " + str);
		}
		if (!detectOption(videoAspect.isWide())){
			sendtext("変換オプションファイルの読み込みに失敗しました。");
			return false;
		}
		return true;
	}

	private static final int CODE_CONVERTING_ABORTED = 100;

	private int converting_video() {
		int code = -1;
		/*
		 * ffmpeg.exe -y mainoption inoption -i infile outoptiont [vhookOption] outfile
		 */
		ffmpeg.setCmd("-y ");
		ffmpeg.addCmd(MainOption);
		ffmpeg.addCmd(" ");
		ffmpeg.addCmd(InOption);
		ffmpeg.addCmd(" -i ");
		ffmpeg.addFile(VideoFile);
		ffmpeg.addCmd(" ");
		ffmpeg.addCmd(OutOption);
		if (!Setting.isVhookDisabled()) {
			if(!addVhookSetting(ffmpeg, selectedVhook, videoAspect.isWide())){
				return -1;
			}
		} else if (!getFFmpegVfOption().isEmpty()){
			ffmpeg.addCmd(" -vfilters ");
			ffmpeg.addCmd(getFFmpegVfOption());
		}
		ffmpeg.addCmd(" ");
		ffmpeg.addFile(ConvertedVideoFile);

		System.out.println("arg:" + ffmpeg.getCmd());
		code = ffmpeg.exec(Status, CODE_CONVERTING_ABORTED, StopFlag, Stopwatch);
		errorLog = ffmpeg.getErrotLog().toString();
		if (fwsFile != null) {
			// fwsFile.delete();	// For DEBUG
		}
		return code;
	}
/*
	public File nmm2avi(File fwstmp) throws IOException {
		int code = -1;
		File tmpdir = new File("tmpdir");
		tmpdir.mkdir();
		if (!tmpdir.isDirectory()){
			sendtext("一時ファイルの保存先フォルダが作成できません。");
			throw new IOException("");
		}
		File [] list = tmpdir.listFiles();
		for (File file: list){
			file.delete();
		}
		File videoavi = new File("fws_tmp.avi");
		if(videoavi.exists()){
			videoavi.delete();
		}
		File avi = new File("fws_out.avi");
		if(avi.exists()){
			avi.delete();
		}
*/		/*
		 * ffmpeg.exe -r 25 -y -i fws_tmp.swf -an -vcodec copy -f image2 %03d.jpg
		 */
/*		ffmpeg.setCmd("-xerror -y -i ");
		ffmpeg.addFile(fwstmp);
//		ffmpeg.addCmd(" -an -vcodec copy -f image2 -vbsf mjpeg2jpeg ");
		ffmpeg.addCmd(" -an -f image2 ");
		ffmpeg.addFile(new File(tmpdir, "%08d.jpg"));
		System.out.println("nmm2avi:" + ffmpeg.getCmd());
		sendtext("NMM動画から一時ファイルを抽出中");
		code = ffmpeg.exec(Status, CODE_CONVERTING_ABORTED, StopFlag);
		if (code != 0){
			if (code != CODE_CONVERTING_ABORTED) { // 中断
				sendtext("変換エラー：" + ffmpeg.getLastError());
			}
			throw new IOException("");
		}
*/		/*
		 * JPEGファイルをAVI形式に合成
		 * ffmpeg.exe -r 1/4 -y -i %03d.jpg -an -vcodec huffyuv -f avi huffjpg.avi
		 */
/*		ffmpeg.setCmd("-xerror -r 1/4 -y -i ");
		ffmpeg.addFile(new File(tmpdir, "%08d.jpg"));
		ffmpeg.addCmd(" -an -vcodec huffyuv -f avi ");
		ffmpeg.addFile(videoavi);
		System.out.println("nmm2avi:" + ffmpeg.getCmd());
		sendtext("NMM動画をAVI形式に変換中");
		code = ffmpeg.exec(Status, CODE_CONVERTING_ABORTED, StopFlag);
		if (code != 0){
			if (code != CODE_CONVERTING_ABORTED) {
				sendtext("変換エラー：" + ffmpeg.getLastError());
			}
			throw new IOException("");
		}
*/		/*
		 * 音声を合成
		 * ffmpeg.exe -y -i fws_tmp.swf -itsoffset 1.0 -i avi4.avi
		 *  -vcodec libxvid -acodec libmp3lame -ab 128k -ar 44100 -ac 2 fwsmp4.avi
		 */
/*		ffmpeg.setCmd("-xerror -y -i ");
		ffmpeg.addFile(fwstmp);
		ffmpeg.addCmd(" -i ");
		ffmpeg.addFile(videoavi);
		ffmpeg.addCmd(" -acodec copy -vcodec copy ");
		ffmpeg.addFile(avi);
		System.out.println("nmm2avi:" + ffmpeg.getCmd());
		sendtext("AVI動画に音声を結合中");
		code = ffmpeg.exec(Status, CODE_CONVERTING_ABORTED, StopFlag);
		if (code != 0){
			if (code != CODE_CONVERTING_ABORTED) {
				sendtext("変換エラー：" + ffmpeg.getLastError());
			}
			throw new IOException("");
		}
		return avi;
	}
*/
	private boolean addVhookSetting(FFmpeg ffmpeg, File vhookExe, boolean isWide) {
		try {
			ffmpeg.addCmd(" -vfilters \"");
			if (!getFFmpegVfOption().isEmpty()){
				ffmpeg.addCmd(getFFmpegVfOption());
				ffmpeg.addCmd(",");
			}
			ffmpeg.addCmd("vhext=");
			ffmpeg.addFile(vhookExe);
			if(CommentMiddleFile!=null){
				ffmpeg.addCmd("|--data-user:");
				ffmpeg.addCmd(URLEncoder.encode(
					Path.toUnixPath(CommentMiddleFile), "Shift_JIS"));
				ffmpeg.addCmd("|--show-user:");
				ffmpeg.addCmd(Setting.getVideoShowNum());
			}
			if(OwnerMiddleFile!=null){
				ffmpeg.addCmd("|--data-owner:");
				ffmpeg.addCmd(URLEncoder.encode(
					Path.toUnixPath(OwnerMiddleFile), "Shift_JIS"));
				ffmpeg.addCmd("|--show-owner:" + NicoClient.STR_OWNER_COMMENT + "|");
			}
			if (OptionalMiddleFile!=null){
				ffmpeg.addCmd("|--data-optional:");
				ffmpeg.addCmd(URLEncoder.encode(
					Path.toUnixPath(OptionalMiddleFile), "Shift_JIS"));
				ffmpeg.addCmd("|--show-optional:");
				ffmpeg.addCmd(Setting.getVideoShowNum());
				if (Setting.isOptionalTranslucent()) {
					ffmpeg.addCmd("|--optional-translucent");
				}
			}
			ffmpeg.addCmd("|--font:");
			ffmpeg.addCmd(URLEncoder.encode(
				Path.toUnixPath(Setting.getFontPath()), "Shift_JIS"));
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
			}
			if (isWide){
				ffmpeg.addCmd("|--nico-width-wide");
			}
			if (videoLength > 0){
				ffmpeg.addCmd("|--video-length:");
				ffmpeg.addCmd(Integer.toString(videoLength));
			}
			if (Setting.isFontHeightFix()){
				ffmpeg.addCmd("|--font-height-fix-ratio:"
						+ Setting.getFontHeightFixRaito());
			}
			if (Setting.isDisableOriginalResize()){
				ffmpeg.addCmd("|--disble-original-resize");
			}
			ffmpeg.addCmd("|\"");
			return true;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
	}

	/*
	private static void addArrayToList(ArrayList<String> list,String array[]){
	for(int i=0;i<array.length;i++){
	list.add(array[i]);
	}
	}
	private static String escape(String str){
	byte[] buff = null;
	try {
	buff = str.getBytes("Shift_JIS");
	} catch (UnsupportedEncodingException e) {
	e.printStackTrace();
	}
	int cnt = 0;
	for(int i=0;i<buff.length;i++){
	if(buff[i] == '\\' || buff[i] == '{' || buff[i] == '}'){
	cnt++;
	}
	cnt++;
	}
	byte[] obuff = new byte[cnt];
	cnt = 0;
	for(int i=0;i<buff.length;i++){
	if(buff[i] == '\\' || buff[i] == '{' || buff[i] == '}'){
	obuff[cnt] = '\\';
	cnt++;
	}
	obuff[cnt] = buff[i];
	cnt++;
	}
	try {
	String out = new String(obuff,"Shift_JIS");
	return out;
	} catch (UnsupportedEncodingException e) {
	e.printStackTrace();
	}
	return "";
	}
	 */
	public boolean isConverted() {
		return StopFlag.isConverted();
	}

	private boolean stopFlagReturn() {
		if (StopFlag.needStop()) {
			sendtext("中止しました。");
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

	private String ffmpegOptionName = "直接入力";

	private String ffmpegVfOption = "";

	boolean detectOption(boolean isWide) {
		File option_file = null;
		if (!isWide) {
			option_file = Setting.getOptionFile();
		} else {
			option_file = Setting.getWideOptionFile();
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
		} else {
			if (!isWide){
				ExtOption = Setting.getCmdLineOptionExt();
				InOption = Setting.getCmdLineOptionIn();
				OutOption = Setting.getCmdLineOptionOut();
				MainOption = Setting.getCmdLineOptionMain();
			} else {
				ExtOption = Setting.getWideCmdLineOptionExt();
				InOption = Setting.getWideCmdLineOptionIn();
				OutOption = Setting.getWideCmdLineOptionOut();
				MainOption = Setting.getWideCmdLineOptionMain();
			}
		}
		//オプションに拡張子を含んでしまった場合にも対応☆
		if(ExtOption != null && !ExtOption.startsWith(".")){
			ExtOption = "."+ExtOption;
		}
		ffmpegVfOption = getvfOption();
		return true;
	}

	private String getvfOption() {
		String vfIn, vfOut, vfMain;
		vfIn = getvfOption(InOption);
		InOption = deletevfOption(InOption, vfIn);
		vfOut = getvfOption(OutOption);
		OutOption = deletevfOption(OutOption, vfOut);
		vfMain = getvfOption(MainOption);
		MainOption = deletevfOption(MainOption, vfMain);
		if (vfIn.isEmpty()){
			vfIn = vfMain;
		} else if (!vfMain.isEmpty()){
			vfIn += "," + vfMain;
		}
		if (vfIn.isEmpty()){
			vfIn = vfOut;
		} else if (!vfOut.isEmpty()){
			vfIn += "," + vfOut;
		}
		return vfIn;
	}
	private static final String VFILTER_FLAG = "-vfilters";
	private String getvfOption(String option){
		if (option == null){
			return "";
		}
		int index;
		if ((index = option.indexOf(VFILTER_FLAG)) < 0){
			return "";
		}
		option = option.substring(index + VFILTER_FLAG.length());
		option = option.trim();
		if ((index = option.indexOf(" ")) < 0){
			return option;
		}
		option = option.substring(0, index);
		return option;
	}
	private String deletevfOption(String option, String vfoption){
		if (option == null){
			return "";
		}
		return option.replace(VFILTER_FLAG,"").replace(vfoption, "")
			.replaceAll(" +", " ");
	}
	public String getInOption(){
			return InOption;
	}
	public String getFFmpegOptionName() {
		return ffmpegOptionName;
	}
	public String getFFmpegVfOption() {
		return ffmpegVfOption;
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
		if (VideoTitle == null){
			VideoTitle = getTitleFromPath(path, VideoID);
			int index = VideoTitle.lastIndexOf("[");
				//過去ログは[YYYY/MM/DD_HH:MM:SS]が最後に付く
			if (index >= 0){
				VideoTitle = VideoTitle.substring(0, index);
			}
		}
	}

	String detectTitleFromVideo(File dir){
		if (dir == null){ return null; }
		String list[] = dir.list(DefaultVideoIDFilter);
		if(list == null){ return null; }
		for (int i = 0; i < list.length; i++) {
			if (list[i].startsWith(VideoID)) {
				String path = list[i];
				if(!path.endsWith(".flv")){
					OtherVideo = path;
					continue;
				}
				setVideoTitleIfNull(path);
				return path;
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
/*
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
*/
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

	/*
	 * lastChar が videoID より後ろにあれば
	 * 最後の lastChar の前までに縮める
	 * lastCharはタイトルにダブって含まれてよいが
	 * タイトル後に少なくとも1つあることは確実でないとダメ
	 */
	/*
	private String getTitileFromPath(String path, String videoID,
			String lastChar){
		if (lastChar != null
				&& path.lastIndexOf(lastChar) > path.indexOf(videoID)){
			path = path.substring(0, path.lastIndexOf(lastChar));
		}
		return getTitleFromPath(path, videoID);
	}
	*/

}
