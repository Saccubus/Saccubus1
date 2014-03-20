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
			System.out.print("Starting Combining XML files. ");
		try {
			if (filelist == null || filelist.isEmpty()){
				return false;
			}
			ChatArray chatArray = new ChatArray();
			SAXParserFactory spfactory = SAXParserFactory.newInstance();
			SAXParser saxparser = spfactory.newSAXParser();
			MultiXMLHandler xmlhandler = new MultiXMLHandler(chatArray);
			for (File file : filelist){
				String filename = file.getName();
				if (filename == null)
					break;
				int index = filename.lastIndexOf("[");
				if(index>0){
					int index2 = filename.lastIndexOf("]");
					if (index2<index)
						index2 = filename.length();
					filename = filename.substring(index+1,index2);
				}
				System.out.print(filename + ". ");
				saxparser.parse(file, xmlhandler);
			}
			// •ÏŠ·Œ‹‰Ê‚Ì‘‚«ž‚Ý
			chatArray.writeXML(output);
			System.out.println("\nCombining finished.");
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
