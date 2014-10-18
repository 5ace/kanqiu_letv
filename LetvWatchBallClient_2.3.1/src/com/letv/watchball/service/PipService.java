package com.letv.watchball.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

import com.letv.watchball.R;
import com.letv.watchball.bean.Game;
import com.letv.watchball.utils.LetvConfiguration;
import com.letv.watchball.utils.LetvConstant;
import com.letv.watchball.utils.LetvUtil;
import com.letv.watchball.utils.LogInfo;
import com.letv.watchball.utils.TextUtil;
import com.letv.watchball.utils.UIs;
import com.letv.watchball.view.PipPlayerView;

/**
 * 隐藏/显示底部控制条的调用逻辑也在这个类中
 * @author 
 */
public class PipService extends Service {
	
	public static final int NOTIFICATION_ID = 109001;
	private static final Class<?>[] mSetForegroundSignature = new Class[] { boolean.class };
	private static final Class<?>[] mStartForegroundSignature = new Class[] { int.class, Notification.class };
	private static final Class<?>[] mStopForegroundSignature = new Class[] { boolean.class };
	
	private WindowManager wm = null;
	private WindowManager.LayoutParams wmParams = null;
	private PipPlayerView view;
	private float mTouchStartX;
	private float mTouchStartY;
	private float x;
	private float y;
	private int state;
	private float StartX;
	private float StartY;
	private int delaytime = 1000;
	/**
	 * 小窗的高度
	 */
	int mHeight = 0;

	private NotificationManager mNM;
	private Method mSetForeground;
	private Method mStartForeground;
	private Method mStopForeground;
	private Object[] mSetForegroundArgs = new Object[1];
	private Object[] mStartForegroundArgs = new Object[2];
	private Object[] mStopForegroundArgs = new Object[1];

	public static void launch(Context context, Bundle bundle){
		Intent intent = new Intent(context,PipService.class);
		intent.putExtra(LetvConstant.Intent.Bundle.PLAY,bundle);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(intent);
	}
	
	void invokeMethod(Method method, Object[] args) {
		try {
			method.invoke(this, args);
		} catch (InvocationTargetException e) {
			// Should not happen.
		} catch (IllegalAccessException e) {
			// Should not happen.
		}
	}

	/**
	 * This is a wrapper around the new startForeground method, using the older
	 * APIs if it is not available.
	 */
	void startForegroundCompat(int id, Notification notification) {
		// If we have the new startForeground API, then use it.
		if (mStartForeground != null) {
			mStartForegroundArgs[0] = Integer.valueOf(id);
			mStartForegroundArgs[1] = notification;
			invokeMethod(mStartForeground, mStartForegroundArgs);
			return;
		}

		// Fall back on the old API.
		mSetForegroundArgs[0] = Boolean.TRUE;
		invokeMethod(mSetForeground, mSetForegroundArgs);
		mNM.notify(id, notification);
	}

	/**
	 * This is a wrapper around the new stopForeground method, using the older
	 * APIs if it is not available.
	 */
	void stopForegroundCompat(int id) {
		// If we have the new stopForeground API, then use it.
		if (mStopForeground != null) {
			mStopForegroundArgs[0] = Boolean.TRUE;
			invokeMethod(mStopForeground, mStopForegroundArgs);
			return;
		}

		// Fall back on the old API. Note to cancel BEFORE changing the
		// foreground state, since we could be killed at that point.
		mNM.cancel(id);
		mSetForegroundArgs[0] = Boolean.FALSE;
		invokeMethod(mSetForeground, mSetForegroundArgs);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		//初始化艾瑞统计
//		IRMonitor.getInstance(this).Init(LetvConstant.MAPPTRACKERKEY, LetvUtil.generateDeviceId(this), LetvConfiguration.isDebug());
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		try {
			mStartForeground = getClass().getMethod("startForeground", mStartForegroundSignature);
			mStopForeground = getClass().getMethod("stopForeground", mStopForegroundSignature);
			adjustPermission();
			return;
		} catch (NoSuchMethodException e) {
			// Running on an older platform.
			mStartForeground = mStopForeground = null;
		}
		try {
			mSetForeground = getClass().getMethod("setForeground", mSetForegroundSignature);
			adjustPermission();
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException("OS doesn't have Service.startForeground OR Service.setForeground!");
		}

	}

	/**
	 * 提升服务权限
	 */
	private void adjustPermission() {
		// Notification notification = new Notification();
		// notification.tickerText = "画中画正在运行。";
		// notification.icon = R.drawable.icon;
		//
		// startForegroundCompat(NOTIFICATION_ID, notification);

		Notification notification = new Notification(R.drawable.notify_icon, TextUtil.getString(R.string.pipservice_title), System.currentTimeMillis());
		Intent notificationIntent = new Intent(this, PipService.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, TextUtil.getString(R.string.pipservice_title), TextUtil.getString(R.string.pipservice_msg), pendingIntent);
		startForegroundCompat(NOTIFICATION_ID, notification);
	}
	private void createView() {
		view = new PipPlayerView(this);
		// 获取WindowManager
		wm = (WindowManager) getApplicationContext().getSystemService("window");
		// 设置LayoutParams(全局变量）相关参数
		
		if(null == wmParams){
			wmParams = new WindowManager.LayoutParams();
			wmParams.type = 2002;
			wmParams.flags |= 8;
			wmParams.alpha = 1.0f;
			wmParams.gravity = Gravity.LEFT | Gravity.BOTTOM; // 调整悬浮窗口至左下角
			// 以屏幕左上角为原点，设置x、y初始值
			wmParams.x = 0;
			wmParams.y = 0;
			// 设置悬浮窗口长宽数据
			int width = LetvUtil.getDisplayWidth(getApplicationContext());
			int height = 29 * width / 48;
			mHeight = height;
			wmParams.width = width;
			wmParams.height = height;
			wmParams.format = 1;
		}

		wm.addView(view, wmParams);

		view.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				// 获取相对屏幕的坐标，即以屏幕左上角为原点
				x = event.getRawX();
				y = event.getRawY() - 25;
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					state = MotionEvent.ACTION_DOWN;
					StartX = x;
					StartY = y;
					// 获取相对View的坐标，即以此View左上角为原点
					mTouchStartX = event.getX();
					mTouchStartY = event.getY();
					break;
				case MotionEvent.ACTION_MOVE:
					state = MotionEvent.ACTION_MOVE;
					if (wannMove()) {
						updateViewPosition();
					}
					break;

				case MotionEvent.ACTION_UP:
					state = MotionEvent.ACTION_UP;
					if (!showControl()) {
						updateViewPosition();
					}
					mTouchStartX = mTouchStartY = 0;
					break;
				}
				return false;
			}
		});

	}

	/**
	 * 有效移动长度大于15时执行控件移动
	 */
	public static int MOVE_LENGTH = 15;

	public boolean wannMove() {
		if (Math.abs(x - StartX) < MOVE_LENGTH && Math.abs(y - StartY) < MOVE_LENGTH) {
			return false;
		}
		return true;
	}

	/**
	 * 显示控制条
	 * @return
	 */
	public boolean showControl() {
		if (Math.abs(x - StartX) < MOVE_LENGTH && Math.abs(y - StartY) < MOVE_LENGTH) {
			if (null == view || null == view.getPlayController() || null == view.getPlayController().getMediaController()) {
				return true;
			}
			if (view.getPlayController().getMediaController().isShowing()) {
				view.getPlayController().getMediaController().hide();
			} else {
				view.getPlayController().getMediaController().show();
			}
			return true;
		}
		return false;
	}

	private void updateViewPosition() {
		// 更新浮动窗口位置参数
		wmParams.x = (int) (x - mTouchStartX);
		wmParams.y = (int) (UIs.getScreenHeight() - (y - mTouchStartY) - mHeight);
		wm.updateViewLayout(view, wmParams);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (null == intent) {
			stopSelf();
			return;
		}
		if(null != wm && null != view){
			try{
				view.finish();
				wm.removeView(view);
			}catch (Exception e) {
				LogInfo.log("zlb", "PipService_onStart_wm.removeView-error = " + e.toString());
			}
			view = null;
		}
		createView();
		Bundle bundle = intent.getBundleExtra(LetvConstant.Intent.Bundle.PLAY);
		view.launch(bundle);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (null != wm && null != view) {
			view.finish();
			wm.removeView(view);
		}
		stopForegroundCompat(NOTIFICATION_ID);
		System.exit(0);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

      public static Game game = null;

}
