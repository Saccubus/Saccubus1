package saccubus;

import java.util.Properties;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;

/**
 * <p>
 * �^�C�g��: ������΂�
 * </p>
 *
 * <p>
 * ����: �j�R�j�R����̓�����R�����g���ŕۑ�
 * </p>
 *
 * <p>
 * ���쌠: Copyright (c) 2007 PSI
 * </p>
 *
 * <p>
 * ��Ж�:
 * </p>
 *
 * @author ������
 * @version 1.0
 */
public class ConvertingSetting {
	public static final String[] ShadowKindArray = {
		"00:�Ȃ�",
		"01:�j�R�j�R���敗",
		"02:�E��",
		"03:�͂�����"
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
	private boolean UseOptionFile;
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
		if (videofile.lastIndexOf(".") <= videofile.lastIndexOf("\\")) {
			videofile += ".flv";
		}
		VideoFile = new File(videofile);
		SaveComment = savecomment;
		if (commentfile.lastIndexOf(".") <= commentfile.lastIndexOf("\\")) {
			commentfile += ".xml";
		}
		CommentFile = new File(commentfile);
		SaveConverted = saveconverted;
		if (convvideofile.lastIndexOf(".") <= convvideofile.lastIndexOf("\\")) {
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
	 * �g���ݒ�R���X�g���N�^
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
			boolean browserChrome
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
		if (ownercommentfile.lastIndexOf(".") <= ownercommentfile.lastIndexOf("\\")) {
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
	public boolean useOptionFile() {
		return UseOptionFile;
	}
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
	 * ��������g���ݒ� 1.22r3 �ɑ΂���
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

	/*
	 * �����܂Ŋg���ݒ� 1.22r3 �ɑ΂���
	 */

	public static void saveSetting(ConvertingSetting setting) {
		Properties prop = new Properties();
		prop.setProperty(PROP_MAILADDR, setting.getMailAddress());
		prop.setProperty(PROP_PASSWORD, setting.getPassword());
		prop.setProperty(PROP_SAVE_VIDEO, new Boolean(setting
			.isSaveVideo()).toString());
		prop.setProperty(PROP_VIDEO_FILE, setting.getVideoFile().getPath());
		prop.setProperty(PROP_SAVE_COMMENT, Boolean.valueOf(setting
			.isSaveComment()).toString());
		prop.setProperty(PROP_COMMENT_FILE, setting.getCommentFile()
			.getPath());
		prop.setProperty(PROP_SAVE_CONVERTED, new Boolean(setting
			.isSaveConverted()).toString());
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
		prop.setProperty(PROP_SHOW_VIDEO, new Boolean(setting
			.isVhook_ShowConvertingVideo()).toString());
		prop.setProperty(PROP_DEL_VIDEO_AFTER_CONV, new Boolean(setting
			.isDeleteVideoAfterConverting()).toString());
		prop.setProperty(PROP_VIDEO_FIX_FILE_NAME, new Boolean(setting
			.isVideoFixFileName()).toString());
		prop.setProperty(PROP_VIDEO_FIX_FILE_NAME_FOLDER, setting
			.getVideoFixFileNameFolder().getPath());
		prop.setProperty(PROP_DEL_COMMENT_AFTER_CONV, new Boolean(setting
			.isDeleteCommentAfterConverting()).toString());
		prop.setProperty(PROP_COMMENT_FIX_FILE_NAME, new Boolean(setting
			.isCommentFixFileName()).toString());
		prop.setProperty(PROP_COMMENT_FIX_FILE_NAME_FOLDER, setting
			.getCommentFixFileNameFolder().getPath());
		prop.setProperty(PROP_NOT_ADD_VIDEOID_CONV, new Boolean(setting
			.isNotAddVideoID_Conv()).toString());
		prop.setProperty(PROP_CONV_FIX_FILE_NAME, new Boolean(setting
			.isConvFixFileName()).toString());
		prop.setProperty(PROP_CONV_FIX_FILE_NAME_FOLDER, setting
			.getConvFixFileNameFolder().getPath());
		prop.setProperty(PROP_NG_WORD, setting.getNG_Word());
		prop.setProperty(PROP_NG_ID, setting.getNG_ID());
		prop.setProperty(PROP_USE_PROXY, new Boolean(setting
			.useProxy()).toString());
		prop.setProperty(PROP_PROXY, setting.getProxy());
		prop.setProperty(PROP_PROXY_PORT, Integer.toString(setting
			.getProxyPort()));
		prop.setProperty(PROP_FIX_FONT_SIZE, new Boolean(setting
			.isFixFontSize()).toString());
		prop.setProperty(PROP_FIX_COMMENT_NUM, new Boolean(setting
			.isFixCommentNum()).toString());
		prop.setProperty(PROP_OPAQUE_COMMENT, new Boolean(setting
			.isOpaqueComment()).toString());
		if (setting.getOptionFile() != null) {
			prop.setProperty(PROP_OPTION_FILE, setting.getOptionFile()
				.getPath());
		}
		prop.setProperty(PROP_DISABLE_VHOOK, new Boolean(setting
			.isVhookDisabled()).toString());
		prop.setProperty(PROP_SHADOW_INDEX, Integer.toString(setting.getShadowIndex()));
		/*
		 * ��������g���ݒ�ۑ� 1.22r3 �ɑ΂���
		 */
		prop.setProperty(PROP_CONV_WITH_COMMENT, new Boolean(setting
				.isConvertWithComment()).toString());
		prop.setProperty(PROP_SAVE_OWNERCOMMENT, new Boolean(setting
			.isSaveOwnerComment()).toString());
		prop.setProperty(PROP_OWNERCOMMENT_FILE, setting
			.getOwnerCommentFile().getPath());
		prop.setProperty(PROP_CONV_WITH_OWNERCOMMENT, new Boolean(setting
			.isConvertWithOwnerComment()).toString());
		prop.setProperty(PROP_ADD_TIMESTAMP, Boolean.valueOf(setting
			.isAddTimeStamp()).toString());
		prop.setProperty(PROP_ADD_OPTION_CONV_VIDEO, new Boolean(
			setting.isAddOption_ConvVideoFile()).toString());
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
		/*
		 * �����܂Ŋg���ݒ�ۑ� 1.22r3 �ɑ΂���
		 */
		try {
			prop.storeToXML(new FileOutputStream(PROP_FILE), "settings");
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
		String win_dir = System.getenv("windir");
		if(!win_dir.endsWith("\\")){
			win_dir = win_dir+"\\";
		}
		return new ConvertingSetting(
			user,
			password,
			Boolean.parseBoolean(prop.getProperty(PROP_SAVE_VIDEO, "true")),
			prop.getProperty(PROP_VIDEO_FILE, ".\\video.flv"),
			Boolean.parseBoolean(prop.getProperty(PROP_SAVE_COMMENT, "true")),
			Boolean.parseBoolean(prop.getProperty(PROP_ADD_TIMESTAMP, "false")),
			prop.getProperty(PROP_COMMENT_FILE, ".\\comment.xml"),
			Boolean.parseBoolean(prop.getProperty(PROP_SAVE_OWNERCOMMENT, "true")),
			prop.getProperty(PROP_OWNERCOMMENT_FILE, ".\\comment" + Converter.OWNER_EXT),
			Boolean.parseBoolean(prop.getProperty(PROP_SAVE_CONVERTED, "true")),
			Boolean.parseBoolean(prop.getProperty(PROP_CONV_WITH_COMMENT,"true")),
			Boolean.parseBoolean(prop.getProperty(PROP_CONV_WITH_OWNERCOMMENT,"true")),
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
			Boolean.parseBoolean(prop.getProperty(PROP_CHROME, "false"))
		);
	}

}
