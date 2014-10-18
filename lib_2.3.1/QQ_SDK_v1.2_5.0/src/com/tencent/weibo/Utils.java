package com.tencent.weibo;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

public class Utils {
	public static final String IMAGE_PATH = "Letv/storage/image";
	
	public static boolean isNetAvailableForPlay(final Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.isAvailable()) {
        }else{
        	activeNetInfo = null;
        }
		if (activeNetInfo == null) {
			return false;
		}

		return true;
	}

	public static String url2FilePath(String url) {
		String filePath;
		String fileName = transformUrlToFileName(url);
		if (fileName == null || fileName.length() == 0) {
			return "";
		} else {
			filePath = Environment.getExternalStorageDirectory() + "/"
					+ IMAGE_PATH + "/" + fileName;
		}

		return filePath;
	}

	private static String transformUrlToFileName(String url) {
		if (url == null || url.length() == 0) {
			return "";
		}

		String name = url.replace(":", "");
		name = name.replace("/", "");
		String d = name.substring(name.lastIndexOf("."), name.length());
		name = name.replace(".", "");
		name = name + d;
		File file = new File(Environment.getExternalStorageDirectory() + "/"
				+ IMAGE_PATH + "/" + name);
		if (file.exists()) {
			file.renameTo(new File(Environment.getExternalStorageDirectory()
					+ "/" + IMAGE_PATH + "/" + name + "letvimage"));
		}
		name += "letvimage";
		return name;
	}

	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("WifiPreference IpAddress", ex.toString());
		}
		return "127.0.0.1";
	}
}
