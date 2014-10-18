package com.letv.star.parse;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.letv.star.LetvStar;
import com.letv.star.bean.User;

public class UserParser {
	
	private Context context ;
	
	private String data ;
	
	private String name ;
	
	private String password ;
	
	public UserParser(Context context , String data ,String name ,String password) {
		this.context = context ;
		this.data = data ;
		this.name = name ;
		this.password = password ;
	}
	
	public boolean Parser(){
		
		try {
			JSONObject jsonObject = new JSONObject(data) ;
			String state = jsonObject.getString("state");
			
			if("y".equalsIgnoreCase(state)){
				User user = new User() ;
				user.setTime(jsonObject.getLong("time"));
				
				jsonObject = jsonObject.getJSONObject("data").getJSONObject("single");
				
				user.setToken(jsonObject.getString("token"));
				user.setExpires(jsonObject.getLong("expires"));
				user.setScope(jsonObject.getString("scope"));
				user.setRefresh(jsonObject.getString("refresh"));
				user.setUid(jsonObject.getString("uid"));
				user.setNick(jsonObject.getString("nick"));
				user.setSex(jsonObject.getInt("sex"));
				user.setBirth(jsonObject.getString("birth"));
				user.setTel(jsonObject.getString("tel"));
				user.setPic(jsonObject.getString("pic"));
				user.setMail(jsonObject.getString("mail"));
				user.setTstate(jsonObject.getInt("tstate"));
				
				user.setName(name);
				user.setPassword(password);
				LetvStar.getInstance().saveUser(context , user);
				
				return true ;
			}else{
				return false ;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return false ;
	}
}
