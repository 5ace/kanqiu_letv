package com.letv.watchball.async;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;

import com.letv.http.bean.LetvBaseBean;
import com.letv.http.bean.LetvDataHull;
import com.letv.watchball.R;
import com.letv.watchball.utils.LetvUtil;
import com.letv.watchball.utils.NetWorkTypeUtils;
import com.letv.watchball.utils.UIs;
import com.letv.watchball.view.LoadingDialog;

/**
 * 网络请求的异步任务
 * */
public abstract class LetvHttpAsyncTask<T extends LetvBaseBean> extends
		LetvBaseTaskImpl implements LetvHttpAsyncTaskInterface<T> {

	protected Context context;

	private boolean dialog = false;

	private LoadingDialog loadingDialog;

	protected boolean isCancel = false;

	private Handler handler;

	private Timer timer;

	private boolean dialogcancelable = true;

	private String message;
	private boolean isLocalSucceed = false;

	public LetvHttpAsyncTask(Context context) {
		this.context = context;
		handler = new Handler(Looper.getMainLooper());
		timer = new Timer();
	}

	public LetvHttpAsyncTask(Context context, boolean dialog) {
		this.context = context;
		this.dialog = dialog;
		handler = new Handler(Looper.getMainLooper());
		timer = new Timer();
	}

	public final boolean run() {
		try {
			if (!isCancel) {// 加载本地数据
				final T t = loadLocalData();

				if (t != null) {
					isLocalSucceed = true;
					postUI(new Runnable() {

						@Override
						public void run() {
							loadLocalDataComplete(t);
						}
					});

				}
			}

			boolean hasNet = LetvUtil.hasNet();

			if (!hasNet) {// 判断网络
				cancel();
				postUI(new Runnable() {

					@Override
					public void run() {
						// if (!isLocalSucceed) {
						netNull();
						// }
					}
				});

				return true;
			}

			postUI(new Runnable() {

				@Override
				public void run() {
					showDialog();
				}
			});

			if (!isCancel) {// 加载网络数据
				final LetvDataHull<T> dataHull = doInBackground();

				if (!isCancel) {
					postUI(new Runnable() {

						@Override
						public void run() {
							try {
								isCancel = true;
								cancel(true);
								if (dataHull == null) {
									if (!isLocalSucceed)
										netErr(0, null);
								} else {
									cancel(true);
									message = dataHull.getMessage();
									if (dataHull.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
										onPostExecute(dataHull.getUpdataId(),
												dataHull.getDataEntity());

									} else if (dataHull.getDataType() == LetvDataHull.DataType.DATA_CAN_NOT_PARSE) {
										if (!isLocalSucceed)
											dataNull(dataHull.getUpdataId(),
													message);
									}/*
									 * else if (dataHull.getDataType() ==
									 * LetvDataHull.DataType.DATA_NO_UPDATE) {
									 * if (!isLocalSucceed) noUpdate(); }
									 */else {
										if (!isLocalSucceed)
											netErr(dataHull.getUpdataId(),
													message);
									}
								}
								cancel();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			postUI(new Runnable() {

				@Override
				public void run() {
					netErr(0, null);
				}
			});
		}

		return true;
	}

	public void cancel(boolean isCancel) {
		cancelDialog();
		this.isCancel = isCancel;
		if (timer != null) {
			timer.purge();
			timer.cancel();
		}
	}

	public boolean isCancelled() {
		return this.isCancel;
	}

	@Override
	public boolean onPreExecute() {
		return true;
	}

	private void postUI(Runnable runnable) {
		if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
			handler.post(runnable);
		} else {
			runnable.run();
		}
	}

	public String getMessage() {
		return message;
	}

	public final void start() {
		isCancel = !onPreExecute();
		if (isCancel) {
			postUI(new Runnable() {

				@Override
				public void run() {
					preFail();
				}
			});
		}
		mThreadPool.addNewTask(this);// 加入线程队列，等待执行

		if (dialog) {
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					if (!LetvHttpAsyncTask.this.isCancel) {
						handler.post(new Runnable() {

							@Override
							public void run() {

								NetworkInfo networkInfo = NetWorkTypeUtils
										.getAvailableNetWorkInfo();

								if (networkInfo == null) {

									UIs.notifyShortNormal(context,
											R.string.toast_net_15);
								}
								netErr(0, null);
							}
						});
						LetvHttpAsyncTask.this.cancel(true);
					}
				}
			}, 15 * 1000);
		}

	}

	private void showDialog() {
		if (dialog) {
			if (loadingDialog == null || !loadingDialog.isShowing()) {
				if (context instanceof Activity) {
					if (!((Activity) context).isFinishing()
							&& !context.isRestricted()) {
						try {
							loadingDialog = new LoadingDialog(context,
									R.string.dialog_loading);
							loadingDialog.setCancelable(dialogcancelable);
							loadingDialog.show();
							handler.postDelayed(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									loadingDialog.dismiss();
								}
							}, 1500);

							loadingDialog
									.setOnDismissListener(new OnDismissListener() {
										@Override
										public void onDismiss(
												DialogInterface dialog) {
											/**
											 * 取消loading的逻辑修改：数据在加载时，
											 * 点按物理返回键，只用于返回上一级页面，不用于取消数据加载；
											 */
											LetvHttpAsyncTask.this
													.cancel(false);
										}
									});
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	public void cancelDialog() {
		if (loadingDialog != null && loadingDialog.isShowing()) {
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

	/**
	 * 设置对话框是否可以按返回取消
	 * 
	 * @param cancelable
	 */
	public void setDialogCancelable(boolean cancelable) {
		dialogcancelable = cancelable;
	}

	public void preFail() {
	}

	public void netNull() {
		UIs.notifyShortNormal(context, R.string.toast_net_null);
	};

	public void netErr(int updateId, String errMsg) {
		// UIs.notifyShortNormal(context, R.string.toast_net_err);
	};

	public void dataNull(int updateId, String errMsg) {
	};

	/**
	 * 数据无更新，回调
	 * */
	public void noUpdate() {
	}

	/**
	 * 加载本地内容
	 * */
	public T loadLocalData() {
		return null;
	}

	/**
	 * 加载本地内容完成后，回调
	 * */
	public boolean loadLocalDataComplete(T t) {
		return false;
	}

	/**
	 * 本地数据是否加载成功
	 * */
	public boolean isLocalSucceed() {
		return isLocalSucceed;
	}
}
