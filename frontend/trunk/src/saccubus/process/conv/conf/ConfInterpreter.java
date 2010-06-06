/**
 * 設定ファイルを読み込んでゴリゴリする。
 */
package saccubus.process.conv.conf;

import saccubus.info.MovieEngineInfo;
import saccubus.process.conv.conf.model.Config;
import saccubus.process.conv.conf.model.ConfigElement;
import saccubus.process.conv.conf.model.ConfigItem;

/**
 * @author PSI
 *
 */
public class ConfInterpreter {
	/**
	 * アイテム
	 */
	private final ConfigElement[] Elements;
	/**
	 * アイテム内のインデックス
	 */
	private int ItemIndex;
	
	protected ConfInterpreter(MovieEngineInfo info){
		Config conf = Config.loadConfig(info.getOptionFilePath());
		ConfigItem item = null;
		if(conf != null){
			ConfigItem[] items = conf.getItems();
			for(ConfigItem it :items){
				if(it.getName().equals(info.getOptionTitle())){
					item = it;
					break;
				}
			}
		}
		Elements = item.getElements();
		ItemIndex = 0;
	}
	/**
	 * 次に実行すべきコマンドを返す
	 * @return
	 */
	public String nextCommand(){
		String str = null;
		for(int i=ItemIndex;i<Elements.length;i++){
			ConfigElement element = Elements[i];
			if(element.getElementType() == ConfigElement.Type.command){
				str = replaceCommand(element.getCommandLine());
				if(str != null){
					break;
				}
			}else if(element.getElementType() == ConfigElement.Type.option){
				execOption(element.getOptionName(),element.getOptionValue());
			}
		}
		return str;
	}
	/**
	 * リセット。再度インタープリターを実行したい場合
	 */
	public void reset(){
		ItemIndex = 0;
	}
	/**
	 * <option>で指定されるオプションを実行する。
	 * @return
	 */
	private boolean execOption(String name,String value){
		return true;
	}
	/**
	 * コマンドを環境にしたがってリプレースする。
	 * @param command
	 * @return
	 */
	private String replaceCommand(String command){
		return command;
	}
}
