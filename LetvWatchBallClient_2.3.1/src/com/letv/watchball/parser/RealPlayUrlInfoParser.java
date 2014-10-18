package com.letv.watchball.parser;

import org.json.JSONException;
import org.json.JSONObject;

import com.letv.watchball.bean.RealPlayUrlInfo;

public class RealPlayUrlInfoParser {
	
	private RealPlayUrlInfo realPlayUrlInfo = null;
	
	public RealPlayUrlInfoParser(RealPlayUrlInfo realPlayUrlInfo) {
		this.realPlayUrlInfo = realPlayUrlInfo;
	}

	public RealPlayUrlInfo parse(String data) throws JSONException {
		JSONObject jsonObject = new JSONObject(data);
		realPlayUrlInfo.setRealUrl(jsonObject.getString("location"));
		realPlayUrlInfo.setGeo(jsonObject.getString("geo"));
		realPlayUrlInfo.setCode(200);
		return realPlayUrlInfo;
	}
}
