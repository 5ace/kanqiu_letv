package com.letv.watchball.bean;

public class Comments extends Base{

	public Body body;
	
	public class Body{
		public Comment comments;
	}
	
	public class Comment{
		public String result;
		public int total;
		public Data[] data;
	}
	
	public class Data{
		public long uid;
		public String content;
		public String vtime;
		public long ctime;
		public String city;
		public String from;
		public int replynum;
		public int flag;
		public User user;
	}
	public class User{
		public String uid;
		public String username;
		public String photo;
		public String isvip;
	} 
	
	public Data newData(){
		return new Data();
	}
	public User newUser(){
		return new User();
	}
}
