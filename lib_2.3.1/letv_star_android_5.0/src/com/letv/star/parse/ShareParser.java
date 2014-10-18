package com.letv.star.parse;

import org.json.JSONException;
import org.json.JSONObject;

public class ShareParser {
	
	private String data ;

	public ShareParser(String data) {
		this.data = data ;
	}
	
	public int parse(){
		try {
			JSONObject jsonObject = new JSONObject(data);
			String state = jsonObject.getString("state");
			
			if("y".equalsIgnoreCase(state)){
				return 0 ;
			}else{
				int code = jsonObject.getInt("code");
				
				return code ;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return -1 ;
	}
}
