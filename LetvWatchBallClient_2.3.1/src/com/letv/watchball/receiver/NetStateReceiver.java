package com.letv.watchball.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;

import com.letv.watchball.activity.MainActivity;
import com.letv.watchball.utils.LetvSubsribeGameUtil;
import com.letv.watchball.utils.NetWorkTypeUtils;

public class NetStateReceiver extends BroadcastReceiver {

	Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;

		if (MainActivity.getInstance() == null) {
			return;
		}

		NetworkInfo networkInfo = NetWorkTypeUtils.getAvailableNetWorkInfo();
		if (networkInfo != null) {
			System.out.println("++++--->>"+ context.getClass());
			
			LetvSubsribeGameUtil.updateSubsribeGames(context);
		}
	}
}
