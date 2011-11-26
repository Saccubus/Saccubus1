package saccubus;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

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
public class Saccubus {
	boolean packFrame = false;

	/**
	 * �A�v���P�[�V�����̍\�z�ƕ\���B
	 */
	public Saccubus() {
		// ���C���t���[���̏���
		MainFrame frame = new MainFrame();
		// validate() �̓T�C�Y�𒲐�����
		// pack() �͗L���ȃT�C�Y�������C�A�E�g�Ȃǂ���擾����
		if (packFrame) {
			frame.pack();
		} else {
			frame.validate();
		}

		// �E�B���h�E�𒆉��ɔz�u
		frame.setLocationByPlatform(true);

		// �X�v���b�V���͉B��
		// ���C���t���[���\��
		frame.setVisible(true);
	}

	/**
	 * �A�v���P�[�V�����G���g���|�C���g�B
	 * 
	 * @param args
	 *            String[]
	 */
	public static void main(String[] args) {
		if (args.length > 0) {
			Prompt.main(args);
			return;
		}
		System.out.println("Version " + MainFrame_AboutBox.rev);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
				} catch (Exception exception) {
					exception.printStackTrace();
				}

				new Saccubus();
			}
		});
	}
}
