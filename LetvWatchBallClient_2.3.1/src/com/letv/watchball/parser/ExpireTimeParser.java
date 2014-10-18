package com.letv.watchball.parser;

import org.json.JSONObject;

import android.text.TextUtils;

import com.letv.http.parse.LetvMainParser;
import com.letv.watchball.bean.ExpireTimeBean;

public class ExpireTimeParser extends LetvMainParser<ExpireTimeBean,String>{

	@Override
	public ExpireTimeBean parse(String data) throws Exception {
		JSONObject jsonObject = new JSONObject(data);
		int time = getInt(jsonObject, "expiretime");
		if(time > 0){
			ExpireTimeBean.getTm().updateTimestamp(time);
			return ExpireTimeBean.getTm();
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
