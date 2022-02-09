package saccubus.conv;

import java.util.ArrayList;

/**
 * タイトル: さきゅばす<br/>
 * 説明: ニコニコ動画の動画をコメントつきで保存<br/>
 *
 * @author 未入力<br/>
 * @version 1.30<br/>
 */
public class CommandReplace {

	private final ArrayList<String> ngList;
	private final ArrayList<String> replaceList;

	public CommandReplace(String src, String dest){
		ngList = makeList(src);
		replaceList = makeList(dest);
	}

	private final ArrayList<String> makeList(String str){
		if (str == null || str.isEmpty()){
			return null;
		}
		String[] tmp = str.split(" ");
		String[] tmp2 = new String[tmp.length];
		int index = 0;
		int index2;
		for(index2 = 0;index < tmp.length && index2 < tmp.length; index2++){
			if (tmp[index].startsWith("\"")){
				StringBuffer sb = new StringBuffer(tmp[index]);
				for(index++; index < tmp.length; index++){
					sb.append(" " + tmp[index]);
					if (tmp[index].endsWith("/")){
						index++;
						break;
					}
				}
				tmp2[index2] = sb.toString();
			} else {
				tmp2[index2] = tmp[index];
				index++;
			}
		}
		ArrayList<String> list = new ArrayList<String>();
		for (index = 0; index < index2; index++){
			str = tmp2[index];
			if (str.length() >= 2 && str.startsWith("\"") && str.endsWith("\"")){
				list.add(str.substring(1,str.length() - 1));
			} else {
				list.add(str);
			}

		}
		return list;
	}

	public String replace(String cmd){
		if (cmd == null){
			return null;
		}
		if (ngList == null || ngList.isEmpty() || ngList.get(0).isEmpty()){
			return cmd;
		}
		for (int i = 0; i < ngList.size(); i++){
			String ngCmd = ngList.get(i);
			String replaceCmd;
			if (replaceList != null && i < replaceList.size()){
				replaceCmd = replaceList.get(i);
			} else {
				replaceCmd = "";
			}
			if (ngCmd.equals("all")){
				ngCmd = ".*";
			}
			if (ngCmd.isEmpty()){
				if (cmd.isEmpty())
					cmd = replaceCmd;
			} else {
				cmd = cmd.replaceAll(ngCmd, replaceCmd);
			}
		}
		return cmd;
	}
}
