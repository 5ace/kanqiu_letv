package com.letv.watchball.async;

/**
 * 线程池工厂
 * */
public class LetvThreadPoolFactory {
	
	/**
	 * 默认配置
	 * */
	private static final ThreadPoolOptions defaultOptions = new ThreadPoolOptions();
	
	static{
		defaultOptions.setPriority(Thread.NORM_PRIORITY);
		defaultOptions.setSize(1);
		defaultOptions.setWaitPeriod(100);
		defaultOptions.setReplayFailTask(false);
	}
	

	public static LetvBaseThreadPool create(ThreadPoolOptions options){
		return initialize(options);
	}
	
	private static LetvBaseThreadPool initialize(ThreadPoolOptions options){
		if(options == null){
			options = defaultOptions ;
		}
		LetvBaseThreadPool threadPool = new LetvBaseThreadPool(options);
		return threadPool ;
	}
}
