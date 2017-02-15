package saccubus.conv;

import saccubus.util.Logger;

public class CommentReplace {
	private final String src;
	private final String dest;
	private final boolean enabled;
	private final boolean partial;
	private final boolean replace_user;
	private final boolean replace_owner;
	private final boolean fill;
	private Logger log;
	final int rcolor;
	final int rsize;
	final int rlocation;
	final int vpos;
	final int sec;

	public CommentReplace(Chat item, String ssrc, String sdest, String senabled,
			String spartial, String starget, String sfill, saccubus.util.Logger logger){
		src = ssrc;
		dest = sdest;
		enabled = toBoolean(senabled);
		partial = toBoolean(spartial);
		replace_user = contains(starget,"user");
		replace_owner = contains(starget,"owner");
		rcolor = item.getColorNumber();
		rsize = item.getSize();
		rlocation = item.getLocation();
		vpos = item.getVpos();
		int s = item.getDurationSec();
		sec = s;
		fill = toBoolean(sfill);
		log = logger;
		log.println("Final-converted:" +vpos
				+":@"+item.getDurationSec()+" "+item.getColorName()+" "+item.getSizeName()+" "+item.getLocName()
				+":/replace(src:"+src +",dest:"+dest
				+",enabled:"+enabled +",targetOU:"+replace_owner+"+"+replace_user
				+",fill:"+fill +",partial:"+partial+").");
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
	private boolean contains(String str,String key){
		if(str==null)
			return false;
		else
			return str.contains(key);
	}

	private String replace(String com){
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
		int vend;
		if(sec==0)
			vend = Integer.MAX_VALUE;
		else
			vend = vpos + (sec)*100;
		String comment = chat.getComment();
		if(enabled && (vpos < chat.getVpos() && chat.getVpos() <= vend)){
			if(!chat.isOwner()){
				if(replace_user){
				//���[�U�[�R�����g
					chat.process(rcolor, rsize, rlocation, replace(comment));
				}
			}else {
				if(!chat.isScript()){
					if(replace_owner){
						//�X�N���v�g�ȊO�I�[�i�[�R�����g
						chat.process(rcolor, rsize, rlocation, replace(comment));
					}
				}
			}
		}
	}

}
