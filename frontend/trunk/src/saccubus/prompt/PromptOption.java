/**
 * オプション関係の処理を行う。
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
	 * オーバライドするプロパティ
	 */
	private Properties OverrideProp = new Properties();
	/**
	 * すべてダウンロードする？
	 */
	private boolean EnableAllDownload = false;
	/**
	 * すべてダウンロードしない
	 */
	private boolean DisableAllDownload = false;
	/**
	 * オプションを処理
	 */
	public PromptOption(String args[]) {
		for(int i=0;i<args.length;i++){
			if(args[i].startsWith("--")){//開始が二本ダッシュの時はオプション
				if(execCmd(args[i].substring(2),args[i+1])){
					i++;
				}
			}else if(args[i].startsWith("-")){//それ以外の場合はプロパティ定義
				OverrideProp.setProperty(args[i].substring(1),args[i+1]);
				i++;
			}
		}
	}
	/**
	 * コマンドを実行する。次の文字もコマンドの一部ならば、trueを返す。
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
	 * 設定情報を修正する。
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
