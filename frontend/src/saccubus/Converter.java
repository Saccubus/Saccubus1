package saccubus;

import javax.swing.JLabel;
import saccubus.net.NicoClient;
import java.io.*;

import saccubus.conv.CombineXML;
import saccubus.conv.ConvertToVideoHook;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Properties;

import saccubus.util.Cws2Fws;
import saccubus.util.Stopwatch;
import saccubus.ConvertingSetting;

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
	private static final String TMP_COMMENT = "./vhook.tmp";
	private static final String TMP_OWNERCOMMENT = "./vhookowner.tmp";
	private static final String VIDEO_URL_PARSER = "http://www.nicovideo.jp/watch/";
	public static final String OWNER_EXT = "[Owner].xml";	// ���e�҃R�����g�T�t�B�b�N�X
	private static final String TMP_COMBINED_XML = "./tmp_comment.xml";
	private String OtherVideo;

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
	private File OwnerCommentFile = null;
	private File ConvertedVideoFile = null;
	private File CommentMiddleFile = null;
	private File OwnerMiddleFile = null;

	public File getVideoFile() {
		return VideoFile;
	}
	public ConvertingSetting getSetting(){
		return Setting;
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
		sendtext("�`�F�b�N���Ă��܂�");
		if (!isSaveConverted() && !isSaveVideo()
			&& !isSaveComment() && !isSaveOwnerComment()){
			sendtext("�������邱�Ƃ�����܂���");
			return false;
		}
		if (isSaveConverted()) {
			File a = new File(Setting.getFFmpegPath());
			if (!a.canRead()) {
				sendtext("FFmpeg��������܂���B");
				return false;
			}
			if (Setting.getVhookPath().indexOf(' ') >= 0) {
				sendtext("�����܂���B����vhook���C�u�����ɂ͔��p�󔒂͎g���܂���B");
				return false;
			}
			a = new File(Setting.getVhookPath());
			if (!a.canRead()) {
				sendtext("Vhook���C�u������������܂���B");
				return false;
			}
			a = new File(Setting.getFontPath());
			if (!a.canRead()) {
				sendtext("�t�H���g��������܂���B");
				return false;
			}
			if (!detectOption()) {
				sendtext("�ϊ��I�v�V�����t�@�C���̓ǂݍ��݂Ɏ��s���܂����B");
				return false;
			}
		} else {
			if (isDeleteVideoAfterConverting()) {
				sendtext("�ϊ����Ȃ��̂ɁA����폜��������ėǂ���ł����H");
				return false;
			}
			if (isDeleteCommentAfterConverting()) {
				sendtext("�ϊ����Ȃ��̂ɁA�R�����g�폜��������ėǂ���ł����H");
				return false;
			}
		}
		if (isSaveVideo() ||
				isSaveComment() || isSaveOwnerComment()) {
			if (getMailAddress() == null
					|| getMailAddress().isEmpty()
					|| getPassword() == null
					|| getPassword().isEmpty()) {
				sendtext("���[���A�h���X���p�X���[�h���󔒂ł��B");
				return false;
			}
			if (useProxy()
				&& (   getProxy() == null || getProxy().isEmpty()
					|| getProxyPort() < 0 || getProxyPort() > 65535   )){
				sendtext("�v���L�V�̐ݒ肪�s���ł��B");
				return false;
			}
		}
		sendtext("�`�F�b�N�I��");
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
			sendtext("���O�C����");
			NicoClient client = new NicoClient(getMailAddress(),
					getPassword(), StopFlag, proxy, proxy_port);
			if (!client.isLoggedIn()) {
				sendtext("���O�C���Ɏ��s");
			} else {
				sendtext("���O�C������");
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
		if (isSaveVideo()) {
			if (isVideoFixFileName()) {
				folder.mkdir();
				VideoFile = new File(folder,
						VideoID + VideoTitle + ".flv");
			} else {
				VideoFile = Setting.getVideoFile();
			}
			sendtext("����̃_�E�����[�h�J�n��");
			if (client == null){
				sendtext("���O�C�����ĂȂ��̂ɓ���̕ۑ��ɂȂ�܂���");
				return false;
			}
			VideoFile = client.getVideo(VideoFile, Status);
			if (stopFlagReturn()) {
				return false;
			}
			if (VideoFile == null) {
				sendtext("����̃_�E�����[�h�Ɏ��s");
				return false;
			}
		} else {
			if (isSaveConverted()) {
				if (isVideoFixFileName()) {
					String videoFilename;
					if((videoFilename = detectTitleFromVideo(folder)) == null){
						if (OtherVideo == null){
							sendtext("����t�@�C�����t�H���_�ɑ��݂��܂���B");
						} else {
							sendtext("����t�@�C����.flv�ł���܂���F" + OtherVideo);
						}
						return false;
					}
					VideoFile = new File(folder, videoFilename);
					if (!VideoFile.canRead()) {
						sendtext("����t�@�C�����ǂݍ��߂܂���B");
						return false;
					}
				} else {
					VideoFile = Setting.getVideoFile();
					if (!VideoFile.exists()) {
						sendtext("����t�@�C�������݂��܂���B");
						return false;
					}
				}
			}
		}
		sendtext("����̕ۑ����I��");
		return true;
	}

	private boolean saveComment(NicoClient client) {
		sendtext("�R�����g�̕ۑ�");
		File folder = Setting.getCommentFixFileNameFolder();
		if (isSaveComment()) {
			if (isCommentFixFileName()) {
				folder.mkdir();
				String ext = ".xml";
				if (Setting.isAddTimeStamp()) {
					MyDateFormat mdf = new MyDateFormat();
					if(Time == null || Time.isEmpty()){
						ext = "[" + mdf.formatNow() + "]" + ext;
					} else if (mdf.makeTime(Time)){
						ext = "[" + mdf.formatDate() + "]" + ext;
					} else {
						ext = "[" + Time + "]" + ext;
					}
				}
				CommentFile = new File(folder,VideoID + VideoTitle + ext);
			} else {
				CommentFile = Setting.getCommentFile();
			}
			if (client == null){
				sendtext("���O�C�����ĂȂ��̂ɃR�����g�̕ۑ��ɂȂ�܂���");
				return false;
			}
			String back_comment = Setting.getBackComment();
			if (Setting.isFixCommentNum()) {
				back_comment = client
						.getBackCommentFromLength(back_comment);
			}
			sendtext("�R�����g�̃_�E�����[�h�J�n��");
			CommentFile = client.getComment(CommentFile, Status,
					back_comment);
			if (stopFlagReturn()) {
				return false;
			}
			if (CommentFile == null) {
				sendtext("�R�����g�̃_�E�����[�h�Ɏ��s");
				return false;
			}
		}
		sendtext("�R�����g�̕ۑ��I��");
		return true;
	}

	private boolean saveOwnerComment(NicoClient client){
		sendtext("���e�҃R�����g�̕ۑ�");
		File folder = Setting.getCommentFixFileNameFolder();
		if (isSaveOwnerComment()) {
			if (isCommentFixFileName()) {
				folder.mkdir();
				OwnerCommentFile = new File(folder,
					VideoID + VideoTitle + OWNER_EXT);
			} else {
				OwnerCommentFile = Setting.getOwnerCommentFile();
			}
			sendtext("���e�҃R�����g�̃_�E�����[�h�J�n��");
			if (client == null){
				sendtext("���O�C�����ĂȂ��̂ɓ��e�҃R�����g�̕ۑ��ɂȂ�܂���");
				return false;
			}
			OwnerCommentFile = client.getOwnerComment(OwnerCommentFile, Status);
			if (stopFlagReturn()) {
				return false;
			}
			if (OwnerCommentFile == null) {
				sendtext("���e�҃R�����g�̃_�E�����[�h�Ɏ��s");
				return false;
			}
		}
		sendtext("���e�҃R�����g�̕ۑ��I��");
		return true;
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
					return false;
				}
				// VideoTitle �͌��������B
				if (pathlist.size() > 1) {
					ArrayList<File> filelist = new ArrayList<File>();
					for(int i = 0; i < pathlist.size(); i++){
						filelist.add(new File(folder, pathlist.get(i)));
					}
					CommentFile = new File(TMP_COMBINED_XML);
					sendtext("�R�����g�t�@�C��������");
					if (!CombineXML.combineXML(filelist, CommentFile)){
						sendtext("�R�����g�t�@�C���������o���܂���ł����i�o�O�H�j");
						return false;
					}
				} else {
					// �R�����g�t�@�C���͂ЂƂ�����������
					File comfile = new File(folder,pathlist.get(0));
					if (!isSaveComment()){
						CommentFile = comfile;
					} else if (!comfile.getPath().equals(CommentFile.getPath())){
						sendtext("�ۑ������R�����g�t�@�C����������܂���B");
						return false;
					}
				}
			}
			if (!isSaveComment()) {
				if (isCommentFixFileName()) {
					if (!Setting.isAddTimeStamp()){
						// �R�����g�t�@�C���͂ЂƂ�
						String commentfilename = detectTitleFromComment(folder);
						if(commentfilename == null){
							sendtext("�R�����g�t�@�C�����t�H���_�ɑ��݂��܂���B");
							return false;
						}
						// VideoTitle �͌��������B
						CommentFile = new File(folder, commentfilename);
						if (!CommentFile.canRead()) {
							sendtext("�R�����g�t�@�C�����ǂݍ��߂܂���B");
							return false;
						}
					} else {
						// �����ς�
					}
				} else {
					CommentFile = Setting.getCommentFile();
					if (!CommentFile.exists()) {
						sendtext("�R�����g�t�@�C�������݂��܂���B");
						return false;
					}
				}
			}
			CommentMiddleFile = convertToCommentMiddle(
					CommentFile, TMP_COMMENT);
			if (CommentMiddleFile == null){
				sendtext("�R�����g�ϊ��Ɏ��s�B�����炭���K�\���̊ԈႢ�H");
				return false;
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
						return false;
					}
					// VideoTitle �͌��������B
					OwnerCommentFile = new File(folder, ownerfilename);
					if (!OwnerCommentFile.canRead()) {
						sendtext("���e�҃R�����g�t�@�C�����ǂݍ��߂܂���B");
						return false;
					}
				} else {
					OwnerCommentFile = Setting.getOwnerCommentFile();
					if (!OwnerCommentFile.exists()) {
						sendtext("���e�҃R�����g�t�@�C�������݂��܂���B");
						return false;
					}
				}
			}
			OwnerMiddleFile = convertToCommentMiddle(
					OwnerCommentFile, TMP_OWNERCOMMENT);
			if (OwnerMiddleFile == null){
				sendtext("���e�҃R�����g�ϊ��Ɏ��s�B�����炭���K�\���̊ԈႢ�H");
				return false;
			}
		}
		return true;
	}

	private void deleteCommentFile(){
		CommentFile.delete();
	}

	private File convertToCommentMiddle(File commentfile,
			String outpath) {
		File middlefile = new File(outpath);
		if (!ConvertToVideoHook.convert
			(commentfile, middlefile,
			Setting.getNG_ID(), Setting.getNG_Word())){
			return null;
		}
		return middlefile;
	}

	private boolean convertVideo() {
		sendtext("����̕ϊ����J�n");
		/*�r�f�I���̊m��*/
		File folder = Setting.getConvFixFileNameFolder();
		if (Setting.isConvFixFileName()) {
			folder.mkdir();
			String conv_name = VideoTitle;
			if (!Setting.isNotAddVideoID_Conv()) {//�t������Ȃ�
				if (conv_name == null){
					conv_name = VideoID;
				} else {
					conv_name = VideoID + conv_name;
				}
			}
			if (conv_name == null || conv_name.isEmpty()) {
				sendtext("�ϊ���̃r�f�I�t�@�C�������m��ł��܂���B");
				return false;
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
		if (ConvertedVideoFile.getPath().equals(VideoFile.getPath())){
			sendtext("�ϊ���̃t�@�C�������ϊ��O�Ɠ����ł�");
			return false;
		}
		int code = converting_video(TMP_COMMENT,TMP_OWNERCOMMENT);
		Stopwatch.stop();
		if (code == 0) {
			sendtext("�ϊ�������ɏI�����܂����B");
			System.out.println("�ϊ����ԁ@" + Stopwatch.formatLatency());
			System.out.println(LastFrame);
			return true;
		} else if (code == CODE_CONVERTING_ABORTED) { /*���f*/

		} else {
			sendtext("�ϊ��G���[�F" + LastError);
		}
		return false;
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
				if (!client.getVideoInfo(Tag, Time)) {
					sendtext(Tag + "�̏��̎擾�Ɏ��s");
					return;
				}
				sendtext(Tag + "�̏��̎擾�ɐ���");
				if (stopFlagReturn()) {
					return;
				}
				VideoTitle = client.getVideoTitle();
			}

			if (!saveVideo(client) || stopFlagReturn()) {
				return;
			}

			if (!saveComment(client) || stopFlagReturn()){
				return;
			}

			if (!saveOwnerComment(client) || stopFlagReturn()) {
				return;
			}

			if (!isSaveConverted()) {
				sendtext("����E�R�����g��ۑ����A�ϊ��͍s���܂���ł����B");
				return;
			}

			if (!convertComment() || stopFlagReturn()) {
				return;
			}

			if (!convertOwnerComment() || stopFlagReturn()){
				return;
			}

			if (convertVideo()) {
				if (isDeleteCommentAfterConverting()
					&& CommentFile != null) {
					deleteCommentFile();
					// OwnerCommentFile.delete();
				}
				if (isDeleteVideoAfterConverting()
					&& VideoFile != null) {
					VideoFile.delete();
				}
			}
		} finally {
			StopFlag.finished();
			if (CommentMiddleFile != null) {
				//  �f�o�b�O�̂��߃R�����g�t�@�C�����c���Ă���
				// CommentMiddleFile.delete();
			}
			if (OwnerMiddleFile != null){
				//	���e�҃R�����g�͍폜���Ȃ��B
				// OwnerMiddleFile.delete();
			}
			Stopwatch.show();
		}
	}

	String LastError = "�G���[��񂪂���܂���";
	String LastFrame = "";

	private static final int CODE_CONVERTING_ABORTED = 100;

	private int converting_video(String vhook_path, String vhookowner_path) {
		Process process = null;
		BufferedReader ebr = null;
		File fwsFile = Cws2Fws.createFws(VideoFile);

		StringBuffer sb = new StringBuffer();
		sb.append("\"");
		sb.append(Setting.getFFmpegPath().replace("\\", "/"));
		sb.append("\"");
		sb.append(" -y ");
		sb.append(MainOption);
		sb.append(" ");
		sb.append(InOption);
		sb.append(" -i ");
		if (fwsFile == null) {
			sb.append("\"");
			sb.append(VideoFile.getPath().replace("\\", "/"));
			sb.append("\"");
		} else {
			sb.append(fwsFile.getPath().replace("\\", "/"));
		}
		sb.append(" ");
		sb.append(OutOption);
		if (!Setting.isVhookDisabled()) {
			if(!addVhookSetting(sb, vhook_path, vhookowner_path)){
				return -1;
			}
		}
		sb.append(" \"");
		sb.append(ConvertedVideoFile.getPath().replace("\\", "/"));
		sb.append("\"");
		String cmd = sb.substring(0);
		System.out.println("arg:" + cmd);
		try {
			System.out.println("\n\n----\nProcessing FFmpeg...\n----\n\n");
			process = Runtime.getRuntime().exec(cmd);
			ebr = new BufferedReader(new InputStreamReader(
					process.getErrorStream()));
			String e;
			while ((e = ebr.readLine()) != null) {
				LastError = e;
				if (e.startsWith("frame=")) { //
					sendtext(e);
					LastFrame = e;
					Stopwatch.show();
				} else if(!e.endsWith("No accelerated colorspace conversion found")){
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
			try {
				process.getErrorStream().close();
				ebr.close();
				if (fwsFile != null) {
				//	fwsFile.delete();	// For DEBUG
				}
			} catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}

	private boolean addVhookSetting(StringBuffer sb,
			String vhook_path, String vhookowner_path) {
		try {
			sb.append(" -vfilters \"vhext=");
			sb.append(Setting.getVhookPath().replace("\\", "/"));
			sb.append("|");
			if(CommentMiddleFile!=null){
				sb.append("--data-user:");
				sb.append(URLEncoder.encode(
					vhook_path.replace("\\","/"),"Shift_JIS"));
				sb.append("|");
			}
			if(OwnerMiddleFile!=null){
				sb.append("--data-owner:");
				sb.append(URLEncoder.encode(
					vhookowner_path.replace("\\","/"),"Shift_JIS"));
				sb.append("|");
			}
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

	boolean detectOption() {
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
	public String getInOption(){
		if (detectOption()){
			return InOption;
		} else {
			return null;
		}
	}
	/**
	 *
	 * @author orz
	 *
	 */
	private class VideoIDFilter implements FilenameFilter {
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

	String detectTitleFromVideo(File dir){
		String list[] = dir.list(new VideoIDFilter(VideoID));
		if(list == null){
			return null;
		}
		for (int i = 0; i < list.length; i++) {
			if (list[i].startsWith(VideoID)) {
				String path = list[i];
				if(!path.endsWith(".flv")){
					OtherVideo = path;
					continue;
				}
				if (VideoTitle != null){
					return path;
				}
				VideoTitle = path.substring(VideoID.length());
				int lastindex = VideoTitle.lastIndexOf('.');
				if (lastindex >= 0) {
					VideoTitle = VideoTitle.substring(0,lastindex);
				}
				return path;
			}
		}
		return null;
	}

	private String detectTitleFromComment(File dir){
		String list[] = dir.list(new VideoIDFilter(VideoID));
		if(list == null){ return null; }
		for (int i = 0; i < list.length; i++) {
			String path = list[i];
			if (!path.endsWith(".xml") || path.endsWith(OWNER_EXT)){
				continue;
			}
			if (VideoTitle == null){
				int fromindex = VideoID.length();
					// ���̂Ȃ�� VideoID �����ɕt������
				int index = path.lastIndexOf("[");
					//�ߋ����O��[YYYY/MM/DD_HH:MM:SS]���Ō�ɕt��
				if (index >= fromindex){
					VideoTitle = path.substring(fromindex, index);
				} else {
					VideoTitle = path.substring(
							fromindex, path.lastIndexOf("."));
				}
			}
			return path;
		}
		return null;
	}

	private static final String TCOMMENT_EXT =".txml";
	private String detectTitleFromOwnerComment(File dir){
		String list[] = dir.list(new VideoIDFilter(VideoID));
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
			if (VideoTitle == null){
				VideoTitle = path.substring(VideoID.length(),
						path.lastIndexOf(ext));
			}
			return path;
		}
		return null;
	}

	private ArrayList<String> detectFilelistFromComment(File dir){
		String list[] = dir.list(new VideoIDFilter(VideoID));
		if (list == null) { return null; }
		ArrayList<String> filelist = new ArrayList<String>();
		for (int i = 0; i < list.length; i++){
			String path = list[i];
			if (!path.endsWith(".xml") || path.endsWith(OWNER_EXT)){
				continue;
			}
			if (VideoTitle == null){
				int fromindex = VideoID.length();
					// ���̂Ȃ�� VideoID �����ɕt������
				int index = path.lastIndexOf("[");
					//�ߋ����O��[YYYY/MM/DD_HH:MM:SS]���Ō�ɕt��
				if (index >= fromindex){
					VideoTitle = path.substring(fromindex, index);
				} else {
					VideoTitle = path.substring(
							fromindex, path.lastIndexOf(".xml"));
				}
			}
			filelist.add(path);
		}
		if (filelist.isEmpty()){
			return null;
		}
		return filelist;
	}

	/*
	 * 	���e�҃R�����g�Ɋւ���t�@�C�����^�O�Ɗg���q������
	 * 		�@ NicoBrowser�g��1.4.4�̏ꍇ
	 * 			���[�U�R�����g = VideiID + VideoTitle + ".xml"
	 * 			���e�҃R�����g = VideoID + VideoTitle + ".txml"
	 * 		�A����̂�����΂�
	 * 			���[�U�R�����g = VideoID + VideoTitle + ".xml"
	 * 			���e�҃R�����g = VideoID + VideoTitle + "{Owner].xml"
	 * 		�BNNDD�Ȃ�
	 * 			���[�U�R�����g = VideoTitle + VideoID + ".xml"
	 * 			���e�҃R�����g = VideoTitle + VideoID + "{Owner].xml"
	 * 		�CNicoPlayer�Ȃ�
	 * 			���[�U�R�����g = VideoTitle + "(" + Tag + ")" + ".xml"
	 * 			�ߋ����O       = VideoTitie + "(" + Tag + ")[" + YYYY�NMM��DD��HH��MM��SS�b + "}.xml"
	 * 			���e�҃R�����g = VideoTitle + "(" + Tag + "){Owner].xml"
	 *
	 *
	 */

}
