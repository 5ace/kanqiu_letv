package com.letv.watchball.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.letv.cache.LetvCacheMannager;
import com.letv.cache.view.LetvImageView;
import com.letv.http.bean.LetvDataHull;
import com.letv.http.parse.LetvGsonParser;
import com.letv.watchball.R;
import com.letv.watchball.async.LetvHttpAsyncTask;
import com.letv.watchball.bean.MatchList.Body.Match;
import com.letv.watchball.bean.RankingTable;
import com.letv.watchball.bean.RankingTable.Body;
import com.letv.watchball.bean.RankingTable.Body.GroupList;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.utils.TextUtil;

/**
 * 排名Fragment
 * 
 * @author Liuheyuan
 * 
 */
public class RankerFragment extends Fragment {

	/**
	 * 排名分组ListView
	 */
	private ExpandableListView ranker_fragment_expandableListView;

	private MyExpandableAdapter myExpandableAdapter = new MyExpandableAdapter();

	/**
	 * 赛事ID
	 */
	private String match_type;

	/**
	 * 赛事类型，足球，篮球
	 */
	private String level;

	/**
	 * 请求数据线程
	 */
	private RequestRankData requestRankData;

	/**
	 * 排名数据
	 */
	private RankingTable rankingTable = new RankingTable();

	private Match match;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// return super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.ranker_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		ranker_fragment_expandableListView = (ExpandableListView) getView()
				.findViewById(R.id.ranker_fragment_expandableListView);
		ranker_fragment_expandableListView.setAdapter(myExpandableAdapter);
		ranker_fragment_expandableListView.setGroupIndicator(null);
		ranker_fragment_expandableListView
				.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

					@Override
					public boolean onGroupClick(ExpandableListView parent,
							View v, int groupPosition, long id) {
						return true;
					}
				});
	}

	/**
	 * 设置当前赛事ID
	 * 
	 * @param match_type
	 */
	private void setMatch_type(String match_type) {
		this.match_type = match_type;
	}

	/**
	 * 设置当前赛事类型
	 * 
	 * @param level
	 */
	private void setLevel(String level) {
		this.level = level;
		if ("2".equals(level)) {
			// 篮球
			getView().findViewById(R.id.ranker_fragment_draw).setVisibility(
					View.GONE);
			((TextView) getView().findViewById(R.id.ranker_fragment_score_tv))
					.setText(R.string.ranker_fragment_header_win_rate);
		} else {
			getView().findViewById(R.id.ranker_fragment_draw).setVisibility(
					View.VISIBLE);
			((TextView) getView().findViewById(R.id.ranker_fragment_score_tv))
					.setText(R.string.ranker_fragment_header_score);
		}
	}

	public void setMatch(Match match) {
		if (null == match) {
			return;
		}
		if (null != this.match && this.match.type.equals(match.type)) {
			return;
		}
		// match变化时，先clear之前的数据
		rankingTable = new RankingTable();
		myExpandableAdapter.notifyDataSetChanged();
		this.match = match;
		setLevel(match.level);
		setMatch_type(match.type);
		requestData();
	}

	/**
	 * 请求数据
	 */
	public void requestData() {
		if (null != requestRankData) {
			requestRankData.cancel(true);
		}
		requestRankData = new RequestRankData(getActivity());
		requestRankData.start();

	}

	/**
	 * 请求VrsVideos数据
	 * 
	 * @author Liuheyuan
	 * 
	 */
	private class RequestRankData extends LetvHttpAsyncTask<RankingTable> {

		public RequestRankData(Context context) {
			super(context, true);
		}

		@Override
		public LetvDataHull<RankingTable> doInBackground() {
			LetvDataHull<RankingTable> dataHull = null;

			dataHull = LetvHttpApi.requestTable(match_type, level,
					new LetvGsonParser<RankingTable>(0, RankingTable.class));
			return dataHull;
		}

		@Override
		public void onPostExecute(int updateId, RankingTable result) {
			synchronized (rankingTable) {
				rankingTable = result;
				// // 通知界面刷新数据
				// refrashListView();
				myExpandableAdapter.notifyDataSetChanged();
				int groupCount = myExpandableAdapter.getGroupCount();
				for (int i = 0; i < groupCount; i++) {
					ranker_fragment_expandableListView.expandGroup(i);
				}
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

	/**
	 * @author Liuheyuan 排名分组适配器
	 */
	class MyExpandableAdapter extends BaseExpandableListAdapter {

		LayoutParams lParams0;
		LayoutParams lParamsNomel;

		@Override
		public int getGroupCount() {
			if (null != rankingTable && null != rankingTable.body) {
				return rankingTable.body.length;
			}

			return 0;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			if (null != rankingTable && null != rankingTable.body) {
				return rankingTable.body[groupPosition].list.length;
			}

			return 0;
		}

		@Override
		public Body getGroup(int groupPosition) {
			return rankingTable.body[groupPosition];
		}

		@Override
		public GroupList getChild(int groupPosition, int childPosition) {
			return rankingTable.body[groupPosition].list[childPosition];
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			GroupHolder mHolder = null;
			if (null == convertView) {
				mHolder = new GroupHolder();
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.ranker_fragment_list_item_group, null);
				mHolder.groupName = (TextView) convertView
						.findViewById(R.id.ranker_fragment_item_rank_group);
				convertView.setTag(mHolder);

				lParams0 = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
				lParamsNomel = (LayoutParams) convertView.getLayoutParams();
			} else {
				mHolder = (GroupHolder) convertView.getTag();
			}
			String groupName = getGroup(groupPosition).group;
			if (TextUtils.isEmpty(groupName.trim())) {
				if (null != lParams0)
					convertView.setLayoutParams(lParams0);
			} else {
				if (null != lParamsNomel)
					convertView.setLayoutParams(lParamsNomel);
				mHolder.groupName.setText(groupName);
			}
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			ChildHolder mHolder = null;
			if (null == convertView) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.ranker_fragment_list_item_child, null);
				mHolder = new ChildHolder();
				mHolder.rank = (TextView) convertView
						.findViewById(R.id.ranker_fragment_item_rank);
				mHolder.itemIcon = (LetvImageView) convertView
						.findViewById(R.id.ranker_fragment_item_icon);
				mHolder.itemName = (TextView) convertView
						.findViewById(R.id.ranker_fragment_item_name);
				mHolder.win = (TextView) convertView
						.findViewById(R.id.ranker_fragment_item_win);
				mHolder.equality = (TextView) convertView
						.findViewById(R.id.ranker_fragment_item_equality_tv);
				mHolder.equality_main = (LinearLayout) convertView
						.findViewById(R.id.ranker_fragment_item_equality);
				mHolder.lose = (TextView) convertView
						.findViewById(R.id.ranker_fragment_item_lose);
				mHolder.score = (TextView) convertView
						.findViewById(R.id.ranker_fragment_item_score);
				mHolder.ranker_item_main = (LinearLayout) convertView
						.findViewById(R.id.ranker_item_main);
				convertView.setTag(mHolder);
			}

			mHolder = (ChildHolder) convertView.getTag();
			// 设置背景颜色
			if (childPosition % 2 == 0) {
				mHolder.ranker_item_main.setBackgroundColor(0xffffffff);
			} else {
				mHolder.ranker_item_main.setBackgroundColor(0xffefefef);
			}
			GroupList mGroupList = getChild(groupPosition, childPosition);
			mHolder.rank.setText(String.valueOf(mGroupList.rank));
			mHolder.itemName.setText(String.valueOf(mGroupList.team));
			mHolder.win.setText(String.valueOf(mGroupList.win));
			mHolder.lose.setText(String.valueOf(mGroupList.lose));
			if ("2".equals(level)) {
				mHolder.equality_main.setVisibility(View.GONE);
				mHolder.score.setText(String.valueOf(mGroupList.win_rate));
			} else {
				mHolder.equality_main.setVisibility(View.VISIBLE);
				mHolder.equality.setText(String.valueOf(mGroupList.draw));
				mHolder.score.setText(String.valueOf(mGroupList.score));
			}
			mHolder.itemIcon.setImageResource(R.drawable.ic_default);
			LetvCacheMannager.getInstance().loadImage(mGroupList.img_url,
					mHolder.itemIcon);
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}

		class ChildHolder {
			public TextView rank;
			public LetvImageView itemIcon;
			public TextView itemName;
			public TextView win;
			public TextView equality;
			public TextView lose;
			public TextView score;
			public LinearLayout ranker_item_main, equality_main;
		}

		class GroupHolder {
			public TextView groupName;
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		/**
		 * frament 销毁时，移除此fragment
		 */
		Fragment fragment = getFragmentManager().findFragmentById(
				R.id.ranker_fragment);
		FragmentTransaction ft = getActivity().getSupportFragmentManager()
				.beginTransaction();
		if (null != fragment && !ft.isEmpty()) {
			ft.remove(fragment).commitAllowingStateLoss();
		}
	}
}
