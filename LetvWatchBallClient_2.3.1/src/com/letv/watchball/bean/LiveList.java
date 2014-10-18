package com.letv.watchball.bean;


/**
 * 直播列表
 * 
 * @author liuhanzhi
 * 
 */
public class LiveList extends Base {
	public Body[] body;

	/**
	 * 一天的直播信息
	 * 
	 * @author liuhanzhi
	 * 
	 */
	public class Body {
		/**
		 * 日期
		 */
		public String date;
		/**
		 * 直播列表
		 */
		public Game[] live_infos;
		
	}
}
