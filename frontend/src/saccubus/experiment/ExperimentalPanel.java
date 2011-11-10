package saccubus.experiment;
//
//import java.awt.Color;
//import java.awt.Font;
//import java.awt.GridBagConstraints;
//import java.awt.GridBagLayout;
//import java.awt.event.ItemEvent;
//import java.awt.event.ItemListener;
//
//import javax.swing.BorderFactory;
//import javax.swing.JCheckBox;
//import javax.swing.JLabel;
import javax.swing.JPanel;
//import javax.swing.JTextField;
//import javax.swing.border.EtchedBorder;
//import javax.swing.border.TitledBorder;
//
//import saccubus.MainFrame;
//
///**
// * <p>
// * さきゅばす
// * </p>
// * @version 1.26
// * @author orz
// */
public class ExperimentalPanel extends JPanel {
//
//	/**
//	 *
//	 */
	private static final long serialVersionUID = 1L;
//
//	JPanel experimentPanel = new JPanel();
//	JCheckBox fontHeightFixCheckBox = new JCheckBox();
//	JTextField fontHeightRatioTextField = new JTextField();
//	JCheckBox disableOriginalResizeCheckBox = new JCheckBox();
//	JCheckBox enableLimitWidthCheckBox = new JCheckBox();
//	JTextField limitWidthTextField = new JTextField();
//	JCheckBox enableDoubleLimitWidthCheckBox = new JCheckBox();
//	JTextField doubleLimitWidthTextField = new JTextField();
//	JCheckBox disableLinefeedResizeCheckBox = new JCheckBox();
//	JCheckBox disableLimitWidthResizeCheckBox = new JCheckBox();
//	JCheckBox disableDoubleResizeCheckBox = new JCheckBox();
//	JCheckBox disableFontDoublescaleCheckBox = new JCheckBox();
//	JCheckBox enableLimitHeightCheckBox = new JCheckBox();
//	JTextField limitHeightTextField = new JTextField();
//	JCheckBox fixedFontSizeCheckBox = new JCheckBox();
//	JTextField fixedFontSizeTextField = new JTextField();
//	JPanel convNGFontPanel = new JPanel();
//
//	public ExperimentalPanel(){
//		super();
//		experimentPanel = getExperimentalPanel();
//		convNGFontPanel = getConvNGFontPanel();
//		setLayout(new GridBagLayout());
//		GridBagConstraints gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 0;
//		gridBagConstraints.weightx = 1.0;
//		gridBagConstraints.insets = MainFrame.INSETS_0_5_0_5;
//		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//		add(experimentPanel, gridBagConstraints);
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 0;
//		gridBagConstraints.weightx = 1.0;
//		gridBagConstraints.insets = MainFrame.INSETS_0_5_0_5;
//		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//		gridBagConstraints.anchor = GridBagConstraints.NORTH;
//		add(convNGFontPanel, gridBagConstraints);
//		JLabel dummy = new JLabel();
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 0;
//		gridBagConstraints.gridy = 99;
//		gridBagConstraints.weightx = 1.0;
//		gridBagConstraints.weighty = 1.0;
//		gridBagConstraints.fill = GridBagConstraints.BOTH;
//		add(dummy, gridBagConstraints);
//	}
//
//	private JPanel getExperimentalPanel(){
//		JPanel panel = new JPanel();
//		panel.setLayout(new GridBagLayout());
//		Font font = getFont();
//		font.deriveFont(12.0f);
//		panel.setBorder(BorderFactory.createTitledBorder(
//			BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
//			"実験的設定",
//			TitledBorder.LEADING, TitledBorder.TOP, getFont(), Color.blue));
//
//		JLabel label = new JLabel();
//		GridBagConstraints gridBagConstraints = new GridBagConstraints();
//		label.setFont(font);
//		label.setText("もし良い設定を見つけたらお知らせ下さい");
//		label.setForeground(Color.black);
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 0;
//		gridBagConstraints.gridy = 0;
//		gridBagConstraints.gridwidth = 2;
//		gridBagConstraints.weightx = 1.0;
//		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//		gridBagConstraints.insets = MainFrame.INSETS_0_5_0_5;
//		panel.add(label, gridBagConstraints);
//		fontHeightFixCheckBox.setText("フォント高さ調整(％)");
//		fontHeightFixCheckBox.setForeground(Color.blue);
//		fontHeightFixCheckBox.setToolTipText("4:3のfont高さ(％) 16:9のfont高さ(％) 4:3の次の高さとの差(％) 16:9の次の高さとの差(％)");
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 0;
//		gridBagConstraints.gridy = 1;
//		gridBagConstraints.anchor = GridBagConstraints.WEST;
//		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//		gridBagConstraints.insets = MainFrame.INSETS_0_5_0_5;
//		panel.add(fontHeightFixCheckBox, gridBagConstraints);
//		fontHeightRatioTextField.setText("100 94 16 1");
//		fontHeightRatioTextField.setForeground(Color.blue);
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 1;
//		gridBagConstraints.gridy = 1;
//		gridBagConstraints.anchor = GridBagConstraints.WEST;
//		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//		gridBagConstraints.weightx = 1.0;
//		gridBagConstraints.insets = MainFrame.INSETS_0_5_0_5;
//		panel.add(fontHeightRatioTextField,gridBagConstraints);
//		disableOriginalResizeCheckBox.setText("既定のさきゅばすのリサイズを無効にする");
//		disableOriginalResizeCheckBox.setForeground(Color.blue);
//		disableOriginalResizeCheckBox.setToolTipText("以下の設定はこれを無効にしないと設定出来ない");
//		disableOriginalResizeCheckBox.addItemListener(new ItemListener() {
//			@Override
//			public void itemStateChanged(ItemEvent e) {
//				setDisableOriginalResizeCheckBox(e.getStateChange() == ItemEvent.SELECTED);
//			}
//		});
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 0;
//		gridBagConstraints.gridy = 3;
//		gridBagConstraints.gridwidth = 2;
//		gridBagConstraints.weightx = 1.0;
//		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//		gridBagConstraints.insets = MainFrame.INSETS_0_5_0_5;
//		panel.add(disableOriginalResizeCheckBox, gridBagConstraints);
//		enableLimitWidthCheckBox.setText("臨界幅の設定(px) 2 or 4");
//		enableLimitWidthCheckBox.setForeground(Color.blue);
//		enableLimitWidthCheckBox.setToolTipText("通常臨界幅 full臨界幅 通常ダブルリサイズ最小 fullダブルリサイズ最小の順にpxで指定");
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 0;
//		gridBagConstraints.gridy = 5;
//		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//		gridBagConstraints.insets = MainFrame.INSETS_0_25_0_5;
//		panel.add(enableLimitWidthCheckBox, gridBagConstraints);
//		limitWidthTextField.setText("544 672 480 512");
//		limitWidthTextField.setForeground(Color.blue);
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 1;
//		gridBagConstraints.gridy = 5;
//		gridBagConstraints.weightx = 1.0;
//		gridBagConstraints.anchor = GridBagConstraints.WEST;
//		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//		gridBagConstraints.insets = MainFrame.INSETS_0_5_0_5;
//		panel.add(limitWidthTextField, gridBagConstraints);
//		enableDoubleLimitWidthCheckBox.setText("ダブルリサイズ後臨界幅(px)");
//		enableDoubleLimitWidthCheckBox.setForeground(Color.blue);
//		enableDoubleLimitWidthCheckBox.setToolTipText("通常ダブルリサイズ臨界幅 fullダブルリサイズ臨界幅の順にpxで指定");
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 0;
//		gridBagConstraints.gridy = 6;
//		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//		gridBagConstraints.insets = MainFrame.INSETS_0_25_0_5;
//		panel.add(enableDoubleLimitWidthCheckBox, gridBagConstraints);
//		doubleLimitWidthTextField.setForeground(Color.blue);
//		doubleLimitWidthTextField.setText("1024 1280");
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 1;
//		gridBagConstraints.gridy = 6;
//		gridBagConstraints.weightx = 1.0;
//		gridBagConstraints.anchor = GridBagConstraints.WEST;
//		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//		gridBagConstraints.insets = MainFrame.INSETS_0_5_0_5;
//		panel.add(doubleLimitWidthTextField, gridBagConstraints);
//		enableLimitHeightCheckBox.setText("臨界高さ設定(px)");
//		enableLimitHeightCheckBox.setForeground(Color.blue);
//		enableLimitHeightCheckBox.setToolTipText("臨界高、4:3　16:9の順に指定、ニコ動公式は両方385px");
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 0;
//		gridBagConstraints.gridy = 7;
//		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//		gridBagConstraints.insets = MainFrame.INSETS_0_25_0_5;
//		panel.add(enableLimitHeightCheckBox, gridBagConstraints);
//		limitHeightTextField.setText("384 384");
//		limitHeightTextField.setForeground(Color.blue);
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 1;
//		gridBagConstraints.gridy = 7;
//		gridBagConstraints.weightx = 1.0;
//		gridBagConstraints.anchor = GridBagConstraints.WEST;
//		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//		gridBagConstraints.insets = MainFrame.INSETS_0_5_0_5;
//		panel.add(limitHeightTextField, gridBagConstraints);
//		disableLinefeedResizeCheckBox.setText("改行リサイズ無効");
//		disableLinefeedResizeCheckBox.setForeground(Color.blue);
//		disableLinefeedResizeCheckBox.setToolTipText("現rev.では変更可、デフォルト有効");
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 0;
//		gridBagConstraints.gridy = 8;
//		gridBagConstraints.gridwidth = 2;
//		gridBagConstraints.weightx = 1.0;
//		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//		gridBagConstraints.insets = MainFrame.INSETS_0_25_0_5;
//		panel.add(disableLinefeedResizeCheckBox, gridBagConstraints);
//		disableDoubleResizeCheckBox.setText("臨界幅リサイズ無効");
//		disableDoubleResizeCheckBox.setForeground(Color.blue);
//		disableDoubleResizeCheckBox.setToolTipText("現rev.では変更可、デフォルト有効");
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 0;
//		gridBagConstraints.gridy = 9;
//		gridBagConstraints.gridwidth = 2;
//		gridBagConstraints.weightx = 1.0;
//		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//		gridBagConstraints.insets = MainFrame.INSETS_0_25_0_5;
//		panel.add(disableDoubleResizeCheckBox, gridBagConstraints);
//		disableLimitWidthResizeCheckBox.setText("ダブルリサイズ無効");
//		disableLimitWidthResizeCheckBox.setForeground(Color.blue);
//		disableLimitWidthResizeCheckBox.setToolTipText("現rev.では変更可、デフォルト有効");
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 0;
//		gridBagConstraints.gridy = 10;
//		gridBagConstraints.gridwidth = 2;
//		gridBagConstraints.weightx = 1.0;
//		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//		gridBagConstraints.insets = MainFrame.INSETS_0_25_0_5;
//		panel.add(disableLimitWidthResizeCheckBox, gridBagConstraints);
//		disableFontDoublescaleCheckBox.setText("フォントを自動的に2倍に拡大しない");
//		disableFontDoublescaleCheckBox.setForeground(Color.blue);
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 0;
//		gridBagConstraints.gridy = 11;
//		gridBagConstraints.gridwidth = 2;
//		gridBagConstraints.weightx = 1.0;
//		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//		gridBagConstraints.insets = MainFrame.INSETS_0_5_0_5;
//		panel.add(disableFontDoublescaleCheckBox,gridBagConstraints);
//		fixedFontSizeCheckBox.setText("修正フォントサイズ設定");
//		fixedFontSizeCheckBox.setForeground(Color.blue);
//		fixedFontSizeCheckBox.setToolTipText("既定値を修正出来ます。normal big smallの順(pt)");
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 0;
//		gridBagConstraints.gridy = 12;
//		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//		gridBagConstraints.insets = MainFrame.INSETS_0_5_0_5;
//		panel.add(fixedFontSizeCheckBox, gridBagConstraints);
//		fixedFontSizeTextField.setForeground(Color.blue);
//		fixedFontSizeTextField.setText("24 39 15");
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 1;
//		gridBagConstraints.gridy = 12;
//		gridBagConstraints.weightx = 1.0;
//		gridBagConstraints.anchor = GridBagConstraints.WEST;
//		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//		gridBagConstraints.insets = MainFrame.INSETS_0_5_0_5;
//		panel.add(fixedFontSizeTextField, gridBagConstraints);
//		JLabel dummy = new JLabel();
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 0;
//		gridBagConstraints.gridy = 99;
//		gridBagConstraints.gridwidth = 2;
//		gridBagConstraints.weightx = 1.0;
//		gridBagConstraints.weighty = 1.0;
//		gridBagConstraints.fill = GridBagConstraints.BOTH;
//		panel.add(dummy, gridBagConstraints);
//		return panel;
//	}
//
//	JTextField ngFontTextField = new JTextField();
//	JTextField ngFontCodeTextField = new JTextField();
//
//	private JPanel getConvNGFontPanel(){
//		JPanel panel = new JPanel();
//		JLabel label;
//		GridBagConstraints gridBagConstraints;
//		panel.setLayout(new GridBagLayout());
//		panel.setBorder(BorderFactory.createTitledBorder(
//			BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
//			"NGフォント設定",
//			TitledBorder.LEADING, TitledBorder.TOP, getFont(), Color.blue));
//
//		label = new JLabel();
//		label.setText("さきゅばすで透明にするフォント文字を指定します");
//		label.setForeground(Color.black);
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 0;
//		gridBagConstraints.gridy = 0;
//		gridBagConstraints.gridwidth = 2;
//		gridBagConstraints.weightx = 1.0;
//		gridBagConstraints.anchor = GridBagConstraints.WEST;
//		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//		gridBagConstraints.insets = MainFrame.INSETS_0_5_0_5;
//		panel.add(label,gridBagConstraints);
//		label = new JLabel();
//		label.setText("NGフォント");
//		label.setForeground(Color.blue);
//		label.setForeground(Color.blue);
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 0;
//		gridBagConstraints.gridy = 2;
//		gridBagConstraints.insets = MainFrame.INSETS_0_5_0_5;
//		panel.add(label, gridBagConstraints);
//		ngFontTextField.setForeground(Color.blue);
//		ngFontTextField.setText("初期値");
//	//	ngFontTextField.setEditable(false);
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 1;
//		gridBagConstraints.gridy = 2;
//		gridBagConstraints.weightx = 1.0;
//		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//		gridBagConstraints.insets = MainFrame.INSETS_0_5_0_5;
//		panel.add(ngFontTextField, gridBagConstraints);
//		label = new JLabel();
//		label.setText("NGフォントコード");
//		label.setForeground(Color.blue);
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 0;
//		gridBagConstraints.gridy = 4;
//		gridBagConstraints.insets = MainFrame.INSETS_0_5_0_5;
//		panel.add(label, gridBagConstraints);
//		ngFontCodeTextField.setForeground(Color.blue);
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 1;
//		gridBagConstraints.gridy = 4;
//		gridBagConstraints.weightx = 1.0;
//		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//		gridBagConstraints.insets = MainFrame.INSETS_0_5_0_5;
//		panel.add(ngFontCodeTextField, gridBagConstraints);
//		JLabel dummy = new JLabel();
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 0;
//		gridBagConstraints.gridy = 99;
//		gridBagConstraints.gridwidth = 2;
//		gridBagConstraints.weightx = 1.0;
//		gridBagConstraints.weighty = 1.0;
//		gridBagConstraints.fill = GridBagConstraints.BOTH;
//		panel.add(dummy, gridBagConstraints);
//		return panel;
//	}
//
//	void setDisableOriginalResizeCheckBox(boolean disable) {
//		if (disable){
//		//	disableOriginalResizeCheckBox.setSelected(true);
//			enableLimitWidthCheckBox.setEnabled(true);
//			enableDoubleLimitWidthCheckBox.setEnabled(true);
//			enableLimitHeightCheckBox.setEnabled(true);
//			disableLinefeedResizeCheckBox.setEnabled(true);
//			disableDoubleResizeCheckBox.setEnabled(true);
//			disableLimitWidthResizeCheckBox.setEnabled(true);
//			limitWidthTextField.setEnabled(true);
//			doubleLimitWidthTextField.setEditable(true);
//			limitHeightTextField.setEnabled(true);
//		} else {
//		//	disableOriginalResizeCheckBox.setSelected(false);
//			enableLimitWidthCheckBox.setEnabled(false);
//			enableDoubleLimitWidthCheckBox.setEnabled(false);
//			enableLimitHeightCheckBox.setEnabled(false);
//			disableLinefeedResizeCheckBox.setEnabled(false);
//			disableDoubleResizeCheckBox.setEnabled(false);
//			disableLimitWidthResizeCheckBox.setEnabled(false);
//			limitWidthTextField.setEnabled(false);
//			doubleLimitWidthTextField.setEditable(false);
//			limitHeightTextField.setEnabled(false);
//		}
//	}
//
//	public ExperimentalSetting getSetting() {
//		return new ExperimentalSetting(
//			disableOriginalResizeCheckBox.isSelected(),
//			disableLimitWidthResizeCheckBox.isSelected(),
//			disableLinefeedResizeCheckBox.isSelected(),
//			disableDoubleResizeCheckBox.isSelected(),
//			disableFontDoublescaleCheckBox.isSelected(),
//			fontHeightFixCheckBox.isSelected(),
//			fontHeightRatioTextField.getText(),
//			limitWidthTextField.getText(),
//			limitHeightTextField.getText(),
//			fixedFontSizeCheckBox.isSelected(),
//			fixedFontSizeTextField.getText(),
//			enableLimitHeightCheckBox.isSelected(),
//			ngFontCodeTextField.getText(),
//			enableLimitWidthCheckBox.isSelected(),
//			enableDoubleLimitWidthCheckBox.isSelected(),
//			doubleLimitWidthTextField.getText()
//		);
//	}
//
//	public void setSetting(ExperimentalSetting exp) {
//		fontHeightFixCheckBox.setSelected(exp.isFontHeightFix());
//		fontHeightRatioTextField.setText(exp.getFontHeightFixRaito());
//		disableOriginalResizeCheckBox.setSelected(exp.isDisableOriginalResize());
//		disableLinefeedResizeCheckBox.setSelected(exp.isDisableLinefeedResize());
//		disableLimitWidthResizeCheckBox.setSelected(exp.isDisableLimitWidthResize());
//		disableDoubleResizeCheckBox.setSelected(exp.isDisableDoubleResize());
//		disableFontDoublescaleCheckBox.setSelected(exp.isDisableFontDoublescale());
//		limitWidthTextField.setText(exp.getLimitWidth());
//		limitHeightTextField.setText(exp.getLimitHeight());
//		fixedFontSizeCheckBox.setSelected(exp.isEnableFixedFontSizeUse());
//		fixedFontSizeTextField.setText(exp.getFixedFontSize());
//		enableLimitHeightCheckBox.setSelected(exp.isEnableLimitHeight());
//		ngFontCodeTextField.setText(exp.getNGFontCode());
//		ngFontTextField.setText(Ucode.decodeList(
//			exp.getNGFontCode(), getFont()));
//		enableLimitWidthCheckBox.setSelected(exp.isEnableLimitWidth());
//		enableDoubleLimitWidthCheckBox.setSelected(exp.isEnableLimitWidth());
//		doubleLimitWidthTextField.setText(exp.getDoubleLimitWidth());
//
//		// ↓の設定は最後に
//		setDisableOriginalResizeCheckBox(exp.isDisableOriginalResize());
//	}
}
