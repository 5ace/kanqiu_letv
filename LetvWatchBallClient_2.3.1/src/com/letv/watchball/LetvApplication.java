package com.letv.watchball;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;

import com.letv.ads.AdsManager;
import com.letv.cache.LetvCacheMannager;
import com.letv.datastatistics.entity.DataStatusInfo;
import com.letv.pp.service.LeService;
import com.letv.watchball.bean.Game;
import com.letv.watchball.bean.IP;
import com.letv.watchball.bean.LetvThumbnailImpl;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.manager.FragmentManager;
import com.letv.watchball.push.LetvWbPushService;
import com.letv.watchball.utils.CrashHandler;
import com.letv.watchball.utils.LetvConfiguration;
import com.letv.watchball.utils.LetvConstant;
import com.letv.watchball.utils.LetvUtil;
import com.media.NativeInfos;

public class LetvApplication extends Application {

	private static LetvApplication instance;
	private boolean is3GTip_forPlay;
	private String videoFormat = "mp4";
	private boolean isLiveUrl_350 = false;
	private boolean isRequestIpSuccess = false;
	private boolean isIpValid = false;
	private boolean showVideoList=false;
	private String deviceID = null;

	private boolean isUseTest = false;

	private boolean isShowAdvertisement = false;

	private boolean isPinAdvertisement = false;

	private boolean isRequestApiSuccess = false;

	private DataStatusInfo mDataStatusInfo = null;
    
	private FragmentManager manager;

	private boolean is3GTip_forDownload = false;

	private int isShowChannelRecommend ;//0 不显示 //1显示，无推荐 // 2显示，有推荐

	private int isShowLiveRecommend ;

	private int isShowDownloadRecommend ;

	private int isShowSettingRecommend ;

	private int recommendNum ;
	
	WifiLock mWifiLock;
	/**
	 * 异常处理关闭
	 */
	private boolean isAlowThrowException = false;
	
	/**
	 * 是否强制升级
	 */
	private boolean isForceUpdating;
	/**
	 * iP信息，会在每次网络变换的时候更新
	 * */
	private IP ip;
	/**
	 * 用户登录时间
	 */
	private long logInTime = 0;
	public static boolean DEBUG_GET_INFO = LetvUtil.isDebug();// 打印出联网数据
      //  世界杯开关，非测试请置为 ### false ###
      private boolean showWorldCup = false;
      private Game game;
      private LeService p2pService=null;//p2p 的service每次开机后，开启即可

      @Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		setVType();
		LetvHttpApi.initialize(LetvConstant.Global.PCODE, LetvConstant.Global.VERSION,LetvUtil.generateDeviceId(this));
		/**
		 * 启动推送
		 * */

            if ("com.letv.watchball".equalsIgnoreCase(getCurProcessName(this))) {
//			PreferencesManager.getInstance().setisPlayCloud(false);
                  if (PreferencesManager.getInstance().isGameResultRemind()) {
                        LetvWbPushService.schedule(this);
                  } else {
                        LetvWbPushService.unschedule(this);
                  }
            }
		CrashHandler mCrashHandler = CrashHandler.getInstance();
            initAds();
		if(isAlowThrowException){
			mCrashHandler.init(getApplicationContext());
		}

            LetvCacheMannager.getInstance().init(this,new LetvThumbnailImpl());
	}

      private void initAds() {
            // 初始化广告包
            AdsManager.getInstance().initAd(getApplicationContext(), LetvConfiguration.isDebug(), LetvConfiguration.isDebug());

      }

      public LeService getP2pService() {
            return p2pService;
      }

      public void setP2pService(LeService p2pService) {
            this.p2pService = p2pService;
      }

      public static LetvApplication getInstance(){
		return instance;
	}
	
	
	private boolean setVType() {
		int supportLevel = NativeInfos.getSupportLevel();
		if (supportLevel == NativeInfos.SUPPORT_MP4_LEVEL || !NativeInfos.ifSupportVfpOrNeon()) {
			// 请求mp4资源
			videoFormat = "no";
			isLiveUrl_350 = true;
		} else if (supportLevel == NativeInfos.SUPPORT_TS350K_LEVEL) {
			// 请求350k ts资源
			videoFormat = "ios";
			isLiveUrl_350 = true;
		} else if (supportLevel == NativeInfos.SUPPORT_TS800K_LEVEL) {
			// 请求800k ts资源
			videoFormat = "ios";
			isLiveUrl_350 = false;
		}else if (supportLevel == NativeInfos.SUPPORT_TS1000K_LEVEL) {
			// 请求1000k ts资源
			videoFormat = "ios";
			isLiveUrl_350 = false;
		}else{
			Log.i("videotype", "videotype");
			videoFormat = "ios";
			isLiveUrl_350 = true;
		}
		return isLiveUrl_350;
	}
	
	public FragmentManager getManager() {
		return manager;
	}

	public void setManager(FragmentManager manager) {
		this.manager = manager;
	}

	public String getVideoFormat() {
		return videoFormat;
	}
	
	
	
	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public boolean isLiveUrl_350() {
		return isLiveUrl_350;
	}

	public boolean isRequestApiSuccess() {
		return isRequestApiSuccess;
	}

	public void setRequestApiSuccess(boolean isRequestApiSuccess) {
		this.isRequestApiSuccess = isRequestApiSuccess;
	}

	public boolean isShowAdvertisement() {
		return isShowAdvertisement;
	}

	public void setShowAdvertisement(boolean isShowAdvertisement) {
		this.isShowAdvertisement = isShowAdvertisement;
	}
	
	public boolean isPinAdvertisement() {
		return isPinAdvertisement;
	}

	public void setPinAdvertisement(boolean isPinAdvertisement) {
		this.isPinAdvertisement = isPinAdvertisement;
	}

	public boolean isIpValid() {
		return isIpValid;
	}

	public void setIpValid(boolean isIpValid) {
		this.isIpValid = isIpValid;
	}

	public boolean isRequestIpSuccess() {
		return isRequestIpSuccess;
	}

	public void setRequestIpSuccess(boolean isRequestIpSuccess) {
		this.isRequestIpSuccess = isRequestIpSuccess;
	}

	public boolean isUseTest() {
		return isUseTest;
	}

	public void setUseTest(boolean isUseTest) {
		this.isUseTest = isUseTest;
	}

	public DataStatusInfo getDataStatusInfo() {
		return mDataStatusInfo;
	}

	public void setDataStatusInfo(DataStatusInfo mDataStatusInfo) {
		this.mDataStatusInfo = mDataStatusInfo;
	}

	public boolean is3GTip_forPlay() {
		return is3GTip_forPlay;
	}

	public void setIs3GTip_forPlay(boolean is3gTip_forPlay) {
		is3GTip_forPlay = is3gTip_forPlay;
	}

	public boolean is3GTip_forDownload() {
		return is3GTip_forDownload;
	}

	public void setIs3GTip_forDownload(boolean is3gTip_forDownload) {
		is3GTip_forDownload = is3gTip_forDownload;
	}
	
	
	/**
	 * 是否显示频道页的推荐位
	 * */
	public int isShowChannelRecommend() {
		return isShowChannelRecommend;
	}

	/**
	 * 设置是否显示频道页的推荐位
	 * */
	public void setShowChannelRecommend(int isShowChannelRecommend) {
		this.isShowChannelRecommend = isShowChannelRecommend;
	}

	/**
	 * 是否显示直播页推荐位
	 * */
	public int isShowLiveRecommend() {
		return isShowLiveRecommend;
	}

	/**
	 * 设置是否显示直播页的推荐位
	 * */
	public void setShowLiveRecommend(int isShowLiveRecommend) {
		this.isShowLiveRecommend = isShowLiveRecommend;
	}

	/**
	 * 是否显示下载页推荐位
	 * */
	public int isShowDownloadRecommend() {
		return isShowDownloadRecommend;
	}

	/**
	 * 设置是否显示下载页的推荐位
	 * */
	public void setShowDownloadRecommend(int isShowDownloadRecommend) {
		this.isShowDownloadRecommend = isShowDownloadRecommend;
	}

	/**
	 * 是否显示我的乐视的推荐位
	 * */
	public int isShowSettingRecommend() {
		return isShowSettingRecommend;
	}

	/**
	 * 设置我的乐视页是否显示推荐位
	 * */
	public void setShowSettingRecommend(int isShowSettingRecommend) {
		this.isShowSettingRecommend = isShowSettingRecommend;
	}
	
	/**
	 * 设置推荐位的推荐数
	 * */
	public void setRecommendNum(int recommendNum){
		this.recommendNum = recommendNum ;
	}
	
	/**
	 * 得到推荐位的推荐数
	 * */
	public int getRecommendNum(){
		return recommendNum ;
	}
	
	public boolean isForceUpdating() {
		return isForceUpdating;
	}

	public void setForceUpdating(boolean isForceUpdating) {
		this.isForceUpdating = isForceUpdating;
	}
	public IP getIp() {
		return ip;
	}

	public void setIp(IP ip) {
		this.ip = ip;
	}
	
	/**
	 * 得到当前进程的名字
	 * */
	private String getCurProcessName(Context context) {
		int pid = android.os.Process.myPid();
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
				.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {

				return appProcess.processName;
			}
		}
		return null;
	}
	
	public long getLogInTime() {
		return logInTime;
	}

	public void setLogInTime(long logInTime) {
		this.logInTime = logInTime;
	}

      public void setShowWorldCup(boolean showWorldCup) {
            this.showWorldCup = showWorldCup;
      }

      public boolean isShowWorldCup() {
            return showWorldCup;
      }

      public void saveLiveGame(Game game) {
            this.game = game;
      }

      public Game getLiveGame(){
            return game;
      }

	public boolean isShowVideoList() {
		return showVideoList;
	}

	public void setShowVideoList(boolean showVideoList) {
		this.showVideoList = showVideoList;
	}
      
      
}
