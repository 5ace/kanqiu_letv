package com.letv.watchball.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class UIControllerUtils {

	public static void gotoWeb(Context context, String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_FROM_BACKGROUND);
		context.startActivity(intent);
	}

//	public static void gotoActivity(Context context, HomeSimpleBlock block, int index, String from) {
//		String curUrl = null;
//		boolean isHomeFocus = true;
//		if (NetWorkTypeUtils.isNetAvailable()) {
//			UIs.showToast(R.string.load_data_no_net);
//			return;
//		}
//		switch (block.getAt()) {
//		case 1:
//			BasePlayActivity.launch(context, block.getPid(), block.getVid(), BasePlayActivity.LAUNCH_FROM_HOME);
//			break;
//		case 2:
//			BasePlayActivity.launch(context, 0, block.getVid(), BasePlayActivity.LAUNCH_FROM_HOME);
//			break;
//		case 3:// 全屏播放直播流
//			String tvCode = block.getStreamCode();
//			String liveUrl = block.getLiveUrl();
//			curUrl = liveUrl;
//			if (!TextUtils.isEmpty(liveUrl) && !TextUtils.isEmpty(tvCode)) {
//				BasePlayActivity.launchLives(context, tvCode, tvCode, liveUrl, true);
//			}else {
//				UIs.showToast("直播地址为空");
//			}
//			break;
//		case 4:// 外跳web
//			String webUrl = block.getWebUrl();
//			if (webUrl == null)
//				return;
//			webUrl = LetvUtil.checkUrl(webUrl);
//			curUrl = webUrl;
//			UIControllerUtils.gotoWeb(context, webUrl);
//			break;
//		case 5:// 内嵌webview
//			String webViewUrl = block.getWebViewUrl();
//			if (webViewUrl == null)
//				return;
//			webViewUrl = LetvUtil.checkUrl(webViewUrl);
//			curUrl = webViewUrl;
//			LetvWebViewActivity.launch(context, webViewUrl, block.getNameCn());
//			break;
//		case 6:// 进入精品推荐
//			RecommendFragmentActivity.launch(context);
//			isHomeFocus = false;
//			break;
//		case 7:// 频道入口引导
//			isHomeFocus = false;
//			if (!MainActivity.getInstance().gotoPage(block.getCid(), false)) {
//				MainActivity.getInstance().gotoPage("更多", false);
//			}
//			break;
//		default:
//			break;
//		}
//		if (isHomeFocus) {
//			String ap = DataConstant.StaticticsVersion2Constatnt.StaticticsName.STATICTICS_NAM_FL
//					+ from + "&"
//					+ DataConstant.StaticticsVersion2Constatnt.StaticticsName.STATICTICS_NAM_WZ + (index + 1) + "&"
//					+ DataConstant.StaticticsVersion2Constatnt.StaticticsName.STATICTICS_NAM_NA
//					+ LetvUtil.getData(block.getNameCn());
//			DataStatistics.getInstance().sendActionInfo(context, "0", "0", LetvUtil.getPcode(), "0", ap, "0",
//					block.getCid() + "", block.getPid() + "", block.getVid() + "", LetvUtil.getUID(), curUrl, null,
//					null, null, PreferencesManager.getInstance().isLogin() ? 0 : 1);
//		}
//	}
//
//	public static void gotoActivity(Context context, ChannelHomeSimpleBlock block, boolean isDobly) {
//		if (NetWorkTypeUtils.isNetAvailable()) {
//			UIs.showToast(R.string.load_data_no_net);
//			return;
//		}
//		switch (block.getAt()) {
//		case 1:
//			if (isDobly) {
//				BasePlayActivity.launch(context, block.getPid(), block.getVid(), isDobly,
//						BasePlayActivity.LAUNCH_FROM_CHANNEL);
//			} else {
//				BasePlayActivity.launch(context, block.getPid(), block.getVid(), BasePlayActivity.LAUNCH_FROM_CHANNEL);
//			}
//			break;
//		case 2:
//			if (isDobly) {
//				BasePlayActivity.launch(context, 0, block.getVid(), isDobly, BasePlayActivity.LAUNCH_FROM_CHANNEL);
//			} else {
//				BasePlayActivity.launch(context, 0, block.getVid(), BasePlayActivity.LAUNCH_FROM_CHANNEL);
//			}
//			break;
//		case 3:// 全屏播放直播流
//			String tvCode = block.getStreamCode();
//			String liveUrl = block.getLiveUrl();
//			String tm = block.getTm();
//			if (!TextUtils.isEmpty(liveUrl) && !TextUtils.isEmpty(tvCode) && !TextUtils.isEmpty(tm)) {
//				BasePlayActivity.launchLives(context, tvCode, null, liveUrl, true);
//			} else {
//				UIs.showToast("直播地址为空");
//			}
//			break;
//		case 4:// 外跳web
//			String webUrl = block.getWebUrl();
//			if (webUrl == null)
//				return;
//			webUrl = LetvUtil.checkUrl(webUrl);
//			UIControllerUtils.gotoWeb(context, webUrl);
//			break;
//		case 5:// 内嵌webview
//			String webViewUrl = block.getWebViewUrl();
//			if (webViewUrl == null)
//				return;
//			webViewUrl = LetvUtil.checkUrl(webViewUrl);
//			LetvWebViewActivity.launch(context, webViewUrl, block.getNameCn());
//			break;
//		case 6:// 进入精品推荐
//			RecommendFragmentActivity.launch(context);
//			break;
//		default:
//			break;
//		}
//	}
}
