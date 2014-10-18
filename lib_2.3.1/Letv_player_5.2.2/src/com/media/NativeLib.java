package com.media;

import android.util.Log;

public class NativeLib {
	
	private static final String TAG = "NativeLib";
	
	private static boolean sLoaded = false;
	
	public NativeLib() throws Exception {
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
	    	if (NativeInfos.ifSupportNeon()){
	    		System.loadLibrary("letvplayer_neon");
	    	} else{
	    		System.loadLibrary("letvplayer_vfp");
	    	}
    	}catch (UnsatisfiedLinkError e) {
    		Log.d(TAG, "Couldn't load lib: " + e.getMessage());
			err = true;
    	}
    	
    	if(!err) {
    		sLoaded = true;
    	}

    	return sLoaded;
    }

}
