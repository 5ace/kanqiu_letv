package com.letv.watchball.ui.impl;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.letv.http.bean.LetvDataHull;
import com.letv.http.parse.LetvGsonParser;
import com.letv.watchball.R;
import com.letv.watchball.async.LetvHttpAsyncTask;
import com.letv.watchball.bean.AlbumNew;
import com.letv.watchball.bean.Comments;
import com.letv.watchball.bean.LocalCacheBean;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.parser.AlbumNewParse;
import com.letv.watchball.ui.IntroductionBuilder;
import com.letv.watchball.ui.LetvBaseFragment;
import com.letv.watchball.ui.PlayAlbumController;
import com.letv.watchball.ui.PlayLiveController;
import com.letv.watchball.ui.PlayAlbumController.PlayAlbumControllerCallBack;
import com.letv.watchball.ui.PlayController;
import com.letv.watchball.utils.LetvCacheDataHandler;
import com.letv.watchball.utils.LogInfo;
import com.letv.watchball.utils.UIs;
import com.letv.watchball.view.PublicLoadLayout;

public class HalfPlayIntroductionFragment extends LetvBaseFragment implements PlayAlbumControllerCallBack,OnItemClickListener{

	private PlayController playController;

	private PublicLoadLayout root;
	/**
	 * 直播
	 */
	public static final int LAUNCH_MODE_LIVE = 4;
	/**
	 * 直播 全屏直播
	 */
	public static final int LAUNCH_MODE_LIVE_FULL = 5;
	
	
	private PlayAlbumController playAlbumController;
	private PlayLiveController  PlayLiveController ;
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = UIs.createPage(getActivity(), R.layout.detailplay_half_introduction);
		root.setPadding(1, 0, 1, 0);
		root.setBackgroundResource(R.color.letv_color_ffdfdfdf);
		return root;
	}

	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(((BasePlayActivity) getActivity()).mPlayController.getLaunchMode()==LAUNCH_MODE_LIVE||((BasePlayActivity) getActivity()).mPlayController.getLaunchMode()==LAUNCH_MODE_LIVE_FULL){
			PlayLiveController = (PlayLiveController) ((BasePlayActivity) getActivity()).mPlayController;
			playController = PlayLiveController;
		}else{
			playAlbumController = (PlayAlbumController) ((BasePlayActivity) getActivity()).mPlayController;
			playController = playAlbumController;
		}

		playController.introductionCallBack = this;

		handlerData();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onDestroyView() {
//		Log.d("lhz", "HalfPlayIntroductionFragment.onDestroyView");
		super.onDestroyView();
		cancel();
		this.root = null;
		this.playController.introductionCallBack = null;
		this.playController = null;
	}

	/**
	 * 根据状态完成数据载入
	 * */
	private void handlerData() {
		switch (playController.introductionCallBackState) {
		case PlayAlbumControllerCallBack.STATE_RUNNING:
			if (root != null) {
				root.loading(true);
			}
			break;
		case PlayAlbumControllerCallBack.STATE_FINISH:
			if (root != null) {
				root.finish();
				if(((BasePlayActivity) getActivity()).mPlayController.getLaunchMode()==LAUNCH_MODE_LIVE||((BasePlayActivity) getActivity()).mPlayController.getLaunchMode()==LAUNCH_MODE_LIVE_FULL){
					IntroductionBuilder.build(playController.getAlbum(), root,PlayLiveController.game);
				}else{
					IntroductionBuilder.build(playController.getAlbum(), root,null);
				}

				
				if(null != playController.getVideo()){
					request(playController.getVideo().getCid(), playController.vid+"");
				}
			}
			break;
		case PlayAlbumControllerCallBack.STATE_NET_NULL:
			if (root != null) {
				root.error(R.string.get_data_error);
			}
			break;
		case PlayAlbumControllerCallBack.STATE_NET_ERR:
			if (root != null) {
				root.error(R.string.get_data_error);
			}
			break;
		case PlayAlbumControllerCallBack.STATE_DATA_NULL:
			if (root != null) {
				root.error(R.string.get_data_error);
			}
			break;
		}
	}

	@Override
	public void notify(int state) {
		handlerData();
	}
	@Override
	public void requestDetails(long cid, String vid) {
		request(cid, vid);
	}

      @Override
      public void setCurPage(int curPage) {

      }

      private RequestVideosDetailTask requestTask;

	public void request(long cid, String vid) {
		LogInfo.log("main", "cid = "+cid + "  vid ="+vid);
		if (cid == AlbumNew.Channel.TYPE_JOY || cid == AlbumNew.Channel.TYPE_PE) {//娱乐跟体育频道才去请求视频详情
			cancel();
			requestTask = new RequestVideosDetailTask(getActivity(), vid);
			requestTask.start();
		}
	}

	public void cancel() {
		if (requestTask != null) {
			requestTask.cancel();
			requestTask = null;
		}
	}

	/**
	 * 请求数据
	 * 
	 */
	private class RequestVideosDetailTask extends LetvHttpAsyncTask<AlbumNew> {
		private String vid;
		private String markId = null;

		public RequestVideosDetailTask(Context context, String vid) {
			super(context);
			this.vid = vid;
			if (root != null) {
				root.loading(false);
			}
		}

		@Override
		public AlbumNew loadLocalData() {
			try {
				LocalCacheBean bean = LetvCacheDataHandler.readDetailData(vid);
				if (bean != null) {
					AlbumNew album = null;
					AlbumNewParse albumNewParse = new AlbumNewParse();
					album = albumNewParse.initialParse(bean.getCacheData());
					markId = bean.getMarkId();
					return album;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public boolean loadLocalDataComplete(AlbumNew result) {
			if (result != null) {
				if(((BasePlayActivity) getActivity()).mPlayController.getLaunchMode()==LAUNCH_MODE_LIVE||((BasePlayActivity) getActivity()).mPlayController.getLaunchMode()==LAUNCH_MODE_LIVE_FULL){
					IntroductionBuilder.build(playController.getAlbum(), root,PlayLiveController.game);
				}else{
					IntroductionBuilder.build(playController.getAlbum(), root,null);
				}
				return true;
			}
			return false;
		}

		@Override
		public LetvDataHull<AlbumNew> doInBackground() {
			LetvDataHull<AlbumNew> dataHull;
			if (!isLocalSucceed()) {
				markId = null;
			}
			AlbumNewParse parser = new AlbumNewParse();
			int aid = (int) playController.aid;
			if(((BasePlayActivity) getActivity()).mPlayController.getLaunchMode()==LAUNCH_MODE_LIVE||((BasePlayActivity) getActivity()).mPlayController.getLaunchMode()==LAUNCH_MODE_LIVE_FULL){
				dataHull = LetvHttpApi.requestAlbumVideoInfo(0, aid+"", "album", null, parser);
			}else{
				dataHull = LetvHttpApi.requestAlbumVideoInfo(0, vid, "video", null, parser);
			}
			
			if (dataHull.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
				LetvCacheDataHandler.saveDetailData(parser.getMarkId(), dataHull.getSourceData(), vid);
			}
			return dataHull;
		}

		@Override
		public void onPostExecute(int updateId, AlbumNew result) {
			if (result != null) {
				
				if(((BasePlayActivity) getActivity()).mPlayController.getLaunchMode()==LAUNCH_MODE_LIVE||((BasePlayActivity) getActivity()).mPlayController.getLaunchMode()==LAUNCH_MODE_LIVE_FULL){
					IntroductionBuilder.build(playController.getAlbum(), root,PlayLiveController.game);
				}else{
					IntroductionBuilder.build(playController.getAlbum(), root,null);
				}
			}
			if (root != null) {
				root.finish();
			}
		}

		@Override
		public void netNull() {
			if (root != null) {
				root.error(R.string.get_data_error);
			}
		}

		@Override
		public void netErr(int updateId, String errMsg) {
			if (root != null) {
				root.error(R.string.get_data_error);
			}
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			if (root != null) {
				root.error(R.string.get_data_error);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
}
