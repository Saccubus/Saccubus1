package saccubus.json;

import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import saccubus.util.Logger;

public class Mson {

//	private final static boolean DEBUG = false;
	private JsonElement json;
	public static final Mson MSON_NULL = new Mson("null");
	public Mson(JsonElement je) {
		setJson(je);
	}
	public Mson(String input){
		new Mson(parse(input).json);
	}
	public String toString(){
		if(isNull())
			return "null";
		else
			return json.toString();
	}
	public String getAsString(){
		return unquote(toString());
	}
	public String getAsString(String key){
		return get(key).getAsString();
	}
	public boolean getAsBoolean(){
		return json.getAsBoolean();
	}
	final String S_QUOTE2 = "\"";
	private String unquote(String str) {
		if(str==null) return null;
		str = str.trim();
		if(str.startsWith(S_QUOTE2) && str.endsWith(S_QUOTE2)){
			str = str.substring(1, str.length()-1);
		}
		if(str.equals("null"))
			return null;
		return str;
	}
	private boolean isPrimitive(){
		return json.isJsonPrimitive();
	}
	private boolean isString(){
		return isPrimitive() && json.getAsJsonPrimitive().isString();
	}
	boolean isObject(){
		return json.isJsonObject();
	}
	boolean isArray(){
		return json.isJsonArray();
	}
	public boolean isEmpty(){
		return isString() && json.getAsString().isEmpty();
	}
	void setJson(JsonElement js) {
		json = js;
	}
	Set<Entry<String, JsonElement>> entrySet(){
		return getAsJsonObject().entrySet();
	}
	private JsonObject getAsJsonObject(){
		return json.getAsJsonObject();
	}
	private JsonArray getAsJsonArray(){
		return json.getAsJsonArray();
	}
	public static Mson parse(String text){
		JsonReader reader = new JsonReader(new StringReader(text));
		reader.setLenient(true);
		return new Mson(new JsonParser().parse(reader));
	}
	public void prettyPrint(PrintStream ps){
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		ps.println(gson.toJson(json));
	}
	public void prettyPrint(Logger log){
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		log.println(gson.toJson(json));
	}
	public static void prettyPrint(String input, PrintStream ps) {
		try {
			parse(input).prettyPrint(ps);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void prettyPrint(String input, Logger log) {
		try {
			parse(input).prettyPrint(log);
		} catch (Exception e) {
			log.printStackTrace(e);
		}
	}
	public Mson get(String key) {
		if(isObject()){
			for(Entry<String, JsonElement> ent:entrySet()){
				String k = ent.getKey();
				Mson sj = new Mson(ent.getValue());
				if(key.equals(k))
					return sj;
				Mson sjo = sj.get(key);
				if(!sjo.isNull())
					return sjo;
			}
			return MSON_NULL;
		}
		if(isArray()){
			JsonArray ja = json.getAsJsonArray();
			for(JsonElement jae : ja){
				Mson sj = new Mson(jae);	//配列の1要素
				Mson sje = sj.get(key);	//要素のget結果
				if(!sje.isNull())
					return sje;
			}
			return MSON_NULL;
		}
		return MSON_NULL;
	}
	public Mson get2(String key) { // 最初の1階層だけ調べる
		if(isObject()){
			for(Entry<String, JsonElement> ent:entrySet()){
				String k = ent.getKey();
				Mson sj = new Mson(ent.getValue());
				if(key.equals(k))
					return sj;
			}
			return MSON_NULL;
		}
		return MSON_NULL;
	}
	public static ArrayList<String[]> getListString(Mson mson,String[] keys){
		ArrayList<String[]> ret = new ArrayList<String[]>();
		String[] rets = new String[keys.length];
		if(mson.isObject()){
			rets = new String[keys.length];
			for(Entry<String,JsonElement> ent:mson.entrySet()){
				Mson val = new Mson(ent.getValue());
				boolean flag = false;
				for(int i=0;i < keys.length;i++){
					if(keys[i].equals(ent.getKey())){
						String s = val.toString();
						if(val.isString()){
							s = val.getAsString();
						}
						rets[i] = s;
						flag = true;
					}
				}
				if(!flag){
					ret.addAll(getListString(val,keys));
				}
			}
			if(rets[0]!=null)
				ret.add(rets);
			return ret;
		}
		if(mson.isArray()){
			for(JsonElement je : mson.getAsJsonArray()){
				ret.addAll(getListString(new Mson(je),keys));
			}
			return ret;
		}
		return ret;
	}
	public ArrayList<String[]> getListString(String[] keys){
		return getListString(this, keys);
	}
	public boolean isNull() {
		return json==null || json.isJsonNull()
			|| equals(MSON_NULL)
			|| (isString() && json.getAsString().equals("[]"));
	}
	public Mson get(int i) {
		if(isArray()){
			JsonArray ja = getAsJsonArray();
			return new Mson(ja.get(i));
		}
		return MSON_NULL;
	}
	public int getSize(){
		if(isArray()){
			JsonArray ja = getAsJsonArray();
			return ja.size();
		}
		return 0;
	}
	public String getAsString(int i) {
		return get(i).getAsString();
	}
}
