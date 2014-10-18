package com.letv.watchball.bean;

/**
 * 原创节目视频
 * 
 * @author liuhanzhi
 * 
 */
public class OriginalVideo extends Base {
	public Body body = new Body();

	public class Body {
		public int total = 0;
		public Video[] videos = new Video[]{};

		public class Video {
			/**
			 * 视频id
			 */
			public String vid;
			/**
			 * 视频pid
			 */
			public String pid;
			/**
			 * 视频名称
			 */
			public String name;
			/**
			 * 视频封面图
			 */
			public String img_url;
			/**
			 * 发布日期
			 */
			public String release_date;


		}
	}
}
