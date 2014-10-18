package com.letv.watchball.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;

import com.letv.watchball.LetvApplication;

public class TextUtil {

	public static String getString(int id) {
		return LetvApplication.getInstance().getString(id);
	}

	public static String getString(int id, Object... objects) {
		return LetvApplication.getInstance().getString(id, objects);
	}

	public static String text(int id) {
		return LetvApplication.getInstance().getString(id);
	}

	public static String text(int id, Object... objects) {
		return LetvApplication.getInstance().getString(id, objects);
	}

	/**
	 * 设置自动平铺的bitmap
	 * 
	 * @param res
	 * @param repeateX
	 * @param repeateY
	 * @return
	 */
	public static BitmapDrawable setRepeatBitMapDrawable(int res, boolean repeateX, boolean repeateY) {
		Bitmap bitmap = BitmapFactory.decodeResource(LetvApplication.getInstance().getResources(), res);
		BitmapDrawable bmp = new BitmapDrawable(bitmap);
		bmp.mutate(); // make sure that we aren't sharing state anymore
		if (repeateX) {
			bmp.setTileModeX(TileMode.REPEAT);
		}
		if (repeateY) {
			bmp.setTileModeY(TileMode.REPEAT);
		}
		// bmp.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
		return bmp;
	}

	/**
	 * 设置自动平铺的bitmap
	 * 
	 * @param res
	 * @param repeateX
	 * @param repeateY
	 * @return
	 */
	public static BitmapDrawable setRepeatBitMapDrawable(int res) {
		Bitmap bitmap = BitmapFactory.decodeResource(LetvApplication.getInstance().getResources(), res);
		BitmapDrawable bmp = new BitmapDrawable(bitmap);
		// bmp.mutate(); // make sure that we aren't sharing state anymore
		bmp.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
		bmp.setDither(true);
		return bmp;
	}
}
