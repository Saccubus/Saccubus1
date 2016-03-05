package saccubus.util;

import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class AudioPlay {
	public static boolean playWav(File file){
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
			return true;
		} catch (Exception e) {
			System.out.println("wav error");
			//e.printStackTrace();
			return false;
		}
	}

	public static boolean playWav(String wav){
		File file = new File(wav);
		if(!file.exists()){
			System.out.println("wav file not found: "+wav);
			return false;
		}
		return playWav(file);
	}

	public static void main(String[] args){
		String filename =
			//	"C:/WINDOWS/Media/dollJudgement.wav";
				"end.wav";
		playWav(filename);
	}
}
