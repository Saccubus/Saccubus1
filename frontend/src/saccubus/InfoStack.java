package saccubus;

import javax.swing.JLabel;

public class InfoStack {

	private final JLabel label;
	private String stack;

	public InfoStack(JLabel jInfo){
		label = jInfo;
		stack = jInfo.getText();
	}

	public void pushText(String txt){
		stack = label.getText();
		label.setText(txt+" "+stack);
	}

	public void popText(){
		label.setText(stack);
	}
}
