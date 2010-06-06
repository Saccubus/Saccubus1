/**
 * IO�Ɋւ��郆�[�e�B���e�B�B
 */
package saccubus.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author PSI
 *
 */
public class IOUtil {
	/*
	 * OutputStream�փf�[�^����������
	 */
	public static void writeInt(OutputStream os, int i) throws IOException {
		// ���g���G���f�B�A��
		os.write((i >> 0) & 0xff);
		os.write((i >> 8) & 0xff);
		os.write((i >> 16) & 0xff);
		os.write((i >> 24) & 0xff);
	}
	public static int readInt(InputStream is) throws IOException{
		return
			((is.read() & 0xff) <<  0)+ 
			((is.read() & 0xff) <<  8)+
			((is.read() & 0xff) << 16)+
			((is.read() & 0xff) << 24);
	}
	
}
