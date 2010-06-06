/**
 * 新たに作り出されるもの専用。例えば変換済み動画とか。
 */
package saccubus.info;

import java.io.File;
import java.util.Properties;

/**
 * @author PSI
 *
 */
public abstract class AbstractCreatableInfo extends AbstractSavableInfo {
	//生成される？
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
		//生成される？
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
		//生成される？
		prop.setProperty(getPropID()+PropEnableCreating, Boolean.toString(EnableCreating));
		return true;
	}
	/*
	 * 以下Getter/Setter
	 */

	/**
	 * @return enableCreating
	 */
	public boolean isEnableCreating() {
		return EnableCreating;
	}

	/**
	 * @param enableCreating 設定する enableCreating
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
