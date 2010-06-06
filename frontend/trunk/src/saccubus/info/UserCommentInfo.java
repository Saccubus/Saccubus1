/**
 * 
 */
package saccubus.info;

import java.io.File;
import java.util.Properties;

/**
 * @author Account01
 *
 */
public class UserCommentInfo extends AbstractCommentInfo {
	//�_�E�����[�h����R�����g���͎����I�Ɍ��肷��́H
	private boolean EnableAutoAmountDetection;
	private final static boolean DefEnableAutoAmountDetection = true;
	private final static String PropEnableAutoAmountDetection = "EnableAutoAmountDetection";
	//�_�E�����[�h����R�����g���́H
	private int DownloadingAmount;
	private final static int DefDownloadingAmount = 500;
	private final static String PropDownloadingAmount = "DownloadingAmount";
	//�ϊ�����R�����g���𐧌�����H
	private boolean EnableLimitConvertingAmount;
	private final static boolean DefEnableLimitConvertingAmount = false;
	private final static String PropEnableLimitConvertingAmount = "EnableLimitConvertingAmount";
	//�ϊ�����R�����g���́H
	private int ConvertingAmount;
	private final static int DefConvertingAmount = 500;
	private final static String PropConvertingAmount = "ConvertingAmount";

	/**
	 * @param autoRenaming
	 * @param autoRenamingFolder
	 * @param file
	 * @param enableDownloading
	 * @param delAfterConv
	 * @param enableConverting
	 * @param visibleAmount
	 * @param enableAutoAmountDetection
	 * @param downloadingAmount
	 * @param enableLimitConvertingAmount
	 * @param convertingAmount
	 */
	public UserCommentInfo(boolean autoRenaming, File autoRenamingFolder, File file, boolean enableDownloading, boolean delAfterConv, boolean enableConverting, int visibleAmount, boolean enableAutoAmountDetection, int downloadingAmount, boolean enableLimitConvertingAmount, int convertingAmount) {
		super(autoRenaming, autoRenamingFolder, file, enableDownloading, delAfterConv, enableConverting, visibleAmount);
		EnableAutoAmountDetection = enableAutoAmountDetection;
		DownloadingAmount = downloadingAmount;
		EnableLimitConvertingAmount = enableLimitConvertingAmount;
		ConvertingAmount = convertingAmount;
	}

	/**
	 * 
	 */
	public UserCommentInfo() {
		super();
	}

	/* (non-Javadoc)
	 * @see saccubus.info.AbstractSavableInfo#getPropID()
	 */
	@Override
	public String getPropID() {
		return "user_comment";
	}

	/* (non-Javadoc)
	 * @see saccubus.info.AbstractConvertableInfo#loadInfo(java.util.Properties)
	 */
	@Override
	public boolean loadInfo(Properties prop) {
		super.loadInfo(prop);
		String str;
		//�_�E�����[�h����R�����g���͎����I�Ɍ��肷��́H
		str = prop.getProperty(getPropID()+PropEnableAutoAmountDetection);
		if(str != null){
			EnableAutoAmountDetection = Boolean.parseBoolean(str);
		}else{
			EnableAutoAmountDetection = isDefEnableAutoAmountDetection();
		}
		//�_�E�����[�h����R�����g���́H
		str = prop.getProperty(getPropID()+PropDownloadingAmount);
		if(str != null){
			DownloadingAmount = Integer.parseInt(str);
		}else{
			DownloadingAmount = getDefDownloadingAmount();
		}
		//�ϊ�����R�����g���𐧌�����H
		str = prop.getProperty(getPropID()+PropEnableLimitConvertingAmount);
		if(str != null){
			EnableLimitConvertingAmount = Boolean.parseBoolean(str);
		}else{
			EnableLimitConvertingAmount = isDefEnableLimitConvertingAmount();
		}
		//�ϊ�����R�����g���́H
		str = prop.getProperty(getPropID()+PropConvertingAmount);
		if(str != null){
			ConvertingAmount = Integer.parseInt(str);
		}else{
			ConvertingAmount = getDefConvertingAmount();
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see saccubus.info.AbstractConvertableInfo#saveInfo(java.util.Properties)
	 */
	@Override
	public boolean saveInfo(Properties prop) {
		super.saveInfo(prop);
		//�_�E�����[�h����R�����g���͎����I�Ɍ��肷��́H
		prop.setProperty(getPropID()+PropEnableAutoAmountDetection, Boolean.toString(EnableAutoAmountDetection));
		//�_�E�����[�h����R�����g���́H
		prop.setProperty(getPropID()+PropDownloadingAmount, Integer.toString(DownloadingAmount));
		//�ϊ�����R�����g���𐧌�����H
		prop.setProperty(getPropID()+PropEnableLimitConvertingAmount, Boolean.toString(EnableLimitConvertingAmount));
		//�ϊ�����R�����g���́H
		prop.setProperty(getPropID()+PropConvertingAmount, Integer.toString(ConvertingAmount));
		return true;
	}
	/*
	 * �ȉ�Getter/Setter
	 */

	/**
	 * @return convertingAmount
	 */
	public int getConvertingAmount() {
		return ConvertingAmount;
	}

	/**
	 * @param convertingAmount �ݒ肷�� convertingAmount
	 */
	public void setConvertingAmount(int convertingAmount) {
		ConvertingAmount = convertingAmount;
	}

	/**
	 * @return downloadingAmount
	 */
	public int getDownloadingAmount() {
		return DownloadingAmount;
	}

	/**
	 * @param downloadingAmount �ݒ肷�� downloadingAmount
	 */
	public void setDownloadingAmount(int downloadingAmount) {
		DownloadingAmount = downloadingAmount;
	}

	/**
	 * @return enableAutoAmountDetection
	 */
	public boolean isEnableAutoAmountDetection() {
		return EnableAutoAmountDetection;
	}

	/**
	 * @param enableAutoAmountDetection �ݒ肷�� enableAutoAmountDetection
	 */
	public void setEnableAutoAmountDetection(boolean enableAutoAmountDetection) {
		EnableAutoAmountDetection = enableAutoAmountDetection;
	}

	/**
	 * @return enableLimitConvertingAmount
	 */
	public boolean isEnableLimitConvertingAmount() {
		return EnableLimitConvertingAmount;
	}

	/**
	 * @param enableLimitConvertingAmount �ݒ肷�� enableLimitConvertingAmount
	 */
	public void setEnableLimitConvertingAmount(boolean enableLimitConvertingAmount) {
		EnableLimitConvertingAmount = enableLimitConvertingAmount;
	}

	/**
	 * @return defConvertingAmount
	 */
	public static int getDefConvertingAmount() {
		return DefConvertingAmount;
	}

	/**
	 * @return defDownloadingAmount
	 */
	public static int getDefDownloadingAmount() {
		return DefDownloadingAmount;
	}

	/**
	 * @return defEnableAutoAmountDetection
	 */
	public static boolean isDefEnableAutoAmountDetection() {
		return DefEnableAutoAmountDetection;
	}

	/**
	 * @return defEnableLimitConvertingAmount
	 */
	public static boolean isDefEnableLimitConvertingAmount() {
		return DefEnableLimitConvertingAmount;
	}
	


}
