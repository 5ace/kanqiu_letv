package com.letv.watchball.utils;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.letv.http.bean.LetvDataHull;
import com.letv.http.parse.LetvGsonParser;
import com.letv.watchball.async.LetvHttpAsyncTask;
import com.letv.watchball.bean.Game;
import com.letv.watchball.bean.PushSubscribeGame;
import com.letv.watchball.bean.SubscribeList;
import com.letv.watchball.db.DBManager;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.receiver.LetvLiveReceiver;

public class LetvSubsribeGameUtil {

	/**
	 * 创建闹钟，预定闹钟
	 * */
	public static boolean createClock(Context context) {
		long time = DBManager.getInstance().getSubscribeGameTrace()
				.getNearestTrace();
		time = time - System.currentTimeMillis();
		int remindAhead = PreferencesManager.getInstance().getGameStartRemind();
		if (remindAhead > 0 && time > 0) {
			time -= remindAhead * 60 * 1000;
			Intent intent = new Intent(context, LetvLiveReceiver.class);
			PendingIntent pi = PendingIntent.getBroadcast(context,
					LetvConstant.LETV_LIVEBOOK_CODE, intent, 0);
			AlarmManager am = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			am.set(AlarmManager.RTC_WAKEUP, time, pi);
			return true;
		}
		return false;
	}

	/**
	 * 关闭闹钟
	 * */
	public static void closeClock(Context context) {
		Intent intent = new Intent(context, LetvLiveReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context,
				LetvConstant.LETV_LIVEBOOK_CODE, intent, 0);
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		am.cancel(pi);
	}

	/**
	 * 刷新闹钟
	 * 
	 * @param context
	 */
	public static void updateClock(Context context) {
		closeClock(context);
		createClock(context);
	}

	/**
	 * 从服务器更新数据
	 * */
	public static void updateSubsribeGames(Context context) {
		if (!PreferencesManager.getInstance().isUpdateSubscribeGame()) {
			new LetvHttpAsyncTask<SubscribeList>(context, false) {
				@Override
				public LetvDataHull<SubscribeList> doInBackground() {
					// TODO 预约提醒
					return LetvHttpApi.requestMatchesRemind(0,
							new LetvGsonParser<SubscribeList>(0,
									SubscribeList.class));
				}

				@Override
				public void onPostExecute(int updateId, SubscribeList result) {
					// if(result != null) {
					// PreferencesManager.getInstance().setIsLiveRemind(result.isBookOpen());
					// }
					if (null != result && null != result.body)
						comparisonSubscribeGames(context, result.body);
					PreferencesManager.getInstance().setIsUpdateSubscribeGame(
							true);
				}
			}.start();
		}
	}

	/**
	 * 更新预约列表到数据库，并刷新闹钟
	 * 
	 * @param context
	 * @param bodys
	 */
	public static void comparisonSubscribeGames(Context context, Game[] bodys) {

		if (bodys != null) {

			closeClock(context);

			ArrayList<String> tempMd5s = new ArrayList<String>();

			for (int i = 0; i < bodys.length; i++) {
				Game mBody = bodys[i];
				// String md5_id = MD5.toMd5(mLiveBookProgram.getProgramName() +
				// mLiveBookProgram.getChannelName() +
				// mLiveBookProgram.getCode() +
				// mLiveBookProgram.getPlay_time());
				tempMd5s.add(mBody.id);
				PushSubscribeGame mPushSubscribeGame = new PushSubscribeGame();
				mPushSubscribeGame.id = mBody.id;
				mPushSubscribeGame.home = mBody.home;
				mPushSubscribeGame.guest = mBody.guest;
				mPushSubscribeGame.level = mBody.level;
				mPushSubscribeGame.playDate = mBody.playDate;
				mPushSubscribeGame.playTime = mBody.playTime;
				mPushSubscribeGame.status = mBody.status;

				// mPushSubscribeGame.playDate = "09月26日 周三";
				// mPushSubscribeGame.playTime =
				// LetvUtil.timeFormat(System.currentTimeMillis() + i*5*60*1000,
				// "HH:mm");
				DBManager.getInstance().getSubscribeGameTrace()
						.saveSubscribeGameTrace(mPushSubscribeGame);
			}

			ArrayList<PushSubscribeGame> pushSubscribeGames = DBManager
					.getInstance().getSubscribeGameTrace().getAllTrace();
			if (pushSubscribeGames != null && pushSubscribeGames.size() > 0) {
				for (int i = 0; i < pushSubscribeGames.size(); i++) {
					if (!tempMd5s.contains(pushSubscribeGames.get(i).id)) {
						DBManager.getInstance().getSubscribeGameTrace()
								.remove(pushSubscribeGames.get(i).id);
					}
				}
			}

			createClock(context);
		}
	}

	/**
	 * 添加一条预约提醒闹钟
	 * 
	 * @param context
	 * @param body
	 */
	public static void SubscribeGameProgram(Context context,
			PushSubscribeGame body) {
		DBManager.getInstance().getSubscribeGameTrace()
				.saveSubscribeGameTrace(body);

		createClock(context);
	}

	/**
	 * 取消一条预约提醒闹钟
	 * 
	 * @param context
	 * @param id
	 */
	public static void cancelSubscribeGameProgram(Context context, String id) {
		DBManager.getInstance().getSubscribeGameTrace().remove(id);

		// createClock(context);
	}
}
