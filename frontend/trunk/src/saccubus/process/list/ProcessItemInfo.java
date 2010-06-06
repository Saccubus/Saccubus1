/**
 * 変換時に使う一時的な設定情報。
 */
package saccubus.process.list;

import saccubus.info.RootInfo;

/**
 * @author PSI
 *
 */
public class ProcessItemInfo {
	/**
	 * このアイテムを変換するときに使用する設定情報
	 */
	private RootInfo Info;
	/**
	 * 動画のID。sm****、ca*****みたいな形式。
	 */
	private String VideoID;
	/**
	 * 過去ログ機能を用いる場合はこちら。使わないなら-1に設定される。
	 */
	private int Wayback;
	/**
	 * @param info
	 * @param videoID
	 * @param wayback
	 */
	public ProcessItemInfo(RootInfo info, String videoID, int wayback) {
		super();
		Info = info;
		VideoID = videoID;
		Wayback = wayback;
	}
	/**
	 * @param info
	 * @param videoID
	 */
	public ProcessItemInfo(RootInfo info, String videoID) {
		Info = info;
		VideoID = videoID;
		Wayback = -1;
	}
	/*
	 * 以下Getter/Setter
	 * 動的に設定を書き換えられてもいいかもしれない。
	 */
	/**
	 * @return info
	 */
	public RootInfo getInfo() {
		return Info;
	}
	/**
	 * @return videoID
	 */
	public String getVideoID() {
		return VideoID;
	}
	/**
	 * @return wayback
	 */
	public int getWayback() {
		return Wayback;
	}
	/**
	 * @param info 設定する info
	 */
	public void setInfo(RootInfo info) {
		Info = info;
	}
	/**
	 * @param videoID 設定する videoID
	 */
	public void setVideoID(String videoID) {
		VideoID = videoID;
	}
	/**
	 * @param wayback 設定する wayback
	 */
	public void setWayback(int wayback) {
		Wayback = wayback;
	}

}
