package com.letv.watchball.ui.impl;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;

import com.letv.watchball.R;
import com.letv.watchball.adapter.DetailVideosEpisodeAdapter;
import com.letv.watchball.adapter.DetailVideosListAdapter;
import com.letv.watchball.bean.Video;
import com.letv.watchball.bean.VideoList;
import com.letv.watchball.ui.LetvBaseFragment;
import com.letv.watchball.ui.LetvFunction;
import com.letv.watchball.ui.PlayAlbumController;
import com.letv.watchball.ui.PlayAlbumController.PlayAlbumControllerCallBack;
import com.letv.watchball.ui.PlayController;
import com.letv.watchball.ui.PlayLiveController;
import com.letv.watchball.utils.UIs;
import com.letv.watchball.view.PublicLoadLayout;

public class HalfPlayVideosFragment extends LetvBaseFragment implements PlayAlbumControllerCallBack,OnItemClickListener{

	private PlayController playController;

	private PublicLoadLayout root;

	/**
	 * 翻页切换标签布局
	 * */
	private View episodeScroll;

	/**
	 * 翻页切换标签
	 * */
	private GridView episodeGallery;

	/**
	 * 剧集列表 宫格形势
	 * */
	private GridView videosGridView;

	/**
	 * 剧集列表 列表形势
	 * */
	private ListView videosListView;

	/**
	 * 宫格适配器
	 * */
//	private DetailVideosGridAdapter gridAdapter;

	/**
	 * 列表适配器
	 * */
	private DetailVideosListAdapter listAdapter;

	/**
	 * 翻页适配器
	 * */
	private DetailVideosEpisodeAdapter episodeAdapter;
      private int curPage = 0;
      private View videoLoadingLayout;

      @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = UIs.createPage(getActivity(), R.layout.detailplay_half_videos);
		findView();
		return root;
	}

	private void findView() {
		episodeScroll = root.findViewById(R.id.detailplay_half_video_anthology_scroll);
		episodeGallery = (GridView) root.findViewById(R.id.detailplay_half_video_anthology_horigallery);
		videosGridView = (GridView) root.findViewById(R.id.detailplay_half_video_anthology_gridview);
		videosListView = (ListView) root.findViewById(R.id.detailplay_half_video_anthology_listview);
            videoLoadingLayout = root.findViewById(R.id.videos_loading_layout);
		int h = UIs.getScreenHeight();
		int w = UIs.getScreenWidth();

		episodeScroll.getLayoutParams().width = Math.min(w, h);
		videosGridView.getLayoutParams().width = Math.min(w, h);
		videosListView.getLayoutParams().width = Math.min(w, h);
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		playController = ((BasePlayActivity) getActivity()).mPlayController;
		if (playController != null) {
			playController.videosCallBack = this;
		}
		// episodeGallery.setViewPager(playAlbumController.getViewPager());
		Log.d("lhz", "HalfPlayVideosFragment.onActivityCreated()");
		 handlerData();
		// registerReceiver();
	}

	/**
	 * 销毁相关成员变量
	 * */
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		// unregisterReceiver();
		playController.videosCallBack = null;
		playController = null;
		episodeAdapter = null;
		listAdapter = null;
		episodeGallery = null;
		videosGridView = null;
		videosListView = null;
	}

	/**
	 * 创建视频列表
	 * */
	private void createVideos() {
		if (playController.videos != null && playController.videos.get(playController.curPage) != null) {// 判断要显示的页面数据是否存在
			root.finish();
			createEpisode();
			if (playController.isList) {
				createVideosList();
			} else {
				createVideosGrid();
			}
		}
	};

	/**
	 * 创建选集列表
	 * */
	private void createEpisode() {
		int curPage = playController.curPage;
		int totle = playController.totle;
		int pageSize = playController.pageSize;

        Log.d("newsPage", "total = " + totle);
        Log.d("newsPage", "pagesize = " + pageSize);

		if (totle > pageSize) {

			int cl = LetvFunction.calculateRows(totle, pageSize);
			episodeGallery.getLayoutParams().width = UIs.zoomWidth(70 * cl);
			episodeGallery.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
			episodeGallery.setNumColumns(cl);

			if (episodeAdapter == null) {
				episodeAdapter = new DetailVideosEpisodeAdapter(getActivity());
				
				episodeGallery.setAdapter(episodeAdapter);
				episodeScroll.setVisibility(View.GONE);
				episodeGallery.setOnItemClickListener(episodeClickListener);
			
				episodeAdapter.setTotle(totle);
				episodeAdapter.setCurPage(curPage);
				episodeAdapter.setPageSize(pageSize);
				episodeAdapter.notifyDataSetChanged();
		
			}
			
		
		}
	}

	/**
	 * 创建列表
	 * */
	private void createVideosList() {
		int curPage = playController.curPage;
		long curId = playController.vid;

		VideoList list = playController.videos.get(curPage);

		if (list != null) {
			if (listAdapter == null) {
				listAdapter = new DetailVideosListAdapter(getActivity());
				videosListView.setAdapter(listAdapter);
				videosListView.setVisibility(View.VISIBLE);
				videosGridView.setVisibility(View.GONE);
			}
			videosListView.setOnItemClickListener(listItemClickListenerForPlay);
			listAdapter.setList(list);
			listAdapter.setCurId(curId);
			listAdapter.notifyDataSetChanged();
			if (list.contains(playController.getVideo())) {
				videosListView.setSelection(list.indexOf(playController.getVideo()));
			}
		}
	};

	/**
	 * 创建宫格
	 * */
	private void createVideosGrid() {
		int curPage = playController.curPage;

		VideoList list = playController.videos.get(curPage);

		if (list != null) {
				videosGridView.setOnItemClickListener(gridItemClickListenerForPlay);
			
				videosGridView.setSelection(list.indexOf(playController.getVideo()));
		}
	};

	/**
	 * 选集点击
	 * */
	private OnItemClickListener episodeClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                  videoLoadingLayout.setVisibility(View.VISIBLE);
                  videosListView.setVisibility(View.GONE);
                  if (playController.getVideoList(position + 1)) {
                        createVideos();
                  }
                  int curPage = playController.curPage;
                  int totle = playController.totle;
                  int pageSize = playController.pageSize;
                  episodeAdapter.setTotle(totle);
                  episodeAdapter.setCurPage(position + 1);
                  episodeAdapter.setPageSize(pageSize);
                  episodeAdapter.notifyDataSetChanged();

                  mHandler.sendEmptyMessageDelayed(0,400);

            }
	};

      private Handler mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                  videoLoadingLayout.setVisibility(View.GONE);
                  videosListView.setVisibility(View.VISIBLE);
            }
      };

	/**
	 * 播放
	 * */
	private OnItemClickListener listItemClickListenerForPlay = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Video video = playController.videos.get(playController.curPage).get(position);
			int curpage=playController.curPage;
			playController.content=null;
			if(playController instanceof PlayLiveController){
				BasePlayActivity.launch(getActivity(), playController.aid, video.getId(),curpage);
				return;
			}
			if(playController instanceof PlayAlbumController){
                        ((PlayAlbumController)playController).isPlayedAd = false;
                        ((PlayAlbumController)playController).play(video);
			}else {
				BasePlayActivity.launch(getActivity(), playController.aid, playController.vid);
			}
		
		}
	};

	/**
	 * 播放
	 * */
	private OnItemClickListener gridItemClickListenerForPlay = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Video video = playController.videos.get(playController.curPage).get(position);
			if(playController instanceof PlayAlbumController){
				((PlayAlbumController)playController).play(video);
			}else {
				BasePlayActivity.launch(getActivity(), playController.aid, playController.vid);
			}
		}
	};


	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
            episodeGallery.setSelection(curPage);
	}

	/**
	 * 集合状态和数据载入UI
	 * */
	private void handlerData() {
		switch (playController.videosCallBackState) {
		case PlayAlbumControllerCallBack.STATE_RUNNING:
			if (root != null) {
				root.loading(true);
			}
			break;
		case PlayAlbumControllerCallBack.STATE_FINISH:
			if (root != null) {
				createVideos();
			}
			break;
		case PlayAlbumControllerCallBack.STATE_NET_NULL:
			if (root != null && playController.videos == null || playController.videos.size() == 0) {
				root.error(R.string.get_data_error);
			}
			break;
		case PlayAlbumControllerCallBack.STATE_NET_ERR:
			if (root != null && playController.videos == null || playController.videos.size() == 0) {
				root.error(R.string.get_data_error);
			}
			break;
		case PlayAlbumControllerCallBack.STATE_DATA_NULL:
			if (root != null && playController.videos == null || playController.videos.size() == 0) {
				root.error(R.string.get_data_error);
			}
		case PlayAlbumControllerCallBack.STATE_OTHER:
			if (root != null) {
				if (playController.isList) {
					createVideosList();
				} else {
					createVideosGrid();
				}
			}
			break;
		}
	}

	@Override
	public void notify(int state) {
		createEpisode();
		handlerData();
	
	}

	@Override
	public void requestDetails(long cid, String vid) {
		
	}

      @Override
      public void setCurPage(int curPage) {
    	  if(episodeAdapter!=null){
            episodeAdapter.setCurPage(curPage);
    	  }
      }

      @Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
}
