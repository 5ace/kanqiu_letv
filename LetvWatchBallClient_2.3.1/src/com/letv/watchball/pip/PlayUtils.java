package com.letv.watchball.pip;

import android.net.Uri;
import android.text.TextUtils;

import com.letv.http.LetvHttpJavaHandler;
import com.letv.http.bean.LetvDataHull;
import com.letv.http.impl.LetvHttpParameter;
import com.letv.watchball.bean.DDUrlsResult;
import com.letv.watchball.bean.RealPlayUrlInfo;
import com.letv.watchball.bean.VideoFile;
import com.letv.watchball.bean.VideoFile.VideoSchedulingAddress;
import com.letv.watchball.parser.RealPlayUrlInfoParser;
import com.media.LetvBase64;
import com.media.NativeInfos;

public class PlayUtils {

	public final static String BRLIST_350 = "mp4_350";
	public final static String BRLIST_1000 = "mp4_1000";
	public final static String BRLIST_1300 = "mp4_1300";

	/**
	 * 获取调度地址
	 * 
	 * @param videoFile
	 * @param isHd
	 *            是否高清
	 * @return
	 */
	public static DDUrlsResult getDDUrls(VideoFile videoFile, int isHd) {
		String streamLevel = "13";
		String[] urls = new String[4];
		VideoSchedulingAddress address = null;

		boolean hasHigh = false;
		boolean hasLow = false;
		if (videoFile.getMp4_1000() != null
				&& NativeInfos.getSupportLevel() == NativeInfos.SUPPORT_TS800K_LEVEL) {
			hasHigh = true;
		}

		if (videoFile.getMp4_350() != null) {
			hasLow = true;
		}

		switch (isHd) {
		case 1:
			if (hasHigh) {
				address = videoFile.getMp4_1000();
				streamLevel = "21";
			} else {
				isHd = 0;
				address = videoFile.getMp4_350();
				streamLevel = "13";
			}
			break;
		case 0:
			if (hasLow) {
				address = videoFile.getMp4_350();
				streamLevel = "21";
			} else {
				address = videoFile.getMp4_1000();
				streamLevel = "13";
				isHd = 1;
			}
			break;
		case 2:
			address = videoFile.getMp4_1300();
			streamLevel = "24";
		}

		if (address != null) {
			urls[0] = address.getMainUrl();
			urls[1] = address.getBackUrl0();
			urls[2] = address.getBackUrl1();
			urls[3] = address.getBackUrl2();
		}

		DDUrlsResult result = new DDUrlsResult();

		result.setHasHigh(hasHigh);
		result.setHasLow(hasLow);
		result.setHd(isHd);
		result.setDolby(false);
		result.setDdurls(urls);
		result.setStreamLevel(streamLevel);
		return result;
	}

	/**
	 * 获取调度地址
	 * 
	 * @param videoFile
	 * @param isHd
	 *            是否高清
	 * @return
	 */
	public static DDUrlsResult getDDUrls(VideoFile videoFile, int isHd,
			boolean isDolbyChannel) {
		String streamLevel = "13";
		String[] urls = new String[4];
		VideoSchedulingAddress address = null;

		if (isDolbyChannel) {

			address = videoFile.getMp4_800_db();

			if (null == address) {
				address = videoFile.getMp4_1300_db();
				streamLevel = "24";
			}

			if (null == address) {
				address = videoFile.getMp4_720p_db();
				streamLevel = "25";
			}

			if (null == address) {
				address = videoFile.getMp4_1080p6m_db();
				streamLevel = "26";
			}

			if (null != address) {
				urls[0] = address.getMainUrl();
				urls[1] = address.getBackUrl0();
				urls[2] = address.getBackUrl1();
				urls[3] = address.getBackUrl2();

				DDUrlsResult result = new DDUrlsResult();
				result.setHasHigh(true);
				result.setHasLow(false);
				result.setDolby(true);
				result.setHd(isHd);
				result.setDdurls(urls);
				result.setStreamLevel(streamLevel);

				return result;
			} else {
				return getDDUrls(videoFile, isHd);
			}
		} else {
			return getDDUrls(videoFile, isHd);
		}
	}

	/**
	 * 获取真正播放地址
	 * 
	 * @param urls
	 * @return
	 */
	public static LetvDataHull<RealPlayUrlInfo> getRealUrl(String[] urls) {

		RealPlayUrlInfo mRealPlayUrlInfo = null;

		if (urls == null) {
			return null;
		}

		for (int i = 0; i < urls.length; i++) {

			if (TextUtils.isEmpty(urls[i])) {
				continue;
			}

			try {
				mRealPlayUrlInfo = new RealPlayUrlInfo();
				mRealPlayUrlInfo.setDdUrl(urls[i]);

				LetvHttpParameter httpParameter = new LetvHttpParameter(
						urls[i], null, LetvHttpParameter.Type.GET, null, 0);
				String result = new LetvHttpJavaHandler().doGet(httpParameter);
				mRealPlayUrlInfo = new RealPlayUrlInfoParser(mRealPlayUrlInfo)
						.parse(result);

			} catch (Exception e) {
				e.printStackTrace();
			}

			if (200 == mRealPlayUrlInfo.getCode()) {
				LetvDataHull<RealPlayUrlInfo> dataHull = new LetvDataHull<RealPlayUrlInfo>();
				dataHull.setDataEntity(mRealPlayUrlInfo);
				dataHull.setDataType(LetvDataHull.DataType.DATA_IS_INTEGRITY);

				return dataHull;
			}
		}

		return null;
	}

	/**
	 * 是否支持高清
	 * 
	 * @param brList
	 * @return boolean
	 */
	public static boolean isSupportHd(String brList) {

		if (TextUtils.isEmpty(brList)) {
			return false;
		}
		;

		if (brList.contains(BRLIST_1000)) {
			return true;
		}

		return false;
	}

	/**
	 * 是否支持标清
	 * 
	 * @param brList
	 * @return boolean
	 */
	public static boolean isSupportStandard(String brList) {

		if (TextUtils.isEmpty(brList)) {
			return false;
		}
		;

		if (brList.contains(BRLIST_350)) {
			return true;
		}

		return false;
	}

	/**
	 * 暂时默认为TS流
	 * */
	public static String getTss() {
		return "ios";
	}

	/**
	 * 广告拼接播放串（TS流才支持）
	 * */
	public static String splitJointPlayUrl(String playUrl, String adUrl) {
		try {
			String host = Uri.parse(playUrl).getHost();
			String baseUrl = "http://" + host
					+ "/gate_way.m3u8?ID=id9,id10&id9.proxy_url=";
			adUrl = LetvBase64.encode(adUrl.getBytes("UTF-8"));
			playUrl = LetvBase64.encode(playUrl.getBytes("UTF-8"));
			StringBuffer sb = new StringBuffer();
			sb.append(baseUrl);
			sb.append(adUrl);
			sb.append("&id10.proxy_url=");
			sb.append(playUrl);
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
