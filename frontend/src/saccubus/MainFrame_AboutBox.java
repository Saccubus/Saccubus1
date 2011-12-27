package saccubus;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;

//import psi.lib.swing.PopupRightClick;

/**
 * <p>
 * �^�C�g��: ������΂�
 * </p>
 *
 * <p>
 * ����: �j�R�j�R����̓�����R�����g���ŕۑ�
 * </p>
 *
 * <p>
 * ���쌠: Copyright (c) 2007 PSI
 * </p>
 *
 * <p>
 * ��Ж�:
 * </p>
 *
 * @author ������
 * @version 1.0
 */
public class MainFrame_AboutBox extends JDialog implements ActionListener {
	/**
	 *
	 */
	private static final long serialVersionUID = -4256413309312729840L;

//	String version = "ver1.22r(2008/04/27)";

	public static final String rev = "1.30.dev2";
	private static final String modefied = " (2011/12/27)";

	String productHTML =
		"<html><p>�{�v���O������<br/>" +
	//	"  <a href=\'http://www.ne.jp/asahi/mochiyama/my/file/Saccubus-1.22r3.zip\'>" +
	//	"�u������΂� 1.22r3�v</a><br/>" +
	//	"  <a href=\'http://sourceforge.jp/projects/coroid/downloads/48371/saccubus_nibr1.4.4.zip/\'>" +
	//	"������΂�NicoBrowser�g��1.4.4</a><br/>" +
	//	"�������������̂ł��B<br/>" +
		" <a href=\'http://sourceforge.jp/projects/coroid/downloads/52981/inqubus1.7.2.zip/\'>"	+
		"���񂫂�΂�1.7.2</a><br/>���Q�l�ɂ��Ă��܂��B</p>" +
	//	" orz " + rev + " " + modefied + "<br/>"+
	//	"�{�v���O�����̃I���W�i���͈ȉ��̒ʂ�ł��B<br/><br/>"+
		"<p><a href=\'http://sourceforge.jp/projects/saccubus/\'>" +
		"������΂��@"+ rev + modefied + "</a><br/><br/>"+
		"<table border=0>" +
		"<tr><td>Copyright (C) <td>2008 Saccubus Developers Team"+
		"<tr><td><td>2007-2008 PSI"+
		"<tr><td><td>2011-2011 orz"+
		"</table><br/>" +
		"�j�R�j�R����̓�����R�����g���ŕۑ�</p>" +
		"<p>�֘A�����N<br/>" +
		"<a href=\'http://sourceforge.jp/projects/saccubus/\'>������΂� project</a><br/>" +
		"<a href=\'http://sourceforge.jp/projects/coroid/\'>coroid project�i���񂫂�΂����܂ށj</a><br/>" +
		"<br/><a href=\'http://anago.2ch.net/test/read.cgi/software/1310301611/\'>2ch������΂��X��</a>" +
		"</p></html>";

	JPanel panel1 = new JPanel();

	JPanel panel2 = new JPanel();

	JPanel insetsPanel1 = new JPanel();

	JPanel insetsPanel2 = new JPanel();

	JPanel insetsPanel3 = new JPanel();

	JEditorPane editorPane;

	JButton button1 = new JButton();

	JLabel imageLabel = new JLabel();
/*
	JTextArea product_field = new JTextArea(product);
*/
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
	 * �R���|�[�l���g�̏������B
	 *
	 * @throws java.lang.Exception
	 */
	private void jbInit() throws Exception {
		image1 = new ImageIcon(saccubus.MainFrame.class.getResource("icon.png"));
		imageLabel.setIcon(image1);
		setTitle("�o�[�W�������");
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
/*
		product_field.setForeground(insetsPanel3.getForeground());
		product_field.setBackground(insetsPanel3.getBackground());
		product_field.addMouseListener(new PopupRightClick(product_field));
		product_field.setEditable(false);
*/
		editorPane = new JEditorPane("text/html", productHTML);
		editorPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		editorPane.setEditable(false);
		editorPane.setOpaque(false);
		editorPane.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == EventType.ACTIVATED){
					URL url = e.getURL();
					Desktop desktop = Desktop.getDesktop();
					try {
						desktop.browse(url.toURI());
					} catch (IOException ex) {
						ex.printStackTrace();
					} catch (URISyntaxException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		insetsPanel3.add(editorPane, null);
		panel2.add(insetsPanel3, BorderLayout.CENTER);
		insetsPanel1.add(button1, null);
		panel1.add(insetsPanel1, BorderLayout.SOUTH);
		panel1.add(panel2, BorderLayout.NORTH);
		setResizable(true);
	}

	/**
	 * �{�^���C�x���g�Ń_�C�A���O�����
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
