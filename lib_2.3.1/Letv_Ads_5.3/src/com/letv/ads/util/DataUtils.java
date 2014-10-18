package com.letv.ads.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

public final class DataUtils {

	private DataUtils() {
	}

	public static NetworkInfo getAvailableNetWorkInfo(Context context) {
		
		if(context == null) {
			return null;
		}
		
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && activeNetInfo.isAvailable()) {
			return activeNetInfo;
		} else {
			return null;
		}
	}
	
	/**
	 * 返回联网类型
	 * 
	 * @param context
	 * @return wifi或3G
	 */
	public static String getNetType(Context context) {
		String netType = null;
		ConnectivityManager connectivityMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityMgr != null) {
			NetworkInfo networkInfo = connectivityMgr.getActiveNetworkInfo();
			if (networkInfo != null) {
				if (ConnectivityManager.TYPE_WIFI == networkInfo.getType()) {
					netType = "wifi";
				} else if (ConnectivityManager.TYPE_MOBILE == networkInfo
						.getType()) {
					netType = "3G";
				} else {
					netType = "wifi";
				}
			}
		}

		return netType;
	}
	
	public static String generateDeviceId(Context context) {
		String str = getIMEI(context) + getIMSI(context) + getDeviceName()
				+ getBrandName() + getMacAddress(context);
		return MD5Helper(str);
	}

	private static String generate_DeviceId(Context context) {
		String str = getIMEI(context) + getDeviceName() + getBrandName()
				+ getMacAddress(context);
		return MD5Helper(str);
	}

	public static String getUUID(Context context) {
		if(context==null){
			return "";
		}
		return generateDeviceId(context) + "_" + System.currentTimeMillis();
	}

	public static String MD5Helper(String str) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes("UTF-8"));
			byte[] byteArray = messageDigest.digest();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < byteArray.length; i++) {
				if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
					sb.append("0").append(
							Integer.toHexString(0xFF & byteArray[i]));
				} else {
					sb.append(Integer.toHexString(0xFF & byteArray[i]));
				}
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		throw new RuntimeException("no device Id");
	}
	
	public static String getIMEI(Context context) {
		try {
			if(context==null){
				return "";
			}
			String deviceId = ((TelephonyManager) context
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

	public static String getIMSI(Context context) {
		if(context==null){
			return "";
		}
		String subscriberId = ((TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId();
		if (null == subscriberId || subscriberId.length() <= 0) {
			subscriberId = generate_DeviceId(context);
		} else {
			subscriberId.replace(" ", "");
			if (TextUtils.isEmpty(subscriberId)) {
				subscriberId = generate_DeviceId(context);
			}
		}

		return subscriberId;
	}

	public static String getMacAddress(Context context) {
		String macAddress = null;
		if(context==null){
			return "";
		}
		WifiInfo wifiInfo = ((WifiManager) context
				.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
		macAddress = wifiInfo.getMacAddress();
		if (macAddress == null || macAddress.length() <= 0) {
			return "";
		} else {
			return macAddress;
		}
	}

	public static String getResolution(Context context) {
		if(context==null){
			return "";
		}
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return new StringBuilder().append(dm.widthPixels).append("*")
				.append(dm.heightPixels).toString();
	}

	public static String getDensity(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return String.valueOf(dm.density);
	}
	
	/**
	 * 得到手机设备名字
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
	 * 得到手机品牌名字
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
	 * 得到客户端版本信息
	 * 
	 * @param context
	 * @return
	 */
	public static String getClientVersionName(Context context) {
		if (context == null) {
			return "";
		}
		try {
			PackageInfo packInfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return packInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 得到设备名字
	 * */
	public static String getSystemName() {
		return "android";
	}
	
	/**
	 * 得到操作系统版本号
	 */
	public static String getOSVersionName() {
		return android.os.Build.VERSION.RELEASE;
	}
	
	/**
	 * 得到pverson
	 * @param version 例如：参数3.1
	 * @return 则返回3100
	 */
	public static String getPVersion(String version) {
		if(TextUtils.isEmpty(version)) return "";
		
		version = version.replace(".", "");
		
		for (int i = version.length(); i < 4; i++) {
			version = version.concat("0");
		}
		
		return version;
	}
	
	public static String encodeUrl(String url) throws UnsupportedEncodingException {
		if(TextUtils.isEmpty(url)) return null;
//		if(url.indexOf("?") != -1) {
//    		return url.substring(0, url.indexOf("?")) + URLEncoder.encode(url.substring(url.indexOf("?"), url.length()), "UTF-8");
//    	} else {
//    		return url;
//    	}
		return url.replace("|", "%7C");
	}
}
