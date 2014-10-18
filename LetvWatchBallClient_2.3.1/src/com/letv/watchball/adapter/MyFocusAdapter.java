package com.letv.watchball.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.cache.LetvCacheMannager;
import com.letv.cache.view.LetvImageView;
import com.letv.http.bean.LetvDataHull;
import com.letv.http.parse.LetvGsonParser;
import com.letv.watchball.LetvApplication;
import com.letv.watchball.R;
import com.letv.watchball.activity.MainActivity;
import com.letv.watchball.async.LetvHttpAsyncTask;
import com.letv.watchball.bean.Base;
import com.letv.watchball.bean.FocusTeamList;
import com.letv.watchball.bean.FocusTeamList.Body.Team;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.utils.LetvUtil;
import com.letv.watchball.utils.UIs;

public class MyFocusAdapter extends BaseExpandableListAdapter {
	private FocusTeamList.Body[] mFocusTeamList;
	private ArrayList<Integer> focusedRecord = new ArrayList<Integer>();//初始关注状态记录
	private ArrayList<Integer> unFocusedRecord = new ArrayList<Integer>();//关注状态修改记录
//	private ArrayList<ArrayList<Team>> childItemList = new ArrayList<ArrayList<Team>>();
	public MyFocusAdapter(FocusTeamList.Body[] mFocusTeamList) {
		this.mFocusTeamList = mFocusTeamList;
//		this.childItemList = childItemList;
	}

	@Override
	public int getGroupCount() {
		return mFocusTeamList.length;
	}

	@Override
	public FocusTeamList.Body getGroup(int groupPosition) {
		return mFocusTeamList[groupPosition];
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if(null != mFocusTeamList[groupPosition].teams){
			return mFocusTeamList[groupPosition].teams.length;
		}
		return 0;
	}

	@Override
	public Team getChild(int groupPosition, int childPosition) {
		return mFocusTeamList[groupPosition].teams[childPosition];
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		ParentViewHolder mViewHolder = new ParentViewHolder();
		if (null == convertView) {
			convertView = LayoutInflater.from(MainActivity.getInstance()).inflate(R.layout.layout_myfocus_item_parent, null);
			mViewHolder.name = (TextView) convertView.findViewById(R.id.parent_name);
			mViewHolder.icon = (LetvImageView) convertView.findViewById(R.id.parent_icon);
			mViewHolder.groupIndicator = (ImageView) convertView.findViewById(R.id.parent_indicator);
			mViewHolder.focus_all = (ImageView) convertView.findViewById(R.id.focus_all);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ParentViewHolder) convertView.getTag();
		}
		if (isExpanded) {
			mViewHolder.groupIndicator.setImageResource(R.drawable.myfocus_item_parent_close);
		} else {
			mViewHolder.groupIndicator.setImageResource(R.drawable.myfocus_item_parent_open);
		}
		final FocusTeamList.Body groupData = getGroup(groupPosition);
		if(null == groupData)
			return null;
		int focus = "1".equals(groupData.focused)?1:0;
		if (focus == 1) {
			mViewHolder.focus_all.setImageResource(R.drawable.myfocus_item_child_add_sel);
		} else {
			//未关注
			mViewHolder.focus_all.setImageResource(R.drawable.myfocus_item_child_add_nor);
		}
		if (focus == 1) {
			// 已关注
//			setFocus(true, mViewHolder.focus_all);
			mViewHolder.focus_all.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					
					if(LetvUtil.hasNet()) {
						
						new RequestFocusMatch(MainActivity.getInstance(), groupData.type, groupData.level, false, new Runnable() {
							
							@Override
							public void run() {
								// 取消关注成功
								groupData.focused = 0 + "";
								for (int i = 0; i < getChildrenCount(groupPosition); i++) {
									Team team = getChild(groupPosition, i);
									team.focused = 0+"";
								}
//								setFocus(false, focusBtn);
								focusedRecord.add(groupPosition*1000);
								notifyDataSetChanged();
							}
						}).start();
					}else {
						
						UIs.showToast(R.string.toast_net_15);
					}
				}
			});
		} else {
			// 未关注
			mViewHolder.focus_all.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					
					if(LetvUtil.hasNet()) {
						
						new RequestFocusMatch(MainActivity.getInstance(), groupData.type, groupData.level, true, new Runnable() {
							
							@Override
							public void run() {
								// 关注成功
								groupData.focused = 1 + "";
								for (int i = 0; i < getChildrenCount(groupPosition); i++) {
									Team team = getChild(groupPosition, i);
									team.focused = 1 +"";
								}
	//							setFocus(false, focusBtn);
								focusedRecord.add(groupPosition*1000);
								notifyDataSetChanged();
							}
						}).start();
				}else {
					
					UIs.showToast(R.string.toast_net_15);
				}
					
				}
			});
		}
		
		mViewHolder.name.setText(groupData.name);
		LetvCacheMannager.getInstance().loadImage(groupData.img_url, mViewHolder.icon);
		return convertView;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		ChildViewHolder mViewHolder = new ChildViewHolder();
		if (null == convertView) {
			convertView = LayoutInflater.from(MainActivity.getInstance()).inflate(R.layout.my_teams_item, null);
			mViewHolder.name = (TextView) convertView.findViewById(R.id.child_name);
			mViewHolder.icon = (LetvImageView) convertView.findViewById(R.id.child_icon);
			mViewHolder.focusSwitcher = (ImageView) convertView.findViewById(R.id.focus_switcher);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ChildViewHolder) convertView.getTag();
		}
        final Team team = getChild(groupPosition, childPosition);
		if(null == team)
			return null;
		mViewHolder.name.setText(team.team);
		mViewHolder.icon.setImageResource(R.drawable.ic_default);
		LetvCacheMannager.getInstance().loadImage(team.img_url, mViewHolder.icon);
        final FocusTeamList.Body groupData = getGroup(groupPosition);
		int focus = "1".equals(team.focused)?1:0;
		if (focus == 1) {
			mViewHolder.focusSwitcher.setImageResource(R.drawable.myfocus_item_child_add_sel);
		} else {
			mViewHolder.focusSwitcher.setImageResource(R.drawable.myfocus_item_child_add_nor);
		}
		if (focus == 1) {
			// 已关注
//			setFocus(true, mViewHolder.focusSwitcher);
			mViewHolder.focusSwitcher.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					
					if(LetvUtil.hasNet()) {
						new RequestUnfocus(MainActivity.getInstance(), team.id, getGroup(groupPosition).level, new Runnable() {
	
							@Override
							public void run() {
								// 取消关注成功
								team.focused = 0 + "";
	                            boolean childAllFocused = isChildAllFocused(groupData);
	                            if (childAllFocused){
	                                groupData.focused = 1 + "";
	                            } else {
	                                groupData.focused = 0 + "";
	                            }
	//							setFocus(false, focusBtn);
								focusedRecord.add(groupPosition*1000+childPosition+1);
								notifyDataSetChanged();
							}
						}).start();
					}else {
						
						UIs.showToast(R.string.toast_net_15);
					}
				}
			});
		} else {
			// 未关注
//			setFocus(false, mViewHolder.focusSwitcher);
			mViewHolder.focusSwitcher.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					
					if(LetvUtil.hasNet()) {
						new RequestFocus(MainActivity.getInstance(), team.id, getGroup(groupPosition).level, new Runnable() {
	
							@Override
							public void run() {
								// 关注成功
								team.focused = 1 + "";
	                            boolean childAllFocused = isChildAllFocused(groupData);
	                            if (childAllFocused){
	                                groupData.focused = 1 + "";
	                            } else {
	                                groupData.focused = 0 + "";
	                            }
	//							setFocus(true, focusBtn);
	                            unFocusedRecord.add(groupPosition*1000+childPosition+1);
								notifyDataSetChanged();
							}
						}).start();
				}else {
					
					UIs.showToast(R.string.toast_net_15);
				}
				}
			});
		}
		return convertView;
	}

	/**
	 * 关注状态是否改变
	 * @return
	 */
	public boolean hasFocusStateChanged() {
		if (focusedRecord.size() > 0 && (focusedRecord.size() == unFocusedRecord.size())) {
			Comparator<Integer> comparator = new Comparator<Integer>() {

				@Override
				public int compare(Integer lhs, Integer rhs) {
					return lhs - rhs;
				}
			};
			Collections.sort(focusedRecord, comparator);
			Collections.sort(unFocusedRecord, comparator);
			for (int i = 0; i < unFocusedRecord.size(); i++) {
				if (!(focusedRecord.get(i) == (unFocusedRecord.get(i)))) {
					return true;
				}
			}
			return false;
		}
		return true;

	}


    private boolean isChildAllFocused(FocusTeamList.Body group){
        boolean focused = false;
        Team[] teams = group.teams;
        for (int i = 0 ; i < teams.length ; i++ ){
            focused = teams[i].focused.equals("1");
            if (focused){
                focused = true;
                continue;
            } else {
                focused = false;
                break;
            }
        }
        return focused;
    }

//	/**
//	 * 设置关注状态
//	 *
//	 * @param isFocus
//	 * @param tv
//	 */
//	public void setFocus(boolean isFocus, ImageView tv) {
//		if (isFocus) {
//			//关注
//			tv.setImageResource(R.drawable.myfocus_item_child_add_sel);
//		} else {
//			//未关注
//			tv.setImageResource(R.drawable.myfocus_item_child_add_nor);
//		}
//	}

	private class ParentViewHolder {
		private TextView name;
		public LetvImageView icon;
		public ImageView groupIndicator;
		public ImageView focus_all;
	}

	private class ChildViewHolder {
		public TextView name;
		public LetvImageView icon;
		public ImageView focusSwitcher;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	/**
	 * http请求  关注/取消关注赛事所有球队
	 * */
	private class RequestFocusMatch extends LetvHttpAsyncTask<Base> {

		private String matchId;
		private String level;
		private boolean focus;
		private Runnable callback;

		public RequestFocusMatch(Context context, String matchId, String level, boolean focus,Runnable callback) {
			super(context, true);
			this.matchId = matchId;
			this.level = level;
			this.focus = focus;
			this.callback = callback;
		}

		@Override
		public LetvDataHull<Base> doInBackground() {
			return LetvHttpApi.requestFocusMatch(0, matchId, level, focus?"1":"0", new LetvGsonParser<Base>(0,Base.class));
//			return LetvHttpApi.requestFocus(teamId, level, new LetvGsonParser<FocusTeamList>(0, FocusTeamList.class));
		}

		@Override
		public void onPostExecute(int updateId, Base result) {
			if (Integer.parseInt(result.header.status) == 1) {
				callback.run();
				showToast(focus, true);
			} else {
				showToast(focus, false);
			}
		}

		@Override
		public void netNull() {
			showToast(focus, false);
		}

		@Override
		public void netErr(int updateId, String errMsg) {
			showToast(focus, false);
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			showToast(focus, false);
		}
		
		private void showToast(boolean focus,boolean success){
			String toastMsg = (focus?"关注":"取消关注")+(success?"成功":"失败");
            Toast.makeText(LetvApplication.getInstance(), toastMsg , Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * http请求  关注
	 * */
	private class RequestFocus extends LetvHttpAsyncTask<Base> {

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
		public LetvDataHull<Base> doInBackground() {
			return LetvHttpApi.requestFocus(teamId, level, new LetvGsonParser<Base>(0, Base.class));
		}

		@Override
		public void onPostExecute(int updateId, Base result) {
			if (Integer.parseInt(result.header.status) == 1) {
				callback.run();
				Toast.makeText(LetvApplication.getInstance(), "关注成功！", Toast.LENGTH_SHORT).show();
			} else {
				
				if(!LetvUtil.hasNet()) {
					
					Toast.makeText(LetvApplication.getInstance(), "关注失败！", Toast.LENGTH_SHORT).show();
				}
			}
		}

		@Override
		public void netNull() {
			
			if(!LetvUtil.hasNet()) {
				
				Toast.makeText(LetvApplication.getInstance(), "关注失败！", Toast.LENGTH_SHORT).show();
			}
			System.err.println("netNull");
		}

		@Override
		public void netErr(int updateId, String errMsg) {
			
			if(!LetvUtil.hasNet()) {
				
				Toast.makeText(LetvApplication.getInstance(), "关注失败！", Toast.LENGTH_SHORT).show();
			}
			System.err.println("netErr()->" + "updateId:" + updateId + "  ,errMsg:" + errMsg);
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			
			if(!LetvUtil.hasNet()) {
				
				Toast.makeText(LetvApplication.getInstance(), "关注失败！", Toast.LENGTH_SHORT).show();
			}
			System.err.println("dataNull()->" + "updateId:" + updateId + "  ,errMsg:" + errMsg);
		}
	}

	/**
	 * http请求  取消关注
	 * */
	private class RequestUnfocus extends LetvHttpAsyncTask<Base> {

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
		public LetvDataHull<Base> doInBackground() {
			return LetvHttpApi.requestUnfocus(teamId, level, new LetvGsonParser<Base>(0, Base.class));
		}

		@Override
		public void onPostExecute(int updateId, Base result) {
			if (Integer.parseInt(result.header.status) == 1) {
				callback.run();
				Toast.makeText(LetvApplication.getInstance(), "取消关注成功！", Toast.LENGTH_SHORT).show();
			} else {
				
				if(!LetvUtil.CheckNetworkState()){
					
					Toast.makeText(LetvApplication.getInstance(), "取消关注失败！", Toast.LENGTH_SHORT).show();
				}
			}
		}

		@Override
		public void netNull() {
			if(!LetvUtil.CheckNetworkState()){
				
				Toast.makeText(LetvApplication.getInstance(), "取消关注失败！", Toast.LENGTH_SHORT).show();
			}
			System.err.println("netNull");
		}

		@Override
		public void netErr(int updateId, String errMsg) {
			if(!LetvUtil.CheckNetworkState()){
				
				Toast.makeText(LetvApplication.getInstance(), "取消关注失败！", Toast.LENGTH_SHORT).show();
			}
			System.err.println("netErr()->" + "updateId:" + updateId + "  ,errMsg:" + errMsg);
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			if(!LetvUtil.CheckNetworkState()){
				
				Toast.makeText(LetvApplication.getInstance(), "取消关注失败！", Toast.LENGTH_SHORT).show();
			}
			System.err.println("dataNull()->" + "updateId:" + updateId + "  ,errMsg:" + errMsg);
		}
	}

}
