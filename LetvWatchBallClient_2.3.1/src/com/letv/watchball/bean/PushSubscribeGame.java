package com.letv.watchball.bean;

import com.letv.http.bean.LetvBaseBean;

public class PushSubscribeGame implements LetvBaseBean {

	/**
	 * 比赛id
	 */
	public String id;
	/**
	 * 赛事名称
	 */
	public String level;
	/**
	 * 主队
	 */
	public String home;
	/**
	 * 客队
	 */
	public String guest;
	/**
	 * 比赛日期
	 */
	public String playDate;
	/**
	 * 开赛时间
	 */
	public String playTime;
	/**
	 * 格式化后的时间
	 */
	public long playTimeMillisecond;
	/**
	 * 比赛状态 0：未开始；1：直播中；2：已结束
	 */
	public int status;
	/**
	 * 是否已经通知
	 */
	public int isNotify;

	/**
	 * 是否已经发送赛果提醒
	 */
	public int isPushResult;

	@Override
	public String toString() {
		return "PushSubscribeGame{" + "id='" + id + '\'' + ", level='" + level
				+ '\'' + ", home='" + home + '\'' + ", guest='" + guest + '\''
				+ ", playDate='" + playDate + '\'' + ", playTime='" + playTime
				+ '\'' + ", playTimeMillisecond=" + playTimeMillisecond
				+ ", status=" + status + ", isNotify=" + isNotify
				+ ", isPushResult=" + isPushResult + '}';
	}
}
