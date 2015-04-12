import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;


public class ServerTest {
	
	Socket sock;
	
	public ServerTest() {
		try {
			sock = new Socket("localhost", 33333);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void send(String filename) {
		try {
			File music = new File(filename);
			System.out.println(music.length());
			BufferedInputStream fromFile = new BufferedInputStream(new FileInputStream(music));
			BufferedOutputStream toServer = new BufferedOutputStream(sock.getOutputStream());
			byte[] buf = new byte[4096];
			int read = 0;
			while ((read = fromFile.read(buf)) != -1) {
				System.out.println(buf);
				toServer.write(buf, 0, read);
				toServer.flush();
			}
			fromFile.close();
			toServer.close();
			sock.close();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		ServerTest t = new ServerTest();
		t.send("data/Official Ojai Valley Taxidermy TV Commercial.mp3");
	}
}
