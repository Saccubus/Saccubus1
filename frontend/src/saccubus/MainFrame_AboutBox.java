package saccubus;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import psi.lib.swing.PopupRightClick;

/**
 * <p>
 * タイトル: さきゅばす
 * </p>
 *
 * <p>
 * 説明: ニコニコ動画の動画をコメントつきで保存
 * </p>
 *
 * <p>
 * 著作権: Copyright (c) 2007 PSI
 * </p>
 *
 * <p>
 * 会社名:
 * </p>
 *
 * @author 未入力
 * @version 1.0
 */
public class MainFrame_AboutBox extends JDialog implements ActionListener {
	/**
	 *
	 */
	private static final long serialVersionUID = -4256413309312729840L;

	String version = "ver1.22r(2008/04/27)";

	public static final String rev = "1.22r3e2";
	private static final String modefied = "(2011/07/15)";

	String product =
		"本プログラムは\n" +
		"  「さきゅばす 1.22r3」\n" +
		"  「さきゅばすNicoBrowser拡張1.4.4」\n" +
		"を改造したものです。  orz " + rev + " " + modefied + "\n"+
		"上記および本プログラムのオリジナルは以下の通りです。\n\n"+
		"さきゅばす\n"+
		version + "\n\n"+
		"Copyright (C) 2008 Saccubus Developers Team\n"+
		"              2007-2008 PSI\n\n"+
		"ニコニコ動画の動画をコメントつきで保存";

	String productHTML =
		"<html>本プログラムは<br/>" +
		"  「さきゅばす 1.22r3」<br/>" +
		"  「さきゅばすNicoBrowser拡張1.4.4」<br/>" +
		"を改造したものです。  orz " + rev + " " + modefied + "<br/>"+
		"上記および本プログラムのオリジナルは以下の通りです。<br/><br/>"+
		"さきゅばす<br/>"+
		version + "<br/><br/>"+
		"Copyright (C) 2008 Saccubus Developers Team<br/>"+
		"              2007-2008 PSI<br/><br/>"+
		"ニコニコ動画の動画をコメントつきで保存</html>";

	JPanel panel1 = new JPanel();

	JPanel panel2 = new JPanel();

	JPanel insetsPanel1 = new JPanel();

	JPanel insetsPanel2 = new JPanel();

	JPanel insetsPanel3 = new JPanel();

	JButton button1 = new JButton();

	JLabel imageLabel = new JLabel();

	JTextArea product_field = new JTextArea(product);

	ImageIcon image1 = new ImageIcon();

	BorderLayout borderLayout1 = new BorderLayout();

	BorderLayout borderLayout2 = new BorderLayout();

	FlowLayout flowLayout1 = new FlowLayout();

	GridLayout gridLayout1 = new GridLayout();

	public MainFrame_AboutBox(Frame parent) {
		super(parent);
		try {
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			jbInit();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public MainFrame_AboutBox() {
		this(null);
	}

	/**
	 * コンポーネントの初期化。
	 *
	 * @throws java.lang.Exception
	 */
	private void jbInit() throws Exception {
		image1 = new ImageIcon(saccubus.MainFrame.class.getResource("icon.png"));
		imageLabel.setIcon(image1);
		setTitle("バージョン情報");
		panel1.setLayout(borderLayout1);
		panel2.setLayout(borderLayout2);
		insetsPanel1.setLayout(flowLayout1);
		insetsPanel2.setLayout(flowLayout1);
		insetsPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		gridLayout1.setRows(1);
		gridLayout1.setColumns(1);
		insetsPanel3.setLayout(gridLayout1);
		insetsPanel3.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		button1.setText("OK");
		button1.addActionListener(this);
		insetsPanel2.add(imageLabel, null);
		panel2.add(insetsPanel2, BorderLayout.WEST);
		getContentPane().add(panel1, null);
		product_field.setForeground(insetsPanel3.getForeground());
		product_field.setBackground(insetsPanel3.getBackground());
		product_field.addMouseListener(new PopupRightClick(product_field));
		product_field.setEditable(false);
		insetsPanel3.add(product_field, null);
		panel2.add(insetsPanel3, BorderLayout.CENTER);
		insetsPanel1.add(button1, null);
		panel1.add(insetsPanel1, BorderLayout.SOUTH);
		panel1.add(panel2, BorderLayout.NORTH);
		setResizable(true);
	}

	/**
	 * ボタンイベントでダイアログを閉じる
	 *
	 * @param actionEvent
	 *            ActionEvent
	 */
	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		if (actionEvent.getSource() == button1) {
			dispose();
		}
	}
}
