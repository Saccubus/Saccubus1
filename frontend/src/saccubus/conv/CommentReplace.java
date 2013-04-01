package saccubus.conv;

public class CommentReplace {
	private final Chat chat;
	private final String src;
	private final String dest;
	private final boolean enabled;
	private final boolean partial;
	private final boolean replace_user;
	private final boolean replace_owner;
	private final boolean fill;
	private final int vpos;
	private final String color;
	private final String size;
	private final String pos;

	public CommentReplace(Chat item, String ssrc, String sdest, String senabled, String spartial, String starget, String sfill, int svpos,
			String scolor, String ssize, String spos){
		chat = item;
		src = ssrc;
		dest = sdest;
		enabled = toBoolean(senabled);
		partial = toBoolean(spartial);
		replace_user = contains(starget,"user");
		replace_owner = contains(starget,"owner");
		fill = isEquals(sfill,"true");
		color = scolor;
		size = ssize;
		pos = spos;
		vpos = svpos;
		System.out.println("Final:" +vpos +":/replace(src:"+src +",dest:"+dest
				+",enabled:"+enabled +",targetOU:"+replace_owner+"+"+replace_user
				+",fill:"+fill +",partial:"+partial
				+",color:"+color +",size:" +size+",pos:" +pos+").");
	}

	Chat getChat(){
		return chat;
	}

	private boolean toBoolean(String str){
		//decodeあり
		if(str==null)
			return false;
		else
			return str.equals("T");
	}
	static String encodeBoolean(String str){
		if(str==null)
			return "F";
		else if(str.equals("false"))
			return "F";
		else
			return "T";
	}
	static String decodeBoolean(String str){
		if(str==null)
			return "false";
		else if(str.equals("T"))
			return "true";
		else
			return "false";
	}
	private boolean isEquals(String str,String key){
		if(str==null)
			return false;
		else
			return str.equals(key);
	}
	private boolean contains(String str,String key){
		if(str==null)
			return false;
		else
			return str.contains(key);
	}

	public boolean isUsers(){
		return replace_user;
	}

	public boolean isOwner(){
		return replace_owner;
	}

	public boolean isEnabled(){
		return enabled;
	}

	public boolean isPartial(){
		return partial;
	}

	public int getVpos(){
		return vpos;
	}

	public String getColor(){
		return color;
	}

	public String getSize(){
		return size;
	}

	public String getPos(){
		return pos;
	}

	public String replace(String com){
		if(partial){
			//部分一致
			if(!fill)
				//部分置換
				return com.replace(src, dest);
			else if(com.contains(src))
				//全てを置換
				return dest;
			else
				//一致せず
				return com;
		}else if (src.equals(com)){
			//全体一致した
			return dest;
		}else
			//一致せず
			return com;
	}

	void replace(Chat chat) {
		if(isEnabled() && this.getVpos() <= chat.getVpos()){
			if(!chat.isOwner()){
				//ユーザーコメント
				if(isUsers())
					chat.process(this);
			}else{
				//オーナーコメント
				if(getChat().equals(chat)){
					//自分自身のコメント 何もしない
				}
				else if(!chat.isScript()){
					//スクリプト以外
					if(isOwner())
						chat.process(this);
				}
			}
		}
	}

}
