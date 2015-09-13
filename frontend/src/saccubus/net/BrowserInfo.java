/**
 * Inspired from Nicorank by rankingloid 2008 - 2009
 */
package saccubus.net;

import java.io.File;

import saccubus.ConvertingSetting;

/**
 * <p>
 * �^�C�g��: ������΂�
 * </p>
 *
 * <p>
 * ����: �j�R�j�R����̓�����R�����g���ŕۑ�
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
				return "������΂�";
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

	private BrowserCookieKind validBrowser;
/*
	public String getBrowserName(){
		if (validBrowser == BrowserCookieKind.NONE){
			return "������΂�";
		} else if (validBrowser == BrowserCookieKind.MSIE) {
			return "Internet Exploror";
		} else {
			return validBrowser.toString();
		}
	}
*/
	public static final BrowserCookieKind[] ALL_BROWSER = BrowserCookieKind.values();
	public static final int NUM_BROWSER = ALL_BROWSER.length;

	public BrowserInfo(){
		validBrowser = BrowserCookieKind.NONE;
	}

	//private static final String NICOVIDEO_URL = "http://www.nicovideo.jp";

	/**
	 * get valid user session & set valid browser
	 * @param setting : ConvertingSetting
	 * @return user_session : String
	 */
	public String getUserSession(ConvertingSetting setting){
		String user_session = "";
		if(setting == null)
			return user_session;
		for(BrowserCookieKind browser: BrowserInfo.ALL_BROWSER){
			if(setting.isBrowser(browser)){
				validBrowser = browser;
				if (browser == BrowserCookieKind.NONE)
					continue;
				if (browser == BrowserCookieKind.Other){
					user_session = getUserSessionOther(setting.getBrowserCookiePath());
					if(!user_session.isEmpty())
						break;
				}else{
					user_session = getUserSession(browser);
					if(!user_session.isEmpty())
						break;
				}
			}
		}
		return user_session;
	}
	public BrowserCookieKind getValidBrowser(){
		return validBrowser;
	}
	/**
	 *
	 * @param browserKind
	 * @return
	 */
	public String getUserSession(BrowserCookieKind browserKind) {
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
        if (!user_session.isEmpty()){
        	validBrowser = browserKind;
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
	    	e.printStackTrace();
	        return user_session;
	    } finally {
            if (!user_session.isEmpty()){
            	validBrowser = BrowserCookieKind.Other;
            }
	    }
	}

    /// <summary>
    /// Firefox3 ���� user_session ���擾�B�G���[���N�������ꍇ�A��O�𓊂����ɋ󕶎���Ԃ�
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
        	e.printStackTrace();
        }
    	return user_session;
    }

    /// <summary>
    /// Firefox4, 5, 6 ���� user_session ���擾�B�G���[���N�������ꍇ�A��O�𓊂����ɋ󕶎���Ԃ�
    /// </summary>
    /// <returns>user_session</returns>
    private String getUserSessionFromFilefox4()
    {
        String user_session = "";
        StringBuffer sb = new StringBuffer();
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
       	            	sb.append(user_session+" ");
                    }
                    // else continue
                }
            }
            user_session = sb.substring(0).trim();
            return user_session;
        }
        catch (Exception e) {
        	e.printStackTrace();
        	return "";
        }
    }
/*
    /// <summary>
    /// IE6 ���� user_session ���擾
    /// </summary>
    /// <param name="url">�T�C�g�i�j�R�j�R����j��URL</param>
    /// <returns>user_session</returns>
    private String getUserSessionFromIE6(String url)
    {
        return cutUserSession(getCookieFromIE6(url), "");
    }

    /// <summary>
    /// IE6 ����N�b�L�[���擾
    /// </summary>
    /// <param name="url">�擾����N�b�L�[�Ɋ֘A�Â���ꂽURL</param>
    /// <returns>�N�b�L�[������</returns>
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
     *  IE7/IE8/IE9/IE10/IE11 (Win8.1�Ή�) ���� user_session ���擾�B<br/>
     *  �G���[���N�������ꍇ�A��O�𓊂����ɋ󕶎���Ԃ�
     *  </p>
     *  @return user_session
     */
    private String getUserSessionFromMSIE()
    {
        String user_session = " ";
        StringBuffer sb = new StringBuffer();
        String profile_dir = null;
        final String WINDOWS_DIR = "\\Microsoft\\Windows";

        profile_dir = System.getenv("APPDATA");    // userfolder/appdata/Roaming
        if (profile_dir != null && !profile_dir.isEmpty()){
            user_session = getUserSessionFromMSIE(profile_dir + WINDOWS_DIR);
            if(!user_session.isEmpty()){
            	sb.append(user_session+" ");
            }
        }
        profile_dir = System.getenv("LOCALAPPDATA");    // userfolder/appdata/local
        if (profile_dir != null && !profile_dir.isEmpty()){
            user_session = getUserSessionFromMSIE(profile_dir + WINDOWS_DIR);
            if(!user_session.isEmpty()){
            	sb.append(user_session+" ");
            }
        }
        profile_dir = System.getenv("PROFILE");    // userfolder
        if (profile_dir != null && !profile_dir.isEmpty()){
            user_session = getUserSessionFromMSIE(profile_dir);
            if(!user_session.isEmpty()){
            	sb.append(user_session+" ");
            }
        }
        profile_dir = System.getenv("USERPROFILE");    // userfolder
        if (profile_dir != null && !profile_dir.isEmpty()){
            user_session = getUserSessionFromMSIE(profile_dir);
            if(!user_session.isEmpty()){
            	sb.append(user_session+" ");
            }
        }
        user_session = sb.substring(0).trim();
        return user_session;
    }
    /**
     *  profile �t�H���_�����炢 cookie�t�H���_����ς��ĂQ�񌟍�
     *  @return user_session
     */
    private String getUserSessionFromMSIE(String folder)
    {
        final String COOKIE_DIR = "\\Cookies";
        final String COOKIE_DIR2 = "\\InetCookies";
        String user_session = "";
        StringBuilder sb1 = new StringBuilder();
        if(folder==null || folder.isEmpty())
        	return "";
        user_session = getUserSessionFromMSIE2(folder + COOKIE_DIR);
        if(!user_session.isEmpty()){
        	sb1.append(user_session+" ");
        }
        user_session = getUserSessionFromMSIE2(folder + COOKIE_DIR2);
        if(!user_session.isEmpty()){
        	sb1.append(user_session+" ");
        }
        user_session = sb1.substring(0).trim();
        return user_session;
    }
    /**
     *  Cookies �t�H���_�����炢 ���ʂ�cookie�t�H���_����ς��ĂQ�񌟍�
     *  @return user_session
     */
    private String getUserSessionFromMSIE2(String folder)
    {
    	String user_session = null;
        StringBuilder sb2 = new StringBuilder();
        user_session = getUserSessionFromDirectory(folder + "\\");
        if (!user_session.isEmpty())
        {
        	sb2.append(user_session+" ");
        }
        user_session = getUserSessionFromDirectory(folder + "\\Low\\");
        if (!user_session.isEmpty())
        {
        	sb2.append(user_session+" ");
        }
        user_session = sb2.substring(0).trim();
        return user_session;
    }

    /**
     * dir_name �f�B���N�g������ MSIE �̃N�b�L�[�������� user_session ��Ԃ�
     * @param dir_name
     * @return
     */
    private String getUserSessionFromDirectory(String dir_name)
    {
        String user_session = "";
        StringBuffer sb = new StringBuffer();
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
                    	sb.append(user_session+" ");
                    }

                    /*	Obsolete after WindowsUpdate Aug 2011
                    String name = Path.GetFileName(fullname);
                    if (name.indexOf("nicovideo") >= 0 && name.indexOf("www") < 0)
                    {
                        user_session = CutUserSession(Path.ReadAllText(fullname, "MS932"), "");
                        if (!user_session.isEmpty()){
                        	System.out.println("Found cookie in " + fullname.replace("\\", "/"));
                        	return user_session;
                        }
                    }
                    */
                }
                user_session = sb.substring(0).trim();
                return user_session;
            }
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        return "";
    }

    /** <p>
     *  Chrome ���� user_session ���擾�B�G���[���N�������ꍇ�A��O�𓊂����ɋ󕶎���Ԃ�
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
			e.printStackTrace();
			return user_session;
		}
	}

    /** <p>
     *  Chromium ���� user_session ���擾�B�G���[���N�������ꍇ�A��O�𓊂����ɋ󕶎���Ԃ�
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
        	e.printStackTrace();
	        return user_session;
        }
    }

    /** <p>
     *  Opera ���� user_session ���擾�B�G���[���N�������ꍇ�A��O�𓊂����ɋ󕶎���Ԃ�
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
        	e.printStackTrace();
	        return "";
        }
    }

    /// <summary>
    /// �����񂩂� user_session_ �Ŏn�܂镶�����؂�o���ĕԂ��B�p�����ƃA���_�[�o�[�ȊO�̕����Ő؂��B
    /// </summary>
    /// <param name="str">�؂�o���Ώە�����</param>
    /// <returns>user_session ������B������Ȃ���΋󕶎���Ԃ�</returns>
    private String cutUserSession(String str, String filename)
    {
    	String ret = "";
    	StringBuffer sb = new StringBuffer("");
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
            // C# �� string.SubString( , ) �� Java �� String.substring( , ) �͈Ⴄ�̂Œ��ӁI
            start = str.indexOf("user_session_", index);
            if (!ret.isEmpty() && !filename.isEmpty()){
            	System.out.println("Cookie found: " + filename);
                sb.append(ret + " ");
            }
        }
        return sb.toString();
    }

}

