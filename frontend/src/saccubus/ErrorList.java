package saccubus;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.swing.JLabel;

import saccubus.net.Path;

/**
 *
 * @author orz
 * データ StringBuffer, JLabel
 * メソッドappend, getString
 */
public class ErrorList {
	private StringBuffer errList;
	private JLabel st;

	public ErrorList(String initial) {
		st = new JLabel();
		errList = new StringBuffer(initial);
	}

	public ErrorList(JLabel label){
		st = label;
		errList = new StringBuffer();
	}

	public void setLabel(JLabel label){
		st = label;
	}

	private StringBuffer append(String s){
		return errList.append(s);
	}

	public String getString(){
		return errList.substring(0);
	}

	private void setText(){
		st.setText(errList.substring(0).replace("\n", "　"));
	}

	public void setError(String s){
		append(s);
		setText();
	}

	private void clearText(){
		st.setText(" ");
	}

	public void clear() {
		clearData();
		clearText();
	}

	private void clearData() {
		errList.delete(0, errList.length());
	}

	public void save() {
		Path errlistSave = new Path("エラー"+WayBackDate.formatNow()+".txt");
		String text = errList.substring(0);
		try {
			PrintWriter pw = new PrintWriter(errlistSave);
			pw.print(text);
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}
}
