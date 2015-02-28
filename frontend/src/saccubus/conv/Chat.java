package saccubus.conv;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

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

	private static final int CMD_LOC_NAKA = 3;

	private static final int CMD_LOC_FULL = 4;

	private static final int CMD_LOC_WAKU = 8;

	static final int CMD_LOC_SCRIPT = 16;

	private static final int CMD_LOC_PATISSIER = 32;

	private static final int CMD_LOC_INVISIBLE = 64;

	static final int CMD_LOC_IS_BUTTON = 128;

	static final int CMD_LOC_SCRIPT_FOR_OWNER = 256;

	static final int CMD_LOC_SCRIPT_FOR_USER = 512;

	private static final int CMD_LOC_ENDER = 1024;

	/**
	 * Location bit 31-16 追加
	 * 0: 従来(既定値)、1～65535: ＠秒数 (数値=秒数+1)
	 */
	private static final int CMD_MAX_SECONDS = 0x0000ffff;
	private static final int CMD_LOC_SECONDS_BITS = 16;
	private static final int CMD_LOC_SECONDS_MASK = CMD_MAX_SECONDS << CMD_LOC_SECONDS_BITS;

	private static final int CMD_SIZE_DEF = 0;

	private static final int CMD_SIZE_BIG = 1;

	private static final int CMD_SIZE_SMALL = 2;

	private static final int CMD_SIZE_MEDIUM = 3;

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

	private static final int CMD_COLOR_WHITE = 17;

	private static final int CMD_COLOR_PINK2 = 18;

	private static final int CMD_COLOR_CYAN2 = 19;

	private static final int CMD_COLOR_NONE = 99;

	private static final int CMD_COLOR_ERROR = 100;
/*
	// "date"
	@SuppressWarnings("unused")
	private int Date = 0;
*/
	// "mail"
	private int Color = 0;
	private boolean isColorAssigned = false;

	private int Size = 0;
	private boolean isSizeAssigned = false;

	private int Location = 0;
	private boolean isLocationAssigned = false;

	// "No"
	private int No = 0;
/*
	// "user_id"
	@SuppressWarnings("unused")
	private int UserID = 0;
*/
	// "vpos"
	private int Vpos = 0;

	// "is Owner?"
	private boolean IsOwner = false;

	private String Comment = "";

	public Chat() {
	}
/*
	public void setDate(String date_str) {
		Date = Integer.parseInt(date_str);
		// System.out.println("date:" + date_str);
	}
*/
 	String strsec = "";
	int sec = 0;

	public void setMail(String mail_str) {
		// System.out.println("mail:" + mail_str);
		Color = CMD_COLOR_DEF;
		Size = CMD_SIZE_DEF;
		Location = CMD_LOC_DEF;
		if (mail_str == null) {
			return;
		}
		String element[] = mail_str.split(" +");
		for (int i = 0; i < element.length; i++) {
			String str = element[i].toLowerCase();
			/* ロケーション */
			if (str.equals("ue") && !isLocationAssigned) {
				Location |= CMD_LOC_TOP;
				isLocationAssigned = true;
			} else if (str.equals("shita") && !isLocationAssigned) {
				Location |= CMD_LOC_BOTTOM;
				isLocationAssigned = true;
			} else if (str.equals("naka") && !isLocationAssigned) {
				Location |= CMD_LOC_NAKA;
				isLocationAssigned = true;
			}
			// ＠秒数
			else if ((str.startsWith("@") || str.startsWith("＠")) && strsec.isEmpty()) {
				strsec = str.substring(1);
				if (!strsec.isEmpty()){
					try {
						sec = Integer.parseInt(strsec) + 1;	// @0 -> 1
						Location |= ((sec & CMD_MAX_SECONDS) << CMD_LOC_SECONDS_BITS) & CMD_LOC_SECONDS_MASK;
					} catch(NumberFormatException e){
						e.printStackTrace();
					}
				}
			}
			// フルコマンド
			else if (str.equals("full")){
				Location |= CMD_LOC_FULL;
			}
			// 枠コマンド
			else if (str.equals("waku")){
				Location |= CMD_LOC_WAKU;
			}
			// 菓子職人コマンド
			else if (str.equals("patissier")){
				Location |= CMD_LOC_PATISSIER;
			}
			// invisibleコマンド
			else if (str.equals("invisible")){
				Location |= CMD_LOC_INVISIBLE;
			}
			// is_buttonコマンド
			else if (str.equals("is_button")){
				Location |= CMD_LOC_IS_BUTTON;		//setButton(true)
			}
			// enderコマンド
			else if (str.equals("ender")){
				Location |= CMD_LOC_ENDER;
			}
			// サイズ
			else if (str.equals("big") && !isSizeAssigned) {
				Size = CMD_SIZE_BIG;
				isSizeAssigned = true;
			} else if (str.equals("small") && !isSizeAssigned) {
				Size = CMD_SIZE_SMALL;
				isSizeAssigned = true;
			} else if (str.equals("medium") && !isSizeAssigned) {
				Size = CMD_SIZE_MEDIUM;
				isSizeAssigned = true;
			} else {
				int color = getColorNumber(str);
				if (color == CMD_COLOR_NONE){
					// System.out.println("Unknown command:" + str);
					continue;
				}
				if (isColorAssigned){
					// color set more than twice
					System.out.println("[Chat.java]COLOR twice=" + str + ",mail=" + mail_str);
				} else if (color == CMD_COLOR_ERROR){
					System.out.println("[Chat.java]COLOR warning str=" + str + ",mail=" + mail_str);
					Color = CMD_COLOR_DEF;
					isColorAssigned = true;
				} else {
					Color = color;
					isColorAssigned = true;
				}
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
	public void setNo(int n){
		No = n;
	}
	public int getNo(){
		return No;
	}
/*
	public void setUserID(String user_id_str) {
		// System.out.println("user_id:" + user_id_str);
		try {
			UserID = Integer.parseInt(user_id_str);
		} catch (Exception e) {
			UserID = -1;
		}
	}
*/
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
		//Comment += com_str.replace("\t", "\u2001\u2001");
		//Comment += com_str.replace("\t", "      ");	//0x20 6文字
		Comment += com_str;
	}

	public void write(OutputStream os) throws IOException {
		byte[] a = {0,0,};
		try {
			a = (Comment + "\0").getBytes("UnicodeLittleUnmarked");
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
			//throw new IOException("[Chat/write:1]Processing:"+No+"<"+Comment+">");
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
			throw new IOException("[Chat/write:2]Processing:"+No+"<"+Comment+">");
		}
	}

	public void addCmd(int cmd) {
		Location |= cmd;
	}

	public static String makeWakuiro(String wakuiro) {
		//文字色[=:]枠色[,_;]〇〇繰り返し　で指定する。
		//英字色名を色番号に色コードはそのまま指定する。
		//一応defとwhiteは区別する。既定は「def=yellow」
		int color;
		if(!wakuiro.replaceAll("[=:,;]+", "-").contains("-")){
			//全ての枠色を指定
			color = getColorNumber(wakuiro);
			if(color < 0){
				color -= Integer.MIN_VALUE;
				return "0x" + Integer.toString(color, 16);
			}
			return Integer.toString(color);
		}
		//文字色と枠色のペアを出力
		StringBuilder sb = new StringBuilder();
		String[] list = wakuiro.split("[,;]+");
		for (String pairstr : list){
			String[] pair = pairstr.split("[=:]+");
			if(pair.length < 2)
				continue;
			if(Character.isDigit(pair[0].charAt(0))){
				//第1引数はコメント番号
				sb.append(pair[0]);
			}else{
				//第1引数は色指定
				sb.append("_");
				color = getColorNumber(pair[0]);
				if(color < 0){
					color -= Integer.MIN_VALUE;
					sb.append("0x" + Integer.toString(color, 16));
				}else if(color == CMD_COLOR_NONE||color == CMD_COLOR_ERROR){
					sb.append(Integer.toString(CMD_COLOR_DEF));
				}else{
					sb.append(Integer.toString(color));
				}
			}
			sb.append("_");
			color = getColorNumber(pair[1]);
			if(color < 0){
				color -= Integer.MIN_VALUE;
				sb.append("0x" + Integer.toString(color, 16));
			}else if(color == CMD_COLOR_NONE||color == CMD_COLOR_ERROR){
				sb.append(Integer.toString(CMD_COLOR_YELLOW));
			}else{
				sb.append(Integer.toString(color));
			}
			sb.append("/");
		}
		return sb.toString();
	}

	static int getColorNumber(String str) {
		//色名をカラーコード整数値に変換して返す
		//#rrggbbはマイナスの数にして返す
		// 色
		int color;
		if (str.equals("def"))
			return CMD_COLOR_DEF;
		if (str.equals("red"))
			return CMD_COLOR_RED;
		if (str.equals("orange"))
			return CMD_COLOR_ORANGE;
		if (str.equals("yellow"))
			return CMD_COLOR_YELLOW;
		if (str.equals("pink"))
			return CMD_COLOR_PINK;
		if (str.equals("blue"))
			return CMD_COLOR_BLUE;
		if (str.equals("purple"))
			return CMD_COLOR_PURPLE;
		if (str.equals("cyan"))
			return CMD_COLOR_CYAN;
		if (str.equals("green"))
			return CMD_COLOR_GREEN;
		if (str.equals("niconicowhite") || str.equals("white2"))
			return CMD_COLOR_NICOWHITE;
		if (str.equals("arineblue") || str.equals("blue2"))
			return CMD_COLOR_MARINEBLUE;
		if (str.equals("madyellow") || str.equals("yellow2"))
			return CMD_COLOR_MADYELLOW;
		if (str.equals("passionorange") || str.equals("orange2"))
			return CMD_COLOR_PASSIONORANGE;
		if (str.equals("nobleviolet") || str.equals("purple2"))
			return CMD_COLOR_NOBLEVIOLET;
		if (str.equals("elementalgreen") || str.equals("green2"))
			return CMD_COLOR_ELEMENTALGREEN;
		if (str.equals("truered") || str.equals("red2"))
			return CMD_COLOR_TRUERED;
		if (str.equals("black"))
			return CMD_COLOR_BLACK;
		if (str.equals("white"))
			return CMD_COLOR_WHITE;
		if (str.equals("pink2"))
			return CMD_COLOR_PINK2;
		if (str.equals("cyan2"))
			return CMD_COLOR_CYAN2;
		if (!str.startsWith("#"))
			return CMD_COLOR_NONE;	// not color
		// color 24bit
		if(str.length()<7){
			// not converted
			return CMD_COLOR_ERROR;	// default
		}
		try{
			color = Integer.decode(str);
			if(color < 0 || color > 0x00ffffff){
				// error
				return CMD_COLOR_ERROR;
			}
			// 24bit Color is represeted as MINUS value;
			return color + Integer.MIN_VALUE;
		} catch(NumberFormatException e){
			// error
			//e.printStackTrace();
			return CMD_COLOR_ERROR;	// error default
		}
	}

	int getVpos() {
		return Vpos;
	}

	void process(CommentReplace cr){
		Comment = cr.replace(Comment);
		Chat item = cr.getChat();
		if(item.Color != CMD_COLOR_DEF)
			Color = item.Color;
		if(item.Size != CMD_SIZE_DEF)
			Size = item.Size;
		if((item.Location & 3) != CMD_LOC_DEF)
			Location = (Location & ~3) | (item.Location & 3);
	}

	boolean isScript(){
		return (Location & CMD_LOC_SCRIPT)!=0;
	}
	void setOwner(boolean is_owner) {
		IsOwner = is_owner;
	}

	boolean isOwner(){
		return IsOwner;
	}
}
