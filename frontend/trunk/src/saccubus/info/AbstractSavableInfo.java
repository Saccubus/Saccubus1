package saccubus.info;

import java.io.File;
import java.util.Properties;

import saccubus.util.FileUtil;

public abstract class AbstractSavableInfo implements Info {
	//名前は自動で決定して、専用のフォルダに貯める？
	private boolean AutoRenaming;
	private final boolean DefAutoRenaming = true;
	private final static String PropAutoRenaming = "AutoRenaming";
	//名前を自動で決定する場合のフォルダは？
	private File AutoRenamingFolder;
	private final File DefAutoRenamingFolder = new File("."+java.io.File.separator+"[out]"+getPropID());
	private final static String PropAutoRenamingFolder = "AutoRenamingFolder";
	//名前を自動で決定しない場合のファイル（拡張子無し）は？
	private File File;
	private final File DefFile = new File("."+java.io.File.separator+"[out]"+getPropID());
	private final static String PropFile = "File";

	/**
	 * @param autoRenaming
	 * @param autoRenamingFolder
	 * @param file
	 */
	public AbstractSavableInfo(boolean autoRenaming, File autoRenamingFolder, File file) {
		super();
		AutoRenaming = autoRenaming;
		AutoRenamingFolder = autoRenamingFolder;
		File = file;
	}

	public AbstractSavableInfo() {
	}

	public boolean loadInfo(Properties prop) {
		String str;
		//名前は自動で決定して、専用のフォルダに貯める？
		str = prop.getProperty(getPropID()+PropAutoRenaming);
		if(str != null){
			AutoRenaming = Boolean.parseBoolean(str);
		}else{
			AutoRenaming = isDefAutoRenaming();
		}
		//名前を自動で決定する場合のフォルダは？
		str = prop.getProperty(getPropID()+PropAutoRenamingFolder);
		if(str != null){
			AutoRenamingFolder = new File(str);
		}else{
			AutoRenamingFolder = getDefAutoRenamingFolder();
		}
		//名前を自動で決定しない場合のファイル（拡張子無し）は？
		str = prop.getProperty(getPropID()+PropFile);
		if(str != null){
			File = new File(str);
		}else{
			File = getDefFile();
		}
		return false;
	}

	public boolean saveInfo(Properties prop) {
		//名前は自動で決定して、専用のフォルダに貯める？
		prop.setProperty(getPropID()+PropAutoRenaming, Boolean.toString(AutoRenaming));
		//名前を自動で決定する場合のフォルダは？
		prop.setProperty(getPropID()+PropAutoRenamingFolder, AutoRenamingFolder.getPath());
		//名前を自動で決定しない場合のファイル（拡張子無し）は？
		prop.setProperty(getPropID()+PropFile,FileUtil.getFilePathWithoutExt(File));
		return false;
	}
	/*
	 * 設定用IDを返す。
	 */
	public abstract String getPropID();

	/**
	 * @return defAutoRenaming
	 */
	public boolean isDefAutoRenaming() {
		return DefAutoRenaming;
	}

	/**
	 * @return defAutoRenamingFolder
	 */
	public File getDefAutoRenamingFolder() {
		return DefAutoRenamingFolder;
	}

	/**
	 * @return defFile
	 */
	public File getDefFile() {
		return DefFile;
	}

	/*
	 * 以下Getter/Setter
	 */
	public boolean isAutoRenaming() {
		return AutoRenaming;
	}

	public void setAutoRenaming(boolean autoRenaming) {
		AutoRenaming = autoRenaming;
	}

	public File getAutoRenamingFolder() {
		return AutoRenamingFolder;
	}

	public void setAutoRenamingFolder(File autoRenamingFolder) {
		AutoRenamingFolder = autoRenamingFolder;
	}
	public File getFile() {
		return File;
	}

	public void setFile(File file) {
		File = file;
	}

}
