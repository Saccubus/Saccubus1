package saccubus.conv;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.xml.sax.*;
import javax.xml.parsers.*;

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
public class ConvertToVideoHook {
	public static boolean convert(File file, File out, ArrayList<CommentReplace> list, Pattern ng_id,
			Pattern ng_word, CommandReplace ng_cmd, int score_limit) {
		try {
			Packet packet = new Packet(list);
			// SAXパーサーファクトリを生成
			SAXParserFactory spfactory = SAXParserFactory.newInstance();
			// SAXパーサーを生成
			SAXParser parser = spfactory.newSAXParser();
			// XMLファイルを指定されたデフォルトハンドラーで処理します
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
			// 変換結果の書き込み
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
