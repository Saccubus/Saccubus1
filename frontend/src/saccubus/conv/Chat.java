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

	private static final int CMD_LOC_FULL = 4;

	/**
	 * Location bit 15-8 追加
	 * 0: 従来、1～255: ＠秒数
	 */
	@SuppressWarnings("unused")
	private static final int CMD_LOC_SECONDS_MASK = 0x0000ff00;

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

	private String Comment = "";

	public Chat() {
	}
/*
	public void setDate(String date_str) {
		Date = Integer.parseInt(date_str);
		// System.out.println("date:" + date_str);
	}
	String strsec = "";
	int sec = 0;
*/
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
			if (str.equals("ue") && !isLocationAssigned) {
				Location |= CMD_LOC_TOP;
				isLocationAssigned = true;
			} else if (str.equals("shita") && !isLocationAssigned) {
				Location |= CMD_LOC_BOTTOM;
				isLocationAssigned = true;
			}
	/*
			// ＠秒数
			else if (str.startsWith("@") && strsec.isEmpty()) {
				strsec = str.substring(1);
				if (strsec != null && !strsec.isEmpty()){
					try {
						sec = Integer.parseInt(strsec);
						Location |= ((sec & 255) << 8) & CMD_LOC_SECONDS_MASK;
					} catch(NumberFormatException e){
						e.printStackTrace();
					}
				}
			}
	*/
			// フルコマンド
			else if (str.equals("full")){
				Location |= CMD_LOC_FULL;
			}
			// サイズ
			else if (str.equals("big") && !isSizeAssigned) {
				Size = CMD_SIZE_BIG;
				isSizeAssigned = true;
			} else if (str.equals("small") && !isSizeAssigned) {
				Size = CMD_SIZE_SMALL;
				isSizeAssigned = true;
			}
			// 色
			else if (str.equals("red") && !isColorAssigned) {
				Color = CMD_COLOR_RED;
				isColorAssigned = true;
			} else if (str.equals("orange") && !isColorAssigned) {
				Color = CMD_COLOR_ORANGE;
				isColorAssigned = true;
			} else if (str.equals("yellow") && !isColorAssigned) {
				Color = CMD_COLOR_YELLOW;
				isColorAssigned = true;
			} else if (str.equals("pink") && !isColorAssigned) {
				Color = CMD_COLOR_PINK;
				isColorAssigned = true;
			} else if (str.equals("blue") && !isColorAssigned) {
				Color = CMD_COLOR_BLUE;
				isColorAssigned = true;
			} else if (str.equals("purple") && !isColorAssigned) {
				Color = CMD_COLOR_PURPLE;
				isColorAssigned = true;
			} else if (str.equals("cyan") && !isColorAssigned) {
				Color = CMD_COLOR_CYAN;
				isColorAssigned = true;
			} else if (str.equals("green") && !isColorAssigned) {
				Color = CMD_COLOR_GREEN;
				isColorAssigned = true;
			} else if ((str.equals("niconicowhite") || str.equals("white2")) && !isColorAssigned) {
				Color = CMD_COLOR_NICOWHITE;
				isColorAssigned = true;
			} else if ((str.equals("arineblue") || str.equals("blue2")) && !isColorAssigned) {
				Color = CMD_COLOR_MARINEBLUE;
				isColorAssigned = true;
			} else if ((str.equals("madyellow") || str.equals("yellow2")) && !isColorAssigned) {
				Color = CMD_COLOR_MADYELLOW;
				isColorAssigned = true;
			} else if ((str.equals("passionorange") || str.equals("orange2")) && !isColorAssigned) {
				Color = CMD_COLOR_PASSIONORANGE;
				isColorAssigned = true;
			} else if ((str.equals("nobleviolet") || str.equals("purple2")) && !isColorAssigned) {
				Color = CMD_COLOR_NOBLEVIOLET;
				isColorAssigned = true;
			} else if ((str.equals("elementalgreen") || str.equals("green2")) && !isColorAssigned) {
				Color = CMD_COLOR_ELEMENTALGREEN;
				isColorAssigned = true;
			} else if ((str.equals("truered") || str.equals("red2")) && !isColorAssigned) {
				Color = CMD_COLOR_TRUERED;
				isColorAssigned = true;
			} else if (str.equals("black") && !isColorAssigned) {
				Color = CMD_COLOR_BLACK;
				isColorAssigned = true;
			} else if (str.startsWith("#") && !isColorAssigned){
				// color 24bit1
				if(str.length()<7){
					Color = CMD_COLOR_DEF;	// default
					System.out.println("[Chat.java]waring str=" + str + ",mail=" + mail_str);
				}else{
					try{
						Color = Integer.decode(str);
						if(Color < 0 || Color > 0x00ffffff){
							Color = CMD_COLOR_DEF;
						} else{
							// 24bit Color is represeted as MINUS value;
							Color += Integer.MIN_VALUE;
						}
					} catch(NumberFormatException e){
						System.out.println("[Chat.java]error str=" + str + ",mail=" + mail_str);
						//e.printStackTrace();
						Color = CMD_COLOR_DEF;	// default
					}
				}
				isColorAssigned = true;
		// 		Color = simulateColor16(str.substr(1));
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
		Comment += com_str.replace("\t", "\u2001\u2001");
		//Comment += com_str.replace("\t", "      ");	//0x20 6文字
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

}
