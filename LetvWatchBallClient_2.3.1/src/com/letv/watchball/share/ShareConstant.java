package com.letv.watchball.share;

public class ShareConstant {
//	public static final String ShareConstant.mAppid = "100306621";//申请时分配的appid
	public interface BindState{
		public static final int UNBIND = 0 ;
		public static final int BIND = 1 ;
		public static final int BINDPASS = 2 ;
	}

	interface Sina{
		
		public static final String CONSUMER_KEY = "4071506712";

		public static final String CONSUMER_SECRET = "1eea1bdd72eef646052ef6769f75a772";

		public static final String REDIRECTURL = "http://m.letv.com";
//		public static final String REDIRECTURL = "http://mobile.letv.com/?p=12";
		
		
	}
	
	interface TencentWeibo{
		// !!!请根据您的实际情况修改!!! 认证成功后浏览器会被重定向到这个url中 必须与注册时填写的一致
		public static String redirectUri = "http://mobile.letv.com/?p=12";
		// !!!请根据您的实际情况修改!!! 换为您为自己的应用申请到的APP KEY
		public static String clientId = "801462162";
		// !!!请根据您的实际情况修改!!! 换为您为自己的应用申请到的APP SECRET
		public static String clientSecret = "b082fee53ae1d8d5028d86984ebd3be4";
	}
	
	interface TencentQzone{
		
		
		public static final String CALLBACK = "letvqzone://m.letv.com/";
		
		public static final String scope = "get_user_info,get_user_profile,add_share,add_topic,list_album,upload_pic,add_album";//授权范围
		
		public static final String mAppid = "1150075690";//申请时分配的appid
		
	}
	
	interface Weixin{
		
		public static final String APP_ID = "wx1719b7b3d4f3cbcd";
		
	}
	
	interface Renren{
		
//		public static final String API_KEY = "370cd30bdb9248b6904654deebd30c3f";
//
//		public static final String SECRET_KEY = "8169f60bab284fb683038b71afb2e7b9";
//
//		public static final String APP_ID = "246102";
		
		//web
		public static final String API_KEY = "370cd30bdb9248b6904654deebd30c3f";
		
		public static final String SECRET_KEY = "8169f60bab284fb683038b71afb2e7b9";
		
		public static final String APP_ID = "246102";
		
	}
	
	interface LetvStar{
		
	}
	
	interface Kaixin{
		
	}

}
