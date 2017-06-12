package saccubus.json;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map.Entry;

import com.google.gson.JsonElement;

import saccubus.conv.ChatAttribute;
import saccubus.conv.ChatSave;
import saccubus.net.Path;
import saccubus.util.Logger;
//Response Data =
//[
//	{"ping": {"content": "rs:0"}},
//	{"ping": {"content": "ps:0"}},
//	{"thread": {
//		"resultcode": 0,
//		"thread": "9999999993",
//		"server_time": 1484681074,
//		"last_res": 7496,
//		"ticket": "0x38b3de9b",
//		"revision": 1,
//		"click_revision": 48}
//	},
//	{"leaf": {"thread": "9999999993","count": 5140}},
//	{"leaf": {"thread": "9999999993","leaf": 1,"count": 2140}},
//	{"leaf": {"thread": "9999999993","leaf": 2,"count": 216}},
//	{"global_num_res": {"thread": "9999999993","num_res": 7670}},
//	{"ping": {"content": "pf:0"}},
//	{"ping": {"content": "ps:1"}},
//	{"thread": {
//		"resultcode": 0,
//		"thread": "9999999993",
//		"server_time": 1484681074,
//		"last_res": 7496,
//		"ticket": "0x38b3de9b",
//		"revision": 1,
//		"click_revision": 48}
//	},
//	{"chat": {
//		"thread": "9999999993",
//		"no": 4877,
//		"vpos": 13854,
//		"leaf": 2,
//		"date": 1401116767,
//		"premium": 1,
//		"anonymity": 1,
//		"user_id": ���[�U�[ID_4877,
//		"mail": "184",
//		"content": �ʏ�R�����g4877}
//	},
//	����
//	{"chat": {
//		"thread": "9999999993",
//		"no": 7496,
//		"vpos": 2426,
//		"date": 1484578038,
//		"date_usec": 959793,
//		"anonymity": 1,
//		"user_id": "wyT4hdcnpRa5gKm7EiagcCsO20A",
//		"mail": "184",
//		"content": �ʏ�R�����g7496}
//	},
//	{"ping": {"content": "pf:1"}},
//	{"ping": {"content": "ps:2"}},
//	{"thread": {
//		"resultcode": 0,
//		"thread": "9999999994",
//		"server_time": 1484681074,
//		"last_res": 52,
//		"ticket": "0x365b5d16",
//		"revision": 1}
//	},
//	{"leaf": {"thread": "9999999994","count": 41}},
//	{"leaf": {"thread": "9999999994","leaf": 1,"count": 10}},
//	{"leaf": {"thread": "9999999994","leaf": 2,"count": 1}},
//	{"global_num_res": {"thread": "9999999994","num_res": 52}},
//	{"ping": {"content": "pf:2"}},
//	{"ping": {"content": "ps:3"}},
//	{"thread": {
//		"resultcode": 0,
//		"thread": "9999999994",
//		"server_time": 1484681074,
//		"last_res": 52,
//		"ticket": "0x365b5d16",
//		"revision": 1}
//	},
//	{"chat": {
//		"thread": "9999999994",
//		"no": 1,
//		"vpos": 4014,
//		"date": 1324739748,
//		"premium": 1,
//		"anonymity": 1,
//		"user_id": ���[�U�[ID_1,
//		"content": �R�~���j�e�B�R�����g1}
//	},
//	����
//	{"chat": {
//		"thread": "9999999994",
//		"no": 51,
//		"vpos": 5791,
//		"date": 1474285779,
//		"date_usec": 890564,
//		"premium": 1,
//		"anonymity": 1,
//		"user_id": ���[�U�[ID_51,
//		"content": �R�~���j�e�B�R�����g51}
//	},
//	{"ping": {"content": "pf:3"}},
//	{"ping": {"content": "rf:0"}}
//]
//

public class NicoJsonParser {

	private final static String ENCODING = "UTF-8";
	private final Logger log;
	private int chatCount;

	public NicoJsonParser(Logger logger) {
		log = logger;
	}

	public int getChatCount(){
		return chatCount;
	}
	public boolean commentJson2xml(File json, File xml, String kind){
		// json ���̓R�����gJson�e�L�X�g�t�@�C��
		// xml �o�̓R�����gxml�t�@�C��
		String jsonText = Path.readAllText(json, ENCODING);
		Mson mson = Mson.parse(jsonText);
		//mson.prettyPrint(log);
		PrintWriter pw = null;
		try {
			String xmlString = makeCommentXml(mson, kind);
			if(xmlString!=null){
				Path.writeAllText(xml, xmlString, ENCODING);
				log.println(kind+"�R�����gJSON��XML�ɕϊ����܂���: "+xml.getPath());
				return true;
			}
			return false;
		}finally{
			if(pw!=null){
				pw.flush();
				pw.close();
			}
		}
	}

	private String makeCommentXml(Mson mson, String kind){
		StringBuffer sb = new StringBuffer();
		chatCount = 0;
		// �w�b�_
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		// �p�P�b�g�J�n
		sb.append("<packet>");
		//mson�̑S�Ă�Array�ɂ���xmlElementString���쐬
		if(mson.isArray()){
			String s;
			boolean outflag = true;
			for(int i = 0; i < mson.getSize(); i++){
				Mson elem = mson.get(i);
				String key = null;
				JsonElement value = null;
				if(elem.isObject()){
					if(elem.entrySet().size()==1){
						for(Entry<String, JsonElement> e:elem.entrySet()){
							key = e.getKey();
							value = e.getValue();
						}
					}else{
						//�n�b�V���͈��
						return null;
					}
				}else{
					//�z��̒��g��Array��primitive��Null�̓G���[
					return null;
				}
				// key: value
				if(key==null){
					return null;
				}
				Mson m = new Mson(value);
				String contents;
				char p;	// p= 0,1,2,3,4 �Œ�
				// kind :"user" ��ʃR�����g���̓R�~���j�e�B�R�����g(p=0,1)
				// kind :"owner" ���e�҃R�����g���o(P=2�̂�, fork==1�̂�)
				// kind :"oprional" �I�v�V���i���X���b�h���o(p=3,4�̂�)
				// kind :"nicos" �j�R�X�R�����g���o(p=???)
				if(key.equals("ping")){
					// �R���e���c��؂�
					contents = xmlContents(m);
					if(contents.contains("ps:")){
						p = contents.charAt(contents.indexOf(":")+1);
						// contents: "ps:n" n�Ԗ� p�X�^�[�g
						// contents: "pf:n" n�Ԗ� p�t�B�j�b�V��
						// contents: "rs:0" json �X�^�[�g
						// contents: "rf:0" json �t�B�j�b�V��
						log.println("contetns="+contents+", p="+p);
						if(kind.equals("user")){
							outflag = p == '0' || p == '1';
							log.println("user_comment p="+p+", out="+outflag);
						}else
						if(kind.equals("owner")){
							outflag = p == '2';
							log.println("owner_comment p="+p+", out="+outflag);
						}else
						if(kind.equals("optional")){
							outflag = p == '3' || p == '4';
							log.println("optional_thread p="+p+", out="+outflag);
						}else
						if(kind.equals("nicos")){
							outflag = true;
							log.println("nicos_comment p="+p+", out="+outflag);
						}else{
							outflag = p == '0' || p == '1';
							log.println("user_comment p="+p+", out="+outflag);
						}
					}
					continue;
				}
				if(key.equals("thread")){
					// thread����
					s = "<thread"
						+xmlAttributeValue(m, "resultcode")
						+xmlAttributeValue(m, "thread")
						+xmlAttributeValue(m, "fork")
						+xmlAttributeValue(m, "server_time")
						+xmlAttributeValue(m, "last_res")
						+xmlAttributeValue(m, "ticket")
						+xmlAttributeValue(m, "revision")
						+"/>";
					if(outflag) sb.append(s);
					continue;
				}
				if(key.equals("leaf")){
					// leaf����
					s = "<leaf"
						+xmlAttributeValue(m, "thread")
						+xmlAttributeValue(m, "leaf")
						+xmlAttributeValue(m, "count")
						+"/>";
					if(outflag) sb.append(s);
					continue;
				}
				if(key.equals("global_num_res")){
					//global_num_res����
					s = "<global_num_res"
						+xmlAttributeValue(m, "thread")
						+xmlAttributeValue(m, "num_res")
						+"/>";
					if(outflag) sb.append(s);
					continue;
				}
				if(key.equals("chat")){
					//chat����
					s = "<chat"
						+xmlAttributeValue(m, "thread")
						+xmlAttributeValue(m, "fork")
						+xmlAttributeValue(m, "no")
						+xmlAttributeValue(m, "vpos")
						+xmlAttributeValue(m, "leaf")
						+xmlAttributeValue(m, "date")
						+xmlAttributeValue(m, "date_usec")
						+xmlAttributeValue(m, "score")
						+xmlAttributeValue(m, "premium")
					//	+xmlAttributeValue(m, "nicoru")
						+xmlAttributeValue(m, "anonymity")
						+xmlAttributeValue(m, "user_id")
						+xmlAttributeValue(m, "mail")
						+xmlAttributeValue(m, "deleted")
						+">"+xmlContents(m)+"</chat>";
					if(outflag) {
						sb.append(s);
						chatCount++;
					}
					continue;
				}
				// ���̑�
				{
					s = "<"+key
						+xmlAttributeValue(m, "thread")
						+">"+xmlContents(m)+"</"+key+">";
					if(outflag) sb.append(s);
				}
			}
		}else{
			//�S�̂��z��łȂ��Ȃ�G���[
			return null;
		}
		// �p�P�b�g�I��
		sb.append("</packet>");
		return sb.substring(0);
	}
	private static String xmlAttributeValue(Mson m, String key){
		// return empty string when value is null or empty
		String val = m.getAsString(key);
		if(val==null || val.isEmpty())
			val = "";
		else
			val = " "+key+"=\""+ChatAttribute.safeReference(val)+"\"";
		return val;
	}
	private static String xmlContents(Mson m){
		// return empty string when value is null or empty
		String val = m.getAsString("content");
		if(val==null || val.isEmpty())
			val = "";
		else
			val = ChatSave.safeReference(val);
			val = val.replace("\\n", "\n")		//linefeed eval
					.replace("\\\"", "\"")		//quote unescape
					.replace("\\\\", "\\");		//escape eval
		return val;
	}
}
