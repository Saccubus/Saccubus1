/**
 * ���܂胍�W�b�N�Ƃ͖��֌W�̒萔��Ԃ����肷��悤�ȃN���X
 */
package saccubus.process.net;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import saccubus.util.FileUtil;

/**
 * @author PSI
 *
 */
public class NicoUtil {
	/**
	 * �r�f�I�̒�������擾����R�����g����Ԃ�
	 * @param video_length
	 * @param def
	 * @return
	 */
	public int getBackCommentFromLength(int video_length,int def) {
		if (video_length < 0) {
			return def;
		} else if (video_length >= 60 && video_length < 300) {
			return 250;
		} else if (video_length >= 300 && video_length < 600) {
			return 500;
		} else {
			return 1000;
		}
	}
	/**
	 * �r�f�I�^�C�g���擾�p�̂߂��邵
	 */
	private static final String TITLE_PARSE_STR_START = "<title>";
	/**
	 * �r�f�I�y�[�W��HTML���瓮��^�C�g�����擾����B
	 * @param html
	 * @return
	 */
	public String parseVideoTitle(String html){
		int index;
		if ((index = html.indexOf(TITLE_PARSE_STR_START)) >= 0) {
			int index2 = html.indexOf("�]", index);
			if(index2 >= 0){
				String title = html.substring(index+TITLE_PARSE_STR_START.length(), index2);
				return FileUtil.safeFileName(title);
			}
		}
		return "�^�C�g���擾�Ɏ��s���܂����B";
	}
	/**
	 * �ϊ��p�t�H�[�}�b�g�i�b�t���j
	 */
	private final static DateFormat DateFmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	/**
	 * �ϊ��p�t�H�[�}�b�g�i�b�Ȃ��j
	 */
	private final static DateFormat DateFmt2 = new SimpleDateFormat(	"yyyy/MM/dd HH:mm");
	/**
	 * ���͂��ꂽ���Ԃ���A�j�R�j�R����Ŏg���鎞�Ԃɕϊ�����B
	 * @param time
	 * @return
	 */
	public long parseWayBackTime(String time){
		Date date = null;
		long waybacktime = -1;
		try {
			date = DateFmt.parse(time);
		} catch (ParseException ex2) {
			date = null;
		}
		if (date == null) {
			try {
				date = DateFmt2.parse(time);
			} catch (ParseException ex3) {
				date = null;
			}
		}
		if (date != null) {
			waybacktime = date.getTime() / 1000;
		} else {
			try {
				waybacktime = Long.parseLong(time);
			} catch (NumberFormatException ex4) {
				ex4.printStackTrace();
			}
		}
		return waybacktime;
	}
	/**
	 * �ߋ����O�擾�L�[�̂��߂̃p�[�T
	 */
	private final static String WAYBACKKEY_STR = "waybackkey=";
	/**
	 * �ߋ����O�擾�p�̌����p�[�X����B
	 * @param ret
	 * @return
	 */
	public String parseWayBackKey(String ret){
		int idx = 0;
		if ((idx = ret.indexOf(WAYBACKKEY_STR)) < 0) {
			return null;
		}
		int end_idx = Math.max(ret.length(), ret.indexOf("&"));
		String waybackkey = ret.substring(idx + WAYBACKKEY_STR.length(),
				end_idx);
		if (waybackkey == null || waybackkey.equals("")) {
			return null;
		}
		return waybackkey;
	}
	/**
	 * ����̏����擾����B
	 * @param video_id
	 * @param info
	 * @return
	 */
	public VideoInformation parseVideoInfo(String video_id,String info){
		try {//UTF-8�Ńf�R�[�h�B
			info = URLDecoder.decode(info, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//�����Ŏ擾�ł�����
		String thread_id = null;
		String video_url = null;
		String msg_url = null;
		String user_id = null;
		int deleted = -1;
		int video_length = -1;
		boolean is_premium = false;
		//��؂蕶����"&"
		String[] array = info.split("&");
		for (int i = 0; i < array.length; i++) {
			int idx = array[i].indexOf("=");
			if (idx < 0) {
				continue;
			}
			String key = array[i].substring(0, idx);
			String value = array[i].substring(idx + 1);
			if (thread_id == null && key.equalsIgnoreCase("thread_id")) {
				thread_id = value;
			} else if (video_url == null && key.equalsIgnoreCase("url")) {
				video_url = value;
			} else if (msg_url == null && key.equalsIgnoreCase("ms")) {
				msg_url = value;
			} else if (user_id == null && key.equalsIgnoreCase("user_id")) {
				user_id = value;
			} else if (video_length < 0 && key.equalsIgnoreCase("l")) {
				try {
					video_length = Integer.parseInt(value);
				} catch (NumberFormatException e) {
					video_length = -1;
				}
			} else if (deleted < 0 && key.equalsIgnoreCase("deleted")){
				try {
					deleted = Integer.parseInt(value);
				} catch (NumberFormatException e) {
					deleted = -1;
				}
			} else if(!is_premium && key.equalsIgnoreCase("is_premium")){
				is_premium = !value.equals("0");
			}
		}
		return new VideoInformation(video_id,video_url,thread_id,msg_url,user_id,deleted,video_length,is_premium);
	}
	/**
	 * ���[�U�R�����g�擾�p�̃��N�G�X�g�̎擾
	 * @param user
	 * @param threadid
	 * @param back_comment
	 * @param waybackkey
	 * @param waybacktime
	 * @return
	 */
	public String getUserCommentPost(String user,String threadid,int back_comment,String waybackkey,int waybacktime){
		if(waybackkey == null){
			waybackkey = "0";
		}
		if(waybacktime < 0){
			waybacktime = 0;
		}
		return
			"<thread user_id=\"" + user + "\" when=\"" + 
			waybacktime + "\" waybackkey=\"" + waybackkey + 
			"\" res_from=\"-" + back_comment + 
			"\" version=\"20061206\" thread=\"" + threadid + "\" />";
	}
	/**
	 * �I�[�i�[�R�����g�p���N�G�X�g�̎擾
	 * @param user
	 * @param threadid
	 * @return
	 */
	public String getOwnerCommentPost(String user,String threadid){
		return
			"<thread fork=\"1\" user_id=\""+user+
			"\" res_from=\"1000\" version=\"20061206\" thread=\""+threadid+"\" />";
	}
	/*
	 * �j�R�j�R����֌W�̒萔�ꗗ
	 */

	/**
	 * �j�R�j�R����̃g�b�vURL
	 */
	public static final String NICO_TOP_URL = "http://www.nicovideo.jp/";
	/**
	 * �j�R�j�R����Ƀ��O�C������ۂ�URL
	 */
	public static final String NICO_LOGIN_URL = "https://secure.nicovideo.jp/secure/login?site=niconico";
	/**
	 * �j�R�j�R����̃h���C��
	 */
	public static final String NICO_DOMAIN = "nicovideo.jp";
	/**
	 * �ʂ̓���̏����擾���邽�߂�URL
	 */
	public static final String NICO_VIDEO_INFO_URL = "http://www.nicovideo.jp/api/getflv?v=";
	/**
	 * �ߋ����O�擾�L�[�̎擾URL
	 */
	public static final String NICO_WAYBACKKEY_URL = "http://www.nicovideo.jp/api/getwaybackkey?thread=";
	/**
	 * �ʂ̓���̃y�[�WURL
	 */
	public static final String NICO_VIDEO_PAGE_URL = "http://www.nicovideo.jp/watch/";
}
