import java.awt.Color;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;

public class HTMLHandler extends HTMLEditorKit.ParserCallback {
		private boolean inScript, inStyle;
		private final int INDENT_SPACE;
		private int indents;
		boolean atStart;
		private FileWriter writerLinks, writerCodes, writerStrings;
		private ColorLibrary colors;
		private DefaultStyledDocument doc;
		private Set<String> links;

		public HTMLHandler(ColorLibrary colors, DefaultStyledDocument doc) {
			inScript = false;
			inStyle = false;
			indents = 0;
			lastPunctuation = 'h';
			inScriptJsonValue = false;
			inBracket = false;
			exitFromValue = false;
			atStart = true;
			this.colors = colors;
			this.doc = doc;
			INDENT_SPACE = colors.indentSpace;
			try {
				writerLinks = new FileWriter(MainPage.fileLinks);
				writerCodes = new FileWriter(MainPage.fileCodes);
				writerStrings = new FileWriter(MainPage.fileStrings);
				writerLinks.write("");
				writerCodes.write("");
				writerStrings.write("");
			} catch (IOException e) {
				e.printStackTrace();
			}
			links = new HashSet<>();
		}

		private void insertWithColor(String data, Color color, boolean isUnderlined) {
			if (atStart) {
				atStart = false;
				insertWithColor("<!DOCTYPE html>", colors.colorComment);
				insertWithColor("\n");
			}
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

		private void insertWithColor(char data) {
			insertWithColor(String.valueOf(data));
		}

		private void insertWithColor(char data, Color color) {
			insertWithColor(String.valueOf(data), color);
		}
		
		private void insertIndents() {
			for (int i = 0; i < indents * INDENT_SPACE; ++i)
				insertWithColor(" ");
		}

		private boolean inScriptJsonValue, inBracket;

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
				if (lastPunctuation == '}' || lastPunctuation == 'v')
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
						} else {
							insertWithColor(word, colors.colorCodeVariable);
							lastPunctuation = 'v';
						}
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
			if (c == '(')
				inBracket = true;
			if (c == ')')
				inBracket = false;
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
				insertWithColor(";");
				if (!inBracket) {
					insertWithColor("\n");
					insertIndents();
				} else {
					insertWithColor(" ");
				}
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
			try {
				writerStrings.append(String.copyValueOf(data) + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
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
					if (!links.contains(a.getAttribute(ob).toString())) {
						try {
							writerLinks.append(a.getAttribute(ob).toString() + "\n");
						} catch (IOException e) {
							e.printStackTrace();
						}
						links.add(a.getAttribute(ob).toString());
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
					if (!links.contains(a.getAttribute(ob).toString())) {
						try {
							writerLinks.append(a.getAttribute(ob).toString() + "\n");
						} catch (IOException e) {
							e.printStackTrace();
						}
						links.add(a.getAttribute(ob).toString());
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
			try {
				writerStrings.append(String.copyValueOf(data) + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
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
			try {
				writerCodes.close();
				writerLinks.close();
				writerStrings.close();
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