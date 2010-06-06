/**
 * 
 */
package saccubus.process.conv.conf.model;

/**
 * @author PSI
 *
 */
public class ConfigItem {
	private final String Name;
	private final String Description;
	private final ConfigElement Elements[];
	/**
	 * 設定アイテムを生成する。
	 * @param name
	 * @param description
	 * @param elements
	 */
	protected ConfigItem(final String name, final String description, final ConfigElement[] elements) {
		Name = name;
		Description = description;
		Elements = elements;
	}
	/**
	 * @return description
	 */
	public String getDescription() {
		return Description;
	}
	/**
	 * @return elements
	 */
	public ConfigElement[] getElements() {
		return Elements;
	}
	/**
	 * @return name
	 */
	public String getName() {
		return Name;
	}
}
