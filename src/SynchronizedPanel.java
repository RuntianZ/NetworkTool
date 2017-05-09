import java.awt.BorderLayout;
import java.awt.Font;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class SynchronizedPanel extends JPanel{
	public abstract void buttonLock();
	public abstract void buttonUnlock();
}

class DefaultSynchronizedPanel extends SynchronizedPanel {

	@Override
	public void buttonLock() {}

	@Override
	public void buttonUnlock() {}
	
	public DefaultSynchronizedPanel() {
		setSize(450, 425);
		setLayout(new BorderLayout());
		JLabel lbl = new JLabel(" �����ѡ������ѡ��һ��");
		lbl.setFont(new Font("΢���ź�", Font.PLAIN, 16));
		add(lbl, BorderLayout.NORTH);
	}
	
}