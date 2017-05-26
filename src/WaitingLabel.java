import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class WaitingLabel extends JLabel {

	public static final File fileImage = new File("icon.png");
	private AtomicBoolean isStarted;
	private BufferedImage image;
	private final JLabel thisLabel = this;
	private AtomicInteger rotateCount;

	public WaitingLabel(Color background) {
		setPreferredSize(new Dimension(30, 30));
		BufferedImage _image = null;
		try {
			_image = ImageIO.read(fileImage);
		} catch (IOException e) {
			e.printStackTrace();
		}
		image = new BufferedImage(30, 30, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = image.createGraphics();
		image = g2d.getDeviceConfiguration().createCompatibleImage(30, 30, Transparency.TRANSLUCENT);
		g2d.dispose();
		g2d = image.createGraphics();
		g2d.drawImage(_image.getScaledInstance(30, 30, Image.SCALE_SMOOTH), 0, 0, null);
		g2d.dispose();
		setIcon(new ImageIcon(image));
		if (background != null) {
			setBackground(background);
			setOpaque(true);
		}
		isStarted = new AtomicBoolean(false);
		rotateCount = new AtomicInteger(0);
	}

	public WaitingLabel() {
		this(null);
	}

	public void start(int millis) {
		isStarted.set(true);
		new Thread(() -> {
			rotateCount.set(0);
			while (true) {

				try {
					Thread.sleep(millis);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (!isStarted.get())
					return;
				SwingUtilities.invokeLater(() -> {
					thisLabel.setIcon(new ImageIcon(rotateImage(image, 15 * (rotateCount.incrementAndGet() % 24))));
				});

			}
		}).start();
	}

	public void stop() {
		isStarted.set(false);
	}
	
	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		if (!b)
			stop();
	}

	private static BufferedImage rotateImage(BufferedImage bufferedimage, int degree) {
		int w = bufferedimage.getWidth();
		int h = bufferedimage.getHeight();
		int type = bufferedimage.getColorModel().getTransparency();
		BufferedImage img;
		Graphics2D graphics2d;
		(graphics2d = (img = new BufferedImage(w, h, type)).createGraphics())
				.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2d.rotate(Math.toRadians(degree), w / 2, h / 2);
		graphics2d.drawImage(bufferedimage, 0, 0, null);
		graphics2d.dispose();
		return img;
	}
	

}
