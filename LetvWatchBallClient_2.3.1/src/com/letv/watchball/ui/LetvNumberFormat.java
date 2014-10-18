package com.letv.watchball.ui;

import java.util.HashMap;

public class LetvNumberFormat {
	private static java.util.Map<String, String> SmallToBigMap = new HashMap<String, String>();
	static {
		SmallToBigMap.put(String.valueOf(0), "零");
		SmallToBigMap.put(String.valueOf(1), "一");
		SmallToBigMap.put(String.valueOf(2), "二");
		SmallToBigMap.put(String.valueOf(3), "三");
		SmallToBigMap.put(String.valueOf(4), "四");
		SmallToBigMap.put(String.valueOf(5), "五");
		SmallToBigMap.put(String.valueOf(6), "六");
		SmallToBigMap.put(String.valueOf(7), "七");
		SmallToBigMap.put(String.valueOf(8), "八");
		SmallToBigMap.put(String.valueOf(9), "九");
		SmallToBigMap.put(String.valueOf(10), "十");
		SmallToBigMap.put(String.valueOf(100), "百");
		SmallToBigMap.put(String.valueOf(1000), "千");
		SmallToBigMap.put(String.valueOf(10000), "万");
		SmallToBigMap.put(String.valueOf(100000000), "亿");
	}

	public static String format(String num) {
		// 先将末尾的零去掉
		String numString = String.valueOf(num).replaceAll(".[0]+$", "");
		// 分别获取整数部分和小数部分的数字
		String intValue;
		String decValue = "";
		if (numString.indexOf(".") != -1) {
			intValue = numString.substring(0, numString.indexOf(".")-1);
			decValue = numString.substring(numString.indexOf(".")+1);
		} else {
			intValue = String.valueOf(num);
		}
		// 翻译整数部分。
		intValue = formatInteger(Integer.parseInt(String.valueOf(intValue)));
		// 翻译小数部分
		decValue = formatDecnum(decValue);
		String resultString = intValue;
		if (!decValue.equals(""))
			resultString = resultString + "点" + decValue;
		return resultString;
	}

	/**
	 * 将阿拉伯整数数字翻译为汉语小写数字。 其核心思想是按照中文的读法，从后往前每四个数字为一组。每一组最后要加上对应的单位，分别为万、亿等。
	 * 每一组中从后往前每个数字后面加上对应的单位，分别为个十百千。 每一组中如果出现零千、零百、零十的情况下去掉单位。
	 * 每组中若出现多个连续的零，则通读为一个零。 若每一组中若零位于开始或结尾的位置，则不读。
	 * 
	 * @param num
	 * @return
	 */
	public static String formatInteger(int num) {
		int unit = 10000;
		int numTmp = num;
		int perUnit = 10000;
		String sb = new String();
		String unitHeadString = "";
		while (num > 0) {
			int temp = num % perUnit;
			sb = formatIntegerLess10000(temp) + sb;
			// 判断是否以单位表示为字符串首位，如果是，则去掉，替换为零
			if (!unitHeadString.equals(""))
				sb = sb.replaceAll("^" + unitHeadString, "零");
			num = num / perUnit;
			if (num > 0) {
				// 如果大于当前单位，则追加对应的单位
				unitHeadString = SmallToBigMap.get(String.valueOf(unit));
				sb = unitHeadString + sb;
			}
			unit = unit * perUnit;
		}
		if(numTmp<20 && numTmp >=10){
			return sb.substring(1);
		}
		return sb;
	}

	/**
	 * 将小于一万的整数转换为中文汉语小写
	 * 
	 * @param num
	 * @return
	 */
	public static String formatIntegerLess10000(int num) {
		StringBuffer sb = new StringBuffer();
		for (int unit = 1000; unit > 0; unit = unit / 10) {
			int _num = num / unit;
			// 追加数字翻译
			sb.append(SmallToBigMap.get(String.valueOf(_num)));
			if (unit > 1 && _num > 0)
				sb.append(SmallToBigMap.get(String.valueOf(unit)));
			num = num % unit;
		}
		// System.out.println(sb.toString().replaceAll("[零]+",
		// "零").replaceAll("^零", "").replaceAll("零$", ""));
		// 先将连续的零联合为一个零，再去掉头部和末尾的零
		return sb.toString().replaceAll("[零]+", "零").replaceAll("^零", "").replaceAll("零$", "");
	}

	public static String formatDecnum(String num) {
		StringBuffer sBuffer = new StringBuffer();
		char[] chars = num.toCharArray();
		for (int i = 0; i < num.length(); i++) {
			sBuffer.append(SmallToBigMap.get(String.valueOf(chars[i])));
		}

		return sBuffer.toString();
	}
//
//	public static void main(String[] args) {
//		// NumberFormat.formatInteger(1000);
//		System.out.println(NumberFormat.format(123+""));
//		System.out.println(NumberFormat.format("101"));
//		System.out.println(NumberFormat.format("1001"));
//		System.out.println(NumberFormat.format("10100"));
//		System.out.println(NumberFormat.format("1000000001.123"));
//	}
}
