package com.letv.watchball.pip;

import java.util.Formatter;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.letv.watchball.R;
import com.letv.watchball.bean.AlbumNew;
import com.letv.watchball.bean.Episode;
import com.letv.watchball.bean.Video;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.utils.LetvUtil;
import com.letv.watchball.utils.NetWorkTypeUtils;
import com.letv.watchball.utils.UIs;

/**
 * 画中画，播放下载视频，三屏，网络视频所用到的Controller
 * 
 * @author 
 * 
 */
public class PipMediaController extends RelativeLayout implements BaseMediaController {

	private MediaPlayerControl player;
	public PipPlayController mPlayController = null;
	public Context context;
	private View root;
	private ProgressBar mProgressBar; // 视频进度条
	/**
	 * 拖动进度时显示的时间
	 */
	private TextView mProgressTime;
	public boolean showing;
	private boolean dragging;
	private static final int sDefaultTimeout = 3000;
	private static final int FADE_OUT = 1;
	private static final int SHOW_PROGRESS = 2;
	/**
	 * 视频标题
	 */
	private TextView titleTextView;
	private String title;
	/**
	 * 设置视频标题
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
		if(titleTextView != null) {
			titleTextView.setText(title);
		}
	}
	
	public Video getmVideo() {
		return mVideo;
	}

	public void setVideo(Video mVideo) {
		this.mVideo = mVideo;
	}

	public String getRealUrl() {
		return realUrl;
	}

	public void setRealUrl(String realUrl) {
		this.realUrl = realUrl;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	private boolean useFastForward;

	private boolean playNet;// 是否播放在线视频

	private AlbumNew album; // 专辑数据
//	private Episode episode;// 播放视频
	private Video mVideo;// 播放视频
	private String realUrl;
	private String filePath;
	private int order;// 当前播放的集数

	StringBuilder formatBuilder;
	Formatter formatter;

	private ImageView pauseButton; // 暂停按钮

	private View topControllerView;// 头部灰色背景控制条
	private ImageView pip_video_play_controller_finish;
	private LinearLayout pip_video_play_controller_finish_layout;
	public ImageView pip_video_play_controller_fullScreen;
	public LinearLayout pip_video_play_controller_fullScreen_layout;
//	private ImageView play_right = null;// 快进按钮
	private ImageView play_skip_begin = null;// 片头节点
	private ImageView play_skip_end = null;// 片尾节点

	private RelativeLayout pip_video_play_controller_bottomLayout; // 底部控制栏

	private boolean isLive = false;

	private boolean isWebPlay = false;
	
	public int total;

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public PipMediaController(Context context, AttributeSet attrs) {
		super(context, attrs);
		root = this;
		this.context = context;
		useFastForward = true;
		initView();
	}

	public void setAlbum(AlbumNew album) {
		this.album = album;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public void setPlayNet(boolean playNet) {
		this.playNet = playNet;
	}

	@Override
	public void onFinishInflate() {
		if (root != null)
			initControllerView();
	}

	public PipMediaController(Context context, boolean useFastForward) {
		super(context);
		this.context = context;
		this.useFastForward = useFastForward;
		initFloatingWindow();
		initView();
	}

	public PipMediaController(Context context) {
		super(context);
		this.context = context;
		useFastForward = true;
		initFloatingWindow();
		initView();
	}

	public PipMediaController(Context context, AlbumNew album, boolean playNet) {
		super(context);
		this.context = context;
		useFastForward = true;
		initFloatingWindow();
		this.album = album;
		this.playNet = playNet;
		initView();
	}

	/**
	 * 初始化界面
	 */
	public void initView() {
		removeAllViews();
		inflate(context, R.layout.pip_video_play_controller, this);
		mProgressTime = (TextView) findViewById(R.id.progress_time);
	}

	private void initFloatingWindow() {
		setFocusable(true);
		setFocusableInTouchMode(true);
		setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
		requestFocus();

	}

	public void setMediaPlayer(MediaPlayerControl player) {
		this.player = player;
		updatePausePlay();
	}

	public void initControllerView() {

		titleTextView = (TextView) this.findViewById(R.id.pip_video_play_controller_title);
		if (titleTextView != null) {
			String title = mPlayController == null || mPlayController.getVideoTitle() == null ? ""
					: mPlayController.getVideoTitle();
			titleTextView.setText(title);
		}

//		topControllerView = this.findViewById(R.id.play_controller_top);

		pip_video_play_controller_finish = (ImageView) this
				.findViewById(R.id.pip_video_play_controller_finish);
		if (pip_video_play_controller_finish != null) {
			pip_video_play_controller_finish.setOnClickListener(closeListener);
		}

		pip_video_play_controller_finish_layout = (LinearLayout) this
				.findViewById(R.id.pip_video_play_controller_finish_layout);
		if (pip_video_play_controller_finish_layout != null) {
			pip_video_play_controller_finish_layout.setOnClickListener(closeListener);
		}

		pip_video_play_controller_fullScreen = (ImageView) this
				.findViewById(R.id.pip_video_play_controller_fullScreen);
		if (null != pip_video_play_controller_fullScreen) {
			pip_video_play_controller_fullScreen.setOnClickListener(pipToMainPlayerListener);
		}

		pip_video_play_controller_fullScreen_layout = (LinearLayout) this
				.findViewById(R.id.pip_video_play_controller_fullScreen_layout);
		if (null != pip_video_play_controller_fullScreen_layout) {
			pip_video_play_controller_fullScreen_layout.setOnClickListener(pipToMainPlayerListener);
		}

		pip_video_play_controller_bottomLayout = (RelativeLayout) this
				.findViewById(R.id.pip_video_play_controller_bottomLayout);

		pauseButton = (ImageView) this.findViewById(R.id.play_pause);
		if (pauseButton != null) {
			pauseButton.requestFocus();
			pauseButton.setOnClickListener(pauseListener);
		}

//		play_right = (ImageView) this.findViewById(R.id.play_right);
//		if (play_right != null) {
//			play_right.setOnClickListener(forwardListener);
//		}
		
//		int num = 0;
//		if(album != null) {
//			int merge = LetvFunction.getMerge(album.getStyle());
//			num = (merge == 1 ? album.getPlatformVideoNum() : album.getPlatformVideoInfo());
//		}
//		if (album != null && num > 1 && !isWebPlay) {
//			hasNext = true;
//			enableNextBtn();
//		} else {
//			disableNextBtn();
//		}

		mProgressBar = (ProgressBar) this.findViewById(R.id.play_seekbar);
		if (mProgressBar != null) {
			if (mProgressBar instanceof SeekBar) {
				SeekBar seeker = (SeekBar) mProgressBar;
				seeker.setOnSeekBarChangeListener(seekListener);
			}
			mProgressBar.setMax(1000);
		}

		play_skip_begin = (ImageView) this.findViewById(R.id.play_skip_begin);
		play_skip_end = (ImageView) this.findViewById(R.id.play_skip_end);

		formatBuilder = new StringBuilder();
		formatter = new Formatter(formatBuilder, Locale.getDefault());
		
		pip_video_play_controller_bottomLayout.setOnTouchListener(new OnTouchListener() {
			float x = 0;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(mPlayController.getPlayBundle().getBoolean("isLive", false)) {
					return false;
				}
				switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					x = event.getRawX();
					if(null != mHandler) {
						mHandler.removeMessages(FADE_OUT);
					}
					return true;

				case MotionEvent.ACTION_MOVE:
					if(mProgressTime.getVisibility() != View.VISIBLE) {
						mProgressTime.setVisibility(View.VISIBLE);
					}
					moveProgress(event.getRawX() - x);
					x = event.getRawX();
					break;
				case MotionEvent.ACTION_UP:
					mHandler.sendEmptyMessageDelayed(FADE_OUT, sDefaultTimeout);
					mProgressTime.setVisibility(View.GONE);
					break;
				}
				return false;
			}
		});
	}
	
//	private boolean hasNext = false;
	
	/**
	 * 显示下一集按钮
	 */
	public void enableNextBtn() {
//		hasNext = true;
//		play_right.setEnabled(true);
//		play_right.setImageResource(R.drawable.pip_controller_next_selector);
	}
	/**
	 * 隐藏下一集按钮
	 */
	public void disableNextBtn() {
//		hasNext = false;
//		play_right.setEnabled(false);
//		play_right.setImageResource(R.drawable.pip_controller_next_03);
	}
	/**
	 * 非直播，可以触摸进度条下方区域手势调整进度
	 * @param offset
	 */
	public void moveProgress(float offset) {
		double temp = 1d * offset / UIs.getScreenWidth();
		int duration = player.getDuration();
		int position = player.getCurrentPosition() + (int)(duration * temp);
		if(position < 0) {
			position = 0;
		}
		player.seekTo(position);
		mProgressTime.setText(LetvUtil.stringForTime(position) + "/" + LetvUtil.stringForTime(duration));
		long pos = 1000L * position / duration;
		mProgressBar.setProgress((int) pos);
	}

	/**
	 * Show the controller on screen. It will go away automatically after 3
	 * seconds of inactivity.
	 */
	public void show() {
		show(sDefaultTimeout);
	}

	/**
	 * Disable pause or seek buttons if the stream cannot be paused or seeked.
	 * This requires the control interface to be a MediaPlayerControlExt
	 */
	private void disableUnsupportedButtons() {
		if (null != pip_video_play_controller_fullScreen_layout
				&& pip_video_play_controller_fullScreen_layout.getVisibility() == View.GONE) {
			pip_video_play_controller_fullScreen_layout.setVisibility(VISIBLE);
		}

		if (isLive) {

			if (null != mProgressBar) {
				mProgressBar.setVisibility(View.GONE);
			}

			TextView titleTextView = (TextView) this
					.findViewById(R.id.pip_video_play_controller_title);
			if (null != titleTextView) {
				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) titleTextView
						.getLayoutParams();
				RelativeLayout.LayoutParams newLayoutParams = new RelativeLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				newLayoutParams.addRule(ALIGN_PARENT_LEFT);
				newLayoutParams.topMargin = layoutParams.topMargin;
				newLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
				newLayoutParams.addRule(LEFT_OF, R.id.play_pause);

				/**
				 * android:layout_alignParentLeft="true"
				 * android:layout_alignParentTop="true"
				 * android:layout_marginTop="5dip"
				 * android:layout_toLeftOf="@+id/play_time"
				 * android:singleLine="true" android:text="VIDEO"
				 * android:textColor="#FFbfbfbf" android:textSize="16dip" />
				 */

				titleTextView.setLayoutParams(newLayoutParams);
			}

		}

		try {
			if (pauseButton != null && (null == player || !player.canPause())) {
				pauseButton.setEnabled(false);
			}

			/**
			 * 影片来源:1(1:专辑;3:视频)
			 */
//			if (album != null && total > 1 && !isWebPlay && hasNext) {
//				// 显示下一集按钮
//				play_right.setEnabled(true);
//				play_right.setImageResource(R.drawable.pip_controller_next_selector);
//			} else {
//				// 隐藏下一集按钮
//				play_right.setEnabled(false);
//				play_right.setImageResource(R.drawable.pip_controller_next_03);
//			}
		} catch (IncompatibleClassChangeError ex) {
			
		}
	}

	/**
	 * Show the controller on screen. It will go away automatically after
	 * 'timeout' milliseconds of inactivity.
	 * 
	 * @param timeout
	 *            The timeout in milliseconds. Use 0 to show the controller
	 *            until hide() is called.
	 */
	public void show(int timeout) {
		if (!showing) {
			setProgress();
			if (pauseButton != null) {
				pauseButton.requestFocus();
			}
			disableUnsupportedButtons();
			pip_video_play_controller_bottomLayout.setVisibility(View.VISIBLE);
			showControllerInAnim();
			showing = true;
		}
		updatePausePlay();
		mHandler.sendEmptyMessage(SHOW_PROGRESS);

		Message msg = mHandler.obtainMessage(FADE_OUT);
		if (timeout != 0) {

			mHandler.removeMessages(FADE_OUT);
			mHandler.sendMessageDelayed(msg, timeout);
		}
	}

	public void showControllerInAnim() {
		pip_video_play_controller_bottomLayout.startAnimation(AnimationUtils.loadAnimation(
				getContext(), R.anim.pip_pushup_in));
	}

	public void showControllerOutAnim() {
		pip_video_play_controller_bottomLayout.startAnimation(AnimationUtils.loadAnimation(
				getContext(), R.anim.pip_pushdown_out));
	}

	public boolean isShowing() {
		return showing;
	}

	/**
	 * Remove the controller from the screen.
	 */
	public void hide() {

		if (this == null)
			return;

		if (showing) {
			try {
				mHandler.removeMessages(SHOW_PROGRESS);
				showControllerOutAnim();
				pip_video_play_controller_bottomLayout.setVisibility(View.GONE);
			} catch (IllegalArgumentException ex) {
				Log.w("MediaController", "already removed");
			}
			showing = false;
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int pos;
			switch (msg.what) {
			case FADE_OUT:

				hide();
				break;
			case SHOW_PROGRESS:
				try {
					pos = setProgress();
					if (!dragging && showing && player.isPlaying()) {
						msg = obtainMessage(SHOW_PROGRESS);
						sendMessageDelayed(msg, 1000 - (pos % 1000));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				break;
			}
		}
	};

	private String stringForTime(int timeMs) {
		int totalSeconds = timeMs / 1000;

		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;

		formatBuilder.setLength(0);

		if (hours <= 0) {
			return formatter.format("%02d:%02d", minutes, seconds).toString();
		} else {
			return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
		}
	}

	private int setProgress() {
		if (player == null || dragging) {
			return 0;
		}
		int position = player.getCurrentPosition();
		int duration = player.getDuration();
		if (mProgressBar != null) {
			if (duration > 0) {
				// use long to avoid overflow
				long pos = 1000L * position / duration;
				mProgressBar.setProgress((int) pos);
			}
			int percent = player.getBufferPercentage();

			mProgressBar.setSecondaryProgress(percent * 10);
		}
		return position;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int keyCode = event.getKeyCode();
		if (event.getRepeatCount() == 0
				&& (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
						|| keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_SPACE)) {
			doPauseResume();
			show(sDefaultTimeout);
			if (pauseButton != null) {
				pauseButton.requestFocus();
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {
			if (player.isPlaying()) {
				player.pause();
				updatePausePlay();
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			// don't show the controls for volume adjustment
			return super.dispatchKeyEvent(event);
		} else if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
			hide();

			return true;
		} else {
			show(sDefaultTimeout);
		}
		return super.dispatchKeyEvent(event);
	}

	/**
	 * 从画中画跳转至主播放器监听事件
	 */
	public View.OnClickListener pipToMainPlayerListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (null != mPlayController) {
				mPlayController.updateVideoPosition();
			}
			if (mPlayController instanceof PipPlayAlbumController) {
				boolean isDownloadFile = ((PipPlayAlbumController)mPlayController).getIsLocalFile();
				LetvPipPlayFunction.pipToMainPlayer(context, mPlayController.getPlayBundle(), isDownloadFile);
			} else {
				LetvPipPlayFunction.pipToMainPlayer(context, mPlayController.getPlayBundle(), false);
			}
			
			if (null != mPlayController) {
				mPlayController.updateVideoPosition();
				mPlayController.onFinish();
			}
			
				LetvPipPlayFunction.closePipView(getContext());
			
		}
	};

	/**
	 * 返回
	 */
	private View.OnClickListener closeListener = new View.OnClickListener() {

		public void onClick(View v) {
			// Pip关闭Code
			if (null != mPlayController) {
				mPlayController.onFinish();
			}
			LetvPipPlayFunction.closePipView(getContext());
		}
	};

	public void exitPipPlayer() {
		Animation endAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.pip_end_alpha);
		endAnimation.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (null != mPlayController) {
					mPlayController.onFinish();
				}
				// Remove 3.8
				// DataStatistics.getInstance().sendActionCode(getContext(),
				// DataConstant.ACTION.PLAYER.FINISH, null, LetvUtil.getUID(),
				// LetvUtil.getPcode());

				LetvPipPlayFunction.closePipView(getContext());
			}
		});

		root.startAnimation(endAnimation);
	}

	private View.OnClickListener pauseListener = new View.OnClickListener() {
		public void onClick(View v) {
			doPauseResume();
			show(sDefaultTimeout);
		}
	};

	private void updatePausePlay() {
		if (root == null || pauseButton == null)
			return;

		if (player != null && player.isPlaying()) {
			pauseButton.setImageResource(R.drawable.pip_pause_selector);
		} else {
			pauseButton.setImageResource(R.drawable.pip_player_selector);
		}
	}

	private void doPauseResume() {
		if (player.isPlaying()) {
			player.pause();
//			LetvUtil.ireTrackerEventEnd(context, realUrl, filePath);
			
			// Remove 3.8 播放器模块暂停动作
			// DataStatistics.getInstance().sendActionCode(getContext(),
			// DataConstant.ACTION.PLAYER.PAUSE, null, LetvUtil.getUID(),
			// LetvUtil.getPcode());
		} else {
			player.start();
//			if(context != null) {
//				LetvUtil.ireTrackerEventStart(context, album, mVideo, realUrl, filePath);
//			}

			// Remove 3.8 播放器模块播放动作
			// DataStatistics.getInstance().sendActionCode(getContext(),
			// DataConstant.ACTION.PLAYER.PLAY, null, LetvUtil.getUID(),
			// LetvUtil.getPcode());
		}
		updatePausePlay();
	}

	// There are two scenarios that can trigger the seekbar listener to trigger:
	//
	// The first is the user using the touchpad to adjust the posititon of the
	// seekbar's thumb. In this case onStartTrackingTouch is called followed by
	// a number of onProgressChanged notifications, concluded by
	// onStopTrackingTouch.
	// We're setting the field "mDragging" to true for the duration of the
	// dragging
	// session to avoid jumps in the position in case of ongoing playback.
	//
	// The second scenario involves the user operating the scroll ball, in this
	// case there WON'T BE onStartTrackingTouch/onStopTrackingTouch
	// notifications,
	// we will simply apply the updated position without suspending regular
	// updates.
	private OnSeekBarChangeListener seekListener = new OnSeekBarChangeListener() {
		public void onStartTrackingTouch(SeekBar bar) {
			show(3600000);

			dragging = true;

			// By removing these pending progress messages we make sure
			// that a) we won't update the progress while the user adjusts
			// the seekbar and b) once the user is done dragging the thumb
			// we will post one of these messages to the queue again and
			// this ensures that there will be exactly one message queued up.
			mHandler.removeMessages(SHOW_PROGRESS);
		}

		public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
			if (!fromuser) {
				// We're not interested in programmatically generated changes to
				// the progress bar's position.
				return;
			}

			long duration = player.getDuration();
			long newposition = (duration * progress) / 1000L;
			player.seekTo((int) newposition);
		}

		public void onStopTrackingTouch(SeekBar bar) {
			dragging = false;
			setProgress();
			updatePausePlay();
			show(sDefaultTimeout);

			// Ensure that progress is properly updated in the future,
			// the call to show() does not guarantee this because it is a
			// no-op if we are already showing.
			mHandler.sendEmptyMessage(SHOW_PROGRESS);

			if (mPlayController != null) {
				mPlayController.onStopTrackingTouch();
			}
		}
	};

	@Override
	public void setEnabled(boolean enabled) {

		if (pauseButton != null) {
			pauseButton.setEnabled(enabled);
		}

//		if (play_right != null) {
//			play_right.setEnabled(enabled);
//		}

		if (mProgressBar != null) {
			mProgressBar.setEnabled(enabled);
		}
		disableUnsupportedButtons();
		super.setEnabled(enabled);
	}

	/**
	 * 下一集单击事件
	 */
	private View.OnClickListener forwardListener = new View.OnClickListener() {
		
		public void onClick(View v) {
			mPlayController.next();
		}
	};

	private Episode getEpisode() {
		//delete by zlb
//		if (album == null) {
//			return null;
//		}
//
//		if (album.getEpsiodes() == null) {
//			return null;
//		}
//		int pageNum = order / PageData.PAGE_REQUEST_COUNT;
//		if (album.getEpsiodes().size() <= order - pageNum * PageData.PAGE_REQUEST_COUNT) {
//			return null;
//		}
//
//		return album.getEpsiodes().get(order - pageNum * PageData.PAGE_REQUEST_COUNT);
		return null;
		//end by zlb
	}

	private void finishActivity(Context context) {

		if (context instanceof Activity) {
			if (mPlayController != null) {
				mPlayController.onFinish();
			}
			Activity activity = (Activity) context;
			activity.finish();
		}
	}

	// public interface MediaPlayerControl {
	// void start();
	//
	// void pause();
	//
	// void stopPlayback();
	//
	// boolean toggleScreen();
	//
	// String getVideoTitle();
	//
	// int getDuration();
	//
	// int getCurrentPosition();
	//
	// void seekTo(int pos);
	//
	// boolean isPlaying();
	//
	// int getBufferPercentage();
	//
	// boolean canPause();
	//
	// boolean canSeekBackward();
	//
	// boolean canSeekForward();
	// }

	public void updateSkipState() {
		if (play_skip_begin != null && play_skip_end != null) {
			if (album != null && getEpisode() != null && PreferencesManager.getInstance().isSkip()) {
				if (getEpisode().getBtime() > 0) {
					int totalWidth = getWidth();
					int position = (int) (getEpisode().getBtime() * totalWidth * 1.0 * 1000 / player
							.getDuration());
					RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) play_skip_begin
							.getLayoutParams();
					params.leftMargin = position;
					play_skip_begin.setLayoutParams(params);
					play_skip_begin.setVisibility(View.VISIBLE);
				} else {
					play_skip_begin.setVisibility(View.GONE);
				}
				if (getEpisode().getEtime() > 0) {
					int totalWidth = getWidth();
					int position = (int) (getEpisode().getEtime() * totalWidth * 1.0 * 1000 / player
							.getDuration());
					RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) play_skip_end
							.getLayoutParams();
					params.leftMargin = position;
					play_skip_end.setLayoutParams(params);
					play_skip_end.setVisibility(View.VISIBLE);
				} else {
					play_skip_end.setVisibility(View.GONE);
				}
			}
		}
	}

	public boolean isLive() {
		return isLive;
	}

	public void setLive(boolean isLive) {
		this.isLive = isLive;
	}

	@Override
	public void setAnchorView(View view) {

	}

	public void unregisterReceiver() {

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

	}

	@Override
	public void adjustVolumeSeekBar() {

	}

	@Override
	public void adjustSoundDrawable() {
	}

	@Override
	public void setMbundle(Bundle mBundle) {
	}

	@Override
	public void updateLiveChannelProgram() {
	}

	@Override
	public void setOnlyLive(boolean b) {

	}

	@Override
	public void setHd(boolean b) {

	}

	@Override
	public void setHasLow(boolean b) {

	}

	@Override
	public void setHasHigh(boolean b) {

	}

	@Override
	public void updateEpisodeLayout(AlbumNew mAlbum) {
		if (mAlbum != null && mAlbum.getEpisode() > 0) {
			album = mAlbum;
		}

	}

	@Override
	public void setPlayController(PipPlayController playController) {
		mPlayController = playController;
	}
	
	public void initNextBtn(int num) {
		total = num;
		if(num > 1) {
			enableNextBtn();
		} else {
			disableNextBtn();
		}
	}
}
