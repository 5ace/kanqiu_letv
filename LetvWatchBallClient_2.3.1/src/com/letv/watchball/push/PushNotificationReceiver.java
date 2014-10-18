package com.letv.watchball.push;
/**
 * 推送广播接收器
 * */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.letv.watchball.LetvApplication;
import com.letv.watchball.activity.MainActivity;

public class PushNotificationReceiver extends BroadcastReceiver {


      public static final String NOTIFY = "PushNotificationReceiver.NOTIFY";

	@Override
	public void onReceive(Context context, Intent intent) {
//		if(LetvApplication.getInstance().isForceUpdating()){
//			return ;
//		}
		try {
			if(MainActivity.getInstance() != null){
		    	MainActivity.launch(MainActivity.getInstance());
			} else {
				//跳转首页
		    	MainActivity.launch(context);
		    }
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
