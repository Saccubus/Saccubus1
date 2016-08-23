package saccubus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import saccubus.net.BrowserInfo;
import saccubus.net.BrowserInfo.BrowserCookieKind;
import saccubus.net.NicoClient;
import saccubus.util.Encryption;

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
public class ConvertingSetting {
	//default setting for 1.60-

	static final String DEF_OPTS_FPSUP = " -acodec copy -vsync 1 -vcodec libx264 -qscale 1 -f mp4 ";
	static final String DEF_OPTS_SWF_JPEG = " -an -vcodec copy -r 1 -f image2 ";
	static final String DEF_OPTS_JPEG_MP4 = " -an -vcodec libx264 -qscale 1 -pix_fmt yuv420p -f mp4 ";
	static final String DEF_OPTS_MIX = " -acodec copy -vcodec libx264 -qscale 1 -pix_fmt yuv420p -f mp4 ";
	static final boolean DEF_OPTS_SAVE_THUMBINFO_METADATA = false;
	static final String DEFAULT_CMDLINE_OUT="-threads 0 -s 512x384 -acodec libvo_aacenc -ab 128k -ar 44100 -ac 2 -vcodec libx264 -crf 23 -async 1 -aspect 16:9 -pix_fmt yuv420p";
	static final String DEFAULT_WIDE_CMDLINE_OUT = "-threads 0 -s 640x360 -acodec libvo_aacenc -ab 128k -ar 44100 -ac 2 -vcodec libx264 -crf 23 -async 1 -aspect 16:9 -pix_fmt yuv420p";
	static final String DEFAULT_ZQ_CMDLINE_OUT = "-threads 0 -s 640x384 -acodec libvo_aacenc -ab 128k -ar 44100 -ac 2 -vcodec libx264 -crf 23 -async 1 -samx -pix_fmt yuv420p";
	static final String DEFAULT_OPTION_FOLDER = "./optionF";
	static final String DEFAULT_VHOOK_PATH = "./bin/nicovideoE.dll";
	static final String DEFAULT_FFMPEG_PATH = "./bin/ffmpeg.exe";

	public static final String[] ShadowKindArray = {
		"00:なし",
		"01:ニコニコ動画風",
		"02:右下",
		"03:囲い込み",
		"04:Saccubus2風"
		};

	private String MailAddress;
	private String Password;
	private boolean SaveVideo;
	private File VideoFile;
	private boolean SaveComment;
	private File CommentFile;
	private boolean SaveConverted;
	private boolean ConvertWithComment;
	private File ConvertedVideoFile;
	private String FFmpegPath;
	private String VhookPath;
	private String CmdLineOptionExt;
	private String CmdLineOptionIn;
	private String CmdLineOptionOut;
	private String CmdLineOptionMain;
	private String BackComment;
	private String FontPath;
	private int FontIndex;
	private boolean Vhook_ShowConvertingVideo;
	private int VideoShowNum;
	private boolean DeleteVideoAfterConverting;
	private boolean VideoFixFileName;
	private File VideoFixFileNameFolder;
	private boolean DeleteCommentAfterConverting;
	private boolean CommentFixFileName;
	private File CommentFixFileNameFolder;
	private boolean ConvFixFileName;
	private File ConvFixFileNameFolder;
	private String NG_Word;
	private String NG_ID;
	private boolean UseProxy;
	private String Proxy;
	private int ProxyPort;
	private boolean FixFontSize;
	private boolean FixCommentNum;
	private boolean OpaqueComment;
	private boolean NotAddVideoID_Conv;
//	private boolean UseOptionFile;
	private File OptionFile;
	private boolean DisableVhook;
	private int ShadowIndex;
	private boolean SaveOwnerComment;
	private File OwnerCommentFile;
	private boolean ConvertWithOwnerComment;
	private boolean AddTimeStamp;
	private boolean AddOption_ConvVideoFile;
	private String lastHistory;
	private String VhookWidePath;
	private boolean UseVhookNormal;
	private boolean UseVhookWide;
	private boolean BrowserIE;
	private boolean BrowserFF;
	private boolean BrowserChrome;
	private boolean BrowserChromium;
	private boolean BrowserOpera;
	private boolean BrowserOther;
	private String BrowserCookiePath;
	private String optionFolder;
	private File wideOptionFile;
	private String wideCmdLineOptionExt;
	private String wideCmdLineOptionIn;
	private String wideCmdLineOptionOut;
	private String wideCmdLineOptionMain;
	private boolean optionalTranslucent;
	private boolean fontHeightFix;
	private String fontHeightFixRatio;
	private boolean disableOriginalResize;
	private int commentIndex;
	private boolean setCommentSpeed;
	private String commentSpeed;
//	private String debugNicovideo;
	private boolean enableCA;	//仮設定
	private int scoreLimit;
	private boolean disableEco;
	private boolean fontWidthFix;
	private String fontWidthFixRatio;
	private boolean useLineSkip;
	private boolean useExtraFont;
	private String extraFontText;
	private String ngCommand;
	private String replaceCommand;
	private String encryptedPass;
	private String extraMode;
	private String addOption;
	private String wideAddOption;
	private boolean saveWatchPage;
	private boolean saveThumbInfo;
	private String userFolder;
	private boolean saveThumbUser;
	private boolean saveThumbInfoAsText;
	private boolean changeMp4Ext;
	private boolean changeTitleId;
	private boolean saveThumbnailJpg;
	private boolean zqPlayer;
	private String zqVhookPath;
	private File zqOptionFile;
	private String zqCmdLineOptionExt;
	private String zqCmdLineOptionIn;
	private String zqCmdLineOptionOut;
	private String zqCmdLineOptionMain;
	private String zqAddOption;
	private StringBuffer retBuffer;
	private String opaqueRate;
	private boolean swfTo3path;
	private boolean checkFps;
	private Double fpsUp;
	private Double fpsMin;
	private boolean enableSoundOnly;
	private String thumbnailFile;
	private boolean saveAutoList;
	private boolean useFpsFilter;
	private boolean autoPlay;
	private static String defOptsFpsUp = DEF_OPTS_FPSUP;
	private static String defOptsSwfJpeg = DEF_OPTS_SWF_JPEG;
	private static String defOptsJpegMp4 = DEF_OPTS_JPEG_MP4;
	private static String defOptsMix = DEF_OPTS_MIX;
	private static boolean defOptsSaveThumbinfoMetadata = DEF_OPTS_SAVE_THUMBINFO_METADATA;
	private Map<String, String> replaceOptions;
	private boolean liveOparetionConversion;
	private boolean premiumColorCheck;
	private String optionFileDescription;
	private boolean appendCommentMode;
	private int numThread;
	private String appendNotice;
	private int numDownload;
	private boolean PendingMode;
	private boolean OneLineMode;
	private String errorList;
	private boolean liveFlag;
	private boolean forceLiveComment;
	private boolean changeLiveOperationDuration;
	private String liveOperationDuration;
	private boolean enableCommentVposShift;
	private String commentVposShiftString;
	private boolean smilePreferable;

	// NONE,MSIE,FireFox,Chrome,Opera,Chromium,Other
	private boolean[] useBrowser = new boolean[BrowserInfo.NUM_BROWSER];

	private ConvertingSetting(
			String mailaddress,
			String password,
			boolean savevideo,
			String videofile,
			boolean savecomment,
			String commentfile,
			boolean saveconverted,
			String convvideofile,
			String videoshownum,
			String ffmpegpath,
			String vhookpath,
			String cmdlineoption_ext,
			String cmdlineoption_main,
			String cmdlineoption_in,
			String cmdlineoption_out,
			String backcomment,
			String fontpath,
			int font_index,
			boolean showconvvideo,
			boolean delete_video_after_conv,
			boolean video_fix_file_name,
			String video_fix_file_name_folder,
			boolean delete_comment_after_conv,
			boolean comment_fix_file_name,
			String comment_fix_file_name_folder,
			boolean not_add_videoid_conv,
			boolean conv_fix_file_name,
			String conv_fix_file_name_folder,
			String ngword,
			String ngid,
			boolean use_proxy,
			String proxy,
			int proxy_port,
			boolean fix_font_size,
			boolean fix_comment_num,
			boolean opaque_comment,
			File option_file,
			boolean disable_vhook,
			int shadow_index)
	{
		replaceOptions = null;
		MailAddress = mailaddress;
		Password = password;
		SaveVideo = savevideo;
		if (videofile.lastIndexOf(".") <= videofile.lastIndexOf(File.separator)) {
			videofile += ".flv";
		}
		VideoFile = new File(videofile);
		SaveComment = savecomment;
		if (commentfile.lastIndexOf(".") <= commentfile.lastIndexOf(File.separator)) {
			commentfile += ".xml";
		}
		CommentFile = new File(commentfile);
		SaveConverted = saveconverted;
		if (convvideofile.lastIndexOf(".") <= convvideofile.lastIndexOf(File.separator)) {
			convvideofile += ".avi";
		}
		ConvertedVideoFile = new File(convvideofile);
		try {
			VideoShowNum = Integer.parseInt(videoshownum);
		} catch (NumberFormatException ex) {
			VideoShowNum = 40;
		}
		FFmpegPath = ffmpegpath;
		VhookPath = vhookpath;
		CmdLineOptionExt = cmdlineoption_ext;
		CmdLineOptionMain = cmdlineoption_main;
		CmdLineOptionIn = cmdlineoption_in;
		CmdLineOptionOut = cmdlineoption_out;
		BackComment = backcomment;
		FontPath = fontpath;
		FontIndex = font_index;
		Vhook_ShowConvertingVideo = showconvvideo;
		DeleteVideoAfterConverting = delete_video_after_conv;
		VideoFixFileName = video_fix_file_name;
		VideoFixFileNameFolder = new File(video_fix_file_name_folder, "");
		DeleteCommentAfterConverting = delete_comment_after_conv;
		CommentFixFileName = comment_fix_file_name;
		CommentFixFileNameFolder = new File(comment_fix_file_name_folder, "");
		NotAddVideoID_Conv = not_add_videoid_conv;
		ConvFixFileName = conv_fix_file_name;
		ConvFixFileNameFolder = new File(conv_fix_file_name_folder, "");
		NG_Word = ngword;
		NG_ID = ngid;
		UseProxy = use_proxy;
		Proxy = proxy;
		ProxyPort = proxy_port;
		FixFontSize = fix_font_size;
		FixCommentNum = fix_comment_num;
		OpaqueComment = opaque_comment;
		OptionFile = option_file;
		DisableVhook = disable_vhook;
		ShadowIndex = shadow_index;
	}

	/*
	 * 拡張設定コンストラクタ
	 */
	public ConvertingSetting(
			String mailaddress,
			String password,
			boolean savevideo,
			String videofile,
			boolean savecomment,
			boolean addtimestamp,
			String commentfile,
			boolean saveownercomment,
			String ownercommentfile,
			boolean saveconverted,
			boolean convertwithcomment,
			boolean convertwithownercomment,
			String convvideofile,
			String videoshownum,
			String ffmpegpath,
			String vhookpath,
			String cmdlineoption_ext,
			String cmdlineoption_main,
			String cmdlineoption_in,
			String cmdlineoption_out,
			String backcomment,
			String fontpath,
			int font_index,
			boolean showconvvideo,
			boolean delete_video_after_conv,
			boolean video_fix_file_name,
			String video_fix_file_name_folder,
			boolean delete_comment_after_conv,
			boolean comment_fix_file_name,
			String comment_fix_file_name_folder,
			boolean not_add_videoid_conv,
			boolean conv_fix_file_name,
			String conv_fix_file_name_folder,
			String ngword,
			String ngid,
			boolean use_proxy,
			String proxy,
			int proxy_port,
			boolean fix_font_size,
			boolean fix_comment_num,
			boolean opaque_comment,
			File option_file,
			boolean disable_vhook,
			int shadow_index,
			boolean addOption_ConvVideoFile,
			String history,
			String vhook_wide_path,
			boolean use_vhook_normal,
			boolean use_vhook_wide,
			boolean browserIE,
			boolean browserFF,
			boolean browserChrome,
			boolean browserChromium,
			boolean browserOpera,
			boolean browserOther,
			String browserCookiePath,
			String option_folder,
			File wide_option_file,
			String wide_cmdlineoption_ext,
			String wide_cmdlineoption_main,
			String wide_cmdlineoption_in,
			String wide_cmdlineoption_out,
			boolean optional_translucent,
			boolean font_height_fix,
			String font_height_fix_raito,
			boolean disable_original_resize,
			int comment_index,
			boolean set_comment_speed,
			String comment_speed,
			boolean enable_CA,
			int score_limit,
			boolean disable_eco,
			boolean font_width_fix,
			String font_width_fix_raito,
			boolean use_lineskip,
			boolean use_extra_font,
			String extra_font_text,
			String ng_command,
			String replace_command,
			String encrypt_pass,
			String extra_mode,
			String add_option,
			String wide_add_option,
			boolean save_watch_page,
			boolean save_thumb_info,
			String user_folder,
			boolean save_thumb_user,
			boolean save_thumb_info_as_text,
			boolean change_mp4_ext,
			boolean change_text_id,
			boolean save_thumbnail_jpg,
			boolean zq_player,
			String zq_vhook_path,
			File zq_option_file,
			String zq_cmdlineoption_ext,
			String zq_cmdlineoption_main,
			String zq_cmdlineoption_in,
			String zq_cmdlineoption_out,
			String zq_add_option,
			StringBuffer return_buffer,
			String opaque_rate,
			boolean swf_3path,
			boolean check_fps,
			double fps_up,
			double fps_min,
			boolean en_soundonly,
			String thumb_file,
			boolean save_autolist,
			boolean use_fpsfilter,
			boolean auto_play,
			boolean live_operation,
			boolean premium_color_check,
			String option_file_description,
			boolean append_comment,
			String append_notice,
			int n_thread,
			boolean pending_mode,
			boolean one_line_mode,
			String error_list,
			boolean change_live_operation_duration,
			boolean force_live_comment,
			String live_operation_duration,
			boolean enable_comment_vpos_shift,
			String vpos_shift_sec,
			boolean smile_preferable
		)
	{
		this(	mailaddress,
				password,
				savevideo,
				videofile,
				savecomment,
				commentfile,
				saveconverted,
				convvideofile,
				videoshownum,
				ffmpegpath,
				vhookpath,
				cmdlineoption_ext,
				cmdlineoption_main,
				cmdlineoption_in,
				cmdlineoption_out,
				backcomment,
				fontpath,
				font_index,
				showconvvideo,
				delete_video_after_conv,
				video_fix_file_name,
				video_fix_file_name_folder,
				delete_comment_after_conv,
				comment_fix_file_name,
				comment_fix_file_name_folder,
				not_add_videoid_conv,
				conv_fix_file_name,
				conv_fix_file_name_folder,
				ngword,
				ngid,
				use_proxy,
				proxy,
				proxy_port,
				fix_font_size,
				fix_comment_num,
				opaque_comment,
				option_file,
				disable_vhook,
				shadow_index);
		ConvertWithComment = convertwithcomment;
		SaveOwnerComment = saveownercomment;
		if (ownercommentfile.lastIndexOf(".") <= ownercommentfile.lastIndexOf(File.separator)) {
			ownercommentfile += ".xml";
		}
		OwnerCommentFile = new File(ownercommentfile);
		ConvertWithOwnerComment = convertwithownercomment;
		AddTimeStamp = addtimestamp;
		AddOption_ConvVideoFile = addOption_ConvVideoFile;
		lastHistory = history;
		VhookWidePath = vhook_wide_path;
		UseVhookNormal = use_vhook_normal;
		UseVhookWide = use_vhook_wide;
		useBrowser[BrowserCookieKind.NONE.ordinal()] = true;
		BrowserIE = browserIE;
		useBrowser[BrowserCookieKind.MSIE.ordinal()] = browserIE;
	//	useBrowser[BrowserCookieKind.IE6.ordinal()] = false;
		BrowserFF = browserFF;
		useBrowser[BrowserCookieKind.Firefox.ordinal()] = browserFF;
		BrowserChrome = browserChrome;
	//	useBrowser[BrowserCookieKind.Firefox3.ordinal()] = false;
		useBrowser[BrowserCookieKind.Chrome.ordinal()] = browserChrome;
		BrowserChromium = browserChromium;
		useBrowser[BrowserCookieKind.Chromium.ordinal()] = browserChromium;
		BrowserOpera = browserOpera;
		useBrowser[BrowserCookieKind.Opera.ordinal()] = browserOpera;
		BrowserOther = browserOther;
		useBrowser[BrowserCookieKind.Other.ordinal()] = browserOther;
		BrowserCookiePath = browserCookiePath;
		optionFolder = option_folder;
		wideOptionFile = wide_option_file;
		wideCmdLineOptionExt = wide_cmdlineoption_ext;
		wideCmdLineOptionMain = wide_cmdlineoption_main;
		wideCmdLineOptionIn = wide_cmdlineoption_in;
		wideCmdLineOptionOut = wide_cmdlineoption_out;
		optionalTranslucent = optional_translucent;
		fontHeightFix = font_height_fix;
		fontHeightFixRatio = font_height_fix_raito;
		disableOriginalResize = disable_original_resize;
		commentIndex = comment_index;
		setCommentSpeed = set_comment_speed;
		commentSpeed = comment_speed;
		enableCA = enable_CA;
		scoreLimit = score_limit;
		disableEco = disable_eco;
		fontWidthFix = font_width_fix;
		fontWidthFixRatio = font_width_fix_raito;
		useLineSkip = use_lineskip;
		useExtraFont = use_extra_font;
		extraFontText = extra_font_text;
		ngCommand = ng_command;
		replaceCommand = replace_command;
		encryptedPass = encrypt_pass;
		extraMode = extra_mode;
		addOption = add_option;
		wideAddOption = wide_add_option;
		saveWatchPage = save_watch_page;
		saveThumbInfo = save_thumb_info;
		userFolder = user_folder;
		saveThumbUser = save_thumb_user;
		saveThumbInfoAsText = save_thumb_info_as_text;
		changeMp4Ext = change_mp4_ext;
		changeTitleId = change_text_id;
		saveThumbnailJpg = save_thumbnail_jpg;
		zqPlayer = zq_player;
		zqVhookPath = zq_vhook_path;
		zqOptionFile = zq_option_file;
		zqCmdLineOptionExt = zq_cmdlineoption_ext;
		zqCmdLineOptionMain = zq_cmdlineoption_main;
		zqCmdLineOptionIn = zq_cmdlineoption_in;
		zqCmdLineOptionOut = zq_cmdlineoption_out;
		zqAddOption = zq_add_option;
		retBuffer = return_buffer;
		opaqueRate = opaque_rate;
		swfTo3path = swf_3path;
		checkFps = check_fps;
		fpsUp = fps_up;
		fpsMin = fps_min;
		enableSoundOnly = en_soundonly;
		thumbnailFile = thumb_file;
		saveAutoList = save_autolist;
		useFpsFilter = use_fpsfilter;
		autoPlay = auto_play;
		liveOparetionConversion = live_operation;
		premiumColorCheck = premium_color_check;
		optionFileDescription = option_file_description;
		appendCommentMode = append_comment;
		appendNotice = append_notice;
		numThread = n_thread;
		PendingMode = pending_mode;
		OneLineMode = one_line_mode;
		errorList = error_list;
		changeLiveOperationDuration = change_live_operation_duration;
		forceLiveComment = force_live_comment;
		if(forceLiveComment)
			liveFlag = true;
		else
			liveFlag = !(savevideo || savecomment || save_thumb_info);	//ローカル変換=生放送で判断(仮)
		liveOperationDuration = live_operation_duration;
		enableCommentVposShift = enable_comment_vpos_shift;
		commentVposShiftString = vpos_shift_sec;
		smilePreferable = smile_preferable;
	}

	public Map<String,String> getReplaceOptions(){
		return replaceOptions;
	}
	public File getVideoFile() {
		return VideoFile;
	}
	public File getCommentFile() {
		return CommentFile;
	}
	public File getOwnerCommentFile() {
		return OwnerCommentFile;
	}
	public File getConvertedVideoFile() {
		return ConvertedVideoFile;
	}
	public String getFFmpegPath() {
		return FFmpegPath;
	}
	public String getVhookPath() {
		return VhookPath;
	}
	public String getCmdLineOptionIn() {
		return CmdLineOptionIn;
	}
	public String getFontPath() {
		return FontPath;
	}
	public String getFontIndex() {
		return Integer.toString(FontIndex);
	}
	public boolean isVhook_ShowConvertingVideo() {
		return Vhook_ShowConvertingVideo;
	}
	public String getMailAddress() {
		return MailAddress;
	}
	public String getPassword() {
		return Password;
	}
	public boolean isSaveVideo() {
		return SaveVideo;
	}
	public boolean isSaveComment() {
		return SaveComment;
	}
	public boolean isAddTimeStamp() {
		return AddTimeStamp;
	}
	public boolean isSaveOwnerComment() {
		return SaveOwnerComment;
	}
	public boolean isSaveConverted() {
		return SaveConverted;
	}
	public boolean isConvertWithComment(){
		return ConvertWithComment;
	}
	public boolean isConvertWithOwnerComment(){
		return ConvertWithOwnerComment;
	}
	public String getCmdLineOptionOut() {
		return CmdLineOptionOut;
	}
	public String getBackComment() {
		return BackComment;
	}
	public String getVideoShowNum() {
		return Integer.toString(VideoShowNum);
	}
	public String getCmdLineOptionExt() {
		return CmdLineOptionExt;
	}
	public String getCmdLineOptionMain() {
		return CmdLineOptionMain;
	}
	public boolean isVideoFixFileName() {
		return VideoFixFileName;
	}
	public boolean isDeleteVideoAfterConverting() {
		return DeleteVideoAfterConverting;
	}
	public File getVideoFixFileNameFolder() {
		return VideoFixFileNameFolder;
	}
	public boolean isCommentFixFileName() {
		return CommentFixFileName;
	}
	public boolean isDeleteCommentAfterConverting() {
		return DeleteCommentAfterConverting;
	}
	public File getCommentFixFileNameFolder() {
		return CommentFixFileNameFolder;
	}
	public boolean isNotAddVideoID_Conv() {
		return NotAddVideoID_Conv;
	}
	public boolean isConvFixFileName() {
		return ConvFixFileName;
	}
	public File getConvFixFileNameFolder() {
		return ConvFixFileNameFolder;
	}
	public String getNG_Word() {
		return NG_Word;
	}
	public String getNG_ID() {
		return NG_ID;
	}
	public boolean useProxy() {
		return UseProxy;
	}
	public String getProxy() {
		return Proxy;
	}
	public int getProxyPort() {
		return ProxyPort;
	}
	public boolean isFixFontSize() {
		return FixFontSize;
	}
	public boolean isFixCommentNum() {
		return FixCommentNum;
	}
	public boolean isOpaqueComment() {
		return OpaqueComment;
	}
//	public boolean useOptionFile() {
//		return UseOptionFile;
//	}
	public File getOptionFile() {
		return OptionFile;
	}
	public boolean isVhookDisabled() {
		return DisableVhook;
	}
	public int getShadowIndex(){
		return ShadowIndex;
	}
	public boolean isAddOption_ConvVideoFile() {
		return AddOption_ConvVideoFile;
	}
	public String getRequestHistory() {
		return lastHistory;
	}
	public String getVhookWidePath(){
		return VhookWidePath;
	}
	public boolean isUseVhookNormal(){
		return UseVhookNormal;
	}
	public boolean isUseVhookWide(){
		return UseVhookWide;
	}
	public boolean isBrowserIE(){
		return BrowserIE;
	}
	public boolean isBrowserFF(){
		return BrowserFF;
	}
	public boolean isBrowserChrome() {
		return BrowserChrome;
	}
	public boolean isBrowserChromium() {
		return BrowserChromium;
	}
	public boolean isBrowserOpera(){
		return BrowserOpera;
	}
	public boolean isBrowser(BrowserCookieKind browser){
		return useBrowser[browser.ordinal()];
	}
	public boolean isBrowser(int n) {
		return useBrowser[n];
	}

	public boolean isBrowserOther(){
		return BrowserOther;
	}
	public String getBrowserCookiePath(){
		return BrowserCookiePath;
	}
	public String getOptionFolder() {
		return optionFolder;
	}
	public File getWideOptionFile() {
		return wideOptionFile;
	}
	public String getWideCmdLineOptionIn() {
		return wideCmdLineOptionIn;
	}
	public String getWideCmdLineOptionOut() {
		return wideCmdLineOptionOut;
	}
	public String getWideCmdLineOptionExt() {
		return wideCmdLineOptionExt;
	}
	public String getWideCmdLineOptionMain() {
		return wideCmdLineOptionMain;
	}
	public boolean isOptionalTranslucent() {
		return optionalTranslucent;
	}
	public boolean isFontHeightFix() {
		return fontHeightFix;
	}
	public String getFontHeightFixRaito() {
		return fontHeightFixRatio;
	}
	public boolean isDisableOriginalResize() {
		return disableOriginalResize;
	}
	public int getCommentIndex() {
		return commentIndex;
	}
	public boolean isSetCommentSpeed(){
		return setCommentSpeed;
	}
	public String getCommentSpeed(){
		return commentSpeed;
	}
	public boolean isDebugNicovideo(){
		return getProxy().startsWith(NicoClient.DEBUG_PROXY);
	}
	public boolean isEnableCA(){
		return enableCA;
	}
	public int getScoreLimit(){
		return scoreLimit;
	}
	public boolean isDisableEco(){
		return disableEco;
	}
	public boolean isFontWidthFix(){
		return fontWidthFix;
	}
	public String getFontWidthFixRaito(){
		return fontWidthFixRatio;
	}
	public boolean isUseLineSkip(){
		return useLineSkip;
	}
	public boolean isUseExtraFont(){
		return useExtraFont;
	}
	public String getExtraFontText(){
		return extraFontText;
	}
	public String getNGCommand(){
		return ngCommand;
	}
	public String getReplaceCommand(){
		return replaceCommand;
	}
	public String getEncryptPass(){
		return encryptedPass;
	}
	public String getExtraMode(){
		return extraMode;
	}
	public String getAddOption(){
		return addOption;
	}
	public String getWideAddOption(){
		return wideAddOption;
	}
	public boolean isSaveWatchPage(){
		return saveWatchPage;
	}
	public boolean isSaveThumbInfo() {
		return saveThumbInfo;
	}
	public String getUserFolder(){
		return userFolder;
	}
	public boolean isSaveThumbUser() {
		return saveThumbUser;
	}
	public boolean isSaveThumbInfoAsText(){
		return saveThumbInfoAsText;
	}
	public boolean isChangeMp4Ext(){
		return changeMp4Ext;
	}
	public boolean isChangeTitleId(){
		return changeTitleId;
	}
	public boolean isSaveThumbnailJpg(){
		return saveThumbnailJpg;
	}
	public boolean isZqPlayer(){
		return zqPlayer;
	}
	public String getZqVhookPath(){
		return zqVhookPath;
	}
	public File getZqOptionFile(){
		return zqOptionFile;
	}
	public String getZqCmdLineOptionIn() {
		return zqCmdLineOptionIn;
	}
	public String getZqCmdLineOptionOut() {
		return zqCmdLineOptionOut;
	}
	public String getZqCmdLineOptionExt() {
		return zqCmdLineOptionExt;
	}
	public String getZqCmdLineOptionMain() {
		return zqCmdLineOptionMain;
	}
	public String getZqAddOption() {
		return zqAddOption;
	}
	public StringBuffer getReturnBuffer(){
		return retBuffer;
	}
	public String getOpaqueRate(){
		return opaqueRate;
	}
	public boolean isSwfTo3Path(){
		return swfTo3path;
	}
	public boolean enableCheckFps(){
		return checkFps;
	}
	public double getFpsUp(){
		return fpsUp;
	}
	public double getFpsMin() {
		return fpsMin;
	}
	public boolean canSoundOnly() {
		return enableSoundOnly;
	}
	public String getDefaultThumbnail() {
		return thumbnailFile;
	}
	public boolean isSaveAutoList(){
		return saveAutoList;
	}
	public boolean isUseFpsFilter() {
		return useFpsFilter;
	}
	boolean isAutoPlay() {
		return autoPlay;
	}
	public boolean isLiveOperationConversion(){
		return liveOparetionConversion;
	}
	public boolean isPremiumColorCheck(){
		return premiumColorCheck;
	}
	public String getOptionFileDescr(){
		return optionFileDescription;
	}
	public boolean isAppendComment(){
		return appendCommentMode;
	}
	public String getAppendNotice(){
		return appendNotice;
	}
	public int getNumThread(){
		return numThread;
	}
	public int getNumDownload(){
		return numDownload;
	}
	public boolean isPendingMode(){
		return PendingMode;
	}
	public boolean isOneLineMode(){
		return OneLineMode;
	}
	public String getErrorList(){
		return errorList;
	}
	public boolean isLive(){
		return liveFlag;
	}
	public boolean changedLiveOperationDuration() {
		return changeLiveOperationDuration;
	}
	public boolean isForcedLiveComment(){
		return forceLiveComment;
	}
	public String getLiveOperationDuration(){
		return liveOperationDuration;
	}
	public boolean isEnableCommentVposShift(){
		return enableCommentVposShift;
	}
	public String getCommentVposShiftString(){
		return commentVposShiftString;
	}
	public boolean isSmilePreferable(){
		return smilePreferable;
	}
	//
	public static String getDefOptsFpsUp(){
		return defOptsFpsUp;
	}
	public static String getDefOptsSwfJpeg(){
		return defOptsSwfJpeg;
	}
	public static String getDefOptsJpegMp4(){
		return defOptsJpegMp4;
	}
	public static String getDefOptsMix(){
		return defOptsMix;
	}
	public boolean getDefOptsSaveThumbinfoMetadata(){
		return defOptsSaveThumbinfoMetadata;
	}

	static final String PROP_FILE = "."+File.separator+"saccubus.xml";
	static final String PROP_MAILADDR = "MailAddress";
	static final String PROP_PASSWORD = "Password";
	static final String PROP_SAVE_VIDEO = "SaveVideoFile";
	static final String PROP_VIDEO_FILE = "VideoFile";
	static final String PROP_SAVE_COMMENT = "SaveCommentFile";
	static final String PROP_COMMENT_FILE = "CommentFile";
	static final String PROP_SAVE_CONVERTED = "SaveConvertedFile";
	static final String PROP_CONVERTED_FILE = "ConvertedFile";
	static final String PROP_FFMPEG_PATH = "FFnpegPath";
	static final String PROP_VHOOK_PATH = "VhookPath";
	static final String PROP_FONT_PATH = "FontPath";
	static final String PROP_FONT_INDEX = "FontIndex";
	static final String PROP_CMDLINE_EXT = "CMD_EXT";
	static final String PROP_CMDLINE_MAIN = "CMD_MAIN";
	static final String PROP_CMDLINE_IN = "CMD_IN";
	static final String PROP_CMDLINE_OUT = "CMD_OUT";
	static final String PROP_BACK_COMMENT = "BackComment";
	static final String PROP_SHOW_VIDEO = "ShowVideo";
	static final String PROP_SHOW_COMMENT = "ShowCommentNum";
	static final String PROP_VIDEO_FIX_FILE_NAME = "VideoFixFileName";
	static final String PROP_DEL_VIDEO_AFTER_CONV = "DeleteVideoAfterConv";
	static final String PROP_VIDEO_FIX_FILE_NAME_FOLDER = "VideoFixFileNameFolder";
	static final String PROP_DEL_COMMENT_AFTER_CONV = "DeleteCommentAfterConv";
	static final String PROP_COMMENT_FIX_FILE_NAME = "CommentFixFileName";
	static final String PROP_COMMENT_FIX_FILE_NAME_FOLDER = "CommentFixFileNameFolder";
	static final String PROP_NOT_ADD_VIDEOID_CONV = "NotAddVideoIDtoConverted";
	static final String PROP_CONV_FIX_FILE_NAME = "ConvFixFileName";
	static final String PROP_CONV_FIX_FILE_NAME_FOLDER = "ConvFixFileNameFolder";
	static final String PROP_NG_WORD = "NG_Word";
	static final String PROP_NG_ID = "NG_ID";
	static final String PROP_USE_PROXY = "UseProxy";
	static final String PROP_PROXY = "Proxy";
	static final String PROP_PROXY_PORT = "ProxyPort";
	static final String PROP_FIX_FONT_SIZE = "FixFontSize";
	static final String PROP_FIX_COMMENT_NUM = "FixCommentSize";
	static final String PROP_OPAQUE_COMMENT = "OpaqueComment";
	static final String PROP_OPTION_FILE = "OptionFile";
	static final String PROP_DISABLE_VHOOK = "VhookDisabled";
	static final String PROP_SHADOW_INDEX = "ShadowIndex";

	/*
	 * ここから拡張設定 1.22r3 に対する
	 */
	static final String PROP_CONV_WITH_COMMENT = "AddComment";	//"ConvertWithComment";
	static final String PROP_SAVE_OWNERCOMMENT = "TCDownload";	//"SaveOwnerComment";
	static final String PROP_OWNERCOMMENT_FILE = "TCFileName";	//"OwnerCommentFile";
	static final String PROP_CONV_WITH_OWNERCOMMENT = "AddTcomment"; //"ConvertWithOwnerComment"
	@SuppressWarnings("unused")
	private static final String PROP_DEL_OWNERCOMMENT_AFTER_CONV = "TCDelete";	// No use
	@SuppressWarnings("unused")
	private static final String PROP_OWNERCOMMENT_FIX_FILE_NAME = "TCAutoNaming";	// No use
	@SuppressWarnings("unused")
	private static final String PROP_OWNERCOMMENT_FOLDER = "TCDirectory";	// No use [out]tcomment
	@SuppressWarnings("unused")
	private static final String PROP_TEMP_DIR = "TempDir";					// No use .
	@SuppressWarnings("unused")
	private static final String PROP_NICO_BROWSER = "NicoBrowserFileName";	// No use
	static final String PROP_ADD_TIMESTAMP = "AddTimeStamp";
		// Add Timestamp to comment filename, when using multiple comment files as wayback logs.
	static final String PROP_ADD_OPTION_CONV_VIDEO = "AddOptionToConvertedVideo";
		// Make subfolder of video titile name and Add FFmpeg option to converted video filename,
	static final String PROP_HISTORY1= "History1";
	static final String PROP_VHOOK_WIDE_PATH = "VhookWidePath";
	static final String PROP_USE_VHOOK = "UseVhookNormal";
	static final String PROP_USE_VHOOK_WIDE = "UseVhookWide";
	static final String PROP_BROWSER_IE = "ShareBrowserIE";
	static final String PROP_FIREFOX = "ShareFirefox";
	static final String PROP_CHROME = "ShareChrome";
	static final String PROP_CHROMIUM = "ShareChromium";
	static final String PROP_OPERA = "ShareOpera";
	static final String PROP_USE_COOKIE_PATH = "UseCookiePath";
	static final String PROP_BROWSER_COOKIE_PATH = "BrowserCookiePath";
	static final String PROP_OPTION_FOLDER = "OptionFolder";
	static final String PROP_WIDE_OPTION_FILE = "WideOptionFile";
	static final String PROP_WIDE_CMDLINE_EXT = "WideCMD_EXT";
	static final String PROP_WIDE_CMDLINE_MAIN = "WideCMD_MAIN";
	static final String PROP_WIDE_CMDLINE_IN = "WideCMD_IN";
	static final String PROP_WIDE_CMDLINE_OUT = "WideCMD_OUT";
	static final String PROP_OPTIONAL_TRANSLUCENT = "OptionalTranslucent";	// Optional_threadを半透明にする
	static final String PROP_FONT_HEIGHT_FIX = "FontHeightFix";
	static final String PROP_FONT_HEIGHT_FIX_RAITO = "FontHeightFixRaito";
	static final String PROP_DISABLE_ORIGINAL_RESIZE = "DisableOriginalResize";
	static final String PROP_COMMENT_MODE_INDEX = "CommentMode";
	static final String PROP_SET_COMMENT_SPEED = "SetCommentSpeed";
	static final String PROP_COMMENT_SPEED = "CommentSpeed";
	static final String PROP_ENABLE_CA = "EnableCA";
	static final String PROP_SCORE_LIMIT = "CommentScoreLimit";
	static final String PROP_DISABLE_ECO = "DisableEco";
	static final String PROP_FONT_WIDTH_FIX = "FontWidthFix";
	static final String PROP_FONT_WIDTH_FIX_RATIO = "FontWidthFixRatio";
	static final String PROP_USE_LINESKIP = "UseLineskipAsFontsize";
	static final String PROP_USE_EXTRA_FONT = "UseExtraFont";
	static final String PROP_EXTRA_FONT_TEXT = "ExtraFontText";
	static final String PROP_NG_COMMAND = "NGCommand";
	static final String PROP_REPLACE_COMMAND = "ReplaceCommand";
	static final String PROP_ENCRYPT_PASS = "EncryptedPassword";
	static final String PROP_EXTRA_MODE = "ExtraMode";
	static final String PROP_ADD_OPTION = "AddOption";
	static final String PROP_WIDE_ADD_OPTION = "WideAddOption";
	static final String PROP_SAVE_WATCH_PAGE = "SaveWatchPage";
	static final String PROP_SAVE_THUMB_INFO = "SaveThumbInfo";
	static final String PROP_USER_FOLDER = "UseFolder";
	static final String PROP_SAVE_THUMB_USER = "SaveThumbUser";
	static final String PROP_SAVE_THUMB_AS_TEXT = "SaveThumbAsText";
	static final String PROP_CHANGE_MP4_EXT = "ChangeMp4Ext";
	static final String PROP_CHANGE_TITLE_ID = "ChangeTitleId";
	static final String PROP_SAVE_THUMBNAIL_JPG = "SaveThumbnailJpg";
	static final String PROP_ZQ_PLAYER = "QPlayerMode";
	static final String PROP_ZQ_VHOOK_PATH = "QVhookPath";
	static final String PROP_ZQ_OPTION_FILE = "QOptionFile";
	static final String PROP_ZQ_CMDLINE_EXT = "QCMD_EXT";
	static final String PROP_ZQ_CMDLINE_MAIN = "QCMD_MAIN";
	static final String PROP_ZQ_CMDLINE_IN = "QCMD_IN";
	static final String PROP_ZQ_CMDLINE_OUT = "QCMD_OUT";
	static final String PROP_ZQ_ADD_OPTION = "QAddOption";
	static final String PROP_OPAQUE_RATE = "OpaqueRate";
	static final String PROP_SWF_3PATH = "SwfTo3Path";
	static final String PROP_CHECK_FPS = "CheckFps";
	static final String PROP_FPS_UP = "FpsUp";
	static final String PROP_FPS_MIN = "FpsMin";
	static final String PROP_SOUNDONLY = "SoundOnly";
	static final String PROP_THUMBNIAL = "DefaultThumbFile";
	static final String PROP_SAVE_AUTOLIST = "SaveAutoList";
	static final String PROP_USE_FPS_FILTER = "UseFpsFilter";
	static final String PROP_AUTO_PLAY = "AutoPlay";
	static final String PROP_LIVE_OPERATION = "LiveOperationComment";
	static final String PROP_PREMIUM_COLOR_CHECK = "PremiumColorCheck";
	static final String PROP_OPTION_FILE_DESCR = "OptionFileDescr";
	static final String PROP_APPEND_COMMENT = "AppendComment";
	static final String PROP_APPEND_NOTICE = "AppendNotice";
	static final String PROP_NUM_THREAD = "MaxNumberOfThreads";
	static final String PROP_NUM_DOWNLOAD = "MaxNumberOfDownload";
	static final String PROP_PENDING_MODE = "PendingMode";
	static final String PROP_ONE_LINE_MODE = "OneLineMode";
	static final String PROP_ERROR_LIST = "ErrorList";
	static final String PROP_LIVE_CONVERT_SETTING = "LiveConvertSetting";
	static final String PROP_FORCE_LIVE_COMMENT = "ForceLiveComment";
	static final String PROP_LIVE_OPERATION_DURATION = "LiveOperationDuration";
	static final String PROP_ENABLE_COMMENT_VPOS_SHIFT = "EnableCommentVposShift";
	static final String PROP_COMMENT_VPOS_SHIFT = "CommentVposShiftDuration";
	static final String PROP_PREFER_SMILE = "PreferSmileServer";
	// 保存するがGUIでは変更しない
	public static final String PROP_OPTS_FPSUP = "OutOptionFpsUp";
	public static final String PROP_OPTS_SWF_JPEG = "OutOptionSwfJpeg";
	public static final String PROP_OPTS_JPEG_MP4 = "OutOptionJpegMp4";
	public static final String PROP_OPTS_MIX = "OutOptionMix";
	public static final String PROP_OPTS_SAVE_THUMBINFO_METADATA = "SaveThumbinfoMetadata";
	/*
	 * ここまで拡張設定 1.22r3 に対する
	 */

	public static void saveSetting(ConvertingSetting setting, String propFile) {
		Properties prop = setProperty(setting);
		try {
			prop.storeToXML(new FileOutputStream(propFile),
				"settings-"+new Date().toString()+"-Rev"+MainFrame_AboutBox.rev);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static Properties setProperty(ConvertingSetting setting){
		Properties prop = new Properties();
		String user = setting.getMailAddress();
		String password = setting.getPassword();
		String encrypt_pass = setting.getEncryptPass();
		if(user == null) user = "";
		if(password == null) password = "";
		if(encrypt_pass == null) encrypt_pass = "";
		if (!user.isEmpty() && !password.isEmpty() && encrypt_pass.isEmpty()){
			// パスワードを暗号化する
			Key skey = Encryption.makeKey(128,user);
			String try_encryption = Encryption.encode(password, skey);
			if (Encryption.decode(try_encryption, skey).equals(password)){
				//復号確認OK
				password = "#";
				encrypt_pass = "_" + try_encryption;
				System.out.println("パスワードは暗号化されました　 Entry:" + PROP_ENCRYPT_PASS);
			} else {
				System.out.println("パスワード暗号化失敗");
			}
		}else if(encrypt_pass.isEmpty()) {
			System.out.println("メールアドレスが無効なため、パスワードを暗号化しません");
		}
		prop.setProperty(PROP_MAILADDR, user);
		prop.setProperty(PROP_PASSWORD, password);
		prop.setProperty(PROP_ENCRYPT_PASS, encrypt_pass);
		prop.setProperty(PROP_SAVE_VIDEO, Boolean.toString(setting
			.isSaveVideo()));
		prop.setProperty(PROP_VIDEO_FILE, setting.getVideoFile().getPath());
		prop.setProperty(PROP_SAVE_COMMENT, Boolean.toString(setting
			.isSaveComment()));
		prop.setProperty(PROP_COMMENT_FILE, setting.getCommentFile()
			.getPath());
		prop.setProperty(PROP_SAVE_CONVERTED, Boolean.toString(setting
			.isSaveConverted()));
		prop.setProperty(PROP_SHOW_COMMENT, setting.getVideoShowNum());
		prop.setProperty(PROP_CONVERTED_FILE, setting.getConvertedVideoFile()
			.getPath());
		prop.setProperty(PROP_FFMPEG_PATH, setting.getFFmpegPath());
		prop.setProperty(PROP_VHOOK_PATH, setting.getVhookPath());
		prop.setProperty(PROP_FONT_PATH, setting.getFontPath());
		prop.setProperty(PROP_FONT_INDEX, setting.getFontIndex());
		prop.setProperty(PROP_CMDLINE_EXT, setting.getCmdLineOptionExt());
		prop.setProperty(PROP_CMDLINE_MAIN, setting.getCmdLineOptionMain());
		prop.setProperty(PROP_CMDLINE_IN, setting.getCmdLineOptionIn());
		prop.setProperty(PROP_CMDLINE_OUT, setting.getCmdLineOptionOut());
		prop.setProperty(PROP_BACK_COMMENT, setting.getBackComment());
		prop.setProperty(PROP_SHOW_VIDEO, Boolean.toString(setting
			.isVhook_ShowConvertingVideo()));
		prop.setProperty(PROP_DEL_VIDEO_AFTER_CONV, Boolean.toString(setting
			.isDeleteVideoAfterConverting()));
		prop.setProperty(PROP_VIDEO_FIX_FILE_NAME, Boolean.toString(setting
			.isVideoFixFileName()));
		prop.setProperty(PROP_VIDEO_FIX_FILE_NAME_FOLDER, setting
			.getVideoFixFileNameFolder().getPath());
		prop.setProperty(PROP_DEL_COMMENT_AFTER_CONV, Boolean.toString(setting
			.isDeleteCommentAfterConverting()));
		prop.setProperty(PROP_COMMENT_FIX_FILE_NAME, Boolean.toString(setting
			.isCommentFixFileName()));
		prop.setProperty(PROP_COMMENT_FIX_FILE_NAME_FOLDER, setting
			.getCommentFixFileNameFolder().getPath());
		prop.setProperty(PROP_NOT_ADD_VIDEOID_CONV, Boolean.toString(setting
			.isNotAddVideoID_Conv()));
		prop.setProperty(PROP_CONV_FIX_FILE_NAME, Boolean.toString(setting
			.isConvFixFileName()));
		prop.setProperty(PROP_CONV_FIX_FILE_NAME_FOLDER, setting
			.getConvFixFileNameFolder().getPath());
		prop.setProperty(PROP_NG_WORD, setting.getNG_Word());
		prop.setProperty(PROP_NG_ID, setting.getNG_ID());
		prop.setProperty(PROP_USE_PROXY, Boolean.toString(setting
			.useProxy()));
		prop.setProperty(PROP_PROXY, setting.getProxy());
		prop.setProperty(PROP_PROXY_PORT, Integer.toString(setting
			.getProxyPort()));
		prop.setProperty(PROP_FIX_FONT_SIZE, Boolean.toString(setting
			.isFixFontSize()));
		prop.setProperty(PROP_FIX_COMMENT_NUM, Boolean.toString(setting
			.isFixCommentNum()));
		prop.setProperty(PROP_OPAQUE_COMMENT, Boolean.toString(setting
			.isOpaqueComment()));
		if (setting.getOptionFile() != null) {
			prop.setProperty(PROP_OPTION_FILE, setting.getOptionFile()
				.getPath());
		}
		prop.setProperty(PROP_DISABLE_VHOOK, Boolean.toString(setting
			.isVhookDisabled()));
		prop.setProperty(PROP_SHADOW_INDEX, Integer.toString(setting.getShadowIndex()));
		/*
		 * ここから拡張設定保存 1.22r3 に対する
		 */
		prop.setProperty(PROP_CONV_WITH_COMMENT, Boolean.toString(setting
				.isConvertWithComment()));
		prop.setProperty(PROP_SAVE_OWNERCOMMENT, Boolean.toString(setting
			.isSaveOwnerComment()));
		prop.setProperty(PROP_OWNERCOMMENT_FILE, setting
			.getOwnerCommentFile().getPath());
		prop.setProperty(PROP_CONV_WITH_OWNERCOMMENT, Boolean.toString(setting
			.isConvertWithOwnerComment()));
		prop.setProperty(PROP_ADD_TIMESTAMP, Boolean.toString(setting
			.isAddTimeStamp()));
		prop.setProperty(PROP_ADD_OPTION_CONV_VIDEO,  Boolean.toString(
			setting.isAddOption_ConvVideoFile()));
		prop.setProperty(PROP_HISTORY1, setting.lastHistory());
		prop.setProperty(PROP_VHOOK_WIDE_PATH, setting.getVhookWidePath());
		prop.setProperty(PROP_USE_VHOOK,
				Boolean.toString(setting.isUseVhookNormal()));
		prop.setProperty(PROP_USE_VHOOK_WIDE,
				Boolean.toString(setting.isUseVhookWide()));
		prop.setProperty(PROP_BROWSER_IE,
				Boolean.toString(setting.isBrowserIE()));
		prop.setProperty(PROP_FIREFOX,
				Boolean.toString(setting.isBrowserFF()));
		prop.setProperty(PROP_CHROME,
				Boolean.toString(setting.isBrowserChrome()));
		prop.setProperty(PROP_CHROMIUM,
				Boolean.toString(setting.isBrowserChromium()));
		prop.setProperty(PROP_OPERA,
				Boolean.toString(setting.isBrowserOpera()));
		prop.setProperty(PROP_USE_COOKIE_PATH,
				Boolean.toString(setting.isBrowserOther()));
		prop.setProperty(PROP_BROWSER_COOKIE_PATH, setting.getBrowserCookiePath());
		prop.setProperty(PROP_OPTION_FOLDER,
				setting.getOptionFolder());
		if (setting.getWideOptionFile() != null) {
			prop.setProperty(PROP_WIDE_OPTION_FILE, setting.getWideOptionFile()
				.getPath());
		}
		prop.setProperty(PROP_WIDE_CMDLINE_EXT, setting.getWideCmdLineOptionExt());
		prop.setProperty(PROP_WIDE_CMDLINE_MAIN, setting.getWideCmdLineOptionMain());
		prop.setProperty(PROP_WIDE_CMDLINE_IN, setting.getWideCmdLineOptionIn());
		prop.setProperty(PROP_WIDE_CMDLINE_OUT, setting.getWideCmdLineOptionOut());
		prop.setProperty(PROP_OPTIONAL_TRANSLUCENT, Boolean.toString(setting.isOptionalTranslucent()));
		prop.setProperty(PROP_FONT_HEIGHT_FIX, Boolean.toString(setting.isFontHeightFix()));
		prop.setProperty(PROP_FONT_HEIGHT_FIX_RAITO,setting.getFontHeightFixRaito());
		prop.setProperty(PROP_DISABLE_ORIGINAL_RESIZE, Boolean.toString(setting.isDisableOriginalResize()));
		prop.setProperty(PROP_COMMENT_MODE_INDEX, Integer.toString(setting.getCommentIndex()));
		prop.setProperty(PROP_SET_COMMENT_SPEED, Boolean.toString(setting.isSetCommentSpeed()));
		prop.setProperty(PROP_COMMENT_SPEED, setting.getCommentSpeed());
		prop.setProperty(PROP_ENABLE_CA, Boolean.toString(setting.isEnableCA()));
		prop.setProperty(PROP_SCORE_LIMIT, "" + setting.getScoreLimit());
		prop.setProperty(PROP_DISABLE_ECO, Boolean.toString(setting.isDisableEco()));
		prop.setProperty(PROP_FONT_WIDTH_FIX, Boolean.toString(setting.isFontWidthFix()));
		prop.setProperty(PROP_FONT_WIDTH_FIX_RATIO, setting.getFontWidthFixRaito());
		prop.setProperty(PROP_USE_LINESKIP, Boolean.toString(setting.isUseLineSkip()));
		prop.setProperty(PROP_USE_EXTRA_FONT,Boolean.toString(setting.isUseExtraFont()));
		prop.setProperty(PROP_EXTRA_FONT_TEXT, setting.getExtraFontText());
		prop.setProperty(PROP_NG_COMMAND, setting.getNGCommand());
		prop.setProperty(PROP_REPLACE_COMMAND, setting.getReplaceCommand());
		prop.setProperty(PROP_EXTRA_MODE, setting.getExtraMode());
		prop.setProperty(PROP_ADD_OPTION, setting.getAddOption());
		prop.setProperty(PROP_WIDE_ADD_OPTION, setting.getWideAddOption());
		prop.setProperty(PROP_SAVE_WATCH_PAGE, Boolean.toString(setting.isSaveWatchPage()));
		prop.setProperty(PROP_SAVE_THUMB_INFO, Boolean.toString(setting.isSaveThumbInfo()));
		prop.setProperty(PROP_USER_FOLDER, setting.getUserFolder());
		prop.setProperty(PROP_SAVE_THUMB_USER, Boolean.toString(setting.isSaveThumbUser()));
		prop.setProperty(PROP_SAVE_THUMB_AS_TEXT, Boolean.toString(setting.isSaveThumbInfoAsText()));
		prop.setProperty(PROP_CHANGE_MP4_EXT, Boolean.toString(setting.isChangeMp4Ext()));
		prop.setProperty(PROP_CHANGE_TITLE_ID, Boolean.toString(setting.isChangeTitleId()));
		prop.setProperty(PROP_SAVE_THUMBNAIL_JPG, Boolean.toString(setting.isSaveThumbnailJpg()));
		prop.setProperty(PROP_ZQ_PLAYER, Boolean.toString(setting.isZqPlayer()));
		prop.setProperty(PROP_ZQ_VHOOK_PATH, setting.getZqVhookPath());
		if (setting.getZqOptionFile() != null) {
			prop.setProperty(PROP_ZQ_OPTION_FILE, setting.getZqOptionFile()
				.getPath());
		}
		prop.setProperty(PROP_ZQ_CMDLINE_EXT, setting.getZqCmdLineOptionExt());
		prop.setProperty(PROP_ZQ_CMDLINE_MAIN, setting.getZqCmdLineOptionMain());
		prop.setProperty(PROP_ZQ_CMDLINE_IN, setting.getZqCmdLineOptionIn());
		prop.setProperty(PROP_ZQ_CMDLINE_OUT, setting.getZqCmdLineOptionOut());
		prop.setProperty(PROP_ZQ_ADD_OPTION, setting.getZqAddOption());
		prop.setProperty(PROP_OPAQUE_RATE,setting.getOpaqueRate());
		prop.setProperty(PROP_SWF_3PATH, Boolean.toString(setting.isSwfTo3Path()));
		prop.setProperty(PROP_CHECK_FPS, Boolean.toString(setting.enableCheckFps()));
		prop.setProperty(PROP_FPS_UP, Double.toString(setting.getFpsUp()));
		prop.setProperty(PROP_FPS_MIN, Double.toString(setting.getFpsMin()));
		prop.setProperty(PROP_SOUNDONLY, Boolean.toString(setting.canSoundOnly()));
		prop.setProperty(PROP_THUMBNIAL, setting.getDefaultThumbnail());
		prop.setProperty(PROP_SAVE_AUTOLIST, Boolean.toString(setting.isSaveAutoList()));
		prop.setProperty(PROP_USE_FPS_FILTER, Boolean.toString(setting.isUseFpsFilter()));
		prop.setProperty(PROP_AUTO_PLAY, Boolean.toString(setting.isAutoPlay()));
		prop.setProperty(PROP_LIVE_OPERATION, Boolean.toString(setting.isLiveOperationConversion()));
		prop.setProperty(PROP_PREMIUM_COLOR_CHECK, Boolean.toString(setting.isPremiumColorCheck()));
		prop.setProperty(PROP_OPTION_FILE_DESCR, "");
		prop.setProperty(PROP_APPEND_COMMENT, Boolean.toString(setting.isAppendComment()));
		prop.setProperty(PROP_NUM_THREAD, Integer.toString(setting.getNumThread()));
		prop.setProperty(PROP_APPEND_NOTICE, setting.getAppendNotice());
		prop.setProperty(PROP_NUM_DOWNLOAD, Integer.toString(setting.getNumDownload()));
		prop.setProperty(PROP_PENDING_MODE, Boolean.toString(setting.isPendingMode()));
		prop.setProperty(PROP_ONE_LINE_MODE, Boolean.toString(setting.isOneLineMode()));
		prop.setProperty(PROP_ERROR_LIST, setting.getErrorList());
		prop.setProperty(PROP_LIVE_CONVERT_SETTING, Boolean.toString(setting.changedLiveOperationDuration()));
		prop.setProperty(PROP_LIVE_OPERATION_DURATION, setting.getLiveOperationDuration());
		prop.setProperty(PROP_FORCE_LIVE_COMMENT, Boolean.toString(setting.isForcedLiveComment()));
		prop.setProperty(PROP_ENABLE_COMMENT_VPOS_SHIFT, Boolean.toString(setting.isEnableCommentVposShift()));
		prop.setProperty(PROP_COMMENT_VPOS_SHIFT, setting.getCommentVposShiftString());
		prop.setProperty(PROP_PREFER_SMILE, Boolean.toString(setting.isSmilePreferable()));
		//GUIなし ini初期値あり
		prop.setProperty(PROP_OPTS_FPSUP, defOptsFpsUp);
		prop.setProperty(PROP_OPTS_JPEG_MP4, defOptsJpegMp4);
		prop.setProperty(PROP_OPTS_MIX, defOptsMix);
		prop.setProperty(PROP_OPTS_SWF_JPEG, defOptsSwfJpeg);
		prop.setProperty(PROP_OPTS_SAVE_THUMBINFO_METADATA, Boolean.toString(defOptsSaveThumbinfoMetadata));
		/*
		 * ここまで拡張設定保存 1.22r3 に対する
		 */
		return prop;
	}

	public String lastHistory() {
		return getRequestHistory();
	}

	public static void saveSetting(ConvertingSetting setting) {
		saveSetting(setting, PROP_FILE);
	}

	public static Properties loadProperty(String propFile,boolean warnFileNotFind){
		Properties prop = new Properties();
		try {
			prop.loadFromXML(new FileInputStream(propFile));
		} catch(FileNotFoundException e1){
			if(warnFileNotFind){
				e1.printStackTrace();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return prop;
	}

	public static ConvertingSetting addSetting(ConvertingSetting setting, String propFile) {
		Properties setProp = setProperty(setting);
		Properties addProp = loadProperty(propFile, true);
		for(Entry<Object, Object> e : addProp.entrySet()){
			String key = (String)e.getKey();
			String value = (String)e.getValue();
			setProp.put(key, value);
			//System.out.println(key.toString()+"="+value.toString());
		}
		return loadSetting(null, null, setProp);
	}

	public static ConvertingSetting loadSetting(String user, String password, String propFile) {
	    return loadSetting(user, password, propFile, true);
	}

	public static ConvertingSetting loadSetting(String user,
			String password, String propFile, boolean warnFileNotFind) {
		Properties prop = new Properties();
		try {
			prop.loadFromXML(new FileInputStream(propFile));
		} catch(FileNotFoundException e1){
			if(warnFileNotFind){
				e1.printStackTrace();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return loadSetting(user, password, prop);
	}

	public static ConvertingSetting loadSetting(String user,
			String password, Properties prop) {
		if (user == null) {
			user = prop.getProperty(PROP_MAILADDR, "");
		}
		String encrypt_pass = prop.getProperty(PROP_ENCRYPT_PASS, "");
		if (password == null) {
			password = prop.getProperty(PROP_PASSWORD, "");
			if (!user.isEmpty() && password.startsWith("#")){
				//encrypt_pass = prop.getProperty(PROP_ENCRYPT_PASS, "");
				if (encrypt_pass.startsWith("_")){
					// 暗号化パスワードを復号する
					encrypt_pass = encrypt_pass.substring(1);
					Key skey = Encryption.makeKey(128, user);
					password = Encryption.decode(encrypt_pass, skey);
					String try_encryption = Encryption.encode(password, skey);
					if (try_encryption.equals(encrypt_pass)){
						System.out.println("パスワードは復号されました");
						encrypt_pass = "";
					}else{
						System.out.println("パスワード復号失敗");
						password = "";
						encrypt_pass = "";
					}
				} else {
					password = encrypt_pass;
					encrypt_pass = "#";
					System.out.println("パスワードは復号されません");
				}
			} else {
				// password encrypt_pass そのまま。更新はpasswordを暗号化する
				System.out.println("パスワードは暗号化されていません");
				if (encrypt_pass.isEmpty()){
					System.out.println("終了時パスワードが暗号化されます");
				}
			}
		} else {
			if (password.startsWith("_")){
				encrypt_pass = password;
				password = "!";
			}
			if (password.startsWith("!") && encrypt_pass.startsWith("_")){
				Key skey = Encryption.makeKey(128, user);
				String try_decode = Encryption.decode(encrypt_pass.substring(1), skey);
				if (Encryption.encode(try_decode, skey).equals(encrypt_pass.substring(1))){
					password = try_decode;
					System.out.println("パスワードは復号されました");
				}else{
					System.out.println("パスワード復号失敗");
				}
			}
		}
		String option_file_name = prop.getProperty(PROP_OPTION_FILE, null);
		defOptsFpsUp = prop.getProperty(PROP_OPTS_FPSUP, DEF_OPTS_FPSUP);
		defOptsSwfJpeg = prop.getProperty(PROP_OPTS_SWF_JPEG, DEF_OPTS_SWF_JPEG);
		defOptsJpegMp4 = prop.getProperty(PROP_OPTS_JPEG_MP4, DEF_OPTS_JPEG_MP4);
		defOptsMix = prop.getProperty(PROP_OPTS_MIX, DEF_OPTS_MIX);
		defOptsSaveThumbinfoMetadata = Boolean.valueOf(prop.getProperty(
			PROP_OPTS_SAVE_THUMBINFO_METADATA,Boolean.toString(DEF_OPTS_SAVE_THUMBINFO_METADATA)));

		File option_file = null;
		if (option_file_name != null) {
			option_file = new File(option_file_name);
		}
		option_file_name = prop.getProperty(PROP_WIDE_OPTION_FILE, null);
		File wide_option_file = null;
		if (option_file_name != null) {
			wide_option_file = new File(option_file_name);
		}
		File zq_option_file = null;
		option_file_name = prop.getProperty(PROP_ZQ_OPTION_FILE, null);
		if (option_file_name != null) {
			zq_option_file = new File(option_file_name);
		}
		String win_dir = System.getenv("windir");
		if(!win_dir.endsWith(File.separator)){
			win_dir = win_dir+File.separator;
		}
		return new ConvertingSetting(
			user,
			password,
			Boolean.parseBoolean(prop.getProperty(PROP_SAVE_VIDEO, "true")),
			prop.getProperty(PROP_VIDEO_FILE, "./video.flv"),
			Boolean.parseBoolean(prop.getProperty(PROP_SAVE_COMMENT, "true")),
			Boolean.parseBoolean(prop.getProperty(PROP_ADD_TIMESTAMP, "false")),
			prop.getProperty(PROP_COMMENT_FILE, "./comment.xml"),
			Boolean.parseBoolean(prop.getProperty(PROP_SAVE_OWNERCOMMENT, "false")),	// false<-true 1.22r3e8
			prop.getProperty(PROP_OWNERCOMMENT_FILE, "./comment" + ConvertWorker.OWNER_EXT),
			Boolean.parseBoolean(prop.getProperty(PROP_SAVE_CONVERTED, "true")),
			Boolean.parseBoolean(prop.getProperty(PROP_CONV_WITH_COMMENT,"true")),
			Boolean.parseBoolean(prop.getProperty(PROP_CONV_WITH_OWNERCOMMENT,"false")),	// false<-true 1.22r3e8
			prop.getProperty(PROP_CONVERTED_FILE, "./video.avi"),
			prop.getProperty(PROP_SHOW_COMMENT, "40"),
			prop.getProperty(PROP_FFMPEG_PATH,DEFAULT_FFMPEG_PATH),
			prop.getProperty(PROP_VHOOK_PATH,DEFAULT_VHOOK_PATH),
			prop.getProperty(PROP_CMDLINE_EXT, "avi"),
			prop.getProperty(PROP_CMDLINE_MAIN,""),
			prop.getProperty(PROP_CMDLINE_IN, ""),
			prop.getProperty(PROP_CMDLINE_OUT,DEFAULT_CMDLINE_OUT),
			prop.getProperty(PROP_BACK_COMMENT, "500"),
			prop.getProperty(PROP_FONT_PATH, win_dir+"Fonts/msgothic.ttc"),
			Integer.parseInt(prop.getProperty(PROP_FONT_INDEX, "1")),
			Boolean.parseBoolean(prop.getProperty(PROP_SHOW_VIDEO, "true")),
			Boolean.parseBoolean(prop.getProperty(PROP_DEL_VIDEO_AFTER_CONV, "false")),
			Boolean.parseBoolean(prop.getProperty(PROP_VIDEO_FIX_FILE_NAME, "true")),
			prop.getProperty(PROP_VIDEO_FIX_FILE_NAME_FOLDER,"./[out]video/"),
			Boolean.parseBoolean(prop.getProperty(PROP_DEL_COMMENT_AFTER_CONV, "false")),
			Boolean.parseBoolean(prop.getProperty(PROP_COMMENT_FIX_FILE_NAME, "true")),
			prop.getProperty(PROP_COMMENT_FIX_FILE_NAME_FOLDER, "./[out]comment/"),
			Boolean.parseBoolean(prop.getProperty(PROP_NOT_ADD_VIDEOID_CONV, "false")),
			Boolean.parseBoolean(prop.getProperty(PROP_CONV_FIX_FILE_NAME,"true")),
			prop.getProperty(PROP_CONV_FIX_FILE_NAME_FOLDER, "./[out]converted/"),
			prop.getProperty(PROP_NG_WORD, ""),
			prop.getProperty(PROP_NG_ID, ""),
			Boolean.parseBoolean(prop.getProperty(PROP_USE_PROXY, "false")),
			prop.getProperty(PROP_PROXY,""),
			Integer.parseInt(prop.getProperty(PROP_PROXY_PORT,"-1")),
			Boolean.parseBoolean(prop.getProperty(PROP_FIX_FONT_SIZE, "true")),
			Boolean.parseBoolean(prop.getProperty(PROP_FIX_COMMENT_NUM, "true")),
			Boolean.parseBoolean(prop.getProperty(PROP_OPAQUE_COMMENT,"false")),
			option_file,
			Boolean.parseBoolean(prop.getProperty(PROP_DISABLE_VHOOK,"false")),
			Integer.parseInt(prop.getProperty(PROP_SHADOW_INDEX,"1"),10),
			Boolean.parseBoolean(prop.getProperty(PROP_ADD_OPTION_CONV_VIDEO, "false")),
			prop.getProperty(PROP_HISTORY1, ""),
			prop.getProperty(PROP_VHOOK_WIDE_PATH,DEFAULT_VHOOK_PATH),
			Boolean.parseBoolean(prop.getProperty(PROP_USE_VHOOK,"true")),
			Boolean.parseBoolean(prop.getProperty(PROP_USE_VHOOK_WIDE,"true")),
			Boolean.parseBoolean(prop.getProperty(PROP_BROWSER_IE, "false")),
			Boolean.parseBoolean(prop.getProperty(PROP_FIREFOX, "false")),
			Boolean.parseBoolean(prop.getProperty(PROP_CHROME, "false")),
			Boolean.parseBoolean(prop.getProperty(PROP_CHROMIUM, "false")),
			Boolean.parseBoolean(prop.getProperty(PROP_OPERA, "false")),
			Boolean.parseBoolean(prop.getProperty(PROP_USE_COOKIE_PATH, "false")),
			prop.getProperty(PROP_BROWSER_COOKIE_PATH,"他のブラウザCookieのファイル/フォルダを指定"),
			prop.getProperty(PROP_OPTION_FOLDER, DEFAULT_OPTION_FOLDER),
			wide_option_file,
			prop.getProperty(PROP_WIDE_CMDLINE_EXT, "mp4"),
			prop.getProperty(PROP_WIDE_CMDLINE_MAIN,""),
			prop.getProperty(PROP_WIDE_CMDLINE_IN, ""),
			prop.getProperty(PROP_WIDE_CMDLINE_OUT,DEFAULT_WIDE_CMDLINE_OUT),
			Boolean.parseBoolean(prop.getProperty(PROP_OPTIONAL_TRANSLUCENT, "true")),
			Boolean.parseBoolean(prop.getProperty(PROP_FONT_HEIGHT_FIX,"false")),
			prop.getProperty(PROP_FONT_HEIGHT_FIX_RAITO,""),
			Boolean.parseBoolean(prop.getProperty(PROP_DISABLE_ORIGINAL_RESIZE, "false")),
			Integer.parseInt(prop.getProperty(PROP_COMMENT_MODE_INDEX, "0")),
			Boolean.parseBoolean(prop.getProperty(PROP_SET_COMMENT_SPEED, "false")),
			prop.getProperty(PROP_COMMENT_SPEED, ""),
			Boolean.parseBoolean(prop.getProperty(PROP_ENABLE_CA, "false")),
			Integer.parseInt(prop.getProperty(PROP_SCORE_LIMIT, ""+SharedNgScore.MINSCORE)),
			Boolean.parseBoolean(prop.getProperty(PROP_DISABLE_ECO, "false")),
			Boolean.parseBoolean(prop.getProperty(PROP_FONT_WIDTH_FIX, "false")),
			prop.getProperty(PROP_FONT_WIDTH_FIX_RATIO, ""),
			Boolean.parseBoolean(prop.getProperty(PROP_USE_LINESKIP, "false")),
			Boolean.parseBoolean(prop.getProperty(PROP_USE_EXTRA_FONT, "false")),
			prop.getProperty(PROP_EXTRA_FONT_TEXT, ""),
			prop.getProperty(PROP_NG_COMMAND, ""),
			prop.getProperty(PROP_REPLACE_COMMAND, ""),
			encrypt_pass,
			prop.getProperty(PROP_EXTRA_MODE, ""),
			prop.getProperty(PROP_ADD_OPTION, ""),
			prop.getProperty(PROP_WIDE_ADD_OPTION, ""),
			Boolean.parseBoolean(prop.getProperty(PROP_SAVE_WATCH_PAGE, "false")),
			Boolean.parseBoolean(prop.getProperty(PROP_SAVE_THUMB_INFO, "false")),
			prop.getProperty(PROP_USER_FOLDER, "."+File.separator+"user"),
			Boolean.parseBoolean(prop.getProperty(PROP_SAVE_THUMB_USER, "false")),
			Boolean.parseBoolean(prop.getProperty(PROP_SAVE_THUMB_AS_TEXT, "false")),
			Boolean.parseBoolean(prop.getProperty(PROP_CHANGE_MP4_EXT, "false")),
			Boolean.parseBoolean(prop.getProperty(PROP_CHANGE_TITLE_ID, "false")),
			Boolean.parseBoolean(prop.getProperty(PROP_SAVE_THUMBNAIL_JPG, "false")),
			Boolean.parseBoolean(prop.getProperty(PROP_ZQ_PLAYER, "false")),
			prop.getProperty(PROP_ZQ_VHOOK_PATH,DEFAULT_VHOOK_PATH),
			zq_option_file,
			prop.getProperty(PROP_ZQ_CMDLINE_EXT, "mp4"),
			prop.getProperty(PROP_ZQ_CMDLINE_MAIN,""),
			prop.getProperty(PROP_ZQ_CMDLINE_IN, ""),
			prop.getProperty(PROP_ZQ_CMDLINE_OUT,DEFAULT_ZQ_CMDLINE_OUT),
			prop.getProperty(PROP_ZQ_ADD_OPTION, ""),
			new StringBuffer(),
			prop.getProperty(PROP_OPAQUE_RATE, "1.0"),
			Boolean.parseBoolean(prop.getProperty(PROP_SWF_3PATH, "true")),
			Boolean.parseBoolean(prop.getProperty(PROP_CHECK_FPS, "true")),
			Double.parseDouble(prop.getProperty(PROP_FPS_UP, "25.0")),
			Double.parseDouble(prop.getProperty(PROP_FPS_MIN, "23.0")),
			Boolean.parseBoolean(prop.getProperty(PROP_SOUNDONLY, "false")),
			prop.getProperty(PROP_THUMBNIAL, "<自動>"),
			Boolean.parseBoolean(prop.getProperty(PROP_SAVE_AUTOLIST, "false")),
			Boolean.parseBoolean(prop.getProperty(PROP_USE_FPS_FILTER,"false")),
			Boolean.parseBoolean(prop.getProperty(PROP_AUTO_PLAY,"false")),
			Boolean.parseBoolean(prop.getProperty(PROP_LIVE_OPERATION,"false")),
			Boolean.parseBoolean(prop.getProperty(PROP_PREMIUM_COLOR_CHECK,"false")),
			prop.getProperty(PROP_OPTION_FILE_DESCR, ""),
			Boolean.parseBoolean(prop.getProperty(PROP_APPEND_COMMENT, "false")),
			prop.getProperty(PROP_APPEND_NOTICE, ""),
			Integer.parseInt(prop.getProperty(PROP_NUM_THREAD, "1")),
			Boolean.parseBoolean(prop.getProperty(PROP_PENDING_MODE, "false")),
			Boolean.parseBoolean(prop.getProperty(PROP_ONE_LINE_MODE, "false")),
			prop.getProperty(PROP_ERROR_LIST, ""),
			Boolean.parseBoolean(prop.getProperty(PROP_LIVE_CONVERT_SETTING, "false")),
			Boolean.parseBoolean(prop.getProperty(PROP_FORCE_LIVE_COMMENT, "false")),
			prop.getProperty(PROP_LIVE_OPERATION_DURATION,""),
			Boolean.parseBoolean(prop.getProperty(PROP_ENABLE_COMMENT_VPOS_SHIFT,"false")),
			prop.getProperty(PROP_COMMENT_VPOS_SHIFT, "0.0"),
			Boolean.parseBoolean(prop.getProperty(PROP_PREFER_SMILE, "false"))
		);
	}

	public static ConvertingSetting loadSetting(String user, String password) {
		return ConvertingSetting.loadSetting(user, password, PROP_FILE);
	}

	/**
	 * @param replaceOptions セットする replaceOptions
	 */
	public void setReplaceOptions(Map<String, String> replaceOptions) {
		this.replaceOptions = replaceOptions;
	}
}
