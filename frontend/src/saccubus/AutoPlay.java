package saccubus;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

public class AutoPlay {
	private JCheckBox checkbox;
	private HistoryDeque<File> playlist = new HistoryDeque<>(null);
	private HistoryDeque<File> downloadlist = new HistoryDeque<File>(null);
	private VPlayer vplayer;
	private JLabel label;
	private JLabel status;
	private boolean playDownload = false;
	private JLabel playlistChoiceLabel;

	AutoPlay(JCheckBox c, JLabel l, VPlayer p, JLabel s){
		checkbox = c;
		label = l;
		vplayer = p;
		status = s;
		playlistChoiceLabel = new JLabel();
		playlistChoiceLabel.addMouseListener(new  MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e){
					playDownload = !playDownload;
					chooseDownload(playDownload);
				}
			}
		);
		chooseDownload(false);
	}
	AutoPlay(boolean b) {
		this(new JCheckBox(), new JLabel(), null, null);
		checkbox.setSelected(b);
	}

	public void chooseDownload(boolean isDownload){
		playDownload = isDownload;
		String s = isDownload? "DL後　":"変換後　";
		playlistChoiceLabel.setText(s);
		Color c = isDownload? Color.red : Color.black;
		playlistChoiceLabel.setForeground(c);
	}
	public JLabel getChoiceLabel(){
		return playlistChoiceLabel;
	}
	public boolean isPlayDownload(){
		return playDownload;
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

	File next() {
		if(isPlayDownload())
			return downloadlist.next();
		else
			return playlist.next();
	}
	void setNext() {
		setPlayList(next());
	}

	File back() {
		if(isPlayDownload())
			return downloadlist.back();
		else
			return playlist.back();
	}
	void setBack() {
		setPlayList(back());
	}

	private File getNow() {
		if(isPlayDownload())
			return downloadlist.getNow();
		else
			return playlist.getNow();
	}

//	public void offer(File file) {
//		if(playDownload)
//			downloadlist.offer(file);
//		else
//			playlist.offer(file);
//	}
//
	public void offer(File file, boolean choose_download){
		if(choose_download)
			downloadlist.offer(file);
		else
			playlist.offer(file);
	}
	// 変換動画再生
	private void setPlayList(final File video) {
		synchronized (label) {
			label.setVisible(false);
			if(video!=null){
				label.setText(video.getName());
				label.setForeground(Color.blue);
				label.setToolTipText(video.getName());
			}else{
				label.setText("");
				label.setToolTipText(null);
			}
			label.setVisible(true);
		}
	}

	void playAuto() {
		File video = getNow();
		setPlayList(video);
		if(isAutoPlay()){
			playVideo(video);
			next();
		}
	}
	void playVideoNow() {
		File video = getNow();
		setPlayList(video);
		playVideo(video);
	}

	private void playVideo(File video) {
		if(video==null){
			video = getNow();
		}
		if(video==null){
			sendtext("変換後の動画がありません");
			return;
		}
		if(!video.canRead()){
			sendtext("変換後の動画が読めません：" + video.getName());
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
