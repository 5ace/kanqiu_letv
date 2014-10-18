package com.letv.watchball.bean;

/**
 * 我关注球队的所有比赛列表
 * @author liuhanzhi
 *
 */
public class MyTeamMatch extends Base{

	public Body body;
	public class Body{
		public MonthMatches month_matches[];
//		public Team teams;
	}
	public class MonthMatches{
		public String date;
		public Game[] matches;
	}
}
