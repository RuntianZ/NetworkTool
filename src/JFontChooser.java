/**
 * @author Runtian Zhai
 * @license MIT
 * 
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

@SuppressWarnings("serial")
class JFontChooser extends SynchronizedPanel {
	private JTextField showTF;
	private JPanel showPan;
	private String showStr = "���� PKU 1898";
	private JScrollPane spFont;
	private JScrollPane spSize;
	private SingleSelectionList<String> lstFont;
	private SingleSelectionList<String> lstStyle;
	private SingleSelectionList<String> lstSize;
	private Font selectedfont;
	private JPanel panel;
	private JPanel panel_1;
	private JPanel panel_2;
	private JPanel panel_3;
	private JTextField txtFont;
	private JTextField txtStyle;
	private JTextField txtSize;
	private JButton ok;
	private Map<String, Integer> sizeMap;
	private String current_fontName;
	private int current_fontStyle;
	private int current_fontSize;
	private JLabel lblSave;

	public JFontChooser() {
		this.selectedfont = null;
		init(null);
	}
	
	public JFontChooser(Font font) {
		this.selectedfont = font;
		init(font);
	}

	public Font getSelectedfont() {
		return selectedfont;
	}

	public void setSelectedfont(Font selectedfont) {
		this.selectedfont = selectedfont;
	}


	private void init(Font font) {

		// ȡ�õ�ǰ������������.
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] fontNames = ge.getAvailableFontFamilyNames();

		// �ֺ�.
		String[] sizeStr = new String[] { "8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "26", "28",
				"36", "48", "72", "����", "С��", "һ��", "Сһ", "����", "С��", "����", "С��", "�ĺ�", "С��", "���", "С��", "����", "С��",
				"�ߺ�", "�˺�" };
		int sizeVal[] = { 8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72, 42, 36, 26, 24, 22, 18, 16, 15,
				14, 12, 11, 9, 8, 7, 6, 5 };
		sizeMap = new HashMap<>();
		for (int i = 0; i < sizeStr.length; ++i) {
			sizeMap.put(sizeStr[i], sizeVal[i]);
		}
		lstSize = new SingleSelectionList<>(sizeStr);
		spSize = new JScrollPane(lstSize);
		lstStyle = new SingleSelectionList<>(new String[] { "����", "����", "б��", "��б��" });
		txtFont = new JTextField();
		txtSize = new JTextField();
		txtStyle = new JTextField();

		setLayout(new BorderLayout(0, 0));
		lstFont = new SingleSelectionList<>(fontNames);
		spFont = new JScrollPane(lstFont);
		if (font == null)
			font = new Font("����", Font.PLAIN, 9);
		current_fontName = font.getName();
		current_fontStyle = font.getStyle();
		current_fontSize = font.getSize();
		txtFont.setText(font.getName());
		lstFont.setSelectedValue(font.getName(), true);

		lstStyle.setSelectedIndex(font.getStyle()); // ��ʼ����ʽlist
		if (font.getStyle() == 0) {
			txtStyle.setText("����");
		} else if (font.getStyle() == 1) {
			txtStyle.setText("����");
		} else if (font.getStyle() == 2) {
			txtStyle.setText("б��");
		} else if (font.getStyle() == 3) {
			txtStyle.setText("��б��");
		}

		lstFont.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				current_fontName = (String) lstFont.getSelectedValue();
				txtFont.setText(current_fontName);
				showTF.setFont(new Font(current_fontName, current_fontStyle, current_fontSize));
				lblSave.setVisible(true);
			}
		});

		showTF = new JTextField();
		showTF.setFont(new Font(current_fontName, current_fontStyle, current_fontSize));
		showTF.setPreferredSize(new Dimension(300, 50));
		showTF.setHorizontalAlignment(JTextField.CENTER);
		showTF.setText(showStr);
		showTF.setBackground(Color.white);
		showTF.setEditable(false);
		showPan = new JPanel(new FlowLayout());
		showPan.setPreferredSize(new Dimension(300, 60));
		showPan.add(showTF);
		showTF.setFont(font);
		add(showPan, BorderLayout.NORTH);

		// ����.

		lstStyle.setBorder(javax.swing.BorderFactory.createLineBorder(Color.gray));

		lstStyle.setSelectedValue("����", true); // ��ʼ��ΪĬ�ϵ���ʽ
		lstStyle.addListSelectionListener(new ListSelectionListener() {
			@SuppressWarnings("unchecked")
			public void valueChanged(ListSelectionEvent e) {
				String value = (String) ((SingleSelectionList<String>) e.getSource()).getSelectedValue();
				if (value.equals("����")) {
					current_fontStyle = Font.PLAIN;
				}
				if (value.equals("б��")) {
					current_fontStyle = Font.ITALIC;
				}
				if (value.equals("����")) {
					current_fontStyle = Font.BOLD;
				}
				if (value.equals("��б��")) {
					current_fontStyle = Font.BOLD | Font.ITALIC;
				}
				txtStyle.setText(value);
				showTF.setFont(new Font(current_fontName, current_fontStyle, current_fontSize));
				lblSave.setVisible(true);
			}
		});

		lstSize.setSelectedValue("9", false);
		if (font != null) {
			lstSize.setSelectedValue(Integer.toString(font.getSize()), false);
			txtSize.setText(Integer.toString(font.getSize()));
		}

		ok = new JButton("Ӧ��");
		{
			lblSave = new JLabel("����Ӧ�ñ�������");
			lblSave.setFont(new Font("΢���ź�", Font.PLAIN, 16));
			lblSave.setVisible(false);
			JPanel _panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			_panel.add(lblSave);
			_panel.add(ok);
			add(_panel, BorderLayout.SOUTH);
		}
		ok.setEnabled(false);
		new Thread(() -> {
			Main.mainPage.workingLock.lock();
			ok.setEnabled(true);
			Main.mainPage.workingLock.unlock();
		}).start();
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSelectedfont(new Font(current_fontName, current_fontStyle, current_fontSize));
				Main.mainPage.changeFont(selectedfont);
				lblSave.setVisible(false);
			}
		});

		lstSize.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				current_fontSize = (Integer) sizeMap.get(lstSize.getSelectedValue());
				txtSize.setText((String)lstSize.getSelectedValue());
				showTF.setFont(new Font(current_fontName, current_fontStyle, current_fontSize));
				lblSave.setVisible(true);
			}
		});

		setSize(450, 425);

		panel = new JPanel();
		panel.setPreferredSize(new Dimension(300, 100));
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));

		panel_1 = new JPanel(new VFlowLayout(VFlowLayout.TOP));
		panel.add(panel_1, BorderLayout.WEST);


		txtFont.setFont(new Font("΢���ź�", Font.PLAIN, 16));
		txtFont.setEditable(false);
		panel_1.add(txtFont);

		panel_1.add(spFont);

		panel_2 = new JPanel(new VFlowLayout(VFlowLayout.TOP));
		panel.add(panel_2, BorderLayout.EAST);


		txtSize.setFont(new Font("΢���ź�", Font.PLAIN, 16));
		txtSize.setEditable(false);
		panel_2.add(txtSize);

		panel_2.add(spSize);

		panel_3 = new JPanel(new VFlowLayout(VFlowLayout.TOP));
		panel.add(panel_3, BorderLayout.CENTER);



		txtStyle.setFont(new Font("΢���ź�", Font.PLAIN, 16));
		txtStyle.setEditable(false);
		panel_3.add(txtStyle);

		panel_3.add(lstStyle);
		
	}

	@Override
	public void buttonLock() {
		ok.setEnabled(false);
	}

	@Override
	public void buttonUnlock() {
		ok.setEnabled(true);
	}
}