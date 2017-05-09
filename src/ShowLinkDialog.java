import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Toolkit;
import java.util.Vector;

import javax.swing.JList;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.awt.event.ActionEvent;

public class ShowLinkDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JList<String> list;
	private JLabel lblHint;
	private JButton btnOpen;
	private JPanel panelFilter;
	private JButton btnFilter, btnAbsolutePath;
	private final static String HINT_DOWNLOAD = "\u5355\u51FB\u4EE5\u4E0B\u8F7D\u94FE\u63A5\u4E2D\u7684\u5185\u5BB9";
	private final static String HINT_OPEN = "\u5355\u51FB\u4EE5\u5728\u7F16\u8F91\u5668\u4E2D\u6253\u5F00\u7F51\u9875";
	private final static String BUTTON_DOWNLOAD = "\u4ECE\u94FE\u63A5\u4E0B\u8F7D";
	private final static String BUTTON_OPEN = "\u6253\u5F00\u7F51\u9875";
	private final static String FILTER_OPEN = "\u6253\u5F00\u8FC7\u6EE4\u5668";
	private final static String FILTER_CLOSE = "\u5173\u95ED\u8FC7\u6EE4\u5668";
	private final static String SHOW_ABSOLUTE_PATH = "\u663E\u793A\u7EDD\u5BF9\u8DEF\u5F84";
	private final static String SHOW_LOCAL_PATH = "\u663E\u793A\u76F8\u5BF9\u8DEF\u5F84";
	
	/**
	 * Create the dialog.
	 */
	public ShowLinkDialog() {
		setTitle("\u663E\u793A\u94FE\u63A5");
		setModal(true);
		setSize(600, 550);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		{
			int x = Main.mainPage.getX() + Main.mainPage.getWidth() / 2 - 300;
			if (x < 50)
				x = 50;
			if (x > toolkit.getScreenSize().getWidth() - 650)
				x = (int) (toolkit.getScreenSize().getWidth() - 650);
			int y = Main.mainPage.getY() + Main.mainPage.getHeight() / 2 - 275;
			if (y < 50)
				y = 50;
			if (y > toolkit.getScreenSize().getHeight() - 600)
				y = (int) (toolkit.getScreenSize().getHeight() - 600);
			setLocation(x, y);
		}
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel panelRight = new JPanel(new VFlowLayout(VFlowLayout.TOP));
			panelRight.setBorder(new EmptyBorder(5, 5, 5, 5));
			panelRight.setPreferredSize(new Dimension(150, 400));
			contentPanel.add(panelRight, BorderLayout.EAST);
		}
		{
			JPanel panelCenter = new JPanel(new BorderLayout());
			panelFilter = new JPanel();
			JPanel panel = new JPanel(new BorderLayout());
			contentPanel.add(panelCenter, BorderLayout.CENTER);
			{
				JPanel panelNorth = new JPanel(new BorderLayout());
				JLabel lblNewLabel = new JLabel("\u7F51\u9875\u4E2D\u7684\u94FE\u63A5");
				lblNewLabel.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 16));
				panelNorth.add(lblNewLabel, BorderLayout.WEST);
				JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
				btnFilter = new JButton(FILTER_OPEN);
				buttons.add(btnFilter);
				btnAbsolutePath = new JButton(SHOW_ABSOLUTE_PATH);
				buttons.add(btnAbsolutePath);
				panelNorth.add(buttons, BorderLayout.CENTER);
				panel.add(panelNorth, BorderLayout.NORTH);
				panel.add(panelFilter, BorderLayout.CENTER);
			}
			panelCenter.add(panel, BorderLayout.NORTH);
			{
				BufferedReader reader = null;
				Vector<String> vec = new Vector<>();
				String s;
				try {
					reader = new BufferedReader(new FileReader(MainPage.fileLinks));
					while((s = reader.readLine()) != null) {
						if (!s.equals(""))
							vec.add(s);
					}
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				list = new JList<>(vec);
				JScrollPane scp = new JScrollPane(list);
				panelCenter.add(scp, BorderLayout.CENTER);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			buttonPane.setBorder(new EmptyBorder(2, 5, 2, 5));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				lblHint = new JLabel();
				lblHint.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 16));
				buttonPane.add(lblHint);
			}
			{
				btnOpen = new JButton(BUTTON_DOWNLOAD);
				btnOpen.setEnabled(false);
				btnOpen.setActionCommand("OK");
				buttonPane.add(btnOpen);
				getRootPane().setDefaultButton(btnOpen);
			}
			{
				JButton btnClose = new JButton("\u5173\u95ED");
				btnClose.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
				btnClose.setActionCommand("Cancel");
				buttonPane.add(btnClose);
			}
		}
	}

}
