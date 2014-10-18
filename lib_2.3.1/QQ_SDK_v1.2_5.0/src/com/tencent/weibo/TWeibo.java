package com.tencent.weibo;

import java.io.File;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.constants.OAuthConstants;
import com.tencent.weibo.oauthv1.OAuthV1;
import com.tencent.weibo.oauthv1.OAuthV1Client;
import com.tencent.weibo.utils.QHttpClient;

public class TWeibo {
	private static final String TAG = TWeibo.class.getName();
	
	public static final String PREFERENCES = "qq_weibo_auth1";
    /*
     * 申请APP KEY的具体介绍，可参见 
     * http://wiki.open.t.qq.com/index.php/应用接入指引
     * http://wiki.open.t.qq.com/index.php/腾讯微博移动应用接入规范#.E6.8E.A5.E5.85.A5.E6.B5.81.E7.A8.8B
     */
    //!!!请根据您的实际情况修改!!!      认证成功后浏览器会被重定向到这个url中   本例子中不需改动
	private String oauthCallback = "null"; 
    //!!!请根据您的实际情况修改!!!      换为您为自己的应用申请到的APP KEY
	private String oauthConsumeKey = "801073511"; 
    //!!!请根据您的实际情况修改!!!      换为您为自己的应用申请到的APP SECRET
	private String oauthConsumerSecret="ffcee760ec6979269a7986b2cd37e304";
	
	
	private OAuthV1 oAuth;

	private static TWeibo weibo = null;

	private TWeibo() {
		oAuth = new OAuthV1(oauthCallback);
		oAuth.setOauthConsumerKey(oauthConsumeKey);
		oAuth.setOauthConsumerSecret(oauthConsumerSecret);
		
		//关闭OAuthV1Client中的默认开启的QHttpClient。
		OAuthV1Client.getQHttpClient().shutdownConnection();
		
		//为OAuthV1Client配置自己定义QHttpClient。
		OAuthV1Client.setQHttpClient(new QHttpClient());
	}

	public synchronized static TWeibo getInstance() {
		if (null == weibo)
			weibo = new TWeibo();

		return weibo;
	}
	
	public String getOauthCallback() {
		return oauthCallback;
	}

	public void setOauthCallback(String oauthCallback) {
		this.oauthCallback = oauthCallback;
	}

	public String getOauthConsumeKey() {
		return oauthConsumeKey;
	}

	public void setOauthConsumeKey(String oauthConsumeKey) {
		this.oauthConsumeKey = oauthConsumeKey;
	}

	public String getOauthConsumerSecret() {
		return oauthConsumerSecret;
	}

	public void setOauthConsumerSecret(String oauthConsumerSecret) {
		this.oauthConsumerSecret = oauthConsumerSecret;
	}

	public void login(final Context context, final TWeiboListener listener) {
		if (isLogin(context)) {
			listener.onComplete();
		} else {
			try{
				//向腾讯微博开放平台请求获得未授权的Request_Token
				 oAuth=OAuthV1Client.requestToken(oAuth);
				 Log.e(TAG,"request_token:"+oAuth.getOauthToken()+
							"\nrequest_token_secret:"+oAuth.getOauthTokenSecret());
				 LoginActivity.launch(context, new LoginActivity.CallbackListener() {
					
					@Override
					public void callBack(String verifyCode) {
						 try {
							oAuth.setOauthVerifier(verifyCode);
							oAuth = OAuthV1Client.accessToken(oAuth);
							TWeibo.getInstance().saveUserInfo(context, oAuth);
						} catch (Exception e) {
							listener.onFail("认证失败");
							e.printStackTrace();
						}
						 Log.e(TAG,"access_token:"+oAuth.getOauthToken()+
									"\naccess_token_secret:"+oAuth.getOauthTokenSecret());
						 listener.onComplete();
					}
				});
				
// 				if(oAuth.getStatus() == 0){
// 					listener.onComplete();
// 				}else{
// 					listener.onFail("认证失败");
// 				}
			}catch(Exception e){
				Log.e("D", "eddddd = ",e);
				listener.onFail("认证失败");
			}
		}
	}
	
	public void loginDialog(final Activity context, final TencentAuthListener listener) {
		if (isLogin(context)) {
			listener.onComplete();
		} else {
			try{
				//向腾讯微博开放平台请求获得未授权的Request_Token
				oAuth=OAuthV1Client.requestToken(oAuth);
				Log.e("D","request_token:"+oAuth.getOauthToken()+
						"\nrequest_token_secret:"+oAuth.getOauthTokenSecret());
				this.authorize(context, listener, oAuth);
				
			}catch(Exception e){
				Log.e("D", "e = ",e);
//				listener.onFail("认证失败");
			}
		}
	}
	
	public void loginDialog(final Activity context, final TencentAuthListener listener, int width, int height, ProgressDialog progressDialog) {
		if (isLogin(context)) {
			listener.onComplete();
		} else {
			try{
				//向腾讯微博开放平台请求获得未授权的Request_Token
				Log.d("D", "---------------------TWeibo loginDialog");
				oAuth=OAuthV1Client.requestToken(oAuth);
				Log.e("D","request_token:"+oAuth.getOauthToken()+
						"\nrequest_token_secret:"+oAuth.getOauthTokenSecret());
				this.authorize(context, listener, oAuth, width, height, progressDialog);
				
			}catch(Exception e){
				Log.e("D", "e = ",e);
//				listener.onFail("认证失败");
			}
		}
	}

	public void logout(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				PREFERENCES, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();

		editor.clear();
		editor.commit();

		oAuth.setOauthToken("");
		oAuth.setOauthTokenSecret("");
		oAuth.setStatus(3);
		oAuth.setOauthVerifier("");
	}

	public void share(Context context, TWeiboListener listener, String content,
			String imgUrl,boolean isSync) {
		String sync = "1";
		if(isSync)
			sync = "";
		
		if (isLogin(context)) {
			TAPI tAPI = new TAPI(OAuthConstants.OAUTH_VERSION_1);
			try {

				String path = Utils.url2FilePath(imgUrl);
				File file = new File(path);
				if (!TextUtils.isEmpty(path) && file.exists()) {
					// 分享带图片的文字
//					tAPI.addPic(oAuth, "json", content, Utils.getLocalIpAddress(), path);
					tAPI.addPic(oAuth, "json", content, Utils.getLocalIpAddress(),"","", path, sync);
				} else {
					// 分享文字
					String s = tAPI.add(oAuth, "json", content, Utils.getLocalIpAddress(),"","",sync);
					Log.e(TAG,s);
				}
				listener.onComplete();
			} catch (Exception e) {
				e.printStackTrace();
				listener.onFail("分享失败");
			}
			tAPI.shutdownConnection();
		} /*else {
			login(context, listener);
		}*/
	}
	
	public void shareToPad(Context context, TWeiboListener listener, String content,
			String imgUrl,boolean isSync, File file) {
		String sync = "1";
		if(isSync)
			sync = "";
		
		if (isLogin(context)) {
			TAPI tAPI = new TAPI(OAuthConstants.OAUTH_VERSION_1);
			try {

				String path;
				if(file == null){
					path = "";
				}
				path = file.getAbsoluteFile().toString();
				if (!TextUtils.isEmpty(path) && file.exists()) {
					// 分享带图片的文字
					tAPI.addPic(oAuth, "json", content, Utils.getLocalIpAddress(),"","", path, sync);
				} else {
					// 分享文字
					String s = tAPI.add(oAuth, "json", content, Utils.getLocalIpAddress(),"","",sync);
					Log.e(TAG,s);
				}
				listener.onComplete();
			} catch (Exception e) {
				e.printStackTrace();
				listener.onFail("分享失败");
			}
			tAPI.shutdownConnection();
		}
	}

	public void saveUserInfo(Context context, OAuthV1 auth) {
		this.oAuth = auth;

		SharedPreferences preferences = context.getSharedPreferences(
				PREFERENCES, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();

		editor.putString("token", auth.getOauthToken());
		editor.putString("tokenSecrete", auth.getOauthTokenSecret());
		editor.putString("verifier", auth.getOauthVerifier());
		editor.putInt("status", auth.getStatus());
//		editor.putString("signature_method", auth.getOauthSignatureMethod());
//		editor.putString("timestamp", auth.getOauthTimestamp());
//		editor.putString("nonce", auth.getOauthNonce());
		
		
		editor.commit();
	}

	public boolean isLogin(Context context) {
		/*if(!isSessionValid(context)){
			return false;
		}*/
		
		if (TextUtils.isEmpty(oAuth.getOauthVerifier())) {
			SharedPreferences preferences = context.getSharedPreferences(
					PREFERENCES, Context.MODE_PRIVATE);
			
			String verifier = preferences.getString("verifier", null);
			if (verifier != null) {
				String token = preferences.getString("token", null);
				String tokenSecrete = preferences.getString("tokenSecrete", null);
				
				int status = preferences.getInt("status", 3);

				oAuth.setOauthToken(token);
				oAuth.setOauthTokenSecret(tokenSecrete);
				oAuth.setStatus(status);
				oAuth.setOauthVerifier(verifier);
			}else{
				return false;
			}
		}

		return true;
	}
	
	/*private boolean isSessionValid(Context context){
		SharedPreferences preferences = context.getSharedPreferences(
				PREFERENCES, Context.MODE_PRIVATE);
		long loginTime = preferences.getLong("loginTime", 0);
		String expires = oAuth.getExpiresIn();
		if(loginTime > 0 && !TextUtils.isEmpty(expires)){
			long expiresIn = Long.parseLong(expires);
			if((System.currentTimeMillis() - loginTime)/1000 > expiresIn){
				//超过有效期
				return false;
			}else{
				return true;
			}
		}else{
			return false;
		}
	}*/

	public OAuthV1 getAuth() {
		return oAuth;
	}

	public interface TWeiboListener {

		public void onComplete();

		public void onError();

		public void onFail(String message);

	}
	
	/**
	 * 完成登录并获取sessionkey(User-Agent Flow)。
	 * 
	 * @param activity
	 * @param listener
	 */
	public void authorize(Activity activity, final TencentAuthListener listener, OAuthV1 oAuth) {
		String url = OAuthConstants.OAUTH_V1_AUTHORIZE_URL+"?oauth_token="+oAuth.getOauthToken();
		new TencentDialog(activity, url, listener).show();
	}
	
	public void authorize(Activity activity, final TencentAuthListener listener, OAuthV1 oAuth, int width, int height, ProgressDialog progressDialog) {
		String url = OAuthConstants.OAUTH_V1_AUTHORIZE_URL+"?oauth_token="+oAuth.getOauthToken();
		Log.d("D", "-------------------TWeibo authorize");
		new TencentDialog(activity, url, listener, width, height, progressDialog).show();
	}
}
