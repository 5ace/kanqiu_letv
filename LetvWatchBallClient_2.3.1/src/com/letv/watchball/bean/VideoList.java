package com.letv.watchball.bean;

import java.util.ArrayList;

import com.letv.http.bean.LetvBaseBean;

/**
 * 视频列表
 * */
public class VideoList extends ArrayList<Video> implements LetvBaseBean{

	private static final long serialVersionUID = 1L;
	
	private int pagenum ;
	
	private int videoPosition ;

	public int getPagenum() {
		return pagenum;
	}

	public void setPagenum(int pagenum) {
		this.pagenum = pagenum;
	}

	public int getVideoPosition() {
		return videoPosition;
	}

	public void setVideoPosition(int videoPosition) {
		this.videoPosition = videoPosition;
	}
	
	
}
