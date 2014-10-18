package com.letv.watchball.bean;

/**
 * 排行榜(积分榜)
 * 
 * @author liuhanzhi
 * @changeBy Liuheyuan
 */
public class RankingTable extends Base {
	/**
	 * 分组
	 */
	public Body[] body;

	public class Body {

		/**
		 * 分组名称，“”为当前无分组
		 */
		public String group;
		/**
		 * 分组排名
		 */
		public GroupList[] list;

		public class GroupList {
			/**
			 * 球队名称
			 */
			public String team;
			/**
			 * 排名
			 */
			public int rank;
			/**
			 * 获胜场次
			 */
			public int win;
			/**
			 * 失败场次
			 */
			public int lose;
			/**
			 * 打平场次
			 */
			public int draw;
			/**
			 * 积分
			 */
			public int score;
			/**
			 * 球队icon
			 */
			public String img_url;
			
			/**
			 * 胜率 (对于篮球)
			 */
			public String win_rate;
		}

	}
}
