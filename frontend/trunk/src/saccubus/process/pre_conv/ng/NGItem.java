/**
 * 
 */
package saccubus.process.pre_conv.ng;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author PSI
 *
 */
public class NGItem {
	protected enum Type {
		Disabled,
		Normal,
		White,
		RegExp
	}
	protected enum MatchType {
		Banned,
		NotMatched,
		Accepted
	}
	private Type NGType;
	private String Keyword;
	private Pattern Pat;
	private static final String KEYWORD_NORMAL = "normal:";
	private static final String KEYWORD_WHITE = "white:";
	private static final String KEYWORD_REGEXP = "regexp:";
	/**
	 * @param keyword
	 */
	protected NGItem(String keyword) {
		String lower = keyword.toLowerCase();
		//戦闘の文字で分ける
		if(lower.startsWith(KEYWORD_NORMAL)){
			NGType = Type.Normal;
			keyword = keyword.substring(KEYWORD_NORMAL.length());
		}else if(lower.startsWith(KEYWORD_WHITE)){
			NGType = Type.White;
			keyword = keyword.substring(KEYWORD_WHITE.length());
		}else if(lower.startsWith(KEYWORD_REGEXP)){
			NGType = Type.RegExp;
			keyword = keyword.substring(KEYWORD_REGEXP.length());
		}else{
			//何も無かった場合は普通にNGワード。
			NGType = Type.Normal;
		}
		switch(NGType){
			case Normal:
				Keyword = keyword;
				break;
			case White:
				Keyword = keyword;
				break;
			case RegExp:
				try {
					Pattern.compile(keyword);
				} catch (PatternSyntaxException e) {
					System.out.println("Failed to compile regexp:"+keyword);
					System.out.println("This pattern will be disabled.");
					e.printStackTrace();
					NGType = Type.Disabled;
				}
				break;
		}
	}
	/**
	 * このNGワードに引っかかるかどうかをチェックする。
	 * @param word
	 * @return
	 */
	protected MatchType match(String word){
		if(word == null){
			return MatchType.NotMatched;
		}
		switch(NGType){
			case Normal:
				if(Keyword != null && word.indexOf(Keyword) >= 0){
					return MatchType.Banned;
				}
				break;
			case White:
				if(Keyword != null && word.indexOf(Keyword) >= 0){
					return MatchType.Accepted;
				}
				break;
			case RegExp:
				if(Pat != null && Pat.matcher(word).matches()){
					return MatchType.Banned;
				}
				break;
			case Disabled:
				return MatchType.NotMatched;
		}
		return MatchType.NotMatched;
	}
}
