/**
 *
 */
package saccubus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import saccubus.FFmpeg.Aspect;
import saccubus.FFmpeg.Callback;
import saccubus.util.BitReader;
import saccubus.util.Cws2Fws;

/**
 * @author orz
 *
 */
public class VideofileInfo {
	final File videoFile;
	private FFmpeg ffmpeg;
	final HashMap<String,LinkedList<String>> videoInfoMap = new HashMap<String, LinkedList<String>>(0);
	public static final String[] VIDEOINFO_KEYS = {"Duration:","Video:","Audio:"};
	private final StringBuffer output;
	private String src = null;
	private Aspect aspect = null;
	private int duration = -1;
	private double frameRate = 0.0;

	public VideofileInfo(File videofile,FFmpeg ffmpeg){
		this.videoFile = videofile;
		this.ffmpeg = ffmpeg;
		this.output = new StringBuffer();
		initInfoMap(VIDEOINFO_KEYS);
	}

	public void initInfoMap(final String[] infokeys) {
		class GetInfoCallback implements Callback {
			@Override
			public void doEveryLoop(String e) {
				for(String key : infokeys){
					if (e.contains(key)){
						System.out.println(" " + e.trim());
						output.append(e.trim() + "\n");
						e = e.replace(key, "").trim();
						LinkedList<String> list = new LinkedList<String>();
						for(String v:e.split(",+")) if(!v.isEmpty()) list.add(v);
						videoInfoMap.put(key, list);
					}
				}
			}
		}

		ffmpeg.setCmd("-y -i ");
		ffmpeg.addFile(videoFile);
		System.out.println("get Info:" + VIDEOINFO_KEYS.toString() + " " + ffmpeg.getCmd());
		ffmpeg.exec(new GetInfoCallback());
		src = output.toString();
	}

	public Aspect getAspect(){
		if(aspect==null){
			aspect = getAspect0();
		}
		return aspect;
	}

	private Aspect getAspect0() {
		long width = 0;
		long height = 0;
		if (Cws2Fws.isFws(videoFile)){	// swf, maybe NMM
			FileInputStream fis = null;
			System.out.println("get aspect from FWS(swf)");
			try {
				fis = new FileInputStream(videoFile);
				BitReader br = new BitReader(fis);
				int bit = (int)br.readBit(32);	// "FWS" + version, dummy
					bit = (int)br.readBit(32);	// file size, dummy
					bit = (int)br.readBit(5);	// RECT bits spec
				width =	 br.readBit(bit);		// xmin is 0
				width =  br.readBit(bit);		// xmax is width
				width /= 20;	// From swip to pixel
				height = br.readBit(bit);		// ymin is 0
				height = br.readBit(bit);		// ymax is height
				height /= 20;	// From swip to pixel
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} finally {
				if (fis != null){
					try {
						fis.close();
					} catch (IOException e) { }
				}
			}
		} else {
			// check by ffmpeg
			// ffmpeg.exe -y -i file
			LinkedList<String> strs = videoInfoMap.get("Video:");
			if (strs==null)
				return null;
			for(String str:strs){
				if(!str.contains("x")) continue;
				str = str.replaceAll("[^0-9x]", "_");
				String[] list = str.split("_+");
				for (String s : list){
					if(!s.isEmpty()){
						str = s;
						if(str.startsWith("0x")) continue;
						list = str.split("x");
						if(list==null || list.length!=2) continue;
						try {
							width = Long.parseLong(list[0]);
							height = Long.parseLong(list[1]);
						} catch(NumberFormatException e){
							e.printStackTrace();
							//return null;
						}
						if(width!=0 && height!=0) break;
					}
				}
			}
		}
		Aspect asp = new Aspect((int)width, (int)height);
		System.out.println(asp.explain());
		return asp;
	}

	public int getDuration(){
		if(duration == -1){
			duration = getDuration0();
		}
		return duration;
	}

	// Duration: hh:mm:ss, -> seconds
	private int getDuration0() {
		String TEXT_DURATION = "Duration:";
		String duration = "";
		if(!src.contains(TEXT_DURATION))
			return 0;
		int index = src.indexOf(TEXT_DURATION)+TEXT_DURATION.length();
		duration = src.substring(index, src.indexOf(",", index)).trim();
		String tms = "";
		int it = 0;
		index = duration.lastIndexOf(":");	//for min:sec
		if(index < 0){
			it = Integer.parseInt(duration);	//sec
		}else{
			tms = duration.substring(index+1);
			duration = duration.substring(0, index);	//hour:min
			if(tms.contains(".")){
				it = (int)(Double.parseDouble(tms));
			} else
				it = Integer.parseInt(tms);	//sec
			index = duration.lastIndexOf(":");	//for hour:min
			if(index < 0){
				it += Integer.parseInt(duration) * 60;	//min
			}else{
				tms = duration.substring(index+1);	//min
				duration = duration.substring(0, index);	//hour
				it += Integer.parseInt(tms) * 60;	//min
				it += Integer.parseInt(duration) * 3600;	//hour
			}
		}
		return it;
	}

	public double getFrameRate() {
		if(frameRate<=0.0){
			frameRate = getFramerate0();
		}
		return frameRate ;
	}

	private double getFramerate0() {
		LinkedList<String> strs = videoInfoMap.get("Video:");
		if (strs==null)
			return 0.0;
		double r = 0.0;
		for(String str:strs){
			if(!str.contains("tbr")) continue;
			str = str.replace("tbr", "").trim();
			try {
				r = Double.parseDouble(str);
			}catch (NumberFormatException e){
				r = 0.0;
			}
			break;
		}
		return r;
	}

//	int getVideoLength(File videoFile) {
//		return videoLength;
//	}
}
