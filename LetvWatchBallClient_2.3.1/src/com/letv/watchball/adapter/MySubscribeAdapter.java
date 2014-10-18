package com.letv.watchball.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.letv.cache.LetvCacheMannager;
import com.letv.cache.view.LetvImageView;
import com.letv.watchball.R;
import com.letv.watchball.bean.Game;

public class MySubscribeAdapter extends ArrayAdapter<Game>{

	public MySubscribeAdapter(Context context) {
		super(context, 0);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHodler = null;
		final Game data = getItem(position);
		if (convertView == null || !convertView.getTag().equals(viewHodler)) {
			viewHodler = new ViewHolder();
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.my_subscribe_item, null);
			viewHodler.matchName = (TextView) convertView.findViewById(R.id.game_name);
			viewHodler.date = (TextView) convertView.findViewById(R.id.game_date);
			viewHodler.startTime = (TextView) convertView.findViewById(R.id.game_time);
			viewHodler.homeIcon = (LetvImageView) convertView.findViewById(R.id.team_home_icon);
			viewHodler.guestIcon = (LetvImageView) convertView.findViewById(R.id.team_guest_icon);
			viewHodler.homeName = (TextView) convertView.findViewById(R.id.team_host);
			viewHodler.guestName= (TextView) convertView.findViewById(R.id.team_guest);
			
			convertView.setTag(viewHodler);
		}else{
			viewHodler = (ViewHolder) convertView.getTag();
		}
		//child item style
		viewHodler.homeIcon.setImageResource(R.drawable.ic_default);
		viewHodler.guestIcon.setImageResource(R.drawable.ic_default);
		LetvCacheMannager.getInstance().loadImage(data.homeImg, viewHodler.homeIcon);
		LetvCacheMannager.getInstance().loadImage(data.guestImg, viewHodler.guestIcon);
		viewHodler.matchName.setText(data.level);
		viewHodler.date.setText(data.playDate);
		viewHodler.startTime.setText(data.playTime);
		viewHodler.homeName.setText(data.home);
		viewHodler.guestName.setText(data.guest);
			
		return convertView;
	}
	
	public class ViewHolder{
		private LetvImageView homeIcon,guestIcon;
		private TextView matchName,date,startTime,homeName,guestName;
	}
	
	
}
