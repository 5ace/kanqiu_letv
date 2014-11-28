package com.letv.watchball.parser;

import org.json.JSONObject;



import com.letv.http.parse.LetvMainParser;
import com.letv.watchball.bean.UseTicket;

public class UseTicketParser extends LetvMainParser<UseTicket, JSONObject> {

	public UseTicket parse(JSONObject data) {
		UseTicket bean = new UseTicket();
		try {
			JSONObject body = data.getJSONObject("body");
			JSONObject result = body.getJSONObject("result");
			bean.status = result.getString("status");
		} catch (Exception e) {
			bean.status="0";
			return bean;
			
		}
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
