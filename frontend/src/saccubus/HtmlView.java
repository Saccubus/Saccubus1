package saccubus;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;

import psi.lib.swing.PopupRightClick;

public class HtmlView extends JDialog implements ActionListener {

	private final Frame parent;
	private JEditorPane editorPane;
//	private JScrollPane scrollPane;
	private static final double WIDTH_RATE = 1.5;
	private static final double HEIGHT_RATE = 1.1;

	public HtmlView(Frame owner, String title, String text) {
		super(owner, title, false);
		parent = owner;
		init(text);
	}

	private void init(String text){
		try {
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Font f = parent.getFont();
		f = new Font(f.getName(), f.getStyle(), f.getSize()-2);
		setFont(f);
		editorPane = new JEditorPane("text/html", markupHtml(text));
		editorPane.setEditable(false);
		editorPane.setOpaque(false);
		editorPane.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == EventType.ACTIVATED){
					URL url = e.getURL();
					Desktop desktop = Desktop.getDesktop();
					try {
						desktop.browse(url.toURI());
					} catch (IOException ex) {
						ex.printStackTrace();
					} catch (URISyntaxException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		editorPane.addMouseListener(new PopupRightClick(editorPane));
	//	scrollPane = new JScrollPane(editorPane);
		setLayout(new BorderLayout());
		add(new JScrollPane(editorPane), BorderLayout.CENTER);
		pack();
		Dimension dim = parent.getSize();
		dim.width = (int)(WIDTH_RATE * dim.width);
		dim.height = (int)(HEIGHT_RATE * dim.height);
		setSize(dim);
		Point pt = parent.getLocation();
		setLocation(pt.x+20, pt.y+20);
		editorPane.setCaretPosition(0);	//テキスト先頭を表示

		setVisible(true);
		setResizable(true);
	}

	public void setText(String text){
		editorPane.setText(text);
		editorPane.setCaretPosition(0);
		update(getGraphics());
	}

	public static String markupHtml(String text){
		text = text.replaceAll("<", "&lt;")
			.replaceAll(">", "&gt;")
			.replaceAll("\n", "<br/>")
			.replaceAll("http://[a-zA-Z0-9%._\\-\\?=/]+","<a href=\"$0\">$0</a>");
		text = "<html>" + text + "<br/></html>";
		return text;
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
	//	notifyAll();
		dispose();
	}

}
