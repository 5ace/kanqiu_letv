package com.letv.watchball.async;

/**
 * 异步任务接口
 * */
public interface LetvSimpleAsyncTaskInterface <D>{

	/**
	 * 异步任务开始前
	 * */
	public boolean onPreExecute();
	
	/**
	 * 异步任务执行
	 * */
	public D doInBackground();
	
	/**
	 * 异步任务完成
	 * */
	public void onPostExecute(D result);
	
}
