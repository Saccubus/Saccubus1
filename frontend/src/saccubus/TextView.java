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
import javax.swing.SwingUtilities;

import psi.lib.swing.PopupRightClick;

/**
 * <P>������΂�</P>
 * @author orz
 * @version 1.39
 */
public class TextView extends JDialog implements ActionListener {

	private final MainFrame parent;
	public JTextArea textArea1;
	final JTextField inputField = new JTextField();
	private static final double WIDTH_RATE = 1.2;
	private static final double HEIGHT_RATE = 0.9;

	public TextView(MainFrame owner, String title) {
		this(owner, title, false);
	}

	public TextView(MainFrame owner, String title, boolean modal) {
		this(owner, title, modal, true);
	}

	public TextView(MainFrame owner, String title, boolean modal, boolean visible) {
		super(owner, title, modal);
		parent = owner;
		init(visible);
	}

	private void init(boolean visible){
		try {
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Font f = getFont();
		if(f!=null)
			setFont(new Font("Monospaced", f.getStyle(), f.getSize()));
		textArea1 = new JTextArea();
		setLayout(new BorderLayout());
		textArea1.setLineWrap(true);
		textArea1.setOpaque(false);
		textArea1.addMouseListener(new PopupRightClick(textArea1));
		add(new JScrollPane(textArea1), BorderLayout.CENTER);
		JPanel inputPanel = new JPanel();
		if(f!=null)
			inputPanel.setFont(new Font(f.getFontName(), f.getStyle(), f.getSize()+1));
		inputPanel.setLayout(new BorderLayout());
		inputPanel.setBackground(Color.cyan);
		inputPanel.setBorder(BorderFactory.createEtchedBorder());
		inputPanel.add(new JLabel(" FFmpeg���s>>�@ffmpeg "), BorderLayout.WEST);
		inputField.setToolTipText("���s�������R�}���h���^�C�v�i��: ffmpeg -h�@�Ȃ� -h�Ɠ���)");
		inputField.addMouseListener(new PopupRightClick(inputField));
		inputPanel.add(inputField, BorderLayout.CENTER);
		JButton submitButton = new JButton();
		submitButton.setText("���s");
		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				help_actionPerformed(inputField.getText(), false);
			}
		});
		inputPanel.add(submitButton, BorderLayout.EAST);
		add(inputPanel, BorderLayout.SOUTH);
		pack();
		if(parent!=null){
			Dimension dim = parent.getSize();
			dim.width = (int)(WIDTH_RATE * dim.width);
			dim.height = (int)(HEIGHT_RATE * dim.height);
			setSize(dim);
			setLocationRelativeTo(parent);
		}
		super.setVisible(visible);
		setResizable(true);
	}

	public void setVisible(boolean visible){
		super.setVisible(visible);
	}

	public JTextArea getTextArea() {
		return textArea1;
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		dispose();
	}

	public void help_actionPerformed(String s, boolean backtotop){
		try{
			textArea1.setText(null);
			s = s.replace("ffmpeg ", "").trim();
			if(parent!=null){
				ArrayList<String> list = parent.execFFmpeg(s);
				for(String line:list){
					textArea1.append(line);
				}
				if(backtotop)
					textArea1.setCaretPosition(0);
				MainFrame.log.println(textArea1.getText());
			}
			else {
				inputField.setText("����View�ł͎g���܂���");
				MainFrame.log.println(inputField.getText());
			}
		} catch(NullPointerException ex){
			if(parent!=null)
				parent.sendtext("(�L�́M)���ʂ��\n�K�b\n");
			else
				inputField.setText("����View�ł͎g���܂���");
			ex.printStackTrace();
		} catch (FileNotFoundException ex) {
			textArea1.setText(ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void print(final String s){
		synchronized(textArea1){
			if(!isVisible() || SwingUtilities.isEventDispatchThread()){
				textArea1.append(s);
			}
			else
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						@Override
						public void run() {
							textArea1.append(s);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		repaint();
	}

	public void clearlog() {
		synchronized(textArea1){
			textArea1.setText(null);
		}
	}
}
