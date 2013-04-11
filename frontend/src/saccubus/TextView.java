package saccubus;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import psi.lib.swing.PopupRightClick;

/**
 * <P>さきゅばす</P>
 * @author orz
 * @version 1.39
 */
public class TextView extends JDialog implements ActionListener {

	private final Frame parent;
	public JTextArea textArea1;
	private static final double WIDTH_RATE = 1.2;
	private static final double HEIGHT_RATE = 0.9;

//	public MainFrame_TextView(Frame owner) {
//		this(owner,"テキスト情報");
//	}

	public TextView(Frame owner, String title) {
		this(owner, title, false);
	}

//	public MainFrame_TextView(Frame owner, boolean modal) {
//		this(owner,"テキスト情報", modal);
//	}

	public TextView(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		parent = owner;
		init();
	}

	private void init(){
		try {
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Font f = getFont();
		setFont(new Font("Monospaced", f.getStyle(), f.getSize()));
		textArea1 = new JTextArea();
		setLayout(new BorderLayout());
		textArea1.setLineWrap(true);
		textArea1.setOpaque(false);
		textArea1.addMouseListener(new PopupRightClick(textArea1));
		add(new JScrollPane(textArea1), BorderLayout.CENTER);
		pack();
		Dimension dim = parent.getSize();
		dim.width = (int)(WIDTH_RATE * dim.width);
		dim.height = (int)(HEIGHT_RATE * dim.height);
		setSize(dim);
		setLocationRelativeTo(parent);
		setVisible(true);
		setResizable(true);
	}

	public JTextArea getTextArea() {
		return textArea1;
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		dispose();
	}

}
