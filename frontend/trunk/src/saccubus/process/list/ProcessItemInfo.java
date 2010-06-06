/**
 * �ϊ����Ɏg���ꎞ�I�Ȑݒ���B
 */
package saccubus.process.list;

import saccubus.info.RootInfo;

/**
 * @author PSI
 *
 */
public class ProcessItemInfo {
	/**
	 * ���̃A�C�e����ϊ�����Ƃ��Ɏg�p����ݒ���
	 */
	private RootInfo Info;
	/**
	 * �����ID�Bsm****�Aca*****�݂����Ȍ`���B
	 */
	private String VideoID;
	/**
	 * �ߋ����O�@�\��p����ꍇ�͂�����B�g��Ȃ��Ȃ�-1�ɐݒ肳���B
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
	 * �ȉ�Getter/Setter
	 * ���I�ɐݒ�������������Ă�������������Ȃ��B
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
	 * @param info �ݒ肷�� info
	 */
	public void setInfo(RootInfo info) {
		Info = info;
	}
	/**
	 * @param videoID �ݒ肷�� videoID
	 */
	public void setVideoID(String videoID) {
		VideoID = videoID;
	}
	/**
	 * @param wayback �ݒ肷�� wayback
	 */
	public void setWayback(int wayback) {
		Wayback = wayback;
	}

}
