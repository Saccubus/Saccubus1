/**
 * 
 */
package saccubus.filemanager;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;

import saccubus.MainFrame;

/**
 * @author PSI
 *
 */
public class FileManagerFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;
	private final MainFrame MainFrameInstance;

	/**
	 * This is the default constructor
	 */
	public FileManagerFrame(final MainFrame frame) {
		super();
		MainFrameInstance = frame;
		initialize();
	}
	public void showFrame(){
		setLocationRelativeTo(MainFrameInstance);
		setVisible(true);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setIconImage(MainFrame.WinIcon);
		this.setSize(300, 200);
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		this.setContentPane(getJContentPane());
		this.setTitle("ファイルマネージャ － さきゅばす");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
		}
		return jContentPane;
	}

}
