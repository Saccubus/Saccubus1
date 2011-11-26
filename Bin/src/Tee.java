import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Tee {

	/**
	 * 標準入力を多重化して標準出力すると同時に引数にコピーする
	 * @param args [-a] outfile_name : String
	 */
	public static void main(String[] args) {
		if (args.length < 1){
			System.err.println("Usage: java -cp Bin.jar Tee [-a] output_filename.");
			return;
		}
		File output;
		boolean isAppend = false;
		if (args[0].equalsIgnoreCase("-a")){
			isAppend = true;
			output = new File(args[1]);
		} else {
			output = new File(args[0]);
		}
		if (output.isDirectory()){
			System.err.println("Error: output must not be a directory.");
			return;
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(output, isAppend);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		byte[] buf = new byte[4096];
		int len = 0;
		try {
			while((len = System.in.read(buf, 0, buf.length)) > 0) {
				fos.write(buf, 0, len);
				System.out.write(buf, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null){
					fos.flush();
					fos.close();
				}
			} catch(Exception e){}
		}
		return;
	}

}
