package com.letv.watchball.parser;

import org.json.JSONException;
import org.json.JSONObject;

import com.letv.watchball.bean.RegisterResult;

/**
 * 用户注册接口返回值解析类
 * 
 * @author ddf
 * 
 */
public class RegisterResultParser extends LetvMasterParser<RegisterResult> {

	/**
	 * 返回状态值
	 */
	private final String RESULT = "result";

	private final String SSOUID = "ssouid";

	private final String USER_NAME = "username";

	@Override
	public RegisterResult parse(JSONObject data) throws JSONException {

		RegisterResult registerResult = new RegisterResult();

		registerResult.setResult(getInt(data, RESULT));

		registerResult.setSsouid(getString(data, SSOUID));

		registerResult.setUsername(getString(data, USER_NAME));

		return registerResult;
	}

}
