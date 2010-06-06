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

	//映像エンジンのパスは？
	private File MovieEnginePath;
	private final File DefMovieEnginePath = new File("./bin/");
	private final static String PropMovieEnginePath = "MovieEnginePath";
	//拡張vhookライブラリのパスは？
	private File VhookPath;
	private final File DefVhookPath = new File("./bin/nicovideo");
	private final static String PropVhookPath = "VhookPath";

	//オプションファイルのパスは？
	private File OptionFilePath;
	private final File DefOptionFilePath = new File("./option/デフォルト設定.xml");
	private final static String PropOptionFilePath = "OptionFilePath";
	//オプションのタイトルは？
	private String OptionTitle;
	private final String DefOptionTitle = "[ 4:3]デフォルト設定";
	private final static String PropOptionTitle = "OptionTitle";
	
	/* (non-Javadoc)
	 * @see saccubus.info.Info#loadInfo(java.util.Properties)
	 */
	public boolean loadInfo(Properties prop) {
		String str;
		//映像エンジンのパスは？
		str = prop.getProperty(PropMovieEnginePath);
		if(str != null){
			MovieEnginePath = new File(str);
		}else{
			MovieEnginePath = getDefMovieEnginePath();
		}
		//拡張vhookライブラリのパスは？
		str = prop.getProperty(PropVhookPath);
		if(str != null){
			VhookPath = new File(str);
		}else{
			VhookPath = getDefVhookPath();
		}
		//オプションファイルのパスは？
		str = prop.getProperty(PropOptionFilePath);
		if(str != null){
			OptionFilePath = new File(str);
		}else{
			OptionFilePath = getDefOptionFilePath();
		}
		//オプションのタイトルは？
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
		//映像エンジンのパスは？
		prop.setProperty(PropMovieEnginePath, MovieEnginePath.getPath());
		//拡張vhookライブラリのパスは？
		prop.setProperty(PropVhookPath, VhookPath.getPath());
		//オプションファイルのパスは？
		prop.setProperty(PropOptionFilePath, OptionFilePath.getPath());
		//オプションのタイトルは？
		prop.setProperty(PropOptionTitle, OptionTitle);
		return true;
	}
	/*
	 * 以下Getter/Setter
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
