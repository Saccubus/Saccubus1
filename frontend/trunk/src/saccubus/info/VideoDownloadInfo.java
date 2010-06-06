/**
 * ビデオダウンロードは基本的にこれだけ。
 * 低画質キャンセルとかを入れるならまた話は別だけども。
 */
package saccubus.info;

import java.io.File;

/**
 * @author PSI
 *
 */
public class VideoDownloadInfo extends AbstractDownloadableInfo {

	/**
	 * 
	 */
	public VideoDownloadInfo() {
		super();
	}


	/**
	 * @param autoRenaming
	 * @param autoRenamingFolder
	 * @param file
	 * @param enableDownloading
	 * @param delAfterConv
	 */
	public VideoDownloadInfo(boolean autoRenaming, File autoRenamingFolder, File file, boolean enableDownloading, boolean delAfterConv) {
		super(autoRenaming, autoRenamingFolder, file, enableDownloading, delAfterConv);
	}


	/* (non-Javadoc)
	 * @see saccubus.info.AbstractDownloadableInfo#getPropID()
	 */
	@Override
	public String getPropID() {
		return "video";
	}
}
