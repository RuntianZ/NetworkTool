import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJSON;
import org.mozilla.javascript.Scriptable;

public class Main { 
	public static final MainPage mainPage = new MainPage();
	
	public static void main(String[] args){
		mainPage.setVisible(true);
		
	/*	ColorLibrary colors = new ColorLibrary();
		colors.colorTagLabel = Color.GRAY;
		colors.colorTag = Color.BLUE;
		colors.colorComment = Color.GREEN;
		colors.colorContent = Color.BLACK;
		colors.colorAttributeName = Color.RED;
		colors.colorAttributeValue = Color.ORANGE;
		colors.colorLink = Color.ORANGE;
		colors.colorBackground = Color.BLACK;
		colors.colorCode = Color.YELLOW;
		colors.indentSpace = 3;
		Font font = new Font("宋体", Font.PLAIN, 15);
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File("settings.dat")));
			out.writeObject(colors);
			out.writeObject(font);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		//parseJS();
	}
	
	public static void parseJS() {
		  //1、通过Http请求获取js的String数据，格式如上
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File("sample.js")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();
		String s = null;
		try {
			while((s = reader.readLine()) != null)
				sb.append(s+"\n");
			s = sb.toString();
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		  //3、初始化Context
		  Context cx = Context.enter();
		  Scriptable scope = cx.initStandardObjects();
		  Context.exit();
		} 
} 


