package saccubus.conv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import saccubus.net.Path;
import saccubus.util.Logger;

/**
 *
 * @author orz
 * @version 1.22r3e
 *
 */
public class CombineXML {

	public static boolean combineXML(
			ArrayList<File> filelist, File output, Logger log){
			log.print("Starting Combining XML files. ");
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
				String text = Path.readAllText(file, "UTF-8");
				StringBuilder sb = new StringBuilder();
				if (text.startsWith("\uFEFF"))	//BOMÉRÅ[ÉhçÌèú
					sb.append(text.substring(1));
				else
					sb.append(text);
				String rexp = "^\s*<(chat|thread) ";
				Pattern p = Pattern.compile(rexp, Pattern.DOTALL);
				Matcher m = p.matcher(sb.toString());
				if (m.find()) {
					sb.insert(0, "<packet>" + System.getProperty("line.separator"));
					sb.insert(0, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + System.getProperty("line.separator"));
				}
				rexp = "</packet>";
				p = Pattern.compile(rexp, Pattern.DOTALL);
				m = p.matcher(sb.toString());
				if (!m.find()) {
					sb.append("</packet>" + System.getProperty("line.separator"));
				}
				text = sb.toString();
				rexp = "(</packet>.*<\\?xml [^>]*>.?<packet>|[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F])";
				p = Pattern.compile(rexp, Pattern.DOTALL);
				m = p.matcher(text);
				text = m.replaceAll("");
				String linecount = text.replaceAll("[^\n]", "");
				Path.writeAllText(file, text, "UTF-8");
				log.println("\nLines: "+linecount.length()+", File:"+file.getPath());
				saxparser.parse(file, xmlhandler);
			}
			// ïœä∑åãâ ÇÃèëÇ´çûÇ›
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
