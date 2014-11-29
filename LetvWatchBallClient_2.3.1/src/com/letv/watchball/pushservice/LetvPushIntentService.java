package com.letv.watchball.pushservice;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.letv.android.lcm.LetvPushBaseIntentService;
import com.letv.http.bean.LetvDataHull;
import com.letv.http.parse.LetvGsonParser;
import com.letv.watchball.LetvApplication;
import com.letv.watchball.R;
import com.letv.watchball.activity.LetvWebViewActivity;
import com.letv.watchball.activity.WelcomeActivity;
import com.letv.watchball.bean.Game;
import com.letv.watchball.bean.Game.LiveTs;
import com.letv.watchball.bean.LiveList;
import com.letv.watchball.bean.PushMsgBean;
import com.letv.watchball.bean.VideoList;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.ui.PlayLiveController;
import com.letv.watchball.ui.impl.BasePlayActivity;

public class LetvPushIntentService extends LetvPushBaseIntentService {

	private Notification mNotification;

	public LetvPushIntentService(String name) {
		super(name);
	}

	private static final String TAG = "LetvPushIntentService";

	public LetvPushIntentService() {
		super(TAG);
	}

	@Override
	protected void onMessage(Context context, String message, String code,
			String serdId) {
		showMsgNotification(LetvApplication.getInstance(), message);
	}

	// 解析传递过来的json数据
	private String getString(JSONObject jsonObject, String name)
			throws JSONException {
		String value = "";
		if (jsonObject.has(name) == false) {
			return value;
		}
		value = jsonObject.getString(name);
		if ("null".equalsIgnoreCase(value)) {
			value = "";
		}
		return value;
	}

	private PushMsgBean getPushBean(String dataString) {
		try {
			PushMsgBean p = new PushMsgBean();
			JSONObject data = new JSONObject(dataString);
			//Log.e("gongmeng", "JSON object :" + data.toString());
			p.setAt(getString(data, "at"));
			p.setCid(getString(data, "cid"));
			p.setId(getString(data, "id"));
			p.setIsActivate(getString(data, "isActivate"));
			p.setIsOnDeskTop(getString(data, "isOnDeskTop"));
			p.setLiveEndDate(getString(data, "liveEndDate"));
			p.setMsg(getString(data, "msg"));
			p.setNeedJump(getString(data, "needJump"));
			p.setPicUrl(getString(data, "picUrl"));
			p.setResid(getString(data, "resid"));
			p.setTime(getString(data, "time"));
			p.setTitle(getString(data, "title"));
			p.setType(getString(data, "type"));
			return p;
		} catch (JSONException e) {
			//Log.e("gongmeng", "gongmeng" + e.getStackTrace().toString());
		} catch (Exception e) {
			//Log.e("gongmeng", "gongmeng" + e.getStackTrace().toString());
		}
		return null;

	}

	// 同步加载图片，本身是挂起的service所以不需要使用异步处理方法
	public Bitmap returnBitMap(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try {
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			//Log.e("gongmeng", "bitmap is null");
		}
		return bitmap;
	}

	private void showMsgNotification(Context context, String dataString) {
		if (!PreferencesManager.getInstance().isPushservice()) {
			//Log.e("gongmeng", "pushservice is false");
			return;
		}

		PushMsgBean pushMsgBean = getPushBean(dataString);

		if (pushMsgBean == null) {
			//Log.e("gongmeng", "pushmsgbean is null");
			return;
		}

		// 自定义通知中心的样式

		RemoteViews contentView = new RemoteViews(this.getPackageName(),
				R.layout.notify_view);
		contentView.setTextViewText(R.id.push_text, pushMsgBean.getMsg());
		// contentView.setLong(R.id.notify_time, "setTime",
		// System.currentTimeMillis());
		if (pushMsgBean.getPicUrl() != null) {
			try {
				contentView.setImageViewBitmap(R.id.push_icon,
						returnBitMap(pushMsgBean.getPicUrl()));

			} catch (Exception e) {
				contentView.setImageViewResource(R.id.push_icon,
						R.drawable.notify_icon);
			}
		} else {
			contentView.setImageViewResource(R.id.push_icon,
					R.drawable.notify_icon);
		}

		// 自定义点击事件
		Intent intent;

		if (!pushMsgBean.getType().equalsIgnoreCase("")) {
			switch (Integer.valueOf(pushMsgBean.getType())) {
			case 1:
				// TODO
			case 2:
			case 3:
				// 播放单个视频或者专辑
				intent = new Intent(context, BasePlayActivity.class);
				// LAUNCH_MODE_VIDEO = 3
				intent.putExtra("launchMode", 3);
				intent.putExtra("aid", Integer.valueOf(pushMsgBean.getCid()));
				intent.putExtra("vid", Integer.valueOf(pushMsgBean.getResid()));
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				if (!(context instanceof Activity)) {
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				}

				break;
			case 5:
				Game game = requestLiveStream(pushMsgBean.getResid());
				if (game == null) {
					Log.e("gomgmeng", "game is null");
					return;
				}
				if (game.status != 1) {
					if (game.getVid() != 0) {
						intent = new Intent(context, BasePlayActivity.class);
						// LAUNCH_MODE_VIDEO = 3
						intent.putExtra("launchMode", 3);
						intent.putExtra("aid", game.getPid());
						intent.putExtra("vid", game.getVid());
						intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
						if (!(context instanceof Activity)) {
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						}
						break;
					}
					return;
				}
				intent = new Intent(context, BasePlayActivity.class);
				int launchMode;
				if (game.getPid() > 0 && game.getVid() > 0) {
					launchMode = 4;
				} else {
					launchMode = 5;
				}
				LetvApplication.getInstance().saveLiveGame(game);

				intent.putExtra("launchMode", launchMode);
				intent.putExtra(PlayLiveController.LIVE_CODE,
						game.live_350.code);
				intent.putExtra(PlayLiveController.LIVE_STREAMID,
						game.live_350.streamId);
				intent.putExtra(PlayLiveController.LIVE_URL,
						game.live_350.liveUrl);
				intent.putExtra(PlayLiveController.GAME, game);
				intent.putExtra("aid", game.getPid());
				intent.putExtra("vid", game.getVid());
				intent.putExtra("id", game.id);
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				if (!(context instanceof Activity)) {
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				}
				break;
			case 6:
				if (Integer.valueOf(pushMsgBean.getNeedJump()) == 0) {
					intent = new Intent(context, LetvWebViewActivity.class);
				//	Log.e("gongmeng", "resid:" + pushMsgBean.getResid());
					intent.putExtra("url", pushMsgBean.getResid());
					intent.putExtra("loadType", pushMsgBean.getTitle());
					break;
				} else {
					Uri uri = Uri.parse(pushMsgBean.getResid());
					intent = new Intent(Intent.ACTION_VIEW, uri);
					break;
				}

			default:
				intent = new Intent(this, WelcomeActivity.class);
				intent.putExtra("notification", "true");
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				if (!(context instanceof Activity)) {
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				}
				Bundle bundle = new Bundle();
				intent.putExtras(bundle);
				break;

			}
		} else {
			intent = new Intent(this, WelcomeActivity.class);
			intent.putExtra("notification", "true");
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);
			Bundle bundle = new Bundle();
			intent.putExtras(bundle);
		}

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		// 创建通知并发布
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		mNotification = new Notification(R.drawable.notify_icon,
				pushMsgBean.getMsg(), System.currentTimeMillis());

		mNotification.defaults |= Notification.DEFAULT_LIGHTS;
		mNotification.defaults |= Notification.DEFAULT_SOUND;
		mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
		mNotification.flags |= Notification.FLAG_SHOW_LIGHTS;
		mNotification.contentIntent = contentIntent;
		mNotification.contentView = contentView;
		// TODO 这里使用了老的代码中qqzone统治的ID，应当建立文档对这个通知ID进行维护
		notificationManager.notify(3331, mNotification);
	}

	// 获取直播所需要的所有参数，建立起一个直播用的activity
	/**
	 * @param id
	 *            直播流的id
	 */
	private Game requestLiveStream(String id) {
		LetvDataHull<LiveList> dataHull = LetvHttpApi.requestLiveinfos("0",
				new LetvGsonParser<LiveList>(0, LiveList.class));
		LiveList result = dataHull.getDataEntity();
		if (null == result || result.body.length == 0) {
			return null;
		}
		String fristdate = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE)
				.format(Calendar.getInstance().getTime());
		String[] dates = fristdate.split("-");
		String loacldate = dates[dates.length - 1];
		for (int i = 0; i < result.body.length; i++) {
			ArrayList<Game> childrens = new ArrayList<Game>();
			Game[] mLiveInfos = result.body[i].live_infos;
			for (int j = 0; j < mLiveInfos.length; j++) {
				Game game = mLiveInfos[j];
				if (game.id.equalsIgnoreCase(id))
					return game;
			}
		}
		return null;
		/*
		 * String baseUrl =
		 * "http://api.live.letv.com/v1/liveRoom/single/1013?withAllData=1&id="
		 * +id;
		 * 
		 * HttpGet getMethod = new HttpGet(baseUrl); HttpClient httpClient = new
		 * DefaultHttpClient(); Game gameBean = new Game(); try { HttpResponse
		 * response = httpClient.execute(getMethod); JSONObject data = new
		 * JSONObject(EntityUtils.toString(response.getEntity(), "utf-8"));
		 * Log.e("gongmeng", data.toString()); gameBean.pay =
		 * data.getString("isPay"); gameBean.id = data.getString("id");
		 * gameBean.level = data.getString("level2"); gameBean.level0 =
		 * data.getString("level1"); gameBean.home = data.getString("home");
		 * gameBean.guest = data.getString("guest"); if(data.has("homeScore"))
		 * gameBean.homeScore = data.getString("homeScore");
		 * if(data.has("guestScore")) gameBean.guest =
		 * data.getString("guestScore"); if(data.has("isVs")) gameBean.vs =
		 * data.getString("isvs"); gameBean.playTime =
		 * data.getString("beginTime"); gameBean.status =
		 * Integer.valueOf(data.getString("status")); gameBean.pid =
		 * data.getString("pid"); gameBean.vid = ""; gameBean.platform =
		 * "Mobile"; gameBean.ch = "letv_live_sports"; String selectId =
		 * data.getString("selectId"); String uri =
		 * "http://api.live.letv.com/v1/stream/1013/"
		 * +selectId+"?withAllStreams=1"; HttpGet getMethod_stream = new
		 * HttpGet(uri); response = httpClient.execute(getMethod_stream); data =
		 * new JSONObject(EntityUtils.toString(response.getEntity(), "utf-8"));
		 * Log.e("gongmeng", data.toString()); JSONArray streamArray =
		 * data.getJSONArray("rows"); for(int i = 0; i< streamArray.length();
		 * i++) { data = streamArray.getJSONObject(i);
		 * if(data.getString("rateType").equalsIgnoreCase("flv_1000")){
		 * gameBean.live_800 =gameBean.new LiveTs(); gameBean.live_800.code =
		 * "flv_800"; gameBean.live_800.liveUrl = data.getString("streamUrl");
		 * gameBean.live_800.streamId = data.getString("streamName"); //TODO }
		 * if(data.getString("rateType").equalsIgnoreCase("flv_350")){
		 * gameBean.live_350 =gameBean.new LiveTs(); gameBean.live_350.code =
		 * "flv_350"; gameBean.live_350.liveUrl = data.getString("streamUrl");
		 * gameBean.live_350.streamId = data.getString("streamName"); //TODO } }
		 * return gameBean; } catch (ClientProtocolException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } catch (IOException
		 * e) { // TODO Auto-generated catch block e.printStackTrace(); } catch
		 * (ParseException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (JSONException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } return null;
		 */

	}

	/**
	 * @param context
	 * @param deviceToken
	 *            device token changed, application may update the shared
	 *            preference and send the new device token to app server.
	 */
	@Override
	protected void onTokenChanged(Context context, String deviceToken) {
		Log.d(TAG, "token changed=" + deviceToken);
		final SharedPreferences prefs = getSharedPreferences("device_token",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("registration_id", deviceToken);
		editor.commit();
	}

}
