package saccubus.conv;

import org.xml.sax.Attributes;

public class ChatAttribute {
	final String Q_DATE = "date";
	final String Q_USERID = "user_id";
	final String Q_NO = "no";
	final String Q_DATE_USEC = "date_usec";

	private String key = "";	// date,user_id,no ‚Ü‚½‚Í date,user_id,date_usec
	private String attributeStr = "";

	public String getValue() { return attributeStr; }

	public ChatAttribute(Attributes attributes){
		String date = "";
		String userid = "";
		String no = "";
		if (attributes!=null) {
			try {
				date = attributes.getValue(Q_DATE);
			} catch(Exception e){
				date = "error1";
			}
			try {
				userid = attributes.getValue(Q_USERID);
			} catch(Exception e){
				userid = "error2";
			}
			try {
				no = attributes.getValue(Q_NO);
				if (no == null || no.isEmpty() || no.equals("-1")) {
					no = attributes.getValue(Q_DATE_USEC);
				}
			} catch(Exception e) {
				no = "0";
			}
			key = date + userid + no;
			try {
				attributeStr = toAttributeString(attributes);
			} catch(Exception e){
				// attribureStr = "";
			}
		}
	}

	private static String toAttributeString(Attributes attributes){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i <attributes.getLength(); i++) {
			sb.append(attributes.getQName(i));
			sb.append("=\"");
			sb.append(ChatSave.safeReference(attributes.getValue(i)));
			sb.append("\" ");
		}
		return sb.substring(0).trim();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ChatAttribute))
			return false;
		return key.equals(((ChatAttribute)obj).key);
	}

	@Override
	public int hashCode() {
		return key.hashCode();
	}
}
