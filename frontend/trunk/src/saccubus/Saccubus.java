package saccubus;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import saccubus.prompt.Prompt;

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
 * @author PSI
 * @version 1.0
 */
public class Saccubus {
	boolean packFrame = false;

	/**
	 * アプリケーションの構築と表示。
	 */
	public Saccubus() {
		// メインフレームの準備
		MainFrame frame = new MainFrame();
		// validate() はサイズを調整する
		// pack() は有効なサイズ情報をレイアウトなどから取得する
		if (packFrame) {
			frame.pack();
		} else {
			frame.validate();
		}

		// ウィンドウを中央に配置
		frame.setLocationByPlatform(true);

		// スプラッシュは隠す
		// メインフレーム表示
		frame.setVisible(true);
	}

	/**
	 * アプリケーションエントリポイント。
	 * 
	 * @param args
	 *            String[]
	 */
	public static void main(String[] args) {
		//引数が有る場合はCUIで起動
		if (args.length > 0) {
			Prompt prompt = new Prompt();
			prompt.main(args);
			return;
		}
		//引数が無い場合はGUIで起動
		SwingUtilities.invokeLater(new Runnable() {
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
