package saccubus.util;

public class Bool {

	public static boolean parseBoolean(String s) {
		if ("1".equals(s))
			return true;
		if (s == null)
			return false;
		if ("yes".equals(s.toLowerCase()))
			return true;
		// if("0".equals(s))
		//	return false;
		// if("no".equals(s.toLowerCase()))
		//	return false;
		return Boolean.parseBoolean(s);
	}
}
