package saccubus;

import javax.swing.JLabel;

import saccubus.util.Stopwatch;

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
public class Prompt {
	public static void main(String[] args) {
		String mail = args[0];
		String pass = args[1];
		String tag = args[2];
		String time = args.length < 4 ? "" : args[3];
		ConvertingSetting setting = ConvertingSetting.loadSetting(mail, pass);
		Converter conv = new Converter(tag, time, setting, new JLabel(),
				new ConvertStopFlag(null, null, null, null), new JLabel());
		System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		System.out.println("Saccubus on CUI");
		System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		System.out.println("Mailaddr: " + mail);
		System.out.println("Password: hidden");
		System.out.println("VideoID: " + tag);
		System.out.println("WaybackTime: " + time);
		System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		conv.start();
		System.out.println("ElapsedTime: " + Stopwatch.formatElapsedTime());
		System.out.println("Finished.");
	}
}
