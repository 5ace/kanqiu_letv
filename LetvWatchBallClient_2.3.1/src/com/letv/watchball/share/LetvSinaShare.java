package com.letv.watchball.share;


public class LetvSinaShare {
//
//	/**
//	 * 判断是否已经登录
//	 * */
//	public static int isLogin(final Context context) {
//		Weibo mWeibo = Weibo.getInstance();
//		mWeibo.initToken(context);
//
//		return mWeibo.isSessionValid();
//	}
//
//	/**
//	 * 分享时登录
//	 * */
//	public static void login(final Activity context, final Album album , final int order) {
//		Weibo mWeibo = Weibo.getInstance();
//		mWeibo.setupConsumerConfig(ShareConstant.Sina.CONSUMER_KEY,
//				ShareConstant.Sina.CONSUMER_SECRET);
//		mWeibo.setRedirectUrl(ShareConstant.Sina.REDIRECTURL);
//
//		WeiboDialogListener listener = new WeiboDialogListener() {
//
//			@Override
//			public void onWeiboException(WeiboException e) {
//
//			}
//
//			@Override
//			public void onError(DialogError e) {
//
//			}
//
//			@Override
//			public void onComplete(Bundle values) {
//				if((context instanceof ShareActivity) || (context instanceof ShareConfigActivity)){
//					
//				}else{
//					ShareActivity.launch(context,1, album , order);
//				}
//			}
//
//			@Override
//			public void onCancel() {
//
//			}
//		};
//		mWeibo.authorizeFull(context, listener);
//	}
//
//
//	/**
//	 * 设置中绑定账号
//	 * */
//	public static void Bound(final Activity context,WeiboDialogListener listener) {
//		Weibo mWeibo = Weibo.getInstance();
//		mWeibo.setupConsumerConfig(ShareConstant.Sina.CONSUMER_KEY,ShareConstant.Sina.CONSUMER_SECRET);
//		mWeibo.setRedirectUrl(ShareConstant.Sina.REDIRECTURL);
//
//		mWeibo.authorizeFull(context, listener);
//	}
//	
//
//	/**
//	 * 分享
//	 * */
//	public static void share(Context context, String caption, String imaUrl,
//			RequestListener listener) {
//		Weibo weibo = Weibo.getInstance();
//		try {
//			String path = ImageStorage.url2FilePath(imaUrl);
//			if (!TextUtils.isEmpty((String) (weibo.getAccessToken().getToken()))) {
//				File file = new File(path);
//				if (!TextUtils.isEmpty(path) && file.exists()) {
//					upload(context, weibo, Weibo.getAppKey(), path, caption,
//							"", "", listener);
//				} else {
//					update(context, weibo, Weibo.getAppKey(), caption, "", "",
//							listener);
//				}
//			} else {
//
//			}
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (WeiboException e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * 得到UID
//	 * */
//	public static void getUserId(Context context, RequestListener listener) {
//		Weibo weibo = Weibo.getInstance();
//		weibo.getUserId(context, listener);
//	}
//
//	/**
//	 * 通过UID 或 SCREEN_NAME得到用户信息
//	 * */
//	public static void getUserInfo(Context context, String uid,
//			String screen_name, RequestListener listener) {
//		Weibo weibo = Weibo.getInstance();
//		weibo.getUserInfo(context, uid, screen_name, listener);
//	}
//
//	/**
//	 * 分享图片
//	 * */
//	private static String upload(Context context, Weibo weibo, String source,
//			String file, String status, String lon, String lat,
//			RequestListener listener) throws WeiboException {
//		WeiboParameters bundle = new WeiboParameters();
//		bundle.add("source", source);
//		bundle.add("pic", file);
//		bundle.add("status", status);
//		if (!TextUtils.isEmpty(lon)) {
//			bundle.add("lon", lon);
//		}
//		if (!TextUtils.isEmpty(lat)) {
//			bundle.add("lat", lat);
//		}
//		String rlt = "";
//		String url = Weibo.SERVER + "statuses/upload.json";
//		AsyncWeiboRunner weiboRunner = new AsyncWeiboRunner(weibo);
//		weiboRunner.request(context, url, bundle, Utility.HTTPMETHOD_POST,
//				listener);
//
//		return rlt;
//	}
//
//	/**
//	 * 分享文字
//	 * */
//	private static String update(Context context, Weibo weibo, String source,
//			String status, String lon, String lat, RequestListener listener)
//			throws MalformedURLException, IOException, WeiboException {
//		WeiboParameters bundle = new WeiboParameters();
//		bundle.add("source", source);
//		bundle.add("status", status);
//		if (!TextUtils.isEmpty(lon)) {
//			bundle.add("lon", lon);
//		}
//		if (!TextUtils.isEmpty(lat)) {
//			bundle.add("lat", lat);
//		}
//		String rlt = "";
//		String url = Weibo.SERVER + "statuses/update.json";
//		AsyncWeiboRunner weiboRunner = new AsyncWeiboRunner(weibo);
//		weiboRunner.request(context, url, bundle, Utility.HTTPMETHOD_POST,
//				listener);
//		return rlt;
//	}
//
//	/**
//	 * 发微博
//	 * */
//	public static String update(Context context, String status,
//			RequestListener listener) {
//		Weibo weibo = Weibo.getInstance();
//		WeiboParameters bundle = new WeiboParameters();
//		bundle.add("source", Weibo.getAppKey());
//		bundle.add("status", status);
//
//		String rlt = "";
//		String url = Weibo.SERVER + "statuses/update.json";
//		AsyncWeiboRunner weiboRunner = new AsyncWeiboRunner(weibo);
//		weiboRunner.request(context, url, bundle, Utility.HTTPMETHOD_POST,
//				listener);
//		return rlt;
//	}
//
//	/**
//	 * 得到话题列表
//	 * */
//	public static void getTrends(Context context, String uid, int count,
//			int page, RequestListener listener) {
//		Weibo weibo = Weibo.getInstance();
//		weibo.getTrends(context, uid, count, page, listener);
//	}
//
//	/**
//	 * 得到微博列表
//	 * */
//	public static void getUserTimeline(Context context, String uid,
//			String screen_name, int since_id, int max_id, int count, int page,
//			int base_app, int feature, int trim_user, RequestListener listener) {
//		Weibo weibo = Weibo.getInstance();
//		weibo.userTimeline(context, uid, screen_name, since_id, max_id, count,
//				page, base_app, feature, trim_user, listener);
//	}
//
//	/**
//	 * 转发
//	 * */
//	public static void statusesRepost(Context context, String status,
//			String id, RequestListener listener) {
//		Weibo weibo = Weibo.getInstance();
//		weibo.statusesRepost(context, status, id, 0, listener);
//	}
//
//	/**
//	 * 评论
//	 * */
//	public static void commentsCreate(Context context, String comment,
//			String id, RequestListener listener) {
//		Weibo weibo = Weibo.getInstance();
//		weibo.commentsCreate(context, comment, id, 1, listener);
//	}
//
//	/**
//	 * 登出
//	 * */
//	public static void logout(Activity context) {
//		Weibo mWeibo = Weibo.getInstance();
//		mWeibo.logout(context);
//	}
//
//	/**
//	 * 关注
//	 * */
//	public static void friendshipsCreate(Context context, String uid,
//			RequestListener listener) {
//		Weibo weibo = Weibo.getInstance();
//		weibo.friendshipsCreate(context, uid, "", listener);
//	}
//
//	/**
//	 * 取消关注
//	 * */
//	public static void friendshipsDestroy(Context context, String uid,
//			RequestListener listener) {
//		Weibo weibo = Weibo.getInstance();
//		weibo.friendshipsDestroy(context, uid, "", listener);
//	}
}
