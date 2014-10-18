package com.letv.watchball.bean;


/**
 * 所有可关注球队列表
 * @author liuhanzhi
 *
 */
public class MyTeams extends Base{
	public Body[] body;
	public class Body{
		public String name;
		public int teamId;
		public String level;
		public String img_url;
		
		/**
		 * 是否关注 (默认关注)
		 * 1 关注，0 未关注
		 */
		public String focused;
	}
}
