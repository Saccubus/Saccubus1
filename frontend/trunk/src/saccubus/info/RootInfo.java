/**
 * �ݒ�̑��{�R�B
 * ���ׂĂ�Info�I�u�W�F�N�g�������ŕێ�����B
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
	 * �萔��`
	 */
	private static final File DefInfoFile = new File("info.xml");
	
	/**
	 * �����o�[��`
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
	 * ���\�b�h��`
	 */
	/* (non-Javadoc)
	 * @see saccubus.info.Info#loadInfo(java.util.Properties)
	 */
	public boolean loadInfo(Properties prop) {
		return loadInfo(prop,null);
	}
	public boolean loadInfo(Properties prop,Properties override) {
		//�I�[�o�[���C�h����B
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
	 * �f�t�H���g�̒l��ς���
	 * @return
	 */
	private static RootInfo _default = null;
	public static RootInfo getDefault(){
		//���ɂ���Ȃ炻���Ԃ�
		if(_default != null){
			return _default;
		}
		_default = new RootInfo();
		//�ݒ��ǂݍ��ނ��߂̃v���p�e�B���쐬
		Properties prop = new Properties();
		try {
			//���ɃG���[���N���Ă��t�H���[���Ȃ��B
			//������΃f�t�H���g��ݒ肷��͂�������B
			prop.loadFromXML(new FileInputStream(DefInfoFile));
		} catch (InvalidPropertiesFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//��������ɓǂݍ���
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
		//�I�[�o�[���C�h����B
		if(override != null){
			prop.putAll(override);
		}
		return true;
	}
	/**
	 * �f�t�H���g��ۑ�����B
	 * @param info
	 */
	public static void saveDefault(RootInfo info){
		_default = info;
		//�Z�b�g�����v���p�e�B�̍쐬
		Properties prop = new Properties();
		//�ݒ���Z�b�g����B
		_default.saveInfo(prop);
		//�ۑ�����B
		try {
			prop.storeToXML(new FileOutputStream(DefInfoFile), "Saccubus Setting","UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * �f�t�H���g��ۑ�����B
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
	 * @param convertedVideoInfo �ݒ肷�� convertedVideoInfo
	 */
	public void setConvertedVideoInfo(ConvertedVideoInfo convertedVideoInfo) {
		ConvertedVideoInfo = convertedVideoInfo;
	}
	/**
	 * @param movieEngineInfo �ݒ肷�� movieEngineInfo
	 */
	public void setMovieEngineInfo(MovieEngineInfo movieEngineInfo) {
		MovieEngineInfo = movieEngineInfo;
	}
	/**
	 * @param networkInfo �ݒ肷�� networkInfo
	 */
	public void setNetworkInfo(NetworkInfo networkInfo) {
		NetworkInfo = networkInfo;
	}
	/**
	 * @param info �ݒ肷�� nGInfo
	 */
	public void setNGInfo(NGInfo info) {
		NGInfo = info;
	}
	/**
	 * @param nicoInfo �ݒ肷�� nicoInfo
	 */
	public void setNicoInfo(NicoInfo nicoInfo) {
		NicoInfo = nicoInfo;
	}
	/**
	 * @param ownerCommentInfo �ݒ肷�� ownerCommentInfo
	 */
	public void setOwnerCommentInfo(OwnerCommentInfo ownerCommentInfo) {
		OwnerCommentInfo = ownerCommentInfo;
	}
	/**
	 * @param ownerFilterDownloadingInfo �ݒ肷�� ownerFilterDownloadingInfo
	 */
	public void setOwnerFilterDownloadingInfo(
			OwnerFilterInfo ownerFilterDownloadingInfo) {
		OwnerFilterDownloadingInfo = ownerFilterDownloadingInfo;
	}
	/**
	 * @param userCommentInfo �ݒ肷�� userCommentInfo
	 */
	public void setUserCommentInfo(UserCommentInfo userCommentInfo) {
		UserCommentInfo = userCommentInfo;
	}
	/**
	 * @param vhookInfo �ݒ肷�� vhookInfo
	 */
	public void setVhookInfo(VhookInfo vhookInfo) {
		VhookInfo = vhookInfo;
	}
	/**
	 * @param videoDownloadInfo �ݒ肷�� videoDownloadInfo
	 */
	public void setVideoDownloadInfo(VideoDownloadInfo videoDownloadInfo) {
		VideoDownloadInfo = videoDownloadInfo;
	}
}
