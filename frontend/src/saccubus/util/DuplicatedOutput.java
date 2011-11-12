/**
 *
 */
package saccubus.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * @author orz
 * @version 1.26.2
 */
public class DuplicatedOutput extends OutputStream {
	private PrintStream ps;
	private PrintStream fos;

	public DuplicatedOutput() {
	}
	public DuplicatedOutput(File file) throws IOException{
		//fos = new PrintStream(file);
		fos = new PrintStream(new FileOutputStream(file, true),true);
	}
	public PrintStream dup(PrintStream ps){
		this.ps = ps;
		return new PrintStream(this);
	}

	/**
	 * @param file
	 * @throws IOException
	 */
	public DuplicatedOutput(File file, PrintStream dest) throws IOException{
		fos = new PrintStream(file);
		ps = dest;
	}

	public void print(String x){
		ps.print(x);
		fos.print(x);
	}
	public void print(Object x){print(""+x);}
	public void print(int x){print(""+x);}
	public void print(boolean x){print(""+x);}
	public void print(byte x){print(""+x);}
	public void print(short x){print(""+x);}
	public void print(long x){print(""+x);}
	public void print(char x){print(""+x);}
	public void print(double x){print(""+x);}
	public void print(float x){print(""+x);}
	public void print(char[] x){
		print(new String(x));
	}
	public void println(){
		ps.println();
		fos.println();
	}
	public void println(String x){
		ps.println(x);
		fos.println(x);
	}
	public void println(Object x){println(""+x);}
	public void println(int x){println(""+x);}
	public void println(boolean x){println(""+x);}
	public void println(byte x){println(""+x);}
	public void println(short x){println(""+x);}
	public void println(long x){println(""+x);}
	public void println(char x){println(""+x);}
	public void println(double x){println(""+x);}
	public void println(float x){println(""+x);}
	public void println(char[] x){
		println(new String(x));
	}
	public void flush(){
		ps.flush();
		fos.flush();
	}
	public void close(){
		ps.close();
		fos.close();
	}
	public PrintStream getFilePrintStream() {
		return fos;
	}

	/* (”ñ Javadoc)
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException {
		ps.write(b);
		fos.write(b);
	}

}
