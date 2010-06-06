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
public abstract class AbstractCommentInfo extends AbstractConvertableInfo {

	//一画面に見えるコメント数は？
	private int VisibleAmount;
	private final static int DefVisibleAmount = 30;
	private final static String PropVisibleAmount = "VisibleAmount";

	/**
	 * @param autoRenaming
	 * @param autoRenamingFolder
	 * @param file
	 * @param enableDownloading
	 * @param delAfterConv
	 * @param enableConverting
	 * @param visibleAmount
	 */
	public AbstractCommentInfo(boolean autoRenaming, File autoRenamingFolder, File file, boolean enableDownloading, boolean delAfterConv, boolean enableConverting, int visibleAmount) {
		super(autoRenaming, autoRenamingFolder, file, enableDownloading, delAfterConv, enableConverting);
		VisibleAmount = visibleAmount;
	}

	/**
	 * 
	 */
	public AbstractCommentInfo() {
	}

	@Override
	public boolean loadInfo(Properties prop) {
		super.loadInfo(prop);
		String str;
		//一画面に見えるコメント数は？
		str = prop.getProperty(getPropID()+PropVisibleAmount);
		if(str != null){
			try {
				VisibleAmount = Integer.parseInt(str);
			} catch (NumberFormatException e) {
				VisibleAmount = getDefVisibleAmount();
			}
		}else{
			VisibleAmount = getDefVisibleAmount();
		}
		return true;
	}

	@Override
	public boolean saveInfo(Properties prop) {
		super.saveInfo(prop);
		//一画面に見えるコメント数は？
		prop.setProperty(getPropID()+PropVisibleAmount, Integer.toString(VisibleAmount));
		return true;
	}
	/*
	 * 以下Getter/Setter
	 */

	public int getVisibleAmount() {
		return VisibleAmount;
	}

	public void setVisibleAmount(int visibleAmount) {
		VisibleAmount = visibleAmount;
	}

	public static int getDefVisibleAmount() {
		return DefVisibleAmount;
	}


}
