package saccubus.conv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import saccubus.util.Logger;

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
public class ConvertToVideoHook {
	public static boolean convert(File file, File out, ArrayList<CommentReplace> list, Pattern ng_id,
			Pattern ng_word, CommandReplace ng_cmd, int score_limit, boolean live_op,
			boolean premium_color_check, String duration, Logger log, boolean is_debug) {
		try {
			Packet packet = new Packet(list, log, is_debug);
			// SAX�p�[�T�[�t�@�N�g���𐶐�
			SAXParserFactory spfactory = SAXParserFactory.newInstance();
			// SAX�p�[�T�[�𐶐�
			SAXParser parser = spfactory.newSAXParser();
			// XML�t�@�C�����w�肳�ꂽ�f�t�H���g�n���h���[�ŏ������܂�
			NicoXMLReader nico_reader = null;
			try {
				nico_reader = new NicoXMLReader(packet, ng_id, ng_word, ng_cmd, score_limit,
								live_op, premium_color_check, duration, log);
			} catch (java.util.regex.PatternSyntaxException e) {
				e.printStackTrace();
				return false;
			}
			if (nico_reader != null) {
				parser.parse(file, nico_reader);
			}
			// �ϊ����ʂ̏�������
			FileOutputStream fos = new FileOutputStream(out);
			packet.write(fos);
			fos.flush();
			fos.close();
			return true;
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (SAXException ex) {
			ex.printStackTrace();
		} catch (ParserConfigurationException ex) {
			ex.printStackTrace();
		}
		return false;
	}
}
