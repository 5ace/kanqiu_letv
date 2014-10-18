package com.letv.ads.http;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;

import com.letv.adlib.managers.debugger.ARKDebugManager;
import com.letv.adlib.managers.status.ad.AdStatusManager;
import com.letv.adlib.managers.status.ad.IPlayerStatusDelegate;
import com.letv.adlib.model.ad.common.AdInfo;
import com.letv.adlib.model.ad.common.CommonAdItem;
import com.letv.adlib.model.ad.types.LetvVideoAdZoneType;
import com.letv.adlib.model.ad.types.UserLogErrorType;
import com.letv.adlib.model.request.SimpleAdReqParams;
import com.letv.adlib.model.services.CommonAdDataService;
import com.letv.adlib.model.video.MobileAppClientInfo;
import com.letv.ads.util.ClientInfoUtil;
import com.letv.ads.util.LogInfo;

public class AdsHttpApi {

	/**
	 * 调用方舟SDK得到广告，同步耗时
	 * */
	private static ArrayList<CommonAdItem> getAd(SimpleAdReqParams reqParam) {
		if (reqParam == null || reqParam.clientInfo == null || TextUtils.isEmpty(reqParam.azType)) {
			ARKDebugManager.showArkErrorInfo("广告参数异常", null);
			return null;
		}
		ArrayList<CommonAdItem> adInfos = null;
		try {
			adInfos = new CommonAdDataService().getAdData(reqParam);
		} catch (Exception e) {
			ARKDebugManager.showArkErrorInfo("获取广告数据出错", e);
		}

		return adInfos;
	}
	/**
	 * 请求前贴专用   调用方舟SDK得到广告，同步耗时
	 * */
	private static AdInfo getFrontAd(SimpleAdReqParams reqParam) {
		if (reqParam == null || reqParam.clientInfo == null || TextUtils.isEmpty(reqParam.azType)) {
			ARKDebugManager.showArkErrorInfo("广告参数异常", null);
			return null;
		}
		AdInfo adWithInformer = null;
		try {
			adWithInformer = new CommonAdDataService().getAdWithInformer(reqParam);
		} catch (Exception e) {
			ARKDebugManager.showArkErrorInfo("获取广告数据出错", e);
		}
		
		return adWithInformer;
	}

	/**
	 * 请求前贴片广告
	 */
	public static ArrayList<CommonAdItem> getFrontAd(Context context, String cid, String pid, String vid , String mmsid , String uuid , String uid , String vlen , String py , String ty , IPlayerStatusDelegate iPlayerStatusDelegate) {
		MobileAppClientInfo clientInfo = ClientInfoUtil.getVideoPlayerInfo(context, cid, pid, vid, mmsid, uuid, uid, vlen, py , ty , iPlayerStatusDelegate);

		SimpleAdReqParams reqParam = new SimpleAdReqParams();
		reqParam.clientInfo = clientInfo;
		reqParam.azType = LetvVideoAdZoneType.PREROLL.value();// 前帖片
		return getAd(reqParam);
	}
	/**
	 * modified by zengsonghai 20140304
	 * 请求多个前贴片广告 needMultiPreRoll  是：true 否：false;
	 */
	public static AdInfo getFrontAd(Context context, String cid, String pid, String vid , String mmsid , String uuid , String uid , String vlen , String py , String ty , IPlayerStatusDelegate iPlayerStatusDelegate, boolean needMultiPreRoll, boolean isVipVideo, boolean disableAvd) {
//		vid = "letvtest01";
//		pid = "group_1";
		MobileAppClientInfo clientInfo = ClientInfoUtil.getVideoPlayerInfo(context, cid, pid, vid, mmsid, uuid, uid, vlen, py , ty , iPlayerStatusDelegate, isVipVideo);
		
		SimpleAdReqParams reqParam = new SimpleAdReqParams();
		reqParam.clientInfo = clientInfo;
		reqParam.disableAVD = disableAvd;//AVD开关
		reqParam.azType = LetvVideoAdZoneType.PREROLL.value();// 前帖片
		reqParam.needMultiPreRoll = needMultiPreRoll;
		return getFrontAd(reqParam);
	}
	/**
	 * 上报付费影片广告？
	 * @param context
	 * @param cid
	 * @param pid
	 * @param vid
	 * @param mmsid
	 * @param uuid
	 * @param uid
	 * @param vlen
	 * @param py
	 * @param ty
	 * @param iPlayerStatusDelegate
	 * @param needMultiPreRoll
	 * @param isVipVideo
	 * @param disableAvd
	 */
	public static void getFrontVipAd(Context context, String cid, String pid, String vid , String mmsid , String uuid , String uid , String vlen , String py , String ty , IPlayerStatusDelegate iPlayerStatusDelegate, boolean needMultiPreRoll, boolean isVipVideo, boolean disableAvd) {
		MobileAppClientInfo clientInfo = ClientInfoUtil.getVideoPlayerInfo(context, cid, pid, vid, mmsid, uuid, uid, vlen, py , ty , iPlayerStatusDelegate, isVipVideo);
		
		SimpleAdReqParams reqParam = new SimpleAdReqParams();
		reqParam.clientInfo = clientInfo;
		reqParam.disableAVD = disableAvd;//AVD开关
		reqParam.azType = LetvVideoAdZoneType.PREROLL.value();// 前帖片
		reqParam.needMultiPreRoll = needMultiPreRoll;
		AdStatusManager adStatusManager = new AdStatusManager(clientInfo, "2");
		adStatusManager.OnAcComplate(UserLogErrorType.NO_AD);
	}

	/**
	 * 请求直播前贴片广告
	 */
	public static ArrayList<CommonAdItem> getLiveFrontAd(Context context, String streamUrl , String uuid , String uid , String py, String ty ,IPlayerStatusDelegate iPlayerStatusDelegate) {
		MobileAppClientInfo clientInfo = ClientInfoUtil.getLivePlayerInfo(context, streamUrl , uuid, uid , ty ,iPlayerStatusDelegate);

		SimpleAdReqParams reqParam = new SimpleAdReqParams();
		reqParam.clientInfo = clientInfo;
		reqParam.azType = LetvVideoAdZoneType.PREROLL.value();// 前贴片

		return getAd(reqParam);
	}
	/**
	 * 请求直播前贴片广告
	 */
	public static ArrayList<CommonAdItem> getLiveFrontAd(Context context, String streamUrl , String uuid , String uid , String py, String ty ,IPlayerStatusDelegate iPlayerStatusDelegate, boolean needMultiPreRoll) {
		MobileAppClientInfo clientInfo = ClientInfoUtil.getLivePlayerInfo(context, streamUrl , uuid, uid , ty ,iPlayerStatusDelegate);
		LogInfo.log("songAds", "context=" + context + "clientInfo=" + clientInfo);
		SimpleAdReqParams reqParam = new SimpleAdReqParams();
		reqParam.clientInfo = clientInfo;
		reqParam.azType = LetvVideoAdZoneType.PREROLL.value();// 前贴片
		reqParam.needMultiPreRoll = needMultiPreRoll;
		return getAd(reqParam);
	}

	/**
	 * 请求暂停广告
	 */
	public static ArrayList<CommonAdItem> getPauseAd(Context context, String cid, String pid, String vid , String mmsid , String uuid , String uid , String vlen , String py , String ty , IPlayerStatusDelegate iPlayerStatusDelegate) {
		MobileAppClientInfo clientInfo = ClientInfoUtil.getVideoPlayerInfo(context, cid, pid, vid, mmsid, uuid, uid, vlen, py , ty , iPlayerStatusDelegate);

		SimpleAdReqParams reqParam = new SimpleAdReqParams();
		reqParam.clientInfo = clientInfo;
		reqParam.azType = LetvVideoAdZoneType.PAUSE.value();// 暂停

		return getAd(reqParam);
	}

	/**
	 * 请求移动端开机启动图广告
	 */
	public static ArrayList<CommonAdItem> getBeginImgAd(Context context) {

		MobileAppClientInfo clientInfo = ClientInfoUtil.getBeginInfo(context);

		SimpleAdReqParams reqParam = new SimpleAdReqParams();
		reqParam.clientInfo = clientInfo;
		reqParam.azType = LetvVideoAdZoneType.SPLASH_SCREEN.value();// 开机启动图

		return getAd(reqParam);
	}

	/**
	 * 请求移动端焦点图第三帧广告
	 */
	public static ArrayList<CommonAdItem> getFocusImgAd(Context context) {
		MobileAppClientInfo clientInfo = ClientInfoUtil.getFocusInfo(context);

		SimpleAdReqParams reqParam = new SimpleAdReqParams();
		reqParam.clientInfo = clientInfo;
		reqParam.azType = LetvVideoAdZoneType.FOCUS.value();// 焦点图

		return getAd(reqParam);
	}

	/**
	 * 请求移动端详情页顶部banner广告
	 */
	public static ArrayList<CommonAdItem> getTopBannerAd(Context context, String cid, String pid) {
		MobileAppClientInfo clientInfo = ClientInfoUtil.getBannerInfo(context , cid , pid);

		SimpleAdReqParams reqParam = new SimpleAdReqParams();
		reqParam.clientInfo = clientInfo;
		reqParam.azType = LetvVideoAdZoneType.BANNER.value();// banner广告

		return getAd(reqParam);
	}
	/**
	 * 请求搜索框广告
	 */
	public static ArrayList<CommonAdItem> getSearchKeyWord(Context context) {
		MobileAppClientInfo clientInfo = ClientInfoUtil.getFocusInfo(context);
		SimpleAdReqParams reqParam = new SimpleAdReqParams();
        reqParam.clientInfo = clientInfo;
        //搜索框关键词广告
        reqParam.azType = LetvVideoAdZoneType.KEYWORD.value();
        return getAd(reqParam);
	}
}
