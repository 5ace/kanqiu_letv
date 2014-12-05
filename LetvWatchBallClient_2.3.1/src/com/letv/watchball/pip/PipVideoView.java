package com.letv.watchball.pip;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.widget.RelativeLayout.LayoutParams;

import com.letv.watchball.utils.LetvUtil;

public class PipVideoView extends VideoView {

	public PipVideoView(Context context) {
		super(context);
	}

	public PipVideoView(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
	}

	public PipVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void initVideoView() {
		super.initVideoView();
		setKeepScreenOn(true);
		getHolder().setFormat(PixelFormat.TRANSLUCENT);
	}

	@Override
	protected void remodelScreen(boolean fullScreen) {
		this.fullScreen = fullScreen;
		int screenWidth = LetvUtil.getDisplayWidth(getContext());
		int screenHeight = 29 * screenWidth / 48;

		int videoWidth = mVideoWidth;
		int videoHeight = mVideoHeight;
		int mWidth = screenWidth;
		int mHeight = screenHeight;

		if (videoWidth > 0 && videoHeight > 0) {
			if (videoWidth * mHeight > mWidth * videoHeight) {
				mHeight = mWidth * videoHeight / videoWidth;
			} else if (videoWidth * mHeight < mWidth * videoHeight) {
				mWidth = mHeight * videoWidth / videoHeight;
			} else {

			}
		}
		setVideoViewScale(mWidth, mHeight);

	}

	@Override
	protected void setVideoViewScale(int width, int height) {
		LayoutParams lp = (LayoutParams) this.getLayoutParams();
		lp.height = height;
		lp.width = width;
		setLayoutParams(lp);
	}
}
