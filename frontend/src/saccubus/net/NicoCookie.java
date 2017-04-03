package saccubus.net;

class NicoCookie {

	//Set-Cookie
	// 1 https secure cookie for login (about one month)
	// 2 nicosid cookie (long life)
	// 3 user_session cookie (about one month)
	// 4 delete user_session cookie, shall immediately expire (no life)
	// 5 other
	private String secure_cookie;
	private String normal_cookie;
	private String session_cookie;
	private String delete_cookie;
	private String other_cookie;
	private String ret_cookie = "";

	String get(String url){
		ret_cookie = "";
		if(url.contains("https"))
			add(secure_cookie);
		add(normal_cookie);
		add(session_cookie);
		add(other_cookie);
		return ret_cookie;
	}
	void setSecureCookie(String str){
		secure_cookie = str;
	}
	void setNormalCookie(String str){
		normal_cookie = str;
	}
	void setSessionCookie(String str){
		session_cookie = str;
	}
	void setDeleteCookie(String str){
		delete_cookie = str;
	}
	void setOtherCookie(String str){
		other_cookie = str;
	}
	void add(String str){
		if(str!=null && !str.isEmpty()){
			if(!ret_cookie.isEmpty()){
				ret_cookie += "; ";
			}
			ret_cookie += str;
		}
	}
	boolean isEmpty(){
		if(secure_cookie==null
		&& normal_cookie==null
		&& session_cookie==null
		&& delete_cookie==null
		&& other_cookie==null)
			return true;
		return false;
	}
	public String toString(){
		return get("https://www.nicovideo.jp/");
	}
	public void update(NicoCookie new_cookie) {
		if(new_cookie==null) return;
		if(new_cookie.secure_cookie!=null && !new_cookie.secure_cookie.isEmpty())
			secure_cookie = new_cookie.secure_cookie;
		if(new_cookie.normal_cookie!=null && !new_cookie.normal_cookie.isEmpty())
			normal_cookie = new_cookie.normal_cookie;
		if(new_cookie.session_cookie!=null && !new_cookie.session_cookie.isEmpty())
			session_cookie = new_cookie.session_cookie;
		if(new_cookie.delete_cookie!=null && !new_cookie.delete_cookie.isEmpty())
			delete_cookie = new_cookie.delete_cookie;
		if(new_cookie.other_cookie!=null && !new_cookie.other_cookie.isEmpty())
			other_cookie = new_cookie.other_cookie;
	}
	public void setSession(String session) {
		setSessionCookie(session);
	}
	public void addNormalCookie(String string) {
		if(normal_cookie==null || normal_cookie.isEmpty())
			normal_cookie = string;
		else if(!normal_cookie.contains(string))
			normal_cookie += "; " + string;
	}
	String getUsersession() {
		if(session_cookie==null)
				return "";
		int index = session_cookie.indexOf("user_session_");
		if(index < 0) return "";
		return session_cookie.substring(index);
	}
}