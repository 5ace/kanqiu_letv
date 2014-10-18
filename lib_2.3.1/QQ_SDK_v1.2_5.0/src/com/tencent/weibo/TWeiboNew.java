package com.tencent.weibo;

import java.io.File;
import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.constants.OAuthConstants;
import com.tencent.weibo.oauthv2.OAuthV2;

public class TWeiboNew {
	public static final String PREFERENCES = "qq_weibo";
	/*
	 * 申请APP KEY的具体介绍，可参见 http://wiki.open.t.qq.com/index.php/应用接入指引
	 * http://wiki.
	 * open.t.qq.com/index.php/腾讯微博移动应用接入规范#.E6.8E.A5.E5.85.A5.E6.B5.81.E7.A8.8B
	 */
	// !!!请根据您的实际情况修改!!! 认证成功后浏览器会被重定向到这个url中 必须与注册时填写的一致
	public static String redirectUri = "http://www.tencent.com/zh-cn/index.shtml";
	// !!!请根据您的实际情况修改!!! 换为您为自己的应用申请到的APP KEY
	public static String clientId = "801115505";
	// !!!请根据您的实际情况修改!!! 换为您为自己的应用申请到的APP SECRET
	public static String clientSecret = "be1dd1410434a9f7d5a2586bab7a6829";

	private OAuthV2 oAuth;

	private static TWeiboNew weibo = null;

	private TWeiboNew() {
		oAuth = new OAuthV2(redirectUri);
		oAuth.setClientId(clientId);
		oAuth.setClientSecret(clientSecret);
	}

	public synchronized static TWeiboNew getInstance() {
		if (null == weibo)
			weibo = new TWeiboNew();

		return weibo;
	}

	public void login(Context context, TWeiboListener listener) {
		if (isLogin(context) == 1) {
			listener.onComplete();
		} else {
			LoginNewActivity.launch(context, listener);
		}
	}

	public void logout(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				PREFERENCES, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();

		editor.clear();
		editor.commit();

		oAuth.setAccessToken("");
		oAuth.setExpiresIn("");
		oAuth.setOpenid("");
		oAuth.setOpenkey("");
		oAuth.setStatus(3);
	}

	public void share(Context context, TWeiboListener listener, String content,
			String imgUrl,boolean isSync) {
		if(!Utils.isNetAvailableForPlay(context)){
			listener.onFail("分享失败");
			return ;
		}
		String sync = "1";
		if(isSync)
			sync = "0";
		
		if (isLogin(context) == 1) {
			TAPI tAPI = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
			try {

				String path = Utils.url2FilePath(imgUrl);
				File file = new File(path);
				/*if(!file.exists()){
                     file.createNewFile();
                     InputStream inputStream=WeiBoAPIV2Activity.class.getResourceAsStream("/res/drawable-hdpi/logo_qweibo.jpg");
                     FileOutputStream fileOutputStream=new FileOutputStream(file);
                     byte[] buf=new byte[1024];
                     int ins;
                     while ((ins=inputStream.read(buf))!=-1) {
                         fileOutputStream.write(buf,0,ins);
                     }
                     inputStream.close();
                     fileOutputStream.close();
                }*/
				if (!TextUtils.isEmpty(path) && file.exists()) {
					// 分享带图片的文字
//					tAPI.addPic(oAuth, "json", content, Utils.getLocalIpAddress(), path);
					tAPI.addPic(oAuth, "json", content, Utils.getLocalIpAddress(),"","", path, sync);
				} else {
					// 分享文字
					tAPI.add(oAuth, "json", content, Utils.getLocalIpAddress(),"","",sync);
				}
				listener.onComplete();
			} catch (Exception e) {
				e.printStackTrace();
				listener.onFail("分享失败");
			}
			tAPI.shutdownConnection();
		} else {
			LoginNewActivity.launch(context, listener);
		}
	}

	public void saveUserInfo(Context context, OAuthV2 auth) {
		this.oAuth = auth;

		SharedPreferences preferences = context.getSharedPreferences(
				PREFERENCES, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();

		editor.putString("accessToken", auth.getAccessToken());
		editor.putString("expiresIn", auth.getExpiresIn());
		editor.putString("openId", auth.getOpenid());
		editor.putString("openKey", auth.getOpenkey());
		editor.putInt("status", auth.getStatus());

		editor.putLong("loginTime", Calendar.getInstance().getTimeInMillis());
		
		editor.commit();
	}

	public int isLogin(Context context) {
		if(!(isSessionValid(context) == 1)){
			if (TextUtils.isEmpty(oAuth.getAccessToken())) {
				SharedPreferences preferences = context.getSharedPreferences(
						PREFERENCES, Context.MODE_PRIVATE);
				String accessToken = preferences.getString("accessToken", null);
				if (accessToken != null) {
					String expiresIn = preferences.getString("expiresIn", null);
					String openId = preferences.getString("openId", null);
					String openKey = preferences.getString("openKey", null);
					int status = preferences.getInt("status", 3);

					oAuth.setAccessToken(accessToken);
					oAuth.setExpiresIn(expiresIn);
					oAuth.setOpenid(openId);
					oAuth.setOpenkey(openKey);
					oAuth.setStatus(status);
				}
			}
		}
		
		return isSessionValid(context) ;
	}
	
	private int isSessionValid(Context context){
		SharedPreferences preferences = context.getSharedPreferences(
				PREFERENCES, Context.MODE_PRIVATE);
		long loginTime = preferences.getLong("loginTime", 0);
		String expires = oAuth.getExpiresIn();
		if(loginTime > 0 && !TextUtils.isEmpty(expires)){
			long expiresIn = Long.parseLong(expires);
			if((System.currentTimeMillis() - loginTime)/1000 > expiresIn){
				//超过有效期
				return 2;
			}else{
				return 1;
			}
		}else{
			return 0;
		}
	}

	public OAuthV2 getAuth() {
		return oAuth;
	}

	public interface TWeiboListener {

		public void onComplete();

		public void onError();

		public void onFail(String message);

	}
}
