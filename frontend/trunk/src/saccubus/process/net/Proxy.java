/**
 * 
 */
package saccubus.process.net;

import java.net.InetSocketAddress;

import saccubus.info.NetworkInfo;

/**
 * @author PSI
 *
 */
public class Proxy {
	private java.net.Proxy Proxy;
	/**
	 * プロキシ
	 * @param network_info
	 */
	protected Proxy(NetworkInfo info) {
		String proxy = info.getProxy();
		int proxy_port = info.getProxyPort();
		if(info.isUseProxy()
				&& (proxy != null && proxy.length() > 0)
				&& (proxy_port >= 0 && proxy_port <= 0xffff)
			){
			Proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, 
					new InetSocketAddress(proxy,proxy_port));
		}else{
			Proxy = java.net.Proxy.NO_PROXY;
		}
	}
	/**
	 * プロキシを取得
	 * @return
	 */
	protected java.net.Proxy getProxy(){
		return Proxy;
	}

}
