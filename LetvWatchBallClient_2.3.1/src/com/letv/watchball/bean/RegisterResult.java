package com.letv.watchball.bean;

import com.letv.http.bean.LetvBaseBean;
/**
 * 用户注册接口返回值实体
 * 请求：
 * {@link register(int updataId , String email , String mobile , String password , String nickname , String gender ,String registService , String vcode , com.letv.watchball.parse.RegisterResultParser)}
 * 解析：
 * {@link  com.letv.watchball.parse.RegisterResultParser}
 * */
public class RegisterResult implements LetvBaseBean {

	private int result;
	
	private String ssouid;
	
	private String username;

    
 
	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getSsouid() {
		return ssouid;
	}

	public void setSsouid(String ssouid) {
		this.ssouid = ssouid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "RegisterResult [result=" + result + ", ssouid=" + ssouid
				+ ", username=" + username + "]";
	}
}
