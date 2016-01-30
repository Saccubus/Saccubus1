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

	/**
	 *
	 */
	public ListInfo(String vid, JLabel[] status, JButton button) {
		vidArea = new JLabel(vid);
		url = vid;
		status3 = status;
		jButton = button;
		init();
	}
	private void init() {

		setLayout(new GridBagLayout());

	//	jButton.setBorderPainted(true);
	//	jButton.setBackground(Color.CYAN);

		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth = 1;
		gc.gridheight = 3;
		gc.anchor = GridBagConstraints.WEST;;
		gc.fill = GridBagConstraints.BOTH;;
		add(jButton, gc);

		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.gridy = 0;
		gc.gridwidth = 4;
		gc.gridheight = 1;
		gc.anchor = GridBagConstraints.WEST;;
		gc.fill = GridBagConstraints.HORIZONTAL;
		add(vidArea, gc);
		vidArea.setForeground(Color.blue);

		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.gridy = 1;
		gc.gridwidth = 1;
		gc.gridheight = 1;
		gc.anchor = GridBagConstraints.WEST;
		gc.fill = GridBagConstraints.HORIZONTAL;
		add(status3[2], gc);
		status3[2].setForeground(Color.darkGray);

		gc = new GridBagConstraints();
		gc.gridx = 2;
		gc.gridy = 1;
		gc.gridwidth = 1;
		gc.gridheight = 1;
		gc.anchor = GridBagConstraints.CENTER;;
		gc.fill = GridBagConstraints.HORIZONTAL;
		add(new JLabel("Å@Å@"), gc);

		gc = new GridBagConstraints();
		gc.gridx = 3;
		gc.gridy = 1;
		gc.gridwidth = 1;
		gc.gridheight = 1;
		gc.weightx = 1.0;
		gc.anchor = GridBagConstraints.EAST;
		gc.fill = GridBagConstraints.HORIZONTAL;
		add(status3[1], gc);
		status3[1].setForeground(Color.MAGENTA);

		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.gridy = 2;
		gc.gridwidth = 3;
		gc.gridheight = 1;
		gc.weightx = 1.0;
		gc.anchor = GridBagConstraints.WEST;
		gc.fill = GridBagConstraints.HORIZONTAL;
		add(status3[0], gc);

		//Dimension sz = getjButton().getMaximumSize();
		int height = getjButton().getMaximumSize().height;
		setMaximumSize(new Dimension(1000, (int)(height * 4.5)));

		setAlignmentX(LEFT_ALIGNMENT);
		setVisible(true);
	}
	public ListInfo(String vid){
		this(vid, new JLabel[]{new JLabel(),new JLabel(), new JLabel()}, new JButton());
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
}
