package com.letv.watchball.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.letv.watchball.bean.FocusTeamList;
import com.letv.watchball.bean.MyTeams;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.utils.LetvUtil;
import com.letv.watchball.utils.UIs;

public class MyTeamsAdapter extends BaseAdapter {

	private List<MyTeams.Body> Teamlist;
	private LayoutInflater mInflater;
	private Context context;

	public MyTeamsAdapter(Context context, List<MyTeams.Body> Teamlist) {
		this.Teamlist = Teamlist;
		this.context = context;
		this.mInflater = LayoutInflater.from(context);

	}

	@Override
	public int getCount() {
		return Teamlist.size() + 1;
	}

	@Override
	public MyTeams.Body getItem(int Position) {
		if (Position == 0) {
			return null;
		}
		return Teamlist.get(Position - 1);
	}

	@Override
	public long getItemId(int Position) {
		return Position;
	}

	@Override
	public View getView(final int Position, View convertView, ViewGroup arg2) {
		BallTeamAdapterViewHolder holder = null;
		if (convertView == null) {
			holder = new BallTeamAdapterViewHolder();
			convertView = mInflater.inflate(R.layout.my_teams_item, null);

			holder.name = (TextView) convertView.findViewById(R.id.child_name);
			holder.icon = (LetvImageView) convertView
					.findViewById(R.id.child_icon);
			holder.focusSwitcher = (ImageView) convertView
					.findViewById(R.id.focus_switcher);
			convertView.setTag(holder);
		} else {

			holder = (BallTeamAdapterViewHolder) convertView.getTag();
		}
		// 第一个item 添加球队
		if (Position == 0) {
			holder.name.setText(R.string.right_fragment_add_team);
			holder.icon.setImageResource(R.drawable.add_teams_icon);
			holder.focusSwitcher.setVisibility(View.GONE);
			return convertView;
		}

		final MyTeams.Body team = getItem(Position);
		holder.name.setText(team.name);
		holder.icon.setImageResource(R.drawable.ic_default);
		LetvCacheMannager.getInstance().loadImage(team.img_url, holder.icon);
		int focus = Integer.parseInt(team.focused);
		final ImageView focusBtn = holder.focusSwitcher;
		holder.focusSwitcher
				.setImageResource(R.drawable.myfocus_item_child_add_sel);
		holder.focusSwitcher.setVisibility(View.VISIBLE);
		if (focus == 1) {
			// 已关注
			setFocus(true, holder.focusSwitcher);
			holder.focusSwitcher.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (LetvUtil.hasNet()) {

						new RequestUnfocus(MainActivity.getInstance(),
								team.teamId + "", team.level, new Runnable() {

									@Override
									public void run() {
										// 取消关注成功
										setFocus(false, focusBtn);
										team.focused = 0 + "";
										// Teamlist.remove(Position - 1);
										notifyDataSetChanged();
									}
								}).start();
					} else {

						UIs.showToast(R.string.toast_net_15);
					}
				}
			});
		} else {
			// 未关注
			setFocus(false, holder.focusSwitcher);
			holder.focusSwitcher.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (LetvUtil.hasNet()) {

						new RequestFocus(MainActivity.getInstance(),
								team.teamId + "", team.level, new Runnable() {

									@Override
									public void run() {
										// 关注成功
										setFocus(true, focusBtn);
										team.focused = 1 + "";
										notifyDataSetChanged();
									}
								}).start();
					} else {

						UIs.showToast(R.string.toast_net_15);
					}
				}
			});
		}
		return convertView;
	}

	/**
	 * 设置关注状态
	 * 
	 * @param isFocus
	 * @param tv
	 */
	public void setFocus(boolean isFocus, ImageView tv) {
		if (isFocus) {
			tv.setImageResource(R.drawable.myfocus_item_child_add_sel);
		} else {
			tv.setImageResource(R.drawable.myfocus_item_child_add_nor);
		}
	}

	public class BallTeamAdapterViewHolder {
		private TextView name;
		private LetvImageView icon;
		private ImageView focusSwitcher;
	}

	/**
	 * http请求 关注
	 * */
	private class RequestFocus extends LetvHttpAsyncTask<FocusTeamList> {

		private String teamId;
		private String level;
		private Runnable callback;

		public RequestFocus(Context context, String teamId, String level,
				Runnable callback) {
			super(context, true);
			this.teamId = teamId;
			this.level = level;
			this.callback = callback;
		}

		@Override
		public LetvDataHull<FocusTeamList> doInBackground() {
			return LetvHttpApi.requestFocus(teamId, level,
					new LetvGsonParser<FocusTeamList>(0, FocusTeamList.class));
		}

		@Override
		public void onPostExecute(int updateId, FocusTeamList result) {
			if (Integer.parseInt(result.header.status) == 1) {
				callback.run();
				Toast.makeText(LetvApplication.getInstance(), "关注成功！",
						Toast.LENGTH_SHORT).show();
			} else {

				if (!LetvUtil.hasNet()) {

					Toast.makeText(LetvApplication.getInstance(), "关注失败！",
							Toast.LENGTH_SHORT).show();
				}
			}
		}

		@Override
		public void netNull() {

			if (!LetvUtil.hasNet()) {

				Toast.makeText(LetvApplication.getInstance(), "关注失败！",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void netErr(int updateId, String errMsg) {

			if (!LetvUtil.hasNet()) {

				Toast.makeText(LetvApplication.getInstance(), "关注失败！",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void dataNull(int updateId, String errMsg) {

			if (!LetvUtil.hasNet()) {

				Toast.makeText(LetvApplication.getInstance(), "关注失败！",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * http请求 关注
	 * */
	private class RequestUnfocus extends LetvHttpAsyncTask<FocusTeamList> {

		private String teamId;
		private String level;
		private Runnable callback;

		public RequestUnfocus(Context context, String teamId, String level,
				Runnable callback) {
			super(context, true);
			this.teamId = teamId;
			this.level = level;
			this.callback = callback;
		}

		@Override
		public LetvDataHull<FocusTeamList> doInBackground() {
			return LetvHttpApi.requestUnfocus(teamId, level,
					new LetvGsonParser<FocusTeamList>(0, FocusTeamList.class));
		}

		@Override
		public void onPostExecute(int updateId, FocusTeamList result) {
			if (Integer.parseInt(result.header.status) == 1) {
				callback.run();
				Toast.makeText(LetvApplication.getInstance(), "取消关注成功！",
						Toast.LENGTH_SHORT).show();
			} else {

				if (!LetvUtil.CheckNetworkState()) {

					Toast.makeText(LetvApplication.getInstance(), "取消关注失败！",
							Toast.LENGTH_SHORT).show();
				}
			}
		}

		@Override
		public void netNull() {
			if (!LetvUtil.CheckNetworkState()) {

				Toast.makeText(LetvApplication.getInstance(), "取消关注失败！",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void netErr(int updateId, String errMsg) {
			if (!LetvUtil.CheckNetworkState()) {

				Toast.makeText(LetvApplication.getInstance(), "取消关注失败！",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			if (!LetvUtil.CheckNetworkState()) {

				Toast.makeText(LetvApplication.getInstance(), "取消关注失败！",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
}
