package saccubus.conv;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

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
	// qName
	final private String qName;

	// all attributes and its values
	private ChatAttribute attributesStr = null;

	// comment itself
	private String Comment = "";

	public ChatSave(String qname) {
		qName = qname;
	}

	public String getQName() {
		return qName;
	}

	public void setAttributeString(ChatAttribute attributes){
		attributesStr = attributes;
	}

	public ChatAttribute getAttributes() {
		return attributesStr;
	}

	public void setComment(String com_str) {
		// System.out.println("Comment[" + com_str.length() + "]:" + com_str);
		Comment += com_str;
	}

	public void printXML(PrintWriter pw)
		throws UnsupportedEncodingException {
		pw.print("<"+qName);
		String s = safeReference(attributesStr.getValue());
		if(!s.isEmpty()){
			pw.print(" ");
			pw.print(safeReference(attributesStr.getValue()));
		}
		if(Comment!=null && !Comment.isEmpty()){
			pw.print(">");
			pw.print(safeReference(Comment));
			pw.println("</" + qName + ">");
		}else{
			pw.println("/>");
		}
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

	public static String safeReference(String str){
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
