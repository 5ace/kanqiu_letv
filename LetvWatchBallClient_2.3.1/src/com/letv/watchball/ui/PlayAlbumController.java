package com.letv.watchball.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.util.Log;
import cn.com.iresearch.mvideotracker.IRVideo;

import com.letv.adlib.model.ad.common.CommonAdItem;
import com.letv.adlib.model.ad.types.SimpleAdMediaType;
import com.letv.ads.ADPlayFragment;
import com.letv.pp.service.LeService;
import com.letv.pp.url.PlayUrl;
import com.letv.star.bean.User;
import com.letv.watchball.activity.LetvAccountLogin;
import com.letv.watchball.bean.*;
import com.letv.watchball.parser.*;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.letv.datastatistics.DataStatistics;
import com.letv.datastatistics.entity.StatisticsVideoInfo;
import com.letv.datastatistics.util.DataConstant;
import com.letv.http.bean.LetvDataHull;
import com.letv.watchball.LetvApplication;
import com.letv.watchball.R;
import com.letv.watchball.adapter.DetailPlayPagerAdapter;
import com.letv.watchball.adapter.DetailPlayScrollingTabsAdapter;
import com.letv.watchball.async.LetvBaseTaskImpl;
import com.letv.watchball.async.LetvHttpAsyncTask;
import com.letv.watchball.async.LetvSimpleAsyncTask;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.service.PipService;
import com.letv.watchball.ui.impl.BasePlayActivity;
import com.letv.watchball.utils.LetvCacheDataHandler;
import com.letv.watchball.utils.LetvConstant;
import com.letv.watchball.utils.LetvPlayRecordFunction;
import com.letv.watchball.utils.LetvTools;
import com.letv.watchball.utils.LetvUtil;
import com.letv.watchball.utils.LogInfo;
import com.letv.watchball.utils.NetWorkTypeUtils;
import com.letv.watchball.utils.PlayUtils;
import com.letv.watchball.utils.UIControllerUtils;
import com.letv.watchball.utils.UIs;
import com.letv.watchball.view.PlayLoadLayout;
import com.letv.watchball.view.PlayLoadLayout.PlayLoadLayoutCallBack;
import com.letv.watchball.view.ScrollTabIndicator;
import com.letv.watchball.view.SettingViewPager;
import com.media.VideoView;
import com.media.VideoView.VideoViewStateChangeListener;

public class PlayAlbumController extends PlayController implements
		VideoViewStateChangeListener, PlayControllerCallBack,
		PlayLoadLayoutCallBack, ADPlayFragment.PlayAdListener {
	/**
	 * 刷新进度
	 * */
	private final int HANDLER_TIME = 0x100;
	/**
	 * 统计播放时间
	 */
	private final int UPDATE_STATICICS_TIME = 0x101;
	/**
	 * 统计阻塞时间
	 */
	private final int UPDATE_STATICICS_BLOCK_TIME = 0x102;

	/**
	 * 一秒
	 * */
	private final int HANDLER_TIME_DELAYED = 1000;

	/**
	 * 记录到播放流程，哪一个环节失败了；0 无失败 ，1
	 * 专辑详情，2，视频详情，3，视频列表，4，canplay和付费信息接口，5，videofile接口，6，调度中，7，缓冲中或播放中
	 * */
	public int playCallBackState;

	/**
	 * 是否跳过片头片尾
	 * */
	public boolean isSikp;

	/**
	 * 半屏控制
	 * */
	private PlayAlbumHalfController mHalfController;

	/**
	 * 全屏控制
	 * */
	private PlayAlbumFullController mFullController;

	/**
	 * 半屏内容展示tabs
	 * */
	private ScrollTabIndicator tabs;

	/**
	 * 半屏内容展示viewPage
	 * */
	private SettingViewPager viewPager;

	/**
	 * 半屏内容展示viewPage适配器
	 * */
	private DetailPlayPagerAdapter viewPagerAdapter;

	/**
	 * 加载 错误 提示布局
	 * */
	private PlayLoadLayout loadLayout;

	/**
	 * 加载视频错误码
	 */
	private int errorCodeJoint = 0;

	// /**
	// * 专辑 ID
	// * */
	// public long aid;
	//
	// /**
	// * 视频ID
	// * */
	// public long vid;
	private boolean isPlayedAdFinish = false;
	/**
	 * 真实地址
	 * */
	private String realUrl;

	/**
	 * 当前视频所在专辑（可能为空）
	 * */
	private AlbumNew album;

	/**
	 * 当前播放的视频信息（不能为空）
	 * */
	private Video video;

	/**
	 * 当前视频是否有高清
	 * */
	public boolean hasHd;

	/**
	 * 当前视频是否有标清
	 * */
	public boolean hasStandard;

	/**
	 * 播放是否是高清
	 * */
	public boolean isHd;

	/**
	 * 是否来自杜比频道
	 * */
	public boolean isDolby;

	/**
	 * 播放视频的播放记录
	 * */
	public PlayRecord playRecord;

	/**
	 * 是否是本地文件
	 * */
	private boolean isLocalFile;

	/**
	 * 本地文件地址
	 * */
	private String filePath;

	/**
	 * 片头时间
	 * */
	public long bTime;

	/**
	 * 总时间
	 * */
	private long totleTime;

	/**
	 * 当前时间
	 * */
	private long curTime;

	/**
	 * 片尾时间
	 * */
	public long eTime;

	/**
	 * 跳过时间
	 * */
	private long seek;

	/**
	 * 付费视频，是否支持播放
	 * */
	public boolean canplay;

	/**
	 * 是否已经提示过；
	 * */
	private boolean alreadyPrompt;

	/**
	 * 下载只显示一次提示的标记
	 * */
	public boolean isShowToast = true;

	/**
	 * 跳过片尾提示一次
	 * */
	public boolean isShowSkipEnd = true;

	/**
	 * 记录请求的任务
	 * */
	public List<LetvBaseTaskImpl> tasks = new ArrayList<LetvBaseTaskImpl>();

	/**
	 * 码流切换次数计算
	 */
	private String uuidTimp;
	/**
	 * 播放时间统计
	 */
	private long timeElapsed = 0;

	private long finaltime = 0;
	/**
	 * 上一次播放时间
	 */
	private long lastTimeElapsed = 0;
	/**
	 * 阻塞时间
	 */
	private long blockTime = 0;
	/**
	 * 重试次数
	 */
	private int retryNum = 0;
	private int updateCount = 0;
	/**
	 * 当前播放视频的码流
	 */
	private String streamLevel = PreferencesManager.getInstance().isPlayHd() ? "13"
			: "21";

	/**
	 * ***************************广告，播放时长相关参数***********************
	 */

	private long adConsumeTime = 0;// 广告时长
	private long adJoinConsumeTime = 0;// 广告拼接时长
	private long albumPayConsumeTime = 0;// 专辑付费信息时长
	private long videoDetailsConsumeTime = 0;// 视频详情时长
	private long getRealUrlConsumeTime = 0;// 正式播放地址时长
	private long albumVListConsumeTime = 0;// 专辑视频列表时长
	private long videoFileConsumeTime = 0;// videoFile时长
	private long canPlayConsumeTime = 0;// 专辑是否可播放时长
	private long totalConsumeTime = 0;// 总时长
	private long loadingConsumeTime = 0;// 视频加载时长
	private long beginPlayedTime;// 初始播放位置

	private boolean isgslb = false;// 视频调度成功、失败
	private boolean iscload = false;// 视频下载成功、失败
	private boolean ispush = false;// 视频下载成功、失败
	private long bufferTime = 0;// 视频卡顿总时长
	private int bufferNum = 0;// 视频卡顿总次数
	private String videoSend = "vsend=CDN";// 视频内容分发系统
	private String vformat = "vformat=m3u8";// 视频格式

	private boolean isbuffered = false;// 视频缓冲标志
	private boolean isFirstPlay = true;// 视频第一次播放
	private boolean needPlayAd = true;
	public boolean isPlayedAd = false;
	private boolean isSingle = false;

	// ***************************广告，播放时长相关参数 end***********************

	private enum PLAY_MODE {
		/**
		 * m3u8广告拼接
		 */
		M3U8,
		/**
		 * 普通播放
		 */
		NORMAL,
		/**
		 * 初始状态
		 */
		NONE;
	}

	private PLAY_MODE mPlayMode = PLAY_MODE.NONE;

	/**
	 * 刷新进度handler
	 * */
	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_TIME:
				if (getActivity() == null
						|| getActivity().getPlayFragment() == null) {
					return false;
				}
				long oldTime = curTime;
				timeElapsed++;
				curTime = getActivity().getPlayFragment().getCurrentPosition();
				/************* add by zlb for pip ************/
				if (getLaunchMode() == PLAY_DEFAULT) { // 播放本地视频(不包括有下载记录的视频)时，切到小窗要传过去的播放参数
					localSeek = curTime / 1000;
				}
				/************* end by zlb for pip ************/
				int bufferPercentage = (int) (totleTime
						* getActivity().getPlayFragment().getBufferPercentage() / 100000);
				if (oldTime == curTime) {
					loadLayout.loading();
					blockTime++;
					statisticsVideoInfo.setBufcount(statisticsVideoInfo
							.getBufcount() + 1);
				} else {
					loadLayout.finish();
					if (playRecord != null) {
						playRecord.setPlayedDuration(curTime / 1000);
					}
					if (blockTime > 0) {
						handler.sendEmptyMessage(UPDATE_STATICICS_BLOCK_TIME);
					}
				}
				if (mHalfController != null)
					mHalfController.updateProgress((int) curTime / 1000,
							bufferPercentage);

				if (mFullController != null)
					mFullController.updateProgress((int) curTime / 1000,
							bufferPercentage);
				if (isSikp && eTime > 0) {
					if (curTime / 1000 + 15 >= eTime && isShowSkipEnd) {
						isShowSkipEnd = false;
						// UIs.showToast("即将跳过片尾");
					}
					if (curTime / 1000 >= eTime) {// 判断是跳过片尾
						stopHandlerTime();
						playNext();
						playRecord.setPlayedDuration(-1);
						return false;
					}
				}
				handler.sendEmptyMessageDelayed(HANDLER_TIME,
						HANDLER_TIME_DELAYED);
				break;
			case UPDATE_STATICICS_BLOCK_TIME:
				// updatePlayDataStatistics(DataConstant.StaticticsVersion2Constatnt.PlayerAction.BLOCK_ACTION,
				// blockTime);
				blockTime = 0;
				break;
			case UPDATE_STATICICS_TIME:
				handler.sendEmptyMessageDelayed(UPDATE_STATICICS_TIME,
						UPDATE_STATICICS_TIME);
				if (updateCount == 0) {
					if (timeElapsed - lastTimeElapsed < 15) {
						updateCount = 0;
						// handler.sendEmptyMessageDelayed(UPDATE_STATICICS_TIME,
						// (timeElapsed - lastTimeElapsed) * 1000);
					} else {
						updateCount = 1;
						updatePlayDataStatistics(
								DataConstant.StaticticsVersion2Constatnt.PlayerAction.TIME_ACTION,
								timeElapsed);
						lastTimeElapsed = timeElapsed;

						// handler.sendEmptyMessageDelayed(UPDATE_STATICICS_TIME,
						// 60000);
					}
				} else if (updateCount == 1) {
					if (timeElapsed - lastTimeElapsed < 60) {
						updateCount = 1;
						// handler.sendEmptyMessageDelayed(UPDATE_STATICICS_TIME,
						// (timeElapsed - lastTimeElapsed) * 1000);
					} else {
						updateCount = 2;
						updatePlayDataStatistics(
								DataConstant.StaticticsVersion2Constatnt.PlayerAction.TIME_ACTION,
								(timeElapsed - lastTimeElapsed));
						lastTimeElapsed = timeElapsed;

						// handler.sendEmptyMessageDelayed(UPDATE_STATICICS_TIME,
						// 3 * 60000);
					}

				} else if (updateCount == 2) {
					if (timeElapsed - lastTimeElapsed > 180) {
						updateCount = 2;
						if (timeElapsed - lastTimeElapsed >= 180) {
							finaltime = 180;
						}
						updatePlayDataStatistics(
								DataConstant.StaticticsVersion2Constatnt.PlayerAction.TIME_ACTION,
								finaltime);
						lastTimeElapsed = timeElapsed;
						handler.removeMessages(UPDATE_STATICICS_TIME);
						handler.sendEmptyMessageDelayed(UPDATE_STATICICS_TIME,
								3 * 60000);
					}
				}
				break;
			}

			return false;
		}
	});

	/*************************************** 统计相关参数 *************************************/

	/**
	 * 统计对象
	 * */
	private StatisticsVideoInfo statisticsVideoInfo = new StatisticsVideoInfo();

	/**
	 * 开始播放的时间（s）
	 * */
	private long startTime;

	/**
	 * 开始播放的时间（s）
	 * */
	private long requestStartTime;

	/**************************************************************************************/
	/**
	 * 是否可以跳小窗播放
	 */
	private boolean canToPip = false;

	/**** add by zlb for pip *****/
	/**
	 * 本地视频地址(不包括有下载记录的视频);
	 */
	private String localPath = null;
	/**
	 * 本地视频(不包括有下载记录的视频)播放的位置，需要传给小窗,单位s
	 */
	private long localSeek;
	private PlayUrl p2pPlayer;
	/**
	 * 设置p2p开关
	 */
	private boolean isP2PMode = PreferencesManager.getInstance().getUtp();

	/**
	 * g3调度地址
	 */
	private String ddUrl;
	private User canPlayResult;

	/**** end add by zlb for pip *****/

	public PlayAlbumController(BasePlayActivity activity) {
		super(activity);
	}

	@Override
	public void create() {
		isSikp = PreferencesManager.getInstance().isSkip();
		isHd = PreferencesManager.getInstance().isPlayHd();
		super.create();
		startLoadingData();

		/*
		 * 艾瑞初始化视频信息
		 */
		try {
			totleTime = getActivity().getPlayFragment().getDuration();
			IRVideo.getInstance(getActivity()).newVideoPlay(
					String.valueOf(vid), this.totleTime, false);
		} catch (Exception e) {
			// Log.e("gongmeng", "vvtracker playablbum init");
		}
	}

	@Override
	protected void initLayout() {
		ADPlayFragment.VipViewCallBack mVipViewCallBack = new ADPlayFragment.VipViewCallBack() {

			@Override
			public void onClick() {
				if (PreferencesManager.getInstance().isLogin()) {
					if (false) {
						// playAdFragment.stopFrontAd();
						playAdFragment.pause();
						playAdFragment.setMobileNetBg(false);
						getFrontAd();
					} else {
						// VipProductsActivity.launchFromPlay(getActivity(),
						// getActivity().getResources().getString(R.string.pim_vip_good_title),
						// aid, vid);
					}
				} else {
					LetvAccountLogin.launch(getActivity());
				}
				String apFl = "95";
				if (UIs.isLandscape(getActivity())) {
					apFl = "a18";
				}
				// LetvUtil.staticticsInfoPost(getActivity(), apFl, null, 0, 0,
				// null, aid + "", vid + "", null);
			}

			@Override
			public int getVideoCurrentTime() {
				try {
					if (getActivity().getPlayFragment() != null) {
						return getActivity().getPlayFragment()
								.getCurrentPosition();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				return 0;
			}

			@Override
			public void ads3G2GClick() {
				if (UIs.isLandscape(getActivity())) {
					mFullController.star();
				} else {
					mHalfController.star();
				}
				playAdFragment.setPauseAd(false);
				star();
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						if (isPlayedAdFinish) {
							if (UIs.isLandscape(getActivity())) {
								mFullController.fullPlayControllerPlay
										.performClick();
							} else {
								mHalfController.halfPlayControllerPlay
										.performClick();
							}
						}

					}
				}, 500);
			}
		};
		if (getLaunchMode() == PlayController.PLAY_ALBUM) {// 播放专辑
			loadLayout = new PlayLoadLayout(getActivity());
			loadLayout.setCallBack(this);
			loadLayout.setLayoutParams(new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			getActivity().getPlayUpper().addView(loadLayout);
			loadLayout.loading();

			UIs.inflate(getActivity(), R.layout.detailplay_half_progress,
					getActivity().getPlayUpper(), true);
			UIs.inflate(getActivity(), R.layout.detailplay_full_controller,
					getActivity().getPlayUpper(), true);
			UIs.inflate(getActivity(), R.layout.play_album_lower, getActivity()
					.getPlayLower(), true);
			initViewPagerAndTab();
			setLaunchMode(PlayController.PLAY_ALBUM);
			playAdFragment = new ADPlayFragment();
			playAdFragment.setViewCallBack(mVipViewCallBack);
			playAdFragment.setAdListener(this);
			getActivity().getAdLayout().setViewCallBack(mVipViewCallBack);
			// playAdFragment.setUid(LetvUtil.getUID());
			// playAdFragment.setPcode(LetvUtil.getPcode());
			// playAdFragment.setPtid(LetvUtil.getUUID(getActivity()));
			// getActivity().getSupportFragmentManager().beginTransaction().add(R.id.play_upper,
			// playAdFragment).commit();
			getActivity().getSupportFragmentManager().beginTransaction()
					.add(R.id.play_upper, playAdFragment)
					.commitAllowingStateLoss();
			initHalfController();
			initFullController();
		} else if (getLaunchMode() == PlayController.PLAY_VIDEO) {// 播放视频
			if (!UIs.isLandscape(getActivity())) {
				UIs.screenLandscape(getActivity());
			}
			loadLayout = new PlayLoadLayout(getActivity());
			loadLayout.setCallBack(this);
			loadLayout.setLayoutParams(new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			getActivity().getPlayUpper().addView(loadLayout);
			loadLayout.loading();
			UIs.inflate(getActivity(), R.layout.detailplay_full_controller,
					getActivity().getPlayUpper(), true);

			playAdFragment = new ADPlayFragment();
			playAdFragment.setViewCallBack(mVipViewCallBack);
			playAdFragment.setAdListener(this);
			getActivity().getAdLayout().setViewCallBack(mVipViewCallBack);
			// playAdFragment.setUid(LetvUtil.getUID());
			// playAdFragment.setPcode(LetvUtil.getPcode());
			// playAdFragment.setPtid(LetvUtil.getUUID(getActivity()));
			// getActivity().getSupportFragmentManager().beginTransaction().add(R.id.play_upper,
			// playAdFragment).commit();
			getActivity().getSupportFragmentManager().beginTransaction()
					.add(R.id.play_upper, playAdFragment)
					.commitAllowingStateLoss();

			initFullController();
		} else {// 播放扫描，暂时只是这个
			if (!UIs.isLandscape(getActivity())) {
				UIs.screenLandscape(getActivity());
			}

			loadLayout = new PlayLoadLayout(getActivity());
			loadLayout.setCallBack(this);
			loadLayout.setLayoutParams(new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			getActivity().getPlayUpper().addView(loadLayout);
			loadLayout.loading();
			UIs.inflate(getActivity(), R.layout.detailplay_full_controller,
					getActivity().getPlayUpper(), true);

			playAdFragment = new ADPlayFragment();
			playAdFragment.setViewCallBack(mVipViewCallBack);
			playAdFragment.setAdListener(this);
			getActivity().getAdLayout().setViewCallBack(mVipViewCallBack);
			// playAdFragment.setUid(LetvUtil.getUID());
			// playAdFragment.setPcode(LetvUtil.getPcode());
			// playAdFragment.setPtid(LetvUtil.getUUID(getActivity()));
			// getActivity().getSupportFragmentManager().beginTransaction().add(R.id.play_upper,
			// playAdFragment).commit();
			getActivity().getSupportFragmentManager().beginTransaction()
					.add(R.id.play_upper, playAdFragment)
					.commitAllowingStateLoss();
			initFullController();
		}
	}

	@Override
	protected void readArguments() {
		if (getLaunchMode() == PlayController.PLAY_ALBUM) {
			Intent intent = getActivity().getIntent();
			aid = intent.getIntExtra("aid", 0);
			if (aid < 0) {
				aid = 0;
			}
			if (aid == 0) {
				isSingle = true;
			}
			curPage = intent.getIntExtra("curPage", 1);
			vid = intent.getIntExtra("vid", 0);
			isPlayedAd = intent.getBooleanExtra("fromPip", false);
			if (vid < 0) {
				vid = 0;
			}
			isDolby = intent.getBooleanExtra("isDolby", false);
			seek = intent.getLongExtra("seek", 0);
		} else if (getLaunchMode() == PlayController.PLAY_VIDEO) {
			Intent intent = getActivity().getIntent();
			vid = intent.getIntExtra("vid", 0);
			curPage = intent.getIntExtra("curPage", 1);
			isPlayedAd = intent.getBooleanExtra("fromPip", false);
			if (vid < 0) {
				vid = 0;
			}
			isDolby = intent.getBooleanExtra("isDolby", false);
			seek = intent.getLongExtra("seek", 0);
		} else {
			Intent intent = getActivity().getIntent();
			realUrl = intent.getStringExtra("uri");
			isPlayedAd = intent.getBooleanExtra("fromPip", false);
			seek = intent.getLongExtra("seek", 0);
		}
		updatePlayDataStatistics(
				DataConstant.StaticticsVersion2Constatnt.PlayerAction.INIT_ACTION,
				-1);
		try {
			// 环境日志在用户的每次开机上报
			IP ip = LetvApplication.getInstance().getIp();
			DataStatistics.getInstance().sendEnvInfo(getActivity(), "0", "0",
					ip == null ? "" : ip.getClient_ip(), LetvUtil.getSource(),
					true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 格式化所有属性很控件
	 * */
	@Override
	public void format() {
		destroyTasks();
		resetTimeCount();
		if (tabs != null) {
			tabs.setListener(null);
			tabs.setAdapter(null);
			tabs.removeAllViewsInLayout();
		}

		if (viewPager != null) {
			viewPager.setAdapter(null);
			viewPager.removeAllViewsInLayout();
		}

		if (viewPagerAdapter != null) {
			viewPagerAdapter.format();
		}

		if (loadLayout != null) {
			loadLayout.removeAllViews();
		}

		tabs = null;
		viewPager = null;
		loadLayout = null;

		if (mFullController != null)
			mFullController.format();
		if (mHalfController != null)
			mHalfController.format();

		getActivity().getSupportFragmentManager().beginTransaction()
				.remove(playAdFragment).commit();

		getActivity().getPlayUpper().removeAllViews();
		getActivity().getPlayLower().removeAllViews();

		clearValue();
	}

	/**
	 * 清除数据
	 * */
	private void clearValue() {
		aid = 0;
		vid = 0;
		setAlbum(null);
		setVideo(null);
		hasHd = false;
		hasStandard = false;
		isHd = false;
		isDolby = false;
		playRecord = null;
		curPage = 1;
		pageSize = 50;
		merge = 0;
		order = "-1";
		totle = 0;
		videos.clear();
		isList = false;
		realUrl = null;
		isSikp = false;
		isLocalFile = false;
		filePath = null;
		isShowToast = true;
		playAdFragment = null;
		isShowSkipEnd = true;
		seek = 0;
		resetTimeCount();
		destroyTasks();
	}

	/**
	 * 改变方向
	 * */
	@Override
	public void changeDirection(boolean isLandscape) {
		if (isLandscape) {
			if (mHalfController != null)
				mHalfController.hide();
			if (mFullController != null) {
				mFullController.show();
				showLock();
			}
		} else {
			if (mHalfController != null) {
				mHalfController.show();
				showLock();
			}
			if (mFullController != null)
				mFullController.hide();
		}
	}

	/**
	 * 初始化全屏控制器
	 * */
	private void initFullController() {
		mFullController = new PlayAlbumFullController(this, getActivity()
				.getWindow().getDecorView().getRootView());
		mFullController.setCallBack(this);
	}

	/**
	 * 初始化半屏控制器
	 * */
	private void initHalfController() {
		mHalfController = new PlayAlbumHalfController(this, getActivity()
				.getWindow().getDecorView().getRootView());
		mHalfController.setCallBack(this);
	}

	/**
	 * 初始化ViewPager和tab
	 * */
	private void initViewPagerAndTab() {
		viewPager = (SettingViewPager) getActivity().findViewById(
				R.id.detailplay_half_detail_viewpager);
		viewPager.setPagingEnabled(true);
		viewPagerAdapter = new DetailPlayPagerAdapter(getActivity()
				.getSupportFragmentManager());
		viewPager.setAdapter(viewPagerAdapter);

		viewPager.setCurrentItem(2);
		tabs = (ScrollTabIndicator) getActivity().findViewById(
				R.id.detailplay_half_detail_indicator);

		DetailPlayScrollingTabsAdapter tabsAdapter = new DetailPlayScrollingTabsAdapter(
				getActivity());
		tabs.setViewPager(viewPager, 1);
		tabs.setAdapter(tabsAdapter);
		tabs.setListener(onPageChangeListener);
	}

	@Override
	public ViewPager getViewPager() {
		return viewPager;
	}

	/**
	 * 开始刷新进度条
	 * */
	private void startHandlerTime() {
		handler.removeMessages(HANDLER_TIME);
		handler.sendEmptyMessage(HANDLER_TIME);

	}

	/**
	 * 停止刷新进度条
	 * */
	private void stopHandlerTime() {
		handler.removeMessages(HANDLER_TIME);
		handler.removeMessages(UPDATE_STATICICS_TIME);
	}

	private boolean ispost = true;

	/**
	 * 监听播放器当前状态
	 * */
	@Override
	public void onChange(int mCurrentState) {
		if (mCurrentState == VideoView.STATE_PLAYING) {
			errorCodeJoint = 0;
			startTime = System.currentTimeMillis();

			if (playAdFragment != null) {
				playAdFragment.closePauseAd();
			}

			{

				// 开始播放了初始化播放记录和进度条
				curTime = getActivity().getPlayFragment().getCurrentPosition();
				totleTime = getActivity().getPlayFragment().getDuration();

				if (mHalfController != null)
					mHalfController.initProgress((int) totleTime / 1000,
							(int) curTime / 1000, 0);

				if (mFullController != null)
					mFullController.initProgress((int) totleTime / 1000,
							(int) curTime / 1000, 0);

				if (playRecord != null) {
					playRecord.setTotalDuration(totleTime / 1000);// 根据真实播放文件初始化总时长
				}
				// 艾瑞检测，视频启动
				try {
					IRVideo.getInstance(getActivity()).videoPlay();
				} catch (Exception e) {
					// Log.e("gongmeng", "vvTracker Video play error");
				}

			}
			// 开始刷新进度
			startHandlerTime();

			// 开始播放，隐藏loading
			if (loadLayout != null)
				loadLayout.finish();

			if (mHalfController != null) {
				mHalfController.star();
			}

			if (mFullController != null) {
				mFullController.star();
			}
			// LetvUtil.ireTrackerEventStart(getActivity(), album, video,
			// realUrl, filePath);
			canToPip = true;
			if (viewPagerAdapter != null) {
				viewPagerAdapter.notifyDataSetChanged();
				if (videosCallBack != null)
					videosCallBack.notify(videosCallBackState);// 刷新半屏的视频列表
				getCommentsCallBackState = PlayAlbumControllerCallBack.STATE_FINISH;
				if (getCommentsCallBack != null)
					getCommentsCallBack.notify(getCommentsCallBackState);
			}
		} else if (mCurrentState == VideoView.STATE_PAUSED) {
			{// 统计播放时长
				long ct = System.currentTimeMillis();
				if (startTime != 0) {
					statisticsVideoInfo.setPlayedTime(statisticsVideoInfo
							.getPlayedTime() + (ct - startTime));
				}
				// 艾瑞检测，视频启动
				try {
					IRVideo.getInstance(getActivity()).videoPause();
				} catch (Exception e) {
					// Log.e("gongmeng", "vvTracker Video pause error");
				}
			}

			// 暂停播放，隐藏loading
			loadLayout.finish();
			// 停止刷新进度
			stopHandlerTime();

			if (mHalfController != null)
				mHalfController.pause();

			if (mFullController != null) {
				mFullController.pause();
			}

			if (playRecord != null) {
				LetvPlayRecordFunction.submitPlayTraces(getActivity(),
						playRecord.getChannelId(), playRecord.getAlbumId(),
						playRecord.getVideoId(), playRecord.getVideoNextId(),
						playRecord.getType(), playRecord.getTotalDuration(),
						playRecord.getPlayedDuration(), playRecord.getTitle(),
						playRecord.getImg(), playRecord.getCurEpsoid());
			}
			// LetvUtil.ireTrackerEventEnd(getActivity(), realUrl, filePath);
		} else if (mCurrentState == VideoView.STATE_ERROR) {

			errorCodeJoint = LetvUtil.intJoint(errorCodeExtra, errorCodeWhat);

			updatePlayDataStatistics(
					DataConstant.StaticticsVersion2Constatnt.PlayerAction.PLAY_ACTION,
					-1);
			statisticsVideoInfo.setStatus("4");

			if (playAdFragment != null) {
				playAdFragment.closePauseAd();
			}

			if (loadLayout != null) {
				if (isLocalFile && !TextUtils.isEmpty(filePath)) {
					loadLayout.localError();
				} else {
					if (!TextUtils.isEmpty(realUrl)) {
						loadLayout.requestError();
					}
				}
			}

			playCallBackState = 7;
			statisticsVideoInfo.setErr("3");
			stopHandlerTime();

			if (getActivity() != null
					&& getActivity().getPlayFragment() != null) {
				getActivity().getPlayFragment().pause();
				getActivity().getPlayFragment().stopPlayback();
			}

			// 艾瑞检测，视频结束
			try {
				IRVideo.getInstance(getActivity()).videoEnd();
			} catch (Exception e) {
				// Log.e("gongmeng", "vvTracker Video end error");
			}
		} else if (mCurrentState == VideoView.STATE_IDLE) {

			if (playAdFragment != null) {
				playAdFragment.closePauseAd();
			}

			if (playCallBackState == 0) {
				if (loadLayout != null)
					loadLayout.loading();
			}

			stopHandlerTime();

			if (mHalfController != null)
				mHalfController.Inoperable();

			if (mFullController != null) {
				mFullController.Inoperable();
			}
		} else if (mCurrentState == VideoView.STATE_PLAYBACK_COMPLETED) {
			stopHandlerTime();
			if (playRecord != null) {
				playRecord.setPlayedDuration(-1);
			}
			// LetvUtil.ireTrackerEventEnd(getActivity(), realUrl, filePath);
			playNext();
		} else if (mCurrentState == VideoView.STATE_STOPBACK) {// 调起stopback时回调
			handler.removeMessages(HANDLER_TIME);
			handler.removeMessages(UPDATE_STATICICS_TIME);
			if (TextUtils.isEmpty(statisticsVideoInfo.getStatus())) {// 如果没有状态，就代表正常完成
				statisticsVideoInfo.setStatus("3");
			}

			{// 统计播放时长
				long ct = System.currentTimeMillis();
				if (startTime != 0) {
					statisticsVideoInfo.setPlayedTime((statisticsVideoInfo
							.getPlayedTime() + (ct - startTime)) / 1000);
				}
				try {
					IRVideo.getInstance(getActivity()).videoEnd();
				} catch (Exception e) {
					// Log.e("gongmeng", "vvTracker Video end error");
				}
			}
			statisticsVideoInfo.setPid(aid + "");
			statisticsVideoInfo.setVid(vid + "");
			statisticsVideoInfo.setFrom("1");
			statisticsVideoInfo.setPtype("1");
			statisticsVideoInfo.setTerminaltype("phone");
			statisticsVideoInfo.setUid(LetvUtil.getUID());
			statisticsVideoInfo.setPcode(LetvUtil.getPcode());
			statisticsVideoInfo.setPtid(LetvUtil.getUUID(getActivity()));
			if (video != null) {
				statisticsVideoInfo.setCid(video.getCid() + "");
				statisticsVideoInfo.setMmsid(video.getMid());
				statisticsVideoInfo.setVlen(totleTime / 1000 + "");
			}
			stopHandlerTime();

			LogInfo.log("onChange", statisticsVideoInfo.toString());
			updatePlayDataStatistics(
					DataConstant.StaticticsVersion2Constatnt.PlayerAction.TIME_ACTION,
					(timeElapsed - lastTimeElapsed));
			updatePlayDataStatistics(
					DataConstant.StaticticsVersion2Constatnt.PlayerAction.END_ACTION,
					-1);
			resetTimeCount();
			// DataStatistics.getInstance().sendVideoInfo(getActivity(),
			// statisticsVideoInfo);
			// LetvUtil.ireTrackerEventEnd(getActivity(), realUrl, filePath);
		} else if (mCurrentState == VideoView.STATE_ENFORCEMENT) {
			if (playAdFragment != null) {
				playAdFragment.closePauseAd();
			}
			if (getActivity().getPlayFragment().isEnforcementPause()) {
				{// 统计播放时长
					long ct = System.currentTimeMillis();
					if (startTime != 0) {
						statisticsVideoInfo.setPlayedTime(statisticsVideoInfo
								.getPlayedTime() + (ct - startTime));
					}

					try {
						IRVideo.getInstance(getActivity()).videoPause();
					} catch (Exception e) {
						// Log.e("gongmeng", "vvTracker Video pause error");
					}

				}
				// 暂停播放，隐藏loading
				loadLayout.finish();
				// 停止刷新进度
				stopHandlerTime();

				if (mHalfController != null)
					mHalfController.pause();

				if (mFullController != null)
					mFullController.pause();
				// LetvUtil.ireTrackerEventEnd(getActivity(), realUrl,
				// filePath);
			}
		}
	}

	/**
	 * 开始请求数据
	 * */
	private void startLoadingData() {
		requestStartTime = System.currentTimeMillis();// 请求时间开始
		if (getLaunchMode() == PlayController.PLAY_ALBUM) {
			new RequestAlbum(getActivity()).start();
		} else if (getLaunchMode() == PlayController.PLAY_VIDEO) {
			new checkPlayRecordTask(getActivity(), false, curPage, aid, vid)
					.start();
		} else {
			if (!TextUtils.isEmpty(realUrl)) {// 直接给播放地址的播放
				getActivity().getPlayFragment().playLocal(realUrl,
						(int) seek * 1000);
				/**** add by zlb for pip *****/
				localPath = realUrl;
				localSeek = seek;
				/********* end ***************/
			}
		}
	}

	@Override
	public boolean getVideoList(int page) {
		if (page != curPage) {
			if (videos.get(page) != null) {// 判断将要跳去的页面数据是否已经存在
				curPage = page;// 存在直接翻页
				return true;
			} else {
				new RequestVideoList(getActivity(), false, page, aid, 0)
						.start();
				Log.d("newsPage", "page = " + page);
			}
		}
		return false;
	}

	/**
	 * 初始化播放记录，在没有播放记录的情况下
	 * */
	public void createPlayRecord() {
		if (playRecord == null) {
			playRecord = new PlayRecord();

			playRecord.setAlbumId((int) aid);
			playRecord.setVideoId((int) vid);
			if (album != null) {
				playRecord.setVideoType(album.getType());
			} else if (video != null) {
				playRecord.setVideoType(video.getType());
			}
			playRecord.setTitle(video.getNameCn());
			playRecord.setChannelId(video.getCid());
			playRecord.setImg(video.getPic());
			playRecord.setFrom(2);
			playRecord.setCurEpsoid(video.getEpisode());
			if (seek > 0) {
				playRecord.setPlayedDuration(seek / 1000);
			} else {
				playRecord.setPlayedDuration(0);
			}
			playRecord.setTotalDuration(video.getDuration());
			curTime = playRecord.getPlayedDuration() * 1000;
			totleTime = playRecord.getTotalDuration() * 1000;
			playRecord.setUpdateTime(System.currentTimeMillis());
		}
	}

	/**
	 * 同意处理，跳过片头，和提示跳过片头
	 * */
	private void startPlayNet() {
		switch (NetWorkTypeUtils.getNetType()) {
		case NetWorkTypeUtils.NETTYPE_2G:
		case NetWorkTypeUtils.NETTYPE_3G:
			/**
			 * 一次进入，只提示一次
			 * */
			if (!alreadyPrompt) {
				alreadyPrompt = true;
				if (video != null && playAdFragment != null) {
					playAdFragment.setPauseAd(true);
					playAdFragment.setADPause(true);
					getActivity().getPlayFragment().setEnforcementPause(true);
				}
			}
			break;
		}
		statisticsVideoInfo.setPid(LetvUtil.getUUID(getActivity()));
		statisticsVideoInfo.setCode(isHd ? "800" : "350");
		long ct = System.currentTimeMillis();// 请求结算时间
		statisticsVideoInfo.setUtime(((int) (ct - requestStartTime) / 1000));

		// if (video != null && video.getDuration() < 180) {
		// playRecord.setPlayedDuration(0);
		// }

		if (isSikp && bTime > 0 && playRecord.getPlayedDuration() <= 0) {
			playRecord.setPlayedDuration(bTime);
		}
		isShowSkipEnd = true;

		resetTimeCount();
		getActivity().getPlayFragment().playNet(realUrl, false, isDolby,
				(int) playRecord.getPlayedDuration() * 1000);
		if (ispostplay) {

			updatePlayDataStatistics(
					DataConstant.StaticticsVersion2Constatnt.PlayerAction.PLAY_ACTION,
					-1);
		}

	}

	/**
	 * 播放 只针对本专辑下的视频
	 * */
	public void play(Video video) {
		try {
			if (video.getId() != vid || loadLayout.getErrState() != 0) {
				loadLayout.loading();
				canplay = false;
				statisticsVideoInfo.setStatus("2");// 手动结束
				destroyTasks();
				getActivity().getPlayFragment().pause();
				getActivity().getPlayFragment().stopPlayback();

				if (playAdFragment != null) {
					playAdFragment.setPauseAd(false);
					playAdFragment.pause();
					playAdFragment.stopPlayback();
				}

				this.vid = video.getId();
				this.setVideo(video);
				isLocalFile = false;
				filePath = null;
				videosCallBackState = PlayAlbumControllerCallBack.STATE_FINISH;
				if (videosCallBack != null)
					videosCallBack.notify(videosCallBackState);// 刷新半屏的视频列表

				// if (mFullController != null)
				// mFullController.initVideos();// 刷新全屏的视频列表
				playRecord = null;

				startLoadingData();
				createPlayRecord();

				requestStartTime = System.currentTimeMillis();// 请求开始时间
				new RequestVideoFile(getActivity()).start();

			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	/**
	 * 播放 针对清晰度切换，VideoFile接口失败后重试
	 * */
	public void play() {
		loadLayout.loading();
		resetTimeCount();
		canplay = false;
		statisticsVideoInfo.setStatus("2");// 手动结束

		isLocalFile = false;
		filePath = null;
		destroyTasks();
		getActivity().getPlayFragment().pause();
		getActivity().getPlayFragment().stopPlayback();
		if (playAdFragment != null) {
			playAdFragment.setPauseAd(false);
			playAdFragment.pause();
			playAdFragment.stopPlayback();
		}
		startLoadingData();
		createPlayRecord();

		requestStartTime = System.currentTimeMillis();// 请求开始时间
		new RequestVideoFile(getActivity()).start();
	}

	/**
	 * 播放 播放下一集
	 * */
	public void playNext() {
		loadLayout.loading();
		isPlayedAd = false;
		Video v = video;
		if (videos != null && videos.size() > 0) {
			isLocalFile = false;
			filePath = null;
			Iterator<Integer> ir = videos.keySet().iterator();
			int pos = -1;
			int page = -1;
			while (ir.hasNext()) {
				page = ir.next();
				VideoList list = videos.get(page);
				if (list != null && list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						if (v.getId() == list.get(i).getId()) {
							pos = i;
							break;
						}
					}

					if (pos != -1 && page != -1) {
						break;
					}
				}
			}

			if (pos == -1 || page == -1) {
				getActivity().finish();
				return;
			} else {
				VideoList videoList = videos.get(page);

				if (pos < videoList.size() - 1) {
					play(videoList.get(pos + 1));
					return;
				} else {
					if (pageSize * (page - 1) + pos + 1 < totle) {
						if (pos < videoList.size()) {
							if (pos + 1 == videoList.size()) {// 本页最后一条数据
								// videosCallBack.setCurPage(curPage + 1);
								// if (videos.containsKey(page + 1)) {//
								// 如果已经有下页数据，播放
								// VideoList list = videos.get(page + 1);
								// if (list != null && list.size() > 0) {
								// play(list.get(0));
								// curPage = curPage + 1;
								// videosCallBack.notify(0);
								// return;
								// } else {
								// this.curPage = page + 1;
								// destroyTasks();
								// getActivity().getPlayFragment().pause();
								// getActivity().getPlayFragment().stopPlayback();
								//
								// playRecord = null;
								// vid = 0;
								//
								// requestStartTime =
								// System.currentTimeMillis();//
								// 请求开始时间
								// new RequestVideoList(getActivity(), true,
								// curPage, aid, 0).start();
								// videosCallBack.setCurPage(curPage);
								// return;
								// }
								// } else {// 如果没有下页数据，请求再播放
								// this.curPage = page + 1;
								// destroyTasks();
								// getActivity().getPlayFragment().pause();
								// getActivity().getPlayFragment().stopPlayback();
								//
								// playRecord = null;
								// vid = 0;
								//
								// requestStartTime =
								// System.currentTimeMillis();//
								// 请求开始时间
								// new RequestVideoList(getActivity(), true,
								// curPage, aid, 0).start();
								// videosCallBack.setCurPage(curPage);
								// return;
								// }

								getActivity().finish();
							} else {// 本也直接去下集播放
								play(videoList.get(pos + 1));
								// getActivity().finish();
								return;
							}
						} else {
							getActivity().finish();
						}
					} else {// 最后一条视频，关闭
						getActivity().finish();
					}
				}
			}

		} else {// 没有视频列表，关闭
			getActivity().finish();
		}
	}

	/**
	 * 得到前帖广告
	 * */
	private void getFrontAd() {
		if (video != null
				&& playAdFragment != null
				&& PreferencesManager.getInstance().getUserId()
						.equalsIgnoreCase("")) {
			com.letv.ads.util.LogInfo.log("ads", " vid=" + vid);
			playAdFragment.getDemandFrontAd(video.getCid(), aid, vid, video
					.getMid(), uuidTimp, PreferencesManager.getInstance()
					.getUserId(), video.getDuration() + "", "", "0",
					isSupportM3U8(), video.needPay(), true);
			getActivity().getPlayFragment().setEnforcementWait(true);
		}
	}

	/**
	 * 正常流程得到前贴广告
	 */
	private void getFrontAdNormal() {
		Log.d("ads", "getFrontAdNormal");
		if (null != video && video.needPay()) {// 付费视频跳过广告
			return;
		}
		if (!needPlayAd
				|| !PreferencesManager.getInstance().getUserId()
						.equalsIgnoreCase("")) {
			return;
		}
		boolean flag = false;
		switch (NetWorkTypeUtils.getNetType()) {
		case NetWorkTypeUtils.NETTYPE_2G:
		case NetWorkTypeUtils.NETTYPE_3G:
			flag = true;
			boolean isDownLoad = false;
			if (!isDownLoad && !isLocalFile) {
				if ((!alreadyPrompt)) {
					if (video != null && playAdFragment != null) {
						playAdFragment.setPauseAd(true);
						playAdFragment.setADPause(true);
						getPauseAd();
					}
					alreadyPrompt = true;
					if (video != null && playAdFragment != null) {
						adConsumeTime = System.currentTimeMillis();
						playAdFragment.getDemandFrontAd(video.getCid(), aid,
								vid, video.getMid(), uuidTimp,
								PreferencesManager.getInstance().getUserId(),
								video.getDuration() + "", "", "0",
								isSupportM3U8(), video.needPay(), true);
						getActivity().getPlayFragment().setEnforcementPause(
								true);
					}
					getActivity().getPlayFragment().setEnforcementWait(true);
					// setMobileNetBg(true);
					loadLayout.finish();
					// UIs.showToast(R.string.play_2g3gnet_tag);
				} else {
					// UIs.showToast(R.string.play_net_tag);
					if (video != null && playAdFragment != null) {
						adConsumeTime = System.currentTimeMillis();
						playAdFragment.getDemandFrontAd(video.getCid(), aid,
								vid, video.getMid(), uuidTimp,
								PreferencesManager.getInstance().getUserId(),
								video.getDuration() + "", "", "0",
								isSupportM3U8(), video.needPay(), true);
						// getActivity().getPlayFragment().setEnforcementPause(true);
						getActivity().getPlayFragment()
								.setEnforcementWait(true);
					}
				}
			} else {
				flag = false;
			}
			break;
		}
		if (!flag) {
			Log.d("ads", "playAdFragment=" + playAdFragment);
			if (video != null && playAdFragment != null) {
				adConsumeTime = System.currentTimeMillis();
				playAdFragment.getDemandFrontAd(video.getCid(), aid, vid, video
						.getMid(), uuidTimp, PreferencesManager.getInstance()
						.getUserId(), video.getDuration() + "", "", "0",
						isSupportM3U8(), video.needPay(), true);
				getActivity().getPlayFragment().setEnforcementWait(true);
			}
		}
	}

	/**
	 * 是否支持软解播放
	 * 
	 * @return
	 */
	private boolean isSupportM3U8() {
		return false;
		// String vf = LetvApplication.getInstance().getVideoFormat();
		// return "ios".equals(vf);
	}

	/**
	 * 前帖广告完成的回调
	 * */
	@Override
	public void onFinish(boolean isFinishByHand, boolean hasAds) {
		handler.removeMessages(UPDATE_STATICICS_TIME);
		handler.sendEmptyMessage(UPDATE_STATICICS_TIME);
		if (!TextUtils.isEmpty(realUrl)) {
			updatePlayDataStatistics(
					DataConstant.StaticticsVersion2Constatnt.PlayerAction.PLAY_ACTION,
					-1);
		} else {
			ispostplay = true;
		}
		if (video != null && playAdFragment != null && hasAds) {
			playAdFragment.setPauseAd(false);
			mIVideoStatusInformer = playAdFragment.getIVideoStatusInformer();
		}
		if (getActivity().getPlayFragment().isEnforcementPause()
				&& playAdFragment != null && playAdFragment.isHaveFrontAds()) {
			getActivity().getPlayFragment().setEnforcementPause(false);
		}

		getActivity().getPlayFragment().start();

		getActivity().getPlayFragment().setEnforcementWait(false);
		adConsumeTime = System.currentTimeMillis() - adConsumeTime;
		if (getRealUrlConsumeTime < 30 * 1000 && adConsumeTime < 120 * 1000) {// 上报加载时长
			// (加载广告和请求调度地址是异步调用)
			staticticsLoadTimeInfo(getActivity());
		}
		if (isFinishByHand) {

		} else {
			if (playCallBackState == 7) {// 某些设备上，播放完广告后，无法播放视频
				if (TextUtils.isEmpty(realUrl)) {
					new RequestVideoFile(getActivity()).start();
				} else {
					startPlayNet();
				}
			} else {
				getActivity().getPlayFragment().start();
			}
		}

		isPlayedAdFinish = true;
	}

	/**
	 * 各接口缓冲时间统计
	 * 
	 * @param mContext
	 */
	private void staticticsLoadTimeInfo(Context mContext) {
		try {
			StringBuilder sb = new StringBuilder();
			long sVid = 0;
			long sCid = 0;
			if (video != null) {
				sVid = video.getId();
				sCid = video.getCid();
			}
			int vid = getActivity().getIntent().getIntExtra("vid", 0);
			if (vid > 0) {// 调度播放时，如果有vid，则上报视频详情时长，否则上报 专辑视频详情 lhz
				LogInfo.log("lhz", "videoDetailsConsumeTime:"
						+ videoDetailsConsumeTime);
				albumVListConsumeTime = videoDetailsConsumeTime;
			}
			long adsTotalTime = 0;
			long adsRequestTime = 0;
			long adsLoadingTime = 0;
			if (playAdFragment != null) {
				adsTotalTime = playAdFragment.getAdsVideoTotalTime();
				adsRequestTime = playAdFragment.getAdsLoadingTime();
				adsLoadingTime = playAdFragment.getAdsPlayLoadTime();
			}
			sb.append("type1=" + LetvUtil.getNetType(mContext) + "&");
			sb.append("type2=" + "0" + "&");
			sb.append("type3="
					+ LetvUtil.staticticsLoadTimeInfoFormat(adConsumeTime)
					+ "&");
			sb.append("type4="
					+ LetvUtil
							.staticticsLoadTimeInfoFormat(albumVListConsumeTime)
					+ "&");
			sb.append("type5="
					+ LetvUtil
							.staticticsLoadTimeInfoFormat(videoFileConsumeTime)
					+ "&");
			sb.append("type6="
					+ LetvUtil.staticticsLoadTimeInfoFormat(canPlayConsumeTime)
					+ "&");
			sb.append("type7="
					+ LetvUtil
							.staticticsLoadTimeInfoFormat(albumPayConsumeTime)
					+ "&");
			sb.append("type8="
					+ LetvUtil.staticticsLoadTimeInfoFormat(adJoinConsumeTime)
					+ "&");
			sb.append("type9="
					+ LetvUtil
							.staticticsLoadTimeInfoFormat(getRealUrlConsumeTime)
					+ "&");
			sb.append("type10="
					+ LetvUtil.staticticsLoadTimeInfoFormat(totalConsumeTime)
					+ "&");

			sb.append("type11="
					+ LetvUtil.staticticsLoadTimeInfoFormat(adsRequestTime)
					+ "&");
			sb.append("type12=" + adsTotalTime + "&");
			sb.append("type13="
					+ LetvUtil.staticticsLoadTimeInfoFormat(loadingConsumeTime)
					+ "&");
			sb.append("type14="
					+ LetvUtil.staticticsLoadTimeInfoFormat(adsLoadingTime));

			// DataStatistics.getInstance().sendActionInfo(mContext, "0", "0",
			// LetvUtil.getPcode(), "22", sb.toString(),
			// "0", sCid + "", aid + "", sVid + "", LetvUtil.getUID(), null,
			// null, null, null,
			// PreferencesManager.getInstance().isLogin() ? 0 : 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ArrayList<CommonAdItem> ads;
	private long adTotalDuration;

	/**
	 * 广告拼接
	 */
	@Override
	public void onM3U8(ArrayList<CommonAdItem> ads) {
		if (null != ads && ads.size() >= 0
				&& SimpleAdMediaType.VIDEO == ads.get(0).mediaFileType) {
			this.ads = ads;
			String ahl = "";
			for (int i = 0; i < ads.size(); i++) {
				adTotalDuration += ads.get(i).duration * 1000;
				if (i == ads.size() - 1) {
					ahl += ads.get(i).mediaFileUrl + "&tts=ios";
				} else {
					ahl += ads.get(i).mediaFileUrl + "&tts=ios|";
				}
			}
			String vl = ddUrl;
			String atl = "";
			if (!TextUtils.isEmpty(ahl)) {
				new RequestAdJoining(getActivity(), ahl, vl, atl).start();
			} else {
				mPlayMode = PLAY_MODE.NORMAL;
				getActivity().getPlayFragment().setEnforcementPause(false);
				getActivity().getPlayFragment().setEnforcementWait(false);
				startPlayNet();
			}
		} else {
			mPlayMode = PLAY_MODE.NORMAL;
			startPlayNet();
		}
	}

	/**
	 * 请求 广告拼接
	 * */
	public class RequestAdJoining extends LetvHttpAsyncTask<AdJoiningBean> {

		private String ahl;
		private String vl;
		private String atl;

		public RequestAdJoining(Context context, String ahl, String vl,
				String atl) {
			super(context);
			this.ahl = ahl;
			this.vl = vl;
			this.atl = atl;
		}

		@Override
		public LetvDataHull<AdJoiningBean> doInBackground() {
			adJoinConsumeTime = System.currentTimeMillis();
			return LetvHttpApi.requestAdJoining(0, ahl, vl, atl,
					new AdJoiningParser());
		}

		@Override
		public void onPostExecute(int updateId, AdJoiningBean result) {
			adJoinConsumeTime = System.currentTimeMillis() - adJoinConsumeTime;
			tasks.remove(this);
			if (null != result) {
				boolean ahlSuccess = (TextUtils.isEmpty(ahl) || (!TextUtils
						.isEmpty(ahl) && result.isAhsSuccess())) ? true : false;
				boolean vlSuccess = (TextUtils.isEmpty(vl) || (!TextUtils
						.isEmpty(vl) && result.isVsSuccess())) ? true : false;
				boolean atlSuccess = (TextUtils.isEmpty(atl) || (!TextUtils
						.isEmpty(atl) && result.isAtsSuccess())) ? true : false;
				if (ahlSuccess && vlSuccess && atlSuccess
						&& null != result.getMuri()) {
					realUrl = result.getMuri();
					curTime = 0;// 初始化初始播放时间
					mPlayMode = PLAY_MODE.M3U8;
					getActivity().getPlayFragment().setEnforcementPause(false);
					getActivity().getPlayFragment().setEnforcementWait(false);
					startPlayNet();

					getActivity().getAdLayout().startAd();
					getActivity().getPlayGestrue().setVisibility(View.GONE);
					getActivity().getPlayUpper().setVisibility(View.GONE);
				} else {
					requestAdJoinFailed();
				}
				if (null != playAdFragment) {
					playAdFragment.setAdJoinBean(result.getAhs(),
							result.getVs());
				}
			}
		}

		@Override
		public void netErr(int updateId, String errMsg) {
			adJoinConsumeTime = System.currentTimeMillis() - adJoinConsumeTime;
			tasks.remove(this);
			requestAdJoinFailed();
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			adJoinConsumeTime = System.currentTimeMillis() - adJoinConsumeTime;
			tasks.remove(this);
			requestAdJoinFailed();
		}

		@Override
		public void netNull() {
			adJoinConsumeTime = System.currentTimeMillis() - adJoinConsumeTime;
			tasks.remove(this);
			requestAdJoinFailed();
		}

		@Override
		public void noUpdate() {
			adJoinConsumeTime = System.currentTimeMillis() - adJoinConsumeTime;
			super.noUpdate();
		}

		private void requestAdJoinFailed() {
			mPlayMode = PLAY_MODE.NORMAL;
			getActivity().getPlayFragment().setEnforcementPause(false);
			getActivity().getPlayFragment().setEnforcementWait(false);
			startPlayNet();
		}

	}

	/**
	 * 检查是否有播放记录
	 * */
	private class checkPlayRecordTask extends LetvSimpleAsyncTask<PlayRecord> {

		private boolean isAlbum;

		private int page = 1;

		private long albumId = 0;

		private long videoId = 0;

		public checkPlayRecordTask(Context context, boolean isAlbum, int page,
				long albumId, long videoId) {
			super(context, false);
			tasks.add(this);
			this.page = page;
			this.albumId = albumId;
			this.videoId = videoId;
			this.isAlbum = isAlbum;
		}

		@Override
		public PlayRecord doInBackground() {
			if (playRecord == null) {// 如果播放记录已经生成，直接跳过
				PlayRecord playRecord = null;

				if (isAlbum) {
					if (videoId > 0) {
						playRecord = LetvPlayRecordFunction.getPoint(0,
								(int) videoId, false);
					} else {
						if (page == 1) {
							playRecord = LetvPlayRecordFunction.getPoint(
									(int) albumId, 0, false);
						}
					}
				} else {
					playRecord = LetvPlayRecordFunction.getPoint(0,
							(int) videoId, false);
				}

				return playRecord;
			}

			return playRecord;
		}

		@Override
		public void onPostExecute(PlayRecord result) {
			tasks.remove(this);
			playRecord = result;
			if (playRecord != null) {
				if (seek > 0) {
					playRecord.setPlayedDuration(seek / 1000);
				}
				vid = playRecord.getVideoId();
				curTime = playRecord.getPlayedDuration() * 1000;
				totleTime = playRecord.getTotalDuration() * 1000;

			}
			if (isAlbum) {

			} else {
				new RequestVideo(context).start();
			}

		}
	}

	/**
	 * 请求专辑详情
	 * */
	private class RequestAlbum extends LetvHttpAsyncTask<AlbumNew> {

		private String markId = null;

		public RequestAlbum(Context context) {
			super(context);
			tasks.add(this);

			introductionCallBackState = PlayAlbumControllerCallBack.STATE_RUNNING;
			if (introductionCallBack != null)
				introductionCallBack.notify(introductionCallBackState);
			getCommentsCallBackState = PlayAlbumControllerCallBack.STATE_RUNNING;
			if (getCommentsCallBack != null)
				getCommentsCallBack.notify(getCommentsCallBackState);
		}

		@Override
		public AlbumNew loadLocalData() {
			try {
				LocalCacheBean bean = LetvCacheDataHandler
						.readDetailData(String.valueOf(aid));
				if (bean != null) {
					AlbumNew album = null;
					AlbumNewParse albumNewParse = new AlbumNewParse();
					album = albumNewParse.initialParse(bean.getCacheData());
					markId = bean.getMarkId();

					return album;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public boolean loadLocalDataComplete(AlbumNew result) {
			if (result != null) {
				setAlbum(result);
				merge = LetvFunction.getMerge(album.getStyle());
				order = LetvFunction.getOrder(album.getCid());
				totle = merge == 0 ? album.getPlatformVideoInfo() : album
						.getPlatformVideoNum();// 合并与不合并总级数取不一样的字段
				isList = LetvFunction.getIsList(album.getStyle());// 初始化，是宫格合适列表
				new checkPlayRecordTask(context, true, curPage, aid, vid)
						.start();

				introductionCallBackState = PlayAlbumControllerCallBack.STATE_FINISH;
				if (introductionCallBack != null)
					introductionCallBack.notify(introductionCallBackState);
				getCommentsCallBackState = PlayAlbumControllerCallBack.STATE_FINISH;
				if (getCommentsCallBack != null)
					getCommentsCallBack.notify(getCommentsCallBackState);

				return true;
			}

			return false;
		}

		@Override
		public LetvDataHull<AlbumNew> doInBackground() {
			if (!isLocalSucceed()) {
				markId = null;
			}
			AlbumNewParse parser = new AlbumNewParse();
			LetvDataHull<AlbumNew> dataHull = LetvHttpApi
					.requestAlbumVideoInfo(0, String.valueOf(vid), "video",
							markId, parser);
			if (dataHull.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
				LetvCacheDataHandler.saveDetailData(parser.getMarkId(),
						dataHull.getSourceData(), String.valueOf(aid));
			}

			return dataHull;
		}

		@Override
		public void onPostExecute(int updateId, AlbumNew result) {
			tasks.remove(this);
			setAlbum(result);
			merge = LetvFunction.getMerge(album.getStyle());
			order = LetvFunction.getOrder(album.getCid());
			if (isSingle) {
				aid = 0;
			} else {
				aid = album.getPid();
			}
			totle = merge == 0 ? album.getPlatformVideoInfo() : album
					.getPlatformVideoNum();// 合并与不合并总级数取不一样的字段
			isList = LetvFunction.getIsList(album.getStyle());// 初始化，是宫格合适列表
			if (!isLocalSucceed()) {
				new checkPlayRecordTask(context, true, curPage, aid, vid)
						.start();
				getCommentsCallBackState = PlayAlbumControllerCallBack.STATE_FINISH;
				if (getCommentsCallBack != null)
					getCommentsCallBack.notify(getCommentsCallBackState);
				introductionCallBackState = PlayAlbumControllerCallBack.STATE_FINISH;
				if (introductionCallBack != null)
					introductionCallBack.notify(introductionCallBackState);
			}
			if (aid != 0) {
				new RequestVideoList(context, true, 1, aid, vid).start();
			} else {
				new RequestVideo(context).start();
			}
		}

		@Override
		public void netNull() {
			tasks.remove(this);
			if (isLocalFile) {
				return;
			}
			introductionCallBackState = PlayAlbumControllerCallBack.STATE_NET_NULL;
			if (introductionCallBack != null)
				introductionCallBack.notify(introductionCallBackState);
			getCommentsCallBackState = PlayAlbumControllerCallBack.STATE_NET_NULL;
			if (getCommentsCallBack != null)
				getCommentsCallBack.notify(getCommentsCallBackState);
			videosCallBackState = PlayAlbumControllerCallBack.STATE_NET_NULL;
			if (videosCallBack != null)
				videosCallBack.notify(videosCallBackState);
			loadLayout.requestError();
			playCallBackState = 1;
			statisticsVideoInfo.setErr("2");
		}

		@Override
		public void netErr(int updateId, String errMsg) {
			tasks.remove(this);
			if (isLocalFile) {
				return;
			}
			introductionCallBackState = PlayAlbumControllerCallBack.STATE_NET_ERR;
			if (introductionCallBack != null)
				introductionCallBack.notify(introductionCallBackState);
			getCommentsCallBackState = PlayAlbumControllerCallBack.STATE_NET_ERR;
			if (getCommentsCallBack != null)
				getCommentsCallBack.notify(getCommentsCallBackState);
			videosCallBackState = PlayAlbumControllerCallBack.STATE_NET_ERR;
			if (videosCallBack != null)
				videosCallBack.notify(videosCallBackState);
			loadLayout.requestError();
			playCallBackState = 1;
			statisticsVideoInfo.setErr("2");
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			tasks.remove(this);
			if (isLocalFile) {
				return;
			}
			introductionCallBackState = PlayAlbumControllerCallBack.STATE_DATA_NULL;
			if (introductionCallBack != null)
				introductionCallBack.notify(introductionCallBackState);
			getCommentsCallBackState = PlayAlbumControllerCallBack.STATE_DATA_NULL;
			if (getCommentsCallBack != null)
				getCommentsCallBack.notify(getCommentsCallBackState);
			videosCallBackState = PlayAlbumControllerCallBack.STATE_DATA_NULL;
			if (videosCallBack != null)
				videosCallBack.notify(videosCallBackState);
			loadLayout.requestError();
			playCallBackState = 1;
			statisticsVideoInfo.setErr("2");
		}

		@Override
		public void noUpdate() {
			tasks.remove(this);
			super.noUpdate();
		}
	}

	/**
	 * 请求视频详情
	 * */
	private class RequestVideo extends LetvHttpAsyncTask<Video> {

		private String markId;

		public RequestVideo(Context context) {
			super(context);
			tasks.add(this);
		}

		@Override
		public Video loadLocalData() {
			try {
				LocalCacheBean bean = LetvCacheDataHandler
						.readDetailData(String.valueOf(vid));
				if (bean != null) {
					Video video = null;
					VideoParser videoParser = new VideoParser();
					video = videoParser.initialParse(bean.getCacheData());
					markId = bean.getMarkId();

					return video;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public boolean loadLocalDataComplete(Video result) {
			if (result != null) {
				setVideo(result);
				vid = video.getId();

				createPlayRecord();

				if (playRecord != null) {
					playRecord.setTotalDuration(video.getDuration());
					totleTime = playRecord.getTotalDuration() * 1000;
				}

				new RequestVideoFile(context).start();

				return true;
			}

			return false;
		}

		@Override
		public LetvDataHull<Video> doInBackground() {
			if (!isLocalSucceed()) {
				markId = null;
			}
			VideoParser parser = new VideoParser();
			LetvDataHull<Video> dataHull = LetvHttpApi.requestAlbumVideoInfo(0,
					String.valueOf(vid), "video", markId, parser);
			if (dataHull.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
				LetvCacheDataHandler.saveDetailData(parser.getMarkId(),
						dataHull.getSourceData(), String.valueOf(vid));
			}
			return dataHull;
		}

		@Override
		public void onPostExecute(int updateId, Video result) {
			tasks.remove(this);
			setVideo(result);
			vid = video.getId();

			if (!isLocalSucceed()) {
				createPlayRecord();
				if (playRecord != null) {
					playRecord.setTotalDuration(video.getDuration());
					totleTime = playRecord.getTotalDuration() * 1000;
				}
				new RequestVideoFile(context).start();
			}
		}

		@Override
		public void netNull() {
			tasks.remove(this);
			if (isLocalFile) {
				return;
			}
			loadLayout.requestError();
			playCallBackState = 2;
			statisticsVideoInfo.setErr("2");
		}

		@Override
		public void netErr(int updateId, String errMsg) {
			tasks.remove(this);
			if (isLocalFile) {
				return;
			}
			loadLayout.requestError();
			playCallBackState = 2;
			statisticsVideoInfo.setErr("2");
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			tasks.remove(this);
			if (isLocalFile) {
				return;
			}
			loadLayout.requestError();
			playCallBackState = 2;
			statisticsVideoInfo.setErr("2");
		}

		@Override
		public void noUpdate() {
			tasks.remove(this);
			super.noUpdate();
		}
	}

	/**
	 * 请求视频列表（针对专辑类型）
	 * */
	private class RequestVideoList extends LetvHttpAsyncTask<VideoList> {

		private boolean isPlay;

		private int page;

		private String markId;

		private long videoId;

		private long albumId;

		private int localDataPos;

		public RequestVideoList(Context context, boolean isPlay, int page,
				long aid, long vid) {
			super(context);
			tasks.add(this);
			this.isPlay = isPlay;
			this.page = 1;
			this.videoId = vid;
			this.albumId = aid;

			videosCallBackState = PlayAlbumControllerCallBack.STATE_RUNNING;
			if (videosCallBack != null)
				videosCallBack.notify(videosCallBackState);
		}

		@Override
		public VideoList loadLocalData() {
			try {
				LocalCacheBean bean = null;
				if (videoId > 0) {
					List<LocalCacheBean> beans = LetvCacheDataHandler
							.readDetailVLData(String.valueOf(albumId));
					if (beans != null && beans.size() > 0) {
						for (LocalCacheBean b : beans) {
							if (b.getCacheData() != null
									&& b.getCacheData().contains(
											String.valueOf(videoId))) {
								bean = b;
								break;
							}
						}
					}
				} else {
					bean = LetvCacheDataHandler.readDetailVLData(
							String.valueOf(albumId), String.valueOf(page),
							String.valueOf(pageSize), order,
							String.valueOf(merge));
				}

				VideoList videoList = null;
				if (bean != null) {
					VideoListParser listParser = new VideoListParser();
					videoList = listParser.initialParse(bean.getCacheData());
					if (videoList != null && videoList.size() > 0) {
						if (videoId > 0) {
							for (int i = 0; i < videoList.size(); i++) {
								if (videoId == videoList.get(i).getId()) {
									localDataPos = i;
								}
							}
						}
						markId = bean.getMarkId();
						return videoList;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			markId = null;
			return null;
		}

		@Override
		public boolean loadLocalDataComplete(VideoList result) {
			if (result != null) {
				int p = result.getPagenum();
				if (isPlay) {
					System.out.println("p = =" + p);
					System.out.println("vid = =" + vid);
					if (p <= 0 && vid <= 0) {
						curPage = page;
						setVideo(result.get(0));
						vid = video.getId();
					} else {
						curPage = p;
						setVideo(result.get(localDataPos));
						vid = video.getId();
					}

					createPlayRecord();

					if (playRecord != null) {
						playRecord.setTotalDuration(video.getDuration());
						totleTime = playRecord.getTotalDuration() * 1000;
					}

					new RequestVideoFile(context).start();
				} else {
					if (p <= 0) {
						curPage = page;
					} else {
						curPage = p;
					}
				}
				videos.put(curPage, result);

				videosCallBackState = PlayAlbumControllerCallBack.STATE_FINISH;
				if (videosCallBack != null)
					videosCallBack.notify(videosCallBackState);
				// if (mFullController != null)
				// mFullController.initVideos();

				return true;
			}

			return false;
		}

		@Override
		public LetvDataHull<VideoList> doInBackground() {

			VideoListParser parser = new VideoListParser();
			LetvDataHull<VideoList> dataHull = LetvHttpApi
					.requestAlbumVideoList(0, String.valueOf(albumId),
							String.valueOf(videoId), String.valueOf(page),
							String.valueOf(pageSize), order,
							String.valueOf(merge), markId, parser);
			Log.d("newsPage", "page&pageSize = " + page + " " + pageSize);

			if (dataHull.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
				if (dataHull.getDataEntity() == null) {
					return null;
				}
				int p = dataHull.getDataEntity().getPagenum();
				if (p <= 0) {
					p = page;
				}

				try {
					String dataString = dataHull.getSourceData();
					JSONObject jsonObject = new JSONObject(dataString);
					JSONObject bodyObject = jsonObject.optJSONObject("body");
					if (bodyObject != null && !bodyObject.has("pagenum")) {
						bodyObject.put("pagenum", p);
					}
					LetvCacheDataHandler.saveDetailVLData(markId,
							jsonObject.toString(), String.valueOf(albumId),
							String.valueOf(p), String.valueOf(pageSize), order,
							String.valueOf(merge));
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			return dataHull;
		}

		@Override
		public void onPostExecute(int updateId, VideoList result) {
			tasks.remove(this);
			int p = result.getPagenum();
			if (isPlay && !isLocalSucceed()) {

				if (p <= 0) {
					curPage = page;
					setVideo(result.get(0));
					vid = video.getId();
				} else {
					curPage = p;
					setVideo(result.get(result.getVideoPosition() - 1));
					vid = video.getId();
				}
				if (videoId > 0) {
					if (result != null && result.size() > 0) {
						if (videoId > 0) {
							for (int i = 0; i < result.size(); i++) {
								if (videoId == result.get(i).getId()) {
									localDataPos = i;
									break;
								}
							}
						}
					}
					curPage = p;
					setVideo(result.get(localDataPos));
					vid = video.getId();
				}

				createPlayRecord();
				if (playRecord != null) {
					playRecord.setTotalDuration(video.getDuration());
					totleTime = playRecord.getTotalDuration() * 1000;
				}
				new RequestVideoFile(context).start();
			} else {
				if (p <= 0) {
					curPage = page;
				} else {
					curPage = p;
				}
			}
			videos.put(curPage, result);

			videosCallBackState = PlayAlbumControllerCallBack.STATE_FINISH;
			if (videosCallBack != null)
				videosCallBack.notify(videosCallBackState);
			// if (mFullController != null)
			// mFullController.initVideos();
		}

		@Override
		public void netNull() {
			tasks.remove(this);
			if (isLocalFile) {
				return;
			}
			videosCallBackState = PlayAlbumControllerCallBack.STATE_NET_NULL;
			if (videosCallBack != null)
				videosCallBack.notify(videosCallBackState);
			if (isPlay) {
				loadLayout.requestError();
				playCallBackState = 3;
				statisticsVideoInfo.setErr("2");
			}
		}

		@Override
		public void netErr(int updateId, String errMsg) {
			tasks.remove(this);
			if (isLocalFile) {
				return;
			}
			videosCallBackState = PlayAlbumControllerCallBack.STATE_NET_ERR;
			if (videosCallBack != null)
				videosCallBack.notify(videosCallBackState);
			if (isPlay) {
				loadLayout.requestError();
				playCallBackState = 3;
				statisticsVideoInfo.setErr("2");
			}
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			tasks.remove(this);
			if (isLocalFile) {
				return;
			}
			videosCallBackState = PlayAlbumControllerCallBack.STATE_DATA_NULL;
			if (videosCallBack != null)
				videosCallBack.notify(videosCallBackState);
			if (isPlay) {
				loadLayout.requestError();
				playCallBackState = 3;
				statisticsVideoInfo.setErr("2");
			}
		}

		@Override
		public void noUpdate() {
			tasks.remove(this);
			super.noUpdate();
		}
	}

	/**
	 * 请求播放视频的调度地址
	 * */
	private class RequestVideoFile extends LetvHttpAsyncTask<VideoFile> {

		String mid;

		boolean needCheckCanPlay = true;

		public RequestVideoFile(Context context) {
			super(context);
			tasks.add(this);
			mid = video.getMid();
			this.needCheckCanPlay = true;
		}

		public RequestVideoFile(Context context, boolean needCheckCanPlay) {
			super(context);
			mid = video.getMid();
			this.needCheckCanPlay = needCheckCanPlay;
		}

		@Override
		public boolean onPreExecute() {
			if (isLocalFile && !TextUtils.isEmpty(filePath)) {
				tasks.remove(this);
				return false;
			}
			if (introductionCallBack != null) {
				introductionCallBack.requestDetails(video.getCid(), vid + "");
			}
			if (LetvTools.checkIp(video.getControlAreas(),
					video.getDisableType())) {
				if (video.canPlay()) {
					if (video.needJump()) {
						loadLayout.jumpError();
						tasks.remove(this);
						canToPip = false;
						return false;
					} else {
						if (needCheckCanPlay && video.needPay()
								&& album != null) {
							if (PreferencesManager.getInstance().isLogin()) {
								// new RequestCanplay(context).start();
								tasks.remove(this);
								return false;
							} else {
								loadLayout.vipNotLoginError();
								tasks.remove(this);
								canToPip = false;
								return false;
							}
						} else {
							canplay = true;

						}
					}
				} else {
					loadLayout.notPlay();
					tasks.remove(this);
					canToPip = false;
					return false;
				}
			} else {
				loadLayout.ipError();
				tasks.remove(this);
				canToPip = false;
				return false;
			}

			return true;
		}

		@Override
		public LetvDataHull<VideoFile> doInBackground() {
			if (isLocalSucceed()) {
				return null;
			}
			if (mid.equals(video.getMid())) {
				LetvDataHull<VideoFile> dataHull = null;
				String tm = String.valueOf(TimestampBean.getTm()
						.getCurServerTime());
				String key = LetvTools.generateVideoFileKey(mid, tm);
				if (isDolby) {
					dataHull = LetvHttpApi.requestVideoFile(0, mid, "0", "no",
							tm, key, new VideoFileParser(video.getPay() == 2));
				} else {
					dataHull = LetvHttpApi.requestVideoFile(0, mid, "0",
							LetvApplication.getInstance().getVideoFormat(), tm,
							key, new VideoFileParser(video.getPay() == 2));
				}

				if (dataHull != null && dataHull.getErrMsg() == 5) {
					LetvDataHull<TimestampBean> dh = LetvHttpApi.getTimestamp(
							0, new TimestampParser());
					if (dh != null
							&& dh.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
						if (mid.equals(video.getMid())) {
							tm = String.valueOf(TimestampBean.getTm()
									.getCurServerTime());
							key = LetvTools.generateVideoFileKey(mid, tm);
							if (isDolby) {
								dataHull = LetvHttpApi
										.requestVideoFile(0, mid, "0", "no",
												tm, key, new VideoFileParser(
														video.getPay() == 2));
							} else {
								dataHull = LetvHttpApi
										.requestVideoFile(
												0,
												mid,
												"0",
												LetvApplication.getInstance()
														.getVideoFormat(),
												tm,
												key,
												new VideoFileParser(video
														.getPay() == 2));
							}
						}

					}
				}

				return dataHull;
			}

			return null;
		}

		@Override
		public void onPostExecute(int updateId, VideoFile result) {
			// updatePlayDataStatistics(DataConstant.StaticticsVersion2Constatnt.PlayerAction.GSLB_ACTION,
			// -1);
			tasks.remove(this);
			if (mid.equals(video.getMid())) {
				new RequestRealPlayUrl(context, result, mid).start();
			}
		}

		@Override
		public void netNull() {
			tasks.remove(this);
			if (mid.equals(video.getMid())) {
				loadLayout.requestError();
				playCallBackState = 5;
				statisticsVideoInfo.setErr("1");
			}
		}

		@Override
		public void netErr(int updateId, String errMsg) {
			tasks.remove(this);
			if (mid.equals(video.getMid())) {
				loadLayout.requestError();
				playCallBackState = 5;
				statisticsVideoInfo.setErr("1");
			}
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			tasks.remove(this);
			if (mid.equals(video.getMid())) {
				loadLayout.requestError();
				playCallBackState = 5;
				statisticsVideoInfo.setErr("1");
			}
		}

		@Override
		public void noUpdate() {
			tasks.remove(this);
			super.noUpdate();
		}
	}

	/**
	 * 请求真是播放地址
	 * */
	private class RequestRealPlayUrl extends LetvHttpAsyncTask<RealPlayUrlInfo> {

		private final VideoFile videoFile;
		private String mid;
		private DDUrlsResult ddUrlsResult;

		public RequestRealPlayUrl(Context context, VideoFile videoFile,
				String mid) {
			super(context);
			tasks.add(this);
			this.videoFile = videoFile;
			this.mid = mid;
			setP2pMode();
		}

		@Override
		public boolean onPreExecute() {
			if (mid.equals(video.getMid())) {
				boolean isHd = PreferencesManager.getInstance().isPlayHd();
				if (isHd) {
					if (!PlayUtils.isSupportHd(video.getBrList())) {
						isHd = false;
					}
				} else {
					if (!PlayUtils.isSupportStandard(video.getBrList())) {
						isHd = true;
					}
				}
				DDUrlsResult ddUrlsResult = PlayUtils.getDDUrls(videoFile,
						isHd, isDolby);
				if (ddUrlsResult != null && ddUrlsResult.getDdurls() != null
						&& ddUrlsResult.getDdurls().length > 0) {
					PlayAlbumController.this.isHd = ddUrlsResult.isHd();
					PlayAlbumController.this.isDolby = ddUrlsResult.isDolby();
					PlayAlbumController.this.hasHd = ddUrlsResult.isHasHigh();
					PlayAlbumController.this.hasStandard = ddUrlsResult
							.isHasLow();
					this.ddUrlsResult = ddUrlsResult;
					streamLevel = ddUrlsResult.getStreamLevel();
					if (!isPlayedAd) {
						getFrontAdNormal();
						LogInfo.log("ads", "request ads");
						isPlayedAd = true;
					}

					// updatePlayDataStatistics(DataConstant.StaticticsVersion2Constatnt.PlayerAction.CLOAD_ACTION,
					// -1);
					return true;
				} else {
					if (mid.equals(video.getMid())) {
						loadLayout.notPlay();
						canToPip = false;
					}
				}
			}
			return false;
		}

		@Override
		public LetvDataHull<RealPlayUrlInfo> doInBackground() {
			if (mid.equals(video.getMid())) {
				// p2p播放
				LeService p2pService = LetvApplication.getInstance()
						.getP2pService();
				if (isP2PMode && null != p2pService) {
					ddUrl = PlayUtils.getDdUrl(
							ddUrlsResult.getDdurls(),
							null == canPlayResult ? "" : canPlayResult
									.getToken(), PreferencesManager
									.getInstance().getUserId());
					LetvDataHull<RealPlayUrlInfo> dataHull = new LetvDataHull<RealPlayUrlInfo>();
					dataHull.setDataType(LetvDataHull.DataType.DATA_IS_INTEGRITY);
					return dataHull;
				}
				LetvDataHull<RealPlayUrlInfo> dataHull = PlayUtils
						.getRealUrl(ddUrlsResult.getDdurls());
				if (dataHull.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
					statisticsVideoInfo.setDdurl(dataHull.getDataEntity()
							.getDdUrl());
					statisticsVideoInfo.setPlayurl(dataHull.getDataEntity()
							.getRealUrl());
					return dataHull;
				}
			}

			return null;
		}

		@Override
		public void onPostExecute(int updateId, RealPlayUrlInfo result) {
			tasks.remove(this);
			if (null == result) {
				// 执行p2p播放
				p2pPlayer = new PlayUrl(LetvApplication.getInstance()
						.getP2pService().getServicePort(), ddUrl, "", "");
				realUrl = p2pPlayer.getPlay();
				startPlayNet();
				return;
			}
			if (200 == result.getCode() && mid.equals(video.getMid())) {
				realUrl = result.getRealUrl();
				startPlayNet();
			} else {
				loadLayout.requestError();
				playCallBackState = 6;
				statisticsVideoInfo.setErr("1");
			}
		}

		@Override
		public void netNull() {
			tasks.remove(this);
			if (mid.equals(video.getMid())) {
				loadLayout.requestError();
				playCallBackState = 6;
				statisticsVideoInfo.setErr("1");
			}
		}

		@Override
		public void netErr(int updateId, String errMsg) {
			tasks.remove(this);
			if (mid == null && video == null) {
				return;
			}
			if (mid.equals(video.getMid())) {
				loadLayout.requestError();
				playCallBackState = 6;
				statisticsVideoInfo.setErr("1");
			}
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			tasks.remove(this);
			if (mid.equals(video.getMid())) {
				loadLayout.requestError();
				playCallBackState = 6;
				statisticsVideoInfo.setErr("1");
			}
		}

		@Override
		public void noUpdate() {
			tasks.remove(this);
			super.noUpdate();
		}
	}

	/**
	 * 设置是否开启P2P
	 */
	private void setP2pMode() {
		switch (NetWorkTypeUtils.getNetType()) {
		case NetWorkTypeUtils.NETTYPE_2G:
		case NetWorkTypeUtils.NETTYPE_3G:
			isP2PMode = false;
			break;
		default:
			if (PreferencesManager.getInstance().getUtp()) {
				isP2PMode = true;
			} else {
				isP2PMode = false;
			}
			break;
		}
		if (isDolby) {
			isP2PMode = false;
		}
	}

	/**
	 * 请求时间戳
	 * */
	public class RequestTimestampTask extends LetvHttpAsyncTask<TimestampBean> {

		public RequestTimestampTask(Context context) {
			super(context);
		}

		@Override
		public LetvDataHull<TimestampBean> doInBackground() {
			return LetvHttpApi.getTimestamp(0, new TimestampParser());
		}

		@Override
		public void onPostExecute(int updateId, TimestampBean result) {

		}
	}

	/**
	 * 回调函数，给不同需要数据的Fragment和View进行回调
	 * */
	public interface PlayAlbumControllerCallBack {
		public int STATE_RUNNING = 0;
		public int STATE_FINISH = 1;
		public int STATE_NET_NULL = 2;
		public int STATE_NET_ERR = 3;
		public int STATE_DATA_NULL = 4;
		public int STATE_OTHER = 5;
		public int STATE_RETRY = 6;

		public void notify(int state);

		public void requestDetails(long cid, String vid);

		public void setCurPage(int curPage);
	}

	@Override
	public void seekFinish(int progress) {
		getActivity().getPlayFragment().seekTo(progress * 1000);
	}

	public boolean ispostplay = false;

	@Override
	public void star() {
		// setMobileNetBg(false);

		// getActivity().getPlayFragment().seekTo(getActivity().getPlayFragment().getCurrentPosition());
		getActivity().getPlayFragment().start();

		if (playAdFragment != null && playAdFragment.isPauseAd()) {
			playAdFragment.setADPause(false);
			playAdFragment.setPauseAd(false);
		} else {
			if (getActivity().getPlayFragment().isEnforcementPause()
					&& playAdFragment != null && !playAdFragment.isPlaying()) {
				loadLayout.loading();
				getActivity().getPlayFragment().setEnforcementPause(false);
				UIs.showToast("当前为非WIFI网络，继续播放将消耗流量");
				// UIs.showToast(R.string.play_net_tag);
			}
			if (playAdFragment != null) {
				playAdFragment.setPauseAd(false);
				playAdFragment.star();
			}

		}

	}

	@Override
	public void pause() {
		getActivity().getPlayFragment().pause();
		if (!isLocalFile) {
			getPauseAd();
		}
	}

	@Override
	public void full() {
		getActivity().getmOrientationSensorListener().lockOnce(
				getActivity().getRequestedOrientation());
		UIs.screenLandscape(getActivity());
	}

	@Override
	public void half() {
		getActivity().getmOrientationSensorListener().lockOnce(
				getActivity().getRequestedOrientation());
		UIs.screenPortrait(getActivity());
		if (videosCallBack != null)
			videosCallBack.notify(videosCallBackState);// 刷新半屏的视频列表

	}

	@Override
	public void favorite() {/*
							 * if
							 * (LetvFunction.collection(FavouriteBean.getInstance
							 * (album, video), UIs.isLandscape(getActivity()) ?
							 * DataConstant
							 * .ACTION.FAVORITEACTION.FULL_PLAYER_CLICK_ACTION :
							 * DataConstant
							 * .ACTION.FAVORITEACTION.HALF_PLAYER_CLICK_ACTION))
							 * { if (mFullController != null) {
							 * mFullController.favorite(1); } if
							 * (mHalfController != null) {
							 * mHalfController.favorite(1); }
							 * UIs.showToast(R.string.toast_favorite_ok); }
							 */
	}

	@Override
	public void cancelFavorite() {/*
								 * if (LetvFunction.unCollection(getFavId(album,
								 * video))) { if (mFullController != null) {
								 * mFullController.favorite(0); } if
								 * (mHalfController != null) {
								 * mHalfController.favorite(0); }
								 * UIs.showToast(R
								 * .string.toast_favorite_cancel); }
								 */
	}

	@Override
	public void download() {/*
							 * if (!video.needPay() ||
							 * PreferencesManager.getInstance().isVip()) { if
							 * (LetvTools.checkIp(video.getControlAreas(),
							 * video.getDisableType())) { if
							 * (LetvFunction.startDownLoad(getActivity(), album,
							 * video, downloadHd, isDolby, isShowToast,
							 * UIs.isLandscape(getActivity()) ? true : false)) {
							 * isShowToast = false; changeDownload(); } } else {
							 * UIs.showToast("海外版权，无法下载"); } } else {
							 * UIs.showToast("付费内容，vip用户才能下载"); }
							 */
	}

	/**
	 * 改变为已下载状态
	 * */
	public void changeDownload() {/*
								 * if (mFullController != null)
								 * mFullController.download(1); if
								 * (mHalfController != null)
								 * mHalfController.download(1);
								 */
	}

	@Override
	public void back() {
		statisticsVideoInfo.setStatus("2");// 手动结束
		getActivity().finish();
	}

	@Override
	public void share() {

	}

	@Override
	public void forward() {
		getActivity().getPlayFragment().forward();
	}

	@Override
	public void rewind() {
		getActivity().getPlayFragment().rewind();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (!super.onKeyDown(keyCode, event)) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				if (event.getRepeatCount() == 0) {
					if (UIs.isLandscape(getActivity())
							&& getLaunchMode() == PLAY_ALBUM) {
						half();

					} else {
						back();
					}
				}
				return true;
			}
		} else {
			return true;
		}

		return false;
	}

	/**
	 * 从进入到播放整个流程，区分失败的原因进行重试
	 * */
	@Override
	public void onRequestErr() {
		retryNum++;

		switch (playCallBackState) {
		case 1:
		case 2:
			startLoadingData();
			break;
		case 3:
			new RequestVideoList(getActivity(), true, curPage, aid, vid)
					.start();
			break;
		case 4:
			// new RequestCanplay(getActivity()).start();
			break;
		case 5:
		case 6:
			new RequestVideoFile(getActivity()).start();
			break;
		case 7:
			if (TextUtils.isEmpty(realUrl)) {
				new RequestVideoFile(getActivity()).start();
			} else {
				if (aid <= 0 && vid <= 0) {
					getActivity().getPlayFragment().playLocal(realUrl, 0);
				} else {
					startPlayNet();
				}
			}
			break;
		}

	}

	/**
	 * 需要付费视频，区分登录与未登录进行引导
	 * */
	@Override
	public void onVipErr(boolean isLogin) {/*
											 * if (isLogin) {
											 * VipProductsActivity
											 * .launch(getActivity(),
											 * getActivity
											 * ().getResources().getString
											 * (R.string.pim_vip_good_title)); }
											 * else {
											 * LoginMainActivity.launch(getActivity
											 * (), LoginMainActivity.FORPLAY); }
											 */
	}

	/**
	 * 不版权外跳
	 * */
	@Override
	public void onJumpErr() {
		playWebVideo(vid);
	}

	/**
	 * 点播片外跳
	 * */
	@Override
	public void onDemandErr() {
		playWebVideo(vid);
	}

	/**
	 * 外跳播放
	 * */
	public void playWebVideo(long vid) {
		StringBuilder sbUrl = new StringBuilder();
		sbUrl.append("http://m.letv.com/play.php?from=mapp&version=");
		sbUrl.append(LetvTools.getSystemName());
		sbUrl.append("_");
		sbUrl.append(LetvConstant.Global.VERSION);
		sbUrl.append("&type=0&id=");
		sbUrl.append(vid);
		String webUrl = sbUrl.toString();
		UIControllerUtils.gotoWeb(getActivity(), webUrl);
	}

	@Override
	public void onTimeChange() {
		if (mFullController != null)
			mFullController.onTimeChange();
		if (mHalfController != null)
			mHalfController.onTimeChange();
	}

	@Override
	public void onNetChange() {
		// switch (NetWorkTypeUtils.getNetType()) {
		// case NetWorkTypeUtils.NETTYPE_2G:
		// case NetWorkTypeUtils.NETTYPE_3G:
		// /**
		// * 一次进入，只提示一次
		// * */
		// if (!alreadyPrompt && getLaunchMode() != PLAY_DOWNLOAD) {
		// alreadyPrompt = true;
		// UIs.showToast("当前为非WIFI网络，继续播放将消耗流量");
		// }
		// break;
		// }
		if (mFullController != null)
			mFullController.onNetChange();
		if (mHalfController != null)
			mHalfController.onNetChange();
	}

	@Override
	public void onBatteryChange(int curStatus, int curPower) {
		if (mFullController != null)
			mFullController.onBatteryChange(curStatus, curPower);
		if (mHalfController != null)
			mHalfController.onBatteryChange(curStatus, curPower);

	}

	@Override
	public void onDownloadStateChange() {/*
										 * if (mFullController != null) {
										 * mFullController.download(0); } if
										 * (videosCallBack != null) {
										 * videosCallBack
										 * .notify(PlayAlbumControllerCallBack
										 * .STATE_OTHER); }
										 */
	}

	@Override
	public void onHeadsetPlug() {
		int max = getMaxSoundVolume();
		int cur = getCurSoundVolume();
		if (mFullController != null)
			mFullController.onVolumeChange(max, cur);

		if (mHalfController != null)
			mHalfController.onVolumeChange(max, cur);
	}

	@Override
	public void curVolume(int max, int progrees) {
		if (mFullController != null)
			mFullController.onVolumeChange(max, progrees);
		if (mHalfController != null)
			mHalfController.onVolumeChange(max, progrees);
	}

	@Override
	public void curVolume(int max, int progrees, boolean isUp) {
		if (mFullController != null)
			mFullController.onVolumeChange(max, progrees, isUp);
		if (mHalfController != null)
			mHalfController.onVolumeChange(max, progrees);
	}

	@Override
	public Video getVideo() {
		return video;
	}

	public void setVideo(Video video) {
		this.video = video;
		if (this.video != null) {
			eTime = video.getEtime();
			bTime = video.getBtime();
			uuidTimp = LetvUtil.getUUID(getActivity());
			if (video != null && video.getDuration() < 180) {
				if (playRecord != null) {
					playRecord.setPlayedDuration(0);
				}
			}

		} else {
			eTime = 0;
			bTime = 0;
		}

		if (mFullController != null)
			mFullController.videoChange(album, video);
		if (mHalfController != null)
			mHalfController.videoChange(album, video);

	}

	@Override
	public AlbumNew getAlbum() {
		return album;
	}

	public void setAlbum(AlbumNew album) {

		this.album = album;
		// if (mFullController != null)
		// mFullController.albumChange(album);
		// if (mHalfController != null)
		// mHalfController.albumChange(album);
	}

	public long getCurTime() {
		return curTime;
	}

	public void setCurTime(long curTime) {
		this.curTime = curTime;
	}

	/**
	 * 半屏页进入下载列表
	 * */
	@Override
	public void openDownload() {/*
								 * if (isDownloadState == false) {
								 * isDownloadState = true; if (viewPager != null
								 * && viewPager.getCurrentItem() != 1) {
								 * viewPager.setCurrentItem(1, false); }
								 * tabs.setVisibility(View.GONE);
								 * viewPager.setPagingEnabled(false);
								 * videosCallBack
								 * .notify(introductionCallBackState =
								 * PlayAlbumControllerCallBack.STATE_FINISH); }
								 */
	}

	/**
	 * 半屏页关闭下载列表
	 * */
	@Override
	public void closeDownload() {/*
								 * if (isDownloadState == true) {
								 * isDownloadState = false; if (viewPager !=
								 * null && viewPager.getCurrentItem() != 1) {
								 * viewPager.setCurrentItem(1, false); }
								 * tabs.setVisibility(View.VISIBLE);
								 * viewPager.setPagingEnabled(true);
								 * videosCallBack
								 * .notify(introductionCallBackState =
								 * PlayAlbumControllerCallBack.STATE_FINISH); }
								 */
	}

	private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
			if (arg0 != 1) {

			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};

	@Override
	public void changeDownLoad(boolean isHd) {
		// downloadHd = isHd;
	}

	@Override
	public void onLongPress() {
		super.onLongPress();
		// if (mFullController != null) {
		// mFullController.clickShowAndHide(false);
		// }
		//
		// if (mHalfController != null) {
		// mHalfController.clickShowAndHide(false);
		// }
	}

	@Override
	public void onSingleTapUp() {
		// if (!isLock) {
		if (mFullController != null) {
			if (mFullController.clickShowAndHide()) {
				showLock();
			}
		}

		if (mHalfController != null) {
			if (mHalfController.clickShowAndHide()) {
				showLock();
			}
		}
		// }
	}

	@Override
	public void onLandscapeScroll(float incremental) {
		super.onLandscapeScroll(incremental);
		// if (!isLock) {
		if (getActivity().getPlayFragment().isInPlaybackState()) {
			int duration = getActivity().getPlayFragment().getDuration();
			int cur = getActivity().getPlayFragment().getCurrentPosition();
			int newcur = cur + (int) (incremental * duration);
			if (newcur < 0) {
				newcur = 1;
			}

			if (newcur > duration) {
				newcur = duration;
			}
			progressRegulate(newcur, duration);
		}
		// }
	}

	@Override
	public void onLandscapeScrollFinish(float incremental) {
		super.onLandscapeScrollFinish(incremental);
		// if (!isLock) {
		int duration = getActivity().getPlayFragment().getDuration();
		int pos = getActivity().getPlayFragment().getCurrentPosition()
				+ (int) (incremental * duration);
		if (pos < 0) {
			pos = 1;
		}

		if (pos > duration) {
			pos = duration;
		}
		getActivity().getPlayFragment().seekTo(pos);
		// }
	}

	@Override
	public void onDoubleFingersUp() {/*
									 * super.onDoubleFingersUp(); if (null !=
									 * album && video != null) { if (null ==
									 * mTsController) { mTsController = new
									 * TsController(this); }
									 * mTsController.pushPlay(); }
									 */
	}

	@Override
	public void onDoubleFingersDown() {/*
										 * super.onDoubleFingersDown(); if (null
										 * != album && video != null) { if (null
										 * == mTsController) { mTsController =
										 * new TsController(this); }
										 * mTsController.pushDownLoad(); }
										 */
	}

	/**
	 * 退出 页面，销毁相关对象
	 * */
	@Override
	public void onDestroy() {
		try {
			handler.removeMessages(HANDLER_TIME);
			handler.removeMessages(UPDATE_STATICICS_TIME);
			destroyTasks();

			if (tabs != null) {
				tabs.removeAllViewsInLayout();
			}

			if (viewPager != null) {
				viewPager.removeAllViewsInLayout();
			}

			if (loadLayout != null) {
				loadLayout.removeAllViews();
			}

			if (mFullController != null)
				mFullController.format();
			if (mHalfController != null)
				mHalfController.format();

			getActivity().getPlayUpper().removeAllViews();
			getActivity().getPlayLower().removeAllViews();

			clearValue();

			tabs = null;
			viewPager = null;
			viewPagerAdapter = null;
			loadLayout = null;

			super.onDestroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 清除请求
	 * */
	private void destroyTasks() {
		// 清空
		for (LetvBaseTaskImpl taskImpl : tasks) {
			if (taskImpl != null && !taskImpl.isCancelled()) {
				taskImpl.cancel();
			}
		}
		tasks.clear();
	}

	/**
	 * 得到暂停广告
	 * */
	private void getPauseAd() {
		com.letv.ads.util.LogInfo.log("ads", "getDemandPauseAd");
		if (video != null && playAdFragment != null) {
			playAdFragment.getDemandPauseAd(video.getCid(), aid, vid, video
					.getMid(), uuidTimp, PreferencesManager.getInstance()
					.getUserId(), video.getDuration() + "", "", "0");
		}
	}

	// /**
	// * 登录成功回来
	// * */
	// @Override
	// public void onActivityResultLoginSuccess() {
	// loadLayout.loading();
	// new RequestVideoFile(getActivity()).start();
	// }
	//
	// /**
	// * 支付成功后回来
	// * */
	// @Override
	// public void onActivityResultPaySuccess() {
	// loadLayout.loading();
	// new RequestVideoFile(getActivity()).start();
	// }

	@Override
	public void toPip() {
		if (!canToPip) {
			return;
		}

		Bundle mBundle = new Bundle();
		mBundle.putBoolean("isLive", false);
		mBundle.putString("albumtitle", (album != null ? album.getNameCn()
				: null));
		mBundle.putString("order", order);
		mBundle.putLong("aid", aid);
		mBundle.putLong("vid", vid);
		mBundle.putBoolean("isDolby", isDolby);
		mBundle.putInt("launch_mode", getLaunchMode());
		mBundle.putSerializable("album", album);
		mBundle.putInt("curPage", curPage);
		mBundle.putString("url", localPath);
		mBundle.putLong("lastvid", getLastVideoPos(videos));
		mBundle.putLong("seek", localSeek);
		PipService.launch(getActivity(), mBundle);
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (getActivity() != null) {
					back();
				}
			}
		}, 500);

	}

	private long getLastVideoPos(HashMap<Integer, VideoList> list) {
		if (null == list || list.size() == 0) {
			return -1;
		}
		Set<Integer> set = list.keySet();
		int maxPage = -1;
		for (Integer page : set) {
			maxPage = Math.max(maxPage, page);
		}

		VideoList videoList = list.get(maxPage);
		return videoList.get(videoList.size() - 1).getId();
	}

	/**
	 * 计时复位
	 */
	private void resetTimeCount() {
		timeElapsed = lastTimeElapsed = 0;
		updateCount = 0;
		retryNum = 0;
	}

	public String getRealUrl() {
		return realUrl;
	}

	/**
	 * 上报播放统计
	 * 
	 * @param actionCode
	 */
	private void updatePlayDataStatistics(String actionCode, long pt) {
		try {
			// long sVid = 0;
			long sCid = 4;
			String sb = null;
			// if (video != null) {
			// sVid = video.getId();
			// sCid = video.getCid();
			// }
			if (isLocalFile) {
				sb = "offline=1";
			}
			DataStatistics.getInstance().sendPlayInfo(getActivity(), "0", "0",
					actionCode, errorCodeJoint + "", (pt > 0 ? pt : 0) + "",
					"-", LetvUtil.getUID(), uuidTimp, sCid + "", aid + "",
					vid + "", video == null ? null : video.getDuration() + "",
					retryNum + "", "0", streamLevel, realUrl, null, sb, null,
					null, LetvUtil.getPcode(),
					PreferencesManager.getInstance().isLogin() ? 0 : 1, null,
					"-", "-");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPlayFailed() {

	}
}