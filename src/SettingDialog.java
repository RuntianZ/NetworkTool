/**
 * @author Runtian Zhai
 * @license MIT
 * 
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Vector;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


@SuppressWarnings("serial")
class SettingDialog extends PoppedDialog {

	private final JPanel contentPanel = new JPanel();
	private SynchronizedPanel panel;
	private SingleSelectionList<SettingPackage> list;
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

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			Vector<SettingPackage> vec = new Vector<>();
			vec.add(new SettingPackage("编辑器字体", new JFontChooser(font)));
			panelCCC = new CodeColorChooser(colors);
			savedPackage = new SettingPackage("编辑器风格", panelCCC);
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
			vec.add(new SettingPackage("配色方案", panelIE));
			vec.add(new SettingPackage("工具选项", new DefaultOperationPanel()));
			list = new SingleSelectionList<>(vec);
			list.setFont(new Font("微软雅黑", Font.PLAIN, 18));
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
