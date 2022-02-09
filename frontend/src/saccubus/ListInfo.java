/**
 *
 */
package saccubus;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * @author orz
 *
 */
public class ListInfo extends JComponent {

	private final JLabel vidArea;
	private String url;
	private final JLabel[] status3;
	private final JButton jButton;
	private boolean oneLine;
	private int key;
	private boolean deleted = false;
	private ConvertStopFlag flag = null;

	/**
	 * @param isOneLine
	 *
	 */
	public ListInfo(String vid, JLabel[] status, JButton button,
			boolean isOneLine, int index, ConvertStopFlag stop_flag) {
		vidArea = status[3];
		url = vid;
		vidArea.setText(vid);
		status3 = status;
		jButton = button;
		oneLine = isOneLine;
		key = index;
		flag = stop_flag;
		init();
	}
	private void init() {

		setLayout(new GridBagLayout());

	//	jButton.setBorderPainted(true);
	//	jButton.setBackground(Color.CYAN);

		GridBagConstraints gc = new GridBagConstraints();
		if(oneLine) {
			gc.gridheight = 1;
		}else{
			gc.gridheight = 3;
		}
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth = 1;
		gc.anchor = GridBagConstraints.WEST;;
		gc.fill = GridBagConstraints.BOTH;;
		add(jButton, gc);

		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.gridy = 0;
		gc.weightx = 0.0f;
		gc.gridheight = 1;
		gc.anchor = GridBagConstraints.WEST;;
		if(oneLine) {
			gc.gridwidth = 1;
		}else{
			gc.gridwidth = 4;
		}
		gc.fill = GridBagConstraints.HORIZONTAL;
		vidArea.setForeground(Color.blue);
//		Dimension sz = vidArea.getSize();
//		vidArea.setSize(Short.MAX_VALUE, sz.height);
		add(vidArea, gc);

		gc = new GridBagConstraints();
		if(oneLine) {
			gc.gridx = 2;
			gc.gridy = 0;
		}else{
			gc.gridx = 1;
			gc.gridy = 1;
		}
		gc.gridwidth = 1;
		gc.gridheight = 1;
		gc.anchor = GridBagConstraints.WEST;
		gc.fill = GridBagConstraints.HORIZONTAL;
		status3[2].setForeground(Color.darkGray);
		add(status3[2], gc);

		gc = new GridBagConstraints();
		if(oneLine) {
			gc.gridx = 3;
			gc.gridy = 0;
			gc.anchor = GridBagConstraints.WEST;;
		}else{
			gc.gridx = 2;
			gc.gridy = 1;
			gc.anchor = GridBagConstraints.CENTER;;
		}
		gc.gridwidth = 1;
		gc.gridheight = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		add(new JLabel("　　"), gc);

		gc = new GridBagConstraints();
		if(oneLine) {
			gc.gridx = 4;
			gc.gridy = 0;
			gc.anchor = GridBagConstraints.WEST;
		}else{
			gc.gridx = 3;
			gc.gridy = 1;
			gc.anchor = GridBagConstraints.EAST;
			gc.weightx = 1.0;
		}
		gc.gridwidth = 1;
		gc.gridheight = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		status3[1].setForeground(Color.MAGENTA);
		add(status3[1], gc);

		gc = new GridBagConstraints();
		if(oneLine) {
			gc.gridx = 5;
			gc.gridy = 0;
			gc.gridwidth = 1;
		}else{
			gc.gridx = 1;
			gc.gridy = 2;
			gc.gridwidth = 3;
		}
		gc.weightx = 1.0;
		gc.gridheight = 1;
		gc.anchor = GridBagConstraints.WEST;
		gc.fill = GridBagConstraints.HORIZONTAL;
		add(status3[0], gc);

		//Dimension sz = getjButton().getMaximumSize();
		int height = getjButton().getMaximumSize().height;
		if(oneLine) {
			setMaximumSize(new Dimension(Short.MAX_VALUE, (int)(height * 1.0f)));
			setPreferredSize(new Dimension(Short.MAX_VALUE, (int)(height * 1.0f)));
		}else{
			setMaximumSize(new Dimension(Short.MAX_VALUE, (int)(height * 1.8f)));
			setPreferredSize(new Dimension(1024, (int)(height * 1.8f)));
		}

		setAlignmentX(0.0f);
		setAlignmentY(0.0f);
		setVisible(true);
	}
	public ListInfo(String vid, boolean isOneLine, int index,
		JButton button, ConvertStopFlag flag){
		this(vid, new JLabel[]{new JLabel(),new JLabel(), new JLabel(), new JLabel()},
				button, isOneLine, index, flag);
	}
	public JLabel getVidArea() {
		return vidArea;
	}
	public String getVid(){
		return url;
	}
	public JButton getjButton() {
		return jButton;
	}
	public JLabel[] getStatus() {
		return status3;
	}
	public int getKey(){
		return key;
	}
	public void resetVid() {
		String vid = "("+key+")"+url;
		vidArea.setText(vid);
		//url = vid;
	}
	public void setDelete() {
		deleted  = true;
	}
	public boolean isDeleted(){
		return deleted;
	}
	public ConvertStopFlag getStopFlag() {
		return flag;
	}
}
