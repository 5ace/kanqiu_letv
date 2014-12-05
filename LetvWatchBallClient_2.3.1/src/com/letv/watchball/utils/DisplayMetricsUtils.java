package com.letv.watchball.utils;

import android.content.Context;

public class DisplayMetricsUtils {
	/**
	 * 屏幕宽度像素
	 */
	private int w_px;
	/**
	 * 屏幕高度像素
	 */
	private int h_px;
	/**
	 * 屏幕宽度密度
	 */
	private float w_dip;
	/**
	 * 屏幕高度密度
	 */
	private float h_dip;
	/**
	 * 屏幕密度
	 */
	private float dip;

	private float densityDpi;

	public DisplayMetricsUtils(Context mContext) {

		android.util.DisplayMetrics displayMetrics = mContext.getResources()
				.getDisplayMetrics();
		w_px = displayMetrics.widthPixels;
		h_px = displayMetrics.heightPixels;
		w_dip = displayMetrics.xdpi;
		h_dip = displayMetrics.ydpi;
		dip = displayMetrics.density;
		densityDpi = displayMetrics.densityDpi;

	}

	public int getW_px() {
		return this.w_px;
	}

	public int getH_px() {
		return this.h_px;
	}

	public float getW_dip() {
		return this.w_dip;
	}

	public float getH_dip() {
		return this.h_dip;
	}

	public float getDip() {
		return this.dip;
	}

	public float getDensityDpi() {
		return this.densityDpi;
	}

}
