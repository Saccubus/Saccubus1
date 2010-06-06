/**
 * 
 */
package saccubus.info;

import java.io.File;

/**
 * @author Account01
 *
 */
public class OwnerCommentInfo extends AbstractCommentInfo {

	/**
	 * 
	 */
	public OwnerCommentInfo() {
	}


	/**
	 * @param autoRenaming
	 * @param autoRenamingFolder
	 * @param file
	 * @param enableDownloading
	 * @param delAfterConv
	 * @param enableConverting
	 * @param visibleAmount
	 */
	public OwnerCommentInfo(boolean autoRenaming, File autoRenamingFolder, File file, boolean enableDownloading, boolean delAfterConv, boolean enableConverting, int visibleAmount) {
		super(autoRenaming, autoRenamingFolder, file, enableDownloading, delAfterConv,
				enableConverting, visibleAmount);
	}


	/* (non-Javadoc)
	 * @see saccubus.info.AbstractSavableInfo#getPropID()
	 */
	@Override
	public String getPropID() {
		return "owner_comment";
	}

}
