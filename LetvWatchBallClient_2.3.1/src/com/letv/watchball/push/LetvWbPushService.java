package com.letv.watchball.push;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.letv.http.bean.LetvDataHull;
import com.letv.http.parse.LetvGsonParser;
import com.letv.watchball.R;
import com.letv.watchball.activity.MainActivity;
import com.letv.watchball.async.LetvHttpAsyncTask;
import com.letv.watchball.bean.MatchResult;
import com.letv.watchball.bean.PushSubscribeGame;
import com.letv.watchball.db.DBManager;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.utils.LetvLogTool;
import com.letv.watchball.utils.LetvUtil;
import com.umeng.analytics.MobclickAgent;

public class LetvWbPushService extends Service {

	private static final String TAG = LetvWbPushService.class.getSimpleName();
	
//	RetrieveTask task;
	WakeLock mWakeLock;

      Handler mPushHandler = new Handler(){


            @Override
            public void handleMessage(Message msg) {
                  switch (msg.what){
                        case 0:
                              ArrayList<PushSubscribeGame> list = DBManager.getInstance().getSubscribeGameTrace().getAllSubscribeGamesTrace();
                              executeRequest(list);
                              break;
                        default:
                              break;
                  }


            }
      };
      private long SCHEDULE_TIME = 60*10*1000;


      @Override
	public void onCreate() {
		
		super.onCreate();
		Log.d("smy", "LetvPushService:"+"onCreate");
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		mWakeLock.acquire();
		

//		PushSubscribeGame mPushSubscribeGame = new PushSubscribeGame();
//		mPushSubscribeGame.id = "7dd9f5eb-c886-4355-baab-032f20cfd771";
//		mPushSubscribeGame.home = "home";
//		mPushSubscribeGame.guest = "guest";
//		new RetrieveTask(this, mPushSubscribeGame).start();7
	}

      @Override
      public void onStart(Intent intent, int startId) {
            mPushHandler.sendEmptyMessageDelayed(0,SCHEDULE_TIME);
      }

      @Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		
//		if(!task.isCancelled()){
//			task.cancel(true);
//		}
		
		mWakeLock.release();
		
		super.onDestroy();
	}
	
	public static void schedule(Context context){
		if(!PreferencesManager.getInstance().isGameResultRemind()){
			return ;
		}
            Intent intent = new Intent();
            intent.setClass(context.getApplicationContext(),LetvWbPushService.class);
            context.getApplicationContext().startService(intent);




		//更新间隔
//		int interval = 30;
//
//		Intent intent = new Intent(context,LetvWbPushService.class);
//		PendingIntent pending = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//		Calendar c = new GregorianCalendar();
////		c.add(Calendar.SECOND, interval);
//		c.add(Calendar.MINUTE, interval);
//
//		AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//		alarm.cancel(pending);
//
//		alarm.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pending);
		
		

//		//更新间隔
//		int interval = PreferencesManager.getInstance().getPushDistance() ;
//		long historyTime = PreferencesManager.getInstance().getPushTime() ;
//		
//		
//		Intent intent = new Intent(context,LetvPushService.class);
//		PendingIntent pending = PendingIntent.getService(context, 15210123, intent, PendingIntent.FLAG_CANCEL_CURRENT);
////		Calendar c = new GregorianCalendar();
////		c.add(Calendar.SECOND, interval);
//		
//		AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//		
//		alarm.cancel(pending);
//		alarm.set(AlarmManager.RTC_WAKEUP, (historyTime + interval * 1000), pending);
	
	}
	
	public static void unschedule(Context context){
//		Intent intent = new Intent(context, LetvWbPushService.class);
//		PendingIntent pending = PendingIntent.getService(context, 0, intent, 0);
//		AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//		alarm.cancel(pending);

            Intent intent = new Intent();
            intent.setClass(context.getApplicationContext(),LetvWbPushService.class);
            context.getApplicationContext().stopService(intent);
	}
	
	int count = 0;
	public void executeRequest(final ArrayList<PushSubscribeGame> list){



                  if(list.size() == 0){
//                        Log.d("smy", "list.size():"+list.size()+"count:"+count);
                        schedule(LetvWbPushService.this);
                  }else{
                        count = 0;
			Runnable callback = new Runnable() {
				
				@Override
				public void run() {
					count++;
					if(count == list.size()){
                                    count = 0;
                                    schedule(LetvWbPushService.this);
                              }
                        }
			};
			for (int i = 0; i<list.size();i++) {
//                        Log.d("smy", "list.size():"+list.size()+"count:"+count);
                        new RetrieveTask(this,list.get(i), callback).start();
                  }
		}
		
	}


	class RetrieveTask extends LetvHttpAsyncTask<MatchResult>{
		private PushSubscribeGame mPushSubscribeGame;
		private Runnable callback;
		public RetrieveTask(Context context,PushSubscribeGame mPushSubscribeGame,Runnable callback) {
			super(context);
			this.mPushSubscribeGame = mPushSubscribeGame;
			this.callback = callback;
		}

		@Override
		public LetvDataHull<MatchResult> doInBackground() {
//			long historyTime = PreferencesManager.getInstance().getPushTime() ;
//			long curTime = System.currentTimeMillis() ;
//			if(curTime - historyTime >= 29 * 60 * 1000){
////				long historyId = PreferencesManager.getInstance().getPushId() ;
//				PreferencesManager.getInstance().savePushTime(curTime);
//				return LetvHttpApi.requestMatchInfo("951b5898-9f41-4e51-91de-be2136bf9507", new LetvGsonParser<MatchResult>(0, MatchResult.class));
//			}
			
			return LetvHttpApi.requestMatchInfo(mPushSubscribeGame.id, new LetvGsonParser<MatchResult>(0, MatchResult.class));
//			long historyTime = PreferencesManager.getInstance().getPushTime() ;
//			long curTime = System.currentTimeMillis() ;
//			int distance = PreferencesManager.getInstance().getPushDistance();
//			if(curTime - historyTime >= (distance - 10) * 1000){
//				PreferencesManager.getInstance().savePushTime(curTime);
////			return LetvHttpApi.requestMatchInfo("7dd9f5eb-c886-4355-baab-032f20cfd771", new LetvGsonParser<MatchResult>(0, MatchResult.class));
//				return LetvHttpApi.requestMatchInfo(mPushSubscribeGame.id, new LetvGsonParser<MatchResult>(0, MatchResult.class));
//			}
//			return null;
		}

		@Override
		public void onPostExecute(int updateId, MatchResult result) {
//			if(null != result){
//				LetvLiveReceiver.notification(context, notiTitle, notiContent);
//				DBManager.getInstance().getSubscribeGameTrace().update(mPushSubscribeGame.id,true);
//			}
                  synchronized (this) {
                        Log.d("smy", "onPostExecute(int updateId, MatchResult result)");
                        callback.run();
                        if (null != result && null != result.body) {
                              if (PreferencesManager.getInstance().isGameResultRemind() && LetvUtil.sleepAlarm()) {
                                    String notiTitle = context.getResources().getString(R.string.letvpushservice_live_title_result);
                                    String notiContent = mPushSubscribeGame.home + "VS" + mPushSubscribeGame.guest + "  " + ",比分 " + result.body.homeScore + "：" + result.body.guestScore;
//                              Log.d("smy", "callback run");
                                    String historyId = PreferencesManager.getInstance().getGameId();
                                    String vid = mPushSubscribeGame.id;
                                    long gamePushId = Long.parseLong(mPushSubscribeGame.id);
                                    if (!historyId.equals(vid)) {

                                          MobclickAgent.onEvent(LetvWbPushService.this, "push_success");

                                          Intent intent = new Intent(LetvWbPushService.this, MainActivity.class);
                                          intent.setAction(PushNotificationReceiver.NOTIFY);
                                          PendingIntent pendingIntent = PendingIntent.getActivity(LetvWbPushService.this, (int) gamePushId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                          NotificationManager notificationManager = (NotificationManager) LetvWbPushService.this.getSystemService(Context.NOTIFICATION_SERVICE);
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
                              }
                        }

                  }

		}
		
		public void preFail(){
			callback.run();
		}
		
		public void netNull() {
			callback.run();
		};

		public void netErr(int updateId, int errMsg) {
			callback.run();
		};

		public void dataNull(int updateId, int errMsg) {
			callback.run();
		};
	}
}
