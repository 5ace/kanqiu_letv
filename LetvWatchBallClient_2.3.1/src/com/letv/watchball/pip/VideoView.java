/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.letv.watchball.pip;

import java.io.IOException;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout.LayoutParams;

import com.letv.watchball.ui.impl.BasePlayActivity;
import com.letv.watchball.utils.LetvUtil;
import com.letv.watchball.utils.LogInfo;
import com.media.NativeInfos;
import com.media.NativePlayer;
import com.media.NativePlayer.OnLoadingPerListener;

/**
 * Displays a video file. The VideoView class can load images from various
 * sources (such as resources or content providers), takes care of computing its
 * measurement from the video so that it can be used in any layout manager, and
 * provides various display options such as scaling and tinting.
 */
public class VideoView extends SurfaceView implements MediaPlayerControl {
	protected String TAG = "VideoView";
	// settable by the client
	protected Uri mUri;
	protected int mDuration;
	protected int mAdDuraction;

	// all possible internal states
	protected static final int STATE_ERROR = -1;
	protected static final int STATE_IDLE = 0;
	protected static final int STATE_PREPARING = 1;
	protected static final int STATE_PREPARED = 2;
	protected static final int STATE_PLAYING = 3;
	protected static final int STATE_PAUSED = 4;
	protected static final int STATE_PLAYBACK_COMPLETED = 5;

	// mCurrentState is a VideoView object's current state.
	// mTargetState is the state that a method caller intends to reach.
	// For instance, regardless the VideoView object's current state,
	// calling pause() intends to bring the object to a target state
	// of STATE_PAUSED.
	protected int mCurrentState = STATE_IDLE;
	protected int mTargetState = STATE_IDLE;

	// All the stuff we need for playing and showing a video
	protected SurfaceHolder mSurfaceHolder = null;
	protected MediaPlayer mMediaPlayer = null;
	protected Context mContext;
	protected int mVideoWidth;
	protected int mVideoHeight;
	protected int mSurfaceWidth;
	protected int mSurfaceHeight;
	protected BaseMediaController mBaseMediaController;
	protected OnLoadingPerListener mOnLoadingPerListener;
	protected OnCompletionListener mOnCompletionListener;
	protected MediaPlayer.OnPreparedListener mOnPreparedListener;
	protected int mCurrentBufferPercentage;
	protected OnErrorListener mOnErrorListener;
	protected boolean fullScreen;// 是否全屏模式
	protected String videoTitle;// 视频标题
	protected int mSeekWhenPrepared; // recording the seek position while
										// preparing
	protected boolean mCanPause;
	protected boolean mCanSeekBack;
	protected boolean mCanSeekForward;

	protected boolean isBufferCount = true;// 添加字段，播放统计时需要
	protected boolean firstPlay = true;// 当前VideoView所代表的播放器首次进行播放
	protected boolean firstInitScreen = true;

	protected boolean isOnPause = false;

	protected int screenWidth;
	protected int screenHeight;

	protected boolean isPlayingAd = false;

	public VideoView(Context context) {
		super(context);
		this.mContext = context;
		initVideoView();
	}

	public VideoView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		this.mContext = context;
		initVideoView();
	}

	public VideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		initVideoView();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
		int height = getDefaultSize(mVideoHeight, heightMeasureSpec);

		if (LetvUtil.isDebug()) {
			System.out.println("onMeasure.............mVideoWidth:" + mVideoWidth
					+ ",mVideoHeight:" + mVideoHeight + ",widthMeasureSpec:" + widthMeasureSpec
					+ ",heightMeasureSpec:" + heightMeasureSpec);
		}
		setMeasuredDimension(width, height);
	}

	public int resolveAdjustedSize(int desiredSize, int measureSpec) {
		int result = desiredSize;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		switch (specMode) {
		case MeasureSpec.UNSPECIFIED:
			/*
			 * Parent says we can be as big as we want. Just don't be larger
			 * than max size imposed on ourselves.
			 */
			result = desiredSize;
			break;

		case MeasureSpec.AT_MOST:
			/*
			 * Parent says we can be as big as we want, up to specSize. Don't be
			 * larger than specSize, and don't be larger than the max size
			 * imposed on ourselves.
			 */
			result = Math.min(desiredSize, specSize);
			break;

		case MeasureSpec.EXACTLY:
			// No choice. Do what we are told.
			result = specSize;
			break;
		}
		return result;
	}

	protected void initVideoView() {

		if ((mContext instanceof Activity)) {
			Activity activity = (Activity) mContext;
			Display display = activity.getWindowManager().getDefaultDisplay();
			screenWidth = display.getWidth();
			screenHeight = display.getHeight();
		}

		mVideoWidth = 0;
		mVideoHeight = 0;
		getHolder().addCallback(mSHCallback);

		if (LetvUtil.isDebug()) {
			System.out.println("NativeInfos.ifNativePlayer():" + NativeInfos.ifNativePlayer());
		}
		if (!NativeInfos.ifNativePlayer()) {
			getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		// setKeepScreenOn(true);
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();
		mCurrentState = STATE_IDLE;
		mTargetState = STATE_IDLE;
	}

	/**
	 * 设置播放路径
	 * 
	 * @param path
	 */
	public void setVideoPath(String path) {
		setVideoURI(Uri.parse(path));
	}

	/**
	 * 设置播放地址
	 * 
	 * @param uri
	 */
	public void setVideoURI(Uri uri) {
		try {
			mUri = uri;
			mSeekWhenPrepared = 0;
			openVideo();
			requestLayout();
			invalidate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 停止播放
	 */
	public synchronized void stopPlayback() {// 改动，加了线程
		if (LetvUtil.isDebug()) {
			System.out.println("stopPlayback...................mMediaPlayer:" + mMediaPlayer);
		}

		mCurrentState = STATE_IDLE;
		mTargetState = STATE_IDLE;
		if (mMediaPlayer != null) {
			final MediaPlayer m = mMediaPlayer;
			mMediaPlayer = null;
			new Thread() {
				public void run() {
					try {
						if (m != null) {
							m.stop();
							m.release();
							LogInfo.log("--------------stopPlayback----------------");
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
	}

	/**
	 * 打开并初始化播放器
	 */
	protected synchronized void openVideo() {

		if (LetvUtil.isDebug()) {
			System.out.println("openVideo.................mSurfaceHolder:" + mSurfaceHolder
					+ ",isOnPause:" + isOnPause + ",mCurrentState:" + mCurrentState + ",mUri:"
					+ mUri);
		}

		if (mUri == null || mSurfaceHolder == null || isOnPause) {
			// not ready for playback just yet, will try again later
			return;
		}
		// Tell the music playback service to pause
		// TODO: these constants need to be published somewhere in the
		// framework.
		Intent i = new Intent("com.android.music.musicservicecommand");
		i.putExtra("command", "pause");
		mContext.sendBroadcast(i);

		// we shouldn't clear the target state, because somebody might have
		// called start() previously
		// release(false); 改动，每次开始播放时都是先释放去掉
		try {
			if (mMediaPlayer == null) {
				if (NativeInfos.ifNativePlayer()) {
					mMediaPlayer = new NativePlayer();
					// 添加如下这段代码，设置监听
					((NativePlayer) mMediaPlayer).setOnLoadingPerListener(mLoadingPerListener);
//					Log.e("xue", "软解");
				} else {
					mMediaPlayer = new MediaPlayer();
//					Log.e("xue", "系统");
				}

				if (LetvUtil.isDebug()) {
					System.out.println("openVideo...............mMediaPlayer:" + mMediaPlayer);
				}
				mMediaPlayer.reset();
				mMediaPlayer.setOnPreparedListener(mPreparedListener);
				mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
				mDuration = -1;
				mMediaPlayer.setOnCompletionListener(mCompletionListener);
				mMediaPlayer.setOnErrorListener(mErrorListener);
				mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
				mCurrentBufferPercentage = 0;
				mMediaPlayer.setDataSource(mContext, mUri);
				mMediaPlayer.setDisplay(mSurfaceHolder);
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mMediaPlayer.setScreenOnWhilePlaying(true);
				mMediaPlayer.prepareAsync();
				// we don't set the target state here either, but preserve the
				// target state that was there before.
				mCurrentState = STATE_PREPARING;
			}
			// attachMediaController();
		} catch (IOException ex) {
			ex.printStackTrace();
			Log.w(TAG, "Unable to open content: " + mUri, ex);
			mCurrentState = STATE_ERROR;
			mTargetState = STATE_ERROR;
			mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
			return;
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
			Log.w(TAG, "Unable to open content: " + mUri, ex);
			mCurrentState = STATE_ERROR;
			mTargetState = STATE_ERROR;
			mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
			return;
		}
	}

	public void setMediaController(BaseMediaController controller) {
		if (mBaseMediaController != null) {
			mBaseMediaController.hide();
		}
		mBaseMediaController = controller;
		// attachMediaController();
	}

	/**
	 * 绑定播放控件
	 */
	protected void attachMediaController() {
		if (mMediaPlayer != null && mBaseMediaController != null) {
			mBaseMediaController.setMediaPlayer(this);
			View anchorView = this.getParent() instanceof View ? (View) this.getParent() : this;
			mBaseMediaController.setAnchorView(anchorView);
			mBaseMediaController.setEnabled(isInPlaybackState());
		}
	}

	MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
		public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

			if (isInPlaybackState()) {
				mVideoWidth = mp.getVideoWidth();
				mVideoHeight = mp.getVideoHeight();
			}

			if (LetvUtil.isDebug()) {
				System.out
						.println("onVideoSizeChanged............mVideoWidth:" + mVideoWidth
								+ ",mVideoHeight:" + mVideoHeight + ",width:" + width + ",height:"
								+ height);
			}

			if (mVideoWidth != 0 && mVideoHeight != 0) {
				getHolder().setFixedSize(mVideoWidth, mVideoHeight);
				remodelScreen(fullScreen);
				// if (firstInitScreen) {
				// remodelScreen(false);// 默认情况下为非全屏播放
				// firstInitScreen = false;
				// }
			}
		}
	};

	MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
		public void onPrepared(MediaPlayer mp) {
			attachMediaController();
			if (mMediaPlayer == null) {
				mMediaPlayer = mp;
			}

			if (LetvUtil.isDebug()) {
				System.out.println("OnPreparedListener..........................mCurrentState:"
						+ mCurrentState);
			}

			mCurrentState = STATE_PREPARED;

			// // Get the capabilities of the player for this stream
			// Metadata data = mp.getMetadata(MediaPlayer.METADATA_ALL,
			// MediaPlayer.BYPASS_METADATA_FILTER);
			//
			// if (data != null) {
			// mCanPause = !data.has(Metadata.PAUSE_AVAILABLE)
			// || data.getBoolean(Metadata.PAUSE_AVAILABLE);
			// mCanSeekBack = !data.has(Metadata.SEEK_BACKWARD_AVAILABLE)
			// || data.getBoolean(Metadata.SEEK_BACKWARD_AVAILABLE);
			// mCanSeekForward = !data.has(Metadata.SEEK_FORWARD_AVAILABLE)
			// || data.getBoolean(Metadata.SEEK_FORWARD_AVAILABLE);
			// } else {
			// mCanPause = mCanSeekForward = mCanSeekForward = true;
			// }

			mCanPause = mCanSeekBack = mCanSeekForward = true;

			if (mOnPreparedListener != null) {
				mOnPreparedListener.onPrepared(mMediaPlayer);
			}
			if (mBaseMediaController != null) {
				mBaseMediaController.setEnabled(true);
			}

			if (isInPlaybackState()) {
				try {
					mVideoWidth = mp.getVideoWidth();
					mVideoHeight = mp.getVideoHeight();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				}
			}

			int seekToPosition = mSeekWhenPrepared; // mSeekWhenPrepared may be
													// changed after seekTo()
													// call
			if (seekToPosition != 0) {
				seekTo(seekToPosition);
			}

			if (LetvUtil.isDebug()) {
				System.out.println("onPrepared............mVideoWidth:" + mVideoWidth
						+ ",mVideoHeight:" + mVideoHeight);
			}

			if (mVideoWidth != 0 && mVideoHeight != 0) {
				// Log.i("@@@@", "video size: " + mVideoWidth +"/"+
				// mVideoHeight);
				getHolder().setFixedSize(mVideoWidth, mVideoHeight);

				if (firstInitScreen) {
					remodelScreen(false);// 默认情况下为非全屏播放
					firstInitScreen = false;
				}

				if (mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
					// We didn't actually change the size (it was already at the
					// size
					// we need), so we won't get a "surface changed" callback,
					// so
					// start the video here instead of in the callback.

					if (mTargetState == STATE_PLAYING) {
						start();
						/*
						 * 为了不让开始时显示控制栏而先注释掉 if (mBaseMediaController != null &&
						 * getWindowToken() != null) {
						 * mBaseMediaController.show();
						 * 
						 * }
						 */
					} else if (!isPlaying() && (seekToPosition != 0 || getCurrentPosition() > 0)) {
						/*
						 * 为了不让开始时显示控制栏而先注释掉 if (mBaseMediaController != null) {
						 * mBaseMediaController.show(0); }
						 */
					}
				}
			} else {
				// We don't know the video size yet, but should start anyway.
				// The video size might be reported to us later.
				if (mTargetState == STATE_PLAYING) {
					start();
				}
			}

			if (firstPlay) {
				firstPlay = false;

				if (mTargetState == STATE_PLAYING) {
					start();
				}
				/*
				 * 为了不让开始时显示控制栏而先注释掉 if (mBaseMediaController != null &&
				 * getWindowToken() != null) { mBaseMediaController.show(); }
				 */
			}

			KeyguardManager keyguardManager = (KeyguardManager) getContext().getSystemService(
					Context.KEYGUARD_SERVICE);
			if (LetvUtil.isDebug()) {
				System.out.println("keyguardManager.inKeyguardRestrictedInputMode():"
						+ keyguardManager.inKeyguardRestrictedInputMode());
			}
			if (keyguardManager.inKeyguardRestrictedInputMode()) {
				pause();
			}
		}
	};

	protected MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
		public void onCompletion(MediaPlayer mp) {
			mCurrentState = STATE_PLAYBACK_COMPLETED;
			mTargetState = STATE_PLAYBACK_COMPLETED;
			if (mBaseMediaController != null) {
				mBaseMediaController.hide();
			}
			if (mOnCompletionListener != null) {
				mOnCompletionListener.onCompletion(mMediaPlayer);
			}
		}
	};

	protected NativePlayer.OnLoadingPerListener mLoadingPerListener = new NativePlayer.OnLoadingPerListener() {
		@Override
		public void onLoadingPer(MediaPlayer mp, int per) {
			// per即为缓冲比的大小
			if (mOnLoadingPerListener != null) {
				mOnLoadingPerListener.onLoadingPer(mp, per);
			}
		}
	};

	protected MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
		public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
			mCurrentState = STATE_ERROR;
			mTargetState = STATE_ERROR;
			if (mBaseMediaController != null) {
				mBaseMediaController.hide();
			}

			/* If an error handler has been supplied, use it and finish. */
			if (mOnErrorListener != null) {
				if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err)) {
					return true;
				}
			}

			/*
			 * Otherwise, pop up an error dialog so the user knows that
			 * something bad has happened. Only try and pop up the dialog if
			 * we're attached to a window. When we're going away and no longer
			 * have a window, don't bother showing the user an error.
			 */
			if (getWindowToken() != null) {

				// Resources r = mContext.getResources();
				// int messageId;
				//
				// if (framework_err ==
				// MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {
				// messageId =
				// com.android.internal.R.string.VideoView_error_text_invalid_progressive_playback;
				// } else {
				// messageId =
				// com.android.internal.R.string.VideoView_error_text_unknown;
				// }
				//
				// new AlertDialog.Builder(mContext)
				// .setTitle(com.android.internal.R.string.VideoView_error_title)
				// .setMessage(messageId)
				// .setPositiveButton(com.android.internal.R.string.VideoView_error_button,
				// new DialogInterface.OnClickListener() {
				// public void onClick(DialogInterface dialog, int whichButton)
				// {
				// /* If we get here, there is no onError listener, so
				// * at least inform them that the video is over.
				// */
				// if (mOnCompletionListener != null) {
				// mOnCompletionListener.onCompletion(mMediaPlayer);
				// }
				// }
				// })
				// .setCancelable(false)
				// .show();
			}
			return true;
		}
	};

	protected MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
		public void onBufferingUpdate(MediaPlayer mp, int percent) {
			mCurrentBufferPercentage = percent;

		}
	};

	/**
	 * Register a callback to be invoked when the media file is loaded and ready
	 * to go.
	 * 
	 * @param l
	 *            The callback that will be run
	 */
	public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {
		mOnPreparedListener = l;
	}

	/**
	 * Register a callback to be invoked when the end of a media file has been
	 * reached during playback.
	 * 
	 * @param l
	 *            The callback that will be run
	 */
	public void setOnCompletionListener(OnCompletionListener l) {
		mOnCompletionListener = l;
	}

	public void setOnLoadingPerListener(OnLoadingPerListener l) {
		mOnLoadingPerListener = l;
	}

	/**
	 * Register a callback to be invoked when an error occurs during playback or
	 * setup. If no listener is specified, or if the listener returned false,
	 * VideoView will inform the user of any errors.
	 * 
	 * @param l
	 *            The callback that will be run
	 */
	public void setOnErrorListener(OnErrorListener l) {
		mOnErrorListener = l;
	}

	SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
		public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
			if (LetvUtil.isDebug()) {
				System.out.println("surfaceChanged.....................holder:" + holder + ",w:"
						+ w + ",h:" + h);
			}
			mSurfaceWidth = w;
			mSurfaceHeight = h;

			boolean isValidState = (mTargetState == STATE_PLAYING);
			boolean hasValidSize = (mVideoWidth == w && mVideoHeight == h);
			if (mMediaPlayer != null && isValidState && hasValidSize) {
				if (mSeekWhenPrepared != 0) {
					seekTo(mSeekWhenPrepared);
				}
				start();
			}
		}

		public void surfaceCreated(SurfaceHolder holder) {
			if (LetvUtil.isDebug()) {
				System.out.println("surfaceCreated.....................holder:" + holder);
			}
			try {
				mSurfaceHolder = holder;
				openVideo();// 改动，只有手动setvideourl才加载视频
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			if (LetvUtil.isDebug()) {
				System.out.println("surfaceDestroyed.....................holder:" + holder);
			}

			// after we return from this we can't use the surface any more
			mSurfaceHolder = null;
			if (mBaseMediaController != null)
				mBaseMediaController.hide();
			// release(true);//改动，默认界面消失后，播放停止，去掉后需要手动调用停止方法
		}
	};

	/**
	 * 释放播放器
	 * 
	 * @param cleartargetstate
	 */
	protected synchronized void release(boolean cleartargetstate) {
		if (mMediaPlayer != null) {
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
			mCurrentState = STATE_IDLE;
			if (cleartargetstate) {
				mTargetState = STATE_IDLE;
			}
		}
	}

	@Override
	public boolean onTrackballEvent(MotionEvent ev) {
		if (isInPlaybackState() && mBaseMediaController != null) {
			toggleMediaControlsVisiblity();
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		BasePlayActivity ac = null;
		if (mContext != null && mContext instanceof BasePlayActivity) {
			ac = (BasePlayActivity) mContext;
		}

//		if (ac != null && !ac.isLock()) {
		if (ac != null) {
			if (mCurrentState == STATE_PREPARED || mCurrentState == STATE_PLAYING
					|| mCurrentState == STATE_PAUSED) {
				if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
						|| keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
					return true;
				}
			}

			boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK
					&& keyCode != KeyEvent.KEYCODE_VOLUME_UP
					&& keyCode != KeyEvent.KEYCODE_VOLUME_DOWN && keyCode != KeyEvent.KEYCODE_MENU
					&& keyCode != KeyEvent.KEYCODE_CALL && keyCode != KeyEvent.KEYCODE_ENDCALL;
			if (isInPlaybackState() && isKeyCodeSupported && mBaseMediaController != null) {
				if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
						|| keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
					if (event.getAction() == KeyEvent.ACTION_DOWN) {
						if (mMediaPlayer.isPlaying()) {
							pause();
							mBaseMediaController.show();
						} else {
							start();
							mBaseMediaController.hide();
						}
						return true;
					}
				} else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP && mMediaPlayer.isPlaying()) {
					pause();
					mBaseMediaController.show();
				} else {
					toggleMediaControlsVisiblity();
				}
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 显示或隐藏播放控制界面
	 */
	protected void toggleMediaControlsVisiblity() {
		if (mBaseMediaController.isShowing()) {
			mBaseMediaController.hide();
		} else {
			mBaseMediaController.show();
		}
	}

	/**
	 * 开始播放
	 */
	public synchronized void start() {
		if (isInPlaybackState()) {
			try {
				mMediaPlayer.start();
				mCurrentState = STATE_PLAYING;
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
		mTargetState = STATE_PLAYING;
	}

	/**
	 * 暂停播放
	 */
	public synchronized void pause() {
		if (isInPlaybackState()) {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.pause();
				mCurrentState = STATE_PAUSED;
			}
		}
		mTargetState = STATE_PAUSED;
	}

	/**
	 * 根据是否全屏播放来设置播放界面尺寸
	 * 
	 * @param fullScreen
	 */
	protected void remodelScreen(boolean fullScreen) {
		this.fullScreen = fullScreen;

		if (fullScreen) {

			int videoWidth = mVideoWidth;
			int videoHeight = mVideoHeight;
			int mWidth = screenWidth;
			int mHeight = screenHeight;

			// if (LetvUtil.isDebug()) {
			// System.out.println("fullScreen............................");
			// System.out.println("videoWidth:" + videoWidth + ",videoHeight:" +
			// videoHeight);
			// System.out.println("screenWidth:" + screenWidth +
			// ",screenHeight:" + screenHeight);
			// System.out.println("videoScale:" + ((float) videoWidth /
			// videoHeight)
			// + ",screenScale:" + ((float) screenWidth / screenHeight));
			// }

			if (videoWidth > 0 && videoHeight > 0) {
				if ((videoWidth * screenHeight) >= (videoHeight * screenWidth)) {
					videoHeight = mHeight;
					videoWidth = (int) ((float) videoWidth * mHeight / mVideoHeight);
				} else {
					videoWidth = mWidth;
					videoHeight = (int) ((float) videoHeight * mWidth / mVideoWidth);
				}

				if (LetvUtil.isDebug()) {
					System.out.println("finalWidth:" + videoWidth + ",finalHeight:" + videoHeight);
				}

				setVideoViewScale(videoWidth, videoHeight);
			}

		} else {
			int videoWidth = mVideoWidth;
			int videoHeight = mVideoHeight;
			int mWidth = screenWidth;
			int mHeight = screenHeight;

			// if (LetvUtil.isDebug()) {
			// System.out.println("normalScreen............................");
			// System.out.println("videoWidth:" + videoWidth + ",videoHeight:" +
			// videoHeight);
			// System.out.println("screenWidth:" + screenWidth +
			// ",screenHeight:" + screenHeight);
			// System.out.println("videoScale:" + ((float) videoWidth /
			// videoHeight)
			// + ",screenScale:" + ((float) screenWidth / screenHeight));
			// }

			if (videoWidth > 0 && videoHeight > 0) {
				if ((videoWidth * screenHeight) >= (videoHeight * screenWidth)) {
					videoWidth = mWidth;
					videoHeight = (int) ((float) videoHeight * mWidth / mVideoWidth);
				} else {
					videoHeight = mHeight;
					videoWidth = (int) ((float) videoWidth * mHeight / mVideoHeight);
				}

				if (LetvUtil.isDebug()) {
					System.out.println("finalWidth:" + videoWidth + ",finalHeight:" + videoHeight);
				}

				setVideoViewScale(videoWidth, videoHeight);
			}
		}

	}

	/**
	 * 切换全屏和非全屏
	 */
	public boolean toggleScreen() {

		fullScreen = !fullScreen;
		remodelScreen(fullScreen);

		return fullScreen;
	}

	/**
	 * 切换全屏和非全屏
	 */
	public boolean toggleScreen(boolean fullScreen) {

		this.fullScreen = fullScreen;
		remodelScreen(this.fullScreen);

		return this.fullScreen;
	}

	/**
	 * 设置播放器控件的大小
	 * 
	 * @param width
	 * @param height
	 */
	protected void setVideoViewScale(int width, int height) {
		LayoutParams lp = (LayoutParams) this.getLayoutParams();
		lp.height = height;
		lp.width = width;

		if (NativeInfos.ifNativePlayer()) {
			lp.leftMargin = -1000;
			lp.rightMargin = -1000;
			lp.topMargin = -1000;
			lp.bottomMargin = -1000;
		}

		setLayoutParams(lp);
	}

	/**
	 * 得到播放总时长
	 */
	public synchronized int getDuration() {
		if (isInPlaybackState()) {
			if (mDuration > 0) {
				return mDuration;
			}
			mDuration = mMediaPlayer.getDuration() - mAdDuraction * 1000;
			return mDuration;
		}
		mDuration = -1;
		return mDuration;
	}

	/**
	 * 得到当前播放时长
	 */
	public synchronized int getCurrentPosition() {
		if (isInPlaybackState()) {
			int t = (mMediaPlayer.getCurrentPosition() - mAdDuraction * 1000);
			if (t > 0) {
				isPlayingAd = false;
				return t;
			} else {
				isPlayingAd = true;
				return t;
			}
		}
		return 0;
	}

	private synchronized void seekToAd(int msec) {
		if (isInPlaybackState()) {
			int adt = mAdDuraction * 1000;
			try {
				if (adt + msec <= 0) {
					msec = 0;
				} else {
					msec = adt + msec;
				}

				mMediaPlayer.seekTo(msec);
				mSeekWhenPrepared = 0;
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		} else {
			mSeekWhenPrepared = msec;
		}
	}

	public synchronized void seekTo(int msec) {
		if (msec < 0) {
			seekToAd(msec);
			return;
		}
		if (isInPlaybackState()) {
			int adt = mAdDuraction * 1000;
			try {
				if (msec <= 0) {
					msec = 1 * 1000;
				}

				msec += adt;
				mMediaPlayer.seekTo(msec);
				mSeekWhenPrepared = 0;
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		} else {
			mSeekWhenPrepared = msec;
		}
	}

	/**
	 * 播放器是否正在播放
	 */
	public synchronized boolean isPlaying() {
		return isInPlaybackState() && mMediaPlayer.isPlaying();
	}

	/**
	 * 播放器是否正在播放
	 */
	public synchronized boolean isPlayingAd() {
		return isPlayingAd;
	}

	/**
	 * 得到当前播放缓冲百分比
	 */
	public int getBufferPercentage() {
		if (mMediaPlayer != null) {
			return mCurrentBufferPercentage;
		}
		return 0;
	}

	/**
	 * 播放器是否处于播放状态
	 * 
	 * @return
	 */
	public synchronized boolean isInPlaybackState() {
		return (mMediaPlayer != null && mCurrentState != STATE_ERROR && mCurrentState != STATE_IDLE && mCurrentState != STATE_PREPARING);
	}

	/**
	 * 设置视频标题，由于标题在mediaController中显示，所以本方法要在设置mediaController之前调用
	 * 
	 * @param videoTitle
	 *            视频标题
	 */
	public void setVideoTitle(String videoTitle) {
		this.videoTitle = videoTitle;
	}

	public boolean canPause() {
		return mCanPause;
	}

	public boolean canSeekBackward() {
		return mCanSeekBack;
	}

	public boolean canSeekForward() {
		return mCanSeekForward;
	}

	public String getVideoTitle() {
		return videoTitle;
	}

	public void onPause() {
		isOnPause = true;
	}

	public void onResume() {
		isOnPause = false;
	}

	@Override
	public int getScreenWidth() {
		return screenWidth;
	}

	@Override
	public int getScreenHeight() {
		return screenHeight;
	}

	public int getmAdDuraction() {
		return mAdDuraction;
	}

	public void setmAdDuraction(int mAdDuraction) {
		if (mAdDuraction > 0) {
			isPlayingAd = true;
		}
		this.mAdDuraction = mAdDuraction;
	}
}
