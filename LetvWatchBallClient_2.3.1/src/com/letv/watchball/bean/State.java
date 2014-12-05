package com.letv.watchball.bean;

import com.letv.http.bean.LetvBaseBean;

public class State implements LetvBaseBean {

	private boolean isSucceed;

	public boolean isSucceed() {
		return isSucceed;
	}

	public void setSucceed(boolean isSucceed) {
		this.isSucceed = isSucceed;
	}
}
