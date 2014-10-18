package com.letv.datastatistics.parse;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.letv.datastatistics.DataStatistics;
import com.letv.datastatistics.entity.AdsInfo;
import com.letv.datastatistics.entity.ApiInfo;
import com.letv.datastatistics.entity.DataStatusInfo;
import com.letv.datastatistics.entity.Defaultbr;
import com.letv.datastatistics.entity.JpushInfo;
import com.letv.datastatistics.entity.LogoInfo;
import com.letv.datastatistics.entity.RecommendInfo;
import com.letv.datastatistics.entity.StatInfo;
import com.letv.datastatistics.entity.UpgradeInfo;

public class DataStatusInfoParse {

	public DataStatusInfo parseJson(JSONObject jsonObject) throws JSONException {

		DataStatusInfo mDataStatusInfo = null;

		if (jsonObject == null) {
			return null;
		}

		JSONObject jsonBodyObject = null;

		if (jsonObject.has("body")) {
			jsonBodyObject = jsonObject.optJSONObject("body");

			if (jsonBodyObject == null) {
				return null;
			}

			mDataStatusInfo = new DataStatusInfo();

			if (jsonBodyObject.has("tm")) {
				mDataStatusInfo.setTm(jsonBodyObject.optInt("tm"));
			}

			if (jsonBodyObject.has("apiinfo")) {

				JSONObject apiInfoObject = jsonBodyObject.optJSONObject("apiinfo");

				if (apiInfoObject != null) {
					ApiInfo mApiInfo = new ApiInfo();
					mApiInfo.setApistatus(apiInfoObject.optString("apistatus"));
					mDataStatusInfo.setApiInfo(mApiInfo);
				}
			}

			if (jsonBodyObject.has("statinfo")) {

				JSONObject statInfoObject = jsonBodyObject.optJSONObject("statinfo");

				if (statInfoObject != null) {
					StatInfo mStatInfo = new StatInfo();
					mStatInfo.setResult(statInfoObject.optString("result"));
					mDataStatusInfo.setStatInfo(mStatInfo);
				}
			}

			if (jsonBodyObject.has("upgrade")) {

				JSONObject upgradeObject = jsonBodyObject.optJSONObject("upgrade");

				if (upgradeObject != null) {
					UpgradeInfo mUpgradeInfo = new UpgradeInfo();
					mUpgradeInfo.setV(upgradeObject.optString("v"));
					mUpgradeInfo.setTitle(upgradeObject.optString("title"));
					mUpgradeInfo.setMsg(upgradeObject.optString("msg"));
					mUpgradeInfo.setUptype(upgradeObject.optString("uptype"));
					mUpgradeInfo.setUrl(upgradeObject.optString("url"));
					mUpgradeInfo.setUpgrade(upgradeObject.optString("upgrade"));
					mDataStatusInfo.setUpgradeInfo(mUpgradeInfo);
				}
			}

			if (jsonBodyObject.has("adinfo")) {

				JSONObject adsInfoObject = jsonBodyObject.optJSONObject("adinfo");
				AdsInfo mAdsInfo = null;

				if (adsInfoObject != null) {
					mAdsInfo = new AdsInfo();
					mAdsInfo.setKey(adsInfoObject.optString("key"));
					mAdsInfo.setValue(adsInfoObject.optString("val"));
					mDataStatusInfo.setAdsInfo(mAdsInfo);
				}

				if (jsonBodyObject.has("adpininfo") && mAdsInfo != null) {

					JSONObject adsPinInfoObject = jsonBodyObject.optJSONObject("adpininfo");

					if (adsPinInfoObject != null) {
						mAdsInfo.setPinKey(adsPinInfoObject.optString("key"));
						mAdsInfo.setPinValue(adsPinInfoObject.optString("val"));
					}
				}
			}

			if (jsonBodyObject.has("recommendinfo")) {

				JSONArray recommendInfoArray = jsonBodyObject.optJSONArray("recommendinfo");

				if (recommendInfoArray != null && recommendInfoArray.length() > 0) {
					HashMap<String, RecommendInfo> recommendInfos = new HashMap<String, RecommendInfo>();
					for (int i = 0; i < recommendInfoArray.length(); i++) {
						JSONObject recommendInfoObject = recommendInfoArray.optJSONObject(i);
						if (recommendInfoObject != null) {
							String key = recommendInfoObject.optString("key");
							String val = recommendInfoObject.optString("val");
							int num = recommendInfoObject.optInt("num");
							if (!TextUtils.isEmpty(key)) {
								RecommendInfo mRecommendInfo = new RecommendInfo();
								mRecommendInfo.setKey(key);
								mRecommendInfo.setValue(val);
								mRecommendInfo.setNum(num);
								recommendInfos.put(key, mRecommendInfo);
							}
						}
					}
					mDataStatusInfo.setRecommendInfos(recommendInfos);
				}
			}

			if (jsonBodyObject.has("defaultbr")) {
				JSONObject object = jsonBodyObject.optJSONObject("defaultbr");

				if (object != null) {

					if (object.has("play")) {
						JSONObject o = object.optJSONObject("play");
						if (o != null && o.has("gphone")) {
							o = o.optJSONObject("gphone");
							if (o != null) {
								Defaultbr pd = new Defaultbr();

								pd.setLow(o.optString("low", "350"));
								pd.setNormal(o.optString("normal", "1000"));
								pd.setHigh(o.optString("high", "1300"));
								pd.setLow_zh(o.optString("low_zh", "流畅"));
								pd.setNormal_zh(o.optString("normal_zh", "高清"));
								pd.setHigh_zh(o.optString("high_zh", "超清"));

								mDataStatusInfo.setPlayDefaultbr(pd);
							}
						}
					}

					if (object.has("download")) {
						JSONObject o = object.optJSONObject("download");
						if (o != null && o.has("gphone")) {
							o = o.optJSONObject("gphone");
							if (o != null) {
								Defaultbr dd = new Defaultbr();

								dd.setLow(o.optString("low", "350"));
								dd.setNormal(o.optString("normal", "1000"));
								dd.setHigh(o.optString("high", "1300"));
								dd.setLow_zh(o.optString("low_zh", "流畅"));
								dd.setNormal_zh(o.optString("normal_zh", "高清"));
								dd.setHigh_zh(o.optString("high_zh", "超清"));

								mDataStatusInfo.setDownloadDefaultbr(dd);
							}
						}
					}
				}
			}

			if (jsonBodyObject.has("exchange")) {
				JSONArray array = jsonBodyObject.getJSONArray("exchange");
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					if (obj.has("key")) {
						String key = obj.getString("key");
						if (key != null && key.equals("channel")) {
							if (obj.getString("val") != null)
								mDataStatusInfo.setChannelRecommendSwitch(obj.getString("val").equals("1") ? true
										: false);
						} else if (key != null && key.equals("bottom")) {

							if (obj.getString("val") != null) {
								mDataStatusInfo.setBottomRecommendSwitch(obj.getString("val").equals("1") ? true
										: false);
							}
						}
					}

				}
			}

			if (mDataStatusInfo.getApiInfo() != null
					&& ApiInfo.APISTATUS_TEST.equals(mDataStatusInfo.getApiInfo().getApistatus())) {
				DataStatistics.getInstance().setUseTest(true);
			} else {
				DataStatistics.getInstance().setUseTest(false);
			}

			if (jsonBodyObject.has("androidOpen350")) {
				mDataStatusInfo.setAndroidOpen350(jsonBodyObject.getInt("androidOpen350"));
			}
		}
		if (jsonBodyObject.has("logoinfo")) {
			JSONObject logoInfoObject = jsonBodyObject.optJSONObject("logoinfo");
			if (logoInfoObject != null) {
				LogoInfo mLogoInfo = new LogoInfo();
				mLogoInfo.setIcon(logoInfoObject.optString("icon"));
				mLogoInfo.setJumpUrl(logoInfoObject.optString("url"));
				mLogoInfo.setStatus(logoInfoObject.optString("status"));
				mDataStatusInfo.setmLogoInfo(mLogoInfo);
			}
		}
		if (jsonBodyObject.has("jpush")) {
			JSONObject jpushInfoObject = jsonBodyObject.optJSONObject("jpush");
			if (jpushInfoObject != null) {
				JpushInfo mJpushInfo = new JpushInfo();
				mJpushInfo.setStatus(jpushInfoObject.optString("status"));
				mDataStatusInfo.setmJpushInfo(mJpushInfo);
			}
		}
		return mDataStatusInfo;
	}
}
