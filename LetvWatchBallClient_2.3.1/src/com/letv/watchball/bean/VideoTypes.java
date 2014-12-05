package com.letv.watchball.bean;

/**
 * @author Liuheyuan 视频新闻筛选字段
 */
public class VideoTypes extends Base {
	public Body body;

	public class Body {
		/**
		 * 最新最热
		 */
		public VideoBean[] sort;
		/**
		 * 筛选赛事
		 */
		public VideoBean[] filter;
	}

	public class VideoBean {
		public String name;
		public String id;
	}
}
