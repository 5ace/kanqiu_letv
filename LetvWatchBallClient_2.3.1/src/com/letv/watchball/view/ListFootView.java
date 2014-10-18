package com.letv.watchball.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.letv.watchball.R;
import com.letv.watchball.utils.UIs;

public class ListFootView extends LinearLayout{
	
	public LinearLayout loadingLayout ;
	
	private LinearLayout refreshLayout ;
	
	public ListFootView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context) ;
	}

	public ListFootView(Context context) {
		super(context);
		init(context) ;
	}

	protected void init(Context context) {
        View view =  UIs.inflate(context,R.layout.listview_foot, null);
        
        loadingLayout = (LinearLayout) view.findViewById(R.id.loading_layout);
        refreshLayout = (LinearLayout) view.findViewById(R.id.refresh_layout);
        
        this.addView(view);
	}
	
	public void showLoading(){
		loadingLayout.setVisibility(View.VISIBLE);
		refreshLayout.setVisibility(View.INVISIBLE);
	}
	
	public void showRefresh(){
		loadingLayout.setVisibility(View.INVISIBLE);
		refreshLayout.setVisibility(View.VISIBLE);
	}
	
	public void hide(){
		loadingLayout.setVisibility(View.GONE);
		refreshLayout.setVisibility(View.GONE);
	}
}
