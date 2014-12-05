package com.letv.watchball.receiver;

import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.letv.watchball.R;
import com.letv.watchball.activity.MainActivity;
import com.letv.watchball.bean.PushSubscribeGame;
import com.letv.watchball.db.DBManager;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.push.LetvWbPushService;
import com.letv.watchball.push.PushNotificationReceiver;
import com.letv.watchball.utils.LetvLogTool;
import com.letv.watchball.utils.LetvSubsribeGameUtil;
import com.letv.watchball.utils.LetvUtil;

public class LetvLiveReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent in) {

		if (PreferencesManager.getInstance().getGameStartRemind() > 0
				&& LetvUtil.sleepAlarm()) {
			ArrayList<PushSubscribeGame> list = DBManager.getInstance()
					.getSubscribeGameTrace().getCurrentTrace();
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {

					String liveNtificationGameId = PreferencesManager
							.getInstance().getLiveNtificationGameId();
					PushSubscribeGame mPushSubscribeGame = list.get(i);
					DBManager.getInstance().getSubscribeGameTrace()
							.updateNotity(mPushSubscribeGame.id, true);

					if (mPushSubscribeGame.id.equals(liveNtificationGameId)) {
						continue;
					}

					String notiTitle = context.getResources().getString(
							R.string.letvpushservice_live_title);
					String notiContent = mPushSubscribeGame.home + "VS"
							+ mPushSubscribeGame.guest + "  "
							+ mPushSubscribeGame.playTime;
					Intent intent = new Intent(context, MainActivity.class);
					intent.setAction(PushNotificationReceiver.NOTIFY);
					int codeToInt = LetvUtil.codeToInt(mPushSubscribeGame.id);
					PendingIntent pendingIntent = PendingIntent.getActivity(
							context, codeToInt, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);
					NotificationManager notificationManager = (NotificationManager) context
							.getSystemService(Context.NOTIFICATION_SERVICE);
					Notification notification = new Notification();
					notification.icon = R.drawable.notify_icon;
					notification.tickerText = notiTitle;
					notification.flags = Notification.FLAG_AUTO_CANCEL;
					notification.defaults |= Notification.DEFAULT_SOUND;
					notification.setLatestEventInfo(context, notiTitle,
							notiContent, pendingIntent);
					notificationManager.notify(codeToInt, notification);

					// notification(context, notiTitle, notiContent);
					PreferencesManager.getInstance().setLiveNtificationGameId(
							mPushSubscribeGame.id);

					LetvLogTool.getInstance().log(
							"\r\n\r\n\r\n"
									+ "time:"
									+ LetvUtil.timeFormat(System
											.currentTimeMillis()) + "\r\n");
					LetvLogTool.getInstance().log(
							"notifycationId:" + codeToInt + "notiTitle:"
									+ notiTitle + "\r\n" + "notiContent:"
									+ notiContent);
				}
			}

			LetvSubsribeGameUtil.createClock(context);
			/**
			 * 设置赛果轮询
			 */
			// if(PreferencesManager.getInstance().isGameResultRemind()){
			// LetvPushService.schedule(context);
			// } else {
			// LetvPushService.unschedule(context);
			// }
		}
	}

	// public static void notification(Context context,String notiTitle,String
	// notiContent){
	// LetvLogTool.getInstance().log("\r\n\r\n\r\n"+"time:"+LetvUtil.timeFormat(System.currentTimeMillis())+"\r\n");
	// LetvLogTool.getInstance().log("notiTitle:"+"notiTitle"+"\r\n"+"notiContent:"+notiContent);
	// Intent intent= new Intent(context , PushNotificationReceiver.class);
	// // intent.putExtra("at", 3);
	// // intent.putExtra("channelName", mPushSubscribeGame.getChannelName());
	// // intent.putExtra("url", mPushSubscribeGame.getUrl());
	// // intent.putExtra("url_350", mPushSubscribeGame.getUrl_350());
	// // intent.putExtra("code", mPushSubscribeGame.getCode());
	// // intent.putExtra("programName", mPushSubscribeGame.getProgramName());
	// PendingIntent pendingIntent = PendingIntent.getActivity(context ,10,
	// intent, PendingIntent.FLAG_UPDATE_CURRENT);
	// NotificationManager notificationManager =
	// (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
	// Notification notification = new Notification() ;
	// notification.icon = R.drawable.icon ;
	// notification.tickerText = notiTitle;
	// notification.flags = Notification.FLAG_AUTO_CANCEL ;
	// notification.defaults |= Notification.DEFAULT_SOUND;
	// notification.setLatestEventInfo(context,notiTitle,notiContent,pendingIntent);
	// int codeToInt = (int) (Math.random()*10000);
	// // int codeToInt = 100;
	// // System.out.println("codeToInt:" + codeToInt);
	// // System.out.println("mPushSubscribeGame.getCode():" +
	// mPushSubscribeGame.getCode());
	// // System.out.println("mPushSubscribeGame.getProgramName():" +
	// mPushSubscribeGame.getProgramName());
	// notificationManager.notify(codeToInt , notification);
	// }

}
