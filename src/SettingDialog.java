import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;

class SettingDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private SynchronizedPanel panel;

	/**
	 * Create the dialog.
	 */
	public SettingDialog(ColorLibrary colors, Font font) {
		setModal(true);
		setTitle("\u7F16\u8F91\u5668\u8BBE\u7F6E");
		setResizable(false);
		setSize(600, 447);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		{
			int x = Main.mainPage.getX() + Main.mainPage.getWidth() / 2 - 300;
			if (x < 50)
				x = 50;
			if (x > toolkit.getScreenSize().getWidth() - 650)
				x = (int) (toolkit.getScreenSize().getWidth() - 650);
			int y = Main.mainPage.getY() + Main.mainPage.getHeight() / 2 - 225;
			if (y < 50)
				y = 50;
			if (y > toolkit.getScreenSize().getHeight() - 497)
				y = (int) (toolkit.getScreenSize().getHeight() - 497);
			setLocation(x, y);
		}
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			Vector<SettingPackage> vec = new Vector<>();
			vec.add(new SettingPackage("±à¼­Æ÷×ÖÌå", new JFontChooser(font)));
			vec.add(new SettingPackage("±à¼­Æ÷·ç¸ñ", new CodeColorChooser(colors)));
			JList<SettingPackage> list = new JList<>(vec);
			list.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 18));
			list.setPreferredSize(new Dimension(125, 425));
			list.addListSelectionListener(e -> {
				SettingPackage spk = list.getSelectedValue();
				if (spk != null) {
					contentPanel.remove(panel);
					panel = spk.panel;
					panel.buttonLock();
					contentPanel.add(panel, BorderLayout.CENTER);
					contentPanel.updateUI();
					repaint();
					new Thread(() -> {
						Main.mainPage.workingLock.lock();
						panel.buttonUnlock();
						Main.mainPage.workingLock.unlock();
					}).start();
				}
			});
			contentPanel.add(list, BorderLayout.WEST);
		}
		{
			panel = new DefaultSynchronizedPanel();
			contentPanel.add(panel, BorderLayout.CENTER);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("\u5173\u95ED");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}
	
	private class SettingPackage {
		String title;
		SynchronizedPanel panel;
		public SettingPackage(String title, SynchronizedPanel panel){	
			this.title = title;
			this.panel = panel;
		}
		
		@Override
		public String toString() {
			return title;
		}
	}

}
