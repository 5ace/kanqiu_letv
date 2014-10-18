package com.letv.watchball.activity;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;

import com.letv.http.bean.LetvDataHull;
import com.letv.http.parse.LetvGsonParser;
import com.letv.watchball.R;
import com.letv.watchball.adapter.MyFocusAdapter;
import com.letv.watchball.async.LetvHttpAsyncTask;
import com.letv.watchball.bean.FocusTeamList;
import com.letv.watchball.bean.FocusTeamList.Body.Team;
import com.letv.watchball.http.api.LetvHttpApi;

public class MyFocusManagerActivity extends LetvBaseActivity {

	private ExpandableListView mExpandableListView;
	private MyFocusAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_myfocus);
		mExpandableListView = (ExpandableListView) findViewById(R.id.my_focus_list);
		mExpandableListView.setGroupIndicator(null);
		findViewById(R.id.my_focus_close).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
			}
		});
		new RequestFocusTeams(this).start();

	}

	@Override
	public void finish() {
		
		if (mAdapter!=null&&mAdapter.hasFocusStateChanged()) {
			// 关注为非空状态 回到球队管理
			setResult(RESULT_OK);
			// startActivityForResult(new Intent(MyFocusManagerActivity.this,
			// MyTeamMatchActivity.class), 0);
		}
		super.finish();
	}

	/**
	 * http请求
	 * */
	private class RequestFocusTeams extends LetvHttpAsyncTask<FocusTeamList> {

		public RequestFocusTeams(Context context) {
			super(context, true);

		}

		@Override
		public LetvDataHull<FocusTeamList> doInBackground() {
			return LetvHttpApi.requestGetFocusTeam(new LetvGsonParser<FocusTeamList>(0, FocusTeamList.class));
		}

		@Override
		public void onPostExecute(int updateId, FocusTeamList result) {
			// 解析关注列表数据
			if (null != result.body && result.body.length > 0) {
//				ArrayList<FocusItemParent> parentItemList = new ArrayList<FocusItemParent>();
//				ArrayList<ArrayList<Team>> childItemList = new ArrayList<ArrayList<Team>>();
//				for (FocusTeamList.Body body : result.body) {
//					FocusItemParent parentItem = new FocusItemParent();
//					parentItem.matchId = body.matchId;
//					parentItem.name = body.name;
//					parentItem.focused = body.focused;
//					parentItem.imgUrl = body.img_url;
//					parentItem.level = body.level;
//					// childItemList.add((ArrayList<Team>)
//					// Arrays.asList(body.teams));
//					ArrayList<Team> teamList = new ArrayList<Team>();
//					if (null == body.teams || body.teams.length == 0)
//						continue;
//					for (Team team : body.teams) {
//						teamList.add(team);
//					}
//					childItemList.add(teamList);
//					parentItemList.add(parentItem);
//				}
//				mAdapter = new MyFocusAdapter(parentItemList, childItemList);
				mAdapter = new MyFocusAdapter(result.body);
				mExpandableListView.setAdapter(mAdapter);
//				mExpandableListView.expandGroup(0);
				// mExpandableListView.setOnGroupClickListener(mOnGroupClickListener);
				// mAdapter.notifyDataSetChanged();
			}

		}

		@Override
		public void netNull() {
		}

		@Override
		public void netErr(int updateId, String errMsg) {
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
		}
	}

//	public class FocusItemParent {
//		public String matchId;
//		public String name;
//		public String focused;
//		public String imgUrl;
//		public String level;
//	}
}
