package com.media.ffmpeg;

import java.io.IOException;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.view.Surface;
import android.view.SurfaceHolder;

public class FFMpegPlayer extends MediaPlayer
{
	private static final int MEDIA_NOP = 0; // interface test message
	private static final int MEDIA_PREPARED = 1;
	private static final int MEDIA_PLAYBACK_COMPLETE = 2;
	private static final int MEDIA_BUFFERING_UPDATE = 3;
	private static final int MEDIA_SEEK_COMPLETE = 4;
	private static final int MEDIA_SET_VIDEO_SIZE = 5;
	private static final int MEDIA_ERROR = 100;
	private static final int MEDIA_INFO = 200;

	public static final int MEDIA_ERROR_UNKNOWN = 1;
	public static final int MEDIA_ERROR_SERVER_DIED = 100;
	public static final int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200;
	public static final int MEDIA_INFO_UNKNOWN = 1;
	public static final int MEDIA_INFO_VIDEO_TRACK_LAGGING = 700;
	public static final int MEDIA_INFO_BAD_INTERLEAVING = 800;
	public static final int MEDIA_INFO_NOT_SEEKABLE = 801;
	public static final int MEDIA_INFO_METADATA_UPDATE = 802;
	public static final int MEDIA_INFO_FRAMERATE_VIDEO = 900;
	public static final int MEDIA_INFO_FRAMERATE_AUDIO = 901;
	
	public static final int HARDWARE_DECODE = 1;
	public static final int SOFTWARE_DECODE = 0;

	private final static String TAG = "FFMpegPlayer";

	private int mNativeContext;
	private int mNativeData = 0;

	private Surface mSurface;
	private AudioTrack mTrack;
	private SurfaceHolder mSurfaceHolder;
	private static Rect mRect = null;
	private EventHandler mEventHandler;
	private PowerManager.WakeLock mWakeLock = null;
	private boolean mScreenOnWhilePlaying;
	private boolean mStayAwake;
    private static GLRenderControler    mGlRenderControler;
    //sunyuanzeng
    private MediaDecoder mVideoDecoder;
    private Context mContext;
    //end
	static
	{
		try
		{
			FFMpeg loadLib = new FFMpeg();
			native_init();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public FFMpegPlayer()
	{
		Looper looper;
		if ((looper = Looper.myLooper()) != null)
		{
			mEventHandler = new EventHandler(this, looper);
		}
		else if ((looper = Looper.getMainLooper()) != null)
		{
			mEventHandler = new EventHandler(this, looper);
		}
		else
		{
			mEventHandler = null;
		}
//		native_init();
		native_setup(new WeakReference<FFMpegPlayer>(this));
	}
	
	public FFMpegPlayer( Context context )
	{
		Looper looper;
		if ((looper = Looper.myLooper()) != null)
		{
			mEventHandler = new EventHandler(this, looper);
		}
		else if ((looper = Looper.getMainLooper()) != null)
		{
			mEventHandler = new EventHandler(this, looper);
		}
		else
		{
			mEventHandler = null;
		}
//		native_init();
		native_setup(new WeakReference<FFMpegPlayer>(this));
		//sunyuanzeng
		mContext = context;
		//end
	}

	private static void postEventFromNative(Object mediaplayer_ref, int what, int arg1, int arg2, Object obj)
	{

		FFMpegPlayer mp = (FFMpegPlayer) ((WeakReference<?>) mediaplayer_ref).get();
		if (mp == null)
		{
			return;
		}
		if (mp.mEventHandler != null)
		{
			Message m = mp.mEventHandler.obtainMessage(what, arg1, arg2, obj);
			mp.mEventHandler.sendMessage(m);
		}

	}

	public void setDisplay(SurfaceHolder sh)
	{
		mSurfaceHolder = sh;
		if (sh != null)
		{
			mSurface = sh.getSurface();
		}
		else
		{
			mSurface = null;
		}

		updateSurfaceScreenOn();
		//	        _setVideoSurface(mSurface);
	}

	public void start() throws IllegalStateException
	{
		stayAwake(true);
		_start();
	}

	public void stop() throws IllegalStateException
	{
		stayAwake(false);
		_stop();
	}

	public void pause() throws IllegalStateException
	{
		stayAwake(false);
		_pause();
	}

	public void prepareAsync() throws IllegalStateException
	{
		try
		{
			prepare();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void setDataSource(Context context, Uri uri)
	{
		try
		{
			setDataSource(uri.toString());
			//setDataSource("http://cache.video.qiyi.com/mm/f0a6f7d6dfe445598498191fa0ac6879/484/cfe19c08d4b3592064624d6ec93d836e.m3u8?msessionid=NzdjNTcwNjJlN2M2OWI4ZjA0N2M5MTM0MTc3YWEwYzE=");

		}
		catch (IllegalArgumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalStateException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private native void _setVideoSurface(Surface surface) throws IOException;

	public native void setDataSource(String path) throws IOException, IllegalArgumentException, IllegalStateException;

	public native void prepare() throws IOException, IllegalStateException;

	private native void _start() throws IllegalStateException;

	private native void _stop() throws IllegalStateException;

	private native void _pause() throws IllegalStateException;

	public native int getVideoWidth();

	public native int getVideoHeight();

	public native String getLastUrl();

	public native String getVersion();

	public native boolean isPlaying();

	public native void seekTo(int msec) throws IllegalStateException;

	public native int getCurrentPosition();

	public native int getDuration();

	private native void _release();

	private native void _reset();

	private native int native_suspend_resume(boolean isSuspend);

	public native void setAudioStreamType(int streamtype);

	private static native final void native_init() throws RuntimeException;

	private native final void native_setup(Object mediaplayer_this);

	private native final void native_finalize();

	public native void native_gl_resize(int w, int h);

	public native void native_gl_render();

	public void release()
	{
//		Log.i(TAG, "release()");
		stayAwake(false);
		updateSurfaceScreenOn();
		mOnPreparedListener = null;
		mOnBufferingUpdateListener = null;
		mOnCompletionListener = null;
		mOnSeekCompleteListener = null;
		mOnErrorListener = null;
		mOnInfoListener = null;
		mOnVideoSizeChangedListener = null;
		_release();
		/*
		        if (mSurfaceHolder != null){ 
		    		Canvas tmpCanvas = mSurfaceHolder.lockCanvas(mRect); 
		    			if (tmpCanvas != null){ 
		    				//tmpCanvas.drawColor(Color.BLACK);
		    				Log.d(TAG,"drawColor");
		    				mSurfaceHolder.unlockCanvasAndPost(tmpCanvas);
		    			}
		    		}
		    		*/
	}

	public void reset()
	{
		stayAwake(false);
		_reset();
		mEventHandler.removeCallbacksAndMessages(null);
	}

	public boolean suspend()
	{
//		Log.i(TAG, "suspend()");
		if (native_suspend_resume(true) < 0)
		{
			return false;
		}

		stayAwake(false);
		mEventHandler.removeCallbacksAndMessages(null);

		return true;
	}

	public boolean resume()
	{
		if (native_suspend_resume(false) < 0)
		{
			return false;
		}

		if (isPlaying())
		{
			stayAwake(true);
		}

		return true;
	}

	public void setWakeMode(Context context, int mode)
	{
		boolean washeld = false;
		if (mWakeLock != null)
		{
			if (mWakeLock.isHeld())
			{
				washeld = true;
				mWakeLock.release();
			}
			mWakeLock = null;
		}

		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(mode | PowerManager.ON_AFTER_RELEASE, MediaPlayer.class.getName());
		mWakeLock.setReferenceCounted(false);
		if (washeld)
		{
			mWakeLock.acquire();
		}
	}

	public void setScreenOnWhilePlaying(boolean screenOn)
	{
		if (mScreenOnWhilePlaying != screenOn)
		{
			mScreenOnWhilePlaying = screenOn;
			updateSurfaceScreenOn();
		}
	}

	private void stayAwake(boolean awake)
	{
		if (mWakeLock != null)
		{
			if (awake && !mWakeLock.isHeld())
			{
				mWakeLock.acquire();
			}
			else if (!awake && mWakeLock.isHeld())
			{
				mWakeLock.release();
			}
		}
		mStayAwake = awake;
		updateSurfaceScreenOn();
	}

	private void updateSurfaceScreenOn()
	{
		if (mSurfaceHolder != null)
		{
			mSurfaceHolder.setKeepScreenOn(mScreenOnWhilePlaying && mStayAwake);
		}
	}

	private OnVideoSizeChangedListener mOnVideoSizeChangedListener;

	public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener mSizeChangedListener)
	{
		mOnVideoSizeChangedListener = mSizeChangedListener;
	}

	private OnSeekCompleteListener mOnSeekCompleteListener;

	public void setOnSeekCompleteListener(OnSeekCompleteListener listener)
	{
		mOnSeekCompleteListener = listener;
	}

	private OnPreparedListener mOnPreparedListener;

	public void setOnPreparedListener(OnPreparedListener listener)
	{
		mOnPreparedListener = listener;
	}

	private OnBufferingUpdateListener mOnBufferingUpdateListener;

	public void setOnBufferingUpdateListener(OnBufferingUpdateListener listener)
	{
		mOnBufferingUpdateListener = listener;
	}

	private OnCompletionListener mOnCompletionListener;

	public void setOnCompletionListener(OnCompletionListener listener)
	{
		mOnCompletionListener = listener;
	}

	private OnErrorListener mOnErrorListener;

	public void setOnErrorListener(OnErrorListener listener)
	{
		mOnErrorListener = listener;
	}

	private OnInfoListener mOnInfoListener;

	public void setOnInfoListener(OnInfoListener listener)
	{
		mOnInfoListener = listener;
	}

	private class EventHandler extends Handler
	{
		private FFMpegPlayer mMediaPlayer;

		public EventHandler(FFMpegPlayer ffmpegPlayer, Looper looper)
		{
			super(looper);
			mMediaPlayer = ffmpegPlayer;
		}

		@Override
		public void handleMessage(Message msg)
		{

			if (mMediaPlayer.mNativeContext == 0)
			{
				return;
			}
			switch (msg.what)
			{
				case MEDIA_PREPARED:
					if (mOnPreparedListener != null)
						mOnPreparedListener.onPrepared(mMediaPlayer);
					return;

				case MEDIA_PLAYBACK_COMPLETE:
					if (mOnCompletionListener != null)
						mOnCompletionListener.onCompletion(mMediaPlayer);
					stayAwake(false);
					return;

				case MEDIA_BUFFERING_UPDATE:
					if (mOnBufferingUpdateListener != null)
					{
						mOnBufferingUpdateListener.onBufferingUpdate(mMediaPlayer, msg.arg1);
					}
					return;

				case MEDIA_SEEK_COMPLETE:
					if (mOnSeekCompleteListener != null)
						mOnSeekCompleteListener.onSeekComplete(mMediaPlayer);
					return;

				case MEDIA_SET_VIDEO_SIZE:
					if (mOnVideoSizeChangedListener != null)
					{
						//by wmw,2012-02-27
						if (null == mRect)
						{
							mRect = new Rect();
							mRect.set(0, 0, msg.arg1, msg.arg2);
						}

						mOnVideoSizeChangedListener.onVideoSizeChanged(mMediaPlayer, msg.arg1, msg.arg2);
					}
					return;

				case MEDIA_ERROR:
					boolean error_was_handled = false;
					if (mOnErrorListener != null)
					{
						error_was_handled = mOnErrorListener.onError(mMediaPlayer, msg.arg1, msg.arg2);
					}
					if (mOnCompletionListener != null && !error_was_handled)
					{
						mOnCompletionListener.onCompletion(mMediaPlayer);
					}
					stayAwake(false);
					return;

				case MEDIA_INFO:
					if (mOnInfoListener != null)
					{
						mOnInfoListener.onInfo(mMediaPlayer, msg.arg1, msg.arg2);
					}
					return;

				case MEDIA_NOP:
					break;

				default:
					return;
			}
		}
	}

	public static void initAudioTrack(Object mediaplayer_ref, int sampleRateInHz, int channelConfig) throws IOException
	{
		FFMpegPlayer mp = (FFMpegPlayer) ((WeakReference<?>) mediaplayer_ref).get();
		if (mp == null)
		{
			return;
		}

		int bufferSizeInBytes = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, AudioFormat.ENCODING_PCM_16BIT);
		mp.mTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz, channelConfig, AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytes, AudioTrack.MODE_STREAM);
		mp.mTrack.play();
	}

	public static void writeAudioTrack(Object mediaplayer_ref, byte[] audioData, int size) throws IOException
	{
		FFMpegPlayer mp = (FFMpegPlayer) ((WeakReference<?>) mediaplayer_ref).get();
		if (mp == null)
		{
			return;
		}
		mp.mTrack.write(audioData, 0, size);
	}

	public static void releaseAudioTrack(Object mediaplayer_ref) throws IOException
	{
		FFMpegPlayer mp = (FFMpegPlayer) ((WeakReference<?>) mediaplayer_ref).get();
		if (mp == null)
		{
			return;
		}
		if (mp.mTrack != null)
		{
			mp.mTrack.stop();
			mp.mTrack.release();
			mp.mTrack = null;
		}
	}

	private native void _setAudioTrack(AudioTrack track) throws IOException;

	public static void JavaDraw(Object mediaplayer_ref) throws IOException
	{
		FFMpegPlayer mp = (FFMpegPlayer) ((WeakReference<?>) mediaplayer_ref).get();
		if (mp == null)
		{
			return;
		}

		Canvas canvas = mp.mSurfaceHolder.lockCanvas(mRect);

		//added by wmw,2012-04-09
		if (canvas == null)
		{
			return;
		}
		_nativeDraw(canvas);
		mp.mSurfaceHolder.unlockCanvasAndPost(canvas);
	}

	private static native void _nativeDraw(Canvas canvas);

    public interface GLRenderControler{

		 void setGLStartRenderMode();

		 void setGLStopRenderMode();
	 }

	public void setRenderControler(GLRenderControler controler) {
		mGlRenderControler = controler;
	}

	public static void stopRenderMode() {
		if (mGlRenderControler != null)
			mGlRenderControler.setGLStopRenderMode();
	}

	public static void startRenderMode() {
		if (mGlRenderControler != null)
			mGlRenderControler.setGLStartRenderMode();
	}
	
	//sunyuanzeng
	public static void initVideoDecoder( Object mediaplayer_ref, int width, int height ) throws IOException
	{
		
	}
	
	public static void stopVideoDecoder( Object mediaplayer_ref ) throws IOException
	{
		FFMpegPlayer mp = (FFMpegPlayer) ((WeakReference<?>) mediaplayer_ref).get();
		if (mp == null )
		{
			return;
		}
		if(mp.mVideoDecoder != null)
		{
//			Log.d("chenyg", "stopVideoDecoder");
			mp.mVideoDecoder.stopCodec();
			mp.mVideoDecoder = null;
		}
	}
	
	public static int fillInputBuffer( Object mediaplayer_ref, byte[] data, long pts, int flush ) throws IOException
	{
		FFMpegPlayer mp = (FFMpegPlayer) ((WeakReference<?>) mediaplayer_ref).get();
		if (mp == null || mp.mVideoDecoder == null)
		{
			return -1;
		}

		return mp.mVideoDecoder.fillInputBuffer(data, pts, flush);
	}
	
	public static int flushCodec( Object mediaplayer_ref ) throws IOException
	{
//		Log.d("chenyg", "flushCodec");
		FFMpegPlayer mp = (FFMpegPlayer) ((WeakReference<?>) mediaplayer_ref).get();
		if (mp == null)
		{
			return -1;
		}
		
		if( mp.mVideoDecoder != null )
		{
			mp.mVideoDecoder.flushCodec();
		}
		
		return 0;
	}
	
	public void setDecoderSurface( Surface surface )
	{
		mSurface = surface;
	}
	
	public native int _native_sync( long pts ) throws IOException;
	
	public native int setHardwareDecode( int hwDecode ) throws IOException;
	//end
}
