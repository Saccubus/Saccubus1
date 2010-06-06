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
public class MovieEngineInfo implements Info {

	//�f���G���W���̃p�X�́H
	private File MovieEnginePath;
	private final File DefMovieEnginePath = new File("./bin/");
	private final static String PropMovieEnginePath = "MovieEnginePath";
	//�g��vhook���C�u�����̃p�X�́H
	private File VhookPath;
	private final File DefVhookPath = new File("./bin/nicovideo");
	private final static String PropVhookPath = "VhookPath";

	//�I�v�V�����t�@�C���̃p�X�́H
	private File OptionFilePath;
	private final File DefOptionFilePath = new File("./option/�f�t�H���g�ݒ�.xml");
	private final static String PropOptionFilePath = "OptionFilePath";
	//�I�v�V�����̃^�C�g���́H
	private String OptionTitle;
	private final String DefOptionTitle = "[ 4:3]�f�t�H���g�ݒ�";
	private final static String PropOptionTitle = "OptionTitle";
	
	/* (non-Javadoc)
	 * @see saccubus.info.Info#loadInfo(java.util.Properties)
	 */
	public boolean loadInfo(Properties prop) {
		String str;
		//�f���G���W���̃p�X�́H
		str = prop.getProperty(PropMovieEnginePath);
		if(str != null){
			MovieEnginePath = new File(str);
		}else{
			MovieEnginePath = getDefMovieEnginePath();
		}
		//�g��vhook���C�u�����̃p�X�́H
		str = prop.getProperty(PropVhookPath);
		if(str != null){
			VhookPath = new File(str);
		}else{
			VhookPath = getDefVhookPath();
		}
		//�I�v�V�����t�@�C���̃p�X�́H
		str = prop.getProperty(PropOptionFilePath);
		if(str != null){
			OptionFilePath = new File(str);
		}else{
			OptionFilePath = getDefOptionFilePath();
		}
		//�I�v�V�����̃^�C�g���́H
		str = prop.getProperty(PropOptionTitle);
		if(str != null){
			OptionTitle = str;
		}else{
			OptionTitle = getDefOptionTitle();
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see saccubus.info.Info#saveInfo(java.util.Properties)
	 */
	public boolean saveInfo(Properties prop) {
		//�f���G���W���̃p�X�́H
		prop.setProperty(PropMovieEnginePath, MovieEnginePath.getPath());
		//�g��vhook���C�u�����̃p�X�́H
		prop.setProperty(PropVhookPath, VhookPath.getPath());
		//�I�v�V�����t�@�C���̃p�X�́H
		prop.setProperty(PropOptionFilePath, OptionFilePath.getPath());
		//�I�v�V�����̃^�C�g���́H
		prop.setProperty(PropOptionTitle, OptionTitle);
		return true;
	}
	/*
	 * �ȉ�Getter/Setter
	 */
	public File getMovieEnginePath() {
		return MovieEnginePath;
	}

	public void setMovieEnginePath(File movieEnginePath) {
		MovieEnginePath = movieEnginePath;
	}

	public File getVhookPath() {
		return VhookPath;
	}

	public void setVhookPath(File vhookPath) {
		VhookPath = vhookPath;
	}

	public File getDefMovieEnginePath() {
		return DefMovieEnginePath;
	}

	public File getDefVhookPath() {
		return DefVhookPath;
	}

	public File getDefOptionFilePath() {
		return DefOptionFilePath;
	}

	public String getDefOptionTitle() {
		return DefOptionTitle;
	}

	public File getOptionFilePath() {
		return OptionFilePath;
	}

	public String getOptionTitle() {
		return OptionTitle;
	}

	public void setOptionFilePath(File optionFilePath) {
		OptionFilePath = optionFilePath;
	}

	public void setOptionTitle(String optionTitle) {
		OptionTitle = optionTitle;
	}

}
