package saccubus.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.InflaterInputStream;

/**
 *
 * @author yuki
 */
public class Cws2Fws {

	private static final String CWS = "CWS";
	private static final String FWS = "FWS";

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
			bis.read(header, 0, header.length);
			if (CWS.equals(new String(header))) {
				return true;
			}
		} catch (IOException ex) {
			Logger.getLogger(Cws2Fws.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException ex) {
					Logger.getLogger(Cws2Fws.class.getName()).log(Level.SEVERE, null, ex);
				}
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
		if (!isCws(in)) {
			return null;
		}
		File out = new File("fws_tmp.swf");
		InputStream is = null;
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
			header[0] = "F".getBytes()[0]; // CWS -> FWS

			os = new BufferedOutputStream(new FileOutputStream(out));
			os.write(header);

			byte buffer[] = new byte[1024 * 1024];
			InflaterInputStream iis = new InflaterInputStream(is);
			while (true) {
				int rs = iis.read(buffer);
				if (rs < 0) {
					break;
				}
				os.write(buffer, 0, rs);
			}
			return out;
		} catch (IOException ex) {
			Logger.getLogger(Cws2Fws.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ex) {
					Logger.getLogger(Cws2Fws.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
			if (os != null) {
				try {
					os.close();
				} catch (IOException ex) {
					Logger.getLogger(Cws2Fws.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
		return null;
	}
}
