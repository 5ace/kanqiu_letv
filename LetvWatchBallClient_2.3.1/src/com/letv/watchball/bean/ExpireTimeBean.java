package com.letv.watchball.bean;

import com.letv.http.bean.LetvBaseBean;

public class ExpireTimeBean implements LetvBaseBean {

	/**
	 * 服务器时间与本地时间的偏移
	 * */
	private int offset;

	private static final ExpireTimeBean tm = new ExpireTimeBean();

	private ExpireTimeBean() {
	}

	/**
	 * 得到时间戳对象
	 * */
	public static ExpireTimeBean getTm() {
		return tm;
	}

	/**
	 * 更新时间戳
	 * */
	public void updateTimestamp(int newTimestamp) {
		long cur = System.currentTimeMillis();
		offset = (int) (cur / 1000 - newTimestamp);
	}

	/**
	 * 得到当前的服务器时间，由偏移量换算得来
	 * */
	public int getCurServerTime() {
		long cur = System.currentTimeMillis();
		return (int) (cur / 1000 - offset);
	}
}
