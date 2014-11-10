package com.letv.watchball;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager.WifiLock;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.letv.ads.AdsManager;
import com.letv.ads.util.LogInfo;
import com.letv.android.lcm.LetvPushManager;
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

      //推送相关
      Context mApplicationContext;
      String regid;
  	private static final String TAG = "Letv";

  	public static final String PROPERTY_REG_ID = "registration_id";
    /**
     * Substitute you own sender ID here. you got from the push server,
     * Most applications use a single sender ID. You may use multiple senders 
     * if different servers may send messages to the app.
     */
    private static final String SENDER_ID = "WpAq";
    /**
     * Substitute you own app ID here, you got from the push server,
     * it identify the application like package name.
     */
    private static final String APP_ID = "JbMz";
    
	private static final String SHARED_PREFERENCE_FILE = "device_token";
	private LetvPushManager mLpm;
	
      @Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		mApplicationContext = getApplicationContext();

		setVType();
		LetvHttpApi.initialize(LetvConstant.Global.PCODE, LetvConstant.Global.VERSION,LetvUtil.generateDeviceId(this));
		/**
		 * 启动推送
		 * */
		/* 将http轮询方式的推送换成socket推送
            if ("com.letv.watchball".equalsIgnoreCase(getCurProcessName(this))) {
                  if (PreferencesManager.getInstance().isGameResultRemind()) {
                        LetvWbPushService.schedule(this);
                  } else {
                        LetvWbPushService.unschedule(this);
                  }
            }
        */
		regid = getRegistrationId(mApplicationContext);
		LogInfo.log("push token:" + regid.toString());
        if (regid.isEmpty()) {
        		//开始注册推送服务
            registerInBackground();
        }else{
        		Log.i(TAG, "application already register");
        }
        this.getTokenInBackground();
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
      
    /*
     * 推送相关
     */
	/**
     * Gets the current registration ID for application on LCM service, if there is one.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getLcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        return registrationId;
    }
    /**
     * Stores the registration ID  in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getLcmPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.commit();
    }
    
    private void removeRegistrationId(Context context){
    	final SharedPreferences prefs = getLcmPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, "");
        editor.commit();
    }
    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getLcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(SHARED_PREFERENCE_FILE,
                Context.MODE_PRIVATE);
    }
    
    /**
     * Registers the application with LCM servers asynchronously.
     * <p>
     * Stores the registration ID  in the application's shared preferences.
     * there are two error messages will return when throw exception
     * {@link LetvPushManager#ERROR_MAIN_THREAD}}
     * {@link LetvPushManager#ERROR_SERVICE_NOT_AVAILABLE}}
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (mLpm == null) {
                        mLpm = LetvPushManager.getInstance(mApplicationContext);
                    }
                    regid = mLpm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;
                    Log.d("gongmeng", msg);
                    // You should send the registration ID to your server over HTTP, so it
                    // can  send messages to your app.
                    sendRegistrationIdToBackend();

                    // Persist the regID - no need to register again.
                    storeRegistrationId(mApplicationContext, regid);
                } catch (Exception ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i(TAG, msg);
            }
        }.execute(null, null, null);
    }
    
    /**
     * Sends the registration ID(device token ) to your server over HTTP, so it can send
     * messages to your app..
     */
    private void sendRegistrationIdToBackend() {
      // Your implementation here.
    }
/**
 * there are two error messages will return when throw exception
 * {@link LetvPushManager#ERROR_MAIN_THREAD}}
 * {@link LetvPushManager#ERROR_SERVICE_NOT_AVAILABLE}}
 */
    private void unregisterInBackground(){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "UNREGISTER_SUCCESS";
                try {
                    if (mLpm == null) {
                        mLpm = LetvPushManager.getInstance(mApplicationContext);
                    }
                    // unregister the application,will cause all sender id unregister
                    //if you have more than one sender id,then you can unregister
                    //one of them.call below method
                    //mLpm.unRegister(APP_ID, SENDER_ID);
                     mLpm.unRegister();
                } catch (Exception ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to unregister.
                   
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
            	 Log.d(TAG,"msg="+msg);
                //you should delete the device token that you save in shared preference
                //so that it will check register again when application restart.
                if("UNREGISTER_SUCCESS".equals(msg)){
                	removeRegistrationId(mApplicationContext);
                }
            }
        }.execute(null, null, null);
    
    }
    
    private void getTokenInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (mLpm == null) {
                        mLpm = LetvPushManager.getInstance(mApplicationContext);
                    }
                    regid = mLpm.getDeviceToken();
                    msg = "Device registered, registration ID=" + regid;
                    LogInfo.log(msg);
                    sendRegistrationIdToBackend();

                    // Persist the regID - no need to register again.
                    storeRegistrationId(mApplicationContext, regid);
                } catch (Exception ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }
            @Override
            protected void onPostExecute(String msg) {
            	 Log.i(TAG, msg);
            }
        }.execute(null, null, null);
    }
}
