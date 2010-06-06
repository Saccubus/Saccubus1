/**
 * �N�b�L�[�p���[�e�B���e�B�B
 * �N�b�L�[�̎擾�A�Ǘ��Ȃǂ��s���B��{�I�ɑS��static���\�b�h�B
 */
package saccubus.process.net;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import saccubus.info.NicoInfo;
import saccubus.util.SystemUtil;

/**
 * @author PSI
 *
 */
public class NicoCookie {
	private static String Cookie = null;
	private ConnectionMaker ConMaker;
	private NicoInfo NicoInfo;
	/**
	 * @param conMaker
	 */
	protected NicoCookie(NicoInfo nicoInfo,ConnectionMaker conMaker) {
		NicoInfo = nicoInfo;
		ConMaker = conMaker;
	}
	/**
	 * �N�b�L�[���擾����B
	 * @param last_failed
	 * @return
	 */
	protected String getCookie(boolean last_failed){
		if(!last_failed && Cookie != null){
			return Cookie;
		}
		//�V�X�e�����̃N�b�L�[���g���Ȃ����ǂ����`�F�b�N���Ă݂�B
		String cookie = SystemUtil.getCookie();
		if(checkLogin(cookie)){
			Cookie = cookie;
			return cookie;
		}
		//���O�C�����ăN�b�L�[���擾����B
		cookie = login();
		if(cookie == null){
			Cookie = null;
			return null;
		}
		int index = cookie.indexOf(";");
		//��������������
		if(index < 0){
			Cookie = null;
			return null;
		}
		//�V�X�e�����ɐݒ肷��B
		SystemUtil.setCookie(cookie);
		//�������ԂȂǂ̏��͍폜����B
		Cookie = cookie.substring(0, index);
		return Cookie;
	}
	/**
	 * �N�b�L�[����ǉ�����B
	 * @param con
	 */
	protected void addCookie(Connection con){
		if(con == null){
			return;
		}
		String add = con.getHeaderInfo("Set-Cookie");
		if(add == null){
			return;
		}
		int index = add.indexOf(";");
		if(index < 0){
			return;
		}
		Cookie += "; ";
		Cookie += add.substring(0,index);
	}
	/**
	 * ���̃N�b�L�[�Ń��O�C���ł��邩�ǂ������`�F�b�N����
	 * @param cookie
	 * @return
	 */
	private boolean checkLogin(String cookie){
		Connection con = ConMaker.makeConnection(false, true, NicoUtil.NICO_TOP_URL);
		String let = con.loadString();
		//���O�C��URL�����݂��Ȃ������O�C���ł��Ă���
		return let.indexOf(NicoUtil.NICO_LOGIN_URL) < 0;
	}
	/**
	 * ���O�C������
	 * @return
	 */
	private String login(){
		String login;
		try {
			StringBuffer sb = new StringBuffer(4096);
			sb.append("next_url=&");
			sb.append("mail=");
			sb.append(URLEncoder.encode(NicoInfo.getMailaddr(), "UTF-8"));
			sb.append("&password=");
			sb.append(URLEncoder.encode(NicoInfo.getPassword(), "UTF-8"));
			sb.append("&submit.x=103&submit.y=16");
			login = sb.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		Connection con = ConMaker.makeConnection(false, false, NicoUtil.NICO_LOGIN_URL,login);
		if(con.getStatus() == Connection.State.REDIRECTED){
			return  con.getHeaderInfo("Set-Cookie");
		}
		return null;
	}
}
