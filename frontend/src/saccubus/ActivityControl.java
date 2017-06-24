package saccubus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;

import saccubus.util.FileDropTarget;

public class ActivityControl {
	final private JPanel activityStatusPanel;	//�e�R���e�i�p�l��
	final private JPanel activityPane;				//�X�N���[�������f�[�^
	final private JScrollPane activityScroll;	//�X�N���[���g
	private ConcurrentHashMap<Integer, ListInfo> listInfoMap;
	private JTextComponent copyTarget;
	private int max_n = 0;

	ActivityControl(){
		activityPane = new JPanel();
		activityPane.setLayout(new BoxLayout(activityPane, BoxLayout.Y_AXIS));
		activityPane.setMaximumSize(new Dimension(200, Short.MAX_VALUE));
		activityScroll = new JScrollPane(activityPane);
		//�󋵕\���X�N���[���ʐݒ�
		activityScroll.getVerticalScrollBar().setBlockIncrement(0);	//=Block=Unit
		activityScroll.getVerticalScrollBar().setUnitIncrement(26);	//=jButton.Height()?
		activityStatusPanel = new JPanel();
		activityStatusPanel.setLayout(new BorderLayout());
		activityStatusPanel.add(activityScroll,BorderLayout.CENTER);
		activityStatusPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				"�󋵕\��", TitledBorder.LEADING, TitledBorder.TOP,
				new JLabel().getFont(), Color.red));
		listInfoMap = new ConcurrentHashMap<>();
	}
	JPanel getVisiblePane(){
		return activityStatusPanel;
	}
//	JPanel getActivityPane(){
//		return activityPane;
//	}
//	JScrollPane getScrollPane(){
//		return activityScroll;
//	}

	public void add(final ListInfo listinfo) {
		activityPane.add(listinfo);
		listinfo.setAlignmentX(Component.LEFT_ALIGNMENT);
		listinfo.addMouseListener(new ActivityRightClickMenu(copyTarget, listinfo));
		int n = listinfo.getKey();
		listInfoMap.put(n, listinfo);
		n = Math.max(n, listInfoMap.size());
		max_n = Math.max(max_n, n);
	}
	public synchronized void removeAll() {
		listInfoMap.clear();
		activityPane.removeAll();
		activityPane.repaint();
		activityPane.setLayout(new BoxLayout(activityPane, BoxLayout.Y_AXIS));
	}
	public DropTarget addDropTarget(JTextField videoIDField, boolean isDir) {
		return new DropTarget(activityStatusPanel, DnDConstants.ACTION_COPY,
				new FileDropTarget(videoIDField, isDir));
	}
	public void setPopup(JTextComponent tc) {
		copyTarget = tc;
	}

	private synchronized void remakeListView() {
		ConcurrentHashMap<Integer, ListInfo> copyMap = listInfoMap;
		listInfoMap = new ConcurrentHashMap<Integer, ListInfo>();
		activityPane.setVisible(false);
		activityPane.removeAll();
		activityPane.setVisible(true);
		//�ēo�^
		for(Entry<Integer, ListInfo> e: copyMap.entrySet()){
			ListInfo l = e.getValue();
			if(l!=null && !l.isDeleted()){
				add(l);
			//	Logger.MainLog.println("add Listinfo("+l.getKey()+"): "+l.getVid());
			}
		}
		activityPane.repaint();
	}
	private void deleteAction(ListInfo s) {
		if(s!=null){
			ConvertStopFlag flag = s.getStopFlag();
			// �����Ă����牽�����Ȃ�
			if(flag!=null && flag.isFinished()){
				s.setDelete();
				remakeListView();
			}
		}
		
	}
	private void copyAction(ListInfo s) {
		if(s!=null){
			String vid = s.getVid();
			if(vid!=null){
				vid = vid.replace("sm0_", "sm0-").replaceAll("_.*", "").replace("sm0-", "sm0_");
				copyTarget.setText(vid);
			}
		}
	}

	class ActionMenu extends AbstractAction{
		private final String name;
		private final Object target;
		public ActionMenu(String s, Object o){
			super(s);
			name = s;
			target = o;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if(name.equals(ACTION_DELETE_ITEM)){
				if(target instanceof ListInfo)
					deleteAction((ListInfo)target);
			}else
			if(name.equals(ACTION_COPY_VID)){
				if(target instanceof ListInfo)
					copyAction((ListInfo)target);
			}
		}
	}
	static final String ACTION_COPY_VID = "�R�s�yID";
	static final String ACTION_DELETE_ITEM = "�폜�A�C�e��";
	class ActivityRightClickMenu implements MouseListener {
		JTextComponent target;
		ListInfo source;
		public ActivityRightClickMenu(JTextComponent t, ListInfo s) {
			target = t;
			source = s;
		}
		@Override
		public void mouseClicked(MouseEvent e) {
		}
		@Override
		public void mouseEntered(MouseEvent e) {
		}
		@Override
		public void mousePressed(MouseEvent e) {
			mousePopup(e);
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			mousePopup(e);
		}
		@Override
		public void mouseExited(MouseEvent e) {
		}
		private void mousePopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				ListInfo s = (ListInfo)e.getSource();
				showPopup(s, e.getX(), e.getY());
				e.consume();
			}
		}
		private void showPopup(ListInfo s, int x, int y) {
			JPopupMenu popup = new JPopupMenu("popup");
			JMenuItem deleteItem;
			JMenuItem copyItem;
			deleteItem = new JMenuItem(new ActionMenu(ACTION_DELETE_ITEM, s));
			copyItem = new JMenuItem(new ActionMenu(ACTION_COPY_VID, s));
			popup.add(deleteItem);
			popup.add(copyItem);
			deleteItem.setText(ACTION_DELETE_ITEM);
			deleteItem.setToolTipText("�I���O�͍폜�o���Ȃ��i��~���Ă���N���b�N�j");
			copyItem.setText(ACTION_COPY_VID);
			copyItem.setToolTipText("����ID��URL/ID���Ƀy�[�X�g");
			popup.show(s, x, y);
		}
	}
}
