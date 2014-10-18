package com.letv.watchball.pip;

import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.letv.watchball.bean.Game.LiveTs;
import com.letv.watchball.service.PipService;
import com.letv.watchball.ui.PlayLiveController;
import com.letv.watchball.ui.impl.BasePlayActivity;

public class LetvPipPlayFunction {

	public static void launchPlay(Context context, Bundle bundle) {
		PipService.launch(context, bundle);
	}

	/**
	 * 关闭画中画
	 * 
	 * @param context
	 */
	public static void closePipView(Context context) {

		if (null != context && PipServiceIsStart(context)) {
			Intent serviceStop = new Intent();
			serviceStop.setClass(context, PipService.class);
			context.stopService(serviceStop);
		}
	}

	/**
	 * 判断Mini播放器服务是否开启
	 * 
	 * @return
	 */
	public static boolean PipServiceIsStart(Context context) {
		String className = "com.letv.watchball.service.PipService";

		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		List<ActivityManager.RunningServiceInfo> mServiceList = mActivityManager
				.getRunningServices(Integer.MAX_VALUE);

		for (int i = 0; i < mServiceList.size(); i++) {

			if (className.equals(mServiceList.get(i).service.getClassName())) {
				return true;
			}
		}
		return false;

	}

	/**
	 * 从画中画跳转至主播放器
	 * 
	 * @param context
	 * @param bundle
	 */
	public static void pipToMainPlayer(Context context, Bundle bundle, boolean isDownload) {
//        String localPath = bundle.getString("url");
		if (bundle.getBoolean("isLive")) {
			String code = bundle.getString(PlayLiveController.LIVE_CODE);
			String url = bundle.getString(PlayLiveController.LIVE_URL);
			String streamId = bundle.getString(PlayLiveController.LIVE_STREAMID);
			long aid = bundle.getLong(PlayLiveController.AID, 0);
			long vid = bundle.getLong(PlayLiveController.VID, 0);
			BasePlayActivity.launchLives(context, code, streamId, url, aid, vid,null,true);
		}else{
			BasePlayActivity.launch(context, bundle.getLong("aid", 0), bundle.getLong("vid", 0),true);
		}
//		/**
//    	 * 跳转本地播放
//    	 */
//		if(bundle.getInt("launch_mode") == PlayController.PLAY_DEFAULT) {
//        	BasePlayActivity.launch(context, localPath, 1,bundle.getLong("seek"));
//        	return;
//        }
		
		//2013/11/21 ljn update 修复从下载完成的视频进入全屏进入小窗再返回的问题，现在的逻辑是：
		//1、小窗播放的是下载的视频，返回进入全屏播放
		//2、小窗播放的非下载的视频，返回进入半屏播放
//		if ((PlayController.PLAY_DOWNLOAD == bundle.getInt("launch_mode", 0)) && isDownload) {
//			BasePlayActivity.launchDownload(context, bundle.getLong("aid", 0), bundle.getLong("vid", 0));
//		} else {
//		}
	}

//	public static void pipToMainLocalVideoPlayer(Context context, Bundle bundle) {
////		LocalPlayerActivity.launch(context, bundle);
//	}
}
