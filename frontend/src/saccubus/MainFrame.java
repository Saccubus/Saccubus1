package saccubus;

import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import java.io.*;
import java.util.ArrayList;

import psi.lib.swing.PopupRightClick;
import saccubus.util.*;

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
	JLabel elapsedTimeBar = new JLabel();
	JLabel vhookInfoBar = new JLabel();
	JLabel infoBar = new JLabel();
	JTabbedPane MainTabbedPane = new JTabbedPane();
	JPanel SavingInfoTabPanel = new JPanel();
	JPanel FFMpegTabPanel = new JPanel();
	JPanel VideoInfoPanel = new JPanel();
	JPanel StatusPanel = new JPanel();
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
	JPanel OwnerCommentInfoPanel = new JPanel();
	GridBagLayout gridBagLayout4 = new GridBagLayout();
	JCheckBox SavingVideoCheckBox = new JCheckBox();
	JTextField VideoSavedFileField = new JTextField();
	JButton ShowSavingVideoFileDialogButton = new JButton();
	JCheckBox SavingCommentCheckBox = new JCheckBox();
	JTextField CommentSavedFileField = new JTextField();
	JCheckBox AddTimeStampToCommentCheckBox = new JCheckBox();
	JCheckBox SavingOwnerCommentCheckBox = new JCheckBox();
	JLabel OwnerCommentNoticeLabel1 = new JLabel();
	JButton ShowSavingCommentFileDialogButton = new JButton();
	JPanel ConvertedVideoSavingInfoPanel = new JPanel();
	GridBagLayout gridBagLayout5 = new GridBagLayout();
	JCheckBox SavingConvertedVideoCheckBox = new JCheckBox();
	JCheckBox ConvertWithCommentCheckBox = new JCheckBox();
	JCheckBox ConvertWithOwnerCommentCheckBox = new JCheckBox();
	JCheckBox AddOption_ConvVideoFileCheckBox = new JCheckBox();
	JTextField ConvertedVideoSavedFileField = new JTextField();
	JButton ShowSavingConvertedVideoFileDialogButton = new JButton();
	GridBagLayout gridBagLayout6 = new GridBagLayout();
	ButtonGroup VideoSaveButtonGroup = new ButtonGroup();
	ButtonGroup CommentSaveButtonGroup = new ButtonGroup();
	ButtonGroup ConvSaveButtonGroup = new ButtonGroup();
	JPanel CheckFFmpegFunctionPanel = new JPanel();
	JButton CheckFFmpegVersionButton = new JButton();
	JLabel CheckFFmpegVersionLabel = new JLabel();
	JTextArea TextFFmpegOutput = new JTextArea();
	JButton CheckDownloadVideoButton = new JButton();
	JLabel CheckDownloadVideoLabel = new JLabel();
	JPanel BrowserInfoPanel = new JPanel();
	JLabel BrowserInfoLabel = new JLabel();
	JCheckBox BrowserIECheckBox = new JCheckBox();
	JCheckBox BrowserFFCheckBox = new JCheckBox();
	JCheckBox BrowserChromeCheckBox = new JCheckBox();
	JCheckBox BrowserOperaCheckBox = new JCheckBox();
	JCheckBox BrowserChromiumCheckBox = new JCheckBox();
	JCheckBox BrowserOtherCheckBox = new JCheckBox();
	JButton BrowserCookieDialogButton = new JButton();
	JTextField BrowserCookieField = new JTextField();


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
		GridBagConstraints grid8_x1_y6_73 = new GridBagConstraints();
		grid8_x1_y6_73.fill = GridBagConstraints.HORIZONTAL;
		grid8_x1_y6_73.gridy = 6;
		grid8_x1_y6_73.weightx = 1.0;
		grid8_x1_y6_73.gridwidth = 4;
		grid8_x1_y6_73.insets = new Insets(0, 0, 0, 5);
		grid8_x1_y6_73.gridx = 1;
		GridBagConstraints grid8_x0_y6_72 = new GridBagConstraints();
		grid8_x0_y6_72.gridx = 0;
		grid8_x0_y6_72.anchor = GridBagConstraints.WEST;
		grid8_x0_y6_72.insets = new Insets(0, 5, 0, 5);
		grid8_x0_y6_72.fill = GridBagConstraints.NONE;
		grid8_x0_y6_72.gridwidth = 1;
		grid8_x0_y6_72.gridy = 6;
		ShadowKindLabel = new JLabel();
		ShadowKindLabel.setText("影の種類");
		ShadowKindLabel.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
		GridBagConstraints grid1_x1_y0_71 = new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 6);
		grid1_x1_y0_71.fill = GridBagConstraints.BOTH;
		grid1_x1_y0_71.ipady = 0;
		GridBagConstraints grid10_x1_y1_70 = new GridBagConstraints();
		grid10_x1_y1_70.fill = GridBagConstraints.HORIZONTAL;
		grid10_x1_y1_70.gridy = 1;
		grid10_x1_y1_70.ipadx = 0;
		grid10_x1_y1_70.ipady = 0;
		grid10_x1_y1_70.weightx = 1.0;
		grid10_x1_y1_70.insets = new Insets(0, 0, 0, 0);
		grid10_x1_y1_70.gridx = 1;
		GridBagConstraints grid10_x0_y1_69 = new GridBagConstraints();
		grid10_x0_y1_69.gridx = 0;
		grid10_x0_y1_69.ipadx = 0;
		grid10_x0_y1_69.ipady = 0;
		grid10_x0_y1_69.insets = new Insets(0, 5, 0, 5);
		grid10_x0_y1_69.anchor = GridBagConstraints.WEST;
		grid10_x0_y1_69.gridy = 1;
		GridBagConstraints grid10_x1_y0_68 = new GridBagConstraints();
		grid10_x1_y0_68.fill = GridBagConstraints.BOTH;
		grid10_x1_y0_68.gridy = 0;
		grid10_x1_y0_68.ipady = 0;
		grid10_x1_y0_68.weightx = 1.0;
		grid10_x1_y0_68.insets = new Insets(0, 0, 0, 0);
		grid10_x1_y0_68.gridx = 1;
		GridBagConstraints grid10_x0_y0_67 = new GridBagConstraints();
		grid10_x0_y0_67.gridx = 0;
		grid10_x0_y0_67.ipadx = 0;
		grid10_x0_y0_67.ipady = 0;
		grid10_x0_y0_67.insets = new Insets(0, 5, 0, 5);
		grid10_x0_y0_67.anchor = GridBagConstraints.WEST;
		grid10_x0_y0_67.gridy = 0;
		GridBagConstraints grid8_x0_y1_66 = new GridBagConstraints();
		grid8_x0_y1_66.gridx = 0;
		grid8_x0_y1_66.insets = new Insets(0, 5, 5, 5);
		grid8_x0_y1_66.anchor = GridBagConstraints.WEST;
		grid8_x0_y1_66.gridwidth = 2;
		grid8_x0_y1_66.gridy = 1;
		ViewCommentLabel = new JLabel();
		ViewCommentLabel.setText("表示コメント数");
		GridBagConstraints grid8_x3_y1_65 = new GridBagConstraints();
		grid8_x3_y1_65.fill = GridBagConstraints.HORIZONTAL;
		grid8_x3_y1_65.gridy = 1;
		grid8_x3_y1_65.weightx = 1.0;
		grid8_x3_y1_65.gridwidth = 6;
		grid8_x3_y1_65.insets = new Insets(0, 5, 5, 5);
		grid8_x3_y1_65.gridx = 3;
		GridBagConstraints grid8_x4_y3_64 = new GridBagConstraints(1, 1,
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0);
		grid8_x4_y3_64.gridy = 3;
		grid8_x4_y3_64.fill = GridBagConstraints.HORIZONTAL;
		grid8_x4_y3_64.gridx = 4;
		GridBagConstraints grid8_x0_y7_63 = new GridBagConstraints(0, 4,
				2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0);
		grid8_x0_y7_63.gridy = 7;
		grid8_x0_y7_63.gridx = 0;
		grid8_x0_y7_63.gridwidth = 5;
		GridBagConstraints grid8_x1_y5_62 = new GridBagConstraints(0, 3,
				2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0);
		grid8_x1_y5_62.gridy = 5;
		grid8_x1_y5_62.gridx = 1;
		grid8_x1_y5_62.fill = GridBagConstraints.HORIZONTAL;
		grid8_x1_y5_62.insets = new Insets(0, 0, 5, 5);
		grid8_x1_y5_62.gridwidth = 4;
		GridBagConstraints grid8_x0_y5_61 = new GridBagConstraints(0, 2,
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0);
		grid8_x0_y5_61.gridy = 5;
		grid8_x0_y5_61.gridx = 0;
		grid8_x0_y5_61.fill = GridBagConstraints.VERTICAL;
		grid8_x0_y5_61.anchor = GridBagConstraints.WEST;
		grid8_x0_y5_61.gridwidth = 1;
		GridBagConstraints grid8_x1_y3_60 = new GridBagConstraints(0, 1,
				1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0);
		grid8_x1_y3_60.gridy = 3;
		grid8_x1_y3_60.gridx = 1;
		grid8_x1_y3_60.fill = GridBagConstraints.HORIZONTAL;
		grid8_x1_y3_60.insets = new Insets(0, 0, 0, 5);
		grid8_x1_y3_60.gridwidth = 3;
		GridBagConstraints grid8_x0_y3_59 = new GridBagConstraints(0, 0,
				2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0);
		grid8_x0_y3_59.gridy = 3;
		grid8_x0_y3_59.insets = new Insets(5, 5, 5, 5);
		grid8_x0_y3_59.gridx = 0;
		grid8_x0_y3_59.fill = GridBagConstraints.NONE;
		grid8_x0_y3_59.anchor = GridBagConstraints.WEST;
		grid8_x0_y3_59.weightx = 0.0;
		grid8_x0_y3_59.gridwidth = 1;
		GridBagConstraints grid8_x0_y0_58 = new GridBagConstraints();
		grid8_x0_y0_58.gridx = 0;
		grid8_x0_y0_58.anchor = GridBagConstraints.WEST;
		grid8_x0_y0_58.insets = new Insets(0, 5, 5, 5);
		grid8_x0_y0_58.gridwidth = 5;
		grid8_x0_y0_58.weightx = 1.0;
		grid8_x0_y0_58.fill = GridBagConstraints.HORIZONTAL;
		grid8_x0_y0_58.gridy = 0;
		GridBagConstraints grid9_x1_y2_57 = new GridBagConstraints();
		grid9_x1_y2_57.fill = GridBagConstraints.BOTH;
		grid9_x1_y2_57.gridy = 2;
		grid9_x1_y2_57.weightx = 1.0;
		grid9_x1_y2_57.insets = new Insets(0, 0, 5, 5);
		grid9_x1_y2_57.gridx = 1;
		GridBagConstraints grid9_x0_y2_56 = new GridBagConstraints();
		grid9_x0_y2_56.gridx = 0;
		grid9_x0_y2_56.insets = new Insets(0, 5, 5, 5);
		grid9_x0_y2_56.anchor = GridBagConstraints.WEST;
		grid9_x0_y2_56.gridy = 2;
		ExtOptionLabel = new JLabel();
		ExtOptionLabel.setText("出力の拡張子");
		GridBagConstraints grid9_x0_y1_55 = new GridBagConstraints();
		grid9_x0_y1_55.gridx = 0;
		grid9_x0_y1_55.fill = GridBagConstraints.HORIZONTAL;
		grid9_x0_y1_55.weightx = 1.0;
		grid9_x0_y1_55.gridwidth = 2;
		grid9_x0_y1_55.gridy = 1;
		GridBagConstraints grid9_x1_y5_53 = new GridBagConstraints(1, 3,
				2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0);
		grid9_x1_y5_53.gridy = 5;
		grid9_x1_y5_53.gridheight = 1;
		grid9_x1_y5_53.weightx = 1.0;
		grid9_x1_y5_53.gridwidth = 1;
		GridBagConstraints grid9_x1_y4_52 = new GridBagConstraints(2, 2,
				1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0);
		grid9_x1_y4_52.gridy = 4;
		grid9_x1_y4_52.gridwidth = 1;
		grid9_x1_y4_52.weightx = 1.0;
		grid9_x1_y4_52.gridx = 1;
		GridBagConstraints grid9_x1_y3_51 = new GridBagConstraints(2, 1,
				1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0);
		grid9_x1_y3_51.gridy = 3;
		grid9_x1_y3_51.gridwidth = 1;
		grid9_x1_y3_51.weightx = 1.0;
		grid9_x1_y3_51.gridx = 1;
		GridBagConstraints grid9_x0_y5_50 = new GridBagConstraints(0, 3,
				1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0);
		grid9_x0_y5_50.gridy = 5;
		GridBagConstraints grid9_x0_y4_49 = new GridBagConstraints(0, 2,
				2, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0);
		grid9_x0_y4_49.gridy = 4;
		grid9_x0_y4_49.gridwidth = 1;
		GridBagConstraints grid9_x0_y3_48 = new GridBagConstraints(0, 1,
				1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0);
		grid9_x0_y3_48.gridy = 3;
		GridBagConstraints grid5_x3_y8_45 = new GridBagConstraints(3, 5,
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0);
		grid5_x3_y8_45.gridy = 8;
		GridBagConstraints grid5_x3_y6_44 = new GridBagConstraints(3, 3,
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0);
		grid5_x3_y6_44.gridy = 6;
		GridBagConstraints grid5_x0_y8_43 = new GridBagConstraints(0, 5,
				3, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(3, 50, 5, 5), 0, 0);
		grid5_x0_y8_43.gridy = 8;
		GridBagConstraints grid5_x0_y7_42 = new GridBagConstraints(0, 4,
				4, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 25, 0, 5), 0, 0);
		grid5_x0_y7_42.gridy = 7;
		GridBagConstraints grid5_x0_y6_41 = new GridBagConstraints(0, 3,
				3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 50, 0, 5), 0, 0);
		grid5_x0_y6_41.gridy = 6;
		GridBagConstraints gird5_x0_y5_89 = new GridBagConstraints();
		gird5_x0_y5_89.gridx = 0;
		gird5_x0_y5_89.gridy = 5;
		gird5_x0_y5_89.gridwidth = 4;
		gird5_x0_y5_89.anchor = GridBagConstraints.WEST;
		gird5_x0_y5_89.fill = GridBagConstraints.NONE;
		gird5_x0_y5_89.insets = new Insets(0, 50, 0, 5);
		GridBagConstraints grid5_x0_y4_39 = new GridBagConstraints();
		grid5_x0_y4_39.gridx = 0;
		grid5_x0_y4_39.insets = new Insets(0, 50, 0, 5);
		grid5_x0_y4_39.fill = GridBagConstraints.HORIZONTAL;
		grid5_x0_y4_39.weightx = 1.0;
		grid5_x0_y4_39.gridwidth = 4;
		grid5_x0_y4_39.gridy = 4;
		GridBagConstraints grid5_x0_y3_40 = new GridBagConstraints(0, 2,
				4, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 25, 0, 5), 0, 0);
		grid5_x0_y3_40.gridy = 3;
		GridBagConstraints grid5_x0_y2_81 = new GridBagConstraints();
		grid5_x0_y2_81.gridx = 0;
		grid5_x0_y2_81.gridy = 2;
		grid5_x0_y2_81.gridwidth = 3;
		grid5_x0_y2_81.gridheight = 1;
//		grid5_x0_y2_81.weightx = 1.0;
//		grid5_x0_y2_81.weighty = 0.0;
		grid5_x0_y2_81.anchor = GridBagConstraints.CENTER;
		grid5_x0_y2_81.fill = GridBagConstraints.BOTH;
		grid5_x0_y2_81.insets = new Insets(0, 25, 0, 5);
		GridBagConstraints grid5_x0_y1_78 = new GridBagConstraints();
		grid5_x0_y1_78.gridx = 0;
		grid5_x0_y1_78.gridy = 1;
		grid5_x0_y1_78.gridwidth = 3;
		grid5_x0_y1_78.gridheight = 1;
//		grid5_x0_y1_78.weightx = 1.0;
//		grid5_x0_y1_78.weighty = 0.0;
		grid5_x0_y1_78.anchor = GridBagConstraints.CENTER;
		grid5_x0_y1_78.fill = GridBagConstraints.BOTH;
		grid5_x0_y1_78.insets = new Insets(0, 25, 0, 5);
		GridBagConstraints grid2_x__y__35 = new GridBagConstraints();
		grid2_x__y__35.fill = GridBagConstraints.BOTH;
		grid2_x__y__35.weighty = 1.0;
		grid2_x__y__35.weightx = 1.0;
		GridBagConstraints grid8_x0_y9_33 = new GridBagConstraints();
		grid8_x0_y9_33.gridx = 0;
		grid8_x0_y9_33.fill = GridBagConstraints.HORIZONTAL;
		grid8_x0_y9_33.weightx = 1.0;
		grid8_x0_y9_33.insets = new Insets(0, 5, 5, 5);
		grid8_x0_y9_33.gridwidth = 5;
		grid8_x0_y9_33.gridy = 9;
		GridBagConstraints grid4_x0_y9_26 = new GridBagConstraints();
		grid4_x0_y9_26.gridx = 0;
		grid4_x0_y9_26.gridwidth = 4;
		grid4_x0_y9_26.insets = new Insets(0, 25, 0, 5);
		grid4_x0_y9_26.weightx = 1.0;
		grid4_x0_y9_26.fill = GridBagConstraints.HORIZONTAL;
		grid4_x0_y9_26.gridy = 9;
		GridBagConstraints grid4_x0_y8_25 = new GridBagConstraints();
		grid4_x0_y8_25.gridx = 0;
		grid4_x0_y8_25.gridwidth = 4;
		grid4_x0_y8_25.fill = GridBagConstraints.HORIZONTAL;
		grid4_x0_y8_25.insets = new Insets(0, 25, 0, 5);
		grid4_x0_y8_25.weightx = 1.0;
		grid4_x0_y8_25.gridy = 8;
		GridBagConstraints grid4_x3_y14_24 = new GridBagConstraints(3, 10,
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0);
		grid4_x3_y14_24.gridy = 14;
		GridBagConstraints grid4_x3_y12_23 = new GridBagConstraints(3, 8,
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0);
		grid4_x3_y12_23.gridy = 12;
		GridBagConstraints grid4_x1_y10_22 = new GridBagConstraints(1, 6,
				4, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0);
		grid4_x1_y10_22.gridy = 10;
		GridBagConstraints grid4_x0_y14_21 = new GridBagConstraints(0, 10,
				3, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 50, 5, 5), 0, 0);
		grid4_x0_y14_21.gridy = 14;
		GridBagConstraints grid4_x0_y13_20 = new GridBagConstraints(0, 9,
				4, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 25, 0, 5), 0, 0);
		grid4_x0_y13_20.gridy = 13;
		GridBagConstraints grid4_x0_y12_19 = new GridBagConstraints(0, 8,
				3, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 50, 0, 5), 0, 0);
		grid4_x0_y12_19.gridy = 12;
		GridBagConstraints grid4_x0_y11_18 = new GridBagConstraints(0, 7,
				4, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 25, 0, 5), 0, 0);
		grid4_x0_y11_18.gridy = 11;
		GridBagConstraints grid4_x0_y10_17 = new GridBagConstraints(0, 6,
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0);
		grid4_x0_y10_17.gridy = 10;
		grid4_x0_y10_17.insets = new Insets(0, 50, 0, 5);
		GridBagConstraints grid4_x0_y6_16 = new GridBagConstraints(0, 5,
				4, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0);
		grid4_x0_y6_16.gridy = 6;
		GridBagConstraints grid4_x0_y7_86 = new GridBagConstraints();
		grid4_x0_y7_86.gridx = 0;
		grid4_x0_y7_86.gridy = 7;
		grid4_x0_y7_86.gridwidth = 4;
		grid4_x0_y7_86.anchor = GridBagConstraints.WEST;
		grid4_x0_y7_86.fill = GridBagConstraints.HORIZONTAL;
		grid4_x0_y7_86.insets = new Insets(0, 25, 0, 5);
		GridBagConstraints grid11_x0_y0_75 = new GridBagConstraints();
		grid11_x0_y0_75.gridx = 0;
		grid11_x0_y0_75.gridy = 0;
		grid11_x0_y0_75.gridwidth = 4;
		grid11_x0_y0_75.gridheight = 1;
		grid11_x0_y0_75.weightx = 1.0;
		grid11_x0_y0_75.weighty = 0.0;
		grid11_x0_y0_75.anchor = GridBagConstraints.NORTHWEST;
		grid11_x0_y0_75.fill = GridBagConstraints.HORIZONTAL;
		grid11_x0_y0_75.insets = new Insets(0, 5, 5, 5);
		GridBagConstraints grid11_x0_y1_76 = new GridBagConstraints();
		grid11_x0_y1_76.gridx = 0;
		grid11_x0_y1_76.gridy = 1;
		grid11_x0_y1_76.gridwidth = 4;
		grid11_x0_y1_76.gridheight = 1;
		grid11_x0_y1_76.weightx = 1.0;
		grid11_x0_y1_76.weighty = 0.0;
		grid11_x0_y1_76.anchor = GridBagConstraints.NORTHWEST;
		grid11_x0_y1_76.fill = GridBagConstraints.HORIZONTAL;
		grid11_x0_y1_76.insets = new Insets(0, 25, 5, 5);
//		GridBagConstraints grid11_x0_y2_77 = new GridBagConstraints();
//		GridBagConstraints grid11_x0_y3_80 = new GridBagConstraints();
		GridBagConstraints grid8_x0_y8_14 = new GridBagConstraints();
		grid8_x0_y8_14.gridx = 0;
		grid8_x0_y8_14.anchor = GridBagConstraints.WEST;
		grid8_x0_y8_14.fill = GridBagConstraints.HORIZONTAL;
		grid8_x0_y8_14.weightx = 1.0;
		grid8_x0_y8_14.gridwidth = 5;
		grid8_x0_y8_14.insets = new Insets(0, 5, 0, 5);
		grid8_x0_y8_14.gridy = 8;
		GridBagConstraints grid12_x0_y0_7 = new GridBagConstraints(0, 0,
				1, 1, 1.0, 1.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0);
		grid12_x0_y0_7.weighty = 0.0;
		GridBagConstraints grid12_x0_y1_6 = new GridBagConstraints();
		grid12_x0_y1_6.gridx = 0;
		grid12_x0_y1_6.weighty = 0.0;
		grid12_x0_y1_6.weightx = 1.0;
		grid12_x0_y1_6.insets = new Insets(0, 5, 0, 5);
		grid12_x0_y1_6.fill = GridBagConstraints.HORIZONTAL;
		grid12_x0_y1_6.anchor = GridBagConstraints.NORTH;
		grid12_x0_y1_6.gridy = 1;
		GridBagConstraints grid12_x0_y2_95 = new GridBagConstraints();
		grid12_x0_y2_95.gridx = 0;
		grid12_x0_y2_95.gridy = 2;
		grid12_x0_y2_95.weightx = 1.0;
		grid12_x0_y2_95.weighty = 1.0;
		grid12_x0_y2_95.insets = new Insets(5, 5, 0, 5);
		grid12_x0_y2_95.fill = GridBagConstraints.HORIZONTAL;
		grid12_x0_y2_95.anchor = GridBagConstraints.NORTH;
		GridBagConstraints grid6_x0_y1_1 = new GridBagConstraints(0, 1,
				1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0);
		grid6_x0_y1_1.fill = GridBagConstraints.HORIZONTAL;
		grid6_x0_y1_1.anchor = GridBagConstraints.NORTH;
		grid6_x0_y1_1.weighty = 0.0;
		this.setIconImage(WinIcon);
		contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(borderLayout1);
		setSize(new Dimension(400, 625));
		/* setBounds(0, 50, 400, 620); */
		setTitle("さきゅばす " + saccubus.MainFrame_AboutBox.rev );
		this.addWindowListener(new MainFrame_this_windowAdapter(this));
		statusBar.setText(" ");
		elapsedTimeBar.setText(" ");
		elapsedTimeBar.setForeground(Color.blue);
		vhookInfoBar.setText(" ");
		vhookInfoBar.setForeground(Color.blue);
		infoBar.setText(" ");
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
		GridBagLayout gridBagLayout13 = new GridBagLayout();
		BrowserInfoPanel.setLayout(gridBagLayout13);
		BrowserInfoPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				"ブラウザ情報共有設定", TitledBorder.LEADING, TitledBorder.TOP,
				getFont(), Color.blue));
		BrowserInfoLabel.setText("ログイン済みブラウザからセッションを取得する（ブラウザはログアウトされない）");
		BrowserInfoLabel.setForeground(Color.blue);
		BrowserInfoLabel.setToolTipText("メールアドレス、パスワードは入力不要（指定ブラウザがログインしていないとエラー）");
		GridBagConstraints grid13_x0_y0_96 = new GridBagConstraints();
		grid13_x0_y0_96.gridx = 0;
		grid13_x0_y0_96.gridy = 0;
		grid13_x0_y0_96.gridwidth = 2;
		grid13_x0_y0_96.weightx = 1.0;
		grid13_x0_y0_96.anchor = GridBagConstraints.NORTH;
		grid13_x0_y0_96.fill = GridBagConstraints.HORIZONTAL;
		grid13_x0_y0_96.insets = new Insets(0, 5, 0, 5);
		BrowserInfoPanel.add(BrowserInfoLabel, grid13_x0_y0_96);
		BrowserIECheckBox.setText("Interner Eplorer (IE7/IE8/IE9)");
		BrowserIECheckBox.setForeground(Color.blue);
		GridBagConstraints grid13_x0_y1_97 = new GridBagConstraints();
		grid13_x0_y1_97.gridx = 0;
		grid13_x0_y1_97.gridy = 1;
		grid13_x0_y1_97.gridwidth = 2;
		grid13_x0_y1_97.weightx = 1.0;
		grid13_x0_y1_97.anchor = GridBagConstraints.NORTH;
		grid13_x0_y1_97.fill = GridBagConstraints.HORIZONTAL;
		grid13_x0_y1_97.insets = new Insets(0, 5, 0, 5);
		BrowserInfoPanel.add(BrowserIECheckBox, grid13_x0_y1_97);
		BrowserFFCheckBox.setText("Firefox (FF3/FF4/FF5)");
		BrowserFFCheckBox.setForeground(Color.blue);
		GridBagConstraints grid13_x0_y2_98 = new GridBagConstraints();
		grid13_x0_y2_98.gridx = 0;
		grid13_x0_y2_98.gridy = 2;
		grid13_x0_y2_98.gridwidth = 2;
		grid13_x0_y2_98.weightx = 1.0;
		grid13_x0_y2_98.anchor = GridBagConstraints.NORTH;
		grid13_x0_y2_98.fill = GridBagConstraints.HORIZONTAL;
		grid13_x0_y2_98.insets = new Insets(0, 5, 0, 5);
		BrowserInfoPanel.add(BrowserFFCheckBox, grid13_x0_y2_98);
		BrowserChromeCheckBox.setText("Google Chrome");
		BrowserChromeCheckBox.setForeground(Color.blue);
		GridBagConstraints grid13_x0_y3_99 = new GridBagConstraints();
		grid13_x0_y3_99.gridx = 0;
		grid13_x0_y3_99.gridy = 3;
		grid13_x0_y3_99.gridwidth = 2;
		grid13_x0_y3_99.weightx = 1.0;
		grid13_x0_y3_99.anchor = GridBagConstraints.NORTH;
		grid13_x0_y3_99.fill = GridBagConstraints.HORIZONTAL;
		grid13_x0_y3_99.insets = new Insets(0, 5, 0, 5);
		BrowserInfoPanel.add(BrowserChromeCheckBox, grid13_x0_y3_99);
		BrowserOperaCheckBox.setText("Opera");
		BrowserOperaCheckBox.setForeground(Color.blue);
		GridBagConstraints grid13_x0_y4_100 = new GridBagConstraints();
		grid13_x0_y4_100.gridx = 0;
		grid13_x0_y4_100.gridy = 4;
		grid13_x0_y4_100.gridwidth = 2;
		grid13_x0_y4_100.weightx = 1.0;
		grid13_x0_y4_100.anchor = GridBagConstraints.NORTH;
		grid13_x0_y4_100.fill = GridBagConstraints.HORIZONTAL;
		grid13_x0_y4_100.insets = new Insets(0, 5, 0, 5);
		BrowserInfoPanel.add(BrowserOperaCheckBox, grid13_x0_y4_100);
		BrowserChromiumCheckBox.setText("Chromium派生 (SRware Ironなど)");
		BrowserChromiumCheckBox.setForeground(Color.blue);
		GridBagConstraints grid13_x0_y5_101 = new GridBagConstraints();
		grid13_x0_y5_101.gridx = 0;
		grid13_x0_y5_101.gridy = 5;
		grid13_x0_y5_101.gridwidth = 2;
		grid13_x0_y5_101.weightx = 1.0;
		grid13_x0_y5_101.anchor = GridBagConstraints.NORTH;
		grid13_x0_y5_101.fill = GridBagConstraints.HORIZONTAL;
		grid13_x0_y5_101.insets = new Insets(0, 5, 0, 5);
		BrowserInfoPanel.add(BrowserChromiumCheckBox, grid13_x0_y5_101);
		BrowserOtherCheckBox.setText("上記以外のブラウザのCookieのファイル又はフォルダを指定");
		BrowserOtherCheckBox.setForeground(Color.blue);
		GridBagConstraints grid13_x0_y6_102 = new GridBagConstraints();
		grid13_x0_y6_102.gridx = 0;
		grid13_x0_y6_102.gridy = 6;
		grid13_x0_y6_102.gridwidth = 2;
		grid13_x0_y6_102.weightx = 1.0;
		grid13_x0_y6_102.anchor = GridBagConstraints.NORTH;
		grid13_x0_y6_102.fill = GridBagConstraints.HORIZONTAL;
		grid13_x0_y6_102.insets = new Insets(0, 5, 0, 5);
		BrowserInfoPanel.add(BrowserOtherCheckBox, grid13_x0_y6_102);
		BrowserCookieField.setText("－場所は自分で捜して下さい－");
		BrowserCookieField.setForeground(Color.blue);
		GridBagConstraints grid13_x0_y7_103 = new GridBagConstraints();
		grid13_x0_y7_103.gridx = 0;
		grid13_x0_y7_103.gridy = 7;
		grid13_x0_y7_103.gridwidth = 1;
		grid13_x0_y7_103.weightx = 1.0;
		grid13_x0_y7_103.anchor = GridBagConstraints.NORTH;
		grid13_x0_y7_103.fill = GridBagConstraints.HORIZONTAL;
		grid13_x0_y7_103.insets = new Insets(0, 25, 0, 5);
		BrowserInfoPanel.add(BrowserCookieField, grid13_x0_y7_103);
		BrowserCookieDialogButton.setText("参照");
		BrowserCookieDialogButton.setForeground(Color.blue);
		BrowserCookieDialogButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					showSaveDialog("他のブラウザのCookieへのパス", BrowserCookieField);
				}
			});
		GridBagConstraints grid13_x1_y7_104 = new GridBagConstraints();
		grid13_x1_y7_104.gridx = 1;
		grid13_x1_y7_104.gridy = 7;
		grid13_x1_y7_104.gridwidth = 1;
		grid13_x1_y7_104.weightx = 0.0;
		grid13_x1_y7_104.anchor = GridBagConstraints.SOUTH;
		grid13_x1_y7_104.fill = GridBagConstraints.NONE;
		grid13_x1_y7_104.insets = new Insets(0, 0, 0, 5);
		BrowserInfoPanel.add(BrowserCookieDialogButton, grid13_x1_y7_104);

		SavingVideoCheckBox.setText("動画をダウンロードする");
		ShowSavingVideoFileDialogButton.setText("参照");
		ShowSavingVideoFileDialogButton
				.addActionListener(new MainFrame_ShowSavingVideoDialogButton_actionAdapter(
						this));
		Video_SaveFolderRadioButton.setText("保存するフォルダを指定し、ファイル名は自動で決定する");
		ShowSavingVideoFolderDialogButton.setText("参照");
		ShowSavingVideoFolderDialogButton
				.addActionListener(new MainFrame_ShowSavingVideoFolderDialogButton_actionAdapter(
						this));
		Video_SaveFileRadioButton.setText("保存するファイル名を指定する");
		CommentSaveInfoPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				"コメント保存設定", TitledBorder.LEADING, TitledBorder.TOP, new Font(
						"MS UI Gothic", Font.PLAIN, 12), Color.black));
		CommentSaveInfoPanel.setLayout(gridBagLayout4);
		SavingCommentCheckBox.setText("コメントをダウンロードする");
		AddTimeStampToCommentCheckBox.setText("コメントファイル名に日時を付加する（フォルダを指定した時のみ）");
		AddTimeStampToCommentCheckBox.setForeground(Color.blue);
		AddTimeStampToCommentCheckBox.setToolTipText("過去ログにも現在のコメントにも日時が付く");
		ShowSavingCommentFileDialogButton.setText("参照");
		ShowSavingCommentFileDialogButton
				.addActionListener(new MainFrame_ShowSavingCommentDialogButton_actionAdapter(
						this));
		Comment_SaveFolderRadioButton.setText("保存するフォルダを指定し、ファイル名は自動で決定する");
		ShowSavingCommentFolderDialogButton.setText("参照");
		ShowSavingCommentFolderDialogButton
				.addActionListener(new MainFrame_ShowSavingCommentFolderDialogButton_actionAdapter(
						this));
		Comment_SaveFileRadioButton.setText("保存するファイル名を指定する");
		OwnerCommentInfoPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				"投稿者コメント保存設定", TitledBorder.LEADING, TitledBorder.TOP, new Font(
						"MS UI Gothic", Font.PLAIN, 12), Color.blue));
		OwnerCommentInfoPanel.setLayout(gridBagLayout11);
		SavingOwnerCommentCheckBox.setForeground(Color.blue);
		SavingOwnerCommentCheckBox.setText("投稿者コメントをダウンロードする　（取得数＝５００ 固定）");
		OwnerCommentNoticeLabel1.setText("（フォルダ指定は通常コメントの設定通り・変換後に削除しない）");
		OwnerCommentNoticeLabel1.setForeground(Color.blue);
		OwnerCommentNoticeLabel1.setToolTipText("ファイル名は、タイトル＋［Ｏｗｎｅｒ］．ｘｍｌ");
		ConvertedVideoSavingInfoPanel.setBorder(BorderFactory
				.createTitledBorder(BorderFactory.createEtchedBorder(),
						"コメント付き動画保存設定"));
		ConvertedVideoSavingInfoPanel.setLayout(gridBagLayout5);
		SavingConvertedVideoCheckBox.setText("動画を変換する");
		ConvertWithCommentCheckBox.setText("コメントを付加する");
		ConvertWithOwnerCommentCheckBox.setText("投稿者コメントを付加する");
		ConvertWithOwnerCommentCheckBox.setForeground(Color.blue);
		AddOption_ConvVideoFileCheckBox.setText("サブフォルダを作りFFmpeg設定をファイル名に（デバッグ用）");
		AddOption_ConvVideoFileCheckBox.setForeground(Color.blue);
		AddOption_ConvVideoFileCheckBox.setToolTipText("サブフォルダ名はビデオタイトル");
		ShowSavingConvertedVideoFileDialogButton.setText("参照");
		ShowSavingConvertedVideoFileDialogButton
				.addActionListener(new MainFrame_ShowSavingConvertedVideoDialogButton_actionAdapter(
						this));
		FFMpegTabPanel.setLayout(gridBagLayout6);
		PathSettingPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "プログラムの位置の設定"));
		PathSettingPanel.setLayout(gridBagLayout7);
		FFmpegPathLabel.setText("FFmpeg");
		CheckFFmpegVersionLabel.setText("バージョンを表示する⇒ ");
		CheckFFmpegVersionLabel.setForeground(Color.blue);
		CheckFFmpegVersionButton.setText("表示");
		CheckFFmpegVersionButton.setToolTipText("指定されたFFmpegのバージョンを表示する");
		CheckFFmpegVersionButton.setForeground(Color.blue);
		CheckFFmpegVersionButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e){
						FFVersionButton_actionPerformed(e);
					}
				});
		SettingFFmpegPathButton.setText("参照");
		SettingFFmpegPathButton
				.addActionListener(new MainFrame_SettingFFmpegPathButton_actionAdapter(
						this));
		VhookPathLabel.setText("拡張vhook 従来 ");
		UseVhookCheckBox.setText("使用する（デフォルト）");
		UseVhookCheckBox.setForeground(Color.blue);
		SettingVhookPathButton.setText("参照");
		SettingVhookPathButton
				.addActionListener(new MainFrame_SettingVhookPathButton_actionAdapter(
						this));
		VhookWidePathLabel.setText("拡張vhook ワイド ");
		VhookWidePathLabel.setForeground(Color.blue);
		UseVhookWideCheckBox.setText("使用する（オプション）");
		UseVhookWideCheckBox.setForeground(Color.blue);
		SettingVhookWidePathButton.setText("参照");
		SettingVhookWidePathButton
				.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						SettingVhookWidePathButton_actionPerformed(e);
					}
				});
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
		OpPanel.setLayout(gridBagLayout10);
		Conv_SaveFolderRadioButton.setText("保存するフォルダを指定し、ファイル名は自動で決定する");
		ShowSavingConvertedVideoFolderDialogButton.setText("参照");
		ShowSavingConvertedVideoFolderDialogButton
				.addActionListener(new MainFrame_ShowSavingConvertedVideoFolderDialogButton_actionAdapter(
						this));
		Conv_SaveFileRadioButton.setText("保存するファイル名を指定する");
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

		StatusPanel.setLayout(new BorderLayout());
		StatusPanel.add(elapsedTimeBar,BorderLayout.WEST);
		StatusPanel.add(vhookInfoBar, BorderLayout.EAST);
		StatusPanel.add(statusBar, BorderLayout.SOUTH);
		contentPane.add(StatusPanel, BorderLayout.SOUTH);
		contentPane.add(MainTabbedPane, BorderLayout.CENTER);
		contentPane.add(VideoInfoPanel, BorderLayout.NORTH);
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
		PathSettingPanel.add(FFmpegPathLabel, new GridBagConstraints(0, 0, 1,
				1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(0, 5, 0, 5), 0, 0));
		GridBagConstraints grid7_x1_y0_84 = new GridBagConstraints();
		grid7_x1_y0_84.gridx = 1;
		grid7_x1_y0_84.gridy = 0;
		grid7_x1_y0_84.weightx = 1.0;
		grid7_x1_y0_84.anchor = GridBagConstraints.EAST;
		grid7_x1_y0_84.insets = new Insets(0, 0, 0, 0);
		PathSettingPanel.add(CheckFFmpegVersionLabel, grid7_x1_y0_84);
		GridBagConstraints grid7_x2_y0_83 = new GridBagConstraints();
		grid7_x2_y0_83.gridx = 2;
		grid7_x2_y0_83.gridy = 0;
		grid7_x2_y0_83.weightx = 0.0;
		grid7_x2_y0_83.anchor = GridBagConstraints.WEST;
		grid7_x2_y0_83.fill = GridBagConstraints.HORIZONTAL;
		grid7_x2_y0_83.insets = new Insets(0, 5, 5, 5);
		PathSettingPanel.add(CheckFFmpegVersionButton, grid7_x2_y0_83);
		PathSettingPanel.add(FFmpegPathField, new GridBagConstraints(0, 1, 2,
				1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 5, 0, 0), 0, 0));
		GridBagConstraints grid7_x2_y1_74 = new GridBagConstraints(2, 1,
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0);
		PathSettingPanel.add(SettingFFmpegPathButton, grid7_x2_y1_74);
		GridBagConstraints grid7_x0_y2_54 = new GridBagConstraints(0, 2,
				2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0);
		grid7_x0_y2_54.gridwidth = 1;
		grid7_x0_y2_54.weightx = 0.0;
		PathSettingPanel.add(VhookPathLabel, grid7_x0_y2_54);
		GridBagConstraints grid7_x1_y2_90 = new GridBagConstraints();
		grid7_x1_y2_90.gridx = 1;
		grid7_x1_y2_90.gridy = 2;
		grid7_x1_y2_90.gridwidth = 2;
		grid7_x1_y2_90.weightx = 1.0;
		grid7_x1_y2_90.anchor = GridBagConstraints.NORTHWEST;
		grid7_x1_y2_90.fill = GridBagConstraints.HORIZONTAL;
		grid7_x1_y2_90.insets = new Insets(0, 5, 0, 5);
		PathSettingPanel.add(UseVhookCheckBox, grid7_x1_y2_90);
		PathSettingPanel.add(VhookPathField, new GridBagConstraints(0, 3, 2, 1,
				1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 5, 0, 0), 0, 0));
		PathSettingPanel.add(SettingVhookPathButton, new GridBagConstraints(2,
				3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
		GridBagConstraints grid7_x0_y4_91 = new GridBagConstraints();
		grid7_x0_y4_91.gridx = 0;
		grid7_x0_y4_91.gridy = 4;
		grid7_x0_y4_91.gridwidth = 1;
		grid7_x0_y4_91.gridheight = 1;
		grid7_x0_y4_91.weightx = 0.0;
		grid7_x0_y4_91.weighty = 0.0;
		grid7_x0_y4_91.anchor = GridBagConstraints.NORTHWEST;
		grid7_x0_y4_91.fill = GridBagConstraints.HORIZONTAL;
		grid7_x0_y4_91.insets = new Insets(5, 5, 0, 5);
		PathSettingPanel.add(VhookWidePathLabel, grid7_x0_y4_91);
		GridBagConstraints grid7_x1_y4_92 = new GridBagConstraints();
		grid7_x1_y4_92.gridx = 1;
		grid7_x1_y4_92.gridy = 4;
		grid7_x1_y4_92.gridwidth = 2;
		grid7_x1_y4_92.weightx = 1.0;
		grid7_x1_y4_92.anchor = GridBagConstraints.NORTHWEST;
		grid7_x1_y4_92.fill = GridBagConstraints.HORIZONTAL;
		grid7_x1_y4_92.insets = new Insets(0, 5, 0, 5);
		PathSettingPanel.add(UseVhookWideCheckBox, grid7_x1_y4_92);
		GridBagConstraints grid7_x0_y5_93 = new GridBagConstraints();
		grid7_x0_y5_93.gridx = 0;
		grid7_x0_y5_93.gridy = 5;
		grid7_x0_y5_93.gridwidth = 2;
		grid7_x0_y5_93.gridheight = 1;
		grid7_x0_y5_93.weightx = 1.0;
		grid7_x0_y5_93.weighty = 0.0;
		grid7_x0_y5_93.anchor = GridBagConstraints.NORTHWEST;
		grid7_x0_y5_93.fill = GridBagConstraints.HORIZONTAL;
		grid7_x0_y5_93.insets = new Insets(0, 5, 0, 0);
		PathSettingPanel.add(VhookWidePathField, grid7_x0_y5_93);
		GridBagConstraints grid7_x2_y5_94 = new GridBagConstraints();
		grid7_x2_y5_94.gridx = 2;
		grid7_x2_y5_94.gridy = 5;
		grid7_x2_y5_94.anchor = GridBagConstraints.NORTHWEST;
		grid7_x2_y5_94.fill = GridBagConstraints.HORIZONTAL;
		grid7_x2_y5_94.insets = new Insets(0, 5, 0, 5);
		PathSettingPanel.add(SettingVhookWidePathButton, grid7_x2_y5_94);
		FFmpegSettingPanel.add(getFFmpegOptionComboBoxPanel(),grid9_x0_y1_55);
		FFmpegSettingPanel.add(ExtOptionLabel, grid9_x0_y2_56);
		FFmpegSettingPanel.add(getExtOptionField(), grid9_x1_y2_57);
		FFmpegSettingPanel.add(MainOptionLabel, grid9_x0_y3_48);
		FFmpegSettingPanel.add(MainOptionField, grid9_x1_y3_51);
		FFmpegSettingPanel.add(InLabel, grid9_x0_y4_49);
		FFmpegSettingPanel.add(CommandLineInOptionField, grid9_x1_y4_52);
		FFmpegSettingPanel.add(OutLabel, grid9_x0_y5_50);
		FFmpegSettingPanel.add(CommandLineOutOptionField, grid9_x1_y5_53);
		VideoInfoPanel.add(OpPanel, new GridBagConstraints(0, 0, 1, 1, 1.0,
				0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		VideoInfoPanel.add(DoButton, grid1_x1_y0_71);
		OpPanel.add(VideoID_Label, grid10_x0_y0_67);
		OpPanel.add(VideoID_TextField, grid10_x1_y0_68);
		OpPanel.add(WayBackLabel, grid10_x0_y1_69);
		OpPanel.add(WayBackField, grid10_x1_y1_70);
		MainTabbedPane.add(BasicInfoTabPanel, "基本設定");
		MainTabbedPane.add(SavingInfoTabPanel, "保存設定");
		MainTabbedPane.add(FFMpegTabPanel, "動画設定");
		MainTabbedPane.addTab("変換設定", null, getConvertingSettingPanel(), null);
		SavingInfoTabPanel.add(getSaveInfoTabPaneEach(), grid2_x__y__35);
		BasicInfoTabPanel.add(UserInfoPanel, grid12_x0_y0_7);
		BasicInfoTabPanel.add(getProxyInfoPanel(), grid12_x0_y1_6);
		BasicInfoTabPanel.add(BrowserInfoPanel, grid12_x0_y2_95);
		VhookSettingPanel.setLayout(gridBagLayout8);
		VhookSettingPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "拡張vhookライブラリの設定"));
		VhookSettingPanel.add(getNotUseVhookCheckBox(), grid8_x0_y0_58);
		VhookSettingPanel.add(ViewCommentLabel, grid8_x0_y1_66);
		VhookSettingPanel.add(getViewCommentField(), grid8_x3_y1_65);
		VhookSettingPanel.add(FontPathLabel, grid8_x0_y3_59);
		VhookSettingPanel.add(FontPathField, grid8_x1_y3_60);
		VhookSettingPanel.add(SettingFontPathButton, grid8_x4_y3_64);
		VhookSettingPanel.add(FontIndexLabel, grid8_x0_y5_61);
		VhookSettingPanel.add(FontIndexField, grid8_x1_y5_62);
		VhookSettingPanel.add(ShadowKindLabel, grid8_x0_y6_72);
		VhookSettingPanel.add(getShadowComboBox(), grid8_x1_y6_73);
		VhookSettingPanel.add(ShowConvVideoCheckBox, grid8_x0_y7_63);
		VhookSettingPanel.add(getFixFontSizeCheckBox(), grid8_x0_y8_14);
		VhookSettingPanel.add(getOpaqueCommentCheckBox(), grid8_x0_y9_33);
		CommentSaveInfoPanel.add(SavingCommentCheckBox, grid4_x0_y6_16);
		CommentSaveInfoPanel.add(AddTimeStampToCommentCheckBox, grid4_x0_y7_86);
		CommentSaveInfoPanel.add(getDelCommentCheckBox(), grid4_x0_y8_25);
		CommentSaveInfoPanel.add(getFixCommentNumCheckBox(),grid4_x0_y9_26);
		CommentSaveInfoPanel.add(CommentNumLabel, grid4_x0_y10_17);
		CommentSaveInfoPanel.add(CommentNumField, grid4_x1_y10_22);
		CommentSaveInfoPanel.add(Comment_SaveFolderRadioButton,grid4_x0_y11_18);
		CommentSaveInfoPanel.add(CommentSavedFolderField, grid4_x0_y12_19);
		CommentSaveInfoPanel.add(ShowSavingCommentFolderDialogButton,grid4_x3_y12_23);
		CommentSaveInfoPanel.add(Comment_SaveFileRadioButton,grid4_x0_y13_20);
		CommentSaveInfoPanel.add(CommentSavedFileField, grid4_x0_y14_21);
		CommentSaveInfoPanel.add(ShowSavingCommentFileDialogButton,grid4_x3_y14_24);
		OwnerCommentInfoPanel.add(SavingOwnerCommentCheckBox, grid11_x0_y0_75);
		OwnerCommentInfoPanel.add(OwnerCommentNoticeLabel1, grid11_x0_y1_76);
		ConvertedVideoSavingInfoPanel.add(SavingConvertedVideoCheckBox,
				new GridBagConstraints(0, 0, 3, 1, 1.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 5, 0, 5), 0, 0));
		ConvertedVideoSavingInfoPanel.add(ConvertWithCommentCheckBox,				grid5_x0_y1_78);
		ConvertedVideoSavingInfoPanel.add(ConvertWithOwnerCommentCheckBox,			grid5_x0_y2_81);
		ConvertedVideoSavingInfoPanel.add(Conv_SaveFolderRadioButton,				grid5_x0_y3_40);
		ConvertedVideoSavingInfoPanel.add(getNotAddVideoID_ConvVideoCheckBox(),		grid5_x0_y4_39);
		ConvertedVideoSavingInfoPanel.add(AddOption_ConvVideoFileCheckBox,			gird5_x0_y5_89);
		ConvertedVideoSavingInfoPanel.add(ConvertedVideoSavedFolderField,			grid5_x0_y6_41);
		ConvertedVideoSavingInfoPanel.add(ShowSavingConvertedVideoFolderDialogButton,grid5_x3_y6_44);
		ConvertedVideoSavingInfoPanel.add(Conv_SaveFileRadioButton,					grid5_x0_y7_42);
		ConvertedVideoSavingInfoPanel.add(ConvertedVideoSavedFileField,				grid5_x0_y8_43);
		ConvertedVideoSavingInfoPanel.add(ShowSavingConvertedVideoFileDialogButton,	grid5_x3_y8_45);
		FFMpegTabPanel.add(PathSettingPanel, new GridBagConstraints(0, 0, 1, 1,
				1.0, 0.0, GridBagConstraints.NORTHEAST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
		FFMpegTabPanel.add(FFmpegSettingPanel, grid6_x0_y1_1);
		CheckFFmpegFunctionPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "FFmpeg機能チェック",
				TitledBorder.LEADING, TitledBorder.TOP,
				new Font("MS UI Gothic", Font.PLAIN, 12), Color.blue));
		CheckFFmpegFunctionPanel.setForeground(Color.blue);
		CheckFFmpegFunctionPanel.setLayout(new GridBagLayout());
		CheckDownloadVideoButton.setText("DL動画");
		CheckDownloadVideoButton.setToolTipText(
			"ダウンロードした動画をチェックする：幅　高さ　fps　映像codec　音声codec");
		CheckDownloadVideoButton.setForeground(Color.blue);
		CheckDownloadVideoButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e){
						CheckDownloadVideoButton_actionPerformed(e);
					}
				});
		GridBagConstraints grid_x0_y1_85 = new GridBagConstraints();
		grid_x0_y1_85.gridx = 0;
		grid_x0_y1_85.gridy = 1;
		grid_x0_y1_85.weightx = 0.0;
		grid_x0_y1_85.anchor = GridBagConstraints.WEST;
		grid_x0_y1_85.fill = GridBagConstraints.HORIZONTAL;
		grid_x0_y1_85.insets = new Insets(0, 5, 5, 5);
		CheckFFmpegFunctionPanel.add(CheckDownloadVideoButton, grid_x0_y1_85);
		CheckDownloadVideoLabel.setText("ダウンロードした動画をチェックする");
		CheckDownloadVideoLabel.setForeground(Color.blue);
		GridBagConstraints grid_x1_y1_88 = new GridBagConstraints();
		grid_x1_y1_88.gridx = 1;
		grid_x1_y1_88.gridy = 1;
		grid_x0_y1_85.weightx = 1.0;
		grid_x1_y1_88.weighty = 0.0;
		grid_x1_y1_88.anchor = GridBagConstraints.WEST;
		grid_x1_y1_88.fill = GridBagConstraints.HORIZONTAL;
		grid_x1_y1_88.insets = new Insets(0, 5, 5, 5);
		CheckFFmpegFunctionPanel.add(CheckDownloadVideoLabel,grid_x1_y1_88);
		GridBagConstraints grid_x0_y2_87 = new GridBagConstraints();
		grid_x0_y2_87.gridx = 0;
		grid_x0_y2_87.gridy = 2;
		grid_x0_y2_87.gridwidth = 2;
		grid_x0_y2_87.gridheight = 4;
		grid_x0_y2_87.weightx = 1.0;
		grid_x0_y2_87.weighty = 1.0;
		grid_x0_y2_87.anchor = GridBagConstraints.NORTHEAST;
		grid_x0_y2_87.fill = GridBagConstraints.BOTH;
		grid_x0_y2_87.insets = new Insets(0, 5, 5, 5);
		TextFFmpegOutput.setLineWrap(true);
		TextFFmpegOutput.setForeground(Color.blue);
		TextFFmpegOutput.setOpaque(false);
		CheckFFmpegFunctionPanel.add(
				new JScrollPane(TextFFmpegOutput), grid_x0_y2_87);
		GridBagConstraints grid6_x0_y2_82 = new GridBagConstraints();
		grid6_x0_y2_82.gridx = 0;
		grid6_x0_y2_82.gridy = 2;
		grid6_x0_y2_82.gridwidth = 4;
		grid6_x0_y2_82.gridheight = 4;
		grid6_x0_y2_82.weightx = 1.0;
		grid6_x0_y2_82.weighty = 1.0;
		grid6_x0_y2_82.anchor = GridBagConstraints.NORTHWEST;
		grid6_x0_y2_82.fill = GridBagConstraints.BOTH;
		grid6_x0_y2_82.insets = new Insets(0, 5, 5, 5);
		FFMpegTabPanel.add(CheckFFmpegFunctionPanel, grid6_x0_y2_82);
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
		VhookWidePathField
				.addMouseListener(new PopupRightClick(this.VhookWidePathField));
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

		TextFFmpegOutput.addMouseListener(
				new PopupRightClick(this.TextFFmpegOutput));
		BrowserCookieField.addMouseListener(
				new PopupRightClick(this.BrowserCookieField));
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
		addTarget(VhookWidePathField, false);
		addTarget(FontPathField, false);

		addTarget(BrowserCookieField, false);
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
	JLabel VhookWidePathLabel = new JLabel();
	JCheckBox UseVhookCheckBox = new JCheckBox();
	JCheckBox UseVhookWideCheckBox = new JCheckBox();
	JTextField VhookPathField = new JTextField();
	JTextField VhookWidePathField = new JTextField();
	JButton SettingVhookPathButton = new JButton();
	JButton SettingVhookWidePathButton = new JButton();
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
			CurrentDir = chooser.getCurrentDirectory();
			field.setText(getRelativePath(chooser.getSelectedFile()));
		}
	}
	private String getRelativePath(File file){
		return file.getAbsolutePath().replace(
				new File("").getAbsolutePath(), ".");
	}
	private void showSaveDialog(String title, JTextField field) {
		File dir = new File(field.getText());
		if (!dir.exists()){
			dir = new File("");
		}
		JFileChooser chooser = new JFileChooser(dir);
		chooser.setDialogTitle(title);
		int code = 0;
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		code = chooser.showOpenDialog(this);
		if (code == JFileChooser.APPROVE_OPTION) {
		//	CurrentDir = chooser.getCurrentDirectory();
			field.setText(getRelativePath(chooser.getSelectedFile()));
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
		String comment = CommentSavedFileField.getText();
		String ownercomment = comment;
		int index = comment.lastIndexOf('.');
		if (index > comment.lastIndexOf('\\')){
			ownercomment = comment.substring(0, index);
		}
		ownercomment += saccubus.Converter.OWNER_EXT;
		return new ConvertingSetting(
			MailAddrField.getText(),
			new String(PasswordField.getPassword()),
			SavingVideoCheckBox.isSelected(),
			VideoSavedFileField.getText(),
			SavingCommentCheckBox.isSelected(),
			AddTimeStampToCommentCheckBox.isSelected(),
			CommentSavedFileField.getText(),
			SavingOwnerCommentCheckBox.isSelected(),
			ownercomment,
			SavingConvertedVideoCheckBox.isSelected(),
			ConvertWithCommentCheckBox.isSelected(),
			ConvertWithOwnerCommentCheckBox.isSelected(),
			ConvertedVideoSavedFileField.getText(),
			ViewCommentField.getText(),
			FFmpegPathField.getText(),
			VhookPathField.getText(),
			ExtOptionField.getText(),
			MainOptionField.getText(),
			CommandLineInOptionField.getText(),
			CommandLineOutOptionField.getText(),
			Integer.toString(back_comment),
			FontPathField.getText(),
			Integer.parseInt(FontIndexField.getText()),
			ShowConvVideoCheckBox.isSelected(),
			DelVideoCheckBox.isSelected(),
			Video_SaveFolderRadioButton.isSelected(),
			VideoSavedFolderField.getText(),
			DelCommentCheckBox.isSelected(),
			Comment_SaveFolderRadioButton.isSelected(),
			CommentSavedFolderField.getText(),
			NotAddVideoID_ConvVideoCheckBox.isSelected(),
			Conv_SaveFolderRadioButton.isSelected(),
			ConvertedVideoSavedFolderField.getText(),
			NGWordTextField.getText(),
			NGIDTextField.getText(),
			UseProxyCheckBox.isSelected(),
			ProxyTextField.getText(),
			proxy_port,
			FixFontSizeCheckBox.isSelected(),
			FixCommentNumCheckBox.isSelected(),
			OpaqueCommentCheckBox.isSelected(),
			FFmpegOptionModel.getSelectedFile(),
			NotUseVhookCheckBox.isSelected(),
			ShadowComboBox.getSelectedIndex(),
			AddOption_ConvVideoFileCheckBox.isSelected(),
			VideoID_TextField.getText(),
			VhookWidePathField.getText(),
			UseVhookCheckBox.isSelected(),
			UseVhookWideCheckBox.isSelected(),
			BrowserIECheckBox.isSelected(),
			BrowserFFCheckBox.isSelected(),
			BrowserChromeCheckBox.isSelected(),
			BrowserChromiumCheckBox.isSelected(),
			BrowserOperaCheckBox.isSelected(),
			BrowserOtherCheckBox.isSelected(),
			BrowserCookieField.getText()
			);
	}

	private void setSetting(ConvertingSetting setting) {
		MailAddrField.setText(setting.getMailAddress());
		PasswordField.setText(setting.getPassword());
		SavingVideoCheckBox.setSelected(setting.isSaveVideo());
		VideoSavedFileField.setText(setting.getVideoFile().getPath());
		SavingCommentCheckBox.setSelected(setting.isSaveComment());
		AddTimeStampToCommentCheckBox.setSelected(setting.isAddTimeStamp());
		CommentSavedFileField.setText(setting.getCommentFile().getPath());
		SavingOwnerCommentCheckBox.setSelected(setting.isSaveOwnerComment());
		SavingConvertedVideoCheckBox.setSelected(setting.isSaveConverted());
		ConvertWithCommentCheckBox.setSelected(setting.isConvertWithComment());
		ConvertWithOwnerCommentCheckBox.setSelected(setting.isConvertWithOwnerComment());
		ConvertedVideoSavedFileField.setText(setting.getConvertedVideoFile().getPath());
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
		ShowConvVideoCheckBox.setSelected(setting.isVhook_ShowConvertingVideo());
		DelVideoCheckBox.setSelected(setting.isDeleteVideoAfterConverting());
		VideoSavedFolderField.setText(setting.getVideoFixFileNameFolder().getPath());
		CommentSavedFolderField.setText(setting.getCommentFixFileNameFolder().getPath());
		ConvertedVideoSavedFolderField.setText(setting.getConvFixFileNameFolder().getPath());
		DelCommentCheckBox.setSelected(setting.isDeleteCommentAfterConverting());
		NotAddVideoID_ConvVideoCheckBox.setSelected(setting.isNotAddVideoID_Conv());
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
		AddOption_ConvVideoFileCheckBox.setSelected(setting.isAddOption_ConvVideoFile());
		VideoID_TextField.setText(setting.getHistory1());
		VhookWidePathField.setText(setting.getVhookWidePath());
		UseVhookCheckBox.setSelected(setting.isUseVhookNormal());
		UseVhookWideCheckBox.setSelected(setting.isUseVhookWide());
		BrowserIECheckBox.setSelected(setting.isBrowserIE());
		BrowserFFCheckBox.setSelected(setting.isBrowserFF());
		BrowserChromeCheckBox.setSelected(setting.isBrowserChrome());
		BrowserChromiumCheckBox.setSelected(setting.isBrowserChromium());
		BrowserOperaCheckBox.setSelected(setting.isBrowserOpera());
		BrowserOtherCheckBox.setSelected(setting.isBrowserOther());
		BrowserCookieField.setText(setting.getBrowserCookiePath());
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
	Converter converter = null;

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
		if (converter == null || converter.isConverted()) {
			Stopwatch.setup(elapsedTimeBar);
			converter = new Converter(VideoID_TextField.getText(), WayBackField
					.getText(), this.getSetting(), this.statusBar,
					new ConvertStopFlag(this.DoButton, DoButtonStopString,
							DoButtonWaitString, DoButtonDefString), vhookInfoBar);
			converter.start();
		} else { /* 開始しているので、ストップする。 */
			final ConvertStopFlag flag = converter.getStopFlag();
			if (!flag.needStop()) { /* まだストップしていない。 */
				flag.stop();
			}
		}
	}

	/* FFmpeg versionチェック実行 */

	public void FFVersionButton_actionPerformed(ActionEvent e){
		try{
			if (converter != null && !converter.isConverted()){
				TextFFmpegOutput.setText("変換実施中。お待ちください。");
				return;
			}
			TextFFmpegOutput.setText(null);
			ArrayList<String> list = execFFmpeg("-version");
			TextFFmpegOutput.append(list.get(0));
			if (list.size() >= 1){
				TextFFmpegOutput.append(list.get(1));
			}
		} catch(NullPointerException ex){
			TextFFmpegOutput.setText("(´∀｀)＜ぬるぽ\nガッ\n");
			ex.printStackTrace();
		} catch (FileNotFoundException ex) {
			TextFFmpegOutput.setText(ex.getMessage());
		}
	}
	public void CheckDownloadVideoButton_actionPerformed(ActionEvent e){
		boolean needStop = true;
		try {
			File inputVideo = null;
			if (converter == null || converter.isConverted()){
				ConvertingSetting setting = getSetting();
				converter = new Converter(
						VideoID_TextField.getText(),
						WayBackField.getText(), setting, statusBar,
						new ConvertStopFlag(null, null, null, null),
						vhookInfoBar);
				if (setting.isVideoFixFileName()) {
					File folder = setting.getVideoFixFileNameFolder();
					String path = converter.detectTitleFromVideo(folder);
					if (path == null || path.isEmpty()){
						TextFFmpegOutput.setText(
								"検索しましたが動画が見つかりません。");
						return;
					}
					inputVideo = new File(folder, path);
				} else {
					inputVideo = setting.getVideoFile();
				}
		//	} else if (converter.isConverted()){
		//		inputVideo = converter.getVideoFile();
			} else {
				TextFFmpegOutput.setText("変換実施中。お待ちください。");
				needStop = false;
				return;
			}
			if (inputVideo == null || !inputVideo.canRead()){
				TextFFmpegOutput.setText("ダウンロード動画がありません。");
				return;
			}
			TextFFmpegOutput.setText(inputVideo.getName() + "\n");
			ArrayList<String> list = execFFmpeg("-y "
				+ "-i \"" + inputVideo.getPath() + "\"");
			for (String line : list){
				if (line.indexOf("Stream #0.") >= 0){
					TextFFmpegOutput.append(line.replace("Stream #0.", "").trim() + "\n");
				}
			}
		} catch(NullPointerException ex){
			TextFFmpegOutput.setText("(´∀｀)＜ぬるぽ\nガッ\n");
			ex.printStackTrace();
		} catch (FileNotFoundException ex) {
			TextFFmpegOutput.setText(ex.getMessage());
		} finally {
			if (needStop) {
				converter.getStopFlag().finished();
			}
		}
	}

	private ArrayList<String> execFFmpeg(String parameter)
			throws FileNotFoundException {
		final ArrayList<String> output = new ArrayList<String>();

		if (parameter == null) {
			parameter = "";
		}
		String path = getSetting().getFFmpegPath();
		if (!new File(path).canRead()) {
			throw new FileNotFoundException("FFmpegが見つかりません");
		}
		FFmpeg ffmpeg = new FFmpeg(path);
		ffmpeg.setCmd(parameter);
		System.out.println("execute:" + ffmpeg.getCmd());
		Stopwatch.setup(new JLabel());
		ffmpeg.exec(-9, new FFmpeg.CallbackInterface() {
				@Override
				public void doEveryLoop(String e) {
					System.out.println(e);
					output.add(e.trim() + "\n");
				}
				@Override
				public boolean checkStop() {
					return false;
				}
				@Override
				public void doAbort(String e) {
				}
		});
		return output;
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

	public void SettingVhookWidePathButton_actionPerformed(ActionEvent e) {
		showSaveDialog("拡張vhookワイドへのパス", VhookWidePathField, false, false);
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
			GridBagConstraints grid_x0_y1_11 = new GridBagConstraints();
			grid_x0_y1_11.weighty = 1.0;
			grid_x0_y1_11.weightx = 1.0;
			grid_x0_y1_11.insets = new Insets(0, 5, 0, 5);
			grid_x0_y1_11.gridy = 1;
			grid_x0_y1_11.gridx = 0;
			grid_x0_y1_11.anchor = GridBagConstraints.NORTH;
			grid_x0_y1_11.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints grid_x0_y0_0 = new GridBagConstraints(0,
					2, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH,
					GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0);
			grid_x0_y0_0.gridx = 0;
			grid_x0_y0_0.anchor = GridBagConstraints.CENTER;
			grid_x0_y0_0.weighty = 0.0;
			grid_x0_y0_0.gridy = 0;
			ConvertingSettingPanel = new JPanel();
			ConvertingSettingPanel.setLayout(new GridBagLayout());
			ConvertingSettingPanel.add(getNGWordSettingPanel(),
					grid_x0_y1_11);
			ConvertingSettingPanel.add(VhookSettingPanel, grid_x0_y0_0);
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
			GridBagConstraints grid_x1_y1_5 = new GridBagConstraints();
			grid_x1_y1_5.fill = GridBagConstraints.HORIZONTAL;
			grid_x1_y1_5.gridy = 1;
			grid_x1_y1_5.weightx = 1.0;
			grid_x1_y1_5.insets = new Insets(0, 5, 5, 5);
			grid_x1_y1_5.gridx = 1;
			GridBagConstraints grid_x0_y1_4 = new GridBagConstraints();
			grid_x0_y1_4.gridx = 0;
			grid_x0_y1_4.anchor = GridBagConstraints.WEST;
			grid_x0_y1_4.insets = new Insets(0, 5, 5, 0);
			grid_x0_y1_4.gridy = 1;
			NGIDLabel = new JLabel();
			NGIDLabel.setText("NG ID");
			GridBagConstraints grid_x1_y0_3 = new GridBagConstraints();
			grid_x1_y0_3.fill = GridBagConstraints.HORIZONTAL;
			grid_x1_y0_3.gridy = 0;
			grid_x1_y0_3.weightx = 1.0;
			grid_x1_y0_3.insets = new Insets(0, 5, 5, 5);
			grid_x1_y0_3.gridx = 1;
			GridBagConstraints grid_x0_y0_2 = new GridBagConstraints();
			grid_x0_y0_2.gridx = 0;
			grid_x0_y0_2.insets = new Insets(0, 5, 5, 0);
			grid_x0_y0_2.gridy = 0;
			NGWordLavel = new JLabel();
			NGWordLavel.setText("NGワード");
			NGWordSettingPanel = new JPanel();
			NGWordSettingPanel.setLayout(new GridBagLayout());
			NGWordSettingPanel.setBorder(BorderFactory.createTitledBorder(
					BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
					"NGワード・ID設定"));
			NGWordSettingPanel.add(NGWordLavel, grid_x0_y0_2);
			NGWordSettingPanel.add(getNGWordTextField(), grid_x1_y0_3);
			NGWordSettingPanel.add(NGIDLabel, grid_x0_y1_4);
			NGWordSettingPanel.add(getNGIDTextField(), grid_x1_y1_5);
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
			GridBagConstraints grid_x0_y0_13 = new GridBagConstraints();
			grid_x0_y0_13.gridx = 0;
			grid_x0_y0_13.gridwidth = 2;
			grid_x0_y0_13.weightx = 1.0;
			grid_x0_y0_13.fill = GridBagConstraints.HORIZONTAL;
			grid_x0_y0_13.insets = new Insets(0, 5, 0, 5);
			grid_x0_y0_13.gridy = 0;
			GridBagConstraints grid_x1_y2_12 = new GridBagConstraints();
			grid_x1_y2_12.fill = GridBagConstraints.HORIZONTAL;
			grid_x1_y2_12.gridy = 2;
			grid_x1_y2_12.weightx = 1.0;
			grid_x1_y2_12.insets = new Insets(5, 0, 5, 5);
			grid_x1_y2_12.gridx = 1;
			GridBagConstraints grid_x0_y2_10 = new GridBagConstraints();
			grid_x0_y2_10.gridx = 0;
			grid_x0_y2_10.insets = new Insets(5, 5, 5, 5);
			grid_x0_y2_10.gridy = 2;
			ProxyPortLabel = new JLabel();
			ProxyPortLabel.setText("ポート番号");
			GridBagConstraints grid_x1_y1_9 = new GridBagConstraints();
			grid_x1_y1_9.fill = GridBagConstraints.BOTH;
			grid_x1_y1_9.gridy = 1;
			grid_x1_y1_9.weightx = 1.0;
			grid_x1_y1_9.insets = new Insets(0, 0, 0, 5);
			grid_x1_y1_9.gridx = 1;
			GridBagConstraints grid_x0_y1_8 = new GridBagConstraints();
			grid_x0_y1_8.gridx = 0;
			grid_x0_y1_8.insets = new Insets(0, 5, 0, 5);
			grid_x0_y1_8.fill = GridBagConstraints.NONE;
			grid_x0_y1_8.anchor = GridBagConstraints.EAST;
			grid_x0_y1_8.gridy = 1;
			ProxyLabel = new JLabel();
			ProxyLabel.setText("プロキシ");
			ProxyInfoPanel = new JPanel();
			ProxyInfoPanel.setLayout(new GridBagLayout());
			ProxyInfoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
					.createEtchedBorder(), "プロキシ設定"));
			ProxyInfoPanel.add(ProxyLabel, grid_x0_y1_8);
			ProxyInfoPanel.add(getProxyTextField(), grid_x1_y1_9);
			ProxyInfoPanel.add(ProxyPortLabel, grid_x0_y2_10);
			ProxyInfoPanel.add(getProxyPortTextField(), grid_x1_y2_12);
			ProxyInfoPanel.add(getUseProxyCheckBox(), grid_x0_y0_13);
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
			GridBagConstraints grid_x0_y1_15 = new GridBagConstraints();
			grid_x0_y1_15.fill = GridBagConstraints.HORIZONTAL;
			grid_x0_y1_15.gridwidth = 4;
			grid_x0_y1_15.gridx = 0;
			grid_x0_y1_15.gridy = 1;
			grid_x0_y1_15.weightx = 1.0;
			grid_x0_y1_15.insets = new Insets(0, 25, 0, 5);
			GridBagConstraints grid_x3_y5_32 = new GridBagConstraints(3,
					4, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0);
			grid_x3_y5_32.gridx = 3;
			grid_x3_y5_32.insets = new Insets(0, 0, 5, 5);
			grid_x3_y5_32.gridy = 5;
			GridBagConstraints grid_x0_y5_30 = new GridBagConstraints(0,
					4, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 50, 0, 5), 0, 0);
			grid_x0_y5_30.gridx = 0;
			grid_x0_y5_30.insets = new Insets(0, 50, 5, 5);
			grid_x0_y5_30.gridy = 5;
			GridBagConstraints grid_x0_y4_29 = new GridBagConstraints(0,
					3, 4, 1, 1.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 25, 0, 5), 0, 0);
			grid_x0_y4_29.gridx = 0;
			grid_x0_y4_29.gridy = 4;
			GridBagConstraints grid_x0_y3_28 = new GridBagConstraints(0,
					2, 3, 1, 1.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.BOTH, new Insets(0, 50, 0, 5), 0, 0);
			grid_x0_y3_28.gridx = 0;
			grid_x0_y3_28.gridy = 3;
			GridBagConstraints grid_x0_y2_27 = new GridBagConstraints(0,
					1, 4, 1, 1.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(0, 25, 0, 5), 0,
					0);
			grid_x0_y2_27.gridx = 0;
			grid_x0_y2_27.gridy = 2;
			GridBagConstraints grid_x0_y0_34 = new GridBagConstraints();
			grid_x0_y0_34.insets = new Insets(0, 5, 0, 5);
			grid_x0_y0_34.gridy = 0;
			grid_x0_y0_34.weightx = 1.0;
			grid_x0_y0_34.fill = GridBagConstraints.HORIZONTAL;
			grid_x0_y0_34.gridx = 0;
			GridBagConstraints grid_x3_y3_31 = new GridBagConstraints(3,
					2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0);
			grid_x3_y3_31.gridy = 3;
			grid_x3_y3_31.gridx = 3;
			VideoSaveInfoPanel = new JPanel();
			VideoSaveInfoPanel.setLayout(new GridBagLayout());
			VideoSaveInfoPanel.setBorder(BorderFactory.createTitledBorder(
					BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
					"動画保存設定", TitledBorder.LEADING,
					TitledBorder.TOP, new Font("MS UI Gothic",
							Font.PLAIN, 12), Color.black));
			VideoSaveInfoPanel.add(SavingVideoCheckBox, grid_x0_y0_34);
			VideoSaveInfoPanel.add(getDelVideoCheckBox(), grid_x0_y1_15);
			VideoSaveInfoPanel.add(Video_SaveFolderRadioButton,
					grid_x0_y2_27);
			VideoSaveInfoPanel.add(VideoSavedFolderField, grid_x0_y3_28);
			VideoSaveInfoPanel.add(ShowSavingVideoFolderDialogButton,
					grid_x3_y3_31);
			VideoSaveInfoPanel.add(Video_SaveFileRadioButton,
					grid_x0_y4_29);
			VideoSaveInfoPanel.add(VideoSavedFileField, grid_x0_y5_30);
			VideoSaveInfoPanel.add(ShowSavingVideoFileDialogButton,
					grid_x3_y5_32);
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
			GridBagConstraints grid_x_y_36 = new GridBagConstraints();
			grid_x_y_36.weighty = 0.0;
			grid_x_y_36.weightx = 1.0;
			grid_x_y_36.fill = GridBagConstraints.HORIZONTAL;
			grid_x_y_36.insets = new Insets(0, 5, 0, 5);
			grid_x_y_36.anchor = GridBagConstraints.NORTH;
			grid_x_y_36.gridx = 0;
			grid_x_y_36.gridy = 0;
			GridBagConstraints grid_x0_y1_37 = new GridBagConstraints();
			grid_x0_y1_37.anchor = GridBagConstraints.NORTH;
			grid_x0_y1_37.gridx = 0;
			grid_x0_y1_37.gridy = 1;
			grid_x0_y1_37.weighty = 0.0;
			grid_x0_y1_37.fill = GridBagConstraints.HORIZONTAL;
			grid_x0_y1_37.gridheight = 1;
			grid_x0_y1_37.insets = new Insets(0, 5, 0, 5);
			GridBagConstraints grid_x0_y2_79 = new GridBagConstraints();
			grid_x0_y2_79.gridx = 0;
			grid_x0_y2_79.gridy = 2;
			grid_x0_y2_79.gridwidth = 1;
			grid_x0_y2_79.weightx = 1.0;
			grid_x0_y2_79.weighty = 1.0;
			grid_x0_y2_79.anchor = GridBagConstraints.NORTH;
			grid_x0_y2_79.fill = GridBagConstraints.HORIZONTAL;
			grid_x0_y2_79.insets = new Insets(0, 5, 0, 5);
			VideoSavingTabbedPanel = new JPanel();
			VideoSavingTabbedPanel.setLayout(new GridBagLayout());
			VideoSavingTabbedPanel.add(getVideoSaveInfoPanel(),
					grid_x_y_36);
			VideoSavingTabbedPanel.add(CommentSaveInfoPanel,
					grid_x0_y1_37);
			VideoSavingTabbedPanel.add(OwnerCommentInfoPanel,
					grid_x0_y2_79);
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
			GridBagConstraints grid_x_y_38 = new GridBagConstraints(0,
					2, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH,
					GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0);
			grid_x_y_38.gridx = -1;
			grid_x_y_38.fill = GridBagConstraints.HORIZONTAL;
			grid_x_y_38.gridy = -1;
			ConvertedVideoSavingTabbedPanel = new JPanel();
			ConvertedVideoSavingTabbedPanel.setLayout(new GridBagLayout());
			ConvertedVideoSavingTabbedPanel.add(ConvertedVideoSavingInfoPanel,
					grid_x_y_38);
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
			GridBagConstraints grid_x_y_47 = new GridBagConstraints();
			grid_x_y_47.fill = GridBagConstraints.BOTH;
			grid_x_y_47.gridx = -1;
			grid_x_y_47.gridy = -1;
			grid_x_y_47.insets = new Insets(0, 0, 5, 5);
			GridBagConstraints grid_x_y_46 = new GridBagConstraints();
			grid_x_y_46.fill = GridBagConstraints.HORIZONTAL;
			grid_x_y_46.gridwidth = 3;
			grid_x_y_46.gridx = -1;
			grid_x_y_46.gridy = -1;
			grid_x_y_46.weightx = 1.0;
			grid_x_y_46.insets = new Insets(0, 5, 5, 5);
			FFmpegOptionComboBoxPanel = new JPanel();
			FFmpegOptionComboBoxPanel.setLayout(new GridBagLayout());
			FFmpegOptionComboBoxPanel.add(getFFmpegOptionComboBox(),
					grid_x_y_46);
			FFmpegOptionComboBoxPanel.add(getFFmpegOptionReloadButton(),
					grid_x_y_47);
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
