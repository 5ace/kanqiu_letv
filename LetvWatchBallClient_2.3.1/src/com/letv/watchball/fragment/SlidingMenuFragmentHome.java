package com.letv.watchball.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.handmark.pulltorefresh.library.internal.Utils;
import com.letv.android.slidingmenu.lib.CustomViewAbove;
import com.letv.android.slidingmenu.lib.app.SlidingFragmentActivity;
import com.letv.datastatistics.DataStatistics;
import com.letv.watchball.LetvApplication;
import com.letv.watchball.R;
import com.letv.watchball.activity.MainActivity;
import com.letv.watchball.activity.WelcomeActivity;
import com.letv.watchball.bean.WorldCupEntity;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.manager.FragmentManager;
import com.letv.watchball.manager.HomeFragmentLsn;
import com.letv.watchball.utils.LetvUtil;
import com.letv.watchball.utils.UIs;

public class SlidingMenuFragmentHome extends Fragment implements OnClickListener {

	private String tag = SlidingMenuFragmentHome.class.getSimpleName();
	private SlidingFragmentActivity activity;
	private  static Handler mHandler=new Handler();
	/**
	 * touch事件的起始坐标点
	 */
	private float startPosX, endPosX;
	/**
	 * 是否触发move事件标志
	 */
	private boolean isMoved = false;
	/**
	 * 
	 */
	private boolean ispost=true;
	/**
	 * 直播fragment
	 */
	private GLiveInfoFragment mLiveFragment;
      // private View switcherBg;

      /**
       *
       */
      private WorldCupFragment mWorldCupFragment;

	/**
	 * 视频新闻fragment
	 */
	private VideoNewsFragment mVideoNewsFragment;
      private HomeFragmentLsn mHomeFragmentLsn;
      public static final String FRAGMENT_TAG_LIVE = "live";
      public static final String FRAGMENT_TAG_NEWS = "news";
      private View rootView;
      private TextView live, news;
      private boolean isLiveFragment = true;
      private boolean isWorldCupFragment;

      @Override
	public void onAttach(Activity activity) {
		// Log.d("lhz", "SlidingMenuFragmentHome:"+"onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Log.d("lhz", "SlidingMenuFragmentHome:"+"onCreate");
		setRetainInstance(true);  
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Log.d("lhz", "SlidingMenuFragmentHome:"+"onCreateView");

		
				if (rootView != null) {
			        ViewGroup parent = (ViewGroup) rootView.getParent();
			        if (parent != null)
			            parent.removeView(rootView);
			    }
			    try {
			    	rootView = inflater.inflate(R.layout.fragment_home, container, false);
			    } catch (InflateException e) {
			        /* map is already there, just return view as it is */
			    }
		        
		        
		
			// switcherBg = rootView.findViewById(R.id.switcher_bg);
			if(rootView!=null){
				live = (TextView) rootView.findViewById(R.id.live);
				news = (TextView) rootView.findViewById(R.id.news);
	
				rootView.findViewById(R.id.toggle_left).setOnClickListener(this);
				rootView.findViewById(R.id.toggle_right).setOnClickListener(this);
				rootView.findViewById(R.id.live).setOnClickListener(this);
				rootView.findViewById(R.id.news).setOnClickListener(this);
	            rootView.findViewById(R.id.world_cup).setOnClickListener(this);
	            RadioButton worldCup = (RadioButton) rootView.findViewById(R.id.world_cup);
	                 if(!TextUtils.isEmpty(WorldCupEntity._getInstance().getName())){
	                	  worldCup.setText(WorldCupEntity._getInstance().getName());
	                 }else{
	                	  worldCup.setText("乐视网");
	                 }
				// rootView.findViewById(R.id.radiogroup_home).setOnTouchListener(this);
				// switcherBg.setOnTouchListener(this);
	//            if(mLiveFragment != null && !mLiveFragment.isAdded())
				mLiveFragment = (GLiveInfoFragment) getFragmentManager().findFragmentById(R.id.container_live);
	//            if(mVideoNewsFragment != null && !mVideoNewsFragment.isAdded())
                        MainActivity mainActivity = (MainActivity) getActivity();
                        mVideoNewsFragment = (VideoNewsFragment) getFragmentManager().findFragmentById(R.id.container_video_news);
                        mWorldCupFragment = (WorldCupFragment) getFragmentManager().findFragmentById(R.id.container_world_cup);
                        mainActivity.setWebFragmentBackListener(mWorldCupFragment);
                        setHomeFragmentLsn(mHomeFragmentLsn);
			// initHomeView();
                        
			
				return rootView;
			}else{
				TextView m=new TextView(getActivity());
				return m;
			}
			}


	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
        Log.i("oyys", "onResume");
       
 //		if (null != mHomeFragmentLsn) {
//			mHomeFragmentLsn.toggleRight();
//		}
	}
	public void setGLiveInfoFragment(GLiveInfoFragment fragment){
		
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// Log.d("lhz", "SlidingMenuFragmentHome:"+"onActivityCreated");
		super.onActivityCreated(savedInstanceState);
		if(rootView!=null){
		rootView.findViewById(R.id.live).performClick();
            boolean showWorldCup = PreferencesManager.getInstance().isShowWorldCup();
            if (!showWorldCup){
                  RadioButton live = (RadioButton) rootView.findViewById(R.id.live);
                  RadioButton news = (RadioButton) rootView.findViewById(R.id.news);
                  RadioButton worldCup = (RadioButton) rootView.findViewById(R.id.world_cup);
                  RadioGroup titleGroup = (RadioGroup) rootView.findViewById(R.id.title_group);
                  worldCup.setVisibility(View.GONE);
                
                  live.setText("视频直播");
                  ViewGroup.LayoutParams liveLayoutParams = live.getLayoutParams();
                  liveLayoutParams.width = UIs.dipToPx(120);
                  live.setLayoutParams(liveLayoutParams);
                  news.setBackgroundResource(R.drawable.event_title_top_right);
                  news.setText("视频新闻");
                  ViewGroup.LayoutParams newsLayoutParams = news.getLayoutParams();
                  newsLayoutParams.width = UIs.dipToPx(120);
                  news.setLayoutParams(newsLayoutParams);
                  titleGroup.invalidate();
            }
		}else{
			mHandler.postDelayed(new Runnable() {

                 @Override
                 public void run() {
                	 Intent i = new Intent();
                     i.setClass(MainActivity.getInstance(), WelcomeActivity.class);

                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                           MainActivity.getInstance().startActivity(i);

                	 MainActivity.getInstance().finish();
                 }
           }, 1000);
			
		}
//		showLiveFragment();
	}

	public SlidingMenuFragmentHome() {

	}

	public SlidingMenuFragmentHome(SlidingFragmentActivity activity, HomeFragmentLsn mHomeFragmentLsn) {
		this.activity = activity;
		this.mHomeFragmentLsn = mHomeFragmentLsn;
	}

	public void setHomeFragmentLsn(HomeFragmentLsn mHomeFragmentLsn) {
		mLiveFragment.setHomeFragmentLsn(mHomeFragmentLsn);
		mVideoNewsFragment.setHomeFragmentLsn(mHomeFragmentLsn);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.toggle_left:
			if (null != activity) {
				activity.toggle();
			
			}
			break;
		case R.id.toggle_right:
			if (null != activity) {
				activity.toggleSecondaryMenu();
				if (null != mHomeFragmentLsn) {
					mHomeFragmentLsn.toggleRight();
				}
			}
			break;
		case R.id.live:
			showLiveFragment();
			break;
		case R.id.news:
			showNewsFragment();
			break;
            case R.id.world_cup:
                  if (isWorldCupFragment){
                        mWorldCupFragment.reloadWorldCup();
                  } else {
                        showWorldCupFragment();
                  }
                  break;
		default:
			break;
		}
	}

      private void showWorldCupFragment() {
            isWorldCupFragment = true;
            isLiveFragment = false;
            CustomViewAbove.mEnabled = false;
            mWorldCupFragment.isWorldCupFragmentShow(true);
            mWorldCupFragment.getView().setVisibility(View.VISIBLE);
            mVideoNewsFragment.getView().setVisibility(View.GONE);
            mLiveFragment.getView().setVisibility(View.GONE);
      }

      /**
	 * 直播
	 */
	public void showLiveFragment() {
            isWorldCupFragment = false;
		isLiveFragment = true;
            CustomViewAbove.mEnabled = true;
            mWorldCupFragment.isWorldCupFragmentShow(false);
//		live.setTextColor(getResources().getColor(R.color.letv_header_tv_sel));
//		news.setTextColor(getResources().getColor(R.color.letv_header_tv_nor));
		mVideoNewsFragment.getView().setVisibility(View.GONE);
            mWorldCupFragment.getView().setVisibility(View.GONE);
		mLiveFragment.getView().setVisibility(View.VISIBLE);
		mLiveFragment.requestLiveData();
	}

	/**
	 * 视频新闻
	 */
	public void showNewsFragment() {
            isWorldCupFragment = false;
		isLiveFragment = false;
            CustomViewAbove.mEnabled = true;
            mWorldCupFragment.isWorldCupFragmentShow(false);
//		live.setTextColor(getResources().getColor(R.color.letv_header_tv_nor));
//		news.setTextColor(getResources().getColor(R.color.letv_header_tv_sel));
		mVideoNewsFragment.getView().setVisibility(View.VISIBLE);
		mLiveFragment.getView().setVisibility(View.GONE);
            mWorldCupFragment.getView().setVisibility(View.GONE);
            mVideoNewsFragment.requestFiltetTypeData();
	}

	public void updateSuscribeStatus() {
		if (mLiveFragment != null) {
			mLiveFragment.updateSuscribeStatus();
		}
	}

	/**
	 * 执行新闻筛选的关闭操作(如果新闻列表不显示，就不用关闭)
	 * 
	 * @return 视频新闻筛选 list 当前是否可见
	 */
	public boolean closeNewsFilter() {
		boolean showVideoNewsFragment=false;
		if(mVideoNewsFragment!=null&&mVideoNewsFragment.getView()!=null){
			showVideoNewsFragment=(mVideoNewsFragment.getView().getVisibility() == View.GONE) || mVideoNewsFragment.closeNewsFilter();
		}
		return showVideoNewsFragment;
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		 FragmentTransaction childFt = getChildFragmentManager().beginTransaction();

	        if (mVideoNewsFragment != null&&!childFt.isEmpty()){
	            childFt.remove(mVideoNewsFragment).commit();
	        }

	        if (mLiveFragment != null&&!childFt.isEmpty()){
	            childFt.remove(mLiveFragment).commit();
	        }
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		/**
		 * fragment 销毁时，移除此fragment
		 */
		 Log.i("oyys", "onDestroyView");
        FragmentTransaction childFt = getChildFragmentManager().beginTransaction();

        if (mVideoNewsFragment != null&&!childFt.isEmpty()){
            childFt.remove(mVideoNewsFragment).commit();
        }

        if (mLiveFragment != null&&!childFt.isEmpty()){
            childFt.remove(mLiveFragment).commit();
        }

        Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_right);
		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		if (null != fragment&&!ft.isEmpty()) {
			ft.remove(fragment).commit();
		}

	
		activity=null;
		mLiveFragment=null;
		mVideoNewsFragment=null;
		mHomeFragmentLsn=null;

	}
	public void reloadFragment(){
		mLiveFragment.refreshData();
	}
	
	public void refreshMain(){
		//Log.e("gongmeng", "refresh the main fragment");
		if(isLiveFragment) {
			mLiveFragment.refreshData();
		}
	}
	public void toggleHome() {
		
		if (isLiveFragment) {
			mLiveFragment.requestLiveData();
		} else {
			mVideoNewsFragment.requestFiltetTypeData();
		}
	}

      public void toWorldCup(){
            rootView.findViewById(R.id.world_cup).performClick();
      }


}
