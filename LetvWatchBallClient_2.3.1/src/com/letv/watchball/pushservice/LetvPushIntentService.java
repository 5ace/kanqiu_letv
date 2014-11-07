package com.letv.watchball.pushservice;

import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letv.android.lcm.LetvPushBaseIntentService;
import com.letv.watchball.LetvApplication;
import com.letv.watchball.R;
import com.letv.watchball.activity.MainActivity;
import com.letv.watchball.activity.WelcomeActivity;
import com.letv.watchball.bean.MatchResult;
import com.letv.watchball.bean.PushMsgBean;
import com.letv.watchball.bean.PushSubscribeGame;
import com.letv.watchball.db.DBManager;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.push.LetvWbPushService;
import com.letv.watchball.push.PushNotificationReceiver;
import com.letv.watchball.utils.LetvLogTool;
import com.letv.watchball.utils.LetvUtil;
import com.umeng.analytics.MobclickAgent;

public class LetvPushIntentService extends LetvPushBaseIntentService{

	public LetvPushIntentService(String name) {
		super(name);
	}

	private static final String TAG = "LetvPushIntentService";
	public LetvPushIntentService() {
		super(TAG);
	}
	private PushSubscribeGame mPushSubscribeGame;

	@Override
	protected void onMessage(Context context, String message,String code,String serdId) {
		Log.d(TAG,"message="+message+" code="+code+" senderId="+serdId+" packagename="+context.getPackageName());
		
		updateContent(context, "message:"+ message+"\n"+"serdId:"+serdId +"\n"+"packageName:"+context.getPackageName());
		/*
		MatchResult result = null; //message序列化成result
		if (PreferencesManager.getInstance().isGameResultRemind() && LetvUtil.sleepAlarm()) {
            String notiTitle = getApplicationContext().getResources().getString(R.string.letvpushservice_live_title_result);
            String notiContent = mPushSubscribeGame.home + "VS" + mPushSubscribeGame.guest + "  " + ",比分 " + result.body.homeScore + "：" + result.body.guestScore;

            String historyId = PreferencesManager.getInstance().getGameId();
            String vid = mPushSubscribeGame.id;
            long gamePushId = Long.parseLong(mPushSubscribeGame.id);
            if (!historyId.equals(vid)) {

                  MobclickAgent.onEvent(LetvPushIntentService.this, "push_success");

                  Intent intent = new Intent(LetvPushIntentService.this, MainActivity.class);
                  intent.setAction(PushNotificationReceiver.NOTIFY);
                  PendingIntent pendingIntent = PendingIntent.getActivity(LetvPushIntentService.this, (int) gamePushId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                  NotificationManager notificationManager = (NotificationManager) LetvPushIntentService.this.getSystemService(Context.NOTIFICATION_SERVICE);
                  Notification notification = new Notification();
                  notification.icon = R.drawable.notify_icon;
                  notification.tickerText = notiTitle;
                  notification.flags = Notification.FLAG_AUTO_CANCEL;
                  notification.defaults |= Notification.DEFAULT_SOUND;
                  notification.setLatestEventInfo(context, notiTitle, notiContent, pendingIntent);
                  notificationManager.notify((int) gamePushId, notification);

                  PreferencesManager.getInstance().setGameId(vid);
                  DBManager.getInstance().getSubscribeGameTrace().updatePush(mPushSubscribeGame.id, true);

                  LetvLogTool.getInstance().log("\r\n\r\n\r\n" + "time:" + LetvUtil.timeFormat(System.currentTimeMillis()) + "\r\n");
                  LetvLogTool.getInstance().log("notifycationId:" + vid + "notiTitle:" + notiTitle + "\r\n" + "notiContent:" + notiContent);
            }
		}*/
		showMsgNotification(LetvApplication.getInstance(), message);
	}
	private void showMsgNotification(Context context, String dataString){
		NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);               
		Notification n = new Notification(R.drawable.ic_launcher, "点击查看详情", System.currentTimeMillis());      
		n.defaults |= Notification.DEFAULT_LIGHTS; 
		//n.defaults |= Notification.DEFAULT_VIBRATE; 
		n.defaults |= Notification.DEFAULT_SOUND;
		
		n.flags |= Notification.FLAG_AUTO_CANCEL;
		n.flags |= Notification.FLAG_SHOW_LIGHTS;
		Intent i = new Intent(context, WelcomeActivity.class);
		i.putExtra("notification", "true");
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		Bundle bundle = new Bundle();
		i.putExtras(bundle);
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		PushMsgBean response = null;
		try {
			response = (PushMsgBean)mapper.readValue(dataString, PushMsgBean.class);
			
			//PendingIntent
			PendingIntent contentIntent = PendingIntent.getActivity(context,0,i,0);
			String msgTxt = "乐视看球";
			String detailTxt = "点击查看详情";
			if (response != null) {
				detailTxt = response.getBody().getPushmsg().getMsg();
			}
			n.setLatestEventInfo(context, msgTxt, detailTxt, contentIntent);
			nm.notify(12156, n);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * @param context		
	 * @param deviceToken
	 * 	device token changed, application may update the shared preference and
	 *  send the new device token to app server.
	 */
	@Override
	protected void onTokenChanged(Context context, String deviceToken) {
		Log.d(TAG, "token changed=" + deviceToken);
		
		updateContent(context, deviceToken);
	}

	private void updateContent(final Context context, final String responseString) {
		Log.i("Letv push", "response:"+responseString);
		return;
		/*
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(context.getApplicationContext(), responseString, Toast.LENGTH_LONG).show();
				Looper.loop();
			}
		}).start();
		*/
	}

}
