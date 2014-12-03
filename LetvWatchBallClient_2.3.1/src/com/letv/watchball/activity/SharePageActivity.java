package com.letv.watchball.activity;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.letv.watchball.share.ShareConstant;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.cache.LetvCacheTools;
import com.letv.star.LetvStarListener;
import com.letv.watchball.R;
import com.letv.watchball.async.LetvSimpleAsyncTask;
import com.letv.watchball.bean.AlbumNew;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.share.AccessTokenKeeper;
import com.letv.watchball.share.LetvRenrenShare;
import com.letv.watchball.share.LetvSinaShareOauth;
import com.letv.watchball.share.LetvStarShare;
import com.letv.watchball.share.LetvTencentQzoneShare;
import com.letv.watchball.share.LetvTencentWeiboShare;
import com.letv.watchball.share.ShareConstant;
import com.letv.watchball.ui.impl.BasePlayActivity;
import com.letv.watchball.utils.LetvUtil;
import com.letv.watchball.utils.TextUtil;
import com.letv.watchball.utils.UIs;
import com.renren.api.connect.android.common.AbstractRequestListener;
import com.renren.api.connect.android.exception.RenrenError;
import com.renren.api.connect.android.photos.PhotoUploadResponseBean;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.tencent.weibo.TWeiboNew.TWeiboListener;

public class SharePageActivity extends PimBaseActivity implements
		View.OnClickListener, IWeiboHandler.Response {

	/**
	 * 判别式那个一个分享的标志位
	 * */
	private int from = 0; // 1 sina ， 2 腾讯微博 ， 3 QQ空间 ， 4 人人 ， 5 大咔

	/**
	 * 分享的专辑集数
	 * */
	private int order;

	/**
	 * 用户分享内容编辑框
	 * */
	private EditText userContent;

	/**
	 * 还能输入长度显示view
	 * */
	private TextView lastLength;

	/**
	 * 还能输入的最长长度
	 * */
	private int maxLength = 140;

	private View sinaFlag;
	private View tencentFlag;
	private View qzoomFlag;
	private View renrenFlag;
	private View lestarFlag;

	private ImageView sinaIcon;
	private ImageView tencentIcon;
	private ImageView qzoomIcon;
	private ImageView renrenIcon;
	private ImageView lestarIcon;
	private ImageView topicOfConversation;
	private ImageView payAttentionTo;

	private View sinaView;
	private View tencentView;
	private View qzoomView;
	private View renrenView;
	private View lestarView;

	public static final String CONSUMER_KEY = "4071506712";

	public static final String CONSUMER_SECRET = "1eea1bdd72eef646052ef6769f75a772";

	public static final String REDIRECTURL = "http://m.letv.com";
	

	public static final String SCOPE = "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";
	/**
	 * sina网是否登录标志位
	 * */
	private boolean sinaIsLogin = true;
	/**
	 * sina weibo的token 
	 */
	private static Oauth2AccessToken accessToken;
	/**
	 * 腾讯是否登录标志位
	 * */
	private boolean tencentWeiboIsLogin = true;

	/**
	 * 腾讯是否登录标志位
	 * */
	private boolean tencentQzoneIsLogin;

	/**
	 * 人人网是否登录标志位
	 * */
	private boolean renrenIsLogin;

	/**
	 * 大咔是否登录标志位
	 * */
	private boolean letvStarIsLogin;

	/**
	 * sina网是否分享标志位
	 * */
	private boolean sinaIsShare = false;

	/**
	 * 腾讯是否分享标志位
	 * */
	private boolean tencentIsShare = false;

	/**
	 * 腾讯是否分享标志位
	 * */
	private boolean qzoomIsShare = false;

	/**
	 * 人人网是否分享标志位
	 * */
	private boolean renrenIsShare = false;

	/**
	 * 大咔是否分享标志位
	 * */
	private boolean letvStarIsShare = false;

	/**
	 * sina微博回调
	 * */
	private SsoHandler mSsoHandler;

	private String title;

	private String icon;

	private int id;

	private int type;

	private int cid;

	private String year;

	private String director;

	private String actor;

	private long timeLength;

	private int pos_cursor = 0;

	private int vid;

	private String shareUrl;

	private TextView top_title;

	private boolean isLive;

	private String liveShare;

	public static void launch(Context activity, int from, String title,
			String icon, int id, int type, int cid, String year,
			String director, String actor, long timeLength, int order, int vid,
			boolean isLive, String liveShare) {

		Intent intent = new Intent(activity, SharePageActivity.class);
		intent.putExtra("from", from);
		intent.putExtra("title", title);
		intent.putExtra("icon", icon);
		intent.putExtra("id", id);
		intent.putExtra("type", type);
		intent.putExtra("cid", cid);
		intent.putExtra("year", year);
		intent.putExtra("director", director);
		intent.putExtra("actor", actor);
		intent.putExtra("timeLength", timeLength);
		intent.putExtra("order", order);
		intent.putExtra("vid", vid);
		intent.putExtra("isLive", isLive);
		intent.putExtra("liveShare", liveShare);
		activity.startActivity(intent);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PreferencesManager.getInstance().setSinaIsShare(false);
		PreferencesManager.getInstance().setTencentIsShare(false);
		PreferencesManager.getInstance().setQzoneIsShare(false);
		PreferencesManager.getInstance().setRenrenIsShare(false);
		PreferencesManager.getInstance().setLestarIsShare(false);
		Intent intent = getIntent();
		from = intent.getIntExtra("from", 0);
		title = intent.getStringExtra("title");
		icon = intent.getStringExtra("icon");
		year = intent.getStringExtra("year");
		director = intent.getStringExtra("director");
		actor = intent.getStringExtra("actor");
		timeLength = intent.getLongExtra("timeLength", 0);
		id = intent.getIntExtra("id", 0);
		type = intent.getIntExtra("type", 0);
		cid = intent.getIntExtra("cid", 0);
		order = intent.getIntExtra("order", 1);
		vid = intent.getIntExtra("vid", -1);
		isLive = intent.getBooleanExtra("isLive", false);
		liveShare = intent.getStringExtra("liveShare");
		findView(isLive);
		setTopTitle();
		switch (from) {
		case 1:
			PreferencesManager.getInstance().setSinaIsShare(true);
			break;
		case 2:
			PreferencesManager.getInstance().setTencentIsShare(true);
			break;
		case 3:
			PreferencesManager.getInstance().setQzoneIsShare(true);
			break;
		case 4:
			PreferencesManager.getInstance().setRenrenIsShare(true);
			break;
		case 5:
			PreferencesManager.getInstance().setLestarIsShare(true);
			break;
		}

		maxLength = (140 - count(userContent.getText().toString()));
		if (maxLength < 0) {
			lastLength.setTextColor(getResources().getColor(
					R.color.letv_color_ffff0000));
		} else {
			lastLength.setTextColor(getResources().getColor(
					R.color.letv_color_ff393939));
		}
		lastLength.setText(maxLength + "");

		pos_cursor = userContent.getEditableText().length();
		userContent.setSelection(pos_cursor);
		
		if (intent.getBooleanExtra("sina", false)) {
			AuthInfo mWeiboAuth = new AuthInfo(this,
					CONSUMER_KEY,
					REDIRECTURL, SCOPE);
			 SsoHandler mSsoHandler = new SsoHandler(this,
			 mWeiboAuth);
			mSsoHandler.authorize(new WeiboAuthListener() {

				@Override
				public void onComplete(Bundle values) {
					accessToken = Oauth2AccessToken.parseAccessToken(values);
					Log.e("gongmeng", "Letv Token:" + accessToken.getToken());
					if (accessToken.isSessionValid()) 
						AccessTokenKeeper.keepAccessToken(SharePageActivity.this, accessToken);	
					
				}

				@Override
				public void onWeiboException(WeiboException e) {
					e.printStackTrace();
				}

				@Override
				public void onCancel() {
					// TODO Auto-generated method stub
				}

			});
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		return super.onTouchEvent(event);
	}

	/**
	 * 初始化控件
	 * */
	public void findView(boolean isLive) {
		userContent = (EditText) findViewById(R.id.ShareText);
		lastLength = (TextView) findViewById(R.id.maxlength);
		top_title = (TextView) findViewById(R.id.top_title);
		findViewById(R.id.top_button_Share).setOnClickListener(this);
		findViewById(R.id.top_button).setOnClickListener(this);
		if (isLive)
			shareUrl = liveShare;
		else
			shareUrl = LetvUtil.getShareHint(title, type, id, order, vid);

		if (shareUrl == null) {
			UIs.notifyShortNormal(this, R.string.toast_net_null);
		}

		userContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String ss = s.toString();
				System.out.println("ss=" + ss);
				maxLength = (140 - count(ss));
				System.out.println("maxLength=" + maxLength);
				if (maxLength < 0) {
					lastLength.setTextColor(0xffff0000);
				} else {
					lastLength.setTextColor(getResources().getColor(
							R.color.letv_color_ff393939));
				}
				lastLength.setText(maxLength + "");
				pos_cursor = userContent.getSelectionStart();
			}
		});
		System.out.println("shareUrl=" + shareUrl);
		userContent.setText(shareUrl);
	}

	public void setTopTitle() {

		switch (from) {
		case 1:
			top_title.setText(R.string.share_sina_title_s);
			break;
		case 2:
			top_title.setText(R.string.share_qq_title);
			break;
		case 3:
			top_title.setText(R.string.share_qzone_title);
			break;
		case 4:
			top_title.setText(R.string.share_renren_title);
			break;
		case 5:
			top_title.setText(R.string.share_lestar_title);
			break;
		default:
			top_title.setText(R.string.share_tab_title);
			break;
		}

	}

	/**
	 * 构成默认分享的内容
	 * */
	private String getShareContent(String test) {

		String content = getString(R.string.share_content, "");

		String userc = test;

		if (userc.length() > 0) {
			content = getString(R.string.share_content, userc);
		}

		return content;
	}

	/**
	 * 人人网和sina微博计算字数的一个换算字符串（计算字数时使用，实际发布的不是该内容）
	 * */
	private String getShareCalculateContent(String text) {

		String content = getString(R.string.share_content_calculate, "");

		String userc = text;

		if (userc.length() > 0) {
			content = getString(R.string.share_content_calculate, userc);
		}

		return content;
	}

	/**
	 * 字数计算方法
	 * */
	public int count(String text) {
		int doubleC = 0;

		String str = getShareCalculateContent(text);
		int singelC = 0;
		String s = "[^\\x00-\\xff]";
		Pattern pattern = Pattern.compile(s);
		Matcher ma = pattern.matcher(str);

		while (ma.find()) {
			doubleC++;
		}
		singelC = str.length() - doubleC;
		if (singelC % 2 != 0) {
			doubleC += (singelC + 1) / 2;
		} else {
			doubleC += singelC / 2;
		}

		return doubleC;
	}

	@Override
	protected void onResume() {
		super.onResume();
		isBind();
	}

	public void isBind() {
		sinaIsLogin = LetvSinaShareOauth.isLogin(SharePageActivity.this);
		tencentWeiboIsLogin = LetvTencentWeiboShare
				.isLogin(SharePageActivity.this) == ShareConstant.BindState.BIND;
		tencentQzoneIsLogin = LetvTencentQzoneShare
				.isLogin(SharePageActivity.this) == ShareConstant.BindState.BIND;
		renrenIsLogin = LetvRenrenShare.isLogin(SharePageActivity.this);
		letvStarIsLogin = LetvStarShare.isLogin(SharePageActivity.this);

		sinaIsShare = PreferencesManager.getInstance().sinaIsShare();
		tencentIsShare = PreferencesManager.getInstance().tencentIsShare();
		qzoomIsShare = PreferencesManager.getInstance().qzoneIsShare();
		renrenIsShare = PreferencesManager.getInstance().renrenIsShare();
		letvStarIsShare = PreferencesManager.getInstance().lestarIsShare();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
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
	private class RequestTask extends LetvSimpleAsyncTask<Void> {

		public RequestTask(Context context) {
			super(context, false);

		}

		@Override
		public Void doInBackground() {
			if (sinaIsLogin && sinaIsShare) {
			
				try {
					accessToken = AccessTokenKeeper.readAccessToken(context);
					
					if (accessToken.getToken() != null) {
						StatusesAPI mStatusesAPI =  new StatusesAPI(SharePageActivity.this, CONSUMER_KEY, accessToken);
						mStatusesAPI.update(userContent.getText().toString(), null, null, mListener);
					} else {
						Toast.makeText(SharePageActivity.this, "分享失败", Toast.LENGTH_LONG);
					}
				} catch (WeiboException e) {
					e.printStackTrace();
				}
			}

			if (tencentWeiboIsLogin && tencentIsShare) {
				TWeiboListener listener = new TWeiboListener() {

					@Override
					public void onFail(String message) {
						SharePageActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								NotificationManager notificationManager = (NotificationManager) SharePageActivity.this
										.getSystemService(Context.NOTIFICATION_SERVICE);
								Notification notification = new Notification();
								PendingIntent contentIntent = PendingIntent
										.getActivity(SharePageActivity.this, 0,
												new Intent(), 0);
								notification.setLatestEventInfo(
										SharePageActivity.this, null, null,
										contentIntent);
								notification.icon = R.drawable.notification_tencent_icon;
								notification.tickerText = TextUtil
										.getString(R.string.shareactivity_sina_fail);
								notification.flags = Notification.FLAG_AUTO_CANCEL;
								notificationManager
										.notify(222222, notification);
								notificationManager.cancel(222222);
							}
						});
					}

					@Override
					public void onError() {
						SharePageActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								NotificationManager notificationManager = (NotificationManager) SharePageActivity.this
										.getSystemService(Context.NOTIFICATION_SERVICE);
								Notification notification = new Notification();
								PendingIntent contentIntent = PendingIntent
										.getActivity(SharePageActivity.this, 0,
												new Intent(), 0);
								notification.setLatestEventInfo(
										SharePageActivity.this, null, null,
										contentIntent);
								notification.icon = R.drawable.notification_tencent_icon;
								notification.tickerText = TextUtil
										.getString(R.string.shareactivity_sina_fail);
								notification.flags = Notification.FLAG_AUTO_CANCEL;
								notificationManager
										.notify(222222, notification);
								notificationManager.cancel(222222);
							}
						});
					}

					@Override
					public void onComplete() {
						SharePageActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								NotificationManager notificationManager = (NotificationManager) SharePageActivity.this
										.getSystemService(Context.NOTIFICATION_SERVICE);
								Notification notification = new Notification();
								PendingIntent contentIntent = PendingIntent
										.getActivity(SharePageActivity.this, 0,
												new Intent(), 0);
								notification.setLatestEventInfo(
										SharePageActivity.this, null, null,
										contentIntent);
								notification.icon = R.drawable.notification_tencent_icon;
								notification.tickerText = TextUtil
										.getString(R.string.shareactivity_sina_ok);
								notification.flags = Notification.FLAG_AUTO_CANCEL;
								notificationManager
										.notify(222222, notification);
								notificationManager.cancel(222222);
							}
						});
					}
				};

				LetvTencentWeiboShare.share(SharePageActivity.this,
						getShareContent(userContent.getText().toString()),
						icon, false, listener);
			}

			if (tencentQzoneIsLogin && qzoomIsShare) {
				if (LetvTencentQzoneShare.onTencentQZResult != null) {
					LetvTencentQzoneShare.onTencentQZResult
							.onTencentQZResult_back(shareUrl);
				}

			}

			if (renrenIsLogin && renrenIsShare) {
				AbstractRequestListener<PhotoUploadResponseBean> renrenListener = new AbstractRequestListener<PhotoUploadResponseBean>() {
					@Override
					public void onRenrenError(RenrenError renrenError) {
						String newpath = LetvCacheTools.StringTool
								.createFilePath2(icon);
						File newfile = new File(newpath);
						if (newfile != null && newfile.exists()) {
							newfile.delete();
						}
						SharePageActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								NotificationManager notificationManager = (NotificationManager) SharePageActivity.this
										.getSystemService(Context.NOTIFICATION_SERVICE);
								Notification notification = new Notification();
								PendingIntent contentIntent = PendingIntent
										.getActivity(SharePageActivity.this, 0,
												new Intent(), 0);
								notification.setLatestEventInfo(
										SharePageActivity.this, null, null,
										contentIntent);
								notification.icon = R.drawable.notification_renren_icon;
								notification.tickerText = TextUtil
										.getString(R.string.shareactivity_sina_fail);
								notification.flags = Notification.FLAG_AUTO_CANCEL;
								notificationManager
										.notify(444444, notification);
								notificationManager.cancel(444444);
							}
						});
					}

					@Override
					public void onFault(Throwable fault) {
						String newpath = LetvCacheTools.StringTool
								.createFilePath2(icon);
						File newfile = new File(newpath);
						if (newfile != null && newfile.exists()) {
							newfile.delete();
						}
						SharePageActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								NotificationManager notificationManager = (NotificationManager) SharePageActivity.this
										.getSystemService(Context.NOTIFICATION_SERVICE);
								Notification notification = new Notification();
								PendingIntent contentIntent = PendingIntent
										.getActivity(SharePageActivity.this, 0,
												new Intent(), 0);
								notification.setLatestEventInfo(
										SharePageActivity.this, null, null,
										contentIntent);
								notification.icon = R.drawable.notification_renren_icon;
								notification.tickerText = TextUtil
										.getString(R.string.shareactivity_sina_fail);
								notification.flags = Notification.FLAG_AUTO_CANCEL;
								notificationManager
										.notify(444444, notification);
								notificationManager.cancel(444444);
							}
						});
					}

					@Override
					public void onComplete(PhotoUploadResponseBean photoResponse) {
						String newpath = LetvCacheTools.StringTool
								.createFilePath2(icon);
						File newfile = new File(newpath);
						if (newfile != null && newfile.exists()) {
							newfile.delete();
						}
						SharePageActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								NotificationManager notificationManager = (NotificationManager) SharePageActivity.this
										.getSystemService(Context.NOTIFICATION_SERVICE);
								Notification notification = new Notification();
								PendingIntent contentIntent = PendingIntent
										.getActivity(SharePageActivity.this, 0,
												new Intent(), 0);
								notification.setLatestEventInfo(
										SharePageActivity.this, null, null,
										contentIntent);
								notification.icon = R.drawable.notification_renren_icon;
								notification.tickerText = TextUtil
										.getString(R.string.shareactivity_sina_ok);
								notification.flags = Notification.FLAG_AUTO_CANCEL;
								notificationManager
										.notify(444444, notification);
								notificationManager.cancel(444444);
							}
						});
					}
				};
				LetvRenrenShare.share(SharePageActivity.this,
						getShareContent(userContent.getText().toString()),
						SharePageActivity.this.icon, renrenListener);
			}

			if (letvStarIsLogin && letvStarIsShare) {
				if (cid == AlbumNew.Channel.TYPE_MOVIE
						|| cid == AlbumNew.Channel.TYPE_TV
						|| cid == AlbumNew.Channel.TYPE_CARTOON
						|| cid == AlbumNew.Channel.TYPE_JOY
						|| cid == AlbumNew.Channel.TYPE_MUSIC
						|| cid == AlbumNew.Channel.TYPE_PE
						|| cid == AlbumNew.Channel.TYPE_LETV_MAKE
						|| cid == AlbumNew.Channel.TYPE_DOCUMENT_FILM
						|| cid == AlbumNew.Channel.TYPE_OPEN_CLASS
						|| cid == AlbumNew.Channel.TYPE_FASHION
						|| cid == AlbumNew.Channel.TYPE_TVSHOW) {
					LetvStarListener letvStarListener = new LetvStarListener() {

						@Override
						public void onFail(String failLog) {
							SharePageActivity.this
									.runOnUiThread(new Runnable() {

										@Override
										public void run() {
											NotificationManager notificationManager = (NotificationManager) SharePageActivity.this
													.getSystemService(Context.NOTIFICATION_SERVICE);
											Notification notification = new Notification();
											PendingIntent contentIntent = PendingIntent
													.getActivity(
															SharePageActivity.this,
															0, new Intent(), 0);
											notification.setLatestEventInfo(
													SharePageActivity.this,
													null, null, contentIntent);
											notification.icon = R.drawable.notification_lestar_icon;
											notification.tickerText = TextUtil
													.getString(R.string.shareactivity_sina_fail);
											notification.flags = Notification.FLAG_AUTO_CANCEL;
											notificationManager.notify(555555,
													notification);
											notificationManager.cancel(555555);
										}
									});
						}

						@Override
						public void onErr(String errLog) {
							SharePageActivity.this
									.runOnUiThread(new Runnable() {

										@Override
										public void run() {
											NotificationManager notificationManager = (NotificationManager) SharePageActivity.this
													.getSystemService(Context.NOTIFICATION_SERVICE);
											Notification notification = new Notification();
											PendingIntent contentIntent = PendingIntent
													.getActivity(
															SharePageActivity.this,
															0, new Intent(), 0);
											notification.setLatestEventInfo(
													SharePageActivity.this,
													null, null, contentIntent);
											notification.icon = R.drawable.notification_lestar_icon;
											notification.tickerText = TextUtil
													.getString(R.string.shareactivity_sina_fail);
											notification.flags = Notification.FLAG_AUTO_CANCEL;
											notificationManager.notify(555555,
													notification);
											notificationManager.cancel(555555);
										}
									});
						}

						@Override
						public void onComplete() {
							SharePageActivity.this
									.runOnUiThread(new Runnable() {

										@Override
										public void run() {
											NotificationManager notificationManager = (NotificationManager) SharePageActivity.this
													.getSystemService(Context.NOTIFICATION_SERVICE);
											Notification notification = new Notification();
											PendingIntent contentIntent = PendingIntent
													.getActivity(
															SharePageActivity.this,
															0, new Intent(), 0);
											notification.setLatestEventInfo(
													SharePageActivity.this,
													null, null, contentIntent);
											notification.icon = R.drawable.notification_lestar_icon;
											notification.tickerText = TextUtil
													.getString(R.string.shareactivity_sina_ok);
											notification.flags = Notification.FLAG_AUTO_CANCEL;
											notificationManager.notify(555555,
													notification);
											notificationManager.cancel(555555);
										}
									});
						}
					};
					LetvStarShare.share(SharePageActivity.this,
							getShareContent(userContent.getText().toString()),
							id, type, cid, icon, title, year, director, actor,
							timeLength, letvStarListener);
				}
			}
			return null;

		}

		@Override
		public void onPostExecute(Void result) {

		}

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.top_button_Share:
			if (maxLength < 0) {
				// UIs.call(this, R.string.shareactivity_text140, null);
				UIs.callDialogMsgPositiveButton(SharePageActivity.this, -1,
						R.string.SEVEN_ZERO_TWO_CONSTANT, null);
				return;
			}
			if (!((sinaIsLogin && sinaIsShare)
					|| (tencentWeiboIsLogin && tencentIsShare)
					|| (tencentQzoneIsLogin && qzoomIsShare)
					|| (renrenIsLogin && renrenIsShare) || (letvStarIsLogin && letvStarIsShare))) {
				// UIs.call(this, R.string.shareactivity_chooce, null);
				return;
			}
			new RequestTask(SharePageActivity.this).start();
			((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(
							getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
			finish();
			break;
		case R.id.top_button:
			// new RequestTask(SharePageActivity.this).start();
			((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(
							getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
			SharePageActivity.this.finish();
			break;

		}
	}

	@Override
	public int getContentView() {
		return R.layout.share_page;
	}

	@Override
	public void onResponse(BaseResponse arg0) {
		// TODO Auto-generated method stub

	}

	public static void launchSina(Context activity, int from, String title,
			String icon, int id, int type, int cid, String year,
			String director, String actor, long timeLength, int order, int vid,
			boolean isLive, String liveShare) {

		Intent intent = new Intent(activity, SharePageActivity.class);
		intent.putExtra("from", 1);
		intent.putExtra("title", title);
		intent.putExtra("icon", icon);
		intent.putExtra("id", id);
		intent.putExtra("type", type);
		intent.putExtra("cid", cid);
		intent.putExtra("year", year);
		intent.putExtra("director", director);
		intent.putExtra("actor", actor);
		intent.putExtra("timeLength", timeLength);
		intent.putExtra("order", order);
		intent.putExtra("vid", vid);
		intent.putExtra("isLive", isLive);
		intent.putExtra("liveShare", liveShare);
		intent.putExtra("sina", true);
		activity.startActivity(intent);

	}

}
