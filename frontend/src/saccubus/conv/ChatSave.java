package saccubus.conv;

import java.io.*;

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
		�������̎Q��	���l�����Q��	����
		<	&lt;	<	&#60;	���Ȃ�
		>	&gt;	>	&#62;	��Ȃ�
		&	&amp;	&	&#38;	�A���p�T���h
		"	&quot;	"	&#34;	��d���p��
			&nbsp;	 	&#160;	�X�y�[�X ( ���s�֎~�X�y�[�X )
		�H	&copy;	�H	&#169;	���쌠
		�H	&reg;	�H	&#174;	�o�^���W
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
