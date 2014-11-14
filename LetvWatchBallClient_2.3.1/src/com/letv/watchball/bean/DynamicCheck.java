package com.letv.watchball.bean;

import com.letv.http.bean.LetvBaseBean;
import com.letv.watchball.bean.Comments.Comment;

public class DynamicCheck implements LetvBaseBean{

	private int status;
	private String token;
	public void setToken(String token){
		this.token = token;
	}
	public void setStatus(int status){
		this.status = status;
	}
	public String getToken(){
		return token;
	}
	public int getStatus(){
		return status;
	}
	
	
}
