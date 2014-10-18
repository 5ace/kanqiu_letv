package com.letv.watchball.parser;

import org.json.JSONObject;

import com.letv.http.parse.LetvMainParser;
import com.letv.watchball.bean.PlayTraceBoolean;

public class PlayTraceBooleanParser extends LetvMainParser<PlayTraceBoolean, JSONObject>{

	@Override
	public PlayTraceBoolean parse(JSONObject data) throws Exception {
		
		if(data != null && has(data, "result")){
			int result = getInt(data, "result");
			
			PlayTraceBoolean playTraceBoolean = new PlayTraceBoolean() ;
			
			playTraceBoolean.setSucceed(result == 1);
			
			return playTraceBoolean ;
		}
		
		return null;
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
