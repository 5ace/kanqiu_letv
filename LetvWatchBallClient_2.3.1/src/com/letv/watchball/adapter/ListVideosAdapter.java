package com.letv.watchball.adapter;

import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.letv.cache.LetvCacheMannager;
import com.letv.cache.view.LetvImageView;
import com.letv.watchball.R;
import com.letv.watchball.bean.ListVideo;

public class ListVideosAdapter extends BaseAdapter {

	private Context mContext;

	private ListVideo vrsVideosList = new ListVideo();

	public ListVideosAdapter(Context context) {
		this.mContext = context;
	}

	public ListVideo getVrsVieoList() {
		return vrsVideosList;
	}
	
	public void setVrsVieoList(ListVideo vrsVideosList){
		this.vrsVideosList = vrsVideosList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHandler mViewHandler = null;
		if (convertView == null) {
			mViewHandler = new ViewHandler();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.news_list_shows_item, null);
			mViewHandler.news_icon = (LetvImageView) convertView.findViewById(R.id.news_items_icon);
			mViewHandler.news_title = (TextView) convertView.findViewById(R.id.news_items_title);
			mViewHandler.news_date = (TextView) convertView.findViewById(R.id.news_items_date);
			convertView.setTag(mViewHandler);
		} else {
			mViewHandler = (ViewHandler) convertView.getTag();
		}
		mViewHandler.news_title.setText(vrsVideosList.body.data[position].name);
		mViewHandler.news_date.setText(vrsVideosList.body.data[position].ctime);
		mViewHandler.news_icon.setImageResource(R.drawable.news_ic_default);
		LetvCacheMannager.getInstance().loadImage(vrsVideosList.body.data[position].images.img200_150, mViewHandler.news_icon);

		return convertView;
	}

	private class ViewHandler {
		LetvImageView news_icon;
		TextView news_title;
		TextView news_date;
	}

	@Override
	public int getCount() {
		return vrsVideosList.body.data.length;
	}

	@Override
	public Object getItem(int position) {
		return vrsVideosList.body.data[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}