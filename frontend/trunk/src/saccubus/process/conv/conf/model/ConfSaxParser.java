/**
 * 
 */
package saccubus.process.conv.conf.model;

import java.util.LinkedList;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import saccubus.process.conv.conf.model.ConfigElement.Type;

/**
 * @author PSI
 *
 */
public class ConfSaxParser extends DefaultHandler {
	/**
	 * 最終的に出力されるConf
	 */
	private Config Conf;
	/**
	 * Configに関わる情報
	 */
	private boolean ConfigStarted = false;
	private String ConfigName;
	private boolean ConfigNameStarted = false;
	private String ConfigDesc;
	private boolean ConfigDescStarted = false;
	/**
	 * ConfigItemに関わる情報
	 */
	private final LinkedList<ConfigItem> ItemList = new LinkedList<ConfigItem>();
	private boolean ItemStarted = false;
	private String ItemName;
	private boolean ItemNameStarted = false;
	private String ItemDescription;
	private boolean ItemDescStarted = false;
	/**
	 * ConfigElementに関わる情報
	 */
	private final LinkedList<ConfigElement> ElementList = new LinkedList<ConfigElement>();
	private Type ElementType;
	private String OptionName;
	private String OptionValue;
	private String CommandLine;
	/**
	 * 設定を読み込み後に取得する。
	 * @return
	 */
	public Config getConfig(){
		return Conf;
	}
	/**
	 * コンフィグの初期化
	 */
	private void startConf(){
		ConfigStarted = true;
		ConfigNameStarted = false;
		ConfigDescStarted = false;
		ConfigName = "";
		ConfigDesc = "";
		ItemList.clear();
	}
	/**
	 * コンフィグの終了
	 */
	private void endConf(){
		ConfigStarted = false;
		Conf = new Config(ConfigDesc,ConfigName,ItemList.toArray(_TmpItemArray));
	}
	/**
	 * アイテムの初期化
	 */
	private void startItem(){
		ItemStarted = true;
		ItemName = "";
		ItemNameStarted = false;
		ItemDescription = "";
		ItemDescStarted = false;
		ElementType = null;
		ElementList.clear();
	}
	/**
	 * アイテムをリストへ追加
	 */
	private void endItem(){
		ItemStarted = false;
		ItemList.add(new ConfigItem(ItemName,ItemDescription,ElementList.toArray(_TmpElementArray)));
	}
	/**
	 * エレメントを開始
	 * @param type
	 * @param name
	 */
	private void startElement(Type type,String name){
		ElementType = type;
		OptionName = null;
		OptionValue = null;
		CommandLine = null;
		if(type == Type.option){
			OptionName = name;
		}
	}
	/**
	 * エレメントをリストに追加
	 */
	private void endElement(){
		ElementList.add(new ConfigElement(ElementType,OptionName,OptionValue,CommandLine));
		ElementType = null;
	}
	/**
	 * エレメント開始
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		if(ConfigStarted){
			if(ItemStarted){//アイテムも始まった
				if(!ItemNameStarted && qName.equals(XML_Name)){
					ItemNameStarted = true;
				}else if(!ItemDescStarted && qName.equals(XML_Desc)){
					ItemDescStarted = true;
				}else if(ElementType==null && qName.equals(XML_Option)){
					startElement(Type.option,attributes.getValue(XML_OptionName));
				}else if(ElementType==null && qName.equals(XML_Command)){
					startElement(Type.command,null);
				}
			}else if(qName.equals("item")){
				startItem();
			}else{//アイテムはまだ始まっていない
				if(!ConfigNameStarted && qName.equals(XML_Name)){
					ConfigNameStarted = true;
				}else if(!ConfigDescStarted && qName.equals(XML_Desc)){
					ConfigDescStarted = true;
				}
			}
		}else{
			if(qName.equals("conf")){
				startConf();
			}
		}
	}
	@Override
	public void characters(char[] ch, int offset, int length) {
		if(ConfigStarted){
			if(ItemStarted){//アイテムも始まった
				if(ElementType == Type.option){
					OptionValue = new String(ch,offset,length);
				}else if(ElementType == Type.command){
					CommandLine = new String(ch,offset,length);
				}else if(ItemNameStarted){
					ItemName = new String(ch,offset,length);
				}else if(ItemDescStarted){
					ItemDescription = new String(ch,offset,length);
				}
			}else{//まだアイテムは始まっていない
				if(ConfigNameStarted){
					ConfigName = new String(ch,offset,length);
				}else if(ConfigDescStarted){
					ConfigDesc = new String(ch,offset,length);
				}
			}
		}
	}
	@Override
	public void endElement(String uri, String localName, String qName) {
		if(ConfigStarted){
			if(ItemStarted){//アイテムも始まった
				if(qName.equals("item")){
					endItem();
				}else{
					if(ItemNameStarted && qName.equals(XML_Name)){
						ItemNameStarted = false;
					}else if(ItemDescStarted && qName.equals(XML_Desc)){
						ItemDescStarted = false;
					}else if(ElementType!=null && (qName.equals(XML_Option) || qName.equals(XML_Command))){
						endElement();
					}
				}
			}else{//アイテムはまだ始まっていない
				if(ConfigNameStarted && qName.equals(XML_Name)){
					ConfigNameStarted = false;
				}else if(ConfigDescStarted && qName.equals(XML_Desc)){
					ConfigDescStarted = false;
				}
			}
		}else{
			if(qName.equals("conf")){
				endConf();
			}
		}
	}
	/**
	 * テンポラリ
	 */
	private final ConfigItem[] _TmpItemArray = new ConfigItem[0];
	private final ConfigElement[] _TmpElementArray = new ConfigElement[0];
	/**
	 * XML要素名
	 */
	private static final String XML_Name = "name";
	private static final String XML_Desc = "desc";
	private static final String XML_Option = "option";
	private static final String XML_OptionName = "name";
	private static final String XML_Command = "command";
}
