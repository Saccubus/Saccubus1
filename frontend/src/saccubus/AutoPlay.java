package saccubus;

import java.awt.Color;
import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

public class AutoPlay {
	private JCheckBox checkbox;
	private HistoryDeque<File> playlist = new HistoryDeque<>(null);
	private VPlayer vplayer;
	private JLabel label;
	private JLabel status;

	AutoPlay(JCheckBox c, JLabel l, VPlayer p, JLabel s){
		checkbox = c;
		label = l;
		vplayer = p;
		status = s;
	}
	AutoPlay(boolean b) {
		this(new JCheckBox(), new JLabel(), null, null);
		checkbox.setSelected(b);
	}

	void setSelected(boolean b){
		checkbox.setSelected(b);
	}
	void setLabel(JLabel l){
		label = l;
	}
	void setStatus(JLabel s){
		status = s;
	}
	void setCheckBox(JCheckBox c) {
		checkbox = c;
		checkbox.setSelected(c.isSelected());
	}

	boolean isAutoPlay(){
		return checkbox.isSelected();
	}

	void next() {
		setPlayList(playlist.next());
	}

	void back() {
		setPlayList(playlist.back());
	}

	private File getNow() {
		return playlist.getNow();
	}

	public void offer(File file) {
		playlist.offer(file);
	}

	// •ÏŠ·“®‰æÄ¶
	private void setPlayList(File video) {
		if(video!=null){
			label.setText(video.getName());
			label.setForeground(Color.blue);
		}else{
			label.setText("");
		}
		label.repaint();
	}

	void playVideo() {
		setPlayList(getNow());
		playVideo(getNow());
	}

	void playAuto() {
		if(isAutoPlay()){
			playVideo();
			playlist.next();
		}
	}

	private void playVideo(File video) {
		if(video==null){
			video = playlist.getNow();
		}
		if(video==null){
			sendtext("•ÏŠ·Œã‚Ì“®‰æ‚ª‚ ‚è‚Ü‚¹‚ñ");
			return;
		}
		if(!video.canRead()){
			sendtext("•ÏŠ·Œã‚Ì“®‰æ‚ª“Ç‚ß‚Ü‚¹‚ñF" + video.getName());
			return;
		}
		if(vplayer!=null && vplayer.isAlive()){
			vplayer.interrupt();
		}
		vplayer = new VPlayer(video, status);
		vplayer.start();
		return;
	}

	private void sendtext(String text) {
		status.setText(text);
	}
}
