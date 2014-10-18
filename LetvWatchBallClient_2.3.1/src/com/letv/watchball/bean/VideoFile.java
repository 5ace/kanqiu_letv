package com.letv.watchball.bean;

import android.text.TextUtils;

import com.letv.http.bean.LetvBaseBean;

public class VideoFile implements LetvBaseBean{
	
	/**
	 * 资源id
	 * */
	private String mmsid ; 
	
	/**
	 * 标清350
	 * */
	private VideoSchedulingAddress mp4_350 ;
	
	/**
	 * 高清1000
	 * */
	private VideoSchedulingAddress mp4_1000 ;
	
	/**
	 * 超清1300
	 * */
	private VideoSchedulingAddress mp4_1300 ;
	
	/**
	 * 杜比800
	 */
	private VideoSchedulingAddress mp4_800_db;
	
	/**
	 * 杜比1300
	 */
	private VideoSchedulingAddress mp4_1300_db;
	
	/**
	 * 杜比720p
	 */
	private VideoSchedulingAddress mp4_720p_db;
	
	/**
	 * 杜比1080p
	 */
	private VideoSchedulingAddress mp4_1080p6m_db;
	
	public String getMmsid() {
		return mmsid;
	}

	public void setMmsid(String mmsid) {
		this.mmsid = mmsid;
	}
	
	public VideoSchedulingAddress getMp4_1080p6m_db() {
		return mp4_1080p6m_db;
	}

	public void setMp4_1080p6m_db(VideoSchedulingAddress mp4_1080p6m_db) {
		this.mp4_1080p6m_db = mp4_1080p6m_db;
	}

	public VideoSchedulingAddress getMp4_800_db() {
		return mp4_800_db;
	}

	public void setMp4_800_db(VideoSchedulingAddress mp4_800_db) {
		this.mp4_800_db = mp4_800_db;
	}

	public VideoSchedulingAddress getMp4_1300_db() {
		return mp4_1300_db;
	}

	public void setMp4_1300_db(VideoSchedulingAddress mp4_1300_db) {
		this.mp4_1300_db = mp4_1300_db;
	}

	public VideoSchedulingAddress getMp4_720p_db() {
		return mp4_720p_db;
	}

	public void setMp4_720p_db(VideoSchedulingAddress mp4_720p_db) {
		this.mp4_720p_db = mp4_720p_db;
	}

	public VideoSchedulingAddress getMp4_350() {
		return mp4_350;
	}

	public void setMp4_350(VideoSchedulingAddress mp4_350) {
		this.mp4_350 = mp4_350;
	}

	public VideoSchedulingAddress getMp4_1000() {
		return mp4_1000;
	}

	public void setMp4_1000(VideoSchedulingAddress mp4_1000) {
		this.mp4_1000 = mp4_1000;
	}

	public VideoSchedulingAddress getMp4_1300() {
		return mp4_1300;
	}

	public void setMp4_1300(VideoSchedulingAddress mp4_1300) {
		this.mp4_1300 = mp4_1300;
	}




	public static class VideoSchedulingAddress implements LetvBaseBean{
		
		/**
		 * 主调度地址
		 * */
		private String mainUrl ;
		/**
		 * 备用地址1
		 * */
		private String backUrl0 ;
		/**
		 * 备用地址2
		 * */
		private String backUrl1 ;
		/**
		 * 备用地址3
		 * */
		private String backUrl2 ;
		/**
		 * 文件大小
		 * */
		private long filesize ;

		public String getMainUrl() {
			if(TextUtils.isEmpty(mainUrl)){
				return null ;
			}
			return mainUrl;
		}

		public void setMainUrl(String mainUrl) {
			this.mainUrl = mainUrl;
		}

		public String getBackUrl0() {
			if(TextUtils.isEmpty(backUrl0)){
				return null ;
			}
			return backUrl0;
		}

		public void setBackUrl0(String backUrl0) {
			this.backUrl0 = backUrl0;
		}

		public String getBackUrl1() {
			if(TextUtils.isEmpty(backUrl1)){
				return null ;
			}
			return backUrl1;
		}

		public void setBackUrl1(String backUrl1) {
			this.backUrl1 = backUrl1;
		}

		public String getBackUrl2() {
			if(TextUtils.isEmpty(backUrl2)){
				return null ;
			}
			return backUrl2;
		}

		public void setBackUrl2(String backUrl2) {
			this.backUrl2 = backUrl2;
		}

		public long getFilesize() {
			return filesize;
		}

		public void setFilesize(long filesize) {
			this.filesize = filesize;
		}
	}
}
