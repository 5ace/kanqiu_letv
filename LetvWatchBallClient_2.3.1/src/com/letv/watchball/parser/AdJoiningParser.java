package com.letv.watchball.parser;

import com.letv.http.parse.LetvMainParser;
import com.letv.watchball.bean.AdJoiningBean;
import org.json.JSONObject;


public class AdJoiningParser extends LetvMainParser<AdJoiningBean, String> {

	@Override
	public AdJoiningBean parse(String data) throws Exception {
		if (null != data) {
			JSONObject json = new JSONObject(data);
			AdJoiningBean bean = new AdJoiningBean();
			bean.setAhs(getString(json, "ahs"));
			bean.setVs(getString(json, "vs"));
			bean.setAts(getString(json, "ats"));
			bean.setMuri(getString(json, "muri"));
			return bean;
		}
		return null;
	}

	@Override
	protected boolean canParse(String data) {
		return true;
	}

	@Override
	protected String getData(String data) throws Exception {
		return data;
	}

}
