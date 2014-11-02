package com.letv.watchball.pushservice;

import com.letv.android.lcm.LetvPushWakefulReceiver;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;


public class LePushMessageReceiver extends LetvPushWakefulReceiver{
	
	@Override
	public void onReceive(Context context, Intent intent) {
		 // Explicitly specify that LetvPushIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),
        		LetvPushIntentService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
	}
}
