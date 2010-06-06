package saccubus.info;

import java.io.File;
import java.util.Properties;

import saccubus.util.FileUtil;

public abstract class AbstractSavableInfo implements Info {
	//���O�͎����Ō��肵�āA��p�̃t�H���_�ɒ��߂�H
	private boolean AutoRenaming;
	private final boolean DefAutoRenaming = true;
	private final static String PropAutoRenaming = "AutoRenaming";
	//���O�������Ō��肷��ꍇ�̃t�H���_�́H
	private File AutoRenamingFolder;
	private final File DefAutoRenamingFolder = new File("."+java.io.File.separator+"[out]"+getPropID());
	private final static String PropAutoRenamingFolder = "AutoRenamingFolder";
	//���O�������Ō��肵�Ȃ��ꍇ�̃t�@�C���i�g���q�����j�́H
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
		//���O�͎����Ō��肵�āA��p�̃t�H���_�ɒ��߂�H
		str = prop.getProperty(getPropID()+PropAutoRenaming);
		if(str != null){
			AutoRenaming = Boolean.parseBoolean(str);
		}else{
			AutoRenaming = isDefAutoRenaming();
		}
		//���O�������Ō��肷��ꍇ�̃t�H���_�́H
		str = prop.getProperty(getPropID()+PropAutoRenamingFolder);
		if(str != null){
			AutoRenamingFolder = new File(str);
		}else{
			AutoRenamingFolder = getDefAutoRenamingFolder();
		}
		//���O�������Ō��肵�Ȃ��ꍇ�̃t�@�C���i�g���q�����j�́H
		str = prop.getProperty(getPropID()+PropFile);
		if(str != null){
			File = new File(str);
		}else{
			File = getDefFile();
		}
		return false;
	}

	public boolean saveInfo(Properties prop) {
		//���O�͎����Ō��肵�āA��p�̃t�H���_�ɒ��߂�H
		prop.setProperty(getPropID()+PropAutoRenaming, Boolean.toString(AutoRenaming));
		//���O�������Ō��肷��ꍇ�̃t�H���_�́H
		prop.setProperty(getPropID()+PropAutoRenamingFolder, AutoRenamingFolder.getPath());
		//���O�������Ō��肵�Ȃ��ꍇ�̃t�@�C���i�g���q�����j�́H
		prop.setProperty(getPropID()+PropFile,FileUtil.getFilePathWithoutExt(File));
		return false;
	}
	/*
	 * �ݒ�pID��Ԃ��B
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
	 * �ȉ�Getter/Setter
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
