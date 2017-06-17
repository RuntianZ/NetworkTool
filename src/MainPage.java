/**
 * @author Runtian Zhai
 * @license MIT
 * 
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.Font;
import java.awt.Toolkit;

import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
class MainPage extends JFrame {

	private JPanel contentPane;
	private JEditorPane textPane;
	private JPanel panel;
	private JButton btnLink;
	private JButton btnNew;
	private JButton btnSettings;
	private JLabel lblNewLabel;
	private JButton btnWebsite;
	private DefaultStyledDocument doc;
	private int defaultBrowser, defaultOpenLink;
	public static final File fileLinks = new File("links.dat");
	public static final File fileCodes = new File("codes.dat");
	private static final File fileSettings = new File("settings.edt");
	public static final File fileSave = new File("save.dat");
	private static final File fileParsed = new File("parsed.dat");
	public static final File fileStrings = new File("strings.dat");
	public static final File fileOptions = new File("options.dat");
	private Font font;
	private ColorLibrary colors;
	private JButton btnAbout;
	private JLabel lblDownload;
	private String savedURLString;
	private static final Font INIT_FONT = new Font("微软雅黑", Font.PLAIN, 24);
	public final ReentrantLock workingLock = new ReentrantLock();
	private final ReentrantLock downloadingLock = new ReentrantLock();
	private static final String TEXTPANE_INIT = "\u5355\u51FB\u201C\u9009\u62E9\u7F51\u5740\u201D\u5F00\u59CB\u5DE5\u4F5C";
	private static final String PARSING = "\u89E3\u6790\u4E2D";
	private static final String DOWNLOADING = "\u4E0B\u8F7D\u4E2D";
	private static final String SHOWING = "\u663E\u793A\u4E2D";
	private static final String SELECT_A_SITE = "\u9009\u62E9\u7F51\u5740";
	private static final String CLOSE = "\u5173\u95ED";
	private static final String SHOW_WEBSITE = "\u663E\u793A\u7F51\u9875";
	private static final String SHOW_WEBSITE_SOURCE = "\u663E\u793A\u4EE3\u7801";
	private Thread downloadThread, parseThread, displayThread;
	private WaitingLabel labelWaiting;
	private AtomicBoolean atmBoolean;

	public MainPage() {
		this(null);
	}

	/**
	 * Create the frame.
	 */
	public MainPage(String urlString) {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		ObjectInputStream in = null;
		atmBoolean = new AtomicBoolean(true);
		try {
			in = new ObjectInputStream(new FileInputStream(fileSettings));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			colors = (ColorLibrary) in.readObject();
			font = (Font) in.readObject();
			in.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		updateOptions();
		downloadThread = null;
		parseThread = null;
		displayThread = null;
		setTitle("\u7F51\u7EDC\u722C\u866B\u5DE5\u5177");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		setBounds((int) (toolkit.getScreenSize().getWidth()) * 1 / 7,
				(int) (toolkit.getScreenSize().getHeight()) * 1 / 7, (int) (toolkit.getScreenSize().getWidth()) * 5 / 7,
				(int) (toolkit.getScreenSize().getHeight()) * 5 / 7);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		textPane = new TextPaneMenu();
		textPane.setFont(INIT_FONT);
		textPane.setBackground(Color.WHITE);
		textPane.setText(TEXTPANE_INIT);
		textPane.setEnabled(false);
		doc = null;
		JScrollPane scrollPane = new JScrollPane(textPane);
		contentPane.add(scrollPane, BorderLayout.CENTER);

		panel = new JPanel(new VFlowLayout(VFlowLayout.TOP));
		panel.setPreferredSize(new Dimension(125, 425));
		contentPane.add(panel, BorderLayout.EAST);

		lblNewLabel = new JLabel("\u7F51\u7EDC\u722C\u866B\u5DE5\u5177");
		lblNewLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblNewLabel);

		btnNew = new JButton(SELECT_A_SITE);
		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String s = btnNew.getText();
				if (s.equals(SELECT_A_SITE))
					new URLChooseDialog().setVisible(true);
				else
					closePane();
			}
		});
		panel.add(btnNew);

		btnWebsite = new JButton(SHOW_WEBSITE);
		btnWebsite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				contentPane.removeAll();
				contentPane.add(panel, BorderLayout.EAST);
				if (btnWebsite.getText().equals(SHOW_WEBSITE)) {
					if (defaultBrowser == 0) {
						textPane = new UneditableEditorPane();
						btnWebsite.setText(SHOW_WEBSITE_SOURCE);
						textPane.addHyperlinkListener(new HyperlinkListener() {
							@Override
							public void hyperlinkUpdate(HyperlinkEvent e) {
								if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
									loading();
									atmBoolean.set(true);
									if (defaultOpenLink == 0) {
										reDownload(e.getURL().toString(), false);
										try {
											textPane.setPage(e.getURL());
										} catch (Exception e1) {
											closePane();
											downloadFailed();
											return;
										}
										new Thread(() -> {
											try {
												Thread.sleep(100);
											} catch (InterruptedException e1) {
												e1.printStackTrace();
											}
											if (!atmBoolean.get())
												return;
											downloadingLock.lock();
											afterLoading();
											downloadingLock.unlock();
										}).start();
									} else {
										SwingUtilities.invokeLater(() -> {
											setTextPane();
											reDownload(e.getURL().toString(), true);
											
											contentPane.updateUI();
										});
										
									}
								}
							}
						});
						
						
					} else {
						// From <link>http://blog.csdn.net/hfmbook/article/details/16882807</link>
						if (java.awt.Desktop.isDesktopSupported()) {
							try {
								java.net.URI uri = java.net.URI.create(savedURLString);
								java.awt.Desktop dp = java.awt.Desktop.getDesktop();
								if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {
									dp.browse(uri);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				} else {
					setTextPane();
					reParse(true);
					
				}
				JScrollPane jsp = new JScrollPane(textPane);
				contentPane.add(jsp, BorderLayout.CENTER);
				contentPane.updateUI();
				if (btnWebsite.getText().equals(SHOW_WEBSITE_SOURCE) && defaultBrowser == 0) {
					try {
						textPane.setPage(savedURLString);
					} catch (IOException e1) {
						closePane();
						downloadFailed();
						return;
					}
				}
			}
		});
		btnWebsite.setEnabled(false);
		panel.add(btnWebsite);

		btnLink = new JButton("\u722C\u866B\u5DE5\u5177");
		btnLink.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new ShowLinkDialog(savedURLString).setVisible(true);
			}
		});
		btnLink.setEnabled(false);
		panel.add(btnLink);

		btnSettings = new JButton("\u7F16\u8F91\u5668\u8BBE\u7F6E");
		btnSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new SettingDialog(colors, font).setVisible(true);
			}
		});
		panel.add(btnSettings);

		btnAbout = new JButton("\u5173\u4E8E");
		btnAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Object ob[] = {"确认", "访问官网", "查看条款"};
				JOptionPane pane = new JOptionPane(
						 "作者:S68-翟润天(1600012737)\nS67-徐可涵(1600012731)\n"
								+ "S69-陶铭绪(1600012899)\nS70-金奕成(1600017746)\n" + "本开源软件适用MIT X11许可证",
						JOptionPane.INFORMATION_MESSAGE, JOptionPane.YES_NO_OPTION, null, ob);
				pane.createDialog("关于").setVisible(true);
				Object value = pane.getValue();
				if (value != null) {
					String st = (String)value;
					if (st.equals("访问官网")) {
						if (java.awt.Desktop.isDesktopSupported()) {
							try {
								java.net.URI uri = java.net.URI.create("http://www.github.com/RuntianZ/NetworkTool");
								java.awt.Desktop dp = java.awt.Desktop.getDesktop();
								if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {
									dp.browse(uri);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} else if (st.equals("查看条款")) {
						try {
							BufferedReader reader = new BufferedReader(new FileReader(new File("license.txt")));
							StringBuilder sb = new StringBuilder();
							while(true) {
								String s = reader.readLine();
								if (s == null)
									break;
								sb.append(s + "\n");
							}
							JOptionPane.showMessageDialog(null, sb.toString(), "条款", JOptionPane.INFORMATION_MESSAGE);
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					}
				}
			}
		});
		panel.add(btnAbout);

		JPanel _panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		labelWaiting = new WaitingLabel();
		labelWaiting.setVisible(false);
		_panel.add(labelWaiting);
		panel.add(_panel);

		lblDownload = new JLabel(DOWNLOADING);
		lblDownload.setForeground(Color.RED);
		lblDownload.setFont(new Font("微软雅黑", Font.BOLD, 17));
		lblDownload.setHorizontalAlignment(SwingConstants.CENTER);
		lblDownload.setVisible(false);
		panel.add(lblDownload);

		if (urlString != null) {
			start(urlString);
		}
	}

	/**
	 * Start working on the textpane.
	 * 
	 * @param s
	 *            The URL String.
	 */
	public synchronized void start(String urlString) {
		if (urlString == null)
			return;
		new Thread(() -> {
			workingLock.lock();
			loading();
			reDownload(urlString, true);
			workingLock.unlock();
		}).start();

	}
	

	public void changeFont(Font font) {
		textPane.setFont(font);
		this.font = font;
		updateSettings();
		textPane.updateUI();
		repaint();
	}

	private void reDownload(String urlString, boolean b) {
		downloadThread = new Thread(() -> {
			downloadingLock.lock();
			savedURLString = urlString;
			InputStream in = null;
			boolean flag = false;
			try {
				URLConnection connection = new URL(urlString).openConnection();
				connection.setConnectTimeout(1000);
				connection.setReadTimeout(1000);
				
				in = connection.getInputStream();
			} catch (MalformedURLException e) {
				urlMalformed();
				parseThread = null;
				flag = true;
			} catch (IOException e) {
				downloadFailed();
				parseThread = null;
				flag = true;
			}
			if (flag) {
				closePane();
				downloadingLock.unlock();
				return;
			}
			try {
				FileOutputStream out = new FileOutputStream(fileSave);
				new DownloadStream(in, out).start();
				in.close();
				out.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			downloadingLock.unlock();
			parseThread.start();
			try {
				parseThread.join();
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		parseThread = new Thread(() -> {
			downloadingLock.lock();
			lblDownload.setText(PARSING);
			try {
				doc = new DefaultStyledDocument();
				HTMLEditorKit.Parser parser = new ParserDelegator();
				parser.parse(new InputStreamReader(new FileInputStream(fileSave), "utf-8"), 
						new HTMLHandler(colors, doc), true);
				ObjectOutputStream fout = new ObjectOutputStream(new FileOutputStream(fileParsed));
				fout.writeObject(doc);
				fout.close();
				if (!b)
					return;
				displayThread.start();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				downloadingLock.unlock();
			}

		});

		displayThread = new Thread(() -> {
			downloadingLock.lock();
			lblDownload.setText(SHOWING);
			try {
				ObjectInputStream fin = new ObjectInputStream(new FileInputStream(fileSettings));
				colors = (ColorLibrary) fin.readObject();
				font = (Font) fin.readObject();
				fin.close();
				SwingUtilities.invokeLater(() -> {
					textPane.setBackground(colors.colorBackground);
					textPane.setFont(font);
					afterLoading();
					textPane.setDocument(doc);
					textPane.updateUI();
					contentPane.updateUI();
					doc = null;
					repaint();
				});
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				downloadingLock.unlock();
			}

		});

		downloadThread.start();
		try {
			Thread.sleep(100);
			downloadThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void updateSettings(ColorLibrary thecolors) {
		if (thecolors != null)
			colors = thecolors.clone();
		saveSettings(fileSettings);
	}

	public void updateSettings() {
		saveSettings(fileSettings);
	}

	public void colorChanged(ColorLibrary thecolors) {
		updateSettings(thecolors);
		if (btnWebsite.getText().equals(SHOW_WEBSITE) && btnWebsite.isEnabled())
			reParse(false);
	}

	private void reParse(boolean b) {
		loading();
		parseThread = new Thread(() -> {
			workingLock.lock();
			lblDownload.setText(PARSING);
			try {
				if (b) {
					ObjectInputStream fin = new ObjectInputStream(new FileInputStream(fileParsed));
					doc = (DefaultStyledDocument) (fin.readObject());
					fin.close();
				} else {
					doc = new DefaultStyledDocument();
					HTMLEditorKit.Parser parser = new ParserDelegator();
					parser.parse(new InputStreamReader(new FileInputStream(fileSave), "utf-8"), 
							new HTMLHandler(colors, doc), true);
					ObjectOutputStream fout = new ObjectOutputStream(new FileOutputStream(fileParsed));
					fout.writeObject(doc);
					fout.close();
				}
				displayThread.start();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				workingLock.unlock();
			}

		});

		displayThread = new Thread(() -> {
			workingLock.lock();
			lblDownload.setText(SHOWING);
			try {
				ObjectInputStream fin = new ObjectInputStream(new FileInputStream(fileSettings));
				colors = (ColorLibrary) fin.readObject();
				font = (Font) fin.readObject();
				fin.close();
				SwingUtilities.invokeLater(() -> {
					textPane.setBackground(colors.colorBackground);
					textPane.setFont(font);
					afterLoading();
					textPane.setDocument(doc);
					textPane.updateUI();
					contentPane.updateUI();
					doc = null;
					repaint();
				});
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				workingLock.unlock();
			}

		});

		parseThread.start();
	}

	private void loading() {
		SwingUtilities.invokeLater(() -> {
			btnNew.setEnabled(false);
			btnSettings.setEnabled(false);
			btnWebsite.setEnabled(false);
			btnLink.setEnabled(false);
			btnAbout.setEnabled(false);
			textPane.setEnabled(false);
			textPane.setBackground(Color.WHITE);
			textPane.setFont(INIT_FONT);
			textPane.setText("页面正在加载中,请等待");
			lblDownload.setVisible(true);
			labelWaiting.setVisible(true);
			labelWaiting.start(50);
		});
	}

	private void afterLoading() {
		SwingUtilities.invokeLater(() -> {
			btnNew.setEnabled(true);
			btnSettings.setEnabled(true);
			textPane.setEnabled(true);
			btnWebsite.setEnabled(true);
			btnLink.setEnabled(true);
			btnAbout.setEnabled(true);
			lblDownload.setText(DOWNLOADING);
			lblDownload.setVisible(false);
			labelWaiting.setVisible(false);
			btnNew.setText(CLOSE);
		});
	}

	public void closePane() {
		SwingUtilities.invokeLater(() -> {
			btnNew.setEnabled(true);
			btnSettings.setEnabled(true);
			btnWebsite.setEnabled(false);
			btnLink.setEnabled(false);
			btnAbout.setEnabled(true);
			contentPane.removeAll();
			contentPane.add(panel, BorderLayout.EAST);
			textPane = new TextPaneMenu();
			JScrollPane jsp = new JScrollPane(textPane);
			contentPane.add(jsp, BorderLayout.CENTER);
			textPane.setText(TEXTPANE_INIT);
			textPane.setBackground(Color.WHITE);
			btnNew.setText(SELECT_A_SITE);
			btnWebsite.setText(SHOW_WEBSITE);
			textPane.setFont(INIT_FONT);
			textPane.setEnabled(false);
			textPane.updateUI();
			lblDownload.setText(DOWNLOADING);
			lblDownload.setVisible(false);
			labelWaiting.setVisible(false);
			doc = null;
			contentPane.updateUI();
		});
	}

	public void saveSettings(File file) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
			out.writeObject(colors);
			out.writeObject(font);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void downloadFailed() {
		atmBoolean.set(false);
		SwingUtilities.invokeLater(() -> {
			JOptionPane.showMessageDialog(null, "下载失败", "错误", JOptionPane.ERROR_MESSAGE);
		});
	}

	private void urlMalformed() {
		atmBoolean.set(false);
		SwingUtilities.invokeLater(() -> {
			JOptionPane.showMessageDialog(null, "下载失败", "错误", JOptionPane.ERROR_MESSAGE);
		});
	}
	
	public void setTextPane() {
		contentPane.removeAll();
		contentPane.add(panel, BorderLayout.EAST);
		textPane = new TextPaneMenu();
		btnWebsite.setText(SHOW_WEBSITE);
		JScrollPane jsp = new JScrollPane(textPane);
		contentPane.add(jsp, BorderLayout.CENTER);
	}

	public void updateOptions() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileOptions));
			String st1 = reader.readLine();
			String st2 = reader.readLine();
			reader.close();
			defaultBrowser = ("内置浏览器".equals(st1)) ? 0 : 1;
			defaultOpenLink = ("查看网页".equals(st2)) ? 0 : 1;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
