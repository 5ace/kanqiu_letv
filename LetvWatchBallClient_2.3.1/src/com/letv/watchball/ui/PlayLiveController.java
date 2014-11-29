package com.letv.watchball.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.iresearch.mvideotracker.IRVideo;

import com.letv.adlib.model.ad.common.CommonAdItem;
import com.letv.ads.ADPlayFragment;
import com.letv.datastatistics.DataStatistics;
import com.letv.datastatistics.entity.StatisticsVideoInfo;
import com.letv.datastatistics.util.DataConstant;
import com.letv.http.bean.LetvDataHull;
import com.letv.pp.service.LeService;
import com.letv.pp.url.PlayUrl;
import com.letv.utils.MD5;
import com.letv.watchball.LetvApplication;
import com.letv.watchball.R;
import com.letv.watchball.activity.LoginMainActivity;
import com.letv.watchball.adapter.DetailLivePlayPagerAdapter;
import com.letv.watchball.adapter.LivelPlayScrollingTabsAdapter;
import com.letv.watchball.async.LetvBaseTaskImpl;
import com.letv.watchball.async.LetvHttpAsyncTask;
import com.letv.watchball.bean.*;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.parser.AlbumNewParse;
import com.letv.watchball.parser.DynamicCheckParser;
import com.letv.watchball.parser.ExpireTimeParser;
import com.letv.watchball.parser.LiveRealParser;
import com.letv.watchball.parser.TicketCountParser;
import com.letv.watchball.parser.UseTicketParser;
import com.letv.watchball.parser.VideoListParser;
import com.letv.watchball.service.PipService;
import com.letv.watchball.ui.PlayAlbumController.PlayAlbumControllerCallBack;
import com.letv.watchball.ui.impl.BasePlayActivity;
import com.letv.watchball.utils.*;
import com.letv.watchball.view.PlayHalfPay;
import com.letv.watchball.view.PlayHalfPay.PlayHalfPayCallBack;
import com.letv.watchball.view.PlayLoadLayout;
import com.letv.watchball.view.PlayLoadLayout.PlayLoadLayoutCallBack;
import com.letv.watchball.view.ScrollTabIndicator;
import com.letv.watchball.view.SettingViewPager;
import com.media.VideoView;
import com.media.VideoView.VideoViewStateChangeListener;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author zhanglibin
 * 
 */
public class PlayLiveController extends PlayController implements
		VideoViewStateChangeListener, PlayLiveControllerCallBack,
		PlayLoadLayoutCallBack, PlayHalfPayCallBack,
		ADPlayFragment.PlayAdListener {

	/** Bundle中的key **/
	public static String LIVE_CODE = "code";
	public static String LIVE_URL = "url";
	public static String LIVE_STREAMID = "streamId";
	public static String AID = "aid";
	public static String VID = "vid";
	public static String ID = "id";
	public static String LIVE_PROGRAM_NAME = "program_name";
	public static String LIVE_ENCRYPT = "encrypt_url";
	public static String LIVE_MODE = "mode";
	public static String LIVE_CHANNEL_NAME = "live_channel_name";
	public static String GAME = "game";

	/**
	 * 刷新
	 * */
	private final int HANDLER_TIME = 0x100;
	/**
	 * 一秒
	 * */
	private final int HANDLER_TIME_DELAYED = 1000;

	/**
	 * Handler 等待广告fragment初始化完毕再播放广告的id
	 */
	public final int WAIT_ADFRAGMENT_COMMIT = 1001;

	/**
	 * 统计播放时间
	 */
	private final int UPDATE_STATICICS_TIME = 0x101;
	/** ************ **/

	// private long aid;
	// private long vid;
	/**
	 * 5.1开始，对应轮播台卫视台的channel_ename和直播厅的ct。以此请求播放地址以及各种信息。
	 */
	protected String mCode;
	/**
	 * 需通过streamId,tm加密后直接播生成真实的播放地址才能播放
	 */
	public String mLiveUrl = null;
	/**
	 * 加密用
	 */
	public String mStreamId = null;
	public String mToken = null;
	/**
	 * 真实的播放地址，仅用于统计使用
	 */
	public String mRealLink;

	/**
	 * 节目名称
	 */
	public String mProgramName;
	/**
	 * 频道名称
	 */
	public String mChannelName;
	/**
	 * Handler 定时刷新id
	 */
	public final int REFRESHEPGLIST = 1000;

	/**
	 * 加载 错误 提示布局
	 * */
	protected PlayLoadLayout loadLayout;

	/**
	 * 直播券查询 使用
	 */
	protected PlayHalfPay ticketFrame;
	/**
	 * 半屏内容展示tabs
	 * */
	protected ScrollTabIndicator tabs;

	/**
	 * 半屏内容展示viewPage
	 * */
	protected SettingViewPager viewPager;
	/**
	 * 
	 * 半屏内容展示viewPage适配器
	 * */
	protected DetailLivePlayPagerAdapter viewPagerAdapter;

	/**
	 * 半屏控制
	 * */
	protected PlayLiveHalfController mHalfController;

	/**
	 * 全屏控制
	 * */
	protected PlayLiveFullController mFullController;

	protected RequestRealLink mRequestRealLink = null;

	/**
	 * 节目列表星期标题栏的适配器
	 */
	protected LivelPlayScrollingTabsAdapter tabsAdapter;
	protected View live_half_controller_normal;
	protected View detailplay_half_detail_normal;

	/*************************************** 统计相关参数 *************************************/

	/**
	 * 统计对象
	 * */
	protected StatisticsVideoInfo statisticsVideoInfo = new StatisticsVideoInfo();

	/**
	 * 开始播放的时间（s）
	 * */
	private long startTime;

	/**
	 * 开始播放的时间（s）
	 * */
	private long requestStartTime;

	/**
	 * 记录请求的任务
	 * */
	public List<LetvBaseTaskImpl> tasks = new ArrayList<LetvBaseTaskImpl>();

	/**
	 * 是否已经提示过；
	 * */
	protected boolean alreadyPrompt;

	public TextView no_program;
	public TextView no_play_program;

	public boolean ispostplay = false;
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
	 * 重试次数
	 */
	private int retryNum = 0;
	private int updateCount = 0;

	/**
	 * 当前视频所在专辑（可能为空）
	 * */
	private AlbumNew album;
	private Video video;
	public Game game;
	public boolean isHd;
	public boolean hasHd;
	public boolean hasStandard;
	private boolean isP2PMode = PreferencesManager.getInstance().getUtp();

	// 用于鉴权的token
	private String payToken_live350 = "";
	private String payToken_live800 = "";
	private boolean nologin = false;
	/**
	 * 播放页广告fragment
	 * */
	protected ADPlayFragment playAdFragment;
	private boolean isPlayedAd = false;
	private boolean isUploadStatictics;
	private long loadingConsumeTime;
	private boolean isPlayedAdFinish;

	public void setCode(String mCode) {
		this.mCode = mCode;
	}

	public String getCode() {
		return mCode;
	}

	public String getChannelName() {
		return mChannelName;
	}

	public PlayLiveController(BasePlayActivity activity) {
		super(activity);
	}

	@Override
	public void onActivityRestart() {
		if (game.pay.equalsIgnoreCase("1") && nologin)
			getActivity().recreate();
	}

	/**
	 * 刷新handler
	 * */
	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {

			case HANDLER_TIME:

				timeElapsed++;

				handler.sendEmptyMessageDelayed(HANDLER_TIME,
						HANDLER_TIME_DELAYED);
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
						lastTimeElapsed = timeElapsed;
						updatePlayDataStatistics(
								DataConstant.StaticticsVersion2Constatnt.PlayerAction.TIME_ACTION,
								mStreamId, mProgramName, mRealLink, timeElapsed);

						// handler.sendEmptyMessageDelayed(UPDATE_STATICICS_TIME,
						// 60000);
					}
				} else if (updateCount == 1) {
					if (timeElapsed - lastTimeElapsed < 60) {
						updateCount = 1;

						// handler.sendEmptyMessageDelayed(UPDATE_STATICICS_TIME,
						// (timeElapsed - lastTimeElapsed) * 1000);
					} else {
						Log.i("oyys", "send 60="
								+ (timeElapsed - lastTimeElapsed));
						updateCount = 2;
						updatePlayDataStatistics(
								DataConstant.StaticticsVersion2Constatnt.PlayerAction.TIME_ACTION,
								mStreamId, mProgramName, mRealLink,
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
								mStreamId, mProgramName, mRealLink, finaltime);
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

	@Override
	public void create() {
		super.create();

		updatePlayDataStatistics(
				DataConstant.StaticticsVersion2Constatnt.PlayerAction.INIT_ACTION,
				mStreamId, mProgramName, mRealLink, 0);
		try {
			// 环境日志在用户的每次开机上报
			IP ip = LetvApplication.getInstance().getIp();
			DataStatistics.getInstance().sendEnvInfo(getActivity(), "0", "0",
					ip == null ? "" : ip.getClient_ip(), LetvUtil.getSource(),
					true);
			/*
			 * 艾瑞初始化视频信息直播信息不能确定视频长度，把视频长度设置成1
			 */
			IRVideo.getInstance(getActivity()).newVideoPlay(
					String.valueOf(vid), 1, false);

		} catch (Exception e) {
			e.printStackTrace();
		}
		// 播放的是传来的url。

		if (game.live_350 != null && !TextUtils.isEmpty(game.live_350.streamId)
				&& !TextUtils.isEmpty(game.live_350.liveUrl)
				&& game.live_800 != null
				&& !TextUtils.isEmpty(game.live_800.streamId)
				&& !TextUtils.isEmpty(game.live_800.liveUrl)) {
			hasStandard = true;
		}

		if (game.live_800 != null && !TextUtils.isEmpty(game.live_800.streamId)
				&& !TextUtils.isEmpty(game.live_800.liveUrl)) {
			hasHd = true;
		}

		if (game.live_350 != null && !TextUtils.isEmpty(game.live_350.liveUrl)) {
			mLiveUrl = game.live_350.liveUrl;
		}
		if (game.live_800 != null && !TextUtils.isEmpty(game.live_800.liveUrl)) {
			mLiveUrl = game.live_800.liveUrl;
		}
		if (!TextUtils.isEmpty(mLiveUrl)) {
			boolean playHd = PreferencesManager.getInstance().isPlayHd();
			if (!playHd && game.live_350 != null
					&& !TextUtils.isEmpty(game.live_350.streamId)
					&& !TextUtils.isEmpty(game.live_350.liveUrl)) {
				isHd = false;
				// 如果是付费直播，则需要鉴权
				if (Integer.valueOf(game.pay) == 1) {
					String userId = PreferencesManager.getInstance()
							.getUserId();
					if (userId == null || userId.equalsIgnoreCase("")) {
						nologin = true;
						loadLoginUI();
						return;
					}

					String pid = game.id;
					String liveid = game.liveid;
					String from = "mobile";
					String streamId = game.live_350.streamId;
					String splatId = "1013";
					String lsstart = String.valueOf(game.status);
					// (pid,liveid,from,streamId,splatId,userId,version,pcode)
					payToken_live350 = requestDynamicCheck(pid, liveid, from,
							streamId, splatId, userId, lsstart);
					// 取得token失败后停止播放并尝试获取直播券数量
					if (payToken_live350 == null
							|| payToken_live350.equalsIgnoreCase("")) {
						// TODO
						startLoadingData();
						loadPayUI();
						RequestTicketCount requestTicketCount = new RequestTicketCount(
								this.getActivity(), game.liveid, userId,
								ticketFrame);
						requestTicketCount.start();
						return;
					}
					// Log.e("gogmeng", "live500_token" + payToken_live500);
					playUrl(game.live_350.streamId, game.live_350.liveUrl,
							payToken_live350);
				} else {
					playUrl(game.live_350.streamId, game.live_350.liveUrl);
				}
			} else {
				if (game.live_800 != null
						&& !TextUtils.isEmpty(game.live_800.streamId)
						&& !TextUtils.isEmpty(game.live_800.liveUrl)) {
					isHd = true;
					PreferencesManager.getInstance().setIsPlayHd(true);
					// 如果是付费直播，则需要鉴权
					if (Integer.valueOf(game.pay) == 1) {
						String userId = PreferencesManager.getInstance()
								.getUserId();
						if (userId == null || userId.equalsIgnoreCase("")) {
							nologin = true;
							loadLoginUI();
							return;
						}

						String pid = game.id;
						String liveid = game.liveid;
						String from = "mobile";
						String streamId = game.live_350.streamId;
						String splatId = "1013";
						String lsstart = String.valueOf(game.status);
						// (pid,liveid,from,streamId,splatId,userId,version,pcode)
						payToken_live800 = requestDynamicCheck(pid, liveid,
								from, streamId, splatId, userId, lsstart);
						// 取得token失败后停止播放并尝试获取直播券数量
						if (payToken_live800 == null
								|| payToken_live800.equalsIgnoreCase("")) {
							// TODO
							loadPayUI();
							RequestTicketCount requestTicketCount = new RequestTicketCount(
									this.getActivity(), game.liveid, userId,
									ticketFrame);
							requestTicketCount.start();

						}
						// Log.e("gogmeng", "live800_token" + payToken_live800);
						playUrl(game.live_800.streamId, game.live_800.liveUrl,
								payToken_live800);
					} else {
						playUrl(game.live_800.streamId, game.live_800.liveUrl);
					}
				} else {
					loadLayout.requestError();
					return;
				}

			}
			startLoadingData();
		} else {
			loadLayout.requestError();
		}

		mFullController.initHighOrLow();
	}

	private void loadLoginUI() {
		this.startLoadingData();
		this.mHalfController.pause();
		getActivity().getPlayUpper().removeAllViews();

		this.ticketFrame = new PlayHalfPay(this.getActivity(), game.homeImg,
				game.guestImg);
		this.ticketFrame.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		ticketFrame.setCallBack(this);
		ticketFrame.noLogin();
		getActivity().getPlayUpper().addView(ticketFrame);
	}

	/**
	 * 停止播放，加载直播券的UI
	 */
	private void loadPayUI() {
		this.mHalfController.pause();
		getActivity().getPlayUpper().removeAllViews();
		View live_half_controller = getActivity().findViewById(
				R.id.live_half_controller);
		live_half_controller.setVisibility(8);
		this.ticketFrame = new PlayHalfPay(this.getActivity(), game.homeImg,
				game.guestImg);
		this.ticketFrame.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		ticketFrame.setCallBack(this);
		getActivity().getPlayUpper().addView(ticketFrame);
	}

	public String requestDynamicCheck(String pid, String liveid, String from,
			String streamId, String splatId, String userId, String lsstart) {
		if (userId.equalsIgnoreCase("")) {
			return "";
		}

		Map<String, String> map = new HashMap<String, String>();
		map.put("pid", pid);
		map.put("liveid", liveid);
		map.put("from", from);
		map.put("streamId", streamId);
		map.put("splatId", splatId);
		map.put("userId", userId);
		map.put("version", LetvConstant.Global.VERSION);
		map.put("pcode", LetvConstant.Global.PCODE);
		Collection<String> keyset = map.keySet();
		List<String> list = new ArrayList<String>(keyset);

		// 对key键值按字典升序排序
		Collections.sort(list);

		StringBuilder stringBuilder = new StringBuilder("");
		for (int i = 0; i < list.size(); i++) {
			System.out.println("key键---值: " + list.get(i) + ","
					+ map.get(list.get(i)));
			stringBuilder
					.append(list.get(i) + "=" + map.get(list.get(i)) + "&");
		}
		stringBuilder.append(LetvConstant.Global.ASIGN_KEY);
		String apisign = MD5.toMd5(stringBuilder.toString());

		// String pid, String liveid,
		// String from, String streamId, String splatId, String userId,
		// String lsstart, String apisign,
		try {
			LetvDataHull<DynamicCheck> dh = LetvHttpApi.dynamiccheck(0, pid,
					liveid, from, streamId, splatId, userId, lsstart, apisign,
					new DynamicCheckParser());
			if (dh != null
					&& dh.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
				JSONObject data = new JSONObject(dh.getSourceData());
				data = data.getJSONObject("body");
				data = data.getJSONObject("result");
				String status = data.getString("status");

				if (Integer.valueOf(status) == 1) { // 鉴权成功
					Log.e("gongmeng", "鉴权成功");
					return data.getString("token");

				} else {
					return "";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 如果streamId为空，则直接播放url，不进行加密。所有播放的入口
	 * 
	 * @param streamId
	 * @param url
	 */
	public void playUrl(String streamId, final String url) {

		mLiveUrl = url;

		
		if (TextUtils.isEmpty(streamId)) {
			play(url);
		} else {
			mStreamId = streamId;
			requestRealLink(streamId, url);
		}
	}

	public void playUrl(String streamId, final String url, String token) {

		mLiveUrl = url;

		// 播放广告
		LogInfo.log("ads", "isPlayedAd=" + isPlayedAd + "playAdFragment="
				+ playAdFragment);
		if (!isPlayedAd && playAdFragment != null) {
			timeRequestAd = System.currentTimeMillis();
			LogInfo.log("ads", "request ads");

			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					getFrontAdNormal(url, "1");
					isPlayedAd = true;
				}
			}, 500);

		}

		if (TextUtils.isEmpty(streamId)) {
			play(url);
		} else {
			mStreamId = streamId;
			mToken = token;

			requestRealLink(streamId, url, token, PreferencesManager
					.getInstance().getUserId());

		}

	}

	/**
	 * 正常流程得到前贴广告
	 */
	public void getFrontAdNormal(String url, String ty) {
		Log.d("ads", "getFrontAdNormal");
		boolean flag = false;
		switch (NetWorkTypeUtils.getNetType()) {
		case NetWorkTypeUtils.NETTYPE_2G:
		case NetWorkTypeUtils.NETTYPE_3G:
			/**
			 * 一次进入，只提示一次
			 * */
			flag = true;
			if (!alreadyPrompt) {
				if (playAdFragment != null) {
					playAdFragment.setPauseAd(true);
					playAdFragment.setADPause(true);
				}
				if (playAdFragment != null) {
					playAdFragment.getLiveFrontAd(getActivity(), url, LetvUtil
							.getUUID(getActivity()), PreferencesManager
							.getInstance().getUserId(), "", ty);
					getActivity().getPlayFragment().setEnforcementWait(true);
				}
				getActivity().getPlayFragment().setEnforcementPause(true);
				alreadyPrompt = true;
				// setMobileNetBg(true);
				// UIs.showToast(R.string.play_2g3gnet_tag);
			} else if (true) {
				// UIs.showToast(R.string.play_net_tag);
				if (playAdFragment != null) {
					playAdFragment.getLiveFrontAd(getActivity(), url, LetvUtil
							.getUUID(getActivity()), PreferencesManager
							.getInstance().getUserId(), "", ty);
					getActivity().getPlayFragment().setEnforcementWait(true);
				}
			}
			break;
		}
		if (!flag) {
			if (playAdFragment != null) {
				playAdFragment.getLiveFrontAd(getActivity(), url, LetvUtil
						.getUUID(getActivity()), PreferencesManager
						.getInstance().getUserId(), "", ty);
				getActivity().getPlayFragment().setEnforcementWait(true);
			}
		}
	}

	@Override
	protected void initLayout() {

		if (getLaunchMode() == PLAY_LIVE) {
			loadLayout = new PlayLoadLayout(getActivity());
			loadLayout.setCallBack(this);
			loadLayout.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			getActivity().getPlayUpper().addView(loadLayout);
			loadLayout.loading();

			UIs.inflate(getActivity(), R.layout.live_full_play_controller,
					getActivity().getPlayUpper(), true);
			UIs.inflate(getActivity(), R.layout.play_live_lower, getActivity()
					.getPlayLower(), true);// 播放窗口下面的区域

			/**
			 * 广告
			 */
			playAdFragment = new ADPlayFragment();
			playAdFragment
					.setViewCallBack(new ADPlayFragment.VipViewCallBack() {

						@Override
						public void onClick() {
							if (PreferencesManager.getInstance().isLogin()) {
								if (false) {
									// playAdFragment.pause();
									// playAdFragment.setMobileNetBg(false);
									// getFrontAd(mLiveStreamBean.getLiveUrl(),
									// getLaunchMode() == PLAY_LIVE_LUNBO ? "2"
									// : "1");
								} else {
									// VipProductsActivity.launch(getActivity(),
									// getActivity().getResources().getString(R.string.pim_vip_good_title));
								}
							} else {
								// LetvAccountLogin.launch(getActivity(),
								// LoginMainActivity.FORPLAY);
							}
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
											mHalfController.mPlayPause
													.performClick();
										}
									}

								}
							}, 500);
						}
					});
			playAdFragment.setAdListener(this);
			getActivity().getSupportFragmentManager().beginTransaction()
					.add(R.id.play_upper, playAdFragment).commit();

			initViewPagerAndTab();
			initHalfController();
			videosCallBackState = PlayAlbumControllerCallBack.STATE_FINISH;
			if (videosCallBack != null)
				videosCallBack.notify(videosCallBackState);// 刷新半屏的视频列表
			initFullController();

			no_program = (TextView) getActivity().findViewById(R.id.no_program);
			no_play_program = (TextView) getActivity().findViewById(
					R.id.no_play_program);
		} else if (getLaunchMode() == PLAY_LIVE_FULL) {
			if (!UIs.isLandscape(getActivity())) {
				UIs.screenLandscape(getActivity());
			}
			loadLayout = new PlayLoadLayout(getActivity());
			loadLayout.setCallBack(this);
			loadLayout.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			getActivity().getPlayUpper().addView(loadLayout);
			loadLayout.loading();

			UIs.inflate(getActivity(), R.layout.live_full_play_controller,
					getActivity().getPlayUpper(), true);
			// UIs.inflate(getActivity(), R.layout.play_live_lower,
			// getActivity().getPlayLower(), true);// 播放窗口下面的区域

			initFullController();

			no_program = (TextView) getActivity().findViewById(R.id.no_program);
			no_play_program = (TextView) getActivity().findViewById(
					R.id.no_play_program);
		}

	}

	public void setNoProgram(boolean visibility) {
		if (no_program != null) {
			no_program.setVisibility(visibility ? View.VISIBLE : View.GONE);
		}
	}

	public void setNotPlayProgram(boolean visibility) {
		if (no_play_program != null) {
			no_play_program
					.setVisibility(visibility ? View.VISIBLE : View.GONE);
		}
		if (mFullController != null) {
			mFullController.setPipEnable(visibility);
		}
	}

	/**
	 * 初始化ViewPager和tab
	 * */
	protected void initViewPagerAndTab() {
		viewPager = (SettingViewPager) getActivity().findViewById(
				R.id.detailplay_half_detail_viewpager);
		viewPager.setPagingEnabled(true);
		viewPagerAdapter = new DetailLivePlayPagerAdapter(getActivity()
				.getSupportFragmentManager());
		viewPager.setAdapter(viewPagerAdapter);

		viewPager.setCurrentItem(1);
		tabs = (ScrollTabIndicator) getActivity().findViewById(
				R.id.detailplay_half_detail_indicator);

		LivelPlayScrollingTabsAdapter tabsAdapter = new LivelPlayScrollingTabsAdapter(
				getActivity());
		tabs.setViewPager(viewPager, 1);
		tabs.setAdapter(tabsAdapter);
		tabs.setListener(onPageChangeListener);
	}

	protected void initViewPager() {
		viewPager.setCurrentItem(0);
	}

	protected void initTabs() {
		tabs.setViewPager(viewPager, 1);
		tabs.setAdapter(tabsAdapter);
		tabs.setListener(onPageChangeListener);
	}

	@Override
	public ViewPager getViewPager() {
		return viewPager;
	}

	protected OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {/*
											 * if (isZhiboting()) { showDay =
											 * today + arg0 - 1; } else {
											 * showDay = today + arg0; } //
											 * UIs.showToast("arg0 = " + arg0 +
											 * " showDay = " + showDay); //
											 * requestLiveEpgInfo();
											 * setNoProgram(false);
											 * requestData(true, false);
											 */
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};

	protected void initFullController() {
		mFullController = new PlayLiveFullController(this, getActivity()
				.getWindow().getDecorView().getRootView());
		mFullController.setCallBack(this);
	}

	protected void initHalfController() {
		mHalfController = new PlayLiveHalfController(this, getActivity()
				.getWindow().getDecorView().getRootView());
		mHalfController.setCallBack(this);

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
	public void onDownloadStateChange() {

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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (!super.onKeyDown(keyCode, event)) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				// if (UIs.isLandscape(getActivity())) {
				if (UIs.isLandscape(getActivity())
						&& getLaunchMode() == PLAY_LIVE) {
					half();
				} else {
					back();
				}
				return true;
			}
		} else {
			return true;
		}

		return false;
	}

	@Override
	public void onRequestErr() {
		if (mLiveUrl != null) {
			playUrl(mStreamId, mLiveUrl, mToken);
		} else {
			startLoadingData();
		}
	}

	@Override
	public void onVipErr(boolean isLogin) {

	}

	@Override
	public void onJumpErr() {

	}

	@Override
	public void onDemandErr() {

	}

	@Override
	public void star() {
		getActivity().getPlayFragment().start();
		if (playAdFragment != null && playAdFragment.isPauseAd()
				&& playAdFragment.isHaveFrontAds()) {
			playAdFragment.setADPause(false);
			playAdFragment.setPauseAd(false);
			// UIs.showToast(R.string.play_net_tag);
		} else {
			if (getActivity().getPlayFragment().isEnforcementPause()
					&& playAdFragment != null && !playAdFragment.isPlaying()) {
				if (TextUtils.isEmpty(mProgramName)) {
					loadLayout.loading();
				} else {
					loadLayout.loading();
				}
				getActivity().getPlayFragment().setEnforcementPause(false);
				getActivity().getPlayFragment().setEnforcementWait(false);
				getActivity().getPlayFragment().pause();
				UIs.showToast("当前为非WIFI网络，继续播放将消耗流量");
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
	}

	@Override
	public void back() {
		statisticsVideoInfo.setStatus("2");// 手动结束
		getActivity().finish();
	}

	public int mCurrentState;

	/**
	 * 监听播放器当前状态
	 * */
	@Override
	public void onChange(int currentState) {
		mCurrentState = currentState;
		if (mCurrentState == VideoView.STATE_PLAYING) {

			setNotPlayProgram(false);
			startTime = System.currentTimeMillis();

			// 开始播放，隐藏loading
			if (loadLayout != null)
				loadLayout.finish();

			if (mHalfController != null) {
				mHalfController.star();
			}

			if (mFullController != null) {
				mFullController.star();
				mFullController.setPipEnable(true);
			}

			// 艾瑞检测，视频启动
			try {
				IRVideo.getInstance(getActivity()).videoPlay();
			} catch (Exception e) {
				// Log.e("gongmeng", "vvTracker Video play error");
			}

		} else if (mCurrentState == VideoView.STATE_PAUSED) {
			{// 统计播放时长
				long ct = System.currentTimeMillis();
				if (startTime != 0) {
					statisticsVideoInfo.setPlayedTime(statisticsVideoInfo
							.getPlayedTime() + (ct - startTime));
				}

				// 艾瑞检测，视频暂停
				try {
					IRVideo.getInstance(getActivity()).videoPause();
				} catch (Exception e) {
					// Log.e("gongmeng", "vvTracker pause error");
				}
			}
			// 暂停播放，隐藏loading
			if (loadLayout != null) {
				loadLayout.finish();
			}

			if (mHalfController != null)
				mHalfController.pause();

			if (mFullController != null)
				mFullController.pause();
		} else if (mCurrentState == VideoView.STATE_ERROR) {
			statisticsVideoInfo.setStatus(DataConstant.STATUS.ERROR);

			if (playAdFragment != null) {
				playAdFragment.closePauseAd();
			}

			if (loadLayout != null)
				loadLayout.requestError();
			statisticsVideoInfo.setErr("3");
			if (mFullController != null) {
				mFullController.setPipEnable(false);
			}
			// 艾瑞检测，视频停止
			try {
				IRVideo.getInstance(getActivity()).videoEnd();
			} catch (Exception e) {
				// Log.e("gongmeng", "vvTracker pause error");
			}

		} else if (mCurrentState == VideoView.STATE_IDLE) {
			if (loadLayout != null)
				loadLayout.loading();

			if (mHalfController != null)
				mHalfController.Inoperable();

			if (mFullController != null) {
				mFullController.Inoperable();
				mFullController.setPipEnable(false);
			}
		} else if (mCurrentState == VideoView.STATE_PLAYBACK_COMPLETED) {
			getActivity().getPlayFragment().pause();
			getActivity().getPlayFragment().stopPlayback();
			// 艾瑞检测，视频停止
			try {
				IRVideo.getInstance(getActivity()).videoEnd();
			} catch (Exception e) {
				// Log.e("gongmeng", "vvTracker end error");
			}

		} else if (mCurrentState == VideoView.STATE_STOPBACK) {// 调起stopback时回调

			if (TextUtils.isEmpty(statisticsVideoInfo.getStatus())) {// 如果没有状态，就代表正常完成
				statisticsVideoInfo.setStatus(DataConstant.STATUS.AUTO);
			}

			{// 统计播放时长
				long ct = System.currentTimeMillis();
				if (startTime != 0) {
					statisticsVideoInfo.setPlayedTime(statisticsVideoInfo
							.getPlayedTime() + (ct - startTime));
				}

				// 艾瑞检测，视频停止
				try {
					IRVideo.getInstance(getActivity()).videoEnd();
				} catch (Exception e) {
					// Log.e("gongmeng", "vvTracker end error");
				}

			}
			statisticsVideoInfo.setFrom(DataConstant.FROM.OTHER);
			statisticsVideoInfo.setPtype("3");
			statisticsVideoInfo.setTerminaltype("phone");
			statisticsVideoInfo.setUid(LetvUtil.getUID());
			statisticsVideoInfo.setPcode(LetvUtil.getPcode());
			statisticsVideoInfo.setPtid(LetvUtil.getUUID(getActivity()));
			if (playAdFragment != null) {
				statisticsVideoInfo.setAc(playAdFragment.getAc());
			}
			// 暂停播放，隐藏loading
			if (loadLayout != null) {
				loadLayout.finish();
			}
			LogInfo.log("onChange", statisticsVideoInfo.toString());
			// DataStatistics.getInstance().sendVideoInfo(getActivity(),
			// statisticsVideoInfo);
			LogInfo.log("zlb", "STATE_STOPBACK");
			updatePlayDataStatistics(
					DataConstant.StaticticsVersion2Constatnt.PlayerAction.TIME_ACTION,
					mStreamId, mProgramName, mRealLink,
					(timeElapsed - lastTimeElapsed));
			updatePlayDataStatistics(
					DataConstant.StaticticsVersion2Constatnt.PlayerAction.END_ACTION,
					mStreamId, mProgramName, mRealLink, 0);
			handler.removeMessages(HANDLER_TIME);
			handler.removeMessages(UPDATE_STATICICS_TIME);
		} else if (mCurrentState == VideoView.STATE_ENFORCEMENT) {
			if (getActivity().getPlayFragment().isEnforcementPause()) {
				{// 统计播放时长
					long ct = System.currentTimeMillis();
					if (startTime != 0) {
						statisticsVideoInfo.setPlayedTime(statisticsVideoInfo
								.getPlayedTime() + (ct - startTime));
					}

					// 艾瑞检测，视频暂停
					try {
						IRVideo.getInstance(getActivity()).videoPause();
					} catch (Exception e) {
						// Log.e("gongmeng", "vvTracker pause error");
					}
				}
				// 暂停播放，隐藏loading
				loadLayout.finish();
				// 停止刷新进度
				// stopHandlerTime();

				if (mHalfController != null)
					mHalfController.pause();

				if (mFullController != null) {
					mFullController.pause();
					mFullController.setPipEnable(false);
				}

			} else {

			}
		}
	}

	@Override
	protected void readArguments() {
		Intent intent = getActivity().getIntent();

		// intent.putExtra("launchMode", launchMode);
		// intent.putExtra(PlayLiveController.LIVE_CODE, code);
		// intent.putExtra(PlayLiveController.LIVE_STREAMID, streamId);
		// intent.putExtra(PlayLiveController.LIVE_URL, url);
		//
		// intent.putExtra("aid", aid);
		// intent.putExtra("vid", vid);
		// intent.putExtra(PlayLiveController.LIVE_URL, url);
		// intent.putExtra(PlayLiveController.LIVE_CODE, code);
		game = (Game) intent.getSerializableExtra(PlayLiveController.GAME);
		if (null != game) {
			LetvApplication.getInstance().saveLiveGame(game);
		} else {
			game = LetvApplication.getInstance().getLiveGame();
		}

		if (game == null) {
			game = PipService.game;
		}
		aid = intent.getLongExtra(PlayLiveController.AID, 0);
		vid = intent.getLongExtra(PlayLiveController.VID, 0);
		id = intent.getLongExtra(PlayLiveController.ID,
				Long.valueOf(TextUtils.isEmpty(game.id) ? "0" : game.id));
		mCode = intent.getStringExtra(PlayLiveController.LIVE_CODE);
		// mStreamId = intent.getStringExtra(LIVE_STREAMID);
		isPlayedAd = intent.getBooleanExtra("fromPip", false);
		if (!PreferencesManager.getInstance().getUserId().equalsIgnoreCase(""))
			isPlayedAd = true;
		// mLiveUrl = intent.getStringExtra(LIVE_URL);// anti leech , not read
		// url,
		// only use code
		mChannelName = intent.getStringExtra(LIVE_CHANNEL_NAME);
		mProgramName = getActivity().getIntent().getStringExtra(
				PlayLiveController.LIVE_PROGRAM_NAME);
	}

	@Override
	public void changeDirection(boolean isLandscape) {
		// if (getLaunchMode() == PlayController.PLAY_LIVE_SANFANG) {
		// return;
		// }
		if (isLandscape) {
			if (mHalfController != null)
				mHalfController.hide();
			if (mFullController != null)
				mFullController.show();
			showLock();
		} else {
			if (mHalfController != null)
				mHalfController.show();
			showLock();
			if (mFullController != null)
				mFullController.hide();
		}
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
		if (playAdFragment != null) {
			playAdFragment.setMuteViewStatus(progrees);
		}
	}

	@Override
	public void format() {
		tabs.setListener(null);
		tabs.setAdapter(null);
		tabs.removeAllViewsInLayout();
		tabs = null;
		viewPager.setAdapter(null);
		viewPager.removeAllViewsInLayout();
		viewPager = null;
		viewPagerAdapter.format();
		loadLayout.removeAllViews();
		loadLayout = null;
		setNoProgram(false);
		setNotPlayProgram(false);

		if (mFullController != null)
			mFullController.format();
		if (mHalfController != null)
			mHalfController.format();

		getActivity().getSupportFragmentManager().beginTransaction()
				.remove(playAdFragment).commit();
		getActivity().getPlayUpper().removeAllViews();
		getActivity().getPlayLower().removeAllViews();
		// clearValue();
	}

	@Override
	public void book() {

	}

	@Override
	public void onLongPress() {
		super.onLongPress();
		if (mFullController != null) {
			mFullController.clickShowAndHide(false);
		}

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
	}

	public void play(String mUrl) {
		switch (NetWorkTypeUtils.getNetType()) {
		case NetWorkTypeUtils.NETTYPE_2G:
		case NetWorkTypeUtils.NETTYPE_3G:
			/**
			 * 一次进入，只提示一次
			 * */
			if (!alreadyPrompt) {
				alreadyPrompt = true;
				if (playAdFragment != null) {
					playAdFragment.setPauseAd(true);
					playAdFragment.setADPause(true);
					getActivity().getPlayFragment().setEnforcementPause(true);
				}
				getActivity().getPlayFragment().setEnforcementPause(true);
			}
			break;
		}
		long ct = System.currentTimeMillis();// 请求结算时间
		statisticsVideoInfo.setUtime(((int) (ct - requestStartTime) / 1000));
		statisticsVideoInfo.setPlayurl(mUrl);
		mRealLink = mUrl;

		videosCallBackState = PlayAlbumControllerCallBack.STATE_FINISH;
		if (videosCallBack != null)
			videosCallBack.notify(videosCallBackState);// 刷新半屏的视频列表

		getActivity().getPlayFragment().playNet(mUrl, true, false, 0);
		if (ispostplay) {

			updatePlayDataStatistics(
					DataConstant.StaticticsVersion2Constatnt.PlayerAction.PLAY_ACTION,
					mStreamId, mProgramName, mRealLink, 0);
		}
	}

	public void setProgramName(String name) {
		mProgramName = name;
	}

	boolean hasInitExpireTime = false;

	public String replaceTm(String tm, String url) {
		if (TextUtils.isEmpty(url)) {
			return null;
		}
		int posT = url.indexOf("tm=");
		if (posT == -1) {
			return null;
		}
		int posE = url.indexOf("&", posT) == -1 ? url.length() : url.indexOf(
				"&", posT);
		return url.replace(url.substring(posT, posE), "tm=" + tm);
	}

	/**
	 * 请求节目单耗时
	 */
	private long timeRequestProgramList;
	/**
	 * 请求能否播放耗时
	 */
	private long timeRequestCanplay;
	/**
	 * 请求真实播放地址耗时
	 */
	private long timeRequestRealUrl;
	/**
	 * 请求广告耗时
	 */
	private long timeRequestAd;
	private long totalConsumeTime;

	private boolean hasStartPlay;

	@Override
	public void onFinish(boolean isFinishByHand, boolean hasAds) {
		// if(onlyFull){
		// onShowcotroller();
		// }
		if (!TextUtils.isEmpty(mRealLink)) {
			updatePlayDataStatistics(
					DataConstant.StaticticsVersion2Constatnt.PlayerAction.PLAY_ACTION,
					mStreamId, mProgramName, mRealLink, 0);
		} else {
			ispostplay = true;
		}
		if (playAdFragment != null && hasAds) {
			playAdFragment.setPauseAd(false);
		}

		LogInfo.log("live", "request ad finish");
		handler.removeMessages(REFRESHEPGLIST);
		if (getActivity().getPlayFragment().isEnforcementPause()
				&& playAdFragment != null && playAdFragment.isHaveFrontAds()) {
			getActivity().getPlayFragment().setEnforcementPause(false);
		}
		timeRequestAd = System.currentTimeMillis() - timeRequestAd;

		if (isFinishByHand) {
			// 当是手动结束时，不执行播放
			if (!isUploadStatictics) {
				totalConsumeTime = System.currentTimeMillis()
						- totalConsumeTime;
				// LogInfo.log("live", "onFinish isFinishByHand = " +
				// isFinishByHand + " , staticticsLoadTimeInfo timeRequestAd = "
				// + timeRequestAd
				// + " , timeRequestProgramList = " + timeRequestProgramList
				// + " , timeRequestCanplay = " + timeRequestCanplay
				// + " , timeRequestRealUrl = " + timeRequestRealUrl);
				staticticsLoadTimeInfo(getActivity(), timeRequestAd,
						timeRequestProgramList, timeRequestCanplay,
						timeRequestRealUrl);
			}
			return;
		} else {
			if (hasStartPlay && !isUploadStatictics) {
				// LogInfo.log("live", "onFinish isFinishByHand = " +
				// isFinishByHand + " , staticticsLoadTimeInfo timeRequestAd = "
				// + timeRequestAd
				// + " , timeRequestProgramList = " + timeRequestProgramList
				// + " , timeRequestCanplay = " + timeRequestCanplay
				// + " , timeRequestRealUrl = " + timeRequestRealUrl);
				totalConsumeTime = System.currentTimeMillis()
						- totalConsumeTime;
				staticticsLoadTimeInfo(getActivity(), timeRequestAd,
						timeRequestProgramList, timeRequestCanplay,
						timeRequestRealUrl);
			}
		}

		getActivity().getPlayFragment().setEnforcementWait(false);
		getActivity().getPlayFragment().start();
		handler.removeMessages(HANDLER_TIME);
		handler.sendEmptyMessage(HANDLER_TIME);
		handler.removeMessages(UPDATE_STATICICS_TIME);
		handler.sendEmptyMessage(UPDATE_STATICICS_TIME);
		isPlayedAdFinish = true;
	}

	@Override
	public void onM3U8(ArrayList<CommonAdItem> ads) {

	}

	/**
	 * 使用直播券
	 * 
	 */
	protected class RequestUseTicket extends LetvHttpAsyncTask<UseTicket> {

		String liveid;
		String uid;
		Context handler;

		/**
		 * @param context
		 * @param liveid
		 * @param uid
		 */
		public RequestUseTicket(Context context, String liveid, String uid) {
			super(context);
			handler = context;
			this.liveid = liveid;
			this.uid = uid;
			if (uid == null || uid.equalsIgnoreCase("")) {
				this.cancel();
			}
		}

		@Override
		public LetvDataHull<UseTicket> doInBackground() {
			if (liveid == null || liveid.length() < 16)
				return null;
			String channel = liveid.substring(0, 2);
			String category = liveid.substring(2, 5);
			String season = liveid.substring(5, 9);
			String turn = liveid.substring(9, 12);
			String gameid = liveid.substring(12, 16);
			LetvDataHull<UseTicket> hull = null;
			Map<String, String> map = new HashMap<String, String>();
			map.put("userid", uid);
			map.put("tickettype", "1");
			map.put("channel", channel);
			map.put("season", season);
			map.put("category", category);
			map.put("game", gameid);
			map.put("turn", turn);
			map.put("version", LetvConstant.Global.VERSION);
			map.put("pcode", LetvConstant.Global.PCODE);
			Collection<String> keyset = map.keySet();
			List<String> list = new ArrayList<String>(keyset);

			// 对key键值按字典升序排序
			Collections.sort(list);

			StringBuilder stringBuilder = new StringBuilder("");
			for (int i = 0; i < list.size(); i++) {
				System.out.println("key键---值: " + list.get(i) + ","
						+ map.get(list.get(i)));
				stringBuilder.append(list.get(i) + "=" + map.get(list.get(i))
						+ "&");
			}
			stringBuilder.append(LetvConstant.Global.ASIGN_KEY);
			String apisign = MD5.toMd5(stringBuilder.toString());

			hull = LetvHttpApi.useTicket(0, uid, channel, category, season,
					turn, gameid, "1", apisign, new UseTicketParser());
			if (hull.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY)
				return hull;

			return null;
		}

		@Override
		public void onPostExecute(int updateId, UseTicket result) {
			if (result.status.equalsIgnoreCase("1")) {
				getActivity().recreate();
			} else {
				Toast.makeText(context, "购买失败", Toast.LENGTH_LONG).show();
			}
		}
	}

	/**
	 * 请求直播券数量
	 * 
	 */
	protected class RequestTicketCount extends LetvHttpAsyncTask<TicketCount> {

		String liveid;
		String uid;
		PlayHalfPay handler;

		public RequestTicketCount(Context context, String liveid, String uid,
				PlayHalfPay ticketFrame) {
			super(context);
			this.liveid = liveid;
			this.uid = uid;
			if (uid == null || uid.equalsIgnoreCase("")) {
				Toast.makeText(context, "请登录后收看付费视频", Toast.LENGTH_LONG).show();
				this.cancel();
			}
			handler = ticketFrame;
			// TODO Auto-generated constructor stub
		}

		@Override
		public LetvDataHull<TicketCount> doInBackground() {
			if (liveid == null || liveid.length() < 16)
				return null;
			String channel = liveid.substring(0, 2);
			String category = liveid.substring(2, 5);
			String season = liveid.substring(5, 9);
			String turn = liveid.substring(9, 12);
			String gameid = liveid.substring(12, 16);
			LetvDataHull<TicketCount> hull = null;
			Map<String, String> map = new HashMap<String, String>();
			map.put("userid", uid);
			map.put("channel", channel);
			map.put("category", category);
			map.put("version", LetvConstant.Global.VERSION);
			map.put("season", season);
			map.put("pcode", LetvConstant.Global.PCODE);
			Collection<String> keyset = map.keySet();
			List<String> list = new ArrayList<String>(keyset);

			// 对key键值按字典升序排序
			Collections.sort(list);

			StringBuilder stringBuilder = new StringBuilder("");
			for (int i = 0; i < list.size(); i++) {
				System.out.println("key键---值: " + list.get(i) + ","
						+ map.get(list.get(i)));
				stringBuilder.append(list.get(i) + "=" + map.get(list.get(i))
						+ "&");
			}
			stringBuilder.append(LetvConstant.Global.ASIGN_KEY);
			String apisign = MD5.toMd5(stringBuilder.toString());

			hull = LetvHttpApi.getTicketCount(0, uid, channel, category,
					season, turn, gameid, apisign, new TicketCountParser());
			if (hull.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY)
				;

			return hull;
		}

		@Override
		public void onPostExecute(int updateId, TicketCount result) {
			if (result.count.equalsIgnoreCase("0")) {
				handler.setZeroTicket();
			} else {
				handler.setTicketCount(result.count);
			}
		}

	}

	/**
	 * 请求真实的播放地址
	 * 
	 * @author zhanglibin
	 * 
	 */
	protected class RequestRealLink extends LetvHttpAsyncTask<RealLink> {
		String url = null;
		String streamId = null;
		String token = null;
		String uid = null;

		public RequestRealLink(Context context, String streamId, String url,
				String token, String uid) {
			super(context);
			this.token = token;
			this.uid = uid;
			this.url = url;
			this.streamId = streamId;
			tasks.add(this);
		}

		public RequestRealLink(Context context, String streamId, String url) {
			super(context);
			this.url = url;
			this.streamId = streamId;
			tasks.add(this);
		}

		@Override
		public boolean onPreExecute() {
			// 播放广告
			LogInfo.log("ads", "isPlayedAd=" + isPlayedAd + "playAdFragment="
					+ playAdFragment);
			if (!isPlayedAd && playAdFragment != null) {
				timeRequestAd = System.currentTimeMillis();
				LogInfo.log("ads", "request ads");

				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						getFrontAdNormal(url, "1");
						isPlayedAd = true;
					}
				}, 500);
			}
			return true;
		}

		@Override
		public LetvDataHull<RealLink> doInBackground() {
			LetvDataHull<ExpireTimeBean> hull = null;
			if (!hasInitExpireTime) {// 更新过期时间
				hull = LetvHttpApi
						.getExpireTimestamp(0, new ExpireTimeParser());
				hasInitExpireTime = hull.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY;
			}
			String tm = null;
			if (hasInitExpireTime) {
				tm = String.valueOf(ExpireTimeBean.getTm().getCurServerTime());
			}
			if (TextUtils.isEmpty(tm)) {
				return null;
			}
			// Log.i("oyys", url);
			String newUrl = replaceTm(tm, url);
			String encryptUrl = newUrl + "&key="
					+ LetvTools.generateLiveEncryptKey(streamId, tm);
			/*************** p2p逻辑 start *****************/
			StringBuilder builder = new StringBuilder(encryptUrl);
			builder.append("&");
			builder.append("format");
			builder.append("=");
			builder.append("3");
			builder.append("&");
			builder.append("expect");
			builder.append("=");
			builder.append("1");
			if (!(token == null) && !token.equalsIgnoreCase("")) {
				builder.append("&token=" + token);
			}
			// Log.i("oyys", builder.toString());
			LeService p2pService = LetvApplication.getInstance()
					.getP2pService();
			if (isP2PMode && null != p2pService) {
				LogInfo.log("live", "isP2PMode =  true");
				// 执行p2p播放
				PlayUrl cc = new PlayUrl(p2pService.getServicePort(),
						p2pFormat(builder), "", "");
				RealLink p2pDataEntity = new RealLink();
				p2pDataEntity.setLocation(cc.getPlay());
				LetvDataHull<RealLink> p2pResult = new LetvDataHull<RealLink>();
				p2pResult.setDataType(LetvDataHull.DataType.DATA_IS_INTEGRITY);
				p2pResult.setDataEntity(p2pDataEntity);
				return p2pResult;
			}
			/**************** p2p逻辑 end ****************/
			LetvDataHull<RealLink> result = LetvHttpApi.requestRealLink(0,
					encryptUrl, new LiveRealParser());
			return result;
		}

		@Override
		public void onPostExecute(int updateId, RealLink result) {
			tasks.remove(this);
			if (result != null) {
				mRealLink = result.getLocation();
				play(result.getLocation());
			} else {
				loadLayout.cannotPlayError();
			}

		}

		@Override
		public void netErr(int updateId, String errMsg) {
			tasks.remove(this);
			statisticsVideoInfo.setErr("2");
			loadLayout.requestError();
		}

		@Override
		public void netNull() {
			tasks.remove(this);
			statisticsVideoInfo.setErr("2");
			loadLayout.requestError();
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			tasks.remove(this);
			statisticsVideoInfo.setErr("2");
			loadLayout.requestError();
		}
	}

	/**
	 * 各接口缓冲时间统计
	 * 
	 * @param mContext
	 */
	private void staticticsLoadTimeInfo(Context mContext, long adConsumeTime,
			long programListsConsumeTime, long canPlayConsumeTime,
			long realPlayUrlConsumeTime) {
		long adsTotalTime = 0;
		long adsRequestTime = 0;
		long adsLoadingTime = 0;
		if (playAdFragment != null) {
			adsTotalTime = playAdFragment.getAdsVideoTotalTime();
			adsRequestTime = playAdFragment.getAdsLoadingTime();
			adsLoadingTime = playAdFragment.getAdsPlayLoadTime();
		}
		String ad = LetvUtil.staticticsLoadTimeInfoFormat(adConsumeTime) + "";
		String pro = LetvUtil
				.staticticsLoadTimeInfoFormat(programListsConsumeTime) + "";
		String can = LetvUtil.staticticsLoadTimeInfoFormat(canPlayConsumeTime)
				+ "";
		String real = LetvUtil
				.staticticsLoadTimeInfoFormat(realPlayUrlConsumeTime) + "";
		String type10 = LetvUtil.staticticsLoadTimeInfoFormat(totalConsumeTime)
				+ "";

		LogInfo.log("live", "staticticsLoadTimeInfo adConsumeTime = " + ad
				+ " , programListsConsumeTime = " + pro
				+ " , canPlayConsumeTime = " + can
				+ " , realPlayUrlConsumeTime = " + real + " , type10 = "
				+ type10);
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("type1=" + LetvUtil.getNetType(mContext) + "&");
			sb.append("type2=1" + "&");
			sb.append("type3=" + ad + "&");
			sb.append("type4=" + pro + "&");
			sb.append("type5=0" + "&");
			sb.append("type6=" + can + "&");
			sb.append("type7=0" + "&");
			sb.append("type8=0" + "&");
			sb.append("type9=" + real + "&");
			sb.append("type10=" + type10 + "&");

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
			// "0", "", "", "", LetvUtil.getUID(), null, null, null, null,
			// PreferencesManager.getInstance().isLogin() ? 0 : 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		isUploadStatictics = true;
	}

	/**
	 * p2p拼接
	 * 
	 * @param builder
	 * @return
	 */
	private String p2pFormat(StringBuilder builder) {
		/**
		 * p2p赋予调度地址
		 */
		// 小窗播放不走P2P控制，不知道会不会引响直播 modified by zengsonghai 2010118
		// if (PreferencesManager.getInstance().getUtp()) {
		// P2P._getinstace().setISP2P(true);
		// } else {
		// P2P._getinstace().setISP2P(false);
		// }
		// if(P2P._getinstace().isP2PMode()){
		/**
		 *********************** p2p直播调度地址*************************
		 * 
		 */
		// 低端机走这方法会抛异常 LetvP2PUtils.builderURL(builder.toString());
		String[] strs = builder.toString().split("&");
		String newDdUrl = "";
		for (int i = 0; i < strs.length; i++) {
			if (!strs[i].contains("tm") && !strs[i].contains("expect")
					&& !strs[i].contains("format") && !strs[i].contains("key")) {
				newDdUrl += strs[i] + "&";
			}
		}
		newDdUrl = newDdUrl.substring(0, newDdUrl.length() - 1);
		// Log.w("VideoView", "newDdUrl : " + newDdUrl);
		return newDdUrl;
		// P2P._getinstace().setDdUrl(newDdUrl);
		/**
		 *********************** p2p直播调度地址*************************
		 */
	}

	/**
	 * 请求直播地址
	 */
	public void requestRealLink(String streamId, String url, String token,
			String uid) {
		if (mRequestRealLink != null && !mRequestRealLink.isCancelled()) {
			mRequestRealLink.cancel();
			mRequestRealLink = null;
		}
		// requestStartTime = System.currentTimeMillis();// 请求开始时间
		mRequestRealLink = new RequestRealLink(getActivity(), streamId, url,
				token, uid);
		mRequestRealLink.start();
	}

	public void requestRealLink(String streamId, String url) {
		if (mRequestRealLink != null && !mRequestRealLink.isCancelled()) {
			mRequestRealLink.cancel();
			mRequestRealLink = null;
		}
		// requestStartTime = System.currentTimeMillis();// 请求开始时间
		mRequestRealLink = new RequestRealLink(getActivity(), streamId, url);
		mRequestRealLink.start();
	}

	/**
	 * 清除请求
	 * */
	protected void destroyTasks() {
		// 清空
		for (LetvBaseTaskImpl taskImpl : tasks) {
			if (taskImpl != null && !taskImpl.isCancelled()) {
				taskImpl.cancel();
			}
		}
		tasks.clear();
	}

	public void getFrontAd(String url, String ty) {
		if (playAdFragment != null) {
			playAdFragment.getLiveFrontAd(getActivity(), url, LetvUtil
					.getUUID(getActivity()), PreferencesManager.getInstance()
					.getUserId(), "", ty);
			getActivity().getPlayFragment().setEnforcementWait(true);
		}
	}

	@Override
	public void onDestroy() {
		try {

			timeElapsed = lastTimeElapsed = 0;
			updateCount = 0;
			retryNum = 0;
			handler.removeMessages(HANDLER_TIME);
			handler.removeMessages(UPDATE_STATICICS_TIME);
			destroyTasks();
			if (tabs != null) {
				tabs.removeAllViewsInLayout();
			}
			if (viewPager != null) {
				viewPager.removeAllViewsInLayout();
			}

			loadLayout.removeAllViews();

			if (mFullController != null)
				mFullController.format();
			if (mHalfController != null)
				mHalfController.format();

			getActivity().getPlayUpper().removeAllViews();
			getActivity().getPlayLower().removeAllViews();

			tabs = null;
			viewPager = null;
			playAdFragment = null;
			viewPagerAdapter = null;
			loadLayout = null;

			super.onDestroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void toPip() {
		if (getActivity() != null) {
			getActivity().finish();
		}
		Bundle mBundle = new Bundle();
		mBundle.putBoolean("isLive", true);
		// 播放参数
		mBundle.putString(LIVE_STREAMID, mStreamId);
		mBundle.putLong(AID, aid);
		mBundle.putLong(VID, vid);
		mBundle.putString(LIVE_URL, mLiveUrl);
		mBundle.putString(LIVE_PROGRAM_NAME, mProgramName);

		// 传过去，传回来
		mBundle.putString(LIVE_CODE, mCode);
		mBundle.putInt(LIVE_MODE, getLaunchMode());
		mBundle.putSerializable(GAME, game);
		PipService.game = game;
		PipService.launch(getActivity(), mBundle);
	}

	public String getStreamIdPlaying() {
		return mStreamId;
	}

	/**
	 * 上报播放统计 mStreamId, mProgramName, mRealLink,
	 * 
	 * @param actionCode
	 */
	public void updatePlayDataStatistics(String actionCode, String streamID,
			String proName, String realUrl, long pt) {
		// LogInfo.log("zlb", "actionCode = " + actionCode + " , streamID = " +
		// streamID + " , proName = " + proName + " , realUrl = " + realUrl);

		String type = "1";
		String py = "";
		String lid = "-";
		lid = LetvApplication.getInstance().getLiveGame().id;
		String ch = LetvApplication.getInstance().getLiveGame().ch;
		String pid = mStreamId;
		String vid = "-";
		String vt = "";
		if (mCode.equalsIgnoreCase("flv_350")) {
			vt = "1";
		} else if (mCode.equalsIgnoreCase("flv_800")) {
			vt = "16";
		} else if (mCode.equalsIgnoreCase("flv_1300")) {
			vt = "17";
		}

		py = "title=" + LetvApplication.getInstance().getLiveGame().matchName
				+ "&" + "level2="
				+ LetvApplication.getInstance().getLiveGame().level + "&"
				+ "level1="
				+ LetvApplication.getInstance().getLiveGame().level0;
		// switch (getLaunchMode()) {
		// case PlayController.PLAY_LIVE_LUNBO:
		// type = "2";
		// ch = TextUtils.isEmpty(streamID) ? "-" : streamID;
		// pid = "-";
		// break;
		// case PlayController.PLAY_LIVE_WEISHI:
		// type = "1";
		// ch = "-";
		// pid = TextUtils.isEmpty(streamID) ? "-" : streamID;
		// vid = TextUtils.isEmpty(proName) ? "-" : proName;
		// break;
		// case PlayController.PLAY_LIVE_ZHIBOTING:
		// default:
		// break;
		// }
		try {
			DataStatistics.getInstance().sendPlayInfo(getActivity(), "0", "0",
					actionCode, "0", (pt > 0 ? pt : 0) + "", "-",
					LetvUtil.getUID(), LetvUtil.getUUID(getActivity()), "4",
					URLEncoder.encode(""), URLEncoder.encode(vid),
					video == null ? "-" : video.getDuration() + "", "-", type,
					vt, realUrl, "-", py, null, "-", LetvUtil.getPcode(),
					PreferencesManager.getInstance().isLogin() ? 0 : 1, ch,
					"-", lid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPlayFailed() {
		if (mLiveUrl != null) {
			playUrl(mStreamId, mLiveUrl, mToken);
		} else {
			startLoadingData();
		}
	}

	public void startLoadingData() {
		if (getLaunchMode() == PLAY_LIVE) {
			new RequestAlbum(getActivity()).start();
		}
	}

	@Override
	public Video getVideo() {
		return video;
	}

	public void setVideo(Video video) {
		this.video = video;

	}

	@Override
	public AlbumNew getAlbum() {
		return album;
	}

	public void setAlbum(AlbumNew album) {
		this.album = album;
	}

	@Override
	public boolean getVideoList(int page) {
		if (page != curPage) {
			if (videos.get(page) != null) {// 判断将要跳去的页面数据是否已经存在
				return true;
			} else {
				new RequestVideoList(getActivity(), page, aid, 0).start();
			}
		}
		return false;
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
				new RequestVideoList(context, curPage, aid, vid).start();

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
					.requestAlbumVideoInfo(0, String.valueOf(aid), "album",
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
			totle = merge == 0 ? album.getPlatformVideoInfo() : album
					.getPlatformVideoNum();// 合并与不合并总级数取不一样的字段
			isList = LetvFunction.getIsList(album.getStyle());// 初始化，是宫格合适列表
			if (!isLocalSucceed()) {
				new RequestVideoList(context, curPage, aid, vid).start();
				introductionCallBackState = PlayAlbumControllerCallBack.STATE_FINISH;
				if (introductionCallBack != null)
					introductionCallBack.notify(introductionCallBackState);
				getCommentsCallBackState = PlayAlbumControllerCallBack.STATE_FINISH;
				if (getCommentsCallBack != null)
					getCommentsCallBack.notify(getCommentsCallBackState);
			}

		}

		@Override
		public void netNull() {
			tasks.remove(this);
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
			statisticsVideoInfo.setErr("2");
		}

		@Override
		public void netErr(int updateId, String errMsg) {
			tasks.remove(this);
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
			statisticsVideoInfo.setErr("2");
			// staticticsErrorInfo(getActivity(),
			// DataConstant.ERRORCODE.REQUEST_PLAY_NET_ER_ERROR, "playerError",
			// 0, -1);
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			tasks.remove(this);
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

		private int page;

		private String markId;

		private long videoId;

		private long albumId;

		private int localDataPos;

		public RequestVideoList(Context context, int page, long aid, long vid) {
			super(context);
			tasks.add(this);
			this.page = page;
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
				setVideo(result.get(0));
				vid = video.getId();
				if (p <= 0) {
				} else if (result.getVideoPosition() > 0
						&& result.size() >= result.getVideoPosition()) {
					setVideo(result.get(result.getVideoPosition() - 1));
				}

				videos.put(curPage, result);

				videosCallBackState = PlayAlbumControllerCallBack.STATE_FINISH;
				if (videosCallBack != null)
					videosCallBack.notify(videosCallBackState);

				return true;
			}

			return false;
		}

		@Override
		public LetvDataHull<VideoList> doInBackground() {

			VideoListParser parser = new VideoListParser();
			LetvDataHull<VideoList> dataHull = LetvHttpApi
					.requestAlbumVideoList(0, String.valueOf(albumId), "0",
							String.valueOf(page), String.valueOf(pageSize),
							order, String.valueOf(merge), markId, parser);

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
			setVideo(result.get(0));
			vid = -1;
			if (p <= 0) {
			} else if (result.getVideoPosition() > 0
					&& result.size() >= result.getVideoPosition()) {
				setVideo(result.get(result.getVideoPosition() - 1));
			}

			videos.put(curPage, result);

			videosCallBackState = PlayAlbumControllerCallBack.STATE_FINISH;
			if (videosCallBack != null)
				videosCallBack.notify(videosCallBackState);
		}

		@Override
		public void netNull() {
			tasks.remove(this);
			videosCallBackState = PlayAlbumControllerCallBack.STATE_NET_NULL;
			if (videosCallBack != null)
				videosCallBack.notify(videosCallBackState);

		}

		@Override
		public void netErr(int updateId, String errMsg) {
			tasks.remove(this);

			videosCallBackState = PlayAlbumControllerCallBack.STATE_NET_ERR;
			if (videosCallBack != null)
				videosCallBack.notify(videosCallBackState);
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			tasks.remove(this);

			videosCallBackState = PlayAlbumControllerCallBack.STATE_DATA_NULL;
			if (videosCallBack != null)
				videosCallBack.notify(videosCallBackState);
		}

		@Override
		public void noUpdate() {
			tasks.remove(this);
			super.noUpdate();
		}
	}

	@Override
	public void onUseTicket(int status) {
		if (status == 0) {
			Uri uri = Uri
					.parse("http://yuanxian.letv.com/zt2014/yingchaofufeizhuanti/index.shtml");
			Intent payWebView = new Intent(Intent.ACTION_VIEW, uri);
			getActivity().startActivity(payWebView);
		} else {
			RequestUseTicket requestUseTicket = new RequestUseTicket(
					this.getActivity(), game.liveid, PreferencesManager
							.getInstance().getUserId());
			requestUseTicket.start();
		}
	}

	@Override
	public void onLogin() {
		LoginMainActivity.launch(this.getActivity().getPlayFragment());
	}

	@Override
	public void onBuyTicket() {
		Uri uri = Uri
				.parse("http://yuanxian.letv.com/zt2014/yingchaofufeizhuanti/index.shtml");
		Intent payWebView = new Intent(Intent.ACTION_VIEW, uri);
		getActivity().startActivity(payWebView);
	}

	/**
	 * 全屏播放器下播放付费视频
	 * 
	 * @param streamId
	 *            视频流
	 * @param liveUrl
	 *            地址
	 * @param steam_code
	 *            视频码率
	 */
	public void playLivePayUrl(String streamId, String liveUrl, int stream_code) {
		String pid = game.id;
		String liveid = game.liveid;
		String from = "mobile";
		String userId = PreferencesManager.getInstance().getUserId();
		String splatId = "1013";
		String lsstart = String.valueOf(game.status);
		String token;
		if (stream_code == 350) {
			if (payToken_live350.equalsIgnoreCase(""))
				payToken_live350 = requestDynamicCheck(pid, liveid, from,
						streamId, splatId, userId, lsstart);

			token = payToken_live350;
		} else {
			if (payToken_live800.equalsIgnoreCase(""))
				payToken_live800 = requestDynamicCheck(pid, liveid, from,
						streamId, splatId, userId, lsstart);
			token = payToken_live800;
		}
		playUrl(streamId, liveUrl, token);
	}
}
