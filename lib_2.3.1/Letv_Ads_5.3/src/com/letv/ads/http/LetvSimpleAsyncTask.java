package com.letv.ads.http;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

/**
 * 普通异步任务，用来做查询数据库或者读取本地文件并需要更新UI的操作
 * */
public abstract class LetvSimpleAsyncTask<T> extends Thread implements LetvSimpleAsyncTaskInterface<T> {

	protected Context context;

	private Handler handler ;
	
	private boolean isCancel = false;

	public boolean isCancel() {
		return isCancel;
	}

	public void cancel(boolean isCancel) {
		this.isCancel = isCancel;
	}

	public LetvSimpleAsyncTask(Context context) {
		this.context = context;
		handler = new Handler(Looper.getMainLooper()) ;
	}

	@Override
	public final void run() {
		try {
			postUI(new Runnable() {
				@Override
				public void run() {
					onPreExecute();
				}
			});
			if(!isCancel){
				final T result = doInBackground();
				if(!isCancel){
					postUI(new Runnable() {
						@Override
						public void run() {
							if(!isCancel){
								onPostExecute(result);
							}
						}
					});
				}
			}
		} finally {
		}
	}
	
	private void postUI(Runnable runnable){
		if(Thread.currentThread() != Looper.getMainLooper().getThread()){
			handler.post(runnable);
		}else{
			runnable.run() ;
		}
	}
}
