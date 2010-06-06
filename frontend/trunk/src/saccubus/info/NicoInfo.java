/**
 * 
 */
package saccubus.info;

import java.util.Properties;

/**
 * @author PSI
 *
 */
public class NicoInfo implements Info {
	//メールアドレス
	private String Mailaddr;
	private static final String DefMailaddr = "ここにメールアドレスを入力してください。";
	private static final String PropMailaddr = "Mailaddr";
	//パスワード
	private String Password;
	private static final String DefPassword = "";
	private static final String PropPassword = "Password";

	/**
	 * 
	 */
	public NicoInfo() {
	}

	/**
	 * @param mailaddr
	 * @param password
	 */
	public NicoInfo(String mailaddr, String password) {
		Mailaddr = mailaddr;
		Password = password;
	}

	/* (non-Javadoc)
	 * @see saccubus.info.Info#loadInfo(java.util.Properties)
	 */
	public boolean loadInfo(Properties prop) {
		String str;
		//メールアドレス
		str = prop.getProperty(PropMailaddr);
		if(str != null){
			Mailaddr = str;
		}else{
			Mailaddr = DefMailaddr;
		}
		//パスワード
		str = prop.getProperty(PropPassword);
		if(str != null){
			Password = str;
		}else{
			Password= DefPassword;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see saccubus.info.Info#saveInfo(java.util.Properties)
	 */
	public boolean saveInfo(Properties prop) {
		//メールアドレス
		prop.setProperty(PropMailaddr, Mailaddr);
		//パスワード
		prop.setProperty(PropPassword, Password);
		return true;
	}

	/**
	 * @return mailaddr
	 */
	public String getMailaddr() {
		return Mailaddr;
	}

	/**
	 * @return password
	 */
	public String getPassword() {
		return Password;
	}

	/**
	 * @param mailaddr 設定する mailaddr
	 */
	public void setMailaddr(String mailaddr) {
		Mailaddr = mailaddr;
	}

	/**
	 * @param password 設定する password
	 */
	public void setPassword(String password) {
		Password = password;
	}

}
