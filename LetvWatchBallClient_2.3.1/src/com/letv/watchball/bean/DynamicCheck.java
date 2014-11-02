package com.letv.watchball.bean;

import com.letv.http.bean.LetvBaseBean;

public class DynamicCheck implements LetvBaseBean {

	/**
	 * 
	 */
	private static final DynamicCheck dc = new DynamicCheck();

	private DynamicCheck() {
	}

	/**
	 * 得到时间戳对象
	 * */
	public static DynamicCheck getdc() {
		return dc;
	}
	private static final long serialVersionUID = 1121212454L;
	private String status;
	private String token;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
}
