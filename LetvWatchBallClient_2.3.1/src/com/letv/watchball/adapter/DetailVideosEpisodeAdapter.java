package com.letv.watchball.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.letv.watchball.R;
import com.letv.watchball.ui.LetvFunction;
import com.letv.watchball.utils.UIs;

public class DetailVideosEpisodeAdapter extends LetvBaseAdapter {

	private int totle;

	private int curPage = 1;

	private int pageSize;

	/**
	 * 自定义子视图布局
	 */
	private int itemLayout = 0;

	public DetailVideosEpisodeAdapter(Context context) {
		super(context);
	}

	public DetailVideosEpisodeAdapter(Context context, int selfItemLayout) {
		super(context);
		itemLayout = selfItemLayout;
	}

	@Override
	public int getCount() {

		if (totle == 0) {
			return 0;
		}

		if (pageSize == 0) {
			return 0;
		}
		return LetvFunction.calculateRows(totle, pageSize);
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView textView = null;
		if (convertView == null) {
			if (itemLayout != 0) {
				convertView = UIs.inflate(context, itemLayout, parent, false);
			} else {
				convertView = UIs.inflate(context, R.layout.detailplay_half_videos_episode_item, parent, false);
			}

			textView = (TextView) convertView.findViewById(R.id.video_episode_text);

			convertView.setTag(textView);
			convertView.getLayoutParams().height = UIs.dipToPx(44);
		} else {
			textView = (TextView) convertView.getTag();
		}

		int star = 0;
		int end = 0;

		star = pageSize * position + 1;
		end = pageSize * (position + 1);
		if (end > totle) {
			end = totle;
		}

		if ((position + 1) == curPage) {
			textView.setTextColor(context.getResources().getColor(R.color.letv_color_ff00a0e9));
		} else {
			textView.setTextColor(context.getResources().getColor(R.color.letv_color_ff5c5c5c));
		}

		textView.setText(star + "-" + end);

		return convertView;
	}

	public int getTotle() {
		return totle;
	}

	public void setTotle(int totle) {
		this.totle = totle;
	}

	public int getCurPage() {
		return curPage;
	}

	public void setCurPage(int curPage) {
            Log.d("curPage" ,"curPage=" + curPage);
            if (curPage == 0){
                  curPage = 1;
            }
                  this.curPage = curPage;
                  notifyDataSetChanged();
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
}
