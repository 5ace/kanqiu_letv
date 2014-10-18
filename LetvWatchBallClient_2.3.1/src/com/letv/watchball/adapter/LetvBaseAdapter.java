package com.letv.watchball.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class LetvBaseAdapter extends BaseAdapter{
	
	protected List<?> list ;
	
	protected Context context ;
	
	public LetvBaseAdapter(Context context){
		this.context = context ;
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size() ;
	}

	@Override
	public Object getItem(int position) {
		return list == null ? 0 : list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position ;
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);

	public List<?> getList() {
		return list;
	}

	public void setList(List<?> list) {
		this.list = list;
	}
}
