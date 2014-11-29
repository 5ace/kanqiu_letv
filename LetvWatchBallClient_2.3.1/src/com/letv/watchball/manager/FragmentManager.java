package com.letv.watchball.manager;

import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.letv.android.slidingmenu.lib.LoadListener;
import com.letv.android.slidingmenu.lib.SlidingMenu;
import com.letv.android.slidingmenu.lib.app.SlidingFragmentActivity;
import com.letv.watchball.R;
import com.letv.watchball.bean.Game;
import com.letv.watchball.bean.MatchList.Body.Match;
import com.letv.watchball.bean.MatchList.Body.OriginalColumn;
import com.letv.watchball.fragment.OriginalFragment;
import com.letv.watchball.fragment.ScheduleFragment;
import com.letv.watchball.fragment.SlidingMenuFragmentHome;
import com.letv.watchball.fragment.SlidingMenuFragmentLeft;
import com.letv.watchball.fragment.SlidingMenuFragmentRight;
import com.letv.watchball.utils.LetvUtil;

public class FragmentManager implements  LeftFragmentLsn, HomeFragmentLsn, RightFragmentLsn {

	/**
	 * sliding menu左右中间Fragment与左右Fragment之间的offset
	 */
	private int leftOffset, rightOffset;
	private SlidingMenuFragmentLeft leftFragment;
	private SlidingMenuFragmentHome mainFragment;
	private SlidingMenuFragmentRight rightFragment;

	/**
	 * 赛事新闻fragment
	 */
	private ScheduleFragment mEventsVideoNewsFragment;
	/**
	 * 原创节目fragment
	 */
	private OriginalFragment mOriginalFragment;
	private SlidingFragmentActivity mContext;

	public void onCreate(SlidingFragmentActivity context) {
		this.mContext = context;
		if(context==null){
			return;
		}
		
		init();
	}

      private Handler mHandler = new Handler();

	public void init() {
				try {
				leftOffset = (int) (LetvUtil.getScreenWidth(mContext) * 0.35f);
				rightOffset = (int) (LetvUtil.getScreenWidth(mContext) * 0.18f);
				mContext.getSlidingMenu().setMode(SlidingMenu.LEFT_RIGHT);
				mContext.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
				// set the Behind View
				// 左边的Fragment
				
				
				LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);  
				View parent1 = (View) inflater.inflate(R.layout.menu_frame, null);  
				View parent2 = (View) inflater.inflate(R.layout.main_frame, null);  
				View parent3 = (View) inflater.inflate(R.layout.menu_frame_two, null); 
				
//				FrameLayout home=(FrameLayout)mContext.findViewById(R.id.home);
//				FrameLayout main_frame_live=(FrameLayout) mContext.findViewById(R.id.main_frame_live);
//				FrameLayout main_frame_event=(FrameLayout) mContext.findViewById(R.id.main_frame_event);
//				
//				home.removeAllViews();
//				main_frame_live.removeAllViews();
//				main_frame_event.removeAllViews();
				
				
				mContext.setBehindContentView(parent1);
				if(leftFragment==null){
				leftFragment = new SlidingMenuFragmentLeft(this);
				mContext.getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, leftFragment).commit();
				}
				// 中间的Fragment
				mContext.setContentView(parent2);
				if(mainFragment==null){
				mainFragment = new SlidingMenuFragmentHome(mContext, this);
				mContext.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_live, mainFragment).commitAllowingStateLoss();
				}
				if(mEventsVideoNewsFragment==null){
				mEventsVideoNewsFragment = new ScheduleFragment();
				mContext.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_event, mEventsVideoNewsFragment).commitAllowingStateLoss();
				}
				
				if(mOriginalFragment==null){
				mOriginalFragment = new OriginalFragment();
				mContext.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_original, mOriginalFragment).commitAllowingStateLoss();
				}
				
				
		
				// 右边的Fragment
				rightFragment = new SlidingMenuFragmentRight(this);
				mContext.getSlidingMenu().setSecondaryMenu(parent3);
				mContext.getSlidingMenu().setSecondaryShadowDrawable(R.drawable.shadowright);
				mContext.getSlidingMenu().setShadowWidth(500);
				mContext.getSupportFragmentManager().beginTransaction().addToBackStack(null);
				mContext.getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_two, rightFragment).commitAllowingStateLoss();
				
				// customize the SlidingMenu
				SlidingMenu sm = mContext.getSlidingMenu();
				sm.setShadowWidthRes(R.dimen.shadow_width);
				sm.setShadowDrawable(R.drawable.shadow);
				// sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
				sm.setFadeDegree(0.35f);
				sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		            sm.setLoadListener(new LoadListener() {
		                  @Override
		                  public void loadData() {
		
		
		                        mHandler.post(new Runnable() {
		                              @Override
		                              public void run() {
		                                    loadRightFragmentData();
		                              }
		                        });
		                  }
		            });
				//设置不可滑动
		//		sm.setSlidingEnabled(false);
		
				// 设置左右两个Fragment与中间Fragment的重叠偏移量
				mContext.getSlidingMenu().setLeftBehindOffset(leftOffset);
				mContext.getSlidingMenu().setRightBehindOffset(rightOffset);
				
		} catch (Exception e) {
			// TODO: handle exception
			onDestroy();
		}
	}

	/**
	 * 热门直播
	 */
	private void toggleHome(Object obj) {
//		if(null == mainFragment){
//			mainFragment = new SlidingMenuFragmentHome(mContext, this);
//			mContext.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_live, mainFragment).commit();
//		}
//		
//		mContext.findViewById(R.id.main_frame_live).setVisibility(View.VISIBLE);
//		mContext.findViewById(R.id.main_frame_event).setVisibility(View.GONE);
//		mContext.findViewById(R.id.main_frame_original).setVisibility(View.GONE);
		if(mainFragment!=null&&mainFragment.getView()!=null&&mEventsVideoNewsFragment!=null&&mEventsVideoNewsFragment.getView()!=null&&mOriginalFragment!=null&&mOriginalFragment.getView()!=null){
		((ViewGroup) mainFragment.getView().getParent()).setVisibility(View.VISIBLE);
		((ViewGroup) mEventsVideoNewsFragment.getView().getParent()).setVisibility(View.GONE);
		((ViewGroup) mOriginalFragment.getView().getParent()).setVisibility(View.GONE);
		
		mainFragment.toggleHome();
            if (null != obj){
                  mainFragment.toWorldCup();
            }
		}

	}

	/**
	 * 赛事
	 */
	private void toggleEvent(Match match) {
//		if(null == mEventsVideoNewsFragment){
//			mEventsVideoNewsFragment = new ScheduleFragment();
//			mContext.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_event, mEventsVideoNewsFragment).commit();
//		}
//		mContext.getSupportFragmentManager().findFragmentById(R.id.main_frame_live).getView().setVisibility(View.GONE);
//		mContext.getSupportFragmentManager().findFragmentById(R.id.main_frame_event).getView().setVisibility(View.VISIBLE);
//		mContext.getSupportFragmentManager().findFragmentById(R.id.main_frame_original).getView().setVisibility(View.GONE);
		
		if(mainFragment!=null&&mainFragment.getView()!=null&&mEventsVideoNewsFragment!=null&&mEventsVideoNewsFragment.getView()!=null&&mOriginalFragment!=null&&mOriginalFragment.getView()!=null){
			((ViewGroup) mainFragment.getView().getParent()).setVisibility(View.GONE);
			((ViewGroup) mEventsVideoNewsFragment.getView().getParent()).setVisibility(View.VISIBLE);
			((ViewGroup) mOriginalFragment.getView().getParent()).setVisibility(View.GONE);
			mEventsVideoNewsFragment.setMatch(match);
		}


	}

	/**
	 * 原创节目
	 */
	private void toggleOriginal(OriginalColumn originalColumn) {
//		if(null == mOriginalFragment){
//			mOriginalFragment = new OriginalFragment();
//			mContext.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_original, mOriginalFragment).commit();
//		}
//		mContext.getSupportFragmentManager().findFragmentById(R.id.main_frame_live).getView().setVisibility(View.GONE);
//		mContext.getSupportFragmentManager().findFragmentById(R.id.main_frame_event).getView().setVisibility(View.GONE);
//		mContext.getSupportFragmentManager().findFragmentById(R.id.main_frame_original).getView().setVisibility(View.VISIBLE);
		
		if(mainFragment!=null&&mainFragment.getView()!=null&&mEventsVideoNewsFragment!=null&&mEventsVideoNewsFragment.getView()!=null&&mOriginalFragment!=null&&mOriginalFragment.getView()!=null){
		((ViewGroup) mainFragment.getView().getParent()).setVisibility(View.GONE);
		((ViewGroup) mEventsVideoNewsFragment.getView().getParent()).setVisibility(View.GONE);
		((ViewGroup) mOriginalFragment.getView().getParent()).setVisibility(View.VISIBLE);

		mOriginalFragment.setOriginalColumn(originalColumn);
		
		}
	}
	
	/**
	 * 执行新闻筛选的关闭操作
	 * @return 视频新闻筛选 list 是否已关闭
	 */
	public boolean closeNewsFilter(){
		return mainFragment.closeNewsFilter();
	}

	@Override
	public void invoke(int action, Object obj) {
		// 左侧Fragment中item点击事件处理
		mContext.getSlidingMenu().toggle();
		switch (action) {
		case ACTION_LIVE:
			toggleHome(obj);
			break;
		case ACTION_EVENTS:
			// 回调切换
			toggleEvent((Match) obj);
			break;
		case ACTION_ORIGINAL:
			// 原创节目
			toggleOriginal((OriginalColumn) obj);
		default:
			break;
		}

	}



	public void onDestroy() {
        android.support.v4.app.FragmentManager supportFragmentManager = mContext.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        if(leftFragment!=null){
        	fragmentTransaction.remove(leftFragment);
        	leftFragment=null;
        }
		 if(rightFragment!=null){
			 
			 fragmentTransaction.remove(rightFragment);
			 rightFragment=null;
		 }
		 if(mainFragment!=null){
			 fragmentTransaction.remove(mainFragment);
			 mainFragment=null;
		 }
		 if(mEventsVideoNewsFragment!=null){
			 fragmentTransaction.remove(mEventsVideoNewsFragment);
			 mEventsVideoNewsFragment=null;
		 }
		 if(mOriginalFragment!=null){
			 fragmentTransaction.remove(mOriginalFragment);
			 mOriginalFragment=null;
		 }
	     fragmentTransaction.commit();
   

    }

	@Override
	public void addSubscribe(Game game, String date) {
		rightFragment.addSubscribe(game, date);
	}

	@Override
	public void removeSubscribe(String id) {
		rightFragment.removeSubscribe(id);
	}

	@Override
	public void toggleRight() {
		rightFragment.reflashUI();
	}
	@Override
	public void loadRightFragmentData() {
		rightFragment.loadloadMySubscribetData();
            rightFragment.loadMyTeamData();
      }
	@Override
	public void updateSuscribeStatus() {
		mainFragment.updateSuscribeStatus();
	}
	@Override
	public void resetMainFragment(){
		mainFragment.reloadFragment();
	}
	@Override
    public void toggleHome() {
    	mainFragment.refreshMain();
    }
	@Override
	public void reloadAllDatas() {
		leftFragment.reloadMatchList();
		rightFragment.loadRightFragmentData();
	}
}
