package com.letv.watchball.fragment;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.letv.http.bean.LetvDataHull;
import com.letv.http.parse.LetvGsonParser;
import com.letv.watchball.LetvApplication;
import com.letv.watchball.R;
import com.letv.watchball.adapter.ListVideosAdapter;
import com.letv.watchball.adapter.VideoNewsFocusAdapter;
import com.letv.watchball.async.LetvBaseTaskImpl;
import com.letv.watchball.async.LetvHttpAsyncTask;
import com.letv.watchball.bean.FocusPicInfo;
import com.letv.watchball.bean.FocusPicInfo.FocusPic;
import com.letv.watchball.bean.ListVideo;
import com.letv.watchball.bean.ListVideo.Body.Data;
import com.letv.watchball.bean.LocalCacheBean;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.ui.impl.BasePlayActivity;
import com.letv.watchball.utils.LetvCacheDataHandler;
import com.letv.watchball.utils.LetvConstant;
import com.letv.watchball.utils.LetvUtil;
import com.letv.watchball.view.LetvGalleryFocusView;
import com.letv.watchball.view.ListFootView;

/**
 * ListFragment 用于 视频新闻 和赛事列表 的数据展示,设置footer，底部自动加载更多
 * 
 * @author Liuheyuan
 * 
 */
public class VideoListFragment extends ListFragment {
	private ListView mListView;
	private LetvGalleryFocusView mGalleryFocusView;

	private ListVideosAdapter adapter;
	private ImageView refresh;
	private int startPos;
	/**
	 * 页码
	 */
	public int pageNum = 1;
	public int type = 0;
	/**
	 * 一次加载的条数
	 * */
	private int pageSize = 30;

	private String itemId = "0";
	private String currentOrderBy = LetvConstant.VideoNewsOrderBy.DATE; // 默认最新

	private ListFootView footerView;
	/**
	 * 是否有焦点图
	 */
	public boolean showFocusPic = false;
	/**
	 * 是否焦点图已经加载
	 */
	private boolean hasFocusPicLoaded = false;

	private boolean update = false;
	private boolean vrsLock = true;

	public VideoListFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// 初始化ListView
		mListView = getListView();
		mListView.setDivider(new ColorDrawable(getResources().getColor(
				R.color.letv_list_divider)));
		mListView.setDividerHeight(1);
		mListView.setCacheColorHint(0);
		mListView.setFadingEdgeLength(0);
		// 创建Adapter
		adapter = new ListVideosAdapter(getActivity());
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == SCROLL_STATE_IDLE) {
					if (refresh != null) {
						refresh.setAlpha(255);
					}
				} else {
					if (refresh != null) {
						refresh.setAlpha(100);
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				Log.d("lhz", "firstVisibleItem:" + firstVisibleItem
						+ " ,visibleItemCount:" + visibleItemCount
						+ ",totalItemCount:" + totalItemCount);
				Log.d("lhz", "startPos:" + startPos);
				startPos = firstVisibleItem;
				type = 0;// 其他页面刷新初始化
				if (firstVisibleItem + visibleItemCount >= totalItemCount) {

					if (null != footerView) {
						footerView.showLoading();
					}

					if (vrsLock) {
						requsetData(false);
						vrsLock = false;
					}

				}
			}
		});

		// 创建footerView
		footerView = new ListFootView(getActivity());
		footerView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				requsetData(true);
			}
		});
		mListView.addFooterView(footerView);
		footerView.hide();
		if (null == mGalleryFocusView) {
			mGalleryFocusView = new LetvGalleryFocusView(getActivity());

		}
		getListView().addHeaderView(mGalleryFocusView);

		setListAdapter(adapter);

	}

	@Override
	public void onPause() {
		super.onPause();
		if (mGalleryFocusView != null) {
			mGalleryFocusView.stopRemove();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mGalleryFocusView != null) {
			mGalleryFocusView.startMove();
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if (null == getListView()) {
			return;
		}
		position -= getListView().getHeaderViewsCount();
		if (position < 0) {
			return;
		}
		Data data = adapter.getVrsVieoList().body.data[position];
		if (!data.aid.equals("") && !data.vid.equals("")) {
			BasePlayActivity.launch(getActivity(), Long.parseLong(data.aid),
					Long.parseLong(data.vid));
		} else { // 做个容错
			BasePlayActivity.launch(getActivity(), 0, 0);
		}
	}

	@Override
	public void setListAdapter(ListAdapter adapter) {
		super.setListAdapter(adapter);
	}

	/**
	 * 设置赛事ID
	 * 
	 * @param itemId
	 */
	public void setItemId(String itemId) {
		if (itemId.equals(this.itemId) && null != adapter.getVrsVieoList()
				&& null != adapter.getVrsVieoList().body
				&& null != adapter.getVrsVieoList().body.data
				&& adapter.getVrsVieoList().body.data.length > 0) {
			return;
		}
		this.itemId = itemId;
		// if (mListView.getFooterViewsCount() > 0) {
		// mListView.removeFooterView(footerView);
		// }
		// mListView.addFooterView(footerView);
		// setListAdapter(adapter);
		// 赛事切换时，先清空之前数据
		adapter.setVrsVieoList(new ListVideo());

		requsetData(true);
		refrashListView();
	}

	/**
	 * 设置当前过滤类型
	 * 
	 * @param currentOrderBy
	 */
	public void setCurrentOrderBy(String currentOrderBy) {
		if (currentOrderBy.equals(this.currentOrderBy)
				&& null != adapter.getVrsVieoList()
				&& null != adapter.getVrsVieoList().body
				&& null != adapter.getVrsVieoList().body.data
				&& adapter.getVrsVieoList().body.data.length > 0) {
			return;
		}
		this.currentOrderBy = currentOrderBy;
		// if (mListView.getFooterViewsCount() > 0) {
		// mListView.removeFooterView(footerView);
		// }
		// mListView.addFooterView(footerView);
		// setListAdapter(adapter);
		// 赛事切换时，先清空之前数据
		adapter.setVrsVieoList(new ListVideo());
		refrashListView();
		requsetData(true);
	}

	/**
	 * 设置赛事id，排序类型，请求数据，在赛程里面调用
	 */
	public void setCurrentOrderBy_itemId_requestData(String itemId,
			String currentOrderBy) {
		if (itemId.equals(this.itemId)
				&& currentOrderBy.equals(this.currentOrderBy)
				&& null != adapter.getVrsVieoList()
				&& null != adapter.getVrsVieoList().body
				&& null != adapter.getVrsVieoList().body.data
				&& adapter.getVrsVieoList().body.data.length > 0) {
			return;
		}
		this.itemId = itemId;
		this.currentOrderBy = currentOrderBy;
		// 赛事切换时，先清空之前数据
		adapter.setVrsVieoList(new ListVideo());
		LetvApplication.getInstance().setShowVideoList(true);
		requsetData(true);
		refrashListView();

	}

	private RequestVrsVideosList requestVrsVideosListThread;

	public void requsetData(boolean isNew) {

		if (isNew && showFocusPic && !hasFocusPicLoaded) {
			new RequestFocusPic(getActivity()).start();
			LetvApplication.getInstance().setShowVideoList(true);
		}
		// if(isNew&LetvApplication.getInstance().isShowVideoList()){
		if (null != requestVrsVideosListThread) {
			requestVrsVideosListThread.cancel(true);
		}
		requestVrsVideosListThread = new RequestVrsVideosList(getActivity(),
				isNew);
		requestVrsVideosListThread.start();
		requestVrsVideosListThread.cancelDialog();
		LetvApplication.getInstance().setShowVideoList(false);
		// }
	}

	public void setImageView(ImageView refresh) {
		this.refresh = refresh;

		// notityListImage();
	}

	private void refrashListView() {
		adapter.notifyDataSetChanged();

		// notityListImage();
	}

	/**
	 * 请求VrsVideos数据
	 * 
	 * @author Liuheyuan
	 * 
	 */
	private class RequestVrsVideosList extends LetvHttpAsyncTask<ListVideo> {
		private boolean isNew = false;

		public RequestVrsVideosList(Context context, boolean isNew) {
			super(context, isNew);
			this.isNew = isNew;

			if (isNew) {
				pageNum = 1;
				// startPos = 0;
				// hopePos = 0;
			}
		}

		@Override
		public ListVideo loadLocalData() {
			if (isNew) {
				try {
					LocalCacheBean bean = LetvCacheDataHandler
							.readHomeNewsData(currentOrderBy, itemId);
					ListVideo result = new LetvGsonParser<ListVideo>(0,
							ListVideo.class).initialParse(bean.getCacheData());
					return result;
				} catch (Exception e) {

				}
			}
			return null;
		}

		@Override
		public boolean loadLocalDataComplete(ListVideo t) {
			if (null != t) {
				onPostExecute(0, t);
				return true;
			}
			return false;
		}

		@Override
		public LetvDataHull<ListVideo> doInBackground() {
			LetvDataHull<ListVideo> dataHull = null;

			if (isCancel) {
				return null;
			}

			dataHull = LetvHttpApi.requestListVideo(0, itemId, pageNum + "",
					pageSize + "", currentOrderBy,
					new LetvGsonParser<ListVideo>(0, ListVideo.class));
			// dataHull = LetvHttpApi.requestVrsVideos(itemId, currentOrderBy,
			// startPos, num, new LetvGsonParser<ListVideo>(0,
			// ListVideo.class));
			if (dataHull.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
				LetvCacheDataHandler.saveHomeNewsData(dataHull.getSourceData(),
						currentOrderBy, itemId);
			}
			return dataHull;
		}

		@Override
		public void onPostExecute(int updateId, ListVideo result) {
			synchronized (adapter.getVrsVieoList()) {
				if (isNew || null == adapter.getVrsVieoList()) {
					adapter.setVrsVieoList(result);
				} else {
					adapter.getVrsVieoList().body.data = (Data[]) LetvUtil
							.addAllArrays(adapter.getVrsVieoList().body.data,
									result.body.data);
					adapter.getVrsVieoList().body.total = result.body.total;
				}
				if (!isNew) {
					pageNum++;
				} else {
					if (type == 0) {
						pageNum = 2;
					}
				}
				Log.i("oyys", "type==" + type);

				// startPos += pageSize;
				if (null != footerView) {
					footerView.hide();
				}
				// if (null != footerView) {
				// footerView.showLoading();
				// }

				// 通知界面刷新数据
				refrashListView();
				this.cancelDialog();

				vrsLock = true;
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

	/**
	 * 请求VrsVideos数据
	 * 
	 * @author Liuheyuan
	 * 
	 */
	private class RequestFocusPic extends LetvHttpAsyncTask<FocusPicInfo> {
		private boolean isNew = false;

		public RequestFocusPic(Context context) {
			super(context);
		}

		@Override
		public FocusPicInfo loadLocalData() {
			if (isNew) {
				try {
					LocalCacheBean bean = LetvCacheDataHandler
							.readFocusPicInfo();
					FocusPicInfo result = new LetvGsonParser<FocusPicInfo>(0,
							FocusPicInfo.class).initialParse(bean
							.getCacheData());
					return result;
				} catch (Exception e) {
				}
			}
			return null;
		}

		@Override
		public boolean loadLocalDataComplete(FocusPicInfo t) {
			if (null != t) {
				onPostExecute(0, t);
				return true;
			}
			return false;
		}

		@Override
		public LetvDataHull<FocusPicInfo> doInBackground() {
			LetvDataHull<FocusPicInfo> dataHull = null;

			dataHull = LetvHttpApi.requestFocusPic(0,
					new LetvGsonParser<FocusPicInfo>(0, FocusPicInfo.class));
			// dataHull = LetvHttpApi.requestVrsVideos(itemId, currentOrderBy,
			// startPos, num, new LetvGsonParser<ListVideo>(0,
			// ListVideo.class));
			if (dataHull.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
				LetvCacheDataHandler.saveFocusPicInfo(dataHull.getSourceData());
			}
			return dataHull;
		}

		@Override
		public void onPostExecute(int updateId, FocusPicInfo result) {
			if (!isLocalSucceed()) {
				hasFocusPicLoaded = true;
			}
			if (null != result && null != result.body
					&& null != result.body.focuspic
					&& result.body.focuspic.length > 0) {

				VideoNewsFocusAdapter adapter = new VideoNewsFocusAdapter(
						getActivity(), null);
				ArrayList<FocusPic> list = new ArrayList<FocusPic>();

				for (FocusPic focuspic : result.body.focuspic) {
					if (focuspic.getPid() != -1 | focuspic.getVid() != -1) {
						list.add(focuspic);
					}
				}
				// list.addAll(Arrays.asList(result.body.focuspic));
				adapter.setList(list);
				mGalleryFocusView.setFocusInitData(list, adapter);
				adapter.notifyDataSetChanged();
				this.cancelDialog();
			}
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			super.dataNull(updateId, errMsg);
		}

		@Override
		public void netErr(int updateId, String errMsg) {
			super.netErr(updateId, errMsg);
		}

		@Override
		public void netNull() {
			super.netNull();
		}

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		/**
		 * frament 销毁时，移除此fragment
		 */
		Fragment fragment = getFragmentManager().findFragmentById(
				mListView.getId());
		FragmentTransaction ft = getActivity().getSupportFragmentManager()
				.beginTransaction();
		if (null != fragment) {
			ft.remove(fragment).commitAllowingStateLoss();
		}
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public void setbuttontype(int type) {
		this.type = type;
	}
}
