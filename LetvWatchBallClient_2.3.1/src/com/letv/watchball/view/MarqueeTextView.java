package com.letv.watchball.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

/**
 * 不获取焦点也能跑马灯
 */
public class MarqueeTextView extends TextView {

	private boolean isMarquee = true;
	private boolean isFocused;
	private boolean isSelected;

	public MarqueeTextView(Context context) {
		super(context);
	}

	public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean isFocused() {
		return isMarquee && isFocused;
	}

	@Override
	public void setSelected(boolean selected) {

		isFocused = selected;
		isSelected = !selected;

		super.setSelected(selected);
	}

	@Override
	@ExportedProperty
	public boolean isSelected() {
		return isMarquee && isSelected;
	}

	public void setMarquee(boolean isMarquee) {
		this.isMarquee = isMarquee;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		isSelected = isMarquee;
		super.onDraw(canvas);
	}

	@Override
	protected boolean setFrame(int l, int t, int r, int b) {
		isSelected = isMarquee;
		return super.setFrame(l, t, r, b);
	}
}
