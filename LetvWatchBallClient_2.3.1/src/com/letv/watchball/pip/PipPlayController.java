package com.letv.watchball.pip;

import android.content.Intent;
import android.os.Bundle;

import com.letv.watchball.view.PlayLoadLayout.PlayLoadLayoutCallBack;

public interface PipPlayController extends PlayLoadLayoutCallBack{
	
	public void onCreate(Bundle bundle);
	
	public void onResume();
	
	public void onPause();
	
	public void onDestroy();
	
	public void onFinish();
	
	public String getFrom();
	
	public void onStopTrackingTouch();
	
	public Bundle getPlayBundle();
	
	public void onActivityResult(int requestCode, int resultCode, Intent data);
	
	public VideoView getVideoView();
	
	public BaseMediaController getMediaController();
	
	public String getVideoTitle();
	
	public void updateVideoPosition();
	
	public boolean isLive();
	
	public boolean isLoadingShown();
	
	public void onVideoPause();
	
	public void onVideoStart();
	
//	public ShackVideoInfo getVideoInfo();
	
	public boolean isPlayingAd();
	
	public void handlerAdClick();
	
	public void next();
}
