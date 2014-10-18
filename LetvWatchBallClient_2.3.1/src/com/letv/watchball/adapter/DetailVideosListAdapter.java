package com.letv.watchball.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.letv.cache.LetvCacheMannager;
import com.letv.cache.view.LetvImageView;
import com.letv.watchball.R;
import com.letv.watchball.bean.Video;
import com.letv.watchball.utils.LetvUtil;
import com.letv.watchball.utils.UIs;

public class DetailVideosListAdapter extends LetvBaseAdapter {

	private long curId;

	private boolean isDownload;

	public DetailVideosListAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Video video = (Video) getItem(position);

		ViewHandler handler = null;
		if (convertView == null) {
			convertView = UIs.inflate(context, R.layout.detailplay_half_videos_list_item, parent, false);
			handler = new ViewHandler();

			handler.news_items_date = (TextView) convertView.findViewById(R.id.news_items_date);
			handler.episode = (TextView) convertView.findViewById(R.id.videos_list_item_title);
			handler.news_items_icon = (LetvImageView) convertView.findViewById(R.id.news_items_icon);
			handler.top = convertView.findViewById(R.id.top);
			handler.left = convertView.findViewById(R.id.left);
			handler.bottom = convertView.findViewById(R.id.bottom);
			handler.right = convertView.findViewById(R.id.right);

			convertView.setTag(handler);
		} else {
			handler = (ViewHandler) convertView.getTag();
		}

		handler.news_items_date.setText("发布时间："+video.getReleaseDate());
		handler.episode.setText(video.getNameCn());
		LetvCacheMannager.getInstance().loadImage(video.getPic(), handler.news_items_icon);

		if (curId == video.getId()) {
//			handler.top.setBackgroundResource(R.color.letv_color_ff00a0e9);
//			handler.left.setBackgroundResource(R.color.letv_color_ff00a0e9);
//			handler.right.setBackgroundResource(R.color.letv_color_ff00a0e9);
//			handler.bottom.setBackgroundResource(R.color.letv_color_ff00a0e9);
			convertView.setBackgroundResource(R.color.letv_color_ececec);
//			handler.episode.setTextColor(context.getResources().getColor(R.color.letv_color_ff0f93de));
		} else {
			handler.top.setBackgroundResource(R.color.letv_color_ffa1a1a1);
			handler.left.setBackgroundResource(R.color.letv_color_ffa1a1a1);
			handler.right.setBackgroundResource(R.color.letv_color_ffa1a1a1);
			handler.bottom.setBackgroundResource(R.color.letv_color_ffa1a1a1);
//			if (dbBean != null) {
//				convertView.setBackgroundResource(R.color.letv_color_ffdfdfdf);
//			} else {
				convertView.setBackgroundResource(R.color.letv_color_fff6f6f6);
//			}
			handler.episode.setTextColor(context.getResources().getColor(R.color.letv_color_ff5c5c5c));
		}


		return convertView;
	}

	private class ViewHandler {
		private TextView episode;
		private TextView news_items_date;
		private LetvImageView news_items_icon;
		private View top;
		private View left;
		private View bottom;
		private View right;
	}

	public long getCurId() {
		return curId;
	}

	public void setCurId(long curId) {
		this.curId = curId;
	}

	public boolean isDownload() {
		return isDownload;
	}

	public void setDownload(boolean isDownload) {
		this.isDownload = isDownload;
	}
}
