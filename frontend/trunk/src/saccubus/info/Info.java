/**
 * �ݒ荀�ڂ͕K��������p���i�������j�����ĂȂ���΂Ȃ�Ȃ��B
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
