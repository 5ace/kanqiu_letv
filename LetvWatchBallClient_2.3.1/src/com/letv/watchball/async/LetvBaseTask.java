package com.letv.watchball.async;

public interface LetvBaseTask {

	/**
	 * 任务执行
	 * */
	public boolean run();
	
	/**
	 * 取消任务
	 * */
	public void cancel();

	/**
	 * 是否取消
	 * */
	public boolean isCancelled();
}
