package com.letv.watchball.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LetvConfiguration {

	private static String source;

	private static String flurryKey;

	private static boolean debug;

	private static String pcode;

	private static boolean jingpin;

	private static boolean errorCatch;

	private static boolean umeng;

	private static boolean haveAd;

	private static String[] channels_pcodes = null;
	private static String[] channels_names = null;

	static {
		InputStream in = null;
		try {
			Properties properties = new Properties();

			in = LetvConfiguration.class.getClassLoader().getResourceAsStream(
					"letv.properties");
			properties.load(in);

			source = properties.getProperty("letv.source");
			flurryKey = properties.getProperty("letv.flurry.key");
			debug = Boolean.parseBoolean(properties.getProperty("letv.debug"));
			pcode = properties.getProperty("letv.pcode");

			String errorCatchStr = properties.getProperty("letv.error.catch");
			errorCatch = Boolean.parseBoolean(errorCatchStr);

			String jin = properties.getProperty("letv.jingpin");
			jingpin = Boolean.parseBoolean(jin);

			String strUmeng = properties.getProperty("letv.umeng");
			umeng = Boolean.parseBoolean(strUmeng);

			String strHaveAd = properties.getProperty("letv.havead");
			haveAd = Boolean.parseBoolean(strHaveAd);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String getSource() {

		return source;
	}

	public static String getFlurryKey() {

		return flurryKey;
	}

	public static boolean isDebug() {
		return debug;
	}

	public static String getPcode() {

		return pcode;
	}

	public static boolean isErrorCatch() {

		return errorCatch;
	}

	public static boolean isJingpin() {

		return jingpin;
	}

	public static boolean isUmeng() {

		return umeng;
	}

	public static boolean isHaveAd() {

		return haveAd;
	}

}
