package com.letv.watchball.bean;

import com.letv.http.bean.LetvBaseBean;

public class PushAdImage implements LetvBaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String pic1;
	private long createTime;
	private long mTime;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPic1() {
		return pic1;
	}
	public void setPic1(String pic1) {
		this.pic1 = pic1;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public long getmTime() {
		return mTime;
	}
	public void setmTime(long mTime) {
		this.mTime = mTime;
	}

      public boolean playPlatform ;

      public boolean getPlayPlatform() {
            return playPlatform;
      }

      public void setPlayPlatform(boolean playPlatform) {
            this.playPlatform = playPlatform;
      }
}
