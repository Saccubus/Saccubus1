package saccubus;

import java.util.Date;
import java.util.Properties;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;

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
	public static final String[] ShadowKindArray = {
		"00:なし",
		"01:ニコニコ動画風",
		"02:右下",
		"03:囲い込み"
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
	private String History1;
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
	private boolean debugNicovideo;
	private boolean enableCA;	//仮設定

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
			String history1,
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
			boolean debug_nicovideo,
			boolean enable_CA
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
		History1 = history1;
		VhookWidePath = vhook_wide_path;
		UseVhookNormal = use_vhook_normal;
		UseVhookWide = use_vhook_wide;
		BrowserIE = browserIE;
		BrowserFF = browserFF;
		BrowserChrome = browserChrome;
		BrowserChromium = browserChromium;
		BrowserOpera = browserOpera;
		BrowserOther = browserOther;
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
		debugNicovideo = debug_nicovideo;
		enableCA = enable_CA;
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
	public String getHistory1() {
		return History1;
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
		return debugNicovideo;
	}
	public boolean isEnableCA(){
		return enableCA;
	}

	private static final String PROP_FILE = "./saccubus.xml";
	private static final String PROP_MAILADDR = "MailAddress";
	private static final String PROP_PASSWORD = "Password";
	private static final String PROP_SAVE_VIDEO = "SaveVideoFile";
	private static final String PROP_VIDEO_FILE = "VideoFile";
	private static final String PROP_SAVE_COMMENT = "SaveCommentFile";
	private static final String PROP_COMMENT_FILE = "CommentFile";
	private static final String PROP_SAVE_CONVERTED = "SaveConvertedFile";
	private static final String PROP_CONVERTED_FILE = "ConvertedFile";
	private static final String PROP_FFMPEG_PATH = "FFnpegPath";
	private static final String PROP_VHOOK_PATH = "VhookPath";
	private static final String PROP_FONT_PATH = "FontPath";
	private static final String PROP_FONT_INDEX = "FontIndex";
	private static final String PROP_CMDLINE_EXT = "CMD_EXT";
	private static final String PROP_CMDLINE_MAIN = "CMD_MAIN";
	private static final String PROP_CMDLINE_IN = "CMD_IN";
	private static final String PROP_CMDLINE_OUT = "CMD_OUT";
	private static final String PROP_BACK_COMMENT = "BackComment";
	private static final String PROP_SHOW_VIDEO = "ShowVideo";
	private static final String PROP_SHOW_COMMENT = "ShowCommentNum";
	private static final String PROP_VIDEO_FIX_FILE_NAME = "VideoFixFileName";
	private static final String PROP_DEL_VIDEO_AFTER_CONV = "DeleteVideoAfterConv";
	private static final String PROP_VIDEO_FIX_FILE_NAME_FOLDER = "VideoFixFileNameFolder";
	private static final String PROP_DEL_COMMENT_AFTER_CONV = "DeleteCommentAfterConv";
	private static final String PROP_COMMENT_FIX_FILE_NAME = "CommentFixFileName";
	private static final String PROP_COMMENT_FIX_FILE_NAME_FOLDER = "CommentFixFileNameFolder";
	private static final String PROP_NOT_ADD_VIDEOID_CONV = "NotAddVideoIDtoConverted";
	private static final String PROP_CONV_FIX_FILE_NAME = "ConvFixFileName";
	private static final String PROP_CONV_FIX_FILE_NAME_FOLDER = "ConvFixFileNameFolder";
	private static final String PROP_NG_WORD = "NG_Word";
	private static final String PROP_NG_ID = "NG_ID";
	private static final String PROP_USE_PROXY = "UseProxy";
	private static final String PROP_PROXY = "Proxy";
	private static final String PROP_PROXY_PORT = "ProxyPort";
	private static final String PROP_FIX_FONT_SIZE = "FixFontSize";
	private static final String PROP_FIX_COMMENT_NUM = "FixCommentSize";
	private static final String PROP_OPAQUE_COMMENT = "OpaqueComment";
	private static final String PROP_OPTION_FILE = "OptionFile";
	private static final String PROP_DISABLE_VHOOK = "VhookDisabled";
	private static final String PROP_SHADOW_INDEX = "ShadowIndex";

	/*
	 * ここから拡張設定 1.22r3 に対する
	 */
	private static final String PROP_CONV_WITH_COMMENT = "AddComment";	//"ConvertWithComment";
	private static final String PROP_SAVE_OWNERCOMMENT = "TCDownload";	//"SaveOwnerComment";
	private static final String PROP_OWNERCOMMENT_FILE = "TCFileName";	//"OwnerCommentFile";
	private static final String PROP_CONV_WITH_OWNERCOMMENT = "AddTcomment"; //"ConvertWithOwnerComment"
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
	private static final String PROP_ADD_TIMESTAMP = "AddTimeStamp";
		// Add Timestamp to comment filename, when using multiple comment files as wayback logs.
	private static final String PROP_ADD_OPTION_CONV_VIDEO = "AddOptionToConvertedVideo";
		// Make subfolder of video titile name and Add FFmpeg option to converted video filename,
	private static final String PROP_HISTORY1= "History1";
	private static final String PROP_VHOOK_WIDE_PATH = "VhookWidePath";
	private static final String PROP_USE_VHOOK = "UseVhookNormal";
	private static final String PROP_USE_VHOOK_WIDE = "UseVhookWide";
	private static final String PROP_BROWSER_IE = "ShareBrowserIE";
	private static final String PROP_FIREFOX = "ShareFirefox";
	private static final String PROP_CHROME = "ShareChrome";
	private static final String PROP_CHROMIUM = "ShareChromium";
	private static final String PROP_OPERA = "ShareOpera";
	private static final String PROP_USE_COOKIE_PATH = "UseCookiePath";
	private static final String PROP_BROWSER_COOKIE_PATH = "BrowserCookiePath";
	private static final String PROP_OPTION_FOLDER = "OptionFolder";
	private static final String PROP_WIDE_OPTION_FILE = "WideOptionFile";
	private static final String PROP_WIDE_CMDLINE_EXT = "WideCMD_EXT";
	private static final String PROP_WIDE_CMDLINE_MAIN = "WideCMD_MAIN";
	private static final String PROP_WIDE_CMDLINE_IN = "WideCMD_IN";
	private static final String PROP_WIDE_CMDLINE_OUT = "WideCMD_OUT";
	private static final String PROP_OPTIONAL_TRANSLUCENT = "OptionalTranslucent";	// Optional_threadを半透明にする
	private static final String PROP_FONT_HEIGHT_FIX = "FontHeightFix";
	private static final String PROP_FONT_HEIGHT_FIX_RAITO = "FontHeightFixRaito";
	private static final String PROP_DISABLE_ORIGINAL_RESIZE = "DisableOriginalResize";
	private static final String PROP_COMMENT_MODE_INDEX = "CommentMode";
	private static final String PROP_SET_COMMENT_SPEED = "SetCommentSpeed";
	private static final String PROP_COMMENT_SPEED = "CommentSpeed";
	private static final String PROP_ENABLE_CA = "EnableCA";

	/*
	 * ここまで拡張設定 1.22r3 に対する
	 */

	public static void saveSetting(ConvertingSetting setting) {
		Properties prop = new Properties();
		prop.setProperty(PROP_MAILADDR, setting.getMailAddress());
		prop.setProperty(PROP_PASSWORD, setting.getPassword());
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
		prop.setProperty(PROP_ADD_TIMESTAMP, Boolean.valueOf(setting
			.isAddTimeStamp()).toString());
		prop.setProperty(PROP_ADD_OPTION_CONV_VIDEO,  Boolean.toString(
			setting.isAddOption_ConvVideoFile()));
		prop.setProperty(PROP_HISTORY1, setting.getHistory1());
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

		/*
		 * ここまで拡張設定保存 1.22r3 に対する
		 */
		try {
			prop.storeToXML(new FileOutputStream(PROP_FILE),
				"settings-"+new Date().toString()+"-Rev"+MainFrame_AboutBox.rev);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static ConvertingSetting loadSetting(String user, String password) {
		Properties prop = new Properties();
		try {
			prop.loadFromXML(new FileInputStream(PROP_FILE));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		if (user == null) {
			user = prop.getProperty(PROP_MAILADDR, "");
		}
		if (password == null) {
			password = prop.getProperty(PROP_PASSWORD, "");
		}
		String option_file_name = prop.getProperty(PROP_OPTION_FILE, null);
		File option_file = null;
		if (option_file_name != null) {
			option_file = new File(option_file_name);
		}
		option_file_name = prop.getProperty(PROP_WIDE_OPTION_FILE, null);
		File wide_option_file = null;
		if (option_file_name != null) {
			wide_option_file = new File(option_file_name);
		}
		String win_dir = System.getenv("windir");
		if(!win_dir.endsWith(File.separator)){
			win_dir = win_dir+File.separator;
		}
		return new ConvertingSetting(
			user,
			password,
			Boolean.parseBoolean(prop.getProperty(PROP_SAVE_VIDEO, "true")),
			prop.getProperty(PROP_VIDEO_FILE, ".\\video.flv"),
			Boolean.parseBoolean(prop.getProperty(PROP_SAVE_COMMENT, "true")),
			Boolean.parseBoolean(prop.getProperty(PROP_ADD_TIMESTAMP, "false")),
			prop.getProperty(PROP_COMMENT_FILE, ".\\comment.xml"),
			Boolean.parseBoolean(prop.getProperty(PROP_SAVE_OWNERCOMMENT, "false")),	// false<-true 1.22r3e8
			prop.getProperty(PROP_OWNERCOMMENT_FILE, ".\\comment" + Converter.OWNER_EXT),
			Boolean.parseBoolean(prop.getProperty(PROP_SAVE_CONVERTED, "true")),
			Boolean.parseBoolean(prop.getProperty(PROP_CONV_WITH_COMMENT,"true")),
			Boolean.parseBoolean(prop.getProperty(PROP_CONV_WITH_OWNERCOMMENT,"false")),	// false<-true 1.22r3e8
			prop.getProperty(PROP_CONVERTED_FILE, ".\\video.avi"),
			prop.getProperty(PROP_SHOW_COMMENT, "40"),
			prop.getProperty(PROP_FFMPEG_PATH,".\\bin\\ffmpeg.exe"),
			prop.getProperty(PROP_VHOOK_PATH,".\\bin\\nicovideoE.dll"),
			prop.getProperty(PROP_CMDLINE_EXT, "avi"),
			prop.getProperty(PROP_CMDLINE_MAIN,""),
			prop.getProperty(PROP_CMDLINE_IN, ""),
			prop.getProperty(PROP_CMDLINE_OUT,"-threads 4 -s 512x384 -acodec libmp3lame -ab 128k -ar 44100 -ac 2 -vcodec libxvid -qscale 3 -async 1 -aspect 4:3"),
			prop.getProperty(PROP_BACK_COMMENT, "500"),
			prop.getProperty(PROP_FONT_PATH, win_dir+"Fonts\\msgothic.ttc"),
			Integer.parseInt(prop.getProperty(PROP_FONT_INDEX, "1")),
			Boolean.parseBoolean(prop.getProperty(PROP_SHOW_VIDEO, "true")),
			Boolean.parseBoolean(prop.getProperty(PROP_DEL_VIDEO_AFTER_CONV, "false")),
			Boolean.parseBoolean(prop.getProperty(PROP_VIDEO_FIX_FILE_NAME, "true")),
			prop.getProperty(PROP_VIDEO_FIX_FILE_NAME_FOLDER,".\\[out]video\\"),
			Boolean.parseBoolean(prop.getProperty(PROP_DEL_COMMENT_AFTER_CONV, "false")),
			Boolean.parseBoolean(prop.getProperty(PROP_COMMENT_FIX_FILE_NAME, "true")),
			prop.getProperty(PROP_COMMENT_FIX_FILE_NAME_FOLDER, ".\\[out]comment\\"),
			Boolean.parseBoolean(prop.getProperty(PROP_NOT_ADD_VIDEOID_CONV, "false")),
			Boolean.parseBoolean(prop.getProperty(PROP_CONV_FIX_FILE_NAME,"true")),
			prop.getProperty(PROP_CONV_FIX_FILE_NAME_FOLDER, ".\\[out]converted\\"),
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
			prop.getProperty(PROP_HISTORY1, "http://www.nicovideo.jp/watch/"),
			prop.getProperty(PROP_VHOOK_WIDE_PATH,".\\bin\\nicovideoE.dll"),
			Boolean.parseBoolean(prop.getProperty(PROP_USE_VHOOK,"true")),
			Boolean.parseBoolean(prop.getProperty(PROP_USE_VHOOK_WIDE,"true")),
			Boolean.parseBoolean(prop.getProperty(PROP_BROWSER_IE, "false")),
			Boolean.parseBoolean(prop.getProperty(PROP_FIREFOX, "false")),
			Boolean.parseBoolean(prop.getProperty(PROP_CHROME, "false")),
			Boolean.parseBoolean(prop.getProperty(PROP_CHROMIUM, "false")),
			Boolean.parseBoolean(prop.getProperty(PROP_OPERA, "false")),
			Boolean.parseBoolean(prop.getProperty(PROP_USE_COOKIE_PATH, "false")),
			prop.getProperty(PROP_BROWSER_COOKIE_PATH,"－場所は自分で捜して下さい－"),
			prop.getProperty(PROP_OPTION_FOLDER, ".\\option"),
			wide_option_file,
			prop.getProperty(PROP_WIDE_CMDLINE_EXT, "mp4"),
			prop.getProperty(PROP_WIDE_CMDLINE_MAIN,""),
			prop.getProperty(PROP_WIDE_CMDLINE_IN, ""),
			prop.getProperty(PROP_WIDE_CMDLINE_OUT,"-threads 4 -s 640x360 -acodec libmp3lame -ab 128k -ar 44100 -ac 2 -vcodec libxvid -qscale 3 -async 1 -aspect 16:9"),
			Boolean.parseBoolean(prop.getProperty(PROP_OPTIONAL_TRANSLUCENT, "true")),
			Boolean.parseBoolean(prop.getProperty(PROP_FONT_HEIGHT_FIX,"false")),
			prop.getProperty(PROP_FONT_HEIGHT_FIX_RAITO,"102"),
			Boolean.parseBoolean(prop.getProperty(PROP_DISABLE_ORIGINAL_RESIZE, "false")),
			Integer.parseInt(prop.getProperty(PROP_COMMENT_MODE_INDEX, "0")),
			Boolean.parseBoolean(prop.getProperty(PROP_SET_COMMENT_SPEED, "false")),
			prop.getProperty(PROP_COMMENT_SPEED, ""),
			false,
			Boolean.parseBoolean(prop.getProperty(PROP_ENABLE_CA, "false"))
		);
	}

	public void setFontPath(String path) {
		FontPath = path;
	}

	public void setFontIndex(int i) {
		FontIndex = i;
	}

}
