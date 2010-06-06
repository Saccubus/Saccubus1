/**
 * �ݒ�t�@�C����Java���爵���₷������I�u�W�F�N�g
 */
package saccubus.process.conv.conf.model;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

/**
 * @author PSI
 *
 */
public class Config {
	private final String Description;
	private final String ConfigName;
	private final ConfigItem Items[];
	/**
	 * �ݒ�𐶐�����B
	 * @param author
	 * @param configName
	 * @param items
	 */
	protected Config(final String desc, final String configName, final ConfigItem[] items) {
		Description = desc;
		ConfigName = configName;
		Items = items;
	}
	/**
	 * ���Җ����擾����B
	 * @return description
	 */
	public String getDescription() {
		return Description;
	}
	/**
	 * �ݒ薼���擾����i�t�@�C�����j
	 * @return configName
	 */
	public String getConfigName() {
		return ConfigName;
	}
	/**
	 * �ݒ�̈ꗗ���擾
	 * @return items
	 */
	public ConfigItem[] getItems() {
		return Items;
	}
	/**
	 * XML�t�@�C������͂��Đݒ���擾����B
	 * @param conf_file
	 * @return
	 */
	public static Config loadConfig(File conf_file){
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			ConfSaxParser handler = new ConfSaxParser();
			parser.parse(conf_file, handler);
			return handler.getConfig();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
