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

	public ConvertStopFlag(final JButton button, final String stop_text,
			final String wait_text, final String done_text) {
		Flag = false;
		Finished = false;
		Button = button;
		if (button != null && stop_text != null) {
			button.setText(stop_text);
		}
		DoneText = done_text;
		WaitText = wait_text;
	}

	/**
	 * Call from prompt CUI without Display
	 */
	public ConvertStopFlag() {
		Flag = false;
		Finished = false;
		Button = null;
		WaitText = null;
		DoneText = null;
	}

	public void stop() {
		Flag = true;
		if (Button != null && WaitText != null) {
			Button.setText(WaitText);
			Button.setEnabled(false);
		}
	}

	public boolean needStop() {
		return Flag;
	}

	public boolean isConverted() {
		return Finished;
	}

	public void finished() {
		Finished = true;
		if (Button != null && DoneText != null) {
			Button.setText(DoneText);
			Button.setEnabled(true);
		}
	}
}
