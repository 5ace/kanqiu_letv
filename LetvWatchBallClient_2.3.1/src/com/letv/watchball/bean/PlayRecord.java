package com.letv.watchball.bean;

import com.letv.http.bean.LetvBaseBean;

/**
 * 保存播放记录实体,用来保存播放记录
 * 
 */
public class PlayRecord implements LetvBaseBean {

	/**
	 * 频道ID
	 * */
	private int channelId;

	/**
	 * 专辑ID
	 * */
	private int albumId;

	/**
	 * 视频ID
	 * */
	private int videoId;

	/**
	 * 下一集视频ID
	 * */
	private int videoNextId;

	/**
	 * 用户ID
	 * */
	private String userId;

	/**
	 * 来源 1:web;2:mobile;3:pad;4:tv;5:pc桌面
	 * */
	private int from;

	/**
	 * 视频类型
	 * */
	private int videoType;

	/**
	 * 视频总长度
	 * */
	private long totalDuration;

	/**
	 * 视频播放时间点,单位:s
	 * */
	private long playedDuration;

	/**
	 * 视频最后更新时间点yyyy-mm-dd 00:00:00,单位:s
	 * */
	private long updateTime;

	/**
	 * 视频标题
	 * */
	private String title;

	/**
	 * 视频封面图片
	 * */
	private String img;

	/**
	 * 视频类型
	 * */
	private int type;

	/**
	 * 当前集数
	 */
	private int curEpsoid;

	/**
	 * 视频封面图片
	 * */
	private String img300;

	public enum PlayDeviceFrom {
		WEB(1), MOBILE(2), PAD(3), TV(4), PC(5);

		private int device;

		private PlayDeviceFrom(int device) {
			this.device = device;
		}

		public int getInt() {
			return device;
		}

		/**
		 * 根据From获取设备类型
		 * 
		 * @param messageId
		 * @return
		 */
		public static PlayDeviceFrom getDeviceFromById(int from) {
			for (PlayDeviceFrom type : PlayDeviceFrom.values()) {
				if (type.getInt() == from) {
					return type;
				}
			}
			return MOBILE;
		}
	}

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	public int getAlbumId() {
		return albumId;
	}

	public void setAlbumId(int albumId) {
		this.albumId = albumId;
	}

	public int getVideoId() {
		return videoId;
	}

	public void setVideoId(int videoId) {
		this.videoId = videoId;
	}

	public int getVideoNextId() {
		return videoNextId;
	}

	public void setVideoNextId(int videoNextId) {
		this.videoNextId = videoNextId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public PlayDeviceFrom getFrom() {
		return PlayDeviceFrom.getDeviceFromById(from);
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getVideoType() {
		return videoType;
	}

	public void setVideoType(int videoType) {
		this.videoType = videoType;
	}

	public long getTotalDuration() {
		return totalDuration;
	}

	public void setTotalDuration(long totalDuration) {
		this.totalDuration = totalDuration;
	}

	public long getPlayedDuration() {
		return playedDuration;
	}

	public void setPlayedDuration(long playedDuration) {
		this.playedDuration = playedDuration;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getCurEpsoid() {
		return curEpsoid;
	}

	public void setCurEpsoid(int curEpsoid) {
		this.curEpsoid = curEpsoid;
	}

	public String getImg300() {
		return img300;
	}

	public void setImg300(String img300) {
		this.img300 = img300;
	}

	@Override
	public String toString() {
		return "channelId=" + channelId;
//		return "PlayRecord [channelId=" + channelId + ", albumId=" + albumId + ", videoId=" + videoId + ", videoNextId=" + videoNextId + ", userId=" + userId
//				+ ", from=" + from + ", videoType=" + videoType + ", totalDuration=" + totalDuration + ", playedDuration=" + playedDuration + ", updateTime="
//				+ updateTime + ", title=" + title + ", img=" + img + ", type=" + type + ", curEpsoid=" + curEpsoid + "]";
	}

}
