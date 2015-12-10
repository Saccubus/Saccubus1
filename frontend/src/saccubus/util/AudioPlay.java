package saccubus.util;

import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class AudioPlay {
	public static void playWav(File file){
		// Read the sound file using AudioInputStream.
		AudioInputStream stream;
		byte[] buf;
		AudioFormat format;
		DataLine.Info info;
		SourceDataLine line;
		try {
			stream = AudioSystem.getAudioInputStream(file);
			buf = new byte[stream.available()];
			stream.read(buf,0,buf.length);

			format=stream.getFormat();
			long len=format.getFrameSize()*stream.getFrameLength();

			info=new DataLine.Info(SourceDataLine.class,format);
			line=(SourceDataLine)AudioSystem.getLine(info);

			line.open(format);
			line.start();

			line.write(buf,0,(int)len);

			line.drain();
			line.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void playWav(String wav){
		playWav(new File(wav));
	}

	public static void main(String[] args){
		String filename =
				"C:/WINDOWS/Media/dollJudgement.wav";
			//	"D:/mmd/wav/test03.wav";
		playWav(filename);
	}
}
