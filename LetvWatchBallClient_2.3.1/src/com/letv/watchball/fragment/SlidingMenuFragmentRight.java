package com.letv.watchball.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.letv.cache.LetvCacheMannager;
import com.letv.datastatistics.DataStatistics;
import com.letv.http.bean.LetvDataHull;
import com.letv.http.parse.LetvGsonParser;
import com.letv.watchball.R;
import com.letv.watchball.activity.LoginMainActivity;
import com.letv.watchball.activity.MyFocusManagerActivity;
import com.letv.watchball.activity.MyTeamMatchActivity;
import com.letv.watchball.activity.SettingsActivity;
import com.letv.watchball.activity.WelcomeActivity;
import com.letv.watchball.adapter.MyTeamsAdapter;
import com.letv.watchball.async.LetvHttpAsyncTask;
import com.letv.watchball.bean.Game;
import com.letv.watchball.bean.MyTeams;
import com.letv.watchball.bean.MyTeams.Body;
import com.letv.watchball.bean.User;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.manager.RightFragmentLsn;
import com.letv.watchball.parser.UserParser;
import com.letv.watchball.utils.LetvUtil;
import com.letv.watchball.utils.UIs;
import com.letv.watchball.view.RoundImageView;

public class SlidingMenuFragmentRight extends Fragment implements OnClickListener{
	private final int REQUEST_CODE_MYTEAM_MATCH = 0x0010;
	private final int REQUST_CODE_MYFOCUS = 0x0011;
	
	/**
	 * 我的预约
	 */
	private GMySubscribeFragment mySubscribesFragment;
	/**
	 * 我的球队
	 */
	private View view ;
	private ListView myTeamsList;
	private RightFragmentLsn mRightFragmentLsn;
	private boolean isDataLoaded = false;
	private boolean isLogin = false;
	public SlidingMenuFragmentRight(){
		
	}
	public SlidingMenuFragmentRight(RightFragmentLsn mRightFragmentLsn) {
		this.mRightFragmentLsn = mRightFragmentLsn;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if (view != null) {
	        ViewGroup parent = (ViewGroup) view.getParent();
	        if (parent != null)
	            parent.removeView(view);
	    }
	    try {
	    	view = inflater.inflate(R.layout.fragment_right, container, false);
	    } catch (InflateException e) {
	        /* map is already there, just return view as it is */
	    }
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(view!=null){
		myTeamsList = (ListView) getActivity().findViewById(R.id.my_teams);
		mySubscribesFragment = (GMySubscribeFragment) getFragmentManager().findFragmentById(R.id.my_subscribe_fragment);
		mySubscribesFragment.setRightFragmentLsn(mRightFragmentLsn);
		getActivity().findViewById(R.id.settings).setOnClickListener(this);
		getActivity().findViewById(R.id.btn_head_login).setOnClickListener(this);
		getActivity().findViewById(R.id.btn_head_loginout).setOnClickListener(this);
		getActivity().findViewById(R.id.right_mysubscribe).setOnClickListener(this);
		getActivity().findViewById(R.id.right_myteams).setOnClickListener(this);
//		getActivity().findViewById(R.id.toggle_manager_my_team).setOnClickListener(this);
		
		getActivity().findViewById(R.id.right_mysubscribe).performClick();
		
		myTeamsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(position == 0){
					//添加球队
					startActivityForResult(new Intent(getActivity(), MyFocusManagerActivity.class), 0);
					return;
				
				}
				MyTeams.Body body = (Body) myTeamsList.getAdapter().getItem(position);
				if(null != body){
					Bundle bundle = new Bundle();
					bundle.putString("name", body.name);
					bundle.putString("teamId", body.teamId+"");
					bundle.putString("level", body.level);
					bundle.putString("focused", body.focused);
					Intent intent = new Intent(getActivity(), MyTeamMatchActivity.class);
					intent.putExtras(bundle);
					startActivityForResult(intent, REQUEST_CODE_MYTEAM_MATCH);
				}
			}
			
		});
		initLoginStatus();
		}
	}
	
	public void reflashUI(){
		initLoginStatus();
		if(null != mySubscribesFragment)
			mySubscribesFragment.reflashUI();
	}
	

	@Override
	public void onClick(View v) {
		View lineMyTeams = getView().findViewById(R.id.right_myteams_bottom_line);
		View lineMySubscribe = getView().findViewById(R.id.right_mysubscribe_bottom_line);
		LayoutParams lineMyTeamsParams = lineMyTeams.getLayoutParams(); 
		LayoutParams lineMySubscribeParams = lineMySubscribe.getLayoutParams(); 
		switch (v.getId()) {
		case R.id.settings:
			//设置
			startActivity(new Intent(getActivity(), SettingsActivity.class));
			break;
		case R.id.btn_head_login:
			// 用户头像点击事件
			if (isLogin) {
//				SettingCenterFragmentActivity.launch(mActivity, From.PersonalInfo.getInt());
			} else {
				LoginMainActivity.launch(this);
			}
			break;
		case R.id.btn_head_loginout:
			// 注销
			doLogout();
			
			//登录成功 上报统计 modified by zengsonghai 20131112
			DataStatistics.getInstance().sendLoginInfo(getActivity(), "0", "0", LetvUtil.getUID(), "-", "-", System.currentTimeMillis()/1000 +"", LetvUtil.getPcode(),1);
			break;
		case R.id.right_mysubscribe:
			//我的预约
			lineMyTeamsParams.height = UIs.dipToPx(1);
			lineMySubscribeParams.height = UIs.dipToPx(3);
			lineMyTeams.requestLayout();
			lineMySubscribe.requestLayout();

			mySubscribesFragment.getView().setVisibility(View.VISIBLE);
			myTeamsList.setVisibility(View.GONE);
			loadRightFragmentData();
			break;
			
		case R.id.right_myteams:
			//我的球队
			lineMyTeamsParams.height = UIs.dipToPx(3);
			lineMySubscribeParams.height = UIs.dipToPx(1);
			lineMyTeams.requestLayout();
			lineMySubscribe.requestLayout();
			
			mySubscribesFragment.getView().setVisibility(View.GONE);
			myTeamsList.setVisibility(View.VISIBLE);
			loadRightFragmentData();
			break;
//		case R.id.toggle_manager_my_team:
//			//添加球队
//			startActivityForResult(new Intent(getActivity(), MyFocusManagerActivity.class), 0);
//			break;	
		default:
			break;
		}
	}

	private void initLoginStatus() {

		isLogin = PreferencesManager.getInstance().isLogin();
		if (isLogin) {
			// 用户已登录,请求用户信息(头像,昵称,VIP)
			getView().findViewById(R.id.textv_nickname_hint).setVisibility(View.GONE);
			getView().findViewById(R.id.btn_head_loginout).setVisibility(View.VISIBLE);
			String tk = PreferencesManager.getInstance().getSso_tk();
			String userName = PreferencesManager.getInstance().getUserName();
			new RequestLoginTask(getActivity(), tk, userName).start();
		} else {
			// 用户注销登录
			getView().findViewById(R.id.textv_nickname_hint).setVisibility(View.VISIBLE);
			getView().findViewById(R.id.btn_head_loginout).setVisibility(View.GONE);
		}
	}
	
	/**
	 * 退出登录
	 */
	private void doLogout() {
		UIs.call(getActivity(), R.string.personal_center_activity_logout, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//2013-10-30 by ljnalex delete start 退出登录删除本地播放记录需求不合理
		    	//DBManager.getInstance().getPlayTrace().clearAll();
		    	//2013-10-30 by ljnalex delete end
				
				PreferencesManager.getInstance().logoutUser();
				isLogin = false;
				updateUI(0, null);
				// 用户退出登录数据统计
//				DataStatistics.getInstance().sendUserInfo(getActivity(), LetvUtil.getUID(), LetvUtil.getPcode(), LetvUtil.getSource(), "1",
//						(System.currentTimeMillis() - LetvApplication.getInstance().getLogInTime()) / 1000 + "");
				//注销登录 上报统计 modified by zengsonghai 20131112
//				DataStatistics.getInstance().sendLoginInfo(getActivity(), "0", "0", LetvUtil.getUID(), "-", "-", System.currentTimeMillis()/1000 +"", LetvUtil.getPcode(), 1);
				
//				getActivity().finish();
			}

		}, null);
	}
	
	/**
	 * @param updateId
	 * @param object
	 */
	private void updateUI(int updateId, Object... object) {
		if (object != null) {
			User user = (User) object[0];

			String userName = TextUtils.isEmpty(user.getNickname()) ? user.getUsername() : user.getNickname();
			getActivity().findViewById(R.id.textv_nickname_hint).setVisibility(View.INVISIBLE);
			getView().findViewById(R.id.btn_head_loginout).setVisibility(View.VISIBLE);
			TextView textv_nick = (TextView) getActivity().findViewById(R.id.textv_nickname);
			textv_nick.setText(userName);
			Log.e("gongmeng", "username:"+userName);
			PreferencesManager.getInstance().setNickUserName(userName);
			// 添加头像k
			RoundImageView roundHead = (RoundImageView) getActivity().findViewById(R.id.btn_head_login);
			String icon = user.getPicture();
			roundHead.setImageResource(R.drawable.btn_head);
			LetvCacheMannager.getInstance().loadImage(icon, roundHead);
		}else{
			getActivity().findViewById(R.id.textv_nickname_hint).setVisibility(View.VISIBLE);
			getView().findViewById(R.id.btn_head_loginout).setVisibility(View.INVISIBLE);
			TextView textv_nick = (TextView) getActivity().findViewById(R.id.textv_nickname);
			textv_nick.setText(R.string.user_not_login);

			// 添加头像k
			RoundImageView roundHead = (RoundImageView) getActivity().findViewById(R.id.btn_head_login);
			roundHead.setImageResource(R.drawable.btn_head);
		}
	}
	
	public void loadRightFragmentData() {
		if(mySubscribesFragment.getView().getVisibility() == View.VISIBLE){
			mySubscribesFragment.loadMySubscribe();
		
		}else{
			if(isDataLoaded)
				return ;
			isDataLoaded = true;
			new RequestMyTeams(getActivity()).start();
		}
	}

	public void loadloadMySubscribetData() {
			if(mySubscribesFragment!=null){
            mySubscribesFragment.reflashUI();
			}
	}

      public void loadMyTeamData(){
            Log.d("smy", "loadRightFragmentData");
            new RequestMyTeams(getActivity()).start();
      }



	public void addSubscribe(Game game,String date) {
		mySubscribesFragment.addSubscribe(game,date);
	}
	public void removeSubscribe(String id) {
		mySubscribesFragment.removeSubscribe(id);
	}
	
	@Override
	public void onResume() {
		super.onResume();
//		isLogin();
	
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			isDataLoaded = false;
			loadRightFragmentData();
//			new RequestMyTeams(getActivity());
		}
		//登录返回
		if(requestCode == LoginMainActivity.LOGIN){
			initLoginStatus();
		}
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		/**
		 * frament 销毁时，移除此fragment
		 */
		Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_right);
		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		if(null != fragment&&!ft.isEmpty()){
			ft.remove(fragment).commitAllowingStateLoss();
		}
		
	}
	/* ========================= 网络请求 =================================== */

	private class RequestLoginTask extends LetvHttpAsyncTask<User> {

		private String tk;

		private String userName;

		public RequestLoginTask(Context context, String tk, String userName) {
			super(context);
			this.tk = tk;
			this.userName = userName;
			Log.e("gongmeng", "get nickname");
		}

		@Override
		public LetvDataHull<User> doInBackground() {
			LetvDataHull<User> dataHull = LetvHttpApi.requestUserInfoByTk(0, tk, new UserParser());
//			if (dataHull.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
//				LetvDataHull<LeDian> ldh = LetvHttpApi.queryRecord(0, userName, "", "", "02", "0", "", "", "",
//						new LeDianParser());
//				if (ldh.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
//					dataHull.getDataEntity().setPoint(ldh.getDataEntity().getBalance() + "");
//				}
//			}
			return dataHull;
		}

		@Override
		public void onPostExecute(int updateId, User result) {
			Log.e("gongmeng", "update the nickname");
			Log.e("gongmeng", result.getNickname());
			updateUI(0, result);
		}
	}
	
	/**
	 * http请求
	 * */
	private class RequestMyTeams extends LetvHttpAsyncTask<MyTeams>{

		public RequestMyTeams(Context context) {
			super(context,true);
			
		}

		@Override
		public LetvDataHull<MyTeams> doInBackground() {
			return LetvHttpApi.requestMyTeams(new LetvGsonParser<MyTeams>(0, MyTeams.class));
		}

		@Override
		public void onPostExecute(int updateId, MyTeams result) {
			//解析关注列表数据
			ArrayList<Body> teamList = new ArrayList<Body>();
			if(result.body.length >0){
				for (MyTeams.Body body : result.body) {
					body.focused = 1 + "";
					teamList.add(body);
				}
			}
			MyTeamsAdapter myTeamAdapte = new MyTeamsAdapter(getActivity(),teamList);
			myTeamsList.setAdapter(myTeamAdapte);
			
		}
		
		@Override
		public void netNull() {
			isDataLoaded = false;
		}
		
		@Override
		public void netErr(int updateId, String errMsg) {
			isDataLoaded = false;
		}
		
		@Override
		public void dataNull(int updateId , String errMsg) {
			isDataLoaded = false;
		}
	}
	
}
