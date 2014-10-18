package com.letv.ads;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.VideoView;
import com.letv.adlib.managers.status.ad.AdStatusManager;
import com.letv.adlib.managers.status.ad.IPlayerStatusDelegate;
import com.letv.adlib.managers.status.video.IVideoStatusInformer;
import com.letv.adlib.model.ad.common.AdInfo;
import com.letv.adlib.model.ad.common.CommonAdItem;
import com.letv.adlib.model.ad.types.AdClickShowType;
import com.letv.adlib.model.ad.types.CuePointType;
import com.letv.adlib.model.ad.types.SimpleAdMediaType;
import com.letv.ads.http.AdsHttpApi;
import com.letv.ads.http.LetvSimpleAsyncTask;
import com.letv.ads.util.AdsUtils;
import com.letv.ads.util.LogInfo;
import com.letv.cache.LetvCacheMannager;
import com.letv.watchball.ads.R;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

@SuppressLint("NewApi")
public class ADPlayFragment extends Fragment {

	/**
	 * 无广告
	 * */
	private static final int NONE = 0;

	/**
	 * 前帖视频广告
	 * */
	private static final int FRONT_MP4 = 1;

	/**
	 * 前帖图片广告
	 * */
	private static final int FRONT_JPG = 2;

	/**
	 * 涨停广告
	 * */
	private static final int PAUSE = 3;

	/**
	 * handler 刷新时间
	 * */
	private static final int HANDLER_TIME = 0x100;

	/**
	 * 根布局
	 * */
	private View root;

	/**
	 * 视频广告布局
	 * */
	private View mVideoViewLayout;

	/**
	 * videoview
	 * */
	private VideoView mVideoViewFirst;
	private VideoView mVideoViewSecond;

	/**
	 * 视频广告点击区
	 * */
	private View mVideoViewClick;

	/**
	 * 图片前帖
	 * */
	private ImageView mImageView;

	/**
	 * 静音按钮
	 * */
	private ImageView muteView;

	/**
	 * 时间
	 * */
	private TextView mTimeTextView;

	/**
	 * loading圈
	 * */
	private View mLoading;

	/**
	 * 暂停广告布局
	 * */
	private View pauseLayout;

	/**
	 * 暂停图片
	 * */
	private ImageView pauseImage;

	/**
	 * 暂停广告关闭按钮
	 * */
	private View pauseDel;

	/**
	 * 前帖广告回调
	 * */
	private PlayAdListener adListener;

	private String mTimeText = "15";
	/**
	 * 当前的广告无聊
	 * */
	// private CommonAdItem mPlayAdInfo;

	/**
	 * 资源加载开始时间
	 */
	protected long loadingStart = 0;

	// /**
	// * 资源加载完成时间
	// */
	// protected long loadingEnd = 0;
	/**
	 * 当前时间
	 * */
	private int curTime = 0;

	/**
	 * 总时间
	 * */
	private int totalTime = 0;

	private int totalTimeTmp = 0;

	/**
	 * 广告请求时长
	 */
	private long adsRequestTime = 0;
	/**
	 * 广告加载时长
	 */
	private long adsLoadingTime = 0;

	/**
	 * 当前广告类型
	 * */
	private int curAdType = NONE;

	/**
	 * 当前执行的线程
	 * */
	private LetvSimpleAsyncTask curTask;

	/**
	 * activity是否已经销毁
	 */
	private boolean isDestroy = false;
	/**
	 * fragment是否已经添加至activity
	 */
	private boolean isReady = false;

	/**
	 * 是否广告显示成功
	 * */
	private boolean isShow = false;

	/**
	 * 声音管理者
	 * */
	private AudioManager audioManager;

	/**
	 * 初始音量
	 * */
	private int old = 0;

	/**
	 * 会员去广告按钮
	 * */
	private View vipView;

	/**
	 * 会员去广告按钮回调
	 * */
	private VipViewCallBack viewCallBack;

	/**
	 * 开始时间
	 * */
	private long startTime;
	/**
	 * 半屏2G/3G 暂停显示默认图片
	 */
	private View wifiTopViewHalf;

	/**
	 * 前贴广告是否请求成功
	 */
	private boolean isHaveFrontAds = true;

	/**
	 * 当前是否是第一个播放器在工作
	 */
	private boolean isFirstVideoViewWorking = true;

	/**
	 * 广告信息
	 */
	private ArrayList<CommonAdItem> adsList = null;
	/**
	 * 新增IVideoStatusInformer ，用于客户端将播放状态通知给广告
	 */
	private IVideoStatusInformer informer = null;
	/**
	 * 前贴广告位置
	 */
	private int adsItemIndex = 0;
	/**
	 * 已经播放完视频时间
	 */
	private int adsPlayedTime = 0;
	private boolean isFirstPlayAds = true;

	private CommonAdItem firstCommonAdItem;
	private CommonAdItem secondCommonAdItem;

	private AdStatusManager mAdStatusManagerFirst;
	private AdStatusManager mAdStatusManagerSecond;

	private RequestDemandFrontAd mRequestDemandFrontAd;
	/**
	 * 刷新进度handler
	 * */
	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			if (!isReady || isDestroy) {
				return false;
			}
			switch (msg.what) {
			case HANDLER_TIME:
				if (curAdType == FRONT_MP4) {
					int time = getCurrentWorkingVideo(isFirstVideoViewWorking).getCurrentPosition() + adsPlayedTime
							* 1000;
					int pos = 0;
					if (curTime == time) {
						if (!isPauseAd) {
							mLoading.setVisibility(View.VISIBLE);
						}
					} else {
						if (time <= 0 && curTime > 0) {
							if (!isPauseAd) {
								mLoading.setVisibility(View.VISIBLE);
							}
						} else {
							mLoading.setVisibility(View.GONE);
							curTime = time;
						}
						if (isFirstPlayAds) {
							isFirstPlayAds = false;
							adsLoadingTime = System.currentTimeMillis() - adsLoadingTime;
						}
					}
					pos = totalTime * 1000 - curTime;
					if (pos < 0) {
						pos = 0;
					}
					if (vipView != null && vipView.getVisibility() != View.VISIBLE) {
						vipView.setVisibility(View.GONE);
					}
                              float fTime = pos /1000;
                              int iTime = Math.round(fTime);
					mTimeTextView.setText(String.valueOf(iTime));
					mTimeText = mTimeTextView.getText().toString();
                              Log.d("adtime", "pos=" + pos + "   time=" + mTimeText);
                              handler.sendEmptyMessageDelayed(HANDLER_TIME, 1000);
				} else if (curAdType == FRONT_JPG) {
					int pos = totalTime - curTime;
					if (pos <= 0) {
						pos = 0;
					}
					mTimeTextView.setText(String.valueOf(pos));
					mTimeText = mTimeTextView.getText().toString();
					if (pos == 0) {
						/**
						 * 完成统计
						 * */
						if (adStatusManager != null) {
							adStatusManager.onAdPlayComplete();
						}
						FrontAdFinish();
						return false;
					}
					curTime++;
					handler.sendEmptyMessageDelayed(HANDLER_TIME, 1000);
				}

				break;
			}

			return false;
		}
	});

	/**
	 * 当前的统计工具
	 * */
	private AdStatusManager adStatusManager;

	private boolean isPauseAd = false;
      private boolean viewCreated;
      private boolean hasAds;

      @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
		if (audioManager != null) {
			if (audioManager.getMode() == AudioManager.MODE_INVALID) {
				audioManager.setMode(AudioManager.MODE_NORMAL);
			}
		}
		firstIsVisable=true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.ad_play, null, false);
		findView();
            viewCreated = true;
		return root;
	}

	/**
	 * 初始化控件
	 * */
	private void findView() {
		mVideoViewLayout = root.findViewById(R.id.ad_video_layout);
		mVideoViewFirst = (VideoView) root.findViewById(R.id.ad_video_first);
		mVideoViewSecond = (VideoView) root.findViewById(R.id.ad_video_second);
		mVideoViewClick = root.findViewById(R.id.ad_video_click);
		mImageView = (ImageView) root.findViewById(R.id.ad_image);
		mTimeTextView = (TextView) root.findViewById(R.id.ad_time);
		mLoading = root.findViewById(R.id.ad_loading);
		pauseLayout = root.findViewById(R.id.pause_layout);
		pauseImage = (ImageView) root.findViewById(R.id.pause_img);
		pauseDel = root.findViewById(R.id.pause_del);
		muteView = (ImageView) root.findViewById(R.id.ad_mute);
		vipView = root.findViewById(R.id.ad_vip);
		wifiTopViewHalf = root.findViewById(R.id.wifiTopViewHalfAd);
		videoViewInit();
		pauseDel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				closePauseAd();
			}
		});
		if (audioManager != null) {// 初始化声音管理器
			int cur = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			if (cur == 0) {
				muteView.setImageResource(R.drawable.mute);
			} else {
				muteView.setImageResource(R.drawable.not_muted);
			}
		}

		muteView.setOnClickListener(new OnClickListener() {// 静音按钮监听
			@Override
			public void onClick(View v) {
				int cur = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
				if (cur == 0) {
					if (old == 0) {// 无声音，且上一次的声音也为0，那么给一个初始值
						audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 5, 0);
						muteView.setImageResource(R.drawable.not_muted);
					} else {
						audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, old, 0);
						muteView.setImageResource(R.drawable.not_muted);
					}
				} else {
					old = cur;
					audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
					muteView.setImageResource(R.drawable.mute);
				}
			}
		});

		vipView.setOnClickListener(new OnClickListener() {// 会员去广告按钮，点击回调主工程

			@Override
			public void onClick(View v) {
				if (viewCallBack != null) {
					viewCallBack.onClick();
				}
			}
		});
		wifiTopViewHalf.setOnClickListener(new OnClickListener() {// 3G/2G
																	// 点击继续播放
					@Override
					public void onClick(View v) {
						if (viewCallBack != null) {
							viewCallBack.ads3G2GClick();
						}
						if (vipView != null) {
							vipView.setVisibility(View.GONE);
						}
					}
				});
	}

	/**
	 * 设置广告声音图标显示
	 * 
	 * @param cur
	 */
	public void setMuteViewStatus(int cur) {
		if (muteView != null && !isDestroy) {
			if (cur <= 0) {
				muteView.setImageResource(R.drawable.mute);
			} else {
				muteView.setImageResource(R.drawable.not_muted);
			}
		}
	}

	/**
	 * 播放器初始化
	 */
	private void videoViewInit() {
		mVideoViewSecond.setZOrderOnTop(true);
		mVideoViewSecond.setZOrderMediaOverlay(true);
		mVideoViewFirst.setZOrderOnTop(true);
		mVideoViewFirst.setZOrderMediaOverlay(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (isLandscape()) {
			zoomViewFull(root);
			zoomView(217, 123, pauseLayout);
		} else {
			zoomView(320, 180, root);
			zoomView(270, 152, pauseLayout);
		}

		isReady = true;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (isLandscape()) {
			zoomViewFull(root);
			zoomView(217, 123, pauseLayout);
		} else {
			zoomView(320, 180, root);
			zoomView(270, 152, pauseLayout);
		}
		if (isPauseAd) {
			mLoading.setVisibility(View.GONE);
			if (isLandscape()) {
				setwifiTopViewVisible(true);
//				if (vipView != null) {
//					vipView.setVisibility(View.GONE);
//				}
			} else {
				if (isPauseAd) {
					setwifiTopViewVisible(true);
				} else {
//					setwifiTopViewVisible(false);
				}
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (curAdType == FRONT_JPG) {
			startHandlerTime();
			/**
			 * 继续统计
			 * */
			if (adStatusManager != null) {
				adStatusManager.onAdResumed();
			}
		} else if (curAdType == FRONT_MP4) {
			if (!isPauseAd) {
				setwifiTopViewVisible(false);
				if (isFirstVideoViewWorking) {
					mVideoViewFirst.setVisibility(View.VISIBLE);
					mVideoViewSecond.setVisibility(View.GONE);
				} else {
					mVideoViewSecond.setVisibility(View.VISIBLE);
					mVideoViewFirst.setVisibility(View.GONE);
				}
				getCurrentWorkingVideo(isFirstVideoViewWorking).start();
				getCurrentWorkingVideo(isFirstVideoViewWorking).seekTo(
						curTime >= adsPlayedTime * 1000 ? (curTime - adsPlayedTime * 1000) : 0);
				startHandlerTime();
				mTimeTextView.setText(mTimeText);
				/**
				 * 继续统计
				 * */
				if (adStatusManager != null) {
					adStatusManager.onAdResumed();
				}
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (curAdType == FRONT_JPG || curAdType == FRONT_MP4) {
			/**
			 * 暂停统计
			 * */
			if (adStatusManager != null) {
				adStatusManager.onAdPaused();
				// //LogInfo.log("ads", "--------onPause--暂停统计----------");
			}
		}
		pause();
		mTimeText = mTimeTextView.getText().toString();
		// //LogInfo.log("ads", "----onPause----FRONT_MP4--curTime=" + curTime +
		// "   mTimeText =" + mTimeText);
	}

	@Override
	public void onDestroyView() {// 做一些必要的消耗
		super.onDestroyView();
		isDestroy = true;
		isReady = false;
		mVideoViewFirst.stopPlayback();
		mVideoViewSecond.stopPlayback();
		stopHandlerTime();
		if (curTask != null) {
			curTask.cancel(true);
		}
		curTask = null;
	}

	public VipViewCallBack getViewCallBack() {
		return viewCallBack;
	}

	public void setViewCallBack(VipViewCallBack viewCallBack) {
		this.viewCallBack = viewCallBack;
	}

	/**
	 * 播放,兼容在线与本地播放器
	 * */
	public boolean play(String uriString) {
		if (!TextUtils.isEmpty(uriString)) {
			isFirstVideoViewWorking = true;
			mVideoViewLayout.setVisibility(View.VISIBLE);
			mVideoViewFirst.setVisibility(View.VISIBLE);
			mTimeTextView.setVisibility(View.VISIBLE);
			mImageView.setVisibility(View.GONE);
			muteView.setVisibility(View.VISIBLE);
			mTimeTextView.setVisibility(View.VISIBLE);
			vipView.setVisibility(View.GONE);

			mVideoViewFirst.setVideoPath(uriString);
                  Log.d("adsvideo", "uriString=" + uriString);
                  mVideoViewFirst.setOnErrorListener(mOnErrorListenerFirst);
			mVideoViewFirst.setOnPreparedListener(mOnPreparedListenerFirst);
			mVideoViewFirst.setOnCompletionListener(onCompletionListenerFirst);
			if (!isPauseAd) {
				mLoading.setVisibility(View.VISIBLE);
				mVideoViewFirst.start();
			} else {
				mLoading.setVisibility(View.GONE);
				mVideoViewFirst.pause();
			}
			return true;
		} else {
			FrontAdFinish();
			return false;
		}
	}

	/**
	 * 跳过视频广告
	 * */
	public void stopFrontAd() {
		if (curAdType == FRONT_MP4) {
			mVideoViewFirst.stopPlayback();
			mVideoViewSecond.stopPlayback();
			stopHandlerTime();
			FrontAdFinish();
		} else if (curAdType == FRONT_JPG) {
			stopHandlerTime();
			FrontAdFinish();
		}
	}

	/**
	 * 播放
	 * */
	public void star() {
		getCurrentWorkingVideo(isFirstVideoViewWorking).start();
		startHandlerTime();
	}

	/**
	 * 暂停
	 * */
	public void pause() {
		// LogInfo.log("ads", "isFirstVideoViewWorking =" +
		// isFirstVideoViewWorking);
            VideoView currentWorkingVideo = getCurrentWorkingVideo(isFirstVideoViewWorking);
            if (null != currentWorkingVideo){
                  currentWorkingVideo.pause();
            }
            stopHandlerTime();
	}

	public void setADPause(boolean pauseAd) {
		if (pauseAd) {
			pause();
		} else {
			if (isPauseAd) {
				isPauseAd = false;
				setwifiTopViewVisible(false);
				mLoading.setVisibility(View.VISIBLE);
				getCurrentWorkingVideo(isFirstVideoViewWorking).start();
				getCurrentWorkingVideo(isFirstVideoViewWorking).seekTo(
						curTime >= adsPlayedTime * 1000 ? (curTime - adsPlayedTime * 1000) : 0);
				startHandlerTime();
				if (adStatusManager != null) {
					adStatusManager.onAdResumed();
				}
			}
		}
	}

	/**
	 * @return the isPauseAd
	 */
	public boolean isPauseAd() {
		return isPauseAd;
	}

	/**
	 * @param isPauseAd
	 *            the isPauseAd to set
	 */
	public void setPauseAd(boolean isPauseAd) {
		this.isPauseAd = isPauseAd;
		setMobileNetBg(isPauseAd);
	}

	/**
	 * @return the isHaveFrontAds
	 */
	public boolean isHaveFrontAds() {
		return isHaveFrontAds;
	}

	/**
	 * @param isHaveFrontAds
	 *            the isHaveFrontAds to set
	 */
	public void setHaveFrontAds(boolean isHaveFrontAds) {
		this.isHaveFrontAds = isHaveFrontAds;
	}

	/**
	 * 显示隐藏 2G/3G 暂停播放 默认图片
	 * 
	 * @param disp
	 */
	public void setMobileNetBg(boolean disp) {
            if (!viewCreated){
                 return;
            }
			if(disp){
                        if (null != mLoading)
				mLoading.setVisibility(View.GONE);
			} else {
                        if (null != mLoading)
				mLoading.setVisibility(View.VISIBLE);
			}
			setwifiTopViewVisible(disp);
		
	}
	public boolean firstIsVisable=true;
	private void setwifiTopViewVisible(boolean isVisable) {
		if (isVisable) {
			if (wifiTopViewHalf != null&&firstIsVisable) {
				firstIsVisable=false;
				wifiTopViewHalf.setVisibility(View.VISIBLE);
			}
			if (vipView != null) {
				vipView.setVisibility(View.GONE);
			}
		} else {
			if (wifiTopViewHalf != null) {
				wifiTopViewHalf.setVisibility(View.GONE);
			}
			if (vipView != null) {
				vipView.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 停止
	 * */
	public void stopPlayback() {
		if (mAdStatusManagerFirst != null) {
			mAdStatusManagerFirst.onAdStoped();
		}
		if (mAdStatusManagerSecond != null) {
			mAdStatusManagerSecond.onAdStoped();
		}

		FrontAdFinish(true);
		if (curTask != null) {
			curTask.cancel(true);
		}
	}

	/**
	 * 跳到
	 * */
	public void seekTo(int msec) {
		getCurrentWorkingVideo(isFirstVideoViewWorking).seekTo(msec);
	}

	/**
	 * 是否在播放中
	 * */
	public boolean isPlaying() {
		return getCurrentWorkingVideo(isFirstVideoViewWorking).isPlaying();
	}

	/**
	 * 得到当前时间点
	 * */
	public int getCurrentPosition() {
		return getCurrentWorkingVideo(isFirstVideoViewWorking).getCurrentPosition();
	}

	/**
	 * 得到当前缓冲时间点
	 * */
	public int getBufferPercentage() {
		return getCurrentWorkingVideo(isFirstVideoViewWorking).getBufferPercentage();
	}

	/**
	 * 得到广告listener
	 * */
	public PlayAdListener getAdListener() {
		return adListener;
	}

	/**
	 * 设置广告listener
	 * */
	public void setAdListener(PlayAdListener adListener) {
		this.adListener = adListener;
	}

	/**
	 * 前贴广告任务
	 * 
	 * @param context
	 * @param cid
	 * @param aid
	 * @param vid
	 * @param mmsid
	 * @param uuid
	 * @param uid
	 * @param vlen
	 * @param py
	 * @param ty
	 * @param isSupportM3U8
	 */
	private void startRequestFrontAdTask(Context context, int cid, long aid, long vid, String mmsid, String uuid,
			String uid, String vlen, String py, String ty, boolean isSupportM3U8, boolean isVipVideo, boolean disableAvd) {
		cancelRequestFrontAdTask();
		mRequestDemandFrontAd = new RequestDemandFrontAd(context, cid, aid, vid, mmsid, uuid, uid, vlen, py, ty,
				isSupportM3U8, isVipVideo, disableAvd);
            Log.d("ads","startRequestFrontAdTask");
            mRequestDemandFrontAd.start();
            Log.d("ads","startRequestFrontAdTask");
      }

	/**
	 * 取消前贴广告
	 */
	private void cancelRequestFrontAdTask() {
		if (mRequestDemandFrontAd != null) {
			mRequestDemandFrontAd.cancel(true);
			mRequestDemandFrontAd = null;
		}
	}

	/**
	 * 得到前帖广告
	 * */
	public void getDemandFrontAd(int cid, long aid, long vid, String mmsid, String uuid, String uid, String vlen,
			String py, String ty, boolean isSupportM3U8, boolean isVipVideo, boolean disableAvd) {
		startTime = System.currentTimeMillis();
            LogInfo.log("ads","getDemandFrontAd");
		if (isDestroy) {
			return;
		}
		loadingStart = System.currentTimeMillis();
		startRequestFrontAdTask(getActivity(), cid, aid, vid, mmsid, uuid, uid, vlen, py, ty, isSupportM3U8,
				isVipVideo, disableAvd);
	}

	/**
	 * 得到前帖广告
	 * */
	public void getDemandPauseAd(int cid, long aid, long vid, String mmsid, String uuid, String uid, String vlen,
			String py, String ty) {
            LogInfo.log("ads", "getDemandPauseAd");
		if (isDestroy) {
			return;
		}
		new RequestDemandPauseAd(getActivity(), cid, aid, vid, mmsid, uuid, uid, vlen, py, ty).start();
	}

	/**
	 * 得到直播前帖广告
	 * */
	public void getLiveFrontAd(Context context,String streamUrl, String uuid, String uid, String py, String ty) {
		startTime = System.currentTimeMillis();
            Log.d("ads","RequestLiveFrontAd");
		if (isDestroy) {
			return;
		}
		loadingStart = System.currentTimeMillis();
		new RequestLiveFrontAd(context, streamUrl, uuid, uid, py, ty).start();
	}

	/**
	 * 开始刷新进度条
	 * */
	private void startHandlerTime() {
		handler.removeMessages(HANDLER_TIME);
		if (!isPauseAd) {
			handler.sendEmptyMessage(HANDLER_TIME);
		}
	}

	/**
	 * 停止刷新进度条
	 * */
	private void stopHandlerTime() {
		handler.removeMessages(HANDLER_TIME);
	}

	/**
	 * 判断是否是横屏
	 * */
	public boolean isLandscape() {
            int t = 0;
            if (null != getActivity()){
                  t = getActivity().getResources().getConfiguration().orientation;
            }
		if (t == Configuration.ORIENTATION_LANDSCAPE) {
			return true;
		}

		return false;
	}

	/**
	 * 得到屏幕高度
	 * */
	public int getScreenHeight() {
		return ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
	}

	/**
	 * 得到屏幕宽度
	 * */
	public int getScreenWidth() {
		return ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
	}

	/**
	 * 将一倍尺寸缩放到当前屏幕大小的尺寸（宽）
	 * */
	public int zoomWidth(int w) {
		int sw = 0;
		sw = getScreenWidth();

		return (int) (w * sw / 320f + 0.5f);
	}

	/**
	 * 缩放控件
	 * */
	public void zoomView(int w, int h, View view) {
		if (view == null) {
			return;
		}

		LayoutParams params = view.getLayoutParams();

		if (params == null) {
			return;
		}

		params.width = zoomWidth(w);
		params.height = zoomWidth(h);
	}

	/**
	 * 缩放控件
	 * */
	public void zoomViewFull(View view) {
		if (view == null) {
			return;
		}

		LayoutParams params = view.getLayoutParams();

		if (params == null) {
			return;
		}

		params.width = getScreenWidth();
		params.height = getScreenHeight();
	}

	/**
	 * 前帖播放完成调用
	 */
	private void FrontAdFinish() {
		FrontAdFinish(false);
	}

      /**
       * 前帖播放完成调用，恢复布局到初始状态，销毁必要的对象
       * */
	private void FrontAdFinish(boolean isFinishByHand) {
		adsItemIndex = 0;
		isPauseAd = false;
		curAdType = NONE;
		totalTime = 0;
		curTime = 0;
		adsPlayedTime = 0;
		stopHandlerTime();

		if (mVideoViewFirst != null) {
			mVideoViewFirst.stopPlayback();
		}
		if (mVideoViewSecond != null) {
			mVideoViewSecond.stopPlayback();
		}
		mTimeTextView.setText(null);
		mTimeTextView.setVisibility(View.GONE);
		muteView.setVisibility(View.GONE);
		vipView.setVisibility(View.GONE);

		mVideoViewLayout.setVisibility(View.GONE);
		mVideoViewClick.setOnClickListener(null);
		mImageView.setOnClickListener(null);
		mVideoViewFirst.setVisibility(View.GONE);
		mVideoViewSecond.setVisibility(View.GONE);
		mLoading.setVisibility(View.GONE);
		mImageView.setImageDrawable(null);
		mImageView.setVisibility(View.GONE);

		if (adListener != null) {
			if (isReady && !isDestroy) {
				adListener.onFinish(isFinishByHand,hasAds);
				if (informer != null) {
					informer.OnVideoStart();
				}
			}
		}
		int cur = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		if (cur == 0) {
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, old, 0);
			muteView.setImageResource(R.drawable.not_muted);
		}
		// mPlayAdInfo = null ;
		adStatusManager = null;
		isFirstPlayAds = true;
		adsLoadingTime = 0;
	}

	/**
	 * 关闭暂停广告
	 * */
	public void closePauseAd() {
		if (curAdType == PAUSE) {
			curAdType = NONE;
			adStatusManager = null;
			// LogInfo.log("ads", "----------closePauseAd----------");
		}
		pauseLayout.setVisibility(View.GONE);
		pauseImage.setImageDrawable(null);
		pauseDel.setVisibility(View.GONE);
		pauseImage.setOnClickListener(null);
	}

	/**
	 * 请求完成，处理前帖广告
	 * */
	public void handler(final CommonAdItem adInfo) {
		if (!isReady || isDestroy) {
			isPauseAd = false;
			return;
		}
		if (adInfo == null) {
			FrontAdFinish();
			return;
		}
		mVideoViewLayout.setVisibility(View.VISIBLE);
		if (isFirstVideoViewWorking) {
			adStatusManager = mAdStatusManagerFirst;
		} else {
			adStatusManager = new AdStatusManager(adInfo);
		}
		// LogInfo.log("ads", "xxxxxxx---handler  adInfo---" + adInfo.index);
		if (SimpleAdMediaType.VIDEO == adInfo.mediaFileType) { // 显示视频广告
			if (curAdType == FRONT_MP4 || curAdType == FRONT_JPG) {
				star();
				return;
			}
			isShow = true;
			boolean su = play(adInfo.mediaFileUrl);
			if (su) {
				mVideoViewClick.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						clickGotoWeb(adInfo);
					}
				});
				// totalTime = adInfo.duration;
				exposureStatistics(adInfo);
			}
		} else if (SimpleAdMediaType.BITMAP == adInfo.mediaFileType) {// 显示图片广告
			if (!TextUtils.isEmpty(adInfo.mediaFileUrl)) {
				if (curAdType == FRONT_MP4 || curAdType == FRONT_JPG) {
					star();
					return;
				}
				LetvCacheMannager.getInstance().loadImage(adInfo.mediaFileUrl, mImageView, new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String imageUri, View view) {

					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						isShow = true;
						mImageView.setImageBitmap(loadedImage);
						mImageView.setVisibility(View.VISIBLE);
						mTimeTextView.setVisibility(View.VISIBLE);
						muteView.setVisibility(View.GONE);
						mTimeTextView.setVisibility(View.VISIBLE);
						vipView.setVisibility(View.GONE);
						mLoading.setVisibility(View.GONE);
						curAdType = FRONT_JPG;
						startHandlerTime();

						/**
						 * 加载完成统计
						 * */
						if (adStatusManager != null) {
							adStatusManager.onAdLoadComplete((int) (System.currentTimeMillis() - startTime));
						}
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {

					}
				});

				mImageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						clickGotoWeb(adInfo);
					}
				});
				totalTime = adInfo.duration;
				totalTimeTmp = totalTime;
				exposureStatistics(adInfo);
			}
		} else {
			FrontAdFinish();
		}
	}

	/**
	 * 请求完成，处理暂停广告
	 * */
	public void handlerPauseAd(final CommonAdItem adInfo) {
		if (adInfo == null) {
			return;
		}

		adStatusManager = new AdStatusManager(adInfo);

		if (SimpleAdMediaType.VIDEO == adInfo.mediaFileType) { // 显示视频广告

		} else if (SimpleAdMediaType.BITMAP == adInfo.mediaFileType) {// 显示图片广告
			if (!TextUtils.isEmpty(adInfo.mediaFileUrl)) {
				LetvCacheMannager.getInstance().loadImage(adInfo.mediaFileUrl, pauseImage, new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String imageUri, View view) {

					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						pauseImage.setImageBitmap(loadedImage);
						pauseLayout.setVisibility(View.VISIBLE);
						pauseImage.setVisibility(View.VISIBLE);
						pauseDel.setVisibility(View.VISIBLE);
						curAdType = PAUSE;
						exposureStatistics(adInfo);
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {

					}
				});
				pauseImage.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						clickGotoWeb(adInfo);
					}
				});
				// exposureStatistics(adInfo);
			}
		} else {

		}
	}

	/**
	 * 请求点播前帖片广告
	 * */
	private class RequestDemandFrontAd extends LetvSimpleAsyncTask<ArrayList<CommonAdItem>> {

		private Context mContext;

		private String cid;

		private String aid;

		private String vid;

		private String mmsid;

		private String uuid;

		private String uid;

		private String vlen;

		private String py;

		private String ty;

		private boolean isVipVideo;

		private boolean isSupportM3U8;

		private boolean disableAvd;

		public RequestDemandFrontAd(Context context, int cid, long aid, long vid, String mmsid, String uuid,
				String uid, String vlen, String py, String ty, boolean isSupportM3U8, boolean isVipVideo,
				boolean disableAvd) {
			super(context);
			adsRequestTime = System.currentTimeMillis();
			mContext = context;
			if (curTask != null)
				curTask.cancel(true);
			curTask = null;
			curTask = this;
			if (adsList != null) {
				adsList.clear();
			}
			firstCommonAdItem = null;
			secondCommonAdItem = null;
			mAdStatusManagerFirst = null;
			mAdStatusManagerSecond = null;
			totalTime = 0;
			adsItemIndex = 0;
			this.cid = String.valueOf(cid);
			this.aid = String.valueOf(aid);
			this.vid = String.valueOf(vid);
			this.mmsid = mmsid;
			this.uuid = uuid;
			this.uid = uid;
			this.vlen = vlen;
			this.py = py;
			this.ty = ty;
			this.disableAvd = disableAvd;
			this.isVipVideo = isVipVideo;
			this.isSupportM3U8 = isSupportM3U8;
			this.isSupportM3U8 = isSupportM3U8;
		}

		@Override
		public ArrayList<CommonAdItem> doInBackground() {
                  LogInfo.log("ads", "----------start---- cid=" + cid + " aid="+ aid + " vid="+ vid + " disableAvd=" + disableAvd);
                  if (isCancel()) {
                        return null;
                  }
			AdInfo frontAd = AdsHttpApi.getFrontAd(mContext, cid, aid, vid, mmsid, uuid, uid, vlen, py, ty,
					iPlayerStatusDelegate, true, isVipVideo, disableAvd);
			if (frontAd != null) {
				adsList = frontAd.adList;
				informer = frontAd.informer;
				LogInfo.log("ads", " ------------ end----- ");
				if (adsList != null && adsList.size() > 0) {
					CommonAdItem mCommonAdItem = null;
					for (int i = 0; i < adsList.size(); i++) {
						mCommonAdItem = adsList.get(i);
						totalTime += mCommonAdItem.duration;
						LogInfo.log("ads", "i =" + i);
						LogInfo.log("ads", " mCommonAdItem.mediaFileUrl =" +
						mCommonAdItem.mediaFileUrl);
						LogInfo.log("ads", " mCommonAdItem.duration =" +
						mCommonAdItem.duration);
					}
					totalTimeTmp = totalTime;
                              hasAds = true;
					return adsList;
				}
			}
                  hasAds = false;
			return null;
		}

		@Override
		public void onPostExecute(ArrayList<CommonAdItem> result) {
			adsRequestTime = System.currentTimeMillis() - adsRequestTime;
			curTask = null;
			firstCommonAdItem = null;
			if (null != result && result.size() > 0) {
				isHaveFrontAds = true;
				firstCommonAdItem = distributeAdsInfo(result);
			} else {
				isHaveFrontAds = false;
				FrontAdFinish();
				return;
			}
			if (isSupportM3U8) {
				if (null != adListener) {
					adListener.onM3U8(result);
				}
				if (null != firstCommonAdItem && SimpleAdMediaType.VIDEO == firstCommonAdItem.mediaFileType) {
					FrontAdFinish();
					return;
				}
			}
			if (isCancel() || null == firstCommonAdItem) {
				isHaveFrontAds = false;
				FrontAdFinish();
				return;
			}
			adsLoadingTime = System.currentTimeMillis();
			isFirstVideoViewWorking = true;
			secondCommonAdItem = distributeAdsInfo(result);

			mAdStatusManagerFirst = null;
			mAdStatusManagerSecond = null;

			mAdStatusManagerFirst = new AdStatusManager(firstCommonAdItem);

			handler(firstCommonAdItem);
			if (secondCommonAdItem != null) {
				videoViewPlayInit(secondCommonAdItem.mediaFileUrl, false, secondCommonAdItem);
			}
		}

		@Override
		public void onPreExecute() {

		}
	}

	/**
	 * 请求直播前帖片
	 * */
	private class RequestLiveFrontAd extends LetvSimpleAsyncTask<ArrayList<CommonAdItem>> {

		private String streamUrl;

		private String uuid;

		private String uid;

		private String py;

		private String ty;

		public RequestLiveFrontAd(Context context, String streamUrl, String uuid, String uid, String py, String ty) {
			super(context);
			adsRequestTime = System.currentTimeMillis();
			if (curTask != null) {
				curTask.cancel(true);
			}
			curTask = null;
			curTask = this;
			if (adsList != null) {
				adsList.clear();
			}
			firstCommonAdItem = null;
			secondCommonAdItem = null;

			mAdStatusManagerFirst = null;
			mAdStatusManagerSecond = null;

			totalTime = 0;
			adsItemIndex = 0;
			this.streamUrl = streamUrl;
			this.uuid = uuid;
			this.uid = uid;
			this.py = py;
			this.ty = ty;

		}

		@Override
		public ArrayList<CommonAdItem> doInBackground() {
                  Log.d("ads","RequestLiveFrontAd" + "streamUrl=" +streamUrl+ "uuid=" +uuid + "uid=" +uid + "py=" +py + "ty=" + "iPlayerStatusDelegate=" + iPlayerStatusDelegate);
                  if (isCancel()) {
                        return null;
                  }
                  Log.d("ads","RequestLiveFrontAd" + "streamUrl=" +streamUrl+ "uuid=" +uuid + "uid=" +uid + "py=" +py + "ty=" + "iPlayerStatusDelegate=" + iPlayerStatusDelegate);


			return AdsHttpApi.getLiveFrontAd(context, streamUrl, uuid, uid, py, ty, iPlayerStatusDelegate, false);
		}

		@Override
		public void onPostExecute(ArrayList<CommonAdItem> result) {
			adsRequestTime = System.currentTimeMillis() - adsRequestTime;
			curTask = null;
			if (null != result && result.size() > 0) {
				firstCommonAdItem = result.get(0);
				adsItemIndex++;// distributeAdsInfo(result);
				if (null == firstCommonAdItem) {
					isHaveFrontAds = false;
					FrontAdFinish();
					return;
				} else {
					isHaveFrontAds = true;
					totalTime += firstCommonAdItem.duration;
					totalTimeTmp = totalTime;
					// LogInfo.log("ads", " mCommonAdItem.mediaFileUrl =" +
					// firstCommonAdItem.mediaFileUrl);
					// LogInfo.log("ads", " mCommonAdItem.duration =" +
					// firstCommonAdItem.duration);
				}
			} else {
				isHaveFrontAds = false;
				FrontAdFinish();
				return;
			}
			// if (null != adInfo) {
			// if (null != adListener) {
			// adListener.onM3U8(result);
			// }
			// if(SimpleAdMediaType.VIDEO == adInfo.mediaFileType){
			// return;
			// }
			// }
			// mPlayAdInfo = result;
			if (isCancel() || null == firstCommonAdItem) {
				isHaveFrontAds = false;
				FrontAdFinish();
				return;
			}
			if (mVideoViewSecond != null) {
				mVideoViewSecond.setVisibility(View.GONE);
			}
			isFirstVideoViewWorking = true;
			mAdStatusManagerFirst = new AdStatusManager(firstCommonAdItem);
			handler(firstCommonAdItem);
		}

		@Override
		public void onPreExecute() {
			// mVideoViewLayout.setVisibility(View.VISIBLE);
			// if (!isPauseAd) {
			// mLoading.setVisibility(View.VISIBLE);
			// }
		}
	}

	/**
	 * 请求点播暂停
	 * */
	private class RequestDemandPauseAd extends LetvSimpleAsyncTask<CommonAdItem> {

		private String cid;

		private String aid;

		private String vid;

		private String mmsid;

		private String uuid;

		private String uid;

		private String vlen;

		private String py;

		private String ty;

		public RequestDemandPauseAd(Context context, int cid, long aid, long vid, String mmsid, String uuid,
				String uid, String vlen, String py, String ty) {
			super(context);
			if (curTask != null)
				curTask.cancel(true);
			curTask = null;
			curTask = this;
			this.cid = String.valueOf(cid);
			this.aid = String.valueOf(aid);
			this.vid = String.valueOf(vid);
			this.mmsid = mmsid;
			this.uuid = uuid;
			this.uid = uid;
			this.vlen = vlen;
			this.py = py;
			this.ty = ty;
		}

		@Override
		public CommonAdItem doInBackground() {

			if (isCancel()) {
				return null;
			}
                  LogInfo.log("ads", "----------pause start----");
			ArrayList<CommonAdItem> ads = AdsHttpApi.getPauseAd(context, cid, aid, vid, mmsid, uuid, uid, vlen, py, ty,
					iPlayerStatusDelegate);

			if (ads != null && ads.size() > 0) {
				return ads.get(0);
			}

			return null;
		}

		@Override
		public void onPostExecute(CommonAdItem result) {
			curTask = null;
			// mPlayAdInfo = result;
			if (result == null) {
				LogInfo.log("ads", "result is null");
				return;
			} else {
				LogInfo.log("ads", "result.url =" + result.mediaFileUrl);
				handlerPauseAd(result);
			}
		}

		@Override
		public void onPreExecute() {
		}
	}

	/**
	 * 点击广告跳转到系统浏览器,并请求点击曝光地址和第三方点击曝光地址
	 */
	public void clickGotoWeb(CommonAdItem info) {
		if (info.clickShowType == AdClickShowType.ExternalWebBrowser
				|| info.clickShowType == AdClickShowType.InternalWebView) {
			if (AdsUtils.checkClickEvent()) {
				if (!TextUtils.isEmpty(info.getClickUrl())) {
					AdsUtils.gotoWeb(getActivity(), info.getClickUrl(), info.clickShowType);
				}
			}
		}
		clickStatistics(info);
	}

	/**
	 * 曝光上报
	 * */
	public void exposureStatistics(CommonAdItem info) {
		/**
		 * 曝光统计
		 * */
		if (adStatusManager != null) {
			adStatusManager.onAdPlayStart();
		}
	}

	/**
	 * 点击上报
	 * */
	public void clickStatistics(CommonAdItem info) {
		/**
		 * 点击统计
		 * */
		if (adStatusManager != null) {
			adStatusManager.onAdClicked();
			// LogInfo.log("ads", "----------clickStatistics----------");
		}
	}

	public boolean isShow() {
		return isShow;
	}

	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}

	public interface PlayAdListener {
		/**
		 * 广告结束
		 * 
		 * @param isFinishByHand
		 *            是否是手动结束
		 */
		public void onFinish(boolean isFinishByHand ,boolean hasAds);

		/**
		 * 广告拼接 m3u8
		 * 
		 * @param ads
		 */
		public void onM3U8(ArrayList<CommonAdItem> ads);
	}

	public interface VipViewCallBack {
		public void onClick();

		public void ads3G2GClick();

		public int getVideoCurrentTime();
	};

	public String getAc() {
		if (isShow) {
			return "001_0";
		}

		return "000_0";
	}

	IPlayerStatusDelegate iPlayerStatusDelegate = new IPlayerStatusDelegate() {

		@Override
		public int getVideoCurrentTime() {
			if (viewCallBack != null) {
				return viewCallBack.getVideoCurrentTime();
			}
			return 0;
		}

		@Override
		public int getAdCurrentTime() {
			if (curAdType == FRONT_MP4 && getCurrentWorkingVideo(isFirstVideoViewWorking) != null) {
				return curTime >= adsPlayedTime * 1000 ? (curTime - adsPlayedTime * 1000) : 0;
			} else if (curAdType == FRONT_JPG) {
				return curTime * 1000;
			}
			return 0;
		}

		@Override
		public Rect getPlayerRect() {
			if (AdsManager.getInstance().getVideoCallBack() != null) {
				return AdsManager.getInstance().getVideoCallBack().getPlayerRect();
			}
			return null;
		}

		@Override
		public void pauseVideo() {
			if (AdsManager.getInstance().getVideoCallBack() != null) {
				AdsManager.getInstance().getVideoCallBack().pauseVideo();
			}
		}

		@Override
		public void resumeVideo() {
			if (AdsManager.getInstance().getVideoCallBack() != null) {
				AdsManager.getInstance().getVideoCallBack().resumeVideo();
			}
		}
	};
	/**
	 * 播放完成回调
	 * */
	private OnCompletionListener onCompletionListenerFirst = new OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {
			mVideoViewFirst.stopPlayback();
			mVideoViewFirst.setVisibility(View.INVISIBLE);
			if (firstCommonAdItem != null) {
				adsPlayedTime += firstCommonAdItem.duration;
				if (mAdStatusManagerFirst != null) {
					mAdStatusManagerFirst.onAdPlayComplete();
				} else {
					mAdStatusManagerFirst = new AdStatusManager(firstCommonAdItem);
					mAdStatusManagerFirst.onAdPlayComplete();
				}
				// LogInfo.log("ads", "1---onCompletionListenerFirst---" +
				// firstCommonAdItem.index);
			}
			if (secondCommonAdItem != null) {
				// LogInfo.log("ads", "2---onCompletionListenerFirst---" +
				// firstCommonAdItem.index);
				whichVideoViewToPlay(false, secondCommonAdItem);
				firstCommonAdItem = distributeAdsInfo(adsList);
				if (firstCommonAdItem != null) {
					if (!videoViewPlayInit(firstCommonAdItem.mediaFileUrl, true, firstCommonAdItem)) {
						firstCommonAdItem = null;
					}
				}
			} else {
				FrontAdFinish();
				stopHandlerTime();
			}

		}
	};
	/**
	 * 准备完成回调
	 * */
	private OnPreparedListener mOnPreparedListenerFirst = new OnPreparedListener() {
		@Override
		public void onPrepared(MediaPlayer mp) {
			curAdType = FRONT_MP4;
			if (!isFirstVideoViewWorking) {
				mVideoViewFirst.pause();
			} else {
				startHandlerTime();
			}
			// LogInfo.log("ads", "---mOnPreparedListenerFirst---");
			/**
			 * 加载完成统计
			 * */
			if (firstCommonAdItem != null) {
				if (mAdStatusManagerFirst != null) {
					mAdStatusManagerFirst.onAdLoadComplete((int) (System.currentTimeMillis() - startTime));
				} else {
					mAdStatusManagerFirst = new AdStatusManager(firstCommonAdItem);
					mAdStatusManagerFirst.onAdLoadComplete((int) (System.currentTimeMillis() - startTime));
				}
			}
		}
	};
	/**
	 * 播放错误回调
	 * */
	private OnErrorListener mOnErrorListenerFirst = new OnErrorListener() {
		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			// LogInfo.log("ads", "---mOnErrorListenerFirst---");
			mVideoViewFirst.setVisibility(View.INVISIBLE);
			if (firstCommonAdItem != null) {
				if (mAdStatusManagerFirst != null) {
					mAdStatusManagerFirst.onAdLoadError();
				} else {
					mAdStatusManagerFirst = new AdStatusManager(firstCommonAdItem);
					mAdStatusManagerFirst.onAdLoadError();
				}
				// LogInfo.log("ads", "0---firstCommonAdItem---" +
				// firstCommonAdItem.index);
				totalTime = totalTime - firstCommonAdItem.duration;
				totalTimeTmp = totalTime;
			}
			if (secondCommonAdItem != null) {
				// LogInfo.log("ads", "1---firstCommonAdItem---" +
				// firstCommonAdItem.index);
				if (isFirstVideoViewWorking) {// 第一个播放器出错，判断当前播放的是否是第一个，是则开始播放第二个；同时准备下个播放器（也就是第一个）
					whichVideoViewToPlay(false, secondCommonAdItem);
				}
				firstCommonAdItem = distributeAdsInfo(adsList);
				if (firstCommonAdItem != null) {
					if (!videoViewPlayInit(firstCommonAdItem.mediaFileUrl, true, firstCommonAdItem)) {
						firstCommonAdItem = null;
					}
				} else {
					mVideoViewFirst.setVisibility(View.GONE);
				}
			} else {
				stopHandlerTime();
				FrontAdFinish();
				onDestroy();
			}
			return true;
		}
	};
	/**
	 * 播放完成回调
	 * */
	private OnCompletionListener onCompletionListenerSecond = new OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mp) {
			mVideoViewSecond.stopPlayback();
			mVideoViewSecond.setVisibility(View.INVISIBLE);
			if (secondCommonAdItem != null) {
				adsPlayedTime += secondCommonAdItem.duration;
				if (mAdStatusManagerSecond != null) {
					mAdStatusManagerSecond.onAdPlayComplete();
				} else {
					mAdStatusManagerSecond = new AdStatusManager(secondCommonAdItem);
					mAdStatusManagerSecond.onAdPlayComplete();
				}
				// LogInfo.log("ads",
				// "---AdStatusManager onCompletionListenerSecond---");
			}

			// LogInfo.log("ads", "---onCompletionListenerSecond---");
			if (firstCommonAdItem != null) {
				whichVideoViewToPlay(true, firstCommonAdItem);// 第二个播放器播放完成，开始播放第一个；同时初始化第一个
				secondCommonAdItem = distributeAdsInfo(adsList);
				if (secondCommonAdItem != null) {
					if (!videoViewPlayInit(secondCommonAdItem.mediaFileUrl, false, secondCommonAdItem)) {
						secondCommonAdItem = null;
					}
				}
			} else {
				FrontAdFinish();
				stopHandlerTime();
			}

		}
	};
	/**
	 * 准备完成回调
	 * */
	private OnPreparedListener mOnPreparedListenerSecond = new OnPreparedListener() {
		@Override
		public void onPrepared(MediaPlayer mp) {
			curAdType = FRONT_MP4;
			if (isFirstVideoViewWorking) {
				mVideoViewSecond.pause();
			} else {
				startHandlerTime();
			}

			// LogInfo.log("ads", "---mOnPreparedListenerSecond---");
			/**
			 * 加载完成统计
			 * */
			if (secondCommonAdItem != null) {
				if (mAdStatusManagerSecond != null) {
					mAdStatusManagerSecond.onAdLoadComplete((int) (System.currentTimeMillis() - startTime));
				} else {
					mAdStatusManagerSecond = new AdStatusManager(secondCommonAdItem);
					mAdStatusManagerSecond.onAdLoadComplete((int) (System.currentTimeMillis() - startTime));
				}
			}

		}
	};
	/**
	 * 播放错误回调
	 * */
	private OnErrorListener mOnErrorListenerSecond = new OnErrorListener() {
		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			mVideoViewSecond.setVisibility(View.INVISIBLE);
			if (secondCommonAdItem != null) {
				// LogInfo.log("ads", "---secondCommonAdItem---" +
				// secondCommonAdItem.index);
				if (mAdStatusManagerSecond != null) {
					mAdStatusManagerSecond.onAdLoadError();
				} else {
					mAdStatusManagerSecond = new AdStatusManager(secondCommonAdItem);
					mAdStatusManagerSecond.onAdLoadError();
				}
				totalTime = totalTime - secondCommonAdItem.duration;
				totalTimeTmp = totalTime;
			}
			// LogInfo.log("ads", "---mOnErrorListenerSecond---");
			if (firstCommonAdItem != null) {
				if (!isFirstVideoViewWorking) {// 第二个播放器出错，判断当前播放的是否是第二个，是则开始播放第一个；同时准备下个播放器（也就是第二个）
					whichVideoViewToPlay(true, firstCommonAdItem);
				}
				secondCommonAdItem = distributeAdsInfo(adsList);
				if (secondCommonAdItem != null) {
					if (!videoViewPlayInit(secondCommonAdItem.mediaFileUrl, false, secondCommonAdItem)) {
						secondCommonAdItem = null;
					}
				} else {
					mVideoViewSecond.setVisibility(View.GONE);
				}
			} else {
				stopHandlerTime();
				FrontAdFinish();
				onDestroy();
			}
			return true;
		}
	};

	/**
	 * 设置播放器数据、监听
	 * 
	 * @param playUrl
	 * @param whichVideoView
	 * @return
	 */
	private boolean videoViewPlayInit(final String playUrl, final boolean whichVideoView, CommonAdItem adItem) {
		if (!TextUtils.isEmpty(playUrl)) {
			final VideoView mVideoView = getCurrentWorkingVideo(whichVideoView);
			if (whichVideoView) {
				mAdStatusManagerFirst = null;
				mAdStatusManagerFirst = new AdStatusManager(adItem);
			} else {
				mAdStatusManagerSecond = null;
				mAdStatusManagerSecond = new AdStatusManager(adItem);
			}
			if (VERSION.SDK_INT >= 11) {// android鐗堟湰鍙� 灏忎簬 11 鐨勭壒闇€澶勭悊锛� 涓嶆敮鎸佸弻缂撳啿
//				if(whichVideoView) {//绗簩涓挱鏀惧櫒 鍑嗗 鍙互鏄剧ず 锛屼笉浼氶伄鎸＄涓€涓�
					mVideoView.setVisibility(View.VISIBLE);
//				} else {
//					mVideoView.setVisibility(View.INVISIBLE);
//				}
				mVideoView.setVideoPath(playUrl);
				mVideoView.setOnErrorListener(whichVideoView ? mOnErrorListenerFirst : mOnErrorListenerSecond);
				mVideoView.setOnPreparedListener(whichVideoView ? mOnPreparedListenerFirst : mOnPreparedListenerSecond);
				mVideoView.setOnCompletionListener(whichVideoView ? onCompletionListenerFirst
						: onCompletionListenerSecond);
				mVideoView.requestFocus();
				mVideoView.start();
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 分发广告信息
	 * 
	 * @param result
	 * @return
	 */
	private CommonAdItem distributeAdsInfo(ArrayList<CommonAdItem> result) {
		if (result != null && result.size() > adsItemIndex) {
			// LogInfo.log("ads", "result.size() =" + result.size() +
			// "  adsItemIndex =" + adsItemIndex);
			adsItemIndex++;
			return result.get(adsItemIndex - 1);
		} else {
			// LogInfo.log("ads", " null ----- " + "  adsItemIndex =" +
			// adsItemIndex);
			return null;
		}
	}

	/**
	 * 那个播放器开始播放
	 * 
	 * @param which
	 */
	private void whichVideoViewToPlay(boolean which, CommonAdItem mAdItem) {
		// LogInfo.log("ads", "1---which = " + which + "---mAdItem=" +
		// mAdItem.index);
		VideoView mVideoView = getCurrentWorkingVideo(which);
		isFirstVideoViewWorking = which;
		mVideoView.setVisibility(View.VISIBLE);
		if(which){
			mAdStatusManagerFirst = new AdStatusManager(mAdItem);
		} else {
			mAdStatusManagerSecond = new AdStatusManager(mAdItem);
		}
		if (VERSION.SDK_INT < 11) {// android版本号 小于 11 的特需处理， 不支持双缓冲
			mVideoView.setVideoPath(mAdItem.mediaFileUrl);
			mVideoView.setOnErrorListener(which ? mOnErrorListenerFirst : mOnErrorListenerSecond);
			mVideoView.setOnPreparedListener(which ? mOnPreparedListenerFirst : mOnPreparedListenerSecond);
			mVideoView.setOnCompletionListener(which ? onCompletionListenerFirst : onCompletionListenerSecond);
		}
		mVideoView.requestFocus();
		startPlayHandler(mAdItem);
	}

	private void startPlayHandler(final CommonAdItem adInfo) {
		if (!isReady || isDestroy) {
			isPauseAd = false;
			return;
		}
		if (adInfo == null) {
			FrontAdFinish();
			return;
		}
		if (adInfo.getCuePointType() == CuePointType.Standard) {
			mTimeTextView.setVisibility(View.GONE);
		} else {
			mTimeTextView.setVisibility(View.VISIBLE);
		}
		if(isFirstVideoViewWorking){
			if(mAdStatusManagerFirst == null){
				mAdStatusManagerFirst = new AdStatusManager(adInfo);
			}
			adStatusManager = mAdStatusManagerFirst;
		} else {
			if(mAdStatusManagerSecond == null){
				mAdStatusManagerSecond = new AdStatusManager(adInfo);
			}
			adStatusManager = mAdStatusManagerSecond;
		}
		// LogInfo.log("ads", "xxxxxxx---adInfo---" + adInfo.index);
		if (SimpleAdMediaType.VIDEO == adInfo.mediaFileType) { // 显示视频广告
			star();
			isShow = true;
			mVideoViewClick.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clickGotoWeb(adInfo);
				}
			});
			// totalTime = adInfo.duration;
			exposureStatistics(adInfo);
		} else if (SimpleAdMediaType.BITMAP == adInfo.mediaFileType) {// 显示图片广告
			if (!TextUtils.isEmpty(adInfo.mediaFileUrl)) {
				LetvCacheMannager.getInstance().loadImage(adInfo.mediaFileUrl, mImageView, new ImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						isShow = true;
						mImageView.setImageBitmap(loadedImage);
						mImageView.setVisibility(View.VISIBLE);
						mTimeTextView.setVisibility(View.VISIBLE);
						muteView.setVisibility(View.GONE);
						mTimeTextView.setVisibility(View.VISIBLE);
						vipView.setVisibility(View.GONE);
						mLoading.setVisibility(View.GONE);
						curAdType = FRONT_JPG;
						startHandlerTime();
						/**
						 * 加载完成统计
						 * */
						if (adStatusManager != null) {
							adStatusManager.onAdLoadComplete((int) (System.currentTimeMillis() - startTime));
						}
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {

					}
				});
				mImageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						clickGotoWeb(adInfo);
					}
				});
				totalTime = adInfo.duration;
				totalTimeTmp = totalTime;
				exposureStatistics(adInfo);
			}
		} else {
			FrontAdFinish();
		}
	}

	/**
	 * 获取播放的videoView
	 * 
	 * @return
	 */
	private VideoView getCurrentWorkingVideo(boolean isFirstVideoView) {
		if (isFirstVideoView) {
			return mVideoViewFirst;
		} else {
			return mVideoViewSecond;
		}
	}

	/**
	 * 
	 * @param ahs
	 *            广告拼接结果 广告前贴片
	 * @param vs
	 *            广告拼接结果 正片
	 */
	public void setAdJoinBean(String ahs, String vs) {
		ArrayList<CommonAdItem> adListCopy = new ArrayList<CommonAdItem>();
		if (adsList == null || adsList.size() <= 0) {
			return;
		}
		adListCopy.addAll(adsList);
		if (!TextUtils.isEmpty(ahs)) {
			String[] splitStr = ahs.split(",");
			int count = adListCopy.size();
			if (count > 0 && count == splitStr.length) {
				for (int i = 0; i < count; i++) {
					adListCopy.get(i).combineErrorCode = splitStr[i];
				}
				adStatusManager.onAdLoadError(adListCopy);// 上传拼接错误广告
			}
		}
	}

	/**
	 * 新增IVideoStatusInformer ，用于客户端将播放状态通知给广告
	 * 
	 * @return
	 */
	public IVideoStatusInformer getIVideoStatusInformer() {
		if (informer != null) {
			return informer;
		}
		return null;
	}

	/**
	 * 获取广告视频总时长
	 * 
	 * @return
	 */
	public int getAdsVideoTotalTime() {
		return totalTimeTmp;
	}

	/**
	 * 广告加载时长
	 * 
	 * @return
	 */
	public long getAdsLoadingTime() {
		return adsRequestTime;
	}

	/**
	 * 广告加载时长
	 * 
	 * @return
	 */
	public long getAdsPlayLoadTime() {
		return adsLoadingTime;
	}

}