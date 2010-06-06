/**
 * �ݒ�t�@�C����ǂݍ���ŃS���S������B
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
	 * �A�C�e��
	 */
	private final ConfigElement[] Elements;
	/**
	 * �A�C�e�����̃C���f�b�N�X
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
	 * ���Ɏ��s���ׂ��R�}���h��Ԃ�
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
	 * ���Z�b�g�B�ēx�C���^�[�v���^�[�����s�������ꍇ
	 */
	public void reset(){
		ItemIndex = 0;
	}
	/**
	 * <option>�Ŏw�肳���I�v�V���������s����B
	 * @return
	 */
	private boolean execOption(String name,String value){
		return true;
	}
	/**
	 * �R�}���h�����ɂ��������ă��v���[�X����B
	 * @param command
	 * @return
	 */
	private String replaceCommand(String command){
		return command;
	}
}
