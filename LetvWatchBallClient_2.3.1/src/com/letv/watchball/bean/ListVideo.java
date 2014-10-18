package com.letv.watchball.bean;


/**
 * 视频新闻和赛事列表数据结构
 * @author Liuheyuan
 *
 */
public class ListVideo extends Base {

	public Body body = new Body();
	
	public static class Body{
		
		public Data[] data = new Data[]{};
		
		public int total = 0;
		
		
		public static class Data{
			
			public String vid;
			public String name;
			public String subname;
			public String icon;
			public String score;
			public String cid;
			public String type;
			public String at;
			public String year;
			public String count;
			public String isend;
			public String time_length;
			public String director;
			public String actor;
			public String intro;
			public String area;
			public String subcate;
			public String style;
			public String tv;
			public String rcompany;
			public String ctime;
			public String albumtype;
			public String albumtype_stamp;
			public String pay;
			public String needJump;
			public String singleprice;
			public String allowmonth;
			public String paydate;
			public String aid;
			public String icon_300x400;
			public String releaseDate;
			public Images images;

		}
		
		public static class Images{
			public String img200_150;
			public String img400_300;
			
		}
		
	}
	
	
}
