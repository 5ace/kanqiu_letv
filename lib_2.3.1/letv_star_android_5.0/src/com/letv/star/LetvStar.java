package com.letv.star;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.Toast;

import com.letv.star.bean.User;
import com.letv.star.net.HttpUtil;
import com.letv.star.parse.ShareParser;
import com.letv.star.parse.UserParser;


public class LetvStar {
	
	public final static String PREFERENCES = "letv_star" ;
	
	public static String APP_KEY = "f3baaf29f3383b560a1857893ead8710" ;
	
	public static String APP_SECRET = "9c0b26a21c5e71fe6b9b79f924c593f9" ;
	
	private static LetvStar mLetvStar ;

	public User mUser ;
	
//	private final String BASE_URL = "http://10.200.89.34/Api/index.php?r=";//测试
	private final String BASE_URL = "http://mobile.starcast.letv.com/index.php?r=";//正式
	
	public LetvStar(){
		
	}
	
	public synchronized static LetvStar getInstance(){
		if(mLetvStar == null)
			mLetvStar = new LetvStar();
		
		return mLetvStar ;
	}
	
	public void loginDialog(Context context,LetvStarListener listener, int width, int height){
			if(isLogin(context)){
			listener.onComplete() ;
			return ;
		}else{
			new LetvStarDialog(context, listener, width, height).show();
		}
	}
	
	public void loginDialog(Context context,LetvStarListener listener){
		if(isLogin(context)){
			listener.onComplete() ;
			return ;
		}else{
			new LetvStarDialog(context, listener).show();
		}
	}
	
	public void login(Context context , LetvStarListener listener){
		if(isLogin(context)){
			listener.onComplete() ;
			return ;
		}else{
			LetvStarLoginActivity.lanuch(context, listener);
		}
	}
	
	public boolean login(final Context context , String name , String password , LetvStarListener listener){
		
		Bundle params = new Bundle();
		params.putString("client_id", LetvStar.APP_KEY);
		params.putString("client_secret", LetvStar.APP_SECRET);
		params.putString("grant_type", "password");
		
		if(name == null || password == null || name.length() == 0 || password.length() == 0){
			listener.onErr("用户名或密码不能为空");
			return false ;
		}
		
		params.putString("username", name);
		params.putString("password", password);

		String data = HttpUtil.doPost(context , BASE_URL + "user/login", params);
		if(data != null && data.length() > 0){
			boolean flag = new UserParser(context , data , name , password).Parser();
			if(flag){
				listener.onComplete();
				return true ;
			}else{
				listener.onFail("登录失败");
				if(context instanceof Activity){
					((Activity)context).runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(context, "登录失败", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		}else{
			listener.onFail("请求失败");
			if(context instanceof Activity){
				((Activity)context).runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(context, "登录失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		}
		
		return false ;
	}
	
	public void share(final Context context , String desc , String albumId , String isAlbum , String cid , String pic , String name , String year , String director , String stars , String duration , final LetvStarListener listener){
		String album = createAlbumJson(albumId, isAlbum, cid, pic, name, year, director, stars, duration);
		final Bundle params = new Bundle();
		
		params.putString("oauth_token", mUser.getToken());
		params.putString("uid", mUser.getUid());
		params.putString("nick", mUser.getNick());
		params.putString("type", "6");
		params.putString("desc", desc);
		params.putString("album", album);
		params.putString("loc", "");
		params.putString("lon", "");
		params.putString("lat", "");
		params.putString("version", "2.1.0");
		
		String data = HttpUtil.doPost(context , BASE_URL + "content/create", params);//正式
		System.out.println("data  ==  " + data);
		if(data != null && data.length() > 0){
			int flag = new ShareParser(data).parse();
			if(flag == 0){
				listener.onComplete();
			}else{
				if(flag == 1002 || flag == 1003){
					login(context, mUser.getName() , mUser.getPassword() ,new LetvStarListener() {
						
						@Override
						public void onFail(String failLog) {
							listener.onFail("分享失败");
						}
						
						@Override
						public void onErr(String errLog) {
							listener.onFail("分享失败");
						}
						
						@Override
						public void onComplete() {
							mUser = null ;
							isLogin(context);
							params.putString("oauth_token", mUser.getToken());
							String data = HttpUtil.doPost(context , BASE_URL + "content/create", params);
							if(data != null && data.length() > 0){
								int flag = new ShareParser(data).parse();
								if(flag == 0){
									listener.onComplete();
								}else{
									listener.onFail("分享失败");
								}
							}
						}
					});
				}else{
					listener.onFail("分享失败");
				}
			}
		}else{
			listener.onFail("请求失败");
		}
	}
	
	
	public String createAlbumJson(String albumId , String isAlbum , String cid , String pic , String name , String year , String director , String stars , String duration){
		JSONObject jsonObject = new JSONObject() ;
		
//		if("4".equals(cid)){
//			cid = "1";
//		}else if("5".equals(cid)){
//			cid = "2";
//		}else if("6".equals(cid)){
//			cid = "3";
//		}else if("86".equals(cid)){
//			cid = "4";
//		}else if("78".equals(cid)){
//			cid = "11";
//		}else if("221".equals(cid)){
//			cid = "12";
//		}else if("164".equals(cid)){
//			cid = "19";
//		}else if("202".equals(cid)){
//			cid = "21";
//		}else if("111".equals(cid)){
//			cid = "16";
//		}else if("92".equals(cid)){
//			cid = "17";
//		}else if("186".equals(cid)){
//			cid = "20";
//		}
		
		try {
			jsonObject.put("id", albumId);
			jsonObject.put("isalbum", isAlbum);
			jsonObject.put("cid", cid);
			jsonObject.put("pic", pic);
			jsonObject.put("name", name);
			if(null == year)
				year = "";
			jsonObject.put("year", year);
			if(null == director)
				director = "";
			jsonObject.put("director", director);
			if(null == stars)
				stars = "";
			jsonObject.put("stars", stars);
			if(null == duration)
				duration = "";
			jsonObject.put("duration", duration);
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
		
		return jsonObject.toString();
	}
	
	public void saveUser(Context context , User user){
		mUser = user ;
		
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		Editor editor = preferences.edit() ;
		
		editor.putString("name", user.getName());
		editor.putString("password", user.getPassword());
		editor.putString("uid", user.getUid());
		editor.putString("nick", user.getNick());
		editor.putString("token", user.getToken());
		editor.putLong("expires", user.getExpires());
		editor.putLong("time", user.getTime());
		
		editor.commit() ;
	}
	
	public boolean isLogin(Context context){
		
		if(mUser == null){
			SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
			String uid = preferences.getString("uid", null);
			if(uid != null){
				String name = preferences.getString("name", null);
				String password = preferences.getString("password", null);
				String nick = preferences.getString("nick", null);
				String token = preferences.getString("token", null);
				long expires = preferences.getLong("expires", 0);
				mUser = new User() ;
				mUser.setName(name);
				mUser.setPassword(password);
				mUser.setUid(uid);
				mUser.setNick(nick);
				mUser.setToken(token);
				mUser.setExpires(expires);
			}
		}

		if(mUser != null && mUser.getUid() != null){
			return true ;
		}
		
		return false ;
	}
	
	public void logout(Context context){
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		Editor editor = preferences.edit() ;
		
		editor.clear();
		editor.commit() ;
		
		mUser = null ;
	}
}
