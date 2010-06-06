/**
 * 設定の総本山。
 * すべてのInfoオブジェクトをここで保持する。
 */
package saccubus.info;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * @author PSI
 *
 */
public class RootInfo implements Info {
	/**
	 * 定数定義
	 */
	private static final File DefInfoFile = new File("info.xml");
	
	/**
	 * メンバー定義
	 */
	//general
	private NetworkInfo NetworkInfo = new NetworkInfo();
	private NicoInfo NicoInfo = new NicoInfo();
	//downloading
	private VideoDownloadInfo VideoDownloadInfo = new VideoDownloadInfo();
	private OwnerFilterInfo OwnerFilterDownloadingInfo = new OwnerFilterInfo();
	private OwnerCommentInfo OwnerCommentInfo = new OwnerCommentInfo();
	private UserCommentInfo UserCommentInfo = new UserCommentInfo();
	//movie engine
	private MovieEngineInfo MovieEngineInfo = new MovieEngineInfo();
	//pre-converting
	private NGInfo NGInfo = new NGInfo();
	//converting
	private ConvertedVideoInfo ConvertedVideoInfo = new ConvertedVideoInfo();
	private VhookInfo VhookInfo = new VhookInfo();
	
	/**
	 * メソッド定義
	 */
	/* (non-Javadoc)
	 * @see saccubus.info.Info#loadInfo(java.util.Properties)
	 */
	public boolean loadInfo(Properties prop) {
		return loadInfo(prop,null);
	}
	public boolean loadInfo(Properties prop,Properties override) {
		//オーバーライドする。
		if(override != null){
			prop.putAll(override);
		}
		//general
		NetworkInfo.loadInfo(prop);
		NicoInfo.loadInfo(prop);
		//downloading
		VideoDownloadInfo.loadInfo(prop);
		OwnerFilterDownloadingInfo.loadInfo(prop);
		OwnerCommentInfo.loadInfo(prop);
		UserCommentInfo.loadInfo(prop);
		//movie engine
		MovieEngineInfo.loadInfo(prop);
		//pre-converting
		NGInfo.loadInfo(prop);
		//converting
		ConvertedVideoInfo.loadInfo(prop);
		VhookInfo.loadInfo(prop);
		return true;
	}
	/**
	 * デフォルトの値を変えす
	 * @return
	 */
	private static RootInfo _default = null;
	public static RootInfo getDefault(){
		//既にあるならそれを返す
		if(_default != null){
			return _default;
		}
		_default = new RootInfo();
		//設定を読み込むためのプロパティを作成
		Properties prop = new Properties();
		try {
			//特にエラーが起きてもフォローしない。
			//無ければデフォルトを設定するはずだから。
			prop.loadFromXML(new FileInputStream(DefInfoFile));
		} catch (InvalidPropertiesFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//それを元に読み込む
		_default.loadInfo(prop);
		return _default;
	}
	/* (non-Javadoc)
	 * @see saccubus.info.Info#saveInfo(java.util.Properties)
	 */
	public boolean saveInfo(Properties prop) {
		return saveInfo(prop,null);
	}
	public boolean saveInfo(Properties prop,Properties override) {
		//general
		NetworkInfo.saveInfo(prop);
		NicoInfo.saveInfo(prop);
		//downloading
		VideoDownloadInfo.saveInfo(prop);
		OwnerFilterDownloadingInfo.saveInfo(prop);
		OwnerCommentInfo.saveInfo(prop);
		UserCommentInfo.saveInfo(prop);
		//movie engine
		MovieEngineInfo.saveInfo(prop);
		//pre-converting
		NGInfo.saveInfo(prop);
		//converting
		ConvertedVideoInfo.saveInfo(prop);
		VhookInfo.saveInfo(prop);
		//オーバーライドする。
		if(override != null){
			prop.putAll(override);
		}
		return true;
	}
	/**
	 * デフォルトを保存する。
	 * @param info
	 */
	public static void saveDefault(RootInfo info){
		_default = info;
		//セットされるプロパティの作成
		Properties prop = new Properties();
		//設定をセットする。
		_default.saveInfo(prop);
		//保存する。
		try {
			prop.storeToXML(new FileOutputStream(DefInfoFile), "Saccubus Setting","UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * デフォルトを保存する。
	 *
	 */
	public static void saveDefault(){
		saveDefault(_default);
	}
	/**
	 * @return convertedVideoInfo
	 */
	public ConvertedVideoInfo getConvertedVideoInfo() {
		return ConvertedVideoInfo;
	}
	/**
	 * @return movieEngineInfo
	 */
	public MovieEngineInfo getMovieEngineInfo() {
		return MovieEngineInfo;
	}
	/**
	 * @return networkInfo
	 */
	public NetworkInfo getNetworkInfo() {
		return NetworkInfo;
	}
	/**
	 * @return nGInfo
	 */
	public NGInfo getNGInfo() {
		return NGInfo;
	}
	/**
	 * @return nicoInfo
	 */
	public NicoInfo getNicoInfo() {
		return NicoInfo;
	}
	/**
	 * @return ownerCommentInfo
	 */
	public OwnerCommentInfo getOwnerCommentInfo() {
		return OwnerCommentInfo;
	}
	/**
	 * @return ownerFilterDownloadingInfo
	 */
	public OwnerFilterInfo getOwnerFilterDownloadingInfo() {
		return OwnerFilterDownloadingInfo;
	}
	/**
	 * @return userCommentInfo
	 */
	public UserCommentInfo getUserCommentInfo() {
		return UserCommentInfo;
	}
	/**
	 * @return vhookInfo
	 */
	public VhookInfo getVhookInfo() {
		return VhookInfo;
	}
	/**
	 * @return videoDownloadInfo
	 */
	public VideoDownloadInfo getVideoDownloadInfo() {
		return VideoDownloadInfo;
	}
	/**
	 * @param convertedVideoInfo 設定する convertedVideoInfo
	 */
	public void setConvertedVideoInfo(ConvertedVideoInfo convertedVideoInfo) {
		ConvertedVideoInfo = convertedVideoInfo;
	}
	/**
	 * @param movieEngineInfo 設定する movieEngineInfo
	 */
	public void setMovieEngineInfo(MovieEngineInfo movieEngineInfo) {
		MovieEngineInfo = movieEngineInfo;
	}
	/**
	 * @param networkInfo 設定する networkInfo
	 */
	public void setNetworkInfo(NetworkInfo networkInfo) {
		NetworkInfo = networkInfo;
	}
	/**
	 * @param info 設定する nGInfo
	 */
	public void setNGInfo(NGInfo info) {
		NGInfo = info;
	}
	/**
	 * @param nicoInfo 設定する nicoInfo
	 */
	public void setNicoInfo(NicoInfo nicoInfo) {
		NicoInfo = nicoInfo;
	}
	/**
	 * @param ownerCommentInfo 設定する ownerCommentInfo
	 */
	public void setOwnerCommentInfo(OwnerCommentInfo ownerCommentInfo) {
		OwnerCommentInfo = ownerCommentInfo;
	}
	/**
	 * @param ownerFilterDownloadingInfo 設定する ownerFilterDownloadingInfo
	 */
	public void setOwnerFilterDownloadingInfo(
			OwnerFilterInfo ownerFilterDownloadingInfo) {
		OwnerFilterDownloadingInfo = ownerFilterDownloadingInfo;
	}
	/**
	 * @param userCommentInfo 設定する userCommentInfo
	 */
	public void setUserCommentInfo(UserCommentInfo userCommentInfo) {
		UserCommentInfo = userCommentInfo;
	}
	/**
	 * @param vhookInfo 設定する vhookInfo
	 */
	public void setVhookInfo(VhookInfo vhookInfo) {
		VhookInfo = vhookInfo;
	}
	/**
	 * @param videoDownloadInfo 設定する videoDownloadInfo
	 */
	public void setVideoDownloadInfo(VideoDownloadInfo videoDownloadInfo) {
		VideoDownloadInfo = videoDownloadInfo;
	}
}
