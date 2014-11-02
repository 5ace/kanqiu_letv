package com.letv.watchball.parser;

import org.json.JSONObject;

import android.text.TextUtils;

import com.letv.http.parse.LetvMainParser;
import com.letv.watchball.bean.DynamicCheck;
import com.letv.watchball.bean.TimestampBean;

public class DynamicCheckParser extends LetvMainParser<DynamicCheck,String>{

	@Override
	public DynamicCheck parse(String data) throws Exception {
		JSONObject jsonObject = new JSONObject(data);
		int time = getInt(jsonObject, "time");
		if(time > 0){
			return DynamicCheck.getdc();
		}
		
		return null ;
	}

	@Override
	protected boolean canParse(String data) {
		if(TextUtils.isEmpty(data)){
			return false ;
		}
		return true;
	}

	@Override
	protected String getData(String data) throws Exception {
		return data;
	}
}
