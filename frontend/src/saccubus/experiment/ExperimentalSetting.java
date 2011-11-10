///**
// *
// */
package saccubus.experiment;
//
///**
// * <p>
// * さきゅばす<br/>
// * コメントアート用の調整のための設定
// * </p>
// * @author orz
// * @version 1.26β
// */
public class ExperimentalSetting {
//
//	boolean disableOriginalResize;
//	boolean disableLimitWidthResize;
//	boolean disableLinefeedResize;
//	boolean disableDoubleResize;
//	boolean disableFontFDoublescale;
//	boolean fontHeightFix;
//	String fontHeightFixRatio;
//	String limitWidth;
//	boolean enableLimitHeight;
//	String limitHeight;
//	boolean enableFixedFontSizeUse;
//	String fixedFontSize;	// original is 24 39 15 pt
//	String ngFontCode;
//	String ngFonts;
//	boolean enableLimitWidthUse;
//	boolean enableDoubleLimitWidthUse;
//	String doubleLimitWidth;
//
//	/**
//	 * コンストラクタ
//	 */
//	public ExperimentalSetting(
//		boolean disable_original_resize,
//		boolean disable_limitwidth_resize,
//		boolean disable_linefeed_resize,
//		boolean disable_double_resize,
//		boolean disable_font_doublescale,
//		boolean font_height_fix,
//		String font_height_fix_raito,
//		String nico_limit_width,
//		String nico_limit_height,
//		boolean enable_fixed_font_size_use,
//		String fixed_font_size,
//		boolean enable_limit_height,
//		String ng_font_code,
//		boolean enable_limit_width,
//		boolean enable_double_limit,
//		String double_limit_width
//	  )
//	{
//		disableOriginalResize = disable_original_resize;
//		disableLimitWidthResize = disable_limitwidth_resize;
//		disableLinefeedResize = disable_linefeed_resize;
//		disableDoubleResize = disable_double_resize;
//		disableFontFDoublescale = disable_font_doublescale;
//		fontHeightFix = font_height_fix;
//		fontHeightFixRatio = font_height_fix_raito;
//		limitWidth = nico_limit_width;
//		limitHeight = nico_limit_height;
//		enableFixedFontSizeUse = enable_fixed_font_size_use;
//		fixedFontSize = fixed_font_size;
//		enableLimitHeight = enable_limit_height;
//		ngFontCode = ng_font_code;
//		ngFonts = Ucode.decodeList(ng_font_code);
//		enableLimitWidthUse = enable_limit_width;
//		enableDoubleLimitWidthUse = enable_double_limit;
//		doubleLimitWidth = double_limit_width;
//	}
//
//	public boolean isFontHeightFix() {
//		return fontHeightFix;
//	}
//	public String getFontHeightFixRaito() {
//		return fontHeightFixRatio;
//	}
//	public boolean isDisableOriginalResize(){
//		return disableOriginalResize;
//	}
//	public boolean isDisableLimitWidthResize(){
//		return disableLimitWidthResize;
//	}
//	public boolean isDisableLinefeedResize(){
//		return disableLinefeedResize;
//	}
//	public boolean isDisableDoubleResize(){
//		return disableDoubleResize;
//	}
//	public boolean isDisableFontDoublescale(){
//		return disableFontFDoublescale;
//	}
//	public String getLimitWidth(){
//		return limitWidth;
//	}
//	public boolean isEnableLimitHeight(){
//		return enableLimitHeight;
//	}
//	public String getLimitHeight(){
//		return limitHeight;
//	}
//	public String getFixedFontSize(){
//		return fixedFontSize;
//	}
//	public boolean isEnableFixedFontSizeUse(){
//		return enableFixedFontSizeUse;
//	}
//	public String getNGFontCode(){
//		return ngFontCode;
//	}
//	public String getNGFonts(){
//		return ngFonts;
//	}
//	public boolean isEnableDoubleLimitWidth(){
//		return enableDoubleLimitWidthUse;
//	}
//	public boolean isEnableLimitWidth(){
//		return enableLimitWidthUse;
//	}
//	public String getDoubleLimitWidth(){
//		return doubleLimitWidth;
//	}
//
//	private static String convBooleanToString(boolean var){
//		return var ? "1" : "0";
//	}
//	public String makeString() {
//		String ret =
//		  convBooleanToString(disableOriginalResize)
//		+ convBooleanToString(disableLimitWidthResize)
//		+ convBooleanToString(disableLinefeedResize)
//		+ convBooleanToString(disableDoubleResize)
//		+ convBooleanToString(disableFontFDoublescale)
//		+ convBooleanToString(fontHeightFix)
//		+ convBooleanToString(enableFixedFontSizeUse)
//		+ convBooleanToString(enableLimitHeight)
//		+ convBooleanToString(enableLimitWidthUse)
//		+ convBooleanToString(enableDoubleLimitWidthUse)
//		+ ":" + fontHeightFixRatio
//		+ ":" + limitWidth
//		+ ":" + limitHeight
//		+ ":" + fixedFontSize
//		+ ":" + ngFontCode
//		+ ":" + doubleLimitWidth;
//		return ret;
//	}
//
//	static final int NB_BOOLEAN_ITEM = 10;
//	public static ExperimentalSetting getSetting(String string){
//		String[] list = string.split(":");
//		String boolstr = getElement(list,0);
//		boolean[] boola = new boolean[NB_BOOLEAN_ITEM];
//		for(int i=0;i<NB_BOOLEAN_ITEM;i++){
//			if(boolstr == null || boolstr.length() <= i){
//				boola[i] = false;
//			} else {
//				boola[i] = boolstr.charAt(i) == '1';
//			}
//		}
//		return new ExperimentalSetting(	// Default
//			boola[0]	//disableOriginalResize	false
//			,boola[1]	//disableLimitWidthResize	false
//			,boola[2]	//disableLinefeedResize	false
//			,boola[3]	//disableDoubleResize	false
//			,boola[4]	//disableFontDoublescale	false
//			,boola[5]	//fontHeightFix	false
//			,getElement(list,1)	//fontHeightFix
//			,getElement(list,2)	//limitWidth
//			,getElement(list,3)	//limitHeight
//			,boola[6]	//enableFixedFontSizeUse	false
//			,getElement(list,4)	//fixedFontSize
//			,boola[7]	//enableLimitHeight	false
//			,getElement(list,5)	// NGFontCode
//			,boola[8]
//			,boola[9]
//			,getElement(list,6)
//		);
//	}
////	public static ExperimentalSetting getSetting(){
////		return ExperimentalSetting.getSetting("10000000:116 94 6 6:512 672:384 384:24 39 15:U#02CB");
////	}
//
//	// default = ""
//	private static String getElement(String[] str, int index){
//		if(str == null || str.length <= index){
//			return "";
//		}
//		return str[index];
//	}
//
}
