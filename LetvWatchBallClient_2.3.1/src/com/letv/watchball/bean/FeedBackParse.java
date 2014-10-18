package com.letv.watchball.bean;

import com.letv.watchball.parser.LetvMobileParser;
import org.json.JSONException;
import org.json.JSONObject;


public class FeedBackParse extends LetvMobileParser<State> {

	@Override
	public State parse(JSONObject data) throws JSONException {
		
		String result = getString(data, "msg");
		State state = new State();
		if(result.equals("ok") ){
			state.setSucceed(true);
		}else{
			state.setSucceed(false);
		}
		
		return state ;
	}

    @Override
    protected JSONObject getData(String data) throws JSONException {
        JSONObject object = null ;
        if(status == STATE.NORMAL){
            object = new JSONObject(data);
            object = getJSONObject(object, HEADER);
        }
        return object;
    }
}
