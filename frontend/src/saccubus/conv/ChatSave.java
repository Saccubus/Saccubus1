package saccubus.conv;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

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
		�������̎Q��	���l�����Q��	����
		<	&lt;	<	&#60;	���Ȃ�
		>	&gt;	>	&#62;	��Ȃ�
		&	&amp;	&	&#38;	�A���p�T���h
		"	&quot;	"	&#34;	��d���p��
			&nbsp;	 	&#160;	�X�y�[�X ( ���s�֎~�X�y�[�X )
		�H	&copy;	�H	&#169;	���쌠
		�H	&reg;	�H	&#174;	�o�^���W
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
