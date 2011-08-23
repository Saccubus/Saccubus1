package saccubus;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import psi.lib.swing.PopupRightClick;

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

	String version = "ver1.22r(2008/04/27)";

	public static final String rev = "1.22r3e2";
	private static final String modefied = "(2011/07/15)";

	String product =
		"�{�v���O������\n" +
		"  �u������΂� 1.22r3�v\n" +
		"  �u������΂�NicoBrowser�g��1.4.4�v\n" +
		"�������������̂ł��B  orz " + rev + " " + modefied + "\n"+
		"��L����і{�v���O�����̃I���W�i���͈ȉ��̒ʂ�ł��B\n\n"+
		"������΂�\n"+
		version + "\n\n"+
		"Copyright (C) 2008 Saccubus Developers Team\n"+
		"              2007-2008 PSI\n\n"+
		"�j�R�j�R����̓�����R�����g���ŕۑ�";

	String productHTML =
		"<html>�{�v���O������<br/>" +
		"  �u������΂� 1.22r3�v<br/>" +
		"  �u������΂�NicoBrowser�g��1.4.4�v<br/>" +
		"�������������̂ł��B  orz " + rev + " " + modefied + "<br/>"+
		"��L����і{�v���O�����̃I���W�i���͈ȉ��̒ʂ�ł��B<br/><br/>"+
		"������΂�<br/>"+
		version + "<br/><br/>"+
		"Copyright (C) 2008 Saccubus Developers Team<br/>"+
		"              2007-2008 PSI<br/><br/>"+
		"�j�R�j�R����̓�����R�����g���ŕۑ�</html>";

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
