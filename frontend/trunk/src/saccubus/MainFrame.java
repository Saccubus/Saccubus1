/**
 * 
 */
package saccubus;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import saccubus.filemanager.FileManagerFrame;

/**
 * @author PSI
 *
 */
public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	public static final Image WinIcon = Toolkit.getDefaultToolkit().createImage(saccubus.MainFrame.class.getResource("icon32.png"));

	private JPanel jContentPane = null;
	private JMenuBar MainMenuBar = null;
	private JMenu FileMenu = null;
	private JMenuItem ExitMenuItem = null;
	private JMenu SettingMenu = null;
	private JMenuItem LoginInfoMenuItem = null;
	private JMenu HelpMenu = null;
	private JMenuItem AboutMenuItem = null;
	private JMenuItem FileManagerMenuItem = null;
	private JMenuItem InfoMenuItem = null;

	/**
	 * This is the default constructor
	 */
	public MainFrame() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(300, 200);
		this.setJMenuBar(getMainMenuBar());
		this.setContentPane(getJContentPane());
		this.setIconImage(WinIcon);
		this.setTitle("さきゅばす");
	}
	
	FileManagerFrame FileManagerFrame = null;
	private void showFileManager(){
		if(FileManagerFrame == null){
			FileManagerFrame = new FileManagerFrame(this);
		}
		if(FileManagerFrame.isVisible()){
			FileManagerFrame.setVisible(true);
		}else{
			FileManagerFrame.showFrame();
		}
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
		}
		return jContentPane;
	}

	/**
	 * This method initializes MainMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getMainMenuBar() {
		if (MainMenuBar == null) {
			MainMenuBar = new JMenuBar();
			MainMenuBar.add(getFileMenu());
			MainMenuBar.add(getSettingMenu());
			MainMenuBar.add(getHelpMenu());
		}
		return MainMenuBar;
	}

	/**
	 * This method initializes FileMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getFileMenu() {
		if (FileMenu == null) {
			FileMenu = new JMenu();
			FileMenu.setText("ファイル");
			FileMenu.add(getFileManagerMenuItem());
			FileMenu.addSeparator();
			FileMenu.add(getExitMenuItem());
		}
		return FileMenu;
	}

	/**
	 * This method initializes ExitMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getExitMenuItem() {
		if (ExitMenuItem == null) {
			ExitMenuItem = new JMenuItem();
			ExitMenuItem.setText("終了");
			ExitMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.exit(0);
				}
			});
		}
		return ExitMenuItem;
	}

	/**
	 * This method initializes SettingMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getSettingMenu() {
		if (SettingMenu == null) {
			SettingMenu = new JMenu();
			SettingMenu.setText("設定");
			SettingMenu.add(getLoginInfoMenuItem());
			SettingMenu.add(getInfoMenuItem());
		}
		return SettingMenu;
	}

	/**
	 * This method initializes LoginInfoMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getLoginInfoMenuItem() {
		if (LoginInfoMenuItem == null) {
			LoginInfoMenuItem = new JMenuItem();
			LoginInfoMenuItem.setText("ログイン情報の設定");
			LoginInfoMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					PasswordDialog.setDefaultLoginInfo(MainFrame.this);
				}
			});
		}
		return LoginInfoMenuItem;
	}

	/**
	 * This method initializes HelpMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getHelpMenu() {
		if (HelpMenu == null) {
			HelpMenu = new JMenu();
			HelpMenu.setText("ヘルプ");
			HelpMenu.add(getAboutMenuItem());
		}
		return HelpMenu;
	}

	/**
	 * This method initializes AboutMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getAboutMenuItem() {
		if (AboutMenuItem == null) {
			AboutMenuItem = new JMenuItem();
			AboutMenuItem.setText("バージョン方法");
			AboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					MainFrame_AboutBox dlg = new MainFrame_AboutBox(MainFrame.this);
					dlg.showDialog();
				}
			});
		}
		return AboutMenuItem;
	}

	/**
	 * This method initializes FileManagerMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getFileManagerMenuItem() {
		if (FileManagerMenuItem == null) {
			FileManagerMenuItem = new JMenuItem();
			FileManagerMenuItem.setText("ファイルマネージャ");
			FileManagerMenuItem.addActionListener(new java.awt.event.ActionListener() {   
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					showFileManager();
				}
			
			});
		}
		return FileManagerMenuItem;
	}

	/**
	 * This method initializes InfoMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getInfoMenuItem() {
		if (InfoMenuItem == null) {
			InfoMenuItem = new JMenuItem();
			InfoMenuItem.setText("デフォルト変換設定");
		}
		return InfoMenuItem;
	}

}
