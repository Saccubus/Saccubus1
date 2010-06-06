/**
 * 
 */
package saccubus.info;

import java.io.File;
import java.util.Properties;

/**
 * @author PSI
 *
 */
public class NGInfo implements Info {
	NGItemInfo NGWordInfo = new NGItemInfo("word");
	NGItemInfo NGCommandInfo = new NGItemInfo("command");
	NGItemInfo NGUserInfo = new NGItemInfo("user");

	/* (non-Javadoc)
	 * @see saccubus.info.Info#loadInfo(java.util.Properties)
	 */
	public boolean loadInfo(Properties prop) {
		NGWordInfo.loadInfo(prop);
		NGCommandInfo.loadInfo(prop);
		NGUserInfo.loadInfo(prop);
		return true;
	}

	/* (non-Javadoc)
	 * @see saccubus.info.Info#saveInfo(java.util.Properties)
	 */
	public boolean saveInfo(Properties prop) {
		NGWordInfo.saveInfo(prop);
		NGCommandInfo.saveInfo(prop);
		NGUserInfo.saveInfo(prop);
		return true;
	}

	/*
	 * Getter/Setter
	 */
	public NGItemInfo getNGCommandInfo() {
		return NGCommandInfo;
	}

	public NGItemInfo getNGUserInfo() {
		return NGUserInfo;
	}

	public NGItemInfo getNGWordInfo() {
		return NGWordInfo;
	}

}

class NGItemInfo implements Info{
	private String ItemID;
	//有効？
	private boolean NGEnabled;
	private static final boolean DefNGEnabled = false;
	private String PropNGEnabled;
	//ファイル名は？
	private File NGFile;
	private File DefNGFile;
	private String PropNGFile;


	/**
	 * @param itemID
	 * @param enabled
	 * @param file
	 */
	public NGItemInfo(String itemID, boolean enabled, File file) {
		super();
		ItemID = itemID;
		NGEnabled = enabled;
		NGFile = file;
		detectItemID();
	}

	/**
	 * @param itemID
	 */
	public NGItemInfo(String itemID) {
		ItemID = itemID;
		detectItemID();
	}

	/**
	 * ItemIDから色々決定する。
	 */
	private void detectItemID(){
		//プロパティ名を決定。
		PropNGEnabled = ItemID+"NG";
		PropNGFile = ItemID+"NGFile";
		//デフォルト設定を決定
		DefNGFile = new File("."+java.io.File.separator+"[NG]"+ItemID+".txt");
	}

	/* (non-Javadoc)
	 * @see saccubus.info.Info#loadInfo(java.util.Properties)
	 */
	public boolean loadInfo(Properties prop) {
		String str;
		//有効？
		str = prop.getProperty(PropNGEnabled);
		if(str != null){
			NGEnabled = Boolean.parseBoolean(str);
		}else{
			NGEnabled = DefNGEnabled;
		}
		//ファイル名は？
		str = prop.getProperty(PropNGFile);
		if(str != null){
			NGFile = new File(str);
		}else{
			NGFile = DefNGFile;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see saccubus.info.Info#saveInfo(java.util.Properties)
	 */
	public boolean saveInfo(Properties prop) {
		//有効？
		prop.setProperty(PropNGEnabled, Boolean.toString(NGEnabled));
		//ファイル名は？
		prop.setProperty(PropNGFile, NGFile.getPath());
		return false;
	}

	/*
	 * Getter/Setter
	 */
	public boolean isNGEnabled() {
		return NGEnabled;
	}

	public void setNGEnabled(boolean enabled) {
		NGEnabled = enabled;
	}

	public File getNGFile() {
		return NGFile;
	}

	public void setNGFile(File file) {
		NGFile = file;
	}
}