package com.letv.watchball.parser;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.letv.http.parse.LetvMainParser;
import com.letv.watchball.bean.TicketCount;

public class TicketCountParser extends LetvMainParser<TicketCount, JSONObject> {

	@Override
	public TicketCount parse(JSONObject data) {
		TicketCount ticketCount = new TicketCount();
		Log.e("gongmeng", "ticketcount:" + data.toString());
		try {
			if (data != null) {
				JSONObject body = data.getJSONObject("body");
				JSONObject result = body.getJSONObject("result");
				String status = result.getString("status");
				if (status.equalsIgnoreCase("0")) {
					ticketCount.count = "0";
					return ticketCount;
				}
				JSONArray pack = result.getJSONArray("package");
				for (int i = 0; i < pack.length(); i++) {
					JSONObject tick = pack.getJSONObject(i);
					if (tick.getString("type").equalsIgnoreCase("1"))
						ticketCount.count = tick.getString("count");
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Log.e("gongmeng", "count:" + ticketCount.count);
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
