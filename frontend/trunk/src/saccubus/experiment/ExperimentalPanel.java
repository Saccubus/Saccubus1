package saccubus.experiment;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import saccubus.MainFrame;

/**
 * <p>
 * さきゅばす
 * </p>

 * @version 1.26

 * @author orz

 */
public class ExperimentalPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JPanel experimentPanel = new JPanel();
	JCheckBox fontHeightFixCheckBox = new JCheckBox();
	JTextField fontHeightRatioTextField = new JTextField();
	JLabel fontHeightRatioLabel = new JLabel();
	JCheckBox disableOriginalResizeCheckBox = new JCheckBox();
	JLabel disableOriginalResizeLabel = new JLabel();
	JCheckBox disableLimitWidthResizeCheckBox = new JCheckBox();
	JLabel limitWidthResizeLabel = new JLabel();
	JTextField limitWidthTextField = new JTextField();
	JCheckBox disableLinefeedResizeCheckBox = new JCheckBox();
	JCheckBox disableDoubleResizeCheckBox = new JCheckBox();
	JCheckBox disableFontDoublescaleCheckBox = new JCheckBox();
	JLabel limitHeightLabel = new JLabel();
	JTextField limitHeightTextField = new JTextField();
	JCheckBox fixedFontSizeCheckBox = new JCheckBox();
	JTextField fixedFontSizeTextField = new JTextField();

	public ExperimentalPanel(){
		super();
		init();
	}

	private void init(){
		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
			"実験的設定（もし良い設定を見つけたらお知らせ下さい）",
			TitledBorder.LEADING, TitledBorder.TOP, getFont(), Color.blue));

		fontHeightFixCheckBox.setText("フォント高さ調整(％)");
		fontHeightFixCheckBox.setForeground(Color.blue);
		fontHeightFixCheckBox.setToolTipText("4:3のfont高さ(%) 16:9のfont高さ(%) 次の高さとの差(px)");
		GridBagConstraints grid20_x0_y = new GridBagConstraints();
		grid20_x0_y.gridx = 0;
		grid20_x0_y.gridy = 0;
		grid20_x0_y.anchor = GridBagConstraints.WEST;
		grid20_x0_y.fill = GridBagConstraints.HORIZONTAL;
		grid20_x0_y.insets = MainFrame.INSETS_0_5_0_5;
		add(fontHeightFixCheckBox, grid20_x0_y);
		fontHeightRatioTextField.setText("116 94 1");
		fontHeightRatioTextField.setForeground(Color.blue);
		GridBagConstraints grid20_x1_y = new GridBagConstraints();
		grid20_x1_y.gridx = 1;
		grid20_x1_y.gridy = 0;
		grid20_x1_y.anchor = GridBagConstraints.WEST;
		grid20_x1_y.fill = GridBagConstraints.HORIZONTAL;
		grid20_x1_y.weightx = 1.0;
		grid20_x1_y.insets = MainFrame.INSETS_0_5_0_5;
		add(fontHeightRatioTextField,grid20_x1_y);
		disableOriginalResizeCheckBox.setText("従来のさきゅばすのリサイズを無効にする");
		disableOriginalResizeCheckBox.setForeground(Color.blue);
		disableOriginalResizeCheckBox.setToolTipText("以下の設定はこれを無効にしないと設定出来ない");
		disableOriginalResizeCheckBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				setDisableOriginalResizeCheckBox(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		grid20_x0_y = new GridBagConstraints();
		grid20_x0_y.gridx = 0;
		grid20_x0_y.gridy = 2;
		grid20_x0_y.gridwidth = 2;
		grid20_x0_y.weightx = 1.0;
		grid20_x0_y.fill = GridBagConstraints.HORIZONTAL;
		grid20_x0_y.insets = MainFrame.INSETS_0_5_0_5;
		add(disableOriginalResizeCheckBox, grid20_x0_y);
		disableLimitWidthResizeCheckBox.setText("臨界幅リサイズを無効にする");
		disableLimitWidthResizeCheckBox.setForeground(Color.blue);
		disableLimitWidthResizeCheckBox.setSelected(false);
		disableLimitWidthResizeCheckBox.setToolTipText("現rev.では変更可、デフォルト有効");
		grid20_x0_y = new GridBagConstraints();
		grid20_x0_y.gridx = 0;
		grid20_x0_y.gridy = 4;
		grid20_x0_y.gridwidth = 2;
		grid20_x0_y.weightx = 1.0;
		grid20_x0_y.fill = GridBagConstraints.HORIZONTAL;
		grid20_x0_y.insets = MainFrame.INSETS_0_25_0_5;
		add(disableLimitWidthResizeCheckBox, grid20_x0_y);
		limitWidthResizeLabel.setText("臨界幅の設定");
		limitWidthResizeLabel.setForeground(Color.blue);
	//	limitWidthResizeLabel.setToolTipText("臨界幅は、通常コマンド　fullコマンドの順にpxで指定");
		grid20_x0_y.gridx = 0;
		grid20_x0_y.gridy = 5;
		grid20_x0_y.fill = GridBagConstraints.HORIZONTAL;
		grid20_x0_y.insets = new Insets(0, 50, 0, 5);
		add(limitWidthResizeLabel, grid20_x0_y);
		limitWidthTextField.setText("524 1048");
		limitWidthTextField.setForeground(Color.blue);
		limitWidthTextField.setEditable(true);
		grid20_x1_y.gridx = 1;
		grid20_x1_y.gridy = 5;
		grid20_x1_y.weightx = 1.0;
		grid20_x1_y.fill = GridBagConstraints.HORIZONTAL;
		grid20_x1_y.insets = MainFrame.INSETS_0_5_0_5;
		add(limitWidthTextField, grid20_x1_y);
		limitHeightLabel.setText("臨界高さ設定");
		limitHeightLabel.setForeground(Color.blue);
		limitHeightLabel.setToolTipText("臨界高、4:3　16:9の順に指定、ニコ動公式は両方385px");
		grid20_x0_y = new GridBagConstraints();
		grid20_x0_y.gridx = 0;
		grid20_x0_y.gridy = 6;
		grid20_x0_y.fill = GridBagConstraints.HORIZONTAL;
		grid20_x0_y.insets = new Insets(0, 50, 0, 5);
		add(limitHeightLabel, grid20_x0_y);
		limitHeightTextField.setText("384 384");
		limitHeightTextField.setForeground(Color.blue);
		grid20_x1_y = new GridBagConstraints();
		grid20_x1_y.gridx = 1;
		grid20_x1_y.gridy = 6;
		grid20_x1_y.weightx = 1.0;
		grid20_x1_y.anchor = GridBagConstraints.WEST;
		grid20_x1_y.fill = GridBagConstraints.HORIZONTAL;
		grid20_x1_y.insets = MainFrame.INSETS_0_5_0_5;
		add(limitHeightTextField, grid20_x1_y);
		disableLinefeedResizeCheckBox.setText("改行リサイズ無効");
		disableLinefeedResizeCheckBox.setForeground(Color.blue);
		disableLinefeedResizeCheckBox.setSelected(false);
		disableLinefeedResizeCheckBox.setToolTipText("現rev.では変更可、デフォルト有効");
		grid20_x0_y = new GridBagConstraints();
		grid20_x0_y.gridx = 0;
		grid20_x0_y.gridy = 7;
		grid20_x1_y.fill = GridBagConstraints.HORIZONTAL;
		grid20_x0_y.insets = MainFrame.INSETS_0_25_0_5;
		add(disableLinefeedResizeCheckBox, grid20_x0_y);
		disableDoubleResizeCheckBox.setText("ダブルリサイズ無効");
		disableDoubleResizeCheckBox.setForeground(Color.blue);
		disableDoubleResizeCheckBox.setSelected(false);
		disableDoubleResizeCheckBox.setToolTipText("現rev.では変更可、デフォルト有効");
		grid20_x0_y = new GridBagConstraints();
		grid20_x0_y.gridx = 1;
		grid20_x0_y.gridy = 7;
		grid20_x0_y.weightx = 1.0;
		grid20_x0_y.fill = GridBagConstraints.HORIZONTAL;
		grid20_x0_y.insets = MainFrame.INSETS_0_5_0_5;
		add(disableDoubleResizeCheckBox, grid20_x0_y);
		disableFontDoublescaleCheckBox.setText("フォントを自動的に2倍に拡大しない");
		disableFontDoublescaleCheckBox.setForeground(Color.blue);
		grid20_x0_y = new GridBagConstraints();
		grid20_x0_y.gridx = 0;
		grid20_x0_y.gridy = 9;
		grid20_x0_y.gridwidth = 2;
		grid20_x0_y.weightx = 1.0;
		grid20_x0_y.fill = GridBagConstraints.HORIZONTAL;
		grid20_x0_y.insets = MainFrame.INSETS_0_5_0_5;
		add(disableFontDoublescaleCheckBox,grid20_x0_y);
		fixedFontSizeCheckBox.setText("修正フォントサイズ設定");
		fixedFontSizeCheckBox.setForeground(Color.blue);
		fixedFontSizeCheckBox.setToolTipText("既定値を修正出来ます。normal big smallの順(pt)");
		grid20_x0_y = new GridBagConstraints();
		grid20_x0_y.gridx = 0;
		grid20_x0_y.gridy = 10;
		grid20_x0_y.fill = GridBagConstraints.HORIZONTAL;
		grid20_x0_y.insets = MainFrame.INSETS_0_5_0_5;
		add(fixedFontSizeCheckBox, grid20_x0_y);
		fixedFontSizeTextField.setForeground(Color.blue);
		fixedFontSizeTextField.setText("24 39 15");
		grid20_x1_y = new GridBagConstraints();
		grid20_x1_y.gridx = 1;
		grid20_x1_y.gridy = 10;
		grid20_x1_y.weightx = 1.0;
		grid20_x1_y.anchor = GridBagConstraints.WEST;
		grid20_x1_y.fill = GridBagConstraints.HORIZONTAL;
		grid20_x1_y.insets = MainFrame.INSETS_0_5_0_5;
		add(fixedFontSizeTextField, grid20_x1_y);
	}

	void setDisableOriginalResizeCheckBox(boolean disable) {
		if (disable){
		//	disableOriginalResizeCheckBox.setSelected(true);
			disableLimitWidthResizeCheckBox.setEnabled(true);
			disableLinefeedResizeCheckBox.setEnabled(true);
			disableDoubleResizeCheckBox.setEnabled(true);
			limitWidthTextField.setEnabled(true);
			limitHeightTextField.setEnabled(true);
		} else {
		//	disableOriginalResizeCheckBox.setSelected(false);
			disableLimitWidthResizeCheckBox.setEnabled(false);
			disableLinefeedResizeCheckBox.setEnabled(false);
			disableDoubleResizeCheckBox.setEnabled(false);
			limitWidthTextField.setEnabled(false);
			limitHeightTextField.setEnabled(false);
		}
	}

	public ExperimentalSetting getSetting() {
		return new ExperimentalSetting(
			disableOriginalResizeCheckBox.isSelected(),
			disableLimitWidthResizeCheckBox.isSelected(),
			disableLinefeedResizeCheckBox.isSelected(),
			disableDoubleResizeCheckBox.isSelected(),
			disableFontDoublescaleCheckBox.isSelected(),
			fontHeightFixCheckBox.isSelected(),
			fontHeightRatioTextField.getText(),
			limitWidthTextField.getText(),
			limitHeightTextField.getText(),
			fixedFontSizeCheckBox.isSelected(),
			fixedFontSizeTextField.getText()
		);
	}

	public void setSetting(ExperimentalSetting exp) {
		fontHeightFixCheckBox.setSelected(exp.isFontHeightFix());
		fontHeightRatioTextField.setText(exp.getFontHeightFixRaito());
		disableOriginalResizeCheckBox.setSelected(exp.isDisableOriginalResize());
		disableLimitWidthResizeCheckBox.setSelected(exp.isDisableLimitWidthResize());
		disableLinefeedResizeCheckBox.setSelected(exp.isDisableLinefeedResize());
		disableDoubleResizeCheckBox.setSelected(exp.isDisableDoubleResize());
		disableFontDoublescaleCheckBox.setSelected(exp.isDisableFontDoublescale());
		limitWidthTextField.setText(exp.getLimitWidth());
		limitHeightTextField.setText(exp.getLimitHeight());
		fixedFontSizeCheckBox.setSelected(exp.isEnableFixedFontSizeUse());
		fixedFontSizeTextField.setText(exp.getFixedFontSize());
		// ↓の設定は最後に
		setDisableOriginalResizeCheckBox(exp.isDisableOriginalResize());
	}
}

