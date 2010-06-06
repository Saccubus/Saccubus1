/**
 * 変換動画専用
 */
package saccubus.info;

import java.util.Properties;

/**
 * @author PSI
 *
 */
public class ConvertedVideoInfo extends AbstractCreatableInfo {
	//動画IDを付加する？
	private boolean EnableAddingVideoID;
	private final static boolean DefEnableAddingVideoID = true;
	private final static String PropEnableAddingVideoID = "EnableAddingVideoID";

	/* (non-Javadoc)
	 * @see saccubus.info.AbstractCreatableInfo#loadInfo(java.util.Properties)
	 */
	@Override
	public boolean loadInfo(Properties prop) {
		super.loadInfo(prop);
		String str;
		//動画IDを付加する？
		str = prop.getProperty(getPropID()+PropEnableAddingVideoID);
		if(str != null){
			EnableAddingVideoID = Boolean.parseBoolean(str);
		}else{
			EnableAddingVideoID = isDefEnableAddingVideoID();
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see saccubus.info.AbstractCreatableInfo#saveInfo(java.util.Properties)
	 */
	@Override
	public boolean saveInfo(Properties prop) {
		super.saveInfo(prop);
		//動画IDを付加する？
		prop.setProperty(getPropID()+PropEnableAddingVideoID, Boolean.toString(EnableAddingVideoID));
		return true;
	}

	/* (non-Javadoc)
	 * @see saccubus.info.AbstractSavableInfo#getPropID()
	 */
	@Override
	public String getPropID() {
		return "converted_video";
	}
	/*
	 * 以下Getter/Setter
	 */

	/**
	 * @return enableAddingVideoID
	 */
	public boolean isEnableAddingVideoID() {
		return EnableAddingVideoID;
	}

	/**
	 * @param enableAddingVideoID 設定する enableAddingVideoID
	 */
	public void setEnableAddingVideoID(boolean enableAddingVideoID) {
		EnableAddingVideoID = enableAddingVideoID;
	}

	/**
	 * @return defEnableAddingVideoID
	 */
	public static boolean isDefEnableAddingVideoID() {
		return DefEnableAddingVideoID;
	}

}
