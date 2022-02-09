/**
 * added java source file
 */
package saccubus;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

/**
 * @author orz_e
 *
 */
public class JPanelHideable extends JPanel {
	private static ConcurrentHashMap<String, JPanelHideable> hideMap = new ConcurrentHashMap<>();
	// panel-name -> panel mapping, for status save
	private final JPanel contentPanel;
	private final JLabel hideLabel;
	private final JLabel showLabel;
	private boolean contentVisible;
	private String panelID;
	/**
	 *
	 */
	boolean isContentVisible(){
		return contentVisible;
	}
	private JPanelHideable() {
		super();
		contentPanel = new JPanel();
		contentPanel.setLayout(new GridBagLayout());
		hideLabel = new JLabel("消");
		hideLabel.setForeground(Color.red);
		showLabel = new JLabel("表示");
		showLabel.setForeground(Color.blue);
		contentVisible = true;
		FocusedHideSwitch adapter = new FocusedHideSwitch(this);
		hideLabel.addMouseListener(adapter);
		showLabel.addMouseListener(adapter);
	}
	private JPanelHideable(String title, Color col){
		this();
		super.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints(
			0, 0, 1, 1, 1.0, 1.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0), 5, 5
		);
		super.add(contentPanel, c);
		showLabel.setVisible(false);
		super.add(showLabel, c);
		super.setBorder(BorderFactory.createTitledBorder(
				MainFrame.CREATE_ETCHED_BORDER,
				title, TitledBorder.LEADING, TitledBorder.TOP,
				getFont(), col));
		contentPanel.setLayout(new GridBagLayout());
		contentPanel.setVisible(true);
	}
	public JPanelHideable(String name, String title, Color col){
		this(title, col);
		panelID = name;
		hideMap.put(panelID, this);
	}

	public JLabel getHideLabel(){
		return hideLabel;
	}

	public Component add(Component comp){
		contentPanel.add(comp);
		return contentPanel;
	}

	public void add(Component comp, Object c){
		contentPanel.add(comp, c);
	}

	public void hidePanel(){
		if(SwingUtilities.isEventDispatchThread()){
			hidePanelDo();
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					hidePanelDo();
				}
			});
		}
	}
	private void hidePanelDo(){
		contentVisible = false;
		contentPanel.setVisible(false);
		showLabel.setVisible(true);
		MainFrame.reflesh();
	}

	public void showPanel(){
		if(SwingUtilities.isEventDispatchThread()){
			showPanelDo();
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showPanelDo();
				}
			});
		}
	}

	private void showPanelDo(){
		contentVisible = true;
		showLabel.setVisible(false);
		contentPanel.setVisible(true);
		MainFrame.reflesh();
	}

	public static String getHideMap(){
		StringBuffer sb = new StringBuffer();
		for(JPanelHideable p: hideMap.values()){
			if(!p.contentVisible){
				sb.append(p.panelID);
				sb.append("\t");
			}
		}
		return sb.substring(0).trim();
	}

	public static void hidePanelAll() {
		for(JPanelHideable p: hideMap.values()){
			if(p!=null){
				p.hidePanel();
			}
		}
	}

	public static void showPanelAll() {
		for(JPanelHideable p: hideMap.values()){
			if(p!=null){
				p.showPanel();
			}
		}
	}

	public static void setHideMap(String hideMapString){
		if(hideMapString==null || hideMapString.isEmpty())
			return;
		String[] keys = hideMapString.split("\t");
		for(String key: keys){
			if(key.isEmpty())
				continue;
			JPanelHideable p = hideMap.get(key);
			if(p!=null)
				p.hidePanel();
		}
	}

	class FocusedHideSwitch extends MouseAdapter {
		private JPanelHideable target;

		FocusedHideSwitch(JPanelHideable comp){
			target = comp;
		}

		@Override
		public void  mouseClicked(MouseEvent e) {
			if(target.contentVisible){
				target.hidePanel();
			} else {
				target.showPanel();
			}
		}
	}
}
