/**
 * 設定項目は必ずこれを継承（だっけ）をしてなければならない。
 */
package saccubus.info;

import java.util.Properties;

/**
 * @author PSI
 *
 */
public interface Info {
	public abstract boolean loadInfo(Properties prop);
	public abstract boolean saveInfo(Properties prop);
}
