/**
 * @author Runtian Zhai
 * @license MIT
 * 
 */

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JComboBox;

@SuppressWarnings("serial")
public class DefaultOperationPanel extends SynchronizedPanel {

	private JButton btnApply;
	private JLabel lblHint;
	private ActionListener listener;
	private JComboBox<String> combo1,combo2;
	private String choice1, choice2, choice3, defaultDir;
	private boolean isEdited;
	private final JPanel thisPanel = this;
	
	/**
	 * Create the panel.
	 */
	public DefaultOperationPanel() {
		setSize(450, 425);
		isEdited = false;
		setLayout(new BorderLayout());
		{
			JPanel btnPanel = new JPanel();
			btnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			try {
				BufferedReader reader = new BufferedReader(new FileReader(MainPage.fileOptions));
				choice1 = reader.readLine();
				choice2 = reader.readLine();
				choice3 = reader.readLine();
				defaultDir = reader.readLine();
				if (choice1 == null)
					choice1 = "内置浏览器";
				if (choice2 == null)
					choice2 = "查看网页";
				if (choice3 == null)
					choice3 = "(((ht|f)tp(s?))\\://)"
							+ "(www.|[a-zA-Z].)[a-zA-Z0-9\\-\\.]+\\."
							+ "(com|edu|gov|mil|net|org|biz|info|name|museum|us|ca|uk|cn)"
							+ "(\\:[0-9]+)*(/($|[a-zA-Z0-9\\.\\,\\;\\?\\'\\\\\\+&%\\$#\\=~_\\-]+))*";
				if (defaultDir == null)
					defaultDir = "defaultDir";
				reader.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			lblHint = new JLabel("\u5355\u51FB\u5E94\u7528\u4FDD\u5B58\u8BBE\u7F6E");
			lblHint.setFont(new Font("微软雅黑", Font.PLAIN, 16));
			btnPanel.add(lblHint);
			
			btnApply = new JButton("\u5E94\u7528");
			btnApply.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					new Thread(() -> {
						buttonLock();
						String st1 = (String)combo1.getSelectedItem();
						String st2 = (String)combo2.getSelectedItem();
						if (st1 == null || st2 == null)
							return;
						choice1 = st1;
						choice2 = st2;
						try {
							FileWriter writer = new FileWriter(MainPage.fileOptions);
							writer.write(choice1 + "\n" + choice2 + "\n" + choice3 + "\n" + defaultDir);
							writer.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						lblHint.setVisible(false);
						Main.mainPage.updateOptions();
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
			btnPanel.add(btnApply);
			add(btnPanel, BorderLayout.SOUTH);
		}
		{
			listener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (isEdited) {
						lblHint.setVisible(true);
						return;
					}
					String st1 = (combo1 == null) ? null : (String)combo1.getSelectedItem();
					String st2 = (combo2 == null) ? null : (String)combo2.getSelectedItem();
					if (choice1.equals(st1) && choice2.equals(st2))
						lblHint.setVisible(false);
					else
						lblHint.setVisible(true);
				}
			};
			
			JPanel panel = new JPanel(new VFlowLayout(VFlowLayout.TOP));
			JPanel panel_1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JLabel lbl_1 = new JLabel("\u9ED8\u8BA4\u7F51\u9875\u6D4F\u89C8\u5668");
			lbl_1.setFont(new Font("微软雅黑", Font.PLAIN, 16));
			panel_1.add(lbl_1);
			final String[] st_1 = {"内置浏览器", "桌面浏览器"};
			combo1 = new MyComboBox<String>(st_1);
			combo1.setSelectedItem(choice1);
			panel_1.add(combo1);
			panel.add(panel_1);

			JPanel panel_2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JLabel lbl_2 = new JLabel("\u6253\u5F00\u94FE\u63A5\u65F6");
			lbl_2.setFont(new Font("微软雅黑", Font.PLAIN, 16));
			panel_2.add(lbl_2);
			final String[] st_2 = {"查看网页", "查看代码"};
			combo2 = new MyComboBox<String>(st_2);
			combo2.setSelectedItem(choice2);
			panel_2.add(combo2);
			panel.add(panel_2);

			JPanel panel_3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JLabel lbl_3 = new JLabel("深度挖掘正则表达式");
			lbl_3.setFont(new Font("微软雅黑", Font.PLAIN, 16));
			panel_3.add(lbl_3);
			JButton btnRegex = new JButton("修改");
			btnRegex.addActionListener(e -> {
				String st = JOptionPane.showInputDialog(thisPanel, "输入正则表达式", choice3);
				System.out.println(st);
				if (st != null && !st.equals("")) {
					choice3 = st;
					isEdited = true;
					lblHint.setVisible(true);
				}
			});
			panel_3.add(btnRegex);
			panel.add(panel_3);
			
			add(panel, BorderLayout.CENTER);
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
	
	private class MyComboBox<E> extends JComboBox<E> {
		public MyComboBox(E[] items) {
			super(items);
			this.addActionListener(listener);
		}
	}

}
