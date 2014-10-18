package com.letv.watchball.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;

import com.letv.http.bean.LetvDataHull;
import com.letv.http.parse.LetvGsonParser;
import com.letv.watchball.adapter.LiveAdapter.MODE;
import com.letv.watchball.async.LetvHttpAsyncTask;
import com.letv.watchball.bean.Game;
import com.letv.watchball.bean.MyTeamMatch;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.utils.LetvCacheDataHandler;

public class GMyTeamMatchFragment extends GBaseFragment {

	/**
	 * 是否首次加载
	 */
	private boolean isLoaded = false;
//	/**
//	 * empty view
//	 */
//	private EmptyAlertView emptyView;

	public GMyTeamMatchFragment() {
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// 设置emptyview
//		emptyView = new EmptyAlertView(getActivity());
//		((ViewGroup) listView.getParent()).addView(emptyView, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		listView.setEmptyView(emptyView);
		adapter.setMode(MODE.TEAMS);
		
	}

	/**
	 * 首次初始化时数据加载
	 */
	public void loadMyTeamsInfos(String teamId,String level) {
		if (isLoaded) {
			return;
		}
		isLoaded = true;
//		// 友盟统计 我的球队比赛列表
//		HashMap<String, String> map = new HashMap<String, String>();
//		map.put("devId", LetvUtil.getDeviceId(getActivity()));
		new RequestMyTeamsList(getActivity(),teamId,level).start();
	}
	
	private class RequestMyTeamsList extends LetvHttpAsyncTask<MyTeamMatch> {

		private String teamId,level;
		public RequestMyTeamsList(Context context,String teamId,String level) {
			super(context, true);
			this.teamId = teamId;
			this.level = level;

		}

		@Override
		public boolean onPreExecute() {
//			emptyView.setVisibility(View.GONE);
			adapter.listParent.clear();
			adapter.listChild.clear();
			notifyDateChanged();
			return super.onPreExecute();
		}
//
//		@Override
//		public MyAllMatches loadLocalData() {
//			try {
//				LocalCacheBean bean = LetvCacheDataHandler.readMyTeamsData();
//				MyAllMatches result = new LetvGsonParser<MyAllMatches>(0, MyAllMatches.class).initialParse(bean.getCacheData());
//				return result;
//			} catch (Exception e) {
//			}
//			return null;
//		}
//
//		@Override
//		public boolean loadLocalDataComplete(MyAllMatches result) {
//			if (null != result) {
//				if (result.body.length == 0) {
//					hasMatch = false;
//					emptyView.showNoMatchs();
//				} else {
//					hasMatch = true;
//				}
//				adapter.listParent.clear();
//				adapter.listChild.clear();
//				for (int i = 0; i < result.body.length; i++) {
//					adapter.listParent.add(result.body[i].date);
//					Game[] games = result.body[i].matches;
//					ArrayList<Game> children = new ArrayList<Game>();
//					for (int j = 0; j < games.length; j++) {
//						children.add(games[j]);
//					}
//					adapter.listChild.add(children);
//				}
//				notifyDateChanged();
//				return true;
//			}
//			return false;
//		}

		@Override
		public LetvDataHull<MyTeamMatch> doInBackground() {
			LetvDataHull<MyTeamMatch> dataHull = LetvHttpApi.requestMyTeamMatch(teamId,level,new LetvGsonParser<MyTeamMatch>(0, MyTeamMatch.class));
			if (dataHull.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
				LetvCacheDataHandler.saveMyTeamsData(dataHull.getSourceData());
			}
			return dataHull;
		}

		@Override
		public void onPostExecute(int updateId, MyTeamMatch result) {
			if (null == result || null == result.body || null == result.body.month_matches || null == result.body.month_matches || result.body.month_matches.length == 0) {
//				emptyView.showNoMatchs();
			} else {
			}
			adapter.listParent.clear();
			adapter.listChild.clear();
			for (int i = 0; i < result.body.month_matches.length; i++) {
				adapter.listParent.add(result.body.month_matches[i].date);
				Game[] games = result.body.month_matches[i].matches;
				ArrayList<Game> children = new ArrayList<Game>();
				for (int j = 0; j < games.length; j++) {
					children.add(games[j]);
				}
				adapter.listChild.add(children);
			}
			notifyDateChanged();
		}

		@Override
		public void netNull() {
			isLoaded = false;
		}

		@Override
		public void netErr(int updateId, String errMsg) {
			isLoaded = false;
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			isLoaded = false;
		}
	}


}
