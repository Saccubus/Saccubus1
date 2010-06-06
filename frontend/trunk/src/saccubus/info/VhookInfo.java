/**
 * �g��vhook���C�u�����p�̐ݒ�B
 */
package saccubus.info;

import java.io.File;
import java.util.Properties;

import saccubus.util.SystemUtil;

/**
 * @author PSI
 *
 */
public class VhookInfo implements Info {
	//�g��vhook���C�u�����͗L���H
	private boolean EnableVhook;
	private final static boolean DefEnableVhook = true;
	private final static String PropEnableVhook = "EnableVhook";
	//�t�H���g�p�X
	private File FontPath;
	private final static File DefFontPath = new File("."+File.separator+"font.ttf");
	private final static String PropFontPath = "FontPath";
	//�t�H���g�C���f�b�N�X
	private int FontIndex;
	private final static int DefFontIndex = 0;
	private final static String PropFontIndex = "FontIndex";
	//�e�̎��
	private int Shadow;
	private final static int DefShadow = 1;
	private final static String PropShadow = "Shadow";
	//�ϊ����̉摜��\������H
	private boolean EnableShowVideo;
	private final static boolean DefEnableShowVideo = true;
	private final static String PropEnableShowVideo = "EnableShowVideo";
	//�t�H���g�T�C�Y�̎���������L���ɂ���H
	private boolean EnableFixFontSize;
	private final static boolean DefEnableFixFontSize = true;
	private final static String PropEnableFixFontSize = "EnableFixFontSize";
	//���ׂẴR�����g��s�����ɂ���H
	private boolean EnableOpaqueComment;
	private final static boolean DefEnableOpaqueComment = true;
	private final static String PropEnableOpaqueComment = "EnableOpaqueComment";

	/**
	 * @param enableVhook
	 * @param fontPath
	 * @param fontIndex
	 * @param shadow
	 * @param enableShowVideo
	 * @param enableFixFontSize
	 * @param enableOpaqueComment
	 */
	public VhookInfo(boolean enableVhook, File fontPath, int fontIndex, int shadow, boolean enableShowVideo, boolean enableFixFontSize, boolean enableOpaqueComment) {
		super();
		EnableVhook = enableVhook;
		FontPath = fontPath;
		FontIndex = fontIndex;
		Shadow = shadow;
		EnableShowVideo = enableShowVideo;
		EnableFixFontSize = enableFixFontSize;
		EnableOpaqueComment = enableOpaqueComment;
	}

	/**
	 * 
	 */
	public VhookInfo() {
		super();
	}

	/* (non-Javadoc)
	 * @see saccubus.info.Info#loadInfo(java.util.Properties)
	 */
	public boolean loadInfo(Properties prop) {
		String str;
		//�g��vhook���C�u�����͗L���H
		str = prop.getProperty(PropEnableVhook);
		if(str != null){
			EnableVhook = Boolean.parseBoolean(str);
		}else{
			EnableVhook = isDefEnableVhook();
		}
		//�t�H���g�p�X
		str = prop.getProperty(PropFontPath);
		if(str != null){
			FontPath = new File(str);
		}else{
			FontPath = getDefFontPath();
		}
		//�t�H���g�C���f�b�N�X
		str = prop.getProperty(PropFontIndex);
		if(str != null){
			FontIndex = Integer.parseInt(str);
		}else{
			FontIndex = getDefFontIndex();
		}
		//�e�̎��
		str = prop.getProperty(PropShadow);
		if(str != null){
			Shadow = Integer.parseInt(str);
		}else{
			Shadow = getDefShadow();
		}
		//�ϊ����̉摜��\������H
		str = prop.getProperty(PropEnableShowVideo);
		if(str != null){
			EnableShowVideo = Boolean.parseBoolean(str);
		}else{
			EnableShowVideo = isDefEnableShowVideo();
		}
		//�t�H���g�T�C�Y�̎���������L���ɂ���H
		str = prop.getProperty(PropEnableFixFontSize);
		if(str != null){
			EnableFixFontSize = Boolean.parseBoolean(str);
		}else{
			EnableFixFontSize = isDefEnableFixFontSize();
		}
		//���ׂẴR�����g��s�����ɂ���H
		str = prop.getProperty(PropEnableOpaqueComment);
		if(str != null){
			EnableOpaqueComment = Boolean.parseBoolean(str);
		}else{
			EnableOpaqueComment = isDefEnableOpaqueComment();
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see saccubus.info.Info#saveInfo(java.util.Properties)
	 */
	public boolean saveInfo(Properties prop) {
		//�g��vhook���C�u�����͗L���H
		prop.setProperty(PropEnableVhook, Boolean.toString(EnableVhook));
		//�t�H���g�p�X
		prop.setProperty(PropFontPath, FontPath.getPath());
		//�t�H���g�C���f�b�N�X
		prop.setProperty(PropFontIndex, Integer.toString(FontIndex));
		//�e�̎��
		prop.setProperty(PropShadow, Integer.toString(Shadow));
		//�ϊ����̉摜��\������H
		prop.setProperty(PropEnableShowVideo, Boolean.toString(EnableShowVideo));
		//�t�H���g�T�C�Y�̎���������L���ɂ���H
		prop.setProperty(PropEnableFixFontSize, Boolean.toString(EnableFixFontSize));
		//���ׂẴR�����g��s�����ɂ���H
		prop.setProperty(PropEnableOpaqueComment, Boolean.toString(EnableOpaqueComment));
		return true;
	}
	/*
	 * �ȉ�Getter/Setter
	 */

	public static boolean isDefEnableFixFontSize() {
		return DefEnableFixFontSize;
	}

	public static boolean isDefEnableOpaqueComment() {
		return DefEnableOpaqueComment;
	}

	public static boolean isDefEnableShowVideo() {
		return DefEnableShowVideo;
	}

	public static boolean isDefEnableVhook() {
		return DefEnableVhook;
	}

	public static int getDefFontIndex() {
		if(SystemUtil.isSystemWindows){
			String win_dir = System.getenv("windir");
			if(win_dir != null && !win_dir.equals("")){
				return 1;
			}else{
				return DefFontIndex;
			}
		}
		return DefFontIndex;
	}

	public static File getDefFontPath() {
		if(SystemUtil.isSystemWindows){
			String win_dir = System.getenv("windir");
			if(win_dir != null && !win_dir.equals("")){
				if(!win_dir.endsWith("\\")){
					win_dir = win_dir+"\\";
				}
				return new File(win_dir+"Fonts\\msgothic.ttc");
			}else{
				return DefFontPath;
			}
		}else{
			return DefFontPath;
		}
	}

	public static int getDefShadow() {
		return DefShadow;
	}

	public boolean isEnableFixFontSize() {
		return EnableFixFontSize;
	}

	public boolean isEnableOpaqueComment() {
		return EnableOpaqueComment;
	}

	public boolean isEnableShowVideo() {
		return EnableShowVideo;
	}

	public boolean isEnableVhook() {
		return EnableVhook;
	}

	public int getFontIndex() {
		return FontIndex;
	}

	public File getFontPath() {
		return FontPath;
	}

	public int getShadow() {
		return Shadow;
	}

	public void setEnableFixFontSize(boolean enableFixFontSize) {
		EnableFixFontSize = enableFixFontSize;
	}

	public void setEnableOpaqueComment(boolean enableOpaqueComment) {
		EnableOpaqueComment = enableOpaqueComment;
	}

	public void setEnableShowVideo(boolean enableShowVideo) {
		EnableShowVideo = enableShowVideo;
	}

	public void setEnableVhook(boolean enableVhook) {
		EnableVhook = enableVhook;
	}

	public void setFontIndex(int fontIndex) {
		FontIndex = fontIndex;
	}

	public void setFontPath(File fontPath) {
		FontPath = fontPath;
	}

	public void setShadow(int shadow) {
		Shadow = shadow;
	}

}
