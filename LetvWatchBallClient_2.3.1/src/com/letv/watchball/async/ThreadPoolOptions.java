package com.letv.watchball.async;

/**
 * 线程池配置类
 * */
public class ThreadPoolOptions {

	/**
	 * 线程池数量
	 * */
	private int size;

	/**
	 * 线程池内线程的级别
	 * */
	private int priority;

	/**
	 * 线程等待时间
	 * */
	private int waitPeriod;

	/**
	 * 是否重新执行失败的任务
	 * */
	private boolean isReplayFailTask;

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getWaitPeriod() {
		return waitPeriod;
	}

	public void setWaitPeriod(int waitPeriod) {
		this.waitPeriod = waitPeriod;
	}

	public boolean isReplayFailTask() {
		return isReplayFailTask;
	}

	public void setReplayFailTask(boolean isReplayFailTask) {
		this.isReplayFailTask = isReplayFailTask;
	}
}
