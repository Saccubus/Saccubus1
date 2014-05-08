package saccubus.json;

import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class Mson {

//	private final static boolean DEBUG = false;
	private JsonElement json;
	public Mson(JsonElement je) {
		json = je;
	}
	public String toString(){
		if(json==null)
			return "[]";
		else
			return json.toString();
	}
/*
	private Mson add(JsonElement elem) throws Exception {
		if(elem==null){
			elem = JsonNull.INSTANCE;	//ì¡éÍÇ»èàóù
		}
		if(json.isJsonNull()){
			json = new JsonArray();
		}
		if(json.isJsonArray()){
			json.getAsJsonArray().add(elem);
			return this;
		}
		else
		if(json.isJsonPrimitive()){
			throw new Exception();
		}
		else
		return null;
	}
	public Mson add(Mson mson) throws Exception {
		if(mson==null){
			return add(JsonNull.INSTANCE);
		}
		return add(mson.json);
	}
	public Mson put(String key, Mson val) throws Exception {
		if(json.isJsonNull()){
			json = new JsonObject();
		}
		if(json.isJsonObject()){
			json.getAsJsonObject().add(key, val.json);
			return this;
		}
		return null;
	}
	public Mson append(Mson mList) throws Exception {
		if(mList==null||mList.json.isJsonNull())
			return this;
		if(!mList.json.isJsonArray())
			return null;
		json.getAsJsonArray().addAll(mList.json.getAsJsonArray());
		return this;
	}
*/
	public static Mson parse(String text) throws Exception {
		JsonReader reader = new JsonReader(new StringReader(text));
		reader.setLenient(true);
		return new Mson(new JsonParser().parse(reader));
	}
	public void prettyPrint(PrintStream ps){
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String js = gson.toJson(json);
		ps.println(js);
	}
	public static ArrayList<String[]> getListString(JsonElement json,String[] keys) throws Exception{
		ArrayList<String[]> ret = new ArrayList<String[]>();
		String[] rets = new String[keys.length];
		if(json.isJsonObject()){
			JsonObject jo = json.getAsJsonObject();
			rets = new String[keys.length];
			for(Entry<String,JsonElement> ent:jo.entrySet()){
				JsonElement val = ent.getValue();
				boolean flag = false;
				for(int i=0;i < keys.length;i++){
					if(keys[i].equals(ent.getKey())){
						String s = val.toString();
						if(val.isJsonPrimitive() && val.getAsJsonPrimitive().isString()){
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
		if(json.isJsonArray()){
			JsonArray ja = json.getAsJsonArray();
			for(JsonElement je : ja){
				ret.addAll(getListString(je,keys));
			}
			return ret;
		}
		return ret;
	}
	public ArrayList<String[]> getListString(String[] keys) throws Exception {
		return Mson.getListString(json, keys);
	}
	/*
	 *
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
		mson4 = Mson.parse("{\"key2\":[\"string\",\"string\"],\"key\":\"string\"}");
		System.out.println("4_1>"+mson4);
		Mson mson5 = Mson.parse("{\"fullscreen\":false,\"ÉXÉNÉäÅ[Éìïù\":1200,\"screen_height\":900,\"antialias\":false,\"port\":39390,\"max_script_execution_time\":5000,\"max_local_storage_size\":512000,\"upnp\":false,\"udp_port\":39391,\"language\":\"jp\",\"lobby_servers\":[\"m2op.net\"]}}");
		System.out.println("4_2>"+mson5);
		System.out.println("End");
	}
	 */
}
