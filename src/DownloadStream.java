import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DownloadStream {
	private InputStream in;
	private OutputStream out;
	
	public DownloadStream(InputStream in, OutputStream out) {
		this.in = in;
		this.out = out;
	}
	
	public void start() {
		byte[] bufferedArea = new byte[65536];
		try {
			while (true) {
				int t = in.read(bufferedArea, 0, 65536);
				if (t == -1)
					break;
				out.write(bufferedArea, 0, t);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
