package saccubus.conv;

import org.xml.sax.Attributes;

public class ChatAttribute {
	final String Q_DATE = "date";
	final String Q_USERID = "user_id";
	final String Q_NO = "no";
	final String Q_DATE_USEC = "date_usec";

	private String date = null;
	private String userid = null;
	private String no = null;
	private String attributeStr = null;

	public String getValue() { return attributeStr; }

	public ChatAttribute(Attributes attributes){
		if (attributes!=null) {
			date = attributes.getValue(Q_DATE);
			userid = attributes.getValue(Q_USERID);
			no = attributes.getValue(Q_NO);
			if (no == null || no.equals("-1")) {
				no = attributes.getValue(Q_DATE_USEC);
			}
			attributeStr = toAttributeString(attributes);
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
		ChatAttribute attr = (ChatAttribute)obj;
		if (this == attr)
			return true;
		if(hashCode()!=attr.hashCode())
			return false;
		if (date == null){
			if (attr.date != null)
				return false;
		}
		else if(!date.equals(attr.date))
			return false;
		if (userid == null) {
			if (attr.userid != null)
				return false;
		}
		else if (!userid.equals(attr.userid))
			return false;
		if (no == null) {
			if (attr.no != null)
				return false;
		}
		else if (!no.equals(attr.no))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		if (date==null)
			return 0;
		return date.hashCode();
	}
}
