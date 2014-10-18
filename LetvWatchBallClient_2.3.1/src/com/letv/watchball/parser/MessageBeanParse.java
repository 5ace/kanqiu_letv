package com.letv.watchball.parser;

import org.json.JSONException;
import org.json.JSONObject;

import com.letv.watchball.bean.MessageBean;

/**
 * 客户端提示语接口解析
 * */
public class MessageBeanParse extends LetvMobileParser<MessageBean> {
	@Override
	public MessageBean parse(JSONObject data) throws JSONException {
		MessageBean mMessageBean = null;
		String title = getString(data, "title");
		String message = getString(data, "message");
		mMessageBean = new MessageBean(title, message);
//		LetvHttpLog.Err(mMessageBean.toString());
		return mMessageBean;
	}
}
