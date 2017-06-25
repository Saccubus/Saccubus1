package saccubus.util;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import saccubus.MainFrame;

/*
 * 実行スレッドや操作がない状態で
 * 一定時間がきたらSaccubusを終了する
 */
public class SelfTerminate {
	private long timeout;
	private TimerTask timertask;
	private Timer timer;
	private TimerTask timertask2;
	private Timer timer2;
	private Logger log;
	private MainFrame parent;
	private static AtomicBoolean restartFlag = new AtomicBoolean(false);

	public SelfTerminate(Logger logger, MainFrame frame){
		parent = frame;
		log = logger;
		timeout = Long.MAX_VALUE;
		timertask = null;
	}
	private void doSetting(long time){
		timeout = time;
		log.println("SelfTerminate timeout="+timeout);
		if(timer2==null){
			timer2 = new Timer(false);
			timertask2 = new TimerTask() {
				@Override
				public void run() {
					if(restartFlag.getAndSet(false)){
						if(timer!=null){
							timer.cancel();
							timer = null;
							startTimer();
						}
					}
				}
			};
			timer2.schedule(timertask2, 0, 500);
		}
	}
	private void doTerminate(){
		log.println("SelfTerminate: 時すでに時間切れ.\n"+new Date());
		if(timer!=null) timer.cancel();
		if(timer2!=null) timer2.cancel();
		parent.jMenuFileExit_actionPerformed(null);
	}
	private synchronized void startTimer(){
		timer = new Timer(false);
		timertask = new TimerTask() {
			@Override
			public void run() {
				doTerminate();
			}
		};
		timer.schedule(timertask, timeout);
		jMenuSelfTerminate.setText("自動終了 時間制限 オン "+(timeout/1000)+"秒");
	}
	public static void restartTimer(){
		restartFlag.set(true);
	}

	JMenu selfTerminateMenu = new JMenu();
	boolean choice = false;
	JTextField inputArea = new JTextField();
	String inputValue;
	String[] units = {"秒","分","時間 "};
	JComboBox<String> cBox = new JComboBox<>(units);
	public void setUp(long timedef) {
		final JDialog dialog = new JDialog(parent,"時間制限設定",true);
		dialog.setBounds(parent.getX()+64, parent.getY()+164, 168, 110);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setLayout(new BorderLayout(10, 5));
		dialog.add(new JLabel("時間制限値を入力してください"),
				BorderLayout.NORTH);
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BorderLayout());
		inputArea.setText(""+(timedef/1000));
		inputPanel.add(inputArea,BorderLayout.CENTER);
		cBox.setSelectedIndex(0);
		inputPanel.add(cBox,BorderLayout.EAST);
		dialog.add(inputPanel, BorderLayout.CENTER);
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				choice = true;
				dialog.dispose();
			}
		});
		JButton cancel = new JButton("キャンセル");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				choice = false;
				dialog.dispose();
			}
		});
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(ok);
		buttonPanel.add(cancel);
		dialog.add(buttonPanel, BorderLayout.SOUTH);
		dialog.setVisible(true);
		jMenuSelfTerminate.setSelected(false);

		if(choice){
			inputValue = inputArea.getText();
			int unitsel = cBox.getSelectedIndex();
			long[] unitLength = { 1000, 60000, 360000}; 
			long timeval = 0;
			try {
				timeval = Long.decode(inputValue);
			}catch(NumberFormatException e){
				timeval = 0;
			}
			timeval *= unitLength[unitsel];

			int ans =
			JOptionPane.showConfirmDialog(
				parent,
				"設定値は "+inputValue+units[unitsel]
					+"("+timeval+"ミリ秒) でよろしいですか?",
				"確認",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.INFORMATION_MESSAGE);
			if(ans==JOptionPane.YES_OPTION ){
				if(timeval > 0){
					doSetting(timeval);
					startTimer();
				}
			}else{
				if(timer!=null){
					timer.cancel();
					timer = null;
					timeout = Long.MAX_VALUE;
					log.println("Timer canceled, timeout="+timeout);
				}
				jMenuSelfTerminate.setText("自動終了 時間制限設定");
			}
		}
		jMenuSelfTerminate.setSelected(true);
	}
	JCheckBoxMenuItem jMenuSelfTerminate = new JCheckBoxMenuItem();
	public JMenuItem initMenu(final long timeval) {
		jMenuSelfTerminate.setText("自動終了 時間制限設定");
		jMenuSelfTerminate.setToolTipText("変換実行がなくGUI操作もない状態で時間制限以上経つと終了する");
		jMenuSelfTerminate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setUp(timeval);
			}
		});
		return jMenuSelfTerminate;
	}
}
