package saccubus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import saccubus.json.Mson;
import saccubus.net.Loader;
import saccubus.net.Path;

//import saccubus.util.Stopwatch;

/**
 * MylistGetter url�̃}�C���X�g���l�b�g����ǂݍ���� ����ID_title<���s> �̃��X�g�������Ԃ�
 * extends SwingWorker<String,String>
 * @author orz
 *
 */
public class MylistGetter extends SwingWorker<String, String> {

	private static final boolean MYLIST_DEBUG = false;
	private static final boolean MYLIST_DEBUG2 = false;
	private String url;
	private final MainFrame parent;
	private final JLabel[] status3;
	private JLabel Status;
//	private JLabel MovieInfo;
	private final ConvertStopFlag StopFlag;
	private final StringBuffer ret;

	private final ConvertingSetting Setting;
	private String resultText;
	private String mylistID;
	private String watchInfo;
//	private String Tag;

	public MylistGetter(String url0, MainFrame frame, JLabel[] in_status, ConvertStopFlag flag, StringBuffer sb){
		url = url0;
		int index = url.indexOf('?');
		if(index >= 0){
		//	int index2 = url.lastIndexOf('/',index);
		//	Tag = url.substring(index2+1,index);
			watchInfo = url.substring(index);
		}else{
		//	int index2 = url.lastIndexOf('/');
		//	Tag = url.substring(index2+1);
			watchInfo = "";
		}
		parent = frame;
		status3 = in_status;
		Status = status3[0];
		StopFlag = flag;
		Setting = frame.getSetting();
		ret = sb;
	}

	public MylistGetter(String tag, String info, ConvertingSetting setting, JLabel[] in_status, ConvertStopFlag flag, StringBuffer sb){
		url = tag;
		watchInfo = info;
		parent = null;
		status3 = in_status;
		Status = status3[0];
		StopFlag = flag;
		Setting = setting;
		ret = sb;
	}

	//	private void sendtext(String text) {
//		Status.setText(text);
//	}

	private void sendtext(String text) {
	//	Status.setText(text);
		publish(text);
	}

	protected void process(List<String> lists){
	//	Status.setText(text);
		String text = "";
		for(String s:lists) text = s;
		Status.setText(text);
	}

	@SuppressWarnings("unused")
	@Override
	protected String doInBackground() throws Exception {
		if(!url.startsWith("http")){
			// return ret unchanged
			return "00";
		}else{
			final ArrayList<String[]> plist = new ArrayList<String[]>();
			//start here.
			Path file = Path.mkTemp(url.replace("http://","").replace("nicovideo.jp/","")
					.replaceAll("[/\\:\\?=\\&]+", "_") + ".html");
			Loader loader = new Loader(Setting, status3);
			if(!loader.load(url,file)){
				sendtext("load���s "+url);
				return "E1";
			}
			String text = Path.readAllText(file.getPath(), "UTF-8");
			sendtext("�ۑ����܂����B" + file.getRelativePath());
			if(StopFlag.needStop()) {
				return "FF";
			}
			if(MYLIST_DEBUG && parent!=null){
				resultText = HtmlView.markupHtml(text);
				final HtmlView hv = new HtmlView(parent, "�}�C���X�g", url);
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						hv.setText(resultText);
					}
				});
			}
			if(StopFlag.needStop()) {
				return "FF";
			}
			if(url.contains("api/deflist")) {
				//mylist api����
				mylistID = "deflist";
			}else
			if(url.contains("api/mylist/list?group_id=")){
				//mylist api����
				int start = url.indexOf("id=")+3;
				mylistID = url.substring(start);
			}else
			if(url.contains("mylist")) {
				//mylist����
				String json_start = "Mylist.preload(";
				int start = text.indexOf(json_start);
				if(start < 0){
					sendtext("JSON not found "+url);
					return "E2";	//JSON not found
				}
				start += json_start.length();
				int end = (text+");\n").indexOf(");\n", start);	// end of JSON
				text = (text+");\n").substring(start, end);
				start = text.indexOf(",");
				mylistID = text.substring(0, start);
				text = text.substring(start+1).trim();
			}else{
				// here will come XML parser
			}
			//common
			try {
				file = new Path(file.getRelativePath().replace(".html", ".xml"));
				Path.unescapeStoreXml(file, text, url);		//xml is property key:json val:JSON
				Properties prop = new Properties();
					prop.loadFromXML(new FileInputStream(file));		//read JSON xml
				text = prop.getProperty("json", "0");
				file = new Path(file.getRelativePath().replace(".html", ".xml"));
				//
				if(MYLIST_DEBUG && parent!=null){
					resultText = HtmlView.markupHtml(text);
					final HtmlView hv2 = new HtmlView(parent, "�}�C���X�g mson", "mson");
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							hv2.setText(resultText);
						}
					});
				}
				//
				System.out.println("get mylist/"+mylistID);
				System.out.println("mson: "+text.length());
				if(StopFlag.needStop()) {
					return "FF";
				}
				// parse mson
				sendtext("�p�[�X���s��");
				Mson mson = null;
				try{
					mson = Mson.parse(text);
				}catch(Exception e){
					e.printStackTrace();
				}
				if(mson==null){
					sendtext("�p�[�X���s");
					return "E3";
				}
				sendtext("�p�[�X���� "+mylistID);
				if(StopFlag.needStop()) {
					return "FF";
				}
				//rename to .txt
				file = new Path(file.getRelativePath().replace(".xml", ".txt"));
				mson.prettyPrint(new PrintStream(file));	//pretty print
				sendtext("���X�g���� "+mylistID);
				if(StopFlag.needStop()) {
					return "FF";
				}
				String[] keys = {"watch_id","title"};
				ArrayList<String[]> id_title_list = mson.getListString(keys);	// List of id & title
				for(String[] vals:id_title_list){
				//	System.out.println("Getting ["+ vals[0] + "]"+ vals[1]);
					plist.add(0, vals);
				}

				int sz = plist.size();
				sendtext("���o���� "+mylistID + "�@"+sz+"�@"+ url);
				System.out.println("Success mylist/"+mylistID+" item:"+sz);
				if(sz == 0){
					sendtext("���悪����܂���B"+mylistID);
					return "E4";
				}
				if(StopFlag.needStop()) {
					return "FF";
				}
				if(MYLIST_DEBUG2 && parent!=null){
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							TextView dlg = new TextView(parent, "mylist/"+mylistID);
							JTextArea textout = dlg.getTextArea();
							for(String[] idts:plist){
								textout.append("["+idts[0]+"]"+idts[1]+"\n");
							}
							textout.setCaretPosition(0);
						}
					});
				}
				if(StopFlag.needStop()) {
					return "FF";
				}
				//start downloader
				if(Setting.isSaveAutoList()){
					saveAutoList(plist);
					sendtext("autolist.bat �o�͐���");
				} else {
					//���ʃf�[�^
					for(String[] vals:plist){
						ret.append(vals[0]+"\t"+vals[1]+"\t"+watchInfo+"\n");
					}
				}
				return "00";
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally{

			}
		}
		return "EX";
	}

	//�I����EDT�Ŏ������s�����
	public void done(){
		String result = null;
		try {
			result = get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		StopFlag.finish();
		StopFlag.setButtonEnabled(false);
		if (result==null){
			System.out.println("done#get()==null �o�O?");
		}else{
			System.out.println("done#get() "+result);
		}
		sendtext("["+result+"]");
		if(parent!=null)
			parent.myListGetterDone(ret);
	}

	private void saveAutoList(ArrayList<String[]> mylist) {
		File autobat = new File(".\\auto.bat");
		final String CMD_LINE = "%CMD% ";
		File autolist = new File(".\\autolist.bat");
		if(!autobat.canRead()){
			System.out.println("auto.bat���Ȃ��̂�autolist.bat���o�͂ł��܂���:"+mylistID);
			sendtext("�o�͎��s autolist.bat:"+mylistID);
			return;
		}
		BufferedReader br = null;
		PrintWriter pw = null;
		String s;
		boolean flag2nd = false;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(autobat), "MS932"));
			pw = new PrintWriter(autolist, "MS932");
			pw.println(":�������� autolist.bat for mylist/" + mylistID);
			pw.println(": produced by Saccubus" + MainFrame_AboutBox.rev + " " + new Date());
			pw.println(":�\�\�\�\�\�\�\�\�\�\�\�\�\�\�\�\�\�\");
			while((s = br.readLine())!=null){
				if(!s.startsWith(CMD_LINE)){
					// %CMD%�s�������܂ŃR�s�[
					pw.println(s);
				}else{
					// �}�C���X�g��%CMD%�o��
					pw.println(":�\�\�\�\�\�\�\�\�\�\�\�\�\�\�\�\�\�\");
					pw.println(":set OPTION=�ߋ����O���� ���̃I�v�V���� �Ȃǂ�K�v�ɉ����w��(readmeNew.txt�Q��)");
					pw.println("set OPTION=");
					pw.println(":�ۑ��ϊ����Ȃ��s�͍폜���Ă�������");
					watchInfo = watchInfo.replace('?', '+');
					if(!s.contains("auto")){
						// �}�C���X�g��%CMD%�o��(�L�q�@1)
						for(String[] ds: mylist){
							pw.println(":�^�C�g�� " + ds[1]);
							pw.println("%CMD% "+ ds[0] + "?watch_harmful=1"+watchInfo+" %OPTION% @PUP");
						}
					}else{
						// �}�C���X�g��%CMD%�o��(�L�q�@2)
						flag2nd = true;
						pw.print("%CMD% autolist?watch_harmful=1"+watchInfo+" %OPTION% @PUP");
					}
					break;
				}
			}
			while((s = br.readLine())!=null){
				if(!s.startsWith(CMD_LINE)){
					// %CMD%�s�ȊO���o��
					pw.println(s);
					continue;
				}
			}
			sendtext("�o�͐��� autolist.bat:"+mylistID);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try{
				br.close();
				pw.flush();
				pw.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		if(flag2nd){
			// �L�q�@2 autolist.txt�o��
			File autolisttxt = new File(".\\autolist.txt");
			pw = null;
			try {
				pw = new PrintWriter(autolisttxt, "MS932");
				pw.println(":�������� autolist.txt for mylist/" + mylistID);
				pw.println(": produced by Saccubus" + MainFrame_AboutBox.rev + " " + new Date());
				pw.println(":�ۑ��ϊ����Ȃ��s�͍폜���Ă�������");
				for(String[] ds: mylist){
					pw.println(ds[0] + "\t�^�C�g�� :" + ds[1]);
				}
				sendtext("�o�͐��� autolist.txt:"+mylistID);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (pw!=null) {
					try{
						pw.flush();
						pw.close();
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
			}
		}

	}

}
