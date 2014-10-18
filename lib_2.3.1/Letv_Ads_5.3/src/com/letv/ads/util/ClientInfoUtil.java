package com.letv.ads.util;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.letv.adlib.apps.types.LetvAppsType;
import com.letv.adlib.managers.status.ad.IPlayerStatusDelegate;
import com.letv.adlib.model.video.MobileAppClientInfo;
import com.letv.ads.AdsManager;
/**
 * 广告请求配置类
 * */
public class ClientInfoUtil {

	/**
	 * 生成简单的客户端信息。一般的界面中的广告，不需要正片相关信息
	 * @return
	 */
	public static MobileAppClientInfo getClientInfo(Context context) {
		MobileAppClientInfo cInfo = new MobileAppClientInfo();

//		cInfo.appContext = context.getApplicationContext();// SDK需要这个，以实现更丰富的功能
		cInfo.appContext = context;// SDK需要这个，以实现更丰富的功能
		cInfo.isDisableAd = !AdsManager.getInstance().isShowAd();// 是否禁用广告。这个移动端是有个开关来管理广告的，暂时我们也用这个开关的值。同样不播广告，我也得上报。
            LogInfo.log("ads", "isShowAd=" + !AdsManager.getInstance().isShowAd());
		cInfo.isVIP = AdsManager.getInstance().isVip();// 是否为VIP。一般情况下，我们知道是VIP，就不会请求广告系统了，但需要发上报。
		cInfo.macAddr = Commons.MAC ;
		
		// 以下参数一般是上报需要的，为了方便，直接用动态方式传入。注意，这些参数必须与上报文档中要求的完全一致
		Map<String, String> dynamicParams = new HashMap<String, String>();
		dynamicParams.put("ch", "");// 传空
		dynamicParams.put("p1", Commons.P1);// 一级产品线代码
		dynamicParams.put("p2", Commons.P2);// 二级产品线代码
		dynamicParams.put("p3", Commons.P3);// 三级产品线代码
		dynamicParams.put("pv", Commons.PVERSION);// 客户端代码
		dynamicParams.put("pcode", Commons.PCODE);// pcode
		dynamicParams.put("lc", Commons.DEVICE_ID);// 唯一ID
//		dynamicParams.put("macAddr", Commons.MAC);// mac地址

		cInfo.dynamicParams = dynamicParams;
		cInfo.appType = LetvAppsType.LETV_SPORT;

		return cInfo;
	}

	/**
	 * 播放器内广告，需要与正片相关的信息
	 * @return
	 */
	public static MobileAppClientInfo getBeginInfo(Context context) {
		MobileAppClientInfo vInfo = getClientInfo(context);
		return vInfo;
	}

	/**
	 * 播放器内广告，需要与正片相关的信息
	 * @return
	 */
	public static MobileAppClientInfo getFocusInfo(Context context) {
		MobileAppClientInfo vInfo = getClientInfo(context);
		return vInfo;
	}

	/**
	 * 播放器内广告，需要与正片相关的信息
	 * @return
	 */
	public static MobileAppClientInfo getBannerInfo(Context context, String cid, String pid) {
		MobileAppClientInfo vInfo = getClientInfo(context);
		vInfo.cid = cid;
		vInfo.pid = pid;
		
		return vInfo;
	}

	/**
	 * 点播播放器内广告，需要与正片相关的信息
	 * @return
	 */
	public static MobileAppClientInfo getVideoPlayerInfo(Context context , String cid , String pid ,String vid ,String mmsid , String uuid , String uid , String vlen , String py , String ty , IPlayerStatusDelegate iPlayerStatusDelegate) {
		MobileAppClientInfo vInfo = getClientInfo(context);
		vInfo.cid = cid;// 电视剧频道
		vInfo.pid = pid;// 上阵父子兵
		vInfo.vid = vid;// 上阵父子兵第3集
		vInfo.mmsid = mmsid;// 媒资ID
		vInfo.playerStatusDelegate = iPlayerStatusDelegate ;
		if (vInfo.dynamicParams != null) {
			vInfo.dynamicParams.put("py", py);
			vInfo.dynamicParams.put("uid", uid);
			vInfo.dynamicParams.put("uuid", uuid);// 播放器生成的，用户一次播放视频的唯一标识
			vInfo.dynamicParams.put("vlen", vlen);// 正片时长
			vInfo.dynamicParams.put("ty", ty);
		}
		return vInfo;
	}
	/**
	 * 点播播放器内广告，需要与正片相关的信息  带有影片付费信息
	 * @return
	 */
	public static MobileAppClientInfo getVideoPlayerInfo(Context context , String cid , String pid ,String vid ,String mmsid , String uuid , String uid , String vlen , String py , String ty , IPlayerStatusDelegate iPlayerStatusDelegate, boolean isVipVideo) {
		MobileAppClientInfo vInfo = getClientInfo(context);
		vInfo.cid = cid;// 电视剧频道
		vInfo.pid = pid;// 上阵父子兵
		vInfo.vid = vid;// 上阵父子兵第3集
		vInfo.mmsid = mmsid;// 媒资ID
		vInfo.isVipMovie = isVipVideo;
		vInfo.playerStatusDelegate = iPlayerStatusDelegate ;
		if (vInfo.dynamicParams != null) {
			vInfo.dynamicParams.put("py", py);
			vInfo.dynamicParams.put("uid", uid);
			vInfo.dynamicParams.put("uuid", uuid);// 播放器生成的，用户一次播放视频的唯一标识
			vInfo.dynamicParams.put("vlen", vlen);// 正片时长
			vInfo.dynamicParams.put("ty", ty);
		}
		return vInfo;
	}
	
	/**
	 * 直播播放器内广告，需要与正片相关的信息
	 * @return
	 */
	public static MobileAppClientInfo getLivePlayerInfo(Context context , String streamUrl ,String uuid , String uid , String ty ,IPlayerStatusDelegate iPlayerStatusDelegate) {
		MobileAppClientInfo vInfo = getClientInfo(context);
		vInfo.streamURL = streamUrl ;
		vInfo.playerStatusDelegate = iPlayerStatusDelegate ;
		
		if (vInfo.dynamicParams != null) {
			vInfo.dynamicParams.put("py", "");
			vInfo.dynamicParams.put("uid", uid);
			vInfo.dynamicParams.put("uuid", uuid);// 播放器生成的，用户一次播放视频的唯一标识
			vInfo.dynamicParams.put("ty", ty);
		}
		return vInfo;
	}
}
