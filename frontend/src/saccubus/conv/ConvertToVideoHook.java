package saccubus.conv;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.xml.sax.*;
import javax.xml.parsers.*;

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
			Pattern ng_word, CommandReplace ng_cmd, int score_limit) {
		try {
			Packet packet = new Packet(list);
			// SAX�p�[�T�[�t�@�N�g���𐶐�
			SAXParserFactory spfactory = SAXParserFactory.newInstance();
			// SAX�p�[�T�[�𐶐�
			SAXParser parser = spfactory.newSAXParser();
			// XML�t�@�C�����w�肳�ꂽ�f�t�H���g�n���h���[�ŏ������܂�
			NicoXMLReader nico_reader = null;
			try {
				nico_reader = new NicoXMLReader(packet, ng_id, ng_word, ng_cmd, score_limit);
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
