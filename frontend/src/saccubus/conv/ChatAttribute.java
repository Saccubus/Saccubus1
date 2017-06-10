package saccubus.conv;

import org.xml.sax.Attributes;

public class ChatAttribute implements Comparable<ChatAttribute> {
	final String Q_DATE = "date";
	final String Q_USERID = "user_id";
	final String Q_NO = "no";
	final String Q_DATE_USEC = "date_usec";

	private String key = "";	// no,date,user_id,date_usec
	private String attributeStr = "";

	public String getValue() { return attributeStr; }

	public ChatAttribute(String qName, Attributes attributes) {
		if(qName.equals("chat")){
			String date = "";
			String userid = "";
			String no = "";
			String usec = "";
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
						no = "0";
						usec = attributes.getValue(Q_DATE_USEC);
					}
					else {
						usec = "";
					}
				} catch(Exception e) {
					no = "0";
				}
				no = String.format("%10s", no);
				key = no + date + userid + usec;
				try {
					attributeStr = toAttributeString(attributes);
				} catch(Exception e){
					// attribureStr = "";
				}
			}
		}else{
			try {
				attributeStr = toAttributeString(attributes);
			} catch(Exception e){
				// attribureStr = "";
			}
			key = "          " + qName + " " + attributeStr;
		}
	}

	private static String toAttributeString(Attributes attributes){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i <attributes.getLength(); i++) {
			sb.append(attributes.getQName(i));
			sb.append("=\"");
			sb.append(safeReference(attributes.getValue(i)));
			sb.append("\" ");
		}
		return sb.substring(0).trim();
	}

	public static String safeReference(String s){
		return ChatSave.safeReference(s).replace("\"", "&quote;");
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

	@Override
	public int compareTo(ChatAttribute o) {
		return key.compareTo(o.key);
	}
}
