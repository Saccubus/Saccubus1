package saccubus;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

public class ClearButton extends JTextField {

	MouseClickAdapter adp;
	JTextComponent textComponent;

	public ClearButton(JTextComponent tc){
		super();
		this.setForeground(Color.black);
		this.setText("✕");
		this.setEditable(false);
		textComponent = tc;
		adp = new MouseClickAdapter(textComponent);
		this.addMouseListener(adp);
	}

	public class MouseClickAdapter extends MouseAdapter {
		private JTextComponent target;

		public MouseClickAdapter(JTextComponent cmp){
			target = cmp;
		}
		@Override
		public void mouseClicked(MouseEvent e) {
			target.setText("");
		}
	}

}
