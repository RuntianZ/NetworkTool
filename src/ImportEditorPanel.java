/**
 * @author Runtian Zhai
 * @license MIT
 * 
 */

import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class ImportEditorPanel extends SynchronizedPanel {

	private final SynchronizedPanel thisPanel = this;
	private File selectedFile;

	/**
	 * Create the panel.
	 */
	public ImportEditorPanel(Runnable r, CodeColorChooser panelCCC) {
		setSize(450, 425);
		setLayout(new VFlowLayout(VFlowLayout.TOP));

		JLabel lblNewLabel = new JLabel(
				"\u5355\u51FB\u201C\u5BFC\u5165\u201D\u4EE5\u4ECE\u6587\u4EF6\u7CFB\u7EDF\u4E2D\u5BFC\u5165\u5DF2\u6709\u7684\u914D\u8272\u65B9\u6848");
		lblNewLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
		add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel(
				"\u5355\u51FB\u201C\u5BFC\u51FA\u201D\u4EE5\u4FDD\u5B58\u5F53\u524D\u914D\u8272\u65B9\u6848");
		lblNewLabel_1.setFont(new Font("微软雅黑", Font.PLAIN, 16));
		add(lblNewLabel_1);

		JPanel panel = new JPanel();
		add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));

		JButton btnNewButton = new JButton("\u5BFC\u5165");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser jfc = new JFileChooser();
				jfc.setCurrentDirectory(new File("editor"));
				jfc.setFileFilter(new FileFilter() {

					@Override
					public boolean accept(File f) {
						if (f.getName().endsWith(".edt"))
							return true;
						return false;
					}

					@Override
					public String getDescription() {
						return "配色方案文件(*.edt)";
					}

				});
				jfc.showOpenDialog(thisPanel);
				selectedFile = jfc.getSelectedFile();
				if (selectedFile != null) {
					if (selectedFile.isFile()) {
						r.run();
					} else {
						System.out.println(selectedFile.getName());
						JOptionPane.showMessageDialog(thisPanel, "文件不存在", "错误", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		panel.add(btnNewButton);

		JButton btnNewButton_1 = new JButton("\u5BFC\u51FA");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser jfc = new JFileChooser();
				jfc.setCurrentDirectory(new File("editor"));
				jfc.setFileFilter(new FileFilter() {

					@Override
					public boolean accept(File f) {
						return true;
					}

					@Override
					public String getDescription() {
						return "配色方案文件(*.edt)";
					}

				});
				jfc.showSaveDialog(thisPanel);
				selectedFile = jfc.getSelectedFile();
				if (selectedFile != null) {
					if (!selectedFile.getName().endsWith(".edt"))
						selectedFile = new File(selectedFile.getAbsolutePath() + ".edt");
					if (selectedFile.isFile()) {
						int t = JOptionPane.showConfirmDialog(thisPanel, "确实要覆盖文件吗", 
								"覆盖文件", JOptionPane.YES_NO_OPTION);
						if (t != JOptionPane.YES_OPTION)
							return;
					}
					if (panelCCC.isEdited()) {
						JOptionPane.showMessageDialog(thisPanel, "请先保存当前设置",
								"错误", JOptionPane.ERROR_MESSAGE);
					}
					Main.mainPage.saveSettings(selectedFile);
					JOptionPane.showMessageDialog(thisPanel, "保存成功", "已保存", JOptionPane.PLAIN_MESSAGE);
				}
			}
		});
		panel.add(btnNewButton_1);
	}

	@Override
	public void buttonLock() {
	}

	@Override
	public void buttonUnlock() {
	}
	
	public File getSelectedFile() {
		return selectedFile;
	}

}
