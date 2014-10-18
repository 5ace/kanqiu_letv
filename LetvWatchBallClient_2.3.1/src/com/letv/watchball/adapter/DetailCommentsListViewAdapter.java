package com.letv.watchball.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.letv.watchball.R;
import com.letv.watchball.bean.Comments;
import com.letv.watchball.ui.PlayController;
import com.letv.watchball.utils.UIs;

public class DetailCommentsListViewAdapter extends LetvBaseAdapter {
	private Comments.Data mRecommend;
	public DetailCommentsListViewAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mViewHolder = null;
		mRecommend = (Comments.Data) getItem(position);
		if(mRecommend==null){
			return null;
		}
		if (convertView == null) {
			mViewHolder = new ViewHolder();
			convertView = UIs.inflate(context, R.layout.detailplay_half_comments_listview_item, parent, false);
			
			mViewHolder.userName = (TextView) convertView.findViewById(R.id.user_name);
			mViewHolder.cTime = (TextView) convertView.findViewById(R.id.ctime);
			mViewHolder.cContent = (TextView) convertView.findViewById(R.id.comment_content);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		if(null != mRecommend.user && !TextUtils.isEmpty(mRecommend.user.username)){
			mViewHolder.userName.setText(mRecommend.user.username);
		}
		if(!TextUtils.isEmpty(mRecommend.vtime)){
			mViewHolder.cTime.setText(mRecommend.vtime);
		}
		if(!TextUtils.isEmpty(mRecommend.content)){
			mViewHolder.cContent.setText(mRecommend.content);
		}
		return convertView;
	}
	private class ViewHolder {
		public TextView userName;
		public TextView cTime;
		public TextView cContent;
	}
}
