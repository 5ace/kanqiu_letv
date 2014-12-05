package com.letv.watchball.async;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;

import android.text.TextUtils;
import com.letv.datastatistics.entity.UpgradeInfo;
import com.letv.watchball.LetvApplication;
import com.letv.watchball.R;
import com.letv.watchball.activity.MainActivity;
import com.letv.watchball.activity.SettingsActivity;
import com.letv.watchball.utils.TextUtil;

public class UpdateDownloadAsyncTask extends LetvAsyncTask<Integer, Void> {

	public final static int SETUP_STATE = 100;
	private Handler mainHandler;
	private String upType;
	private Activity activity;
	private String url;
	public int startPosition = 0;
	public int progress;
	private int oldProgress;
	private String appName = "LetvAndroidChient" + ".apk";;
	private String path;
	private boolean flag = true;
	private String newV;
	private ProgressDialog mProgressDialog = null;

	private Notification updateNotification;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if (forseUpdate) {
					if (null != mProgressDialog)
						mProgressDialog.setProgress(progress);
				} else {
					updateNotification.setLatestEventInfo(activity, notiTitle,
							progress + "%", updatePendingIntent);
					updateNotificationManager.notify(NOTIFY_ID,
							updateNotification);
				}

				break;
			case 1:
				if (forseUpdate) {
					if (null != mProgressDialog)
						mProgressDialog.dismiss();

					mainHandler.sendEmptyMessage(0);
				} else {
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					String type = "application/vnd.android.package-archive";
					File file = new File(path + "/" + appName);
					intent.setDataAndType(Uri.fromFile(file), type);
					updatePendingIntent = PendingIntent.getActivity(activity,
							0, intent, 0);

					updateNotification.defaults = Notification.DEFAULT_SOUND;// 铃声提醒
					updateNotification.setLatestEventInfo(activity, "看球",
							"下载完成,点击安装。", updatePendingIntent);
					updateNotificationManager.notify(NOTIFY_ID,
							updateNotification);
				}

				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				String type = "application/vnd.android.package-archive";
				File file = new File(path + "/" + appName);
				intent.setDataAndType(Uri.fromFile(file), type);
				activity.startActivityForResult(intent, SETUP_STATE);

				break;
			}
		};
	};
	private boolean forseUpdate;
	private static final int NOTIFY_ID = 0;
	private NotificationManager updateNotificationManager;
	private PendingIntent updatePendingIntent;
	String notiTitle = "看球正在更新";

	public UpdateDownloadAsyncTask(Activity activity, String url, String newV,
			String upType) throws UpdataAppException {
		if (url == null || url.length() <= 0 || activity == null) {
			throw new UpdataAppException();
		}
		this.url = url;
		this.newV = newV;
		this.activity = activity;
		this.upType = upType;
	}

	public UpdateDownloadAsyncTask(SettingsActivity settingsActivity,
			UpgradeInfo result) throws UpdataAppException {
		this(settingsActivity, result.getUrl(), result.getV(), result
				.getUptype());
	}

	public UpdateDownloadAsyncTask(MainActivity mainActivity, String url,
			String v, String uptype, Handler mHandler)
			throws UpdataAppException {
		this(mainActivity, url, v, uptype);
		mainHandler = mHandler;
	}

	@Override
	protected Void doInBackground() {

		URL url = null;
		HttpURLConnection httpURLConnection = null;
		InputStream inputStream = null;
		FileOutputStream outputStream = null;

		/**
		 * 如果有SD卡就保存在SD卡，否则保存到内存中
		 * */
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			path = LetvApplication
					.getInstance()
					.getDir("updata",
							Context.MODE_WORLD_WRITEABLE
									| Context.MODE_WORLD_READABLE
									| Context.MODE_PRIVATE).getPath();
		} else {
			path = Environment.getExternalStorageDirectory().getPath();
		}

		int length = 0;
		try {
			url = new URL(this.url);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setAllowUserInteraction(true);

			length = httpURLConnection.getContentLength();

			inputStream = httpURLConnection.getInputStream();
			File file = new File(path + "/" + appName);
			if (file.exists() && file.isFile()) {
				file.delete();
			}
			outputStream = new FileOutputStream(file);
			// outputStream.seek(startPosition);

			byte[] buf = new byte[1024 * 10];
			int read = 0;
			int curSize = startPosition;

			while (flag) {
				publishProgress(progress);

				read = inputStream.read(buf);
				if (read == -1) {
					publishProgress(progress);
					break;
				}

				outputStream.write(buf, 0, read);
				curSize = curSize + read;

				progress = (curSize * 100 / length);

				if (curSize == length) {
					progress = 100;
					publishProgress(progress);
					break;
				}
			}
			inputStream.close();
			outputStream.close();
			httpURLConnection.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void close() {
		flag = false;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		SettingsActivity.setUpDateIsRunning(false);
		// 系统权限写入
		if (progress == 100) {
			String[] command = { "chmod", "777", path + "/" + appName };
			ProcessBuilder builder = new ProcessBuilder(command);
			try {
				builder.start();
			} catch (IOException e) {
				e.printStackTrace();
			}

			handler.sendEmptyMessage(1);
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		SettingsActivity.setUpDateIsRunning(true);
		forseUpdate = !TextUtils.isEmpty(upType)
				&& upType.equals(UpgradeInfo.UPTYPE_FORCE);
		if (forseUpdate) {
			if (null != mProgressDialog) {
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
			mProgressDialog = new ProgressDialog(activity);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.setTitle(R.string.dialog_default_title);
			mProgressDialog.setIcon(R.drawable.dialog_icon);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setMax(100);
			mProgressDialog.setMessage(TextUtil.text(
					R.string.updatedownloadasynctask_download, newV));
			mProgressDialog.show();
		} else {
			updateNotificationManager = (NotificationManager) activity
					.getSystemService(Context.NOTIFICATION_SERVICE);
			Intent intent = new Intent(activity, activity.getClass());
			updatePendingIntent = PendingIntent.getActivity(activity, 0,
					intent, 0);
			updateNotification = new Notification();
			updateNotification.icon = R.drawable.notify_icon;
			updateNotification.tickerText = notiTitle;
			updateNotification.flags = Notification.FLAG_AUTO_CANCEL;
			updateNotification.setLatestEventInfo(activity, notiTitle, "0%",
					updatePendingIntent);
			updateNotificationManager.notify(NOTIFY_ID, updateNotification);
		}
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		if (progress > oldProgress) {
			oldProgress = progress;
			handler.sendEmptyMessage(0);
		}
		super.onProgressUpdate(values);
	};

	public static class UpdataAppException extends Exception {
		private static final long serialVersionUID = 1L;

		@Override
		public void printStackTrace() {
			System.err.println("Updata fail , parameters not initialized");
		}
	}
}
