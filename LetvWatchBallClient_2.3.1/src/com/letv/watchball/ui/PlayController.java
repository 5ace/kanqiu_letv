package com.letv.watchball.ui;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.letv.adlib.managers.status.video.IVideoStatusInformer;
import com.letv.ads.ADPlayFragment;
import com.letv.watchball.R;
import com.letv.watchball.bean.AlbumNew;
import com.letv.watchball.bean.Video;
import com.letv.watchball.bean.VideoList;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.ui.PlayAlbumController.PlayAlbumControllerCallBack;
import com.letv.watchball.ui.impl.BasePlayActivity;
import com.letv.watchball.ui.impl.BasePlayActivity.OnRelevantStateChangeListener;
import com.letv.watchball.utils.LetvUtil;
import com.letv.watchball.utils.OrientationSensorListener.OnDirectionChangeListener;
import com.letv.watchball.utils.UIs;
import com.letv.watchball.view.AddCommentLayout;
import com.letv.watchball.view.LetvPlayGestureLayout.LetvPlayGestureCallBack;

public abstract class PlayController implements OnRelevantStateChangeListener, LetvPlayGestureCallBack {
	
	/**
	 * 专辑 ID
	 * */
	public long aid;

	/**
	 * 视频ID
	 * */
	public long vid;
	/**
	 * 直播ID
	 * */
	public long id;
	public String content;
	
	
	/**
	 * 播放器错误码
	 */
	public int errorCodeExtra;
	public int errorCodeWhat;
	/**
	 * 状态回调
	 * */
	public int introductionCallBackState;
	public PlayAlbumControllerCallBack introductionCallBack;
	public int videosCallBackState;
	public PlayAlbumControllerCallBack videosCallBack;
	public int getCommentsCallBackState;
	public PlayAlbumControllerCallBack getCommentsCallBack;
	/**
	 * 视频列表当前页页码
	 * */
	public int curPage = 1;

	/**
	 * 一页的条数
	 * */
	public int pageSize = 60;

	/**
	 * 是否合并
	 * */
	public int merge = 0;

	/**
	 * 排序
	 * */
	public String order = "-1";

	/**
	 * 视频总数
	 * */
	public int totle = 0;

	/**
	 * 视频集合，以页数为key
	 * */
	public final HashMap<Integer, VideoList> videos = new HashMap<Integer, VideoList>();

	/**
	 * 剧集列表是否是列表形势
	 * */
	public boolean isList;
	
	

	public static final int PLAY_DEFAULT = 0;

	public static final int PLAY_ALBUM = 1;

	public static final int PLAY_VIDEO = 2;

//	public static final int PLAY_DOWNLOAD = 3;

	/**
	 * 有半屏的直播
	 */
	public static final int PLAY_LIVE = 4;
	/**
	 * 无半屏的直播
	 */
	public static final int PLAY_LIVE_FULL = 4;
	// 轮播和卫视台的处理逻辑一样
/*	public static final int PLAY_LIVE_LUNBO = 5;
	public static final int PLAY_LIVE_WEISHI = 6;

	public static final int PLAY_LIVE_ZHIBOTING = 7;*/

	private BasePlayActivity activity;

	private int launchMode = PLAY_VIDEO;

	private final AudioManager audioManager;

	/**
	 * 手势 亮度的布局
	 * */
	private View brightnessLayout;
	/**
	 * 手势 声音的布局
	 * */
	private View volumeLayout;
	/**
	 * 手势 亮度的ProgressBar
	 * */
	private ProgressBar brightnessSeekbar;
	/**
	 * 手势 声音的ProgressBar
	 * */
	private ProgressBar volumeSeekbar;
	/**
	 * 手势 锁的image
	 * */
	private ImageView lockBar;

	/**
	 * 手势 进度的布局
	 * */
	private View progressLayout;

	/**
	 * 手势 当前进度显示
	 * */
	private TextView progressTextView;

	/**
	 * 手势 总长度显示
	 * */
	private TextView totalTextView;

	/**
	 * 是否上锁
	 * */
	protected boolean isLock;

	/**
	 * 手势操作开始的声音点
	 * */
	private int oldV;

	/**
	 * 手势操作开始的亮度点
	 * */
	private int oldB;
	/**
	 * 发表评论
	 */
	public AddCommentLayout add_comment_main;

      /**
       * 新增IVideoStatusInformer ，用于客户端将播放状态通知给广告
       */
      public IVideoStatusInformer mIVideoStatusInformer;

      /**
       * 播放页广告fragment
       * */
      public ADPlayFragment playAdFragment;



	private Handler xHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (lockBar != null) {
				lockBar.setVisibility(View.GONE);
			}
		};
	};

	public PlayController(BasePlayActivity activity) {
		this.activity = activity;
		audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
		if (audioManager != null) {
			if (audioManager.getMode() == AudioManager.MODE_INVALID) {
				audioManager.setMode(AudioManager.MODE_NORMAL);
			}
		}
	}
	public void onActivityRestart(){
		
	}
	
	public void create() {
		findGestrueView();
		readArguments();
		initLayout();
		initCommentEditText();
		int currentValue = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		int maxValue = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		curVolume(maxValue, currentValue);
		oldV = currentValue;

//		isLock = PreferencesManager.getInstance().isLockDirection();
		lockBar.setImageResource(isLock ? R.drawable.lock : R.drawable.unlock);
		activity.getmOrientationSensorListener().setLock(isLock);
		activity.getmOrientationSensorListener().setOnDirectionChangeListener(new OnDirectionChangeListener() {

			@Override
			public void onChange(int orientation, int orientationProperty) {
				if (lockBar.getVisibility() != View.VISIBLE) {
					lockBar.setVisibility(View.VISIBLE);
					startHandlerTime();
				}
			}
		});
	}
	
	/**
	 * 发表评论
	 */
	private void initCommentEditText(){
		add_comment_main = (AddCommentLayout) getActivity().findViewById(R.id.add_comment_main);
		add_comment_main.init();
		
	}

	private void findGestrueView() {
		brightnessLayout = activity.getPlayGestrue().findViewById(R.id.brightness_layout);
		volumeLayout = activity.getPlayGestrue().findViewById(R.id.volume_layout);
		brightnessSeekbar = (ProgressBar) activity.getPlayGestrue().findViewById(R.id.brightness_verseekbar);
		volumeSeekbar = (ProgressBar) activity.getPlayGestrue().findViewById(R.id.volume_verseekbar);
		lockBar = (ImageView) activity.getPlayGestrue().findViewById(R.id.lock);
		progressLayout = activity.getPlayGestrue().findViewById(R.id.progress_layout);
		progressTextView = (TextView) activity.getPlayGestrue().findViewById(R.id.progress);
		totalTextView = (TextView) activity.getPlayGestrue().findViewById(R.id.total);

		brightnessLayout.setVisibility(View.GONE);
		volumeLayout.setVisibility(View.GONE);
		progressLayout.setVisibility(View.GONE);
		initVolume(getMaxSoundVolume(), getCurSoundVolume());
		initBrightness(getMaxBrightness(), getCurBrightness());
		oldV = getCurBrightness();

		setBrightness(PreferencesManager.getInstance().getPlayBrightness());
		activity.getPlayGestrue().initializeData((float) getCurSoundVolume() / getMaxSoundVolume(), (float) getCurBrightness() / getMaxBrightness());
		activity.getPlayGestrue().setLetvPlayGestureCallBack(this);

		lockBar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LockRegulate();
			}
		});
	}

	/**
	 * 倒计时隐藏lock
	 * */
	private void startHandlerTime() {
		if(xHandler!=null){
			xHandler.removeMessages(1);
			xHandler.sendEmptyMessageDelayed(1, 3000);
		}
	}

	public void showLock() {
		if(lockBar!=null){
		lockBar.setVisibility(View.VISIBLE);
		}
		startHandlerTime();
	}

	public void setLaunchMode(int launchMode) {
		this.launchMode = launchMode;
	};

	public int getLaunchMode() {
		return launchMode;
	};

	public BasePlayActivity getActivity() {
		return activity;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
//			int currentValue = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//			++currentValue;
//			int maxValue = setSoundVolume(currentValue, false);
//			curVolume(maxValue, currentValue);
//			return true;
//		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
//			int currentValue = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//			--currentValue;
//			int maxValue = setSoundVolume(currentValue, false);
//			curVolume(maxValue, currentValue);
//			return true;
//		}
		return false;
	};

	/**
	 * 得到最大音量
	 * */
	public int getMaxSoundVolume() {
		return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}

	/**
	 * 得到当前音量
	 * */
	public int getCurSoundVolume() {
		return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	/**
	 * 得到最大亮度
	 * */
	public int getMaxBrightness() {
		return 255;
	}

	/**
	 * 得到当前亮度
	 * */
	public int getCurBrightness() {
		WindowManager.LayoutParams wl = activity.getWindow().getAttributes();
		float br = wl.screenBrightness;
		if (br < 0) {
			br = 0;
			setBrightness(br);
		}

		return (int) (br * 255);
	}

	// /**
	// * 得到屏幕亮度的调节方式 SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
	// * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
	// */
	// private int getScreenMode() {
	// int sm = 0;
	// try {
	// sm = Settings.System.getInt(activity.getContentResolver(),
	// Settings.System.SCREEN_BRIGHTNESS_MODE);
	// } catch (Exception localException) {
	// localException.printStackTrace();
	// }
	//
	// return sm;
	// }
	//
	// /**
	// * 设置当前屏幕亮度的模式 SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
	// * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
	// */
	// private void setScreenMode(int paramInt) {
	// try {
	// Settings.System.putInt(activity.getContentResolver(),
	// Settings.System.SCREEN_BRIGHTNESS_MODE, paramInt);
	// } catch (Exception localException) {
	// localException.printStackTrace();
	// }
	// }

	/**
	 * 初始化手势声音布局
	 * */
	public void initVolume(int max, int cur) {
		if (volumeSeekbar != null) {
			volumeSeekbar.setMax(max);
			volumeSeekbar.setProgress(cur);
		}
	}

	/**
	 * 初始化手势亮度布局
	 * */
	public void initBrightness(int max, int cur) {
		if (brightnessSeekbar != null) {
			brightnessSeekbar.setMax(max);
			brightnessSeekbar.setProgress(cur);
		}
	}

	/**
	 * 调节音量
	 * */
	public int setSoundVolume(int value, boolean isShow) {
		int maxValue = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		if (value >= 0 && value <= maxValue) {
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, value, 0);
			volumeRegulate(isShow, value);
		}
		return  maxValue;
	}

	/**
	 * 调节音量
	 * */
	public void setBrightness(float value) {

		WindowManager.LayoutParams wl = activity.getWindow().getAttributes();
		if (value < 0.01f) {
			wl.screenBrightness = 0.01f;
		} else {
			wl.screenBrightness = value;
		}

		activity.getWindow().setAttributes(wl);
	}

	/**
	 * 调节亮度
	 * */
	public void brightnessRegulate(boolean isShow, int pos) {
		if (isShow) {
			if (brightnessLayout.getVisibility() != View.VISIBLE) {
				brightnessLayout.setVisibility(View.VISIBLE);
			}
		} else {
			// if (brightnessLayout.getVisibility() == View.VISIBLE) {
			// brightnessLayout.setVisibility(View.GONE);
			// }
		}
		if (brightnessSeekbar != null) {
			brightnessSeekbar.setProgress(pos);
		}
	}

	/**
	 * 调节声音
	 * */
	public void volumeRegulate(boolean isShow, int pos) {
		if (isShow) {
			if (volumeLayout.getVisibility() != View.VISIBLE) {
				volumeLayout.setVisibility(View.VISIBLE);
			}
		} else {
			// if (volumeLayout.getVisibility() == View.VISIBLE) {s
			// volumeLayout.setVisibility(View.GONE);
			// }
		}
		if (volumeSeekbar != null) {
			volumeSeekbar.setProgress(pos);
		}
	}

	/**
	 * 调节进度
	 * */
	public void progressRegulate(int curPos, int total) {
		if (progressLayout != null && progressLayout.getVisibility() != View.VISIBLE) {
			progressLayout.setVisibility(View.VISIBLE);
		}

		if (progressTextView != null) {
			progressTextView.setText(LetvUtil.stringForTime(curPos));
		}

		if (totalTextView != null) {
			totalTextView.setText(LetvUtil.stringForTime(total));
		}
	}

	/**
	 * lock状态
	 * */
	public void LockRegulate() {
		isLock = !isLock;
		lockBar.setImageResource(isLock ? R.drawable.lock : R.drawable.unlock);
		if (isLock) {
			UIs.showToast("方向已锁定，单击解锁");
		} else {
			UIs.showToast("方向已解锁，单击加锁");
		}
//		PreferencesManager.getInstance().setLockDirection(isLock);
		activity.getmOrientationSensorListener().setLock(isLock);
	}

	public void wakeLock() {

	}

	@Override
	public void onDoubleFingersDown() {

	}

	@Override
	public void onDoubleFingersUp() {

	}

	@Override
	public void onSingleTapUp() {
		// if (isLock)
		// return;
	}

	@Override
	public void onDoubleTap() {
		// if (isLock)
		// return;
	}

	@Override
	public void onRightScroll(float incremental) {
		int max = getMaxSoundVolume();
		int newVlaue = oldV + (int) (max * incremental);
		if (newVlaue < 0) {
			newVlaue = 0;
		}
		if (newVlaue > max) {
			newVlaue = max;
		}
		setSoundVolume(newVlaue, true);
		curVolume(max, newVlaue);
	}

	@Override
	public void onLeftScroll(float incremental) {
		int max = getMaxBrightness();
		int newVlaue = oldB + (int) (max * incremental);
		if (newVlaue < 0) {
			newVlaue = 0;
		}
		if (newVlaue > max) {
			newVlaue = max;
		}
		setBrightness((float) newVlaue / max);
		brightnessRegulate(true, newVlaue);
	}

	@Override
	public void onLandscapeScroll(float incremental) {

	}

	@Override
	public void onLandscapeScrollFinish(float incremental) {

	}

	@Override
	public void onTouchEventUp() {
		volumeLayout.setVisibility(View.GONE);
		brightnessLayout.setVisibility(View.GONE);
		progressLayout.setVisibility(View.GONE);
	}

	@Override
	public void onDown() {
		oldV = getCurSoundVolume();
		oldB = getCurBrightness();
	}

	@Override
	public void onLongPress() {

	}

	public boolean isLock() {
		return isLock;
	}

	@Override
	public abstract void onTimeChange();

	@Override
	public abstract void onNetChange();

	@Override
	public abstract void onBatteryChange(int curStatus, int curPower);

	protected abstract void initLayout();

	protected abstract void readArguments();

	public abstract void changeDirection(boolean isLandscape);
	public abstract void curVolume(int max, int progrees, boolean isUp);
	public abstract void curVolume(int max, int progrees);

	public abstract void format();
	
	
	
	public abstract ViewPager getViewPager();
	public abstract AlbumNew getAlbum();
	public abstract Video getVideo();
	/**
	 * 切换视频列表
	 * */
	public abstract boolean getVideoList(int page);

	public void onDestroy() {
		activity = null;
		brightnessLayout = null;
		volumeLayout = null;
		brightnessSeekbar = null;
		volumeSeekbar = null;
		lockBar = null;
		progressLayout = null;
		progressTextView = null;
		totalTextView = null;
		
		introductionCallBack = null;
		videosCallBack = null;
		getCommentsCallBack = null;
	};

//	public abstract void onActivityResultLoginSuccess();

//	public abstract void onActivityResultPaySuccess();

	public void saveBrightness() {
		PreferencesManager.getInstance().setPlayBrightness(getCurBrightness() / 255f);
	}

	public void onError(int what, int extra) {
		
		errorCodeExtra = extra;
		errorCodeWhat = what;
	}

}
