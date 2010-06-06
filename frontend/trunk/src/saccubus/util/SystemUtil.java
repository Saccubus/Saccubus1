/**
 * OS�`�F�b�N�Ƃ����̕ӗp
 */
package saccubus.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author PSI
 *
 */
public class SystemUtil {
	private static final String OS_STR = System.getProperty("os.name","").toLowerCase();
	public static final boolean isSystemWindows = OS_STR.indexOf("windows") >= 0;
	public static final boolean isSystemLinux = OS_STR.indexOf("linux") >= 0;
	public static final boolean isSystemMac = OS_STR.indexOf("mac") >= 0;
	/**
	 * �V�X�e���̃N�b�L�[���擾���܂��B
	 * @return
	 */
	public static final String getCookie(){
		if(!isSystemWindows){
			return null;
		}
		try {
			Process pr = Runtime.getRuntime().exec(".\\bin\\cookie.exe");
			BufferedReader br = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String cookie = br.readLine();
			br.close();
			pr.waitFor();
			if(pr.exitValue() == 0){
				return cookie;
			}else{
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * �V�X�e���̃N�b�L�[��ݒ肵�܂��B
	 * @param str
	 * @return
	 */
	public static final boolean setCookie(final String str){
		if(!isSystemWindows){
			return false;
		}
		try {
			Process pr = Runtime.getRuntime().exec(".\\bin\\cookie.exe \""+str+"\"");
			pr.waitFor();
			return pr.exitValue() == 0;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}
}
