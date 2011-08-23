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

	private File ConvertedVideoFile;

	private String FFmpegPath;

	private String VhookPath;

	private String CmdLineOptionExt;

	private String CmdLineOptionIn;

	private String CmdLineOptionOut;

	private String CmdLineOptionMain;

	private String FontPath;

	private int FontIndex;

	private String BackComment;

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

	public ConvertingSetting(String mailaddress, String password,
			boolean savevideo, String videofile, boolean savecomment,
			String commentfile, boolean saveconverted, String convvideofile,
			String videoshownum, String ffmpegpath, String vhookpath,
			String cmdlineoption_ext, String cmdlineoption_main,
			String cmdlineoption_in, String cmdlineoption_out,
			String backcomment, String fontpath, int font_index,
			boolean showconvvideo, boolean delete_video_after_conv,
			boolean video_fix_file_name, String video_fix_file_name_folder,
			boolean delete_comment_after_conv, boolean comment_fix_file_name,
			String comment_fix_file_name_folder, boolean not_add_videoid_conv,
			boolean conv_fix_file_name, String conv_fix_file_name_folder,
			String ngword, String ngid, boolean use_proxy, String proxy,
			int proxy_port, boolean fix_font_size, boolean fix_comment_num,
			boolean opaque_comment, File option_file, boolean disable_vhook,
			int shadow_index) {
		MailAddress = mailaddress;
		Password = password;
		SaveVideo = savevideo;
		if (videofile.lastIndexOf(".") < videofile.lastIndexOf("\\")) {
			videofile += ".flv";
		}
		VideoFile = new File(videofile);
		SaveComment = savecomment;
		if (commentfile.lastIndexOf(".") < commentfile.lastIndexOf("\\")) {
			commentfile += ".xml";
		}
		CommentFile = new File(commentfile);
		SaveConverted = saveconverted;
		if (convvideofile.lastIndexOf(".") < convvideofile.lastIndexOf("\\")) {
			convvideofile += ".avi";
		}
		ConvertedVideoFile = new File(convvideofile);
		try {
			VideoShowNum = Integer.parseInt(videoshownum);
		} catch (NumberFormatException ex) {
			VideoShowNum = 30;
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

	public File getVideoFile() {
		return VideoFile;
	}

	public File getCommentFile() {
		return CommentFile;
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

	public boolean isSaveConverted() {
		return SaveConverted;
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

	public static void saveSetting(ConvertingSetting setting) {
		Properties prop = new Properties();
		prop.setProperty(PROP_MAILADDR, setting.getMailAddress());
		prop.setProperty(PROP_PASSWORD, setting.getPassword());
		prop.setProperty(PROP_SAVE_VIDEO, Boolean.toString(setting
				.isSaveVideo()));
		prop.setProperty(PROP_VIDEO_FILE, setting.getVideoFile().getPath());
		prop.setProperty(PROP_SAVE_COMMENT, Boolean.toString(setting
				.isSaveComment()));
		prop.setProperty(PROP_COMMENT_FILE, setting.getCommentFile().getPath());
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
		prop.setProperty(PROP_SHOW_VIDEO, Boolean.toString((setting
				.isVhook_ShowConvertingVideo())));
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

		prop.setProperty(PROP_CONV_FIX_FILE_NAME, (new Boolean(setting
				.isConvFixFileName())).toString());
		prop.setProperty(PROP_CONV_FIX_FILE_NAME_FOLDER, setting
				.getConvFixFileNameFolder().getPath());

		prop.setProperty(PROP_NG_WORD, setting.getNG_Word());
		prop.setProperty(PROP_NG_ID, setting.getNG_ID());
		prop.setProperty(PROP_USE_PROXY, Boolean.toString(setting.useProxy()));
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
		return new ConvertingSetting(user, password,
				Boolean.parseBoolean(prop.getProperty(PROP_SAVE_VIDEO, "true")),
				prop.getProperty(PROP_VIDEO_FILE, ".\\video.flv"),
				 Boolean.parseBoolean(prop.getProperty(PROP_SAVE_COMMENT, "true")),
				 prop.getProperty(PROP_COMMENT_FILE, ".\\comment.xml"),
				 Boolean.parseBoolean(prop.getProperty(PROP_SAVE_CONVERTED, "true")),
				 prop.getProperty(PROP_CONVERTED_FILE, ".\\video.avi"),
				 prop.getProperty(PROP_SHOW_COMMENT, "30"),
				 prop.getProperty(PROP_FFMPEG_PATH,".\\bin\\ffmpeg.exe"),
				 prop.getProperty(PROP_VHOOK_PATH,".\\bin\\nicovideo.dll"),
				 prop.getProperty(PROP_CMDLINE_EXT, "avi"),
				 prop.getProperty(PROP_CMDLINE_MAIN,""),
				 prop.getProperty(PROP_CMDLINE_IN, ""),
				 prop.getProperty(PROP_CMDLINE_OUT,"-s 512x384 -acodec libmp3lame -ab 128k -ar 44100 -ac 2 -vcodec libxvid -qscale 3 -async 1"),
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
				 option_file, Boolean.parseBoolean(prop.getProperty(PROP_DISABLE_VHOOK,"false")),
				Integer.parseInt(prop.getProperty(PROP_SHADOW_INDEX,"1"),
				 10)
		);
	}

}
