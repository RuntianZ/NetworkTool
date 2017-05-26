import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Image;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JList;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class ShowLinkDialog extends PoppedDialog {

	private final JDialog thisDialog = this;
	private final JPanel contentPanel = new JPanel();
	private JList<String> list;
	private JButton btnOpen;
	private JPanel panelFilter, panelRight;
	private JButton btnFilter, btnAbsolutePath;
	private JCheckBox checkCSS, checkJS, checkPic, checkSite;
	private Vector<String> vecSite, vecPic, vecCSS, vecJS, vecOthers, vecAll;
	private String urlString, picPath;
	private final static String HINT_DOWNLOAD = "单击以下载网页内容并在主面板中显示";
	private final static String HINT_OPEN = "单击以下载并用系统默认应用打开文件";
	private final static String BUTTON_DOWNLOAD = "\u4E0B\u8F7D";
	private final static String BUTTON_OPEN = "\u6253\u5F00";
	private final static String FILTER_OPEN = "\u6253\u5F00\u8FC7\u6EE4\u5668";
	private final static String FILTER_CLOSE = "\u5173\u95ED\u8FC7\u6EE4\u5668";
	private final static String SHOW_ABSOLUTE_PATH = "\u663E\u793A\u7EDD\u5BF9\u8DEF\u5F84";
	private final static String SHOW_LOCAL_PATH = "\u663E\u793A\u76F8\u5BF9\u8DEF\u5F84";
	private AtomicInteger currSelect, threadCount;

	private String regexHttp;
	/**
	 * Create the dialog.
	 */
	public ShowLinkDialog(String urlString) {
		setTitle("爬虫工具");
		setModal(true);
		setSize(700, 550);
		threadCount = new AtomicInteger(0);
		currSelect = new AtomicInteger(-1);
		this.urlString = urlString;
		if (this.urlString.endsWith("htm") || this.urlString.endsWith(".html")) {
			int pos = this.urlString.lastIndexOf("/");
			this.urlString = this.urlString.substring(0, pos);
		}
		regexHttp = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("options.dat")));
			reader.readLine();
			reader.readLine();
			regexHttp = reader.readLine();
			reader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			panelRight = new JPanel(new VFlowLayout(VFlowLayout.TOP));
			panelRight.setBorder(new EmptyBorder(5, 5, 5, 5));
			
			
			
			JPanel panel_right = new JPanel(new BorderLayout());
			panel_right.setBorder(new EmptyBorder(5, 5, 5, 5));
			panel_right.setPreferredSize(new Dimension(150, 400));
			contentPanel.add(panel_right, BorderLayout.EAST);

			panel_right.add(panelRight, BorderLayout.CENTER);
		}
		{
			JPanel panelCenter = new JPanel(new BorderLayout());
			panelFilter = new JPanel(new FlowLayout());
			checkCSS = new JCheckBox();
			checkJS = new JCheckBox();
			checkPic = new JCheckBox();
			checkSite = new JCheckBox();
			addCheckBox("网站", checkSite);
			addCheckBox("图片文件", checkPic);
			addCheckBox("CSS文件", checkCSS);
			addCheckBox("JS文件", checkJS);

			panelFilter.setVisible(false);
			JPanel panel = new JPanel(new BorderLayout());
			contentPanel.add(panelCenter, BorderLayout.CENTER);
			{
				JPanel panelNorth = new JPanel(new BorderLayout());
				JLabel lblNewLabel = new JLabel("\u7F51\u9875\u4E2D\u7684\u94FE\u63A5");
				lblNewLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
				panelNorth.add(lblNewLabel, BorderLayout.WEST);
				JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
				JButton btnDeepSearch = new JButton("\u6DF1\u5EA6\u6316\u6398");
				btnDeepSearch.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						int t = JOptionPane.showConfirmDialog(thisDialog, 
								"深度挖掘将挖掘网页代码中的全部链接,是否继续?", "深度挖掘",
								JOptionPane.YES_NO_OPTION);
						if (t == JOptionPane.YES_OPTION) {

							StringBuilder sb = new StringBuilder();
							try {
								BufferedReader reader = new BufferedReader(new FileReader(MainPage.fileStrings));
								while (true) {
									String st = reader.readLine();
									if (st == null)
										break;
									sb.append(st);
								}
								reader.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							Pattern pattern = Pattern.compile(regexHttp);
							Matcher matcher = pattern.matcher(sb.toString());
							StringBuilder s = new StringBuilder();
							Set<String> set = new HashSet<>();
							for (int i = 0; i < vecAll.size(); ++i) {
								s.append(vecAll.get(i) + "\n");
								set.add(vecAll.get(i));
							}
							while (matcher.find()) {
								String st = matcher.group();
								if (st.startsWith(")"))
									continue;
								if (set.contains(st))
									continue;
								set.add(st);
								s.append(st + "\n");
							}
							try {
								BufferedWriter writer = new BufferedWriter(new FileWriter(MainPage.fileLinks));
								writer.write(s.toString());
								writer.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							updateData();
							if (btnFilter.getText().equals(FILTER_OPEN))
								closeFilter();
							else
								linkFilter();
							btnDeepSearch.setEnabled(false);
						}
					}
				});
				buttons.add(btnDeepSearch);
				btnFilter = new JButton(FILTER_OPEN);
				btnFilter.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						if (btnFilter.getText().equals(FILTER_OPEN)) {
							btnFilter.setText(FILTER_CLOSE);
							panelFilter.setVisible(true);
							linkFilter();
						} else {
							closeFilter();
						}
					}
				});
				buttons.add(btnFilter);
				btnAbsolutePath = new JButton(SHOW_ABSOLUTE_PATH);
				btnAbsolutePath.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						if (btnAbsolutePath.getText().equals(SHOW_ABSOLUTE_PATH)) {
							addAbsolutePath(vecSite); 
							addAbsolutePath(vecJS);
							addAbsolutePath(vecCSS);
							addAbsolutePath(vecPic);
							addAbsolutePath(vecOthers);
							addAbsolutePath(vecAll);
							if (btnFilter.getText().equals(FILTER_OPEN))
								closeFilter();
							else
								linkFilter();
							btnAbsolutePath.setText(SHOW_LOCAL_PATH);
						} else {
							updateData();
							if (btnFilter.getText().equals(FILTER_OPEN))
								closeFilter();
							else
								linkFilter();
							btnAbsolutePath.setText(SHOW_ABSOLUTE_PATH);
						}
					}
				});
				buttons.add(btnAbsolutePath);
				panelNorth.add(buttons, BorderLayout.CENTER);
				panel.add(panelNorth, BorderLayout.NORTH);
				panel.add(panelFilter, BorderLayout.CENTER);
			}
			panelCenter.add(panel, BorderLayout.NORTH);
			list = new JList<>();

			list.addListSelectionListener(e -> {
				String s = absolutePath(list.getSelectedValue());
				int k = list.getSelectedIndex();
				if (k == currSelect.get())
					return;
				currSelect.set(k);
				int i = categorize(s);
				if (i == 1) {
					btnOpen.setText(BUTTON_DOWNLOAD);
					btnOpen.setToolTipText(HINT_DOWNLOAD);
				} else {
					btnOpen.setText(BUTTON_OPEN);
					btnOpen.setToolTipText(HINT_OPEN);
				}
				panelRight.removeAll();
				final JLabel lbl_1 = new JLabel();
				lbl_1.setFont(new Font("微软雅黑", Font.PLAIN, 16));
				lbl_1.setHorizontalAlignment(JLabel.CENTER);
				final JLabel lbl_2 = new JLabel();
				lbl_2.setFont(new Font("Consolas", Font.PLAIN, 12));
				lbl_2.setHorizontalAlignment(JLabel.CENTER);
				final JLabel lbl_3 = new JLabel();
				panelRight.add(lbl_1);
				panelRight.add(lbl_2);
				panelRight.add(lbl_3);
				lbl_3.setVisible(false);
				panelRight.updateUI();
				switch (i) {
				case 1:
					lbl_1.setText("网页文件");
					if (s.endsWith("/"))
						lbl_2.setText("index.htm");
					else 
						lbl_2.setText(getFileName(s));
					break;
				case 2:
					lbl_1.setText("JS文件");
					lbl_2.setText(getFileName(s));
					break;
				case 3:
					lbl_1.setText("CSS文件");
					lbl_2.setText(getFileName(s));
					break;
				case 4:
					lbl_1.setText("图片文件");
					lbl_2.setText(getFileName(s));
					if (threadCount.incrementAndGet() == 3) {
						SwingUtilities.invokeLater(() -> {
							list.setEnabled(false);
						});
					}
					new Thread(() -> {
						synchronized (list) {
							int u = currSelect.get();
							String outFile = "temp\\" + getFileName(s);
							int pos = outFile.lastIndexOf(".");
							picPath = "temp\\temp" + outFile.substring(pos);
							File fileOut = new File(outFile);
							try {
								if (s.endsWith(".ico"))
									throw new Exception();
								URL url = new URL(s);
								URLConnection connection = url.openConnection();
								connection.setConnectTimeout(1000);
								connection.setReadTimeout(1000);
								InputStream in = connection.getInputStream();
								FileOutputStream out = new FileOutputStream(fileOut);
								new DownloadStream(in, out).start();
								in.close();
								out.close();
								BufferedImage image = ImageIO.read(fileOut);
								Image image_put = image.getScaledInstance(120, 
										120 * image.getHeight() / image.getWidth(), Image.SCALE_SMOOTH);
								if (u != currSelect.get())
									return;
								lbl_3.setIcon(new ImageIcon(image_put));
								lbl_3.setVisible(true);
							} catch (Exception e2) {
								if (u != currSelect.get())
									return;
								lbl_3.setIcon(new ImageIcon(
									ShowLinkDialog.class.getResource(
											"/javax/swing/plaf/basic/icons/image-failed.png")));
								lbl_3.setVisible(true);
							
							} finally {
								CopyFileUtil.renameFile(outFile, picPath, true);
								threadCount.decrementAndGet();
								if (!list.isEnabled()) {
									SwingUtilities.invokeLater(() -> {
										list.setEnabled(true);
									});
								}
								panelRight.updateUI();
							}
						}
					}).start();
					break;
				case 0:
					lbl_1.setText("其它文件");
					lbl_2.setText(getFileName(s));
				}
			});
			
			JScrollPane scp = new JScrollPane(list);
			panelCenter.add(scp, BorderLayout.CENTER);

			updateData();
			closeFilter();
		}
		{

			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			buttonPane.setBorder(new EmptyBorder(2, 5, 2, 5));

			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				btnOpen = new JButton(BUTTON_OPEN);
				btnOpen.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						String s = absolutePath(list.getSelectedValue());
						if (s == null)
							return;
						if (btnOpen.getText().equals(BUTTON_OPEN)) {
							try {
								int pos = s.lastIndexOf(".");
								String path = "temp\\temp" + s.substring(pos);
								File file = new File(path);
								if (categorize(s) != 4) {
									URL url = new URL(s);
									
									URLConnection connection = url.openConnection();
									connection.setConnectTimeout(1000);
									connection.setReadTimeout(1000);
									InputStream in = connection.getInputStream();
									FileOutputStream out = new FileOutputStream(file);
									new DownloadStream(in, out).start();
									in.close();
									out.close();
								}
								Desktop.getDesktop().open(file);
							} catch (Exception e) {
								JOptionPane.showMessageDialog(thisDialog, "打开文件失败", 
										"错误", JOptionPane.ERROR_MESSAGE);
							}
						} else {
							dispose();
							Main.mainPage.start(s);
						}
					}
				});
				
				JButton btnNewButton = new JButton("\u4FDD\u5B58");
				btnNewButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						JFileChooser chooser = new JFileChooser();
						String s = absolutePath(list.getSelectedValue());
						final String f;
						int i = categorize(s);
						if (i == 1 && !(s.contains(".htm") || s.contains(".shtm"))) {
							f = "htm";
						} else {
							int pos = s.lastIndexOf(".");
							f = s.substring(pos + 1);
						}
						FileFilter filter = new FileFilter() {
							@Override
							public boolean accept(File arg0) {
								return true;
							}
							@Override
							public String getDescription() {
								return f + "文件";
							}
						};
						chooser.setFileFilter(filter);
						{
							try {
								BufferedReader reader = new BufferedReader(new FileReader(MainPage.fileOptions));
								reader.readLine();
								reader.readLine();
								reader.readLine();
								String st = reader.readLine();
								reader.close();
								if (st != null) {
									File defaultDir = new File(st);
									if (defaultDir.isDirectory())
										chooser.setCurrentDirectory(defaultDir);
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						int t = chooser.showSaveDialog(thisDialog);
						if (t != JFileChooser.APPROVE_OPTION)
							return;
						File file = chooser.getSelectedFile();
						if (!file.getPath().endsWith(f))
							file = new File(file.getAbsolutePath() + "." + f);
						boolean p = true;
						if (file.exists()) {
							t = JOptionPane.showConfirmDialog(thisDialog, "确定要覆盖文件吗?", 
									"警告", JOptionPane.YES_NO_OPTION);
							if (t != JOptionPane.YES_OPTION)
								p = false;
						}
						if (!p)
							return;
						try {
							URL url = new URL(s);
							URLConnection connection = url.openConnection();
							connection.setConnectTimeout(1000);
							connection.setReadTimeout(1000);
							InputStream in = connection.getInputStream();
							FileOutputStream out = new FileOutputStream(file);
							new DownloadStream(in, out).start();
							in.close();
							out.close();
						} catch (Exception e) {
							JOptionPane.showMessageDialog(thisDialog, "保存文件失败", 
									"错误", JOptionPane.ERROR_MESSAGE);
						}
						try {
							BufferedReader reader = new BufferedReader(new FileReader(MainPage.fileOptions));
							String st_1 = reader.readLine();
							String st_2 = reader.readLine();
							String st_3 = reader.readLine();
							String st = file.getAbsolutePath();
							int pos = st.lastIndexOf("\\");
							String st_4 = st.substring(0, pos + 1);
							reader.close();
							FileWriter writer = new FileWriter(MainPage.fileOptions);
							writer.write(st_1 + "\n" + st_2 + "\n" + st_3 + "\n" + st_4);
							writer.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
				buttonPane.add(btnNewButton);
				btnOpen.setToolTipText(HINT_OPEN);
				buttonPane.add(btnOpen);
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
				getRootPane().setDefaultButton(btnClose);
			}
		}
	}

	private void addCheckBox(String s, JCheckBox box) {
		MouseAdapter adapter = new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				box.setSelected(!box.isSelected());
				linkFilter();
			}
		};
		JPanel panel = new JPanel(new FlowLayout());
		panel.setBackground(Color.ORANGE);
		panel.setOpaque(true);
		JLabel lbl = new JLabel(s);
		lbl.addMouseListener(adapter);
		panel.add(lbl);
		box.setBackground(Color.ORANGE);
		box.setOpaque(true);
		panel.add(box);
		box.addActionListener(e -> {
			linkFilter();
		});
		panel.addMouseListener(adapter);
		panelFilter.add(panel);
	}

	private void linkFilter() {
		Vector<String> vec = new Vector<>();
		if (checkSite.isSelected())
			vec.addAll(vecSite);
		if (checkPic.isSelected())
			vec.addAll(vecPic);
		if (checkJS.isSelected())
			vec.addAll(vecJS);
		if (checkCSS.isSelected())
			vec.addAll(vecCSS);
		String[] str = new String[vec.size()];
		for (int i = 0; i < vec.size(); ++i)
			str[i] = vec.elementAt(i);
		list.setListData(str);
	}

	private void updateData() {
		BufferedReader reader = null;
		String s;
		vecSite = new Vector<>();
		vecPic = new Vector<>();
		vecCSS = new Vector<>();
		vecJS = new Vector<>();
		vecOthers = new Vector<>();
		vecAll = new Vector<>();
		try {
			reader = new BufferedReader(new FileReader(MainPage.fileLinks));
			while ((s = reader.readLine()) != null) {
				if (!s.equals("")) {
					switch (categorize(s)) {
					case 1:
						vecSite.add(s);
						break;
					case 2:
						vecJS.add(s);
						break;
					case 3:
						vecCSS.add(s);
						break;
					case 4:
						vecPic.add(s);
						break;
					case 0:
						vecOthers.add(s);
					}
					vecAll.add(s);
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void closeFilter() {
		btnFilter.setText(FILTER_OPEN);
		panelFilter.setVisible(false);
		String[] str = new String[vecAll.size()];
		for (int i = 0; i < vecAll.size(); ++i)
			str[i] = vecAll.elementAt(i);
		list.setListData(str);
	}

	/**
	 * Categorizes the string.
	 * @param s The string.
	 * @return 1 for websites, 2 for js files, 3 for css files,
	 * 4 for pictures, 0 for others, -1 for null.
	 */
	private int categorize(String s) {
		if (s == null)
			return -1;
		if (s.endsWith("/") || s.contains(".htm") || s.contains(".shtm") ||
				s.endsWith(".com") || s.endsWith(".org") || s.endsWith(".edu") ||
				s.endsWith(".cn") || s.endsWith(".net") || s.endsWith(".gov") ||
				s.endsWith(".us") || s.endsWith(".uk") || s.endsWith(".jp") ||
				s.endsWith(".info"))
			return 1;
		else if (s.endsWith(".js"))
			return 2;
		else if (s.endsWith(".css"))
			return 3;
		else if (s.endsWith(".jpg") || s.endsWith(".bmp") || s.endsWith(".png") || s.endsWith(".ico")
				|| s.endsWith(".gif"))
			return 4;
		else {
			int pos = s.lastIndexOf("/");
			String ss = s.substring(pos + 1);
			if (!ss.contains("."))
				return 1;
		}
		return 0;
	}
	
	private String absolutePath(String path) {
		if (path == null)
			return null;
		String ans;
		if (path.startsWith("//")) {
			path = "http:" + path;
		}
		if (path.startsWith("http") || path.startsWith("ftp"))
			ans = path;
		else {
			String s;
			if (urlString.endsWith("/"))
				s = urlString;
			else
				s = urlString + "/";
			if (!s.startsWith("http"))
				s = "http://" + s;
			if (path.startsWith(".")) {
				if (path.startsWith("./")) {
					ans = s + path.substring(2);
				} else {
					String st = path;
					String stt = s;
					while (st.startsWith("../")) {
						int pos = stt.lastIndexOf("/", s.length() - 2);
						stt = stt.substring(0, pos);
						st = st.substring(3);
					}
					ans = stt + "/" + st;
				}
			} else if (path.startsWith("/")) {
				int pos = s.indexOf("/", 8);
				ans = s.substring(0, pos) + path;
			} else {
				ans = s + path;
			}
		}
		return ans;
	}
	
	private void addAbsolutePath(Vector<String> vec) {
		for (int i = 0; i < vec.size(); ++i) {
			vec.set(i, absolutePath(vec.get(i)));
		}
	}
	
	private String getFileName(String s) {
		int pos = s.lastIndexOf("/");
		return s.substring(pos + 1);
	}
	
}