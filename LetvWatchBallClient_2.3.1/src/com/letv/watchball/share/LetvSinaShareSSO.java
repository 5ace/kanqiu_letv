package com.letv.watchball.share;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.letv.cache.LetvCacheTools;
import com.letv.watchball.activity.SharePageActivity;
import com.letv.watchball.bean.ShareAlbum;
import com.letv.watchball.ui.impl.BasePlayActivity;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.WeiboParameters;
import com.weibo.sdk.android.net.AsyncWeiboRunner;
import com.weibo.sdk.android.net.RequestListener;
import com.weibo.sdk.android.sso.SsoHandler;
import com.weibo.sdk.android.util.Utility;


public class LetvSinaShareSSO {
	
	private static Oauth2AccessToken accessToken ;
	
	
	/**
	 * 判断是否已经登录
	 * */
	public static int isLogin(final Context context) {
		System.out.println("  sina accessToken=="+accessToken);
		if(accessToken == null){
			accessToken = AccessTokenKeeper.readAccessToken(context);
			System.out.println("  accessToken == null  accessToken=="+accessToken);
		}else{
			return accessToken.isSessionValid() ;
		}
		if(accessToken == null){
			return 0 ;
		}
		
		return accessToken.isSessionValid();
	}
	
	/**
	 * 分享时登录
	 * */
	public static SsoHandler login(final Activity context, final ShareAlbum album , final int order, final int vid) {
		if(isLogin(context) == 1&&context instanceof BasePlayActivity){
			
				SharePageActivity.launch(context,1, album.getShare_AlbumName(), album.getIcon(), album.getShare_id(), album.getType(), album.getCid(), album.getYear(), album.getDirector(), album.getActor(), album.getTimeLength() , order, vid);
			
			
			return null ;
		}else{
			Weibo mWeibo = Weibo.getInstance(ShareConstant.Sina.CONSUMER_KEY, ShareConstant.Sina.REDIRECTURL);
			SsoHandler mSsoHandler = new SsoHandler(context,mWeibo);
	        mSsoHandler.authorize( new WeiboAuthListener(){

				@Override
				public void onComplete(Bundle values) {
					String token = values.getString("access_token");
					String expires_in = values.getString("expires_in");
					accessToken = new Oauth2AccessToken(token, expires_in);

					if (accessToken.isSessionValid() == 1) {
						
						AccessTokenKeeper.keepAccessToken(context, accessToken);
						
						if(context instanceof BasePlayActivity){
							SharePageActivity.launch(context,1, album.getShare_AlbumName(), album.getIcon(), album.getShare_id(), album.getType(), album.getCid(), album.getYear(), album.getDirector(), album.getActor(), album.getTimeLength() , order, vid);
						}
					}
				}

				@Override
				public void onWeiboException(WeiboException e) {
				}

				@Override
				public void onError(WeiboDialogError e) {
				}

				@Override
				public void onCancel() {
				}
	        	
	        });
	        return mSsoHandler ;
		}
	}
	
	/**
	 * 分享
	 * */
	public static void share(Context context, String caption, String imaUrl,RequestListener listener) {
		Weibo mWeibo = Weibo.getInstance(ShareConstant.Sina.CONSUMER_KEY, ShareConstant.Sina.REDIRECTURL);
		try {
			String path = LetvCacheTools.StringTool.createFilePath(imaUrl);
			if (!TextUtils.isEmpty((String) (accessToken.getToken()))) {
				File file = new File(path);
				if (!TextUtils.isEmpty(path) && file.exists()) {
					upload(mWeibo, Weibo.app_key, path, caption,"", "", listener);
				} else {
					update(mWeibo, Weibo.app_key, caption, "", "",listener);
				}
			} else {

			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 分享图片
	 * */
	private static String upload(Weibo weibo, String source,
			String file, String status, String lon, String lat,
			RequestListener listener) throws WeiboException {
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("source", source);
		bundle.add("pic", file);
		bundle.add("status", status);
		bundle.add("access_token", accessToken.getToken());
		
		if (!TextUtils.isEmpty(lon)) {
			bundle.add("lon", lon);
		}
		if (!TextUtils.isEmpty(lat)) {
			bundle.add("lat", lat);
		}
		String rlt = "";
		String url = Weibo.SERVER + "statuses/upload.json";
		AsyncWeiboRunner.request(url, bundle, "POST",listener);

		return rlt;
	}

	/**
	 * 分享文字
	 * */
	private static String update(Weibo weibo, String source,
			String status, String lon, String lat, RequestListener listener)
			throws MalformedURLException, IOException, WeiboException {
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("source", source);
		bundle.add("status", status);
		bundle.add("access_token", accessToken.getToken());
		if (!TextUtils.isEmpty(lon)) {
			bundle.add("lon", lon);
		}
		if (!TextUtils.isEmpty(lat)) {
			bundle.add("lat", lat);
		}
		String rlt = "";
		String url = Weibo.SERVER + "statuses/update.json";
		AsyncWeiboRunner.request(url, bundle, "POST",listener);
		return rlt;
	}
	
	/**
	 * 登出
	 * */
	public static void logout(Activity context) {
		accessToken = null ;
		AccessTokenKeeper.clear(context);
		Utility.clearCookies(context);
	}
	
	public interface SinaSSOCallback{
		public void CallBack(int requestCode, int resultCode, Intent data);
	}
}
