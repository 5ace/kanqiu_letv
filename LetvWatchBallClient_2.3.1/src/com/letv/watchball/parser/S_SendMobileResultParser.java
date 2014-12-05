package com.letv.watchball.parser;

import org.json.JSONException;
import org.json.JSONObject;

import com.letv.http.parse.LetvMainParser;
import com.letv.watchball.bean.S_SendMobileResult;

/**
 * 手机短信下行接口解析类
 * 
 * @author ddf
 * 
 */
public class S_SendMobileResultParser extends
		LetvMainParser<S_SendMobileResult, String> {

	/**
	 * 返回值号
	 */
	private final String CODE = "code";

	@Override
	public S_SendMobileResult parse(String data) throws JSONException {

		JSONObject jsonObject = new JSONObject(data);
		S_SendMobileResult s_SendMobileResult = new S_SendMobileResult();

		s_SendMobileResult.setCode(getInt(jsonObject, CODE));

		return s_SendMobileResult;
	}

	@Override
	protected boolean canParse(String data) {

		try {
			JSONObject object = new JSONObject(data);
			if (object.has(CODE)) {
				return true;
			} else {
				return false;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	protected String getData(String data) throws JSONException {

		return data;
	}

}
