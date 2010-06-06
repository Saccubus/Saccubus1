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
	 * メンバー、定数定義
	 */
	//プロキシは使うの？
	private boolean UseProxy;
	private final static boolean DefUseProxy = false;
	private final static String PropUseProxy = "UseProxy";
	//プロキシのアドレスは？
	private String Proxy;
	private final static String DefProxy = "127.0.0.1";
	private final static String PropProxy = "Proxy";
	//プロキシのポートは？
	private int ProxyPort;
	private final static int DefProxyPort = 8080;
	private final static String PropProxyPort = "ProxyPort";
	
	/**
	 * メソッド定義
	 */
	
	/* (non-Javadoc)
	 * @see saccubus.info.Info#loadInfo(java.util.Properties)
	 */
	public boolean loadInfo(Properties prop) {
		String str;
		//プロキシは使うの？
		str = prop.getProperty(PropUseProxy);
		if(str != null){
			UseProxy = Boolean.parseBoolean(str);
		}else{
			UseProxy = DefUseProxy;
		}
		//プロキシのアドレスは？
		str = prop.getProperty(PropProxy);
		if(str != null){
			Proxy = str;
		}else{
			Proxy = DefProxy;
		}
		
		//プロキシのポートは？
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
		//プロキシは使うの？
		prop.setProperty(PropUseProxy, Boolean.toString(UseProxy));
		//プロキシのアドレスは？
		prop.setProperty(PropProxy, Proxy);
		//プロキシのポートは？
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
	 * @param proxy 設定する proxy
	 */
	public void setProxy(String proxy) {
		Proxy = proxy;
	}
	/**
	 * @param proxyPort 設定する proxyPort
	 */
	public void setProxyPort(int proxyPort) {
		ProxyPort = proxyPort;
	}
	/**
	 * @param useProxy 設定する useProxy
	 */
	public void setUseProxy(boolean useProxy) {
		UseProxy = useProxy;
	}
}
