package com.letv.watchball.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.letv.http.LetvHttpLog;
import com.letv.watchball.bean.MessageBean;
import com.letv.watchball.bean.MessageBeanListMap;
import com.letv.watchball.db.DBManager;
import com.letv.watchball.db.DialogMsgTraceHandler;
import com.letv.watchball.db.PreferencesManager;

/**
 * 客户端提示语列表解析
 * */
public class MessageBeanListParser extends LetvMobileParser<MessageBeanListMap> {

	@Override
	public MessageBeanListMap parse(JSONObject data) throws JSONException {
		JSONArray jsonArray = data.names();
		MessageBeanListMap mMessageBeanListMap = null;
		if (jsonArray != null) {
			if (isNewData()) {
				mMessageBeanListMap = new MessageBeanListMap();
				MessageBean mMessageBean = null;
				DialogMsgTraceHandler dialogMsgTrace = DBManager.getInstance().getDialogMsgTrace();
				int length = jsonArray.length();
				if (length > 25) {
					dialogMsgTrace.clearAll();// 清除数据再插入，效率更高
				}
				for (int i = 0; i < length; i++) {
					if (length > 25) {
						mMessageBean = new MessageBeanParse().parse(getJSONObject(data,
								jsonArray.getString(i)));
						dialogMsgTrace.saveDialogMsg(jsonArray.getString(i), mMessageBean.title,
								mMessageBean.message);
					}else {
						break;
					}
//					if (i < 2) {
//						mMessageBeanListMap.put(jsonArray.getString(i), mMessageBean);// 不为空就行
//					}
				}
				mMessageBeanListMap.setMax(length);
				LetvHttpLog.Err("----->length =" + length);
				PreferencesManager.getInstance().saveDialogMsgMarkid(getMarkId());
				PreferencesManager.getInstance().saveDialogMsgInfo(data.toString());
//				LetvApplication.getInstance().setMsgSuc(true);
				//TODO  注释了只请求一次的代码
			}
		}
		return mMessageBeanListMap;
	}
	@Override
	protected String getLocationData() {
//		LetvApplication.getInstance().setMsgSuc(true);
		return PreferencesManager.getInstance().getDialogMsgInfo();
	}
}
