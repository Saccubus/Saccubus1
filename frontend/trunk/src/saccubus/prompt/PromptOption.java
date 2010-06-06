/**
 * �I�v�V�����֌W�̏������s���B
 */
package saccubus.prompt;

import java.util.Properties;

import saccubus.info.RootInfo;

/**
 * @author PSI
 *
 */
public class PromptOption {
	/**
	 * �I�[�o���C�h����v���p�e�B
	 */
	private Properties OverrideProp = new Properties();
	/**
	 * ���ׂă_�E�����[�h����H
	 */
	private boolean EnableAllDownload = false;
	/**
	 * ���ׂă_�E�����[�h���Ȃ�
	 */
	private boolean DisableAllDownload = false;
	/**
	 * �I�v�V����������
	 */
	public PromptOption(String args[]) {
		for(int i=0;i<args.length;i++){
			if(args[i].startsWith("--")){//�J�n����{�_�b�V���̎��̓I�v�V����
				if(execCmd(args[i].substring(2),args[i+1])){
					i++;
				}
			}else if(args[i].startsWith("-")){//����ȊO�̏ꍇ�̓v���p�e�B��`
				OverrideProp.setProperty(args[i].substring(1),args[i+1]);
				i++;
			}
		}
	}
	/**
	 * �R�}���h�����s����B���̕������R�}���h�̈ꕔ�Ȃ�΁Atrue��Ԃ��B
	 * @param str
	 * @param next
	 * @return
	 */
	private boolean execCmd(String str,String next){
		if(str.equals("enable-all-download")){
			EnableAllDownload = true;
			return false;
		}else if(str.equals("enable-all-download")){
			DisableAllDownload = true;
			return false;
		}else{
			return false;
		}
	}
	/**
	 * �ݒ�����C������B
	 * @param info
	 */
	public void fixInfo(RootInfo info){
		if(EnableAllDownload){
			info.getOwnerCommentInfo().setEnableDownloading(true);
			info.getUserCommentInfo().setEnableDownloading(true);
			info.getVideoDownloadInfo().setEnableDownloading(true);
			info.getOwnerFilterDownloadingInfo().setEnableDownloading(true);
		}else if(DisableAllDownload){
			info.getOwnerCommentInfo().setEnableDownloading(false);
			info.getUserCommentInfo().setEnableDownloading(false);
			info.getVideoDownloadInfo().setEnableDownloading(false);
			info.getOwnerFilterDownloadingInfo().setEnableDownloading(false);
		}
	}
	/**
	 * @return overrideProp
	 */
	public Properties getOverrideProp() {
		return OverrideProp;
	}

}
