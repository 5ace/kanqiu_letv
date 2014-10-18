package com.letv.watchball.bean;

/**
 * 赛果轮询
 * @author liuhanzhi
 *
 */
public class MatchResult extends Base{
	public Body body;
	public class Body{
		public String home;
		public String guest;
		public String homeScore;
		public String guestScore;
		public int status;
		public String vid;
	}
}
