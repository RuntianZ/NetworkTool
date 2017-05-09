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
import javax.swing.JList;
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

class CodeColorChooser extends SynchronizedPanel {
	private JButton btnApply;
	private JPanel valuePanel;
	private JList<ColorPackage> list;

	/**
	 * Create the panel.
	 */
	public CodeColorChooser(ColorLibrary thecolors) {
		colors = thecolors.clone();
		
		setSize(450, 425);
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new BorderLayout());
		btnPanel.add(panel, BorderLayout.CENTER);
		add(btnPanel, BorderLayout.SOUTH);
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		JLabel label = new JLabel("\u5355\u51FB\u5E94\u7528\u4FDD\u5B58\u8BBE\u7F6E");
		label.setFont(new Font("微软雅黑", Font.PLAIN, 16));
		panel.add(label);
		
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
		Vector<ColorPackage> vec = new Vector<>();
		vec.add(new ColorPackage("背景") {
			@Override
			public void run() {
				Color color = JColorChooser.showDialog(null, "选择颜色", colors.colorBackground); 
				if (color != null)
					colors.colorBackground = color;
				updatePane();
			}

			@Override
			public JComponent getValue() {
				JLabel lbl = new JLabel();
				lbl.setPreferredSize(new Dimension(40, 20));
				lbl.setBackground(colors.colorBackground);
				lbl.setOpaque(true);
				return lbl;
			}
		});
		vec.add(new ColorPackage("网页内容") {
			@Override
			public void run() {
				Color color = JColorChooser.showDialog(null, "选择颜色", colors.colorContent);
				if (color != null)
					colors.colorContent = color;
				updatePane();
			}
			
			@Override
			public JComponent getValue() {
				JLabel lbl = new JLabel();
				lbl.setPreferredSize(new Dimension(40, 20));
				lbl.setBackground(colors.colorContent);
				lbl.setOpaque(true);
				return lbl;
			}
		});
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
		vec.add(new ColorPackage("标签") {
			@Override
			public void run() {
				Color color = JColorChooser.showDialog(null, "选择颜色", colors.colorTag); 
				if (color != null)
					colors.colorTag = color;
				updatePane();
			}
			
			@Override
			public JComponent getValue() {
				JLabel lbl = new JLabel();
				lbl.setPreferredSize(new Dimension(40, 20));
				lbl.setBackground(colors.colorTag);
				lbl.setOpaque(true);
				return lbl;
			}
		});
		vec.add(new ColorPackage("标签标识") {
			@Override
			public void run() {
				Color color = JColorChooser.showDialog(null, "选择颜色", colors.colorTagLabel);
				if (color != null)
					colors.colorTagLabel = color;
				updatePane();
			}
			
			@Override
			public JComponent getValue() {
				JLabel lbl = new JLabel();
				lbl.setPreferredSize(new Dimension(40, 20));
				lbl.setBackground(colors.colorTagLabel);
				lbl.setOpaque(true);
				return lbl;
			}
		});
		vec.add(new ColorPackage("属性名") {
			@Override
			public void run() {
				Color color = JColorChooser.showDialog(null, "选择颜色", colors.colorAttributeName); 
				if (color != null)
					colors.colorAttributeName = color;
				updatePane();
			}
			
			@Override
			public JComponent getValue() {
				JLabel lbl = new JLabel();
				lbl.setPreferredSize(new Dimension(40, 20));
				lbl.setBackground(colors.colorAttributeName);
				lbl.setOpaque(true);
				return lbl;
			}
		});
		vec.add(new ColorPackage("属性值") {
			@Override
			public void run() {
				Color color = JColorChooser.showDialog(null, "选择颜色", colors.colorAttributeValue); 
				if (color != null)
					colors.colorAttributeValue = color;
				updatePane();
			}
			
			@Override
			public JComponent getValue() {
				JLabel lbl = new JLabel();
				lbl.setPreferredSize(new Dimension(40, 20));
				lbl.setBackground(colors.colorAttributeValue);
				lbl.setOpaque(true);
				return lbl;
			}
		});
		vec.add(new ColorPackage("资源链接") {
			@Override
			public void run() {
				Color color = JColorChooser.showDialog(null, "选择颜色", colors.colorLink); 
				if (color != null)
					colors.colorLink = color;
				updatePane();
			}
			
			@Override
			public JComponent getValue() {
				JLabel lbl = new JLabel();
				lbl.setPreferredSize(new Dimension(40, 20));
				lbl.setBackground(colors.colorLink);
				lbl.setOpaque(true);
				return lbl;
			}
		});
		vec.add(new ColorPackage("网页注释") {
			@Override
			public void run() {
				Color color = JColorChooser.showDialog(null, "选择颜色", colors.colorComment); 
				if (color != null)
					colors.colorComment = color;
				updatePane();
			}
			
			@Override
			public JComponent getValue() {
				JLabel lbl = new JLabel();
				lbl.setPreferredSize(new Dimension(40, 20));
				lbl.setBackground(colors.colorComment);
				lbl.setOpaque(true);
				return lbl;
			}
		});
		vec.add(new ColorPackage("CSS字段") {
			@Override
			public void run() {
				Color color = JColorChooser.showDialog(null, "选择颜色", colors.colorCSSVariable); 
				if (color != null)
					colors.colorCSSVariable = color;
				updatePane();
			}
			
			@Override
			public JComponent getValue() {
				JLabel lbl = new JLabel();
				lbl.setPreferredSize(new Dimension(40, 20));
				lbl.setBackground(colors.colorCSSVariable);
				lbl.setOpaque(true);
				return lbl;
			}
		});
		vec.add(new ColorPackage("Json属性名") {
			@Override
			public void run() {
				Color color = JColorChooser.showDialog(null, "选择颜色", colors.colorJsonAttributeName); 
				if (color != null)
					colors.colorJsonAttributeName = color;
				updatePane();
			}
			
			@Override
			public JComponent getValue() {
				JLabel lbl = new JLabel();
				lbl.setPreferredSize(new Dimension(40, 20));
				lbl.setBackground(colors.colorJsonAttributeName);
				lbl.setOpaque(true);
				return lbl;
			}
		});
		vec.add(new ColorPackage("Json属性值") {
			@Override
			public void run() {
				Color color = JColorChooser.showDialog(null, "选择颜色", colors.colorJsonAttributeValue); 
				if (color != null)
					colors.colorJsonAttributeValue = color;
				updatePane();
			}
			
			@Override
			public JComponent getValue() {
				JLabel lbl = new JLabel();
				lbl.setPreferredSize(new Dimension(40, 20));
				lbl.setBackground(colors.colorJsonAttributeValue);
				lbl.setOpaque(true);
				return lbl;
			}
		});
		vec.add(new ColorPackage("风格属性名") {
			@Override
			public void run() {
				Color color = JColorChooser.showDialog(null, "选择颜色", colors.colorCSSAttributeName); 
				if (color != null)
					colors.colorCSSAttributeName = color;
				updatePane();
			}
			
			@Override
			public JComponent getValue() {
				JLabel lbl = new JLabel();
				lbl.setPreferredSize(new Dimension(40, 20));
				lbl.setBackground(colors.colorCSSAttributeName);
				lbl.setOpaque(true);
				return lbl;
			}
		});
		vec.add(new ColorPackage("风格属性值") {
			@Override
			public void run() {
				Color color = JColorChooser.showDialog(null, "选择颜色", colors.colorCSSAttributeValue); 
				if (color != null)
					colors.colorCSSAttributeValue = color;
				updatePane();
			}
			
			@Override
			public JComponent getValue() {
				JLabel lbl = new JLabel();
				lbl.setPreferredSize(new Dimension(40, 20));
				lbl.setBackground(colors.colorCSSAttributeValue);
				lbl.setOpaque(true);
				return lbl;
			}
		});
		vec.add(new ColorPackage("代码保留字") {
			@Override
			public void run() {
				Color color = JColorChooser.showDialog(null, "选择颜色", colors.colorCodeRetain); 
				if (color != null)
					colors.colorCodeRetain = color;
				updatePane();
			}
			
			@Override
			public JComponent getValue() {
				JLabel lbl = new JLabel();
				lbl.setPreferredSize(new Dimension(40, 20));
				lbl.setBackground(colors.colorCodeRetain);
				lbl.setOpaque(true);
				return lbl;
			}
		});
		vec.add(new ColorPackage("代码内变量") {
			@Override
			public void run() {
				Color color = JColorChooser.showDialog(null, "选择颜色", colors.colorCodeVariable); 
				if (color != null)
					colors.colorCodeVariable = color;
				updatePane();
			}
			
			@Override
			public JComponent getValue() {
				JLabel lbl = new JLabel();
				lbl.setPreferredSize(new Dimension(40, 20));
				lbl.setBackground(colors.colorCodeVariable);
				lbl.setOpaque(true);
				return lbl;
			}
		});
		vec.add(new ColorPackage("代码内函数") {
			@Override
			public void run() {
				Color color = JColorChooser.showDialog(null, "选择颜色", colors.colorCodeFunction); 
				if (color != null)
					colors.colorCodeFunction = color;
				updatePane();
			}
			
			@Override
			public JComponent getValue() {
				JLabel lbl = new JLabel();
				lbl.setPreferredSize(new Dimension(40, 20));
				lbl.setBackground(colors.colorCodeFunction);
				lbl.setOpaque(true);
				return lbl;
			}
		});
		vec.add(new ColorPackage("代码字符串") {
			@Override
			public void run() {
				Color color = JColorChooser.showDialog(null, "选择颜色", colors.colorCodeString); 
				if (color != null)
					colors.colorCodeString = color;
				updatePane();
			}
			
			@Override
			public JComponent getValue() {
				JLabel lbl = new JLabel();
				lbl.setPreferredSize(new Dimension(40, 20));
				lbl.setBackground(colors.colorCodeString);
				lbl.setOpaque(true);
				return lbl;
			}
		});
		vec.add(new ColorPackage("代码标点") {
			@Override
			public void run() {
				Color color = JColorChooser.showDialog(null, "选择颜色", colors.colorComma); 
				if (color != null)
					colors.colorComma = color;
				updatePane();
			}
			
			@Override
			public JComponent getValue() {
				JLabel lbl = new JLabel();
				lbl.setPreferredSize(new Dimension(40, 20));
				lbl.setBackground(colors.colorComma);
				lbl.setOpaque(true);
				return lbl;
			}
		});
		vec.add(new ColorPackage("代码内注释") {
			@Override
			public void run() {
				Color color = JColorChooser.showDialog(null, "选择颜色", colors.colorCSSComment); 
				if (color != null)
					colors.colorCSSComment = color;
				updatePane();
			}
			
			@Override
			public JComponent getValue() {
				JLabel lbl = new JLabel();
				lbl.setPreferredSize(new Dimension(40, 20));
				lbl.setBackground(colors.colorCSSComment);
				lbl.setOpaque(true);
				return lbl;
			}
		});
		list = new JList<>(vec);
		list.addListSelectionListener(e -> {
			updateValuePanel();
		});
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
		valuePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
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
		doc = textPane.getStyledDocument();
		try {
			doc.remove(0, doc.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}  
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
		insertWithColor("style", colors.colorTag);
		insertWithColor(">", colors.colorTagLabel);
		insertWithColor("\n");
		insertWithColor("</", colors.colorTagLabel);
		insertWithColor("body", colors.colorTag);
		insertWithColor(">", colors.colorTagLabel);
		insertWithColor("\n");
		textPane.setDocument(doc);
		textPane.updateUI();
		updateValuePanel();
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
}
