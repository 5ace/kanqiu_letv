package com.letv.watchball.share;

import android.app.Activity;
import android.content.Context;

import com.letv.watchball.activity.SharePageActivity;
import com.letv.watchball.bean.ShareAlbum;
import com.letv.watchball.ui.impl.BasePlayActivity;
import com.tencent.weibo.TWeiboNew;
import com.tencent.weibo.TWeiboNew.TWeiboListener;

public class LetvTencentWeiboShare {

	private static void setAppId() {

		// 设置app key 和 app secrect
		TWeiboNew.redirectUri = ShareConstant.TencentWeibo.redirectUri;
		TWeiboNew.clientId = ShareConstant.TencentWeibo.clientId;
		TWeiboNew.clientSecret = ShareConstant.TencentWeibo.clientSecret;
	}

	/**
	 * 判断是否登录
	 * */
	public static int isLogin(final Context context) {
		setAppId();
		return TWeiboNew.getInstance().isLogin(context);
	}

	/**
	 * 登录
	 * */
	public static void login(final Activity context, final ShareAlbum album,
			final int witch, final int order, final int vid, final boolean isLive, final String liveShare) {
		setAppId();
		TWeiboNew.getInstance().login(context, new TWeiboListener() {

			@Override
			public void onFail(String message) {

			}

			@Override
			public void onError() {

			}

			@Override
			public void onComplete() {
				// if((context instanceof ShareActivity) || (context instanceof
				// ShareConfigActivity)){
				//
				// }else{
				if (context instanceof BasePlayActivity) {
					SharePageActivity.launch(context, witch,
							album.getShare_AlbumName(), album.getIcon(),
							album.getShare_id(), album.getType(),
							album.getCid(), album.getYear(),
							album.getDirector(), album.getActor(),
							album.getTimeLength(), order, vid, isLive, liveShare);
				}
				// }
			}
		});
	}

	/**
	 * 分享图片
	 * */
	public static void share(Activity context, String caption, String imaUrl,
			boolean isQZoom, final TWeiboListener listener) {
		setAppId();
		try {
			TWeiboNew.getInstance().share(context, listener, caption, imaUrl,
					isQZoom);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 登出
	 * */
	public static void logout(Activity context) {
		setAppId();
		TWeiboNew.getInstance().logout(context);
	}
}
