package com.letv.watchball.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import com.letv.watchball.R;
import com.letv.watchball.async.LetvAsyncTask;
import com.letv.watchball.pip.PipLiveChannelPlayController;
import com.letv.watchball.pip.PipPlayAlbumController;
import com.letv.watchball.pip.PipPlayController;

/**
 * 悬浮窗的view，控制的逻辑全在PipPlayController中
 * 
 * @author
 * 
 */
public class PipPlayerView extends LinearLayout {

	private StartPlayAsync startPlayAsync;
	private PipPlayController mPipPlayController;
	private Bundle mBundle;
	private IntentFilter lockScreenIntentFilter = null; // 监控锁屏与解锁过滤器

	public PipPlayerView(Context context) {
		super(context);
		init(context);
	}

	public PipPlayerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	protected void init(Context context) {
		removeAllViews();
		inflate(context, R.layout.pip_localplayer, this);
	}

	private void startStartPlayAsync() {
		closeStartPlayAsync();
		startPlayAsync = new StartPlayAsync();
		startPlayAsync.execute();
	}

	private void closeStartPlayAsync() {
		if (null != startPlayAsync) {
			/* changed by zlb */
			// startPlayAsync.cancel(true);
			startPlayAsync.cancel();
			/* end change by zlb */
		}
		startPlayAsync = null;
	}

	class StartPlayAsync extends LetvAsyncTask<String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground() {
			try {
				Thread.currentThread().sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			onCreate(mBundle);
			onResume();
		}
	}

	public void launch(Bundle bundle) {
		mBundle = bundle;
		startStartPlayAsync();
	}

	protected void onCreate(Bundle bundle) {
		boolean threeScreen = bundle.getBoolean("isThreeScreen");
		if (threeScreen) {
			// playController = new PipThreeScreenPlayController(this);
		} else {
			boolean isLive = bundle.getBoolean("isLive");
			if (isLive) {
				mPipPlayController = new PipLiveChannelPlayController(this);
			} else {
				mPipPlayController = new PipPlayAlbumController(this);
			}
		}
		mPipPlayController.onCreate(bundle);
		lockScreenIntentFilter = new IntentFilter();
		lockScreenIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		lockScreenIntentFilter.addAction(Intent.ACTION_SCREEN_ON);
		lockScreenIntentFilter.addAction(Intent.ACTION_USER_PRESENT);
		registerLockScreenBroadcast();
	}

	public PipPlayController getPlayController() {
		return mPipPlayController;
	}

	protected void onResume() {
		mPipPlayController.onResume();
	}

	protected void onPause() {
		if (null != mPipPlayController) {
			mPipPlayController.onPause();
		}
	}

	protected void onDestroy() {
		if (null != mPipPlayController) {
			mPipPlayController.onDestroy();
		}
		unregisterLockScreenBroadcast();
		closeStartPlayAsync();
	}

	public void finish() {
		onPause();
		onDestroy();
	}

	/**
	 * 注册关屏与开屏广播
	 * */
	public void registerLockScreenBroadcast() {
		if (lockScreenIntentFilter != null) {
			getContext().registerReceiver(lockScreenreceiver,
					lockScreenIntentFilter);
		}
	}

	/**
	 * 取消关屏与开屏广播
	 * */
	public void unregisterLockScreenBroadcast() {
		try {
			getContext().unregisterReceiver(lockScreenreceiver);
		} catch (Exception e) {
			Log.e("LHY",
					"PipMediaController-unregisterLockScreenBroadcast-Exception = "
							+ e.toString());
		}

	}

	// 监听黑屏广播
	public BroadcastReceiver lockScreenreceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			if (action.equals(Intent.ACTION_SCREEN_OFF)) {
				mPipPlayController.onPause();
				// 锁屏
				Log.d("zlb", "Intent.ACTION_SCREEN_OFF");
			} else if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
				Log.d("zlb", "Intent.ACTION_USER_PRESENT");
				mPipPlayController.onResume();
			}
		}
	};
}
