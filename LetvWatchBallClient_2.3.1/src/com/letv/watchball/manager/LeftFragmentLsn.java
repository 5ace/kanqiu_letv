package com.letv.watchball.manager;

public interface LeftFragmentLsn {
	/**
	 * 热门直播
	 */
	public static final int ACTION_LIVE = 0x1001;
	/**
	 * 我的球队
	 */
	public static final int ACTION_MY_TEAM = ACTION_LIVE+1;
	/**
	 * 赛事
	 */
	public static final int ACTION_EVENTS = ACTION_MY_TEAM+1;
	/**
	 * 原创
	 */
	public static final int ACTION_ORIGINAL = ACTION_EVENTS+1;
	/**
	 * 左侧fragment的点击事件处理
	 * @param action action类型  
	 * @param obj 传入参数
	 */
	public void invoke(int action,Object obj);

}
