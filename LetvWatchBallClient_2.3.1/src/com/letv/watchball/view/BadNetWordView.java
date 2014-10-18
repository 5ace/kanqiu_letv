package com.letv.watchball.view;

import com.letv.watchball.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class BadNetWordView extends LinearLayout{

	public BadNetWordView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setBackgroundColor(R.color.letv_main_bg);
		setGravity(Gravity.CENTER);
		
		ImageView badNet = new ImageView(getContext());
		badNet.setImageResource(R.drawable.badnetwork);
		this.addView(badNet);
		this.setVisibility(View.GONE);
	}
	
	/**
	 * 显示网络异常
	 * @param retry
	 */
	public void showBadNet(final Runnable retry){
		setVisibility(View.VISIBLE);
		setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				retry.run();
			}
		});
	}

	
}
