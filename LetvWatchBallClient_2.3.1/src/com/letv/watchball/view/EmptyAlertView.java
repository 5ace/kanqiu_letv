package com.letv.watchball.view;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.letv.watchball.R;
import com.letv.watchball.utils.LetvUtil;

public class EmptyAlertView extends RelativeLayout{

	private ImageView emptyImage;
	public EmptyAlertView(Context context) {
		super(context);
		emptyImage = new ImageView(getContext());
//		emptyMatch.setImageResource(R.drawable.empty_match_icon);
		this.addView(emptyImage, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		setVisibility(View.GONE);
	}
	/**
	 * 无比赛
	 */
	public void showNoMatchs(){
		setVisibility(View.VISIBLE);
		emptyImage.setVisibility(VISIBLE);
		emptyImage.setImageResource(R.drawable.empty_match_icon);
		LayoutParams params = (LayoutParams) emptyImage.getLayoutParams();
		params.topMargin = (int) (LetvUtil.getScreenHeight((Activity)getContext()) * 0.12f);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		emptyImage.requestLayout();
	}
	
	/**
	 * 无关注
	 */
	public void showNoFocus(){
		emptyImage.setVisibility(VISIBLE);
		emptyImage.setImageResource(R.drawable.empty_focus_icon);
		LayoutParams params = (LayoutParams) emptyImage.getLayoutParams();
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		emptyImage.requestLayout();
	}
	/**
	 * 无预约
	 */
	public void showNoSubscribe(){
		emptyImage.setImageResource(R.drawable.empty_subscribe_icon);
//		LayoutParams params = (LayoutParams) emptyImage.getLayoutParams();
//		params.topMargin = (int) (LetvUtil.getScreenHeight((Activity)getContext()) * 0.25f);
//		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
//		emptyImage.requestLayout();
	}

	
}
