/**
 * @author Runtian Zhai
 * @license MIT
 * 
 */

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import java.awt.Font;
import java.util.Vector;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.ScrollPaneConstants;

@SuppressWarnings("serial")
class CodeColorChooser extends SynchronizedPanel {
	private JButton btnApply;
	private JPanel valuePanel;
	private SingleSelectionList<ColorPackage> list;
	private boolean isEdited;
	private JLabel lblSave;
	private Vector<ColorPackage> vec;
	private Vector<Color> vecColors;

	/**
	 * Create the panel.
	 */
	public CodeColorChooser(ColorLibrary thecolors) {
		colors = thecolors.clone();
		valuePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		setSize(450, 425);
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new BorderLayout());
		btnPanel.add(panel, BorderLayout.CENTER);
		add(btnPanel, BorderLayout.SOUTH);
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		lblSave = new JLabel("\u5355\u51FB\u5E94\u7528\u4FDD\u5B58\u8BBE\u7F6E");
		lblSave.setFont(new Font("微软雅黑", Font.PLAIN, 16));
		panel.add(lblSave);
		
		btnApply = new JButton("\u5E94\u7528");
		btnApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Main.mainPage.colorChanged(colors);
				new Thread(() -> {
					buttonLock();
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Main.mainPage.workingLock.lock();
					buttonUnlock();
					Main.mainPage.workingLock.unlock();
				}).start();	
				isEdited = false;
				lblSave.setVisible(false);
			}
		});
		btnApply.setEnabled(false);
		panel.add(btnApply);
		
		JPanel panel_1 = new JPanel();
		add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		
		JPanel leftPanel = new JPanel();
		leftPanel.setPreferredSize(new Dimension(125, 300));
		leftPanel.setLayout(new BorderLayout());
		{
			JLabel lbl = new JLabel("\u9009\u62E9\u9879");
			lbl.setHorizontalAlignment(SwingConstants.CENTER);
			lbl.setFont(new Font("微软雅黑", Font.PLAIN, 20));
			leftPanel.add(lbl, BorderLayout.NORTH);
		}
		vec = new Vector<>();
		initVecColors();
		addColorPackage("背景", 0);
		addColorPackage("网页内容", 1);
		vec.add(new ColorPackage("缩进数量") {
			@Override
			public void run() {
				String s = JOptionPane.showInputDialog(null, "输入缩进数", "缩进数", JOptionPane.INFORMATION_MESSAGE);
				if (s == null)
					return;
				try {
					int i = Integer.parseUnsignedInt(s);
					if (i < 0 || i > 10)
						throw new NumberFormatException();
					colors.indentSpace = i;
					updatePane();
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(null, "输入需为0-10之间的整数", "错误", JOptionPane.ERROR_MESSAGE);
				}
			}

			@Override
			public JComponent getValue() {
				JLabel lbl = new JLabel(Integer.toString(colors.indentSpace));
				lbl.setFont(new Font("微软雅黑", Font.PLAIN, 15));
				return lbl;
			}
		});
		addColorPackage("标签", 2);
		addColorPackage("标签标识", 3);
		addColorPackage("属性名", 4);
		addColorPackage("属性值", 5);
		addColorPackage("资源链接", 6);
		addColorPackage("网页注释", 7);
		addColorPackage("CSS字段", 8);
		addColorPackage("Json属性名", 9);
		addColorPackage("Json属性值", 10);
		addColorPackage("风格属性名", 11);
		addColorPackage("风格属性值", 12);
		addColorPackage("代码保留字", 13);
		addColorPackage("代码内变量", 14);
		addColorPackage("代码内函数", 15);
		addColorPackage("代码字符串", 16);
		addColorPackage("代码标点", 17);
		addColorPackage("代码内注释", 18);
		list = new SingleSelectionList<>(vec);
		list.addListSelectionListener(e -> {
			updateValuePanel();
		});
		list.setSelectedIndex(0);
		list.setFont(new Font("微软雅黑", Font.PLAIN, 16));
		JScrollPane scr = new JScrollPane(list);
		scr.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		leftPanel.add(scr, BorderLayout.CENTER);
		JButton btnChoose = new JButton("\u4FEE\u6539");
		btnChoose.addActionListener(e -> {
			ColorPackage c = list.getSelectedValue();
			if (c == null)
				return;
			c.run();
		});
		btnPanel.add(btnChoose, BorderLayout.WEST);
		btnChoose.setPreferredSize(new Dimension(125, 10));
		panel_1.add(leftPanel, BorderLayout.WEST);
		JPanel panel_0 = new JPanel(new BorderLayout());
		JLabel lbl_0_1 = new JLabel("\u5F53\u524D\u503C");
		lbl_0_1.setFont(new Font("微软雅黑", Font.PLAIN, 15));
		lbl_0_1.setBorder(new EmptyBorder(5, 0, 5, 0));
		panel_0.add(lbl_0_1, BorderLayout.WEST);
		panel_0.add(valuePanel, BorderLayout.CENTER);
		leftPanel.add(panel_0, BorderLayout.SOUTH);
		
		JPanel panel_2 = new JPanel();
		panel_1.add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel = new JLabel("\u6548\u679C\u9884\u89C8");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("微软雅黑", Font.PLAIN, 20));
		panel_2.add(lblNewLabel, BorderLayout.NORTH);
		
		textPane = new JTextPane();
		textPane.setEditable(false);
		panel_2.add(textPane, BorderLayout.CENTER);
		
		updatePane();
		isEdited = false;
		lblSave.setVisible(false);
	}
	
	private abstract class ColorPackage implements Runnable {
		String title;
		public ColorPackage(String s) {
			title = s;
		}
		
		@Override
		public String toString() {
			return title;
		}
		
		public abstract JComponent getValue();
	}
	
	private void updatePane() {
		updateColors();
		doc = textPane.getStyledDocument();
		try {
			doc.remove(0, doc.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}  
		isEdited = true;
		textPane.setBackground(colors.colorBackground);
		insertWithColor("<!--网页注释-->", colors.colorComment);
		insertWithColor("\n");
		insertWithColor("<", colors.colorTagLabel);
		insertWithColor("body", colors.colorTag);
		insertWithColor(">", colors.colorTagLabel);
		insertWithColor("\n");
		insertIndents();
		insertWithColor("<", colors.colorTagLabel);
		insertWithColor("link ", colors.colorTag);
		insertWithColor("属性名", colors.colorAttributeName);
		insertWithColor("=");
		insertWithColor("\"属性值\" ", colors.colorAttributeValue);
		insertWithColor("href", colors.colorAttributeName);
		insertWithColor("=");
		insertWithColor("\"", colors.colorAttributeValue);
		insertWithColor("http://www.example.com", colors.colorLink, true);
		insertWithColor("\"", colors.colorAttributeValue);
		insertWithColor("");
		insertWithColor("/>", colors.colorTagLabel);
		insertWithColor("\n");
		insertIndents();
		insertWithColor("文本内容", colors.colorContent);
		insertWithColor("\n");
		insertIndents();
		insertWithColor("<", colors.colorTagLabel);
		insertWithColor("script", colors.colorTag);
		insertWithColor(">", colors.colorTagLabel);
		insertWithColor("\n");
		insertIndents();
		insertIndents();
		insertWithColor("function", colors.colorCodeRetain);
		insertWithColor("() {");
		insertWithColor("\n");
		insertIndents();
		insertIndents();
		insertIndents();
		insertWithColor("aString", colors.colorCodeVariable);
		insertWithColor("=\'");
		insertWithColor("字符串", colors.colorCodeString);
		insertWithColor("\';\n");
		insertIndents();
		insertIndents();
		insertIndents();
		insertWithColor("aJson", colors.colorCodeVariable);
		insertWithColor("=\'");
		insertWithColor("Json属性名", colors.colorJsonAttributeName);
		insertWithColor(":");
		insertWithColor("Json属性值", colors.colorJsonAttributeValue);
		insertWithColor("\';\n");
		insertIndents();
		insertIndents();
		insertIndents();
		insertWithColor("变量", colors.colorCodeVariable);
		insertWithColor(".");
		insertWithColor("函数名", colors.colorCodeFunction);
		insertWithColor("();\n");
		insertIndents();
		insertIndents();
		insertWithColor("}\n");
		insertIndents();
		insertWithColor("</", colors.colorTagLabel);
		insertWithColor("script", colors.colorTag);
		insertWithColor(">", colors.colorTagLabel);
		insertWithColor("\n");
		insertIndents();
		insertWithColor("<", colors.colorTagLabel);
		insertWithColor("style", colors.colorTag);
		insertWithColor(">", colors.colorTagLabel);
		insertWithColor("\n");
		insertIndents();
		insertIndents();
		insertWithColor("/* 代码内注释 */", colors.colorCSSComment);
		insertWithColor("\n");
		insertIndents();
		insertIndents();
		insertWithColor("CSS字段", colors.colorCSSVariable);
		insertWithColor("{");
		insertWithColor("风格属性名", colors.colorCSSAttributeName);
		insertWithColor(":");
		insertWithColor("风格属性值", colors.colorCSSAttributeValue);
		insertWithColor(";}");
		insertWithColor("\n");
		insertIndents();
		insertWithColor("<", colors.colorTagLabel);
		insertWithColor("/style", colors.colorTag);
		insertWithColor(">", colors.colorTagLabel);
		insertWithColor("\n");
		insertWithColor("</", colors.colorTagLabel);
		insertWithColor("body", colors.colorTag);
		insertWithColor(">", colors.colorTagLabel);
		insertWithColor("\n");
		textPane.setDocument(doc);
		textPane.updateUI();
		updateValuePanel();
		lblSave.setVisible(true);
		repaint();
	}
	
	private JTextPane textPane;
	private ColorLibrary colors;
	private StyledDocument doc;
	
	private void insertWithColor(String data, Color color, boolean isUnderlined) {
		SimpleAttributeSet attr = new SimpleAttributeSet();
		StyleConstants.setForeground(attr, color);
		if (isUnderlined)
			StyleConstants.setUnderline(attr, true);
		try {
			doc.insertString(doc.getLength(), data, attr);
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
	
	private void insertIndents() {
		for (int i = 0; i < colors.indentSpace; ++i)
			insertWithColor(" ");
	}
	
	private void updateValuePanel() {
		ColorPackage c = list.getSelectedValue();
		if (c != null) {
			valuePanel.removeAll();
			valuePanel.add(c.getValue(), BorderLayout.CENTER);
			valuePanel.updateUI();
		}
	}

	@Override
	public void buttonLock() {
		btnApply.setEnabled(false);
	}

	@Override
	public void buttonUnlock() {
		btnApply.setEnabled(true);
	}
	
	public void setColors(ColorLibrary thecolors) {
		colors = thecolors.clone();
		initVecColors();
		updatePane();
	}
	
	public boolean isEdited() {
		return isEdited;
	}
	
	private void addColorPackage(String title, int id) {
		vec.add(new ColorPackage(title) {
			@Override
			public void run() {
				Color color = JColorChooser.showDialog(null, "选择颜色", vecColors.elementAt(id)); 
				if (color != null) {
					vecColors.set(id, color);
					updatePane();
				}
			}
			
			@Override
			public JComponent getValue() {
				JLabel lbl = new JLabel();
				lbl.setPreferredSize(new Dimension(40, 20));
				lbl.setBackground(vecColors.elementAt(id));
				lbl.setOpaque(true);
				return lbl;
			}
		});
	}
	
	private void initVecColors() {
		vecColors = new Vector<>();
		vecColors.add(colors.colorBackground);
		vecColors.add(colors.colorContent);
		vecColors.add(colors.colorTag);
		vecColors.add(colors.colorTagLabel);
		vecColors.add(colors.colorAttributeName);
		vecColors.add(colors.colorAttributeValue);
		vecColors.add(colors.colorLink);
		vecColors.add(colors.colorComment);
		vecColors.add(colors.colorCSSVariable);
		vecColors.add(colors.colorJsonAttributeName);
		vecColors.add(colors.colorJsonAttributeValue);
		vecColors.add(colors.colorCSSAttributeName);
		vecColors.add(colors.colorCSSAttributeValue);
		vecColors.add(colors.colorCodeRetain);
		vecColors.add(colors.colorCodeVariable);
		vecColors.add(colors.colorCodeFunction);
		vecColors.add(colors.colorCodeString);
		vecColors.add(colors.colorComma);
		vecColors.add(colors.colorCSSComment);
	}
	
	private void updateColors() {
		colors.colorBackground = vecColors.elementAt(0);
		colors.colorContent = vecColors.elementAt(1);
		colors.colorTag = vecColors.elementAt(2);
		colors.colorTagLabel = vecColors.elementAt(3);
		colors.colorAttributeName = vecColors.elementAt(4);
		colors.colorAttributeValue = vecColors.elementAt(5);
		colors.colorLink = vecColors.elementAt(6);
		colors.colorComment = vecColors.elementAt(7);
		colors.colorCSSVariable = vecColors.elementAt(8);
		colors.colorJsonAttributeName = vecColors.elementAt(9);
		colors.colorJsonAttributeValue = vecColors.elementAt(10);
		colors.colorCSSAttributeName = vecColors.elementAt(11);
		colors.colorCSSAttributeValue = vecColors.elementAt(12);
		colors.colorCodeRetain = vecColors.elementAt(13);
		colors.colorCodeVariable = vecColors.elementAt(14);
		colors.colorCodeFunction = vecColors.elementAt(15);
		colors.colorCodeString = vecColors.elementAt(16);
		colors.colorComma = vecColors.elementAt(17);
		colors.colorCSSComment = vecColors.elementAt(18);
	}
}
