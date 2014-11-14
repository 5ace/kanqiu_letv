package com.letv.watchball.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import za.co.immedia.pinnedheaderlistview.PinnedHeaderListView;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.letv.http.bean.LetvDataHull;
import com.letv.http.parse.LetvGsonParser;
import com.letv.watchball.R;
import com.letv.watchball.adapter.LiveAdapter;
import com.letv.watchball.adapter.LiveAdapter.MODE;
import com.letv.watchball.async.LetvHttpAsyncTask;
import com.letv.watchball.bean.Game;
import com.letv.watchball.bean.LiveList;
import com.letv.watchball.bean.LocalCacheBean;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.manager.HomeFragmentLsn;
import com.letv.watchball.utils.LetvCacheDataHandler;
import com.letv.watchball.utils.LetvUtil;
import com.letv.watchball.utils.UIs;
import com.letv.watchball.view.MyProgressBar;
import com.letv.watchball.view.PullToRefreshListViewHeader;

public class GLiveInfoFragment extends Fragment {

	private LiveAdapter adapter;
	/**
	 * 下一页索引
	 */
	private int nextPageIndex = 0;
	/**
	 * 上一页索引
	 */
	private int prePageIndex = 0;
	private PullToRefreshListViewHeader mPullToRefreshListViewHeader;
	/**
	 * 自动刷新bar
	 */
	// private ProgressBar autoRefreshBar;
	private LinearLayout bad_network;
	/**
	 * 计时刷新handler
	 */
	private AutoRefreshHandler mAutoRefreshHandler;
	private HomeFragmentLsn mHomeFragmentLsn;
	private MyProgressBar refreshBar;
	private ImageView refresh;
	private ViewGroup root;
	/**
	 * 第一页的位置索引，用于即时刷新
	 */
	private int firstPageIndex = 0;
	/**
	 * 第一页group个数，用于计时刷新
	 */
	private int firstPageSize = 0;
	/**
	 * 初次加载数据时候会出现
	 */
	private int last = 0;

	public GLiveInfoFragment() {
	}

	public void setHomeFragmentLsn(HomeFragmentLsn mHomeFragmentLsn) {
		this.mHomeFragmentLsn = mHomeFragmentLsn;
		adapter.setHomeFragmentLsn(mHomeFragmentLsn);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		try {
			root = (ViewGroup) inflater.inflate(R.layout.fragment_home_live,
					null, false);
		} catch (InflateException e) {
			/* map is already there, just return view as it is */
		}

		mPullToRefreshListViewHeader = (PullToRefreshListViewHeader) root
				.findViewById(R.id.fragment_home_live);

		mPullToRefreshListViewHeader.getRefreshableView()
				.setFadingEdgeLength(0);
		mPullToRefreshListViewHeader.getRefreshableView().setDivider(null);
		// mPullToRefreshListViewHeader.getRefreshableView().setDivider(new
		// ColorDrawable(getResources().getColor(R.color.letv_color_ffd5d5d5)));

		// autoRefreshBar = (ProgressBar)
		// root.findViewById(R.id.auto_refrsh_progress);

		refreshBar = (MyProgressBar) root.findViewById(R.id.refreshBar);
		refresh = (ImageView) root.findViewById(R.id.refresh);
		refreshBar
				.setProgressBarCallBack(new MyProgressBar.ProgressBarCallBack() {

					@Override
					public void stop() {
						mAutoRefreshHandler.removeMessages(1);
					}

					@Override
					public void start() {
						mAutoRefreshHandler.sendEmptyMessage(1);
					}
				});

		mAutoRefreshHandler = new AutoRefreshHandler(refreshBar);
		bad_network = (LinearLayout) root.findViewById(R.id.bad_network);
		bad_network.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!LetvUtil.CheckNetworkState()) {
					UIs.showToast("没有网络");
					return;

				}
				requestLiveData();
				if (null != mHomeFragmentLsn) {
					mHomeFragmentLsn.reloadAllDatas();
				}
			}
		});
		refresh.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub

				switch (arg1.getAction()) {
				case MotionEvent.ACTION_DOWN:
					refresh.setAlpha(100);
					break;
				case MotionEvent.ACTION_UP:
					refresh.setAlpha(255);
					resetThread();
					if (prePageIndex == 0) {
						loadFirstPage(true, false);
					}
					UpdateToast();
					break;
				case MotionEvent.ACTION_MOVE:
					refresh.setAlpha(255);
					break;
				}
				return true;
			}
		});
		return root;
	}

	public void UpdateToast() {
		String fristdate = new SimpleDateFormat("HH:mm:ss", Locale.CHINESE)
				.format(Calendar.getInstance().getTime());
		Toast.makeText(getActivity(), "比赛数据已更新：" + fristdate,
				Toast.LENGTH_SHORT).show();

	}

	private void resetThread() {
		// if(null != thread && thread.isAlive()){
		// thread.interrupt();
		// thread = null;
		// // mAutoRefreshHandler.sendEmptyMessage(1);
		// }
		refreshBar.resetProgressBar();
	}

	/**
	 * 计时刷新handler
	 * 
	 * @author liuhanzhi
	 * 
	 */
	private class AutoRefreshHandler extends Handler {

		private MyProgressBar progressbar;

		public AutoRefreshHandler(MyProgressBar progressbar) {
			this.progressbar = progressbar;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				if (progressbar.getPositon() == 359 && null != root
						&& root.getVisibility() == View.VISIBLE) {

					UpdateToast();
					if (prePageIndex == 0) {
						loadFirstPage(true, false);
					}
					mHomeFragmentLsn.loadRightFragmentData();
				}
				progressbar.updatePosition();
				sendEmptyMessageDelayed(1, 250);
				break;

			default:
				break;
			}
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
		loadFirstPage(true, true);
	}

	private void init() {
		adapter = new LiveAdapter(getActivity(), false);
		adapter.setMode(MODE.HOME);

		mPullToRefreshListViewHeader.getRefreshableView().setAdapter(adapter);
		mPullToRefreshListViewHeader
				.setOnRefreshListener(new OnRefreshListener2<PinnedHeaderListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<PinnedHeaderListView> refreshView) {
						// //刷新第一页数据
						loadPrePage();
						resetThread();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<PinnedHeaderListView> refreshView) {
						// loadNextPage();
						// mAutoRefreshHandler.progress=0;//刷新时候进度条归零
						// mHomeFragmentLsn.loadRightFragmentData();
						resetThread();
					}

				});

		mPullToRefreshListViewHeader
				.setOnScrollListener(new OnScrollListener() {

					@Override
					public void onScrollStateChanged(AbsListView view,
							int scrollState) {
						if (null != mPullToRefreshListViewHeader) {
							mPullToRefreshListViewHeader.getRefreshableView()
									.onScrollStateChanged(view, scrollState);
						}

						if (scrollState == SCROLL_STATE_IDLE) {
							refresh.setAlpha(255);
						} else {
							if (refresh != null) {
								refresh.setAlpha(100);
							}
						}
						if (last == adapter.getCount()
								&& scrollState == this.SCROLL_STATE_FLING) {
							loadNextPage();
							// Toast.makeText(getActivity(), "已滑到底部.",
							// Toast.LENGTH_SHORT).show();
						}

					}

					@Override
					public void onScroll(AbsListView view,
							int firstVisibleItem, int visibleItemCount,
							int totalItemCount) {
						if (null != mPullToRefreshListViewHeader) {
							mPullToRefreshListViewHeader.getRefreshableView()
									.onScroll(view, firstVisibleItem,
											visibleItemCount, totalItemCount);
						}
						last = visibleItemCount + firstVisibleItem;

					}
				});

	}

	/**
	 * 获取/刷新 首页数据
	 * 
	 * @param dialog
	 */
	private void loadFirstPage(boolean dialog, boolean needSetSelection) {
		new RequestMatchList(getActivity(), prePageIndex, dialog,
				needSetSelection, null).start();
	}

	/**
	 * 加载上一页数据
	 */
	private void loadPrePage() {
		prePageIndex--;
		new RequestMatchList(getActivity(), prePageIndex, false, true,
				new Runnable() {

					@Override
					public void run() {
						// prePageIndex++;
					}
				}).start();

	}

	/**
	 * 加载下一页数据
	 */
	private void loadNextPage() {
		nextPageIndex++;
		new RequestMatchList(getActivity(), nextPageIndex, false, false,
				new Runnable() {

					@Override
					public void run() {
					}
				}).start();
	}

	/**
	 * 如果当前是bad network自动重新加载
	 */
	public void requestLiveData() {
		if (bad_network.getVisibility() == View.VISIBLE) {
			bad_network.setVisibility(View.GONE);
			mPullToRefreshListViewHeader.setVisibility(View.VISIBLE);
			nextPageIndex = -1;
			loadNextPage();
		}
	}

	/**
	 * http请求热门直播
	 * */
	private class RequestMatchList extends LetvHttpAsyncTask<LiveList> {

		/**
		 * page索引
		 */
		private int pageIndex;
		/**
		 * 是否要定位至当天比赛位置
		 */
		private boolean needSetSelection;
		/**
		 * 失败回调
		 */
		private Runnable errorCb;

		private int selection;

		private boolean LoacalDateSelection = true;

		public RequestMatchList(Context context, int pageIndex, boolean dialog,
				boolean needSetSelection, Runnable errorCb) {
			super(context, dialog);
			this.pageIndex = pageIndex;
			this.needSetSelection = needSetSelection;
			this.errorCb = errorCb;

		}

		@Override
		public LiveList loadLocalData() {
			try {
				// 只在首次进入时加载缓存
				if (pageIndex == 0 && needSetSelection) {
					// first page
					LocalCacheBean bean = LetvCacheDataHandler
							.readHomeLiveData();
					if (bean != null) {
						LiveList result = null;
						LetvGsonParser<LiveList> albumNewParse = new LetvGsonParser<LiveList>(
								0, LiveList.class);
						result = albumNewParse
								.initialParse(bean.getCacheData());
						String fristdate = new SimpleDateFormat("yyyy-MM-dd",
								Locale.CHINESE).format(Calendar.getInstance()
								.getTime());
						String[] dates = fristdate.split("-");
						String loacldate = dates[dates.length - 1];
						boolean isSelectionSet = false;
						// if (adapter.listParent.size() > firstPageIndex) {
						// adapter.listParent.remove(firstPageIndex);
						// adapter.listChild.remove(firstPageIndex);
						// }

						for (int i = 0; i < result.body.length; i++) {
							if (!isSelectionSet) {
							}
							ArrayList<Game> childrens = new ArrayList<Game>();
							Game[] mLiveInfos = result.body[i].live_infos;

							if (result.body[i].date.substring(3, 5).contains(
									loacldate)) {
								LoacalDateSelection = false;

							}
							if (LoacalDateSelection) {
								System.out.println("mLiveInfos.length="
										+ mLiveInfos.length);
								selection += mLiveInfos.length + 1;
							}
							for (int j = 0; j < mLiveInfos.length; j++) {
								Game game = mLiveInfos[j];

								// 设置listview起始位置
								if (LoacalDateSelection == false
										&& mLiveInfos[j].status == 2) {
									selection++;
								}
								childrens.add(game);
							}

							adapter.listParent.add(result.body[i].date);
							adapter.listChild.add(childrens);
							// adapter.listParent.add(i + firstPageIndex,
							// result.body[i].date);
							// adapter.listChild.add(i + firstPageIndex,
							// childrens);
							if (!isSelectionSet) {
							}
						}
						firstPageSize = result.body.length;
						// if (needSetSelection) {
						// mPullToRefreshListViewHeader.getRefreshableView().setSelection(selection);
						// }

						return result;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public boolean loadLocalDataComplete(LiveList result) {
			if (null == result || result.body.length == 0) {
				return false;
			}
			if (mPullToRefreshListViewHeader != null) {
				mPullToRefreshListViewHeader.onRefreshComplete();

				adapter.notifyDataSetChanged();
				if (needSetSelection && selection >= 0) {
					mPullToRefreshListViewHeader.getRefreshableView()
							.setSelection(selection);
				}
			}
			resetThread();
			return true;
		}

		@Override
		public LetvDataHull<LiveList> doInBackground() {
			LetvDataHull<LiveList> dataHull = LetvHttpApi.requestLiveinfos(
					pageIndex + "", new LetvGsonParser<LiveList>(0,
							LiveList.class));
			LiveList result = dataHull.getDataEntity();
			if (null == result || result.body.length == 0) {
				return dataHull;
			}
			String fristdate = new SimpleDateFormat("yyyy-MM-dd",
					Locale.CHINESE).format(Calendar.getInstance().getTime());
			String[] dates = fristdate.split("-");
			String loacldate = dates[dates.length - 1];
			if (pageIndex == 0) {
				// first page
				selection = 0;
				LoacalDateSelection = true;
				if (adapter.listParent.size() > firstPageIndex
						&& firstPageSize > 0) {
					for (int i = 0; i < firstPageSize; i++) {
						adapter.listParent.remove(firstPageIndex);
						adapter.listChild.remove(firstPageIndex);
					}
				}
				for (int i = 0; i < result.body.length; i++) {
					ArrayList<Game> childrens = new ArrayList<Game>();

					Game[] mLiveInfos = result.body[i].live_infos;
					if (result.body[i].date.substring(3, 5).contains(loacldate)) {
						LoacalDateSelection = false;

					}
					if (LoacalDateSelection) {
						System.out.println("mLiveInfos.length="
								+ mLiveInfos.length);
						selection += mLiveInfos.length + 1;
					}

					for (int j = 0; j < mLiveInfos.length; j++) {
						Game game = mLiveInfos[j];
						// 设置listview起始位置
						if (LoacalDateSelection == false
								&& mLiveInfos[j].status == 2) {
							selection++;
						}

						childrens.add(game);
					}

					adapter.listParent.add(i + firstPageIndex,
							result.body[i].date);
					adapter.listChild.add(i + firstPageIndex, childrens);
				}
				firstPageSize = result.body.length;
				// if (needSetSelection) {
				// mPullToRefreshListViewHeader.getRefreshableView().setSelection(selection);
				// }
			} else if (pageIndex > 0) {
				// next page
				for (int i = 0; i < result.body.length; i++) {
					ArrayList<Game> childrens = new ArrayList<Game>();
					Game[] mLiveInfos = result.body[i].live_infos;
					for (int j = 0; j < mLiveInfos.length; j++) {
						Game game = mLiveInfos[j];
						childrens.add(game);
					}
					adapter.listParent.add(result.body[i].date);
					adapter.listChild.add(childrens);
				}
			} else if (pageIndex < 0) {
				// previous page
				selection = 0;// 上拉时候坐标初始化
				LoacalDateSelection = true;// 上拉时候初始化
				int total = 0;
				firstPageIndex += result.body.length;
				for (int i = result.body.length - 1; i >= 0; i--) {
					if (i == 0) {// 最后一条数据时候，上面的for循环汉之写的。
					// LoacalDateSelection=false;
						selection = selection - 1;
					}

					ArrayList<Game> childrens = new ArrayList<Game>();
					Game[] mLiveInfos = result.body[i].live_infos;

					// if(LoacalDateSelection){//最后一条就不加坐标了。直接定位
					selection += mLiveInfos.length + 1;
					// }
					for (int j = 0; j < mLiveInfos.length; j++) {
						Game game = mLiveInfos[j];
						childrens.add(game);
					}
					total++;
					total += mLiveInfos.length;
					adapter.listParent.add(0, result.body[i].date);
					adapter.listChild.add(0, childrens);
				}
			}
			if (dataHull.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
				LetvCacheDataHandler.saveHomeLiveData(dataHull.getSourceData());
			}
			return dataHull;
		}

		@Override
		public void onPostExecute(int updateId, LiveList result) {
			mPullToRefreshListViewHeader.onRefreshComplete();
			if (null == result || result.body.length == 0) {
				if (null != errorCb)
					errorCb.run();
				Toast.makeText(getActivity(), "没有更多数据啦~", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			adapter.notifyDataSetChanged();
			if (needSetSelection && selection >= 0) {
				System.out.println("selection====" + (selection));
				mPullToRefreshListViewHeader.getRefreshableView().setSelection(
						selection);
			}

			resetThread();

		}

		@Override
		public void netNull() {
			if (null != errorCb) {
				errorCb.run();
			}
			mPullToRefreshListViewHeader.onRefreshComplete();
			if (!isLocalSucceed()) {
				bad_network.setVisibility(View.VISIBLE);
				mPullToRefreshListViewHeader.setVisibility(View.GONE);
			}
		}

		@Override
		public void netErr(int updateId, String errMsg) {
			if (null != errorCb) {
				errorCb.run();
			}
			if (mPullToRefreshListViewHeader != null) {
				mPullToRefreshListViewHeader.onRefreshComplete();
			}
			if (!isLocalSucceed()) {
				// bad_network.setVisibility(View.VISIBLE);
				// mPullToRefreshListViewHeader.setVisibility(View.GONE);
			}
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			if (null != errorCb) {
				errorCb.run();
			}
			mPullToRefreshListViewHeader.onRefreshComplete();
		}
	}

	/*
	 * private class HomePageNotyfy{ private static final int FIRET_PAGE = 0;
	 * private static final int PRE_PAGE = 1; private static final int NEXT_PAGE
	 * = 2; private static final int ERROR = 3; public void notify(int
	 * code,LiveList result) { switch (code) { case FIRET_PAGE:
	 * 
	 * break; case PRE_PAGE:
	 * 
	 * break; case NEXT_PAGE:
	 * 
	 * break; case ERROR:
	 * 
	 * break; default: break; } };
	 * 
	 * };
	 */

	public void updateSuscribeStatus() {
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (mAutoRefreshHandler != null)
			mAutoRefreshHandler.removeMessages(1);
		mAutoRefreshHandler = null;
		if (root != null) {
			ViewGroup parent = (ViewGroup) root.getParent();
			if (parent != null)
				parent.removeView(root);
		}
		/**
		 * frament 销毁时，移除此fragment
		 * 
		 */
		FragmentTransaction ft = getChildFragmentManager().beginTransaction();
		if (!ft.isEmpty()) {
			ft.remove(this).commitAllowingStateLoss();
		}

		adapter = null;
		mPullToRefreshListViewHeader = null;
		bad_network = null;
		refresh = null;
		mHomeFragmentLsn = null;
		root = null;
	}

}
