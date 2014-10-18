package com.letv.cache;

import java.io.File;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.DiscCacheAware;
import com.nostra13.universalimageloader.cache.disc.impl.TotalSizeLimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.HttpClientImageDownloader;
import com.nostra13.universalimageloader.utils.LetvThumbnailUtils;

public class LetvCacheConfiguration {
	
	private static final int MAX_FILE_COUNT = 100;
	private static final int MAX_FILE_TOTAL_SIZE = 20 * 1024 * 1024;

	/**
	 * 初始化缓存工具
	 * */
	public static void initCacheLibrary(Context context , LetvThumbnailUtils thumbnailUtils) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
												.memoryCache(getMemoryCache(context))
												.discCache(getDiscCache())//本地缓存配置
												//.discCacheFileCount(MAX_FILE_COUNT)
												.threadPoolSize(3)//图片线程池3
												.threadPriority(Thread.NORM_PRIORITY - 2)//线程级别 3
												.denyCacheImageMultipleSizesInMemory()//一个URL对应一个图片
												.imageDownloader(new HttpClientImageDownloader(context, PoolingClientConnectionManager.get()))
												.tasksProcessingOrder(QueueProcessingType.LIFO)//任务队列执行顺序 后进先出
												.setThumbnailUtils(thumbnailUtils)
//												.enableLogging()
												.build();
		
		ImageLoader.getInstance().init(config);
	}
	
	/**
	 * 缓存配置
	 * */
	public static LruMemoryCache getMemoryCache(Context context){
		int cacheSize = 4 * 1024 * 1024 ;
		try{
			int memClass = ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
	        int availableSize = memClass >> 3;
			cacheSize = 1024 * 1024 * (availableSize == 0 ? 4 : availableSize);
			Log.d("ljn", "getMemoryCache---memClass:" + memClass + "----availableSize:" + availableSize);
		} catch (Exception exception){
			exception.printStackTrace();
		}
		LruMemoryCache memoryCache = new LruMemoryCache(cacheSize);
		
		return memoryCache ;
	}
	
	/**
	 * 缓存目录生成，
	 * 目的地：./letv/cache/pics/
	 * 命名：MD5加密
	 * 最大大小：50兆
	 * */
	public static DiscCacheAware getDiscCache(){
		final File dir = new File(LetvCacheTools.ConstantTool.IMAGE_CACHE_PATH);
//		DiscCacheAware discCacheAware = new TotalSizeLimitedDiscCache(dir, new Md5FileNameGenerator(), MAX_FILE_TOTAL_SIZE);
		DiscCacheAware discCacheAware = new UnlimitedDiscCache(dir, new Md5FileNameGenerator());
		
		return discCacheAware ;
	}
}
