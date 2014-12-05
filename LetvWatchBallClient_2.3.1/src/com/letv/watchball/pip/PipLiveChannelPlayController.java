package com.letv.watchball.pip;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.letv.http.bean.LetvDataHull;
import com.letv.watchball.R;
import com.letv.watchball.async.LetvHttpAsyncTask;
import com.letv.watchball.bean.ExpireTimeBean;
import com.letv.watchball.bean.Game;
import com.letv.watchball.bean.RealLink;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.parser.ExpireTimeParser;
import com.letv.watchball.parser.LiveRealParser;
import com.letv.watchball.ui.PlayLiveController;
import com.letv.watchball.utils.LetvTools;
import com.letv.watchball.utils.LogInfo;
import com.letv.watchball.utils.NetWorkTypeUtils;
import com.letv.watchball.utils.UIs;
import com.letv.watchball.view.PipPlayerView;
import com.letv.watchball.view.PlayLoadLayout;
import com.media.NativeInfos;

public class PipLiveChannelPlayController implements PipPlayController,
		android.media.MediaPlayer.OnErrorListener,
		android.media.MediaPlayer.OnPreparedListener,
		android.media.MediaPlayer.OnCompletionListener,
		com.media.NativePlayer.OnLoadingPerListener {

	protected PipPlayerView activity;
	protected PipMediaController mediaController;
	protected View progressView;
	protected View dialogView;

	protected PipVideoView videoView;

	protected TextView loading_video = null;
	/**
	 * 加载 错误 提示布局
	 * */
	private PlayLoadLayout loadLayout;
	public int today = 0;// 今天

	/**
	 * 是否首次进入本页面
	 */
	protected boolean more;

	/**
	 * 播放时的网络状态
	 */
	protected Boolean netWifi;

	protected boolean isFirst = true;

	protected boolean isRegNetReceiver = false;

	protected Bundle mBundle;

	protected String programName = null;

	protected String mUrl = null;

	protected String mStreamId = null;

	protected String mCode = null;

	protected boolean is3GTip = false;

	protected boolean is3GTipShowing = false;

	protected String realUrl = null;
	private Game game;

	public PipLiveChannelPlayController(Activity activity) {
		// super(activity);
	}

	public PipLiveChannelPlayController(PipPlayerView activity) {
		this.activity = activity;
	}

	protected void initView() {
		progressView = activity.findViewById(R.id.play_progressview);
		RelativeLayout videoContainer = (RelativeLayout) activity
				.findViewById(R.id.videoview_container);
		videoView = new PipVideoView(activity.getContext());
		videoView.setLayoutParams(new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.FILL_PARENT));
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) videoView
				.getLayoutParams();
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, -1);
		videoContainer.addView(videoView);
		dialogView = activity.findViewById(R.id.play_loading_dialog);
		loading_video = (TextView) activity.findViewById(R.id.loading_video);

		// 断网重试或其他获取数据错误的layout
		loadLayout = new PlayLoadLayout(getContext());
		loadLayout.setCallBack(this);
		loadLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		videoContainer.addView(loadLayout);
		loadLayout.loading();
	}

	@Override
	public void onResume() {
		videoView.onResume();
		if (progressView == null || dialogView == null || is3GTipShowing) {
			return;
		}

		if (more) {
			if (progressView.getVisibility() == View.GONE) {
				loadLayout.loading();
				dialogView.setVisibility(View.VISIBLE);
			}
		}
		playUrl(mStreamId, mUrl);
		more = true;
	}

	protected void showNotSupportVfpOrNeonMessage() {
		UIs.notifyLong(getContext(), R.string.liveplay_error_exittip);
		finish();
	}

	protected void showIs3GTipMessage(String url, String title) {
		UIs.notifyLong(activity.getContext(),
				R.string.dialog_messge_pip_mobilenet);
		startPlay(url, title);
	}

	protected void showNotAllowMobileNetworkMessage() {
		UIs.notifyLong(activity.getContext(),
				R.string.dialog_messge_setmobilenet);
		finish();
	}

	protected void createMediaController() {
		mediaController = (PipMediaController) activity
				.findViewById(R.id.pip_pipMediaController);
		mediaController.setVisibility(View.VISIBLE);
		// activity.findViewById(R.id.pip_pipLocalplayerMediaController).setVisibility(View.GONE);
		// // 隐藏本地播放器Controller
		mediaController.setLive(true);
		mediaController.setPlayController(this);
		mediaController.initControllerView();
	}

	protected void showOnErrorMessage() {
		UIs.notifyLong(activity.getContext(), R.string.play_error);
		finish();
	}

	protected void applyWindowFullScreen() {

	}

	protected void noDataNotify() {
		UIs.notifyLong(activity.getContext(), R.string.play_no_data);
		finish();
	}

	protected void finish() {
		activity.finish();
	}

	@Override
	public BaseMediaController getMediaController() {
		return mediaController;
	}

	public Context getContext() {
		return activity.getContext();
	}

	@Override
	public void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		mBundle = bundle;
		NativeInfos.mOffLinePlay = false;
		NativeInfos.mIsLive = true;
		// is3GTip = LetvApplication.getInstance().is3GTip_forPlay();//zlb
		is3GTip = false;
		programName = bundle.getString(PlayLiveController.LIVE_PROGRAM_NAME);
		mUrl = bundle.getString(PlayLiveController.LIVE_URL);
		mStreamId = bundle.getString(PlayLiveController.LIVE_STREAMID);
		mCode = bundle.getString(PlayLiveController.LIVE_CODE);
		game = (Game) bundle.getSerializable(PlayLiveController.GAME);
		initView();
		showLoadingInfo();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		videoView.onPause();
		unregisterNetReceiver();
		videoView.pause();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onFinish() {
		// TODO Auto-generated method stub
		finish();
	}

	@Override
	public String getFrom() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStopTrackingTouch() {
		// TODO Auto-generated method stub

	}

	@Override
	public Bundle getPlayBundle() {
		// TODO Auto-generated method stub
		return mBundle;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

	}

	@Override
	public VideoView getVideoView() {
		// TODO Auto-generated method stub
		return videoView;
	}

	@Override
	public String getVideoTitle() {
		// TODO Auto-generated method stub
		return getTitleName();
	}

	@Override
	public void updateVideoPosition() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isLoadingShown() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onVideoPause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onVideoStart() {
		// TODO Auto-generated method stub
	}

	// @Override
	// public ShackVideoInfo getVideoInfo() {
	// // TODO Auto-generated method stub
	// return null;
	// }

	@Override
	public boolean isPlayingAd() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void handlerAdClick() {
		// TODO Auto-generated method stub

	}

	protected String getTitleName() {
		StringBuilder sb = new StringBuilder();
		if (!TextUtils.isEmpty(programName)) {
			sb.append(programName);
		}
		return sb.toString();
	}

	/**
	 * 进入真实播放环节，首先检查网络
	 * 
	 * @param videoId
	 * @param url
	 * @param title
	 */
	protected void play(final String url, final String title) {
		realUrl = url;
		if (!NativeInfos.ifSupportVfpOrNeon()) {

			showNotSupportVfpOrNeonMessage();

			return;
		}

		if (videoView.isInPlaybackState()) {
			return;
		}

		NetworkInfo networkInfo = NetWorkTypeUtils.getAvailableNetWorkInfo();

		if (networkInfo == null) {
			noDataNotify();
			netWifi = null;
			return;
		}

		if (NetWorkTypeUtils.isWifi()) {
			startPlay(url, title);
			netWifi = true;
			return;
		}

		netWifi = false;

		if (PreferencesManager.getInstance().isAllowMobileNetwork()) {

			if (!is3GTip) {
				showIs3GTipMessage(url, title);
			} else {
				startPlay(url, title);
			}

			return;
		}
		showNotAllowMobileNetworkMessage();
	}

	/**
	 * 开始加载视频，播放
	 * 
	 * @param videoId
	 * @param url
	 * @param title
	 */
	protected void startPlay(String url, String title) {

		registerNetReceiver();
		// startTimer();
		Uri realUri = Uri.parse(url);
		videoView.setVideoURI(realUri);
		videoView.setVideoTitle(title);
		createMediaController();
		videoView.setMediaController(getMediaController());
		videoView.setOnErrorListener(this);
		videoView.setOnCompletionListener(this);
		videoView.setOnPreparedListener(this);
		videoView.setOnLoadingPerListener(this);
		videoView.requestFocus();
		videoView.start();
	}

	protected void registerNetReceiver() {
		if (!isRegNetReceiver) {
			IntentFilter filter = new IntentFilter();
			filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
			getContext().registerReceiver(netChangeReceiver, filter);
			isRegNetReceiver = true;
		}
	}

	protected void unregisterNetReceiver() {
		if (isRegNetReceiver) {
			getContext().unregisterReceiver(netChangeReceiver);
			isRegNetReceiver = false;
		}
	}

	public String getCode() {
		// TODO Auto-generated method stub
		return mCode;
	}

	@Override
	public void onLoadingPer(MediaPlayer mp, int per) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		finish();
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		progressView.setVisibility(View.GONE);
		dialogView.setVisibility(View.GONE);
		loadLayout.finish();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		LogInfo.log("------------------------------------onError");
		return false;
	}

	protected BroadcastReceiver netChangeReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			NetworkInfo networkInfo = NetWorkTypeUtils
					.getAvailableNetWorkInfo();

			if (networkInfo != null) {

				boolean mobile = !NetWorkTypeUtils.isWifi();

				if (mobile) {// 当前为3G网络

					if (netWifi) {// 从wifi网络转过来的

						onPause();// 视频播放必须停止
						play(mStreamId, mUrl);// 重新开始播放
					}
				}
			}
		}
	};

	// protected boolean preparePlay() {
	// if (!TextUtils.isEmpty(mRealUrl)) {
	// return true;
	// }
	// return false;
	// }

	// protected void doPlay() {
	// if (!preparePlay()) {
	// noDataNotify();
	// return;
	// }
	//
	// play(mRealUrl, getTitleName());
	// }

	/**
	 * 显示loading信息
	 */
	protected void showLoadingInfo() {
		loading_video.setText(getContext().getString(R.string.will_play));
	}

	@Override
	public void next() {

	}

	@Override
	public void onRequestErr() {
		onResume();
	}

	@Override
	public void onVipErr(boolean isLogin) {

	}

	@Override
	public void onJumpErr() {

	}

	@Override
	public void onDemandErr() {

	}

	/**
	 * 请求直播地址
	 */
	public void playUrl(String streamId, String url) {
		if (TextUtils.isEmpty(url)) {
			UIs.showToast("播放地址为空");
			finish();
			LetvPipPlayFunction.closePipView(getContext());
		}
		if (TextUtils.isEmpty(streamId)) {
			play(url, getTitleName());
		} else {
			new RequestRealLink(getContext(), streamId, url).start();
		}
	}

	boolean hasInitExpireTime = false;

	public String replaceTm(String tm, String url) {
		if (TextUtils.isEmpty(url)) {
			return null;
		}
		int posT = url.indexOf("tm=");
		/**** add by zlb on 2013-12-09 没有tm则在尾部追加 ****/
		if (posT == -1) {
			return url + "&tm=" + tm;
		}
		/**** end by zlb on 2013-12-09 ****/
		int posE = url.indexOf("&", posT) == -1 ? url.length() : url.indexOf(
				"&", posT);
		return url.replace(url.substring(posT, posE), "tm=" + tm);
	}

	/**
	 * 请求真实的播放地址
	 * 
	 * @author zhanglibin
	 * 
	 */
	protected class RequestRealLink extends LetvHttpAsyncTask<RealLink> {
		String url = null;
		String streamId = null;

		public RequestRealLink(Context context, String streamId, String url) {
			super(context);
			this.url = url;
			this.streamId = streamId;
			// TODO Auto-generated constructor stub
		}

		@Override
		public LetvDataHull<RealLink> doInBackground() {
			// TODO Auto-generated method stub
			LetvDataHull<ExpireTimeBean> hull = null;
			if (!hasInitExpireTime) {// 更新过期时间
				hull = LetvHttpApi
						.getExpireTimestamp(0, new ExpireTimeParser());
				hasInitExpireTime = hull.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY;
			}
			String tm = null;
			if (hasInitExpireTime) {
				tm = String.valueOf(ExpireTimeBean.getTm().getCurServerTime());
			}
			String newUrl = replaceTm(tm, url);
			String encryptUrl = newUrl + "&key="
					+ LetvTools.generateLiveEncryptKey(streamId, tm);
			LetvDataHull<RealLink> result = LetvHttpApi.requestRealLink(0,
					encryptUrl, new LiveRealParser());
			return result;
		}

		@Override
		public void onPostExecute(int updateId, RealLink result) {
			// TODO Auto-generated method stub
			if (result != null) {
				// mRealLink = result.getLocation();
				play(result.getLocation(), getTitleName());
			}
		}

		@Override
		public void netErr(int updateId, String errMsg) {
			// TODO Auto-generated method stub
			loadLayout.requestError();
		}

		@Override
		public void netNull() {
			// TODO Auto-generated method stub
			loadLayout.requestError();
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			// TODO Auto-generated method stub
			loadLayout.requestError();
		}
	}

	protected boolean preparePlay() {
		if (!TextUtils.isEmpty(realUrl)) {
			return true;
		}
		return false;
	}

	protected void doPlay() {
		if (!preparePlay()) {
			noDataNotify();
			return;
		}

		play(mStreamId, mUrl);
	}

	@Override
	public void onPlayFailed() {
		// TODO Auto-generated method stub

	}
}
