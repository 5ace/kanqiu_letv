package com.media;

import java.io.IOException;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
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

public class NativePlayer extends MediaPlayer
{
    private static final int MEDIA_NOP = 0; // interface test message
    private static final int MEDIA_PREPARED = 1;
    private static final int MEDIA_PLAYBACK_COMPLETE = 2;
    private static final int MEDIA_BUFFERING_UPDATE = 3;
    private static final int MEDIA_SEEK_COMPLETE = 4;
    private static final int MEDIA_SET_VIDEO_SIZE = 5;
    private static final int MEDIA_ERROR = 100;
    private static final int MEDIA_INFO = 200;
    private static final int MEDIA_LOADING_PER = 400;
 
    private final static String 		TAG = "NativePlayer";

    private int 						mNativeContext;
    private int                         mNativeData = 0;

    private Surface 					mSurface;
    private AudioTrack 					mTrack;
    private SurfaceHolder  				mSurfaceHolder;
	private static Rect             	mRect = null;
	private EventHandler 				mEventHandler;
    private PowerManager.WakeLock 		mWakeLock = null;
    private boolean 					mScreenOnWhilePlaying;
    private boolean 					mStayAwake;
    private static Bitmap				mBitmap;
    private boolean						mLocalLogable = true;
    
    static{
    	try {
			NativeLib loadLib = new NativeLib();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public NativePlayer() {
        Looper looper;
        if ((looper = Looper.myLooper()) != null) {
            mEventHandler = new EventHandler(this, looper);
        } else if ((looper = Looper.getMainLooper()) != null) {
            mEventHandler = new EventHandler(this, looper);
        } else {
            mEventHandler = null;
        }
        native_init();
        native_setup(new WeakReference<NativePlayer>(this));
        setLocalLogable(mLocalLogable);
    }
    
    
    public static void initAudioTrack(Object mediaplayer_ref, int sampleRateInHz, int channelConfig) throws IOException
    {
    	NativePlayer mp = (NativePlayer)((WeakReference<?>)mediaplayer_ref).get();
        if (mp == null) {
            return;
        }       

        int bufferSizeInBytes = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, AudioFormat.ENCODING_PCM_16BIT);
        mp.mTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz, channelConfig, AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytes, AudioTrack.MODE_STREAM);
        mp.mTrack.play();
    }
    
    public static void writeAudioTrack(Object mediaplayer_ref, byte[] audioData, int size) throws IOException
    {
    	NativePlayer mp = (NativePlayer)((WeakReference<?>)mediaplayer_ref).get();
        if (mp == null) {
            return;
        } 
        mp.mTrack.write(audioData, 0, size);
    }
    
    public static void releaseAudioTrack(Object mediaplayer_ref) throws IOException
    {
    	NativePlayer mp = (NativePlayer)((WeakReference<?>)mediaplayer_ref).get();
        if (mp == null) {
            return;
        }   
        if(mp.mTrack!=null)
        {
            mp.mTrack.stop();
            mp.mTrack.release();
            mp.mTrack = null;
        }
    }

    private native void _setAudioTrack(AudioTrack track) throws IOException;
    
    public static void JavaDraw(Object mediaplayer_ref) throws IOException
    {
    	NativePlayer mp = (NativePlayer)((WeakReference<?>)mediaplayer_ref).get();
        if (mp == null) {
            return;
        }  
        
    	Canvas canvas = mp.mSurfaceHolder.lockCanvas(mRect);
    	
    	if (canvas == null){
    		return;
    	}
    	_nativeDraw(mBitmap);   
    	canvas.drawBitmap(mBitmap, 0, 0, null); 
    	mp.mSurfaceHolder.unlockCanvasAndPost(canvas);
    }
      
    private static native void _nativeDraw(Bitmap canvas);
    
    private static void postEventFromNative(Object mediaplayer_ref, int what, int arg1, int arg2, Object obj) {
    	
		NativePlayer mp = (NativePlayer)((WeakReference<?>)mediaplayer_ref).get();
		if (mp == null) {
		    return;
		}
		if (mp.mEventHandler != null) {
		    Message m = mp.mEventHandler.obtainMessage(what, arg1, arg2, obj);
		    mp.mEventHandler.sendMessage(m);
		}
		
    }
    
    public void setDisplay(SurfaceHolder sh) {
        mSurfaceHolder = sh;
        if (sh != null) {
            mSurface = sh.getSurface();
        } else {
            mSurface = null;
        }

        updateSurfaceScreenOn();
//        _setVideoSurface(mSurface);
    }

    public void start() throws IllegalStateException {
    	stayAwake(true);
        _start();
    }

    public void stop() throws IllegalStateException {
    	stayAwake(false);
        _stop();
    }

    public void pause() throws IllegalStateException {
    	stayAwake(false);
        _pause();
    }
    
    public void prepareAsync() throws IllegalStateException{
    	try {
			prepare();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
     
    public void setDataSource(Context context, Uri uri){
    	try {
			setDataSource(uri.toString());
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private native void setLocalLogable(boolean logable);
    private native void _setVideoSurface(Surface surface) throws IOException;
    public native void setDataSource(String path) throws IOException, IllegalArgumentException, IllegalStateException;
    public native void prepare() throws IOException, IllegalStateException;
    private native void _start() throws IllegalStateException;
    private native void _stop() throws IllegalStateException;
    private native void _pause() throws IllegalStateException;
    public native int getVideoWidth();
    public native int getVideoHeight();
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

    public void release() {
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
    	if (mSurfaceHolder != null){
    		Canvas tmpCanvas = mSurfaceHolder.lockCanvas(mRect);
    		if (tmpCanvas != null){
	    		tmpCanvas.drawColor(Color.BLACK);
	    		mSurfaceHolder.unlockCanvasAndPost(tmpCanvas);
    		}
    	}
    }


    public void reset() {
    	stayAwake(false);
        _reset();
        mEventHandler.removeCallbacksAndMessages(null);
    }

    public boolean suspend() {
        if (native_suspend_resume(true) < 0) {
            return false;
        }

        stayAwake(false);
        mEventHandler.removeCallbacksAndMessages(null);
        
        return true;
    }

    public boolean resume() {
        if (native_suspend_resume(false) < 0) {
            return false;
        }
        
        if (isPlaying()) {
            stayAwake(true);
        }
        
        return true;
    }
    
    public void setWakeMode(Context context, int mode) {
        boolean washeld = false;
        if (mWakeLock != null) {
            if (mWakeLock.isHeld()) {
                washeld = true;
                mWakeLock.release();
            }
            mWakeLock = null;
        }

        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(mode|PowerManager.ON_AFTER_RELEASE, MediaPlayer.class.getName());
        mWakeLock.setReferenceCounted(false);
        if (washeld) {
            mWakeLock.acquire();
        }
    }
    
    public void setScreenOnWhilePlaying(boolean screenOn) {
        if (mScreenOnWhilePlaying != screenOn) {
            mScreenOnWhilePlaying = screenOn;
            updateSurfaceScreenOn();
        }
    }

    private void stayAwake(boolean awake) {
        if (mWakeLock != null) {
            if (awake && !mWakeLock.isHeld()) {
                mWakeLock.acquire();
            } else if (!awake && mWakeLock.isHeld()) {
                mWakeLock.release();
            }
        }
        mStayAwake = awake;
        updateSurfaceScreenOn();
    }
    
    private void updateSurfaceScreenOn() {
        if (mSurfaceHolder != null) {
            mSurfaceHolder.setKeepScreenOn(mScreenOnWhilePlaying && mStayAwake);
        }
    }
    
	private OnVideoSizeChangedListener  mOnVideoSizeChangedListener;
	public void setOnVideoSizeChangedListener(
			OnVideoSizeChangedListener mSizeChangedListener) {
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
    
    public interface OnLoadingPerListener
    {
        void onLoadingPer(MediaPlayer mp, int per);
    }   
    private OnLoadingPerListener mOnLoadingPerListener;
    public void setOnLoadingPerListener(OnLoadingPerListener listener)
    {
    	mOnLoadingPerListener = listener;
    }

    
    private class EventHandler extends Handler
    {
        private NativePlayer mMediaPlayer;

        public EventHandler(NativePlayer nativePlayer, Looper looper) {
            super(looper);
            mMediaPlayer = nativePlayer;
        }

        @Override
        public void handleMessage(Message msg) {    
            
            if (mMediaPlayer.mNativeContext == 0) {
                return;
            }
            switch(msg.what) {
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
                if (mOnBufferingUpdateListener != null){
                    mOnBufferingUpdateListener.onBufferingUpdate(mMediaPlayer, msg.arg1);
                }
                return;

            case MEDIA_SEEK_COMPLETE:
            	if (mOnSeekCompleteListener != null)
                    mOnSeekCompleteListener.onSeekComplete(mMediaPlayer);
            	return;

            case MEDIA_SET_VIDEO_SIZE:
              if (mOnVideoSizeChangedListener != null){    
              	  if (null == mRect){
              		  mRect = new Rect();
              		  mRect.set(0, 0, msg.arg1, msg.arg2);
              	  }
              	  mBitmap = Bitmap.createBitmap(msg.arg1, msg.arg2, Bitmap.Config.RGB_565);
                  mOnVideoSizeChangedListener.onVideoSizeChanged(mMediaPlayer, msg.arg1, msg.arg2);
              }
              return;

            case MEDIA_ERROR:
                boolean error_was_handled = false;
                if (mOnErrorListener != null) {
                    error_was_handled = mOnErrorListener.onError(mMediaPlayer, msg.arg1, msg.arg2);
                }
                if (mOnCompletionListener != null && !error_was_handled) {
                    mOnCompletionListener.onCompletion(mMediaPlayer);
                }
                stayAwake(false);
                return;

            case MEDIA_INFO:
                if (mOnInfoListener != null) {
                    mOnInfoListener.onInfo(mMediaPlayer, msg.arg1, msg.arg2);
                }
                return;
                
            case MEDIA_LOADING_PER:
            	if (mOnLoadingPerListener != null) {
            		mOnLoadingPerListener.onLoadingPer(mMediaPlayer, msg.arg1);
            	}

            case MEDIA_NOP:
                break;

            default:
                return;
            }
        }
    }
}
