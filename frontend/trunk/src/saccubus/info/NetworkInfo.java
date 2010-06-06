/**
 * 
 */
package saccubus.info;

import java.util.Properties;

/**
 * @author PSI
 *
 */
public class NetworkInfo implements Info {
	public NetworkInfo() {
	}
	/**
	 * @param useProxy
	 * @param proxy
	 * @param proxyPort
	 */
	public NetworkInfo(boolean useProxy, String proxy, int proxyPort) {
		UseProxy = useProxy;
		Proxy = proxy;
		ProxyPort = proxyPort;
	}

	/**
	 * �����o�[�A�萔��`
	 */
	//�v���L�V�͎g���́H
	private boolean UseProxy;
	private final static boolean DefUseProxy = false;
	private final static String PropUseProxy = "UseProxy";
	//�v���L�V�̃A�h���X�́H
	private String Proxy;
	private final static String DefProxy = "127.0.0.1";
	private final static String PropProxy = "Proxy";
	//�v���L�V�̃|�[�g�́H
	private int ProxyPort;
	private final static int DefProxyPort = 8080;
	private final static String PropProxyPort = "ProxyPort";
	
	/**
	 * ���\�b�h��`
	 */
	
	/* (non-Javadoc)
	 * @see saccubus.info.Info#loadInfo(java.util.Properties)
	 */
	public boolean loadInfo(Properties prop) {
		String str;
		//�v���L�V�͎g���́H
		str = prop.getProperty(PropUseProxy);
		if(str != null){
			UseProxy = Boolean.parseBoolean(str);
		}else{
			UseProxy = DefUseProxy;
		}
		//�v���L�V�̃A�h���X�́H
		str = prop.getProperty(PropProxy);
		if(str != null){
			Proxy = str;
		}else{
			Proxy = DefProxy;
		}
		
		//�v���L�V�̃|�[�g�́H
		str = prop.getProperty(PropProxyPort);
		if(str != null){
			try {
				ProxyPort = Integer.parseInt(str);
			} catch (NumberFormatException e) {
				ProxyPort = -1;
			}
		}else{
			ProxyPort = DefProxyPort;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see saccubus.info.Info#saveInfo(java.util.Properties)
	 */
	public boolean saveInfo(Properties prop) {
		//�v���L�V�͎g���́H
		prop.setProperty(PropUseProxy, Boolean.toString(UseProxy));
		//�v���L�V�̃A�h���X�́H
		prop.setProperty(PropProxy, Proxy);
		//�v���L�V�̃|�[�g�́H
		prop.setProperty(PropProxyPort, Integer.toString(ProxyPort));
		return true;
	}
	/**
	 * @return proxy
	 */
	public String getProxy() {
		return Proxy;
	}
	/**
	 * @return proxyPort
	 */
	public int getProxyPort() {
		return ProxyPort;
	}
	/**
	 * @return useProxy
	 */
	public boolean isUseProxy() {
		return UseProxy;
	}
	/**
	 * @param proxy �ݒ肷�� proxy
	 */
	public void setProxy(String proxy) {
		Proxy = proxy;
	}
	/**
	 * @param proxyPort �ݒ肷�� proxyPort
	 */
	public void setProxyPort(int proxyPort) {
		ProxyPort = proxyPort;
	}
	/**
	 * @param useProxy �ݒ肷�� useProxy
	 */
	public void setUseProxy(boolean useProxy) {
		UseProxy = useProxy;
	}
}
