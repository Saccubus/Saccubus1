package saccubus.prompt;

import java.util.Iterator;
import java.util.Properties;
import java.util.TreeMap;

import saccubus.info.RootInfo;

/**
 * <p>
 * タイトル: さきゅばす
 * </p>
 * 
 * <p>
 * 説明: ニコニコ動画の動画をコメントつきで保存
 * </p>
 * 
 * <p>
 * 著作権: Copyright (c) 2007 PSI
 * </p>
 * 
 * <p>
 * 会社名:
 * </p>
 * 
 * @author 未入力
 * @version 1.0
 */
public class Prompt {
	/**
	 * @param rootInfo
	 */
	public Prompt() {
		super();
	}
	public void main(String[] args) {
		PromptOption opt = new PromptOption(args);
		//設定読み込み
		RootInfo info = new RootInfo();
		info.loadInfo(null, opt.getOverrideProp());
		opt.fixInfo(info);
		//スタートアップメッセージ
		System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		System.out.println("Saccubus on CUI");
		System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		System.out.println("Overrided Properties:");
		outPropList(opt.getOverrideProp());//オーバーライドされたリストの表示
		System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		//実行
		//TODO 追加
		//終了
		System.out.println("Finished.");
	}
	public static void outPropList(Properties prop) {
	    TreeMap<Object, Object> map = new TreeMap<Object, Object>();
	    map.putAll(prop);
	    Iterator<Object> it = map.keySet().iterator();
	    while (it.hasNext()) {
	        String key = (String)it.next();
	        String value = (String)map.get(key);
	        System.out.println(key + " -> " + value);
	    }
	}
}
