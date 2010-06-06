/**
 * 
 */
package saccubus;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.GridLayout;

/**
 * @author PSI
 *
 */
public class EditorFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;
	private File EditingFile;

	private JScrollPane EditScrollPane = null;

	private JTextArea EditTextArea = null;

	private JPanel MenuPanel = null;

	private JButton SaveEndPanel = null;

	private JButton CancelPanel = null;
	private JFrame Parent = null;
	/**
	 * This is the default constructor
	 */
	public EditorFrame(JFrame parent,File file) {
		super();
		EditingFile = file;
		Parent = parent;
		initialize();
		readText();
	}
	/**
	 * ウインドウ表示
	 *
	 */
	public void wshow(){
		this.pack();
		this.setLocationRelativeTo(Parent);
		this.setVisible(true);
	}
	/**
	 * ファイルを読み込む
	 */
	private void readText(){
		if(EditingFile.exists() && EditingFile.isFile() && EditingFile.canRead() && EditingFile.canWrite()){
			try {
				StringBuffer sb = new StringBuffer();
				InputStreamReader in = new InputStreamReader(new FileInputStream(EditingFile), "UTF-8");
				BufferedReader br = new BufferedReader(in);
				String str;
				while((str = br.readLine()) != null){
					sb.append(str);
					sb.append("\n");
				}
				EditTextArea.setText(sb.toString());
				in.close();
			} catch (UnsupportedEncodingException e) {
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				EditTextArea.setText("ファイルの読み込みに失敗しました。"+e.getMessage());
				SaveEndPanel.setEnabled(false);
			} catch (IOException e) {
				e.printStackTrace();
				EditTextArea.setText("ファイルの読み込みに失敗しました。"+e.getMessage());
				SaveEndPanel.setEnabled(false);
			}
		}else{
			EditTextArea.setText("ファイルの読み込みに失敗しました");
			SaveEndPanel.setEnabled(false);
		}
	}
	/**
	 * テキストをセーブする。
	 */
	private boolean saveText(){
		try {
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(EditingFile),"UTF-8");
			BufferedWriter bw = new BufferedWriter(osw);
			bw.write(EditTextArea.getText());
			bw.close();
			return true;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "エンコーディング方式が間違っています。", "保存に失敗", JOptionPane.ERROR_MESSAGE);
			return false;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "ファイルが見つかりません。", "保存に失敗", JOptionPane.ERROR_MESSAGE);
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "IOエラーです。"+e.getMessage(), "保存に失敗", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	/**
	 * ウインドウを閉じる
	 *
	 */
	private void closeWindow(){
		this.setVisible(false);
		this.dispose();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(400, 300);
		this.setContentPane(getJContentPane());
		this.setTitle("編集中："+EditingFile.getName());
		this.setIconImage(MainFrame.WinIcon);
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
			jContentPane.add(getEditScrollPane(), BorderLayout.CENTER);
			jContentPane.add(getMenuPanel(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes EditScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getEditScrollPane() {
		if (EditScrollPane == null) {
			EditScrollPane = new JScrollPane();
			EditScrollPane.setViewportView(getEditTextArea());
		}
		return EditScrollPane;
	}

	/**
	 * This method initializes EditTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getEditTextArea() {
		if (EditTextArea == null) {
			EditTextArea = new JTextArea();
		}
		return EditTextArea;
	}

	/**
	 * This method initializes MenuPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getMenuPanel() {
		if (MenuPanel == null) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(1);
			MenuPanel = new JPanel();
			MenuPanel.setLayout(gridLayout);
			MenuPanel.add(getSaveEndPanel(), null);
			MenuPanel.add(getCancelPanel(), null);
		}
		return MenuPanel;
	}

	/**
	 * This method initializes SaveEndPanel	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSaveEndPanel() {
		if (SaveEndPanel == null) {
			SaveEndPanel = new JButton();
			SaveEndPanel.setText("セーブして終了");
			SaveEndPanel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(saveText()){//セーブに成功すれば
						closeWindow();
					}
				}
			});
		}
		return SaveEndPanel;
	}

	/**
	 * This method initializes CancelPanel	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCancelPanel() {
		if (CancelPanel == null) {
			CancelPanel = new JButton();
			CancelPanel.setText("キャンセル");
			CancelPanel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					closeWindow();
				}
			});
		}
		return CancelPanel;
	}

}
