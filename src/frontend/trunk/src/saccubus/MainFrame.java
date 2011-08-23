package saccubus;

import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;
import psi.lib.swing.PopupRightClick;
import saccubus.util.FileDropTarget;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JTabbedPane;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.KeyEvent;

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
public class MainFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2564486741331062989L;

	public static final Image WinIcon = Toolkit.getDefaultToolkit()
			.createImage(saccubus.MainFrame.class.getResource("icon32.png"));

	
	JPanel contentPane;

	BorderLayout borderLayout1 = new BorderLayout();

	JMenuBar jMenuBar1 = new JMenuBar();

	JMenu jMenuFile = new JMenu();

	JMenuItem jMenuFileExit = new JMenuItem();

	JMenu jMenuHelp = new JMenu();

	JMenuItem jMenuHelpAbout = new JMenuItem();

	JLabel statusBar = new JLabel();

	JTabbedPane MainTabbedPane = new JTabbedPane();

	JPanel SavingInfoTabPanel = new JPanel();

	JPanel FFMpegTabPanel = new JPanel();

	JPanel VideoInfoPanel = new JPanel();

	JTextField VideoID_TextField = new JTextField();

	JButton DoButton = new JButton();

	public static final String DoButtonDefString = "変換";

	public static final String DoButtonStopString = "停止";

	public static final String DoButtonWaitString = "待機";

	GridBagLayout gridBagLayout2 = new GridBagLayout();

	JPanel UserInfoPanel = new JPanel();

	GridBagLayout gridBagLayout3 = new GridBagLayout();

	JLabel MailAddrLabel = new JLabel();

	JTextField MailAddrField = new JTextField();

	JLabel PasswordLabel = new JLabel();

	JPasswordField PasswordField = new JPasswordField();

	JPanel CommentSaveInfoPanel = new JPanel();

	GridBagLayout gridBagLayout4 = new GridBagLayout();

	JCheckBox SavingVideoCheckBox = new JCheckBox();

	JTextField VideoSavedFileField = new JTextField();

	JButton ShowSavingVideoFileDialogButton = new JButton();

	JCheckBox SavingCommentCheckBox = new JCheckBox();

	JTextField CommentSavedFileField = new JTextField();

	JButton ShowSavingCommentFileDialogButton = new JButton();

	JPanel ConvertedVideoSavingInfoPanel = new JPanel();

	GridBagLayout gridBagLayout5 = new GridBagLayout();

	JCheckBox SavingConvertedVideoCheckBox = new JCheckBox();

	JTextField ConvertedVideoSavedFileField = new JTextField();

	JButton ShowSavingConvertedVideoFileDialogButton = new JButton();

	GridBagLayout gridBagLayout6 = new GridBagLayout();

	ButtonGroup VideoSaveButtonGroup = new ButtonGroup();

	ButtonGroup CommentSaveButtonGroup = new ButtonGroup();

	ButtonGroup ConvSaveButtonGroup = new ButtonGroup();

	public MainFrame() {
		try {
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			jbInit();
			setPopup();
			setDropTarget();
			ConvertingSetting setting = ConvertingSetting.loadSetting(null,
					null);
			this.setSetting(setting);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * コンポーネントの初期化。
	 * 
	 * @throws java.lang.Exception
	 */
	private void jbInit() throws Exception {
		GridBagConstraints gridBagConstraints74 = new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0);
		gridBagConstraints74.gridwidth = 2;
		GridBagConstraints gridBagConstraints73 = new GridBagConstraints();
		gridBagConstraints73.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints73.gridy = 6;
		gridBagConstraints73.weightx = 1.0;
		gridBagConstraints73.gridwidth = 4;
		gridBagConstraints73.insets = new Insets(0, 0, 0, 5);
		gridBagConstraints73.gridx = 1;
		GridBagConstraints gridBagConstraints72 = new GridBagConstraints();
		gridBagConstraints72.gridx = 0;
		gridBagConstraints72.anchor = GridBagConstraints.WEST;
		gridBagConstraints72.insets = new Insets(0, 5, 0, 5);
		gridBagConstraints72.fill = GridBagConstraints.NONE;
		gridBagConstraints72.gridwidth = 1;
		gridBagConstraints72.gridy = 6;
		ShadowKindLabel = new JLabel();
		ShadowKindLabel.setText("影の種類");
		ShadowKindLabel.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
		GridBagConstraints gridBagConstraints71 = new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 6);
		gridBagConstraints71.fill = GridBagConstraints.BOTH;
		gridBagConstraints71.ipady = 0;
		GridBagConstraints gridBagConstraints70 = new GridBagConstraints();
		gridBagConstraints70.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints70.gridy = 1;
		gridBagConstraints70.ipadx = 0;
		gridBagConstraints70.ipady = 0;
		gridBagConstraints70.weightx = 1.0;
		gridBagConstraints70.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints70.gridx = 1;
		GridBagConstraints gridBagConstraints69 = new GridBagConstraints();
		gridBagConstraints69.gridx = 0;
		gridBagConstraints69.ipadx = 0;
		gridBagConstraints69.ipady = 0;
		gridBagConstraints69.insets = new Insets(0, 5, 0, 5);
		gridBagConstraints69.anchor = GridBagConstraints.WEST;
		gridBagConstraints69.gridy = 1;
		GridBagConstraints gridBagConstraints68 = new GridBagConstraints();
		gridBagConstraints68.fill = GridBagConstraints.BOTH;
		gridBagConstraints68.gridy = 0;
		gridBagConstraints68.ipady = 0;
		gridBagConstraints68.weightx = 1.0;
		gridBagConstraints68.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints68.gridx = 1;
		GridBagConstraints gridBagConstraints67 = new GridBagConstraints();
		gridBagConstraints67.gridx = 0;
		gridBagConstraints67.ipadx = 0;
		gridBagConstraints67.ipady = 0;
		gridBagConstraints67.insets = new Insets(0, 5, 0, 5);
		gridBagConstraints67.anchor = GridBagConstraints.WEST;
		gridBagConstraints67.gridy = 0;
		GridBagConstraints gridBagConstraints66 = new GridBagConstraints();
		gridBagConstraints66.gridx = 0;
		gridBagConstraints66.insets = new Insets(0, 5, 5, 5);
		gridBagConstraints66.anchor = GridBagConstraints.WEST;
		gridBagConstraints66.gridwidth = 2;
		gridBagConstraints66.gridy = 1;
		ViewCommentLabel = new JLabel();
		ViewCommentLabel.setText("表示コメント数");
		GridBagConstraints gridBagConstraints65 = new GridBagConstraints();
		gridBagConstraints65.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints65.gridy = 1;
		gridBagConstraints65.weightx = 1.0;
		gridBagConstraints65.gridwidth = 6;
		gridBagConstraints65.insets = new Insets(0, 5, 5, 5);
		gridBagConstraints65.gridx = 3;
		GridBagConstraints gridBagConstraints64 = new GridBagConstraints(1, 1,
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0);
		gridBagConstraints64.gridy = 3;
		gridBagConstraints64.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints64.gridx = 4;
		GridBagConstraints gridBagConstraints63 = new GridBagConstraints(0, 4,
				2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0);
		gridBagConstraints63.gridy = 7;
		gridBagConstraints63.gridx = 0;
		gridBagConstraints63.gridwidth = 5;
		GridBagConstraints gridBagConstraints62 = new GridBagConstraints(0, 3,
				2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0);
		gridBagConstraints62.gridy = 5;
		gridBagConstraints62.gridx = 1;
		gridBagConstraints62.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints62.insets = new Insets(0, 0, 5, 5);
		gridBagConstraints62.gridwidth = 4;
		GridBagConstraints gridBagConstraints61 = new GridBagConstraints(0, 2,
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0);
		gridBagConstraints61.gridy = 5;
		gridBagConstraints61.gridx = 0;
		gridBagConstraints61.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints61.anchor = GridBagConstraints.WEST;
		gridBagConstraints61.gridwidth = 1;
		GridBagConstraints gridBagConstraints60 = new GridBagConstraints(0, 1,
				1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0);
		gridBagConstraints60.gridy = 3;
		gridBagConstraints60.gridx = 1;
		gridBagConstraints60.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints60.insets = new Insets(0, 0, 0, 5);
		gridBagConstraints60.gridwidth = 3;
		GridBagConstraints gridBagConstraints59 = new GridBagConstraints(0, 0,
				2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0);
		gridBagConstraints59.gridy = 3;
		gridBagConstraints59.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints59.gridx = 0;
		gridBagConstraints59.fill = GridBagConstraints.NONE;
		gridBagConstraints59.anchor = GridBagConstraints.WEST;
		gridBagConstraints59.weightx = 0.0;
		gridBagConstraints59.gridwidth = 1;
		GridBagConstraints gridBagConstraints58 = new GridBagConstraints();
		gridBagConstraints58.gridx = 0;
		gridBagConstraints58.anchor = GridBagConstraints.WEST;
		gridBagConstraints58.insets = new Insets(0, 5, 5, 5);
		gridBagConstraints58.gridwidth = 5;
		gridBagConstraints58.weightx = 1.0;
		gridBagConstraints58.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints58.gridy = 0;
		GridBagConstraints gridBagConstraints57 = new GridBagConstraints();
		gridBagConstraints57.fill = GridBagConstraints.BOTH;
		gridBagConstraints57.gridy = 2;
		gridBagConstraints57.weightx = 1.0;
		gridBagConstraints57.insets = new Insets(0, 0, 5, 5);
		gridBagConstraints57.gridx = 1;
		GridBagConstraints gridBagConstraints56 = new GridBagConstraints();
		gridBagConstraints56.gridx = 0;
		gridBagConstraints56.insets = new Insets(0, 5, 5, 5);
		gridBagConstraints56.anchor = GridBagConstraints.WEST;
		gridBagConstraints56.gridy = 2;
		ExtOptionLabel = new JLabel();
		ExtOptionLabel.setText("出力動画の拡張子");
		GridBagConstraints gridBagConstraints55 = new GridBagConstraints();
		gridBagConstraints55.gridx = 0;
		gridBagConstraints55.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints55.weightx = 1.0;
		gridBagConstraints55.gridwidth = 2;
		gridBagConstraints55.gridy = 1;
		GridBagConstraints gridBagConstraints54 = new GridBagConstraints(0, 2,
				2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0);
		gridBagConstraints54.gridwidth = 3;
		GridBagConstraints gridBagConstraints53 = new GridBagConstraints(1, 3,
				2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0);
		gridBagConstraints53.gridy = 5;
		gridBagConstraints53.gridheight = 1;
		gridBagConstraints53.weightx = 1.0;
		gridBagConstraints53.gridwidth = 1;
		GridBagConstraints gridBagConstraints52 = new GridBagConstraints(2, 2,
				1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0);
		gridBagConstraints52.gridy = 4;
		gridBagConstraints52.gridwidth = 1;
		gridBagConstraints52.weightx = 1.0;
		gridBagConstraints52.gridx = 1;
		GridBagConstraints gridBagConstraints51 = new GridBagConstraints(2, 1,
				1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0);
		gridBagConstraints51.gridy = 3;
		gridBagConstraints51.gridwidth = 1;
		gridBagConstraints51.weightx = 1.0;
		gridBagConstraints51.gridx = 1;
		GridBagConstraints gridBagConstraints50 = new GridBagConstraints(0, 3,
				1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0);
		gridBagConstraints50.gridy = 5;
		GridBagConstraints gridBagConstraints49 = new GridBagConstraints(0, 2,
				2, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0);
		gridBagConstraints49.gridy = 4;
		gridBagConstraints49.gridwidth = 1;
		GridBagConstraints gridBagConstraints48 = new GridBagConstraints(0, 1,
				1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0);
		gridBagConstraints48.gridy = 3;
		GridBagConstraints gridBagConstraints45 = new GridBagConstraints(3, 5,
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0);
		gridBagConstraints45.gridy = 7;
		GridBagConstraints gridBagConstraints44 = new GridBagConstraints(3, 3,
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0);
		gridBagConstraints44.gridy = 5;
		GridBagConstraints gridBagConstraints43 = new GridBagConstraints(0, 5,
				3, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(3, 50, 5, 5), 0, 0);
		gridBagConstraints43.gridy = 7;
		GridBagConstraints gridBagConstraints42 = new GridBagConstraints(0, 4,
				4, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 25, 0, 5), 0, 0);
		gridBagConstraints42.gridy = 6;
		GridBagConstraints gridBagConstraints41 = new GridBagConstraints(0, 3,
				3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 50, 0, 5), 0, 0);
		gridBagConstraints41.gridy = 5;
		GridBagConstraints gridBagConstraints40 = new GridBagConstraints(0, 2,
				4, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 25, 0, 5), 0, 0);
		gridBagConstraints40.gridy = 3;
		GridBagConstraints gridBagConstraints39 = new GridBagConstraints();
		gridBagConstraints39.gridx = 0;
		gridBagConstraints39.insets = new Insets(0, 50, 0, 0);
		gridBagConstraints39.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints39.weightx = 1.0;
		gridBagConstraints39.gridwidth = 4;
		gridBagConstraints39.gridy = 4;
		GridBagConstraints gridBagConstraints35 = new GridBagConstraints();
		gridBagConstraints35.fill = GridBagConstraints.BOTH;
		gridBagConstraints35.weighty = 1.0;
		gridBagConstraints35.weightx = 1.0;
		GridBagConstraints gridBagConstraints33 = new GridBagConstraints();
		gridBagConstraints33.gridx = 0;
		gridBagConstraints33.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints33.weightx = 1.0;
		gridBagConstraints33.insets = new Insets(0, 5, 5, 5);
		gridBagConstraints33.gridwidth = 5;
		gridBagConstraints33.gridy = 9;
		GridBagConstraints gridBagConstraints26 = new GridBagConstraints();
		gridBagConstraints26.gridx = 0;
		gridBagConstraints26.gridwidth = 4;
		gridBagConstraints26.insets = new Insets(0, 25, 0, 5);
		gridBagConstraints26.weightx = 1.0;
		gridBagConstraints26.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints26.gridy = 9;
		GridBagConstraints gridBagConstraints25 = new GridBagConstraints();
		gridBagConstraints25.gridx = 0;
		gridBagConstraints25.gridwidth = 4;
		gridBagConstraints25.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints25.insets = new Insets(0, 25, 0, 5);
		gridBagConstraints25.weightx = 1.0;
		gridBagConstraints25.gridy = 8;
		GridBagConstraints gridBagConstraints24 = new GridBagConstraints(3, 10,
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0);
		gridBagConstraints24.gridy = 14;
		GridBagConstraints gridBagConstraints23 = new GridBagConstraints(3, 8,
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0);
		gridBagConstraints23.gridy = 12;
		GridBagConstraints gridBagConstraints22 = new GridBagConstraints(1, 6,
				4, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0);
		gridBagConstraints22.gridy = 10;
		GridBagConstraints gridBagConstraints21 = new GridBagConstraints(0, 10,
				3, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 50, 5, 5), 0, 0);
		gridBagConstraints21.gridy = 14;
		GridBagConstraints gridBagConstraints20 = new GridBagConstraints(0, 9,
				4, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 25, 0, 5), 0, 0);
		gridBagConstraints20.gridy = 13;
		GridBagConstraints gridBagConstraints19 = new GridBagConstraints(0, 8,
				3, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 50, 0, 5), 0, 0);
		gridBagConstraints19.gridy = 12;
		GridBagConstraints gridBagConstraints18 = new GridBagConstraints(0, 7,
				4, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 25, 0, 5), 0, 0);
		gridBagConstraints18.gridy = 11;
		GridBagConstraints gridBagConstraints17 = new GridBagConstraints(0, 6,
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0);
		gridBagConstraints17.gridy = 10;
		gridBagConstraints17.insets = new Insets(0, 50, 0, 5);
		GridBagConstraints gridBagConstraints16 = new GridBagConstraints(0, 5,
				4, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0);
		gridBagConstraints16.gridy = 7;
		GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
		gridBagConstraints14.gridx = 0;
		gridBagConstraints14.anchor = GridBagConstraints.WEST;
		gridBagConstraints14.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints14.weightx = 1.0;
		gridBagConstraints14.gridwidth = 5;
		gridBagConstraints14.insets = new Insets(0, 5, 0, 5);
		gridBagConstraints14.gridy = 8;
		GridBagConstraints gridBagConstraints7 = new GridBagConstraints(0, 0,
				1, 1, 1.0, 1.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0);
		gridBagConstraints7.weighty = 0.0;
		GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
		gridBagConstraints6.gridx = 0;
		gridBagConstraints6.weighty = 1.0;
		gridBagConstraints6.weightx = 1.0;
		gridBagConstraints6.insets = new Insets(0, 5, 0, 5);
		gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints6.anchor = GridBagConstraints.NORTH;
		gridBagConstraints6.gridy = 1;
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints(0, 1,
				1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0);
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.anchor = GridBagConstraints.NORTH;
		gridBagConstraints1.weighty = 1.0;
		this.setIconImage(WinIcon);
		contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(borderLayout1);
		setSize(new Dimension(400, 560));
		setTitle("さきゅばす");
		this.addWindowListener(new MainFrame_this_windowAdapter(this));
		statusBar.setText(" ");
		jMenuFile.setText("ファイル");
		jMenuFileExit.setText("終了");
		jMenuFileExit
				.addActionListener(new MainFrame_jMenuFileExit_ActionAdapter(
						this));
		jMenuHelp.setText("ヘルプ");
		jMenuHelpAbout.setText("バージョン情報");
		jMenuHelpAbout
				.addActionListener(new MainFrame_jMenuHelpAbout_ActionAdapter(
						this));
		VideoInfoPanel.setLayout(gridBagLayout1);
		VideoID_TextField.setText("http://www.nicovideo.jp/watch/");
		DoButton.setText(DoButtonDefString);
		DoButton.addActionListener(new MainFrame_DoButton_actionAdapter(this));
		SavingInfoTabPanel.setLayout(gridBagLayout2);
		UserInfoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createEtchedBorder(), "ユーザ設定"));
		UserInfoPanel.setLayout(gridBagLayout3);
		MailAddrLabel.setText("メールアドレス");
		PasswordLabel.setText("パスワード");
		CommentSaveInfoPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				"コメント保存設定", TitledBorder.LEADING, TitledBorder.TOP, new Font(
						"MS UI Gothic", Font.PLAIN, 12), Color.black));
		CommentSaveInfoPanel.setLayout(gridBagLayout4);
		SavingVideoCheckBox.setText("動画をダウンロードする");
		ShowSavingVideoFileDialogButton.setText("参照");
		ShowSavingVideoFileDialogButton
				.addActionListener(new MainFrame_ShowSavingVideoDialogButton_actionAdapter(
						this));
		SavingCommentCheckBox.setText("コメントをダウンロードする");
		ShowSavingCommentFileDialogButton.setText("参照");
		ShowSavingCommentFileDialogButton
				.addActionListener(new MainFrame_ShowSavingCommentDialogButton_actionAdapter(
						this));
		ConvertedVideoSavingInfoPanel.setBorder(BorderFactory
				.createTitledBorder(BorderFactory.createEtchedBorder(),
						"コメント付き動画保存設定"));
		ConvertedVideoSavingInfoPanel.setLayout(gridBagLayout5);
		SavingConvertedVideoCheckBox.setText("コメント付き動画に変換する");
		ShowSavingConvertedVideoFileDialogButton.setText("参照");
		ShowSavingConvertedVideoFileDialogButton
				.addActionListener(new MainFrame_ShowSavingConvertedVideoDialogButton_actionAdapter(
						this));
		FFMpegTabPanel.setLayout(gridBagLayout6);
		PathSettingPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "プログラムの位置の設定"));
		PathSettingPanel.setLayout(gridBagLayout7);
		FFmpegPathLabel.setText("FFmpeg");
		SettingFFmpegPathButton.setText("参照");
		SettingFFmpegPathButton
				.addActionListener(new MainFrame_SettingFFmpegPathButton_actionAdapter(
						this));
		VhookPathLabel.setText("拡張vhookライブラリ");
		SettingVhookPathButton.setText("参照");
		SettingVhookPathButton
				.addActionListener(new MainFrame_SettingVhookPathButton_actionAdapter(
						this));
		VhookSettingPanel.setLayout(gridBagLayout8);
		VhookSettingPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "拡張vhookライブラリの設定"));
		FFmpegSettingPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "FFmpegの設定"));
		FFmpegSettingPanel.setLayout(gridBagLayout9);
		FontPathLabel.setText("フォントパス");
		SettingFontPathButton.setText("参照");
		SettingFontPathButton
				.addActionListener(new MainFrame_SettingFontPathButton_actionAdapter(
						this));
		ShowConvVideoCheckBox.setText("変換中の画像を表示する");
		InLabel.setText("入力オプション");
		OutLabel.setText("出力オプション");
		CommentNumLabel.setText("取得コメント数");
		MainOptionLabel.setText("メインオプション");
		FontIndexLabel.setText("フォント番号");
		VideoID_Label.setText("URL/ID");
		WayBackLabel.setText("過去ログ");
		OpPanel.setLayout(new GridBagLayout());
		Conv_SaveFileRadioButton.setText("保存するファイル名を指定する");
		Conv_SaveFolderRadioButton.setText("保存するフォルダを指定し、ファイル名は自動で決定する");
		ShowSavingConvertedVideoFolderDialogButton.setText("参照");
		ShowSavingConvertedVideoFolderDialogButton
				.addActionListener(new MainFrame_ShowSavingConvertedVideoFolderDialogButton_actionAdapter(
						this));
		ShowSavingVideoFolderDialogButton.setText("参照");
		ShowSavingVideoFolderDialogButton
				.addActionListener(new MainFrame_ShowSavingVideoFolderDialogButton_actionAdapter(
						this));
		Video_SaveFolderRadioButton.setText("保存するフォルダを指定し、ファイル名は自動で決定する");
		Video_SaveFileRadioButton.setText("保存するファイル名を指定する");
		Comment_SaveFileRadioButton.setText("保存するファイル名を指定する");
		ShowSavingCommentFolderDialogButton.setText("参照");
		ShowSavingCommentFolderDialogButton
				.addActionListener(new MainFrame_ShowSavingCommentFolderDialogButton_actionAdapter(
						this));
		Comment_SaveFolderRadioButton.setText("保存するフォルダを指定し、ファイル名は自動で決定する");
		BasicInfoTabPanel.setLayout(gridBagLayout12);
		jMenuBar1.add(jMenuFile);
		jMenuFile.add(jMenuFileExit);
		jMenuBar1.add(jMenuHelp);
		jMenuHelp.add(jMenuHelpAbout);
		setJMenuBar(jMenuBar1);
		/* ビデオグループ */
		VideoSaveButtonGroup.add(Video_SaveFileRadioButton);
		VideoSaveButtonGroup.add(Video_SaveFolderRadioButton);

		CommentSaveButtonGroup.add(Comment_SaveFileRadioButton);
		CommentSaveButtonGroup.add(Comment_SaveFolderRadioButton);

		ConvSaveButtonGroup.add(Conv_SaveFileRadioButton);
		ConvSaveButtonGroup.add(Conv_SaveFolderRadioButton);

		contentPane.add(statusBar, BorderLayout.SOUTH);
		contentPane.add(MainTabbedPane, java.awt.BorderLayout.CENTER);
		contentPane.add(VideoInfoPanel, java.awt.BorderLayout.NORTH);
		UserInfoPanel.add(PasswordField, new GridBagConstraints(1, 1, 1, 1,
				1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 5, 5, 5), 0, 0));
		UserInfoPanel.add(MailAddrField, new GridBagConstraints(1, 0, 1, 1,
				1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 5, 5, 5), 0, 0));
		UserInfoPanel.add(PasswordLabel, new GridBagConstraints(0, 1, 1, 1,
				0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(0, 5, 5, 0), 0, 0));
		UserInfoPanel.add(MailAddrLabel, new GridBagConstraints(0, 0, 1, 1,
				0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(0, 5, 5, 0), 0, 0));
		PathSettingPanel.add(FFmpegPathField, new GridBagConstraints(0, 1, 1,
				1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
		PathSettingPanel.add(SettingFFmpegPathButton, gridBagConstraints74);
		PathSettingPanel.add(FFmpegPathLabel, new GridBagConstraints(0, 0, 2,
				1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(0, 5, 5, 5), 0, 0));
		PathSettingPanel.add(VhookPathField, new GridBagConstraints(0, 3, 1, 1,
				1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 5, 5, 5), 0, 0));
		PathSettingPanel.add(SettingVhookPathButton, new GridBagConstraints(1,
				3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));
		PathSettingPanel.add(VhookPathLabel, gridBagConstraints54);
		FFmpegSettingPanel.add(CommandLineOutOptionField, gridBagConstraints53);
		FFmpegSettingPanel.add(CommandLineInOptionField, gridBagConstraints52);
		FFmpegSettingPanel.add(MainOptionField, gridBagConstraints51);
		FFmpegSettingPanel.add(MainOptionLabel, gridBagConstraints48);
		FFmpegSettingPanel.add(InLabel, gridBagConstraints49);
		FFmpegSettingPanel.add(OutLabel, gridBagConstraints50);
		FFmpegSettingPanel.add(getFFmpegOptionComboBoxPanel(),
				gridBagConstraints55);
		FFmpegSettingPanel.add(ExtOptionLabel, gridBagConstraints56);
		FFmpegSettingPanel.add(getExtOptionField(), gridBagConstraints57);
		VideoInfoPanel.add(DoButton, gridBagConstraints71);
		VideoInfoPanel.add(OpPanel, new GridBagConstraints(0, 0, 1, 1, 1.0,
				0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		OpPanel.add(VideoID_Label, gridBagConstraints67);
		OpPanel.add(VideoID_TextField, gridBagConstraints68);
		OpPanel.add(WayBackLabel, gridBagConstraints69);
		OpPanel.add(WayBackField, gridBagConstraints70);
		ConvertedVideoSavingInfoPanel.add(SavingConvertedVideoCheckBox,
				new GridBagConstraints(0, 0, 3, 1, 1.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 5, 0, 5), 0, 0));
		MainTabbedPane.add(BasicInfoTabPanel, "基本設定");
		MainTabbedPane.add(SavingInfoTabPanel, "保存設定");
		MainTabbedPane.add(FFMpegTabPanel, "動画設定");
		MainTabbedPane.addTab("変換設定", null, getConvertingSettingPanel(), null);
		SavingInfoTabPanel.add(getSaveInfoTabPaneEach(), gridBagConstraints35);
		BasicInfoTabPanel.add(UserInfoPanel, gridBagConstraints7);
		BasicInfoTabPanel.add(getProxyInfoPanel(), gridBagConstraints6);
		VhookSettingPanel.add(FontPathLabel, gridBagConstraints59);
		VhookSettingPanel.add(ShowConvVideoCheckBox, gridBagConstraints63);
		VhookSettingPanel.add(FontPathField, gridBagConstraints60);
		VhookSettingPanel.add(FontIndexField, gridBagConstraints62);
		VhookSettingPanel.add(FontIndexLabel, gridBagConstraints61);
		VhookSettingPanel.add(SettingFontPathButton, gridBagConstraints64);
		VhookSettingPanel.add(getFixFontSizeCheckBox(), gridBagConstraints14);
		VhookSettingPanel.add(getOpaqueCommentCheckBox(), gridBagConstraints33);
		VhookSettingPanel.add(getNotUseVhookCheckBox(), gridBagConstraints58);
		VhookSettingPanel.add(getViewCommentField(), gridBagConstraints65);
		VhookSettingPanel.add(ViewCommentLabel, gridBagConstraints66);
		VhookSettingPanel.add(ShadowKindLabel, gridBagConstraints72);
		VhookSettingPanel.add(getShadowComboBox(), gridBagConstraints73);
		CommentSaveInfoPanel.add(CommentNumLabel, gridBagConstraints17);
		CommentSaveInfoPanel.add(SavingCommentCheckBox, gridBagConstraints16);
		ConvertedVideoSavingInfoPanel.add(Conv_SaveFolderRadioButton,
				gridBagConstraints40);
		CommentSaveInfoPanel.add(CommentNumField, gridBagConstraints22);
		CommentSaveInfoPanel.add(Comment_SaveFolderRadioButton,
				gridBagConstraints18);
		CommentSaveInfoPanel.add(Comment_SaveFileRadioButton,
				gridBagConstraints20);
		CommentSaveInfoPanel.add(CommentSavedFolderField, gridBagConstraints19);
		CommentSaveInfoPanel.add(ShowSavingCommentFolderDialogButton,
				gridBagConstraints23);
		CommentSaveInfoPanel.add(CommentSavedFileField, gridBagConstraints21);
		CommentSaveInfoPanel.add(ShowSavingCommentFileDialogButton,
				gridBagConstraints24);
		CommentSaveInfoPanel.add(getDelCommentCheckBox(), gridBagConstraints25);
		CommentSaveInfoPanel.add(getFixCommentNumCheckBox(),
				gridBagConstraints26);
		ConvertedVideoSavingInfoPanel.add(Conv_SaveFileRadioButton,
				gridBagConstraints42);
		ConvertedVideoSavingInfoPanel.add(ConvertedVideoSavedFolderField,
				gridBagConstraints41);
		ConvertedVideoSavingInfoPanel.add(
				ShowSavingConvertedVideoFolderDialogButton,
				gridBagConstraints44);
		ConvertedVideoSavingInfoPanel.add(ConvertedVideoSavedFileField,
				gridBagConstraints43);
		ConvertedVideoSavingInfoPanel.add(
				ShowSavingConvertedVideoFileDialogButton, gridBagConstraints45);
		ConvertedVideoSavingInfoPanel.add(getNotAddVideoID_ConvVideoCheckBox(),
				gridBagConstraints39);
		FFMpegTabPanel.add(PathSettingPanel, new GridBagConstraints(0, 0, 1, 1,
				1.0, 0.0, GridBagConstraints.NORTHEAST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
		FFMpegTabPanel.add(FFmpegSettingPanel, gridBagConstraints1);
	}

	private void setPopup() {
		MainOptionField.addMouseListener(new PopupRightClick(
				this.MainOptionField));
		CommandLineInOptionField.addMouseListener(new PopupRightClick(
				this.CommandLineInOptionField));
		CommandLineOutOptionField.addMouseListener(new PopupRightClick(
				this.CommandLineOutOptionField));
		CommentNumField.addMouseListener(new PopupRightClick(
				this.CommentNumField));

		CommentSavedFileField.addMouseListener(new PopupRightClick(
				this.CommentSavedFileField));
		CommentSavedFolderField.addMouseListener(new PopupRightClick(
				this.CommentSavedFolderField));

		ConvertedVideoSavedFileField.addMouseListener(new PopupRightClick(
				this.ConvertedVideoSavedFileField));
		ConvertedVideoSavedFolderField.addMouseListener(new PopupRightClick(
				this.ConvertedVideoSavedFolderField));

		VideoSavedFileField.addMouseListener(new PopupRightClick(
				this.VideoSavedFileField));
		VideoSavedFolderField.addMouseListener(new PopupRightClick(
				this.VideoSavedFolderField));

		FFmpegPathField.addMouseListener(new PopupRightClick(
				this.FFmpegPathField));
		VhookPathField
				.addMouseListener(new PopupRightClick(this.VhookPathField));
		VideoID_TextField.addMouseListener(new PopupRightClick(
				this.VideoID_TextField));
		ViewCommentField.addMouseListener(new PopupRightClick(
				this.ViewCommentField));
		FontPathField.addMouseListener(new PopupRightClick(this.FontPathField));
		MailAddrField.addMouseListener(new PopupRightClick(this.MailAddrField));
		PasswordField.addMouseListener(new PopupRightClick(this.PasswordField));
		WayBackField.addMouseListener(new PopupRightClick(this.WayBackField));

		ProxyTextField
				.addMouseListener(new PopupRightClick(this.ProxyTextField));
		ProxyPortTextField.addMouseListener(new PopupRightClick(
				this.ProxyPortTextField));

		FontIndexField
				.addMouseListener(new PopupRightClick(this.FontIndexField));

		NGWordTextField.addMouseListener(new PopupRightClick(
				this.NGWordTextField));
		NGIDTextField.addMouseListener(new PopupRightClick(this.NGIDTextField));
	}

	private void setDropTarget() {
		addTarget(VideoSavedFileField, false);
		addTarget(VideoSavedFolderField, true);

		addTarget(CommentSavedFileField, false);
		addTarget(CommentSavedFolderField, true);

		addTarget(ConvertedVideoSavedFileField, false);
		addTarget(ConvertedVideoSavedFolderField, true);

		addTarget(FFmpegPathField, false);
		addTarget(VhookPathField, false);
		addTarget(FontPathField, false);

	}

	private DropTarget addTarget(JTextField c, boolean isDir) {
		return new DropTarget(c, DnDConstants.ACTION_COPY, new FileDropTarget(
				c, isDir), true);
	}

	private File CurrentDir = new File(".");

	JPanel PathSettingPanel = new JPanel();

	JLabel FFmpegPathLabel = new JLabel();

	GridBagLayout gridBagLayout7 = new GridBagLayout();

	JTextField FFmpegPathField = new JTextField();

	JButton SettingFFmpegPathButton = new JButton();

	JLabel VhookPathLabel = new JLabel();

	JTextField VhookPathField = new JTextField();

	JButton SettingVhookPathButton = new JButton();

	JPanel VhookSettingPanel = new JPanel();

	GridBagLayout gridBagLayout8 = new GridBagLayout();

	JPanel FFmpegSettingPanel = new JPanel();

	GridBagLayout gridBagLayout9 = new GridBagLayout();

	JLabel FontPathLabel = new JLabel();

	JTextField FontPathField = new JTextField();

	JButton SettingFontPathButton = new JButton();

	JCheckBox ShowConvVideoCheckBox = new JCheckBox();

	JTextField CommandLineOutOptionField = new JTextField();

	private void showSaveDialog(String title, JTextField field, boolean isSave,
			boolean isDir) {
		JFileChooser chooser = new JFileChooser(CurrentDir);
		chooser.setDialogTitle(title);
		int code = 0;
		if (isDir) {
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		}
		if (isSave) {
			code = chooser.showSaveDialog(this);
		} else {
			code = chooser.showOpenDialog(this);
		}
		if (code == JFileChooser.APPROVE_OPTION) {
			if (isDir) {
				CurrentDir = chooser.getCurrentDirectory();
				field.setText(CurrentDir.getAbsolutePath());
			} else {
				File selected = chooser.getSelectedFile();
				CurrentDir = chooser.getCurrentDirectory();
				field.setText(selected.getAbsolutePath());
			}
		}
	}

	private ConvertingSetting getSetting() {
		int back_comment;
		try {
			back_comment = Integer.parseInt(CommentNumField.getText());
		} catch (NumberFormatException ex) {
			back_comment = 500;
		}
		int proxy_port;
		try {
			proxy_port = Integer.parseInt(ProxyPortTextField.getText());
		} catch (NumberFormatException e) {
			proxy_port = -1;
		}
		ConvertingSetting setting = new ConvertingSetting(MailAddrField
				.getText(), new String(PasswordField.getPassword()),
				SavingVideoCheckBox.isSelected(),
				VideoSavedFileField.getText(), SavingCommentCheckBox
						.isSelected(), CommentSavedFileField.getText(),
				SavingConvertedVideoCheckBox.isSelected(),
				ConvertedVideoSavedFileField.getText(), ViewCommentField
						.getText(), FFmpegPathField.getText(), VhookPathField
						.getText(), ExtOptionField.getText(), MainOptionField
						.getText(), CommandLineInOptionField.getText(),
				CommandLineOutOptionField.getText(), Integer
						.toString(back_comment), FontPathField.getText(),
				Integer.parseInt(FontIndexField.getText()),
				ShowConvVideoCheckBox.isSelected(), DelVideoCheckBox
						.isSelected(),
				Video_SaveFolderRadioButton.isSelected(), VideoSavedFolderField
						.getText(), DelCommentCheckBox.isSelected(),
				Comment_SaveFolderRadioButton.isSelected(),
				CommentSavedFolderField.getText(),
				NotAddVideoID_ConvVideoCheckBox.isSelected(),
				Conv_SaveFolderRadioButton.isSelected(),
				ConvertedVideoSavedFolderField.getText(), NGWordTextField
						.getText(), NGIDTextField.getText(), UseProxyCheckBox
						.isSelected(), ProxyTextField.getText(), proxy_port,
				FixFontSizeCheckBox.isSelected(), FixCommentNumCheckBox
						.isSelected(), OpaqueCommentCheckBox.isSelected(),
				FFmpegOptionModel.getSelectedFile(), NotUseVhookCheckBox
						.isSelected(),ShadowComboBox.getSelectedIndex());
		return setting;
	}

	private void setSetting(ConvertingSetting setting) {
		MailAddrField.setText(setting.getMailAddress());
		PasswordField.setText(setting.getPassword());
		SavingVideoCheckBox.setSelected(setting.isSaveVideo());
		VideoSavedFileField.setText(setting.getVideoFile().getPath());
		SavingCommentCheckBox.setSelected(setting.isSaveComment());
		CommentSavedFileField.setText(setting.getCommentFile().getPath());
		SavingConvertedVideoCheckBox.setSelected(setting.isSaveConverted());
		ConvertedVideoSavedFileField.setText(setting.getConvertedVideoFile()
				.getPath());
		ViewCommentField.setText(setting.getVideoShowNum());
		FFmpegPathField.setText(setting.getFFmpegPath());
		VhookPathField.setText(setting.getVhookPath());
		ExtOptionField.setText(setting.getCmdLineOptionExt());
		MainOptionField.setText(setting.getCmdLineOptionMain());
		CommandLineOutOptionField.setText(setting.getCmdLineOptionOut());
		CommandLineInOptionField.setText(setting.getCmdLineOptionIn());
		CommentNumField.setText(setting.getBackComment());
		FontPathField.setText(setting.getFontPath());
		FontIndexField.setText(setting.getFontIndex());
		ShowConvVideoCheckBox
				.setSelected(setting.isVhook_ShowConvertingVideo());
		VideoSavedFolderField.setText(setting.getVideoFixFileNameFolder()
				.getPath());
		CommentSavedFolderField.setText(setting.getCommentFixFileNameFolder()
				.getPath());
		ConvertedVideoSavedFolderField.setText(setting
				.getConvFixFileNameFolder().getPath());
		DelVideoCheckBox.setSelected(setting.isDeleteVideoAfterConverting());
		DelCommentCheckBox
				.setSelected(setting.isDeleteCommentAfterConverting());
		NotAddVideoID_ConvVideoCheckBox.setSelected(setting
				.isNotAddVideoID_Conv());
		if (setting.isVideoFixFileName()) {
			Video_SaveFileRadioButton.setSelected(false);
			Video_SaveFolderRadioButton.setSelected(true);
		} else {
			Video_SaveFileRadioButton.setSelected(true);
			Video_SaveFolderRadioButton.setSelected(false);
		}
		if (setting.isCommentFixFileName()) {
			Comment_SaveFileRadioButton.setSelected(false);
			Comment_SaveFolderRadioButton.setSelected(true);
		} else {
			Comment_SaveFileRadioButton.setSelected(true);
			Comment_SaveFolderRadioButton.setSelected(false);
		}
		if (setting.isConvFixFileName()) {
			Conv_SaveFileRadioButton.setSelected(false);
			Conv_SaveFolderRadioButton.setSelected(true);
		} else {
			Conv_SaveFileRadioButton.setSelected(true);
			Conv_SaveFolderRadioButton.setSelected(false);
		}
		NGWordTextField.setText(setting.getNG_Word());
		NGIDTextField.setText(setting.getNG_ID());
		// プロキシ関連
		UseProxyCheckBox.setSelected(setting.useProxy());
		ProxyTextField.setText(setting.getProxy());
		int proxy_port = setting.getProxyPort();
		if (proxy_port >= 0 && proxy_port <= 65535) {
			ProxyPortTextField.setText(Integer.toString(proxy_port));
		} else {
			ProxyPortTextField.setText("");
		}
		FixFontSizeCheckBox.setSelected(setting.isFixFontSize());
		FixCommentNumCheckBox.setSelected(setting.isFixCommentNum());
		OpaqueCommentCheckBox.setSelected(setting.isOpaqueComment());
		FFmpegOptionModel.reload(setting.getOptionFile());
		NotUseVhookCheckBox.setSelected(setting.isVhookDisabled());
		ShadowComboBox.setSelectedIndex(setting.getShadowIndex());
	}

	/**
	 * [ファイル|終了] アクションが実行されました。
	 * 
	 * @param actionEvent
	 *            ActionEvent
	 */
	void jMenuFileExit_actionPerformed(ActionEvent actionEvent) {
		ConvertingSetting setting = this.getSetting();
		ConvertingSetting.saveSetting(setting);
		System.exit(0);
	}

	/**
	 * [ヘルプ|バージョン情報] アクションが実行されました。
	 * 
	 * @param actionEvent
	 *            ActionEvent
	 */
	void jMenuHelpAbout_actionPerformed(ActionEvent actionEvent) {
		MainFrame_AboutBox dlg = new MainFrame_AboutBox(this);
		dlg.pack();
		dlg.setLocationRelativeTo(this);
		dlg.setVisible(true);
	}

	/* 変換・保存する */
	Converter Converter = null;

	JTextField CommandLineInOptionField = new JTextField();

	JLabel InLabel = new JLabel();

	JLabel OutLabel = new JLabel();

	JLabel CommentNumLabel = new JLabel();

	JTextField CommentNumField = new JTextField();

	JLabel MainOptionLabel = new JLabel();

	JTextField MainOptionField = new JTextField();

	JLabel FontIndexLabel = new JLabel();

	JTextField FontIndexField = new JTextField();

	JLabel VideoID_Label = new JLabel();

	JLabel WayBackLabel = new JLabel();

	JTextField WayBackField = new JTextField();

	GridBagLayout gridBagLayout10 = new GridBagLayout();

	GridBagLayout gridBagLayout11 = new GridBagLayout();

	GridBagLayout gridBagLayout1 = new GridBagLayout();

	JPanel OpPanel = new JPanel();

	GridLayout gridLayout1 = new GridLayout();

	JRadioButton Conv_SaveFileRadioButton = new JRadioButton();

	JRadioButton Conv_SaveFolderRadioButton = new JRadioButton();

	JTextField ConvertedVideoSavedFolderField = new JTextField();

	JButton ShowSavingConvertedVideoFolderDialogButton = new JButton();

	JTextField VideoSavedFolderField = new JTextField();

	JButton ShowSavingVideoFolderDialogButton = new JButton();

	JRadioButton Video_SaveFolderRadioButton = new JRadioButton();

	JRadioButton Video_SaveFileRadioButton = new JRadioButton();

	JRadioButton Comment_SaveFileRadioButton = new JRadioButton();

	JTextField CommentSavedFolderField = new JTextField();

	JButton ShowSavingCommentFolderDialogButton = new JButton();

	JRadioButton Comment_SaveFolderRadioButton = new JRadioButton();

	JPanel BasicInfoTabPanel = new JPanel();

	GridBagLayout gridBagLayout12 = new GridBagLayout();

	private JPanel ConvertingSettingPanel = null;

	private JPanel NGWordSettingPanel = null;

	private JLabel NGWordLavel = null;

	private JTextField NGWordTextField = null;

	private JLabel NGIDLabel = null;

	private JTextField NGIDTextField = null;

	private JPanel ProxyInfoPanel = null;

	private JLabel ProxyLabel = null;

	private JTextField ProxyTextField = null;

	private JLabel ProxyPortLabel = null;

	private JTextField ProxyPortTextField = null;

	private JCheckBox UseProxyCheckBox = null;

	private JCheckBox FixFontSizeCheckBox = null;

	private JCheckBox DelVideoCheckBox = null;

	private JCheckBox DelCommentCheckBox = null;

	private JCheckBox FixCommentNumCheckBox = null;

	private JCheckBox OpaqueCommentCheckBox = null;

	private JPanel VideoSaveInfoPanel = null;

	private JTabbedPane SaveInfoTabPaneEach = null;

	private JPanel VideoSavingTabbedPanel = null;

	private JPanel ConvertedVideoSavingTabbedPanel = null;

	private JCheckBox NotAddVideoID_ConvVideoCheckBox = null;

	private JComboBox FFmpegOptionComboBox = null;

	private JButton FFmpegOptionReloadButton = null;

	private JPanel FFmpegOptionComboBoxPanel = null;

	public void DoButton_actionPerformed(ActionEvent e) {
		if (Converter == null || Converter.isConverted()) {
			Converter = new Converter(VideoID_TextField.getText(), WayBackField
					.getText(), this.getSetting(), this.statusBar,
					new ConvertStopFlag(this.DoButton, DoButtonStopString,
							DoButtonWaitString, DoButtonDefString));
			Converter.start();
		} else { /* 開始しているので、ストップする。 */
			final ConvertStopFlag flag = Converter.getStopFlag();
			if (!flag.needStop()) { /* まだストップしていない。 */
				flag.stop();
			}
		}
	}

	/* ビデオ・セーブダイアログ */
	public void ShowSavingVideoDialogButton_actionPerformed(ActionEvent e) {
		showSaveDialog("動画の保存先(ファイル)", VideoSavedFileField, true, false);
	}

	/* コメント・セーブダイアログ */
	public void ShowSavingCommentDialogButton_actionPerformed(ActionEvent e) {
		showSaveDialog("コメントの保存先(ファイル)", CommentSavedFileField, true, false);
	}

	/* コメント付きビデオ・セーブダイアログ */
	public void ShowSavingConvertedVideoDialogButton_actionPerformed(
			ActionEvent e) {
		showSaveDialog("コメント付き動画の保存先(ファイル)", ConvertedVideoSavedFileField,
				true, false);
	}

	/* FFmpegへのパス */
	public void SettingFFmpegPathButton_actionPerformed(ActionEvent e) {
		showSaveDialog("FFmpegへのパス", FFmpegPathField, false, false);
	}

	public void SettingVhookPathButton_actionPerformed(ActionEvent e) {
		showSaveDialog("拡張vhookライブラリへのパス", VhookPathField, false, false);
	}

	public void SettingFontPathButton_actionPerformed(ActionEvent e) {
		showSaveDialog("フォントへのパス", FontPathField, false, false);
	}

	public void this_windowClosing(WindowEvent e) {
		this.jMenuFileExit_actionPerformed(null);
	}

	public void ShowSavingConvertedVideoFolderDialogButton_actionPerformed(
			ActionEvent e) {
		/* フォルダ */
		showSaveDialog("コメント付き動画の保存先(フォルダ)", ConvertedVideoSavedFolderField,
				true, true);
	}

	public void ShowSavingCommentFolderDialogButton_actionPerformed(
			ActionEvent e) {
		showSaveDialog("コメントの保存先(フォルダ)", CommentSavedFolderField, true, true);
	}

	public void ShowSavingVideoFolderDialogButton_actionPerformed(ActionEvent e) {
		showSaveDialog("動画の保存先(フォルダ)", VideoSavedFolderField, true, true);
	}

	/**
	 * This method initializes ConvertingSettingPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getConvertingSettingPanel() {
		if (ConvertingSettingPanel == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.weighty = 1.0;
			gridBagConstraints11.weightx = 1.0;
			gridBagConstraints11.insets = new Insets(0, 5, 0, 5);
			gridBagConstraints11.gridy = 1;
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.anchor = GridBagConstraints.NORTH;
			gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints gridBagConstraints = new GridBagConstraints(0,
					2, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH,
					GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = GridBagConstraints.CENTER;
			gridBagConstraints.weighty = 0.0;
			gridBagConstraints.gridy = 0;
			ConvertingSettingPanel = new JPanel();
			ConvertingSettingPanel.setLayout(new GridBagLayout());
			ConvertingSettingPanel.add(getNGWordSettingPanel(),
					gridBagConstraints11);
			ConvertingSettingPanel.add(VhookSettingPanel, gridBagConstraints);
		}
		return ConvertingSettingPanel;
	}

	/**
	 * This method initializes NGWordSettingPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getNGWordSettingPanel() {
		if (NGWordSettingPanel == null) {
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridy = 1;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.insets = new Insets(0, 5, 5, 5);
			gridBagConstraints5.gridx = 1;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.insets = new Insets(0, 5, 5, 0);
			gridBagConstraints4.gridy = 1;
			NGIDLabel = new JLabel();
			NGIDLabel.setText("NG ID");
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.gridy = 0;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.insets = new Insets(0, 5, 5, 5);
			gridBagConstraints3.gridx = 1;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.insets = new Insets(0, 5, 5, 0);
			gridBagConstraints2.gridy = 0;
			NGWordLavel = new JLabel();
			NGWordLavel.setText("NGワード");
			NGWordSettingPanel = new JPanel();
			NGWordSettingPanel.setLayout(new GridBagLayout());
			NGWordSettingPanel.setBorder(BorderFactory.createTitledBorder(null,
					"NGワード・ID設定"));
			NGWordSettingPanel.add(NGWordLavel, gridBagConstraints2);
			NGWordSettingPanel.add(getNGWordTextField(), gridBagConstraints3);
			NGWordSettingPanel.add(NGIDLabel, gridBagConstraints4);
			NGWordSettingPanel.add(getNGIDTextField(), gridBagConstraints5);
		}
		return NGWordSettingPanel;
	}

	/**
	 * This method initializes NGWordTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getNGWordTextField() {
		if (NGWordTextField == null) {
			NGWordTextField = new JTextField();
		}
		return NGWordTextField;
	}

	/**
	 * This method initializes NGIDTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getNGIDTextField() {
		if (NGIDTextField == null) {
			NGIDTextField = new JTextField();
		}
		return NGIDTextField;
	}

	/**
	 * This method initializes ProxyInfoPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getProxyInfoPanel() {
		if (ProxyInfoPanel == null) {
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.gridx = 0;
			gridBagConstraints13.gridwidth = 2;
			gridBagConstraints13.weightx = 1.0;
			gridBagConstraints13.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints13.insets = new Insets(0, 5, 0, 5);
			gridBagConstraints13.gridy = 0;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints12.gridy = 2;
			gridBagConstraints12.weightx = 1.0;
			gridBagConstraints12.insets = new Insets(5, 0, 5, 5);
			gridBagConstraints12.gridx = 1;
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints10.gridy = 2;
			ProxyPortLabel = new JLabel();
			ProxyPortLabel.setText("ポート番号");
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.fill = GridBagConstraints.BOTH;
			gridBagConstraints9.gridy = 1;
			gridBagConstraints9.weightx = 1.0;
			gridBagConstraints9.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints9.gridx = 1;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.insets = new Insets(0, 5, 0, 5);
			gridBagConstraints8.fill = GridBagConstraints.NONE;
			gridBagConstraints8.anchor = GridBagConstraints.EAST;
			gridBagConstraints8.gridy = 1;
			ProxyLabel = new JLabel();
			ProxyLabel.setText("プロキシ");
			ProxyInfoPanel = new JPanel();
			ProxyInfoPanel.setLayout(new GridBagLayout());
			ProxyInfoPanel.setBorder(BorderFactory.createTitledBorder(null,
					"プロキシ設定"));
			ProxyInfoPanel.add(ProxyLabel, gridBagConstraints8);
			ProxyInfoPanel.add(getProxyTextField(), gridBagConstraints9);
			ProxyInfoPanel.add(ProxyPortLabel, gridBagConstraints10);
			ProxyInfoPanel.add(getProxyPortTextField(), gridBagConstraints12);
			ProxyInfoPanel.add(getUseProxyCheckBox(), gridBagConstraints13);
		}
		return ProxyInfoPanel;
	}

	/**
	 * This method initializes ProxyTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getProxyTextField() {
		if (ProxyTextField == null) {
			ProxyTextField = new JTextField();
		}
		return ProxyTextField;
	}

	/**
	 * This method initializes ProxyPortTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getProxyPortTextField() {
		if (ProxyPortTextField == null) {
			ProxyPortTextField = new JTextField();
		}
		return ProxyPortTextField;
	}

	/**
	 * This method initializes UseProxyCheckBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getUseProxyCheckBox() {
		if (UseProxyCheckBox == null) {
			UseProxyCheckBox = new JCheckBox();
			UseProxyCheckBox.setText("プロキシを使う");
		}
		return UseProxyCheckBox;
	}

	/**
	 * This method initializes FixFontSizeCheckBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getFixFontSizeCheckBox() {
		if (FixFontSizeCheckBox == null) {
			FixFontSizeCheckBox = new JCheckBox();
			FixFontSizeCheckBox.setText("フォントサイズを画面にあわせて自動調整する");
		}
		return FixFontSizeCheckBox;
	}

	/**
	 * This method initializes DelVideoCheckBoc
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getDelVideoCheckBox() {
		if (DelVideoCheckBox == null) {
			DelVideoCheckBox = new JCheckBox();
			DelVideoCheckBox.setText("変換後に動画ファイルを削除する");
		}
		return DelVideoCheckBox;
	}

	/**
	 * This method initializes DelCommentCheckBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getDelCommentCheckBox() {
		if (DelCommentCheckBox == null) {
			DelCommentCheckBox = new JCheckBox();
			DelCommentCheckBox.setText("変換後にコメントファイルを削除する");
		}
		return DelCommentCheckBox;
	}

	/**
	 * This method initializes FixCommentNumCheckBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getFixCommentNumCheckBox() {
		if (FixCommentNumCheckBox == null) {
			FixCommentNumCheckBox = new JCheckBox();
			FixCommentNumCheckBox.setText("コメント取得数は自動で調整する");
		}
		return FixCommentNumCheckBox;
	}

	/**
	 * This method initializes OpaqueCommentCheckBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getOpaqueCommentCheckBox() {
		if (OpaqueCommentCheckBox == null) {
			OpaqueCommentCheckBox = new JCheckBox();
			OpaqueCommentCheckBox.setText("全てのコメントを不透明にする");
		}
		return OpaqueCommentCheckBox;
	}

	/**
	 * This method initializes VideoSaveInfoPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getVideoSaveInfoPanel() {
		if (VideoSaveInfoPanel == null) {
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints15.gridwidth = 4;
			gridBagConstraints15.gridx = 0;
			gridBagConstraints15.gridy = 1;
			gridBagConstraints15.weightx = 1.0;
			gridBagConstraints15.insets = new Insets(0, 25, 0, 5);
			GridBagConstraints gridBagConstraints32 = new GridBagConstraints(3,
					4, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0);
			gridBagConstraints32.gridx = 3;
			gridBagConstraints32.insets = new Insets(0, 0, 5, 5);
			gridBagConstraints32.gridy = 5;
			GridBagConstraints gridBagConstraints30 = new GridBagConstraints(0,
					4, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 50, 0, 5), 0, 0);
			gridBagConstraints30.gridx = 0;
			gridBagConstraints30.insets = new Insets(0, 50, 5, 5);
			gridBagConstraints30.gridy = 5;
			GridBagConstraints gridBagConstraints29 = new GridBagConstraints(0,
					3, 4, 1, 1.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 25, 0, 5), 0, 0);
			gridBagConstraints29.gridx = 0;
			gridBagConstraints29.gridy = 4;
			GridBagConstraints gridBagConstraints28 = new GridBagConstraints(0,
					2, 3, 1, 1.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.BOTH, new Insets(0, 50, 0, 5), 0, 0);
			gridBagConstraints28.gridx = 0;
			gridBagConstraints28.gridy = 3;
			GridBagConstraints gridBagConstraints27 = new GridBagConstraints(0,
					1, 4, 1, 1.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(0, 25, 0, 5), 0,
					0);
			gridBagConstraints27.gridx = 0;
			gridBagConstraints27.gridy = 2;
			GridBagConstraints gridBagConstraints34 = new GridBagConstraints();
			gridBagConstraints34.insets = new Insets(0, 5, 0, 5);
			gridBagConstraints34.gridy = 0;
			gridBagConstraints34.weightx = 1.0;
			gridBagConstraints34.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints34.gridx = 0;
			GridBagConstraints gridBagConstraints31 = new GridBagConstraints(3,
					2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0);
			gridBagConstraints31.gridy = 3;
			gridBagConstraints31.gridx = 3;
			VideoSaveInfoPanel = new JPanel();
			VideoSaveInfoPanel.setLayout(new GridBagLayout());
			VideoSaveInfoPanel.setBorder(BorderFactory.createTitledBorder(null,
					"動画保存設定", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, new Font("MS UI Gothic",
							Font.PLAIN, 12), Color.black));
			VideoSaveInfoPanel.add(SavingVideoCheckBox, gridBagConstraints34);
			VideoSaveInfoPanel.add(Video_SaveFolderRadioButton,
					gridBagConstraints27);
			VideoSaveInfoPanel.add(ShowSavingVideoFolderDialogButton,
					gridBagConstraints31);
			VideoSaveInfoPanel.add(VideoSavedFolderField, gridBagConstraints28);
			VideoSaveInfoPanel.add(Video_SaveFileRadioButton,
					gridBagConstraints29);
			VideoSaveInfoPanel.add(VideoSavedFileField, gridBagConstraints30);
			VideoSaveInfoPanel.add(ShowSavingVideoFileDialogButton,
					gridBagConstraints32);
			VideoSaveInfoPanel.add(getDelVideoCheckBox(), gridBagConstraints15);
		}
		return VideoSaveInfoPanel;
	}

	/**
	 * This method initializes SaveInfoTabPaneEach
	 * 
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getSaveInfoTabPaneEach() {
		if (SaveInfoTabPaneEach == null) {
			SaveInfoTabPaneEach = new JTabbedPane();
			SaveInfoTabPaneEach.addTab("動画・コメント", null,
					getVideoSavingTabbedPanel(), null);
			SaveInfoTabPaneEach.addTab("コメント付き動画", null,
					getConvertedVideoSavingTabbedPanel(), null);
		}
		return SaveInfoTabPaneEach;
	}

	/**
	 * This method initializes VideoSavingTabbedPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getVideoSavingTabbedPanel() {
		if (VideoSavingTabbedPanel == null) {
			GridBagConstraints gridBagConstraints36 = new GridBagConstraints();
			gridBagConstraints36.weighty = 0.0;
			gridBagConstraints36.weightx = 1.0;
			gridBagConstraints36.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints36.insets = new Insets(0, 5, 0, 5);
			gridBagConstraints36.anchor = GridBagConstraints.NORTH;
			GridBagConstraints gridBagConstraints37 = new GridBagConstraints(0,
					1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0);
			gridBagConstraints37.anchor = GridBagConstraints.NORTH;
			gridBagConstraints37.gridx = 0;
			gridBagConstraints37.gridy = 1;
			gridBagConstraints37.weighty = 1.0;
			gridBagConstraints37.fill = GridBagConstraints.HORIZONTAL;
			VideoSavingTabbedPanel = new JPanel();
			VideoSavingTabbedPanel.setLayout(new GridBagLayout());
			VideoSavingTabbedPanel.add(getVideoSaveInfoPanel(),
					gridBagConstraints36);
			VideoSavingTabbedPanel.add(CommentSaveInfoPanel,
					gridBagConstraints37);
		}
		return VideoSavingTabbedPanel;
	}

	/**
	 * This method initializes ConvertedVideoSavingTabbedPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getConvertedVideoSavingTabbedPanel() {
		if (ConvertedVideoSavingTabbedPanel == null) {
			GridBagConstraints gridBagConstraints38 = new GridBagConstraints(0,
					2, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH,
					GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0);
			gridBagConstraints38.gridx = -1;
			gridBagConstraints38.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints38.gridy = -1;
			ConvertedVideoSavingTabbedPanel = new JPanel();
			ConvertedVideoSavingTabbedPanel.setLayout(new GridBagLayout());
			ConvertedVideoSavingTabbedPanel.add(ConvertedVideoSavingInfoPanel,
					gridBagConstraints38);
		}
		return ConvertedVideoSavingTabbedPanel;
	}

	/**
	 * This method initializes NotAddVideoID_ConvVideoCheckBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getNotAddVideoID_ConvVideoCheckBox() {
		if (NotAddVideoID_ConvVideoCheckBox == null) {
			NotAddVideoID_ConvVideoCheckBox = new JCheckBox();
			NotAddVideoID_ConvVideoCheckBox.setText("ファイル名に動画IDを付加しない");
		}
		return NotAddVideoID_ConvVideoCheckBox;
	}

	/**
	 * This method initializes FFmpegOptionComboBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private final FFmpegComboBoxModel FFmpegOptionModel = new FFmpegComboBoxModel();

	private JLabel ExtOptionLabel = null;

	private JTextField ExtOptionField = null;

	private JCheckBox NotUseVhookCheckBox = null;

	private JTextField ViewCommentField = null;

	private JLabel ViewCommentLabel = null;

	private JLabel ShadowKindLabel = null;

	private JComboBox ShadowComboBox = null;

	private JComboBox getFFmpegOptionComboBox() {
		if (FFmpegOptionComboBox == null) {
			FFmpegOptionComboBox = new JComboBox(FFmpegOptionModel);
			FFmpegOptionComboBox
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							if (FFmpegOptionModel.isFile()) {// ファイル
								ExtOptionField.setEnabled(false);
								MainOptionField.setEnabled(false);
								CommandLineInOptionField.setEnabled(false);
								CommandLineOutOptionField.setEnabled(false);
							} else {// ファイルでない
								ExtOptionField.setEnabled(true);
								MainOptionField.setEnabled(true);
								CommandLineInOptionField.setEnabled(true);
								CommandLineOutOptionField.setEnabled(true);
							}
						}
					});
		}
		return FFmpegOptionComboBox;
	}

	/**
	 * This method initializes FFmpegOptionReloadButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getFFmpegOptionReloadButton() {
		if (FFmpegOptionReloadButton == null) {
			FFmpegOptionReloadButton = new JButton();
			FFmpegOptionReloadButton.setText("更新");
			FFmpegOptionReloadButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							FFmpegOptionModel.reload();
						}
					});
		}
		return FFmpegOptionReloadButton;
	}

	/**
	 * This method initializes FFmpegOptionComboBoxPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getFFmpegOptionComboBoxPanel() {
		if (FFmpegOptionComboBoxPanel == null) {
			GridBagConstraints gridBagConstraints47 = new GridBagConstraints();
			gridBagConstraints47.fill = GridBagConstraints.BOTH;
			gridBagConstraints47.gridx = -1;
			gridBagConstraints47.gridy = -1;
			gridBagConstraints47.insets = new Insets(0, 0, 5, 5);
			GridBagConstraints gridBagConstraints46 = new GridBagConstraints();
			gridBagConstraints46.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints46.gridwidth = 3;
			gridBagConstraints46.gridx = -1;
			gridBagConstraints46.gridy = -1;
			gridBagConstraints46.weightx = 1.0;
			gridBagConstraints46.insets = new Insets(0, 5, 5, 5);
			FFmpegOptionComboBoxPanel = new JPanel();
			FFmpegOptionComboBoxPanel.setLayout(new GridBagLayout());
			FFmpegOptionComboBoxPanel.add(getFFmpegOptionComboBox(),
					gridBagConstraints46);
			FFmpegOptionComboBoxPanel.add(getFFmpegOptionReloadButton(),
					gridBagConstraints47);
		}
		return FFmpegOptionComboBoxPanel;
	}

	/**
	 * This method initializes ExtOptionField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getExtOptionField() {
		if (ExtOptionField == null) {
			ExtOptionField = new JTextField();
		}
		return ExtOptionField;
	}

	/**
	 * This method initializes NotUseVhookCheckBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getNotUseVhookCheckBox() {
		if (NotUseVhookCheckBox == null) {
			NotUseVhookCheckBox = new JCheckBox();
			NotUseVhookCheckBox.setText("拡張vhookライブラリを無効にする（デバッグ用）");
		}
		return NotUseVhookCheckBox;
	}

	/**
	 * This method initializes ViewCommentField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getViewCommentField() {
		if (ViewCommentField == null) {
			ViewCommentField = new JTextField();
		}
		return ViewCommentField;
	}

	/**
	 * This method initializes ShadowComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */

	private JComboBox getShadowComboBox() {
		if (ShadowComboBox == null) {
			ShadowComboBox = new JComboBox(ConvertingSetting.ShadowKindArray);
		}
		return ShadowComboBox;
	}

}

class MainFrame_ShowSavingVideoFolderDialogButton_actionAdapter implements
		ActionListener {
	private MainFrame adaptee;

	MainFrame_ShowSavingVideoFolderDialogButton_actionAdapter(MainFrame adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.ShowSavingVideoFolderDialogButton_actionPerformed(e);
	}
}

class MainFrame_ShowSavingCommentFolderDialogButton_actionAdapter implements
		ActionListener {
	private MainFrame adaptee;

	MainFrame_ShowSavingCommentFolderDialogButton_actionAdapter(
			MainFrame adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.ShowSavingCommentFolderDialogButton_actionPerformed(e);
	}
}

class MainFrame_ShowSavingConvertedVideoFolderDialogButton_actionAdapter
		implements ActionListener {
	private MainFrame adaptee;

	MainFrame_ShowSavingConvertedVideoFolderDialogButton_actionAdapter(
			MainFrame adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.ShowSavingConvertedVideoFolderDialogButton_actionPerformed(e);
	}
}

class MainFrame_this_windowAdapter extends WindowAdapter {
	private MainFrame adaptee;

	MainFrame_this_windowAdapter(MainFrame adaptee) {
		this.adaptee = adaptee;
	}

	public void windowClosing(WindowEvent e) {
		adaptee.this_windowClosing(e);
	}
}

class MainFrame_SettingFontPathButton_actionAdapter implements ActionListener {
	private MainFrame adaptee;

	MainFrame_SettingFontPathButton_actionAdapter(MainFrame adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.SettingFontPathButton_actionPerformed(e);
	}
}

class MainFrame_SettingVhookPathButton_actionAdapter implements ActionListener {
	private MainFrame adaptee;

	MainFrame_SettingVhookPathButton_actionAdapter(MainFrame adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.SettingVhookPathButton_actionPerformed(e);
	}
}

class MainFrame_SettingFFmpegPathButton_actionAdapter implements ActionListener {
	private MainFrame adaptee;

	MainFrame_SettingFFmpegPathButton_actionAdapter(MainFrame adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.SettingFFmpegPathButton_actionPerformed(e);
	}
}

class MainFrame_ShowSavingConvertedVideoDialogButton_actionAdapter implements
		ActionListener {
	private MainFrame adaptee;

	MainFrame_ShowSavingConvertedVideoDialogButton_actionAdapter(
			MainFrame adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.ShowSavingConvertedVideoDialogButton_actionPerformed(e);
	}
}

class MainFrame_ShowSavingCommentDialogButton_actionAdapter implements
		ActionListener {
	private MainFrame adaptee;

	MainFrame_ShowSavingCommentDialogButton_actionAdapter(MainFrame adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.ShowSavingCommentDialogButton_actionPerformed(e);
	}
}

class MainFrame_ShowSavingVideoDialogButton_actionAdapter implements
		ActionListener {
	private MainFrame adaptee;

	MainFrame_ShowSavingVideoDialogButton_actionAdapter(MainFrame adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.ShowSavingVideoDialogButton_actionPerformed(e);
	}
}

class MainFrame_DoButton_actionAdapter implements ActionListener {
	private MainFrame adaptee;

	MainFrame_DoButton_actionAdapter(MainFrame adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.DoButton_actionPerformed(e);
	}
}

class MainFrame_jMenuFileExit_ActionAdapter implements ActionListener {
	MainFrame adaptee;

	MainFrame_jMenuFileExit_ActionAdapter(MainFrame adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent actionEvent) {
		adaptee.jMenuFileExit_actionPerformed(actionEvent);
	}
}

class MainFrame_jMenuHelpAbout_ActionAdapter implements ActionListener {
	MainFrame adaptee;

	MainFrame_jMenuHelpAbout_ActionAdapter(MainFrame adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent actionEvent) {
		adaptee.jMenuHelpAbout_actionPerformed(actionEvent);
	}
}
