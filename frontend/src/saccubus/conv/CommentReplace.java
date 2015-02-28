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

	public CommentReplace(Chat item, String ssrc, String sdest, String senabled, String spartial, String starget, String sfill){
		chat = item;
		src = ssrc;
		dest = sdest;
		enabled = toBoolean(senabled);
		partial = toBoolean(spartial);
		replace_user = contains(starget,"user");
		replace_owner = contains(starget,"owner");
		fill = toBoolean(sfill);
		System.out.println("Final-converted:" +chat.getVpos() +":/replace(src:"+src +",dest:"+dest
				+",enabled:"+enabled +",targetOU:"+replace_owner+"+"+replace_user
				+",fill:"+fill +",partial:"+partial+").");
	}

	Chat getChat(){
		return chat;
	}

	private boolean toBoolean(String str){
		//decode����
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
	boolean isEquals(String str,String key){
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

	public String replace(String com){
		if(partial){
			//������v
			if(!fill)
				//�����u��
				return com.replace(src, dest);
			else if(com.contains(src))
				//�S�Ă�u��
				return dest;
			else
				//��v����
				return com;
		}else if (src.equals(com)){
			//�S�̈�v����
			return dest;
		}else
			//��v����
			return com;
	}

	void replace(Chat chat) {
		if(isEnabled() && this.chat.getVpos() <= chat.getVpos()){
			if(!chat.isOwner()){
				//���[�U�[�R�����g
				if(isUsers())
					chat.process(this);
			}else{
				//�I�[�i�[�R�����g
				if(getChat().equals(chat)){
					//�������g�̃R�����g �������Ȃ�
					chat.addCmd(Chat.CMD_LOC_SCRIPT);
				}
				else if(!chat.isScript()){
					//�X�N���v�g�ȊO
					if(isOwner())
						chat.process(this);
				}
			}
		}
	}

}
