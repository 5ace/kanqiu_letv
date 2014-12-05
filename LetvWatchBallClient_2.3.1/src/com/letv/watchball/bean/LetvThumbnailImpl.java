package com.letv.watchball.bean;

import android.graphics.Bitmap;

import com.media.NativeInfos;
import com.media.NativeThumbnail;
import com.nostra13.universalimageloader.utils.LetvThumbnailUtils;

public class LetvThumbnailImpl implements LetvThumbnailUtils {

	public Bitmap getThumbnailBitmap(String path) {
		if (NativeInfos.getSupportLevel() == NativeInfos.SUPPORT_MP4_LEVEL
				|| !NativeInfos.ifSupportVfpOrNeon()) {
			return com.letv.watchball.bean.LetvThumbnailUtils
					.getThumbnailBitmap(path);
		} else {
			NativeThumbnail nativeThumbnail = new NativeThumbnail(path);
			return nativeThumbnail.getVideoThumbnail(96, 96, 10);
		}
	};
}
