package com.letv.watchball.share;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;

import com.letv.watchball.R;
import com.letv.watchball.activity.SharePageActivity;
import com.letv.watchball.bean.ShareAlbum;
import com.letv.watchball.ui.impl.BasePlayActivity;
import com.letv.watchball.utils.TextUtil;
import com.tencent.open.HttpStatusException;
import com.tencent.open.NetworkUnavailableException;
import com.tencent.tauth.Constants;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class LetvTencentQzoneShare {
	
	public static String mAccessToken, mOpenId;
	public static long mExpires_in ;
	public static final int PROGRESS = 0;
	public static  Context mxcontext;
	public static  IUiListener listener;
	public static Tencent	mTencent;
	private final static LetvTencentQzoneShare mLetvTencentQzoneShare=new LetvTencentQzoneShare();
	
	public static LetvTencentQzoneShare get_instace(){
		
		return mLetvTencentQzoneShare;
		
	}
	/**
	 * 判断是否登录
	 * */
	public static int isLogin(final Context context){
		if(!(satisfyConditions() == ShareConstant.BindState.BIND)){
			initLogin(context);
		}
		
		return satisfyConditions();
	}
	
	public void addShare(final ShareAlbum album , final int order, final int vid){
		onTencentQZResult=new onTencentQZResult() {
			@Override
			public void onTencentQZResult_back(String texturl) {
				// TODO Auto-generated method stub
		    	String replace_url = null;
				replace_url = LetvShareControl.getInstance().getShare().getVideo_url()
						.replace("{aid}", album.getShare_id() + "");
				replace_url = replace_url.replace("{index}", "1");
				replace_url = replace_url.replace("{vid}", vid + "");
				BaseApiListener mBaseApiListener = new BaseApiListener("add_share", true, mTencent);
				Bundle parmas = new Bundle();
				parmas.putString("title", "乐视视频");// 必须。feeds的标题，最长36个中文字，超出部分会被截断。
				parmas.putString("url", "http://www.letv.com/");// 必须。分享所在网页资源的链接，点击后跳转至第三方网页，
				parmas.putString("description", texturl);// 
				parmas.putString("summary", texturl);// 所分享的网页资源的摘要内容，或者是网页的概要描述。
				parmas.putString("images", album.getIcon());// 所分享的网页资源的代表性图片链接"，请以http://开头，长度限制255字符。多张图片以竖线（|）分隔，目前只有第一张图片有效，图片规格100*100为佳。
				parmas.putString("type", "5");// 分享内容的类型加url方法的话。
				parmas.putString("playurl", replace_url);//
				mTencent.requestAsync(Constants.GRAPH_ADD_SHARE, parmas, Constants.HTTP_POST, mBaseApiListener, null);
		        
		        
		        
			}
		};
		

	}
	/**
	 * 登录
	 * */
	public  void  login( final Context context ,final ShareAlbum album , final int order, final int vid){
		mxcontext=context;
		if(mTencent==null){
			mTencent = Tencent.createInstance(ShareConstant.TencentQzone.mAppid, context);
		}
		addShare(album,order,vid);
		if(satisfyConditions() == ShareConstant.BindState.BIND&&context instanceof BasePlayActivity){
			mTencent.setAccessToken(mAccessToken, mExpires_in+"");
			mTencent.setOpenId(mOpenId);
		
			SharePageActivity.launch(context,3, album.getShare_AlbumName(), album.getIcon(), album.getShare_id(), album.getType(), album.getCid(), album.getYear(), album.getDirector(), album.getActor(), album.getTimeLength() , order, vid);
			
			return ;
		}
		
	
		  listener = new IUiListener() {

			@Override
			public void onCancel() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onComplete(JSONObject arg0) {
				// TODO Auto-generated method stub
				System.out.println("arg0="+arg0);
				try {
					mAccessToken=arg0.getString("access_token");
					mOpenId=arg0.getString("openid");
					mExpires_in=System.currentTimeMillis()+Long.parseLong(arg0.getString("expires_in")) * 1000;
					saveLogin(context);
					if(satisfyConditions() == ShareConstant.BindState.BIND&&context instanceof BasePlayActivity){
						SharePageActivity.launch(context,3, album.getShare_AlbumName(), album.getIcon(), album.getShare_id(), album.getType(), album.getCid(), album.getYear(), album.getDirector(), album.getActor(), album.getTimeLength() , order, vid);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onError(UiError arg0) {
				// TODO Auto-generated method stub
				
			}
	          
	        };
	            if(context!=null&&mTencent!=null){
	            	mTencent.login((Activity)context, ShareConstant.TencentQzone.scope, listener);
	            }
	}

	
	/**
	 * 登出
	 * */
	public static void logout(Activity context){
		mAccessToken = null ;
		mOpenId = null ;
		mExpires_in = 0 ;
		SharedPreferences preferences = context.getSharedPreferences("qzone", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.clear() ;
		editor.commit() ;
	}
	
	
	private static int satisfyConditions() {
		if(mAccessToken != null && ShareConstant.TencentQzone.mAppid != null && mOpenId != null && !mAccessToken.equals("") && !ShareConstant.TencentQzone.mAppid.equals("") && !mOpenId.equals("")){
			if(mExpires_in > System.currentTimeMillis()){
				return ShareConstant.BindState.BIND ;
			}else{
				return ShareConstant.BindState.BINDPASS ;
			}
		}
		return ShareConstant.BindState.UNBIND ;
	}
	
	private static void saveLogin(Context context){
		SharedPreferences preferences = context.getSharedPreferences("qzone", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		
		editor.putString("mAccessToken", mAccessToken);
		editor.putString("mOpenId", mOpenId);
		editor.putLong("mExpires_in", mExpires_in);
		
		editor.commit() ;
	}
	
	private static void initLogin(Context context){
		SharedPreferences preferences = context.getSharedPreferences("qzone", Context.MODE_PRIVATE);
		mAccessToken = preferences.getString("mAccessToken", null);
		mOpenId = preferences.getString("mOpenId", null);
		mExpires_in = preferences.getLong("mExpires_in", 0);
	}
	
	public class BaseApiListener implements IRequestListener {
        public  String mScope = "all";
        public Boolean mNeedReAuth = false;
        public Tencent mxTencent;

        public BaseApiListener(String scope, boolean needReAuth,Tencent mTencent) {
            mScope = scope;
            mNeedReAuth = needReAuth;
            mxTencent=mTencent;
        }

        @Override
        public void onComplete(final JSONObject response, Object state) {
            doComplete(response, state);
        }

        protected void doComplete(JSONObject response, Object state) {
            try {
                int ret = response.getInt("ret");
                if (ret == 100030) {
                    if (mNeedReAuth) {
                        Runnable r = new Runnable() {
                            public void run() {
                            	if(mxcontext!=null){
                            		mxTencent.reAuth((Activity)mxcontext, mScope, listener);
                            	}
                            	
                            	}
                        };
                        ((Activity) mxcontext).runOnUiThread(r);
                    }
                }
                
                NotificationManager notificationManager = (NotificationManager) mxcontext
						.getSystemService(Context.NOTIFICATION_SERVICE);
				Notification notification = new Notification();
				PendingIntent contentIntent = PendingIntent.getActivity(
						(Activity) mxcontext, 0, new Intent(), 0);
				notification.setLatestEventInfo((Activity) mxcontext, null, null,
						contentIntent);
				notification.icon = R.drawable.notification_qzone_icon;
				notification.tickerText = TextUtil
						.getString(R.string.shareactivity_sina_ok);
				notification.flags = Notification.FLAG_AUTO_CANCEL;
				notificationManager.notify(333333, notification);
				notificationManager.cancel(333333);
                // azrael 2/1注释掉了, 这里为何要在api返回的时候设置token呢,
                // 如果cgi返回的值没有token, 则会清空原来的token
                // String token = response.getString("access_token");
                // String expire = response.getString("expires_in");
                // String openid = response.getString("openid");
                // mTencent.setAccessToken(token, expire);
                // mTencent.setOpenId(openid);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("toddtest", response.toString());
            }

        }

        @Override
        public void onIOException(final IOException e, Object state) {
        }

        @Override
        public void onMalformedURLException(final MalformedURLException e,
                Object state) {
        }

        @Override
        public void onJSONException(final JSONException e, Object state) {
        }

        @Override
        public void onConnectTimeoutException(ConnectTimeoutException arg0,
                Object arg1) {

        }

        @Override
        public void onSocketTimeoutException(SocketTimeoutException arg0,
                Object arg1) {
        }

        @Override
        public void onUnknowException(Exception arg0, Object arg1) {
        	
        	NotificationManager notificationManager = (NotificationManager) mxcontext
					.getSystemService(Context.NOTIFICATION_SERVICE);
			Notification notification = new Notification();
			PendingIntent contentIntent = PendingIntent.getActivity(
					(Activity) mxcontext, 0, new Intent(), 0);
			notification.setLatestEventInfo((Activity) mxcontext, null, null,
					contentIntent);
			notification.icon = R.drawable.notification_qzone_icon;
			notification.tickerText = TextUtil
					.getString(R.string.shareactivity_sina_fail);
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			notificationManager.notify(333333, notification);
			notificationManager.cancel(333333);
        }

        @Override
        public void onHttpStatusException(HttpStatusException arg0, Object arg1) {
        }

        @Override
        public void onNetworkUnavailableException(NetworkUnavailableException arg0, Object arg1) {
        }
    }
	
	public static onTencentQZResult onTencentQZResult;
	
	public interface onTencentQZResult{
	
		public void onTencentQZResult_back(String texturl);
	
	}
}
