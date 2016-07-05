package saccubus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import psi.lib.swing.PopupRightClick;

/**
 * <P>さきゅばす</P>
 * @author orz
 * @version 1.39
 */
public class TextView extends JDialog implements ActionListener {

	private final MainFrame parent;
	public JTextArea textArea1;
	final JTextField inputFeild = new JTextField();
	private static final double WIDTH_RATE = 1.2;
	private static final double HEIGHT_RATE = 0.9;

	public TextView(MainFrame owner, String title) {
		this(owner, title, false);
	}

	public TextView(MainFrame owner, String title, boolean modal) {
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
		JPanel inputPanel = new JPanel();
		inputPanel.setFont(new Font(f.getFontName(), f.getStyle(), f.getSize()+1));
		inputPanel.setLayout(new BorderLayout());
		inputPanel.setBackground(Color.cyan);
		inputPanel.setBorder(BorderFactory.createEtchedBorder());
		JLabel inputTitle = new JLabel("実行したいヘルプ項目を選択しコピー(Ctrl+C)し下にペースト(Ctrl+V)して実行");
		inputTitle.setHorizontalAlignment(JLabel.CENTER);
		inputTitle.setForeground(Color.blue);
		inputPanel.add(inputTitle, BorderLayout.NORTH);
		inputPanel.add(new JLabel(" コマンド実行>>　ffmpeg "), BorderLayout.WEST);
		inputFeild.addMouseListener(new PopupRightClick(inputFeild));
		inputPanel.add(inputFeild, BorderLayout.CENTER);
		JButton submitButton = new JButton();
		submitButton.setText("実行");
		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				help_actionPerformed(inputFeild.getText());
			}
		});
		inputPanel.add(submitButton, BorderLayout.EAST);
		add(inputPanel, BorderLayout.SOUTH);
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

	/* FFmpeg help 表示 */
	public void help_actionPerformed(String s){
		try{
			textArea1.setText(null);
			s = s.replace("ffmpeg", "").trim();
			if(!s.isEmpty() && s.charAt(0)!='-'){
				s = "-".concat(s);
			}
			ArrayList<String> list = parent.execFFmpeg(s);
			for(String line:list){
				textArea1.append(line);
			}
			textArea1.setCaretPosition(0);
		} catch(NullPointerException ex){
			parent.sendtext("(´∀｀)＜ぬるぽ\nガッ\n");
			ex.printStackTrace();
		} catch (FileNotFoundException ex) {
			textArea1.setText(ex.getMessage());
			ex.printStackTrace();
		}
	}
}
