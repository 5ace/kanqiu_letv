package com.letv.watchball.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.http.bean.LetvDataHull;
import com.letv.http.parse.LetvGsonParser;
import com.letv.watchball.LetvApplication;
import com.letv.watchball.R;
import com.letv.watchball.async.LetvHttpAsyncTask;
import com.letv.watchball.bean.FocusTeamList;
import com.letv.watchball.fragment.GMyTeamMatchFragment;
import com.letv.watchball.http.api.LetvHttpApi;

public class MyTeamMatchActivity extends LetvBaseActivity implements OnClickListener{
	private GMyTeamMatchFragment mGMyTeamMatchFragment;
	
	/***************球队相关信息*********************/
	private String name;//球队名
	private String teamId;//球队id
	private String level;//球队level
	private String focused;//球队关注状态 1关注 0未关注
	/***********************************/
	
	/**
	 * 是否关注  初始关注
	 */
	private boolean isFocused = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_my_team_match);
//		mySubscribesFragment = (GMySubscribeFragment) getFragmentManager().findFragmentById(R.id.my_subscribe_fragment);
		
		mGMyTeamMatchFragment =  (GMyTeamMatchFragment) getSupportFragmentManager().findFragmentById(R.id.my_teams_fragment);
		findViewById(R.id.toggle_manager_my_team).setOnClickListener(this);
		findViewById(R.id.my_focus_close).setOnClickListener(this);
		
		
		Bundle bundle = getIntent().getExtras();
		if(null != bundle){
			name = bundle.getString("name");
			teamId = bundle.getString("teamId");
			level = bundle.getString("level");
			focused = bundle.getString("focused");
			if(!TextUtils.isEmpty(name)){
				((TextView)findViewById(R.id.my_team_match_name)).setText(name);
			}
			if(!TextUtils.isEmpty(teamId)&&!TextUtils.isEmpty(level)){
				mGMyTeamMatchFragment.loadMyTeamsInfos(teamId, level);
			}
		}
		
	}

	@Override
	public void onClick(final View v) {
		switch (v.getId()) {
			//Close
		case R.id.my_focus_close:
			//初始状态为true，返回时如果是false则状态发生改变
			if(!isFocused){
				setResult(RESULT_OK);
			}
			finish();
			break;
			//关注(取消关注)
		case R.id.toggle_manager_my_team:
			if(!isFocused){
				new RequestFocus(MyTeamMatchActivity.this, teamId, level, new Runnable() {
					
					@Override
					public void run() {
						((ImageView)v).setImageResource(R.drawable.myfocus_item_child_add_sel);
					}
				}).start();
			}else{
				new RequestUnfocus(MyTeamMatchActivity.this, teamId, level, new Runnable() {
					
					@Override
					public void run() {
						((ImageView)v).setImageResource(R.drawable.myfocus_item_child_add_nor);
					}
				}).start();
			}
//			startActivity(new Intent(MyTeamMatchActivity.this, MyFocusManagerActivity.class));
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	/**
	 * http请求  关注
	 * */
	private class RequestFocus extends LetvHttpAsyncTask<FocusTeamList> {

		private String teamId;
		private String level;
		private Runnable callback;

		public RequestFocus(Context context, String teamId, String level, Runnable callback) {
			super(context, true);
			this.teamId = teamId;
			this.level = level;
			this.callback = callback;
		}

		@Override
		public LetvDataHull<FocusTeamList> doInBackground() {
			return LetvHttpApi.requestFocus(teamId, level, new LetvGsonParser<FocusTeamList>(0, FocusTeamList.class));
		}

		@Override
		public void onPostExecute(int updateId, FocusTeamList result) {
			if (Integer.parseInt(result.header.status) == 1) {
				callback.run();
				Toast.makeText(LetvApplication.getInstance(), "关注成功！", Toast.LENGTH_SHORT).show();
                isFocused = true;
			} else {
				Toast.makeText(LetvApplication.getInstance(), "关注失败！", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void netNull() {
			Toast.makeText(LetvApplication.getInstance(), "关注失败！", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void netErr(int updateId, String errMsg) {
			Toast.makeText(LetvApplication.getInstance(), "关注失败！", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			Toast.makeText(LetvApplication.getInstance(), "关注失败！", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * http请求  取消关注
	 * */
	private class RequestUnfocus extends LetvHttpAsyncTask<FocusTeamList> {

		private String teamId;
		private String level;
		private Runnable callback;

		public RequestUnfocus(Context context, String teamId, String level, Runnable callback) {
			super(context, true);
			this.teamId = teamId;
			this.level = level;
			this.callback = callback;
		}

		@Override
		public LetvDataHull<FocusTeamList> doInBackground() {
			return LetvHttpApi.requestUnfocus(teamId, level, new LetvGsonParser<FocusTeamList>(0, FocusTeamList.class));
		}

		@Override
		public void onPostExecute(int updateId, FocusTeamList result) {
			if (Integer.parseInt(result.header.status) == 1) {
				callback.run();
				Toast.makeText(LetvApplication.getInstance(), "取消关注成功！", Toast.LENGTH_SHORT).show();
                isFocused = false;
			} else {
				Toast.makeText(LetvApplication.getInstance(), "取消关注失败！", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void netNull() {
			Toast.makeText(LetvApplication.getInstance(), "取消关注失败！", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void netErr(int updateId, String errMsg) {
			Toast.makeText(LetvApplication.getInstance(), "取消关注失败！", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			Toast.makeText(LetvApplication.getInstance(), "取消关注失败！", Toast.LENGTH_SHORT).show();
		}
	}

	

}
