package com.letv.watchball.bean;

import com.letv.http.bean.LetvBaseBean;
import com.letv.watchball.bean.Comments.Comment;

public class DynamicCheck implements LetvBaseBean{

	public String status;
	public String token;
	public String code;
	public void setToken(String token){
		this.token = token;
	}
	public void setStatus(String status){
		this.status = status;
	}
	public String getToken(){
		return token;
	}
	public String getStatus(){
		return status;
	}
	
	
}
