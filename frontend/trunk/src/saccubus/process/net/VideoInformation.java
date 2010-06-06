/**
 * 
 */
package saccubus.process.net;

/**
 * @author PSI
 *
 */
public class VideoInformation {
	private String VideoID;
	private String VideoURL;
	private String ThreadID;
	private String MessageURL;
	private String UserID;
	private int Deleted;
	private int VideoLength;
	private boolean isPremium;
	/**
	 * ìÆâÊèÓïÒÇê∂ê¨ÅB
	 * @param videoID
	 * @param videoURL
	 * @param threadID
	 * @param messageURL
	 * @param userID
	 * @param deleted
	 * @param videoLength
	 * @param isPremium
	 */
	protected VideoInformation(String videoID, String videoURL, String threadID, String messageURL, String userID, int deleted, int videoLength, boolean isPremium) {
		VideoID = videoID;
		VideoURL = videoURL;
		ThreadID = threadID;
		MessageURL = messageURL;
		UserID = userID;
		Deleted = deleted;
		VideoLength = videoLength;
		this.isPremium = isPremium;
	}
	/**
	 * @return deleted
	 */
	public int getDeleted() {
		return Deleted;
	}
	/**
	 * @return isPremium
	 */
	public boolean isPremium() {
		return isPremium;
	}
	/**
	 * @return messageURL
	 */
	public String getMessageURL() {
		return MessageURL;
	}
	/**
	 * @return threadID
	 */
	public String getThreadID() {
		return ThreadID;
	}
	/**
	 * @return userID
	 */
	public String getUserID() {
		return UserID;
	}
	/**
	 * @return videoID
	 */
	public String getVideoID() {
		return VideoID;
	}
	/**
	 * @return videoLength
	 */
	public int getVideoLength() {
		return VideoLength;
	}
	/**
	 * @return videoURL
	 */
	public String getVideoURL() {
		return VideoURL;
	}
}
