

import java.io.*;

import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.media.PlugInManager;
import javax.media.format.AudioFormat;

public class PlaySound {
	public static void main(String[] args) {
		Format input1 = new AudioFormat(AudioFormat.MPEGLAYER3);
		Format input2 = new AudioFormat(AudioFormat.MPEG);
		Format output = new AudioFormat(AudioFormat.LINEAR);
		PlugInManager.addPlugIn(
			"com.sun.media.codec.audio.mp3.JavaDecoder",
			new Format[]{input1, input2},
			new Format[]{output},
			PlugInManager.CODEC
		);
		try{
			Player player = Manager.createPlayer(new MediaLocator(new File("src/data/09 - On GP.mp3").toURI().toURL()));
			System.out.println(player.getMediaTime().getNanoseconds());
			
			player.start();
			
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
