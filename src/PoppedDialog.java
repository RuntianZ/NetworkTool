/**
 * @author Runtian Zhai
 * @license MIT
 * 
 */

import java.awt.Toolkit;

import javax.swing.JDialog;

@SuppressWarnings("serial")
public class PoppedDialog extends JDialog {

	@Override
	public void setSize(int arg0, int arg1) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		{
			super.setSize(arg0, arg1);
			int x = Main.mainPage.getX() + Main.mainPage.getWidth() / 2 - arg0 / 2;
			if (x < 50)
				x = 50;
			if (x > toolkit.getScreenSize().getWidth() - arg0 - 50)
				x = (int) (toolkit.getScreenSize().getWidth() - arg0 - 50);
			int y = Main.mainPage.getY() + Main.mainPage.getHeight() / 2 - arg1 / 2;
			if (y < 50)
				y = 50;
			if (y > toolkit.getScreenSize().getHeight() - arg1 - 50)
				y = (int) (toolkit.getScreenSize().getHeight() - arg1 - 50);
			setLocation(x, y);
		}
	}
	
}
