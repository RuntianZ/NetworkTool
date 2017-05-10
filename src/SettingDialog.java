import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Vector;

import javax.swing.JList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;

class SettingDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private SynchronizedPanel panel;
	private JList<SettingPackage> list;
	private CodeColorChooser panelCCC;
	private SettingPackage savedPackage;
	private ImportEditorPanel panelIE;
	/**
	 * Create the dialog.
	 */
	public SettingDialog(ColorLibrary colors, Font font) {
		setModal(true);
		setTitle("\u7F16\u8F91\u5668\u8BBE\u7F6E");
		setResizable(false);
		setSize(600, 447);
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
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			Vector<SettingPackage> vec = new Vector<>();
			vec.add(new SettingPackage("±à¼­Æ÷×ÖÌå", new JFontChooser(font)));
			panelCCC = new CodeColorChooser(colors);
			savedPackage = new SettingPackage("±à¼­Æ÷·ç¸ñ", panelCCC);
			vec.add(savedPackage);
			panelIE = new ImportEditorPanel(() -> {
				File f = panelIE.getSelectedFile();
				ObjectInputStream in = null;
				ColorLibrary thecolors = null;
				try {
					in = new ObjectInputStream(new FileInputStream(f));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					thecolors = (ColorLibrary) in.readObject();
					in.close();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				list.setSelectedValue(savedPackage, true);
				panelCCC.setColors(thecolors);
				listSelected();
			}, panelCCC);
			vec.add(new SettingPackage("ÅäÉ«·½°¸", panelIE));
			list = new JList<>(vec);
			list.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 18));
			list.setPreferredSize(new Dimension(125, 425));
			list.addListSelectionListener(e -> {
				listSelected();
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
	
	private void listSelected() {
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
	}

}
