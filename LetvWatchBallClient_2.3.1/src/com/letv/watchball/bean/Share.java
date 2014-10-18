package com.letv.watchball.bean;

import com.letv.http.bean.LetvBaseBean;

/**
 * 解析： {@link ShareLinkParse}
 * */
public class Share implements LetvBaseBean {
	private String album_url;// 专辑链接替换规则
	private String video_url;// 视频链接替换规则

	private String albumEnd;
	private String videoEnd;
	public String getAlbum_url() {
		return album_url;
	}

	public void setAlbum_url(String album_url) {
		this.album_url = album_url;
	}

	public String getVideo_url() {
		return video_url;
	}

	public void setVideo_url(String video_url) {
		this.video_url = video_url;
	}

	public String getAlbumEnd() {
		return albumEnd;
	}

	public void setAlbumEnd(String albumEnd) {
		this.albumEnd = albumEnd;
	}

	public String getVideoEnd() {
		return videoEnd;
	}

	public void setVideoEnd(String videoEnd) {
		this.videoEnd = videoEnd;
	}

}
