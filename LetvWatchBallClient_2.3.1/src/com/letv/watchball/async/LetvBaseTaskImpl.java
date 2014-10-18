package com.letv.watchball.async;

public abstract class LetvBaseTaskImpl implements LetvBaseTask{
	
	protected boolean isCancel = false ;
	/**
	 * 线程池
	 * */
	protected static final LetvBaseThreadPool mThreadPool;

	static {// 初始化线程池
		ThreadPoolOptions options = new ThreadPoolOptions();
		options.setPriority(Thread.NORM_PRIORITY + 1);
		options.setSize(20);
		options.setWaitPeriod(1000);
		options.setReplayFailTask(false);
		mThreadPool = LetvThreadPoolFactory.create(options);
	}
	
	@Override
	public void cancel() {
		this.isCancel = true;
		if(mThreadPool != null){
			mThreadPool.removeTask(this);
		}
	}

	@Override
	public boolean isCancelled() {
		return this.isCancel;
	}
}
