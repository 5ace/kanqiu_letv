package com.letv.ads;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Rect;

import com.letv.adlib.managers.status.ad.AdStatusManager;
import com.letv.adlib.managers.status.video.IVideoStatusInformer;
import com.letv.adlib.model.ad.common.CommonAdItem;
import com.letv.adlib.model.services.CommonAdDataService;
import com.letv.ads.db.AdsDBHandler;
import com.letv.ads.http.AdsHttpApi;
import com.letv.ads.http.LetvSimpleAsyncTask;
import com.letv.ads.util.Commons;
import com.letv.ads.util.DataUtils;
import com.letv.ads.util.LogInfo;
import com.letv.ads.util.SearchKeyWordCallBack;

/**
 * 静态图片广告管理类
 * */
public class AdsManager {

      private static boolean isInit = false;
      private boolean isShowAd = true;

	private static AdsManager instance;

	private Context context;

	/**
	 * 开机引导图广告
	 * */
	private CommonAdItem beginAdInfo;

	/**
	 * 焦点图第三张
	 * */
	private ArrayList<CommonAdItem> focusAdInfo;

	/**
	 * 搜索全屏广告
	 * */
	// private CommonAdItem fullbackAdInfo;

	private VipCallBack vipCallBack;

	private VideoCallBack videoCallBack;

	private AdsManager() {

	}

	public synchronized static AdsManager getInstance() {
		if (instance == null) {
			instance = new AdsManager();
		}

		return instance;
	}

	private IVideoStatusInformer informer;

	/**
	 * 初始化广告所需数据
	 * 
	 * @param context
	 *            Application context
	 * @param version
	 *            版本号
	 * @param pcode
	 *            PCODE
	 * @param source
	 *            平台
	 * @param test
	 *            是否测试接口
	 */
	public void initAdData(Context context, String kv, String platform, String version, String pcode, boolean test) {
		this.context = context;
		Commons.VERSION = version;
		Commons.PCODE = pcode;
		Commons.KV = kv;
		Commons.PLATFORM = platform;
		Commons.MAC = DataUtils.getMacAddress(context);
		Commons.DEVICE = DataUtils.getDeviceName();
		Commons.DEVICE_INFO = DataUtils.getSystemName() + "_" + DataUtils.getBrandName() + "_" + Commons.DEVICE;
		Commons.DEVICE_ID = DataUtils.generateDeviceId(context);
		Commons.PVERSION = DataUtils.getPVersion(Commons.VERSION);
		Commons.TEST = test;

		SharedPreferences preferences = context.getSharedPreferences("ad_srtting", Context.MODE_PRIVATE);
		isShowAd = preferences.getBoolean("isShow", false);
	}

	/**
	 * 得到上一次的开机图
	 * */
	public CommonAdItem getBeginAdInfo() {

		if (beginAdInfo != null) {
			return beginAdInfo;
		}

		ArrayList<CommonAdItem> ads = AdsDBHandler.getAd(context, "begin");

		if (ads != null && ads.size() > 0) {
			beginAdInfo = ads.get(0);
		}
            LogInfo.log("ads", "----------end----");
		updateBeginAdInfo();

		return beginAdInfo;
	}

	/**
	 *  上报付费影片广告？
	 * @param mContext
	 * @param cid
	 * @param aid
	 * @param vid
	 * @param mmsid
	 * @param uuid
	 * @param uid
	 * @param vlen
	 * @param py
	 * @param ty
	 * @param isVipVideo
	 * @param disableAvd
	 */
	public void getFrontVipAd(Context mContext, int cid, long aid, long vid, String mmsid, String uuid,
			String uid, String vlen, String py, String ty, boolean isVipVideo, boolean disableAvd) {
		AdsHttpApi.getFrontVipAd(mContext, cid+"", aid+"",  vid+"" ,  mmsid ,  uuid ,  uid ,  vlen ,  py ,  ty , null, true, isVipVideo, disableAvd);
	}

	/**
	 * 开机广告更新
	 * */
	public void updateBeginAdInfo() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				checkData(AdsHttpApi.getBeginImgAd(context), "begin");
			}
		};
		thread.start();
	}

	/**
	 * 得到焦点图广告
	 * */
	public ArrayList<CommonAdItem> getFocusAdInfo() {
		if (focusAdInfo != null) {
			return focusAdInfo;
		}
		focusAdInfo = AdsHttpApi.getFocusImgAd(context);
		return focusAdInfo;
	}

	/**
	 * 得到顶部通栏广告
	 * */
	public CommonAdItem getBannerAdInfo(String cid, String pid) {
		CommonAdItem bannerAdInfo = checkData(AdsHttpApi.getTopBannerAd(context, cid, pid), "banner");
		return bannerAdInfo;
	}

	/**
	 * 检查结果数据是否正常
	 * */
	private CommonAdItem checkData(ArrayList<CommonAdItem> ads, String ad) {
		if ("begin".equals(ad) && AdsDBHandler.has(context, ad)) {
			AdsDBHandler.remove(context, ad);
		}
		if (ads != null && ads.size() > 0) {
			CommonAdItem adItem = ads.get(0);

			if ("begin".equals(ad)) {
				AdsDBHandler.saveAd(context, ad, adItem);
			}

			return adItem;
		}

		return null;
	}

	/**
	 * 是否显示广告，用初始化接口设置
	 * */
	public boolean isShowAd() {
            SharedPreferences preferences = context.getSharedPreferences("ad_srtting", Context.MODE_PRIVATE);
            boolean isShow = preferences.getBoolean("isShow", true);
            isShowAd = isShow;
            return isShowAd;
	}

	/**
	 * 初始化是否显示广告
	 * */
	public void setShowAd(boolean isShowAd) {
		SharedPreferences preferences = context.getSharedPreferences("ad_srtting", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean("isShow", isShowAd);
		editor.commit();
		this.isShowAd = isShowAd;
	}

	public VipCallBack getVipCallBack() {
		return vipCallBack;
	}

	public void setVipCallBack(VipCallBack vipCallBack) {
		this.vipCallBack = vipCallBack;
	}

	public VideoCallBack getVideoCallBack() {
		return videoCallBack;
	}

	public void setVideoCallBack(VideoCallBack videoCallBack) {
		this.videoCallBack = videoCallBack;
	}

	public boolean isVip() {
		if (vipCallBack != null) {
			return vipCallBack.isVip();
		}

		return false;
	}

      public void setInit (boolean b) {
            isInit = b;
      }

      public boolean isInit (){
            return isInit;
      }

      public interface VipCallBack {
		public boolean isVip();
	}

	public interface VideoCallBack {
		/**
		 * 执行暂停正片动作
		 */
		public void pauseVideo();

		/**
		 * 执行继续播放正片动作
		 */
		public void resumeVideo();

		/**
		 * 获取播放器显示区域
		 * 
		 * @return
		 */
		public Rect getPlayerRect();
	}

	/**
       *
       * 初始化广告配置信息 ， 需要在开机读到接口时调用
       *
       * @param config
       *            配置节点 格式aa,bb;cc,dd;ee,ff;
       *
       * @param useTestServer
       *            是否开始测试模式，默认为线上模式
       *
       * @param showLog
       *            是否开启LOG，默认不开启
       */
	public void initAd(Context context, boolean useTestServer, boolean showLog) {
            try{
                  new CommonAdDataService().initAd(context, useTestServer, showLog);
            }catch (IllegalArgumentException iae){
                  iae.printStackTrace();
            }
	}

	/**
	 * 传入广告配置信息
	 * 
	 * @param config
	 */
	public void initRemoteConfig(String config) {
		new CommonAdDataService().initRemoteConfig(config);
	}

	/**
	 * 请求搜索框广告
	 */
	private ArrayList<CommonAdItem> getSearchKeyWord(Context context) {
		return AdsHttpApi.getSearchKeyWord(context);
	}

	/**
	 * 搜索框广告成功 曝光
	 * 
	 * @param mCommonAdItem
	 */
	private void searchKeyWordExposure(CommonAdItem mCommonAdItem) {
		if (mCommonAdItem != null) {
			new AdStatusManager(mCommonAdItem).onAdPlayStart();
		}
	}

	/**
	 * 请求点播暂停
	 * */
	private class RequestSearchKeyWord extends LetvSimpleAsyncTask<CommonAdItem> {
		Context context;
		SearchKeyWordCallBack mCallBack;

		public RequestSearchKeyWord(Context context, SearchKeyWordCallBack mCallBack) {
			super(context);
			this.context = context;
			this.mCallBack = mCallBack;
		}

		@Override
		public CommonAdItem doInBackground() {
			if (isCancel()) {
				return null;
			}
			ArrayList<CommonAdItem> searchKeyWord = getSearchKeyWord(context);
			if (searchKeyWord != null && searchKeyWord.size() > 0) {
				return searchKeyWord.get(0);
			}
			return null;
		}

		@Override
		public void onPostExecute(CommonAdItem result) {
			if (result != null) {
				searchKeyWordExposure(result);
				if (mCallBack != null) {
					mCallBack.updateSearchTextView(result.message);
				}
			}
		}

		@Override
		public void onPreExecute() {

		}
	}

	private RequestSearchKeyWord mRequestSearchKeyWord;

	public void requestSearchKeyWord(Context context, SearchKeyWordCallBack mSearchKeyWordCallBack) {
		cancellSearchKeyWord();
		mRequestSearchKeyWord = new RequestSearchKeyWord(context, mSearchKeyWordCallBack);
		mRequestSearchKeyWord.start();

	}

	public void cancellSearchKeyWord() {
		if (mRequestSearchKeyWord != null) {
			mRequestSearchKeyWord.cancel(true);
		}
		mRequestSearchKeyWord = null;
	}
}
