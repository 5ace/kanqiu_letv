package com.letv.watchball.bean;

/**
 * 所有可关注球队列表
 * 
 * @author liuhanzhi
 * 
 */
public class FocusTeamList extends Base {
	public Body[] body;

	public class Body {
		/**
		 * 赛事id
		 */
		public String type;
		/**
		 * 赛事名称
		 */
		public String name;
		/**
		 * 赛事关注状态
		 */
		public String focused;
		/**
		 * 赛事icon
		 */
		public String img_url;
		public String level;
		public Team[] teams;

		public class Team {
			/**
			 * 球队id
			 */
			public String id;
			/**
			 * 球队名
			 */
			public String team;
			/**
			 * 球队icon
			 */
			public String img_url;
			/**
			 * 是否关注 0：不关注，1：关注
			 */
			public String focused;

			public String level;
		}
	}

}
