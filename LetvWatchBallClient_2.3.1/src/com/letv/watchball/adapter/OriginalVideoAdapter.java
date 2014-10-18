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
import com.letv.watchball.bean.OriginalVideo;

public class OriginalVideoAdapter extends BaseAdapter {

	private HashMap<LetvImageView, Integer> images;
	private Context context;
	private OriginalVideo originalVideos = new OriginalVideo();
	
	
	public OriginalVideoAdapter(Context context) {
		images = new HashMap<LetvImageView, Integer>();
		this.context = context;
	}
	public OriginalVideo getOriginalVideos() {
		return originalVideos;
	}


	public void setOriginalVideos(OriginalVideo originalVideos) {
		this.originalVideos = originalVideos;
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHandler mViewHandler = null;
		if (convertView == null) {
			mViewHandler = new ViewHandler();
			convertView = LayoutInflater.from(context).inflate(R.layout.news_list_shows_item, null);
			mViewHandler.news_icon = (LetvImageView) convertView.findViewById(R.id.news_items_icon);
			mViewHandler.news_title = (TextView) convertView.findViewById(R.id.news_items_title);
			mViewHandler.news_date = (TextView) convertView.findViewById(R.id.news_items_date);
			convertView.setTag(mViewHandler);
		} else {
			mViewHandler = (ViewHandler) convertView.getTag();
		}
		mViewHandler.news_title.setText(originalVideos.body.videos[position].name);
		mViewHandler.news_date.setText(originalVideos.body.videos[position].release_date);
		mViewHandler.news_icon.setImageResource(R.drawable.news_ic_default);
		LetvCacheMannager.getInstance().loadImage(originalVideos.body.videos[position].img_url, mViewHandler.news_icon);

		return convertView;
	}

	public synchronized void notifyImageView(int start, int end) {/*
		synchronized (images) {
			if (images == null || images.size() == 0) {
				return;
			}
			Iterator<LetvImageView> i = images.keySet().iterator();
			while (i.hasNext()) {
				LetvImageView imageView = i.next();
				int pos = images.get(imageView);
				String icon = originalVideos.body.videos[pos].img_url;
				if (pos >= start && pos <= end) {
					LetvCacheMannager.getInstance().loadImage(icon, imageView);
				}
			}
			images.clear();
		}
	*/}

	@Override
	public void notifyDataSetChanged() {
		images.clear();
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return originalVideos.body.videos.length;
	}

	@Override
	public Object getItem(int position) {
		return originalVideos.body.videos[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private class ViewHandler {
		LetvImageView news_icon;
		TextView news_title;
		TextView news_date;
	}
}
