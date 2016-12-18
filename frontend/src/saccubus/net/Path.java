package saccubus.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

/**
 * <p>
 * タイトル: さきゅばす
 * </p>
 *
 * <p>
 * 説明: ニコニコ動画の動画をコメントつきで保存
 * </p>
 *
 * @version 1.22r3e
 * @author orz
 *
 */
public class Path extends File{
	static final long serialVersionUID = 1L;

	public Path(String path){
		super(path);
		// 注意　pathが相対パス名でも絶対パス名でもない時は
		// カレントディレクトリにあるファイルを意味する
	}
	public Path(File file){
		super(file.getAbsolutePath());
	}
	public Path(File dir, String name){
		super(dir, name);
	}
	Path(Path dirPath, String name){
		super(dirPath, name);
	}
	Path(String dirName, String name) {
		super(dirName, name);
	}
//	@Override
//	public String getName(){
//		return super.getName();
//	}
	/**
	 * static getName(String)
	 * @param name : full path name of file : String
	 * @return last name of file : String
	 */
//	public static String getName(String name){
//		File path = new File(name);
//		return path.getName();
//	}
	/**
	 * alias of static getName(String)
	 * @param fullname : full path name of file : String
	 * @return last name of file : String
	 */
//	public static String getFileName(String fullname){
//		return getName(fullname);
//	}
	/**
	 * alias of getAbsolutePath()
	 * @return
	 */
	private String getFullName(){
		return getAbsolutePath();
	}
	/**
	 * @return Full Path Name of Parent : String
	 */
//	@Override
//	public String getParent(){
//		return super.getParent();
//	}
	/**
	 *
	 * @return Full Path of Parent : Path
	 */
//	public Path getParentPath(){
//		return new Path(getParent());
//	}
//	@Override
//	public boolean isDirectory(){
//		return file.isDirectory();
//	}
//	@Override
//	public boolean isFile(){
//		return file.isFile();
//	}
//	public String[] list(){
//		return file.list();
//	}
	/**
	 * static list of dir, returns full path name array<br/>
	 * NOT alias as (new Path(dir)).list()<br/>
	 * @param dir : Name of directory : String
	 * @return Full filenames of child dirs or child files : Array of String
	 */
	public static String[] getFullnameList(String dir){
		File path = new File(dir);
		String[] lists = path.list();
		if(lists==null)
			return null;
		int l = lists.length;
		String[] fulls = new String[l];
		for (int i = 0; i < l; i++){
			fulls[i] = new File(path, lists[i]).getAbsolutePath();
		}
		return fulls;
	}
	/**
	 * static exists(String)
	 */
//	public static boolean exists(String name){
//		Path path = new Path(name);
//		return path.exists();
//	}
//	/**
//	 * @return Child directorys or child files : Array of File
//	 */
//	public File[] listFiles(){
//		return file.listFiles();
//	}
	/**
	 * @return Child directories or child files : Array of Path
	 */
	public Path[] listPath(){
		String[] lists = list();
		if(lists==null)
			return null;
		int l = lists.length;
		Path[] paths = new Path[l];
		for (int i = 0; i < l; i++){
			paths[i] = new Path(this, lists[i]);
		}
		return paths;
	}
	/**
	 * static String[] list(String)
	 * @param dir : name of parent, must be full path : String
	 * @return Childs of dir : Array of String
	 */
	public static String[] list(String dir){
		Path path = new Path(dir);
		return path.list();
	}
	/**
	 * static String[] list(path)
	 * @param path : Path of parent : Path
	 * @return Childs of path : Array of String
	 */
	public static String[] list(Path path){
		return path.list();
	}
	/**
	 * static File[] listFiles(dir)
	 * @param dir : Name of parent : String
	 * @return Childs of dir : Array of File
	 */
//	private static File[] listFiles(String dir){
//		Path path = new Path(dir);
//		return path.listFiles();
//	}
	/**
	 * static File[] listFiles(path)
	 * @param path : Path of parent : Path
	 * @return Childs of path : Array of File
	 */
//	private static File[] listFiles(Path path){
//		return path.listFiles();
//	}
	/**
	 * static Path[] listPath(dir)
	 * @param dir : Name of parent : String
	 * @return Childs of dir : Array of Path
	 */
	public static Path[] listPath(String dir){
		Path path = new Path(dir);
		return path.listPath();
	}
	/**
	 * static isDirectory(String str)
	 * @param str : Name of dir or file : String
	 * @return true if str is a directory
	 */
	public static boolean isDirectory(String str){
		Path path = new Path(str);
		return path.isDirectory();
	}
	/**
	 * static isFile(String str)
	 * @param str : Name of dir or file : String
	 * @return true if str is a normal file
	 */
	public static boolean isFile(String str){
		Path path = new Path(str);
		return path.isFile();
	}
	/**
	 * search file name of childs
	 * @param name : target file name, not contains directory path: String
	 * @return String : Full name of found file/directory, if name is child name
	 *                  "" empty string if not found
	 */
	public String search(String name){
		Path[] childs = this.listPath();
		//Path namePath = new Path(name); ←NG
		if(childs==null)
			return "";
		for (Path p : childs){
			if (p.getName().equals(name)){
				return p.getAbsolutePath();		// means p.getFullName()
			}
		}
		return "";
	}
	/**
	 * static search file name of childs
	 * @param dir : parent file name : String
	 * @param name : target file name, not contains directory path: String
	 * @return String : Full name of found file/directory, if name is child name
	 *                  "" empty string if not found
	 */
	public static String search(String dir, String name){
		Path dirPath = new Path(dir);
		return dirPath.search(name);
	}
	/**
	 * static search file name of childs: ANOTHER IMPLEMENTATION
	 * @param dir : parent file name : String
	 * @param name : target file name, not contains directory path: String
	 * @return String : Full name of found file/directory, if name is child name
	 *                  "" empty string if not found
	 */
	public static String searchFile(String dir, String name){
		File dirFile = new File(dir);
		String[] childs = dirFile.list();
		if(childs==null)
			return "";
		for (String s : childs){
			if (s.equals(name)){
				// return new File(dirFile, name).getAbsolutePath();
				return dir + File.separator + name;
			}
		}
		return "";
	}
	/**
	 * index of string in the last name of Path, i.e. path.getName()
	 * @param str : search key string
	 * @return index of str, -1 if not found
	 */
	public int indexOf(String str){
		String name = getName();
		return name.indexOf(str);
	}
	/**
	 * whether contains key string in the last name of Path, i.e. path.getName()
	 * @param str : search key string
	 * @return true if last name of path contains key string : boolean
	 */
	public boolean contains(String str){
		return indexOf(str) >= 0;
	}
	public static boolean contains(File file, String str){
		String name = file.getPath();
		return name.indexOf(str)>=0;
	}
	/**
	 * search file name conteins key string among childs
	 * @param key : target key : String
	 * @return String : Full name of found file/directory, if that child name contains key
	 *                  "" empty string if not found
	 */
	public String searchContains(String key){
		String[] childs = list();
		if(childs==null)
			return "";
		for (String s : childs){
			if (s.indexOf(key) >= 0){
				return new Path(this, s).getFullName();		// i.e. p.getFullName()
			}
		}
		return "";
	}
	/**
	 * static search file name conteins key string among childs
	 * @param dir : parent file full path name : String
	 * @param key : target key : String
	 * @return String : Full name of found file/directory, if that child name contains key
	 *                  "" empty string if not found
	 */
	public static String searchContains(String dir, String key){
		Path path = new Path(dir);
		return path.searchContains(key);
	}
	/**
	 * @see java.io.File#equals(java.lang.Object)
	 */
//	public boolean equals(Object obj){
//		if (!obj.getClass().equals(this.getClass())){
//			return false;
//		}
//		return this.of().equals(((Path)obj).of());
//	}
	/**
	 * Read all text from file and return String with Encoding
	 */
	public static String readAllText(String path, String encoding) {
		BufferedReader br = null;
		File file = null;
		try {
			file  = new File(path);
		}catch(Exception e){
			return "";
		}
		if(!file.isFile())
			return "";
		try {
			br = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), encoding));
			StringBuffer sb = new StringBuffer();
			String str = null;
			while ((str = br.readLine()) != null){
				sb.append(str + "\n");
			}
			return sb.substring(0);
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		} finally {
			if (br != null) {
				try { br.close(); }
				catch (IOException e) { }
			}
		}
	}
	public boolean writeAllText(String text, String encoding) {
		BufferedWriter bw = null;
		try{

			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this, false), encoding));
			bw.write(text);
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}finally{
			if(bw!=null)
			try {	bw.close();	}
			catch (IOException e) {
				return false;
			}
		}
		return true;
	}
	public static boolean writeAllText(String filename, String text, String encoding){
		return new Path(filename).writeAllText(text, encoding);
	}
	/**
	 *
	 * @param string   filename
	 * @param app_dir  directory name
	 * @return String : fullname of found file,<br/>
	 *  "" empty string if not found
	 */
	public static String sSearchFile(String string, String app_dir) {
		return search(app_dir, string);
	}

	public String getRelativePath(){
		return getAbsolutePath().replace(new Path("").getAbsolutePath(), ".");
	}
	public String replace(String old, String rep){
		return getRelativePath().replace(old, rep);
	}
	public String getUnixPath(){
		return replace(File.separator, "/");
	}
	public static String toUnixPath(String path){
		return new Path(path).getUnixPath();
	}
	public static String toUnixPath(File file){
		return new Path(file).getUnixPath();
	}
	public static Path mkTemp(String string) {
		File dir = new File("temp");
		if (dir.mkdir()){
			System.out.println("Created directory: temp");
		}
		if (!dir.isDirectory()){
			System.out.println("Can't make directory: temp");
			dir = new File("");
		}
		return new Path(dir, string);
	}
	/**
	 *
	 * @param srcfile
	 * @param destfile
	 * @return
	 */
	/*
	public static boolean fileCopy(File srcfile, File destfile) {
		FileChannel srcch = null, destch = null;
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(srcfile);
			srcch = fis.getChannel();
			fos = new FileOutputStream(destfile);
			destch = fos.getChannel();
			destch.transferFrom(srcch, 0, srcch.size());
			//sc.transferTo(0, sc.size(), dc);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (destch != null) try { destch.close(); } catch (IOException e) {};
			if (fos != null) try { fos.flush(); fos.close(); } catch(Exception e1) {e1.printStackTrace();};
			if (srcch != null) try { srcch.close(); } catch (IOException e) {};
			if (fis != null) try { fis.close(); } catch(Exception e3) {e3.printStackTrace();};
		}
	}
	*/
	public static boolean fileCopy(File srcfile, File destfile) {
		try {
			Files.copy(srcfile.toPath(), destfile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * unescape Unicode-Escape like "\u0061"
	 */
	public static boolean unescapeUEtoXml(Path input, Path xml){
		String path = input.getPath();
		String text = readAllText(path, "UTF-8");
		return unescapeStoreXml(xml, text, Path.toUnixPath(input));
	}

	public static boolean unescapeStoreXml(Path xml, String json, String comment) {
		try {
			PrintStream ps = new PrintStream(new FileOutputStream(xml), true, "UTF-8");
			ps.println("# ");
			ps.print("json = ");
			ps.println(json);
			ps.flush();
			ps.close();
			Properties prop = new Properties();
			prop.load(new FileInputStream(xml));
			// load ISO-8859-1 decoding Unicode-Escape
			prop.storeToXML(new FileOutputStream(xml), comment, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 *
	 * @param srcfile
	 * @param destfile
	 * @return ok?
	 */
	public static boolean move(File srcfile, File destfile) {
		try {
			Files.move(srcfile.toPath(), destfile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * Read all text from file and return String with Encoding
	 */
	public static String readAllText(File file, String encoding) {
		return Path.readAllText(file.getPath(), encoding);
	}
	/**
	 * Write all text to file with Encoding
	 */
	public static void writeAllText(File file, String text, String encoding) {
		writeAllText(file.getPath(), text, encoding);
	}
	public static String getExtention(File file){
		String name = file.getName();
		if(!name.contains("."))
			return "";
		return name.substring(name.lastIndexOf('.'));
	}
	public String getExtension() {
		return getExtention(this);
	}
	public static boolean hasExt(File file, String ext){
		return getExtention(file).equals(ext);
	}
	public boolean hasExt(String ext){
		return hasExt(this, ext);
	}
	static String getRemovedExtName(String path) {
		int index = path.lastIndexOf(".");
		if (index > path.lastIndexOf(File.separator)) {
			path = path.substring(0, index);		// 拡張子を削除
		}
		return path;
	}
	static String getReplacedExtName(String path, String ext) {
		return getRemovedExtName(path) + ext;
	}
	public static File getReplacedExtFile(File file, String ext){
		return new File(getReplacedExtName(file.getPath(),ext));
	}
	public File replaceExt(String ext){
		return getReplacedExtFile(this, ext);
	}
}