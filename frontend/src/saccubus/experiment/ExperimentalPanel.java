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
// * ������΂�
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
//			"�����I�ݒ�",
//			TitledBorder.LEADING, TitledBorder.TOP, getFont(), Color.blue));
//
//		JLabel label = new JLabel();
//		GridBagConstraints gridBagConstraints = new GridBagConstraints();
//		label.setFont(font);
//		label.setText("�����ǂ��ݒ���������炨�m�点������");
//		label.setForeground(Color.black);
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 0;
//		gridBagConstraints.gridy = 0;
//		gridBagConstraints.gridwidth = 2;
//		gridBagConstraints.weightx = 1.0;
//		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//		gridBagConstraints.insets = MainFrame.INSETS_0_5_0_5;
//		panel.add(label, gridBagConstraints);
//		fontHeightFixCheckBox.setText("�t�H���g��������(��)");
//		fontHeightFixCheckBox.setForeground(Color.blue);
//		fontHeightFixCheckBox.setToolTipText("4:3��font����(��) 16:9��font����(��) 4:3�̎��̍����Ƃ̍�(��) 16:9�̎��̍����Ƃ̍�(��)");
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
//		disableOriginalResizeCheckBox.setText("����̂�����΂��̃��T�C�Y�𖳌��ɂ���");
//		disableOriginalResizeCheckBox.setForeground(Color.blue);
//		disableOriginalResizeCheckBox.setToolTipText("�ȉ��̐ݒ�͂���𖳌��ɂ��Ȃ��Ɛݒ�o���Ȃ�");
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
//		enableLimitWidthCheckBox.setText("�ՊE���̐ݒ�(px) 2 or 4");
//		enableLimitWidthCheckBox.setForeground(Color.blue);
//		enableLimitWidthCheckBox.setToolTipText("�ʏ�ՊE�� full�ՊE�� �ʏ�_�u�����T�C�Y�ŏ� full�_�u�����T�C�Y�ŏ��̏���px�Ŏw��");
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
//		enableDoubleLimitWidthCheckBox.setText("�_�u�����T�C�Y��ՊE��(px)");
//		enableDoubleLimitWidthCheckBox.setForeground(Color.blue);
//		enableDoubleLimitWidthCheckBox.setToolTipText("�ʏ�_�u�����T�C�Y�ՊE�� full�_�u�����T�C�Y�ՊE���̏���px�Ŏw��");
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
//		enableLimitHeightCheckBox.setText("�ՊE�����ݒ�(px)");
//		enableLimitHeightCheckBox.setForeground(Color.blue);
//		enableLimitHeightCheckBox.setToolTipText("�ՊE���A4:3�@16:9�̏��Ɏw��A�j�R�������͗���385px");
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
//		disableLinefeedResizeCheckBox.setText("���s���T�C�Y����");
//		disableLinefeedResizeCheckBox.setForeground(Color.blue);
//		disableLinefeedResizeCheckBox.setToolTipText("��rev.�ł͕ύX�A�f�t�H���g�L��");
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 0;
//		gridBagConstraints.gridy = 8;
//		gridBagConstraints.gridwidth = 2;
//		gridBagConstraints.weightx = 1.0;
//		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//		gridBagConstraints.insets = MainFrame.INSETS_0_25_0_5;
//		panel.add(disableLinefeedResizeCheckBox, gridBagConstraints);
//		disableDoubleResizeCheckBox.setText("�ՊE�����T�C�Y����");
//		disableDoubleResizeCheckBox.setForeground(Color.blue);
//		disableDoubleResizeCheckBox.setToolTipText("��rev.�ł͕ύX�A�f�t�H���g�L��");
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 0;
//		gridBagConstraints.gridy = 9;
//		gridBagConstraints.gridwidth = 2;
//		gridBagConstraints.weightx = 1.0;
//		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//		gridBagConstraints.insets = MainFrame.INSETS_0_25_0_5;
//		panel.add(disableDoubleResizeCheckBox, gridBagConstraints);
//		disableLimitWidthResizeCheckBox.setText("�_�u�����T�C�Y����");
//		disableLimitWidthResizeCheckBox.setForeground(Color.blue);
//		disableLimitWidthResizeCheckBox.setToolTipText("��rev.�ł͕ύX�A�f�t�H���g�L��");
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 0;
//		gridBagConstraints.gridy = 10;
//		gridBagConstraints.gridwidth = 2;
//		gridBagConstraints.weightx = 1.0;
//		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//		gridBagConstraints.insets = MainFrame.INSETS_0_25_0_5;
//		panel.add(disableLimitWidthResizeCheckBox, gridBagConstraints);
//		disableFontDoublescaleCheckBox.setText("�t�H���g�������I��2�{�Ɋg�債�Ȃ�");
//		disableFontDoublescaleCheckBox.setForeground(Color.blue);
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 0;
//		gridBagConstraints.gridy = 11;
//		gridBagConstraints.gridwidth = 2;
//		gridBagConstraints.weightx = 1.0;
//		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//		gridBagConstraints.insets = MainFrame.INSETS_0_5_0_5;
//		panel.add(disableFontDoublescaleCheckBox,gridBagConstraints);
//		fixedFontSizeCheckBox.setText("�C���t�H���g�T�C�Y�ݒ�");
//		fixedFontSizeCheckBox.setForeground(Color.blue);
//		fixedFontSizeCheckBox.setToolTipText("����l���C���o���܂��Bnormal big small�̏�(pt)");
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
//			"NG�t�H���g�ݒ�",
//			TitledBorder.LEADING, TitledBorder.TOP, getFont(), Color.blue));
//
//		label = new JLabel();
//		label.setText("������΂��œ����ɂ���t�H���g�������w�肵�܂�");
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
//		label.setText("NG�t�H���g");
//		label.setForeground(Color.blue);
//		label.setForeground(Color.blue);
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 0;
//		gridBagConstraints.gridy = 2;
//		gridBagConstraints.insets = MainFrame.INSETS_0_5_0_5;
//		panel.add(label, gridBagConstraints);
//		ngFontTextField.setForeground(Color.blue);
//		ngFontTextField.setText("�����l");
//	//	ngFontTextField.setEditable(false);
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 1;
//		gridBagConstraints.gridy = 2;
//		gridBagConstraints.weightx = 1.0;
//		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//		gridBagConstraints.insets = MainFrame.INSETS_0_5_0_5;
//		panel.add(ngFontTextField, gridBagConstraints);
//		label = new JLabel();
//		label.setText("NG�t�H���g�R�[�h");
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
//		// ���̐ݒ�͍Ō��
//		setDisableOriginalResizeCheckBox(exp.isDisableOriginalResize());
//	}
}
