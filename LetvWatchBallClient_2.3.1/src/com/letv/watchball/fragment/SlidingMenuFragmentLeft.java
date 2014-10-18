package com.letv.watchball.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

import com.letv.http.bean.LetvDataHull;
import com.letv.http.parse.LetvGsonParser;
import com.letv.watchball.LetvApplication;
import com.letv.watchball.R;
import com.letv.watchball.activity.WelcomeActivity;
import com.letv.watchball.adapter.LeftFragmentAdapter;
import com.letv.watchball.async.LetvHttpAsyncTask;
import com.letv.watchball.bean.LocalCacheBean;
import com.letv.watchball.bean.MatchList;
import com.letv.watchball.bean.MatchList.Body.Match;
import com.letv.watchball.bean.MatchList.Body.OriginalColumn;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.manager.LeftFragmentLsn;
import com.letv.watchball.utils.LetvCacheDataHandler;

@SuppressLint("ValidFragment")
public class SlidingMenuFragmentLeft extends Fragment{
	/**
	 * tag parent
	 */
	private String[] tagParent;

	private ArrayList<LeftFragmentItem> itemsTop = new ArrayList<LeftFragmentItem>();// 顶部item

	private LeftFragmentLsn callback;
	private ExpandableListView mListView;
	private LeftFragmentAdapter adapter;

	public SlidingMenuFragmentLeft() {
	}

	public SlidingMenuFragmentLeft(LeftFragmentLsn callback) {
		this.callback = callback;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_left,null, false);
		mListView = (ExpandableListView) root.findViewById(R.id.listView1);
		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		String[] tagTop = getResources().getStringArray(R.array.watchball_top);
		TypedArray tagTopIcon = getResources().obtainTypedArray(R.array.watchball_top_icon);
		tagParent = getResources().getStringArray(R.array.watchball_tags);
		adapter = new LeftFragmentAdapter(getActivity());
		// add tag top
		for (int i = 0; i < tagTop.length; i++) {
			LeftFragmentItem topItem = new LeftFragmentItem(tagTop[i], tagTopIcon.getResourceId(i, -1));
			itemsTop.add(topItem);
		}
		adapter.groupItemList.add(tagParent[0]);
		adapter.childItemList.add(itemsTop);
		mListView.setAdapter(adapter);
//		mListView.setGroupIndicator(null);
//		mListView.setCacheColorHint(Color.TRANSPARENT);
//		mListView.setDivider(getResources().getDrawable(R.drawable.left_item_line_repeate));
		notifyDateChanged();
		reloadMatchList();
	}
	
	public void reloadMatchList(){
		/**
		 * 如果之前有加载的赛事列表，直接返回
		 */
		if(adapter.groupItemList.size() > 1){
			return;
		}
		new RequestMatchList(getActivity()).start();
	}

	public void notifyDateChanged() {
		 adapter.notifyDataSetChanged();
		// 展开所有parent
		for (int i = 0; i < adapter.groupItemList.size(); i++) {
			mListView.expandGroup(i);
		}
		// 设置parent不可点击
		mListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				return true;
			}
		});
		
		
		mListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				LeftFragmentItem item = adapter.getChild(groupPosition, childPosition);
				LetvApplication.getInstance().setShowVideoList(true);
				switch (groupPosition) {
				case 0:
					// 直播
					if (childPosition == 0) {
						callback.invoke(LeftFragmentLsn.ACTION_LIVE, null);
					} else if (childPosition == 1) {
						// 我的球队
						callback.invoke(LeftFragmentLsn.ACTION_MY_TEAM, null);
					}
					break;
				case 1:
					//赛事
					if (null != item) {
                                    if (item.type.equals("310")){
                                          callback.invoke(LeftFragmentLsn.ACTION_LIVE, "0");
                                    } else {
                                          callback.invoke(LeftFragmentLsn.ACTION_EVENTS, item.match);
                                    }

					}
					break;
				case 2:
					//原创节目
					if (null != item) {
						callback.invoke(LeftFragmentLsn.ACTION_ORIGINAL, item.originalColumn);
					}
					break;
				}
				adapter.onItemClick(groupPosition,childPosition);
				adapter.notifyDataSetChanged();
				return true;
			}
		});
	}
	

	/**
	 * http请求赛事列表
	 * */
	private class RequestMatchList extends LetvHttpAsyncTask<MatchList> {

		public RequestMatchList(Context context) {
			super(context, true);

		}

		@Override
		public MatchList loadLocalData() {
			try {
				LocalCacheBean bean = LetvCacheDataHandler.readMatchListData();
				MatchList result = new LetvGsonParser<MatchList>(0,MatchList.class).initialParse(bean.getCacheData());
				return result;
			} catch (Exception e) {
			}
			return null;
		}
		
		@Override
		public boolean loadLocalDataComplete(MatchList t) {
			if(null != t){
				onPostExecute(0, t);
				return true;
			}
			return false;
		}

		@Override
		public LetvDataHull<MatchList> doInBackground() {
			LetvDataHull<MatchList> dataHull = LetvHttpApi.requestMatchlist(true, new LetvGsonParser<MatchList>(0, MatchList.class));
			if (dataHull.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
				LetvCacheDataHandler.saveMatchListData(dataHull.getSourceData());
			}
			return dataHull;
//			return LetvHttpApi.requestMatchlist(true, new LetvGsonParser<MatchList>(0, MatchList.class));
		}

		@Override
		public void onPostExecute(int updateId, MatchList result) {
			
			//先clear之前的数据(缓存)
			while(adapter.groupItemList.size() > 1) {	
				adapter.groupItemList.remove(1);
				adapter.childItemList.remove(1);
			}
			// 添加网络加载的 赛事列表
			Match[] matchs = result.body.match_list;
			ArrayList<LeftFragmentItem> itemsMatch = new ArrayList<LeftFragmentItem>();
			for (int i = 0; i < matchs.length; i++) {
				LeftFragmentItem sampleItem = new LeftFragmentItem(matchs[i].name, matchs[i].img_url, matchs[i].type);
				sampleItem.match = matchs[i];
				itemsMatch.add(sampleItem);
			}
			adapter.groupItemList.add(tagParent[1]);
			adapter.childItemList.add(itemsMatch);
			// 添加网络加载的 原创节目列表
			OriginalColumn[] originals = result.body.original_columns;
			 ArrayList<LeftFragmentItem> itemsOriginal = new ArrayList<LeftFragmentItem>();
			for (int i = 0; i < originals.length; i++) {
				LeftFragmentItem sampleItem = new LeftFragmentItem(originals[i].name, originals[i].img_url, originals[i].id + "");
				sampleItem.originalColumn = originals[i];
				itemsOriginal.add(sampleItem);
			}
			adapter.groupItemList.add(tagParent[2]);
			adapter.childItemList.add(itemsOriginal);
			notifyDateChanged();
		}

		@Override
		public void netNull() {
			System.err.println("netNull");
		}

		@Override
		public void netErr(int updateId, String errMsg) {
			System.err.println("netErr()->" + "updateId:" + updateId + "  ,errMsg:" + errMsg);
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			System.err.println("dataNull()->" + "updateId:" + updateId + "  ,errMsg:" + errMsg);
		}
	}

	public class LeftFragmentItem {

		public String childTitle;// 赛事或原创节目 name
		public int imgRes;// 赛事或原创节目 图片res(初始化)
		public String imgUrl;// 赛事或原创节目 图片url(网络)
		public String type;// 赛事或原创节目 id
		public Match match;// 赛事info
		public OriginalColumn originalColumn;// 原创info

		public LeftFragmentItem(String name, int imgRes) {
			this.childTitle = name;
			this.imgRes = imgRes;
		}

		public LeftFragmentItem(String name, String imgUrl, String type) {
			this.childTitle = name;
			this.imgUrl = imgUrl;
			this.type = type;
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		/**
		 * frament 销毁时，移除此fragment
		 */
		
		Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_left);
		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		if(null != fragment&&!ft.isEmpty()){
			ft.remove(fragment).commitAllowingStateLoss();
		}
	}
}
