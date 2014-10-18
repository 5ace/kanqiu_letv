package com.letv.watchball.adapter;

import java.util.ArrayList;

import com.letv.watchball.LetvApplication;
import za.co.immedia.pinnedheaderlistview.SectionedBaseAdapter;

import android.util.Log;

import com.handmark.pulltorefresh.library.internal.Utils;
import com.letv.watchball.bean.SubscribeList;
import com.letv.watchball.utils.LetvUtil;
import android.content.Context;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.cache.LetvCacheMannager;
import com.letv.http.bean.LetvDataHull;
import com.letv.http.parse.LetvGsonParser;
import com.letv.watchball.R;
import com.letv.watchball.async.LetvHttpAsyncTask;
import com.letv.watchball.bean.Base;
import com.letv.watchball.bean.Game;
import com.letv.watchball.bean.Game.LiveTs;
import com.letv.watchball.bean.PushSubscribeGame;
import com.letv.watchball.db.DBManager;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.manager.HomeFragmentLsn;
import com.letv.watchball.manager.RightFragmentLsn;
import com.letv.watchball.ui.impl.BasePlayActivity;
import com.letv.watchball.utils.LetvSubsribeGameUtil;
import com.letv.watchball.utils.NetWorkTypeUtils;
import com.letv.watchball.view.RoundImageView;

public class LiveAdapter extends SectionedBaseAdapter {

    //	private ViewChildHolder mViewChildHodler;
//	private ViewGroupHolder mViewGroupHolder;
	// private String watchDate;
	private HomeFragmentLsn mHomeFragmentLsn;
	private RightFragmentLsn mRightFragmentLsn;

	public ArrayList<String> listParent = new ArrayList<String>();
	public ArrayList<ArrayList<Game>> listChild = new ArrayList<ArrayList<Game>>();
	private Context context;
      public void setCanDelete(boolean b) {
      }


      public enum MODE{
		HOME,
		TEAMS,
		SUBCRIBE,
		SCHEDULE,
	}
	private MODE mode;
	public void setMode(MODE mode){
		this.mode = mode;
	}
	public LiveAdapter(Context context,boolean canDelete) {
		this.context = context;
	}
	public void clear() {
		if(null != listParent&&null != listChild){
                  listParent.clear();
                  listChild.clear();
            }

            Log.d("TAG", "listParent" + listParent.size() + "-----listChild" + listChild.size());

	}

	public void setHomeFragmentLsn(HomeFragmentLsn mHomeFragmentLsn) {
		this.mHomeFragmentLsn = mHomeFragmentLsn;
	}
	
	public void setRightFragmentLsn(RightFragmentLsn mRightFragmentLsn){
		this.mRightFragmentLsn = mRightFragmentLsn;
	}
	@Override
	public int getSectionCount() {
		return listParent.size();
	}

	@Override
	public int getCountForSection(int sectioon) {
		return listChild.get(sectioon).size();
	}
	@Override
	public long getItemId(int section, int position) {
		return position;
	}

    public String getGroup(int groupPosition) {
        if(listParent.size() <= groupPosition){
            return null;
        }
		return listParent.get(groupPosition);
	}

	@Override
	public Game getItem(int setion, int position) {
		if(listChild.size() <= setion || null == listChild.get(setion) || listChild.get(setion).size() <= position){
			return null;
		}
		return listChild.get(setion).get(position);
	}

	@Override
	public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
		ViewGroupHolder mViewGroupHolder = null;
		String dateStr = getGroup(section);
		if(null == dateStr){
			return null;
		}
		if (convertView == null) {
			mViewGroupHolder = new ViewGroupHolder();
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.game_group_item, null);
			mViewGroupHolder.date = (TextView) convertView.findViewById(R.id.watch_date);
			convertView.setTag(mViewGroupHolder);
		} else {
			mViewGroupHolder = (ViewGroupHolder) convertView.getTag();
		}
			mViewGroupHolder.date.setText(dateStr);
		//如果只有一个group，设置grou不可见
//		if(getGroupCount() == 1){
//			convertView.setVisibility(View.GONE);
//		}
		return convertView;
	}
	private int homeLeftMargin,guestRightMargin;
	@Override
	public View getItemView(final int section, final int position, View convertView, ViewGroup parent) {
		ViewChildHolder mViewChildHodler = null;
		final Game game = getItem(section, position);
		if(null == game){
			return null;
		}
		final String date = getGroup(section);
		if (convertView == null) {
			mViewChildHodler = new ViewChildHolder();
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.game_child_item, null);
			mViewChildHodler.content_frame = (FrameLayout) convertView.findViewById(R.id.content_frame);
			mViewChildHodler.homeIcon = (RoundImageView) convertView.findViewById(R.id.team_home_icon);
			mViewChildHodler.guestIcon = (RoundImageView) convertView.findViewById(R.id.team_guest_icon);
			mViewChildHodler.home = (TextView) convertView.findViewById(R.id.team_home);
			mViewChildHodler.guest = (TextView) convertView.findViewById(R.id.team_guest);
			mViewChildHodler.title = (TextView) convertView.findViewById(R.id.title);
			mViewChildHodler.watchStatus = (ImageView) convertView.findViewById(R.id.status);
			mViewChildHodler.score = (TextView) convertView.findViewById(R.id.score);
			mViewChildHodler.startTime = (TextView) convertView.findViewById(R.id.start_time);
			
			convertView.setTag(mViewChildHodler);
		} else {
			mViewChildHodler = (ViewChildHolder) convertView.getTag();
		}
		// child item style
		mViewChildHodler.homeIcon.setImageResource(R.drawable.ic_default);
		mViewChildHodler.guestIcon.setImageResource(R.drawable.ic_default);
		LetvCacheMannager.getInstance().loadImage(game.homeImg, mViewChildHodler.homeIcon);
		LetvCacheMannager.getInstance().loadImage(game.guestImg, mViewChildHodler.guestIcon);
   
  
		if(game.getVs().equals("")){
			mViewChildHodler.home.setText(game.level);
			mViewChildHodler.guest.setText(game.level);
		}else{
			mViewChildHodler.home.setText(game.home);
			mViewChildHodler.guest.setText(game.guest);
		}
		/**
		 * 动态设置home guest的位置
		 */
		RelativeLayout.LayoutParams homeParams = (RelativeLayout.LayoutParams) mViewChildHodler.home.getLayoutParams();
		RelativeLayout.LayoutParams guestParams = (RelativeLayout.LayoutParams) mViewChildHodler.guest.getLayoutParams();

		if(homeLeftMargin == 0){
			homeLeftMargin = homeParams.leftMargin;
		}
		if(guestRightMargin == 0){
			guestRightMargin = guestParams.rightMargin;
		}
		mViewChildHodler.home.measure(0, 0);
		mViewChildHodler.guest.measure(0, 0);

		homeParams.leftMargin = homeLeftMargin - mViewChildHodler.home.getMeasuredWidth() / 2;
		mViewChildHodler.home.requestLayout();
		guestParams.rightMargin = guestRightMargin - mViewChildHodler.guest.getMeasuredWidth() / 2;
		mViewChildHodler.guest.requestLayout();
		
		if(mode == MODE.TEAMS){
			mViewChildHodler.title.setText(game.level+game.matchName + "-" + game.playDay);
		}else{
			mViewChildHodler.title.setText(game.level + "-" + game.matchName);
		}
		if(game.status == 0){
			String startTime = game.playTime;
			mViewChildHodler.startTime.setText(startTime);
			mViewChildHodler.score.setVisibility(View.GONE);
			mViewChildHodler.startTime.setVisibility(View.VISIBLE);
		}else{
			String scoreStr = game.homeScore + " - " + game.guestScore;
			if(game.status == 0 || (TextUtils.isEmpty(game.homeScore)&&TextUtils.isEmpty(game.guestScore))){
				scoreStr = "---";
			}
			mViewChildHodler.score.setText(scoreStr);
			mViewChildHodler.score.setVisibility(View.VISIBLE);
			mViewChildHodler.startTime.setVisibility(View.GONE);
		}
		switch (game.status) {
		case 0:
			/**
			 * 未开始
			 */
			switch (mode) {
			case SCHEDULE:
			case TEAMS:
				mViewChildHodler.watchStatus.setImageResource(R.drawable.team_manager_not_begin);
				mViewChildHodler.watchStatus.setClickable(false);
				mViewChildHodler.content_frame.setClickable(false);
				break;
			case HOME:
			case SUBCRIBE:
				//判断是否当前比赛是否被预约
				mViewChildHodler.watchStatus.setClickable(true);
				game.isGameSubscribed = DBManager.getInstance().getSubscribeGameTrace().hasSubscribeGameTrace(game.id);
				if (!game.isGameSubscribed) {
					// 未预约
					
					if(game.platform!=null&&!game.platform.contains("Mobile")){
						
						mViewChildHodler.watchStatus.setImageResource(R.drawable.live_subscribe_btn_enabled);
					}else{
						mViewChildHodler.watchStatus.setImageResource(R.drawable.live_subcribe_btn);
					}
				
					// viewHodler.watchStatus.setText("未预约");
					final ViewChildHolder mTViewChildHodler = mViewChildHodler;
					mViewChildHodler.watchStatus.setClickable(true);
					mViewChildHodler.watchStatus.setEnabled(true);
					mViewChildHodler.watchStatus.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if(game.platform!=null&&!game.platform.contains("Mobile")){
								
								Toast.makeText(getContext(), "因版权限制，请使用乐视超级电视、盒子观看直播.", Toast.LENGTH_SHORT).show();
								return;
							}
							
							
							
							// 直播预约
							new RequestSubscribe(getContext(), game, date,new Runnable() {

								@Override
								public void run() {
									mTViewChildHodler.watchStatus.setImageResource(R.drawable.live_unsubcribe_btn);
									// viewHodler.watchStatus.setText("已预约");
//									game.isGameSubscribed = true;
									
									notifyDataSetChanged();
									
									if(null != mHomeFragmentLsn){
										mHomeFragmentLsn.addSubscribe(game,date);
									}
									if(null != mRightFragmentLsn){
										mRightFragmentLsn.updateSuscribeStatus();
									}
									for (ExpandlableItem uExpandlableItem : unsubscribeList) {
										if(uExpandlableItem.groupPosition == section && uExpandlableItem.childPosition == position){
											unsubscribeList.remove(uExpandlableItem);
											break;
										}
									}
								}
							}).start();
						}
					});
				} else {
					// 已预约
					mViewChildHodler.watchStatus.setImageResource(R.drawable.live_unsubcribe_btn);
					final ViewChildHolder mTViewChildHodler = mViewChildHodler;
					mViewChildHodler.watchStatus.setClickable(true);
					mViewChildHodler.watchStatus.setEnabled(true);
					mViewChildHodler.watchStatus.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// 取消预约
							new RequestUnsubscribe(getContext(), game, new Runnable() {

								@Override
								public void run() {
									mTViewChildHodler.watchStatus.setImageResource(R.drawable.live_subcribe_btn);
									// viewHodler.watchStatus.setText("未预约");
//									game.isGameSubscribed = false;
//                                    if (canDelete){
//                                        ArrayList<Game> games = listChild.get(section);
//                                        games.remove(position);
//                                        if(games.isEmpty()) {
//                                            listChild.remove(section);
//                                            listParent.remove(section);
//                                        }
//
//                                    }
									notifyDataSetChanged();
									
									if(null != mHomeFragmentLsn){
										mHomeFragmentLsn.removeSubscribe(game.id);
									}
									if(null != mRightFragmentLsn){
										mRightFragmentLsn.updateSuscribeStatus();
									}
									ExpandlableItem eItem = new ExpandlableItem();
									eItem.groupPosition = section;
									eItem.childPosition = position;
									unsubscribeList.add(eItem);
								}
							}).start();
						}
					});
				}
				
			}
			break;
		case 1:
			/**
			 * 直播中
			 */
			
			//TODO 播放直播
			if(game.platform!=null&&!game.platform.contains("Mobile")){
		
				mViewChildHodler.watchStatus.setImageResource(R.drawable.live_no_btn_enabled);
			}else{
				mViewChildHodler.watchStatus.setImageResource(R.drawable.live_on_btn);
			}
				mViewChildHodler.watchStatus.setClickable(true);
				mViewChildHodler.watchStatus.setEnabled(true);
				
				mViewChildHodler.watchStatus.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(game.platform!=null&&!game.platform.contains("Mobile")){
							Toast.makeText(getContext(), "因版权限制，请使用乐视超级电视、盒子观看直播.", Toast.LENGTH_SHORT).show();
							return;
						}

//                                    BasePlayActivity.launchLives(getContext(),game);
//                                          return;
                                          LetvApplication.getInstance().saveLiveGame(game);
							if(game.live_350!=null&&!"".equals(game.live_350)){
								LiveTs liveTs = game.live_350;
								BasePlayActivity.launchLives(getContext(), liveTs.code, liveTs.streamId, liveTs.liveUrl, game.getPid(), game.getVid(),game);
								return;
							}
							if(game.live_800!=null&&!"".equals(game.live_800)){
								LiveTs liveTs = game.live_800;
								BasePlayActivity.launchLives(getContext(), liveTs.code, liveTs.streamId, liveTs.liveUrl, game.getPid(), game.getVid(),game);
								return;
							}
							if(game.live_1300!=null&&!"".equals(game.live_1300)){
								LiveTs liveTs = game.live_1300;
								BasePlayActivity.launchLives(getContext(), liveTs.code, liveTs.streamId, liveTs.liveUrl, game.getPid(), game.getVid(),game);
								return;
							}
							
							Toast.makeText(context, "不好意思，没直播地址", Toast.LENGTH_SHORT).show();
//						BasePlayActivity.launchLives(getContext(), game.live_350.code, game.live_350.streamId, game.live_350.liveUrl, game.pid, game.getVid(),game);
//						LetvPlayFunction.playLiveVideo(getContext(), game.home, game.guest, game.live_url_350, game.live_url_350, false);
					}
				});
			
			break;
		case 2:
			/**
			 * 已结束
			 */
			if(!TextUtils.isEmpty(game.vid)&&TextUtils.isEmpty(game.recording_id)){
                        mViewChildHodler.watchStatus.setImageResource(R.drawable.live_collection_btn);
                        mViewChildHodler.watchStatus.setEnabled(true);
                        mViewChildHodler.watchStatus.setClickable(true);
                        mViewChildHodler.content_frame.setEnabled(true);
                        mViewChildHodler.watchStatus.setOnClickListener(new OnClickListener() {

                              @Override
                              public void onClick(View v) {
//						LiveTs liveTs = game.live_350;
//						BasePlayActivity.launchLives(getContext(), liveTs.code, liveTs.streamId, liveTs.liveUrl, game.getPid(), game.getVid());
                                    BasePlayActivity.launch(getContext(), game.getPid(), game.getVid());
                              }
                        });
			}else if (!TextUtils.isEmpty(game.recording_id)&&TextUtils.isEmpty(game.vid)){
                        mViewChildHodler.watchStatus.setImageResource(R.drawable.live_recording_btn);
                        mViewChildHodler.watchStatus.setEnabled(true);
                        mViewChildHodler.watchStatus.setClickable(true);
                        mViewChildHodler.content_frame.setEnabled(true);
                        mViewChildHodler.watchStatus.setOnClickListener(new OnClickListener() {

                              @Override
                              public void onClick(View v) {
//						LiveTs liveTs = game.live_350;
//						BasePlayActivity.launchLives(getContext(), liveTs.code, liveTs.streamId, liveTs.liveUrl, game.getPid(), game.getVid());
                                    BasePlayActivity.launch(getContext(), game.getPid(), game.getRecording_id());
                              }
                        });
				
			} else if (!TextUtils.isEmpty(game.vid)&&!TextUtils.isEmpty(game.recording_id)) {
                        String netType = LetvUtil.getNetType(getContext());
                        if ("3G".equals(netType)){
                              mViewChildHodler.watchStatus.setImageResource(R.drawable.live_collection_btn);
                              mViewChildHodler.watchStatus.setEnabled(true);
                              mViewChildHodler.watchStatus.setClickable(true);
                              mViewChildHodler.content_frame.setEnabled(true);
                              mViewChildHodler.watchStatus.setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
//						LiveTs liveTs = game.live_350;
//						BasePlayActivity.launchLives(getContext(), liveTs.code, liveTs.streamId, liveTs.liveUrl, game.getPid(), game.getVid());
                                          BasePlayActivity.launch(getContext(), game.getPid(), game.getVid());
                                    }
                              });
                        }else if ("wifi".equals(netType)){
                              mViewChildHodler.watchStatus.setImageResource(R.drawable.live_recording_btn);
                              mViewChildHodler.watchStatus.setEnabled(true);
                              mViewChildHodler.watchStatus.setClickable(true);
                              mViewChildHodler.content_frame.setEnabled(true);
                              mViewChildHodler.watchStatus.setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
//						LiveTs liveTs = game.live_350;
//						BasePlayActivity.launchLives(getContext(), liveTs.code, liveTs.streamId, liveTs.liveUrl, game.getPid(), game.getVid());
                                          BasePlayActivity.launch(getContext(), game.getPid(), game.getRecording_id());
                                    }
                              });
                        } else {
                              mViewChildHodler.watchStatus.setImageResource(R.drawable.live_collection_no);
                              mViewChildHodler.watchStatus.setEnabled(false);
                              mViewChildHodler.content_frame.setClickable(false);
                              mViewChildHodler.content_frame.setEnabled(false);
                        }

                  } else {
                        mViewChildHodler.watchStatus.setImageResource(R.drawable.live_collection_no);
                        mViewChildHodler.watchStatus.setEnabled(false);
                        mViewChildHodler.content_frame.setClickable(false);
                        mViewChildHodler.content_frame.setEnabled(false);
                  }
			break;
			
		}
		//设置convertview的点击事件
		final View watchStatus = mViewChildHodler.watchStatus;
		if(watchStatus.isClickable()){
			convertView.setBackgroundResource(R.drawable.home_game_child_bg);
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					watchStatus.performClick();
				}
			});
		}

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	private Context getContext() {
		return context;
	}

	public class ViewChildHolder {
		private RoundImageView homeIcon, guestIcon;
		private TextView home, guest, title, score,startTime;
		private ImageView watchStatus;
		private FrameLayout content_frame;
	}

	public class ViewGroupHolder {
		private TextView date;
	}
	
	public class ExpandlableItem{
		public int groupPosition;
		public int childPosition;
	}
	/**
	 * 取消预约列表
	 */
	public ArrayList<ExpandlableItem> unsubscribeList = new ArrayList<LiveAdapter.ExpandlableItem>();
	public ArrayList<ExpandlableItem> getUnsubscribes(){
		return unsubscribeList;
	}

	/**
	 * http比赛预约
	 * */
	private class RequestSubscribe extends LetvHttpAsyncTask<Base> {

		private Game mLiveInfo;
		private Runnable callback;
		private String date;
		public RequestSubscribe(Context context, Game mLiveInfo, String date,Runnable callback) {
			super(context, true);
			this.mLiveInfo = mLiveInfo;
			this.date = date;
			this.callback = callback;

		}

		@Override
		public LetvDataHull<Base> doInBackground() {
			return LetvHttpApi.requestSubscribeMatch(mLiveInfo.id, new LetvGsonParser<Base>(0, Base.class));
		}

		@Override
		public void onPostExecute(int updateId, Base result) {
			int status = Integer.parseInt(result.header.status);
			if (status == 1) {
				// 预约成功后，不自动刷新列表
				// if(null != mHomeFragmentLsn){
				// mHomeFragmentLsn.reloadMySubscribe();
				// }
				PushSubscribeGame mPushSubscribeGame = new PushSubscribeGame();
				mPushSubscribeGame.id = mLiveInfo.id;
				mPushSubscribeGame.home = mLiveInfo.home;
				mPushSubscribeGame.guest = mLiveInfo.guest;
				mPushSubscribeGame.level = mLiveInfo.level;
				mPushSubscribeGame.playDate = mLiveInfo.playDate;
				mPushSubscribeGame.playTime = mLiveInfo.playTime;
				mPushSubscribeGame.status = mLiveInfo.status;
				LetvSubsribeGameUtil.SubscribeGameProgram(getContext(), mPushSubscribeGame);
				Toast.makeText(getContext(), "预约成功！", Toast.LENGTH_SHORT).show();
				callback.run();
			} else {
				
				NetworkInfo networkInfo = NetWorkTypeUtils.getAvailableNetWorkInfo();
				if(networkInfo== null) {
					
					Toast.makeText(getContext(), "预约失败！ " + result.header.msg, Toast.LENGTH_SHORT).show();
				}
			}
		}

		@Override
		public void netNull() {
			
			NetworkInfo networkInfo = NetWorkTypeUtils.getAvailableNetWorkInfo();
			if(networkInfo== null) {
				
				Toast.makeText(getContext(), "预约失败！ ", Toast.LENGTH_SHORT).show();
			}
			System.err.println("netNull");
		}

		@Override
		public void netErr(int updateId, String errMsg) {

			NetworkInfo networkInfo = NetWorkTypeUtils.getAvailableNetWorkInfo();
			if(networkInfo== null) {
				
				Toast.makeText(getContext(), "预约失败！ ", Toast.LENGTH_SHORT).show();
			}
			System.err.println("netErr()->" + "updateId:" + updateId + "  ,errMsg:" + errMsg);
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			Toast.makeText(getContext(), "预约失败！ ", Toast.LENGTH_SHORT).show();
			System.err.println("dataNull()->" + "updateId:" + updateId + "  ,errMsg:" + errMsg);
		}
	}

	/**
	 * http比赛预约
	 * */
	private class RequestUnsubscribe extends LetvHttpAsyncTask<Base> {

		private Game mLiveInfo;
		private Runnable callback;

		public RequestUnsubscribe(Context context, Game mLiveInfo, Runnable callback) {
			super(context, true);
			this.mLiveInfo = mLiveInfo;
			this.callback = callback;

		}

		@Override
		public LetvDataHull<Base> doInBackground() {
			return LetvHttpApi.requestUnsubscribeMatch(mLiveInfo.id, new LetvGsonParser<Base>(0, Base.class));
		}

		@Override
		public void onPostExecute(int updateId, Base result) {
			int status = Integer.parseInt(result.header.status);
			if (status == 1) {
				// 预约成功后，不自动刷新列表
				// if(null != mHomeFragmentLsn){
				// mHomeFragmentLsn.reloadMySubscribe();
				// }
				PushSubscribeGame mPushSubscribeGame = new PushSubscribeGame();
				mPushSubscribeGame.id = mLiveInfo.id;
				mPushSubscribeGame.home = mLiveInfo.home;
				mPushSubscribeGame.guest = mLiveInfo.guest;
				mPushSubscribeGame.level = mLiveInfo.level;
				mPushSubscribeGame.playDate = mLiveInfo.playDate;
				mPushSubscribeGame.playTime = mLiveInfo.playTime;
				mPushSubscribeGame.status = mLiveInfo.status;
				LetvSubsribeGameUtil.cancelSubscribeGameProgram(getContext(), mPushSubscribeGame.id);
				Toast.makeText(getContext(), "取消预约成功！", Toast.LENGTH_SHORT).show();
				callback.run();
			} else {
				
				NetworkInfo networkInfo = NetWorkTypeUtils.getAvailableNetWorkInfo();
				if(networkInfo== null) {
					
					Toast.makeText(getContext(), "取消预约失败！ " + result.header.msg, Toast.LENGTH_SHORT).show();
				}
			}
		}

		@Override
		public void netNull() {
			
			NetworkInfo networkInfo = NetWorkTypeUtils.getAvailableNetWorkInfo();
			if(networkInfo== null) {
				
				Toast.makeText(getContext(), "取消预约失败！ ", Toast.LENGTH_SHORT).show();
			}
			System.err.println("netNull");
		}

		@Override
		public void netErr(int updateId, String errMsg) {
			
			NetworkInfo networkInfo = NetWorkTypeUtils.getAvailableNetWorkInfo();
			if(networkInfo== null) {
				
				Toast.makeText(getContext(), "取消预约失败！ ", Toast.LENGTH_SHORT).show();
			}
			System.err.println("netErr()->" + "updateId:" + updateId + "  ,errMsg:" + errMsg);
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			Toast.makeText(getContext(), "取消预约失败！ ", Toast.LENGTH_SHORT).show();
			System.err.println("dataNull()->" + "updateId:" + updateId + "  ,errMsg:" + errMsg);
		}
	}
	
}
