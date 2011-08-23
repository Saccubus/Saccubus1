package saccubus;

import javax.swing.JLabel;
import saccubus.net.NicoClient;
import java.io.*;

import saccubus.conv.ConvertToVideoHook;
import java.net.URLEncoder;
import java.util.Properties;
import saccubus.util.Cws2Fws;

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
	private ConvertingSetting Setting;

	private String Tag;

	private String VideoID;

	private String VideoTitle;

	private String Time;

	private JLabel Status;

	private final ConvertStopFlag StopFlag;

	private static final String TMP_COMMENT_MIDDLE_FILE = "./vhook.tmp";
	private static final String VIDEO_URL_PARSER = "http://www.nicovideo.jp/watch/";

	public Converter(String url, String time, ConvertingSetting setting,
			JLabel status, ConvertStopFlag flag) {
		url = url.trim();
		if(url.startsWith(VIDEO_URL_PARSER)){
			int index = url.indexOf('?',VIDEO_URL_PARSER.length());
			if(index >= 0){
				Tag = url.substring(VIDEO_URL_PARSER.length(),index);
			}else{
				Tag = url.substring(VIDEO_URL_PARSER.length());
			}
		}else{
			Tag = url;
		}
		VideoID = "[" + Tag + "]";
		Time = time;
		Setting = setting;
		Status = status;
		StopFlag = flag;
	}

	private File VideoFile = null;

	private File CommentFile = null;

	private File ConvertedVideoFile = null;

	private File CommentMiddleFile = null;

	public void run() {
		boolean converted = false;
		try {
			if (!Setting.isSaveConverted() && !Setting.isSaveComment()
					&& !Setting.isSaveVideo()) {
				Status.setText("�������邱�Ƃ�����܂���");
				return;
			}
			if (Setting.isSaveConverted()) {
				File a = new File(Setting.getFFmpegPath());
				if (!a.canRead()) {
					Status.setText("FFmpeg��������܂���B");
					return;
				}
				if (Setting.getVhookPath().indexOf(' ') >= 0) {
					Status.setText("�����܂���B����vhook���C�u�����ɂ͔��p�󔒂͎g���܂���B");
					return;
				}
				a = new File(Setting.getVhookPath());
				if (!a.canRead()) {
					Status.setText("Vhook���C�u������������܂���B");
					return;
				}
				a = new File(Setting.getFontPath());
				if (!a.canRead()) {
					Status.setText("�t�H���g��������܂���B");
					return;
				}
				if (!detectOption()) {
					Status.setText("�ϊ��I�v�V�����t�@�C���̓ǂݍ��݂Ɏ��s���܂����B");
					return;
				}
			} else {
				if (Setting.isDeleteVideoAfterConverting()) {
					Status.setText("�ϊ����Ȃ��̂ɁA����폜��������ėǂ���ł����H");
					return;
				}
				if (Setting.isDeleteCommentAfterConverting()) {
					Status.setText("�ϊ����Ȃ��̂ɁA�R�����g�폜��������ėǂ���ł����H");
					return;
				}
			}
			NicoClient client = null;
			if (Setting.isSaveVideo() || Setting.isSaveComment()) {
				if (Setting.getMailAddress() == null
						|| Setting.getPassword() == null
						|| Setting.getMailAddress().equals("")
						|| Setting.getPassword().equals("")) {
					Status.setText("���[���A�h���X���p�X���[�h���󔒂ł��B");
					return;
				}
				if (Setting.useProxy()
						&& (Setting.getProxy() == null || Setting.getProxy()
								.length() <= 0)
						&& (Setting.getProxyPort() < 0 || Setting
								.getProxyPort() > 65535)) {
					Status.setText("�v���L�V�̐ݒ肪�s���ł��B");
					return;
				}
				if (stopFlagReturn()) {
					return;
				}
				Status.setText("���O�C����");
				String proxy;
				int proxy_port;
				if (Setting.useProxy()) {
					proxy = Setting.getProxy();
					proxy_port = Setting.getProxyPort();
				} else {
					proxy = null;
					proxy_port = -1;
				}
				client = new NicoClient(Setting.getMailAddress(), Setting
						.getPassword(), StopFlag, proxy, proxy_port);

				if (!client.isLoggedIn()) {
					Status.setText("���O�C���Ɏ��s");
					return;
				}
				if (stopFlagReturn()) {
					return;
				}
				/*����̕ۑ�*/
				if (!client.getVideoInfo(Tag, Time)) {
					Status.setText(Tag + "�̏��̎擾�Ɏ��s");
					return;
				}
				if (stopFlagReturn()) {
					return;
				}
				VideoTitle = client.getVideoTitle();
			}
			if (Setting.isSaveVideo()) {
				if (Setting.isVideoFixFileName()) {
					Setting.getVideoFixFileNameFolder().mkdir();
					VideoFile = new File(Setting.getVideoFixFileNameFolder(),
							VideoID + VideoTitle + ".flv");
				} else {
					VideoFile = Setting.getVideoFile();
				}
				Status.setText("����̃_�E�����[�h�J�n��");
				VideoFile = client.getVideo(VideoFile, Status);
				if (stopFlagReturn()) {
					return;
				}
				if (VideoFile == null) {
					Status.setText("����̃_�E�����[�h�Ɏ��s");
					return;
				}
			} else {
				if (Setting.isSaveConverted()) {
					if (Setting.isVideoFixFileName()) {
						if(!detectVideoTitle(Setting.getVideoFixFileNameFolder())){
							Status.setText("����t�@�C�������݂��܂���B");
							return;
						}
						VideoFile = new File(Setting
								.getVideoFixFileNameFolder(), VideoID
								+ VideoTitle + ".flv");
						if (!VideoFile.canRead()) {
							Status.setText("����t�@�C�����ǂݍ��߂܂���B");
							return;
						}
					} else {
						VideoFile = Setting.getVideoFile();
						if (!VideoFile.exists()) {
							Status.setText("����t�@�C�������݂��܂���B");
							return;
						}
					}
				}
			}

			if (stopFlagReturn()) {
				return;
			}

			if (Setting.isSaveComment()) {
				if (Setting.isCommentFixFileName()) {
					Setting.getCommentFixFileNameFolder().mkdir();
					CommentFile = new File(Setting
							.getCommentFixFileNameFolder(), VideoID
							+ VideoTitle + ".xml");
				} else {
					CommentFile = Setting.getCommentFile();
				}
				String back_comment = Setting.getBackComment();
				if (Setting.isFixCommentNum()) {
					back_comment = client
							.getBackCommentFromLength(back_comment);
				}
				Status.setText("�R�����g�̃_�E�����[�h�J�n��");
				CommentFile = client.getComment(CommentFile, Status,
						back_comment);
				if (stopFlagReturn()) {
					return;
				}
				if (CommentFile == null) {
					Status.setText("�R�����g�̃_�E�����[�h�Ɏ��s");
					return;
				}
			} else {
				if (Setting.isSaveConverted()) {
					if (Setting.isCommentFixFileName()) {
						if(!detectVideoTitle(Setting.getCommentFixFileNameFolder())){
							Status.setText("�R�����g�t�@�C�������݂��܂���B");
							return;
						}
						CommentFile = new File(Setting
								.getCommentFixFileNameFolder(), VideoID
								+ VideoTitle + ".xml");
						if (!CommentFile.canRead()) {
							Status.setText("�R�����g�t�@�C�����ǂݍ��߂܂���B");
							return;
						}
					} else {
						CommentFile = Setting.getCommentFile();
						if (!CommentFile.exists()) {
							Status.setText("�R�����g�t�@�C�������݂��܂���B");
							return;
						}
					}
				}
			}

			if (stopFlagReturn()) {
				return;
			}

			if (!Setting.isSaveConverted()) {
				Status.setText("����E�R�����g��ۑ����A�ϊ��͍s���܂���ł����B");
				return;
			}
			CommentMiddleFile = new File(TMP_COMMENT_MIDDLE_FILE);
			Status.setText("�R�����g�̒��ԃt�@�C���ւ̕ϊ���");
			boolean conv = ConvertToVideoHook
					.convert(CommentFile, CommentMiddleFile,
							Setting.getNG_ID(), Setting.getNG_Word());
			if (!conv) {
				Status.setText("�R�����g�ϊ��Ɏ��s�B�����炭���K�\���̊ԈႢ�H");
				return;
			}
			if (stopFlagReturn()) {
				return;
			}
			Status.setText("����̕ϊ����J�n");
			/*�r�f�I���̊m��*/
			if (Setting.isConvFixFileName()) {
				if (VideoTitle == null) {
					Status.setText("�ϊ���̃r�f�I�t�@�C�������m��ł��܂���B");
					return;
				}
				Setting.getConvFixFileNameFolder().mkdir();
				String conv_name = VideoTitle;
				if (!Setting.isNotAddVideoID_Conv()) {//�t�����Ȃ��Ȃ�
					conv_name = VideoID + conv_name;
				}
				ConvertedVideoFile = new File(Setting
						.getConvFixFileNameFolder(), conv_name + ExtOption);
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
			int code;
			if ((code = converting_video(TMP_COMMENT_MIDDLE_FILE)) == 0) {
				converted = true;
				Status.setText("�ϊ�������ɏI�����܂����B");
			} else if (code == CODE_CONVERTING_ABORTED) { /*���f*/

			} else {
				Status.setText("�ϊ��G���[�F" + LastError);
			}

		} finally {
			StopFlag.finished();
			if (CommentMiddleFile != null) {
				CommentMiddleFile.delete();
			}
			if (converted) {
				if (Setting.isDeleteCommentAfterConverting()
						&& CommentFile != null) {
					CommentFile.delete();
				}
				if (Setting.isDeleteVideoAfterConverting() && VideoFile != null) {
					VideoFile.delete();
				}
			}
		}
	}

	String LastError;

	private static final int CODE_CONVERTING_ABORTED = 100;

	private int converting_video(String vhook_path) {
        File fwsFile = Cws2Fws.createFws(VideoFile);

		StringBuffer sb = new StringBuffer();
		sb.append("\"");
		sb.append(Setting.getFFmpegPath().replace("\\", "\\\\"));
		sb.append("\"");
		sb.append(" -y ");
		sb.append(MainOption);
		sb.append(" ");
		sb.append(InOption);
		sb.append(" -i ");
		if (fwsFile == null) {
			sb.append("\"");
			sb.append(VideoFile.getPath().replace("\\", "\\\\"));
			sb.append("\"");
		} else {
			sb.append(fwsFile.getPath().replace("\\", "\\\\"));
		}
		sb.append(" ");
		sb.append(OutOption);
		sb.append(" \"");
		sb.append(ConvertedVideoFile.getPath().replace("\\", "\\\\"));
		sb.append("\"");
		if (!Setting.isVhookDisabled()) {
			if(!addVhookSetting(sb, vhook_path)){
				return -1;
			}
		}
		String cmd = sb.substring(0);
		System.out.println("arg:" + cmd);
		try {
			System.out.println("\n\n----\nProcessing FFmpeg...\n----\n\n");
			Process process = Runtime.getRuntime().exec(cmd);
			BufferedReader ebr = new BufferedReader(new InputStreamReader(
					process.getErrorStream()));
			String e;
			while ((e = ebr.readLine()) != null) {
				LastError = e;
				if (LastError.startsWith("frame=")) { //
					Status.setText(LastError);
				} else if(!LastError.endsWith("No accelerated colorspace conversion found")){
					System.out.println(e);
				}
				if (stopFlagReturn()) {
					process.destroy();
					return CODE_CONVERTING_ABORTED;
				}
			}
			process.waitFor();
			return process.exitValue();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
			return -1;
		} catch (IOException ex) {
			ex.printStackTrace();
			return -1;
        } finally {
            if (fwsFile != null) {
                fwsFile.delete();
            }
        }
	}

	private boolean addVhookSetting(StringBuffer sb, String vhook_path) {
		try {
			sb.append(" -vfilters \"vhext=");
			sb.append(Setting.getVhookPath().replace("\\", "/"));
			sb.append("|");
			sb.append("--data-user:");
			sb.append(URLEncoder.encode(vhook_path.replace("\\","/"),
					"Shift_JIS"));
			sb.append("|");
			sb.append("--font:");
			sb.append(URLEncoder.encode(
					Setting.getFontPath().replace("\\","/"), "Shift_JIS"));
			sb.append("|");
			sb.append("--font-index:");
			sb.append(Setting.getFontIndex());
			sb.append("|");
			sb.append("--show-user:");
			sb.append(Setting.getVideoShowNum());
			sb.append("|");
			sb.append("--shadow:");
			sb.append(Setting.getShadowIndex());
			sb.append("|");
			if (Setting.isVhook_ShowConvertingVideo()) {
				sb.append("--enable-show-video");
				sb.append("|");
			}
			if (Setting.isFixFontSize()) {
				sb.append("--enable-fix-font-size");
				sb.append("|");
			}
			if (Setting.isOpaqueComment()) {
				sb.append("--enable-opaque-comment");
			}
			sb.append("\"");
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
			this.Status.setText("���~���܂����B");
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

	private boolean detectOption() {
		boolean ret;
		if (Setting.getOptionFile() != null) {
			try {
				Properties prop = new Properties();
				prop.loadFromXML(new FileInputStream(Setting.getOptionFile()));
				ExtOption = prop.getProperty("EXT", null);
				InOption = prop.getProperty("IN", null);
				OutOption = prop.getProperty("OUT", null);
				MainOption = prop.getProperty("MAIN", null);
				if (ExtOption != null && InOption != null && OutOption != null
						&& MainOption != null) {
					ret = true;
				} else {
					ret = false;
				}
			} catch (IOException ex) {
				ex.printStackTrace();
				ret = false;
			}
		} else {
			ExtOption = Setting.getCmdLineOptionExt();
			InOption = Setting.getCmdLineOptionIn();
			OutOption = Setting.getCmdLineOptionOut();
			MainOption = Setting.getCmdLineOptionMain();
			ret = true;
		}
		//�I�v�V�����Ɋg���q���܂�ł��܂����ꍇ�ɂ��Ή���
		if(!ExtOption.startsWith(".")){
			ExtOption = "."+ExtOption;
		}
		return ret;
	}
	private boolean detectVideoTitle(File dir){
		String list[] = dir.list();
		if(list == null){
			return false;
		}
		for (int i = 0; i < list.length; i++) {
			if (list[i].startsWith(VideoID)) {
				VideoTitle = list[i].substring(VideoID.length(),
						list[i].lastIndexOf("."));
				return true;
			}
		}
		return false;
	}
}
