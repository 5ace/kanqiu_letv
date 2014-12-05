package com.letv.watchball.view;

/*
 * Copyright (C) 2010 Neil Davies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This code is base on the Android Gallery widget and was Created 
 * by Neil Davies neild001 'at' gmail dot com to be a Coverflow widget
 * 
 * @author Neil Davies
 */

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;
import android.widget.ScrollView;

public class GalleryFlow extends Gallery {

	public static boolean isFling = true;// 当为false时表示Gallery只可以一次滑动一张

	private int mCoveflowCenter;

	private int MaxZ = -50;

	// private float MaxY = -0.0f ;
	private float MaxY = -MaxZ * 0.3f;

	private Camera mCamera;

	public GalleryFlow(Context context, AttributeSet attrs) {
		super(context, attrs);
		setStaticTransformationsEnabled(true);
		mCamera = new Camera();
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		int event;

		int f = isScrollingLeft(e1, e2);
		if (f == -1) {
			return super.onFling(e1, e2, velocityX, velocityY);
		}
		if (isFling) {
			return super.onFling(e1, e2, velocityX, velocityY);
		} else {
			if (f == 0) {
				event = KeyEvent.KEYCODE_DPAD_LEFT;
			} else {
				event = KeyEvent.KEYCODE_DPAD_RIGHT;
			}
			onKeyDown(event, null);
			return false;
		}
	}

	private int isScrollingLeft(MotionEvent e1, MotionEvent e2) {
		if (e1 == null || e2 == null) {
			return -1;
		}
		return e2.getX() > e1.getX() ? 0 : 1;
	}

	@Override
	protected boolean getChildStaticTransformation(View child, Transformation t) {

		// 每个View的中轴
		int c = getCenterOfView(child);
		// int w = child.getWidth() / 2;
		int w = getWidth();

		// mCoveflowCenter gallery的中轴
		int ts = Math.abs(c - mCoveflowCenter);

		// if(c == mCoveflowCenter){
		// transformImageBitmap(child, t, ((float)(w - ts) / w) * MaxZ * 8 ,
		// ((float)(w - ts) / w) * MaxY);
		// }else{

		transformImageBitmap(child, t, ((float) (w - ts * 2) / w) * MaxZ * 6,
				((float) (w - ts) / w) * MaxY);
		// }

		return true;
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mCoveflowCenter = getCenterOfCoverflow();
		super.onSizeChanged(w, h, oldw, oldh);
	}

	/**
	 * 得到View的中轴
	 * */
	private static int getCenterOfView(View view) {
		return view.getLeft() + view.getWidth() / 2;
	}

	/**
	 * 得到gallery的中轴
	 * */
	private int getCenterOfCoverflow() {
		return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2
				+ getPaddingLeft();
	}

	private void transformImageBitmap(View child, Transformation t, float tsZ,
			float tsY) {
		// 对效果进行保存
		mCamera.save();
		final Matrix imageMatrix = t.getMatrix();
		// 图片高度
		final int imageHeight = child.getLayoutParams().height;
		// 图片宽度
		final int imageWidth = child.getLayoutParams().width;

		mCamera.translate(0, 0, tsZ);
		mCamera.getMatrix(imageMatrix);
		mCamera.restore();

		imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));
		imageMatrix.postTranslate((imageWidth / 2), (imageHeight / 2));
	}
}