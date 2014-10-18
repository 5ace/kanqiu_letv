package com.letv.watchball.ui.impl;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.letv.ads.AdsManager;
import com.letv.watchball.LetvApplication;
import com.letv.watchball.R;
import com.letv.watchball.ui.LetvBaseFragment;
import com.letv.watchball.ui.PlayController;
import com.letv.watchball.utils.UIs;
import com.media.NativeInfos;
import com.media.VideoView;
import com.media.VideoView.VideoViewStateChangeListener;

public class BasePlayFragment extends LetvBaseFragment implements android.media.MediaPlayer.OnErrorListener,
		android.media.MediaPlayer.OnPreparedListener, android.media.MediaPlayer.OnCompletionListener {

      private final int ON_VIDEO_START = 1;// 正片开始播放
      private final int ON_VIDEO_PAUSE = 2;//正片暂停
      private final int ON_VIDEO_RESUME = 3;//正片继续
      private final int ON_VIDEO_COMPLATE = 4;//当视频播放完成
      private final int ON_VIDEO_ERROR = 5;//当视频播放错误


	private ViewGroup root ;
	
	private VideoView mVideoView;

	private Uri playUri;

      private PlayController mPlayController;

	/**
	 * videoview状态变化的监听
	 * */
	private VideoViewStateChangeListener stateChangeListener ;
	
	private boolean notResumeSeek = false;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root =  (ViewGroup) UIs.inflate(inflater, R.layout.fragment_play, null, false);
		mVideoView = (VideoView) root.findViewById(R.id.video_view);

		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(UIs.isLandscape(getActivity())){
			((BasePlayActivity)getActivity()).mPlayController.add_comment_main.setVisibility(View.GONE);
			UIs.zoomViewFull(root);
		} else {
			((BasePlayActivity)getActivity()).mPlayController.add_comment_main.setVisibility(View.VISIBLE);
			UIs.zoomView(320, 180, root);
		}

            mPlayController = getPlayController();

            AdsManager.getInstance().setVideoCallBack(mVideoCallBack);
	}

      private PlayController getPlayController() {
            return ((BasePlayActivity) getActivity()).mPlayController;
      }

      private AdsManager.VideoCallBack mVideoCallBack = new AdsManager.VideoCallBack() {
            @Override
            public void resumeVideo() {
                  mVideoView.start();
            }
            @Override
            public void pauseVideo() {
                  mVideoView.pause();
            }
            @Override
            public Rect getPlayerRect() {
                  Rect rect = new Rect();
                  root.getGlobalVisibleRect(rect);
//				LogInfo.log("ads", "BasePlayFragment   rect.top =" + rect.top + "  rect.bottom= " + rect.bottom + " rect.left="
//						+ rect.left + " rect.right=" + rect.right);
                  return rect;
            }
      };


      @Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if(UIs.isLandscape(getActivity())){
			((BasePlayActivity)getActivity()).mPlayController.add_comment_main.setVisibility(View.GONE);
			UIs.zoomViewFull(root);
		} else {
			((BasePlayActivity)getActivity()).mPlayController.add_comment_main.setVisibility(View.VISIBLE);
			UIs.zoomView(320, 180, root);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if(mVideoView.getLastSeekWhenPrepared() != 0 && !notResumeSeek){
			mVideoView.seekTo(mVideoView.getLastSeekWhenPrepared());
		} 
		mVideoView.start();
	}

	@Override
	public void onPause() {
		super.onPause();
		mVideoView.pause();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		playUri = null ;
		mVideoView.stopPlayback();
		root.removeAllViews();
		root = null ;
		mVideoView = null ;
	}
	
	/**
	 * 播放,兼容在线与本地播放器
	 * */
	public void playLocal(String uriString , int msec) {
		
		NativeInfos.mIsLive = false;
		NativeInfos.mOffLinePlay = true;
		NativeInfos.doWithNativePlayUrl(uriString);
		
		playUri = Uri.parse(uriString);
		mVideoView.setVideoURI(playUri);
		mVideoView.setOnErrorListener(this);
		mVideoView.setOnCompletionListener(this);
		mVideoView.setOnPreparedListener(this);
		mVideoView.requestFocus();
		if(msec > 0){
			mVideoView.seekTo(msec);
		}
		mVideoView.start();
	}
	
	/**
	 * 播放在线
	 * */
	public void playNet(String uriString, boolean isLive , boolean isDolby , int msec) {
		
		NativeInfos.mOffLinePlay = false;
		NativeInfos.mIsLive = isLive;
		initNativeInfos();
		if(isDolby){
			NativeInfos.mOffLinePlay = true ;
			NativeInfos.mIfNative3gpOrMp4 = true ;
		}
		
		playUri = Uri.parse(uriString);
		mVideoView.setVideoURI(playUri);
		mVideoView.setOnErrorListener(this);
		mVideoView.setOnCompletionListener(this);
		mVideoView.setOnPreparedListener(this);
		mVideoView.requestFocus();
		if(msec > 0){
			mVideoView.seekTo(msec);
		}
		mVideoView.start();
	}

	/**
	 * 播放
	 * */
	public void start(){
		mVideoView.start() ;
	}
	
	/**
	 * 暂停
	 * */
	public void pause(){
		mVideoView.pause() ;
	}
	
	/**
	 * 停止
	 * */
	public void stopPlayback(){
		mVideoView.stopPlayback() ;
	}
	
	/**
	 * 跳到
	 * */
	public void seekTo(int msec){
		mVideoView.seekTo(msec);
	}
	
	/**
	 * 快进
	 * */
	public void forward(){
		mVideoView.forward();
	}
	
	/**
	 * 快退
	 * */
	public void rewind(){
		mVideoView.rewind();
	}
	
	/**
	 * 是否在播放中
	 * */
	public boolean isPlaying(){
		return mVideoView.isPlaying() ;
	}
	
	/**
	 * 是否暂停
	 * */
	public boolean isPaused(){
		return mVideoView.isPaused() ;
	}
	
	/**
	 * 得到当前时间点
	 * */
	public int getCurrentPosition(){
		return mVideoView.getCurrentPosition() ;
	}
	
	
	/**
	 * 得到当前缓冲时间点
	 * */
	public int getBufferPercentage(){
		return mVideoView.getBufferPercentage() ;
	}

	/**
	 * 得到总时长
	 * */
	public int getDuration(){
		return mVideoView.getDuration() ;
	}
	
	/**
	 * 是否进入播放状态（包括准备完成，播放，暂停，完成等）
	 * */
	public boolean isInPlaybackState(){
		return mVideoView.isInPlaybackState() ;
	}
	
	public void setEnforcementWait(boolean enforcementWait){
		if(mVideoView != null){
			mVideoView.setEnforcementWait(enforcementWait);
		}
	}
	
	public void setEnforcementPause(boolean enforcementPause){
		if(mVideoView != null){
			mVideoView.setEnforcementPause(enforcementPause);
		}
	}
	
	public boolean isEnforcementPause(){
		if(mVideoView != null){
			return mVideoView.isEnforcementPause();
		}
		
		return false ;
	}
	
	/**
	 * 初始化播放器类型 本地播放器还是系统播放器
	 * */
	public void initNativeInfos() {
		String vf = LetvApplication.getInstance().getVideoFormat();
		if("ios".equals(vf)){
			NativeInfos.mOffLinePlay = false;
			NativeInfos.mIsLive = false;
		} else if("no".equals(vf)){
			NativeInfos.mOffLinePlay = true;
			NativeInfos.mIfNative3gpOrMp4 = true;
			NativeInfos.mIsLive = false;
		}
	}


	/**
	 * 播放完成回调
	 * */
	@Override
	public void onCompletion(MediaPlayer mp) {
	}

	/**
	 * 准备完成回调
	 * */
	@Override
	public void onPrepared(MediaPlayer mp) {
		
	}

	/**
	 * 播放错误回调
	 * */
	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		mPlayController.onError(what,extra);
		return false;
	}
	
	public VideoViewStateChangeListener getStateChangeListener() {
		return stateChangeListener;
	}

	/**
	 * 播放器状态变换的监听
	 * */
	public void setStateChangeListener(VideoViewStateChangeListener stateChangeListener) {
		this.stateChangeListener = stateChangeListener;
		mVideoView.setmStateChangeListener(this.stateChangeListener);
	}

	public boolean isNotResumeSeek() {
		return notResumeSeek;
	}

	public void setNotResumeSeek(boolean notResumeSeek) {
		this.notResumeSeek = notResumeSeek;
	}

      /**
       * 广告接口回调播放各种状态
       * @param whichStatus
       * @param isHand
       */
      private void callAdsPlayInterface(int whichStatus, boolean isHand) {
            try {
                  if (mPlayController != null && mPlayController.mIVideoStatusInformer != null) {
                        switch (whichStatus) {
                              case ON_VIDEO_START:
                                    mPlayController.mIVideoStatusInformer.OnVideoStart();// 正片开始播放 public void OnVideoStart();
                                    break;
                              case ON_VIDEO_PAUSE:
                                    mPlayController.mIVideoStatusInformer.OnVideoPause(isHand);//正片暂停   是否由用户主动触发
                                    break;
                              case ON_VIDEO_RESUME:
                                    mPlayController.mIVideoStatusInformer.OnVideoResume(isHand);//正片继续  是否由用户主动触发
                                    break;
                              case ON_VIDEO_COMPLATE:
                                    mPlayController.mIVideoStatusInformer.OnVideoComplate();//当视频播放完成 public void OnVideoComplate();
                                    break;
                              case ON_VIDEO_ERROR:
                                    mPlayController.mIVideoStatusInformer.onVideoError();//当视频播放错误 public void onVideoError();
                                    break;
                        }
                  }
            } catch (Exception e) {
                  e.printStackTrace();
            }
      }

}