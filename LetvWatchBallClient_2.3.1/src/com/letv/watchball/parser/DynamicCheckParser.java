package com.letv.watchball.parser;

import org.json.JSONObject;
import com.letv.http.parse.LetvMainParser;
import com.letv.watchball.bean.DynamicCheck;

public class DynamicCheckParser extends
		LetvMainParser<DynamicCheck, JSONObject> {

	@Override
	public DynamicCheck parse(JSONObject data) throws Exception {
		DynamicCheck dynamicCheck = new DynamicCheck();
		if (data != null) {
			JSONObject result = data.getJSONObject("result");
			JSONObject body = result.getJSONObject("body");
			dynamicCheck.setStatus(body.getInt("status"));
			if (has(body, "token"))
				dynamicCheck.setToken(body.getString("token"));
			return dynamicCheck;
		}
		dynamicCheck.setStatus(0);
		return dynamicCheck;
	}

	@Override
	protected boolean canParse(String data) {
		return true;
	}

	@Override
	protected JSONObject getData(String data) throws Exception {
		return new JSONObject(data);
	}
}
