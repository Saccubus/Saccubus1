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
 * ������΂�
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
			"�����I�ݒ�i�����ǂ��ݒ���������炨�m�点�������j",
			TitledBorder.LEADING, TitledBorder.TOP, getFont(), Color.blue));

		fontHeightFixCheckBox.setText("�t�H���g��������(��)");
		fontHeightFixCheckBox.setForeground(Color.blue);
		fontHeightFixCheckBox.setToolTipText("4:3��font����(%) 16:9��font����(%) ���̍����Ƃ̍�(px)");
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
		disableOriginalResizeCheckBox.setText("�]���̂�����΂��̃��T�C�Y�𖳌��ɂ���");
		disableOriginalResizeCheckBox.setForeground(Color.blue);
		disableOriginalResizeCheckBox.setToolTipText("�ȉ��̐ݒ�͂���𖳌��ɂ��Ȃ��Ɛݒ�o���Ȃ�");
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
		disableLimitWidthResizeCheckBox.setText("�ՊE�����T�C�Y�𖳌��ɂ���");
		disableLimitWidthResizeCheckBox.setForeground(Color.blue);
		disableLimitWidthResizeCheckBox.setSelected(false);
		disableLimitWidthResizeCheckBox.setToolTipText("��rev.�ł͕ύX�A�f�t�H���g�L��");
		grid20_x0_y = new GridBagConstraints();
		grid20_x0_y.gridx = 0;
		grid20_x0_y.gridy = 4;
		grid20_x0_y.gridwidth = 2;
		grid20_x0_y.weightx = 1.0;
		grid20_x0_y.fill = GridBagConstraints.HORIZONTAL;
		grid20_x0_y.insets = MainFrame.INSETS_0_25_0_5;
		add(disableLimitWidthResizeCheckBox, grid20_x0_y);
		limitWidthResizeLabel.setText("�ՊE���̐ݒ�");
		limitWidthResizeLabel.setForeground(Color.blue);
	//	limitWidthResizeLabel.setToolTipText("�ՊE���́A�ʏ�R�}���h�@full�R�}���h�̏���px�Ŏw��");
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
		limitHeightLabel.setText("�ՊE�����ݒ�");
		limitHeightLabel.setForeground(Color.blue);
		limitHeightLabel.setToolTipText("�ՊE���A4:3�@16:9�̏��Ɏw��A�j�R�������͗���385px");
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
		disableLinefeedResizeCheckBox.setText("���s���T�C�Y����");
		disableLinefeedResizeCheckBox.setForeground(Color.blue);
		disableLinefeedResizeCheckBox.setSelected(false);
		disableLinefeedResizeCheckBox.setToolTipText("��rev.�ł͕ύX�A�f�t�H���g�L��");
		grid20_x0_y = new GridBagConstraints();
		grid20_x0_y.gridx = 0;
		grid20_x0_y.gridy = 7;
		grid20_x1_y.fill = GridBagConstraints.HORIZONTAL;
		grid20_x0_y.insets = MainFrame.INSETS_0_25_0_5;
		add(disableLinefeedResizeCheckBox, grid20_x0_y);
		disableDoubleResizeCheckBox.setText("�_�u�����T�C�Y����");
		disableDoubleResizeCheckBox.setForeground(Color.blue);
		disableDoubleResizeCheckBox.setSelected(false);
		disableDoubleResizeCheckBox.setToolTipText("��rev.�ł͕ύX�A�f�t�H���g�L��");
		grid20_x0_y = new GridBagConstraints();
		grid20_x0_y.gridx = 1;
		grid20_x0_y.gridy = 7;
		grid20_x0_y.weightx = 1.0;
		grid20_x0_y.fill = GridBagConstraints.HORIZONTAL;
		grid20_x0_y.insets = MainFrame.INSETS_0_5_0_5;
		add(disableDoubleResizeCheckBox, grid20_x0_y);
		disableFontDoublescaleCheckBox.setText("�t�H���g�������I��2�{�Ɋg�債�Ȃ�");
		disableFontDoublescaleCheckBox.setForeground(Color.blue);
		grid20_x0_y = new GridBagConstraints();
		grid20_x0_y.gridx = 0;
		grid20_x0_y.gridy = 9;
		grid20_x0_y.gridwidth = 2;
		grid20_x0_y.weightx = 1.0;
		grid20_x0_y.fill = GridBagConstraints.HORIZONTAL;
		grid20_x0_y.insets = MainFrame.INSETS_0_5_0_5;
		add(disableFontDoublescaleCheckBox,grid20_x0_y);
		fixedFontSizeCheckBox.setText("�C���t�H���g�T�C�Y�ݒ�");
		fixedFontSizeCheckBox.setForeground(Color.blue);
		fixedFontSizeCheckBox.setToolTipText("����l���C���o���܂��Bnormal big small�̏�(pt)");
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
		// ���̐ݒ�͍Ō��
		setDisableOriginalResizeCheckBox(exp.isDisableOriginalResize());
	}
}

