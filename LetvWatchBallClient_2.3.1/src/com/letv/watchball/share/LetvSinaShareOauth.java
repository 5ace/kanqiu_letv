package com.letv.watchball.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.letv.cache.LetvCacheTools;
import com.letv.watchball.activity.SharePageActivity;
import com.letv.watchball.bean.ShareAlbum;
import com.letv.watchball.ui.impl.BasePlayActivity;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.utils.LogUtil;

public class LetvSinaShareOauth {

	private static Oauth2AccessToken accessToken;

	/**
	 * 判断是否已经登录
	 * */
	public static boolean isLogin(final Context context) {
		System.out.println("sina accessToken==" + accessToken);
		if (accessToken == null) {
			accessToken = AccessTokenKeeper.readAccessToken(context);
			System.out.println("  accessToken == null  accessToken=="
					+ accessToken);
		} else {
			return accessToken.isSessionValid();
		}
		if (accessToken == null) {
			return false;
		}

		return accessToken.isSessionValid();
	}

	/**
	 * 分享时登录
	 * */
	public static void login(final Activity context, final ShareAlbum album,
			final int order, final int vid, final boolean isLive,
			final String liveShare) {
		if (isLogin(context) && context instanceof BasePlayActivity) {

			SharePageActivity.launch(context, 1, album.getShare_AlbumName(),
					album.getIcon(), album.getShare_id(), album.getType(),
					album.getCid(), album.getYear(), album.getDirector(),
					album.getActor(), album.getTimeLength(), order, vid,
					isLive, liveShare);

		} else {

		}
	}

	/**
	 * 分享入口
	 * 
	 * @param context
	 * @param caption
	 *            需要分享的文字
	 * @param imaUrl
	 *            图片
	 * @param listener
	 *            回调接口
	 */
	public static void share(Context context, String textContent,
			String imaUrl, RequestListener listener) {

		try {
			String ImagePath = LetvCacheTools.StringTool.createFilePath(imaUrl);
			if (accessToken.getToken() != null) {

			} else {
				Toast.makeText(context, "分享失败", Toast.LENGTH_LONG);
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

	private static RequestListener mListener = new RequestListener() {
		@Override
		public void onComplete(String response) {
			if (!TextUtils.isEmpty(response)) {

				if (response.startsWith("{\"statuses\"")) {
					// 调用 StatusList#parse 解析字符串成微博列表对象
					StatusList statuses = StatusList.parse(response);
					if (statuses != null && statuses.total_number > 0) {

					}
				} else if (response.startsWith("{\"created_at\"")) {
					// 调用 Status#parse 解析字符串成微博对象
					Status status = Status.parse(response);

				} else {

				}
			}
		}

		@Override
		public void onWeiboException(WeiboException e) {

			ErrorInfo info = ErrorInfo.parse(e.getMessage());

		}
	};
}
