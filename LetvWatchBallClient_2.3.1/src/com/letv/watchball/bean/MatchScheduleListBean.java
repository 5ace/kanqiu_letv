package com.letv.watchball.bean;


/**
 * 赛程列表
 * 
 * @author liuhanzhi
 * 
 */
public class MatchScheduleListBean extends Base{
	public MatchSchedule body;
	
	public class MatchSchedule {
		public Round[] rounds;
		public MatchList[] match_list;

		public class Round {
			/**
			 * 轮次检索key
			 */
			public String key;
			/**
			 * 轮次名称
			 */
			public String name;
			/**
			 * 是否当前轮次    1：是，0：否
			 */
			public String cur;
		}

		public class MatchList {
			
			/**
			 * 比赛日期，date: "06月01日 周六",
			 */
			public String date;
			
			/**
			 * 按天分类的比赛列表
			 */
			public Game[] matches;
			
//			public class Match{
//				
//				/**
//				 * 比赛id
//				 */
//				public String id;
//				/**
//				 * 主队
//				 */
//				public String home;
//				/**
//				 * 客队
//				 */
//				public String guest;
//				/**
//				 * 主队比分
//				 */
//				public String homeScore;
//				/**
//				 * 客队比分
//				 */
//				public String guestScore;
//				/**
//				 * 主队icon
//				 */
//				public String homeImg;
//				/**
//				 * 客队icon
//				 */
//				public String guestImg;
//				/**
//				 * 开赛时间
//				 */
//				public String playTime;
//				/**
//				 * 开赛日期
//				 */
//				public String playDate;
//				/**
//				 * 比赛状态    0：未开始；1：直播中；2：已结束
//				 */
//				public String status;
//				/**
//				 * 视频id
//				 */
//				public String vid;
//				/**
//				 * 350码流直播地址
//				 */
//				public String live_url_350;
//				/**
//				 * 800码流直播地址
//				 */
//				public String live_url_800;
//				/**
//				 * 1300码流直播地址
//				 */
//				public String live_url_1300;
//				
//			}
			
			
		}
	}
}
