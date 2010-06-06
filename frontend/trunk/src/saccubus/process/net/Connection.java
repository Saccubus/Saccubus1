/**
 * �ʂ̃_�E�����[�h�̂��߂̃N���X
 */
package saccubus.process.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * @author PSI
 *
 */
public class Connection {
	private int ContentLength;
	private InputStream Input;
	private HttpURLConnection HttpURLConnection;
	private State Status;
	public enum State {
		NORMAL,
		REDIRECTED
	}
	/**
	 * @param contentLength
	 * @param input
	 * @param connection
	 */
	protected Connection(HttpURLConnection connection,InputStream input,int contentLength) {
		ContentLength = contentLength;
		Input = input;
		if(Input == null){
			Status = State.REDIRECTED;
		}else{
			Status = State.NORMAL;
		}
		HttpURLConnection = connection;
	}
	/**
	 * �e�L�X�g�Ƃ��ēǂ�ŕԂ�
	 * @return
	 */
	public String loadString(){
		BufferedReader br = new BufferedReader(new InputStreamReader(Input));
		String str;
		StringBuffer sb = (ContentLength < 0) ? new StringBuffer(ContentLength) : new StringBuffer();
		try {
			while((str = br.readLine())!=null){
				sb.append(str);
				sb.append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		HttpURLConnection.disconnect();
		return sb.toString();
	}
	/**
	 * �ǂݍ��ݎ��ɗp����o�b�t�@
	 */
	private final byte[] Buff = new byte[1048576];
	/**
	 * �R�[���o�b�N�N���X���g���ēǂݍ���
	 * @param callback
	 * @return
	 */
	public boolean loadCallback(ConnectionCallback callback){
		int size = 0;
		int read;
		boolean ret = true;
		try {
			while ((size < ContentLength) && (read = Input.read(Buff, 0, Buff.length)) > 0) {
				//���܂œǂݍ���ł����f�[�^���Ăяo��
				size += read;
				//�R�[���o�b�N���Ăяo��
				boolean call = callback.connectionRead(ContentLength,size, Buff, read);
				if(!call){
					ret = false;
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			ret = false;
		}
		return ret;
	}
	/**
	 * �w�b�_�����擾����
	 * FIXME:�{���͔z��ŕԂ����ق��������񂾂��ǁA���܂�Ƃ������Ă邵�߂�ǂ�����������u�B
	 * �����I�ɖ�肪�N������ύX���ĂˁB
	 * @param header
	 * @return
	 */
	public String getHeaderInfo(String header){
		int i = 1;
		String key;
		String value;
		while ((key = HttpURLConnection.getHeaderFieldKey(i)) != null) {
			if (key.equalsIgnoreCase(header)) {
				value = HttpURLConnection.getHeaderField(i);
				if (value != null) {
					return value;
				}
			}
			i++;
		}
		return null;
	}
	/**
	 * ����
	 * @throws IOException 
	 */
	public void close() throws IOException{
		Input.close();
		HttpURLConnection.disconnect();
	}
	/**
	 * @return contentLength
	 */
	public int getContentLength() {
		return ContentLength;
	}
	/**
	 * @return input
	 */
	public InputStream getInput() {
		return Input;
	}
	/**
	 * @return uRLConnection
	 */
	public HttpURLConnection getHttpURLConnection() {
		return HttpURLConnection;
	}
	/**
	 * @return status
	 */
	public State getStatus() {
		return Status;
	}
}
