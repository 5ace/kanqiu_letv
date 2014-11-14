package com.letv.watchball.pushservice;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.letv.android.lcm.LetvPushBaseIntentService;
import com.letv.watchball.LetvApplication;
import com.letv.watchball.R;
import com.letv.watchball.activity.LetvWebViewActivity;
import com.letv.watchball.activity.WelcomeActivity;
import com.letv.watchball.bean.PushMsgBean;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.ui.impl.BasePlayActivity;

public class LetvPushIntentService extends LetvPushBaseIntentService {

	private Notification mNotification;

	public LetvPushIntentService(String name) {
		super(name);
	}

	private static final String TAG = "LetvPushIntentService";

	public LetvPushIntentService() {
		super(TAG);
	}

	@Override
	protected void onMessage(Context context, String message, String code,
			String serdId) {
		Log.e("gongmeng", "message=" + message + " code=" + code + " senderId="
				+ serdId + " packagename=" + context.getPackageName());

		showMsgNotification(LetvApplication.getInstance(), message);
	}

	// 解析传递过来的json数据
	private String getString(JSONObject jsonObject, String name)
			throws JSONException {
		String value = "";
		if (jsonObject.has(name) == false) {
			return value;
		}
		value = jsonObject.getString(name);
		if ("null".equalsIgnoreCase(value)) {
			value = "";
		}
		return value;
	}

	private PushMsgBean getPushBean(String dataString) {
		try {
			PushMsgBean p = new PushMsgBean();
			JSONObject data = new JSONObject(dataString);
			Log.e("gongmeng", "JSON object :" + data.toString());
			p.setAt(getString(data, "at"));
			p.setCid(getString(data, "cid"));
			p.setId(getString(data, "id"));
			p.setIsActivate(getString(data, "isActivate"));
			p.setIsOnDeskTop(getString(data, "isOnDeskTop"));
			p.setLiveEndDate(getString(data, "liveEndDate"));
			p.setMsg(getString(data, "msg"));
			p.setNeedJump(getString(data, "needJump"));
			p.setPicUrl(getString(data, "picUrl"));
			p.setResid(getString(data, "resid"));
			p.setTime(getString(data, "time"));
			p.setTitle(getString(data, "title"));
			p.setType(getString(data, "type"));
			return p;
		} catch (JSONException e) {
			Log.e("gongmeng", "gongmeng" + e.getStackTrace().toString());
		} catch (Exception e) {
			Log.e("gongmeng", "gongmeng" + e.getStackTrace().toString());
		}
		return null;

	}

	// 同步加载图片，本身是挂起的service所以不需要使用异步处理方法
	public Bitmap returnBitMap(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try {
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			Log.e("gongmeng", "bitmap is null");
		}
		return bitmap;
	}

	private void showMsgNotification(Context context, String dataString) {
		if (!PreferencesManager.getInstance().isPushservice()) {
			Log.e("gongmeng", "pushservice is false");
			return;
		}

		PushMsgBean pushMsgBean = getPushBean(dataString);

		if (pushMsgBean == null) {
			Log.e("gongmeng", "pushmsgbean is null");
			return;
		}

		// 自定义通知中心的样式

		RemoteViews contentView = new RemoteViews(this.getPackageName(),
				R.layout.notify_view);
		contentView.setTextViewText(R.id.push_text, pushMsgBean.getMsg());
		//contentView.setLong(R.id.notify_time, "setTime",
		//		System.currentTimeMillis());
		if (pushMsgBean.getPicUrl() != null) {
			try {
				contentView.setImageViewBitmap(R.id.push_icon,
						returnBitMap(pushMsgBean.getPicUrl()));

			} catch (Exception e) {
				contentView.setImageViewResource(R.id.push_icon,
						R.drawable.notify_icon);
			}
		} else {
			contentView.setImageViewResource(R.id.push_icon,
					R.drawable.notify_icon);
		}

		// 自定义点击事件
		Intent intent;
		if (pushMsgBean.getType() != null) {
			switch(Integer.valueOf(pushMsgBean.getType())){
				case 1:

				case 2:
				case 3:
					//播放单个视频或者专辑
					intent = new Intent(context, BasePlayActivity.class);
					//LAUNCH_MODE_VIDEO = 3 
					intent.putExtra("launchMode", 3);					
					intent.putExtra("vid", Integer.valueOf(pushMsgBean.getResid()));
					intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					if (!(context instanceof Activity)) {
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					}
					break;
				case 5:
					//打开一个直播页
					intent = new Intent(context, BasePlayActivity.class);
					//LAUNCH_MODE_LIVE_FULL = 5 
					intent.putExtra("launchMode", 5);
					intent.putExtra("vid", Integer.valueOf(pushMsgBean.getResid()));
					
					intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					if (!(context instanceof Activity)) {
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					}
					break;
				case 6:
					//打开一个url
					intent = new Intent(context, LetvWebViewActivity.class);
					intent.putExtra("url", pushMsgBean.getResid());
					intent.putExtra("loadType", pushMsgBean.getTitle());
					intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					if (!(context instanceof Activity)) {
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					}
					break;
				default:
					intent = new Intent(context, BasePlayActivity.class);
					//LAUNCH_MODE_VIDEO = 3 
					intent.putExtra("launchMode", 3);
					intent.putExtra("aid", Integer.valueOf(pushMsgBean.getCid()));
					intent.putExtra("vid", Integer.valueOf(pushMsgBean.getResid()));
//					intent.putExtra("from", from);
					intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					if (!(context instanceof Activity)) {
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					}
					break;

			}
		} else {
			intent = new Intent(this, WelcomeActivity.class);
			intent.putExtra("notification", "true");
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);
			Bundle bundle = new Bundle();
			intent.putExtras(bundle);
		}
		/*
		 * try {
		 * 
		 * if(pushMsgBean.getType() == null){
		 * 
		 * 
		 * 
		 * } switch(Integer.valueOf(pushMsgBean.getType())){ case 1: break; case
		 * 2: case 3: break; case 5: break; case 6: break;
		 * 
		 * default: break; }
		 * 
		 * } catch (Exception e) { Log.e("gongmeng",
		 * e.getStackTrace().toString()); }
		 */
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

		// 创建通知并发布
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		mNotification = new Notification(R.drawable.notify_icon,
				pushMsgBean.getMsg(), System.currentTimeMillis());

		mNotification.defaults |= Notification.DEFAULT_LIGHTS;
		mNotification.defaults |= Notification.DEFAULT_SOUND;
		mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
		mNotification.flags |= Notification.FLAG_SHOW_LIGHTS;

		mNotification.contentIntent = contentIntent;
		mNotification.contentView = contentView;
		// TODO 这里使用了老的代码中qqzone统治的ID，应当建立文档对这个通知ID进行维护
		notificationManager.notify(3331, mNotification);
	}

	/**
	 * @param context
	 * @param deviceToken
	 *            device token changed, application may update the shared
	 *            preference and send the new device token to app server.
	 */
	@Override
	protected void onTokenChanged(Context context, String deviceToken) {
		Log.d(TAG, "token changed=" + deviceToken);
		final SharedPreferences prefs = getSharedPreferences("device_token",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("registration_id", deviceToken);
		editor.commit();
	}

}
