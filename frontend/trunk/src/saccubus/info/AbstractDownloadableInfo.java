/**
 * ダウンロードするもの（コメント、動画、などなど）用のクラスの親クラス。
 */
package saccubus.info;

import java.io.File;
import java.util.Properties;
/**
 * @author PSI
 *
 */
public abstract class AbstractDownloadableInfo extends AbstractSavableInfo {

	/**
	 * メンバー、定数定義
	 */

	//そもそも、ダウンロードするの？
	private boolean EnableDownloading;
	private final static boolean DefEnableDownloading = true;
	private final static String PropEnableDownloading = "EnableDownloading";
	//変換後削除する？
	private boolean DelAfterConv;
	private final boolean DefDelAfterConv = true;
	private final static String PropDelAfterConv = "DelAfterConv";


	/**
	 * @param autoRenaming
	 * @param autoRenamingFolder
	 * @param file
	 * @param enableDownloading
	 * @param delAfterConv
	 */
	public AbstractDownloadableInfo(boolean autoRenaming, File autoRenamingFolder, File file, boolean enableDownloading, boolean delAfterConv) {
		super(autoRenaming, autoRenamingFolder, file);
		EnableDownloading = enableDownloading;
		DelAfterConv = delAfterConv;
	}

	/**
	 * 
	 */
	public AbstractDownloadableInfo() {
	}

	/**
	 * @param autoRenaming
	 * @param autoRenamingFolder
	 * @param file
	 */
	public AbstractDownloadableInfo(boolean autoRenaming, File autoRenamingFolder, File file) {
		super(autoRenaming, autoRenamingFolder, file);
	}

	/**
	 * コードの開始
	 */

	/* (non-Javadoc)
	 * @see saccubus.info.Info#loadInfo(com.sun.xml.internal.fastinfoset.sax.Properties)
	 */
	public boolean loadInfo(Properties prop) {
		super.loadInfo(prop);
		String str;
		//そもそもダウンロードするの？
		str = prop.getProperty(getPropID()+PropEnableDownloading);
		if(str != null){
			EnableDownloading = Boolean.parseBoolean(str);
		}else{
			EnableDownloading = isDefEnableDownloading();
		}
		//変換後、削除する？
		str = prop.getProperty(getPropID()+PropDelAfterConv);
		if(str != null){
			DelAfterConv = Boolean.parseBoolean(str);
		}else{
			DelAfterConv = isDefDelAfterConv();
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see saccubus.info.Info#saveInfo(com.sun.xml.internal.fastinfoset.sax.Properties)
	 */
	public boolean saveInfo(Properties prop) {
		super.saveInfo(prop);
		//そもそもダウンロードするの？
		prop.setProperty(getPropID()+PropEnableDownloading, Boolean.toString(EnableDownloading));
		//変換後、削除する？
		prop.setProperty(getPropID()+PropDelAfterConv, Boolean.toString(DelAfterConv));
		return true;
	}
	
	/*
	 * 以下Getter/Setter
	 */
	
	public boolean isDelAfterConv() {
		return DelAfterConv;
	}

	public void setDelAfterConv(boolean delAfterConv) {
		DelAfterConv = delAfterConv;
	}

	public boolean isEnableDownloading() {
		return EnableDownloading;
	}

	public void setEnableDownloading(boolean enableDownloading) {
		EnableDownloading = enableDownloading;
	}

	/**
	 * @return defEnableDownloading
	 */
	public static boolean isDefEnableDownloading() {
		return DefEnableDownloading;
	}

	/**
	 * @return defDelAfterConv
	 */
	public boolean isDefDelAfterConv() {
		return DefDelAfterConv;
	}

}
