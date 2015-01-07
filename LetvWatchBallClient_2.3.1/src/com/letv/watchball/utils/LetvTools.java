package com.letv.watchball.utils;

import java.util.Formatter;
import java.util.Locale;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.WindowManager;

import com.letv.watchball.LetvApplication;
import com.letv.watchball.bean.AlbumNew;

public class LetvTools {

	/**
	 * 生存videofile接口的key
	 * */
	public static String generateVideoFileKey(String mid, String tm) {
		StringBuilder builder = new StringBuilder();
		builder.append(mid);
		builder.append(",");
		builder.append(tm);
		builder.append(",");
		builder.append("bh65OzqYYYmHRQ");
		return MD5.toMd5(builder.toString());
	}

	/**
	 * 生成直播加密的key
	 * */
	public static String generateLiveEncryptKey(String streamId, String tm) {
		StringBuilder builder = new StringBuilder();
		builder.append(streamId);
		builder.append(",");
		builder.append(tm);
		builder.append(",");
		builder.append(LetvConstant.MIYUE);
		return MD5.toMd5(builder.toString());
	}

	/**
	 * 是否有网络
	 * */
	public static boolean hasNet() {
		NetworkInfo networkInfo = NetWorkTypeUtils.getAvailableNetWorkInfo();

		if (networkInfo == null) {
			return false;
		}
		return true;
	}

	/**
	 * 关闭数据库Cursor对象
	 */
	public static void closeCursor(Cursor cursor) {
		if (null != cursor) {
			if (!cursor.isClosed()) {
				cursor.close();
			}
		}
	}

	/**
	 * 将long型时间（单位：秒）转换为23:12:22形式
	 * */
	public static String getNumberTime(long time_second) {

		Formatter formatter = new Formatter(null, Locale.getDefault());

		try {

			long seconds = time_second % 60;

			long minutes = (time_second / 60) % 60;

			long hours = time_second / 3600;

			return formatter.format("%02d:%02d:%02d", hours, minutes, seconds)
					.toString();

		} finally {
			formatter.close();
		}
	}

	/**
	 * 得到渠道号
	 */
	public static String getPcode() {
		String pcode = "120110000";
		try {
			ApplicationInfo appInfo = LetvApplication
					.getInstance()
					.getPackageManager()
					.getApplicationInfo(
							LetvApplication.getInstance().getPackageName(), PackageManager.GET_META_DATA);
			pcode = String.valueOf(appInfo.metaData.getInt("pcode"));

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// return LetvConfiguration.getPcode();
		return pcode;
	}

	/**
	 * 得到客户端版本名
	 */
	public static String getClientVersionName() {
		try {
			PackageInfo packInfo = LetvApplication
					.getInstance()
					.getPackageManager()
					.getPackageInfo(
							LetvApplication.getInstance().getPackageName(), 0);

			return packInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 得到客户端版本号
	 */
	public static int getClientVersionCode() {
		try {
			PackageInfo packInfo = LetvApplication
					.getInstance()
					.getPackageManager()
					.getPackageInfo(
							LetvApplication.getInstance().getPackageName(), 0);
			return packInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 生成设备唯一ID
	 * */
	public static String generateDeviceId() {
		String str = getIMEI() + getIMSI() + getDeviceName() + getBrandName()
				+ getMacAddress();
		return MD5.toMd5(str);
	}

	/**
	 * 得到IMEI号
	 * */
	public static String getIMEI() {
		try {
			String deviceId = ((TelephonyManager) LetvApplication.getInstance()
					.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
			if (null == deviceId || deviceId.length() <= 0) {
				return "";
			} else {
				return deviceId.replace(" ", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String getIMSI() {
		String subscriberId = ((TelephonyManager) LetvApplication.getInstance()
				.getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId();
		if (null == subscriberId || subscriberId.length() <= 0) {
			subscriberId = generate_DeviceId();
		} else {
			subscriberId.replace(" ", "");
			if (TextUtils.isEmpty(subscriberId)) {
				subscriberId = generate_DeviceId();
			}
		}

		return subscriberId;
	}

	private static String generate_DeviceId() {
		String str = getIMEI() + getDeviceName() + getBrandName()
				+ getMacAddress();
		return MD5.toMd5(str);
	}

	/**
	 * 得到设备名字
	 * */
	public static String getDeviceName() {
		String model = android.os.Build.MODEL;
		if (model == null || model.length() <= 0) {
			return "";
		} else {
			return model;
		}
	}

	/**
	 * 得到品牌名字
	 * */
	public static String getBrandName() {
		String brand = android.os.Build.BRAND;
		if (brand == null || brand.length() <= 0) {
			return "";
		} else {
			return brand;
		}
	}

	/**
	 * 得到系统名字
	 * */
	public static String getSystemName() {
		return "android";
	}

	/**
	 * 得到系统MAC地址
	 * */
	public static String getMacAddress() {
		String macAddress = null;
		WifiInfo wifiInfo = ((WifiManager) LetvApplication.getInstance()
				.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
		if (wifiInfo != null) {
			macAddress = wifiInfo.getMacAddress();
			if (macAddress == null || macAddress.length() <= 0) {
				return "";
			} else {
				return macAddress;
			}
		} else {
			return "";
		}
	}

	/**
	 * 检查是否装在sd卡
	 * 
	 * @return
	 */
	public static boolean sdCardMounted() {
		final String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)
				&& !state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
			return true;
		}
		return false;
	}

	/**
	 * 获取屏幕宽度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		return ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getWidth();
	}

	/**
	 * dip to px 转化
	 * 
	 * @param mContext
	 * @param dipValue
	 * @return
	 */
	public static int dipToPx(Context mContext, int dipValue) {
		if (null == mContext) {
			return (int) dipValue;
		}

		final float scale = mContext.getResources().getDisplayMetrics().density;
		int pxValue = (int) (dipValue * scale + 0.5f);

		return pxValue;
	}

	/**
	 * 判断是否有海外版权
	 * */
	public static boolean checkIp(String controlAreas, int disableType) {
		String allowforeign = isAllowforeign(controlAreas, disableType);
		if (AlbumNew.Copyright.NEITHER_P_D.equals(allowforeign)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 判断海外版权
	 * */
	public static String isAllowforeign(String controlAreas, int disableType) {
		String allowforeign = AlbumNew.Copyright.ALL_P_D;
		/**
		 * 0-有播放和下载版权 3-无播放和下载版权
		 * 
		 * controlAreas string 受控区域，多个逗号分隔，结合disableType字段处理，空为全部允许 disableType
		 * string 1:全部允许;2:部分允许;3:全部屏蔽;4:部分屏蔽
		 */
		if (TextUtils.isEmpty(controlAreas)) {
			return allowforeign;
		}
		switch (disableType) {
		case 1:
			allowforeign = AlbumNew.Copyright.ALL_P_D;
			break;
		case 2:
			if (LetvApplication.getInstance().getIp() != null) {
				if (LetvApplication.getInstance().getIp().getUser_country() != null) {
					if (controlAreas.contains(LetvApplication.getInstance()
							.getIp().getUser_country())) {
						allowforeign = AlbumNew.Copyright.ALL_P_D;
					} else {
						allowforeign = AlbumNew.Copyright.NEITHER_P_D;
					}
				} else {
					allowforeign = AlbumNew.Copyright.ALL_P_D;
				}
			} else {
				allowforeign = AlbumNew.Copyright.ALL_P_D;
			}
			break;
		case 3:
			allowforeign = AlbumNew.Copyright.NEITHER_P_D;
			break;
		case 4:
			if (LetvApplication.getInstance().getIp() != null) {
				if (controlAreas.contains(LetvApplication.getInstance().getIp()
						.getUser_country())) {
					allowforeign = AlbumNew.Copyright.NEITHER_P_D;
				} else {
					allowforeign = AlbumNew.Copyright.ALL_P_D;
				}
			} else {
				allowforeign = AlbumNew.Copyright.ALL_P_D;
			}
			break;
		}
		return allowforeign;
	}
}
