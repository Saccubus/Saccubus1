package saccubus;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import saccubus.net.Path;

/**
 *
 * @author orz
 * データ StringBuffer, JLabel
 * メソッドappend, getString
 */
public class ErrorControl {
	private StringBuffer errList;
	private StringBuffer errlog;
	private StringBuffer ecoList;
	private StringBuffer ecolog;
	private JLabel st;
	private JLabel st2;

	public ErrorControl(String initial) {
		st = new JLabel();
		st2 = new JLabel();
		errList = new StringBuffer(initial);
		errlog = new StringBuffer(initial);
		ecoList = new StringBuffer(initial);
		ecolog = new StringBuffer(initial);
	}

	public ErrorControl(JLabel label, JLabel label2){
		st = label;
		st2 = label2;
		errList = new StringBuffer();
		errlog = new StringBuffer();
		ecoList = new StringBuffer();
		ecolog = new StringBuffer();
	}

	public void setLabel(JLabel label, JLabel label2){
		st = label;
		st2 = label2;
	}
	private void sendtext(final String s, final String s2){
		if(SwingUtilities.isEventDispatchThread()){
			st.setText(s);
			st2.setText(s2);
		}else
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				st.setText(s);
				st2.setText(s2);
			}
		});
	}

	public void setError(String code, String mes, String logmsg){
		mes = mes.replace("\n", "");
		errList.append(mes+"\t"+code+"\n");
		errlog.append(mes+"\t"+code+" "+logmsg+"\n");
		setText();
	}
	public void setEco(String code, String mes, String logmsg){
		mes = mes.replace("\n", "");
		ecoList.append(mes+"\t"+code+"\n");
		ecolog.append(mes+"\t"+code+" "+logmsg+"\n");
		setText();
	}
	public String getString(){
		return errList.substring(0);
	}
	public String getEcoString() {
		return ecoList.substring(0);
	}

	private synchronized void setText(){
		final String text = errList.toString().replace("\n", "　").replace("\t", "_");
		final String text2 = ecoList.toString().replace("\n", "　").replace("\t", "_");
		sendtext(text, text2);
	}

	public void setEco(String s){
		ecoList.append(s);
		ecolog.append(s);
		setText();
	}

	public void setError(String s){
		errList.append(s);
		errlog.append(s);
		setText();
	}

	public void clear() {
		clearData();
		clearEco();
	}

	public void clearData() {
		errList.delete(0, errList.length());
		errlog.delete(0, errlog.length());
		setText();
	}
	public void clearEco() {
		ecoList.delete(0, ecoList.length());
		ecolog.delete(0, ecolog.length());
		setText();
	}

	public boolean save() {
		Path errlistSave = new Path("エラー"+WayBackDate.formatNow()+".txt");
		Path ecolistSave = new Path("エコノミー"+WayBackDate.formatNow()+".txt");
		String text = null;
		PrintWriter pw = null;
		StringBuffer sb = null;
		try {
			text = errlog.substring(0);
			sb = new StringBuffer();
			if(text!=null && !text.isEmpty()){
				String[] tt2 = text.split("\n");
				pw = new PrintWriter(errlistSave);
				for(String t:tt2){
					if(t.contains("42 エコノミー")){
						sb.append(t+"\n");
					}else{
						pw.print(t+"\n");
					}
				}
				pw.flush();
				pw.close();
			}
		
			text = sb.toString() + ecolog.substring(0);
			if(text!=null && !text.isEmpty()){
				pw = new PrintWriter(ecolistSave);
				pw.print(text);
				pw.flush();
				pw.close();
			}
			return true;
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return false;
		}
	}

}
