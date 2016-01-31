package saccubus;

import javax.swing.JButton;

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
 *
 */
public class ConvertStopFlag {
	private boolean Flag = false;

	private boolean Finished = false;

	private boolean Pending = false;

	private boolean PendingMode = false;

	private final JButton Button;

	private final String WaitText;

	private final String DoneText;

	private final String StopText;

	private final String PendingText;

/*
 * ��Ԃ́@�ϊ��҂�,�ϊ��ҋ@(disable),��~�҂�,�I���ҋ@(disable),�I�� �̂T��
 * method go �ϊ��҂���boolean=readyToGo���ϊ��ҋ@
 * method started �ϊ��ҋ@����~�҂�
 * method stop ��~�҂���boolean=needstop���I���ҋ@
 * method finish �I���҂����I�� or �ĕϊ��҂�
 */

	public ConvertStopFlag(
		final JButton button,
		final String stop_text,
		final String wait_text,
		final String done_text,
		final String pending_text,
		final boolean pending_mode){
		Button = button;
		StopText = stop_text;
		WaitText = wait_text;
		DoneText = done_text;
		PendingText = pending_text;
		PendingMode = pending_mode;
		init();
	}

	private void init(){
		Flag = false;
		Finished = false;
		Pending = PendingMode;
		if(Pending){
			if (Button != null && PendingText != null) {
				Button.setText(PendingText);
				Button.setEnabled(true);
			}
		}else{
			if (Button != null && StopText != null) {
				Button.setText(StopText);
				Button.setEnabled(true);
			}
		}
	}
	//use default DoButton string
	public ConvertStopFlag(final JButton button){
		this(button,
			MainFrame.DoButtonStopString,
			MainFrame.DoButtonWaitString,
			MainFrame.DoButtonDoneString,
			MainFrame.DoButtonDefString,
			false
		);
	}

	public void go(){
		if(Pending){
			Flag = false;
			Pending = false;
			if (Button!=null && WaitText != null) {
				Button.setText(WaitText);
				Button.setEnabled(false);
			}
		}
	}

	public boolean isReady(){
		return !Pending;
	}

	public boolean isPending(){
		return Pending;
	}

	public boolean isNotStarted(){
		return Finished && Pending;
	}

	public void start(){
		// this may be called from NON EDT asynchronus method,
		// so may cause a problem, then should use invokeLater
		// or just Start without Button
		Finished = false;
		if (Button != null && StopText != null) {
			Button.setText(StopText);
			Button.setEnabled(true);
		}

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

	public boolean isFinished() {
		return Finished;
	}

	public void finish() {
		// this may be called from NON EDT asynchronus method,
		// so may cause a problem, then should use invokeLater
		// or just Finish without Button
		Finished = true;
		Pending = true;
		if (Button!= null && DoneText != null) {
			Button.setText(DoneText);
			Button.setEnabled(false);
		}
	}
	public void finishWithoutButton(){
		Finished = true;
	}
	public JButton getButton() {
		return Button;
	}
	public void setButtonEnabled(boolean b) {
		Button.setEnabled(b);
	}

	public boolean started() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return false;
	}

}
