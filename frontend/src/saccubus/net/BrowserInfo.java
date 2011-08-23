/**
 * Inspired from Nicorank by rankingloid 2008 - 2009
 */
package saccubus.net;

import java.util.Arrays;

/**
/**
 * <p>
 * タイトル: さきゅばす
 * </p>
 *
 * <p>
 * 説明: ニコニコ動画の動画をコメントつきで保存
 * </p>
 *
 * @version 1.22r3e
 * @author orz
 *
 */
public class BrowserInfo {

	public BrowserInfo(){

	}

	public enum BrowserCookieKind {
		NONE, IE9, IE7, IE6, Firefox3, FireFox4, Chrome,
	}

	private static final String NICOVIDEO_URL = "http://www.nicovideo.jp";

	/**
	 *
	 * @param browserKind
	 * @return
	 */
	public String getUserSession(BrowserCookieKind browserKind) {
        String user_session = "";
        switch (browserKind)
        {
            case IE6:
                user_session = GetUserSessionFromIE6(NICOVIDEO_URL);
                break;
            case IE7:
            case IE9:
                user_session = GetUserSessionFromIE7();
                break;
            case FireFox4:
                user_session = GetUserSessionFromFilefox4();
                if (!user_session.isEmpty()){
                	return user_session;
                }
            case Firefox3:
                user_session = GetUserSessionFromFilefox3();
                break;
            case Chrome:
            	user_session = GetUserSesionChrome();
            	break;
        }
        return user_session;
    }

    /// <summary>
    /// Firefox3 から user_session を取得。エラーが起こった場合、例外を投げずに空文字を返す
    /// </summary>
    /// <returns>user_session</returns>
    private String GetUserSessionFromFilefox3()
    {
        String user_session = "";
        try
        {
            String app_dir = System.getenv("APPDATA");
            if (app_dir == null || app_dir.isEmpty()){
            	return "";
            }
            app_dir += "\\Mozilla\\Firefox\\Profiles\\";
            String sqlist_filename = Path.SearchFile("cookies.sqlite", app_dir);
            //if (sqlist_filename.isEmpty() || !Path.Exists(sqlist_filename))
            if (!Path.isFile(sqlist_filename))
            {
                return "";
            }
            //FileStream fs = new FileStream(sqlist_filename, FileMode.Open, FileAccess.Read, FileShare.ReadWrite);
            String dataStr = Path.ReadAllText(sqlist_filename, "US-ASCII");
            user_session = CutUserSession(dataStr);
            if (!user_session.isEmpty()){
            	System.out.println("Found cookie in " + sqlist_filename.replace("\\", "/"));
            }
        	return user_session;
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
    	return user_session;
    }

    /// <summary>
    /// Firefox4, 5 から user_session を取得。エラーが起こった場合、例外を投げずに空文字を返す
    /// </summary>
    /// <returns>user_session</returns>
    private String GetUserSessionFromFilefox4()
    {
        String user_session = "";
        try
        {
            String app_dir = System.getenv("APPDATA");
            if (app_dir == null || app_dir.isEmpty()){
            	return "";
            }
            app_dir += "\\Mozilla\\Firefox\\Profiles\\";
            String[] userLists = Path.GetFiles(app_dir);
            for (String user_dir : userLists){
            	String sqlist_filename = Path.search(user_dir, "cookies.sqlite");
                if (Path.isFile(sqlist_filename))
                {
                    String dataStr = Path.ReadAllText(sqlist_filename, "US-ASCII");
                    user_session = CutUserSession(dataStr);
                    if (!user_session.isEmpty()){
                    	System.out.println("Found cookie in " + sqlist_filename.replace("\\", "/"));
                    	return user_session;
                    }
                }
            }
            return "";	// not found
        }
        catch (Exception e) {
        	e.printStackTrace();
        	return "";
        }
    }

    /// <summary>
    /// IE6 から user_session を取得
    /// </summary>
    /// <param name="url">サイト（ニコニコ動画）のURL</param>
    /// <returns>user_session</returns>
    private String GetUserSessionFromIE6(String url)
    {
        return CutUserSession(GetCookieFromIE6(url));
    }

    /// <summary>
    /// IE6 からクッキーを取得
    /// </summary>
    /// <param name="url">取得するクッキーに関連づけられたURL</param>
    /// <returns>クッキー文字列</returns>
    private String GetCookieFromIE6(String url)
    {
        int size = 4096;
        byte[] dummy = new byte[size];
        Arrays.fill(dummy, (byte)' ');
        StringBuilder buff = new StringBuilder(new String(dummy));
        int[] ref_size = new int[1];
        ref_size[0] = size;
        //InternetGetCookie(url, null, buff, /*ref*/ ref_size);
        return buff.toString().replace(';', ',');
    }
/*
 *  [DllImport("wininet.dll")]
 *  private extern static bool InternetGetCookie(string url, string name, StringBuilder data, ref uint size);
 *
 *  shuold use NLink.win32
 */

    /** <p>
     *  IE7 から user_session を取得。エラーが起こった場合、例外を投げずに空文字を返す<br/>
     *  IE8, IE9も同じ
     *  </p>
     *  @return user_session
     */
    private String GetUserSessionFromIE7()
    {
        String user_session = "";

        String profile_dir = System.getenv("USERPROFILE");
        if (profile_dir == null || profile_dir.isEmpty()){
        	return "";
        }
        String search_dir = profile_dir + "\\AppData\\Roaming\\Microsoft\\Windows\\Cookies\\Low\\";
        user_session = GetUserSessionFromDirectory(search_dir);
        if (user_session == "")
        {
        	search_dir = profile_dir + "\\AppData\\Roaming\\Microsoft\\Windows\\Cookies\\";
            user_session = GetUserSessionFromDirectory(search_dir);
        }
        if (user_session == "")
        {
        	search_dir = profile_dir + "\\Cookies\\";
            user_session = GetUserSessionFromDirectory(search_dir);
        }
        return user_session;
    }

    /** <p>
     *  Chrome から user_session を取得。エラーが起こった場合、例外を投げずに空文字を返す<br/>
     *  </p>
     *  @return user_session
     */
    private String GetUserSesionChrome()
    {
    	String user_session = "";
    	String cookie_file = "";
        try {
	        String local_Appdir = System.getenv("LOCALAPPDATA");
	        if (local_Appdir != null && !local_Appdir.isEmpty()){
	        	// Win7 32bit
	        	cookie_file = local_Appdir + "\\Google\\Chrome\\User Data\\Default\\Cookies";
	        	if (Path.isFile(cookie_file)){
		            String dataStr = Path.ReadAllText(cookie_file, "UTF-8");
		            user_session = CutUserSession(dataStr);
	        	}
	        }
	        String profile_dir = System.getenv("USERPROFILE");
	        if (profile_dir != null && !profile_dir.isEmpty()){
	        	// XP 32bit
	        	cookie_file = profile_dir
	        		+ "\\Local Settings\\Application Data\\Google\\Chrome\\User Data\\Default\\Cookies";
	        	if (Path.isFile(cookie_file)){
		            String dataStr = Path.ReadAllText(cookie_file, "UTF-8");
		            user_session = CutUserSession(dataStr);
	        	}
	        }
	        String app_dir = System.getenv("APPDATA");
	        if (app_dir != null && !app_dir.isEmpty()){
	        	// ??? just try
	        	cookie_file = app_dir + "\\Google\\Chrome\\User Data\\Default\\Cookies";
	        	if (Path.isFile(cookie_file)){
		            String dataStr = Path.ReadAllText(cookie_file, "UTF-8");
		            user_session = CutUserSession(dataStr);
	        	}
	        }
	        return user_session;
        } catch(Exception e){
        	e.printStackTrace();
	        return user_session;
        } finally {
            if (!user_session.isEmpty()){
            	System.out.println("Found cookie in " + cookie_file.replace("\\", "/"));
            }
        }
    }

    private String GetUserSessionFromDirectory(String dir_name)
    {
        String user_session = "";
        if (Path.Exists(dir_name))
        {
            try {
                String[] files = Path.GetFiles(dir_name);
                for (String fullname : files)
                {
                    String name = Path.GetFileName(fullname);
                    if (name.indexOf("nicovideo") >= 0 && name.indexOf("www") < 0)
                    {
                        user_session = CutUserSession(Path.ReadAllText(fullname, "MS932"));
                        if (!user_session.isEmpty()){
                        	System.out.println("Found cookie in " + fullname.replace("\\", "/"));
                        	return user_session;
                        }
                    }
                }
            }
            catch (Exception e) {
            	e.printStackTrace();
            }
        }
        return user_session;
    }

    /// <summary>
    /// 文字列から user_session_ で始まる文字列を切り出して返す。数字とアンダーバー以外の文字で切れる。
    /// </summary>
    /// <param name="str">切り出す対象文字列</param>
    /// <returns>user_session 文字列。見つからなければ空文字を返す</returns>
    private String CutUserSession(String str)
    {
        int start = str.indexOf("user_session_");
        if (start >= 0)
        {
            int index = start + "user_session_".length();
            while (index < str.length() && ('0' <= str.charAt(index) && str.charAt(index) <= '9'
            	|| str.charAt(index) == '_'))
            {
                ++index;
            }
            return str.substring(start, index);
            // C# string.SubString( , ) DIFFER FROM Java String.substring( , )   CAREFULL!!
        }
        return "";
    }

}

