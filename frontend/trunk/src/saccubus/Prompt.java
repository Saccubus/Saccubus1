package saccubus;

import javax.swing.JLabel;

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
public class Prompt {
	public static void main(String[] args) {
		if (args.length < 3){
			System.out.println("Error. Mailaddr,Password,VideoID must be specified.");
			return;
		}
		String mail = args[0];
		String pass = args[1];
		String tag = args[2];
		String time = args.length < 4 ? "" : args[3];
		ConvertingSetting setting = ConvertingSetting.loadSetting(mail, pass);
		JLabel status = new JLabel();
		JLabel info = new JLabel();
		JLabel watch = new JLabel();
		Converter conv = new Converter(tag, time, setting, status, new ConvertStopFlag()
										, info, watch);		// these two params are extended
		System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		System.out.println("Saccubus on CUI");
		System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		System.out.println("Mailaddr: " + mail);
		System.out.println("Password: hidden");
		System.out.println("VideoID: " + tag);
		System.out.println("WaybackTime: " + time);
		System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		System.out.println("Version " + MainFrame_AboutBox.rev );
		//do {
			conv.start();
			try {
				conv.join();
		//		break;
			} catch (InterruptedException e) {
				//e.printStackTrace();
		//		continue;
			}
		//} while(true);
		// System.out.println("LastStatus: " + status.getText());
		// System.out.println("VideoInfo: " + info.getText());
		// System.out.println("ElapsedTime: " + watch.getText());
		System.out.println("Finished.");
	}
}
