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
	//���[���A�h���X
	private String Mailaddr;
	private static final String DefMailaddr = "�����Ƀ��[���A�h���X����͂��Ă��������B";
	private static final String PropMailaddr = "Mailaddr";
	//�p�X���[�h
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
		//���[���A�h���X
		str = prop.getProperty(PropMailaddr);
		if(str != null){
			Mailaddr = str;
		}else{
			Mailaddr = DefMailaddr;
		}
		//�p�X���[�h
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
		//���[���A�h���X
		prop.setProperty(PropMailaddr, Mailaddr);
		//�p�X���[�h
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
	 * @param mailaddr �ݒ肷�� mailaddr
	 */
	public void setMailaddr(String mailaddr) {
		Mailaddr = mailaddr;
	}

	/**
	 * @param password �ݒ肷�� password
	 */
	public void setPassword(String password) {
		Password = password;
	}

}
