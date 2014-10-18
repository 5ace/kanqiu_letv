package com.letv.cache;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap.Config;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.widget.ImageView;

import com.letv.cache.LetvCacheTools.ConstantTool;
import com.letv.cache.LetvCacheTools.SDCardTool;
import com.letv.cache.LetvCacheTools.SDCardTool.cleanCacheListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.utils.LetvThumbnailUtils;

/**
 * 缓存管理者，实现了对外调用的方法
 * */
public final class LetvCacheMannager {

	/**
	 * 私有实例
	 * */
	private static LetvCacheMannager mLetvCacheMannager;

	/**
	 * 图片显示配置
	 * */
	private DisplayImageOptions options;

	/**
	 * 构造方法
	 * */
	private LetvCacheMannager() {
	}

	/**
	 * 得到实例
	 * */
	public synchronized static LetvCacheMannager getInstance() {
		if (mLetvCacheMannager == null) {
			mLetvCacheMannager = new LetvCacheMannager();
		}

		return mLetvCacheMannager;
	}

	/**
	 * 初始化
	 * */
	public void init(Context context, LetvThumbnailUtils letvThumbnailUtils) {
		LetvCacheConfiguration.initCacheLibrary(context, letvThumbnailUtils);

		options = new DisplayImageOptions.Builder()
					.cacheInMemory()
					.cacheOnDisc()
					.displayer(new SimpleBitmapDisplayer())
					//.displayer(getDisplayer())
					.bitmapConfig(Config.RGB_565)
					.imageScaleType(ImageScaleType.EXACTLY)
					.resetViewBeforeLoading()
					.build();
	}
	
	private BitmapDisplayer getDisplayer() {
		if (VERSION.SDK_INT >= 9) {
			return new FadeInBitmapDisplayer(200);
		} else {
			return new SimpleBitmapDisplayer();
		}
	}

	/**
	 * 加载图片
	 * */
	public synchronized void loadImage(final String url, final ImageView imageView) {
		if(!ImageLoader.getInstance().isInited()){
			return ;
		}
		if (TextUtils.isEmpty(url) || imageView == null) {
			return;
		}
		
		ImageLoader.getInstance().displayImage(url, imageView, options);
	}
	
	/**
	 * 加载图片
	 * */
	public synchronized void loadImage(final String url, final ImageView imageView , DisplayImageOptions options) {
		if(!ImageLoader.getInstance().isInited()){
			return ;
		}
		if (TextUtils.isEmpty(url) || imageView == null) {
			return;
		}

		ImageLoader.getInstance().displayImage(url, imageView, options);
	}

	/**
	 * 加载图片
	 * */
	public synchronized void loadImage(final String url, final ImageView imageView, ImageLoadingListener loadingListener) {
		if(!ImageLoader.getInstance().isInited()){
			return ;
		}
		if (TextUtils.isEmpty(url) || imageView == null) {
			return;
		}

		ImageLoader.getInstance().displayImage(url, imageView, options , loadingListener);
	}
	
	/**
	 * 加载图片
	 * */
	public synchronized void loadImage(final String url, final ImageView imageView , DisplayImageOptions options, ImageLoadingListener loadingListener) {
		if(!ImageLoader.getInstance().isInited()){
			return ;
		}
		if (TextUtils.isEmpty(url) || imageView == null) {
			return;
		}

		ImageLoader.getInstance().displayImage(url, imageView, options , loadingListener);
	}

	/**
	 * 加载本地视频截图
	 * */
	public synchronized void loadVideoImage(String path, final ImageView imageView) {
		if(!ImageLoader.getInstance().isInited()){
			return ;
		}
		if (TextUtils.isEmpty(path)) {
			return;
		}
		path = "LetvThumbnailUtils" + path;
		ImageLoader.getInstance().displayImage(path, imageView, options);
	}

	/**
	 * 销毁缓存对象，外部工程无需直接调用
	 * */
	public void destroy() {
		if(ImageLoader.getInstance().isInited()){
			ImageLoader.getInstance().stop();
			ImageLoader.getInstance().clearMemoryCache();
			ImageLoader.getInstance().destroy();
		}
	}
	
	/**
	 * 销毁缓存对象，外部工程无需直接调用
	 * */
	public void clearCacheBitmap() {
		try{
			if(ImageLoader.getInstance().isInited()){
				ImageLoader.getInstance().clearMemoryCache();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 清除缓存
	 * */
	public static void cleanCache(cleanCacheListener listener) {
		if (SDCardTool.sdCardMounted()) {
			SDCardTool.deleteAllFile(ConstantTool.IMAGE_CACHE_PATH, listener);
		} else {
			listener.onErr();
		}
	}

	/**
	 * 清除缓存目录大小
	 * */
	public static String getCacheSize() {
		if (SDCardTool.sdCardMounted()) {
			File file = new File(ConstantTool.IMAGE_CACHE_PATH);
			if (file != null && file.exists()) {
				long size = SDCardTool.getFileSize(file);

				return SDCardTool.FormetFileSize(size);
			} else {
				return " 0M ";
			}
		} else {
			return "";
		}
	}
}
