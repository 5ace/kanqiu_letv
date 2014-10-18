package com.media.ffmpeg;

import com.media.NativeInfos;

import android.util.Log;

public class FFMpeg {
  
	private static final String TAG = "FFMpeg";

	private static boolean sLoaded = false;
	
	public static boolean mIsTsPlayer;

    public FFMpeg() throws Exception {
    	if(!loadLibs()) {
    		throw new Exception("Couldn't load native libs!!");
    	}
    }
    
    private static boolean loadLibs() {
    	if(sLoaded) {
    		return true;
    	}
    	boolean err = false;
    	
    	try{
//			System.loadLibrary("ffmpeg_qiyi_x86");
//			System.loadLibrary("ffmpeg_jni_qiyi_x86");
	    	if (NativeInfos.ifSupportNeon()){
	    		    
	    		    System.loadLibrary("lenthevcdec");
	    			System.loadLibrary("ffmpeg_neon");
	    			System.loadLibrary("ffmpeg_jni_neon");    
//	    			Log.i("chenyg","ffmpeg_jni_neon");

	    	} else{	    		
	    			System.loadLibrary("lenthevcdec");
	    			System.loadLibrary("ffmpeg_vfp");
	    			System.loadLibrary("ffmpeg_jni_vfp");	    		
//	    			Log.i("chenyg","ffmpeg_jni_vfp");
	    	}
    	}catch (UnsatisfiedLinkError e) {
//    		Log.d("FFMpeg", "Couldn't load lib: " + e.getMessage());
			err = true;
    	}
    	
    	if(!err) {
    		sLoaded = true;
    	}
    	return sLoaded;
    }
}
