package com.letv.watchball.fragment;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.letv.http.bean.LetvDataHull;
import com.letv.http.parse.LetvGsonParser;
import com.letv.watchball.R;
import com.letv.watchball.adapter.OriginalVideoAdapter;
import com.letv.watchball.async.LetvHttpAsyncTask;
import com.letv.watchball.bean.MatchList.Body.OriginalColumn;
import com.letv.watchball.bean.OriginalVideo;
import com.letv.watchball.bean.OriginalVideo.Body.Video;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.ui.impl.BasePlayActivity;
import com.letv.watchball.utils.LetvUtil;
import com.letv.watchball.view.ListFootView;

/**
 * 原创节目视频列表展示控件
 * 
 * @author Liuheyuan
 * 
 */
public class OriginalVideoListFragment extends ListFragment {
	private ListView mListView;

	private OriginalVideoAdapter adapter;

	/**
	 * 当前ListView的起始项
	 * */
	private int _start, _end;

	/**
	 * 加载的起始位置
	 * */
	private int startPos = 0;

	private int hopePos = 0;

	/**
	 * 一次加载的条数
	 * */
	private int num = 30;

	private ListFootView footerView;
	private TextView emptyView;

	// private OriginalVideo originalVideos = new OriginalVideo();

	private OriginalColumn originalColumn;

	public OriginalVideoListFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// 初始化ListView
		mListView = getListView();
		mListView.setDivider(new ColorDrawable(getResources().getColor(R.color.letv_list_divider)));
		mListView.setDividerHeight(1);
		mListView.setCacheColorHint(0);
		mListView.setFadingEdgeLength(0);
//		mListView.setSelector(android.R.color.transparent);
		adapter = new OriginalVideoAdapter(getActivity());
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == SCROLL_STATE_IDLE) {
					if (_end < adapter.getOriginalVideos().body.videos.length) {
						notityListImage();
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				_start = firstVisibleItem;
				_end = firstVisibleItem + visibleItemCount;
				if (firstVisibleItem == 0) {
					notityListImage();
				}

				if (firstVisibleItem + visibleItemCount >= adapter.getOriginalVideos().body.total && 0 != adapter.getOriginalVideos().body.total) {
					notityListImage();
					if (mListView.getFooterViewsCount() > 0) {
						mListView.removeFooterView(footerView);
					}
					return;
				}
//				System.err.println("startPos:"+startPos+",hopePos:"+hopePos);
				if (firstVisibleItem + visibleItemCount >= totalItemCount && startPos > hopePos) {
					// LHY hopePos 和 startPos 相差很大
					hopePos += num;
					requsetData(false);
				}
			}
		});

		// 创建footerView
		footerView = new ListFootView(getActivity());
		footerView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				requsetData(false);
			}
		});
		mListView.addFooterView(footerView);

		// 创建emptyView
		emptyView = new TextView(getActivity());
		emptyView.setText("emptyView");
		mListView.setEmptyView(emptyView);

		setListAdapter(adapter);

	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		long pid;
		Video data = adapter.getOriginalVideos().body.videos[position];
//		LetvPlayFunction.playVideo(getActivity(), Integer.parseInt(data.vid), null, null, false, 0);
		if(data.pid!=null){
			pid=Long.parseLong(data.pid);
		}else{
			 pid=0;
		}
		BasePlayActivity.launch(getActivity(), pid,Long.parseLong(data.vid));
	}

	@Override
	public void setListAdapter(ListAdapter adapter) {
		super.setListAdapter(adapter);
	}

	public void setOriginalColumn(OriginalColumn originalColumn) {
		if (null != this.originalColumn && originalColumn.id == this.originalColumn.id) {
			return;
		}
		this.originalColumn = originalColumn;
		if (mListView.getFooterViewsCount() > 0) {
			mListView.removeFooterView(footerView);
		}
		mListView.addFooterView(footerView);
		setListAdapter(adapter);
		requsetData(true);

	}

	private void notityListImage() {
		if (adapter != null) {
			adapter.notifyImageView(_start, _end);
		}
	}

	private RequestVrsVideosList requestVrsVideosListThread;

	public void requsetData(boolean isNew) {
		if (null != requestVrsVideosListThread) {
			requestVrsVideosListThread.cancel(true);
		}
		requestVrsVideosListThread = new RequestVrsVideosList(getActivity(), isNew);
		requestVrsVideosListThread.start();
	}

	private void refrashListView() {
		adapter.notifyDataSetChanged();
		notityListImage();
	}

	/**
	 * 请求VrsVideos数据
	 * 
	 * @author Liuheyuan
	 * 
	 */
	private class RequestVrsVideosList extends LetvHttpAsyncTask<OriginalVideo> {
		private boolean isNew = false;

		public RequestVrsVideosList(Context context, boolean isNew) {
			super(context, isNew);
			this.isNew = isNew;

			if (isNew) {
				startPos = 0;
				hopePos = 0;
			}
		}

		@Override
		public LetvDataHull<OriginalVideo> doInBackground() {
			LetvDataHull<OriginalVideo> dataHull = null;

			// dataHull = LetvHttpApi.requestVrsVideos(itemId, currentOrderBy,
			// startPos, num, new LetvGsonParser<OriginalVideo>(0,
			// OriginalVideo.class));

			dataHull = LetvHttpApi.requestOriginalVideo(String.valueOf(originalColumn.id), startPos, num, new LetvGsonParser<OriginalVideo>(0,
					OriginalVideo.class));
//			if (null != footerView) {
//				footerView.showLoading();
//			}
			return dataHull;
		}

		@Override
		public void onPostExecute(int updateId, OriginalVideo result) {
			synchronized (adapter.getOriginalVideos()) {
				if (isNew || null == adapter.getOriginalVideos()) {
					adapter.setOriginalVideos(result);
				} else {
					adapter.getOriginalVideos().body.videos = (Video[]) LetvUtil.addAllArrays(adapter.getOriginalVideos().body.videos, result.body.videos);
					adapter.getOriginalVideos().body.total = result.body.total;
				}
				startPos += num;

				if (null != footerView) {
					footerView.hide();
				}

				// 通知界面刷新数据
				refrashListView();
			}
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			super.dataNull(updateId, errMsg);
			if (null != footerView) {
				footerView.showRefresh();
			}
		}

		@Override
		public void netErr(int updateId, String errMsg) {
			super.netErr(updateId, errMsg);
			if (null != footerView) {
				footerView.showRefresh();
			}
		}

		@Override
		public void netNull() {
			super.netNull();
			if (null != footerView) {
				footerView.showRefresh();
			}
		}

	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		/**
		 * frament 销毁时，移除此fragment
		 */
		Fragment fragment = getFragmentManager().findFragmentById(mListView.getId());
		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		if(null != fragment){
			ft.remove(fragment).commitAllowingStateLoss();
		}
	}

}
