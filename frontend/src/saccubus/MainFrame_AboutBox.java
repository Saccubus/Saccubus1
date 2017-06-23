package saccubus;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;

//import psi.lib.swing.PopupRightClick;

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
public class MainFrame_AboutBox extends JDialog implements ActionListener {
	/**
	 *
	 */
	private static final long serialVersionUID = -4256413309312729840L;

//	String version = "ver1.22r(2008/04/27)";

	public static final String rev = "1.67.6.24";
	private static final String modefied = " (2017/06/24)";

	String productHTML =
		"<html>" +
	//	"  <a href=\'http://www.ne.jp/asahi/mochiyama/my/file/Saccubus-1.22r3.zip\'>" +
	//	"「さきゅばす 1.22r3」</a><br/>" +
	//	"  <a href=\'http://sourceforge.jp/projects/coroid/downloads/48371/saccubus_nibr1.4.4.zip/\'>" +
	//	"さきゅばすNicoBrowser拡張1.4.4</a><br/>" +
	//	"を改造したものです。<br/>" +
	//	" orz " + rev + " " + modefied + "<br/>"+
	//	"本プログラムのオリジナルは以下の通りです。<br/><br/>"+
		"<p><a href=\'https://github.com/Saccubus/Saccubus1.x/wiki\'>" +
		"さきゅばす</a> "+ rev + modefied + "<br/><br/>"+
		"<table border=0>" +
		"<tr><td>Copyright (C) <td>2008 Saccubus Developers Team"+
		"<tr><td><td>2007-2008 2012-2015(FFmpeg) PSI"+
		"<tr><td><td>2011-2017 orz"+
		"</table>" +
		"ニコニコ動画の動画をコメントつきで保存</p>" +
		"<p>本プログラムは<br/>" +
		" <a href=\'http://osdn.jp/projects/coroid/wiki/Inqubus\'>"	+
		"いんきゅばす</a>/inqubus1.7.2.zip<br/>を参考にしています。</p>" +
		"<p>関連リンク<br/>" +
		"さきゅばす <a href=\'http://osdn.jp/projects/saccubus/\'>osdn.jp/projects/saccubus</a><br/>" +
		"coroid いんきゅばす<a href=\'http://osdn.jp/projects/coroid/\'>osdn.jp/projects/coroid</a>  雪羽氏<br/>" +
//		"<a href=\'http://anago.2ch.net/test/read.cgi/software/1346798166/l50\'>2chさきゅばすスレ 最新50</a>" +
		"<a href=\'http://egg.2ch.net/test/read.cgi/software/1449586179/l50\'>2chさきゅばすスレ 最新50</a>" +
		"</p></html>";

	JPanel panel1 = new JPanel();

	JPanel panel2 = new JPanel();

	JPanel insetsPanel1 = new JPanel();

	JPanel insetsPanel2 = new JPanel();

	JPanel insetsPanel3 = new JPanel();

	JEditorPane editorPane;

	JButton button1 = new JButton();

	JLabel imageLabel = new JLabel();
/*
	JTextArea product_field = new JTextArea(product);
*/
	ImageIcon image1 = new ImageIcon();

	BorderLayout borderLayout1 = new BorderLayout();

	BorderLayout borderLayout2 = new BorderLayout();

	FlowLayout flowLayout1 = new FlowLayout();

	GridLayout gridLayout1 = new GridLayout();

	public MainFrame_AboutBox(Frame parent) {
		super(parent);
		try {
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			jbInit();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public MainFrame_AboutBox() {
		this(null);
	}

	/**
	 * コンポーネントの初期化。
	 *
	 * @throws java.lang.Exception
	 */
	private void jbInit() throws Exception {
		image1 = new ImageIcon(saccubus.MainFrame.class.getResource("icon.png"));
		imageLabel.setIcon(image1);
		setTitle("バージョン情報");
		panel1.setLayout(borderLayout1);
		panel2.setLayout(borderLayout2);
		insetsPanel1.setLayout(flowLayout1);
		insetsPanel2.setLayout(flowLayout1);
		insetsPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		gridLayout1.setRows(1);
		gridLayout1.setColumns(1);
		insetsPanel3.setLayout(gridLayout1);
		insetsPanel3.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		button1.setText("OK");
		button1.addActionListener(this);
		insetsPanel2.add(imageLabel, null);
		panel2.add(insetsPanel2, BorderLayout.WEST);
		getContentPane().add(panel1, null);
		editorPane = new JEditorPane("text/html", productHTML);
		editorPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
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
		insetsPanel3.add(editorPane, null);
		panel2.add(insetsPanel3, BorderLayout.CENTER);
		insetsPanel1.add(button1, null);
		panel1.add(insetsPanel1, BorderLayout.SOUTH);
		panel1.add(panel2, BorderLayout.NORTH);
		setResizable(true);
	}

	/**
	 * ボタンイベントでダイアログを閉じる
	 *
	 * @param actionEvent
	 *            ActionEvent
	 */
	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		if (actionEvent.getSource() == button1) {
			dispose();
		}
	}
}
