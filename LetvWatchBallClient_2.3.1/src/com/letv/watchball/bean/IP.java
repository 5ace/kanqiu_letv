package com.letv.watchball.bean;

import com.letv.http.bean.LetvBaseBean;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.parser.IPParser;

/**
 * 海外IP实体 请求：
 * {@link LetvHttpApi#requestIP(int, com.letv.exp.parser.LetvMainParser)} 解析：
 * {@link IPParser}
 * */
public class IP implements LetvBaseBean {

	private String client_ip;// 当前访问端Ip地址
	private String user_country;// 国家编号

	public String getClient_ip() {
		return client_ip;
	}

	public void setClient_ip(String client_ip) {
		this.client_ip = client_ip;
	}

	public String getUser_country() {
		return user_country;
	}

	public void setUser_country(String user_country) {
		this.user_country = user_country;
	}

}
