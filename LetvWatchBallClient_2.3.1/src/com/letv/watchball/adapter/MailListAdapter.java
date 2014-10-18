package com.letv.watchball.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.letv.watchball.R;

public class MailListAdapter extends ArrayAdapter<String> {
	
	private Context mContext = null;
	
	public MailListAdapter(Context context, String[] mails) {
		super(context, R.layout.mail_item, mails);
		mContext = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder mViewHolder = null;
		
		if(convertView == null){
			
			mViewHolder = new ViewHolder();

			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			convertView = inflater.inflate(R.layout.mail_item, null);
			  
			mViewHolder.mail_txt = (TextView) convertView.findViewById(R.id.mail_txt);
			
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder)convertView.getTag();
		}
		
		mViewHolder.mail_txt.setText(getItem(position));
		
		return convertView;
	}

	private class ViewHolder{
		private TextView mail_txt = null;
	}
}
