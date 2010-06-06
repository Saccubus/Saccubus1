/**
 * ダウンロードできて、変換にも使えるもの用のアブストラクト・クラス
 * 例：コメントなど
 */
package saccubus.info;

import java.io.File;
import java.util.Properties;

/**
 * @author PSI
 *
 */
public abstract class AbstractConvertableInfo extends AbstractDownloadableInfo {
	//変換に使う？
	private boolean EnableConverting;
	private final static boolean DefEnableConverting = true;
	private final static String PropEnableConverting = "EnableConverting";


	/**
	 * @param autoRenaming
	 * @param autoRenamingFolder
	 * @param file
	 * @param enableDownloading
	 * @param delAfterConv
	 * @param enableConverting
	 */
	public AbstractConvertableInfo(boolean autoRenaming, File autoRenamingFolder, File file, boolean enableDownloading, boolean delAfterConv, boolean enableConverting) {
		super(autoRenaming, autoRenamingFolder, file, enableDownloading, delAfterConv);
		EnableConverting = enableConverting;
	}

	/**
	 * 
	 */
	public AbstractConvertableInfo() {
	}

	@Override
	public boolean loadInfo(Properties prop) {
		super.loadInfo(prop);
		String str;
		//変換に使う？
		str = prop.getProperty(getPropID()+PropEnableConverting);
		if(str != null){
			EnableConverting = Boolean.parseBoolean(str);
		}else{
			EnableConverting = isDefEnableConverting();
		}
		return true;
	}

	@Override
	public boolean saveInfo(Properties prop) {
		super.saveInfo(prop);
		//変換に使う？
		prop.setProperty(getPropID()+PropEnableConverting, Boolean.toString(EnableConverting));
		return true;
	}

	/*
	 * 以下Getter/Setter
	 */

	public boolean isEnableConverting() {
		return EnableConverting;
	}

	public void setEnableConverting(boolean enableConverting) {
		EnableConverting = enableConverting;
	}

	/**
	 * @return defEnableConverting
	 */
	public static boolean isDefEnableConverting() {
		return DefEnableConverting;
	}


}
