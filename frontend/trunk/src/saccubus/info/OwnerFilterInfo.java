/**
 * フィルタ機能用
 */
package saccubus.info;

import java.io.File;

/**
 * @author PSI
 *
 */
public class OwnerFilterInfo extends AbstractConvertableInfo {


	/**
	 * 
	 */
	public OwnerFilterInfo() {
	}

	/**
	 * @param autoRenaming
	 * @param autoRenamingFolder
	 * @param file
	 * @param enableDownloading
	 * @param delAfterConv
	 * @param enableConverting
	 */
	public OwnerFilterInfo(boolean autoRenaming, File autoRenamingFolder, File file, boolean enableDownloading, boolean delAfterConv, boolean enableConverting) {
		super(autoRenaming, autoRenamingFolder, file, enableDownloading, delAfterConv,
				enableConverting);
	}

	/* (non-Javadoc)
	 * @see saccubus.info.AbstractDownloadableInfo#getPropID()
	 */
	@Override
	public String getPropID() {
		return "owner_filter";
	}

}
