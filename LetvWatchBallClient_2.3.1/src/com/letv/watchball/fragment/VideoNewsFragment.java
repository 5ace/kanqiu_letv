package com.letv.watchball.fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.http.bean.LetvDataHull;
import com.letv.http.parse.LetvGsonParser;
import com.letv.watchball.LetvApplication;
import com.letv.watchball.R;
import com.letv.watchball.async.LetvHttpAsyncTask;
import com.letv.watchball.bean.LocalCacheBean;
import com.letv.watchball.bean.VideoTypes;
import com.letv.watchball.bean.VideoTypes.VideoBean;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.manager.HomeFragmentLsn;
import com.letv.watchball.utils.LetvCacheDataHandler;
import com.letv.watchball.utils.LetvUtil;
import com.letv.watchball.utils.UIs;

public class VideoNewsFragment extends Fragment {

	VideoListFragment videoListFragment;

	/**
	 * 最新，最热button
	 */
	private LinearLayout video_news_fragment_filterRadioButton_orderBy;
	/**
	 * 球队ItemId Button
	 */
	private LinearLayout video_news_fragment_filterRadioButton_itemId;
	private TextView video_news_fragment_selector_title1,
			video_news_fragment_selector_title2;

	private ImageView video_news_fragment_selector_icon1,
			video_news_fragment_selector_icon2, refresh;
	/**
	 * 筛选ListView
	 */
	private ListView video_news_lstFilter;
	private LinearLayout video_news_lstFilter_cover;

	private FilterAdapter filterAdapter;

	/**
	 * 球队筛选类型数据
	 */
	private VideoBean[] newsTypeBean;

	/**
	 * 排序筛选类型，最新，最热
	 */
	private VideoBean[] sortTypeBean;

	/**
	 * 当前筛选数据类型
	 */
	private VideoBean[] currentTypeBean;
	/**
	 * 当前的排序类型，球队筛选类型
	 */
	private int mNewsTypeItemPos, mSortTypeItmePos = 0;
	/**
	 * video_news_lstFilter listview的高度,默认200
	 */
	private int lstFilterHeight = 200;
	private HomeFragmentLsn mHomeFragmentLsn;
	private LinearLayout bad_network;
	private RelativeLayout video_news_fragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// VideoTypes.Body body0 = new VideoTypes.Body();
		// body0.name = "最新";
		// body0.type = LetvConstant.VideoNewsOrderBy.DATE;
		// VideoTypes.Body body1 = new VideoTypes.Body();
		// body1.name = "最热";
		// body1.type = LetvConstant.VideoNewsOrderBy.PLAYCOUNT;
		// sortTypeBean.body = new Body[] { body0, body1 };
	}

	private ViewGroup rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		try {
			rootView = (ViewGroup) inflater.inflate(
					R.layout.video_news_fragment, null, false);
		} catch (InflateException e) {
			/* map is already there, just return view as it is */
		}

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		video_news_fragment = (RelativeLayout) getView().findViewById(
				R.id.video_news_fragment);
		// 初始化ListFragment
		videoListFragment = (VideoListFragment) getActivity()
				.getSupportFragmentManager().findFragmentById(
						R.id.video_news_fragment_videoList);
		videoListFragment.showFocusPic = true;
		// videoListFragment.requsetData(true);

		video_news_fragment_filterRadioButton_orderBy = (LinearLayout) getView()
				.findViewById(
						R.id.video_news_fragment_filterRadioButton_orderBy);
		video_news_fragment_filterRadioButton_itemId = (LinearLayout) getView()
				.findViewById(R.id.video_news_fragment_filterRadioButton_itemId);
		video_news_fragment_selector_title1 = (TextView) getView()
				.findViewById(R.id.news_list_selector_title1);
		video_news_fragment_selector_title2 = (TextView) getView()
				.findViewById(R.id.news_list_selector_title2);
		video_news_fragment_selector_icon1 = (ImageView) getView()
				.findViewById(R.id.news_list_selector_icon1);
		video_news_fragment_selector_icon2 = (ImageView) getView()
				.findViewById(R.id.news_list_selector_icon2);
		refresh = (ImageView) getView().findViewById(R.id.refresh);
		video_news_lstFilter = (ListView) getView().findViewById(
				R.id.video_news_lstFilter);
		video_news_lstFilter_cover = (LinearLayout) getView().findViewById(
				R.id.video_news_lstFilter_cover);
		videoListFragment.setImageView(refresh);
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

					new RequestVrsVideosType(getActivity()).start();
					videoListFragment.setbuttontype(1);
					videoListFragment.requsetData(true);
					String fristdate = new SimpleDateFormat("HH:mm:ss",
							Locale.CHINESE).format(Calendar.getInstance()
							.getTime());
					Toast.makeText(getActivity(), "比赛数据已更新：" + fristdate,
							Toast.LENGTH_SHORT).show();
					break;
				case MotionEvent.ACTION_MOVE:
					refresh.setAlpha(255);
					break;
				}
				return true;
			}
		});
		video_news_lstFilter_cover.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				video_news_lstFilter_cover.setVisibility(View.GONE);
				if (video_news_fragment_filterRadioButton_orderBy.isSelected()) {
					video_news_fragment_filterRadioButton_orderBy
							.performClick();
				} else if (video_news_fragment_filterRadioButton_itemId
						.isSelected()) {
					video_news_fragment_filterRadioButton_itemId.performClick();
				}
			}
		});
		video_news_fragment_filterRadioButton_orderBy
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						video_news_fragment_filterRadioButton_itemId
								.setSelected(false);
						if (v.isSelected()) {
							v.setSelected(false);
							video_news_lstFilter_cover.setVisibility(View.GONE);
							video_news_fragment_selector_icon1
									.setImageResource(R.drawable.news_list_selector_btn_nor);
						} else {
							v.setSelected(true);
							currentTypeBean = sortTypeBean;
							filterAdapter.onItemClicked(mSortTypeItmePos);
							filterAdapter.notifyDataSetChanged();

							LayoutParams params = video_news_lstFilter
									.getLayoutParams();
							params.height = LayoutParams.WRAP_CONTENT;
							video_news_lstFilter.setLayoutParams(params);
							video_news_lstFilter.requestLayout();
							video_news_lstFilter_cover
									.setVisibility(View.VISIBLE);
							video_news_fragment_selector_icon1
									.setImageResource(R.drawable.news_list_selector_btn_sel);
							video_news_fragment_selector_icon2
									.setImageResource(R.drawable.news_list_selector_btn_nor);
						}
					}
				});

		video_news_fragment_filterRadioButton_itemId
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						video_news_fragment_filterRadioButton_orderBy
								.setSelected(false);
						if (v.isSelected()) {
							v.setSelected(false);
							video_news_lstFilter_cover.setVisibility(View.GONE);
							video_news_fragment_selector_icon2
									.setImageResource(R.drawable.news_list_selector_btn_nor);
						} else {
							v.setSelected(true);
							currentTypeBean = newsTypeBean;
							filterAdapter.onItemClicked(mNewsTypeItemPos);
							filterAdapter.notifyDataSetChanged();

							LayoutParams params = video_news_lstFilter
									.getLayoutParams();
							params.height = lstFilterHeight;
							video_news_lstFilter.setLayoutParams(params);
							video_news_lstFilter.requestLayout();
							video_news_lstFilter_cover
									.setVisibility(View.VISIBLE);
							video_news_fragment_selector_icon2
									.setImageResource(R.drawable.news_list_selector_btn_sel);
							video_news_fragment_selector_icon1
									.setImageResource(R.drawable.news_list_selector_btn_nor);
						}
					}
				});

		video_news_lstFilter
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						video_news_fragment_filterRadioButton_orderBy
								.setSelected(false);
						video_news_fragment_filterRadioButton_itemId
								.setSelected(false);
						video_news_fragment_selector_icon1
								.setImageResource(R.drawable.news_list_selector_btn_nor);
						video_news_fragment_selector_icon2
								.setImageResource(R.drawable.news_list_selector_btn_nor);
						video_news_lstFilter_cover.setVisibility(View.GONE);
						LetvApplication.getInstance().setShowVideoList(true);
						if (currentTypeBean == sortTypeBean) {
							// 保存当前选中位置
							mSortTypeItmePos = position;
							// 排序，最新最热筛选
							videoListFragment
									.setCurrentOrderBy(currentTypeBean[position].id);
							video_news_fragment_selector_title1
									.setText(currentTypeBean[position].name);
						} else if (currentTypeBean == newsTypeBean) {
							// 保存当前选中位置
							mNewsTypeItemPos = position;
							// 赛事Item 筛选

							videoListFragment
									.setItemId(currentTypeBean[position].id);
							video_news_fragment_selector_title2
									.setText(currentTypeBean[position].name);
						}

					}
				});

		filterAdapter = new FilterAdapter();
		video_news_lstFilter.setAdapter(filterAdapter);
		video_news_lstFilter.setCacheColorHint(Color.TRANSPARENT);

		if (video_news_lstFilter.getLayoutParams().height > 0) {
			lstFilterHeight = video_news_lstFilter.getLayoutParams().height;
		}

		bad_network = (LinearLayout) getView().findViewById(R.id.bad_network);
		bad_network.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!LetvUtil.CheckNetworkState()) {
					UIs.showToast("没有网络");
					return;

				}
				requestFiltetTypeData();
				if (null != mHomeFragmentLsn) {
					mHomeFragmentLsn.reloadAllDatas();
				}
			}
		});
		// requestFiltetTypeData();
	}

	/**
	 * 执行新闻筛选的关闭操作
	 * 
	 * @return 视频新闻筛选 list是否已关闭
	 */
	public boolean closeNewsFilter() {
		boolean isClose = video_news_lstFilter_cover.getVisibility() == View.GONE;
		if (video_news_fragment_filterRadioButton_itemId.isSelected()) {
			video_news_fragment_filterRadioButton_itemId.performClick();
		}
		if (video_news_fragment_filterRadioButton_orderBy.isSelected()) {
			video_news_fragment_filterRadioButton_orderBy.performClick();
		}
		return isClose;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private boolean isFirtInit = true;

	/**
	 * 请求视频新闻筛选类型
	 */
	public void requestFiltetTypeData() {
		if (bad_network.getVisibility() == View.VISIBLE) {
			bad_network.setVisibility(View.GONE);
			video_news_fragment.setVisibility(View.VISIBLE);
			isFirtInit = true;
		}
		if (isFirtInit) {
			isFirtInit = false;
			new RequestVrsVideosType(getActivity()).start();
			videoListFragment.requsetData(true);
		}
	}

	/**
	 * @author Liuheyuan 筛选适配器
	 */
	class FilterAdapter extends BaseAdapter {

		private int mCurrentPos;

		public void onItemClicked(int pos) {
			this.mCurrentPos = pos;
		}

		@Override
		public int getCount() {
			if (null != currentTypeBean) {
				return currentTypeBean.length;
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (null != currentTypeBean) {
				return currentTypeBean.length;
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = LayoutInflater.from(getActivity()).inflate(
					R.layout.news_list_selector_type, null);
			TextView tView = (TextView) view
					.findViewById(R.id.news_list_selector_type_name);
			tView.setText(currentTypeBean[position].name);

			// //设置点击item背景
			if (mCurrentPos == position) {
				view.findViewById(R.id.news_list_selector_selection)
						.setVisibility(View.VISIBLE);
				// view.setBackgroundResource(R.drawable.news_list_selector_item_bg);
			} else {
				view.findViewById(R.id.news_list_selector_selection)
						.setVisibility(View.INVISIBLE);
				// view.setBackgroundResource(0);
			}
			return view;
		}

	}

	/**
	 * @author Liuheyuan 请求视频新闻类型
	 */
	private class RequestVrsVideosType extends LetvHttpAsyncTask<VideoTypes> {

		public RequestVrsVideosType(Context context) {
			super(context, false);
		}

		@Override
		public VideoTypes loadLocalData() {
			try {
				LocalCacheBean bean = LetvCacheDataHandler.readVideoTypesData();
				VideoTypes result = new LetvGsonParser<VideoTypes>(0,
						VideoTypes.class).initialParse(bean.getCacheData());
				return result;
			} catch (Exception e) {
			}
			return null;
		}

		@Override
		public boolean loadLocalDataComplete(VideoTypes t) {
			if (null != t) {
				onPostExecute(0, t);
				return true;
			}
			return false;
		}

		@Override
		public LetvDataHull<VideoTypes> doInBackground() {
			LetvDataHull<VideoTypes> dataHull = null;
			dataHull = LetvHttpApi.requestVideoTypes(0,
					new LetvGsonParser<VideoTypes>(0, VideoTypes.class));
			// dataHull = LetvHttpApi.requestVideoTypes(new
			// LetvGsonParser<VideoTypes>(0, VideoTypes.class));
			if (dataHull.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
				LetvCacheDataHandler.saveVideoTypesData(dataHull
						.getSourceData());
			}
			return dataHull;
		}

		@Override
		public void onPostExecute(int updateId, VideoTypes result) {

			if (null != result) {
				sortTypeBean = result.body.sort;
				newsTypeBean = result.body.filter;
			}

			if (null != filterAdapter) {
				filterAdapter.notifyDataSetChanged();
			}
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			// super.dataNull(updateId, errMsg);
		}

		@Override
		public void netErr(int updateId, String errMsg) {
			if (!isLocalSucceed()) {
				bad_network.setVisibility(View.VISIBLE);
				video_news_fragment.setVisibility(View.GONE);
			}
		}

		@Override
		public void netNull() {
			if (!isLocalSucceed()) {
				bad_network.setVisibility(View.VISIBLE);
				video_news_fragment.setVisibility(View.GONE);
			}
		}

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (rootView != null) {
			ViewGroup parent = (ViewGroup) rootView.getParent();
			if (parent != null)
				parent.removeView(rootView);
		}
		/**
		 * frament 销毁时，移除此fragment
		 */

		Fragment fragment = getFragmentManager().findFragmentById(
				R.id.root_video);
		FragmentTransaction ft = getActivity().getSupportFragmentManager()
				.beginTransaction();
		if (null != fragment && !ft.isEmpty()) {
			ft.remove(fragment).commitAllowingStateLoss();
		}

		videoListFragment = null;
		video_news_fragment_filterRadioButton_orderBy = null;
		video_news_fragment_filterRadioButton_itemId = null;
		video_news_fragment_selector_title1 = null;
		video_news_fragment_selector_title2 = null;
		video_news_fragment_selector_icon1 = null;
		video_news_fragment_selector_icon2 = null;
		video_news_lstFilter = null;
		video_news_lstFilter_cover = null;
		filterAdapter = null;
		newsTypeBean = null;
		currentTypeBean = null;
		sortTypeBean = null;
		mHomeFragmentLsn = null;
		bad_network = null;
		video_news_fragment = null;
	}

	public void setHomeFragmentLsn(HomeFragmentLsn mHomeFragmentLsn) {
		this.mHomeFragmentLsn = mHomeFragmentLsn;
	}

}
