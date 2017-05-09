import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.function.Predicate;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

class URLChooseDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private JList<String> list;
	private final File file = new File("history.txt");

	/**
	 * Create the dialog.
	 */
	public URLChooseDialog() {
		setModal(true);
		setResizable(false);
		setTitle("\u9009\u62E9\u7F51\u5740");
		setSize(536, 433);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		{
			int x = Main.mainPage.getX() + Main.mainPage.getWidth() / 2 - 268;
			if (x < 50)
				x = 50;
			if (x > toolkit.getScreenSize().getWidth() - 586)
				x = (int) (toolkit.getScreenSize().getWidth() - 586);
			int y = Main.mainPage.getY() + Main.mainPage.getHeight() / 2 - 215;
			if (y < 50)
				y = 50;
			if (y > toolkit.getScreenSize().getHeight() - 483)
				y = (int) (toolkit.getScreenSize().getHeight() - 483);
			setLocation(x, y);
		}
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		Vector<String> vec = new Vector<>();
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.NORTH);
			panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			{
				JLabel lblNewLabel = new JLabel("\u8F93\u5165URL\uFF1A");
				lblNewLabel.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 18));
				panel.add(lblNewLabel);
			}
			{
				textField = new JTextField();
				panel.add(textField);
				textField.setPreferredSize(new Dimension(300, 30));
			}
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.CENTER);
			panel.setLayout(new BorderLayout(0, 0));
			{
				JLabel lblNewLabel_1 = new JLabel("\u8FD1\u671F\u67E5\u770B\u8FC7\u7684URL\uFF1A");
				lblNewLabel_1.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 18));
				panel.add(lblNewLabel_1, BorderLayout.NORTH);
			}
			{
				BufferedReader reader = null;
				String str;
				try {
					reader = new BufferedReader(new FileReader(file));
					while((str = reader.readLine()) != null) {
						vec.add(str);
					}
					reader.close();
				} catch (IOException e) {}
				list = new JList<>(vec);
				list.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						new Thread( () -> {
							if (e.getClickCount() >= 2) {
								String st = list.getSelectedValue();
								boolean p = false;
								if (st != null) {
									String sst = textField.getText();
									if (st.equals(sst))
										p = true;
									textField.setText(st);
								}
								if(e.getClickCount() >= 3 || p) {
									dispose();
									updateVec(vec, st);
									Main.mainPage.start(st);
								}
							}
						}).start();
					}
				});
				JScrollPane scrollPane = new JScrollPane(list);
				scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				panel.add(scrollPane, BorderLayout.CENTER);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("\u786E\u5B9A");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						String ss = textField.getText();
						if (ss == null || ss.equals(""))
							return;
						dispose();
						updateVec(vec, ss);
						Main.mainPage.start(ss);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("\u53D6\u6D88");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	private void updateVec(Vector<String> vec, String s) {
		vec.removeIf(new Predicate<String>() {
			@Override
			public boolean test(String arg0) {
				if (arg0.equals(s))
					return true;
				return false;
			}
		});
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(s + "\n");
			for (String st : vec)
				writer.write(st + "\n");
			writer.close();
		} catch (IOException e) {}
		
	}

}
