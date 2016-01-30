package saccubus;

import javax.swing.JButton;

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
public class ConvertStopFlag {
	private boolean Flag = false;

	private boolean Finished = false;

	private final JButton Button;

	private final String WaitText;

	private final String DoneText;

	private final String StopText;

	public ConvertStopFlag(final JButton button, final String stop_text,
			final String wait_text, final String done_text) {
		Button = button;
		StopText = stop_text;
		WaitText = wait_text;
		DoneText = done_text;
		init();
	}
	private void init(){
		Flag = false;
		Finished = false;
		if (getButton() != null && StopText != null) {
			getButton().setText(StopText);
			getButton().setEnabled(true);
		}
	}
	//use default DoButton string
	public ConvertStopFlag(final JButton button){
		this(button,
			MainFrame.DoButtonStopString,
			MainFrame.DoButtonWaitString,
			MainFrame.DoButtonDefString
		);
	}
	/**
	 * Call from prompt CUI without Display
	 */
	public ConvertStopFlag() {
		this(null,null,null,null);
	}

	public void stop() {
		Flag = true;
		if (getButton() != null && WaitText != null) {
			getButton().setText(WaitText);
			getButton().setEnabled(false);
		}
	}

	public boolean needStop() {
		return Flag;
	}

	public boolean isFinished() {
		return Finished;
	}

	public void finish() {
		// this may be called from NON EDT asynchronus method,
		// so may cause a problem, then should use invokeLater
		// or just Finish without Button
		Finished = true;
		if (getButton() != null && DoneText != null) {
			getButton().setText(DoneText);
			getButton().setEnabled(true);
		}
	}
	public void finishWithoutButton(){
		Finished = true;
	}
	public JButton getButton() {
		return Button;
	}
	public void setButtonEnabled(boolean b) {
		getButton().setEnabled(b);
	}

}
