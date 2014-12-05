package com.letv.watchball.bean;

import com.letv.http.bean.LetvBaseBean;

public class TimestampBean implements LetvBaseBean {

	/**
	 * 服务器时间与本地时间的偏移
	 * */
	private int offset;

	private static final TimestampBean tm = new TimestampBean();

	private TimestampBean() {
	}

	/**
	 * 得到时间戳对象
	 * */
	public static TimestampBean getTm() {
		return tm;
	}

	/**
	 * 更新时间戳
	 * */
	public void updateTimestamp(int newTimestamp) {
		long cur = System.currentTimeMillis();
		offset = (int) (cur / 1000 - newTimestamp);
		/**
		 * 同时更新下载包的时间戳
		 * */
		// com.letv.watchball.loader.db.TimestampBean.getTm().updateTimestamp(newTimestamp);
	}

	/**
	 * 得到当前的服务器时间，由偏移量换算得来
	 * */
	public int getCurServerTime() {
		long cur = System.currentTimeMillis();
		return (int) (cur / 1000 - offset);
	}
}
