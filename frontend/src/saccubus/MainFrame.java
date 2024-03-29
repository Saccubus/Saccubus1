package saccubus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
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
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicArrowButton;

import psi.lib.swing.PopupRightClick;
import saccubus.net.BrowserInfo;
import saccubus.net.Gate;
import saccubus.net.Loader;
import saccubus.net.NicoMap;
import saccubus.net.Path;
import saccubus.util.FileDropTarget;
import saccubus.util.Logger;
import saccubus.util.SelfTerminate;

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
	static final Border CREATE_ETCHED_BORDER = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

	/**
	 *
	 */
	private static final long serialVersionUID = 2564486741331062989L;

	public static final Image WinIcon = Toolkit.getDefaultToolkit()
			.createImage(saccubus.MainFrame.class.getResource("icon32.png"));

	private final static File TOP_DIR= new File(".");
	private final static File PROPERTY_SET1_XML = new File(TOP_DIR,"Saccubus_Set1.xml");
	private final static File PROPERTY_SET2_XML = new File(TOP_DIR,"Saccubus_Set2.xml");
	JPanel contentPane;
	BorderLayout borderLayout1 = new BorderLayout();
	JMenuBar jMenuBar1 = new JMenuBar();
	JMenu jMenuFile = new JMenu();
	JMenuItem jMenuOpen = new JMenuItem();
	JMenuItem jMenuAdd = new JMenuItem();
	JMenuItem jMenuSave = new JMenuItem();
	JMenuItem jMenuSaveAs = new JMenuItem();
	JMenuItem jMenuSaveSet1 = new JMenuItem();
	JMenuItem jMenuSaveSet2 = new JMenuItem();
	JMenuItem jMenuOpenSet1 = new JMenuItem();
	JMenuItem jMenuOpenSet2 = new JMenuItem();
	JMenuItem jMenuReset = new JMenuItem();
	JMenuItem jMenuFileExit = new JMenuItem();
	JMenuItem jMenuInit = new JMenuItem();
	JMenu jMenuHelp = new JMenu();
	JMenuItem jMenuHelpAbout = new JMenuItem();
	JMenuItem jMenuHelpUpdate = new JMenuItem();
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
	JMenuItem jMenuHelpEncoders = new JMenuItem();
	JMenuItem jMenuHelpPixformats = new JMenuItem();
	JMenuItem jMenuHelpHwaccels = new JMenuItem();
	JMenu jMenuDetail = new JMenu();
	JMenuItem jMenuNGConfig = new JMenuItem();
	JMenuItem jMenuAprilFool = new JMenuItem();
	JMenu jMenuTips = new JMenu();
	JMenuItem jTips1 = new JMenuItem();
	JMenuItem jTips2 = new JMenuItem();
	JMenuItem jTips3 = new JMenuItem();
	JMenu jRestTimeMenu;
	JCheckBoxMenuItem jMenuCheckSize = new JCheckBoxMenuItem();
	JCheckBoxMenuItem jMenuClearErrorAtEnd = new JCheckBoxMenuItem(); 
	JMenuItem jMenuSelfTerminate = new JMenuItem();
	JCheckBoxMenuItem jMenuEnableUnicode = new JCheckBoxMenuItem();
	JMenu jMenuAction = new JMenu();
	JMenuItem jMenuLogview = new JCheckBoxMenuItem();
	JMenuItem jMenuLatestCheck = new JMenuItem();
	JMenuItem jMenuPanelHideAll = new JMenuItem();
	JMenuItem jMenuPanelShowAll = new JMenuItem();
	JMenuItem jMenuPanelInit = new JMenuItem();
	JMenuItem jMenuPanelUpdate = new JMenuItem();
	JMenuItem jMenuFolderOpen = new JMenuItem();
	JMenuItem jMenuDebug = new JCheckBoxMenuItem();
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
	JButton historyReplaceButton = new BasicArrowButton(SwingConstants.NORTH);
	JButton DoButton = new JButton();
	public static final String DoButtonDefString = "変換";
	public static final String DoButtonStopString = "停止";
	public static final String DoButtonWaitString = "待機";
	public static final String DoButtonDoneString = "終了";
	GridBagLayout gridBagLayout2 = new GridBagLayout();
	JPanel loginCheckPanel = new JPanel();
	JButton loginCheckButton = new JButton();
	JLabel loginStatusLabel = new JLabel();
	JLabel nicoLabel = new JLabel();
	final JCheckBox html5CheckBox = new JCheckBox();
	int html5Player = 0;
	String[] html5PlayerArray = {"flash","html5"};
	private String[] shadowDefaultSetting = new String[2];

	JPanel UserInfoPanel = new JPanel();
	GridBagLayout gridBagLayout3 = new GridBagLayout();
	JLabel MailAddrLabel = new JLabel();
	JTextField MailAddrField = new JTextField();
	JLabel PasswordLabel = new JLabel();
	JPasswordField PasswordField = new JPasswordField();
	JPanel CommentSaveInfoPanel = new JPanel();
//	JPanel OwnerCommentInfoPanel = new JPanel();
	JPanelHideable OldCommentModePanel;
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
	JPanelHideable CheckFFmpegFunctionPanel;
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
	JPanelHideable OptionalThreadInfoPanel;
	JLabel OptionalthreadLabel = new JLabel();
	JCheckBox OptionalTranslucentCheckBox = new JCheckBox();
	JPanelHideable experimentPanel;
	JCheckBox fontHeightFixCheckBox = new JCheckBox();
	JTextField fontHeightRatioTextField = new JTextField();
	JCheckBox disableOriginalResizeCheckBox = new JCheckBox();
	@SuppressWarnings({ "rawtypes", "unchecked" })
	JComboBox commentModeComboBox = new JComboBox(commentModeArray);
	JCheckBox commentSpeedCheckBox = new JCheckBox();
	JTextField commentSpeedTextField = new JTextField();
	JCheckBox commentLineFeedCheckBox = new JCheckBox();
	JTextField commentLineFeedTextField = new JTextField();
	JCheckBox enableCA_CheckBox = new JCheckBox();
	JCheckBox enableHtml5CommentCheckBox = new JCheckBox();
	JCheckBox enableCommentJsonCheckBox = new JCheckBox();
	JCheckBox enableCommentXmlCheckBox = new JCheckBox();
	JCheckBox enableAutoHtml5CheckBox = new JCheckBox();
	JCheckBox disableEcoCheckBox = new JCheckBox();
	JCheckBox preferSmileCheckBox = new JCheckBox();
	JCheckBox forceDmcDlCheckBox = new JCheckBox();
	JCheckBox enableRangeCheckBox = new JCheckBox();
	JCheckBox enableSeqResumeCheckBox = new JCheckBox();
	JCheckBox inhibitSmallCheckBox = new JCheckBox();
	JCheckBox autoFlvToMp4CheckBox = new JCheckBox();
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
	JPanelHideable watchPageSavingInfoPanel;
	private JPanel watchPageSavingTabbedPanel = new JPanel();
	JCheckBox saveThumbUserCheckBox = new JCheckBox();
	JLabel userFolderLabel = new JLabel();
	JTextField userFolderTextField = new JTextField();
	JRadioButton saveThumbInfoExtTxtRadioButton = new JRadioButton();
	JRadioButton saveThumbInfoExtXmlRadioButton = new JRadioButton();
	ButtonGroup thumbInfoExtButtonGroup = new ButtonGroup();
	JPanelHideable fileNameInfoPanel;
	JCheckBox changeMp4ExtCheckBox = new JCheckBox();
	JCheckBox changeTitleIdCheckBox = new JCheckBox();
	JCheckBox saveThumbnailJpgCheckBox = new JCheckBox();
	JTextField opaqueRateTextField = new JTextField();
	private JButton showDownloadListButton = new JButton();
	private JLabel showDownloadListLabel = new JLabel();
	private JPanelHideable updateInfoPanel;
	private JLabel updateInfoLabel = new JLabel();
//	private JLabel updateInfoLabel1 = new JLabel();
//	private JLabel updateInfoLabel2 = new JLabel();
	private JCheckBox nmmNewEnableCheckBox = new JCheckBox();
	private JCheckBox fpsUpCheckBox = new JCheckBox();
	private JPanel fpsFixPanel;
	private JCheckBox fpsIntegralMultipleCheckBox = new JCheckBox();
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
	private JCheckBox liveCommentMinVposCheckBox = new JCheckBox();
	private JTextField liveCommentMinVposTextField = new JTextField();
	private JPanelHideable liveConvertInfoPanel;
	private JCheckBox liveOparationDurationChangeCheckBox = new JCheckBox();
	private JCheckBox premiumColorCheckBox = new JCheckBox();
	private JCheckBox appendCommentCheckBox = new JCheckBox();
	private JSpinner nThreadSpinner;
	private String notice;
	private HistoryDeque<String> requestHistory;
	private HistoryDeque<String> mylistHistory;
	private HistoryDeque<String> requestTemplete;
	private int historyID = 0;
	private Object[] histories = new Object[3];
	private AutoPlay autoPlay;
	private JPanelHideable extraDownloadInfoPanel;
	private String initialPanelHideMapping;
	private JCheckBox ngEnableMultilinesCheckBox = new JCheckBox();
	private static final String[] ngEnableMLToolchipTexts = {
			"オフ：複数行・運営コメ簡易変更後にはNG適用しない",
			"オン：複数行・運営コメ簡易変更後にもNG適用する",
	};
	final String DEBUG_NET_FLAG = "debug/";
	final String DEBUG_COMMENT_FLAG = "-debug";
	private String debug_port = "80";
	private JCheckBox resizeAdjustCheckBox = new JCheckBox();
	private JTextField resizeAdjustField = new JTextField();
	private JSlider resizeAdjustSlider;
	private JLabel zqSizeMinLabel = new JLabel();
	private JTextField zqSizeMinField = new JTextField();
	private JLabel zqSizeMaxLabel = new JLabel();
	private JTextField zqSizeMaxField = new JTextField();
	private JLabel zqFpsRangeLabel = new JLabel();
	private JTextField zqFpsRangeField = new JTextField();
	private JPanel zqLimitOptionPanel;
	private ConvertingSetting initialSetting;
//                                                   (up left down right)
	private static final Insets INSETS_0_5_0_0 = new Insets(0, 5, 0, 0);
	private static final Insets INSETS_0_5_0_5 = new Insets(0, 5, 0, 5);
	private static final Insets INSETS_0_5_5_5 = new Insets(0, 5, 5, 5);
	private static final Insets INSETS_0_0_5_5 = new Insets(0, 0, 5, 5);
	private static final Insets INSETS_0_5_5_0 = new Insets(0, 5, 5, 0);
	private static final Insets INSETS_0_0_0_5 = new Insets(0, 0, 0, 5);
//	private static final Insets INSETS_0_0_5_0 = new Insets(0, 0, 5, 0);
	private static final Insets INSETS_0_0_0_0 = new Insets(0, 0, 0, 0);
//	private static final Insets INSETS_5_5_5_5 = new Insets(5, 5, 5, 5);
	private static final Insets INSETS_0_25_0_5 = new Insets(0, 25, 0, 5);
	private static final Insets INSETS_0_50_0_5 = new Insets(0, 50, 0, 5);
	private static final String[] commentModeArray = {
		"0：コメント表示自動選択（2010年12月22日以後は新表示）",
		"1：新コメント表示（毎分最新100コメント＋旧表示）",
		"2：旧コメント表示（最大10分以上で1000コメント表示）",
	};

	public static final String THUMB_DEFALT_STRING = "<自動>";
	private static final String MY_MYLIST = "my/mylist";
	private static final String VIDEO_URL_PARSER = "https://www.nicovideo.jp/watch/";
	static final Logger log = Logger.MainLog;
	static MainFrame MasterMainFrame = null;
	static final String renameFileMacro = "ファイル名置換マクロ";
	static final String renameFileMacroDescription =
		"パス(フォルダ名およびファイル名)の中の以下の部分が置換されます。\n"
		+"%LOW%　→economy時　low_\n"
		+"%ID%　→動画ID(%LOW%がなくeconomy時　動画IDlow_)\n"
		+"%id%　→[動画ID](%LOW%がなくeconomy時　[動画ID]low_)\n"
		+"%TITLE%　→動画タイトル\n"
		+"%title%　→全角空白を半角空白に変えた動画タイトル\n"
		+"%CAT%　→もしあればカテゴリータグ\n"
		+"%TAGn%　n=1,2,...10　→(n+1)番めのタグ\n"
		;

	private String input_url;
	private String url;
	private ActivityControl activities;
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
	private JPanel errorUrlPanel;
	private JLabel errorUrlLabel;
	private JLabel ecoUrlLabel;
	private JButton errorResetUrlButton;
	private JPanel errorButtonPanel;
	private JButton errorListDeleteButton;
	private JButton errorListSaveButton;

	private JPanel playVideoPanel;
	private JLabel playVideoLabel;
	private JPanel playChoicedPanel;
	private JPanel playVideoButtonPanel;
	private JButton playVideoPlayButton;
	private JButton playVideoNextButton;
	private JButton playVideoBackButton;

	private JButton AllSaveButton;

	private SelfTerminate selfTerminateSetting;

	public MainFrame() {
		try {
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			jbInit();
			setPopup();
			setDropTarget();
			ConvertingSetting setting = null;
			if(new File(ConvertingSetting.PROP_FILE).exists()){
				setting = ConvertingSetting.loadSetting(null, null);
			} else {
				setting = ConvertingSetting.loadSetting(null,
					null, ConvertingSetting.INI_FILE, false);
			}
			this.setSetting(setting);
			initialSetting = setting;
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
		final MainFrame self = this;
		GridBagConstraints grid1_x1_y0_71 = new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, INSETS_0_0_0_0, 0, 6);
		grid1_x1_y0_71.fill = GridBagConstraints.BOTH;
		grid1_x1_y0_71.ipady = 0;
		GridBagConstraints grid10_x3_y1_70c = new GridBagConstraints();
		grid10_x3_y1_70c.fill = GridBagConstraints.NONE;
		grid10_x3_y1_70c.gridy = 1;
		grid10_x3_y1_70c.gridx = 3;
		grid10_x3_y1_70c.ipadx = 0;
		grid10_x3_y1_70c.ipady = 0;
		grid10_x3_y1_70c.weightx = 0.0;
		grid10_x3_y1_70c.insets = INSETS_0_0_0_0;
		GridBagConstraints grid10_x2_y1_70b = new GridBagConstraints();
		grid10_x2_y1_70b.fill = GridBagConstraints.NONE;
		grid10_x2_y1_70b.gridy = 1;
		grid10_x2_y1_70b.gridx = 2;
		grid10_x2_y1_70b.ipadx = 0;
		grid10_x2_y1_70b.ipady = 0;
		grid10_x2_y1_70b.weightx = 0.0;
		grid10_x2_y1_70b.insets = INSETS_0_0_0_0;
		GridBagConstraints grid10_x1_y1_70 = new GridBagConstraints();
		grid10_x1_y1_70.fill = GridBagConstraints.HORIZONTAL;
		grid10_x1_y1_70.gridy = 1;
		grid10_x1_y1_70.ipadx = 0;
		grid10_x1_y1_70.ipady = 0;
		grid10_x1_y1_70.weightx = 1.0;
		grid10_x1_y1_70.insets = INSETS_0_0_0_0;
		grid10_x1_y1_70.gridx = 1;
		grid10_x1_y1_70.gridwidth = 1;
		GridBagConstraints grid10_x0_y1_69 = new GridBagConstraints();
		grid10_x0_y1_69.gridx = 0;
		grid10_x0_y1_69.ipadx = 0;
		grid10_x0_y1_69.ipady = 0;
		grid10_x0_y1_69.insets = INSETS_0_5_0_5;
		grid10_x0_y1_69.anchor = GridBagConstraints.WEST;
		grid10_x0_y1_69.gridy = 1;
		GridBagConstraints grid10_x1_y0_68 = new GridBagConstraints();
		grid10_x1_y0_68.fill = GridBagConstraints.BOTH;
		grid10_x1_y0_68.gridx = 1;
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
		GridBagConstraints grid10_x3_y0_68D = new GridBagConstraints();
		grid10_x3_y0_68D.gridx = 4;
		grid10_x3_y0_68D.fill = GridBagConstraints.NONE;
		grid10_x3_y0_68D.gridy = 0;
		grid10_x3_y0_68D.weightx = 0.0;
		grid10_x3_y0_68D.insets = INSETS_0_0_0_0;
		GridBagConstraints grid10_x0_y0_67 = new GridBagConstraints();
		grid10_x0_y0_67.gridx = 0;
		grid10_x0_y0_67.ipadx = 0;
		grid10_x0_y0_67.ipady = 0;
		grid10_x0_y0_67.insets = INSETS_0_5_0_5;
		grid10_x0_y0_67.anchor = GridBagConstraints.WEST;
		grid10_x0_y0_67.gridy = 0;
		GridBagConstraints grid9_x1_y2_57 = new GridBagConstraints();
		grid9_x1_y2_57.fill = GridBagConstraints.BOTH;
		grid9_x1_y2_57.gridy = 2;
		grid9_x1_y2_57.weightx = 1.0;
		grid9_x1_y2_57.insets = INSETS_0_0_0_5;
		grid9_x1_y2_57.gridx = 1;
		GridBagConstraints grid9_x0_y2_56 = new GridBagConstraints();
		grid9_x0_y2_56.gridx = 0;
		grid9_x0_y2_56.insets = INSETS_0_5_0_5;
		grid9_x0_y2_56.anchor = GridBagConstraints.WEST;
		grid9_x0_y2_56.gridy = 2;
		ExtOptionLabel = new JLabel("出力の拡張子");
		GridBagConstraints grid9_x0_y1_55 = new GridBagConstraints();
		grid9_x0_y1_55.gridx = 0;
		grid9_x0_y1_55.fill = GridBagConstraints.HORIZONTAL;
		grid9_x0_y1_55.weightx = 1.0;
		grid9_x0_y1_55.gridwidth = 4;
		grid9_x0_y1_55.gridy = 1;
		GridBagConstraints grid9_x1_y5_53 = new GridBagConstraints(1, 3,
				2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, INSETS_0_0_0_5, 0, 0);
		grid9_x1_y5_53.gridy = 5;
		grid9_x1_y5_53.gridheight = 1;
		grid9_x1_y5_53.weightx = 1.0;
		grid9_x1_y5_53.gridwidth = 3;
		GridBagConstraints grid9_x1_y4_52 = new GridBagConstraints(2, 2,
				1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, INSETS_0_0_0_5, 0, 0);
		grid9_x1_y4_52.gridy = 4;
		grid9_x1_y4_52.gridwidth = 3;
		grid9_x1_y4_52.weightx = 1.0;
		grid9_x1_y4_52.gridx = 1;
		GridBagConstraints grid9_x1_y3_51 = new GridBagConstraints(2, 1,
				1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, INSETS_0_0_0_5, 0, 0);
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
				GridBagConstraints.BOTH, INSETS_0_5_0_5, 0, 0);
		grid9_x0_y4_49.gridy = 4;
		grid9_x0_y4_49.gridwidth = 1;
		GridBagConstraints grid9_x0_y3_48 = new GridBagConstraints(0, 1,
				1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, INSETS_0_5_0_5, 0, 0);
		grid9_x0_y3_48.gridy = 2;
		grid9_x0_y3_48.gridx = 2;
		GridBagConstraints grid5_x3_y8_45 = new GridBagConstraints(3, 5,
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, INSETS_0_0_0_5, 0, 0);
		grid5_x3_y8_45.gridy = 8;
		GridBagConstraints grid5_x3_y6_44 = new GridBagConstraints(3, 3,
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, INSETS_0_0_0_5, 0, 0);
		grid5_x3_y6_44.gridy = 6;
		GridBagConstraints grid5_x0_y8_43 = new GridBagConstraints(0, 8,
				2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, INSETS_0_50_0_5, 0, 0);
		GridBagConstraints grid5_x2_y8 = new GridBagConstraints(2, 8,
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, INSETS_0_0_0_0, 0, 0);
		GridBagConstraints grid5_x0_y9 = new GridBagConstraints(0, 9,
				4, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, INSETS_0_25_0_5, 0, 0);
		GridBagConstraints grid5_x0_y7_42 = new GridBagConstraints(0, 7,
				3, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, INSETS_0_25_0_5, 0, 0);
		GridBagConstraints grid5_x2_y7_42b = new GridBagConstraints(3, 7,
				1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, INSETS_0_5_0_0, 0, 0);
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
		GridBagConstraints grid4_x0_y9_26 = new GridBagConstraints();
		grid4_x0_y9_26.gridx = 0;
		grid4_x0_y9_26.gridwidth = 6;
		grid4_x0_y9_26.insets = INSETS_0_25_0_5;
		grid4_x0_y9_26.weightx = 1.0;
		grid4_x0_y9_26.fill = GridBagConstraints.HORIZONTAL;
		grid4_x0_y9_26.gridy = 9;
		GridBagConstraints grid4_x0_y8_25 = new GridBagConstraints();
		grid4_x0_y8_25.gridx = 0;
		grid4_x0_y8_25.gridwidth = 6;
		grid4_x0_y8_25.fill = GridBagConstraints.HORIZONTAL;
		grid4_x0_y8_25.insets = INSETS_0_25_0_5;
		grid4_x0_y8_25.weightx = 1.0;
		grid4_x0_y8_25.gridy = 8;
		GridBagConstraints grid4_x5_y14_24 = new GridBagConstraints(5, 14,
				1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, INSETS_0_0_0_5, 0, 0);
		grid4_x5_y14_24.gridy = 14;
		GridBagConstraints grid4_x5_y12_23 = new GridBagConstraints(5, 12,
				1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, INSETS_0_0_0_5, 0, 0);
		grid4_x5_y12_23.gridy = 12;
		GridBagConstraints grid4_x0_y14_21 = new GridBagConstraints(0, 14,
				4, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, INSETS_0_50_0_5, 0, 0);
		grid4_x0_y14_21.gridy = 14;
		GridBagConstraints grid4_x4_y14 = new GridBagConstraints(4, 14,
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, INSETS_0_0_0_0, 0, 0);
		GridBagConstraints grid4_x0_y13_20 = new GridBagConstraints(0, 13,
				6, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, INSETS_0_25_0_5, 0, 0);
		grid4_x0_y13_20.gridy = 13;
		GridBagConstraints grid4_x0_y12_19 = new GridBagConstraints(0, 12,
				4, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, INSETS_0_50_0_5, 0, 0);
		grid4_x0_y12_19.gridy = 12;
		GridBagConstraints grid4_x4_y12 = new GridBagConstraints(4, 12,
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, INSETS_0_0_0_0, 0, 0);
		GridBagConstraints grid4_x0_y11_18 = new GridBagConstraints(0, 7,
				6, 1, 1.0, 0.0, GridBagConstraints.CENTER,
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
		grid4_x1_y10_22.gridx = 1;
		grid4_x1_y10_22.gridy = 10;
		grid4_x1_y10_22.gridwidth = 5;
		grid4_x1_y10_22.weightx = 1.0;
		grid4_x1_y10_22.anchor = GridBagConstraints.CENTER;
		grid4_x1_y10_22.fill = GridBagConstraints.BOTH;
		grid4_x1_y10_22.insets = INSETS_0_0_0_5;
		GridBagConstraints grid4_x0_y6_16 = new GridBagConstraints(0, 5,
				4, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, INSETS_0_5_0_5, 0, 0);
		grid4_x0_y6_16.gridy = 6;
		grid4_x0_y6_16.gridwidth = 1;
		grid4_x0_y6_16.insets = INSETS_0_5_0_0;
		GridBagConstraints grid4_x1_y6 = new GridBagConstraints();
		grid4_x1_y6.gridx = 1;
		grid4_x1_y6.gridy = 6;
		grid4_x1_y6.anchor = GridBagConstraints.WEST;
		grid4_x1_y6.fill = GridBagConstraints.HORIZONTAL;
		grid4_x1_y6.insets = INSETS_0_5_0_0;
		GridBagConstraints grid4_x2_y6 = new GridBagConstraints();
		grid4_x2_y6.gridx = 2;
		grid4_x2_y6.gridy = 6;
		grid4_x2_y6.anchor = GridBagConstraints.WEST;
		grid4_x2_y6.fill = GridBagConstraints.HORIZONTAL;
		grid4_x2_y6.insets = INSETS_0_5_0_0;
		GridBagConstraints grid4_x3_y6_ = new GridBagConstraints();
		grid4_x3_y6_.gridx = 3;
		grid4_x3_y6_.gridy = 6;
		grid4_x3_y6_.gridwidth = 3;
		grid4_x3_y6_.anchor = GridBagConstraints.WEST;
		grid4_x3_y6_.fill = GridBagConstraints.HORIZONTAL;
		grid4_x3_y6_.insets = INSETS_0_5_0_5;
		GridBagConstraints grid4_x0_y7_86 = new GridBagConstraints();
		grid4_x0_y7_86.gridx = 0;
		grid4_x0_y7_86.gridy = 7;
		grid4_x0_y7_86.gridwidth = 5;
		grid4_x0_y7_86.anchor = GridBagConstraints.WEST;
		grid4_x0_y7_86.fill = GridBagConstraints.HORIZONTAL;
		grid4_x0_y7_86.insets = INSETS_0_25_0_5;
		GridBagConstraints grid11_x0_y0_75 = new GridBagConstraints();
		grid11_x0_y0_75.gridx = 0;
		grid11_x0_y0_75.gridy = 0;
		grid11_x0_y0_75.gridwidth = 2;
		grid11_x0_y0_75.weightx = 1.0;
		grid11_x0_y0_75.anchor = GridBagConstraints.CENTER;
		grid11_x0_y0_75.fill = GridBagConstraints.HORIZONTAL;
		grid11_x0_y0_75.insets = INSETS_0_5_0_5;
		GridBagConstraints grid11_x1_y1_76 = new GridBagConstraints();
		grid11_x1_y1_76.gridx = 1;
		grid11_x1_y1_76.gridy = 1;
		grid11_x1_y1_76.weightx = 1.0;
		grid11_x1_y1_76.anchor = GridBagConstraints.WEST;
		grid11_x1_y1_76.fill = GridBagConstraints.HORIZONTAL;
		grid11_x1_y1_76.insets = INSETS_0_0_0_5;
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
		grid12_x0_y2_95.insets = INSETS_0_5_0_5;
		grid12_x0_y2_95.fill = GridBagConstraints.HORIZONTAL;
		grid12_x0_y2_95.anchor = GridBagConstraints.NORTH;
		this.setIconImage(WinIcon);
		contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(borderLayout1);
		setSize(new Dimension(400, 625));
		/* setBounds(0, 50, 400, 620); */
		setTitle("さきゅばす " + saccubus.MainFrame_AboutBox.rev );
		this.addWindowListener(new MainFrame_this_windowAdapter());
		statusBar.setText(" ");
		elapsedTimeBar.setText(" ");
		elapsedTimeBar.setForeground(Color.blue);
		vhookInfoBar.setText(" ");
		vhookInfoBar.setForeground(Color.blue);
		infoBar.setText(" ");
		jMenuFile.setText("ファイル");
		jMenuFileExit.setText("終了");
		jMenuFileExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jMenuFileExit_actionPerformed(e);
			}
		});
		jMenuHelp.setText("ヘルプ");
		jMenuHelpAbout.setText("バージョン情報");
		jMenuHelpAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuHelpAbout_actionPerformed(e);
			}
		});
		jMenuHelpUpdate.setText("更新履歴");
		jMenuHelpUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showReadme_actionPerformed("更新履歴.txt", "UTF-8");
			}
		});
		jMenuHelpReadme.setText("　readme(オリジナル)表示");
		jMenuHelpReadme.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showReadme_actionPerformed("readme.txt", "MS932");
			}
		});
		jMenuHelpReadmeNew.setText("readmeNew(最新)表示");
		jMenuHelpReadmeNew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showReadme_actionPerformed("readmeNew.txt", "UTF-8");
			}
		});
		jMenuHelpReadmePlus.setText("　readme+表示");
		jMenuHelpReadmePlus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showReadme_actionPerformed("readme+.txt","MS932");
			}
		});
		jMenuHelpReadmeFirst.setText("　最初に必ず読んで　表示");
		jMenuHelpReadmeFirst.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showReadme_actionPerformed("最初に必ず読んで.txt","UTF-8");
			}
		});
		jMenuHelpErrorTable.setText("エラーコード表　表示");
		jMenuHelpErrorTable.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showReadme_actionPerformed("エラーコード.txt","MS932");
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
		jMenuHelpEncoders.setText("　FFmpegエンコーダ");
		jMenuHelpEncoders.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { FFhelp_actionPerformed("-encoders"); }
		});
		jMenuHelpPixformats.setText("　FFmpegピクセルフォーマット");
		jMenuHelpPixformats.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { FFhelp_actionPerformed("-pix_fmts"); }
		});
		jMenuHelpHwaccels.setText("　FFmpegHW支援");
		jMenuHelpHwaccels.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { FFhelp_actionPerformed("-hwaccels"); }
		});
		jMenuAction.setText("アクション");
		jMenuLogview.setText("ログView On");
		jMenuLogview.setSelected(Logger.isViewVisible());
		jMenuLogview.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Logger.setViewVisislbe(true);
				jMenuLogview.setSelected(Logger.isViewVisible());
			}
		});
		jMenuLatestCheck.setText("最新バージョンチェック");
		jMenuLatestCheck.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String url = "https://github.com/Saccubus/Saccubus1.x/releases/latest";
					log.println("Access "+url);
					Loader loader = new Loader(
							self.getSetting(), new JLabel[]{statusBar,elapsedTimeBar,new JLabel()}, log, html5CheckBox.isSelected());
					NicoMap map = loader.loadHttpsUrl(url);
					String location = map.get("location");
					if(location==null)
						return;
					String latest;
					if(location.endsWith("/") && location.length()>=2){
						latest = location.substring(0,location.length()-1);
					}
					if(location.contains("/"))
						latest = location.substring(location.lastIndexOf("/")+1);
					else
						latest = "";
					String rev = MainFrame_AboutBox.rev;
					String buf;
					if(rev.equals(latest)){
						buf = "Rev."+rev+"は最新です";
						log.println(buf);
						JOptionPane.showMessageDialog(self, buf);
					}
					else {
						buf = "現在Rev."+rev+"です。最新はRev."+latest+"です";
						log.println(buf);
						if(latest.isEmpty()){
							JOptionPane.showMessageDialog(self, buf);
						}else{
							int rc = JOptionPane.showConfirmDialog(
								self,buf+"\nページを表示しますか?","選択",JOptionPane.YES_NO_OPTION);
							if(rc == 0){
								Desktop.getDesktop().browse(URI.create(url));
							}
						}
					}
				}catch(Exception e2){
					log.printStackTrace(e2);
				}
			}
		});
		jMenuPanelShowAll.setText("項目全表示");
		jMenuPanelShowAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JPanelHideable.showPanelAll();
			}
		});
		jMenuPanelHideAll.setText("項目最小表示");
		jMenuPanelHideAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JPanelHideable.hidePanelAll();
			}
		});
		jMenuPanelInit.setText("項目起動時に戻す");
		jMenuPanelInit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JPanelHideable.setHideMap(initialPanelHideMapping);
			}
		});
		jMenuPanelUpdate.setText("項目表示現在の状態で更新");
		jMenuPanelUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				initialPanelHideMapping = JPanelHideable.getHideMap();
			}
		});
		jMenuFolderOpen.setText("さきゅばすフォルダを開く");
		jMenuFolderOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openFolder(getFile("."));
			}
		});
		jMenuDetail.setText("詳細設定");
		jMenuNGConfig.setText("ニコニコ動画のNG設定保存");
		jMenuNGConfig.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendtext("ニコニコ動画のNG設定保存");
				Loader loader = new Loader(getSetting(),
					new JLabel[]{statusBar, elapsedTimeBar, new JLabel()},
					log,html5CheckBox.isSelected());
				Path file = new Path("configNG.xml");
				String url = "https://ext.nicovideo.jp/api/configurengclient?mode=get";
				if (loader.load(url, file)){
					sendtext("ニコニコ動画のNG設定を保存しました：" + file.getRelativePath());
				}
			}
		});
		jMenuAprilFool.setText("AprilFool再現");
		jMenuAprilFool.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AprilFool_Dioalog dialog = new AprilFool_Dioalog(MainFrame.getMaster());
				dialog.init();
				dialog.setVisible(true);
			}
		});
		jMenuTips.setText("設定TIPS");
		jTips1 = new JMenuItem();
		jTips1.setText("弾幕化しないモード");
		jTips1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainTabbedPane.setSelectedComponent(ConvertingSettingPanel);
				commentOffCheckbox.setOpaque(true);
				commentOffCheckbox.setBackground(Color.yellow);
				boolean op = commentOffField.isOpaque();
				commentOffField.setOpaque(true);
				Color bc = commentOffField.getBackground();
				commentOffField.setBackground(Color.yellow);
				int ret = JOptionPane.showConfirmDialog(MainFrame.getMaster(),
					" [変換設定][コメントオフエリア]をオンにして\n指定欄に何も入れない\n"
					+"\n設定しますか？(Noはリセット)");
				//設定
				if(ret==JOptionPane.YES_OPTION){
					commentOffCheckbox.setSelected(true);
					commentOffField.setText("");
				}else if(ret==JOptionPane.NO_OPTION){
					commentOffCheckbox.setSelected(false);
				}
				commentOffCheckbox.setOpaque(false);
				commentOffField.setOpaque(op);
				commentOffField.setBackground(bc);
				MainFrame.reflesh();
			}
		});
		jMenuTips.add(jTips1);
		jTips2 = new JMenuItem();
		jTips2.setText("右端で非表示回避、動画幅で衝突判定");
		jTips2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainTabbedPane.setSelectedComponent(ConvertingSettingPanel);
				boolean visible = VhookSettingPanel.isContentVisible();
				VhookSettingPanel.showPanel();
				commentSpeedCheckBox.setOpaque(true);
				commentSpeedCheckBox.setBackground(Color.yellow);
				boolean op = commentSpeedTextField.isOpaque();
				commentSpeedTextField.setOpaque(true);
				Color bc = commentSpeedTextField.getBackground();
				commentSpeedTextField.setBackground(Color.yellow);
				int ret = JOptionPane.showConfirmDialog(MainFrame.getMaster(),
					" [変換設定][コメント速度]をオンにして\n指定欄に何も入れない\n"
					+"\n設定しますか？(Noはリセット)");
				if(ret==JOptionPane.YES_OPTION){
					//設定
					commentSpeedCheckBox.setSelected(true);
					commentSpeedTextField.setText("");
				}else if(ret==JOptionPane.NO_OPTION){
					commentSpeedCheckBox.setSelected(false);
				}
				if(!visible)
					VhookSettingPanel.hidePanel();
				commentSpeedCheckBox.setOpaque(false);
				commentSpeedTextField.setOpaque(op);
				commentSpeedTextField.setBackground(bc);
				MainFrame.reflesh();
			}
		});
		jMenuTips.add(jTips2);
		jTips3 = new JMenuItem();
		jTips3.setText("途中でコメントが消えないモードにする");
		jTips3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainTabbedPane.setSelectedComponent(ConvertingSettingPanel);
				boolean visible = VhookSettingPanel.isContentVisible();
				VhookSettingPanel.showPanel();
				commentEraseTypeLabel.setOpaque(true);
				commentEraseTypeLabel.setBackground(Color.yellow);
				boolean op = commentEraseTypeComboBox.isOpaque();
				commentEraseTypeComboBox.setOpaque(true);
				Color bc = commentEraseTypeComboBox.getBackground();
				commentEraseTypeComboBox.setBackground(Color.yellow);
				int ret = JOptionPane.showConfirmDialog(MainFrame.getMaster(),
					"  [変換設定][表示数超過]を 1 にする\n"
					+"\n設定しますか？(Noはリセット)");
				if(ret==JOptionPane.YES_OPTION){
					//設定
					commentEraseTypeComboBox.setSelectedIndex(1);
				}else if(ret==JOptionPane.NO_OPTION){
					commentEraseTypeComboBox.setSelectedIndex(0);
				}
				if(!visible)
					VhookSettingPanel.hidePanel();
				commentEraseTypeLabel.setOpaque(false);
				commentEraseTypeComboBox.setOpaque(op);
				commentEraseTypeComboBox.setBackground(bc);
				MainFrame.reflesh();
			}
		});
		jMenuTips.add(jTips3);
		jMenuCheckSize.setText("読み込み済み動画のサイズチェックを行う");
		jMenuCheckSize.setToolTipText(
			"<html>サーバのファイルサイズ情報と一致しない場合再読込します。<BR>"
			+"動画差し替えの場合はローカル変換又はオフにして下さい。</html>");
		jMenuClearErrorAtEnd.setText("終了時エラーリスト保存&クリア");
		selfTerminateSetting = new SelfTerminate(log, this);
		jMenuSelfTerminate = selfTerminateSetting.initMenu(getTerminateTimeout());
		jRestTimeMenu = selfTerminateSetting.getRestTimeMenu();
		jMenuEnableUnicode.setText("Unicode表示を有効にする。(従来はshift-jis)");
		jMenuEnableUnicode.setForeground(Color.blue);
		jMenuEnableUnicode.setToolTipText("ファイルタイトルやログ表示でUnicodeのままにする。");
		jMenuOpen.setText("開く(Open)...");
		jMenuOpen.setForeground(Color.blue);
		jMenuOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JTextField propFileField = new JTextField("");
				if(showSaveDialog("設定ファイルのパス", propFileField, false, false)
						!= DIALOG_OK){
					sendtext("キャンセル");
					return;
				}
				String filename = propFileField.getText();
				if(filename!=null && !filename.isEmpty() && Path.isFile(filename)){
					sendtext("");
					setSetting(ConvertingSetting.loadSetting(null, null, filename));
				}
				else
					sendtext("設定ファイル読み込みエラー");
			}
		});
		jMenuAdd.setText("追加 (Add)...");
		jMenuAdd.setForeground(Color.blue);
		jMenuAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JTextField propFileField = new JTextField("");
				if(showSaveDialog("追加用設定ファイルのパス", propFileField, false, false)
						!= DIALOG_OK){
					sendtext("キャンセル");
					return;
				}
				String filename = propFileField.getText();
				if(filename!=null && !filename.isEmpty() && Path.isFile(filename))
					setSetting(ConvertingSetting.addSetting(getSetting(), filename));
				else
					sendtext("設定ファイル追加エラー");
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
				if(showSaveDialog("設定ファイルのパス", propFileField,	true, false)
						!= DIALOG_OK){
					sendtext("キャンセル");
					return;
				}
				String filename = propFileField.getText();
				if(filename!=null && !filename.isEmpty())
					ConvertingSetting.saveSetting(getSetting(), filename);
				else
					sendtext("設定ファイル保存エラー");
			}
		});
		jMenuOpenSet1.setText("設定1を開く");
		jMenuOpenSet1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File file = PROPERTY_SET1_XML;
				if(file.isFile()){
					sendtext("");
					setSetting(ConvertingSetting.loadSetting(null, null, file.getPath()));
				}
				else
					sendtext("設定1読み込みエラー");
			}
		});
		jMenuOpenSet2.setText("設定2を開く");
		jMenuOpenSet2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File file = PROPERTY_SET2_XML;
				if(file.isFile()){
					sendtext("");
					setSetting(ConvertingSetting.loadSetting(null, null, file.getPath()));
				}
				else
					sendtext("設定2読み込みエラー");
			}
		});
		jMenuSaveSet1.setText("設定1に現在の設定を保存");
		jMenuSaveSet1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendtext("");
				ConvertingSetting.saveSetting(getSetting(), PROPERTY_SET1_XML.getPath());
			}
		});
		jMenuSaveSet2.setText("設定2に現在の設定を保存");
		jMenuSaveSet2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendtext("");
				ConvertingSetting.saveSetting(getSetting(), PROPERTY_SET2_XML.getPath());
			}
		});
		jMenuDebug.setText("デバッグモード");
		jMenuDebug.setForeground(Color.blue);
		jMenuDebug.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean is_debug = jMenuDebug.isSelected();
				debugModeSet(is_debug);
			}
		});
		jMenuInit.setText("初期化 (Init)");
		jMenuInit.setForeground(Color.blue);
		jMenuInit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String mlad = null;
				String pswd = null;
				try {
					mlad = MailAddrField.getText();
					pswd = new String(PasswordField.getPassword());
				}catch(NullPointerException ex){
					//e.printStackTrace();
					mlad = null;
					pswd = null;
				}
				setSetting(ConvertingSetting.loadSetting(mlad, pswd, ConvertingSetting.INI_FILE, false));
			}
		});
		jMenuReset.setText("起動時の設定に戻す");
		jMenuReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setSetting(initialSetting);
			}
		});
		VideoInfoPanel.setLayout(gridBagLayout1);
		VideoID_TextField.setText("https://www.nicovideo.jp/watch/");
		requestHistory = new HistoryDeque<String>(" [リクエスト履歴終り] ", true);
		histories[0] = requestHistory;
		mylistHistory = new HistoryDeque<String>(" [マイリスト履歴終り] ", true);
		histories[1] = mylistHistory;
		requestTemplete = new HistoryDeque<String>("[テンプレート]", true);
		String[] REQ_TEMPLETE = {
			"watch/動画ID",
			"mylist/マイリストID",
			"user/ユーザID/video",
			"my/video",
			"my/mylist",
			"my/history",
			"search/キーワードサーチ?&オプション",
			"tag/タグサーチ?&オプション",
			"オプションsort &n:コメ時,&v:再生数,&f:投稿日,&m:マイリス数,&r:コメ数,&l:長さ,&h:人気",
			"オプションorder 英小文字:降順(order=d),英大文字:昇順(order=a)",
			"オプションpage 無し,数字:開始ページ数",
			"tag/VOCALOID?&f1 オプション実例",
		};
		for(String s: REQ_TEMPLETE)
			requestTemplete.offer(s);
		histories[2] = requestTemplete;
		DoButton.setText(DoButtonDefString);
		DoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DoButton_actionPerformed(e);
			}
		});
		SavingInfoTabPanel.setLayout(gridBagLayout2);
		loginCheckPanel.setLayout(new GridBagLayout());
		loginCheckButton.setText("ログインチェック");
		loginCheckButton.setForeground(Color.blue);
		loginCheckButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame_loginCheck(loginStatusLabel);
			}
		});
		GridBagConstraints loginGBC; 
		loginGBC = new GridBagConstraints(
			1, 0,
			1, 1,
			0.0, 0.0,
			GridBagConstraints.WEST,
			GridBagConstraints.NONE,
			INSETS_0_0_0_0,
			0, 0);
		loginCheckPanel.add(loginCheckButton,loginGBC);
		loginStatusLabel.setText("");
		loginGBC = new GridBagConstraints(
			2, 0,
			1, 1,
			1.0, 0.0,
			GridBagConstraints.WEST,
			GridBagConstraints.HORIZONTAL,
			INSETS_0_0_0_0,
			0, 0);
		loginCheckPanel.add(loginStatusLabel,loginGBC);
		nicoLabel.setText("_");
		nicoLabel.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String url = VideoID_TextField.getText();
				if(url==null || url.isEmpty()){
					url = requestHistory.getLast();
				}else{
					url = treatUrlHttp(url);
				}
				if(!url.startsWith("http"))
					url = "https://www.nicovideo.jp/watch/"+url;
				try {
					URI uri = URI.create(url);
					Desktop.getDesktop().browse(uri);
					nicoLabel.setText("o");
					nicoLabel.setForeground(Color.blue);
				} catch (IOException e1) {
					nicoLabel.setText("x");
					nicoLabel.setForeground(Color.red);
				} catch (RuntimeException e2){
					nicoLabel.setText("x");
					nicoLabel.setForeground(Color.red);
				}
			}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
		});
		html5CheckBox.setSelected(false);
		html5CheckBox.setText("html5");
		html5CheckBox.setToolTipText("html5プレーヤー使用を要求");
		html5CheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setHtml5AutoDefault();
			}
		});
		loginGBC = new GridBagConstraints(
			3, 0,
			1, 1,
			0.0, 0.0,
			GridBagConstraints.WEST,
			GridBagConstraints.NONE,
			INSETS_0_0_0_0,
			0, 0);
		loginCheckPanel.add(html5CheckBox,loginGBC);
		enableAutoHtml5CheckBox = new JCheckBox();
		enableAutoHtml5CheckBox.setText("自");
		enableAutoHtml5CheckBox.setForeground(Color.blue);
		enableAutoHtml5CheckBox.setToolTipText("自動html5切替:コメント,影をhtml5プレーヤー使用に合わせる");
		enableAutoHtml5CheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				enableAutoHtml5Action();
			}
		});
		loginGBC = new GridBagConstraints(
			4, 0,
			1, 1,
			0.0, 0.0,
			GridBagConstraints.WEST,
			GridBagConstraints.NONE,
			INSETS_0_0_0_0,
			0, 0);
		loginCheckPanel.add(enableAutoHtml5CheckBox,loginGBC);
		UserInfoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createEtchedBorder(), "ユーザ設定"));
		UserInfoPanel.setLayout(gridBagLayout3);
		MailAddrLabel.setText("メールアドレス");
		PasswordLabel.setText("パスワード");
		GridBagLayout gridBagLayout13 = new GridBagLayout();
		BrowserInfoPanel.setLayout(gridBagLayout13);
		BrowserInfoPanel.setBorder(BorderFactory.createTitledBorder(
				CREATE_ETCHED_BORDER,
				"ブラウザ情報共有設定", TitledBorder.LEADING, TitledBorder.TOP,
				getFont(), Color.blue));
		BrowserInfoLabel.setText("ログイン済みブラウザからセッションを取得する（ブラウザはログアウトされない）");
		BrowserInfoLabel.setForeground(Color.blue);
		BrowserInfoLabel.setToolTipText("メールアドレス、パスワードは入力不要（指定ブラウザがログインしていないとエラー）");
		GridBagConstraints grid13_x0_y0_96 = new GridBagConstraints();
		grid13_x0_y0_96.gridx = 0;
		grid13_x0_y0_96.gridy = 0;
		grid13_x0_y0_96.gridwidth = 3;
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
		grid13_x0_y1_97.gridwidth = 3;
		grid13_x0_y1_97.weightx = 1.0;
		grid13_x0_y1_97.anchor = GridBagConstraints.NORTH;
		grid13_x0_y1_97.fill = GridBagConstraints.HORIZONTAL;
		grid13_x0_y1_97.insets = INSETS_0_5_0_5;
		BrowserInfoPanel.add(BrowserIECheckBox, grid13_x0_y1_97);
		BrowserFFCheckBox.setText("Firefox (FF3/FF4～50)");
		BrowserFFCheckBox.setForeground(Color.blue);
		GridBagConstraints grid13_x0_y2_98 = new GridBagConstraints();
		grid13_x0_y2_98.gridx = 0;
		grid13_x0_y2_98.gridy = 2;
		grid13_x0_y2_98.gridwidth = 3;
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
		grid13_x0_y3_99.gridwidth = 3;
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
		grid13_x0_y4_100.gridwidth = 3;
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
		grid13_x0_y5_101.gridwidth = 3;
		grid13_x0_y5_101.weightx = 1.0;
		grid13_x0_y5_101.anchor = GridBagConstraints.NORTH;
		grid13_x0_y5_101.fill = GridBagConstraints.HORIZONTAL;
		grid13_x0_y5_101.insets = INSETS_0_5_0_5;
		BrowserInfoPanel.add(BrowserChromiumCheckBox, grid13_x0_y5_101);
		BrowserOtherCheckBox.setText("");
		BrowserOtherCheckBox.setForeground(Color.blue);
		GridBagConstraints grid13_x0_y6_102 = new GridBagConstraints();
		grid13_x0_y6_102.gridx = 0;
		grid13_x0_y6_102.gridy = 6;
		grid13_x0_y6_102.gridwidth = 1;
		grid13_x0_y6_102.weightx = 0.0;
		grid13_x0_y6_102.anchor = GridBagConstraints.NORTH;
		grid13_x0_y6_102.fill = GridBagConstraints.NONE;
		grid13_x0_y6_102.insets = INSETS_0_5_0_5;
		BrowserInfoPanel.add(BrowserOtherCheckBox, grid13_x0_y6_102);
		BrowserCookieField.setText("他のブラウザCookieのファイル/フォルダを指定");
		BrowserCookieField.setForeground(Color.blue);
		GridBagConstraints grid13_x0_y7_103 = new GridBagConstraints();
		grid13_x0_y7_103.gridx = 1;
		grid13_x0_y7_103.gridy = 6;
		grid13_x0_y7_103.gridwidth = 1;
		grid13_x0_y7_103.weightx = 1.0;
		grid13_x0_y7_103.anchor = GridBagConstraints.NORTH;
		grid13_x0_y7_103.fill = GridBagConstraints.HORIZONTAL;
		grid13_x0_y7_103.insets = INSETS_0_5_0_5;
		BrowserInfoPanel.add(BrowserCookieField, grid13_x0_y7_103);
		BrowserCookieDialogButton.setText("参照");
		BrowserCookieDialogButton.setForeground(Color.blue);
		BrowserCookieDialogButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(showSaveDialog("他のブラウザのCookieへのパス", BrowserCookieField)
							!= DIALOG_OK)
						sendtext("キャンセル");
				}
			});
		GridBagConstraints grid13_x1_y7_104 = new GridBagConstraints();
		grid13_x1_y7_104.gridx = 2;
		grid13_x1_y7_104.gridy = 6;
		grid13_x1_y7_104.gridwidth = 1;
		grid13_x1_y7_104.weightx = 0.0;
		grid13_x1_y7_104.anchor = GridBagConstraints.SOUTH;
		grid13_x1_y7_104.fill = GridBagConstraints.NONE;
		grid13_x1_y7_104.insets = INSETS_0_0_0_5;
		BrowserInfoPanel.add(BrowserCookieDialogButton, grid13_x1_y7_104);

		SavingVideoCheckBox.setText("動画保存");
		disableEcoCheckBox.setText("eco中止");
		disableEcoCheckBox.setForeground(Color.blue);
		ShowSavingVideoFileDialogButton.setText("参照");
		ShowSavingVideoFileDialogButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ShowSavingVideoDialogButton_actionPerformed(e);
			}
		});
		Video_SaveFolderRadioButton.setText("保存するフォルダを指定し、ファイル名は自動で決定する");
		openVideoSaveFolderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
				{openFolder(getFile(VideoSavedFolderField.getText()));}
		});
		ShowSavingVideoFolderDialogButton.setText("参照");
		ShowSavingVideoFolderDialogButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ShowSavingVideoFolderDialogButton_actionPerformed(e);
			}
		});
		Video_SaveFileRadioButton.setText("保存するファイル名を指定する(置換マクロ有り)->");
		Video_SaveFileRadioButton.setForeground(Color.blue);
		videoFileMacroLabel.setText("説明");
		videoFileMacroLabel.setForeground(Color.red);
		videoFileMacroLabel.addMouseListener(
				new ForcusedPopupBoard(this, renameFileMacro, renameFileMacroDescription));
		openVideoSaveFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
				{openFileParent(getFile(VideoSavedFileField.getText()));}
		});
		CommentSaveInfoPanel.setBorder(BorderFactory.createTitledBorder(
				CREATE_ETCHED_BORDER,
				"コメント保存設定"
			));
			//	,TitledBorder.LEADING, TitledBorder.TOP,
			//	new Font("MS UI Gothic", Font.PLAIN, 12), Color.black));
		CommentSaveInfoPanel.setLayout(gridBagLayout4);
		SavingCommentCheckBox.setText("コメントをダウンロード");
		appendCommentCheckBox.setText("コメント追加");
		appendCommentCheckBox.addActionListener(new MainFrame_noticePop(this));
		//enableCommentJsonCheckBox.setText("Json");
		//enableCommentJsonCheckBox.setForeground(Color.blue);
		//enableCommentJsonCheckBox.setToolTipText("コメントをJsonでアクセスする");
		//enableCommentJsonCheckBox.setEnabled(false);
		//enableCommentXmlCheckBox.setText("xml");
		//enableCommentXmlCheckBox.setForeground(Color.black);
		//enableCommentXmlCheckBox.setToolTipText("コメントをXmlでアクセスする(旧仕様)");
		//enableCommentXmlCheckBox.setEnabled(false);
		AddTimeStampToCommentCheckBox.setText("コメントファイル名に日時付加（フォルダ指定時）");
		AddTimeStampToCommentCheckBox.setForeground(Color.blue);
		AddTimeStampToCommentCheckBox.setToolTipText("過去ログにも現在のコメントにも日時が付く");
		ShowSavingCommentFileDialogButton.setText("参照");
		ShowSavingCommentFileDialogButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ShowSavingCommentDialogButton_actionPerformed(e);
			}
		});
		Comment_SaveFolderRadioButton.setText("保存するフォルダを指定し、ファイル名は自動で決定する");
		openCommentSaveFolderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
				{openFolder(getFile(CommentSavedFolderField.getText()));}
		});
		ShowSavingCommentFolderDialogButton.setText("参照");
		ShowSavingCommentFolderDialogButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ShowSavingCommentFolderDialogButton_actionPerformed(e);
			}
		});
		Comment_SaveFileRadioButton.setText("保存するファイル名を指定する");
		openCommentSaveFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
				{openFileParent(getFile(CommentSavedFileField.getText()));}
		});
		OldCommentModePanel = new JPanelHideable("OldCommentMode","投稿者コメント保存説明・コメント表示モード設定",Color.blue);
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
		ShowSavingConvertedVideoFileDialogButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ShowSavingConvertedVideoDialogButton_actionPerformed(e);
			}
		});
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
		OptionalThreadInfoPanel = new JPanelHideable("OptionalThreadInfo","オプショナルスレッド設定",Color.blue);
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
		PathSettingPanel.setLayout(new GridBagLayout());
		VhookPathSettingPanel = new JPanelHideable("VhookPath","拡張Vhookパスの設定",Color.black);
		FFmpegPathSettingPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "FFmpegの位置の設定"));
		FFmpegPathSettingPanel.setLayout(new GridBagLayout());
//		FFmpegPathLabel.setText("FFmpeg");
		CheckFFmpegVersionLabel.setText("FFmpegバージョン表示");
		CheckFFmpegVersionLabel.setForeground(Color.blue);
		CheckFFmpegVersionButton.setText("表");
		CheckFFmpegVersionButton.setToolTipText("指定されたFFmpegのバージョンを表示する");
		CheckFFmpegVersionButton.setForeground(Color.blue);
		CheckFFmpegVersionButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				FFVersionButton_actionPerformed(e);
			}
		});
		SettingFFmpegPathButton.setText("参照");
		SettingFFmpegPathButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SettingFFmpegPathButton_actionPerformed(e);
			}
		});
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
		SettingVhookPathButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SettingVhookPathButton_actionPerformed(e);
			}
		});
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
				CREATE_ETCHED_BORDER,
				"FFmpegの設定１ （拡張Vhook 従来を選択した時）"));
		FFmpegSettingPanel.setLayout(new GridBagLayout());
		WideFFmpegSettingPanel.setBorder(BorderFactory.createTitledBorder(
			CREATE_ETCHED_BORDER,
			"FFmpegの設定２ （拡張Vhook ワイドを選択した時）",
			TitledBorder.LEADING, TitledBorder.TOP, getFont(), Color.blue));
		WideFFmpegSettingPanel.setLayout(new GridBagLayout());
		additionalOptionPanel.setBorder(BorderFactory.createTitledBorder(
			CREATE_ETCHED_BORDER,
			"FFmpeg追加設定 (オプションを上書き/追加します)",
			TitledBorder.LEADING, TitledBorder.TOP, getFont(), Color.blue));
		SettingFontPathButton.setText("参照");
		SettingFontPathButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SettingFontPathButton_actionPerformed(e);
			}
		});
		InLabel.setText("入力オプション");
		OutLabel.setText("出力オプション");
		CommentNumLabel.setText("取得コメント数");
		MainOptionLabel.setText("メインオプション");
		VideoID_Label.setText("URL/ID");
		WayBackLabel.setText("過去ログ");
		dateUserFirstCheckBox.setText("");
		dateUserFirstCheckBox.setToolTipText("過去ログ日時を自動設定する");
		OpPanel.setLayout(gridBagLayout10);
		Conv_SaveFolderRadioButton.setText("保存するフォルダを指定し、ファイル名は自動で決定する");
		ShowSavingConvertedVideoFolderDialogButton.setText("参照");
		ShowSavingConvertedVideoFolderDialogButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ShowSavingConvertedVideoFolderDialogButton_actionPerformed(e);
			}
		});
		Conv_SaveFileRadioButton.setText("保存するファイル名を指定する(置換マクロ有り)->");
		Conv_SaveFileRadioButton.setForeground(Color.blue);
		convFileMacroLabel.setText("説明");
		convFileMacroLabel.setForeground(Color.red);
		convFileMacroLabel.addMouseListener(
			new ForcusedPopupBoard(this, renameFileMacro, renameFileMacroDescription));

		BasicInfoTabPanel.setLayout(gridBagLayout12);
		jMenuBar1.add(jMenuFile);
		jMenuFile.add(jMenuOpen);
		jMenuFile.add(jMenuAdd);
		jMenuFile.add(jMenuSave);
		jMenuFile.add(jMenuSaveAs);
		jMenuFile.add(jMenuReset);
		jMenuFile.add(jMenuOpenSet1);
		jMenuFile.add(jMenuSaveSet1);
		jMenuFile.add(jMenuOpenSet2);
		jMenuFile.add(jMenuSaveSet2);
		jMenuFile.add(jMenuInit);
		jMenuFile.add(jMenuDebug);
		jMenuFile.add(jMenuFileExit);
		jMenuBar1.add(jMenuDetail);
		jMenuDetail.add(jMenuNGConfig);
		jMenuDetail.add(jMenuAprilFool);
		jMenuDetail.add(jMenuTips);
		jMenuDetail.add(jMenuCheckSize);
		jMenuDetail.add(jMenuClearErrorAtEnd);
		jMenuDetail.add(jMenuSelfTerminate);
		jMenuDetail.add(jMenuEnableUnicode);
		jMenuBar1.add(jMenuAction);
		jMenuAction.add(jMenuLogview);
		jMenuAction.add(jMenuLatestCheck);
		jMenuAction.add(jMenuPanelShowAll);
		jMenuAction.add(jMenuPanelHideAll);
		jMenuAction.add(jMenuPanelInit);
		jMenuAction.add(jMenuPanelUpdate);
		jMenuAction.add(jMenuFolderOpen);
		jMenuBar1.add(jMenuHelp);
		jMenuBar1.add(jRestTimeMenu);
		jMenuHelp.add(jMenuHelpAbout);
		jMenuHelp.add(jMenuHelpUpdate);
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
		jMenuHelp.add(jMenuHelpEncoders);
		jMenuHelp.add(jMenuHelpPixformats);
		jMenuHelp.add(jMenuHelpHwaccels);
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
		UserInfoPanel.add(MailAddrLabel, new GridBagConstraints(0, 0, 1, 1,
				0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				INSETS_0_5_0_0, 0, 0));
		UserInfoPanel.add(MailAddrField, new GridBagConstraints(1, 0, 1, 1,
				1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				INSETS_0_5_5_0, 0, 0));
		UserInfoPanel.add(new ClearButton(MailAddrField), new GridBagConstraints(2, 0, 1, 1,
				0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				INSETS_0_0_5_5, 0, 0));
		UserInfoPanel.add(PasswordLabel, new GridBagConstraints(0, 1, 1, 1,
				0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				INSETS_0_5_0_0, 0, 0));
		UserInfoPanel.add(PasswordField, new GridBagConstraints(1, 1, 1, 1,
				1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				INSETS_0_5_5_0, 0, 0));
		UserInfoPanel.add(new ClearButton(PasswordField), new GridBagConstraints(2, 1, 1, 1,
				0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				INSETS_0_0_5_5, 0, 0));
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
		grid7_x2_y3_.insets = INSETS_0_5_0_5;
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
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 5;
		c.anchor = GridBagConstraints.SOUTHEAST;
		VhookPathSettingPanel.add(VhookPathSettingPanel.getHideLabel(), c);
// option default OR normal(4:3)
		FFmpegSettingPanel.add(getFFmpegOptionComboBoxPanel(),grid9_x0_y1_55);
		FFmpegSettingPanel.add(ExtOptionLabel, grid9_x0_y2_56);
		FFmpegSettingPanel.add(ExtOptionField, grid9_x1_y2_57);
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
		grid90_x0_y0_.insets = INSETS_0_5_0_5;
		additionalOptionPanel.add(new JLabel("設定１に追加"), grid90_x0_y0_);
		GridBagConstraints grid90_x1_y0_ = new GridBagConstraints();
		grid90_x1_y0_.gridx = 1;
		grid90_x1_y0_.gridy = 0;
		grid90_x1_y0_.weightx = 1.0;
		grid90_x1_y0_.fill = GridBagConstraints.HORIZONTAL;
		grid90_x1_y0_.insets = INSETS_0_5_0_5;
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
		GridBagConstraints grid1_x0_y2_100 = new GridBagConstraints(
				0, 2, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL,INSETS_0_5_0_5, 0, 0);
		VideoInfoPanel.add(loginCheckPanel, grid1_x0_y2_100);
		OpPanel.add(VideoID_Label, grid10_x0_y0_67);
		OpPanel.add(VideoID_TextField, grid10_x1_y0_68);
		OpPanel.add(historyBackButton, grid10_x1_y0_68B);
		OpPanel.add(historyForwardButton, grid10_x2_y0_68C);
		OpPanel.add(historyReplaceButton, grid10_x3_y0_68D);
		historyReplaceButton.setForeground(Color.black);
		historyBackButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String vid = "";
				Object obj = histories[historyID];
				if(obj instanceof HistoryDeque<?>)
					vid = (String) ((HistoryDeque<?>) obj).back();
				VideoID_TextField.setText(vid);
			}
		});
		historyForwardButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String vid = "";
				Object obj = histories[historyID];
				if(obj instanceof HistoryDeque<?>)
					vid = (String) ((HistoryDeque<?>) obj).next();
				VideoID_TextField.setText(vid);
			}
		});
		historyReplaceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				historyID = (historyID+1)%3;
				switch(historyID){
				case 0:
					VideoID_TextField.setText(" [リクエスト履歴] ");
					break;
				case 1:
					VideoID_TextField.setText(" [マイリスト履歴] ");
					break;
				case 2:
					VideoID_TextField.setText(" [テンプレート] ");
				}
			}
		});
		OpPanel.add(WayBackLabel, grid10_x0_y1_69);
		OpPanel.add(WayBackField, grid10_x1_y1_70);
		OpPanel.add(dateUserFirstCheckBox, grid10_x2_y1_70b);
		OpPanel.add(nicoLabel, grid10_x3_y1_70c);
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
		grid12_x0_y3.insets = INSETS_0_5_0_5;
		grid12_x0_y3.fill = GridBagConstraints.HORIZONTAL;
		grid12_x0_y3.anchor = GridBagConstraints.NORTH;
		BasicInfoTabPanel.add(getUpdateInfoPanel(),grid12_x0_y3);

		CommentSaveInfoPanel.add(SavingCommentCheckBox, grid4_x0_y6_16);
		CommentSaveInfoPanel.add(appendCommentCheckBox, grid4_x1_y6);
	//	CommentSaveInfoPanel.add(enableCommentJsonCheckBox, grid4_x2_y6);
	//	CommentSaveInfoPanel.add(enableCommentXmlCheckBox, grid4_x3_y6_);
	//	CommentSaveInfoPanel.add(dateUserFirstCheckBox, grid4_x2_y6_2);
		CommentSaveInfoPanel.add(AddTimeStampToCommentCheckBox, grid4_x0_y7_86);
		DelCommentCheckBox.setText("変換後にコメントファイルを削除する");
		CommentSaveInfoPanel.add(DelCommentCheckBox, grid4_x0_y8_25);
		FixCommentNumCheckBox.setText("コメント取得数は自動で調整する");
		CommentSaveInfoPanel.add(FixCommentNumCheckBox,grid4_x0_y9_26);
		CommentSaveInfoPanel.add(CommentNumLabel, grid4_x0_y10_17);
		CommentSaveInfoPanel.add(CommentNumField, grid4_x1_y10_22);
		CommentSaveInfoPanel.add(Comment_SaveFolderRadioButton,grid4_x0_y11_18);
		CommentSaveInfoPanel.add(CommentSavedFolderField, grid4_x0_y12_19);
		CommentSaveInfoPanel.add(openCommentSaveFolderButton, grid4_x4_y12);
		CommentSaveInfoPanel.add(ShowSavingCommentFolderDialogButton,grid4_x5_y12_23);
		CommentSaveInfoPanel.add(Comment_SaveFileRadioButton,grid4_x0_y13_20);
		CommentSaveInfoPanel.add(CommentSavedFileField, grid4_x0_y14_21);
		CommentSaveInfoPanel.add(openCommentSaveFileButton, grid4_x4_y14);
		CommentSaveInfoPanel.add(ShowSavingCommentFileDialogButton,grid4_x5_y14_24);
		OldCommentModePanel.add(commentModeComboBox, grid11_x0_y0_75);
		c = new GridBagConstraints();
		c.gridy = 1;
		OldCommentModePanel.add(OldCommentModePanel.getHideLabel(),c);
		OldCommentModePanel.add(OwnerCommentNoticeLabel1, grid11_x1_y1_76);
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
		NotAddVideoID_ConvVideoCheckBox.setText("ファイル名に動画IDを付加しない");
		ConvertedVideoSavingInfoPanel.add(NotAddVideoID_ConvVideoCheckBox,
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
		ConvertedVideoSavingInfoPanel.add(convFileMacroLabel,grid5_x2_y7_42b);
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
		c = new GridBagConstraints();
		c.gridy = 1;
		c.anchor = GridBagConstraints.SOUTHEAST;
		OptionalThreadInfoPanel.add(OptionalThreadInfoPanel.getHideLabel(), c);
		GridBagConstraints grid6_x0_y0_110 = new GridBagConstraints();
		grid6_x0_y0_110.gridx = 0;
		grid6_x0_y0_110.gridy = 1;
		grid6_x0_y0_110.weightx = 1.0;
		grid6_x0_y0_110.weighty = 0.0;
		grid6_x0_y0_110.anchor = GridBagConstraints.NORTH;
		grid6_x0_y0_110.fill = GridBagConstraints.HORIZONTAL;
		grid6_x0_y0_110.insets = INSETS_0_5_0_5;
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
				BorderFactory.createEtchedBorder(), "拡張Vhookパスの設定",
				TitledBorder.LEADING, TitledBorder.TOP,
				getFont(), Color.blue));
		zqPlayerModePanel.setForeground(Color.blue);
		zqPlayerModePanel.setLayout(new GridBagLayout());
		zqPlayerModePanel.setToolTipText("");
		zqPlayerModeCheckBox.setText("アスペクト比共通画面を使う　(使わない→従来画面を使う)");
		//zqPlayerModeCheckBox.setToolTipText("以下の設定が4:3と16:9が共通に使えます。");
		zqPlayerModeCheckBox.setForeground(Color.blue);
		GridBagConstraints grid_x0_y2_0 = new GridBagConstraints();
		grid_x0_y2_0 = new GridBagConstraints();
		grid_x0_y2_0.gridx = 0;
		grid_x0_y2_0.gridy = 0;
		grid_x0_y2_0.gridwidth = 2;
		grid_x0_y2_0.weightx = 1.0;
		grid_x0_y2_0.weighty = 0.0;
		grid_x0_y2_0.anchor = GridBagConstraints.NORTH;
		grid_x0_y2_0.fill = GridBagConstraints.BOTH;
		grid_x0_y2_0.insets = INSETS_0_5_0_5;
		zqPlayerModePanel.add(zqPlayerModeCheckBox, grid_x0_y2_0);
		grid_x0_y2_0 = new GridBagConstraints();
		grid_x0_y2_0.gridx = 0;
		grid_x0_y2_0.gridy = 1;
		grid_x0_y2_0.weightx = 1.0;
		grid_x0_y2_0.weighty = 0.0;
		grid_x0_y2_0.anchor = GridBagConstraints.NORTH;
		grid_x0_y2_0.fill = GridBagConstraints.BOTH;
		grid_x0_y2_0.insets = INSETS_0_5_0_5;
	//	zqPlayerModePanel.add(
	//		new JLabel("拡張Vhookの位置を設定する(アスペクト比共通)"),gird_x0_y2_0);
		grid_x0_y2_0 = new GridBagConstraints();
		grid_x0_y2_0.gridx = 0;
		grid_x0_y2_0.gridy = 2;
		grid_x0_y2_0.weightx = 1.0;
		grid_x0_y2_0.weighty = 0.0;
		grid_x0_y2_0.anchor = GridBagConstraints.NORTH;
		grid_x0_y2_0.fill = GridBagConstraints.BOTH;
		grid_x0_y2_0.insets = INSETS_0_5_0_5;
		zqPlayerModePanel.add(zqVhookPathField, grid_x0_y2_0);
		zqSettingVhookPathButton.setText("参照");
		zqSettingVhookPathButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SettingVhookZqPathButton_actionPerformed(e); }
		});
		grid_x0_y2_0 = new GridBagConstraints();
		grid_x0_y2_0.gridx = 1;
		grid_x0_y2_0.gridy = 2;
		grid_x0_y2_0.weightx = 0.0;
		grid_x0_y2_0.weighty = 0.0;
		grid_x0_y2_0.anchor = GridBagConstraints.NORTH;
		grid_x0_y2_0.fill = GridBagConstraints.BOTH;
		grid_x0_y2_0.insets = INSETS_0_5_0_5;
		zqPlayerModePanel.add(zqSettingVhookPathButton, grid_x0_y2_0);
		grid_x0_y2_0 = new GridBagConstraints();
		grid_x0_y2_0.gridx = 0;
		grid_x0_y2_0.gridy = 2;
		grid_x0_y2_0.weightx = 1.0;
		grid_x0_y2_0.weighty = 0.0;
		grid_x0_y2_0.anchor = GridBagConstraints.NORTH;
		grid_x0_y2_0.fill = GridBagConstraints.BOTH;
		grid_x0_y2_0.insets = INSETS_0_5_0_5;
		FFMpegTab2Panel.add(zqPlayerModePanel,grid_x0_y2_0);
// option Q
		zqFFmpegSettingPanel.setBorder(BorderFactory.createTitledBorder(
			CREATE_ETCHED_BORDER,
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

		grid_x0_y2_0 = new GridBagConstraints();
		grid_x0_y2_0.gridx = 0;
		grid_x0_y2_0.gridy = 6;
		grid_x0_y2_0.weightx = 0.0;
		grid_x0_y2_0.weighty = 0.0;
		grid_x0_y2_0.anchor = GridBagConstraints.NORTH;
		grid_x0_y2_0.fill = GridBagConstraints.BOTH;
		grid_x0_y2_0.insets = INSETS_0_5_0_5;
		zqFFmpegSettingPanel.add(new JLabel("追加オプション"), grid_x0_y2_0);
		grid_x0_y2_0 = new GridBagConstraints();
		grid_x0_y2_0.gridx = 1;
		grid_x0_y2_0.gridy = 6;
		grid_x0_y2_0.weightx = 1.0;
		grid_x0_y2_0.weighty = 0.0;
		grid_x0_y2_0.gridwidth =3;
		grid_x0_y2_0.anchor = GridBagConstraints.NORTH;
		grid_x0_y2_0.fill = GridBagConstraints.BOTH;
		grid_x0_y2_0.insets = INSETS_0_0_0_5;
		zqFFmpegSettingPanel.add(zqAdditionalOptionFiled, grid_x0_y2_0);

		zqLimitOptionPanel = new JPanel();
		zqLimitOptionPanel.setLayout(new GridBagLayout());
		grid_x0_y2_0 = new GridBagConstraints();
		grid_x0_y2_0.gridx = 0;
		grid_x0_y2_0.anchor = GridBagConstraints.NORTH;
		grid_x0_y2_0.fill = GridBagConstraints.BOTH;
		grid_x0_y2_0.insets = INSETS_0_0_0_5;
		zqSizeMinLabel.setText("最小サイズ");
		zqSizeMinLabel.setForeground(Color.blue);
		zqSizeMinLabel.setToolTipText("オプションファイル指定");
		zqLimitOptionPanel.add(zqSizeMinLabel, grid_x0_y2_0);
		grid_x0_y2_0 = new GridBagConstraints();
		grid_x0_y2_0.gridx = 1;
		grid_x0_y2_0.weightx = 1.0;
		grid_x0_y2_0.anchor = GridBagConstraints.NORTH;
		grid_x0_y2_0.fill = GridBagConstraints.BOTH;
		grid_x0_y2_0.insets = INSETS_0_0_0_5;
		zqSizeMinField.setToolTipText("最小横:縦");
		zqLimitOptionPanel.add(zqSizeMinField, grid_x0_y2_0);

		grid_x0_y2_0 = new GridBagConstraints();
		grid_x0_y2_0.gridx = 2;
		grid_x0_y2_0.anchor = GridBagConstraints.NORTH;
		grid_x0_y2_0.fill = GridBagConstraints.BOTH;
		grid_x0_y2_0.insets = INSETS_0_0_0_5;
		zqSizeMaxLabel.setText("最大サイズ");
		zqSizeMaxLabel.setForeground(Color.blue);
		zqSizeMaxLabel.setToolTipText("オプションファイル指定");
		zqLimitOptionPanel.add(zqSizeMaxLabel, grid_x0_y2_0);
		grid_x0_y2_0 = new GridBagConstraints();
		grid_x0_y2_0.gridx = 3;
		grid_x0_y2_0.weightx = 1.0;
		grid_x0_y2_0.anchor = GridBagConstraints.NORTH;
		grid_x0_y2_0.fill = GridBagConstraints.BOTH;
		grid_x0_y2_0.insets = INSETS_0_0_0_5;
		zqSizeMaxField.setToolTipText("最大横:縦");
		zqLimitOptionPanel.add(zqSizeMaxField, grid_x0_y2_0);

		grid_x0_y2_0 = new GridBagConstraints();
		grid_x0_y2_0.gridx = 4;
		grid_x0_y2_0.anchor = GridBagConstraints.NORTH;
		grid_x0_y2_0.fill = GridBagConstraints.BOTH;
		grid_x0_y2_0.insets = INSETS_0_0_0_5;
		zqFpsRangeLabel.setText("最小fps");
		zqFpsRangeLabel.setForeground(Color.blue);
		zqFpsRangeLabel.setToolTipText("オプションファイル指定");
		zqLimitOptionPanel.add(zqFpsRangeLabel, grid_x0_y2_0);
		grid_x0_y2_0 = new GridBagConstraints();
		grid_x0_y2_0.gridx = 5;
		grid_x0_y2_0.weightx = 1.0;
		grid_x0_y2_0.anchor = GridBagConstraints.NORTH;
		grid_x0_y2_0.fill = GridBagConstraints.BOTH;
		grid_x0_y2_0.insets = INSETS_0_0_0_5;
		zqFpsRangeField.setToolTipText("既定:指定値に最も近い動画fpsの整数倍、J付きは数値丁度");
		zqLimitOptionPanel.add(zqFpsRangeField, grid_x0_y2_0);

		grid_x0_y2_0 = new GridBagConstraints();
		grid_x0_y2_0.gridx = 0;
		grid_x0_y2_0.gridy = 7;
		grid_x0_y2_0.weightx = 1.0;
		grid_x0_y2_0.gridwidth = 4;
		grid_x0_y2_0.anchor = GridBagConstraints.NORTH;
		grid_x0_y2_0.fill = GridBagConstraints.BOTH;
		grid_x0_y2_0.insets = INSETS_0_5_0_5;
		zqFFmpegSettingPanel.add(zqLimitOptionPanel, grid_x0_y2_0);

		grid_x0_y2_0 = new GridBagConstraints();
		grid_x0_y2_0.gridx = 0;
		grid_x0_y2_0.gridy = 8;
		grid_x0_y2_0.weightx = 0.0;
		grid_x0_y2_0.weighty = 0.0;
		grid_x0_y2_0.anchor = GridBagConstraints.NORTH;
		grid_x0_y2_0.fill = GridBagConstraints.BOTH;
		grid_x0_y2_0.insets = INSETS_0_0_0_5;
		zqMetadataCheckBox.setText("メタデータ");
		zqFFmpegSettingPanel.add(zqMetadataCheckBox, grid_x0_y2_0);
		grid_x0_y2_0 = new GridBagConstraints();
		grid_x0_y2_0.gridx = 1;
		grid_x0_y2_0.gridy = 8;
		grid_x0_y2_0.weightx = 1.0;
		grid_x0_y2_0.weighty = 0.0;
		grid_x0_y2_0.gridwidth =3;
		grid_x0_y2_0.anchor = GridBagConstraints.NORTH;
		grid_x0_y2_0.fill = GridBagConstraints.BOTH;
		grid_x0_y2_0.insets = INSETS_0_0_0_5;
		zqFFmpegSettingPanel.add(zqMetadataField, grid_x0_y2_0);
		zqFFmpegSettingPanel.setForeground(Color.blue);

		grid_x0_y2_0 = new GridBagConstraints();
		grid_x0_y2_0.gridx = 0;
		grid_x0_y2_0.gridy = 4;
		grid_x0_y2_0.weightx = 1.0;
		grid_x0_y2_0.weighty = 0.0;
		grid_x0_y2_0.anchor = GridBagConstraints.NORTH;
		grid_x0_y2_0.fill = GridBagConstraints.BOTH;
		grid_x0_y2_0.insets = INSETS_0_5_0_5;
		FFMpegTab2Panel.add(zqFFmpegSettingPanel,grid_x0_y2_0);

		CheckFFmpegFunctionPanel = new JPanelHideable(
			"FFmpegFunction", "FFmpeg機能チェック", Color.blue);
		GridBagConstraints grid__x0_y0_107 = new GridBagConstraints();
		grid__x0_y0_107.gridx = 0;
		grid__x0_y0_107.gridy = 0;
		grid__x0_y0_107.weightx = 0.0;
		grid__x0_y0_107.weightx = 0.0;
		grid__x0_y0_107.anchor = GridBagConstraints.NORTHWEST;
		grid__x0_y0_107.fill = GridBagConstraints.NONE;
		grid__x0_y0_107.insets = INSETS_0_0_0_0;
		CheckFFmpegFunctionPanel.add(CheckFFmpegVersionButton, grid__x0_y0_107);
		GridBagConstraints grid__x1_y0_106 = new GridBagConstraints();
		grid__x1_y0_106.gridx = 1;
		grid__x1_y0_106.gridy = 0;
		grid__x1_y0_106.weightx = 0.5;
		grid__x1_y0_106.weighty = 0.0;
		grid__x1_y0_106.anchor = GridBagConstraints.WEST;
		grid__x1_y0_106.fill = GridBagConstraints.HORIZONTAL;
		grid__x1_y0_106.insets = INSETS_0_0_0_0;
		CheckFFmpegFunctionPanel.add(CheckFFmpegVersionLabel, grid__x1_y0_106);
		CheckDownloadVideoButton.setText("表");
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
		grid_x0_y1_85.gridx = 2;
		grid_x0_y1_85.gridy = 0;
		grid_x0_y1_85.weightx = 0.0;
		grid_x0_y1_85.weighty = 0.0;
		grid_x0_y1_85.anchor = GridBagConstraints.NORTHWEST;
		grid_x0_y1_85.fill = GridBagConstraints.NONE;
		grid_x0_y1_85.insets = INSETS_0_0_0_0;
		CheckFFmpegFunctionPanel.add(CheckDownloadVideoButton, grid_x0_y1_85);
		CheckDownloadVideoLabel.setText("保存動画チェック");
		CheckDownloadVideoLabel.setForeground(Color.blue);
		GridBagConstraints grid_x1_y1_88 = new GridBagConstraints();
		grid_x1_y1_88.gridx = 3;
		grid_x1_y1_88.gridy = 0;
		grid_x1_y1_88.weightx = 0.5;
		grid_x1_y1_88.weighty = 0.0;
		grid_x1_y1_88.anchor = GridBagConstraints.WEST;
		grid_x1_y1_88.fill = GridBagConstraints.HORIZONTAL;
		grid_x1_y1_88.insets = INSETS_0_0_0_0;
		CheckFFmpegFunctionPanel.add(CheckDownloadVideoLabel,grid_x1_y1_88);
		showDownloadListButton.setText("表");
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
		grid_x1_y3_x.insets = INSETS_0_0_0_0;
		CheckFFmpegFunctionPanel.add(showDownloadListButton, grid_x1_y3_x);
		showDownloadListLabel.setText("ダウンロードリスト表示");
		grid_x1_y3_x = new GridBagConstraints();
		grid_x1_y3_x.gridx = 1;
		grid_x1_y3_x.gridy = 3;
		grid_x1_y3_x.weightx = 0.5;
		grid_x1_y3_x.weighty = 0.0;
		grid_x1_y3_x.anchor = GridBagConstraints.WEST;
		grid_x1_y3_x.fill = GridBagConstraints.HORIZONTAL;
		grid_x1_y3_x.insets = INSETS_0_0_0_0;
		CheckFFmpegFunctionPanel.add(showDownloadListLabel, grid_x1_y3_x);
		playConvertedVideoButton.setText("再");
		playConvertedVideoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				autoPlay.playAuto();
			}
		});
		playConvertedVideoButton.setForeground(Color.blue);
		GridBagConstraints grid_x1_y4_x = new GridBagConstraints();
		grid_x1_y4_x.gridx = 2;
		grid_x1_y4_x.gridy = 3;
		grid_x1_y4_x.weightx = 0.0;
		grid_x1_y4_x.weighty = 0.0;
		grid_x1_y4_x.anchor = GridBagConstraints.NORTHWEST;
		grid_x1_y4_x.fill = GridBagConstraints.NONE;
		grid_x1_y4_x.insets = INSETS_0_0_0_0;
		CheckFFmpegFunctionPanel.add(playConvertedVideoButton, grid_x1_y4_x);
		playConvertedVideoLabel.setText("変換後動画再生");
		playConvertedVideoLabel.setForeground(Color.blue);
		grid_x1_y4_x = new GridBagConstraints();
		grid_x1_y4_x.gridx = 3;
		grid_x1_y4_x.gridy = 3;
		grid_x1_y4_x.weightx = 0.5;
		grid_x1_y4_x.weighty = 0.0;
		grid_x1_y4_x.anchor = GridBagConstraints.WEST;
		grid_x1_y4_x.fill = GridBagConstraints.HORIZONTAL;
		grid_x1_y4_x.insets = INSETS_0_0_0_0;
		CheckFFmpegFunctionPanel.add(playConvertedVideoLabel, grid_x1_y4_x);
		c = new GridBagConstraints();
		c.gridy = 3;
		c.anchor = GridBagConstraints.SOUTHEAST;
		CheckFFmpegFunctionPanel.add(CheckFFmpegFunctionPanel.getHideLabel(), c);
		GridBagConstraints grid6_x0_y2_82 = new GridBagConstraints();
		grid6_x0_y2_82.gridx = 0;
		grid6_x0_y2_82.gridy = 6;
		grid6_x0_y2_82.weightx = 1.0;
		grid6_x0_y2_82.weighty = 1.0;
		grid6_x0_y2_82.anchor = GridBagConstraints.NORTHWEST;
		grid6_x0_y2_82.fill = GridBagConstraints.HORIZONTAL;
		grid6_x0_y2_82.insets = INSETS_0_5_0_5;
		FFMpegTab2Panel.add(CheckFFmpegFunctionPanel, grid6_x0_y2_82);

		convertManager = new ConvertManager(new JLabel[] {statusBar, elapsedTimeBar, infoBar});
		convertManager.start();
	}
	private void setHtml5AutoDefault(){
		html5Player = html5CheckBox.isSelected()? 1 : 0;
		if(enableAutoHtml5CheckBox.isSelected()){
			setShadowDefault();
			setHtml5Comment();
		}
	}
	private void setShadowDefault(){
		try {
			String s = shadowDefaultSetting[html5Player];
			if(s!=null && !s.isEmpty()){
				String[] list = s.split(" ", 2);
				Integer idx = Integer.decode(list[0]);
				if(SwingUtilities.isEventDispatchThread()){
					ShadowComboBox.setSelectedIndex(idx.intValue());
				}else{
					final int index = idx.intValue();
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							ShadowComboBox.setSelectedIndex(index);
						}
					});
				}
				setExtraShadowText(list[1]);
			}
		}catch(Exception e){}
	}
	private void setHtml5Comment(){
		if(enableAutoHtml5CheckBox.isSelected()){
			enableHtml5CommentCheckBox.setEnabled(true);
			enableHtml5CommentCheckBox.setSelected(html5CheckBox.isSelected());
			enableHtml5CommentCheckBox.setEnabled(false);
		}
	}
	private void enableAutoHtml5Action(){
		setHtml5AutoDefault();
		if(enableAutoHtml5CheckBox.isSelected()){
			enableHtml5CommentCheckBox.setEnabled(false);
		}else{
			enableHtml5CommentCheckBox.setEnabled(true);
		}
	}

	private String getTextField(JTextField input){
		String val = "";
		if(input!=null)
			val = input.getText();
		if(val==null)
			val = "";
		return val;
	}
	private void debugModeSet(final boolean b){
		if(SwingUtilities.isEventDispatchThread()){
			doDebugModeSet(b);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					doDebugModeSet(b);
				}
			});
		}
	}
	public static boolean getTextFieldBoolean(JTextField field,String key){
		String text = field.getText();
		if(text==null || key==null || key.isEmpty())
			return false;
		return text.contains(key);
	}
	public static boolean setTextFieldBoolean(JTextField field,String key,boolean b, String separater){
		boolean yes = getTextFieldBoolean(field,key);
		String text = field.getText();
		if(text==null) text = "";
		if(b){
			if(!yes){
				if(!text.isEmpty())
					key = key+separater;
				field.setText(key+text);
			}
		}else{
			if(yes){
				field.setText(text.replaceAll(key, "").trim());
			}
		}
		return yes;	//old value
	}
	private void doDebugModeSet(boolean b){
		setTextFieldBoolean(ProxyTextField, DEBUG_NET_FLAG, b, "");
		setDebugProxyPort(b);
		setTextFieldBoolean(extraModeField, DEBUG_COMMENT_FLAG, b, " ");
	}
	private boolean isDebugNet(){
		return ProxyPortTextField.getText().startsWith(DEBUG_NET_FLAG);
	}
	private boolean isDebugComment(){
		return getTextFieldBoolean(extraModeField, DEBUG_COMMENT_FLAG);
	}
	private boolean isDebugModeSet(){
		return isDebugNet() || isDebugComment();
	}
	private void setDebugProxyPort(boolean b){
		String proxy_port = getTextField(ProxyPortTextField);
		if(b){
			// set enable debug
			if(saveUseProxy.isEmpty())
				saveUseProxy = Boolean.toString(UseProxyCheckBox.isSelected());
			if(proxy_port.isEmpty())
				ProxyPortTextField.setText(debug_port);
			UseProxyCheckBox.setSelected(true);
		}else{
			// disable debug and restore proxy
			if(!saveUseProxy.isEmpty()){
				b = Boolean.parseBoolean(saveUseProxy);
				UseProxyCheckBox.setSelected(b);
				saveUseProxy = "";
			}
			if(proxy_port.equals(debug_port))
				ProxyPortTextField.setText("");
		}
	}

	private JPanel getVhookSettingPanel()
	{
		if (VhookSettingPanel==null) {
			VhookSettingPanel = new JPanelHideable("VhookSetting","拡張vhookライブラリの設定",Color.black);
			GridBagConstraints grid8_y11_x0_w3 = new GridBagConstraints();
			grid8_y11_x0_w3.gridy = 11;
			grid8_y11_x0_w3.gridx = 0;
			grid8_y11_x0_w3.gridwidth = 3;
			grid8_y11_x0_w3.anchor = GridBagConstraints.WEST;
			grid8_y11_x0_w3.fill = GridBagConstraints.HORIZONTAL;
			grid8_y11_x0_w3.insets = INSETS_0_0_0_5;
			GridBagConstraints grid8_y11_x3_w3 = new GridBagConstraints();
			grid8_y11_x3_w3.gridy = 11;
			grid8_y11_x3_w3.gridx = 3;
			grid8_y11_x3_w3.gridwidth = 3;
			grid8_y11_x3_w3.weightx = 1.0;
			grid8_y11_x3_w3.anchor = GridBagConstraints.CENTER;
			grid8_y11_x3_w3.fill = GridBagConstraints.HORIZONTAL;
			grid8_y11_x3_w3.insets = INSETS_0_0_0_5;
			GridBagConstraints grid8_y10_x0_w3 = new GridBagConstraints();
			grid8_y10_x0_w3.gridy = 10;
			grid8_y10_x0_w3.gridx = 0;
			grid8_y10_x0_w3.gridwidth = 3;
			grid8_y10_x0_w3.anchor = GridBagConstraints.WEST;
			grid8_y10_x0_w3.fill = GridBagConstraints.HORIZONTAL;
			grid8_y10_x0_w3.insets = INSETS_0_0_0_5;
			GridBagConstraints grid8_y10_x3_w3 = new GridBagConstraints();
			grid8_y10_x3_w3.gridy = 10;
			grid8_y10_x3_w3.gridx = 3;
			grid8_y10_x3_w3.gridwidth = 3;
			grid8_y10_x3_w3.weightx = 1.0;
			grid8_y10_x3_w3.anchor = GridBagConstraints.CENTER;
			grid8_y10_x3_w3.fill = GridBagConstraints.HORIZONTAL;
			grid8_y10_x3_w3.insets = INSETS_0_0_0_5;
			GridBagConstraints grid8_y9_x0_w3 = new GridBagConstraints();
			grid8_y9_x0_w3.gridy = 9;
			grid8_y9_x0_w3.gridx = 0;
			grid8_y9_x0_w3.gridwidth = 3;
			grid8_y9_x0_w3.anchor = GridBagConstraints.WEST;
			grid8_y9_x0_w3.fill = GridBagConstraints.HORIZONTAL;
			grid8_y9_x0_w3.insets = INSETS_0_0_0_5;
			GridBagConstraints grid8_y9_x3_w3 = new GridBagConstraints();
			grid8_y9_x3_w3.gridy = 9;
			grid8_y9_x3_w3.gridx = 3;
			grid8_y9_x3_w3.gridwidth = 3;
			grid8_y9_x3_w3.weightx = 1.0;
			grid8_y9_x3_w3.anchor = GridBagConstraints.WEST;
			grid8_y9_x3_w3.fill = GridBagConstraints.HORIZONTAL;
			grid8_y9_x3_w3.insets = INSETS_0_0_0_5;
			GridBagConstraints grid8_y7_x0_w2 = new GridBagConstraints();
			grid8_y7_x0_w2.gridy = 7;
			grid8_y7_x0_w2.gridx = 0;
			grid8_y7_x0_w2.gridwidth = 2;
			grid8_y7_x0_w2.weightx = 0.0;
			grid8_y7_x0_w2.anchor = GridBagConstraints.WEST;
			grid8_y7_x0_w2.fill = GridBagConstraints.HORIZONTAL;
			grid8_y7_x0_w2.insets = INSETS_0_0_0_5;
			GridBagConstraints grid8_y7_x2_w1 = new GridBagConstraints();
			grid8_y7_x2_w1.gridy = 7;
			grid8_y7_x2_w1.gridx = 2;
			grid8_y7_x2_w1.gridwidth = 1;
			grid8_y7_x2_w1.weightx = 0.0;
			grid8_y7_x2_w1.anchor = GridBagConstraints.WEST;
			grid8_y7_x2_w1.fill = GridBagConstraints.HORIZONTAL;
			grid8_y7_x2_w1.insets = INSETS_0_0_0_5;
			GridBagConstraints grid8_y7_x3_w1 = new GridBagConstraints();
			grid8_y7_x3_w1.gridy = 7;
			grid8_y7_x3_w1.gridx = 3;
			grid8_y7_x3_w1.gridwidth = 1;
			grid8_y7_x3_w1.weightx = 0.8;
			grid8_y7_x3_w1.anchor = GridBagConstraints.WEST;
			grid8_y7_x3_w1.fill = GridBagConstraints.HORIZONTAL;
			grid8_y7_x3_w1.insets = INSETS_0_0_0_5;
			GridBagConstraints grid8_y7_x4_w2 = new GridBagConstraints();
			grid8_y7_x4_w2.gridy = 7;
			grid8_y7_x4_w2.gridx = 4;
			grid8_y7_x4_w2.gridwidth = 2;
			grid8_y7_x4_w2.weightx = 0.2;
			grid8_y7_x4_w2.anchor = GridBagConstraints.WEST;
			grid8_y7_x4_w2.fill = GridBagConstraints.HORIZONTAL;
			grid8_y7_x4_w2.insets = INSETS_0_0_0_5;
			GridBagConstraints grid8_y6_x0_w1 = new GridBagConstraints();
			grid8_y6_x0_w1.gridy = 6;
			grid8_y6_x0_w1.gridx = 0;
			grid8_y6_x0_w1.gridwidth = 1;
			grid8_y6_x0_w1.anchor = GridBagConstraints.WEST;
			grid8_y6_x0_w1.fill = GridBagConstraints.NONE;
			grid8_y6_x0_w1.insets = INSETS_0_5_0_5;
			GridBagConstraints grid8_y6_x1_w3 = new GridBagConstraints();
			grid8_y6_x1_w3.gridy = 6;
			grid8_y6_x1_w3.gridx = 1;
			grid8_y6_x1_w3.gridwidth = 3;
			grid8_y6_x1_w3.weightx = 1.0;
			grid8_y6_x1_w3.anchor = GridBagConstraints.WEST;
			grid8_y6_x1_w3.fill = GridBagConstraints.HORIZONTAL;
			grid8_y6_x1_w3.insets = INSETS_0_0_0_5;
			GridBagConstraints grid8_y6_x4_w2 = new GridBagConstraints();
			grid8_y6_x4_w2.gridy = 6;
			grid8_y6_x4_w2.gridx = 4;
			grid8_y6_x4_w2.gridwidth = 2;
			grid8_y6_x4_w2.weightx = 0.0;
			grid8_y6_x4_w2.anchor = GridBagConstraints.WEST;
			grid8_y6_x4_w2.fill = GridBagConstraints.HORIZONTAL;
			grid8_y6_x4_w2.insets = INSETS_0_0_0_5;
			GridBagConstraints grid8_y5_x0_w1 = new GridBagConstraints();
			grid8_y5_x0_w1.gridy = 5;
			grid8_y5_x0_w1.gridx = 0;
			grid8_y5_x0_w1.gridwidth = 1;
			grid8_y5_x0_w1.anchor = GridBagConstraints.WEST;
			grid8_y5_x0_w1.fill = GridBagConstraints.NONE;
			grid8_y5_x0_w1.insets = INSETS_0_5_0_5;
			GridBagConstraints grid8_y5_x1_w2 = new GridBagConstraints();
			grid8_y5_x1_w2.gridy = 5;
			grid8_y5_x1_w2.gridx = 1;
			grid8_y5_x1_w2.gridwidth = 2;
			grid8_y5_x1_w2.weightx = 0.0;
			grid8_y5_x1_w2.anchor = GridBagConstraints.WEST;
			grid8_y5_x1_w2.fill = GridBagConstraints.HORIZONTAL;
			grid8_y5_x1_w2.insets = INSETS_0_0_0_5;
			GridBagConstraints grid8_y5_x3_w2 = new GridBagConstraints();
			grid8_y5_x3_w2.gridy = 5;
			grid8_y5_x3_w2.gridx = 3;
			grid8_y5_x3_w2.gridwidth = 2;
			grid8_y5_x3_w2.weightx = 0.0;
			grid8_y5_x3_w2.anchor = GridBagConstraints.WEST;
			grid8_y5_x3_w2.fill = GridBagConstraints.HORIZONTAL;
			grid8_y5_x3_w2.insets = INSETS_0_0_0_5;
			GridBagConstraints grid8_y3_x0_w1 = new GridBagConstraints();
			grid8_y3_x0_w1.gridy = 3;
			grid8_y3_x0_w1.gridx = 0;
			grid8_y3_x0_w1.gridwidth = 1;
			grid8_y3_x0_w1.weightx = 0.0;
			grid8_y3_x0_w1.anchor = GridBagConstraints.WEST;
			grid8_y3_x0_w1.fill = GridBagConstraints.NONE;
			grid8_y3_x0_w1.insets = INSETS_0_5_0_5;
			GridBagConstraints grid8_y3_x1_w5 = new GridBagConstraints();
			grid8_y3_x1_w5.gridy = 3;
			grid8_y3_x1_w5.gridx = 1;
			grid8_y3_x1_w5.gridwidth = 5;
			grid8_y3_x1_w5.anchor = GridBagConstraints.WEST;
			grid8_y3_x1_w5.fill = GridBagConstraints.HORIZONTAL;
			grid8_y3_x1_w5.insets = INSETS_0_0_0_5;
			GridBagConstraints grid8_y2_x0_w1 = new GridBagConstraints();
			grid8_y2_x0_w1.gridy = 2;
			grid8_y2_x0_w1.gridx = 0;
			grid8_y2_x0_w1.gridwidth = 1;
			grid8_y2_x0_w1.anchor = GridBagConstraints.WEST;
			grid8_y2_x0_w1.fill = GridBagConstraints.HORIZONTAL;
			grid8_y2_x0_w1.insets = INSETS_0_5_0_5;
			GridBagConstraints grid8_y2_x1_w5 = new GridBagConstraints();
			grid8_y2_x1_w5.gridy = 2;
			grid8_y2_x1_w5.gridx = 1;
			grid8_y2_x1_w5.gridwidth = 5;
			grid8_y2_x1_w5.weightx = 1.0;
			grid8_y2_x1_w5.anchor = GridBagConstraints.WEST;
			grid8_y2_x1_w5.fill = GridBagConstraints.HORIZONTAL;
			grid8_y2_x1_w5.insets = INSETS_0_0_0_5;
			GridBagConstraints grid8_y1_x0_w1 = new GridBagConstraints();
			grid8_y1_x0_w1.gridy = 1;
			grid8_y1_x0_w1.gridx = 0;
			grid8_y1_x0_w1.gridwidth = 1;
			grid8_y1_x0_w1.anchor = GridBagConstraints.WEST;
			grid8_y1_x0_w1.fill = GridBagConstraints.HORIZONTAL;
			grid8_y1_x0_w1.insets = INSETS_0_5_0_5;
			GridBagConstraints grid8_y1_x1_w5 = new GridBagConstraints();
			grid8_y1_x1_w5.gridy = 1;
			grid8_y1_x1_w5.gridx = 1;
			grid8_y1_x1_w5.gridwidth = 5;
			grid8_y1_x1_w5.weightx = 1.0;
			grid8_y1_x1_w5.anchor = GridBagConstraints.WEST;
			grid8_y1_x1_w5.fill = GridBagConstraints.HORIZONTAL;
			grid8_y1_x1_w5.insets = INSETS_0_0_0_5;
			GridBagConstraints grid8_y0_x0_w2 = new GridBagConstraints();
			grid8_y0_x0_w2.gridy = 0;
			grid8_y0_x0_w2.gridx = 0;
			grid8_y0_x0_w2.gridwidth = 2;
			grid8_y0_x0_w2.weightx = 0.0;
			grid8_y0_x0_w2.anchor = GridBagConstraints.WEST;
			grid8_y0_x0_w2.fill = GridBagConstraints.HORIZONTAL;
			grid8_y0_x0_w2.insets = INSETS_0_0_0_0;
			GridBagConstraints grid8_y0_x2_w3 = new GridBagConstraints();
			grid8_y0_x2_w3.gridy = 0;
			grid8_y0_x2_w3.gridx = 2;
			grid8_y0_x2_w3.gridwidth = 3;
			grid8_y0_x2_w3.anchor = GridBagConstraints.WEST;
			grid8_y0_x2_w3.fill = GridBagConstraints.HORIZONTAL;
			grid8_y0_x2_w3.insets = INSETS_0_0_0_0;
			GridBagConstraints grid8_y0_x5_w1 = new GridBagConstraints();
			grid8_y0_x5_w1.gridy = 0;
			grid8_y0_x5_w1.gridx = 5;
			grid8_y0_x5_w1.gridwidth = 1;
			grid8_y0_x5_w1.anchor = GridBagConstraints.WEST;
			grid8_y0_x5_w1.fill = GridBagConstraints.HORIZONTAL;
			grid8_y0_x5_w1.insets = INSETS_0_0_0_0;
			NotUseVhookCheckBox.setText("拡張vhook無効(デバッグ用)");
			NotUseVhookCheckBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(NotUseVhookCheckBox.isSelected()){
						ShowConvVideoCheckBox.setEnabled(false);
					}else{
						ShowConvVideoCheckBox.setEnabled(true);
					}
				}
			});
			VhookSettingPanel.add(NotUseVhookCheckBox, grid8_y0_x0_w2);
			ShowConvVideoCheckBox.setText("変換中画像表示");
			VhookSettingPanel.add(ShowConvVideoCheckBox, grid8_y0_x2_w3);
			videoThumbnailSizeCheckBox.setForeground(Color.blue);
			videoThumbnailSizeCheckBox.setText("小画面");
			videoThumbnailSizeCheckBox.setToolTipText("変換サイズとは関係なく表示だけを小さくします");
			VhookSettingPanel.add(videoThumbnailSizeCheckBox, grid8_y0_x5_w1);
			VhookSettingPanel.add(new JLabel("表示コメント数"), grid8_y1_x0_w1);
			VhookSettingPanel.add(ViewCommentField, grid8_y1_x1_w5);
			commentEraseTypeLabel.setText("表示数超過");
			VhookSettingPanel.add(commentEraseTypeLabel, grid8_y2_x0_w1);
			commentEraseTypeComboBox = new JComboBox<String>(CommentEraseTypeArray);
			VhookSettingPanel.add(commentEraseTypeComboBox,grid8_y2_x1_w5);
			VhookSettingPanel.add(new JLabel("フォントパス"), grid8_y3_x0_w1);
			VhookSettingPanel.add(getFontPathPanel(), grid8_y3_x1_w5);
			VhookSettingPanel.add(new JLabel("フォント番号"), grid8_y5_x0_w1);
			VhookSettingPanel.add(FontIndexField, grid8_y5_x1_w2);
			normalFontCheckBox.setText("細");
			normalFontCheckBox.setForeground(Color.blue);
			normalFontCheckBox.setToolTipText("可能ならboldフォントを使いません(追加モード設定)");
			normalFontCheckBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setTextFieldBoolean(extraModeField, "-normal"
						, normalFontCheckBox.isSelected(), " ");
				}
			});
			VhookSettingPanel.add(normalFontCheckBox, grid8_y5_x3_w2);
			shadowAutoSettingButton.setText("影既定");
			shadowAutoSettingButton.setToolTipText("自動html5切替時の影の設定。");
			VhookSettingPanel.add(shadowAutoSettingButton, grid8_y6_x4_w2);
			shadowAutoSettingButton.addActionListener(new ActionListener() {
				JLabel label1 = new JLabel("この影を現在のプレーヤーの既定値とします。");
				JLabel label2 = new JLabel();
				JLabel label3 = new JLabel();
				JLabel label4 = new JLabel("自動html5切換オフ時は既定値は無効");
				@Override
				public void actionPerformed(ActionEvent e) {
					int player = html5CheckBox.isSelected()? 1 : 0;
					int shadowIndex = ShadowComboBox.getSelectedIndex();
					String extraShadow = getExtraShadowText();
					String extraShadow2 = "";
					if(!extraShadow.isEmpty())
						extraShadow2 = "、　"+extraShadow;
					label2.setText("プレーヤー：　"+html5PlayerArray[player]);
					label2.setForeground(Color.blue);
					label3.setText("既定の影　"+ShadowComboBox.getItemAt(shadowIndex)+extraShadow2);
					label3.setForeground(Color.blue);
					Box message = Box.createVerticalBox();
					message.add(label1);
					message.add(Box.createVerticalStrut(10));
					message.add(label2);
					message.add(Box.createVerticalStrut(10));
					message.add(label3);
					message.add(Box.createVerticalStrut(10));
					message.add(label4);
					int shadowNotice =
					JOptionPane.showConfirmDialog(
						MainFrame.getMaster(),
						message,
						"影切替自動設定", JOptionPane.OK_CANCEL_OPTION
					);
					if(shadowNotice==JOptionPane.OK_OPTION){
						shadowDefaultSetting[player] = Integer.toString(shadowIndex)+" "+extraShadow;
					}
				}
			});
			VhookSettingPanel.add(new JLabel("影の種類"), grid8_y6_x0_w1);
			ShadowComboBox = new JComboBox<String>(ConvertingSetting.ShadowKindArray);
			VhookSettingPanel.add(ShadowComboBox, grid8_y6_x1_w3);
			FixFontSizeCheckBox.setText("フォントサイズを自動調整");
			VhookSettingPanel.add(FixFontSizeCheckBox, grid8_y7_x0_w2);
			resizeAdjustCheckBox.setText("補正%");
			resizeAdjustCheckBox.setForeground(Color.blue);
			VhookSettingPanel.add(resizeAdjustCheckBox, grid8_y7_x2_w1);
			resizeAdjustField.setText("100");
			VhookSettingPanel.add(resizeAdjustField, grid8_y7_x3_w1);
			resizeAdjustSlider = new JSlider(0, 200, 100);
			VhookSettingPanel.add(resizeAdjustSlider, grid8_y7_x4_w2);
			resizeAdjustSlider.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					resizeAdjustField
						.setText(Integer.toString(resizeAdjustSlider.getValue()));
				}
			});
			resizeAdjustCheckBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					resizeAdjustAction(resizeAdjustCheckBox.isSelected());
				}
			});
			OpaqueCommentCheckBox.setText("全てのコメントを不透明に(0.0～1.0)");
			VhookSettingPanel.add(OpaqueCommentCheckBox, grid8_y9_x0_w3);
			opaqueRateTextField.setForeground(Color.blue);
			opaqueRateTextField.setToolTipText("不透明度（既定 オフ 0.0-0.3/オン 1.0）");
			VhookSettingPanel.add(opaqueRateTextField, grid8_y9_x3_w3);
			//VhookSettingPanel.add(new JLabel("設定 0～1"), grid8_x3_y9_103);
			commentSpeedCheckBox.setText("コメント速度（Pixel/Sec又は@秒数)");
			commentSpeedCheckBox.setForeground(Color.blue);
			VhookSettingPanel.add(commentSpeedCheckBox, grid8_y10_x0_w3);
			commentSpeedTextField.setForeground(Color.blue);
			commentSpeedTextField.setToolTipText("公式の最小値は約138Pixel/Sec,約4秒");
			VhookSettingPanel.add(commentSpeedTextField, grid8_y10_x3_w3);
			commentLineFeedCheckBox.setText("コメント行送り(フォントサイズに対する%値)");
			commentLineFeedCheckBox.setForeground(Color.blue);
			commentLineFeedCheckBox.setToolTipText("ver.2: マイナス値の時影付でも行間縮み易くした");
			VhookSettingPanel.add(commentLineFeedCheckBox, grid8_y11_x0_w3);
			commentLineFeedTextField.setForeground(Color.blue);
			commentLineFeedTextField.setToolTipText("整数値[,ver](ver=1は1.67.1.12)");
			VhookSettingPanel.add(commentLineFeedTextField, grid8_y11_x3_w3);
			GridBagConstraints c = new GridBagConstraints();
			c.gridy = 11;
			c.anchor = GridBagConstraints.SOUTHEAST;
			c.insets = INSETS_0_0_0_0;
			VhookSettingPanel.add(VhookSettingPanel.getHideLabel(), c);
		}
		return VhookSettingPanel;
	}
	private String getExtraShadowText(){
		try {
			String t = extraModeField.getText();
			Matcher m = Pattern.compile(".*(-shadow=[^ ]*).*").matcher(t);
			if(m.find())
				return m.group(1);
		}catch(NullPointerException e){};
		return "";
	}
	private void setExtraShadowText(String shadow){
		if(shadow==null) shadow = "";
		String old = getExtraShadowText();
		setTextFieldBoolean(extraModeField, old, !shadow.isEmpty(), " ");
	}

	private void resizeAdjustAction(boolean is_selected) {
		resizeAdjustField.setEditable(is_selected);
		resizeAdjustSlider.setEnabled(is_selected);
	}

	private JPanel fontPathPanel;

	private JPanel getFontPathPanel(){
		if(fontPathPanel==null){
			fontPathPanel = new JPanel(new GridBagLayout());
			GridBagConstraints grid_y0_x0_w4 = new GridBagConstraints();
			grid_y0_x0_w4.gridy = 0;
			grid_y0_x0_w4.gridx = 0;
			grid_y0_x0_w4.gridwidth = 4;
			grid_y0_x0_w4.weightx = 1.0;
			grid_y0_x0_w4.anchor = GridBagConstraints.WEST;
			grid_y0_x0_w4.fill = GridBagConstraints.HORIZONTAL;
			grid_y0_x0_w4.insets = INSETS_0_0_0_5;
			GridBagConstraints grid_y0_x4_w1 = new GridBagConstraints();
			grid_y0_x4_w1.gridy = 0;
			grid_y0_x4_w1.gridx = 4;
			grid_y0_x4_w1.gridwidth = 1;
			grid_y0_x4_w1.weightx = 0.0;
			grid_y0_x4_w1.anchor = GridBagConstraints.WEST;
			grid_y0_x4_w1.fill = GridBagConstraints.HORIZONTAL;
			grid_y0_x4_w1.insets = INSETS_0_0_0_0;
			fontPathPanel.add(FontPathField, grid_y0_x0_w4);
			fontPathPanel.add(SettingFontPathButton, grid_y0_x4_w1);
		}
		return fontPathPanel;
	}

	private JPanel getExperimentPanel(){
		if(experimentPanel==null){
			experimentPanel = new JPanelHideable("experimental","実験的設定（仮）", Color.blue);
			fontWidthFixCheckBox.setText("フォント調整　幅％");
			fontWidthFixCheckBox.setForeground(Color.blue);
			GridBagConstraints grid20_x0_y0 = new GridBagConstraints();
			grid20_x0_y0.gridx = 0;
			grid20_x0_y0.gridy = 0;
			grid20_x0_y0.gridwidth = 1;
			//grid20_x0_y0.gridheight = 1;
			grid20_x0_y0.weightx = 0.0;
			//grid20_x0_y0.weighty = 0.0;
			grid20_x0_y0.anchor = GridBagConstraints.NORTHWEST;
			grid20_x0_y0.fill = GridBagConstraints.NONE;
			grid20_x0_y0.insets = INSETS_0_0_0_0;
			experimentPanel.add(fontWidthFixCheckBox, grid20_x0_y0);
			fontWidthRatioTextField.setText("100");
			fontWidthRatioTextField.setForeground(Color.blue);
			GridBagConstraints grid20_x1_y0 = new GridBagConstraints();
			grid20_x1_y0.gridx = 1;
			grid20_x1_y0.gridy = 0;
			grid20_x1_y0.weightx = 0.5;
			grid20_x1_y0.gridwidth = 1;
			grid20_x1_y0.anchor = GridBagConstraints.NORTHWEST;
			grid20_x1_y0.fill = GridBagConstraints.HORIZONTAL;
			grid20_x1_y0.insets = INSETS_0_0_0_0;
			experimentPanel.add(fontWidthRatioTextField, grid20_x1_y0);
			//fontHeightRatioLabel.setText("倍率（％）");
			//fontHeightRatioLabel.setForeground(Color.blue);
			fontHeightFixCheckBox.setText("高％");
			fontHeightFixCheckBox.setForeground(Color.blue);
			GridBagConstraints grid20_x0_y1 = new GridBagConstraints();
			grid20_x0_y1.gridx = 2;
			grid20_x0_y1.gridy = 0;
			grid20_x0_y1.weightx = 0.0;
			grid20_x0_y1.gridwidth = 1;
			grid20_x0_y1.anchor = GridBagConstraints.NORTHWEST;
			grid20_x0_y1.fill = GridBagConstraints.NONE;
			grid20_x0_y1.insets = INSETS_0_0_0_0;
			experimentPanel.add(fontHeightFixCheckBox, grid20_x0_y1);
			fontHeightRatioTextField.setText("100");
			fontHeightRatioTextField.setForeground(Color.blue);
			GridBagConstraints grid20_x1_y1 = new GridBagConstraints();
			grid20_x1_y1.gridx = 3;
			grid20_x1_y1.gridy = 0;
			grid20_x1_y1.anchor = GridBagConstraints.NORTHWEST;
			grid20_x1_y1.fill = GridBagConstraints.HORIZONTAL;
			grid20_x1_y1.weightx = 0.5;
			grid20_x1_y1.gridwidth = 2;
			grid20_x1_y1.insets = INSETS_0_0_0_5;
			experimentPanel.add(fontHeightRatioTextField,grid20_x1_y1);
			disableOriginalResizeCheckBox.setText("開発版を有効");
			disableOriginalResizeCheckBox.setForeground(Color.blue);
			disableOriginalResizeCheckBox.setToolTipText("Windows7基準");
			GridBagConstraints grid20_x0_y7 = new GridBagConstraints();
			grid20_x0_y7.gridx = 0;
			grid20_x0_y7.gridy = 7;
			//grid20_x0_y7.gridwidth = 5;
			//grid20_x0_y7.gridheight = 1;
			//grid20_x0_y7.weightx = 1.0;
			//grid20_x0_y7.weighty = 0.0;
			grid20_x0_y7.anchor = GridBagConstraints.NORTHWEST;
			grid20_x0_y7.fill = GridBagConstraints.HORIZONTAL;
			grid20_x0_y7.insets = INSETS_0_0_0_0;
			experimentPanel.add(disableOriginalResizeCheckBox, grid20_x0_y7);
			enableHtml5CommentCheckBox.setText("html5モード");
			enableHtml5CommentCheckBox.setForeground(Color.blue);
			enableHtml5CommentCheckBox.setToolTipText("フォントサイズ/フォントコマンド対応。Windows10基準。調整中");
			GridBagConstraints grid20_x1_y7 = new GridBagConstraints();
			grid20_x1_y7.gridx = 1;
			grid20_x1_y7.gridy = 7;
			grid20_x1_y7.gridwidth = 1;
			grid20_x1_y7.weightx = 0.0;
			grid20_x1_y7.anchor = GridBagConstraints.NORTHWEST;
			grid20_x1_y7.fill = GridBagConstraints.HORIZONTAL;
			grid20_x1_y7.insets = INSETS_0_0_0_0;
			experimentPanel.add(enableHtml5CommentCheckBox, grid20_x1_y7);

			enableCA_CheckBox.setText("ＣＡフォント対応");
			enableCA_CheckBox.setForeground(Color.blue);
			enableCA_CheckBox.setToolTipText("多種類のフォントを使ってフォント変化を強制的に使用するようになります");
			GridBagConstraints drid20_x0_y8 = new GridBagConstraints();
			drid20_x0_y8.gridx = 0;
			drid20_x0_y8.gridy = 8;
			drid20_x0_y8.gridwidth = 1;
			drid20_x0_y8.weightx = 0.0;
			drid20_x0_y8.anchor = GridBagConstraints.NORTHWEST;
			drid20_x0_y8.fill = GridBagConstraints.HORIZONTAL;
			drid20_x0_y8.insets = INSETS_0_0_0_5;
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
			GridBagConstraints grid20_x2_y8 = new GridBagConstraints();
			grid20_x2_y8.gridx = 1;
			grid20_x2_y8.gridy = 8;
			grid20_x2_y8.anchor = GridBagConstraints.NORTHWEST;
			grid20_x2_y8.fill = GridBagConstraints.HORIZONTAL;
			grid20_x2_y8.insets = INSETS_0_0_0_0;
			experimentPanel.add(useExtraFontCheckBox,grid20_x2_y8);
			extraFontTextField.setForeground(Color.blue);
			GridBagConstraints grid20_x4_y8 = new GridBagConstraints();
			grid20_x4_y8.gridx = 2;
			grid20_x4_y8.gridy = 8;
			grid20_x4_y8.gridwidth = 3;
			grid20_x4_y8.weightx = 1.0;
			grid20_x4_y8.anchor = GridBagConstraints.NORTHWEST;
			grid20_x4_y8.fill = GridBagConstraints.HORIZONTAL;
			grid20_x4_y8.insets = INSETS_0_5_0_5;
			experimentPanel.add(extraFontTextField, grid20_x4_y8);
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
			grid20_x1_y12.gridwidth = 4;
			grid20_x1_y12.anchor = GridBagConstraints.WEST;
			grid20_x1_y12.fill = GridBagConstraints.HORIZONTAL;
			grid20_x1_y12.insets = INSETS_0_5_0_5;
			experimentPanel.add(extraModeField, grid20_x1_y12);

			GridBagConstraints c = new GridBagConstraints();
			c.gridy = 12;
			c.anchor = GridBagConstraints.SOUTHEAST;
			c.insets = INSETS_0_0_0_0;
			experimentPanel.add(experimentPanel.getHideLabel(), c);
		}
		return experimentPanel;
	}
	/**
	 * getUpdateInfoPanel()
	 */
	private JPanel getUpdateInfoPanel() {
		if(updateInfoPanel==null) {
			updateInfoPanel = new JPanelHideable("updateInfo","新機能情報", Color.blue);

			GridBagConstraints grid14_x1_y0 = new GridBagConstraints();
			grid14_x1_y0.gridx = 0;
			grid14_x1_y0.gridy = 0;
			grid14_x1_y0.gridwidth = 5;
			grid14_x1_y0.anchor = GridBagConstraints.NORTH;
			grid14_x1_y0.fill = GridBagConstraints.HORIZONTAL;
			grid14_x1_y0.insets = INSETS_0_5_0_5;
			updateInfoPanel.add(
				new JLabel("新機能を全てオフにすると以前と同じ。長時間ローカル変換等はオフで試して下さい"),
				grid14_x1_y0);

			nmmNewEnableCheckBox.setText("NM動画に少し対応");
			nmmNewEnableCheckBox.setForeground(Color.blue);
			nmmNewEnableCheckBox.setToolTipText("フォント、ビデオクリップ、テキスト、アクションスクリプト未対応");
			nmmNewEnableCheckBox.setEnabled(true);
			GridBagConstraints grid14_x0_y1 = new GridBagConstraints();
			grid14_x0_y1.gridx = 0;
			grid14_x0_y1.gridy = 1;
			grid14_x0_y1.gridwidth = 1;
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

			fpsFixPanel = new JPanel();
			fpsFixPanel.setLayout(new GridBagLayout());
			fpsUpCheckBox.setText("fps変更");
			fpsUpCheckBox.setForeground(Color.blue);
			fpsUpCheckBox.setEnabled(true);
			fpsUpCheckBox.setToolTipText("フレームレートが低い動画のfps変更。");
			GridBagConstraints grid14_x0_y3 = new GridBagConstraints();
			grid14_x0_y3.gridx = 0;
			grid14_x0_y3.anchor = GridBagConstraints.NORTH;
			grid14_x0_y3.fill = GridBagConstraints.HORIZONTAL;
			grid14_x0_y3.insets = INSETS_0_5_0_5;
			fpsFixPanel.add(fpsUpCheckBox, grid14_x0_y3);

			GridBagConstraints grid14_x1_y3 = new GridBagConstraints();
			grid14_x1_y3.gridx = 1;
			grid14_x1_y3.anchor = GridBagConstraints.CENTER;
			grid14_x1_y3.fill = GridBagConstraints.HORIZONTAL;
			grid14_x1_y3.insets = INSETS_0_0_0_0;
			fpsFixPanel.add(new JLabel("最小(fps)"), grid14_x1_y3);

			fpsMinTextField = new JTextField();
			fpsMinTextField.setText("23");
			fpsMinTextField.setForeground(Color.blue);
			GridBagConstraints grid14_x2_y3 = new GridBagConstraints();
			grid14_x2_y3.gridx = 2;
			grid14_x2_y3.weightx = 1.0;
			grid14_x2_y3.anchor = GridBagConstraints.NORTH;
			grid14_x2_y3.fill = GridBagConstraints.HORIZONTAL;
			grid14_x2_y3.insets = INSETS_0_0_0_5;
			fpsFixPanel.add(fpsMinTextField, grid14_x2_y3);

			GridBagConstraints grid14_x3_y3 = new GridBagConstraints();
			grid14_x3_y3.gridx = 3;
			grid14_x3_y3.anchor = GridBagConstraints.CENTER;
			grid14_x3_y3.fill = GridBagConstraints.HORIZONTAL;
			grid14_x3_y3.insets = INSETS_0_0_0_0;
			fpsFixPanel.add(new JLabel("変換(fps)"), grid14_x3_y3);

			fpsUpTextFiled = new JTextField();
			fpsUpTextFiled.setText("25");
			fpsUpTextFiled.setForeground(Color.blue);
			GridBagConstraints grid14_x4_y3 = new GridBagConstraints();
			grid14_x4_y3.gridx = 4;
			grid14_x4_y3.weightx = 1.0;
			grid14_x4_y3.anchor = GridBagConstraints.NORTH;
			grid14_x4_y3.fill = GridBagConstraints.HORIZONTAL;
			grid14_x4_y3.insets = INSETS_0_0_0_0;
			fpsFixPanel.add(fpsUpTextFiled, grid14_x4_y3);

			fpsIntegralMultipleCheckBox.setText("整数倍");
			fpsIntegralMultipleCheckBox.setForeground(Color.blue);
			fpsIntegralMultipleCheckBox.setToolTipText("変更後のfpsを元動画のfpsの整数倍に補正する。");
			GridBagConstraints grid14_x5_y3 = new GridBagConstraints();
			grid14_x5_y3.gridx = 5;
			grid14_x5_y3.anchor = GridBagConstraints.CENTER;
			grid14_x5_y3.fill = GridBagConstraints.HORIZONTAL;
			grid14_x5_y3.insets = INSETS_0_0_0_0;
			fpsFixPanel.add(fpsIntegralMultipleCheckBox, grid14_x5_y3);

			GridBagConstraints grid14_x0_y3_2 = new GridBagConstraints();
			grid14_x0_y3_2.gridx = 0;
			grid14_x0_y3_2.gridy = 3;
			grid14_x0_y3_2.gridwidth = 4;
			grid14_x0_y3_2.weightx = 1.0;
			grid14_x0_y3_2.anchor = GridBagConstraints.NORTH;
			grid14_x0_y3_2.fill = GridBagConstraints.HORIZONTAL;
			grid14_x0_y3_2.insets = INSETS_0_0_0_5;
			updateInfoPanel.add(fpsFixPanel,grid14_x0_y3_2);

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

			GridBagConstraints grid14_hide = new GridBagConstraints();
			grid14_hide.anchor = GridBagConstraints.BELOW_BASELINE_TRAILING;
			grid14_hide.gridy = 6;
			updateInfoPanel.add(updateInfoPanel.getHideLabel(),grid14_hide);
		}
		return updateInfoPanel;
	}

	private void mainFrame_loginCheck(JLabel status) {
		sendLoginStatus(status, "ログインチェック中");
		BrowserInfo.resetBrowserInfo();
		Path file = Path.mkTemp("top");
		String url = "https://www.nicovideo.jp";
		Loader loader = new Loader(getSetting(),
			new JLabel[]{status, elapsedTimeBar, new JLabel()},log,html5CheckBox.isSelected());
		if (loader.load(url, file) || loader.isLoggedIn()){
			appendLoginStatus(status, "　ログイン済み");
			file.delete();
			loader.getIsHtml5();
			return;
		}
		appendLoginStatus(status, "　ログインしていません");
	}

	private void sendLoginStatus(final JLabel label, final String mes, final boolean is_append){
		if(label!=null){
			if(SwingUtilities.isEventDispatchThread()){
				label.setText(is_append? label.getText()+mes :mes );
			}else{
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						label.setText(is_append? label.getText()+mes :mes );
					}
				});
			}
		}
	}
	private void sendLoginStatus(final JLabel label, final String st){
		sendLoginStatus(label, st, false);
	}
	private void appendLoginStatus(final JLabel label, final String st){
		sendLoginStatus(label, st, true);
	}
	public void sendLoginStatus(String st){
		sendLoginStatus(loginStatusLabel, st);
	}

	private JPanel getManagentPanel() {
		if(managementPanel ==null){

			managementPanel = new JPanel();
			managementPanel.setLayout(new GridBagLayout());

			managementControl = new JPanel();
			managementControl.setLayout(new GridBagLayout());
			managementControl.setBorder(BorderFactory.createTitledBorder(
					CREATE_ETCHED_BORDER,
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
			SpinnerNumberModel model = new SpinnerNumberModel(0, 0, null, 1);
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
					//convertManager.sendTimeInfo();
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
			grid415.insets = INSETS_0_0_0_0;
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
			grid414.insets = INSETS_0_0_0_5;
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
						statusBar.setText("予約履歴を保存しました");
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
						statusBar.setText("予約履歴保存失敗");
					}
				}
			});
			managementControl.add(AllSaveButton, grid414);

			GridBagConstraints grid416 = new GridBagConstraints();
			grid416.gridx = 12;
			grid416.gridy = 1;
			grid416.gridwidth = 2;
			grid416.gridheight = 1;
			grid416.weightx = 0.0;
			grid416.anchor = GridBagConstraints.WEST;
			grid416.fill = GridBagConstraints.HORIZONTAL;
			grid416.insets = INSETS_0_0_0_0;
			showIconThumbnailCheckBox.setText("サムネ表示");
			managementControl.add(showIconThumbnailCheckBox,grid416);
			managementControl.add(new JPanel(),
				new GridBagConstraints(14, 1, 1, 1, 1.0, 1, GridBagConstraints.WEST,
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
			activities = new ActivityControl();
			managementPanel.add(activities.getVisiblePane(), grid41);

			errorStatusPanel = new JPanel();
			errorUrlLabel = new JLabel(" ");
			errorUrlLabel.setForeground(Color.blue);
			ecoUrlLabel = new JLabel(" ");
			errorUrlLabel.setForeground(Color.red);
			errorControl = new ErrorControl(errorUrlLabel,ecoUrlLabel);
			errorResetUrlButton = new JButton("再登録");
			errorResetUrlButton.setForeground(Color.BLUE);
			errorResetUrlButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					StringBuffer vlist = new StringBuffer(errorControl.getList());
					if(!vlist.substring(0).trim().isEmpty()){
						myListGetterDone(vlist, log);
						errorControl.clearData();
						convertManager.clearError();
					}
					StringBuffer evlist = new StringBuffer(errorControl.getEcoList());
					if(!evlist.substring(0).trim().isEmpty()){
						myListGetterDone(evlist, log);
						errorControl.clearEco();
					}
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
			errorUrlPanel = new JPanel();
			errorUrlPanel.setLayout(new BorderLayout());
			errorUrlLabel.setForeground(Color.blue);
			errorUrlPanel.add(errorUrlLabel, BorderLayout.NORTH);
 			ecoUrlLabel.setForeground(Color.gray);
 			errorUrlPanel.add(ecoUrlLabel, BorderLayout.SOUTH);
			errorStatusPanel.setLayout(new BorderLayout());
			errorStatusPanel.add(
				new JLabel("<html><font color=blue>エラーID  <br><font color=gray>エコ成功  </html>"),
				BorderLayout.WEST);
			errorStatusPanel.add(errorUrlPanel, BorderLayout.CENTER);
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
					autoPlay.setBack();
				}
			});
			playVideoNextButton = new JButton("次");
			playVideoNextButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					autoPlay.setNext();
				}
			});
			playVideoPlayButton = new JButton("再生");
			playVideoPlayButton.setForeground(Color.BLUE);
			playVideoPlayButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					autoPlay.playVideoNow();
				}
			});
			autoPlay = new AutoPlay(autoPlayCheckBox,playVideoLabel,null,statusBar);
			playVideoButtonPanel = new JPanel();
			playVideoButtonPanel.setLayout(new BorderLayout());
			playVideoButtonPanel.add(playVideoBackButton, BorderLayout.WEST);
			playVideoButtonPanel.add(playVideoPlayButton, BorderLayout.CENTER);
			playVideoButtonPanel.add(playVideoNextButton, BorderLayout.EAST);
			playChoicedPanel = new JPanel();
			playChoicedPanel.setLayout(new BorderLayout());
			playChoicedPanel.add(autoPlay2CheckBox,BorderLayout.WEST);
			playChoicedPanel.add(autoPlay.getChoiceLabel(), BorderLayout.CENTER);
			playVideoPanel = new JPanel();
			playVideoPanel.setLayout(new BorderLayout());
			playVideoPanel.add(playChoicedPanel, BorderLayout.WEST);
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
		activities.removeAll();
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

		activities.setPopup(VideoID_TextField);
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

		activities.addDropTarget(VideoID_TextField, false);
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
	JPanelHideable VhookPathSettingPanel;
	JPanel FFmpegPathSettingPanel = new JPanel();
//	JLabel FFmpegPathLabel = new JLabel();
//	JLabel OptionPathLabel = new JLabel();
//	GridBagLayout gridBagLayout7 = new GridBagLayout();
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
	JPanelHideable VhookSettingPanel;
	JLabel commentEraseTypeLabel = new JLabel();
	JPanel FFmpegSettingPanel = new JPanel();
	JPanel WideFFmpegSettingPanel = new JPanel();
	private JPanel zqPlayerModePanel = new JPanel();
	private JCheckBox zqPlayerModeCheckBox = new JCheckBox();
	private JTextField zqAdditionalOptionFiled = new JTextField();
	private JCheckBox zqMetadataCheckBox = new JCheckBox();
	private JTextField zqMetadataField = new JTextField();
	private JTextField zqExtOptionField = new JTextField();
	private JTextField zqMainOptionField = new JTextField();
	private JTextField zqCommandLineInOptionField = new JTextField();
	private JTextField zqCommandLineOutOptionField = new JTextField();
	private JTextField zqVhookPathField = new JTextField();
	private JButton zqSettingVhookPathButton = new JButton();
	private JPanel zqFFmpegSettingPanel = new JPanel();
	private JComboBox<FFmpegSelectedItem> zqFFmpegOptionComboBox = null;
	private JButton zqFFmpegOptionReloadButton = null;
	private JPanel zqFFmpegOptionComboBoxPanel = null;
	private JTextArea zqOptionFileDescription = new JTextArea("",2,20);

//	JLabel FontPathLabel;
	JTextField FontPathField = new JTextField();
	JButton SettingFontPathButton = new JButton();
	JCheckBox ShowConvVideoCheckBox = new JCheckBox();
	JCheckBox videoThumbnailSizeCheckBox = new JCheckBox();
	JCheckBox showIconThumbnailCheckBox = new JCheckBox();
	JTextField CommandLineOutOptionField = new JTextField();

	final int DIALOG_OK = JFileChooser.APPROVE_OPTION;
	private int showSaveDialog(String title, JTextField field, boolean isSave,
			boolean isDir) {
		String name = field.getText();
		File file;
		if(name!=null && !name.isEmpty())
			file = new File(name);
		else
			file = null;
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
		return code;
	}
	private String getRelativePath(File file){
		return file.getAbsolutePath().replace(
				new File("").getAbsolutePath(), ".");
	}
	private int showSaveDialog(String title, JTextField field) {
		File file = new File(field.getText());
		if (!file.exists()){
			file = new File("");
		}
		JFileChooser chooser = new JFileChooser(file);
		chooser.setDialogTitle(title);
		int code = 0;
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		code = chooser.showOpenDialog(this);
		if (code == DIALOG_OK) {
		//	CurrentDir = chooser.getCurrentDirectory();
			field.setText(getRelativePath(chooser.getSelectedFile()));
		}
		return code;
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
			live_op_duration = Integer.valueOf(duration);
			if(live_op_duration <= 0)
				duration = "";
			else
				duration = "" + live_op_duration;
		} catch(NumberFormatException e){
			//log.printStackTrace(e);
			duration = "";
		}
		// duration -> settingへ
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
		String last_history = VideoID_TextField.getText();
		if(last_history==null || last_history.isEmpty())
			last_history = requestHistory.getLast();
		if(last_history==null)
			last_history = "";
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
			last_history,
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
			vposshift,
			preferSmileCheckBox.isSelected(),
			forceDmcDlCheckBox.isSelected(),
			enableRangeCheckBox.isSelected(),
			enableSeqResumeCheckBox.isSelected(),
			inhibitSmallCheckBox.isSelected(),
			autoFlvToMp4CheckBox.isSelected(),
			JPanelHideable.getHideMap(),
			commentEraseTypeComboBox.getSelectedIndex(),
			html5CheckBox.isSelected(),
			zqMetadataCheckBox.isSelected(),
			zqMetadataField.getText(),
			commentOffCheckbox.isSelected(),
			commentOffField.getText(),
			commentLineFeedCheckBox.isSelected(),
			commentLineFeedTextField.getText(),
			dateUserFirstCheckBox.isSelected(),
			ngEnableMultilinesCheckBox.isSelected(),
			layerControlCheckBox.isSelected(),
			resizeAdjustCheckBox.isSelected(),
			resizeAdjustField.getText(),
			fpsIntegralMultipleCheckBox.isSelected(),
			zqSizeMinField.getText(),
			zqSizeMaxField.getText(),
			zqFpsRangeField.getText(),
			enableHtml5CommentCheckBox.isSelected(),
			enableAutoHtml5CheckBox.isSelected(),
			shadowDefaultSetting[0],
			shadowDefaultSetting[1],
			jMenuCheckSize.isSelected(),
			errorControl.getEcoString(),
			jMenuClearErrorAtEnd.isSelected(),
			enableCommentJsonCheckBox.isSelected(),
			enableCommentXmlCheckBox.isSelected(),
			liveCommentMinVposCheckBox.isSelected(),
			liveCommentMinVposTextField.getText(),
			showIconThumbnailCheckBox.isSelected(),
			videoThumbnailSizeCheckBox.isSelected(),
			jMenuEnableUnicode.isSelected()
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
	private void setSetting(final ConvertingSetting setting){
		if(SwingUtilities.isEventDispatchThread()){
			doSetSetting(setting);
		}else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					doSetSetting(setting);
				}
			});
		}
	}
	private void doSetSetting(ConvertingSetting setting) {
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
		videoThumbnailSizeCheckBox.setSelected(setting.isVideoThumbnailSize());
		showIconThumbnailCheckBox.setSelected(setting.isShowThumbnail());
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
		FFmpegOptionReload();
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
		WideFFmpegOptionReload();
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
		zqFFmpegOptionReload();
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
		errorControl.setError(setting.getErrorList(),false);
		liveOparationDurationChangeCheckBox.setSelected(setting.changedLiveOperationDuration());
		liveCommentModeCheckBox.setSelected(setting.isForcedLiveComment());
		liveOperationDurationTextField.setText((setting.getLiveOperationDuration()));
		liveCommentVposShiftCheckBox.setSelected(setting.isEnableCommentVposShift());
		liveCommentVposShiftTextField.setText(setting.getCommentVposShiftString());
		preferSmileCheckBox.setSelected(setting.isSmilePreferable());
		forceDmcDlCheckBox.setSelected(setting.doesDmcforceDl());
		enableRangeCheckBox.setSelected(setting.canRangeRequest());
		enableSeqResumeCheckBox.setSelected(setting.canSeqResume());
		inhibitSmallCheckBox.setSelected(setting.isInhibitSmaller());
		autoFlvToMp4CheckBox.setSelected(setting.isAutoFlvToMp4());
		autoFlvToMp4CheckBox.setEnabled(false);
		initialPanelHideMapping = setting.getPanelHideMapping();
		JPanelHideable.setHideMap(initialPanelHideMapping);
		commentEraseTypeComboBox.setSelectedIndex(setting.getCommentEraseType());
		html5CheckBox.setSelected(setting.isHtml5());
		zqMetadataCheckBox.setSelected(setting.enableMetadata());
		zqMetadataField.setText(setting.getZqMetadataOption());
		commentOffCheckbox.setSelected(setting.isCommentOff());
		commentOffField.setText(setting.getCommentOff());
		commentLineFeedCheckBox.setSelected(setting.enableCommentLF());
		commentLineFeedTextField.setText(setting.getCommentLF());
		dateUserFirstCheckBox.setSelected(setting.isSetDateUserFirst());
		ngEnableMultilinesCheckBox.setSelected(setting.isNGenableML());
		layerControlCheckBox.setSelected(setting.isLayerControl());
		resizeAdjustCheckBox.setSelected(setting.isResizeAdjust());
		resizeAdjustAction(setting.isResizeAdjust());
		resizeAdjustField.setText(setting.getResizeAdjust());
		fpsIntegralMultipleCheckBox.setSelected(setting.isFpsIntegralMultiple());
		zqSizeMinField.setText(setting.getZqSizeMin());
		zqSizeMaxField.setText(setting.getZqSizeMax());
		zqFpsRangeField.setText(setting.getZqFpsFloor());
		enableHtml5CommentCheckBox.setSelected(setting.isHtml5Comment());
		enableAutoHtml5CheckBox.setSelected(setting.isAutoHtml5Comment());
		shadowDefaultSetting[0] = setting.getShadowDefault();
		shadowDefaultSetting[1] = setting.getHtml5ShadowDefault();
		jMenuCheckSize.setSelected(setting.isEnableCheckSize());
		errorControl.setEco(setting.getEcoList());
		jMenuClearErrorAtEnd.setSelected(setting.clearErrorAtEnd());
		enableCommentJsonCheckBox.setSelected(setting.enableCommentJson());
		enableCommentXmlCheckBox.setSelected(setting.enableCommentXml());
		liveCommentMinVposCheckBox.setSelected(setting.isEnableLiveCommentMinVpos());
		liveCommentMinVposTextField.setText(setting.getLivecommentMinVpos());
		setHtml5AutoDefault();
		terminateTimeout = setting.getTerminateTimeout();
		jMenuEnableUnicode.setSelected(setting.isEnableUnicode());
		if(isDebugModeSet())
			jMenuDebug.setSelected(true);
	}

	/**
	 * [ファイル|終了] アクションが実行されました。
	 *
	 * @param actionEvent
	 *            ActionEvent
	 */
	public void jMenuFileExit_actionPerformed(ActionEvent actionEvent) {
		ConvertingSetting setting = this.getSetting();
		if(setting.clearErrorAtEnd()){
			errorControl.save();
			errorControl.clear();
			setting = this.getSetting();
		}
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
	JTextField FontIndexField = new JTextField();
	JCheckBox normalFontCheckBox = new JCheckBox();
	JLabel VideoID_Label = new JLabel();
	JLabel WayBackLabel = new JLabel();
	JTextField WayBackField = new JTextField();
	JCheckBox dateUserFirstCheckBox = new JCheckBox();
	GridBagLayout gridBagLayout10 = new GridBagLayout();
	GridBagLayout gridBagLayout11 = new GridBagLayout();
	GridBagLayout gridBagLayout1 = new GridBagLayout();
	JPanel OpPanel = new JPanel();
	GridLayout gridLayout1 = new GridLayout();
	JRadioButton Conv_SaveFileRadioButton = new JRadioButton();
	JRadioButton Conv_SaveFolderRadioButton = new JRadioButton();
	JLabel videoFileMacroLabel = new JLabel();
	JLabel convFileMacroLabel = new JLabel();
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
	private JTextField NGWordTextField = new JTextField();
	private JTextField NGIDTextField = new JTextField();
	private JPanelHideable ProxyInfoPanel;
	private JTextField ProxyTextField = new JTextField();
	private JTextField ProxyPortTextField = new JTextField();
	private JCheckBox UseProxyCheckBox = new JCheckBox();
	private String saveUseProxy = "";
	private JCheckBox FixFontSizeCheckBox = new JCheckBox();
	private JCheckBox DelVideoCheckBox = new JCheckBox();
	private JCheckBox DelCommentCheckBox = new JCheckBox();
	private JCheckBox FixCommentNumCheckBox = new JCheckBox();
	private JCheckBox OpaqueCommentCheckBox = new JCheckBox();
	private JPanel VideoSaveInfoPanel = null;
	private JTabbedPane SaveInfoTabPaneEach = null;
	private JTabbedPane FFmpegInfoTabPaneEach = new JTabbedPane();
	private JPanel VideoSavingTabbedPanel = null;
	private JPanel ConvertedVideoSavingTabbedPanel = null;
	private JCheckBox NotAddVideoID_ConvVideoCheckBox = new JCheckBox();
	private JComboBox<FFmpegSelectedItem> FFmpegOptionComboBox = null;
	private JComboBox<FFmpegSelectedItem> WideFFmpegOptionComboBox = null;
	private JButton FFmpegOptionReloadButton = null;
	private JButton WideFFmpegOptionReloadButton = null;
	private JPanel FFmpegOptionComboBoxPanel = null;
	private JPanel WideFFmpegOptionComboBoxPanel = null;
	private JLabel ngCommandLabel = new JLabel();
	private JTextField ngCommandField;
//	private JLabel replaceCommandLabel;
//	private JTextField replaceCommandField;
	private final JRadioButton sharedNgNoneRadioButton = new JRadioButton();
	private final JRadioButton sharedNgLowRadioButton = new JRadioButton();
	private final JRadioButton sharedNgMediumRadioButton = new JRadioButton();
	private final JRadioButton sharedNgHighRadioButton = new JRadioButton();
	private JLabel sharedNgLabel = new JLabel();
	private JPanel sharedNgPanel;
	public static StringBuffer resultHistory = new StringBuffer("");
	private JPanel managementPanel;
	private JPanel managementControl = new JPanel();
//	private JPanel activityStatusPanel;
	private MylistGetter mylistGetter;
	private StringBuffer movieList;
	private int numThread;
	private LinkedHashMap<JButton, ConvertStopFlag> buttonTable = new LinkedHashMap<>();
	private ConvertManager convertManager;
	private String Tag;
	private String watchInfo;
	private boolean PendingMode;
	private JCheckBox commentOffCheckbox;
	private JTextField commentOffField;
	private JCheckBox layerControlCheckBox;

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
			//String watchinfo = ss.length>2 ?ss[2] : "";
			// idを登録
			url = vid;
			OneLineMode = getSetting().isOneLineMode();
			requestHistory.add(url);
			int indexNow = convNo++;
			JButton stopButton = new JButton();
			ConvertStopFlag stopFlag =
				new ConvertStopFlag(stopButton,"停","待","終", "変", pending);
			ListInfo listInfo = new ListInfo(vid+"_"+title,OneLineMode,indexNow,
				stopButton, stopFlag);
			activities.add(listInfo);
			stopButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					stopButton_actionPerformed(e);
				}
			});
			//log.println(">"+indexNow+"個目の要求: "+vid);
			sendtext(">"+indexNow+"個目の要求: "+vid);
			listInfo.resetVid();
			buttonTable.put(stopButton, stopFlag);
			ConvertingSetting setting1 = getSetting();
			// ConverManager処理を要求
			convertManager.request(
				indexNow,
				numThread,
				url,
				WayBackField.getText(),
				setting1,
				listInfo.getStatus(),
				stopFlag,
				this,
				autoPlay,
				errorControl,
				vList,
				log);
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
			SelfTerminate.restartTimer2();
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
			if(url==null || url.isEmpty()){
				sendtext("URL/IDが入力されていません");
				log.println("変換ボタンが押されたがURL/ID欄が入力されていません");
				return;
			}
			input_url = url;
			requestHistory.add(url);
			/*
			 * URL解析
			 */
			boolean isLocal = checkLocal(url);
			if(isLocal){
				MainTabbedPane.setSelectedComponent(SavingInfoTabPanel);
				SaveInfoTabPaneEach.setSelectedComponent(VideoSavingTabbedPanel);
				return;
			}
			boolean isMylist = parseUrlMylist();
			// 入力データは ?以降が watchinfo それ以前が urlに Tagは url最後の/より後
			String vid = isMylist? url : Tag;
			log.println("url="+url+", watchinfo="+watchInfo+", Tag="+Tag);
			managementPanel.addNotify();
			MainTabbedPane.setSelectedComponent(managementPanel);
			int indexNow = convNo++;
			JButton stopButton = new JButton();
			ConvertStopFlag stopFlag =
				new ConvertStopFlag(stopButton,"停","待","終", "変", PendingMode);
			ListInfo listInfo = new ListInfo(vid,OneLineMode,indexNow,stopButton,stopFlag);
			activities.add(listInfo);
			VideoID_TextField.setText("");
			stopButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					stopButton_actionPerformed(e);
				}
			});
			//log.println(">"+indexNow+"個目の要求: "+vid);
			sendtext(">"+indexNow+"個目の要求: "+vid);
			listInfo.resetVid();
			buttonTable.put(stopButton, stopFlag);
			ConvertingSetting setting1 = getSetting();
			if (isMylist){
				//マイリストページ動画ID解析
				// url = "https://www/nicovideo.jp/mylist/1234567?watch_harmful=1" など
				mylistHistory.add(input_url);
				movieList = new StringBuffer();
				mylistGetter = new MylistGetter(
					indexNow,
					url,
					watchInfo,
					this,
					listInfo.getStatus(),
					stopFlag,
					errorControl,
					movieList,
					log);
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
					Tag+watchInfo,
					WayBackField.getText(),
					setting1,
					listInfo.getStatus(),
					stopFlag,
					this,
					autoPlay,
					errorControl,
					sbret,
					log);
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
				String extension = Path.getExtention(localFile).toLowerCase();
				if(extension.length()>=3 && ".mp4.flv.avi.f4v.wmv.mpg.mpeg.webm".contains(extension)){
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
		if (url.startsWith("http:"))
			url = url.replace("http:", "https:");				
		if(url.startsWith(VIDEO_URL_PARSER)){
			url = url.substring(VIDEO_URL_PARSER.length());
		}else if(url.startsWith("https://www.nicovideo.jp/" + MY_MYLIST)
				||url.startsWith(MY_MYLIST)){
			int index = url.indexOf("/#/");
			if(index < 0){
				url = "https://www.nicovideo.jp/api/deflist/list";
			}else{
				url = "https://www.nicovideo.jp/api/mylist/list?group_id="+url.substring(index+3);
				//url = url.replace("my/mylist/#/","mylist/");
			}
		}else if(!url.startsWith("http")){
			if(	  url.startsWith("mylist/")
				||url.startsWith("user/")
				||url.startsWith("my/")
				||url.startsWith("watch/")
				||url.startsWith("search/")
				||url.startsWith("tag/")){
				url = "https://www.nicovideo.jp/" + url;	//may not work
			//}else if(url.startsWith("lv")){
			//	url = "https://live.nicovideo.jp/watch/"+ url;	//may not work
			//}else if(url.startsWith("co")){
			//	url = "https://com.nicovideo.jp/watch/" + url;	//may not work
			}
		}
		if(url.startsWith("https://www.nicovideo.jp/tag/")
			||url.startsWith("https://www.nicovideo.jp/search/")){
			String keyword = url.replaceFirst("https://www.nicovideo.jp/(search|tag)/", "");
			String param = "";
			int index = keyword.indexOf('?');
			if (index > 0){
				param = keyword.substring(index+1);
				keyword = keyword.substring(0, index);
			}
			if(!keyword.contains("%")){
				try {
					String ek = URLEncoder.encode(keyword, "UTF-8");
					url = url.replace(keyword, ek);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			if(param.isEmpty())
				return url;
			//
			String SEARCH_KEY = "nvfmrlhNVFMRLH";
			String param2 = "";
			if(param.startsWith("&") && param.length()>=2){
				char sort = param.charAt(1);
				int k = SEARCH_KEY.indexOf(sort);
				char order = 'd';
				if(k>=0){
					if(k>=7){
						sort = Character.toLowerCase(sort);
						order = 'a';
					}
					param2 = "sort="+sort+"&order="+order;
					String page = param.substring(2);
					if(page.contains(" "))
						page = page.replaceAll(" .*", "");
					if(!page.isEmpty()){
						int p = 0;
						try {
							p = Integer.decode(page);
							if(p>=1 && p <=99){
								param2 += "&page="+p;
							}
						}catch(NumberFormatException e){
						}
					}
					url = url.replace(param, param2);
					log.println("url: "+url);
					return url;
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
			watchInfo = url.substring(index);
			url = url.substring(0, index);
		}else{
			watchInfo = "";
		}
		int index2 = url.lastIndexOf('/');
		Tag = url.substring(index2+1);
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

	/* readme表示 */
	public void showReadme_actionPerformed(String readmePath, String encoding){
		HtmlView hv;
		String text = "ファイルが見つかりません.";
		try{
			String docfile = new File("doc"+File.separator+readmePath).getPath();
			text = Path.readAllText(docfile, encoding);
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
			url = VideoID_TextField.getText();
			if(url==null || url.isEmpty()){
				sendtext("URL/IDが入力されていません");
				log.println("変換ボタンが押されたがURL/ID欄が入力されていません");
				return;
			}
			ConvertWorker conv = new ConvertWorker(
					0,
					url,
					WayBackField.getText(),
					setting,
					new JLabel[]{statusBar,new JLabel(),new JLabel(), new JLabel()},
					new ConvertStopFlag(new JButton()),
					this,
					autoPlay,
					convertManager,
					errorControl,
					new StringBuffer(),
					Logger.MainLog);
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

	ArrayList<String> execFFmpeg(String parameter)
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
	public void ShowSavingConvertedVideoDialogButton_actionPerformed(ActionEvent e) {
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

	public void ShowSavingConvertedVideoFolderDialogButton_actionPerformed(ActionEvent e) {
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
			ConvertingSettingPanel.add(getExperimentPanel(), grid_x0_y2_0);
			ConvertingSettingPanel.add(getNGWordSettingPanel(), grid_x0_y1_11);
			ConvertingSettingPanel.add(getVhookSettingPanel(), grid_x0_y0_0);
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
			GridBagConstraints gridy4_x0_12 = new GridBagConstraints();
			gridy4_x0_12.gridx = 0;
			gridy4_x0_12.gridy = 0;
			gridy4_x0_12.anchor = GridBagConstraints.WEST;
			gridy4_x0_12.insets = INSETS_0_5_0_5;
			GridBagConstraints gridy4_x1_13 = new GridBagConstraints();
			gridy4_x1_13.gridx = 1;
			gridy4_x1_13.gridy = 0;
			gridy4_x1_13.insets = INSETS_0_0_0_0;
			GridBagConstraints gridy4_x2_14 = new GridBagConstraints();
			gridy4_x2_14.gridx = 2;
			gridy4_x2_14.gridy = 0;
			gridy4_x2_14.insets = INSETS_0_0_0_0;
			GridBagConstraints gridy4_x3_15 = new GridBagConstraints();
			gridy4_x3_15.gridx = 3;
			gridy4_x3_15.gridy = 0;
			gridy4_x3_15.insets = INSETS_0_0_0_0;
			GridBagConstraints gridy4_x4_16 = new GridBagConstraints();
			gridy4_x4_16.gridx = 4;
			gridy4_x4_16.gridy = 0;
			gridy4_x4_16.weightx = 1.0;
			gridy4_x4_16.anchor = GridBagConstraints.WEST;
			gridy4_x4_16.insets = INSETS_0_0_0_0;

			sharedNgPanel = new JPanel();
			sharedNgPanel.setLayout(new GridBagLayout());
			sharedNgHighRadioButton.setText("強");
			sharedNgMediumRadioButton.setText("中");
			sharedNgLowRadioButton.setText("弱");
			sharedNgNoneRadioButton.setText("無し");
			sharedNgPanel.add(sharedNgHighRadioButton, gridy4_x1_13);
			sharedNgPanel.add(sharedNgMediumRadioButton, gridy4_x2_14);
			sharedNgPanel.add(sharedNgLowRadioButton, gridy4_x3_15);
			sharedNgPanel.add(sharedNgNoneRadioButton, gridy4_x4_16);
			sharedNgScore.add(sharedNgHighRadioButton,SharedNgScore.HIGH);
			sharedNgScore.add(sharedNgMediumRadioButton,SharedNgScore.MEDIUM);
			sharedNgScore.add(sharedNgLowRadioButton,SharedNgScore.LOW);
			sharedNgScore.add(sharedNgNoneRadioButton,SharedNgScore.NONE);
			sharedNgNoneRadioButton.setSelected(true);

			GridBagConstraints grid_x0_y0_2 = new GridBagConstraints();
			grid_x0_y0_2.gridx = 0;
			grid_x0_y0_2.gridy = 0;
			grid_x0_y0_2.anchor = GridBagConstraints.WEST;
			grid_x0_y0_2.fill = GridBagConstraints.HORIZONTAL;
			grid_x0_y0_2.insets = INSETS_0_5_0_0;
			GridBagConstraints grid_x1_y0_3 = new GridBagConstraints();
			grid_x1_y0_3.gridx = 1;
			grid_x1_y0_3.gridy = 0;
			grid_x1_y0_3.gridwidth = 4;
			grid_x1_y0_3.weightx = 1.0;
			grid_x1_y0_3.fill = GridBagConstraints.HORIZONTAL;
			grid_x1_y0_3.insets = INSETS_0_5_0_0;
			GridBagConstraints grid_x5_y0_4 = new GridBagConstraints();
			grid_x5_y0_4.gridx = 5;
			grid_x5_y0_4.gridy = 0;
			grid_x5_y0_4.gridwidth = 1;
			grid_x5_y0_4.weightx = 0.0;
			grid_x5_y0_4.anchor = GridBagConstraints.EAST;
			grid_x5_y0_4.fill = GridBagConstraints.HORIZONTAL;
			grid_x5_y0_4.insets = INSETS_0_0_0_0;
			GridBagConstraints grid_x0_y1_5 = new GridBagConstraints();
			grid_x0_y1_5.gridx = 0;
			grid_x0_y1_5.gridy = 1;
			grid_x0_y1_5.anchor = GridBagConstraints.WEST;
			grid_x0_y1_5.insets = INSETS_0_5_0_0;
			GridBagConstraints grid_x1_y1_7 = new GridBagConstraints();
			grid_x1_y1_7.gridx = 1;
			grid_x1_y1_7.gridy = 1;
			grid_x1_y1_7.gridwidth = 5;
			grid_x1_y1_7.weightx = 1.0;
			grid_x1_y1_7.fill = GridBagConstraints.HORIZONTAL;
			grid_x1_y1_7.insets = INSETS_0_5_0_5;
			GridBagConstraints grid_x0_y3_8 = new GridBagConstraints();
			grid_x0_y3_8.gridx = 0;
			grid_x0_y3_8.gridy = 3;
			grid_x0_y3_8.anchor = GridBagConstraints.WEST;
			grid_x0_y3_8.fill  = GridBagConstraints.HORIZONTAL;
			grid_x0_y3_8.insets = INSETS_0_5_0_0;
			GridBagConstraints grid_x1_y3_9 = new GridBagConstraints();
			grid_x1_y3_9.gridx = 1;
			grid_x1_y3_9.gridy = 3;
			grid_x1_y3_9.gridwidth = 5;
			grid_x1_y3_9.weightx = 1.0;
			grid_x1_y3_9.fill = GridBagConstraints.HORIZONTAL;
			grid_x1_y3_9.insets = INSETS_0_5_0_5;
			GridBagConstraints grid_x0_y5_17 = new GridBagConstraints();
			grid_x0_y5_17.gridx = 0;
			grid_x0_y5_17.gridy = 5;
			grid_x0_y5_17.gridwidth = 2;
			grid_x0_y5_17.weightx = 0.0;
			grid_x0_y5_17.anchor = GridBagConstraints.WEST;
			grid_x0_y5_17.fill = GridBagConstraints.HORIZONTAL;
			grid_x0_y5_17.insets = INSETS_0_0_0_0;
			GridBagConstraints grid_x1_y5_17b = new GridBagConstraints();
			grid_x1_y5_17b.gridx = 2;
			grid_x1_y5_17b.gridy = 5;
			grid_x1_y5_17b.gridwidth = 4;
			grid_x1_y5_17b.weightx = 1.0;
			grid_x1_y5_17b.anchor = GridBagConstraints.WEST;
			grid_x1_y5_17b.fill = GridBagConstraints.HORIZONTAL;
			grid_x1_y5_17b.insets = INSETS_0_0_0_0;
			GridBagConstraints grid_x0_y6_18 = new GridBagConstraints();
			grid_x0_y6_18.gridx = 0;
			grid_x0_y6_18.gridy = 6;
			grid_x0_y6_18.gridwidth = 2;
			grid_x0_y6_18.anchor = GridBagConstraints.WEST;
			grid_x0_y6_18.fill = GridBagConstraints.HORIZONTAL;
			grid_x0_y6_18.insets = INSETS_0_0_0_5;
			GridBagConstraints grid_x2_y6_19 = new GridBagConstraints();
			grid_x2_y6_19.gridx = 2;
			grid_x2_y6_19.gridy = 6;
			grid_x2_y6_19.gridwidth = 2;
			grid_x2_y6_19.weightx = 1.0;
			grid_x2_y6_19.anchor = GridBagConstraints.WEST;
			grid_x2_y6_19.fill = GridBagConstraints.HORIZONTAL;
			grid_x2_y6_19.insets = INSETS_0_5_0_0;
			GridBagConstraints grid_x4_y6_20 = new GridBagConstraints();
			grid_x4_y6_20.gridx = 4;
			grid_x4_y6_20.gridy = 6;
			grid_x4_y6_20.gridwidth = 1;
			grid_x4_y6_20.anchor = GridBagConstraints.EAST;
			grid_x4_y6_20.fill = GridBagConstraints.HORIZONTAL;
			grid_x4_y6_20.insets = INSETS_0_0_0_0;
			GridBagConstraints grid_x5_y6_21 = new GridBagConstraints();
			grid_x5_y6_21.gridx = 5;
			grid_x5_y6_21.gridy = 6;
			grid_x5_y6_21.anchor = GridBagConstraints.WEST;
			grid_x5_y6_21.fill = GridBagConstraints.HORIZONTAL;
			grid_x5_y6_21.insets = INSETS_0_0_0_5;

			NGWordSettingPanel = new JPanel();
			NGWordSettingPanel.setLayout(new GridBagLayout());
			NGWordSettingPanel.setBorder(BorderFactory.createTitledBorder(
					CREATE_ETCHED_BORDER,
					"NGワード・ID設定"));
			NGWordSettingPanel.add(new JLabel("NGワード"), grid_x0_y0_2);
			NGWordSettingPanel.add(NGWordTextField, grid_x1_y0_3);
			ngEnableMultilinesCheckBox.setText("複");
			ngEnableMultilinesCheckBox.setToolTipText(ngEnableMLToolchipTexts[0]);
			ngEnableMultilinesCheckBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JCheckBox src = ngEnableMultilinesCheckBox;
					if(e.getSource().equals((Object)src)){
						int sel = src.isSelected()? 1 : 0;
						src.setToolTipText(ngEnableMLToolchipTexts[sel]);
					}
				}
			});
			NGWordSettingPanel.add(ngEnableMultilinesCheckBox, grid_x5_y0_4);
			NGWordSettingPanel.add(new JLabel("NG ID"), grid_x0_y1_5);
			NGWordSettingPanel.add(NGIDTextField, grid_x1_y1_7);
			ngCommandLabel.setText("NGコマンド");
			ngCommandLabel.setForeground(Color.blue);
			NGWordSettingPanel.add(ngCommandLabel, grid_x0_y3_8);
			ngCommandField = new JTextField();
			ngCommandField.setForeground(Color.blue);
			NGWordSettingPanel.add(ngCommandField, grid_x1_y3_9);
			sharedNgLabel.setText("NG共有レベル ");
			sharedNgLabel.setForeground(Color.blue);
			NGWordSettingPanel.add(sharedNgLabel, grid_x0_y5_17);
			NGWordSettingPanel.add(sharedNgPanel, grid_x1_y5_17b);
			commentOffCheckbox = new JCheckBox("コメントオフエリア");
			commentOffCheckbox.setForeground(Color.blue);
			commentOffCheckbox.setToolTipText("サイズ 上から+,下から-。pixel数か動画サイズの%値かコメント行数(b,m,s+数字)。n付きはnakaコメントのみ");
			NGWordSettingPanel.add(commentOffCheckbox, grid_x0_y6_18);
			commentOffField = new JTextField("");
			commentOffField.setToolTipText("例:-b3n (下からbigサイズ3行分はコメントオフ,nakaコメントのみ)");
			NGWordSettingPanel.add(commentOffField, grid_x2_y6_19);
			layerControlCheckBox = new JCheckBox("レ");
			NGWordSettingPanel.add(layerControlCheckBox, grid_x4_y6_20);
			layerControlCheckBox.setToolTipText("レイヤー順:ue shitaコマンドを手前側に表示する");
			NGWordSettingPanel.add(new JLabel("イヤー順"), grid_x5_y6_21);
		}
		return NGWordSettingPanel;
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
			grid_x1_y2_12.insets = INSETS_0_5_0_5;
			grid_x1_y2_12.gridx = 2;
			GridBagConstraints grid_x0_y2_10 = new GridBagConstraints();
			grid_x0_y2_10.gridx = 1;
			grid_x0_y2_10.insets = INSETS_0_5_0_5;
			grid_x0_y2_10.gridy = 1;
			GridBagConstraints grid_x1_y1_9 = new GridBagConstraints();
			grid_x1_y1_9.fill = GridBagConstraints.BOTH;
			grid_x1_y1_9.gridy = 0;
			grid_x1_y1_9.weightx = 1.0;
			grid_x1_y1_9.insets = INSETS_0_5_5_5;
			grid_x1_y1_9.gridx = 2;
			GridBagConstraints grid_x0_y1_8 = new GridBagConstraints();
			grid_x0_y1_8.gridx = 1;
			grid_x0_y1_8.insets = INSETS_0_5_0_5;
			grid_x0_y1_8.fill = GridBagConstraints.NONE;
			grid_x0_y1_8.anchor = GridBagConstraints.WEST;
			grid_x0_y1_8.gridy = 0;
			ProxyInfoPanel = new JPanelHideable("ProxyInfo","プロキシ設定",Color.black);
			ProxyInfoPanel.add(new JLabel("プロキシ"), grid_x0_y1_8);
			ProxyInfoPanel.add(ProxyTextField, grid_x1_y1_9);
			ProxyInfoPanel.add(new JLabel("ポート番号"), grid_x0_y2_10);
			ProxyInfoPanel.add(ProxyPortTextField, grid_x1_y2_12);
			UseProxyCheckBox.setText("プロキシを使う");
			ProxyInfoPanel.add(UseProxyCheckBox, grid_x0_y0_13);
			GridBagConstraints c = new GridBagConstraints();
			c.gridy = 1;
			c.anchor = GridBagConstraints.SOUTHEAST;
			ProxyInfoPanel.add(ProxyInfoPanel.getHideLabel(),c);
		}
		return ProxyInfoPanel;
	}

	/**
	 * This method initializes VideoSaveInfoPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getVideoSaveInfoPanel() {
		if (VideoSaveInfoPanel == null) {
			GridBagConstraints grid_x0_y0_34 = new GridBagConstraints();
			grid_x0_y0_34.insets = INSETS_0_5_0_5;
			grid_x0_y0_34.gridy = 0;
			grid_x0_y0_34.fill = GridBagConstraints.HORIZONTAL;
			grid_x0_y0_34.gridx = 0;
			grid_x0_y0_34.gridwidth = 6;
			GridBagConstraints grid_x0_y1_15 = new GridBagConstraints();
			grid_x0_y1_15.fill = GridBagConstraints.HORIZONTAL;
			grid_x0_y1_15.gridwidth = 1;
			grid_x0_y1_15.gridx = 0;
			grid_x0_y1_15.gridy = 1;
			grid_x0_y1_15.weightx = 0.0;
			grid_x0_y1_15.anchor = GridBagConstraints.WEST;
			grid_x0_y1_15.insets = INSETS_0_25_0_5;
			GridBagConstraints grid_x1_y1_15b = new GridBagConstraints();
			grid_x1_y1_15b.fill = GridBagConstraints.HORIZONTAL;
			grid_x1_y1_15b.gridwidth = 1;
			grid_x1_y1_15b.gridx = 1;
			grid_x1_y1_15b.gridy = 1;
			grid_x1_y1_15b.weightx = 0.0;
			grid_x1_y1_15b.anchor = GridBagConstraints.WEST;
			grid_x1_y1_15b.insets = INSETS_0_0_0_5;
			GridBagConstraints grid_x2_y1_15_2 = new GridBagConstraints();
			grid_x2_y1_15_2.fill = GridBagConstraints.HORIZONTAL;
			grid_x2_y1_15_2.gridwidth = 2;
			grid_x2_y1_15_2.gridx = 2;
			grid_x2_y1_15_2.gridy = 1;
			grid_x2_y1_15_2.weightx = 1.0;
			grid_x2_y1_15_2.anchor = GridBagConstraints.WEST;
			grid_x2_y1_15_2.insets = INSETS_0_0_0_5;
			GridBagConstraints grid_x0_y2_27 = new GridBagConstraints(
					0, 2, 5, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, INSETS_0_25_0_5, 0, 0);
			GridBagConstraints grid_x0_y3_28 = new GridBagConstraints(
					0, 3, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, INSETS_0_50_0_5, 0, 0);
			GridBagConstraints grid_x2_y3 = new GridBagConstraints(
					2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, INSETS_0_0_0_0, 0, 0);
			GridBagConstraints grid_x3_y3_31 = new GridBagConstraints(
					3, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE, INSETS_0_0_0_5, 0, 0);
			GridBagConstraints grid_x0_y4_29 = new GridBagConstraints(
					0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, INSETS_0_25_0_5, 0, 0);
			GridBagConstraints grid_x1_y4_29b = new GridBagConstraints(
					3, 4, 2, 1, 1.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, INSETS_0_0_0_5, 0, 0);
			GridBagConstraints grid_x0_y5_30 = new GridBagConstraints(
					0, 5, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, INSETS_0_50_0_5, 0, 0);
			GridBagConstraints grid_x2_y5 = new GridBagConstraints(
					2, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, INSETS_0_0_0_0, 0, 0);
			GridBagConstraints grid_x3_y5_32 = new GridBagConstraints(
					3, 5, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE, INSETS_0_0_0_5, 0, 0);
			VideoSaveInfoPanel = new JPanel();
			VideoSaveInfoPanel.setLayout(new GridBagLayout());
			VideoSaveInfoPanel.setBorder(BorderFactory.createTitledBorder(
					CREATE_ETCHED_BORDER,
					"動画保存設定"
				));
			JPanel savingVideoSubPanel = new JPanel();
			savingVideoSubPanel.setLayout(new GridBagLayout());
			VideoSaveInfoPanel.add(savingVideoSubPanel, grid_x0_y0_34);
			savingVideoSubPanel.add(SavingVideoCheckBox, new GridBagConstraints());
			savingVideoSubPanel.add(disableEcoCheckBox, new GridBagConstraints());

			JPanel extraDownloadPanel0 = new JPanel();
			extraDownloadPanel0.setLayout(new BorderLayout());
			JLabel extraDownloadLabel = new JLabel();
			extraDownloadLabel.setText("拡張ダウンロード設定(dmc)");
			extraDownloadLabel.setForeground(Color.blue);
			extraDownloadLabel.setToolTipText("ページ情報設定タブに移動しました");
			extraDownloadPanel0.add(extraDownloadLabel, BorderLayout.CENTER);
			BasicArrowButton extraDownloadArrow = new BasicArrowButton(SwingConstants.SOUTH);
			extraDownloadArrow.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					openWatchPageSavingTabbedPanel();
				}
			});
			extraDownloadPanel0.add(extraDownloadArrow, BorderLayout.WEST);

			GridBagConstraints grid000_last = new GridBagConstraints();
			grid000_last.weightx = 1.0;
			grid000_last.fill = GridBagConstraints.NONE;
			savingVideoSubPanel.add(extraDownloadPanel0, grid000_last);

			DelVideoCheckBox.setText("変換後に動画削除");
			VideoSaveInfoPanel.add(DelVideoCheckBox, grid_x0_y1_15);
			VideoSaveInfoPanel.add(Video_SaveFolderRadioButton,
					grid_x0_y2_27);
			VideoSaveInfoPanel.add(VideoSavedFolderField, grid_x0_y3_28);
			VideoSaveInfoPanel.add(openVideoSaveFolderButton,grid_x2_y3);
			VideoSaveInfoPanel.add(ShowSavingVideoFolderDialogButton,
					grid_x3_y3_31);
			VideoSaveInfoPanel.add(Video_SaveFileRadioButton,
					grid_x0_y4_29);
			VideoSaveInfoPanel.add(videoFileMacroLabel, grid_x1_y4_29b);
			VideoSaveInfoPanel.add(VideoSavedFileField, grid_x0_y5_30);
			VideoSaveInfoPanel.add(openVideoSaveFileButton,grid_x2_y5);
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

			liveConvertInfoPanel = new JPanelHideable("liveConvert","生放送変換設定", Color.red);
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
			liveCommentMinVposCheckBox.setText("↑適用後のコメント表示最小VPOS(時刻)(秒)");
			liveCommentMinVposCheckBox.setForeground(Color.blue);
			liveCommentMinVposCheckBox.setToolTipText("コメント遅延補正後,最小値より時刻（VPOS）の早いコメントを表示しません。(マイナス可)");
			GridBagConstraints grid14_x0_y8 = new GridBagConstraints();
			grid14_x0_y8.gridx = 0;
			grid14_x0_y8.gridy = 8;
			grid14_x0_y8.gridwidth = 2;
			grid14_x0_y8.weightx = 0.0;
			grid14_x0_y8.anchor = GridBagConstraints.NORTHWEST;
			grid14_x0_y8.fill = GridBagConstraints.HORIZONTAL;
			grid14_x0_y8.insets = INSETS_0_5_0_5;
			liveConvertInfoPanel.add(liveCommentMinVposCheckBox,grid14_x0_y8);
			GridBagConstraints grid14_x2_y8 = new GridBagConstraints();
			grid14_x2_y8.gridx = 2;
			grid14_x2_y8.gridy = 8;
			grid14_x2_y8.gridwidth = 1;
			grid14_x2_y8.weightx = 1.0;
			grid14_x2_y8.anchor = GridBagConstraints.NORTHWEST;
			grid14_x2_y8.fill = GridBagConstraints.HORIZONTAL;
			grid14_x2_y8.insets = INSETS_0_5_0_5;
			liveConvertInfoPanel.add(liveCommentMinVposTextField,grid14_x2_y8);
			GridBagConstraints c = new GridBagConstraints();
			c.gridy = 8;
			c.anchor = GridBagConstraints.SOUTHEAST;
			liveConvertInfoPanel.add(liveConvertInfoPanel.getHideLabel(), c);

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

	private void openWatchPageSavingTabbedPanel(){
		MainTabbedPane.setSelectedComponent(SavingInfoTabPanel);
		SaveInfoTabPaneEach.setSelectedComponent(watchPageSavingTabbedPanel);
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
		grid_x0_y1.weighty = 0.0;
		watchPageSavingTabbedPanel.add(getFileNameInfoPanel(),grid_x0_y1);

		GridBagConstraints grid_x0_y2 = new GridBagConstraints();
		grid_x0_y2.gridx = 0;
		grid_x0_y2.gridy = 2;
		grid_x0_y2.anchor = GridBagConstraints.NORTH;
		grid_x0_y2.fill = GridBagConstraints.HORIZONTAL;
		grid_x0_y2.insets = INSETS_0_5_0_5;
		grid_x0_y2.weightx = 1.0;
		grid_x0_y2.weighty = 1.0;
		watchPageSavingTabbedPanel.add(getExtraDownloadInfoPanel(),grid_x0_y2);
		return watchPageSavingTabbedPanel;
	}

	/**
	 * getWatchPageSavingInfoPanel
	 * @return watchPageSavingTabbedPanel
	 */
	private JPanel getWatchPageSavingInfoPanel() {
		watchPageSavingInfoPanel = new JPanelHideable("PageSavingInfo","情報保存設定", Color.black);
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

		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 10;
		c.anchor = GridBagConstraints.SOUTHEAST;
		watchPageSavingInfoPanel.add(watchPageSavingInfoPanel.getHideLabel(), c);
		return watchPageSavingInfoPanel;
	}

	/**
	 * getfileNameInfoPanel
	 * @return
	 */
	private JPanel getFileNameInfoPanel(){
		fileNameInfoPanel = new JPanelHideable("fileNameInfo","保存ファイル名設定（特殊）", Color.red);
		GridBagConstraints gridbagc = new GridBagConstraints();
		JLabel fileNameInfoLabel = new JLabel();
		fileNameInfoLabel.setText("この設定を行うと自動で変換できない場合があります");
		fileNameInfoLabel.setForeground(Color.red);
		fileNameInfoLabel.setFont(new Font(Font.SERIF,Font.BOLD,new Font(null).getSize()+1));
		gridbagc.gridx = 0;
		gridbagc.gridy = 0;
		gridbagc.anchor = GridBagConstraints.WEST;
		gridbagc.fill = GridBagConstraints.HORIZONTAL;
		gridbagc.insets = INSETS_0_0_0_5;
		gridbagc.weightx = 1.0;
		fileNameInfoPanel.add(fileNameInfoLabel, gridbagc);

		changeMp4ExtCheckBox.setText("mp4保存動画の拡張子を.mp4にする（既定は.flv）");
		gridbagc = new GridBagConstraints();
		gridbagc.gridx = 0;
		gridbagc.gridy = 1;
		gridbagc.anchor = GridBagConstraints.WEST;
		gridbagc.fill = GridBagConstraints.HORIZONTAL;
		gridbagc.insets = INSETS_0_0_0_5;
		gridbagc.weightx = 1.0;
		fileNameInfoPanel.add(changeMp4ExtCheckBox, gridbagc);

		changeTitleIdCheckBox.setText("保存動画のIDをタイトルの後ろにつける（既定はタイトルの前）");
		gridbagc = new GridBagConstraints();
		gridbagc.gridx = 0;
		gridbagc.gridy = 2;
		gridbagc.anchor = GridBagConstraints.WEST;
		gridbagc.fill = GridBagConstraints.HORIZONTAL;
		gridbagc.insets = INSETS_0_0_0_5;
		gridbagc.weightx = 1.0;
		fileNameInfoPanel.add(changeTitleIdCheckBox, gridbagc);

		gridbagc = new GridBagConstraints();
		gridbagc.gridy = 2;
		gridbagc.anchor = GridBagConstraints.SOUTHEAST;
		fileNameInfoPanel.add(fileNameInfoPanel.getHideLabel(), gridbagc);
		return fileNameInfoPanel;
	}

	/**
	 * getExtraDownloadInfoPanel
	 * @return extraDownloadInfoPanel
	 */
	private JPanel getExtraDownloadInfoPanel(){
		extraDownloadInfoPanel = new JPanelHideable("extraDownload","拡張ダウンロード設定(dmc)", Color.blue);

		GridBagConstraints gridbagc = new GridBagConstraints();
		gridbagc.gridx = 0;
		gridbagc.gridy = 0;
		gridbagc.anchor = GridBagConstraints.WEST;
		gridbagc.fill = GridBagConstraints.HORIZONTAL;
		gridbagc.insets = INSETS_0_0_0_5;
		gridbagc.weightx = 1.0;
		preferSmileCheckBox.setText("smileサーバ強制ダウンロード");
		preferSmileCheckBox.setForeground(Color.blue);
		preferSmileCheckBox.setToolTipText("dmcサーバに有ってもsmileサーバから読みます。");
		preferSmileCheckBox.setEnabled(false);
		extraDownloadInfoPanel.add(preferSmileCheckBox, gridbagc);

		gridbagc = new GridBagConstraints();
		gridbagc.gridx = 0;
		gridbagc.gridy = 1;
		gridbagc.anchor = GridBagConstraints.WEST;
		gridbagc.fill = GridBagConstraints.HORIZONTAL;
		gridbagc.insets = INSETS_0_0_0_5;
		gridbagc.weightx = 1.0;
		forceDmcDlCheckBox.setText("dmcサーバ強制ダウンロード");
		forceDmcDlCheckBox.setForeground(Color.blue);
		forceDmcDlCheckBox.setToolTipText("dmcサーバ動画がサイズが小さくてもダウンロードします。変換に使うのはサイズの大きい方。");
		extraDownloadInfoPanel.add(forceDmcDlCheckBox, gridbagc);

		gridbagc = new GridBagConstraints();
		gridbagc.gridx = 0;
		gridbagc.gridy = 2;
		gridbagc.anchor = GridBagConstraints.WEST;
		gridbagc.fill = GridBagConstraints.HORIZONTAL;
		gridbagc.insets = INSETS_0_0_0_5;
		gridbagc.weightx = 1.0;
		enableRangeCheckBox.setText("dmc(R)ダウンロード(同時接続数2　非推奨。分割ダウンロード)");
	//	enableRangeCheckBox.setForeground(Color.blue);
		enableRangeCheckBox.setToolTipText("可能ならHTTP/1.1 Rangeヘッダーを使用する(同時接続数2)");
		extraDownloadInfoPanel.add(enableRangeCheckBox, gridbagc);

		gridbagc = new GridBagConstraints();
		gridbagc.gridx = 0;
		gridbagc.gridy = 3;
		gridbagc.anchor = GridBagConstraints.WEST;
		gridbagc.fill = GridBagConstraints.HORIZONTAL;
		gridbagc.insets = INSETS_0_0_0_5;
		gridbagc.weightx = 1.0;
		enableSeqResumeCheckBox.setText("dmc(S)ダウンロード(同時接続数1 resume付き。高速 )");
		enableSeqResumeCheckBox.setForeground(Color.blue);
		enableSeqResumeCheckBox.setToolTipText("可能ならSequentialResumeを行う(同時接続数1)");
		extraDownloadInfoPanel.add(enableSeqResumeCheckBox, gridbagc);

		gridbagc = new GridBagConstraints();
		gridbagc.gridx = 0;
		gridbagc.gridy = 4;
		gridbagc.anchor = GridBagConstraints.WEST;
		gridbagc.fill = GridBagConstraints.HORIZONTAL;
		gridbagc.insets = INSETS_0_0_0_5;
		gridbagc.weightx = 1.0;
		inhibitSmallCheckBox.setText("Large強制");
		inhibitSmallCheckBox.setForeground(Color.blue);
		inhibitSmallCheckBox.setToolTipText("サイズの小さい動画はダウンロードしません。他より低優先");
		extraDownloadInfoPanel.add(inhibitSmallCheckBox, gridbagc);

		gridbagc = new GridBagConstraints();
		gridbagc.gridx = 0;
		gridbagc.gridy = 5;
		gridbagc.anchor = GridBagConstraints.WEST;
		gridbagc.fill = GridBagConstraints.HORIZONTAL;
		gridbagc.insets = INSETS_0_0_0_5;
		gridbagc.weightx = 1.0;
		autoFlvToMp4CheckBox.setText("dmcはデフォルトでmp4に(拡張子はflvのまま)↑のmp4設定併用可");
		autoFlvToMp4CheckBox.setForeground(Color.blue);
		autoFlvToMp4CheckBox.setToolTipText(
			"dmc動画はmp4コンテナでダウンロードできたので設定不要。拡張子はflvのまま。↑の設定と併用すれば拡張子もmp4になります。");
		autoFlvToMp4CheckBox.setEnabled(false);
		extraDownloadInfoPanel.add(autoFlvToMp4CheckBox, gridbagc);

		gridbagc = new GridBagConstraints();
		gridbagc.gridy = 5;
		gridbagc.anchor = GridBagConstraints.SOUTHEAST;
		extraDownloadInfoPanel.add(extraDownloadInfoPanel.getHideLabel(), gridbagc);
		return extraDownloadInfoPanel;
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
	private JTextField ExtOptionField = new JTextField();
	private JCheckBox NotUseVhookCheckBox = new JCheckBox();
	private JTextField ViewCommentField = new JTextField();
	private JComboBox<String> ShadowComboBox = null;
	private JButton shadowAutoSettingButton = new JButton();

	/**
	 * Initialize FFmpegOptionComboBox
	 * @return
	 */
	private JComboBox<FFmpegSelectedItem> getFFmpegOptionComboBox() {
		if (FFmpegOptionComboBox == null) {
			FFmpegOptionComboBox = new JComboBox<FFmpegSelectedItem>(FFmpegOptionModel);
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
	private JComboBox<FFmpegSelectedItem> getWideFFmpegOptionComboBox() {
		if (WideFFmpegOptionComboBox == null) {
			WideFFmpegOptionComboBox = new JComboBox<FFmpegSelectedItem>(WideFFmpegOptionModel);
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
	private JComboBox<FFmpegSelectedItem> getZqFFmpegOptionComboBox() {
		if (zqFFmpegOptionComboBox == null) {
			zqFFmpegOptionComboBox = new JComboBox<FFmpegSelectedItem>(zqFFmpegOptionModel);
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
								String sizeMin = prop.getProperty("MIN", "");
								zqSizeMinField.setText(sizeMin);
								if(!sizeMin.isEmpty()){
									zqSizeMinField.setForeground(Color.black);
								}else{
									zqSizeMinField.setForeground(Color.gray);
								}
								String sizeMax = prop.getProperty("MAX", "");
								zqSizeMaxField.setText(sizeMax);
								if(!sizeMax.isEmpty()){
									zqSizeMaxField.setForeground(Color.black);
								}else{
									zqSizeMaxField.setForeground(Color.gray);
								}
								String fpsFloor = prop.getProperty("FPS", "");
								zqFpsRangeField.setText(fpsFloor);
								if(!fpsFloor.isEmpty()){
									zqFpsRangeField.setForeground(Color.black);
								}else{
									zqFpsRangeField.setForeground(Color.gray);
								}
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
								zqSizeMinField.setForeground(Color.gray);
								zqSizeMaxField.setForeground(Color.gray);
								zqFpsRangeField.setForeground(Color.gray);
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
			FFmpegOptionReloadButton.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						FFmpegOptionReload();
					}
				});
		}
		return FFmpegOptionReloadButton;
	}
	void FFmpegOptionReload(){
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
			WideFFmpegOptionReloadButton.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						WideFFmpegOptionReload();
					}
				});
		}
		return WideFFmpegOptionReloadButton;
	}
	void WideFFmpegOptionReload(){
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
			zqFFmpegOptionReloadButton.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						zqFFmpegOptionReload();
					}
				});
		}
		return zqFFmpegOptionReloadButton;
	}
	void zqFFmpegOptionReload(){
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
	/**
	 * This method initializes FFmpegOptionComboBoxPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getFFmpegOptionComboBoxPanel() {
		if (FFmpegOptionComboBoxPanel == null) {
			GridBagConstraints grid_x_y_47 = new GridBagConstraints();
			grid_x_y_47.fill = GridBagConstraints.NONE;
			grid_x_y_47.insets = INSETS_0_0_0_5;
			GridBagConstraints grid_x_y_46 = new GridBagConstraints();
			grid_x_y_46.fill = GridBagConstraints.HORIZONTAL;
			grid_x_y_46.gridwidth = 3;
			grid_x_y_46.weightx = 1.0;
			grid_x_y_46.insets = INSETS_0_5_0_5;
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
			grid_x_y_1_.insets = INSETS_0_0_0_5;
			GridBagConstraints grid_x_y_2_ = new GridBagConstraints();
			grid_x_y_2_.fill = GridBagConstraints.HORIZONTAL;
			grid_x_y_2_.gridwidth = 3;
			grid_x_y_2_.weightx = 1.0;
			grid_x_y_2_.insets = INSETS_0_5_0_5;
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
			grid_x_y_1_.insets = INSETS_0_0_0_5;
			GridBagConstraints grid_x_y_2_ = new GridBagConstraints();
			grid_x_y_2_.fill = GridBagConstraints.HORIZONTAL;
			grid_x_y_2_.gridwidth = 3;
			grid_x_y_2_.weightx = 1.0;
			grid_x_y_2_.insets = INSETS_0_5_0_5;
			zqFFmpegOptionComboBoxPanel = new JPanel();
			zqFFmpegOptionComboBoxPanel.setLayout(new GridBagLayout());
			zqFFmpegOptionComboBoxPanel.add(getZqFFmpegOptionComboBox(),
					grid_x_y_2_);
			zqFFmpegOptionComboBoxPanel.add(getZqFFmpegOptionReloadButton(),
					grid_x_y_1_);
		}
		return zqFFmpegOptionComboBoxPanel;
	}

	private JComboBox<String> commentEraseTypeComboBox;
	String[] CommentEraseTypeArray= {
		"0:従来通り(表示数を越えると古いコメントを消去)",
		"1:表示数を越えた新規コメントを無視",
	};;

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

	static void setMaster(MainFrame frame) {
		if(frame!=null && getMaster()==null)
			MasterMainFrame = frame;
	}

	public static MainFrame getMaster(){
		return MasterMainFrame;
	}

	public void setDateUserFirst(String dateUF) {
		WayBackField.setText(dateUF);
	}

	class MainFrame_this_windowAdapter extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			jMenuFileExit_actionPerformed(null);
		}
	}

	public static void reflesh() {
		if(MasterMainFrame!=null){
			if(SwingUtilities.isEventDispatchThread())
				MasterMainFrame.repaint();
			else
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						MasterMainFrame.repaint();
					}
				});
		}
	}

	private long terminateTimeout;
	public long getTerminateTimeout() {
		return terminateTimeout;
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

class ForcusedPopupBoard extends MouseAdapter {

	private final MainFrame frame;
	private final String title;
	private final String caption;
	ForcusedPopupBoard(MainFrame frame, String t, String s){
		this.frame = frame;
		title = t;
		caption = s;
	}

	public void mouseEntered(MouseEvent e){
		//custom title, no icon
		JOptionPane.showMessageDialog(frame,
		    caption, title,
		    JOptionPane.PLAIN_MESSAGE);
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
		String url = "https://www.nicovideo.jp/my/history";
		if (loader.load(url, file)){
			status.setText("視聴履歴をロードしました：" + file.getRelativePath());
		}
	}
}
*/

