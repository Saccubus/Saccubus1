/**
 * �_�E�����[�h�ł��āA�ϊ��ɂ��g������̗p�̃A�u�X�g���N�g�E�N���X
 * ��F�R�����g�Ȃ�
 */
package saccubus.info;

import java.io.File;
import java.util.Properties;

/**
 * @author PSI
 *
 */
public abstract class AbstractConvertableInfo extends AbstractDownloadableInfo {
	//�ϊ��Ɏg���H
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
		//�ϊ��Ɏg���H
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
		//�ϊ��Ɏg���H
		prop.setProperty(getPropID()+PropEnableConverting, Boolean.toString(EnableConverting));
		return true;
	}

	/*
	 * �ȉ�Getter/Setter
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
