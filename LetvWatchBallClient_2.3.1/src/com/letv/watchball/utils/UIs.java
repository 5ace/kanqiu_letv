package com.letv.watchball.utils;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.watchball.LetvApplication;
import com.letv.watchball.R;
import com.letv.watchball.bean.MessageBean;
import com.letv.watchball.db.DBManager;
import com.letv.watchball.view.PublicLoadLayout;

public class UIs {

	private static LetvToast mLetvToast = null;

	private static Toast mToast = null;

	/**
	 * 创建一个公共的加载布局
	 * */
	public static PublicLoadLayout createPage(Context context, int layoutId) {

		PublicLoadLayout rootView = new PublicLoadLayout(context);
		rootView.addContent(layoutId);

		return rootView;
	}
	/**
	 * 根据资源ID得到View
	 * */
	public static View inflate(Context context, int resource, ViewGroup root, boolean attachToRoot) {
		return LayoutInflater.from(context).inflate(resource, root, attachToRoot);
	}

	/**
	 * 根据资源ID得到View
	 * */
	public static View inflate(Context context, int resource, ViewGroup root) {
		return LayoutInflater.from(context).inflate(resource, root);
	}

	/**
	 * 根据资源ID得到View
	 * */
	public static View inflate(LayoutInflater inflater, int resource, ViewGroup root, boolean attachToRoot) {
		return inflater.inflate(resource, root, attachToRoot);
	}

	/**
	 * 将一倍尺寸缩放到当前屏幕大小的尺寸（宽）
	 * */
	public static int zoomWidth(int w) {
		int sw = 0;
		sw = getScreenWidth();

		return (int) (w * sw / 320f + 0.5f);
	}

	/**
	 * 将一倍尺寸缩放到当前屏幕大小的尺寸（高）
	 * */
	public static int zoomHeight(int h) {
		int sh = 0;
		sh = getScreenHeight();

		return (int) (h * sh / 480f + 0.5f);
	}

	/**
	 * 缩放控件
	 * */
	public static void zoomView(int w, int h, View view) {
		if (view == null) {
			return;
		}

		LayoutParams params = view.getLayoutParams();

		if (params == null) {
			return;
		}

		params.width = zoomWidth(w);
		params.height = zoomHeight(h);
	}

	/**
	 * 缩放控件
	 * */
	public static void zoomViewHeight(int h, View view) {
		if (view == null) {
			return;
		}

		LayoutParams params = view.getLayoutParams();

		if (params == null) {
			return;
		}

		params.height = zoomWidth(h);
	}

	/**
	 * 缩放控件
	 * */
	public static void zoomViewWidth(int w, View view) {
		if (view == null) {
			return;
		}

		LayoutParams params = view.getLayoutParams();

		if (params == null) {
			return;
		}

		params.width = zoomWidth(w);
	}

	/**
	 * 缩放控件
	 * */
	public static void zoomViewFull(View view) {
		if (view == null) {
			return;
		}

		LayoutParams params = view.getLayoutParams();

		if (params == null) {
			return;
		}

		params.width = getScreenWidth();
		params.height = getScreenHeight();
	}

	/**
	 * dip转px
	 * */
	public static int dipToPx(int dipValue) {
		final float scale = LetvApplication.getInstance().getResources().getDisplayMetrics().density;
		int pxValue = (int) (dipValue * scale + 0.5f);

		return pxValue;
	}

	/**
	 * px转dip
	 * */
	public static float dipToPxFloat(int dipValue) {
		final float scale = LetvConstant.Global.displayMetrics.density;
		float pxValue = dipValue * scale;

		return pxValue;
	}

	/**
	 * 获取顶部状态栏高度
	 * 
	 * @param act
	 * @return
	 */
	public static int getStatusBarHeight(Activity act) {
		Rect frame = new Rect();
		act.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		return frame.top;
	}

	/**
	 * 获取标题栏高度
	 * 
	 * @param act
	 * @return
	 */
	public static int getTitleBarHeight(Activity act) {
		int contentTop = act.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
		int titleBarHeight = contentTop - getStatusBarHeight(act);
		return titleBarHeight;
	}

	/**
	 * 获取内容区域高度
	 * 
	 * @param act
	 * @return
	 */
	public static int getContentHeight(Activity act) {
		int contentTop = act.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
		return (getScreenHeight() - contentTop);
	}

	/**
	 * 得到屏幕宽度
	 * */
	public static int getScreenWidth() {
		return ((WindowManager) LetvApplication.getInstance().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
	}

	/**
	 * 得到屏幕高度
	 * */
	public static int getScreenHeight() {
		return ((WindowManager) LetvApplication.getInstance().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
	}

	/**
	 * 判断是否是横屏
	 * */
	public static boolean isLandscape(Activity activity) {

		int t = activity.getResources().getConfiguration().orientation;
		if (t == Configuration.ORIENTATION_LANDSCAPE) {
			return true;
		}

		return false;
	}

	/**
	 * 全屏
	 * */
	public static void fullScreen(Activity activity) {
		activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	/**
	 * 隐藏Title
	 * */
	public static void notFullScreen(Activity activity) {
		activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	/**
	 * 设置为横屏
	 * */
	public static void screenLandscape(Activity activity) {
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	/**
	 * 设置为竖屏
	 * */
	public static void screenPortrait(Activity activity) {
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	public static void notifyShort(Context context, int textId) {
		if (mLetvToast == null && LetvApplication.getInstance() != null) {
			mLetvToast = new LetvToast(LetvApplication.getInstance());
			mLetvToast.setDuration(Toast.LENGTH_SHORT);
		}

		mLetvToast.setErr(false);
		mLetvToast.setMsg(textId);
		mLetvToast.show();
	}

	public static void notifyShort(Context context, String text) {
		if (mLetvToast == null && LetvApplication.getInstance() != null) {
			mLetvToast = new LetvToast(LetvApplication.getInstance());
			mLetvToast.setDuration(Toast.LENGTH_SHORT);
		}
		mLetvToast.setErr(false);
		mLetvToast.setMsg(text);
		mLetvToast.show();
	}

	public static void notifyErrShort(Context context, String text) {
		if (mLetvToast == null && LetvApplication.getInstance() != null) {
			mLetvToast = new LetvToast(LetvApplication.getInstance());
			mLetvToast.setDuration(Toast.LENGTH_SHORT);
		}

		mLetvToast.setErr(true);
		mLetvToast.setMsg(text);
		mLetvToast.show();
	}

	public static void notifyErrShort(Context context, int textId) {
		if (mLetvToast == null && LetvApplication.getInstance() != null) {
			mLetvToast = new LetvToast(LetvApplication.getInstance());
			mLetvToast.setDuration(Toast.LENGTH_SHORT);
		}

		mLetvToast.setErr(true);
		mLetvToast.setMsg(textId);
		mLetvToast.show();
	}

	public static void notifyErrLong(Context context, String text) {
		if (mLetvToast == null && LetvApplication.getInstance() != null) {
			mLetvToast = new LetvToast(LetvApplication.getInstance());
			mLetvToast.setDuration(Toast.LENGTH_LONG);
		}

		mLetvToast.setErr(true);
		mLetvToast.setMsg(text);
		mLetvToast.show();
	}

	public static void notifyErrLong(Context context, int textId) {
		if (mLetvToast == null && LetvApplication.getInstance() != null) {
			mLetvToast = new LetvToast(LetvApplication.getInstance());
			mLetvToast.setDuration(Toast.LENGTH_LONG);
		}

		mLetvToast.setErr(true);
		mLetvToast.setMsg(textId);
		mLetvToast.show();
	}

	public static void notifyTiming(Context context, int msgId, int time) {
		if (mLetvToast == null && LetvApplication.getInstance() != null) {
			mLetvToast = new LetvToast(LetvApplication.getInstance());
			mLetvToast.setDuration(Toast.LENGTH_LONG);
		}

		mLetvToast.setErr(false);
		mLetvToast.setMsg(msgId);
		mLetvToast.show();

		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				mLetvToast.cancel();
			}
		};

		mLetvToast.show();
		handler.sendEmptyMessageDelayed(0, time);
	}

	public static void notifyTiming(Context context, String msg, int time) {

		if (mLetvToast == null && LetvApplication.getInstance() != null) {
			mLetvToast = new LetvToast(LetvApplication.getInstance());
			mLetvToast.setDuration(Toast.LENGTH_LONG);
		}

		mLetvToast.setErr(false);
		mLetvToast.setMsg(msg);
		mLetvToast.show();

		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				mLetvToast.cancel();
			}
		};

		mLetvToast.show();
		handler.sendEmptyMessageDelayed(0, time);
	}

	public static void cancelNotify() {
		if (mLetvToast != null) {
			mLetvToast.cancel();
		}
		if (mToast != null) {
			mToast.cancel();
		}
	}

	public static void notifyShortNormal(Context context, String text) {
		if (mToast == null && LetvApplication.getInstance() != null) {
			mToast = Toast.makeText(LetvApplication.getInstance(), "", Toast.LENGTH_SHORT);
		}

		mToast.setDuration(Toast.LENGTH_SHORT);
		mToast.setText(text);
		mToast.show();
	}

	public static void notifyShortNormal(Context context, int textId) {
		if (mToast == null && LetvApplication.getInstance() != null) {
			mToast = Toast.makeText(LetvApplication.getInstance(), "", Toast.LENGTH_SHORT);
		}

		mToast.setDuration(Toast.LENGTH_SHORT);
		mToast.setText(textId);
		mToast.show();
	}

	public static void notifyLongNormal(Context context, String text) {
		if (mToast == null && LetvApplication.getInstance() != null) {
			mToast = Toast.makeText(LetvApplication.getInstance(), "", Toast.LENGTH_SHORT);
		}

		mToast.setDuration(Toast.LENGTH_LONG);
		mToast.setText(text);
		mToast.show();
	}

	public static void notifyLongNormal(Context context, int textId) {
		if (mToast == null && LetvApplication.getInstance() != null) {
			mToast = Toast.makeText(LetvApplication.getInstance(), "", Toast.LENGTH_SHORT);
		}

		mToast.setDuration(Toast.LENGTH_LONG);
		mToast.setText(textId);
		mToast.show();
	}

	public static void changeTimeState(TextView v) {
		if (v != null) {
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.setTimeInMillis(System.currentTimeMillis());
			int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
			int minite = mCalendar.get(Calendar.MINUTE);
			v.setText(LetvUtil.getStringTwo(String.valueOf(hour)) + ":" + LetvUtil.getStringTwo(String.valueOf(minite)));
		}
	}

	public static void changeNetState(Context context, View v) {
		if (v != null) {
			switch (NetWorkTypeUtils.getNetType()) {
			case NetWorkTypeUtils.NETTYPE_NO:
				v.setBackgroundResource(R.drawable.net_no);
				break;
			case NetWorkTypeUtils.NETTYPE_WIFI:
				v.setBackgroundResource(R.drawable.net_wifi);
				break;
			case NetWorkTypeUtils.NETTYPE_2G:
				v.setBackgroundResource(R.drawable.net_2g);
				break;
			case NetWorkTypeUtils.NETTYPE_3G:
				v.setBackgroundResource(R.drawable.net_3g);
				break;
			default:
				break;
			}
		}
	}

	public static void changeBatteryState(int status, int value, View v) {
		if (v != null) {
			boolean isCharging = false;

			switch (status) {
			case BatteryManager.BATTERY_STATUS_CHARGING:// 充电状态
				isCharging = true;
				break;
			case BatteryManager.BATTERY_STATUS_DISCHARGING:// 放电状态
				break;
			case BatteryManager.BATTERY_STATUS_NOT_CHARGING:// 未充电
				break;
			case BatteryManager.BATTERY_STATUS_FULL:// 充满电
				break;
			case BatteryManager.BATTERY_STATUS_UNKNOWN:// 未知状态
				break;
			default:
				break;
			}

			if (isCharging) {
				v.setBackgroundResource(R.drawable.battery_charge);
			} else {
				if (value >= 80) {
					v.setBackgroundResource(R.drawable.battery5);
				} else if (value >= 60) {
					v.setBackgroundResource(R.drawable.battery4);
				} else if (value >= 40) {
					v.setBackgroundResource(R.drawable.battery3);
				} else if (value >= 20) {
					v.setBackgroundResource(R.drawable.battery2);
				} else if (value >= 0) {
					v.setBackgroundResource(R.drawable.battery1);
				}
			}
		}
	}

	/**
	 * 4.0 点击隐藏导航栏
	 * 
	 * @param activity
	 */
	public static void setFullscreenCompatibility(Activity activity) {
		// if ("Galaxy Nexus".equals(Build.MODEL.trim()));
		// try {
		// Object localObject1 = View.class.getDeclaredField(
		// "SYSTEM_UI_FLAG_HIDE_NAVIGATION").get(View.class);
		// Class[] localObject2 = new Class[1];
		// localObject2[0] = Integer.TYPE;
		// Method localMethod = View.class.getMethod("setSystemUiVisibility",
		// localObject2);
		// View view = activity.getWindow().getDecorView();
		// Object[] arrayOfObject = new Object[1];
		// arrayOfObject[0] = localObject1;
		// localMethod.invoke(view, arrayOfObject);
		// return;
		// } catch (InvocationTargetException localInvocationTargetException) {
		// } catch (NoSuchMethodException localNoSuchMethodException) {
		// } catch (IllegalAccessException localIllegalAccessException) {
		// } catch (NoSuchFieldException localNoSuchFieldException) {
		// } catch (IllegalArgumentException localIllegalArgumentException) {
		// } catch (SecurityException localSecurityException) {
		// } catch (Exception e) {
		// }
	}

	public static void changeSoundState(int value, int maxValue, ImageView v) {
		if (v != null) {
			if (value >= maxValue / 3 * 2) {
				v.setImageResource(R.drawable.sound_three);
			} else if (value >= maxValue / 3) {
				v.setImageResource(R.drawable.sound_two);
			} else if (value > 0) {
				v.setImageResource(R.drawable.sound_one);
			} else {
				v.setImageResource(R.drawable.sound_zero);
			}
		}
	}

	public static void call(Activity activity, int messageId, DialogInterface.OnClickListener yes) {

		if (activity == null) {
			return;
		}

		Dialog dialog = new AlertDialog.Builder(activity).setTitle(R.string.dialog_default_title).setIcon(R.drawable.dialog_icon).setMessage(messageId)
				.setPositiveButton(R.string.dialog_default_ok, yes).create();

		if (!activity.isFinishing() && !activity.isRestricted()) {
			dialog.show();
		}
	}

	public static boolean call(Activity activity, int messageId, int yes, int no, DialogInterface.OnClickListener yesListener,
			DialogInterface.OnClickListener noListener, View view, boolean cancelable) {

		if (activity == null) {
			return false;
		}

		Dialog dialog = new AlertDialog.Builder(activity).setTitle(R.string.dialog_default_title).setIcon(R.drawable.dialog_icon).setMessage(messageId)
				.setCancelable(cancelable).setView(view).setPositiveButton(yes, yesListener).setNegativeButton(no, noListener).create();

		if (!activity.isFinishing() && !activity.isRestricted()) {
			dialog.show();
			return true;
		}

		return false;
	}
	
	public static void call(Activity activity, int messageId,DialogInterface.OnClickListener yesListener,
			DialogInterface.OnClickListener noListener) {

		if (activity == null) {
			return;
		}

		Dialog dialog = new AlertDialog.Builder(activity).setTitle(R.string.dialog_default_title).setIcon(R.drawable.dialog_icon).setMessage(messageId)
				.setPositiveButton(R.string.dialog_default_ok, yesListener).setNegativeButton(R.string.dialog_default_no, noListener).create();

		if (!activity.isFinishing() && !activity.isRestricted()) {
			dialog.show();
		}

	}

	public static void call(Activity activity, int messageId, int yes, int no, DialogInterface.OnClickListener yesListener,
			DialogInterface.OnClickListener noListener) {

		if (activity == null) {
			return;
		}

		Dialog dialog = new AlertDialog.Builder(activity).setTitle(R.string.dialog_default_title).setIcon(R.drawable.dialog_icon).setMessage(messageId)
				.setPositiveButton(yes, yesListener).setNegativeButton(no, noListener).create();

		if (!activity.isFinishing() && !activity.isRestricted()) {
			dialog.show();
		}

	}

	public static void call(Activity activity, int title, int messageId, int yes, DialogInterface.OnClickListener yesListener, boolean cancelable) {

		if (activity == null) {
			return;
		}

		Dialog dialog = new AlertDialog.Builder(activity).setTitle(title).setIcon(R.drawable.dialog_icon).setMessage(messageId).setCancelable(cancelable)
				.setPositiveButton(yes, yesListener).create();

		if (!activity.isFinishing() && !activity.isRestricted()) {
			dialog.show();
		}
	}

	public static void call(Context activity, String title, String message, int yes, int no, DialogInterface.OnClickListener yesListener,
			DialogInterface.OnClickListener noListener, boolean cancelable) {

		if (activity == null) {
			return;
		}

		Dialog dialog = new AlertDialog.Builder(activity).setTitle(title).setIcon(R.drawable.dialog_icon).setMessage(message).setCancelable(cancelable)
				.setPositiveButton(yes, yesListener).setNegativeButton(no, noListener).create();
		if (activity instanceof Activity) {
			if (!((Activity) activity).isFinishing() && !activity.isRestricted()) {
				dialog.show();
			}
		}
	}
	
	//
	public static void call(Activity activity, String message, DialogInterface.OnClickListener yes) {

		if (activity == null) {
			return;
		}

		Dialog dialog = new AlertDialog.Builder(activity).setTitle(R.string.dialog_default_title).setIcon(R.drawable.dialog_icon).setMessage(message)
				.setPositiveButton(R.string.dialog_default_ok, yes).create();

		if (!activity.isFinishing() && !activity.isRestricted()) {
			dialog.show();
		}
	}

	//
	public static void call(Activity activity, String message, DialogInterface.OnClickListener yes, boolean cancelable) {

		if (activity == null) {
			return;
		}

		Dialog dialog = new AlertDialog.Builder(activity).setTitle(R.string.dialog_default_title).setIcon(R.drawable.dialog_icon).setMessage(message)
				.setPositiveButton(R.string.dialog_default_ok, yes).create();
		dialog.setCancelable(cancelable);
		if (!activity.isFinishing() && !activity.isRestricted()) {
			dialog.show();
		}
	}
	/**
	 * 弹出的dialog(只有一个确定键)
	 * 
	 * @param activity
	 * @param dialogTitle
	 *            弹框的title 用系统默认传-1
	 * @param dialogMsg
	 *            弹框的messaget
	 * @param yes
	 */
	public static void callDialogMsgPositiveButton(Activity activity, int dialogTitle, int dialogMsg, DialogInterface.OnClickListener yes) {
		String title = (dialogTitle == -1 ? null : activity.getResources().getString(dialogTitle));
		String message = activity.getResources().getString(dialogMsg);
		Dialog dialog = new AlertDialog.Builder(activity).setTitle(
		/* dialogMsgByMsg.title */(!"".equals(dialogTitle) && title != null) ? title : activity.getString(R.string.dialog_default_title))
				.setIcon(R.drawable.dialog_icon).setMessage(message).setPositiveButton(R.string.dialog_default_ok, yes).create();

		if (!activity.isFinishing() && !activity.isRestricted()) {
			dialog.show();
		}
	}

	/**
	 * 弹出的dialog(带两个Button)
	 * 
	 * @param activity
	 * @param dialogTitle
	 *            标题 使用默认值传 -1
	 * @param dialogMsg
	 *            内容信息
	 * @param yes
	 *            确定键显示的值，使用默认值传 -1
	 * @param yesBtnRes
	 * @param no
	 *            取消键显示的值，使用默认值传 -1
	 * @param noBtnRes
	 */
	public static void callDialogMsgPositiveNegtivButton(Activity activity, int dialogTitle, String dialogMsg, DialogInterface.OnClickListener yes,
			int yesBtnRes, DialogInterface.OnClickListener no, int noBtnRes) {
		String title = (dialogTitle == -1 ? null : activity.getResources().getString(dialogTitle));
		Dialog dialog = new AlertDialog.Builder(activity).setTitle(
		/* dialogMsgByMsg.title */(!"".equals(dialogTitle) && title != null) ? title : activity.getString(R.string.dialog_default_title))
				.setIcon(R.drawable.dialog_icon).setMessage(dialogMsg).setPositiveButton(-1 == yesBtnRes ? R.string.dialog_default_ok : yesBtnRes, yes)
				.setNegativeButton(-1 == noBtnRes ? R.string.dialog_default_no : noBtnRes, no).create();

		if (!activity.isFinishing() && !activity.isRestricted()) {
			dialog.show();
		}
	}
	

	// ========================================================================================================
	// // -----------------------** 客户端dialog数据服务端化-----START
	public static void callDialogMsgPositiveButton(Activity activity, String msgId, DialogInterface.OnClickListener yes) {
		MessageBean dialogMsgByMsg = DBManager.getInstance().getDialogMsgTrace().getDialogMsgByMsgId(msgId);
		if (activity == null || dialogMsgByMsg == null) {
			return;
		}
		Dialog dialog = new AlertDialog.Builder(activity)
				.setTitle(
						(!"".equals(dialogMsgByMsg.title) && dialogMsgByMsg.title != null) ? dialogMsgByMsg.title : activity
								.getString(R.string.dialog_default_title)).setIcon(R.drawable.dialog_icon).setMessage(dialogMsgByMsg.message)
				.setPositiveButton(R.string.dialog_default_ok, yes).create();

		if (!activity.isFinishing() && !activity.isRestricted()) {
			dialog.show();
		}
	}

	//
	public static void callDialogMsgPositiveButton(Activity activity, String msgId, int yes, DialogInterface.OnClickListener yesListener,
			DialogInterface.OnKeyListener onKeyListener) {
		MessageBean dialogMsgByMsg = DBManager.getInstance().getDialogMsgTrace().getDialogMsgByMsgId(msgId);
		if (activity == null || dialogMsgByMsg == null) {
			return;
		}
		Dialog dialog = new AlertDialog.Builder(activity)
				.setTitle(
						(!"".equals(dialogMsgByMsg.title) && dialogMsgByMsg.title != null) ? dialogMsgByMsg.title : activity
								.getString(R.string.dialog_default_title)).setOnKeyListener(onKeyListener).setIcon(R.drawable.dialog_icon)
				.setMessage(dialogMsgByMsg.message).setPositiveButton(yes, yesListener).create();

		if (!activity.isFinishing() && !activity.isRestricted()) {
			dialog.show();
		}
	}

	//
	public static void callDialogMsgPosNeg(Activity activity, String msgId, int yes, int no, DialogInterface.OnClickListener yesListener,
			DialogInterface.OnClickListener noListener) {
		MessageBean dialogMsgByMsg = DBManager.getInstance().getDialogMsgTrace().getDialogMsgByMsgId(msgId);

		if (activity == null || dialogMsgByMsg == null) {
			return;
		}

		Dialog dialog = new AlertDialog.Builder(activity)
				.setTitle(
						(!"".equals(dialogMsgByMsg.title) && dialogMsgByMsg.title != null) ? dialogMsgByMsg.title : activity
								.getString(R.string.dialog_default_title)).setIcon(R.drawable.dialog_icon).setMessage(dialogMsgByMsg.message)
				.setCancelable(false).setPositiveButton(yes, yesListener).setNegativeButton(no, noListener).create();

		if (!activity.isFinishing() && !activity.isRestricted()) {
			dialog.show();
		}
	}

	public static boolean callDialogMsgPosNeg(Activity activity, String msgId, int yes, int no, DialogInterface.OnClickListener yesListener,
			DialogInterface.OnClickListener noListener, View view, boolean cancelable) {
		MessageBean dialogMsgByMsg = DBManager.getInstance().getDialogMsgTrace().getDialogMsgByMsgId(msgId);

		if (activity == null || dialogMsgByMsg == null) {
			return false;
		}

		Dialog dialog = new AlertDialog.Builder(activity)
				.setTitle(
						(!"".equals(dialogMsgByMsg.title) && dialogMsgByMsg.title != null) ? dialogMsgByMsg.title : activity
								.getString(R.string.dialog_default_title)).setView(view).setCancelable(cancelable).setIcon(R.drawable.dialog_icon)
				.setMessage(dialogMsgByMsg.message).setPositiveButton(yes, yesListener).setNegativeButton(no, noListener).create();

		if (!activity.isFinishing() && !activity.isRestricted()) {
			dialog.show();
			return true;
		}
		return false;
	}

	//
	public static void callDialogMsgPosNegDefault(Activity activity, String msgId, DialogInterface.OnClickListener yes, DialogInterface.OnClickListener no) {

		MessageBean dialogMsgByMsg = DBManager.getInstance().getDialogMsgTrace().getDialogMsgByMsgId(msgId);
		if (activity == null || dialogMsgByMsg == null) {
			return;
		}
		Dialog dialog = new AlertDialog.Builder(activity)
				.setTitle(
						(!"".equals(dialogMsgByMsg.title) && dialogMsgByMsg.title != null) ? dialogMsgByMsg.title : activity
								.getString(R.string.dialog_default_title)).setIcon(R.drawable.dialog_icon).setMessage(dialogMsgByMsg.message)
				.setPositiveButton(R.string.dialog_default_ok, yes).setNegativeButton(R.string.dialog_default_no, no).create();

		if (!activity.isFinishing() && !activity.isRestricted()) {
			try {
				dialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	// public static void callDialogMsgPosNegCancel(Activity activity, String
	// msgId, int yes, int no,
	// DialogInterface.OnClickListener yesListener,
	// DialogInterface.OnClickListener noListener, boolean cancelable, String...
	// messageWhichs) {
	// Dialog dialog = null;
	// MessageBean dialogMsgByMsg = DBManager.getInstance().getDialogMsgTrace()
	// .getDialogMsgByMsgId(msgId);
	// dialog = new AlertDialog.Builder(activity)
	// .setTitle(activity.getString(R.string.dialog_default_title))
	// .setMessage(
	// activity.getString(R.string.share_binding) + messageWhichs[0]
	// + activity.getString(R.string.share_remove_binding_dialog)
	// + messageWhichs[0] + "。").setCancelable(cancelable)
	// .setPositiveButton(no, noListener).setNegativeButton(yes,
	// yesListener).create();
	// if (!activity.isFinishing() && !activity.isRestricted()) {
	// dialog.show();
	// }
	// }
	public static void callDialogMsgPosNegCancel(Activity activity, String msgId, int yes, int no, DialogInterface.OnClickListener yesListener,
			DialogInterface.OnClickListener noListener, boolean cancelable, String... messageWhichs) {
		Dialog dialog = null;
		MessageBean dialogMsgByMsg = DBManager.getInstance().getDialogMsgTrace().getDialogMsgByMsgId(msgId);
		if (activity == null || dialogMsgByMsg == null) {
			return;
		}
		if (dialogMsgByMsg.message.contains("%1$s")) {

			dialog = new AlertDialog.Builder(activity)
					.setTitle(
							/* dialogMsgByMsg.title */(!"".equals(dialogMsgByMsg.title) && dialogMsgByMsg.title != null) ? dialogMsgByMsg.title : activity
									.getString(R.string.dialog_default_title)).setMessage(String.format(dialogMsgByMsg.message, messageWhichs))
					.setCancelable(cancelable).setPositiveButton(yes, yesListener).setNegativeButton(no, noListener).create();
		} else {
			dialog = new AlertDialog.Builder(activity)
					.setTitle(
							/* dialogMsgByMsg.title */(!"".equals(dialogMsgByMsg.title) && dialogMsgByMsg.title != null) ? dialogMsgByMsg.title : activity
									.getString(R.string.dialog_default_title)).setMessage(dialogMsgByMsg.message).setCancelable(cancelable)
					.setPositiveButton(yes, yesListener).setNegativeButton(no, noListener).create();

		}

		if (!activity.isFinishing() && !activity.isRestricted()) {
			dialog.show();
		}
	}

	/**
	 * 显示小提示
	 */
	public static void showToast(String text) {
		if (mToast != null) {
			mToast.cancel();
		}
		mToast = Toast.makeText(LetvApplication.getInstance(), text, Toast.LENGTH_SHORT);
		mToast.setText(text);
		mToast.setDuration(Toast.LENGTH_SHORT);
		mToast.show();
	}

	public static void showToast(int txtId) {
		if (mToast != null) {
			mToast.cancel();
		}
		mToast = Toast.makeText(LetvApplication.getInstance(), txtId, Toast.LENGTH_SHORT);
		mToast.setText(txtId);
		mToast.setGravity(Gravity.CENTER, 0, 0);
		mToast.setDuration(Toast.LENGTH_SHORT);
		mToast.show();
	}
	
	public static void notifyLong(Context context, String text) {
		if (LetvApplication.getInstance() != null) {
			mLetvToast = new LetvToast(LetvApplication.getInstance());
			mLetvToast.setDuration(Toast.LENGTH_LONG);
		}

		mLetvToast.setErr(false);
		mLetvToast.setMsg(text);
		mLetvToast.show();
	}

	public static void notifyLong(Context context, int textId) {
		if (LetvApplication.getInstance() != null) {
			mLetvToast = new LetvToast(LetvApplication.getInstance());
			mLetvToast.setDuration(Toast.LENGTH_LONG);
		}

		mLetvToast.setErr(false);
		mLetvToast.setMsg(textId);
		mLetvToast.show();
	}
	
	/**
	 * public static void showToast(int txtId) { mToast =
	 * Toast.makeText(LetvApplication.getInstance(), txtId, Toast.LENGTH_SHORT);
	 * mToast.setText(txtId); mToast.setGravity(Gravity.CENTER, 0, 0);
	 * mToast.setDuration(Toast.LENGTH_SHORT); mToast.show(); }
	 * 
	 * /** 隐藏键盘输入法
	 * 
	 * @param mActivity
	 */
	public static void hideSoftkeyboard(Activity mActivity) {
		if (null != mActivity && null != mActivity.getCurrentFocus()) {
			InputMethodManager mInputMethodManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (null != mInputMethodManager) {
				mInputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
	}
	
	/**
	 * 加载资源文件图片
	 * */
	public static Drawable loadResourcesDrawable(Context context, int resourcesId) {
		return Drawable.createFromStream(context.getResources().openRawResource(resourcesId), null);
	}

	/**
	 * 加载资源文件图片
	 * */
	public static Bitmap loadResourcesBitmap(Context context, int resourcesId) {
		return BitmapFactory.decodeStream(context.getResources().openRawResource(resourcesId));
	}

	/**
	 * 加载资源文件图片
	 * */
	public static void recycleBitmap(Bitmap bitmap) {
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
		}
		bitmap = null;
	}

	/**
	 * 加载资源文件图片
	 * */
	public static void recycleDrawable(Drawable drawable) {
		if (drawable != null) {
			drawable.setCallback(null);
		}
		drawable = null;
	}

}
