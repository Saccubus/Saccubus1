package saccubus.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class Search {
	private final String tag;
	private String tagPat = null;
	private String extPat = null;
	private String tagRegex = null;
	private String extRegex = null;

	public Search(String tag){
		this.tag = tag;
	}

	public Search(String tag, String tagPat, String extPat){
		this(tag);
		this.tagPat = tagPat;
		this.extPat = extPat;
	}

	public void setTag(String tagRegex){
		this.tagRegex = tagRegex;
		this.tagRegex = tagPat.replace("@", tag);
	}

	public void setExt(String extRegex){
		this.extRegex = extRegex;
		this.extRegex = "(.*" + extPat.replace("|", ")|(.*") + ")";
	}

	public String search1Filename(File dir){
		String list[] = dir.list(new RegExpFilter(tagRegex));
		if(list == null){
			return null;
		}
		for (int i = 0; i < list.length; i++) {
			String path = list[i];
			if (!path.matches(extRegex)){
				continue;
			}
			return path;
		}
		return null;
	}

	public ArrayList<String> searchFileList(File dir){
		String list[] = dir.list(new RegExpFilter(tagRegex));
		if(list == null){
			return null;
		}
		ArrayList<String> slist = new ArrayList<String>();
		for (int i = 0; i < list.length; i++){
			String path = list[i];
			if (path.matches(extPat)){
				slist.add(path);
			}
		}
		if (slist.isEmpty()){
			return null;
		}
		return slist;
	}

	public ArrayList<String> searchFileList(File dir, String lastChar){
		String list[] = dir.list(new RegExpFilter(tagRegex));
		if(list == null){
			return null;
		}
		ArrayList<String> slist = new ArrayList<String>();
		for (int i = 0; i < list.length; i++){
			String path = list[i];
			if (path.matches(extPat)){
				int index = path.lastIndexOf(lastChar);
				if (index >= 0){
					path = path.substring(index);
				}
				slist.add(path);
			}
		}
		if (slist.isEmpty()){
			return null;
		}
		return slist;
	}
	/**
	 *
	 * @author orz
	 *
	 */
	private class RegExpFilter implements FilenameFilter {
		private final String regex;
		public RegExpFilter(String regExp){
			this.regex = regExp;
		}
		@Override
		public boolean accept(File dir, String name) {
			if (name.matches(regex)){
				return true;
			} else {
				return false;
			}
		}
	}

}
