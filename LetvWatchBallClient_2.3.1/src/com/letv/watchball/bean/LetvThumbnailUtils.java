package com.letv.watchball.bean;

import java.lang.reflect.Method;

import android.graphics.Bitmap;
import android.provider.MediaStore.Images;

/**
 * 获得视频截图工具类
 * @author liuheyuan
 *
 */
public class LetvThumbnailUtils {

	private static Object ThumbnailUtils = null;
	
	private static Method ThumbnailUtils_createVideoThumbnail;
	
	static {
		try {
			ThumbnailUtils = Class.forName("android.media.ThumbnailUtils").newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			ThumbnailUtils_createVideoThumbnail = ThumbnailUtils.getClass().getMethod("createVideoThumbnail", String.class, int.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Bitmap getThumbnailBitmap(String path){
		Bitmap bitmap = null;
		try {
			bitmap = (Bitmap) ThumbnailUtils_createVideoThumbnail.invoke(ThumbnailUtils, path, Images.Thumbnails.MICRO_KIND);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap ;
	}
	
}
