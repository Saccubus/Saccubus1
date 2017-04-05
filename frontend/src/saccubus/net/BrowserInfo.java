/**
 * Inspired from Nicorank by rankingloid 2008 - 2009
 */
package saccubus.net;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import saccubus.ConvertingSetting;
import saccubus.util.Logger;

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

	public enum BrowserCookieKind {
		NONE {
			@Override
			public String getName(){
				return "さきゅばす";
			}
		},
		MSIE {
			@Override
			public String getName(){
				return "Internet Exploror";
			}
		},
	//	IE6,
	//	Firefox3,
		Firefox,
		Chrome,
		Opera,
		Chromium,
		Other,;

		public String getName(){
			return name();
		}
	}

	private static BrowserCookieKind validBrowser = BrowserCookieKind.NONE;
	public String getName(){
		return validBrowser.getName();
	}
/*
	public String getBrowserName(){
		if (validBrowser == BrowserCookieKind.NONE){
			return "さきゅばす";
		} else if (validBrowser == BrowserCookieKind.MSIE) {
			return "Internet Exploror";
		} else {
			return validBrowser.toString();
		}
	}
*/
	public static final BrowserCookieKind[] ALL_BROWSER = BrowserCookieKind.values();
	public static final int NUM_BROWSER = ALL_BROWSER.length;
	private Logger log;
	private static Set<String> faultUserSessionSet = new ConcurrentSkipListSet<>();
	private static String last_user_session = "";
	private static String last_browser_value = "";

	public BrowserInfo(Logger logger){
		log = logger;
	}

	//private static final String NICOVIDEO_URL = "http://www.nicovideo.jp";

	/**
	 * get valid user session & set valid browser
	 * @param setting : ConvertingSetting
	 * @return user_session : String
	 */
	public synchronized void checkUserSession(ConvertingSetting setting){
		String browser_value = "";
		if(setting == null)
			return;
		last_browser_value = "";
		if(isBrowser(setting) && !isFalseSession(last_user_session)){
			log.println("Last user_session matched! "+last_user_session);
			last_browser_value = last_user_session;
			return;
		}
		last_user_session = "";
		for(BrowserCookieKind browser: BrowserInfo.ALL_BROWSER){
			if(setting.isBrowser(browser)){
				if (browser == BrowserCookieKind.NONE){
					continue;
				}
				if (browser == BrowserCookieKind.Other){
					browser_value = getUserSessionOther(setting.getBrowserCookiePath());
					if(!browser_value.isEmpty()){
						validBrowser = browser;
						last_browser_value = browser_value;
						return;
					}
				}else{
					browser_value = getUserSession(browser);
					if(!browser_value.isEmpty()){
						validBrowser = browser;
						last_browser_value = browser_value;
						return;
					}
				}
			}
		}
	}


	public static void resetLastUserSession() {
		last_user_session = "";
	}
	public static boolean isBrowser(ConvertingSetting setting) {
		return validBrowser != BrowserCookieKind.NONE && setting.isBrowser(validBrowser);
	}
	public static boolean isFalseSession(String s){
		return s==null || s.isEmpty()
			|| faultUserSessionSet.contains(s);
	}
	public boolean isValid(){
		return !last_browser_value.isEmpty();
	}
	public static String getLastUsersession() {
		return last_user_session;
	}
	String getLastBrowserValue(){
		if(last_user_session.isEmpty()){
			return last_browser_value;
		}
		return last_user_session;
	}
	static void setLastUsersession(String session){
		faultUserSessionSet.remove(session);
		last_user_session = session;
	}
	public static void resetBrowserInfo(){
		faultUserSessionSet.clear();
		last_user_session = "";
		last_browser_value = "";
		validBrowser = BrowserCookieKind.NONE;
	}
	public void addFaultUserSession(String session) {
		faultUserSessionSet.add(session);
	}

	/**
	 *
	 * @param browserKind
	 * @return
	 */
	private String getUserSession(BrowserCookieKind browserKind) {
        String user_session = "";
        switch (browserKind)
        {
         // case IE6:
         //     user_session = getUserSessionFromIE6(NICOVIDEO_URL);
         //     break;
            case MSIE:
                user_session = getUserSessionFromMSIE();
                break;
            case Firefox:
                user_session = getUserSessionFromFilefox4();
                if (!user_session.isEmpty()){
                	break;
                }
         // case Firefox3:
                user_session = getUserSessionFromFilefox3();
                break;
            case Chrome:
            	user_session = getUserSesionChrome();
            	break;
            case Chromium:
            	user_session = getUserSesionChromium();
            	break;
            case Opera:
            	user_session = getUserSessionOpera();
            	break;
            default:
            	break;
        }
        return user_session;
    }

	/**
	 *
	 * @param fileOrDir fullname of file or directory
	 * @return
	 */
	public String getUserSessionOther(String fileOrDir)
	{
		String user_session = "";
	    try {
	    	if (Path.isDirectory(fileOrDir)){
	    		// Directory Type like MSIE
	            user_session = getUserSessionFromDirectory(fileOrDir);
	        	return user_session;
	    	}
	    	if (Path.isFile(fileOrDir)){
	    		// File Type like Firefox3
	            String dataStr = Path.readAllText(fileOrDir, "UTF-8");
	            user_session = cutUserSession(dataStr, fileOrDir);
	            return user_session;
	    	}
	        return user_session;
	    } catch(Exception e){
	    	log.printStackTrace(e);
	        return user_session;
	    } finally {
            if (!user_session.isEmpty()){
            	validBrowser = BrowserCookieKind.Other;
            }
	    }
	}

    /// <summary>
    /// Firefox3 から user_session を取得。エラーが起こった場合、例外を投げずに空文字を返す
    /// </summary>
    /// <returns>user_session</returns>
    private String getUserSessionFromFilefox3()
    {
        String user_session = "";
        try
        {
            String app_dir = System.getenv("APPDATA");
            if (app_dir == null || app_dir.isEmpty()){
            	return "";
            }
            String sqlist_filename = app_dir + "\\Mozilla\\Firefox\\Profiles\\cookies.sqlite";
            if (!Path.isFile(sqlist_filename))
            {
                return "";
            }
            String dataStr = Path.readAllText(sqlist_filename, "US-ASCII");
            user_session = cutUserSession(dataStr, sqlist_filename);
        	return user_session;
        }
        catch (Exception e) {
        	log.printStackTrace(e);
        }
    	return user_session;
    }

    /// <summary>
    /// Firefox4, 5, 6 から user_session を取得。エラーが起こった場合、例外を投げずに空文字を返す
    /// </summary>
    /// <returns>user_session</returns>
    private String getUserSessionFromFilefox4()
    {
        String user_session = "";
    	Set<String> us = new LinkedHashSet<>();
        try
        {
            String app_dir = System.getenv("APPDATA");
            if (app_dir == null || app_dir.isEmpty()){
            	return "";
            }
            String[] userLists = Path.getFullnameList(app_dir + "\\Mozilla\\Firefox\\Profiles\\");
            if(userLists==null)
            	return "";
            for (String user_dir : userLists){
            	String sqlist_filename = user_dir + "\\cookies.sqlite";
                if (Path.isFile(sqlist_filename))
                {
                    String dataStr = Path.readAllText(sqlist_filename, "US-ASCII");
                    user_session = cutUserSession(dataStr, sqlist_filename);
                    if (!user_session.isEmpty()){
                    	us.add(user_session);
                    }
                    // else continue
                }
            }
            user_session = String.join(" ",us);
            return user_session;
        }
        catch (Exception e) {
        	log.printStackTrace(e);
        	return "";
        }
    }
/*
    /// <summary>
    /// IE6 から user_session を取得
    /// </summary>
    /// <param name="url">サイト（ニコニコ動画）のURL</param>
    /// <returns>user_session</returns>
    private String getUserSessionFromIE6(String url)
    {
        return cutUserSession(getCookieFromIE6(url), "");
    }

    /// <summary>
    /// IE6 からクッキーを取得
    /// </summary>
    /// <param name="url">取得するクッキーに関連づけられたURL</param>
    /// <returns>クッキー文字列</returns>
    private String getCookieFromIE6(String url)
    {
        int size = 4096;
        byte[] dummy = new byte[size];
        Arrays.fill(dummy, (byte)' ');
        StringBuilder buff = new StringBuilder(new String(dummy));
        int[] ref_size = new int[1];
        ref_size[0] = size;
        //InternetGetCookie(url, null, buff, /*ref / ref_size);
        return buff.toString().replace(';', ',');
    }
*/
/*
 *  [DllImport("wininet.dll")]
 *  private extern static bool InternetGetCookie(string url, string name, StringBuilder data, ref uint size);
 *
 *  shuold use NLink.win32
 */

    /** <p>
     *  IE7/IE8/IE9/IE10/IE11 (Win8.1対応) から user_session を取得。<br/>
     *  エラーが起こった場合、例外を投げずに空文字を返す
     *  </p>
     *  @return user_session
     */
    private String getUserSessionFromMSIE()
    {
        String user_session = " ";
        Set<String> us = new LinkedHashSet<>();
        String profile_dir = null;
        final String WINDOWS_DIR = "\\Microsoft\\Windows";

        profile_dir = System.getenv("APPDATA");    // userfolder/appdata/Roaming
        if (profile_dir != null && !profile_dir.isEmpty()){
            user_session = getUserSessionFromMSIE(profile_dir + WINDOWS_DIR);
            if(!user_session.isEmpty()){
            	us.add(user_session);
            }
        }
        profile_dir = System.getenv("LOCALAPPDATA");    // userfolder/appdata/local
        if (profile_dir != null && !profile_dir.isEmpty()){
            user_session = getUserSessionFromMSIE(profile_dir + WINDOWS_DIR);
            if(!user_session.isEmpty()){
            	us.add(user_session);
            }
        }
        profile_dir = System.getenv("PROFILE");    // userfolder
        if (profile_dir != null && !profile_dir.isEmpty()){
            user_session = getUserSessionFromMSIE(profile_dir);
            if(!user_session.isEmpty()){
            	us.add(user_session);
            }
        }
        profile_dir = System.getenv("USERPROFILE");    // userfolder
        if (profile_dir != null && !profile_dir.isEmpty()){
            user_session = getUserSessionFromMSIE(profile_dir);
            if(!user_session.isEmpty()){
            	us.add(user_session);
            }
        }
        user_session = String.join(" ",us);
        return user_session;
    }
    /**
     *  profile フォルダをもらい cookieフォルダ名を変えて２回検索
     *  @return user_session
     */
    private String getUserSessionFromMSIE(String folder)
    {
        final String COOKIE_DIR = "\\Cookies";
        final String COOKIE_DIR2 = "\\InetCookies";
        String user_session = "";
        Set<String> us = new LinkedHashSet<>();
        if(folder==null || folder.isEmpty())
        	return "";
        user_session = getUserSessionFromMSIE2(folder + COOKIE_DIR);
        if(!user_session.isEmpty()){
        	us.add(user_session);
        }
        user_session = getUserSessionFromMSIE2(folder + COOKIE_DIR2);
        if(!user_session.isEmpty()){
        	us.add(user_session);
        }
        user_session = String.join(" ",us);
        return user_session;
    }
    /**
     *  Cookies フォルダをもらい 下位のcookieフォルダ名を変えて２回検索
     *  @return user_session
     */
    private String getUserSessionFromMSIE2(String folder)
    {
    	String user_session = null;
        Set<String> us = new LinkedHashSet<>();
        user_session = getUserSessionFromDirectory(folder + "\\");
        if (!user_session.isEmpty())
        {
        	us.add(user_session);
        }
        user_session = getUserSessionFromDirectory(folder + "\\Low\\");
        if (!user_session.isEmpty())
        {
        	us.add(user_session);
        }
        user_session = String.join(" ", us);
        return user_session;
    }

    /**
     * dir_name ディレクトリから MSIE のクッキーを見つけて user_session を返す
     * @param dir_name
     * @return
     */
    private String getUserSessionFromDirectory(String dir_name)
    {
        String user_session = "";
        Set<String> us = new LinkedHashSet<>();
        try {
	        if (Path.isDirectory(dir_name))
	        {
                String[] files = Path.getFullnameList(dir_name);
                if(files==null)
                	return "";
                for (String fullname : files)
                {
                	File file = null;
                	try{
                		file = new File(fullname);
                	}catch(Exception e){
                		file = null;
                	}
                	if(file==null)
                		return "";
                    user_session = cutUserSession(Path.readAllText(fullname, "MS932"), fullname);
                    if (!user_session.isEmpty()){
                    	us.add(user_session);
                    }

                    /*	Obsolete after WindowsUpdate Aug 2011
                    String name = Path.GetFileName(fullname);
                    if (name.indexOf("nicovideo") >= 0 && name.indexOf("www") < 0)
                    {
                        user_session = CutUserSession(Path.ReadAllText(fullname, "MS932"), "");
                        if (!user_session.isEmpty()){
                        	log.println("Found cookie in " + fullname.replace("\\", "/"));
                        	return user_session;
                        }
                    }
                    */
                }
                user_session = String.join(" ", us);
                return user_session;
            }
        }
        catch (Exception e) {
        	log.printStackTrace(e);
        }
        return "";
    }

    /** <p>
     *  Chrome から user_session を取得。エラーが起こった場合、例外を投げずに空文字を返す
     *  </p>
     *  @return user_session
     */
	private String getUserSesionChrome()
	{
		String user_session = "";
		String cookie_file = "";
		String googleChrome = "\\Google\\Chrome\\User Data\\Default\\Cookies";
		try {
			String local_Appdir = System.getenv("LOCALAPPDATA");
			if (local_Appdir != null && !local_Appdir.isEmpty()){
				// Win7 32bit
				cookie_file = local_Appdir + googleChrome;
				if (Path.isFile(cookie_file)){
					String dataStr = Path.readAllText(cookie_file, "UTF-8");
					user_session = cutUserSession(dataStr, cookie_file);
					if (!user_session.isEmpty()){
						return user_session;
					}
				}
			}
			String profile_dir = System.getenv("USERPROFILE");
			if (profile_dir != null && !profile_dir.isEmpty()){
				// XP 32bit
				cookie_file = profile_dir
					+ "\\Local Settings\\Application Data" + googleChrome;
				if (Path.isFile(cookie_file)){
					String dataStr = Path.readAllText(cookie_file, "UTF-8");
					user_session = cutUserSession(dataStr, cookie_file);
					return user_session;
				}
			}
			String app_dir = System.getenv("APPDATA");
			if (app_dir != null && !app_dir.isEmpty()){
				// ??? just try
				cookie_file = app_dir + googleChrome;
				if (Path.isFile(cookie_file)){
					String dataStr = Path.readAllText(cookie_file, "UTF-8");
					user_session = cutUserSession(dataStr, cookie_file);
					return user_session;
				}
			}
			return user_session;
		} catch(Exception e){
			log.printStackTrace(e);
			return user_session;
		}
	}

    /** <p>
     *  Chromium から user_session を取得。エラーが起こった場合、例外を投げずに空文字を返す
     *  </p>
     *  @return user_session
     */
    private String getUserSesionChromium()
    {
    	String user_session = "";
    	String cookie_file = "";
    	String chromium = "\\Chromium\\User Data\\Default\\Cookies";
        try {
	        String local_Appdir = System.getenv("LOCALAPPDATA");
	        if (local_Appdir != null && !local_Appdir.isEmpty()){
	        	// Win7 32bit
	        	cookie_file = local_Appdir + chromium;
	        	if (Path.isFile(cookie_file)){
		            String dataStr = Path.readAllText(cookie_file, "UTF-8");
		            user_session = cutUserSession(dataStr, cookie_file);
		            return user_session;
	        	}
	        }
	        String profile_dir = System.getenv("USERPROFILE");
	        if (profile_dir != null && !profile_dir.isEmpty()){
	        	// XP 32bit
	        	cookie_file = profile_dir
	        		+ "\\Local Settings\\Application Data" + chromium;
	        	if (Path.isFile(cookie_file)){
		            String dataStr = Path.readAllText(cookie_file, "UTF-8");
		            user_session = cutUserSession(dataStr, cookie_file);
		            return user_session;
	        	}
	        }
	        return user_session;
        } catch(Exception e){
        	log.printStackTrace(e);
	        return user_session;
        }
    }

    /** <p>
     *  Opera から user_session を取得。エラーが起こった場合、例外を投げずに空文字を返す
     *  </p>
     *  @return user_session
     */
    private String getUserSessionOpera()
    {
    	String user_session = "";
    	String cookie_file = "";
        try {
	        String app_dir = System.getenv("APPDATA");
	        if (app_dir != null && !app_dir.isEmpty()){
	        	// Win7/XP 32bit
	        	cookie_file = app_dir + "\\Opera\\Opera\\cookies4.dat";
	        	if (Path.isFile(cookie_file)){
		            String dataStr = Path.readAllText(cookie_file, "UTF-8");
		            user_session = cutUserSession(dataStr, cookie_file);
	    	        return user_session;
	        	}
	        }
	        return "";
        } catch(Exception e){
        	log.printStackTrace(e);
	        return "";
        }
    }

    /// <summary>
    /// 文字列から user_session_ で始まる文字列を切り出して返す。英数字とアンダーバー以外の文字で切れる。
    /// </summary>
    /// <param name="str">切り出す対象文字列</param>
    /// <returns>user_session 文字列。見つからなければ空文字を返す</returns>
    private String cutUserSession(String str, String filename)
    {
    	String ret = "";
    	Set<String> us = new LinkedHashSet<>();
        int start = str.indexOf("user_session_");
        while (start >= 0)
        {
            int index = start + "user_session_".length();
            while (index < str.length() && (('0' <= str.charAt(index) && str.charAt(index) <= '9')
            	|| ('a' <= str.charAt(index) && str.charAt(index) <= 'z')
            	|| str.charAt(index) == '_'))
            {
                ++index;
            }
            ret = str.substring(start, index);
            // C# の string.SubString( , ) と Java の String.substring( , ) は違うので注意！
            start = str.indexOf("user_session_", index);
            if (!ret.isEmpty() && !filename.isEmpty()){
                if(!us.contains(ret) && !faultUserSessionSet.contains(ret)){
                	log.println("Cookie found: " + filename+" "+ret.substring(ret.length()- 4));
                    us.add(ret);
                }
            }
        }
        return String.join(" ", us);
    }
}

