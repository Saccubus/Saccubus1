/**
 * �E�N���b�N���j���[�쐬�p�B
 * 2005�N���[�B�����Ԃ�O�̂𗬗p���Ă�񂾂Ȃ��B
 */
package saccubus.util;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;

/**
 * <p>
 * �^�C�g��: �|�P�����Z�[�u�f�[�^�G�f�B�^ for GBA
 * </p>
 * 
 * <p>
 * ����:
 * </p>
 * 
 * <p>
 * ���쌠: Copyright (c) 2005 PSI
 * </p>
 * 
 * <p>
 * ��Ж�: �Ձi�v�T�C�j�̋����֐S���
 * </p>
 * 
 * @author PSI
 * @version 1.0
 */
public class PopupRightClick implements MouseListener, ActionListener {
	JPopupMenu popup;

	JMenuItem CutMenu;

	JMenuItem CopyMenu;

	JMenuItem PasteMenu;

	JTextComponent Owner;

	public static final String Actin_Cut = "CO";

	public static final String Actin_Copy = "CU";

	public static final String Actin_Paste = "P";

	private boolean pressed = false;

	public PopupRightClick(JTextComponent owner) {
		this.Owner = owner;
		popup = new JPopupMenu("���j���[");
		CopyMenu = new JMenuItem("�R�s�[(CTRL + C)");
		CopyMenu.setActionCommand(Actin_Copy);
		CopyMenu.addActionListener(this);
		popup.add(CopyMenu);

		CutMenu = new JMenuItem("�؂���(CTRL + X)");
		CutMenu.setActionCommand(Actin_Cut);
		CutMenu.addActionListener(this);
		popup.add(CutMenu);

		PasteMenu = new JMenuItem("�\��t��(CTRL + V)");
		PasteMenu.setActionCommand(Actin_Paste);
		PasteMenu.addActionListener(this);
		popup.add(PasteMenu);
	}

	/**
	 * Invoked when the mouse button has been clicked (pressed and released) on
	 * a component.
	 * 
	 * @param e
	 *            MouseEvent
	 * @todo ���� java.awt.event.MouseListener ���\�b�h������
	 */
	public void mouseClicked(MouseEvent e) {
	}

	/**
	 * Invoked when the mouse enters a component.
	 * 
	 * @param e
	 *            MouseEvent
	 * @todo ���� java.awt.event.MouseListener ���\�b�h������
	 */
	public void mouseEntered(MouseEvent e) {
	}

	/**
	 * Invoked when the mouse exits a component.
	 * 
	 * @param e
	 *            MouseEvent
	 * @todo ���� java.awt.event.MouseListener ���\�b�h������
	 */
	public void mouseExited(MouseEvent e) {
		pressed = false;
	}

	/**
	 * Invoked when a mouse button has been pressed on a component.
	 * 
	 * @param e
	 *            MouseEvent
	 * @todo ���� java.awt.event.MouseListener ���\�b�h������
	 */
	public void mousePressed(MouseEvent e) {
		pressed = true;
	}

	/**
	 * Invoked when a mouse button has been released on a component.
	 * 
	 * @param e
	 *            MouseEvent
	 * @todo ���� java.awt.event.MouseListener ���\�b�h������
	 */
	public void mouseReleased(MouseEvent e) {
		// �E�N���b�N�̎������̘b
		if (pressed && SwingUtilities.isRightMouseButton(e)) {
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
		pressed = false;
	}

	/**
	 * ��������PopupMenu
	 * 
	 * @param e
	 *            ActionEvent
	 * @todo ���� java.awt.event.ActionListener ���\�b�h������
	 */
	public void actionPerformed(ActionEvent e) {
		String ActionCommand = e.getActionCommand();
		if (ActionCommand.equals(Actin_Cut)) { // �J�b�g
			Owner.cut();
		} else if (ActionCommand.equals(Actin_Copy)) { // �R�s�[
			Owner.copy();
		} else if (ActionCommand.equals(Actin_Paste)) { // �\��t��
			Owner.paste();
		}
	}
}
