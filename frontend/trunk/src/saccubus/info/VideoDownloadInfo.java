/**
 * �r�f�I�_�E�����[�h�͊�{�I�ɂ��ꂾ���B
 * ��掿�L�����Z���Ƃ�������Ȃ�܂��b�͕ʂ����ǂ��B
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
