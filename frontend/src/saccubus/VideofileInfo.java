/**
 *
 */
package saccubus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.JLabel;

import saccubus.FFmpeg.Aspect;
import saccubus.FFmpeg.CallbackInterface;
import saccubus.util.BitReader;
import saccubus.util.Cws2Fws;
import saccubus.util.Logger;
import saccubus.util.Stopwatch;

/**
 * @author orz
 *
 */
public class VideofileInfo {
	private final File videoFile;
	private FFmpeg ffmpeg;
	private final HashMap<String,LinkedList<String>> videoInfoMap = new HashMap<String, LinkedList<String>>(0);
	private final static String VI_DURATION = "Duration:";
	private final static String VI_VIDEO = "Video:";
	private final static String VI_AUDIO = "Audio:";
	public static final String[] VIDEOINFO_KEYS = {VI_DURATION,VI_VIDEO,VI_AUDIO};
	private final StringBuffer output;
	private String src = null;
	private Aspect aspect = null;
	private int duration = -1;
	private double frameRate = -1.0;
	private final JLabel status;
	private final ConvertStopFlag flag;
	//private final Stopwatch watch;
	private Logger log;
	private File metadataFile;

	public VideofileInfo(File videofile,FFmpeg ffmpeg, JLabel status,
		ConvertStopFlag flag, Stopwatch watch, File metadataf, Logger logger){
		this.videoFile = videofile;
		this.ffmpeg = ffmpeg;
		this.status = status;
		this.flag = flag;
		//this.watch = watch;
		this.output = new StringBuffer();
		log = logger;
		metadataFile = metadataf;
		initInfoMap(VIDEOINFO_KEYS, log);
	}

	public void initInfoMap(final String[] infokeys, Logger log) {
		class InfoMapCallback implements CallbackInterface {
			@Override
			public void doEveryLoop(String e, Logger log) {
				//watch.show();
				for(String key : infokeys){
					if (e.contains(key)){
						log.println(" " + e.trim());
						output.append(e.trim() + "\n");
						e = e.replace(key, "").trim();
						LinkedList<String> list = new LinkedList<String>();
						for(String v:e.split(",+"))
							if(!v.isEmpty()) list.add(v.trim());
						videoInfoMap.put(key, list);
					}
				}
			}
			@Override
			public boolean checkStop() {
				return flag.needStop();
			}
			@Override
			public void doAbort(String e) {
				synchronized (status) {
					status.setText("ffmpegの実行を中止しました。");
				}
			}
		}

		ffmpeg.setCmd("-y -analyzeduration 10M -i ");	//workaround input option
		ffmpeg.addFile(videoFile);
		if(metadataFile!=null){
			ffmpeg.addCmd(" -f ffmetadata ");
			ffmpeg.addFile(metadataFile);
		}
		log.println("get Info:" + ffmpeg.getCmd());
		int abortedCode = 0;
		ffmpeg.exec(abortedCode, new InfoMapCallback(), log);
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
			log.println("get aspect from FWS(swf)");
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
				log.printStackTrace(e);
				return Aspect.ERROR;
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
			LinkedList<String> strs = videoInfoMap.get(VI_VIDEO);
			if (strs==null)
				return Aspect.ERROR;
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
							log.printStackTrace(e);
							//return null;
						}
						if(width!=0L && height!=0L) break;
					}
				}
			}
		}
		Aspect asp = new Aspect((int)width, (int)height);
		log.println(asp.explain());
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
		String TEXT_DURATION = VI_DURATION;
		String duration = "";
		if(!src.contains(TEXT_DURATION))
			return 0;
		int index = src.indexOf(TEXT_DURATION)+TEXT_DURATION.length();
		duration = src.substring(index, src.indexOf(",", index)).trim();
		String tms = "";
		int it = 0;
		if(duration.contains("NA")||duration.contains("N/A"))
			return 0;
		try{
			index = duration.lastIndexOf(":");	//for min:sec
			if(index < 0){
				tms = duration;		//sec
				duration = "";		//hour:min
			}else{
				tms = duration.substring(index+1);			//sec
				duration = duration.substring(0, index);	//hour:min
			}
			if(tms.contains(".")){
				it = (int)(Double.parseDouble(tms));
			} else
				it = Integer.parseInt(tms);		//sec
			index = duration.lastIndexOf(":");	//for hour:min
			if(index < 0){
				tms = duration;		//min
				duration = "";		//hour
			}else{
				tms = duration.substring(index+1);			//min
				duration = duration.substring(0, index);	//hour
			}
			it += Integer.parseInt(tms) * 60;	//min
			if(!duration.isEmpty())
				it += Integer.parseInt(duration) * 3600;	//hour
		}catch(NumberFormatException e){
			log.printStackTrace(e);
		}
		return it;
	}

	public double getFrameRate() {
		if(frameRate==-1.0){
			frameRate = getFramerate0();
		}
		return frameRate ;
	}

	private double getFramerate0() {
		LinkedList<String> strs = videoInfoMap.get(VI_VIDEO);
		double r = 0.0;
		if (strs==null)
			return r;
		log.println("getFramerate: videoInfoMap");
		try {
			for(String str:strs){
				log.print(str+",");
				if(!str.contains("tbr")) continue;
				str = str.replace("tbr", "").replace("k", "").trim();
				r = Double.parseDouble(str);
				break;
			}
		}catch (NumberFormatException e){
			r = 0.0;
		}catch (NullPointerException e){
			r = 0.0;
		}
		return r;
	}

	public boolean isVideoContainisRawvideo(){
		LinkedList<String> strs = videoInfoMap.get(VI_VIDEO);
		if(strs==null)
			return false;
		for(String s:strs){
			if(s.contains("rawvideo"))
				return true;
		}
		return false;
	}
//	int getVideoLength(File videoFile) {
//		return videoLength;
//	}

	public boolean isAudioContainsAac(){
		LinkedList<String> strs = videoInfoMap.get(VI_AUDIO);
		if(strs==null)
			return false;
		for(String s:strs){
			if(s.contains("aac"))
				return true;
		}
		return false;
	}
	public boolean isAudioContainsAacLc(){
		LinkedList<String> strs = videoInfoMap.get(VI_AUDIO);
		if(strs==null)
			return false;
		for(String s:strs){
			if(s.contains("aac") && !s.contains("he"))
				return true;
		}
		return false;
	}
}
