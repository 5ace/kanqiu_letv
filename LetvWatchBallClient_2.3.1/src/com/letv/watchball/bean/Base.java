package com.letv.watchball.bean;

import com.letv.http.bean.LetvBaseBean;

/**
 * 返回体的头信息
 * @author liuhanzhi
 *
 */
public class Base implements LetvBaseBean{
	public Header header;
	public class Header{
		public String status;
		public String msg;
	}
	
//	public abstract D getBody();
}
