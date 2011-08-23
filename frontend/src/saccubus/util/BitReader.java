package saccubus.util;

import java.io.IOException;
import java.io.InputStream;

public class BitReader {

	private byte[] buf;
	private InputStream is;
	private int rembits;
	private int remval;

	public BitReader(InputStream in){
		buf = new byte[9];
		is = in;
		remval = 0;
		rembits = 0;
	}

	public long readBit(int bits) throws IOException{
		if (bits > 63){
			throw new IOException("bits too big: " + bits);
		}
		if (bits <= 0){
			throw new IOException("bits must be positive: " + bits);
		}
		int n = (bits + 7 - rembits) / 8;
		if (n > 0)
			is.read(buf, 0, n);
		long ret = remval;
		int i = 0;
		while(rembits < bits){
			ret = (ret << 8) + buf[i++];
			rembits += 8;
		}
		rembits -= bits;	// 0 <= rembiys < 8
		remval = (int)(ret & ((1 << rembits) - 1));
		ret >>= rembits;
		return ret;
	}
}
