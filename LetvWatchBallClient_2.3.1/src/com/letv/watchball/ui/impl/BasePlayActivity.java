package com.letv.watchball.ui.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.letv.http.bean.LetvDataHull;
import com.letv.star.bean.User;
import com.letv.utils.MD5;
import com.letv.watchball.LetvApplication;
import com.letv.watchball.R;
import com.letv.watchball.activity.LetvBaseActivity;
import com.letv.watchball.bean.DynamicCheck;
import com.letv.watchball.bean.Game;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.parser.DynamicCheckParser;
import com.letv.watchball.pip.LetvPipPlayFunction;
import com.letv.watchball.service.PipService;
import com.letv.watchball.share.LetvShareControl;
import com.letv.watchball.share.RequestShareLinkTask;
import com.letv.watchball.ui.PlayAlbumController;
import com.letv.watchball.ui.PlayController;
import com.letv.watchball.ui.PlayLiveController;
import com.letv.watchball.utils.ChangeOrientationHandler;
import com.letv.watchball.utils.LetvConstant;
import com.letv.watchball.utils.LetvUtil;
import com.letv.watchball.utils.OrientationSensorListener;
import com.letv.watchball.utils.UIs;
import com.letv.watchball.view.LetvPlayGestureLayout;
import com.letv.watchball.view.PlayAdLayout;

public class BasePlayActivity extends LetvBaseActivity {

	private static final int LAUNCH_MODE_URI = 1;

	private static final int LAUNCH_MODE_ALBUM = 2;

	private static final int LAUNCH_MODE_VIDEO = 3;

	/**
	 * 直播
	 */
	public static final int LAUNCH_MODE_LIVE = 4;
	/**
	 * 直播 全屏直播
	 */
	public static final int LAUNCH_MODE_LIVE_FULL = 5;

	public static final int PLAY_MODE_SYSTEM = 1;

	public static final int PLAY_MODE_NATIVE = 2;

	public static final int LAUNCH_FROM_CHANNEL = 1;

	public static final int LAUNCH_FROM_HOME = 2;

	public static final int LAUNCH_FROM_PLAYRECORD = 4;

	public static final int LAUNCH_FROM_RECOMMEND = 5;

	public static final int LAUNCH_FROM_FAVORITE = 6;

	public static final int LAUNCH_FROM_RELATED = 7;

	public static final int LAUNCH_FROM_RANKING = 8;

	public static final int LAUNCH_FROM_VCR = 9;

	public static final int LOGIN_REQUESTCODE = 0x10;

	public static final int PAY_REQUESTCODE = 0x11;

	public PlayController mPlayController;

	private OnRelevantStateChangeListener onRelevantStateChangeListener;

	private Handler handler;
	private OrientationSensorListener mOrientationSensorListener;
	private SensorManager sm;
	private Sensor sensor;

	/**
	 * 打开播放页
	 * 
	 * 播放地址，或者本地视频路径 全屏播放
	 * */
	public static void launch(Context context, String uriString, int playMode) {
		if (!LetvUtil.CheckNetworkState()) {
			UIs.showToast("没有网络");
			return;
		}
		Intent intent = new Intent(context, BasePlayActivity.class);
		intent.putExtra("launchMode", LAUNCH_MODE_URI);
		intent.putExtra("uri", uriString);
		intent.putExtra("playMode", playMode);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		if (!(context instanceof Activity)) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		context.startActivity(intent);
	}

	/**
	 * 打开播放页
	 * 
	 * 播放地址，或者本地视频路径 全屏播放
	 * */
	public static void launch(Context context, String uriString, int playMode,
			long seek) {
		if (!LetvUtil.CheckNetworkState()) {
			UIs.showToast("没有网络");
			return;
		}
		Intent intent = new Intent(context, BasePlayActivity.class);
		intent.putExtra("launchMode", LAUNCH_MODE_URI);
		intent.putExtra("uri", uriString);
		intent.putExtra("seek", seek);
		intent.putExtra("playMode", playMode);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		if (!(context instanceof Activity)) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		context.startActivity(intent);
	}

	/**
	 * 直播中半屏跳点播
	 * 
	 * 播放专辑或者视频
	 * */
	public static void launch(Context context, long aid, long vid, int curPage) {

		if (!LetvUtil.CheckNetworkState()) {
			UIs.showToast("没有网络");
			return;
		}
		int launchMode = 0;
		if (aid > 0) {
			launchMode = LAUNCH_MODE_ALBUM;
		} else {
			launchMode = LAUNCH_MODE_VIDEO;
		}
		Intent intent = new Intent(context, BasePlayActivity.class);
		intent.putExtra("launchMode", launchMode);
		intent.putExtra("aid", (int) aid);
		intent.putExtra("vid", (int) vid);
		intent.putExtra("curPage", (int) curPage);
		// intent.putExtra("from", from);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		if (!(context instanceof Activity)) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}

		if (context instanceof Activity) {
			((Activity) context).startActivityForResult(intent, 100);
		} else {
			context.startActivity(intent);
		}
	}

	/**
	 * 打开播放页
	 * 
	 * 播放专辑或者视频
	 * */
	public static void launch(Context context, long aid, long vid) {

		if (!LetvUtil.CheckNetworkState()) {
			UIs.showToast("没有网络");
			return;
		}
		int launchMode = 0;
		if (aid > 0) {
			launchMode = LAUNCH_MODE_ALBUM;
		} else {
			launchMode = LAUNCH_MODE_VIDEO;
		}
		Intent intent = new Intent(context, BasePlayActivity.class);
		intent.putExtra("launchMode", launchMode);
		intent.putExtra("aid", (int) aid);
		intent.putExtra("vid", (int) vid);
		// intent.putExtra("from", from);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		if (!(context instanceof Activity)) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}

		if (context instanceof Activity) {
			((Activity) context).startActivityForResult(intent, 100);
		} else {
			context.startActivity(intent);
		}
	}

	/**
	 * 打开播放页
	 * 
	 * 播放专辑或者视频
	 * */
	public static void launch(Context context, long aid, long vid,
			boolean isPlayAd) {

		if (!LetvUtil.CheckNetworkState()) {
			UIs.showToast("没有网络");
			return;
		}
		int launchMode = 0;
		if (aid > 0) {
			launchMode = LAUNCH_MODE_ALBUM;
		} else {
			launchMode = LAUNCH_MODE_VIDEO;
		}
		Intent intent = new Intent(context, BasePlayActivity.class);
		intent.putExtra("launchMode", launchMode);
		intent.putExtra("aid", (int) aid);
		intent.putExtra("vid", (int) vid);
		intent.putExtra("fromPip", isPlayAd);
		// intent.putExtra("from", from);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		if (!(context instanceof Activity)) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}

		if (context instanceof Activity) {
			((Activity) context).startActivityForResult(intent, 100);
		} else {
			context.startActivity(intent);
		}
	}

	/**
	 * 打开播放页
	 * 
	 * 播放专辑或者视频
	 * */
	public static void launch(Context context, long aid, long vid, long seek) {

		if (!LetvUtil.CheckNetworkState()) {
			UIs.showToast("没有网络");
			return;
		}

		int launchMode = 0;
		if (aid > 0) {
			launchMode = LAUNCH_MODE_ALBUM;
		} else {
			launchMode = LAUNCH_MODE_VIDEO;
		}
		Intent intent = new Intent(context, BasePlayActivity.class);
		intent.putExtra("launchMode", launchMode);
		intent.putExtra("aid", (int) aid);
		intent.putExtra("vid", (int) vid);
		// intent.putExtra("from", from);
		intent.putExtra("seek", seek);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		if (!(context instanceof Activity)) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		context.startActivity(intent);
	}

	/**
	 * 
	 * @param context
	 * @param code
	 * @param streamId
	 * @param url
	 * @param aid
	 */
	public static void launchLives(Context context, String code,
			String streamId, String url, long aid, long vid, Game game) {

		if (!LetvUtil.CheckNetworkState()) {
			UIs.showToast("没有网络");
			return;
		}
		Intent intent = new Intent(context, BasePlayActivity.class);
		int launchMode = 0;
		if (aid > 0 && vid > 0) {
			launchMode = LAUNCH_MODE_LIVE;
		} else {
			launchMode = LAUNCH_MODE_LIVE_FULL;
		}
		if (null != game) {
			LetvApplication.getInstance().saveLiveGame(game);
		} else {
			game = LetvApplication.getInstance().getLiveGame();
		}
		intent.putExtra("launchMode", launchMode);
		intent.putExtra(PlayLiveController.LIVE_CODE, code);
		intent.putExtra(PlayLiveController.LIVE_STREAMID, streamId);
		intent.putExtra(PlayLiveController.LIVE_URL, url);
		intent.putExtra(PlayLiveController.GAME, game);
		intent.putExtra("aid", aid);
		intent.putExtra("vid", vid);
		if (game != null)
			intent.putExtra("id", game.id);
		intent.putExtra(PlayLiveController.LIVE_URL, url);
		intent.putExtra(PlayLiveController.LIVE_CODE, code);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		if (!(context instanceof Activity)) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		context.startActivity(intent);
	}

	/**
	 * 
	 * @param context
	 * @param code
	 * @param streamId
	 * @param url
	 * @param aid
	 */
	public static void launchLives(Context context, String code,
			String streamId, String url, long aid, long vid, Game game,
			boolean isPlayAd) {

		if (!LetvUtil.CheckNetworkState()) {
			UIs.showToast("没有网络");
			return;
		}
		Intent intent = new Intent(context, BasePlayActivity.class);
		int launchMode = 0;
		if (aid > 0 && vid > 0) {
			launchMode = LAUNCH_MODE_LIVE;
		} else {
			launchMode = LAUNCH_MODE_LIVE_FULL;
		}
		if (null != game) {
			LetvApplication.getInstance().saveLiveGame(game);
		} else {
			game = LetvApplication.getInstance().getLiveGame();
		}
		intent.putExtra("launchMode", launchMode);
		intent.putExtra(PlayLiveController.LIVE_CODE, code);
		intent.putExtra(PlayLiveController.LIVE_STREAMID, streamId);
		intent.putExtra(PlayLiveController.LIVE_URL, url);
		intent.putExtra(PlayLiveController.GAME, game);
		intent.putExtra("aid", aid);
		intent.putExtra("vid", vid);
		intent.putExtra("fromPip", isPlayAd);
		if (game != null)
			intent.putExtra("id", game.id);
		intent.putExtra(PlayLiveController.LIVE_URL, url);
		intent.putExtra(PlayLiveController.LIVE_CODE, code);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		if (!(context instanceof Activity)) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		context.startActivity(intent);
	}

	/**
	 * 
	 * @param context
	 * @param game
	 */

	public static void launchLives(Context context, Game game) {

		if (!LetvUtil.CheckNetworkState()) {
			UIs.showToast("没有网络");
			return;
		}
		Intent intent = new Intent(context, BasePlayActivity.class);
		int launchMode = 0;
		if (game.getPid() > 0 && game.getVid() > 0) {
			launchMode = LAUNCH_MODE_LIVE;
		} else {
			launchMode = LAUNCH_MODE_LIVE_FULL;
		}
		intent.putExtra("launchMode", launchMode);
		intent.putExtra(PlayLiveController.GAME, game);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		if (!(context instanceof Activity)) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		context.startActivity(intent);
	}

	/**
	 * 播放器之上的容器
	 * */
	protected FrameLayout playUpperLayout;
	/**
	 * 播放器之上的容器
	 * */
	protected FrameLayout playUpper;

	/**
	 * 播放器之上的手势层
	 * */
	protected LetvPlayGestureLayout playGestrue;

	/**
	 * 播放器之下的容器（半屏才显示）
	 * */
	protected RelativeLayout playLower;

	/**
	 * 播放器的Fragment
	 * */
	protected BasePlayFragment playFragment;

	/**
	 * 打开方式；用来区别播放不动的视频
	 * */
	private int launchMode;

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		playFragment.pause();
		playFragment.stopPlayback();
		mPlayController.format();
		readLaunchMode();
		initView();
		initWindow();
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_play);
		findView();

		// 自定义旋转
		handler = new ChangeOrientationHandler(this);
		sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mOrientationSensorListener = new OrientationSensorListener(handler,
				this);

		// 关闭小窗播放
		if (LetvPipPlayFunction.PipServiceIsStart(this)) {
			LetvPipPlayFunction.closePipView(this);
		}
		rect = new Rect();
		readLaunchMode();
		initView();
		initWindow();

	}
	@Override
	protected void onRestart(){
		super.onRestart();
		this.mPlayController.onActivityRestart();
	}
	@Override
	protected void onResume() {
		super.onResume();
		
		if (!LetvShareControl.getInstance().isShare()) {
			new RequestShareLinkTask(this).start();
		}
		sm.registerListener(mOrientationSensorListener, sensor,
				SensorManager.SENSOR_DELAY_UI);
		registerReceiver();
		//this.mPlayController.onActivityResume();
		// boolean isLogin = PreferencesManager.getInstance().isLogin();
		// if (isLogin) {
		//
		// }

	}

	@Override
	protected void onPause() {
		super.onPause();
		sm.unregisterListener(mOrientationSensorListener);
		unregisterReceiver();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	/**
	 * 页面改变时，改变控件尺寸
	 * */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		initWindow();// 旋转后更改页面尺寸
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if (requestCode == LoginMainActivity.LOGIN && resultCode ==
		// LoginMainActivity.LOGIN_SUCCESS) {
		// mPlayController.onActivityResultLoginSuccess();
		// }
		//
		// if (requestCode == VipProductsActivity.PAY && resultCode ==
		// VipProductsActivity.PAY_SUCCESS) {
		// mPlayController.onActivityResultPaySuccess();
		// }
		//
		if (HalfPlayShareFragment.onFragmentResult != null) {// 解决分享不会有onactiviResult
			HalfPlayShareFragment.onFragmentResult.onFragmentResult_back(
					requestCode, resultCode, data);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mPlayController.onKeyDown(keyCode, event)) {
			return true;
		} // 获取手机当前音量值
		boolean ss = super.onKeyDown(keyCode, event);
		if (mPlayController != null) {
			int volNum = mPlayController.getCurSoundVolume();
			boolean isUp = false;
			if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
				volNum += 1;
				isUp = true;
			} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
				volNum -= 1;
				isUp = false;
			}
			mPlayController.curVolume(mPlayController.getMaxSoundVolume(),
					volNum, isUp);
		}

		return ss;
	}

	/**
	 * 初始化控件
	 * */
	private void findView() {
		playUpperLayout = (FrameLayout) findViewById(R.id.play_upper_layout);
		playUpper = (FrameLayout) findViewById(R.id.play_upper);
		playLower = (RelativeLayout) findViewById(R.id.play_lower);
		playFragment = (BasePlayFragment) getSupportFragmentManager()
				.findFragmentById(R.id.play_fragment);
		playGestrue = (LetvPlayGestureLayout) findViewById(R.id.play_gestrue);
		adLayout = (PlayAdLayout) findViewById(R.id.play_ad_layout);
	}

	/**
	 * 读取加载模式
	 * */
	private void readLaunchMode() {
		Intent intent = getIntent();
		launchMode = intent.getIntExtra("launchMode", 0);
		if (launchMode == 0) {
			String path = null;
			if (null != getIntent() && null != getIntent().getData()) {

				Uri uriPath = getIntent().getData();
				String scheme = uriPath.getScheme();
				if (scheme == null || scheme.equals("file")) {
					path = uriPath.getPath();
				} else {
					path = uriPath.toString();
				}
			}

			launchMode = LAUNCH_MODE_URI;
			intent.putExtra("launchMode", LAUNCH_MODE_URI);
			intent.putExtra("uri", path);
			intent.putExtra("seek", 0);
			intent.putExtra("playMode", PLAY_MODE_SYSTEM);
		}
	}

	/**
	 * 根据播放形势不一样，生成不同的mPlayController
	 * */
	private void initView() {
		if (launchMode == LAUNCH_MODE_URI) {
			PlayAlbumController controller = null;
			mPlayController = controller = new PlayAlbumController(this);
			playFragment.setStateChangeListener(controller);
			mPlayController.setLaunchMode(PlayController.PLAY_DEFAULT);
			mPlayController.create();

			mOrientationSensorListener.setJustLandscape(true);
			setOnRelevantStateChangeListener(mPlayController);
			playFragment.setNotResumeSeek(false);
		} else if (launchMode == LAUNCH_MODE_ALBUM) {
			PlayAlbumController controller = null;
			mPlayController = controller = new PlayAlbumController(this);
			playFragment.setStateChangeListener(controller);
			mPlayController.setLaunchMode(PlayController.PLAY_ALBUM);
			mPlayController.create();

			mOrientationSensorListener.setJustLandscape(false);
			setOnRelevantStateChangeListener(mPlayController);
			playFragment.setNotResumeSeek(false);
		} else if (launchMode == LAUNCH_MODE_VIDEO) {
			PlayAlbumController controller = null;
			mPlayController = controller = new PlayAlbumController(this);
			playFragment.setStateChangeListener(controller);
			mPlayController.setLaunchMode(PlayController.PLAY_VIDEO);
			mPlayController.create();

			mOrientationSensorListener.setJustLandscape(true);
			setOnRelevantStateChangeListener(mPlayController);
			playFragment.setNotResumeSeek(false);
		} else if (launchMode == LAUNCH_MODE_LIVE) {
			PlayLiveController controller = null;
			mPlayController = controller = new PlayLiveController(this);
			playFragment.setStateChangeListener(controller);
			mPlayController.setLaunchMode(PlayController.PLAY_LIVE);

			setOnRelevantStateChangeListener(mPlayController);
			playFragment.setNotResumeSeek(true);
			mPlayController.create();
		} else if (launchMode == LAUNCH_MODE_LIVE_FULL) {
			PlayLiveController controller = null;
			mPlayController = controller = new PlayLiveController(this);
			playFragment.setStateChangeListener(controller);
			mPlayController.setLaunchMode(PlayController.PLAY_LIVE_FULL);

			setOnRelevantStateChangeListener(mPlayController);
			playFragment.setNotResumeSeek(true);
			mPlayController.create();
		}

	}

	/**
	 * 初始化页面尺寸
	 * */
	private void initWindow() {
		boolean isLandscape = UIs.isLandscape(this);
		if (isLandscape) {
			playLower.setVisibility(View.GONE);
			playLower.requestLayout();
			UIs.zoomViewFull(playUpperLayout);
			UIs.fullScreen(this);
		} else {
			playLower.setVisibility(View.VISIBLE);
			playLower.requestLayout();
			UIs.zoomView(320, 180, playUpperLayout);
			UIs.notFullScreen(this);
		}
		mPlayController.changeDirection(isLandscape);
	}

	public FrameLayout getPlayUpper() {
		return playUpper;
	}

	public void setPlayUpper(FrameLayout playUpper) {
		this.playUpper = playUpper;
	}

	public RelativeLayout getPlayLower() {
		return playLower;
	}

	public void setPlayLower(RelativeLayout playLower) {
		this.playLower = playLower;
	}

	public BasePlayFragment getPlayFragment() {
		return playFragment;
	}

	public void setPlayFragment(BasePlayFragment playFragment) {
		this.playFragment = playFragment;
	}

	public LetvPlayGestureLayout getPlayGestrue() {
		return playGestrue;
	}

	public void setPlayGestrue(LetvPlayGestureLayout playGestrue) {
		this.playGestrue = playGestrue;
	}

	public OnRelevantStateChangeListener getOnRelevantStateChangeListener() {
		return onRelevantStateChangeListener;
	}

	public void setOnRelevantStateChangeListener(
			OnRelevantStateChangeListener onRelevantStateChangeListener) {
		this.onRelevantStateChangeListener = onRelevantStateChangeListener;
	}

	public OrientationSensorListener getmOrientationSensorListener() {
		return mOrientationSensorListener;
	}

	public void setmOrientationSensorListener(
			OrientationSensorListener mOrientationSensorListener) {
		this.mOrientationSensorListener = mOrientationSensorListener;
	}

	/**
	 * 注册关于监听 电量，时间，网络状态的监听
	 * */
	public void registerReceiver() {
		try {
			IntentFilter filter = new IntentFilter();
			filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
			filter.addAction(Intent.ACTION_TIME_TICK);
			filter.addAction(Intent.ACTION_BATTERY_CHANGED);
			filter.addAction(Intent.ACTION_HEADSET_PLUG);
			// filter.addAction("android.intent.action.HEADSET_PLUG");
			registerReceiver(mBroadcastReceiver, filter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 取消关于监听 电量，时间，网络状态的监听
	 * */
	public void unregisterReceiver() {
		try {
			unregisterReceiver(mBroadcastReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 监听网络，电量，时间
	 * */
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {// 监听每分钟的时间变化
				if (onRelevantStateChangeListener != null) {
					onRelevantStateChangeListener.onTimeChange();
				}
			} else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent
					.getAction())) {// 监听网络连接状态变化
				if (onRelevantStateChangeListener != null) {
					onRelevantStateChangeListener.onNetChange();
				}
			} else if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {// 监听电量变化
				int status = intent.getIntExtra("status",
						BatteryManager.BATTERY_STATUS_UNKNOWN);// 获得电池状态
				int level = intent.getExtras().getInt("level", 0);// 获得当前电量
				int scale = intent.getExtras().getInt("scale", 100);// 获得总电量
				int curPower = level * 100 / scale;

				if (onRelevantStateChangeListener != null) {
					onRelevantStateChangeListener.onBatteryChange(status,
							curPower);
				}
			} else if (Intent.ACTION_HEADSET_PLUG.equals(intent.getAction())) {
				if (onRelevantStateChangeListener != null) {
					onRelevantStateChangeListener.onHeadsetPlug();
				}
			}
		}
	};

	/**
	 * 相关状态变化的监听
	 * */
	public interface OnRelevantStateChangeListener {
		public void onTimeChange();

		public void onNetChange();

		public void onBatteryChange(int curStatus, int curPower);

		public void onDownloadStateChange();

		public void onHeadsetPlug();
	}

	@Override
	protected void onDestroy() {
		mPlayController.saveBrightness();
		super.onDestroy();
		playUpper.removeAllViews();
		playLower.removeAllViews();
		playUpperLayout.removeAllViews();
		if (null != mPlayController) {
			mPlayController.onDestroy();
		}
		mPlayController = null;
		playUpper = null;
		playLower = null;
		playUpperLayout = null;
	}

	/**
	 * 收起软键盘
	 */
	public void collapseSoftInputMethod() {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	private final int ON_VIDEORE_SIZE = 6;// 当播放区域发生改变

	private final int ON_ACTIVITY_PAUSE = 7;// 当 Activity 暂停

	private final int ON_ACTIVITY_RESUME = 8;// 当 Activity 继续

	private final int ON_ACTIVITY_EXIT = 9;// 当 Activity 退出

	/**
	 * 播放显示区域
	 */
	public Rect rect;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (playUpperLayout != null) {
				playUpperLayout.getGlobalVisibleRect(rect);
				// LogInfo.log("ads", "rect.top ="+rect.top +"  rect.bottom= "+
				// rect.bottom+" rect.left="+ rect.left+" rect.right="+
				// rect.right);
				callAdsPlayInterface(ON_VIDEORE_SIZE);
			}
		}
	};

	/**
	 * 广告接口回调播放各种状态
	 * 
	 * @param whichStatus
	 */
	private void callAdsPlayInterface(int whichStatus) {
		try {
			if (mPlayController != null
					&& mPlayController.mIVideoStatusInformer != null) {
				switch (whichStatus) {
				case ON_VIDEORE_SIZE:
					mPlayController.mIVideoStatusInformer.OnVideoResize(rect);
					break;
				case ON_ACTIVITY_PAUSE:
					mPlayController.mIVideoStatusInformer.OnActivityPause();
					break;
				case ON_ACTIVITY_RESUME:
					mPlayController.mIVideoStatusInformer.OnActivityResume();
					break;
				case ON_ACTIVITY_EXIT:
					mPlayController.mIVideoStatusInformer.OnActivityExit();
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected PlayAdLayout adLayout;

	public PlayAdLayout getAdLayout() {
		return adLayout;
	}

	// public static void launch(Context context, Bundle bundle) {
	// Intent intent = new Intent(context, BasePlayActivity.class);
	// intent.putExtra(LetvConstant.Intent.Bundle.PLAY, bundle);
	// if (!(context instanceof Activity)) {
	// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	// }
	// context.startActivity(intent);
	// }
}
