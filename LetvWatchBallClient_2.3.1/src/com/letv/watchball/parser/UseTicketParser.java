package com.letv.watchball.parser;

import org.json.JSONObject;

import com.letv.http.parse.LetvMainParser;
import com.letv.watchball.bean.UseTicket;


public class UseTicketParser extends LetvMainParser<UseTicket, JSONObject> {

	public UseTicket parse(JSONObject data) throws Exception {
		UseTicket bean = new UseTicket();
		if(data==null)
			return null;
		JSONObject body = data.getJSONObject("body");
		JSONObject result = data.getJSONObject("result");
		bean.status = result.getString("status");
		return bean;
	}

	@Override
	protected boolean canParse(String data) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected JSONObject getData(String data) throws Exception {
		return new JSONObject(data);
	}



}
