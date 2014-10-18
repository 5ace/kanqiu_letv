package com.letv.watchball.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

public class LetvExpandableListView extends ExpandableListView {

	public LetvExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LetvExpandableListView(Context context) {
		super(context);
	}
//	@Override 
//    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { 
//
//        int expandSpec = MeasureSpec.makeMeasureSpec( 
//                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST); 
//        super.onMeasure(widthMeasureSpec, expandSpec); 
//    } 

}
