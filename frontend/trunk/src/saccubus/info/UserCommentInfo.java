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
	//ダウンロードするコメント数は自動的に決定するの？
	private boolean EnableAutoAmountDetection;
	private final static boolean DefEnableAutoAmountDetection = true;
	private final static String PropEnableAutoAmountDetection = "EnableAutoAmountDetection";
	//ダウンロードするコメント数は？
	private int DownloadingAmount;
	private final static int DefDownloadingAmount = 500;
	private final static String PropDownloadingAmount = "DownloadingAmount";
	//変換するコメント数を制限する？
	private boolean EnableLimitConvertingAmount;
	private final static boolean DefEnableLimitConvertingAmount = false;
	private final static String PropEnableLimitConvertingAmount = "EnableLimitConvertingAmount";
	//変換するコメント数は？
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
		//ダウンロードするコメント数は自動的に決定するの？
		str = prop.getProperty(getPropID()+PropEnableAutoAmountDetection);
		if(str != null){
			EnableAutoAmountDetection = Boolean.parseBoolean(str);
		}else{
			EnableAutoAmountDetection = isDefEnableAutoAmountDetection();
		}
		//ダウンロードするコメント数は？
		str = prop.getProperty(getPropID()+PropDownloadingAmount);
		if(str != null){
			DownloadingAmount = Integer.parseInt(str);
		}else{
			DownloadingAmount = getDefDownloadingAmount();
		}
		//変換するコメント数を制限する？
		str = prop.getProperty(getPropID()+PropEnableLimitConvertingAmount);
		if(str != null){
			EnableLimitConvertingAmount = Boolean.parseBoolean(str);
		}else{
			EnableLimitConvertingAmount = isDefEnableLimitConvertingAmount();
		}
		//変換するコメント数は？
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
		//ダウンロードするコメント数は自動的に決定するの？
		prop.setProperty(getPropID()+PropEnableAutoAmountDetection, Boolean.toString(EnableAutoAmountDetection));
		//ダウンロードするコメント数は？
		prop.setProperty(getPropID()+PropDownloadingAmount, Integer.toString(DownloadingAmount));
		//変換するコメント数を制限する？
		prop.setProperty(getPropID()+PropEnableLimitConvertingAmount, Boolean.toString(EnableLimitConvertingAmount));
		//変換するコメント数は？
		prop.setProperty(getPropID()+PropConvertingAmount, Integer.toString(ConvertingAmount));
		return true;
	}
	/*
	 * 以下Getter/Setter
	 */

	/**
	 * @return convertingAmount
	 */
	public int getConvertingAmount() {
		return ConvertingAmount;
	}

	/**
	 * @param convertingAmount 設定する convertingAmount
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
	 * @param downloadingAmount 設定する downloadingAmount
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
	 * @param enableAutoAmountDetection 設定する enableAutoAmountDetection
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
	 * @param enableLimitConvertingAmount 設定する enableLimitConvertingAmount
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
