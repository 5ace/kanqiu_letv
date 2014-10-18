package com.letv.watchball.bean;


/**
 * 视频新闻刷选类型
 * @author liuhanzhi
 *
 */
public class VideoNews {

	public Body[] body;
	public class Body{
		/**
		 * 赛事名称
		 */
		public String name;
		/**
		 * 赛事id
		 */
		public String type;
	}
}
