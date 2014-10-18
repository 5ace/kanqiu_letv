package com.letv.watchball.bean;

import com.letv.http.bean.LetvBaseBean;

/**
 * 视频对象
 * 解析：{@link EpisodeParse}
 * */
public class Episode implements LetvBaseBean{
	
	/**
	 * 视频名称
	 * */
	private String title ;
	
	/**
	 * 上映时间，(支持格式：2011 | 2011-06 | 2011-06-13)
	 * */
	private String releasedate ;
	
	/**
	 * 视频类型：1,正片 2,预告片 3,花絮 4,资讯 5,其他
	 * */
	private int videotype ;
	
	/**
	 * 视频id
	 * */
	private int vid ;
	
	/**
	 * 媒体资源id
	 * */
	private int mmsid ;
	
	/**
	 * 该视频支持的码率，多个值使用半角逗号","分隔；码率取值：350,1000,1300
	 * */
	private String brList ;
	
	/**
	 * 该视频是否允许下载：1-表示允许，2-表示不允许
	 * */
	private int allownDownload ;
	
	/**
	 * 视频片头位置，单位（秒）
	 * */
	private long btime ;
	
	/**
	 * 视频片尾位置，单位（秒）
	 * */
	private long etime ;
	
	/**
	 * 视频时长，单位（秒）
	 * */
	private long duration ;
	
	/**
	 * 视频图片地址
	 * */
	private String icon ;
	
	/**
	 * 是否需要付费：1-不需要，2-需要
	 */
	private int pay = 1;
	
	public int getPay() {
		return pay;
	}

	public void setPay(int pay) {
		this.pay = pay;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getReleasedate() {
		return releasedate;
	}

	public void setReleasedate(String releasedate) {
		this.releasedate = releasedate;
	}

	public int getVideotype() {
		return videotype;
	}

	public void setVideotype(int videotype) {
		this.videotype = videotype;
	}

	public int getVid() {
		return vid;
	}

	public void setVid(int vid) {
		this.vid = vid;
	}

	public int getMmsid() {
		return mmsid;
	}

	public void setMmsid(int mmsid) {
		this.mmsid = mmsid;
	}

	public String getBrList() {
		return brList;
	}

	public void setBrList(String brList) {
		this.brList = brList;
	}

	public int getAllownDownload() {
		return allownDownload;
	}

	public void setAllownDownload(int allownDownload) {
		this.allownDownload = allownDownload;
	}

	public long getBtime() {
		return btime;
	}

	public void setBtime(long btime) {
		this.btime = btime;
	}

	public long getEtime() {
		return etime;
	}

	public void setEtime(long etime) {
		this.etime = etime;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
}
