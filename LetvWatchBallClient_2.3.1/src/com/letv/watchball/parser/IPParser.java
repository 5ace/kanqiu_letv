package com.letv.watchball.parser;

import org.json.JSONException;
import org.json.JSONObject;

import com.letv.http.parse.LetvMainParser;
import com.letv.watchball.bean.IP;

/**
 * 海外IP接口解析
 * */
public class IPParser extends LetvMainParser<IP, String> {

	// private final String IP = "ip" ;
	private final String CLIENT_IP = "clientIP";
	private final String USER_COUNTRY = "userCountry";

	@Override
	public IP parse(String data) throws JSONException {

		JSONObject jsonObject = new JSONObject(data);

		IP ip = new IP();

		// ip.setIpLocale(getInt(jsonObject, IP));
		ip.setClient_ip(getString(jsonObject, CLIENT_IP));
		ip.setUser_country(getString(jsonObject, USER_COUNTRY));

		return ip;
	}

	@Override
	protected boolean canParse(String data) {
		return true;
	}

	@Override
	protected String getData(String data) throws JSONException {
		return data;
	}
}
