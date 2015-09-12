/**
 *
 */
package saccubus;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;

import javafx.event.ActionEvent;

import javax.swing.JDialog;
import javax.swing.JTextArea;

/**
 * @author orz
 *
 */
public class MsgBox extends JDialog {

	private JTextArea text;

	/**
	 * @param owner
	 * @param title
	 * @param width
	 * @param height
	 * @param msg
	 */
	public MsgBox(Frame owner, String title, int width, int height, String msg) {
		super(owner, title);
		init();
		setLocationRelativeTo(owner);
		setSize(width, height);
		text = new JTextArea();
		text.setOpaque(true);
		getContentPane().add(text);
		text.setText(msg);
		//setVisible(true);
	}

	private void init() {
		try {
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Font f = getFont();
		setFont(new Font("Monospaced", f.getStyle(), f.getSize()));
		setLayout(new BorderLayout());
		setResizable(true);
	}

	public JTextArea getTextArea() {
		return text;
	}

	public void setText(String msg){
		text.setText(msg);
	}

	public void actionPerformed(ActionEvent actionEvent) {
		dispose();
	}

}
