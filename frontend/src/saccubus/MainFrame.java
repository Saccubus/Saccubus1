package saccubus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicArrowButton;

import psi.lib.swing.PopupRightClick;
import saccubus.net.Gate;
import saccubus.net.Loader;
import saccubus.net.Path;
import saccubus.util.FileDropTarget;
import saccubus.util.Logger;

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

	private final static File TOP_DIR= new File(".");
	JPanel contentPane;
	BorderLayout borderLayout1 = new BorderLayout();
	JMenuBar jMenuBar1 = new JMenuBar();
	JMenu jMenuFile = new JMenu();
	JMenuItem jMenuOpen = new JMenuItem();
	JMenuItem jMenuAdd = new JMenuItem();
	JMenuItem jMenuSave = new JMenuItem();
	JMenuItem jMenuSaveAs = new JMenuItem();
	JMenuItem jMenuFileExit = new JMenuItem();
	JMenuItem jMenuInit = new JMenuItem();
	JMenu jMenuHelp = new JMenu();
	JMenuItem jMenuHelpAbout = new JMenuItem();
	JMenuItem jMenuHelpReadme = new JMenuItem();
	JMenuItem jMenuHelpReadmeNew = new JMenuItem();
	JMenuItem jMenuHelpReadmePlus = new JMenuItem();
	JMenuItem jMenuHelpReadmeFirst = new JMenuItem();
	JMenuItem jMenuHelpErrorTable = new JMenuItem();
	JMenuItem jMenuHelpFF = new JMenuItem();
	JMenuItem jMenuHelpFormats = new JMenuItem();
	JMenuItem jMenuHelpCodecs = new JMenuItem();
	JMenuItem jMenuHelpProtocols = new JMenuItem();
	JMenuItem jMenuHelpFilters = new JMenuItem();
	JMenu jMenuDetail = new JMenu();
	JMenuItem jMenuNGConfig = new JMenuItem();
	JMenuItem jMenuAprilFool = new JMenuItem();
	public JLabel statusBar = new JLabel();
	public JLabel elapsedTimeBar = new JLabel();
	JLabel vhookInfoBar = new JLabel();
	JLabel infoBar = new JLabel();
	JTabbedPane MainTabbedPane = new JTabbedPane();
	JPanel SavingInfoTabPanel = new JPanel();
	JPanel FFMpegInfoTabPanel = new JPanel();
	JPanel FFMpegTabPanel = new JPanel();
	JPanel FFMpegTab2Panel = new JPanel();
	JPanel VideoInfoPanel = new JPanel();
	JPanel StatusPanel = new JPanel();
	JTextField VideoID_TextField = new JTextField();
	JButton historyBackButton = new BasicArrowButton(SwingConstants.WEST);
	JButton historyForwardButton = new BasicArrowButton(SwingConstants.EAST);
	JButton DoButton = new JButton();
	public static final String DoButtonDefString = "変換";
	public static final String DoButtonStopString = "停止";
	public static final String DoButtonWaitString = "待機";
	public static final String DoButtonDoneString = "終了";
	GridBagLayout gridBagLayout2 = new GridBagLayout();
	JPanel UserInfoPanel = new JPanel();
	GridBagLayout gridBagLayout3 = new GridBagLayout();
	JLabel MailAddrLabel = new JLabel();
	JTextField MailAddrField = new JTextField();
	JLabel PasswordLabel = new JLabel();
	JPasswordField PasswordField = new JPasswordField();
	JPanel CommentSaveInfoPanel = new JPanel();
//	JPanel OwnerCommentInfoPanel = new JPanel();
	JPanel OldCommentModePanel = new JPanel();
	GridBagLayout gridBagLayout4 = new GridBagLayout();
	JCheckBox SavingVideoCheckBox = new JCheckBox();
	JTextField VideoSavedFileField = new JTextField();
	JButton ShowSavingVideoFileDialogButton = new JButton();
	JCheckBox SavingCommentCheckBox = new JCheckBox();
	JTextField CommentSavedFileField = new JTextField();
	JCheckBox AddTimeStampToCommentCheckBox = new JCheckBox();
//	JCheckBox SavingOwnerCommentCheckBox = new JCheckBox();
	JCheckBox oldCommentModeEnableCheckBox = new JCheckBox();
	JCheckBox newCommentModeEnableCheckBox = new JCheckBox();
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
//	JTextArea TextFFmpegOutput = new JTextArea();
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
	JPanel OptionalThreadInfoPanel = new JPanel();
	JLabel OptionalthreadLabel = new JLabel();
	JCheckBox OptionalTranslucentCheckBox = new JCheckBox();
	JPanel experimentPanel = new JPanel();
	JCheckBox fontHeightFixCheckBox = new JCheckBox();
	JTextField fontHeightRatioTextField = new JTextField();
	JCheckBox disableOriginalResizeCheckBox = new JCheckBox();
	@SuppressWarnings({ "rawtypes", "unchecked" })
	JComboBox commentModeComboBox = new JComboBox(commentModeArray);
	JCheckBox commentSpeedCheckBox = new JCheckBox();
	JTextField commentSpeedTextField = new JTextField();
	JCheckBox enableCA_CheckBox = new JCheckBox();
	JCheckBox disableEcoCheckBox = new JCheckBox();
	JCheckBox fontWidthFixCheckBox = new JCheckBox();
	JTextField fontWidthRatioTextField = new JTextField();
	JCheckBox useLineskipAsFontsizeCheckBox = new JCheckBox();
	JCheckBox useExtraFontCheckBox = new JCheckBox();
	JTextField extraFontTextField = new JTextField();
	private String encrypt_pass;
	SharedNgScore sharedNgScore = new SharedNgScore();
	JLabel extraModeLabel = new JLabel();
	JTextField extraModeField = new JTextField();
	JPanel additionalOptionPanel = new JPanel();
	JTextField additionalOptionFiled = new JTextField();
	JTextField wideAdditionalOptionFiled = new JTextField();
	JCheckBox saveWatchPageInfoCheckBox = new JCheckBox();
	JCheckBox saveThumbInfoCheckBox = new JCheckBox();
	JPanel watchPageSavingInfoPanel = new JPanel();
	private JPanel watchPageSavingTabbedPanel = new JPanel();
	JCheckBox saveThumbUserCheckBox = new JCheckBox();
	JLabel userFolderLabel = new JLabel();
	JTextField userFolderTextField = new JTextField();
	JRadioButton saveThumbInfoExtTxtRadioButton = new JRadioButton();
	JRadioButton saveThumbInfoExtXmlRadioButton = new JRadioButton();
	ButtonGroup thumbInfoExtButtonGroup = new ButtonGroup();
	JPanel fileNameInfoPanel = new JPanel();
	JCheckBox changeMp4ExtCheckBox = new JCheckBox();
	JCheckBox changeTitleIdCheckBox = new JCheckBox();
	JCheckBox saveThumbnailJpgCheckBox = new JCheckBox();
	JTextField opaqueRateTextField = new JTextField();
	private JButton showDownloadListButton = new JButton();
	private JLabel showDownloadListLabel = new JLabel();
	private JPanel updateInfoPanel = new JPanel();
	private JLabel updateInfoLabel = new JLabel();
//	private JLabel updateInfoLabel1 = new JLabel();
//	private JLabel updateInfoLabel2 = new JLabel();
	private JCheckBox nmmNewEnableCheckBox = new JCheckBox();
	private JCheckBox fpsUpCheckBox = new JCheckBox();
	private JRadioButton fpsFilterRadioButton = new JRadioButton();
	private JRadioButton fpsConvRadioButton = new JRadioButton();
	private ButtonGroup fpsConvButtonGroup = new ButtonGroup();
	private JCheckBox soundOnlyCheckBox = new JCheckBox();
	private JTextField fpsUpTextFiled = new JTextField();
	private JTextField fpsMinTextField = new JTextField();
	private JTextField thumbTextFiled = new JTextField();
	private JButton playConvertedVideoButton = new JButton();
	private JLabel playConvertedVideoLabel = new JLabel();
	private JCheckBox saveAutoListCheckBox = new JCheckBox();
	private JCheckBox autoPlayCheckBox = new JCheckBox();
	private JCheckBox autoPlay2CheckBox = new JCheckBox();
	private JCheckBox liveOperationCheckBox = new JCheckBox();
	private JLabel liveOperationLabel = new JLabel();
	private JTextField liveOperationDurationTextField = new JTextField();
	private JCheckBox liveCommentModeCheckBox = new JCheckBox();
	private JCheckBox liveCommentVposShiftCheckBox = new JCheckBox();
	private JTextField liveCommentVposShiftTextField = new JTextField();
	private JPanel liveConvertInfoPanel = new JPanel();
	private JCheckBox liveOparationDurationChangeCheckBox = new JCheckBox();
	private JCheckBox premiumColorCheckBox = new JCheckBox();
	private JCheckBox appendCommentCheckBox = new JCheckBox();
	private JSpinner nThreadSpinner;
	private String notice;
	private HistoryDeque<String> requestHistory;
	private AutoPlay autoPlay;
//                                                   (up left down right)
	private static final Insets INSETS_0_5_0_0 = new Insets(0, 5, 0, 0);
	private static final Insets INSETS_0_5_0_5 = new Insets(0, 5, 0, 5);
	private static final Insets INSETS_0_5_5_5 = new Insets(0, 5, 5, 5);
	private static final Insets INSETS_0_0_5_5 = new Insets(0, 0, 5, 5);
	private static final Insets INSETS_0_0_0_5 = new Insets(0, 0, 0, 5);
	private static final Insets INSETS_0_0_5_0 = new Insets(0, 0, 5, 0);
	private static final Insets INSETS_0_0_0_0 = new Insets(0, 0, 0, 0);
	private static final Insets INSETS_5_5_5_5 = new Insets(5, 5, 5, 5);
	private static final Insets INSETS_0_25_0_5 = new Insets(0, 25, 0, 5);
	private static final Insets INSETS_0_50_0_5 = new Insets(0, 50, 0, 5);
	private static final String[] commentModeArray = {
		"0：コメント表示自動選択（2010年12月22日以後は新表示）",
		"1：新コメント表示（毎分最新100コメント＋旧表示）",
		"2：旧コメント表示（最大10分以上で1000コメント表示）",
	};

	public static final String THUMB_DEFALT_STRING = "<自動>";
	private static final String MY_MYLIST = "my/mylist";
	private static final String VIDEO_URL_PARSER = "http://www.nicovideo.jp/watch/";
	private static final Logger log = Logger.MainLog;

	private String url;
	private JPanel activityPane;
	private JScrollPane activityScroll;
	private JButton AllCancelButton;
	private JButton AllDeleteButton;
	private ChangeListener changeListener;
	private JButton AllExecButton;
	private JCheckBox PendingModeCheckbox;
	private JCheckBox OneLineCheckbox;
	private boolean OneLineMode;
	private JCheckBox downloadDownCheckBox;

	private ErrorControl errorControl;
	private JPanel errorStatusPanel;
	private JLabel errorUrlLabel;
	private JButton errorResetUrlButton;
	private JPanel errorButtonPanel;
	private JButton errorListDeleteButton;
	private JButton errorListSaveButton;

	private JPanel playVideoPanel;
	private JLabel playVideoLabel;
	private JPanel playVideoButtonPanel;
	private JButton playVideoPlayButton;
	private JButton playVideoNextButton;
	private JButton playVideoBackButton;

	private JButton AllSaveButton;

	public MainFrame() {
		try {
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			jbInit();
			setPopup();
			setDropTarget();
			ConvertingSetting setting;
			if(new File(ConvertingSetting.PROP_FILE).exists()){
				setting = ConvertingSetting.loadSetting(null, null);
			} else {
				setting = ConvertingSetting.loadSetting(null,
					null, "./saccubus.ini", false);
			}
			this.setSetting(setting);
			new StringBuffer();
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
		grid8_x1_y6_73.insets = INSETS_0_0_0_5;
		grid8_x1_y6_73.gridx = 1;
		GridBagConstraints grid8_x0_y6_72 = new GridBagConstraints();
		grid8_x0_y6_72.gridx = 0;
		grid8_x0_y6_72.anchor = GridBagConstraints.WEST;
		grid8_x0_y6_72.insets = INSETS_0_5_0_5;
		grid8_x0_y6_72.fill = GridBagConstraints.NONE;
		grid8_x0_y6_72.gridwidth = 1;
		grid8_x0_y6_72.gridy = 6;
		ShadowKindLabel = new JLabel();
		ShadowKindLabel.setText("影の種類");
		ShadowKindLabel.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
		GridBagConstraints grid1_x1_y0_71 = new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, INSETS_0_0_0_0, 0, 6);
		grid1_x1_y0_71.fill = GridBagConstraints.BOTH;
		grid1_x1_y0_71.ipady = 0;
		GridBagConstraints grid10_x1_y1_70 = new GridBagConstraints();
		grid10_x1_y1_70.fill = GridBagConstraints.HORIZONTAL;
		grid10_x1_y1_70.gridy = 1;
		grid10_x1_y1_70.ipadx = 0;
		grid10_x1_y1_70.ipady = 0;
		grid10_x1_y1_70.weightx = 1.0;
		grid10_x1_y1_70.insets = INSETS_0_0_0_0;
		grid10_x1_y1_70.gridx = 1;
		grid10_x1_y1_70.gridwidth = 3;
		GridBagConstraints grid10_x0_y1_69 = new GridBagConstraints();
		grid10_x0_y1_69.gridx = 0;
		grid10_x0_y1_69.ipadx = 0;
		grid10_x0_y1_69.ipady = 0;
		grid10_x0_y1_69.insets = INSETS_0_5_0_5;
		grid10_x0_y1_69.anchor = GridBagConstraints.WEST;
		grid10_x0_y1_69.gridy = 1;
		GridBagConstraints grid10_x1_y0_68 = new GridBagConstraints();
		grid10_x1_y0_68.fill = GridBagConstraints.BOTH;
		grid10_x1_y0_68.gridy = 0;
		grid10_x1_y0_68.ipady = 0;
		grid10_x1_y0_68.weightx = 1.0;
		grid10_x1_y0_68.insets = INSETS_0_0_0_0;
		GridBagConstraints grid10_x1_y0_68B = new GridBagConstraints();
		grid10_x1_y0_68B.gridx = 2;
		grid10_x1_y0_68B.fill = GridBagConstraints.NONE;
		grid10_x1_y0_68B.gridy = 0;
		grid10_x1_y0_68B.weightx = 0.0;
		grid10_x1_y0_68B.insets = INSETS_0_0_0_0;
		GridBagConstraints grid10_x2_y0_68C = new GridBagConstraints();
		grid10_x2_y0_68C.gridx = 3;
		grid10_x2_y0_68C.fill = GridBagConstraints.NONE;
		grid10_x2_y0_68C.gridy = 0;
		grid10_x2_y0_68C.weightx = 0.0;
		grid10_x2_y0_68C.insets = INSETS_0_0_0_0;
		GridBagConstraints grid10_x0_y0_67 = new GridBagConstraints();
		grid10_x0_y0_67.gridx = 0;
		grid10_x0_y0_67.ipadx = 0;
		grid10_x0_y0_67.ipady = 0;
		grid10_x0_y0_67.insets = INSETS_0_5_0_5;
		grid10_x0_y0_67.anchor = GridBagConstraints.WEST;
		grid10_x0_y0_67.gridy = 0;
		GridBagConstraints grid8_x0_y1_66 = new GridBagConstraints();
		grid8_x0_y1_66.gridx = 0;
		grid8_x0_y1_66.insets = INSETS_0_5_0_5;
		grid8_x0_y1_66.anchor = GridBagConstraints.WEST;
		grid8_x0_y1_66.gridwidth = 1;
		grid8_x0_y1_66.gridy = 1;
		ViewCommentLabel = new JLabel();
		ViewCommentLabel.setText("表示コメント数");
		GridBagConstraints grid8_x1_y1_65 = new GridBagConstraints();
		grid8_x1_y1_65.anchor = GridBagConstraints.CENTER;
		grid8_x1_y1_65.fill = GridBagConstraints.HORIZONTAL;
		grid8_x1_y1_65.gridy = 1;
		grid8_x1_y1_65.weightx = 1.0;
		grid8_x1_y1_65.gridwidth = 6;
		grid8_x1_y1_65.insets = INSETS_0_0_0_5;
		grid8_x1_y1_65.gridx = 1;
		GridBagConstraints grid8_x4_y3_64 = new GridBagConstraints(1, 1,
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, INSETS_0_0_0_5, 0, 0);
		grid8_x4_y3_64.gridy = 3;
		grid8_x4_y3_64.fill = GridBagConstraints.HORIZONTAL;
		grid8_x4_y3_64.gridx = 4;
		GridBagConstraints grid8_x0_y7_63 = new GridBagConstraints(0, 4,
				2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, INSETS_0_5_0_5, 0, 0);
		grid8_x0_y7_63.gridy = 7;
		grid8_x0_y7_63.gridx = 0;
		grid8_x0_y7_63.gridwidth = 5;
		GridBagConstraints grid8_x1_y5_62 = new GridBagConstraints(0, 3,
				2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, INSETS_0_5_5_5, 0, 0);
		grid8_x1_y5_62.gridy = 5;
		grid8_x1_y5_62.gridx = 1;
		grid8_x1_y5_62.fill = GridBagConstraints.HORIZONTAL;
		grid8_x1_y5_62.insets = INSETS_0_0_5_5;
		grid8_x1_y5_62.gridwidth = 4;
		GridBagConstraints grid8_x0_y5_61 = new GridBagConstraints(0, 2,
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, INSETS_5_5_5_5, 0, 0);
		grid8_x0_y5_61.gridy = 5;
		grid8_x0_y5_61.gridx = 0;
		grid8_x0_y5_61.fill = GridBagConstraints.VERTICAL;
		grid8_x0_y5_61.anchor = GridBagConstraints.WEST;
		grid8_x0_y5_61.gridwidth = 1;
		GridBagConstraints grid8_x1_y3_60 = new GridBagConstraints(0, 1,
				1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, INSETS_0_5_0_5, 0, 0);
		grid8_x1_y3_60.gridy = 3;
		grid8_x1_y3_60.gridx = 1;
		grid8_x1_y3_60.fill = GridBagConstraints.HORIZONTAL;
		grid8_x1_y3_60.insets = INSETS_0_0_0_5;
		grid8_x1_y3_60.gridwidth = 3;
		GridBagConstraints grid8_x0_y3_59 = new GridBagConstraints(0, 0,
				2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, INSETS_0_5_5_5, 0, 0);
		grid8_x0_y3_59.gridy = 3;
		grid8_x0_y3_59.insets = INSETS_5_5_5_5;
		grid8_x0_y3_59.gridx = 0;
		grid8_x0_y3_59.fill = GridBagConstraints.NONE;
		grid8_x0_y3_59.anchor = GridBagConstraints.WEST;
		grid8_x0_y3_59.weightx = 0.0;
		grid8_x0_y3_59.gridwidth = 1;
		GridBagConstraints grid8_x0_y0_58 = new GridBagConstraints();
		grid8_x0_y0_58.gridx = 0;
		grid8_x0_y0_58.anchor = GridBagConstraints.WEST;
		grid8_x0_y0_58.insets = INSETS_0_5_0_5;
		grid8_x0_y0_58.gridwidth = 5;
		grid8_x0_y0_58.weightx = 1.0;
		grid8_x0_y0_58.fill = GridBagConstraints.HORIZONTAL;
		grid8_x0_y0_58.gridy = 0;
		GridBagConstraints grid9_x1_y2_57 = new GridBagConstraints();
		grid9_x1_y2_57.fill = GridBagConstraints.BOTH;
		grid9_x1_y2_57.gridy = 2;
		grid9_x1_y2_57.weightx = 1.0;
		grid9_x1_y2_57.insets = INSETS_0_0_5_5;
		grid9_x1_y2_57.gridx = 1;
		GridBagConstraints grid9_x0_y2_56 = new GridBagConstraints();
		grid9_x0_y2_56.gridx = 0;
		grid9_x0_y2_56.insets = INSETS_0_5_5_5;
		grid9_x0_y2_56.anchor = GridBagConstraints.WEST;
		grid9_x0_y2_56.gridy = 2;
		ExtOptionLabel = new JLabel();
		ExtOptionLabel.setText("出力の拡張子");
		GridBagConstraints grid9_x0_y1_55 = new GridBagConstraints();
		grid9_x0_y1_55.gridx = 0;
		grid9_x0_y1_55.fill = GridBagConstraints.HORIZONTAL;
		grid9_x0_y1_55.weightx = 1.0;
		grid9_x0_y1_55.gridwidth = 4;
		grid9_x0_y1_55.gridy = 1;
		GridBagConstraints grid9_x1_y5_53 = new GridBagConstraints(1, 3,
				2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, INSETS_0_0_5_5, 0, 0);
		grid9_x1_y5_53.gridy = 5;
		grid9_x1_y5_53.gridheight = 1;
		grid9_x1_y5_53.weightx = 1.0;
		grid9_x1_y5_53.gridwidth = 3;
		GridBagConstraints grid9_x1_y4_52 = new GridBagConstraints(2, 2,
				1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, INSETS_0_0_5_5, 0, 0);
		grid9_x1_y4_52.gridy = 4;
		grid9_x1_y4_52.gridwidth = 3;
		grid9_x1_y4_52.weightx = 1.0;
		grid9_x1_y4_52.gridx = 1;
		GridBagConstraints grid9_x1_y3_51 = new GridBagConstraints(2, 1,
				1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, INSETS_0_0_5_5, 0, 0);
		grid9_x1_y3_51.gridy = 2;
		grid9_x1_y3_51.gridwidth = 1;
		grid9_x1_y3_51.weightx = 1.0;
		grid9_x1_y3_51.gridx = 3;
		GridBagConstraints grid9_x0_y5_50 = new GridBagConstraints(0, 3,
				1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, INSETS_0_5_0_5, 0, 0);
		grid9_x0_y5_50.gridy = 5;
		GridBagConstraints grid9_x0_y4_49 = new GridBagConstraints(0, 2,
				2, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, INSETS_0_5_5_5, 0, 0);
		grid9_x0_y4_49.gridy = 4;
		grid9_x0_y4_49.gridwidth = 1;
		GridBagConstraints grid9_x0_y3_48 = new GridBagConstraints(0, 1,
				1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, INSETS_0_5_5_5, 0, 0);
		grid9_x0_y3_48.gridy = 2;
		grid9_x0_y3_48.gridx = 2;
		GridBagConstraints grid5_x3_y8_45 = new GridBagConstraints(3, 5,
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, INSETS_0_0_5_5, 0, 0);
		grid5_x3_y8_45.gridy = 8;
		GridBagConstraints grid5_x3_y6_44 = new GridBagConstraints(3, 3,
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, INSETS_0_0_0_5, 0, 0);
		grid5_x3_y6_44.gridy = 6;
		GridBagConstraints grid5_x0_y8_43 = new GridBagConstraints(0, 8,
				2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(3, 50, 5, 5), 0, 0);
		GridBagConstraints grid5_x2_y8 = new GridBagConstraints(2, 8,
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, INSETS_0_0_5_0, 0, 0);
		GridBagConstraints grid5_x0_y9 = new GridBagConstraints(0, 9,
				4, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, INSETS_0_25_0_5, 0, 0);
		GridBagConstraints grid5_x0_y7_42 = new GridBagConstraints(0, 4,
				4, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, INSETS_0_25_0_5, 0, 0);
		grid5_x0_y7_42.gridy = 7;
		GridBagConstraints grid5_x0_y6_41 = new GridBagConstraints(0, 6,
				2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, INSETS_0_50_0_5, 0, 0);
		GridBagConstraints grid5_x2_y6 = new GridBagConstraints(2, 6,
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, INSETS_0_0_0_0, 0, 0);
		GridBagConstraints gird5_x0_y5_89 = new GridBagConstraints();
		gird5_x0_y5_89.gridx = 0;
		gird5_x0_y5_89.gridy = 5;
		gird5_x0_y5_89.gridwidth = 4;
		gird5_x0_y5_89.anchor = GridBagConstraints.WEST;
		gird5_x0_y5_89.fill = GridBagConstraints.NONE;
		gird5_x0_y5_89.insets = INSETS_0_50_0_5;
		GridBagConstraints grid5_x0_y4_39 = new GridBagConstraints();
		grid5_x0_y4_39.gridx = 0;
		grid5_x0_y4_39.insets = INSETS_0_50_0_5;
		grid5_x0_y4_39.fill = GridBagConstraints.HORIZONTAL;
		grid5_x0_y4_39.weightx = 1.0;
		grid5_x0_y4_39.gridwidth = 4;
		grid5_x0_y4_39.gridy = 4;
		GridBagConstraints grid5_x0_y3_40 = new GridBagConstraints(0, 2,
				4, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, INSETS_0_25_0_5, 0, 0);
		grid5_x0_y3_40.gridy = 3;
		GridBagConstraints grid5_x0_y2_81 = new GridBagConstraints();
		grid5_x0_y2_81.gridx = 0;
		grid5_x0_y2_81.gridy = 2;
		grid5_x0_y2_81.gridwidth = 3;
		grid5_x0_y2_81.gridheight = 1;
		grid5_x0_y2_81.anchor = GridBagConstraints.CENTER;
		grid5_x0_y2_81.fill = GridBagConstraints.BOTH;
		grid5_x0_y2_81.insets = INSETS_0_25_0_5;
		GridBagConstraints grid5_x0_y1_78 = new GridBagConstraints();
		grid5_x0_y1_78.gridx = 0;
		grid5_x0_y1_78.gridy = 1;
		grid5_x0_y1_78.gridwidth = 3;
		grid5_x0_y1_78.gridheight = 1;
		grid5_x0_y1_78.anchor = GridBagConstraints.CENTER;
		grid5_x0_y1_78.fill = GridBagConstraints.BOTH;
		grid5_x0_y1_78.insets = INSETS_0_25_0_5;
		GridBagConstraints grid2_x__y__35 = new GridBagConstraints();
		grid2_x__y__35.fill = GridBagConstraints.BOTH;
		grid2_x__y__35.weighty = 1.0;
		grid2_x__y__35.weightx = 1.0;
		GridBagConstraints grid8_x0_y10_0 = new GridBagConstraints();
		grid8_x0_y10_0.gridx = 0;
		grid8_x0_y10_0.gridy = 10;
		grid8_x0_y10_0.gridwidth = 2;
		grid8_x0_y10_0.weightx = 0.0;
		grid8_x0_y10_0.anchor = GridBagConstraints.WEST;
		grid8_x0_y10_0.fill = GridBagConstraints.HORIZONTAL;
		grid8_x0_y10_0.insets = INSETS_0_5_0_5;
		GridBagConstraints grid8_x2_y10_1 = new GridBagConstraints();
		grid8_x2_y10_1.gridx = 2;
		grid8_x2_y10_1.gridy = 10;
		grid8_x2_y10_1.gridwidth = 3;
		grid8_x2_y10_1.weightx = 1.0;
		grid8_x2_y10_1.anchor = GridBagConstraints.CENTER;
		grid8_x2_y10_1.fill = GridBagConstraints.HORIZONTAL;
		grid8_x2_y10_1.insets = INSETS_0_5_0_5;
		GridBagConstraints grid8_x0_y9_33 = new GridBagConstraints();
		grid8_x0_y9_33.gridx = 0;
		grid8_x0_y9_33.fill = GridBagConstraints.HORIZONTAL;
		grid8_x0_y9_33.weightx = 1.0;
		grid8_x0_y9_33.anchor = GridBagConstraints.WEST;
		grid8_x0_y9_33.insets = INSETS_0_5_0_5;
		grid8_x0_y9_33.gridwidth = 3;
		grid8_x0_y9_33.gridy = 9;
		GridBagConstraints grid8_x2_y9_101 = new GridBagConstraints();
		grid8_x2_y9_101.gridx = 3;
		grid8_x2_y9_101.gridy = 9;
		grid8_x2_y9_101.gridwidth = 1;
		grid8_x2_y9_101.weightx = 0.0;
		grid8_x2_y9_101.anchor = GridBagConstraints.WEST;
		grid8_x2_y9_101.fill = GridBagConstraints.HORIZONTAL;
		grid8_x2_y9_101.insets = INSETS_0_5_0_5;
		GridBagConstraints grid8_x3_y9_103 = new GridBagConstraints();
		grid8_x3_y9_103.gridx = 4;
		grid8_x3_y9_103.gridy = 9;
		grid8_x3_y9_103.gridwidth = 1;
//		grid8_x3_y9_103.weightx = 1.0;
		grid8_x3_y9_103.anchor = GridBagConstraints.WEST;
		grid8_x3_y9_103.fill = GridBagConstraints.HORIZONTAL;
		grid8_x3_y9_103.insets = INSETS_0_5_0_5;
		GridBagConstraints grid4_x0_y9_26 = new GridBagConstraints();
		grid4_x0_y9_26.gridx = 0;
		grid4_x0_y9_26.gridwidth = 4;
		grid4_x0_y9_26.insets = INSETS_0_25_0_5;
		grid4_x0_y9_26.weightx = 1.0;
		grid4_x0_y9_26.fill = GridBagConstraints.HORIZONTAL;
		grid4_x0_y9_26.gridy = 9;
		GridBagConstraints grid4_x0_y8_25 = new GridBagConstraints();
		grid4_x0_y8_25.gridx = 0;
		grid4_x0_y8_25.gridwidth = 4;
		grid4_x0_y8_25.fill = GridBagConstraints.HORIZONTAL;
		grid4_x0_y8_25.insets = INSETS_0_25_0_5;
		grid4_x0_y8_25.weightx = 1.0;
		grid4_x0_y8_25.gridy = 8;
		GridBagConstraints grid4_x3_y14_24 = new GridBagConstraints(3, 10,
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, INSETS_0_0_5_5, 0, 0);
		grid4_x3_y14_24.gridy = 14;
		GridBagConstraints grid4_x3_y12_23 = new GridBagConstraints(3, 8,
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, INSETS_0_0_0_5, 0, 0);
		grid4_x3_y12_23.gridy = 12;
		GridBagConstraints grid4_x0_y14_21 = new GridBagConstraints(0, 14,
				2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 50, 5, 5), 0, 0);
		grid4_x0_y14_21.gridy = 14;
		GridBagConstraints grid4_x2_y14 = new GridBagConstraints(2, 14,
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, INSETS_0_0_5_0, 0, 0);
		GridBagConstraints grid4_x0_y13_20 = new GridBagConstraints(0, 9,
				4, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, INSETS_0_25_0_5, 0, 0);
		grid4_x0_y13_20.gridy = 13;
		GridBagConstraints grid4_x0_y12_19 = new GridBagConstraints(0, 12,
				2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, INSETS_0_50_0_5, 0, 0);
		grid4_x0_y12_19.gridy = 12;
		GridBagConstraints grid4_x2_y12 = new GridBagConstraints(2, 12,
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, INSETS_0_0_0_0, 0, 0);
		GridBagConstraints grid4_x0_y11_18 = new GridBagConstraints(0, 7,
				4, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, INSETS_0_25_0_5, 0, 0);
		grid4_x0_y11_18.gridy = 11;
		GridBagConstraints grid4_x0_y10_17 = new GridBagConstraints();
		grid4_x0_y10_17.gridx = 0;
		grid4_x0_y10_17.gridy = 10;
		grid4_x0_y10_17.gridwidth = 1;
		grid4_x0_y10_17.anchor = GridBagConstraints.CENTER;
		grid4_x0_y10_17.fill = GridBagConstraints.BOTH;
		grid4_x0_y10_17.insets = INSETS_0_50_0_5;
		GridBagConstraints grid4_x1_y10_22 = new GridBagConstraints();
		//grid4_x1_y10_22.gridx = 1;
		grid4_x1_y10_22.gridy = 10;
		grid4_x1_y10_22.gridwidth = 4;
		grid4_x1_y10_22.weightx = 1.0;
		grid4_x1_y10_22.anchor = GridBagConstraints.CENTER;
		grid4_x1_y10_22.fill = GridBagConstraints.BOTH;
		grid4_x1_y10_22.insets = INSETS_0_0_0_5;
		GridBagConstraints grid4_x0_y6_16 = new GridBagConstraints(0, 5,
				4, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, INSETS_5_5_5_5, 0, 0);
		grid4_x0_y6_16.gridy = 6;
		grid4_x0_y6_16.gridwidth = 1;
		grid4_x0_y6_16.insets = INSETS_0_5_0_0;
		GridBagConstraints grid4_x1_y6_ = new GridBagConstraints();
		//grid4_x1_y6_.gridx = 2;
		grid4_x1_y6_.gridy = 6;
		grid4_x1_y6_.anchor = GridBagConstraints.WEST;
		grid4_x1_y6_.fill = GridBagConstraints.HORIZONTAL;
		grid4_x1_y6_.insets = INSETS_0_5_0_5;
		GridBagConstraints grid4_x0_y7_86 = new GridBagConstraints();
		grid4_x0_y7_86.gridx = 0;
		grid4_x0_y7_86.gridy = 7;
		grid4_x0_y7_86.gridwidth = 4;
		grid4_x0_y7_86.anchor = GridBagConstraints.WEST;
		grid4_x0_y7_86.fill = GridBagConstraints.HORIZONTAL;
		grid4_x0_y7_86.insets = INSETS_0_25_0_5;
		GridBagConstraints grid11_x0_y0_75 = new GridBagConstraints();
		grid11_x0_y0_75.gridx = 0;
		grid11_x0_y0_75.gridy = 0;
		grid11_x0_y0_75.weightx = 1.0;
		grid11_x0_y0_75.anchor = GridBagConstraints.CENTER;
		grid11_x0_y0_75.fill = GridBagConstraints.HORIZONTAL;
		grid11_x0_y0_75.insets = INSETS_0_5_5_5;
		GridBagConstraints grid11_x0_y1_76 = new GridBagConstraints();
		grid11_x0_y1_76.gridx = 0;
		grid11_x0_y1_76.gridy = 1;
		grid11_x0_y1_76.weightx = 1.0;
		grid11_x0_y1_76.anchor = GridBagConstraints.WEST;
		grid11_x0_y1_76.fill = GridBagConstraints.HORIZONTAL;
		grid11_x0_y1_76.insets = INSETS_0_5_5_5;
		GridBagConstraints grid8_x0_y8_14 = new GridBagConstraints();
		grid8_x0_y8_14.gridx = 0;
		grid8_x0_y8_14.anchor = GridBagConstraints.WEST;
		grid8_x0_y8_14.fill = GridBagConstraints.HORIZONTAL;
		grid8_x0_y8_14.weightx = 1.0;
		grid8_x0_y8_14.gridwidth = 5;
		grid8_x0_y8_14.insets = INSETS_0_5_0_5;
		grid8_x0_y8_14.gridy = 8;
		GridBagConstraints grid12_x0_y0_7 = new GridBagConstraints(0, 0,
				1, 1, 1.0, 1.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, INSETS_0_5_0_5, 0, 0);
		grid12_x0_y0_7.weighty = 0.0;
		GridBagConstraints grid12_x0_y1_6 = new GridBagConstraints();
		grid12_x0_y1_6.gridx = 0;
		grid12_x0_y1_6.weighty = 0.0;
		grid12_x0_y1_6.weightx = 1.0;
		grid12_x0_y1_6.insets = INSETS_0_5_0_5;
		grid12_x0_y1_6.fill = GridBagConstraints.HORIZONTAL;
		grid12_x0_y1_6.anchor = GridBagConstraints.NORTH;
		grid12_x0_y1_6.gridy = 1;
		GridBagConstraints grid12_x0_y2_95 = new GridBagConstraints();
		grid12_x0_y2_95.gridx = 0;
		grid12_x0_y2_95.gridy = 2;
		grid12_x0_y2_95.weightx = 1.0;
		grid12_x0_y2_95.weighty = 0.0;
		grid12_x0_y2_95.insets = new Insets(5, 5, 0, 5);
		grid12_x0_y2_95.fill = GridBagConstraints.HORIZONTAL;
		grid12_x0_y2_95.anchor = GridBagConstraints.NORTH;
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
		jMenuHelpReadme.setText("　reame(オリジナル)表示");
		jMenuHelpReadme.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showReadme_actionPerformed("readme.txt");
			}
		});
		jMenuHelpReadmeNew.setText("reameNew(最新)表示");
		jMenuHelpReadmeNew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showReadme_actionPerformed("readmeNew.txt");
			}
		});
		jMenuHelpReadmePlus.setText("　reame+表示");
		jMenuHelpReadmePlus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showReadme_actionPerformed("readme+.txt");
			}
		});
		jMenuHelpReadmeFirst.setText("　最初に必ず読んで　表示");
		jMenuHelpReadmeFirst.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showReadme_actionPerformed("最初に必ず読んで.txt");
			}
		});
		jMenuHelpErrorTable.setText("エラーコード　表示");
		jMenuHelpErrorTable.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showReadme_actionPerformed("エラーコード.txt");
			}
		});
		jMenuHelpFF.setText("FFmpegヘルプ表示");
		jMenuHelpFF.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){ FFhelp_actionPerformed("-h"); }
		});
		jMenuHelpFormats.setText("　FFmpegフォーマット");
		jMenuHelpFormats.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){ FFhelp_actionPerformed("-formats"); }
		});
		jMenuHelpCodecs.setText("　FFmpegコーデック");
		jMenuHelpCodecs.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){ FFhelp_actionPerformed("-codecs"); }
		});
		jMenuHelpProtocols.setText("　FFmpegプロトコル");
		jMenuHelpProtocols.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){ FFhelp_actionPerformed("-protocols"); }
		});
		jMenuHelpFilters.setText("　FFmpegフィルター");
		jMenuHelpFilters.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){ FFhelp_actionPerformed("-filters"); }
		});
		jMenuDetail.setText("詳細設定");
		jMenuNGConfig.setText("ニコニコ動画のNG設定保存");
		jMenuNGConfig.addActionListener(new MainFrame_LoadNGConfig(this));
		jMenuAprilFool.setText("AprilFool再現");
		jMenuAprilFool.addActionListener(new MainFrame_jMenuAfDialog(this));
		jMenuOpen.setText("開く(Open)...");
		jMenuOpen.setForeground(Color.blue);
		jMenuOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JTextField propFileField = new JTextField("");
				showSaveDialog("設定ファイルのパス", propFileField, false, false);
				setSetting(ConvertingSetting.loadSetting(null, null, propFileField.getText()));
			}
		});
		jMenuAdd.setText("追加 (Add)...");
		jMenuAdd.setForeground(Color.blue);
		jMenuAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JTextField propFileField = new JTextField("");
				showSaveDialog("追加用設定ファイルのパス", propFileField, false, false);
				setSetting(ConvertingSetting.addSetting(getSetting(), propFileField.getText()));
			}
		});
		jMenuSave.setText("上書き保存 (Save saccubus.xml)");
		jMenuSave.setForeground(Color.blue);
		jMenuSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ConvertingSetting.saveSetting(getSetting());
			}
		});
		jMenuSaveAs.setText("名前を付けて保存 (saveAs)...");
		jMenuSaveAs.setForeground(Color.blue);
		jMenuSaveAs.addActionListener(new ActionListener() {
			JTextField propFileField = new JTextField(ConvertingSetting.PROP_FILE);
			@Override
			public void actionPerformed(ActionEvent e) {
				showSaveDialog("設定ファイルのパス", propFileField,	true, false);
				ConvertingSetting.saveSetting(getSetting(), propFileField.getText());
			}
		});
		jMenuInit.setText("初期化 (Init)");
		jMenuInit.setForeground(Color.blue);
		jMenuInit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setSetting(ConvertingSetting.loadSetting(null, null, "./saccubus.ini", false));
			}
		});
		VideoInfoPanel.setLayout(gridBagLayout1);
		VideoID_TextField.setText("http://www.nicovideo.jp/watch/");
		requestHistory = new HistoryDeque<String>(new String());
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
		grid13_x0_y0_96.insets = INSETS_0_5_0_5;
		BrowserInfoPanel.add(BrowserInfoLabel, grid13_x0_y0_96);
		BrowserIECheckBox.setText("Interner Eplorer (IE7/IE8/IE9～11)");
		BrowserIECheckBox.setForeground(Color.blue);
		GridBagConstraints grid13_x0_y1_97 = new GridBagConstraints();
		grid13_x0_y1_97.gridx = 0;
		grid13_x0_y1_97.gridy = 1;
		grid13_x0_y1_97.gridwidth = 2;
		grid13_x0_y1_97.weightx = 1.0;
		grid13_x0_y1_97.anchor = GridBagConstraints.NORTH;
		grid13_x0_y1_97.fill = GridBagConstraints.HORIZONTAL;
		grid13_x0_y1_97.insets = INSETS_0_5_0_5;
		BrowserInfoPanel.add(BrowserIECheckBox, grid13_x0_y1_97);
		BrowserFFCheckBox.setText("Firefox (FF3/FF4～36)");
		BrowserFFCheckBox.setForeground(Color.blue);
		GridBagConstraints grid13_x0_y2_98 = new GridBagConstraints();
		grid13_x0_y2_98.gridx = 0;
		grid13_x0_y2_98.gridy = 2;
		grid13_x0_y2_98.gridwidth = 2;
		grid13_x0_y2_98.weightx = 1.0;
		grid13_x0_y2_98.anchor = GridBagConstraints.NORTH;
		grid13_x0_y2_98.fill = GridBagConstraints.HORIZONTAL;
		grid13_x0_y2_98.insets = INSETS_0_5_0_5;
		BrowserInfoPanel.add(BrowserFFCheckBox, grid13_x0_y2_98);
		BrowserChromeCheckBox.setText("Google Chrome(ver 33.0.x.x以降は暗号化のため不可)");
		BrowserChromeCheckBox.setForeground(Color.blue);
		GridBagConstraints grid13_x0_y3_99 = new GridBagConstraints();
		grid13_x0_y3_99.gridx = 0;
		grid13_x0_y3_99.gridy = 3;
		grid13_x0_y3_99.gridwidth = 2;
		grid13_x0_y3_99.weightx = 1.0;
		grid13_x0_y3_99.anchor = GridBagConstraints.NORTH;
		grid13_x0_y3_99.fill = GridBagConstraints.HORIZONTAL;
		grid13_x0_y3_99.insets = INSETS_0_5_0_5;
		BrowserInfoPanel.add(BrowserChromeCheckBox, grid13_x0_y3_99);
		BrowserOperaCheckBox.setText("Opera(ver 20.0以降は暗号化のため不可)");
		BrowserOperaCheckBox.setForeground(Color.blue);
		GridBagConstraints grid13_x0_y4_100 = new GridBagConstraints();
		grid13_x0_y4_100.gridx = 0;
		grid13_x0_y4_100.gridy = 4;
		grid13_x0_y4_100.gridwidth = 2;
		grid13_x0_y4_100.weightx = 1.0;
		grid13_x0_y4_100.anchor = GridBagConstraints.NORTH;
		grid13_x0_y4_100.fill = GridBagConstraints.HORIZONTAL;
		grid13_x0_y4_100.insets = INSETS_0_5_0_5;
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
		grid13_x0_y5_101.insets = INSETS_0_5_0_5;
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
		grid13_x0_y6_102.insets = INSETS_0_5_0_5;
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
		grid13_x0_y7_103.insets = INSETS_0_25_0_5;
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
		grid13_x1_y7_104.insets = INSETS_0_0_0_5;
		BrowserInfoPanel.add(BrowserCookieDialogButton, grid13_x1_y7_104);

		GridBagLayout gridBagLayout14 = new GridBagLayout();
		updateInfoPanel.setLayout(gridBagLayout14);
		updateInfoPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				"新機能情報", TitledBorder.LEADING, TitledBorder.TOP,
				getFont(), Color.blue));
		nmmNewEnableCheckBox.setText("NM動画に少し対応");
		nmmNewEnableCheckBox.setForeground(Color.blue);
		nmmNewEnableCheckBox.setToolTipText("フォント、ビデオクリップ、テキスト、アクションスクリプト未対応");
		nmmNewEnableCheckBox.setEnabled(true);
		GridBagConstraints grid14_x0_y0 = new GridBagConstraints();
		grid14_x0_y0.gridx = 0;
		grid14_x0_y0.gridy = 0;
		grid14_x0_y0.gridwidth = 5;
//		grid14_x0_y0.weightx = 1.0;
		grid14_x0_y0.anchor = GridBagConstraints.NORTH;
		grid14_x0_y0.fill = GridBagConstraints.HORIZONTAL;
		grid14_x0_y0.insets = INSETS_0_5_0_5;
		updateInfoPanel.add(new JLabel("新機能を全てオフにすると以前と同じ。長時間ローカル変換等はオフで試して下さい"), grid14_x0_y0);
		GridBagConstraints grid14_x0_y1 = new GridBagConstraints();
		grid14_x0_y1.gridx = 0;
		grid14_x0_y1.gridy = 1;
		grid14_x0_y1.gridwidth = 1;
//		grid14_x0_y1.weightx = 1.0;
		grid14_x0_y1.anchor = GridBagConstraints.NORTH;
		grid14_x0_y1.fill = GridBagConstraints.HORIZONTAL;
		grid14_x0_y1.insets = INSETS_0_5_0_5;
		updateInfoPanel.add(nmmNewEnableCheckBox, grid14_x0_y1);
		updateInfoLabel.setText("（主に1枚絵。画面の切替はズレが有る）");
		updateInfoLabel.setForeground(Color.blue);
		updateInfoLabel.setToolTipText("手書き漫画の動画などは画面か音声かがネタバレになるので注意。");
		GridBagConstraints grid14_x1_y1 = new GridBagConstraints();
		grid14_x1_y1.gridx = 1;
		grid14_x1_y1.gridy = 1;
		grid14_x1_y1.gridwidth = 4;
		grid14_x1_y1.weightx = 1.0;
		grid14_x1_y1.anchor = GridBagConstraints.CENTER;
		grid14_x1_y1.fill = GridBagConstraints.HORIZONTAL;
		grid14_x1_y1.insets = INSETS_0_5_0_5;
		updateInfoPanel.add(updateInfoLabel, grid14_x1_y1);
		fpsUpCheckBox.setText("fps変更");
		fpsUpCheckBox.setForeground(Color.blue);
		fpsUpCheckBox.setEnabled(true);
		fpsUpCheckBox.setToolTipText("フレームレートが低い動画のfps変更。");
		GridBagConstraints grid14_x0_y3 = new GridBagConstraints();
		grid14_x0_y3.gridx = 0;
		grid14_x0_y3.gridy = 3;
		grid14_x0_y3.weightx = 0.0;
		grid14_x0_y3.anchor = GridBagConstraints.NORTH;
		grid14_x0_y3.fill = GridBagConstraints.HORIZONTAL;
		grid14_x0_y3.insets = INSETS_0_5_0_5;
		updateInfoPanel.add(fpsUpCheckBox, grid14_x0_y3);
		GridBagConstraints grid14_x1_y3 = new GridBagConstraints();
		grid14_x1_y3.gridx = 1;
		grid14_x1_y3.gridy = 3;
		grid14_x1_y3.gridwidth = 1;
		grid14_x1_y3.weightx = 0.0;
		grid14_x1_y3.anchor = GridBagConstraints.CENTER;
		grid14_x1_y3.fill = GridBagConstraints.HORIZONTAL;
		grid14_x1_y3.insets = INSETS_0_5_0_5;
		updateInfoPanel.add(new JLabel("最小(fps)"), grid14_x1_y3);
		fpsMinTextField = new JTextField();
		fpsMinTextField.setText("23");
		fpsMinTextField.setForeground(Color.blue);
		GridBagConstraints grid14_x2_y3 = new GridBagConstraints();
		grid14_x2_y3.gridx = 2;
		grid14_x2_y3.gridy = 3;
		grid14_x2_y3.gridwidth = 1;
		grid14_x2_y3.weightx = 0.5;
		grid14_x2_y3.anchor = GridBagConstraints.NORTH;
		grid14_x2_y3.fill = GridBagConstraints.HORIZONTAL;
		grid14_x2_y3.insets = INSETS_0_5_0_5;
		updateInfoPanel.add(fpsMinTextField, grid14_x2_y3);
		GridBagConstraints grid14_x3_y3 = new GridBagConstraints();
		grid14_x3_y3.gridx = 3;
		grid14_x3_y3.gridy = 3;
		grid14_x3_y3.gridwidth = 1;
		grid14_x3_y3.weightx = 0.0;
		grid14_x3_y3.anchor = GridBagConstraints.CENTER;
		grid14_x3_y3.fill = GridBagConstraints.HORIZONTAL;
		grid14_x3_y3.insets = INSETS_0_5_0_5;
		updateInfoPanel.add(new JLabel("変換(fps)"), grid14_x3_y3);
		fpsUpTextFiled = new JTextField();
		fpsUpTextFiled.setText("25");
		fpsUpTextFiled.setForeground(Color.blue);
		GridBagConstraints grid14_x4_y3 = new GridBagConstraints();
		grid14_x4_y3.gridx = 4;
		grid14_x4_y3.gridy = 3;
		grid14_x4_y3.gridwidth = 1;
		grid14_x4_y3.weightx = 0.5;
		grid14_x4_y3.anchor = GridBagConstraints.NORTH;
		grid14_x4_y3.fill = GridBagConstraints.HORIZONTAL;
		grid14_x4_y3.insets = INSETS_0_5_0_5;
		updateInfoPanel.add(fpsUpTextFiled, grid14_x4_y3);
		fpsFilterRadioButton.setText("fpsフィルター使用");
		fpsFilterRadioButton.setForeground(Color.blue);
		fpsFilterRadioButton.setSelected(true);
		fpsFilterRadioButton.setToolTipText("ffmpeg内fpsフィルターを使う");
		GridBagConstraints grid14_x0_y4 = new GridBagConstraints();
		grid14_x0_y4.gridx = 0;
		grid14_x0_y4.gridy = 4;
		grid14_x0_y4.weightx = 0.0;
		grid14_x0_y4.anchor = GridBagConstraints.NORTH;
		grid14_x0_y4.fill = GridBagConstraints.HORIZONTAL;
		grid14_x0_y4.insets = INSETS_0_5_0_5;
		updateInfoPanel.add(fpsFilterRadioButton, grid14_x0_y4);
		fpsConvRadioButton.setText("2path-fps変換(1.50互換)");
		fpsConvRadioButton.setSelected(false);
		fpsConvRadioButton.setToolTipText("2回変換を使う");
		GridBagConstraints grid14_x1_y4 = new GridBagConstraints();
		grid14_x1_y4.gridx = 1;
		grid14_x1_y4.gridy = 4;
		grid14_x1_y4.gridwidth = 3;
		grid14_x1_y4.anchor = GridBagConstraints.NORTH;
		grid14_x1_y4.fill = GridBagConstraints.HORIZONTAL;
		grid14_x1_y4.insets = INSETS_0_5_0_5;
		updateInfoPanel.add(fpsConvRadioButton, grid14_x1_y4);
//

		soundOnlyCheckBox.setText("映像なし許可");
		soundOnlyCheckBox.setForeground(Color.blue);
		soundOnlyCheckBox.setToolTipText("映像が認識できない時音声のみコメント付きに変換する");
		GridBagConstraints grid14_x0_y5 = new GridBagConstraints();
		grid14_x0_y5.gridx = 0;
		grid14_x0_y5.gridy = 5;
		grid14_x0_y5.weightx = 0.0;
		grid14_x0_y5.anchor = GridBagConstraints.NORTH;
		grid14_x0_y5.fill = GridBagConstraints.HORIZONTAL;
		grid14_x0_y5.insets = INSETS_0_5_0_5;
		updateInfoPanel.add(soundOnlyCheckBox, grid14_x0_y5);
		GridBagConstraints grid14_x1_y5 = new GridBagConstraints();
		grid14_x1_y5.gridx = 1;
		grid14_x1_y5.gridy = 5;
		grid14_x1_y5.gridwidth = 1;
		grid14_x1_y5.weightx = 0.0;
		grid14_x1_y5.anchor = GridBagConstraints.CENTER;
		grid14_x1_y5.fill = GridBagConstraints.HORIZONTAL;
		grid14_x1_y5.insets = INSETS_0_5_0_5;
		updateInfoPanel.add(new JLabel("代替サムネ"), grid14_x1_y5);
		thumbTextFiled = new JTextField();
		thumbTextFiled.setText(THUMB_DEFALT_STRING);
		thumbTextFiled.setForeground(Color.blue);
		GridBagConstraints grid14_x2_y5 = new GridBagConstraints();
		grid14_x2_y5.gridx = 2;
		grid14_x2_y5.gridy = 5;
		grid14_x2_y5.gridwidth = 2;
		grid14_x2_y5.weightx = 0.5;
		grid14_x2_y5.anchor = GridBagConstraints.NORTH;
		grid14_x2_y5.fill = GridBagConstraints.HORIZONTAL;
		grid14_x2_y5.insets = INSETS_0_5_0_5;
		updateInfoPanel.add(thumbTextFiled, grid14_x2_y5);

		JPanel liveOperationPanel0 = new JPanel();
		liveOperationPanel0.setLayout(new BorderLayout());
		liveOperationLabel.setText("運営コメ簡易変更");
		liveOperationLabel.setForeground(Color.blue);
		liveOperationLabel.setToolTipText("コメント付き動画設定タブに移動しました");
		liveOperationPanel0.add(liveOperationLabel, BorderLayout.CENTER);
		BasicArrowButton liveOperationArrow = new BasicArrowButton(SwingConstants.SOUTH);
		liveOperationArrow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openConvertedVideoSavingTabbedPanel();
			}
		});
		liveOperationPanel0.add(liveOperationArrow, BorderLayout.WEST);
		GridBagConstraints grid14_x0_y6 = new GridBagConstraints();
		grid14_x0_y6.gridx = 0;
		grid14_x0_y6.gridy = 6;
		grid14_x0_y6.gridwidth = 1;
		grid14_x0_y6.weightx = 0.0;
		grid14_x0_y6.anchor = GridBagConstraints.NORTH;
		grid14_x0_y6.fill = GridBagConstraints.HORIZONTAL;
		grid14_x0_y6.insets = INSETS_0_5_0_5;
		updateInfoPanel.add(liveOperationPanel0, grid14_x0_y6);

		premiumColorCheckBox.setText("プレミアムカラーチェック有効");
		premiumColorCheckBox.setForeground(Color.blue);
		premiumColorCheckBox.setToolTipText("一般会員のプレミアムカラー使用を無効にします。(1.60以下のバグ修正)");
		GridBagConstraints grid14_x2_y6 = new GridBagConstraints();
		grid14_x2_y6.gridx = 1;
		grid14_x2_y6.gridy = 6;
		grid14_x2_y6.gridwidth = 3;
		grid14_x2_y6.weightx = 0.0;
		grid14_x2_y6.anchor = GridBagConstraints.NORTH;
		grid14_x2_y6.fill = GridBagConstraints.HORIZONTAL;
		grid14_x2_y6.insets = INSETS_0_5_0_5;
		updateInfoPanel.add(premiumColorCheckBox, grid14_x2_y6);

		SavingVideoCheckBox.setText("動画をダウンロードする");
		disableEcoCheckBox.setText("エコノミー時は中止");
		disableEcoCheckBox.setForeground(Color.blue);
		ShowSavingVideoFileDialogButton.setText("参照");
		ShowSavingVideoFileDialogButton
				.addActionListener(new MainFrame_ShowSavingVideoDialogButton_actionAdapter(
						this));
		Video_SaveFolderRadioButton.setText("保存するフォルダを指定し、ファイル名は自動で決定する");
		openVideoSaveFolderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
				{openFolder(getFile(VideoSavedFolderField.getText()));}
		});
		ShowSavingVideoFolderDialogButton.setText("参照");
		ShowSavingVideoFolderDialogButton
				.addActionListener(new MainFrame_ShowSavingVideoFolderDialogButton_actionAdapter(
						this));
		Video_SaveFileRadioButton.setText("保存するファイル名を指定する");
		openVideoSaveFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
				{openFileParent(getFile(VideoSavedFileField.getText()));}
		});
		CommentSaveInfoPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				"コメント保存設定"
			));
			//	,TitledBorder.LEADING, TitledBorder.TOP,
			//	new Font("MS UI Gothic", Font.PLAIN, 12), Color.black));
		CommentSaveInfoPanel.setLayout(gridBagLayout4);
		SavingCommentCheckBox.setText("コメントをダウンロードする");
		appendCommentCheckBox.setText("追加モードで保存");
		appendCommentCheckBox.addActionListener(new MainFrame_noticePop(this));
		AddTimeStampToCommentCheckBox.setText("コメントファイル名に日時を付加する（フォルダを指定した時のみ）");
		AddTimeStampToCommentCheckBox.setForeground(Color.blue);
		AddTimeStampToCommentCheckBox.setToolTipText("過去ログにも現在のコメントにも日時が付く");
		ShowSavingCommentFileDialogButton.setText("参照");
		ShowSavingCommentFileDialogButton
				.addActionListener(new MainFrame_ShowSavingCommentDialogButton_actionAdapter(
						this));
		Comment_SaveFolderRadioButton.setText("保存するフォルダを指定し、ファイル名は自動で決定する");
		openCommentSaveFolderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
				{openFolder(getFile(CommentSavedFolderField.getText()));}
		});
		ShowSavingCommentFolderDialogButton.setText("参照");
		ShowSavingCommentFolderDialogButton
				.addActionListener(new MainFrame_ShowSavingCommentFolderDialogButton_actionAdapter(
						this));
		Comment_SaveFileRadioButton.setText("保存するファイル名を指定する");
		openCommentSaveFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
				{openFileParent(getFile(CommentSavedFileField.getText()));}
		});
 		OldCommentModePanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				"投稿者コメント保存説明・コメント表示モード設定", TitledBorder.LEADING, TitledBorder.TOP,
				getFont(), Color.blue));
 		OldCommentModePanel.setLayout(gridBagLayout11);
 		commentModeComboBox.setForeground(Color.blue);
		OwnerCommentNoticeLabel1.setText("投稿者コメント保存　取得数１０００ 　フォルダ指定は通常コメントの設定通り");
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
		openConvSaveFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
				{openFileParent(getFile(ConvertedVideoSavedFileField.getText()));}
		});
		openConvSaveFolderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
				{openFolder(getFile(ConvertedVideoSavedFolderField.getText()));}
		});
		ShowSavingConvertedVideoFileDialogButton.setText("参照");
		ShowSavingConvertedVideoFileDialogButton
				.addActionListener(new MainFrame_ShowSavingConvertedVideoDialogButton_actionAdapter(
						this));
		autoPlayCheckBox.setText("変換後自動再生(拡張子の既定値)");
		autoPlay2CheckBox.setText("自動再生");
		autoPlayCheckBox.setForeground(Color.blue);
		autoPlay2CheckBox.setForeground(Color.blue);
		autoPlay2CheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				autoPlayCheckBox.setSelected(autoPlay2CheckBox.isSelected());
				autoPlay.setSelected(autoPlay2CheckBox.isSelected());
			}
		});
		OptionalThreadInfoPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				"オプショナルスレッド設定", TitledBorder.LEADING, TitledBorder.TOP,
				getFont(), Color.blue));
		OptionalThreadInfoPanel.setLayout(new GridBagLayout());
		OptionalthreadLabel.setText("コミュニティ動画で通常コメントをオプショナルスレッドで読み込みます");
		OptionalthreadLabel.setForeground(Color.blue);
		OptionalTranslucentCheckBox.setText("通常コメントを半透明にする");
		OptionalTranslucentCheckBox.setForeground(Color.blue);
		FFMpegTabPanel.setLayout(gridBagLayout6);
		FFMpegTab2Panel.setLayout(new GridBagLayout());
		PathSettingPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "オプションフォルダの位置の設定",
				TitledBorder.LEADING, TitledBorder.TOP,
				getFont(), Color.blue));
		PathSettingPanel.setLayout(gridBagLayout7);
		VhookPathSettingPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "拡張Vhookライブラリの設定"));
		VhookPathSettingPanel.setLayout(new GridBagLayout());
		FFmpegPathSettingPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "FFmpegの位置の設定"));
		FFmpegPathSettingPanel.setLayout(new GridBagLayout());
		FFmpegPathLabel.setText("FFmpeg");
		CheckFFmpegVersionLabel.setText("FFmpegのバージョンを表示する");
		CheckFFmpegVersionLabel.setForeground(Color.blue);
		CheckFFmpegVersionButton.setText("表示");
		CheckFFmpegVersionButton.setToolTipText("指定されたFFmpegのバージョンを表示する");
		CheckFFmpegVersionButton.setForeground(Color.blue);
		CheckFFmpegVersionButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){ FFVersionButton_actionPerformed(e); }
		});
		SettingFFmpegPathButton.setText("参照");
		SettingFFmpegPathButton
				.addActionListener(new MainFrame_SettingFFmpegPathButton_actionAdapter(this));
		SettingOptionPathButton.setText("参照");
		SettingOptionPathButton.setForeground(Color.blue);
		SettingOptionPathButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { SettingOptionPathButton_actionPerformed(e); }
		});
		VhookSettingLabel.setText("拡張Vhookの位置の設定と自動判定の選択をする");
		VhookSettingLabel.setForeground(Color.blue);
		VhookSettingLabel.setToolTipText("従来4:3とワイド16:9を両方チェックすると自動判定を行う");
		VhookPathLabel.setText("拡張vhook 従来 ");
		UseVhookCheckBox.setText("使用する（デフォルト 及び 4:3用）");
		UseVhookCheckBox.setForeground(Color.blue);
		UseVhookCheckBox.setToolTipText("FFmpegの設定１を参照する");
		SettingVhookPathButton.setText("参照");
		SettingVhookPathButton
				.addActionListener(new MainFrame_SettingVhookPathButton_actionAdapter(this));
		VhookWidePathLabel.setText("拡張vhook ワイド ");
		VhookWidePathLabel.setForeground(Color.blue);
		UseVhookWideCheckBox.setText("使用する（16:9用）");
		UseVhookWideCheckBox.setForeground(Color.blue);
		UseVhookWideCheckBox.setToolTipText("FFmpegの設定２を参照する");
		SettingVhookWidePathButton.setText("参照");
		SettingVhookWidePathButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SettingVhookWidePathButton_actionPerformed(e); }
		});
		FFmpegSettingPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				"FFmpegの設定１ （拡張Vhook 従来を選択した時）"));
		FFmpegSettingPanel.setLayout(gridBagLayout9);
		WideFFmpegSettingPanel.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
			"FFmpegの設定２ （拡張Vhook ワイドを選択した時）",
			TitledBorder.LEADING, TitledBorder.TOP, getFont(), Color.blue));
		WideFFmpegSettingPanel.setLayout(new GridBagLayout());
		additionalOptionPanel.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
			"FFmpeg追加設定 (オプションを上書き/追加します)",
			TitledBorder.LEADING, TitledBorder.TOP, getFont(), Color.blue));
		FontPathLabel.setText("フォントパス");
		SettingFontPathButton.setText("参照");
		SettingFontPathButton
			.addActionListener(new MainFrame_SettingFontPathButton_actionAdapter(this));
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
		jMenuFile.add(jMenuOpen);
		jMenuFile.add(jMenuAdd);
		jMenuFile.add(jMenuSave);
		jMenuFile.add(jMenuSaveAs);
		jMenuFile.add(jMenuInit);
		jMenuFile.add(jMenuFileExit);
		jMenuBar1.add(jMenuDetail);
		jMenuDetail.add(jMenuNGConfig);
		jMenuDetail.add(jMenuAprilFool);
		jMenuBar1.add(jMenuHelp);
		jMenuHelp.add(jMenuHelpAbout);
		jMenuHelp.add(jMenuHelpReadmeNew);
		jMenuHelp.add(jMenuHelpReadme);
		jMenuHelp.add(jMenuHelpReadmePlus);
		jMenuHelp.add(jMenuHelpReadmeFirst);
		jMenuHelp.add(jMenuHelpErrorTable);
		jMenuHelp.add(jMenuHelpFF);
		jMenuHelp.add(jMenuHelpFormats);
		jMenuHelp.add(jMenuHelpCodecs);
		jMenuHelp.add(jMenuHelpProtocols);
		jMenuHelp.add(jMenuHelpFilters);
		setJMenuBar(jMenuBar1);
		/* ビデオグループ */
		VideoSaveButtonGroup.add(Video_SaveFileRadioButton);
		VideoSaveButtonGroup.add(Video_SaveFolderRadioButton);

		CommentSaveButtonGroup.add(Comment_SaveFileRadioButton);
		CommentSaveButtonGroup.add(Comment_SaveFolderRadioButton);

		ConvSaveButtonGroup.add(Conv_SaveFileRadioButton);
		ConvSaveButtonGroup.add(Conv_SaveFolderRadioButton);

		thumbInfoExtButtonGroup.add(saveThumbInfoExtTxtRadioButton);
		thumbInfoExtButtonGroup.add(saveThumbInfoExtXmlRadioButton);

		fpsConvButtonGroup.add(fpsFilterRadioButton);
		fpsConvButtonGroup.add(fpsConvRadioButton);

		StatusPanel.setLayout(new BorderLayout());
		StatusPanel.add(elapsedTimeBar,BorderLayout.WEST);
		StatusPanel.add(vhookInfoBar, BorderLayout.EAST);
		StatusPanel.add(statusBar, BorderLayout.SOUTH);
		contentPane.add(StatusPanel, BorderLayout.SOUTH);
		contentPane.add(MainTabbedPane, BorderLayout.CENTER);
		contentPane.add(VideoInfoPanel, BorderLayout.NORTH);
		UserInfoPanel.add(PasswordField, new GridBagConstraints(1, 1, 1, 1,
				1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				INSETS_0_5_5_5, 0, 0));
		UserInfoPanel.add(MailAddrField, new GridBagConstraints(1, 0, 1, 1,
				1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				INSETS_0_5_5_5, 0, 0));
		UserInfoPanel.add(PasswordLabel, new GridBagConstraints(0, 1, 1, 1,
				0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(0, 5, 5, 0), 0, 0));
		UserInfoPanel.add(MailAddrLabel, new GridBagConstraints(0, 0, 1, 1,
				0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(0, 5, 5, 0), 0, 0));
		PathSettingPanel.add(OptionPathField, new GridBagConstraints(0, 1, 2, 1,
				1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, INSETS_0_5_0_5, 0, 0));
		GridBagConstraints grid7_x2_y1_74 = new GridBagConstraints(2, 1, 1, 1,
				0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, INSETS_0_5_0_5, 0, 0);
		PathSettingPanel.add(SettingOptionPathButton, grid7_x2_y1_74);
		GridBagConstraints grid7_x0_y0_ = new GridBagConstraints();
		grid7_x0_y0_.gridx = 0;
		grid7_x0_y0_.gridy = 0;
		grid7_x0_y0_.gridwidth = 2;
		grid7_x0_y0_.weightx = 1.0;
		grid7_x0_y0_.anchor = GridBagConstraints.CENTER;
		grid7_x0_y0_.fill = GridBagConstraints.HORIZONTAL;
		grid7_x0_y0_.insets = INSETS_0_5_0_5;
		VhookPathSettingPanel.add(VhookSettingLabel, grid7_x0_y0_);
		GridBagConstraints grid7_x0_y2_54 = new GridBagConstraints();
		grid7_x0_y2_54.gridx = 0;
		grid7_x0_y2_54.gridy = 2;
		grid7_x0_y2_54.weightx = 0.0;
		grid7_x0_y2_54.weighty = 0.0;
		grid7_x0_y2_54.anchor = GridBagConstraints.CENTER;
		grid7_x0_y2_54.fill = GridBagConstraints.HORIZONTAL;
		grid7_x0_y2_54.insets = INSETS_0_5_0_5;
		VhookPathSettingPanel.add(VhookPathLabel, grid7_x0_y2_54);
		GridBagConstraints grid7_x1_y2_90 = new GridBagConstraints();
		grid7_x1_y2_90.gridx = 1;
		grid7_x1_y2_90.gridy = 2;
		grid7_x1_y2_90.gridwidth = 2;
		grid7_x1_y2_90.weightx = 1.0;
		grid7_x1_y2_90.anchor = GridBagConstraints.WEST;
		grid7_x1_y2_90.fill = GridBagConstraints.HORIZONTAL;
		grid7_x1_y2_90.insets = INSETS_0_5_0_5;
		VhookPathSettingPanel.add(UseVhookCheckBox, grid7_x1_y2_90);
		GridBagConstraints grid7_x0_y3_ = new GridBagConstraints();
		grid7_x0_y3_.gridx = 0;
		grid7_x0_y3_.gridy = 3;
		grid7_x0_y3_.gridwidth = 2;
		grid7_x0_y3_.weightx = 1.0;
		grid7_x0_y3_.anchor = GridBagConstraints.WEST;
		grid7_x0_y3_.fill = GridBagConstraints.HORIZONTAL;
		grid7_x0_y3_.insets = INSETS_0_5_0_5;
		VhookPathSettingPanel.add(VhookPathField, grid7_x0_y3_);
		GridBagConstraints grid7_x2_y3_ = new GridBagConstraints();
		grid7_x2_y3_.gridx = 2;
		grid7_x2_y3_.gridy = 3;
		grid7_x2_y3_.gridwidth = 1;
		grid7_x2_y3_.weightx = 0.0;
		grid7_x2_y3_.insets = INSETS_0_5_5_5;
		VhookPathSettingPanel.add(SettingVhookPathButton, grid7_x2_y3_);
		GridBagConstraints grid7_x0_y4_91 = new GridBagConstraints();
		grid7_x0_y4_91.gridx = 0;
		grid7_x0_y4_91.gridy = 4;
		grid7_x0_y4_91.gridwidth = 1;
		grid7_x0_y4_91.weightx = 0.0;
		grid7_x0_y4_91.anchor = GridBagConstraints.CENTER;
		grid7_x0_y4_91.fill = GridBagConstraints.HORIZONTAL;
		grid7_x0_y4_91.insets = INSETS_0_5_0_5;
		VhookPathSettingPanel.add(VhookWidePathLabel, grid7_x0_y4_91);
		GridBagConstraints grid7_x1_y4_92 = new GridBagConstraints();
		grid7_x1_y4_92.gridx = 1;
		grid7_x1_y4_92.gridy = 4;
		grid7_x1_y4_92.gridwidth = 1;
		grid7_x1_y4_92.weightx = 1.0;
		grid7_x1_y4_92.anchor = GridBagConstraints.WEST;
		grid7_x1_y4_92.fill = GridBagConstraints.HORIZONTAL;
		grid7_x1_y4_92.insets = INSETS_0_5_0_5;
		VhookPathSettingPanel.add(UseVhookWideCheckBox, grid7_x1_y4_92);
		GridBagConstraints grid7_x0_y5_93 = new GridBagConstraints();
		grid7_x0_y5_93.gridx = 0;
		grid7_x0_y5_93.gridy = 5;
		grid7_x0_y5_93.gridwidth = 2;
		grid7_x0_y5_93.weightx = 1.0;
		grid7_x0_y5_93.anchor = GridBagConstraints.WEST;
		grid7_x0_y5_93.fill = GridBagConstraints.HORIZONTAL;
		grid7_x0_y5_93.insets = INSETS_0_5_0_5;
		VhookPathSettingPanel.add(VhookWidePathField, grid7_x0_y5_93);
		GridBagConstraints grid7_x2_y5_94 = new GridBagConstraints();
		grid7_x2_y5_94.gridx = 2;
		grid7_x2_y5_94.gridy = 5;
		grid7_x2_y5_94.gridwidth = 1;
		grid7_x2_y5_94.weightx = 0.0;
		grid7_x2_y5_94.insets = INSETS_0_5_0_5;
		VhookPathSettingPanel.add(SettingVhookWidePathButton, grid7_x2_y5_94);
// option default OR normal(4:3)
		FFmpegSettingPanel.add(getFFmpegOptionComboBoxPanel(),grid9_x0_y1_55);
		FFmpegSettingPanel.add(ExtOptionLabel, grid9_x0_y2_56);
		FFmpegSettingPanel.add(getExtOptionField(), grid9_x1_y2_57);
		FFmpegSettingPanel.add(MainOptionLabel, grid9_x0_y3_48);
		FFmpegSettingPanel.add(MainOptionField, grid9_x1_y3_51);
		FFmpegSettingPanel.add(InLabel, grid9_x0_y4_49);
		FFmpegSettingPanel.add(CommandLineInOptionField, grid9_x1_y4_52);
		FFmpegSettingPanel.add(OutLabel, grid9_x0_y5_50);
		FFmpegSettingPanel.add(CommandLineOutOptionField, grid9_x1_y5_53);
// option wide(16:9)
		WideFFmpegSettingPanel.add(getWideFFmpegOptionComboBoxPanel(),grid9_x0_y1_55);
		WideFFmpegSettingPanel.add(new JLabel(ExtOptionLabel.getText()), grid9_x0_y2_56);
		WideFFmpegSettingPanel.add(wideExtOptionField, grid9_x1_y2_57);
		WideFFmpegSettingPanel.add(new JLabel(MainOptionLabel.getText()), grid9_x0_y3_48);
		WideFFmpegSettingPanel.add(wideMainOptionField, grid9_x1_y3_51);
		WideFFmpegSettingPanel.add(new JLabel(InLabel.getText()), grid9_x0_y4_49);
		WideFFmpegSettingPanel.add(wideCommandLineInOptionField, grid9_x1_y4_52);
		WideFFmpegSettingPanel.add(new JLabel(OutLabel.getText()), grid9_x0_y5_50);
		WideFFmpegSettingPanel.add(wideCommandLineOutOptionField, grid9_x1_y5_53);
		WideFFmpegSettingPanel.setForeground(Color.blue);
// Addtional opttion
		additionalOptionPanel.setLayout(new GridBagLayout());
		GridBagConstraints grid90_x0_y0_ = new GridBagConstraints();
		grid90_x0_y0_.gridx = 0;
		grid90_x0_y0_.gridy = 0;
		grid90_x0_y0_.anchor = GridBagConstraints.WEST;
		grid90_x0_y0_.insets = INSETS_0_5_5_5;
		additionalOptionPanel.add(new JLabel("設定１に追加"), grid90_x0_y0_);
		GridBagConstraints grid90_x1_y0_ = new GridBagConstraints();
		grid90_x1_y0_.gridx = 1;
		grid90_x1_y0_.gridy = 0;
		grid90_x1_y0_.weightx = 1.0;
		grid90_x1_y0_.fill = GridBagConstraints.HORIZONTAL;
		grid90_x1_y0_.insets = INSETS_0_5_5_5;
		additionalOptionPanel.add(additionalOptionFiled, grid90_x1_y0_);
		GridBagConstraints grid90_x0_y1_ = new GridBagConstraints();
		grid90_x0_y1_.gridx = 0;
		grid90_x0_y1_.gridy = 1;
		grid90_x0_y1_.anchor = GridBagConstraints.WEST;
		grid90_x0_y1_.insets = INSETS_0_5_0_5;
		additionalOptionPanel.add(new JLabel("設定２に追加"), grid90_x0_y1_);
		GridBagConstraints grid90_x1_y1_ = new GridBagConstraints();
		grid90_x1_y1_.gridx = 1;
		grid90_x1_y1_.gridy = 1;
		grid90_x1_y1_.weightx = 1.0;
		grid90_x1_y1_.fill = GridBagConstraints.HORIZONTAL;
		grid90_x1_y1_.insets = INSETS_0_5_0_5;
		additionalOptionPanel.add(wideAdditionalOptionFiled, grid90_x1_y1_);
// Added FFmpegPathSettingPanel form here
//		FFmpegPathSettingPanel.add(FFmpegPathLabel, new GridBagConstraints(0, 0, 1,
//				1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
//				INSETS_0_5_0_5, 0, 0));
		FFmpegPathSettingPanel.add(FFmpegPathField, new GridBagConstraints(0, 1, 2,
				1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, INSETS_0_5_0_0, 0, 0));
		GridBagConstraints grid__x2_y1_108 = new GridBagConstraints(2, 1,
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, INSETS_0_5_0_5, 0, 0);
		FFmpegPathSettingPanel.add(SettingFFmpegPathButton, grid__x2_y1_108);
// end FFmpegPathSettingPanel
		VideoInfoPanel.add(OpPanel, new GridBagConstraints(0, 0, 1, 1, 1.0,
				0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				INSETS_0_0_0_0, 0, 0));
		VideoInfoPanel.add(DoButton, grid1_x1_y0_71);
		OpPanel.add(VideoID_Label, grid10_x0_y0_67);
		OpPanel.add(VideoID_TextField, grid10_x1_y0_68);
		OpPanel.add(historyBackButton, grid10_x1_y0_68B);
		OpPanel.add(historyForwardButton, grid10_x2_y0_68C);
		historyBackButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String vid = requestHistory.back();
				VideoID_TextField.setText(vid);
			}
		});
		historyForwardButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String vid = requestHistory.next();
				VideoID_TextField.setText(vid);
			}
		});
		OpPanel.add(WayBackLabel, grid10_x0_y1_69);
		OpPanel.add(WayBackField, grid10_x1_y1_70);
		MainTabbedPane.add(BasicInfoTabPanel, "基本設定");
		MainTabbedPane.add(SavingInfoTabPanel, "保存設定");
		MainTabbedPane.add(FFMpegInfoTabPanel, "動画設定");
		MainTabbedPane.addTab("変換設定", null, getConvertingSettingPanel(), null);
		MainTabbedPane.addTab("管理", getManagentPanel());
		SavingInfoTabPanel.add(getSaveInfoTabPaneEach(), grid2_x__y__35);
		FFmpegInfoTabPaneEach.addTab("変換オプション設定(共通)", null, FFMpegTab2Panel, null);
		FFmpegInfoTabPaneEach.addTab("変換オプション設定(従来)", null, FFMpegTabPanel, null);
		FFMpegInfoTabPanel.setLayout(new GridBagLayout());
		GridBagConstraints grid__x__y__105 = new GridBagConstraints();
		grid__x__y__105.fill = GridBagConstraints.BOTH;
		grid__x__y__105.weightx = 1.0;
		grid__x__y__105.weighty = 1.0;
		FFMpegInfoTabPanel.add(FFmpegInfoTabPaneEach, grid__x__y__105);
		BasicInfoTabPanel.add(UserInfoPanel, grid12_x0_y0_7);
		BasicInfoTabPanel.add(getProxyInfoPanel(), grid12_x0_y1_6);
		BasicInfoTabPanel.add(BrowserInfoPanel, grid12_x0_y2_95);

		GridBagConstraints grid12_x0_y3 = new GridBagConstraints();
		grid12_x0_y3.gridx = 0;
		grid12_x0_y3.gridy = 3;
		grid12_x0_y3.weightx = 1.0;
		grid12_x0_y3.weighty = 1.0;
		grid12_x0_y3.insets = new Insets(5, 5, 0, 5);
		grid12_x0_y3.fill = GridBagConstraints.HORIZONTAL;
		grid12_x0_y3.anchor = GridBagConstraints.NORTH;
		BasicInfoTabPanel.add(updateInfoPanel,grid12_x0_y3);

		VhookSettingPanel.setLayout(gridBagLayout8);
		VhookSettingPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "拡張vhookライブラリの設定"));
		VhookSettingPanel.add(getNotUseVhookCheckBox(), grid8_x0_y0_58);
		VhookSettingPanel.add(ViewCommentLabel, grid8_x0_y1_66);
		VhookSettingPanel.add(getViewCommentField(), grid8_x1_y1_65);
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
		opaqueRateTextField.setForeground(Color.blue);
		opaqueRateTextField.setToolTipText("不透明度（既定 オフ 0.0-0.3/オン 1.0）");
		VhookSettingPanel.add(opaqueRateTextField, grid8_x2_y9_101);
		VhookSettingPanel.add(new JLabel("設定 0～1"),grid8_x3_y9_103);
		commentSpeedCheckBox.setText("コメント速度（Pixel/Sec）");
		commentSpeedCheckBox.setForeground(Color.blue);
		VhookSettingPanel.add(commentSpeedCheckBox, grid8_x0_y10_0);
		commentSpeedTextField.setForeground(Color.blue);
		commentSpeedTextField.setToolTipText("公式の最小値は約138Pixel/Sec");
		VhookSettingPanel.add(commentSpeedTextField, grid8_x2_y10_1);

		CommentSaveInfoPanel.add(SavingCommentCheckBox, grid4_x0_y6_16);
		CommentSaveInfoPanel.add(appendCommentCheckBox, grid4_x1_y6_);
		CommentSaveInfoPanel.add(AddTimeStampToCommentCheckBox, grid4_x0_y7_86);
		CommentSaveInfoPanel.add(getDelCommentCheckBox(), grid4_x0_y8_25);
		CommentSaveInfoPanel.add(getFixCommentNumCheckBox(),grid4_x0_y9_26);
		CommentSaveInfoPanel.add(CommentNumLabel, grid4_x0_y10_17);
		CommentSaveInfoPanel.add(CommentNumField, grid4_x1_y10_22);
		CommentSaveInfoPanel.add(Comment_SaveFolderRadioButton,grid4_x0_y11_18);
		CommentSaveInfoPanel.add(CommentSavedFolderField, grid4_x0_y12_19);
		CommentSaveInfoPanel.add(openCommentSaveFolderButton, grid4_x2_y12);
		CommentSaveInfoPanel.add(ShowSavingCommentFolderDialogButton,grid4_x3_y12_23);
		CommentSaveInfoPanel.add(Comment_SaveFileRadioButton,grid4_x0_y13_20);
		CommentSaveInfoPanel.add(CommentSavedFileField, grid4_x0_y14_21);
		CommentSaveInfoPanel.add(openCommentSaveFileButton, grid4_x2_y14);
		CommentSaveInfoPanel.add(ShowSavingCommentFileDialogButton,grid4_x3_y14_24);
		OldCommentModePanel.add(commentModeComboBox, grid11_x0_y0_75);
		OldCommentModePanel.add(OwnerCommentNoticeLabel1, grid11_x0_y1_76);
		ConvertedVideoSavingInfoPanel.add(SavingConvertedVideoCheckBox,
				new GridBagConstraints(0, 0, 3, 1, 1.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					INSETS_0_5_0_5, 0, 0));
		ConvertedVideoSavingInfoPanel.add(ConvertWithCommentCheckBox,
				grid5_x0_y1_78);
		ConvertedVideoSavingInfoPanel.add(ConvertWithOwnerCommentCheckBox,
				grid5_x0_y2_81);
		ConvertedVideoSavingInfoPanel.add(Conv_SaveFolderRadioButton,
				grid5_x0_y3_40);
		ConvertedVideoSavingInfoPanel.add(getNotAddVideoID_ConvVideoCheckBox(),
				grid5_x0_y4_39);
		ConvertedVideoSavingInfoPanel.add(AddOption_ConvVideoFileCheckBox,
				gird5_x0_y5_89);
		ConvertedVideoSavingInfoPanel.add(ConvertedVideoSavedFolderField,
				grid5_x0_y6_41);
		ConvertedVideoSavingInfoPanel.add(openConvSaveFolderButton,
				grid5_x2_y6);
		ConvertedVideoSavingInfoPanel.add(ShowSavingConvertedVideoFolderDialogButton,
				grid5_x3_y6_44);
		ConvertedVideoSavingInfoPanel.add(Conv_SaveFileRadioButton,
				grid5_x0_y7_42);
		ConvertedVideoSavingInfoPanel.add(ConvertedVideoSavedFileField,
				grid5_x0_y8_43);
		ConvertedVideoSavingInfoPanel.add(openConvSaveFileButton,
				grid5_x2_y8);
		ConvertedVideoSavingInfoPanel.add(
				ShowSavingConvertedVideoFileDialogButton,	grid5_x3_y8_45);
		ConvertedVideoSavingInfoPanel.add(autoPlayCheckBox, grid5_x0_y9);
		GridBagConstraints grid__x_y_000_ = new GridBagConstraints();
		grid__x_y_000_.gridx = 0;
		grid__x_y_000_.gridy = 0;
		grid__x_y_000_.gridwidth = 1;
		grid__x_y_000_.gridheight = 1;
		grid__x_y_000_.weightx = 1.0;
		grid__x_y_000_.weighty = 0.0;
		grid__x_y_000_.anchor = GridBagConstraints.CENTER;
		grid__x_y_000_.fill = GridBagConstraints.HORIZONTAL;
		grid__x_y_000_.insets = INSETS_0_5_0_5;
		OptionalThreadInfoPanel.add(OptionalthreadLabel, grid__x_y_000_);
		grid__x_y_000_ = new GridBagConstraints();
		grid__x_y_000_.gridx = 0;
		grid__x_y_000_.gridy = 1;
		grid__x_y_000_.gridwidth = 1;
		grid__x_y_000_.gridheight = 1;
		grid__x_y_000_.weightx = 1.0;
		grid__x_y_000_.weighty = 1.0;
		grid__x_y_000_.anchor = GridBagConstraints.CENTER;
		grid__x_y_000_.fill = GridBagConstraints.HORIZONTAL;
		grid__x_y_000_.insets = INSETS_0_5_0_5;
		OptionalThreadInfoPanel.add(OptionalTranslucentCheckBox, grid__x_y_000_);
		GridBagConstraints grid6_x0_y0_110 = new GridBagConstraints();
		grid6_x0_y0_110.gridx = 0;
		grid6_x0_y0_110.gridy = 1;
		grid6_x0_y0_110.weightx = 1.0;
		grid6_x0_y0_110.weighty = 0.0;
		grid6_x0_y0_110.anchor = GridBagConstraints.NORTH;
		grid6_x0_y0_110.fill = GridBagConstraints.HORIZONTAL;
		grid6_x0_y0_110.insets = INSETS_5_5_5_5;
		FFMpegTabPanel.add(VhookPathSettingPanel, grid6_x0_y0_110);
		GridBagConstraints grid6_x0_y1_1 = new GridBagConstraints(0, 1, 1, 1,
				1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, INSETS_0_5_0_5, 0, 0);
		grid6_x0_y1_1.gridy = 2;
		grid6_x0_y1_1.anchor = GridBagConstraints.NORTH;
		FFMpegTabPanel.add(FFmpegSettingPanel, grid6_x0_y1_1);
		GridBagConstraints grid6_x0_y3_111 = new GridBagConstraints();
		grid6_x0_y3_111.gridx = 0;
		grid6_x0_y3_111.gridy = 3;
		grid6_x0_y3_111.weightx = 1.0;
		grid6_x0_y3_111.anchor = GridBagConstraints.NORTH;
		grid6_x0_y3_111.fill = GridBagConstraints.HORIZONTAL;
		grid6_x0_y3_111.insets = INSETS_0_5_0_5;
		FFMpegTabPanel.add(WideFFmpegSettingPanel, grid6_x0_y3_111);
		GridBagConstraints grid6_x0_y4_ = new GridBagConstraints();
		grid6_x0_y4_.gridx = 0;
		grid6_x0_y4_.gridy = 4;
		grid6_x0_y4_.weightx = 1.0;
		grid6_x0_y4_.weighty = 1.0;
		grid6_x0_y4_.anchor = GridBagConstraints.NORTH;
		grid6_x0_y4_.fill = GridBagConstraints.HORIZONTAL;
		grid6_x0_y4_.insets = INSETS_0_5_0_5;
		FFMpegTabPanel.add(additionalOptionPanel, grid6_x0_y4_);
		FFMpegTab2Panel.add(PathSettingPanel, new GridBagConstraints(0, 0, 1, 1,
				1.0, 0.0, GridBagConstraints.NORTHEAST,
				GridBagConstraints.HORIZONTAL, INSETS_0_5_0_5, 0, 0));
		GridBagConstraints grid__x0_y0_109 = new GridBagConstraints();
		grid__x0_y0_109.gridx = 0;
		grid__x0_y0_109.gridy = 1;
		grid__x0_y0_109.weightx = 1.0;
		grid__x0_y0_109.weighty = 0.0;
		grid__x0_y0_109.anchor = GridBagConstraints.NORTH;
		grid__x0_y0_109.fill = GridBagConstraints.BOTH;
		grid__x0_y0_109.insets = INSETS_0_5_0_5;
		FFMpegTab2Panel.add(FFmpegPathSettingPanel, grid__x0_y0_109);
		zqPlayerModePanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "拡張Vhookライブラリーの設定",
				TitledBorder.LEADING, TitledBorder.TOP,
				getFont(), Color.blue));
		zqPlayerModePanel.setForeground(Color.blue);
		zqPlayerModePanel.setLayout(new GridBagLayout());
		zqPlayerModePanel.setToolTipText("");
		zqPlayerModeCheckBox.setText("共通画面を使う　(使わない→従来画面を使う)");
		//zqPlayerModeCheckBox.setToolTipText("以下の設定が4:3と16:9が共通に使えます。");
		zqPlayerModeCheckBox.setForeground(Color.blue);
		GridBagConstraints gird_x0_y2_0 = new GridBagConstraints();
		gird_x0_y2_0 = new GridBagConstraints();
		gird_x0_y2_0.gridx = 0;
		gird_x0_y2_0.gridy = 0;
		gird_x0_y2_0.weightx = 1.0;
		gird_x0_y2_0.weighty = 0.0;
		gird_x0_y2_0.anchor = GridBagConstraints.NORTH;
		gird_x0_y2_0.fill = GridBagConstraints.BOTH;
		gird_x0_y2_0.insets = INSETS_0_5_0_5;
		zqPlayerModePanel.add(zqPlayerModeCheckBox, gird_x0_y2_0);
		zqVhookSettingLavel.setText("拡張Vhookの位置を設定する(アスペクト比共通)");
		gird_x0_y2_0 = new GridBagConstraints();
		gird_x0_y2_0.gridx = 0;
		gird_x0_y2_0.gridy = 1;
		gird_x0_y2_0.weightx = 1.0;
		gird_x0_y2_0.weighty = 0.0;
		gird_x0_y2_0.anchor = GridBagConstraints.NORTH;
		gird_x0_y2_0.fill = GridBagConstraints.BOTH;
		gird_x0_y2_0.insets = INSETS_0_5_5_5;
		zqPlayerModePanel.add(zqVhookSettingLavel,gird_x0_y2_0);
		gird_x0_y2_0 = new GridBagConstraints();
		gird_x0_y2_0.gridx = 0;
		gird_x0_y2_0.gridy = 2;
		gird_x0_y2_0.weightx = 1.0;
		gird_x0_y2_0.weighty = 0.0;
		gird_x0_y2_0.anchor = GridBagConstraints.NORTH;
		gird_x0_y2_0.fill = GridBagConstraints.BOTH;
		gird_x0_y2_0.insets = INSETS_0_5_0_5;
		zqPlayerModePanel.add(zqVhookPathField, gird_x0_y2_0);
		zqSettingVhookPathButton.setText("参照");
		zqSettingVhookPathButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SettingVhookZqPathButton_actionPerformed(e); }
		});
		gird_x0_y2_0 = new GridBagConstraints();
		gird_x0_y2_0.gridx = 1;
		gird_x0_y2_0.gridy = 2;
		gird_x0_y2_0.weightx = 0.0;
		gird_x0_y2_0.weighty = 0.0;
		gird_x0_y2_0.anchor = GridBagConstraints.NORTH;
		gird_x0_y2_0.fill = GridBagConstraints.BOTH;
		gird_x0_y2_0.insets = INSETS_0_5_0_5;
		zqPlayerModePanel.add(zqSettingVhookPathButton, gird_x0_y2_0);
		gird_x0_y2_0 = new GridBagConstraints();
		gird_x0_y2_0.gridx = 0;
		gird_x0_y2_0.gridy = 2;
		gird_x0_y2_0.weightx = 1.0;
		gird_x0_y2_0.weighty = 0.0;
		gird_x0_y2_0.anchor = GridBagConstraints.NORTH;
		gird_x0_y2_0.fill = GridBagConstraints.BOTH;
		gird_x0_y2_0.insets = INSETS_0_5_0_5;
		FFMpegTab2Panel.add(zqPlayerModePanel,gird_x0_y2_0);
// option Q
		zqFFmpegSettingPanel.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
			"共通FFmpegの設定",TitledBorder.LEADING,
			TitledBorder.TOP, getFont(), Color.blue));
		zqFFmpegSettingPanel.setLayout(new GridBagLayout());
		zqFFmpegSettingPanel.add(getZqFFmpegOptionComboBoxPanel(),grid9_x0_y1_55);
		zqFFmpegSettingPanel.add(new JLabel(ExtOptionLabel.getText()), grid9_x0_y2_56);
		zqFFmpegSettingPanel.add(zqExtOptionField, grid9_x1_y2_57);
		zqFFmpegSettingPanel.add(new JLabel(MainOptionLabel.getText()), grid9_x0_y3_48);
		zqFFmpegSettingPanel.add(zqMainOptionField, grid9_x1_y3_51);
		zqFFmpegSettingPanel.add(new JLabel(InLabel.getText()), grid9_x0_y4_49);
		zqFFmpegSettingPanel.add(zqCommandLineInOptionField, grid9_x1_y4_52);
		zqFFmpegSettingPanel.add(new JLabel(OutLabel.getText()), grid9_x0_y5_50);
		zqFFmpegSettingPanel.add(zqCommandLineOutOptionField, grid9_x1_y5_53);
		gird_x0_y2_0 = new GridBagConstraints();
		gird_x0_y2_0.gridx = 0;
		gird_x0_y2_0.gridy = 6;
		gird_x0_y2_0.weightx = 0.0;
		gird_x0_y2_0.weighty = 0.0;
		gird_x0_y2_0.anchor = GridBagConstraints.NORTH;
		gird_x0_y2_0.fill = GridBagConstraints.BOTH;
		gird_x0_y2_0.insets = INSETS_0_5_0_5;
		zqFFmpegSettingPanel.add(new JLabel("追加オプション"), gird_x0_y2_0);
		gird_x0_y2_0 = new GridBagConstraints();
		gird_x0_y2_0.gridx = 1;
		gird_x0_y2_0.gridy = 6;
		gird_x0_y2_0.weightx = 1.0;
		gird_x0_y2_0.weighty = 0.0;
		gird_x0_y2_0.gridwidth =3;
		gird_x0_y2_0.anchor = GridBagConstraints.NORTH;
		gird_x0_y2_0.fill = GridBagConstraints.BOTH;
		gird_x0_y2_0.insets = INSETS_0_0_0_5;
		zqFFmpegSettingPanel.add(zqAdditionalOptionFiled, gird_x0_y2_0);
		zqFFmpegSettingPanel.setForeground(Color.blue);
/*
 * 		デバッグ中 保留
		gird_x0_y2_0 = new GridBagConstraints();
		gird_x0_y2_0.gridy = 7;
		gird_x0_y2_0.gridx = 0;
		gird_x0_y2_0.weightx = 0.0;
		gird_x0_y2_0.weighty = 0.0;
		gird_x0_y2_0.anchor = GridBagConstraints.NORTH;
		gird_x0_y2_0.fill = GridBagConstraints.BOTH;
		gird_x0_y2_0.insets = INSETS_0_5_0_5;
		zqFFmpegSettingPanel.add(new JLabel("注釈"), gird_x0_y2_0);
		gird_x0_y2_0 = new GridBagConstraints();
		gird_x0_y2_0.gridy = 7;
		gird_x0_y2_0.gridx = 1;
		gird_x0_y2_0.gridwidth = 1;
		gird_x0_y2_0.weightx = 1.0;
		gird_x0_y2_0.weighty = 0.0;
		gird_x0_y2_0.gridwidth =3;
		gird_x0_y2_0.anchor = GridBagConstraints.NORTH;
		gird_x0_y2_0.fill = GridBagConstraints.BOTH;
		gird_x0_y2_0.insets = INSETS_0_0_0_5;
		zqFFmpegSettingPanel.add(zqOptionFileDescription, gird_x0_y2_0);
		zqFFmpegSettingPanel.setForeground(Color.blue);
*/
		gird_x0_y2_0 = new GridBagConstraints();
		gird_x0_y2_0.gridx = 0;
		gird_x0_y2_0.gridy = 4;
		gird_x0_y2_0.weightx = 1.0;
		gird_x0_y2_0.weighty = 0.0;
		gird_x0_y2_0.anchor = GridBagConstraints.NORTH;
		gird_x0_y2_0.fill = GridBagConstraints.BOTH;
		gird_x0_y2_0.insets = INSETS_0_5_5_5;
		FFMpegTab2Panel.add(zqFFmpegSettingPanel,gird_x0_y2_0);

		CheckFFmpegFunctionPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "FFmpeg機能チェック",
				TitledBorder.LEADING, TitledBorder.TOP,
			//	new Font("MS UI Gothic", Font.PLAIN, 12),
				getFont(), Color.blue));
		CheckFFmpegFunctionPanel.setForeground(Color.blue);
		CheckFFmpegFunctionPanel.setLayout(new GridBagLayout());
		GridBagConstraints grid__x0_y0_107 = new GridBagConstraints();
		grid__x0_y0_107.gridx = 0;
		grid__x0_y0_107.gridy = 0;
		grid__x0_y0_107.weightx = 0.0;
		grid__x0_y0_107.weightx = 0.0;
		grid__x0_y0_107.anchor = GridBagConstraints.NORTHWEST;
		grid__x0_y0_107.fill = GridBagConstraints.NONE;
		grid__x0_y0_107.insets = INSETS_0_5_0_5;
		CheckFFmpegFunctionPanel.add(CheckFFmpegVersionButton, grid__x0_y0_107);
		GridBagConstraints grid__x1_y0_106 = new GridBagConstraints();
		grid__x1_y0_106.gridx = 1;
		grid__x1_y0_106.gridy = 0;
		grid__x1_y0_106.weightx = 1.0;
		grid__x1_y0_106.weighty = 0.0;
		grid__x1_y0_106.anchor = GridBagConstraints.WEST;
		grid__x1_y0_106.fill = GridBagConstraints.HORIZONTAL;
		grid__x1_y0_106.insets = INSETS_0_5_0_5;
		CheckFFmpegFunctionPanel.add(CheckFFmpegVersionLabel, grid__x1_y0_106);
		CheckDownloadVideoButton.setText("表示");
		CheckDownloadVideoButton.setToolTipText(
			"ダウンロード動画をチェック：幅x高さ　fps　映像codec　音声codec");
		CheckDownloadVideoButton.setForeground(Color.blue);
		CheckDownloadVideoButton.addActionListener(
				new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e){
						CheckDownloadVideoButton_actionPerformed(e);
					}
				});
		GridBagConstraints grid_x0_y1_85 = new GridBagConstraints();
		grid_x0_y1_85.gridx = 0;
		grid_x0_y1_85.gridy = 1;
		grid_x0_y1_85.weightx = 0.0;
		grid_x0_y1_85.weighty = 0.0;
		grid_x0_y1_85.anchor = GridBagConstraints.NORTHWEST;
		grid_x0_y1_85.fill = GridBagConstraints.NONE;
		grid_x0_y1_85.insets = INSETS_0_5_0_5;
		CheckFFmpegFunctionPanel.add(CheckDownloadVideoButton, grid_x0_y1_85);
		CheckDownloadVideoLabel.setText("ダウンロードした動画をチェックする");
		CheckDownloadVideoLabel.setForeground(Color.blue);
		GridBagConstraints grid_x1_y1_88 = new GridBagConstraints();
		grid_x1_y1_88.gridx = 1;
		grid_x1_y1_88.gridy = 1;
		grid_x1_y1_88.weightx = 1.0;
		grid_x1_y1_88.weighty = 0.0;
		grid_x1_y1_88.anchor = GridBagConstraints.WEST;
		grid_x1_y1_88.fill = GridBagConstraints.HORIZONTAL;
		grid_x1_y1_88.insets = INSETS_0_5_0_5;
		CheckFFmpegFunctionPanel.add(CheckDownloadVideoLabel,grid_x1_y1_88);
		showDownloadListButton.setText("表示");
		showDownloadListButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showDownloadList_actionPerformed(e);
			}
		});
		GridBagConstraints grid_x1_y3_x = new GridBagConstraints();
		grid_x1_y3_x.gridx = 0;
		grid_x1_y3_x.gridy = 3;
		grid_x1_y3_x.weightx = 0.0;
		grid_x1_y3_x.weighty = 0.0;
		grid_x1_y3_x.anchor = GridBagConstraints.NORTHWEST;
		grid_x1_y3_x.fill = GridBagConstraints.NONE;
		grid_x1_y3_x.insets = INSETS_0_5_0_5;
		CheckFFmpegFunctionPanel.add(showDownloadListButton, grid_x1_y3_x);
		showDownloadListLabel.setText("ダウンロードリストを表示する");
		grid_x1_y3_x = new GridBagConstraints();
		grid_x1_y3_x.gridx = 1;
		grid_x1_y3_x.gridy = 3;
		grid_x1_y3_x.weightx = 1.0;
		grid_x1_y3_x.weighty = 0.0;
		grid_x1_y3_x.anchor = GridBagConstraints.WEST;
		grid_x1_y3_x.fill = GridBagConstraints.HORIZONTAL;
		grid_x1_y3_x.insets = INSETS_0_5_0_5;
		CheckFFmpegFunctionPanel.add(showDownloadListLabel, grid_x1_y3_x);
		playConvertedVideoButton.setText("再生");
		playConvertedVideoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				autoPlay.playVideo();
			}
		});
		playConvertedVideoButton.setForeground(Color.blue);
		GridBagConstraints grid_x1_y4_x = new GridBagConstraints();
		grid_x1_y4_x.gridx = 0;
		grid_x1_y4_x.gridy = 4;
		grid_x1_y4_x.weightx = 0.0;
		grid_x1_y4_x.weighty = 0.0;
		grid_x1_y4_x.anchor = GridBagConstraints.NORTHWEST;
		grid_x1_y4_x.fill = GridBagConstraints.NONE;
		grid_x1_y4_x.insets = INSETS_0_5_0_5;
		CheckFFmpegFunctionPanel.add(playConvertedVideoButton, grid_x1_y4_x);
		playConvertedVideoLabel.setText("変換後の動画を再生する(拡張子の既定ソフト)");
		playConvertedVideoLabel.setForeground(Color.blue);
		grid_x1_y4_x = new GridBagConstraints();
		grid_x1_y4_x.gridx = 1;
		grid_x1_y4_x.gridy = 4;
		grid_x1_y4_x.weightx = 1.0;
		grid_x1_y4_x.weighty = 0.0;
		grid_x1_y4_x.anchor = GridBagConstraints.WEST;
		grid_x1_y4_x.fill = GridBagConstraints.HORIZONTAL;
		grid_x1_y4_x.insets = INSETS_0_5_0_5;
		CheckFFmpegFunctionPanel.add(playConvertedVideoLabel, grid_x1_y4_x);
		GridBagConstraints grid6_x0_y2_82 = new GridBagConstraints();
		grid6_x0_y2_82.gridx = 0;
		grid6_x0_y2_82.gridy = 6;
		grid6_x0_y2_82.weightx = 1.0;
		grid6_x0_y2_82.weighty = 1.0;
		grid6_x0_y2_82.anchor = GridBagConstraints.NORTHWEST;
		grid6_x0_y2_82.fill = GridBagConstraints.HORIZONTAL;
		grid6_x0_y2_82.insets = INSETS_0_5_5_5;
		FFMpegTab2Panel.add(CheckFFmpegFunctionPanel, grid6_x0_y2_82);

		experimentPanel.setLayout(new GridBagLayout());
		experimentPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				"実験的設定（仮）", TitledBorder.LEADING, TitledBorder.TOP,
				getFont(), Color.blue));
		fontWidthFixCheckBox.setText("フォント幅の調整（％）");
		fontWidthFixCheckBox.setForeground(Color.blue);
		GridBagConstraints grid20_x0_y0 = new GridBagConstraints();
		grid20_x0_y0.gridx = 0;
		grid20_x0_y0.gridy = 0;
		grid20_x0_y0.gridwidth = 2;
		//grid20_x0_y0.gridheight = 1;
		grid20_x0_y0.weightx = 0.0;
		//grid20_x0_y0.weighty = 0.0;
		grid20_x0_y0.anchor = GridBagConstraints.NORTHWEST;
		grid20_x0_y0.fill = GridBagConstraints.NONE;
		grid20_x0_y0.insets = INSETS_0_5_0_5;
		experimentPanel.add(fontWidthFixCheckBox, grid20_x0_y0);
		fontWidthRatioTextField.setText("100");
		fontWidthRatioTextField.setForeground(Color.blue);
		GridBagConstraints grid20_x1_y0 = new GridBagConstraints();
		grid20_x1_y0.gridx = 2;
		grid20_x1_y0.gridy = 0;
		grid20_x1_y0.weightx = 1.0;
		grid20_x1_y0.gridwidth = 2;
		grid20_x1_y0.anchor = GridBagConstraints.NORTHWEST;
		grid20_x1_y0.fill = GridBagConstraints.HORIZONTAL;
		grid20_x1_y0.insets = INSETS_0_5_0_5;
		experimentPanel.add(fontWidthRatioTextField, grid20_x1_y0);
		//fontHeightRatioLabel.setText("倍率（％）");
		//fontHeightRatioLabel.setForeground(Color.blue);
		fontHeightFixCheckBox.setText("フォント高の調整（％）");
		fontHeightFixCheckBox.setForeground(Color.blue);
		GridBagConstraints grid20_x0_y1 = new GridBagConstraints();
		grid20_x0_y1.gridx = 0;
		grid20_x0_y1.gridy = 1;
		grid20_x0_y1.weightx = 0.0;
		grid20_x0_y1.gridwidth = 2;
		grid20_x0_y1.anchor = GridBagConstraints.NORTHWEST;
		grid20_x0_y1.fill = GridBagConstraints.NONE;
		grid20_x0_y1.insets = INSETS_0_5_0_5;
		experimentPanel.add(fontHeightFixCheckBox, grid20_x0_y1);
		fontHeightRatioTextField.setText("100");
		fontHeightRatioTextField.setForeground(Color.blue);
		GridBagConstraints grid20_x1_y1 = new GridBagConstraints();
		grid20_x1_y1.gridx = 2;
		grid20_x1_y1.gridy = 1;
		grid20_x1_y1.anchor = GridBagConstraints.NORTHWEST;
		grid20_x1_y1.fill = GridBagConstraints.HORIZONTAL;
		grid20_x1_y1.weightx = 1.0;
		grid20_x1_y1.gridwidth = 2;
		grid20_x1_y1.insets = INSETS_0_5_0_5;
		experimentPanel.add(fontHeightRatioTextField,grid20_x1_y1);
		disableOriginalResizeCheckBox.setText("開発版を有効（従来のさきゅばすのリサイズを無効にする）");
		disableOriginalResizeCheckBox.setForeground(Color.blue);
		disableOriginalResizeCheckBox.setToolTipText("コメントアート用に調整中。" +
			"現在はオフの方がいいみたいです。安定したらオフの方に反映します。");
		GridBagConstraints grid20_x0_y7 = new GridBagConstraints();
		grid20_x0_y7.gridx = 0;
		grid20_x0_y7.gridy = 7;
		grid20_x0_y7.gridwidth = 4;
		//grid20_x0_y7.gridheight = 1;
		grid20_x0_y7.weightx = 1.0;
		//grid20_x0_y7.weighty = 0.0;
		grid20_x0_y7.anchor = GridBagConstraints.NORTHWEST;
		grid20_x0_y7.fill = GridBagConstraints.HORIZONTAL;
		grid20_x0_y7.insets = INSETS_0_5_0_5;
		experimentPanel.add(disableOriginalResizeCheckBox, grid20_x0_y7);
		enableCA_CheckBox.setText("ＣＡフォント対応：多種類のフォントを使う");
		enableCA_CheckBox.setForeground(Color.blue);
		enableCA_CheckBox.setToolTipText("フォント変化を強制的に使用するようになります");
		GridBagConstraints drid20_x0_y8 = new GridBagConstraints();
		drid20_x0_y8.gridx = 0;
		drid20_x0_y8.gridy = 8;
		drid20_x0_y8.gridwidth = 4;
		drid20_x0_y8.weightx = 1.0;
		drid20_x0_y8.anchor = GridBagConstraints.NORTHWEST;
		drid20_x0_y8.fill = GridBagConstraints.HORIZONTAL;
		drid20_x0_y8.insets = INSETS_0_5_0_5;
		experimentPanel.add(enableCA_CheckBox, drid20_x0_y8);
	//	useLineskipAsFontsizeCheckBox.setText("LineSkipをFontSizeとする（デバッグ用）");
	//	useLineskipAsFontsizeCheckBox.setForeground(Color.blue);
	//	GridBagConstraints grid20_x0_y9 = new GridBagConstraints();
	//	grid20_x0_y9.gridx = 0;
	//	grid20_x0_y9.gridy = 9;
	//	grid20_x0_y9.gridwidth = 4;
	//	grid20_x0_y9.weightx = 1.0;
	//	grid20_x0_y9.anchor = GridBagConstraints.NORTHWEST;
	//	grid20_x0_y9.fill = GridBagConstraints.HORIZONTAL;
	//	grid20_x0_y9.insets = INSETS_0_5_0_5;
	//	experimentPanel.add(useLineskipAsFontsizeCheckBox, grid20_x0_y9);
		useExtraFontCheckBox.setText("追加フォント");
		useExtraFontCheckBox.setForeground(Color.blue);
		useExtraFontCheckBox.setToolTipText("追加フォントパス フォント番号 開始unicode16進4桁-終了16進4桁　と指定して下さい");
		GridBagConstraints grid20_x0_y10 = new GridBagConstraints();
		grid20_x0_y10.gridx = 0;
		grid20_x0_y10.gridy = 10;
		grid20_x0_y10.anchor = GridBagConstraints.NORTHWEST;
		grid20_x0_y10.fill = GridBagConstraints.HORIZONTAL;
		grid20_x0_y10.insets = INSETS_0_5_0_5;
		experimentPanel.add(useExtraFontCheckBox,grid20_x0_y10);
		extraFontTextField.setForeground(Color.blue);
		GridBagConstraints grid20_x1_y10 = new GridBagConstraints();
		grid20_x1_y10.gridx = 1;
		grid20_x1_y10.gridy = 10;
		grid20_x1_y10.gridwidth = 3;
		grid20_x1_y10.weightx = 1.0;
		grid20_x1_y10.anchor = GridBagConstraints.NORTHWEST;
		grid20_x1_y10.fill = GridBagConstraints.HORIZONTAL;
		grid20_x1_y10.insets = INSETS_0_5_0_5;
		experimentPanel.add(extraFontTextField, grid20_x1_y10);
		extraModeLabel.setText("追加モード");
		extraModeLabel.setForeground(Color.blue);
		extraModeLabel.setToolTipText("黄枠モード他の追加の動作を指定します");
		GridBagConstraints grid20_x0_y12 = new GridBagConstraints();
		grid20_x0_y12.gridx = 0;
		grid20_x0_y12.gridy = 12;
		grid20_x0_y12.anchor = GridBagConstraints.WEST;
		grid20_x0_y12.fill = GridBagConstraints.HORIZONTAL;
		grid20_x0_y12.insets = INSETS_0_5_0_5;
		experimentPanel.add(extraModeLabel, grid20_x0_y12);
		extraModeField.setForeground(Color.blue);
		GridBagConstraints grid20_x1_y12 = new GridBagConstraints();
		grid20_x1_y12.gridx = 1;
		grid20_x1_y12.gridy = 12;
		grid20_x1_y12.gridwidth = 3;
		grid20_x1_y12.anchor = GridBagConstraints.WEST;
		grid20_x1_y12.fill = GridBagConstraints.HORIZONTAL;
		grid20_x1_y12.insets = INSETS_0_5_0_5;
		experimentPanel.add(extraModeField, grid20_x1_y12);

		convertManager = new ConvertManager(new JLabel[] {statusBar, elapsedTimeBar, infoBar});
	}

	private JPanel getManagentPanel() {
		if(managementPanel ==null){

			managementPanel = new JPanel();
			managementPanel.setLayout(new GridBagLayout());

			managementControl = new JPanel();
			managementControl.setLayout(new GridBagLayout());
			managementControl.setBorder(BorderFactory.createTitledBorder(
					BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
					"管理制御", TitledBorder.LEADING, TitledBorder.TOP,
					getFont(), Color.red));
			GridBagConstraints grid400 = new GridBagConstraints();
			grid400.gridx = 0;
			grid400.gridy = 0;
			grid400.gridwidth = 1;
			grid400.gridheight = 1;
			grid400.weightx = 0.0;
			grid400.anchor = GridBagConstraints.WEST;
			grid400.fill = GridBagConstraints.HORIZONTAL;
			grid400.insets = INSETS_0_0_0_0;
			managementControl.add(new JLabel("同時変換"),grid400);
			GridBagConstraints grid401 = new GridBagConstraints();
			grid401.gridx = 1;
			grid401.gridy = 0;
			grid401.gridwidth = 6;
			grid401.gridheight = 1;
			grid401.weightx = 0.0;
			grid401.anchor = GridBagConstraints.WEST;
			grid401.fill = GridBagConstraints.HORIZONTAL;
			grid401.insets = INSETS_0_5_0_5;
			SpinnerNumberModel model = new SpinnerNumberModel(0, null, null, 1);
			nThreadSpinner = new JSpinner(model);
			changeListener = (new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					noticeConvertManager(e);
				}
			});
			managementControl.add(nThreadSpinner,grid401);
			downloadDownCheckBox = new JCheckBox("1");
			downloadDownCheckBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Gate.setNumGate(downloadDownCheckBox.isSelected()? 1:2, log);
					convertManager.sendTimeInfo();
				}
			});
			GridBagConstraints grid400B = new GridBagConstraints();
			grid400B.gridx = 7;
			grid400B.gridy = 0;
			grid400B.gridwidth = 1;
			grid400B.gridheight = 1;
			grid400B.weightx = 0.0;
			grid400B.anchor = GridBagConstraints.WEST;
			grid400B.fill = GridBagConstraints.HORIZONTAL;
			grid400B.insets = INSETS_0_0_0_0;
			managementControl.add(downloadDownCheckBox,grid400B);

			GridBagConstraints grid402 = new GridBagConstraints();
			grid402.gridx = 8;
			grid402.gridy = 0;
			grid402.gridwidth = 4;
			grid402.gridheight = 1;
			grid402.weightx = 0.0;
			grid402.anchor = GridBagConstraints.NORTH;
			grid402.fill = GridBagConstraints.HORIZONTAL;
			grid402.insets = INSETS_0_5_0_0;
			PendingModeCheckbox = new JCheckBox("開始保留", false);
			PendingModeCheckbox.setToolTipText("変換ボタンを押した時に保留にする、個別ボタンで変換開始");
			managementControl.add(PendingModeCheckbox, grid402);
			GridBagConstraints grid415 = new GridBagConstraints();
			grid415.gridx = 12;
			grid415.gridy = 0;
			grid415.gridwidth = 3;
			grid415.gridheight = 1;
			grid415.weightx = 1.0;
			grid415.anchor = GridBagConstraints.NORTH;
			grid415.fill = GridBagConstraints.HORIZONTAL;
			grid415.insets = INSETS_0_5_0_0;
			OneLineCheckbox = new JCheckBox("１行表示", false);
			OneLineCheckbox.setToolTipText("1動画ごとに１行で表示");
			managementControl.add(OneLineCheckbox, grid415);

			GridBagConstraints grid410 = new GridBagConstraints();
			grid410.gridx = 0;
			grid410.gridy = 1;
			grid410.gridwidth = 1;
			grid410.gridheight = 1;
			grid410.weightx = 0.0;
			grid410.anchor = GridBagConstraints.WEST;
			grid410.fill = GridBagConstraints.BOTH;
			grid410.insets = INSETS_0_0_0_0;
			managementControl.add(new JLabel("全制御　"), grid410);

			GridBagConstraints grid411 = new GridBagConstraints();
			grid411.gridx = 1;
			grid411.gridy = 1;
			grid411.gridwidth = 3;
			grid411.gridheight = 1;
			grid411.weightx = 0.0;
			grid411.anchor = GridBagConstraints.WEST;
			grid411.fill = GridBagConstraints.HORIZONTAL;
			grid411.insets = INSETS_0_0_0_0;
			AllExecButton = new JButton("変換");
			AllExecButton.setForeground(Color.blue);
			AllExecButton.setToolTipText("変換を開始します");
			AllExecButton.setEnabled(false);
			managementControl.add(AllExecButton, grid411);

			GridBagConstraints grid412 = new GridBagConstraints();
			grid412.gridx = 4;
			grid412.gridy = 1;
			grid412.gridwidth = 3;
			grid412.gridheight = 1;
			grid412.weightx = 0.0;
			grid412.anchor = GridBagConstraints.WEST;
			grid412.fill = GridBagConstraints.HORIZONTAL;
			grid412.insets = INSETS_0_0_0_0;
			AllCancelButton = new JButton("停止");
			AllCancelButton.setToolTipText("全てのリクエストを取り消し、全変換を停止します");
			AllCancelButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					AllCancel_ActionHandler(e);
				}
			});
			managementControl.add(AllCancelButton, grid412);

			GridBagConstraints grid413 = new GridBagConstraints();
			grid413.gridx = 7;
			grid413.gridy = 1;
			grid413.gridwidth = 3;
			grid413.gridheight = 1;
			grid413.weightx = 0.0;
			grid413.anchor = GridBagConstraints.WEST;
			grid413.fill = GridBagConstraints.HORIZONTAL;
			grid413.insets = INSETS_0_0_0_0;
			AllDeleteButton = new JButton("消去");
			AllDeleteButton.setForeground(Color.red);
			AllDeleteButton.setToolTipText("全て停止して全変換表示を消去します");
			AllDeleteButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					AllDelete_ActionHandler(e);
				}
			});
			managementControl.add(AllDeleteButton, grid413);

			GridBagConstraints grid414 = new GridBagConstraints();
			grid414.gridx = 10;
			grid414.gridy = 1;
			grid414.gridwidth = 2;
			grid414.gridheight = 1;
			grid414.weightx = 0.0;
			grid414.anchor = GridBagConstraints.WEST;
			grid414.fill = GridBagConstraints.HORIZONTAL;
			grid414.insets = INSETS_0_0_0_0;
			AllSaveButton = new JButton("保存");
			AllSaveButton.setForeground(Color.BLACK);
			AllSaveButton.setToolTipText("変換予約履歴を保存します");
			AllSaveButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Path reqlistSave = new Path("動画ID"+WayBackDate.formatNow()+".txt");
					String text = requestHistory.getText();
					try {
						PrintWriter pw = new PrintWriter(reqlistSave);
						pw.print(text);
						pw.flush();
						pw.close();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
				}
			});
			managementControl.add(AllSaveButton, grid414);
			managementControl.add(new JPanel(),
				new GridBagConstraints(13, 1, 1, 1, 1.0, 1, GridBagConstraints.WEST,
						GridBagConstraints.BOTH, INSETS_0_0_0_0, 0,	0));

			GridBagConstraints grid40 = new GridBagConstraints();
			grid40.gridx = 0;
			grid40.gridy = 0;
			grid40.gridwidth = 1;
			grid40.weightx = 1.0;
			grid40.anchor = GridBagConstraints.NORTH;
			grid40.fill = GridBagConstraints.BOTH;
			grid40.insets = INSETS_0_5_0_5;
			managementPanel.add(managementControl,grid40);

			GridBagConstraints grid41 = new GridBagConstraints();
			grid41.gridx = 0;
			grid41.gridy = 2;
			grid41.gridwidth = 1;
			grid41.weightx = 1.0;
			grid41.weighty = 1.0;
			grid41.anchor = GridBagConstraints.NORTH;
			grid41.fill = GridBagConstraints.BOTH;
			grid41.insets = INSETS_0_0_0_0;
			activityStatusPanel = new JPanel();
			activityStatusPanel.setLayout(new BorderLayout());
			activityPane = new JPanel();
			activityPane.setMaximumSize(new Dimension(200, Short.MAX_VALUE));
			activityPane.setLayout(new BoxLayout(activityPane, BoxLayout.Y_AXIS));
			activityScroll = new JScrollPane(activityPane);
			activityStatusPanel.add(activityScroll,BorderLayout.CENTER);
			activityStatusPanel.setBorder(BorderFactory.createTitledBorder(
					BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
					"状況表示", TitledBorder.LEADING, TitledBorder.TOP,
					new JLabel().getFont(), Color.red));

			managementPanel.add(activityStatusPanel, grid41);

			errorStatusPanel = new JPanel();
			errorUrlLabel = new JLabel(" ");
			errorUrlLabel.setForeground(Color.DARK_GRAY);
			errorControl = new ErrorControl(errorUrlLabel);
			errorResetUrlButton = new JButton("再登録");
			errorResetUrlButton.setForeground(Color.BLUE);
			errorResetUrlButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					StringBuffer vlist = new StringBuffer(errorControl.getString());
					myListGetterDone(vlist, log);
					errorControl.clear();
					convertManager.clearError();
				}
			});
			errorListDeleteButton = new JButton("消去");
			errorListDeleteButton.setForeground(Color.RED);
			errorListDeleteButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					errorControl.clear();
					convertManager.clearError();
				}
			});
			errorListSaveButton = new JButton("保存");
			errorListSaveButton.setForeground(Color.BLACK);
			errorListSaveButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if(errorControl.save())
						sendtext("エラーリストを保存しました");
					else
						sendtext("エラーリスト保存失敗");
				}

			});
			errorButtonPanel = new JPanel();
			errorButtonPanel.setLayout(new BorderLayout());
			errorButtonPanel.add(errorResetUrlButton, BorderLayout.WEST);
			errorButtonPanel.add(errorListDeleteButton, BorderLayout.CENTER);
			errorButtonPanel.add(errorListSaveButton, BorderLayout.EAST);
			errorStatusPanel.setLayout(new BorderLayout());
			errorStatusPanel.add(new JLabel("エラーID  "), BorderLayout.WEST);
			errorStatusPanel.add(errorUrlLabel, BorderLayout.CENTER);
			errorStatusPanel.add(errorButtonPanel, BorderLayout.EAST);
			GridBagConstraints grid42 = new GridBagConstraints();
			grid42.gridx = 0;
			grid42.gridy = 3;
			grid42.gridwidth = 1;
			grid42.weightx = 1.0;
			grid42.anchor = GridBagConstraints.NORTH;
			grid42.fill = GridBagConstraints.BOTH;
			grid42.insets = INSETS_0_5_0_5;
			managementPanel.add(errorStatusPanel, grid42);

			playVideoLabel = new JLabel(" ");
			playVideoBackButton = new JButton("戻");
			playVideoBackButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					autoPlay.back();
				}
			});
			playVideoNextButton = new JButton("次");
			playVideoNextButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					autoPlay.next();
				}
			});
			playVideoPlayButton = new JButton("再生");
			playVideoPlayButton.setForeground(Color.BLUE);
			playVideoPlayButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					autoPlay.playVideo();
				}
			});
			playVideoButtonPanel = new JPanel();
			playVideoButtonPanel.setLayout(new BorderLayout());
			playVideoButtonPanel.add(playVideoBackButton, BorderLayout.WEST);
			playVideoButtonPanel.add(playVideoPlayButton, BorderLayout.CENTER);
			playVideoButtonPanel.add(playVideoNextButton, BorderLayout.EAST);
			playVideoPanel = new JPanel();
			playVideoPanel.setLayout(new BorderLayout());
			playVideoPanel.add(autoPlay2CheckBox, BorderLayout.WEST);
			playVideoPanel.add(playVideoLabel, BorderLayout.CENTER);
			playVideoPanel.add(playVideoButtonPanel, BorderLayout.EAST);
			GridBagConstraints grid43 = new GridBagConstraints();
			grid43.gridx = 0;
			grid43.gridy = 4;
			grid43.gridwidth = 1;
			grid43.weightx = 1.0;
			grid43.anchor = GridBagConstraints.NORTH;
			grid43.fill = GridBagConstraints.BOTH;
			grid43.insets = INSETS_0_5_0_5;
			managementPanel.add(playVideoPanel, grid43);
			autoPlay = new AutoPlay(autoPlayCheckBox,playVideoLabel,null,statusBar);
		}
		return managementPanel;
	}

	private int noticeConvertManager(ChangeEvent e) {
		return convertManager.notice(nThreadSpinner.getValue());
	}

	private void AllCancel_ActionHandler(ActionEvent e) {
		// cancel request
		for(JButton button:buttonTable.keySet()){
			ConvertStopFlag flag = buttonTable.get(button);
			if(flag!=null){
				convertManager.gotoCancel(flag);
			}
		}
		convertManager.cancelAllRequest();
		convertManager.queueCheckAndGo();
		//buttonTable の全ボタンをdisable
		for(JButton button:buttonTable.keySet()){
			button.setEnabled(false);
		}
		// buttonTable全要素削除
		buttonTable.clear();
	}

	private void AllDelete_ActionHandler(ActionEvent e) {
		AllCancel_ActionHandler(e);
		convertManager.allDelete();
		activityPane.removeAll();
		activityPane.repaint();
		activityPane.setLayout(new BoxLayout(activityPane, BoxLayout.Y_AXIS));
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
		OptionPathField.addMouseListener(new PopupRightClick(this.OptionPathField));
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

		BrowserCookieField.addMouseListener(
				new PopupRightClick(this.BrowserCookieField));

		activityPane.addMouseListener(new PopupRightClick(this.VideoID_TextField));
		managementControl.addMouseListener(new PopupRightClick(this.VideoID_TextField));
	}

	private void setDropTarget() {
		addTarget(VideoSavedFileField, false);
		addTarget(VideoSavedFolderField, true);

		addTarget(CommentSavedFileField, false);
		addTarget(CommentSavedFolderField, true);

		addTarget(ConvertedVideoSavedFileField, false);
		addTarget(ConvertedVideoSavedFolderField, true);

		addTarget(FFmpegPathField, false);
		addTarget(OptionPathField, false);
		addTarget(VhookPathField, false);
		addTarget(VhookWidePathField, false);
		addTarget(FontPathField, false);

		addTarget(BrowserCookieField, false);

		addComponentTarget(VideoID_TextField, activityPane, false);
		addComponentTarget(VideoID_TextField, managementControl, false);
	}

	private DropTarget addTarget(JTextField c, boolean isDir) {
		return new DropTarget(c, DnDConstants.ACTION_COPY, new FileDropTarget(
				c, isDir), true);
	}

	private DropTarget addComponentTarget(JTextField f, JComponent c, boolean isDir) {
		return new DropTarget(c, DnDConstants.ACTION_COPY,
				new FileDropTarget(f, isDir));
	}

	private File CurrentDir = new File(".");

	JPanel PathSettingPanel = new JPanel();
	JPanel VhookPathSettingPanel = new JPanel();
	JPanel FFmpegPathSettingPanel = new JPanel();
	JLabel FFmpegPathLabel = new JLabel();
	JLabel OptionPathLabel = new JLabel();
	GridBagLayout gridBagLayout7 = new GridBagLayout();
	JTextField FFmpegPathField = new JTextField();
	JTextField OptionPathField = new JTextField();
	JButton SettingFFmpegPathButton = new JButton();
	JButton SettingOptionPathButton = new JButton();
	JLabel VhookSettingLabel = new JLabel();
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
	JPanel WideFFmpegSettingPanel = new JPanel();
	GridBagLayout gridBagLayout9 = new GridBagLayout();
	private JPanel zqPlayerModePanel = new JPanel();
	private JLabel zqVhookSettingLavel = new JLabel();
	private JCheckBox zqPlayerModeCheckBox = new JCheckBox();
	private JTextField zqAdditionalOptionFiled = new JTextField();
	private JTextField zqExtOptionField = new JTextField();
	private JTextField zqMainOptionField = new JTextField();
	private JTextField zqCommandLineInOptionField = new JTextField();
	private JTextField zqCommandLineOutOptionField = new JTextField();
	private JTextField zqVhookPathField = new JTextField();
	private JButton zqSettingVhookPathButton = new JButton();
	private JPanel zqFFmpegSettingPanel = new JPanel();
	@SuppressWarnings("rawtypes")
	private JComboBox zqFFmpegOptionComboBox = null;
	private JButton zqFFmpegOptionReloadButton = null;
	private JPanel zqFFmpegOptionComboBoxPanel = null;
	private JTextArea zqOptionFileDescription = new JTextArea("",2,20);

	JLabel FontPathLabel = new JLabel();
	JTextField FontPathField = new JTextField();
	JButton SettingFontPathButton = new JButton();
	JCheckBox ShowConvVideoCheckBox = new JCheckBox();
	JTextField CommandLineOutOptionField = new JTextField();

	private void showSaveDialog(String title, JTextField field, boolean isSave,
			boolean isDir) {
		File file = new File(field.getText());
		if (file == null || !file.exists()){
			file = CurrentDir;
		} else if (file.isFile() || isDir){	// field is file OR want for Dir
			file = file.getParentFile();
		}
		JFileChooser chooser = new JFileChooser(file);
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
		File file = new File(field.getText());
		if (!file.exists()){
			file = new File("");
		}
		JFileChooser chooser = new JFileChooser(file);
		chooser.setDialogTitle(title);
		int code = 0;
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		code = chooser.showOpenDialog(this);
		if (code == JFileChooser.APPROVE_OPTION) {
		//	CurrentDir = chooser.getCurrentDirectory();
			field.setText(getRelativePath(chooser.getSelectedFile()));
		}
	}

	public ConvertingSetting getSetting() {
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
		if (index > comment.lastIndexOf(File.separatorChar)){
			ownercomment = comment.substring(0, index);
		}
		ownercomment += saccubus.ConvertWorker.OWNER_EXT;
		Double fpsUp = 0.0;
		Double fpsMin = 0.0;
//		if(fpsUpCheckBox.isSelected()){
			try{
				fpsUp = Double.parseDouble(fpsUpTextFiled.getText());
				fpsMin = Double.parseDouble(fpsMinTextField.getText());
			}catch(NumberFormatException e){
				//e.printStackTrace();
			}
//		}
		try {
			numThread = Integer.parseInt(nThreadSpinner.getValue().toString());
		} catch(NumberFormatException e){
			numThread = 1;
		}
		autoPlay.setCheckBox(autoPlayCheckBox);
		// 運営コメント設定チェック
		String duration = liveOperationDurationTextField.getText();
		int live_op_duration = 0;
		try {
			if(live_op_duration <= 0)
				duration = "";
			else
				duration = "" + live_op_duration;
		} catch(NumberFormatException e){
			//log.printStackTrace(e);
			duration = "";
		}
		String vposshift = liveCommentVposShiftTextField.getText();
		double vpos_shift_sec = 0.0;
		if(!vposshift.isEmpty()){
			try {
				vpos_shift_sec = Double.valueOf(vposshift);
				vposshift = Double.toString(vpos_shift_sec);
			} catch(NumberFormatException e){
				vposshift = "";
				log.println("VPOSシフト値が不正");
			}
		}
		return new ConvertingSetting(
			MailAddrField.getText(),
			new String(PasswordField.getPassword()),
			SavingVideoCheckBox.isSelected(),
			VideoSavedFileField.getText(),
			SavingCommentCheckBox.isSelected(),
			AddTimeStampToCommentCheckBox.isSelected(),
			CommentSavedFileField.getText(),
			true,	//	SavingOwnerCommentCheckBox.isSelected(),
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
			requestHistory.getLast(),
			VhookWidePathField.getText(),
			UseVhookCheckBox.isSelected(),
			UseVhookWideCheckBox.isSelected(),
			BrowserIECheckBox.isSelected(),
			BrowserFFCheckBox.isSelected(),
			BrowserChromeCheckBox.isSelected(),
			BrowserChromiumCheckBox.isSelected(),
			BrowserOperaCheckBox.isSelected(),
			BrowserOtherCheckBox.isSelected(),
			BrowserCookieField.getText(),
			OptionPathField.getText(),
			WideFFmpegOptionModel.getSelectedFile(),
			wideExtOptionField.getText(),
			wideMainOptionField.getText(),
			wideCommandLineInOptionField.getText(),
			wideCommandLineOutOptionField.getText(),
			OptionalTranslucentCheckBox.isSelected(),
			fontHeightFixCheckBox.isSelected(),
			fontHeightRatioTextField.getText(),
			disableOriginalResizeCheckBox.isSelected(),
			commentModeComboBox.getSelectedIndex(),
			commentSpeedCheckBox.isSelected(),
			commentSpeedTextField.getText(),
//			getDebugMode(),
			enableCA_CheckBox.isSelected(),
			sharedNgScore.getScore(),
			disableEcoCheckBox.isSelected(),
			fontWidthFixCheckBox.isSelected(),
			fontWidthRatioTextField.getText(),
			useLineskipAsFontsizeCheckBox.isSelected(),
			useExtraFontCheckBox.isSelected(),
			extraFontTextField.getText(),
			ngCommandField.getText(),
			"",	//replaceCommandField.getText(),
			encrypt_pass,
			extraModeField.getText(),
			additionalOptionFiled.getText(),
			wideAdditionalOptionFiled.getText(),
			saveWatchPageInfoCheckBox.isSelected(),
			saveThumbInfoCheckBox.isSelected(),
			userFolderTextField.getText(),
			saveThumbUserCheckBox.isSelected(),
			saveThumbInfoExtTxtRadioButton.isSelected(),
			changeMp4ExtCheckBox.isSelected(),
			changeTitleIdCheckBox.isSelected(),
			saveThumbnailJpgCheckBox.isSelected(),
			zqPlayerModeCheckBox.isSelected(),
			zqVhookPathField.getText(),
			zqFFmpegOptionModel.getSelectedFile(),
			zqExtOptionField.getText(),
			zqMainOptionField.getText(),
			zqCommandLineInOptionField.getText(),
			zqCommandLineOutOptionField.getText(),
			zqAdditionalOptionFiled.getText(),
			resultHistory,
			opaqueRateTextField.getText(),
			nmmNewEnableCheckBox.isSelected(),
			fpsUpCheckBox.isSelected(),
			fpsUp,
			fpsMin,
			soundOnlyCheckBox.isSelected(),
			thumbTextFiled.getText(),
			saveAutoListCheckBox.isSelected(),
			fpsFilterRadioButton.isSelected(),
			autoPlayCheckBox.isSelected(),
			liveOperationCheckBox.isSelected(),
			premiumColorCheckBox.isSelected(),
			zqOptionFileDescription.getText(),
			appendCommentCheckBox.isSelected(),
			notice,
			numThread,
			PendingModeCheckbox.isSelected(),
			OneLineCheckbox.isSelected(),
			errorControl.getString(),
			liveOparationDurationChangeCheckBox.isSelected(),
			liveCommentModeCheckBox.isSelected(),
			duration,
			liveCommentVposShiftCheckBox.isSelected(),
			vposshift
		);
	}

	/*
	private String getDebugMode() {

		String proxy = ProxyTextField.getText();
		if (proxy != null && proxy.startsWith(NicoClient.DEBUG_PROXY)){
			proxy = proxy.substring(0, proxy.indexOf('/', NicoClient.DEBUG_PROXY.length()) + 1);
			return proxy.replace(NicoClient.DEBUG_PROXY, "");
		}
		return null;
	}
*/
	private void setSetting(ConvertingSetting setting) {
		MailAddrField.setText(setting.getMailAddress());
		PasswordField.setText(setting.getPassword());
		SavingVideoCheckBox.setSelected(setting.isSaveVideo());
		VideoSavedFileField.setText(setting.getVideoFile().getPath());
		SavingCommentCheckBox.setSelected(setting.isSaveComment());
		AddTimeStampToCommentCheckBox.setSelected(setting.isAddTimeStamp());
		CommentSavedFileField.setText(setting.getCommentFile().getPath());
//		SavingOwnerCommentCheckBox.setSelected(setting.isSaveOwnerComment());
		commentModeComboBox.setSelectedIndex(setting.getCommentIndex());
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
		OptionPathField.setText(setting.getOptionFolder());
		FFmpegOptionModel.setOptionFolder(setting.getOptionFolder());
		FFmpegOptionModel.reload(setting.getOptionFile());
		NotUseVhookCheckBox.setSelected(setting.isVhookDisabled());
		ShadowComboBox.setSelectedIndex(setting.getShadowIndex());
		AddOption_ConvVideoFileCheckBox.setSelected(setting.isAddOption_ConvVideoFile());
		VideoID_TextField.setText(setting.lastHistory());
		requestHistory.add(setting.lastHistory());
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
		WideFFmpegOptionModel.setOptionFolder(setting.getOptionFolder());
		WideFFmpegOptionModel.reload(setting.getWideOptionFile());
		wideExtOptionField.setText(setting.getWideCmdLineOptionExt());
		wideMainOptionField.setText(setting.getWideCmdLineOptionMain());
		wideCommandLineOutOptionField.setText(setting.getWideCmdLineOptionOut());
		wideCommandLineInOptionField.setText(setting.getWideCmdLineOptionIn());
		OptionalTranslucentCheckBox.setSelected(setting.isOptionalTranslucent());
		fontHeightFixCheckBox.setSelected(setting.isFontHeightFix());
		fontHeightRatioTextField.setText(setting.getFontHeightFixRaito());
		disableOriginalResizeCheckBox.setSelected(setting.isDisableOriginalResize());
		commentSpeedCheckBox.setSelected(setting.isSetCommentSpeed());
		commentSpeedTextField.setText(setting.getCommentSpeed());
		enableCA_CheckBox.setSelected(setting.isEnableCA());
		sharedNgScore.setScore(setting.getScoreLimit());
		disableEcoCheckBox.setSelected(setting.isDisableEco());
		fontWidthFixCheckBox.setSelected(setting.isFontWidthFix());
		fontWidthRatioTextField.setText(setting.getFontWidthFixRaito());
		useLineskipAsFontsizeCheckBox.setSelected(setting.isUseLineSkip());
		useExtraFontCheckBox.setSelected(setting.isUseExtraFont());
		extraFontTextField.setText(setting.getExtraFontText());
		ngCommandField.setText(setting.getNGCommand());
	//	replaceCommandField.setText(setting.getReplaceCommand());
		encrypt_pass = setting.getEncryptPass();
		extraModeField.setText(setting.getExtraMode());
		additionalOptionFiled.setText(setting.getAddOption());
		wideAdditionalOptionFiled.setText(setting.getWideAddOption());
		saveWatchPageInfoCheckBox.setSelected(setting.isSaveWatchPage());
		saveThumbInfoCheckBox.setSelected(setting.isSaveThumbInfo());
		userFolderTextField.setText(setting.getUserFolder());
		saveThumbUserCheckBox.setSelected(setting.isSaveThumbUser());
		saveThumbInfoExtTxtRadioButton.setSelected(setting.isSaveThumbInfoAsText());
		saveThumbInfoExtXmlRadioButton.setSelected(!setting.isSaveThumbInfoAsText());
		changeMp4ExtCheckBox.setSelected(setting.isChangeMp4Ext());
		changeTitleIdCheckBox.setSelected(setting.isChangeTitleId());
		saveThumbnailJpgCheckBox.setSelected(setting.isSaveThumbnailJpg());
		zqPlayerModeCheckBox.setSelected(setting.isZqPlayer());
		zqVhookPathField.setText(setting.getZqVhookPath());
		zqFFmpegOptionModel.setOptionFolder(setting.getOptionFolder());
		zqFFmpegOptionModel.reload(setting.getZqOptionFile());
		zqExtOptionField.setText(setting.getZqCmdLineOptionExt());
		zqMainOptionField.setText(setting.getZqCmdLineOptionMain());
		zqCommandLineOutOptionField.setText(setting.getZqCmdLineOptionOut());
		zqCommandLineInOptionField.setText(setting.getZqCmdLineOptionIn());
		zqAdditionalOptionFiled.setText(setting.getZqAddOption());
		opaqueRateTextField.setText(setting.getOpaqueRate());
		nmmNewEnableCheckBox.setSelected(setting.isSwfTo3Path());
		fpsUpCheckBox.setSelected(setting.enableCheckFps());
		fpsFilterRadioButton.setSelected(setting.isUseFpsFilter());
		fpsConvRadioButton.setSelected(!setting.isUseFpsFilter());
		fpsMinTextField.setText(Double.toString(setting.getFpsMin()));
		fpsUpTextFiled.setText(Double.toString(setting.getFpsUp()));
		soundOnlyCheckBox.setSelected(setting.canSoundOnly());
		thumbTextFiled.setText(setting.getDefaultThumbnail());
		saveAutoListCheckBox.setSelected(setting.isSaveAutoList());
		boolean b = setting.isAutoPlay();
		autoPlay.setSelected(b);
		autoPlay2CheckBox.setSelected(b);
		autoPlay.setLabel(playVideoLabel);
		autoPlay.setStatus(statusBar);
		liveOperationCheckBox.setSelected(setting.isLiveOperationConversion());
		premiumColorCheckBox.setSelected(setting.isPremiumColorCheck());
		zqOptionFileDescription.setText(setting.getOptionFileDescr());
		zqOptionFileDescription.setLineWrap(true);
		zqOptionFileDescription.setEditable(false);
		appendCommentCheckBox.setSelected(setting.isAppendComment());
		notice = setting.getAppendNotice();
		nThreadSpinner.setValue((Integer)(setting.getNumThread()));
		PendingModeCheckbox.setSelected(setting.isPendingMode());
		OneLineCheckbox.setSelected(setting.isOneLineMode());
		errorControl.setError(setting.getErrorList());
		liveOparationDurationChangeCheckBox.setSelected(setting.changedLiveOperationDuration());
		liveCommentModeCheckBox.setSelected(setting.isForcedLiveComment());
		liveOperationDurationTextField.setText((setting.getLiveOperationDuration()));
		liveCommentVposShiftCheckBox.setSelected(setting.isEnableCommentVposShift());
		liveCommentVposShiftTextField.setText(setting.getCommentVposShiftString());
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
	ConvertWorker converter = null;

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
	BasicArrowButton openConvSaveFolderButton = new BasicArrowButton(SwingConstants.EAST);
	BasicArrowButton openConvSaveFileButton = new BasicArrowButton(SwingConstants.EAST);
	JButton ShowSavingConvertedVideoFolderDialogButton = new JButton();
	JTextField VideoSavedFolderField = new JTextField();
	BasicArrowButton openVideoSaveFolderButton = new BasicArrowButton(SwingConstants.EAST);
	BasicArrowButton openVideoSaveFileButton = new BasicArrowButton(SwingConstants.EAST);
	JButton ShowSavingVideoFolderDialogButton = new JButton();
	JRadioButton Video_SaveFolderRadioButton = new JRadioButton();
	JRadioButton Video_SaveFileRadioButton = new JRadioButton();
	JRadioButton Comment_SaveFileRadioButton = new JRadioButton();
	JTextField CommentSavedFolderField = new JTextField();
	BasicArrowButton openCommentSaveFolderButton = new BasicArrowButton(SwingConstants.EAST);
	BasicArrowButton openCommentSaveFileButton = new BasicArrowButton(SwingConstants.EAST);
	JButton ShowSavingCommentFolderDialogButton = new JButton();
	JRadioButton Comment_SaveFolderRadioButton = new JRadioButton();
	JPanel BasicInfoTabPanel = new JPanel();
	GridBagLayout gridBagLayout12 = new GridBagLayout();
	JTextField wideExtOptionField = new JTextField();
	JTextField wideMainOptionField = new JTextField();
	JTextField wideCommandLineInOptionField = new JTextField();
	JTextField wideCommandLineOutOptionField = new JTextField();
	private JPanel ConvertingSettingPanel = null;
	private JPanel NGWordSettingPanel = null;
	private JLabel NGWordLabel = null;
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
	private JTabbedPane FFmpegInfoTabPaneEach = new JTabbedPane();
	private JPanel VideoSavingTabbedPanel = null;
	private JPanel ConvertedVideoSavingTabbedPanel = null;
	private JCheckBox NotAddVideoID_ConvVideoCheckBox = null;
	@SuppressWarnings("rawtypes")
	private JComboBox FFmpegOptionComboBox = null;
	@SuppressWarnings("rawtypes")
	private JComboBox WideFFmpegOptionComboBox = null;
	private JButton FFmpegOptionReloadButton = null;
	private JButton WideFFmpegOptionReloadButton = null;
	private JPanel FFmpegOptionComboBoxPanel = null;
	private JPanel WideFFmpegOptionComboBoxPanel = null;
	private JLabel ngCommandLabel;
	private JTextField ngCommandField;
//	private JLabel replaceCommandLabel;
//	private JTextField replaceCommandField;
	private final JRadioButton sharedNgNoneRadioButton = new JRadioButton();
	private final JRadioButton sharedNgLowRadioButton = new JRadioButton();
	private final JRadioButton sharedNgMediumRadioButton = new JRadioButton();
	private final JRadioButton sharedNgHighRadioButton = new JRadioButton();
	private JLabel sharedNgLabel;
	private JPanel sharedNgPanel;
	public static StringBuffer resultHistory = new StringBuffer("");
	private JPanel managementPanel;
	private JPanel managementControl = new JPanel();
	private JPanel activityStatusPanel;
	private MylistGetter mylistGetter;
	private StringBuffer movieList;
	private int numThread;
	private LinkedHashMap<JButton, ConvertStopFlag> buttonTable = new LinkedHashMap<>();
	private ConvertManager convertManager;
	private String Tag;
	@SuppressWarnings("unused")
	private String watchInfo;
	private boolean PendingMode;

	public void myListGetterDone(StringBuffer vList, Logger log) {
		PendingMode = getSetting().isPendingMode();
		myListGetterDone(vList, PendingMode, log);
	}

	public void myListGetterDone(StringBuffer vList, boolean pending, Logger log) {
		// mylist読み込み終了　結果を受け取る
		if(vList==null){
			log.println("マイリスト結果受け取り失敗　バグ?");
			sendtext("マイリスト結果受け取り失敗　バグ?");
			return;
		}
		String str = vList.substring(0);
		vList = new StringBuffer();
		log.println(str);
		String[] lists = str.split("\n");
		for(String id_title:lists){
			if(id_title.isEmpty()) continue;
			log.println("登録\t"+id_title);
			String[] ss = id_title.split("\t");
			String vid = ss[0];
			String title = ss.length>1 ? ss[1] : "";
			String watchinfo = ss.length>2 ?ss[2] : "";
			// idを登録
			url = vid + watchinfo;
			OneLineMode = getSetting().isOneLineMode();
			requestHistory.add(url);
			ListInfo listInfo = new ListInfo(vid+"_"+title,OneLineMode);
			listInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
			JLabel[] status3 = listInfo.getStatus();
			activityPane.add(listInfo);
			JButton stopButton = listInfo.getjButton();
			stopButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					stopButton_actionPerformed(e);
				}
			});
			int indexNow = convNo++;
			//log.println(">"+indexNow+"個目の要求: "+vid);
			sendtext(">"+indexNow+"個目の要求: "+vid);
			ConvertStopFlag stopFlag =
				new ConvertStopFlag(stopButton,"停","待","終", "変", pending);
			buttonTable.put(stopButton, stopFlag);
			ConvertingSetting setting1 = getSetting();
			// ConverManager処理を要求
			convertManager.request(
				indexNow,
				numThread,
				url,
				WayBackField.getText(),
				setting1,
				status3,
				stopFlag,
				this,
				autoPlay,
				errorControl,
				vList);
			// return to dispatch
 		}

	}

	//全変換ボタン
	private void AllExecButton_handler(ActionEvent e) {
		for(ConvertStopFlag flag:buttonTable.values()){
			convertManager.gotoRequest(flag);
		}

	}
	//変換/停止ボタン
	public void stopButton_actionPerformed(ActionEvent e){
		Object obj = e.getSource();
		if(obj instanceof JButton){
			ConvertStopFlag flag = buttonTable.get((JButton)obj);
			if(flag==null){
				log.println("stopButton が登録されていません");
				return;
			}
			convertManager.buttonPushed(flag);
		}
	}

	static int convNo = 1;
	public void DoButton_actionPerformed(ActionEvent e) {
		try{
			if(changeListener!=null){
				nThreadSpinner.addChangeListener(changeListener);
				changeListener = null;
				AllExecButton.setEnabled(true);
				AllExecButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						AllExecButton_handler(e);
					}
				});
			}
			PendingMode = PendingModeCheckbox.isSelected();
			OneLineMode = OneLineCheckbox.isSelected();
			sendtext(" ");
			DoButton.setEnabled(false);
			//DoButton has nolonger stop function
			// so new video or new mylist will be converted
			url = VideoID_TextField.getText();
			if(url==null)
				url = "";
			if(url.isEmpty()){
				sendtext("URL/IDが入力されていません");
				log.println("変換ボタンが押されたがURL/ID欄が入力されていません");
				return;
			}
			requestHistory.add(url);
			/*
			 * URL解析
			 */
			boolean isLocal = checkLocal(url);
			if(isLocal){
				MainTabbedPane.setSelectedComponent(SavingInfoTabPanel);
				return;
			}
			boolean isMylist = parseUrlMylist();
			String vid = isMylist? url : Tag;
			managementPanel.addNotify();
			MainTabbedPane.setSelectedComponent(managementPanel);
			ListInfo listInfo = new ListInfo(vid,OneLineMode);
			JLabel[] status3 = listInfo.getStatus();
			activityPane.add(listInfo);
			listInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
			VideoID_TextField.setText("");
			JButton stopButton = listInfo.getjButton();
			stopButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					stopButton_actionPerformed(e);
				}
			});
			int indexNow = convNo++;
			//log.println(">"+indexNow+"個目の要求: "+vid);
			sendtext(">"+indexNow+"個目の要求: "+vid);
			ConvertStopFlag stopFlag =
				new ConvertStopFlag(stopButton,"停","待","終", "変", PendingMode);
			buttonTable.put(stopButton, stopFlag);
			ConvertingSetting setting1 = getSetting();
			if (isMylist){
				//マイリストページ動画ID解析
				// url = "http://www/nicovideo.jp/mylist/1234567?watch_harmful=1" など
				movieList = new StringBuffer();
				mylistGetter = new MylistGetter(
					indexNow,
					url,
					this,
					status3,
					stopFlag,
					errorControl,
					movieList);
				mylistGetter.execute();
				stopFlag.go();		//mylistGetterは無条件に実行
				// MylistGetter実行
				// return to dispatch
			}else
			{
				//通常変換
				// Shall fileQueue for playing video be cleared or truncated to 1 file?
				// Now it will be cleared only change mylist to video, temporally.
				// url = "sm1234567?req=mylist" など
				// url = "1234567" マイメモリーこっち
				sendtext(">同時変換数　"+numThread+" "+indexNow);
				log.println(">同時変換数　"+numThread+" "+url);
				log.println(">"+indexNow+" "+url);
				StringBuffer sbret = new StringBuffer();
				convertManager.request(
					indexNow,
					numThread,
					url,
					WayBackField.getText(),
					setting1,
					status3,
					stopFlag,
					this,
					autoPlay,
					errorControl,
					sbret);
				// ConverManager処理を要求
				// return to dispatch
			}
		}catch(Exception ex){
			ex.printStackTrace();
			sendtext("MainFrame error");
			log.println("MainFrame error");
		}
		finally{
			DoButton.setEnabled(true);
		}
	}

	private boolean checkLocal(String path) {
		if(new File(path).exists()){
			File localFile = new File(path);
			String vid;
			String regex = "[]):/\\\\_\\t\\.\\?].*$";
			String Tag = localFile.getName().replaceFirst(regex, "").trim();
			log.println("Tag:"+Tag);
			int i0 = Tag.indexOf('(');
			if(i0 > 0) Tag = Tag.substring(0,i0);
			i0 = Tag.indexOf('[');
			if(i0 > 0) Tag = Tag.substring(0,i0);
			if(Tag.charAt(0)=='('){
				vid = Tag.substring(1);
				Tag += ')';
			} else if(Tag.charAt(0)=='['){
				vid = Tag.substring(1);
				Tag += ']';
			} else {
				vid = Tag;
			}
			log.println("Tag:"+Tag);
			log.println("vid:"+vid);
			if(idcheck(vid)){
				url = vid;
			} else {
				url = "sm0";
			}
			if(vid.startsWith("lv")){
				url = "sm0_"+vid;
			}
			VideoID_TextField.setText(url);
		//	Tag = url;
			if(localFile.isFile()){
				SavingVideoCheckBox.setSelected(false);
				String extension = new Path(localFile).getExtension().toLowerCase();
				if(".mp4.flv.avi".contains(extension)){
					VideoSavedFileField.setText(path);
					Video_SaveFileRadioButton.setSelected(true);
					String localComment = path.replace(extension, ".xml");
					if(new File(localComment).exists()){
						SavingCommentCheckBox.setSelected(false);
						Comment_SaveFileRadioButton.setSelected(true);
						CommentSavedFileField.setText(localComment);
					}
					saveThumbInfoCheckBox.setSelected(false);
					return true;
				}
				return false;
			}else if(localFile.isDirectory()){
				SavingVideoCheckBox.setSelected(false);
				VideoSavedFolderField.setText(path);
				Video_SaveFolderRadioButton.setSelected(true);
				CommentSavedFolderField.setText(path);
				Comment_SaveFolderRadioButton.setSelected(true);
				saveThumbInfoCheckBox.setSelected(false);
			}
			return true;
		}
		return false;
	}

	static boolean idcheck(String vid) {
		if(vid.length() > 13) return false;
		for(char c : vid.toCharArray())
			if(!isAlphaNumeric(c)) return false;
		return true;
	}

	private static boolean isAlphaNumeric(char c) {
		return ('A'<=c&&c<='Z')||(c>='a'&&c<='z')||('0'<=c&&c<='9');
	}

	public static String treatUrlHttp(String url){
		url = url.trim();
		if(url.startsWith("/")){
			url = url.substring(1);
		}
		if(url.startsWith(VIDEO_URL_PARSER)){
			url = url.substring(VIDEO_URL_PARSER.length());
		}else if(url.startsWith("http://www.nicovideo.jp/" + MY_MYLIST)
				||url.startsWith(MY_MYLIST)){
			int index = url.indexOf("/#/");
			if(index < 0){
				url = "http://www.nicovideo.jp/api/deflist/list";
			}else{
				url = "http://www.nicovideo.jp/api/mylist/list?group_id="+url.substring(index+3);
				//url = url.replace("my/mylist/#/","mylist/");
			}
		}else if(!url.startsWith("http")){
			if(	  url.startsWith("mylist/")
				||url.startsWith("user/")
				||url.startsWith("my/")
				||url.startsWith("watch/")
				||url.startsWith("search/")
				||url.startsWith("tag/")){
				url = "http://www.nicovideo.jp/" + url;	//may not work
			}else if(url.startsWith("lv")){
				url = "http://live.nicovideo.jp/watch/"+ url;	//may not work
			}else if(url.startsWith("co")){
				url = "http://com.nicovideo.jp/watch/" + url;	//may not work
			}
		}
		if(url.startsWith("http://www.nicovideo.jp/tag/")
			||url.startsWith("http://www.nicovideo.jp/search/")){
			String keyword = url.replaceFirst("http://www.nicovideo.jp/(search|tag)/", "");
			String watchinfo = "";
			int index = keyword.indexOf('?');
			if (index > 0){
				watchinfo = keyword.substring(index);
				keyword = keyword.substring(0, index);
			}
			if(!keyword.contains("%")){
				try {
					url = url.replace(keyword, URLEncoder.encode(keyword, "UTF-8")) + watchinfo;
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return url;
	}

	private boolean parseUrlMylist() {
		url = treatUrlHttp(url);
		boolean isMylist = url.startsWith("http");
		log.println("Url:"+url+" isMylist="+isMylist);
		int index = 0;
		index = url.indexOf('#');
		if(index >= 0){
			url = url.replace("#+", "?").replace("#/", "?");
		}
		index = url.indexOf('?');
		if(index >= 0){
			int index2 = url.lastIndexOf('/',index);
			Tag = url.substring(index2+1,index);
			watchInfo = url.substring(index);
		}else{
			int index2 = url.lastIndexOf('/');
			Tag = url.substring(index2+1);
			watchInfo = "";
		}
		if(Tag.contains("/")||Tag.contains(":")){
			Tag = Tag.replace("/","_").replace(":","_");
			log.println("BUG Tag changed: "+Tag);
		}
		return isMylist;
	}

	void sendtext(String text) {
		statusBar.setText(text);
	}

	/* FFmpeg versionチェック実行 */
	public void FFVersionButton_actionPerformed(ActionEvent e){
		//JTextArea textout = TextFFmpegOutput;
		JTextArea textout = null;
		try{
			textout = new TextView(
				this,"FFmpegバージョン情報").getTextArea();
			textout.setText(null);
			ArrayList<String> list = execFFmpeg("-version");
			for(String line:list){
				textout.append(line);
			}
			textout.setCaretPosition(0);
		} catch(NullPointerException ex){
			sendtext("(´∀｀)＜ぬるぽ\nガッ\n");
			ex.printStackTrace();
		} catch (FileNotFoundException ex) {
			textout.setText(ex.getMessage());
			ex.printStackTrace();
		}
	}

	/* FFmpeg help 表示 */
	public void FFhelp_actionPerformed(String s){
		JTextArea textout = null;
		try{
			textout = new TextView(
				this,"FFmpegヘルプ情報").getTextArea();
			textout.setText(null);
			ArrayList<String> list = execFFmpeg(s);
			for(String line:list){
				textout.append(line);
			}
			textout.setCaretPosition(0);
		} catch(NullPointerException ex){
			sendtext("(´∀｀)＜ぬるぽ\nガッ\n");
			ex.printStackTrace();
		} catch (FileNotFoundException ex) {
			textout.setText(ex.getMessage());
			ex.printStackTrace();
		}
	}

	/* ダウンロードリスト表示 */
	public void showDownloadList_actionPerformed(ActionEvent s){
		JTextArea textout = null;
		textout = new TextView(
			this, "ダウンロードリスト").getTextArea();
		textout.setText(resultHistory.toString());
	//	textout.setCaretPosition(0);
	}

	// 変換動画再生
//	private void playConvertedVideo_actionPerformed(ActionEvent e) {
//		if(converter!=null)
//			converter.playConvertedVideo();
//	}

	/* readme表示 */
	public void showReadme_actionPerformed(String readmePath){
		HtmlView hv;
		String text = "ファイルが見つかりません.";
		try{
			String docfile = new File("doc"+File.separator+readmePath).getPath();
			text = Path.readAllText(docfile, "MS932");
			hv = new HtmlView(this, "readme表示", "");
			hv.setText(HtmlView.markupHtml(text));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/* 動画情報 表示 */
	public void CheckDownloadVideoButton_actionPerformed(ActionEvent e){
	//	boolean needStop = true;
	//	JTextArea textout = TextFFmpegOutput;
		JTextArea textout = null;
		try {
			textout = new TextView(this,
				"ダウンロード動画情報").getTextArea();
			File inputVideo = null;
			ConvertingSetting setting = getSetting();
			ConvertWorker conv = new ConvertWorker(
					0,
					url,
					WayBackField.getText(),
					setting,
					new JLabel[]{statusBar,new JLabel(),new JLabel()},
					new ConvertStopFlag(new JButton()),
					this,
					autoPlay,
					new ConvertManager(null),
					errorControl,
					new StringBuffer());
			if (setting.isVideoFixFileName()) {
				File folder = setting.getVideoFixFileNameFolder();
				String path = conv.detectTitleFromVideo(folder);
				if (path == null || path.isEmpty()){
					textout.setText("検索しましたが動画が見つかりません。");
					return;
				}
				inputVideo = new File(folder, path);
			} else {
				inputVideo = setting.getVideoFile();
			}
			if (inputVideo == null || !inputVideo.canRead()){
				textout.setText("ダウンロード動画がありません。");
				return;
			}
			textout.setText(inputVideo.getName() + "\n");
			ArrayList<String> list = execFFmpeg("-y -analyzeduration 10M -i \"" + inputVideo.getPath() + "\"");
			for (String line : list){
				if (line.contains("Stream ")){
					textout.append(line.replace("Stream ", "").trim() + "\n");
				}
				else if(line.contains("Duration:")){
					textout.append(line.trim() + "\n");
				}
			}
			textout.setCaretPosition(0);
		} catch(NullPointerException ex){
			sendtext("(´∀｀)＜ぬるぽ\nガッ");
			ex.printStackTrace();
		} catch (FileNotFoundException ex) {
			textout.setText(ex.getMessage());
			ex.printStackTrace();
	//	} finally {
	//		if (needStop) {
	//			converter.getStopFlag().finished();
	//		}
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
	//	log.println("execute:" + ffmpeg.getCmd());
		ffmpeg.exec(new FFmpeg.Callback() {
				@Override
				public void doEveryLoop(String e, Logger log) {
				//	log.println(e);
					output.add(e.trim() + "\n");
				}
		}, log);
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

	public void SettingOptionPathButton_actionPerformed(ActionEvent e) {
		showSaveDialog("オプションフォルダ", OptionPathField, true, true);
		FFmpegOptionModel.setOptionFolder(OptionPathField.getText());
		WideFFmpegOptionModel.setOptionFolder(OptionPathField.getText());
		zqFFmpegOptionModel.setOptionFolder(OptionPathField.getText());
	}

	public void SettingVhookPathButton_actionPerformed(ActionEvent e) {
		showSaveDialog("拡張vhookライブラリへのパス", VhookPathField, false, false);
	}

	public void SettingVhookWidePathButton_actionPerformed(ActionEvent e) {
		showSaveDialog("拡張vhookワイドへのパス", VhookWidePathField, false, false);
	}

	public void SettingVhookZqPathButton_actionPerformed(ActionEvent e) {
		showSaveDialog("Q拡張vhookライブラリへのパス", zqVhookPathField, false, false);
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

			GridBagConstraints grid_x0_y2_0 = new GridBagConstraints();
			grid_x0_y2_0.gridx = 0;
			grid_x0_y2_0.gridy = 2;
			grid_x0_y2_0.gridwidth = 1;
			grid_x0_y2_0.gridheight = 1;
			grid_x0_y2_0.weightx = 1.0;
			grid_x0_y2_0.weighty = 1.0;
			grid_x0_y2_0.anchor = GridBagConstraints.NORTH;
			grid_x0_y2_0.fill = GridBagConstraints.HORIZONTAL;
			grid_x0_y2_0.insets = INSETS_0_5_0_5;
			GridBagConstraints grid_x0_y1_11 = new GridBagConstraints();
			grid_x0_y1_11.weighty = 0.0;
			grid_x0_y1_11.weightx = 1.0;
			grid_x0_y1_11.gridy = 1;
			grid_x0_y1_11.gridx = 0;
			grid_x0_y1_11.anchor = GridBagConstraints.NORTH;
			grid_x0_y1_11.fill = GridBagConstraints.HORIZONTAL;
			grid_x0_y1_11.insets = INSETS_0_5_0_5;
			GridBagConstraints grid_x0_y0_0 = new GridBagConstraints(0, 2,
					1, 1, 1.0, 1.0, GridBagConstraints.NORTH,
					GridBagConstraints.HORIZONTAL, INSETS_0_5_0_5, 0, 0);
			grid_x0_y0_0.gridx = 0;
			grid_x0_y0_0.anchor = GridBagConstraints.CENTER;
			grid_x0_y0_0.weighty = 0.0;
			grid_x0_y0_0.gridy = 0;
			ConvertingSettingPanel = new JPanel();
			ConvertingSettingPanel.setLayout(new GridBagLayout());
			ConvertingSettingPanel.add(experimentPanel, grid_x0_y2_0);
			ConvertingSettingPanel.add(getNGWordSettingPanel(), grid_x0_y1_11);
			ConvertingSettingPanel.add(VhookSettingPanel, grid_x0_y0_0);
		}
		return ConvertingSettingPanel;
	}

	/**
	 * This method initializes NGWordSettingPanel
	 *
s	 * @return javax.swing.JPanel
	 */
	private JPanel getNGWordSettingPanel() {
		if (NGWordSettingPanel == null) {
			sharedNgPanel = new JPanel();
			sharedNgPanel.setLayout(new GridBagLayout());
			GridBagConstraints grid_x0_y5_17 = new GridBagConstraints();
			grid_x0_y5_17.gridx = 0;
			grid_x0_y5_17.gridy = 5;
			grid_x0_y5_17.gridwidth = 2;
			grid_x0_y5_17.weightx = 1.0;
			grid_x0_y5_17.anchor = GridBagConstraints.WEST;
			grid_x0_y5_17.fill = GridBagConstraints.HORIZONTAL;
			grid_x0_y5_17.insets = INSETS_0_0_0_0;
			GridBagConstraints grid_x4_y4_16 = new GridBagConstraints();
			grid_x4_y4_16.gridx = 4;
			grid_x4_y4_16.gridy = 0;
			grid_x4_y4_16.weightx = 1.0;
			grid_x4_y4_16.anchor = GridBagConstraints.WEST;
			grid_x4_y4_16.insets = INSETS_0_0_0_0;
			sharedNgNoneRadioButton.setText("無し");
			GridBagConstraints grid_x3_y4_15 = new GridBagConstraints();
			grid_x3_y4_15.gridx = 3;
			grid_x3_y4_15.gridy = 0;
			grid_x3_y4_15.insets = INSETS_0_0_0_0;
			sharedNgLowRadioButton.setText("弱");
			GridBagConstraints grid_x2_y4_14 = new GridBagConstraints();
			grid_x2_y4_14.gridx = 2;
			grid_x2_y4_14.gridy = 0;
			grid_x2_y4_14.insets = INSETS_0_0_0_0;
			sharedNgMediumRadioButton.setText("中");
			GridBagConstraints grid_x1_y4_13 = new GridBagConstraints();
			grid_x1_y4_13.gridx = 1;
			grid_x1_y4_13.gridy = 0;
			grid_x1_y4_13.insets = INSETS_0_0_0_0;
			sharedNgHighRadioButton.setText("強");
			GridBagConstraints grid_x0_y4_12 = new GridBagConstraints();
			grid_x0_y4_12.gridx = 0;
			grid_x0_y4_12.gridy = 0;
			grid_x0_y4_12.anchor = GridBagConstraints.WEST;
			grid_x0_y4_12.insets = INSETS_0_5_0_5;
			sharedNgLabel = new JLabel();
			sharedNgLabel.setText("NG共有レベル ");
			sharedNgLabel.setForeground(Color.blue);
			sharedNgPanel.add(sharedNgLabel, grid_x0_y4_12);
			sharedNgPanel.add(sharedNgHighRadioButton, grid_x1_y4_13);
			sharedNgPanel.add(sharedNgMediumRadioButton, grid_x2_y4_14);
			sharedNgPanel.add(sharedNgLowRadioButton, grid_x3_y4_15);
			sharedNgPanel.add(sharedNgNoneRadioButton, grid_x4_y4_16);
		//	GridBagConstraints grid_x1_y4_11 = new GridBagConstraints();
		//	grid_x1_y4_11.gridx = 1;
		//	grid_x1_y4_11.gridy = 4;
		//	grid_x1_y4_11.weightx = 1.0;
		//	grid_x1_y4_11.fill = GridBagConstraints.HORIZONTAL;
		//	grid_x1_y4_11.insets = INSETS_0_5_0_5;
		//	replaceCommandField = new JTextField();
		//	replaceCommandField.setForeground(Color.blue);
		//	GridBagConstraints grid_x0_y4_10 = new GridBagConstraints();
		//	grid_x0_y4_10.gridx = 0;
		//	grid_x0_y4_10.gridy = 4;
		//	grid_x0_y4_10.anchor = GridBagConstraints.WEST;
		//	grid_x0_y4_10.insets = INSETS_0_5_0_0;
		//	replaceCommandLabel = new JLabel(" ");
		//	replaceCommandLabel.setForeground(Color.blue);
			GridBagConstraints grid_x1_y3_9 = new GridBagConstraints();
			grid_x1_y3_9.gridx = 1;
			grid_x1_y3_9.gridy = 3;
			grid_x1_y3_9.weightx = 1.0;
			grid_x1_y3_9.fill = GridBagConstraints.HORIZONTAL;
			grid_x1_y3_9.insets = INSETS_0_5_0_5;
			ngCommandField = new JTextField();
			ngCommandField.setForeground(Color.blue);
			GridBagConstraints grid_x0_y3_8 = new GridBagConstraints();
			grid_x0_y3_8.gridx = 0;
			grid_x0_y3_8.gridy = 3;
			grid_x0_y3_8.anchor = GridBagConstraints.WEST;
			grid_x0_y3_8.insets = INSETS_0_5_0_0;
			ngCommandLabel = new JLabel("NGコマンド");
			ngCommandLabel.setForeground(Color.blue);
		//	GridBagConstraints grid_x1_y2_7 = new GridBagConstraints();
		//	grid_x1_y2_7.gridx = 1;
		//	grid_x1_y2_7.gridy = 2;
		//	grid_x1_y2_7.weightx = 1.0;
		//	grid_x1_y2_7.fill = GridBagConstraints.HORIZONTAL;
		//	grid_x1_y2_7.insets = INSETS_0_5_0_5;
		//	scoreTextField.setText("" + SharedNgGroup.MINSCORE);
		//	scoreTextField.setForeground(Color.blue);
		//	GridBagConstraints grid_x0_y2_6 = new GridBagConstraints();
		//	grid_x0_y2_6.gridx = 0;
		//	grid_x0_y2_6.gridy = 2;
		//	grid_x0_y2_6.anchor = GridBagConstraints.WEST;
		//	grid_x0_y2_6.insets = INSETS_0_5_0_0;
		//	scoreCheckBox.setText("score");
		//	scoreCheckBox.setForeground(Color.blue);
			GridBagConstraints grid_x1_y1_5 = new GridBagConstraints();
			grid_x1_y1_5.fill = GridBagConstraints.HORIZONTAL;
			grid_x1_y1_5.gridy = 1;
			grid_x1_y1_5.weightx = 1.0;
			grid_x1_y1_5.insets = INSETS_0_5_0_5;
			grid_x1_y1_5.gridx = 1;
			GridBagConstraints grid_x0_y1_4 = new GridBagConstraints();
			grid_x0_y1_4.gridx = 0;
			grid_x0_y1_4.anchor = GridBagConstraints.WEST;
			grid_x0_y1_4.insets = INSETS_0_5_0_0;
			grid_x0_y1_4.gridy = 1;
			NGIDLabel = new JLabel();
			NGIDLabel.setText("NG ID");
			GridBagConstraints grid_x1_y0_3 = new GridBagConstraints();
			grid_x1_y0_3.fill = GridBagConstraints.HORIZONTAL;
			grid_x1_y0_3.gridy = 0;
			grid_x1_y0_3.weightx = 1.0;
			grid_x1_y0_3.insets = INSETS_0_5_0_5;
			grid_x1_y0_3.gridx = 1;
			GridBagConstraints grid_x0_y0_2 = new GridBagConstraints();
			grid_x0_y0_2.gridx = 0;
			grid_x0_y0_2.insets = INSETS_0_5_0_0;
			grid_x0_y0_2.gridy = 0;
			grid_x0_y0_2.anchor = GridBagConstraints.WEST;
			NGWordLabel = new JLabel();
			NGWordLabel.setText("NGワード");
			NGWordSettingPanel = new JPanel();
			NGWordSettingPanel.setLayout(new GridBagLayout());
			NGWordSettingPanel.setBorder(BorderFactory.createTitledBorder(
					BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
					"NGワード・ID設定"));
			NGWordSettingPanel.add(NGWordLabel, grid_x0_y0_2);
			NGWordSettingPanel.add(getNGWordTextField(), grid_x1_y0_3);
			NGWordSettingPanel.add(NGIDLabel, grid_x0_y1_4);
			NGWordSettingPanel.add(getNGIDTextField(), grid_x1_y1_5);
			NGWordSettingPanel.add(ngCommandLabel, grid_x0_y3_8);
			NGWordSettingPanel.add(ngCommandField, grid_x1_y3_9);
		//	NGWordSettingPanel.add(replaceCommandLabel, grid_x0_y4_10);
		//	NGWordSettingPanel.add(replaceCommandField, grid_x1_y4_11);
			NGWordSettingPanel.add(sharedNgPanel, grid_x0_y5_17);
			sharedNgScore.add(sharedNgHighRadioButton,SharedNgScore.HIGH);
			sharedNgScore.add(sharedNgMediumRadioButton,SharedNgScore.MEDIUM);
			sharedNgScore.add(sharedNgLowRadioButton,SharedNgScore.LOW);
			sharedNgScore.add(sharedNgNoneRadioButton,SharedNgScore.NONE);
			sharedNgNoneRadioButton.setSelected(true);
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
			//grid_x0_y0_13.gridwidth = 1;
			grid_x0_y0_13.weightx = 0.0;
			grid_x0_y0_13.fill = GridBagConstraints.HORIZONTAL;
			grid_x0_y0_13.insets = INSETS_0_5_0_5;
			grid_x0_y0_13.gridy = 0;
			GridBagConstraints grid_x1_y2_12 = new GridBagConstraints();
			grid_x1_y2_12.fill = GridBagConstraints.HORIZONTAL;
			grid_x1_y2_12.gridy = 1;
			grid_x1_y2_12.weightx = 1.0;
			grid_x1_y2_12.insets = new Insets(5, 0, 5, 5);
			grid_x1_y2_12.gridx = 2;
			GridBagConstraints grid_x0_y2_10 = new GridBagConstraints();
			grid_x0_y2_10.gridx = 1;
			grid_x0_y2_10.insets = INSETS_5_5_5_5;
			grid_x0_y2_10.gridy = 1;
			ProxyPortLabel = new JLabel();
			ProxyPortLabel.setText("ポート番号");
			GridBagConstraints grid_x1_y1_9 = new GridBagConstraints();
			grid_x1_y1_9.fill = GridBagConstraints.BOTH;
			grid_x1_y1_9.gridy = 0;
			grid_x1_y1_9.weightx = 1.0;
			grid_x1_y1_9.insets = INSETS_0_0_0_5;
			grid_x1_y1_9.gridx = 2;
			GridBagConstraints grid_x0_y1_8 = new GridBagConstraints();
			grid_x0_y1_8.gridx = 1;
			grid_x0_y1_8.insets = INSETS_0_5_0_5;
			grid_x0_y1_8.fill = GridBagConstraints.NONE;
			grid_x0_y1_8.anchor = GridBagConstraints.WEST;
			grid_x0_y1_8.gridy = 0;
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
			grid_x0_y1_15.insets = INSETS_0_25_0_5;
			GridBagConstraints grid_x2_y5 = new GridBagConstraints(2,
					5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, INSETS_0_0_5_0, 0, 0);
			GridBagConstraints grid_x3_y5_32 = new GridBagConstraints(3,
					4, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
					GridBagConstraints.NONE, INSETS_0_0_0_5, 0, 0);
			grid_x3_y5_32.gridx = 3;
			grid_x3_y5_32.insets = INSETS_0_0_5_5;
			grid_x3_y5_32.gridy = 5;
			GridBagConstraints grid_x0_y5_30 = new GridBagConstraints(0,
					5, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 50, 5, 5), 0, 0);
			GridBagConstraints grid_x0_y4_29 = new GridBagConstraints(0,
					3, 4, 1, 1.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, INSETS_0_25_0_5, 0, 0);
			grid_x0_y4_29.gridx = 0;
			grid_x0_y4_29.gridy = 4;
			GridBagConstraints grid_x0_y3_28 = new GridBagConstraints(0,
					3, 2, 1, 1.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.BOTH, INSETS_0_50_0_5, 0, 0);
			GridBagConstraints grid_x0_y2_27 = new GridBagConstraints(0,
					1, 4, 1, 1.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, INSETS_0_25_0_5, 0,
					0);
			grid_x0_y2_27.gridx = 0;
			grid_x0_y2_27.gridy = 2;
			GridBagConstraints grid_x0_y0_34 = new GridBagConstraints();
			grid_x0_y0_34.insets = INSETS_0_5_0_5;
			grid_x0_y0_34.gridy = 0;
			grid_x0_y0_34.weightx = 0.0;
			grid_x0_y0_34.fill = GridBagConstraints.HORIZONTAL;
			grid_x0_y0_34.gridx = 0;
			GridBagConstraints grid_x1_y0_34_2 = new GridBagConstraints();
			grid_x1_y0_34_2.insets = INSETS_0_5_0_5;
			grid_x1_y0_34_2.gridy = 0;
			grid_x1_y0_34_2.weightx = 1.0;
			grid_x1_y0_34_2.fill = GridBagConstraints.HORIZONTAL;
			grid_x1_y0_34_2.gridx = 1;
			GridBagConstraints grid_x2_y3 = new GridBagConstraints(2,
					3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, INSETS_0_0_0_0, 0, 0);
			GridBagConstraints grid_x3_y3_31 = new GridBagConstraints(3,
					2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, INSETS_0_0_0_5, 0, 0);
			grid_x3_y3_31.gridy = 3;
			grid_x3_y3_31.gridx = 3;
			VideoSaveInfoPanel = new JPanel();
			VideoSaveInfoPanel.setLayout(new GridBagLayout());
			VideoSaveInfoPanel.setBorder(BorderFactory.createTitledBorder(
					BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
					"動画保存設定"
				));
				//	, TitledBorder.LEADING, TitledBorder.TOP,
				//	new Font("MS UI Gothic", Font.PLAIN, 12), Color.black));
			VideoSaveInfoPanel.add(SavingVideoCheckBox, grid_x0_y0_34);
			VideoSaveInfoPanel.add(disableEcoCheckBox, grid_x1_y0_34_2);
			VideoSaveInfoPanel.add(getDelVideoCheckBox(), grid_x0_y1_15);
			VideoSaveInfoPanel.add(Video_SaveFolderRadioButton,
					grid_x0_y2_27);
			VideoSaveInfoPanel.add(VideoSavedFolderField, grid_x0_y3_28);
			VideoSaveInfoPanel.add(openVideoSaveFolderButton,grid_x2_y3);
			VideoSaveInfoPanel.add(ShowSavingVideoFolderDialogButton,
					grid_x3_y3_31);
			VideoSaveInfoPanel.add(openVideoSaveFileButton,grid_x2_y5);
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
			SaveInfoTabPaneEach.addTab("ページ情報", null,
					getWatchPageSavingTabbedPanel(), null);
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
			grid_x_y_36.insets = INSETS_0_5_0_5;
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
			grid_x0_y1_37.insets = INSETS_0_5_0_5;
			GridBagConstraints grid_x0_y2_79 = new GridBagConstraints();
			grid_x0_y2_79.gridx = 0;
			grid_x0_y2_79.gridy = 2;
			grid_x0_y2_79.gridwidth = 1;
			grid_x0_y2_79.weightx = 1.0;
			grid_x0_y2_79.weighty = 1.0;
			grid_x0_y2_79.anchor = GridBagConstraints.NORTH;
			grid_x0_y2_79.fill = GridBagConstraints.HORIZONTAL;
			grid_x0_y2_79.insets = INSETS_0_5_0_5;
			VideoSavingTabbedPanel = new JPanel();
			VideoSavingTabbedPanel.setLayout(new GridBagLayout());
			VideoSavingTabbedPanel.add(getVideoSaveInfoPanel(),
					grid_x_y_36);
			VideoSavingTabbedPanel.add(CommentSaveInfoPanel,
					grid_x0_y1_37);
			VideoSavingTabbedPanel.add(OldCommentModePanel,
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
					GridBagConstraints.HORIZONTAL, INSETS_0_5_0_5, 0, 0);
			grid_x_y_38.gridx = 0;
			grid_x_y_38.fill = GridBagConstraints.HORIZONTAL;
			grid_x_y_38.gridy = 0;
			grid_x_y_38.weighty = 0.0;
			ConvertedVideoSavingTabbedPanel = new JPanel();
			ConvertedVideoSavingTabbedPanel.setLayout(new GridBagLayout());
			ConvertedVideoSavingTabbedPanel.add(ConvertedVideoSavingInfoPanel,
					grid_x_y_38);
			GridBagConstraints grid_x_y__ = new GridBagConstraints();
			grid_x_y__.gridx = 0;
			grid_x_y__.gridy = 1;
			grid_x_y__.gridwidth = 1;
			grid_x_y__.gridheight = 1;
			grid_x_y__.anchor = GridBagConstraints.NORTH;
			grid_x_y__.fill = GridBagConstraints.HORIZONTAL;
			grid_x_y__.insets = INSETS_0_5_0_5;
			grid_x_y__.weightx = 1.0;
			grid_x_y__.weighty = 0.0;
			ConvertedVideoSavingTabbedPanel.add(OptionalThreadInfoPanel, grid_x_y__);

			liveConvertInfoPanel.setBorder(BorderFactory.createTitledBorder(
					BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
					"生放送変換設定", TitledBorder.LEADING, TitledBorder.TOP,
					getFont(), Color.red));
			liveConvertInfoPanel.setLayout(new GridBagLayout());
			liveOperationCheckBox.setText("運営コメ簡易変更");
			liveOperationCheckBox.setForeground(Color.blue);
			liveOperationCheckBox.setToolTipText("運営コメントのコマンドを少し変更します(ニコ生と同じではありません)");
			GridBagConstraints grid14_x0_y1 = new GridBagConstraints();
			grid14_x0_y1.gridx = 0;
			grid14_x0_y1.gridy = 1;
			grid14_x0_y1.gridwidth = 3;
			grid14_x0_y1.weightx = 1.0;
			grid14_x0_y1.anchor = GridBagConstraints.NORTHWEST;
			grid14_x0_y1.fill = GridBagConstraints.HORIZONTAL;
			grid14_x0_y1.insets = INSETS_0_5_0_5;
			liveConvertInfoPanel.add(liveOperationCheckBox, grid14_x0_y1);
			liveOparationDurationChangeCheckBox.setText("運営コメントの秒数を自分で設定する");
			liveOparationDurationChangeCheckBox.setForeground(Color.blue);
			GridBagConstraints grid14_x0_y3 = new GridBagConstraints();
			grid14_x0_y3.gridx = 0;
			grid14_x0_y3.gridy = 3;
			grid14_x0_y3.gridwidth = 2;
			grid14_x0_y3.weightx = 0.0;
			grid14_x0_y3.anchor = GridBagConstraints.NORTHWEST;
			grid14_x0_y3.fill = GridBagConstraints.HORIZONTAL;
			grid14_x0_y3.insets = INSETS_0_25_0_5;
			liveConvertInfoPanel.add(liveOparationDurationChangeCheckBox,grid14_x0_y3);
			liveOperationDurationTextField = new JTextField();
			GridBagConstraints grid14_x0_y4 = (GridBagConstraints)grid14_x0_y1.clone();
			grid14_x0_y4.gridx = 2;
			grid14_x0_y4.gridy = 3;
			grid14_x0_y4.gridwidth = 1;
			grid14_x0_y4.weightx = 1.0;
			grid14_x0_y4.insets = INSETS_0_5_0_5;
			liveConvertInfoPanel.add(liveOperationDurationTextField, grid14_x0_y4);
			liveCommentModeCheckBox.setText("生コメ強制　動画でも強制的にニコ生コメント仕様");
			liveCommentModeCheckBox.setForeground(Color.blue);
			liveCommentModeCheckBox.setToolTipText("通常は自動判定,nakaコメ1秒遅延,blue2の色変更等");
			GridBagConstraints grid14_x0_y6 = new GridBagConstraints();
			grid14_x0_y6.gridx = 0;
			grid14_x0_y6.gridy = 6;
			grid14_x0_y6.gridwidth = 3;
			grid14_x0_y6.weightx = 1.0;
			grid14_x0_y6.anchor = GridBagConstraints.NORTHWEST;
			grid14_x0_y6.fill = GridBagConstraints.HORIZONTAL;
			grid14_x0_y6.insets = INSETS_0_5_0_5;
			liveConvertInfoPanel.add(liveCommentModeCheckBox, grid14_x0_y6);
			liveCommentVposShiftCheckBox.setText("コメントを遅らせる(秒)(マイナス値は早める)");
			liveCommentVposShiftCheckBox.setForeground(Color.blue);
			liveCommentVposShiftCheckBox.setToolTipText("コメントのVPOS値(時刻)を秒単位(小数点値可能)で変更します。itsoffsetとは逆の効果");
			GridBagConstraints grid14_x0_y7 = new GridBagConstraints();
			grid14_x0_y7.gridx = 0;
			grid14_x0_y7.gridy = 7;
			grid14_x0_y7.gridwidth = 2;
			grid14_x0_y7.weightx = 0.0;
			grid14_x0_y7.anchor = GridBagConstraints.NORTHWEST;
			grid14_x0_y7.fill = GridBagConstraints.HORIZONTAL;
			grid14_x0_y7.insets = INSETS_0_5_0_5;
			liveConvertInfoPanel.add(liveCommentVposShiftCheckBox,grid14_x0_y7);
			GridBagConstraints grid14_x2_y7 = new GridBagConstraints();
			grid14_x2_y7.gridx = 2;
			grid14_x2_y7.gridy = 7;
			grid14_x2_y7.gridwidth = 1;
			grid14_x2_y7.weightx = 1.0;
			grid14_x2_y7.anchor = GridBagConstraints.NORTHWEST;
			grid14_x2_y7.fill = GridBagConstraints.HORIZONTAL;
			grid14_x2_y7.insets = INSETS_0_5_0_5;
			liveConvertInfoPanel.add(liveCommentVposShiftTextField,grid14_x2_y7);
			GridBagConstraints grid_x0_y2 = new GridBagConstraints();
			grid_x0_y2.gridx = 0;
			grid_x0_y2.gridy = 2;
			grid_x0_y2.gridwidth = 1;
			grid_x0_y2.gridheight = 1;
			grid_x0_y2.anchor = GridBagConstraints.NORTH;
			grid_x0_y2.fill = GridBagConstraints.HORIZONTAL;
			grid_x0_y2.insets = INSETS_0_5_0_5;
			grid_x0_y2.weightx = 1.0;
			grid_x0_y2.weighty = 1.0;
			ConvertedVideoSavingTabbedPanel.add(liveConvertInfoPanel, grid_x0_y2);
		}
		return ConvertedVideoSavingTabbedPanel;
	}

	private void openConvertedVideoSavingTabbedPanel(){
		MainTabbedPane.setSelectedComponent(SavingInfoTabPanel);
		SaveInfoTabPaneEach.setSelectedComponent(ConvertedVideoSavingTabbedPanel);
	}

	/**
	 * getWatchPageSavingTabbedPanel
	 * @return watchPageSavingTabbedPanel
	 */
	private JPanel getWatchPageSavingTabbedPanel() {
		GridBagConstraints grid_x0_y0 = new GridBagConstraints();
		grid_x0_y0.gridx = 0;
		grid_x0_y0.gridy = 0;
		grid_x0_y0.anchor = GridBagConstraints.NORTH;
		grid_x0_y0.fill = GridBagConstraints.HORIZONTAL;
		grid_x0_y0.insets = INSETS_0_5_0_5;
		grid_x0_y0.weightx = 1.0;
		grid_x0_y0.weighty = 0.0;
		watchPageSavingTabbedPanel = new JPanel();
		watchPageSavingTabbedPanel.setLayout(new GridBagLayout());
		watchPageSavingTabbedPanel.add(getWatchPageSavingInfoPanel(), grid_x0_y0);

		GridBagConstraints grid_x0_y1 = new GridBagConstraints();
		grid_x0_y1.gridx = 0;
		grid_x0_y1.gridy = 1;
		grid_x0_y1.anchor = GridBagConstraints.NORTH;
		grid_x0_y1.fill = GridBagConstraints.HORIZONTAL;
		grid_x0_y1.insets = INSETS_0_5_0_5;
		grid_x0_y1.weightx = 1.0;
		grid_x0_y1.weighty = 1.0;
		watchPageSavingTabbedPanel.add(getFileNameInfoPanel(),grid_x0_y1);
		return watchPageSavingTabbedPanel;
	}

	/**
	 * getWatchPageSavingInfoPanel
	 * @return watchPageSavingTabbedPanel
	 */
	private JPanel getWatchPageSavingInfoPanel() {
		watchPageSavingInfoPanel = new JPanel();
		watchPageSavingInfoPanel.setLayout(new GridBagLayout());
		watchPageSavingInfoPanel.setBorder(
			BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
			"情報保存設定"));

		saveThumbInfoCheckBox.setText("動画情報を保存する（Videoと同じフォルダ）");
		saveThumbInfoCheckBox.setSelected(true);
		GridBagConstraints grid_x0_y0 = new GridBagConstraints();
		grid_x0_y0.gridx = 0;
		grid_x0_y0.gridy = 0;
		grid_x0_y0.anchor = GridBagConstraints.NORTH;
		grid_x0_y0.fill = GridBagConstraints.HORIZONTAL;
		grid_x0_y0.insets = INSETS_0_0_0_5;
		grid_x0_y0.weightx = 1.0;
		grid_x0_y0.weighty = 1.0;
		grid_x0_y0.gridwidth = 2;
		watchPageSavingInfoPanel.add(saveThumbInfoCheckBox, grid_x0_y0);

		saveThumbInfoExtTxtRadioButton.setText("拡張子.txt");
		saveThumbInfoExtTxtRadioButton.setSelected(true);
		GridBagConstraints grid_x0_y1 = new GridBagConstraints();
		grid_x0_y1.gridx = 0;
		grid_x0_y1.gridy = 1;
		grid_x0_y1.anchor = GridBagConstraints.NORTH;
		grid_x0_y1.fill = GridBagConstraints.HORIZONTAL;
		grid_x0_y1.insets = INSETS_0_5_0_5;
		grid_x0_y1.weightx = 0.0;
		grid_x0_y1.weighty = 1.0;
		watchPageSavingInfoPanel.add(saveThumbInfoExtTxtRadioButton, grid_x0_y1);

		saveThumbInfoExtXmlRadioButton.setText("拡張子.xml");
		saveThumbInfoExtXmlRadioButton.setSelected(false);
		GridBagConstraints grid_x1_y1 = new GridBagConstraints();
		grid_x1_y1.gridx = 1;
		grid_x1_y1.gridy = 1;
		grid_x1_y1.anchor = GridBagConstraints.NORTHWEST;
		grid_x1_y1.fill = GridBagConstraints.HORIZONTAL;
		grid_x1_y1.insets = INSETS_0_0_0_5;
		grid_x1_y1.weightx = 1.0;
		grid_x1_y1.weighty = 1.0;
		watchPageSavingInfoPanel.add(saveThumbInfoExtXmlRadioButton, grid_x1_y1);

		saveThumbUserCheckBox.setText("投稿者名を保存する(上の動画情報ファイル)");
		GridBagConstraints grid_x0_y2 = new GridBagConstraints();
		grid_x0_y2.gridx = 0;
		grid_x0_y2.gridy = 2;
		grid_x0_y2.anchor = GridBagConstraints.NORTH;
		grid_x0_y2.fill = GridBagConstraints.HORIZONTAL;
		grid_x0_y2.insets = INSETS_0_0_0_5;
		grid_x0_y2.weightx = 1.0;
		grid_x0_y2.weighty = 1.0;
		grid_x0_y2.gridwidth = 2;
		watchPageSavingInfoPanel.add(saveThumbUserCheckBox, grid_x0_y2);

		userFolderLabel.setText("ユーザーフォルダ");
		GridBagConstraints grid_x0_y3 = new GridBagConstraints();
		grid_x0_y3.gridx = 0;
		grid_x0_y3.gridy = 3;
		grid_x0_y3.anchor = GridBagConstraints.WEST;
		grid_x0_y3.fill = GridBagConstraints.HORIZONTAL;
		grid_x0_y3.insets = INSETS_0_5_0_5;
		grid_x0_y3.weightx = 0.0;
		grid_x0_y3.weighty = 1.0;
		watchPageSavingInfoPanel.add(userFolderLabel, grid_x0_y3);

		userFolderTextField.setText("."+File.separator+"user");
		GridBagConstraints grid_x1_y3 = new GridBagConstraints();
		grid_x1_y3.gridx = 1;
		grid_x1_y3.gridy = 3;
		grid_x1_y3.anchor = GridBagConstraints.NORTHWEST;
		grid_x1_y3.fill = GridBagConstraints.HORIZONTAL;
		grid_x1_y3.insets = INSETS_0_0_0_5;
		grid_x1_y3.weightx = 1.0;
		grid_x1_y3.weighty = 1.0;
		watchPageSavingInfoPanel.add(userFolderTextField, grid_x1_y3);

		saveThumbnailJpgCheckBox.setText("サムネイル画像を保存する(videoと同じフォルダ)");
		GridBagConstraints grid_x0_y4 = new GridBagConstraints();
		grid_x0_y4.gridx = 0;
		grid_x0_y4.gridy = 4;
		grid_x0_y4.anchor = GridBagConstraints.NORTHWEST;
		grid_x0_y4.fill = GridBagConstraints.HORIZONTAL;
		grid_x0_y4.insets = INSETS_0_0_0_5;
		grid_x0_y4.weightx = 1.0;
		grid_x0_y4.weighty = 1.0;
		grid_x0_y4.gridwidth = 2;
		watchPageSavingInfoPanel.add(saveThumbnailJpgCheckBox, grid_x0_y4);

		saveWatchPageInfoCheckBox.setText("watchページをhtmlファイルに保存する（.\\temp下）");
		GridBagConstraints grid_x0_y9 = new GridBagConstraints();
		grid_x0_y9.gridx = 0;
		grid_x0_y9.gridy = 9;
		grid_x0_y9.anchor = GridBagConstraints.NORTH;
		grid_x0_y9.fill = GridBagConstraints.HORIZONTAL;
		grid_x0_y9.insets = INSETS_0_0_0_5;
		grid_x0_y9.weightx = 1.0;
		grid_x0_y9.weighty = 1.0;
		grid_x0_y9.gridwidth = 2;
		watchPageSavingInfoPanel.add(saveWatchPageInfoCheckBox, grid_x0_y9);

		saveAutoListCheckBox.setText("マイリスト自動変換用のautolist.batファイルを保存する");
		GridBagConstraints grid_x0_y10 = new GridBagConstraints();
		grid_x0_y10.gridx = 0;
		grid_x0_y10.gridy = 10;
		grid_x0_y10.anchor = GridBagConstraints.NORTH;
		grid_x0_y10.fill = GridBagConstraints.HORIZONTAL;
		grid_x0_y10.insets = INSETS_0_0_0_5;
		grid_x0_y10.weightx = 1.0;
		grid_x0_y10.weighty = 1.0;
		grid_x0_y10.gridwidth = 2;
		watchPageSavingInfoPanel.add(saveAutoListCheckBox, grid_x0_y10);

		return watchPageSavingInfoPanel;
	}

	/**
	 * getfileNameInfoPanel
	 * @return
	 */
	private JPanel getFileNameInfoPanel(){
		fileNameInfoPanel = new JPanel();
		fileNameInfoPanel.setLayout(new GridBagLayout());
		fileNameInfoPanel.setBorder(
			BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				"保存ファイル名設定（特殊）", TitledBorder.LEADING,
				TitledBorder.TOP, getFont(), Color.red));
/*
		fileNameInfoPanel.setBorder(
			BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
			"保存ファイル名設定（特殊）"));
 */
		GridBagConstraints gridbagc = new GridBagConstraints();
		JLabel fileNameInfoLanel = new JLabel();
		fileNameInfoLanel.setText("この設定を行うと自動で変換できない場合があります");
		fileNameInfoLanel.setForeground(Color.red);
		fileNameInfoLanel.setFont(new Font(Font.SERIF,Font.BOLD,new Font(null).getSize()+1));
		gridbagc.gridx = 0;
		gridbagc.gridy = 0;
		gridbagc.anchor = GridBagConstraints.WEST;
		gridbagc.fill = GridBagConstraints.HORIZONTAL;
		gridbagc.insets = INSETS_0_0_0_5;
		gridbagc.weightx = 1.0;
		fileNameInfoPanel.add(fileNameInfoLanel, gridbagc);

		changeMp4ExtCheckBox.setText("mp4保存動画の拡張子を.mp4にする（既定は.flv）");
		gridbagc.gridx = 0;
		gridbagc.gridy = 1;
		gridbagc.anchor = GridBagConstraints.WEST;
		gridbagc.fill = GridBagConstraints.HORIZONTAL;
		gridbagc.insets = INSETS_0_0_0_5;
		gridbagc.weightx = 1.0;
		fileNameInfoPanel.add(changeMp4ExtCheckBox, gridbagc);

		changeTitleIdCheckBox.setText("保存動画のIDをタイトルの後ろにつける（既定はタイトルの前）");
		gridbagc.gridx = 0;
		gridbagc.gridy = 2;
		gridbagc.anchor = GridBagConstraints.WEST;
		gridbagc.fill = GridBagConstraints.HORIZONTAL;
		gridbagc.insets = INSETS_0_0_0_5;
		gridbagc.weightx = 1.0;
		fileNameInfoPanel.add(changeTitleIdCheckBox, gridbagc);
		return fileNameInfoPanel;
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
	private final FFmpegComboBoxModel WideFFmpegOptionModel = new FFmpegComboBoxModel();
	private final FFmpegComboBoxModel zqFFmpegOptionModel = new FFmpegComboBoxModel();

	private JLabel ExtOptionLabel = null;
	private JTextField ExtOptionField = null;
	private JCheckBox NotUseVhookCheckBox = null;
	private JTextField ViewCommentField = null;
	private JLabel ViewCommentLabel = null;
	private JLabel ShadowKindLabel = null;
	@SuppressWarnings("rawtypes")
	private JComboBox ShadowComboBox = null;

	/**
	 * Initialize FFmpegOptionComboBox
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private JComboBox getFFmpegOptionComboBox() {
		if (FFmpegOptionComboBox == null) {
			FFmpegOptionComboBox = new JComboBox(FFmpegOptionModel);
			FFmpegOptionComboBox
					.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							if (FFmpegOptionModel.isFile()) {// ファイル
								Properties prop = new Properties();
								try {
									prop.loadFromXML(new FileInputStream(
										FFmpegOptionModel.getSelectedFile()));
								} catch (IOException ex) {
									sendtext("設定2のオプションファイルが表示できません。");
									ex.printStackTrace();
								}
								ExtOptionField.setText(prop.getProperty("EXT", ""));
								MainOptionField.setText(prop.getProperty("MAIN", ""));
								CommandLineInOptionField.setText(prop.getProperty("IN", ""));
								CommandLineOutOptionField.setText(prop.getProperty("OUT", ""));
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
	 * Initialize WideFFmpegOptionComboBox
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JComboBox getWideFFmpegOptionComboBox() {
		if (WideFFmpegOptionComboBox == null) {
			WideFFmpegOptionComboBox = new JComboBox(WideFFmpegOptionModel);
			WideFFmpegOptionComboBox
					.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							if (WideFFmpegOptionModel.isFile()) {// ファイル
								Properties prop = new Properties();
								try {
									prop.loadFromXML(new FileInputStream(
										WideFFmpegOptionModel.getSelectedFile()));
								} catch (IOException ex) {
									sendtext("設定2のオプションファイルが表示できません。");
									ex.printStackTrace();
								}
								wideExtOptionField.setText(prop.getProperty("EXT", ""));
								wideMainOptionField.setText(prop.getProperty("MAIN", ""));
								wideCommandLineInOptionField.setText(prop.getProperty("IN", ""));
								wideCommandLineOutOptionField.setText(prop.getProperty("OUT", ""));
								wideExtOptionField.setEnabled(false);
								wideMainOptionField.setEnabled(false);
								wideCommandLineInOptionField.setEnabled(false);
								wideCommandLineOutOptionField.setEnabled(false);
								return;
							} else {// ファイルでない
								wideExtOptionField.setEnabled(true);
								wideMainOptionField.setEnabled(true);
								wideCommandLineInOptionField.setEnabled(true);
								wideCommandLineOutOptionField.setEnabled(true);
							}
						}
					});
		}
		return WideFFmpegOptionComboBox;
	}

	/**
	 * Initialize WideFFmpegOptionComboBox
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JComboBox getZqFFmpegOptionComboBox() {
		if (zqFFmpegOptionComboBox == null) {
			zqFFmpegOptionComboBox = new JComboBox(zqFFmpegOptionModel);
			zqFFmpegOptionComboBox
					.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							if (zqFFmpegOptionModel.isFile()) {// ファイル
								File optionXml = null;
								Properties prop = new Properties();
								String descr = "";
								try {
									optionXml = zqFFmpegOptionModel.getSelectedFile();
									prop.loadFromXML(new FileInputStream(optionXml));
									descr = Path.readAllText(optionXml.getAbsolutePath(), "UTF-8");
									if(descr.contains("<!--"))
										descr = descr.replaceAll("\\s+"," ").replaceAll(".*<!--", "").replaceAll("-->.*", "");
									else
										descr = "";
								} catch (IOException ex) {
									sendtext("Qwatchのオプションファイルが表示できません。");
									ex.printStackTrace();
								}
								zqExtOptionField.setText(prop.getProperty("EXT", ""));
								zqMainOptionField.setText(prop.getProperty("MAIN", ""));
								zqCommandLineInOptionField.setText(prop.getProperty("IN", ""));
								zqCommandLineOutOptionField.setText(prop.getProperty("OUT", ""));
								zqOptionFileDescription.setText(descr);
								zqExtOptionField.setEnabled(false);
								zqMainOptionField.setEnabled(false);
								zqCommandLineInOptionField.setEnabled(false);
								zqCommandLineOutOptionField.setEnabled(false);
								zqOptionFileDescription.setEnabled(false);
								return;
							} else {// ファイルでない
								zqExtOptionField.setEnabled(true);
								zqMainOptionField.setEnabled(true);
								zqCommandLineInOptionField.setEnabled(true);
								zqCommandLineOutOptionField.setEnabled(true);
								zqOptionFileDescription.setEnabled(true);
							}
						}
					});
		}
		return zqFFmpegOptionComboBox;
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
					.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							FFmpegOptionModel.reload();
							if (!FFmpegOptionModel.isFile()) {// ファイルでない
								Properties prop = new Properties();
								try {
									prop.loadFromXML(new FileInputStream(ConvertingSetting.PROP_FILE));
								} catch (IOException ex) {
									sendtext("設定1のオプションファイルが更新できません。");
									ex.printStackTrace();
									return;
								}
								ExtOptionField.setText(prop.getProperty("CMD_EXT", ""));
								MainOptionField.setText(prop.getProperty("CMD_MAIN", ""));
								CommandLineInOptionField.setText(prop.getProperty("CMD_IN", ""));
								CommandLineOutOptionField.setText(prop.getProperty("CMD_OUT", ""));
							}
						}
					});
		}
		return FFmpegOptionReloadButton;
	}
	/**
	 * This method initializes WideFFmpegOptionReloadButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getWideFFmpegOptionReloadButton() {
		if (WideFFmpegOptionReloadButton == null) {
			WideFFmpegOptionReloadButton = new JButton();
			WideFFmpegOptionReloadButton.setText("更新");
			WideFFmpegOptionReloadButton.setForeground(Color.blue);
			WideFFmpegOptionReloadButton
					.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							WideFFmpegOptionModel.reload();
							if (!WideFFmpegOptionModel.isFile()) {// ファイルでない
								Properties prop = new Properties();
								try {
									prop.loadFromXML(new FileInputStream(
										ConvertingSetting.PROP_FILE));
								} catch (IOException ex) {
									sendtext("設定2のオプションファイルが更新できません。");
									ex.printStackTrace();
									return;
								}
								wideExtOptionField.setText(prop.getProperty("WideCMD_EXT", ""));
								wideMainOptionField.setText(prop.getProperty("WideCMD_MAIN", ""));
								wideCommandLineInOptionField.setText(prop.getProperty("WideCMD_IN", ""));
								wideCommandLineOutOptionField.setText(prop.getProperty("WideCMD_OUT", ""));
							}
						}
					});
		}
		return WideFFmpegOptionReloadButton;
	}

	/**
	 * This method initializes WideFFmpegOptionReloadButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getZqFFmpegOptionReloadButton() {
		if (zqFFmpegOptionReloadButton == null) {
			zqFFmpegOptionReloadButton = new JButton();
			zqFFmpegOptionReloadButton.setText("更新");
			zqFFmpegOptionReloadButton.setForeground(Color.blue);
			zqFFmpegOptionReloadButton
					.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							zqFFmpegOptionModel.reload();
							if (!zqFFmpegOptionModel.isFile()) {// ファイルでない
								Properties prop = new Properties();
								String optionXml = null;
								String descr = "";
								try {
									optionXml = ConvertingSetting.PROP_FILE;
									prop.loadFromXML(new FileInputStream(optionXml));
									descr = Path.readAllText(optionXml, "UTF-8");
									if(descr.contains("<!--"))
										descr = descr.replaceAll("\\s+"," ").replaceAll(".*<!--", "").replaceAll("-->.*", "");
									else
										descr = "";
								} catch (IOException ex) {
									sendtext("Qwatchのオプションファイルが更新できません。");
									ex.printStackTrace();
									return;
								}
								zqExtOptionField.setText(prop.getProperty("QCMD_EXT", ""));
								zqMainOptionField.setText(prop.getProperty("QCMD_MAIN", ""));
								zqCommandLineInOptionField.setText(prop.getProperty("QCMD_IN", ""));
								zqCommandLineOutOptionField.setText(prop.getProperty("QCMD_OUT", ""));
								zqOptionFileDescription.setText(descr);
							}
						}
					});
		}
		return zqFFmpegOptionReloadButton;
	}

	/**
	 * This method initializes FFmpegOptionComboBoxPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getFFmpegOptionComboBoxPanel() {
		if (FFmpegOptionComboBoxPanel == null) {
			GridBagConstraints grid_x_y_47 = new GridBagConstraints();
			grid_x_y_47.fill = GridBagConstraints.NONE;
			grid_x_y_47.insets = INSETS_0_0_5_5;
			GridBagConstraints grid_x_y_46 = new GridBagConstraints();
			grid_x_y_46.fill = GridBagConstraints.HORIZONTAL;
			grid_x_y_46.gridwidth = 3;
			grid_x_y_46.weightx = 1.0;
			grid_x_y_46.insets = INSETS_0_5_5_5;
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
	 * This method initializes FFmpegOptionComboBoxPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getWideFFmpegOptionComboBoxPanel() {
		if (WideFFmpegOptionComboBoxPanel == null) {
			GridBagConstraints grid_x_y_1_ = new GridBagConstraints();
			grid_x_y_1_.fill = GridBagConstraints.NONE;
			grid_x_y_1_.insets = INSETS_0_0_5_5;
			GridBagConstraints grid_x_y_2_ = new GridBagConstraints();
			grid_x_y_2_.fill = GridBagConstraints.HORIZONTAL;
			grid_x_y_2_.gridwidth = 3;
			grid_x_y_2_.weightx = 1.0;
			grid_x_y_2_.insets = INSETS_0_5_5_5;
			WideFFmpegOptionComboBoxPanel = new JPanel();
			WideFFmpegOptionComboBoxPanel.setLayout(new GridBagLayout());
			WideFFmpegOptionComboBoxPanel.add(getWideFFmpegOptionComboBox(),
					grid_x_y_2_);
			WideFFmpegOptionComboBoxPanel.add(getWideFFmpegOptionReloadButton(),
					grid_x_y_1_);
		}
		return WideFFmpegOptionComboBoxPanel;
	}

	/**
	 * This method initializes FFmpegOptionComboBoxPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getZqFFmpegOptionComboBoxPanel() {
		if (zqFFmpegOptionComboBoxPanel == null) {
			GridBagConstraints grid_x_y_1_ = new GridBagConstraints();
			grid_x_y_1_.fill = GridBagConstraints.NONE;
			grid_x_y_1_.insets = INSETS_0_0_5_5;
			GridBagConstraints grid_x_y_2_ = new GridBagConstraints();
			grid_x_y_2_.fill = GridBagConstraints.HORIZONTAL;
			grid_x_y_2_.gridwidth = 3;
			grid_x_y_2_.weightx = 1.0;
			grid_x_y_2_.insets = INSETS_0_5_5_5;
			zqFFmpegOptionComboBoxPanel = new JPanel();
			zqFFmpegOptionComboBoxPanel.setLayout(new GridBagLayout());
			zqFFmpegOptionComboBoxPanel.add(getZqFFmpegOptionComboBox(),
					grid_x_y_2_);
			zqFFmpegOptionComboBoxPanel.add(getZqFFmpegOptionReloadButton(),
					grid_x_y_1_);
		}
		return zqFFmpegOptionComboBoxPanel;
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JComboBox getShadowComboBox() {
		if (ShadowComboBox == null) {
			ShadowComboBox = new JComboBox(ConvertingSetting.ShadowKindArray);
		}
		return ShadowComboBox;
	}

	private File getFile(String path){
		if(path==null)
			return new File("");
		if(path.startsWith(".")){
			return new File(TOP_DIR,path);
		}
		return new File(path);
	}

	private int openFolder(File folder){
		if(folder==null || !folder.isDirectory())
			return -1;
		ArrayList<String> cmd = new ArrayList<String>();
		cmd.add("explorer.exe");
		cmd.add(folder.getAbsolutePath());
		return cmd_prompt(cmd);
	}

	private int openFileParent(File file){
		if(file==null)
			return -1;
		return openFolder(file.getParentFile());
	}
	public int cmd_prompt(ArrayList<String> args){
		ArrayList<String> cmd = new ArrayList<String>();
		ProcessBuilder pb = null;
		Process process = null;
		int ret = -1;
		try {
			for(String arg: args){
				if((arg.contains("　") && !arg.contains(" "))
					&& !arg.startsWith("\"")){
					arg = "\"" + arg + "\"";
				}
				arg = arg.replaceAll("([\\(\\)\\^&;, 　])","^$1");
				cmd.add(arg);
			}
			pb = new ProcessBuilder(cmd);
			pb.redirectErrorStream(true);
			process = pb.start();
			process.waitFor();
			ret = process.exitValue();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if(process!=null){
					process.destroy();
					process.getInputStream().close();
				}
			} catch(Exception ex){
				//ex.printStackTrace();
			}
		}
		return ret;
	}

	String getNotice() {
		return notice;
	}

	void setNotice(String string) {
		notice = string;
	}

}

class MainFrame_ShowSavingVideoFolderDialogButton_actionAdapter implements
		ActionListener {
	private MainFrame adaptee;

	MainFrame_ShowSavingVideoFolderDialogButton_actionAdapter(MainFrame adaptee) {
		this.adaptee = adaptee;
	}

	@Override
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

	@Override
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

	@Override
	public void actionPerformed(ActionEvent e) {
		adaptee.ShowSavingConvertedVideoFolderDialogButton_actionPerformed(e);
	}
}

class MainFrame_this_windowAdapter extends WindowAdapter {
	private MainFrame adaptee;

	MainFrame_this_windowAdapter(MainFrame adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void windowClosing(WindowEvent e) {
		adaptee.this_windowClosing(e);
	}
}

class MainFrame_SettingFontPathButton_actionAdapter implements ActionListener {
	private MainFrame adaptee;

	MainFrame_SettingFontPathButton_actionAdapter(MainFrame adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		adaptee.SettingFontPathButton_actionPerformed(e);
	}
}

class MainFrame_SettingVhookPathButton_actionAdapter implements ActionListener {
	private MainFrame adaptee;

	MainFrame_SettingVhookPathButton_actionAdapter(MainFrame adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		adaptee.SettingVhookPathButton_actionPerformed(e);
	}
}

class MainFrame_SettingFFmpegPathButton_actionAdapter implements ActionListener {
	private MainFrame adaptee;

	MainFrame_SettingFFmpegPathButton_actionAdapter(MainFrame adaptee) {
		this.adaptee = adaptee;
	}

	@Override
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

	@Override
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

	@Override
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

	@Override
	public void actionPerformed(ActionEvent e) {
		adaptee.ShowSavingVideoDialogButton_actionPerformed(e);
	}
}

class MainFrame_DoButton_actionAdapter implements ActionListener {
	private MainFrame adaptee;

	MainFrame_DoButton_actionAdapter(MainFrame adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		adaptee.DoButton_actionPerformed(e);
	}
}

class MainFrame_jMenuFileExit_ActionAdapter implements ActionListener {
	MainFrame adaptee;

	MainFrame_jMenuFileExit_ActionAdapter(MainFrame adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		adaptee.jMenuFileExit_actionPerformed(actionEvent);
	}
}

class MainFrame_jMenuHelpAbout_ActionAdapter implements ActionListener {
	MainFrame adaptee;

	MainFrame_jMenuHelpAbout_ActionAdapter(MainFrame adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		adaptee.jMenuHelpAbout_actionPerformed(actionEvent);
	}
}

class MainFrame_LoadNGConfig implements ActionListener {
	MainFrame mainFrame;

	MainFrame_LoadNGConfig(MainFrame frame){
		mainFrame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JLabel watch = mainFrame.elapsedTimeBar;
		mainFrame.sendtext("ニコニコ動画のNG設定保存");
		Loader loader = new Loader(mainFrame.getSetting(),
			new JLabel[]{mainFrame.statusBar, new JLabel(), watch, new JLabel()},
			Logger.MainLog);
		Path file = new Path("configNG.xml");
		String url = "http://ext.nicovideo.jp/api/configurengclient?mode=get";
		if (loader.load(url, file)){
			mainFrame.sendtext("ニコニコ動画のNG設定を保存しました：" + file.getRelativePath());
		}
	}
}

class MainFrame_noticePop implements ActionListener {
	MainFrame adaptee;
	static boolean showed = false;

	public MainFrame_noticePop(MainFrame frame) {
		adaptee = frame;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		JCheckBox src = (JCheckBox)e.getSource();
		String notice = adaptee.getNotice();
		if(notice.contains("showed")){
			showed = true;
		}
		int rc = 0;
		if(src.isSelected() && !showed){
			String[] opt = {"YES(以後注意しない)","YES","NO"};
			rc = JOptionPane.showOptionDialog(
				adaptee,
				 "追加モードは指定ファイルにコメントを追加保存します。\n"
				+"日時付加ありで追加モードにすると変換時にフォルダ合成しません。\n"
				+"下の「変換後にコメントファイルを削除する」にチェックを入れると\n"
				+"変換実行の成功後、指定したファイルの追加されたコメントを含む\n"
				+"全てのコメントが削除されます。\n\n"
				+"追加モードにしますか？",
				"注意 追加モード",
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.WARNING_MESSAGE,
				null, opt, 0
			);
			if (rc==0){
				adaptee.setNotice("showed,");
				showed = true;
			}else if (rc!=1){
				src.setSelected(false);
			};
		}
	}
}

class MainFrame_jMenuAfDialog implements ActionListener {
	MainFrame mainFrame;

	public MainFrame_jMenuAfDialog(MainFrame frame) {
		mainFrame = frame;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		AprilFool_Dioalog dialog = new AprilFool_Dioalog(mainFrame);
		dialog.init();
		dialog.setVisible(true);
	}

}

class AprilFool_Dioalog extends JDialog {

	private static final long serialVersionUID = 1L;
	JPanel panel1;
	JPanel panel2;
	JPanel panel;
	JRadioButton buttonOff;
	JRadioButton button2008;
	JRadioButton button2009;
	JRadioButton button2010;
	ButtonGroup group;
	JButton okButton;
	JButton cancelButton;
	public static String APRIL_OPT = "-April=";
	private MainFrame parent;
	private String extra;
	private String year = "";
	private String aprilopt = "";

	public AprilFool_Dioalog(MainFrame frame){
		super(frame);
		parent = frame;
	}

	public AprilFool_Dioalog(){
		this(null);
	}

	public void init(){
		setTitle("エイプリルフール機能の設定");
		try {
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		panel1 = new JPanel(new GridLayout(4, 1));
		buttonOff = new JRadioButton("オフ　通常の設定");
		button2008 = new JRadioButton("ニコニコ動画2.0(笑) 2008エイプリルフール");
		button2009 = new JRadioButton("ニコニコ動画(βββ)　2009年エイプリルフール");
		button2010 = new JRadioButton("ニコニコ動画黒字化　 2010エイプリルフール");
		panel1.add(buttonOff);
		panel1.add(button2008);
		panel1.add(button2009);
		panel1.add(button2010);
		group = new ButtonGroup();
		group.add(buttonOff);
		group.add(button2008);
		group.add(button2009);
		group.add(button2010);
		extra = parent.extraModeField.getText();
		showSelectedYear();
		panel2 = new JPanel(new GridLayout(1, 2, 5, 5));
		okButton = new JButton("設定");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setSelectedYear();
				dispose();
			}
		});
		cancelButton = new JButton("取り消し");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		panel2.add(okButton);
		panel2.add(cancelButton);
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(panel1, BorderLayout.NORTH);
		panel.add(panel2, BorderLayout.SOUTH);
		add(panel);
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);
	}

	private void showSelectedYear(){
		if(extra.contains(APRIL_OPT)){
			int index = extra.indexOf(APRIL_OPT) + APRIL_OPT.length();
			int index2 = (extra + " ").indexOf(" ", index);
			year = extra.substring(index, index2);
			aprilopt = APRIL_OPT + year;
			if(year.equals("2008")){
				group.setSelected(button2008.getModel(), true);
			}else if(year.equals("2009")){
				group.setSelected(button2009.getModel(), true);
			}else if(year.equals("2010")){
				group.setSelected(button2010.getModel(), true);
			}else{
				group.setSelected(buttonOff.getModel(), true);
			}
		}
	}

	private String getSelectedYear(){
		if(buttonOff.isSelected())
			return "";
		if(button2008.isSelected())
			return "2008";
		if(button2009.isSelected())
			return "2009";
		if(button2010.isSelected())
			return "2010";
		return null;
	}

	private void setSelectedYear(){
		//設定
		//元の設定値を削除
		extra = extra.replace(aprilopt, "").trim();
		String year = getSelectedYear();
		//エイプリルフール設定
		if(year!=null && !year.isEmpty()){
			parent.extraModeField.setText((extra + " " + APRIL_OPT + year));
		}else{
			parent.extraModeField.setText(extra);
		}
	}

	public void actionPerformed(ActionEvent e){
		dispose();
	}
}


/*
class MainFrame_LoadViewHistory implements ActionListener {
	MainFrame mainFrame;

	MainFrame_LoadViewHistory(MainFrame adaptee){
		mainFrame = adaptee;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JLabel status = mainFrame.statusBar;
		JLabel watch = mainFrame.elapsedTimeBar;
		status.setText("視聴履歴のロード");
		Loader loader = new Loader(mainFrame.getSetting(), status, watch);
		Path file = new Path("myhistory.html");
		String url = "http://www.nicovideo.jp/my/history";
		if (loader.load(url, file)){
			status.setText("視聴履歴をロードしました：" + file.getRelativePath());
		}
	}
}
*/

