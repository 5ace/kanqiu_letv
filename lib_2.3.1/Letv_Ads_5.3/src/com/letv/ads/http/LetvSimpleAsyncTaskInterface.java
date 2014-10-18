package com.letv.ads.http;


public interface LetvSimpleAsyncTaskInterface <T>{

	/**
	 * 异步任务开始前
	 * */
	public void onPreExecute();
	
	/**
	 * 异步任务执行
	 * */
	public T doInBackground();
	
	/**
	 * 异步任务完成
	 * */
	public void onPostExecute(T result);
	
}
