/**
 * 
 */
package saccubus.experiment;

/**
 * <p>
 * さきゅばす<br/>
 * コメントアート用の調整のための設定
 * </p>
 * @author orz
 * @version 1.26β
 */
public class ExperimentalSetting {

	boolean disableOriginalResize;
	boolean disableLimitWidthResize;
	boolean disableLinefeedResize;
	boolean disableDoubleResize;
	boolean disableFontFDoublescale;
	boolean fontHeightFix;
	String fontHeightFixRatio;
	String limitWidth;
	String limitHeight;
	boolean enableFixedFontSizeUse;
	String fixedFontSize;	// original is 24 39 15 pt

	/**
	 * コンストラクタ
	 */
	public ExperimentalSetting(
		boolean disable_original_resize,
		boolean disable_limitwidth_resize,
		boolean disable_linefeed_resize,
		boolean disable_double_resize,
		boolean disable_font_doublescale,
		boolean font_height_fix,
		String font_height_fix_raito,
		String nico_limit_width,
		String nico_limit_height,
		boolean enable_fixed_font_size_use,
		String fixed_font_size
	  )
	{
		disableOriginalResize = disable_original_resize;
		disableLimitWidthResize = disable_limitwidth_resize;
		disableLinefeedResize = disable_linefeed_resize;
		disableDoubleResize = disable_double_resize;
		disableFontFDoublescale = disable_font_doublescale;
		fontHeightFix = font_height_fix;
		fontHeightFixRatio = font_height_fix_raito;
		limitWidth = nico_limit_width;
		limitHeight = nico_limit_height;
		enableFixedFontSizeUse = enable_fixed_font_size_use;
		fixedFontSize = fixed_font_size;
	}

	public boolean isFontHeightFix() {
		return fontHeightFix;
	}
	public String getFontHeightFixRaito() {
		return fontHeightFixRatio;
	}
	public boolean isDisableOriginalResize(){
		return disableOriginalResize;
	}
	public boolean isDisableLimitWidthResize(){
		return disableLimitWidthResize;
	}
	public boolean isDisableLinefeedResize(){
		return disableLinefeedResize;
	}
	public boolean isDisableDoubleResize(){
		return disableDoubleResize;
	}
	public boolean isDisableFontDoublescale(){
		return disableFontFDoublescale;
	}
	public String getLimitWidth(){
		return limitWidth;
	}
	public String getLimitHeight(){
		return limitHeight;
	}
	public String getFixedFontSize(){
		return fixedFontSize;
	}
	public boolean isEnableFixedFontSizeUse(){
		return enableFixedFontSizeUse;
	}

	private String convBooleanToString(boolean var){
		return var ? "1" : "0";
	}
	public String makeString() {
		String ret =
		  convBooleanToString(disableOriginalResize)
		+ convBooleanToString(disableLimitWidthResize)
		+ convBooleanToString(disableLinefeedResize)
		+ convBooleanToString(disableDoubleResize)
		+ convBooleanToString(disableFontFDoublescale)
		+ convBooleanToString(fontHeightFix)
		+ convBooleanToString(enableFixedFontSizeUse)
		+ ":" + fontHeightFixRatio
		+ ":" + limitWidth
		+ ":" + limitHeight
		+ ":" + fixedFontSize;
		return ret;
	}

	static final int NB_BOOLEAN_ITEM = 7;
	public static ExperimentalSetting getSetting(String string){
		String[] list = string.split(":");
		char[] boolc = getElement(list,0,"0").toCharArray();
		boolean[] boola = new boolean[NB_BOOLEAN_ITEM];
		for(int i=0;i<boola.length;i++){
			char c = getElement(boolc, i, '0');
			boola[i] = c == '1';
		}
		return new ExperimentalSetting(				// Default
			boola[0],	//disableOriginalResize		// false
			boola[1],	//disableLimitWidthResize	// false
			boola[2],	//disableLinefeedResize		// false
			boola[3],	//disableDoubleResize		// false
			boola[4],	//disableFontDoublescale	// false
			boola[5],	//fontHeightFix				// false
			getElement(list,1,"116 94 5"),	//fontHeightFix
			getElement(list,2,"524 1048"),	//limitWidth
			getElement(list,3,"384 384"),	//limitHeight
			boola[6],	//enableFixedFontSizeUse	// false
			getElement(list,4,"24 39 15")	//fixedFontSize
		);
	}
	public static ExperimentalSetting getSetting(){
		return ExperimentalSetting.getSetting("1000000:116 94 6:524 1048:384 384:24 39 15");
	}

	private static String getElement(String[] prop, int index, String def){
		if(prop == null){
			return def;
		}
		if (prop.length <= index ){
			return def;
		}
		return prop[index];
	}

	private static char getElement(char[] prop, int index, char def){
		if(prop == null){
			return def;
		}
		if (prop.length <= index ){
			return def;
		}
		return prop[index];
	}

}
