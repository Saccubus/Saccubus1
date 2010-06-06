/**
 * 
 */
package saccubus;

import javax.swing.JPanel;
import java.awt.Frame;
import java.awt.BorderLayout;
import javax.swing.JDialog;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;

import saccubus.info.NicoInfo;
import saccubus.info.RootInfo;

/**
 * @author PSI
 *
 */
public class PasswordDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JPanel ButtonPanel = null;

	private JButton OKButton = null;

	private JButton CancelButton = null;

	private JPanel DescPanel = null;

	private JLabel DescLabel1 = null;

	private JLabel DescLabel2 = null;

	private JPanel CenterPanel = null;

	private JLabel PassLabel = null;

	private JLabel MailLabel = null;

	private JTextField MailField = null;

	private JPasswordField PasswordField = null;
	
	private String InputedMailaddr = null;
	private String InputedPassword = null;

	/**
	 * @param owner
	 */
	public PasswordDialog(Frame owner) {
		super(owner);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setTitle("ログイン");
		this.setContentPane(getJContentPane());
	}
	/**
	 * 親情報を設定して、ダイアログを表示する。
	 * @param info
	 * @return
	 */
	public NicoInfo showDialog(NicoInfo info){
		//元々の情報を設定
		this.MailField.setText(info.getMailaddr());
		this.PasswordField.setText(info.getPassword());
		//サイズを設定
		this.pack();
		this.setSize(300, this.getHeight());
		this.setLocationRelativeTo(this.getOwner());
		//this.setSize(300, 200);
		this.setModal(true);
		this.setVisible(true);
		//設定する。
		if(InputedMailaddr == null || InputedPassword == null){
			return null;
		}
		return new NicoInfo(InputedMailaddr,InputedPassword);
	}
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getButtonPanel(), BorderLayout.SOUTH);
			jContentPane.add(getDescPanel(), BorderLayout.NORTH);
			jContentPane.add(getCenterPanel(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes ButtonPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getButtonPanel() {
		if (ButtonPanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.insets = new Insets(5, 0, 5, 5);
			gridBagConstraints1.gridy = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			ButtonPanel = new JPanel();
			ButtonPanel.setLayout(new GridBagLayout());
			ButtonPanel.add(getOKButton(), gridBagConstraints);
			ButtonPanel.add(getCancelButton(), gridBagConstraints1);
		}
		return ButtonPanel;
	}

	/**
	 * This method initializes OKButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getOKButton() {
		if (OKButton == null) {
			OKButton = new JButton();
			OKButton.setText("OK");
			OKButton.addActionListener(new java.awt.event.ActionListener() {   
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					InputedMailaddr = MailField.getText();
					InputedPassword = new String(PasswordField.getPassword());
					setVisible(false);
					dispose();
				}
			
			});
		}
		return OKButton;
	}

	/**
	 * This method initializes CancelButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCancelButton() {
		if (CancelButton == null) {
			CancelButton = new JButton();
			CancelButton.setText("Cancel");
			CancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					InputedMailaddr = null;
					InputedPassword = null;
					setVisible(false);
					dispose();
				}
			});
		}
		return CancelButton;
	}

	/**
	 * This method initializes DescPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getDescPanel() {
		if (DescPanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			gridBagConstraints3.insets = new Insets(0, 5, 5, 5);
			gridBagConstraints3.gridy = 1;
			DescLabel2 = new JLabel();
			DescLabel2.setText("パスワードを入力してください。");
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.insets = new Insets(5, 5, 0, 5);
			gridBagConstraints2.fill = GridBagConstraints.NONE;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			DescLabel1 = new JLabel();
			DescLabel1.setText("ニコニコ動画のメールアドレスと");
			DescPanel = new JPanel();
			DescPanel.setLayout(new GridBagLayout());
			DescPanel.add(DescLabel1, gridBagConstraints2);
			DescPanel.add(DescLabel2, gridBagConstraints3);
		}
		return DescPanel;
	}

	/**
	 * This method initializes CenterPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getCenterPanel() {
		if (CenterPanel == null) {
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.gridy = 1;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.insets = new Insets(0, 0, 5, 5);
			gridBagConstraints7.gridx = 1;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints6.gridy = 0;
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.insets = new Insets(5, 0, 5, 5);
			gridBagConstraints6.gridx = 1;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints5.gridy = 0;
			MailLabel = new JLabel();
			MailLabel.setText("メールアドレス");
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.insets = new Insets(0, 5, 5, 5);
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.gridy = 1;
			PassLabel = new JLabel();
			PassLabel.setText("パスワード");
			CenterPanel = new JPanel();
			CenterPanel.setLayout(new GridBagLayout());
			CenterPanel.add(PassLabel, gridBagConstraints4);
			CenterPanel.add(MailLabel, gridBagConstraints5);
			CenterPanel.add(getMailField(), gridBagConstraints6);
			CenterPanel.add(getPasswordField(), gridBagConstraints7);
		}
		return CenterPanel;
	}

	/**
	 * This method initializes MailField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getMailField() {
		if (MailField == null) {
			MailField = new JTextField();
		}
		return MailField;
	}

	/**
	 * This method initializes PasswordField	
	 * 	
	 * @return javax.swing.JPasswordField	
	 */
	private JPasswordField getPasswordField() {
		if (PasswordField == null) {
			PasswordField = new JPasswordField();
		}
		return PasswordField;
	}
	/**
	 * デフォルトのログイン情報を設定するダイアログを表示する。
	 *
	 */
	public static void setDefaultLoginInfo(Frame frame){
		RootInfo info = RootInfo.getDefault();
		//ダイアログ生成
		PasswordDialog dlg = new PasswordDialog(frame);
		NicoInfo nInfo = dlg.showDialog(info.getNicoInfo());
		if(nInfo != null){
			info.setNicoInfo(nInfo);
			//情報を保存
			RootInfo.saveDefault();
		}
	}
}
