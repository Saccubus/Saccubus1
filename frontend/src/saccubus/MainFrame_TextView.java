package saccubus;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import psi.lib.swing.PopupRightClick;

/**
 * <P>さきゅばす</P>
 * @author orz
 * @version 1.39
 */
public class MainFrame_TextView extends JDialog implements ActionListener {

	private final Frame parent;
	private JPanel panel1;
	private JButton button1;
	public JTextArea textArea1;
	private static final Insets INSETS_0_5_0_5 = new Insets(0, 5, 0, 5);

	public MainFrame_TextView(Frame owner) {
		this(owner,"テキスト情報");
	}

	public MainFrame_TextView(Frame owner, String title) {
		this(owner, title, false);
	}

	public MainFrame_TextView(Frame owner, boolean modal) {
		this(owner,"テキスト情報", modal);
	}

	public MainFrame_TextView(Frame owner, double height_rate){
		this(owner, "テキスト情報", height_rate);
	}

	public MainFrame_TextView(Frame owner, String title, boolean modal) {
		this(owner, title, modal, 0.4);
	}

	public MainFrame_TextView(Frame owner, boolean modal, double height_rate) {
		this(owner,"テキスト情報", modal, height_rate);
	}

	public MainFrame_TextView(Frame owner, String title, double height_rate){
		this(owner, title, false, height_rate);
	}

	public MainFrame_TextView(Frame owner, String title, boolean modal, double height_rate) {
		super(owner, title, modal);
		parent = owner;
		init(height_rate);
	}

	private void init(double height_rate){
		try {
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Font f = getFont();
		setFont(new Font("Monospaced", f.getStyle(), f.getSize()));
		panel1 = new JPanel();
		button1 = new JButton();
		textArea1 = new JTextArea();
		setLayout(new BorderLayout());
		panel1.setLayout(new GridBagLayout());
		button1.setText("OK");
		button1.addActionListener(this);
		textArea1.setLineWrap(true);
	//	textArea1.setForeground(Color.blue);
		textArea1.setOpaque(false);
		textArea1.addMouseListener(new PopupRightClick(textArea1));
		GridBagConstraints grid1 = new GridBagConstraints();
		grid1.gridx = 0;
		grid1.gridy = 0;
		grid1.weightx = 1.0;
		grid1.weighty = 1.0;
		grid1.anchor = GridBagConstraints.CENTER;
		grid1.fill = GridBagConstraints.BOTH;
		grid1.insets = INSETS_0_5_0_5;
		panel1.add(new JScrollPane(textArea1), grid1);
		GridBagConstraints grid2 = new GridBagConstraints();
		grid2.gridx = 0;
		grid2.gridy = 1;
		grid2.weightx = 1.0;
		grid2.weighty = 0.0;
		grid2.anchor = GridBagConstraints.CENTER;
		grid2.fill = GridBagConstraints.NONE;
		grid2.insets = INSETS_0_5_0_5;
		panel1.add(button1, grid2);
		add(panel1);
		pack();
		Dimension dim = parent.getSize();
		dim.width = (int)(1.5 * dim.width);
		dim.height = (int)(height_rate * dim.height);
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
