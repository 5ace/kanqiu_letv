package com.letv.watchball.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.push.LetvWbPushService;
import com.letv.watchball.utils.LetvSubsribeGameUtil;

public class BootCompletedBroadcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
			/**
			 * 开启预约提醒闹钟
			 */
			LetvSubsribeGameUtil.createClock(context);
			/**
			 * 设置赛果轮询
			 */
			if (PreferencesManager.getInstance().isGameResultRemind()) {
				LetvWbPushService.schedule(context);
			} else {
				LetvWbPushService.unschedule(context);
			}
		}
	}
}
