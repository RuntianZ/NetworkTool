import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DefaultOperationPanel extends SynchronizedPanel {

	private JButton btnApply;
	
	/**
	 * Create the panel.
	 */
	public DefaultOperationPanel() {
		setSize(450, 425);
		setLayout(new BorderLayout());
		{
			JPanel btnPanel = new JPanel();
			btnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

			JLabel label = new JLabel("\u5355\u51FB\u5E94\u7528\u4FDD\u5B58\u8BBE\u7F6E");
			label.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 16));
			btnPanel.add(label);
			
			btnApply = new JButton("\u5E94\u7528");
			btnApply.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					//TODO
					new Thread(() -> {
						buttonLock();
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Main.mainPage.workingLock.lock();
						buttonUnlock();
						Main.mainPage.workingLock.unlock();
					}).start();
				}
			});
			btnApply.setEnabled(false);
			btnPanel.add(btnApply);
			add(btnPanel, BorderLayout.SOUTH);
		}
	}

	@Override
	public void buttonLock() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void buttonUnlock() {
		// TODO Auto-generated method stub
		
	}

}
