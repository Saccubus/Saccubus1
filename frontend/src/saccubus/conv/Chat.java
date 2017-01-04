package saccubus.conv;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import saccubus.util.Logger;
import saccubus.util.Util;

/**
 * <p>
 * �^�C�g��: ������΂�
 * </p>
 *
 * <p>
 * ����: �j�R�j�R����̓�����R�����g���ŕۑ�
 * </p>
 *
 * <p>
 * ���쌠: Copyright (c) 2007 PSI
 * </p>
 *
 * <p>
 * ��Ж�:
 * </p>
 *
 * @author ������
 * @version 1.0
 */
public class Chat {
	private static final int CMD_LOC_DEF = 0;

	private static final int CMD_LOC_TOP = 1;

	private static final int CMD_LOC_BOTTOM = 2;

	private static final int CMD_LOC_NAKA = 3;

	private static final int CMD_EX_FULL = 4;

	private static final int CMD_EX_WAKU = 8;

	static final int CMD_EX_SCRIPT = 16;

	private static final int CMD_EX_PATISSIER = 32;

	private static final int CMD_EX_INVISIBLE = 64;

	static final int CMD_EX_IS_BUTTON = 128;

	private static final int CMD_EX_SCRIPT_FOR_OWNER = 256;

	private static final int CMD_EX_SCRIPT_FOR_USER = 512;

	private static final int CMD_EX_ENDER = 1024;

	/**
	 * Location bit 31-16 �ǉ�
	 * 0: �]��(����l)�A1�`65535: ���b�� (���l=�b��+1)
	 */
	private static final int CMD_MAX_SECONDS = 0x0000ffff;
	private static final int CMD_DUR_SECONDS_BITS = 16;

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

	//private static final int CMD_COLOR_RED = 1;

	//private static final int CMD_COLOR_ORANGE = 2;

	private static final int CMD_COLOR_YELLOW = 3;

	//private static final int CMD_COLOR_PINK = 4;

	//private static final int CMD_COLOR_BLUE = 5;

	//private static final int CMD_COLOR_PURPLE = 6;

	//private static final int CMD_COLOR_CYAN = 7;

	//private static final int CMD_COLOR_GREEN = 8;

	private static final int CMD_COLOR_NICOWHITE = 9;

	private static final int CMD_COLOR_MARINEBLUE = 10;

	private static final int CMD_COLOR_MADYELLOW = 11;

	private static final int CMD_COLOR_PASSIONORANGE = 12;

	private static final int CMD_COLOR_NOBLEVIOLET = 13;

	private static final int CMD_COLOR_ELEMENTALGREEN = 14;

	private static final int CMD_COLOR_TRUERED = 15;

	//private static final int CMD_COLOR_BLACK = 16;

	//private static final int CMD_COLOR_WHITE = 17;

	private static final int CMD_COLOR_PINK2 = 18;

	private static final int CMD_COLOR_CYAN2 = 19;

	private static final int CMD_COLOR_BLACK2 = 20;

	private static final int CMD_COLOR_NONE = 99;

	private static final int CMD_COLOR_ERROR = 100;

	private static final String[] COLOR_NAME = {
		//CMD_COLOR_DEF 0
		"def",
		//CMD_COLOR_RED 1
		"red",
		//CMD_COLOR_ORANGE 2
		"orange",
		//CMD_COLOR_YELLOW 3
		"yellow",
		//CMD_COLOR_PINK 4
		"pink",
		//CMD_COLOR_BLUE 5
		"blue",
		//CMD_COLOR_PURPLE 6
		"purple",
		//CMD_COLOR_CYAN 7
		"cyan",
		//CMD_COLOR_GREEN 8
		"green",
		//CMD_COLOR_NICOWHITE 9
		"white2",
		//CMD_COLOR_MARINEBLUE 10
		"blue2",
		//CMD_COLOR_MADYELLOW 11
		"yellow2",
		//CMD_COLOR_PASSIONORANGE 12
		"orange2",
		//CMD_COLOR_NOBLEVIOLET 13
		"purple2",
		//CMD_COLOR_ELEMENTALGREEN 14
		"green2",
		//CMD_COLOR_TRUERED 15
		"red2",
		//CMD_COLOR_BLACK 16
		"black",
		//CMD_COLOR_WHITE 17
		"white",
		//CMD_COLOR_PINK2 18
		"pink2",
		//CMD_COLOR_CYAN2 19
		"cyan2",
		//CMD_COLOR_BLACK2 20
		"black2",
	};

	private static HashMap<String, Integer> ColorNumberMap;

	static {
		ColorNumberMap = new HashMap<>();
		for(int i = 0; i < COLOR_NAME.length; i++){
			ColorNumberMap.put(COLOR_NAME[i], Integer.valueOf(i));
		}
		ColorNumberMap.put("niconicowhite", CMD_COLOR_NICOWHITE);
		ColorNumberMap.put("marineblue", CMD_COLOR_MARINEBLUE);
		ColorNumberMap.put("madyellow", CMD_COLOR_MADYELLOW);
		ColorNumberMap.put("passionorange", CMD_COLOR_PASSIONORANGE);
		ColorNumberMap.put("nobleviolet", CMD_COLOR_NOBLEVIOLET);
		ColorNumberMap.put("elementalgreen", CMD_COLOR_ELEMENTALGREEN);
		ColorNumberMap.put("truered", CMD_COLOR_TRUERED);
		ColorNumberMap.put("none", CMD_COLOR_NONE);
		ColorNumberMap.put("error", CMD_COLOR_ERROR);
	}
	// "mail"
	private int Color = 0;
	private boolean isColorAssigned = false;

	private int Size = 0;
	private boolean isSizeAssigned = false;

	private int Location = 0;
	private boolean isLocationAssigned = false;

	private int extend = 0;
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

	private Logger log;
	public Chat(Logger logger) {
		log = logger;
	}
/*
	public void setDate(String date_str) {
		Date = Integer.parseInt(date_str);
		// log.println("date:" + date_str);
	}
*/
 	String strsec = "";
	int sec = 0;

	public void setMail(String mail_str) {
		// log.println("mail:" + mail_str);
		Color = CMD_COLOR_DEF;
		Size = CMD_SIZE_DEF;
		Location = CMD_LOC_DEF;
		extend = 0;
		sec = 0;
		if (mail_str == null) {
			return;
		}
		String element[] = mail_str.split(" +");
		for (int i = 0; i < element.length; i++) {
			String str = element[i].toLowerCase();
			/* ���P�[�V���� */
			if (str.equals("ue") && !isLocationAssigned) {
				Location = CMD_LOC_TOP;
				isLocationAssigned = true;
			} else if (str.equals("shita") && !isLocationAssigned) {
				Location = CMD_LOC_BOTTOM;
				isLocationAssigned = true;
			} else if (str.equals("naka") && !isLocationAssigned) {
				Location = CMD_LOC_NAKA;
				isLocationAssigned = true;
			}
			// ���b��
			else if ((str.startsWith("@") || str.startsWith("��")) && strsec.isEmpty()) {
				strsec = str.substring(1);
				if (!strsec.isEmpty()){
					try {
						sec = Integer.parseInt(strsec) + 1;	// @0 -> 1
					} catch(NumberFormatException e){
						// log.printStackTrace(e);
						log.println("\nChat: �ϊ��G���[ @"+strsec+" at No:"+No);
					}
				}
			}
			// �t���R�}���h
			else if (str.equals("full")){
				extend |= CMD_EX_FULL;
			}
			// �g�R�}���h
			else if (str.equals("waku")){
				extend |= CMD_EX_WAKU;
			}
			// �َq�E�l�R�}���h
			else if (str.equals("patissier")){
				extend |= CMD_EX_PATISSIER;
			}
			// invisible�R�}���h
			else if (str.equals("invisible")){
				extend |= CMD_EX_INVISIBLE;
			}
			// is_button�R�}���h
			else if (str.equals("is_button")){
				extend |= CMD_EX_IS_BUTTON;		//setButton(true)
			}
			// ender�R�}���h
			else if (str.equals("ender")){
				extend |= CMD_EX_ENDER;
			}
			// �T�C�Y
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
					// log.println("Unknown command:" + str);
					continue;
				}
				if (isColorAssigned){
					// color set more than twice
					log.println("[Chat.java]COLOR twice=" + str + ",mail=" + mail_str);
				} else if (color == CMD_COLOR_ERROR){
					log.println("[Chat.java]COLOR warning str=" + str + ",mail=" + mail_str);
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
		// log.println("no:" + no_str);
	}
	public void setNo(int n){
		No = n;
	}
	public int getNo(){
		return No;
	}
/*
	public void setUserID(String user_id_str) {
		// log.println("user_id:" + user_id_str);
		try {
			UserID = Integer.parseInt(user_id_str);
		} catch (Exception e) {
			UserID = -1;
		}
	}
*/
	public void setVpos(String vpos_str) {
		// log.println("vpos:" + vpos_str);
		try {
			Vpos = Integer.parseInt(vpos_str);
		} catch (Exception e) {
			Vpos = -1;
		}

	}

	public void setComment(String com_str) {
		// log.println("Comment[" + com_str.length() + "]:" + com_str);
		//Comment += com_str.replace("\t", "\u2001\u2001");
		//Comment += com_str.replace("\t", "      ");	//0x20 6����
		Comment += com_str;
	}

	public void debug(Logger log) throws IOException {
		log.print("Chat.debug: No="+No);
		log.print(",Vpos="+Vpos);
		log.print(",Loc="+Location+",extend="+extend+",sec="+sec);
		log.print(",Size="+Size);
		log.print(",Color="+Color);
		log.print(",Length="+Comment.length());
		log.println(",\n"+Comment);
	}
	public void write(OutputStream os) throws IOException {
		byte[] a = {0,0,};
		try {
			a = (Comment + "\0").getBytes("UnicodeLittleUnmarked");
		} catch (UnsupportedEncodingException ex) {
			log.printStackTrace(ex);
			//throw new IOException("[Chat/write:1]Processing:"+No+"<"+Comment+">");
		}
		Util.writeInt(os, No);
		Util.writeInt(os, Vpos);
		Util.writeInt(os, Location | extend | ((sec & CMD_MAX_SECONDS) << CMD_DUR_SECONDS_BITS));
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

	void setButton() {
		extend |= CMD_EX_IS_BUTTON;
	}

	void setScript(){
		extend |= CMD_EX_SCRIPT;
	}
	void setScriptForUser(){
		extend |= CMD_EX_SCRIPT_FOR_USER;
	}
	void setScriptForOwner(){
		extend |= CMD_EX_SCRIPT_FOR_OWNER;
	}
	public static String makeWakuiro(String wakuiro) {
		//�����F[=:]�g�F[,_;]�Z�Z�J��Ԃ��@�Ŏw�肷��B
		//�p���F����F�ԍ��ɐF�R�[�h�͂��̂܂܎w�肷��B
		//�ꉞdef��white�͋�ʂ���B����́udef=yellow�v
		int color;
		if(!wakuiro.replaceAll("[=:,;]+", "-").contains("-")){
			//�S�Ă̘g�F���w��
			color = getColorNumber(wakuiro);
			if(color < 0){
				color -= Integer.MIN_VALUE;
				return "0x" + Integer.toString(color, 16);
			}
			return Integer.toString(color);
		}
		//�����F�Ƙg�F�̃y�A���o��
		StringBuilder sb = new StringBuilder();
		String[] list = wakuiro.split("[,;]+");
		for (String pairstr : list){
			String[] pair = pairstr.split("[=:]+");
			if(pair.length < 2)
				continue;
			if(Character.isDigit(pair[0].charAt(0))){
				//��1�����̓R�����g�ԍ�
				sb.append(pair[0]);
			}else{
				//��1�����͐F�w��
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

	static String getColorName(int col){
		//�J���[�R�[�h�����l��F���ɕϊ����ĕԂ�
		//�}�C�i�X�̐���#rrggbb�ɂ��ĕԂ�
		// �F
		if(col < 0)
			return String.format("#%06d", -col);
		if(col<=CMD_COLOR_BLACK2)
			return COLOR_NAME[col];
		return "error";
	}
	String getColorName() {
		return getColorName(Color);
	}

	int getColorNumber(){
		return Color;
	}
	static int getColorNumber(String str) {
		//�F�����J���[�R�[�h�����l�ɕϊ����ĕԂ�
		//#rrggbb�̓}�C�i�X�̐��ɂ��ĕԂ�
		// �F
		Integer colorNum = ColorNumberMap.get(str);
		if(colorNum!=null)
			return colorNum.intValue();
		int color;
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
			//log.printStackTrace(e);
			return CMD_COLOR_ERROR;	// error default
		}
	}

	int getVpos() {
		return Vpos;
	}

	void process(int rcolor, int rsize, int rlocation, String rcom){
		if(!rcom.equals(Comment)){
			Comment = rcom;
			//�����񂪒u�����ꂽ�Ȃ�J���[�E�T�C�Y�E���P�[�V�������u��
			if(rcolor != CMD_COLOR_DEF)
				Color = rcolor;
			if(rsize != CMD_SIZE_DEF)
				Size = rsize;
			if((rlocation) != CMD_LOC_DEF)
				Location = rlocation;
			//log.println("Chat: comment replaced #"+getNo()+":"+getVpos());
		}
	}

	boolean isScript(){
		return (extend & CMD_EX_SCRIPT)!=0;
	}
	void setOwner(boolean is_owner) {
		IsOwner = is_owner;
	}

	boolean isOwner(){
		return IsOwner;
	}

	boolean isPremumColor() {
		return (Color < 0 ) ||
			(Color == CMD_COLOR_NICOWHITE ) ||
			(Color == CMD_COLOR_MARINEBLUE ) ||
			(Color == CMD_COLOR_MADYELLOW ) ||
			(Color == CMD_COLOR_PASSIONORANGE ) ||
			(Color == CMD_COLOR_NOBLEVIOLET ) ||
			(Color == CMD_COLOR_ELEMENTALGREEN ) ||
			(Color == CMD_COLOR_TRUERED ) ||
			(Color == CMD_COLOR_CYAN2 ) ||
			(Color == CMD_COLOR_PINK2 ) ||
			(Color == CMD_COLOR_BLACK2);
	}

	void setDefColor() {
		Color = CMD_COLOR_DEF;
		isColorAssigned = true;
	}

	int getDurationSec() {
		return sec;
	}
	int getSize() {
		return Size;
	}
	int getLocation() {
		return Location;
	}
	String getSizeName() {
		if(Size==CMD_SIZE_BIG)
			return "big";
		if(Size==CMD_SIZE_SMALL)
			return "small";
		if(Size==CMD_SIZE_MEDIUM)
			return "medium";
		return "def";
	}
	public String getLocName() {
		switch(Location){
		case CMD_LOC_BOTTOM:
			return "shita";
		case CMD_LOC_TOP:
			return "ue";
		case CMD_LOC_NAKA:
			return "naka";
		}
		return "def";
	}

	String getComment() {
		return Comment;
	}
}
