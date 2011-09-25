/**
 * 以下を参考にしました
 * JavaでUnicodeエスケープ
 * http://blog.junion.org/2010/11/03/id_74
 */
package saccubus.experiment;

import java.awt.Font;

/**
 * <p>
 * 以下を参考にしました
 * JavaでUnicodeエスケープ
 * http://blog.junion.org/2010/11/03/id_74
 * </p>
 * @author orz
 */
public class Ucode {

	public static String encode(String str) {
		if(str == null || str.isEmpty()){
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for(char c : str.toCharArray()){
			sb.append(String.format("U#%04X ", (int)c));
		}
		return sb.toString().trim();
	}

	public static Character decode(String ucode){
		if(ucode == null || ucode.isEmpty()){
			return null;
		}
		if(!ucode.toUpperCase().startsWith("U#")){
			return null;
		}
		ucode = ucode.substring(1).toUpperCase();
		if(ucode == null || ucode.isEmpty()){
			return null;
		}
		int u = 0;
		try{
			u = Integer.decode(ucode);
			if (0 < u && u <= 0xffff && Character.isDefined((char)u)){
				return new Character((char)u);
			}
		} catch(NumberFormatException e){
			// e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param ulist	//List "U#0000 U#1234 ..."
	 * @param font	//if null, don't check canDisplay
	 * @return	//decoded chars to String
	 */
	public static String decodeList(String ulist, Font font){
		if(ulist == null || ulist.isEmpty()){
			return "";
		}
		String[] list = ulist.split(" ");
		StringBuffer sb = new StringBuffer();
		for(String ucode : list){
			Character ch = decode(ucode);
			if(ch == null){
				continue;
			}
			if(font == null){
				sb.append(ch.charValue());
			}
			else if (font.canDisplay(ch)){
				sb.append(ch.charValue());
			}
		}
		return sb.toString();
	}

	public static String decodeList(String ulist){
		return decodeList(ulist, null);
	}
}
