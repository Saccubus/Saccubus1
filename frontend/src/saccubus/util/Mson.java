package saccubus.util;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Mylist System Object Notation
 * @author orz
 * @version 1.40
 */
public class Mson {

	enum MsonType {
		MSON_NULL,
		MSON_STRING,
		MSON_LIST,
		MSON_ASSOC,
		MSON_NUMBER,
		MSON_BOOLEAN,
	}
	public MsonType type;
	private Object obj;
	private final static boolean DEBUG = false;
	private class MsonList extends ArrayList<Mson> {
		@Override
		public String toString(){
			if(this.isEmpty()){
				return "[]";
			}
			StringBuffer sb = new StringBuffer();
			for(Mson mson:this){
				sb.append("," + mson.toString());
			}
			return sb.replace(0, 1, "[").toString() + "]";
		}
	}
	private class MsonAssoc extends HashMap<String,Mson>{
		@Override
		public String toString(){
			if(this.isEmpty()){
				return "{}";
			}
			StringBuffer sb = new StringBuffer();
			for(String key:keySet()){
				sb.append(","+"\""+key+"\":"+get(key).toString());
			}
			return sb.replace(0, 1, "{").toString() + "}";
		}
	}

	public Mson() {
		type = MsonType.MSON_NULL;
		obj = null;
	}
	public Mson(String str){
		this();
		if(str!=null){
			type = MsonType.MSON_STRING;
			obj = str;
		}
	}
	public Mson(Number num){
		this();
		if(num!=null){
			type = MsonType.MSON_NUMBER;
			obj = num;
		}
	}
	public Mson(int i){
		this(Integer.valueOf(i));
	}
	public Mson(boolean bool){
		this();
		type = MsonType.MSON_BOOLEAN;
		obj = bool;
	}
	public static Mson newInstance(String str){
		return new Mson(str);
	}
	public String toString(){
		switch (type) {
		case MSON_NULL:
			return "[]";
		case MSON_STRING:
			if(!(obj instanceof String))
				return "*ERROR:shutSB MSON_STRING*" + obj.toString() + "*";
			return "\""+(String)obj + "\"";
		case MSON_LIST:
			if(!(obj instanceof MsonList))
				return "*ERROR:shutSB MSON_LIST*" + obj.toString() + "*";
			return ((MsonList)obj).toString();
		case MSON_ASSOC:
			if(!(obj instanceof MsonAssoc))
				return "*ERROR:shutSB MSON_ASSOC*" + obj.toString() + "*";
			return ((MsonAssoc)obj).toString();
		case MSON_NUMBER:
			if(!(obj instanceof Number))
				return "*ERROR:shutSB MSON_NUMBER*" + obj.toString() + "*";
			return ((Number)obj).toString();
		case MSON_BOOLEAN:
			if(!(obj instanceof Boolean))
				return "*ERROR:shutSB MSON_BOOLEAN*" + obj.toString() + "*";
			return ((Boolean)obj).toString();

		default:
			break;
		}
		return "*ERROR:shutSB MSON_ASSOC*" + obj.toString() + "*";
	}
	public static Mson newList(){
		Mson mson = new Mson();
		mson.type = MsonType.MSON_LIST;
		mson.obj = mson.new MsonList();
		return mson;
	}
	public Mson add(Mson element) throws Exception{
		if(element==null){
			element = new Mson();
		}
		switch(type){
		case MSON_NULL:
			type = MsonType.MSON_LIST;
			obj = new MsonList();
		case MSON_LIST:
			// string := [<mson>, <mson>, ...]
			MsonList list = (MsonList)obj;
			list.add(element);
			obj = list;
			return this;
		case MSON_STRING:
			throw new Exception();
		case MSON_ASSOC:
			throw new Exception();
		case MSON_NUMBER:
			throw new Exception();
		default:
			break;
		}
		return null;
	}
	public Mson append(Mson mList) throws Exception{
	//	Mson mson = (Mson)this.clone();
		Mson mson = this;
		if(mList==null||mList.type==MsonType.MSON_NULL){
			return mson;
		}
		if(mList.type!=MsonType.MSON_LIST){
			return null;
		}
		MsonList list = (MsonList)mList.obj;
		for(Mson element:list){
			mson.add(element);
		}
		return mson;
	}
	public Mson add(String str) throws Exception{
		return add(new Mson(str));
	}
	public static Mson newAssoc(){
		Mson mson = new Mson();
		mson.type = MsonType.MSON_ASSOC;
		MsonAssoc hash = mson.new MsonAssoc();
		mson.obj = hash;
		return mson;
	}
	public static MsonType getType(Mson mson){
		return mson.type;
	}
	public Mson put(String key, Mson val) throws Exception{
		switch (type) {
		case MSON_NULL:
			type = MsonType.MSON_ASSOC;
			obj = new MsonAssoc();
		case MSON_ASSOC:
			// string := {"<key>": <val>, "<key>": <val>, ...}
			MsonAssoc hash = (MsonAssoc)obj;
			hash.put(key, val);
			obj = hash;
			return this;
		case MSON_LIST:
			throw new Exception();
		case MSON_STRING:
			throw new Exception();
		case MSON_NUMBER:
			throw new Exception();
		default:
			break;
		}
		return null;
	}
	public static Mson parse(String text) throws Exception{
		return parse(text,0,new StringBuffer());
	}
	public static Mson parse(String input, int last, StringBuffer ret) throws Exception{
		char c;
		Mson mson;
		Mson mson1;
		String key;
		c = input.charAt(last);
		if(c=='['){
			last++;
			mson = Mson.newList();
			while(last < input.length()){
				c= input.charAt(last);
				if(c==']'){
					last++;
					ret.replace(0, ret.length(), input.substring(last));
					if(DEBUG) System.out.println("[mson0]:" + mson);
					return mson;
				}
				mson1 = parse(input, last, ret);
				if(DEBUG)
					System.out.println("[mson1:" + mson1);
				c = ret.charAt(0);
				if(c==','){
					input = ret.substring(0);
					last = 1;
					mson = mson.add(mson1);
					if(DEBUG) System.out.println("[mson2,:" + mson);
					continue;
				}
				if(c==']'){
					ret.delete(0, 1);
					mson = mson.add(mson1);
					if(DEBUG) System.out.println("[mson3]:" + mson);
					return mson;
				}
			}
		}
		if(c==']'){
			last++;
			ret.replace(0, ret.length(), input.substring(last));
			mson = new Mson();
			if(DEBUG) System.out.println("]mson:" + mson);
			return mson;
		}
		if(c=='{'){
			last++;
			mson = Mson.newAssoc();
			while(true){
				key = parseString(input, ++last, ret);
				if(DEBUG) System.out.println("<key>" + key);
				input = ret.substring(0);
				last = 0;
				c = input.charAt(last++);
				if(c==':'){
					mson1 = parse(input, last, ret);
					if(DEBUG) System.out.println(":mson1:" + mson1);
					input = ret.substring(0);
					last = 0;
					c = input.charAt(last++);
					if(c==','){
						mson = mson.put(key, mson1);
						if(DEBUG) System.out.println("{mson1:{" +key + ":" + mson1);
						continue;
					}
					if(c=='}'){
						ret.delete(0, 1);
						mson = mson.put(key, mson1);
						if(DEBUG) System.out.println("{mson}:{" +key + ":" + mson1);
						return mson;
					}
				}
				if(DEBUG) {
					System.out.println("Illegal char:" + c);
					System.out.println("input:" + input);
					System.out.println("last :" + last);
					System.out.println("ret  :" + ret);
				}
				throw new Exception();
			}
		}
		if(c=='}'){
			last++;
			ret.replace(0, ret.length(), input.substring(last));
			mson = new Mson();
			if(DEBUG) System.out.println("}mson:" + mson);
			return mson;
		}
		if(c=='\"'){
			last++;
			key = parseString(input, last, ret);
			mson = new Mson(key);
			if(DEBUG) System.out.println("key:" + mson);
			return mson;
		}
		// else Number or Boolean or null
		if(Character.isDigit(c)){
			Number num = parseNum(input, last, ret);
			mson = new Mson(num);
			if(DEBUG) System.out.println("num:" + mson);
			return mson;
		}
		if(input.substring(last, last+4).toLowerCase().equals("true")){
			mson = new Mson(true);
			ret.replace(0, ret.length(), input.substring(last+4));
			if(DEBUG) System.out.println("true:" + mson);
			return mson;
		}
		if(input.substring(last, last+5).toLowerCase().equals("false")){
			mson = new Mson(false);
			ret.replace(0, ret.length(), input.substring(last+5));
			if(DEBUG) System.out.println("false:" + mson);
			return mson;
		}
		if(input.substring(last, last+4).toLowerCase().equals("null")){
			mson = new Mson();
			ret.replace(0, ret.length(), input.substring(last+4));
			if(DEBUG) System.out.println("null:" + mson);
			return mson;
		}
		Matcher m = Pattern.compile("[\\],}]").matcher(input.substring(last));
		if(m.find()){
			ret.replace(0,ret.length(),input.substring(m.end()));
			if(DEBUG) System.out.println("null<-"+ input.substring(last,m.start()));
			return null;
		}
		last++;
		ret.replace(0,ret.length(),input.substring(last));
		if(DEBUG) System.out.println("null<-"+ c);
		return null;
	}
	private static String parseString(String input, int last, StringBuffer ret) {
		char c;
		StringBuffer sb = new StringBuffer();
		while(last < input.length()){
			c = input.charAt(last++);
			if(c=='\"'){
				ret.replace(0, ret.length(), input.substring(last));
				return sb.toString();
			}
			sb.append(c);
		}
		ret.delete(0, ret.length());
		return sb.toString();
	}
	private static Number parseNum(String input, int last, StringBuffer ret) {
		char c;
		StringBuffer sb = new StringBuffer();
		while(last < input.length()){
			c = input.charAt(last);
			if(Character.isDigit(c)){
				sb.append(c);
				last++;
				continue;
			}
			break;
		}
		int i = Integer.decode(sb.toString());
		ret.replace(0, ret.length(), input.substring(last));
		return Integer.valueOf(i);
	}
	public void prettyPrint(PrintStream ps){
		prettyPrint(ps, 0);
	}
	private void prettyPrint(PrintStream ps, int indent){
		String tab = " ";
		String tabs = "";
		for(int i = 0; i < indent; i++) tabs += tab;
		switch(type){
		case MSON_NULL:
			ps.print(tabs);
			ps.println("null");
			break;
		case MSON_STRING:
		case MSON_NUMBER:
		case MSON_BOOLEAN:
			ps.print(tabs);
			ps.println(toString());
			break;
		case MSON_LIST:
			ps.print(tabs);
			ps.println("[");
			for(Mson element:(MsonList)obj){
				element.prettyPrint(ps, indent+1);
			}
			ps.print(tabs);
			ps.println("]");
			break;
		case MSON_ASSOC:
			ps.print(tabs);
			ps.println("{");
			MsonAssoc assoc = (MsonAssoc)obj;
			for(String key:assoc.keySet()){
				ps.print(tabs);
				ps.print("\""+key+"\":"+tab);
				Mson val = assoc.get(key);
				MsonType valtype = val.type;
				switch (valtype) {
				case MSON_NULL:
				case MSON_STRING:
				case MSON_BOOLEAN:
				case MSON_NUMBER:
					ps.println(val.toString());
					break;
				case MSON_ASSOC:
				case MSON_LIST:
					val.prettyPrint(ps, indent+2);
				default:
					break;
				}
			}
			ps.print(tabs);
			ps.println("}");
			break;
		default:
				break;
		}
	}
	public Mson deepSearch(String key){
		switch(type){
		case MSON_ASSOC:
			MsonAssoc hash = (MsonAssoc)obj;
			if(hash.containsKey(key))
				return hash.get(key);
			//else
			for(Mson mson:hash.values()){
				Mson mson2 = mson.deepSearch(key);
				if(mson2!=null){
					return mson2;
				}
			}
			return null;
		case MSON_LIST:
			MsonList list = (MsonList)obj;
			for(Mson mson:list){
				Mson mson2 = mson.deepSearch(key);
				if(mson2!=null){
					return mson2;
				}
			}
			return null;
		case MSON_NULL:
		case MSON_STRING:
		case MSON_NUMBER:
		case MSON_BOOLEAN:
			return null;
		default:
			break;
		}
		return null;
	}
	public Mson getList(String key) throws Exception{
		Mson ret = new Mson();
		if(type==MsonType.MSON_ASSOC){
			MsonAssoc hash = (MsonAssoc)obj;
			for(String key2:hash.keySet()){
				Mson mson = hash.get(key2);
				if(key.equals(key2)){
					ret = ret.add(mson);
				}else{
					ret = ret.append(mson.getList(key));
				}
			}
			return ret;
		}
		if(type==MsonType.MSON_LIST){
			MsonList list = (MsonList)obj;
			for(Mson mson:list){
				ret = ret.append(mson.getList(key));
			}
			return ret;
		}
		return ret;
	}
	public ArrayList<Mson[]> getLists(String[] keys) throws Exception{
		ArrayList<Mson[]> ret = new ArrayList<Mson[]>();	//list of Assoc
		Mson[] rets = new Mson[keys.length];	// values[] : entry of list
		if(type==MsonType.MSON_ASSOC){
			MsonAssoc hash = (MsonAssoc)obj;
			rets = new Mson[keys.length];
			for(String mkey:hash.keySet()){
				Mson mson = hash.get(mkey);
				boolean flag = false;
				for(int i=0;i<keys.length;i++){
					if(mkey.equals(keys[i])){
						rets[i] = mson;
						flag = true;
					}
				}
				if(!flag){
					ret.addAll(mson.getLists(keys));
				}
			}
			if(rets[0]!=null)
				ret.add(rets);
			return ret;
		}
		if(type==MsonType.MSON_LIST){
			MsonList list = (MsonList)obj;
			for(Mson mson:list){
				ret.addAll(mson.getLists(keys));
			}
			return ret;
		}
		return ret;
	}
	public ArrayList<String[]> getListString(String[] keys) throws Exception{
		ArrayList<String[]> ret = new ArrayList<String[]>();	//list of Assoc
		String[] rets = new String[keys.length];	// values[] : entry of list
		if(type==MsonType.MSON_ASSOC){
			MsonAssoc hash = (MsonAssoc)obj;
			rets = new String[keys.length];
			for(String mkey:hash.keySet()){
				Mson mson = hash.get(mkey);
				boolean flag = false;
				for(int i=0;i<keys.length;i++){
					if(mkey.equals(keys[i])){
						String s = mson.toString();
						if(mson.type==MsonType.MSON_STRING)
							s = s.substring(1, s.length()-1);
						rets[i] = s;
						flag = true;
					}
				}
				if(!flag){
					ret.addAll(mson.getListString(keys));
				}
			}
			if(rets[0]!=null)
				ret.add(rets);
			return ret;
		}
		if(type==MsonType.MSON_LIST){
			MsonList list = (MsonList)obj;
			for(Mson mson:list){
				ret.addAll(mson.getListString(keys));
			}
			return ret;
		}
		return ret;
	}
	public static void main(String[] arg) throws Exception{
		Mson mson0 = new Mson();
		System.out.println(""+ mson0);
		Mson mson1 = new Mson("string");
		System.out.println(""+mson1);
		Mson mson2 = new Mson();
		mson2 = mson2.add(mson1);
		System.out.println("2_1>"+mson2);
		mson2 = mson2.add(mson1);
		System.out.println("2_2>"+mson2);
		Mson mson3 = new Mson();
		mson3.put("key", mson1);
		System.out.println("3_1>"+mson3);
		mson3.put("key2", mson2);
		System.out.println("3_2>"+mson3);
		Mson mson4 = new Mson();
		StringBuffer ret =  new StringBuffer();
		mson4 = Mson.parse("{\"key2\":[\"string\",\"string\"],\"key\":\"string\"}", 0, ret);
		System.out.println("4_1>"+mson4);
		mson4 = Mson.parse("{\"fullscreen\":false,\"ƒXƒNƒŠ[ƒ“•\":1200,\"screen_height\":900,\"antialias\":false,\"port\":39390,\"max_script_execution_time\":5000,\"max_local_storage_size\":512000,\"upnp\":false,\"udp_port\":39391,\"language\":\"jp\",\"lobby_servers\":[\"m2op.net\"]}}", 0, ret);
		System.out.println("4_2>"+mson4);
		System.out.println("End");
	}
}
