import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.ScrollPaneConstants;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.awt.event.ActionEvent;
import javax.swing.JProgressBar;

class MainPage extends JFrame {

	private JPanel contentPane;
	private JEditorPane textPane;
	private JPanel panel;
	private JButton btnCode;
	private JButton btnCatch;
	private JButton btnFind;
	private JButton btnLink;
	private JButton btnNew;
	private JButton btnSettings;
	private JLabel lblNewLabel;
	private JButton btnWebsite;
	private DefaultStyledDocument doc;
	public static final File fileLinks = new File("links.dat");
	public static final File fileCodes = new File("codes.dat");
	private static final File fileSettings = new File("settings.edt");
	private static final File fileSave = new File("save.dat");
	private static final File fileParsed = new File("parsed.dat");
	private Font font;
	private ColorLibrary colors;
	private JButton btnAbout;
	private JLabel lblDownload;
	private String savedURLString;
	private static final Font INIT_FONT = new Font("微软雅黑", Font.PLAIN, 24);
	public static final ReentrantLock workingLock = new ReentrantLock();
	private static final ReentrantLock downloadingLock = new ReentrantLock();
	private static final String TEXTPANE_INIT = "\u5355\u51FB\u201C\u9009\u62E9\u7F51\u5740\u201D\u5F00\u59CB\u5DE5\u4F5C";
	private static final String PARSING = "\u89E3\u6790\u4E2D";
	private static final String DOWNLOADING = "\u4E0B\u8F7D\u4E2D";
	private static final String SHOWING = "\u663E\u793A\u4E2D";
	private static final String SELECT_A_SITE = "\u9009\u62E9\u7F51\u5740";
	private static final String CLOSE = "\u5173\u95ED";
	private static final String SHOW_WEBSITE = "\u663E\u793A\u7F51\u9875";
	private static final String SHOW_WEBSITE_SOURCE = "\u663E\u793A\u4EE3\u7801";
	private Thread downloadThread, parseThread, displayThread;

	public MainPage() {
		this(null);
	}

	/**
	 * Create the frame.
	 */
	public MainPage(String urlString) {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		ObjectInputStream in = null;
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
		textPane = new JTextPane();
		textPane.setEditable(false);
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
					textPane = new JEditorPane();
					textPane.addHyperlinkListener(new HyperlinkListener() {
						@Override
						public void hyperlinkUpdate(HyperlinkEvent e) {
							if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
								loading();
								try {
									textPane.setPage(e.getURL());
								} catch (Exception e1) {
									closePane();
									downloadFailed();
									return;
								}
								reDownload(e.getURL().toString(), false);
								new Thread(() -> {
									try {
										Thread.sleep(100);
									} catch (InterruptedException e1) {
										e1.printStackTrace();
									}
									downloadingLock.lock();
									afterLoading();
									downloadingLock.unlock();
								}).start();
							}
						}
					});
					textPane.setEditable(false);
					btnWebsite.setText(SHOW_WEBSITE_SOURCE);
				} else {
					textPane = new JTextPane();
					textPane.setEditable(false);
					reParse(true);
					btnWebsite.setText(SHOW_WEBSITE);
				}
				JScrollPane jsp = new JScrollPane(textPane);
				contentPane.add(jsp, BorderLayout.CENTER);
				contentPane.updateUI();
				if (btnWebsite.getText().equals(SHOW_WEBSITE_SOURCE)) {
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

		btnLink = new JButton("\u663E\u793A\u94FE\u63A5");
		btnLink.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new ShowLinkDialog().setVisible(true);
			}
		});
		btnLink.setEnabled(false);
		panel.add(btnLink);

		btnFind = new JButton("\u67E5\u627E");
		btnFind.setEnabled(false);
		panel.add(btnFind);

		btnCatch = new JButton("\u591A\u5A92\u4F53\u6293\u53D6");
		btnCatch.setEnabled(false);
		panel.add(btnCatch);

		btnCode = new JButton("\u4EE3\u7801\u5206\u6790");
		btnCode.setEnabled(false);
		panel.add(btnCode);

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
				JOptionPane.showMessageDialog(null, "作者:S68-翟润天  S67-徐可涵\n本开源软件适用MIT X11许可证", "关于",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		panel.add(btnAbout);

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

	private class HTMLHandler extends HTMLEditorKit.ParserCallback {
		private boolean inScript, inStyle;
		private final int INDENT_SPACE;
		private int indents;
		boolean isWorking;
		private FileWriter writerLinks, writerCodes;

		public HTMLHandler() {
			inScript = false;
			inStyle = false;
			indents = 0;
			lastPunctuation = 'h';
			inScriptJsonValue = false;
			exitFromValue = false;
			INDENT_SPACE = colors.indentSpace;
			insertWithColor("<!DOCTYPE html>", colors.colorComment);
			insertWithColor("\n");
			isWorking = true;
			try {
				writerLinks = new FileWriter(fileLinks);
				writerCodes = new FileWriter(fileCodes);
				writerLinks.write("");
				writerCodes.write("");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void insertWithColor(String data, Color color, boolean isUnderlined) {
			if (!isWorking)
				return;
			SimpleAttributeSet attr = new SimpleAttributeSet();
			StyleConstants.setForeground(attr, color);
			if (isUnderlined)
				StyleConstants.setUnderline(attr, true);
			try {
				synchronized (doc) {
					doc.insertString(doc.getLength(), data, attr);
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}

		private void insertWithColor(String data, Color color) {
			insertWithColor(data, color, false);
		}

		private void insertWithColor(String data) {
			insertWithColor(data, colors.colorComma, false);
		}

		private void insertWithColor(char data) {
			insertWithColor(String.valueOf(data));
		}

		private void insertWithColor(char data, Color color) {
			insertWithColor(String.valueOf(data), color);
		}

		private void insertWithColor(char data, Color color, boolean isUnderlined) {
			insertWithColor(String.valueOf(data), color, isUnderlined);
		}

		private void insertIndents() {
			for (int i = 0; i < indents * INDENT_SPACE; ++i)
				insertWithColor(" ");
		}

		private boolean inScriptJsonValue;

		/**
		 * Handles a javascript and save it in fileCodes.
		 * 
		 * @param data
		 *            The script.
		 */
		private void handleScript(String data) {
			int index = 0;
			while (index < data.length()) {
				while (index < data.length() && isPunctuation(data.charAt(index))) {
					int flag = handlePunctuation(data.charAt(index++));
					if (flag == 1) {
						while (index < data.length() && data.charAt(index) != '\n' && data.charAt(index) != '\r')
							insertWithColor(data.charAt(index++), colors.colorCSSComment);
						insertWithColor('\n');
						insertIndents();
						if (index < data.length())
							++index;
					} else if (flag == 2) {
						while (index < data.length() && (data.charAt(index) != '/' || data.charAt(index - 1) != '*'))
							insertWithColor(data.charAt(index++), colors.colorCSSComment);
						insertWithColor('/', colors.colorCSSComment);
						if (index < data.length())
							++index;
					} else if (flag == 3) {
						while (index < data.length() && (data.charAt(index) != '\'' || data.charAt(index - 1) == '\\'))
							insertWithColor(data.charAt(index++), colors.colorCodeString);
						insertWithColor('\'');
						if (index < data.length())
							++index;
					} else if (flag == 4) {
						while (index < data.length() && (data.charAt(index) != '\"'|| data.charAt(index - 1) == '\\'))
							insertWithColor(data.charAt(index++), colors.colorCodeString);
						insertWithColor('\"');
						if (index < data.length())
							++index;
					}
				}
				if (index == data.length())
					break;
				int i = index;
				while (++index < data.length()) {
					if (isPunctuation(data.charAt(index)))
						break;
				}
				String word = data.substring(i, index);
				if (lastPunctuation == '}')
					insertWithColor(' ');
				lastPunctuation = 'h';
				if (inScriptJsonValue) {
					insertWithColor(word, colors.colorJsonAttributeValue);
				} else {
					if (isRetainWord(word))
						insertWithColor(word, colors.colorCodeRetain);
					else {
						while (index < data.length() && data.charAt(index) == ' ') {
							++index;
						}
						if (index < data.length() && data.charAt(index) == '(')
							insertWithColor(word, colors.colorCodeFunction);
						else if (index < data.length() && data.charAt(index) == ':') {
							insertWithColor(word, colors.colorJsonAttributeName);
							if ("style".equals(word)) {
								handlePunctuation(':');
								++index;
								while (index < data.length() && data.charAt(index) == ' ') {
									++index;
								}
								if (index < data.length() && data.charAt(index) == '\'') {
									insertWithColor('\n');
									++indents;
									insertIndents();
									handlePunctuation('\'');
									int j = ++index;
									while (++index < data.length()) {
										if (data.charAt(index) == '\'' && data.charAt(index - 1) != '\\')
											break;
									}
									String s = data.substring(j, index);
									handleCSS(s);
									handlePunctuation('\'');
									++index;
									--indents;
								}
							}
						} else
							insertWithColor(word, colors.colorCodeVariable);
					}
				}
			}
		}

		private char lastPunctuation;
		private boolean exitFromValue;

		private int handlePunctuation(char c) {
			if (c == '\n' || c == '\r' || c == '\t')
				return 0;
			if (lastPunctuation == ' ' && c == ' ')
				return 0;
			if (lastPunctuation == '/' && c == '/') {
				try {
					doc.remove(doc.getLength() - 2, 2);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
				insertWithColor("//", colors.colorCSSComment);
				return 1;
			}
			if (lastPunctuation == '/' && c == '*') {
				try {
					doc.remove(doc.getLength() - 2, 2);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
				insertWithColor("/*", colors.colorCSSComment);
				return 2;
			}
			int ans;
			if (c == '\'' && lastPunctuation != '/' && lastPunctuation != '\\')
				ans = 3;
			else if (c == '\"' && lastPunctuation != '/' && lastPunctuation != '\\')
				ans = 4;
			else
				ans = 0;
			if (c == '}') {
				if (lastPunctuation != ' ')
					insertWithColor('\n');
				--indents;
				insertIndents();
				insertWithColor("}");
				lastPunctuation = '}';
			} else if (c == '{') {
				insertWithColor(" {\n");
				++indents;
				insertIndents();
				lastPunctuation = ' ';
			} else if (c == ';') {
				insertWithColor(";\n");
				insertIndents();
				lastPunctuation = ' ';
			} else if (c == ',') {
				insertWithColor(c + " ");
				if (exitFromValue) {
					insertWithColor('\n');
					insertIndents();
				}
				lastPunctuation = ' ';
			} else {
				insertWithColor(c);
				lastPunctuation = c;
			}
			exitFromValue = false;

			if (inScriptJsonValue) {
				exitFromValue = true;
				inScriptJsonValue = false;
			}
			return ans;
		}

		private boolean isRetainWord(String s) {
			for (int i = 0; i < RETAIN_WORDS.length; ++i)
				if (RETAIN_WORDS[i].equals(s))
					return true;
			return false;
		}

		private final String[] RETAIN_WORDS = { "abstract", "boolean", "break", "byte", "case", "catch", "char",
				"class", "const", "continue", "debugger", "default", "delete", "do", "double", "else", "enum", "export",
				"extends", "false", "final", "finally", "float", "for", "function", "goto", "if", "implements",
				"import", "in", "instanceof", "int", "interface", "long", "native", "new", "null", "package", "private",
				"protected", "public", "return", "short", "static", "super", "switch", "synchronized", "this", "throw",
				"throws", "transient", "true", "try", "typeof", "var", "void", "volatile", "while", "with" };

		private boolean isPunctuation(char c) {
			return ((c > 'Z' || c < 'A') && (c > 'z' || c < 'a') && (c != '#') && (c != '_') && (c > '9' || c < '0')
					&& (c != '-'));
		}

		@Override
		public void handleComment(char[] data, int pos) {
			insertIndents();
			if (inScript) {
				handleScript(String.copyValueOf(data));
			} else if (inStyle) {
				handleCSS(String.copyValueOf(data));
			} else {
				insertWithColor("<!--" + String.copyValueOf(data) + "-->\n", colors.colorComment);
			}
		}

		@Override
		public void handleEndTag(HTML.Tag t, int pos) {
			--indents;
			if (!t.equals(HTML.Tag.SCRIPT)) {
				insertIndents();
			}
			insertWithColor("</", colors.colorTagLabel);
			insertWithColor(t.toString(), colors.colorTag);
			insertWithColor(">", colors.colorTagLabel);
			insertWithColor("\n");
			if (t.equals(HTML.Tag.SCRIPT))
				inScript = false;
			if (t.equals(HTML.Tag.STYLE))
				inStyle = false;
		}

		private void handleCSS(String s) {
			boolean inComment = false;
			boolean needIndent = false;
			for (int i = 0; i < s.length(); ++i) {
				if (needIndent) {
					insertIndents();
					needIndent = false;
				}
				if (!inComment && i < s.length() - 1 && s.charAt(i) == '/' && s.charAt(i + 1) == '*') {
					inComment = true;
					insertWithColor('/', colors.colorCSSComment);
					++i;
				}
				if (inComment) {
					insertWithColor(s.charAt(i), colors.colorCSSComment);
					if (i > 0 && s.charAt(i) == '/' && s.charAt(i - 1) == '*') {
						inComment = false;
						insertWithColor("\n");
						needIndent = true;
					}
				} else {
					if (s.charAt(i) == '{') {
						insertWithColor('{', colors.colorComma);
						boolean isValue = false;
						boolean inString = false;
						while (++i < s.length() && s.charAt(i) != '}') {
							if (!inString &&
								(s.charAt(i) == ' ' || s.charAt(i) == '\n' ||
								 s.charAt(i) == '\r' || s.charAt(i) == 't'))
								continue;
							if (s.charAt(i) == '\'' || s.charAt(i) == '\"')
								inString = !inString;
							if (s.charAt(i) == ';') {
								insertWithColor(';', colors.colorComma);
								isValue = false;
							} else if (s.charAt(i) == ':') {
								insertWithColor(':', colors.colorComma);
								isValue = true;
							} else if (isValue) {
								insertWithColor(s.charAt(i), colors.colorCSSAttributeValue);
							} else {
								insertWithColor(s.charAt(i), colors.colorCSSAttributeName);
							}
						}
						insertWithColor("}\n", colors.colorComma);
						needIndent = true;
					} else if (s.charAt(i) == '.' || s.charAt(i) == ':' || s.charAt(i) == ',') {
						insertWithColor(s.charAt(i), colors.colorComma);
					} else if (s.charAt(i) != '\n' && s.charAt(i) != '\r') {
						insertWithColor(s.charAt(i), colors.colorCSSVariable);
					}
				}
			}
		}

		@Override
		public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
			int ti = doc.getLength();
			insertIndents();
			++indents;
			insertWithColor("<", colors.colorTagLabel);
			insertWithColor(t.toString(), colors.colorTag);
			Enumeration<?> en = a.getAttributeNames();
			while (en.hasMoreElements()) {
				Object ob = en.nextElement();
				insertWithColor(" ", colors.colorContent);
				insertWithColor(ob.toString(), colors.colorAttributeName);
				insertWithColor("=", colors.colorComma);
				if (("href".equals(ob.toString()) || "src".equals(ob.toString()))
						&& !a.getAttribute(ob).toString().startsWith("javascript")
						&& !a.getAttribute(ob).toString().equals("")
						&& !a.getAttribute(ob).toString().equals("about:blank")
						&& !a.getAttribute(ob).toString().startsWith("#")) {
					try {
						writerLinks.append(a.getAttribute(ob).toString() + "\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
					insertWithColor('\"', colors.colorAttributeValue);
					insertWithColor(a.getAttribute(ob).toString(), colors.colorLink, true);
					insertWithColor('\"', colors.colorAttributeValue);
				} else {
					if (ob.toString().startsWith("on")) {
						insertWithColor('\"', colors.colorAttributeValue);
						handleScript(a.getAttribute(ob).toString());
						insertWithColor('\"', colors.colorAttributeValue);
					} else if ("style".equals(ob.toString())) {
						insertWithColor('\"', colors.colorAttributeValue);
						handleCSS(a.getAttribute(ob).toString());
						insertWithColor('\"', colors.colorAttributeValue);
					} else
						insertWithColor("\"" + a.getAttribute(ob).toString() + "\"", colors.colorAttributeValue);
				}
			}
			insertWithColor(">", colors.colorTagLabel);
			if (t.equals(HTML.Tag.SCRIPT))
				inScript = true;
			insertWithColor("\n");
			if (t.equals(HTML.Tag.STYLE))
				inStyle = true;
		}

		@Override
		public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos) {
			insertIndents();
			insertWithColor("<", colors.colorTagLabel);
			insertWithColor(t.toString(), colors.colorTag);
			Enumeration<?> en = a.getAttributeNames();
			while (en.hasMoreElements()) {
				Object ob = en.nextElement();
				insertWithColor(" ", colors.colorContent);
				insertWithColor(ob.toString(), colors.colorAttributeName);
				insertWithColor("=", colors.colorComma);
				if (("href".equals(ob.toString()) || "src".equals(ob.toString()))
						&& !a.getAttribute(ob).toString().startsWith("javascript")
						&& !a.getAttribute(ob).toString().equals("")
						&& !a.getAttribute(ob).toString().equals("about:blank")
						&& !a.getAttribute(ob).toString().startsWith("#")) {
					try {
						writerLinks.append(a.getAttribute(ob).toString() + "\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
					insertWithColor('\"', colors.colorAttributeValue);
					insertWithColor(a.getAttribute(ob).toString(), colors.colorLink, true);
					insertWithColor('\"', colors.colorAttributeValue);
				} else {
					if (ob.toString().startsWith("on")) {
						insertWithColor('\"', colors.colorAttributeValue);
						handleScript(a.getAttribute(ob).toString());
						insertWithColor('\"', colors.colorAttributeValue);
					} else if ("style".equals(ob.toString())) {
						insertWithColor('\"', colors.colorAttributeValue);
						handleCSS(a.getAttribute(ob).toString());
						insertWithColor('\"', colors.colorAttributeValue);
					} else
						insertWithColor("\"" + a.getAttribute(ob).toString() + "\"", colors.colorAttributeValue);
				}

			}
			insertWithColor("/>", colors.colorTagLabel);
			insertWithColor("\n");
		}

		@Override
		public void handleText(char[] data, int pos) {
			insertIndents();
			if (inScript) {
				handleScript(String.copyValueOf(data));
			} else if (inStyle) {
				handleCSS(String.copyValueOf(data));
			} else {
				boolean p = false;
				if(data[0] == '.') {
					int i = 1;
					while(i < data.length && i < 35 && data[i] != '{')
						++i;
					if (i == data.length || i == 35)
						p = false;
					else 
						p = true;
				}
				if (p)
					handleCSS(String.copyValueOf(data));
				else
					insertWithColor(String.copyValueOf(data), colors.colorContent);
			}
			insertWithColor("\n");
		}

		@Override
		public void handleEndOfLineString(String eol) {
			isWorking = false;
			try {
				writerCodes.close();
				writerLinks.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
				in = new URL(urlString).openStream();
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
			byte[] bufferedArea = new byte[65536];
			try {
				FileOutputStream out = new FileOutputStream(fileSave);
				while (in.read(bufferedArea, 0, 65536) != -1) {
					out.write(bufferedArea, 0, 65536);
				}
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
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
				HTMLHandler handler = new HTMLHandler();
				parser.parse(new FileReader(fileSave), handler, true);
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
					doc = (DefaultStyledDocument)(fin.readObject());
					fin.close();
				} else {
					doc = new DefaultStyledDocument();
					HTMLEditorKit.Parser parser = new ParserDelegator();
					HTMLHandler handler = new HTMLHandler();
					parser.parse(new FileReader(fileSave), handler, true);
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
			btnFind.setEnabled(false);
			btnCatch.setEnabled(false);
			btnCode.setEnabled(false);
			btnAbout.setEnabled(false);
			textPane.setEnabled(false);
			textPane.setBackground(Color.WHITE);
			textPane.setFont(INIT_FONT);
			textPane.setText("页面正在加载中,请等待");
			lblDownload.setVisible(true);
		});
	}

	private void afterLoading() {
		SwingUtilities.invokeLater(() -> {
			btnNew.setEnabled(true);
			btnSettings.setEnabled(true);
			textPane.setEnabled(true);
			btnWebsite.setEnabled(true);
			btnLink.setEnabled(true);
			btnFind.setEnabled(true);
			btnCatch.setEnabled(true);
			btnCode.setEnabled(true);
			btnAbout.setEnabled(true);
			lblDownload.setText(DOWNLOADING);
			lblDownload.setVisible(false);
			btnNew.setText(CLOSE);
		});
	}

	private void closePane() {
		SwingUtilities.invokeLater(() -> {
			btnNew.setEnabled(true);
			btnSettings.setEnabled(true);
			btnWebsite.setEnabled(false);
			btnLink.setEnabled(false);
			btnFind.setEnabled(false);
			btnCatch.setEnabled(false);
			btnCode.setEnabled(false);
			btnAbout.setEnabled(true);
			contentPane.removeAll();
			contentPane.add(panel, BorderLayout.EAST);
			textPane = new JTextPane();
			textPane.setEditable(false);
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
			doc = null;
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
		JOptionPane.showMessageDialog(null, "下载失败", "错误", JOptionPane.ERROR_MESSAGE);
	}

	private void urlMalformed() {
		JOptionPane.showMessageDialog(null, "URL格式有误", "错误", JOptionPane.ERROR_MESSAGE);
	}

}
