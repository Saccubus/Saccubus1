/**
 * �V���ɍ��o�������̐�p�B�Ⴆ�Εϊ��ςݓ���Ƃ��B
 */
package saccubus.info;

import java.io.File;
import java.util.Properties;

/**
 * @author PSI
 *
 */
public abstract class AbstractCreatableInfo extends AbstractSavableInfo {
	//���������H
	private boolean EnableCreating;
	private final static boolean DefEnableCreating = true;
	private final static String PropEnableCreating = "EnableCreating";

	/**
	 * @param autoRenaming
	 * @param autoRenamingFolder
	 * @param file
	 * @param enableCreating
	 */
	public AbstractCreatableInfo(boolean autoRenaming, File autoRenamingFolder, File file, boolean enableCreating) {
		super(autoRenaming, autoRenamingFolder, file);
		EnableCreating = enableCreating;
	}

	/**
	 * 
	 */
	public AbstractCreatableInfo() {
	}

	@Override
	public boolean loadInfo(Properties prop) {
		super.loadInfo(prop);
		String str;
		//���������H
		str = prop.getProperty(getPropID()+PropEnableCreating);
		if(str != null){
			EnableCreating = Boolean.parseBoolean(str);
		}else{
			EnableCreating = isDefEnableCreating();
		}
		return true;
	}

	@Override
	public boolean saveInfo(Properties prop) {
		super.saveInfo(prop);
		//���������H
		prop.setProperty(getPropID()+PropEnableCreating, Boolean.toString(EnableCreating));
		return true;
	}
	/*
	 * �ȉ�Getter/Setter
	 */

	/**
	 * @return enableCreating
	 */
	public boolean isEnableCreating() {
		return EnableCreating;
	}

	/**
	 * @param enableCreating �ݒ肷�� enableCreating
	 */
	public void setEnableCreating(boolean enableCreating) {
		EnableCreating = enableCreating;
	}

	/**
	 * @return defEnableCreating
	 */
	public static boolean isDefEnableCreating() {
		return DefEnableCreating;
	}


}
