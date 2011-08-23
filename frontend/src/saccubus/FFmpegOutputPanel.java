/**
 *
 */
package saccubus;

import java.awt.*;
import javax.swing.*;

/**
 * Ç≥Ç´Ç„ÇŒÇ∑Å@ägí£
 *
 * @author orz
 * @version 1.22r3e
 *
 */
public class FFmpegOutputPanel extends JFrame {
    private static final long serialVersionUID = 1L;
    public JTextArea textArea;

	/**
	 * @throws HeadlessException
	 */
	public FFmpegOutputPanel() throws HeadlessException {
        this.setSize(new Dimension(400,300));
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        textArea = new JTextArea();
        textArea.setLineWrap(true);
        JScrollPane scroll = new JScrollPane(textArea);
        this.add(scroll);
	}

}
