package com.letv.watchball.utils;

import android.util.Log;

public class LogInfo {
	private final static boolean isDebug = LetvConfiguration.isDebug();
	private static String TAG = "haitian";
	private final static int DEBUG_LEVEL = 4;
	public static void log(String msg){
		if(isDebug){
			Log.e(TAG, msg);
		}
	}
	public static void logd(String msg){
		if(isDebug){
			Log.d(TAG, msg);
		}
	}
	public static void logw(String msg){
		if(isDebug){
			Log.w(TAG, msg);
		}
	}
	
	public static void log(String Tag,String msg){
		if(isDebug){
			switch (DEBUG_LEVEL) {
			case 0:
				Log.i(Tag, msg);
				break;
			case 1:
				Log.d(Tag, msg);
				break;
			case 2:
				Log.v(Tag, msg);
				break;
			case 3:
				Log.w(Tag, msg);
				break;
			case 4:
				Log.e(Tag, msg);
				break;
			default:
				Log.i(Tag, msg);
				break;
			}
		}
	}
	
}
