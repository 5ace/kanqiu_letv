package com.media.ffmpeg;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
//import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.MediaController;
import android.widget.RelativeLayout.LayoutParams;

public class TSVideoView extends GLSurfaceView implements MediaController.MediaPlayerControl
{
	private static final String TAG = "TSVideoView";
	
	public static final int STATE_ERROR = -1;
	public static final int STATE_IDLE = 0;
	public static final int STATE_PREPARING = 1;
	public static final int STATE_PREPARED = 2;
	public static final int STATE_PLAYING = 3;
	public static final int STATE_PAUSED = 4;
	public static final int STATE_PLAYBACK_COMPLETED = 5;
	public static final int STATE_STOPBACK = 6;
	public static final int STATE_ENFORCEMENT = 7;
	
	private int mCurrentState = STATE_IDLE;
	private int mTargetState = STATE_IDLE;
	
	private final int FORWARD_TIME = 15000 ;
	private final int REWIND_TIME = 15000 ;

	// All the stuff we need for playing and showing a video
	private SurfaceHolder mSurfaceHolder = null;
	private FFMpegPlayer mMediaPlayer = null;
	private Context mContext;
	private int mVideoWidth;
	private int mVideoHeight;
	private int mSurfaceWidth;
	private int mSurfaceHeight;
	private OnCompletionListener mOnCompletionListener;
	private OnPreparedListener mOnPreparedListener;
	private int mCurrentBufferPercentage;
	private OnErrorListener mOnErrorListener;
	private OnBufferingUpdateListener mOnBufferingUpdateListener;
	private OnSeekCompleteListener mOnSeekCompleteListener;
	
	private TSVideoViewStateChangeListener mStateChangeListener;

	private boolean fullScreen;
	private int mSeekWhenPrepared; // recording the seek position while
	// preparing
	private boolean mCanPause;
	private boolean mCanSeekBack;
	private boolean mCanSeekForward;

	private boolean firstPlay = true;// 当前VideoView所代表的播放器首次进行播放
	private String mLastUrl;
	private String mVersion;
	private Uri mUri;
	private int mDuration;
	
	/**
	 * 记录消毁前的时间点
	 * */
	protected int lastSeekWhenPrepared = 0 ;
	
	/**
	 * 强制等待，无法播放
	 * */
	private boolean enforcementWait = false ;
	
	/**
	 * 强制暂停
	 * */
	private boolean enforcementPause = false ;

	public TSVideoView(Context context)
	{
		super(context);
		this.mContext = context;
		initVideoView();
	}
	

	public TSVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		initVideoView();
	}

	private void initVideoView()
	{
		mVideoWidth = 0;
		mVideoHeight = 0;
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		setRenderer(new MyRenderer());
		this.onPause();
		getHolder().addCallback(mSHCallback);
		getHolder().setFormat(PixelFormat.TRANSLUCENT);
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();
		mCurrentState = STATE_IDLE;
		StateChange(mCurrentState);
		mTargetState = STATE_IDLE;
	}
	

	public void setVideoPath(String videoPath) {
		setVideoURI(Uri.parse(videoPath));
	}
	
	public void setVideoURI(Uri uri) {
		mUri = uri;
//		Log.i("chenyg", "TSVideoView: uri=" + mUri.toString());
		mSeekWhenPrepared = 0;
		openVideo();
		requestLayout();
		invalidate();
	}

	/**
	 * mediaControl 底层方法必须实现
	 */
	@Override
	public boolean canPause()
	{
		return mCanPause;
	}

	@Override
	public boolean canSeekBackward()
	{
		return mCanSeekBack;
	}

	@Override
	public boolean canSeekForward()
	{
		return mCanSeekForward;
	}

	@Override
	public int getBufferPercentage()
	{
		if (mMediaPlayer != null)
		{
			return mCurrentBufferPercentage;
		}

		return 0;
	}

	@Override
	public int getCurrentPosition()
	{
		if (isInPlaybackState())
		{
			return mMediaPlayer.getCurrentPosition();
		}

		return 0;
	}

	@Override
	public int getDuration()
	{
		if (isInPlaybackState())
		{
			if (mDuration > 0)
			{
				return mDuration;
			}
			mDuration = mMediaPlayer.getDuration();
			return mDuration;
		}

		mDuration = -1;
		return mDuration;
	}

	public View getVideoView()
	{

		return this;
	}

	public String getSkipLastURL()
	{
		return mLastUrl;
	}

	public String getVersion()
	{
		return mVersion;
	}

	public int getViewWidth()
	{
		return getLayoutParams().width;
	}

	public int getViewHeight()
	{
		return getLayoutParams().height;
	}

	@Override
	public void start()
	{
		if(!enforcementWait && !enforcementPause){
			if (isInPlaybackState()) {
//				setVisibility(View.VISIBLE);
				mMediaPlayer.start();
				mCurrentState = STATE_PLAYING;
				StateChange(mCurrentState);
			}
		}
		mTargetState = STATE_PLAYING;
		StateChange(STATE_ENFORCEMENT);
	}

	@Override
	public void pause()
	{
		if (isInPlaybackState())
		{
			if (mMediaPlayer.isPlaying())
			{
				mMediaPlayer.pause();
				mCurrentState = STATE_PAUSED;
				StateChange(mCurrentState);
			}
		}

		mTargetState = STATE_PAUSED;
	}

	@Override
	public void seekTo(int mesc)
	{
		if(mesc < 1000){
			mesc = 1000 ;
		}
		if (isInPlaybackState())
		{
			mMediaPlayer.seekTo(mesc);
			mSeekWhenPrepared = 0;
			lastSeekWhenPrepared = 0;
		}
		else
		{
			mSeekWhenPrepared = mesc;
			lastSeekWhenPrepared = 0;
		}

	}
	
	/**
	 * 固定快进
	 * */
	public void forward() {
		seekTo(getCurrentPosition() + FORWARD_TIME) ;
	}

	/**
	 * 固定快退
	 * */
	public void rewind() {
		seekTo(getCurrentPosition() - REWIND_TIME) ;
	}

	public void stopPlayback(boolean isRemoveCallBack)
	{
		StateChange(STATE_STOPBACK);//为统计加入
		if (mMediaPlayer != null)
		{
			mMediaPlayer.stop();
			if (isRemoveCallBack)
			{
				getHolder().removeCallback(mSHCallback);
			}
			mMediaPlayer.release();
			mMediaPlayer = null;
			mCurrentState = STATE_IDLE;
			StateChange(mCurrentState);
			mTargetState = STATE_IDLE;
			setVisibility(INVISIBLE);
		}
//		Log.i(TAG, "stopPlayback()");
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

	SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback()
	{
		public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
		{
//			Log.i(TAG, "surfaceChanged()");
			mSurfaceWidth = w;
			mSurfaceHeight = h;
			boolean isValidState = (mTargetState == STATE_PLAYING);
			boolean hasValidSize = (mVideoWidth == w && mVideoHeight == h);
			if (mMediaPlayer != null && isValidState && hasValidSize)
			{
				if (mSeekWhenPrepared != 0)
				{
					seekTo(mSeekWhenPrepared);
				}
				start();

			}
		}

		public void surfaceCreated(SurfaceHolder holder)
		{
//			Log.i(TAG, "surfaceCreated");
			if (mSurfaceHolder == null)
			{
				mSurfaceHolder = holder;
				openVideo();
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder)
		{
//			Log.i(TAG, "surfaceDestroyed()");
			mSurfaceHolder = null;
			lastSeekWhenPrepared = getCurrentPosition() ;
			release(true);
		}
	};

	public boolean isInPlaybackState()
	{
		return (mMediaPlayer != null && mCurrentState != STATE_ERROR && mCurrentState != STATE_IDLE && mCurrentState != STATE_PREPARING);
	}

	private void openVideo()
	{
//		Log.i(TAG, "openVideo");
		if (mUri == null || mSurfaceHolder == null)
		{
			setVisibility(VISIBLE);
			return;
		}
		release(false);
		try
		{
			mMediaPlayer = new FFMpegPlayer();
			mMediaPlayer.setHardwareDecode(FFMpegPlayer.SOFTWARE_DECODE);//sunyuanzeng
			onResume();
			mMediaPlayer.setOnPreparedListener(mPreparedListener);
			mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
			mDuration = -1;
			mMediaPlayer.setOnCompletionListener(mCompletionListener);
			mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
			mMediaPlayer.setOnErrorListener(mErrorListener);
			mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
			mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
			mMediaPlayer.setRenderControler(mGLRenderControler);
			mCurrentBufferPercentage = 0;
			
			
			mMediaPlayer.setDataSource(mContext, mUri);
			//mMediaPlayer.setDataSource(mContext, Uri.parse("http://cache.video.qiyi.com/vd/334053/cfce4ed87b3a422ab4f400dbe36a9b01/"));
			mMediaPlayer.setDisplay(mSurfaceHolder);
//			mMediaPlayer.setDecoderSurface(mSurfaceHolder.getSurface());
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setScreenOnWhilePlaying(true);
			mMediaPlayer.prepareAsync();
			mCurrentState = STATE_PREPARING;
		}
		catch (IllegalArgumentException ex)
		{
//			Log.w(TAG, "Unable to open content: " + mUri, ex);
			mCurrentState = STATE_ERROR;
			StateChange(mCurrentState);
			mTargetState = STATE_ERROR;
			mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
			return;
		}
		catch (IOException e) {
//			Log.w(TAG, "Unable to open content: " + mUri, e);
			mCurrentState = STATE_ERROR;
			StateChange(mCurrentState);
			mTargetState = STATE_ERROR;
			mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
		}
	}

	// 设置播放器控件的大小
	private void setVideoViewScale(int width, int height)
	{
		LayoutParams lp = (LayoutParams) this.getLayoutParams();
		lp.height = height;
		lp.width = width;
		setLayoutParams(lp);
	}

	public boolean toggleScreen()
	{
		invalateScreenSize();
		return fullScreen;
	}

	private void release(boolean cleartargetstate)
	{
//		Log.i(TAG, "release()");
		if (mMediaPlayer != null)
		{
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
			mCurrentState = STATE_IDLE;
			StateChange(mCurrentState);
			if (cleartargetstate)
			{
				mTargetState = STATE_IDLE;
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK && keyCode != KeyEvent.KEYCODE_VOLUME_UP && keyCode != KeyEvent.KEYCODE_VOLUME_DOWN && keyCode != KeyEvent.KEYCODE_MENU
				&& keyCode != KeyEvent.KEYCODE_CALL && keyCode != KeyEvent.KEYCODE_ENDCALL;
		if (isInPlaybackState() && isKeyCodeSupported)
		{
			if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
			{
				if (mMediaPlayer.isPlaying())
				{
					pause();
				}
				else
				{
					start();

				}
				return true;
			}
			else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP && mMediaPlayer.isPlaying())
			{
				pause();
			}
			else
			{
				toggleMediaControlsVisiblity();
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	private void toggleMediaControlsVisiblity()
	{

	}

	@Override
	public boolean isPlaying()
	{
		return isInPlaybackState() && mMediaPlayer.isPlaying();
	}
	
	public boolean isPaused() {
		return  mCurrentState != STATE_PAUSED;
	}

	MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener()
	{
		public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
			mVideoWidth = mp.getVideoWidth();
			mVideoHeight = mp.getVideoHeight();
			if (mVideoWidth != 0 && mVideoHeight != 0) {
				getHolder().setFixedSize(mVideoWidth, mVideoHeight);
			}
		}
	};

	MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener()
	{
		public void onPrepared(MediaPlayer mp)
		{
			mCurrentState = STATE_PREPARED;
			StateChange(mCurrentState);

			mCanPause = mCanSeekBack = mCanSeekForward = true;

			if (mOnPreparedListener != null)
			{
				mOnPreparedListener.onPrepared(mMediaPlayer);
			}
			mLastUrl = ((FFMpegPlayer) mp).getLastUrl();
			mVersion = ((FFMpegPlayer) mp).getVersion();

			int seekToPosition = mSeekWhenPrepared; // mSeekWhenPrepared may be
			if (seekToPosition != 0)
			{
				seekTo(seekToPosition);
			}
			mVideoWidth = mp.getVideoWidth();
			mVideoHeight = mp.getVideoHeight();
			if (mVideoWidth != 0 && mVideoHeight != 0)
			{
				if (mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight)
				{
					if (mTargetState == STATE_PLAYING)
					{
						start();
					}
				}
				else
				{
					getHolder().setFixedSize(mVideoWidth, mVideoHeight);
				}

			}
			else
			{

				if (mTargetState == STATE_PLAYING)
				{
					start();
				}
			}

		}
	};

	private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener()
	{
		public void onCompletion(MediaPlayer mp)
		{
			mCurrentState = STATE_PLAYBACK_COMPLETED;
			StateChange(mCurrentState);
			mTargetState = STATE_PLAYBACK_COMPLETED;
			mCurrentState = STATE_STOPBACK;
			StateChange(mCurrentState);//未统计加入
			if (mOnCompletionListener != null)
			{
				mOnCompletionListener.onCompletion(mMediaPlayer);
			}
			/**
			 * 播放完成，停止并释放资源
			 */
			TSVideoView.this.pause();
			TSVideoView.this.release(true);
		}
	};

	private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener()
	{
		public boolean onError(MediaPlayer mp, int framework_err, int impl_err)
		{
//			Log.d(TAG, "Error: " + framework_err + "," + impl_err);
			mCurrentState = STATE_ERROR;
			StateChange(mCurrentState);
			mTargetState = STATE_ERROR;
			/* If an error handler has been supplied, use it and finish. */
			if (mOnErrorListener != null)
			{
				if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err))
				{
					return true;
				}
			}

			if (getWindowToken() != null)
			{

			}
			return true;
		}
	};

	private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener()
	{

		public void onBufferingUpdate(MediaPlayer mp, int percent)
		{
			mCurrentBufferPercentage = percent;
			if (mOnBufferingUpdateListener != null)
				mOnBufferingUpdateListener.onBufferingUpdate(mp, percent);
		}
	};
	private MediaPlayer.OnSeekCompleteListener mSeekCompleteListener = new MediaPlayer.OnSeekCompleteListener()
	{
		public void onSeekComplete(MediaPlayer mp)
		{
			if (mOnSeekCompleteListener != null)
			{
				mOnSeekCompleteListener.onSeekComplete(mMediaPlayer);
			}
			//	resume();
		}
	};

	public void setOnBufferingUpdateListener(OnBufferingUpdateListener l)
	{
		mOnBufferingUpdateListener = l;
	}

	public void setOnPreparedListener(OnPreparedListener mOnPreparedListener)
	{
		this.mOnPreparedListener = mOnPreparedListener;
	}

	public void setOnCompletionListener(OnCompletionListener mOnCompletionListener)
	{
		this.mOnCompletionListener = mOnCompletionListener;
	}

	public void setOnErrorListener(OnErrorListener mOnErrorListener)
	{
		this.mOnErrorListener = mOnErrorListener;
	}

	private FFMpegPlayer.GLRenderControler mGLRenderControler = new FFMpegPlayer.GLRenderControler()
	{

		public void setGLStartRenderMode()
		{

			setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

		}

		public void setGLStopRenderMode()
		{

			setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

		}

	};

	public void setOnSeekCompleteListener(OnSeekCompleteListener mSeekCompleteListener)
	{
		this.mOnSeekCompleteListener = mSeekCompleteListener;
	}

	class MyRenderer implements GLSurfaceView.Renderer
	{

		public void onSurfaceCreated(GL10 gl, EGLConfig c)
		{
//			Log.i(TAG, "myRenderer");

		}

		public void onSurfaceChanged(GL10 gl, int w, int h)
		{
			mSurfaceHeight = h;
			mSurfaceWidth = w;
 
			if (mMediaPlayer != null)
			{

				if ((lastW != w) || (lastH != h))
				{
					gl.glViewport(0, 0, w, h);
					mMediaPlayer.native_gl_resize(w, h);
					lastW = w;
					lastH = h;
//					gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
//					gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
//					gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
				}

			}
		}

		public void onDrawFrame(GL10 gl)
		{
			try
			{
				if (mMediaPlayer != null && mMediaPlayer.isPlaying())
				{
					mMediaPlayer.native_gl_render();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		public long time = 0;
		public int lastW = 0;
		public int lastH = 0;
		public short framerate = 0;
		public long fpsTime = 0;
		public long frameTime = 0;
		public float avgFPS = 0;
	}
	
	public int getLastSeekWhenPrepared() {
		return lastSeekWhenPrepared;
	}

	public int getAudioSessionId() {
		return 0;
	}
	
	private void StateChange(int mCurrentState){
		if(mStateChangeListener != null){
			mStateChangeListener.onChange(mCurrentState) ;
		}
	}
	
	public TSVideoViewStateChangeListener getStateChangeListener() {
		return mStateChangeListener;
	}

	public void setStateChangeListener(TSVideoViewStateChangeListener listener) {
		this.mStateChangeListener = listener;
	}

	public interface TSVideoViewStateChangeListener{
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
