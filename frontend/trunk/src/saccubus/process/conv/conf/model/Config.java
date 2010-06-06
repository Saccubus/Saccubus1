/**
 * 設定ファイルをJavaから扱いやすくするオブジェクト
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
	 * 設定を生成する。
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
	 * 著者名を取得する。
	 * @return description
	 */
	public String getDescription() {
		return Description;
	}
	/**
	 * 設定名を取得する（ファイル名）
	 * @return configName
	 */
	public String getConfigName() {
		return ConfigName;
	}
	/**
	 * 設定の一覧を取得
	 * @return items
	 */
	public ConfigItem[] getItems() {
		return Items;
	}
	/**
	 * XMLファイルを解析して設定を取得する。
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
