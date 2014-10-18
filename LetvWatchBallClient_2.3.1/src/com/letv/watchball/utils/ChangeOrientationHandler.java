package com.letv.watchball.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

public class ChangeOrientationHandler extends Handler {

	public static final int ORIENTATION_8 = 1;
	public static final int ORIENTATION_9 = 2;
	public static final int ORIENTATION_0 = 3;
	public static final int ORIENTATION_1 = 4;

	private Activity activity;

	public ChangeOrientationHandler(Activity ac) {
		super();
		activity = ac;
	}

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case ORIENTATION_8:// 反横屏
			activity.setRequestedOrientation(8);
			break;
		case ORIENTATION_9:// 反竖屏
			activity.setRequestedOrientation(9);
			break;
		case ORIENTATION_0:// 正横屏
			activity.setRequestedOrientation(0);
			break;
		case ORIENTATION_1:// 正竖屏
			activity.setRequestedOrientation(1);
			break;
		}

		super.handleMessage(msg);
	}

}
