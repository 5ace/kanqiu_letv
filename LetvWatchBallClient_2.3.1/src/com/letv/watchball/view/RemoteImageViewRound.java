package com.letv.watchball.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;

import com.letv.cache.view.LetvImageView;

/**
 * 画出圆角
 * 
 * @author liuheyuan
 * 
 */
public class RemoteImageViewRound extends LetvImageView {

	public RemoteImageViewRound(Context context) {
		super(context);
	}

	public RemoteImageViewRound(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		try {

			Path clipPath = new Path();
			int w = this.getWidth();
			int h = this.getHeight();
//			clipPath.addRoundRect(new RectF(0, 0, w, h), 45.0f, 45.0f, Path.Direction.CW);
			clipPath.addCircle(w/2, h/2, w/2, Path.Direction.CW);
			canvas.clipPath(clipPath);
		} catch (Exception e) {
			Log.d("LHY", "RemoteImageViewRound-onDraw = " + e.toString());
		}
		super.onDraw(canvas);
	}
}
