package com.letv.watchball.bean;

/**
 * 直播订阅列表
 * 
 * @author liuhanzhi
 * 
 */
public class SubscribeGroupList extends Base {
	public Body body;

	public class Body {
		public SubscribesList[] subscribe_list;
		public Game[] focus_list;
	}

	public class SubscribesList {
		public String date;
		public Game[] live_infos;

	}

}
