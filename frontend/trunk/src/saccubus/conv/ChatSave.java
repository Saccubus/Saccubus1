package saccubus.conv;

import java.io.*;

/**
 * <p>
 * タイトル: さきゅばす
 * </p>
 *
 * <p>
 * 説明: ニコニコ動画の動画をコメントつきで保存
 * </p>
 *
 * <p>
 * 著作権: Copyright (c) 2007 PSI
 * </p>
 *
 * <p>
 * 会社名:
 * </p>
 *
 * @author 未入力
 * @version 1.0
 */
public class ChatSave {
	// all attributes and its values
	private String attributesStr = "";

	// "No"
	private int No = 0;

	// comment itself
	private String Comment = "";

	public ChatSave() {
	}

	public void setAttributeString(String attributes){
		attributesStr = attributes;
	}

	public void setNo(String no_str) {
//		noStr = no_str;
		try {
			No = Integer.parseInt(no_str);
		} catch (Exception e) {
			No = -1;
		}
	}

	public int getNo() {
		return No;
	}

	public void setComment(String com_str) {
		// System.out.println("Comment[" + com_str.length() + "]:" + com_str);
		Comment += com_str;
	}

	public void printXML(PrintWriter pw)
		throws UnsupportedEncodingException {
		pw.print("<chat ");
		pw.print(attributesStr);
		pw.print(">");
		pw.print(safeReference(Comment));
		pw.println("</chat>");
	}

	/*
		文字実体参照	数値文字参照	説明
		<	&lt;	<	&#60;	小なり
		>	&gt;	>	&#62;	大なり
		&	&amp;	&	&#38;	アンパサンド
		"	&quot;	"	&#34;	二重引用符
			&nbsp;	 	&#160;	スペース ( 改行禁止スペース )
		？	&copy;	？	&#169;	著作権
		？	&reg;	？	&#174;	登録商標
	*/

	private String safeReference(String str){
		if (str == null){
			return "";
		}
		str = str.replace("&", "&amp;");
		str = str.replace("<", "&lt;");
		str = str.replace(">", "&gt;");
//		str = str.replace("\"", "&quot;");
		return str;
	}
}
