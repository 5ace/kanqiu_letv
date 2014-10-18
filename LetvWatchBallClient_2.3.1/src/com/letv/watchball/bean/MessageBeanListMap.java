package com.letv.watchball.bean;

import java.util.HashMap;

import com.letv.http.bean.LetvBaseBean;
/**
 * 获取用户包月信息实体类
 * 请求：
 * 解析：
 * */
public class MessageBeanListMap extends HashMap<String, MessageBean> implements LetvBaseBean{

	private static final long serialVersionUID = 1L;
	/**
	 * 最大值
	 */
	private int max ;

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}
}
