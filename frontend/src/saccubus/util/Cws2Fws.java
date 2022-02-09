package saccubus.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.InflaterInputStream;

import saccubus.net.Path;

// import saccubus.net.Path;


/**
 *
 * @author yuki
 */
public class Cws2Fws {

	private static final String CWS = "CWS";
	private static final String FWS = "FWS";
	private static final String TMP_FWS = "_fws.swf";

	public Cws2Fws(){
	}
	/**
	 * 圧縮SWFかどうか判定する.
	 * @param file 判定対象.
	 * @return 圧縮SWFであればtrue.
	 */
	public static boolean isCws(File file) {
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(file));
			byte header[] = new byte[CWS.length()];
			int l = bis.read(header, 0, header.length);
			if (l == header.length && CWS.equals(new String(header))) {
				return true;
			}
			/**
			 * not modefy, same as 1.22r3e2 = 1.22r3
			 *
			if (FWS.equals(new String(header))) {
				return true;
			}
			*/
		} catch (IOException ex) {
			log("Cws2Fws:" + ex.getMessage());
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException ex) { }
			}
		}
		return false;
	}

	/**
	 * 圧縮SWFを展開する.
	 * @param in 展開対象.
	 * @return 展開後のファイル. 対象が圧縮SWFでなければnull.
	 */
	public static File createFws(File in) {
		boolean wasCws = false;
		if (!isCws(in)) {
			return null;
		}
		File out = Path.mkTemp(in.getName().replace(".flv", "") + TMP_FWS);
		InputStream is = null;
		InputStream iis = null;
		OutputStream os = null;
		try {
			final int headerSize = 8;
			byte header[] = new byte[headerSize];
			is = new FileInputStream(in);
			int size = 0;
			while (size < headerSize) {
				int read = is.read(header, size, headerSize);
				if (read < 0) {
					throw new IOException("ファイルフォーマット不正");
				}
				size += read;
			}
			if (new String(header, 0, CWS.length()).equals(CWS)) {
				wasCws = true;
			}
			header[0] = "F".getBytes()[0]; // CWS -> FWS

			os = new BufferedOutputStream(new FileOutputStream(out));
			os.write(header);

			byte buffer[] = new byte[1024 * 1024];
			if (wasCws){
				iis = new InflaterInputStream(is);
			} else {
				iis = is;
			}
			while (true) {
				int rs = iis.read(buffer);
				if (rs < 0) {
					break;
				}
				os.write(buffer, 0, rs);
			}
			return out;
		} catch (IOException ex) {
			log("Cws2Fws:" + ex.getMessage());
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ex) { }
			}
			if (iis != null) {
				try {
					iis.close();
				} catch (IOException ex) { }
			}
			if (os != null) {
				try {
					os.flush();
					os.close();
				} catch (IOException ex) { }
			}
		}
		return null;
	}
/*
	public static File createFws(File in, Converter converter) throws IOException {
		File out = createFws(in);
		if (out != null){
			out = converter.nmm2avi(out);
		}
		return out;
	}
*/
	private static void log(String msg){
		System.out.println(msg);
	}

	public static boolean isFws(File file) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			byte header[] = new byte[FWS.length()];
			int l = fis.read(header, 0, header.length);
			if (l == header.length && FWS.equals(new String(header))) {
				return true;
			}
		} catch (IOException ex) {
			log("Cws2Fws:" + ex.getMessage());
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ex) { }
			}
		}
		return false;
	}

}
