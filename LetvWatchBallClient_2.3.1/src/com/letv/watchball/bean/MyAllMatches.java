package com.letv.watchball.bean;

/**
 * 我关注球队的所有比赛列表
 * @author liuhanzhi
 *
 */
public class MyAllMatches extends Base{

	public Body[] body;
	public class Body{
		public String date;
		public Game[] matches;
	}
}
