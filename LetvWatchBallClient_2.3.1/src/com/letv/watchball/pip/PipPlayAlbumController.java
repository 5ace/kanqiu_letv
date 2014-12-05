package com.letv.watchball.pip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.http.bean.LetvDataHull;
import com.letv.watchball.LetvApplication;
import com.letv.watchball.R;
import com.letv.watchball.async.LetvBaseTaskImpl;
import com.letv.watchball.async.LetvHttpAsyncTask;
import com.letv.watchball.async.LetvSimpleAsyncTask;
import com.letv.watchball.bean.AlbumNew;
import com.letv.watchball.bean.DDUrlsResult;
import com.letv.watchball.bean.Game;
import com.letv.watchball.bean.LocalCacheBean;
import com.letv.watchball.bean.PlayRecord;
import com.letv.watchball.bean.RealPlayUrlInfo;
import com.letv.watchball.bean.TimestampBean;
import com.letv.watchball.bean.Video;
import com.letv.watchball.bean.VideoFile;
import com.letv.watchball.bean.VideoList;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.parser.TimestampParser;
import com.letv.watchball.parser.VideoFileParser;
import com.letv.watchball.parser.VideoListParser;
import com.letv.watchball.parser.VideoParser;
import com.letv.watchball.ui.PlayController;
import com.letv.watchball.ui.PlayLiveController;
import com.letv.watchball.utils.LetvCacheDataHandler;
import com.letv.watchball.utils.LetvPlayRecordFunction;
import com.letv.watchball.utils.LetvTools;
import com.letv.watchball.utils.LogInfo;
import com.letv.watchball.utils.NetWorkTypeUtils;
import com.letv.watchball.utils.UIs;
import com.letv.watchball.view.PipPlayerView;
import com.letv.watchball.view.PlayLoadLayout;
import com.media.NativeInfos;

public class PipPlayAlbumController implements PipPlayController, android.media.MediaPlayer.OnErrorListener,
		android.media.MediaPlayer.OnPreparedListener, android.media.MediaPlayer.OnCompletionListener {
	/*******************布局*******************/
	/**
	 * 悬浮窗的view
	 */
	protected PipPlayerView activity;
	/**
	 * 悬浮窗底部，播放控制条(暂停/开始，下一个，进度条，视频名称，时间)
	 */
	protected PipMediaController mediaController;
	/**
	 * 刚切换到小窗播放时显示的loading框
	 */
	protected View progressView;
	/**
	 * PipVideoView
	 */
	protected VideoView videoView;
	/**
	 * 即将播放textview
	 */
	protected TextView loading_video = null;
	/**
	 * 加载 错误 提示布局
	 * */
	private PlayLoadLayout loadLayout;
	
	/*******************************************/
	
	
	/**
	 * Hanlder msg id
	 */
	protected static final int MSG_PLAYING_ID = 0;
	/**
	 * 播放时每隔1s handler发送一次 msg
	 */
	protected static final int MSG_PLAYING_TIME = 1000;
	
	/**
	 * 传过来的参数，有可能为空
	 */
	protected AlbumNew album;
	/**
	 * Video bean
	 */
	protected Video mVideo;
	/**
	 * VideoFile bean
	 */
	protected VideoFile mVideoFile = null;
	/**
	 * 是否合并
	 * */
	public int merge = 0;
	/**
	 * 在线视频的真实播放地址
	 */
	protected String realUrl = null;
	/**
	 * 是否首次进入本页面
	 */
	protected boolean more;
	/**
	 * 播放时的网络状态
	 */
	protected Boolean netWifi;
	/**
	 * 排序
	 * */
	public String order = "-1";
	/**
	 * 视频ID
	 * */
	public long vid;

	// /**
	// * 缓冲次数
	// */
	// protected int bufferCount = 0;
	/**
	 * 资源加载开始时间
	 */
	protected long loadingStart = 0;

	/**
	 * 资源加载完成时间
	 */
	protected long loadingEnd = 0;

	/**
	 * 播放的开始时间
	 */
	protected long playStart = 0;

	/**
	 * 播放的结束时间时间
	 */
	protected long playEnd = 0;

	/**
	 * 播放的总时间
	 */
	protected long playTotal = 0;

	/**
	 * 专辑 ID
	 * */
	public long aid;

	/**
	 * 视频类型 （专辑 单视频）区别请求详情接口
	 * */
	protected int type;

	/**
	 * 专辑title
	 * */
	protected String albumtitle = "";

	/**
	 * 视频title
	 * */
	protected String episodetitle = "";

	/**
	 * 频道ID
	 * */
	protected int channelId = 0;

	protected boolean isReadySkipFoot = false;

	protected boolean isRegNetReceiver = false;

	protected boolean isPlayHd = false;

	protected boolean is3GTip = false;

	protected Bundle mBundle;

	protected boolean isDolby;

	protected String ptid = null;

	protected String ptype = "1";

	protected String ac = "000_0";

	private int vlen = 0;

	private DDUrlsResult ddUrlsResult;

	private int pageNum = 1;// 剧集列表是哪一页

	/**
	 * 是否是本地文件
	 * */
	private boolean isLocalFile;

	/**
	 * 本地文件地址
	 * */
	private String filePath;

	/**
	 * 片头时间
	 * */
	public long bTime;

	/**
	 * 片尾时间
	 * */
	private long eTime = 0;

	/**
	 * 是否跳过片头片尾
	 */
	boolean isSkip;
	/**
	 * 只有播放本地视频才用(不包括有下载记录的视频)
	 */
	private long seek = 0;
	/**
	 * 所有视频集合，以页数为key
	 * */
	public final HashMap<Integer, VideoList> mVideoListMap = new HashMap<Integer, VideoList>();
	
	protected Handler handler = new Handler() {
		private long pos = 0;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_PLAYING_ID:
				if (videoView.isPlaying()) {
					if (progressView.getVisibility() == View.VISIBLE) {
						if (videoView.getCurrentPosition() > curTime) {
							if (curTime > pos && pos != 0) {
								progressView.setVisibility(View.GONE);
								loadLayout.finish();
							} else {
								pos = curTime;
							}
						}
					} else if (videoView.getCurrentPosition() == curTime) {
						progressView.setVisibility(View.GONE);
					} else if (videoView.getCurrentPosition() > curTime) {
						progressView.setVisibility(View.GONE);
						loadLayout.finish();
					}

					curTime = videoView.getCurrentPosition();
					if(getLaunchMode() == PlayController.PLAY_DEFAULT) {
						localPos = curTime / 1000;
					}
					
					if (playRecord != null) {
						playRecord.setPlayedDuration(curTime / 1000);
					}
				} else {
					loadLayout.finish();
				}
//				if (isSkip && eTime * 1000 > videoView.getDuration() - position) {
//					endPlayingMessage();
//					LogInfo.log("zlb", "跳过片尾：time = " + eTime);
//					onCompletion(null);
//				} else {
					sendPlayingMessage();
//				}
				break;
			}
		}
	};

	/**
	 * 播放是否是高清
	 * */
	public int isHd;
	/**
	 * 当前视频是否有高清
	 * */
	public boolean hasHd;

	/**
	 * 当前视频是否有标清
	 * */
	public boolean hasStandard;

	private int launchMode = PlayController.PLAY_DEFAULT;

	private long lastVid = 0;

	public PipPlayAlbumController(PipPlayerView activity) {
		this.activity = activity;
	}

	@Override
	public void onCreate(Bundle bundle) {
		readArguments(bundle);
		isSkip = PreferencesManager.getInstance().isSkip();
//		if (album != null) {
//			merge = LetvFunction.getMerge(album.getStyle());
//			totle = merge == 0 ? album.getPlatformVideoInfo() : album.getPlatformVideoNum();// 合并与不合并总级数取不一样的字段
//		} else {
//			LogInfo.log("zlb", "pip album is null");
//		}

		initView();
		if (!TextUtils.isEmpty(albumtitle)) {
			showLoadingInfo();
		}
		isPlayHd = false;
		createMediaController();
//		mediaController.initNextBtn(totle);
		
		if (vid == lastVid) {
			mediaController.disableNextBtn();
		}
	}

	@Override
	public void onResume() {
		videoView.onResume();
		if (progressView == null || loadLayout == null) {
			return;
		}
		if (more) {
			if (progressView.getVisibility() == View.GONE) {
				loadLayout.loading();
			}
			if (mVideo != null) {// 如果已经初始化过，继续播放
				if (videoView != null) {
					videoView.start();
				}
				sendPlayingMessage();// 启动handler，刷新时间
			}
//			LetvUtil.ireTrackerEventStart(getContext(), album, mVideo, realUrl, filePath);
		} else {
			if (getLaunchMode() == PlayController.PLAY_ALBUM) {
				new checkPlayRecordTask(getContext(), true, curPage, aid, vid).start();
			} else if (getLaunchMode() == PlayController.PLAY_VIDEO) {
				new checkPlayRecordTask(getContext(), false, 0, aid, vid).start();
			} 
//				else if (getLaunchMode() == PlayController.PLAY_DOWNLOAD) {
//				new checkDownloadTask(getContext()).start();
//				Log.d("live", "-------checkDownloadTask ");
//			} else {
//				Log.d("live", "-------default ");
////				if (!TextUtils.isEmpty(realUrl)) {// 直接给播放地址的播放
////					playLocal(realUrl, (int) position * 1000);
////				}
//				if(!TextUtils.isEmpty(filePath)){
//					playLocal(filePath,(int) localPos * 1000);
//					Log.d("live", "localPos = " + localPos);
//				}
//			}
		}
		more = true;
	}

	@Override
	public void onPause() {
		videoView.onPause();
		videoView.pause();// 暂停播放
		LogInfo.log("MAT_EVENT", "pipMediaC onPause");
//		LetvUtil.ireTrackerEventEnd(getContext(), realUrl, filePath);
		playEnd = System.currentTimeMillis();
		playTotal = playTotal + (playEnd - playStart);

		// cancelTimer();

		updateVideoPosition();// 更新播放记录

		endPlayingMessage();
	}

	protected void doOnPause() {
		unregisterNetReceiver();
		endPlayingMessage();
		videoView.stopPlayback();
	}

	@Override
	public void onDestroy() {
		// unbind三屏service
		destroyTasks();
		clearValue();
	}

	@Override
	public void onFinish() {
		finish();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// unregisterNetReceiver();
		if (aid > 0) {
			onCompletionPlayNext();
		} else {
			finish();
			LetvPipPlayFunction.closePipView(getContext());
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		vlen = videoView.getDuration();
		loadingEnd = System.currentTimeMillis();
		loadLayout.finish();
		getMediaController().updateSkipState();
		sendPlayingMessage();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		return false;
	}

	@Override
	public String getFrom() {
		return null;
	}

	@Override
	public void onStopTrackingTouch() {

	}

	@Override
	public Bundle getPlayBundle() {
		mBundle.putLong("aid", aid);
		mBundle.putLong("vid", vid);
		mBundle.putString("url", filePath);
		mBundle.putLong("seek", localPos);
		mBundle.putSerializable("game", game);
		return mBundle;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

	}

	@Override
	public VideoView getVideoView() {
		return videoView;
	}

	@Override
	public String getVideoTitle() {
		return null == mVideo ? null : mVideo.getNameCn();
	}

	@Override
	public void updateVideoPosition() {
		submitPlayRecord();
	}

	@Override
	public boolean isLive() {
		return false;
	}

	@Override
	public boolean isLoadingShown() {
		return false;
	}

	@Override
	public void onVideoPause() {

	}

	@Override
	public void onVideoStart() {

	}
//
//	@Override
//	public ShackVideoInfo getVideoInfo() {
//		return null;
//	}

	@Override
	public boolean isPlayingAd() {
		return false;
	}

	@Override
	public void handlerAdClick() {

	}

	protected void initView() {
		// 正在加载，即将播出...文字及背景layout
		progressView = activity.findViewById(R.id.play_progressview);
		// videoview container
		RelativeLayout videoContainer = (RelativeLayout) activity.findViewById(R.id.videoview_container);
		// videoview
		videoView = new PipVideoView(activity.getContext());
		videoView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT));
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) videoView.getLayoutParams();
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, -1);
		videoContainer.addView(videoView);
		// 断网重试或其他获取数据错误的layout
		loadLayout = new PlayLoadLayout(getContext());
		loadLayout.setCallBack(this);
		loadLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		videoContainer.addView(loadLayout);
		loadLayout.loading();
		loading_video = (TextView) activity.findViewById(R.id.loading_video);
	}

	protected void show3GTipMessage(int videoId, String url, String title, int order) {
		UIs.notifyLong(activity.getContext(), R.string.dialog_messge_pip_mobilenet);
		// startPlayFinal(videoId, url, title, order);
		return;
	}

	protected void showNotAllowMobileNetworkMessage() {
		UIs.notifyLong(activity.getContext(), R.string.dialog_messge_setmobilenet);
		finish();
	}

	protected void createMediaController() {
		mediaController = (PipMediaController) activity.findViewById(R.id.pip_pipMediaController);
		mediaController.setVisibility(View.VISIBLE);
		mediaController.setAlbum(album);
		mediaController.setPlayController(this);
		mediaController.initControllerView();
	}

	protected void showOnErrorMessage() {
		UIs.notifyLong(activity.getContext(), R.string.play_error);
		finish();
	}

	protected void applyWindowFullScreen() {
		// 画中画不会执行window full screen
	}

	protected void onCompletionPlayNext() {
		NetworkInfo networkInfo = NetWorkTypeUtils.getAvailableNetWorkInfo();
		if(networkInfo== null) {
			finish();
			LetvPipPlayFunction.closePipView(getContext());
			return;
		}
		next();
	}

	protected void showIpInvalidMessage() {
		UIs.notifyLong(activity.getContext(), R.string.no_overseas_play);
		finish();
	}

	protected void noDataNotify() {
		UIs.notifyLong(activity.getContext(), R.string.play_no_data);
		finish();
	}

	public void finish() {
		activity.finish();
	}

	public BaseMediaController getMediaController() {
		return mediaController;
	}

	protected Context getContext() {
		return activity.getContext();
	}

	/**
	 * 显示loading信息
	 */
	protected void showLoadingInfo() {
		String loadingInfo = "";
		loadingInfo = getContext().getString(R.string.will_play);
		loading_video.setText(loadingInfo);
	}

	protected void sendPlayingMessage() {
		if (progressView.getVisibility() == View.VISIBLE) {
			handler.sendEmptyMessageDelayed(MSG_PLAYING_ID, 50);
		} else {
			handler.sendEmptyMessageDelayed(MSG_PLAYING_ID, MSG_PLAYING_TIME);
		}
	}

	protected void endPlayingMessage() {
		handler.removeMessages(MSG_PLAYING_ID);
	}

	// protected boolean skipHead() {
	//
	// boolean skip = PreferencesManager.getInstance().isSkip();
	//
	// if (skip) {
	// // if (episode.getBtime() > 0) {
	// //
	// // if (adFinishSeekPos < episode.getBtime() * 1000) {
	// // videoView.seekTo((int) (episode.getBtime() * 1000 + adTsDuration *
	// 1000));
	// // position = videoView.getCurrentPosition();
	// // UIs.notifyShortNormal(getContext(), R.string.skip_head);
	// // return true;
	// // }
	// // }
	// }
	//
	// return false;
	// }

	protected void attemptSkipFoot() {

		boolean skip = PreferencesManager.getInstance().isSkip();

		if (skip) {
			// if (episode.getEtime() > 0) {
			//
			// if (!isReadySkipFoot
			// && videoView.getCurrentPosition() + 5000 >= episode.getEtime() *
			// 1000) {
			// UIs.notifyLongNormal(getContext(), R.string.skip_foot);
			// isReadySkipFoot = true;
			// }
			//
			// if (videoView.getCurrentPosition() >= episode.getEtime() * 1000)
			// {
			//
			// onCompletion(null);
			// }
			// }
		}

	}

	protected void registerNetReceiver() {

		if (!isRegNetReceiver) {
			if (activity != null) {
				IntentFilter filter = new IntentFilter();
				filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
				activity.getContext().registerReceiver(netChangeReceiver, filter);
				isRegNetReceiver = true;
			}
		}
	}

	protected void unregisterNetReceiver() {
		if (isRegNetReceiver) {
			if (activity != null) {
				activity.getContext().unregisterReceiver(netChangeReceiver);
				isRegNetReceiver = false;
			}
		}
	}

	protected BroadcastReceiver netChangeReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			NetworkInfo networkInfo = NetWorkTypeUtils.getAvailableNetWorkInfo();

			if (networkInfo != null) {

				boolean mobile = !NetWorkTypeUtils.isWifi();

				if (mobile) {// 当前为3G网络

					if (netWifi) {// 从wifi网络转过来的
					// updateVideoPosition();
					// // 视频播放必须停止
					// doOnPause();
					// play(episode.getVid(), realUrl, episode.getTitle(),
					// order);// 重新开始播放
					}
				}
			} else {
				if (PreferencesManager.getInstance().isAllowMobileNetwork()) {

				}
				UIs.notifyShortNormal(context, R.string.network_cannot_use);
			}
		}
	};

	// protected void playNetCheck(final int videoId, final String url, final
	// String title,
	// final int order) {
	// if (NetWorkTypeUtils.isWifi()) {
	// startPlay(videoId, url, title, order);
	// netWifi = true;
	// return;
	// }
	//
	// netWifi = false;
	//
	// if (PreferencesManager.getInstance().isAllowMobileNetwork()) {
	// if (!is3GTip) {
	// show3GTipMessage(videoId, url, title, order);
	// } else {
	// startPlay(videoId, url, title, order);
	// }
	// } else {
	// showNotAllowMobileNetworkMessage();
	// }
	// }

	/**
	 * 播放视频的播放记录
	 * */
	public PlayRecord playRecord;

	/**
	 * 总时间
	 * */
	private long totleTime;

	/**
	 * 当前时间
	 * */
	private long curTime;

	// 在暂停的时候提交播放记录，以便下次播放该视频时，从中断的地方开始播放
	private void submitPlayRecord() {
		if (playRecord != null) {
			LetvPlayRecordFunction.submitPlayTraces(getContext(), playRecord.getChannelId(), playRecord.getAlbumId(), playRecord.getVideoId(),
					playRecord.getVideoNextId(), playRecord.getType(), playRecord.getTotalDuration(), playRecord.getPlayedDuration(), playRecord.getTitle(),
					playRecord.getImg(), playRecord.getCurEpsoid());
		}
	}

	/**
	 * 初始化播放记录，在没有播放记录的情况下
	 * */
	public void createPlayRecord() {
		if (playRecord == null) {
			playRecord = new PlayRecord();

			playRecord.setAlbumId((int) aid);
			playRecord.setVideoId((int) vid);
			if (album != null) {
				playRecord.setVideoType(album.getType());
			} else if (mVideo != null) {
				playRecord.setVideoType(mVideo.getType());
			}
			playRecord.setTitle(mVideo.getNameCn());
			playRecord.setChannelId(mVideo.getCid());
			playRecord.setImg(mVideo.getPic());
			playRecord.setFrom(2);
			playRecord.setCurEpsoid(mVideo.getEpisode());
			if (curTime > 0) {
				playRecord.setPlayedDuration(curTime / 1000);
			} else {
				playRecord.setPlayedDuration(0);
			}
			playRecord.setTotalDuration(mVideo.getDuration());
			curTime = playRecord.getPlayedDuration() * 1000;
			totleTime = playRecord.getTotalDuration() * 1000;
			playRecord.setUpdateTime(System.currentTimeMillis());
		}
	}

	/**
	 * 记录请求的任务
	 * */
	public List<LetvBaseTaskImpl> tasks = new ArrayList<LetvBaseTaskImpl>();

	public void setVideo(Video video) {
		this.mVideo = video;
		mediaController.setVideo(video);
		if (null != video) {
			eTime = video.getEtime();
			bTime = video.getBtime();
			mediaController.setTitle(video.getNameCn());
		} else {
			eTime = 0;
			bTime = 0;
		}
	}

	public void setFilePath(String path) {
		filePath = path;
		mediaController.setFilePath(path);
	}

	public void setIsLocalFile(boolean local) {
		isLocalFile = local;
		mediaController.setPlayNet(!local);
		if (local) {
			mediaController.setRealUrl(null);
			realUrl = null;
		}
	}
	
	public boolean getIsLocalFile() {
		return isLocalFile;
	}
	/**
	 * 专辑中视频总数
	 * */
	public int totle = 0;
	@Override
	public void next() {
		loadLayout.loading();
		Video v = mVideo;
		curTime = 0;
		setIsLocalFile(false);
		filePath = null;
		if (null == mVideo) {
			return;
		}
		if (mVideoListMap != null && mVideoListMap.size() > 0) {
			Iterator<Integer> ir = mVideoListMap.keySet().iterator();// 页码的keyset
			int pos = -1;
			int page = -1;
			int tempTotal = 0;
			// 查找当前的视频所在的page和在那个page中的list中的position
			while (ir.hasNext()) {
				page = ir.next();
				VideoList list = mVideoListMap.get(page);
				tempTotal += null == list ? 0 : list.size();
				if (list != null && list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						if (v.getId() == list.get(i).getId()) {
							pos = i;
							break;
						}
					}
					// mVideoListMap为空
					if (pos != -1 && page != -1) {
						break;
					}
				}
			}
			totle = Math.max(tempTotal, totle);

			if (pos == -1 || page == -1) {
				finish();
				return;
			} else {
				VideoList videoList = mVideoListMap.get(page);
				if (pos < videoList.size() - 1) {
					play(videoList.get(pos + 1));
					if (pageSize * (page - 1) + pos + 1 == totle - 1 && totle > 1) {
						mediaController.disableNextBtn();
					} else {
						mediaController.enableNextBtn();
					}
					return;
				} else {
					if (pageSize * (page - 1) + pos + 1 < totle) {

						if (pos < videoList.size()) {
							if (pos + 1 == videoList.size()) {// 本页最后一条数据
								if (mVideoListMap.containsKey(page + 1)) {// 如果已经有下页数据，播放
									VideoList list = mVideoListMap.get(page + 1);
									if (list != null && list.size() > 0) {
										play(list.get(0));
										return;
									} else {
										this.curPage = page + 1;
										destroyTasks();
										doOnPause();
										playRecord = null;
										vid = 0;
										new RequestVideoList(activity.getContext(), true, curPage, aid, vid).start();
										return;
									}
								} else {// 如果没有下页数据，请求再播放
									this.curPage = page + 1;
									destroyTasks();
									doOnPause();
									playRecord = null;
									vid = 0;
									new RequestVideoList(activity.getContext(), true, curPage, aid, vid).start();
									return;
								}
							} else {// 本页直接去下集播放
								play(videoList.get(pos + 1));
								return;
							}
						} else {
							finish();
						}
					} else {// 最后一条视频，关闭
						finish();
						LetvPipPlayFunction.closePipView(getContext());
					}
				}
			}
		} else {// 没有视频列表，关闭
			LetvPipPlayFunction.closePipView(getContext());
			finish();
		}
		
	}

	/**
	 * 请求播放视频的调度地址
	 * */
	private class RequestVideoFile extends LetvHttpAsyncTask<VideoFile> {

		String mid;

		boolean needCheckCanPlay = true;

		public RequestVideoFile(Context context) {
			super(context);
			tasks.add(this);
			mid = mVideo.getMid();
			this.needCheckCanPlay = true;
		}

		public RequestVideoFile(Context context, boolean needCheckCanPlay) {
			super(context);
			mid = mVideo.getMid();
			this.needCheckCanPlay = needCheckCanPlay;
		}

		@Override
		public boolean onPreExecute() {
			if (isLocalFile && !TextUtils.isEmpty(filePath)) {
				tasks.remove(this);
				return false;
			}
			return true;
		}

//		@Override
//		public VideoFile loadLocalData() {
//
//			DownloadDBBean info = LetvFunction.canPlayLocal(vid);
//			if (info != null) {
//				setIsLocalFile(true);
//				// filePath = info.getFilePath();
//				setFilePath(info.getFilePath());
//				isHd = !(info.getIsHd() == 0);
//				hasHd = false;
//				hasStandard = false;
//				return new VideoFile();
//			} else {
//				setIsLocalFile(false);
//				filePath = null;
//			}
//
//			return null;
//		}
//
//		@Override
//		public boolean loadLocalDataComplete(VideoFile t) {
//			play();
//			return isLocalFile;
//		}

		@Override
		public LetvDataHull<VideoFile> doInBackground() {
			if (isLocalSucceed()) {
				return null;
			}

			if (mid.equals(mVideo.getMid())) {
				LetvDataHull<VideoFile> dataHull = null;
				String tm = String.valueOf(TimestampBean.getTm().getCurServerTime());
				String key = LetvTools.generateVideoFileKey(mid, tm);
				if (isDolby) {
					dataHull = LetvHttpApi.requestVideoFile(0, mid, "0", "no", tm, key, new VideoFileParser(mVideo.getPay() == 2));
				} else {
					dataHull = LetvHttpApi.requestVideoFile(0, mid, "0", LetvApplication.getInstance().getVideoFormat(), tm, key,
							new VideoFileParser(mVideo.getPay() == 2));
				}

				if (dataHull != null && dataHull.getErrMsg() == 5) {
					LetvDataHull<TimestampBean> dh = LetvHttpApi.getTimestamp(0, new TimestampParser());
					if (dh != null && dh.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
						if (mid.equals(mVideo.getMid())) {
							tm = String.valueOf(TimestampBean.getTm().getCurServerTime());
							key = LetvTools.generateVideoFileKey(mid, tm);
							if (isDolby) {
								dataHull = LetvHttpApi.requestVideoFile(0, mid, "0", "no", tm, key, new VideoFileParser(mVideo.getPay() == 2));
							} else {
								dataHull = LetvHttpApi.requestVideoFile(0, mid, "0", LetvApplication.getInstance().getVideoFormat(), tm, key,
										new VideoFileParser(mVideo.getPay() == 2));
							}
						}
					}
				}

				return dataHull;
			}

			return null;
		}

		@Override
		public void onPostExecute(int updateId, VideoFile result) {
			tasks.remove(this);
			if (mid.equals(mVideo.getMid())) {
				new RequestRealPlayUrl(context, result, mid).start();
			}
		}

		@Override
		public void netNull() {
			tasks.remove(this);
			endPlayingMessage();
//			UIs.showToast("RequestVideoFile net netNull");
			loadLayout.requestError();
		}

		@Override
		public void netErr(int updateId, String errMsg) {
			tasks.remove(this);
			endPlayingMessage();
//			UIs.showToast("RequestVideoFile net netErr");
			loadLayout.requestError();
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			tasks.remove(this);
			if (mid.equals(mVideo.getMid())) {
				loadLayout.requestError();
			}
		}

		@Override
		public void noUpdate() {
			tasks.remove(this);
			super.noUpdate();
		}
	}

	private Uri playUri;

	/**
	 * 播放,兼容在线与本地播放器
	 * */
	public void playLocal(String uriString, int msec) {
		loadLayout.finish();
		NativeInfos.mIsLive = false;
		NativeInfos.mOffLinePlay = true;
		NativeInfos.doWithNativePlayUrl(uriString);
		playUri = Uri.parse(uriString);
		videoView.setVideoURI(playUri);
		videoView.setVideoTitle(getVideoTitle());
		videoView.setMediaController(getMediaController());
		videoView.setOnErrorListener(this);
		videoView.setOnCompletionListener(this);
		videoView.setOnPreparedListener(this);
		videoView.requestFocus();
		if (msec > 0) {
			videoView.seekTo(msec);
		}
		videoView.start();
	}

	/**
	 * 播放在线
	 * */
	public void playNet(String uriString, boolean isLive, boolean isDolby, int msec) {
		loadLayout.finish();
		NativeInfos.mOffLinePlay = false;
		NativeInfos.mIsLive = isLive;
		initNativeInfos();
		if (isDolby) {
			NativeInfos.mOffLinePlay = true;
			NativeInfos.mIfNative3gpOrMp4 = true;
		}
		playUri = Uri.parse(uriString);
		videoView.setVideoURI(playUri);
		videoView.setMediaController(getMediaController());
		videoView.setOnErrorListener(this);
		videoView.setOnCompletionListener(this);
		videoView.setOnPreparedListener(this);
		videoView.requestFocus();
		if (msec > 0) {
			videoView.seekTo(msec);
		}
		videoView.start();
	}

	/**
	 * 请求真是播放地址
	 * */
	private class RequestRealPlayUrl extends LetvHttpAsyncTask<RealPlayUrlInfo> {

		private final VideoFile videoFile;
		private String mid;
		private DDUrlsResult ddUrlsResult;

		public RequestRealPlayUrl(Context context, VideoFile videoFile, String mid) {
			super(context);
			tasks.add(this);
			this.videoFile = videoFile;
			this.mid = mid;
		}

		@Override
		public boolean onPreExecute() {
			if (mid.equals(mVideo.getMid())) {
				int isHd = PreferencesManager.getInstance().isPlayHd();
				if (isHd!=0) {
					if (!PlayUtils.isSupportHd(mVideo.getBrList())) {
						isHd = 0;
					}
				} 
				DDUrlsResult ddUrlsResult = PlayUtils.getDDUrls(videoFile, isHd, isDolby);
				if (ddUrlsResult != null && ddUrlsResult.getDdurls() != null && ddUrlsResult.getDdurls().length > 0) {
					PipPlayAlbumController.this.isHd = ddUrlsResult.isHd();
					PipPlayAlbumController.this.isDolby = ddUrlsResult.isDolby();
					PipPlayAlbumController.this.hasHd = ddUrlsResult.isHasHigh();
					PipPlayAlbumController.this.hasStandard = ddUrlsResult.isHasLow();
					this.ddUrlsResult = ddUrlsResult;
					return true;
				} else {
					if (mid.equals(mVideo.getMid())) {
						// loadLayout.notPlay();
					}
				}
			}
			return false;
		}

		@Override
		public LetvDataHull<RealPlayUrlInfo> doInBackground() {
			if (mid.equals(mVideo.getMid())) {
				LetvDataHull<RealPlayUrlInfo> dataHull = PlayUtils.getRealUrl(ddUrlsResult.getDdurls());
				if (dataHull.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
					return dataHull;
				}
			}
			return null;
		}

		@Override
		public void onPostExecute(int updateId, RealPlayUrlInfo result) {
			tasks.remove(this);
			if (200 == result.getCode() && mid.equals(mVideo.getMid())) {
				realUrl = result.getRealUrl();
				isLocalFile = false;
				play();
			}
		}

		@Override
		public void netNull() {
			tasks.remove(this);
			endPlayingMessage();
//			UIs.showToast("RequestRealPlayUrl net netNull");
			loadLayout.requestError();
			if (mid.equals(mVideo.getMid())) {
				// loadLayout.requestError();
				// playCallBackState = 6;
				// statisticsVideoInfo.setErr("1");
			}
		}

		@Override
		public void netErr(int updateId, String errMsg) {
			tasks.remove(this);
			endPlayingMessage();
//			UIs.showToast("RequestRealPlayUrl net netErr");
			loadLayout.requestError();
			if (mid.equals(mVideo.getMid())) {
				// loadLayout.requestError();
				// playCallBackState = 6;
				// statisticsVideoInfo.setErr("1");
			}
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			tasks.remove(this);
			if (mid.equals(mVideo.getMid())) {
				// loadLayout.requestError();
				// playCallBackState = 6;
				// statisticsVideoInfo.setErr("1");
			}
		}

		@Override
		public void noUpdate() {
			tasks.remove(this);
			super.noUpdate();
		}
	}

	/**
	 * 开始播放
	 */
	private void play() {
		int msec = 0;
//		if (mVideo != null && mVideo.getDuration() < 180) {
//			playRecord.setPlayedDuration(0);
//		}
		if (isSkip && curTime <= 0 && playRecord.getPlayedDuration() <= 0) {
			msec = (int) bTime * 1000;
		} else {
			msec = (int) playRecord.getPlayedDuration() * 1000;
		}
		if (isLocalFile) {
			playLocal(filePath, msec);
			realUrl = null;
		} else {
			filePath = null;
			playNet(realUrl, false, isDolby, (int) msec);
		}
//		LetvUtil.ireTrackerEventStart(getContext(), album, mVideo, realUrl, filePath);
	}

	/**
	 * 初始化播放器类型 本地播放器还是系统播放器
	 * */
	public void initNativeInfos() {
		String vf = LetvApplication.getInstance().getVideoFormat();
		if ("ios".equals(vf)) {
			NativeInfos.mOffLinePlay = false;
			NativeInfos.mIsLive = false;
		} else if ("no".equals(vf)) {
			NativeInfos.mOffLinePlay = true;
			NativeInfos.mIfNative3gpOrMp4 = true;
			NativeInfos.mIsLive = false;
		}
	}

	public int getLaunchMode() {
		return launchMode;
	};
	/**
	 * 本地视频(不包括有下载记录的)播放到的位置
	 */
    private long localPos;
    private Game game;
    
	public void readArguments(Bundle bundle) {
		mBundle = bundle;
		isPlayHd = (PreferencesManager.getInstance().isPlayHd()==1);

		// is3GTip = LetvApplication.getInstance().is3GTip_forPlay();//delete by
		// zlb

		vid = bundle.getLong("vid", 0);// vid不为0，优先以vid播放

		type = bundle.getInt("type");

		channelId = bundle.getInt("channelId");
		
		game = (Game) bundle.getSerializable(PlayLiveController.GAME);
		
		albumtitle = bundle.getString("albumtitle");

		episodetitle = bundle.getString("episodetitle");

		aid = bundle.getLong("aid");
		isDolby = bundle.getBoolean("isDolby");

		launchMode = bundle.getInt("launch_mode");

		album = (AlbumNew) bundle.getSerializable("album");

		lastVid = bundle.getLong("lastvid");
		
		filePath = bundle.getString("url");
		localPos = bundle.getLong("seek");
	}

	/**
	 * 播放 只针对本专辑下的视频
	 * */
	public void play(Video video) {
		if (video.getId() != vid) {
			doOnPause();
			this.vid = video.getId();
			this.setVideo(video);
			setIsLocalFile(false);
			filePath = null;
			playRecord = null;
			createPlayRecord();
			new RequestVideoFile(activity.getContext()).start();
		}
	}

	/**
	 * 检查是否有播放记录
	 * */
	private class checkPlayRecordTask extends LetvSimpleAsyncTask<PlayRecord> {

		private boolean isAlbum;
		private int page = 1;
		private long albumId = 0;
		private long videoId = 0;

		public checkPlayRecordTask(Context context, boolean isAlbum, int page, long albumId, long videoId) {
			super(context, false);
			tasks.add(this);
			this.page = page;
			this.albumId = albumId;
			this.videoId = videoId;
			this.isAlbum = isAlbum;
		}

		@Override
		public PlayRecord doInBackground() {
			if (playRecord == null) {// 如果播放记录已经生成，直接跳过
				PlayRecord playRecord = null;

				if (isAlbum) {
					if (videoId > 0) {
						playRecord = LetvPlayRecordFunction.getPoint(0, (int) videoId, false);
					} else {
						if (page == 1) {
							playRecord = LetvPlayRecordFunction.getPoint((int) albumId, 0, false);
						}
					}
				} else {
					playRecord = LetvPlayRecordFunction.getPoint(0, (int) videoId, false);
				}

				return playRecord;
			}

			return playRecord;
		}

		@Override
		public void onPostExecute(PlayRecord result) {
			tasks.remove(this);
			playRecord = result;
			if (playRecord != null) {
				if (curTime > 0) {
					playRecord.setPlayedDuration(curTime / 1000);
				}
				vid = playRecord.getVideoId();
				curTime = playRecord.getPlayedDuration() * 1000;
				totleTime = playRecord.getTotalDuration() * 1000;
			}
			if (isAlbum) {
				new RequestVideoList(context, true, page, aid, vid).start();
			} else {
				new RequestVideo(context).start();
			}
		}
	}
	/**
	 * 记录请求的任务
	 * */
	/**
	 * 视频列表当前页页码
	 * */
	public int curPage = 1;
	/**
	 * 一页的条数
	 * */
	public int pageSize = 60;
	/**
	 * 请求视频列表（针对专辑类型）
	 * */
	private class RequestVideoList extends LetvHttpAsyncTask<VideoList> {

		private boolean isPlay;

		private int page;

		private String markId;

		private long videoId;

		private long albumId;

		private int localDataPos;

		public RequestVideoList(Context context, boolean isPlay, int page, long aid, long vid) {
			super(context);
			tasks.add(this);
			this.isPlay = isPlay;
			this.page = page;
			this.videoId = vid;
			this.albumId = aid;
		}

		@Override
		public VideoList loadLocalData() {
			try {
				LocalCacheBean bean = null;
				if (videoId > 0) {
					List<LocalCacheBean> beans = LetvCacheDataHandler.readDetailVLData(String.valueOf(albumId));
					if (beans != null && beans.size() > 0) {
						for (LocalCacheBean b : beans) {
							if (b.getCacheData() != null && b.getCacheData().contains(String.valueOf(videoId))) {
								bean = b;
								break;
							}
						}
					}
				} else {
					bean = LetvCacheDataHandler.readDetailVLData(String.valueOf(albumId), String.valueOf(page), String.valueOf(pageSize), order,
							String.valueOf(merge));
				}

				VideoList videoList = null;

				if (bean != null) {
					VideoListParser listParser = new VideoListParser();
					videoList = listParser.initialParse(bean.getCacheData());
					if (videoList != null && videoList.size() > 0) {
						if (videoId > 0) {
							for (int i = 0; i < videoList.size(); i++) {
								if (videoId == videoList.get(i).getId()) {
									localDataPos = i;
								}
							}
						}
						markId = bean.getMarkId();
						setVideo(videoList.get(localDataPos));
						return videoList;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			markId = null;
			return null;
		}

		@Override
		public boolean loadLocalDataComplete(VideoList result) {
			if (result != null) {
				int p = result.getPagenum();
				if (isPlay) {
					if (p <= 0) {
						curPage = page;
						setVideo(result.get(0));
						vid = mVideo.getId();
					} else {
						curPage = p;
						setVideo(result.get(localDataPos));
						vid = mVideo.getId();
					}

					createPlayRecord();

					if (playRecord != null) {
						playRecord.setTotalDuration(mVideo.getDuration());
						totleTime = playRecord.getTotalDuration() * 1000;
					}

					new RequestVideoFile(context).start();
				} else {
					if (p <= 0) {
						curPage = page;
					} else {
						curPage = p;
					}
				}
				mVideoListMap.put(curPage, result);
				return true;
			}
			return false;
		}

		@Override
		public LetvDataHull<VideoList> doInBackground() {

			VideoListParser parser = new VideoListParser();
			LetvDataHull<VideoList> dataHull = LetvHttpApi.requestAlbumVideoList(0, String.valueOf(albumId), String.valueOf(videoId), String.valueOf(page),
					String.valueOf(pageSize), order, String.valueOf(merge), markId, parser);
			if (dataHull.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
				if (dataHull.getDataEntity() == null) {
					return null;
				}
				int p = dataHull.getDataEntity().getPagenum();
				if (p <= 0) {
					p = page;
				}

				try {
					String dataString = dataHull.getSourceData();
					JSONObject jsonObject = new JSONObject(dataString);
					JSONObject bodyObject = jsonObject.optJSONObject("body");
					if (bodyObject != null && !bodyObject.has("pagenum")) {
						bodyObject.put("pagenum", p);
					}
					LetvCacheDataHandler.saveDetailVLData(markId, jsonObject.toString(), String.valueOf(albumId), String.valueOf(p), String.valueOf(pageSize),
							order, String.valueOf(merge));
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			return dataHull;
		}

		@Override
		public void onPostExecute(int updateId, VideoList result) {
			tasks.remove(this);
			int p = result.getPagenum();
			if (isPlay && !isLocalSucceed()) {
				if (p <= 0) {
					curPage = page;
					setVideo(result.get(0));
					vid = mVideo.getId();
				} else {
					curPage = p;
					setVideo(result.get(result.getVideoPosition() - 1));
					vid = mVideo.getId();
				}

				createPlayRecord();
				if (playRecord != null) {
					playRecord.setTotalDuration(mVideo.getDuration());
					totleTime = playRecord.getTotalDuration() * 1000;
				}
				new RequestVideoFile(context).start();
			} else {
				if (p <= 0) {
					curPage = page;
				} else {
					curPage = p;
				}
			}

			mVideoListMap.put(curPage, result);
		}

		@Override
		public void netNull() {
			tasks.remove(this);
			if (isLocalFile) {
				return;
			}
			endPlayingMessage();
//			UIs.showToast("RequestVideoList net null");
			loadLayout.requestError();
			// videosCallBackState = PlayAlbumControllerCallBack.STATE_NET_NULL;
			// if (videosCallBack != null)
			// videosCallBack.notify(videosCallBackState);
			// if (isPlay) {
			// loadLayout.requestError();
			// playCallBackState = 3;
			// statisticsVideoInfo.setErr("2");
			// }
		}

		@Override
		public void netErr(int updateId, String errMsg) {
			tasks.remove(this);
			if (isLocalFile) {
				return;
			}
			endPlayingMessage();
//			UIs.showToast("RequestVideoList net netErr");
			loadLayout.requestError();
			// videosCallBackState = PlayAlbumControllerCallBack.STATE_NET_ERR;
			// if (videosCallBack != null)
			// videosCallBack.notify(videosCallBackState);
			// if (isPlay) {
			// loadLayout.requestError();
			// playCallBackState = 3;
			// statisticsVideoInfo.setErr("2");
			// }
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			tasks.remove(this);
			if (isLocalFile) {
				return;
			}
			// videosCallBackState =
			// PlayAlbumControllerCallBack.STATE_DATA_NULL;
			// if (videosCallBack != null)
			// videosCallBack.notify(videosCallBackState);
			// if (isPlay) {
			// loadLayout.requestError();
			// playCallBackState = 3;
			// statisticsVideoInfo.setErr("2");
			// }
		}

		@Override
		public void noUpdate() {
			tasks.remove(this);
			super.noUpdate();
		}
	}
	/**
	 * 请求视频详情
	 * */
	private class RequestVideo extends LetvHttpAsyncTask<Video> {

		private String markId;

		public RequestVideo(Context context) {
			super(context);
			tasks.add(this);
		}

		@Override
		public Video loadLocalData() {
			try {
				LocalCacheBean bean = LetvCacheDataHandler.readDetailData(String.valueOf(vid));
				if (bean != null) {
					Video video = null;
					VideoParser videoParser = new VideoParser();
					video = videoParser.initialParse(bean.getCacheData());
					markId = bean.getMarkId();

					return video;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public boolean loadLocalDataComplete(Video result) {
			if (result != null) {
				setVideo(result);
				vid = mVideo.getId();
				LogInfo.log("zz", "RequestVideo vid = " + vid);
				createPlayRecord();

				if (playRecord != null) {
					playRecord.setTotalDuration(mVideo.getDuration());
					totleTime = playRecord.getTotalDuration() * 1000;
				}

				new RequestVideoFile(context).start();

				return true;
			}

			return false;
		}

		@Override
		public LetvDataHull<Video> doInBackground() {
			if (!isLocalSucceed()) {
				markId = null;
			}
			VideoParser parser = new VideoParser();
			LetvDataHull<Video> dataHull = LetvHttpApi.requestAlbumVideoInfo(0, String.valueOf(vid), "video", markId, parser);
			if (dataHull.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
				LetvCacheDataHandler.saveDetailData(parser.getMarkId(), dataHull.getSourceData(), String.valueOf(vid));
			}
			return dataHull;
		}

		@Override
		public void onPostExecute(int updateId, Video result) {
			tasks.remove(this);
			setVideo(result);
			vid = mVideo.getId();
			LogInfo.log("zz", "RequestVideo vid = " + vid + " , video etime = " + result.getEtime() + " , btime = " + result.getBtime());
			if (!isLocalSucceed()) {
				createPlayRecord();
				if (playRecord != null) {
					playRecord.setTotalDuration(mVideo.getDuration());
					totleTime = playRecord.getTotalDuration() * 1000;
				}
				new RequestVideoFile(context).start();
			}
		}

		@Override
		public void netNull() {
			tasks.remove(this);
			endPlayingMessage();
//			UIs.showToast("RequestVideo net netNull");
			loadLayout.requestError();
			if (isLocalFile) {
				return;
			}
			// loadLayout.requestError();
			// playCallBackState = 2;
			// statisticsVideoInfo.setErr("2");
		}

		@Override
		public void netErr(int updateId, String errMsg) {
			tasks.remove(this);
			if (isLocalFile) {
				return;
			}
//			UIs.showToast("RequestVideo net netErr");
			loadLayout.requestError();
			// loadLayout.requestError();
			// playCallBackState = 2;
			// statisticsVideoInfo.setErr("2");
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			tasks.remove(this);
			if (isLocalFile) {
				return;
			}
			// loadLayout.requestError();
			// playCallBackState = 2;
			// statisticsVideoInfo.setErr("2");
		}

		@Override
		public void noUpdate() {
			tasks.remove(this);
			super.noUpdate();
		}
	}

	/**
	 * 清除请求
	 * */
	private void destroyTasks() {
		// 清空
		for (LetvBaseTaskImpl taskImpl : tasks) {
			if (taskImpl != null && !taskImpl.isCancelled()) {
				taskImpl.cancel();
			}
		}
		tasks.clear();
	}

	/**
	 * 清除数据
	 * */
	private void clearValue() {
		aid = 0;
		vid = 0;
		setVideo(null);
		hasHd = false;
		hasStandard = false;
		isHd = 0;
		isDolby = false;
		playRecord = null;
		merge = 0;
		order = "-1";
		realUrl = null;
		setIsLocalFile(false);
		filePath = null;
		curTime = 0;
		destroyTasks();
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

	@Override
	public void onPlayFailed() {
		
	}
}
