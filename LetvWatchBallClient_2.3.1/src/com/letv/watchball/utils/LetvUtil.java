package com.letv.watchball.utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.letv.http.LetvHttpConstant;
import com.letv.http.LetvHttpLog;
import com.letv.watchball.LetvApplication;
import com.letv.watchball.R;
import com.letv.watchball.bean.Share;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.share.LetvShareControl;
import com.letv.watchball.ui.LetvNumberFormat;

public class LetvUtil {

	public static long lastClickTime = 0;

	public static long currentClickTime = 0;
	public static boolean checkClickEvent() {
		return checkClickEvent(1000);
	}
	
	public static boolean checkClickEvent(long interval) {
		currentClickTime = System.currentTimeMillis();
		if (currentClickTime - lastClickTime > interval) {
			lastClickTime = currentClickTime;
			return true;
		} else {
			lastClickTime = currentClickTime;
			return false;
		}
	}
	/**
	 * 得到客户端版本信息
	 * 
	 * @return
	 */
	public static String getClientVersionName() {
		Context context = LetvApplication.getInstance();
		if (context == null) {
			return "";
		}
		try {
			PackageInfo packInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return "2.3.2";
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 得到客户端版本信息
	 * 
	 * @param context
	 * @return
	 */
	public static int getClientVersionCode(Context context) {
		if (context == null) {
			return 0;
		}
		try {
			PackageInfo packInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 得到操作系统版本号
	 */
	public static String getOSVersionName() {
		return android.os.Build.VERSION.RELEASE;
	}

	public static String getSDK() {
		return android.os.Build.VERSION.SDK;
	}

	/**
	 * 得到UA
	 */

	public static String getUserAgent() {
		StringBuilder model = new StringBuilder(android.os.Build.MODEL);
		if (model.toString().startsWith("\"")) {
			model.deleteCharAt(0);
		}
		if (model.toString().endsWith("\"")) {
			model.deleteCharAt(model.length() - 1);
		}
		return model.toString();
	}

	/**
	 * 获得手机deviceId(即手机的IMEI号)
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context) {
		if(null == context){
			return null;
		}
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
		// String tel = tm.getLine1Number();
		// String simsn = tm.getSimSerialNumber();
		// String imsi = tm.getSubscriberId();
	}

	/**
	 * 获得手机Sim卡的序号(即IMSI)
	 * 
	 * @param context
	 * @return
	 */
	public static String getSimSequence(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSubscriberId();
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
	 * 渠道pcode号
	 * 
	 * @return pcode
	 */
	public static String getPcode() {

		return LetvConfiguration.getPcode();
	}
	
	/**
	 * 打印日志
	 * 
	 * @return
	 */
	public static boolean isDebug() {
		boolean isDebug = LetvConfiguration.isDebug();
		LetvHttpConstant.setDebug(isDebug);
		return isDebug;
	}

	/**
	 * 得到设备名字
	 * */
	public static String getSystemName() {
		return "android";
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
	 * 检查字符串是否为空
	 * 
	 * @return
	 */
	public static boolean hasText(String text) {

		if (text == null) {
			return false;
		}

		if (text.equals("")) {
			return false;
		}

		return true;
	}

	/**
	 * 计算视频时长
	 * */
	public static String getNumberTime(long time_second) {

		long seconds = time_second % 60;

		long minutes = (time_second / 60) % 60;

		long hours = time_second / 3600;

		Formatter formatter = new Formatter(null, Locale.getDefault());

		return formatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString();

	}

	/**
	 * 获得屏幕宽度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Activity context) {
		int width = context.getWindowManager().getDefaultDisplay().getWidth();
		return width;
	}

	/**
	 * 获得屏幕高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Activity context) {
		int height = context.getWindowManager().getDefaultDisplay().getHeight();
		return height;
	}

	/**
	 * 获得屏幕密度
	 * 
	 * @param context
	 * @return
	 */
	public static float getDensity(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.density;
	}
	
	public static boolean hasNet(){
		return null != getAvailableNetWorkInfo(LetvApplication.getInstance());
	}
	
	public static  NetworkInfo getAvailableNetWorkInfo(Context context) {
		NetworkInfo activeNetInfo = null;
		if(context!=null){
			ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			activeNetInfo = connectivityManager.getActiveNetworkInfo();
		}
        if (activeNetInfo != null && activeNetInfo.isAvailable()) {
            return activeNetInfo;
        }else{
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
		ConnectivityManager connectivityMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityMgr != null) {
			NetworkInfo networkInfo = connectivityMgr.getActiveNetworkInfo();
			if (networkInfo != null) {
				if (ConnectivityManager.TYPE_WIFI == networkInfo.getType()) {
					netType = "wifi";
				} else if (ConnectivityManager.TYPE_MOBILE == networkInfo.getType()) {
					netType = "3G";
				} else {
					netType = "wifi";
				}
			}
		}

		return netType;
	}

	public static boolean hasSDCard() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * <p>
	 * Adds all the elements of the given arrays into a new array.
	 * </p>
	 * <p>
	 * The new array contains all of the element of <code>array1</code> followed
	 * by all of the elements <code>array2</code>. When an array is returned, it
	 * is always a new array.
	 * </p>
	 * 
	 * <pre>
	 * ArrayUtils.addAll(null, null) = null
	 * ArrayUtils.addAll(array1, null) = cloned copy of array1
	 * ArrayUtils.addAll(null, array2) = cloned copy of array2
	 * ArrayUtils.addAll([], []) = []
	 * ArrayUtils.addAll([null], [null]) = [null, null]
	 * ArrayUtils.addAll(["a", "b", "c"], ["1", "2", "3"]) = ["a", "b", "c", "1", "2", "3"]
	 * </pre>
	 * 
	 * @param array1
	 *            the first array whose elements are added to the new array, may
	 *            be <code>null</code>
	 * @param array2
	 *            the second array whose elements are added to the new array,
	 *            may be <code>null</code>
	 * @return The new array, <code>null</code> if <code>null</code> array
	 *         inputs. The type of the new array is the type of the first array.
	 * @since 2.1
	 */
	public static Object[] addAllArrays(Object[] array1, Object[] array2) {
		if (array1 == null) {
			return clone(array2);
		} else if (array2 == null) {
			return clone(array1);
		}
		Object[] joinedArray = (Object[]) Array.newInstance(array1.getClass().getComponentType(), array1.length + array2.length);
		System.arraycopy(array1, 0, joinedArray, 0, array1.length);
		System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
		return joinedArray;
	}
	
	/**
     * <p>Shallow clones an array returning a typecast result and handling
     * <code>null</code>.</p>
     *
     * <p>The objects in the array are not cloned, thus there is no special
     * handling for multi-dimensional arrays.</p>
     * 
     * <p>This method returns <code>null</code> if <code>null</code> array input.</p>
     * 
     * @param array the array to shallow clone, may be <code>null</code>
     * @return the cloned array, <code>null</code> if <code>null</code> input
     */
    public static Object[] clone(Object[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }
    
	/**
	 * 关闭数据库Cursor对象
	 * @author liuheyuan
	 * @param cursor
	 */
	public static void closeCursor(Cursor cursor){
		if(null != cursor){
			if(!cursor.isClosed()){
				cursor.close();
			}
		}
	}
	
	/**
	 * 时间格式化
	 * @param timeMillis
	 * @return
	 */
	public static String timeFormat(long timeMillis,String format){
		try {
			String date = new SimpleDateFormat(format).format(new Date(timeMillis));
			return date;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 时间格式化 默认 yyyy-MM-dd HH:mm:ss
	 * @param timeMillis time now
	 * @return
	 */
	public static String timeFormat(long timeMillis){
		String format = "yyyy-MM-dd HH:mm:ss";
		String date = new SimpleDateFormat(format).format(new Date(timeMillis));
		return date;
	}
	
	
	/**
	 * @param date 格式："05月25日 周六"
	 * @param time 格式： "15:35",
	 * @return time now
	 */
	public static long timeFormatSubscribeGame(String date, String time) {
		try {
			String year = new SimpleDateFormat("yyyy").format(new Date(System.currentTimeMillis()));
			String month = date.subSequence(0, date.indexOf("月")).toString();
			String day = date.subSequence(date.indexOf("月"), date.indexOf("日")).toString().replace("月", "");
			time = time.replace(":", "");
			date = (year + month + day + time).replaceAll(" ", "");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");
			return sdf.parse(date).getTime();
		} catch (Exception e) {
//			e.printStackTrace();
			return timeFormatSubscribeGame2(date, time);
		}
	}
	
	/**
	 * @param date 格式："2013.05.25"
	 * @param time 格式： "15:35",
	 * @return time now
	 */
	public static long timeFormatSubscribeGame2(String date, String time) {
		try {
			date = date.replace(" ", "");
			time = time.replace(" ", "");
			date = date.replace(".", "");
			time = time.replace(":", "");
			date = date.subSequence(0, 8).toString();
			date = (date + time);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");
			return sdf.parse(date).getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public static String getStringTwo(String strIn) {
		String strResult;
		if (strIn.length() >= 2) {
			strResult = strIn;
		} else {
			strResult = "0".concat(String.valueOf(String.valueOf(strIn)));
		}
		return strResult;
	}
	
	/**
	 * 格式化时间字符串
	 * @param timeMs 毫秒
	 * @return 返回格式00:00:00
	 */
	public static String stringForTime(int timeMs) {
		
		StringBuilder formatBuilder = new StringBuilder();
		
		Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());
		
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;

        formatBuilder.setLength(0);
//      if (hours > 0) {
            return formatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString();
//      } else {
//          return formatter.format("%02d:%02d", minutes, seconds).toString();
//      }
    }
	
	public static String getUUID(Context context) {
		return generateDeviceId(context) + "_" + System.currentTimeMillis();
	}
	
	public static String generateDeviceId(Context context) {
		String str = getIMEI(context) + getIMSI(context) + getDeviceName()
				+ getBrandName() + getMacAddress(context);
		return MD5Helper(str);
	}
	
	public static String getIMEI(Context context) {
		String deviceId = ((TelephonyManager) LetvApplication.getInstance()
				.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		if (null == deviceId || deviceId.length() <= 0) {
			return "";
		} else {
			return deviceId.replace(" ", "");
		}
	}

	public static String getIMSI(Context context) {
		String subscriberId = ((TelephonyManager) LetvApplication.getInstance()
				.getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId();
		if (null == subscriberId || subscriberId.length() <= 0) {
			subscriberId = LetvUtil.generate_DeviceId(context);
		} else {
                  subscriberId.replace(" ", "");
                  if (TextUtils.isEmpty(subscriberId)) {
				subscriberId = LetvUtil.generate_DeviceId(context);
			}
		}

		return subscriberId;
	}
	
	public static String getMacAddress(Context context) {
		if(context==null){
			return "";
		}
		String macAddress = null;
		WifiInfo wifiInfo = ((WifiManager) context
				.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
		macAddress = wifiInfo.getMacAddress();
		if (macAddress == null || macAddress.length() <= 0) {
			return "";
		} else {
			return macAddress;
		}
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
	
	private static String generate_DeviceId(Context context) {
		String str = getIMEI(context) + getDeviceName() + getBrandName()
				+ getMacAddress(context);
		return MD5Helper(str);
	}
	
	public static String getUID() {
		if(PreferencesManager.getInstance().isLogin()) {
			return PreferencesManager.getInstance().getUserId();
		} else {
			return "";
		}
	}
	
	public static String getSource() {

		return LetvConfiguration.getSource();
	}

	
	/**
	 * 网络是否处于联通状态，用于播放
	 */
	public static boolean isNetAvailableForPlay(final Context context) {

		NetworkInfo networkInfo = NetWorkTypeUtils.getAvailableNetWorkInfo();
		if (networkInfo == null) {
			return false;
		}

		return true;
	}
	
	public static long lastClickPlayTime = 0;

	public static long currentClickPlayTime = 0;
	public static boolean checkClickPlay() {
		currentClickPlayTime = System.currentTimeMillis();
		if (currentClickPlayTime - lastClickPlayTime > 2000) {
			lastClickPlayTime = currentClickPlayTime;
			return true;
		} else {
			lastClickPlayTime = currentClickPlayTime;
			return false;
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

      public static float staticticsLoadTimeInfoFormat(long time) {
            if (time > 120 * 1000) {
                  time = System.currentTimeMillis() - time;
            }
            return (time / 10) * 1.0f / 100;
      }

      public interface IpValidListener {
		public void ipValid();

		public void ipInvalid();
	}

	
	/**
	 * 电视台code转为唯一的数字
	 * */
	public static int codeToInt(String code) {
		if (TextUtils.isEmpty(code)) {
			return 0;
		}
		code.replace("-", "");
		if(code.length() > 5){
			code = code.substring(0, 5);
		}
		code = code.toUpperCase();
		StringBuilder numString = new StringBuilder();
		HashMap<Character, Integer> hashMap = new HashMap<Character, Integer>();
		{
			hashMap.put('A', 1);
			hashMap.put('B', 2);
			hashMap.put('C', 3);
			hashMap.put('D', 4);
			hashMap.put('E', 5);
			hashMap.put('F', 6);
			hashMap.put('G', 7);
			hashMap.put('H', 8);
			hashMap.put('I', 9);
			hashMap.put('J', 10);
			hashMap.put('K', 11);
			hashMap.put('L', 12);
			hashMap.put('M', 13);
			hashMap.put('N', 14);
			hashMap.put('O', 15);
			hashMap.put('P', 16);
			hashMap.put('Q', 17);
			hashMap.put('R', 18);
			hashMap.put('S', 19);
			hashMap.put('T', 20);
			hashMap.put('U', 21);
			hashMap.put('V', 22);
			hashMap.put('W', 23);
			hashMap.put('X', 24);
			hashMap.put('Y', 25);
			hashMap.put('Z', 26);
			hashMap.put('0', 27);
			hashMap.put('1', 28);
			hashMap.put('2', 29);
			hashMap.put('3', 30);
			hashMap.put('4', 31);
			hashMap.put('5', 32);
			hashMap.put('6', 33);
			hashMap.put('7', 34);
			hashMap.put('8', 35);
			hashMap.put('9', 36);
		}
		if (TextUtils.isEmpty(code)) {
			return 0;
		}
		char[] cs = code.toCharArray();

		for (int i = 0; i < cs.length; i++) {
			int a = 0;
			if (hashMap.containsKey(cs[i])) {
				a = hashMap.get(cs[i]);
			}

			numString.append(a);
		}
		try {
			return (int) (Long.parseLong(numString.toString()));
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public static String getShareHint(String name, int type, int id, int order, int vid) {
		Share share = LetvShareControl.getInstance().getShare();
		String orderStr = LetvNumberFormat.format(order + "");
		String replace_url = null;
		LetvHttpLog.Err("type =" + type + "  id =" + id + "  vid=" + vid);
		if (share != null) {
			if (type == 1) {
				replace_url = share.getVideo_url().replace("{vid}", vid + "");
				replace_url = replace_url.replace("{index}", "1");
				replace_url = replace_url.replace("{aid}", id + "");
				return TextUtil.getString(R.string.share_msg_album_playforsina, name, replace_url);
			} else if (type == 2) {
				replace_url = share.getVideo_url().replace("{aid}", id + "");
				replace_url = replace_url.replace("{index}", "1");
				replace_url = replace_url.replace("{vid}", vid + "");
				return TextUtil.getString(R.string.share_msg_album_play, name);
			}
		}
		return null;
	}

	public static String getSharePlayUrl(int type, int id, int order, int vid) {
		Share share = LetvShareControl.getInstance().getShare();
		String orderStr = LetvNumberFormat.format(order + "");
		String replace_url = null;
		LogInfo.log("getSharePlayUrl", "type ===" + type + "  id =" + id + "  vid=" + vid);
		LetvHttpLog.Err("type =" + type + "  id =" + id + "  vid=" + vid);
		if (share != null) {

			replace_url = share.getVideo_url().replace("{vid}", vid + "");
			replace_url = replace_url.replace("{index}", "1");
			replace_url = replace_url.replace("{aid}", id + "");
			return replace_url;
		}
		return null;
	}
	
	/**
	 * 判断object【】是否为空
	 * 
	 * @param array
	 * @return
	 */
	public static boolean isEmptyArray(Object[] array) {
		return isEmptyArray(array, 1);
	}

	public static boolean isEmptyArray(Object array) {
		return null == array;
	}

	public static boolean isEmptyArray(Object[] array, int len) {
		return null == array || array.length < len;
	}
	
	public static boolean checkBrowser(Context context, String packageName) {
		if (packageName == null || "".equals(packageName))
			return false;
		try {
			context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}
	
	/**
	 * 获得画中画屏幕宽度
	 * 
	 * @param context
	 * @return
	 */
	public static int getDisplayWidth(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		int w = dm.widthPixels;
		int h = dm.heightPixels;
		return w < h ? w : h;
	}

	/**
	 * 验证注册手机号码是否正确
	 */
	public static boolean isMobileNO(String mobiles) {
		if (mobiles == null) {
			return false;
		}
		// Pattern p =
		// Pattern.compile("^((1[0-9][0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
		Pattern p = Pattern.compile("^(1)\\d{10}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	/**
	 * 验证密码是否是 数字和字母，长度6-16,区分大小写
	 */
	public static boolean passwordFormat(String password) {
		if (password == null)
			return false;
		String regular = "^[a-zA-Z0-9_]{6,16}$";
		Pattern pattern = Pattern.compile(regular);
		Matcher matcher = pattern.matcher(password);
		if (!matcher.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * 验证邮箱格式是否正确
	 * 
	 * @param email
	 * @return
	 */
	public static boolean emailFormats(String email) {
		if (email == null)
			return false;
		String regular = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
		Pattern pattern = Pattern.compile(regular);
		Matcher matcher = pattern.matcher(email);
		if (!matcher.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * 检查登录时帐号字符是否合法
	 */
	public static boolean checkLoginUserString(String userNameString) {
		return true;
		// userNameString = userNameString.trim();
		// if (null != userNameString && !"".equals(userNameString)) {
		// char userNameChar[] = userNameString.toCharArray();
		// boolean isMail = false;
		// for (char i : userNameChar) {
		// // 检查是否含有@字符
		// if (i == '@') {
		// isMail = true;
		// break;
		// }
		// }
		//
		// if (isMail) {
		// return emailFormats(userNameString);
		// } else {
		// return isMobileNO(userNameString);
		// }
		// } else {
		// return false;
		// }
	}

	/**
	 * 生成年月日
	 * 
	 * @param time
	 * **/
	public static String timeString(long time) {

		// 按自定义格式SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		SimpleDateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd");
		formatTime.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
		Date currentTime = new Date(time);
		String tt = formatTime.format(currentTime);
		return tt ;
	}

	/**
	 * 生成年月日
	 * 
	 * @param time
	 * **/
	public static String timeStringBySecond(long time) {
		SimpleDateFormat formatTime = new SimpleDateFormat("MM-dd HH:mm:ss");
		Date currentTime = new Date(time * 1000);
		return formatTime.format(currentTime);
	}
	/**生成时分
	 * @param time
	 * @return
	 */
	public static String time2StrByMinSec(long time) {
		SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
		Date currentTime = new Date(time * 1000);
		return formatTime.format(currentTime);
	}

	/**
	 * 生成年月日
	 * 
	 * @param time
	 * **/
	public static String timeStringAll(long time) {

		// 按自定义格式SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		SimpleDateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date currentTime = new Date(time);
		return formatTime.format(currentTime);
	}

	public static String timeString(String time) {
		if (null != time && !"".equals(time.trim())) {
			try {
				return timeString(Long.parseLong(time));
			} catch (Exception e) {
				Log.e("LHY", "Utils - timeString - error = " + e.toString());
			}

		}

		return "";
	}

	/**
	 * 短信找回密码
	 */
	public static void retrievePwdBySMS(Context context, String phonenumber) {
		// Uri smsToUri = Uri.parse("smsto://10690159292121");
		// Uri smsToUri = Uri.parse("smsto:10690159292121");
		Uri smsToUri = Uri.parse("smsto:" + phonenumber);
		Intent mIntent = new Intent(Intent.ACTION_SENDTO, smsToUri);
		mIntent.putExtra("sms_body", "");
		context.startActivity(mIntent);
	}

    /**
     * 23-7点免打扰
     * @return 是否免打扰
     */

    public static boolean sleepAlarm(){

        if (PreferencesManager.getInstance().isSleepRemind()){

            Calendar calendar = Calendar.getInstance();

            int i = calendar.get(Calendar.HOUR_OF_DAY);

            return (i < 23 && i >= 7);
        } else {
            return true;
        }

    }
    public static boolean CheckNetworkState(){

           boolean flag = false;

          ConnectivityManager manager = (ConnectivityManager)LetvApplication.getInstance().getSystemService(

                Context.CONNECTIVITY_SERVICE);

           if(manager.getActiveNetworkInfo() != null)

          {

             flag = manager.getActiveNetworkInfo().isAvailable();

           }
           				return flag;

        }

      /**
       *
       * 将两个小于16位的int型数据合并为一个int型数据
       *
       * ！！！！！传入的数据顺序就是取出的数据顺序！！！！！
       *
       * @param a  想要合并的第一个int数值
       * @param b  想要合并的第二个int数值
       * @return 合并后的数值，高16位是合并前第一个数值，低16位是合并前第二个数值
       */

      public static int intJoint(int a, int b) {
            int c = (a&0x0000ffff) << 16 | (b&0x0000ffff);
            return c;
      }

      /**
       * 将合并后的int数值拆分为两个16位 int数值
       *
       * ！！！！！传入的数据顺序就是取出的数据顺序！！！！！
       *
       * @param a 合并后的数值
       * @return [0] 为合并前的第一个数值 [1] 为合并后的第二个数值
       */


      public static int[] intSplit(int a){
            int[] ints = new int[2];
            ints[0] = (a&0xffff0000) >>> 16;
            ints[1] = a&0x0000ffff;
            return ints;
      }
}
