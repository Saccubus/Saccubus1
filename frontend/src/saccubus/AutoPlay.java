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
		playlist.next();
		setPlayList();
	}

	void back() {
		playlist.back();
		setPlayList();
	}

	private File getNow() {
		return playlist.getNow();
	}

	public void offer(File file) {
		playlist.offer(file);
		setPlayList();
	}

	// ïœä∑ìÆâÊçƒê∂
	private void setPlayList() {
		File video = playlist.getNow();
		if(video!=null){
			label.setText(video.getName());
			label.setForeground(Color.blue);
		}else{
			label.setText("");
		}
		label.repaint();
	}

	void playVideo() {
		setPlayList();
		playVideo(getNow());
	}

	void playAuto() {
		if(isAutoPlay()){
			playVideo(playlist.next());
			setPlayList();
		}
	}

	private void playVideo(File videofile) {
		File video = videofile;
		if(video==null){
			video = playlist.next();
		}
		if(video==null){
			sendtext("ïœä∑å„ÇÃìÆâÊÇ™Ç†ÇËÇ‹ÇπÇÒ");
			return;
		}
		if(!video.canRead()){
			sendtext("ïœä∑å„ÇÃìÆâÊÇ™ì«ÇﬂÇ‹ÇπÇÒÅF" + video.getName());
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

	public File poll() {
		return playlist.poll();
	}
}
