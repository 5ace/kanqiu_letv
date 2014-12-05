package com.letv.watchball.bean;

import com.letv.http.bean.LetvBaseBean;

/**
 * 解析： {@link MessageBeanParse}
 * */
public class MessageBean implements LetvBaseBean {
	/**
	 * title String 标题 message String 内容
	 */
	public String msgId;
	public String title;
	public String message;

	public MessageBean() {
		super();
	}

	@Override
	public String toString() {
		return "MessageBean [msgId =" + msgId + "title=" + title + ", message="
				+ message + "]";
	}

	public MessageBean(String msgId, String title, String message) {
		super();
		this.msgId = msgId;
		this.title = title;
		this.message = message;
	}

	public MessageBean(String title, String message) {
		super();
		this.title = title;
		this.message = message;
	}

}
