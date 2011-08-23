package saccubus.conv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.PatternSyntaxException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author orz
 * @version 1.22r3e
 *
 */
public class CombineXML {

	public static boolean combineXML(
			ArrayList<File> filelist, File output){
		try {
			if (filelist == null || filelist.isEmpty()){
				return false;
			}
			ChatArray chatArray = new ChatArray();
			SAXParserFactory spfactory = SAXParserFactory.newInstance();
			SAXParser saxparser = spfactory.newSAXParser();
			MultiXMLHandler xmlhandler = new MultiXMLHandler(chatArray);
			for (int i = 0; i < filelist.size(); i++){
				File file = filelist.get(i);
				saxparser.parse(file, xmlhandler);
			}
			// •ÏŠ·Œ‹‰Ê‚Ì‘‚«ž‚Ý
			chatArray.writeXML(output);
			return true;
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (SAXException ex) {
			ex.printStackTrace();
		} catch (ParserConfigurationException ex) {
			ex.printStackTrace();
		} catch (PatternSyntaxException ex) {
			ex.printStackTrace();
		}
		return false;
	}

}
