package com.letv.watchball.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.letv.cache.LetvCacheMannager;
import com.letv.cache.view.LetvImageView;
import com.letv.watchball.R;
import com.letv.watchball.fragment.SlidingMenuFragmentLeft.LeftFragmentItem;

public class LeftFragmentAdapter extends BaseExpandableListAdapter {

//		private ViewHolderChild viewHodlerChild;
//		private ViewHolderGroup viewHolderGroup;
		private int clickedGroupPos,clickChildPos = 0;
		public ArrayList<String> groupItemList = new ArrayList<String>();
		public ArrayList<ArrayList<LeftFragmentItem>> childItemList = new ArrayList<ArrayList<LeftFragmentItem>>();
		private Context mContext;

		public LeftFragmentAdapter(Context context) {
			this.mContext = context;
		}
		
		public void onItemClick(int groupPosition,int childPositon) {
			this.clickedGroupPos = groupPosition;
			this.clickChildPos = childPositon;
		}

		@Override
		public int getGroupCount() {
			return groupItemList.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return childItemList.get(groupPosition).size();
		}

		@Override
		public String getGroup(int groupPosition) {
			return groupItemList.get(groupPosition);
		}

		@Override
		public LeftFragmentItem getChild(int groupPosition, int childPosition) {
			return childItemList.get(groupPosition).get(childPosition);
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
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			ViewHolderGroup viewHolderGroup = null;
			String mItem = groupItemList.get(groupPosition);
			if(convertView == null){
				viewHolderGroup = new ViewHolderGroup();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.left_item_group, null);
				viewHolderGroup.titleParent = (TextView) convertView.findViewById(R.id.parent_title);
//				viewHolderGroup.left_item_parent = (LinearLayout) convertView.findViewById(R.id.left_item_parent);
				viewHolderGroup.left_item_parent_divider = (View) convertView.findViewById(R.id.left_item_parent_divider);
				convertView.setTag(viewHolderGroup);
			}else{
				viewHolderGroup = (ViewHolderGroup) convertView.getTag();
			}
			
//			AbsListView.LayoutParams params0 = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, 1);
//			AbsListView.LayoutParams params1 = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//			if(TextUtils.isEmpty(mItem)){
//				Log.i("king"," TextUtils.isEmpty(mItem) is true!");
////				convertView.setLayoutParams(params0);
////				viewHolderGroup.left_item_parent.setBackgroundColor(Color.TRANSPARENT);
////				viewHolderGroup.left_item_parent.setBackgroundDrawable(null);
//				viewHolderGroup.left_item_parent_divider.setVisibility(View.GONE);
//			}else{
//				convertView.setLayoutParams(params1);
//				viewHolderGroup.left_item_parent.setBackgroundColor(R.color.letv_color_0f93de);
//				viewHolderGroup.left_item_parent.setVisibility(View.VISIBLE);
				if(groupPosition == 0){
					viewHolderGroup.left_item_parent_divider.setVisibility(View.GONE);
				}
				viewHolderGroup.titleParent.setText(mItem);
//			}
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			ViewHolderChild viewHodlerChild = null;
			LeftFragmentItem mItem = childItemList.get(groupPosition).get(childPosition);
			int imgRes = mItem.imgRes;
			if (convertView == null || !convertView.getTag().equals(viewHodlerChild)) {
				viewHodlerChild = new ViewHolderChild();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.left_item_child, null);
				viewHodlerChild.icon = (LetvImageView) convertView.findViewById(R.id.row_icon);
				viewHodlerChild.titleChild = (TextView) convertView.findViewById(R.id.child_title);
				viewHodlerChild.left_item_child_selected = (View) convertView.findViewById(R.id.left_item_parent_selected);
				viewHodlerChild.left_itme_child_divider = (View) convertView.findViewById(R.id.left_itme_child_divider);
				convertView.setTag(viewHodlerChild);
			} else {
				viewHodlerChild = (ViewHolderChild) convertView.getTag();
			}
			//
			viewHodlerChild.icon.setImageResource(R.drawable.ic_default);
			viewHodlerChild.icon.setVisibility(View.VISIBLE);
			viewHodlerChild.titleChild.setText(mItem.childTitle);
			if (imgRes > 0) {
				// tag 本地
				viewHodlerChild.icon.setImageResource(mItem.imgRes);
			} else {
				// tag 网络
				if (!TextUtils.isEmpty(mItem.imgUrl)) {
					viewHodlerChild.icon.setVisibility(View.VISIBLE);
					LetvCacheMannager.getInstance().loadImage(mItem.imgUrl, viewHodlerChild.icon);
				} else {
					viewHodlerChild.icon.setVisibility(View.GONE);
				}
			}
			
			//设置点击item背景
			if(childPosition == clickChildPos && groupPosition == clickedGroupPos){
				viewHodlerChild.left_item_child_selected.setVisibility(View.VISIBLE);
			}else{
				viewHodlerChild.left_item_child_selected.setVisibility(View.INVISIBLE);
			}
			//隐藏child中的最后一个元素的divider
			if(childItemList.get(groupPosition).size()-1 == childPosition){
				viewHodlerChild.left_itme_child_divider.setVisibility(View.INVISIBLE);
			}

			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
				
		public class ViewHolderChild{
			private LetvImageView icon;
			private TextView titleChild;
			private View left_item_child_selected;
			private View left_itme_child_divider;
		}
		public class ViewHolderGroup{
			private TextView titleParent;
//			private LinearLayout left_item_parent;
			private View left_item_parent_divider;
		}


	}