package com.letv.watchball.parser;

import org.json.JSONArray;
import org.json.JSONObject;

import com.letv.http.parse.LetvMainParser;
import com.letv.watchball.bean.TicketCount;

public class TicketCountParser extends LetvMainParser<TicketCount, JSONObject> {

	@Override
	public TicketCount parse(JSONObject data) throws Exception {
		TicketCount ticketCount = new TicketCount();

		if (data != null) {
			JSONObject result = data.getJSONObject("result");
			String status = result.getString("status");
			if (status.equalsIgnoreCase("0")) {
				ticketCount.count = "0";
				return ticketCount;
			}
			JSONArray packages = result.getJSONArray("packages");
			for (int i = 0; i < packages.length(); i++) {
				JSONObject tick = packages.getJSONObject(i);
				if (tick.getString("type").equalsIgnoreCase("1"))
					ticketCount.count = tick.getString("count");
			}
		}

		return ticketCount;
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
