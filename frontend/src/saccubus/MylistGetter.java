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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import saccubus.json.Mson;
import saccubus.net.Gate;
import saccubus.net.Loader;
import saccubus.net.Path;
import saccubus.util.Logger;

//import saccubus.util.Stopwatch;

/**
 * MylistGetter urlのマイリストをネットから読み込んで 動画ID_title<改行> のリスト文字列を返す
 * extends SwingWorker<String,String>
 * @author orz
 *
 */
public class MylistGetter extends SwingWorker<String, String> {

	private static final boolean MYLIST_DEBUG = false;
	private static final boolean MYLIST_DEBUG2 = false;
	static final String WATCH_HARMFUL = "watch_harmful=1";
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
	private Gate gate;
	private final int id;
	private final ErrorControl errorControl;
	private Logger log;

	public MylistGetter(int worker_id, String tag, String info, MainFrame frame,
		JLabel[] in_status,ConvertStopFlag flag, ErrorControl error_control,
		StringBuffer sb, Logger logger)
	{
		id = worker_id;
		url = tag;
		watchInfo = info;
		parent = frame;
		status3 = in_status;
		Status = status3[0];
		StopFlag = flag;
		Setting = frame.getSetting();
		ret = sb;
		errorControl = error_control;
		log = new Logger("mylist", id, ConvertWorker.TMP_LOG_FRONTEND);
		log.addSysout(logger);
	}

	public MylistGetter(int worker_id, String tag, String info, ConvertingSetting setting,
			JLabel[] in_status, ConvertStopFlag flag, ErrorControl errcon, StringBuffer sb,
			Logger logger)
	{
		id = worker_id;
		url = tag;
		watchInfo = info;
		parent = null;
		status3 = in_status;
		Status = status3[0];
		StopFlag = flag;
		Setting = setting;
		ret = sb;
		errorControl = errcon;
		log = new Logger("mylistB", id, ConvertWorker.TMP_LOG_FRONTEND);
		log.addSysout(logger);
	}

	private String mySendText = "";
	private void sendtext(String text) {
		mySendText = text;
		publish(text);
	}
	private String gettext(){
		return mySendText;
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
		}
		final ArrayList<String[]> plist = new ArrayList<String[]>();
		//start here.
		String autolist = "auto";
		Path file = Path.mkTemp(url.replace("http://","").replace("nicovideo.jp/","")
				.replaceAll("[/\\:\\?=\\&]+", "_") + ".html");
		Loader loader = new Loader(Setting, status3, log, Setting.isHtml5());
		gate = Gate.open(id,log);
		if(!loader.load(url+watchInfo,file)){
			addError("E1",url);
			sendtext("[E1]load失敗 "+url);
			gate.exit("E1");
			return "E1";
		}
		gate.exit("0");
		String text = Path.readAllText(file.getPath(), "UTF-8");
		sendtext("保存しました。" + file.getRelativePath());
		if(StopFlag.needStop()) {
			return "FF";
		}
		if(MYLIST_DEBUG && parent!=null){
			resultText = HtmlView.markupHtml(text);
			final HtmlView hv = new HtmlView(parent, "マイリスト", url);
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
			//mylist api処理
			mylistID = "deflist";
		}else
		if(url.contains("api/mylist/list?group_id=")){
			//mylist api処理
			int start = url.indexOf("id=")+3;
			mylistID = url.substring(start);
		}else
		if(url.contains("mylist")) {
			//mylist処理
			String json_start = "Mylist.preload(";
			int start = text.indexOf(json_start);
			if(start < 0){
				addError("E2",url);
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
			mylistID = "0";	//other page
			HashSet<String> errorSet = new HashSet<>();
			// it would be better that here would come XML parser, but just scraping
			// watch/(sm|nm|so)?9999 を抽出してautolist0.txtに出力する
			HashMap<String,String> map = new LinkedHashMap<>();
			String regex = "watch/((s[a-z]|n[a-z])?[1-9][0-9]*)";	//抽出ID
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(text);
			String titleRe = "(title|alt)=\"([^\"]+)\"";	//抽出タイトル
			Pattern p2 = Pattern.compile(titleRe);	//title pattern
			int i1 = 0;	//last index of last match key
			String key = "";
			String val = "";
			text += "watch/1";	//setinel of dummy ID
			while(m.find()){
				if(i1 > 0){
					Matcher m2 = p2.matcher(text.substring(i1, m.start()));	// title matcher
					String title = map.get(key);		// 登録済みタイトル
					val = "";
					while(m2.find()) {
						String val2 = m2.group(2);
						if(val2==null)
							val2 = "";
						else if("1列".equals(val2) || "2列".equals(val2) || "4列".equals(val2))
							val2 = "";
						else if("キャッシュ".equals(val2)||"マイリストコメント一覧".equals(val2))
							val2 = "";
						else if(val2.contains(" -"))
							val2 = val2.substring(val2.indexOf(" -")+(" -").length());
						val2 = val2.replace("&nbsp;", " ").replace("&amp;", "＆");
						val2 = val2.trim();
						if(!val2.isEmpty() && (val.isEmpty() || val.length() < val2.length())){
							val = val2;
						}
					}
					if(val.isEmpty()){
						// title抽出失敗
						if(title.isEmpty()){
							//keyのみ登録済み→削除
							map.remove(key);
							log.println("title isEmpty() ERROR map.remove("+key+")");
							errorSet.add(key);	//error登録
						}else{
							//タイトル登録済み
							//log.println("title isEmpty() title("+key+","+map.get(key)+") duplicated key");
						}
					}else{
						// タイトル抽出ok
						if(title.isEmpty()){
							//タイトル未登録
							map.put(key, val);
							log.println("map.put("+key+",\""+val+"\")");
						}else if(!title.equals(val)){
							//タイトル登録済み
							if(title.length()<val.length() && val.length()<36){
								map.put(key, val);
								log.println("map.put("+key+",\""+val+"\")");
							}
							else if(title.length()<val.length()){
								val = (title + "_" + val);
								if(val.length()>48)
									val = val.substring(0,48);
								map.put(key, val);
								log.println("map.put("+key+",\""+val+"\")");
							}
							//else
							//	log.println("title("+key+") found \""+val+"\" but title was \""+title+"\"");
						}
					}
				}
				i1 = m.end();
				key = m.group(1);
				//log.println("key= "+key);
				if(key==null || key.isEmpty()){
					// keyは見つからない
					log.println("key is empty ");
					i1 = 0;
				}else if(!MainFrame.idcheck(key)){
					// keyは動画IDではない
					log.println("key idcheck() ERROR "+key);
					i1 = 0;
					//errorSet.add(key);	//error登録
				}else{
					if(map.containsKey(key)){
						// key登録済み
						//String ttl = map.get(key);
						//log.println("key map.containsKey("+key+","+ttl+")");
					}else{
						// key登録
						map.put(key, "");
					}
					//log.println("key map.put( "+key+")");
					errorSet.remove(key);
				}
			}
			map.remove("1");	//delete sentinel
			errorSet.remove("1");
			text = text.substring(0,text.lastIndexOf("watch/1"));
			// うまく取れない場合に別の検索を行う
			if(errorSet.size()>map.size()){
				log.println("Retry title check: "+url);
				//別の抽出
				// <a href="watch/sm999999">タイトル</a>
				map.clear();
				regex = "<a +href=\"(http://www.nicovideo.jp/)?watch/((s[a-z]|n[a-z])?[1-9][0-9]*)\"[^>]*>([^<]+)</a>";
				p = Pattern.compile(regex);
				m = p.matcher(text);
				while(m.find()){
					key = m.group(2);
					val = m.group(4);
					log.println("key="+key+",val=\""+val+"\"");
					if(!map.containsKey(key) ||
						map.get(key).isEmpty())
					{
						map.put(key, val);
						log.println("map.put("+key+",\""+val+"\")");
					}
				}
				map.remove("1");	//delete sentinel
				errorSet.remove("1");
				if(map.size() >= errorSet.size()){
					errorSet.clear();
				}
			}
			sendtext("抽出成功 　"+map.size()+"個　"+ url);
			if(map.isEmpty()){
				//addError(url);
				sendtext("[E5]動画がありません、リトライ");
				//return "E5";
				String json_start = "first_data: ";
				int start = text.indexOf(json_start);
				if(start < 0){
					addError("E2",url);
					sendtext("JSON not found "+url);
					return "E2";	//JSON not found
				}
				start += json_start.length();
				String endStr = "jQuery(";
				int end = (text+endStr).indexOf(endStr, start);	// end of JSON
				text = (text+endStr).substring(start, end);
				start = text.indexOf("{");
				mylistID = Long.toString(new Date().getTime());
				text = text.substring(start).trim();
			}
			if(!errorSet.isEmpty()){
				// タイトル抽出失敗またはidcheckエラー
				StringBuffer sb = new StringBuffer();
				for(String es:errorSet){
					if(!watchInfo.isEmpty() && !es.contains(watchInfo.substring(1)))
						es += watchInfo;
					sb.append(es + "\n");
				}
				errorControl.setError(sb.substring(0));
			}
			// plist output
			for(String k: map.keySet()){
				val = map.get(k);
				plist.add(new String[]{k, val});
			}
			/*
			if(plist.isEmpty()){
				errorList.append(url+"\n");
				if(parent!=null)
					parent.setErrorUrl(errorList);
				sendtext("[E5]動画がありません");
				return "E5";
			}
		 	*/
		}
		//common
		try {
			if(mylistID.equals("0")){
				autolist = "autolist0";
			}else{
				autolist = "autolist";
				//Json解析
				file = new Path(file.getRelativePath().replace(".html", ".xml"));
				Path.unescapeStoreXml(file, text, url);		//xml is property key:json val:JSON
				Properties prop = new Properties();
					prop.loadFromXML(new FileInputStream(file));		//read JSON xml
				text = prop.getProperty("json", "0");
				file = new Path(file.getRelativePath().replace(".html", ".xml"));
				//
				if(MYLIST_DEBUG && parent!=null){
					resultText = HtmlView.markupHtml(text);
					final HtmlView hv2 = new HtmlView(parent, "マイリスト mson", "mson");
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							hv2.setText(resultText);
						}
					});
				}
				//
				log.println("get mylist/"+mylistID);
				log.println("mson: "+text.length());
				if(StopFlag.needStop()) {
					return "FF";
				}
				// parse mson
				sendtext("パース実行中");
				Mson mson = null;
				try{
					mson = Mson.parse(text);
				}catch(Exception e){
					log.printStackTrace(e);
				}
				if(mson==null){
					addError("E3",url);
					sendtext("[E3]パース失敗");
					return "E3";
				}
				sendtext("パース成功 "+mylistID);
				if(StopFlag.needStop()) {
					return "FF";
				}
				//rename to .txt
				file = new Path(file.getRelativePath().replace(".xml", ".txt"));
				mson.prettyPrint(new PrintStream(file));	//pretty print
				sendtext("リスト成功 "+mylistID);
				if(StopFlag.needStop()) {
					return "FF";
				}
				String[] keys = {"watch_id","title"};
				ArrayList<String[]> id_title_list = mson.getListString(keys);	// List of id & title
				for(String[] vals:id_title_list){
				//	log.println("Getting ["+ vals[0] + "]"+ vals[1]);
					plist.add(0, vals);
				}

				int sz = plist.size();
				sendtext("抽出成功 "+mylistID + "　"+sz+"個　"+ url);
				log.println("Success mylist/"+mylistID+" item:"+sz);
				if(sz == 0){
					addError("E4",url);
					sendtext("[E4]動画がありません。"+mylistID);
					//return "E4";
					keys[0] = "id";
					keys[1] = "title_short";
					id_title_list = mson.getListString(keys);	// List of id & title
					plist.clear();
					for(String[] vals:id_title_list){
					//	log.println("Getting ["+ vals[0] + "]"+ vals[1]);
						plist.add(0, vals);
					}

					sz = plist.size();
					sendtext("抽出成功 "+mylistID + "　"+sz+"個　"+ url);
					log.println("Success mylist/"+mylistID+" item:"+sz);
					if(sz == 0){
						addError("E4",url);
						sendtext("[E4]動画がありません。"+mylistID);
						return "E4";
					}
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

			}
			if(StopFlag.needStop()) {
				return "FF";
			}
			if(Setting.isSaveAutoList()){
				if(saveAutoList(autolist,plist)){
					//出力成功
					sendtext("[00]"+autolist+".bat 出力成功");
					return "00";
				}else{
					//出力失敗
					addError("E6",url);
					sendtext("[E6]"+autolist+".bat 出力失敗");
					return "E6";
				}
			} else {
				//結果データ
				for(String[] vals:plist){
					if(vals.length>1)
						ret.append(vals[0]+"\t"+vals[1]+"\t"+watchInfo+"\n");
					else
						ret.append(vals[0]+"\t"+autolist+"\t"+watchInfo+"\n");
				}
				return "00";
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			log.printStackTrace(e);
		} finally{

		}
		addError("EX",url);
		sendtext("[EX]例外発生?");
		return "EX";
	}

	private void addError(String code, String errorID) {
		if(!watchInfo.isEmpty() && !errorID.contains(watchInfo.substring(1)))
			errorID += watchInfo;
		errorControl.setError(code,errorID,gettext());
	}

	//終了時EDTで自動実行される
	public void done(){
		String result = null;
		try {
			result = get();
		} catch (InterruptedException | ExecutionException e) {
			log.printStackTrace(e);
		}
		StopFlag.finish();
		StopFlag.setButtonEnabled(false);
		if (result==null){
			log.println("done#get()==null バグ?");
		}else{
			log.println("done#get() "+result);
		}
		if(parent!=null){
			parent.myListGetterDone(ret, log);
		}
	}

	private boolean saveAutoList(String autoname, ArrayList<String[]> mylist) {
		File autobat = new File(".\\auto.bat");
		final String CMD_LINE = "%CMD% ";
		File autolist = new File(".\\"+autoname+".bat");
		if(!autobat.canRead()){
			log.println("auto.batがないので"+autoname+".batが出力できません:"+mylistID);
			sendtext("出力失敗 "+autoname+".bat:"+mylistID);
			return false;
		}
		//
		String user = Setting.getMailAddress();
		if(user!=null && user.isEmpty()) user = null;
		BufferedReader br = null;
		PrintWriter pw = null;
		String s;
		boolean flag2nd = false;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(autobat), "MS932"));
			pw = new PrintWriter(autolist, "MS932");
			pw.println(":自動生成 "+autoname+".bat for mylist/" + mylistID);
			pw.println(": produced by Saccubus" + MainFrame_AboutBox.rev + " " + new Date());
			pw.println(":――――――――――――――――――");
			while((s = br.readLine())!=null){
				if(s.startsWith("set MAILADDR=doremi@mahodo.co.jp") && user!=null){
					// メールアドレス置換
					pw.println("set MAILADDR="+user);
					continue;
				}
				if(s.startsWith("set PASSWORD=steeki_tabetai") && user!=null){
					// パスワード置換
					pw.println("set PASSWORD=!");
					continue;
				}
				if(!s.startsWith(CMD_LINE)){
					// %CMD%行が現れるまでコピー
					pw.println(s);
				}else{
					// マイリストの%CMD%出力
					pw.println(":――――――――――――――――――");
					pw.println(":set OPTION=過去ログ日時 他のオプション などを必要に応じ指定(readmeNew.txt参照)");
					pw.println("set OPTION=");
					if(watchInfo.isEmpty())
						watchInfo = "?" + WATCH_HARMFUL;
					else if(!watchInfo.contains(WATCH_HARMFUL)){
						watchInfo = "?" + WATCH_HARMFUL + watchInfo.replace('?', '&');
					}
					if(!s.contains("auto")){
						// マイリストの%CMD%出力(記述法1)
						pw.println(":保存変換しない行は削除してください");
						for(String[] ds: mylist){
							if(ds.length>1){
								pw.println(":タイトル " + ds[1]);
							}
							pw.println("%CMD% "+ ds[0] + watchInfo+" %OPTION% @PUP");
						}
					}else{
						// マイリストの%CMD%出力(記述法2)
						flag2nd = true;
						pw.print("%CMD% "+autoname + watchInfo+" %OPTION% @PUP");
					}
					break;
				}
			}
			while((s = br.readLine())!=null){
				if(!s.startsWith(CMD_LINE)){
					// %CMD%行以外を出力
					pw.println(s);
					continue;
				}
			}
			sendtext("出力成功 "+autoname+".bat:"+mylistID);
			if(!flag2nd)
				return true;
			else
				return saveListtxt(autoname,mylist);
		} catch (IOException e) {
			log.printStackTrace(e);
		} finally {
			try{
				br.close();
				pw.flush();
				pw.close();
			}catch(Exception ex){
				log.printStackTrace(ex);
			}
		}
		return false;
	}

	boolean saveListtxt(String autoname,List<String[]> dl){
		// 記述法2 autolist.txt出力
		File autolisttxt = new File(".\\"+autoname+".txt");
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(autolisttxt, "MS932");
			pw.println(":自動生成 "+autoname+".txt for mylist/" + mylistID);
			pw.println(": produced by Saccubus" + MainFrame_AboutBox.rev + " " + new Date());
			pw.println(":保存変換しない行は削除してください");
			for(String[] ds: dl){
				if(ds.length>1)
					pw.println(ds[0] + "\tタイトル :" + ds[1]);
				else
					pw.println(ds[0]);
			}
			sendtext("出力成功 "+autoname+".txt:"+mylistID);
			return true;
		} catch (IOException e) {
			log.printStackTrace(e);
		} finally {
			if (pw!=null) {
				try{
					pw.flush();
					pw.close();
				}catch(Exception ex){
					log.printStackTrace(ex);
				}
			}
		}
		return false;
	}
}
