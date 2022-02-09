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
	long SECOND_IN_MILIS = 1000;
	long restsec;
	static boolean started = false;
	private void doSetting(long time){
		timeout = time;
		restsec = time/SECOND_IN_MILIS;
		log.println("SelfTerminate timeout="+timeout+" msec");
		if(timer!=null){
			timer.cancel();
			timer = null;
		}
		if(timer2!=null){
			timer2.cancel();
			timer2 = null;
		}
		timer2 = new Timer(false);
		timertask2 = new TimerTask() {
			@Override
			public void run() {
				restTimeMenu.setText("自動終了まで"+restsec+"秒");
				if(!started){
					if(restartFlag.getAndSet(false))
						started = true;
				}else{
					// timer had started
					if(timer==null){
						startTimer();
					}else {
						restsec--;
						// timer!=null
						if(restartFlag.getAndSet(false)){
							// restart
							restsec = timeout/SECOND_IN_MILIS;
							timer.cancel();
							timer = null;
							startTimer();
						}
					}
				}
			}
		};
		timer2.schedule(timertask2, SECOND_IN_MILIS, SECOND_IN_MILIS);
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
		timer.schedule(timertask, timeout+SECOND_IN_MILIS);
	}
	public static void restartTimer(){
		restartFlag.set(true);
	}
	public static void restartTimer2(){
		restartFlag.set(started);
	}

	boolean choice = false;
	JTextField inputArea = new JTextField();
	String inputValue;
	String[] units = {"秒","分","時間 "};
	JComboBox<String> cBox = new JComboBox<>(units);
	public void setUp(long timedef, boolean enable) {
		if(enable){
			final JDialog dialog = new JDialog(parent,"時間制限設定",true);
			dialog.setBounds(parent.getX()+64, parent.getY()+164, 168, 110);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setLayout(new BorderLayout(10, 5));
			dialog.add(new JLabel("時間制限値を入力してください"),
					BorderLayout.NORTH);
			JPanel inputPanel = new JPanel();
			inputPanel.setLayout(new BorderLayout());
			inputArea.setText(""+(timedef/SECOND_IN_MILIS));
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
		}

		if(enable && choice){
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

			int ans = JOptionPane.NO_OPTION;
			if(timeval>0){
				ans = JOptionPane.showConfirmDialog(
					parent,
					"設定値は "+inputValue+units[unitsel]
						+"("+timeval+"ミリ秒) でよろしいですか?",
					"確認",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.INFORMATION_MESSAGE);
			}
			if(ans==JOptionPane.YES_OPTION && timeval > 0){
				doSetting(timeval);
			//	startTimer();
				return;
			}
		}
		// disable
		if(timer!=null){
			timer.cancel();
			timer = null;
			timeout = Long.MAX_VALUE;
			if(timer2!=null){
				timer2.cancel();
				timer2 = null;
			}
			restTimeMenu.setText(" ");
			log.println("Timer canceled.");
		}
	}

	JCheckBoxMenuItem jMenuSelfTerminate = new JCheckBoxMenuItem();
	public JMenuItem initMenu(final long timeval) {
		jMenuSelfTerminate.setText("自動終了 時間制限設定");
		jMenuSelfTerminate.setToolTipText("変換実行がなくGUI操作もない状態で時間制限以上経つと終了する");
		jMenuSelfTerminate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setUp(timeval, jMenuSelfTerminate.isSelected());
			}
		});
		return jMenuSelfTerminate;
	}
	private JMenu restTimeMenu = new JMenu();
	public JMenu getRestTimeMenu() {
		return restTimeMenu;
	}
}
