package saccubus.conv;

import java.io.*;

import saccubus.util.Util;

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
public class Chat {
	private static final int CMD_LOC_DEF = 0;

	private static final int CMD_LOC_TOP = 1;

	private static final int CMD_LOC_BOTTOM = 2;

	@SuppressWarnings("unused")
	private static final int CMD_SIZE_MAX = 3;

	private static final int CMD_SIZE_DEF = 0;

	private static final int CMD_SIZE_BIG = 1;

	private static final int CMD_SIZE_SMALL = 2;

	@SuppressWarnings("unused")
	private static final int COMMENT_FONT_SIZE[] = { 24, // DEF
			39, // BIG
			15, // SMALL
	};

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

	// "date"
	@SuppressWarnings("unused")
	private int Date = 0;

	// "mail"
	private int Color = 0;

	private int Size = 0;

	private int Location = 0;

	// "No"
	private int No = 0;

	// "user_id"
	@SuppressWarnings("unused")
	private int UserID = 0;

	// "vpos"
	private int Vpos = 0;

	private String Comment = "";

	public Chat() {
	}

	public void setDate(String date_str) {
		Date = Integer.parseInt(date_str);
		// System.out.println("date:" + date_str);
	}

	public void setMail(String mail_str) {
		// System.out.println("mail:" + mail_str);
		Color = CMD_COLOR_DEF;
		Size = CMD_SIZE_DEF;
		Location = CMD_LOC_DEF;
		if (mail_str == null) {
			return;
		}
		String element[] = mail_str.split(" ");
		for (int i = 0; i < element.length; i++) {
			String str = element[i].toLowerCase();
			/* ロケーション */
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

	public void setNo(String no_str) {
		try {
			No = Integer.parseInt(no_str);
		} catch (Exception e) {
			No = -1;
		}
		// System.out.println("no:" + no_str);
	}

	public void setUserID(String user_id_str) {
		// System.out.println("user_id:" + user_id_str);
		try {
			UserID = Integer.parseInt(user_id_str);
		} catch (Exception e) {
			UserID = -1;
		}
	}

	public void setVpos(String vpos_str) {
		// System.out.println("vpos:" + vpos_str);
		try {
			Vpos = Integer.parseInt(vpos_str);
		} catch (Exception e) {
			Vpos = -1;
		}

	}

	public void setComment(String com_str) {
		// System.out.println("Comment[" + com_str.length() + "]:" + com_str);
		if (Comment.equals("")) {
			Comment += com_str;
		} else {
			Comment += com_str;
		}
	}

	public void write(OutputStream os) throws IOException {
		byte[] a = null;
		try {
			a = (Comment + "\0").getBytes("UnicodeLittleUnmarked");
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		Util.writeInt(os, No);
		Util.writeInt(os, Vpos);
		Util.writeInt(os, Location);
		Util.writeInt(os, Size);
		Util.writeInt(os, Color);
		Util.writeInt(os, a.length);
		try {
			os.write(a);
		} catch (IOException ex1) {
			ex1.printStackTrace();
		}
	}

}
