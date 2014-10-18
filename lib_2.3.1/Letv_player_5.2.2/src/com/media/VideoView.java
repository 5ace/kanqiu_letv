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

//package com.letv.android.screen.client.views;
package com.media;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.MediaController;
import android.widget.RelativeLayout.LayoutParams;

//import com.letv.android.screen.client.ui.PlayerActivity.CancelActivity;

/**
 * Displays a video file. The VideoView class can load images from various
 * sources (such as resources or content providers), takes care of computing its
 * measurement from the video so that it can be used in any layout manager, and
 * provides various display options such as scaling and tinting.
 */
public class VideoView extends SurfaceView implements MediaController.MediaPlayerControl {
	private String TAG = "VideoView";
	// settable by the client
	private Uri mUri;
	private int mDuration;

	// all possible internal states
	public static final int STATE_ERROR = -1;
	public static final int STATE_IDLE = 0;
	public static final int STATE_PREPARING = 1;
	public static final int STATE_PREPARED = 2;
	public static final int STATE_PLAYING = 3;
	public static final int STATE_PAUSED = 4;
	public static final int STATE_PLAYBACK_COMPLETED = 5;
	public static final int STATE_STOPBACK = 6;
	public static final int STATE_ENFORCEMENT = 7;

	// mCurrentState is a VideoView object's current state.
	// mTargetState is the state that a method caller intends to reach.
	// For instance, regardless the VideoView object's current state,
	// calling pause() intends to bring the object to a target state
	// of STATE_PAUSED.
	private int mCurrentState = STATE_IDLE;
	private int mTargetState = STATE_IDLE;

	private final int FORWARD_TIME = 15000;
	private final int REWIND_TIME = 15000;

	// All the stuff we need for playing and showing a video
	private SurfaceHolder mSurfaceHolder = null;
	private MediaPlayer mMediaPlayer = null;
	private Context mContext;
	private int mVideoWidth;
	private int mVideoHeight;
	private int mSurfaceWidth;
	private int mSurfaceHeight;
	private MediaController mMediaController;
	// private CancelActivity mCancelActivity;
	private OnCompletionListener mOnCompletionListener;
	private MediaPlayer.OnPreparedListener mOnPreparedListener;
	private VideoViewStateChangeListener mStateChangeListener;
	private int mCurrentBufferPercentage;
	private OnErrorListener mOnErrorListener;
	private boolean fullScreen;
	private int mSeekWhenPrepared; // recording the seek position while
									// preparing
	private boolean mCanPause;
	private boolean mCanSeekBack;
	private boolean mCanSeekForward;

	protected boolean isBufferCount = true;// 添加字段，播放统计时需要

	/**
	 * 记录消耗前的时间点
	 * */
	protected int lastSeekWhenPrepared = 0;

	/**
	 * 强制等待，无法播放
	 * */
	private boolean enforcementWait = false;

	/**
	 * 强制暂停
	 * */
	private boolean enforcementPause = false;

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
		if (!fullScreen) {
			if (mVideoWidth > 0 && mVideoHeight > 0) {
				if (mVideoWidth * height > width * mVideoHeight) {

					height = width * mVideoHeight / mVideoWidth;
				} else if (mVideoWidth * height < width * mVideoHeight) {

					width = height * mVideoWidth / mVideoHeight;
				} else {

				}
			}
		}
		setMeasuredDimension(width, height);
	}

	private void invalateScreenSize() {
		fullScreen = !fullScreen;
		LayoutParams lp = (LayoutParams) this.getLayoutParams();
		this.setLayoutParams(lp);
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
	private void initVideoView() {
		mVideoWidth = 0;
		mVideoHeight = 0;
		getHolder().addCallback(mSHCallback);
		// if (!NativeInfos.ifNativePlayer()) {
		// getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// } else {
		// getHolder().setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
		// }
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();
		mCurrentState = STATE_IDLE;
		StateChange(mCurrentState);
		mTargetState = STATE_IDLE;
	}

	public void setVideoPath(String path) {
		setVideoURI(Uri.parse(path));
	}

	public void setVideoURI(Uri uri) {
//		if(P2P._getinstace().isP2PMode()){
//				uri=LetvP2PUtils.getP2PUrl(uri+"");//p2p
//		}
		Log.w(TAG, "URI : " + uri);
		mUri = uri;
		mSeekWhenPrepared = 0;
		openVideo();
		requestLayout();
		invalidate();
		// setVisibility(View.VISIBLE);
	}

	public void stopPlayback() {
		StateChange(STATE_STOPBACK);// 为统计加入
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
			mCurrentState = STATE_IDLE;
			StateChange(mCurrentState);
			mTargetState = STATE_IDLE;
			setVisibility(View.INVISIBLE);
		}
//		if(P2P._getinstace().isP2PMode()){
//		 LetvP2PUtils.onStopP2p();//p2p
//		}
	}

	private void openVideo() {
		if (mUri == null) {
			// not ready for playback just yet, will try again later
			return;
		}

		if (mSurfaceHolder == null) {
			if (!NativeInfos.ifNativePlayer()) {
				getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			} else {
				getHolder().setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
			}
			setVisibility(VISIBLE);
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
		release(false);
		try {
			if (NativeInfos.ifNativePlayer()) {
				mMediaPlayer = new NativePlayer();
//				Log.e("main", "--------------------NativePlayer-----");
			} else {
				mMediaPlayer = new MediaPlayer();
//				Log.e("main", "--------------------MediaPlayer-----");
			}
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
			StateChange(mCurrentState);
			attachMediaController();
		} catch (IOException ex) {
			Log.w(TAG, "Unable to open content: " + mUri, ex);
			mCurrentState = STATE_ERROR;
			StateChange(mCurrentState);
			mTargetState = STATE_ERROR;
			mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
			return;
		} catch (IllegalArgumentException ex) {
			Log.w(TAG, "Unable to open content: " + mUri, ex);
			mCurrentState = STATE_ERROR;
			StateChange(mCurrentState);
			mTargetState = STATE_ERROR;
			mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
			return;
		}
	}

	public void setMediaController(MediaController controller) {
		if (mMediaController != null) {
			mMediaController.hide();
		}
		mMediaController = controller;
		attachMediaController();
	}

	// public void setCancelActivity(CancelActivity mCancelActivity){

	// this.mCancelActivity = mCancelActivity;
	// }

	private void attachMediaController() {
		if (mMediaPlayer != null && mMediaController != null) {
			mMediaController.setMediaPlayer(this);
			View anchorView = this.getParent() instanceof View ? (View) this.getParent() : this;
			mMediaController.setAnchorView(anchorView);
			mMediaController.setEnabled(isInPlaybackState());
		}
	}

	MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
		public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
			mVideoWidth = mp.getVideoWidth();
			mVideoHeight = mp.getVideoHeight();
			if (mVideoWidth != 0 && mVideoHeight != 0) {
				getHolder().setFixedSize(mVideoWidth, mVideoHeight);
			}
		}
	};

	MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
		public void onPrepared(MediaPlayer mp) {
			mCurrentState = STATE_PREPARED;
			StateChange(mCurrentState);

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
			if (mMediaController != null) {
				mMediaController.setEnabled(true);
			}
			mVideoWidth = mp.getVideoWidth();
			mVideoHeight = mp.getVideoHeight();

			int seekToPosition = mSeekWhenPrepared; // mSeekWhenPrepared may be
													// changed after seekTo()
													// call
			if (seekToPosition != 0) {
				seekTo(seekToPosition);
			}
			if (mVideoWidth != 0 && mVideoHeight != 0) {
				// Log.i("@@@@", "video size: " + mVideoWidth +"/"+
				// mVideoHeight);
				getHolder().setFixedSize(mVideoWidth, mVideoHeight);
				if (mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
					// We didn't actually change the size (it was already at the
					// size
					// we need), so we won't get a "surface changed" callback,
					// so
					// start the video here instead of in the callback.
					if (mTargetState == STATE_PLAYING) {
						start();
						if (mMediaController != null) {
							mMediaController.show();
						}
					} else if (!isPlaying() && (seekToPosition != 0 || getCurrentPosition() > 0)) {
						if (mMediaController != null) {
							// Show the media controls when we're paused into a
							// video and make 'em stick.
							mMediaController.show(0);
						}
					}
				}
			} else {
				// We don't know the video size yet, but should start anyway.
				// The video size might be reported to us later.
				if (mTargetState == STATE_PLAYING) {
					start();
				}
			}
		}
	};

	private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
		public void onCompletion(MediaPlayer mp) {
			mCurrentState = STATE_PLAYBACK_COMPLETED;
			StateChange(mCurrentState);
			mTargetState = STATE_PLAYBACK_COMPLETED;
			mCurrentState = STATE_STOPBACK;
			StateChange(mCurrentState);// 未统计加入
			if (mMediaController != null) {
				mMediaController.hide();
			}
			if (mOnCompletionListener != null) {
				mOnCompletionListener.onCompletion(mMediaPlayer);
			}
		}
	};

	private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
		public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
			Log.d(TAG, "Error: " + framework_err + "," + impl_err);
			mCurrentState = STATE_ERROR;
			StateChange(mCurrentState);
			mTargetState = STATE_ERROR;
			if (mMediaController != null) {
				mMediaController.hide();
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
			// if (getWindowToken() != null) {
			//
			// // Resources r = mContext.getResources();
			// // int messageId;
			// //
			// // if (framework_err ==
			// // MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {
			// // messageId =
			// //
			// com.android.internal.R.string.VideoView_error_text_invalid_progressive_playback;
			// // } else {
			// // messageId =
			// // com.android.internal.R.string.VideoView_error_text_unknown;
			// // }
			// //
			// // new AlertDialog.Builder(mContext)
			// // .setTitle(com.android.internal.R.string.VideoView_error_title)
			// // .setMessage(messageId)
			// //
			// .setPositiveButton(com.android.internal.R.string.VideoView_error_button,
			// // new DialogInterface.OnClickListener() {
			// // public void onClick(DialogInterface dialog, int whichButton)
			// // {
			// // /* If we get here, there is no onError listener, so
			// // * at least inform them that the video is over.
			// // */
			// // if (mOnCompletionListener != null) {
			// // mOnCompletionListener.onCompletion(mMediaPlayer);
			// // }
			// // }
			// // })
			// // .setCancelable(false)
			// // .show();
			// }
			return true;
		}
	};

	private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
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
			mSurfaceWidth = w;
			mSurfaceHeight = h;
			boolean isValidState = (mTargetState == STATE_PLAYING);
			boolean hasValidSize = (mVideoWidth == w && mVideoHeight == h);
			if (mMediaPlayer != null && isValidState && hasValidSize) {
				if (mSeekWhenPrepared != 0) {
					seekTo(mSeekWhenPrepared);
				}
				start();
				if (mMediaController != null) {
					mMediaController.show();
				}
			}
		}

		public void surfaceCreated(SurfaceHolder holder) {
			// if (!NativeInfos.ifNativePlayer()) {
			// holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			// Log.e("xue", "surfaceCreated  --  系统");
			// } else {
			// holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
			// Log.e("xue", "surfaceCreated  --  软解");
			// }

			mSurfaceHolder = holder;
			openVideo();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			// after we return from this we can't use the surface any more
			mSurfaceHolder = null;
			if (mMediaController != null)
				mMediaController.hide();
			lastSeekWhenPrepared = getCurrentPosition();
			release(true);
		}
	};

	/*
	 * release the media player in any state
	 */
	private void release(boolean cleartargetstate) {
		if (mMediaPlayer != null) {
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
			mCurrentState = STATE_IDLE;
			StateChange(mCurrentState);
			if (cleartargetstate) {
				mTargetState = STATE_IDLE;
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (isInPlaybackState() && mMediaController != null) {
			toggleMediaControlsVisiblity();
		}
		// if(mCancelActivity != null){
		//
		// mCancelActivity.callCancel();
		// }
		return false;
	}

	@Override
	public boolean onTrackballEvent(MotionEvent ev) {
		if (isInPlaybackState() && mMediaController != null) {
			toggleMediaControlsVisiblity();
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// BasePlayActivity ac = null ;
		// if(mContext != null && mContext instanceof BasePlayActivity){
		// ac = (BasePlayActivity)mContext ;
		// }
		// boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK &&
		// keyCode != KeyEvent.KEYCODE_VOLUME_UP &&
		// keyCode != KeyEvent.KEYCODE_VOLUME_DOWN &&
		// keyCode != KeyEvent.KEYCODE_MENU &&
		// keyCode != KeyEvent.KEYCODE_CALL &&
		// keyCode != KeyEvent.KEYCODE_ENDCALL;
		// if (isInPlaybackState() && isKeyCodeSupported && mMediaController !=
		// null && ac != null && !ac.isLock()) {
		// if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK ||
		// keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
		// if (mMediaPlayer.isPlaying()) {
		// pause();
		// mMediaController.show();
		// } else {
		// start();
		// mMediaController.hide();
		// }
		// return true;
		// } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
		// && mMediaPlayer.isPlaying()) {
		// pause();
		// mMediaController.show();
		// } else {
		// toggleMediaControlsVisiblity();
		// }
		// }
		boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK
				&& keyCode != KeyEvent.KEYCODE_VOLUME_UP && keyCode != KeyEvent.KEYCODE_VOLUME_DOWN
				&& keyCode != KeyEvent.KEYCODE_MENU && keyCode != KeyEvent.KEYCODE_CALL
				&& keyCode != KeyEvent.KEYCODE_ENDCALL;
		if (isInPlaybackState() && isKeyCodeSupported && mMediaController != null) {
			if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
					|| keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
				if (mMediaPlayer.isPlaying()) {
					pause();
					mMediaController.show();
				} else {
					start();
					mMediaController.hide();
				}
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP && mMediaPlayer.isPlaying()) {
				pause();
				mMediaController.show();
			} else {
				toggleMediaControlsVisiblity();
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	public int getLastSeekWhenPrepared() {
		return lastSeekWhenPrepared;
	}

	private void toggleMediaControlsVisiblity() {
		if (mMediaController.isShowing()) {
			mMediaController.hide();
		} else {
			mMediaController.show();
		}
	}

	public void start() {
		if (!enforcementWait && !enforcementPause) {
			if (isInPlaybackState()) {
				// setVisibility(View.VISIBLE);
				mMediaPlayer.start();
				mCurrentState = STATE_PLAYING;
				StateChange(mCurrentState);
			}
		}
		mTargetState = STATE_PLAYING;
		StateChange(STATE_ENFORCEMENT);
	}

	public void pause() {
		if (isInPlaybackState()) {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.pause();
				mCurrentState = STATE_PAUSED;
				StateChange(mCurrentState);
				
				/*
				 * 播放统计用到
				 */
				// VideoPing.pauseCount++;
			}
		}
//		if(P2P._getinstace().isP2PMode()){
//			 LetvP2PUtils.onStopP2p();//p2p
//			}
		mTargetState = STATE_PAUSED;
	}

	public boolean toggleScreen() {
		invalateScreenSize();
		return fullScreen;
	}

	// cache duration as mDuration for faster access
	public int getDuration() {
		if (isInPlaybackState()) {
			if (mDuration > 0) {
				return mDuration;
			}
			mDuration = mMediaPlayer.getDuration();
			return mDuration;
		}
		mDuration = -1;
		return mDuration;
	}

	public int getCurrentPosition() {
		if (isInPlaybackState()) {
			return mMediaPlayer.getCurrentPosition();
		}
		return 0;
	}

	public void seekTo(int msec) {
		if (msec < 1000) {
			msec = 1000;
		}
		if (isInPlaybackState()) {
			mMediaPlayer.seekTo(msec);
			mSeekWhenPrepared = 0;
			lastSeekWhenPrepared = 0;
		} else {
			mSeekWhenPrepared = msec;
			lastSeekWhenPrepared = 0;
		}
	}

	/**
	 * 固定快进
	 * */
	public void forward() {
		seekTo(getCurrentPosition() + FORWARD_TIME);
	}

	/**
	 * 固定快退
	 * */
	public void rewind() {
		seekTo(getCurrentPosition() - REWIND_TIME);
	}

	public boolean isPlaying() {
		return isInPlaybackState() && mMediaPlayer.isPlaying();
	}

	public boolean isPaused() {
		return mCurrentState != STATE_PAUSED;
	}

	public int getBufferPercentage() {
		if (mMediaPlayer != null) {
			return mCurrentBufferPercentage;
		}
		return 0;
	}

	public boolean isInPlaybackState() {
		return (mMediaPlayer != null && mCurrentState != STATE_ERROR && mCurrentState != STATE_IDLE && mCurrentState != STATE_PREPARING);
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

	private void StateChange(int mCurrentState) {
		if (mStateChangeListener != null) {
			mStateChangeListener.onChange(mCurrentState);
		}
	}

	public VideoViewStateChangeListener getmStateChangeListener() {
		return mStateChangeListener;
	}

	public void setmStateChangeListener(VideoViewStateChangeListener mStateChangeListener) {
		this.mStateChangeListener = mStateChangeListener;
	}

	public interface VideoViewStateChangeListener {
		public void onChange(int mCurrentState);
	}

	public boolean isEnforcementWait() {
		return enforcementWait;
	}

	public void setEnforcementWait(boolean enforcementWait) {
		this.enforcementWait = enforcementWait;
	}

	public boolean isEnforcementPause() {
		return enforcementPause;
	}

	public void setEnforcementPause(boolean enforcementPause) {
		this.enforcementPause = enforcementPause;
	}
}
