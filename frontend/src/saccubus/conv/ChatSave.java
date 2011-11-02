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
/*
	private static final int CMD_COLOR_DEF = 0;

	private static final int CMD_COLOR_RED = 1;

	private static final int CMD_COLOR_ORANGE = 2;

	private static final int CMD_COLOR_YELLOW = 3;

	private static final int CMD_COLOR_PINK = 4;

	private static final int CMD_COLOR_BLUE = 5;

	private static final int CMD_COLOR_PURPLE = 6;

	private static final int CMD_COLOR_CYAN = 7;

	private static final int CMD_COLOR_GREEN = 8;

	private static final int CMD_COLOR_NICOWHITE = 9;

	private static final int CMD_COLOR_MARINEBLUE = 10;

	private static final int CMD_COLOR_MADYELLOW = 11;

	private static final int CMD_COLOR_PASSIONORANGE = 12;

	private static final int CMD_COLOR_NOBLEVIOLET = 13;

	private static final int CMD_COLOR_ELEMENTALGREEN = 14;

	private static final int CMD_COLOR_TRUERED = 15;

	private static final int CMD_COLOR_BLACK = 16;
*/
	// all attributes and its values
	private String attributesStr = "";
/*
	@SuppressWarnings("unused")
	private Attributes attr = null;

	// "date"
	@SuppressWarnings("unused")
	private int Date = 0;
	private String dateStr = "";

	// "mail"
	private String mailStr = "";
	@SuppressWarnings("unused")
	private int Color = 0;
*/
	// "No"
	private int No = 0;
/*
	private String noStr = "";

	// "user_id"
	@SuppressWarnings("unused")
	private int UserID = 0;
	private String userIDStr = "";

	private String vposStr = "";

	// "fork"
	private String forkStr = "";
*/
	// comment itself
	private String Comment = "";

	public ChatSave() {
	}
/*
	public void setAttributes(Attributes attr){
		this.attr = attr;
	}
*/
	public void setAttributeString(String attributes){
		attributesStr = attributes;
	}
/*
	public void setDate(String date_str) {
		Date = Integer.parseInt(date_str);
		dateStr = date_str;
		// System.out.println("date:" + date_str);
	}

	public void setMail(String mail_str) {
		// System.out.println("mail:" + mail_str);
		if (mail_str == null) {
			return;
		}
		mailStr = mail_str;
/*
		String element[] = mail_str.split(" ");
		for (int i = 0; i < element.length; i++) {
			String str = element[i].toLowerCase();
			ロケーション
			if (str.equals("ue")) {
				Location = CMD_LOC_TOP;
			} else if (str.equals("shita")) {
				Location = CMD_LOC_BOTTOM;
			} else if (str.equals("big")) {
				Size = CMD_SIZE_BIG;
			} else if (str.equals("small")) {
				Size = CMD_SIZE_SMALL;
			} else if (str.equals("red")) {
				Color = CMD_COLOR_RED;
			} else if (str.equals("orange")) {
				Color = CMD_COLOR_ORANGE;
			} else if (str.equals("yellow")) {
				Color = CMD_COLOR_YELLOW;
			} else if (str.equals("pink")) {
				Color = CMD_COLOR_PINK;
			} else if (str.equals("blue")) {
				Color = CMD_COLOR_BLUE;
			} else if (str.equals("purple")) {
				Color = CMD_COLOR_PURPLE;
			} else if (str.equals("cyan")) {
				Color = CMD_COLOR_CYAN;
			} else if (str.equals("green")) {
				Color = CMD_COLOR_GREEN;
			} else if (str.equals("niconicowhite") || str.equals("white2")) {
				Color = CMD_COLOR_NICOWHITE;
			} else if (str.equals("arineblue") || str.equals("blue2")) {
				Color = CMD_COLOR_MARINEBLUE;
			} else if (str.equals("madyellow") || str.equals("yellow2")) {
				Color = CMD_COLOR_MADYELLOW;
			} else if (str.equals("passionorange") || str.equals("orange2")) {
				Color = CMD_COLOR_PASSIONORANGE;
			} else if (str.equals("nobleviolet") || str.equals("purple2")) {
				Color = CMD_COLOR_NOBLEVIOLET;
			} else if (str.equals("elementalgreen") || str.equals("green2")) {
				Color = CMD_COLOR_ELEMENTALGREEN;
			} else if (str.equals("truered") || str.equals("red2")) {
				Color = CMD_COLOR_TRUERED;
			} else if (str.equals("black")) {
				Color = CMD_COLOR_BLACK;
			} else {
				// System.out.println("Unknown command:" + str);
			}
		}

	}
*/

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

/*
	public void setUserID(String user_id_str) {
		userIDStr = user_id_str;
	}

	public void setVpos(String vpos_str) {
		vposStr = vpos_str;
	}

	public void setFork(String fork_str){
		forkStr = fork_str;
	}
*/
	public void setComment(String com_str) {
		// System.out.println("Comment[" + com_str.length() + "]:" + com_str);
		Comment += com_str;
	}

	public void printXML(PrintWriter pw)
		throws UnsupportedEncodingException {
		pw.print("<chat ");
/*
		if (attr != null){
			for (int i = 0; i <attr.getLength(); i++) {
				pw.print(attr.getQName(i));
				pw.print("=\"");
				pw.print(attr.getValue(i));
				pw.print("\" ");
			}
		}
*/
		pw.print(attributesStr);
		pw.print(">");
/*
		pw.print("vpos=\"" + vposStr + "\" ");
		pw.print("date=\"" + dateStr + "\" ");
		pw.print("mail=\"" + mailStr + "\" ");
		if (forkStr != null && !forkStr.isEmpty()){
			pw.print("fork=\"" + forkStr + "\" ");
		}
		pw.print("user_id=\"" + userIDStr + "\">");
*/
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
/*
	private String unsefeReference(String str){
		str = str.replace("&lt;", "<");
		str = str.replace("&gt;", ">");
		str = str.replace("&amp;", "&");
		reuturn str;
	}
*/
}
