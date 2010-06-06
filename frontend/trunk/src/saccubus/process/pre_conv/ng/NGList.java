/**
 * 
 */
package saccubus.process.pre_conv.ng;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author PSI
 *
 */
public class NGList {
	/**
	 * NGリスト
	 */
	private LinkedList<NGItem> ItemList = new LinkedList<NGItem>();
	/**
	 * Stringの配列からリストを作る。
	 * @param list
	 */
	public NGList(String[] list){
		for(int i=0;i<list.length;i++){
			addList(list[i]);
		}
	}
	/**
	 * InputStreamから作る。
	 * @param is
	 * @throws IOException 
	 */
	public NGList(InputStream is) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
		String line;
		while((line = br.readLine()) != null){
			addList(line);
		}
		br.close();
	}
	/**
	 * ファイルから作る。
	 * @param file
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public NGList(File file) throws FileNotFoundException, IOException{
		this(new FileInputStream(file));
	}
	/**
	 * 
	 * @param filename
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public NGList(String filename) throws FileNotFoundException, IOException{
		this(new FileInputStream(filename));
	}
	/**
	 * リストにNGワードを追加する。
	 * @param key
	 */
	private void addList(String key){
		if(key != null && key.length() > 0){
			ItemList.add(new NGItem(key));
		}
	}
	public boolean match(String str){
		Iterator<NGItem> it = ItemList.iterator();
		NGItem item;
		NGItem.MatchType type;
		while(it.hasNext()){
			item = it.next();
			type = item.match(str);
			if(type == NGItem.MatchType.Banned){
				return true;
			}else if(type == NGItem.MatchType.Accepted){
				return false;
			}
		}
		return false;
	}
}
