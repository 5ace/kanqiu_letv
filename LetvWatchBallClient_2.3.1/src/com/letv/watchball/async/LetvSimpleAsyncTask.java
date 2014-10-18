package com.letv.watchball.async;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.letv.watchball.R;
import com.letv.watchball.view.LoadingDialog;

/**
 * 普通异步任务，用来做查询数据库或者读取本地文件并需要更新UI的操作
 * */
public abstract class LetvSimpleAsyncTask<T> extends LetvBaseTaskImpl implements LetvSimpleAsyncTaskInterface<T> {

	protected Context context;

	private boolean dialog = true;

	private LoadingDialog loadingDialog;
	
	private Handler handler ;

	public LetvSimpleAsyncTask(Context context) {
		this.context = context;
		handler = new Handler(Looper.getMainLooper()) ;
	}

	public LetvSimpleAsyncTask(Context context, boolean dialog) {
		this.context = context;
		this.dialog = dialog;
		handler = new Handler(Looper.getMainLooper()) ;
	}

	@Override
	public final boolean run() {
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
							cancelDialog();
						}
					});
				}
			}
		} finally {
			cancelDialog();
		}
		return false;
	}
	
	@Override
	public boolean onPreExecute() {
		return true;
	}
	
	private void postUI(Runnable runnable){
		if(Thread.currentThread() != Looper.getMainLooper().getThread()){
			handler.post(runnable);
		}else{
			runnable.run() ;
		}
	}
	
	public synchronized void start() {
		postUI(new Runnable() {
			
			@Override
			public void run() {
				showDialog();
			}
		});
		mThreadPool.addNewTask(this);// 加入线程队列，等待执行
	}

	private void showDialog() {
		if (dialog && (loadingDialog == null || !loadingDialog.isShowing())) {
			loadingDialog = new LoadingDialog(context , R.string.dialog_loading);
			if (context instanceof Activity) {
				if (!((Activity)context).isFinishing() && !context.isRestricted()) {
					try {
						loadingDialog.show();
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private void cancelDialog(){
		if(loadingDialog != null && loadingDialog.isShowing()){
			postUI(new Runnable() {
				
				@Override
				public void run() {
					try {
						loadingDialog.dismiss();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
}
