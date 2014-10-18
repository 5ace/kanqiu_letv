package com.letv.watchball.bean;

/**
 * 赛事列表
 * 
 * @author liuhanzhi
 * 
 */
public class MatchList extends Base {
	public Body body;

	public static class Body {
		
		public Match[] match_list;
		public OriginalColumn[] original_columns;
		
		public static class Match{
			/**
			 * 赛事名称
			 */
			public String name;
			/**
			 * 赛事id
			 */
			public String type;
			/**
			 * 赛事图标
			 */
			public String img_url;
			public int img_res;//本地字段
			/**
			 * 是否支持赛程    1：支持；0：不支持
			 */
			public String schedule;
			/**
			 * 是否支持排行榜     1：支持；0：不支持
			 */
			public String rank;
			/**
			 * 赛事级别     1：足球，2：篮球
			 */
			public String level;
		}
		
		public static class OriginalColumn{
			/**
			 * 原创节目id
			 */
			public int id;
			/**
			 * 原创节目名
			 */
			public String name;
			/**
			 * 原创节目icon
			 */
			public String img_url;
		}
	}
}
