/**
 * 
 */
package saccubus.process.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import saccubus.info.NetworkInfo;
import saccubus.info.NicoInfo;

/**
 * @author PSI
 *
 */
public class ConnectionMaker {
	private Proxy Proxy;
	private NicoCookie Cookie;
	/**
	 * @param netInfo
	 */
	public ConnectionMaker(NicoInfo nicoInfo,NetworkInfo netInfo) {
		Proxy = new Proxy(netInfo);
		Cookie = new NicoCookie(nicoInfo,this);
	}
	/**
	 * �|�X�g�𗘗p�����R�l�N�V�������͂�
	 * @param url
	 * @param post
	 * @return
	 */
	protected Connection makeConnection(boolean last_failed, String url,String post){
		return makeConnection(last_failed, true,true,url,post);
	}
	/**
	 * �|�X�g�𗘗p�����N�b�L�[��p���Ȃ��R�l�N�V�����𒣂�
	 * @param last_failed
	 * @param follow_redirect
	 * @param url
	 * @param post
	 * @return
	 */
	protected Connection makeConnection(boolean last_failed, boolean follow_redirect,String url,String post){
		return makeConnection(last_failed, false, follow_redirect,url,post);
	}
	/**
	 * �|�X�g�𗘗p�����R�l�N�V�������͂�
	 * @param use_cookie
	 * @param url
	 * @param post
	 * @return
	 */
	protected Connection makeConnection(boolean last_failed, boolean use_cookie,boolean follow_redirect,String url,String post){
		try {
			HttpURLConnection con;
			con = (HttpURLConnection) (new URL(url)).openConnection(Proxy.getProxy());
			con.setInstanceFollowRedirects(follow_redirect);
			//���N�G�X�g���[�h�̐ݒ�
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestMethod("POST");
			//�j�R�j�R����̃T�C�g�Ȃ�΃N�b�L�[��ݒ肷��B
			if(checkNicoURL(url)){
				String cookie = Cookie.getCookie(last_failed);
				if(cookie == null){
					return null;
				}
				con.addRequestProperty("Cookie", cookie);
			}
			//�R�l�N�V�����͖���؂��Ă��ǂ��񂶂�Ȃ����Ȃ��B
			con.addRequestProperty("Connection", "close");
			con.connect();
			//POST�f�[�^����������
			OutputStream os = con.getOutputStream();
			os.write(url.getBytes("UTF-8"));
			os.flush();
			os.close();
			//���X�|���X�R�[�h�̎擾
			int rescode = con.getResponseCode();
			if (rescode != HttpURLConnection.HTTP_OK) {
				return null;
			}else if(rescode >= 300 && rescode < 400){
				//Input == null�̓��_�C���N�g������킷
				return new Connection(con,null,-1);
			}
			InputStream is = con.getInputStream();
			//�t�@�C���̒������擾����B
			String content_length_str = con.getHeaderField("Content-length");
			int max_size = -1;
			if (content_length_str != null && !content_length_str.equals("")) {
				try {
					max_size = Integer.parseInt(content_length_str);
				} catch (NumberFormatException e) {
					max_size = -1;
				}
			}
			//�I�u�W�F�N�g�𐶐����ĕԂ�
			return new Connection(con,is,max_size);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * GET�𗘗p�����R�l�N�V�����𒣂�
	 * @param url
	 * @return
	 */
	public Connection makeConnection(boolean last_failed, String url){
		return makeConnection(last_failed,true,true,url);
	}
	/**
	 * GET�𗘗p�����N�b�L�[��p���Ȃ��R�l�N�V�����𒣂�
	 * @param url
	 * @return
	 */
	protected Connection makeConnection(boolean last_failed,boolean follow_redirect, String url){
		return makeConnection(last_failed,follow_redirect,url);
	}
	/**
	 * GET�𗘗p�����R�l�N�V�����𒣂�
	 * @param url
	 * @return
	 */
	protected Connection makeConnection(boolean last_failed, boolean use_proxy, boolean follow_redirect, String url){
		HttpURLConnection con;
		try {
			con = (HttpURLConnection) (new URL(url)).openConnection(Proxy.getProxy());
			con.setInstanceFollowRedirects(follow_redirect);
			//�o�͂̂�
			con.setDoInput(true);
			con.setRequestMethod("GET");
			//�j�R�j�R����Ȃ�΃N�b�L�[������
			if(checkNicoURL(url)){
				String cookie = Cookie.getCookie(last_failed);
				if(cookie == null){
					return null;
				}
				con.addRequestProperty("Cookie", cookie);
			}
			//�R�l�N�V�����͖���؂鎖�ɂ��Ă����B
			con.addRequestProperty("Connection", "close");
			//�ڑ����ă��X�|���X�R�[�h�̎擾
			con.connect();
			int rescode = con.getResponseCode();
			if (rescode != HttpURLConnection.HTTP_OK) {
				return null;
			}else if(rescode >= 300 && rescode < 400){
				//Input == null�̓��_�C���N�g������킷
				return new Connection(con,null,-1);
			}
			InputStream is = con.getInputStream();
			//�t�@�C���̒������擾����B
			String content_length_str = con.getHeaderField("Content-length");
			int max_size = -1;
			if (content_length_str != null && !content_length_str.equals("")) {
				try {
					max_size = Integer.parseInt(content_length_str);
				} catch (NumberFormatException e) {
					max_size = -1;
				}
			}
			//�I�u�W�F�N�g�𐶐����ĕԂ�
			return new Connection(con,is,max_size);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * URL���j�R�j�R����A�X�}�C���r�f�I���ǂ����̃`�F�b�N
	 * @param url
	 * @return
	 */
	private static boolean checkNicoURL(String url){
		url = url.toLowerCase();
		if(!url.startsWith("http://")){
			return false;
		}
		int idx = url.indexOf("/",7);
		if(url.indexOf(NicoUtil.NICO_DOMAIN) < idx){
			return true;
		}
		return false;
	}
}
